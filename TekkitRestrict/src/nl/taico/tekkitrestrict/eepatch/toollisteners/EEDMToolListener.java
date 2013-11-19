package nl.taico.tekkitrestrict.eepatch.toollisteners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.EEEnums.EEAction2;
import ee.events.dm.EEDMAxeEvent;
import ee.events.dm.EEDMHammerEvent;
import ee.events.dm.EEDMHoeEvent;
import ee.events.dm.EEDMPickaxeEvent;
import ee.events.dm.EEDMShearsEvent;
import ee.events.dm.EEDMSpadeEvent;
import ee.events.dm.EEDMSwordEvent;
import ee.events.dm.EEDMToolEvent;

public class EEDMToolListener implements Listener {
	@EventHandler
	public void onEvent(EEDMToolEvent event){
		Player player = event.getPlayer();
		ArrayList<EEAction2> toSearch = null;
		String name = null;
		
		if (event instanceof EEDMPickaxeEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.dmpickaxe")) return;
			toSearch = EEPSettings.dmpick;
			name = "Dark Matter Pickaxe";
		} else if (event instanceof EEDMSpadeEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.dmshovel")) return;
			toSearch = EEPSettings.dmshovel;
			name = "Dark Matter Shovel";
		} else if (event instanceof EEDMSwordEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.dmsword")) return;
			toSearch = EEPSettings.dmsword;
			name = "Dark Matter Sword";
		} else if (event instanceof EEDMAxeEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.dmaxe")) return;
			toSearch = EEPSettings.dmaxe;
			name = "Dark Matter Axe";
		} else if (event instanceof EEDMHammerEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.dmhammer")) return;
			toSearch = EEPSettings.dmhammer;
			name = "Dark Matter Hammer";
		} else if (event instanceof EEDMHoeEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.dmhoe")) return;
			toSearch = EEPSettings.dmhoe;
			name = "Dark Matter Hoe";
		} else if (event instanceof EEDMShearsEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.dmshears")) return;
			toSearch = EEPSettings.dmshears;
			name = "Dark Matter Shears";
		} else {
			return;
		}
		
		EEAction2 action = event.getExtraInfo();
		for (EEAction2 blocked : toSearch){
			if (blocked == null) continue;
			if (blocked == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to do this with the "+name+"!");
				return;
			}
		}
	}
}
