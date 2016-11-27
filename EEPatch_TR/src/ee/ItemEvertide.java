package ee;

import ee.events.EEEnums.EEAmuletAction;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.amulet.EEEvertideAmuletEvent;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MovingObjectPosition;
//import net.minecraft.server.StepSound;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class ItemEvertide extends ItemEECharged {
	public ItemEvertide(int var1) {
		super(var1, 4);
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int blockface) {
		if (EEProxy.isClient(world)) return false;

		if (EEEventManager.callEvent(new EEEvertideAmuletEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAmuletAction.CreateWater))) return false;
		
		Block var8 = Block.byId[8];
		
		int charge = chargeLevel(item);

		if (charge > 0) {
			world.makeSound(human, "waterball", 0.8F, 1.5F);
			human.C_();
			double dir = EEBase.direction(human);

			if (world.getTypeId(x, y, z) != Block.SNOW.id) {
				if (blockface == 0) y--;
				else if (blockface == 1) y++;
				else if (blockface == 2) z--;
				else if (blockface == 3) z++;
				else if (blockface == 4) x--;
				else if (blockface == 5) x++;
			}

			if (y == 127) return false;

			int nx = 0;
			int ny = 0;
			int nz = 0;
			int id = 1;
			
			
			if (dir == 0.0D) {
				ny = y;
				for (int var11 = -(charge / 7 + 1); var11 <= charge / 7 + 1; var11++) {
					for (int var12 = -(charge / 7 + 1); var12 <= charge / 7 + 1; var12++) {
						nx = x + var11;
						nz = z + var12;
						id = world.getTypeId(nx,ny, nz);
						if (id == 0 || id == 78) {
							if (attemptBreak(human, nx, ny, nz)) world.setTypeId(nx, ny, nz, 8);
						}
					}
				}
			} else if (dir == 1.0D) {
				nx = x;
				nz = z;
				for (int var11 = 0; var11 <= charge; var11++) {
					ny = y + var11;
					id = world.getTypeId(nx,ny, nz);
					if (id == 0 || id == 78) {
						if (attemptBreak(human, nx, ny, nz)) world.setTypeId(nx, ny, nz, 8);
					}

					if (charge == 7) {
						for (int var12 = 1; var12 < 4; var12++) {
							id = world.getTypeId(nx, ny + var12, nz);
							if (id == 0 || id == 78) {//added + var12 to the second one
								if (attemptBreak(human, nx, ny + var12, nz))
									world.setTypeId(nx, ny + var12, nz, 8);
							}
						}
					}
				}
			} else if (dir == 2.0D) {
				ny = y;
				nx = x;
				for (int var11 = 0; var11 <= charge; var11++) {
					nz = z + var11;
					id = world.getTypeId(nx, ny, nz);
					if (id == 0 || id == 78) {
						if (attemptBreak(human, nx, ny, nz)) world.setTypeId(nx, ny, nz, 8);
					}

					if (charge == 7) {
						id = world.getTypeId(nx - 1, ny, nz);
						if (id == 0 || id == 78) {
							if (attemptBreak(human, nx - 1, ny, nz)) world.setTypeId(nx - 1, ny, nz, 8);
						}

						id = world.getTypeId(nx + 1, ny, nz);
						if (id == 0 || id == 78) {
							if (attemptBreak(human, nx + 1, ny, nz)) world.setTypeId(nx + 1, ny, nz, 8);
						}
					}
				}
			} else if (dir == 3.0D) {
				ny = y;
				nz = z;
				for (int var11 = 0; var11 <= charge; var11++) {
					nx = x - var11;
					id = world.getTypeId(nx, ny, nz);
					if (id == 0 || id == 78) {
						if (attemptBreak(human, nx, ny, nz)) world.setTypeId(nx, ny, nz, 8);
					}

					if (charge == 7) {
						id = world.getTypeId(nx, ny, nz - 1);
						if (id == 0 || id == 78) {
							if (attemptBreak(human, nx, ny, nz - 1)) world.setTypeId(nx, ny, nz - 1, 8);
						}

						id = world.getTypeId(nx, ny, nz + 1);
						if (id == 0 || id == 78) {
							if (attemptBreak(human, nx, ny, nz + 1)) world.setTypeId(nx, ny, nz + 1, 8);
						}
					}
				}
			} else if (dir == 4.0D) {
				ny = y;
				nx = x;
				for (int var11 = 0; var11 <= charge; var11++) {
					nz = z - var11;
					id = world.getTypeId(nx, ny, nz);
					if (id == 0 || id == 78) {
						if (attemptBreak(human, nx, ny, nz)) world.setTypeId(nx, ny, nz, 8);
					}

					if (charge == 7) {
						id = world.getTypeId(nx - 1, ny, nz);
						if (id == 0 || id == 78) {
							if (attemptBreak(human, nx - 1, ny, nz)) world.setTypeId(nx - 1, ny, nz, 8);
						}

						id = world.getTypeId(nx + 1, ny, nz);
						if (id == 0 || id == 78) {
							if (attemptBreak(human, nx + 1, ny, nz)) world.setTypeId(nx + 1, ny, nz, 8);
						}
					}
				}
			} else if (dir == 5.0D) {
				ny = y;
				nz = z;
				for (int var11 = 0; var11 <= charge; var11++) {
					nx = x + var11;
					id = world.getTypeId(nx, ny, nz);
					if (id == 0 || id == 78) {
						if (attemptBreak(human, nx, ny, nz)) world.setTypeId(nx, ny, nz, 8);
					}

					if (charge == 7) {
						id = world.getTypeId(nx, ny, nz - 1);
						if (id == 0 || id == 78) {
							if (attemptBreak(human, nx, ny, nz - 1)) world.setTypeId(nx, ny, nz - 1, 8);
						}
						
						id = world.getTypeId(nx, ny, nz + 1);
						if (id == 0 || id == 78) {
							if (attemptBreak(human, nx, ny, nz + 1)) world.setTypeId(nx, ny, nz + 1, 8);
						}
					}
				}
			}

			resetCharge(item, world, human, false);
			return true;
		}

		if (charge < 1) {
			if (world.getTypeId(x, y, z) == Block.SNOW.id) {
				blockface = 0;
			} else {
				if (blockface == 0) y--;
				else if (blockface == 1) y++;
				else if (blockface == 2) z--;
				else if (blockface == 3) z++;
				else if (blockface == 4) x--;
				else if (blockface == 5) x++;
			}

			if (y == 127) return false;

			if (world.mayPlace(8, x, y, z, false, blockface)) {
				if (attemptPlace(human, x, y, z)) {
					if (world.setTypeIdAndData(x, y, z, 8, filterData(item.getData()))) {
						Block.byId[8].postPlace(world, x, y, z, blockface);
						Block.byId[8].postPlace(world, x, y, z, human);
						world.makeSound(x + 0.5F, y + 0.5F, z + 0.5F, var8.stepSound.getName(), (var8.stepSound.getVolume1() + 1.0F) / 2.0F,
								var8.stepSound.getVolume2() * 0.8F);
					}
					return true;
				}
			}
		}

		return false;
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return item;

		float var4 = 1.0F;
		float var5 = human.lastPitch + (human.pitch - human.lastPitch) * var4;
		float var6 = human.lastYaw + (human.yaw - human.lastYaw) * var4;
		double var7 = human.lastX + (human.locX - human.lastX) * var4;
		double var9 = human.lastY + (human.locY - human.lastY) * var4 + 1.62D - human.height;
		double var11 = human.lastZ + (human.locZ - human.lastZ) * var4;
		Vec3D var13 = Vec3D.create(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.01745329F - 3.141593F);
		float var15 = MathHelper.sin(-var6 * 0.01745329F - 3.141593F);
		float var16 = -MathHelper.cos(-var5 * 0.01745329F);
		float var17 = MathHelper.sin(-var5 * 0.01745329F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 5.0D;
		Vec3D var23 = var13.add(var18 * var21, var17 * var21, var20 * var21);
		MovingObjectPosition var24 = world.rayTrace(var13, var23, false);

		if (var24 == null) {
			return item;
		}

		if (var24.type == EnumMovingObjectType.TILE) {
			int nx = var24.b;
			int ny = var24.c;
			int nz = var24.d;

			if (!world.a(human, nx, ny, nz)) {
				return item;
			}

			if (var24.face == 0) ny--;
			else if (var24.face == 1) ny++;
			else if (var24.face == 2) nz--;
			else if (var24.face == 3) nz++;
			else if (var24.face == 4) nx--;
			else if (var24.face == 5) nx++;

			if (!human.d(nx, ny, nz)) return item;

			if ((world.isEmpty(nx, ny, nz)) || (!world.getMaterial(nx, ny, nz).isBuildable())) {
				if (attemptBreak(human, nx, ny, nz)) {
					world.setTypeIdAndData(nx, ny, nz, 8, 0);
				}
			}
		}

		return item;
	}

	public boolean onItemUseFirst(ItemStack item, EntityHuman entityPlayer, World var3, int var4, int var5, int var6, int var7) {
		return false;
	}

	public void ConsumeReagent(ItemStack item, EntityHuman entityPlayer, boolean var3) {}

	private int checkTicks = 0;
	private boolean airAllowed = true;
	public void doPassive(ItemStack item, World world, EntityHuman human) {
		if (human.getAirTicks() < 0){
			if (checkTicks <= 0){
				airAllowed = !EEEventManager.callEvent(new EEEvertideAmuletEvent(item, EEAction.PASSIVE, human, EEAmuletAction.StopDrowning));
				checkTicks = 300;
			}
			if (airAllowed) human.setAirTicks(human.maxAirTicks);
			checkTicks--;
		}

		AxisAlignedBB var4 = AxisAlignedBB.b(human.boundingBox.a, human.boundingBox.b - 0.2D, human.boundingBox.c, human.boundingBox.d, human.boundingBox.e, human.boundingBox.f);
		EEBase.updatePlayerInWater(human, world.b(var4, Material.WATER));
	}

	public void doActive(ItemStack item, World world, EntityHuman entityPlayer) {}

	public void doHeld(ItemStack item, World world, EntityHuman entityPlayer) {}

	public void doRelease(ItemStack item, World world, EntityHuman human) {
		if (EEEventManager.callEvent(new EEEvertideAmuletEvent(item, EEAction.RELEASE, human, EEAmuletAction.CreateWaterBall))) return;
		human.C_();
		world.makeSound(human, "waterball", 0.6F, 1.0F);
		world.addEntity(new EntityWaterEssence(world, human));
	}

	public void doLeftClick(ItemStack item, World world, EntityHuman entityPlayer) {}

	public void doToggle(ItemStack item, World world, EntityHuman entityPlayer) {}
}