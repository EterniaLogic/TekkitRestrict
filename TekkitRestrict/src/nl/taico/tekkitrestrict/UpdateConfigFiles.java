/**
 * 
 */
package nl.taico.tekkitrestrict;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.Log.Warning;

public class UpdateConfigFiles {

	public static final String s = File.separator;
	
	public static void v09(){
		Warning.load("The config file version differs from the current one.", false);
		Warning.load("Backing up old config files and writing new ones.", false);
		
		final String path = "plugins"+s+"tekkitrestrict"+s;
		String bpath = path+"config_backup";
		File temp = new File(bpath);
		temp.mkdirs();
		bpath += s;
		
		backupConfig(path + "General.config.yml", bpath + "General.config.yml");
		backupConfig(path + "Advanced.config.yml", bpath + "Advanced.config.yml");
		backupConfig(path + "ModModifications.config.yml", bpath + "ModModifications.config.yml");
		backupConfig(path + "DisableClick.config.yml", bpath + "DisableClick.config.yml");
		backupConfig(path + "DisableItems.config.yml", bpath + "DisableItems.config.yml");
		backupConfig(path + "Hack.config.yml", bpath + "Hack.config.yml");
		backupConfig(path + "LimitedCreative.config.yml", bpath + "LimitedCreative.config.yml");
		backupConfig(path + "Logging.config.yml", bpath + "Logging.config.yml");
		backupConfig(path + "TPerformance.config.yml", bpath + "TPerformance.config.yml");
		backupConfig(path + "MicroPermissions.config.yml", bpath + "MicroPermissions.config.yml");
		backupConfig(path + "SafeZones.config.yml", bpath + "SafeZones.config.yml");
		backupConfig(path + "EEPatch.config.yml", bpath + "EEPatch.config.yml");
		
		TR.saveDefaultConfig(true);
		TR.reloadConfig();
		
		Warning.load("New config files have been written. Please set the required settings in the new config files.", false);
	}
	
	public static boolean backupConfig(@NonNull final String sourceString, @NonNull final String destString){
		try {
			final File sourceFile = new File(sourceString);
			final File destFile = new File(destString);
			if (!sourceFile.exists()) return false;
			
			if(!destFile.exists()) destFile.createNewFile();
			
			FileChannel source = null;
			FileChannel destination = null;

			try {
				source = new FileInputStream(sourceFile).getChannel();
				destination = new FileOutputStream(destFile).getChannel();
				destination.transferFrom(source, 0, source.size());
			} finally {
				if(source != null) source.close();
				if(destination != null) destination.close();
			}

			if(source != null) source.close();
			if(destination != null) destination.close();

		} catch (final IOException ex){
			Warning.load("Cannot backup config: " + sourceString, false);
			return false;
		}
		return true;

	}
}
