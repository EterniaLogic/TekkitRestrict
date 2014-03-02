package nl.taico.tekkitrestrict.eepatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.config.EEPatchConfig;

import ee.events.EEEnums.EEAction2;
import ee.events.EEEnums.EEAmuletAction;
import ee.events.EEEnums.EEPedestalAction;
import ee.events.EEEnums.EERingAction;
import ee.events.EEEnums.EEArmorAction;
import ee.events.EEEnums.EETransmuteAction;
import ee.events.EEEnums.EEWatchAction;

public class EEPSettings {
	public static HashMap<Integer, Integer> MaxCharge = new HashMap<Integer, Integer>();
	
	public static HashMap<String, ArrayList<Integer>> Groups = new HashMap<String, ArrayList<Integer>>();
	public static HashMap<String, Integer> EENames = new HashMap<String, Integer>();
	static {
		EENames.put("dmpickaxe", 27543);
		EENames.put("dmspade", 27544);
		EENames.put("dmshovel", 27544);
		EENames.put("dmhoe", 27545);
		EENames.put("dmsword", 27546);
		EENames.put("dmaxe", 27547);
		EENames.put("dmshears", 27548);
		EENames.put("dmhammer", 27555);

		EENames.put("rmpickaxe", 27564);
		EENames.put("rmshovel", 27565);
		EENames.put("rmspade", 27565);
		EENames.put("rmhoe", 27566);
		EENames.put("rmsword", 27567);
		EENames.put("rmaxe", 27568);
		EENames.put("rmshears", 27569);
		EENames.put("rmhammer", 27570);
		
		EENames.put("rmkatar", 27572);
		EENames.put("redkatar", 27572);
		EENames.put("katar", 27572);
		EENames.put("rmmorningstar", 27573);
		EENames.put("redmorningstar", 27573);
		EENames.put("morningstar", 27573);
		
		EENames.put("destructioncatalyst", 27527);
		EENames.put("hyperkineticlens", 27535);
		EENames.put("hyperlens", 27535);
		EENames.put("cataclycticlens", 27556);
		EENames.put("catalyticlens", 27556);
		
		EENames.put("evertideamulet", 27530);
		EENames.put("volcaniteamulet", 27531);
		
		EENames.put("zeroring", 27574);
		EENames.put("freezering", 27574);
		EENames.put("snowring", 27574);
		EENames.put("ringofignition", 27533);
		EENames.put("ignitionring", 27533);
		EENames.put("firering", 27533);
		
		EENames.put("ringofarcana", 27584);
		EENames.put("arcanaring", 27584);
		EENames.put("voidring", 27593);
		EENames.put("harvestring", 27537);
		EENames.put("harvestgodessband", 27537);
		EENames.put("blackholeband", 27532);
		EENames.put("bhb", 27532);
		EENames.put("archangelsmite", 27534);
		EENames.put("archangelssmite", 27534);
		EENames.put("archangelring", 27534);
		EENames.put("archangelsring", 27534);
		EENames.put("swiftwolfsrendinggale", 27536);
		EENames.put("swiftwolfrendinggale", 27536);
		EENames.put("swiftwolfsring", 27536);
		EENames.put("swiftwolfring", 27536);
		EENames.put("SWRG", 27536);
		
		EENames.put("philosopherstone", 27526);
		EENames.put("philosophersstone", 27526);
		EENames.put("watchofflowingtime", 27538);
		EENames.put("watchoftime", 27538);
		EENames.put("watch", 27538);
		EENames.put("mercurialeye", 27583);
		
		EENames.put("dmchest", 27549);
		EENames.put("dmchestplate", 27549);
		EENames.put("dmhelmet", 27550);
		EENames.put("dmleggings", 27551);
		EENames.put("dmgreaves", 27551);
		EENames.put("dmboots", 27552);
		
		EENames.put("rmchest", 27575);
		EENames.put("rmchestplate", 27575);
		EENames.put("rmhelmet", 27576);
		EENames.put("rmleggings", 27577);
		EENames.put("rmgreaves", 27577);
		EENames.put("rmboots", 27578);
		
		EENames.put("infernalarmor", 27579);
		EENames.put("gemchest", 27579);
		EENames.put("gemchestplate", 27579);
		EENames.put("abysshelmet", 27580);
		EENames.put("gemhelmet", 27580);
		EENames.put("gravitygreaves", 27581);
		EENames.put("gemgreaves", 27581);
		EENames.put("gemleggings", 27581);
		EENames.put("hurricaneboots", 27582);
		EENames.put("gemboots", 27582);
		
		EENames.put("gemofeternaldensity", 27553);
		EENames.put("repairtalisman", 27554);
		
		EENames.put("soulstone", 27529);
		EENames.put("bodystone", 27588);
		EENames.put("lifestone", 27589);
		EENames.put("mindstone", 27590);
		
		EENames.put("diviningrod", 27585);
		
		EENames.put("transmutationtablet", 27592);
		
		EENames.put("kleinstarein", 27557);
		EENames.put("kleinstar1", 27557);
		EENames.put("kleinstarzwei", 27558);
		EENames.put("kleinstar2", 27558);
		EENames.put("kleinstardrei", 27559);
		EENames.put("kleinstar3", 27559);
		EENames.put("kleinstarvier", 27560);
		EENames.put("kleinstar4", 27560);
		EENames.put("kleinstarsphere", 27561);
		EENames.put("kleinstar5", 27561);
		EENames.put("kleinstaromega", 27591);
		EENames.put("kleinstar6", 27591);
		EENames.put("alchemybag", 27562);
		
		ArrayList<Integer> dmtools = new ArrayList<Integer>();
		dmtools.add(27543);
		dmtools.add(27544);
		dmtools.add(27545);
		dmtools.add(27546);
		dmtools.add(27547);
		dmtools.add(27548);
		dmtools.add(27555);
		Groups.put("dmtools", dmtools);
		
		ArrayList<Integer> rmtools = new ArrayList<Integer>();
		rmtools.add(27564);
		rmtools.add(27565);
		rmtools.add(27566);
		rmtools.add(27567);
		rmtools.add(27568);
		rmtools.add(27569);
		rmtools.add(27570);
		Groups.put("rmtools", rmtools);
		
		ArrayList<Integer> amulets = new ArrayList<Integer>();
		amulets.add(27530);
		amulets.add(27531);
		Groups.put("amulets", amulets);
		
		ArrayList<Integer> rings = new ArrayList<Integer>();
		rings.add(27574);
		rings.add(27533);
		Groups.put("rings", rings);
	}
	
