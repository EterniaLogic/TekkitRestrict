package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.List;

import com.github.dreadslicer.tekkitrestrict.objects.TRDupeSettings;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.SSMode;
import com.github.dreadslicer.tekkitrestrict.objects.TRHackSettings;
import com.griefcraft.lwc.LWCPlugin;

public class TRConfigCache {
	public static class Global {
		public static boolean kickFromConsole, debug;
	}
	
	public static class Hacks {
		public static TRHackSettings flys, forcefields, speeds;

		public static String broadcastFormat;
		

	}
	
	public static class Dupes {
		public static TRDupeSettings alcBags, rmFurnaces, tankcarts, tankcartGlitchs, transmutes, pedestals, teleports;

		public static String broadcastFormat;

	}
	
	public static class SafeZones {
		//public static boolean SSDisableFly;
		public static boolean UseSafeZones;
		//public static List<String> SSPlugins;
		public static boolean UseGP, UseTowny, UsePS, UseFactions, UseWG;
		public static SSMode GPMode = SSMode.Admin;
		public static SSMode WGMode = SSMode.Specific;
	}
	
	public static class Listeners {
		public static boolean UseBlockLimit, BlockCreativeContainer;
		public static boolean UseNoItem, UseLimitedCreative, useNoCLickPerms;
	}
	
	public static class LogFilter {
		public static List<String> replaceList = new ArrayList<String>();
		public static boolean splitLogs, filterLogs;
		public static String logLocation, fileFormat, logFormat;
	}
	
	public static class Threads {
		public static int saveSpeed, worldCleanerSpeed, inventorySpeed, SSEntityRemoverSpeed, gemArmorSpeed;
		public static boolean GAMovement, GAOffensive;
		public static boolean SSDisableEntities, SSDechargeEE, SSDisableArcane;
		public static boolean RMDB;//, UseRPTimer;
		public static int ChangeDisabledItemsIntoId;
		//public static int RPTickTime;
		
		@SuppressWarnings("rawtypes")
		public static ArrayList<Class> SSClassBypasses = new ArrayList<Class>();
	}
	
	public static class LWC {
		public static LWCPlugin lwcPlugin;
	}
	
	public static class ChunkUnloader {
		public static int maxChunks, maxRadii;
		public static boolean enabled;
		public static int maxChunksEnd, maxChunksNether, maxChunksNormal;//TODO
		public static int maxChunksTotal;//TODO
		public static int unloadOrder;
	}
}
