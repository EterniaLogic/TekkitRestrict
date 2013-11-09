package nl.taico.tekkitrestrict;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.taico.tekkitrestrict.TRConfigCache.Global;

public class Util {
	public static void kick(Player player, String message){
		if (Global.kickFromConsole)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + player.getName() + " " + message);
		else
			player.kickPlayer(message);
	}
	
	public static String inGroup(int id){
		if (inRange(id, 27520, 27599) || inRange(id, 126, 130)) return "ee";
		if (inRange(id, 153, 174) || inRange(id, 4056, 4066) || inRange(id, 4298, 4324)) return "buildcraft";
		if (inRange(id, 4299, 4305) || id == 179) return "additionalpipes";
		if (inRange(id, 219, 223) || inRange(id, 225, 250) || inRange(id, 30171, 30256)) return "industrialcraft";
		if (id == 192 || inRange(id, 31256, 31260)) return "nuclearcontrol";
		if (id == 190) return "powerconverters";
		if (id == 183) return "compactsolars";
		if (id == 187) return "chargingbench";
		if (inRange(id, 253, 254) || inRange(id, 188, 191)) return "advancedmachines";
		if (id == 136) return "redpowercore";
		if (id == 138 || inRange(id, 1258, 1328)) return "redpowerlogic";
		if (inRange(id, 133, 134) || id == 148) return "redpowercontrol";
		if (id == 137 || inRange(id, 150, 151)) return "redpowermachine";
		if (id == 147) return "redpowerlighting";
		if (id == 177 || inRange(id, 6358, 6363) || id == 6406 || inRange(id, 6408, 6412)) return "wirelessredstone";
		if (inRange(id, 253, 254) || inRange(id, 11366, 11374)) return "mffs";
		if (inRange(id, 206, 215) || inRange(id, 7256, 7316)) return "railcraft";
		if (id == 194) return "tubestuffs";
		if (inRange(id, 19727, 19762) || id == 181) return "ironchests";
		if (inRange(id, 26483, 26530)) return "balkonweaponmod";
		if (id == 178 || id == 7493) return "enderchest";
		if (id == 4095 || id == 214 || id == 7303 || id == 179) return "chunkloaders";
		return null;
	}
	
	public static boolean inRange(int id, int min, int max){
		if (min > 10000) return (id > min && id < max);
		return (id < max && id > min);
	}
}
