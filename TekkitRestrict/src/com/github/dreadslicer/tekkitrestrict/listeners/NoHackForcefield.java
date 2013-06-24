package com.github.dreadslicer.tekkitrestrict.listeners;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Hacks;
import com.github.dreadslicer.tekkitrestrict.TRNoHack;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.HackType;
import com.github.dreadslicer.tekkitrestrict.Util;

public class NoHackForcefield implements Listener {
	private static ConcurrentHashMap<String, Integer> tickTolerance = new ConcurrentHashMap<String, Integer>();

	@EventHandler
	private void onEntityDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) return;

		Player damager = (Player) e.getDamager();

		if (Util.hasHackBypass(damager, "forcefield")) return;
		
		//Ignore rm sword and katar.
		if (damager.getItemInHand() != null && (damager.getItemInHand().getTypeId() == 27567 || damager.getItemInHand().getTypeId() == 27572)) return;

		double pdir = damager.getLocation().getYaw();
		Location Damagedloc = e.getEntity().getLocation();
		Location Attackerloc = damager.getLocation();
		// ok, to solve this, we are going to perform vector
		// projections // first, let's project the entities location
		// onto the player's // vector direction. // int _v_ =
		// Math.sqrt(()^2+()^2);
		// Vector eloc = new Vector(Eloc.getX(), 0, Eloc.getZ());
		//Damaged - Attacker = the distance between the attacker and the damaged.
		Vector ploc = new Vector(Damagedloc.getX() - Attackerloc.getX(), 0, Damagedloc.getZ() - Attackerloc.getZ());
		Vector xloc = new Vector(5, 0, 0);
		Vector zloc = new Vector(0, 0, 5);
		/*
		 * double x1 = 0, y1 = 0, z1 = 5; double x2 = ploc.getX(), y2 =
		 * 0, z2 = ploc.getZ(); double n1 =
		 * Math.sqrt(x1*x1+y1*y1+z1*z1), n2 =
		 * Math.sqrt(x2*x2+y2*y2+z2*z2); double angle =
		 * Math.acos((x1*x2+y1*y2+z1*z2)/(n1*n2)) * 180 / Math.PI;
		 */
		// angle = 360-angle+180;

		if (pdir < 0) {
			pdir = Math.abs(pdir + 360.00d);
		}

		double anglej = xloc.angle(ploc) * 180 / Math.PI;//The angle in degrees. If the angle is 180, that means the 
		double angle = zloc.angle(ploc) * 180 / Math.PI;

		// angle 0 -> 3.14-> 0
		// angle 0 -> 180 -> 0
		// pdir 0 -> 180 -> 360
		// if(pdir > 180) pdir = Math.abs(360-pdir);

		// 181 = 179
		if (anglej < 90) {
			angle = 360 - angle;
		}

		// tekkitrestrict.log.info("proj:  "+pdir+" / "+angle);

		// change between 0 and 360.
		double cr1 = ((pdir + Hacks.ffVangle) > 360) ? pdir + Hacks.ffVangle - 360 : pdir + Hacks.ffVangle;
		double cr2 = ((pdir - Hacks.ffVangle) < 0) ? 360 - Math.abs(pdir - Hacks.ffVangle) : pdir - Hacks.ffVangle;
		// r1 = 360 r2 = 5
		// 360 + 30 = 390 - 360 = 30 < 5 false
		// r1 = 5 r2 = 360
		// 5 - 30 = 360-25 = 335 > 360 false

		String name = damager.getName();
		if (cr1 < angle || cr2 > angle) {
			Integer oldValue = tickTolerance.get(name);
			if (oldValue == null) tickTolerance.put(name, 1);//If not exist yet, make one
			else {//Otherwise, check if it exeeds the limit.
				if ((oldValue+1) > Hacks.ffTolerance) {
					TRNoHack.handleHack(damager, HackType.forcefield);
					tickTolerance.remove(name);
				} else
					tickTolerance.put(name, oldValue + 1);
			}
		} else {
			tickTolerance.remove(name);
		}
	}

	// private static double min = 10000, max=-10000;
	public static void playerLogout(String playerName) {
		tickTolerance.remove(playerName);
	}

	public static void clearMaps() {
		tickTolerance.clear();
	}


}
