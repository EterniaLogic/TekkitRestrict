package nl.taico.tekkitrestrict;

import ic2.common.ElectricItem;
import ic2.common.ItemArmorElectric;
import ic2.common.ItemElectricTool;
import ic2.common.StackUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.NBTTagCompound;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingSand;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Threads;
import nl.taico.tekkitrestrict.functions.TRChunkUnloader;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRNoHack;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.functions.TRSafeZone;
import nl.taico.tekkitrestrict.objects.TRCharge;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

import ee.EEBase;
import ee.ItemEECharged;

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
	//public TRLimitFlyThread limitFlyThread = new TRLimitFlyThread();
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
		//limitFlyThread.setName("TekkitRestrict_LimitFlyThread_Unused");
		saveThread.start();
		disableItemThread.start();
		worldScrubThread.start();
		gemArmorThread.start();
		entityRemoveThread.start();
		//if (tekkitrestrict.config.getBoolean("LimitFlightTime", false)) limitFlyThread.start();
	}
	
	public static void reload() {
		// reloads the variables in each thread...
		instance.disableItemThread.reload();
		//instance.limitFlyThread.reload();
	}
}

class TRLimitFlyThread extends Thread {
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

class TGemArmorDisabler extends Thread {
	@Override
	public void run() {
		int errors = 0;
		while (true) {
			try {
				if (!tekkitrestrict.EEEnabled){
					Warning.other("The GemArmorDisabler thread has stopped because EE is disabled.", false);
					break; //If ee is disabled, stop the thread.
				}
				GemArmorDisabler();
			} catch (Exception ex) {
				errors++;
				Warning.other("Error: [GemArmor thread] " + ex.toString(), false);
				if (errors < 2){
					Log.Exception(ex, true);
				}
				
				if (errors > 50){
					Warning.other("The GemArmorDisabler thread has errored for more than 50 time now. It will now be disabled.", true);
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

	private void GemArmorDisabler() throws Exception {
		//TODO Change this one day
		try {
			if (!Threads.GAMovement) {
				synchronized (EEBase.playerArmorMovementToggle) {
					
					Iterator<EntityHuman> it = EEBase.playerArmorMovementToggle.keySet().iterator();
					ArrayList<EntityHuman> toremove = new ArrayList<EntityHuman>();
					while (it.hasNext()){
						EntityHuman human = it.next();
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
					Iterator<EntityHuman> it = EEBase.playerArmorOffensiveToggle.keySet().iterator();
					ArrayList<EntityHuman> toremove = new ArrayList<EntityHuman>();
					while (it.hasNext()){
						EntityHuman human = it.next();
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
		} catch (Exception ex) {
			throw ex;
		}
	}
}

class TEntityRemover extends Thread {
	@Override
	public void run() {
		try {
			Thread.sleep(Threads.SSEntityRemoverSpeed);
		} catch (InterruptedException e) {
			if (tekkitrestrict.disable) return; //If plugin is disabling, then stop the thread. The EntityRemoveThread shouldn't trigger again.
		}
		while (true) {
			try {
				disableEntities();
			} catch (Exception ex) {
				Warning.other("An error occurred trying to disable entities!", false);
				Log.Exception(ex, false);
			}
			
			try {
				Thread.sleep(Threads.SSEntityRemoverSpeed);
			} catch (InterruptedException e) {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The EntityRemoveThread shouldn't trigger again.
			}
		}
	}

	private boolean err1;
	
	@SuppressWarnings("rawtypes")
	private void disableEntities() {
		if (!Threads.SSDisableEntities) return;

		List<World> worlds = tekkitrestrict.getInstance().getServer().getWorlds();
		
		
		for (World world : worlds) {
			try {
				Chunk[] chunks = world.getLoadedChunks();
				for (Chunk c : chunks){
					ArrayList<Entity> tbr = new ArrayList<Entity>();
					Entity[] entities = c.getEntities();
					for (Entity e : entities){
						if (e instanceof org.bukkit.entity.Item || e instanceof Player || e instanceof ExperienceOrb || e instanceof FallingSand || e instanceof Painting) continue;
						if (e instanceof Vehicle && !(e instanceof Pig)) continue;
						boolean blocked = false;
						for (Class cl : TRConfigCache.Threads.SSClassBypasses){
							if (cl.isInstance(e)){
								blocked = true;
								break;
							}
						}
						if (blocked) continue;
						tbr.add(e);
					}
					
					for (Entity e : tbr){
						if (e == null) continue;
						if (!TRSafeZone.getSafeZoneByLocation(e.getLocation(), true).equals("")) {
							e.remove();
						}
						
					}
				}
				/*
				List<Entity> entities = world.getEntities();
				for (int i = 0;i<entities.size();i++){
					Entity e = entities.get(i);
					//e instanceof Vehicle = pig
					if (e instanceof org.bukkit.entity.Item || e instanceof Player || e instanceof ExperienceOrb || e instanceof FallingSand || e instanceof Painting) continue;
					if (e instanceof Vehicle && !(e instanceof Pig)) continue;
					boolean blocked = false;
					for (Class cl : TRConfigCache.Threads.SSClassBypasses){
						if (cl.isInstance(e)){
							blocked = true;
							break;
						}
					}
					if (blocked) continue;
					
					if (!TRSafeZone.getSafeZoneByLocation(e.getLocation(), true).equals("")) {
						tbr.add(e);
					}
				}
				*/
			} catch (Exception ex){
				if (!err1){
					Warning.other("An error occurred in the entities Disabler thread! (this error will only be logged once)", false);
					Log.Exception(ex, false);
					err1 = true;
				}
				//Entities list probably modified while iterating over it.
			}
		}
	}
}

class DisableItemThread extends Thread {
	private ConcurrentHashMap<Integer, TRItem> ssDecharged = new ConcurrentHashMap<Integer, TRItem>();
	private ConcurrentHashMap<Integer, TRCharge> mCharges = new ConcurrentHashMap<Integer, TRCharge>();
	private ConcurrentHashMap<Integer, TRCharge> maxEU = new ConcurrentHashMap<Integer, TRCharge>();
	
	@Override
	public void run() {
		load();
		while (true) {
			try {
				// Disabled Items remover
				Player[] players = tekkitrestrict.getInstance().getServer().getOnlinePlayers();
				for (Player player : players) {
					try {
						disableItems(player);
					} catch (Exception ex) {
						Warning.other("An error occured in [ItemDisabler thread] (1 player)!", false);
						Log.debugEx(ex);
					}
				}
			} catch (Exception ex) {
				Warning.other("An error occured in [ItemDisabler thread] (player loop)!", false);
				Log.Exception(ex, true);
			}
			
			try {
				Thread.sleep(Threads.inventorySpeed);
			} catch (InterruptedException e) {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The disableItemsThread should not trigger again. (As all players will be gone on shutdown)
			}
		}
	}
	
	private void disableItems(Player player) {
		if (player == null) return;
		
		PlayerInventory inv = player.getInventory();
		ItemStack[] st1 = inv.getContents();

		// //////////// NORMAL INVENTORY
		boolean changedInv = false, changedArmor = false;
		boolean bypassn = player.hasPermission("tekkitrestrict.bypass.noitem");
		boolean bypassc = player.hasPermission("tekkitrestrict.bypass.creative");
		boolean bypassSafezone = player.hasPermission("tekkitrestrict.bypass.safezone");
		boolean isCreative = (player.getGameMode() == GameMode.CREATIVE);
		
		try {
			for (int i = 0; i < st1.length; i++) {
				try {
					if (st1[i] == null) continue;

					String banned = null;
					int id = st1[i].getTypeId();
					int data = st1[i].getDurability();
					
					if (isCreative){
						if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
						if (banned == null && !bypassc) banned = TRNoItem.isItemBannedInCreative(player, id, data, false);
					} else {
						if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
					}
					
					if (banned != null) {
						if (banned.equals("")) banned = ChatColor.RED + "Removed a banned item in your inventory: "+id+":"+data+".";
						TRItem.sendBannedMessage(player, banned);
						changedInv = true;
						st1[i] = new ItemStack(Threads.ChangeDisabledItemsIntoId, 1);
						continue; //Item is now dirt so continue with next one.
					}
					else if (checkEECharge(st1[i]) || checkCharge(st1[i])){
						changedInv = true;
						continue;
					}
					else if (!bypassSafezone && TRSafeZone.isSafeZoneFor(player, true, false)) {
						if (checkEEArcanaSafeZone(st1[i]) || checkEEChargeSafeZone(st1[i])) changedInv = true;
					}
				} catch (Exception ex) {
					Warning.other("Error: [ItemDisabler thread] (check inv) " + ex.toString(), false);
					Log.debugEx(ex);
				}
			} //End of first for loop
			
			
			// //////////// ARMOR INVENTORY
			//boolean changed1 = false;
			ItemStack[] st2 = inv.getArmorContents();
			for (int i = 0; i < st2.length; i++) {
				try {
					ItemStack str = st2[i];
					int id = str.getTypeId();
					int data = str.getDurability();

					String banned = null;
					if (isCreative){
						if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
						if (banned == null && !bypassc) banned = TRNoItem.isItemBannedInCreative(player, id, data, false);
					} else {
						if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
					}
					
					if (banned != null) {
						if (banned.equals("")) banned = ChatColor.RED + "Removed banned armor from your inventory: "+id+":"+data+".";
						TRItem.sendBannedMessage(player, banned);
						changedArmor = true;
						st2[i] = new ItemStack(Threads.ChangeDisabledItemsIntoId, 1); //proceed to remove it.
						continue;
					}
					else if (checkCharge(st2[i])){
						changedArmor = true;
						continue;
					}
					
				} catch (Exception ex) {
					Warning.other("Error: [ItemDisabler thread] (check armor) " + ex.toString(), false);
					Log.debugEx(ex);
				}
			}
			
			// place new inventory back.
			if (changedInv){
				inv.setContents(st1);
			}
			
			if (changedArmor){
				inv.setArmorContents(st2);
			}
			
		} catch (Exception ex) {
			Warning.other("Error: [ItemDisabler thread] " + ex.toString(), false);
			Log.debugEx(ex);
		}
	}

	private boolean checkCharge(ItemStack is){
		int id = is.getTypeId();
		TRCharge s = maxEU.get(id);
		if (s == null) return false;
		
		try {
			net.minecraft.server.ItemStack mcItemStack = ((CraftItemStack) is).getHandle();

			Item si = mcItemStack.getItem();
			NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(mcItemStack);
			if (si instanceof ItemArmorElectric) {
				ItemArmorElectric ci = (ItemArmorElectric) si;
				if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
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
					return true;
				}
			} else if (si instanceof ItemElectricTool) {
				ItemElectricTool ci = (ItemElectricTool) si;
				if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
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
					return true;
				}
			} else if (si instanceof ElectricItem) {
				ElectricItem ci = (ElectricItem) si;
				if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
					//this.addOriginalEU(ci.id, ci.maxCharge, ci.transferLimit, mcItemStack);
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
					return true;
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
			Log.Debug("Error: [Decharger[7] thread] " + ex.toString());
			Log.debugEx(ex);
		}
		return false;
	}
	
	private boolean checkEECharge(ItemStack is){
		if (!tekkitrestrict.EEEnabled) return false;
		
		int id = is.getTypeId();
		TRCharge g = mCharges.get(id);
		if (g == null) return false;
		
		try {
			net.minecraft.server.ItemStack mcItemStack = ((CraftItemStack) is).getHandle();
			if (!(mcItemStack.getItem() instanceof ItemEECharged)) return false;
			
			ItemEECharged eer = (ItemEECharged) mcItemStack.getItem();
			double maxEE = eer.getMaxCharge();//1, 2, 3, etc.
			double per = maxEE / 100.000;
			int setMax = (int) Math.round((per * g.maxcharge));

			short chargeGoal = getShort(is, "chargeGoal");
			short chargeLevel = getShort(is, "chargeLevel");
			short chargeTicks = getShort(is, "chargeTicks");
			if (chargeGoal > setMax || chargeLevel > setMax) {
				setShort(is, "chargeLevel", setMax);
				setShort(is, "chargeGoal", setMax);
				// var1.setData(setMax);
				
				//If canActivate2 then 2, if canActivate then 1, else 0
				int actdata = eer.canActivate2() ? 2 : eer.canActivate() ? 1 : 0;
				mcItemStack.setData(mcItemStack.i() - (setMax * 10 + chargeTicks << actdata));
				return true;
			}
		} catch (Exception ex) {
			Log.Debug("Error: [EECharge thread] " + ex.getMessage());
			Log.debugEx(ex);
		}
		return false;
	}
	
	private boolean checkEEChargeSafeZone(@NonNull ItemStack is){
		if (!Threads.SSDechargeEE) return false;
		
		int id = is.getTypeId();
		TRItem g = ssDecharged.get(id);
		if (g == null) return false;
		
		try {
			net.minecraft.server.ItemStack mcItemStack = ((CraftItemStack) is).getHandle();
			
			if (!(mcItemStack.getItem() instanceof ItemEECharged)) return false;
			if (getShort(is, "chargeGoal") > 0 || getShort(is, "chargeLevel") > 0) {
				setShort(is, "chargeLevel", 0);
				setShort(is, "chargeGoal", 0);
				mcItemStack.setData(200);
				return true;
			}
		} catch (Exception ex) {
			Log.Debug("SSDisableItem[9] Error! " + ex.getMessage());
			Log.debugEx(ex);
		}
		return false;
	}
	
	private boolean checkEEArcanaSafeZone(ItemStack is){
		if (!Threads.SSDisableArcane) return false;
		
		int id = is.getTypeId();
		if (id != 27584) return false;
		
		try {
			net.minecraft.server.ItemStack mcItemStack = ((CraftItemStack) is).getHandle();
			if (mcItemStack.getData() != 6 || !getString(is, "mode").equalsIgnoreCase("earth")) {
				setString(is, "mode", "earth");
				mcItemStack.setData(6);
				return true;
			}
		} catch (Exception ex) {
			Log.Debug("SSDisableArcane[2] Error! " + ex.getMessage());
			Log.debugEx(ex);
		}
		return false;
	}
	
	private void load(){ reload(); }
	public void reload() {
		if (ssDecharged == null) ssDecharged = new ConcurrentHashMap<Integer, TRItem>();
		else ssDecharged.clear();
		
		if (mCharges == null) mCharges = new ConcurrentHashMap<Integer, TRCharge>();
		else mCharges.clear();
		
		if (maxEU == null) maxEU = new ConcurrentHashMap<Integer, TRCharge>();
		else maxEU.clear();
		
		//this.throttle = tekkitrestrict.config.getBoolean("ThrottleInventoryThread");
		
		List<String> dechargeSS = tekkitrestrict.config.getStringList(ConfigFile.ModModifications, "DechargeInSS");
		for (String s : dechargeSS) {
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(s);
			} catch (TRException ex) {
				Warning.config("You have an error in your ModModifications.config in DechargeInSS:", false);
				Warning.config(ex.toString(), false);
				continue;
			}
			
			for (TRItem iss1 : iss) {
				ssDecharged.put(iss1.id, iss1);
			}
		}

		List<String> meu = tekkitrestrict.config.getStringList(ConfigFile.ModModifications, "MaxEU");
		for (String s : meu) {
			if (!s.contains(" ")){
				Warning.config("You have an error in your ModModifications.config in MaxEU!", false);
				Warning.config("Invalid number of arguments in \""+s+"\". Required: 3", false);
				continue;
			}
			
			String[] sseu = s.split(" ");
			if (sseu.length != 3){
				Warning.config("You have an error in your ModModifications.config in MaxEU!", false);
				Warning.config("Invalid number of arguments in \""+s+"\". Required: 3", false);
				continue;
			}
			int eu, chrate;
			
			try {
				eu = Integer.parseInt(sseu[1]);
			} catch (NumberFormatException ex){
				Warning.config("You have an error in your ModModifications.config in MaxEU!", false);
				Warning.config("Invalid MaxEU value \""+sseu[1]+"\" in \""+s+"\"!", false);
				continue;
			}
			try {
				chrate = Integer.parseInt(sseu[2]);
			} catch (NumberFormatException ex){
				Warning.config("You have an error in your ModModifications.config in MaxEU!", false);
				Warning.config("Invalid charge rate \""+sseu[2]+"\" in \""+s+"\"!", false);
				continue;
			}

			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(sseu[0]);
			} catch (TRException ex) {
				Warning.config("You have an error in your ModModifications.config in MaxEU:", false);
				Warning.config(ex.toString(), false);
				continue;
			}
			for (TRItem iss1 : iss) {
				TRCharge gg = new TRCharge();
				gg.id = iss1.id;
				gg.data = iss1.data;
				gg.maxcharge = eu;
				gg.chargerate = chrate;
				maxEU.put(gg.id, gg);
			}
		}

		// process charges...
		List<String> MaxCharges = tekkitrestrict.config.getStringList(ConfigFile.ModModifications, "MaxCharge");
		for (String charge : MaxCharges) {
			if (!charge.contains(" ")) {
				Log.Warning.config("You have an error in your maxchare list in ModModifications.config: \""+charge+"\" does not follow the format: \"itemstr percentage\"", false);
				continue;
			}
			
			String[] sscharge = charge.replace("%", "").split(" ");
			
			int max = 0;
			try {
				max = Integer.parseInt(sscharge[1]);
			} catch (NumberFormatException ex){
				Warning.config("You have an error in your maxchare list in ModModifications.config: \""+sscharge[1]+"\" is not a valid number", false);
				continue;
			}
			
			List<TRItem> iss;
			try {
				iss = TRItemProcessor.processItemString(sscharge[0]);
			} catch (TRException ex) {
				Warning.config("You have an error in your ModModifications.config in MaxCharge:", false);
				Warning.config(ex.toString(), false);
				continue;
			}
			for (TRItem isr : iss) {
				TRCharge gg = new TRCharge();
				gg.id = isr.id;
				gg.data = isr.data;
				gg.maxcharge = max;
				mCharges.put(gg.id, gg);
			}
		}
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
	@Override
	public void run() {
		try {
			Thread.sleep(Threads.worldCleanerSpeed);//Don't trigger immediately, but sleep first.
		} catch (InterruptedException ex) {
			if (tekkitrestrict.disable) return; //If plugin is disabling, then stop the thread. The WorldScrubber thread shouldn't trigger again.
		}
		while (true) {
			doWScrub();

			try {
				Thread.sleep(Threads.worldCleanerSpeed);
			} catch (InterruptedException ex) {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The WorldScrubber thread shouldn't trigger again.
			}
		}
	}

	private boolean err1, err2;
	/**
	 * Runs TRChunkUnloader.unloadSChunks().<br>
	 * Then if UseRPTimer or RemoveDisabledBlocks is turned on, it will execute those features.
	 */
	private void doWScrub() {
		try {
			TRChunkUnloader.unloadSChunks();
		} catch (Exception ex) {
			if (!err1){
				Warning.other("An error occurred in the ChunkUnloader! (This error will only be logged once)", false);
				Log.Exception(ex, false);
				err1 = true;
			}
		}
		
		if (!Threads.RMDB) return;
		
		try {
			Server server = tekkitrestrict.getInstance().getServer();
			
			List<World> worlds = server.getWorlds();
			for (World bukkitWorld : worlds) { //For each world
				Chunk[] loadedChunks = bukkitWorld.getLoadedChunks();
				
				for (Chunk c : loadedChunks) { //For each loaded chunk
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 16; z++) {
							for (int y = 0; y < 256; y++) {
								Block bl = c.getBlock(x, y, z);
								if (TRNoItem.isBlockBanned(bl)) {
									bl.setTypeId(Threads.ChangeDisabledItemsIntoId);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			if (!err2){
				Warning.other("An error occurred in the BannedBlocksRemover! (This error will only be logged once)", false);
				Log.Exception(ex, false);
				err2 = true;
			}
		}
	}
}

class TSaveThread extends Thread {
	private boolean err1, err2, err3, err4;
	@Override
	public void run() {
		try {
			if (tekkitrestrict.disable) return; //If plugin is disabling, then stop the thread. The savethread triggers again if interrupted.
			Thread.sleep(Threads.saveSpeed);
		} catch (InterruptedException ex) {}
		while (true) {
			// runs save functions for both safezones and itemlimiter
			try {
				TRLimiter.saveLimiters();
			} catch (Exception ex) {
				if (!err1){
					Warning.other("An error occurred while trying to save the Limiter! (This error will only be logged once)", false);
					Log.Exception(ex, false);
					err1 = true;
				}
			}
			
			try {
				TRSafeZone.save();
			} catch (Exception ex) {
				if (!err2){
					Warning.other("An error occurred while trying to save the SafeZones! (This error will only be logged once)", false);
					Log.Exception(ex, false);
					err2 = true;
				}
			}

			try {
				TRLogger.saveLogs();
			} catch (Exception ex) {
				if (!err3){
					Warning.other("An error occurred while trying to save the logs! (This error will only be logged once)", false);
					Log.Exception(ex, false);
					err3 = true;
				}
			}
			try {
				TRNoHack.clearMaps();
			} catch (Exception ex) {}
			
			try {
				TRLimiter.manageData();
			} catch(Exception ex){
				if (!err4){
					Warning.other("An error occurred with the Limiter Data Manager! (This error will only be logged once)", false);
					Log.Exception(ex, false);
					err4 = true;
				}
			}

			try {
				if (tekkitrestrict.disable) break; //If plugin is disabling, then stop the thread. The savethread triggers again if interrupted.
				Thread.sleep(Threads.saveSpeed);
			} catch (InterruptedException e) {}
		}
	}
}
