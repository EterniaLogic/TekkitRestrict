package com.github.dreadslicer.tekkitrestrict.eepatch.ringlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.dreadslicer.tekkitrestrict.eepatch.EEPSettings;

import ee.events.ring.EEIgnitionRingEvent;

public class EEIgnitionRingListener implements Listener {
	@EventHandler
	public void IgnitionRingEvent(EEIgnitionRingEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.ignitionring")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.firering){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Ring of Ignition.");
				return;
			}
		}
	}
}
