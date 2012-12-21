package com.github.dreadslicer.tekkitrestrict.lib;

import java.util.Queue;

import com.github.dreadslicer.tekkitrestrict.TRLogger;

public class TRChunkRunner implements Runnable {
	public Queue<Object[]> chunks = new java.util.concurrent.LinkedBlockingQueue<Object[]>();

	@Override
	public void run() {
		chunkDeload();
	}

	private void chunkDeload() {
		Object[] ccx = chunks.poll();
		while (ccx != null) {
			try {
				/*
				 * Chunk chunk = (Chunk)ccx[0]; WorldServer world =
				 * (WorldServer)ccx[1]; //wo.unloadChunk(x, z, true, false);
				 * //WorldServer world =
				 * ((org.bukkit.craftbukkit.CraftWorld)wo).getHandle(); int x =
				 * chunk.x; int z = chunk.z; if(chunk != null){
				 * chunk.removeEntities();
				 * world.chunkProviderServer.saveChunk(chunk);
				 * world.chunkProviderServer.saveChunkNOP(chunk);
				 * world.chunkProviderServer.unloadQueue.remove(x, z);
				 * world.chunkProviderServer.chunks.remove(x, z);
				 * world.chunkProviderServer.chunkList.remove(chunk);
				 * tekkitrestrict.log.info("chunkunload "+x+","+z); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
				TRLogger.Log("debug",
						"Chunk Unloader[2] Error! " + e.getMessage());
			}
			ccx = chunks.poll();
		}
		chunks = null;
	}
}
