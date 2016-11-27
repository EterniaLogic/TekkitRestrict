package ee;

import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.rm.EERMHoeEvent;
import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.World;

public class ItemRedHoe extends ItemRedTool {
	private static Block[] blocksEffectiveAgainst;
	private static boolean breakMode;
	
	static {
		blocksEffectiveAgainst = (new Block[] { Block.DIRT, Block.GRASS });
	}

	public ItemRedHoe(int var1) {
		super(var1, 3, 8, blocksEffectiveAgainst);
	}

	public float getDestroySpeed(ItemStack var1, Block var2) {
		if ((var2.material != Material.EARTH) && (var2.material != Material.GRASS)) {
			return super.getDestroySpeed(var1, var2);
		}

		float var3 = 18.0F + chargeLevel(var1) * 4;

		if (breakMode) var3 /= 10.0F;

		return var3;
	}

	public void doBreak(ItemStack var1, World var2, EntityHuman var3) {
		int charge = chargeLevel(var1);
		if (charge < 1) return;

		int x = (int) EEBase.playerX(var3);
		int y = (int) (EEBase.playerY(var3) - 1.0D);
		int z = (int) EEBase.playerZ(var3);

		var3.C_();
		var2.makeSound(var3, "flash", 0.8F, 1.5F);

		for (int var7 = -(charge * charge) - 1; var7 <= charge * charge + 1; var7++) {
			for (int var8 = -(charge * charge) - 1; var8 <= charge * charge + 1; var8++) {
				int nx = x + var7;
				int nz = z + var8;
				int id = var2.getTypeId(nx, y, nz);
				int id2 = var2.getTypeId(nx, y + 1, nz);

				if ((id2 == yFlower || id2 == rFlower || id2 == bMush || id2 == rMush || id2 == longGrass || id2 == bush)
					&& attemptBreak(var3, nx, y + 1, nz)) {
					Block.byId[id2].dropNaturally(var2, nx, y + 1, nz, var2.getData(nx, y + 1, nz), 1.0F, 1);
					var2.setTypeId(nx, y + 1, nz, 0);
				}

				if ((id2 == 0) && ((id == Block.DIRT.id) || (id == Block.GRASS.id)) && attemptBreak(var3, nx, y, nz)) {
					if (getFuelRemaining(var1) < 1) {
						ConsumeReagent(var1, var3, false);
					}

					if (getFuelRemaining(var1) > 0) {
						var2.setTypeId(nx, y, nz, 60);
						setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);

						if (var2.random.nextInt(8) == 0) {
							var2.a("largesmoke", nx, y, nz, 0.0D, 0.0D, 0.0D);
						}

						if (var2.random.nextInt(8) == 0) {
							var2.a("explode", nx, y, nz, 0.0D, 0.0D, 0.0D);
						}
					}
				}
			}
		}
	}

	public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int x, int y, int z, int face) {
		if (EEProxy.isClient(var3)) return false;
		
		int charge = chargeLevel(var1);
		if (charge > 0) {
			if (EEEventManager.callEvent(new EERMHoeEvent(var1, EEAction.RIGHTCLICK, var2, x, y, z, EEAction2.TillRadius))) return false;
			var2.C_();
			var3.makeSound(var2, "flash", 0.8F, 1.5F);

			int tempid = var3.getTypeId(x, y, z);
			if (tempid == yFlower || tempid == rFlower || tempid == bMush
			|| tempid == rMush || tempid == longGrass || tempid == bush) {
				y--;
			}

			for (int var8 = -(charge * charge) - 1; var8 <= charge * charge + 1; var8++) {
				for (int var9 = -(charge * charge) - 1; var9 <= charge * charge + 1; var9++) {
					int nx = x + var8;
					int nz = z + var9;
					int id = var3.getTypeId(nx, y, nz);
					int id2 = var3.getTypeId(nx, y + 1, nz);

					if ((id2 == yFlower || id2 == rFlower || id2 == bMush || id2 == rMush || id2 == longGrass || id2 == bush) && attemptBreak(var2, nx, y + 1, nz)) {
						Block.byId[id2].dropNaturally(var3, nx, y + 1, nz, var3.getData(nx, y + 1, nz), 1.0F, 1);
						var3.setTypeId(nx, y + 1, nz, 0);
						id2 = 0;
					}

					if (id2 == 0 && (id == Block.DIRT.id || id == Block.GRASS.id) && attemptBreak(var2, nx, y, nz)) {
						if (getFuelRemaining(var1) < 1) {
							ConsumeReagent(var1, var2, false);
						}

						if (getFuelRemaining(var1) > 0) {
							var3.setTypeId(nx, y, nz, 60);
							setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);

							if (var3.random.nextInt(8) == 0) {
								var3.a("largesmoke", nx, y, nz, 0.0D, 0.0D, 0.0D);
							}

							if (var3.random.nextInt(8) == 0) {
								var3.a("explode", nx, y, nz, 0.0D, 0.0D, 0.0D);
							}
						}
					}
				}
			}

			return false;
		}
		
		if ((var2 != null) && (!var2.d(x, y, z))) {
			return false;
		}

		int var8 = var3.getTypeId(x, y, z);
		int var9 = var3.getTypeId(x, y + 1, z);

		if (((face == 0) || (var9 != 0) || (var8 != Block.GRASS.id)) && (var8 != Block.DIRT.id)) {
			return false;
		}

		Block var10 = Block.SOIL;
		var3.makeSound(x + 0.5F, y + 0.5F, z + 0.5F, var10.stepSound.getName(), (var10.stepSound.getVolume1() + 1.0F) / 2.0F,
				var10.stepSound.getVolume2() * 0.8F);

		if (var3.isStatic) {
			return true;
		} else {
			if (attemptBreak(var2, x, y, z)) {
				var3.setTypeId(x, y, z, var10.id);
				return true;
			} else {
				return false;
			}
		}
	}

	public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) return var1;
		
		if (EEEventManager.callEvent(new EERMHoeEvent(var1, EEAction.RIGHTCLICK, var3, EEAction2.TillRadius))) return var1;
		doBreak(var1, var2, var3);
		return var1;
	}

	public boolean isFull3D() {
		return true;
	}

	public void doHeld(ItemStack var1, World var2, EntityHuman var3) {}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EERMHoeEvent(var1, EEAction.RELEASE, var3, EEAction2.TillRadius))) return;
		doBreak(var1, var2, var3);
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {}
	public void doLeftClick(ItemStack var1, World var2, EntityHuman var3) {}
	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {}
}
