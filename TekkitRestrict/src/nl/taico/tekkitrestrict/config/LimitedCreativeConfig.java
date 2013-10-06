package nl.taico.tekkitrestrict.config;

import java.util.ArrayList;

public class LimitedCreativeConfig extends TRConfig {
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>();
		
		tbr.add("######################################################################");
		tbr.add("## Configuration file for TekkitRestrict                            ##");
		tbr.add("## Authors: Taeir, DreadEnd (aka DreadSlicer)                       ##");
		tbr.add("## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/    ##");
		tbr.add("## Please ask questions/report issues on the BukkitDev page.        ##");
		tbr.add("######################################################################");
		tbr.add("");
		tbr.add("######################################################################");
		tbr.add("#################### LimitedCreative Configuration ###################");
		tbr.add("######################################################################");
		tbr.add("");
		tbr.add("# If this is enabled, it prevents the use of ANY container while in");
		tbr.add("# creative mode. This is everything you can interact with with the");
		tbr.add("# exception of your own inventory.");
		tbr.add("# Default: true");
		tbr.add("LimitedCreativeNoContainer: true");
		if (extra) tbr.add("#:-;-:# LimitedCreativeNoContainer");
		tbr.add("");
		tbr.add("# Prevent the use of these mods or IDs in creative");
		tbr.add("LimitedCreative:");
		if (extra) tbr.add("#:-;-:# LimitedCreative 22");
		tbr.add("- EE");
		tbr.add("- RedPowerControl");
		tbr.add("- RedPowerLogic");
		tbr.add("- RedPowerMachine");
		tbr.add("- WirelessRedstone");
		tbr.add("- BuildCraft");
		tbr.add("- AdditionalPipes");
		tbr.add("- AdvancedMachines");
		tbr.add("- IndustrialCraft");
		tbr.add("- NuclearControl");
		tbr.add("- CompactSolars");
		tbr.add("- ChargingBench");
		tbr.add("- PowerConverters");
		tbr.add("- Mffs");
		tbr.add("- RailCraft");
		tbr.add("- TubeStuffs");
		tbr.add("- IronChests");
		tbr.add("- BalkonWeaponMod");
		tbr.add("- EnderChest");
		tbr.add("- ChunkLoaders");
		tbr.add("#- RedPowerCore");
		tbr.add("#- RedPowerLighting");
		tbr.add("");
		tbr.add("######################################################################");
		
		return tbr;
	}
	
	public static void upgradeFile(){
		upgradeFile("LimitedCreative", convertDefaults(defaultContents(true)));
	}
}
