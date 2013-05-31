package com.github.dreadslicer.tekkitrestrict.commands;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.dreadslicer.tekkitrestrict.TRCacheItem;
import com.github.dreadslicer.tekkitrestrict.TRLimitBlock;
import com.github.dreadslicer.tekkitrestrict.TRLogger;
import com.github.dreadslicer.tekkitrestrict.TRNoItem;
import com.github.dreadslicer.tekkitrestrict.TRPerformance;
import com.github.dreadslicer.tekkitrestrict.TRSafeZone;
import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;
import com.github.dreadslicer.tekkitrestrict.TRSafeZone.SafeZoneCreate;
import com.github.dreadslicer.tekkitrestrict.lib.TRLimit;

public class TRCommandTR implements CommandExecutor {

	private static CommandSender sender;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		TRCommandTR.sender = sender;
		boolean console = !(sender instanceof Player);
		for (int i = 0; i < args.length; i++){
			args[i] = args[i].toLowerCase();
		}
		
		LinkedList<String> message = new LinkedList<String>();
		boolean usemsg = true;
		if (cmd.getName().equalsIgnoreCase("tekkitrestrict")) {
			if (args.length == 0) {
				sender.sendMessage("[TekkitRestrict " + tekkitrestrict.getInstance().getDescription().getVersion() + " Commands]");
				sender.sendMessage("Aliases: /tr, /tekkitrestrict");

				if (sender.hasPermission("tekkitrestrict.admin")) {
					sender.sendMessage("/tr admin - list admin commands");
				}
				return true;
			}
			
			try {
				// following...

				if (args[0].equals("reload")) {
					if (sendNoPerm("admin.reload")) return true;

					tekkitrestrict.getInstance().reload();
					sender.sendMessage("Tekkit Restrict Reloaded!");
					
				} else if (args[0].equals("threadlag")) {
					if (sendNoPerm("admin.threadlag")) return true;
					
					TRPerformance.getThreadLag(sender);
				} else if (args[0].equals("reinit")) {
					if (sendNoPerm("admin.reinit")) return true;
					
					sender.sendMessage(ChatColor.RED + "Reinitializing server.");
					tekkitrestrict.getInstance().getServer().reload();
				} else if (args[0].equals("admin")) {
					if (sendNoPerm("admin")) return true;

					if (args.length == 1) {
						message.addAll(help(1));
						return true;
					}
					
					if (args[1].equals("help")) {
						try {
							message.addAll(help(Integer.parseInt(args[2])));
						} catch (NumberFormatException ex) {
							message.addAll(help(2));
						}
					} else if (args[1].equals("safezone")) {
						// /tr admin safezone
						
						if (args.length == 2 || args[2].equals("help")){
							if (sendNoPerm("admin.safezone")) return true;
							send(ChatColor.BLUE + " -- TekkitRestrict Safezone Help --");
							send("/tr admin safezone check [player]", "Check if a player is in a safezone.");
							send("/tr admin safezone list [page]", "List safezones");
							send("/tr admin safezone addwg [region]", "Add a safezone using WorldGuard");
							send("/tr admin safezone addgp", "Add a safezone using GriefPrevention");
							send("/tr admin safezone del [name]", "remove safezone");
							return true;
						}
						
						if (eIC(args[2], "list")){
							if (sendNoPerm("admin.safezone.list")) return true;
							
							int requestedPage = 1;
							if (args.length == 4){
								try {
									requestedPage = Integer.parseInt(args[3]);
								} catch (NumberFormatException ex){
									send(ChatColor.RED + "Page number incorrect!");
									return true;
								}
							}
							int totalPages = TRSafeZone.zones.size() / 5;
							if (totalPages == 0) totalPages = 1;
							if (requestedPage > totalPages) requestedPage = totalPages;
							
							send("SafeZones - Page " + requestedPage + " of " + totalPages);
							for (int i = ((requestedPage-1) * 5); i < (requestedPage * 5); i++){
								if (TRSafeZone.zones.size() <= i) break;
								
								TRSafeZone z = TRSafeZone.zones.get(i);
								if (z == null) continue;
								
								send("[" + z.world + "] " + z.name);
								send("  - Location: [" + z.x1 + " " + z.y1 + " " + z.z1 + "] - [" + z.x2 + " " + z.y2 + " " + z.z2 + "]");
							}
							return true;
						}
						
						if (eIC(args[2], "check")){
							Player target;
							if (args.length == 4){
								if (sendNoPerm("admin.safezone.check.others")) return true;
								target = Bukkit.getPlayer(args[3]);
								if (target == null){
									send(ChatColor.RED + "Cannot find player " + args[3] + "!");
									return true;
								}
							} else {
								if (sendNoPerm("admin.safezone.check")) return true;
								if (sender instanceof Player){
									target = (Player) sender;
								} else {
									send(ChatColor.RED + "The console can only use /tr admin safezone check <player>");
									return true;
								}
							}
							
							if (TRSafeZone.inSafeZone(target))
								send(ChatColor.BLUE + target.getName() + " is currently in a safezone.");
							else
								send(ChatColor.BLUE + target.getName() + " is currently " + ChatColor.RED + "not" + ChatColor.BLUE + " in a safezone.");
							
							return true;
						}
						
						if (eIC(args[2], "rem", "del") || eIC(args[2], "remove", "delete")){
							if (sendNoPerm("admin.safezone.remove")) return true;
							
							if (args.length != 4){
								send(ChatColor.RED + "Incorrect syntaxis!");
								send(ChatColor.RED + "Correct usage: /tr admin safezone rem <name>");
								return true;
							}
							
							String name = null;
							for (int i = 0; i < TRSafeZone.zones.size(); i++) {
								TRSafeZone zone = TRSafeZone.zones.get(i);
								name = zone.name;
								if (!name.toLowerCase().equals(args[3])) continue;
								
								TRSafeZone.zones.remove(i);
								send(ChatColor.GREEN + "Safezone " + name + " removed.");
								break;
							}
							//FIXME uppercase problem
							try {
								tekkitrestrict.db.query("DELETE FROM `tr_saferegion` WHERE `name` = '"
												+ tekkitrestrict.antisqlinject((name == null ? args[3] : name)) + "'");
							} catch (SQLException ex) {}
							
							return true;
						}
						
						if (eIC(args[2], "addwg")){
							if (console){
								send(ChatColor.RED + "This command can not be executed by the console!");
								return true;
							}
							
							if (sendNoPerm("admin.safezone.addwg")) return true;
							Player player = (Player) sender;
							String name = args[3];

							for (TRSafeZone current : TRSafeZone.zones){
								if (current.world.equalsIgnoreCase(player.getWorld().getName())){
									if (current.name.toLowerCase().equals(name)){
										send(ChatColor.RED + "There is already a safezone by this name!");
										return true;
									}
								}
							}
							PluginManager pm = Bukkit.getPluginManager();
							if (!pm.isPluginEnabled("WorldGuard")){
								send(ChatColor.RED + "WorldGuard is not enabled!");
								return true;
							}
							
							try {
								com.sk89q.worldguard.bukkit.WorldGuardPlugin wg = (com.sk89q.worldguard.bukkit.WorldGuardPlugin) pm.getPlugin("WorldGuard");
								Map<String, com.sk89q.worldguard.protection.regions.ProtectedRegion> rm = wg
										.getRegionManager(player.getWorld()).getRegions();
								com.sk89q.worldguard.protection.regions.ProtectedRegion pr = rm.get(name);
								if (pr != null) {
									TRSafeZone zone = new TRSafeZone();
									zone.mode = 1;
									zone.name = name;
									zone.world = player.getWorld().getName();
									TRSafeZone.zones.add(zone);
									TRSafeZone.save();
									send(ChatColor.GREEN + "Attached to region `" + name + "`!");
								} else {
									send(ChatColor.RED + "Region does not exist!");
									return true;
								}
							} catch (Exception E) {
								send(ChatColor.RED + "Error! (does the region exist?)");
								return true;
							}
							return true;
						}
						
						if (eIC(args[2], "addgp")){
							if (console){
								send(ChatColor.RED + "This command can not be executed by the console!");
								return true;
							}
							
							if (sendNoPerm("admin.safezone.addgp")) return true;
							
							if (args.length != 4){
								send(ChatColor.RED + "Incorrect syntaxis!");
								send(ChatColor.RED + "Correct usage: /tr admin safezone addgp <name>");
								return true;
							}
							
							Player player = (Player) sender;
							String name = args[3];

							for (TRSafeZone current : TRSafeZone.zones){
								if (current.world.equalsIgnoreCase(player.getWorld().getName())){
									if (current.name.toLowerCase().equals(name)){
										send(ChatColor.RED + "There is already a safezone by this name!");
										return true;
									}
								}
							}
							PluginManager pm = Bukkit.getPluginManager();
							if (!pm.isPluginEnabled("GriefPrevention")){
								send(ChatColor.RED + "GriefPrevention is not enabled!");
								return true;
							}
							
							SafeZoneCreate response = TRSafeZone.addSafeZone(player, "griefprevention", name);
							if (response == SafeZoneCreate.AlreadyExists)
								send(ChatColor.RED + "This region is already a safezone!");
							else if (response == SafeZoneCreate.RegionNotFound)
								send(ChatColor.RED + "There is no region at your current position!");
							else if (response == SafeZoneCreate.PluginNotFound)
								send(ChatColor.RED + "Safezones are disabled for GriefPrevention claims.");
							else if (response == SafeZoneCreate.Success)
								send(ChatColor.GREEN + "This claim is now a safezone!");
							else 
								send(ChatColor.RED + "An undefined error occured!");
					
							return true;
						}
						
						
					} else if (args[1].equalsIgnoreCase("limit")) {
						if (args.length == 2) {
							message.add("[TekkitRestrict "
									+ tekkitrestrict.getInstance()
											.getDescription().getVersion()
									+ " Limit Commands]");
							message.add("/tr admin limit clear [player]");
							message.add("/tr admin limit clear [player] [INDEX[id:data]]");
							message.add("/tr admin limit list [player]");
						} else if (args[2].equalsIgnoreCase("clear")) {
							// clears a player's limits...
							try {
								TRLimitBlock cc = TRLimitBlock
										.getLimiter(args[3]); //len 4
								if (args.length == 5) {
									List<TRCacheItem> iss = TRCacheItem
											.processItemString("", args[4],
													-1);
									for (TRCacheItem isr : iss) {
										for (TRLimit trl : cc.itemlimits) {
											//tekkitrestrict.log.info(isr.id+":"+isr.getData()+" ?= "+trl.blockID+":"+trl.blockData);
											if (TRNoItem.equalSet(isr.id,
													isr.getData(),
													trl.blockID,
													trl.blockData)) {
												if(trl.placedBlock != null)
													trl.placedBlock.clear();
												else trl.placedBlock = new LinkedList<Location>();
											}
											int ci = cc.itemlimits.indexOf(trl);
											if(ci != -1) cc.itemlimits.set(ci, trl);
										}
									}
									message.add("Cleared " + args[3]
											+ "'s block limits for "+args[4]);
								} else {
									cc.clearLimits();
									message.add("Cleared " + args[3]
											+ "'s block limits!");
								}
							} catch (Exception E) {
								message.add("[Error!] Please use the formats:");
								message.add("/tr admin limit clear [player]");
								message.add("/tr admin limit clear [player] [INDEX[id:data]]");
								message.add("/tr admin limit list [player]");
							}
						} else if (args[2].equalsIgnoreCase("list")) {
							// clears a player's limits...
							try {
								TRLimitBlock cc = TRLimitBlock
										.getLimiter(args[3]);
								boolean c = false;
								for (TRLimit l : cc.itemlimits) {
									c=true;
									int cccl = cc.getMax(cc.player, l.blockID, l.blockData);
									cccl = cccl == -1 ? 0 : cccl;
									message.add("[" + l.blockID + ":"
											+ l.blockData + "] - "
											+ l.placedBlock.size()+"/"+cccl+" blocks");
								}
								if(!c) message.add(args[3]+" does not have any limits!");
							} catch (Exception E) {
								message.add("[Error...] Please use the format:");
								message.add("  /tr admin limit list [player]");
							}
						}
					} else if (args[1].equalsIgnoreCase("emc")) {
						// tempset, lookup
						if (args.length == 2) {
							message.add("/tr admin emc tempset [id:data] [EMC]");
							message.add("/tr admin emc lookup [id:data]");
						} else if (args[2].equalsIgnoreCase("tempset")) {
							try {
								if (args[3] != null) {
									List<TRCacheItem> iss = TRCacheItem
											.processItemString("", args[3],
													-1);
									for (TRCacheItem isr : iss) {
										int data = isr.getData();
										HashMap<Integer, Integer> hm = (HashMap<Integer, Integer>)ee.EEMaps.alchemicalValues
												.get(isr.id);
										if (hm != null) {
											hm.put(data, Integer
													.parseInt(args[4]));
											message.add("Temporary EMC set successful!");
											ee.EEMaps.alchemicalValues.put(
													isr.id, hm);
										} else {
											hm = new HashMap<Integer, Integer>();
											hm.put(data, Integer
													.parseInt(args[4]));
											message.add("Temporary EMC set successful!");
											ee.EEMaps.alchemicalValues.put(
													isr.id, hm);
										}
									}
								} else {
									message.add("Use format:");
									message.add("/tr admin emc tempset [id] [EMC]   (data = 0)");
									message.add("/tr admin emc tempset [id:data] [EMC]");
									message.add("/tr admin emc tempset [id-id2] [EMC]");
								}
							} catch (Exception e) {
								TRLogger.Log("debug",
										"[COM] EMCSet " + e.getMessage());
								message.add("Sorry, EMC set unsuccessful...");
								message.add("/tr admin emc tempset [id] [EMC]   (data = 0)");
								message.add("/tr admin emc tempset [id:data] [EMC]");
								message.add("/tr admin emc tempset [id-id2] [EMC]");
							}
						} else if (args[2].equalsIgnoreCase("lookup")) {
							try {
								if (args[3] != null) {
									List<TRCacheItem> iss = TRCacheItem
											.processItemString("", args[3],
													-1);
									for (TRCacheItem isr : iss) {
										HashMap<Integer, Integer> hm = (HashMap<Integer, Integer>)ee.EEMaps.alchemicalValues
												.get(isr.id);
										int data = isr.getData();
										// message.add("[Results]");
										if (hm != null) {
											if (data == 0) {
												Iterator<?> ks = hm.keySet()
														.iterator();
												while (ks.hasNext()) {
													try {
														Integer dat = Integer
																.parseInt(ks
																		.next()
																		.toString());
														Object jj = hm
																.get(dat);
														if (jj != null) {
															int emc = Integer
																	.parseInt(jj
																			.toString());
															message.add("["
																	+ isr.id
																	+ ":"
																	+ dat
																	+ "] EMC: "
																	+ emc);
														}
													} catch (Exception e) {
													}
												}
											} else {
												Object adfk = hm
														.get(isr.data);
												if (adfk != null) {
													int emc = Integer
															.parseInt(adfk
																	.toString());
													int datax = isr.data == -10 ? 0
															: isr.data;
													message.add("["
															+ isr.id + ":"
															+ datax
															+ "] EMC: "
															+ emc);
												}
											}
										}
									}
								} else {
									message.add("Use format:");
									message.add("/tr admin emc lookup [id]   (data = 0)");
									message.add("/tr admin emc lookup [id:data]");
								}
							} catch (Exception e) {
								//e.printStackTrace();
								TRLogger.Log("debug", "[COM] EMCLookup "
										+ e.getMessage());
								message.add("Sorry, EMC lookup unsuccessful...");
								message.add("/tr admin emc lookup [id]   (all registered data values... spam?)");
								message.add("/tr admin emc lookup [id:data]");
								message.add("/tr admin emc lookup [id-id2] (Spam?)");
							}
						}
					}
				} else {
						usemsg = false;
						sender.sendMessage("You do not have access to that command!");
				}
				
			} catch (Exception e) {
				message.add("An error has occured processing your command.");
				TRLogger.Log("debug", "TRCommandTR Error: " + e.getMessage());
				for (StackTraceElement ee : e.getStackTrace()) {
					TRLogger.Log("debug", "     " + ee.toString());
				}
			}
			if (usemsg) {
				//sendMessage(player, message.toArray(new String[0])); TODO
				message.clear();
			}
			return true;
		}
		return false;
	}

	public static void sendMessage(Player player, String[] message) {
		if (player != null) {
			for (int k = 0; k < message.length; k++) {
				player.sendRawMessage(message[k]);
			}
		} else {
			for (int k = 0; k < message.length; k++) {
				tekkitrestrict.log.log(Level.OFF, message[k]);
			}
		}
	}

	public static List<String> help(int page) {
		List<String> message = new LinkedList<String>();
		if (page == 1) {
			message.add("[TekkitRestrict "
					+ tekkitrestrict.getInstance().getDescription()
							.getVersion() + " Admin Commands]");
			message.add("[Help page 1] - /tr admin help <page>");
			message.add("/tr admin safezone list [page] - list safezones");
			message.add("/tr admin safezone addwg [region] - add safezone using WorldGuard");
			message.add("/tr admin safezone rem [name] - remove safezone");
			message.add("/tr admin limit clear [player]");
			message.add("/tr admin limit clear [player] [INDEX[id:data]]");
			message.add("/tr admin limit list [player]");
			message.add("[1/2 pages]");
		} else if (page == 2) {
			message.add("[TekkitRestrict "
					+ tekkitrestrict.getInstance().getDescription()
							.getVersion() + " Admin Commands]");
			message.add("[Help page 2] - /tr admin help <page>");
			message.add("/tr admin emc tempset [id:data] [EMC]");
			message.add("/tr admin emc lookup [id:data]");
			message.add("[2/2 pages]");
		}
		return message;
	}
	
	private static void send(String msg){
		sender.sendMessage(msg);
	}
	@SuppressWarnings("unused")
	private static void send(String command, String explanation, ChatColor color, ChatColor color2){
		int msglength = command.length() + 3 + explanation.length();
		if (msglength<=55)
			sender.sendMessage(color + command + " - " + ChatColor.RESET + color2 + explanation);
		else {
			sender.sendMessage(color + command);
			sender.sendMessage(color2 + " - " + explanation);
		}
	}
	private static void send(String command, String explanation){
		int msglength = command.length() + 3 + explanation.length();
		if (msglength<=55) sender.sendMessage(ChatColor.BLUE + command + " - " + ChatColor.GREEN + explanation);
		else {
			sender.sendMessage(ChatColor.BLUE + command);
			sender.sendMessage(ChatColor.GREEN + " - " + explanation);
		}
	}
	
	private static boolean eIC(String arg, String arg2){
		return arg.toLowerCase().equals(arg2.toLowerCase());
	}
	private static boolean eIC(String arg, String arg2, String arg3){
		arg = arg.toLowerCase();
		return arg.equals(arg2.toLowerCase()) ? true : arg.equals(arg3.toLowerCase());
	}
	
	private static boolean sendNoPerm(String perm){
		if (sender.hasPermission("tekkitrestrict." + perm)) return false;
		sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
		return true;
	}
}
