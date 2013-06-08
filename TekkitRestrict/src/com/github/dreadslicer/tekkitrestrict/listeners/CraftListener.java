package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import com.github.dreadslicer.tekkitrestrict.TRNoItem;
import com.github.dreadslicer.tekkitrestrict.Util;

public class CraftListener implements Listener {
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onCraft(CraftItemEvent event){
		Player player = (Player) event.getWhoClicked();
		if (Util.hasBypass(player, "noitem")) return;
		
		ItemStack item = event.getRecipe().getResult();
		if (item == null) return;
		int id = item.getTypeId();
		short data = item.getDurability();
		if (TRNoItem.isItemBanned(player, new com.github.dreadslicer.tekkitrestrict.ItemStack(id, 0, data))) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You are not allowed to craft this item!");
		}
	}
}
