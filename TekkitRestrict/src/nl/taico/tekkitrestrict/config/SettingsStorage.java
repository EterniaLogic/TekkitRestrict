package nl.taico.tekkitrestrict.config;

import nl.taico.taeirlib.config.Config;
import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TekkitRestrict;

public class SettingsStorage {
	public static Config advancedConfig;
	public static Config bannedConfig;
	public static Config databaseConfig;
	public static Config generalConfig;
	public static Config groupPermsConfig;
	public static Config hackDupeConfig;
	public static Config limitedCreativeConfig;
	public static Config limiterConfig;
	public static Config loggingConfig;
	public static Config modModificationsConfig;
	public static Config performanceConfig;
	public static Config safeZoneConfig;
	public static Config unloadConfig;
	public static Config eepatchConfig;

	//Intentionally empty: the class is loaded when this method is called.
	public static void loadConfigs(){
		advancedConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "Advanced.yml");
		bannedConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "Banned.yml");
		databaseConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "Database.yml");
		generalConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "General.yml");
		groupPermsConfig 		= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "GroupPermissions.yml");
		hackDupeConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "HackDupe.yml");
		limitedCreativeConfig 	= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "LimitedCreative.yml");
		limiterConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "Limiter.yml");
		loggingConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "Logging.yml");
		modModificationsConfig 	= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "ModModifications.yml");
		performanceConfig 		= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "Performance.yml");
		safeZoneConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "SafeZones.yml");
		unloadConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "ChunkUnloader.yml");
		eepatchConfig 			= Config.getNewConfig(TekkitRestrict.instance.getLogger(), TekkitRestrict.instance.getDataFolder(), "EEPatch.yml");
	}

	public static void reloadConfigs(){
		Log.trace("Reloading Configs From Disk...");
		boolean b = true;
		b &= advancedConfig.reloadConfig();
		b &= bannedConfig.reloadConfig();
		b &= databaseConfig.reloadConfig();
		b &= generalConfig.reloadConfig();
		b &= groupPermsConfig.reloadConfig();
		b &= hackDupeConfig.reloadConfig();
		b &= limitedCreativeConfig.reloadConfig();
		b &= limiterConfig.reloadConfig();
		b &= loggingConfig.reloadConfig();
		b &= modModificationsConfig.reloadConfig();
		b &= performanceConfig.reloadConfig();
		b &= safeZoneConfig.reloadConfig();
		b &= unloadConfig.reloadConfig();

		b &= eepatchConfig.reloadConfig();

		if (!b) Log.Warning.config("One or more config files were not reloaded.", false);
	}

}
