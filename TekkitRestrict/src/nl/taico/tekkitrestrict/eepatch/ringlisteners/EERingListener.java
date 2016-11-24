package nl.taico.tekkitrestrict.eepatch.ringlisteners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import ee.events.EEEnums.EERingAction;
import ee.events.ring.EEArcaneRingEvent;
import ee.events.ring.EEArchangelRingEvent;
import ee.events.ring.EEBHBEvent;
import ee.events.ring.EEHarvestRingEvent;
import ee.events.ring.EEIgnitionRingEvent;
import ee.events.ring.EERingEvent;
import ee.events.ring.EESWRingEvent;
import ee.events.ring.EEVoidRingEvent;
import ee.events.ring.EEZeroRingEvent;

public class EERingListener implements Listener {
	@EventHandler
	public void onRingEvent(EERingEvent event){
		if (event instanceof EEArcaneRingEvent)
			arcaneRing((EEArcaneRingEvent) event);
		else if (event instanceof EEArchangelRingEvent)
			archangelRing((EEArchangelRingEvent) event);
		else if (event instanceof EEBHBEvent)
			bhbRing((EEBHBEvent) event);
		else if (event instanceof EEHarvestRingEvent)
			harvestRing((EEHarvestRingEvent) event);
		else if (event instanceof EEIgnitionRingEvent)
			ignitionRing((EEIgnitionRingEvent) event);
		else if (event instanceof EESWRingEvent)
			swrgRing((EESWRingEvent) event);
		else if (event instanceof EEVoidRingEvent)
			voidRing((EEVoidRingEvent) event);
		else if (event instanceof EEZeroRingEvent)
			zeroRing((EEZeroRingEvent) event);
	}
	
	public void zeroRing(EEZeroRingEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.zeroring")) return;
		
		final EERingAction action = event.getExtraInfo();
		
		if (EEPSettings.zeroring.contains(action)){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to " + action.getName() + " the Zero Ring.");
			return;
		}
	}
	
	public void voidRing(EEVoidRingEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.voidring")) return;
		
		final EERingAction action = event.getExtraInfo();
		
		if (EEPSettings.voidring.contains(action)){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to " + action.getName() + " the Void Ring.");
			return;
		}
	}
	
	private ArrayList<String> swrgNegate = new ArrayList<String>();
	
	public void swrgRing(EESWRingEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.swiftwolfring")) return;
		
		final EERingAction action = event.getExtraInfo();
		
		if (EEPSettings.flyring.contains(action)){
			event.setCancelled(true);
			final String name = event.getExtraInfo().getName();
			if (!name.equalsIgnoreCase("negatefalldamage"))
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " Swiftwolf's Rending Gale.");
			else {
				if (!swrgNegate.contains(player.getName())){
					player.sendMessage(ChatColor.RED + "You cannot use Swiftwolf's Rending Gale to negate fall damage.");
					swrgNegate.add(player.getName());
				} else {
					player.sendMessage(ChatColor.RED + "You cannot use Swiftwolf's Rending Gale to negate fall damage.");
				}
			}
			return;
		}
	}
	
	public void ignitionRing(EEIgnitionRingEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.ignitionring")) return;
		
		final EERingAction action = event.getExtraInfo();
		
		if (EEPSettings.firering.contains(action)){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to " + action.getName() + " the Ring of Ignition.");
			return;
		}
	}
	
	public void harvestRing(EEHarvestRingEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.harvestring")) return;
		
		final EERingAction action = event.getExtraInfo();
		
		if (EEPSettings.harvestring.contains(action)){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to " + action.getName() + " the Harvest Godess Band.");
			return;
		}
	}
	
	public void bhbRing(EEBHBEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.blackholeband")) return;
		
		EERingAction action = event.getExtraInfo();
		
		if (EEPSettings.blackholeband.contains(action)){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to " + action.getName() + " the Black Hole Band.");
			return;
		}
	}
	
	public void archangelRing(EEArchangelRingEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.archangelring")) return;
		
		final EERingAction action = event.getExtraInfo();
		
		if (EEPSettings.archangelring.contains(action)){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to " + action.getName() + " the Zero Ring.");
			return;
		}
	}
	
	public void arcaneRing(EEArcaneRingEvent event){
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.arcanering")) return;
		
		final EERingAction action = event.getExtraInfo();
		
		if (EEPSettings.arcanering.contains(action)){
			event.setCancelled(true);
			final String name = event.getExtraInfo().getName();
			if (!name.equalsIgnoreCase("negatefalldamage"))
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Ring of Arcana.");
			else {
				if (!arcaneNegate.contains(player.getName())){
					player.sendMessage(ChatColor.RED + "You cannot use the Ring of Arcana to negate fall damage.");
					arcaneNegate.add(player.getName());
				} else {
					player.sendMessage(ChatColor.RED + "You cannot use the Ring of Arcana to negate fall damage.");
				}
			}
			return;
		}
	}
	
	private ArrayList<String> arcaneNegate = new ArrayList<String>();
}
