package com.github.dreadslicer.tekkitrestrict;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public class TRNoDupe {
	//public static String lastPlayer = "";

	/*
	private static void handleDupes(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		int slot = event.getSlot();

		String title = event.getView().getTopInventory().getTitle().toLowerCase();
		
		// RMDupe Slot35
		if (!Util.hasBypass(player, "dupe")) {
			if (title.equals("rm furnace")) {
				if (slot == 35 && event.isShiftClick()) {
					if (preventRMDupe) {
						event.setCancelled(true);
						player.sendMessage("[TRDupe] you are not allowed to Shift+Click here while using a RM Furnace!");

						TRLogger.Log("Dupe", player.getName() + " attempted to dupe using a RM Furnace!");
					} else {
						TRLogger.Log("Dupe", player.getName() + " duped using a RM Furnace!");
					}
					TRLogger.broadcastDupe(player.getName(), "the RM Furnace", "RMFurnace");	
				}
			} else if (title.equals("tank cart")) {
				if (slot == 35 && event.isShiftClick()) {
					if (preventTankCartDupe){
						event.setCancelled(true);
						player.sendMessage("[TRDupe] you are not allowed to Shift+Click here while using a Tank Cart!");

						TRLogger.Log("Dupe", player.getName() + " attempted to dupe using a Tank Cart!");
						TRLogger.broadcastDupe(player.getName(), "the Tank Cart", "TankCart");
					}
				} else if (slot <= 8 && event.isShiftClick()) {
					if (preventTankCartGlitch){
						event.setCancelled(true);
						player.sendMessage("[TR] you are not allowed to Shift+Click here while using a Tank Cart!");
					}
				}
			} else if (title.equals("trans tablet")) {
				// slots-6 7 5 3 1 0 2
				int item = event.getCurrentItem().getTypeId();
				if (item == 27557) {}
				if (item == 27558) {}
				if (item == 27559) {}
				if (item == 27560) {}
				if (item == 27561) {}
				if (item == 27591) {}
				if (event.isShiftClick()) {
					// if (isKlein) {
					boolean isslot = slot == 0 || slot == 1 || slot == 2
							|| slot == 3 || slot == 4 || slot == 5 || slot == 6
							|| slot == 7;
					if (isslot) {
						if (preventTransmuteDupe) {
							event.setCancelled(true);
							player.sendMessage("[TRDupe] you are not allowed to Shift+Click any ");
							player.sendMessage("           item out of the transmutation table!");

							TRLogger.Log("Dupe", player.getName() + " attempted to transmute dupe!");
						} else {
							TRLogger.Log("Dupe", player.getName() + " attempted to transmute dupe!");
						}
						TRLogger.broadcastDupe(player.getName(), "the Transmutation Table", "transmute");
					}
				}
			}
		}
	}*/

	public static void handleDropDupes(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		TRNoDupe_BagCache cache;
		if ((cache = TRNoDupe_BagCache.check(player)) != null) {
			if (cache.hasBHBInBag) {
				try {
					cache.expire();
					event.setCancelled(true);
					player.kickPlayer("[TRDupe] you have a " + cache.dupeItem + " in your [" + cache.inBagColor + "] Alchemy Bag!");

					Log.Dupe("a " + cache.inBagColor + " Alchemy Bag and a " + cache.dupeItem, "alc", player.getName());
				} catch (Exception ex) {
				}
			}
		}
	}
}
