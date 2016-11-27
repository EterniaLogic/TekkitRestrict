package ee;

import net.minecraft.server.Container;
import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.Slot;

public class ContainerTransmutation extends Container {
	private EntityHuman player;
	private TransTabletData transGrid;
	private int latentEnergy;
	private int currentEnergy;
	private int learned;
	private int lock;
	private boolean initialized;

	public ContainerTransmutation(PlayerInventory var1, EntityHuman human, TransTabletData transData) {
		player = human;
		setPlayer(human);
		transGrid = transData;
		learned = transData.learned;
		lock = transData.isMatterLocked() ? 1 : transData.isFuelLocked() ? 2 : 0;
		latentEnergy = transData.getLatentEnergy();
		currentEnergy = transData.getCurrentEnergy();
		a(new SlotTransmuteInput(transData, 0, 43, 29));
		a(new SlotTransmuteInput(transData, 1, 34, 47));
		a(new SlotTransmuteInput(transData, 2, 52, 47));
		a(new SlotTransmuteInput(transData, 3, 16, 56));
		a(new SlotTransmuteInput(transData, 4, 70, 56));
		a(new SlotTransmuteInput(transData, 5, 34, 65));
		a(new SlotTransmuteInput(transData, 6, 52, 65));
		a(new SlotTransmuteInput(transData, 7, 43, 83));
		a(new SlotTransmuteInput(transData, 8, 158, 56));
		a(new SlotConsume(transData, 9, 107, 103));
		a(new SlotTransmute(transData, 10, 158, 15));
		a(new SlotTransmute(transData, 11, 140, 19));
		a(new SlotTransmute(transData, 12, 176, 19));
		a(new SlotTransmute(transData, 13, 123, 36));
		a(new SlotTransmute(transData, 14, 158, 37));
		a(new SlotTransmute(transData, 15, 193, 36));
		a(new SlotTransmute(transData, 16, 116, 56));
		a(new SlotTransmute(transData, 17, 139, 56));
		a(new SlotTransmute(transData, 18, 177, 56));
		a(new SlotTransmute(transData, 19, 199, 56));
		a(new SlotTransmute(transData, 20, 123, 76));
		a(new SlotTransmute(transData, 21, 158, 75));
		a(new SlotTransmute(transData, 22, 193, 76));
		a(new SlotTransmute(transData, 23, 140, 93));
		a(new SlotTransmute(transData, 24, 176, 93));
		a(new SlotTransmute(transData, 25, 158, 97));

		for (int var4 = 0; var4 < 3; var4++) {
			for (int var5 = 0; var5 < 9; var5++) {
				//0+0*9+9= 9
				//0+1*9+9= 18
				//0+2*9+9= 27
				//0+8*9+9=81
				//1+0*9+9=10
				a(new Slot(player.inventory, var5 + var4 * 9 + 9, 35 + var5 * 18, 123 + var4 * 18));
			}
		}

		for (int var4 = 0; var4 < 9; var4++) {
			a(new Slot(player.inventory, var4, 35 + var4 * 18, 181));
		}

		a(transGrid);
		EEBase.watchTransGrid(player);
	}

	public IInventory getInventory() {
		return transGrid;
	}

	/**
	 * Does not appear to be called.
	 * @see net.minecraft.server.Container#setItem(int, net.minecraft.server.ItemStack)
	 */
	public void setItem(int var1, ItemStack var2) {
		super.setItem(var1, var2);

		if (var1 < 26) {
			if (var2 == null) {
				transGrid.items[var1] = null;
			} else {
				transGrid.items[var1] = var2.cloneItemStack();
			}
		}

		a(transGrid);
	}

	
	/**
	 * Called when the inventory is opened.
	 * @see net.minecraft.server.Container#a(net.minecraft.server.IInventory)
	 */
	public void a(IInventory var1) {
		a();
		if (!EEProxy.isClient(EEProxy.theWorld)) {
			transGrid.update();
			transGrid.displayResults(transGrid.latentEnergy + transGrid.currentEnergy);
		}
	}


