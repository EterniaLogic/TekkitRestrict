package nl.taico.tekkitrestrict.listeners;

import nl.taico.tekkitrestrict.FileLog;
import nl.taico.tekkitrestrict.NameProcessor;
import nl.taico.tekkitrestrict.TRConfigCache.Logger;
import nl.taico.tekkitrestrict.TekkitRestrict;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {
	public static boolean errorInteract = false;

	private static FileLog EEAmulet, EERing, EEDmTool, EERmTool, EEDestructive, EEMisc;

	private boolean inRange(int stack, int from, int to) {
		return ((stack >= from) && (stack <= to));
	}

	/** Log EE tools. */
	private void itemLogUse(Player player, ItemStack item, Action action, boolean cancelled) {
		final ItemStack a = player.getItemInHand();
		if (a == null) return;

		if (cancelled && ((action == Action.LEFT_CLICK_AIR) || (action == Action.RIGHT_CLICK_AIR))) cancelled = false;

		int id = a.getTypeId();

		if ((id == 27530) || (id == 27531)){
			if (!Logger.LogAmulets) return;
			logUse(EEAmulet == null ? (EEAmulet = FileLog.getLogOrMake("EEAmulet", false)) : EEAmulet, player, id, cancelled);
		} else if ((id == 27532) || (id == 27534) || (id == 27536) || (id == 27537) || (id == 27574) || (id == 27584) || (id == 27593)) {
			if (!Logger.LogRings) return;
			logUse(EERing == null ? (EERing = FileLog.getLogOrMake("EERing", false)) : EERing, player, id, cancelled);
		} else if (inRange(id, 27543, 27548) || (id == 27555)){
			if (!Logger.LogDMTools) return;
			if ((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK))
				logUse(EEDmTool == null ? (EEDmTool = FileLog.getLogOrMake("EEDmTool", false)) : EEDmTool, player, id, cancelled);
		} else if (inRange(id, 27564, 27573)){
			if (!Logger.LogRMTools) return;
			if ((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK))
				logUse(EERmTool == null ? (EERmTool = FileLog.getLogOrMake("EERmTool", false)) : EERmTool, player, id, cancelled);
		} else if ((id == 27527) || (id == 27556) || (id == 27535)){
			if (!Logger.LogEEDestructive) return;
			logUse(EEDestructive == null ? (EEDestructive = FileLog.getLogOrMake("EEDestructive", false)) : EEDestructive, player, id, cancelled);
		} else if ((id == 27538) || (id == 27553) || (id == 27562) || (id == 27583) || (id == 27585) || (id == 27592)){
			if (!Logger.LogEEMisc) return;
			logUse(EEMisc == null ? (EEMisc = FileLog.getLogOrMake("EEMisc", false)) : EEMisc, player, id, cancelled);
		}
	}

	private void logUse(FileLog fl, Player player, int id, boolean cancelled){
		final Location loc = player.getLocation();

		fl.log("[" + player.getName() + "][" + player.getWorld().getName() +
				" - " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "] used (" + id + ") `" + NameProcessor.getEEName(id) + "`" + (cancelled ? " (Action was cancelled)":""));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onInteract_Logger(PlayerInteractEvent event){
		if (!TekkitRestrict.EEEnabled) return;
		if (event.getPlayer() == null) return;

		itemLogUse(event.getPlayer(), event.getItem(), event.getAction(), event.isCancelled());
	}
}
