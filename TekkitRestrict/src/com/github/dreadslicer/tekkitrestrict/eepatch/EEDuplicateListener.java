package com.github.dreadslicer.tekkitrestrict.eepatch;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ee.events.EEDuplicateEvent;

public class EEDuplicateListener implements Listener {
	@EventHandler
	public void onDuplicateEvent(EEDuplicateEvent event){
		if (!EEPSettings.allowOreDupe) event.setCancelled(true);
	}
}
