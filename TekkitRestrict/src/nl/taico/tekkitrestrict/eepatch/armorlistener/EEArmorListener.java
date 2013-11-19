package nl.taico.tekkitrestrict.eepatch.armorlistener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.EEEnums.EEArmorAction;
import ee.events.armor.EEArmorEvent;

public class EEArmorListener implements Listener {
	@EventHandler
	public void onDestructionEvent(EEArmorEvent event){
		Player player = event.getPlayer();
		String s = event.getExtraInfo().isOffensive()?"Offensive":"Movement";
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.armor."+s.toLowerCase())) return;
		
		EEArmorAction action = event.getExtraInfo();
		
		for (EEArmorAction blocked : EEPSettings.armor){
			if (blocked == null) continue;
			if (blocked == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to use this Gem Armor "+s+" Power!");
				return;
			}
		}
	}
}
