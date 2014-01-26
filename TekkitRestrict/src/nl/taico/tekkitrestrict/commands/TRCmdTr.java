package nl.taico.tekkitrestrict.commands;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.NameProcessor;
import nl.taico.tekkitrestrict.TR;
import nl.taico.tekkitrestrict.TR.FixPack;
import nl.taico.tekkitrestrict.TRConfigCache;
import nl.taico.tekkitrestrict.TRDB;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor;
import nl.taico.tekkitrestrict.TRPerformance;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.LogFilter;
import nl.taico.tekkitrestrict.Updater.UpdateResult;
import nl.taico.tekkitrestrict.api.SafeZones.SafeZoneCreate;
import nl.taico.tekkitrestrict.eepatch.EEPSettings;
import nl.taico.tekkitrestrict.functions.TRChunkUnloader2;
import nl.taico.tekkitrestrict.functions.TREMCSet;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRNoClick;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.functions.TRSafeZone;
import nl.taico.tekkitrestrict.objects.TREnums.ChunkUnloadMethod;
import nl.taico.tekkitrestrict.objects.TREnums.DBType;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TRLimit;
import nl.taico.tekkitrestrict.objects.TRLocation;
import nl.taico.tekkitrestrict.objects.TRPos;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;
import nl.taico.tekkitrestrict.objects.TREnums.SafeZone;

