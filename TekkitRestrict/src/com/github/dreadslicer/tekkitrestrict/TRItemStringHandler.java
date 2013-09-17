package com.github.dreadslicer.tekkitrestrict;

public class TRItemStringHandler {
	public static String parseItem(String name){
		String tbr = parseIC2Name(name);
		if (tbr == null) tbr = parseEEName(name);
		
		return tbr;
	}
	
	public static String parseIC2Name(String name){
		name = name.toLowerCase();
		switch (name){
			case "quantumhelmet":
				return "30171:*";
			case "quantumchestplate":
			case "quantumchest":
			case "quantumbody":
			case "quantumbodyarmor":
				return "30172:*";
			case "quantumleggings":
			case "quantumpants":
			case "quantumlegs":
				return "30173:*";
			case "quantumboots":
			case "quantumshoes":
				return "30174:*";
			
			case "quantumarmor":
			case "quantumsuit":
				return "30171-30174:*";
				
			case "nanohelmet":
				return "30178:*";
			case "nanochestplate":
			case "nanochest":
			case "nanobody":
			case "nanobodyarmor":
				return "30177:*";
			case "nanoleggings":
			case "nanolegs":
			case "nanopants":
				return "30176:*";
			case "nanoboots":
			case "nanoshoes":
				return "30175:*";
				
			case "nanoarmor":
			case "nanosuit":
				return "30175-30178:*";
				
			case "jetpack":
			case "electricjetpack":
				return "30209:*";
				
			case "batpack":
			case "batterypack":
				return "30180:*";
			case "lappack":
				return "30127:*";
				
			case "chainsaw":
				return "30233:*";
			case "miningdrill":
			case "drill":
				return "30235:*";
			case "ddrill":
			case "diamonddrill":
				return "30234:*";
				
			case "electrichoe":
				return "30119:*";
			case "electricwrench":
				return "30141:*";
			case "electrictreetap":
				return "30124:*";
				
			case "nanosaber":
				return "30148:*";
				
			case "mininglaser":
				return "30208:*";
				
			case "rebattery":
			case "re-battery":
				return "30242:*";
			case "energycrystal":
				return "30241:*";
			case "lapatronctrystal":
				return "30240:*";
				
			case "scanner":
			case "od-scanner":
			case "odscanner":
				return "30220:*";
			case "ov-scanner":
			case "ovscanner":
				return "30219:*";
				
			case "digitalthermometer":
				return "31257:*";
				
			default:
				return null;
		}
	}
	
