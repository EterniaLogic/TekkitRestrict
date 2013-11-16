package nl.taico.tekkitrestrict.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import nl.taico.tekkitrestrict.TRConfigCache.Listeners;

public class DropListener implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		
		if (Listeners.UseLimitedCreative && player.getGameMode() == GameMode.CREATIVE){
			if (!player.hasPermission("tekkitrestrict.bypass.creative")) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "[TRLimitedCreative] You cannot drop items!");
				return;
			}
		}
	}
}