	public void a() {
		//Random r = new Random();
		//if (r.nextInt(21)!=20) return;
		super.a();

		for (int var1 = 0; var1 < listeners.size(); var1++) {
			ICrafting var2 = (ICrafting) listeners.get(var1);

			if (latentEnergy != transGrid.latentEnergy || !initialized) {
				var2.setContainerData(this, 0, transGrid.latentEnergy & 0xFFFF);
			}

			if (latentEnergy != transGrid.latentEnergy || !initialized) {
				var2.setContainerData(this, 1, transGrid.latentEnergy >>> 16);
			}

			if (currentEnergy != transGrid.currentEnergy || !initialized) {
				var2.setContainerData(this, 2, transGrid.currentEnergy & 0xFFFF);
			}

			if (currentEnergy != transGrid.currentEnergy || !initialized) {
				var2.setContainerData(this, 3, transGrid.currentEnergy >>> 16);
			}

			if (learned != transGrid.learned || !initialized) {
				var2.setContainerData(this, 4, transGrid.learned);
			}

			if (lock != (transGrid.isMatterLocked() ? 1 : transGrid.isFuelLocked() ? 2 : 0) || !initialized) {
				var2.setContainerData(this, 5, transGrid.isMatterLocked() ? 1 : transGrid.isFuelLocked() ? 2 : 0);
			}
		}

		learned = transGrid.learned;
		lock = transGrid.isMatterLocked() ? 1 : transGrid.isFuelLocked() ? 2 : 0;
		latentEnergy = transGrid.latentEnergy;
		currentEnergy = transGrid.currentEnergy;
		initialized = true;
	}

	
	/**
	 * Client side only
	 */
	public void updateProgressBar(int var1, int var2) {
		if (var1 == 0) transGrid.latentEnergy = transGrid.latentEnergy & 0xFFFF0000 | var2;
		else if (var1 == 1) transGrid.latentEnergy = transGrid.latentEnergy & 0xFFFF | var2 << 16;
		else if (var1 == 2) transGrid.currentEnergy = transGrid.currentEnergy & 0xFFFF0000 | var2;
		else if (var1 == 3) transGrid.currentEnergy = transGrid.currentEnergy & 0xFFFF | var2 << 16;
		else if (var1 == 4) transGrid.learned = var2;
		else if (var1 == 5) {
			if (var2 == 0) {
				transGrid.unlock();
			} else if (var2 == 1) {
				transGrid.fuelUnlock();
				transGrid.matterLock();
			} else if (var2 == 2) {
				transGrid.matterUnlock();
				transGrid.fuelLock();
			}
		}
	}

	/**
	 * Can interact with
	 * @see net.minecraft.server.Container#b(net.minecraft.server.EntityHuman)
	 */
	public boolean b(EntityHuman var1) {
		return true;
	}

	/**
	 * Called when the player closes his inventory.
	 * @see net.minecraft.server.Container#a(net.minecraft.server.EntityHuman)
	 */
	public void a(EntityHuman var1) {
		
		super.a(var1);
		EEBase.closeTransGrid(player);
		
//		if (player.world.isStatic) return;
//		for (int var2 = 0; var2 < 25; var2++) {
//			ItemStack var3 = transGrid.splitWithoutUpdate(var2);
//
//			if (var3 != null) {
//				player.drop(var3);
//			}
//		}
		
	}
	
	/**
	 * merges provided ItemStack with the first available one in the container/player inventory
	 * @param flag If false, goes from i to j, if true, goes from j to i.
	 * @return If the item was successfully put in one of the given slots.
	 * @see net.minecraft.server.Container#a(net.minecraft.server.ItemStack, int, int, boolean)
	 */
	protected boolean a(ItemStack itemstack, int i, int j, boolean flag){
		return super.a(itemstack, i, j, flag);
	}

