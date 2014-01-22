package nl.taico.tekkitrestrict;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.NonNull;

public class Send {
	public CommandSender sender;
	
	public void msg(@NonNull final String msg){
		sender.sendMessage(msg);
	}

	public void msg(@NonNull final String command, @NonNull final String explanation, final ChatColor color, final ChatColor color2){
		final int msglength = command.length() + 3 + explanation.length();
		if (msglength<=55 || !(sender instanceof Player))
			sender.sendMessage(color + command + " - " + ChatColor.RESET + color2 + explanation);
		else {
			sender.sendMessage(color + command);
			sender.sendMessage(color2 + " - " + explanation);
		}
	}
	public void msg(@NonNull final String command, @NonNull final String explanation){
		final int msglength = command.length() + 3 + explanation.length();
		if (msglength<=55 || !(sender instanceof Player)) sender.sendMessage(ChatColor.BLUE + command + " - " + ChatColor.GREEN + explanation);
		else {
			sender.sendMessage(ChatColor.BLUE + command);
			sender.sendMessage(ChatColor.GREEN + " - " + explanation);
		}
	}
	
	public boolean noPerm(@NonNull final String perm){
		if (sender.hasPermission("tekkitrestrict." + perm)) return false;
		sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
		return true;
	}
	
	public boolean noConsole(){
		if (sender instanceof Player) return false;
		sender.sendMessage(ChatColor.RED + "This command can not be run from the console!");
		return true;
	}
}
