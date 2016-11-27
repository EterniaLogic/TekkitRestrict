package ee;

import org.bukkit.entity.Player;

import net.minecraft.server.*;

public class ContainerRMFurnace extends Container {

	private TileRMFurnace furnace;
	private int cookTime;
	private int burnTime;
	private int itemBurnTime;
	private boolean initialized;

	public ContainerRMFurnace(IInventory var1, TileRMFurnace var2) {
		cookTime = 0;
		burnTime = 0;
		itemBurnTime = 0;
		furnace = var2;
		setPlayer(((PlayerInventory) var1).player);
		
		//Slot(IInventory, index, xpos, ypos)
		//Slot 0 at [65,53]
		a(new Slot(var2, 0, 65, 53));
		
		//Slot 1 at [65,17]
		a(new Slot(var2, 1, 65, 17));
		
		//Slot 2 at [11,8]
		//Slot 3 at [11,26]
		//Slot 4 at [11,44]
		//Slot 5 at [11,62]
		
		//Slot 6 at [29,8]
		//Slot 7 at [29,26]
		//Slot 8 at [29,44]
		//Slot 9 at [29,62]
		
		//Slot 10 at [40,8]
		//Slot 11 at [40,26]
		//Slot 12 at [40,44]
		//Slot 13 at [40,62]
		for (int var3 = 0; var3 <= 2; var3++) {
			for (int var4 = 0; var4 <= 3; var4++) {
				a(new Slot(var2, var3 * 4 + var4 + 2, 11 + var3 * 18, 8 + var4 * 18));
			}
		}
		
		//Slot 14 at [125,35]
		
		//Slot 15 at [147,8]
		//Slot 16 at [147,26]
		//Slot 17 at [147,44]
		//Slot 18 at [147,62]
		
		//Slot 19 at [165,8]
		//Slot 20 at [165,26]
		//Slot 21 at [165,44]
		//Slot 22 at [165,62]
		
		//Slot 23 at [183,8]
		//Slot 24 at [183,26]
		//Slot 25 at [183,44]
		//Slot 26 at [183,62]
		a(new Slot(var2, 14, 125, 35));
		for (int var3 = 0; var3 <= 2; var3++) {
			for (int var4 = 0; var4 <= 3; var4++) {
				a(new Slot(var2, var3 * 4 + var4 + 15, 147 + var3 * 18, 8 + var4 * 18));
			}

		}

		for (int var3 = 0; var3 < 3; var3++) {
			for (int var4 = 0; var4 < 9; var4++) {
				a(new Slot(var1, var4 + var3 * 9 + 9, 24 + var4 * 18, 84 + var3 * 18));
			}

		}

		//Slot 0-8, [24-168,142] ???
		for (int var3 = 0; var3 < 9; var3++) {
			a(new Slot(var1, var3, 24 + var3 * 18, 142));
		}

	}

	public IInventory getInventory() {
		return furnace;
	}

	// Update tick
	public void a() {
		super.a();
		for (int var1 = 0; var1 < listeners.size(); var1++) {
			ICrafting var2 = (ICrafting) listeners.get(var1);
			if (cookTime != furnace.furnaceCookTime || !initialized) var2.setContainerData(this, 0, furnace.furnaceCookTime);
			if (burnTime != furnace.furnaceBurnTime || !initialized) var2.setContainerData(this, 1, furnace.furnaceBurnTime);
			if (itemBurnTime != furnace.currentItemBurnTime || !initialized) var2.setContainerData(this, 2, furnace.currentItemBurnTime);
		}

		cookTime = furnace.furnaceCookTime;
		burnTime = furnace.furnaceBurnTime;
		itemBurnTime = furnace.currentItemBurnTime;
		initialized = true;
	}

	public void updateProgressBar(int type, int time) {
		if (type == 0)
			furnace.furnaceCookTime = time;
		else if (type == 1)
			furnace.furnaceBurnTime = time;
		else if (type == 2)
			furnace.currentItemBurnTime = time;
	}

