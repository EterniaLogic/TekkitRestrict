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
	/**
	 * Checks tekkitrestrict.bypass.lwc permission.
	 * @return False if the event was cancelled.
	 */
	public static boolean checkLWCAllowed(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		// link up with LWC!
		if (player.hasPermission("tekkitrestrict.bypass.lwc")) return true;
		
		Block block = event.getBlock();
		int id = block.getTypeId();
		byte data = block.getData();
		
		boolean istype = false;
		// tekkitrestrict.log.info(b.getTypeId()+":"+b.getData());
		blockedloop:
			for (int i = 0; i < TRConfigCache.LWC.blocked.size(); i++) {
				List<TRCacheItem> iss = TRCacheItem.processItemString("", TRConfigCache.LWC.blocked.get(i), -1);
				for (TRCacheItem ist : iss) {
					if (ist.compare(id, data)) {
						istype = true;
						break blockedloop;
					}
				}
			}
		
		if (!istype) return true;
		
		if (TRConfigCache.LWC.lwcPlugin == null){
			PluginManager PM = tekkitrestrict.getInstance().getServer().getPluginManager();
			if (PM.isPluginEnabled("LWC")) TRConfigCache.LWC.lwcPlugin = (LWCPlugin) PM.getPlugin("LWC");
		}
		
		if (TRConfigCache.LWC.lwcPlugin == null) return true;
		
		LWC LWC = TRConfigCache.LWC.lwcPlugin.getLWC();
		String playername = player.getName().toLowerCase();
		
		outerloop:
			for (BlockFace bf : BlockFace.values()) {
				Protection prot = LWC.getProtectionCache().getProtection(block.getRelative(bf));
				if (prot == null) continue;
				if (prot.isOwner(player)) continue;
				
				for (Permission pe : prot.getPermissions()) {
					if (pe.getName().toLowerCase().equals(playername)) continue outerloop;
				}
	
				player.sendMessage("You are not allowed to place this here!");
				event.setCancelled(true);
				return false;
			}
		return true;
	}
}