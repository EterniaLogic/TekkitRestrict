package nl.taico.tekkitrestrict.logging;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class TRCmdListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=true)
	public void onCmd(PlayerCommandPreprocessEvent event){
		TRLogSplitterPlus.logCommand(event.getPlayer().getName(), event.getMessage());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCmd2(ServerCommandEvent event){
		TRLogSplitterPlus.logCommand("CONSOLE", "/"+event.getCommand());
	}
}
