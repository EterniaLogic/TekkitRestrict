package nl.taico.tekkitrestrict.threads;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Threads;
import nl.taico.tekkitrestrict.functions.TRChunkUnloader;
import nl.taico.tekkitrestrict.functions.TRNoItem;

public class TRWorldScrubberThread extends Thread {
	@Override
	public void run() {
		try {
			Thread.sleep(Threads.worldCleanerSpeed);//Don't trigger immediately, but sleep first.
		} catch (InterruptedException ex) {
			if (tekkitrestrict.disable) return; //If plugin is disabling, then stop the thread. The WorldScrubber thread shouldn't trigger again.
		}
		while (true) {
			doWScrub();

			try {
				Thread.sleep(Threads.worldCleanerSpeed);
			} catch (InterruptedException ex) {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The WorldScrubber thread shouldn't trigger again.
			}
		}
	}

	private boolean err1, err2;
	/**
	 * Runs TRChunkUnloader.unloadSChunks().<br>
	 * Then if UseRPTimer or RemoveDisabledBlocks is turned on, it will execute those features.
	 */
	private void doWScrub() {
		try {
			TRChunkUnloader.unloadSChunks();
		} catch (Exception ex) {
			if (!err1){
				Warning.other("An error occurred in the ChunkUnloader! (This error will only be logged once)", false);
				Log.Exception(ex, false);
				err1 = true;
			}
		}
		
		if (!Threads.RMDB) return;
		
		try {
			Server server = tekkitrestrict.getInstance().getServer();
			
			List<World> worlds = server.getWorlds();
			for (World bukkitWorld : worlds) { //For each world
				Chunk[] loadedChunks = bukkitWorld.getLoadedChunks();
				
				for (Chunk c : loadedChunks) { //For each loaded chunk
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 16; z++) {
							for (int y = 0; y < 256; y++) {
								Block bl = c.getBlock(x, y, z);
								if (TRNoItem.isBlockBanned(bl)) {
									bl.setTypeId(Threads.ChangeDisabledItemsIntoId);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			if (!err2){
				Warning.other("An error occurred in the BannedBlocksRemover! (This error will only be logged once)", false);
				Log.Exception(ex, false);
				err2 = true;
			}
		}
	}
}
