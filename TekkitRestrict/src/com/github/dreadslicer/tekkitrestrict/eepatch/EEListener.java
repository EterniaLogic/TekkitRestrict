package com.github.dreadslicer.tekkitrestrict.eepatch;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ee.events.EEEnums.EEAction2;
import ee.events.EEToolEvent;

public class EEListener implements Listener {
	@EventHandler
	public void onToolEvent(EEToolEvent event){
		Player player = event.getPlayer();
		EEAction2 action = event.getExtraInfo();
	}
}