	/**
	 * For shift clicking
	 * @return The item obtained from the clicked slot.
	 * @see net.minecraft.server.Container#a(int)
	 */
	public ItemStack a(int slotNr) {
		Slot slot = (Slot) e.get(slotNr);
		if (slot == null) return null;
		
		ItemStack item = slot.getItem();
		if (item == null) return null;//if slot has no item, return no item.
		
		ItemStack cloneOfItemInOutputSlot = null;

		if (slotNr > 9 && slotNr < 26) {//output slots
			cloneOfItemInOutputSlot = item.cloneItemStack();
		}

		ItemStack tbr = item.cloneItemStack();

		//if shiftclicking on InputSlot
		if (slotNr <= 8) {
			//if there is no spot free in the inventory, set the slot to null
			if (!a(item, 26, 62, true)) {//from hotbar to rest of inventory
				slot.set(null);//item becomes null
			}
		}
		//output slots
		else if (slotNr > 9 && slotNr < 26) {
			//if the item cannot be put into the inventory of the player, STILL set it to null.
			if (!grabResult(item, slot, 26, 62, false)) {//if the player cannot take this item right now, set the contents of the slot to null (item disappears on shift click with full inventory)
				slot.set(null);
			}
			
			//FIXME should it not return null here? Item in slot is set to null, player was not allowed to grab this item.
		}
		//Handles clicks from the player inventory into the given slot, for shift clicks.
		else if (slotNr >= 26 && slotNr < 62) {//player inventory slots
			if ((EEMaps.getEMC(item) > 0 || EEBase.isKleinStar(item)) && !a(item, 0, 8, false)) {//if has emc and cannot merge with input slots
				if (item.count <= 0) {
					slot.set(null);
				}

				return null;
			}
		}
		//if slot == 9 or not available, return nothing
		else if (!a(item, 26, 62, false)) {
			if (item.count <= 0) {
				slot.set(null);
			}

			return null;
		}

		if (item.count <= 0) {
			if (slotNr > 9 && slotNr < 26) {//output slots
				item.count = 1;//sets count back to 1 whenever item is taken out. - FIXME dupe can happen here
			} else {
				slot.set(null);
			}
		} else {
			slot.d();//update slot
		}
		
		if (slotNr < 26) transGrid.calculateEMC();

		if (item.count == tbr.count) {//if the item was unchanged
			if (slotNr > 9 && slotNr < 26 && cloneOfItemInOutputSlot != null) {
				return cloneOfItemInOutputSlot;
			}

			return null;
		}

		if (slotNr > 9 && slotNr < 26 && transGrid.latentEnergy + transGrid.currentEnergy < EEMaps.getEMC(item)) {
			return null;
		}

		slot.c(item);
		

		if (cloneOfItemInOutputSlot != null && slotNr > 9 && slotNr < 26) {
			slot.set(cloneOfItemInOutputSlot);
		}

		return tbr;
	}

	/**
	 * @param item The item the player is taking
	 * @param slot The slot
	 * @param i 
	 * @param j
	 * @param reversed If true, looks from j to i instead of from i to j.
	 * @return If the item was successfully put away in a slot between i and j
	 */
	protected boolean grabResult(ItemStack item, Slot slot, int i, int j, boolean reversed) {
		if (transGrid.latentEnergy + transGrid.currentEnergy < EEMaps.getEMC(item)) return false;

		slot.c(item);//update slot
		boolean flag = false;
		int k = i;

		if (reversed) {
			k = j - 1;
		}

		//Tries to find places to stack the item
		if (item.isStackable()) {
			while (item.count > 0 && ((!reversed && k < j) || (reversed && k >= i))) {
				Slot curSlot = (Slot) e.get(k);
				ItemStack curItem = curSlot.getItem();

				if (curItem != null && curItem.id == item.id && (!item.usesData() || item.getData() == curItem.getData()) && ItemStack.equals(item, curItem)) {//if the current item is the same as the given item
					int amount = curItem.count + item.count;

					if (amount <= item.getMaxStackSize()) {
						item.count = 0;//set inputitem count to 0 (breaks loop)
						curItem.count = amount;//The item in the slot is 
						curSlot.d();//update
						flag = true;
					} else if (curItem.count < item.getMaxStackSize()) {
						item.count -= item.getMaxStackSize() - curItem.count;
						curItem.count = item.getMaxStackSize();
						curSlot.d();//update
						flag = true;
					}
				}

				if (reversed) {
					k--;
				} else {
					k++;
				}
			}
		}

		//Tries to find an empty spot to put the item
		if (item.count > 0) {
			int k2;
			if (reversed) {
				k2 = j - 1;
			} else {
				k2 = i;
			}

			while ((!reversed && k2 < j) || (reversed && k2 >= i)) {
				Slot curSlot = (Slot) e.get(k2);
				ItemStack curItem = curSlot.getItem();

				if (curItem == null) {
					curSlot.set(item.cloneItemStack());
					curSlot.d();//update
					item.count = 0;
					flag = true;
					break;
				}

				if (reversed) {
					k2--;
				} else {
					k2++;
				}
			}
		}

		item.count = 1;
		return flag;
	}

