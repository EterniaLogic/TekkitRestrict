/* IMPORTANT TODO'S:
 * - Add check to disable Anti pedestal EMC gen if EEPatch of the right version is installed (it fixes it)
 * 
 */
package nl.taico.tekkitrestrict;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.logging.Level;

import net.minecraft.server.RedPowerLogic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.commands.*;
import nl.taico.tekkitrestrict.config.EEPatchConfig;
import nl.taico.tekkitrestrict.config.SettingsStorage;
import nl.taico.tekkitrestrict.database.Database;
import nl.taico.tekkitrestrict.eepatch.EEPSettings;
import nl.taico.tekkitrestrict.functions.TRChunkUnloader2;
import nl.taico.tekkitrestrict.functions.TREMCSet;
import nl.taico.tekkitrestrict.functions.TRLWCProtect;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRNoClick;
import nl.taico.tekkitrestrict.functions.TRNoInteract;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.functions.TRRecipeBlock;
import nl.taico.tekkitrestrict.functions.TRSafeZone;
import nl.taico.tekkitrestrict.listeners.Assigner;
import nl.taico.tekkitrestrict.logging.TRLogFilterPlus;
import nl.taico.tekkitrestrict.logging.TRLogSplitterPlus;
import nl.taico.tekkitrestrict.objects.TRVersion;
import nl.taico.tekkitrestrict.objects.TREnums.DBType;
import nl.taico.tekkitrestrict.threads.TRThreadManager;

import static nl.taico.tekkitrestrict.config.SettingsStorage.*;

public class tekkitrestrict extends JavaPlugin {
	public static tekkitrestrict instance;
	public static boolean EEEnabled = false;
	public static Boolean EEPatch = null, FixPack = null;
	
	/** Indicates if tekkitrestrict is disabling. Threads use this to check if they should stop. */
	public static boolean disable = false;
	public static TRVersion version;
	public static double dbversion = 1.2;
	public static int dbworking = 0;
	
	public static DBType dbtype = DBType.Unknown;
	public static Database db;
	public static Updater updater = null;
	
	public static boolean useTMetrics = true;
	public TMetrics tmetrics;
	
	@Override
	public void onLoad() {
		instance = this; //Set the instance
		TR.instance = this;
		version = new TRVersion(getDescription().getVersion());
		Log.init();
		
		SettingsStorage.loadConfigs();
		
		//#################################################### load Settings ####################################################
		try {
			load();
		} catch (Exception ex) {
			Warning.load("An error occurred: Unable to load settings!", true);
			Log.Exception(ex, true);
		}
		//#######################################################################################################################
		
		
		//###################################################### load SQL #######################################################
		Log.info("[DB] Loading Database...");
		if (!TRDB.loadDB()){
			Warning.dbAndLoad("[DB] Failed to load Database!", true);
		} else {
			switch (dbtype){
				case SQLite:
					if (TRDB.initSQLite()) {
						Log.info("[SQLite] SQLite Database loaded!");
					} else {
						Warning.dbAndLoad("[SQLite] Failed to load SQLite Database!", true);
					}
					break;
				case MySQL:
					if (TRDB.initMySQL()){
						Log.info("[MySQL] Database connection established!");
					} else {
						Warning.dbAndLoad("[MySQL] Failed to connect to MySQL Database!", true);
					}
					break;
				default:
					Warning.dbAndLoad("[DB] Unknown Database type set!", true);
					break;
			}
		}
		//#######################################################################################################################
		
		
		//####################################################### RPTimer #######################################################
		if (modModificationsConfig.getBoolean("RPTimer.SetMinimalTime", true)){
			try {
				final double value = modModificationsConfig.getDouble("RPTimer.MinTime", 1.0d);
				RedPowerLogic.minInterval = (int) Math.round((value-0.1d) * 20d); // set minimum interval for logic timers...
				Log.fine("Set the RedPower Timer Min interval to " + value + " seconds.");
			} catch (Exception ex) {
				Warning.load("Setting the RedPower Timer failed!", false);
				Log.debugEx(ex);
			}
		}
		//#######################################################################################################################
		
		
		//####################################################### Patch CC ######################################################
		if (generalConfig.getBoolean("PatchComputerCraft", true)) TRPatches.patchCC();
		//#######################################################################################################################
		
		
		//################################################ Tekkit Material Names ################################################
		if (generalConfig.getBoolean("AddTekkitMaterialNames", true)){
			NameProcessor.addTekkitMaterials();
			Log.fine("Added Tekkit Material Names");
		}
		//#######################################################################################################################
	}
	
