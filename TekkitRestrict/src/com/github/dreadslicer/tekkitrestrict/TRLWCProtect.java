package com.github.dreadslicer.tekkitrestrict;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Permission;
import com.griefcraft.model.Protection;

public class TRLWCProtect {
	public static void checkLWC(BlockPlaceEvent event) {
		// link up with LWC!
		if (Util.hasBypass(event.getPlayer(), "lwc")) return;
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		boolean istype = false;
		// tekkitrestrict.log.info(b.getTypeId()+":"+b.getData());
		for (int i = 0; i < TRConfigCache.LWC.blocked.size(); i++) {
			List<TRCacheItem> iss = TRCacheItem.processItemString("", TRConfigCache.LWC.blocked.get(i), -1);
			for (TRCacheItem ist : iss) {
				if (ist.compare(block.getTypeId(), block.getData())) {
					istype = true;
					i = TRConfigCache.LWC.blocked.size() + 1;
					break;
				}
			}
		}
		
		if (TRConfigCache.LWC.lwcPlugin == null){
			PluginManager PM = tekkitrestrict.getInstance().getServer().getPluginManager();
			if (PM.isPluginEnabled("LWC")) TRConfigCache.LWC.lwcPlugin = (LWCPlugin) PM.getPlugin("LWC");
		}
		
		if (istype && TRConfigCache.LWC.lwcPlugin != null) {
			LWC LWC = TRConfigCache.LWC.lwcPlugin.getLWC();
			String playername = player.getName().toLowerCase();
			for (BlockFace bf : BlockFace.values()) {
				Protection prot = LWC.getProtectionCache().getProtection(block.getRelative(bf));
				if (prot == null) continue;
				
				boolean hasAccess = false;

				for (Permission pe : prot.getPermissions()) {
					if (pe.getName().toLowerCase().equals(playername)){
						hasAccess = true;
						break;
					}
				}

				if (!prot.isOwner(player) && !hasAccess) {
					player.sendMessage("You are not allowed to place this here!");
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}