package nl.taico.tekkitrestrict.functions;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

public class TRNoInteract {
	public int id, data;
	public boolean air = false, block = false, safezone = false, useB = false;
	public String msg = ""; // left / right
	//public TRClickType type = TRClickType.Both;
	public boolean right = true, left = true, trample = false;

	public boolean compare(Player player, Block bl, ItemStack iss, Action action) {
		if (this.useB) {
			if (bl == null) return false;
			if (TRNoItem.equalSet(id, data, bl.getTypeId(), bl.getData())) {
				if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) return true;
			}
		} else if (TRNoItem.equalSet(id, data, iss.getTypeId(), iss.getDurability())) {
			if (safezone && !TRSafeZone.isSafeZoneFor(player, true, true)) return false;
			if (right){
				if (air && (action == Action.RIGHT_CLICK_AIR || (left && action == Action.LEFT_CLICK_AIR))) return true;
				if (block && (action == Action.RIGHT_CLICK_BLOCK || (left && action == Action.LEFT_CLICK_BLOCK))) return true;
				if (trample && action == Action.PHYSICAL) return true;
			} else if (left){
				if (air && action == Action.LEFT_CLICK_AIR) return true;
				if (block && action == Action.LEFT_CLICK_BLOCK) return true;
				if (trample && action == Action.PHYSICAL) return true;
			} else if (trample){
				if (action == Action.PHYSICAL) return true;
			} else {
				Warning.other("An error occurred in TRNoClick: Unknown action " + action.toString(), true);
			}
		}

