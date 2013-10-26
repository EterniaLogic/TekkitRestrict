package nl.taico.tekkitrestrict.eepatch.amuletlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.amulet.EEVolcaniteAmuletEvent;

public class EEVolcaniteListener implements Listener {
	@EventHandler
	public void onEvent(EEVolcaniteAmuletEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.volcaniteamulet")) return;
		
		int action = event.getExtraInfo().ordinal();
		
		for (Integer blocked : EEPSettings.volcanite){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Volcanite Amulet!");
				return;
			}
		}
	}
}
