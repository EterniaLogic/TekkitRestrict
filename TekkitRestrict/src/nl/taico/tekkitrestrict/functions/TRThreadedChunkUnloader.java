package nl.taico.tekkitrestrict.functions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.server.EmptyChunk;
import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.ChunkUnloader;
import nl.taico.tekkitrestrict.TekkitRestrict;
import nl.taico.tekkitrestrict.objects.TREnums.ChunkUnloadMethod;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkUnloadEvent;

import forge.ForgeHooks;

class CLThread extends Thread{
	public CLThread(World world){
		super();
		this.world = world;
	}
	boolean done = false;
	World world;
	
	@Override
	public void run() {
		Thread.currentThread().setName("TekkitRestrict Thread world chunk unloader ("+world.getName()+")");
		
		
		List<Chunk> chunks = TRThreadedChunkUnloader.getWorldChunks(world,400,true,true);
		
		int chunksi = TRThreadedChunkUnloader.doUnloadChunkList(chunks,true,true);
		//Warning.other("TR unloaded "+world.getName()+" "+chunksi+" Chunks (Async)", false);
		
		done=true;
	}
}


public class TRThreadedChunkUnloader {
	static LinkedList<CLThread> tRunners = new LinkedList<CLThread>();

	private static boolean doUnloadChunk(Chunk chunk, boolean force) {
		final net.minecraft.server.WorldServer mcWorld = ((CraftWorld) chunk
				.getWorld()).getHandle();
		int x = chunk.getX();
		int z = chunk.getZ();
		final net.minecraft.server.Chunk mcChunk = ((CraftChunk) chunk)
				.getHandle();
		if (mcChunk == null)
			return false;

		// if (!(mcChunk instanceof EmptyChunk)) {

		// synchronize with the world's chunk server to prevent any
		// multi-threading issues
		try {

			/*
			 * Field f = World.class.getDeclaredField("I");
			 * f.setAccessible(true);
			 */

			if (!force) {
				synchronized(mcWorld.chunkProviderServer){
					mcWorld.chunkProviderServer.queueUnload(x, z);
				}
				return true;
			} else {
				// Throw an event to test it
				// This is modified code from ChunkProviderServer.
				// Do not change unless you want to stop ChunkUnloader from
				// working.
				if (ForgeHooks.canUnloadChunk(mcChunk)) {
					/*ChunkUnloadEvent event = new ChunkUnloadEvent(chunk);
					synchronized(TekkitRestrict.getInstance().getServer().getPluginManager()){
						TekkitRestrict.getInstance().getServer().getPluginManager().callEvent(event);
					}
					
					
					if (!event.isCancelled()) { // TODO: does not go past here!!!!!*/
						try {
							//System.out.println("dounload g");
							mcChunk.removeEntities();
							synchronized(mcWorld.chunkProviderServer){
								synchronized(mcWorld){
									mcWorld.chunkProviderServer.saveChunk(mcChunk);
									mcWorld.chunkProviderServer.saveChunkNOP(mcChunk);
								}
							}
							
							synchronized(mcWorld.chunkProviderServer.chunks){
								mcWorld.chunkProviderServer.chunks.remove(mcChunk.x, mcChunk.z);
							}
							
							synchronized(mcWorld.chunkProviderServer.chunkList){
								mcWorld.chunkProviderServer.chunkList.remove(mcChunk);
							}
							
							//System.out.println("dounload h");
							return true;
				
						}catch(Exception e){
							Warning.other(e.getMessage()+" error TRThreadedChunkUnloader.doUnloadChunk",false);
						}
					//}


					/*
					 * mcChunk.removeEntities();
					 * mcWorld.chunkProviderServer.saveChunk(mcChunk);
					 * mcWorld.chunkProviderServer.saveChunkNOP(mcChunk);
					 * mcWorld.chunkProviderServer.chunks.remove(mcChunk.x,
					 * mcChunk.z);
					 * mcWorld.chunkProviderServer.chunkList.remove(chunk);
					 */
				}
			}
		} catch (Exception e) {
			Warning.other(" Error: TRChunkUnload.doUnloadChunk at " + mcChunk.x
					+ ", " + mcChunk.z + "  :(", false);
			Warning.other(e.getStackTrace().toString(), false);
		}
		
		return false;
	}

	public static int doUnloadChunkList(List<Chunk> toRemove, boolean force, boolean enableSleep){
		int amount=0;
		//System.out.println("DUNL_CHK a");
		for (Chunk chunk : toRemove){
			int x = chunk.getX(), z = chunk.getZ();
			try {
				//System.out.println("DUNL_CHK b");
				if(doUnloadChunk(chunk, force)) amount++;

				// sleep for a time so that the server is not exploded! (I.e: Server hang)
				// This should only be used if the command is called by a thread.
				if(enableSleep) {
				// sleep needs to be at most microseconds...
					java.util.concurrent.TimeUnit.MICROSECONDS.sleep(10);
				}
			} catch (Exception ex){
				Log.debug("Unable to unload chunk at ["+x+","+z+"] in world " + chunk.getWorld().getName());
				amount++;
			}
		}

		//if(!toRemove.isEmpty())
		//	Warning.other(toRemove.size() + " chunks queued up for removal in world " + toRemove.get(0).getWorld().getName(), false);

			

			return amount;
		}