	/**
	 * @param clickType 0 = left click, 1 = right click, 2 = middle mouse
	 * @see net.minecraft.server.Container#clickItem(int, int, boolean, net.minecraft.server.EntityHuman)
	 * Container.slotClick(int, int, boolean, human) <br>(Container.clickItem(int, int, boolean, human))
	 */
	public ItemStack clickItem(int slotNr, int clickType, boolean shift, EntityHuman human) {
		ItemStack var5 = null;
		if (clickType > 1) {
			return null;
		}

		if (clickType == 0 || clickType == 1) {
			PlayerInventory inventory = human.inventory;

			if (slotNr == -999) {
				if (inventory.getCarried() != null) {
					if (clickType == 0) {
						human.drop(inventory.getCarried());
						inventory.setCarried(null);
					}

					if (clickType == 1) {
						human.drop(inventory.getCarried().a(1));//takes one of the stack being held

						if (inventory.getCarried().count == 0) inventory.setCarried(null);
						
					}
				}
			} else if (shift) {
				ItemStack var7 = a(slotNr);

				if (var7 != null) {
					int var8 = var7.id;
					var5 = var7.cloneItemStack();
					Slot slot = (Slot) e.get(slotNr);
					
					if (slot != null && slot.getItem() != null && slot.getItem().id == var8 && slot.getItem().isStackable()) {
						retrySlotClick(slotNr, clickType, 1, slot.getItem().getMaxStackSize(), shift, human);
					}
				}
			} else {
				if (slotNr < 0) return null;

				Slot slot = (Slot) e.get(slotNr);

				if (slot != null) {
					slot.d();
					ItemStack currentItem = slot.getItem();
					ItemStack heldItem = inventory.getCarried();

					if (currentItem != null) {
						var5 = currentItem.cloneItemStack();
					}

					if (currentItem == null) {//if there is nothing in the slot yet.
						//putting in an item
						if (heldItem != null && slot.isAllowed(heldItem)) {
							int amount = (clickType == 0 ? heldItem.count : 1);

							if (amount > slot.a()) amount = slot.a();//if bigger than the max stack size, amount is the max stack size.
							
							slot.set(heldItem.a(amount));//set the contents of the slot to what was taken from the held item.

							if (heldItem.count == 0) {
								inventory.setCarried(null);
							}
						}
					}
					//If nothing is being held (taking out an item) AND there is something in the clicked slot
					else if (heldItem == null) {
						if (slotNr < 10 || slotNr > 25 || transGrid.latentEnergy + transGrid.currentEnergy >= EEMaps.getEMC(currentItem)){
							int amount = (clickType == 0 ? currentItem.count : (currentItem.count + 1) / 2);
							ItemStack var11 = slot.a(amount);//take amount from the clicked slot
							inventory.setCarried(var11);//put it in hand
	
							if (slotNr >= 10 && slotNr <= 25) {//if one of the output slots
								slot.set(new ItemStack(var11.id, 1, var11.getData()));//set new stack in output slot - FIXME dupe can happen here
							} else if (currentItem.count == 0) {
								slot.set(null);
							}
	
							slot.c(inventory.getCarried());//update
						}
					}
					//if input or inventory slot (!output slots)
					else if (slot.isAllowed(heldItem)) {
						//if the stacks match, merge them
						if (currentItem.id == heldItem.id && (!currentItem.usesData() || currentItem.getData() == heldItem.getData()) && ItemStack.equals(currentItem, heldItem)) {
							int amount = (clickType == 0 ? heldItem.count : 1);

							//if it doesnt fit, make it fit
							if (amount > slot.a() - currentItem.count) {
								amount = slot.a() - currentItem.count;
							}

							if (amount > heldItem.getMaxStackSize() - currentItem.count) {
								amount = heldItem.getMaxStackSize() - currentItem.count;
							}

							heldItem.a(amount);

							if (heldItem.count == 0) {
								inventory.setCarried(null);
							}

							currentItem.count += amount;
						}
						//if they dont match, swap them.
						else if (heldItem.count <= slot.a()) {
							slot.set(heldItem);
							inventory.setCarried(currentItem);
						}
					}
					//if not allowed to put the item in that slot AND they match (if you put it in output slot)
					else if (currentItem.id == heldItem.id && heldItem.getMaxStackSize() > 1 && (!currentItem.usesData() || currentItem.getData() == heldItem.getData()) && ItemStack.equals(currentItem, heldItem)) {
						int amount = currentItem.count;
						
						if (amount > 0 && amount + heldItem.count <= heldItem.getMaxStackSize()) {
							if (slotNr < 10 || slotNr > 25 || transGrid.latentEnergy + transGrid.currentEnergy >= EEMaps.getEMC(currentItem)){
								heldItem.count += amount;
								
								if (slotNr < 10 || slotNr > 25) {
									currentItem.a(amount);
	
									if (currentItem.count == 0) {
										slot.set(null);
									}
								}
								
								slot.c(inventory.getCarried());
							}
						}
					}
					if (slotNr < 26){
						
						
						//if (var5 != null){
							

							if (transGrid.currentEnergy == 0) transGrid.unlock();
							
							transGrid.calculateEMC();
							//transGrid.displayResults(transGrid.currentEnergy + transGrid.latentEnergy);
							//transGrid.displayResultsNoUpdate(transGrid.currentEnergy + transGrid.latentEnergy);
						//}
						
					}
				}
			}
		}

		return var5;
	}

