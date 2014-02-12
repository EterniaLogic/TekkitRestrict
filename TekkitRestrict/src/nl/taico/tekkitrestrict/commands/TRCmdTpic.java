package nl.taico.tekkitrestrict.commands;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

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
			final String arg1 = args[1].toLowerCase();
			
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
		final List<World> worlds = Bukkit.getServer().getWorlds();
		for (World world : worlds){
			if (!thorough){
				final Collection<Item> items = world.getEntitiesByClass(Item.class);
				for (Item item : items){
					final Location loc = item.getLocation();
					
					int count = 0;
					for (Item item2 : items){
						if (distanceSquared(loc, item2.getLocation()) <= 256){
							count++;
							if (count >= max){
								player.sendMessage(ChatColor.GREEN + "Found " + count + " items in this area!");
								player.teleport(loc);
								return;
							}
						}
					}
				}
			} else {
				final List<Entity> Entities = world.getEntities();
				for (final Entity current : Entities){
					final Location loc = current.getLocation();
					
					int count = 0;
					for (Entity current2 : Entities){
						if (distanceSquared(loc, current2.getLocation()) <= 256){
							count++;
							if (count >= max) {
								player.sendMessage(ChatColor.GREEN + "Found " + count + " items in this area!");
								player.teleport(current.getLocation());
								return;
							}
						}
					}
				}
			}
			
		}
		if (thorough)
			player.sendMessage(ChatColor.YELLOW + "There are no chunks with " + max + " entities.");
		else
			player.sendMessage(ChatColor.YELLOW + "There are no chunks with " + max + " items.");
	}
	
	public static double distanceSquared(Location loc, Location loc2){
		return (loc.getX()-loc2.getX())*(loc.getX()-loc2.getX())+(loc.getY()-loc2.getY())*(loc.getY()-loc2.getY())+(loc.getZ()-loc2.getZ())*(loc.getZ()-loc2.getZ());
	}
}