	public static ArrayList<Integer> getGroup(String name){
		return Groups.get(name);
	}
	
	public static void loadMaxCharge(){
		MaxCharge.clear();
		
		List<String> mc = EEPatchConfig.getConfig().getStringList("EEMaxCharge");
		for (String current : mc){
			if (current == null) continue;
			if (!current.contains(" ")){
				Log.Warning.config("There is an invalid value in the MaxCharge list in EEPatch.config.yml: \""+current+"\"", false);
				continue;
			}
			
			String temp[] = current.toLowerCase().split(" ");
			
			int charge = 0;
			try {
				charge = Integer.parseInt(temp[1]);
			} catch (NumberFormatException ex){
				Log.Warning.config("\""+temp[1]+"\" is not a valid chargelevel in the MaxCharge list in EEPatch.config.yml", false);
				continue;
			}
			
			Integer id = EENames.get(temp[0]);
			if (id == null){
				ArrayList<Integer> ids = getGroup(temp[0]);
				if (ids == null){
					Log.Warning.config("\""+temp[0]+"\" is not a valid itemname or itemgroup in the MaxCharge list in EEPatch.config.yml", false);
					continue;
				}
				
				for (int nr : ids){
					MaxCharge.put(nr, charge);
				}
				
			} else {
				MaxCharge.put(id, charge);
			}
		}
	}
	
	public static void loadAllDisabledActions(){
		final FileConfiguration config = EEPatchConfig.getConfig();
		loadDisabledRingActions(config.getConfigurationSection("Actions.Rings"));
		loadDisabledDestActions(config.getConfigurationSection("Actions.Tools.Destruction"));
		loadDisabledAmuletActions(config.getConfigurationSection("Actions.Amulets"));
		loadDMToolActions(config.getConfigurationSection("Actions.Tools.DarkMatter"));
		loadRMToolActions(config.getConfigurationSection("Actions.Tools.RedMatter"));
		loadRedToolActions(config.getConfigurationSection("Actions.Tools"));
		loadArmorActions(config.getConfigurationSection("Actions.Armor"));
		loadOtherActions(config.getConfigurationSection("Actions.Other"));
	}
	
