package com.github.dreadslicer.tekkitrestrict.eepatch.destlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.dreadslicer.tekkitrestrict.eepatch.EEPSettings;

import ee.events.destruction.EEHyperCatalystEvent;

public class EEDest3Listener implements Listener {
		
	@EventHandler
	public void EEDest3Event(EEHyperCatalystEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.catalyticlens")) return;
		
		int action = event.getExtraInfo().ordinal();
		
		for (Integer blocked : EEPSettings.dest3){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Catalytic Lens!");
				return;
			}
		}
	}

}
