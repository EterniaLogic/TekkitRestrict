// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst space 
// Source File Name:   TileDMFurnace.java

package ee;

import buildcraft.api.ISpecialInventory;
import buildcraft.api.Orientations;
import ee.core.GuiIds;
import forge.ISidedInventory;

import java.util.Random;

import org.bukkit.entity.HumanEntity;

import net.minecraft.server.*;

// Referenced classes of package ee:
//			TileEE, IEEPowerNet, ItemKleinStar, TileAlchChest, 
//			EEBase, EEMaps, EEItem, EEBlock, 
//			BlockEEStone

public class TileDMFurnace extends TileEE
	implements ISpecialInventory, ISidedInventory, IEEPowerNet
{

	private ItemStack items[];
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int furnaceCookTime;
	public int nextinstack;
	public int nextoutstack;
	private float woftFactor;

	public TileDMFurnace()
	{
		items = new ItemStack[19];
		furnaceBurnTime = 0;
		currentItemBurnTime = 0;
		furnaceCookTime = 0;
		woftFactor = 1.0F;
	}

	@SuppressWarnings("null")
	public void onBlockRemoval()
	{
		for (HumanEntity h : getViewers()) h.closeInventory();
		for (int var1 = 0; var1 < getSize(); var1++)
		{
			ItemStack var2 = getItem(var1);
			if (var2 != null)
			{
				float var3 = world.random.nextFloat() * 0.8F + 0.1F;
				float var4 = world.random.nextFloat() * 0.8F + 0.1F;
				float var5 = world.random.nextFloat() * 0.8F + 0.1F;
				while (var2.count > 0) 
				{
					int var6 = world.random.nextInt(21) + 10;
					if (var6 > var2.count)
						var6 = var2.count;
					var2.count -= var6;
					EntityItem var7 = new EntityItem(world, x + var3, y + var4, z + var5, new ItemStack(var2.id, var6, var2.getData()));
					if (var7 != null)
					{
						float var8 = 0.05F;
						var7.motX = (float)world.random.nextGaussian() * var8;
						var7.motY = (float)world.random.nextGaussian() * var8 + 0.2F;
						var7.motZ = (float)world.random.nextGaussian() * var8;
						if (var7.itemStack.getItem() instanceof ItemKleinStar)
							((ItemKleinStar)var7.itemStack.getItem()).setKleinPoints(var7.itemStack, ((ItemKleinStar)var2.getItem()).getKleinPoints(var2));
						world.addEntity(var7);
					}
				}
			}
		}

	}

	@SuppressWarnings("unused")
	private boolean isChest(TileEntity var1)
	{
		return (var1 instanceof TileEntityChest) || (var1 instanceof TileAlchChest);
	}

	public int getSize()
	{
		return items.length;
	}

	public int getMaxStackSize()
	{
		return 64;
	}

	public ItemStack getItem(int var1)
	{
		return items[var1];
	}

	public ItemStack splitStack(int var1, int var2)
	{
		if (items[var1] != null)
		{
			ItemStack var3;
			if (items[var1].count <= var2)
			{
				var3 = items[var1];
				items[var1] = null;
				return var3;
			}
			var3 = items[var1].a(var2);
			if (items[var1].count == 0)
				items[var1] = null;
			return var3;
		} else
		{
			return null;
		}
	}

	public void setItem(int var1, ItemStack var2)
	{
		items[var1] = var2;
		if (var2 != null && var2.count > getMaxStackSize())
			var2.count = getMaxStackSize();
	}

	public boolean addItem(ItemStack var1, boolean var2, Orientations var3)
	{
		if (var1 == null)
			return false;
		if (getItemBurnTime(var1) > 0 && var1.id != Block.LOG.id)
		{
			if (items[0] == null)
			{
				if (var2)
				{
					items[0] = var1.cloneItemStack();
					for (; var1.count > 0; var1.count--);
				}
				return true;
			}
			if (items[0].doMaterialsMatch(var1) && items[0].count < items[0].getMaxStackSize())
			{
				if (var2)
					for (; items[0].count < items[0].getMaxStackSize() && var1.count > 0; var1.count--)
						items[0].count++;

				return true;
			}
			return false;
		}
		if (FurnaceRecipes.getInstance().getSmeltingResult(var1) == null)
			return false;
		for (int var4 = 1; var4 <= 9; var4++)
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

	public ItemStack extractItem(boolean var1, Orientations var2)
	{
		for (int var3 = 10; var3 < items.length; var3++)
			if (items[var3] != null)
			{
				ItemStack var4 = items[var3].cloneItemStack();
				var4.count = 1;
				if (var1)
				{
					items[var3].count--;
					if (items[var3].count < 1)
						items[var3] = null;
				}
				return var4;
			}
		return null;
	}

	public String getName()
	{
		return "DM Furnace";
	}

	public void a(NBTTagCompound var1)
	{
		super.a(var1);
		NBTTagList var2 = var1.getList("Items");
		items = new ItemStack[getSize()];
		for (int var3 = 0; var3 < var2.size(); var3++)
		{
			NBTTagCompound var4 = (NBTTagCompound)var2.get(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < items.length)
				items[var5] = ItemStack.a(var4);
		}

		woftFactor = var1.getFloat("TimeFactor");
		furnaceBurnTime = var1.getInt("BurnTime");
		furnaceCookTime = var1.getShort("CookTime");
		currentItemBurnTime = getItemBurnTime(items[1]);
	}

	public void b(NBTTagCompound var1)
	{
		super.b(var1);
		var1.setInt("BurnTime", furnaceBurnTime);
		var1.setShort("CookTime", (short)furnaceCookTime);
		var1.setFloat("TimeFactor", woftFactor);
		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < items.length; var3++)
			if (items[var3] != null)
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
		return (furnaceCookTime * var1) / 10;
	}

	public int getBurnTimeRemainingScaled(int var1)
	{
		if (currentItemBurnTime == 0)
			currentItemBurnTime = 10;
		return (furnaceBurnTime * var1) / currentItemBurnTime;
	}

	public boolean isBurning()
	{
		return furnaceBurnTime > 0;
	}

	public void q_()
	{
		if (!clientFail())
		{
			woftFactor = EEBase.getPedestalFactor(world) * EEBase.getPlayerWatchFactor();
			boolean var2 = false;
			boolean var3 = false;
			float woft = getWOFTReciprocal(woftFactor);
			woft = (woft < 1.0F ? 1.0F : woft);
			if (furnaceBurnTime > 0)
			{
				furnaceBurnTime = (int)(furnaceBurnTime - woft);
				if (furnaceBurnTime <= 0)
				{
					furnaceBurnTime = 0;
					var3 = true;
				}
			}
			if (!world.isStatic)
			{
				if (furnaceBurnTime <= 0 && canSmelt())
				{
					currentItemBurnTime = furnaceBurnTime = getItemBurnTime(items[0]) / 16;
					if (furnaceBurnTime > 0)
					{
						var2 = true;
						var3 = true;
						if (items[0] != null && !EEBase.isKleinStar(items[0]))
						{
							if (items[0].getItem().k())
								items[0] = new ItemStack(items[0].getItem().j());
							else
								items[0].count--;
							if (items[0].count <= 0)
								items[0] = null;
						}
					}
				}
				if (isBurning() && canSmelt())
				{
					furnaceCookTime = (int)(furnaceCookTime + woft);
					if (furnaceCookTime >= 9)
					{
						furnaceCookTime = 0;
						smeltItem();
						var2 = true;
						var3 = true;
					}
				} else
				{
					for (int var4 = 11; var4 < 19; var4++)
						if (items[var4] != null && items[var4].count >= items[var4].getMaxStackSize() && tryDropInChest(new ItemStack(items[var4].getItem(), items[var4].count)))
							items[var4] = null;

					furnaceCookTime = 0;
					furnaceBurnTime = 0;
				}
			}
			if (var2) update();
			if (var3) world.notify(x, y, z);
		}
	}

	private boolean canSmelt()
	{
		if (items[1] == null)
		{
			for (int var1 = 2; var1 <= 9; var1++)
				if (items[var1] != null)
				{
					items[1] = items[var1].cloneItemStack();
					items[var1] = null;
					var1 = 10;
				}

			if (items[1] == null)
				return false;
		}
		ItemStack var3 = FurnaceRecipes.getInstance().getSmeltingResult(items[1]);
		if (var3 == null)
			return false;
		if (items[10] == null)
			return true;
		if (!items[10].doMaterialsMatch(var3))
		{
			if (tryDropInChest(items[10].cloneItemStack()))
			{
				items[10] = null;
				return true;
			}
			for (int var2 = 11; var2 <= 18; var2++)
			{
				if (items[var2] == null)
				{
					items[var2] = items[10].cloneItemStack();
					items[10] = null;
					return true;
				}
				if (items[var2].doMaterialsMatch(items[10]))
					while (items[10] != null && items[var2].count < 64) 
					{
						items[10].count--;
						items[var2].count++;
						if (items[10].count == 0)
						{
							items[10] = null;
							return true;
						}
					}
			}

		}
		if (items[10].count < getMaxStackSize() && items[10].count < items[10].getMaxStackSize())
			return true;
		for (int var2 = 11; var2 < 19; var2++)
			if (items[var2] != null && items[var2].count >= items[var2].getMaxStackSize() && tryDropInChest(items[var2].cloneItemStack()))
				items[var2] = null;

		if (items[10] == null)
			return true;
		for (int var2 = 11; var2 <= 18; var2++)
		{
			if (items[var2] == null)
			{
				items[var2] = items[10].cloneItemStack();
				items[10] = null;
				return true;
			}
			if (items[var2].doMaterialsMatch(items[10]))
				while (items[10] != null && items[var2].count < 64) 
				{
					items[10].count--;
					items[var2].count++;
					if (items[10].count == 0)
					{
						items[10] = null;
						return true;
					}
				}
		}

		return items[10].count < var3.getMaxStackSize();
	}

	public void smeltItem()
	{
		if (canSmelt())
		{
			ItemStack var1 = FurnaceRecipes.getInstance().getSmeltingResult(items[1]);
			boolean var2 = false;
			if (items[10] == null)
			{
				items[10] = var1.cloneItemStack();
				if (world.random.nextInt(2) == 0 && EEMaps.isOreBlock(items[1].id))
					items[10].count++;
			} else
			if (items[10].id == var1.id)
			{
				items[10].count += var1.count;
				if (world.random.nextInt(2) == 0 && EEMaps.isOreBlock(items[1].id))
					if (items[10].count < var1.getMaxStackSize())
						items[10].count++;
					else
						var2 = true;
			}
			if (items[10].count == var1.getMaxStackSize())
				if (tryDropInChest(items[10]))
				{
					items[10] = null;
					if (var2)
						items[10] = var1.cloneItemStack();
				} else
				{
					for (int var3 = 11; var3 <= 18; var3++)
						if (items[var3] == null)
						{
							items[var3] = items[10].cloneItemStack();
							items[10] = null;
							if (var2)
								items[10] = var1.cloneItemStack();
							var3 = 19;
						}

				}
			if (items[1].getItem().k())
				items[1] = new ItemStack(items[1].getItem().j());
			else
				items[1].count--;
			if (items[1].count < 1)
				items[1] = null;
			world.notify(x, y, z);
		}
	}

	@SuppressWarnings("deprecation")
	public int getItemBurnTime(ItemStack var1)
	{
		if (var1 == null) return 0;
		int var2 = var1.getItem().id;
		if (EEBase.isKleinStar(var2) && EEBase.takeKleinStarPoints(var1, 32, world))
		{
			return 1600;
		} else
		{
			int var3 = var1.getData();
			return var2 >= 256 || Block.byId[var2].material != Material.WOOD ? var2 != Item.STICK.id ? var2 != Item.COAL.id ? var2 != Item.LAVA_BUCKET.id ? var2 != Block.SAPLING.id ? var2 != EEItem.alchemicalCoal.id ? var2 != EEItem.mobiusFuel.id ? var2 != EEItem.aeternalisFuel.id ? ModLoader.addAllFuel(var2, var3) : 0x64000 : 0x19000 : 25600 : 100 : 3200 : 6400 : 100 : 300;
		}
	}

	public void f()
	{
	}

	public void g()
	{
	}

	public boolean a(EntityHuman var1)
	{
		return world.getTileEntity(x, y, z) == this ? var1.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64D : false;
	}

	public int getStartInventorySide(int var1)
	{
		return var1 != 0 ? var1 != 1 ? 10 : 1 : 0;
	}

	public int getSizeInventorySide(int var1)
	{
		return var1 != 0 ? var1 != 1 ? 9 : 9 : 1;
	}

	public boolean onBlockActivated(EntityHuman var1)
	{
		if (!world.isStatic)
			var1.openGui(mod_EE.getInstance(), GuiIds.DM_FURNACE, world, x, y, z);
		return true;
	}

	public int getTextureForSide(int var1)
	{
		byte var2 = direction;
		return var1 != var2 ? EEBase.dmBlockSide : EEBase.dmFurnaceFront;
	}

	public int getInventoryTexture(int var1)
	{
		return var1 != 3 ? EEBase.dmBlockSide : EEBase.dmFurnaceFront;
	}

	public int getLightValue()
	{
		return isBurning() ? 15 : 0;
	}

	public void randomDisplayTick(Random var1)
	{
		if (isBurning())
		{
			byte var2 = direction;
			float var3 = x + 0.5F;
			float var4 = y + 0.0F + (var1.nextFloat() * 6F) / 16F;
			float var5 = z + 0.5F;
			float var6 = 0.52F;
			float var7 = var1.nextFloat() * 0.6F - 0.3F;
			if (var2 == 4)
			{
				world.a("smoke", var3 - var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
				world.a("flame", var3 - var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
			} else
			if (var2 == 5)
			{
				world.a("smoke", var3 + var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
				world.a("flame", var3 + var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
			} else
			if (var2 == 2)
			{
				world.a("smoke", var3 + var7, var4, var5 - var6, 0.0D, 0.0D, 0.0D);
				world.a("flame", var3 + var7, var4, var5 - var6, 0.0D, 0.0D, 0.0D);
			} else
			if (var2 == 3)
			{
				world.a("smoke", var3 + var7, var4, var5 + var6, 0.0D, 0.0D, 0.0D);
				world.a("flame", var3 + var7, var4, var5 + var6, 0.0D, 0.0D, 0.0D);
			}
			for (int var8 = 0; var8 < 4; var8++)
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
				if ((world.getTypeId(x - 1, y, z) != EEBlock.eeStone.id || world.getData(x - 1, y, z) != 3) && (world.getTypeId(x + 1, y, z) != EEBlock.eeStone.id || world.getData(x + 1, y, z) != 3))
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
		if (canSmelt())
		{
			if (var3)
				furnaceBurnTime += var1;
			return true;
		} else
		{
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

	public void sendAllPackets(int i)
	{
	}

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

	public void setMaxStackSize(int i)
	{
	}
}
