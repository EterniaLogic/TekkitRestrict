package nl.taico.tekkitrestrict.eepatch.ringlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.ring.EEBHBEvent;

public class EEBHBListener implements Listener {
	@EventHandler
	public void BHBEvent(EEBHBEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.blackholeband")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.blackholeband){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Black Hole Band.");
				return;
			}
		}
	}
}
