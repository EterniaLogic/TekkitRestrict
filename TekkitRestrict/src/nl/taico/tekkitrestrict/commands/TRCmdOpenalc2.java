package nl.taico.tekkitrestrict.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.World;

import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

import ee.AlchemyBagData;
import ee.ItemAlchemyBag;

public class TRCmdOpenalc2  implements CommandExecutor {
	//private Send send;
	
	//public TRCmdOpenalc2(){
	//	send = new Send();
	//}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "The console cannot use this command!");
			return true;
		}
		
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
		
		final Player OPlayer = TRCmdOpenInv.Playerz(sender, args[0]);
		if (OPlayer == null){
			sender.sendMessage(ChatColor.RED + "Player " + args[0] + " cannot be found!");
			return true;
		}
		
		final EntityPlayer holder = ((CraftPlayer) OPlayer).getHandle();
		final int color = TRCmdOpenAlc.getColor(args[1]);

		if (color == -1){
			sender.sendMessage(ChatColor.RED + "Unknown color!");
			sender.sendMessage(ChatColor.RED + "Color can be white, lime, etc. OR a number from 0-15.");
			return true;
		}
		
		final AlchemyBagData alcdata = getBag(holder, ((CraftWorld) OPlayer.getWorld()).getHandle(), color);
		final Player player = (Player) sender;
		CraftPlayer pl = (CraftPlayer) player;
		//pl.openInventory(inventory)
		///player.openInventory(alcdata)
		
		return true;
	}
	
	public AlchemyBagData getBag(EntityPlayer targetPlayer, World world, int color){
		return ItemAlchemyBag.getBagData(color, targetPlayer, world);
	}
	/*
	public void openInv(EntityPlayer player, AlchemyBagData bag){
		player.netServerHandler.sendPacket(new Packet100OpenWindow(container.windowId, windowType, title, size));
	    getHandle().activeContainer = container;
	    getHandle().activeContainer.addSlotListener(player);
	}*/
}
