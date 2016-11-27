// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst space 
// Source File Name:   TileCollector.java

package ee;

import org.bukkit.entity.HumanEntity;

import buildcraft.api.ISpecialInventory;
import buildcraft.api.Orientations;
import ee.core.GuiIds;
import forge.ISidedInventory;
import net.minecraft.server.*;

public class TileCollector extends TileEE implements ISpecialInventory, ISidedInventory, IEEPowerNet {

	private ItemStack items[];
	public int currentSunStatus;
	public int collectorSunTime;
	private int accumulate;
	private float woftFactor;
	public int currentFuelProgress;
	public boolean isUsingPower;
	public int kleinProgressScaled;
	public int sunTimeScaled;
	public int kleinPoints;

	public TileCollector() {
		items = new ItemStack[11];
		currentSunStatus = 1;
		collectorSunTime = 0;
		accumulate = 0;
		woftFactor = 1.0F;
		currentFuelProgress = 0;
		kleinProgressScaled = 0;
		sunTimeScaled = 0;
		kleinPoints = 0;
	}

	public int getSize() {
		return items.length;
	}

	@SuppressWarnings("null")
	public void onBlockRemoval() {
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

	public int getMaxStackSize() {
		return 64;
	}

	public ItemStack getItem(int var1) {
		return items[var1];
	}

	public ItemStack splitStack(int var1, int var2) {
		if (items[var1] != null) {
			ItemStack var3;
			if (items[var1].count <= var2) {
				var3 = items[var1];
				items[var1] = null;
				return var3;
			}
			var3 = items[var1].a(var2);
			if (items[var1].count == 0) items[var1] = null;
			return var3;
		} else {
			return null;
		}
	}

	public void setItem(int var1, ItemStack var2) {
		items[var1] = var2;
		if (var2 != null && var2.count > getMaxStackSize()) var2.count = getMaxStackSize();
	}

	public boolean addItem(ItemStack var1, boolean var2, Orientations var3) {
		if (var1 == null) return false;
		if (EEMaps.isFuel(var1)) {
			for (int var4 = 0; var4 <= items.length - 3; var4++) {
				if (items[var4] == null) {
					if (var2) {
						items[var4] = var1.cloneItemStack();
						for (; var1.count > 0; var1.count--);
					}
					return true;
				}
				if (!items[var4].doMaterialsMatch(var1) || items[var4].count >= items[var4].getMaxStackSize()) continue;
				if (var2) {
					for (; items[var4].count < items[var4].getMaxStackSize() && var1.count > 0; var1.count--)
						items[var4].count++;

					if (var1.count != 0) continue;
				}
				return true;
			}

			return false;
		}
		if (!EEBase.isKleinStar(var1) || items[0] != null) return false;
		if (var2) {
			items[0] = var1.cloneItemStack();
			for (; var1.count > 0; var1.count--);
		}
		return true;
	}

	public ItemStack extractItem(boolean var1, Orientations var2) {
		for (int var3 = 0; var3 < items.length; var3++)
			if (items[var3] != null && var3 != items.length - 1) if (var3 == 0) {
				if (EEBase.isKleinStar(items[var3])) {
					ItemStack var4 = items[var3].cloneItemStack();
					if (var1) items[var3] = null;
					return var4;
				}
			} else if (items[var3].id == EEItem.aeternalisFuel.id || items[items.length - 1] != null && items[var3].doMaterialsMatch(items[items.length - 1])) {
				ItemStack var4 = items[var3].cloneItemStack();
				var4.count = 1;
				if (var1) {
					items[var3].count--;
					if (items[var3].count < 1) items[var3] = null;
				}
				return var4;
			}

		return null;
	}

	public String getName() {
		return "Energy Collector";
	}

	public void a(NBTTagCompound var1) {
		super.a(var1);
		NBTTagList var2 = var1.getList("Items");
		items = new ItemStack[getSize()];
		for (int var3 = 0; var3 < var2.size(); var3++) {
			NBTTagCompound var4 = (NBTTagCompound) var2.get(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < items.length) items[var5] = ItemStack.a(var4);
		}

		currentSunStatus = var1.getShort("sunStatus");
		woftFactor = var1.getFloat("timeFactor");
		accumulate = var1.getInt("accumulate");
		collectorSunTime = var1.getInt("sunTime");
	}

	public void b(NBTTagCompound var1) {
		super.b(var1);
		var1.setInt("sunTime", collectorSunTime);
		var1.setFloat("timeFactor", woftFactor);
		var1.setInt("accumulate", accumulate);
		var1.setShort("sunStatus", (short) currentSunStatus);
		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < items.length; var3++)
			if (items[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				items[var3].save(var4);
				var2.add(var4);
			}

		var1.set("Items", var2);
	}

	public int getSunProgressScaled(int var1) {
		return canUpgrade() ? getFuelDifference() > 0 ? (collectorSunTime * var1) / (getFuelDifference() * 80) <= 24 ? (collectorSunTime * var1)
				/ (getFuelDifference() * 80) : 24 : items[0] == null || !EEBase.isKleinStar(items[0]) ? 0 : 24 : 0;
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

	public boolean receiveEnergy(int var1, byte var2, boolean var3) {
		if (!isUsingPower()) return false;
		if (var3) {
			accumulate += var1;
			return true;
		} else {
			return true;
		}
	}

	/**
	 * Gets tile entity
	 */
	public boolean sendEnergy(int var1, byte var2, boolean var3) {
		final TileEntity var4 = world.getTileEntity(x + (var2 != 5 ? ((int) (var2 != 4 ? 0 : 1)) : -1), y + (var2 != 1 ? ((int) (var2 != 0 ? 0 : 1)) : -1), z + (var2 != 3 ? ((int) (var2 != 2 ? 0 : 1)) : -1));
		return var4 != null ? (var4 instanceof IEEPowerNet) && ((IEEPowerNet) var4).receiveEnergy(var1 + ((IEEPowerNet) var4).relayBonus(), var2, var3) : false;
	}

	public void sendAllPackets(int var1) {
		int var2 = 0;
		for (byte var3 = 0; var3 < 6; var3++)
			if (sendEnergy(var1, var3, false)) var2++;

		if (var2 == 0) {
			if (collectorSunTime <= 0xc3500 - var1) collectorSunTime += var1;
		} else {
			int var5 = var1 / var2;
			if (var5 >= 1) {
				for (byte var4 = 0; var4 < 6; var4++)
					sendEnergy(var5, var4, true);

			}
		}
	}

	public boolean passEnergy(int var1, byte var2, boolean var3) {
		return false;
	}

	public int relayBonus() {
		return 0;
	}

	public int getRealSunStatus() {
		if (world.worldProvider.d) currentSunStatus = 16;
		else currentSunStatus = world.getLightLevel(x, y + 1, z) + 1;
		return currentSunStatus;
	}

	public int getSunStatus(int var1) {
		return (getRealSunStatus() * var1) / 16;
	}

	public void q_() {
		if (!clientFail() && !world.isStatic) {
			if (collectorSunTime < 0) collectorSunTime = 0;
			if (items[0] != null && (items[0].getItem() instanceof ItemKleinStar)) {
				kleinProgressScaled = getKleinProgressScaled(48);
				kleinPoints = getKleinPoints(items[0]);
			}
			sunTimeScaled = getSunTimeScaled(48);
			currentFuelProgress = getSunProgressScaled(24);
			currentSunStatus = getSunStatus(12);
			isUsingPower = isUsingPower();
			for (int var1 = items.length - 3; var1 >= 2; var1--)
				if (items[var1] == null && items[var1 - 1] != null) {
					items[var1] = items[var1 - 1].cloneItemStack();
					items[var1 - 1] = null;
				}

			woftFactor = EEBase.getPedestalFactor(world) * EEBase.getPlayerWatchFactor();
			if (isUsingPower()) {
				collectorSunTime += getFactoredProduction();
				if (accumulate > 0) {
					collectorSunTime += accumulate;
					accumulate = 0;
				}
				if (EEBase.isKleinStar(items[0])) {
					for (int var1 = getFactoredProduction() * ItemKleinStar.getLevel_s(items[0]); var1 > 0 && collectorSunTime >= 80 && EEBase.addKleinStarPoints(items[0], 1, world); var1--)
						collectorSunTime -= 80;

				} else {
					for (; getFuelDifference() > 0 && collectorSunTime >= getFuelDifference() * 80; uptierFuel())
						collectorSunTime -= getFuelDifference() * 80;

				}
			} else {
				if (accumulate > 0) {
					collectorSunTime += accumulate;
					accumulate = 0;
				}
				sendAllPackets(getFactoredProduction());
			}
		}
	}

	private int getKleinPoints(ItemStack var1) {
		return var1 != null ? (var1.getItem() instanceof ItemKleinStar) ? ((ItemKleinStar) var1.getItem()).getKleinPoints(var1) : 0 : 0;
	}

	private int getSunTimeScaled(int var1) {
		return (collectorSunTime * var1) / 0xc3500;
	}

	private int getKleinProgressScaled(int var1) {
		return items[0] == null || !(items[0].getItem() instanceof ItemKleinStar) ? 0 : (((ItemKleinStar) items[0].getItem()).getKleinPoints(items[0]) * var1)
				/ ((ItemKleinStar) items[0].getItem()).getMaxPoints(items[0]);
	}

	public int getFactoredProduction() {
		return (int) (getProduction() * getWOFTReciprocal(woftFactor));
	}

	public int getProduction() {
		return getRealSunStatus();
	}

	public boolean isUsingPower() {
		return canUpgrade() && canCollect();
	}

	private int getFuelDifference() {
		return items[0] != null ? getFuelLevel(getNextFuel(items[0])) - getFuelLevel(items[0]) : 0;
	}

	private int getFuelLevel(ItemStack var1) {
		return EEMaps.getEMC(var1);
	}

	private ItemStack getNextFuel(ItemStack var1) {
		int var2 = var1.id;
		int var3 = var1.getData();
		if (items[items.length - 1] == null) {
			if (EEMaps.isFuel(var1)) {
				if (var2 == Item.COAL.id && var3 == 1) return new ItemStack(Item.REDSTONE.id, 1, 0);
				if (var2 == Item.REDSTONE.id) return new ItemStack(Item.COAL.id, 1, 0);
				if (var2 == Item.COAL.id) return new ItemStack(Item.SULPHUR.id, 1, 0);
				if (var2 == Item.SULPHUR.id) return new ItemStack(Item.GLOWSTONE_DUST.id, 1, 0);
				if (var2 == Item.GLOWSTONE_DUST.id) return new ItemStack(EEItem.alchemicalCoal.id, 1, 0);
				if (var2 == EEItem.alchemicalCoal.id) return new ItemStack(Item.BLAZE_POWDER.id, 1, 0);
				if (var2 == Item.BLAZE_POWDER.id) return new ItemStack(Block.GLOWSTONE.id, 1, 0);
				if (var2 == Block.GLOWSTONE.id) return new ItemStack(EEItem.mobiusFuel.id, 1, 0);
				if (var2 == EEItem.mobiusFuel.id) return new ItemStack(EEItem.aeternalisFuel.id, 1, 0);
			}
			return null;
		}
		if (EEMaps.isFuel(items[items.length - 1])) {
			return EEMaps.getEMC(var2, var3) >= EEMaps.getEMC(items[items.length - 1].id, items[items.length - 1].getData()) ? null : items[items.length - 1];
		} else {
			EntityItem var4 = new EntityItem(world, x, y, z, items[items.length - 1].cloneItemStack());
			items[items.length - 1] = null;
			var4.pickupDelay = 10;
			world.addEntity(var4);
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

	public void f() {}

	public void g() {}

	public boolean a(EntityHuman var1) {
		return world.getTileEntity(x, y, z) == this ? var1.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64D : false;
	}

	public int getStartInventorySide(int var1) {
		return var1 != 0 ? 1 : 0;
	}

	public int getSizeInventorySide(int var1) {
		return var1 != 0 ? items.length - 2 : 1;
	}

	public boolean onBlockActivated(EntityHuman var1) {
		if (!world.isStatic) var1.openGui(mod_EE.getInstance(), GuiIds.COLLECTOR_1, world, x, y, z);
		return true;
	}

	public int getTextureForSide(int var1) {
		if (var1 == 1) {
			return EEBase.collectorTop;
		} else {
			byte var2 = direction;
			return var1 == var2 ? EEBase.collectorFront : EEBase.collectorSide;
		}
	}

	public int getInventoryTexture(int var1) {
		return var1 != 1 ? var1 != 3 ? EEBase.collectorSide : EEBase.collectorFront : EEBase.collectorTop;
	}

	public int getLightValue() {
		return 7;
	}

	public void onNeighborBlockChange(int i) {}

	public ItemStack splitWithoutUpdate(int var1) {
		return null;
	}

	public ItemStack[] getContents() {
		return items;
	}

	public void setMaxStackSize(int i) {}

}
