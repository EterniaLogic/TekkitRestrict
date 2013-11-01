package com.github.dreadslicer.tekkitrestrict;

import ic2.common.EntityMiningLaser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.minecraft.server.Block;
import net.minecraft.server.RedPowerLogic;
import net.minecraft.server.RedPowerMachine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import nl.taico.tekkitrestrict.config.AdvancedConfig;
import nl.taico.tekkitrestrict.config.GeneralConfig;
import nl.taico.tekkitrestrict.config.HackDupeConfig;
import nl.taico.tekkitrestrict.config.ModModificationsConfig;
import nl.taico.tekkitrestrict.config.SafeZonesConfig;
import nl.taico.tekkitrestrict.config.TPerformanceConfig;
import nl.taico.tekkitrestrict.eepatch.EEPSettings;

import com.github.dreadslicer.tekkitrestrict.Log.Warning;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandAlc;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandCheck;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandTPIC;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandTR;
import com.github.dreadslicer.tekkitrestrict.database.Database;
import com.github.dreadslicer.tekkitrestrict.lib.TRFileConfiguration;
import com.github.dreadslicer.tekkitrestrict.lib.YamlConfiguration;
import com.github.dreadslicer.tekkitrestrict.listeners.Assigner;
import com.github.dreadslicer.tekkitrestrict.listeners.CraftingListener;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.DBType;
import com.github.dreadslicer.tekkitrestrict.objects.TRItem;
import com.github.dreadslicer.tekkitrestrict.objects.TRVersion;

public class tekkitrestrict extends JavaPlugin {
	private static tekkitrestrict instance;
	public static Logger log;
	public static TRFileConfiguration config;
	public static boolean EEEnabled = false;
	public static Boolean EEPatch = null;
	
	/** Indicates if tekkitrestrict is disabling. Threads use this to check if they should stop. */
	public static boolean disable = false;
	
	public static TRVersion version;
	public static double dbversion = 1.2;
	public static int dbworking = 0;
	
	public static DBType dbtype = DBType.Unknown;
	public static Database db;
	//public static Updater_Old updater = null;
	public static Updater updater2 = null;
	
	public static ExecutorService basfo = Executors.newCachedThreadPool();
	
	private static TRThread ttt = null;
	private static TRLogFilter filter = null;
	public static LinkedList<YamlConfiguration> configList = new LinkedList<YamlConfiguration>();
	
	public static boolean useTMetrics = true;
	private static TMetrics tmetrics;
	
	/**
	 * Log a warning while the plugin is still loading.
	 * If you type /tr warnings, you will see the warnings again.
	 */
	public static void loadWarning(String warning){
		Warning.loadWarnings.add(warning);
		log.warning(warning);
	}
	