	public static ArrayList<EERingAction> zeroring = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> firering = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> harvestring = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> flyring = new ArrayList<EERingAction>();
	
	public static ArrayList<EERingAction> arcanering = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> blackholeband = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> voidring = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> archangelring = new ArrayList<EERingAction>();
	public static void loadDisabledRingActions(ConfigurationSection config){
		zeroring.clear();
		firering.clear();
		harvestring.clear();
		flyring.clear();
		arcanering.clear();
		blackholeband.clear();
		voidring.clear();
		archangelring.clear();
		
		if (!config.getBoolean("ZeroRing.FreezeRadius", true))
			zeroring.add(EERingAction.Freeze);
		if (!config.getBoolean("ZeroRing.ThrowSnowball", true))
			zeroring.add(EERingAction.ThrowSnowball);
		
		if (!config.getBoolean("RingOfIgnition.BurnRadius", true))
			firering.add(EERingAction.Burn);
		if (!config.getBoolean("RingOfIgnition.PassiveExtinguishNearPlayer", true))
			firering.add(EERingAction.Extinguish);
		if (!config.getBoolean("RingOfIgnition.ThrowPyrokinesis", true))
			firering.add(EERingAction.ThrowPyrokinesis);
		
		if (!config.getBoolean("HarvestGodessBand.Fertilize", true))
			harvestring.add(EERingAction.Fertilize);
		if (!config.getBoolean("HarvestGodessBand.PlantRadius", true))
			harvestring.add(EERingAction.PlantRadius);
		if (!config.getBoolean("HarvestGodessBand.HarvestRadius", true))
			harvestring.add(EERingAction.Harvest);
		
		if (!config.getBoolean("SwiftwolfsRendingGale.NegateFallDamage", true))
			flyring.add(EERingAction.NegateFallDamage);
		if (!config.getBoolean("SwiftwolfsRendingGale.Interdict", true))
			flyring.add(EERingAction.Interdict);
		if (!config.getBoolean("SwiftwolfsRendingGale.Gust", true))
			flyring.add(EERingAction.Gust);
		if (!config.getBoolean("SwiftwolfsRendingGale.ActivateInterdict", true))
			flyring.add(EERingAction.ActivateInterdict);
		if (!config.getBoolean("SwiftwolfsRendingGale.ActivateFlight", true))
			flyring.add(EERingAction.Activate);
		
		if (!config.getBoolean("RingOfArcana.StrikeLightning", true))
			arcanering.add(EERingAction.StrikeLightning);
		if (!config.getBoolean("RingOfArcana.Fertilize", true))
			arcanering.add(EERingAction.Fertilize);
		if (!config.getBoolean("RingOfArcana.Interdict", true))
			arcanering.add(EERingAction.Interdict);
		if (!config.getBoolean("RingOfArcana.FreezeRadius", true))
			arcanering.add(EERingAction.Freeze);
		if (!config.getBoolean("RingOfArcana.BurnRadius", true))
			arcanering.add(EERingAction.Burn);
		if (!config.getBoolean("RingOfArcana.HarvestRadius", true))
			arcanering.add(EERingAction.Harvest);
		if (!config.getBoolean("RingOfArcana.Gust", true))
			arcanering.add(EERingAction.Gust);
		if (!config.getBoolean("RingOfArcana.ThrowSnowball", true))
			arcanering.add(EERingAction.ThrowSnowball);
		if (!config.getBoolean("RingOfArcana.ThrowPyrokinesis", true))
			arcanering.add(EERingAction.ThrowPyrokinesis);
		if (!config.getBoolean("RingOfArcana.NegateFallDamage", true))
			arcanering.add(EERingAction.NegateFallDamage);
		if (!config.getBoolean("RingOfArcana.Activate", true))
			arcanering.add(EERingAction.Activate);
		
		if (!config.getBoolean("BlackHoleBand.AttractItems", true))
			blackholeband.add(EERingAction.AttractItems);
		if (!config.getBoolean("BlackHoleBand.DeleteLiquid", true))
			blackholeband.add(EERingAction.DeleteLiquid);
		if (!config.getBoolean("BlackHoleBand.Activate", true))
			blackholeband.add(EERingAction.Activate);
		
		if (!config.getBoolean("VoidRing.AttractItems", true))
			voidring.add(EERingAction.AttractItems);
		if (!config.getBoolean("VoidRing.DeleteLiquid", true))
			voidring.add(EERingAction.DeleteLiquid);
		if (!config.getBoolean("VoidRing.Teleport", true))
			voidring.add(EERingAction.Teleport);
		if (!config.getBoolean("VoidRing.Condense", true))
			voidring.add(EERingAction.Condense);
		if (!config.getBoolean("VoidRing.Activate", true))
			voidring.add(EERingAction.Activate);
		
		if (!config.getBoolean("ArchangelsSmite.ShootArrows", true))
			archangelring.add(EERingAction.ShootArrows);
		if (!config.getBoolean("ArchangelsSmite.Activate", true))
			archangelring.add(EERingAction.Activate);
	}

