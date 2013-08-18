package com.github.dreadslicer.tekkitrestrict;

import ic2.common.ElectricItem;
import ic2.common.ItemArmorElectric;
import ic2.common.ItemElectricTool;
import ic2.common.StackUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Dupes;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Threads;
import com.github.dreadslicer.tekkitrestrict.commands.TRCommandAlc;

import ee.AlchemyBagData;
import ee.EEBase;
import ee.ItemAlchemyBag;
import ee.ItemEECharged;
import eloraam.logic.TileLogicPointer;

public class TRThread {
	/** Thread will trigger again if interrupted. */
	public TSaveThread saveThread = new TSaveThread();
	/** Thread will NOT trigger again if interrupted. */
	public DisableItemThread disableItemThread = new DisableItemThread();
	/** Thread will NOT trigger again if interrupted. */
	public TWorldScrubber worldScrubThread = new TWorldScrubber();
	/** Thread will NOT trigger again if interrupted. */
	public TGemArmorDisabler gemArmorThread = new TGemArmorDisabler();
	/** Thread will NOT trigger again if interrupted. */
	public TEntityRemover entityRemoveThread = new TEntityRemover();
	public TRBagCacheThread bagCacheThread = new TRBagCacheThread();
	public TRLimitFlyThread limitFlyThread = new TRLimitFlyThread();
	private static TRThread instance;

	public TRThread() {
		instance = this;
	}

	/** Give a name to all threads and then start them. */
	public void init() {
		saveThread.setName("TekkitRestrict_SaveThread");
		disableItemThread.setName("TekkitRestrict_InventorySearchThread");
		worldScrubThread.setName("TekkitRestrict_BlockScrubberThread");
		gemArmorThread.setName("TekkitRestrict_GemArmorThread");
		entityRemoveThread.setName("TekkitRestrict_EntityRemoverThread");
		bagCacheThread.setName("TekkitRestrict_BagCacheThread");
		limitFlyThread.setName("TekkitRestrict_LimitFlyThread_Unused");
		saveThread.start();
		disableItemThread.start();
		worldScrubThread.start();
		gemArmorThread.start();
		entityRemoveThread.start();
		//if (tekkitrestrict.config.getBoolean("LimitFlightTime", false)) limitFlyThread.start();
		
		if (tekkitrestrict.EEEnabled && Dupes.alcBag) bagCacheThread.start();
	}

	public static void reload() {
		// reloads the variables in each thread...
		instance.disableItemThread.reload();
		if (tekkitrestrict.EEEnabled && Dupes.alcBag && !instance.bagCacheThread.isAlive()) instance.bagCacheThread.start();
	}

	public static void originalEUEnd() {
		instance.disableItemThread.originalEUEnd();
	}
}

class TRLimitFlyThread extends Thread {
	private int reset = 0;
	private static List<Player> isFlying = Collections.synchronizedList(new LinkedList<Player>());
	private static ConcurrentHashMap<Player, Integer> playerTimes = new ConcurrentHashMap<Player, Integer>();
	private static int groundTime = 99999999;
	
