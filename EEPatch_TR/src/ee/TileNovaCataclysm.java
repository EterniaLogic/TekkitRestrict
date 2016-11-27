// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst space 
// Source File Name:   TileNovaCataclysm.java

package ee;

import java.util.Random;

import net.minecraft.server.*;

// Referenced classes of package ee:
//			TileEE, TileAlchChest, EEBase, EntityCataclysmPrimed, 
//			EEBlock, BlockEEStone

public class TileNovaCataclysm extends TileEE {

	public boolean fuseLit;

	public TileNovaCataclysm() {}

	public void a(NBTTagCompound var1) {
		super.a(var1);
		fuseLit = var1.getBoolean("fuseLit");
	}

	public void b(NBTTagCompound var1) {
		super.b(var1);
		var1.setBoolean("fuseLit", fuseLit);
	}

	/*
	public static boolean putInChest(TileEntity var0, ItemStack var1) {
		if (var1 != null && var1.id != 0) {
			if (var0 == null) return false;
			if (var0 instanceof TileEntityChest) {
				for (int var2 = 0; var2 < ((TileEntityChest) var0).getSize(); var2++) {
					ItemStack var3 = ((TileEntityChest) var0).getItem(var2);
					if (var3 != null && var3.doMaterialsMatch(var1) && var3.count + var1.count <= var3.getMaxStackSize()) {
						var3.count += var1.count;
						return true;
					}
				}

				for (int var2 = 0; var2 < ((TileEntityChest) var0).getSize(); var2++)
					if (((TileEntityChest) var0).getItem(var2) == null) {
						((TileEntityChest) var0).setItem(var2, var1);
						return true;
					}

			} else if (var0 instanceof TileAlchChest) {
				for (int var2 = 0; var2 < ((TileAlchChest) var0).getSize(); var2++) {
					ItemStack var3 = ((TileAlchChest) var0).getItem(var2);
					if (var3 != null && var3.doMaterialsMatch(var1) && var3.count + var1.count <= var3.getMaxStackSize() && var3.getData() == var1.getData()) {
						var3.count += var1.count;
						return true;
					}
				}

				for (int var2 = 0; var2 < ((TileAlchChest) var0).getSize(); var2++)
					if (((TileAlchChest) var0).getItem(var2) == null) {
						((TileAlchChest) var0).setItem(var2, var1);
						return true;
					}

			}
			return false;
		} else {
			return true;
		}
	}*/

	public void q_() {
		for (int var1 = -1; var1 <= 1; var1 += 2) {
			if (world.getTypeId(x + var1, y, z) == Block.FIRE.id) lightFuse();
			if (world.getTypeId(x, y + var1, z) == Block.FIRE.id) lightFuse();
			if (world.getTypeId(x, y, z + var1) == Block.FIRE.id) lightFuse();
		}

	}

