/* IMPORTANT TODO'S:
 * - Add check to disable Anti pedestal EMC gen if EEPatch of the right version is installed (it fixes it)
 * 
 */
package nl.taico.tekkitrestrict;

import static nl.taico.tekkitrestrict.config.SettingsStorage.generalConfig;
import static nl.taico.tekkitrestrict.config.SettingsStorage.loggingConfig;
import static nl.taico.tekkitrestrict.config.SettingsStorage.modModificationsConfig;
import static nl.taico.tekkitrestrict.config.SettingsStorage.reloadConfigs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import lombok.NonNull;
import net.minecraft.server.RedPowerLogic;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.commands.TRCmdCheck;
import nl.taico.tekkitrestrict.commands.TRCmdClearAlc;
import nl.taico.tekkitrestrict.commands.TRCmdOpenAlc;
import nl.taico.tekkitrestrict.commands.TRCmdOpenInv;
import nl.taico.tekkitrestrict.commands.TRCmdTpic;
import nl.taico.tekkitrestrict.commands.TRCmdTr;
import nl.taico.tekkitrestrict.config.SettingsStorage;
import nl.taico.tekkitrestrict.database.Database;
import nl.taico.tekkitrestrict.eepatch.EEPSettings;
import nl.taico.tekkitrestrict.functions.TRChunkUnloadCommandLogic;
import nl.taico.tekkitrestrict.functions.TREMCSet;
import nl.taico.tekkitrestrict.functions.TRLWCProtect;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRNoClick;
import nl.taico.tekkitrestrict.functions.TRNoInteract;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.functions.TRRecipeBlock;
import nl.taico.tekkitrestrict.functions.TRSafeZone;
import nl.taico.tekkitrestrict.listeners.Assigner;
import nl.taico.tekkitrestrict.logging.TRLogEnhancer;
import nl.taico.tekkitrestrict.logging.TRLogFilterPlus;
import nl.taico.tekkitrestrict.logging.TRLogSplitterPlus;
import nl.taico.tekkitrestrict.objects.TREnums.DBType;
import nl.taico.tekkitrestrict.objects.TRVersion;
import nl.taico.tekkitrestrict.threads.TRThreadManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TekkitRestrict extends JavaPlugin {
	public static TekkitRestrict instance;
	public static boolean EEEnabled = false;

	/** Indicates if tekkitrestrict is disabling. Threads use this to check if they should stop. */
	public static boolean disable = false;
	public static TRVersion version;
	public static double dbversion = 1.21;
	public static int dbworking = 0;

	public static DBType dbtype = DBType.Unknown;
	public static Database db;
	public static Updater updater = null;

	public static boolean useTMetrics = true;
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

	public static TekkitRestrict getInstance() {
		return instance;
	}

	public static TRVersion getVersion(){
		return version;
	}

	public TMetrics tmetrics;

	public Boolean addEEItemsToEssentials(){
		try {
			if (!Bukkit.getPluginManager().isPluginEnabled("Essentials")) return null;
			Plugin p = Bukkit.getPluginManager().getPlugin("Essentials");

			Map<String, Integer> items = null;
			Map<String, Short> durabilities = null;
			try {
				Class<?> clazz = p.getClass();
				if ((clazz == Plugin.class) || (clazz == JavaPlugin.class)) clazz = Class.forName("com.earth2me.essentials.Essentials");
				//Class<?> idb = Class.forName("com.earth2me.essentials.ItemDB");
				Object db = clazz.getMethod("getItemDb").invoke(Bukkit.getPluginManager().getPlugin("Essentials"));
				if (db == null) return false;

				Class<?> idb = db.getClass();
				//idb = Class.forName("com.earth2me.essentials.ItemDB");

				final Field field1 = idb.getDeclaredField("items");
				field1.setAccessible(true);
				items = (Map<String, Integer>) field1.get(db);
				final Field field2 = idb.getDeclaredField("durabilities");
				field2.setAccessible(true);
				durabilities = (Map<String, Short>) field2.get(db);
			} catch (Exception ex) {
				Log.debugEx(ex);
				return false;
			}

			if ((items == null) || (durabilities == null)) return false;

			final ArrayList<String> it = new ArrayList<String>();

			it.add("energycollectormk1,126,0");
			it.add("energycollector,126,0");
			it.add("collectormk1,126,0");
			it.add("collector,126,0");
			it.add("collectormk2,126,1");
			it.add("energycollectormk2,126,1");
			it.add("collectormk3,126,2");
			it.add("energycollectormk3,126,2");
			it.add("darkmatterfurnace,126,3");
			it.add("dmfurnace,126,3");
			it.add("redmatterfurnace,126,4");
			it.add("rmfurnace,126,4");
			it.add("antimatterrelay,126,5");
			it.add("antimatterrelaymk1,126,5");
			it.add("relaymk1,126,5");
			it.add("relay,126,5");
			it.add("antimatterrelaymk2,126,6");
			it.add("relaymk2,126,6");
			it.add("antimatterrelaymk3,126,7");
			it.add("relaymk3,126,7");
			it.add("darkmatterblock,126,8");
			it.add("dmblock,126,8");
			it.add("redmatterblock,126,9");
			it.add("rmblock,126,9");
			it.add("darkmatterpedestal,127,0");
			it.add("dmpedestal,127,0");
			it.add("alchemicalchest,128,0");
			it.add("energycondenser,128,1");
			it.add("condenser,128,1");
			it.add("interdictiontorch,129,0");
			it.add("transmutiontablet,130,0");
			it.add("transmutetablet,130,0");
			it.add("transtablet,130,0");
			it.add("transmutiontable,130,0");
			it.add("transmutetable,130,0");

			it.add("philosopherstone,27526,0");
			it.add("philosopher,27526,0");
			it.add("philostone,27526,0");
			it.add("philo,27526,0");
			it.add("destructioncatalyst,27527,0");
			it.add("destcatalyst,27527,0");
			it.add("ironband,27528,0");
			it.add("soulstone,27529,0");
			it.add("evertideamulet,27530,0");
			it.add("evertide,27530,0");
			it.add("volcaniteamulet,27531,0");
			it.add("volcanite,27531,0");
			it.add("blackholeband,27532,0");
			it.add("bhb,27532,0");
			it.add("ringofignition,27533,0");
			it.add("ringignition,27533,0");
			it.add("ignitionring,27533,0");
			it.add("ignitering,27533,0");
			it.add("archangelssmite,27534,0");
			it.add("archangelsmite,27534,0");
			it.add("archangels,27534,0");
			it.add("archangel,27534,0");
			it.add("hyperkineticlens,27535,0");
			it.add("hyperkinetic,27535,0");
			it.add("swiftwolfsrendinggale,27536,0");
			it.add("swiftwolfrendinggale,27536,0");
			it.add("swiftwolf,27536,0");
			it.add("flyring,27536,0");
			it.add("harvestring,27537,0");
			it.add("watchofflowingtime,27538,0");
			it.add("alchemicalcoal,27539,0");
			it.add("mobiusfuel,27540,0");
			it.add("darkmatter,27541,0");
			it.add("dm,27541,0");
			it.add("covalencedustlow,27542,0");
			it.add("covalencelow,27542,0");
			it.add("covalencedustmedium,27542,1");
			it.add("covalencemedium,27542,1");
			it.add("covalencedusthigh,27542,2");
			it.add("covalencehigh,27542,2");
			it.add("darkmatterpickaxe,27543,0");
			it.add("darkmatterpick,27543,0");
			it.add("dmpickaxe,27543,0");
			it.add("dmpick,27543,0");
			it.add("darkmatterspade,27544,0");
			it.add("darkmattershovel,27544,0");
			it.add("dmspade,27544,0");
			it.add("dmshovel,27544,0");
			it.add("darkmatterhoe,27545,0");
			it.add("dmhoe,27545,0");
			it.add("darkmattersword,27546,0");
			it.add("dmsword,27546,0");
			it.add("darkmatteraxe,27547,0");
			it.add("dmaxe,27547,0");
			it.add("darkmattershears,27548,0");
			it.add("dmshears,27548,0");
			it.add("darkmatterarmor,27549,0");
			it.add("darkmatterchestplate,27549,0");
			it.add("dmarmor,27549,0");
			it.add("dmchestplate,27549,0");
			it.add("darkmatterhelmet,27550,0");
			it.add("dmhelmet,27550,0");
			it.add("darkmattergreaves,27551,0");
			it.add("darkmatterleggings,27551,0");
			it.add("dmgreaves,27551,0");
			it.add("dmleggings,27551,0");
			it.add("darkmatterboots,27552,0");
			it.add("dmboots,27552,0");
			it.add("gemofeternaldensity,27553,0");
			it.add("gemeternaldensity,27553,0");
			it.add("repairtalisman,27554,0");
			it.add("talismanofrepair,27554,0");
			it.add("talismanrepair,27554,0");
			it.add("darkmatterhammer,27555,0");
			it.add("dmhammer,27555,0");
			it.add("cataclycticlens,27556,0");
			it.add("klienstarein,27557,0");
			it.add("klienstar1,27557,0");
			it.add("ksein,27557,0");
			it.add("ks1,27557,0");
			it.add("klienstarzwei,27558,0");
			it.add("klienstar2,27558,0");
			it.add("kszwei,27558,0");
			it.add("ks2,27558,0");
			it.add("klienstardrei,27559,0");
			it.add("klienstar3,27559,0");
			it.add("ksdrei,27559,0");
			it.add("ks3,27559,0");
			it.add("klienstarvier,27560,0");
			it.add("klienstar4,27560,0");
			it.add("ksvier,27560,0");
			it.add("ks4,27560,0");
			it.add("klienstarsphere,27561,0");
			it.add("klienstar5,27561,0");
			it.add("kssphere,27561,0");
			it.add("ks5,27561,0");
			it.add("klienstaromega,27591,0");
			it.add("klienstar6,27591,0");
			it.add("ksomega,27591,0");
			it.add("ks6,27591,0");
			it.add("alchemybag,27562,0");
			it.add("alcbag,27562,0");
			it.add("redmatter,27563,0");
			it.add("rm,27563,0");
			it.add("redmatterpickaxe,27564,0");
			it.add("redmatterpick,27564,0");
			it.add("rmpickaxe,27564,0");
			it.add("rmpick,27564,0");
			it.add("redmatterspade,27565,0");
			it.add("redmattershovel,27565,0");
			it.add("rmspade,27565,0");
			it.add("rmshovel,27565,0");
			it.add("redmatterhoe,27566,0");
			it.add("rmhoe,27566,0");
			it.add("redmattersword,27567,0");
			it.add("rmsword,27567,0");
			it.add("redmatteraxe,27568,0");
			it.add("rmaxe,27568,0");
			it.add("redmattershears,27569,0");
			it.add("rmshears,27569,0");
			it.add("redmatterhammer,27570,0");
			it.add("rmhammer,27570,0");
			it.add("aeternalisfuel,27571,0");
			it.add("aeternalis,27571,0");
			it.add("redmatterkatar,27572,0");
			it.add("rmkatar,27572,0");
			it.add("redkatar,27572,0");
			it.add("redmattermorningstar,27573,0");
			it.add("rmmorningstar,27573,0");
			it.add("redmorningstar,27573,0");
			it.add("zeroring,27574,0");
			it.add("redmatterarmor,27575,0");
			it.add("redmatterchestplate,27575,0");
			it.add("rmarmor,27575,0");
			it.add("rmchestplate,27575,0");
			it.add("redmatterhelmet,27576,0");
			it.add("rmhelmet,27576,0");
			it.add("redmattergreaves,27577,0");
			it.add("redmatterleggings,27577,0");
			it.add("rmgreaves,27577,0");
			it.add("rmleggings,27577,0");
			it.add("redmatterboots,27578,0");
			it.add("rmboots,27578,0");
			it.add("infernalarmor,27579,0");
			it.add("gemarmor,27579,0");
			it.add("gemchestplate,27579,0");
			it.add("abysshelmet,27580,0");
			it.add("gemhelmet,27580,0");
			it.add("gravitygreaves,27581,0");
			it.add("gemgreaves,27581,0");
			it.add("gemleggins,27581,0");
			it.add("hurricaneboots,27582,0");
			it.add("gemboots,27582,0");
			it.add("mercurialeye,27583,0");
			it.add("ringofarcana,27584,0");
			it.add("ringarcana,27584,0");
			it.add("arcanaring,27584,0");
			it.add("diviningrod,27585,0");
			it.add("bodystone,27588,0");
			it.add("lifestone,27589,0");
			it.add("mindstone,27590,0");
			it.add("transmutationtablet,27592,0");
			it.add("transtablet,27592,0");
			it.add("voidring,27593,0");
			it.add("alchemytome,27594,0");
			for (final String line : it) {
				try {
					final String[] parts = line.trim().toLowerCase(Locale.ENGLISH).split("[^a-z0-9]");
					if (parts.length < 2) continue;

					final int numeric = Integer.parseInt(parts[1]);

					durabilities.put(parts[0], Short.valueOf((parts.length > 2) && !parts[2].equals("0") ? Short.parseShort(parts[2]) : 0));
					items.put(parts[0], numeric);
				} catch (Exception ex){}
			}
			return true;
		} catch (Exception ex){
			Log.debugEx(ex);
			return false;
		}
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
		if (PatchesAPI.hasFix(PatchesAPI.getEEPatchVer())){
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
	private final void linkPex(){
		if (Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")){
			Log.trace("Linking with PEX...");
			try {
				TRPermHandler.permEx = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
				Log.info("PEX is enabled!");
			} catch (Exception ex) {
				Warning.load("Linking with Pex Failed!", false);
				Log.debugEx(ex);
			}
		}
	}

	public void load(){
		Log.trace("Loading All TR Components...");

		Log.trace("Loading Config Cache...");
		TRConfigCache.loadConfigCache();
		//################################################## Load New Logging ###################################################

		schedule(true, new Runnable(){
			@Override
			public void run(){

				if (loggingConfig.getBoolean("FilterLogs", true)){
					Log.trace("Loading Log Filters...");
					try {
						TRLogFilterPlus.loadFilters(loggingConfig.getSection("Filters"));
						TRLogFilterPlus.assignFilters();
					} catch (Exception ex){
						Log.Warning.load("Error while loading Log Filters!", false);
						Log.debugEx(ex);
					}
				}
				if (loggingConfig.getBoolean("SplitLogs", true)){
					Log.trace("Loading Log Splitters...");
					try {
						TRLogSplitterPlus.loadSplitters(loggingConfig.getSection("Splitters"), loggingConfig.getSection("CommandSplitters"));
						TRLogSplitterPlus.assignSplitter();
					} catch (Exception ex){
						Log.Warning.load("Error while loading Log Splitter!", false);
						Log.debugEx(ex);
					}
				}
				if (loggingConfig.getBoolean("UseLogEnchancer", true)){
					Log.trace("Loading Log Enhancer...");
					try {
						TRLogEnhancer.reload();
						TRLogEnhancer.assignFilters();
					} catch (Exception ex){
						Log.Warning.load("Error while loading Log Enhancer!", false);
						Log.debugEx(ex);
					}
				}
			}
		}, 1l);

		//#######################################################################################################################

		Log.trace("Loading Item Processor...");
		try {
			TRItemProcessor2.load();
		} catch (Exception ex){
			Log.Warning.load("Error while loading Permission Groups!", false);
			Log.debugEx(ex);
		}
		Log.trace("Loading NoItem...");
		try {
			TRNoItem.reload(); //Banned items and limited creative.
		} catch (Exception ex){
			Log.Warning.load("Error while loading NoItem!", false);
			Log.debugEx(ex);
		}
		Log.trace("Loading Banned Interacts...");
		try {
			TRNoInteract.reload();
		} catch (Exception ex){
			Log.Warning.load("Error while loading Banned Interacts!", false);
			Log.debugEx(ex);
		}
		Log.trace("Loading NoClick...");
		try {
			TRNoClick.load();
		} catch (Exception ex){
			Log.Warning.load("Error while loading Banned Inventory Clicks!", false);
			Log.debugEx(ex);
		}
		Log.trace("Loading Limiter...");
		try {
			TRLimiter.reload();
		} catch (Exception ex){
			Log.Warning.load("Error while loading Limiter!", false);
			Log.debugEx(ex);
		}
		Log.trace("Loading RecipeBlock...");
		try {
			TRRecipeBlock.reload();
		} catch (Exception ex){
			Log.Warning.load("Error while loading RecipeBlock!", false);
			Log.debugEx(ex);
		}
		Log.trace("Loading EMC Setter...");
		try {
			TREMCSet.reload();
		} catch (Exception ex){
			Log.Warning.load("Error while loading EMC Setter!", false);
			Log.debugEx(ex);
		}
		Log.trace("Loading LWC Protect...");
		try {
			TRLWCProtect.reload();
		} catch (Exception ex){
			Log.Warning.load("Error while loading LWC Protect!", false);
			Log.debugEx(ex);
		}
		try {
			if (PatchesAPI.hasFix(PatchesAPI.getEEPatchVer())){
				Log.trace("Loading EEPatch Disabled Actions...");
				EEPSettings.loadAllDisabledActions();
				EEPSettings.loadMaxCharge();
			}
		} catch (Exception ex){
			Log.Warning.load("Error while loading EEPatch Disabled Actions!", false);
			Log.debugEx(ex);
		}
	}

	@Override
	public void onDisable() {
		disable = true;
		tmetrics.stop();
		//metrics.stop();
		TRThreadManager.stop();

		TRLogSplitterPlus.disable();
		TRLogFilterPlus.disable();
		TRLogEnhancer.disable();
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

		final PluginManager pm = getServer().getPluginManager();

		TekkitRestrict.EEEnabled = pm.isPluginEnabled("mod_EE"); // determine if EE2 is enabled

		//################################################# Register Listeners ##################################################
		Assigner.assign();
		//#######################################################################################################################

		linkEEPatch();

		if (schedule(false, new Runnable(){
			@Override
			public void run(){
				TRSafeZone.init(pm.getPlugin("WorldGuard"), pm.getPlugin("GriefPrevention"), pm.getPlugin("PreciousStones"));
				TRLimiter.init();
			}
		}) == -1){
			Log.debug("Unable to schedule Limiter and SafeZones. Using non-scheduled methods.");
			TRSafeZone.init(pm.getPlugin("WorldGuard"), pm.getPlugin("GriefPrevention"), pm.getPlugin("PreciousStones"));
			TRLimiter.init();
		}

		//################################################## Register Commands ##################################################
		Log.trace("Registering commands...");
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

		Boolean ess = addEEItemsToEssentials();
		if (ess == null){}
		else if (ess.booleanValue()) Log.info("Added EE Items to Essentials");
		else						 Warning.load("Unable to add EE Items to Essentials", false);

		for (World world : Bukkit.getWorlds()){
			new TRChunkUnloadCommandLogic(world);
		}

		//#################################################### Check Update #####################################################
		schedule(false, new Runnable() {
			@Override
			public void run(){
				if (generalConfig.getBoolean("Auto-Update", true)){
					updater = new Updater(TekkitRestrict.this, 44061, TekkitRestrict.this.getFile(), Updater.UpdateType.DEFAULT, true);
				} else if (generalConfig.getBoolean("CheckForUpdateOnStartup", true)){
					updater = new Updater(TekkitRestrict.this, 44061, TekkitRestrict.this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
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
			@Override
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
	public void onLoad() {
		instance = this; //Set the instance

		version = new TRVersion(getDescription().getVersion());
		Log.init();

		upgradeTo20();

		Log.fine("Loading Configs...");
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

	/**
	 * @param listeners Reload Listeners as well?
	 * @param silent If silent is true, no notice of the reload will appear in the console.
	 */
	public void reload(final boolean listeners, final boolean silent) {
		if (listeners) Assigner.unregisterAll();

		SettingsStorage.reloadConfigs();

		load();
		TRThreadManager.reload();

		//Stop TMetrics if the user disabled it in the config and reloaded.
		if (!generalConfig.getBoolean("UseTMetrics", true)){
			tmetrics.stop();
		}

		if (listeners){
			Assigner.assign();
			if (PatchesAPI.hasFix(PatchesAPI.getEEPatchVer())) Assigner.assignEEPatch();
		}

		if (!silent) Log.info("TekkitRestrict Reloaded!");

		/*
		final int id = Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable(){
			public void run(){
				SettingsStorage.reloadConfigs();

				load();
				TRThreadManager.reload();

				//Stop TMetrics if the user disabled it in the config and reloaded.
				if (!generalConfig2.getBoolean("UseTMetrics", true)){
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
		 */
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
		try {
			saveResource("General.yml", force);
			saveResource("Advanced.yml", force);
			saveResource("ModModifications.yml", force);

			File disinteract = new File(getDataFolder(), "DisableInteract.yml");
			File disclick = new File(getDataFolder(), "DisableClick.yml");
			if (!disclick.exists() && !disinteract.exists()) saveResource("DisableInteract.yml", force);

			saveResource("Banned.yml", force);
			saveResource("HackDupe.yml", force);
			saveResource("LimitedCreative.yml", force);
			saveResource("Logging.yml", force);
			saveResource("Performance.yml", force);
			saveResource("GroupPermissions.yml", force);
			saveResource("SafeZones.yml", force);
			saveResource("Database.yml", force);

			if (PatchesAPI.hasFix(PatchesAPI.getEEPatchVer())) saveResource("EEPatch.yml", force);
		} catch (Exception e) {}
	}

	@Override
	public void saveResource(String resourcePath, boolean replace){
		if ((resourcePath == null) || resourcePath.equals("")) {
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

	private final boolean scheduleLimiterTasks(){
		try {
			int j = schedule(true, new Runnable(){
				boolean err = false;
				@Override
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
				@Override
				public void run() {
					TRLimiter.expireLimiters();
				}
			}, 60L, 32L);
			return (j != -1) && (i != -1);
		} catch (Exception ex){
			Log.Exception(ex, false);
			return false;
		}
	}

	public void Update(){
		updater = new Updater(this, 44061, getFile(), Updater.UpdateType.DEFAULT, true);
	}

	private void upgradeTo20() {
		try {
			if (!getDataFolder().exists()){
				getDataFolder().mkdirs();
				File f = new File(getDataFolder(), "upgraded");
				if (!f.exists()){
					try {
						f.createNewFile();
					} catch (Exception ex){
						Log.warning("Unable to write required config files!");
					}
				}
				Log.trace("Detected Fresh Install. Skipping TekkitRestrict 2.0 Upgrade Text");
				return;
			}

			File f = new File(getDataFolder(), "upgraded");
			if (!f.exists()){
				boolean beta = false;
				File general = new File(getDataFolder(), "General.yml");
				if (general.exists()){
					double confVer = 0d;
					try (BufferedReader br = new BufferedReader(new FileReader(general))){
						String line;
						while ((line = br.readLine()) != null){
							if (line.startsWith("ConfigVersion: ")){
								try {
									confVer = Double.parseDouble(line.substring(15, 18));
								} catch (NumberFormatException ex){
									confVer = 1d;
								}
								break;
							}
						}
					} catch (Exception ex){

					}

					if (confVer >= 3.09d){
						Log.trace("Detected ConfigVersion 3.1 or above. Skipping TekkitRestrict 2.0 Upgrade Text");
						try {
							f.createNewFile();
						} catch (Exception ex){

						}
						return;
					} else {
						Log.trace("Detected TekkitRestrict 2.0 Beta Config File(s).");
						beta = true;
					}
				}

				Log.warning("It seems you have just upgraded to TekkitRestrict 2.0.");
				Log.warning("TekkitRestrict 2.0 is a major update from older versions, and has new Config Files with new Config options.");
				Log.warning("Unfortunately, there is no way to automatically update the old config files, so you will have to reconfigure TekkitRestrict if you want to use version 2.0 or beyond.");
				if (beta) Log.warning("It seems you used a 2.0 Beta version before, so config files should be similar, and easier to update.");

				Log.warning("Backing up old config files...");
				File backupDir = new File(getDataFolder(), "pre_2.0_configs");
				if (!backupDir.exists()){
					backupDir.mkdir();
				}

				for (File file : getDataFolder().listFiles()){
					if (file.getName().endsWith(".yml")){
						file.renameTo(new File(backupDir, file.getName()));
					}
				}

				File fe = new File(getDataFolder(), "Readme-TR-2.0.txt");
				if (!fe.exists()){
					try {
						fe.createNewFile();
					} catch (IOException ex) {}
					try (BufferedWriter bw = new BufferedWriter(new FileWriter(fe))){
						bw.write("It seems you have just upgraded to TekkitRestrict 2.0.");
						bw.newLine();
						bw.write("TekkitRestrict 2.0 is a major update from older versions, and has new Config Files with new Config options.");
						bw.newLine();
						bw.write("Unfortunately, there is no way to automatically update the old config files, so you will have to reconfigure TekkitRestrict if you want to use version 2.0 or beyond.");
						bw.newLine();
						if (beta){
							bw.write("It seems you used a 2.0 Beta version before, so config files should be similar, and easier to update.");
							bw.newLine();
						}
						bw.flush();
					} catch (Exception ex){

					}
				}

				try {
					f.createNewFile();
				} catch (Exception ex){

				}

				try {
					Log.info("Pausing server for 10 seconds so you can read the text above.");
					Thread.sleep(10000l);
				} catch (Exception ex){}
			} else {
				Log.trace("Found upgraded file. Skipping TekkitRestrict 2.0 Upgrade Text");
			}
		} catch (Exception ex){
			Log.severe("Uncaught Exception in CheckUpgradeTo20: ");
			Log.Exception(ex, true);
		}
	}
}