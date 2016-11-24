package nl.taico.tekkitrestrict.eepatch.otherlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.other.EEPhilosopherStoneEvent;

public class EEPhilosopherListener implements Listener {
	@EventHandler
	public void onPhilo(EEPhilosopherStoneEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.philosopherstone")) return;
		
		if (EEPSettings.phil.contains(event.getExtraInfo())){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Philosopher Stone!");
			return;
		}
	}
}
