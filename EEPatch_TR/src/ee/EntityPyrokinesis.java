package ee;

import java.util.List;
import java.util.Random;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class EntityPyrokinesis extends Entity {
	private EntityHuman player;
	private Random rRand;
	private int xTile;
	private int yTile;
	private int zTile;
	private int inTile;
	@SuppressWarnings("unused")
	private int yawDir;
	public static boolean grab = false;
	private boolean inGround;
	private int ticksInAir;

	public EntityPyrokinesis(World world) {
		super(world);
		bf = true;
		b(0.98F, 0.98F);
		height = length / 2.0F;
	}

	public EntityPyrokinesis(World world, double var2, double var4, double var6) {
		this(world);
		setPosition(var2, var4, var6);
		ticksInAir = 0;
	}

	public EntityPyrokinesis(World world, EntityHuman human) {
		super(world);
		player = human;
		xTile = -1;
		yTile = -1;
		zTile = -1;
		inTile = 0;
		ticksInAir = 0;
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
		calcVelo(motX, motY, motZ, 1.999F, 1.0F);
		rRand = new Random();
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

	@Override
	public void F_() {
		super.F_();

		if (!world.isStatic && (player == null || player.dead)) {
			die();
		}

		if (inGround) {
			int var1 = world.getTypeId(xTile, yTile, zTile);

			if (var1 == inTile) return;

			inGround = false;
			motX *= random.nextFloat() * 0.2F;
			motY *= random.nextFloat() * 0.2F;
			motZ *= random.nextFloat() * 0.2F;
		} else {
			ticksInAir += 1;
		}

		float var16 = MathHelper.sqrt(motX * motX + motZ * motZ);
		yaw = (float) (Math.atan2(motX, motZ) * 180.0D / 3.141592653589793D);

		for (pitch = (float) (Math.atan2(motY, var16) * 180.0D / 3.141592653589793D); pitch - lastPitch < -180.0F; lastPitch -= 360.0F);
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

			world.makeSound(this, "heal", 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
			die();
		}

		lastX = locX;
		lastY = locY;
		lastZ = locZ;
		locX += motX;
		locY += motY;
		locZ += motZ;
		setPosition(locX, locY, locZ);
		world.a("smoke", locX, locY + 0.5D, locZ, 0.0D, 0.0D, 0.0D);

		for (int var2 = 0; var2 <= 8; var2++) {
			world.a("flame", locX + (world.random.nextFloat() - 0.5D) / 3.5D, locY + (world.random.nextFloat() - 0.5D) / 3.5D,
					locZ + (world.random.nextFloat() - 0.5D) / 3.5D, motX * 0.8D, motY * 0.8D, motZ * 0.8D);
		}

		if (ticksInAir >= 3) {
			Vec3D var18 = Vec3D.create(locX, locY, locZ);
			Vec3D var17 = Vec3D.create(locX + motX, locY + motY, locZ + motZ);
			MovingObjectPosition var4 = world.rayTrace(var18, var17, grab);
			var18 = Vec3D.create(locX, locY, locZ);
			var17 = Vec3D.create(locX + motX, locY + motY, locZ + motZ);

			if (var4 != null) {
				var17 = Vec3D.create(var4.pos.a, var4.pos.b, var4.pos.c);
			}

			Entity var5 = null;
			List<Entity> var6 = world.getEntities(this, boundingBox.a(motX, motY, motZ).grow(1.0D, 1.0D, 1.0D));
			double var7 = 0.0D;

			for (int var9 = 0; var9 < var6.size(); var9++) {
				Entity var10 = var6.get(var9);

				if (!(var10 instanceof EntityHuman) && var10.o_() && ticksInAir >= 2) {
					float var11 = 0.3F;
					AxisAlignedBB var12 = var10.boundingBox.grow(var11, var11, var11);
					MovingObjectPosition var13 = var12.a(var18, var17);

					if (var13 != null) {
						double var14 = var18.b(var13.pos);

						if (var14 < var7 || var7 == 0.0D) {
							var5 = var10;
							var7 = var14;
						}
					}
				}
			}

			if (var5 != null) {
				var4 = new MovingObjectPosition(var5);
				var4.entity.damageEntity(DamageSource.LAVA, 2);
				var4.entity.setOnFire(100);
				fireBurst((float) var4.entity.locX, (float) var4.entity.locY, (float) var4.entity.locZ);
			} else if (var4 != null) {
				fireBurst(var4.b, var4.c, var4.d);
				die();
			}
		}
	}

	public void fireBurst(float var1, float var2, float var3) {
		if (EEProxy.isClient(world)) return;
		world.makeSound(var1 + 0.5F, var2 + 0.5F, var3 + 0.5F, "fire.ignite", 1.0F, rRand.nextFloat() * 0.4F + 0.8F);

		if (player != null) {
			List<Entity> var4 = world.getEntities(player,
					AxisAlignedBB.b(var1 - 2.0D, var2 - 2.0D, var3 - 2.0D, var1 + 2.0D, var2 + 2.0D, var3 + 2.0D));

			for (int var5 = 0; var5 < var4.size(); var5++) {
				if (!(var4.get(var5) instanceof EntityItem)) {
					Entity var6 = var4.get(var5);
					EEProxy.dealFireDamage(var6, 5);
					var6.setOnFire(100);
				}

			}

		}

		for (int var11 = -1; var11 <= 1; var11++) {
			for (int var5 = -1; var5 <= 1; var5++) {
				for (int var12 = -1; var12 <= 1; var12++) {
					int id = world.getTypeId((int) var1 + var11, (int) var2 + var5, (int) var3 + var12);
					if (id == 0 || id == 78) {
						if (EEPatch.attemptBreak(player, (int) var1 + var11, (int) var2 + var5, (int) var3 + var12))
							world.setTypeId((int) var1 + var11, (int) var2 + var5, (int) var3 + var12, Block.FIRE.id);
					}
				}
			}
		}

		for (int var11 = -2; var11 <= 2; var11++) {
			for (int var5 = -2; var5 <= 2; var5++) {
				for (int var12 = -2; var12 <= 2; var12++) {
					int nx = (int) var1 + var11;
					int ny = (int) var2 + var5;
					int nz = (int) var3 + var12;
					int id = world.getTypeId(nx, ny, nz);
					if (id == Block.OBSIDIAN.id) {
						if (EEPatch.attemptBreak(player, nx, ny, nz)) world.setTypeIdAndData(nx, ny, nz, Block.LAVA.id, 0);
					} else if (id == Block.SAND.id) {
						if (EEPatch.attemptBreak(player, nx, ny, nz)) world.setTypeId(nx, ny, nz, Block.GLASS.id);
					} else if (id == Block.ICE.id) {
						if (EEPatch.attemptBreak(player, nx, ny, nz)) world.setTypeId(nx, ny, nz, Block.WATER.id);
					}

					if (world.random.nextInt(5) == 0 && world.getTypeId(nx, ny + 1, nz) == 0) {
						world.a("largesmoke", nx, ny + 1, nz, 0.0D, 0.0D, 0.0D);
						world.a("flame", nx, ny + 1, nz, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}

		world.a("largesmoke", var1, var2 + 1.0F, var3, 0.0D, 0.0D, 0.0D);
		world.a("flame", var1, var2, var3, 0.0D, 0.0D, 0.0D);
	}

	public void b(NBTTagCompound var1) {}
	public void a(NBTTagCompound var1) {}

	public float getShadowSize() {
		return 0.0F;
	}


}