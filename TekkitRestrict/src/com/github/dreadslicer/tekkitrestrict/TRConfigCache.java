package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.List;

import com.griefcraft.lwc.LWCPlugin;

public class TRConfigCache {
	public static class Global {
		public static boolean kickFromConsole, debug, useNewBanSystem;
	}
	
	public static class Hacks {
		public static List<String> broadcast = new ArrayList<String>(), kick = new ArrayList<String>();
		public static String broadcastFormat;
		
		public static boolean forcefield, speed, fly;
		public static int flyTolerance, flyMinHeight, ffTolerance, speedTolerance;
		public static double ffVangle, speedMaxSpeed;
	}
	
	public static class Dupes {
		public static List<String> broadcast = new ArrayList<String>(), kick = new ArrayList<String>();
		public static String broadcastFormat;
		public static boolean rmFurnace, alcBag, transmute, tankcart, tankcartGlitch, pedestal;
	}
	
	public static class SafeZones {
		public static enum SSMode {
			All, Admin, Specific, SpecificAdmin;
			
			public static SSMode parse(String input){
				input = input.toLowerCase();
				if (input.equals("all")) return SSMode.All;
				if (input.equals("admin")) return SSMode.Admin;
				if (input.equals("specific")) return SSMode.Specific;
				if (input.equals("specificadmin")) return SSMode.SpecificAdmin;
				return SSMode.Admin;
			}
			
			public boolean isAdmin(){
				if (this == SSMode.Admin || this == SSMode.SpecificAdmin) return true;
				return false;
			}
		}
		
		//public static boolean SSDisableFly;
		public static boolean UseSafeZones;
		//public static List<String> SSPlugins;
		public static boolean UseGP, UseTowny, UsePS, UseFactions, UseWG;
		public static SSMode GPMode = SSMode.Admin;
		public static SSMode WGMode = SSMode.Specific;
	}
	
	public static class Listeners {
		public static int[] Exceptions = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
				11, 12, 13, 17, 24, 35, 44, 98, 142 };
		public static boolean UseBlockLimit, BlockCreativeContainer;
	}
	
	public static class LogFilter {
		public static List<String> replaceList = new ArrayList<String>();
		public static boolean logConsole;
		public static String logLocation;
	}
	
	public static class Threads {
		public static int saveSpeed, worldCleanerSpeed, inventorySpeed, SSEntityRemoverSpeed, gemArmorSpeed;
		public static boolean GAMovement, GAOffensive;
		public static boolean SSDisableEntities, SSDechargeEE, SSDisableArcane;
		public static boolean RMDB, UseRPTimer;
		public static int ChangeDisabledItemsIntoId;
		public static int RPTickTime;
	}
	
	public static class LWC {
		public static List<String> blocked;
		public static LWCPlugin lwcPlugin;
	}
	
	public static class MetricValues {
		public static int dupeAttempts = 0;
	}
	
	public static class ChunkUnloader {
		public static int maxChunks, maxRadii;
		public static boolean enabled;
	}
}
