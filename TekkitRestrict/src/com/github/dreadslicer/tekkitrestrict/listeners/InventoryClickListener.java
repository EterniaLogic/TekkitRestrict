package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.dreadslicer.tekkitrestrict.Log;
import com.github.dreadslicer.tekkitrestrict.TRNoItem;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Dupes;

public class InventoryClickListener implements Listener {
	static boolean doDupeCheck = false;
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onInventoryClick(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		
		int id1 = 0;
		int data1 = 0;
		int id2 = 0;
		
		if (event.getCurrentItem() != null)
		{
			id1 = event.getCurrentItem().getTypeId();
			data1 = event.getCurrentItem().getDurability();
			if (!player.hasPermission("tekkitrestrict.bypass.noitem")) {
				boolean banned = false;
				
				if (TRNoItem.isItemBanned(player, id1, data1, false)) banned = true;
				
				if (banned) {
					player.sendMessage(ChatColor.RED + "This item is banned!");
					event.setCancelled(true);
					return;
				}
			}
		}
		
		if (event.getCursor() != null)
		{
			id2 = event.getCursor().getTypeId();
		}
		
		
		if (!doDupeCheck) return;
		
		int slot = event.getSlot();

		String title = event.getView().getTopInventory().getTitle().toLowerCase();
		if (title.equals("rm furnace")){
			if (Dupes.rmFurnace && slot == 35 && event.isShiftClick()){
				if (!player.hasPermission("tekkitrestrict.bypass.dupe.rmfurnace")){
					event.setCancelled(true);
					player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click into a Red Matter Furnace from this slot!");
					Log.Dupe("Red Matter Furnace", "RMFurnace", player.getName());
				}
			}
		} else if (title.equals("tank cart")){
			if (slot == 35) {
				if (Dupes.tankcart && event.isShiftClick() && !player.hasPermission("tekkitrestrict.bypass.dupe.tankcart")){
						event.setCancelled(true);
						player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click into a Tank Cart from this slot!");
						Log.Dupe("Tank Cart", "TankCart", player.getName());
				}
			} else if (slot <= 8){
				if (event.isShiftClick() && Dupes.tankcartGlitch){
					event.setCancelled(true);
					player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click into a Tank Cart!");
					Log.Glitch("Tank Cart", player.getName());
				}
			}
		} else if (title.equals("trans tablet")){
			if (Dupes.transmute && event.isShiftClick()) {
				boolean isslot = slot == 0 || slot == 1 || slot == 2
						|| slot == 3 || slot == 4 || slot == 5 || slot == 6
						|| slot == 7;
				if (isslot && !player.hasPermission("tekkitrestrict.bypass.dupe.transtablet")){
					event.setCancelled(true);
					player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click any item out of the Tranmutation Table(t)!");
					Log.Dupe("Transmution Tablet", "TransmutionTablet", player.getName());
				}
			}
		} else if (title.equals("bag")){
			if (Dupes.alcBag && (id1 == 27532 || id1 == 27593 || id2 == 27532 || id2 == 27593))
			{
				if (!player.hasPermission("tekkitrestrict.bypass.dupe.alcbag")){
					event.setCancelled(true);
					if (event.getCurrentItem().getTypeId() == 27532){
						player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Black Hole Bands in an alchemy bag!");
						Log.Dupe("Alchemy Bag + Black Hole Band", "AlchemicalBag", player.getName());
					}
					else if (event.getCurrentItem().getTypeId() == 27593){
						player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Void Rings in an alchemy bag!");
						Log.Dupe("Alchemy Bag + Void Ring", "AlchemicalBag", player.getName());
					}
				}
			}
		} else if (title.equals("pedestal")){
			if (Dupes.pedestal && (id1 == 27537 || id2 == 27537)){
				if (!player.hasPermission("tekkitrestrict.bypass.dupe.pedestal")){
					event.setCancelled(true);
					player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Harvest rings on a pedestal!");
					Log.Dupe("Pedestal + Harvest Godess Band", "Pedestal", player.getName());
				}
			}
		}
	}
}
