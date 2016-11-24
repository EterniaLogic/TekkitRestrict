package nl.taico.tekkitrestrict.threads;

import ic2.common.ElectricItem;
import ic2.common.ItemArmorElectric;
import ic2.common.ItemElectricTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.minecraft.server.Item;
import net.minecraft.server.NBTTagCompound;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.NameProcessor;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor2;
import nl.taico.tekkitrestrict.TekkitRestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.TRConfigCache.SafeZones;
import nl.taico.tekkitrestrict.TRConfigCache.Threads;
import nl.taico.tekkitrestrict.config.SettingsStorage;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.functions.TRSafeZone;
import nl.taico.tekkitrestrict.objects.TRCharge;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TRItemStack;

import ee.ItemEECharged;

public class TRDisableItemsThread extends Thread {
	private ArrayList<Integer> ssDecharged = new ArrayList<Integer>();
	private HashMap<Integer, TRCharge> mCharges = new HashMap<Integer, TRCharge>();
	private HashMap<Integer, TRCharge> maxEU = new HashMap<Integer, TRCharge>();
	//private boolean reloading = false, busy = false;
	
	@Override
	public void run() {
		load();
		while (true) {
			//if (!reloading){
				try {
					// Disabled Items remover
					final Player[] players = Bukkit.getServer().getOnlinePlayers();
					//busy = true;
					for (final Player player : players) {
						try {
							disableItems(player);
						} catch (final Exception ex) {
							Warning.other("An error occured in [ItemDisabler thread] (1 player)!", false);
							Log.debugEx(ex);
						}
					}
				} catch (final Exception ex) {
					Warning.other("An error occured in [ItemDisabler thread] (player loop)!", false);
					Log.Exception(ex, true);
				}
				//busy = false;
			//}
			
			try {
				Thread.sleep(Threads.inventorySpeed);
			} catch (InterruptedException e) {
				if (TekkitRestrict.disable) break; //If plugin is disabling, then stop the thread. The disableItemsThread should not trigger again. (As all players will be gone on shutdown)
			}
		}
	}
	
