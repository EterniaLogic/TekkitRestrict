package com.github.dreadslicer.tekkitrestrict;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class TRLimitFly {
	private static ConcurrentHashMap<Player, Integer> playerTimes = new ConcurrentHashMap<Player, Integer>();
	private static List<Player> isFlying = Collections.synchronizedList(new LinkedList<Player>());
	private static Thread c = null;
	private static int groundTime = 99999999;
	private static int reset = 0;

	public static void setFly(Player player) {
		if (!isFlying.contains(player)) isFlying.add(player);
	}

	public static void setGrounded(Player player) {
		isFlying.remove(player);
	}
	
	public static void stop(){
		if (c == null) return;
		c.interrupt();
	}

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
						if (tekkitrestrict.disable) break;
					}
					// tekkitrestrict.log.info("flytick");
					for (Player player : isFlying){
						if (player == null) continue;
						Integer time = playerTimes.get(player);
						
						if (time == null) time = 1;
						else time = time + 1;
						
						playerTimes.put(player, time);
					}

					reset++; // will be 1 minute over 24 hours.
					if (reset >= (60 * 24)) { // 1 hour * 24 = 24 hours
						playerTimes.clear();
						reset = 0;
					}
				}
			}
		});
		c.start();
	}

	@SuppressWarnings("unused")
	private static void willGround(Player player) {
		if (Util.hasBypass(player, "flylimit")) return;
		Integer time = playerTimes.get(player);
		if (time == null) return;
		
		if (time >= groundTime) {
			TRNoHack.groundPlayer(player);
			player.sendMessage("You have used up your flight time for today! (" + time + " Minutes)");
			player.sendMessage("Please turn off your flight device.");
		}
	}

	public static void reload() {
		groundTime = tekkitrestrict.config.getInt("FlyLimitDailyMinutes");
	}
}
