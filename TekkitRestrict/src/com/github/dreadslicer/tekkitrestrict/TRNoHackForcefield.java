package com.github.dreadslicer.tekkitrestrict;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.github.dreadslicer.tekkitrestrict.TRNoHack.HackType;

public class TRNoHackForcefield {
	private static Map<Player, Integer> tickTolerance = new ConcurrentHashMap<Player, Integer>();
	private static boolean enabled = true;
	private static double tolerance = 30, vangle = 30;

	public static void reload() {
		tolerance = tekkitrestrict.config.getDouble("HackForcefieldTolerance");
		vangle = tekkitrestrict.config.getDouble("HackForcefieldAngle");
		enabled = tekkitrestrict.config.getBoolean("UseAntiForcefield");
	}

	public static void checkForcefield(EntityDamageByEntityEvent e) {
		// check to determine if the player is even looking at the entity.
		if (e.getDamager().getType() == EntityType.PLAYER && enabled) {
			Player p = tekkitrestrict
					.getInstance()
					.getServer()
					.getPlayer(
							((org.bukkit.entity.HumanEntity) e.getDamager())
									.getName());
			if (!TRPermHandler.hasPermission(p, "hack", "bypass", "")) {
				double pdir = e.getDamager().getLocation().getYaw();
				Location Ploc = e.getEntity().getLocation();
				Location Eloc = e.getDamager().getLocation();

				/*
				 * if (!p.hasLineOfSight(e.getEntity())) {
				 * tekkitrestrict.log.info("hack?"); }
				 */
				// * // ok, to solve this, we are going to perform vector
				// projections // first, let's project the entities location
				// onto the player's // vector direction. // int _v_ =
				// Math.sqrt(()^2+()^2);
				// Vector eloc = new Vector(Eloc.getX(), 0, Eloc.getZ());
				Vector ploc = new Vector(Ploc.getX() - Eloc.getX(), 0,
						Ploc.getZ() - Eloc.getZ());
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

				double anglej = xloc.angle(ploc) * 180 / Math.PI;
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
				double cr1 = ((pdir + vangle) > 360) ? pdir + vangle - 360
						: pdir + vangle;
				double cr2 = ((pdir - vangle) < 0) ? 360 - Math.abs(pdir
						- vangle) : pdir - vangle;
				// r1 = 360 r2 = 5
				// 360 + 30 = 390 - 360 = 30 < 5 false
				// r1 = 5 r2 = 360
				// 5 - 30 = 360-25 = 335 > 360 false

				if (cr1 < angle || cr2 > angle) {
					if (tickTolerance.get(p) != null) {
						tickTolerance.put(p, tickTolerance.get(p) + 1);
						if (tickTolerance.get(p) > tolerance) {
							// tekkitrestrict.log.info("hack?");
							TRNoHack.handleHack(p, HackType.forcefield);
							tickTolerance.remove(p);
						}
					} else {
						tickTolerance.put(p, 1);
					}
				} else {
					tickTolerance.remove(p);
				}
			}
		}
	}

	// private static double min = 10000, max=-10000;
	public static void playerLogout(Player player) {
		if (tickTolerance.containsKey(player)) {
			tickTolerance.remove(player);
		}
	}

	public static void clearMaps() {
		tickTolerance.clear();
	}

