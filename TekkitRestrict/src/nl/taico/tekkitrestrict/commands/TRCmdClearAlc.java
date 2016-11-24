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
import static nl.taico.tekkitrestrict.commands.TRCmdHelper.*;
import ee.AlchemyBagData;
import ee.ItemAlchemyBag;

public class TRCmdClearAlc implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (noPerm(sender, "clearalc")) return false;
		switch (args.length){
			case 2:
				final Player OPlayer = Playerz(sender, args[0]);
				if (OPlayer == null){
					msgr(sender, "Player " + args[0] + " cannot be found!");
					return true;
				}
				
				if (sender instanceof Player){
					final String OName = OPlayer.getName();
					if (sender.hasPermission("tekkitrestrict.openalc.deny."+OName.toLowerCase()) && !sender.isOp() && !sender.hasPermission("tekkitrestrict.openalc.deny.all")){
						msgr(sender, "You are not allowed to clear " + OName + "'s alchemy bags!");
						return true;
					}
				}
				
				final EntityPlayer holder = ((CraftPlayer) OPlayer).getHandle();
				final int color;
				if (args[1].equalsIgnoreCase("all")){
					color = -2;
				} else {
					color = TRCmdOpenAlc.getColor(args[1]);
					if (color == -1){
						msgr(sender, "Unknown color!");
						msgr(sender, "Color can be white, lime, etc. OR a number from 0-15.");
						return true;
					}
				}
				if (color == -2){
					for (int j = 0;j<16;j++){
						try {
							final AlchemyBagData alcdata = getBag(holder, ((CraftWorld) OPlayer.getWorld()).getHandle(), j);
							for (int i = 0;i<alcdata.getSize();i++){
								alcdata.getContents()[i] = null;
							}
							alcdata.update();
						} catch (Exception ex){
							msgr(sender, "An error occured while clearing the bag. Please try again.");
							return true;
						}
					}
					msgg(sender, "Cleared all bags of "+OPlayer.getName());
				} else {
					try {
						final AlchemyBagData alcdata = getBag(holder, ((CraftWorld) OPlayer.getWorld()).getHandle(), color);
						for (int i = 0;i<alcdata.getSize();i++){
							alcdata.getContents()[i] = null;
						}
						alcdata.update();
					} catch (Exception ex){
						msgr(sender, "An error occured while clearing the bag. Please try again.");
						return true;
					}
					msgg(sender, "Cleared the "+TRCmdOpenAlc.getColor2(color) + ChatColor.GREEN + " bag of "+OPlayer.getName());
				}
				break;
			default:
				msgy(sender, "[TekkitRestrict Clearalc help]");
				msg(sender, "/clearalc <player> <color|all> [world]", "Clear the alcbag(s) of the specified player.");
				break;
		}
		return true;
	}
	
	public AlchemyBagData getBag(EntityPlayer targetPlayer, World world, int color){
		return ItemAlchemyBag.getBagData(color, targetPlayer, world);
	}
}
