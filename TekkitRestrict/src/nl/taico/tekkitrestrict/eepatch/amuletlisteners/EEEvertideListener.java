package nl.taico.tekkitrestrict.eepatch.amuletlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.amulet.EEEvertideAmuletEvent;

public class EEEvertideListener implements Listener {
	@EventHandler
	public void onEvent(EEEvertideAmuletEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.volcaniteamulet")) return;
		
		int action = event.getExtraInfo().ordinal();
		
		for (Integer blocked : EEPSettings.evertide){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Evertide Amulet!");
				return;
			}
		}
	}
}
