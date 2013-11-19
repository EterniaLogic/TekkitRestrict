package nl.taico.tekkitrestrict.eepatch.otherlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.other.EETransmutationTableEvent;

public class EETransmutionListener implements Listener {
	@EventHandler
	public void onTrans(EETransmutationTableEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.transmutiontablet")) return;
		
		if (EEPSettings.trans.contains(event.getExtraInfo())){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Transmution Tablet!");
			return;
		}
	}
}
