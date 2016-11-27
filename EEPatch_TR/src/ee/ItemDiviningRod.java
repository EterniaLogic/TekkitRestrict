package ee;

import java.util.ArrayList;

import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.FurnaceRecipes;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ItemDiviningRod extends ItemEECharged {
	protected ItemDiviningRod(int var1) {
		super(var1, 0);
		a(true);
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World var3, int x, int y, int z, int face) {
		if (EEProxy.isClient(var3)) return false;

		if (getShort(item, "cooldown") <= 0) {
			int mode = getMode(item);
			if (getFuelRemaining(item) < (mode == 1 ? 4 : mode == 0 ? 2 : 8)) {
				ConsumeReagent(item, human, true);

				if (getFuelRemaining(item) < (mode == 1 ? 4 : mode == 0 ? 2 : 8)) {
					ConsumeReagent(item, human, true);
				}
			}

			//if (EEEventManager.callEvent(new EEDiviningRodEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.DiviningRod))) return false;

			int area = (mode == 1 ? 16 : (mode == 0 ? 3 : 64));
			doDivining(item, human, area, x, y, z, face);
			setShort(item, "cooldown", 60 / (item.getData() + 1));
			return true;
		}

		return false;
	}

	public void a(ItemStack item, World var2, Entity var3, int var4, boolean var5) {
		short old = getShort(item, "cooldown");
		if (old > 0) {
			setShort(item, "cooldown", old - 1);
		}
	}

	public void addCreativeItems(ArrayList var1) {
		var1.add(new ItemStack(EEItem.diviningRod, 1, 0));
		var1.add(new ItemStack(EEItem.diviningRod, 1, 1));
		var1.add(new ItemStack(EEItem.diviningRod, 1, 2));
	}

	public void doDivining(ItemStack item, EntityHuman human, int area, int x, int y, int z, int face) {
		int mode = getMode(item);
		setFuelRemaining(item, getFuelRemaining(item) - (mode == 1 ? 4 : (mode == 0 ? 2 : 8)));
		float total = 0.0F;
		int amount = 0;
		// boolean var10 = false;
		// boolean var11 = false;
		int highest = 0;
		int oldhigh = 0;
		int middle = 0;
		int oldmid = 0;
		int lowest = 0;
		World world = human.world;

		for (int ix = -1 * (face == 5 ? area : 1); ix <= 1 * (face == 4 ? area : 1); ix++) {
			for (int iy = -1 * (face == 1 ? area : 1); iy <= 1 * (face == 0 ? area : 1); iy++) {
				for (int iz = -1 * (face == 3 ? area : 1); iz <= 1 * (face == 2 ? area : 1); iz++) {
					int id = world.getTypeId(x + ix, y + iy, z + iz);
					int data = world.getData(x + ix, y + iy, z + iz);

					int emc;
					ItemStack var21;
					Block bl;
					if ((emc = EEMaps.getEMC(id, data)) > 0) {
						if (emc > highest) {
							oldhigh = highest;
							highest = emc;
						}

						if (oldhigh > middle) {
							oldmid = middle;
							middle = oldhigh;
						}

						if (oldmid > lowest) {
							lowest = oldmid;
						}

						total += emc;
						amount++;
					} else if (((bl = Block.byId[id]) != null) && ((emc = EEMaps.getEMC(bl.getDropType(id, world.random, 0), data) * bl.quantityDropped(data, 0, world.random)) > 0)) {
						if (emc > highest) {
							oldhigh = highest;
							highest = emc;
						}

						if (oldhigh > middle) {
							oldmid = middle;
							middle = oldhigh;
						}

						if (oldmid > lowest) {
							lowest = oldmid;
						}

						total += emc;
						amount++;
					} else if ((var21 = FurnaceRecipes.getInstance().getSmeltingResult(new ItemStack(id, 1, data))) != null) {
						emc = EEMaps.getEMC(var21.id, var21.getData());
						if (emc != 0) {
							if (emc > highest) {
								oldhigh = highest;
								highest = emc;
							}

							if (oldhigh > middle) {
								oldmid = middle;
								middle = oldhigh;
							}

							if (oldmid > lowest) {
								lowest = oldmid;
							}

							total += emc;
							amount++;
						}
					}
				}
			}
		}

		String var24 = "Divining suggests a value around... " + Math.floor(total / amount);
		String var25 = null;

		if (item.getData() > 0) {
			var25 = " Best found: " + highest + (item.getData() == 2 ? " Second: " + middle + " Third: " + lowest : "");
		}

		human.a(var24);

		if (var25 != null) human.a(var25);
	}

	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {
		if (var1.getData() > 0) changeModes(var1);

		int mode = getMode(var1);
		if (mode == 0) var3.a("Divining rod short range (3x3x3)");
		else if (mode == 1) var3.a("Divining rod mid range (16x3x3)");
		else var3.a("Divining rod long range (64x3x3)");
	}

	public void changeModes(ItemStack item) {
		int mode = getMode(item);
		if (item.getData() > 1) {
			setMode(item, (mode+1)%3);
			//if (mode == 2) setMode(item, 0);
			//else setMode(item, mode + 1);
		} else {
			setMode(item, (mode+1)%2);
			//if (mode == 1) setMode(item, 0);
			//else setMode(item, mode + 1);
		}
		
	}

	public void setMode(ItemStack item, int mode) {
		setShort(item, "mode", (short) mode);
	}

	public int getMode(ItemStack item) {
		return getShort(item, "mode");
	}

	public void doChargeTick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doUncharge(ItemStack var1, World var2, EntityHuman var3) {}
}