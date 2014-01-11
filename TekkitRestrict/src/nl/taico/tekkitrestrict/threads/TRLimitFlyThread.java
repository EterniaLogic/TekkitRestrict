package nl.taico.tekkitrestrict.threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.functions.TRNoHack;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

public class TRLimitFlyThread extends Thread {
	private int reset = 0;
	private List<Player> isFlying = Collections.synchronizedList(new ArrayList<Player>());
	private ConcurrentHashMap<Player, Integer> playerTimes = new ConcurrentHashMap<Player, Integer>();
	private int groundTime = 99999999;
	
	@SuppressWarnings("unused")
	@Override
	public void run(){
		if (true) return;
		load();
		while (true){
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
	
	public void setFly(Player player) {
		if (!isFlying.contains(player)) isFlying.add(player);
	}
	
	public void setGrounded(Player player) {
		isFlying.remove(player);
	}
	
	@SuppressWarnings("unused")
	private void willGround(Player player) {
		if (player.hasPermission("tekkitrestrict.bypass.flylimit")) return;
		Integer time = playerTimes.get(player);
		if (time == null) return;
		
		if (time >= groundTime) {
			TRNoHack.groundPlayer(player);
			player.sendMessage("You have used up your flight time for today! (" + time + " Minutes)");
			player.sendMessage("Please turn off your flight device.");
		}
	}
	
	private void load(){ reload(); }
	public void reload() {
		groundTime = tekkitrestrict.config.getInt(ConfigFile.ModModifications, "FlyLimitDailyMinutes", 999999);
	}
}
