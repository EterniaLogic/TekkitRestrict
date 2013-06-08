package com.github.dreadslicer.tekkitrestrict;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.WorldServer;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.CraftChunk;

public class TRChunkUnloader {
	private static int maxChunks = 2000, maxRadii = 256;
	private static boolean enabled = false;

	public static void reload() {
		maxChunks = tekkitrestrict.config.getInt("MaxChunks");
		maxRadii = tekkitrestrict.config.getInt("MaxRadii");
		enabled = tekkitrestrict.config.getBoolean("UseChunkUnloader");
	}

	public static void unloadSChunks() {
		if (!enabled) return;
		try {
			// TRChunkRunner cr = new TRChunkRunner();
			
			int tot = getTotalChunks();
			if (tot < maxChunks) return; //If the max chunk amount isn't reached, do nothing.
			
			List<World> worlds = tekkitrestrict.getInstance().getServer().getWorlds();
			
			for (World world : worlds) {
				final WorldServer ws = ((CraftWorld) world).getHandle();
				Chunk[] loadedChunks = world.getLoadedChunks(); // Get chunks from
				
				/*Future<String> returnFuture = server.getScheduler().callSyncMethod(tekkitrestrict.getInstance(), new Callable<String>() {
				   public String call() {
					   ws.chunkProviderServer.saveChunks(true, new ss());
					   return "";
				   }
				});*/

				for (Chunk chunk : loadedChunks) { //For every loaded chunk
					// Convert chunk to minecraft server chunk
					net.minecraft.server.Chunk mcChunk = ((CraftChunk) chunk).getHandle();

					// Check for maximum chunks.
					if (!isChunkInUse(ws, chunk.getX(), chunk.getZ(), maxRadii)) {
						if (tot > maxChunks) {
							// cr.chunks.add(new Object[]{ccc,ws});
							// wo.unloadChunk(x, z, true, false);
							// WorldServer world =
							// ((org.bukkit.craftbukkit.CraftWorld)wo).getHandle();
							//MinecraftServer ms = ((org.bukkit.craftbukkit.CraftServer)tekkitrestrict.getInstance().getServer()).getHandle().server;
							int x = mcChunk.x;
							int z = mcChunk.z;
							synchronized(mcChunk){mcChunk.removeEntities();}
							synchronized(ws){ws.chunkProviderServer.saveChunk(mcChunk);}
							synchronized(ws){ws.chunkProviderServer.saveChunkNOP(mcChunk);}
							synchronized(ws.chunkProviderServer.unloadQueue){ws.chunkProviderServer.unloadQueue.remove(x, z);}
							synchronized(ws.chunkProviderServer.chunks){ws.chunkProviderServer.chunks.remove(x, z);}
							synchronized(ws.chunkProviderServer.chunkList){ws.chunkProviderServer.chunkList.remove(mcChunk);}
							// tekkitrestrict.log.info("chunkunload "+x+","+z);
							tot--;
						}
					}
				}
				//IMPORTANT Bukkit also has a chunk.unload method.
				
				// tekkitrestrict.getInstance().getServer().getScheduler().
				// scheduleSyncDelayedTask(tekkitrestrict.getInstance(), cr,
				

			}
			// cr.run();
		} catch (Exception eee) {
			TRLogger.Log("debug", "Chunk Unloader[1] Error! " + eee.getMessage());
		}
	}

	/**
	 * Gets total chunks from each world's chunkProviderServer
	 * @return The number of chunks loaded
	 */
	private static int getTotalChunks() {
		List<World> worlds = tekkitrestrict.getInstance().getServer().getWorlds();
		int r = 0;
		for (World world : worlds){
			WorldServer ws = ((CraftWorld) world).getHandle();
			r += ws.chunkProviderServer.chunkList.size();
		}
		return r;
	}

	/**
	 * @return If there are currently players near that chunk.
	 */
	private static boolean isChunkInUse(WorldServer world, int x, int z, int dist) {
		// Get All players
		List<World> worlds = tekkitrestrict.getInstance().getServer().getWorlds();
		LinkedList<EntityPlayer> arr$ = new LinkedList<EntityPlayer>();
		for (World wo : worlds) { //For each world
			WorldServer ws = ((CraftWorld) wo).getHandle(); //Get the worldserver
			for (Object player : ws.players){ //For each player on that worldserver
				arr$.add((EntityPlayer) player); //Add to the arr$
			}
		}
		//TODO Isn't it better to use server.getOnlinePlayers()?
		
		// loop through said players
		for(EntityPlayer player : arr$){
			// get location of player
			Location loc = new Location(player.world.getWorld(), player.locX, player.locY, player.locZ);
			// determine if location is OK.
			if (loc.getWorld() == world.chunkProviderServer.world.getWorld()
					&& Math.abs(loc.getBlockX() - (x << 4)) <= dist
					&& Math.abs(loc.getBlockZ() - (z << 4)) <= dist) {
				arr$.clear();
				return true;
			}
		}
		arr$.clear();
		return false;
	}
	/*private static class ss implements IProgressUpdate{

		@Override
		public void a(String s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void a(int i) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void b(String s) {
			// TODO Auto-generated method stub
			
		}
		
	}*/
}
