package ee;

import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.dm.EEDMHoeEvent;
import net.minecraft.server.Block;
import net.minecraft.server.BlockDeadBush;
import net.minecraft.server.BlockFlower;
import net.minecraft.server.BlockLongGrass;
import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.World;

public class ItemDarkHoe extends ItemDarkTool {
	private static Block[] blocksEffectiveAgainst = { Block.DIRT, Block.GRASS };
	private static boolean breakMode;

	public ItemDarkHoe(int var1) {
		super(var1, 2, 6, blocksEffectiveAgainst);
	}

	public float getDestroySpeed(ItemStack var1, Block var2) {
		if ((var2.material != Material.EARTH) && (var2.material != Material.GRASS)) {
			return super.getDestroySpeed(var1, var2);
		}

		float var3 = 14.0F + chargeLevel(var1) * 4;

		if (breakMode) var3 /= 10.0F;

		return var3;
	}

	public void doBreak(ItemStack var1, World world, EntityHuman human) {
		int charge = chargeLevel(var1);
		if (charge < 1) return;

		int x = (int) EEBase.playerX(human);
		int y = (int) (EEBase.playerY(human) - 1.0D);
		int z = (int) EEBase.playerZ(human);

		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);

		for (int var7 = -(charge * charge) - 1; var7 <= charge * charge + 1; var7++) {
			for (int var8 = -(charge * charge) - 1; var8 <= charge * charge + 1; var8++) {
				int nx = x + var7;
				int nz = z + var8;
				int id = world.getTypeId(nx, y, nz);
				int data = world.getTypeId(nx, y + 1, nz);

				if (((data == BlockFlower.YELLOW_FLOWER.id) || (data == BlockFlower.RED_ROSE.id) || (data == BlockFlower.BROWN_MUSHROOM.id)
						|| (data == BlockFlower.RED_MUSHROOM.id) || (data == BlockLongGrass.LONG_GRASS.id) || (data == BlockDeadBush.DEAD_BUSH.id))) {
					if (attemptBreak(human, nx, y + 1, nz)) {
						Block.byId[data].dropNaturally(world, nx, y + 1, nz, world.getData(nx, y + 1, nz), 1.0F, 1);
						world.setTypeId(nx, y + 1, nz, 0);
						data = 0;
					}
				}

				if (data == 0 && (id == Block.DIRT.id || id == Block.GRASS.id)) {
					if (attemptBreak(human, nx, y, nz)) {
						if (getFuelRemaining(var1) < 1) {
							ConsumeReagent(var1, human, false);
						}

						if (getFuelRemaining(var1) > 0) {
							world.setTypeId(nx, y, nz, 60);
							setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);

							if (world.random.nextInt(8) == 0) {
								world.a("largesmoke", nx, y, nz, 0.0D, 0.0D, 0.0D);
							}

							if (world.random.nextInt(8) == 0) {
								world.a("explode", nx, y, nz, 0.0D, 0.0D, 0.0D);
							}
						}
					}
				}
			}
		}

	}

	/** When you rightclick with this item */
	public boolean interactWith(ItemStack var1, EntityHuman human, World world, int x, int y, int z, int face) {
		if (EEProxy.isClient(world)) return false;
		
		if (EEEventManager.callEvent(new EEDMHoeEvent(var1, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.TillRadius))) return false;
		
		int charge = chargeLevel(var1);
		if (charge > 0) {
			human.C_();
			world.makeSound(human, "flash", 0.8F, 1.5F);

			int id = world.getTypeId(x, y, z);
			if (id == BlockFlower.YELLOW_FLOWER.id || id == BlockFlower.RED_ROSE.id || id == BlockFlower.BROWN_MUSHROOM.id || id == BlockFlower.RED_MUSHROOM.id
					|| id == BlockLongGrass.LONG_GRASS.id || id == BlockDeadBush.DEAD_BUSH.id) {
				y--;
			}

			for (int var8 = -(charge * charge) - 1; var8 <= charge * charge + 1; var8++) {
				for (int var9 = -(charge * charge) - 1; var9 <= charge * charge + 1; var9++) {
					int nx = x + var8;
					int nz = z + var9;
					int id2 = world.getTypeId(nx, y, nz);
					int id3 = world.getTypeId(nx, y + 1, nz);

					if (((id3 == BlockFlower.YELLOW_FLOWER.id) || (id3 == BlockFlower.RED_ROSE.id) || (id3 == BlockFlower.BROWN_MUSHROOM.id)
							|| (id3 == BlockFlower.RED_MUSHROOM.id) || (id3 == BlockLongGrass.LONG_GRASS.id) || (id3 == BlockDeadBush.DEAD_BUSH.id))) {
						if (attemptBreak(human, nx, y + 1, nz)) {
							Block.byId[id3].dropNaturally(world, nx, y + 1, nz, world.getData(nx, y + 1, nz), 1.0F, 1);
							world.setTypeId(nx, y + 1, nz, 0);
							id3 = 0;
						}

					}

					if ((id3 == 0) && ((id2 == Block.DIRT.id) || (id2 == Block.GRASS.id))) {
						if (attemptBreak(human, nx, y, nz)) {
							if (getFuelRemaining(var1) < 1) {
								ConsumeReagent(var1, human, false);
							}

							if (getFuelRemaining(var1) > 0) {
								world.setTypeId(nx, y, nz, 60);
								setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);

								if (world.random.nextInt(8) == 0) {
									world.a("largesmoke", nx, y, nz, 0.0D, 0.0D, 0.0D);
								}

								if (world.random.nextInt(8) == 0) {
									world.a("explode", nx, y, nz, 0.0D, 0.0D, 0.0D);
								}
							}
						}
					}
				}
			}

			return false;
		}
		
		if ((human != null) && (!human.d(x, y, z)))
			return false;

		int id2 = world.getTypeId(x, y, z);
		int id3 = world.getTypeId(x, y + 1, z);

		if (((face == 0) || (id3 != 0) || (id2 != Block.GRASS.id)) && (id2 != Block.DIRT.id)) {
			return false;
		}

		Block var10 = Block.SOIL;
		world.makeSound(x + 0.5F, y + 0.5F, z + 0.5F, var10.stepSound.getName(), (var10.stepSound.getVolume1() + 1.0F) / 2.0F,
				var10.stepSound.getVolume2() * 0.8F);

		if (world.isStatic) {
			return true;
		} else {
			if (attemptBreak(human, x, y, z)) {
				world.setTypeId(x, y, z, var10.id);
				return true;
			} else {
				return false;
			}
		}
	}

	public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) return var1;
		
		if (EEEventManager.callEvent(new EEDMHoeEvent(var1, EEAction.RIGHTCLICK, var3, EEAction2.TillRadius))) return var1;

		doBreak(var1, var2, var3);
		return var1;
	}

	public boolean isFull3D() {
		return true;
	}

	public void doHeld(ItemStack var1, World var2, EntityHuman var3) {}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EEDMHoeEvent(var1, EEAction.RELEASE, var3, EEAction2.TillRadius))) return;
		doBreak(var1, var2, var3);
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {}
	public void doLeftClick(ItemStack var1, World var2, EntityHuman var3) {}
	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {}
}
