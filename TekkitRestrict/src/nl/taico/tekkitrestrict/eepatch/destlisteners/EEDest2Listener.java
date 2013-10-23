package nl.taico.tekkitrestrict.eepatch.destlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.destruction.EEHyperkineticLensEvent;

public class EEDest2Listener implements Listener {
		
	@EventHandler
	public void EEDest2Event(EEHyperkineticLensEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.hyperkineticlens")) return;
		
		int action = event.getExtraInfo().ordinal();
		
		for (Integer blocked : EEPSettings.dest2){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Hyperkinetic Lens!");
				return;
			}
		}
	}

}
