package nl.taico.tekkitrestrict.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Player;

import net.minecraft.server.BaseMod;
import net.minecraft.server.Container;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.mod_EE;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.Send;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.objects.OpenAlcObj;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

import ee.AlchemyBagData;
import forge.IGuiHandler;
import forge.MinecraftForge;
import forge.NetworkMod;
import forge.packets.PacketOpenGUI;

public class TRCmdOpenAlc implements CommandExecutor {
	private final Send send;
	public TRCmdOpenAlc(){
		send = new Send();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		send.sender = sender;
		
		if (send.noConsole()) return true;

		if (!sender.hasPermission("tekkitrestrict.openalc")){
			sender.sendMessage(ChatColor.RED + "You are not allowed to use this command!");
			return false;
		}
		
		if (!tekkitrestrict.config.getBoolean(ConfigFile.General, "UseOpenAlc", true)){
			sender.sendMessage(ChatColor.RED + "Openalc is disabled!");
			return false;
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
			sender.sendMessage(ChatColor.RED + "Not enough arguments! Usage: /openalc <player> <color>");
			return true;
		} else if (args.length != 2){
			sender.sendMessage(ChatColor.RED + "Too many arguments! Usage: /openalc <player> <color>");
			return true;
		}
		
		final Player player = (Player) sender;
		
		if (OpenAlcObj.isViewing(player.getName())) setPlayerInv(player, false); //If player has an inventory open, close it.
		
		final Player OPlayer = TRCmdOpenInv.Playerz(sender, args[0]);
		
		if (OPlayer == null){
			sender.sendMessage(ChatColor.RED + "Player " + args[0] + " cannot be found!");
			return true;
		}
		
		final String OName = OPlayer.getName();
		
		if (player.hasPermission("tekkitrestrict.openalc.deny."+OName.toLowerCase()) && !player.isOp() && !player.hasPermission("tekkitrestrict.openalc.deny.all")){
			sender.sendMessage(ChatColor.RED + "You are not allowed to open " + OName + "'s alchemy bags!");
			return true;
		}

		final int color = getColor(args[1]);
		if (color == -1){
			sender.sendMessage(ChatColor.RED + "Unknown color!");
			sender.sendMessage(ChatColor.RED + "Color can be white, lime, etc. OR a number from 0-15.");
			return true;
		}
		
		if (OpenAlcObj.isViewed(OName, color)){
			sender.sendMessage(ChatColor.RED + "Someone else is already viewing the "+getColor(color)+" bag of " + OName + "!");
			return true;
		}
		
		try {
			//World world = ((CraftWorld) player.getWorld()).getHandle();
			final AlchemyBagData alcdata = openGui(((CraftPlayer) player).getHandle(), ((CraftPlayer) OPlayer).getHandle(), color);
			if (alcdata == null){
				Warning.other("An error occurred. " + OName + "'s bag might not save properly if he is offline.", false);
				sender.sendMessage(ChatColor.RED + "An error occured: Unable to find the specified bag!");
				return true;
			}
			
			new OpenAlcObj(alcdata, OPlayer, player, color); //Add new OpenAlcObject
			
			final String strcolor = getColor(color);
			sender.sendMessage(ChatColor.GREEN + "Opened " + OName + "'s " + strcolor + " Alchemy Bag!");
			
			tekkitrestrict.log.info(player.getName() + " opened " + OName + "'s " + strcolor + " Alchemy Bag!");
		} catch (Exception ex) {
			sender.sendMessage(ChatColor.RED + "An error has occurred processing your command.");
			Warning.other("Exception in OpenAlc (TRCommandAlc.onCommand)! Error: " + ex.toString(), false);
		}

		return true;
	}
	
	public static void setPlayerInv(Player player, boolean inform) {
		if (player == null) return;

		final String name = player.getName();
		final OpenAlcObj openAlc = OpenAlcObj.getOpenAlcByViewer(name);
		if (openAlc == null) return;
		else {
			if (Bukkit.getPlayerExact(openAlc.getBagOwnerName()) == null) openAlc.getBagOwner().saveData(); //If online, saving is not required.
			
			openAlc.getBag().a();
			
			openAlc.delete();

			player.openInventory(player.getInventory());
			player.closeInventory();
			
			if (inform) player.sendMessage(ChatColor.BLUE + "Your own inventory was restored.");
			return;
		}
	}
	
	/**
	 * Restore the inventory of the viewer.
	 * @param player The viewer of the bag.
	 */
	public static void setOnDisconnect(Player player) {
		if (player == null) return;
		
		String name = player.getName();
		OpenAlcObj openAlc = OpenAlcObj.getOpenAlcByViewer(name);
		
		if (openAlc == null) return;
		else { //A viewer has been found and he matches this player.
			if (Bukkit.getPlayerExact(openAlc.getBagOwnerName()) == null) openAlc.getBagOwner().saveData(); //If online, saving is not required.
			
			openAlc.getBag().a();
			
			openAlc.delete();
		}
	}
	
	/**
	 * @param player The player that needs to see the inventory.
	 * @param targetPlayer The player that owns the inventory.
	 * @return 
	 */
	public static AlchemyBagData openGui(EntityPlayer player, EntityPlayer targetPlayer, int color) {
		BaseMod mod = mod_EE.getInstance();
		if (!(mod instanceof NetworkMod)) return null;
		
		IGuiHandler handler = MinecraftForge.getGuiHandler(mod);
		if (handler == null) return null;
		
		Container container = (Container) handler.getGuiElement(56, targetPlayer, player.world, color, 0, 0);
		if (container == null) return null;
		container.setPlayer(player);

		container = CraftEventFactory.callInventoryOpenEvent(player, container);
		if (container == null) return null;
		player.realGetNextWidowId();
		player.H();

		PacketOpenGUI pkt = new PacketOpenGUI(player.getCurrentWindowIdField(), MinecraftForge.getModID((NetworkMod) mod), 56, color, 0, 0);
		player.netServerHandler.sendPacket(pkt.getPacket());
		player.activeContainer = container;
		player.activeContainer.windowId = player.getCurrentWindowIdField();
		player.activeContainer.addSlotListener(player);
		AlchemyBagData test = null;
		try {
			test = (AlchemyBagData) container.getInventory();
		} catch (Exception ex){
			Warning.other("An error occured in OpenAlc (TRCmdOpenAlc.openGui)!", false);
			Log.Exception(ex, false);
			test = null;
		}
		return test;
	}

	public static int getColor(String color) {
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
			//case -2: return "all";
			default: return "unknown";
		}
	}
	
}