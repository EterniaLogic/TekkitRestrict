package nl.taico.tekkitrestrict.listeners;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import nl.taico.tekkitrestrict.TRListener;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.TRConfigCache.Dupes;
import nl.taico.tekkitrestrict.TRConfigCache.Hacks;
import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.eepatch.EEAssigner;

public class Assigner {
	/**
	 * Registers all Listeners that are required. If certain functionality is turned off, the listeners for it will not be registered.
	 * *Note*: Not everything in tekkitrestrict has been moved to this assigner, i'm still working on that.
	 */
	public static void assign(){
		tekkitrestrict plugin = tekkitrestrict.getInstance();
		PluginManager PM = plugin.getServer().getPluginManager();
		PM.registerEvents(new TRListener(), plugin);
		PM.registerEvents(new QuitListener(), plugin);
		
		PM.registerEvents(new InventoryClickListener(), plugin);
		
		if (Listeners.UseNoItem) {
			CraftingListener.setupCraftHook();
			PM.registerEvents(new ItemPickupListener(), plugin);
		}
		
		if (Dupes.alcBags.prevent ||
			Dupes.pedestals.prevent ||
			Dupes.rmFurnaces.prevent ||
			Dupes.tankcarts.prevent ||
			Dupes.tankcartGlitchs.prevent ||
			Dupes.transmutes.prevent)
			InventoryClickListener.doDupeCheck = true;

		if (Listeners.UseLimitedCreative)
			PM.registerEvents(new DropListener(), plugin);
		
		if (Dupes.teleports.prevent)
			PM.registerEvents(new TeleportListener(), plugin);
		
		if (Listeners.UseBlockLimit){
			PM.registerEvents(new BlockBreakListener(), plugin);
			PM.registerEvents(new LoginListener(), plugin);
		}
		
		if (Hacks.forcefields.enable)
			PM.registerEvents(new NoHackForcefield(), plugin);
		
		if (Hacks.speeds.enable)
			PM.registerEvents(new NoHackSpeed(), plugin);
		
		if (Hacks.flys.enable)
			PM.registerEvents(new NoHackFly(), plugin);
	}
	
	public static void register(Listener listener){
		tekkitrestrict plugin = tekkitrestrict.getInstance();
		PluginManager PM = plugin.getServer().getPluginManager();
		PM.registerEvents(listener, plugin);
	}
	
	public static void assignEEPatch(){
		EEAssigner.assign();
	}
	
	public static void unregisterAll(){
		HandlerList.unregisterAll(tekkitrestrict.getInstance());
	}
}
