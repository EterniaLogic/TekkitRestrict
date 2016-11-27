package ee;

import java.util.*;

import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.rm.EERedKatarEvent;
import net.minecraft.server.*;

public class ItemRedKatar extends ItemRedTool {

	protected ItemRedKatar(int var1) {
		super(var1, 4, 18, blocksEffectiveAgainst);
	}

	public boolean a(ItemStack item, int id, int x, int y, int z, EntityLiving var6) {
		boolean var7 = false;
		if (!EEMaps.isLeaf(id) && id != Block.WEB.id && id != Block.VINE.id && id != BlockFlower.LONG_GRASS.id && id != BlockFlower.DEAD_BUSH.id)
			var7 = true;
		if (!var7)
			EEProxy.dropBlockAsItemStack(Block.byId[id], var6, x, y, z,
					new ItemStack(id, 1, id != Block.LEAVES.id ? ((int) (((ItemEECharged) item.getItem()).getShort(item, "lastMeta")))
							: ((ItemEECharged) item.getItem()).getShort(item, "lastMeta") & 3));
		return super.a(item, id, x, y, z, var6);
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return var1.id == Block.WEB.id;
	}

	public boolean ConsumeReagent(int var1, ItemStack item, EntityHuman entityPlayer, boolean var4) {
		if (getFuelRemaining(item) >= 16) {
			setFuelRemaining(item, getFuelRemaining(item) - 16);
			return true;
		}
		int var5 = getFuelRemaining(item);
		while (getFuelRemaining(item) < 16) {
			ConsumeReagent(item, entityPlayer, var4);
			if (var5 == getFuelRemaining(item)) break;
			var5 = getFuelRemaining(item);
			if (getFuelRemaining(item) >= 16) {
				setFuelRemaining(item, getFuelRemaining(item) - 16);
				return true;
			}
		}
		return false;
	}

	public float getStrVsBlock(ItemStack item, Block var2, int var3) {
		if (getShort(item, "lastMeta") != var3)
			setShort(item, "lastMeta", var3);
		if (var2.id != Block.VINE.id && var2.id != Block.LEAVES.id && var2.id != Block.WEB.id) {
			if (var2.id == Block.WOOL.id) return 5F;
			if (var2.material != Material.EARTH && var2.material != Material.GRASS) {
				return var2.material != Material.WOOD ? super.getDestroySpeed(item, var2) : 18F + chargeLevel(item) * 2;
			} else {
				float var4 = 18F + chargeLevel(item) * 4;
				return var4;
			}
		} else {
			return 15F;
		}
	}

