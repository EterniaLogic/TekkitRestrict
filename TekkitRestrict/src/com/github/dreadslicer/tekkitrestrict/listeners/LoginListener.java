package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.dreadslicer.tekkitrestrict.TRLimitBlock;

public class LoginListener implements Listener {
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e) {
		String playerName = e.getPlayer().getName();
		try {
			TRLimitBlock.removeExpire(playerName);
			TRLimitBlock.getLimiter(playerName);
		} catch(Exception ex){}
	}
}
