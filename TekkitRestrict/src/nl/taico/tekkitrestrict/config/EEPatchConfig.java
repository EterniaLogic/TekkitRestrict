package nl.taico.tekkitrestrict.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import nl.taico.tekkitrestrict.tekkitrestrict;

public class EEPatchConfig {
	private static File file;
	private static FileConfiguration fileConfig;
	public static FileConfiguration getConfig() {
		if (fileConfig == null) {
			reloadConfig();
		}
		return fileConfig;
	}

	@SuppressWarnings("resource")
	public static void reloadConfig() {
		if (fileConfig == null) {
			file = new File(tekkitrestrict.getInstance().getDataFolder(), "EEPatch.yml");
		}
		fileConfig = YamlConfiguration.loadConfiguration(file);

		InputStream defConfigStream = tekkitrestrict.getInstance().getResource("EEPatch.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			fileConfig.setDefaults(defConfig);
		}
	}

	public static void saveConfig() {
		if (fileConfig == null || file == null) return;
		try {
			getConfig().save(file);
		} catch (IOException ex) {
			System.out.println("Could not save config to " + file);
		}
	}

	public static void saveDefaultConfig() {
		if (file == null) {
			file = new File(tekkitrestrict.getInstance().getDataFolder(), "EEPatch.yml");
		}
		if (!file.exists()) tekkitrestrict.getInstance().saveResource("EEPatch.yml", false);
	}
	
	public static void saveDefaultConfigForced() {
		if (file == null) {
			file = new File(tekkitrestrict.getInstance().getDataFolder(), "EEPatch.yml");
		}
		tekkitrestrict.getInstance().saveResource("EEPatch.yml", true);
	}
}
