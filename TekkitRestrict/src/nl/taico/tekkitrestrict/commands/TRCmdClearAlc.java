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

import nl.taico.tekkitrestrict.Send;

import ee.AlchemyBagData;
import ee.ItemAlchemyBag;

public class TRCmdClearAlc implements CommandExecutor {
	final private Send send;
	
	public TRCmdClearAlc(){
		send = new Send();
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		send.sender = sender;
		if (send.noPerm("clearalc")) return false;
		switch (args.length){
			case 2:
				final Player OPlayer = TRCmdOpenInv.Playerz(sender, args[0]);
				if (OPlayer == null){
					sender.sendMessage(ChatColor.RED + "Player " + args[0] + " cannot be found!");
					return true;
				}
				
				if (sender instanceof Player){
					final String OName = OPlayer.getName();
					if (sender.hasPermission("tekkitrestrict.openalc.deny."+OName.toLowerCase()) && !sender.isOp() && !sender.hasPermission("tekkitrestrict.openalc.deny.all")){
						sender.sendMessage(ChatColor.RED + "You are not allowed to clear " + OName + "'s alchemy bags!");
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
						sender.sendMessage(ChatColor.RED + "Unknown color!");
						sender.sendMessage(ChatColor.RED + "Color can be white, lime, etc. OR a number from 0-15.");
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
							send.msg(ChatColor.RED + "An error occured while clearing the bag. Please try again.");
							return true;
						}
					}
					send.msg(ChatColor.GREEN + "Cleared all bags of "+OPlayer.getName());
				} else {
					try {
						final AlchemyBagData alcdata = getBag(holder, ((CraftWorld) OPlayer.getWorld()).getHandle(), color);
						for (int i = 0;i<alcdata.getSize();i++){
							alcdata.getContents()[i] = null;
						}
						alcdata.update();
					} catch (Exception ex){
						send.msg(ChatColor.RED + "An error occured while clearing the bag. Please try again.");
						return true;
					}
					send.msg(ChatColor.GREEN + "Cleared the "+TRCmdOpenAlc.getColor(color) + " bag of "+OPlayer.getName());
				}
				break;
			default:
				send.msg(ChatColor.BLUE+"Clearalc help");
				send.msg("/clearalc <player> <color|all> [world]", "Clear the alcbag(s) of the specified player.");
				break;
		}
		return true;
	}
	
	public AlchemyBagData getBag(EntityPlayer targetPlayer, World world, int color){
		return ItemAlchemyBag.getBagData(color, targetPlayer, world);
	}
}
