package com.github.dreadslicer.tekkitrestrict;

import ic2.common.ElectricItem;
import ic2.common.ItemArmorElectric;
import ic2.common.ItemElectricTool;
import ic2.common.StackUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.server.Item;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.WorldServer;

import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Threads;
import com.github.dreadslicer.tekkitrestrict.lib.TRCharge;

import ee.EEBase;
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
		saveThread.start();
		disableItemThread.start();
		worldScrubThread.start();
		gemArmorThread.start();
		entityRemoveThread.start();
	}

	public static void reload() {
		// reloads the variables in each thread...
		instance.disableItemThread.reload();
	}

	public static void originalEUEnd() {
		instance.disableItemThread.originalEUEnd();
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
				Log.Exception(ex);
				
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

	private void GemArmorDisabler() {
		//TODO Change this one day
		try {
			if (!Threads.GAMovement) {
				synchronized (EEBase.playerArmorMovementToggle) {
					/*
					 * for(Object
					 * xu:EEBase.playerArmorMovementToggle.keySet()){
					 * EntityHuman hu = (EntityHuman)xu;
					 * EEBase.playerArmorMovementToggle.put(hu, false); }
					 */
					EEBase.playerArmorMovementToggle.clear();
				}
				/*
				 * Set ks = EEBase.playerArmorMovementToggle.keySet();
				 * Iterator ki = ks.iterator();
				 * 
				 * while(ki.hasNext()){ net.minecraft.server.EntityHuman H =
				 * (net.minecraft.server.EntityHuman)ki.next();
				 * 
				 * if(!bypassers.contains(tr.getServer().getPlayer(H.name))){
				 * EEBase.playerArmorMovementToggle.remove(H);
				 * tr.getServer().getPlayer(H.name).sendRawMessage("<?>"); }
				 * }
				 */
			}
			
			if (!Threads.GAOffensive) {
				synchronized (EEBase.playerArmorOffensiveToggle) {
					
					 /*for(Object xu:EEBase.playerArmorOffensiveToggle.keySet()){
						 EntityHuman hu = (EntityHuman)xu;
						 EEBase.playerArmorOffensiveToggle.put(hu, false); 
					 }*/
					
					EEBase.playerArmorOffensiveToggle.clear();
				}
				/*
				 * Set ks1 = EEBase.playerArmorOffensiveToggle.keySet();
				 * Iterator ki1 = ks1.iterator();
				 * 
				 * while(ki1.hasNext()){ net.minecraft.server.EntityHuman H
				 * = (net.minecraft.server.EntityHuman)ki1.next(); //boolean
				 * val =
				 * ((Boolean)EEBase.playerArmorOffensiveToggle.get(H)).
				 * booleanValue();
				 * if(!bypassers.contains(tr.getServer().getPlayer
				 * (H.name))){ EEBase.playerArmorMovementToggle.remove(H);
				 * tr.getServer().getPlayer(H.name).sendRawMessage("<?>"); }
				 * }
				 */
			}
		} catch (Exception ex) {}
	}
	/*
	private void reloadBypassers() {
		//bypassers.clear();
		//Player[] ps = tekkitrestrict.getInstance().getServer().getOnlinePlayers();
		// boolean SSDisableGemArmor =
		// tekkitrestrict.config.getBoolean("SSDisableGemArmor");

		//for (Player player : ps) {
			// boolean abypass = tekkitrestrict.p erm.h as(ps[i],
			// "tekkitrestrict.abypass");
			// boolean SSbypass = tekkitrestrict.p erm.h as(ps[i],
			// "tekkitrestrict.safezone.bypass");
			//boolean abypass = TRPermHandler.hasPermission(player, "abypass", "", "");
			
			 * boolean SSbypass =
			 * tekkitrestrict.hasPermission(ps[i],"tekkitrestrict.safezone.bypass"
			 * ); if(safeZone.inSafeZone(ps[i])){ //we now HAVE to have a
			 * safezone bypass to disable this. if(SSDisableGemArmor){
			 * if(SSbypass && abypass){ bypassers.add(ps[i]); } } } else
			 
			//if (abypass) bypassers.add(player);
			
		//}
	}*/
}

