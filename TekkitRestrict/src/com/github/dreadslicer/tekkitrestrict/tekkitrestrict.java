package com.github.dreadslicer.tekkitrestrict;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.github.dreadslicer.tekkitrestrict.lib.TRFileConfiguration;
import com.github.dreadslicer.tekkitrestrict.lib.YamlConfiguration;
import com.github.dreadslicer.tekkitrestrict.listeners.Assigner;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.SSMode;

public class tekkitrestrict extends JavaPlugin {
	public static Logger log;
	public static TRFileConfiguration config;
	public static boolean EEEnabled = false;
	
	/** Indicates if tekkitrestrict is disabling. Threads use this to check if they should stop. */
	public static boolean disable = false;
	public static String version;
	public static Object perm = null;
	public static SQLite db;
	public static Updater updater = null;
	private static tekkitrestrict instance;
	public static ExecutorService basfo = Executors.newCachedThreadPool();
	
	private static TRThread ttt = null;
	public static LinkedList<YamlConfiguration> configList = new LinkedList<YamlConfiguration>();

	@Override
	public void onLoad() {
		instance = this;
		log = this.getLogger();
		
		loadSqlite(); //pre-load the sqlite
		initSqlite();

		this.saveDefaultConfig();

		config = this.getConfigx(); //Load the configuration files
		if (config.getDouble("ConfigVersion", 0.9) < 1.1){
			log.warning("The config file version differs from the current one.");
			
			log.warning("Backing up old config files and writing new ones.");
			
			String path = "plugins"+File.separator+"tekkitrestrict"+File.separator;
			String bpath = path+"config_backup";
			File temp = new File(bpath);
			temp.mkdirs();
			bpath += File.separator;
			
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

			saveDefaultConfig(true);
			reloadConfig();
		}
		
		loadConfigCache();
		
		try {
			int ticks = (int) Math.round(config.getDouble("RPTimerMin", 0.2) * 20);
			RedPowerLogic.minInterval = ticks; // set minimum interval for logic timers...
		} catch (Exception e) {
			log.warning("Setting the RedPower Timer failed.");
		}

		// ///////////
		
		
		if (config.getBoolean("UseLogFilter", true)){
			Enumeration<String> cc = LogManager.getLogManager().getLoggerNames();
			TRLogFilter filter = new TRLogFilter();
			while(cc.hasMoreElements()) {
				Logger.getLogger(cc.nextElement()).setFilter(filter); 
			}
			log.info("Log filter Placed!");
		}
		
		// ///////////

		Log.init();
		log.info("SQLite loaded!");
	}
	@Override
	public void onEnable() {
		
		ttt = new TRThread();
		Assigner.assign(this); //Register the required listeners
		
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
			reload(true);
			ttt.init(); //Start up all threads

			initHeartBeat();
		} catch (Exception e) {
			//e.printStackTrace();
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
						return TRNoItem.getTotalLen();
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
		
		log.info("TekkitRestrict v " + version + " Enabled!");
		
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
		ttt.bagCacheThread.interrupt();
		ttt.limitFlyThread.interrupt();
		
		try { Thread.sleep(1500); } catch (InterruptedException e) {} //Sleep for 1.5 seconds to allow the savethread to save.
		//try {
		//	TRThread.originalEUEnd(); (Currently does nothing)
		//} catch (Exception ex) {
		//}

		TRLogFilter.disable();
		Log.deinit();
		FileLog.closeAll();
		
		log.info("TekkitRestrict v " + version + " disabled!");
	}

	public static tekkitrestrict getInstance() {
		return instance;
	}
	
