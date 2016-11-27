package ee;

import java.util.List;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class EntityWaterEssence extends Entity {
	private int xTile;
	private int yTile;
	private int zTile;
	private int inTile;
	@SuppressWarnings("unused")
	private int yawDir;
	public static boolean grab = true;
	private boolean inGround;
	private EntityHuman player;
	private int ticksInAir;

	public EntityWaterEssence(World var1) {
		super(var1);
		bf = true;
		b(0.98F, 0.98F);
		height = length / 2.0F;
	}

	public EntityWaterEssence(World var1, double var2, double var4, double var6) {
		this(var1);
		setPosition(var2, var4, var6);
	}

	public EntityWaterEssence(World var1, EntityHuman human) {
		super(var1);
		player = human;
		xTile = -1;
		yTile = -1;
		zTile = -1;
		inTile = 0;
		inGround = false;
		yawDir = (MathHelper.floor((human.yaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 0x3) + 1;
		b(0.5F, 0.5F);
		setPositionRotation(human.locX, human.locY + human.getHeadHeight(), human.locZ, human.yaw, human.pitch);
		locX -= MathHelper.cos(yaw / 180.0F * 3.141593F) * 0.16F;
		locY -= 0.1D;
		locZ -= MathHelper.sin(yaw / 180.0F * 3.141593F) * 0.16F;
		setPosition(locX, locY, locZ);
		length = 0.0F;
		be = 10.0D;
		motX = -MathHelper.sin(yaw / 180.0F * 3.141593F) * MathHelper.cos(pitch / 180.0F * 3.141593F);
		motZ = MathHelper.cos(yaw / 180.0F * 3.141593F) * MathHelper.cos(pitch / 180.0F * 3.141593F);
		motY = -MathHelper.sin(pitch / 180.0F * 3.141593F);
		calcVelo(motX, motY, motZ, 1.991F, 1.0F);
	}

	protected void b() {}

	public void calcVelo(double var1, double var3, double var5, float var7, float var8) {
		float var9 = MathHelper.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
		var1 /= var9;
		var3 /= var9;
		var5 /= var9;
		var1 *= var7;
		var3 *= var7;
		var5 *= var7;
		motX = var1;
		motY = var3;
		motZ = var5;
		float var10 = MathHelper.sqrt(var1 * var1 + var5 * var5);
		lastYaw = yaw = (float) (Math.atan2(var1, var5) * 180.0D / 3.141592653589793D);
		lastPitch = pitch = (float) (Math.atan2(var3, var10) * 180.0D / 3.141592653589793D);
	}

	public void F_() {
		super.F_();
		if (!world.isStatic && (player == null || player.dead)) {
			die();
		}

		if (inGround) {
			int var1 = world.getTypeId(xTile, yTile, zTile);

			if (var1 == inTile) {
				die();
				return;
			}

			inGround = false;
			motX *= random.nextFloat() * 0.2F;
			motY *= random.nextFloat() * 0.2F;
			motZ *= random.nextFloat() * 0.2F;
		} else {
			ticksInAir += 1;
		}

		float var19 = MathHelper.sqrt(motX * motX + motZ * motZ);
		yaw = (float) (Math.atan2(motX, motZ) * 180.0D / 3.141592653589793D);

		for (pitch = (float) (Math.atan2(motY, var19) * 180.0D / 3.141592653589793D); pitch - lastPitch < -180.0F; lastPitch -= 360.0F);
		while (pitch - lastPitch >= 180.0F) {
			lastPitch += 360.0F;
		}

		while (yaw - lastYaw < -180.0F) {
			lastYaw -= 360.0F;
		}

		while (yaw - lastYaw >= 180.0F) {
			lastYaw += 360.0F;
		}

		pitch = lastPitch + (pitch - lastPitch) * 0.2F;
		yaw = lastYaw + (yaw - lastYaw) * 0.2F;

		if (h_()) {
			for (int var2 = 0; var2 < 4; var2++) {
				float var3 = 0.25F;
				world.a("smoke", locX - motX * var3, locY - motY * var3, locZ - motZ * var3, motX, motY, motZ);
			}

			world.makeSound(this, "random.fizzle", 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
			die();
		}

		lastX = locX;
		lastY = locY;
		lastZ = locZ;
		locX += motX;
		locY += motY;
		locZ += motZ;
		setPosition(locX, locY, locZ);

		if (locY > 256.0D && motY > 0.0D) {
			if (!EEProxy.getWorldInfo(world).hasStorm()) {
				EEProxy.getWorldInfo(world).setStorm(true);
				EEProxy.getWorldInfo(world).setWeatherDuration(600);
			} else {
				EEProxy.getWorldInfo(world).setWeatherDuration(EEProxy.getWorldInfo(world).getWeatherDuration() + 600);
			}

			die();
		}

		int nx = MathHelper.floor(locX);
		int ny = MathHelper.floor(locY);
		int nz = MathHelper.floor(locZ);

		for (int var5 = -1; var5 <= 1; var5++) {
			for (int var6 = -1; var6 <= 1; var6++) {
				for (int var7 = -1; var7 <= 1; var7++) {
					if (world.getMaterial(nx + var5, ny + var6, nz + var7) == Material.LAVA) {
						int data = world.getData(nx + var5, ny + var6, nz + var7);
						if (data == 0) {
							if (EEPatch.attemptBreak(player, nx + var5, ny + var6, nz + var7)) {
								world.setTypeId(nx + var5, ny + var6, nz + var7, 49);
								world.a("smoke", nx + var5, ny + var6, nz + var7, 0.0D, 0.1D, 0.0D);
							}
						} else if (data <= 4) {
							if (EEPatch.attemptBreak(player, nx + var5, ny + var6, nz + var7)) {
								world.setTypeId(nx + var5, ny + var6, nz + var7, 4);
								world.a("smoke", nx + var5, ny + var6, nz + var7, 0.0D, 0.1D, 0.0D);
							}
						}
					}
				}
			}
		}

		if (ticksInAir >= 3) {
			Vec3D var21 = Vec3D.create(locX, locY, locZ);
			Vec3D var22 = Vec3D.create(locX + motX, locY + motY, locZ + motZ);
			MovingObjectPosition var23 = world.rayTrace(var21, var22, true);
			var21 = Vec3D.create(locX, locY, locZ);
			var22 = Vec3D.create(locX + motX, locY + motY, locZ + motZ);
			Entity var8 = null;
			List<Entity> var9 = world.getEntities(this, boundingBox.a(motX, motY, motZ).grow(1.0D, 1.0D, 1.0D));
			double var10 = 0.0D;

			for (int var12 = 0; var12 < var9.size(); var12++) {
				Entity var13 = var9.get(var12);

				if (var13.o_()) {
					float var14 = 0.3F;
					AxisAlignedBB var15 = var13.boundingBox.grow(var14, var14, var14);
					MovingObjectPosition var16 = var15.a(var21, var22);

					if (var16 != null) {
						double var17 = var21.b(var16.pos);

						if (var17 < var10 || var10 == 0.0D) {
							var8 = var13;
							var10 = var17;
						}
					}
				}
			}

			if (var8 != null) {
				var23 = new MovingObjectPosition(var8);
			}

			if (var23 != null) {
				if (var23.type == EnumMovingObjectType.ENTITY) {
					nx = MathHelper.floor(var23.entity.locX);
					ny = MathHelper.floor(var23.entity.locY);
					nz = MathHelper.floor(var23.entity.locZ);
					makeWater(nx, ny, nz);
					die();
				}

				makeWater(nx, ny, nz);
				die();
			}
		}
	}

	@SuppressWarnings("unused")
	private boolean ConsumeRSD(int var1) {
		return EEBase.Consume(new ItemStack(Item.REDSTONE, var1), player, false) ? true : EEBase.consumeKleinStarPoint(player, 64) ? true : EEBase.Consume(
				new ItemStack(Item.COAL, var1 * 2, 1), player, true);
	}

	public void makeWater(int x, int y, int z) {
		int id = world.getTypeId(x, y, z);
		int data = world.getData(x, y, z);
		if (id == 0) {
			if (EEPatch.attemptPlace(player, x, y, z)) world.setTypeId(x, y, z, 8);
		} else if (id != 11 && (id != 10 || data <= 14)) {
			if (id == 10 || id == 11 && data < 15) {
				if (EEPatch.attemptBreak(player, x, y, z)) world.setTypeId(x, y, z, 4);
			}
		} else {
			if (EEPatch.attemptBreak(player, x, y, z)) world.setTypeId(x, y, z, 49);
		}
		die();
	}

	public void b(NBTTagCompound var1) {}

	public void a(NBTTagCompound var1) {}

	public float getShadowSize() {
		return 0.0F;
	}
}