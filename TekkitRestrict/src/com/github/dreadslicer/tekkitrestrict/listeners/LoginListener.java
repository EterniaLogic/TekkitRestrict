package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.dreadslicer.tekkitrestrict.Log;
import com.github.dreadslicer.tekkitrestrict.TRLimiter;
import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

public class LoginListener implements Listener {
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e) {
		String playerName = e.getPlayer().getName();
		try {
			TRLimiter.removeExpire(playerName);
			TRLimiter.getOnlineLimiter(e.getPlayer());
		} catch(Exception ex){
			tekkitrestrict.log.warning("An error occurred in the LoginListener!");
			Log.Exception(ex, false);
		}
	}
}
