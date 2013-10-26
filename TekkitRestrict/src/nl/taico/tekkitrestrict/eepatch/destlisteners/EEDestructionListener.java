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
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.catalyticlens")) return;
		
		int action = event.getExtraInfo().ordinal();
		
		for (Integer blocked : EEPSettings.dest3){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to do this with the Catalytic Lens!");
				return;
			}
		}
	}
	
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
