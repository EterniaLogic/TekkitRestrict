package nl.taico.tekkitrestrict.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onTeleport(PlayerTeleportEvent event){
		event.getPlayer().closeInventory();
	}
}
