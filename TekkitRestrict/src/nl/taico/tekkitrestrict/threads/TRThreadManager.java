package nl.taico.tekkitrestrict.threads;

import org.bukkit.Bukkit;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Threads;
import nl.taico.tekkitrestrict.functions.TRChunkUnloader;

public class TRThreadManager {
	/** Thread will trigger again if interrupted. */
	public TRSaveThread saveThread = new TRSaveThread();
	/** Thread will NOT trigger again if interrupted. */
	public TRDisableItemsThread disableItemThread = new TRDisableItemsThread();
	/** Thread will NOT trigger again if interrupted. */
	public TRWorldScrubberThread worldScrubThread = new TRWorldScrubberThread();
	/** Thread will NOT trigger again if interrupted. */
	public TRGemArmorThread gemArmorThread = new TRGemArmorThread();
	/** Thread will NOT trigger again if interrupted. */
	public TREntityRemoverThread entityRemoveThread = new TREntityRemoverThread();
	//public TRLimitFlyThread limitFlyThread = new TRLimitFlyThread();
	private static TRThreadManager instance;
	public TRThreadManager() {
		instance = this;
	}

	/** Give a name to all threads and then start them. */
	public void init() {
		saveThread.setName("TekkitRestrict_SaveThread");
		disableItemThread.setName("TekkitRestrict_InventorySearchThread");
		worldScrubThread.setName("TekkitRestrict_BlockScrubberThread");
		gemArmorThread.setName("TekkitRestrict_GemArmorThread");
		entityRemoveThread.setName("TekkitRestrict_EntityRemoverThread");
		//limitFlyThread.setName("TekkitRestrict_LimitFlyThread_Unused");
		saveThread.start();
		disableItemThread.start();
		worldScrubThread.start();
		gemArmorThread.start();
		entityRemoveThread.start();
		//if (tekkitrestrict.config.getBoolean("LimitFlightTime", false)) limitFlyThread.start();
	}
	
	public static void reload() {
		// reloads the variables in each thread...
		instance.disableItemThread.reload();
		//instance.limitFlyThread.reload();
	}
	
	public static void stop(){
		instance.disableItemThread.interrupt();
		instance.entityRemoveThread.interrupt();
		instance.gemArmorThread.interrupt();
		instance.worldScrubThread.interrupt();
		instance.saveThread.interrupt();
		
		//ttt.limitFlyThread.interrupt();
		try {
			instance.saveThread.join(10000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void scheduleChunkUnloader(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(tekkitrestrict.getInstance(), new Runnable(){
			private boolean err1 = false;
			public void run(){
				try {
					TRChunkUnloader.unloadSChunks();
				} catch (Exception ex) {
					if (!err1){
						Warning.other("An error occurred in the ChunkUnloader! (This error will only be logged once)", false);
						Log.Exception(ex, false);
						err1 = true;
					}
				}
			}
		}, Threads.worldCleanerSpeed, Threads.worldCleanerSpeed);
		
		//TODO make a separate speed for the chunk unloader.
	}
}
