package com.github.dreadslicer.tekkitrestrict.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;

import net.minecraft.server.BaseMod;
import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import net.minecraft.server.mod_EE;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.dreadslicer.tekkitrestrict.TRLogger;
import com.github.dreadslicer.tekkitrestrict.TRNoItem;
import com.github.dreadslicer.tekkitrestrict.TRPermHandler;
import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

import forge.IGuiHandler;
import forge.MinecraftForge;
import forge.NetworkMod;

public class TRCommandAlc implements CommandExecutor {

	private tekkitrestrict plugin; // pointer to your main class, unrequired if
									// you don't need methods from the main
									// class

	public TRCommandAlc(tekkitrestrict plugin) {
		this.plugin = plugin;

	}

	private static java.util.HashMap<String, Object[]> InvAlc = new java.util.HashMap<String, Object[]>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player = null;
		boolean alc = false;

		if (sender instanceof Player) {
			player = (Player) sender;
			TRNoItem.isItemBanned(player, 100);
			try {

				// ru.tehkode.permissions.PermissionUser cc =
				// perm.getUser(player.getName());

				if (TRPermHandler.hasPermission(player, "admin", "", "")) {
				}
				if (TRPermHandler.hasPermission(player, "alc", "", "")) {
					alc = true;
				}
			} catch (Exception e) {
				if (player.isOp()) {
					alc = true;
				}
			}
		} else {
			alc = true;
		}
		LinkedList<String> message = new LinkedList<String>();
		boolean usemsg = true;
		if (cmd.getName().equalsIgnoreCase("openalc") && (alc)
				&& tekkitrestrict.config.getBoolean("UseOpenAlc")) {
			try {
				if (tekkitrestrict.EEEnabled) {
					if (args.length == 0) {
						// String message =
						message.add("Usage: /openalc [player] [color] color may be 0-15 OR 'white', ect.");
					} else {
						if (args[0] == "clear") {
							// clears the changes done to the player's current
							// inv.
						} else {
							if (player != null) {
								if (args[1] == null) {
									message.add("Color is not specified");
								} else {
									// save the player's current inv?

									// now we can open the player's current
									// alchemy
									// bag!
									EntityHuman H = ((org.bukkit.craftbukkit.entity.CraftPlayer) player)
											.getHandle();
									World W = ((org.bukkit.craftbukkit.CraftWorld) player
											.getWorld()).getHandle();

									try {
										// set override for the TARGET player
										// Player OPlayer =
										// this.getServer().getOfflinePlayer(args[0]).getPlayer();
										Player OPlayer = Playerz(player,
												args[0]);
										EntityHuman OH = ((org.bukkit.craftbukkit.entity.CraftPlayer) OPlayer)
												.getHandle();

										int color = Integer
												.parseInt(getColor(args[1]));
										// modified from EntityHuman.openGui
										try {
											this.openGui((EntityPlayer) H,
													(EntityPlayer) OH,
													mod_EE.getInstance(), 56,
													W, color, (int) H.locY,
													(int) H.locZ);
											// add this current player to the
											// hashmap!
											Object[] c = new Object[] {
													player.getInventory()
															.getContents(),
													player.getInventory()
															.getArmorContents() };
											InvAlc.put(player.getName(), c);
											TRLogger.Log("OpenAlc", "["
													+ player.getName()
													+ "] opened " + args[0]
													+ "'s [" + getColor(color)
													+ "] Alchemy Bag!");
										} catch (Exception E) {
											message.add("Error! Either "
													+ player.getName()
													+ " does not exist or "
													+ args[1]
													+ " does not exist");
											message.add("    as either a color nor an initialized alchemy bag.");
										}
									} catch (Exception E1) {
										message.add("Player: " + args[0]
												+ " does not exist!");
									}
								}
							}
						}
					}
				} else {
					message.add("EE is not enabled!");
				}
			} catch (Exception e) {
				message.add("An error has occured processing your command.");
				TRLogger.Log("debug", "TRCommandAlc Error: " + e.getMessage());
				for (StackTraceElement ee : e.getStackTrace()) {
					TRLogger.Log("debug", "     " + ee.toString());
				}
			}
			if (usemsg) {
				sendMessage(player, message.toArray(new String[0]));
				message.clear();
			}
			return true;
		}
		// ?
		return false;
	}

	public static void setPlayerInv(String name) {
		try {
			Object[] cc = InvAlc.get(name);
			InvAlc.put(name, null);
			if (cc != null) {
				ItemStack[] MainInv = (ItemStack[]) cc[0];
				ItemStack[] ArmorInv = (ItemStack[]) cc[1];

				CraftPlayer ccr = (CraftPlayer) tekkitrestrict.getInstance()
						.getServer().getPlayer(name);
				org.bukkit.inventory.PlayerInventory pi = ccr.getInventory();

				pi.setContents(MainInv);
				pi.setArmorContents(ArmorInv);
				ccr.updateInventory();
			}
		} catch (Exception ee) {
			//ee.printStackTrace();
		}
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

	private static String getColor(String color1) {
		String color = color1;
		color = color.toLowerCase();
		color = color.replace(" ", "");
		color = color.replace("-", "");
		color = color.replace("/", "");
		color = color.replace("\\", "");
		color = color.replace("white", "0");
		color = color.replace("orange", "1");
		color = color.replace("lightpurple", "2");
		color = color.replace("lightblue", "3");
		color = color.replace("yellow", "4");
		color = color.replace("lightgreen", "5");
		color = color.replace("pink", "6");
		color = color.replace("darkgray", "7");
		color = color.replace("darkgrey", "7");
		color = color.replace("gray", "8");
		color = color.replace("grey", "8");
		color = color.replace("cyan", "9");
		color = color.replace("purple", "10");
		color = color.replace("blue", "11");
		color = color.replace("brown", "12");
		color = color.replace("green", "13");
		color = color.replace("red", "14");
		color = color.replace("black", "15");
		if (color == "") {
			color = "0";
		}
		return color;
	}

	public static String getColor(int color1) {
		String color = "";
		if (color1 == 0) {
			return "white";
		} else if (color1 == 1) {
			return "orange";
		} else if (color1 == 2) {
			return "lightpurple";
		} else if (color1 == 3) {
			return "lightblue";
		} else if (color1 == 4) {
			return "yellow";
		} else if (color1 == 5) {
			return "lightgreen";
		} else if (color1 == 6) {
			return "pink";
		} else if (color1 == 7) {
			return "darkgray";
		} else if (color1 == 8) {
			return "gray";
		} else if (color1 == 9) {
			return "cyan";
		} else if (color1 == 10) {
			return "purple";
		} else if (color1 == 11) {
			return "blue";
		} else if (color1 == 12) {
			return "brown";
		} else if (color1 == 13) {
			return "green";
		} else if (color1 == 14) {
			return "red";
		} else if (color1 == 15) {
			return "black";
		}
		return color;
	}

	// get offline player
	// ///////import Openinv @commands/OpenInvPluginCommand.class/////////
	// Ref author's rights reserved. (C) Author.
	// reason: Loading offline player from name

	private Player Playerz(Player sender, String name) {
		Player target;
		target = plugin.getServer().getPlayer(name);
		if (target != null) {
			return target;
		}
		File playerfolder;
		playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(),
				"players");

		// sender.sendMessage((new
		// StringBuilder()).append(ChatColor.RED).append("Player ").append(name).append(" not found!").toString());
		// return null;
		String playername;
		playername = matchUser(Arrays.asList(playerfolder.listFiles()), name);

		// sender.sendMessage((new
		// StringBuilder()).append(ChatColor.RED).append("Player ").append(name).append(" not found!").toString());
		try {
			MinecraftServer server = ((org.bukkit.craftbukkit.CraftServer) Bukkit
					.getServer()).getServer();
			EntityPlayer entity = new EntityPlayer(server,
					server.getWorldServer(0), playername,
					new ItemInWorldManager(server.getWorldServer(0)));
			target = entity != null ? ((Player) (entity.getBukkitEntity()))
					: null;
			if (target != null) {
				target.loadData();
				return target;
			}
			sender.sendMessage((new StringBuilder()).append(ChatColor.RED)
					.append("Player ").append(name).append(" not found!")
					.toString());
		} catch (Exception e) {
			sender.sendMessage("Error while retrieving offline player data!");
			// e.printStackTrace();
			return null;
		}
		return target;
	}

	@SuppressWarnings("rawtypes")
	public static String matchUser(Collection container, String search) {
		String found = null;
		if (search == null) {
			return found;
		}
		String lowerSearch = search.toLowerCase();
		int delta = 0x7fffffff;
		Iterator iterator = container.iterator();
		while (iterator.hasNext()) {
			File file = (File) iterator.next();
			String filename = file.getName();
			String str = filename.substring(0, filename.length() - 4);
			if (!str.toLowerCase().startsWith(lowerSearch)) {
				continue;
			}
			int curDelta = str.length() - lowerSearch.length();
			if (curDelta < delta) {
				found = str;
				delta = curDelta;
			}
			if (curDelta == 0) {
				break;
			}
		}
		return found;
	}

	// //////End import////////

	// //////Import from net.minecraft.server.EntityHuman////////
	public void openGui(EntityPlayer player, EntityPlayer TargetPlayer,
			BaseMod mod, int ID, net.minecraft.server.World world, int x,
			int y, int z) {
		if (!(mod instanceof NetworkMod)) {
			return;
		}
		IGuiHandler handler = MinecraftForge.getGuiHandler(mod);
		if (handler != null) {
			Container container = (Container) handler.getGuiElement(ID,
					TargetPlayer, world, x, y, z);
			if (container != null) {
				container = org.bukkit.craftbukkit.event.CraftEventFactory
						.callInventoryOpenEvent(player, container);
				if (container != null) {
					player.realGetNextWidowId();
					player.H();

					forge.packets.PacketOpenGUI pkt = new forge.packets.PacketOpenGUI(
							player.getCurrentWindowIdField(),
							MinecraftForge.getModID((NetworkMod) mod), ID, x,
							y, z);
					player.netServerHandler.sendPacket(pkt.getPacket());
					player.activeContainer = container;
					player.activeContainer.windowId = player
							.getCurrentWindowIdField();
					player.activeContainer.addSlotListener(player);
				}
			}
		}
	}
	// //////EndImport////////
}