	@SuppressWarnings("deprecation")
	public ItemStack a(int slotnr) {
		Slot slot = (Slot) e.get(slotnr);
		
		//slot.c()=Does slot has stack?
		if (slot == null || !slot.c()) return null;

		ItemStack item = slot.getItem();
		
		//a(ItemStack, int, int, boolean) merges the itemstack with the first one available in the furnace.
		if (slotnr >= 14 && slotnr <= 26) {//Furnace right
			if (!a(item, 27, 62, true)) {
				if (item.count == 0) slot.set(null);
				return null;
			}
		} else if (slotnr >= 27 && slotnr <= 53) {//Inventory
			if (furnace.getItemBurnTime(item, true) > 0) {
				//if (!a(item, 0, 0, true)) {
				//	Util.log("!a(item, 0, 0, true)");
				//	if (item.count == 0) slot.set(null);
				//	return null;
				//}				
				Slot slot0 = getSlot(0);
				
				if (slot0 == null) return null; //Shouldn't occur.
				
				ItemStack item0 = slot0.getItem();
				
				if (item0 == null){
					slot0.set(item.cloneItemStack());
					slot.set(null);
					slot.d();
					slot0.d();
					EntityHuman human = this.getPlayer();
					if (human != null){
						((Player) human.getBukkitEntity()).updateInventory();
					}
					return null;
				} else {
					if (item0.id == item.id && item0.getData() == item.getData()){
						ItemStack item0clone = item0.cloneItemStack();
						ItemStack itemclone = item.cloneItemStack();
						while(item0clone.count < item0clone.getMaxStackSize() && itemclone.count > 0){
							itemclone.count = itemclone.count-1;
							item0clone.count = item0clone.count+1;
						}
						slot0.set(item0clone);
						if (itemclone.count == 0) slot.set(null);
						else slot.set(itemclone);
						slot.d();
						slot0.d();
						EntityHuman human = this.getPlayer();
						if (human != null){
							((Player) human.getBukkitEntity()).updateInventory();
						}
						return null;
					}
				}
			} else {
				//Try to put the item in slot 1-13.
				if (!a(item, 1, 13, false)) {
					if (item.count == 0) slot.set(null);
					return null;
				}
				//Try to put the item in the hotbar
				if (!a(item, 54, 62, false)) {
					if (item.count == 0) slot.set(null);
					return null;
				}
			}
		} else if (slotnr >= 54 && slotnr < 63) {//Hotbar
			if (furnace.getItemBurnTime(item, true) > 0) {
				//if (!a(item, 0, 0, true)) {
				//	if (item.count == 0) slot.set(null);
				//	return null;
				//}
				Slot slot0 = getSlot(0);
				
				if (slot0 == null) return null; //Shouldn't occur.
				
				ItemStack item0 = slot0.getItem();
				
				if (item0 == null){
					slot0.set(item.cloneItemStack());
					slot.set(null);
					slot.d();
					slot0.d();
					EntityHuman human = this.getPlayer();
					if (human != null){
						((Player) human.getBukkitEntity()).updateInventory();
					}
					return null;
				} else {
					if (item0.id == item.id && item0.getData() == item.getData()){
						ItemStack item0clone = item0.cloneItemStack();
						ItemStack itemclone = item.cloneItemStack();
						while(item0clone.count < item0clone.getMaxStackSize() && itemclone.count > 0){
							itemclone.count = itemclone.count-1;
							item0clone.count = item0clone.count+1;
						}
						slot0.set(item0clone);
						if (itemclone.count == 0) slot.set(null);
						else slot.set(itemclone);
						slot.d();
						slot0.d();
						EntityHuman human = this.getPlayer();
						if (human != null){
							((Player) human.getBukkitEntity()).updateInventory();
						}
						return null;
					}
				}
			} else {
				if (!a(item, 1, 13, false)) {
					if (item.count == 0) slot.set(null);
					return null;
				}
				if (!a(item, 27, 53, false)) {
					if (item.count == 0) slot.set(null);
					return null;
				}
			}
		} else if (!a(item, 27, 62, false)) {//Furnace left
			if (item.count == 0) slot.set(null);
			return null;
		}
		
		//slot.d() = onSlotChanged();
		//Is called when the stack in a Slot changes.
		//Calls inventory.update();
		if (item.count == 0) slot.set(null);
		else slot.d();
		
		ItemStack var2 = item.cloneItemStack();
		if (item.count == var2.count) return null;
		
		//Dead code. item.count is always var2.count.
		slot.c(item);//put item in the slot.
		return var2;
	}

	public void addSlotListener(ICrafting icrafting) {
		super.addSlotListener(icrafting);
		icrafting.setContainerData(this, 0, furnace.furnaceCookTime);
		icrafting.setContainerData(this, 1, furnace.furnaceBurnTime);
		icrafting.setContainerData(this, 2, furnace.currentItemBurnTime);
	}

	public boolean b(EntityHuman var1) {
		return furnace.a(var1);
	}

}