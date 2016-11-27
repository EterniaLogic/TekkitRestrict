package ee;

import buildcraft.api.ISpecialInventory;
import buildcraft.api.Orientations;
import ee.core.GuiIds;
import ee.events.EEEventManager;
import ee.events.EEDuplicateEvent;
import ee.events.EEEnums.DuplicateType;
import forge.ISidedInventory;

import java.util.Random;

import org.bukkit.entity.HumanEntity;

import net.minecraft.server.*;

public class TileRMFurnace extends TileEE implements ISpecialInventory, ISidedInventory, IEEPowerNet
{
	private ItemStack items[] = new ItemStack[27];
	public int furnaceBurnTime = 0;
	public int currentItemBurnTime = 0;
	public int furnaceCookTime = 0;
	public int nextinstack;
	public int nextoutstack;
	private float woftFactor = 1.0F;

	@SuppressWarnings("unused")
	private boolean isChest(TileEntity var1)
	{
		return (var1 instanceof TileEntityChest) || (var1 instanceof TileAlchChest);
	}

	@SuppressWarnings({ "null", "unused" })
	public void onBlockRemoval()
	{
		for (HumanEntity h : getViewers()) h.closeInventory();
		for(int var1 = 0; var1 < getSize(); var1++)
		{
			ItemStack var2 = getItem(var1);
			if(var2 == null) continue;
			
			float var3 = world.random.nextFloat() * 0.8F + 0.1F;
			float var4 = world.random.nextFloat() * 0.8F + 0.1F;
			float var5 = world.random.nextFloat() * 0.8F + 0.1F;
			while(var2.count > 0) 
			{
				int var6 = world.random.nextInt(21) + 10;
				if(var6 > var2.count) var6 = var2.count;
				var2.count -= var6;
				EntityItem var7 = new EntityItem(world, x + var3, y + var4, z + var5, new ItemStack(var2.id, var6, var2.getData()));
				if(var7 == null) continue;
				
				float var8 = 0.05F;
				var7.motX = (float)world.random.nextGaussian() * var8;
				var7.motY = (float)world.random.nextGaussian() * var8 + 0.2F;
				var7.motZ = (float)world.random.nextGaussian() * var8;
				if(var7.itemStack.getItem() instanceof ItemKleinStar)
					((ItemKleinStar)var7.itemStack.getItem()).setKleinPoints(var7.itemStack, ((ItemKleinStar)var2.getItem()).getKleinPoints(var2));
				world.addEntity(var7);
				
			}
			
		}

	}

	public int getSize()
	{
		return items.length;
	}
	public int getMaxStackSize()
	{
		return 64;
	}

	/**
	 * Get the item in the given slot.<br>
	 * For RM Furnace, slots are:<br>
	 * 02 06 10&nbsp;&nbsp;&nbsp;&nbsp;--&nbsp;&nbsp;&nbsp;&nbsp;15 19 23<br>
	 * 03 07 11&nbsp;&nbsp;&nbsp;&nbsp;01&nbsp;&nbsp;&nbsp;&nbsp;16 20 24<br>
	 * 04 08 12&nbsp;&nbsp;&nbsp;&nbsp;--&nbsp;&nbsp;&nbsp;&nbsp;17 21 25<br>
	 * 05 09 13&nbsp;&nbsp;&nbsp;&nbsp;00&nbsp;&nbsp;&nbsp;&nbsp;18 22 26<br>
	 */
	public ItemStack getItem(int slot)
	{
		return items[slot];
	}

	/**
	 * When you take out an item, splitStack is called.
	 * @param slot The slot to take the item out of.
	 * @param count The amount you want to take out of that slot.
	 */
	public ItemStack splitStack(int slot, int count)
	{
		if(items[slot] != null)
		{
			ItemStack var3;
			if(items[slot].count <= count)
			{
				var3 = items[slot];
				items[slot] = null;
				return var3;
			}
			var3 = items[slot].a(count);
			if(items[slot].count == 0) items[slot] = null;
			
			return var3;
		} else {
			return null;
		}
	}

