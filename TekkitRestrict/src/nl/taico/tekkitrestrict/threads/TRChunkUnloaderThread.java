package nl.taico.tekkitrestrict.threads;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Threads;
import nl.taico.tekkitrestrict.TekkitRestrict;
import nl.taico.tekkitrestrict.functions.TRThreadedChunkUnloader;

import org.bukkit.Bukkit;

public class TRChunkUnloaderThread extends Thread{
	private boolean err1 = false;
	private boolean done = true;
	@Override
	public void run(){
		this.setName("TekkitRestrict ChunkUnloader");
		while (true){
			if (done){
				/*Bukkit.getScheduler().scheduleSyncDelayedTask(TekkitRestrict.getInstance(), new Runnable(){
					@Override
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
				});*/
				
				try{
					TRThreadedChunkUnloader.unloadSChunks();
				}catch(Exception e){
					Log.Exception(e, true);
				}
			}

			try {
				sleep(Threads.chunkUnloaderSpeed);
			} catch (InterruptedException e) {
				if (TekkitRestrict.disable) return;
			}
		}
	}
}
