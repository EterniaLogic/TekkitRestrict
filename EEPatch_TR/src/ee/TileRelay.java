/* Warning: No line numbers available in class file */
package ee;

import java.util.Random;

import org.bukkit.entity.HumanEntity;

import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntityChest;
import net.minecraft.server.mod_EE;
import buildcraft.api.ISpecialInventory;
import buildcraft.api.Orientations;
import ee.core.GuiIds;
import forge.ISidedInventory;

public class TileRelay extends TileEE implements ISpecialInventory, ISidedInventory, IEEPowerNet {
	private ItemStack[] items;
	public int scaledEnergy;
	public int accumulate;
	public int arrayCounter;
	private float woftFactor;
	private int in;
	private int klein;
	private boolean isSending;
	public int burnTimeRemainingScaled;
	public int cookProgressScaled;
	public int kleinDrainingScaled;
	public int kleinChargingScaled;
	public int relayEnergyScaled;
	public int kleinDrainPoints;
	public int kleinChargePoints;

	public TileRelay() {
		items = new ItemStack[8];
		in = 0;
		klein = items.length - 1;
		arrayCounter = 0;
		accumulate = 0;
		woftFactor = 1.0F;
		kleinDrainPoints = 0;
		kleinChargePoints = 0;
		kleinDrainingScaled = 0;
		kleinChargingScaled = 0;
		relayEnergyScaled = 0;
	}

