/**
 * 
 */
package nl.taico.tekkitrestrict;

import java.io.File;

public class UpdateConfigFiles {

	public static final String s = File.separator;
	
	public static void v09(){
		tekkitrestrict.loadWarning("The config file version differs from the current one.");
		tekkitrestrict.loadWarning("Backing up old config files and writing new ones.");
		
		String path = "plugins"+s+"tekkitrestrict"+s;
		String bpath = path+"config_backup";
		File temp = new File(bpath);
		temp.mkdirs();
		bpath += s;
		
		tekkitrestrict tr = tekkitrestrict.getInstance();
		
		tr.backupConfig(path + "General.config.yml", bpath + "General.config.yml");
		tr.backupConfig(path + "Advanced.config.yml", bpath + "Advanced.config.yml");
		tr.backupConfig(path + "ModModifications.config.yml", bpath + "ModModifications.config.yml");
		tr.backupConfig(path + "DisableClick.config.yml", bpath + "DisableClick.config.yml");
		tr.backupConfig(path + "DisableItems.config.yml", bpath + "DisableItems.config.yml");
		tr.backupConfig(path + "Hack.config.yml", bpath + "Hack.config.yml");
		tr.backupConfig(path + "LimitedCreative.config.yml", bpath + "LimitedCreative.config.yml");
		tr.backupConfig(path + "Logging.config.yml", bpath + "Logging.config.yml");
		tr.backupConfig(path + "TPerformance.config.yml", bpath + "TPerformance.config.yml");
		tr.backupConfig(path + "MicroPermissions.config.yml", bpath + "MicroPermissions.config.yml");
		tr.backupConfig(path + "SafeZones.config.yml", bpath + "SafeZones.config.yml");
		tr.backupConfig(path + "EEPatch.config.yml", bpath + "EEPatch.config.yml");
		
		tr.saveDefaultConfig(true);
		tr.reloadConfig();
		
		tekkitrestrict.loadWarning("New config files have been written. Please set the required settings in the new config files.");
	}
	
}
