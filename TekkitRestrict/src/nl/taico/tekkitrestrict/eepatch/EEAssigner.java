package nl.taico.tekkitrestrict.eepatch;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import nl.taico.tekkitrestrict.eepatch.amuletlisteners.EEAmuletListener;
import nl.taico.tekkitrestrict.eepatch.destlisteners.EEDestructionListener;
import nl.taico.tekkitrestrict.eepatch.ringlisteners.EERingListener;

import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;

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
		
		if (!tekkitrestrict.config.getBoolean("AllowRMFurnaceOreDuplication", true))
			PM.registerEvents(new EEDuplicateListener(), tr);
		
		if (!EEPSettings.MaxCharge.isEmpty())
			PM.registerEvents(new EEChargeListener(), tr);
	}
}