	public static ArrayList<EEAction2> dest1 = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dest2 = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dest3 = new ArrayList<EEAction2>();
	public static void loadDisabledDestActions(ConfigurationSection config){
		dest1.clear();
		dest2.clear();
		dest3.clear();
		
		if (!config.getBoolean("DestructionCatalyst.BreakRadius", true))
			dest1.add(EEAction2.BreakRadius);
		if (!config.getBoolean("HyperKineticLens.BreakRadius", true))
			dest2.add(EEAction2.BreakRadius);
		if (!config.getBoolean("CatalyticLens.BreakRadius", true))
			dest3.add(EEAction2.BreakRadius);
	}
	
	public static ArrayList<EEAmuletAction> evertide = new ArrayList<EEAmuletAction>();
	public static ArrayList<EEAmuletAction> volcanite = new ArrayList<EEAmuletAction>();
	
	public static void loadDisabledAmuletActions(ConfigurationSection config){
		evertide.clear();
		volcanite.clear();
		
		if (!config.getBoolean("Evertide.CreateWater", true))
			evertide.add(EEAmuletAction.CreateWater);
		if (!config.getBoolean("Evertide.CreateWaterBall", true))
			evertide.add(EEAmuletAction.CreateWaterBall);
		if (!config.getBoolean("Evertide.PreventDrowning", true))
			evertide.add(EEAmuletAction.StopDrowning);
		
		if (!config.getBoolean("Volcanite.CreateLava", true))
			volcanite.add(EEAmuletAction.CreateLava);
		if (!config.getBoolean("Volcanite.CreateLavaBall", true))
			volcanite.add(EEAmuletAction.CreateLavaBall);
		if (!config.getBoolean("Volcanite.Vaporize", true))
			volcanite.add(EEAmuletAction.Vaporize);
		if (!config.getBoolean("Volcanite.FireImmune", true))
			volcanite.add(EEAmuletAction.FireImmune);
	}

	public static ArrayList<EEAction2> dmaxe = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmpick = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmshovel = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmhoe = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmshears = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmhammer = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmsword = new ArrayList<EEAction2>();

