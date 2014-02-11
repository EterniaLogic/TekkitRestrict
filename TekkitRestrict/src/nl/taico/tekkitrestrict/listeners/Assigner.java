package nl.taico.tekkitrestrict.listeners;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TRConfigCache.LogFilter;
import nl.taico.tekkitrestrict.TRConfigCache.Logger;
import nl.taico.tekkitrestrict.TRListener;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.TRConfigCache.Dupes;
import nl.taico.tekkitrestrict.TRConfigCache.Hacks;
import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.eepatch.EEChargeListener;
import nl.taico.tekkitrestrict.eepatch.EEDuplicateListener;
import nl.taico.tekkitrestrict.eepatch.EEPSettings;
import nl.taico.tekkitrestrict.eepatch.amuletlisteners.EEAmuletListener;
import nl.taico.tekkitrestrict.eepatch.destlisteners.EEDestructionListener;
import nl.taico.tekkitrestrict.eepatch.otherlisteners.EEPedestalListener;
import nl.taico.tekkitrestrict.eepatch.otherlisteners.EEPhilosopherListener;
import nl.taico.tekkitrestrict.eepatch.otherlisteners.EETransmutionListener;
import nl.taico.tekkitrestrict.eepatch.otherlisteners.EEWatchListener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EERingListener;
import nl.taico.tekkitrestrict.eepatch.toollisteners.EEDMToolListener;
import nl.taico.tekkitrestrict.eepatch.toollisteners.EERMToolListener;
import nl.taico.tekkitrestrict.logging.TRCmdListener;
import nl.taico.tekkitrestrict.logging.TRNeiListener;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

public class Assigner {
	/**
	 * Registers all Listeners that are required. If certain functionality is turned off, the listeners for it will not be registered.
	 * *Note*: Not everything in tekkitrestrict has been moved to this assigner, i'm still working on that.
	 */
	public static void assign(){
		final tekkitrestrict plugin = tekkitrestrict.getInstance();
		final PluginManager PM = plugin.getServer().getPluginManager();
		register("Main", new TRListener(), PM, plugin);
		register("Quit", new TRListener(), PM, plugin);
		register("Inventory Click/Anti Dupe", new InventoryClickListener(), PM, plugin);
		
		if (Listeners.UseNoItem)
			CraftingListener.setupCraftHook();
		
		if (Logger.LogAmulets || Logger.LogDMTools || Logger.LogEEDestructive || Logger.LogEEMisc || Logger.LogRings || Logger.LogRMTools){
			//if (!tekkitrestrict.EEPatch)
			register("Interact", new InteractListener(), PM, plugin);
		}
		
		if (Dupes.alcBag.prevent ||
			Dupes.pedestal.prevent ||
			Dupes.rmFurnace.prevent ||
			Dupes.tankcart.prevent ||
			Dupes.tankcartGlitch.prevent ||
			Dupes.transmute.prevent ||
			Dupes.diskdrive.prevent)
			InventoryClickListener.doDupeCheck = true;

		if (Listeners.UseLimitedCreative)
			register("Limited Creative Drop", new DropListener(), PM, plugin);
		
		if (Dupes.teleport.prevent)
			register("Anti Teleport Dupe", new TeleportListener(), PM, plugin);
		
		if (Listeners.UseBlockLimit){
			register("Limiter Block Break", new BlockBreakListener(), PM, plugin);
			register("Limiter Login", new LoginListener(), PM, plugin);
		}
		
		if (Hacks.forcefield.enable)
			register("Anti Forcefield Hack", new NoHackForcefield(), PM, plugin);
		
		if (Hacks.speed.enable)
			register("Anti Speed Hack", new NoHackSpeed(), PM, plugin);
		
		if (Hacks.fly.enable)
			register("Anti Fly Hack", new NoHackFly(), PM, plugin);
		
		if (tekkitrestrict.config.getBoolean2(ConfigFile.Logging, "SplitLogs", true))
			register("SplitLogs Command", new TRCmdListener(), PM, plugin);
		
		if (LogFilter.logNEIGive)
			register("NEI ItemSpawn", new TRNeiListener(), PM, plugin);
	}
	
	private static final void register(String name, Listener listener, PluginManager PM, tekkitrestrict plugin){
		try {
			PM.registerEvents(listener, plugin);
		} catch (Exception ex){
			Log.Warning.load("Unable to register the " + name + " listener!", true);
			Log.debugEx(ex);
		}
	}
	
	public static void register(Listener listener){
		tekkitrestrict plugin = tekkitrestrict.getInstance();
		PluginManager PM = plugin.getServer().getPluginManager();
		PM.registerEvents(listener, plugin);
	}
	
	public static void assignEEPatch(){
		final tekkitrestrict tr = tekkitrestrict.getInstance();
		final PluginManager PM = tr.getServer().getPluginManager();
		if (!EEPSettings.arcanering.isEmpty() || !EEPSettings.blackholeband.isEmpty() || !EEPSettings.harvestring.isEmpty() || !EEPSettings.firering.isEmpty() || !EEPSettings.flyring.isEmpty() || !EEPSettings.voidring.isEmpty() || !EEPSettings.zeroring.isEmpty())
			register("EERings", new EERingListener(), PM, tr);
		
		if (!EEPSettings.dest1.isEmpty() || !EEPSettings.dest2.isEmpty() || !EEPSettings.dest3.isEmpty())
			register("EEDestruction", new EEDestructionListener(), PM, tr);
		
		if (!EEPSettings.evertide.isEmpty() || !EEPSettings.volcanite.isEmpty())
			register("EEAmulets", new EEAmuletListener(), PM, tr);
		
		if (!EEPSettings.dmaxe.isEmpty() || !EEPSettings.dmpick.isEmpty() || !EEPSettings.dmhoe.isEmpty() || !EEPSettings.dmshovel.isEmpty() ||
			!EEPSettings.dmhammer.isEmpty() || !EEPSettings.dmshears.isEmpty() || !EEPSettings.dmsword.isEmpty())
			register("EEDMTools", new EEDMToolListener(), PM, tr);
		
		if (!EEPSettings.rmaxe.isEmpty() || !EEPSettings.rmpick.isEmpty() || !EEPSettings.rmhoe.isEmpty() || !EEPSettings.rmshovel.isEmpty() ||
			!EEPSettings.rmhammer.isEmpty() || !EEPSettings.rmshears.isEmpty() || !EEPSettings.rmsword.isEmpty() || !EEPSettings.katar.isEmpty() || !EEPSettings.morningstar.isEmpty())
			register("EERMTools", new EERMToolListener(), PM, tr);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "AllowRMFurnaceOreDuplication", true))
			register("EEDuplication", new EEDuplicateListener(), PM, tr);
		
		if (!EEPSettings.MaxCharge.isEmpty())
			register("EECharge", new EEChargeListener(), PM, tr);
		
		if (!EEPSettings.phil.isEmpty())
			register("EEPhilosopher", new EEPhilosopherListener(), PM, tr);
		
		if (!EEPSettings.trans.isEmpty())
			register("EETransmution", new EETransmutionListener(), PM, tr);
		
		if (!EEPSettings.pedestal.isEmpty())
			register("EEPedestal", new EEPedestalListener(), PM, tr);
		
		if (!EEPSettings.watch.isEmpty())
			register("EEWatch", new EEWatchListener(), PM, tr);
	}
	
	public static void unregisterAll(){
		HandlerList.unregisterAll(tekkitrestrict.getInstance());
	}
}
