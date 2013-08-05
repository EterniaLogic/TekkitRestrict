package com.github.dreadslicer.tekkitrestrict.eepatch;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.dreadslicer.tekkitrestrict.Util;

import ee.events.EEChargeEvent;

public class EEChargeListener implements Listener {
	@EventHandler
	public void onCharge(EEChargeEvent event){
		Player player = (Player) event.getHuman().getBukkitEntity();
		if (player.hasPermission("tekkitrestrict.bypass.maxcharge")) return;
		
		int old = event.getOldChargeLevel();
		int nw = event.getNewChargeLevel();
		if (old > nw) return; //Discharge always allowed

		int id = event.getItem().id;
		
		Integer charge = EEPSettings.MaxCharge.get(id);
		
		if (charge == null){
			if (Util.hasPermission(player, "maxcharge."+id+"."+old)){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to charge this tool to level " + old + " or above.");
			}
			return;
		}
		
		if (nw > charge){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to charge this tool to level " + charge + " or above.");
		}
	}
}