	public boolean tryDropInChest(ItemStack var1) {
		TileEntity var2;
			 if (isChest(var2 = world.getTileEntity(x, y + 1, z))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x, y - 1, z))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x + 1, y, z))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x - 1, y, z))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x, y, z + 1))) return putInChest(var2, var1);
		else if (isChest(var2 = world.getTileEntity(x, y, z - 1))) return putInChest(var2, var1);
		else return false;
	}

	private boolean isChest(TileEntity var1) {
		return (var1 instanceof TileEntityChest) || (var1 instanceof TileAlchChest);
	}

	public void setDefaultDirection(int var1, int var2, int var3) {
		if (!world.isStatic) {
			int var4 = world.getTypeId(var1, var2, var3 - 1);
			int var5 = world.getTypeId(var1, var2, var3 + 1);
			int var6 = world.getTypeId(var1 - 1, var2, var3);
			int var7 = world.getTypeId(var1 + 1, var2, var3);
			byte var8 = 2;
			if (Block.n[var4] && !Block.n[var5]) var8 = 3;
			if (Block.n[var5] && !Block.n[var4]) var8 = 2;
			if (Block.n[var6] && !Block.n[var7]) var8 = 5;
			if (Block.n[var7] && !Block.n[var6]) var8 = 4;
			direction = var8;
		}
	}

	public void lightFuse() {
		if (!fuseLit) {
			fuseLit = true;
			world.setTypeId(x, y, z, 0);
			onBlockDestroyedByPlayer();
		}
	}

	public void onBlockPlacedBy(EntityLiving var1) {
		if (var1 instanceof EntityHuman) player = ((EntityHuman) var1).name;
		int var2 = MathHelper.floor((var1.yaw * 4F) / 360F + 0.5D) & 3;
		if (var2 == 0) direction = 2;
		if (var2 == 1) direction = 5;
		if (var2 == 2) direction = 3;
		if (var2 == 3) direction = 4;
	}

	public int getTextureForSide(int var1) {
		return var1 != 0 ? var1 != 1 ? EEBase.novaCataclysmSide : EEBase.novaCatalystTop : EEBase.novaCatalystBottom;
	}

	public int getInventoryTexture(int var1) {
		return getTextureForSide(var1);
	}

	public int getLightValue() {
		return 4;
	}

	public void onBlockAdded() {
		super.onBlockAdded();
		if (world.isBlockIndirectlyPowered(x, y, z))
			lightFuse();
	}

	public void onNeighborBlockChange(int var1) {
		if (var1 > 0 && Block.byId[var1].isPowerSource() && world.isBlockIndirectlyPowered(x, y, z))
			lightFuse();
	}

	public void onBlockClicked(EntityHuman var1) {
		if (var1.U() != null && var1.U().id == Item.FLINT_AND_STEEL.id)
			lightFuse();
		super.onBlockClicked(var1);
	}

	public boolean onBlockActivated(EntityHuman var1) {
		return false;
	}

	public void onBlockRemoval() {}

	public void randomDisplayTick(Random random) {}

	public void onBlockDestroyedByExplosion() {
		EntityCataclysmPrimed var1 = null;
		EntityHuman human;
		if (player != null && player != "" && (human = world.a(player)) != null)
			var1 = new EntityCataclysmPrimed(world, human, x + 0.5F, y + 0.5F, z + 0.5F);
		else
			var1 = new EntityCataclysmPrimed(world, x + 0.5F, y + 0.5F, z + 0.5F);
		var1.fuse = world.random.nextInt(var1.fuse / 2) + var1.fuse / 4;
		var1.fuse = 0;
		world.addEntity(var1);
	}

	public void onBlockDestroyedByPlayer() {
		if (!world.isStatic)
			if (!fuseLit) {
				dropBlockAsItem_do(new ItemStack(EEBlock.eeStone.id, 1, 11));
			} else {
				EntityCataclysmPrimed var1 = null;
				EntityHuman human;
				if (player != null && player != "" && (human = world.a(player)) != null)
					var1 = new EntityCataclysmPrimed(world, human, x + 0.5F, y + 0.5F, z + 0.5F);
				else
					var1 = new EntityCataclysmPrimed(world, x + 0.5F, y + 0.5F, z + 0.5F);
				var1.fuse = world.random.nextInt(var1.fuse / 2) + var1.fuse / 4;
				world.addEntity(var1);
			}
	}

	protected void dropBlockAsItem_do(ItemStack var1) {
		if (!world.isStatic) {
			float var2 = 0.7F;
			double var3 = world.random.nextFloat() * var2 + (1.0F - var2) * 0.5D;
			double var5 = world.random.nextFloat() * var2 + (1.0F - var2) * 0.5D;
			double var7 = world.random.nextFloat() * var2 + (1.0F - var2) * 0.5D;
			EntityItem var9 = new EntityItem(world, x + var3, y + var5, z + var7, var1);
			var9.pickupDelay = 10;
			world.addEntity(var9);
		}
	}
}
