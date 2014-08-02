package nl.taico.tekkitrestrict;

import java.util.ArrayList;
import java.util.List;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.objects.TRDupeSettings;
import nl.taico.tekkitrestrict.objects.TRHackSettings;
import nl.taico.tekkitrestrict.objects.TREnums.SSMode;

import static nl.taico.tekkitrestrict.config.SettingsStorage.*;

public class TRConfigCache {
	public static void loadConfigCache(){
		Hacks.fly = new TRHackSettings();
		Hacks.fly.enable = hackDupeConfig.getBoolean("Anti-Hacks.Fly.Enabled", true);
		Hacks.fly.kick = hackDupeConfig.getBoolean("Anti-Hacks.Fly.Kick", true);
		Hacks.fly.broadcast = hackDupeConfig.getBoolean("Anti-Hacks.Fly.Broadcast", true);
		Hacks.fly.tolerance = hackDupeConfig.getInt("Anti-Hacks.Fly.Tolerance", 40);
		Hacks.fly.value = (int) Math.round(hackDupeConfig.getDouble("Anti-Hacks.Fly.MinHeight", 3.0));
		Hacks.fly.useCommand = hackDupeConfig.getBoolean("Anti-Hacks.Fly.ExecuteCommand.Enabled", false);
		Hacks.fly.command = hackDupeConfig.getString("Anti-Hacks.Fly.ExecuteCommand.Command", "");
		Hacks.fly.triggerAfter = hackDupeConfig.getInt("Anti-Hacks.Fly.ExecuteCommand.TriggerAfter", 1);
		
		Hacks.forcefield = new TRHackSettings();
		Hacks.forcefield.enable = hackDupeConfig.getBoolean("Anti-Hacks.Forcefield.Enabled", true);
		Hacks.forcefield.kick = hackDupeConfig.getBoolean("Anti-Hacks.Forcefield.Kick", true);
		Hacks.forcefield.broadcast = hackDupeConfig.getBoolean("Anti-Hacks.Forcefield.Broadcast", true);
		Hacks.forcefield.tolerance = hackDupeConfig.getInt("Anti-Hacks.Forcefield.Tolerance", 20);
		Hacks.forcefield.value = hackDupeConfig.getDouble("Anti-Hacks.Forcefield.Angle", 40);
		Hacks.forcefield.useCommand = hackDupeConfig.getBoolean("Anti-Hacks.Forcefield.ExecuteCommand.Enabled", false);
		Hacks.forcefield.command = hackDupeConfig.getString("Anti-Hacks.Forcefield.ExecuteCommand.Command", "");
		Hacks.forcefield.triggerAfter = hackDupeConfig.getInt("Anti-Hacks.Forcefield.ExecuteCommand.TriggerAfter", 1);
		
		Hacks.speed = new TRHackSettings();
		Hacks.speed.enable = hackDupeConfig.getBoolean("Anti-Hacks.MoveSpeed.Enabled", true);
		Hacks.speed.kick = hackDupeConfig.getBoolean("Anti-Hacks.MoveSpeed.Kick", true);
		Hacks.speed.broadcast = hackDupeConfig.getBoolean("Anti-Hacks.MoveSpeed.Broadcast", true);
		Hacks.speed.tolerance = hackDupeConfig.getInt("Anti-Hacks.MoveSpeed.Tolerance", 30);
		Hacks.speed.value = hackDupeConfig.getDouble("Anti-Hacks.MoveSpeed.MaxMoveSpeed", 2.5d);
		Hacks.speed.value *= Hacks.speed.value; //This gets rid of a Math.sqrt and thus saves cpu
		Hacks.speed.useCommand = hackDupeConfig.getBoolean("Anti-Hacks.MoveSpeed.ExecuteCommand.Enabled", false);
		Hacks.speed.command = hackDupeConfig.getString("Anti-Hacks.MoveSpeed.ExecuteCommand.Command", "");
		Hacks.speed.triggerAfter = hackDupeConfig.getInt("Anti-Hacks.MoveSpeed.ExecuteCommand.TriggerAfter", 1);
		
		Hacks.broadcastFormat = hackDupeConfig.getString("Anti-Hacks.BroadcastString", "&9{PLAYER} &ctried to &a{TYPE}&c-hack!");
		
		Dupes.alcBag = new TRDupeSettings();
		Dupes.alcBag.prevent = hackDupeConfig.getBoolean("Anti-Dupes.AlchemyBagDupe.Prevent", true);
		Dupes.alcBag.broadcast = hackDupeConfig.getBoolean("Anti-Dupes.AlchemyBagDupe.Broadcast", true);
		Dupes.alcBag.kick = hackDupeConfig.getBoolean("Anti-Dupes.AlchemyBagDupe.Kick", false);
		Dupes.alcBag.useCommand = hackDupeConfig.getBoolean("Anti-Dupes.AlchemyBagDupe.ExecuteCommand.Enabled", false);
		Dupes.alcBag.command = hackDupeConfig.getString("Anti-Dupes.AlchemyBagDupe.ExecuteCommand.Command", "");
		Dupes.alcBag.triggerAfter = hackDupeConfig.getInt("Anti-Dupes.AlchemyBagDupe.ExecuteCommand.TriggerAfter", 1);
		
		Dupes.rmFurnace = new TRDupeSettings();
		Dupes.rmFurnace.prevent = hackDupeConfig.getBoolean("Anti-Dupes.RMFurnaceDupe.Prevent", true);
		Dupes.rmFurnace.broadcast = hackDupeConfig.getBoolean("Anti-Dupes.RMFurnaceDupe.Broadcast", true);
		Dupes.rmFurnace.kick = hackDupeConfig.getBoolean("Anti-Dupes.RMFurnaceDupe.Kick", false);
		Dupes.rmFurnace.useCommand = hackDupeConfig.getBoolean("Anti-Dupes.RMFurnaceDupe.ExecuteCommand.Enabled", false);
		Dupes.rmFurnace.command = hackDupeConfig.getString("Anti-Dupes.RMFurnaceDupe.ExecuteCommand.Command", "");
		Dupes.rmFurnace.triggerAfter = hackDupeConfig.getInt("Anti-Dupes.RMFurnaceDupe.ExecuteCommand.TriggerAfter", 1);
		
		Dupes.transmute = new TRDupeSettings();
		Dupes.transmute.prevent = hackDupeConfig.getBoolean("Anti-Dupes.TransmuteDupe.Prevent", true);
		Dupes.transmute.broadcast = hackDupeConfig.getBoolean("Anti-Dupes.TransmuteDupe.Broadcast", true);
		Dupes.transmute.kick = hackDupeConfig.getBoolean("Anti-Dupes.TransmuteDupe.Kick", false);
		Dupes.transmute.useCommand = hackDupeConfig.getBoolean("Anti-Dupes.TransmuteDupe.ExecuteCommand.Enabled", false);
		Dupes.transmute.command = hackDupeConfig.getString("Anti-Dupes.TransmuteDupe.ExecuteCommand.Command", "");
		Dupes.transmute.triggerAfter = hackDupeConfig.getInt("Anti-Dupes.TransmuteDupe.ExecuteCommand.TriggerAfter", 1);
		
		Dupes.tankcart = new TRDupeSettings();
		Dupes.tankcart.prevent = hackDupeConfig.getBoolean("Anti-Dupes.TankCartDupe.Prevent", true);
		Dupes.tankcart.broadcast = hackDupeConfig.getBoolean("Anti-Dupes.TankCartDupe.Broadcast", true);
		Dupes.tankcart.kick = hackDupeConfig.getBoolean("Anti-Dupes.TankCartDupe.Kick", false);
		Dupes.tankcart.useCommand = hackDupeConfig.getBoolean("Anti-Dupes.TankCartDupe.ExecuteCommand.Enabled", false);
		Dupes.tankcart.command = hackDupeConfig.getString("Anti-Dupes.TankCartDupe.ExecuteCommand.Command", "");
		Dupes.tankcart.triggerAfter = hackDupeConfig.getInt("Anti-Dupes.TankCartDupe.ExecuteCommand.TriggerAfter", 1);
		
		Dupes.tankcartGlitch = new TRDupeSettings();
		Dupes.tankcartGlitch.prevent = hackDupeConfig.getBoolean("Anti-Dupes.TankCartGlitch.Prevent", true);
		Dupes.tankcartGlitch.broadcast = hackDupeConfig.getBoolean("Anti-Dupes.TankCartGlitch.Broadcast", true);
		Dupes.tankcartGlitch.kick = hackDupeConfig.getBoolean("Anti-Dupes.TankCartGlitch.Kick", false);
		Dupes.tankcartGlitch.useCommand = hackDupeConfig.getBoolean("Anti-Dupes.TankCartGlitch.ExecuteCommand.Enabled", false);
		Dupes.tankcartGlitch.command = hackDupeConfig.getString("Anti-Dupes.TankCartGlitch.ExecuteCommand.Command", "");
		Dupes.tankcartGlitch.triggerAfter = hackDupeConfig.getInt("Anti-Dupes.TankCartGlitch.ExecuteCommand.TriggerAfter", 1);
		
		Dupes.teleport = new TRDupeSettings();
		Dupes.teleport.prevent = hackDupeConfig.getBoolean("Anti-Dupes.TeleportDupe.Prevent", true);
		Dupes.teleport.broadcast = false;
		Dupes.teleport.kick = false;
		Dupes.teleport.useCommand = false;
		Dupes.teleport.command = "";
		Dupes.teleport.triggerAfter = 1;
		
		Dupes.pedestal = new TRDupeSettings();
		Dupes.pedestal.prevent = hackDupeConfig.getBoolean("Anti-Dupes.PedestalEmcGen.Prevent", true);
		Dupes.pedestal.broadcast = hackDupeConfig.getBoolean("Anti-Dupes.PedestalEmcGen.Broadcast", true);
		Dupes.pedestal.kick = hackDupeConfig.getBoolean("Anti-Dupes.PedestalEmcGen.Kick", false);
		Dupes.pedestal.useCommand = hackDupeConfig.getBoolean("Anti-Dupes.PedestalEmcGen.ExecuteCommand.Enabled", false);
		Dupes.pedestal.command = hackDupeConfig.getString("Anti-Dupes.PedestalEmcGen.ExecuteCommand.Command", "");
		Dupes.pedestal.triggerAfter = hackDupeConfig.getInt("Anti-Dupes.PedestalEmcGen.ExecuteCommand.TriggerAfter", 1);
		
		Dupes.diskdrive = new TRDupeSettings();
		Dupes.diskdrive.prevent = hackDupeConfig.getBoolean("Anti-Dupes.DiskDriveDupe.Prevent", true);
		Dupes.diskdrive.broadcast = hackDupeConfig.getBoolean("Anti-Dupes.DiskDriveDupe.Broadcast", true);
		Dupes.diskdrive.kick = hackDupeConfig.getBoolean("Anti-Dupes.DiskDriveDupe.Kick", false);
		Dupes.diskdrive.useCommand = hackDupeConfig.getBoolean("Anti-Dupes.DiskDriveDupe.ExecuteCommand.Enabled", false);
		Dupes.diskdrive.command = hackDupeConfig.getString("Anti-Dupes.DiskDriveDupe.ExecuteCommand.Command", "");
		Dupes.diskdrive.triggerAfter = hackDupeConfig.getInt("Anti-Dupes.DiskDriveDupe.ExecuteCommand.TriggerAfter", 1);
		
		Dupes.broadcastFormat = hackDupeConfig.getString("Anti-Dupes.BroadcastString", "&9{PLAYER} &ctried to dupe&6 {ITEM} &cusing &a{TYPE}&c!");
		
		Global.debug = loggingConfig.getBoolean("LogDebug", false);
		Global.favorPerformanceOverMemory = performanceConfig.getBoolean("FavorPerformanceOverMemory", false);
		Global.kickFromConsole = generalConfig.getBoolean("KickFromConsole", false);
		Global.fixTileEntityErrors = generalConfig.getBoolean("TryFixTileEntityErrors", true);
		
		Listeners.UseBlockLimit = limiterConfig.getBoolean("UseLimiter", true);
		Listeners.BlockCreativeContainer = limitedCreativeConfig.getBoolean("LimitedCreativeNoContainer", true);
		Listeners.UseNoItem = bannedConfig.getBoolean("UseItemBanner", true);
		Listeners.UseLimitedCreative = limitedCreativeConfig.getBoolean("UseLimitedCreative", true);
		Listeners.useNoInteractPerms = bannedConfig.getBoolean("UseNoInteractPermissions", false);
		Listeners.UseWrenchFixer = generalConfig.getBoolean("UseWrenchFix", true);
		
		LogFilter.logLocation = loggingConfig.getString("SplitLogsLocation", "log");
		LogFilter.fileFormat = loggingConfig.getString("FilenameFormat", "{TYPE}-{DAY}-{MONTH}-{YEAR}.log");
		LogFilter.logFormat = loggingConfig.getString("LogStringFormat", "[{HOUR}:{MINUTE}:{SECOND}] {INFO}");
		
		LogFilter.logAllCommandsFile = loggingConfig.getString("LogAllCommandsToFile", "Command");
		if (LogFilter.logAllCommandsFile.equalsIgnoreCase("false")) LogFilter.logAllCommands = false;
		else LogFilter.logAllCommands = true;
		
		LogFilter.logNEIGiveFile = loggingConfig.getString("LogNEIGiveToFile", "SpawnItem");
		if (LogFilter.logNEIGiveFile.equalsIgnoreCase("false")) LogFilter.logNEIGive = false;
		else LogFilter.logNEIGive = true;
		
		Threads.gemArmorSpeed = performanceConfig.getInt("GemArmorDThread", 120);
		Threads.inventorySpeed = performanceConfig.getInt("InventoryThread", 400);
		Threads.saveSpeed = performanceConfig.getInt("AutoSaveThreadSpeed", 11000);
		Threads.SSEntityRemoverSpeed = performanceConfig.getInt("SSEntityRemoverThread", 500);
		Threads.worldCleanerSpeed = performanceConfig.getInt("WorldCleanerThread", 60000);
		Threads.chunkUnloaderSpeed = performanceConfig.getInt("ChunkUnloader", 90000);
		
		Threads.GAMovement = modModificationsConfig.getBoolean("GemArmor.AllowDefensive", true);
		Threads.GAOffensive = modModificationsConfig.getBoolean("GemArmor.AllowOffensive", false);
		
		Threads.SSDisableEntities = safeZoneConfig.getBoolean("InSafeZones DisableEntities", false);
		Threads.SSDechargeEE = safeZoneConfig.getBoolean("InSafeZones DechargeEE", true);
		Threads.SSDisableArcane = safeZoneConfig.getBoolean("InSafeZones DisableRingOfArcana", true);
		List<String> exempt = safeZoneConfig.getStringList("InSafeZones ExemptEntityTypes");
		Threads.SSClassBypasses = new ArrayList<Class<?>>();
		for (String s : exempt){
			if (s.isEmpty()) continue;
			try {
				Class<?> cl = Class.forName("org.bukkit.entity."+s);
				Threads.SSClassBypasses.add(cl);
			} catch (Exception ex){
				try {
					Class<?> cl = Class.forName("org.bukkit.entity."+Character.toUpperCase(s.charAt(0)) + s.substring(1));
					Threads.SSClassBypasses.add(cl);
				} catch (Exception ex2){
					Warning.config("Invalid value in ExemptEntityTypes in SafeZones.config: cannot find class org.bukkit.entity."+ s + "!", false);
					continue;
				}
			}
		}
		
		Threads.RMDB = bannedConfig.getBoolean("RemoveDisabledItemBlocks", false);
		Threads.ChangeDisabledItemsIntoId = bannedConfig.getInt("ChangeDisabledItemsIntoId", 3);
		
		SafeZones.UseSafeZones = safeZoneConfig.getBoolean("UseSafeZones", true);
		SafeZones.useNative = safeZoneConfig.getBoolean("SSEnabledPlugins.TekkitRestrict", true);
		SafeZones.UseFactions = safeZoneConfig.getBoolean("SSEnabledPlugins.Factions", true);
		SafeZones.UseGP = safeZoneConfig.getBoolean("SSEnabledPlugins.GriefPrevention", true);
		SafeZones.UsePS = safeZoneConfig.getBoolean("SSEnabledPlugins.PreciousStones", true);
		SafeZones.UseTowny = safeZoneConfig.getBoolean("SSEnabledPlugins.Towny", true);
		SafeZones.UseWG = safeZoneConfig.getBoolean("SSEnabledPlugins.WorldGuard", true);
		SafeZones.GPMode = SSMode.parse(safeZoneConfig.getString("GriefPreventionSafeZoneMethod", "admin"));
		SafeZones.WGMode = SSMode.parse(safeZoneConfig.getString("WorldGuardSafeZoneMethod", "specific"));
		
		ChunkUnloader.enabled = unloadConfig.getBoolean("UseChunkUnloader", false);
		//ChunkUnloader.maxChunks = unloadConfig.getInt("MaxChunks", 3000);
		ChunkUnloader.maxChunksEnd = unloadConfig.getInt("MaxChunks.TheEnd", 200);
		ChunkUnloader.maxChunksNether = unloadConfig.getInt("MaxChunks.Nether", 400);
		ChunkUnloader.maxChunksNormal = unloadConfig.getInt("MaxChunks.Normal", 4000);
		ChunkUnloader.maxChunksTotal = unloadConfig.getInt("MaxChunks.Total", 4000);
		ChunkUnloader.unloadOrder = unloadConfig.getInt("UnloadOrder", 0);
		ChunkUnloader.maxRadii = unloadConfig.getInt("MaxRadii", 256);
		
		Logger.LogAmulets = loggingConfig.getBoolean("LogAmulets", false);
		Logger.LogRings = loggingConfig.getBoolean("LogRings", false);
		Logger.LogDMTools = loggingConfig.getBoolean("LogDMTools", false);
		Logger.LogRMTools = loggingConfig.getBoolean("LogRMTools", false);
		Logger.LogEEMisc = loggingConfig.getBoolean("LogEEMisc", false);
		Logger.LogEEDestructive = loggingConfig.getBoolean("LogEEDestructive", false);
	}