	@Override
	public void onLoad() {
		instance = this; //Set the instance
		log = getLogger(); //Set the logger
		//version = getDescription().getVersion();
		version = new TRVersion(getDescription().getVersion());
		Log.init();
		
		//#################### load Config ####################
		saveDefaultConfig(false); //Copy config files

		config = this.getConfigx(); //Load the configuration files
		double configVer = config.getDouble("ConfigVersion", 0.9);
		if (configVer < 1.1)
			UpdateConfigFiles.v09();//0 --> newest
		else if (configVer < 1.5) {
			AdvancedConfig.upgradeFile();
			GeneralConfig.upgradeFile();
			HackDupeConfig.upgradeOldHackFile();
			ModModificationsConfig.upgradeFile();
			SafeZonesConfig.upgradeFile();
			TPerformanceConfig.upgradeFile();
			reloadConfig();
		}
		
		try {//Load all settings
			load();
		} catch (Exception ex) {
			Log.Warning.load("An error occurred: Unable to load settings!");
			Log.Exception(ex, true);
		}
		//#####################################################
		
		
		//##################### load SQL ######################
		
		log.info("[DB] Loading Database...");
		if (!TRDB.loadDB()){
			loadWarning("[DB] Failed to load Database!");
		} else {
			if (dbtype == DBType.SQLite) {
				if (TRDB.initSQLite())
					log.info("[SQLite] SQLite Database loaded!");
				else {
					loadWarning("[SQLite] Failed to load SQLite Database!");
				}
			} else if (dbtype == DBType.MySQL) {
				if (TRDB.initMySQL()){
					log.info("[MySQL] Database connection established!");
				} else {
					loadWarning("[MySQL] Failed to connect to MySQL Database!");
				}
			} else {
				loadWarning("[DB] Unknown Database type set!");
			}
		}
		//#####################################################
		
		
		//###################### RPTimer ######################
		if (config.getBoolean2(ConfigFile.General, "UseAutoRPTimer", false)){
			try {
				double value = config.getDouble("RPTimerMin", 0.2d);
				int ticks = (int) Math.round((value-0.1d) * 20d);
				RedPowerLogic.minInterval = ticks; // set minimum interval for logic timers...
				log.info("Set the RedPower Timer Min interval to " + value + " seconds.");
			} catch (Exception e) {
				loadWarning("Setting the RedPower Timer failed!");
			}
		}
		//#####################################################
		
		
		//###################### Patch CC #####################
		if (config.getBoolean2(ConfigFile.General, "PatchComputerCraft", true)){
			PatchCC.start();
		}
		//#####################################################
		
		//BlockBreaker anti-dupe
		try {
			ArrayList<Block> miningLaser = new ArrayList<Block>();
			
			for (Block block : EntityMiningLaser.unmineableBlocks){
				miningLaser.add(block);
			}
			miningLaser.add(Block.byId[194]);
			EntityMiningLaser.unmineableBlocks = miningLaser.toArray(new Block[miningLaser.size()]);
			log.fine("Patched Mining Laser + Auto Crafting Table MK II dupe.");
		} catch (Exception ex){
			loadWarning("Unable to patch Mining Laser + Auto Crafting Table MK II dupe!");
		}
		
		try {
			RedPowerMachine.breakerBlacklist.add(Integer.valueOf(-1 << 15 | 194));
			
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(0 << 15 | 6362));//REP
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(0 << 15 | 6359));//Wireless sniffer
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(0 << 15 | 6363));//Private sniffer
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(0 << 15 | 27562));//Alcbag
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(0 << 15 | 27585));//Divining ROd
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(0 << 15 | 30122));//Cropnalyser
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(0 << 15 | 30104));//Debug item
			
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(0 << 15 | 27592));//transtablet
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(0 << 15 | 7493));//Ender pouch
			log.fine("Patched BlockBreaker + Auto Crafting Table MK II dupe.");
			log.fine("Patched most Deployer Crash Bugs.");
		} catch (Exception ex){
			loadWarning("Unable to patch BlockBreaker + Auto Crafting Table MK II dupe!");
			loadWarning("Unable to patch Deployer Crash Bugs!");
		}
	}
	@Override
	public void onEnable() {
		config.getConfigurationSection("").getValues(true).keySet();
		
		ttt = new TRThread();
		try {
			Assigner.assign(); //Register the required listeners
		} catch (Exception ex){
			Log.Warning.load("A severe error occurred: Unable to start listeners!");
			Log.Exception(ex, true);
		}
		
		TRSafeZone.init();
		
		TRLimiter.init();

		getCommand("tekkitrestrict").setExecutor(new TRCommandTR());
		getCommand("openalc").setExecutor(new TRCommandAlc());
		getCommand("tpic").setExecutor(new TRCommandTPIC());
		getCommand("checklimits").setExecutor(new TRCommandCheck());

		// determine if EE2 is enabled by using pluginmanager
		PluginManager pm = this.getServer().getPluginManager();
		
		tekkitrestrict.EEEnabled = pm.isPluginEnabled("mod_EE");

		try {
			if (pm.isPluginEnabled("PermissionsEx")) {
				TRPermHandler.permEx = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
				log.info("PEX is enabled!");
			}
		} catch (Exception ex) {
			log.info("Linking with Pex Failed!");
			// Was not able to load permissionsEx
		}
		
		try {
			ttt.init();
		} catch (Exception ex) {
			Log.Warning.load("An error occurred: Unable to start threads!");
			Log.Exception(ex, true);
		}
		
		try {
			initHeartBeat();
		} catch (Exception ex){
			Log.Warning.load("An error occurred: Unable to initiate Limiter correctly!");
			Log.Exception(ex, false);
		}
		
		log.info("Linking with EEPatch for extended functionality ...");
		if (linkEEPatch()){
			boolean success = true;
			EEPSettings.loadMaxCharge();
			EEPSettings.loadAllDisabledActions();
			try {
				Assigner.assignEEPatch();
			} catch (Exception ex){
				success = false;
			}
			
			if (success)
				log.info("Linked with EEPatch!");
			else 
				log.warning("Linking with EEPatch Failed!");
		} else {
			log.info("EEPatch is not available. Extended functionality disabled.");
		}
		
		if (config.getBoolean2(ConfigFile.General, "Auto-Update", true)){
			//updater = new Updater_Old(this, "tekkit-restrict", this.getFile(), Updater_Old.UpdateType.DEFAULT, true);
			updater2 = new Updater(this, 44061, this.getFile(), Updater.UpdateType.DEFAULT, true);
		} else if (config.getBoolean2(ConfigFile.General, "CheckForUpdateOnStartup", true)){
			//updater = new Updater_Old(this, "tekkit-restrict", this.getFile(), Updater_Old.UpdateType.NO_DOWNLOAD, true);
			updater2 = new Updater(this, 44061, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
			//if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) log.info(ChatColor.GREEN + "There is an update available: " + updater.getLatestVersionString() + ". Use /tr admin update ingame to update.");
			if (updater2.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) log.info(ChatColor.GREEN + "There is an update available: " + updater2.getLatestName() + ". Use /tr admin update ingame to update.");
		}
		
		//##################### Log Filter ####################
		if (config.getBoolean("UseLogFilter", true)){
			Enumeration<String> cc = LogManager.getLogManager().getLoggerNames();
			filter = new TRLogFilter();
			while (cc.hasMoreElements()){
				Logger.getLogger(cc.nextElement()).setFilter(filter);
			}
		}
		//#####################################################
		
		initMetrics();
		tmetrics = new TMetrics(this, config.getBoolean2(ConfigFile.General, "ShowTMetricsWarnings", true));
		
		if (config.getBoolean2(ConfigFile.General, "UseTMetrics", true)){
			tmetrics.start();
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				if (Warning.loadWarnings()){
					log.warning("There were some warnings while loading TekkitRestrict!");
					log.warning("Use /tr warnings load to view them again (in case you missed them).");
				}
			}
		});
		
		CraftingListener.setupCraftHook();
		log.info("TekkitRestrict v" + version.fullVer + " Enabled!");
	}
	@Override
	public void onDisable() {
		disable = true;
		tmetrics.stop();
		
		ttt.disableItemThread.interrupt();
		ttt.entityRemoveThread.interrupt();
		ttt.gemArmorThread.interrupt();
		ttt.worldScrubThread.interrupt();
		ttt.saveThread.interrupt();
		//ttt.limitFlyThread.interrupt();
		
		try { Thread.sleep(1500); } catch (InterruptedException e) {} //Sleep for 1.5 seconds to allow the savethread to save.

		TRLogger.saveLogs();
		TRLogFilter.disable();
		Log.deinit();
		FileLog.closeAll();
		
		log.info("TekkitRestrict v " + version.fullVer + " disabled!");
	}
	
	public static tekkitrestrict getInstance() {
		return instance;
	}

	private void initMetrics(){
		try {
			Metrics metrics = new Metrics(this);
			Metrics.Graph g = metrics.createGraph("TekkitRestrict Stats");
			
			g.addPlotter(new Metrics.Plotter("Total Safezones") {
				@Override
				public int getValue() {
					return TRSafeZone.zones.size();
				}
			});
			
			g.addPlotter(new Metrics.Plotter("Recipe blocks") {
				@Override
				public int getValue() {
					try{
						int size = 0;
						List<String> ssr = tekkitrestrict.config.getStringList("RecipeBlock");
						for (int i = 0; i < ssr.size(); i++) {
							List<TRItem> iss;
							try {
								iss = TRItemProcessor.processItemString(ssr.get(i));
							} catch (TRException e) {
								continue;
							}
							size += iss.size();
						}
						ssr = tekkitrestrict.config.getStringList("RecipeFurnaceBlock");
						for (int i = 0; i < ssr.size(); i++) {
							List<TRItem> iss;
							try {
								iss = TRItemProcessor.processItemString(ssr.get(i));
							} catch (TRException e) {
								continue;
							}
							size += iss.size();
						}
						return size;
					} catch(Exception ex){
						return 0;
					}
				}
			});
			
			g.addPlotter(new Metrics.Plotter("Disabled items") {
				@Override
				public int getValue() {
					try {
						return TRNoItem.getBannedItemsAmount();
					} catch(Exception ex){
						return 0;
					}
				}
			});
			metrics.start();
		} catch (IOException e) {
			Warning.load("Metrics failed to start.");
		}
	}
	
	public boolean linkEEPatch(){
		if (EEPatch != null) return EEPatch.booleanValue();
		try {
			Class.forName("ee.events.EEEvent");
			EEPatch = true;
			return true;
		} catch (ClassNotFoundException e) {
			EEPatch = false;
			return false;
		}
	}
	
	/** Make the limiter expire limits every 32 ticks. */
	private void initHeartBeat() {
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				TRLimiter.expireLimiters();
			}
		}, 60L, 32L);
	}
	
	public void load(){
		TRConfigCache.loadConfigCache();
		TRItemProcessor.reload();
		TRNoItem.reload(); //Banned items and limited creative.
		TRNoClick.reload();
		TRLimiter.reload();
		TRRecipeBlock.reload();
		TREMCSet.reload();
		if (linkEEPatch()){
			EEPSettings.loadAllDisabledActions();
			EEPSettings.loadMaxCharge();
		}		
	}
	
	/**
	 * @param listeners Reload Listeners as well?
	 * @param silent If silent is true, no notice of the reload will appear in the console.
	 * @param startup If startup is true, it will not 
	 */
	public void reload(boolean listeners, boolean silent) {
		if (listeners) Assigner.unregisterAll();
		
		this.reloadConfig();
		config = this.getConfigx();
		load();
		TRThread.reload();
		
		//Stop TMetrics if the user disabled it in the config and reloaded.
		if (!config.getBoolean(ConfigFile.General, "UseTMetrics", true)){
			tmetrics.stop();
		}
		
		if (listeners){
			Assigner.assign();
			if (linkEEPatch()) Assigner.assignEEPatch();
		}
		
		if (!silent) log.info("TekkitRestrict Reloaded!");
	}

	private TRFileConfiguration getConfigx() {
		if (configList.size() == 0) {
			reloadConfigOldHack();
		}
		return (new TRFileConfiguration());
	}

	@Override
	public void reloadConfig() {
		configList.clear();
		configList.add(reloadc("General.config.yml"));
		configList.add(reloadc("Advanced.config.yml"));
		configList.add(reloadc("ModModifications.config.yml"));
		configList.add(reloadc("DisableClick.config.yml"));
		configList.add(reloadc("DisableItems.config.yml"));
		configList.add(reloadc("HackDupe.config.yml"));
		configList.add(reloadc("LimitedCreative.config.yml"));
		configList.add(reloadc("Logging.config.yml"));
		configList.add(reloadc("TPerformance.config.yml"));
		configList.add(reloadc("GroupPermissions.config.yml"));
		configList.add(reloadc("SafeZones.config.yml"));
		configList.add(reloadc("Database.config.yml"));
		if (linkEEPatch()) configList.add(reloadc("EEPatch.config.yml"));
	}
	
	private void reloadConfigOldHack() {
		configList.clear();
		configList.add(reloadc("General.config.yml"));
		configList.add(reloadc("Advanced.config.yml"));
		configList.add(reloadc("ModModifications.config.yml"));
		configList.add(reloadc("DisableClick.config.yml"));
		configList.add(reloadc("DisableItems.config.yml"));
		File hackfile = new File("plugins"+File.separator+"tekkitrestrict"+File.separator+"Hack.config.yml");
		//File newhackfile = new File("plugins"+File.separator+"tekkitrestrict"+File.separator+"HackDupe.config.yml");
		if (hackfile.exists())
			configList.add(reloadc("Hack.config.yml"));
		else
			configList.add(reloadc("HackDupe.config.yml"));
		configList.add(reloadc("LimitedCreative.config.yml"));
		configList.add(reloadc("Logging.config.yml"));
		configList.add(reloadc("TPerformance.config.yml"));
		configList.add(reloadc("GroupPermissions.config.yml"));
		configList.add(reloadc("SafeZones.config.yml"));
		configList.add(reloadc("Database.config.yml"));
		if (linkEEPatch()) configList.add(reloadc("EEPatch.config.yml"));
	}

	private YamlConfiguration reloadc(String loc) {
		File cf = new File("plugins"+File.separator+"tekkitrestrict"+File.separator + loc);
		// tekkitrestrict.log.info(cf.getAbsolutePath());
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(cf);
		// newConfig.loadFromString(s)
		InputStream defConfigStream = getResource(loc);
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			conf.setDefaults(defConfig);
			try {
				defConfigStream.close();
			} catch (IOException e) {
				loadWarning("Exception while trying to reload the config!");
				e.printStackTrace();
			}
		}
		return conf;
	}
	
	@Deprecated
	@Override
	public void saveDefaultConfig() {
		Level ll = log.getLevel();
		log.setLevel(Level.SEVERE);
		try {
			saveResource("General.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("Advanced.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("ModModifications.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("DisableClick.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("DisableItems.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("HackDupe.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("LimitedCreative.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("Logging.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("TPerformance.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("GroupPermissions.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("SafeZones.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("Database.config.yml", false);
		} catch (Exception e) {}
		try {
			if (linkEEPatch()){
				saveResource("EEPatch.config.yml", false);
			}
		} catch (Exception e) {}
		log.setLevel(ll);
	}
	public void saveDefaultConfig(boolean force) {
		Level ll = log.getLevel();
		log.setLevel(Level.SEVERE);
		try {
			saveResource("General.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("Advanced.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("ModModifications.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("DisableClick.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("DisableItems.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("HackDupe.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("LimitedCreative.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("Logging.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("TPerformance.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("GroupPermissions.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("SafeZones.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("Database.config.yml", force);
		} catch (Exception e) {}
		try {
			if (linkEEPatch()){
				saveResource("EEPatch.config.yml", force);
			}
		} catch (Exception e) {}
		log.setLevel(ll);
	}
	
	public void Update(){
		//updater = new Updater_Old(this, "tekkit-restrict", this.getFile(), Updater_Old.UpdateType.DEFAULT, true);
		updater2 = new Updater(this, 44061, this.getFile(), Updater.UpdateType.DEFAULT, true);
	}
	
	public boolean backupConfig(String sourceString, String destString){
		try {
			File sourceFile = new File(sourceString);
			File destFile = new File(destString);
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

		} catch (IOException ex){
			loadWarning("Cannot backup config: " + sourceString);
			return false;
		}
		return true;

	}

	public static String getFullVersion(){
		return getMajorVersion() + "." + getMinorVersion() + getExtraVersion();
	}
	public static String getMajorVersion(){
		return instance.getDescription().getVersion().split("\\D+")[0];
	}
	public static String getMinorVersion(){
		return instance.getDescription().getVersion().split("\\D+")[1];
	}
	public static String getExtraVersion(){
		String ver = instance.getDescription().getVersion().toLowerCase();
		if (ver.contains("beta")){
			String temp[] = ver.split(" ");
			if (temp.length >= 3 && temp[2].matches("\\d+")){//1.18 beta 2
				return "b" + temp[2];
			} else {
				return "b1";
			}
		} else if (ver.contains("dev")){
			String temp[] = ver.split(" ");
			if (temp.length >= 3 && temp[2].matches("\\d+")){
				return "d" + temp[2];
			} else {
				return "d1";
			}
		} else {
			return "";
		}
	}
	
	public static ArrayList<Integer> getAdvVersion(){
		return null;
	}
	
	public static boolean isBeta(){
		return getExtraVersion().contains("b");
	}
	public static boolean isDev(){
		return getExtraVersion().contains("d");
	}
}
