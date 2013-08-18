package com.github.dreadslicer.tekkitrestrict.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.TRNoItem;

public class Bans {
	/**
	 * @return If the given block is a disabled item (set in the config).
	 */
	public static boolean isBlockBanned(Block block){
		return TRNoItem.isBlockBanned(block);
	}
	
	/**
	 * Note: It checks this player's permissions for tekkitrestrict.noitem.id[.data]
	 * and for tekkitrestrict.bypass.noitem
	 * 
	 * @return If the given id:data combination is banned for this player.
	 */
	public static boolean isItemBanned(Player player, int id, int data){
		return TRNoItem.isItemBanned(player, id, data, true);
	}
	/**
	 * @return If the given item/block is banned in the config.
	 */
	public static boolean isItemGloballyBanned(int id, int data){
		return TRNoItem.isItemGloballyBanned(id, data);
	}
	
	/**
	 * Note: It checks this player's permissions for tekkitrestrict.creative.id[.data]
	 * and for tekkitrestrict.bypass.creative
	 * 
	 * @return If the given id:data combination is banned for this player when he/she is
	 * in creative mode.
	 */
	public static boolean isItemBannedInCreative(Player player, int id, int data){
		return TRNoItem.isItemBannedInCreative(player, id, data, true);
	}
	
	
}