	/** Attacks mobs in a range */
	public void doSwordBreak(ItemStack item, World var2, EntityHuman var3) {
		if (chargeLevel(item) > 0) {
			boolean var4 = false;
			int var5;
			for (var5 = 1; var5 <= chargeLevel(item); var5++) {
				if (var5 == chargeLevel(item)) var4 = true;
				if (ConsumeReagent(1, item, var3, var4)) continue;
				var5--;
				break;
			}

			if (var5 < 1) return;
			var3.C_();
			var2.makeSound(var3, "flash", 0.8F, 1.5F);
			List<Entity> var6 = var2.getEntities(var3, AxisAlignedBB.b((float) var3.locX - (var5 / 1.5D + 2D), var3.locY
					- (var5 / 1.5D + 2D), (float) var3.locZ - (var5 / 1.5D + 2D), (float) var3.locX + var5 / 1.5D
					+ 2D, var3.locY + var5 / 1.5D + 2D, (float) var3.locZ + var5 / 1.5D + 2D));
			for (int var7 = 0; var7 < var6.size(); var7++) {
				Entity var8 = var6.get(var7);
				if (((var8 instanceof EntityLiving) && (EEBase.getSwordMode(var3)) || (var8 instanceof EntityMonster))) {
					var8.damageEntity(DamageSource.playerAttack(var3), weaponDamage + chargeLevel(item) * 2);
				}
			}
		}
	}

	
	//private long delay = 0;
	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int var7) {
		if (EEProxy.isClient(world)) return false;
		//if (human.getBukkitEntity().hasPermission("eepatch.delay") && delay > System.currentTimeMillis()){
		//	return false;
		//}
		//delay = System.currentTimeMillis()+1000*5;
		
		int id = world.getTypeId(x, y, z);
		chargeLevel(item);
		if (EEMaps.isLeaf(id) || id == Block.WEB.id || id == Block.VINE.id || id == longGrass || id == bush){
			if (EEEventManager.callEvent(new EERedKatarEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;
			onItemUseShears(item, human, world, x, y, z, var7);
		} else if ((id == Block.DIRT.id || id == Block.GRASS.id) && world.getTypeId(x, y + 1, z) == 0){
			//Event in method.
			onItemUseHoe(item, human, world, x, y, z, var7);
		} else if (EEMaps.isWood(id)){
			if (EEEventManager.callEvent(new EERedKatarEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;
			onItemUseAxe(item, human, world, x, y, z, var7);
		}
		return false;
	}

	public boolean onItemUseShears(ItemStack item, EntityHuman var2, World var3, int x, int y, int z, int var7) {
		int charge = chargeLevel(item);
		if (charge < 1) return false;
		
		//boolean var8 = false;
		cleanDroplist(item);
		var2.C_();
		var3.makeSound(var2, "flash", 0.8F, 1.5F);
		for (int var9 = -(charge + 2); var9 <= charge + 2; var9++) {
			for (int var10 = -(charge + 2); var10 <= charge + 2; var10++) {
				for (int var11 = -(charge + 2); var11 <= charge + 2; var11++) {
					int id = var3.getTypeId(x + var9, y + var10, z + var11);
					int nx = x + var9;
					int ny = y + var10;
					int nz = z + var11;
					if ((EEMaps.isLeaf(id) || id == Block.VINE.id || id == Block.WEB.id || id == longGrass || id == bush)
							&& attemptBreak(var2, nx, ny, nz)) {
						if (getFuelRemaining(item) < 1) ConsumeReagent(item, var2, false);
						if (getFuelRemaining(item) > 0) {
							int data = var3.getData(nx, ny, nz);
							if (!EEMaps.isLeaf(id) && id != Block.VINE.id && id != Block.WEB.id && id != longGrass && id != bush) {
								ArrayList<ItemStack> var14 = Block.byId[id].getBlockDropped(var3, nx, ny, nz, data, 0);
								ItemStack var16;
								for (Iterator<ItemStack> var15 = var14.iterator(); var15.hasNext(); addToDroplist(item, var16))
									var16 = var15.next();

							}
							else if (id == Block.LEAVES.id)
								addToDroplist(item, new ItemStack(Block.LEAVES.id, 1, data & 3));
							else
								addToDroplist(item, new ItemStack(Block.byId[id], 1, data));
							setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);
							var3.setTypeId(nx, ny, nz, 0);
							if (var3.random.nextInt(8) == 0) var3.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if (var3.random.nextInt(8) == 0) var3.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
						}
					}
				}

			}

		}

		ejectDropList(var3, item, x, y, z);
		return false;
	}
	public boolean onItemUseHoe(ItemStack item, EntityHuman human, World world, int x, int y, int z, int var7) {
		int id = world.getTypeId(x, y, z);
		int charge = chargeLevel(item);
		
		if (charge > 0) {
			if (EEEventManager.callEvent(new EERedKatarEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.TillRadius))) return false;
			human.C_();
			world.makeSound(human, "flash", 0.8F, 1.5F);
			
			if (id == yFlower || id == rFlower || id == bMush || id == rMush || id == longGrass || id == bush) y--;
			
			for (int var8 = -(charge * charge) - 1; var8 <= charge * charge + 1; var8++) {
				for (int var9 = -(charge * charge) - 1; var9 <= charge * charge + 1; var9++) {
					int nx = x + var8;
					int nz = z + var9;
					int id2 = world.getTypeId(nx, y, nz);
					int id3 = world.getTypeId(nx, y + 1, nz);
					if ((id3 == yFlower || id3 == rFlower || id3 == bMush
							|| id3 == rMush || id3 == longGrass || id3 == bush)
							&& attemptBreak(human, nx, y + 1, nz)) {
						Block.byId[id3].dropNaturally(world, nx, y + 1, nz, world.getData(nx, y + 1, nz), 1.0F, 1);
						world.setTypeId(nx, y + 1, nz, 0);
						id3 = 0;
					}
					if (id3 == 0 && (id2 == Block.DIRT.id || id2 == Block.GRASS.id) && attemptBreak(human, nx, y, nz)) {
						if (getFuelRemaining(item) < 1) ConsumeReagent(item, human, false);
						
						if (getFuelRemaining(item) > 0) {
							world.setTypeId(nx, y, nz, 60);
							setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);
							if (world.random.nextInt(8) == 0) world.a("largesmoke", nx, y, nz, 0.0D, 0.0D, 0.0D);
							if (world.random.nextInt(8) == 0) world.a("explode", nx, y, nz, 0.0D, 0.0D, 0.0D);
						}
					}
				}
			}

			return false;
		}
		
		if (human != null && !human.d(x, y, z)) return false;
		int var9 = world.getTypeId(x, y + 1, z);
		if ((var7 == 0 || var9 != 0 || id != Block.GRASS.id) && id != Block.DIRT.id) return false;
		Block var10 = Block.SOIL;
		world.makeSound(x + 0.5F, y + 0.5F, z + 0.5F, var10.stepSound.getName(),
				(var10.stepSound.getVolume1() + 1.0F) / 2.0F, var10.stepSound.getVolume2() * 0.8F);
		
		if (world.isStatic) return true;

		if (!attemptBreak(human, x, y, z)) return false;
		
		world.setTypeId(x, y, z, var10.id);
		return true;
	}
	public boolean onItemUseAxe(ItemStack item, EntityHuman human, World world, int x, int y, int z, int var7) {
		int charge = chargeLevel(item);
		if (charge < 1) return false;

		boolean var14 = false;
		cleanDroplist(item);
		if (chargeLevel(item) < 1) return false;
		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);
		for (int var15 = -(charge * 2) + 1; var15 <= charge * 2 - 1; var15++) {
			for (int var16 = charge * 2 + 1; var16 >= -2; var16--) {
				for (int var17 = -(charge * 2) + 1; var17 <= charge * 2 - 1; var17++) {
					int nx = x + var15;
					int ny = y + var16;
					int nz = z + var17;
					int id = world.getTypeId(nx, ny, nz);
					if ((EEMaps.isWood(id) || EEMaps.isLeaf(id)) && attemptBreak(human, nx, ny, nz)) {
						if (getFuelRemaining(item) < 1) if (var15 == chargeLevel(item) && var17 == chargeLevel(item)) {
							ConsumeReagent(item, human, var14);
							var14 = false;
						} else {
							ConsumeReagent(item, human, false);
						}
						if (getFuelRemaining(item) > 0) {
							int data = world.getData(nx, ny, nz);
							ArrayList<ItemStack> var23 = Block.byId[id].getBlockDropped(world, nx, ny, nz, data, 0);
							ItemStack var25;
							for (Iterator<ItemStack> var24 = var23.iterator(); var24.hasNext(); addToDroplist(item, var25))
								var25 = var24.next();

							world.setTypeId(nx, ny, nz, 0);
							if (!EEMaps.isLeaf(id)) setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);
							if (world.random.nextInt(8) == 0) world.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if (world.random.nextInt(8) == 0) world.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
						}
					}
				}

			}

		}

		ejectDropList(world, item, x, y, z);
		return false;
	}

	public boolean isFull3D() {
		return true;
	}

	public EnumAnimation d(ItemStack var1) {
		return EnumAnimation.d;
	}

	public int c(ItemStack var1) {
		return 72000;
	}

	public ItemStack a(ItemStack item, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) return item;

		var3.a(item, c(item));
		return item;
	}

	public void doShear(ItemStack item, World var2, EntityHuman var3, Entity var4) {
		if (chargeLevel(item) > 0) {
			//boolean var5 = false;
			int var6 = 0;
			if (getFuelRemaining(item) < 10) ConsumeReagent(item, var3, false);
			if (getFuelRemaining(item) < 10) ConsumeReagent(item, var3, false);
			if (getFuelRemaining(item) < 10) ConsumeReagent(item, var3, false);
			while (getFuelRemaining(item) >= 10 && var6 < chargeLevel(item)) {
				setShort(item, "fuelRemaining", getFuelRemaining(item) - 10);
				var6++;
				if (getFuelRemaining(item) < 10) ConsumeReagent(item, var3, false);
				if (getFuelRemaining(item) < 10) ConsumeReagent(item, var3, false);
				if (getFuelRemaining(item) < 10) ConsumeReagent(item, var3, false);
			}
			if (var6 > 0) {
				var3.C_();
				var2.makeSound(var3, "flash", 0.8F, 1.5F);
				int var7 = 3 * var6;
				if (var4 instanceof EntitySheep) {
					if (var2.random.nextInt(100) < var7) {
						EntitySheep var8 = new EntitySheep(var2);
						double var9 = var4.locX - var3.locX;
						double var11 = var4.locZ - var3.locZ;
						if (var9 < 0.0D) var9 *= -1D;
						if (var11 < 0.0D) var11 *= -1D;
						var9 += var4.locX;
						var11 += var4.locZ;
						double var13 = var4.locY;
						for (int var15 = -5; var15 <= 5; var15++) {
							if (var2.getTypeId((int) var9, (int) var13 + var15, (int) var11) == 0
									|| var2.getTypeId((int) var9, (int) var13 + var15 + 1, (int) var11) != 0) continue;
							var8.setPosition(var9, var13 + var15 + 1.0D, var11);
							var8.setColor(((EntitySheep) var4).getColor());
							var2.addEntity(var8);
							break;
						}

					}
					((EntitySheep) var4).setSheared(true);
					int var19 = 3 + var2.random.nextInt(2) + chargeLevel(item) / 2;
					EntityItem var21 = null;
					for (int var10 = 0; var10 < var19; var10++)
						var21 = new EntityItem(var2, var3.locX, var3.locY, var3.locZ, new ItemStack(Block.WOOL.id, var19, ((EntitySheep) var4).getColor()));

					var2.addEntity(var21);
				} else if (var4 instanceof EntityMushroomCow) {
					if (var2.random.nextInt(100) < var7) {
						EntityMushroomCow var18 = new EntityMushroomCow(var2);
						double var9 = var4.locX - var3.locX;
						double var11 = var4.locZ - var3.locZ;
						if (var9 < 0.0D) var9 *= -1D;
						if (var11 < 0.0D) var11 *= -1D;
						var9 += var4.locX;
						var11 += var4.locZ;
						double var13 = var4.locY;
						for (int var15 = -5; var15 <= 5; var15++) {
							if (var2.getTypeId((int) var9, (int) var13 + var15, (int) var11) == 0
									|| var2.getTypeId((int) var9, (int) var13 + var15 + 1, (int) var11) != 0) continue;
							var18.setPosition(var9, var13 + var15 + 1.0D, var11);
							var2.addEntity(var18);
							break;
						}

					}
					((EntityMushroomCow) var4).die();
					EntityCow var20 = new EntityCow(var2);
					var20.setPositionRotation(var4.locX, var4.locY, var4.locZ, var4.yaw, var4.pitch);
					var20.setHealth(((EntityMushroomCow) var4).getHealth());
					var20.V = ((EntityMushroomCow) var4).V;
					var2.addEntity(var20);
					var2.a("largeexplode", var4.locX, var4.locY + (var4.length / 2.0F), var4.locZ, 0.0D, 0.0D, 0.0D);
					int var23 = 5 + var2.random.nextInt(2) + chargeLevel(item) / 2;
					//Object var22 = null;
					for (int var24 = 0; var24 < var23; var24++)
						new EntityItem(var2, var3.locX, var3.locY, var3.locZ, new ItemStack(Block.RED_MUSHROOM, var23));

				}
			}
		} else if (var4 instanceof EntitySheep) {
			new EntitySheep(var2);
			((EntitySheep) var4).setSheared(true);
			int var6 = 3 + var2.random.nextInt(2);
			EntityItem var17 = null;
			for (int var19 = 0; var19 < var6; var19++)
				var17 = new EntityItem(var2, var3.locX, var3.locY, var3.locZ, new ItemStack(Block.WOOL.id, var6, ((EntitySheep) var4).getColor()));

			var2.addEntity(var17);
		} else if (var4 instanceof EntityMushroomCow) {
			((EntityMushroomCow) var4).die();
			EntityCow var16 = new EntityCow(((EntityMushroomCow) var4).world);
			var16.setPositionRotation(((EntityMushroomCow) var4).locX, ((EntityMushroomCow) var4).locY, ((EntityMushroomCow) var4).locZ,
					((EntityMushroomCow) var4).yaw, ((EntityMushroomCow) var4).pitch);
			var16.setHealth(((EntityMushroomCow) var4).getHealth());
			var16.V = ((EntityMushroomCow) var4).V;
			((EntityMushroomCow) var4).world.addEntity(var16);
			((EntityMushroomCow) var4).world.a("largeexplode", ((EntityMushroomCow) var4).locX, ((EntityMushroomCow) var4).locY
					+ (((EntityMushroomCow) var4).length / 2.0F), ((EntityMushroomCow) var4).locZ, 0.0D, 0.0D, 0.0D);
			for (int var6 = 0; var6 < 5; var6++)
				((EntityMushroomCow) var4).world.addEntity(new EntityItem(((EntityMushroomCow) var4).world, ((EntityMushroomCow) var4).locX,
						((EntityMushroomCow) var4).locY + ((EntityMushroomCow) var4).length, ((EntityMushroomCow) var4).locZ, new ItemStack(
								Block.RED_MUSHROOM)));

		}
	}

	public int a(Entity var1) {
		return (var1 instanceof EntitySheep) || (var1 instanceof EntityMushroomCow) ? 1 : weaponDamage;
	}

	public boolean a(ItemStack item, EntityLiving var2, EntityLiving var3) {
		if (!(var3 instanceof EntityHuman)) return true;
		
		EntityHuman var4 = (EntityHuman) var3;
		if (EEEventManager.callEvent(new EERedKatarEvent(item, EEAction.RIGHTCLICK, var4, EEAction2.Shear))) return true;
		if (var2 instanceof EntitySheep) {
			if (!((EntitySheep) var2).isSheared()) doShear(item, var4.world, var4, var2);
			var2.heal(1);
		} else if (var2 instanceof EntityMushroomCow) {
			doShear(item, var4.world, var4, var2);
			var2.heal(1);
		}
		
		return true;
	}

	public void doAlternate(ItemStack item, World var2, EntityHuman var3) {
		//if (EEEventManager.callEvent(new EERedKatarEvent(item, EEAction.ALTERNATE, var3, EEAction2.UpdateSwordMode))) return;
		EEBase.updateSwordMode(var3);
	}

	public void doRelease(ItemStack item, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EERedKatarEvent(item, EEAction.RELEASE, var3, EEAction2.AttackRadius))) return;
		doSwordBreak(item, var2, var3);
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	public boolean itemCharging;
	private static Block blocksEffectiveAgainst[];

	static {
		blocksEffectiveAgainst = (new Block[] { Block.WOOD, Block.BOOKSHELF, Block.LOG, Block.CHEST, Block.DIRT, Block.GRASS, Block.LEAVES, Block.WEB, Block.WOOL });
	}
}