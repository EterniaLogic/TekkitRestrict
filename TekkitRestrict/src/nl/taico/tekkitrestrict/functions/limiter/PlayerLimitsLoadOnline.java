package nl.taico.tekkitrestrict.functions.limiter;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

public class PlayerLimitsLoadOnline {
	private ConcurrentHashMap<String, PlayerLimits> limits = new ConcurrentHashMap<String, PlayerLimits>(Math.min(128, Bukkit.getServer().getMaxPlayers()+2), 0.75f, 6);
}
