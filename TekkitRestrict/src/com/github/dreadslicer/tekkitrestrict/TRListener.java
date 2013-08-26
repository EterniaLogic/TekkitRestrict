package com.github.dreadslicer.tekkitrestrict;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.TileEntity;
import net.minecraft.server.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Global;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandAlc;

import eloraam.core.TileCovered;

public class TRListener implements Listener {
	private static TRListener instance;
	boolean SSInnvincible, UseBlockLimit;
	boolean LogAmulets, LogRings, LogDMTools, LogRMTools, LogEEMisc;
	private Map<Integer, String> EENames = Collections.synchronizedMap(new HashMap<Integer, String>());

	public TRListener() {
		EENames.put(27526, "Philosopher Stone");
		EENames.put(27527, "Destruction Catalyst");
		EENames.put(27528, "Iron Band");
		EENames.put(27529, "Soul Stone");
		EENames.put(27530, "Evertide Amulet");
		EENames.put(27531, "Volcanite Amulet");
		EENames.put(27532, "Black Hole Band");
		EENames.put(27533, "Ring of Ignition");
		EENames.put(27534, "Archangel's Smite");
		EENames.put(27535, "Hyperkinetic Lens");

		EENames.put(27536, "SwiftWolf's Rending Gale");
		EENames.put(27537, "Harvest Ring");
		EENames.put(27538, "Watch of Flowing Time");
		EENames.put(27539, "Alchemical Coal");
		EENames.put(27540, "Mobius Fuel");
		EENames.put(27541, "Dark Matter");
		EENames.put(27542, "Covalence Dust");

		EENames.put(27543, "Dark Matter Pickaxe");
		EENames.put(27544, "Dark Matter Spade");
		EENames.put(27545, "Dark Matter Hoe");
		EENames.put(27546, "Dark Matter Sword");
		EENames.put(27547, "Dark Matter Axe");
		EENames.put(27548, "Dark Matter Shears");

		EENames.put(27549, "Dark Matter Armor");
		EENames.put(27550, "Dark Matter Helmet");
		EENames.put(27551, "Dark Matter Greaves");
		EENames.put(27552, "Dark Matter Boots");

		EENames.put(27553, "Gem of Eternal Density");
		EENames.put(27554, "Repair Talisman");
		EENames.put(27555, "Dark Matter Hammer");
		EENames.put(27556, "Cataclyctic Lens");
		EENames.put(27557, "Klein Star Ein");
		EENames.put(27558, "Klein Star Zwei");
		EENames.put(27559, "Klein Star Drei");
		EENames.put(27560, "Klein Star Vier");
		EENames.put(27561, "Klein Star Sphere");
		EENames.put(27591, "Klein Star Omega");
		EENames.put(27562, "Alchemy Bag");
		EENames.put(27563, "Red Matter");
		EENames.put(27564, "Red Matter Pickaxe");
		EENames.put(27565, "Red Matter Spade");
		EENames.put(27566, "Red Matter Hoe");
		EENames.put(27567, "Red Matter Sword");
		EENames.put(27568, "Red Matter Axe");
		EENames.put(27569, "Red Matter Shears");
		EENames.put(27570, "Red Matter Hammer");
		EENames.put(27571, "Aeternalis Fuel");
		EENames.put(27572, "Red Katar");
		EENames.put(27573, "Red Morning Star");
		EENames.put(27574, "Zero Ring");
		EENames.put(27575, "Red Matter Armor");
		EENames.put(27576, "Red Matter Helmet");
		EENames.put(27577, "Red Matter Greaves");
		EENames.put(27578, "Red Matter Boots");
		EENames.put(27579, "Infernal Armor (Gem)");
		EENames.put(27580, "Abyss Helmet (Gem)");
		EENames.put(27581, "Gravity Greaves (Gem)");
		EENames.put(27582, "Hurricane Boots (Gem)");
		EENames.put(27583, "Mercurial Eye");
		EENames.put(27584, "Ring of Arcana");
		EENames.put(27585, "Divining Rod");
		EENames.put(27588, "Body Stone");
		EENames.put(27589, "Life Stone");
		EENames.put(27590, "Mind Stone");
		EENames.put(27592, "Transmutation Tablet");
		EENames.put(27593, "Void Ring");
		EENames.put(27594, "Alchemy Tome");
		instance = this;
	}

