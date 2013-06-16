package com.github.dreadslicer.tekkitrestrict.listeners;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Hacks;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Listeners;
import com.github.dreadslicer.tekkitrestrict.TRListener;
import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

public class Assigner {
	/**
	 * Registers all Listeners that are required. If certain functionality is turned off, the listeners for it will not be registered.
	 * *Note*: Not everything in tekkitrestrict has been moved to this assigner, i'm still working on that.
	 */
	public static void assign(tekkitrestrict plugin){
		PluginManager PM = plugin.getServer().getPluginManager();
		PM.registerEvents(new TRListener(), plugin);
		PM.registerEvents(new QuitListener(), plugin);
		
		PM.registerEvents(new InventoryClickListener(), plugin);
		if (tekkitrestrict.config.getBoolean("PreventAlcDupe", true) ||
			tekkitrestrict.config.getBoolean("PreventTransmuteDupe", true) ||
			tekkitrestrict.config.getBoolean("PreventTankCartDupe", true) ||
			tekkitrestrict.config.getBoolean("PreventRMFurnaceDupe", true) ||
			tekkitrestrict.config.getBoolean("PreventTankCartGlitch", true) ||
			tekkitrestrict.config.getBoolean("PreventPedestalEmcGen", true))
			InventoryClickListener.doDupeCheck = true;

		if (tekkitrestrict.config.getBoolean("PreventTeleportDupe", true))
			PM.registerEvents(new TeleportListener(), plugin);
		
		if (Listeners.UseBlockLimit){
			PM.registerEvents(new BlockBreakListener(), plugin);
			PM.registerEvents(new LoginListener(), plugin);
		}
		
		if (Hacks.forcefield)
			PM.registerEvents(new NoHackForcefield(), plugin);
		
		if (Hacks.speed)
			PM.registerEvents(new NoHackSpeed(), plugin);
		
		if (Hacks.fly)
			PM.registerEvents(new NoHackFly(), plugin);
	}
	
	public static void register(Listener listener){
		tekkitrestrict plugin = tekkitrestrict.getInstance();
		PluginManager PM = plugin.getServer().getPluginManager();
		PM.registerEvents(listener, plugin);
	}
}
