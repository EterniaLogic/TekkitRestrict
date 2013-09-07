package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.dreadslicer.tekkitrestrict.TRLimiter;
import com.github.dreadslicer.tekkitrestrict.TRNoDupeProjectTable;
import com.github.dreadslicer.tekkitrestrict.TRNoHack;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Listeners;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandAlc;

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
		TRNoDupeProjectTable.playerUnuse(player.getName());
		
		if (Listeners.UseBlockLimit) {
			try {TRLimiter.setExpire(player.getName());}catch(Exception eee){}
		}
	}
}
