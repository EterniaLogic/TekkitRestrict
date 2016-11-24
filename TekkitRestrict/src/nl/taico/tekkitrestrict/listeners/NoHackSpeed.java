package nl.taico.tekkitrestrict.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import nl.taico.tekkitrestrict.TRConfigCache.Hacks;
import nl.taico.tekkitrestrict.functions.TRNoHack;
import nl.taico.tekkitrestrict.objects.TREnums.HackType;

public class NoHackSpeed implements Listener{
	private static HashMap<String, Double[]> tickLastLoc = new HashMap<String, Double[]>();
	private static HashMap<String, Integer> tickTolerance = new HashMap<String, Integer>();
	private static final int maxmove = (15 + 1)*(15 + 1); // above this value, players will be kicked anyways.
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void handleMove(PlayerMoveEvent e) {
		final Player player = e.getPlayer();
		if (player == null || player.hasPermission("tekkitrestrict.bypass.hack.speed")) return;
		
		// determine position of player to get velocity.
		final Location loc = player.getLocation();
		final String name = player.getName();
		final double xNew = loc.getX(), zNew = loc.getZ();
		
		// determine XZ velocity
		
		Double[] XZ = tickLastLoc.get(name);
		if (XZ != null) {
			final double xOld = XZ[0], zOld = XZ[1];
			
			final double xe = (xOld - xNew);
			final double ze = (zOld - zNew);
			final double velo = xe*xe+ze*ze;//Removed sqrt to save cpu
			
			//max 0.8 when running and jumping
			//System.out.println(velo);
			//max 0.6 when running and jumping on ice
			//max 1.0 when running and jumping on ice, the speedy way
			if (velo >= Hacks.speed.value && velo <= maxmove) {
				Integer oldValue = tickTolerance.get(name);
				if (oldValue == null) tickTolerance.put(name, 1);
				else {
					if ((oldValue + 1) > Hacks.speed.tolerance) {
						TRNoHack.handleHack(player, HackType.speed);
						
						tickTolerance.remove(name);
						
					} else {
						tickTolerance.put(name, oldValue + 1);
					}
				}
			} else {
				tickTolerance.remove(name);
			}
		}
		tickLastLoc.put(name, new Double[] { xNew, zNew });
		
	}

	public static void clearMaps() {
		// flushes the fly locator map.
		tickLastLoc = new HashMap<String, Double[]>();
		tickTolerance = new HashMap<String, Integer>();
	}

	public static void playerLogout(String playerName) {
		tickLastLoc.remove(playerName);
		tickTolerance.remove(playerName);
	}
}
