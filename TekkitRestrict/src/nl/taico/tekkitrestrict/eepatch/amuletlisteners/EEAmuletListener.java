package nl.taico.tekkitrestrict.eepatch.amuletlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.amulet.EEAmuletEvent;
import ee.events.amulet.EEEvertideAmuletEvent;
import ee.events.amulet.EEVolcaniteAmuletEvent;

public class EEAmuletListener implements Listener{
	@EventHandler
	public void onAmuletEvent(EEAmuletEvent event){
		if (event instanceof EEEvertideAmuletEvent)
			onEvertideEvent((EEEvertideAmuletEvent) event);
		else if (event instanceof EEVolcaniteAmuletEvent)
			onVolcaniteEvent((EEVolcaniteAmuletEvent) event);
	}
	
	public void onVolcaniteEvent(EEVolcaniteAmuletEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.volcaniteamulet")) return;
		
		if (EEPSettings.volcanite.contains(event.getExtraInfo())){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Volcanite Amulet!");
		}
	}
	
	public void onEvertideEvent(EEEvertideAmuletEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.volcaniteamulet")) return;
		
		if (EEPSettings.evertide.contains(event.getExtraInfo())){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Evertide Amulet!");
		}
	}

}
