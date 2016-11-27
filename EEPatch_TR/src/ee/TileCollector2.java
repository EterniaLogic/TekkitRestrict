// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst space 
// Source File Name:   TileCollector2.java

package ee;

import org.bukkit.entity.HumanEntity;

import buildcraft.api.ISpecialInventory;
import buildcraft.api.Orientations;
import ee.core.GuiIds;
import forge.ISidedInventory;
import net.minecraft.server.*;

// Referenced classes of package ee:
//			TileEE, IEEPowerNet, ItemKleinStar, EEMaps, 
//			EEBase, EEItem

public class TileCollector2 extends TileEE
	implements ISpecialInventory, ISidedInventory, IEEPowerNet
{

	private ItemStack items[];
	public int currentSunStatus;
	public int collectorSunTime;
	private int accumulate;
	private float woftFactor;
	public int currentFuelProgress;
	public boolean isUsingPower;
	public int sunTimeScaled;
	public int kleinProgressScaled;
	public int kleinPoints;

	public TileCollector2()
	{
		items = new ItemStack[15];
		kleinPoints = 0;
		currentSunStatus = 1;
		collectorSunTime = 0;
		woftFactor = 1.0F;
		accumulate = 0;
		currentFuelProgress = 0;
		kleinProgressScaled = 0;
		sunTimeScaled = 0;
	}

	public int getSize()
	{
		return items.length;
	}

	@SuppressWarnings("null")
	public void onBlockRemoval()
	{
		for (HumanEntity h : getViewers()) h.closeInventory();
		for (int var1 = 0; var1 < getSize(); var1++) {
			ItemStack var2 = getItem(var1);
			if (var2 != null) {
				float var3 = world.random.nextFloat() * 0.8F + 0.1F;
				float var4 = world.random.nextFloat() * 0.8F + 0.1F;
				float var5 = world.random.nextFloat() * 0.8F + 0.1F;
				while (var2.count > 0) {
					int var6 = world.random.nextInt(21) + 10;
					if (var6 > var2.count) var6 = var2.count;
					var2.count -= var6;
					EntityItem var7 = new EntityItem(world, x + var3, y + var4, z + var5, new ItemStack(var2.id, var6, var2.getData()));
					if (var7 != null) {
						float var8 = 0.05F;
						var7.motX = (float) world.random.nextGaussian() * var8;
						var7.motY = (float) world.random.nextGaussian() * var8 + 0.2F;
						var7.motZ = (float) world.random.nextGaussian() * var8;
						if (var7.itemStack.getItem() instanceof ItemKleinStar)
							((ItemKleinStar) var7.itemStack.getItem()).setKleinPoints(var7.itemStack, ((ItemKleinStar) var2.getItem()).getKleinPoints(var2));
						world.addEntity(var7);
					}
				}
			}
		}
	}

	public int getMaxStackSize()
	{
		return 64;
	}

	public ItemStack getItem(int i)
	{
		return items[i];
	}

	public ItemStack splitStack(int i, int j)
	{
		if (items[i] != null)
		{
			if (items[i].count <= j)
			{
				ItemStack itemstack = items[i];
				items[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = items[i].a(j);
			if (items[i].count == 0)
				items[i] = null;
			return itemstack1;
		} else
		{
			return null;
		}
	}

	public void setItem(int i, ItemStack itemstack)
	{
		items[i] = itemstack;
		if (itemstack != null && itemstack.count > getMaxStackSize())
			itemstack.count = getMaxStackSize();
	}

	public boolean addItem(ItemStack var1, boolean var2, Orientations var3)
	{
		if (var1 == null) return false;
		if (EEMaps.isFuel(var1))
		{
			for (int var4 = 0; var4 <= items.length - 3; var4++)
			{
				if (items[var4] == null)
				{
					if (var2)
					{
						items[var4] = var1.cloneItemStack();
						for (; var1.count > 0; var1.count--);
					}
					return true;
				}
				if (!items[var4].doMaterialsMatch(var1) || items[var4].count >= items[var4].getMaxStackSize())
					continue;
				if (var2)
				{
					for (; items[var4].count < items[var4].getMaxStackSize() && var1.count > 0; var1.count--)
						items[var4].count++;

					if (var1.count != 0)
						continue;
				}
				return true;
			}

			return false;
		}
		if (!EEBase.isKleinStar(var1) || items[0] != null)
			return false;
		if (var2)
		{
			items[0] = var1.cloneItemStack();
			for (; var1.count > 0; var1.count--);
		}
		return true;
	}

	public ItemStack extractItem(boolean flag, Orientations orientations)
	{
		for (int i = 0; i < items.length; i++)
			if (items[i] != null && i != items.length - 1)
				if (i == 0)
				{
					if (EEBase.isKleinStar(items[i]))
					{
						ItemStack itemstack = items[i].cloneItemStack();
						if (flag)
							items[i] = null;
						return itemstack;
					}
				} else
				if (items[i].id == EEItem.aeternalisFuel.id || items[items.length - 1] != null && items[i].doMaterialsMatch(items[items.length - 1]))
				{
					ItemStack itemstack1 = items[i].cloneItemStack();
					itemstack1.count = 1;
					if (flag)
					{
						items[i].count--;
						if (items[i].count < 1)
							items[i] = null;
					}
					return itemstack1;
				}

		return null;
	}

	public String getName()
	{
		return "Energy Collector";
	}

	public void a(NBTTagCompound nbttagcompound)
	{
		super.a(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getList("Items");
		items = new ItemStack[getSize()];
		for (int i = 0; i < nbttaglist.size(); i++)
		{
			NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.get(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < items.length)
				items[byte0] = ItemStack.a(nbttagcompound1);
		}

		currentSunStatus = nbttagcompound.getShort("sunStatus");
		woftFactor = nbttagcompound.getFloat("timeFactor");
		accumulate = nbttagcompound.getInt("accumulate");
		collectorSunTime = nbttagcompound.getInt("sunTime");
	}

	public void b(NBTTagCompound nbttagcompound)
	{
		super.b(nbttagcompound);
		nbttagcompound.setInt("sunTime", collectorSunTime);
		nbttagcompound.setFloat("timeFactor", woftFactor);
		nbttagcompound.setInt("accumulate", accumulate);
		nbttagcompound.setShort("sunStatus", (short)currentSunStatus);
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < items.length; i++)
			if (items[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				items[i].save(nbttagcompound1);
				nbttaglist.add(nbttagcompound1);
			}

		nbttagcompound.set("Items", nbttaglist);
	}

	public int getSunProgressScaled(int i)
	{
		if (canUpgrade())
		{
			if (getFuelDifference() <= 0)
				return items[0] != null && EEBase.isKleinStar(items[0]) ? 24 : 0;
			if ((collectorSunTime * i) / (getFuelDifference() * 80) > 24)
				return 24;
			else
				return (collectorSunTime * i) / (getFuelDifference() * 80);
		} else
		{
			return 0;
		}
	}

	public boolean canUpgrade() {
		if (items[0] == null) {
			for (int var1 = items.length - 3; var1 >= 1; var1--) {
				if (items[var1] == null || items[items.length - 1] != null && items[var1].doMaterialsMatch(items[items.length - 1])
						|| !EEMaps.isFuel(items[var1]) || items[var1].getItem().id == EEItem.aeternalisFuel.id) continue;
				items[0] = items[var1].cloneItemStack();
				items[var1] = null;
				break;
			}

		}
		if (items[0] == null) {
			if (items[items.length - 2] == null) return false;
			if (EEMaps.isFuel(items[items.length - 2]) && items[items.length - 2].getItem().id != EEItem.aeternalisFuel.id) {
				items[0] = items[items.length - 2].cloneItemStack();
				items[items.length - 2] = null;
			}
		}
		if (items[0] == null) return false;
		if (EEBase.isKleinStar(items[0])) {
			if (EEBase.canIncreaseKleinStarPoints(items[0], world)) return true;
			if (items[items.length - 2] == null) {
				items[items.length - 2] = items[0].cloneItemStack();
				items[0] = null;
				return false;
			}
			for (int var1 = 1; var1 <= items.length - 3; var1++)
				if (items[var1] == null) {
					items[var1] = items[items.length - 2].cloneItemStack();
					items[items.length - 2] = items[0].cloneItemStack();
					items[0] = null;
					return false;
				}

		}
		return items[0].getItem().id == EEItem.aeternalisFuel.id || !EEMaps.isFuel(items[0]) ? items[0].getItem().id == EEItem.darkMatter.id : true;
	}

	public boolean receiveEnergy(int i, byte byte0, boolean flag)
	{
		if (!isUsingPower())
			return false;
		if (flag)
		{
			accumulate += i;
			return true;
		} else
		{
			return true;
		}
	}

	public boolean sendEnergy(int i, byte byte0, boolean flag)
	{
		TileEntity tileentity = world.getTileEntity(x + (byte0 == 5 ? -1 : ((byte)(byte0 == 4 ? 1 : 0))), y + (byte0 == 1 ? -1 : ((byte)(byte0 == 0 ? 1 : 0))), z + (byte0 == 3 ? -1 : ((byte)(byte0 == 2 ? 1 : 0))));
		if (tileentity == null) return false;
		return (tileentity instanceof IEEPowerNet) && ((IEEPowerNet)tileentity).receiveEnergy(i + ((IEEPowerNet)tileentity).relayBonus(), byte0, flag);
	}

	public void sendAllPackets(int i)
	{
		int j = 0;
		for (byte byte0 = 0; byte0 < 6; byte0++)
			if (sendEnergy(i, byte0, false))
				j++;

		if (j == 0)
		{
			if (collectorSunTime <= 0x249f00 - i)
				collectorSunTime += i;
			return;
		}
		int k = i / j;
		if (k < 1)
			return;
		for (byte byte1 = 0; byte1 < 6; byte1++)
			sendEnergy(k, byte1, true);

	}

	public boolean passEnergy(int i, byte byte0, boolean flag)
	{
		return false;
	}

	public int relayBonus()
	{
		return 0;
	}

	public int getRealSunStatus()
	{
		if (world == null)
		{
			System.out.println("World object is turning a null for collectors..");
			return 0;
		}
		if (world.worldProvider.d)
			currentSunStatus = 16;
		else
			currentSunStatus = world.getLightLevel(x, y + 1, z) + 1;
		return currentSunStatus;
	}

	public int getSunStatus(int i)
	{
		return (getRealSunStatus() * i) / 16;
	}

	public void q_()
	{
		if (clientFail() || world.isStatic) return;

		if (collectorSunTime < 0) collectorSunTime = 0;
		if (items[0] != null && (items[0].getItem() instanceof ItemKleinStar))
		{
			kleinProgressScaled = getKleinProgressScaled(48);
			kleinPoints = getKleinPoints(items[0]);
		}
		sunTimeScaled = getSunTimeScaled(48);
		currentFuelProgress = getSunProgressScaled(24);
		currentSunStatus = getSunStatus(12);
		isUsingPower = isUsingPower();
		for (int i = items.length - 3; i >= 2; i--)
			if (items[i] == null && items[i - 1] != null)
			{
				items[i] = items[i - 1].cloneItemStack();
				items[i - 1] = null;
			}

		woftFactor = EEBase.getPedestalFactor(world) * EEBase.getPlayerWatchFactor();
		if (isUsingPower())
		{
			collectorSunTime += getFactoredProduction();
			if (accumulate > 0)
			{
				collectorSunTime += accumulate;
				accumulate = 0;
			}
			if (EEBase.isKleinStar(items[0]))
			{
				for (int j = getFactoredProduction() * ItemKleinStar.getLevel_s(items[0]); j > 0 && collectorSunTime >= 80 && EEBase.addKleinStarPoints(items[0], 1, world); j--)
					collectorSunTime -= 80;

			} else
			{
				for (; getFuelDifference() > 0 && collectorSunTime >= getFuelDifference() * 80; uptierFuel())
					collectorSunTime -= getFuelDifference() * 80;

			}
		} else {
			if (accumulate > 0)
			{
				collectorSunTime += accumulate;
				accumulate = 0;
			}
			sendAllPackets(getFactoredProduction());
		}
	}

	private int getKleinPoints(ItemStack itemstack)
	{
		if (itemstack == null)
			return 0;
		if (itemstack.getItem() instanceof ItemKleinStar)
			return ((ItemKleinStar)itemstack.getItem()).getKleinPoints(itemstack);
		else
			return 0;
	}

	private int getSunTimeScaled(int i)
	{
		return (collectorSunTime * i) / 0x249f00;
	}

	private int getKleinProgressScaled(int i)
	{
		if (items[0] != null && (items[0].getItem() instanceof ItemKleinStar))
			return (((ItemKleinStar)items[0].getItem()).getKleinPoints(items[0]) * i) / ((ItemKleinStar)items[0].getItem()).getMaxPoints(items[0]);
		else
			return 0;
	}

	public int getFactoredProduction()
	{
		return (int)(getProduction() * getWOFTReciprocal(woftFactor));
	}

	public int getProduction()
	{
		return getRealSunStatus() * 3;
	}

	public boolean isUsingPower()
	{
		return canUpgrade() && canCollect();
	}

	private int getFuelDifference()
	{
		if (items[0] == null)
			return 0;
		else
			return getFuelLevel(getNextFuel(items[0])) - getFuelLevel(items[0]);
	}

	private int getFuelLevel(ItemStack itemstack)
	{
		return EEMaps.getEMC(itemstack);
	}

	private ItemStack getNextFuel(ItemStack itemstack)
	{
		int i = itemstack.id;
		int j = itemstack.getData();
		if (items[items.length - 1] == null)
		{
			if (EEMaps.isFuel(itemstack))
			{
				if (i == Item.COAL.id && j == 1)
					return new ItemStack(Item.REDSTONE.id, 1, 0);
				if (i == Item.REDSTONE.id)
					return new ItemStack(Item.COAL.id, 1, 0);
				if (i == Item.COAL.id)
					return new ItemStack(Item.SULPHUR.id, 1, 0);
				if (i == Item.SULPHUR.id)
					return new ItemStack(Item.GLOWSTONE_DUST.id, 1, 0);
				if (i == Item.GLOWSTONE_DUST.id)
					return new ItemStack(EEItem.alchemicalCoal.id, 1, 0);
				if (i == EEItem.alchemicalCoal.id)
					return new ItemStack(Item.BLAZE_POWDER.id, 1, 0);
				if (i == Item.BLAZE_POWDER.id)
					return new ItemStack(Block.GLOWSTONE.id, 1, 0);
				if (i == Block.GLOWSTONE.id)
					return new ItemStack(EEItem.mobiusFuel.id, 1, 0);
				if (i == EEItem.mobiusFuel.id)
					return new ItemStack(EEItem.aeternalisFuel.id, 1, 0);
			}
			return null;
		} else if (EEMaps.isFuel(items[items.length - 1])) {
			if (EEMaps.getEMC(i, j) < EEMaps.getEMC(items[items.length - 1].id, items[items.length - 1].getData()))
				return items[items.length - 1];
			else
				return null;
		} else {
			EntityItem entityitem = new EntityItem(world, x, y, z, items[items.length - 1].cloneItemStack());
			items[items.length - 1] = null;
			entityitem.pickupDelay = 10;
			world.addEntity(entityitem);
			return null;
		}
	}

	private boolean canCollect() {
		if (items[0] == null) {
			for (int var1 = 1; var1 <= items.length - 3; var1++) {
				if (items[var1] == null || items[items.length - 1] != null
						&& (items[items.length - 1] == null || !items[items.length - 1].doMaterialsMatch(items[var1]))) continue;
				items[0] = items[var1].cloneItemStack();
				items[var1] = null;
				break;
			}

			if (items[0] == null) return false;
		}
		if (EEBase.isKleinStar(items[0])) return true;
		if (getNextFuel(items[0]) == null) return false;
		ItemStack var3 = getNextFuel(items[0]).cloneItemStack();
		
		if (items[items.length - 2] == null) return true;
		if (!items[items.length - 2].doMaterialsMatch(var3)) {
			for (int var2 = 1; var2 <= items.length - 3; var2++) {
				if (items[var2] == null) {
					items[var2] = items[items.length - 2].cloneItemStack();
					items[items.length - 2] = null;
					return true;
				}
				if (items[var2].doMaterialsMatch(items[items.length - 2])){
					while (items[items.length - 2] != null && items[var2].count < 64) {
						items[items.length - 2].count--;
						items[var2].count++;
						if (items[items.length - 2].count == 0) {
							items[items.length - 2] = null;
							return true;
						}
					}
				}
			}

		}
		if (items[items.length - 2] != null && !items[items.length - 2].doMaterialsMatch(var3)) return false;
		if (items[items.length - 2].count < getMaxStackSize() && items[items.length - 2].count < items[items.length - 2].getMaxStackSize()) return true;
		for (int var2 = 1; var2 <= items.length - 2; var2++)
			if (items[var2] != null
					&& (items[var2].getItem().id == EEItem.mobiusFuel.id || items[items.length - 1] != null
							&& items[var2].doMaterialsMatch(items[items.length - 1])) && items[var2].count >= items[var2].getMaxStackSize()
					&& tryDropInChest(new ItemStack(items[var2].getItem(), items[var2].count))) items[var2] = null;

		if (items[items.length - 2] == null) return true;
		for (int var2 = 1; var2 <= items.length - 3; var2++) {
			if (items[var2] == null) {
				items[var2] = items[items.length - 2].cloneItemStack();
				items[items.length - 2] = null;
				return true;
			}
			if (items[var2].doMaterialsMatch(items[items.length - 2])) {
				while (items[items.length - 2] != null && items[var2].count < 64) {
					items[items.length - 2].count--;
					items[var2].count++;
					if (items[items.length - 2].count == 0) {
						items[items.length - 2] = null;
						return true;
					}
				}
			}
		}

		return items[items.length - 2].count < var3.getMaxStackSize();
	}

	public void uptierFuel() {
		if (canCollect() && getNextFuel(items[0]) != null) {
			ItemStack var1 = getNextFuel(items[0]).cloneItemStack();
			var1.count = 1;
			if (items[items.length - 2] == null) {
				if ((items[items.length - 1] == null || !var1.doMaterialsMatch(items[items.length - 1])) && var1.getItem() != EEItem.aeternalisFuel) items[items.length - 2] = var1
						.cloneItemStack();
				else if (!tryDropInChest(var1)) items[items.length - 2] = var1.cloneItemStack();
			} else if (items[items.length - 2].id == var1.id) {
				if (items[items.length - 2].count == var1.getMaxStackSize()) {
					if (items[items.length - 2].getItem().id != EEItem.aeternalisFuel.id
							&& (items[items.length - 1] == null || !items[items.length - 2].doMaterialsMatch(items[items.length - 1]))) {
						for (int var2 = 1; var2 <= items.length - 3; var2++) {
							if (items[var2] == null) {
								items[var2] = items[items.length - 2].cloneItemStack();
								items[items.length - 2] = null;
								break;
							}
							if (items[var2].doMaterialsMatch(items[items.length - 2]))
								while (items[var2].count < items[var2].getMaxStackSize() && items[items.length - 2] != null) {
									items[items.length - 2].count--;
									items[var2].count++;
									if (items[items.length - 2].count == 0) items[items.length - 2] = null;
								}
						}

					} else if (tryDropInChest(items[items.length - 2].cloneItemStack())) items[items.length - 2] = null;
				} else if ((items[items.length - 1] == null || !var1.doMaterialsMatch(items[items.length - 1])) && var1.getItem() != EEItem.aeternalisFuel) items[items.length - 2].count += var1.count;
				else if (!tryDropInChest(var1)) items[items.length - 2].count += var1.count;
			} else if ((items[items.length - 1] != null && var1.doMaterialsMatch(items[items.length - 1]) || var1.getItem() == EEItem.aeternalisFuel)
					&& tryDropInChest(items[items.length - 2].cloneItemStack())) items[items.length - 2] = null;
			if (items[0].getItem().k()) items[0] = new ItemStack(items[0].getItem().j());
			else items[0].count--;
			if (items[0].count <= 0) items[0] = null;
		}
	}

	public void f()
	{
	}

	public void g()
	{
	}

	public boolean a(EntityHuman entityhuman) {
		if (world.getTileEntity(x, y, z) != this) return false;
		return entityhuman.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64D;
	}

	public int getStartInventorySide(int i)
	{
		return i == 0 ? 0 : 1;
	}

	public int getSizeInventorySide(int i)
	{
		if (i == 0)
			return 1;
		else
			return items.length - 2;
	}

	public boolean onBlockActivated(EntityHuman entityhuman)
	{
		entityhuman.openGui(mod_EE.getInstance(), GuiIds.COLLECTOR_2, world, x, y, z);
		return true;
	}

	public int getTextureForSide(int i)
	{
		if (i == 1)
			return EEBase.collector2Top;
		byte byte0 = direction;
		if (i != byte0)
			return EEBase.collectorSide;
		else
			return EEBase.collectorFront;
	}

	public int getInventoryTexture(int i)
	{
		if (i == 1)
			return EEBase.collector2Top;
		if (i == 3)
			return EEBase.collectorFront;
		else
			return EEBase.collectorSide;
	}

	public int getLightValue()
	{
		return 11;
	}

	public void onNeighborBlockChange(int j)
	{
	}

	public ItemStack splitWithoutUpdate(int i)
	{
		return null;
	}

	public ItemStack[] getContents()
	{
		return items;
	}

	public void setMaxStackSize(int i)
	{
	}

}
