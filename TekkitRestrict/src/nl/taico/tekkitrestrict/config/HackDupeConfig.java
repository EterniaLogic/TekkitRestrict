package nl.taico.tekkitrestrict.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.tekkitrestrict;

public class HackDupeConfig extends TRConfig {
	public static ArrayList<String> defaultContents(boolean extra){
		ArrayList<String> tbr = new ArrayList<String>();
		
		tbr.add("##############################################################################################");
		tbr.add("## Configuration file for TekkitRestrict                                                    ##");
		tbr.add("## Authors: Taeir, DreadEnd (aka DreadSlicer)                                               ##");
		tbr.add("## BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/                            ##");
		tbr.add("## Please ask questions/report issues on the BukkitDev page.                                ##");
		tbr.add("##############################################################################################");
		tbr.add("");
		tbr.add("##############################################################################################");
		tbr.add("################################ Anti-Hack Configuration #####################################");
		tbr.add("##############################################################################################");
		tbr.add("# Block hackers from screwing your server up!");
		tbr.add("#");
		tbr.add("# Enabled:          Do you want to enable Anti-Hack for this kind of hack?");
		tbr.add("#                   Default: All true");
		tbr.add("#");
		tbr.add("# Tolerance:        The amount of ticks the player has to hack before he is kicked.");
		tbr.add("#                   If you set this too low, innocent people might get kicked for connection");
		tbr.add("#                   problems.");
		tbr.add("#                   Default:");
		tbr.add("#                       MoveSpeed: 30");
		tbr.add("#                       Fly: 40");
		tbr.add("#                       Forcefield: 20");
		tbr.add("#");
		tbr.add("# MaxMoveSpeed:     The maximum speed a player can have (in blocks per second).");
		tbr.add("#                   Speeds above this are considered hacking.");
		tbr.add("#                   People with quantum armor will have 3 times this limit.");
		tbr.add("#                   Default: 2.5");
		tbr.add("#");
		tbr.add("# MinHeight:        Minimal Height for the flycheck to kick in.");
		tbr.add("#                   If you set this too low, people might get kicked for jumping.");
		tbr.add("#                   Default: 3");
		tbr.add("#");
		tbr.add("# Angle:            The maximum angle you are allowed to hit a player with.");
		tbr.add("#                   Default: 40");
		tbr.add("#");
		tbr.add("# Broadcast:        Should a message be broadcast to all players with the ");
		tbr.add("#                   tekkitrestrict.notify.hack permission?");
		tbr.add("#                   Default: All true");
		tbr.add("#");
		tbr.add("# Kick:             Should a player get kicked if he hacks?");
		tbr.add("#                   Default: All true");
		tbr.add("#");
		tbr.add("# ExecuteCommand:");
		tbr.add("#    Enable:        Should a command be executed when someone hacks for a certain amount");
		tbr.add("#                   of times?");
		tbr.add("#                   Default: All true");
		tbr.add("#");
		tbr.add("#    Command:       The command to execute.");
		tbr.add("#                   Default: \"\"");
		tbr.add("#                   NOTE: The following will be replaced:");
		tbr.add("#                   {PLAYER} - The player's name");
		tbr.add("#                   {TYPE}   - The type of hack");
		tbr.add("#");
		tbr.add("#    TriggerAfter:  Set the amount of times the player has to hack before the command is");
		tbr.add("#                   executed. (Might implement save feature later. Currently only on");
		tbr.add("#                   the current server session.)");
		tbr.add("#                   Default: All 1");
		tbr.add("#");
		tbr.add("# BroadcastString:  The formatting of the BroadcastString.");
		tbr.add("#                   Default: \"&9{PLAYER} &ctried to &a{TYPE}&c-hack!\"");
		tbr.add("#                   NOTE: The following will be replaced:");
		tbr.add("#                   {PLAYER} - The player's name");
		tbr.add("#                   {TYPE}   - The type of hack");
		tbr.add("");
		tbr.add("Anti-Hacks:");
		tbr.add("    MoveSpeed:");
		tbr.add("        Enabled: true");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.MoveSpeed.Enabled HackSpeedEnabled");
		tbr.add("        Tolerance: 30");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.MoveSpeed.Tolerance HackMoveSpeedTolerance");
		tbr.add("        MaxMoveSpeed: 2.5");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.MoveSpeed.MaxMoveSpeed HackMoveSpeedMax");
		tbr.add("        Broadcast: true");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.MoveSpeed.Broadcast");
		tbr.add("        Kick: true");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.MoveSpeed.Kick");
		tbr.add("        ExecuteCommand:");
		tbr.add("            Enabled: false");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.MoveSpeed.ExecuteCommand.Enabled");
		tbr.add("            Command: \"\"");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.MoveSpeed.ExecuteCommand.Command");
		tbr.add("            TriggerAfter: 1");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.MoveSpeed.ExecuteCommand.TriggerAfter");
		tbr.add("    Fly:");
		tbr.add("        Enabled: true");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Fly.Enabled HackFlyEnabled");
		tbr.add("        Tolerance: 40");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Fly.Tolerance");
		tbr.add("        MinHeight: 3");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Fly.MinHeight");
		tbr.add("        Broadcast: true");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Fly.Broadcast");
		tbr.add("        Kick: true");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Fly.Kick");
		tbr.add("        ExecuteCommand:");
		tbr.add("            Enabled: false");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Fly.ExecuteCommand.Enabled");
		tbr.add("            Command: \"\"");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Fly.ExecuteCommand.Command");
		tbr.add("            TriggerAfter: 1");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Fly.ExecuteCommand.TriggerAfter");
		tbr.add("    Forcefield:");
		tbr.add("        Enabled: true");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Forcefield.Enabled HackForcefieldEnabled");
		tbr.add("        Tolerance: 20");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Forcefield.Tolerance");
		tbr.add("        Angle: 40");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Forcefield.Angle");
		tbr.add("        Broadcast: true");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Forcefield.Broadcast");
		tbr.add("        Kick: true");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Forcefield.Kick");
		tbr.add("        ExecuteCommand:");
		tbr.add("            Enabled: false");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Forcefield.ExecuteCommand.Enabled");
		tbr.add("            Command: \"\"");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Forcefield.ExecuteCommand.Command");
		tbr.add("            TriggerAfter: 1");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.Forcefield.ExecuteCommand.TriggerAfter");
		tbr.add("    BroadcastString: \"&9{PLAYER} &ctried to &a{TYPE}&c-hack!\"");
		if (extra) tbr.add("#:-;-:# Anti-Hacks.BroadcastString HackBroadcastString");
		tbr.add("");
		tbr.add("##############################################################################################");
		tbr.add("################################## Anti-Dupe Configuration ###################################");
		tbr.add("##############################################################################################");
		tbr.add("# Stop players from Duping!");
		tbr.add("# Prevent:          Do you want to prevent this dupe?");
		tbr.add("#                   Default: All true");
		tbr.add("#");
		tbr.add("# Broadcast:        Should a message be broadcast to all players with the ");
		tbr.add("#                   tekkitrestrict.notify.dupe permission?");
		tbr.add("#                   Default: All true");
		tbr.add("#");
		tbr.add("# Kick:             Should players that try to use this dupe be kicked?");
		tbr.add("#                   Default: All false");
		tbr.add("#                   NOTE: It is not recommended to kick players on attempting to dupe. In");
		tbr.add("#                         most cases it was not the players intention to dupe.");
		tbr.add("#");
		tbr.add("# ExecuteCommand:");
		tbr.add("#    Enable:        Should a command be executed when someone uses this dupe for a certain");
		tbr.add("#                   amount of times?");
		tbr.add("#                   Default: All true");
		tbr.add("#");
		tbr.add("#    Command:       The command to execute.");
		tbr.add("#                   Default: \"\"");
		tbr.add("#                   NOTE: The following will be replaced:");
		tbr.add("#                   {PLAYER} - The player's name");
		tbr.add("#                   {TYPE}   - The type of dupe");
		tbr.add("#                   {ID}     - The item ID");
		tbr.add("#                   {DATA}   - The item's damage value");
		tbr.add("#                   {ITEM}   - A string representation of {ID}:{DATA}");
		tbr.add("#");
		tbr.add("#    TriggerAfter:  Set the amount of times the player has to use this dupe before the ");
		tbr.add("#                   command is executed. (Might implement save feature later. Currently");
		tbr.add("#                   only on the current server session.)");
		tbr.add("#                   Default: All 1");
		tbr.add("#");
		tbr.add("# BroadcastString:  The formatting of the BroadcastString for dupes.");
		tbr.add("#                   Default: \"&9{PLAYER} &ctried to dupe&6 {ITEM} &cusing &a{TYPE}&c!\"");
		tbr.add("#                   NOTE: The following will be replaced:");
		tbr.add("#                   {PLAYER} - The player's name");
		tbr.add("#                   {TYPE}   - The type of dupe");
		tbr.add("#                   {ID}     - The item ID");
		tbr.add("#                   {DATA}   - The item's damage value");
		tbr.add("#                   {ITEM}   - A string representation of {ID}:{DATA}");
		tbr.add("");
		tbr.add("Anti-Dupes:");
		tbr.add("    AlchemyBagDupe:");
		tbr.add("        Prevent: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PreventAlchemyBagDupe PreventAlcDupe");
		tbr.add("        Broadcast: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.AlchemyBagDupe.Broadcast");
		tbr.add("        Kick: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.AlchemyBagDupe.Kick");
		tbr.add("        ExecuteCommand:");
		tbr.add("            Enabled: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.AlchemyBagDupe.ExecuteCommand.Enabled");
		tbr.add("            Command: \"\"");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.AlchemyBagDupe.ExecuteCommand.Command");
		tbr.add("            TriggerAfter: 1");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.AlchemyBagDupe.ExecuteCommand.TriggerAfter");
		tbr.add("    RMFurnaceDupe:");
		tbr.add("        Prevent: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PreventRMFurnaceDupe PreventRMFurnaceDupe");
		tbr.add("        Broadcast: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.RMFurnaceDupe.Broadcast");
		tbr.add("        Kick: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.RMFurnaceDupe.Kick");
		tbr.add("        ExecuteCommand:");
		tbr.add("            Enabled: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.RMFurnaceDupe.ExecuteCommand.Enabled");
		tbr.add("            Command: \"\"");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.RMFurnaceDupe.ExecuteCommand.Command");
		tbr.add("            TriggerAfter: 1");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.RMFurnaceDupe.ExecuteCommand.TriggerAfter");
		tbr.add("    TransmuteDupe:");
		tbr.add("        Prevent: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PreventTransmuteDupe PreventTransmuteDupe");
		tbr.add("        Broadcast: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TransmuteDupe.Broadcast");
		tbr.add("        Kick: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TransmuteDupe.Kick");
		tbr.add("        ExecuteCommand:");
		tbr.add("            Enabled: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TransmuteDupe.ExecuteCommand.Enabled");
		tbr.add("            Command: \"\"");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TransmuteDupe.ExecuteCommand.Command");
		tbr.add("            TriggerAfter: 1");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TransmuteDupe.ExecuteCommand.TriggerAfter");
		tbr.add("    TankCartDupe:");
		tbr.add("        Prevent: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PreventTankCartDupe PreventTankCartDupe");
		tbr.add("        Broadcast: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartDupe.Broadcast");
		tbr.add("        Kick: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartDupe.Kick");
		tbr.add("        ExecuteCommand:");
		tbr.add("            Enabled: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartDupe.ExecuteCommand.Enabled");
		tbr.add("            Command: \"\"");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartDupe.ExecuteCommand.Command");
		tbr.add("            TriggerAfter: 1");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartDupe.ExecuteCommand.TriggerAfter");
		tbr.add("    TankCartGlitch:");
		tbr.add("        Prevent: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PreventTankCartGlitch PreventTankCartGlitch");
		tbr.add("        Broadcast: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartGlitch.Broadcast");
		tbr.add("        Kick: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartGlitch.Kick");
		tbr.add("        ExecuteCommand:");
		tbr.add("            Enabled: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartGlitch.ExecuteCommand.Enabled");
		tbr.add("            Command: \"\"");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartGlitch.ExecuteCommand.Command");
		tbr.add("            TriggerAfter: 1");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.TankCartGlitch.ExecuteCommand.TriggerAfter");
		tbr.add("    PedestalEmcGen:");
		tbr.add("        Prevent: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PreventPedestalEmcGen PreventPedestalEmcGen");
		tbr.add("        Broadcast: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PedestalEmcGen.Broadcast");
		tbr.add("        Kick: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PedestalEmcGen.Kick");
		tbr.add("        ExecuteCommand:");
		tbr.add("            Enabled: false");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PedestalEmcGen.ExecuteCommand.Enabled");
		tbr.add("            Command: \"\"");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PedestalEmcGen.ExecuteCommand.Command");
		tbr.add("            TriggerAfter: 1");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PedestalEmcGen.ExecuteCommand.TriggerAfter");
		tbr.add("    TeleportDupe:");
		tbr.add("        Prevent: true");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.PreventTeleportDupe Dupes.PreventTeleportDupe");
		tbr.add("    BroadcastString: \"&9{PLAYER} &ctried to dupe&6 {ITEM} &cusing &a{TYPE}&c!\"");
		if (extra) tbr.add("#:-;-:# Anti-Dupes.BroadcastString Dupes.BroadcastString");
		tbr.add("");
		tbr.add("##############################################################################################");
		
		return tbr;
	}
	
