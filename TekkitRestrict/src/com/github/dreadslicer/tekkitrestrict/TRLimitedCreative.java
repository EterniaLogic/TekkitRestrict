package com.github.dreadslicer.tekkitrestrict;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TRLimitedCreative {
	private static boolean BlockInteract;

	public static void reload() {
		BlockInteract = tekkitrestrict.config
				.getBoolean("LimitedCreativeNoContainer");
	}

	public static void handleCreativeInvClick(InventoryClickEvent event) {
		Player player = tekkitrestrict.getInstance().getServer()
				.getPlayer(event.getWhoClicked().getName());
		if (!event.isCancelled()) {
			try {
				if (!TRPermHandler.hasPermission(player, "creative", "bypass",
						"")) {
					event.getView().getTopInventory().getSize();

					String invname = event.getView().getTopInventory()
							.getName();
					if (BlockInteract) {
						if (invname != "container.inventory") {
							// player.sendRawMessage("[TRLimitedCreative] You may not interact with other inventories");
							player.sendRawMessage("[TRLimitedCreative] You may not interact with other inventories");
							// event.setCancelled(true);
							if (event.getCurrentItem() != null) {
								event.setCurrentItem(null);
								player.getInventory().addItem(
										player.getItemOnCursor());
								player.setItemOnCursor(null);
							}
							return;
						}
					}

					try {
						// determine of player attempted to pick an item up...
						if (event.getCurrentItem() != null) {
							ItemStack ccc = event.getCurrentItem();
							if (TRNoItem
									.isCreativeItemBanned(
											player,
											new com.github.dreadslicer.tekkitrestrict.ItemStack(
													ccc.getTypeId(), 0, ccc
															.getData()
															.getData()))) {
								// player.kickPlayer("[TRLimitedCreative] You cannot obtain/modify this item type!");
								player.sendRawMessage("[TRLimitedCreative] You cannot obtain/modify this item type!");
								// event.setCancelled(true);
								event.setCurrentItem(null);
								player.getInventory().addItem(
										player.getItemOnCursor());
								player.setItemOnCursor(null);
								// event.setResult(Result.DENY);
								return;
							}
						}
					} catch (Exception e) {
						TRLogger.Log(
								"debug",
								"Error! [TRLimitedCreative IBlock] : "
										+ e.getMessage());
					}
				}
			} catch (Exception e) {
				TRLogger.Log("debug", "Error! [TRLimitedCreative CBlock] : "
						+ e.getMessage());
			}
		}
	}
}
