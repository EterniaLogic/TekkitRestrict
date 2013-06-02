package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.List;

public class TRConfigCache {
	public static class Global {
		public static boolean kickFromConsole, debug;
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
		public static boolean allowNormalUser;
	}
	
	public static class Listeners {
		public static int[] Exceptions = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
				11, 12, 13, 17, 24, 35, 44, 98, 142 };
		public static boolean UseBlockLimit;
	}
	
	public static class LogFilter {
		public static List<String> replaceList = new ArrayList<String>();
		public static boolean logConsole;
		public static String logLocation;
	}
}
