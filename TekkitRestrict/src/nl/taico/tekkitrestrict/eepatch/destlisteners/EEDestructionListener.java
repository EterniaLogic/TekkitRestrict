package nl.taico.tekkitrestrict.eepatch.destlisteners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.destruction.EEDestructionCatalystEvent;
import ee.events.destruction.EEDestructionToolEvent;
import ee.events.destruction.EEHyperCatalystEvent;
import ee.events.destruction.EEHyperkineticLensEvent;

public class EEDestructionListener implements Listener {
	@EventHandler
	public void onDestructionEvent(EEDestructionToolEvent event){
		if (event instanceof EEDestructionCatalystEvent)
			EEDest1Event((EEDestructionCatalystEvent) event);
		else if (event instanceof EEHyperkineticLensEvent)
			EEDest2Event((EEHyperkineticLensEvent) event);
		else if (event instanceof EEHyperCatalystEvent)
			EEDest3Event((EEHyperCatalystEvent) event);
	}
	
	public void EEDest3Event(EEHyperCatalystEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.catalyticlens")) return;
		
		if (EEPSettings.dest3.contains(event.getExtraInfo())){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Catalytic Lens!");
		}
	}
	
	public void EEDest2Event(EEHyperkineticLensEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.hyperkineticlens")) return;
		
		if (EEPSettings.dest2.contains(event.getExtraInfo())){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Hyperkinetic Lens!");
		}
	}
	
	public void EEDest1Event(EEDestructionCatalystEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.destructioncatalyst")) return;
		
		if (EEPSettings.dest1.contains(event.getExtraInfo())){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Destruction Catalyst!");
		}
	}
}
