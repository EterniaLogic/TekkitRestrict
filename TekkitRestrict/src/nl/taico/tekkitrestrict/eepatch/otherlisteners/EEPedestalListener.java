package nl.taico.tekkitrestrict.eepatch.otherlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.EEEnums.EEPedestalAction;
import ee.events.blocks.EEPedestalEvent;

public class EEPedestalListener implements Listener {
	@EventHandler
	public void onPedestal(EEPedestalEvent event){
		Player player = event.getActivationPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.pedestal")) return;

		if (EEPSettings.pedestal.contains(event.getAction())){
			event.setCancelled(true);
			
			if (event.getAction() == EEPedestalAction.Activate){
				player.sendMessage(ChatColor.RED + "You are not allowed to activate pedestals!");
			} else {
				event.getPedestal().setActivated(false);
				player.sendMessage(ChatColor.RED + "You are not allowed to use this item on a pedestal!");
			}
			
			return;
		}
	}
}
