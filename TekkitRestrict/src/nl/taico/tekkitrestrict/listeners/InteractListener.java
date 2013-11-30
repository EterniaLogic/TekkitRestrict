package nl.taico.tekkitrestrict.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.NameProcessor;
import nl.taico.tekkitrestrict.TRLogger;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.functions.TRNoClick;
import nl.taico.tekkitrestrict.functions.TRNoDupeProjectTable;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.objects.TRItem;

public class InteractListener implements Listener {
	public static boolean errorInteract = false;
	// /////// START INTERACT //////////////
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		
		// lets do this based on a white-listed approach.
		// First, lets loop through the DisableClick list to stop clicks.
		// Perf: 8x
		if (TRNoClick.isDisabled(event)){
			event.setCancelled(true);
			return;
		}

		if (TRNoDupeProjectTable.tableUseNotAllowed(event.getClickedBlock(), player)){
			player.sendMessage(ChatColor.RED + "Someone else is already using this project table!");
			event.setCancelled(true);
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE) {
			ItemStack str = player.getItemInHand();
			if (str != null) {
				String msg = null;
				try {
					msg = TRNoItem.isItemBannedInCreative(player, str.getTypeId(), str.getDurability(), true);
				} catch (Exception ex) {
					if (!errorInteract){
						Warning.other("An error occurred in the Limited Creative Interact Listener ('+InteractListener.onPlayerInteract(...)')!", false);
						Log.Exception(ex, false);
						errorInteract = true;
					}
				}
				
				if (msg != null) {
					if (msg.equals("")) msg = ChatColor.RED + "[TRLimitedCreative] You may not interact with this item.";
					TRItem.sendBannedMessage(player, msg);
					event.setCancelled(true);
					player.setItemInHand(null);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInteractEvent2(PlayerInteractEvent event){
		if (!tekkitrestrict.EEEnabled) return;
		
		Player player = event.getPlayer();
		if (player == null) return;
		
		itemLogUse(player, event.getItem(), event.getAction());
	}

	/** Log EE tools. */
	private void itemLogUse(Player player, ItemStack item, Action action) {
		ItemStack a = player.getItemInHand();
		if (a == null) return;

		int id = a.getTypeId();
		
		if (id == 27530 || id == 27531)
			logUse("EEAmulet", player, id);
		else if (id == 27532 || id == 27534 || id == 27536 || id == 27537 || id == 27574 || id == 27584 || id == 27593)
			logUse("EERing", player, id);
		else if (inRange(id, 27543, 27548) || id == 27555){
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				logUse("EEDmTool", player, id);
		} else if (inRange(id, 27564, 27573)){
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				logUse("EERmTool", player, id);
		} else if (id == 27527 || id == 27556 || id == 27535)
			logUse("EEDestructive", player, id);
		else if (id == 27538 || id == 27553 || id == 27562 || id == 27583 || id == 27585 || id == 27592)
			logUse("EEMisc", player, id);
	}
	
	private void logUse(String logname, Player player, int id){
		Location loc = player.getLocation();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		TRLogger.Log(logname, "[" + player.getName() + "][" + player.getWorld().getName() +
				" - " + x + "," + y + "," + z + "] used (" + id + ") `" + NameProcessor.getEEName(id) + "`");
	}

	private boolean inRange(int stack, int from, int to) {
		return (stack >= from && stack <= to);
	}
}