		/**
		 * Gets total chunks from each world's chunkProviderServer
		 * 
		 * @return The number of chunks loaded
		 */
		public static int getTotalChunks() {
			List<World> worlds = Bukkit.getWorlds();
			int r = 0;
			for (World world : worlds) {
				r += world.getLoadedChunks().length;
			}
			return r;
		}

		public static boolean hasChunkLoader(Chunk chunk) {
			return hasChunkLoader(((CraftChunk) chunk).getHandle());
		}

		public static boolean hasChunkLoader(net.minecraft.server.Chunk chunk) {
			return !ForgeHooks.canUnloadChunk(chunk);
		}

		/** @return If there are currently players near that chunk. */
		public static boolean isChunkInUse(World world, int x, int z, int dist) {
			Player[] players = Bukkit.getOnlinePlayers();

			for (Player player : players) {
				try {
					Location loc = player.getLocation();

					if (loc.getWorld() != world)
						continue;

					if ((Math.abs(loc.getBlockX() - (x << 4)) <= dist)
							&& (Math.abs(loc.getBlockZ() - (z << 4)) <= dist)) {
						return true;
					}
				} catch (Exception ex) {
					// return false;
				}
			}

			// Try for world spawns
			Location spawn = world.getSpawnLocation();
			Location chunkpos = new Location(world, x, spawn.getY(), z);
			int spawnrad = TekkitRestrict.getInstance().getServer().getSpawnRadius();
			if (spawn.distance(chunkpos) <= (spawnrad * 16)) {
				return true;
			}

			return false;
		}

		
		public static LinkedList<Chunk> getWorldChunks(World world, int amount, boolean force, boolean enableSleep) {
			LinkedList<Chunk> toRemove = new LinkedList<Chunk>();
			try {
				/*int maxchunks = (world.getEnvironment() == Environment.NORMAL) ? ChunkUnloader.maxChunksNormal
						: (world.getEnvironment() == Environment.NETHER) ? ChunkUnloader.maxChunksNether
								: (world.getEnvironment() == Environment.THE_END) ? ChunkUnloader.maxChunksEnd
										: 400; // default for "Other"*/
				// environments

				Chunk[] loadedChunks = world.getLoadedChunks();
				for (Chunk chunk : loadedChunks) { // For every loaded chunk
					if(!TRThreadedChunkUnloader.hasChunkLoader(chunk) && !TRThreadedChunkUnloader.isChunkInUse(world, chunk.getX(), chunk.getZ(), ChunkUnloader.maxRadii))
						toRemove.add(chunk);
				}

				// Perform the removal of chunks (OR queue?)
				//amount += doUnloadChunkList(toRemove, force, enableSleep);
				//toRemove.clear();

			} catch (Exception ex) {
				Warning.other("An error occurred in the Chunk Unloader!", false);
				Log.Exception(ex, false);
			}

			return toRemove;
		}
		
		public static void unloadSChunks() {
			if (!ChunkUnloader.enabled)
				return;
			List<World> worlds = Bukkit.getWorlds();
			
			
			int tot = getTotalChunks();
			if (tot > ChunkUnloader.maxChunksTotal) {
				// remove chunks from unload queue, too many!
				
				for (World world : worlds) {
					synchronized (((CraftWorld) world).getHandle().chunkProviderServer.unloadQueue) {
						((CraftWorld) world).getHandle().chunkProviderServer.unloadQueue
						.popAll();
					}
				}

				// get chunks #s after world queue removal
				int nr = getTotalChunks() - ChunkUnloader.maxChunksTotal - 1;
				if (nr > 0) {
					Warning.other(
							nr
							+ " chunks are loaded above the total maxChunks limit! (Doing forced removal)",
							false);
					// Warning.other("If the serverload is not too high, you can raise the total maxchunks count in the config.",
					// false);
					// Warning.other("If it is too high, please lower maxradii or kick some players.",
					// false);

				}

				/*if ((ChunkUnloader.unloadOrder >= 0)
						|| (ChunkUnloader.unloadOrder <= 5)) {
					// Special unload orders
					for (Environment env : Environment.values()) {
						if ((nr > 0) && (env != Environment.NORMAL))
							unloadWorldChunks(nr, true, true);
						else
							nr = unloadWorldChunks(nr, true, true);
					}

				} else {
					Warning.config("Invalid value " + ChunkUnloader.unloadOrder
							+ " for UnloadOrder in TPerformance.config.yml!", false);
					Warning.config("Valid: 0, 1, 2, 3, 4 or 5.", false);
					ChunkUnloader.unloadOrder = 0;
				}*/
				
				
				for (World world : worlds) {
					tRunners.add(new CLThread(world));
					tRunners.peekLast().start();
					
					/*List<Chunk> chunks = TRThreadedChunkUnloader.getWorldChunks(world,400,true,true);
					
					int chunksi = TRThreadedChunkUnloader.doUnloadChunkList(chunks,true,true);*/
				}
				
				
				while (true) {
					boolean tdone = true;
					for(CLThread tr : tRunners)
						if(!tr.done) tdone = false;
					if(tdone) break;
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {}
				}
				
				
				tRunners.clear();

				int tot2 = getTotalChunks();
				Warning.other(
						(tot - tot2)
						+ " chunks unloaded. (Negative = chunks loaded)",
						false);
			}else{
				for (World world : worlds) {
					List<Chunk> toRemove = getWorldChunks(world, 0, false, false);
					int chunksi = doUnloadChunkList(toRemove,false,false);
					Warning.other("TR unloaded "+world.getName()+" "+chunksi+" Chunks (Sync)", false);
				}
			}
		}

		
	}
