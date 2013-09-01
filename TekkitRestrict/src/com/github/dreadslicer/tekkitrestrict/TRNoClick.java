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

import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.TRClickType;


public class TRNoClick {
	public int id, data;
	public boolean air = false, block = false, safezone = false, useB = false;
	public String msg = ""; // left / right
	public TRClickType type = TRClickType.Both;

	public boolean compare(Player player, Block bl, ItemStack iss, Action action) {
		if (this.useB) {
			if (bl == null) return false;
			if (TRNoItem.equalSet(id, data, bl.getTypeId(), bl.getData())) {
				if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) return true;
			}
		} else if (TRNoItem.equalSet(id, data, iss.getTypeId(), iss.getDurability())) {

			boolean insafezone = safezone ? TRSafeZone.isSafeZoneFor(player, true, true) : true;
			if (insafezone) {
				if (type.both()){
					if (air && (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR)) return true;
					if (block && (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) return true;
				} else if (type.left()){
					if (air && action == Action.LEFT_CLICK_AIR) return true;
					if (block && action == Action.LEFT_CLICK_BLOCK) return true;
				} else if (type.right()){
					if (air && action == Action.RIGHT_CLICK_AIR) return true;
					if (block && action == Action.RIGHT_CLICK_BLOCK) return true;
				} else if (type.all()) {
					if (air && (action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR)) return true;
					if (block && (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) return true;
					if (action == Action.PHYSICAL) return true;
				} else if (type.trample()){
					if (action == Action.PHYSICAL) return true;
				}
			}
		}

		return false;
	}

	private static List<TRNoClick> disableClickItemActions = Collections.synchronizedList(new LinkedList<TRNoClick>());
	
	public static void reload(){
		disableClickItemActions.clear();
		List<String> disableClicks = tekkitrestrict.config.getStringList(ConfigFile.DisableClick , "DisableClick");
		for (String disableClick : disableClicks){
			
			String temp[] = disableClick.split(" ");
			if (temp[0].equalsIgnoreCase("block")){
				if (temp.length == 1){
					Log.Config.Warning("You have an error in your DisableClick config: \"block\" is not a valid itemstring");
					continue;
				}
				
				List<TRCacheItem> iss = TRCacheItem.processItemString("", temp[1], -1);
				for (TRCacheItem item : iss) {
					TRNoClick noclick = new TRNoClick();
					noclick.id = item.id;
					noclick.data = item.data;
					noclick.msg = "You may not interact with this block.";
					noclick.useB = true;
					disableClickItemActions.add(noclick);
				}
			} else {
				//###########################################################################
				//Id's and data
				List<TRCacheItem> iss = TRCacheItem.processItemString("", temp[0], -1);
				for (TRCacheItem item : iss){
					TRNoClick noclick = new TRNoClick();
					
					noclick.id = item.id;
					noclick.data = item.data;
					
					if (temp.length > 1){
						for (int i=1;i<temp.length;i++){
							String current = temp[i].toLowerCase();
							
							if (current.equals("left")) noclick.type = TRClickType.Left;
							else if (current.equals("right")) noclick.type = TRClickType.Right;
							else if (current.equals("both")) noclick.type = TRClickType.Both;
							else if (current.equals("trample")) noclick.type = TRClickType.Trample;
							else if (current.equals("all")) noclick.type = TRClickType.All;
							else if (current.equals("air")) noclick.air = true;
							else if (current.equals("block")) noclick.block = true;
							else if (current.equals("safezone")) noclick.safezone = true;
							else {
								Log.Config.Warning("You have an error in your DisableClick config: Invalid clicktype \""+current+"\"");
								Log.Config.Warning("Valid types: left, right, both, trample, all, air, block, safezone");
								continue;
							}
						}
					}
					if (!noclick.type.trample() && !noclick.air && !noclick.block){
						noclick.air = true;
						noclick.block = true;
					}
					
					String a = "";
					if (noclick.air){
						if (noclick.block) a = "";
						else a = " in the air";
					} else {
						a = " on blocks";
					}
					
					String s = noclick.safezone ? " inside a safezone." : ".";
					
					if (noclick.type.all() || noclick.type.both())
						noclick.msg = "Sorry, but clicking with this item"+a+" is disabled" + s;
					else if (noclick.type.left())
						noclick.msg = "Sorry, but left-clicking with this item"+a+" is disabled" + s;
					else if (noclick.type.right())
						noclick.msg = "Sorry, but right-clicking with this item"+a+" is disabled" + s;
					else if (noclick.type.trample())
						noclick.msg = "Sorry, but trampling with this item in your hand is disabled" + s;
					
					disableClickItemActions.add(noclick);
				}
				//###########################################################################
				
			}
		}
	}

	public static boolean errorLogged = false;
	public static boolean isDisabled(PlayerInteractEvent e) {
		try {
			Player player = e.getPlayer();
			for (TRNoClick cia : disableClickItemActions) {
				if (cia.compare(player, e.getClickedBlock(), player.getItemInHand(), e.getAction())) {
					if (!cia.msg.equals("")) {
						player.sendMessage(ChatColor.RED + cia.msg);
					} else {
						// tekkitrestrict.log.info(cia.id+"|"+cia.data+" - "+cia.clicktype);
						String t = (cia.type.both() || cia.type.all()) ? "" : " " + cia.type.name();
						String a = (cia.air && !cia.block) ? " in the air" : ((cia.block && !cia.air) ? " on blocks" : "");
						String s = (cia.safezone) ? " inside a safezone." : ".";
						player.sendMessage(ChatColor.RED + "Sorry, but" + t + " clicking with this item" + a + " is disabled" + s);
					}
					return true;
				}
			}
		} catch (Exception ex){
			if (!errorLogged){
				tekkitrestrict.log.warning("Error: [ListenInteract TRNoClick] " + ex.getMessage());
				errorLogged = true;
			}
			TRLogger.Log("debug", "Error: [ListenInteract TRNoClick] " + ex.getMessage());
		}
		return false;
	}
}