class TEntityRemover extends Thread {
	@Override
	public void run() {
		while (true) {
			try {
				disableEntities();
			} catch (Exception ex) {
				TRLogger.Log("debug", "Error: [Entity thread] " + ex.getMessage());
				Log.Exception(ex);
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

		// search through all of the entities
		List<World> worlds = tekkitrestrict.getInstance().getServer().getWorlds();

		//TODO entitylist might be changing while this is being done.
		for (World world : worlds) {
			net.minecraft.server.World wo = ((CraftWorld) world).getHandle();
			
			for (Object e1 : wo.entityList) { // loop through entities
				Entity e = ((net.minecraft.server.Entity) e1).getBukkitEntity();
				if (e instanceof Player || e instanceof org.bukkit.entity.Item) continue;
				if (TRSafeZone.inXYZSafeZone(e.getLocation(), e.getWorld().getName())) {
					e.remove();
				}
			}
		}
	}
}

class DisableItemThread extends Thread {
	//private boolean throttle;
	private List<TRCacheItem> SSDecharged = Collections.synchronizedList(new LinkedList<TRCacheItem>());
	private List<TRCharge> MCharges = Collections.synchronizedList(new LinkedList<TRCharge>()),
						   maxEU = Collections.synchronizedList(new LinkedList<TRCharge>());
	private List<TRCharge> originalEU = Collections.synchronizedList(new LinkedList<TRCharge>());
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
						Log.Exception(ex);
					}
				}
			} catch (Exception ex) {
				TRLogger.Log("debug", "Error: [ItemDisabler thread] " + ex.getMessage());
				Log.Exception(ex);
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
			org.bukkit.inventory.ItemStack[] st1 = inv.getContents();
			org.bukkit.inventory.ItemStack[] st2 = inv.getArmorContents();

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
					if (TRNoItem.isItemBanned(player, id, data) || TRNoItem.isCreativeItemBanned(player, id, data)) {
						st1[i] = new org.bukkit.inventory.ItemStack(Threads.ChangeDisabledItemsIntoId, 1);
						changed = true;
					}

					// //// HANDLE DECHARGE / MAXCHARGE (EE / IC2)
					if (changed) continue;
					
					try {
						String tstr = st1[i].getTypeId() + "";// +":"+st1[i].getData();

						if (tekkitrestrict.EEEnabled) {
							try {
								int m = MChargeStr.indexOf(tstr);
								if (m != -1) {
									TRCharge g = MCharges.get(m);
									if (g.id == st1[i].getTypeId()) {
										if (mcItemStack.getItem() instanceof ee.ItemEECharged) {
											ItemEECharged eer = (ItemEECharged) mcItemStack.getItem();
											double maxEE = eer.getMaxCharge();
											double per = maxEE / 100.000;
											int setMax = (new Double(per * g.maxcharge)).intValue();

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
								Log.Exception(ex);
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
										Double newcharge = (new Double(charge) * new Double(s.maxcharge))
												/ new Double(ci.maxCharge);
										// tekkitrestrict.log.info("charge: "+charge+" newcharge: "+newcharge);
										ci.maxCharge = s.maxcharge;
										ci.transferLimit = s.chargerate;
										nbttagcompound.setInt("charge", newcharge.intValue());

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
										Double newcharge = (new Double(charge) * new Double(s.maxcharge))
												/ new Double(ci.maxCharge);
										// tekkitrestrict.log.info("charge: "+charge+" newcharge: "+newcharge);
										ci.maxCharge = s.maxcharge;
										ci.transferLimit = s.chargerate;
										nbttagcompound.setInt("charge", newcharge.intValue());

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
										Double newcharge = (new Double(charge) * new Double(s.maxcharge))
												/ new Double(ci.maxCharge);
										// tekkitrestrict.log.info("charge: "+charge+" newcharge: "+newcharge);
										ci.maxCharge = s.maxcharge;
										ci.transferLimit = s.chargerate;
										nbttagcompound.setInt("charge", newcharge.intValue());

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
								Log.Exception(ex);
							}
						}

						if (TRSafeZone.inSafeZone(player) &&
							!Util.hasPermission(player, "safezone.bypass") &&
							!Util.hasBypass(player, "safezone", null)) {
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
								Log.Exception(ex);
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
								Log.Exception(ex);
							}
						}
					} catch (Exception ex) {
						TRLogger.Log("debug", "Error: [Decharger[6] thread] " + ex.getMessage());
						Log.Exception(ex);
					}
				} catch (Exception ex) {
					TRLogger.Log("debug", "Error: [ItemDisabler[16] thread] ");
					Log.Exception(ex);
				}
				//Thread.sleep(3);
			} //End of first for loop
			
			
			// //////////// ARMOR INVENTORY
			boolean changed1 = false;
			for (int i = 0; i < st2.length; i++) {
				try {
					org.bukkit.inventory.ItemStack str = st2[i];
					int id = str.getTypeId();
					int data = str.getData().getData();
					if (TRNoItem.isItemBanned(player, id, data) || TRNoItem.isCreativeItemBanned(player, id, data)) {
						// this item is banned/disabled for this player!!!
						st2[i] = new org.bukkit.inventory.ItemStack(Threads.ChangeDisabledItemsIntoId, 1); //proceed to remove it.
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
			Log.Exception(ex);
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
				List<TRCacheItem> iss = TRCacheItem.processItemString("",
						sseu[0], -1);
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
				List<TRCacheItem> iss = TRCacheItem.processItemString("",
						sscharge[0], -1);
				for (TRCacheItem isr : iss) {
					TRCharge gg = new TRCharge();
					gg.id = isr.id;
					gg.data = isr.getData();
					gg.maxcharge = max;
					// tekkitrestrict.log.info(gg.id+":"+gg.data+" "+gg.maxcharge);
					this.MCharges.add(gg);
					this.MChargeStr.add(gg.id + "");
				}
			}
		}
	}

	private void addOriginalEU(int id, int mcharge, int tlimit, Object store) {
		TRCharge trcc = new TRCharge();
		trcc.id = id;
		trcc.maxcharge = mcharge;
		trcc.chargerate = tlimit;
		trcc.itemstack = store;
		originalEU.add(trcc);
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
	public short getShort(org.bukkit.inventory.ItemStack bukkitItemStack, String key) {
		net.minecraft.server.ItemStack var1 = ((CraftItemStack) bukkitItemStack).getHandle();
		if (var1.tag == null) var1.setTag(new NBTTagCompound());
		
		if (!var1.tag.hasKey(key)) setShort(bukkitItemStack, key, 0);
		
		return var1.tag.getShort(key);
	}
	/**
	 * Sets a short value from the given key.<br>
	 * If the item doesn't have a tag it will add one.
	 */

	public void setShort(org.bukkit.inventory.ItemStack bukkitItemStack, String key, int value) {
		net.minecraft.server.ItemStack var1 = ((CraftItemStack) bukkitItemStack).getHandle();
		if (var1.tag == null) var1.setTag(new NBTTagCompound());
		
		var1.tag.setShort(key, (short) value);
	}
	/**
	 * Gets the string value for the given key.<br>
	 * If the item doesn't have a tag it will add one.<br>
	 * If the item doesn't have a value for the specified key it will make it and set it to "".
	 */

	public String getString(org.bukkit.inventory.ItemStack bukkitItemStack, String key) {
		net.minecraft.server.ItemStack var1 = ((CraftItemStack) bukkitItemStack).getHandle();
		if (var1.tag == null) var1.setTag(new NBTTagCompound());
		
		if (!var1.tag.hasKey(key)) setString(bukkitItemStack, key, "");
		
		return var1.tag.getString(key);
	}
	/**
	 * Sets the string value for the given key.<br>
	 * If the item doesn't have a tag it will add one.
	 */

	public void setString(org.bukkit.inventory.ItemStack bukkitItemStack, String key, String value) {
		net.minecraft.server.ItemStack var1 = ((CraftItemStack) bukkitItemStack).getHandle();
		if (var1.tag == null) var1.setTag(new NBTTagCompound());
		
		var1.tag.setString(key, value);
	}
}

class TWorldScrubber extends Thread {
	@Override
	public void run() {
		while (true) {
			try {
				doWScrub();
			} catch (Exception ex) {
				TRLogger.Log("debug", "Error: [WorldScrubber thread] " + ex.getMessage());
				Log.Exception(ex);
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
		try {
			TRChunkUnloader.unloadSChunks();
			sleep(20000);
			//Sleep for 20 seconds to make it less likely that chunks are still being unloaded when it starts
			//the Remove disabled blocks and Redpower checks
		} catch (Exception ex) {
		}
		
		if (!Threads.RMDB && !Threads.UseRPTimer) return;
		
		Server server = tekkitrestrict.getInstance().getServer();
		if (Threads.UseRPTimer){
			if (!server.getPluginManager().isPluginEnabled("mod_RedPowerLogic")) {
				Threads.UseRPTimer = false;
				if (!Threads.RMDB) return; //If both options are now false, nothing else has to be done.
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
								if (TRNoItem.isBlockDisabled(bl)) {
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
				Log.Exception(ex);
			}

			try {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The savethread triggers again if interrupted.
				Thread.sleep(Threads.saveSpeed);
			} catch (InterruptedException e) {}
		}
	}
}
