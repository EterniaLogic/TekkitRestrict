package com.github.dreadslicer.tekkitrestrict.objects;

public class TREnums {
	public enum HackType {
		fly, forcefield, speed
	}
	
	public enum TRClickType{
		Left, Right, Both, Trample, All;
		public boolean right(){return (this == TRClickType.Right);}
		public boolean left(){return (this == TRClickType.Left);}
		public boolean both(){return (this == TRClickType.Both);}
		public boolean trample(){return (this == TRClickType.Trample);}
		public boolean all(){return (this == TRClickType.All);}
	}
	
	public enum ConfigFile {
		General, Advanced, ModModifications, DisableClick, DisableItems, Hack, LimitedCreative, Logging, TPerformance, MicroPermissions, SafeZones, EEPatch;
	}
	
	public enum SSMode {
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
	
	public enum SSPlugin {
		GriefPrevention, WorldGuard, PreciousStones, Factions, Towny;
		public boolean GP(){return (this == SSPlugin.GriefPrevention);}
		public boolean WG(){return (this == SSPlugin.WorldGuard);}
		public boolean PS(){return (this == SSPlugin.PreciousStones);}
		public boolean F(){return (this == SSPlugin.Factions);}
		public boolean T(){return (this == SSPlugin.Towny);}
	}
}
