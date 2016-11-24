package nl.taico.tekkitrestrict.commands;

import static nl.taico.tekkitrestrict.commands.TRCmdHelper.msg;
import static nl.taico.tekkitrestrict.commands.TRCmdHelper.msgr;
import static nl.taico.tekkitrestrict.commands.TRCmdHelper.noConsole;
import static nl.taico.tekkitrestrict.commands.TRCmdHelper.noPerm;

import java.util.ArrayList;
import java.util.List;

import nl.taico.tekkitrestrict.TRPermHandler;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.objects.TRLimit;
import nl.taico.tekkitrestrict.objects.TRPermLimit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TRCmdCheck implements CommandExecutor {	
	public static List<String> getAllPerms(Player player, List<TRLimit> skip){
		final List<TRPermLimit> perms = TRPermHandler.getAllLimiterPerms(player);
		final List<String> tbr = new ArrayList<String>();
		outer:
			for (TRPermLimit limit : perms){
				for (TRLimit l : skip){
					if ((l.id == limit.id) && ((l.data == -1) || (l.data == limit.data) || (limit.data == -1))) continue outer;
				}
				tbr.add("["+ limit.id + ":" + limit.data + "] - 0/"+((limit.max == -2) || (limit.max == -1) ? 0 : limit.max) + " blocks");
			}
		return tbr;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (noConsole(sender)) return true;

		if (noPerm(sender, "checklimits")) return true;

		if (args.length > 1){
			msgr(sender, "Incorrect syntaxis!");
			msg(sender, "/checklimits", "Check all your limits.");
			msg(sender, "/checklimits id", "Check limits for the given id.");
			msg(sender, "/checklimits hand", "Check limits about the block you are holding.");
			return true;
		}
		final Player player = (Player) sender;

		final TRLimiter cc = TRLimiter.getOnlineLimiter(player);

		if (args.length == 1){
			if(cc.itemlimits.isEmpty()){
				msgr(sender, "You don't have any limits!");
				return true;
			}

			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex){
				if (args[0].equalsIgnoreCase("hand")){
					final ItemStack item = player.getItemInHand();
					if (item == null){
						msg(sender, ChatColor.RED + "You don't have anything in your hand!");
						return true;
					} else {
						id = item.getTypeId();
					}
				} else if (args[0].equalsIgnoreCase("help")){
					msg(sender, "/checklimits", "Check all your limits.");
					msg(sender, "/checklimits id", "Check limits for the given id.");
					msg(sender, "/checklimits hand", "Check limits about the block you are holding.");
					return true;
				} else {
					msgr(sender, "You didn't specify a valid number!");
					return true;
				}
			}

			for (TRLimit l : cc.itemlimits) {
				if (l.id != id) continue;
				final int cccl = cc.getMax(player, l.id, l.data);
				msg(sender, "[" + l.id + ":" + l.data + "] - " + l.placedBlock.size() + "/" + (cccl == -1 ? 0 : cccl) + " blocks");
			}
		} else {
			if(cc.itemlimits.isEmpty()){
				msgr(sender, "You don't have any limits!");
				return true;
			}
			final List<TRLimit> skip = new ArrayList<TRLimit>();
			for (TRLimit l : cc.itemlimits) {
				final int cccl = cc.getMax(player, l.id, l.data);
				msg(sender, "[" + l.id + ":" + l.data + "] - " + l.placedBlock.size()+"/"+(cccl == -1 ? 0 : cccl)+" blocks");
				skip.add(l);
			}
			final List<String> permMsgs = getAllPerms(player, skip);
			for (String msg : permMsgs) msg(sender, msg);
		}
		return true;
	}

}
