package com.github.dreadslicer.tekkitrestrict;

//import net.milkbowl.vault.permission.Permission;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.dreadslicer.tekkitrestrict.commands.TRCommandAlc;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandTPIC;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandTR;
import com.github.dreadslicer.tekkitrestrict.lib.TRFileConfiguration;
import com.github.dreadslicer.tekkitrestrict.lib.YamlConfiguration;
//import com.sk89q.worldedit.bukkit.WorldEditPlugin;
//import com.sk89q.worldedit.bukkit.selections.Selection;

public class tekkitrestrict extends JavaPlugin {
	public static Logger log;
	public static TRFileConfiguration config;
	public static boolean EEEnabled = false, disable = false, rp = false;
	public static PluginManager pm;
	public static Object perm = null;
	public static TRSQLDB db;
	private static tekkitrestrict instance;
	public static ExecutorService basfo = Executors.newCachedThreadPool();
	
	private static TRThread ttt = null;
	public static List<YamlConfiguration> configList = new LinkedList<YamlConfiguration>();

	// pre-load the sqlite
	@Override
	public void onLoad() {
		instance = this;
		log = this.getLogger();

		loadSqlite();
		initSqlite();
		pm = this.getServer().getPluginManager();
		TRStackLoader.init();

		this.saveDefaultConfig();

		// load the configuration file
		config = this.getConfigx();

		// set minimum interval for logic timers...
		try {
			double g = tekkitrestrict.config.getDouble("RPTimerMin");
			double ticks = g * 20.0;
			net.minecraft.server.RedPowerLogic.minInterval = Integer.parseInt(String.valueOf(ticks));
		} catch (Exception e) {

		}

		// ///////////
		
		TRLogFilter.reload(); 
		Enumeration<String> cc =LogManager.getLogManager().getLoggerNames(); 
		while(cc.hasMoreElements()) {
			Logger.getLogger(cc.nextElement()).setFilter(new TRLogFilter()); 
		}
		
		// ///////////

		log.info("Log filter Placed!");
		log.info("SQLite loaded!");
	}

	@Override
	public void onEnable() {
		// getLogger().info("onEnabled has been invoked!");
		// log.info("[TekkitRestrict] Begin Enable");
		ttt = new TRThread();
		this.getServer().getPluginManager().registerEvents(new TRListener(), this); //IMPORTANT assigner
		new TRLogger();
		TRSafeZone.init();
		TRLimitFly.init();
		TRLimitBlock.init();
		TRNoDupe_BagCache.init();

		getCommand("tekkitrestrict").setExecutor(new TRCommandTR(this));
		getCommand("openalc").setExecutor(new TRCommandAlc());
		getCommand("tpic").setExecutor(new TRCommandTPIC(this));

		// determine if EE2 is enabled by using pluginmanager
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
			// Was not able to load permissionsEx
		}

		// Initiate noItem, Time-thread and our event listener
		try {
			reload(); // load em up!
			ttt.init();

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
			g.addPlotter(new Metrics.Plotter("Hack attempts") {
				@Override
				public int getValue() {
					try {
						return TRNoHack.hacks;
					} catch(Exception e){
						return 0;
					}
				}
			});
			g.addPlotter(new Metrics.Plotter("Recipe blocks") {
				@Override
				public int getValue() {
					try{
						int size = 0;
						List<String> ssr = tekkitrestrict.config.getStringList("RecipeBlock");
						for (int i = 0; i < ssr.size(); i++) {
							List<TRCacheItem> iss = TRCacheItem.processItemString(
									"", ssr.get(i), -1);
							size += iss.size();
						}
						ssr = tekkitrestrict.config
								.getStringList("RecipeFurnaceBlock");
						for (int i = 0; i < ssr.size(); i++) {
							List<TRCacheItem> iss = TRCacheItem.processItemString(
									"", ssr.get(i), -1);
							size += iss.size();
						}
						return size;
					}
					catch(Exception e){
						return 0;
					}
				}
			});
			g.addPlotter(new Metrics.Plotter("Dupe attempts") {
				@Override
				public int getValue() {
					try {
						return TRNoDupe.dupeAttempts;
					} catch(Exception ex){
						return 0;
					}
				}
			});
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
			// e.printStackTrace();
		}

		// done!
		log.info("TekkitRestrict v " + getDescription().getVersion()+ " Enabled!");
		
