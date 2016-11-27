package ee;

import org.bukkit.entity.Player;

import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.Slot;

public class ContainerDMFurnace extends Container {
	private TileDMFurnace furnace;
	private int cookTime = 0;
	private int burnTime = 0;
	private int itemBurnTime = 0;
	private boolean initialized;

	public ContainerDMFurnace(IInventory var1, TileDMFurnace var2) {
		this.furnace = var2;
		setPlayer(((PlayerInventory) var1).player);
		a(new Slot(var2, 0, 49, 53));
		a(new Slot(var2, 1, 49, 17));

		for (int var3 = 0; var3 <= 1; var3++) {
			for (int var4 = 0; var4 <= 3; var4++) {
				a(new Slot(var2, var3 * 4 + var4 + 2, 13 + var3 * 18, 8 + var4 * 18));
			}
		}

		a(new Slot(var2, 10, 109, 35));

		for (int var3 = 0; var3 <= 1; var3++) {
			for (int var4 = 0; var4 <= 3; var4++) {
				a(new Slot(var2, var3 * 4 + var4 + 11, 131 + var3 * 18, 8 + var4 * 18));
			}
		}

		for (int var3 = 0; var3 < 3; var3++) {
			for (int var4 = 0; var4 < 9; var4++) {
				a(new Slot(var1, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
			}
		}

		for (int var3 = 0; var3 < 9; var3++) {
			a(new Slot(var1, var3, 8 + var3 * 18, 142));
		}
	}

	public IInventory getInventory() {
		return this.furnace;
	}

	public void a() {
		super.a();

		for (int var1 = 0; var1 < this.listeners.size(); var1++) {
			ICrafting var2 = (ICrafting) this.listeners.get(var1);

			if ((this.cookTime != this.furnace.furnaceCookTime) || (!(this.initialized))) {
				var2.setContainerData(this, 0, this.furnace.furnaceCookTime);
			}

			if ((this.burnTime != this.furnace.furnaceBurnTime) || (!(this.initialized))) {
				var2.setContainerData(this, 1, this.furnace.furnaceBurnTime);
			}

			if ((this.itemBurnTime == this.furnace.currentItemBurnTime) && (this.initialized)) continue;
			var2.setContainerData(this, 2, this.furnace.currentItemBurnTime);
		}

		this.cookTime = this.furnace.furnaceCookTime;
		this.burnTime = this.furnace.furnaceBurnTime;
		this.itemBurnTime = this.furnace.currentItemBurnTime;
		this.initialized = true;
	}

	public void updateProgressBar(int type, int var2) {
		if (type == 0) 
			this.furnace.furnaceCookTime = var2;

		if (type == 1) 
			this.furnace.furnaceBurnTime = var2;

		if (type == 2)
			this.furnace.currentItemBurnTime = var2;
	}

	@SuppressWarnings("deprecation")
	public ItemStack a(int slotnr) {
		Slot slot = getSlot(slotnr);

		if (slot == null || !slot.c()) return null;
		
		ItemStack item = slot.getItem();

		if (slotnr >= 10 && slotnr <= 18) {
			if (!(a(item, 19, 54, true))) {
				if (item.count == 0) slot.set(null);
				return null;
			}
		} else if (slotnr >= 19 && slotnr < 45) {
			if (furnace.getItemBurnTime(item) > 0) {
				Slot slot0 = getSlot(0);
				
				if (slot0 == null) return null; //Shouldn't occur.
				
				ItemStack item0 = slot0.getItem();
				
				if (item0 == null){
					slot0.set(item.cloneItemStack());
					slot.set(null);
					
					slot.d();
					slot0.d();
					
					EntityHuman human = this.getPlayer();
					if (human != null) ((Player) human.getBukkitEntity()).updateInventory();
					
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
						if (human != null) ((Player) human.getBukkitEntity()).updateInventory();
					
						return null;
					}
				}
				
			} else {
				if (!(a(item, 1, 9, false))) {
					if (item.count == 0) slot.set(null);
					return null;
				}

				if (!(a(item, 45, 54, false))) {
					if (item.count == 0) slot.set(null);
					return null;
				}
			}
		} else if ((slotnr >= 45) && (slotnr < 54)) {
			if (this.furnace.getItemBurnTime(item) > 0) {
				Slot slot0 = getSlot(0);
				
				if (slot0 == null) return null; //Shouldn't occur.
				
				ItemStack item0 = slot0.getItem();
				
				if (item0 == null){
					slot0.set(item.cloneItemStack());
					slot.set(null);
					
					slot.d();
					slot0.d();
					
					EntityHuman human = this.getPlayer();
					if (human != null) ((Player) human.getBukkitEntity()).updateInventory();
					
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
						if (human != null) ((Player) human.getBukkitEntity()).updateInventory();
						
						return null;
					}
				}
			} else {
				if (!(a(item, 1, 9, false))) {
					if (item.count == 0) slot.set(null);
					return null;
				}

				if (!(a(item, 19, 45, false))) {
					if (item.count == 0) slot.set(null);
					return null;
				}
			}
		} else if (!(a(item, 19, 54, false))) {
			if (item.count == 0) slot.set(null);
			return null;
		}

		if (item.count == 0) 
			slot.set(null);
		else
			slot.d();
		

		ItemStack var2 = item.cloneItemStack();
		if (item.count == var2.count) return null;
		
		slot.c(item);

		return var2;
	}

	public boolean b(EntityHuman var1) {
		return this.furnace.a(var1);
	}
}
