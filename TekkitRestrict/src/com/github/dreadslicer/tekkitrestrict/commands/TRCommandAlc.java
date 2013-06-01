package com.github.dreadslicer.tekkitrestrict.commands;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import net.minecraft.server.BaseMod;
import net.minecraft.server.Container;
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
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

import ee.AlchemyBagData;
import forge.IGuiHandler;
import forge.MinecraftForge;
import forge.NetworkMod;
import forge.packets.PacketOpenGUI;

public class TRCommandAlc implements CommandExecutor {

	public static HashMap<String, HashMap<CraftPlayer, AlchemyBagData>> openAlc = new HashMap<String, HashMap<CraftPlayer, AlchemyBagData>>();

	/**
	 * First string is the player that owns the bag.
	 * Second string is the player that views the bag.
	 */
	public static HashMap<String, String> openAlc2 = new HashMap<String, String>();
	//private static int counter = 0;
	
	//private static HashMap<String, Object[]> InvAlc = new java.util.HashMap<String, Object[]>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "The console cannot use this command!");
			return true;
		}
		
		

		if (!sender.hasPermission("tekkitrestrict.openalc")){
			sender.sendMessage(ChatColor.RED + "You are not allowed to use this command!");
			//Logger.getLogger("Minecraft").log(Level.parse("Player_Command"), sender.getName() + " was denied to use /openalc");
			return true;
		}
		
		//LinkedList<String> message = new LinkedList<String>();
		//boolean usemsg = true;
		if (!cmd.getName().equalsIgnoreCase("openalc")) return true;
		if (!tekkitrestrict.config.getBoolean("UseOpenAlc")){
			sender.sendMessage(ChatColor.RED + "Openalc is disabled!");
		}
		
		if (!tekkitrestrict.EEEnabled) {
			sender.sendMessage(ChatColor.DARK_RED + "EE is not enabled!");
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(ChatColor.GREEN + "/openalc <player> <color> - Open an alchemy bag.");
			sender.sendMessage(ChatColor.GREEN + "Color can be white, lime, etc. OR a number from 0-15.");
			return true;
		} else if (args.length == 1) {
			sender.sendMessage(ChatColor.RED + "Not enough arguments!");
			sender.sendMessage(ChatColor.RED + "Usage: /openalc <player> <color>");
			return true;
		} else if (args.length != 2){
			sender.sendMessage(ChatColor.RED + "Too many arguments!");
			sender.sendMessage(ChatColor.RED + "Usage: /openalc <player> <color>");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (openAlc.containsKey(player.getName())) setPlayerInv(player);
		
		EntityPlayer looker = ((CraftPlayer) player).getHandle();
		Player OPlayer = Playerz(player, args[0]);
		
		if (OPlayer == null){
			sender.sendMessage(ChatColor.RED + "Player " + args[0] + " cannot be found!");
			return true;
		}
		
		String OName = OPlayer.getName();
		if (player.hasPermission("tekkitrestrict.openalc.deny."+OName) && !player.isOp()){
			sender.sendMessage(ChatColor.RED + "You are not allowed to open " + OName + "'s alchemy bags!");
			return true;
		}
		
		if (openAlc2.containsKey(OName)){
			sender.sendMessage(ChatColor.RED + "Someone else is already viewing a bag of " + OName + "!");
			return true;
		}
		
		EntityPlayer holder = ((CraftPlayer) OPlayer).getHandle();

		int color = getColor(args[1]);
		if (color == -1){
			sender.sendMessage(ChatColor.RED + "Unknown color!");
			sender.sendMessage(ChatColor.RED + "Color can be white, lime, etc. OR a number from 0-15.");
			return true;
		}
		
		try {
			World world = ((CraftWorld) player.getWorld()).getHandle();
			AlchemyBagData alcdata = openGui(looker, holder, mod_EE.getInstance(), 56, world, color, (int) looker.locY, (int) looker.locZ);
			if (alcdata == null){
				tekkitrestrict.log.warning("An error occured. " + OName + "'s bag will not save properly if he is offline.");
			}
			//open.add(player.getName());
			String name = player.getName();
			HashMap<CraftPlayer, AlchemyBagData> temp = new HashMap<CraftPlayer, AlchemyBagData>();
			temp.put((CraftPlayer) OPlayer, alcdata);
			openAlc.put(name, temp);
			openAlc2.put(OName, name);
			String strcolor = getColor(color);
			sender.sendMessage(ChatColor.GREEN + "Opened " + OName + "'s " + strcolor + " Alchemy Bag!");
			sender.sendMessage(ChatColor.BLUE + "Close your inventory (twice) to restore your own inventory.");
			
			tekkitrestrict.log.info(player.getName() + " opened " + OName + "'s " + strcolor + " Alchemy Bag!");
			
			//counter++;
			
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "An error has occured processing your command.");
			tekkitrestrict.log.warning("Exception in OpenAlc : " + e.getMessage());
		}
		/*
			try {
				
				
				
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
									Player OPlayer = Playerz(player, args[0]);
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
			} catch (Exception e) {
				message.add("An error has occured processing your command.");
				TRLogger.Log("debug", "TRCommandAlc Error: " + e.getMessage());
				for (StackTraceElement ee : e.getStackTrace()) {
					TRLogger.Log("debug", "     " + ee.toString());
				}
			}*/
			return true;
	}

	/*public static void setPlayerInv(String name) {
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
	}*/

	/** Called when a player logs off while having an inventory open. */
	@SuppressWarnings("deprecation")
	public static void setPlayerInv(Player player) {
		if (player == null) return;
		
		String name = player.getName();
		HashMap<CraftPlayer, AlchemyBagData> data = openAlc.remove(name);
		
		if (data != null) {
			for (CraftPlayer current : data.keySet()){
				if (Bukkit.getPlayerExact(current.getName()) == null) current.saveData();
			}
			for (AlchemyBagData current : data.values()) current.a();
		} else return;
		
		//if (openAlc.isEmpty()) counter = 0;
		//else counter--;
		
		player.updateInventory();
	}
	
	/** Called when a player closes his inventory. */
	public static void setPlayerInv2(Player player) {
		if (player == null) return;
		
		String name = player.getName();
		String viewer = openAlc2.remove(name);
		
		if (viewer != null){
			player = Bukkit.getPlayer(viewer);
			HashMap<CraftPlayer, AlchemyBagData> data = openAlc.remove(viewer);
			
			if (data != null){
				for (CraftPlayer current : data.keySet()){
					if (Bukkit.getPlayerExact(current.getName()) == null) current.saveData();
				}
				for (AlchemyBagData current : data.values()) current.a();
				
				player.openInventory(player.getInventory());
				player.closeInventory();

				player.sendMessage(ChatColor.BLUE + "Your own inventory was restored.");
			}
		} else return;
		
		//if (openAlc.isEmpty()) counter = 0;
		//else counter--;
	}
	
	/**
	 * @param player The player that needs to see the inventory.
	 * @param TargetPlayer The player that owns the inventory.
	 * @return 
	 */
	public static AlchemyBagData openGui(EntityPlayer player, EntityPlayer TargetPlayer, BaseMod mod, int ID, World world, int x, int y, int z) {
		if (!(mod instanceof NetworkMod)) return null;
		
		IGuiHandler handler = MinecraftForge.getGuiHandler(mod);
		if (handler == null) return null;
		
		Container container = (Container) handler.getGuiElement(ID, TargetPlayer, world, x, y, z);
		if (container == null) return null;
		
		container = CraftEventFactory.callInventoryOpenEvent(player, container);
		if (container == null) return null;
		player.realGetNextWidowId();
		player.H();

		PacketOpenGUI pkt = new PacketOpenGUI(player.getCurrentWindowIdField(),
				MinecraftForge.getModID((NetworkMod) mod), ID, x, y, z);
		player.netServerHandler.sendPacket(pkt.getPacket());
		player.activeContainer = container;
		player.activeContainer.windowId = player.getCurrentWindowIdField();
		player.activeContainer.addSlotListener(player);
		AlchemyBagData test = null;
		try {
			test = (AlchemyBagData) container.getInventory();
		} catch (Exception ex){
			tekkitrestrict.log.warning("Cannot Cast: ");
			ex.printStackTrace();
			test = null;
		}
		return test;
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

	private static int getColor(String color) {
		color = color.toLowerCase();
		switch (color){
		case "white": return 0;
		case "orange": return 1;
		case "lightpurple":
		case "magenta": return 2;
		case "lightblue": return 3;
		case "yellow": return 4;
		case "lightgreen":
		case "lime": return 5;
		case "pink": return 6;
		case "gray":
		case "grey": return 7;
		case "lightgray":
		case "lightgrey": return 8;
		case "cyan": return 9;
		case "purple": return 10;
		case "blue": return 11;
		case "brown": return 12;
		case "green": return 13;
		case "red": return 14;
		case "black": return 15;
		default:
			try {
				int tbr = Integer.parseInt(color);
				if (tbr < 16 && tbr > -1) return tbr;
			} catch (NumberFormatException ex){}
			return -1;
		}
	}
	public static String getColor(int color) {
		switch (color){
			case 0: return "white";
			case 1: return "orange";
			case 2: return "magenta";
			case 3: return "lightblue";
			case 4: return "yellow";
			case 5: return "lime";
			case 6: return "pink";
			case 7: return "gray";
			case 8: return "lightgray";
			case 9: return "cyan";
			case 10: return "purple";
			case 11: return "blue";
			case 12: return "brown";
			case 13: return "green";
			case 14: return "red";
			case 15: return "black";
			default: return "unknown";
		}
	}

	private static Player Playerz(Player sender, String name) {
		//Check if the targetplayer is online
		Player target = Bukkit.getPlayer(name);
		if (target != null) return target;
		
		//Otherwise search in the players folder fore the player.
		File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");

		String playername = matchUser(playerfolder.listFiles(), name);//(Removed arrays.AsList)
		if (playername == null) return null;
		
		try {
			MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
			EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playername, new ItemInWorldManager(server.getWorldServer(0)));
			target = entity.getBukkitEntity();
			if (target != null) {
				//Do as if the player logs on.
				target.loadData();
				return target;
			}
			sender.sendMessage((new StringBuilder())
								.append(ChatColor.RED)
								.append("Player ")
								.append(name)
								.append(" can not be found!").toString());
		} catch (Exception e) {
			sender.sendMessage("Error while retrieving offline player data!");
			tekkitrestrict.log.warning("Exception in TRCommandAlc.Playerz: ");
			e.printStackTrace();
			return null;
		}
		return target;
	}
	
	public static String matchUser(File container[], String search) {
		if (search == null) return null;
		
		String found = null;
		String lowerSearch = search.toLowerCase();
		int delta = 0x7fffffff;
		for (File file : container){
			String filename = file.getName();
			String str = filename.substring(0, filename.length() - 4);
			if (!str.toLowerCase().startsWith(lowerSearch)) continue;
			
			int curDelta = str.length() - lowerSearch.length();
			if (curDelta < delta) {
				found = str;
				delta = curDelta;
			}
			if (curDelta == 0) break;
		}
		return found;
	}

	/*@SuppressWarnings("rawtypes")
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
	}*/

	/*public void openGui(EntityPlayer player, EntityPlayer TargetPlayer,
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
	}*/
}