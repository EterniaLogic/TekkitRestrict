package ee;

import ee.events.EEEnums.EEAmuletAction;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.amulet.EEVolcaniteAmuletEvent;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class ItemVolcanite extends ItemEECharged {
	public boolean itemCharging;

	public ItemVolcanite(int var1) {
		super(var1, 4);
	}

	public boolean ConsumeReagent(EntityHuman var1, boolean var2) {
		return EEBase.Consume(new ItemStack(Item.REDSTONE, 1), var1, false) ? true : EEBase.consumeKleinStarPoint(var1, 64) ? true : EEBase.Consume(
				new ItemStack(Item.COAL, 2, 1), var1, true);
	}

	public void doVaporize(ItemStack var1, World world, EntityHuman human) {
		boolean var4 = true;
		world.makeSound(human, "transmute", 0.8F, 1.5F);
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);
		
		int charge = chargeLevel(var1);
		for (int cx = -(1 + charge); cx <= 1 + charge; cx++) {
			for (int cy = -(1 + charge); cy <= 1 + charge; cy++) {
				for (int cz = -(1 + charge); cz <= 1 + charge; cz++) {
					if (world.getMaterial(x + cx, y + cy, z + cz) == Material.WATER && attemptBreak(human, x + cx, y + cy, z + cz)) {
						world.setTypeId(x + cx, y + cy, z + cz, 0);
						world.a("smoke", x + cx, y + cy, z + cz, 0.0D, 0.1D, 0.0D);

						if (var4) {
							world.makeSound(human, "random.fizz", 1.0F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
							var4 = false;
						}
					}
				}
			}
		}
	}

	public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) return var1;
		
		if (EEEventManager.callEvent(new EEVolcaniteAmuletEvent(var1, EEAction.RIGHTCLICK, var3, EEAmuletAction.CreateLava))) return var1;

		float var4 = 1.0F;
		float var5 = var3.lastPitch + (var3.pitch - var3.lastPitch) * var4;
		float var6 = var3.lastYaw + (var3.yaw - var3.lastYaw) * var4;
		double var7 = var3.lastX + (var3.locX - var3.lastX) * var4;
		double var9 = var3.lastY + (var3.locY - var3.lastY) * var4 + 1.62D - var3.height;
		double var11 = var3.lastZ + (var3.locZ - var3.lastZ) * var4;
		Vec3D var13 = Vec3D.create(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.01745329F - 3.141593F);
		float var15 = MathHelper.sin(-var6 * 0.01745329F - 3.141593F);
		float var16 = -MathHelper.cos(-var5 * 0.01745329F);
		float var17 = MathHelper.sin(-var5 * 0.01745329F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 5.0D;
		Vec3D var23 = var13.add(var18 * var21, var17 * var21, var20 * var21);
		MovingObjectPosition var24 = var2.rayTrace(var13, var23, false);

		if (var24 == null) return var1;

		if (var24.type == EnumMovingObjectType.TILE) {
			int nx = var24.b;
			int ny = var24.c;
			int nz = var24.d;

			if (!var2.a(var3, nx, ny, nz)) {
				return var1;
			}

			if (var24.face == 0) ny--;
			else if (var24.face == 1) ny++;
			else if (var24.face == 2) nz--;
			else if (var24.face == 3) nz++;
			else if (var24.face == 4) nx--;
			else if (var24.face == 5) nx++;

			if (!var3.d(nx, ny, nz)) {
				return var1;
			}

			if ((var2.isEmpty(nx, ny, nz) || !var2.getMaterial(nx, ny, nz).isBuildable()) && ConsumeReagent(var3, true) && attemptBreak(var3, nx, ny, nz)) {
				var2.setTypeIdAndData(nx, ny, nz, 10, 0);
			}
		}

		return var1;
	}

	public boolean interactWith(ItemStack var1, EntityHuman human, World world, int x, int y, int z, int face) {
		if (EEProxy.isClient(world)) return false;

		if (EEEventManager.callEvent(new EEVolcaniteAmuletEvent(var1, EEAction.RIGHTCLICK, human, x, y, z, EEAmuletAction.CreateLava))) return false;
		
		Block var8 = Block.byId[10];

		if (chargeLevel(var1) > 0) {
			world.makeSound(human, "transmute", 0.8F, 1.5F);
			human.C_();
			double dir = EEBase.direction(human);
			if (world.getTypeId(x, y, z) != Block.SNOW.id) {
				if (face == 0) y--;
				else if (face == 1) y++;
				else if (face == 2) z--;
				else if (face == 3) z++;
				else if (face == 4) x--;
				else if (face == 5) x++;
			}

			if (y == 127) return false;

			if (dir == 0.0D) {
				for (int var11 = -(chargeLevel(var1) / 7 + 1); var11 <= chargeLevel(var1) / 7 + 1; var11++) {
					for (int var12 = -(chargeLevel(var1) / 7 + 1); var12 <= chargeLevel(var1) / 7 + 1; var12++) {
						int id = world.getTypeId(x + var11, y, z + var12);
						if ((id == 0 || id == 78) && attemptBreak(human, x + var11, y, z + var12)) {
							if (!ConsumeReagent(human, true)) {
								resetCharge(var1, world, human, true);
								return false;
							}

							world.setTypeId(x + var11, y, z + var12, 10);
						}
					}
				}
			} else if (dir == 1.0D) {
				for (int var11 = 0; var11 <= chargeLevel(var1); var11++) {
					int id = world.getTypeId(x, y + var11, z);
					if ((id == 0 || id == 78) && attemptBreak(human, x, y + var11, z)) {
						if (!ConsumeReagent(human, true)) {
							resetCharge(var1, world, human, true);
							return false;
						}

						world.setTypeId(x, y + var11, z, 10);
					}
				}
			} else if (dir == 2.0D) {
				for (int var11 = 0; var11 <= chargeLevel(var1); var11++) {
					int id = world.getTypeId(x, y, z + var11);
					if ((id == 0 || id == 78) && attemptBreak(human, x, y, z + var11)) {
						if (!ConsumeReagent(human, true)) {
							resetCharge(var1, world, human, true);
							return false;
						}

						world.setTypeId(x, y, z + var11, 10);
					}
				}
			} else if (dir == 3.0D) {
				for (int var11 = 0; var11 <= chargeLevel(var1); var11++) {
					int id = world.getTypeId(x - var11, y, z);
					if ((id == 0 || id == 78) && attemptBreak(human, x - var11, y, z)) {
						if (!ConsumeReagent(human, true)) {
							resetCharge(var1, world, human, true);
							return false;
						}

						world.setTypeId(x - var11, y, z, 10);
					}
				}
			} else if (dir == 4.0D) {
				for (int var11 = 0; var11 <= chargeLevel(var1); var11++) {
					int id = world.getTypeId(x, y, z - var11);
					if ((id == 0 || id == 78) && attemptBreak(human, x, y, z - var11)) {
						if (!ConsumeReagent(human, true)) {
							resetCharge(var1, world, human, true);
							return false;
						}

						world.setTypeId(x, y, z - var11, 10);
					}
				}
			} else if (dir == 5.0D) {
				for (int var11 = 0; var11 <= chargeLevel(var1); var11++) {
					int id = world.getTypeId(x + var11, y, z);
					if ((id == 0 || id == 78) && attemptBreak(human, x + var11, y, z)) {
						if (!ConsumeReagent(human, true)) {
							resetCharge(var1, world, human, true);
							return false;
						}

						world.setTypeId(x + var11, y, z, 10);
					}
				}
			}

			resetCharge(var1, world, human, false);
			return true;
		}

		if (chargeLevel(var1) < 1) {
			if (world.getTypeId(x, y, z) == Block.SNOW.id) {
				face = 0;
			} else {
				if (face == 0) y--;
				else if (face == 1) y++;
				else if (face == 2) z--;
				else if (face == 3) z++;
				else if (face == 4) x--;
				else if (face == 5) x++;
			}

			if (y == 127) return false;

			if (world.mayPlace(10, x, y, z, false, face) && world.getTypeId(x, y, z) == 0 && attemptBreak(human, x, y, z)) {
				if (ConsumeReagent(human, true)) {
					if (world.setTypeIdAndData(x, y, z, 10, filterData(var1.getData()))) {
						Block.byId[10].postPlace(world, x, y, z, face);
						Block.byId[10].postPlace(world, x, y, z, human);
						world.makeSound(x + 0.5F, y + 0.5F, z + 0.5F, var8.stepSound.getName(), (var8.stepSound.getVolume1() + 1.0F) / 2.0F,
								var8.stepSound.getVolume2() * 0.8F);
						return true;
					}

					return false;
				}

				resetCharge(var1, world, human, true);
				return false;
			}
		}

		return false;
	}

	public boolean onItemUseFirst(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7) {
		return false;
	}

	int checkTicks = 0;
	boolean allowed = false;
	public void doPassive(ItemStack var1, World var2, EntityHuman var3) {
		if (checkTicks <= 0){
			allowed = !EEEventManager.callEvent(new EEVolcaniteAmuletEvent(var1, EEAction.PASSIVE, var3, EEAmuletAction.FireImmune));
			checkTicks = 600;
			if (!allowed) EEProxy.setPlayerFireImmunity(var3, false);
		}
		if (allowed && !EEProxy.isEntityFireImmune(var3)) {
			EEProxy.setPlayerFireImmunity(var3, true);
		}

		AxisAlignedBB var4 = AxisAlignedBB.b(var3.boundingBox.a, var3.boundingBox.b - 0.2D, var3.boundingBox.c, var3.boundingBox.d, var3.boundingBox.e, var3.boundingBox.f);
		EEBase.updatePlayerInLava(var3, var2.b(var4, Material.LAVA));
	}

	public void doActive(ItemStack var1, World var2, EntityHuman var3) {}

	public void doHeld(ItemStack var1, World var2, EntityHuman var3) {}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EEVolcaniteAmuletEvent(var1, EEAction.RELEASE, var3, EEAmuletAction.CreateLavaBall))) return;
		var3.C_();
		var2.makeSound(var3, "transmute", 0.6F, 1.0F);
		var2.addEntity(new EntityLavaEssence(var2, var3, chargeLevel(var1) >= 8));
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EEVolcaniteAmuletEvent(var1, EEAction.ALTERNATE, var3, EEAmuletAction.Vaporize))) return;
		doVaporize(var1, var2, var3);
	}

	public void doLeftClick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {}
}