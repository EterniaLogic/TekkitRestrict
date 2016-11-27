package ee;

import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;
import net.minecraft.server.mod_EE;
import ee.core.GuiIds;
import ee.events.EEEnums.EETransmuteAction;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.other.EEPhilosopherStoneEvent;

public class ItemPhilosopherStone extends ItemEECharged {
	public ItemPhilosopherStone(int var1) {
		super(var1, 4);
	}

	public void doExtra(World var1, ItemStack var2, EntityHuman var3) {
		var3.openGui(mod_EE.getInstance(), GuiIds.PORT_CRAFTING, var1, (int) var3.locX, (int) var3.locY, (int) var3.locZ);
	}

	public void a(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
		if (cooldown(var1) > 0) {
			setCooldown(var1, cooldown(var1) - 1);
		}

		super.a(var1, var2, var3, var4, var5);
	}

	private void setCooldown(ItemStack var1, int var2) {
		setShort(var1, "cooldown", var2);
	}

	private int cooldown(ItemStack var1) {
		return getShort(var1, "cooldown");
	}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EEPhilosopherStoneEvent(var1, EEAction.RELEASE, var3, EETransmuteAction.ChangeMob))) return;
		var3.C_();
		var2.makeSound(var3, "transmute", 0.6F, 1.0F);
		var2.addEntity(new EntityPhilosopherEssence(var2, var3, chargeLevel(var1)));
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EEPhilosopherStoneEvent(var1, EEAction.ALTERNATE, var3, EETransmuteAction.PortableCrafting))) return;
		doExtra(var2, var1, var3);
	}

	public void doLeftClick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doTransmute(World world, int x, int y, int z, int var5, EntityHuman human) {
		int id = world.getTypeId(x, y, z);

		if (id != var5) {
			if (var5 == Block.DIRT.id && id != Block.DIRT.id && id != Block.GRASS.id
					|| var5 == Block.GRASS.id && id != Block.DIRT.id && id != Block.GRASS.id) {
				return;
			}

			if (var5 != Block.DIRT.id && var5 != Block.GRASS.id) {
				return;
			}
		}

		int data = world.getData(x, y, z);
		int idAbove = world.getTypeId(x, y + 1, z);
		int dataAbove = world.getData(x, y + 1, z);
		world.getMaterial(x, y, z);

		if (id != Block.DIRT.id && id != Block.GRASS.id) {
			if (id == Block.NETHERRACK.id) {
				if (attemptBreak(human, x, y, z)) {
					world.setTypeId(x, y, z, Block.COBBLESTONE.id);

					if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
				}
			} else if (id == Block.GLASS.id) {
				if (human.isSneaking()) {
					if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.SAND.id);
				}

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.COBBLESTONE.id) {
				if (world.worldProvider.d) {
					if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.NETHERRACK.id);
				} else if (human.isSneaking()) {
					if (idAbove == 0) {
						if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.GRASS.id);
					} else {
						if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.DIRT.id);
					}
				} else {
					if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.STONE.id);
				}

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.SAND.id) {
				if (human.isSneaking()) {
					if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.COBBLESTONE.id);
				} else if (idAbove == Block.DEAD_BUSH.id && dataAbove == 0) {
					if (attemptBreak(human, x, y + 1, z) && attemptBreak(human, x, y, z)) {
						world.setRawTypeIdAndData(x, y + 1, z, Block.LONG_GRASS.id, 1);
						world.setTypeId(x, y, z, Block.GRASS.id);
					}
				} else if (idAbove == 0) {
					if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.GRASS.id);
				} else {
					if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.DIRT.id);
				}

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.SANDSTONE.id) {
				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.GRAVEL.id);

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.DEAD_BUSH.id && data == 0) {
				if (attemptBreak(human, x, y - 1, z)) {
					world.setRawTypeIdAndData(x, y, z, Block.LONG_GRASS.id, 1);
					world.setTypeId(x, y - 1, z, Block.GRASS.id);

					if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
				}
			} else if (id == Block.LONG_GRASS.id && data == 1) {
				if (attemptBreak(human, x, y - 1, z) && attemptBreak(human, x, y, z)) {
					world.setRawTypeIdAndData(x, y, z, Block.DEAD_BUSH.id, 0);
					world.setTypeId(x, y - 1, z, Block.SAND.id);

					if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
				}
			} else if (id == Block.GRAVEL.id) {
				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.SANDSTONE.id);

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.STONE.id) {
				if (human.isSneaking()) {
					if (idAbove == 0) {
						if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.GRASS.id);
					} else {
						if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.DIRT.id);
					}
				} else {
					if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.COBBLESTONE.id);
				}

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.PUMPKIN.id) {
				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.MELON.id);

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.MELON.id) {
				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.PUMPKIN.id);

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.RED_ROSE.id) {
				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.YELLOW_FLOWER.id);

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.YELLOW_FLOWER.id) {
				if (attemptBreak(human, x, y, z)) world.setRawTypeIdAndData(x, y, z, 139, 0);

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == 139 && data == 0) {
				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.RED_ROSE.id);

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.RED_MUSHROOM.id) {
				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.BROWN_MUSHROOM.id);

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == Block.BROWN_MUSHROOM.id) {
				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.RED_MUSHROOM.id);

				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			} else if (id == 142){
				if (human.isSneaking()) {
					if (attemptBreak(human, x, y, z)){
						if (data > 0){
							//world.setData(x, y, z, data-1);
							EEPBase.setDataAndUpdate(human, x, y, z, 142, data-1);
						} else if (data == 0){
							//world.setData(x, y, z, 4);
							EEPBase.setDataAndUpdate(human, x, y, z, 142, 4);
						}
					}
				} else {
					if (attemptBreak(human, x, y, z)){
						//world.setData(x, y, z, (data+1)%5);
						EEPBase.setDataAndUpdate(human, x, y, z, 142, (data+1)%5);
					}
					//if (data < 4){
					//	if (attemptBreak(entityPlayer, x, y, z)) world.setData(x, y, z, data+1);
					//} else if (data == 4) {
					//	if (attemptBreak(entityPlayer, x, y, z)) world.setData(x, y, z, 0);
					//}
				}
				
				if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			}
		} else {
			if (human.isSneaking()) {
				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.COBBLESTONE.id);
			} else {
				if (idAbove == Block.LONG_GRASS.id && dataAbove == 1 && attemptBreak(human, x, y + 1, z)) {
					world.setRawTypeIdAndData(x, y + 1, z, Block.DEAD_BUSH.id, 0);
				}

				if (attemptBreak(human, x, y, z)) world.setTypeId(x, y, z, Block.SAND.id);
			}

			if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
		}
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int face) {
		if (EEProxy.isClient(world)) return false;
		
		if (cooldown(item) > 0) return false;

		setCooldown(item, 10);
		
		if (EEEventManager.callEvent(new EEPhilosopherStoneEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EETransmuteAction.Transmute))) return false;
		
		human.C_();
		world.makeSound(human, "transmute", 0.6F, 1.0F);

		int id = world.getTypeId(x, y, z);
		int data = world.getData(x, y, z);
		
		if (id == Block.SNOW.id && face == 1) {
			y--;
		}

		if (id != Block.LOG.id && id != Block.LEAVES.id && id != Block.SAPLING.id && id != 143 && id != 141 && id != 243 // && id != 142
				&& id != 242 && id != 241 && !(id == 139 && data == 1)) {
			int charge = chargeLevel(item);
			int mode = getMode(item);
			if (mode == 0) {
				for (int var10 = -charge * (face == 4 ? 0 : face == 5 ? 2 : 1); var10 <= charge * (face == 5 ? 0 : face == 4 ? 2 : 1); var10++) {
					for (int var11 = -charge * (face == 0 ? 0 : face == 1 ? 2 : 1); var11 <= charge * (face == 1 ? 0 : face == 0 ? 2 : 1); var11++) {
						for (int var12 = -charge * (face == 2 ? 0 : face == 3 ? 2 : 1); var12 <= charge * (face == 3 ? 0 : face == 2 ? 2 : 1); var12++) {
							int nx = x + var10;
							int ny = y + var11;
							int nz = z + var12;
							doTransmute(world, nx, ny, nz, id, human);
						}
					}
				}
			} else if (mode == 1) {
				for (int var10 = -1 * (face == 4 ? 0 : face == 5 ? charge * charge : 1); var10 <= 1 * (face == 5 ? 0 : face == 4 ? charge * charge : 1); var10++) {
					for (int var11 = -1 * (face == 0 ? 0 : face == 1 ? charge * charge : 1); var11 <= 1 * (face == 1 ? 0 : face == 0 ? charge * charge : 1); var11++) {
						for (int var12 = -1 * (face == 2 ? 0 : face == 3 ? charge * charge : 1); var12 <= 1 * (face == 3 ? 0 : face == 2 ? charge * charge : 1); var12++) {
							int nx = x + var10;
							int ny = y + var11;
							int nz = z + var12;
							doTransmute(world, nx, ny, nz, id, human);
						}
					}
				}
			} else if (mode == 2) {
				for (int var10 = -1 * (face != 4 && face != 5 ? charge : 0); var10 <= 1 * (face != 4 && face != 5 ? charge : 0); var10++) {
					for (int var11 = -1 * (face != 0 && face != 1 ? charge : 0); var11 <= 1 * (face != 0 && face != 1 ? charge : 0); var11++) {
						for (int var12 = -1 * (face != 2 && face != 3 ? charge : 0); var12 <= 1 * (face != 2 && face != 3 ? charge : 0); var12++) {
							int nx = x + var10;
							int ny = y + var11;
							int nz = z + var12;
							doTransmute(world, nx, ny, nz, id, human);
						}
					}
				}
			}
		} else {
			int charge = chargeLevel(item);
			int mode = getMode(item);
			if (mode == 0) {
				for (int var10 = -charge * (face == 4 ? 0 : face == 5 ? 2 : 1); var10 <= charge * (face == 5 ? 0 : face == 4 ? 2 : 1); var10++) {
					for (int var11 = -charge * (face == 0 ? 0 : face == 1 ? 2 : 1); var11 <= charge * (face == 1 ? 0 : face == 0 ? 2 : 1); var11++) {
						for (int var12 = -charge * (face == 2 ? 0 : face == 3 ? 2 : 1); var12 <= charge * (face == 3 ? 0 : face == 2 ? 2 : 1); var12++) {
							int nx = x + var10;
							int ny = y + var11;
							int nz = z + var12;
							doTreeTransmute(item, human, world, nx, ny, nz);
						}
					}
				}
			} else if (mode == 1) {
				for (int var10 = -1 * (face == 4 ? 0 : face == 5 ? charge * charge : 1); var10 <= 1 * (face == 5 ? 0 : face == 4 ? charge * charge : 1); var10++) {
					for (int var11 = -1 * (face == 0 ? 0 : face == 1 ? charge * charge : 1); var11 <= 1 * (face == 1 ? 0 : face == 0 ? charge * charge : 1); var11++) {
						for (int var12 = -1 * (face == 2 ? 0 : face == 3 ? charge * charge : 1); var12 <= 1 * (face == 3 ? 0 : face == 2 ? charge * charge : 1); var12++) {
							int nx = x + var10;
							int ny = y + var11;
							int nz = z + var12;
							doTreeTransmute(item, human, world, nx, ny, nz);
						}
					}
				}
			} else if (mode == 2) {
				for (int var10 = -1 * (face != 4 && face != 5 ? charge : 0); var10 <= 1 * (face != 4 && face != 5 ? charge : 0); var10++) {
					for (int var11 = -1 * (face != 0 && face != 1 ? charge : 0); var11 <= 1 * (face != 0 && face != 1 ? charge : 0); var11++) {
						for (int var12 = -1 * (face != 2 && face != 3 ? charge : 0); var12 <= 1 * (face != 2 && face != 3 ? charge : 0); var12++) {
							int nx = x + var10;
							int ny = y + var11;
							int nz = z + var12;
							doTreeTransmute(item, human, world, nx, ny, nz);
						}
					}
				}
			}
			// doTreeTransmute(item, entityPlayer, world, x, y, z);
		}

		return false;
	}

	private void doTreeTransmute(ItemStack item, EntityHuman human, World world, int x, int y, int z) {
		int setId = 0;
		int setData = 0;
		int id = world.getTypeId(x, y, z);
		int data = world.getData(x, y, z) & 0x3;//=data%4

		if (id == Block.LOG.id || id == Block.LEAVES.id || id == Block.SAPLING.id) {
			if (data == 0) {
				setData = 1;
				setId = id;
			} else if (data == 1) {
				setData = 2;
				setId = id;
			} else if (data == 2) {
				setData = 3;
				setId = id;
			} else if (data == 3) {
				if (id == Block.LOG.id) {
					setId = 143;
					setData = 0;
				} else if (id == Block.LEAVES.id) {
					setId = 141;
					setData = 0;
				} else if (id == Block.SAPLING.id) {
					setId = 139;
					setData = 1;
				}
			}
		} else if (id == 143) {
			setId = 243;
			setData = 0;
		} else if (id == 141) {
			setId = 242;
			setData = 0;
		} else if (id == 139 && data == 1) {
			setId = 241;
			setData = 0;
		} else if (id == 243) {
			setId = Block.LOG.id;
			setData = 0;
		} else if (id == 242) {
			setId = Block.LEAVES.id;
			setData = 0;
		} else if (id == 241) {
			setId = Block.SAPLING.id;
			setData = 0;
		}
		/*
		else if (blockId == 142) {// marble
			toSetBlock = 142;
			if (blockData == 0) toSetData = 1;
			else if (blockData == 1) toSetData = 2;
			else if (blockData == 2) toSetData = 3;
			else if (blockData == 3) toSetData = 4;
			else if (blockData == 4) toSetData = 0;
		}*/

		int nId = world.getTypeId(x, y, z);
		int nData = world.getData(x, y, z) & 0x3;
		
		
		if (nData == data && (nId == Block.LOG.id || nId == Block.LEAVES.id || nId == Block.SAPLING.id || nId == 141 || nId == 143 //|| nId == 142
				|| nId == 241 || nId == 242 || nId == 243 || (nId == 139 && nData == 1)) && attemptBreak(human, x, y, z)) {
			world.setTypeIdAndData(x, y, z, setId, setData);
			// doTreeSearch(world, newX, newY, newZ, var7, toSetData, newId, entityPlayer);
		}
	}

	@SuppressWarnings("unused")
	private void doTreeSearch(World world, int x, int y, int z, int var5, int var6, int newBlock, EntityHuman player) {
		for (int var7 = -1; var7 <= 1; var7++) {
			for (int var8 = -1; var8 <= 1; var8++) {
				for (int var9 = -1; var9 <= 1; var9++) {
					if (var7 == 0 && var8 == 0 && var9 == 0) {
						int nx = x + var7;
						int ny = y + var8;
						int nz = z + var9;
						int nId = world.getTypeId(nx, ny, nz);
						int nData = world.getData(nx, ny, nz) & 0x3;
						if ((nData == var5 && nId == Block.LOG.id || nId == Block.LEAVES.id || nId == Block.SAPLING.id || nId == 141
								|| nId == 143 || nId == 241 || nId == 242 || nId == 243 || (nId == 139 && nData == 1))
								&& attemptBreak(player, nx, ny, nz)) {
							world.setTypeIdAndData(nx, ny, nz, newBlock, var6);
							doTreeSearch(world, nx, ny, nz, var5, var6, newBlock, player);
						}
					}
				}
			}
		}
	}
	

	public ItemStack a(ItemStack var1, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return var1;
		
		if (cooldown(var1) > 0) return var1;
		
		setCooldown(var1, 10);
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
		MovingObjectPosition var24 = world.rayTrace(var13, var23, true);

		if (var24 == null) return var1;
		
		if (var24.type == EnumMovingObjectType.TILE) {
			int x = var24.b;
			int y = var24.c;
			int z = var24.d;
			Material mat = world.getMaterial(x, y, z);

			if (mat != Material.LAVA && mat != Material.WATER) {
				return var1;
			}
			
			if (EEEventManager.callEvent(new EEPhilosopherStoneEvent(var1, EEAction.RIGHTCLICK, human, x, y, z, EETransmuteAction.Transmute))) return var1;

			for (int cx = -1 * chargeLevel(var1); cx <= chargeLevel(var1); cx++) {
				for (int cy = -1 * chargeLevel(var1); cy <= chargeLevel(var1); cy++) {
					for (int cz = -1 * chargeLevel(var1); cz <= chargeLevel(var1); cz++) {
						int nx = x + cx;
						int ny = y + cy;
						int nz = z + cz;
						Material var35 = world.getMaterial(nx, ny, nz);

						if (var35 == mat) {
							int var36 = world.getData(nx, ny, nz);
							if (var35 == Material.WATER) {
								if (world.getTypeId(nx, ny + 1, nz) == 0 && attemptBreak(human, nx, ny, nz)) {
									world.setTypeId(nx, ny, nz, Block.ICE.id);

									if (world.random.nextInt(8) == 0) {
										world.a("largesmoke", nx, ny + 1, nz, 0.0D, 0.0D, 0.0D);
									}
								}
							} else if (var35 == Material.LAVA && var36 == 0 && world.getTypeId(nx, ny + 1, nz) == 0
									&& attemptBreak(human, nx, ny, nz)) {
								world.setTypeId(nx, ny, nz, Block.OBSIDIAN.id);

								if (world.random.nextInt(8) == 0) {
									world.a("largesmoke", nx, ny + 1, nz, 0.0D, 0.0D, 0.0D);
								}
							}
						}
					}
				}
			}
		}

		return var1;
	}

	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {
		changeMode(var1, var3);
	}
	

	public int getMode(ItemStack var1) {
		return getInteger(var1, "transmode");
	}
	public void setMode(ItemStack var1, int var2) {
		setInteger(var1, "transmode", var2);
	}
	
	public void changeMode(ItemStack var1, EntityHuman var2) {
		int mode = getMode(var1);
		if (mode == 2) {
			mode = 0;
			setMode(var1, 0);
		} else {
			mode += 1;
			setMode(var1, mode);
		}

		var2.a("Philosopher Stone transmuting " + (mode == 0 ? "in a cube" : mode == 1 ? "in a line" : "in a panel") + ".");
	}

	public boolean e(ItemStack var1) {
		return false;
	}
}