	private boolean checkSS;
	private int err0 = 0, err1 = 0, err2 = 0;
	private void disableItems(final Player player) {
		if (player == null) return;
		final PlayerInventory inv = player.getInventory();
		//clone of items in inventory
		final ItemStack[] st1 = inv.getContents();

		// //////////// NORMAL INVENTORY
		boolean changedInv = false, changedArmor = false;
		boolean bypassn = player.hasPermission("tekkitrestrict.bypass.noitem");
		
		boolean isCreative = Listeners.UseLimitedCreative && (player.getGameMode() == GameMode.CREATIVE);
		boolean bypassc = isCreative && player.hasPermission("tekkitrestrict.bypass.creative");
		int inSafeZone;//-1 = bypass or disabled or (enabled and no safezone), 0 = enabled but unchecked, 1 = enabled and safezone here
		if (checkSS){
			if (player.hasPermission("tekkitrestrict.bypass.safezone")) inSafeZone = -1;
			else inSafeZone = 0;
		} else {
			inSafeZone = -1;
		}
		
		try {
			for (int i = 0; i < st1.length; i++) {
				final ItemStack item = st1[i];
				if (item == null) continue;
				
				final int id = item.getTypeId();
				final int data = item.getDurability();
				
				try {
					String banned = null;
					boolean c = false;
					if (isCreative){
						if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
						if (!bypassc && banned == null){
							banned = TRNoItem.isItemBannedInCreative(player, id, data, false);
							c = true;
						}
					} else {
						if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
					}
					
					if (banned != null) {
						if (banned.isEmpty()){
							if (!c) banned = ChatColor.RED + "Removed "+NameProcessor.getName(new TRItem(id, data))+" ("+id+":"+data+") from your inventory. Reason: This item is banned!";
							else banned = ChatColor.RED + "Removed "+NameProcessor.getName(new TRItem(id, data))+" ("+id+":"+data+") from your inventory. Reason: This item is banned in creative!";
						}
						TRItem.sendBannedMessage(player, banned);
						changedInv = true;
						st1[i] = new ItemStack(Threads.ChangeDisabledItemsIntoId, 1);
						continue; //Item is now dirt so continue with next one.
					}
					
					if (checkEEandIC2Charge(item, id)){
						changedInv = true;
						continue;
					}
					
					switch (inSafeZone){
						case -1: continue;
						case 0:
							if (TRItemStack.getMCItem(item) instanceof ItemEECharged){
								inSafeZone = (TRSafeZone.isSafeZoneFor(player, true, false)?1:-1);
								if (inSafeZone == -1) continue;
								if (checkSafeZone(item, id)){
									changedInv = true;
									continue;
								}
							}
							break;
						default:
							if (TRItemStack.getMCItem(item) instanceof ItemEECharged){
								if (checkSafeZone(item, id)){
									changedInv = true;
									continue;
								}
							}
					}
				} catch (Exception ex) {
					if (err0 > 50) continue;
					err0++;
					Warning.other("Error: [ItemDisabler thread] (check inv) " + ex.toString(), false);
					Log.debugEx(ex);
					if (err0 == 50) Warning.other("This error will not be logged again because it has occured 50 times!", false);
				}
			} //End of first for loop
			
			if (changedInv) inv.setContents(st1);
			
			// //////////// ARMOR INVENTORY
			//boolean changed1 = false;
			final ItemStack[] st2 = inv.getArmorContents();
			for (int i = 0; i < st2.length; i++) {
				final ItemStack item = st2[i];
				if (item == null) continue;
				final int id = item.getTypeId();
				final int data = item.getDurability();
				
				try {
					String banned = null;
					boolean c = false;
					if (isCreative){
						if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
						if (banned == null && !bypassc){
							banned = TRNoItem.isItemBannedInCreative(player, id, data, false);
							c = true;
						}
					} else {
						if (!bypassn) banned = TRNoItem.isItemBanned(player, id, data, false);
					}
					
					if (banned != null) {
						if (banned.isEmpty()){
							if (!c) banned = ChatColor.RED + "Removed "+NameProcessor.getName(new TRItem(id, data))+" ("+id+":"+data+") from your inventory. Reason: This item is banned!";
							else banned = ChatColor.RED + "Removed "+NameProcessor.getName(new TRItem(id, data))+" ("+id+":"+data+") from your inventory. Reason: This item is banned in creative!";
						}
						TRItem.sendBannedMessage(player, banned);
						changedArmor = true;
						st2[i] = new ItemStack(Threads.ChangeDisabledItemsIntoId, 1); //proceed to remove it.
						continue;
					}
					
					if (checkIC2Charge(st2[i], id)){
						changedArmor = true;
						continue;
					}
					
				} catch (Exception ex) {
					if (err1 > 50) continue;
					err1++;
					Warning.other("Error: [ItemDisabler thread] (check armor) " + ex.toString(), false);
					Log.debugEx(ex);
					if (err1 == 50) Warning.other("This error will not be logged again because it has occured 50 times!", false);
				}
			}
			
			// place new inventory back.
			
			if (changedArmor) inv.setArmorContents(st2);
			
		} catch (Exception ex) {
			if (err2 > 50) return;
			err2++;
			Warning.other("Error: [ItemDisabler thread] " + ex.toString(), false);
			Log.debugEx(ex);
			if (err2 == 50) Warning.other("This error will not be logged again because it has occured 50 times!", false);
		}
	}
	
