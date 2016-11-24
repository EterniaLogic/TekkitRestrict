package nl.taico.tekkitrestrict.functions;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TRNoDupeProjectTable {
	private static HashMap<Location,String> usedTables = new HashMap<Location, String>(); //loc, player
	private static HashMap<String, Location> playerUsed = new HashMap<String, Location>();

	/**
	 * Checks bypass.dupe.projecttable permission
	 * @return True if the player is not allowed to use this table.
	 */
	public static boolean isTableUseAllowed(Block block, Player player){
		if ((block == null) || (block.getTypeId() != 137) || (block.getData() != 3)) return true;

		if ((player == null) || player.hasPermission("tekkitrestrict.bypass.dupe.projecttable")) return true;

		final String playerName = player.getName().toLowerCase();
		final Location loc = block.getLocation();
		final String usingPlayer = usedTables.get(loc);
		if ((usingPlayer != null) && !playerName.equals(usingPlayer)) return false;

		playerUsed.put(playerName, loc);
		usedTables.put(loc, playerName);
		return true;
	}

	/** Make a player stop using a project table. */
	public static void playerUnuse(String playerName){
		final Location table = playerUsed.remove(playerName.toLowerCase());
		if (table == null) return;
		usedTables.remove(table);
	}
}
