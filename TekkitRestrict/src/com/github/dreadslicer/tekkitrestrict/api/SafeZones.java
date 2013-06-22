package com.github.dreadslicer.tekkitrestrict.api;

import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.TRSafeZone;
import com.github.dreadslicer.tekkitrestrict.TRSafeZone.SafeZoneCreate;

public class SafeZones {
	//TODO add getSafeZonePlayerIsIn and add a cache with isPlayerInSafeZone
	//to allow fast checks of both.
	public static boolean isPlayerInSafeZone(Player player) {	
		return TRSafeZone.inSafeZone(player);
	}
	
	public static SafeZoneCreate createSafeZone(Player player, String pluginName, String zoneName){
		return TRSafeZone.addSafeZone(player, pluginName, zoneName);
	}
}