	/*
	 * 22:33:27 [INFO] [tekkitrestrict] current: -0.345,-0.291,0.892 22:33:27
	 * [INFO] [tekkitrestrict] expected: 0.663,0.4121,0.624 22:33:27 [INFO]
	 * [tekkitrestrict] current:
	 * -0.34514123377305916,-0.29153691368992035,0.8921231735056356 22:33:27
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6771991775797204,0.39503285358817025,0.6207659127814105 22:33:28 [INFO]
	 * [tekkitrestrict] current:
	 * -0.05879900617931878,-0.3510245113391763,0.9345183087085115 22:33:28
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6771991775797204,0.39503285358817025,0.6207659127814104 22:33:28 [INFO]
	 * [tekkitrestrict] current:
	 * -0.036469343819100455,-0.37055725199211925,0.9280933735123064 22:33:28
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6771991775797205,0.39503285358817025,0.6207659127814104 22:33:28 [INFO]
	 * [tekkitrestrict] current:
	 * -0.036469343819100455,-0.37055725199211925,0.9280933735123064 22:33:28
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6771991775797204,0.3950328535881702,0.6207659127814104 22:33:28 [INFO]
	 * [tekkitrestrict] current:
	 * -0.038939355336622905,-0.36812437316419055,0.928960802449942 22:33:28
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6745653615682172,0.3934964609147934,0.6245975570076086 22:33:28 [INFO]
	 * [tekkitrestrict] current:
	 * -0.041371133861522386,-0.36812437316419055,0.9288556804829713 22:33:28
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6745653615682172,0.3934964609147934,0.6245975570076086 22:33:28 [INFO]
	 * [tekkitrestrict] current:
	 * -0.05330058579756024,-0.37784058339470045,0.9243351887132663 22:33:28
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6719360280989533,0.3919626830577227,0.6283846188703174 22:33:28 [INFO]
	 * [tekkitrestrict] current:
	 * -0.07022932629612419,-0.37784058339470045,0.923203301157413 22:33:28
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6719360280989533,0.39196268305772275,0.6283846188703174 22:33:28 [INFO]
	 * [tekkitrestrict] current:
	 * -0.08507852571446237,-0.36812437316419055,0.925875850395033 22:33:28
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6719360280989533,0.39196268305772275,0.6283846188703175 22:33:28 [INFO]
	 * [tekkitrestrict] current:
	 * -0.11260398449031077,-0.3485719162748332,0.9304933970004282 22:33:28
	 * [INFO] [tekkitrestrict] expected:
	 * 0.665869065641603,0.39205374892916817,0.6347536887424627 22:33:29 [INFO]
	 * [tekkitrestrict] current:
	 * -0.11260398449031077,-0.3485719162748332,0.9304933970004282 22:33:29
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6658690656416031,0.39205374892916817,0.6347536887424627 22:33:29 [INFO]
	 * [tekkitrestrict] current:
	 * -0.11260398449031077,-0.3485719162748332,0.9304933970004282 22:33:30
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6685019691102211,0.39360396312097123,0.6310158773844142 22:33:30 [INFO]
	 * [tekkitrestrict] current:
	 * -0.11260398449031077,-0.3485719162748332,0.9304933970004282 22:33:30
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6685019691102211,0.3936039631209713,0.6310158773844142 22:33:30 [INFO]
	 * [tekkitrestrict] current:
	 * -0.11260398449031077,-0.3485719162748332,0.9304933970004282 22:33:30
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6658690656416031,0.39205374892916817,0.6347536887424626 22:33:30 [INFO]
	 * [tekkitrestrict] current:
	 * -0.11260398449031077,-0.3485719162748332,0.9304933970004282 22:33:30
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6658690656416031,0.39205374892916817,0.6347536887424627 22:33:30 [INFO]
	 * [tekkitrestrict] current:
	 * -0.895865027089041,-0.18995210848451718,0.4016765486321528 22:33:30
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6596589600209392,0.4139036611896089,0.627322736490501 22:33:31 [INFO]
	 * [tekkitrestrict] current:
	 * -0.895865027089041,-0.18995210848451718,0.4016765486321528 22:33:31
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6495698024616309,0.4157246735754438,0.6365784064123983 22:33:31 [INFO]
	 * [tekkitrestrict] current:
	 * -0.895865027089041,-0.18995210848451718,0.4016765486321528 22:33:31
	 * [INFO] [tekkitrestrict] expected:
	 * 0.671139948120374,0.3951571657157342,0.6272335963741813 22:33:31 [INFO]
	 * [tekkitrestrict] current:
	 * -0.895865027089041,-0.18995210848451718,0.4016765486321528 22:33:31
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6495698024616309,0.41572467357544374,0.6365784064123982 22:33:31 [INFO]
	 * [tekkitrestrict] current:
	 * -0.895865027089041,-0.18995210848451718,0.4016765486321528 22:33:31
	 * [INFO] [tekkitrestrict] expected:
	 * 0.668501969110221,0.3936039631209712,0.6310158773844142 22:33:31 [INFO]
	 * [tekkitrestrict] current:
	 * -0.9010458520491446,-0.18995210848451718,0.3899161050627653 22:33:31
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6685019691102211,0.3936039631209713,0.6310158773844142 22:33:31 [INFO]
	 * [tekkitrestrict] current:
	 * -0.8701548718377892,-0.20278711280854278,0.4491190108375911 22:33:31
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6685019691102211,0.3936039631209713,0.6310158773844143 22:33:32 [INFO]
	 * [tekkitrestrict] current:
	 * -0.09025673707927769,-0.16418667909633455,-0.9822914312051793 22:33:32
	 * [INFO] [tekkitrestrict] expected:
	 * 0.671139948120374,0.3951571657157343,0.6272335963741814 22:33:32 [INFO]
	 * [tekkitrestrict] current:
	 * -0.09286860828852624,-0.16160366012881958,-0.9824762992701256 22:33:32
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6719360280989533,0.3919626830577227,0.6283846188703174 22:33:32 [INFO]
	 * [tekkitrestrict] current:
	 * -0.0776121613341117,-0.146082890246352,-0.9862231702765456 22:33:32
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6719360280989531,0.3919626830577227,0.6283846188703174 22:33:32 [INFO]
	 * [tekkitrestrict] current:
	 * -0.0776121613341117,-0.146082890246352,-0.9862231702765456 22:33:32
	 * [INFO] [tekkitrestrict] expected:
	 * 0.6719360280989531,0.39196268305772264,0.6283846188703174 22:33:33 [INFO]
	 * [tekkitrestrict] current:
	 * -0.0776121613341117,-0.146082890246352,-0.9862231702765456 22:33:33
	 * [INFO] [tekkitrestrict] expected:
	 * 0.671139948120374,0.3951571657157342,0.6272335963741813
	 */
}
