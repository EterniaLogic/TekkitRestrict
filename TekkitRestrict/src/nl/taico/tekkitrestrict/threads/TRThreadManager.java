package nl.taico.tekkitrestrict.threads;

public class TRThreadManager {
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
		instance.chunkUnloaderThread.interrupt();

		//ttt.limitFlyThread.interrupt();
		try {
			instance.saveThread.join(15000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
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
	public TRChunkUnloaderThread chunkUnloaderThread = new TRChunkUnloaderThread();

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
		chunkUnloaderThread.setName("TekkitRestrict_ChunkUnloaderThread");
		//limitFlyThread.setName("TekkitRestrict_LimitFlyThread_Unused");
		saveThread.start();
		disableItemThread.start();
		worldScrubThread.start();
		gemArmorThread.start();
		entityRemoveThread.start();
		chunkUnloaderThread.start();
		//if (tekkitrestrict.config.getBoolean("LimitFlightTime", false)) limitFlyThread.start();
	}
}
