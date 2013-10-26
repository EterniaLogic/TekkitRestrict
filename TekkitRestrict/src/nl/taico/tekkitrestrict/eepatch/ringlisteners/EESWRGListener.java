package nl.taico.tekkitrestrict.eepatch.ringlisteners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.ring.EESWRingEvent;

public class EESWRGListener implements Listener {

	private ArrayList<String> negate = new ArrayList<String>();
	
	@EventHandler
	public void SWRGEvent(EESWRingEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.swiftwolfring")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.flyring){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				if (!name.equals("negatefalldamage"))
					player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " Swiftwolf's Rending Gale.");
				else {
					if (!negate.contains(player.getName())){
						player.sendMessage(ChatColor.RED + "You cannot use Swiftwolf's Rending Gale to negate fall damage.");
						negate.add(player.getName());
					}
				}
				return;
			}
		}
	}
}
