package nl.taico.tekkitrestrict.listeners;

import nl.taico.tekkitrestrict.functions.TRNoItem;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemPickupListener implements Listener {
	
	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent ev) {
		if(ev.isCancelled()) return;
		
		ItemStack item = ev.getItem().getItemStack();
		Player player = ev.getPlayer();
		
		boolean bypassn = player.hasPermission("tekkitrestrict.bypass.noitem");
		boolean bypassc = player.hasPermission("tekkitrestrict.bypass.creative");
		boolean isCreative = (player.getGameMode() == GameMode.CREATIVE);
		
		String banned = null;
		int id = item.getTypeId();
		int data = item.getDurability();
		
		if (isCreative){
			if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
			if (banned == null && !bypassc) banned = TRNoItem.isItemBannedInCreative(player, id, data, false);
		} else {
			if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
		}
		
		if (banned != null) {
			ev.setCancelled(true);
		}
	}
	
}