package nl.taico.tekkitrestrict.logging;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TRCmdListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=true)
	public void onCmd(PlayerCommandPreprocessEvent event){
		TRSplitter.splitCmd(event.getPlayer(), event.getMessage());
	}
}