		/*
		 * log.info("T: "+config.get("UseChunkUnloader").toString());
		 * log.info("T1: "+config.get("FlyLimitDailyMinutes").toString());
		 * log.info("T2: "+config.get("RPTimerMin").toString());
		 */
		// TRThrottler.init();
	}

	@Override
	public void onDisable() {
		// turn off our uber awesome mod stuffs:
		try {
			TRLimitBlock.saveLimiters();
		} catch (Exception E) {
		}
		try {
			TRSafeZone.save();
		} catch (Exception E) {
		}

		try {
			TRThread.originalEUEnd();
		} catch (Exception E) {
		}

		disable = true;
		log.info("TekkitRestrict v " + getDescription().getVersion()+ " disabled!");
	}

	public static tekkitrestrict getInstance() {
		return instance;
	}

	/*
	 * public static boolean hasPermission(Player p, String perm,boolean list){
	 * if(list){ //this type of element is in a list... if(perm != null &&
	 * pm.isPluginEnabled("PermissionsEx")){ //return
	 * ((ru.tehkode.permissions.PermissionManager
	 * )tekkitrestrict.perm).getUser(p).getAllPermissions(); String[] perms =
	 * getAllPlayerPerms(p,perm); }else if(pm.isPluginEnabled("Vault")){
	 * RegisteredServiceProvider<net.milkbowl.vault.permission.Permission>
	 * permissionProvider = tekkitrestrict.getInstance().getServer().
	 * getServicesManager
	 * ().getRegistration(net.milkbowl.vault.permission.Permission.class);
	 * //permission. }else{/* //use superperms Set<PermissionAttachmentInfo>
	 * pail = p.getEffectivePermissions(); Iterator<PermissionAttachmentInfo> cc
	 * = pail.iterator(); LinkedList<String> listr = new LinkedList<String>();
	 * while(cc.hasNext()){ PermissionAttachmentInfo cr = cc.next();
	 * listr.add(cr.getPermission());
	 * //tekkitrestrict.log.info("playerperms+ "+cr.getPermission()); } return
	 * false; } } else{ return hasPermission(p,perm); } }
	 */

	private static void initHeartBeat() {
		instance.getServer().getScheduler()
				.scheduleAsyncRepeatingTask(instance, new Runnable() {
					@Override
					public void run() {
						TRLimitBlock.expireLimiters();
					}
				}, 60L, 32L);
	}

	private void loadSqlite() {
		db = new TRSQLDB(null, "tr", "Data", this.getDataFolder().toString());
		db.open();
	}

	private void initSqlite() {
		//determine if Data.db is older.
		Double ver = new Double(this.getDescription().getVersion());
		ResultSet prev = null;
		List<List<String>> srvals = null,limvals=null;
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
				for(List<String> sr:srvals){
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
				for(List<String> sr:limvals){
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

	private List<List<String>> getTableVals(String table) throws SQLException{
		/*ResultSet rs1 = db.query("SELECT COUNT(*) FROM `"+table+"`");
		int c = 0;
		if(rs1 != null){
			if(rs1.next()){
				c=rs1.getInt(1);
			}
		}
		rs1.close();*/
		ResultSet rs = db.query("SELECT * FROM `"+table+"`");
		List<List<String>> ls = new LinkedList<List<String>>();
		if (rs == null) return ls;
		while(rs.next()) {
			List<String> j = new LinkedList<String>();
			
			for(int i=1;i<=100;i++){
				//tekkitrestrict.log.info("++ "+rs.getNString(i));
				try{j.add(rs.getString(i));}catch(Exception e){}
			}
			//tekkitrestrict.log.info("t: "+j.size());
			ls.add(j);
		}
		rs.close();
		return ls;
	}
	
	public void reload() {
		// this.reloadConfig();
		this.reloadConfig();		
		config = this.getConfigx();
		TRNoItem.clear(); //TRNI
		TRCacheItem.reload();
		TRNoItem.reload(); //TRNI2
		TRChunkUnloader.reload();
		TRLogFilter.reload();
		TRThread.reload(); // branches out
		TRListener.reload();
		TRLimitBlock.reload();
		TRLogger.reload();
		TRPerformance.reload();
		TRRecipeBlock.reload();
		TRNoHack.reload();
		TRLimitFly.reload();
		TRNoDupe.reload(); // branches out
		TRLWCProtect.reload();
		TRSafeZone.reload();
		TRLimitedCreative.reload();
		TREMCSet.reload();
		log.info("TekkitRestrict Reloaded!");
	}

	public static String antisqlinject(String ins) {
		ins = ins.replaceAll("--", "");
		ins = ins.replaceAll("`", "");
		ins = ins.replaceAll("'", "");
		ins = ins.replaceAll("\"", "");
		return ins;
	}

	public TRFileConfiguration getConfigx() {
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
		} catch (Exception e) {
		}
		try {
			saveResource("Advanced.config.yml", false);
		} catch (Exception e) {
		}
		try {
			saveResource("ModModifications.config.yml", false);
		} catch (Exception e) {
		}
		try {
			saveResource("DisableClick.config.yml", false);
		} catch (Exception e) {
		}
		try {
			saveResource("DisableItems.config.yml", false);
		} catch (Exception e) {
		}
		try {
			saveResource("Hack.config.yml", false);
		} catch (Exception e) {
		}
		try {
			saveResource("LimitedCreative.config.yml", false);
		} catch (Exception e) {
		}
		try {
			saveResource("Logging.config.yml", false);
		} catch (Exception e) {
		}
		try {
			saveResource("TPerformance.config.yml", false);
		} catch (Exception e) {
		}
		try {
			saveResource("MicroPermissions.config.yml", false);
		} catch (Exception e) {
		}
		log.setLevel(ll);
	}
}
