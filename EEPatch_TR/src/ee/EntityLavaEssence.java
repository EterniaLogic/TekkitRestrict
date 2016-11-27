package ee;

import java.util.List;
//import java.util.Random;



import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import ee.events.EEEventManager;
import ee.events.entity.WaterVaporizeEvent;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.DamageSource;
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

public class EntityLavaEssence extends Entity {
	private boolean chargeMax;
	private EntityHuman player;
	private int ticksInAir;
	private int xTile;
	private int yTile;
	private int zTile;
	private int inTile;
	@SuppressWarnings("unused")
	private int yawDir;
	public static boolean grab = true;
	private boolean inGround;

	public EntityLavaEssence(World world) {
		super(world);
		bf = true;
		b(0.98F, 0.98F);
		height = length / 2.0F;
	}

	public EntityLavaEssence(World world, EntityHuman human, boolean var3) {
		super(world);
		player = human;
		xTile = -1;
		yTile = -1;
		zTile = -1;
		inTile = 0;
		inGround = false;
		chargeMax = var3;
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
		calcVelo(motX, motY, motZ, 0.991F, 1.0F);
	}

	public EntityLavaEssence(World var1, double var2, double var4, double var6) {
		this(var1);
		setPosition(var2, var4, var6);
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
		lastYaw = yaw = (float) (Math.atan2(var1, var5) * 180.0D / Math.PI);
		lastPitch = pitch = (float) (Math.atan2(var3, var10) * 180.0D / Math.PI);
	}

	@Override
	public void F_() {
		super.F_();

		if (!world.isStatic && (player == null || player.dead)) {
			die();
		}

		float var1 = MathHelper.sqrt(motX * motX + motZ * motZ);
		yaw = (float) (Math.atan2(motX, motZ) * 180.0D / Math.PI);

		for (pitch = (float) (Math.atan2(motY, var1) * 180.0D / Math.PI); pitch - lastPitch < -180.0F; lastPitch -= 360.0F);
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
		lastX = locX;
		lastY = locY;
		lastZ = locZ;
		locX += motX;
		locY += motY;
		locZ += motZ;
		setPosition(locX, locY, locZ);

		if (inGround) {
			int id = world.getTypeId(xTile, yTile, zTile);

			if (id == inTile) return;

			inGround = false;
			motX *= random.nextFloat() * 0.2F;
			motY *= random.nextFloat() * 0.2F;
			motZ *= random.nextFloat() * 0.2F;
		} else {
			ticksInAir += 1;
		}

		if (locY > 256.0D && motY > 0.0D) {
			if (EEBase.consumeKleinStarPoint(player, 1)) {
				if (EEProxy.getWorldInfo(world).isThundering()) {
					EEProxy.getWorldInfo(world).setThundering(false);
					EEProxy.getWorldInfo(world).setThunderDuration(0);
				}

				if (EEProxy.getWorldInfo(world).hasStorm()) {
					EEProxy.getWorldInfo(world).setStorm(false);
					EEProxy.getWorldInfo(world).setWeatherDuration(0);
				}

				die();
			} else if (EEBase.Consume(new ItemStack(Item.REDSTONE, 1), player, true)) {
				if (EEProxy.getWorldInfo(world).isThundering()) {
					EEProxy.getWorldInfo(world).setThundering(false);
					EEProxy.getWorldInfo(world).setThunderDuration(0);
				}

				if (EEProxy.getWorldInfo(world).hasStorm()) {
					EEProxy.getWorldInfo(world).setStorm(false);
					EEProxy.getWorldInfo(world).setWeatherDuration(0);
				}

				die();
			} else {
				die();
			}
		}

		int x = MathHelper.floor(locX);
		int y = MathHelper.floor(locY);
		int z = MathHelper.floor(locZ);

		int nx, ny, nz;
		for (int var5 = -2; var5 <= 2; var5++) {
			for (int var6 = -2; var6 <= 2; var6++) {
				for (int var7 = -2; var7 <= 2; var7++) {
					nx = x + var5;
					ny = y + var6;
					nz = z + var7;
					if (world.getMaterial(nx, ny, nz) == Material.WATER) {
						if (EEEventManager.callEvent(new WaterVaporizeEvent(player, nx, ny, nz))) continue;
						
						if (attemptBreak(player, nx, ny, nz)) {
							world.setTypeId(nx, ny, nz, 0);
							world.a("smoke", nx, ny, nz, 0.0D, 0.1D, 0.0D);
							world.makeSound(this, "random.fizz", 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
						}
					}
				}
			}
		}

		if (ticksInAir >= 3) {
			Vec3D var19 = Vec3D.create(locX, locY, locZ);
			Vec3D var20 = Vec3D.create(locX + motX, locY + motY, locZ + motZ);
			MovingObjectPosition var21 = world.rayTrace(var19, var20, true);
			var19 = Vec3D.create(locX, locY, locZ);
			var20 = Vec3D.create(locX + motX, locY + motY, locZ + motZ);

			List<Entity> var9 = world.getEntities(this, boundingBox.a(motX, motY, motZ).grow(1.0D, 1.0D, 1.0D));
			double var10 = 0.0D;

			Entity var8 = null;
			Entity var13;
			for (int var12 = 0; var12 < var9.size(); var12++) {
				var13 = var9.get(var12);

				if (var13.o_() && (player == null || var13 != player)) {
					float var14 = 0.3F;
					AxisAlignedBB var15 = var13.boundingBox.grow(var14, var14, var14);
					MovingObjectPosition var16 = var15.a(var19, var20);

					if (var16 != null) {
						double var17 = var19.b(var16.pos);

						if (var17 < var10 || var10 == 0.0D) {
							var8 = var13;
							var10 = var17;
						}
					}
				}
			}

			if (h_()) {
				for (int var12 = 0; var12 < 4; var12++) {
					float var22 = 0.25F;
					world.a("smoke", locX - motX * var22, locY - motY * var22, locZ - motZ * var22, motX, motY,
							motZ);
				}

				if (chargeMax) {
					world.makeSound(this, "random.fizz", 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));

					if (world.getMaterial(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ)) == Material.WATER) {
						if (attemptBreak(player, MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ)))
							world.setTypeId(MathHelper.floor(locX), MathHelper.floor(locY), MathHelper.floor(locZ), 0);
					}
				}
			}

