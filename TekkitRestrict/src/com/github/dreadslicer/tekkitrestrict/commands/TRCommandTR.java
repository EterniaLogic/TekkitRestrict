package com.github.dreadslicer.tekkitrestrict.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.dreadslicer.tekkitrestrict.Log;
import com.github.dreadslicer.tekkitrestrict.Send;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.LogFilter;
import com.github.dreadslicer.tekkitrestrict.TREMCSet;
import com.github.dreadslicer.tekkitrestrict.TRItemProcessor;
import com.github.dreadslicer.tekkitrestrict.TRDB;
import com.github.dreadslicer.tekkitrestrict.TRException;
import com.github.dreadslicer.tekkitrestrict.TRLimiter;
import com.github.dreadslicer.tekkitrestrict.TRLogger;
import com.github.dreadslicer.tekkitrestrict.TRNoClick;
import com.github.dreadslicer.tekkitrestrict.TRNoItem;
import com.github.dreadslicer.tekkitrestrict.TRPerformance;
import com.github.dreadslicer.tekkitrestrict.TRSafeZone;
import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;
import com.github.dreadslicer.tekkitrestrict.Log.Warning;
import com.github.dreadslicer.tekkitrestrict.Updater.UpdateResult;
import com.github.dreadslicer.tekkitrestrict.api.SafeZones.SafeZoneCreate;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.SafeZone;
import com.github.dreadslicer.tekkitrestrict.objects.TRItem;
import com.github.dreadslicer.tekkitrestrict.objects.TRLimit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TRCommandTR implements CommandExecutor {
	private Send send;
	public TRCommandTR(){
		send = new Send();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		send.sender = sender;
		
		String[] largs = args.clone();
		
		for (int i = 0; i < largs.length; i++){
			largs[i] = largs[i].toLowerCase();
		}
		
		if (largs.length == 0 || largs[0].equals("help")) {
			help();
			return true;
		}
		
		if (largs[0].equals("warnings")){
			warnings(largs);
			return true;
		}
		
		if (largs[0].equals("about")){
			about();
			return true;
		}
		
		if (largs[0].equals("emc")) {
			emcMain(largs);
			return true;
		}
		
		if (largs[0].equals("admin")) {
			adminMain(largs);
			return true;
		}
		
		if (largs[0].equals("debug") && !(send.sender instanceof Player)){
			debugInfo();
			return true;
		}
		
		send.msg(ChatColor.RED + "Unkown subcommand /tr " + largs[0] + "!");
		help();
		return true;
	}

	private void help(){
		send.msg(ChatColor.YELLOW + "[TekkitRestrict v" + tekkitrestrict.version + " Commands]");
		send.msg("Aliases: /tr, /tekkitrestrict");
		if (send.sender.hasPermission("tekkitrestrict.emc")) send.msg("/tr EMC", "List EMC commands.");
		if (send.sender.hasPermission("tekkitrestrict.admin")) send.msg("/tr admin", "list admin commands");
		send.msg("/tr about", "List information about the version and authors of TekkitRestrict");
	}
	private void warnings(String largs[]){
		if (send.noPerm("warnings")) return;
		
		
		if (largs.length == 1 || largs.length > 2){
			send.msg("/tr warnings load", "display warnings during loading");
			send.msg("/tr warnings config", "display config warnings");
			send.msg("/tr warnings other", "display other warnings");
			send.msg("/tr warnings all", "display all warnings");
			return;
		}
		
		if (largs[1].equals("load")){
			ArrayList<String> msgs = Warning.loadWarnings;
			if (msgs.isEmpty()){
				send.msg("There were no warnings during load.");
				return;
			}
			
			for (String str : msgs){
				send.msg(str);
			}
			return;
		}
		
		if (largs[1].equals("config")){
			ArrayList<String> msgs = Warning.configWarnings;
			if (msgs.isEmpty()){
				send.msg("There were no config warnings.");
				return;
			}
			
			for (String str : msgs){
				send.msg(str);
			}
			return;
		}
		
		if (largs[1].equals("other")){
			ArrayList<String> msgs = Warning.otherWarnings;
			if (msgs.isEmpty()){
				send.msg("There were no other warnings.");
				return;
			}
			
			for (String str : msgs){
				send.msg(str);
			}
			return;
		}
	}
	private void about(){
		send.msg("[TekkitRestrict About]");
		send.msg("Original author and creator: DreadSlicer/EterniaLogic");
		send.msg("Current author: Taeir");
		if (!send.sender.hasPermission("tekkitrestrict.admin")) return;
		send.msg("");
		send.msg("Version: " + tekkitrestrict.getFullVersion());
		send.msg("Database version: " + tekkitrestrict.dbversion);

		switch (tekkitrestrict.dbworking){
			case 0: send.msg("DB working: " + ChatColor.GREEN + "yes"); break;
			case 2:	send.msg("DB working: " + ChatColor.RED + "partially; Safezones will not be saved."); break;
			case 4:	send.msg("DB working: " + ChatColor.RED + "partially; Limits will not be saved."); break;
			case 20: send.msg("DB working: " + ChatColor.RED + "no; Unable to read database file.");
			default: send.msg("DB working: " + ChatColor.RED + "no; Database will reset upon next startup."); break;
		}
	}
	private void debugInfo(){
		if (send.sender instanceof Player){
			send.msg(ChatColor.RED + "Only the console can execute this command!");
			return;
		}
		
		String output = "";
		
		ArrayList<String> noitem = TRNoItem.getDebugInfo();
		for (String s : noitem){
			output += s+";";
		}
		ArrayList<String> limiter = TRLimiter.getDebugInfo();
		for (String s : limiter){
			output += s+";";
		}
		
		send.msg(output);
	}
	
	private void emcMain(String largs[]){
		if (send.noPerm("emc")) return;
		
		if (!tekkitrestrict.EEEnabled){
			send.msg(ChatColor.RED + "EquivalentExchange is not enabled!");
			return;
		}
		
		if (largs.length == 1 || largs[1].equals("help")) {
			emcHelp();
			return;
		}
		
		if (largs[1].equals("tempset")) {
			emcTempSet(largs);
			return;
		}
		
		if (largs[1].equals("lookup")) {
			emcLookup(largs);
			return;
		}
		
		send.msg(ChatColor.RED + "Unknown subcommand /tr emc " + largs[1] + "!");
		emcHelp();
		
	}
	private void emcHelp(){
		send.msg(ChatColor.YELLOW + "[TekkitRestrict v" + tekkitrestrict.version + " EMC Commands]");
		send.msg("/tr emc tempset <id[:data]> <EMC>", "Set an emc value till the next restart.");
		send.msg("/tr emc lookup <id[:data]>", "Check the emc value of an item.");
	}
	private void emcTempSet(String largs[]){
		if (send.noPerm("emc.tempset")) return;
		
		if (largs.length != 4){
			send.msg(ChatColor.RED + "Incorrect syntaxis!");
			send.msg(ChatColor.RED + "Correct usage: /tr emc tempset <id:data> <EMC>");
			return;
		}
		
		int emc = 0;
		try {
			emc = Integer.parseInt(largs[3]);
			if (emc < 0){
				send.msg(ChatColor.RED + "Negative values are not allowed!");
				return;
			}
		} catch (NumberFormatException ex){
			send.msg(ChatColor.RED + "This is not a valid number!");
			return;
		}
		
		try {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(largs[2]);
			} catch (TRException ex) {
				send.msg(ChatColor.RED + "Invalid item string:");
				send.msg(ChatColor.RED + ex.getMessage());
				return;
			}
			for (TRItem isr : iss) {
				int data = isr.data == -1 || isr.data == 0 ? 0 : isr.data;
				if (emc > 0) ee.EEMaps.addEMC(isr.id, data, emc);
				else {
					//Remove EMC value.
					HashMap<Integer, Integer> hm = (HashMap<Integer, Integer>) ee.EEMaps.alchemicalValues.get(isr.id);
					if (hm != null){
						hm.remove(data);
						if (hm.isEmpty()) ee.EEMaps.alchemicalValues.remove(isr.id);
						else ee.EEMaps.alchemicalValues.put(isr.id, hm);
					}
				}
				send.msg(ChatColor.GREEN + "Temporary set " + isr.id + ":" + data + " to " + emc + " EMC.");
			}
		} catch (Exception ex){
			Log.debugEx(ex);
			send.msg(ChatColor.RED + "Incorrect syntaxis!");
			send.msg(ChatColor.RED + "Correct usage:");
			send.msg(ChatColor.RED + "/tr emc tempset <id[:data]> <EMC>");
			send.msg(ChatColor.RED + "/tr emc tempset <id-id2> <EMC>");
		}
	}
	private void emcLookup(String largs[]){
		if (send.noPerm("emc.lookup")) return;
		
		if (largs.length != 3){
			send.msg(ChatColor.RED + "Incorrect syntaxis!");
			send.msg(ChatColor.RED + "Correct usage: /tr emc lookup <id[:data]>");
			return;
		}
		
		boolean found = false;
		
		try {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(largs[2]);
			} catch (TRException ex) {
				send.msg(ChatColor.RED + "Invalid item string:");
				send.msg(ChatColor.RED + ex.getMessage());
				return;
			}
			for (TRItem isr : iss) {
				HashMap<Integer, Integer> hm = (HashMap<Integer, Integer>) ee.EEMaps.alchemicalValues.get(isr.id);
				if (hm == null) continue;
				
				if (isr.data == -1) { //Get all data values
					Iterator<Integer> ks = hm.keySet().iterator();//Every data value
					while (ks.hasNext()) {
						Integer dat = ks.next();
						Integer emc = hm.get(dat);
						if (emc == null) continue; //Should never happen.
						found = true;
						send.msg("[" + isr.id + ":" + dat + "] EMC: " + emc);
					}
				} else {
					int datax = isr.data == -10 ? 0 : isr.data;
					Integer emc = hm.get(datax);
					if (emc == null) continue;
					found = true;
					
					send.msg("[" + isr.id + ":" + datax + "] EMC: " + emc);
				}
			}
			
			if (!found){
				send.msg(ChatColor.RED + "No EMC values found for " + largs[2] + ".");
			}
		} catch (Exception ex) {
			Log.debugEx(ex);
			send.msg(ChatColor.RED + "Sorry, EMC lookup unsuccessful...");
			send.msg("/tr emc lookup <id[:data]>");
			send.msg("/tr emc lookup <id-id2>");
		}
	}
	
	private void adminMain(String largs[]){
		if (send.noPerm("admin")) return;

		if (largs.length == 1) {
			adminHelp(1);
			return;
		}
		
		if (largs[1].equals("2")) {
			adminHelp(2);
			return;
		}
		
		if (largs[1].equals("help")) {
			int page = 1;
			if (largs.length == 3){
				try { page = Integer.parseInt(largs[2]); } catch (NumberFormatException ex) {}
			}
			adminHelp(page);
			return;
		}
		
		if (largs[1].equals("reload")) {
			adminReload(largs);
			return;
		}
		
		if (largs[1].equals("update")) {
			adminUpdate(largs);
			return;
		}
		
		if (largs[1].equals("threadlag")) {
			adminThreadLag();
			return;
		}
		
		if (largs[1].equals("reinit")) {
			adminReinit();
			return;
		}
		
		if (largs[1].equals("safezone")) {
			try {
				ssMain(largs);
			} catch (Exception ex) {
				send.msg(ChatColor.RED + "An error has occurred processing your command!");
				Warning.other("Error occurred in /tr admin safezone! Please inform the author.");
				Log.Exception(ex, false);
			}
			return;
		}
		
		if (largs[1].equals("limit")) {
			try {
				limitMain(largs);
			} catch (Exception ex) {
				send.msg(ChatColor.RED + "An error has occurred processing your command!");
				Warning.other("Error occurred in /tr admin limit! Please inform the author.");
				Log.Exception(ex, false);
			}
			return;
		}

		send.msg(ChatColor.RED + "Unknown subcommand /tr admin " + largs[1] + "!");
		send.msg("Use /tr admin help to see all subcommands.");
	}
	private void adminReload(String largs[]){
		if (send.noPerm("admin.reload")) return;
		
		if (largs.length == 3){
			tekkitrestrict.getInstance().saveDefaultConfig(false);
			tekkitrestrict.getInstance().reloadConfig();
			if (largs[2].equals("limiter")){
				TRLimiter.reload();
				send.msg("Limiter Reloaded!");
			} else if (largs[2].equals("noitem")){
				TRNoItem.reload();
				send.msg("NoItem (Banned Items) Reloaded!");
			} else if (largs[2].equals("creative") || largs[2].equals("limitedcreative")){
				TRNoItem.reload();
				send.msg("Limited Creative Banned Items Reloaded!");
			} else if (largs[2].equals("noclick")){
				TRNoClick.reload();
				send.msg("NoClick (disabled interactions) Reloaded!");
			} else if (largs[2].equals("Logger") || largs[2].equals("logfilter") || largs[2].equals("logsplitter")){
				LogFilter.replaceList = tekkitrestrict.config.getStringList(ConfigFile.Logging, "LogFilter");
				LogFilter.splitLogs = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "SplitLogs", true);
				LogFilter.filterLogs = tekkitrestrict.config.getBoolean(ConfigFile.Logging, "FilterLogs", true);
				LogFilter.logLocation = tekkitrestrict.config.getString(ConfigFile.Logging, "SplitLogsLocation", "log");
				LogFilter.fileFormat = tekkitrestrict.config.getString(ConfigFile.Logging, "FilenameFormat", "{TYPE}-{DAY}-{MONTH}-{YEAR}.log");
				LogFilter.logFormat = tekkitrestrict.config.getString(ConfigFile.Logging, "LogStringFormat", "[{HOUR}:{MINUTE}:{SECOND}] {INFO}");
				TRLogger.reload();
				send.msg("Log Filter/Splitter Reloaded!");
			} else if (largs[2].equals("emcset")){
				TREMCSet.reload();
				send.msg("EMC setter Reloaded!");
			} else {
				if (largs[2].equals("help"))
					send.msg(ChatColor.YELLOW + "Possible subcommands: ");
				else
					send.msg(ChatColor.RED + "Unknown subcommand! Possible subcommands: ");
				
				send.msg(ChatColor.YELLOW + "Limiter, Noitem, LimitedCreative, NoClick, Logger and EMCSet");
			}
			return;
		}

		tekkitrestrict.getInstance().reload(true, false);
		send.msg("Tekkit Restrict Reloaded!");
		return;
	}
	private void adminUpdate(String largs[]){
		if (send.noPerm("admin.update")) return;
		
		if (tekkitrestrict.updater == null){
			send.msg(ChatColor.RED + "The update check is disabled in the config.");
			return;
		}
		
		boolean check = false;
		if (largs.length == 3){
			if (largs[2].equals("check")) check = true;
			else if (largs[2].equals("download")) check = false;
			else {
				send.msg(ChatColor.RED + "Invalid argument: \""+largs[2]+"\"! Only \"check\" and \"download\" are allowed.");
				return;
			}
		} else {
			send.msg(ChatColor.RED + "Invalid syntaxis! Correct usage: /tr admin update <check|download>");
			return;
		}
		
		UpdateResult result = tekkitrestrict.updater.getResult();
		if (result == UpdateResult.SUCCESS){
			send.msg(ChatColor.GREEN + "The update TekkitRestrict " + ChatColor.YELLOW + tekkitrestrict.updater.shortVersion + ChatColor.GREEN + " is available, and has already been downloaded.");
			send.msg(ChatColor.GREEN + "This update will be installed on the next server start.");
		} else if (result == UpdateResult.UPDATE_AVAILABLE){
			if (check){
				send.msg(ChatColor.GREEN + "The update TekkitRestrict " + ChatColor.YELLOW + tekkitrestrict.updater.shortVersion + ChatColor.GREEN + " is available.");
				send.msg(ChatColor.YELLOW + "Use /tr admin update download to start downloading.");
			} else {
				send.msg(ChatColor.GREEN + "TekkitRestrict will now start downloading version " + ChatColor.YELLOW + tekkitrestrict.updater.shortVersion + ChatColor.GREEN + ".");
				tekkitrestrict.getInstance().Update();
			}
		} else if (result == UpdateResult.NO_UPDATE){
			send.msg(ChatColor.YELLOW + "There is no update available for TekkitRestrict.");
		} else {
			send.msg(ChatColor.RED + "An error occurred when trying to check for a new version.");
		}
	}
	private void adminThreadLag(){
		if (send.noPerm("admin.threadlag")) return;
		
		TRPerformance.getThreadLag(send.sender);
	}
	private void adminReinit(){
		if ((send.sender instanceof Player) && send.noPerm("admin.reinit")) return;
		//Console should always be able to use this, even though permission default is false in plugin.yml
		
		send.msg(ChatColor.RED + "Reinitializing server.");
		tekkitrestrict.getInstance().getServer().reload();
	}
	private void adminHelp(int page) {
		if (page == 1) {
			send.msg(ChatColor.YELLOW + "[TekkitRestrict " + tekkitrestrict.version + " Admin Commands] Page 1 / 3");
			send.msg("/tr admin help <page>", "Show this help.");
			send.msg("/tr admin reload", "Reload TekkitRestrict");
			send.msg("/tr admin reload help", "View Reload subcommands");
			send.msg("/tr admin update check", "Check for updates.");
			send.msg("/tr admin update download", "Download an update if it is available.");
			send.msg("/tr admin threadlag", "Display threadlag information.");
		} else if (page == 2) {
			send.msg(ChatColor.YELLOW + "[TekkitRestrict " + tekkitrestrict.version + " Admin Commands] Page 2 / 3");
			send.msg("/tr admin safezone list [page]", "List safezones.");
			send.msg("/tr admin safezone check [player]", "Check if a player is in a safezone.");
			send.msg("/tr admin safezone addwg <region>", "Add a safezone using WorldGuard.");
			send.msg("/tr admin safezone addgp <name>", "Add a safezone using GriefPrevention.");
			send.msg("/tr admin safezone rem <name>", "Remove a safezone.");
			send.msg(ChatColor.STRIKETHROUGH + "/tr admin reinit", ChatColor.STRIKETHROUGH + "Reload the server.");
		} else if (page >= 3){
			send.msg(ChatColor.YELLOW + "[TekkitRestrict " + tekkitrestrict.version + " Admin Commands] Page 3 / 3");
			send.msg("/tr admin limit clear <player>", "Clear a players limits.");
			send.msg("/tr admin limit clear <player> <id[:data]>", "Clear a players limits for a specific item.");
			send.msg("/tr admin limit list <player> [id]", "List a players limits.");
		} else {
			send.msg(ChatColor.RED + "Page " + page + " doesn't exist. ");
		}
	}
	
	private void ssMain(String[] largs){
		if (send.noPerm("admin.safezone")) return;
		
		if (largs.length == 2 || largs[2].equals("help")){
			ssHelp();
			return;
		}
		
		if (largs[2].equals("list")){
			ssList(largs);
			return;
		}
		
		if (largs[2].equals("check")){
			ssCheck(largs);
			return;
		}
		
		if (largs[2].equals("rem") || largs[2].equals("del") || largs[2].equals("remove") || largs[2].equals("delete")){
			ssDel(largs);
			return;
		}
		
		if (largs[2].equals("addwg")){
			ssAddWG(largs);
			return;
		}
		
		if (largs[2].equals("addgp")){
			ssAddGP(largs);
			return;
		}
		
		send.msg(ChatColor.RED + "Unknown subcommand /tr admin safezone " + largs[2] + "!");
		ssHelp();
	}
	private void ssHelp(){
		send.msg(ChatColor.YELLOW + "[TekkitRestrict v" + tekkitrestrict.version + " Safezone Commands]");
		send.msg("/tr admin safezone check [player]", "Check if a player is in a safezone.");
		send.msg("/tr admin safezone list [page]", "List safezones");
		send.msg("/tr admin safezone addwg <name>", "Add a WorldGuard safezone");
		send.msg("/tr admin safezone addgp <name>", "Add a GriefPrevention safezone");
		send.msg("/tr admin safezone del <name>", "remove safezone");
	}
	private void ssList(String[] largs){
		if (send.noPerm("admin.safezone.list")) return;
		
		int requestedPage = 1;
		if (largs.length == 4){
			try {
				requestedPage = Integer.parseInt(largs[3]);
			} catch (NumberFormatException ex){
				send.msg(ChatColor.RED + "Page number incorrect!");
				return;
			}
		}
		int totalPages = TRSafeZone.zones.size() / 5;
		if (totalPages == 0) totalPages = 1;
		if (requestedPage > totalPages) requestedPage = totalPages;
		
		send.msg(ChatColor.YELLOW + "SafeZones - Page " + requestedPage + " of " + totalPages);
		for (int i = ((requestedPage-1) * 5); i < (requestedPage * 5); i++){
			if (TRSafeZone.zones.size() <= i) break;
			
			TRSafeZone z = TRSafeZone.zones.get(i);
			if (z == null) continue;
			
			String pl = "";
			if (z.mode == 1) pl = "[WG] ";
			else if (z.mode == 4) pl = "[GP] ";
			else if (z.mode == 0) pl = "[TR] ";
			else if (z.mode == 2) pl = "[PS] ";
			else if (z.mode == 3) pl = "[F] ";
			
			send.msg("" + ChatColor.YELLOW + (i+1) + ": " + pl + z.name + " - Loc: [" + z.world + "] [" + z.x1 + " " + z.y1 + " " + z.z1 + "] - [" + z.x2 + " " + z.y2 + " " + z.z2 + "]");
		}
	}
	private void ssCheck(String[] largs){
		Player target;
		if (largs.length == 4){
			if (send.noPerm("admin.safezone.check.others")) return;
			target = Bukkit.getPlayer(largs[3]);
			if (target == null){
				send.msg(ChatColor.RED + "Cannot find player " + largs[3] + "!");
				return;
			}
		} else {
			if (send.noPerm("admin.safezone.check")) return;
			if (send.sender instanceof Player){
				target = (Player) send.sender;
			} else {
				send.msg(ChatColor.RED + "The console can only use /tr admin safezone check <player>");
				return;
			}
		}
		
		Object[] temp = TRSafeZone.getSafeZoneStatusFor(target);
		SafeZone status = (SafeZone) temp[2];
		
		if (status == SafeZone.isNone || status == SafeZone.pluginDisabled){
			send.msg(ChatColor.YELLOW + target.getName() + " is currently " + ChatColor.RED + "not" + ChatColor.YELLOW + " in a safezone.");
			return;
		}
		
		String plugin = (String) temp[0];
		String zone = (String) temp[1];
		
		if (zone.equals("")) zone = "NO_NAME";
		if (status == SafeZone.isAllowedStrict) {
			send.msg(ChatColor.YELLOW + target.getName() + " is currently in the "+plugin+" safezone " + zone + ", but it doesn't apply to him/her.");
			return;
		}
		
		if (status == SafeZone.isAllowedNonStrict) {
			send.msg(ChatColor.YELLOW + target.getName() + " is currently in the "+plugin+" safezone " + zone + ".");
			return;
		}
		
		if (status == SafeZone.isDisallowed) {
			send.msg(ChatColor.YELLOW + target.getName() + " is currently in the "+plugin+" safezone " + zone + ".");
			return;
		}
		
		if (status == SafeZone.hasBypass) {
			send.msg(ChatColor.YELLOW + target.getName() + " is currently in the "+plugin+" safezone " + zone + ", but it doesn't apply to him/her.");
			return;
		}
	}
	private void ssDel(String[] largs){
		if (send.noPerm("admin.safezone.remove")) return;
		
		if (largs.length != 4){
			send.msg(ChatColor.RED + "Incorrect syntaxis!");
			send.msg(ChatColor.RED + "Correct usage: /tr admin safezone rem <name>");
			return;
		}
		
		boolean found = false;
		String name = null;
		TRSafeZone zone = null;
		for (int i = 0; i < TRSafeZone.zones.size(); i++) {
			zone = TRSafeZone.zones.get(i);
			name = zone.name;
			if (!name.toLowerCase().equals(largs[3])) continue;
			found = true;
		}
		
		if (found){
			if (!TRSafeZone.removeSafeZone(zone)){
				send.msg(ChatColor.RED + "Unable to remove the safezone. Was the claim already removed?");
				return;
			}
			
			send.msg(ChatColor.GREEN + "Safezone " + name + " removed.");
		} else {
			send.msg(ChatColor.RED + "Cannot find safezone " + largs[3] + "!");
			return;
		}
		
		//FIXME uppercase problem
		try {
			tekkitrestrict.db.query("DELETE FROM `tr_saferegion` WHERE `name` = '"
							+ TRDB.antisqlinject((name == null ? largs[3] : name)) + "' COLLATE NOCASE");
		} catch (SQLException ex) {}
	}
	private void ssAddWG(String[] largs){
		if (send.noConsole()) return;
		
		if (send.noPerm("admin.safezone.addwg")) return;
		if (largs.length != 4){
			send.msg(ChatColor.RED + "Incorrect syntaxis!");
			send.msg(ChatColor.RED + "Correct usage: /tr admin safezone addwg <name>");
			return;
		}
		
		String name = largs[3];
		Player player = (Player) send.sender;

		for (TRSafeZone current : TRSafeZone.zones){
			if (current.world.equalsIgnoreCase(player.getWorld().getName())){
				if (current.name.toLowerCase().equals(name)){
					send.msg(ChatColor.RED + "There is already a safezone by this name!");
					return;
				}
			}
		}
		PluginManager pm = Bukkit.getPluginManager();
		if (!pm.isPluginEnabled("WorldGuard")){
			send.msg(ChatColor.RED + "WorldGuard is not enabled!");
			return;
		}
		
		try {
			WorldGuardPlugin wg = (WorldGuardPlugin) pm.getPlugin("WorldGuard");
			Map<String, ProtectedRegion> rm = wg.getRegionManager(player.getWorld()).getRegions();
			ProtectedRegion pr = rm.get(name);
			if (pr != null) {
				TRSafeZone zone = new TRSafeZone();
				zone.mode = 1;
				zone.name = name;
				zone.world = player.getWorld().getName();
				zone.x1 = pr.getMinimumPoint().getBlockX();
				zone.x2 = pr.getMaximumPoint().getBlockX();
				zone.y1 = pr.getMinimumPoint().getBlockY();
				zone.y2 = pr.getMaximumPoint().getBlockY();
				zone.z1 = pr.getMinimumPoint().getBlockZ();
				zone.z2 = pr.getMaximumPoint().getBlockZ();
				zone.locSet = true;
				TRSafeZone.zones.add(zone);
				TRSafeZone.save();
				send.msg(ChatColor.GREEN + "Attached to region \"" + name + "\"!");
			} else {
				send.msg(ChatColor.RED + "Region does not exist!");
				
				String allregions = "";
				for (String current : rm.keySet()) allregions += current + ", ";
				
				if (allregions.length()==0) allregions = "There are no regions!";
				else allregions = allregions.substring(0, allregions.length()-2);
				
				send.msg(ChatColor.YELLOW + "Possible regions: " + allregions);
				return;
			}
		} catch (Exception E) {
			send.msg(ChatColor.RED + "Error! (does the region exist?)");
		}
	}
	private void ssAddGP(String[] largs){
		if (send.noConsole()) return;
		
		if (send.noPerm("admin.safezone.addgp")) return;
		
		if (largs.length != 4){
			send.msg(ChatColor.RED + "Incorrect syntaxis!");
			send.msg(ChatColor.RED + "Correct usage: /tr admin safezone addgp <name>");
			return;
		}
		
		Player player = (Player) send.sender;
		String name = largs[3];

		for (TRSafeZone current : TRSafeZone.zones){
			if (current.world.equalsIgnoreCase(player.getWorld().getName())){
				if (current.name.toLowerCase().equals(name)){
					send.msg(ChatColor.RED + "There is already a safezone by this name!");
					return;
				}
			}
		}
		PluginManager pm = Bukkit.getPluginManager();
		if (!pm.isPluginEnabled("GriefPrevention")){
			send.msg(ChatColor.RED + "GriefPrevention is not enabled!");
			return;
		}
		
		SafeZoneCreate response = TRSafeZone.addSafeZone(player, "griefprevention", name);
		if (response == SafeZoneCreate.AlreadyExists)
			send.msg(ChatColor.RED + "This region is already a safezone!");
		else if (response == SafeZoneCreate.RegionNotFound)
			send.msg(ChatColor.RED + "There is no region at your current position!");
		else if (response == SafeZoneCreate.PluginNotFound)
			send.msg(ChatColor.RED + "Safezones are disabled for GriefPrevention claims.");
		else if (response == SafeZoneCreate.NoPermission)
			send.msg(ChatColor.RED + "You either have no permission to modify this claim or you are not allowed to make safezones.");
		else if (response == SafeZoneCreate.Success)
			send.msg(ChatColor.GREEN + "This claim is now a safezone!");
		else 
			send.msg(ChatColor.RED + "An undefined error occurred!");

	}
	
	private void limitMain(String[] largs){
		if (send.noPerm("admin.limit")) return;
		
		if (largs.length == 2 || largs[2].equals("help")) {
			limitHelp();
			return;
		}
		
		if (largs[2].equals("clear")) {
			limitClear(largs);
			return;
		}
		
		if (largs[2].equals("list")) {
			limitList(largs);
			return;
		}
		
		send.msg(ChatColor.RED + "Unknown subcommand /tr admin limit " + largs[2] + "!");
		limitHelp();
	}
	private void limitHelp(){
		send.msg(ChatColor.YELLOW + "[TekkitRestrict v" + tekkitrestrict.version + " Limit Commands]");
		send.msg("/tr admin limit list <player>", "View a players limits");
		send.msg("/tr admin limit list <player> <id>", "View a specific limit.");
		send.msg("/tr admin limit clear <player>", "Clear a players limits.");
		send.msg("/tr admin limit clear <player> <id>[:data]", "Clear a players limits for a specific itemid.");
	}
	private void limitClear(String[] largs){
		if (send.noPerm("admin.limit.clear")) return;
		
		if (largs.length == 3 || largs.length > 5){
			send.msg(ChatColor.RED + "Invalid syntaxis!");
			send.msg(ChatColor.RED + "Correct usage: /tr admin limit clear <player> [id[:data]]");
			return;
		}
		
		TRLimiter cc = TRLimiter.getLimiter(largs[3]);
		if (cc == null){
			send.msg(ChatColor.RED + "This player doesn't exist!");
			return;
		}
		
		if (largs.length == 4){
			cc.clearLimits();
			send.msg(ChatColor.GREEN + "Cleared " + cc.player + "'s block limits!");
			return;
		}
		
		try {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(largs[4]);
			} catch (TRException ex) {
				send.msg(ChatColor.RED + "Invalid item string:");
				send.msg(ChatColor.RED + ex.getMessage());
				return;
			}
			
			for (TRItem isr : iss) {
				for (TRLimit trl : cc.itemlimits) {
					//tekkitrestrict.log.info(isr.id+":"+isr.getData()+" ?= "+trl.blockID+":"+trl.blockData);
					if (TRNoItem.equalSet(isr.id, isr.data, trl.id, trl.data)) {
						if(trl.placedBlock != null)
							trl.placedBlock.clear();
						else
							trl.placedBlock = new LinkedList<Location>();
					}
					int ci = cc.itemlimits.indexOf(trl);
					if(ci != -1) cc.itemlimits.set(ci, trl);
				}
			}
			send.msg(ChatColor.GREEN + "Cleared " + cc.player + "'s block limits for "+largs[4]);
		} catch (Exception E) {
			send.msg(ChatColor.RED + "[Error!] Please use the formats:");
			send.msg("/tr admin limit clear <player> [id[:data]]", "Clear a players limits.");
		}
	}
	private void limitList(String[] largs){
		if (send.noPerm("admin.limit.list")) return;
		
		if (largs.length < 4 || largs.length > 5){
			send.msg(ChatColor.RED + "Incorrect syntaxis!");
			send.msg(ChatColor.RED + "Correct usage: /tr admin limit list <player> [id]");
			return;
		}
		
		TRLimiter cc = TRLimiter.getLimiter(largs[3]);
		
		if(cc.itemlimits.isEmpty()){
			send.msg(ChatColor.RED + largs[3] + " doesn't have any limits!");
			return;
		}
		
		Player target = Bukkit.getPlayer(cc.player);
		
		if (largs.length == 5){
			int id;
			try {
				id = Integer.parseInt(largs[4]);
			} catch (NumberFormatException ex){
				send.msg(ChatColor.RED + "You didn't specify a valid number!");
				return;
			}
			
			if (target != null){
				for (TRLimit l : cc.itemlimits) {
					if (l.id != id) continue;
					int cccl = cc.getMax(target, l.id, l.data);
					cccl = cccl == -1 ? 0 : cccl;
					send.msg("[" + l.id + ":" + l.data + "] - " + l.placedBlock.size() + "/" + cccl + " blocks");
				}
			} else {
				send.msg("As "+cc.player+" is offline, his/her max limits cannot be listed.");
				for (TRLimit l : cc.itemlimits) {
					if (l.id != id) continue;
					send.msg("[" + l.id + ":" + l.data + "] - " + l.placedBlock.size()+"/? blocks");
				}
			}
			
		} else {
			if (target != null){
				for (TRLimit l : cc.itemlimits) {
					int cccl = cc.getMax(target, l.id, l.data);
					cccl = cccl == -1 ? 0 : cccl;
					send.msg("[" + l.id + ":" + l.data + "] - " + l.placedBlock.size()+"/"+cccl+" blocks");
				}
			} else {
				send.msg("As "+cc.player+" is offline, his/her max limits cannot be listed.");
				for (TRLimit l : cc.itemlimits) {
					send.msg("[" + l.id + ":" + l.data + "] - " + l.placedBlock.size()+"/? blocks");
				}
			}
		}
	}
}
