package com.github.dreadslicer.tekkitrestrict.eepatch.ringlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.dreadslicer.tekkitrestrict.eepatch.EEPSettings;

import ee.events.ring.EEHarvestRingEvent;

public class EEHarvestRingListener implements Listener {
	@EventHandler
	public void HarvestRingEvent(EEHarvestRingEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.harvestring")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.harvestring){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Harvest Godess Band.");
				return;
			}
		}
	}
}