	@Override
	public void onEnable() {
		//############################################### Mining Laser Anti Dupe ################################################
		if (TRPatches.patchMiningLaser()) Log.fine("Patched Mining Laser + Auto Crafting Table MK II dupe.");
		else Warning.load("Unable to patch Mining Laser + Auto Crafting Table MK II dupe!", false);
		//#######################################################################################################################
		
		//################################################# Deployer Anti Crash #################################################
		if (TRPatches.patchDeployer()) Log.fine("Patched most Deployer Crash Bugs.");
		else Warning.load("Unable to patch Deployer Crash Bugs!", false);
		//#######################################################################################################################
		
		//################################################ BlockBreker Anti Dupe ################################################
		if (TRPatches.patchBlockBreaker()) Log.fine("Patched BlockBreaker + Auto Crafting Table MK II dupe.");
		else Warning.load("Unable to patch BlockBreaker + Auto Crafting Table MK II dupe!", false);
		//#######################################################################################################################
		
		//################################################# Nether Ore Recipes ##################################################
		if (TRPatches.addNetherOresRecipes()) Log.fine("Added Missing Nether Ores recipes.");
		else Warning.load("Unable to add missing Nether Ore recipes.", false);
		//#######################################################################################################################
		
		final PluginManager pm = this.getServer().getPluginManager();

		tekkitrestrict.EEEnabled = pm.isPluginEnabled("mod_EE"); // determine if EE2 is enabled
		
		//################################################# Register Listeners ##################################################
		Assigner.assign();
		//#######################################################################################################################

		linkEEPatch();
		
		if (schedule(false, new Runnable(){
			public void run(){
				TRSafeZone.init(pm.getPlugin("WorldGuard"), pm.getPlugin("GriefPrevention"), pm.getPlugin("PreciousStones"));
				TRLimiter.init();
			}
			}) == -1){
			Log.Debug("Unable to schedule Limiter and SafeZones. Using non-scheduled methods.");
			TRSafeZone.init(pm.getPlugin("WorldGuard"), pm.getPlugin("GriefPrevention"), pm.getPlugin("PreciousStones"));
			TRLimiter.init();
		}

		//################################################## Register Commands ##################################################
		getCommand("tekkitrestrict").setExecutor(new TRCmdTr());
		getCommand("openalc").setExecutor(new TRCmdOpenAlc());
		getCommand("openinv").setExecutor(new TRCmdOpenInv());
		getCommand("clearalc").setExecutor(new TRCmdClearAlc());
		getCommand("tpic").setExecutor(new TRCmdTpic());
		getCommand("checklimits").setExecutor(new TRCmdCheck());
		//#######################################################################################################################
		
		linkPex();
		
		//#################################################### Start Threads ####################################################
		try {
			new TRThreadManager().init();
		} catch (Exception ex) {
			Warning.load("An error occurred: Unable to start threads!", true);
			Log.Exception(ex, true);
		}
		//#######################################################################################################################
		
		if (!scheduleLimiterTasks()) Warning.load("Unable to start Limiter Manager!", false);
		
		addEEItemsToEssentials();
		
		for (World world : Bukkit.getWorlds()){
			new TRChunkUnloader2(world);
		}
		
		//#################################################### Check Update #####################################################
		schedule(false, new Runnable() {
			public void run(){
				if (generalConfig.getBoolean("Auto-Update", true)){
					updater = new Updater(tekkitrestrict.this, 44061, tekkitrestrict.this.getFile(), Updater.UpdateType.DEFAULT, true);
				} else if (generalConfig.getBoolean("CheckForUpdateOnStartup", true)){
					updater = new Updater(tekkitrestrict.this, 44061, tekkitrestrict.this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
					if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) Log.info(ChatColor.GREEN + "There is an update available: " + updater.getLatestName() + ". Use /tr admin update ingame to update.");
				}
			}
		});
		//#######################################################################################################################

		//#################################################### Init Metrics #####################################################
		if (!initMetrics()) Warning.load("Metrics failed to start.", false);
		initTMetrics();
		//#######################################################################################################################
		
		schedule(true, new Runnable() {
			public void run() {
				if (!Warning.loadWarnings.isEmpty()){
					Log.warning("There were some warnings while loading TekkitRestrict!");
					Log.warning("Use /tr warnings load to view them again (in case you missed them).");
				} else if (!Warning.dbWarnings.isEmpty()){
					Log.warning("There were some database warnings while loading TekkitRestrict!");
					Log.warning("Use /tr warnings db to view them again (in case you missed them).");
				}
			}
		});
		
		Log.info("TekkitRestrict v" + version.fullVer + " Enabled!");
	}
	
