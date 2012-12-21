package com.github.dreadslicer.tekkitrestrict;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IProgressUpdate;
import net.minecraft.server.WorldServer;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public class TRChunkUnloader {
	private static int maxChunks = 2000, maxRadii = 256;
	private static boolean enabled = false;

	public static void reload() {
		maxChunks = tekkitrestrict.config.getInt("MaxChunks");
		maxRadii = tekkitrestrict.config.getInt("MaxRadii");
		enabled = tekkitrestrict.config.getBoolean("UseChunkUnloader");
	}

	public static void unloadSChunks() {
		try {
			if (enabled) {
				// TRChunkRunner cr = new TRChunkRunner();
				Server server = tekkitrestrict.getInstance().getServer();
				int tot = getTotalChunks();
				for (int j = 0; j < server.getWorlds().size(); j++) {
					World wo = server.getWorlds().get(j); // Get World
					final WorldServer ws = ((CraftWorld) wo).getHandle();
					Chunk[] cc = wo.getLoadedChunks(); // Get chunks from
					
					/*Future<String> returnFuture = server.getScheduler().callSyncMethod(tekkitrestrict.getInstance(), new Callable<String>() {
					   public String call() {
						   ws.chunkProviderServer.saveChunks(true, new ss());
						   return "";
					   }
					});*/

					// loop through chunks
					for (int k = 0; k < cc.length; k++) {
						Chunk c = cc[k]; // get chunk from the chunks from
											// CraftWorld
						// Convert chunk to minecraft server chunk
						net.minecraft.server.Chunk ccc = ((org.bukkit.craftbukkit.CraftChunk) c)
								.getHandle();

						// Check for maximum chunks.
						if (!isChunkInUse(ws, c.getX(), c.getZ(), maxRadii)) {
							if (tot > maxChunks) {
								// cr.chunks.add(new Object[]{ccc,ws});
								// wo.unloadChunk(x, z, true, false);
								// WorldServer world =
								// ((org.bukkit.craftbukkit.CraftWorld)wo).getHandle();
								//MinecraftServer ms = ((org.bukkit.craftbukkit.CraftServer)tekkitrestrict.getInstance().getServer()).getHandle().server;
								int x = ccc.x;
								int z = ccc.z;
								synchronized(ccc){ccc.removeEntities();}
								synchronized(ws){ws.chunkProviderServer.saveChunk(ccc);}
								synchronized(ws){ws.chunkProviderServer.saveChunkNOP(ccc);}
								synchronized(ws.chunkProviderServer.unloadQueue){ws.chunkProviderServer.unloadQueue.remove(x, z);}
								synchronized(ws.chunkProviderServer.chunks){ws.chunkProviderServer.chunks.remove(x, z);}
								synchronized(ws.chunkProviderServer.chunkList){ws.chunkProviderServer.chunkList.remove(ccc);}
								// tekkitrestrict.log.info("chunkunload "+x+","+z);
								tot--;
							}
						}
					}
					// tekkitrestrict.getInstance().getServer().getScheduler().
					// scheduleSyncDelayedTask(tekkitrestrict.getInstance(), cr,
					

				}
				// cr.run();
			}
		} catch (Exception eee) {
			TRLogger.Log("debug",
					"Chunk Unloader[1] Error! " + eee.getMessage());
		}
	}

	private static int getTotalChunks() {
		// gets total chunks from each world's chunkProviderServer
		Server server = tekkitrestrict.getInstance().getServer();
		int r = 0;
		for (int j = 0; j < server.getWorlds().size(); j++) {
			World wo = server.getWorlds().get(j); // Get World
			WorldServer ws = ((CraftWorld) wo).getHandle();
			r += ws.chunkProviderServer.chunkList.size();
		}
		return r;
	}

	private static boolean isChunkInUse(net.minecraft.server.WorldServer world,
			int x, int z, int dist) {
		// Get All players
		Server server = tekkitrestrict.getInstance().getServer();
		List<EntityPlayer> arr$ = new LinkedList<EntityPlayer>();
		for (int j = 0; j < server.getWorlds().size(); j++) {
			World wo = server.getWorlds().get(j); // Get World
			WorldServer ws = ((CraftWorld) wo).getHandle();
			for (int k = 0; k < ws.players.size(); k++) {
				arr$.add((EntityPlayer) ws.players.get(k));
			}
		}
		// loop through said players
		for (int i$ = 0; i$ < arr$.size(); i$++) {
			EntityPlayer player = arr$.get(i$);
			// get location of player
			Location loc = new Location(player.world.getWorld(), player.locX,
					player.locY, player.locZ);
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
	private static class ss implements IProgressUpdate{

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
		
	}
}
