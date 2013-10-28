package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.dreadslicer.tekkitrestrict.TRNoDupe;
import com.github.dreadslicer.tekkitrestrict.TRNoItem;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Dupes;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Listeners;
import com.github.dreadslicer.tekkitrestrict.objects.TRItem;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.DupeType;

public class InventoryClickListener implements Listener {
	static boolean doDupeCheck = false;
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onInventoryClick(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		
		int id1 = 0;
		int data1 = 0;
		int id2 = 0;
		
		if (event.getCurrentItem() != null){
			id1 = event.getCurrentItem().getTypeId();
			data1 = event.getCurrentItem().getDurability();

			String banned = null;
			
			if (player.getGameMode() == GameMode.CREATIVE){
				if (event.getView().getTopInventory() != null){
					if (Listeners.UseLimitedCreative && Listeners.BlockCreativeContainer){
						if (!"container.inventory".equals(event.getView().getTopInventory().getName())){
							player.sendMessage(ChatColor.RED + "[TRLimitedCreative] You may not interact with other inventories.");
							event.setCancelled(true);
							return;
						}
					}
				}
				
				banned = TRNoItem.isItemBannedInCreative(player, id1, data1, true);
				if (banned == null) banned = TRNoItem.isItemBanned(player, id1, data1, true);
			} else {
				banned = TRNoItem.isItemBanned(player, id1, data1, true);
			}
			
			if (banned != null){
				if (banned.equals("")) banned = ChatColor.RED + "[TRItemDisabler] This item is banned!";
				TRItem.sendBannedMessage(player, banned);
				event.setCancelled(true);
				return;
			}
		}
		
		if (event.getCursor() != null){
			id2 = event.getCursor().getTypeId();
		}
		
		if (!doDupeCheck) return;
		
		String title;
		try {
			title = event.getView().getTopInventory().getTitle().toLowerCase();
		} catch (NullPointerException ex) {
			return;
		}
		
		int slot = event.getSlot();
		
		if (title.equals("rm furnace")){
			if (Dupes.rmFurnaces.prevent && slot == 35 && event.isShiftClick()){
				if (!player.hasPermission("tekkitrestrict.bypass.dupe.rmfurnace")){
					event.setCancelled(true);
					player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click into a Red Matter Furnace from this slot!");
					TRNoDupe.handleDupe(player, DupeType.rmFurnace, id1, data1);
				}
			}
		} else if (title.equals("tank cart")){
			if (slot == 35) {
				if (Dupes.tankcarts.prevent && event.isShiftClick() && !player.hasPermission("tekkitrestrict.bypass.dupe.tankcart")){
						event.setCancelled(true);
						player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click into a Tank Cart from this slot!");
						TRNoDupe.handleDupe(player, DupeType.tankCart, id1, data1);
				}
			} else if (slot <= 8){
				if (event.isShiftClick() && Dupes.tankcartGlitchs.prevent){
					event.setCancelled(true);
					player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click into a Tank Cart!");
					TRNoDupe.handleDupe(player, DupeType.tankCartGlitch, id1, data1);
				}
			}
		} else if (title.equals("trans tablet")){
			if (Dupes.transmutes.prevent && event.isShiftClick()) {
				boolean isslot = (slot>=0 && slot<8);
				if (isslot && !player.hasPermission("tekkitrestrict.bypass.dupe.transtablet")){
					event.setCancelled(true);
					player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click any item out of the Tranmutation Table(t)!");
					TRNoDupe.handleDupe(player, DupeType.transmution, id1, data1);
				}
			}
		} else if (title.equals("bag")){
			if (Dupes.alcBags.prevent && (id1 == 27532 || id1 == 27593 || id2 == 27532 || id2 == 27593)){
				if (!player.hasPermission("tekkitrestrict.bypass.dupe.alcbag")){
					event.setCancelled(true);
					if (id1 == 27532){
						player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Black Hole Bands in an alchemy bag!");
					} else if (id1 == 27593){
						player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Void Rings in an alchemy bag!");
					} else {
						player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Black Hole Bands or Void Rings in an alchemy bag!");
					}
					TRNoDupe.handleDupe(player, DupeType.alcBag, id1, data1);
				}
			}
		} else if (title.equals("pedestal")){
			if (Dupes.pedestals.prevent && (id1 == 27537 || id2 == 27537)){
				if (!player.hasPermission("tekkitrestrict.bypass.dupe.pedestal")){
					event.setCancelled(true);
					player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Harvest rings on a pedestal!");
					TRNoDupe.handleDupe(player, DupeType.pedestal, id1, data1);
				}
			}
		}
	}
}
