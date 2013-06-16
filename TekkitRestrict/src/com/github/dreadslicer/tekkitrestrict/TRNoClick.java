package com.github.dreadslicer.tekkitrestrict;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class TRNoClick {
	//TODO possibly change this to my method
	public int id, data;
	public boolean air = true, block = true, usesafezone = false, useB = false, insafezone = false;
	public String clicktype, msg = ""; // left / right

	public boolean compare(Player player, Block bl, ItemStack iss, Action e) {
		boolean r = false;
		// tekkitrestrict.log.info("action: "+e.toString());
		if (this.useB) {
			if (bl != null) { // on a block????
				if (TRNoItem.equalSet(id, data, bl.getTypeId(), bl.getData())) { // correct id???
					if (e == Action.RIGHT_CLICK_BLOCK || e == Action.RIGHT_CLICK_AIR) { // right click???
						return true;
					}
				}
			}
		} else if (TRNoItem.equalSet(id, data, iss.getTypeId(), iss.getData().getData())) {

			insafezone = usesafezone ? TRSafeZone.inSafeZone(player) : true;

			if (insafezone) {
				// tekkitrestrict.log.info("l2");
				if (clicktype.equals("both")) {
					return true;
				} else if (clicktype.equals("left")) {
					if (e == Action.LEFT_CLICK_AIR && air) return true;
					else if (e == Action.LEFT_CLICK_BLOCK && block) return true;
				} else if (clicktype.equals("right")) {
					if (e == Action.RIGHT_CLICK_AIR && air) return true;
					else if (e == Action.RIGHT_CLICK_BLOCK && block) return true;
				}
			}
		}

		return r;
	}

	private static List<TRNoClick> disableClickItemActions = Collections.synchronizedList(new LinkedList<TRNoClick>());
	private static List<String> DisableClicks = Collections.synchronizedList(new LinkedList<String>());

	public static void reload() {
		DisableClicks = Collections.synchronizedList(tekkitrestrict.config.getStringList("DisableClick"));
		disableClickItemActions.clear();
		for (int i = 0; i < DisableClicks.size(); i++) {
			String clickstring = DisableClicks.get(i);

			// tekkitrestrict.log.info("c-"+clickstring);
			if (clickstring.contains(" ")) {
				String[] token = clickstring.split(" ");
				if (token[0].equals("block")) {
					List<TRCacheItem> iss = TRCacheItem.processItemString("", token[1], -1);
					for (TRCacheItem ti : iss) {
						TRNoClick cia = new TRNoClick();
						cia.id = ti.id;
						cia.data = ti.getData();
						cia.msg = "You may not interact with this block.";
						cia.useB = true;
						disableClickItemActions.add(cia);
					}
				} else {
					List<TRCacheItem> iss = TRCacheItem.processItemString("", token[0], -1);
					for (TRCacheItem ti : iss) {
						TRNoClick cia = new TRNoClick();
						cia.id = ti.id;
						cia.data = ti.getData();
						// tekkitrestrict.log.info("c1"+cia.id+"|"+cia.data+" - "+cia.clicktype+" "+cia.air+" "+cia.block);

						cia.clicktype = token[1].toLowerCase();
						// 1234:20 right air
						if (token.length >= 3) {
							String token3 = token[2].toLowerCase();
							if (token3.equals("air")) cia.block = false;
							else if (token3.equals("block")) cia.air = false;
							else if (token3.equals("safezone")) cia.usesafezone = true;
							

							if (token.length == 4) {
								String token4 = token[3].toLowerCase();
								if (token4.equals("safezone")) {
									cia.usesafezone = true;
								}
							}
						}

						// tekkitrestrict.log.info(cia.id+"|"+cia.data+" - `"+cia.clicktype+"` "+cia.air+" "+cia.block);
						disableClickItemActions.add(cia);
					}
				}
			} else {
				List<TRCacheItem> iss = TRCacheItem.processItemString("", clickstring, -1);
				for (TRCacheItem ti : iss) {
					TRNoClick cia = new TRNoClick();
					cia.id = ti.id;
					cia.data = ti.getData();
					cia.clicktype = "both";
					// tekkitrestrict.log.info(cia.id+"|"+cia.data+" - "+cia.clicktype+" "+cia.air+" "+cia.block);
					disableClickItemActions.add(cia);
				}
			}
		}
	}

	public static boolean compareAll(PlayerInteractEvent e) {
		try {
			Player player = e.getPlayer();
			for (TRNoClick cia : disableClickItemActions) {
				if (cia.compare(player, e.getClickedBlock(), player.getItemInHand(), e.getAction())) {
					if (!cia.msg.equals("")) {
						player.sendMessage(ChatColor.RED + cia.msg);
					} else {
						// tekkitrestrict.log.info(cia.id+"|"+cia.data+" - "+cia.clicktype);
						String t = cia.clicktype == "both" ? "" : " " + cia.clicktype;
						String a = (cia.air && !cia.block) ? " in the air" : ((cia.block && !cia.air) ? " on blocks" : "");
						String s = (cia.insafezone && cia.usesafezone) ? " inside a safezone." : ".";
						player.sendMessage(ChatColor.RED + "Sorry, but" + t + " clicking with this item" + a + " is disabled" + s);
					}
					return true;
				}
			}
		} catch (Exception ex){
			TRLogger.Log("debug", "Error: [ListenInteract TRNoClick] " + ex.getMessage());
		}
		return false;
	}
}