package com.github.dreadslicer.tekkitrestrict.commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.h31ix.updater.Updater.UpdateResult;

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
import com.github.dreadslicer.tekkitrestrict.TRCacheItem;
import com.github.dreadslicer.tekkitrestrict.TRLimit;
import com.github.dreadslicer.tekkitrestrict.TRLimitBlock;
import com.github.dreadslicer.tekkitrestrict.TRLogger;
import com.github.dreadslicer.tekkitrestrict.TRNoItem;
import com.github.dreadslicer.tekkitrestrict.TRPerformance;
import com.github.dreadslicer.tekkitrestrict.TRSafeZone;
import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;
import com.github.dreadslicer.tekkitrestrict.api.SafeZones.SafeZoneCreate;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TRCommandTR implements CommandExecutor {
	private Send send;
	public TRCommandTR(){
		send = new Send();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().toLowerCase().equals("tekkitrestrict")) {
			Log.Debug("Please inform the developer that onCommand is acting strangely.");
			return true;
		}
		
		send.sender = sender;
		
		for (int i = 0; i < args.length; i++){
			args[i] = args[i].toLowerCase();
		}
		
		if (args.length == 0 || args[0].equals("help")) {
			send.msg("[TekkitRestrict " + tekkitrestrict.version + " Commands]");
			send.msg("Aliases: /tr, /tekkitrestrict");
			if (sender.hasPermission("tekkitrestrict.emc")) send.msg("/tr EMC", "List EMC commands.");
			if (sender.hasPermission("tekkitrestrict.admin")) send.msg("/tr admin", "list admin commands");
			return true;
		}
		
		if (args[0].equals("emc")) {
			if (send.noPerm("emc")) return true;
			
			if (args.length == 1 || args[1].equals("help")) {
				send.msg("[TekkitRestrict v " + tekkitrestrict.version + " EMC Commands]");
				send.msg("/tr emc tempset <id[:data]> <EMC>", "Set an emc value till the next restart.");
				send.msg("/tr emc lookup <id[:data]>", "Check the emc value of an item.");
				return true;
			}
			
			if (args[1].equals("tempset")) {
				if (send.noPerm("emc.tempset")) return true;
				
				if (args.length != 4){
					send.msg(ChatColor.RED + "Incorrect syntaxis!");
					send.msg(ChatColor.RED + "Correct usage: /tr emc tempset <id:data> <EMC>");
					return true;
				}
				
				int emc = 0;
				try {
					emc = Integer.parseInt(args[3]);
					if (emc < 0){
						send.msg(ChatColor.RED + "Negative values are not allowed!");
						return true;
					}
				} catch (NumberFormatException ex){
					send.msg(ChatColor.RED + "This is not a valid number!");
					return true;
				}
				
				try {
					List<TRCacheItem> iss = TRCacheItem.processItemString("", args[2], -1);
					for (TRCacheItem isr : iss) {
						int data = isr.getData();
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
						send.msg(ChatColor.GREEN + "Temporary EMC set successful!");
					}
				} catch (Exception ex){
					send.msg(ChatColor.RED + "Incorrect syntaxis!");
					send.msg(ChatColor.RED + "Correct usage:");
					send.msg(ChatColor.RED + "/tr emc tempset <id[:data]> <EMC>");
					send.msg(ChatColor.RED + "/tr emc tempset <id-id2> <EMC>");
				}
				
				return true;
			}
			
			if (args[1].equals("lookup")) {
				if (send.noPerm("emc.lookup")) return true;
				
				if (args.length != 3){
					send.msg(ChatColor.RED + "Incorrect syntaxis!");
					send.msg(ChatColor.RED + "Correct usage: /tr emc lookup <id[:data]>");
					return true;
				}
				
				boolean found = false;
				try {
					List<TRCacheItem> iss = TRCacheItem.processItemString("", args[2], -1);
					for (TRCacheItem isr : iss) {
						HashMap<Integer, Integer> hm = (HashMap<Integer, Integer>) ee.EEMaps.alchemicalValues.get(isr.id);
						if (hm == null) continue;
						
						if (isr.data == 0) { //Get all data values
							Iterator<Integer> ks = hm.keySet().iterator();//Every data value
							while (ks.hasNext()) {
								Integer dat = ks.next();
								Integer emc = hm.get(dat);
								if (emc == null) continue; //Should never happen.
								found = true;
								send.msg("[" + isr.id + ":" + dat + "] EMC: " + emc);
							}
						} else {
							Integer emc = hm.get(isr.data);
							if (emc == null) continue;
							found = true;
							int datax = isr.data == -10 ? 0 : isr.data;
							send.msg("[" + isr.id + ":" + datax + "] EMC: " + emc);
						}
					}
					
					if (!found){
						send.msg(ChatColor.RED + "No EMC values found for " + args[2] + ".");
					}
				} catch (Exception ex) {
					//Should never happen.
					TRLogger.Log("debug", "[COM] EMCLookup " + ex.getMessage());
					send.msg(ChatColor.RED + "Sorry, EMC lookup unsuccessful...");
					send.msg("/tr emc lookup <id[:data]>");
					send.msg("/tr emc lookup <id-id2>");
				}
				return true;
			}
		}
		
		try {
			if (args[0].equals("admin")) {
				if (send.noPerm("admin")) return true;

				if (args.length == 1) {
					sendHelp(1);
					return true;
				}
				
				if (args[1].equals("help")) {
					int page = 1;
					if (args.length == 3){
						try { page = Integer.parseInt(args[2]); } catch (NumberFormatException ex) {}
					}
					sendHelp(page);
					return true;
				}
				
				if (args[1].equals("reload")) {
					if (send.noPerm("admin.reload")) return true;

					tekkitrestrict.getInstance().reload();
					send.msg("Tekkit Restrict Reloaded!");
					return true;
				}
				
				if (args[1].equals("update")) {
					if (send.noPerm("admin.update")) return true;
					
					if (tekkitrestrict.updater == null){
						send.msg(ChatColor.RED + "The update check is disabled in the config.");
						return true;
					}
					
					UpdateResult result = tekkitrestrict.updater.getResult();
					if (result == UpdateResult.SUCCESS){
						send.msg(ChatColor.GREEN + "TekkitRestrict will update on the next server start.");
						return true;
					} else if (result == UpdateResult.UPDATE_AVAILABLE){
						send.msg(ChatColor.GREEN + "TekkitRestrict will now start downloading version " + tekkitrestrict.updater.getLatestVersionString() + ".");
						tekkitrestrict.getInstance().Update();
						return true;
					} else if (result == UpdateResult.NO_UPDATE){
						send.msg(ChatColor.YELLOW + "There is no update available for TekkitRestrict.");
						return true;
					} else {
						send.msg(ChatColor.RED + "An error occured when trying to check for a new version.");
						return true;
					}
				}
				
				if (args[1].equals("threadlag")) {
					if (send.noPerm("admin.threadlag")) return true;
					
					TRPerformance.getThreadLag(sender);
					return true;
				}
				
				if (args[1].equals("reinit")) {
					if ((sender instanceof Player) && send.noPerm("admin.reinit")) return true;
					//Console should always be able to use this, even though permission default is false in plugin.yml
					
					send.msg(ChatColor.RED + "Reinitializing server.");
					tekkitrestrict.getInstance().getServer().reload();
					return true;
				}
				
				if (args[1].equals("safezone")) {
					if (send.noPerm("admin.safezone")) return true;
					
					if (args.length == 2 || args[2].equals("help")){
						send.msg(ChatColor.YELLOW + "[TekkitRestrict v " + tekkitrestrict.version + " Safezone Commands]");
						send.msg("/tr admin safezone check [player]", "Check if a player is in a safezone.");
						send.msg("/tr admin safezone list [page]", "List safezones");
						send.msg("/tr admin safezone addwg <name>", "Add a safezone using WorldGuard");
						send.msg("/tr admin safezone addgp <name>", "Add a safezone using GriefPrevention");
						send.msg("/tr admin safezone del <name>", "remove safezone");
						return true;
					}
					
					if (eIC(args[2], "list")){
						if (send.noPerm("admin.safezone.list")) return true;
						
						int requestedPage = 1;
						if (args.length == 4){
							try {
								requestedPage = Integer.parseInt(args[3]);
							} catch (NumberFormatException ex){
								send.msg(ChatColor.RED + "Page number incorrect!");
								return true;
							}
						}
						int totalPages = TRSafeZone.zones.size() / 5;
						if (totalPages == 0) totalPages = 1;
						if (requestedPage > totalPages) requestedPage = totalPages;
						
						send.msg("SafeZones - Page " + requestedPage + " of " + totalPages);
						for (int i = ((requestedPage-1) * 5); i < (requestedPage * 5); i++){
							if (TRSafeZone.zones.size() <= i) break;
							
							TRSafeZone z = TRSafeZone.zones.get(i);
							if (z == null) continue;
							
							send.msg("[" + z.world + "] " + z.name);
							send.msg("  - Location: [" + z.x1 + " " + z.y1 + " " + z.z1 + "] - [" + z.x2 + " " + z.y2 + " " + z.z2 + "]");
						}
						return true;
					}
					
					if (eIC(args[2], "check")){
						Player target;
						if (args.length == 4){
							if (send.noPerm("admin.safezone.check.others")) return true;
							target = Bukkit.getPlayer(args[3]);
							if (target == null){
								send.msg(ChatColor.RED + "Cannot find player " + args[3] + "!");
								return true;
							}
						} else {
							if (send.noPerm("admin.safezone.check")) return true;
							if (sender instanceof Player){
								target = (Player) sender;
							} else {
								send.msg(ChatColor.RED + "The console can only use /tr admin safezone check <player>");
								return true;
							}
						}
						String name = TRSafeZone.getSafeZone(target);
						if (!name.equals(""))
							send.msg(ChatColor.BLUE + target.getName() + " is currently in the " + name + ".");
						else
							send.msg(ChatColor.BLUE + target.getName() + " is currently " + ChatColor.RED + "not" + ChatColor.BLUE + " in a safezone.");
						
						return true;
					}
					
					if (eIC(args[2], "rem", "del") || eIC(args[2], "remove", "delete")){
						if (send.noPerm("admin.safezone.remove")) return true;
						
						if (args.length != 4){
							send.msg(ChatColor.RED + "Incorrect syntaxis!");
							send.msg(ChatColor.RED + "Correct usage: /tr admin safezone rem <name>");
							return true;
						}
						
						boolean found = false;
						String name = null;
						TRSafeZone zone = null;
						for (int i = 0; i < TRSafeZone.zones.size(); i++) {
							zone = TRSafeZone.zones.get(i);
							name = zone.name;
							if (!name.toLowerCase().equals(args[3])) continue;
							found = true;
						}
						
						if (found){
							if (!TRSafeZone.removeSafeZone(zone)){
								send.msg(ChatColor.RED + "Unable to remove the safezone. Was the claim already removed?");
								return true;
							}
							
							send.msg(ChatColor.GREEN + "Safezone " + name + " removed.");
						} else {
							send.msg(ChatColor.RED + "Cannot find safezone " + args[3] + "!");
							return true;
						}
						
						//FIXME uppercase problem
						try {
							tekkitrestrict.db.query("DELETE FROM `tr_saferegion` WHERE `name` = '"
											+ tekkitrestrict.antisqlinject((name == null ? args[3] : name)) + "'");
						} catch (SQLException ex) {}
						
						return true;
					}
					
					if (eIC(args[2], "addwg")){
						if (send.noConsole()) return true;
						
						if (send.noPerm("admin.safezone.addwg")) return true;
						Player player = (Player) sender;
						String name = args[3];

						for (TRSafeZone current : TRSafeZone.zones){
							if (current.world.equalsIgnoreCase(player.getWorld().getName())){
								if (current.name.toLowerCase().equals(name)){
									send.msg(ChatColor.RED + "There is already a safezone by this name!");
									return true;
								}
							}
						}
						PluginManager pm = Bukkit.getPluginManager();
						if (!pm.isPluginEnabled("WorldGuard")){
							send.msg(ChatColor.RED + "WorldGuard is not enabled!");
							return true;
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
								TRSafeZone.zones.add(zone);
								TRSafeZone.save();
								send.msg(ChatColor.GREEN + "Attached to region `" + name + "`!");
							} else {
								send.msg(ChatColor.RED + "Region does not exist!");
								return true;
							}
						} catch (Exception E) {
							send.msg(ChatColor.RED + "Error! (does the region exist?)");
							return true;
						}
						return true;
					}
					
					if (eIC(args[2], "addgp")){
						if (send.noConsole()) return true;
						
						if (send.noPerm("admin.safezone.addgp")) return true;
						
						if (args.length != 4){
							send.msg(ChatColor.RED + "Incorrect syntaxis!");
							send.msg(ChatColor.RED + "Correct usage: /tr admin safezone addgp <name>");
							return true;
						}
						
						Player player = (Player) sender;
						String name = args[3];

						for (TRSafeZone current : TRSafeZone.zones){
							if (current.world.equalsIgnoreCase(player.getWorld().getName())){
								if (current.name.toLowerCase().equals(name)){
									send.msg(ChatColor.RED + "There is already a safezone by this name!");
									return true;
								}
							}
						}
						PluginManager pm = Bukkit.getPluginManager();
						if (!pm.isPluginEnabled("GriefPrevention")){
							send.msg(ChatColor.RED + "GriefPrevention is not enabled!");
							return true;
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
							send.msg(ChatColor.RED + "An undefined error occured!");
				
						return true;
					}
					
					send.msg(ChatColor.YELLOW + "[TekkitRestrict v " + tekkitrestrict.version + " Safezone Commands]");
					send.msg("/tr admin safezone check [player]", "Check if a player is in a safezone.");
					send.msg("/tr admin safezone list [page]", "List safezones");
					send.msg("/tr admin safezone addwg <name>", "Add a safezone using WorldGuard");
					send.msg("/tr admin safezone addgp <name>", "Add a safezone using GriefPrevention");
					send.msg("/tr admin safezone del <name>", "remove safezone");
					return true;
					
				} else if (args[1].equals("limit")) {
					if (send.noPerm("admin.limit")) return true;
					
					if (args.length == 2 || args[2].equals("help")) {
						send.msg(ChatColor.YELLOW + "[TekkitRestrict v " + tekkitrestrict.version + " Limit Commands]");
						//send("/tr admin limit clear <player>");
						//send("/tr admin limit clear <player> [INDEX[id:data]]");
						//send("/tr admin limit list [player]");
						send.msg("/tr admin limit list <player>", "View a players limits");
						send.msg("/tr admin limit list <player> <id>", "View a specific limit.");
						send.msg("/tr admin limit clear <player>", "Clear all a players limits.");
						send.msg("/tr admin limit clear <player> <id>[:data]", "Clear a players limits for a specific itemid.");
						return true;
					}
					
					if (args[2].equals("clear")) {
						if (send.noPerm("admin.limit.clear")) return true;
						if (args.length == 3 || args.length > 5){
							send.msg(ChatColor.RED + "Invalid syntaxis!");
							send.msg(ChatColor.RED + "Correct usage: /tr admin limit clear <player> [id[:data]]");
							return true;
						}
						
						TRLimitBlock cc = TRLimitBlock.getLimiter(args[3]);
						if (cc == null){
							send.msg(ChatColor.RED + "This player doesn't exist!");
							return true;
						}
						
						if (args.length == 4){
							cc.clearLimits();
							send.msg(ChatColor.GREEN + "Cleared " + cc.player + "'s block limits!");
							return true;
						}
						
						try {
							List<TRCacheItem> iss = TRCacheItem.processItemString("", args[4], -1);
							for (TRCacheItem isr : iss) {
								for (TRLimit trl : cc.itemlimits) {
									//tekkitrestrict.log.info(isr.id+":"+isr.getData()+" ?= "+trl.blockID+":"+trl.blockData);
									if (TRNoItem.equalSet(isr.id, isr.getData(), trl.blockID, trl.blockData)) {
										if(trl.placedBlock != null)
											trl.placedBlock.clear();
										else
											trl.placedBlock = new LinkedList<Location>();
									}
									int ci = cc.itemlimits.indexOf(trl);
									if(ci != -1) cc.itemlimits.set(ci, trl);
								}
							}
							send.msg(ChatColor.GREEN + "Cleared " + cc.player + "'s block limits for "+args[4]);
						} catch (Exception E) {
							send.msg(ChatColor.RED + "[Error!] Please use the formats:");
							send.msg("/tr admin limit clear <player> [id[:data]]", "Clear a players limits.");
						}
						
						return true;
						
					} else if (args[2].equals("list")) {
						if (send.noPerm("admin.limit.list")) return true;
						
						if (args.length < 4 || args.length > 5){
							send.msg(ChatColor.RED + "Incorrect syntaxis!");
							send.msg(ChatColor.RED + "Correct usage: /tr admin limit list <player> [id]");
							return true;
						}
						
						TRLimitBlock cc = TRLimitBlock.getLimiter(args[3]);
						
						if(cc.itemlimits.isEmpty()){
							send.msg(ChatColor.RED + args[3] + " doesn't have any limits!");
							return true;
						}
						
						if (args.length == 5){
							int id;
							try {
								id = Integer.parseInt(args[4]);
							} catch (NumberFormatException ex){
								send.msg(ChatColor.RED + "You didn't specify a valid number!");
								return true;
							}
							
							for (TRLimit l : cc.itemlimits) {
								if (l.blockID != id) continue;
								int cccl = cc.getMax(cc.player, l.blockID, l.blockData);
								cccl = cccl == -1 ? 0 : cccl;
								send.msg("[" + l.blockID + ":" + l.blockData + "] - " + l.placedBlock.size() + "/" + cccl + " blocks");
							}
							
						} else {
							for (TRLimit l : cc.itemlimits) {
								int cccl = cc.getMax(cc.player, l.blockID, l.blockData);
								cccl = cccl == -1 ? 0 : cccl;
								send.msg("[" + l.blockID + ":" + l.blockData + "] - " + l.placedBlock.size()+"/"+cccl+" blocks");
							}
						}
						
						return true;
					}
					
					send.msg(ChatColor.RED + "Unknown command!");
					send.msg("[TekkitRestrict v " + tekkitrestrict.version + " Limit Commands]");
					send.msg("/tr admin limit list <player>", "View a players limits");
					send.msg("/tr admin limit list <player> <id>", "View a specific limit.");
					send.msg("/tr admin limit clear <player>", "Clear a players limits.");
					send.msg("/tr admin limit clear <player> <id[:data]>", "Clear a players limits for a specific itemid.");
					return true;
					
				}
				
				
				
			} else {
				send.msg("[TekkitRestrict " + tekkitrestrict.version + " Commands]");
				send.msg("Aliases: /tr, /tekkitrestrict");
				if (sender.hasPermission("tekkitrestrict.emc")) send.msg("/tr emc", "List EMC commands.");
				if (sender.hasPermission("tekkitrestrict.admin")) send.msg("/tr admin", "list admin commands");
				return true;
			}
			
		} catch (Exception e) {
			send.msg(ChatColor.RED + "An error has occured processing your command.");
			TRLogger.Log("debug", "TRCommandTR Error: " + e.getMessage());
			for (StackTraceElement ee : e.getStackTrace()) {
				TRLogger.Log("debug", "     " + ee.toString());
			}
		}

		return true;
	}

	public void sendHelp(int page) {
		if (page == 1) {
			send.msg("[TekkitRestrict " + tekkitrestrict.version + " Admin Commands] Page 1 / 2");
			send.msg("/tr admin help <page>", "Show this help.");
			send.msg("/tr admin reload", "Reload TekkitRestrict");
			send.msg("/tr admin reinit", "Reload the server.");
			send.msg("/tr admin limit clear <player>", "Clear a players limits.");
			send.msg("/tr admin limit clear <player> <id[:data]>", "Clear a players limits for a specific item.");
			send.msg("/tr admin limit list <player> [id]", "List a players limits.");
		} else if (page == 2) {
			send.msg("[TekkitRestrict " + tekkitrestrict.version + " Admin Commands] Page 2 / 2");
			send.msg("/tr admin safezone list [page]", "List safezones.");
			send.msg("/tr admin safezone check [player]", "Check if a player is in a safezone.");
			send.msg("/tr admin safezone addwg <region>", "Add a safezone using WorldGuard.");
			send.msg("/tr admin safezone addgp <name>", "Add a safezone using GriefPrevention.");
			send.msg("/tr admin safezone rem <name>", "Remove a safezone.");
			send.msg("/tr admin threadlag", "Display threadlag information.");
		} else {
			send.msg(ChatColor.RED + "Page doesnt exist.");
		}
	}
	
	private static boolean eIC(String arg, String arg2){
		return arg.toLowerCase().equals(arg2.toLowerCase());
	}
	private static boolean eIC(String arg, String arg2, String arg3){
		arg = arg.toLowerCase();
		return arg.equals(arg2.toLowerCase()) ? true : arg.equals(arg3.toLowerCase());
	}
}
