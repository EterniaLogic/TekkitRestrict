package nl.taico.tekkitrestrict.eepatch.ringlisteners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.ring.EEArcaneRingEvent;

public class EEArcaneRingListener implements Listener {
		
	@EventHandler
	public void ArcaneRingEvent(EEArcaneRingEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.arcanering")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.arcanering){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				if (!name.equals("negatefalldamage"))
					player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Ring of Arcana.");
				else {
					if (!negate.contains(player.getName())){
						player.sendMessage(ChatColor.RED + "You cannot use the Ring of Arcana to negate fall damage.");
						negate.add(player.getName());
					}
				}
				return;
			}
		}
	}
	
	private ArrayList<String> negate = new ArrayList<String>();

}