	public static void loadDMToolActions(ConfigurationSection config){
		dmaxe.clear();
		dmpick.clear();
		dmshovel.clear();
		dmhoe.clear();
		dmshears.clear();
		dmhammer.clear();
		dmsword.clear();
		
		if (!config.getBoolean("Pickaxe.Break-3.Tall", true))
			dmpick.add(EEAction2.TallBreak);
		if (!config.getBoolean("Pickaxe.Break-3.Wide", true))
			dmpick.add(EEAction2.WideBreak);
		if (!config.getBoolean("Pickaxe.Break-3.Long", true))
			dmpick.add(EEAction2.LongBreak);
		if (!config.getBoolean("Pickaxe.BreakOreVein", true))
			dmpick.add(EEAction2.BreakRadius);
		
		if (!config.getBoolean("Axe.BreakRadius", true))
			dmaxe.add(EEAction2.BreakRadius);
		
		if (!config.getBoolean("Shovel.Break-3.Tall", true))
			dmshovel.add(EEAction2.TallBreak);
		if (!config.getBoolean("Shovel.Break-3.Wide", true))
			dmshovel.add(EEAction2.WideBreak);
		if (!config.getBoolean("Shovel.Break-3.Long", true))
			dmshovel.add(EEAction2.LongBreak);
		if (!config.getBoolean("Shovel.BreakRadius", true))
			dmshovel.add(EEAction2.BreakRadius);
		
		if (!config.getBoolean("Hoe.TillRadius", true))
			dmhoe.add(EEAction2.TillRadius);
		
		if (!config.getBoolean("Shears.BreakRadius", true))
			dmshears.add(EEAction2.BreakRadius);
		if (!config.getBoolean("Shears.Shear", true))
			dmshears.add(EEAction2.Shear);
		
		if (!config.getBoolean("Hammer.MegaBreak", true))
			dmhammer.add(EEAction2.MegaBreak);
		if (!config.getBoolean("Hammer.BreakRadius", true))
			dmhammer.add(EEAction2.BreakRadius);
		
		if (!config.getBoolean("Sword.AttackRadius", true))
			dmsword.add(EEAction2.AttackRadius);
	}
	
	public static ArrayList<EEAction2> rmaxe = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmpick = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmshovel = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmhoe = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmshears = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmhammer = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmsword = new ArrayList<EEAction2>();

	public static void loadRMToolActions(ConfigurationSection config){
		rmaxe.clear();
		rmpick.clear();
		rmshovel.clear();
		rmhoe.clear();
		rmshears.clear();
		rmhammer.clear();
		rmsword.clear();
		
		if (!config.getBoolean("Pickaxe.Break-3.Tall", true))
			rmpick.add(EEAction2.TallBreak);
		if (!config.getBoolean("Pickaxe.Break-3.Wide", true))
			rmpick.add(EEAction2.WideBreak);
		if (!config.getBoolean("Pickaxe.Break-3.Long", true))
			rmpick.add(EEAction2.LongBreak);
		if (!config.getBoolean("Pickaxe.BreakOreVein", true))
			rmpick.add(EEAction2.BreakRadius);
		
		if (!config.getBoolean("Axe.BreakRadius", true))
			rmaxe.add(EEAction2.BreakRadius);
		
		if (!config.getBoolean("Shovel.Break-3.Tall", true))
			rmshovel.add(EEAction2.TallBreak);
		if (!config.getBoolean("Shovel.Break-3.Wide", true))
			rmshovel.add(EEAction2.WideBreak);
		if (!config.getBoolean("Shovel.Break-3.Long", true))
			rmshovel.add(EEAction2.LongBreak);
		if (!config.getBoolean("Shovel.BreakRadius", true))
			rmshovel.add(EEAction2.BreakRadius);
		
		if (!config.getBoolean("Hoe.TillRadius", true))
			rmhoe.add(EEAction2.TillRadius);
		
		if (!config.getBoolean("Shears.BreakRadius", true))
			rmshears.add(EEAction2.BreakRadius);
		if (!config.getBoolean("Shears.Shear", true))
			rmshears.add(EEAction2.Shear);
		
		if (!config.getBoolean("Hammer.MegaBreak", true))
			rmhammer.add(EEAction2.MegaBreak);
		if (!config.getBoolean("Hammer.BreakRadius", true))
			rmhammer.add(EEAction2.BreakRadius);
		
		if (!config.getBoolean("Sword.AttackRadius", true))
			rmsword.add(EEAction2.AttackRadius);
	}
	
	public static ArrayList<EEAction2> katar = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> morningstar = new ArrayList<EEAction2>();
	
	public static void loadRedToolActions(ConfigurationSection config){
		katar.clear();
		morningstar.clear();
		
		if (!config.getBoolean("Katar.BreakRadius", true))
			katar.add(EEAction2.BreakRadius);
		if (!config.getBoolean("Katar.TillRadius", true))
			katar.add(EEAction2.TillRadius);
		if (!config.getBoolean("Katar.AttackRadius", true))
			katar.add(EEAction2.AttackRadius);
		if (!config.getBoolean("Katar.Shear", true))
			katar.add(EEAction2.Shear);
		
		if (!config.getBoolean("MorningStar.Break-3.Tall", true))
			morningstar.add(EEAction2.TallBreak);
		if (!config.getBoolean("MorningStar.Break-3.Wide", true))
			morningstar.add(EEAction2.WideBreak);
		if (!config.getBoolean("MorningStar.Break-3.Long", true))
			morningstar.add(EEAction2.LongBreak);
		if (!config.getBoolean("MorningStar.MegaBreak", true))
			morningstar.add(EEAction2.MegaBreak);
		if (!config.getBoolean("MorningStar.BreakRadius", true))
			morningstar.add(EEAction2.BreakRadius);
	}
	
