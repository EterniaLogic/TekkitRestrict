/* IMPORTANT TODO'S:
 * - Add check to disable Anti pedestal EMC gen if EEPatch of the right version is installed (it fixes it)
 * 
 */
package nl.taico.tekkitrestrict;

import ic2.api.Ic2Recipes;
import ic2.common.EntityMiningLaser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.minecraft.server.Block;
import net.minecraft.server.ItemStack;
import net.minecraft.server.RedPowerLogic;
import net.minecraft.server.RedPowerMachine;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.commands.*;
import nl.taico.tekkitrestrict.config.*;
import nl.taico.tekkitrestrict.database.Database;
import nl.taico.tekkitrestrict.eepatch.EEPSettings;
import nl.taico.tekkitrestrict.functions.TREMCSet;
import nl.taico.tekkitrestrict.functions.TRLWCProtect;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRLogFilter;
import nl.taico.tekkitrestrict.functions.TRNoClick;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.functions.TRRecipeBlock;
import nl.taico.tekkitrestrict.functions.TRSafeZone;
import nl.taico.tekkitrestrict.lib.config.TRFileConfiguration;
import nl.taico.tekkitrestrict.lib.config.YamlConfiguration;
import nl.taico.tekkitrestrict.listeners.Assigner;
import nl.taico.tekkitrestrict.objects.TRVersion;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;
import nl.taico.tekkitrestrict.objects.TREnums.DBType;
import nl.taico.tekkitrestrict.threads.TRThreadManager;

public class tekkitrestrict extends JavaPlugin {
	private static tekkitrestrict instance;
	public static Logger log;
	public static TRFileConfiguration config;
	public static boolean EEEnabled = false;
	public static Boolean EEPatch = null, FixPack = null;
	
	/** Indicates if tekkitrestrict is disabling. Threads use this to check if they should stop. */
	public static boolean disable = false;
	////
	public static TRVersion version;
	public static double dbversion = 1.2;
	public static int dbworking = 0;
	
	public static DBType dbtype = DBType.Unknown;
	public static Database db;
	public static Updater updater2 = null;
	
	private static TRLogFilter filter = null;
	public static ArrayList<YamlConfiguration> configList = new ArrayList<YamlConfiguration>();
	
	public static boolean useTMetrics = true;
	public static TMetrics tmetrics;
	
