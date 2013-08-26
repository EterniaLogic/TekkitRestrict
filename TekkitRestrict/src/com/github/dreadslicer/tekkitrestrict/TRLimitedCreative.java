package com.github.dreadslicer.tekkitrestrict;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Listeners;
import com.github.dreadslicer.tekkitrestrict.annotations.Safe;

public class TRLimitedCreative {
	/** Checks bypass creative permission. */
	@Safe(allownull = false)
	public static boolean handleCreativeInvClick(InventoryClickEvent event) {
		if (!Listeners.UseLimitedCreative) return false;
		Player player = (Player) event.getWhoClicked();
		if (player.getGameMode() != GameMode.CREATIVE) return false;
		if (player.hasPermission("tekkitrestrict.bypass.creative")) return false;
		
		try {
			if (event.getView().getTopInventory() != null){
				String invname = event.getView().getTopInventory().getName();
				if (Listeners.BlockCreativeContainer) {
					if (!invname.equals("container.inventory")) {
						player.sendMessage(ChatColor.RED + "[TRLimitedCreative] You may not interact with other inventories.");
						event.setCancelled(true);
						return true;
					}
				}
			}
		} catch (Exception ex) {
			tekkitrestrict.log.warning("An error occured in TRLimitedCreative ContainerCheck! Please inform the author.");
			Log.Exception(ex, false);
		}
		//Dont ban items in creative inventory. They cant rightclick anyway and it causes crashes.
		/*
		try {
			// determine of player attempted to pick an item up...
			ItemStack ccc = event.getCurrentItem();
			if (ccc == null) return false;
			boolean banned = false;
			if (Global.useNewBanSystem){
				if (TRCacheItem2.isBanned(player, "creative", ccc.getTypeId(), ccc.getDurability())) banned = true;
			} else {
				if (TRNoItem.isItemBannedInCreative(player, ccc.getTypeId(), ccc.getDurability(), false)) banned = true;
			}
			
			if (banned) {
				player.sendMessage(ChatColor.RED + "[TRLimitedCreative] You cannot obtain/modify this item type!");
				event.setCancelled(true);
				return true;
			}
			
		} catch (Exception ex) {
			tekkitrestrict.log.warning("An error occured in TRLimitedCreative BannedCreativeItem! Please inform the author.");
			Log.Exception(ex, false);
		}
		*/
		return false;
	}
}
