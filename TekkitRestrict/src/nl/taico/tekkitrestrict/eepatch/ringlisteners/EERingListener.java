package nl.taico.tekkitrestrict.eepatch.ringlisteners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import nl.taico.tekkitrestrict.eepatch.EEPSettings;

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
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.zeroring")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.zeroring){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Zero Ring.");
				return;
			}
		}
	}
	
	public void voidRing(EEVoidRingEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.voidring")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.voidring){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Void Ring.");
				return;
			}
		}
	}
	
	private ArrayList<String> swrgNegate = new ArrayList<String>();
	
	public void swrgRing(EESWRingEvent event){
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
					if (!swrgNegate.contains(player.getName())){
						player.sendMessage(ChatColor.RED + "You cannot use Swiftwolf's Rending Gale to negate fall damage.");
						swrgNegate.add(player.getName());
					}
				}
				return;
			}
		}
	}
	
	public void ignitionRing(EEIgnitionRingEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.ignitionring")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.firering){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Ring of Ignition.");
				return;
			}
		}
	}
	
	public void harvestRing(EEHarvestRingEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.harvestring")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.harvestring){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Harvest Godess Band.");
				return;
			}
		}
	}
	
	public void bhbRing(EEBHBEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.blackholeband")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.blackholeband){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Black Hole Band.");
				return;
			}
		}
	}
	
	public void archangelRing(EEArchangelRingEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.blockactions.archangelring")) return;
		
		int action = event.getExtraInfo().ordinal();
		String name = event.getExtraInfo().getName();
		
		for (Integer blocked : EEPSettings.archangelring){
			if (blocked == null) continue;
			if (blocked.intValue() == action){
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You are not allowed to " + name + " the Zero Ring.");
				return;
			}
		}
	}
	
	public void arcaneRing(EEArcaneRingEvent event){
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
					if (!arcaneNegate.contains(player.getName())){
						player.sendMessage(ChatColor.RED + "You cannot use the Ring of Arcana to negate fall damage.");
						arcaneNegate.add(player.getName());
					}
				}
				return;
			}
		}
	}
	
	private ArrayList<String> arcaneNegate = new ArrayList<String>();
}
