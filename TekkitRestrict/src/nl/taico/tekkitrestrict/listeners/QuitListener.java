package nl.taico.tekkitrestrict.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import nl.taico.tekkitrestrict.TRConfigCache;
import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.commands.TRCmdOpenAlc;
import nl.taico.tekkitrestrict.commands.TRCmdOpenInv;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRNoDupe;
import nl.taico.tekkitrestrict.functions.TRNoDupeProjectTable;
import nl.taico.tekkitrestrict.functions.TRNoHack;

public class QuitListener implements Listener{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e) {
		quit(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent e) {
		quit(e.getPlayer());
	}
	
	public void quit(Player player){
		if (player == null) return;
		
		TRCmdOpenAlc.setOnDisconnect(player);
		TRCmdOpenInv.closeInv(player);
		TRNoDupeProjectTable.playerUnuse(player.getName());
		
		if (Listeners.UseBlockLimit) {
			try {
				TRLimiter.setExpire(player.getName());
			} catch(Exception ex){}
		}
		
		if (TRConfigCache.Global.favorPerformanceOverMemory) return;
		TRNoHack.playerLogout(player.getName());
		TRNoDupe.playerLogout(player.getName());
	}
}
