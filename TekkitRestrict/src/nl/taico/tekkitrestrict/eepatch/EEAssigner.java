package nl.taico.tekkitrestrict.eepatch;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import nl.taico.tekkitrestrict.eepatch.destlisteners.EEDest1Listener;
import nl.taico.tekkitrestrict.eepatch.destlisteners.EEDest2Listener;
import nl.taico.tekkitrestrict.eepatch.destlisteners.EEDest3Listener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EEArcaneRingListener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EEBHBListener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EEHarvestRingListener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EEIgnitionRingListener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EESWRGListener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EEVoidRingListener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EEZeroRingListener;

import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

public class EEAssigner {
	public static void assign(){
		PluginManager PM = Bukkit.getPluginManager();
		tekkitrestrict tr = tekkitrestrict.getInstance();
		if (!EEPSettings.arcanering.isEmpty())
			PM.registerEvents(new EEArcaneRingListener(), tr);
		
		if (!EEPSettings.blackholeband.isEmpty())
			PM.registerEvents(new EEBHBListener(), tr);
		
		if (!EEPSettings.harvestring.isEmpty())
			PM.registerEvents(new EEHarvestRingListener(), tr);
		
		if (!EEPSettings.firering.isEmpty())
			PM.registerEvents(new EEIgnitionRingListener(), tr);
		
		if (!EEPSettings.flyring.isEmpty())
			PM.registerEvents(new EESWRGListener(), tr);
		
		if (!EEPSettings.voidring.isEmpty())
			PM.registerEvents(new EEVoidRingListener(), tr);
		
		if (!EEPSettings.zeroring.isEmpty())
			PM.registerEvents(new EEZeroRingListener(), tr);
		
		if (!EEPSettings.dest1.isEmpty())
			PM.registerEvents(new EEDest1Listener(), tr);
		if (!EEPSettings.dest2.isEmpty())
			PM.registerEvents(new EEDest2Listener(), tr);
		if (!EEPSettings.dest3.isEmpty())
			PM.registerEvents(new EEDest3Listener(), tr);
		
		
		if (!tekkitrestrict.config.getBoolean("AllowRMFurnaceOreDuplication", true))
			PM.registerEvents(new EEDuplicateListener(), tr);
		
		if (!EEPSettings.MaxCharge.isEmpty())
			PM.registerEvents(new EEChargeListener(), tr);
	}
}
