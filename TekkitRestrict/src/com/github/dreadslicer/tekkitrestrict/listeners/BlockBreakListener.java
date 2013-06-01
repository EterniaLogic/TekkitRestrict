package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache;
import com.github.dreadslicer.tekkitrestrict.TRLimitBlock;
import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

public class BlockBreakListener implements Listener{
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		for (int eee : TRConfigCache.Listeners.Exceptions) {
			if (e.getBlock().getTypeId() == eee) return;
		}
		
		Player player = e.getPlayer();
		if (player == null) return;
		String pname = player.getName().toLowerCase();
		if (pname.equals("[buildcraft]") || pname.equals("[redpower]")) return;
		try {
			String pl = TRLimitBlock.getPlayerAt(e.getBlock());
			if (pl != null) {
				TRLimitBlock il = TRLimitBlock.getLimiter(pl);
				il.checkBreakLimit(e);
			}
		} catch(Exception eee){
			tekkitrestrict.log.warning("A minor exception occured in tekkitrestrict. Please give the developer the following information: ");
			tekkitrestrict.log.warning(" - onBlockBreak, block limiter.");
		}
	}
}
