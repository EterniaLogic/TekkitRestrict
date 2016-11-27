package ee;

import ee.network.PacketHandler;
import ee.network.PacketTypeHandler;
import ee.network.TileEntityPacket;

import java.util.Random;

import org.bukkit.entity.HumanEntity;

import net.minecraft.server.*;

public class TileEE extends TileEntity {

	public byte direction;
	public String player;

	public TileEE() {}

	public void a(NBTTagCompound var1) {
		super.a(var1);
		direction = var1.getByte("direction");
		player = var1.getString("player");
	}

	public void b(NBTTagCompound var1) {
		super.b(var1);
		var1.setByte("direction", direction);
		if (player != null && player != "") var1.setString("player", player);
	}

	public int getKleinLevel(int var1) {
		if (var1 != EEItem.kleinStar1.id){
			if (var1 != EEItem.kleinStar2.id){
				if (var1 != EEItem.kleinStar3.id){
					if (var1 != EEItem.kleinStar4.id) {
						if (var1 != EEItem.kleinStar5.id){
							return ((byte) (var1 != EEItem.kleinStar6.id ? 0: 6));
						}
						else return 5;
					}
					else return 4;
				}
				else return 3;
			}
			else return 2;
		}
		else return 1;
	}

	public float getWOFTReciprocal(float var1) {
		float var2 = 1.0F / var1;
		return var2 * (EEBase.getMachineFactor() / 16F);
	}

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
	}

	/**
	 * Notice: gets Tile Entity in quick succession!
	 */
	public boolean tryDropInChest(ItemStack var1) {
		if (world == null || EEProxy.isClient(world)) return false;
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

	public void setDefaultDirection() {
		if (!world.isStatic) {
			int var1 = world.getTypeId(x, y, z - 1);
			int var2 = world.getTypeId(x, y, z + 1);
			int var3 = world.getTypeId(x - 1, y, z);
			int var4 = world.getTypeId(x + 1, y, z);
			byte var5 = 2;
			if (Block.n[var1] && !Block.n[var2]) var5 = 3;
			if (Block.n[var2] && !Block.n[var1]) var5 = 2;
			if (Block.n[var3] && !Block.n[var4]) var5 = 5;
			if (Block.n[var4] && !Block.n[var3]) var5 = 4;
			direction = var5;
		}
	}

	public void onBlockPlacedBy(EntityLiving var1) {
		if (var1 instanceof EntityHuman) player = ((EntityHuman) var1).name;
		int var2 = MathHelper.floor((var1.yaw * 4F) / 360F + 0.5D) & 3;
		if (var2 == 0) direction = 2;
		else if (var2 == 1) direction = 5;
		else if (var2 == 2) direction = 3;
		else if (var2 == 3) direction = 4;
	}

	public int getTextureForSide(int var1) {
		return 0;
	}

	public int getInventoryTexture(int var1) {
		return 0;
	}

	public int getLightValue() {
		return 0;
	}

	public boolean onBlockActivated(EntityHuman var1) {
		return false;
	}

	public void onNeighborBlockChange(int i) {}

	public void onBlockClicked(EntityHuman entityhuman) {}

	public boolean clientFail() {
		if (world == null || EEProxy.isClient(world)) return true;
		
		return world.getTileEntity(x, y, z) != this;
	}

	public void onBlockAdded() {}

	@SuppressWarnings("null")
	public void onBlockRemoval() {
		for (HumanEntity h : getViewers()) h.closeInventory();
		for (int var1 = 0; var1 < getSizeInventory(); var1++) {
			ItemStack var2 = getStackInSlot(var1);
			if (var2 != null) {
				float var3 = world.random.nextFloat() * 0.8F + 0.1F;
				float var4 = world.random.nextFloat() * 0.8F + 0.1F;
				float var5 = world.random.nextFloat() * 0.8F + 0.1F;
				while (var2.count > 0) {
					int var6 = world.random.nextInt(21) + 10;
					if (var6 > var2.count) var6 = var2.count;
					var2.count -= var6;
					EntityItem var7 = new EntityItem(world, x + var3, y + var4, z + var5, new ItemStack(var2.id, var6, var2.getData()));
					if (var7 != null) {
						float var8 = 0.05F;
						var7.motX = (float) world.random.nextGaussian() * var8;
						var7.motY = (float) world.random.nextGaussian() * var8 + 0.2F;
						var7.motZ = (float) world.random.nextGaussian() * var8;
						if (var7.itemStack.getItem() instanceof ItemKleinStar)
							((ItemKleinStar) var7.itemStack.getItem()).setKleinPoints(var7.itemStack, ((ItemKleinStar) var2.getItem()).getKleinPoints(var2));
						world.addEntity(var7);
					}
				}
			}
		}

	}

	private ItemStack getStackInSlot(int var1) {
		return null;
	}

	private int getSizeInventory() {
		return 0;
	}

	public void randomDisplayTick(Random random) {}

	public void onBlockDestroyedByExplosion() {}

	public void onBlockDestroyedByPlayer() {}

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

	public void setDirection(byte var1) {
		direction = var1;
	}

	public void setPlayerName(String var1) {
		player = var1;
	}

	public Packet d() {
		TileEntity t = this.world.getTileEntity(x, y, z);
		if (!(t instanceof TileEE)) return null;
		TileEntityPacket var1 = (TileEntityPacket) PacketHandler.getPacket(PacketTypeHandler.TILE);
		var1.setCoords(x, y, z);
		var1.setOrientation(direction);
		var1.setPlayerName(player);
		return PacketHandler.getPacketForSending(var1);
	}
}
