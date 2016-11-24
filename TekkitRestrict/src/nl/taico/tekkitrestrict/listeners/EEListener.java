package nl.taico.tekkitrestrict.listeners;

import nl.taico.tekkitrestrict.NameProcessor;
import nl.taico.tekkitrestrict.TRConfigCache.Logger;
import nl.taico.tekkitrestrict.TRLogger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import ee.events.EEEvent;
import ee.events.amulet.EEAmuletEvent;
import ee.events.destruction.EEDestructionToolEvent;
import ee.events.dm.EEDMToolEvent;
import ee.events.ring.EERingEvent;
import ee.events.rm.EERMToolEvent;

public class EEListener implements Listener {
	private void logUse(String logname, Player player, int id){
		Location loc = player.getLocation();
		TRLogger.Log(logname, "[" + player.getName() + "][" + player.getWorld().getName() +
				" - " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "] used (" + id + ") `" + NameProcessor.getEEName(id) + "`");
	}

	//IMPORTANT TODO WIP
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEEEvent(EEEvent e){
		if (e instanceof EEAmuletEvent){
			if (!Logger.LogAmulets) return;
			EEAmuletEvent j = (EEAmuletEvent) e;
			logUse("EEAmulet", j.getPlayer(), j.getAmulet().id);
		} else if (e instanceof EERMToolEvent){
			if (!Logger.LogRMTools) return;
			EERMToolEvent j = (EERMToolEvent) e;
			logUse("EERmTool", j.getPlayer(), j.getTool().id);
		} else if (e instanceof EEDMToolEvent){
			if (!Logger.LogDMTools) return;
			EEDMToolEvent j = (EEDMToolEvent) e;
			logUse("EEDmTool", j.getPlayer(), j.getTool().id);
		} else if (e instanceof EEDestructionToolEvent){
			if (!Logger.LogEEDestructive) return;
			EEDestructionToolEvent j = (EEDestructionToolEvent) e;
			logUse("EEDmTool", j.getPlayer(), j.getTool().id);
		} else if (e instanceof EERingEvent){
			if (!Logger.LogRings) return;
			EERingEvent j = (EERingEvent) e;
			logUse("EERing", j.getPlayer(), j.getRing().id);
		} else {
			if (!Logger.LogEEMisc) return;
			//IMPORTANT TODO continue this
		}
	}
}
