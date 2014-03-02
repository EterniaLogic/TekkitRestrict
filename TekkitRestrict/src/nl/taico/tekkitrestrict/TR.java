package nl.taico.tekkitrestrict;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import nl.taico.tekkitrestrict.objects.TRVersion;

public class TR {
	public static tekkitrestrict instance;
	
	public static TRVersion getVersion(){
		return tekkitrestrict.version;
	}
	
	public static Logger getLogger(){
		return instance.getLogger();
	}
	
	@Deprecated
	public static void saveDefaultConfig(boolean force){
		instance.saveDefaultConfig(force);
	}
	
	@Deprecated
	public static void reloadConfig(){
		instance.reloadConfig();
	}
	
	public static void reload(boolean listeners, boolean silent){
		if (instance == null) instance = tekkitrestrict.getInstance();
		instance.reload(listeners, silent);
	}
	
	public static boolean hasFixPack(){
		if (tekkitrestrict.FixPack != null) return tekkitrestrict.FixPack.booleanValue();
		try {
			Class.forName("codechicken.nei.TUtil");
			tekkitrestrict.FixPack = true;
			return true;
		} catch (ClassNotFoundException ex) {
			tekkitrestrict.FixPack = false;
			return false;
		}
	}
	
	public static class FixPack{
		private static Double nei = null;
		public static double getNEIVer(){
			if (nei != null) return nei;
			Class<?> c = null;
			try {
				c = Class.forName("codechicken.nei.TUtil");
			} catch (Exception ex){
				nei = -1D;
				return -1D;
			}
			
			try {
				Field f = c.getField("FPVersion");
				nei = f.getDouble(null);
				return nei;
			} catch (Exception e) {
				nei = 0D;//<1.7
				return 0D;
			}
		}
		
		private static Double wr = null;
		public static double getWRVer(){
			if (wr != null) return wr;
			try {
				Field f = Class.forName("codechicken.wirelessredstone.core.WirelessBolt").getField("FPVersion");
				wr = f.getDouble(null);
				return wr;
			} catch (Exception e) {
				wr = -1D;
				return -1D;
			}
		}
		
		public static boolean hasWeaponsModFix(){
			if (wr != null) return wr == -1D;
			return getWMVer() == -1D;
		}
		
		private static Double wm = null;
		public static double getWMVer(){
			if (wm != null) return wm;
			try {
				Class.forName("net.minecraft.server.TaeirDamageSource");
			} catch (Exception ex) {
				wm = -1D;
				return -1D;
			}
			
			try {
				Field f = Class.forName("net.minecraft.server.TaeirUtil").getField("FPVersion");
				wm = f.getDouble(null);
				return wm;
			} catch (Exception ex) {
				wm = 0D;//<1.9
				return 0D;
			}
		}
		private static Double mffs = null;
		public static double getMFFSVer(){
			if (mffs != null) return mffs;
			Class<?> c = null;
			try {
				c = Class.forName("mffs.TaeirDamageSource");
			} catch (Exception ex){
				mffs = -1D;
				return -1D;
			}
			
			try {
				Field f = c.getField("FPVersion");
				mffs = f.getDouble(null);
				return mffs;
			} catch (Exception e) {
				mffs = 0D;//<1.4
				return 0D;
			}
		}
		
		public static boolean hasMFFSFix(){
			if (mffs != null) return mffs == -1D;
			return getMFFSVer() == -1D;
		}
		
		private static Double railcraft = null;
		public static double getRailcraftVer(){
			if (railcraft != null) return railcraft;
			try {
				Field f = Class.forName("railcraft.common.utility.TileItemLoader").getField("FPVersion");
				railcraft = f.getDouble(null);
				return railcraft;
			} catch (Exception e) {
				railcraft = -1D;
				return -1D;
			}
		}
		
		private static Double redpower = null;
		public static double getRedPowerVer(){
			if (redpower != null) return redpower;
			try {
				Field f = Class.forName("eloraam.wiring.TileWiring").getField("FPVersion");
				redpower = f.getDouble(null);
				return redpower;
			} catch (Exception e) {
				redpower = -1D;
				return -1D;
			}
		}
	}
	private static Double eep = null;
 	public static double getEEPatchVersion(){
 		if (eep != null) return eep;
		try {
			Class.forName("ee.EEPatch");
			return eep = ee.EEPatch.version;
		} catch (ClassNotFoundException ex){
			try {
				Class.forName("ee.events.EEEvent");
			} catch (ClassNotFoundException e) {
				return eep = -1d;
			}
			return eep = 0d;
		}
	}
	
	public static boolean hasEEPatch(){
		if (tekkitrestrict.EEPatch != null) return tekkitrestrict.EEPatch.booleanValue();
		try {
			Class.forName("ee.events.EEEvent");
			tekkitrestrict.EEPatch = true;
			return true;
		} catch (ClassNotFoundException ex) {
			tekkitrestrict.EEPatch = false;
			return false;
		}
	}
}