	public static void reload() {
		instance.SSInnvincible = tekkitrestrict.config.getBoolean("SSInvincible");
		instance.UseBlockLimit = tekkitrestrict.config.getBoolean("UseItemLimiter");

		instance.LogAmulets = tekkitrestrict.config.getBoolean("LogAmulets");
		instance.LogRings = tekkitrestrict.config.getBoolean("LogRings");
		instance.LogDMTools = tekkitrestrict.config.getBoolean("LogDMTools");
		instance.LogRMTools = tekkitrestrict.config.getBoolean("LogRMTools");
		instance.LogEEMisc = tekkitrestrict.config.getBoolean("LogEEMisc");
		
		TRNoClick.reload();
	}

	public static TRListener getInstance() {
		return instance;
	}
	
	private int lastdata = 0;

	/** @return <b>True</b> if id < 8 or id = 12, 13, 17, 24, 35, 44, 98 or 142. <b>False</b> otherwise. */
	private boolean Exempt(int id){
		return (id < 8 || id == 12 || id == 13 || id == 17 || id == 24 || id == 35 || id == 44 || id == 98 || id == 142);
	}
	
	public static boolean errorBlockPlace = false;
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		int id = block.getTypeId();
		if (Exempt(id)) return;
		
		Player player = e.getPlayer();
		
		if (player == null) {
			lastdata = block.getData();
			return;
		}
		
