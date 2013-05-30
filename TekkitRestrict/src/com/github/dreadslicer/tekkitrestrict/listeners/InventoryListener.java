package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.dreadslicer.tekkitrestrict.commands.TRCommandAlc;

public class InventoryListener implements Listener {
	@EventHandler
	private void onInventoryClose(InventoryCloseEvent event){
		TRCommandAlc.setPlayerInv2((Player) event.getPlayer());
	}
	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event){
		setInv(event.getPlayer());
	}
	@EventHandler
	private void onPlayerKick(PlayerKickEvent event){
		setInv(event.getPlayer());
	}
	private void setInv(Player player){
		TRCommandAlc.setPlayerInv(player);
	}
}