	public void setItem(int slot, ItemStack var2)
	{
		items[slot] = var2;
		if(var2 != null && var2.count > getMaxStackSize()) var2.count = getMaxStackSize();
	}

	/**
	 * @param real If real is false, it only tests to see if an item can be added.<br>
	 * If an item can be added, adding the item is attempted again with real = true.
	 * @see buildcraft.api.ISpecialInventory#addItem(net.minecraft.server.ItemStack, boolean, buildcraft.api.Orientations)
	 */
	public boolean addItem(ItemStack item, boolean real, Orientations side)
	{
		if(item == null) return false;
		
		//If the inputitem is fuel
		if(getItemBurnTime(item, true) > 0 && item.id != Block.LOG.id)
		{
			//If there is no item in slot 0.
			if(items[0] == null) {
				if(real) {
					items[0] = item.cloneItemStack();//Put a clone of item in items[0].
					item.count = 0;
					//for(; item.count > 0; item.count--);
				}
				
				return true;
			}
			
			//If items[0] and inputitem are the same, and the stack in items[0] is not full, then add the stacks.
			if(items[0].doMaterialsMatch(item) && items[0].count < items[0].getMaxStackSize()) {
				if(real) {
					//Increase items[0] count by 1 and Lower inputitem count by 1.
					for(; items[0].count < items[0].getMaxStackSize() && item.count > 0; item.count--)
						items[0].count++;
				}
				return true;
			}
			
			return false;
		}

		if(FurnaceRecipes.getInstance().getSmeltingResult(item) == null) return false;
		
		//Try to put the itemstack in a free slot
		for(int slot = 1; slot <= 13; slot++)
		{
			if(items[slot] == null) {
				if(real) {
					items[slot] = item.cloneItemStack();
					item.count = 0;
					//for(; item.count > 0; item.count--);
				}

				return true;
			}
			
			//If the items dont match or if the amount is already at or above max, continue.
			if(!items[slot].doMaterialsMatch(item) || items[slot].count >= items[slot].getMaxStackSize()) continue;

			if(real)
			{
				for(; items[slot].count < items[slot].getMaxStackSize() && item.count > 0; item.count--)
					items[slot].count++;

				if(item.count != 0) continue;
			}

			return true;
		}

		return false;
	}

	/** 
	 * Called when an item is extracted by buildcraft pipes.<br>
	 * If extracted from the bottom, try extracting fuel.
	 * @see buildcraft.api.ISpecialInventory#extractItem(boolean, buildcraft.api.Orientations)
	 */
	public ItemStack extractItem(boolean remove, Orientations orientation)
	{
		switch(orientation) {
		case YNeg:
			if(items[0] == null) return null;
			
			if(items[0].getItem() instanceof ItemKleinStar)
			{
				ItemStack var5 = items[0].cloneItemStack();
				if(remove) items[0] = null;
				return var5;
			}
			// fall through
		case XNeg:
		case Unknown:
		case XPos:
		case YPos:
		case ZNeg:
		case ZPos:
			for(int var3 = 10; var3 < items.length; var3++) {
				if(items[var3] != null) {
					ItemStack var4 = items[var3].cloneItemStack();
					var4.count = 1;
					if(remove) {
						items[var3].count--;
						if(items[var3].count < 1) items[var3] = null;
					}
					return var4;
				}
			}

			// fall through
		}
		return null;
	}

	public String getName()
	{
		return "RM Furnace";
	}

	public void a(NBTTagCompound var1)
	{
		super.a(var1);
		NBTTagList var2 = var1.getList("Items");
		items = new ItemStack[getSize()];
		for(int var3 = 0; var3 < var2.size(); var3++)
		{
			NBTTagCompound var4 = (NBTTagCompound)var2.get(var3);
			byte var5 = var4.getByte("Slot");
			if(var5 >= 0 && var5 < items.length)
				items[var5] = ItemStack.a(var4);
		}

		woftFactor = var1.getFloat("TimeFactor");
		furnaceBurnTime = var1.getInt("BurnTime");
		furnaceCookTime = var1.getShort("CookTime");
		currentItemBurnTime = getItemBurnTime(items[1], false);
	}

