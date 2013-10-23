package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.dreadslicer.tekkitrestrict.TRNoDupe;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.DupeType;

public class TeleportListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onTeleport(PlayerTeleportEvent event){
		event.getPlayer().closeInventory();
		TRNoDupe.handleDupe(event.getPlayer(), DupeType.teleport, 0, 0);
	}
}