	@SuppressWarnings("unused")
	@Override
	public void run(){
		if (true) return;
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
	
	public static void setFly(Player player) {
		if (!isFlying.contains(player)) isFlying.add(player);
	}
	
	public static void setGrounded(Player player) {
		isFlying.remove(player);
	}
	
	@SuppressWarnings("unused")
	private static void willGround(Player player) {
		if (player.hasPermission("tekkitrestrict.bypass.flylimit")) return;
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

class TRBagCacheThread extends Thread {
	@Override
	public void run(){
		while (true) {
			// We want to loop through all of their alchemy bags HERE.
			// This function will also clean up the ones with non-players.

			// loop through all of teh players
			Player[] players = tekkitrestrict.getInstance().getServer().getOnlinePlayers();
			for (int i = 0; i < players.length; i++) {
				try {
					setCheck(players[i]); // checks and sets the vars.
				} catch (Exception e) {
				}
			}

			// remove offline players!
			int to = TRNoDupe_BagCache.watchers.size();
			for (int i = 0; i < to; i++) {
				try {
					TRNoDupe_BagCache cc = TRNoDupe_BagCache.watchers.get(i);
					if (!cc.isOnline()) {
						TRNoDupe_BagCache.watchers.remove(i);
						to--;
						i--;
					}
				} catch (Exception e) {
				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				if (tekkitrestrict.disable) break;
			}
		}
	}
	
	public static void setCheck(Player player) {
		if (player.hasPermission("tekkitrestrict.bypass.dupe.alcbag")) return;
		
		for (int i = 0; i < 16; i++) {
			try {
				EntityHuman H = ((CraftPlayer) player).getHandle();
				AlchemyBagData ABD = ItemAlchemyBag.getBagData(i, H, H.world);
				// ok, now we search!
				net.minecraft.server.ItemStack[] iss = ABD.items;

				for (int j = 0; j < iss.length; j++) {
					if (iss[j] == null) continue;
					if (iss[j].id != 27532 && iss[j].id != 27593) continue;
					if (!player.isOnline()) return;
					
					// they are attempting to dupe?
					
					TRNoDupe_BagCache cache = TRNoDupe_BagCache.watchers.get(player);
					if (cache == null) cache = new TRNoDupe_BagCache();

					cache.player = player;
					cache.inBagColor = TRCommandAlc.getColor(i);
					cache.dupeItem = (iss[j].id == 27532) ? "Black Hole Band" : "Void Ring";
					cache.hasBHBInBag = true;
					// tekkitrestrict.log.info("has in bag!");
					TRNoDupe_BagCache.watchers.put(player, cache);
					// player.kickPlayer("[TRDupe] you have a Black Hole Band in your ["+Color+"] Alchemy Bag! Please remove it NOW!");

					// lastPlayer = player.getName();
					
				}
			} catch (Exception ex) {
				// This alc bag does not exist
			}
		}
	}
}

class TGemArmorDisabler extends Thread {
	//List<Player> bypassers = Collections.synchronizedList(new LinkedList<Player>());

	@Override
	public void run() {
		int errors = 0;
		while (true) {
			try {
				if (!tekkitrestrict.EEEnabled){
					tekkitrestrict.log.warning("The GemArmorDisabler thread has stopped because EE is disabled.");
					break; //If ee is disabled, stop the thread.
				}
				GemArmorDisabler();
			} catch (Exception ex) {
				errors++;
				TRLogger.Log("debug", "Error: [GemArmor thread] " + ex.getMessage());
				Log.debugEx(ex);
				
				if (errors > 100){
					tekkitrestrict.log.warning("The GemArmorDisabler thread has errord for more than 100 time now. It will now be disabled.");
					break;
				}
			}
			
			try {
				Thread.sleep(Threads.gemArmorSpeed);
			} catch (InterruptedException e) {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The gemarmor thread shouldn't trigger again.
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void GemArmorDisabler() {
		//TODO Change this one day
		try {
			if (!Threads.GAMovement) {
				synchronized (EEBase.playerArmorMovementToggle) {
					
					Iterator it = EEBase.playerArmorMovementToggle.keySet().iterator();
					ArrayList<EntityHuman> toremove = new ArrayList<EntityHuman>();
					while (it.hasNext()){
						EntityHuman human = (EntityHuman) it;
						Player player = (Player) human.getBukkitEntity();
						if (player.hasPermission("tekkitrestrict.bypass.gemarmor.defensive")) continue;
						player.sendMessage(ChatColor.RED + "You are not allowed to use GemArmor Movement Powers!");
						toremove.add(human);
					}
					
					for (EntityHuman current : toremove){
						EEBase.playerArmorMovementToggle.remove(current);
					}
					//EEBase.playerArmorMovementToggle.clear();
				}
			}
			
			if (!Threads.GAOffensive) {
				synchronized (EEBase.playerArmorOffensiveToggle) {
					Iterator it = EEBase.playerArmorOffensiveToggle.keySet().iterator();
					ArrayList<EntityHuman> toremove = new ArrayList<EntityHuman>();
					while (it.hasNext()){
						EntityHuman human = (EntityHuman) it;
						Player player = (Player) human.getBukkitEntity();
						if (player.hasPermission("tekkitrestrict.bypass.gemarmor.offensive")) continue;
						player.sendMessage(ChatColor.RED + "You are not allowed to use GemArmor Offensive Powers!");
						toremove.add(human);
					}
					
					for (EntityHuman current : toremove){
						EEBase.playerArmorOffensiveToggle.remove(current);
					}
					
					//EEBase.playerArmorOffensiveToggle.clear();
				}
			}
		} catch (Exception ex) {}
	}
}

class TEntityRemover extends Thread {
	@Override
	public void run() {
		while (true) {
			try {
				disableEntities();
			} catch (Exception ex) {
				TRLogger.Log("debug", "Error: [Entity thread] " + ex.getMessage());
				Log.debugEx(ex);
			}
			
			try {
				Thread.sleep(Threads.SSEntityRemoverSpeed);
			} catch (InterruptedException e) {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The EntityRemoveThread shouldn't trigger again.
			}
		}
	}

	private void disableEntities() {
		if (!Threads.SSDisableEntities) return;

		List<World> worlds = tekkitrestrict.getInstance().getServer().getWorlds();

		for (World world : worlds) {
			Iterator<Entity> entities = world.getEntities().iterator();
			while (entities.hasNext()){
				Entity e = entities.next();
				
				if (e instanceof Player || e instanceof org.bukkit.entity.Item || e instanceof Vehicle || e instanceof ExperienceOrb || e instanceof FallingSand || e instanceof Painting) continue;
				if (TRSafeZone.inXYZSafeZone(e.getLocation())) {
					e.remove();
				}
			}
			
			//net.minecraft.server.World wo = ((CraftWorld) world).getHandle();
			//Iterator<net.minecraft.server.Entity> entities = wo.entityList.iterator();
			//while (entities.hasNext()){
			//	Entity e = entities.next().getBukkitEntity();
			//	if (e instanceof Player || e instanceof org.bukkit.entity.Item) continue;
			//	if (TRSafeZone.inXYZSafeZone(e.getLocation(), e.getWorld().getName())) {
			//		e.remove();
			//	}
			//}
		}
	}
}

class DisableItemThread extends Thread {
	//private boolean throttle;
	private List<TRCacheItem> SSDecharged = Collections.synchronizedList(new LinkedList<TRCacheItem>());
	private List<TRCharge> MCharges = Collections.synchronizedList(new LinkedList<TRCharge>()),
						   maxEU = Collections.synchronizedList(new LinkedList<TRCharge>());
	//private List<TRCharge> originalEU = Collections.synchronizedList(new LinkedList<TRCharge>());
	private List<String> MChargeStr = Collections
			.synchronizedList(new LinkedList<String>()),
			SSDechargedStr = Collections
					.synchronizedList(new LinkedList<String>()),
			maxEUStr = Collections.synchronizedList(new LinkedList<String>());
	Player oos;
	ExecutorService exe = Executors.newCachedThreadPool();
	@Override
	public void run() {
		while (true) {
			try {
				// Disabled Items remover
				// if (UseNoItem) {
				Player[] ps = tekkitrestrict.getInstance().getServer().getOnlinePlayers();
				for (Player pp : ps) {
					try {
						oos = pp;

						/*if (throttle) {
							exe.execute((new Runnable() {
								@Override
								public void run() {
									disableItems(oos);
								}
							}));
						} else {*/
							disableItems(pp);
						//}
					} catch (Exception ex) {
						TRLogger.Log("debug", "Error: [ItemDisabler[1] thread] " + ex.getMessage());
						Log.debugEx(ex);
					}
				}
			} catch (Exception ex) {
				TRLogger.Log("debug", "Error: [ItemDisabler thread] " + ex.getMessage());
				Log.debugEx(ex);
			}
			
			try {
				Thread.sleep(Threads.inventorySpeed);
			} catch (InterruptedException e) {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The disableItemsThread should not trigger again. (As all players will be gone on shutdown)
			}
		}
	}

	private void disableItems(Player player) {
		try {
			PlayerInventory inv = player.getInventory();
			ItemStack[] st1 = inv.getContents();
			ItemStack[] st2 = inv.getArmorContents();

			/*try {
				if (player.getItemOnCursor() != null) {
					org.bukkit.inventory.ItemStack str = player
							.getItemOnCursor();
					if(str != null){
						ItemStack ee = new ItemStack(str.getTypeId(),
								str.getAmount(), str.getData().getData());
						if (TRNoItem.isItemBanned(player, ee)
								|| TRNoItem.isCreativeItemBanned(player, ee)) {
							player.setItemOnCursor(null);
						}
					}
				}
			} catch (Exception ex) {
				TRLogger.Log("debug", "Error: [Inventory thread] DisableCursorItem " + ex.getMessage());
				Log.Exception(ex);
			}*/
			// //////////// NORMAL INVENTORY
			boolean changed = false;
			for (int i = 0; i < st1.length; i++) {
				try {
					if (st1[i] == null) continue;

					net.minecraft.server.ItemStack mcItemStack = ((CraftItemStack) st1[i]).getHandle();

					// //// BAN THE ITEM
					int id = st1[i].getTypeId();
					int data = st1[i].getData().getData();//TODO change to .getDurability()?
					boolean banned = false;
					if (TRConfigCache.Global.useNewBanSystem){
						if (TRCacheItem2.isBanned(player, "noitem", id, data)) banned = true;
						else if (player.getGameMode() == GameMode.CREATIVE && TRCacheItem2.isBanned(player, "creative", id, data)) banned = true;
					} else {
						if (TRNoItem.isItemBanned(player, id, data, true)) banned = true;//TODO Check bypass once then change true to false.
						else if (player.getGameMode() == GameMode.CREATIVE && TRNoItem.isItemBannedInCreative(player, id, data, true)) banned = true;//TODO Check bypass once then change true to false.
					}
					
					if (banned) {
						st1[i] = new ItemStack(Threads.ChangeDisabledItemsIntoId, 1);
						changed = true;
					}

					// //// HANDLE DECHARGE / MAXCHARGE (EE / IC2)
					if (changed) continue;
					
					try {
						String tstr = "" + st1[i].getTypeId();// +":"+st1[i].getData();

						if (tekkitrestrict.EEEnabled) {
							try {
								int m = MChargeStr.indexOf(tstr);
								if (m != -1) {
									TRCharge g = MCharges.get(m);
									if (g.id == st1[i].getTypeId()) {
										if (mcItemStack.getItem() instanceof ItemEECharged) {
											ItemEECharged eer = (ItemEECharged) mcItemStack.getItem();
											double maxEE = eer.getMaxCharge();
											double per = maxEE / 100.000;
											int setMax = (int) (per * g.maxcharge);

											short chargeGoal = getShort(st1[i], "chargeGoal");
											short chargeLevel = getShort(st1[i], "chargeLevel");
											short chargeTicks = getShort(st1[i], "chargeTicks");
											if (chargeGoal > setMax || chargeLevel > setMax) {
												setShort(st1[i], "chargeLevel", setMax);
												setShort(st1[i], "chargeGoal", setMax);
												// var1.setData(setMax);
												mcItemStack.setData(mcItemStack.i() - (setMax * 10 + chargeTicks << (eer.canActivate2() ? 2 :
													((int) (eer.canActivate() ? 1 :
														0)))));
												changed = true;
											}
										}
									}
								}
							} catch (Exception ex) {
								TRLogger.Log("debug", "Error: [MaxCharge thread] " + ex.getMessage());
								Log.debugEx(ex);
							}
						}

						if (maxEUStr.contains(tstr)) {
							try {
								TRCharge s = maxEU.get(maxEUStr.indexOf(tstr));
								Item si = mcItemStack.getItem();
								NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(mcItemStack);
								if (si instanceof ItemArmorElectric) {
									ItemArmorElectric ci = (ItemArmorElectric) si;
									if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
										this.addOriginalEU(ci.id, ci.maxCharge, ci.transferLimit, mcItemStack);
										// tekkitrestrict.log.info(ci.maxCharge+" dur: "+var1.i()+" mc: "+ci.getMaxCharge());
										double charge = nbttagcompound.getInt("charge");
										double newcharge = (charge * s.maxcharge) / ci.maxCharge;
										// tekkitrestrict.log.info("charge: "+charge+" newcharge: "+newcharge);
										ci.maxCharge = s.maxcharge;
										ci.transferLimit = s.chargerate;
										nbttagcompound.setInt("charge", (int) newcharge);

										ElectricItem.charge(mcItemStack, 10, 9999, true, false);
										/*
										 * if (var1.i() > 2)
										 * var1.setData(1 +
										 * ((s.maxcharge - newcharge) *
										 * (var1.i() - 2)) /
										 * s.maxcharge); else
										 * var1.setData(0);
										 */
										changed = true;
									}
								} else if (si instanceof ItemElectricTool) {
									ItemElectricTool ci = (ItemElectricTool) si;
									if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
										this.addOriginalEU(ci.id, ci.maxCharge, ci.transferLimit, mcItemStack);
										// tekkitrestrict.log.info(ci.maxCharge+" dur: "+var1.i()+" mc: "+ci.getMaxCharge());
										double charge = nbttagcompound.getInt("charge");
										double newcharge = (charge * s.maxcharge) / ci.maxCharge;
										// tekkitrestrict.log.info("charge: "+charge+" newcharge: "+newcharge);
										ci.maxCharge = s.maxcharge;
										ci.transferLimit = s.chargerate;
										nbttagcompound.setInt("charge", (int) newcharge);

										ElectricItem.charge(mcItemStack, 10, 9999, true, false);
										/*
										 * if (var1.i() > 2)
										 * var1.setData(1 +
										 * ((s.maxcharge - newcharge) *
										 * (var1.i() - 2)) /
										 * s.maxcharge); else
										 * var1.setData(0);
										 */
										changed = true;
									}
								} else if (si instanceof ElectricItem) {
									ElectricItem ci = (ElectricItem) si;
									if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
										this.addOriginalEU(ci.id, ci.maxCharge, ci.transferLimit, mcItemStack);
										// tekkitrestrict.log.info(ci.maxCharge+" dur: "+var1.i()+" mc: "+ci.getMaxCharge());
										double charge = nbttagcompound.getInt("charge");
										double newcharge = (charge * s.maxcharge) / ci.maxCharge;
										// tekkitrestrict.log.info("charge: "+charge+" newcharge: "+newcharge);
										ci.maxCharge = s.maxcharge;
										ci.transferLimit = s.chargerate;
										nbttagcompound.setInt("charge", (int) newcharge);

										ElectricItem.charge(mcItemStack, 10, 9999, true, false);
										/*
										 * if (var1.i() > 2)
										 * var1.setData(1 +
										 * ((s.maxcharge - newcharge) *
										 * (var1.i() - 2)) /
										 * s.maxcharge); else
										 * var1.setData(0);
										 */
										changed = true;
									}
								}
								/*
								 * if (itemstack.i() > 2)
								 * itemstack.setData(1 +
								 * ((ielectricitem1.getMaxCharge() - k)
								 * * (itemstack .i() - 2)) /
								 * ielectricitem1.getMaxCharge()); else
								 * itemstack.setData(0);
								 */
							} catch (Exception ex) {
								TRLogger.Log("debug", "Error: [Decharger[7] thread] " + ex.getMessage());
								Log.debugEx(ex);
							}
						}

						if (!player.hasPermission("tekkitrestrict.bypass.safezone") && TRSafeZone.inSafeZone(player)) {
							//tekkitrestrict.log.info("in SS");
							try {
								if (Threads.SSDisableArcane) {
									if (st1[i].getTypeId() == 27584 && (mcItemStack.getData() != 6 || !getString(st1[i], "mode").equals("earth"))) {
										setString(st1[i], "mode", "earth");
										mcItemStack.setData(6);
									}
								}
							} catch (Exception ex) {
								TRLogger.Log("debug", "SSDisableArcane[2] Error! " + ex.getMessage());
								Log.debugEx(ex);
							}
							
							if (!Threads.SSDechargeEE) continue;
							int m = SSDechargedStr.indexOf(tstr);
							if (m == -1) continue;
							try {
								TRCacheItem g = SSDecharged.get(m);
								if (g.id != st1[i].getTypeId()) continue;
								
								if (!(mcItemStack.getItem() instanceof ItemEECharged)) continue;
								if (st1[i].getTypeId() == g.id &&
									(getShort(st1[i], "chargeGoal") > 0 || getShort(st1[i], "chargeLevel") > 0)) {

									setShort(st1[i], "chargeLevel", 0);
									setShort(st1[i], "chargeGoal", 0);
									mcItemStack.setData(200);
									changed = true;
								}
							} catch (Exception ex) {
								TRLogger.Log("debug", "SSDisableItem[9] Error! " + ex.getMessage());
								Log.debugEx(ex);
							}
						}
					} catch (Exception ex) {
						TRLogger.Log("debug", "Error: [Decharger[6] thread] " + ex.getMessage());
						Log.debugEx(ex);
					}
				} catch (Exception ex) {
					TRLogger.Log("debug", "Error: [ItemDisabler[16] thread] ");
					Log.debugEx(ex);
				}
				//Thread.sleep(3);
			} //End of first for loop
			
			
			// //////////// ARMOR INVENTORY
			boolean changed1 = false;
			for (int i = 0; i < st2.length; i++) {
				try {
					ItemStack str = st2[i];
					int id = str.getTypeId();
					int data = str.getData().getData();
					boolean banned = false;
					if (TRConfigCache.Global.useNewBanSystem){
						if (TRCacheItem2.isBanned(player, "noitem", id, data)) banned = true;
						else if (player.getGameMode() == GameMode.CREATIVE && TRCacheItem2.isBanned(player, "creative", id, data)) banned = true;
					} else {
						if (TRNoItem.isItemBanned(player, id, data, true)) banned = true;//TODO Check bypass once then change true to false.
						else if (player.getGameMode() == GameMode.CREATIVE && TRNoItem.isItemBannedInCreative(player, id, data, true)) banned = true;//TODO Check bypass once then change true to false.
					}
					
					if (banned) {
						// this item is banned/disabled for this player!!!
						st2[i] = new ItemStack(Threads.ChangeDisabledItemsIntoId, 1); //proceed to remove it.
						changed1 = true;
					}
				} catch (Exception ex) {}
				Thread.sleep(3);
			}
			
			// place new inventory back.
			//TODO Probably not needed as st1 and st2 are references to the players inventory.
			if (changed) inv.setContents(st1);
			if (changed1) inv.setArmorContents(st2);
			
		} catch (Exception ex) {
			TRLogger.Log("debug", "Error: [ItemDisabler[2] thread] " + ex.getMessage());
			Log.debugEx(ex);
		}
	}

	public void reload() {
		if (this.SSDecharged != null) this.SSDecharged.clear();
		
		if (this.SSDechargedStr != null) this.SSDechargedStr.clear();
		
		if (this.MCharges != null) this.MCharges.clear();
		
		if (this.MChargeStr != null) this.MChargeStr.clear();
		
		if (this.maxEU != null) this.maxEU.clear();
		
		if (this.maxEUStr != null) this.maxEUStr.clear();
		
		//this.UseNoItem = tekkitrestrict.config.getBoolean("UseNoItem");
		//this.throttle = tekkitrestrict.config
		//		.getBoolean("ThrottleInventoryThread");
		List<String> MaxCharges = tekkitrestrict.config.getStringList("MaxCharge");
		List<String> sstr = tekkitrestrict.config.getStringList("DechargeInSS");

		for (String s : sstr) {
			List<TRCacheItem> iss = TRCacheItem.processItemString("", s, -1);
			for (TRCacheItem iss1 : iss) {
				this.SSDecharged.add(iss1);
				this.SSDechargedStr.add(iss1.id + "");
			}
		}

		List<String> meu = tekkitrestrict.config.getStringList("MaxEU");
		for (String s : meu) {
			if (s.contains(" ")) {
				String[] sseu = s.split(" ");
				int eu = Integer.parseInt(sseu[1]);
				int chrate = Integer.parseInt(sseu[2]);
				List<TRCacheItem> iss = TRCacheItem.processItemString("", sseu[0], -1);
				for (TRCacheItem iss1 : iss) {
					TRCharge gg = new TRCharge();
					gg.id = iss1.id;
					gg.data = iss1.data;
					gg.maxcharge = eu;
					gg.chargerate = chrate;
					this.maxEU.add(gg);
					this.maxEUStr.add(iss1.id + "");
				}
			}
		}

		// process charges...
		MCharges.clear();
		for (int l = 0; l < MaxCharges.size(); l++) {
			String Charge = MaxCharges.get(l);
			if (Charge.contains(" ")) {
				String[] sscharge = Charge.split(" ");
				int max = Integer.parseInt(sscharge[1]);
				// ItemStack[] gs = TRNoItem.getRangedItemValues(sscharge[0]);
				List<TRCacheItem> iss = TRCacheItem.processItemString("", sscharge[0], -1);
				for (TRCacheItem isr : iss) {
					TRCharge gg = new TRCharge();
					gg.id = isr.id;
					gg.data = isr.data;
					gg.maxcharge = max;
					// tekkitrestrict.log.info(gg.id+":"+gg.data+" "+gg.maxcharge);
					this.MCharges.add(gg);
					this.MChargeStr.add(gg.id + "");
				}
			}
		}
	}

	private void addOriginalEU(int id, int mcharge, int tlimit, Object store) {
		//TRCharge trcc = new TRCharge();
		//trcc.id = id;
		//trcc.maxcharge = mcharge;
		//trcc.chargerate = tlimit;
		//trcc.itemstack = store;
		//originalEU.add(trcc);
	}

	public void originalEUEnd() {
		// returns the affected items back to their normal rates... so they are
		// saved correctly.
		/*
		 * for(TRCharge s:originalEU){ if(s.itemstack != null){ try{
		 * net.minecraft.server.ItemStack var1 =
		 * (net.minecraft.server.ItemStack)s.itemstack; Item si =
		 * var1.getItem(); int k = s.maxcharge; NBTTagCompound nbttagcompound =
		 * StackUtil.getOrCreateNbtData(var1); if(si instanceof
		 * ItemArmorElectric){ ItemArmorElectric ci = (ItemArmorElectric) si;
		 * if(ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate){
		 * this.addOriginalEU(ci.id,ci.maxCharge,ci.transferLimit,var1);
		 * tekkitrestrict
		 * .log.info(ci.maxCharge+" dur: "+var1.i()+" mc: "+ci.getMaxCharge());
		 * double charge = nbttagcompound.getInt("charge"); Double newcharge =
		 * (new Double(charge)*new Double(s.maxcharge))/new
		 * Double(ci.maxCharge);
		 * tekkitrestrict.log.info("charge: "+charge+" newcharge: "+newcharge);
		 * ci.maxCharge = s.maxcharge; ci.transferLimit = s.chargerate;
		 * nbttagcompound.setInt("charge",newcharge.intValue());
		 * 
		 * ElectricItem.charge(var1, 10, 9999, true, false); /*if (var1.i() > 2)
		 * var1.setData(1 + ((s.maxcharge - newcharge) * (var1.i() - 2)) /
		 * s.maxcharge); else var1.setData(0);* / } } else if(si instanceof
		 * ItemElectricTool){ ItemElectricTool ci = (ItemElectricTool) si;
		 * if(ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate){
		 * this.addOriginalEU(ci.id,ci.maxCharge,ci.transferLimit,var1);
		 * //tekkitrestrict
		 * .log.info(ci.maxCharge+" dur: "+var1.i()+" mc: "+ci.getMaxCharge());
		 * int charge = nbttagcompound.getInt("charge"); int newcharge =
		 * (charge*s.maxcharge)/ci.maxCharge; ci.maxCharge = s.maxcharge;
		 * ci.transferLimit = s.chargerate;
		 * nbttagcompound.setInt("charge",newcharge);
		 * 
		 * ElectricItem.charge(var1, 10, 9999, true, false); /*if (var1.i() > 2)
		 * var1.setData(1 + ((ci.getMaxCharge() - k) * (var1.i() - 2)) /
		 * ci.getMaxCharge()); else var1.setData(0);* / } } else if(si
		 * instanceof ElectricItem){ ElectricItem ci = (ElectricItem) si;
		 * if(ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate){
		 * this.addOriginalEU(ci.id,ci.maxCharge,ci.transferLimit,var1);
		 * //tekkitrestrict
		 * .log.info(ci.maxCharge+" dur: "+var1.i()+" mc: "+ci.getMaxCharge());
		 * int charge = nbttagcompound.getInt("charge"); int newcharge =
		 * (charge*s.maxcharge)/ci.maxCharge; ci.maxCharge = s.maxcharge;
		 * ci.transferLimit = s.chargerate;
		 * nbttagcompound.setInt("charge",newcharge);
		 * 
		 * ElectricItem.charge(var1, 10, 9999, true, false); /*if (var1.i() > 2)
		 * var1.setData(1 + ((ci.getMaxCharge() - k) * (var1.i() - 2)) /
		 * ci.getMaxCharge()); else var1.setData(0);* / } } } catch(Exception
		 * es){ es.printStackTrace(); } } }
		 */
	}

	/**
	 * Gets the short value for the given key.<br>
	 * If the item doesn't have a tag it will add one.<br>
	 * If the item doesn't have a value for the specified key it will make it and set it to 0.
	 */
	public short getShort(ItemStack bukkitItemStack, String key) {
		net.minecraft.server.ItemStack var1 = ((CraftItemStack) bukkitItemStack).getHandle();
		if (var1.tag == null) var1.setTag(new NBTTagCompound());
		
		if (!var1.tag.hasKey(key)) setShort(bukkitItemStack, key, 0);
		
		return var1.tag.getShort(key);
	}
	/**
	 * Sets a short value from the given key.<br>
	 * If the item doesn't have a tag it will add one.
	 */

	public void setShort(ItemStack bukkitItemStack, String key, int value) {
		net.minecraft.server.ItemStack var1 = ((CraftItemStack) bukkitItemStack).getHandle();
		if (var1.tag == null) var1.setTag(new NBTTagCompound());
		
		var1.tag.setShort(key, (short) value);
	}
	/**
	 * Gets the string value for the given key.<br>
	 * If the item doesn't have a tag it will add one.<br>
	 * If the item doesn't have a value for the specified key it will make it and set it to "".
	 */

	public String getString(ItemStack bukkitItemStack, String key) {
		net.minecraft.server.ItemStack var1 = ((CraftItemStack) bukkitItemStack).getHandle();
		if (var1.tag == null) var1.setTag(new NBTTagCompound());
		
		if (!var1.tag.hasKey(key)) setString(bukkitItemStack, key, "");
		
		return var1.tag.getString(key);
	}
	/**
	 * Sets the string value for the given key.<br>
	 * If the item doesn't have a tag it will add one.
	 */

	public void setString(ItemStack bukkitItemStack, String key, String value) {
		net.minecraft.server.ItemStack var1 = ((CraftItemStack) bukkitItemStack).getHandle();
		if (var1.tag == null) var1.setTag(new NBTTagCompound());
		
		var1.tag.setString(key, value);
	}
}

class TWorldScrubber extends Thread {
	private boolean stillRunning = false;
	private boolean logged = false;
	@Override
	public void run() {
		while (true) {
			try {
				while (stillRunning){
					sleep(1000); //While still running sleep 1 second to make it less likely that chunks are still being unloaded when it starts
					//the Remove disabled blocks and Redpower checks
					if (!logged){
						logged = true;
						Log.Config.Warning("Your WorldScrubber speed is too fast.");
					}
				}
				doWScrub();
			} catch (Exception ex) {
				TRLogger.Log("debug", "Error: [WorldScrubber thread] " + ex.getMessage());
				Log.debugEx(ex);
			}

			try {
				Thread.sleep(Threads.worldCleanerSpeed);
			} catch (InterruptedException ex) {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The WorldScrubber thread shouldn't trigger again.
			}
		}
	}

	/**
	 * Runs TRChunkUnloader.unloadSChunks().<br>
	 * Then if UseRPTimer or RemoveDisabledBlocks is turned on, it will execute those features.
	 */
	private void doWScrub() {
		stillRunning = true;
		
		try {
			TRChunkUnloader.unloadSChunks();
		} catch (Exception ex) {
		}
		
		if (!Threads.RMDB && !Threads.UseRPTimer){
			stillRunning = false;
			return;
		}
		
		Server server = tekkitrestrict.getInstance().getServer();
		if (Threads.UseRPTimer){
			if (!server.getPluginManager().isPluginEnabled("mod_RedPowerLogic")) {
				Threads.UseRPTimer = false;
				if (!Threads.RMDB){
					stillRunning = false;
					return; //If both options are now false, nothing else has to be done.
				}
			}
		}
		//int currentChunkCount = 0;
		
		List<World> worlds = server.getWorlds();
		for (World bukkitWorld : worlds) { //For each world
			WorldServer worldServer = ((CraftWorld) bukkitWorld).getHandle();

			Chunk[] loadedChunks = bukkitWorld.getLoadedChunks();
			//currentChunkCount += loadedChunks.length;
			
			for (Chunk c : loadedChunks) { //For each loaded chunk
				if (Threads.RMDB) { // loop through all of the blocks in the chunk...
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 16; z++) {
							for (int y = 0; y < 256; y++) {
								// so... yeah.
								Block bl = c.getBlock(x, y, z);
								if (TRNoItem.isBlockBanned(bl)) {
									bl.setTypeId(Threads.ChangeDisabledItemsIntoId);
								}
							}
						}
					}
				}

				if (!Threads.UseRPTimer) continue;
				
				try {
					BlockState[] tileEntities = c.getTileEntities();
					for (BlockState gg : tileEntities) {
						TileEntity te = worldServer.getTileEntity(gg.getX(), gg.getY(), gg.getZ());
						if (te instanceof TileLogicPointer) {
							TileLogicPointer timer = (TileLogicPointer) te;

							if (timer.GetInterval() < Threads.RPTickTime) {
								timer.SetInterval(Threads.RPTickTime);
							}
						}
					}
				} catch (Exception EER) {
					TRLogger.Log("debug", "RPTimerError: " + EER.getMessage());
				}
			}
		}
		stillRunning = false;
	}
}

class TSaveThread extends Thread {
	@Override
	public void run() {
		while (true) {
			// runs save functions for both safezones and itemlimiter
			try {
				TRLimitBlock.saveLimiters();
			} catch (Exception ex) {}
			
			try {
				TRSafeZone.save();
			} catch (Exception ex) {}

			TRLogger.saveLogs();
			TRNoHack.clearMaps();
			try{TRLimitBlock.manageData();}
			catch(Exception ex){
				TRLogger.Log("debug", "ManageData AutoSavethread Error: "+ex.getMessage());
				Log.debugEx(ex);
			}

			try {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The savethread triggers again if interrupted.
				Thread.sleep(Threads.saveSpeed);
			} catch (InterruptedException e) {}
		}
	}
}
