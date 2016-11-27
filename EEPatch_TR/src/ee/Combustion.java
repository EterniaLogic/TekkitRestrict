package ee;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.ChunkPosition;
import net.minecraft.server.DamageSource;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class Combustion {
	public boolean isFlaming;
	private Random ExplosionRNG;
	private World worldObj;
	private ItemStack dropList[];
	public double explosionX;
	public double explosionY;
	public double explosionZ;
	public EntityHuman exploder;
	public float explosionSize;
	public Set<ChunkPosition> destroyedBlockPositions;

	public Combustion(World world, EntityHuman human, double explosionX, double explosionY, double explosionZ, float explosionSize) {
		isFlaming = false;
		ExplosionRNG = new Random();
		dropList = new ItemStack[64];
		destroyedBlockPositions = new HashSet<ChunkPosition>();
		worldObj = world;
		exploder = human;
		this.explosionSize = explosionSize;
		this.explosionX = explosionX;
		this.explosionY = explosionY;
		this.explosionZ = explosionZ;
	}

	protected boolean attemptBreak(EntityHuman player, int x, int y, int z) {
		if (player == null) return false;
		CraftWorld craftWorld = player.world.getWorld();
		CraftServer craftServer = player.world.getServer();
		Block block = craftWorld.getBlockAt(x, y, z);
		if (block == null) return false;
		Player ply = (Player) player.getBukkitEntity();
		if (ply == null)
			return false;
		else {
			BlockBreakEvent event = new BlockBreakEvent(block, ply);
			craftServer.getPluginManager().callEvent(event);
			return !event.isCancelled();
		}
	}

	public void doExplosionA() {
		float var1 = explosionSize;
		//byte var2 = 16;
		int nx;
		int ny;
		int nz;
		for (nx = 0; nx < 16; nx++) {
			for (ny = 0; ny < 16; ny++) {
				for (nz = 0; nz < 16; nz++) {
					if (nx == 0 || nx == 15 || ny == 0 || ny == 15 || nz == 0 || nz == 15) {
						double var6 = (nx / (15.0F)) * 2.0F - 1.0F;
						double var8 = (ny / (15.0F)) * 2.0F - 1.0F;
						double var10 = (nz / (15.0F)) * 2.0F - 1.0F;
						double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
						var6 /= var12;
						var8 /= var12;
						var10 /= var12;
						float var14 = explosionSize * (0.7F + worldObj.random.nextFloat() * 0.6F);
						double explosionX = this.explosionX;
						double explosionY = this.explosionY;
						double explosionZ = this.explosionZ;
						float var21 = 0.3F;
						for (; var14 > 0.0F; var14 -= var21 * 0.75F) {
							int var22 = MathHelper.floor(explosionX);
							int var23 = MathHelper.floor(explosionY);
							int var24 = MathHelper.floor(explosionZ);
							int var25 = worldObj.getTypeId(var22, var23, var24);
							if (var25 > 0 && net.minecraft.server.Block.byId[var25].a(exploder) > 12F)
								var14 -= net.minecraft.server.Block.byId[var25].a(exploder) * var21;
							if (var14 > 0.0F) destroyedBlockPositions.add(new ChunkPosition(var22, var23, var24));
							explosionX += var6 * var21;
							explosionY += var8 * var21;
							explosionZ += var10 * var21;
						}

					}
				}
			}
		}
		explosionSize *= 2.0F;
		nx = MathHelper.floor(explosionX - explosionSize - 1.0D);
		ny = MathHelper.floor(explosionX + explosionSize + 1.0D);
		nz = MathHelper.floor(explosionY - explosionSize - 1.0D);
		int var29 = MathHelper.floor(explosionY + explosionSize + 1.0D);
		int var7 = MathHelper.floor(explosionZ - explosionSize - 1.0D);
		int var30 = MathHelper.floor(explosionZ + explosionSize + 1.0D);
		List<Entity> var9 = worldObj.getEntities(exploder, AxisAlignedBB.b(nx, nz, var7, ny, var29, var30));
		Vec3D var31 = Vec3D.create(explosionX, explosionY, explosionZ);
		for (int var11 = 0; var11 < var9.size(); var11++) {
			Entity var32 = var9.get(var11);
			double var13 = var32.f(explosionX, explosionY, explosionZ) / explosionSize;
			if (var13 <= 1.0D) {
				double var15 = var32.locX - explosionX;
				double var17 = var32.locY - explosionY;
				double var19 = var32.locZ - explosionZ;
				double var40 = MathHelper.sqrt(var15 * var15 + var17 * var17 + var19 * var19);
				var15 /= var40;
				var17 /= var40;
				var19 /= var40;
				double var39 = worldObj.a(var31, var32.boundingBox);
				double var41 = (1.0D - var13) * var39;
				var32.damageEntity(DamageSource.EXPLOSION, (int) (((var41 * var41 + var41) / 2D) * 8D * explosionSize + 1.0D));
				var32.motX += var15 * var41;
				var32.motY += var17 * var41;
				var32.motZ += var19 * var41;
			}
		}

		explosionSize = var1;
		ArrayList<ChunkPosition> var34 = new ArrayList<ChunkPosition>();
		var34.addAll(destroyedBlockPositions);
		if (isFlaming) {
			for (int var33 = var34.size() - 1; var33 >= 0; var33--) {
				ChunkPosition var35 = var34.get(var33);
				if (worldObj.getTypeId(var35.x, var35.y, var35.z) == 0 && net.minecraft.server.Block.n[worldObj.getTypeId(var35.x, var35.y - 1, var35.z)] && ExplosionRNG.nextInt(3) == 0)
					worldObj.setTypeId(var35.x, var35.y, var35.z, net.minecraft.server.Block.FIRE.id);
			}

		}
	}

	@SuppressWarnings({ "null" })
	public void doExplosionB(boolean var1) {
		for (int var2 = 0; var2 <= 63; var2++)
			dropList[var2] = null;

		worldObj.makeSound(explosionX, explosionY, explosionZ, "kinesis", 4F,
				(1.0F + (worldObj.random.nextFloat() - worldObj.random.nextFloat()) * 0.2F) * 0.7F);
		worldObj.a("hugeexplosion", explosionX, explosionY, explosionZ, 0.0D, 0.0D, 0.0D);
		ArrayList<ChunkPosition> var25 = new ArrayList<ChunkPosition>();
		var25.addAll(destroyedBlockPositions);
		for (int var3 = var25.size() - 1; var3 >= 0; var3--) {
			ChunkPosition var4 = var25.get(var3);
			int var5 = var4.x;
			int var6 = var4.y;
			int var7 = var4.z;
			int var8 = worldObj.getTypeId(var5, var6, var7);
			if (var1) {
				double var9 = var5 + worldObj.random.nextFloat();
				double var11 = var6 + worldObj.random.nextFloat();
				double var13 = var7 + worldObj.random.nextFloat();
				double var15 = var9 - explosionX;
				double var17 = var11 - explosionY;
				double var19 = var13 - explosionZ;
				double var21 = MathHelper.sqrt(var15 * var15 + var17 * var17 + var19 * var19);
				var15 /= var21;
				var17 /= var21;
				var19 /= var21;
				double var23 = 0.5D / (var21 / explosionSize + 0.1D);
				var23 *= worldObj.random.nextFloat() * worldObj.random.nextFloat() + 0.3F;
				var15 *= var23;
				var17 *= var23;
				var19 *= var23;
				if (worldObj.random.nextInt(8) == 0)
					worldObj.a("explode", (var9 + explosionX * 1.0D) / 2D, (var11 + explosionY * 1.0D) / 2D, (var13 + explosionZ * 1.0D) / 2D, var15, var17,
							var19);
				if (worldObj.random.nextInt(8) == 0) worldObj.a("smoke", var9, var11, var13, var15, var17, var19);
			}
			if (var8 > 0 && attemptBreak(exploder, var5, var6, var7)) {
				int var27 = worldObj.getData(var5, var6, var7);
				ArrayList<ItemStack> var10 = net.minecraft.server.Block.byId[var8].getBlockDropped(worldObj, var5, var6, var7, var27, 0);
				for (Iterator<ItemStack> var28 = var10.iterator(); var28.hasNext();) {
					ItemStack var12 = var28.next();
					for (int var29 = 0; var29 < dropList.length; var29++) {
						if (dropList[var29] == null) {
							dropList[var29] = var12.cloneItemStack();
							var12 = null;
						} else if (dropList[var29].doMaterialsMatch(var12) && dropList[var29].count < dropList[var29].getMaxStackSize())
							while (dropList[var29].count < dropList[var29].getMaxStackSize() && var12 != null) {
								dropList[var29].count++;
								var12.count--;
								if (var12.count == 0) var12 = null;
							}
						if (var12 == null) break;
					}

				}

				worldObj.setTypeId(var5, var6, var7, 0);
				net.minecraft.server.Block.byId[var8].wasExploded(worldObj, var5, var6, var7);
			}
		}
		if (dropList == null) return;

		if (exploder != null) {
			if (!EEBase.isEmpty(dropList)) {
				EntityLootBall var26 = new EntityLootBall(worldObj, EEBase.playerX(exploder), EEBase.playerY(exploder), EEBase.playerZ(exploder), dropList);
				if (var26 != null) worldObj.addEntity(var26);
			}
		} else {
			if (!EEBase.isEmpty(dropList)) {
				EntityLootBall var26 = new EntityLootBall(worldObj, explosionX, explosionY, explosionZ, dropList);
				if (var26 != null) worldObj.addEntity(var26);
			}
		}
	}

}