	@Override
	public void onLoad() {
		instance = this; //Set the instance
		
		log = getLogger(); //Set the logger
		
		version = new TRVersion(getDescription().getVersion());//.equals("1.2") ? "1.20" : getDescription().getVersion());
		Log.init();
		
		//#################### load Config ####################
		saveDefaultConfig(false); //Copy config files

		config = this.getConfigx(); //Load the configuration files
		final double configVer = config.getDouble(ConfigFile.General, "ConfigVersion", 0.9);
		if (configVer < 1.1) UpdateConfigFiles.v09();
		else if (configVer != 2.1){
			if (configVer < 2.1 && linkEEPatch()) EEPatchConfig.upgradeFile();
			if (configVer < 2.0) SafeZonesConfig.upgradeFile();
			if (configVer < 1.8) DatabaseConfig.upgradeFile();
			if (configVer < 1.7) LoggingConfig.upgradeFile();
			if (configVer < 1.5){
				AdvancedConfig.upgradeFile();
				HackDupeConfig.upgradeOldHackFile();
				ModModificationsConfig.upgradeFile();
				TPerformanceConfig.upgradeFile();
			}
			GeneralConfig.upgradeFile();
			reloadConfig();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			
		}
		
		try {//Load all settings
			load();
		} catch (final Exception ex) {
			Warning.load("An error occurred: Unable to load settings!", true);
			Log.Exception(ex, true);
		}
		//#####################################################
		
		
		//##################### load SQL ######################
		
		log.info("[DB] Loading Database...");
		if (!TRDB.loadDB()){
			Warning.dbAndLoad("[DB] Failed to load Database!", true);
		} else {
			switch (dbtype){
				case SQLite:
					if (TRDB.initSQLite()) {
						log.info("[SQLite] SQLite Database loaded!");
					} else {
						Warning.dbAndLoad("[SQLite] Failed to load SQLite Database!", true);
					}
					break;
				case MySQL:
					if (TRDB.initMySQL()){
						log.info("[MySQL] Database connection established!");
					} else {
						Warning.dbAndLoad("[MySQL] Failed to connect to MySQL Database!", true);
					}
					break;
				default:
					Warning.dbAndLoad("[DB] Unknown Database type set!", true);
					break;
			}
		}
		//#####################################################
		
		
		//###################### RPTimer ######################
		if (config.getBoolean2(ConfigFile.General, "UseAutoRPTimer", false)){
			try {
				final double value = config.getDouble(ConfigFile.ModModifications, "RPTimerMin", 0.2d);
				RedPowerLogic.minInterval = (int) Math.round((value-0.1d) * 20d); // set minimum interval for logic timers...
				log.info("Set the RedPower Timer Min interval to " + value + " seconds.");
			} catch (final Exception ex) {
				Warning.load("Setting the RedPower Timer failed!", false);
				Log.debugEx(ex);
			}
		}
		//#####################################################
		
		
		//###################### Patch CC #####################
		if (config.getBoolean2(ConfigFile.General, "PatchComputerCraft", true)){
			PatchCC.start();
		}
		//#####################################################
		
		
		if (config.getBoolean2(ConfigFile.General, "AddTekkitMaterialNames", true)){
			NameProcessor.addTekkitMaterials();
			log.fine("Added Tekkit Material Names");
		}
		
		if (config.getBoolean2(ConfigFile.Logging, "FilterLogs", true) || config.getBoolean2(ConfigFile.Logging, "SplitLogs", true)){
			final Enumeration<String> cc = LogManager.getLogManager().getLoggerNames();
			if (filter == null) filter = new TRLogFilter();
			while (cc.hasMoreElements()){
				Logger.getLogger(cc.nextElement()).setFilter(filter);
			}
		}
	}
	@Override
	public void onEnable() {
		//BlockBreaker anti-dupe
		try {
			final ArrayList<Block> miningLaser = new ArrayList<Block>();
			for (final Block block : EntityMiningLaser.unmineableBlocks) miningLaser.add(block);
			
			miningLaser.add(Block.byId[194]);
			EntityMiningLaser.unmineableBlocks = miningLaser.toArray(new Block[miningLaser.size()]);
			log.fine("Patched Mining Laser + Auto Crafting Table MK II dupe.");
		} catch (Exception ex){
			Warning.load("Unable to patch Mining Laser + Auto Crafting Table MK II dupe!", false);
			Log.debugEx(ex);
		}
		
		try {
			//.add(dmg << 15 | id)
			RedPowerMachine.breakerBlacklist.add(Integer.valueOf(-1 << 15 | 194));
			
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(6362));//REP
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(6359));//Wireless sniffer
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(6363));//Private sniffer
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(27562));//Alcbag
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(27585));//Divining ROd
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(30122));//Cropnalyser
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(30104));//Debug item
			
			
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(27592));//transtablet
			RedPowerMachine.deployerBlacklist.add(Integer.valueOf(7493));//Ender pouch
			log.fine("Patched BlockBreaker + Auto Crafting Table MK II dupe.");
			log.fine("Patched most Deployer Crash Bugs.");
		} catch (final Exception ex){
			Warning.load("Unable to patch BlockBreaker + Auto Crafting Table MK II dupe!", false);
			Warning.load("Unable to patch Deployer Crash Bugs!", false);
			Log.debugEx(ex);
		}
		
		try {
			Ic2Recipes.addMaceratorRecipe(new ItemStack(135, 1, 2), new ItemStack(30254, 4, 0));
			Ic2Recipes.addMaceratorRecipe(new ItemStack(135, 1, 3), new ItemStack(30255, 4, 0));
			log.fine("Added Missing Nether Ores recipes.");
		} catch (final Exception ex){
			Warning.load("Unable to add missing Nether Ore recipes.", false);
			Log.debugEx(ex);
		}
		
		final PluginManager pm = this.getServer().getPluginManager();
		// determine if EE2 is enabled by using pluginmanager
		tekkitrestrict.EEEnabled = pm.isPluginEnabled("mod_EE");
		
		final TRThreadManager ttt = new TRThreadManager();
		try {
			Assigner.assign(); //Register the required listeners
		} catch (Exception ex){
			Warning.load("A severe error occurred: Unable to start listeners!", true);
			Log.Exception(ex, true);
		}
		
		final Plugin wg = pm.getPlugin("WorldGuard"), gp = pm.getPlugin("GriefPrevention"), ps = pm.getPlugin("PreciousStones");
		
		if (Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable(){
			public void run(){
				TRSafeZone.init(wg, gp, ps);
				TRLimiter.init();
			}
		})==-1){
			log.warning("Unable to schedule Limiter and SafeZones. Using non-scheduled methods.");
			TRSafeZone.init(wg, gp, ps);
			TRLimiter.init();
		}

		getCommand("tekkitrestrict").setExecutor(new TRCmdTr());
		getCommand("openalc").setExecutor(new TRCmdOpenAlc());
		getCommand("openinv").setExecutor(new TRCmdOpenInv());
		getCommand("clearalc").setExecutor(new TRCmdClearAlc());
		getCommand("tpic").setExecutor(new TRCmdTpic());
		getCommand("checklimits").setExecutor(new TRCmdCheck());
		
		try {
			if (pm.isPluginEnabled("PermissionsEx")) {
				TRPermHandler.permEx = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
				log.info("PEX is enabled!");
			}
		} catch (final Exception ex) {
			Warning.load("Linking with Pex Failed!", false);
			Log.debugEx(ex);
			// Was not able to load permissionsEx
		}
		
		try {
			ttt.init();
		} catch (final Exception ex) {
			Warning.load("An error occurred: Unable to start threads!", true);
			Log.Exception(ex, true);
		}
		
		try {
			initHeartBeat();
		} catch (final Exception ex){
			Warning.load("An error occurred: Unable to initiate Limiter Manager correctly!", false);
			Log.Exception(ex, false);
		}
		
		if (linkEEPatch()){
			boolean success = true;
			try {
				Assigner.assignEEPatch();
			} catch (Exception ex){
				success = false;
			}
			
			if (success)
				log.info("Linked with EEPatch for extended functionality!");
			else
				Warning.load("Linking with EEPatch Failed!", true);
		} else {
			log.info("EEPatch is not available. Extended EE integration disabled.");
		}
		
		Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
			public void run(){
				if (config.getBoolean2(ConfigFile.General, "Auto-Update", true)){
					//updater = new Updater_Old(this, "tekkit-restrict", this.getFile(), Updater_Old.UpdateType.DEFAULT, true);
					updater2 = new Updater(tekkitrestrict.this, 44061, tekkitrestrict.this.getFile(), Updater.UpdateType.DEFAULT, true);
				} else if (config.getBoolean2(ConfigFile.General, "CheckForUpdateOnStartup", true)){
					//updater = new Updater_Old(this, "tekkit-restrict", this.getFile(), Updater_Old.UpdateType.NO_DOWNLOAD, true);
					updater2 = new Updater(tekkitrestrict.this, 44061, tekkitrestrict.this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
					//if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) log.info(ChatColor.GREEN + "There is an update available: " + updater.getLatestVersionString() + ". Use /tr admin update ingame to update.");
					if (updater2.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) log.info(ChatColor.GREEN + "There is an update available: " + updater2.getLatestName() + ". Use /tr admin update ingame to update.");
				}
			}
		});
		
		
		//##################### Log Filter ####################
		if (Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable(){
			public void run() {
				if (config.getBoolean2(ConfigFile.Logging, "FilterLogs", true) || config.getBoolean2(ConfigFile.Logging, "SplitLogs", true)){
					final Enumeration<String> cc = LogManager.getLogManager().getLoggerNames();
					if (filter == null) filter = new TRLogFilter();
					while (cc.hasMoreElements()){
						Logger.getLogger(cc.nextElement()).setFilter(filter);
					}
				}
			}
		})==-1){
			if (config.getBoolean2(ConfigFile.Logging, "FilterLogs", true) || config.getBoolean2(ConfigFile.Logging, "SplitLogs", true)){
				Warning.load("Unable to register logfilters! Error: Cannot schedule!", false);
			}
		}
		
		//#####################################################
		
		initMetrics();
		tmetrics = new TMetrics(this, config.getBoolean2(ConfigFile.General, "ShowTMetricsWarnings", true));
		
		if (config.getBoolean2(ConfigFile.General, "UseTMetrics", true)){
			tmetrics.start();
		}
		
		if (pm.isPluginEnabled("Essentials") && config.getBoolean2(ConfigFile.General, "AddEEItemsToEssentials", true)){
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				public void run(){
					if (NameProcessor.addEEItemsToEssentials()){
						log.fine("Added EE Items to Essentials");
					} else {
						Warning.load("Failed to add EE Items to Essentials ItemDB!", false);
					}
				}
			}, 10);
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				if (!Warning.loadWarnings.isEmpty()){
					log.warning("There were some warnings while loading TekkitRestrict!");
					log.warning("Use /tr warnings load to view them again (in case you missed them).");
				} else if (!Warning.dbWarnings.isEmpty()){
					log.warning("There were some database warnings while loading TekkitRestrict!");
					log.warning("Use /tr warnings db to view them again (in case you missed them).");
				}
			}
		});
	
		log.info("TekkitRestrict v" + version.fullVer + " Enabled!");
	}
	@Override
	public void onDisable() {
		disable = true;
		tmetrics.stop();
		//metrics.stop();
		TRThreadManager.stop();
		
		TRLogger.saveLogs();
		TRLogFilter.disable();
		Log.deinit();
		FileLog.closeAll();//Save all logs
		db.close();//close db connection
		getServer().getScheduler().cancelTasks(this);
		log.info("TekkitRestrict v " + version.fullVer + " disabled!");
		version = null;
		dbtype = null;
		updater2 = null;
		db = null;
		tmetrics = null;
		config = null;
		configList = null;
		filter = null;
		log = null;
		instance = null;
	}
	
	@NonNull public static tekkitrestrict getInstance() {
		return instance;
	}

	private void initMetrics(){
		try {
			final Metrics metrics = new Metrics(this);
			final Metrics.Graph g = metrics.createGraph("TekkitRestrict Stats");
			
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
						List<String> ssr = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "RecipeBlock");
						for (String s : ssr) {
							
							try {
								size += TRItemProcessor.processItemString(s).size();
							} catch (TRException e) {
								continue;
							}

						}
						ssr = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "RecipeFurnaceBlock");
						for (String s : ssr) {
							try {
								size += TRItemProcessor.processItemString(s).size();
							} catch (TRException e) {
								continue;
							}
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
		} catch (IOException ex) {
			Warning.load("Metrics failed to start.", false);
		}
	}
	
	public static boolean linkEEPatch(){
		if (EEPatch != null) return EEPatch.booleanValue();
		try {
			Class.forName("ee.events.EEEvent");
			EEPatch = true;
			return true;
		} catch (ClassNotFoundException ex) {
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
		TRLWCProtect.reload();
		if (linkEEPatch()){
			EEPSettings.loadAllDisabledActions();
			EEPSettings.loadMaxCharge();
		}		
	}
	
	/**
	 * @param listeners Reload Listeners as well?
	 * @param silent If silent is true, no notice of the reload will appear in the console.
	 */
	public void reload(final boolean listeners, final boolean silent) {
		if (listeners) Assigner.unregisterAll();
		
		final int id = Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable(){
			public void run(){
				reloadConfig();
				
				load();
				TRThreadManager.reload();
				
				//Stop TMetrics if the user disabled it in the config and reloaded.
				if (!config.getBoolean(ConfigFile.General, "UseTMetrics", true)){
					tmetrics.stop();
				}
				if (listeners){
					Bukkit.getScheduler().scheduleSyncDelayedTask(tekkitrestrict.this, new Runnable(){
						public void run(){
							Assigner.assign();
							if (linkEEPatch()) Assigner.assignEEPatch();
							if (!silent) log.info("TekkitRestrict Reloaded!");
						}
					});
				} else {
					if (!silent) log.info("TekkitRestrict Reloaded!");
				}
			}
		});
		if (id == -1){
			log.severe("Unable to reload tekkitrestrict! Error: cannot schedule reload.");
		}
	}

	@NonNull private TRFileConfiguration getConfigx() {
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

	@NonNull private YamlConfiguration reloadc(@NonNull String loc) {
		final File cf = new File("plugins"+File.separator+"tekkitrestrict"+File.separator + loc);
		// tekkitrestrict.log.info(cf.getAbsolutePath());
		final YamlConfiguration conf = YamlConfiguration.loadConfiguration(cf);
		// newConfig.loadFromString(s)
		final InputStream defConfigStream = getResource(loc);
		if (defConfigStream != null) {
			final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			conf.setDefaults(defConfig);
			try {
				defConfigStream.close();
			} catch (final IOException ex) {
				Warning.load("Exception while trying to reload the config!", false);
				Log.Exception(ex, true);
			}
		}
		return conf;
	}
	
	@Deprecated
	@Override
	public void saveDefaultConfig() {
		//Level ll = log.getLevel();
		//log.setLevel(Level.SEVERE);
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
		//log.setLevel(ll);
	}
	public void saveDefaultConfig(boolean force) {
		//Level ll = log.getLevel();
		//log.setLevel(Level.SEVERE);
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
		//log.setLevel(ll);
	}
	
	public void saveResource(String resourcePath, boolean replace){
		if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        @SuppressWarnings("resource")
		final InputStream in = getResource(resourcePath);
        if (in == null) {
        	throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
        }

        final File outFile = new File(getDataFolder(), resourcePath);
        final int lastIndex = resourcePath.lastIndexOf('/');
        final File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
            	final OutputStream out = new FileOutputStream(outFile);
            	final byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                getLogger().log(Level.FINEST, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (final IOException ex) {
        	getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
	}
	
	public void Update(){
		//updater = new Updater_Old(this, "tekkit-restrict", this.getFile(), Updater_Old.UpdateType.DEFAULT, true);
		updater2 = new Updater(this, 44061, this.getFile(), Updater.UpdateType.DEFAULT, true);
	}
	
	public boolean backupConfig(@NonNull final String sourceString, @NonNull final String destString){
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
