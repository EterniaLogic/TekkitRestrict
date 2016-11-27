package ee.events;

import org.bukkit.Bukkit;

public class EEEventManager {
	public static boolean callEvent(EEEvent event){
		Bukkit.getPluginManager().callEvent(event);
		return event.isCancelled();
	}
}
