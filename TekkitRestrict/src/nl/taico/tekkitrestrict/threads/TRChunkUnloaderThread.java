package nl.taico.tekkitrestrict.threads;

import org.bukkit.Bukkit;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TRConfigCache.Threads;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.functions.TRChunkUnloader;

public class TRChunkUnloaderThread extends Thread{
	private boolean err1 = false;
	private boolean done = true;
	public void run(){
		while (true){
			if (done){
				Bukkit.getScheduler().scheduleSyncDelayedTask(tekkitrestrict.getInstance(), new Runnable(){
					public void run(){
						try {
							done = false;
							TRChunkUnloader.unloadSChunks();
							done = true;
						} catch (Exception ex){
							done = true;
							if (!err1){
								Warning.other("An error occurred in the ChunkUnloader! (This error will only be logged once)", false);
								Log.Exception(ex, false);
								err1 = true;
							}
						}
					}
				});
			}
			
			try {
				sleep(Threads.chunkUnloaderSpeed);
			} catch (InterruptedException e) {
				if (tekkitrestrict.disable) return;
			}
		}
	}
}