		try {
			if (!TRLWCProtect.checkLWCAllowed(e)) return;

			int data = block.getData();
			WorldServer ws = ((CraftWorld) block.getWorld()).getHandle();
			
			TileEntity te1 = ws.getTileEntity(block.getX(), block.getY(), block.getZ());

			if (UseBlockLimit && !player.hasPermission("tekkitrestrict.bypass.limiter")) {
				TRLimitBlock il = TRLimitBlock.getLimiter(player.getName());
				if (!il.checkLimit(e, false)) {
					
					player.sendMessage(ChatColor.RED + "[TRItemLimiter] You cannot place down any more of that block!");
					e.setCancelled(true);
					if (te1 instanceof TileCovered) {
						TileCovered tc = (TileCovered) te1;
						for (int i = 0; i < 6; i++) {
							if (tc.getCover(i) != -1 && tc.getCover(i) == data) {
								tc.tryRemoveCover(i);
							}
						}
						tc.updateBlockChange();
					}
				}
			}

			if (te1 != null && data == 0) {
				if (te1 instanceof TileCovered) {
					// TileCovered tc = (TileCovered)te1;
					// tekkitrestrict.log.info("ar "+lastdata);
					data = lastdata;
				}
			}
			boolean banned = false;
			
			if (Global.useNewBanSystem){
				if (TRCacheItem2.isBanned(player, "noitem", id, data)) banned = true;
			} else {
				if (TRNoItem.isItemBanned(player, id, data, true)) banned = true;
			}
			
			if (banned) {
				// tekkitrestrict.log.info(cc.id+":"+cc.getData());
				player.sendMessage(ChatColor.RED + "[TRItemDisabler] You cannot place down this type of block!");
				e.setCancelled(true);
				if (te1 instanceof TileCovered) {
					TileCovered tc = (TileCovered) te1;
					for (int i = 0; i < 6; i++) {
						if (tc.getCover(i) != -1 && tc.getCover(i) == data) {
							tc.tryRemoveCover(i);
						}
					}
					tc.updateBlockChange();
				}
			}
			lastdata = e.getBlock().getData();
		} catch(Exception ex){
			if (!errorBlockPlace){
				tekkitrestrict.log.warning("A exception occured in tekkitrestrict. Please give the developer the following information: ");
				tekkitrestrict.log.warning("(This error is only logged once) Error: [onBlockPlace] " + ex.getMessage());
				errorBlockPlace = true;
			}
			TRLogger.Log("debug", "Error: [onBlockPlace] " + ex.getMessage());
			Log.debugEx(ex);
		}
		
	}

	public static boolean errorDrop = false;
	@EventHandler(ignoreCancelled = true)
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		
		if (player.getGameMode() == GameMode.CREATIVE){
			if (!player.hasPermission("tekkitrestrict.bypass.creative")) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "[TRLimitedCreative] You cannot drop items!");
				return;
			}
		}
		
		try {
			TRNoDupe_BagCache cache;
			if ((cache = TRNoDupe_BagCache.check(player)) != null) {
				if (cache.hasBHBInBag) {
					cache.expire();
					event.setCancelled(true);
					player.kickPlayer("[TRDupe] you have a " + cache.dupeItem + " in your [" + cache.inBagColor + "] Alchemy Bag!");
					Log.Dupe("a " + cache.inBagColor + " Alchemy Bag and a " + cache.dupeItem, "alc", player.getName());
					return;
				}
			}
		} catch (Exception ex) {
			if (!errorDrop){
				tekkitrestrict.log.warning("A exception occured in tekkitrestrict. Please give the developer the following information: ");
				tekkitrestrict.log.warning("(This error is only logged once) Error: [onDropItem, handleDropDupes] " + ex.getMessage());
				errorDrop = true;
			}
			TRLogger.Log("debug", "Error: [onDropItem, handleDropDupes] " + ex.getMessage());
			Log.debugEx(ex);
		}
	}

	public static boolean errorInteract = false;
	// /////// START INTERACT //////////////
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		
		String pname = player.getName().toLowerCase(); // determine if this is Buildcraft or RedPower... Then exempt.
		if (pname.equals("[buildcraft]") || pname.equals("[redpower]")) return;
		
		// lets do this based on a white-listed approach.
		// First, lets loop through the DisableClick list to stop clicks.
		// Perf: 8x
		if (TRNoClick.isDisabled(event)){
			event.setCancelled(true);
			return;
		}

		if (TRNoDupeProjectTable.tableUseNotAllowed(event.getClickedBlock(), player)){
			player.sendMessage(ChatColor.RED + "Someone else is already using this project table!");
			event.setCancelled(true);
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE) {
			ItemStack str = player.getItemInHand();
			if (str != null) {
				boolean banned = false;
				try {
					if (Global.useNewBanSystem){
						if (TRCacheItem2.isBanned(player, "creative", str.getTypeId(), str.getDurability())) banned = true;
					} else {
						if (TRNoItem.isItemBannedInCreative(player, str.getTypeId(), str.getDurability(), true)) banned = true;
					}
				} catch (Exception ex) {
					if (!errorInteract){
						tekkitrestrict.log.warning("A exception occured in tekkitrestrict. Please give the developer the following information: ");
						tekkitrestrict.log.warning("(This error is only logged once) Error: [ListenInteract TRLimitedCreative] " + ex.getMessage());
						errorInteract = true;
					}
					Log.debugEx(ex);
					TRLogger.Log("debug", "Error: [ListenInteract TRLimitedCreative] " + ex.getMessage());
				}
				
				if (banned) {
					player.sendMessage(ChatColor.RED + "[TRLimitedCreative] You may not interact with this item.");
					event.setCancelled(true);
					player.setItemInHand(null);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInteractEvent2(PlayerInteractEvent event){
		if (!tekkitrestrict.EEEnabled) return;
		
		Player player = event.getPlayer();
		if (player == null) return;
		
		itemLogUse(player, event.getAction());
	}

	/** Log EE tools. */
	private void itemLogUse(Player player, Action action) {
		ItemStack a = player.getItemInHand();
		if (a == null) return;

		int id = a.getTypeId();
		
		if (inRange(id, 27530, 27531))
			logUse("EEAmulet", player, id);
		else if (inRange(id, 27532, 27534) || id == 27536 || id == 27537 || id == 27574 || id == 27584 || id == 27593)
			logUse("EERing", player, id);
		else if (inRange(id, 27543, 27548) || id == 27555){
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				logUse("EEDmTool", player, id);
		} else if (inRange(id, 27564, 27573)){
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				logUse("EERmTool", player, id);
		} else if (id == 27527 || id == 27556 || id == 27535)
			logUse("EEDestructive", player, id);
		else if (id == 27538 || id == 27553 || id == 27562 || id == 27583 || id == 27585 || id == 27592)
			logUse("EEMisc", player, id);
	}
	
	private void logUse(String logname, Player player, int id){
		int x = player.getLocation().getBlockX();
		int y = player.getLocation().getBlockY();
		int z = player.getLocation().getBlockZ();
		TRLogger.Log(logname, "[" + player.getName() + "][" + player.getWorld().getName() +
				" - " + x + "," + y + "," + z + "] used (" + id + ") `" + EENames.get(id) + "`");
	}

	private boolean inRange(int stack, int from, int to) {
		return (stack >= from && stack <= to);
	}

	// /////////// END INTERACT /////////////

	// /////////// START INVClicks/////////////
	public static boolean errorCreativeClick, errorCraft;
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void eventInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() == null) return;
		
		try {
			if (TRLimitedCreative.handleCreativeInvClick(event)) return;
		} catch (Exception ex) {
			if (!errorCreativeClick){
				tekkitrestrict.log.warning("A exception occured in tekkitrestrict. Please give the developer the following information: ");
				tekkitrestrict.log.warning("(This error is only logged once) Error: [handleCreativeInvClick] " + ex.getMessage());
				errorCreativeClick = true;
			}
			TRLogger.Log("debug", "Error! [handleCreativeInvClick] : " + ex.getMessage());
			Log.debugEx(ex);
		}
		try {
			// Determine if they are crafting a banned item.
			if (handleCraftBlock(event)) return;
		} catch (Exception ex) {
			if (!errorCraft){
				tekkitrestrict.log.warning("A exception occured in tekkitrestrict. Please give the developer the following information: ");
				tekkitrestrict.log.warning("(This error is only logged once) Error: [TRhandleCraftBlock] " + ex.getMessage());
				errorCraft = true;
			}
			TRLogger.Log("debug", "Error! [TRhandleCraftBlock] : " + ex.getMessage());
			Log.debugEx(ex);
		}
	}

	private boolean handleCraftBlock(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (item == null) return false;
		
		if (player.hasPermission("tekkitrestrict.bypass.noitem")) return false;
		
		boolean banned = false;
		
		if (Global.useNewBanSystem){
			if (TRCacheItem2.isBanned(player, "noitem", item.getTypeId(), item.getDurability())) banned = true;
		} else {
			if (TRNoItem.isItemBanned(player, item.getTypeId(), item.getDurability(), false)) banned = true;
		}
		
		if (banned) {
			player.sendMessage(ChatColor.RED + "[TRItemDisabler] You cannot obtain/modify this Item type!");
			event.setCancelled(true);
			return true;
		}
		return false;
		
	}

	// ////////////////END INVClicks //////////////////////////

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();
		if (player == null) return;
		
		TRNoDupeProjectTable.playerUnuse(player.getName());
		TRCommandAlc.setPlayerInv2(player);
	}

	private Map<String, Integer> PickupTick = Collections.synchronizedMap(new HashMap<String, Integer>());

	@EventHandler
	public void onPlayerPickupEvent(PlayerPickupItemEvent e) {
		//IMPORTANT Fix this in the next version to the new version.
		Player player = e.getPlayer();
		try {
			TRNoDupe_BagCache cache;
			if ((cache = TRNoDupe_BagCache.check(player)) != null) {
				e.setCancelled(true);
				// player.kickPlayer("[TRDupe] you have a Black Hole Band in your ["+Color+"] Alchemy Bag! Please remove it NOW!");

				// if(showDupesOnConsole)
				// tekkitrestrict.log.info(player.getName()+" ["+cache.inBagColor+" bag] attempted to dupe with the "+cache.dupeItem+"!");
				// TRLogger.Log("Dupe", player.getName()+" ["+cache.inBagColor+" bag] attempted to dupe with the "+cache.dupeItem+"!");
				// TRLogger.broadcastDupe(player.getName(), "the Alchemy Bag and "+cache.dupeItem);
				String playerName = player.getName();
				Integer tick = PickupTick.get(playerName);
				if (tick != null) {
					if (tick >= 40) {
						// player.sendMessage("You may not pick that up while a "+cache.dupeItem+" is in your ["+cache.inBagColor+" bag]");
						player.kickPlayer("[TRDupe] A " + cache.dupeItem + " has been removed from your [" + cache.inBagColor + "] Alchemy Bag!");
						Log.Dupe("a "+ cache.inBagColor + " Alchemy Bag and " + cache.dupeItem, "alc", player.getName());

						// remove the BHB / Void ring!!!
						cache.removeAlc();
						PickupTick.put(playerName, 1);
					} else {
						PickupTick.put(playerName, tick + 1);
					}
				} else
					PickupTick.put(playerName, 1);
				
			}
		} catch (Exception ex) {
			TRLogger.Log("debug", "Error! [TRNoDupePickup] : " + ex.getMessage());
			Log.debugEx(ex);
		}
	}
}
