package com.github.dreadslicer.tekkitrestrict;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Global;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Listeners;

public class TRLimitedCreative {
	public static boolean handleCreativeInvClick(InventoryClickEvent event) {
		if (event.getAction() == InventoryAction.NOTHING) return false;
		Player player = (Player) event.getWhoClicked();
		if (player.getGameMode() == GameMode.CREATIVE) return false;
		if (Util.hasBypass(player, "creative")) return false;
		
		try {
			if (event.getView().getTopInventory() != null){
				String invname = event.getView().getTopInventory().getName();
				if (Listeners.BlockCreativeContainer) {
					if (!invname.equals("container.inventory")) {
						player.sendMessage(ChatColor.RED + "[TRLimitedCreative] You may not interact with other inventories");
						event.setCancelled(true);
						return true;
					}
				}
			}
		} catch (Exception ex) {
			TRLogger.Log("debug", "Error! [TRLimitedCreative ContainerCheck] : " + ex.getMessage());
			Log.Exception(ex);
		}

		try {
			// determine of player attempted to pick an item up...
			ItemStack ccc = event.getCurrentItem();
			if (ccc == null) return false;
			boolean banned = false;
			if (Global.useNewBanSystem){
				if (TRCacheItem2.isBanned(player, "creative", ccc.getTypeId(), ccc.getDurability())) banned = true;
			} else {
				if (TRNoItem.isCreativeItemBanned(player, ccc.getTypeId(), ccc.getData().getData())) banned = true;
			}
			
			if (banned) {
				player.sendMessage(ChatColor.RED + "[TRLimitedCreative] You cannot obtain/modify this item type!");
				event.setCancelled(true);
				return true;
			}
			
		} catch (Exception ex) {
			TRLogger.Log("debug", "Error! [TRLimitedCreative BannedCreativeItem] : " + ex.getMessage());
			Log.Exception(ex);
		}
		return false;
	}
}