	public static ArrayList<EEArmorAction> armor = new ArrayList<EEArmorAction>();
	public static void loadArmorActions(ConfigurationSection config){
		armor.clear();
		
		if (!config.getBoolean("Offensive.Activate", true))
			armor.add(EEArmorAction.OffensiveActivate);
		if (!config.getBoolean("Offensive.Explode", true))
			armor.add(EEArmorAction.OffensiveExplode);
		if (!config.getBoolean("Offensive.Strike", true))
			armor.add(EEArmorAction.OffensiveStrike);
		if (!config.getBoolean("Movement.Activate", true))
			armor.add(EEArmorAction.MovementActivate);
	}

	
	public static ArrayList<EETransmuteAction> phil = new ArrayList<EETransmuteAction>();
	public static ArrayList<EETransmuteAction> trans = new ArrayList<EETransmuteAction>();
	public static ArrayList<EEPedestalAction> pedestal = new ArrayList<EEPedestalAction>();
	public static ArrayList<EEWatchAction> watch = new ArrayList<EEWatchAction>();
	public static void loadOtherActions(ConfigurationSection config){
		phil.clear();
		trans.clear();
		pedestal.clear();
		watch.clear();
		if (!config.getBoolean("PhilosopherStone.ChangeMob", true))
			phil.add(EETransmuteAction.ChangeMob);
		if (!config.getBoolean("PhilosopherStone.PortableCrafting", true))
			phil.add(EETransmuteAction.PortableCrafting);
		if (!config.getBoolean("PhilosopherStone.Transmute", true))
			phil.add(EETransmuteAction.Transmute);
		
		if (!config.getBoolean("TransmutionTablet.ChangeMob", true))
			trans.add(EETransmuteAction.ChangeMob);
		if (!config.getBoolean("TransmutionTablet.PortableTable", true))
			trans.add(EETransmuteAction.PortableTable);
		
		if (!config.getBoolean("Pedestal.Activate", true))
			pedestal.add(EEPedestalAction.Activate);
		if (!config.getBoolean("Pedestal.Attract", true))
			pedestal.add(EEPedestalAction.Attract);
		if (!config.getBoolean("Pedestal.Harvest", true))
			pedestal.add(EEPedestalAction.Harvest);
		if (!config.getBoolean("Pedestal.Heal", true))
			pedestal.add(EEPedestalAction.Heal);
		if (!config.getBoolean("Pedestal.Ignition", true))
			pedestal.add(EEPedestalAction.Ignition);
		if (!config.getBoolean("Pedestal.Interdict", true))
			pedestal.add(EEPedestalAction.Interdict);
		if (!config.getBoolean("Pedestal.Repair", true))
			pedestal.add(EEPedestalAction.Repair);
		if (!config.getBoolean("Pedestal.ShootArrow", true))
			pedestal.add(EEPedestalAction.ShootArrow);
		if (!config.getBoolean("Pedestal.StopStorm", true))
			pedestal.add(EEPedestalAction.StopStorm);
		if (!config.getBoolean("Pedestal.Storm", true))
			pedestal.add(EEPedestalAction.Storm);
		if (!config.getBoolean("Pedestal.StrikeLightning", true))
			pedestal.add(EEPedestalAction.StrikeLightning);
		if (!config.getBoolean("Pedestal.Time", true))
			pedestal.add(EEPedestalAction.Time);
		
		if (!config.getBoolean("WatchOfFlowingTime.ScrollTimeForwards", true))
			watch.add(EEWatchAction.TimeForward);
		if (!config.getBoolean("WatchOfFlowingTime.ScrollTimeBackwards", true))
			watch.add(EEWatchAction.TimeBackward);
	}
}