		return false;
	}

	private static LinkedList<TRNoInteract> disableClickItemActions = new LinkedList<TRNoInteract>();
	
	public static void reload(){
		final LinkedList<TRNoInteract> temp = new LinkedList<TRNoInteract>();
		final List<String> disableClicks = tekkitrestrict.config.getStringList(ConfigFile.DisableInteract , "DisableClick");
		for (String disableClick : disableClicks){
			String msg = null;
			if (disableClick.contains("{")){
				final String tempe[] = disableClick.split("\\{");
				disableClick = tempe[0].trim();
				msg = Log.replaceColors(tempe[1].replace("}", ""));
			}
			final String tempe[] = disableClick.split(" ");
			if (tempe[0].equalsIgnoreCase("block")){
				if (tempe.length == 1){
					Warning.config("You have an error in your DisableInteract config: \"block\" is not a valid itemstring", false);
					continue;
				}
				
				final List<TRItem> iss;
				try {
					iss = TRItemProcessor.processItemString(tempe[1]);
				} catch (TRException ex) {
					Warning.config("You have an error in your DisableInteract.config.yml in DisableClick:", false);
					Warning.config(ex.getMessage(), false);
					continue;
				}
				for (final TRItem item : iss) {
					final TRNoInteract noclick = new TRNoInteract();
					noclick.id = item.id;
					noclick.data = item.data;
					if (msg != null) noclick.msg = msg;
					else noclick.msg = ChatColor.RED + "You may not interact with this block in your hand.";
					noclick.useB = true;
					temp.add(noclick);
				}
			} else {
				//###########################################################################
				//Id's and data
				final List<TRItem> iss;
				try {
					iss = TRItemProcessor.processItemString(tempe[0]);
				} catch (TRException ex) {
					Warning.config("You have an error in your DisableInteract.config.yml in DisableClick:", false);
					Warning.config(ex.getMessage(), false);
					continue;
				}
				
				for (final TRItem item : iss){
					final TRNoInteract noclick = new TRNoInteract();
					
					noclick.id = item.id;
					noclick.data = item.data;
					
					if (tempe.length > 1){
						for (int i = 1; i<tempe.length; i++){
							final String current = tempe[i].toLowerCase();
							switch (current){
								case "left":
									noclick.left = true;
									break;
								case "right":
									noclick.right = true;
									break;
								case "both":
									noclick.left = true;
									noclick.right = true;
									break;
								case "all":
									noclick.left = true;
									noclick.right = true;
									noclick.trample = true;
									break;
								case "trample":
									noclick.trample = true;
									break;
								case "air":
									noclick.air = true;
									break;
								case "block":
									noclick.block = true;
									break;
								case "safezone":
									noclick.safezone = true;
									break;
								default:
									Log.Warning.config("You have an error in your DisableInteract config: Invalid clicktype \""+current+"\"", false);
									Log.Warning.config("Valid types: left, right, both, trample, all, air, block, safezone", false);
									continue;
							}
						}
					}
					if (!noclick.trample && !noclick.air && !noclick.block){
						noclick.air = true;
						noclick.block = true;
					}
					
					if (msg != null){
						noclick.msg = msg;
					} else {
						final String a;
						if (noclick.air){
							if (noclick.block) a = "";
							else a = " in the air";
						} else {
							a = " on blocks";
						}
						
						final String s = noclick.safezone ? " inside a safezone." : ".";
						
						if (noclick.left){
							if (noclick.right) noclick.msg = ChatColor.RED + "Sorry, but clicking with this item"+a+" is not allowed" + s;
							else noclick.msg = ChatColor.RED + "Sorry, but left-clicking with this item"+a+" is not allowed" + s;
						} else if (noclick.right){
							noclick.msg = ChatColor.RED + "Sorry, but right-clicking with this item"+a+" is not allowed" + s;
						} else if (noclick.trample){
							noclick.msg = ChatColor.RED + "Sorry, but trampling with this item in your hand is not allowed" + s;
						}
					}
					
					temp.add(noclick);
				}
				//###########################################################################
				
			}
		}
		disableClickItemActions = temp;
	}

	public static boolean errorLogged = false;
	public static boolean isDisabled(@NonNull PlayerInteractEvent event) {
		final ItemStack item = event.getItem();
		if (item == null) return false;
		
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.noclick")) return false;
		
		final Action action = event.getAction();
		if (Listeners.useNoCLickPerms && hasPerm(player, item, action)){
			final String lr, extra;
			if (action == Action.LEFT_CLICK_AIR){
				lr = "left-clicking";
				extra = " in the air";
			} else if (action == Action.LEFT_CLICK_BLOCK){
				lr = "left-clicking";
				extra = " on a block";
			} else if (action == Action.RIGHT_CLICK_AIR){
				lr = "right-clicking";
				extra = " in the air";
			} else if (action == Action.RIGHT_CLICK_BLOCK){
				lr = "right-clicking";
				extra = " on a block";
			} else if (action == Action.PHYSICAL){
				lr = "trampling";
				extra = "";
			} else {
				lr = "";
				extra = "";
				Warning.other("An error occurred in TRNoClick: Unknown action: " + action.toString(), true);
			}
			player.sendMessage(ChatColor.RED + "Sorry, but "+lr+" with this item"+extra+" is not allowed.");
			return true;
		}
		try {
			for (final TRNoInteract cia : disableClickItemActions) {
				if (cia.compare(player, event.getClickedBlock(), item, action)) {
					if (!cia.msg.isEmpty()) {
						TRItem.sendBannedMessage(player, cia.msg);
					} else {
						StringBuilder msg = new StringBuilder()
							.append(ChatColor.RED.toString())
							.append("Sorry, but ")
							.append(cia.getTypeName())
							.append(" with this item ");
						if (cia.air){
							if (!cia.block) msg.append("in the air ");
						} else {
							if (cia.block) msg.append("on blocks ");
						}
						
						if (cia.safezone) msg.append("is not allowed inside a safezone.");
						else msg.append("is not allowed.");
						player.sendMessage(msg.toString());
					}
					return true;
				}
			}
		} catch (Exception ex){
			if (!errorLogged){
				Warning.other("An error occurred in TRNoClick.isDisabled!", false);
				Warning.other("This error will only be logged once.", false);
				Log.Exception(ex, false);
				errorLogged = true;
			}
		}
		return false;
	}
	
	private String getTypeName(){
		if (left){
			if (right) return "clicking";
			else return "left-clicking";
		} else if (right){
			return "right-clicking";
		} else if (trample){
			return "trampling";
		}
		return "";
	}
	
	private static boolean hasPerm(@NonNull Player player, @NonNull ItemStack item, @NonNull Action action){
		final String base2 = new StringBuilder(34).append("tekkitrestrict.noclick.").append(item.getTypeId()).append(".").append(item.getDurability()).toString();
		if (player.hasPermission(base2)) return true;
		
		final StringBuilder p1 = new StringBuilder(42).append(base2);
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
			p1.append(".left");
		} else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
			p1.append(".right");
		} else {
			p1.append(".trample");
		}
		
		if (player.hasPermission(p1.toString())) return true;
		
		return false;
	}
}