			if (var8 != null) {
				var21 = new MovingObjectPosition(var8);
			}

			if (var21 != null) {
				if (var21.type == EnumMovingObjectType.ENTITY) {
					if (EEBase.consumeKleinStarPoint(player, 2)) {
						if (!EEProxy.isEntityFireImmune(var21.entity)) {
							var21.entity.damageEntity(DamageSource.LAVA, 12);
							var21.entity.setOnFire(600);
						}

						die();
					} else if (EEBase.Consume(new ItemStack(Item.REDSTONE, 2), player, false)) {
						if (!EEProxy.isEntityFireImmune(var21.entity)) {
							var21.entity.damageEntity(DamageSource.LAVA, 12);
							var21.entity.setOnFire(600);
						}

						die();
					} else if (!EEProxy.isEntityFireImmune(var21.entity)) {
						var21.entity.damageEntity(DamageSource.LAVA, 1);
						var21.entity.setOnFire(10);
					}

					die();
				} else {
					makeLava(x, y, z);
				}

				die();
			}
		}
	}

	public void makeLava(int x, int y, int z) {
		if (!EEProxy.isClient(world)) {
			if (EEBase.consumeKleinStarPoint(player, 64)) {
				if (world.getTypeId(x, y, z) == 0) {
					if (attemptBreak(player, x, y, z)) world.setTypeId(x, y, z, 10);
				}
			} else if (EEBase.Consume(new ItemStack(Item.REDSTONE, 1), player, false)) {
				if (world.getTypeId(x, y, z) == 0) {
					if (attemptBreak(player, x, y, z)) world.setTypeId(x, y, z, 10);
				}
			} else if (EEBase.Consume(new ItemStack(Item.COAL, 2, x), player, false) && world.getTypeId(x, y, z) == 0) {
				if (attemptBreak(player, x, y, z)) world.setTypeId(x, y, z, 10);
			}
			die();
		}
	}

	public void b(NBTTagCompound var1) {}
	public void a(NBTTagCompound var1) {}

	public float getShadowSize() {
		return 0.0F;
	}

	protected boolean attemptBreak(EntityHuman human, int x, int y, int z) {
		if (human == null) return false;

		org.bukkit.block.Block block = human.world.getWorld().getBlockAt(x, y, z);
		if (block == null) return false;

		Player player = (Player) human.getBukkitEntity();
		if (player == null) return false;

		BlockBreakEvent event = new BlockBreakEvent(block, player);
		human.world.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}
}
