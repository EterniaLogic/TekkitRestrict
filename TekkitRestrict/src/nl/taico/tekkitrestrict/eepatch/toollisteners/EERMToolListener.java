package nl.taico.tekkitrestrict.eepatch.toollisteners;

import java.util.ArrayList;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import ee.events.EEEnums.EEAction2;
import ee.events.rm.EERMAxeEvent;
import ee.events.rm.EERMHammerEvent;
import ee.events.rm.EERMHoeEvent;
import ee.events.rm.EERMPickaxeEvent;
import ee.events.rm.EERMShearsEvent;
import ee.events.rm.EERMSpadeEvent;
import ee.events.rm.EERMSwordEvent;
import ee.events.rm.EERMToolEvent;
import ee.events.rm.EERedKatarEvent;
import ee.events.rm.EERedMorningStarEvent;

public class EERMToolListener implements Listener {
	@EventHandler
	public void onEvent(EERMToolEvent event){
		final Player player = event.getPlayer();
		final ArrayList<EEAction2> toSearch;
		final String name;

		if (event instanceof EERMPickaxeEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.rmpickaxe")) return;
			toSearch = EEPSettings.rmpick;
			name = "Red Matter Pickaxe";
		} else if (event instanceof EERMSpadeEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.rmshovel")) return;
			toSearch = EEPSettings.rmshovel;
			name = "Red Matter Shovel";
		} else if (event instanceof EERMSwordEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.rmsword")) return;
			toSearch = EEPSettings.rmsword;
			name = "Red Matter Sword";
		} else if (event instanceof EERMAxeEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.rmaxe")) return;
			toSearch = EEPSettings.rmaxe;
			name = "Red Matter Axe";
		} else if (event instanceof EERMHammerEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.rmhammer")) return;
			toSearch = EEPSettings.rmhammer;
			name = "Red Matter Hammer";
		} else if (event instanceof EERMHoeEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.rmhoe")) return;
			toSearch = EEPSettings.rmhoe;
			name = "Red Matter Hoe";
		} else if (event instanceof EERMShearsEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.rmshears")) return;
			toSearch = EEPSettings.rmshears;
			name = "Red Matter Shears";
		} else if (event instanceof EERedKatarEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.katar")) return;
			toSearch = EEPSettings.katar;
			name = "Red Katar";
		} else if (event instanceof EERedMorningStarEvent){
			if (player.hasPermission("tekkitrestrict.bypass.blockactions.morningstar")) return;
			toSearch = EEPSettings.morningstar;
			name = "Red Morning Star";
		} else {
			return;
		}

		if (toSearch.contains(event.getExtraInfo())){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to do this with the "+name+"!");
		}
	}
}