import static nl.taico.tekkitrestrict.commands.TRCmdHelper.*;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TRCmdTr implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		@NonNull String[] largs = args.clone();
		
		for (int i = 0; i < largs.length; i++){
			largs[i] = largs[i].toLowerCase();
		}
		
		if (largs.length == 0 || largs[0].equals("help")) {
			help(sender);
			return true;
		}
		
		if (largs[0].equals("warnings")){
			warnings(sender, largs);
			return true;
		}
		
		if (largs[0].equals("about") || largs[0].equals("info") || largs[0].equals("status")){
			about(sender);
			return true;
		}
		
		if (largs[0].equals("emc")) {
			emcMain(sender, largs);
			return true;
		}
		
		if (largs[0].equals("admin")) {
			adminMain(sender, args, largs);
			return true;
		}
		
		if (largs[0].equals("debug") && !(sender instanceof Player)){
			debugInfo(sender);
			return true;
		}
		
		if (largs[0].equals("banned")){
			banned(sender, largs, true);
			return true;
		}
		
		msgr(sender, "Unkown subcommand /tr " + largs[0] + "!");
		help(sender);
		return true;
	}

	private void help(CommandSender sender){
		msgy(sender, "[TekkitRestrict v" + tekkitrestrict.version.fullVer + " Commands]");
		msg(sender, "Aliases: /tr, /tekkitrestrict");
		if (sender.hasPermission("tekkitrestrict.emc")) msg(sender, "/tr EMC", "List EMC commands.");
		if (sender.hasPermission("tekkitrestrict.admin")) msg(sender, "/tr admin", "list admin commands");
		msg(sender, "/tr about", "List information about the version and authors of TekkitRestrict");
	}
	private void warnings(CommandSender sender, String largs[]){
		if (noPerm(sender, "warnings")) return;
		
		if (largs.length == 1 || largs.length > 2){
			msg(sender, "/tr warnings load", "display warnings during loading");
			msg(sender, "/tr warnings db", "display database warnings");
			msg(sender, "/tr warnings config", "display config warnings");
			msg(sender, "/tr warnings other", "display other warnings");
			msg(sender, "/tr warnings all", "display all warnings");
			return;
		}
		
		if (largs[1].equals("load")){
			LinkedList<String> msgs = Warning.loadWarnings;
			if (msgs.isEmpty()){
				msg(sender, "There were no warnings during load.");
				return;
			}
			
			for (final String str : msgs){
				msg(sender, str);
			}
			return;
		}
		
		if (largs[1].equals("db")){
			final LinkedList<String> msgs = Warning.dbWarnings;
			if (msgs.isEmpty()){
				msg(sender, "There were no database warnings.");
				return;
			}
			
			for (final String str : msgs){
				msg(sender, str);
			}
			return;
		}
		
		if (largs[1].equals("config")){
			final LinkedList<String> msgs = Warning.configWarnings;
			if (msgs.isEmpty()){
				msg(sender, "There were no config warnings.");
				return;
			}
			
			for (final String str : msgs){
				msg(sender, str);
			}
			return;
		}
		
		if (largs[1].equals("other")){
			final LinkedList<String> msgs = Warning.otherWarnings;
			if (msgs.isEmpty()){
				msg(sender, "There were no other warnings.");
				return;
			}
			
			for (final String str : msgs){
				msg(sender, str);
			}
			return;
		}
		
		if (largs[1].equals("all")){
			LinkedList<String> msgs = Warning.loadWarnings;
			if (msgs.isEmpty()){
				msg(sender, "Load Warnings: None");
			} else {
				msg(sender, "Load Warnings:");
				for (final String str : msgs){
					msg(sender, str);
				}
			}
			
			msgs = Warning.dbWarnings;
			if (msgs.isEmpty()){
				msg(sender, "DB Warnings: None");
			} else {
				msg(sender, "DB Warnings:");
				for (final String str : msgs){
					msg(sender, str);
				}
			}
			
			msgs = Warning.configWarnings;
			if (msgs.isEmpty()){
				msg(sender, "Config Warnings: None");
			} else {
				msg(sender, "Config Warnings:");
				for (final String str : msgs){
					msg(sender, str);
				}
			}
			
			msgs = Warning.otherWarnings;
			if (msgs.isEmpty()){
				msg(sender, "Other Warnings: None");
			} else {
				msg(sender, "Other Warnings:");
				for (final String str : msgs){
					msg(sender, str);
				}
			}
			return;
		}
		
		msg(sender, "/tr warnings load", "display warnings during loading");
		msg(sender, "/tr warnings config", "display config warnings");
		msg(sender, "/tr warnings other", "display other warnings");
		msg(sender, "/tr warnings all", "display all warnings");
		return;
	}
	private void about(CommandSender sender){
		msgy(sender, "[TekkitRestrict About]");
		msgb(sender, "Former author and creator: " + ChatColor.GREEN + "DreadSlicer/EterniaLogic");
		msgb(sender, "Current author: " + ChatColor.GREEN + "Taeir");
		msg(sender, "");
		msgb(sender, "Version: " + ChatColor.GREEN + tekkitrestrict.version.toMetricsVersion());
		if (tekkitrestrict.useTMetrics){
			msgb(sender, "Server UID: " + ChatColor.GREEN + tekkitrestrict.tmetrics.uid);
		}
		
		if (!sender.hasPermission("tekkitrestrict.admin")) return;
		
		if (TR.getEEPatchVersion() != -1d) msgb(sender, "EEPatch version: " + ChatColor.GREEN+(TR.getEEPatchVersion()==0d?"< 1.4":TR.getEEPatchVersion()));
		else msgb(sender, "EEPatch: "+ChatColor.RED+"not installed");
		
		if (FixPack.getNEIVer() != -1d) msgb(sender, "FixPack NEI: " + ChatColor.GREEN + (FixPack.getNEIVer()==0d?"<1.7":FixPack.getNEIVer()));
		else msgb(sender, "FixPack NEI: " + ChatColor.RED + "No");
		if (FixPack.getRailcraftVer() != -1d) msgb(sender, "FixPack RailCraft: " + ChatColor.GREEN + FixPack.getRailcraftVer());
		if (FixPack.getRedPowerVer() != -1d) msgb(sender, "FixPack RedPower: " + ChatColor.GREEN + FixPack.getRedPowerVer());
		if (FixPack.getWRVer() != -1d) msgb(sender, "FixPack WirelessRedstone: " + ChatColor.GREEN + FixPack.getWRVer());
		if (FixPack.getMFFSVer() != -1d) msgb(sender, "FixPack MFFS: " + ChatColor.GREEN + (FixPack.getMFFSVer()==0d?"<1.4":FixPack.getMFFSVer()));
		else msgb(sender, "FixPack MFFS: " + ChatColor.RED + "No");
		if (FixPack.getWMVer() != -1d) msgb(sender, "FixPack WeaponsMod: " + ChatColor.GREEN + (FixPack.getWMVer()==0d?"<1.9":FixPack.getWMVer()));
		else msgb(sender, "FixPack WeaponsMod: " + ChatColor.RED + "No");
		
		
		msgb(sender, "Database version: " + ChatColor.GREEN + tekkitrestrict.dbversion);
		
		if (tekkitrestrict.dbtype == DBType.MySQL)
			msgb(sender, "Database type: "+ChatColor.GREEN+"MySQL");
		else if (tekkitrestrict.dbtype == DBType.SQLite)
			msgb(sender, "Database type: "+ChatColor.GREEN+"SQLite");
		
		switch (tekkitrestrict.dbworking){
			case 0: msgb(sender, "DB working: " + ChatColor.GREEN + "Yes"); break;
			case 2:	msgb(sender, "DB working: " + ChatColor.RED + "Partially; Safezones will not be saved."); break;
			case 4:	msgb(sender, "DB working: " + ChatColor.RED + "Partially; Limits will not be saved."); break;
			case 20: msgb(sender, "DB working: " + ChatColor.RED + "No; Unable to read database file.");
			default: msgb(sender, "DB working: " + ChatColor.RED + "No; Database will reset upon next startup."); break;
		}
		
		if (!tekkitrestrict.useTMetrics)
			msgb(sender, "TMetrics: "+ChatColor.RED+"false");
	}
	private void debugInfo(CommandSender sender){
		if (sender instanceof Player){
			msgr(sender, "Only the console can execute this command!");
			return;
		}
		
		String output = "";
		
		for (String s : TRNoItem.getDebugInfo()) output += s+";";
		for (String s : TRLimiter.getDebugInfo()) output += s+";";
		
		msg(sender, output);
	}
	
	private void emcMain(CommandSender sender, String largs[]){
		if (noPerm(sender, "emc")) return;
		
		if (!tekkitrestrict.EEEnabled){
			msgr(sender, "EquivalentExchange is not enabled!");
			return;
		}
		
		if (largs.length == 1 || largs[1].equals("help")) {
			emcHelp(sender);
			return;
		}
		
		if (largs[1].equals("tempset")) {
			emcTempSet(sender, largs);
			return;
		}
		
		if (largs[1].equals("lookup")) {
			emcLookup(sender, largs);
			return;
		}
		
		msgr(sender, "Unknown subcommand /tr emc " + largs[1] + "!");
		emcHelp(sender);
		
	}
	private void emcHelp(CommandSender sender){
		msgy(sender, "[TekkitRestrict v" + tekkitrestrict.version.fullVer + " EMC Commands]");
		msg(sender, "/tr emc tempset <id[:data]> <EMC>", "Set an emc value till the next restart.");
		msg(sender, "/tr emc lookup <id[:data]>", "Check the emc value of an item.");
	}
	private void emcTempSet(CommandSender sender, String largs[]){
		if (noPerm(sender, "emc.tempset")) return;
		
		if (largs.length != 4){
			msgr(sender, "Incorrect syntaxis!");
			msgr(sender, "Correct usage: /tr emc tempset <id:data> <EMC>");
			return;
		}
		
		int emc = 0;
		try {
			emc = Integer.parseInt(largs[3]);
			if (emc < 0){
				msgr(sender, "Negative values are not allowed!");
				return;
			}
		} catch (NumberFormatException ex){
			msgr(sender, "This is not a valid number!");
			return;
		}
		
		try {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(largs[2]);
			} catch (TRException ex) {
				msgr(sender, "Invalid item string:");
				msgr(sender, ex.getMessage());
				return;
			}
			for (TRItem isr : iss) {
				final int data = isr.data == -1 || isr.data == 0 ? 0 : isr.data;
				if (emc > 0) ee.EEMaps.addEMC(isr.id, data, emc);
				else {
					//Remove EMC value.
					final HashMap<Integer, Integer> hm = (HashMap<Integer, Integer>) ee.EEMaps.alchemicalValues.get(isr.id);
					if (hm != null){
						hm.remove(data);
						if (hm.isEmpty()) ee.EEMaps.alchemicalValues.remove(isr.id);
						else ee.EEMaps.alchemicalValues.put(isr.id, hm);
					}
				}
				msgg(sender, "Temporary set " + isr.id + ":" + data + " to " + emc + " EMC.");
			}
		} catch (Exception ex){
			Log.debugEx(ex);
			msgr(sender, "Incorrect syntaxis!");
			msgr(sender, "Correct usage:");
			msgr(sender, "/tr emc tempset <id[:data]> <EMC>");
			msgr(sender, "/tr emc tempset <id-id2> <EMC>");
		}
	}
	private void emcLookup(CommandSender sender, String largs[]){
		if (noPerm(sender, "emc.lookup")) return;
		
		if (largs.length != 3){
			msgr(sender, "Incorrect syntaxis!");
			msgr(sender, "Correct usage: /tr emc lookup <id[:data]>");
			return;
		}
		
		boolean found = false;
		
		try {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(largs[2]);
			} catch (TRException ex) {
				msgr(sender, "Invalid item string:");
				msgr(sender, ex.getMessage());
				return;
			}
			for (TRItem isr : iss) {
				HashMap<Integer, Integer> hm = (HashMap<Integer, Integer>) ee.EEMaps.alchemicalValues.get(isr.id);
				if (hm == null) continue;
				
				if (isr.data == -1) { //Get all data values
					Iterator<Integer> ks = hm.keySet().iterator();//Every data value
					while (ks.hasNext()) {
						final Integer dat = ks.next();
						final Integer emc = hm.get(dat);
						if (emc == null) continue; //Should never happen.
						found = true;
						msg(sender, "[" + isr.id + ":" + dat + "] EMC: " + emc);
					}
				} else {
					final int datax = isr.data == -10 ? 0 : isr.data;
					final Integer emc = hm.get(datax);
					if (emc == null) continue;
					found = true;
					
					msg(sender, "[" + isr.id + ":" + datax + "] EMC: " + emc);
				}
			}
			
			if (!found){
				msgr(sender, "No EMC values found for " + largs[2] + ".");
			}
		} catch (Exception ex) {
			Log.debugEx(ex);
			msgr(sender, "Sorry, EMC lookup unsuccessful...");
			msg(sender, "/tr emc lookup <id[:data]>");
			msg(sender, "/tr emc lookup <id-id2>");
		}
	}
	
	private void adminMain(CommandSender sender, String[] args, String largs[]){
		if (noPerm(sender, "admin")) return;

		if (largs.length == 1) {
			adminHelp(sender, 1);
			return;
		}
		
		if (largs[1].equals("2")) {
			adminHelp(sender, 2);
			return;
		}
		
		if (largs[1].equals("3")) {
			adminHelp(sender, 3);
			return;
		}
		
		if (largs[1].equals("help")) {
			int page = 1;
			if (largs.length == 3){
				try { page = Integer.parseInt(largs[2]); } catch (NumberFormatException ex) {}
			}
			adminHelp(sender, page);
			return;
		}
		
		if (largs[1].equals("reload")) {
			adminReload(sender, largs);
			return;
		}
		
		if (largs[1].equals("update")) {
			adminUpdate(sender, largs);
			return;
		}
		
		if (largs[1].equals("threadlag")) {
			adminThreadLag(sender);
			return;
		}
		
		if (largs[1].equals("reinit")) {
			adminReinit(sender);
			return;
		}
		
		if (largs[1].equals("safezone")) {
			try {
				ssMain(sender, args, largs);
			} catch (Exception ex) {
				msgr(sender, "An error has occurred processing your command!");
				Warning.other("Error occurred in /tr admin safezone! Please inform the author.", false);
				Log.Exception(ex, false);
			}
			return;
		}
		
		if (largs[1].equals("limit")) {
			try {
				limitMain(sender, largs);
			} catch (Exception ex) {
				msgr(sender, "An error has occurred processing your command!");
				Warning.other("Error occurred in /tr admin limit! Please inform the author.", false);
				Log.Exception(ex, false);
			}
			return;
		}
		
		if (largs[1].equals("test")) {
			adminTest(sender, largs);
			return;
		}
		
		if (largs[1].equals("unloadchunks")){
			adminUnloadChunks(sender, largs);
			return;
		}

		msgr(sender, "Unknown subcommand /tr admin " + largs[1] + "!");
		msg(sender, "Use /tr admin help to see all subcommands.");
	}
	private void adminReload(CommandSender sender, String largs[]){
		if (noPerm(sender, "admin.reload")) return;
		
		if (largs.length == 3){
			tekkitrestrict.getInstance().saveDefaultConfig(false);
			tekkitrestrict.getInstance().reloadConfig();
			if (largs[2].equals("limiter")){
				TRLimiter.reload();
				msg(sender, "Limiter Reloaded!");
			} else if (largs[2].equals("noitem")){
				TRNoItem.reload();
				msg(sender, "NoItem (Banned Items) Reloaded!");
			} else if (largs[2].equals("creative") || largs[2].equals("limitedcreative")){
				TRNoItem.reload();
				msg(sender, "Limited Creative Banned Items Reloaded!");
			} else if (largs[2].equals("noclick")){
				TRNoClick.reload();
				msg(sender, "NoClick (disabled interactions) Reloaded!");
			} else if (largs[2].equals("Logger") || largs[2].equals("logfilter") || largs[2].equals("logsplitter")){
				LogFilter.replaceList = tekkitrestrict.config.getStringList(ConfigFile.Logging, "LogFilter");
				LogFilter.splitLogs = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "SplitLogs", true);
				LogFilter.filterLogs = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "FilterLogs", true);
				LogFilter.logLocation = tekkitrestrict.config.getString(ConfigFile.Logging, "SplitLogsLocation", "log");
				LogFilter.fileFormat = tekkitrestrict.config.getString(ConfigFile.Logging, "FilenameFormat", "{TYPE}-{DAY}-{MONTH}-{YEAR}.log");
				LogFilter.logFormat = tekkitrestrict.config.getString(ConfigFile.Logging, "LogStringFormat", "[{HOUR}:{MINUTE}:{SECOND}] {INFO}");
				TRConfigCache.Logger.LogAmulets = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "LogAmulets", false);
				TRConfigCache.Logger.LogRings = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "LogRings", false);
				TRConfigCache.Logger.LogDMTools = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "LogDMTools", false);
				TRConfigCache.Logger.LogRMTools = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "LogRMTools", false);
				TRConfigCache.Logger.LogEEMisc = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "LogEEMisc", false);
				TRConfigCache.Logger.LogEEDestructive = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "LogEEDestructive", false);
				msg(sender, "Log Filter/Splitter Reloaded!");
			} else if (largs[2].equals("emcset")){
				TREMCSet.reload();
				msg(sender, "EMC setter Reloaded!");
			} else {
				if (largs[2].equals("help"))
					msgy(sender, "Possible subcommands: ");
				else
					msgr(sender, "Unknown subcommand! Possible subcommands: ");
				
				msgy(sender, "Limiter, Noitem, LimitedCreative, NoClick, Logger and EMCSet");
			}
			return;
		}

		tekkitrestrict.getInstance().reload(true, false);
		msg(sender, "Tekkit Restrict Reloaded!");
		return;
	}
	private void adminUpdate(CommandSender sender, String largs[]){
		if (noPerm(sender, "admin.update")) return;
		
		if (tekkitrestrict.updater2 == null){
			msgr(sender, "The update check is disabled in the config.");
			return;
		}
		
		boolean check = false;
		if (largs.length == 3){
			if (largs[2].equals("check")) check = true;
			else if (largs[2].equals("download")) check = false;
			else {
				msgr(sender, "Invalid argument: \""+largs[2]+"\"! Only \"check\" and \"download\" are allowed.");
				return;
			}
		} else {
			msgr(sender, "Invalid syntaxis! Correct usage: /tr admin update <check|download>");
			return;
		}
		
		final UpdateResult result = tekkitrestrict.updater2.getResult();
		if (result == UpdateResult.DISABLED){
			msgr(sender, "The update check is disabled in the global Updater config.");
			return;
		} else if (result == UpdateResult.SUCCESS){
			msgg(sender, "The update " + ChatColor.YELLOW + tekkitrestrict.updater2.getLatestName() + ChatColor.GREEN + " is available, and has already been downloaded.");
			msgg(sender, "This update will be installed on the next server start.");
		} else if (result == UpdateResult.UPDATE_AVAILABLE){
			if (check){
				msgg(sender, "The update " + ChatColor.YELLOW + tekkitrestrict.updater2.getLatestName() + ChatColor.GREEN + " is available.");
				msgy(sender, "Use /tr admin update download to start downloading.");
			} else {
				msgg(sender, "TekkitRestrict will now start downloading " + ChatColor.YELLOW + tekkitrestrict.updater2.getLatestName() + ChatColor.GREEN + ".");
				tekkitrestrict.getInstance().Update();
			}
		} else if (result == UpdateResult.NO_UPDATE){
			msgy(sender, "There is no update available for TekkitRestrict.");
		} else {
			msgr(sender, "An error occurred when trying to check for a new version.");
		}
		
		/*
		UpdateResult result = tekkitrestrict.updater.getResult();
		if (result == UpdateResult.SUCCESS){
			msgg(sender, "The update TekkitRestrict " + ChatColor.YELLOW + tekkitrestrict.updater.shortVersion + ChatColor.GREEN + " is available, and has already been downloaded.");
			msgg(sender, "This update will be installed on the next server start.");
		} else if (result == UpdateResult.UPDATE_AVAILABLE){
			if (check){
				msgg(sender, "The update TekkitRestrict " + ChatColor.YELLOW + tekkitrestrict.updater.shortVersion + ChatColor.GREEN + " is available.");
				msgy(sender, "Use /tr admin update download to start downloading.");
			} else {
				msgg(sender, "TekkitRestrict will now start downloading version " + ChatColor.YELLOW + tekkitrestrict.updater.shortVersion + ChatColor.GREEN + ".");
				tekkitrestrict.getInstance().Update();
			}
		} else if (result == UpdateResult.NO_UPDATE){
			msgy(sender, "There is no update available for TekkitRestrict.");
		} else {
			msgr(sender, "An error occurred when trying to check for a new version.");
		}
		*/
	}
	private void adminThreadLag(CommandSender sender){
		if (noPerm(sender, "admin.threadlag")) return;
		
		TRPerformance.getThreadLag(sender);
	}
	private void adminReinit(CommandSender sender){
		if ((sender instanceof Player) && noPerm(sender, "admin.reinit")) return;
		//Console should always be able to use this, even though permission default is false in plugin.yml
		
		msgr(sender, "Reinitializing server.");
		tekkitrestrict.getInstance().getServer().reload();
	}
	private void adminTest(CommandSender sender, String largs[]){
		if (noPerm(sender, "admin.testsettings")) return;
		
		if (largs.length == 4){
			if (largs[2].equals("eepatch")){
				Field[] fields = EEPSettings.class.getDeclaredFields();
				for (final Field f : fields){
					if (!f.getName().equalsIgnoreCase(largs[3])) continue;
					if (!f.isAccessible()) f.setAccessible(true);
					Object vals;
					try {
						vals = f.get(null);
						if (vals != null && vals instanceof ArrayList){
							ArrayList<?> arr = (ArrayList<?>) vals;
							msg(sender, ChatColor.GOLD + "Blocked actions for " + f.getName() + ":");
							int i = 0;
							for (Object val : arr){
								msg(sender, "["+i+"] "+val.toString());
								i++;
							}
							return;
						} else {
							msgr(sender, "There are no blocked actions for " + f.getName() + ".");
							return;
						}
					} catch (IllegalArgumentException | IllegalAccessException e){
						msgr(sender, "An error occurred!");
						return;
					}
				}
				String pFields = "";
				for (Field f : fields) pFields += " " + f.getName();
				pFields = pFields.trim().replace(" ", ", ");
				msgr(sender, "Unknown field! "+ChatColor.GOLD + "Available fields: " + pFields);
				return;
			}
			
			Class<?>[] classes = TRConfigCache.class.getDeclaredClasses();
			
			Field[] fields = null;
			for (Class<?> c : classes){
				if (c.getSimpleName().equalsIgnoreCase(largs[2])){
					fields = c.getDeclaredFields();
					
					for (final Field f : fields){
						if (!f.getName().equalsIgnoreCase(largs[3])) continue;
						if (!f.isAccessible()) f.setAccessible(true);
						Object val;
						try {
							val = f.get(null);
							msg(sender, c.getSimpleName()+"."+f.getName() + ": " + val);
							return;
						} catch (IllegalArgumentException | IllegalAccessException e) {
							msgr(sender, "An error occurred!");
							return;
						}
					}
					//Not found the field
					
					String pFields = "";
					for (Field f : fields) pFields += " " + f.getName();
					pFields = pFields.trim().replace(" ", ", ");
					
					msgr(sender, "Unknown field! "+ChatColor.GOLD+"Available fields: " + pFields);
					return;
				}
			}
			
			
			
			String pClasses = "";
			for (Class<?> c : classes) pClasses += " " + c.getSimpleName();
			pClasses = pClasses.trim().replace(" ", ", ");
			
			msgr(sender, "Unknown class! "+ChatColor.GOLD+"Available classes: " + pClasses + " and EEPatch");
			return;
		} else {
			msgr(sender, "Invalid syntax! Usage: /tr admin test <class> <field>");
			return;
		}
	}
	private void adminHelp(CommandSender sender, int page) {
		if (page == 1) {
			msgy(sender, "[TekkitRestrict " + tekkitrestrict.version.fullVer + " Admin Commands] Page 1 / 3");
			msg(sender, "/tr admin help <page>", "Show this help.");
			msg(sender, "/tr admin safezone list [page]", "List safezones.");
			msg(sender, "/tr admin safezone check [player]", "Check if a player is in a safezone.");
			msg(sender, "/tr admin safezone addwg <region>", "Add a safezone using WorldGuard.");
			msg(sender, "/tr admin safezone addgp <name>", "Add a safezone using GriefPrevention.");
			msg(sender, "/tr admin safezone rem <name>", "Remove a safezone.");
		} else if (page == 2) {
			msgy(sender, "[TekkitRestrict " + tekkitrestrict.version.fullVer + " Admin Commands] Page 2 / 3");
			msg(sender, "/tr admin limit clear <player>", "Clear a players limits.");
			msg(sender, "/tr admin limit clear <player> <id[:data]>", "Clear a players limits for a specific item.");
			msg(sender, "/tr admin limit list <player> [id]", "List a players limits.");
			msg(sender, "/tr admin unloadchunks low", "Only unload chunks not loaded for a reason, default radius.");
			msg(sender, "/tr admin unloadchunks normal", "Unload normally and unload chunks that are kept loaded by ChunkLoaders.");
			msg(sender, "/tr admin unloadchunks high", "Unload all chunks not loaded by players.");
			msg(sender, "/tr admin unloadchunks extreme", "Unload all chunks no matter what. (WARNING: Can crash server)");
		} else if (page >= 3){
			msgy(sender, "[TekkitRestrict " + tekkitrestrict.version.fullVer + " Admin Commands] Page 3 / 3");
			msg(sender, "/tr admin reload", "Reload TekkitRestrict");
			msg(sender, "/tr admin reload help", "View Reload subcommands");
			msg(sender, "/tr admin update <check|download>", "Check for or download updates.");
			msg(sender, "/tr admin threadlag", "Display threadlag information.");
			msg(sender, ChatColor.STRIKETHROUGH + "/tr admin reinit", ChatColor.STRIKETHROUGH + "Reload the server.");
			msg(sender, "/tr admin test <class> <field>", "Get the value of a setting. (Debug feature)");
		} else {
			msgr(sender, "Page " + page + " doesn't exist. ");
		}
	}
	@SuppressWarnings("deprecation")
	private void adminUnloadChunks(CommandSender sender, String largs[]){
		if (noPerm(sender, "admin.forceunload")) return;
		if (largs.length < 2){
			msgy(sender, "[TekkitRestrict " + tekkitrestrict.version.fullVer + "UnloadChunks help]");
			msg(sender, "/tr admin unloadchunks low", "Only unload chunks not loaded for a reason, default radius.");
			msg(sender, "/tr admin unloadchunks normal", "Unload normally and unload chunks that are kept loaded by ChunkLoaders.");
			msg(sender, "/tr admin unloadchunks high", "Unload all chunks not loaded by players.");
			msg(sender, "/tr admin unloadchunks extreme", "Unload all chunks no matter what. (WARNING: Can crash server)");
			return;
		}
		ChunkUnloadMethod method;
		switch (largs[2]){
			case "low":
				method = ChunkUnloadMethod.UnloadLowWhenForced;
				break;
			case "normal":
				method = ChunkUnloadMethod.UnloadNormalWhenForced;
				break;
			case "high":
				method = ChunkUnloadMethod.UnloadHighWhenForced;
				break;
			case "extreme":
				method = ChunkUnloadMethod.UnloadExtremeWhenForced;
				break;
			default:
				msgr(sender, "Incorrect syntax! Correct usage: /tr admin unloadchunks <low|normal|high|extreme> [world]");
				return;
		}

		for (TRChunkUnloader2 unloader : TRChunkUnloader2.getAll()){
			unloader.forceUnload(sender, method);
		}
	}
	
	private void ssMain(CommandSender sender, String[] args, String[] largs){
		if (noPerm(sender, "admin.safezone")) return;
		
		if (largs.length == 2 || largs[2].equals("help")){
			ssHelp(sender);
			return;
		}
		
		if (largs[2].equals("list")){
			ssList(sender, largs);
			return;
		}
		
		if (largs[2].equals("check")){
			ssCheck(sender, largs);
			return;
		}
		
		if (largs[2].equals("rem") || largs[2].equals("del") || largs[2].equals("remove") || largs[2].equals("delete")){
			ssDel(sender, largs);
			return;
		}
		
		if (largs[2].equals("addwg")){
			ssAddWG(sender, largs);
			return;
		}
		
		if (largs[2].equals("addgp")){
			ssAddGP(sender, largs);
			return;
		}
		
		if (largs[2].equals("addnative")){
			ssAddNative(sender, args, largs);
			return;
		}
		
		msgr(sender, "Unknown subcommand /tr admin safezone " + largs[2] + "!");
		ssHelp(sender);
	}
	private void ssHelp(CommandSender sender){
		msgy(sender, "[TekkitRestrict v" + tekkitrestrict.version.fullVer + " Safezone Commands]");
		msg(sender, "/tr admin safezone check [player]", "Check if a player is in a safezone.");
		msg(sender, "/tr admin safezone list [page]", "List safezones");
		msg(sender, "/tr admin safezone addwg <name>", "Add a WorldGuard safezone");
		msg(sender, "/tr admin safezone addgp <name>", "Add a GriefPrevention safezone");
		msg(sender, "/tr admin safezone del <name>", "remove safezone");
	}
	private void ssList(CommandSender sender, String[] largs){
		if (noPerm(sender, "admin.safezone.list")) return;
		
		int requestedPage = 1;
		if (largs.length == 4){
			try {
				requestedPage = Integer.parseInt(largs[3]);
			} catch (NumberFormatException ex){
				msgr(sender, "Page number incorrect!");
				return;
			}
		}
		int totalPages = TRSafeZone.zones.size() / 5;
		if (totalPages == 0) totalPages = 1;
		if (requestedPage > totalPages) requestedPage = totalPages;
		
		msgy(sender, "SafeZones - Page " + requestedPage + " of " + totalPages);
		for (int i = ((requestedPage-1) * 5); i < (requestedPage * 5); i++){
			if (TRSafeZone.zones.size() <= i) break;
			
			final TRSafeZone z = TRSafeZone.zones.get(i);
			if (z == null) continue;
			
			final String pl;
			if (z.mode == 1) pl = "[WG] ";
			else if (z.mode == 4) pl = "[GP] ";
			else if (z.mode == 0) pl = "[TR] ";
			else if (z.mode == 2) pl = "[PS] ";
			else if (z.mode == 3) pl = "[F] ";
			else pl = "";
			
			msgy(sender, "" + (i+1) + ": " + pl + z.name + " - Loc: [" + z.world + "] [" + z.location.x1 + " " + z.location.y1 + " " + z.location.z1 + "] - [" + z.location.x2 + " " + z.location.y2 + " " + z.location.z2 + "]");
		}
	}
	private void ssCheck(CommandSender sender, String[] largs){
		final Player target;
		if (largs.length == 4){
			if (noPerm(sender, "admin.safezone.check.others")) return;
			target = Bukkit.getPlayer(largs[3]);
			if (target == null){
				msgr(sender, "Cannot find player " + largs[3] + "!");
				return;
			}
		} else {
			if (noPerm(sender, "admin.safezone.check")) return;
			if (sender instanceof Player){
				target = (Player) sender;
			} else {
				msgr(sender, "The console can only use /tr admin safezone check <player>");
				return;
			}
		}
		
		final Object[] temp = TRSafeZone.getSafeZoneStatusFor(target);
		final SafeZone status = (SafeZone) temp[2];
		
		if (status == SafeZone.isNone || status == SafeZone.pluginDisabled){
			msgy(sender, target.getName() + " is currently " + ChatColor.RED + "not" + ChatColor.YELLOW + " in a safezone.");
			return;
		}
		
		final String plugin = (String) temp[0];
		String zone = (String) temp[1];
		
		if (zone.isEmpty()) zone = "NO_NAME";
		if (status == SafeZone.isAllowedStrict) {
			msgy(sender, target.getName() + " is currently in the "+plugin+" safezone " + zone + ", but it doesn't apply to him/her.");
			return;
		}
		
		if (status == SafeZone.isAllowedNonStrict) {
			msgy(sender, target.getName() + " is currently in the "+plugin+" safezone " + zone + ".");
			return;
		}
		
		if (status == SafeZone.isDisallowed) {
			msgy(sender, target.getName() + " is currently in the "+plugin+" safezone " + zone + ".");
			return;
		}
		
		if (status == SafeZone.hasBypass) {
			msgy(sender, target.getName() + " is currently in the "+plugin+" safezone " + zone + ", but it doesn't apply to him/her.");
			return;
		}
	}
	private void ssDel(CommandSender sender, String[] largs){
		if (noPerm(sender, "admin.safezone.remove")) return;
		
		if (largs.length != 4){
			msgr(sender, "Incorrect syntaxis!");
			msgr(sender, "Correct usage: /tr admin safezone rem <name>");
			return;
		}
		
		boolean found = false;
		String name = null;
		TRSafeZone zone = null;
		for (int i = 0; i < TRSafeZone.zones.size(); i++) {
			zone = TRSafeZone.zones.get(i);
			name = zone.name;
			if (!name.equalsIgnoreCase(largs[3])) continue;
			found = true;
		}
		
		if (found && zone != null){
			if (!TRSafeZone.removeSafeZone(zone)){
				msgr(sender, "Unable to remove the safezone. Was the claim already removed?");
				return;
			}
			
			msgg(sender, "Safezone " + name + " removed.");
		} else {
			msgr(sender, "Cannot find safezone " + largs[3] + "!");
			return;
		}
		
		try {
			tekkitrestrict.db.query("DELETE FROM `tr_saferegion` WHERE `name` = '"
							+ TRDB.antisqlinject((name == null ? largs[3] : name)) + "' COLLATE NOCASE");
		} catch (SQLException ex) {}
	}
	private void ssAddWG(CommandSender sender, String[] largs){
		if (noConsole(sender)) return;
		
		if (noPerm(sender, "admin.safezone.addwg")) return;
		if (largs.length != 4){
			msgr(sender, "Incorrect syntaxis!");
			msgr(sender, "Correct usage: /tr admin safezone addwg <name>");
			return;
		}
		
		final String name = largs[3];
		final Player player = (Player) sender;

		for (TRSafeZone current : TRSafeZone.zones){
			if (current.world.equalsIgnoreCase(player.getWorld().getName())){
				if (current.name.toLowerCase().equals(name)){
					msgr(sender, "There is already a safezone by this name!");
					return;
				}
			}
		}
		final PluginManager pm = Bukkit.getPluginManager();
		if (!pm.isPluginEnabled("WorldGuard")){
			msgr(sender, "WorldGuard is not enabled!");
			return;
		}
		
		try {
			final WorldGuardPlugin wg = (WorldGuardPlugin) pm.getPlugin("WorldGuard");
			final Map<String, ProtectedRegion> rm = wg.getRegionManager(player.getWorld()).getRegions();
			final ProtectedRegion pr = rm.get(name);
			if (pr != null) {
				final TRSafeZone zone = new TRSafeZone();
				zone.mode = 1;
				zone.name = name;
				zone.world = player.getWorld().getName();
				zone.location = new TRPos(pr.getMinimumPoint(), pr.getMaximumPoint());
				zone.pluginRegion = pr;
				zone.locSet = true;
				TRSafeZone.zones.add(zone);
				TRSafeZone.save();
				msgg(sender, "Attached to region \"" + name + "\"!");
			} else {
				msgr(sender, "Region does not exist!");
				
				String allregions = "";
				for (final String current : rm.keySet()) allregions += current + ", ";
				
				if (allregions.length()==0) allregions = "There are no regions!";
				else allregions = allregions.substring(0, allregions.length()-2);
				
				msgy(sender, "Possible regions: " + allregions);
				return;
			}
		} catch (Exception E) {
			msgr(sender, "Error! (does the region exist?)");
		}
	}
	private void ssAddGP(CommandSender sender, String[] largs){
		if (noConsole(sender)) return;
		
		if (noPerm(sender, "admin.safezone.addgp")) return;
		
		if (largs.length != 4){
			msgr(sender, "Incorrect syntaxis!");
			msgr(sender, "Correct usage: /tr admin safezone addgp <name>");
			return;
		}
		
		final Player player = (Player) sender;
		final String name = largs[3];

		for (TRSafeZone current : TRSafeZone.zones){
			if (current.world.equalsIgnoreCase(player.getWorld().getName())){
				if (current.name.toLowerCase().equals(name)){
					msgr(sender, "There is already a safezone by this name!");
					return;
				}
			}
		}
		if (!Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")){
			msgr(sender, "GriefPrevention is not enabled!");
			return;
		}
		
		final SafeZoneCreate response = TRSafeZone.addSafeZone(player, "griefprevention", name, null);
		if (response == SafeZoneCreate.AlreadyExists)
			msgr(sender, "This region is already a safezone!");
		else if (response == SafeZoneCreate.RegionNotFound)
			msgr(sender, "There is no region at your current position!");
		else if (response == SafeZoneCreate.PluginNotFound)
			msgr(sender, "Safezones are disabled for GriefPrevention claims.");
		else if (response == SafeZoneCreate.NoPermission)
			msgr(sender, "You either have no permission to modify this claim or you are not allowed to make safezones.");
		else if (response == SafeZoneCreate.Success)
			msgg(sender, "This claim is now a safezone!");
		else 
			msgr(sender, "An undefined error occurred!");

	}
	private void ssAddNative(CommandSender sender, String[] args, String[] largs){
		if (noConsole(sender)) return;
		
		if (noPerm(sender, "admin.safezone.addnative")) return;
		
		if (largs.length != 8){
			msgr(sender, "Incorrect syntaxis!");
			msgr(sender, "Correct usage: /tr admin safezone addnative <x> <z> <x> <z> <name>");
			return;
		}
		
		final String name = largs[7];

		for (TRSafeZone current : TRSafeZone.zones){
			if (current.name.toLowerCase().equals(name)){
				msgr(sender, "There is already a safezone by this name!");
				return;
			}
		}
		
		final int x1, x2, z1, z2;
		try {
			x1 = Integer.parseInt(args[3]);
			z1 = Integer.parseInt(args[4]);
			x2 = Integer.parseInt(args[5]);
			z2 = Integer.parseInt(args[6]);
		} catch (NumberFormatException ex){
			msgr(sender, "Incorrect syntax!");
			msgr(sender, "Correct usage: /tr admin safezone addnative <x> <z> <x> <z> <name>");
			return;
		}
		
		final SafeZoneCreate response = TRSafeZone.addSafeZone((Player) sender, "griefprevention", name, new TRPos(x1, 0, z1, x2, 0, z2));
		if (response == SafeZoneCreate.AlreadyExists)
			msgr(sender, "This region is already a safezone!");
		else if (response == SafeZoneCreate.RegionNotFound)
			msgr(sender, "There is no region at your current position!");
		else if (response == SafeZoneCreate.PluginNotFound)
			msgr(sender, "Native TekkitRestrict safezones are disabled.");
		else if (response == SafeZoneCreate.NoPermission)
			msgr(sender, "You are not allowed to make safezones.");
		else if (response == SafeZoneCreate.Success)
			msgg(sender, "Successfully created safezone!");
		else 
			msgr(sender, "An undefined error occurred!");
	}
	
	private void limitMain(CommandSender sender, String[] largs){
		if (noPerm(sender, "admin.limit")) return;
		
		if (largs.length == 2 || largs[2].equals("help")) {
			limitHelp(sender);
			return;
		}
		
		if (largs[2].equals("clear")) {
			limitClear(sender, largs);
			return;
		}
		
		if (largs[2].equals("list")) {
			limitList(sender, largs);
			return;
		}
		
		msgr(sender, "Unknown subcommand /tr admin limit " + largs[2] + "!");
		limitHelp(sender);
	}
	private void limitHelp(CommandSender sender){
		msgy(sender, "[TekkitRestrict v" + tekkitrestrict.version.fullVer + " Limit Commands]");
		msg(sender, "/tr admin limit list <player>", "View a players limits");
		msg(sender, "/tr admin limit list <player> <id>", "View a specific limit.");
		msg(sender, "/tr admin limit clear <player>", "Clear a players limits.");
		msg(sender, "/tr admin limit clear <player> <id>[:data]", "Clear a players limits for a specific itemid.");
	}
	private void limitClear(CommandSender sender, String[] largs){
		if (noPerm(sender, "admin.limit.clear")) return;
		
		if (largs.length == 3 || largs.length > 5){
			msgr(sender, "Invalid syntaxis!");
			msgr(sender, "Correct usage: /tr admin limit clear <player> [id[:data]]");
			return;
		}
		
		final TRLimiter cc = TRLimiter.getLimiter(largs[3]);
		//if (cc == null){
		//	msgr(sender, "This player doesn't exist!");
		//	return;
		//}
		
		if (largs.length == 4){
			cc.clearLimitsAndClearInDB();
			msgg(sender, "Cleared " + cc.player + "'s block limits!");
			return;
		}
		
		try {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(largs[4]);
			} catch (TRException ex) {
				msgr(sender, "Invalid item string:");
				msgr(sender, ex.getMessage());
				return;
			}
			
			for (TRItem isr : iss) {
				for (TRLimit trl : cc.itemlimits) {
					//tekkitrestrict.log.info(isr.id+":"+isr.getData()+" ?= "+trl.blockID+":"+trl.blockData);
					if (TRNoItem.equalSet(isr.id, isr.data, trl.id, trl.data)) {
						if(trl.placedBlock != null)
							trl.placedBlock.clear();
						else
							trl.placedBlock = new LinkedList<TRLocation>();
					}
					int ci = cc.itemlimits.indexOf(trl);
					if (ci != -1) cc.itemlimits.set(ci, trl);
				}
			}
			msgg(sender, "Cleared " + cc.player + "'s block limits for "+largs[4]);
		} catch (Exception E) {
			msgr(sender, "[Error!] Please use the formats:");
			msg(sender, "/tr admin limit clear <player> [id[:data]]", "Clear a players limits.");
		}
	}
	private void limitList(CommandSender sender, String[] largs){
		if (noPerm(sender, "admin.limit.list")) return;
		
		if (largs.length < 4 || largs.length > 5){
			msgr(sender, "Incorrect syntaxis!");
			msgr(sender, "Correct usage: /tr admin limit list <player> [id]");
			return;
		}
		
		final TRLimiter cc = TRLimiter.getLimiter(largs[3]);
		
		if(cc.itemlimits.isEmpty()){
			msgr(sender, largs[3] + " doesn't have any limits!");
			return;
		}
		
		final Player target = Bukkit.getPlayer(cc.player);
		
		if (largs.length == 5){
			int id;
			try {
				id = Integer.parseInt(largs[4]);
			} catch (NumberFormatException ex){
				msgr(sender, "You didn't specify a valid number!");
				return;
			}
			
			if (target != null){
				for (TRLimit l : cc.itemlimits) {
					if (l.id != id) continue;
					final int cccl = cc.getMax(target, l.id, l.data);
					msg(sender, "[" + l.id + ":" + l.data + "] - " + l.placedBlock.size() + "/" + (cccl == -1 ? 0 : cccl) + " blocks");
				}
			} else {
				msg(sender, "As "+cc.player+" is offline, his/her max limits cannot be listed.");
				for (TRLimit l : cc.itemlimits) {
					if (l.id != id) continue;
					msg(sender, "[" + l.id + ":" + l.data + "] - " + l.placedBlock.size()+"/? blocks");
				}
			}
			
		} else {
			if (target != null){
				for (TRLimit l : cc.itemlimits) {
					final int cccl = cc.getMax(target, l.id, l.data);
					msg(sender, "[" + l.id + ":" + l.data + "] - " + l.placedBlock.size()+"/"+(cccl == -1 ? 0 : cccl)+" blocks");
				}
			} else {
				msg(sender, "As "+cc.player+" is offline, his/her max limits cannot be listed.");
				for (TRLimit l : cc.itemlimits) {
					msg(sender, "[" + l.id + ":" + l.data + "] - " + l.placedBlock.size()+"/? blocks");
				}
			}
		}
	}

	private void banned(CommandSender sender, String[] largs, boolean tr){
		int page = 1;
		if (largs.length > 2){
			msgr(sender, "Incorrect syntax! Correct usage: "+(tr?"/tr banned ":"/banned ")+"<page>");
			return;
		}
		try {
			if (largs.length == 2) page = Integer.parseInt(largs[1]);
			else page = 1;
		} catch (NumberFormatException ex){
			msgr(sender, largs[1] + " is not a valid number!");
			return;
		}
		final List<TRItem> banned = TRNoItem.getBannedItems();
		final int lastpage = (int) Math.ceil(banned.size()/8);
		final int start = (page-1) * 8; //8
		final int end = page * 8; //15
		if (page <= 0 || start > banned.size()){
			msgr(sender, "Page " + page + " does not exist!");
			msgr(sender, "Last page: " + lastpage + ".");
			return;
		}
		//TRItemProcessor.
		if (banned.size() > 0){
			msgb(sender, "Banned Items - Page " + page + " of " + lastpage);
			msgr(sender, "" + ChatColor.BOLD + "Banned Item - " + ChatColor.RED + "Reason");
			for (int i = start; i<banned.size() && i < end; i++){
				final TRItem it = banned.get(i);
				
				final String name = NameProcessor.getName(it);
				if (name == null) continue;
				
				String reason = it.msg;
				if (reason == null || reason.isEmpty()) reason = "None";
				else if (reason.toLowerCase().contains("reason:")){
					reason = reason.split("(?i)Reason:")[1].trim();
					if (reason.isEmpty()) reason = it.msg;
				}
				
				if (reason.isEmpty()) reason = "None";
				
				msgg(sender, NameProcessor.getName(it) + " - " + ChatColor.BLUE + it.msg);
			}
		} else {
			msgg(sender, "There are no banned items!");
		}
	}
}
