package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;
import java.util.List;

public class TRConfigCache {
	public static class Global {
		public static boolean kickFromConsole;
	}
	
	public static class Hacks {
		public static List<String> broadcast = new ArrayList<String>(), kick = new ArrayList<String>();
		public static String broadcastFormat;
	}
	
	public static class Dupes {
		public static List<String> broadcast = new ArrayList<String>(), kick = new ArrayList<String>();
		public static String broadcastFormat;
	}
}
