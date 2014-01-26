package nl.taico.tekkitrestrict.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static nl.taico.tekkitrestrict.commands.TRCmdHelper.*;

public class TRCmdTpic implements CommandExecutor {	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (noConsole(sender)) return true;
		if (noPerm(sender, "tpic")) return true;
		
		int max = 0;
		boolean thorough = false;
		
		if (args.length == 0) max = 200;
		else {
			try {
				max = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				msgr(sender, "This is not a valid number!");
				return true;
			}
		}
			
		if (args.length == 2){
			String arg1 = args[1].toLowerCase();
			
			if (arg1.equals("true") || arg1.equals("yes")) thorough = true;
			else if (!arg1.equals("false") && !arg1.equals("no")){
				msgr(sender, "Incorrect syntaxis! Correct usage:");
				msgr(sender, "/tpic [treshold] [include all entities]");
				msgr(sender, "[include all entities] can be true, false, yes or no.");
				return true;
			}
		}
		
		tpic((Player) sender, max, thorough);
		return true;
	}

	/**
	 * Searches for chunks with more than max items in them.<br>
	 * If thorough, searches for chunks with more than max <b>entities</b>.
	 */
	public static void tpic(Player player, int max, boolean thorough) {
		List<World> worlds = Bukkit.getServer().getWorlds();
		for (World world : worlds){
			List<Entity> Entities = world.getEntities();
			for (Entity current : Entities){
				if (!thorough) if (!(current instanceof Item)) continue;
				
				Vector vector = current.getLocation().toVector();
				
				int count = 0;
				for (Entity current2 : Entities){
					if (!thorough) if (!(current2 instanceof Item)) continue;
					
					Vector vectorNearby = current2.getLocation().toVector();
					if (vector.distance(vectorNearby) <= 16) count++;
				}
				
				if (count >= max) {
					player.sendMessage(ChatColor.GREEN + "Found " + count + " items in this area!");
					player.teleport(current.getLocation());
					return;
				}
			}
		}
		if (thorough)
			player.sendMessage(ChatColor.YELLOW + "There are no chunks with " + max + " entities.");
		else
			player.sendMessage(ChatColor.YELLOW + "There are no chunks with " + max + " items.");
	}
}