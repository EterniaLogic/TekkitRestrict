package nl.taico.tekkitrestrict.eepatch;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.eepatch.amuletlisteners.EEAmuletListener;
import nl.taico.tekkitrestrict.eepatch.destlisteners.EEDestructionListener;
import nl.taico.tekkitrestrict.eepatch.otherlisteners.EEPhilosopherListener;
import nl.taico.tekkitrestrict.eepatch.otherlisteners.EETransmutionListener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EERingListener;
import nl.taico.tekkitrestrict.eepatch.toollisteners.EEDMToolListener;
import nl.taico.tekkitrestrict.eepatch.toollisteners.EERMToolListener;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

public class EEAssigner {
	public static void assign(){
		PluginManager PM = Bukkit.getPluginManager();
		tekkitrestrict tr = tekkitrestrict.getInstance();
		if (!EEPSettings.arcanering.isEmpty() || !EEPSettings.blackholeband.isEmpty() || !EEPSettings.harvestring.isEmpty() || !EEPSettings.firering.isEmpty() || !EEPSettings.flyring.isEmpty() || !EEPSettings.voidring.isEmpty() || !EEPSettings.zeroring.isEmpty())
			PM.registerEvents(new EERingListener(), tr);
		
		if (!EEPSettings.dest1.isEmpty() || !EEPSettings.dest2.isEmpty() || !EEPSettings.dest3.isEmpty())
			PM.registerEvents(new EEDestructionListener(), tr);
		
		if (!EEPSettings.evertide.isEmpty() || !EEPSettings.volcanite.isEmpty())
			PM.registerEvents(new EEAmuletListener(), tr);
		
		if (!EEPSettings.dmaxe.isEmpty() || !EEPSettings.dmpick.isEmpty() || !EEPSettings.dmhoe.isEmpty() || !EEPSettings.dmshovel.isEmpty() ||
			!EEPSettings.dmhammer.isEmpty() || !EEPSettings.dmshears.isEmpty() || !EEPSettings.dmsword.isEmpty())
			PM.registerEvents(new EEDMToolListener(), tr);
		
		if (!EEPSettings.rmaxe.isEmpty() || !EEPSettings.rmpick.isEmpty() || !EEPSettings.rmhoe.isEmpty() || !EEPSettings.rmshovel.isEmpty() ||
			!EEPSettings.rmhammer.isEmpty() || !EEPSettings.rmshears.isEmpty() || !EEPSettings.rmsword.isEmpty() || !EEPSettings.katar.isEmpty() || !EEPSettings.morningstar.isEmpty())
			PM.registerEvents(new EERMToolListener(), tr);
		
		if (!tekkitrestrict.config.getBoolean(ConfigFile.EEPatch, "AllowRMFurnaceOreDuplication", true))
			PM.registerEvents(new EEDuplicateListener(), tr);
		
		if (!EEPSettings.MaxCharge.isEmpty())
			PM.registerEvents(new EEChargeListener(), tr);
		
		if (!EEPSettings.phil.isEmpty())
			PM.registerEvents(new EEPhilosopherListener(), tr);
		
		if (!EEPSettings.trans.isEmpty())
			PM.registerEvents(new EETransmutionListener(), tr);
	}
}
