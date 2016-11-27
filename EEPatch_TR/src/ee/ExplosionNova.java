package ee;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityExplodeEvent;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.ChunkPosition;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;
import forge.ISpecialResistance;

public class ExplosionNova {
	public boolean isFlaming;
	private Random ExplosionRNG;
	private World worldObj;
	private ItemStack[] dropList;
	public double explosionX;
	public double explosionY;
	public double explosionZ;
	public Entity exploder;
	public float explosionSize;
	public Set<ChunkPosition> destroyedBlockPositions;
	private EntityHuman player;
	private boolean stopExplosion = false;

	public ExplosionNova(World var1, EntityHuman var2, double var3, double var5, double var7, float var9) {
		isFlaming = false;
		player = var2;
		ExplosionRNG = new Random();
		destroyedBlockPositions = new HashSet<ChunkPosition>();
		dropList = new ItemStack[64];
		worldObj = var1;
		exploder = var2;
		explosionSize = var9;
		explosionX = var3;
		explosionY = var5;
		explosionZ = var7;
	}

	public void doExplosionA() {
		float var1 = explosionSize;
		byte var2 = 16;

		for (int var3 = 0; var3 < var2; var3++) {
			for (int var4 = 0; var4 < var2; var4++) {
				for (int var5 = 0; var5 < var2; var5++) {
					if (var3 == 0 || var3 == var2 - 1 || var4 == 0 || var4 == var2 - 1 || var5 == 0 || var5 == var2 - 1) {
						double var6 = var3 / (var2 - 1.0F) * 2.0F - 1.0F;
						double var8 = var4 / (var2 - 1.0F) * 2.0F - 1.0F;
						double var10 = var5 / (var2 - 1.0F) * 2.0F - 1.0F;
						double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
						var6 /= var12;
						var8 /= var12;
						var10 /= var12;
						float var14 = explosionSize * (0.7F + worldObj.random.nextFloat() * 0.8F);
						double var15 = explosionX;
						double var17 = explosionY;
						double var19 = explosionZ;
						float var21 = 0.6F;
						for (; var14 > 0.0F; var14 -= var21 * 0.75F) {
							int var22 = MathHelper.floor(var15);
							int var23 = MathHelper.floor(var17);
							int var24 = MathHelper.floor(var19);
							int var25 = worldObj.getTypeId(var22, var23, var24);
							if (var25 > 0)
								if (Block.byId[var25] instanceof ISpecialResistance) {
									ISpecialResistance var26 = (ISpecialResistance) Block.byId[var25];
									var14 -= (var26.getSpecialExplosionResistance(worldObj, var22, var23, var24, explosionX, explosionY, explosionZ, exploder) + 0.3F) * var21;
								} else {
									var14 -= (Block.byId[var25].a(exploder) + 0.3F) * var21;
								}
							if (var14 > 0.0F || Block.byId[var25].a(exploder) < 30.0F)
								destroyedBlockPositions.add(new ChunkPosition(var22, var23, var24));
							var15 += var6 * var21;
							var17 += var8 * var21;
							var19 += var10 * var21;
						}
					}
				}
			}
		}

		explosionSize *= 1.7F;
		int var3 = MathHelper.floor(explosionX - explosionSize - 1.0D);
		int var4 = MathHelper.floor(explosionX + explosionSize + 1.0D);
		int var5 = MathHelper.floor(explosionY - explosionSize - 1.0D);
		int var29 = MathHelper.floor(explosionY + explosionSize + 1.0D);
		int var7 = MathHelper.floor(explosionZ - explosionSize - 1.0D);
		int var30 = MathHelper.floor(explosionZ + explosionSize + 1.0D);
		List<Entity> var9 = worldObj.getEntities(exploder, AxisAlignedBB.b(var3, var5, var7, var4, var29, var30));
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
				var32.motX += var15 * var41;
				var32.motY += var17 * var41;
				var32.motZ += var19 * var41;
			}
		}

		explosionSize = var1;

		ArrayList<org.bukkit.block.Block> blockList = new ArrayList<org.bukkit.block.Block>();
		for (Iterator<ChunkPosition> cpIterator = destroyedBlockPositions.iterator(); cpIterator.hasNext();) {
			ChunkPosition cpos = cpIterator.next();
			org.bukkit.block.Block block = worldObj.getWorld().getBlockAt(cpos.x, cpos.y, cpos.z);
			if (block.getTypeId() == 0) continue;
			blockList.add(block);
		}

		org.bukkit.entity.Entity explode = exploder == null ? new EntityTNTPrimed(worldObj).getBukkitEntity() : exploder.getBukkitEntity();
		Location location = new Location(worldObj.getWorld(), explosionX, explosionY, explosionZ);

		EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, explosionSize);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()){
			stopExplosion = true;
			return;
		}


		ArrayList<ChunkPosition> var34 = new ArrayList<ChunkPosition>();
		var34.addAll(destroyedBlockPositions);
		if (isFlaming) {
			for (int var33 = var34.size() - 1; var33 >= 0; var33--) {
				ChunkPosition var35 = var34.get(var33);
				int var37 = var35.x;
				int var36 = var35.y;
				int var16 = var35.z;
				int var38 = worldObj.getTypeId(var37, var36, var16);
				int var18 = worldObj.getTypeId(var37, var36 - 1, var16);
				int flam = Block.byId[var18].getFlammability(null, 0, 0, 0, 0, 0);
				// Block.IsFlammable is a protected method, and can only be accessed by mc mods.
				// if ((var38 == 0) && (Block.n[var18] != 0) && (this.ExplosionRNG.nextInt(3) == 0))
				if (var38 == 0 && flam != 0 && ExplosionRNG.nextInt(3) == 0)
					worldObj.setTypeId(var37, var36, var16, Block.FIRE.id);
			}
		}
	}

	@SuppressWarnings("null")
	public void doExplosionB() {
		if (stopExplosion) return;
		for (int var1 = 0; var1 <= 63; var1++) {
			dropList[var1] = null;
		}
		worldObj.makeSound(explosionX, explosionY, explosionZ, "nova", 4.0F,
				(1.0F + (worldObj.random.nextFloat() - worldObj.random.nextFloat()) * 0.2F) * 0.7F);
		worldObj.a("hugeexplosion", explosionX, explosionY, explosionZ, 0.0D, 0.0D, 0.0D);
		ArrayList<ChunkPosition> var25 = new ArrayList<ChunkPosition>();
		var25.addAll(destroyedBlockPositions);
		for (int var2 = var25.size() - 1; var2 >= 0; var2--) {
			ChunkPosition var3 = var25.get(var2);
			int var4 = var3.x;
			int var5 = var3.y;
			int var6 = var3.z;
			int var7 = worldObj.getTypeId(var4, var5, var6);
			for (int var8 = 0; var8 < 1; var8++) {
				double var9 = var4 + worldObj.random.nextFloat();
				double var11 = var5 + worldObj.random.nextFloat();
				double var13 = var6 + worldObj.random.nextFloat();
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
					worldObj.a("explode", (var9 + explosionX * 1.0D) / 2.0D, (var11 + explosionY * 1.0D) / 2.0D,
							(var13 + explosionZ * 1.0D) / 2.0D, var15, var17, var19);
				if (worldObj.random.nextInt(8) == 0) {
					worldObj.a("smoke", var9, var11, var13, var15, var17, var19);
				}
			}
			if (var7 > 0 && EEPatch.attemptBreak(player, var4, var5, var6)) {
				int var8 = worldObj.getData(var4, var5, var6);
				ArrayList<ItemStack> var27 = Block.byId[var7].getBlockDropped(worldObj, var4, var5, var6, var8, 0);
				for (Iterator<ItemStack> var10 = var27.iterator(); var10.hasNext();) {
					ItemStack var28 = var10.next();
					for (int var12 = 0; var12 < dropList.length; var12++) {
						if (dropList[var12] == null) {
							dropList[var12] = var28.cloneItemStack();
							var28 = null;
						} else if (dropList[var12].doMaterialsMatch(var28) && dropList[var12].count < dropList[var12].getMaxStackSize()) {
							while (dropList[var12].count < dropList[var12].getMaxStackSize() && var28 != null) {
								dropList[var12].count += 1;
								var28.count -= 1;
								if (var28.count == 0) var28 = null;
							}
						}
						if (var28 == null) {
							break;
						}
					}
				}
				Block.byId[var7].wasExploded(worldObj, var4, var5, var6);
				worldObj.setTypeId(var4, var5, var6, 0);
			}
		}

		if (dropList == null) return;

		if (exploder != null) {
			if (!EEBase.isEmpty(dropList)) {
				EntityLootBall var26 = new EntityLootBall(worldObj, EEBase.playerX(player), EEBase.playerY(player), EEBase.playerZ(player), dropList);
				if (var26 != null) worldObj.addEntity(var26);
			}
		} else if (!EEBase.isEmpty(dropList)) {
			EntityLootBall var26 = new EntityLootBall(worldObj, explosionX, explosionY, explosionZ, dropList);
			if (var26 != null) worldObj.addEntity(var26);
		}
	}
}