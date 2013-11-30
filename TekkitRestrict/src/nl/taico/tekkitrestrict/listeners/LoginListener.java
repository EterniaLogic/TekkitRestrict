package nl.taico.tekkitrestrict.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.functions.TRLimiter;

public class LoginListener implements Listener {
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e) {
		String playerName = e.getPlayer().getName();
		try {
			TRLimiter.removeExpire(playerName);
			TRLimiter.getOnlineLimiter(e.getPlayer());
		} catch(Exception ex){
			Warning.other("An error occurred in the LoginListener!", false);
			Log.Exception(ex, false);
		}
	}
}
