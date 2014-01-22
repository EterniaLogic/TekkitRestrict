package nl.taico.tekkitrestrict.commands;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;

import nl.taico.tekkitrestrict.tekkitrestrict;

public class TRCmdOpenInv implements CommandExecutor {
	//private Send send;
	
	//public TRCmdOpenInv(){
	//	send = new Send();
	//}
	
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
	
	public static Player Playerz(final CommandSender sender, final String name) {
		//Check if the targetplayer is online
		Player target = Bukkit.getPlayer(name);
		if (target != null) return target;
		
		//Otherwise search in the players folder for the player.
		final File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");

		final String playername;
		if ((playername = matchUser(playerfolder.listFiles(), name)) == null) return null;
		
		try {
			final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
			target = (new EntityPlayer(server, server.getWorldServer(0), playername, new ItemInWorldManager(server.getWorldServer(0)))).getBukkitEntity();
			
			//Do as if the player logs on.
			if (target != null) target.loadData();
			
			return target;
		} catch (Exception e) {
			sender.sendMessage("Error while retrieving offline player data!");
			tekkitrestrict.log.warning("Exception in openAlc.Playerz: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * @return The closest matching playername for the given name
	 */
	public static String matchUser(final File container[], String search) {
		if (search == null) return null;
		search = search.toLowerCase();
		
		String found = null;
		
		int delta = 0x7fffffff;
		for (final File file : container){
			final String filename = file.getName();
			final String str = filename.substring(0, filename.length() - 4);
			if (!str.toLowerCase().startsWith(search)) continue;
			
			int curDelta = str.length() - search.length();
			if (curDelta < delta) {
				found = str;
				delta = curDelta;
			}
			if (curDelta == 0) break;
		}
		return found;
	}
	
	public static void closeInv(Player player){
		final Player OPlayer = openInv.remove(player.getName());
		if (OPlayer == null) return;
		//if offline then save inventory data
		if (Bukkit.getPlayerExact(OPlayer.getName()) == null) OPlayer.saveData();
	}
}
