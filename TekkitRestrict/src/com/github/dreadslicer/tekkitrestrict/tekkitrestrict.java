package com.github.dreadslicer.tekkitrestrict;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.h31ix.updater.Updater;
import net.h31ix.updater.Updater.UpdateResult;
import net.minecraft.server.RedPowerLogic;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.ChunkUnloader;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Dupes;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Global;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Hacks;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.LWC;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Listeners;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.SafeZones;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Threads;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandAlc;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandTPIC;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandTR;
import com.github.dreadslicer.tekkitrestrict.database.SQLite;
import com.github.dreadslicer.tekkitrestrict.eepatch.EEPSettings;
import com.github.dreadslicer.tekkitrestrict.lib.TRFileConfiguration;
import com.github.dreadslicer.tekkitrestrict.lib.YamlConfiguration;
import com.github.dreadslicer.tekkitrestrict.listeners.Assigner;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.SSMode;

public class tekkitrestrict extends JavaPlugin {
	
	private static tekkitrestrict instance;
	public static Logger log;
	public static TRFileConfiguration config;
	public static boolean EEEnabled = false;
	public static Boolean EEPatch = null;
	
	/** Indicates if tekkitrestrict is disabling. Threads use this to check if they should stop. */
	public static boolean disable = false;
	
	public static String version;
	public static double dbversion = 1.2;
	public static int dbworking = 0;
	
	public static Object perm = null;
	public static SQLite db;
	public static Updater updater = null;
	
	public static ExecutorService basfo = Executors.newCachedThreadPool();
	
	private static TRThread ttt = null;
	private static TRLogFilter filter = null;
	public static LinkedList<YamlConfiguration> configList = new LinkedList<YamlConfiguration>();

	public ArrayList<String> msgCache = new ArrayList<String>();
	
	/**
	 * Log a warning while the plugin is still loading.
	 * If you type /tr warnings, you will see the warnings again.
	 */
	public static void loadWarning(String warning){
		log.warning(warning);
		instance.msgCache.add(warning);
	}
	
