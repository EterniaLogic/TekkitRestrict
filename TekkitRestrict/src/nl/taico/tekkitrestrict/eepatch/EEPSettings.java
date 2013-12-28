package nl.taico.tekkitrestrict.eepatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

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
		
		List<String> mc = tekkitrestrict.config.getStringList(ConfigFile.EEPatch, "EEMaxCharge");
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
		loadDisabledRingActions();
		loadDisabledDestActions();
		loadDisabledAmuletActions();
		loadDMToolActions();
		loadRMToolActions();
		loadRedToolActions();
		loadArmorActions();
		loadOtherActions();
	}
	
	public static ArrayList<EERingAction> zeroring = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> firering = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> harvestring = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> flyring = new ArrayList<EERingAction>();
	
	public static ArrayList<EERingAction> arcanering = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> blackholeband = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> voidring = new ArrayList<EERingAction>();
	public static ArrayList<EERingAction> archangelring = new ArrayList<EERingAction>();
	public static void loadDisabledRingActions(){
		zeroring.clear();
		firering.clear();
		harvestring.clear();
		flyring.clear();
		arcanering.clear();
		blackholeband.clear();
		voidring.clear();
		archangelring.clear();
		
		//tekkitrestrict.config.getConfigurationSection("Actions.Rings");
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.ZeroRing.FreezeRadius", true))
			zeroring.add(EERingAction.Freeze);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.ZeroRing.ThrowSnowball", true))
			zeroring.add(EERingAction.ThrowSnowball);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfIgnition.BurnRadius", true))
			firering.add(EERingAction.Burn);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfIgnition.PassiveExtinguishNearPlayer", true))
			firering.add(EERingAction.Extinguish);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfIgnition.ThrowPyrokinesis", true))
			firering.add(EERingAction.ThrowPyrokinesis);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.HarvestGodessBand.Fertilize", true))
			harvestring.add(EERingAction.Fertilize);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.HarvestGodessBand.PlantRadius", true))
			harvestring.add(EERingAction.PlantRadius);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.HarvestGodessBand.HarvestRadius", true))
			harvestring.add(EERingAction.Harvest);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.SwiftwolfsRendingGale.NegateFallDamage", true))
			flyring.add(EERingAction.NegateFallDamage);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.SwiftwolfsRendingGale.Interdict", true))
			flyring.add(EERingAction.Interdict);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.SwiftwolfsRendingGale.Gust", true))
			flyring.add(EERingAction.Gust);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.SwiftwolfsRendingGale.ActivateInterdict", true))
			flyring.add(EERingAction.ActivateInterdict);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.SwiftwolfsRendingGale.ActivateFlight", true))
			flyring.add(EERingAction.Activate);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.StrikeLightning", true))
			arcanering.add(EERingAction.StrikeLightning);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.Fertilize", true))
			arcanering.add(EERingAction.Fertilize);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.Interdict", true))
			arcanering.add(EERingAction.Interdict);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.FreezeRadius", true))
			arcanering.add(EERingAction.Freeze);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.BurnRadius", true))
			arcanering.add(EERingAction.Burn);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.HarvestRadius", true))
			arcanering.add(EERingAction.Harvest);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.Gust", true))
			arcanering.add(EERingAction.Gust);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.ThrowSnowball", true))
			arcanering.add(EERingAction.ThrowSnowball);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.ThrowPyrokinesis", true))
			arcanering.add(EERingAction.ThrowPyrokinesis);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.NegateFallDamage", true))
			arcanering.add(EERingAction.NegateFallDamage);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.RingOfArcana.Activate", true))
			arcanering.add(EERingAction.Activate);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.BlackHoleBand.AttractItems", true))
			blackholeband.add(EERingAction.AttractItems);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.BlackHoleBand.DeleteLiquid", true))
			blackholeband.add(EERingAction.DeleteLiquid);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.BlackHoleBand.Activate", true))
			blackholeband.add(EERingAction.Activate);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.VoidRing.AttractItems", true))
			voidring.add(EERingAction.AttractItems);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.VoidRing.DeleteLiquid", true))
			voidring.add(EERingAction.DeleteLiquid);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.VoidRing.Teleport", true))
			voidring.add(EERingAction.Teleport);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.VoidRing.Condense", true))
			voidring.add(EERingAction.Condense);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.VoidRing.Activate", true))
			voidring.add(EERingAction.Activate);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.ArchangelsSmite.ShootArrows", true))
			archangelring.add(EERingAction.ShootArrows);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Rings.ArchangelsSmite.Activate", true))
			archangelring.add(EERingAction.Activate);
	}

	public static ArrayList<EEAction2> dest1 = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dest2 = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dest3 = new ArrayList<EEAction2>();
	public static void loadDisabledDestActions(){
		dest1.clear();
		dest2.clear();
		dest3.clear();
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.Destruction.DestructionCatalyst.BreakRadius", true))
			dest1.add(EEAction2.BreakRadius);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.Destruction.HyperKineticLens.BreakRadius", true))
			dest2.add(EEAction2.BreakRadius);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.Destruction.CatalyticLens.BreakRadius", true))
			dest3.add(EEAction2.BreakRadius);
	}
	
	public static ArrayList<EEAmuletAction> evertide = new ArrayList<EEAmuletAction>();
	public static ArrayList<EEAmuletAction> volcanite = new ArrayList<EEAmuletAction>();
	
	public static void loadDisabledAmuletActions(){
		evertide.clear();
		volcanite.clear();
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Amulets.Evertide.CreateWater", true))
			evertide.add(EEAmuletAction.CreateWater);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Amulets.Evertide.CreateWaterBall", true))
			evertide.add(EEAmuletAction.CreateWaterBall);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Amulets.Evertide.PreventDrowning", true))
			evertide.add(EEAmuletAction.StopDrowning);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Amulets.Volcanite.CreateLava", true))
			volcanite.add(EEAmuletAction.CreateLava);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Amulets.Volcanite.CreateLavaBall", true))
			volcanite.add(EEAmuletAction.CreateLavaBall);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Amulets.Volcanite.Vaporize", true))
			volcanite.add(EEAmuletAction.Vaporize);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Amulets.Volcanite.FireImmune", true))
			volcanite.add(EEAmuletAction.FireImmune);
	}

	public static ArrayList<EEAction2> dmaxe = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmpick = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmshovel = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmhoe = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmshears = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmhammer = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> dmsword = new ArrayList<EEAction2>();

	public static void loadDMToolActions(){
		dmaxe.clear();
		dmpick.clear();
		dmshovel.clear();
		dmhoe.clear();
		dmshears.clear();
		dmhammer.clear();
		dmsword.clear();
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Pickaxe.Break-3.Tall", true))
			dmpick.add(EEAction2.TallBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Pickaxe.Break-3.Wide", true))
			dmpick.add(EEAction2.WideBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Pickaxe.Break-3.Long", true))
			dmpick.add(EEAction2.LongBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Pickaxe.BreakOreVein", true))
			dmpick.add(EEAction2.BreakRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Axe.BreakRadius", true))
			dmaxe.add(EEAction2.BreakRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Shovel.Break-3.Tall", true))
			dmshovel.add(EEAction2.TallBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Shovel.Break-3.Wide", true))
			dmshovel.add(EEAction2.WideBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Shovel.Break-3.Long", true))
			dmshovel.add(EEAction2.LongBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Shovel.BreakRadius", true))
			dmshovel.add(EEAction2.BreakRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Hoe.TillRadius", true))
			dmhoe.add(EEAction2.TillRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Shears.BreakRadius", true))
			dmshears.add(EEAction2.BreakRadius);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Shears.Shear", true))
			dmshears.add(EEAction2.Shear);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Hammer.MegaBreak", true))
			dmhammer.add(EEAction2.MegaBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Hammer.BreakRadius", true))
			dmhammer.add(EEAction2.BreakRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.DarkMatter.Sword.AttackRadius", true))
			dmsword.add(EEAction2.AttackRadius);
	}
	
	public static ArrayList<EEAction2> rmaxe = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmpick = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmshovel = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmhoe = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmshears = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmhammer = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> rmsword = new ArrayList<EEAction2>();

	public static void loadRMToolActions(){
		rmaxe.clear();
		rmpick.clear();
		rmshovel.clear();
		rmhoe.clear();
		rmshears.clear();
		rmhammer.clear();
		rmsword.clear();
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Pickaxe.Break-3.Tall", true))
			rmpick.add(EEAction2.TallBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Pickaxe.Break-3.Wide", true))
			rmpick.add(EEAction2.WideBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Pickaxe.Break-3.Long", true))
			rmpick.add(EEAction2.LongBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Pickaxe.BreakOreVein", true))
			rmpick.add(EEAction2.BreakRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Axe.BreakRadius", true))
			rmaxe.add(EEAction2.BreakRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Shovel.Break-3.Tall", true))
			rmshovel.add(EEAction2.TallBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Shovel.Break-3.Wide", true))
			rmshovel.add(EEAction2.WideBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Shovel.Break-3.Long", true))
			rmshovel.add(EEAction2.LongBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Shovel.BreakRadius", true))
			rmshovel.add(EEAction2.BreakRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Hoe.TillRadius", true))
			rmhoe.add(EEAction2.TillRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Shears.BreakRadius", true))
			rmshears.add(EEAction2.BreakRadius);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Shears.Shear", true))
			rmshears.add(EEAction2.Shear);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Hammer.MegaBreak", true))
			rmhammer.add(EEAction2.MegaBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Hammer.BreakRadius", true))
			rmhammer.add(EEAction2.BreakRadius);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.RedMatter.Sword.AttackRadius", true))
			rmsword.add(EEAction2.AttackRadius);
	}
	
	public static ArrayList<EEAction2> katar = new ArrayList<EEAction2>();
	public static ArrayList<EEAction2> morningstar = new ArrayList<EEAction2>();
	
	public static void loadRedToolActions(){
		katar.clear();
		morningstar.clear();
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.Katar.BreakRadius", true))
			katar.add(EEAction2.BreakRadius);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.Katar.TillRadius", true))
			katar.add(EEAction2.TillRadius);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.Katar.AttackRadius", true))
			katar.add(EEAction2.AttackRadius);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.Katar.Shear", true))
			katar.add(EEAction2.Shear);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.MorningStar.Break-3.Tall", true))
			morningstar.add(EEAction2.TallBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.MorningStar.Break-3.Wide", true))
			morningstar.add(EEAction2.WideBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.MorningStar.Break-3.Long", true))
			morningstar.add(EEAction2.LongBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.MorningStar.MegaBreak", true))
			morningstar.add(EEAction2.MegaBreak);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Tools.MorningStar.BreakRadius", true))
			morningstar.add(EEAction2.BreakRadius);
	}
	
	public static ArrayList<EEArmorAction> armor = new ArrayList<EEArmorAction>();
	public static void loadArmorActions(){
		armor.clear();
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Armor.Offensive.Activate", true))
			armor.add(EEArmorAction.OffensiveActivate);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Armor.Offensive.Explode", true))
			armor.add(EEArmorAction.OffensiveExplode);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Armor.Offensive.Strike", true))
			armor.add(EEArmorAction.OffensiveStrike);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Armor.Movement.Activate", true))
			armor.add(EEArmorAction.MovementActivate);
	}

	
	public static ArrayList<EETransmuteAction> phil = new ArrayList<EETransmuteAction>();
	public static ArrayList<EETransmuteAction> trans = new ArrayList<EETransmuteAction>();
	public static ArrayList<EEPedestalAction> pedestal = new ArrayList<EEPedestalAction>();
	public static ArrayList<EEWatchAction> watch = new ArrayList<EEWatchAction>();
	public static void loadOtherActions(){
		phil.clear();
		trans.clear();
		pedestal.clear();
		watch.clear();
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.PhilosopherStone.ChangeMob", true))
			phil.add(EETransmuteAction.ChangeMob);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.PhilosopherStone.PortableCrafting", true))
			phil.add(EETransmuteAction.PortableCrafting);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.PhilosopherStone.Transmute", true))
			phil.add(EETransmuteAction.Transmute);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.TransmutionTablet.ChangeMob", true))
			trans.add(EETransmuteAction.ChangeMob);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.TransmutionTablet.PortableTable", true))
			trans.add(EETransmuteAction.PortableTable);
		
		//TODO IMPORTANT Add pedestal listener
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.Activate", true))
			pedestal.add(EEPedestalAction.Activate);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.Attract", true))
			pedestal.add(EEPedestalAction.Attract);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.Harvest", true))
			pedestal.add(EEPedestalAction.Harvest);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.Heal", true))
			pedestal.add(EEPedestalAction.Heal);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.Ignition", true))
			pedestal.add(EEPedestalAction.Ignition);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.Interdict", true))
			pedestal.add(EEPedestalAction.Interdict);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.Repair", true))
			pedestal.add(EEPedestalAction.Repair);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.ShootArrow", true))
			pedestal.add(EEPedestalAction.ShootArrow);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.StopStorm", true))
			pedestal.add(EEPedestalAction.StopStorm);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.Storm", true))
			pedestal.add(EEPedestalAction.Storm);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.StrikeLightning", true))
			pedestal.add(EEPedestalAction.StrikeLightning);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.Pedestal.Time", true))
			pedestal.add(EEPedestalAction.Time);
		
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.WatchOfFlowingTime.ScrollTimeForwards", true))
			watch.add(EEWatchAction.TimeForward);
		if (!tekkitrestrict.config.getBoolean2(ConfigFile.EEPatch, "Actions.Other.WatchOfFlowingTime.ScrollTimeBackwards", true))
			watch.add(EEWatchAction.TimeBackward);
	}
}
