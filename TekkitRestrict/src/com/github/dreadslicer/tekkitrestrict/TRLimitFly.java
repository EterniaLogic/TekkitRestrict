package com.github.dreadslicer.tekkitrestrict;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class TRLimitFly {
	private static Map<Player, Integer> playerTimes = new java.util.concurrent.ConcurrentHashMap<Player, Integer>();
	private static List<Player> isFlying = Collections
			.synchronizedList(new LinkedList<Player>());
	private static Thread c = null;
	private static int groundTime = 99999999;

	public static void setFly(Player p) {
		if (playerTimes.get(p) == null) {
			playerTimes.put(p, 0);
		}
		if (!isFlying.contains(p)) {
			isFlying.add(p);
		}// tekkitrestrict.log.info("addfly");}
			// tekkitrestrict.log.info(p.getName()+" flew for "+playerTimes.get(p));
	}

	public static void setGrounded(Player p) {
		if (isFlying.contains(p)) {
			isFlying.remove(p);
			// tekkitrestrict.log.info(p.getName()+" was grounded");
		}
	}

	private static int reset = 0;

	public static void init() {
		// initialize a runnable that ticks every 1 minute
		c = new Thread(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("TRFlyLimit_Thread");
				while (!tekkitrestrict.disable) {
					try {
						Thread.sleep(1000 * 60);
					} catch (InterruptedException e) {
					}
					// tekkitrestrict.log.info("flytick");
					for (int i = 0; i < isFlying.size(); i++) {
						Player cc = isFlying.get(i);
						if (cc == null ? true : !cc.isOnline()) {
							isFlying.remove(cc);// tekkitrestrict.log.info("remfly");
							i--;
						} else {
							boolean isNull = playerTimes.get(cc) == null;
							if (!isNull) {
								int current = playerTimes.get(cc);
								playerTimes.put(cc, current + 1);
								isFlying.remove(cc);// tekkitrestrict.log.info("flyupp");
							} else {
								playerTimes.remove(cc);
							}
						}
					}
					reset++; // will be 1 minute over 24 hours.
					if (reset >= (60 * 24)) { // 1 hour * 24 = 24 hours
						// tekkitrestrict.log.info("flyreset");
						playerTimes.clear();
						reset = 0;
					}
				}
			}
		});
		c.start();
	}

	public static void willGround(Player p) {
		if (!TRPermHandler.hasPermission(p, "flylimit", "bypass", "")) {
			for (int i = 0; i < isFlying.size(); i++) {
				Player cc = isFlying.get(i);
				boolean isNull = playerTimes.get(cc) == null;
				if (!isNull) {
					int current = playerTimes.get(cc);
					if (current >= groundTime) {
						// tekkitrestrict.log.info("limitFly");
						TRNoHack.groundPlayer(cc);
						cc.sendMessage("You have used up your flight time for today! ("
								+ current + " Minutes)");
						cc.sendMessage("Please turn off your flight device.");
					}
				}
			}
		}
	}

	public static void reload() {
		groundTime = tekkitrestrict.config.getInt("FlyLimitDailyMinutes");
	}
}
