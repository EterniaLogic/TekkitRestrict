package com.github.dreadslicer.tekkitrestrict.api;

import org.bukkit.entity.Player;

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
	public static boolean isPlayerInSafeZone(Player player) {	
		return TRSafeZone.inSafeZone(player);
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
	 * This checks if there is a Factions safezone at his location that applies to him.<br><br>
	 * 
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return <b>If the player is in a Factions Safezone that applies to him.</b><br>
	 */
	public static boolean isFactionsSafeZoneForPlayer(Player player){
		return TRSafeZone.allowedInFactionsSafeZone(player);
	}
	
	/**
	 * This checks if there is a PreciousStones safezone at his location that applies to him.<br><br>
	 * 
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return <b>If the player is in a PreciousStones Safezone that applies to him.</b><br>
	 */
	public static boolean isPreciousStonesSafeZoneForPlayer(Player player){
		return TRSafeZone.allowedInPreciousStonesSafeZone(player);
	}
	
	/**
	 * This checks if there is a Towny safezone at his location that applies to him.<br><br>
	 * 
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return <b>If the player is in a Towny Safezone that applies to him.</b><br>
	 */
	public static boolean isTownySafeZoneForPlayer(Player player){
		return TRSafeZone.allowedInTownySafeZone(player);
	}
	
	/**
	 * This checks if there is a GriefPrevention safezone at his location that applies to him.<br><br>
	 * 
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)<br>
	 * @return <b>If the player is in a GriefPrevention Safezone that applies to him.</b><br>
	 */
	public static boolean isGriefPreventionSafeZoneForPlayer(Player player){
		return TRSafeZone.allowedInGriefPreventionSafeZone2(player);
	}
}