	@Override
	public void onDisable() {
		disable = true;
		tmetrics.stop();
		//metrics.stop();
		TRThreadManager.stop();
		
		TRLogger.saveLogs();
		TRLogSplitterPlus.disable();
		TRLogFilterPlus.disable();
		//TRLogFilter.disable();
		Log.deinit();
		FileLog.closeAll();//Save all logs
		db.close();//close db connection
		getServer().getScheduler().cancelTasks(this);
		getLogger().info("TekkitRestrict v " + version.fullVer + " disabled!");
		version = null;
		dbtype = null;
		updater = null;
		db = null;
		tmetrics = null;
		
		//TRFileConfiguration.configs = null;
		instance = null;
	}
	
	public static tekkitrestrict getInstance() {
		return instance;
	}

	private final int schedule(boolean sync, Runnable task){
		return schedule(sync, task, -1, -1);
	}
	private final int schedule(boolean sync, Runnable task, long delay){
		return schedule(sync, task, delay, -1);
	}
	private final int schedule(boolean sync, Runnable task, long delay, long repeat){
		if (sync){
			if (repeat == -1){
				if (delay == -1){
					return Bukkit.getScheduler().scheduleSyncDelayedTask(this, task);
				} else {
					return Bukkit.getScheduler().scheduleSyncDelayedTask(this, task, delay);
				}
			} else {
				return Bukkit.getScheduler().scheduleSyncRepeatingTask(this, task, delay, repeat);
			}
		} else {
			if (repeat == -1){
				if (delay == -1){
					return Bukkit.getScheduler().scheduleAsyncDelayedTask(this, task);
				} else {
					return Bukkit.getScheduler().scheduleAsyncDelayedTask(this, task, delay);
				}
			} else {
				return Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, task, delay, repeat);
			}
		}
	}
		
	public static boolean hasEEPatch(){
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
	
	public void load(){
		TRConfigCache.loadConfigCache();
		//################################################## Load New Logging ###################################################
		if (loggingConfig.getBoolean("FilterLogs", true)){
			TRLogFilterPlus.loadFilters(loggingConfig.getConfigurationSection("Filters"));
			TRLogFilterPlus.assignFilters();
		}
		if (loggingConfig.getBoolean("SplitLogs", true)){
			TRLogSplitterPlus.loadSplitters(loggingConfig.getConfigurationSection("Splitters"), loggingConfig.getConfigurationSection("CommandSplitters"));
			TRLogSplitterPlus.assignSplitter();
		}
		//#######################################################################################################################
		
		
		TRItemProcessor2.load();
		TRNoItem.reload(); //Banned items and limited creative.
		TRNoInteract.reload();
		TRNoClick.load();
		TRLimiter.reload();
		TRRecipeBlock.reload();
		TREMCSet.reload();
		TRLWCProtect.reload();
		if (hasEEPatch()){
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
				SettingsStorage.reloadConfigs();
				
				load();
				TRThreadManager.reload();
				
				//Stop TMetrics if the user disabled it in the config and reloaded.
				if (!generalConfig.getBoolean("UseTMetrics", true)){
					tmetrics.stop();
				}
				if (listeners){
					Bukkit.getScheduler().scheduleSyncDelayedTask(tekkitrestrict.this, new Runnable(){
						public void run(){
							Assigner.assign();
							if (hasEEPatch()) Assigner.assignEEPatch();
							if (!silent) Log.info("TekkitRestrict Reloaded!");
						}
					});
				} else {
					if (!silent) Log.info("TekkitRestrict Reloaded!");
				}
			}
		});
		if (id == -1){
			Log.severe("Unable to reload tekkitrestrict! Error: cannot schedule reload.");
		}
	}
	
	@Override
	@Deprecated
	public void reloadConfig() {
		reloadConfigs();
	}
	
	@Override
	@Deprecated
	public void saveDefaultConfig() {
		saveDefaultConfig(false);
	}
	
	@Deprecated
	public void saveDefaultConfig(boolean force) {
		if (force){
			EEPatchConfig.saveDefaultConfigForced();
		} else {
			EEPatchConfig.saveDefaultConfig();
		}
		try {
			saveResource("General.config.yml", force);
			saveResource("Advanced.config.yml", force);
			saveResource("ModModifications.config.yml", force);
			
			File disinteract = new File(getDataFolder(), "DisableInteract.config.yml");
			File disclick = new File(getDataFolder(), "DisableClick.config.yml");
			if (!disclick.exists() && !disinteract.exists()) saveResource("DisableInteract.config.yml", force);
			
			saveResource("DisableItems.config.yml", force);
			saveResource("HackDupe.config.yml", force);
			saveResource("LimitedCreative.config.yml", force);
			saveResource("Logging.config.yml", force);
			saveResource("TPerformance.config.yml", force);
			saveResource("GroupPermissions.config.yml", force);
			saveResource("SafeZones.config.yml", force);
			saveResource("Database.config.yml", force);
			if (hasEEPatch()) saveResource("EEPatch.config.yml", force);
		} catch (Exception e) {}
	}
	
	@Override
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
                getLogger().finest("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
        	getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
	}
	
	public void Update(){
		updater = new Updater(this, 44061, this.getFile(), Updater.UpdateType.DEFAULT, true);
	}
	
	private final boolean initMetrics(){
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
					return TRRecipeBlock.furnaceSize + TRRecipeBlock.recipesSize;
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
			return true;
		} catch (IOException ex) {
			return false;
		}
	}
	
	private final void initTMetrics(){
		tmetrics = new TMetrics(this, generalConfig.getBoolean("ShowTMetricsWarnings", true));
		
		if (generalConfig.getBoolean("UseTMetrics", true)){
			tmetrics.start();
		}
	}
	
	private final void linkEEPatch(){
		if (hasEEPatch()){
			try {
				Assigner.assignEEPatch();
				Log.info("Linked with EEPatch for extended functionality!");
			} catch (Exception ex){
				Warning.load("Linking with EEPatch Failed!", true);
			}
		} else {
			Log.info("EEPatch is not installed. Extended EE integration disabled.");
		}
	}
	
	private final boolean scheduleLimiterTasks(){
		try {
			int j = schedule(true, new Runnable(){
				boolean err = false;
				public void run(){
					try {
						TRLimiter.manageData();
					} catch (Exception ex){
						if (!err){
							Warning.other("An error occurred with the Limiter Data Manager! (This error will only be logged once)", false);
							Log.Exception(ex, false);
							err = true;
						}
					}
				}
			}, 30l*20l, 15l*20l);
		
			int i = schedule(false, new Runnable() {
				public void run() {
					TRLimiter.expireLimiters();
				}
			}, 60L, 32L);
			return j != -1 && i != -1;
		} catch (Exception ex){
			Log.Exception(ex, false);
			return false;
		}
	}
	
	/** Schedule the addition of EE2 items to Essentials. */
	private final void addEEItemsToEssentials(){
		if (Bukkit.getPluginManager().isPluginEnabled("Essentials") && generalConfig.getBoolean("AddEEItemsToEssentials", true)){
			schedule(true, new Runnable(){
				public void run(){
					if (NameProcessor.addEEItemsToEssentials()){
						Log.fine("Added EE Items to Essentials");
					} else {
						Warning.load("Failed to add EE Items to Essentials ItemDB!", false);
					}
				}
			}, 10);
		}
	}
	
	private final void linkPex(){
		if (Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")){
			try {
				TRPermHandler.permEx = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
				Log.info("PEX is enabled!");
			} catch (Exception ex) {
				Warning.load("Linking with Pex Failed!", false);
				Log.debugEx(ex);
			}
		}
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