	protected void retrySlotClick(int slotNr, int clickType, int var3, int maxStackSize, boolean shift, EntityHuman human) {
		if (var3 < maxStackSize) {
			var3++;
			slotClick(slotNr, clickType, var3, maxStackSize, shift, human);
		}
	}

	
	public ItemStack slotClick(int slotNr, int clickType, int var3, int maxStackSize, boolean shift, EntityHuman human) {
		ItemStack var7 = null;

		if (clickType > 1) return null;
		
		if (clickType == 0 || clickType == 1) {
			PlayerInventory inv = human.inventory;

			if (slotNr == -999) {
				if (inv.getCarried() != null && slotNr == -999) {
					if (clickType == 0) {
						human.drop(inv.getCarried());
						inv.setCarried(null);
					}

					if (clickType == 1) {
						human.drop(inv.getCarried().a(1));

						if (inv.getCarried().count == 0) {
							inv.setCarried(null);
						}
					}
				}
			} else if (shift) {
				ItemStack var9 = a(slotNr);

				if (var9 != null) {
					int var10 = var9.id;
					var7 = var9.cloneItemStack();
					Slot var11 = (Slot) e.get(slotNr);

					if (var11 != null && var11.getItem() != null && var11.getItem().id == var10) {
						retrySlotClick(slotNr, clickType, var3, maxStackSize, shift, human);
					}
				}
			} else {
				if (slotNr < 0) {
					return null;
				}

				Slot slot = (Slot) e.get(slotNr);

				if (slot != null) {
					slot.d();
					ItemStack currentItem = slot.getItem();
					ItemStack heldItem = inv.getCarried();

					if (currentItem != null) {
						var7 = currentItem.cloneItemStack();
					}

					if (currentItem == null) {
						if (heldItem != null && slot.isAllowed(heldItem)) {
							int var12 = clickType != 0 ? 1 : heldItem.count;

							if (var12 > slot.a()) {
								var12 = slot.a();
							}

							slot.set(heldItem.a(var12));

							if (heldItem.count == 0) {
								inv.setCarried(null);
							}
						}
					} else if (heldItem == null) {
						if (slotNr < 10 || slotNr > 25 || transGrid.latentEnergy + transGrid.currentEnergy >= EEMaps.getEMC(currentItem)){
							int var12 = clickType != 0 ? (currentItem.count + 1) / 2 : currentItem.count;
							
							ItemStack var13 = slot.a(var12);
							inv.setCarried(var13);
	
							if (slotNr >= 10 && slotNr <= 25) {
								slot.set(new ItemStack(var13.id, 1, var13.getData()));
							} else if (currentItem.count == 0) {
								slot.set(null);
							}
	
							slot.c(inv.getCarried());
						}
					} else if (slot.isAllowed(heldItem)) {
						if (currentItem.id == heldItem.id && (!currentItem.usesData() || currentItem.getData() == heldItem.getData()) && ItemStack.equals(currentItem, heldItem)) {
							int var12 = clickType != 0 ? 1 : heldItem.count;

							if (var12 > slot.a() - currentItem.count) {
								var12 = slot.a() - currentItem.count;
							}

							if (var12 > heldItem.getMaxStackSize() - currentItem.count) {
								var12 = heldItem.getMaxStackSize() - currentItem.count;
							}

							heldItem.a(var12);

							if (heldItem.count == 0) {
								inv.setCarried(null);
							}

							currentItem.count += var12;
						} else if (heldItem.count <= slot.a()) {
							slot.set(heldItem);
							inv.setCarried(currentItem);
						}
					} else if (currentItem.id == heldItem.id && heldItem.getMaxStackSize() > 1 && (!currentItem.usesData() || currentItem.getData() == heldItem.getData()) && ItemStack.equals(currentItem, heldItem)) {
						int amount = currentItem.count;

						if (amount > 0 && amount + heldItem.count <= heldItem.getMaxStackSize()) {
							if (slotNr < 10 || slotNr > 25 || transGrid.latentEnergy + transGrid.currentEnergy >= EEMaps.getEMC(currentItem)){
								heldItem.count += amount;
	
								if (slotNr < 10 || slotNr > 25) {
									currentItem.a(amount);
	
									if (currentItem.count == 0) {
										slot.set(null);
									}
								}
	
								slot.c(inv.getCarried());
							}
						}
					}
					//transGrid.quickCalculateEMC();
				}
			}
		}

		return var7;
	}
}