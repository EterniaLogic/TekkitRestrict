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
import ee.events.destruction.EEDestructionToolEvent;
import ee.events.dm.EEDMToolEvent;
import ee.events.ring.EERingEvent;
import ee.events.rm.EERMToolEvent;

public class EEListener implements Listener {
	//IMPORTANT TODO WIP
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEEEvent(EEEvent e){
		if (e instanceof EEAmuletEvent){
			EEAmuletEvent j = (EEAmuletEvent) e;
			logUse("EEAmulet", j.getPlayer(), j.getAmulet().id);
		} else if (e instanceof EERMToolEvent){
			EERMToolEvent j = (EERMToolEvent) e;
			logUse("EERmTool", j.getPlayer(), j.getTool().id);
		} else if (e instanceof EEDMToolEvent){
			EEDMToolEvent j = (EEDMToolEvent) e;
			logUse("EEDmTool", j.getPlayer(), j.getTool().id);
		} else if (e instanceof EEDestructionToolEvent){
			EEDestructionToolEvent j = (EEDestructionToolEvent) e;
			logUse("EEDmTool", j.getPlayer(), j.getTool().id);
		} else if (e instanceof EERingEvent){
			EERingEvent j = (EERingEvent) e;
			logUse("EERing", j.getPlayer(), j.getRing().id);
		} else {
			//IMPORTANT TODO continue this
		}
	}
	
	private void logUse(String logname, Player player, int id){
		if (!isLoggable(logname)) return;
		Location loc = player.getLocation();
		TRLogger.Log(logname, "[" + player.getName() + "][" + player.getWorld().getName() +
				" - " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "] used (" + id + ") `" + NameProcessor.getEEName(id) + "`");
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