	public static String parseEEName(String name){
		name = name.toLowerCase();
		switch (name){
			case "dmpickaxe": return "27543:*";
			case "dmspade":
			case "dmshovel": return "27544:*";
			case "dmhoe": return "27545:*";
			case "dmsword": return "27546:*";
			case "dmaxe": return "27547:*";
			case "dmshears": return "27548:*";
			case "dmhammer": return "27555:*";
			
			case "dmtools": return "27543-27548:*;27555:*";
	
			case "rmpickaxe": return "27564:*";
			case "rmshovel":
			case "rmspade": return "27565:*";
			case "rmhoe": return "27566:*";
			case "rmsword": return "27567:*";
			case "rmaxe": return "27568:*";
			case "rmshears": return "27569:*";
			case "rmhammer": return "27570:*";
			
			case "rmtools": return "27564-27570:*";
			
			case "rmkatar": 
			case "redkatar": 
			case "katar": return "27572:*";
			case "rmmorningstar":
			case "redmorningstar":
			case "morningstar": return "27573:*";
			
			case "destructioncatalyst": return "27527:*";
			case "hyperkineticlens": 
			case "hyperlens": return "27535:*";
			case "cataclycticlens": 
			case "catalyticlens": return "27556:*";
			
			case "evertideamulet": return "27530:*";
			case "volcaniteamulet": return "27531:*";
			
			case "amulets": return "27530-27531:*";
			
			case "zeroring": 
			case "freezering": 
			case "snowring": return "27574:*";
			case "ringofignition": 
			case "ignitionring": 
			case "firering": return "27533:*";
			
			case "ringofarcana": 
			case "arcanaring": return "27584:*";
			case "voidring": return "27593:*";
			case "harvestring": 
			case "harvestgodessband": return "27537:*";
			case "blackholeband": 
			case "bhb": return "27532:*";
			case "archangelsmite": 
			case "archangelssmite": 
			case "archangelring": 
			case "archangelsring": return "27534:*";
			case "swiftwolfsrendinggale": 
			case "swiftwolfrendinggale": 
			case "swiftwolfsring":
			case "swiftwolfring":
			case "SWRG": return "27536:*";
			
			case "rings": return "27532-27534:*;27536:*;27537:*;27574:*;27584:*;27593:*";
			
			case "philosopherstone":
			case "philosophersstone": return "27526:*";
			case "watchofflowingtime": 
			case "watchoftime": 
			case "watch": return "27538:*";
			case "mercurialeye": return "27583:*";
			
			case "dmchest": 
			case "dmchestplate": return "27549:*";
			case "dmhelmet": return "27550:*";
			case "dmleggings": 
			case "dmgreaves": return "27551:*";
			case "dmboots": return "27552:*";
			
			case "dmarmor":
			case "dmsuit": return "27549-27552:*";
			
			case "rmchest":
			case "rmchestplate": return "27575:*";
			case "rmhelmet": return "27576:*";
			case "rmleggings": 
			case "rmgreaves": return "27577:*";
			case "rmboots": return "27578:*";
			
			case "rmarmor":
			case "rmsuit": return "27575-27578:*";
			
			case "infernalarmor":
			case "gemchest":
			case "gemchestplate": return "27579:*";
			case "abysshelmet":
			case "gemhelmet": return "27580:*";
			case "gravitygreaves": 
			case "gemgreaves": 
			case "gemleggings": return "27581:*";
			case "hurricaneboots": 
			case "gemboots": return "27582:*";
			
			case "gemarmor":
			case "gemsuit": return "27579-27582:*";
			
			case "gemofeternaldensity": return "27553:*";
			case "repairtalisman": return "27554:*";
			
			case "soulstone": return "27529:*";
			case "bodystone": return "27588:*";
			case "lifestone": return "27589:*";
			case "mindstone": return "27590:*";
			
			case "diviningrod": return "27585:*";
			
			case "transmutationtablet": return "27592:*";
			
			case "kleinstarein": 
			case "kleinstar1": return "27557:*";
			case "kleinstarzwei": 
			case "kleinstar2": return "27558:*";
			case "kleinstardrei": 
			case "kleinstar3": return "27559:*";
			case "kleinstarvier": 
			case "kleinstar4": return "27560:*";
			case "kleinstarsphere": 
			case "kleinstar5": return "27561:*";
			case "kleinstaromega": 
			case "kleinstar6": return "27591:*";
			case "alchemybag": return "27562:*";
			
			default:
				return null;
		}
	}
	
	public static int getIdFromIC2Name(String name){
		name = name.toLowerCase();
		switch (name){
			case "quantumhelmet":
				return 30171;
			case "quantumchestplate":
			case "quantumchest":
			case "quantumbody":
			case "quantumbodyarmor":
				return 30172;
			case "quantumleggings":
			case "quantumpants":
			case "quantumlegs":
				return 30173;
			case "quantumboots":
			case "quantumshoes":
				return 30174;
				
			case "nanohelmet":
				return 30178;
			case "nanochestplate":
			case "nanochest":
			case "nanobody":
			case "nanobodyarmor":
				return 30177;
			case "nanoleggings":
			case "nanolegs":
			case "nanopants":
				return 30176;
			case "nanoboots":
			case "nanoshoes":
				return 30175;
				
			case "jetpack":
			case "electricjetpack":
				return 30209;
				
			case "batpack":
			case "batterypack":
				return 30180;
			case "lappack":
				return 30127;
				
			case "chainsaw":
				return 30233;
			case "miningdrill":
			case "drill":
				return 30235;
			case "ddrill":
			case "diamonddrill":
				return 30234;
				
			case "electrichoe":
				return 30119;
			case "electricwrench":
				return 30141;
			case "electrictreetap":
				return 30124;
				
			case "nanosaber":
				return 30148;
				
			case "mininglaser":
				return 30208;
				
			case "rebattery":
			case "re-battery":
				return 30242;
			case "energycrystal":
				return 30241;
			case "lapatronctrystal":
				return 30240;
				
			case "scanner":
			case "od-scanner":
			case "odscanner":
				return 30220;
			case "ov-scanner":
			case "ovscanner":
				return 30219;
				
			case "digitalthermometer":
				return 31257;
				
			default:
				return -1;
		}
	}
}
