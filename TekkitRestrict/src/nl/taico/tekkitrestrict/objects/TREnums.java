package nl.taico.tekkitrestrict.objects;

import java.util.logging.Level;

public class TREnums {
	public enum TRLogLevel {
		FINEST, FINER, FINE, INFO, WARNING, SEVERE, PLAYER_COMMAND, FINELEVELS, ERRORLEVELS, ALL;
		
		public boolean doesApply(Level level){
			if (this == TRLogLevel.ALL) return true;
			
			if (level == Level.INFO){
				return this == TRLogLevel.INFO;
			} else if (level == Level.WARNING){
				return this == TRLogLevel.WARNING || this == TRLogLevel.ERRORLEVELS;
			} else if (level == Level.SEVERE){
				return this == TRLogLevel.SEVERE || this == TRLogLevel.ERRORLEVELS;
			} else if (level == Level.FINE){
				return this == TRLogLevel.FINE || this == TRLogLevel.FINELEVELS;
			} else if (level == Level.FINER){
				return this == TRLogLevel.FINER || this == TRLogLevel.FINELEVELS;
			} else if (level == Level.FINEST){
				return this == TRLogLevel.FINEST || this == TRLogLevel.FINELEVELS;
			}
			
			return false;
		}
		
		public boolean forCommands(){
			return this == TRLogLevel.PLAYER_COMMAND || this == TRLogLevel.ALL;
		}
	}
	
	public enum HackType {
		fly, forcefield, speed
	}
	
	public enum DupeType {
		rmFurnace, alcBag, tankCart, tankCartGlitch, teleport, transmution, pedestal, diskdrive
	}
	
	public enum TRClickType{
		Left, Right, Both, Trample, All;
		public boolean right(){return (this.equals(TRClickType.Right));}
		public boolean left(){return (this.equals(TRClickType.Left));}
		public boolean both(){return (this.equals(TRClickType.Both));}
		public boolean trample(){return (this.equals(TRClickType.Trample));}
		public boolean all(){return (this.equals(TRClickType.All));}
	}
	
	public enum ConfigFile {
		General, Advanced, ModModifications, DisableClick, DisableItems, HackDupe, LimitedCreative, Logging, TPerformance, GroupPermissions, SafeZones, Database, EEPatch;
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

	public enum DBType {
		SQLite, MySQL, Unknown;
	}
	
	public enum SafeZone {
		isNone(0), isAllowedStrict(1), isAllowedNonStrict(2), isDisallowed(3), hasBypass(5), pluginDisabled(6);
		private int number = 0;
		private SafeZone(int nr){
			number = nr;
		}
		
		public int getNumber(){
			return this.number;
		}
		
		public static int getNumber(SafeZone safeZone){
			return safeZone.getNumber();
		}
		
		public boolean isOne(){
			return (number == 1 || number == 2 || number == 3);
		}
		
		public static boolean isOne(SafeZone safeZone){
			return safeZone.isOne();
		}
		
		public static SafeZone getEnum(int number){
			if (number == 0) return SafeZone.isNone;
			else if (number == 1) return SafeZone.isAllowedStrict;
			else if (number == 2) return SafeZone.isAllowedNonStrict;
			else if (number == 3) return SafeZone.isDisallowed;
			else if (number == 5) return SafeZone.hasBypass;
			else if (number == 6) return SafeZone.pluginDisabled;
			else {
				throw new IllegalArgumentException("Number is invalid! Only 0, 1, 2, 3, 5 and 6 are allowed!");
			}
		}
	}

	public enum ChunkUnloadMethod {//TODO make an index of chunks.
		/**
		 * Forced: only unloads when forced; goes to limit, not below it<br>
		 * Low: only chunks not kept loaded by chunkloaders, players (radius x2), spawn
		 */
		UnloadLowWhenForced(1, 2),
		/**
		 * Forced: only unloads when forced; goes to limit, not below it<br>
		 * Normal: only chunks not kept loaded by chunkloaders, players, spawn
		 */
		UnloadNormalWhenForced(2, 2),
		/**
		 * Forced: only unloads when forced; goes to limit, not below it<br>
		 * High: only chunks not kept loaded by players.
		 */
		UnloadHighWhenForced(3, 2),
		/**
		 * Forced: only unloads when forced; goes to limit, not below it<br>
		 * Extreme: unloads all chunks, does no checks.
		 * @deprecated No one should use this, it makes no sense.
		 */
		UnloadExtremeWhenForced(4, 2),
		/**
		 * Forced_Plus: only unloads when forced; goes to limit and x % below it<br>
		 * Low: only chunks not kept loaded by chunkloaders, players (radius x2), spawn
		 */
		UnloadLowWhenForced_Plus(1, 1),
		/**
		 * Forced_Plus: only unloads when forced; goes to limit and x % below it<br>
		 * Normal: only chunks not kept loaded by chunkloaders, players, spawn
		 */
		UnloadNormalWhenForced_Plus(2, 1),
		/**
		 * Forced_Plus: only unloads when forced; goes to limit and x % below it<br>
		 * High: only chunks not kept loaded by players.
		 */
		UnloadHighWhenForced_Plus(3, 1),
		/**
		 * Forced_Plus: only unloads when forced; goes to limit and x % below it<br>
		 * Extreme: unloads all chunks, does no checks.
		 * @deprecated No one should use this, it makes no sense.
		 */
		UnloadExtremeWhenForced_Plus(4, 1),
		/**
		 * Always: always unloads chunks; unloads as many as possible<br>
		 * Low: only chunks not kept loaded by chunkloaders, players (radius x2), spawn
		 */
		UnloadLowAlways(1, 0),
		/**
		 * Always: always unloads chunks; unloads as many as possible<br>
		 * Normal: only chunks not kept loaded by chunkloaders, players, spawn
		 */
		UnloadNormalAlways(2, 0),
		/**
		 * Always: always unloads chunks; unloads as many as possible<br>
		 * High: only chunks not kept loaded by players.
		 */
		UnloadHighAlways(3, 0),//kinda impossible, wont unload chunkloader chunks if not forced.
		/**
		 * Always: always unloads chunks; unloads as many as possible<br>
		 * Extreme: unloads all chunks, does no checks.
		 */
		UnloadExtremeAlways(4, 0),
		/**
		 * Unload all the chunks in a world that are not kept loaded by players or worldanchors.
		 */
		UnloadAllChunksUnforced(9, -1),
		/**
		 * Unload chunks until the total chunk amount is good again.<br>
		 * Equal to unloading unforced first, and then AllChunkUnforced if it cannot lower enough.<br>
		 * If this can still not get by the limit, it will also unload worldanchor chunks.
		 */
		UnloadUnforced(2, -1),
		/**
		 * Unload ALL chunks in a world with force, keeping only ones loaded by players.
		 */
		UnloadAllChunksForced(3, -1),
		/**
		 * Unload ALL CHUNKS IN A WORLD no matter what.
		 */
		UnloadWorldForced(4, -1);
		public final byte nr;
		public final byte forced;
		ChunkUnloadMethod(int nr, int forced){
			this.nr = (byte) nr;
			this.forced = (byte) forced;
		}
		
		public boolean isForced(){
			return this.forced==2;
		}
		public boolean isForced_Plus(){
			return this.forced==1;
		}
		public boolean isAlways(){
			return this.forced==0;
		}
		
		public boolean isLow(){
			return this.nr==1;
		}
		public boolean isNormal(){
			return this.nr==2;
		}
		public boolean isHigh(){
			return this.nr==3;
		}
		public boolean isExtreme(){
			return this.nr==4;
		}
	}
}
