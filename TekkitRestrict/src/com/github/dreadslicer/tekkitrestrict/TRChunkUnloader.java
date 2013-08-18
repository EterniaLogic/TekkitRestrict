package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.ChunkUnloader;

public class TRChunkUnloader {
	/*
	public static void unloadSChunks() {
		if (!ChunkUnloader.enabled) return;
		try {
			// TRChunkRunner cr = new TRChunkRunner();
			
			int tot = getTotalChunks();
			if (tot < ChunkUnloader.maxChunks) return; //If the max chunk amount isn't reached, do nothing.
			
			List<World> worlds = tekkitrestrict.getInstance().getServer().getWorlds();
			
			for (World world : worlds) {
				final WorldServer ws = ((CraftWorld) world).getHandle();
				Chunk[] loadedChunks = world.getLoadedChunks(); // Get chunks from
				
				
				for (Chunk chunk : loadedChunks) { //For every loaded chunk
					// Convert chunk to minecraft server chunk
					//chunk.unload();
					net.minecraft.server.Chunk mcChunk = ((CraftChunk) chunk).getHandle();

					// Check for maximum chunks.
					if (!isChunkInUse(ws, chunk.getX(), chunk.getZ(), ChunkUnloader.maxRadii)) {
						if (tot > ChunkUnloader.maxChunks) {
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
	}*/
	
	public static void unloadSChunks() {
		if (!ChunkUnloader.enabled) return;
		int tot = getTotalChunks();
		if (tot > ChunkUnloader.maxChunksTotal) {
			int nr = tot - ChunkUnloader.maxChunksTotal;
			if (ChunkUnloader.unloadOrder == 0){
				nr = unloadEndChunks(nr, true);
				if (nr > 0) nr = unloadNetherChunks(nr, true);
				if (nr > 0) nr = unloadNormalChunks(nr, true);
			} else if (ChunkUnloader.unloadOrder == 1){
				nr = unloadNetherChunks(nr, true);
				if (nr > 0) nr = unloadEndChunks(nr, true);
				if (nr > 0) nr = unloadNormalChunks(nr, true);
			} else if (ChunkUnloader.unloadOrder == 2){
				nr = unloadNormalChunks(nr, true);
				if (nr > 0) nr = unloadEndChunks(nr, true);
				if (nr > 0) nr = unloadNetherChunks(nr, true);
			} else if (ChunkUnloader.unloadOrder == 3){
				nr = unloadEndChunks(nr, true);
				if (nr > 0) nr = unloadNormalChunks(nr, true);
				if (nr > 0) nr = unloadNetherChunks(nr, true);
			} else if (ChunkUnloader.unloadOrder == 4){
				nr = unloadNetherChunks(nr, true);
				if (nr > 0) nr = unloadNormalChunks(nr, true);
				if (nr > 0) nr = unloadEndChunks(nr, true);
			} else if (ChunkUnloader.unloadOrder == 5){
				nr = unloadNormalChunks(nr, true);
				if (nr > 0) nr = unloadNetherChunks(nr, true);
				if (nr > 0) nr = unloadEndChunks(nr, true);
			} else {
				Log.Config.Warning("Invalid value " + ChunkUnloader.unloadOrder + " for UnloadOrder in TPerformance.config.yml!");
				Log.Config.Warning("Valid: 0, 1, 2, 3, 4 or 5.");
				ChunkUnloader.unloadOrder = 0;
			}
			
			if (nr > 0) {
				tekkitrestrict.log.warning("Chunk Unloader cannot unload enough chunks!");
				tekkitrestrict.log.warning(nr + " chunks are loaded above the total maxChunks limit!");
				tekkitrestrict.log.warning("If the serverload is not too high, you can raise the total maxchunks count in the config.");
				tekkitrestrict.log.warning("If it is too high, please lower maxradii or kick some players.");
			}
			return;
		}
		
		unloadEndChunks(0, false);
		unloadNetherChunks(0, false);
		unloadNormalChunks(0, false);

	}
	
	private static int unloadEndChunks(int amount, boolean force) {
		if (!ChunkUnloader.enabled) return 0;
		try {
			List<World> worlds = Bukkit.getWorlds();
			
			ArrayList<Chunk> toRemove = new ArrayList<Chunk>();
			
			for (World world : worlds) {
				if (world.getEnvironment() != Environment.THE_END) continue; //Only the end
				
				Chunk[] loadedChunks = world.getLoadedChunks();
				
				int tot = loadedChunks.length;
				if (!force && tot < ChunkUnloader.maxChunksEnd) return 0; //If the max chunk amount isn't reached, do nothing.

				for (Chunk chunk : loadedChunks) { //For every loaded chunk
					if (!force){
						if (tot < ChunkUnloader.maxChunksEnd) break; // Check for maximum chunks.
					} else {
						if (amount == 0) break;
					}
					
					if (!isChunkInUse(world, chunk.getX(), chunk.getZ(), ChunkUnloader.maxRadii)) {
						toRemove.add(chunk);
						if (!force) tot--;
						else amount--;
					}
				}
			}
			
			for (Chunk chunk : toRemove){
				chunk.unload(true, false);
			}
			
		} catch (Exception ex) {
			tekkitrestrict.log.warning("An error occured in the Chunk Unloader [End]! Please inform the author.");
			Log.Exception(ex, false);
		}
		return amount;
	}
	
