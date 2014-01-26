package nl.taico.tekkitrestrict.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static nl.taico.tekkitrestrict.commands.TRCmdHelper.*;

public class TRCmdOpenInv implements CommandExecutor {	
	public static HashMap<String, Player> openInv = new HashMap<String, Player>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "The console cannot use this command!");
			return true;
		}
		
		if (!sender.hasPermission("tekkitrestrict.openinv")){
			sender.sendMessage(ChatColor.RED + "You are not allowed to use this command!");
			return false;
		}
		
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Not enough arguments! Usage: /openinv <player>");
			return true;
		} else if (args.length > 1){
			sender.sendMessage(ChatColor.RED + "Too many arguments! Usage: /openinv <player>");
			return true;
		}
		
		final Player OPlayer = Playerz(sender, args[0]);
		if (OPlayer == null){
			sender.sendMessage(ChatColor.RED + "Player " + args[0] + " can't be found!");
			return true;
		}
		
		final Player player = (Player) sender;
		openInv.put(player.getName(), OPlayer);
		
		player.openInventory(OPlayer.getInventory());
		
		return true;
	}
	
	public static void closeInv(Player player){
		final Player OPlayer = openInv.remove(player.getName());
		if (OPlayer == null) return;
		//if offline then save inventory data
		if (Bukkit.getPlayerExact(OPlayer.getName()) == null) OPlayer.saveData();
	}
}
