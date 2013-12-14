package nl.taico.tekkitrestrict.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.NameProcessor;
import nl.taico.tekkitrestrict.TRLogger;
import nl.taico.tekkitrestrict.TRConfigCache.Logger;

import ee.events.EEEvent;
import ee.events.amulet.EEAmuletEvent;

public class EEListener implements Listener {
	//IMPORTANT TODO WIP
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEEEvent(EEEvent e){
		if (e instanceof EEAmuletEvent){
			EEAmuletEvent j = (EEAmuletEvent) e;
			logUse("EEAmulet", j.getPlayer(), j.getAmulet().id);
		}
	}
	
	private void logUse(String logname, Player player, int id){
		if (!isLoggable(logname)) return;
		Location loc = player.getLocation();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		TRLogger.Log(logname, "[" + player.getName() + "][" + player.getWorld().getName() +
				" - " + x + "," + y + "," + z + "] used (" + id + ") `" + NameProcessor.getEEName(id) + "`");
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
}
