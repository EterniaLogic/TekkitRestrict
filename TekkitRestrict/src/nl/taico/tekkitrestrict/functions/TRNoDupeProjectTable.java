package nl.taico.tekkitrestrict.functions;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TRNoDupeProjectTable {
	private static ConcurrentHashMap<Location,String> usedTables = new ConcurrentHashMap<Location, String>(); //loc, player
	private static ConcurrentHashMap<String, Location> playerUsed = new ConcurrentHashMap<String, Location>();
	
	/**
	 * Checks bypass.dupe.projecttable permission
	 * @return True if the player is not allowed to use this table.
	 */
	public static boolean tableUseNotAllowed(Block block, Player player){
		if(block == null) return false;
		if(block.getTypeId() != 137 || block.getData() != 3) return false;
		
		if (player.hasPermission("tekkitrestrict.bypass.dupe.projecttable")) return false;
		
		String playerName = player.getName().toLowerCase();
		Location loc = block.getLocation();
		String usingPlayer = usedTables.get(loc);
		if (usingPlayer != null){
			if(!playerName.equalsIgnoreCase(usingPlayer)){
				return true;
			}
		}
		
		playerUsed.put(playerName, loc);
		usedTables.put(loc, playerName);
		return false;
	}
	
	/** Make a player stop using a project table. */
	public static void playerUnuse(String playerName){
		Location table = playerUsed.remove(playerName.toLowerCase());
		if (table == null) return;
		usedTables.remove(table);
	}
}
