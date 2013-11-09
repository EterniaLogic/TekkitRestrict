package nl.taico.tekkitrestrict.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import nl.taico.tekkitrestict.objects.TREnums.SSPlugin;

import com.github.dreadslicer.tekkitrestrict.TRSafeZone;

public class SafeZones {
	public static enum SafeZoneCreate {
		/** Creation succeeded. */
		Success,
		/** Name already used or the current zone is already a SafeZone. */
		AlreadyExists,
		/** No claim/region can be found at the players current position. */
		RegionNotFound,
		/** The plugin cannot be found. */
		PluginNotFound,
		/** SafeZones are disabled. */
		SafeZonesDisabled,
		/** The user is not allowed to turn the claim/region he is standing in into a safezone. */
		NoPermission,
		/** Unknown reason. */
		Unknown;
	}
	//TODO add getSafeZonePlayerIsIn and add a cache with isPlayerInSafeZone
	//to allow fast checks of both.
	/**
	 * Uses {@link #getSafeZoneFor(Player)}.
	 * @return If the player is in a safezone that applies to him.
	 * @see #isSafeZoneFor(Player, List)
	 */
	public static boolean isSafeZoneFor(Player player) {
		return !getSafeZoneFor(player).equals("");
	}
	
	/**
	 * Uses {@link #getSafeZoneFor(Player, List)}.<br>
	 * Checks plugins in the specified order.
	 * @return If the player is in a safezone that applies to him.
	 * @see #isSafeZoneFor(Player)
	 */
	public static boolean isSafeZoneFor(Player player, List<SSPlugin> order) {
		return !getSafeZoneFor(player, order).equals("");
	}
	
	/**
	 * @param player This player is used for the location and for the permissions checks.<br>
	 * If the player is not allowed to build in a WorldGuard region, or if he is not on the managers
	 * list in a GriefPrevention claim, it will return with NoPermission.
	 * <br><br>
	 * @param pluginName The name of the plugin you want to use to create the safezone.<br>
	 * Can be "griefprevention" or "worldguard". (Case Insensitive)
	 * <br><br>
	 * @param zoneName The name you want the safeZone to have.<br>
	 * Duplicate names are not allowed. (Will return SafeZoneCreate.AlreadyExists)
	 * 
	 * <br><br>
	 * @return The reason why the creation succeeded or failed.
	 */
	public static SafeZoneCreate createSafeZone(Player player, String pluginName, String zoneName){
		return TRSafeZone.addSafeZone(player, pluginName, zoneName);
	}
	
	/**
	 * Removes a WorldGuard/GriefPrevention safezone from the database.<br>
	 * Do not use this method in a loop. It will cause ConcurrentModificationExceptions.
	 * @return True if the removal succeeded. False otherwise.
	 */
	public static boolean removeSafeZone(TRSafeZone zone){
		return TRSafeZone.removeSafeZone(zone);
	}
	
	/**
	 * This checks if there is a Factions safezone at his location that applies to him.<br><br>
	 * 
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return <b>If the player is in a Factions Safezone that applies to him.</b><br>
	 */
	public static boolean isFactionsSafeZoneForPlayer(Player player){
		return TRSafeZone.Factions.isSafeZoneFor(player, true);
	}
	
	/**
	 * This checks if there is a PreciousStones safezone at his location that applies to him.<br><br>
	 * 
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return <b>If the player is in a PreciousStones Safezone that applies to him.</b><br>
	 */
	public static boolean isPreciousStonesSafeZoneForPlayer(Player player){
		return TRSafeZone.PS.isSafeZoneFor(player, true);
	}
	
	/**
	 * This checks if there is a Towny safezone at his location that applies to him.<br><br>
	 * 
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return <b>If the player is in a Towny Safezone that applies to him.</b><br>
	 */
	public static boolean isTownySafeZoneForPlayer(Player player){
		return TRSafeZone.Towny.isSafeZoneFor(player, true);
	}
	
	/**
	 * This checks if there is a GriefPrevention safezone at his location that applies to him.<br><br>
	 * 
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)<br>
	 * @return <b>If the player is in a GriefPrevention Safezone that applies to him.</b><br>
	 */
	public static boolean isGriefPreventionSafeZoneForPlayer(Player player){
		return TRSafeZone.GP.isSafeZoneFor(player);
	}
	
	/**
	 * Searches for a WorldGuard safezone in the database for the given location.
	 * If checkGP is true, it will also check for GriefPrevention Safezones.
	 * @return "" if none is found. A message with more information about the safezone otherwise.
	 */
	public static String getSafeZoneByLocation(Location location, boolean checkGP){
		return TRSafeZone.getSafeZoneByLocation(location, checkGP);
	}
	
	/**
	 * Get a safezone at the players current position that applies to him.<br>
	 * First checks GriefPrevention, then WorldGuard, Towny, Factions and last PreciousStones.
	 * @return "" If none is found/none applies. A string with information about the safezone otherwise.
	 * @see #getSafeZoneFor(Player, List)
	 */
	public static String getSafeZoneFor(Player player){
		if (isGriefPreventionSafeZoneForPlayer(player)) return "GriefPrevention Safezone Claim owned by: " + TRSafeZone.GP.lastGP;
		
		String r = getSafeZoneByLocation(player.getLocation(), false);
		if (!r.equals("")) return r;
		
		if (isTownySafeZoneForPlayer(player)) return "Towny Safezone";
		if (isFactionsSafeZoneForPlayer(player)) return "Safezone Faction: " + TRSafeZone.Factions.lastFaction;
		if (isPreciousStonesSafeZoneForPlayer(player)) return "PreciousStones SafeZone Field: " + TRSafeZone.PS.lastPS;
		
		return "";
	}
	
	/**
	 * Get a safezone at the players current position that applies to him.<br>
	 * Checks in the order of the given list.
	 * @return "" If none is found/none applies. A string with information about the safezone otherwise.
	 * @see #getSafeZoneFor(Player)
	 */
	public static String getSafeZoneFor(Player player, List<SSPlugin> order){
		for (SSPlugin current : order){
			if (current.GP()){
				if (isGriefPreventionSafeZoneForPlayer(player)) return "GriefPrevention Safezone Claim owned by: " + TRSafeZone.GP.lastGP;
			} else if (current.WG()){
				String r = getSafeZoneByLocation(player.getLocation(), false);
				if (!r.equals("")) return r;
			} else if (current.PS()){
				if (isPreciousStonesSafeZoneForPlayer(player)) return "PreciousStones SafeZone Field: " + TRSafeZone.PS.lastPS;
			} else if (current.F()){
				if (isFactionsSafeZoneForPlayer(player)) return "Safezone Faction: " + TRSafeZone.Factions.lastFaction;
			} else if (current.T()){
				if (isTownySafeZoneForPlayer(player)) return "Towny Safezone";
			}
		}
		return "";
	}
}