	private static int unloadNetherChunks(int amount, boolean force) {
		if (!ChunkUnloader.enabled) return 0;
		try {
			List<World> worlds = Bukkit.getWorlds();
			
			ArrayList<Chunk> toRemove = new ArrayList<Chunk>();
			
			for (World world : worlds) {
				if (world.getEnvironment() != Environment.NETHER) continue; //Only the nether
				
				Chunk[] loadedChunks = world.getLoadedChunks();
				
				int tot = loadedChunks.length;
				if (!force && tot < ChunkUnloader.maxChunksNether) return 0; //If the max chunk amount isn't reached, do nothing.

				for (Chunk chunk : loadedChunks) { //For every loaded chunk
					if (!force){
						if (tot < ChunkUnloader.maxChunksNether) break; // Check for maximum chunks.
					} else {
						if (amount == 0) break;
					}
					
					if (!isChunkInUse(world, chunk.getX(), chunk.getZ(), ChunkUnloader.maxRadii)) {
						toRemove.add(chunk);
						if (!force) tot--;
						else amount--;
					}
				}
			}
			
			for (Chunk chunk : toRemove){
				chunk.unload(true, false);
			}
			
		} catch (Exception ex) {
			tekkitrestrict.log.warning("An error occured in the Chunk Unloader [Nether]! Please inform the author.");
			Log.Exception(ex, false);
		}
		
		return amount;
	}
	
	private static int unloadNormalChunks(int amount, boolean force) {
		if (!ChunkUnloader.enabled) return 0;
		try {
			List<World> worlds = Bukkit.getWorlds();
			
			ArrayList<Chunk> toRemove = new ArrayList<Chunk>();
			
			for (World world : worlds) {
				if (world.getEnvironment() != Environment.NORMAL) continue; //Only normal
				
				Chunk[] loadedChunks = world.getLoadedChunks();
				
				int tot = loadedChunks.length;
				if (!force && tot < ChunkUnloader.maxChunksNormal) return 0; //If the max chunk amount isn't reached, do nothing.

				for (Chunk chunk : loadedChunks) { //For every loaded chunk
					if (!force){
						if (tot < ChunkUnloader.maxChunksNormal) break; // Check for maximum chunks.
					} else {
						if (amount == 0) break;
					}
					
					if (!isChunkInUse(world, chunk.getX(), chunk.getZ(), ChunkUnloader.maxRadii)) {
						toRemove.add(chunk);
						if (!force) tot--;
						else amount--;
					}
				}
			}
			
			for (Chunk chunk : toRemove){
				chunk.unload(true, false);
			}
			
		} catch (Exception ex) {
			tekkitrestrict.log.warning("An error occured in the Chunk Unloader [Normal]! Please inform the author.");
			Log.Exception(ex, false);
		}
		
		return amount;
	}

	/**
	 * Gets total chunks from each world's chunkProviderServer
	 * @return The number of chunks loaded
	 */
	private static int getTotalChunks() {
		List<World> worlds = Bukkit.getWorlds();
		int r = 0;
		for (World world : worlds){
			r += world.getLoadedChunks().length;
		}
		return r;
	}

	/** @return If there are currently players near that chunk. */
	private static boolean isChunkInUse(World world, int x, int z, int dist) {
		Player[] players = Bukkit.getOnlinePlayers();

		for(Player player : players){
			Location loc = player.getLocation();
			
			if (loc.getWorld() != world) continue;
			
			if (Math.abs(loc.getBlockX() - (x << 4)) <= dist && Math.abs(loc.getBlockZ() - (z << 4)) <= dist) {
				return true;
			}
		}

		return false;
	}
	
	/*
	/** @return If there are currently players near that chunk. */
	/*
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
			if (loc.getWorld() != world.chunkProviderServer.world.getWorld()) continue;
			if (Math.abs(loc.getBlockX() - (x << 4)) <= dist
			 && Math.abs(loc.getBlockZ() - (z << 4)) <= dist) {
				arr$.clear();
				return true;
			}
		}
		arr$.clear();
		return false;
	}
	*/
}
