package nl.taico.tekkitrestrict.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.FileLog;
import nl.taico.tekkitrestrict.NameProcessor;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.TRConfigCache.Logger;

public class InteractListener implements Listener {
	public static boolean errorInteract = false;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInteract_Logger(PlayerInteractEvent event){
		if (!tekkitrestrict.EEEnabled) return;
		if (event.getPlayer() == null) return;

		itemLogUse(event.getPlayer(), event.getItem(), event.getAction(), event.isCancelled());
	}

	/** Log EE tools. */
	private void itemLogUse(Player player, ItemStack item, Action action, boolean cancelled) {
		ItemStack a = player.getItemInHand();
		if (a == null) return;

		if (cancelled && (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR)) cancelled = false;
		
		int id = a.getTypeId();
		
		if (id == 27530 || id == 27531)
			logUse("EEAmulet", player, id, cancelled);
		else if (id == 27532 || id == 27534 || id == 27536 || id == 27537 || id == 27574 || id == 27584 || id == 27593)
			logUse("EERing", player, id, cancelled);
		else if (inRange(id, 27543, 27548) || id == 27555){
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				logUse("EEDmTool", player, id, cancelled);
		} else if (inRange(id, 27564, 27573)){
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				logUse("EERmTool", player, id, cancelled);
		} else if (id == 27527 || id == 27556 || id == 27535)
			logUse("EEDestructive", player, id, cancelled);
		else if (id == 27538 || id == 27553 || id == 27562 || id == 27583 || id == 27585 || id == 27592)
			logUse("EEMisc", player, id, cancelled);
	}
	
	private void logUse(String logname, Player player, int id, boolean cancelled){
		if (!isLoggable(logname)) return;
		Location loc = player.getLocation();
		FileLog filelog = FileLog.getLogOrMake(logname, true, false);
		filelog.log("[" + player.getName() + "][" + player.getWorld().getName() +
				" - " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "] used (" + id + ") `" + NameProcessor.getEEName(id) + "`" + (cancelled ? " (Action was cancelled)":""));
	}
	
	private boolean isLoggable(@NonNull String type) {
		if (type.equals("EERing")) return Logger.LogRings;
		if (type.equals("EEDmTool")) return Logger.LogDMTools;
		if (type.equals("EERmTool")) return Logger.LogRMTools;
		if (type.equals("EEAmulet")) return Logger.LogAmulets;
		if (type.equals("EEMisc")) return Logger.LogEEMisc;
		if (type.equals("EEDestructive")) return Logger.LogEEDestructive;

		return false;
	}

	private boolean inRange(int stack, int from, int to) {
		return (stack >= from && stack <= to);
	}
}
