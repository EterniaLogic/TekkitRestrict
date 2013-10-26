package nl.taico.tekkitrestrict.eepatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.dreadslicer.tekkitrestrict.Log;
import com.github.dreadslicer.tekkitrestrict.tekkitrestrict;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;

import ee.events.EEEnums.EEAction2;
import ee.events.EEEnums.EEAmuletAction;
import ee.events.EEEnums.EERingAction;

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
				Log.Warning.config("There is an invalid value in the MaxCharge list in EEPatch.config.yml: \""+current+"\"");
				continue;
			}
			
			String temp[] = current.toLowerCase().split(" ");
			
			int charge = 0;
			try {
				charge = Integer.parseInt(temp[1]);
			} catch (NumberFormatException ex){
				Log.Warning.config("\""+temp[1]+"\" is not a valid chargelevel in the MaxCharge list in EEPatch.config.yml");
				continue;
			}
			
			Integer id = EENames.get(temp[0]);
			if (id == null){
				ArrayList<Integer> ids = getGroup(temp[0]);
				if (ids == null){
					Log.Warning.config("\""+temp[0]+"\" is not a valid itemname or itemgroup in the MaxCharge list in EEPatch.config.yml");
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
		tekkitrestrict.log.info("[DEBUG] " + "loading EEPatch Settings...");
		loadDisabledRingActions();
		loadDisabledDestActions();
		loadDisabledAmuletActions();
	}
	
	public static ArrayList<Integer> zeroring = new ArrayList<Integer>();
	public static ArrayList<Integer> firering = new ArrayList<Integer>();
	public static ArrayList<Integer> harvestring = new ArrayList<Integer>();
	public static ArrayList<Integer> flyring = new ArrayList<Integer>();
	
	public static ArrayList<Integer> arcanering = new ArrayList<Integer>();
	public static ArrayList<Integer> blackholeband = new ArrayList<Integer>();
	public static ArrayList<Integer> voidring = new ArrayList<Integer>();
	public static ArrayList<Integer> archangelring = new ArrayList<Integer>();
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
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.ZeroRing.FreezeRadius", true))
			zeroring.add(EERingAction.Freeze.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.ZeroRing.ThrowSnowball", true))
			zeroring.add(EERingAction.ThrowSnowball.ordinal());
		
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfIgnition.BurnRadius", true))
			firering.add(EERingAction.Burn.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfIgnition.PassiveExtinguishNearPlayer", true))
			firering.add(EERingAction.Extinguish.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfIgnition.ThrowPyrokinesis", true))
			firering.add(EERingAction.ThrowPyrokinesis.ordinal());
		
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.HarvestGodessBand.Fertilize", true))
			harvestring.add(EERingAction.Fertilize.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.HarvestGodessBand.PlantRadius", true))
			harvestring.add(EERingAction.PlantRadius.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.HarvestGodessBand.HarvestRadius", true))
			harvestring.add(EERingAction.Harvest.ordinal());
		
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.SwiftwolfsRendingGale.NegateFallDamage", true))
			flyring.add(EERingAction.NegateFallDamage.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.SwiftwolfsRendingGale.Interdict", true))
			flyring.add(EERingAction.Interdict.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.SwiftwolfsRendingGale.Gust", true))
			flyring.add(EERingAction.Gust.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.SwiftwolfsRendingGale.ActivateInterdict", true))
			flyring.add(EERingAction.ActivateInterdict.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.SwiftwolfsRendingGale.ActivateFlight", true))
			flyring.add(EERingAction.Activate.ordinal());
		
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.StrikeLightning", true))
			arcanering.add(EERingAction.StrikeLightning.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.Fertilize", true))
			arcanering.add(EERingAction.Fertilize.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.Interdict", true))
			arcanering.add(EERingAction.Interdict.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.FreezeRadius", true))
			arcanering.add(EERingAction.Freeze.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.BurnRadius", true))
			arcanering.add(EERingAction.Burn.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.HarvestRadius", true))
			arcanering.add(EERingAction.Harvest.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.Gust", true))
			arcanering.add(EERingAction.Gust.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.ThrowSnowball", true))
			arcanering.add(EERingAction.ThrowSnowball.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.ThrowPyrokinesis", true))
			arcanering.add(EERingAction.ThrowPyrokinesis.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.NegateFallDamage", true))
			arcanering.add(EERingAction.NegateFallDamage.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.RingOfArcana.Activate", true))
			arcanering.add(EERingAction.Activate.ordinal());
		
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.BlackHoleBand.AttractItems", true))
			blackholeband.add(EERingAction.AttractItems.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.BlackHoleBand.DeleteLiquid", true))
			blackholeband.add(EERingAction.DeleteLiquid.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.BlackHoleBand.Activate", true))
			blackholeband.add(EERingAction.Activate.ordinal());
		
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.VoidRing.AttractItems", true))
			voidring.add(EERingAction.AttractItems.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.VoidRing.DeleteLiquid", true))
			voidring.add(EERingAction.DeleteLiquid.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.VoidRing.Teleport", true))
			voidring.add(EERingAction.Teleport.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.VoidRing.Condense", true))
			voidring.add(EERingAction.Condense.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.VoidRing.Activate", true))
			voidring.add(EERingAction.Activate.ordinal());
		
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.ArchangelsSmite.ShootArrows", true))
			archangelring.add(EERingAction.ShootArrows.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Rings.ArchangelsSmite.Activate", true))
			archangelring.add(EERingAction.Activate.ordinal());
	}

	public static ArrayList<Integer> dest1 = new ArrayList<Integer>();
	public static ArrayList<Integer> dest2 = new ArrayList<Integer>();
	public static ArrayList<Integer> dest3 = new ArrayList<Integer>();
	public static void loadDisabledDestActions(){
		dest1.clear();
		dest2.clear();
		dest3.clear();
		
		if (!tekkitrestrict.config.getBoolean("Actions.Tools.Destruction.DestructionCatalyst.BreakRadius", true))
			dest1.add(EEAction2.BreakRadius.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Tools.Destruction.HyperKineticLens.BreakRadius", true))
			dest2.add(EEAction2.BreakRadius.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Tools.Destruction.CatalyticLens.BreakRadius", true))
			dest3.add(EEAction2.BreakRadius.ordinal());
	}
	
	public static ArrayList<Integer> evertide = new ArrayList<Integer>();
	public static ArrayList<Integer> volcanite = new ArrayList<Integer>();
	
	public static void loadDisabledAmuletActions(){
		if (!tekkitrestrict.config.getBoolean("Actions.Amulets.Evertide.CreateWater", true))
			evertide.add(EEAmuletAction.CreateWater.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Amulets.Evertide.CreateWaterBall", true))
			evertide.add(EEAmuletAction.CreateWaterBall.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Amulets.Evertide.PreventDrowning", true))
			evertide.add(EEAmuletAction.StopDrowning.ordinal());
		
		if (!tekkitrestrict.config.getBoolean("Actions.Amulets.Volcanite.CreateLava", true))
			volcanite.add(EEAmuletAction.CreateLava.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Amulets.Volcanite.CreateLavaBall", true))
			volcanite.add(EEAmuletAction.CreateLavaBall.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Amulets.Volcanite.Vaporize", true))
			volcanite.add(EEAmuletAction.Vaporize.ordinal());
		if (!tekkitrestrict.config.getBoolean("Actions.Amulets.Volcanite.FireImmune", true))
			volcanite.add(EEAmuletAction.FireImmune.ordinal());
	}
}
