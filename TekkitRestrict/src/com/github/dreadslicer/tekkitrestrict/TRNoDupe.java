package com.github.dreadslicer.tekkitrestrict;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

public class TRNoDupe {
	public static void handleDropDupes(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		TRNoDupe_BagCache cache;
		if ((cache = TRNoDupe_BagCache.check(player)) != null) {
			if (cache.hasBHBInBag) {
				try {
					cache.expire();
					event.setCancelled(true);
					player.kickPlayer("[TRDupe] you have a " + cache.dupeItem + " in your [" + cache.inBagColor + "] Alchemy Bag!");

					Log.Dupe("a " + cache.inBagColor + " Alchemy Bag and a " + cache.dupeItem, "alc", player.getName());
				} catch (Exception ex) {
				}
			}
		}
	}
}
