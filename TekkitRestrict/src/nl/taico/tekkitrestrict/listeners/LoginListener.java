package nl.taico.tekkitrestrict.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TekkitRestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.functions.TRLimiter;

public class LoginListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		Bukkit.getScheduler().scheduleAsyncDelayedTask(TekkitRestrict.instance, new Runnable(){
			public void run(){
				try {
					TRLimiter.removeExpire(p.getName());
					TRLimiter.getOnlineLimiter(p);
				} catch(Exception ex){
					Warning.other("An error occurred in the LoginListener!", false);
					Log.Exception(ex, false);
				}
			}
		});
	}
}