	public static void upgradeOldHackFile(){
		upgradeOldHackFile(convertDefaults2(defaultContents(true), true));
	}
	
	public static void upgradeFile(){
		upgradeFile("HackDupe", convertDefaults2(defaultContents(true), false));
	}
	
	@SuppressWarnings("unchecked")
	private static ArrayList<String> convertDefaults2(ArrayList<String> defaults, boolean fromold){
		int j = defaults.size();
		for (int i = 0;i<j;i++){
			String str = defaults.get(i);
			if (str.contains("#:-;-:#")){
				str = str.replace("#:-;-:# ", "");
				String oldname = null;
				if (str.contains(" ")){
					oldname = str.split(" ")[1];
					str = str.split(" ")[0];
				}
				Object obj = tekkitrestrict.config.get(str, null);
				if (fromold && obj == null && oldname != null){
					obj = tekkitrestrict.config.get(oldname, null);
				}
				if (obj == null){
					defaults.remove(i);
					i--; j--;
					continue;
				}
				
				if (obj instanceof String){
					String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": \""+obj.toString()+"\"");
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof Integer){
					String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": "+toInt(obj));
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof Double){
					String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": "+toDouble(obj));
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof Boolean){
					String str2 = defaults.get(i-1);//Method: "1"
					defaults.set(i-1, str2.split(":")[0] + ": "+((Boolean) obj).toString());
					defaults.remove(i);//Remove posString
					i--; j--;
				} else if (obj instanceof List){
					List<Object> l = (List<Object>) obj;
					
					String str2 = defaults.get(i-1);//Method: "1"
					String toadd = "";
					for (Object o : l){
						if (isPrimitive(o) || o instanceof String){
							toadd += "\""+o.toString()+"\", ";
						} else {
							tekkitrestrict.log.severe("Error in Upgrader: invalid config entry, not Primitive or String");
							continue;
						}
					}
					if (!toadd.equals("")){
						defaults.set(i-1, str2.split(":")[0] + ": [" + toadd.substring(0, toadd.length()-2) + "]");
					} else {
						defaults.set(i-1, str2.split(":")[0] + ": []");
					}
					
					defaults.remove(i);//Remove posString, cursor is at first element of list
					
					i--; j--;
				} else {
					tekkitrestrict.log.severe("Error in Upgrader: invalid config entry, obj is unknown object! Class: " + obj.getClass().getName());
					defaults.remove(i);
					i--; j--;
					continue;
				}
			}
		}
		return defaults;
	}
	
	private static void upgradeOldHackFile(ArrayList<String> content){
		tekkitrestrict.log.info("Upgrading Hack.config.yml file.");
		File configFile = new File("plugins"+s+"tekkitrestrict"+s+"Hack.config.yml");
		if (configFile.exists()){
			File backupfile = new File("plugins"+s+"tekkitrestrict"+s+"Hack.config_backup.yml");
			if (backupfile.exists()) backupfile.delete();
			configFile.renameTo(backupfile);
			configFile = new File("plugins"+s+"tekkitrestrict"+s+"HackDupe.config.yml");
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				Log.Warning.load("Unable to create file HackDupe.config.yml!");
				return;
			}
		}
		
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(configFile));
			for (int i = 0;i<content.size();i++){
				if (i != 0) output.newLine();
				output.append(content.get(i));
			}
			output.close();
		} catch (IOException e) {
			tekkitrestrict.loadWarning("Unable to write changes to HackDupe.config.yml!");
			try {if (output != null) output.close();} catch (IOException e1) {}
			return;
		}
		tekkitrestrict.log.info("HackDupe.config.yml file was upgraded successfully!");
		Log.Warning.loadWarnings.add("HackDupe.config.yml file was upgraded! Please check the new/changed config settings!");
	}
}
