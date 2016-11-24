package nl.taico.tekkitrestrict.commands;

import java.io.File;

import lombok.NonNull;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import nl.taico.tekkitrestrict.Log;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

public class TRCmdHelper {
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

	public static void msg(@NonNull final CommandSender sender, @NonNull final String message){
		sender.sendMessage(message);
	}

	public static void msg(@NonNull final CommandSender sender, @NonNull final String command, @NonNull final String explanation){
		final int msglength = command.length() + 3 + explanation.length();
		if ((msglength<=55) || !(sender instanceof Player)) sender.sendMessage(ChatColor.BLUE + command + " - " + ChatColor.GREEN + explanation);
		else {
			sender.sendMessage(ChatColor.BLUE + command);
			sender.sendMessage(ChatColor.GREEN + " - " + explanation);
		}
	}

	public static void msgb(@NonNull final CommandSender sender, @NonNull final String message){
		sender.sendMessage(ChatColor.BLUE + message);
	}

	public static void msgg(@NonNull final CommandSender sender, @NonNull final String message){
		sender.sendMessage(ChatColor.GREEN + message);
	}

	public static void msgr(@NonNull final CommandSender sender, @NonNull final String message){
		sender.sendMessage(ChatColor.RED + message);
	}

	public static void msgy(@NonNull final CommandSender sender, @NonNull final String message){
		sender.sendMessage(ChatColor.YELLOW + message);
	}

	public static boolean noConsole(CommandSender sender){
		if (sender instanceof Player) return false;
		sender.sendMessage(ChatColor.RED + "This command can not be run from the console!");
		return true;
	}

	public static boolean noPerm(@NonNull final CommandSender sender, @NonNull final String permission){
		if (sender.hasPermission("tekkitrestrict." + permission)) return false;
		sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
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
			Log.warning("Exception in openAlc.Playerz: " + e.getMessage());
			return null;
		}
	}
}
