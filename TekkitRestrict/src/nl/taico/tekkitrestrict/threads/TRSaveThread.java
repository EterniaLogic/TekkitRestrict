package nl.taico.tekkitrestrict.threads;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TRLogger;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Threads;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRSafeZone;

public class TRSaveThread extends Thread {
	private boolean err1, err2, err3;//, err4;
	private boolean saving = false;
	private boolean last = false;
	public boolean isSaving(){
		return saving;
	}
	
	@Override
	public void run() {
		last = false;
		try {
			if (tekkitrestrict.disable) return; //If plugin is disabling, then stop the thread. The savethread triggers again if interrupted.
			Thread.sleep(Threads.saveSpeed);
		} catch (InterruptedException ex) {}
		while (true) {
			saving = true;
			// runs save functions for both safezones and itemlimiter
			try {
				TRLimiter.saveLimiters();
			} catch (Exception ex) {
				if (!err1){
					Warning.other("An error occurred while trying to save the Limiter! (This error will only be logged once)", false);
					Log.Exception(ex, false);
					err1 = true;
				}
			}
			
			try {
				TRSafeZone.save();
			} catch (Exception ex) {
				if (!err2){
					Warning.other("An error occurred while trying to save the SafeZones! (This error will only be logged once)", false);
					Log.Exception(ex, false);
					err2 = true;
				}
			}

			try {
				TRLogger.saveLogs();
			} catch (Exception ex) {
				if (!err3){
					Warning.other("An error occurred while trying to save the logs! (This error will only be logged once)", false);
					Log.Exception(ex, false);
					err3 = true;
				}
			}
			//try {
			//	TRNoHack.clearMaps();
			//} catch (Exception ex) {}
			saving = false;
			
			if (tekkitrestrict.disable){
				if (last) break; //If plugin is disabling, then stop the thread. The savethread triggers again if interrupted.
				else {
					last = true;
					continue;
				}
			}

			try {
				Thread.sleep(Threads.saveSpeed);
			} catch (InterruptedException e) {
				if (tekkitrestrict.disable){
					last = true;
					continue;
				}
			}
		}
	}
}