	public static class Global {
		public static boolean kickFromConsole, debug;
		public static boolean favorPerformanceOverMemory;
		public static boolean fixTileEntityErrors;
	}
	
	public static class Logger {
		public static boolean LogAmulets, LogRings, LogDMTools, LogRMTools, LogEEMisc, LogEEDestructive;
	}
	
	public static class Hacks {
		public static TRHackSettings fly, forcefield, speed;
		public static String broadcastFormat;
	}
	
	public static class Dupes {
		public static TRDupeSettings alcBag, rmFurnace, tankcart, tankcartGlitch, transmute, pedestal, teleport, diskdrive;
		public static String broadcastFormat;
	}
	
	public static class SafeZones {
		//public static boolean SSDisableFly;
		public static boolean UseSafeZones;
		//public static List<String> SSPlugins;
		public static boolean UseGP, UseTowny, UsePS, UseFactions, UseWG, useNative;
		public static SSMode GPMode = SSMode.Admin;
		public static SSMode WGMode = SSMode.Specific;
		public static SSMode FMode = SSMode.All;
	}
	
	public static class Listeners {
		public static boolean UseBlockLimit, BlockCreativeContainer;
		public static boolean UseNoItem, UseLimitedCreative, useNoInteractPerms;
		public static boolean UseWrenchFixer;
	}
	