	public boolean linkEEPatch(){
		try {
			Class.forName("ee.events.EEEvent");
			return true;
		} catch (ClassNotFoundException e) {
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
		
		ChunkUnloader.enabled = config.getBoolean("UseChunkUnloader", false);
		ChunkUnloader.maxChunks = config.getInt("MaxChunks", 3000);
		ChunkUnloader.maxRadii = config.getInt("MaxRadii", 256);
	}

	private static void initHeartBeat() {
		instance.getServer().getScheduler().scheduleAsyncRepeatingTask(instance, new Runnable() {
			@Override
			public void run() {
				TRLimitBlock.expireLimiters();
			}
		}, 60L, 32L);
	}

	private void loadSqlite() {
		db = new SQLite("Data", this.getDataFolder().getPath());
		db.open();
	}
	private void initSqlite() {
		//determine if Data.db is older.
		Double ver = new Double(this.getDescription().getVersion());
		ResultSet prev = null;
		LinkedList<LinkedList<String>> srvals = null, limvals=null;
		try{
			//Version select
			ResultSet rs = db.query("SELECT version FROM tr_dbversion");
			prev = rs;
			if(rs.next()){
				Double verX = rs.getDouble("version");
				if(verX == 1.10 || verX == 1.00){
					//nothing changed...
				}
			}
			rs.close();
		}
		catch(Exception e){
			if(prev != null)
				try {prev.close();} catch (SQLException e1) {}
			//PRE-1.00 version, remove/purge and replace.
			tekkitrestrict.log.info("Transfering last Data.db info to new Database prototype");
			//remove all relevant information from both databases.
			//tr_saferegion =	id name mode data world
			//tr_limiter = 		id player blockdata
			try {
				srvals = this.getTableVals("tr_saferegion");
			} catch(SQLException ex){
				log.warning("Minor exception occured when trying to load the safezones from the database.");
			}
			try {
				limvals = this.getTableVals("tr_limiter");
			} catch(SQLException ex){
				log.warning("Minor exception occured when trying to load the limiter from the database.");
			}
			
			if (srvals != null && limvals != null)
				tekkitrestrict.log.info("DB - Copied "+(srvals.size() + limvals.size())+" rows");
			
			try{db.query("DROP TABLE `tr_saferegion`;");} catch(Exception ex){}
			try{db.query("DROP TABLE `tr_limiter`;");} catch(Exception ex){}
			
			
			try {
				db.query("CREATE TABLE IF NOT EXISTS 'tr_dbversion' (version NUMERIC);");
				db.query("INSERT INTO 'tr_dbversion' (version) VALUES("+ver+");");
			} catch (Exception E) {
			}
		}
		
		
		try {
			db.query("CREATE TABLE IF NOT EXISTS 'tr_saferegion' ( "
					+ "'id' INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "'name' TEXT," + "'mode' INT," + "'data' TEXT,"
					+ "'world' TEXT); ");
			if(srvals != null){
				for(LinkedList<String> sr:srvals){
					String tadd = "";
					for(String l:sr) tadd+=","+l;
					//tadd = tadd.replace("null", "''");
					if(tadd.startsWith(",")) tadd=tadd.substring(1, tadd.length());
					//tekkitrestrict.log.info("INSERT INTO 'tr_saferegion' VALUES("+tadd+");");
					db.query("INSERT INTO 'tr_saferegion' VALUES("+tadd+");");
				}
			}
		} catch (Exception E) {
		}
		try {
			db.query("CREATE TABLE IF NOT EXISTS 'tr_limiter' ( "
					+ "'id' INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "'player' TEXT," + "'blockdata' TEXT);");
			if(limvals != null){
				for(LinkedList<String> sr:limvals){
					String tadd = "";
					for(String l:sr) tadd+=","+l;
					//tadd = tadd.replace("null", "''");
					if(tadd.startsWith(",")) tadd=tadd.substring(1, tadd.length());
					//tekkitrestrict.log.info("INSERT INTO 'tr_saferegion' VALUES("+tadd+");");
					db.query("INSERT INTO 'tr_limiter' VALUES("+tadd+");");
				}
			}
		} catch (Exception E) {
		}
	}

	private LinkedList<LinkedList<String>> getTableVals(String table) throws SQLException {
		ResultSet rs = db.query("SELECT * FROM `"+table+"`");
		LinkedList<LinkedList<String>> values = new LinkedList<LinkedList<String>>();
		if (rs == null) return values;
		while(rs.next()) {
			LinkedList<String> j = new LinkedList<String>();
			for(int i=1;i<=1000;i++){
				try {
					j.add(rs.getString(i));
				} catch (Exception e){
					break;
				}
			}
			values.add(j);
		}
		rs.close();
		return values;
	}
	
	public void reload(boolean silent) {
		this.reloadConfig();		
		config = this.getConfigx();
		loadConfigCache();
		TRNoItem.clear(); //TRNI
		TRCacheItem.reload();
		TRNoItem.reload(); //TRNI2
		TRThread.reload(); // branches out
		TRListener.reload();
		TRLimitBlock.reload();
		TRLogger.reload();
		TRRecipeBlock.reload();
		TRLimitFlyThread.reload();
		TREMCSet.reload();
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
				tekkitrestrict.log.warning("Exception while trying to reload the config!");
				e.printStackTrace();
			}
		}
		return conf;
	}

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
		log.setLevel(ll);
	}
	
	public void Update(){
		updater = new Updater(this, "tekkit-restrict", this.getFile(), Updater.UpdateType.DEFAULT, true);
	}
	
	private boolean backupConfig(String sourceString, String destString){
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
			log.severe("Cannot backup config: " + sourceString);
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