	private boolean checkEEandIC2Charge(final ItemStack is, final int id){
		final net.minecraft.server.ItemStack mcStack = TRItemStack.getMCStack(is);

		final Item si = mcStack.getItem();
		if (TekkitRestrict.EEEnabled){
			if (si instanceof ItemEECharged){
				final TRCharge g = mCharges.get(id);
				if (g == null) return false;
				
				try {
					final ItemEECharged eer = (ItemEECharged) si;
					//double maxEE = eer.getMaxCharge();//1, 2, 3, etc.
					final int setMax = (int) Math.round(((eer.getMaxCharge() / 100.000) * g.maxcharge));

					if (getShort(mcStack, "chargeGoal") > setMax || getShort(mcStack, "chargeLevel") > setMax) {
						setShort(mcStack, "chargeLevel", setMax);
						setShort(mcStack, "chargeGoal", setMax);
						
						//MaxDMG - (max*10 + charge << (If canActivate2 then 2, if canActivate then 1, else 0)
						mcStack.setData(mcStack.i() - (setMax * 10 + getShort(mcStack, "chargeTicks") << (eer.canActivate2()?2:eer.canActivate()?1:0)));
						return true;
					}
				} catch (final Exception ex) {
					Log.debug("Error: [EECharge thread] " + ex.getMessage());
					Log.debugEx(ex);
				}
				return false;
			}
		}
		
		final TRCharge s = maxEU.get(id);
		if (s == null) return false;
		
		if (si instanceof ItemArmorElectric) {
			final ItemArmorElectric ci = (ItemArmorElectric) si;
			if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
				final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcStack);

				ci.maxCharge = s.maxcharge;
				ci.transferLimit = s.chargerate;
				tag.setInt("charge", ((tag.getInt("charge") * s.maxcharge) / ci.maxCharge));

				ElectricItem.charge(mcStack, 10, 9999, true, false);

				return true;
			}
			return false;
		} else if (si instanceof ItemElectricTool) {
			final ItemElectricTool ci = (ItemElectricTool) si;
			if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
				final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcStack);

				ci.maxCharge = s.maxcharge;
				ci.transferLimit = s.chargerate;
				tag.setInt("charge", ((tag.getInt("charge") * s.maxcharge) / ci.maxCharge));

				ElectricItem.charge(mcStack, 10, 9999, true, false);

				return true;
			}
			return false;
		} else if (si instanceof ElectricItem) {
			final ElectricItem ci = (ElectricItem) si;
			if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
				final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcStack);

				ci.maxCharge = s.maxcharge;
				ci.transferLimit = s.chargerate;
				tag.setInt("charge", ((tag.getInt("charge") * s.maxcharge) / ci.maxCharge));

				ElectricItem.charge(mcStack, 10, 9999, true, false);

				return true;
			}
			return false;
		}
		
		return false;
	}

	private boolean checkIC2Charge(final ItemStack is, final int id){
		final TRCharge s = maxEU.get(id);
		if (s == null) return false;
		
		try {
			final net.minecraft.server.ItemStack mcStack = ((CraftItemStack) is).getHandle();

			final Item si = mcStack.getItem();
			if (si instanceof ItemArmorElectric) {
				final ItemArmorElectric ci = (ItemArmorElectric) si;
				if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
					final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcStack);
					
					ci.maxCharge = s.maxcharge;
					ci.transferLimit = s.chargerate;
					tag.setInt("charge", ((tag.getInt("charge") * s.maxcharge) / ci.maxCharge));

					ElectricItem.charge(mcStack, 10, 9999, true, false);

					return true;
				}
				return false;
			} else if (si instanceof ItemElectricTool) {
				final ItemElectricTool ci = (ItemElectricTool) si;
				if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
					final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcStack);

					ci.maxCharge = s.maxcharge;
					ci.transferLimit = s.chargerate;
					tag.setInt("charge", ((tag.getInt("charge") * s.maxcharge) / ci.maxCharge));

					ElectricItem.charge(mcStack, 10, 9999, true, false);

					return true;
				}
				return false;
			} else if (si instanceof ElectricItem) {
				final ElectricItem ci = (ElectricItem) si;
				if (ci.maxCharge != s.maxcharge || ci.transferLimit != s.chargerate) {
					final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcStack);

					ci.maxCharge = s.maxcharge;
					ci.transferLimit = s.chargerate;
					tag.setInt("charge", ((tag.getInt("charge") * s.maxcharge) / ci.maxCharge));

					ElectricItem.charge(mcStack, 10, 9999, true, false);

					return true;
				}
				return false;
			}
		} catch (final Exception ex) {
			Log.debug("Error: [IC2 Decharger[7] thread] " + ex.toString());
			Log.debugEx(ex);
		}
		return false;
	}
	
	/*
	private boolean checkEECharge(ItemStack is, int id){
		if (!tekkitrestrict.EEEnabled) return false;
		
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
	
	private boolean checkEEChargeSafeZone(ItemStack is, int id){
		if (!Threads.SSDechargeEE) return false;
		try {
			net.minecraft.server.ItemStack mcItemStack = ((CraftItemStack) is).getHandle();
			
			if (!(mcItemStack.getItem() instanceof ItemEECharged)) return false;
			
			TRItem g = ssDecharged.get(id);
			if (g == null) return false;
			
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
	
	private boolean checkEEArcanaSafeZone(ItemStack is, int id){
		if (!Threads.SSDisableArcane) return false;
		
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
	
	*/
	private boolean checkSafeZone(final ItemStack is, final int id){
		final net.minecraft.server.ItemStack mcItemStack = ((CraftItemStack) is).getHandle();
		//if (!(mcItemStack.getItem() instanceof ItemEECharged)) return false; already checked by thread
		
		if (id == 27584){
			if (!Threads.SSDisableArcane) return false;
			if (mcItemStack.getData() != 6 || !getString(mcItemStack, "mode").equalsIgnoreCase("earth")) {
				setString(mcItemStack, "mode", "earth");
				mcItemStack.setData(6);
				return true;
			}
			return false;
		}
		
		if (!Threads.SSDechargeEE || !ssDecharged.contains(id)) return false;
		
		if (getShort(mcItemStack, "chargeGoal") > 0 || getShort(mcItemStack, "chargeLevel") > 0) {
			setShort(mcItemStack, "chargeLevel", 0);
			setShort(mcItemStack, "chargeGoal", 0);
			mcItemStack.setData(200);
			return true;
		}
		
		return false;
	}
	
	private void load(){ reload(); }
	public void reload() {
		//reloading = true;
		//int i = 0;
		//while (busy){
		//	if (i == Threads.inventorySpeed+1000) break;
		//	try {
		//		Thread.sleep(1);
		//		i++;
		//	} catch (Exception ex){}
		//}
		Log.trace("Loading Disabled Items Thread...");
		if (!SafeZones.UseSafeZones || !TekkitRestrict.EEEnabled || (!Threads.SSDechargeEE && !Threads.SSDisableArcane)) checkSS = false;
		else checkSS = true;
		
		{
			final ArrayList<Integer> temp = new ArrayList<Integer>();
			final List<String> dechargeSS = SettingsStorage.modModificationsConfig.getStringList("DechargeInSS");
			Log.trace("DisabledItemsThread - Loading DechargeInSS...");
			for (final String s : dechargeSS) {
				final List<TRItem> iss;
				try {
					iss = TRItemProcessor2.processString(s);
				} catch (TRException ex) {
					Warning.config("You have an error in your ModModifications.config in DechargeInSS:", false);
					Warning.config(ex.toString(), false);
					continue;
				}
				
				for (final TRItem iss1 : iss) temp.add(iss1.id);
				
			}
			
			ssDecharged = temp;
		}
		
		{
			final HashMap<Integer, TRCharge> temp = new HashMap<Integer, TRCharge>();
			final List<String> meu = SettingsStorage.modModificationsConfig.getStringList("MaxEU");
			Log.trace("DisabledItemsThread - Loading MaxEU...");
			for (final String s : meu) {
				if (!s.contains(" ")){
					Warning.config("You have an error in your ModModifications.config in MaxEU!", false);
					Warning.config("Invalid number of arguments in \""+s+"\". Required: 3", false);
					continue;
				}
				
				final String[] sseu = s.split(" ");
				if (sseu.length != 3){
					Warning.config("You have an error in your ModModifications.config in MaxEU!", false);
					Warning.config("Invalid number of arguments in \""+s+"\". Required: 3", false);
					continue;
				}
				final int eu, chrate;
				
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
	
				final List<TRItem> iss;
				try {
					iss = TRItemProcessor2.processString(sseu[0]);
				} catch (TRException ex) {
					Warning.config("You have an error in your ModModifications.config in MaxEU:", false);
					Warning.config(ex.toString(), false);
					continue;
				}
				for (final TRItem iss1 : iss) {
					final TRCharge gg = new TRCharge();
					gg.id = iss1.id;
					gg.data = iss1.data;
					gg.maxcharge = eu;
					gg.chargerate = chrate;
					temp.put(gg.id, gg);
				}
			}
			
			maxEU = temp;
		}
		
		{
			// process charges...
			final HashMap<Integer, TRCharge> temp = new HashMap<Integer, TRCharge>();
			
			final List<String> MaxCharges = SettingsStorage.modModificationsConfig.getStringList("MaxCharge");
			Log.trace("DisabledItemsThread - Loading MaxCharge...");
			for (final String charge : MaxCharges) {
				if (!charge.contains(" ")) {
					Log.Warning.config("You have an error in your maxchare list in ModModifications.config: \""+charge+"\" does not follow the format: \"itemstr percentage\"", false);
					continue;
				}
				
				final String[] sscharge = charge.replace("%", "").split(" ");
				
				final int max;
				try {
					max = Integer.parseInt(sscharge[1]);
				} catch (NumberFormatException ex){
					Warning.config("You have an error in your maxchare list in ModModifications.config: \""+sscharge[1]+"\" is not a valid number", false);
					continue;
				}
				
				final List<TRItem> iss;
				try {
					iss = TRItemProcessor2.processString(sscharge[0]);
				} catch (TRException ex) {
					Warning.config("You have an error in your ModModifications.config in MaxCharge:", false);
					Warning.config(ex.toString(), false);
					continue;
				}
				for (final TRItem isr : iss) {
					final TRCharge gg = new TRCharge();
					gg.id = isr.id;
					gg.data = isr.data;
					gg.maxcharge = max;
					temp.put(gg.id, gg);
				}
			}
			mCharges = temp;
		}
		
		//reloading = false;
	}

	/**
	 * Gets the short value for the given key.<br>
	 * If the item doesn't have a tag it will add one.<br>
	 * If the item doesn't have a value for the specified key it will make it and set it to 0.
	 */
	public short getShort(final ItemStack bukkitItemStack, final String key) {
		final NBTTagCompound tag = TRItemStack.getTagOrCreate(bukkitItemStack);
		
		//if (!tag.hasKey(key)) tag.setShort(key, (short) 0);
		
		return tag.getShort(key);
	}
	/**
	 * Gets the short value for the given key.<br>
	 * If the item doesn't have a tag it will add one.<br>
	 * If the item doesn't have a value for the specified key it will make it and set it to 0.
	 */
	public short getShort(final net.minecraft.server.ItemStack mcItemStack, final String key) {
		final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcItemStack);
		
		//if (!tag.hasKey(key)) tag.setShort(key, (short) 0);
		
		return tag.getShort(key);
	}
	/**
	 * Sets a short value from the given key.<br>
	 * If the item doesn't have a tag it will add one.
	 */
	public void setShort(final ItemStack bukkitItemStack, final String key, final int value) {
		final NBTTagCompound tag = TRItemStack.getTagOrCreate(bukkitItemStack);
		
		tag.setShort(key, (short) value);
	}
	/**
	 * Sets a short value from the given key.<br>
	 * If the item doesn't have a tag it will add one.
	 */
	public void setShort(final net.minecraft.server.ItemStack mcItemStack, final String key, final int value) {
		final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcItemStack);
		
		tag.setShort(key, (short) value);
	}
	
	/**
	 * Gets the string value for the given key.<br>
	 * If the item doesn't have a tag it will add one.<br>
	 * If the item doesn't have a value for the specified key it will make it and set it to "".
	 */
	public String getString(final ItemStack bukkitItemStack, final String key) {
		final NBTTagCompound tag = TRItemStack.getTagOrCreate(bukkitItemStack);
		
		//if (!tag.hasKey(key)) tag.setString(key, "");
		
		return tag.getString(key);
	}
	/**
	 * Gets the string value for the given key.<br>
	 * If the item doesn't have a tag it will add one.<br>
	 * If the item doesn't have a value for the specified key it will make it and set it to "".
	 */
	public String getString(final net.minecraft.server.ItemStack mcItemStack, final String key) {
		final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcItemStack);
		
		//if (!tag.hasKey(key)) tag.setString(key, "");
		
		return tag.getString(key);
	}
	/**
	 * Sets the string value for the given key.<br>
	 * If the item doesn't have a tag it will add one.
	 */
	public void setString(final ItemStack bukkitItemStack, final String key, final String value) {
		final NBTTagCompound tag = TRItemStack.getTagOrCreate(bukkitItemStack);
		
		tag.setString(key, value);
	}
	/**
	 * Sets the string value for the given key.<br>
	 * If the item doesn't have a tag it will add one.
	 */
	public void setString(final net.minecraft.server.ItemStack mcItemStack, final String key, final String value) {
		final NBTTagCompound tag = TRItemStack.getTagOrCreate(mcItemStack);
		
		tag.setString(key, value);
	}
}
