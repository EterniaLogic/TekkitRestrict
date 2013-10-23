package nl.taico.tekkitrestrict.eepatch.destlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.destruction.EEDestructionCatalystEvent;

public class EEDest1Listener implements Listener {
		
	@EventHandler
	public void EEDest1Event(EEDestructionCatalystEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.destructioncatalyst")) return;
		
		int action = event.getExtraInfo().ordinal();
		
		for (Integer blocked : EEPSettings.dest1){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Destruction Catalyst!");
				return;
			}
		}
	}

}
