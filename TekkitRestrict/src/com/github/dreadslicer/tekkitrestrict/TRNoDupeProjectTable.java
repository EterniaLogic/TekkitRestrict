package com.github.dreadslicer.tekkitrestrict;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TRNoDupeProjectTable {
	private static ConcurrentHashMap<Location,String> UsedTables = new ConcurrentHashMap<Location, String>(); //loc, player
	private static ConcurrentHashMap<String, Location> PlayerUsed = new ConcurrentHashMap<String, Location>();
	
	/**
	 * Checks bypass.dupe.projecttable permission
	 * @return True if the player is not allowed to use this table.
	 */
	public static boolean tableUseNotAllowed(Block block, Player player){
		if(block == null) return false;
		
		if (player.hasPermission("tekkitrestrict.bypass.dupe.projecttable")) return false;
		
		if(block.getTypeId() == 137 && block.getData() == 3){
			String playerName = player.getName().toLowerCase();
			Location loc = block.getLocation();
			String usingPlayer = UsedTables.get(loc);
			if (usingPlayer != null){
				if(!playerName.equals(usingPlayer.toLowerCase())){
					return true;
				}
			}
			
			PlayerUsed.put(playerName, loc);
			UsedTables.put(loc, playerName);
		}
		return false;
	}
	
	/** Make a player stop using a project table. */
	public static void playerUnuse(String playerName){
		playerName = playerName.toLowerCase();
		Location table = PlayerUsed.remove(playerName);
		if (table == null) return;
		UsedTables.remove(table);
	}
}
