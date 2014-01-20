package nl.taico.tekkitrestrict.eepatch.otherlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.other.EEWOFTEvent;

public class EEWatchListener implements Listener {
	@EventHandler
	public void onWatch(EEWOFTEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.watch")) return;
		
		if (EEPSettings.watch.contains(event.getExtraInfo())){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Watch of Flowing Time!");
			return;
		}
	}
}
