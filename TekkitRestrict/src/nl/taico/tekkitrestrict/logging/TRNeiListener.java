package nl.taico.tekkitrestrict.logging;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import codechicken.nei.NEIGiveEvent;

public class TRNeiListener implements Listener{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNEIGive(NEIGiveEvent event){
		TRLogSplitterPlus.logNEI("[NEI] Giving " + (event.amount==-1?"infinate ":event.amount + " of ") + event.itemname + " to " + event.player.name);
	}
}
