package nl.taico.tekkitrestrict.listeners;

import nl.taico.tekkitrestrict.TRConfigCache.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropListener implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onDropItem(PlayerDropItemEvent event) {
		if (event.getPlayer() == null) return;

		if (Listeners.UseLimitedCreative && (event.getPlayer().getGameMode() == GameMode.CREATIVE)){
			if (!event.getPlayer().hasPermission("tekkitrestrict.bypass.creative")) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "[TRLimitedCreative] You cannot drop items in creative mode!");
				return;
			}
		}
	}
}
