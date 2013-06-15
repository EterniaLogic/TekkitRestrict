package com.github.dreadslicer.tekkitrestrict;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.server.EntityHuman;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Dupes;

import ee.AlchemyBagData;
import ee.ItemAlchemyBag;

public class TRNoDupe_BagCache {
	public Player player;
	public boolean hasBHBInBag = false;
	public String inBagColor = "";
	public String dupeItem = "";
	public static Map<Player, TRNoDupe_BagCache> watchers = Collections.synchronizedMap(new LinkedHashMap<Player, TRNoDupe_BagCache>());

	public Object[] hasBHB() {
		// returns whether the player has a BHB in one of their bags
		return new Object[] { hasBHBInBag, inBagColor };
	}

	public boolean isOnline() {
		return player == null ? false : player.isOnline();
	}

	public void removeAlc() {
		// removes all "Devices" form alc bag.
		if (!Dupes.alcBag) return;
		
		if (!tekkitrestrict.EEEnabled || Util.hasBypass(player, "dupe", "alcbag")) return;
		for (int i = 0; i < 16; i++) {
			try {
				EntityHuman H = ((CraftPlayer) player).getHandle();
				AlchemyBagData ABD = ItemAlchemyBag.getBagData(i, H, H.world);
				// ok, now we search!
				net.minecraft.server.ItemStack[] iss = ABD.items;
				// TRLogger.Log("debug",
				// "info: TTAlc slot "+iss.length);
				for (int j = 0; j < iss.length; j++) {
					if (iss[j] == null) continue;
					if (iss[j].id == 27532 || iss[j].id == 27593)
						iss[j] = null;
				}
				ABD.items = iss;
			} catch (Exception ex) {
				// This alc bag does not exist
			}
		}
	}
	
	public static TRNoDupe_BagCache check(Player p) {
		if (!Dupes.alcBag) return null;
		TRNoDupe_BagCache cc = watchers.get(p);
		if (cc == null || cc.player == null) return null;
		if (cc.hasBHBInBag && cc.isOnline()) {
			return cc;
		}

		return null;
	}

	public void expire(){
		watchers.remove(this);
	}
	
	/**
	 * removes the cache from the list.<br>
	 * this removes some other errors.
	 */
	public static void expire(TRNoDupe_BagCache cache) {
		watchers.remove(cache);
	}
}
