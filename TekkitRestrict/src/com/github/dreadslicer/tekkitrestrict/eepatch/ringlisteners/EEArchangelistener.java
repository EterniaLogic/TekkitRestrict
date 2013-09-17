package com.github.dreadslicer.tekkitrestrict.eepatch.ringlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.dreadslicer.tekkitrestrict.eepatch.EEPSettings;

import ee.events.ring.EEArchangelRingEvent;

public class EEArchangelistener implements Listener {
	@EventHandler
	public void ArchangelRingEvent(EEArchangelRingEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.archangelring")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.archangelring){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Zero Ring.");
				return;
			}
		}
	}

}