	@Override
	public void onLoad() {
		instance = this; //Set the instance
		log = getLogger(); //Set the logger
		
		//#################### load SQLite ####################
		log.info("[DB] Loading SQLite Database...");
		if (!loadSqlite()){
			loadWarning("[DB] Failed to load SQLite Database!");
		} else {
			if (initSqlite())
				log.info("[DB] SQLite Database loaded!");
			else {
				loadWarning("[DB] Failed to load SQLite Database!");
			}
		}

		saveDefaultConfig(false);

		config = this.getConfigx(); //Load the configuration files
		double configVer = config.getDouble("ConfigVersion", 0.9);
		if (configVer < 1.1)
			UpdateConfigFiles.v09();
		else if (configVer < 1.2)
			UpdateConfigFiles.v11();
		
		loadConfigCache();
		if (config.getBoolean("UseAutoRPTimer")){
			try {
				double value = config.getDouble("RPTimerMin", 0.2d);
				int ticks = (int) Math.round((value-0.1d) * 20d);
				RedPowerLogic.minInterval = ticks; // set minimum interval for logic timers...
				log.info("Set the RedPower Timer Min interval to " + value + " seconds.");
			} catch (Exception e) {
				loadWarning("Setting the RedPower Timer failed!");
			}
		}
		
		if (config.getBoolean("PatchComputerCraft", true)){
			PatchCC.start();
		}

		// ///////////
		
		
		if (config.getBoolean("UseLogFilter", true)){
			Enumeration<String> cc = LogManager.getLogManager().getLoggerNames();
			filter = new TRLogFilter();
			while(cc.hasMoreElements()) {
				Logger.getLogger(cc.nextElement()).setFilter(filter); 
			}
			log.info("Log filter Placed!");
		}
		
		// ///////////

		Log.init();
	}
	@Override
	public void onEnable() {
		ttt = new TRThread();
		Assigner.assign(); //Register the required listeners
		
		new TRLogger();
		
		TRSafeZone.init();
		
		TRLimitBlock.init();

		getCommand("tekkitrestrict").setExecutor(new TRCommandTR());
		getCommand("openalc").setExecutor(new TRCommandAlc());
		getCommand("tpic").setExecutor(new TRCommandTPIC());

		// determine if EE2 is enabled by using pluginmanager
		PluginManager pm = this.getServer().getPluginManager();
		if (pm.isPluginEnabled("mod_EE"))
			tekkitrestrict.EEEnabled = true;
		else
			tekkitrestrict.EEEnabled = false;

		try {
			if (pm.isPluginEnabled("PermissionsEx")) {
				perm = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
				log.info("PEX is enabled!");
			}
		} catch (Exception ex) {
			log.info("Linking with Pex Failed!");
			// Was not able to load permissionsEx
		}

		
		
		// Initiate noItem, Time-thread and our event listener
		try {
			reload(false, true);
			ttt.init(); //Start up all threads

			initHeartBeat();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("Linking with EEPatch for extended functionality ...");
		if (linkEEPatch()){
			boolean success = true;
			try {
				Assigner.assignEEPatch();
				//TODO add more here
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
		
		try {
			Metrics metrics = new Metrics(this);
			Metrics.Graph g = metrics.createGraph("TekkitRestrict Stats (Since last server restarts)");
			/*
			 * g.addPlotter(new Metrics.Plotter("Total Safezones") {
			 * 
			 * @Override public int getValue() { return TRSafeZone.zones.size();
			 * } });
			 */
			
			/*
			g.addPlotter(new Metrics.Plotter("Hack attempts") {
				@Override
				public int getValue() {
					try {
						return TRNoHack.hacks;
					} catch(Exception e){
						return 0;
					}
				}
			});*/
			g.addPlotter(new Metrics.Plotter("Recipe blocks") {
				@Override
				public int getValue() {
					try{
						int size = 0;
						List<String> ssr = tekkitrestrict.config.getStringList("RecipeBlock");
						for (int i = 0; i < ssr.size(); i++) {
							List<TRCacheItem> iss = TRCacheItem.processItemString("", ssr.get(i), -1);
							size += iss.size();
						}
						ssr = tekkitrestrict.config.getStringList("RecipeFurnaceBlock");
						for (int i = 0; i < ssr.size(); i++) {
							List<TRCacheItem> iss = TRCacheItem.processItemString("", ssr.get(i), -1);
							size += iss.size();
						}
						return size;
					}
					catch(Exception e){
						return 0;
					}
				}
			});
			
			/*g.addPlotter(new Metrics.Plotter("Dupe attempts") {
				@Override
				public int getValue() {
					try {
						return MetricValues.dupeAttempts;
					} catch(Exception ex){
						return 0;
					}
				}
			});*/
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
			// Failed to submit the stats :-(
		}
		
		if (Global.useNewBanSystem) TRCacheItem2.LoadNoItemConfig();
		
		version = getDescription().getVersion();
		
		if (config.getBoolean("Auto-Update", true)){
			updater = new Updater(this, "tekkit-restrict", this.getFile(), Updater.UpdateType.DEFAULT, true);
		} else if (config.getBoolean("CheckForUpdateOnStartup", true)){
			updater = new Updater(this, "tekkit-restrict", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) log.info(ChatColor.GREEN + "There is an update available: " + updater.getLatestVersionString() + ". Use /tr admin update ingame to update.");
		}
		
		if (config.getBoolean("UseLogFilter", true)){
			Enumeration<String> cc = LogManager.getLogManager().getLoggerNames();
			while (cc.hasMoreElements()){
				Logger l = Logger.getLogger(cc.nextElement());
				if (!(l.getFilter() instanceof TRLogFilter)) l.setFilter(filter);
			}
		}
		
		if (!msgCache.isEmpty()){
			log.warning("There were some warnings while loading TekkitRestrict!");
			log.warning("Use /tr warnings to view them again (in case you missed them).");
		}
		
		log.info("TekkitRestrict v" + version + " Enabled!");
		
		// TRThrottler.init();
	}
	@Override
	public void onDisable() {
		disable = true;
		
		ttt.disableItemThread.interrupt();
		ttt.entityRemoveThread.interrupt();
		ttt.gemArmorThread.interrupt();
		ttt.worldScrubThread.interrupt();
		ttt.saveThread.interrupt();
		if (ttt.bagCacheThread.isAlive()) ttt.bagCacheThread.interrupt();
		//ttt.limitFlyThread.interrupt();
		
		try { Thread.sleep(1500); } catch (InterruptedException e) {} //Sleep for 1.5 seconds to allow the savethread to save.
		//try {
		//	TRThread.originalEUEnd(); (Currently does nothing)
		//} catch (Exception ex) {
		//}
		TRLogger.saveLogs();
		TRLogFilter.disable();
		Log.deinit();
		FileLog.closeAll();
		
		log.info("TekkitRestrict v " + version + " disabled!");
	}

	public static tekkitrestrict getInstance() {
		return instance;
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
	
	public static void loadConfigCache(){
		Hacks.broadcast = config.getStringList(ConfigFile.Hack, "HackBroadcasts");
		Hacks.broadcastFormat = config.getString(ConfigFile.Hack, "HackBroadcastString", "{PLAYER} tried to {TYPE}-hack!"); //TODO add colors
		Hacks.kick = config.getStringList(ConfigFile.Hack, "HackKick");
		
		Hacks.fly = config.getBoolean(ConfigFile.Hack, "HackFlyEnabled", false);
		Hacks.flyTolerance = config.getInt(ConfigFile.Hack, "HackFlyTolerance", 60);
		Hacks.flyMinHeight = config.getInt(ConfigFile.Hack, "HackFlyMinHeight", 3);
		
		Hacks.forcefield = config.getBoolean(ConfigFile.Hack, "HackForcefieldEnabled", true);
		Hacks.ffTolerance = config.getInt(ConfigFile.Hack, "HackForcefieldTolerance", 15);
		Hacks.ffVangle = config.getDouble(ConfigFile.Hack, "HackForcefieldAngle", 40);
		
		Hacks.speed = config.getBoolean(ConfigFile.Hack, "HackSpeedEnabled", false);
		Hacks.speedTolerance = config.getInt(ConfigFile.Hack, "HackMoveSpeedTolerance", 30);
		Hacks.speedMaxSpeed = config.getDouble(ConfigFile.Hack, "HackMoveSpeedMax", 2.5);
		
		Dupes.broadcast = config.getStringList(ConfigFile.Hack, "Dupes.Broadcast");
		Dupes.broadcastFormat = config.getString(ConfigFile.Hack, "Dupes.BroadcastString", "{PLAYER} tried to dupe using {TYPE}!"); //TODO add colors
		Dupes.kick = config.getStringList(ConfigFile.Hack, "Dupes.Kick");
		Dupes.alcBag = config.getBoolean(ConfigFile.Hack, "Dupes.PreventAlchemyBagDupe", true);
		Dupes.rmFurnace = config.getBoolean(ConfigFile.Hack, "Dupes.PreventRMFurnaceDupe", true);
		Dupes.tankcart = config.getBoolean(ConfigFile.Hack, "Dupes.PreventTankCartDupe", true);
		Dupes.tankcartGlitch = config.getBoolean(ConfigFile.Hack, "Dupes.PreventTankCartGlitch", true);
		Dupes.transmute = config.getBoolean(ConfigFile.Hack, "Dupes.PreventTransmuteDupe", true);
		Dupes.pedestal = config.getBoolean(ConfigFile.Hack, "Dupes.PedestalEmcGen", true);
		
		Global.debug = config.getBoolean("ShowDebugMessages", false);
		Global.kickFromConsole = config.getBoolean("KickFromConsole", false);
		Global.useNewBanSystem = config.getBoolean("UseNewBannedItemsSystem", false);
		
		Listeners.UseBlockLimit = config.getBoolean("UseItemLimiter", true);
		Listeners.BlockCreativeContainer = config.getBoolean("LimitedCreativeNoContainer", true);
		
		TRConfigCache.LogFilter.replaceList = config.getStringList("LogFilter");
		TRConfigCache.LogFilter.splitLogs = config.getBoolean("SplitLogs", true);
		TRConfigCache.LogFilter.filterLogs = config.getBoolean("FilterLogs", true);
		TRConfigCache.LogFilter.logLocation = config.getString("SplitLogsLocation", "log");
		TRConfigCache.LogFilter.fileFormat = config.getString("FilenameFormat", "{TYPE}-{DAY}-{MONTH}-{YEAR}.log");
		TRConfigCache.LogFilter.logFormat = config.getString("LogStringFormat", "[{HOUR}:{MINUTE}:{SECOND}] {INFO}");
		
		Threads.gemArmorSpeed = config.getInt("GemArmorDThread");
		Threads.inventorySpeed = config.getInt("InventoryThread");
		Threads.saveSpeed = config.getInt("AutoSaveThreadSpeed");
		Threads.SSEntityRemoverSpeed = config.getInt("SSEntityRemoverThread");
		Threads.worldCleanerSpeed = config.getInt("WorldCleanerThread");
		
		Threads.GAMovement = config.getBoolean(ConfigFile.ModModifications, "AllowGemArmorDefensive", true);
		Threads.GAOffensive = config.getBoolean(ConfigFile.ModModifications, "AllowGemArmorOffensive", false);
		
		Threads.SSDisableEntities = config.getBoolean(ConfigFile.SafeZones, "InSafeZones.DisableEntities", false);
		Threads.SSDechargeEE = config.getBoolean(ConfigFile.SafeZones, "InSafeZones.DechargeEE", true);
		Threads.SSDisableArcane = config.getBoolean(ConfigFile.SafeZones, "InSafeZones.DisableRingOfArcana", true);
		
		Threads.RMDB = config.getBoolean("RemoveDisabledItemBlocks", false);
		Threads.UseRPTimer = config.getBoolean("UseAutoRPTimer", false);
		Threads.ChangeDisabledItemsIntoId = config.getInt("ChangeDisabledItemsIntoId", 3);
		Threads.RPTickTime = (int) Math.round(config.getDouble("RPTimerMin", 0.2) * 20);
		
		LWC.blocked = config.getStringList("LWCPreventNearLocked");
		if (LWC.blocked == null) LWC.blocked = Collections.synchronizedList(new LinkedList<String>());
		else LWC.blocked = Collections.synchronizedList(LWC.blocked);
		
		SafeZones.UseSafeZones = config.getBoolean("UseSafeZones", true);
		SafeZones.UseFactions = config.getBoolean("SSEnabledPlugins.Factions", true);
		SafeZones.UseGP = config.getBoolean("SSEnabledPlugins.GriefPrevention", true);
		SafeZones.UsePS = config.getBoolean("SSEnabledPlugins.PreciousStones", true);
		SafeZones.UseTowny = config.getBoolean("SSEnabledPlugins.Towny", true);
		SafeZones.UseWG = config.getBoolean("SSEnabledPlugins.WorldGuard", true);
		SafeZones.GPMode = SSMode.parse(config.getString("GriefPreventionSafeZoneMethod", "admin"));
		SafeZones.WGMode = SSMode.parse(config.getString("WorldGuardSafeZoneMethod", "specific"));
		
		//SafeZones.SSPlugins = config.getStringList("SSEnabledPlugins");
		//SafeZones.SSDisableFly = config.getBoolean("SSDisableFlying", false);
		//SafeZones.allGPClaimsAreSafezone = config.getBoolean("AllGriefPreventionClaimsAreSafezones", false);
		//SafeZones.allowNormalUser = config.getBoolean("SSAllowNormalUsersToHaveSafeZones", true);
		
		ChunkUnloader.enabled = config.getBoolean(ConfigFile.TPerformance, "UseChunkUnloader", false);
		ChunkUnloader.maxChunks = config.getInt(ConfigFile.TPerformance, "MaxChunks", 3000);
		ChunkUnloader.maxChunksEnd = config.getInt(ConfigFile.TPerformance, "MaxChunks.TheEnd", 200);
		ChunkUnloader.maxChunksNether = config.getInt(ConfigFile.TPerformance, "MaxChunks.Nether", 400);
		ChunkUnloader.maxChunksNormal = config.getInt(ConfigFile.TPerformance, "MaxChunks.Normal", 4000);
		ChunkUnloader.maxChunksTotal = config.getInt(ConfigFile.TPerformance, "MaxChunks.Total", 4000);
		ChunkUnloader.unloadOrder = config.getInt(ConfigFile.TPerformance, "UnloadOrder", 0);
		ChunkUnloader.maxRadii = config.getInt(ConfigFile.TPerformance, "MaxRadii", 256);
	}

	private static void initHeartBeat() {
		instance.getServer().getScheduler().scheduleAsyncRepeatingTask(instance, new Runnable() {
			@Override
			public void run() {
				TRLimitBlock.expireLimiters();
			}
		}, 60L, 32L);
	}

	private boolean newdb = false;
	/** @return If opening the database was successful. */
	private boolean loadSqlite() {
		File dbfile = new File(this.getDataFolder().getPath() + File.separator + "Data.db");
		if (!dbfile.exists()){
			newdb = true;
			log.info("[DB] Creating database file...");
		}
		db = new SQLite("Data", this.getDataFolder().getPath());
		
		return db.open();
	}

	private boolean initSqlite() {
		if (!db.isOpen()) {
			if (!db.open()){
				loadWarning("[DB] Cannot open the database!");
				dbworking = 20;
				return false;
			}
		}
		
		ResultSet prev = null;

		try {
			double verX = -1d;
			boolean purged = true;
			prev = db.query("SELECT version FROM tr_dbversion");
			if(prev.next()) verX = prev.getDouble("version");
			if(prev.next()) purged = false;
			
			prev.close();
			
			//Change version to 1.3 if it is lower
			if(verX != -1d && verX < dbversion){
				db.query("DELETE FROM 'tr_dbversion'");//clear table
				db.query("INSERT INTO 'tr_dbversion' (version) VALUES(" + dbversion + ");");//Insert new version
				transferDB12To13();//Transfer to version 1.3
			} else if (!purged) {
				db.query("DELETE FROM 'tr_dbversion'");//clear table
				db.query("INSERT INTO 'tr_dbversion' (version) VALUES(" + dbversion + ");");//Insert new version
			}
			
		} catch(Exception ex1){
			if(prev != null)
				try {prev.close();} catch (SQLException ex2) {}
			
			if (newdb) initNewDB();
			else transferOldDB();
		}
		if (dbworking == 0) return true;
		return false;
	}
	
	private void initNewDB(){
		dbworking = 0;
		log.info("[DB] Creating new database...");
		try {
			db.query("CREATE TABLE IF NOT EXISTS 'tr_dbversion' (version NUMERIC);");
			db.query("INSERT OR REPLACE INTO 'tr_dbversion' (version) VALUES("+dbversion+");");
		} catch (Exception ex) {
			loadWarning("[DB] Unable to write version to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				loadWarning("[DB] " + cur.toString());
			}
			dbworking += 1;
		}
	
		try {
			db.query("CREATE TABLE IF NOT EXISTS 'tr_saferegion' ( "
			+ "'id' INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "'name' TEXT,"
			+ "'mode' INT,"
			+ "'data' TEXT,"
			+ "'world' TEXT);");
		} catch (Exception ex) {
			loadWarning("[DB] Unable to create safezones table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				loadWarning("[DB] " + cur.toString());
			}
			
			dbworking += 2;
		}
		
		try {
			db.query("CREATE TABLE IF NOT EXISTS 'tr_limiter' ( "
			+ "'player' TEXT UNIQUE,"
			+ "'blockdata' TEXT);");
		} catch (Exception ex) {
			loadWarning("[DB] Unable to create limiter table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				loadWarning("[DB] " + cur.toString());
			}
			dbworking += 4;
		}
		
		dbFailMsg(dbworking);
		if (dbworking != 0)
			loadWarning("[DB] Not all tables could be created!");
		else 
			log.info("[DB] Database created successfully!");
	}

	/** Transfer the database from PRE-1.00 version format to the new format. */
	private void transferOldDB() {
		dbworking = 0;
		log.info("[DB] Transfering old database into the new database format...");
		
		LinkedList<LinkedList<String>> srvals = null, limvals=null;
		
		//tr_saferegion =	id name mode data world
		//tr_limiter = 		id player blockdata
		try {
			srvals = getTableVals("tr_saferegion");
		} catch(SQLException ex){
			loadWarning("[DB] Unable to transfer safezones from the old format to the new one!");
		}
		try {
			limvals = getTableVals("tr_limiter");
		} catch(SQLException ex){
			loadWarning("[DB] Unable to transfer limits from the old format to the new one!");
		}
		
		//Delete old tables
		try{db.query("DROP TABLE `tr_saferegion`;");} catch(Exception ex){}
		try{db.query("DROP TABLE `tr_limiter`;");} catch(Exception ex){}
		
		//################################### VERSION ###################################
		try {
			db.query("CREATE TABLE IF NOT EXISTS 'tr_dbversion' (version NUMERIC);");
			db.query("INSERT OR REPLACE INTO 'tr_dbversion' (version) VALUES("+dbversion+");");
		} catch (Exception ex) {
			loadWarning("[DB] Unable to write version to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				loadWarning("[DB] " + cur.toString());
			}
			dbworking += 1;
		}
		//###############################################################################
		
		//################################## SAFEZONES ##################################
		try {
			db.query("CREATE TABLE IF NOT EXISTS 'tr_saferegion' ( "
					+ "'id' INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "'name' TEXT,"
					+ "'mode' INT,"
					+ "'data' TEXT,"
					+ "'world' TEXT); ");
		} catch (Exception ex) {
			loadWarning("[DB] Unable to create safezones table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				loadWarning("[DB] " + cur.toString());
			}
			
			dbworking += 2;
		}
		
		try {
			//Import safezones
			if(srvals != null){
				for(LinkedList<String> vals:srvals){
					String toadd = "";
					for(String str:vals) toadd+=","+str;
					//toadd = toadd.replace("null", "''");
					if(toadd.startsWith(",")) toadd=toadd.substring(1, toadd.length());
					db.query("INSERT INTO 'tr_saferegion' VALUES("+toadd+");");
				}
				
				log.info("[DB] Transferred " + srvals.size() + " safezones.");
			}
		} catch (Exception ex) {
			loadWarning("[DB] Unable to write safezones to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				loadWarning("[DB] " + cur.toString());
			}
		}
		//###############################################################################
		
		//################################### LIMITER ###################################
		try {
			db.query("CREATE TABLE IF NOT EXISTS 'tr_limiter' ( "
					+ "'player' TEXT UNIQUE,"
					+ "'blockdata' TEXT);");
		} catch (Exception ex) {
			loadWarning("[DB] Unable to create limiter table!");
			for (StackTraceElement cur : ex.getStackTrace()){
				loadWarning("[DB] " + cur.toString());
			}
			dbworking += 4;
		}
		
		try {
			if(limvals != null){
				for(LinkedList<String> vals:limvals){
					String toadd = "";
					for(String str:vals) toadd+=","+str;
					if(toadd.startsWith(",")) toadd=toadd.substring(1, toadd.length());
					db.query("INSERT INTO 'tr_limiter' VALUES("+toadd+");");
				}
				
				log.info("[DB] Transferred "+ limvals.size() + " limits.");
			}
		} catch (Exception ex) {
			loadWarning("[DB] Unable to write limits to database!");
			for (StackTraceElement cur : ex.getStackTrace()){
				loadWarning("[DB] " + cur.toString());
			}
		}
		if (dbworking == 0) {
			log.info("[DB] Transfering into the new database format succeeded!");
		} else {
			dbFailMsg(dbworking);
			loadWarning("[DB] Transfering into the new database format failed!");
		}
		
	}
	
	private void transferDB12To13(){
		log.info("[DB] Updating Database to new format...");
		try {
			db.query("ALTER TABLE 'tr_limiter' RENAME TO 'tr_limiter_old'");
			db.query("CREATE TABLE 'tr_limiter' ("
					+ "'player' TEXT UNIQUE,"
					+ "'blockdata' TEXT);");
			db.query("INSERT INTO 'tr_limiter' (player, blockdata) SELECT player, blockdata FROM tr_limiter_old ORDER BY player ASC");
		} catch (SQLException ex) {
			loadWarning("[DB] Error while updating db!");
			for (StackTraceElement st : ex.getStackTrace()){
				loadWarning("[DB] " + st.toString());
			}
		}
	}
	
	private void dbFailMsg(int fail){
		if (fail == 1 || fail == 3 || fail == 5)
			loadWarning("[DB] The database will RESET upon next server startup because the version table couldn't be created!");
		if (fail == 2 || fail == 3 || fail == 6)
			loadWarning("[DB] Safezones will NOT work properly because the safezones table couldn't be created!");
		if (fail == 4 || fail == 5 || fail == 6)
			loadWarning("[DB] The limiter will NOT work properly because the limiter table couldn't be created!");
		else if (fail == 7) 
			loadWarning("[DB] All database actions failed! Safezones and the limiter will NOT be stored!");
	}
	
	private LinkedList<LinkedList<String>> getTableVals(String table) throws SQLException {
		ResultSet rs = db.query("SELECT * FROM `"+table+"`");
		LinkedList<LinkedList<String>> values = new LinkedList<LinkedList<String>>();
		if (rs == null) return values;
		while(rs.next()) {
			LinkedList<String> row = new LinkedList<String>();
			for (int i=1;i<=20;i++){
				try {
					row.add(rs.getString(i));
				} catch (Exception ex){
					break;
				}
			}
			values.add(row);
		}
		rs.close();
		return values;
	}
	
	public void reload(boolean listeners, boolean silent) {
		if (listeners){
			Assigner.unregisterAll();
		}
		this.reloadConfig();
		config = this.getConfigx();
		loadConfigCache();
		TRNoItem.clear(); //TRNI
		TRCacheItem.reload();
		try {
			TRNoItem.reload(); //TRNI2 FIXME errors
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		TRThread.reload(); // branches out
		TRListener.reload();
		TRLimitBlock.reload();
		TRLogger.reload();
		TRRecipeBlock.reload();
		TRLimitFlyThread.reload();
		TREMCSet.reload();
		if (linkEEPatch()){
			EEPSettings.loadAllDisabledActions();
			EEPSettings.loadMaxCharge();
			if (listeners)
				Assigner.assignEEPatch();
		}
		
		if (listeners)
			Assigner.assign();
		
		if (!silent) log.info("TekkitRestrict Reloaded!");
	}

	public static String antisqlinject(String ins) {
		ins = ins.replaceAll("--", "");
		ins = ins.replaceAll("`", "");
		ins = ins.replaceAll("'", "");
		ins = ins.replaceAll("\"", "");
		return ins;
	}

	private TRFileConfiguration getConfigx() {
		if (configList.size() == 0) {
			reloadConfig();
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
		configList.add(reloadc("Hack.config.yml"));
		configList.add(reloadc("LimitedCreative.config.yml"));
		configList.add(reloadc("Logging.config.yml"));
		configList.add(reloadc("TPerformance.config.yml"));
		configList.add(reloadc("MicroPermissions.config.yml"));
		configList.add(reloadc("SafeZones.config.yml"));
		if (linkEEPatch()) configList.add(reloadc("EEPatch.config.yml"));
	}

	private YamlConfiguration reloadc(String loc) {
		File cf = new File("plugins/tekkitrestrict/" + loc);
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
			saveResource("Hack.config.yml", false);
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
			saveResource("MicroPermissions.config.yml", false);
		} catch (Exception e) {}
		try {
			saveResource("SafeZones.config.yml", false);
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
			saveResource("Hack.config.yml", force);
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
			saveResource("MicroPermissions.config.yml", force);
		} catch (Exception e) {}
		try {
			saveResource("SafeZones.config.yml", force);
		} catch (Exception e) {}
		try {
			if (linkEEPatch()){
				saveResource("EEPatch.config.yml", force);
			}
		} catch (Exception e) {}
		log.setLevel(ll);
	}
	
	public void Update(){
		updater = new Updater(this, "tekkit-restrict", this.getFile(), Updater.UpdateType.DEFAULT, true);
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
			if (temp.length >= 3 && temp[2].matches("\\d+")){
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
	
	public static boolean isBeta(){
		return getExtraVersion().contains("b");
	}
	public static boolean isDev(){
		return getExtraVersion().contains("d");
	}
}