	@SuppressWarnings("null")
	public void onBlockRemoval() {
		for (HumanEntity h : getViewers()) h.closeInventory();
		for (int var1 = 0; var1 < getSize(); ++var1) {
			ItemStack var2 = getItem(var1);

			if (var2 == null) continue;
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

					if (var7.itemStack.getItem() instanceof ItemKleinStar) {
						((ItemKleinStar) var7.itemStack.getItem()).setKleinPoints(var7.itemStack, ((ItemKleinStar) var2.getItem()).getKleinPoints(var2));
					}

					world.addEntity(var7);
				}
			}
		}
	}

	private boolean isChest(TileEntity var1) {
		return var1 instanceof TileEntityChest || var1 instanceof TileAlchChest;
	}

	public static boolean putInChest(TileEntity var0, ItemStack var1) {
		if (var1 != null && var1.id != 0) {
			if (var0 == null) return false;
			
			// int var2;
			if (var0 instanceof TileEntityChest) {
				for (int var2 = 0; var2 < ((TileEntityChest) var0).getSize(); ++var2) {
					ItemStack var3 = ((TileEntityChest) var0).getItem(var2);

					if (var3 != null && var3.doMaterialsMatch(var1) && var3.count + var1.count <= var3.getMaxStackSize()) {
						var3.count += var1.count;
						return true;
					}
				}

				for (int var2 = 0; var2 < ((TileEntityChest) var0).getSize(); ++var2) {
					if (((TileEntityChest) var0).getItem(var2) != null) continue;
					((TileEntityChest) var0).setItem(var2, var1);
					return true;
				}

			} else if (var0 instanceof TileAlchChest) {
				for (int var2 = 0; var2 < ((TileAlchChest) var0).getSize(); ++var2) {
					ItemStack var3 = ((TileAlchChest) var0).getItem(var2);

					if (var3 != null && var3.doMaterialsMatch(var1) && var3.count + var1.count <= var3.getMaxStackSize() && var3.getData() == var1.getData()) {
						var3.count += var1.count;
						return true;
					}
				}

			}
			for (int var2 = 0; var2 < ((TileAlchChest) var0).getSize(); var2++) {
				if (((TileAlchChest) var0).getItem(var2) == null) {
					((TileAlchChest) var0).setItem(var2, var1);
					return true;
				}
			}
			return false;
		}

		return true;
	}

	public boolean tryDropInChest(ItemStack var1) {
		if (world == null || EEProxy.isClient(world)) return false;
		TileEntity var2;
			 if (isChest(var2 = world.getTileEntity(x, y + 1, z))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x, y - 1, z))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x + 1, y, z))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x - 1, y, z))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x, y, z + 1))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x, y, z - 1))) return putInChest(var2, var1);
		else return false;
	}

	public int getSize() {
		return items.length;
	}

	public int getMaxStackSize() {
		return 64;
	}

	public ItemStack getItem(int var1) {
		return items[var1];
	}

	public ItemStack splitStack(int var1, int var2) {
		if (items[var1] != null) {
			if (items[var1].count <= var2) {
				ItemStack var3 = items[var1];
				items[var1] = null;
				return var3;
			}

			ItemStack var3 = items[var1].a(var2);

			if (items[var1].count == 0) {
				items[var1] = null;
			}

			return var3;
		}

		return null;
	}

	public void setItem(int var1, ItemStack var2) {
		items[var1] = var2;

		if (var2 == null || var2.count <= getMaxStackSize()) return;
		var2.count = getMaxStackSize();
	}

	public boolean addItem(ItemStack var1, boolean var2, Orientations var3) {
		if (var1 != null) {
			for (int var4 = 0; var4 <= items.length - 2; var4++) {
				if (items[var4] != null) {
					if (!items[var4].doMaterialsMatch(var1) || items[var4].count >= items[var4].getMaxStackSize()) continue;
					if (var2) {
						for (; items[var4].count < items[var4].getMaxStackSize() && var1.count > 0; var1.count--)
							items[var4].count++;

						if (var1.count != 0) continue;
					}
					return true;
				}
				ItemKleinStar var5;
				if (!(var1.getItem() instanceof ItemKleinStar)
						|| (var3.equals(Orientations.YPos) ? (var5 = (ItemKleinStar) var1.getItem()).getKleinPoints(var1) != var5.getMaxPoints(var1)
								&& var4 == items.length - 1 : (var5 = (ItemKleinStar) var1.getItem()).getKleinPoints(var1) != 0 && var4 == 0)) {
					if (var2) {
						items[var4] = var1.cloneItemStack();
						for (; var1.count > 0; var1.count--);
					}
					return true;
				}
			}

		}
		return false;
	}

	public ItemStack extractItem(boolean var1, Orientations var2) {
		switch (var2) {
			case YPos:
				if (items[items.length - 1] == null) return null;
				if (items[items.length - 1].getItem() instanceof ItemKleinStar) {
					ItemStack var3 = items[items.length - 1].cloneItemStack();
					if (var1) items[items.length - 1] = null;
					return var3;
				}
				// fall through

			default:
				if (items[0] == null) return null;
				if (items[0].getItem() instanceof ItemKleinStar) {
					ItemStack var3 = items[0].cloneItemStack();
					if (var1) items[0] = null;
					return var3;
				}
				// fall through
				return null;
		}
	}

	public String getName() {
		return "AM Array";
	}

	public void a(NBTTagCompound var1) {
		super.a(var1);
		NBTTagList var2 = var1.getList("Items");
		items = new ItemStack[getSize()];

		for (int var3 = 0; var3 < var2.size(); ++var3) {
			NBTTagCompound var4 = (NBTTagCompound) var2.get(var3);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < items.length) items[var5] = ItemStack.a(var4);
		}

		scaledEnergy = var1.getInt("scaledEnergy");
		woftFactor = var1.getFloat("timeFactor");
		arrayCounter = var1.getShort("arrayCounter");
	}

	public void b(NBTTagCompound var1) {
		super.b(var1);
		var1.setInt("scaledEnergy", scaledEnergy);
		var1.setShort("arrayCounter", (short) arrayCounter);
		var1.setFloat("timeFactor", woftFactor);
		NBTTagList var2 = new NBTTagList();

		for (int var3 = 0; var3 < items.length; ++var3) {
			if (items[var3] == null) continue;
			NBTTagCompound var4 = new NBTTagCompound();
			var4.setByte("Slot", (byte) var3);
			items[var3].save(var4);
			var2.add(var4);
		}

		var1.set("Items", var2);
	}

	public int getCookProgressScaled(int var1) {
		return items[klein] != null ? EEBase.isKleinStar(items[klein]) || isSending ? var1 : 0 : 0;
	}

	public int getBurnTimeRemainingScaled(int var1) {
		return items[0] != null ? EEMaps.getEMC(items[0].id, items[in].getData()) <= 0 ? 0 : var1 : 0;
	}

	public int latentEnergy() {
		return scaledEnergy / 80;
	}

	public boolean receiveEnergy(int var1, byte var2, boolean var3) {
		if (passAllPackets(var1, var3)) return true;

		if (scaledEnergy <= scaledMaximum() - var1) {
			if (var3) {
				accumulate += var1;
			}

			return true;
		}

		return false;
	}

	/**
	 * Gets tile entities
	 */
	private boolean passAllPackets(int var1, boolean var2) {
		int var3 = 0;

		for (byte var4 = 0; var4 < 6; var4++) {
			if (passEnergy(var1, var4, false)) var3++;
		}

		if (var3 == 0) return false;

		if (!var2) return true;

		int var6 = var1 / var3;

		if (var6 < 1) return false;

		for (byte var5 = 0; var5 < 6; var5++) {
			passEnergy(var6, var5, true);//Gets tile entity
		}

		return true;
	}

	/**
	 * 
	 * Gets tile entity
	 * @see ee.IEEPowerNet#passEnergy(int, byte, boolean)
	 */
	public boolean passEnergy(int var1, byte var2, boolean var3) {
		final TileEntity var4 = world.getTileEntity(x + (var2 != 5 ? (int) (var2 != 4 ? 0 : 1) : -1), y + (var2 != 1 ? (int) (var2 != 0 ? 0 : 1) : -1), z + (var2 != 3 ? (int) (var2 != 2 ? 0 : 1) : -1));
		if (var4 == null) return false;
		if (!(var4 instanceof TileRelay) && !(var4 instanceof TileRelay2) && !(var4 instanceof TileRelay3)) {
			return var4 instanceof IEEPowerNet && ((IEEPowerNet) var4).receiveEnergy(var1, var2, var3);
		} else {
			//IEEPowerNet var10000 = (IEEPowerNet) var4;
			return false;
		}
	}

	/**
	 * Gets tile entity
	 * @see ee.IEEPowerNet#sendEnergy(int, byte, boolean)
	 */
	public boolean sendEnergy(int var1, byte var2, boolean var3) {
		final TileEntity var4 = world.getTileEntity(x + (var2 == 4 ? 1 : var2 == 5 ? -1 : 0), y + (var2 == 0 ? 1 : var2 == 1 ? -1 : 0), z + (var2 == 2 ? 1 : var2 == 3 ? -1 : 0));
		return var4 != null
				?
					var4 instanceof TileRelay || var4 instanceof TileRelay2 || var4 instanceof TileRelay3
					?
						false
					:
						var4 instanceof IEEPowerNet && ((IEEPowerNet) var4).receiveEnergy(var1, var2, var3)
				:
				false;
	}

	/**
	 * Gets tile entity
	 * @see ee.IEEPowerNet#sendAllPackets(int)
	 */
	public void sendAllPackets(int var1) {
		int var2 = 0;

		for (byte var3 = 0; var3 < 6; var3++) {
			if (sendEnergy(var1, var3, false)) var2++;
		}

		if (var2 == 0) return;
		int var5 = var1 / var2;

		if (var5 < 1) return;
		for (byte var4 = 0; var4 < 6; var4++) {
			if (scaledEnergy - var5 <= 0) return;

			if (sendEnergy(var5, var4, true)) scaledEnergy -= var5;
		}
	}

	public int relayBonus() {
		return 4;
	}

	private float getRelayOutput() {
		return 64F;
	}

	private int relayMaximum() {
		return 100000;
	}

	private int scaledMaximum() {
		return relayMaximum() * 80;
	}

	public int getRelayProductivity() {
		return (int) (getRelayOutput() * getWOFTReciprocal(woftFactor));
	}

	public void q_() {
		if (clientFail()) return;
		boolean var1 = false;
		woftFactor = EEBase.getPedestalFactor(world) * EEBase.getPlayerWatchFactor();

		if (!world.isStatic) {
			burnTimeRemainingScaled = getBurnTimeRemainingScaled(12);
			cookProgressScaled = getCookProgressScaled(24);
			kleinDrainingScaled = getKleinDrainingScaled(30);
			kleinChargingScaled = getKleinChargingScaled(30);
			relayEnergyScaled = getRelayEnergyScaled(102);

			if (accumulate > 0) {
				scaledEnergy += accumulate;
				accumulate = 0;
			}

			if (items[0] != null && EEBase.isKleinStar(items[0])) {
				for (int var2 = getRelayProductivity() * ItemKleinStar.getLevel_s(items[0]); var2 > 0; --var2) {
					if (latentEnergy() >= relayMaximum() || !EEBase.takeKleinStarPoints(items[0], 1, world)) continue;
					scaledEnergy += 80;
				}

			}

			if (arrayCounter <= 0 && canDestroy()) {
				arrayCounter = 20;
				var1 = true;
				destroyItem();
			}

			if (scaledEnergy >= getRelayProductivity()) {
				sendAllPackets(getRelayProductivity());

				if (items[items.length - 1] != null && EEBase.isKleinStar(items[items.length - 1]) && scaledEnergy > 80) {
					for (int var2 = getRelayProductivity() * ItemKleinStar.getLevel_s(items[items.length - 1]); var2 > 0; --var2) {
						if (scaledEnergy >= 80 && EEBase.addKleinStarPoints(items[items.length - 1], 1, world)) scaledEnergy -= 80;
					}
				}

			}

			if (arrayCounter > 0) {
				arrayCounter--;
			}
		}

		if (var1) world.notify(x, y, z);
	}

	private int getRelayEnergyScaled(int var1) {
		return latentEnergy() * var1 / relayMaximum();
	}

	private int getKleinChargingScaled(int var1) {
		if (items[items.length - 1] != null && items[items.length - 1].getItem() instanceof ItemKleinStar) {
			kleinChargePoints = ((ItemKleinStar) items[items.length - 1].getItem()).getKleinPoints(items[items.length - 1]);
			return ((ItemKleinStar) items[items.length - 1].getItem()).getKleinPoints(items[items.length - 1]) * var1
					/ ((ItemKleinStar) items[items.length - 1].getItem()).getMaxPoints(items[items.length - 1]);
		} else {
			kleinChargePoints = 0;
			return 0;
		}
	}

	private int getKleinDrainingScaled(int var1) {
		if (items[0] != null && items[0].getItem() instanceof ItemKleinStar) {
			kleinDrainPoints = ((ItemKleinStar) items[0].getItem()).getKleinPoints(items[0]);
			return ((ItemKleinStar) items[0].getItem()).getKleinPoints(items[0]) * var1 / ((ItemKleinStar) items[0].getItem()).getMaxPoints(items[0]);
		} else {
			kleinDrainPoints = 0;
			return 0;
		}
	}

	private boolean canDestroy() {
		if (items[0] == null) {
			for (int var1 = items.length - 2; var1 >= 1; var1--) {
				if (items[var1] == null || EEMaps.getEMC(items[var1]) <= 0) continue;
				items[0] = items[var1].cloneItemStack();
				items[var1] = null;
				break;
			}

		}
		return items[0] != null ? EEBase.isKleinStar(items[0]) ? true
				: EEMaps.getEMC(items[0]) != 0 ? scaledEnergy / 80 + EEMaps.getEMC(items[0]) <= relayMaximum() : false : false;
	}

	public void destroyItem() {
		if (canDestroy() && !EEBase.isKleinStar(items[0])) {
			scaledEnergy += getCorrectValue(items[0]) * 80;
			items[0].count--;
			if (items[0].count <= 0) items[0] = null;
		}
	}

	private int getCorrectValue(ItemStack var1) {
		return EEMaps.getEMC(var1);
	}

	@SuppressWarnings("unused")
	private int getItemBurnTime(ItemStack var1) {
		if (var1 == null || EEBase.isKleinStar(var1)) return 0;
		if (EEMaps.getEMC(var1) == 0) {
			EntityItem var2 = new EntityItem(world, x, y, z, var1.cloneItemStack());
			var2.pickupDelay = 10;
			world.addEntity(var2);
			var1 = null;
			return 0;
		} else {
			return var1.d() ? (int) (EEMaps.getEMC(var1.id) * ((float) var1.i() - (float) var1.getData()) / var1.i()) : EEMaps.getEMC(var1);
		}
	}

	public void f() {}

	public void g() {}

	public boolean a(EntityHuman var1) {
		return world.getTileEntity(x, y, z) == this ? var1.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64D : false;
	}

	public int getStartInventorySide(int var1) {
		return var1 == 1 ? items.length - 1 : 0;
	}

	public int getSizeInventorySide(int var1) {
		return var1 == 1 ? 1 : items.length - 1;
	}

	public boolean onBlockActivated(EntityHuman var1) {
		if (!world.isStatic) {
			var1.openGui(mod_EE.getInstance(), GuiIds.RELAY_1, world, x, y, z);
		}

		return true;
	}

	public int getTextureForSide(int var1) {
		if (var1 == 1) {
			return EEBase.relayTop;
		}

		return var1 == direction ? EEBase.relayFront : EEBase.relaySide;
	}

	public int getInventoryTexture(int var1) {
		return var1 != 1 ? var1 != 3 ? EEBase.relaySide : EEBase.relayFront : EEBase.relayTop;
	}

	public int getLightValue() {
		return 7;
	}

	public void onNeighborBlockChange(int var1) {}

	public void randomDisplayTick(Random var1) {}

	public ItemStack splitWithoutUpdate(int var1) {
		return null;
	}

	public ItemStack[] getContents() {
		return items;
	}

	public void setMaxStackSize(int size) {}
}

/* Location:           C:\Program Files\eclipse_Kepler\lib\Tekkit_Classic\mods\EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
 * Qualified Name:     ee.TileRelay
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
