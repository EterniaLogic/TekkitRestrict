package nl.taico.tekkitrestrict.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.commands.TRCommandAlc;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRNoDupe;
import nl.taico.tekkitrestrict.functions.TRNoDupeProjectTable;
import nl.taico.tekkitrestrict.functions.TRNoHack;

public class QuitListener implements Listener{
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		quit(e.getPlayer());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		quit(e.getPlayer());
	}
	
	public void quit(Player player){
		if (player == null) return;
		
		TRCommandAlc.restoreViewerInventory(player, false);
		TRNoHack.playerLogout(player);
		TRNoDupe.playerLogout(player);
		TRNoDupeProjectTable.playerUnuse(player.getName());
		
		if (Listeners.UseBlockLimit) {
			try {TRLimiter.setExpire(player.getName());}catch(Exception eee){}
		}
	}
}
