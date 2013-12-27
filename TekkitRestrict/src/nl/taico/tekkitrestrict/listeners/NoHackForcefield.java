package nl.taico.tekkitrestrict.listeners;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.TRConfigCache.Hacks;
import nl.taico.tekkitrestrict.functions.TRNoHack;
import nl.taico.tekkitrestrict.objects.TREnums.HackType;

public class NoHackForcefield implements Listener {
	private static ConcurrentHashMap<String, Integer> tickTolerance = new ConcurrentHashMap<String, Integer>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	private void onEntityDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) return;
		//if (e.getCause() == DamageCause.PROJECTILE || e.getCause() == DamageCause.MAGIC || e.getCause() == DamageCause.BLOCK_EXPLOSION ||
		//	e.getCause() == DamageCause.POISON || e.getCause() == DamageCause.FIRE_TICK) return;
		
		if (e.getCause() != DamageCause.ENTITY_ATTACK && e.getCause() != DamageCause.CUSTOM) return;
		
		if (e.getEntity() == e.getDamager()) return;
		final Player damager = (Player) e.getDamager();
		
		if (damager.hasPermission("tekkitrestrict.bypass.hack.forcefield")) return;
		
		//Ignore rm sword and katar.
		if (damager.getItemInHand() != null && (damager.getItemInHand().getTypeId() == 27567 || damager.getItemInHand().getTypeId() == 27572)) return;
		
		if (e.getCause() == DamageCause.ENTITY_ATTACK){
			ItemStack[] inv = damager.getInventory().getContents();
			for (int i = 0; i<9; i++){
				ItemStack stack = inv[i];
				if (stack == null) continue;
				if (stack.getTypeId()==27534 && stack.getDurability()==1) return;//Archangelring damage
			}
		}
		final Location Attackerloc = damager.getLocation();
		
		final Location Damagedloc = e.getEntity().getLocation();
		
		Bukkit.getScheduler().scheduleAsyncDelayedTask(tekkitrestrict.getInstance(), new Runnable(){
			public void run(){
				// ok, to solve this, we are going to perform vector
				// projections // first, let's project the entities location
				// onto the player's // vector direction. // int _v_ =
				// Math.sqrt(()^2+()^2);
				// Vector eloc = new Vector(Eloc.getX(), 0, Eloc.getZ());
				//Damaged - Attacker = the distance between the attacker and the damaged.
				double pdir = Attackerloc.getYaw();
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
				//TRLogger.Log("debug", "Angle: " + angle);
				// change between 0 and 360.
				double cr1 = ((pdir + Hacks.forcefields.value) > 360) ? pdir + Hacks.forcefields.value - 360 : pdir + Hacks.forcefields.value;
				double cr2 = ((pdir - Hacks.forcefields.value) < 0) ? 360 - Math.abs(pdir - Hacks.forcefields.value) : pdir - Hacks.forcefields.value;
				// r1 = 360 r2 = 5
				// 360 + 30 = 390 - 360 = 30 < 5 false
				// r1 = 5 r2 = 360
				// 5 - 30 = 360-25 = 335 > 360 false

				String name = damager.getName();
				if (cr1 < angle || cr2 > angle) {
					Integer oldValue = tickTolerance.get(name);
					if (oldValue == null) tickTolerance.put(name, 1);//If not exist yet, make one
					else {//Otherwise, check if it exeeds the limit.
						if ((oldValue+1) > Hacks.forcefields.tolerance) {
							TRNoHack.handleHack(damager, HackType.forcefield);
							tickTolerance.remove(name);
						} else
							tickTolerance.put(name, oldValue + 1);
					}
				} else {
					Integer oldValue = tickTolerance.get(name);
					if (oldValue == null) return;
					
					if (oldValue < 2) tickTolerance.remove(name);
					else tickTolerance.put(name, oldValue - 1);
				}
			}
		});
		
		
	}

	// private static double min = 10000, max=-10000;
	public static void playerLogout(String playerName) {
		tickTolerance.remove(playerName);
	}

	public static void clearMaps() {
		tickTolerance.clear();
	}


}