	public static class LogFilter {
		//public static List<String> replaceList = new ArrayList<String>();
		//public static boolean splitLogs, filterLogs;
		public static String logLocation, fileFormat, logFormat;
		public static boolean logAllCommands, logNEIGive;
		public static String logAllCommandsFile, logNEIGiveFile;
		public static FileLog logAllCommandsLog, logNEIGiveLog;
	}
	
	public static class Threads {
		public static int saveSpeed, worldCleanerSpeed, inventorySpeed, SSEntityRemoverSpeed, gemArmorSpeed, chunkUnloaderSpeed;
		public static boolean GAMovement, GAOffensive;
		public static boolean SSDisableEntities, SSDechargeEE, SSDisableArcane;
		public static boolean RMDB;//, UseRPTimer;
		public static int ChangeDisabledItemsIntoId;
		//public static int RPTickTime;
		
		public static ArrayList<Class<?>> SSClassBypasses = new ArrayList<Class<?>>();
	}
	
	public static class LWC {
		public static boolean lwc;
	}
	
	public static class ChunkUnloader {
		public static int maxRadii;
		public static boolean enabled;
		public static int maxChunksEnd, maxChunksNether, maxChunksNormal;//TODO
		public static int maxChunksTotal;//TODO
		public static int unloadOrder;
	}
}
