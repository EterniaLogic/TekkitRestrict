package nl.taico.tekkitrestrict;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class PatchesAPI {
	public static boolean hasFix(double version){
		return version != -1d;
	}
	
	private static Double eep = null;
	public static double getEEPatchVer(){
 		if (eep != null) return eep;
		try {
			Field f = Class.forName("ee.EEPatch").getField("version");
			return eep = f.getDouble(null);
		} catch (Exception ex){
			try {
				Class.forName("ee.events.EEEvent");
			} catch (ClassNotFoundException e) {
				return eep = -1d;
			}
			return eep = 0d;
		}
	}
	
	private static Double nei = null;
	public static double getNEIVer(){
		if (nei != null) return nei;
		Class<?> c = null;
		try {
			c = Class.forName("codechicken.nei.TUtil");
		} catch (Exception ex){
			return nei = -1D;
		}
		
		try {
			Field f = c.getField("FPVersion");
			return nei = f.getDouble(null);
		} catch (Exception e) {
			return nei = 0D;//<1.7
		}
	}
	
	private static Double wr = null;
	public static double getWRVer(){
		if (wr != null) return wr;
		try {
			Field f = Class.forName("codechicken.wirelessredstone.core.WirelessBolt").getField("FPVersion");
			return wr = f.getDouble(null);
		} catch (Exception e) {
			return wr = -1D;
		}
	}
	
	private static Double wra = null;
	public static double getWRAddonsVer(){
		if (wra != null) return wra;
		try {
			Field f = Class.forName("codechicken.wirelessredstone.addons.RedstoneEtherAddonManager").getField("FPVersion");
			return wra = f.getDouble(null);
		} catch (Exception e) {
			return wra = -1d;
		}
	}
	
	private static Double wm = null;
	public static double getWMVer(){
		if (wm != null) return wm;
		try {
			Class.forName("net.minecraft.server.TaeirDamageSource");
		} catch (Exception ex) {
			return wm = -1D;
		}
		
		try {
			Field f = Class.forName("net.minecraft.server.TaeirUtil").getField("FPVersion");
			return wm = f.getDouble(null);
		} catch (Exception ex) {
			return wm = 0D;//<1.9
		}
	}
	
	private static Double mffs = null;
	public static double getMFFSVer(){
		if (mffs != null) return mffs;
		Class<?> c = null;
		try {
			c = Class.forName("mffs.TaeirDamageSource");
		} catch (Exception ex){
			return mffs = -1D;
		}
		
		try {
			Field f = c.getField("FPVersion");
			return mffs = f.getDouble(null);
		} catch (Exception e) {
			return mffs = 0D;//<1.4
		}
	}
	
	private static Double railcraft = null;
	public static double getRailcraftVer(){
		if (railcraft != null) return railcraft;
		try {
			Field f = Class.forName("railcraft.common.utility.TileItemLoader").getField("FPVersion");
			return railcraft = f.getDouble(null);
		} catch (Exception e) {
			return railcraft = -1D;
		}
	}
	
	private static Double redpower = null;
	public static double getRedPowerVer(){
		if (redpower != null) return redpower;
		try {
			Field f = Class.forName("eloraam.wiring.TileWiring").getField("FPVersion");
			return redpower = f.getDouble(null);
		} catch (Exception e) {
			return redpower = -1D;
		}
	}
	
	private static Double ic2 = null;
	public static double getIC2Ver(){
		if (ic2 != null) return ic2;
		try {
			Field f = Class.forName("ic2.common.TileEntityTesla").getField("FPVersion");
			return ic2 = f.getDouble(null);
		} catch (Exception e) {
			return ic2 = -1d;
		}
	}

	private static Double apipes = null;
	public static double getAdditionalPipesVer(){
		if (apipes != null) return apipes;

		try {
			Field f = Class.forName("buildcraft.additionalpipes.MutiPlayerProxy").getField("FPVersion");
			return apipes = f.getDouble(null);
		} catch (Exception e) {
			return apipes = -1D;
		}
	}
	
	public static List<String> getOverview(){
		List<String> tbr = new ArrayList<>();
		if (hasFix(getEEPatchVer()))			tbr.add(ChatColor.GREEN + "EquivalentExchange: EEPatch "+(getEEPatchVer()==0d?"< v1.4":"v"+getEEPatchVer()));
		else									tbr.add(ChatColor.RED   + "EquivalentExchange: no");
		
		if (hasFix(getNEIVer()))				tbr.add(ChatColor.GREEN + "NotEnoughItems: FixPack-NEI "+(getNEIVer()==0d?"< v1.7":"v"+getNEIVer()));
		else									tbr.add(ChatColor.RED   + "NotEnoughItems: no");
		
		if (hasFix(getWRVer())) 				tbr.add(ChatColor.GREEN + "WirelessRedstone: FixPack-WR v"+getWRVer());
		else									tbr.add(ChatColor.RED   + "WirelessRedstone: no");
		
		if (hasFix(getWRAddonsVer())) 			tbr.add(ChatColor.GREEN + "WirelessRedstone Addons: FixPack-WRA v"+getWRAddonsVer());
		else									tbr.add(ChatColor.RED   + "WirelessRedstone Addons: no");
		
		if (hasFix(getWMVer())) 				tbr.add(ChatColor.GREEN + "WeaponsMod: FixPack-WM "+(getWMVer()==0d?"< v1.9":"v"+getWMVer()));
		else									tbr.add(ChatColor.RED   + "WeaponsMod: no");
		
		if (hasFix(getMFFSVer())) 				tbr.add(ChatColor.GREEN + "ModularForcefieldSystems: FixPack-MFFS "+(getMFFSVer()==0d?"< v1.4":"v"+getMFFSVer()));
		else					  				tbr.add(ChatColor.RED   + "ModularForcefieldSystems: no");
		
		if (hasFix(getRailcraftVer())) 			tbr.add(ChatColor.GREEN + "RailCraft: FixPack-RailCraft v"+getRailcraftVer());
		else					       			tbr.add(ChatColor.GOLD  + "RailCraft: no");
		
		if (hasFix(getRedPowerVer())) 			tbr.add(ChatColor.GREEN + "RedPower: FixPack-RedPower v"+getRedPowerVer());
		else					      			tbr.add(ChatColor.RED   + "RedPower: no");
		
		if (hasFix(getIC2Ver()))  				tbr.add(ChatColor.GREEN + "IndustrialCraft 2: FixPack-IC2 v"+getIC2Ver());
		else					  				tbr.add(ChatColor.RED   + "IndustrialCraft 2: no");
		
		if (hasFix(getAdditionalPipesVer())) 	tbr.add(ChatColor.GREEN + "AdditionalPipes: FixPack-APipes v"+getAdditionalPipesVer());
		else					  			 	tbr.add(ChatColor.GOLD  + "AdditionalPipes: no");
		
		return tbr;
	}
}
