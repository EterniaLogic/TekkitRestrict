package com.github.dreadslicer.tekkitrestrict;

import java.util.Collections;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class TRLWCProtect {
	private static List<String> blocked;

	public static void checkLWC(org.bukkit.event.block.BlockPlaceEvent e) {
		// link up with LWC!
		if (!TRPermHandler.hasPermission(e.getPlayer(), "lwc", "bypass", "")) {
			Block b = e.getBlock();
			Player player = e.getPlayer();
			boolean istype = false;
			// tekkitrestrict.log.info(b.getTypeId()+":"+b.getData());
			for (int i = 0; i < blocked.size(); i++) {
				List<TRCacheItem> iss = TRCacheItem.processItemString("",
						blocked.get(i), -1);
				for (TRCacheItem ist : iss) {
					if (ist.compare(b.getTypeId(), b.getData())) {
						istype = true;
						i = blocked.size() + 1;
						break;
					}
				}
			}
			if (istype
					&& tekkitrestrict.getInstance().getServer()
							.getPluginManager().isPluginEnabled("LWC")) {
				com.griefcraft.lwc.LWCPlugin p = (com.griefcraft.lwc.LWCPlugin) tekkitrestrict
						.getInstance().getServer().getPluginManager()
						.getPlugin("LWC");
				com.griefcraft.lwc.LWC LWC = p.getLWC();
				for (BlockFace bf : BlockFace.values()) {
					com.griefcraft.model.Protection prot = LWC
							.getProtectionCache().getProtection(
									e.getBlock().getRelative(bf));
					if (prot != null) {
						boolean hasAccess = false;
						// prot.getAccess("", Protection.Type.PUBLIC);

						for (com.griefcraft.model.Permission pe : prot
								.getPermissions()) {
							if (pe.getName().toLowerCase()
									.equals(player.getName().toLowerCase())) {
								hasAccess = true;
							}
						}

						if (!prot.isOwner(player) && !hasAccess) {
							player.sendMessage("You are not allowed to place this here!");
							e.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}

	public static void reload() {
		blocked = Collections.synchronizedList(tekkitrestrict.config
				.getStringList("LWCPreventNearLocked"));
	}
}