	public void b(NBTTagCompound var1)
	{
		super.b(var1);
		var1.setInt("BurnTime", furnaceBurnTime);
		var1.setShort("CookTime", (short)furnaceCookTime);
		var1.setFloat("TimeFactor", woftFactor);
		NBTTagList var2 = new NBTTagList();
		for(int var3 = 0; var3 < items.length; var3++)
			if(items[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte)var3);
				items[var3].save(var4);
				var2.add(var4);
			}

		var1.set("Items", var2);
	}

	public int getCookProgressScaled(int var1)
	{
		return world == null || EEProxy.isClient(world) ? 0 : ((furnaceCookTime + (!isBurning() || !canSmelt() ? 0 : 1)) * var1) / 3;
	}

	public int getBurnTimeRemainingScaled(int var1)
	{
		if(currentItemBurnTime == 0)
			currentItemBurnTime = 10;
		return (furnaceBurnTime * var1) / currentItemBurnTime;
	}

	public boolean isBurning()
	{
		return furnaceBurnTime > 0;
	}

	/** Is called every tick. */
	public void q_()
	{
		if(clientFail()) return;
		
		woftFactor = EEBase.getPedestalFactor(world) * EEBase.getPlayerWatchFactor();
		float woft = getWOFTReciprocal(woftFactor);
		woft = (woft < 1.0F ? 1.0F : woft);
		boolean doUpdate = false;
		boolean notifyWorld = false;
		
		//If furnace is burning.
		if(furnaceBurnTime > 0) {
			furnaceBurnTime = (int)(furnaceBurnTime - woft);
			if(furnaceBurnTime <= 0)
			{
				furnaceBurnTime = 0;
				notifyWorld = true;
			}
		}
		
		if(!world.isStatic) {
			if(furnaceBurnTime <= 0 && canSmelt())
			{
				currentItemBurnTime = furnaceBurnTime = getItemBurnTime(items[0], false) / 48;
				if(furnaceBurnTime > 0)
				{
					doUpdate = true;
					notifyWorld = true;
					if(items[0] != null && !EEBase.isKleinStar(items[0])) {
						if(items[0].getItem().k())
							items[0] = new ItemStack(items[0].getItem().j());
						else
							items[0].count--;
						
						if(items[0].count <= 0) items[0] = null;
					}
				}
			}
			
			if(isBurning() && canSmelt()) {
				for(furnaceCookTime = (int)(furnaceCookTime + woft); furnaceCookTime >= 3 && canSmelt();)
				{
					furnaceCookTime -= 3;
					smeltItem();
					doUpdate = true;
					notifyWorld = true;
				}

			} else {
				for(int var4 = 15; var4 < 27; var4++) {
					if(items[var4] != null && items[var4].count >= items[var4].getMaxStackSize() && tryDropInChest(new ItemStack(items[var4].getItem(), items[var4].count, items[var4].getData())))
						items[var4] = null;
				}

				furnaceCookTime = 0;
				furnaceBurnTime = 0;
			}
		}
		
		if(doUpdate) update();
		if(notifyWorld) world.notify(x, y, z);
	}

	private boolean canSmelt()
	{
		if(items[1] == null)
		{
			for(int slot = 2; slot <= 13; slot++)
			{
				if(items[slot] == null) continue;
				items[1] = items[slot].cloneItemStack();
				items[slot] = null;
				break;
			}

			if(items[1] == null) return false;
				
		}
		
		ItemStack var3 = FurnaceRecipes.getInstance().getSmeltingResult(items[1]);
		if(var3 == null) return false;
		
		if(items[14] == null) return true;
		
		if(!items[14].doMaterialsMatch(var3))
		{
			if(tryDropInChest(items[14].cloneItemStack()))
			{
				items[14] = null;
				return true;
			}
			
			for(int var2 = 15; var2 <= 26; var2++)
			{
				if(items[var2] == null)
				{
					items[var2] = items[14].cloneItemStack();
					items[14] = null;
					return true;
				}
				if(items[var2].doMaterialsMatch(items[14])) {
					while(items[14] != null && items[var2].count < 64) 
					{
						items[14].count--;
						items[var2].count++;
						if(items[14].count == 0)
						{
							items[14] = null;
							return true;
						}
					}
				}
			}
		}
		
		if(items[14].count < getMaxStackSize() && items[14].count < items[14].getMaxStackSize()){
			return true;
		}
		for(int var2 = 15; var2 < 27; var2++) {
			if(items[var2] != null && items[var2].count >= items[var2].getMaxStackSize() && tryDropInChest(items[var2].cloneItemStack()))
				items[var2] = null;
		}

		if(items[14] == null) return true;
		
		for(int var2 = 15; var2 <= 26; var2++)
		{
			if(items[var2] == null)
			{
				items[var2] = items[14].cloneItemStack();
				items[14] = null;
				return true;
			}
			
			if(items[var2].doMaterialsMatch(items[14]))
				while(items[14] != null && items[var2].count < 64) 
				{
					items[14].count--;
					items[var2].count++;
					if(items[14].count <= 0)
					{
						items[14] = null;
						return true;
					}
				}
		}
		return items[14].count < var3.getMaxStackSize();
	}

	public void smeltItem()
	{
		if (!canSmelt()) return;
		
		ItemStack var1 = FurnaceRecipes.getInstance().getSmeltingResult(items[1]);
		boolean var2 = false;
		if(items[14] == null) {
			items[14] = var1.cloneItemStack();
			if(EEMaps.isOreBlock(items[1].id) && !EEEventManager.callEvent(new EEDuplicateEvent(items[1], DuplicateType.RMFurnace))) items[14].count++;
		} else if(items[14].id == var1.id) {
			items[14].count += var1.count;
			if(EEMaps.isOreBlock(items[1].id) && !EEEventManager.callEvent(new EEDuplicateEvent(items[1], DuplicateType.RMFurnace))) {
				if(items[14].count < var1.getMaxStackSize())
					items[14].count++;
				else
					var2 = true;
			}
		}
		
		if(items[14].count == var1.getMaxStackSize())
		{
			if(tryDropInChest(items[14]))
			{
				items[14] = null;
				
				if(var2) items[14] = var1.cloneItemStack();
			} else {
				for(int var3 = 15; var3 <= 26; var3++)
				{
					if(items[var3] != null) continue;
					items[var3] = items[14].cloneItemStack();
					items[14] = null;
					
					if(var2) items[14] = var1.cloneItemStack();
					
					break;
				}

			}
		}
		
		if(items[1].getItem().k()) {
			items[1] = new ItemStack(items[1].getItem().j());
		} else {
			items[1].count--;
		}
		
		if(items[1].count < 1) items[1] = null;
		world.notify(x, y, z);
	}

	/**
	 * Returns the period of time something burns for.
	 * @return 0 if the item == null.
	 */
	@SuppressWarnings("deprecation")
	public int getItemBurnTime(ItemStack var1, boolean dontUseKSPoints)
	{
		if(var1 == null) return 0;
		
		int id = var1.getItem().id;
		if(EEBase.isKleinStar(id) && EEBase.takeKleinStarPoints(var1, dontUseKSPoints ? 0 : 32, world)) {
			return 1600;
		} else {
			if (id == EEItem.mobiusFuel.id) return 102400;
			else if (id == EEItem.aeternalisFuel.id) return 409600;
			else if (id == EEItem.alchemicalCoal.id) return 25600;
			else if (id == Item.COAL.id) return 6400;
			else if (id == Item.LAVA_BUCKET.id) return 3200;
			else if (id == Item.STICK.id) return 100;
			else if (id == Block.SAPLING.id) return 100;
			else if (id >= 256 || Block.byId[id].material == Material.WOOD) return 300;
			else return ModLoader.addAllFuel(id, var1.getData());
		}
	}

	public void f() {}
	public void g() {}

	public boolean a(EntityHuman var1)
	{
		return world.getTileEntity(x, y, z) == this ? var1.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64D : false;
	}

	public int getStartInventorySide(int var1)
	{
		return var1 != 1 ? 1 : 0;
	}
	public int getSizeInventorySide(int var1)
	{
		return var1 != 1 ? 26 : 1;
	}

	public boolean onBlockActivated(EntityHuman var1)
	{
		if(!world.isStatic) var1.openGui(mod_EE.getInstance(), GuiIds.RM_FURNACE, world, x, y, z);
		return true;
	}

	public int getTextureForSide(int var1)
	{
		byte var2 = direction;
		return var1 != var2 ? EEBase.rmBlockSide : EEBase.rmFurnaceFront;
	}

	public int getInventoryTexture(int var1)
	{
		return var1 != 3 ? EEBase.rmBlockSide : EEBase.rmFurnaceFront;
	}

	public int getLightValue()
	{
		return isBurning() ? 15 : 0;
	}

	public void randomDisplayTick(Random var1)
	{
		if(isBurning())
		{
			byte var2 = direction;
			float var3 = x + 0.5F;
			float var4 = y + 0.0F + (var1.nextFloat() * 6F) / 16F;
			float var5 = z + 0.5F;
			float var6 = 0.52F;
			float var7 = var1.nextFloat() * 0.6F - 0.3F;
			if(var2 == 4)
			{
				world.a("smoke", var3 - var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
				world.a("flame", var3 - var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
			} else
				if(var2 == 5)
				{
					world.a("smoke", var3 + var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
					world.a("flame", var3 + var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
				} else
					if(var2 == 2)
					{
						world.a("smoke", var3 + var7, var4, var5 - var6, 0.0D, 0.0D, 0.0D);
						world.a("flame", var3 + var7, var4, var5 - var6, 0.0D, 0.0D, 0.0D);
					} else
						if(var2 == 3)
						{
							world.a("smoke", var3 + var7, var4, var5 + var6, 0.0D, 0.0D, 0.0D);
							world.a("flame", var3 + var7, var4, var5 + var6, 0.0D, 0.0D, 0.0D);
						}
			for(int var8 = 0; var8 < 4; var8++)
			{
				double var9 = x + var1.nextFloat();
				double var11 = y + var1.nextFloat();
				double var13 = z + var1.nextFloat();
				double var15 = 0.0D;
				double var17 = 0.0D;
				double var19 = 0.0D;
				int var21 = var1.nextInt(2) * 2 - 1;
				var15 = (var1.nextFloat() - 0.5D) * 0.5D;
				var17 = (var1.nextFloat() - 0.5D) * 0.5D;
				var19 = (var1.nextFloat() - 0.5D) * 0.5D;
				if((world.getTypeId(x - 1, y, z) != EEBlock.eeStone.id || world.getData(x - 1, y, z) != 3) && (world.getTypeId(x + 1, y, z) != EEBlock.eeStone.id || world.getData(x + 1, y, z) != 3))
				{
					var9 = x + 0.5D + 0.25D * var21;
					var15 = var1.nextFloat() * 2.0F * var21;
				} else
				{
					var13 = z + 0.5D + 0.25D * var21;
					var19 = var1.nextFloat() * 2.0F * var21;
				}
				world.a("portal", var9, var11, var13, var15, var17, var19);
			}

		}
	}

	public boolean receiveEnergy(int var1, byte var2, boolean var3)
	{
		if(canSmelt())
		{
			if(var3) furnaceBurnTime += var1;
			return true;
		} else {
			return false;
		}
	}
	public boolean sendEnergy(int var1, byte var2, boolean var3)
	{
		return false;
	}
	public boolean passEnergy(int var1, byte var2, boolean var3)
	{
		return false;
	}

	public void sendAllPackets(int i) {}

	public int relayBonus()
	{
		return 0;
	}

	public ItemStack splitWithoutUpdate(int var1)
	{
		return null;
	}

	public ItemStack[] getContents()
	{
		return items;
	}

	public void setMaxStackSize(int i) {}
}