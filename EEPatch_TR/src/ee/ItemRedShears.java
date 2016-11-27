package ee;

import java.util.*;

import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.rm.EERMShearsEvent;

import net.minecraft.server.*;

public class ItemRedShears extends ItemRedTool {

	public ItemRedShears(int var1) {
		super(var1, 3, 12, blocksEffectiveAgainst);
	}

	public boolean a(ItemStack var1, int var2, int var3, int var4, int var5, EntityLiving var6) {
		boolean var7 = false;
		if (!EEMaps.isLeaf(var2) && var2 != Block.WEB.id && var2 != Block.VINE.id && var2 != BlockFlower.LONG_GRASS.id && var2 != BlockFlower.DEAD_BUSH.id)
			var7 = true;
		if (!var7)
			EEProxy.dropBlockAsItemStack(Block.byId[var2], var6, var3, var4, var5,
					new ItemStack(var2, 1, var2 != Block.LEAVES.id ? ((int) (((ItemEECharged) var1.getItem()).getShort(var1, "lastMeta")))
							: ((ItemEECharged) var1.getItem()).getShort(var1, "lastMeta") & 3));
		return super.a(var1, var2, var3, var4, var5, var6);
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return var1.id == Block.WEB.id;
	}

	public float getDestroySpeed(ItemStack var1, Block var2) {
		return var2.id == Block.VINE.id || var2.id == Block.LEAVES.id || var2.id == Block.WEB.id ? 15F : var2.id != Block.WOOL.id ? super.getDestroySpeed(var1, var2) : 5F;
	}

	public void doBreak(ItemStack item, World world, EntityHuman human) {
		int charge = chargeLevel(item);
		if (charge < 1) return;
		
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);
		cleanDroplist(item);
		charge = chargeLevel(item);
		if (charge < 1) return;
		
		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);
		for (int var7 = -(charge + 2); var7 <= charge + 2; var7++) {
			for (int var8 = -(charge + 2); var8 <= charge + 2; var8++) {
				for (int var9 = -(charge + 2); var9 <= charge + 2; var9++) {
					
					int nx = x + var7;
					int ny = y + var8;
					int nz = z + var9;
					int id = world.getTypeId(nx, ny, nz);
					if ((EEMaps.isLeaf(id) || id == Block.VINE.id || id == Block.WEB.id || id == Block.LONG_GRASS.id || id == Block.DEAD_BUSH.id)
							&& attemptBreak(human, nx, ny, nz)) {
						if (getFuelRemaining(item) < 1) ConsumeReagent(item, human, false);
						if (getFuelRemaining(item) > 0) {
							int var11 = world.getData(nx, ny, nz);
							if (!EEMaps.isLeaf(id) && id != Block.VINE.id && id != Block.WEB.id && id != Block.LONG_GRASS.id && id != Block.DEAD_BUSH.id) {
								ArrayList<ItemStack> var12 = Block.byId[id].getBlockDropped(world, nx, ny, nz, var11, 0);
								ItemStack var14;
								for (Iterator<ItemStack> var13 = var12.iterator(); var13.hasNext(); addToDroplist(item, var14))
									var14 = var13.next();

							}
							else if (id == Block.LEAVES.id)
								addToDroplist(item, new ItemStack(Block.LEAVES.id, 1, var11 & 3));
							else
								addToDroplist(item, new ItemStack(Block.byId[id], 1, var11));
							
							setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);
							world.setTypeId(nx, ny, nz, 0);
							
							if (world.random.nextInt(8) == 0) world.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if (world.random.nextInt(8) == 0) world.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
						}
					}
				}

			}

		}

		ejectDropList(world, item, x, y, z);
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int var7) {
		if (EEProxy.isClient(world)) return false;
		
		if (EEEventManager.callEvent(new EERMShearsEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;
		
		int charge = chargeLevel(item);
		if (charge < 1) return false;
		
		// boolean var8 = false;
		cleanDroplist(item);
		
		charge = chargeLevel(item);
		if (charge < 1) return false;
		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);
		for (int var9 = -(charge + 2); var9 <= charge + 2; var9++) {
			for (int var10 = -(charge + 2); var10 <= charge + 2; var10++) {
				for (int var11 = -(charge + 2); var11 <= charge + 2; var11++) {
					int nx = x + var9;
					int ny = y + var10;
					int nz = z + var11;
					int id = world.getTypeId(nx, ny, nz);
					if ((EEMaps.isLeaf(id) || id == Block.VINE.id || id == Block.WEB.id || id == Block.LONG_GRASS.id || id == Block.DEAD_BUSH.id)
							&& getFuelRemaining(item) < 1 && attemptBreak(human, nx, ny, nz)) {
						int data = world.getData(nx, ny, nz);
						ArrayList<ItemStack> var14 = Block.byId[id].getBlockDropped(world, nx, ny, nz, data, 0);
						ItemStack var16;
						for (Iterator<ItemStack> var15 = var14.iterator(); var15.hasNext(); addToDroplist(item, var16))
							var16 = var15.next();

						setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);
						world.setTypeId(nx, ny, nz, 0);
						if (world.random.nextInt(8) == 0) world.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
						if (world.random.nextInt(8) == 0) world.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
					}
				}

			}

		}

		ejectDropList(world, item, x, y, z);
		return false;
	}

	public void doShear(ItemStack item, World world, EntityHuman human, Entity var4) {
		if (chargeLevel(item) > 0) {
			// boolean var5 = false;
			int var6 = 0;
			if (getFuelRemaining(item) < 10) ConsumeReagent(item, human, false);
			if (getFuelRemaining(item) < 10) ConsumeReagent(item, human, false);
			if (getFuelRemaining(item) < 10) ConsumeReagent(item, human, false);
			while (getFuelRemaining(item) >= 10 && var6 < chargeLevel(item)) {
				setShort(item, "fuelRemaining", getFuelRemaining(item) - 10);
				var6++;
				if (getFuelRemaining(item) < 10) ConsumeReagent(item, human, false);
				if (getFuelRemaining(item) < 10) ConsumeReagent(item, human, false);
				if (getFuelRemaining(item) < 10) ConsumeReagent(item, human, false);
			}
			if (var6 > 0) {
				human.C_();
				world.makeSound(human, "flash", 0.8F, 1.5F);
				int var7 = 2 * var6;
				if (var4 instanceof EntitySheep) {
					if (world.random.nextInt(100) < var7) {
						EntitySheep var8 = new EntitySheep(world);
						double var9 = var4.locX - human.locX;
						double var11 = var4.locZ - human.locZ;
						if (var9 < 0.0D) var9 *= -1D;
						if (var11 < 0.0D) var11 *= -1D;
						var9 += var4.locX;
						var11 += var4.locZ;
						double var13 = var4.locY;
						for (int var15 = -5; var15 <= 5; var15++) {
							if (world.getTypeId((int) var9, (int) var13 + var15, (int) var11) == 0
									|| world.getTypeId((int) var9, (int) var13 + var15 + 1, (int) var11) != 0) continue;
							var8.setPosition(var9, var13 + var15 + 1.0D, var11);
							var8.setColor(((EntitySheep) var4).getColor());
							world.addEntity(var8);
							break;
						}

					}
					((EntitySheep) var4).setSheared(true);
					int var19 = 3 + world.random.nextInt(2) + chargeLevel(item) / 4;
					EntityItem var21 = null;
					for (int var10 = 0; var10 < var19; var10++)
						var21 = new EntityItem(world, human.locX, human.locY, human.locZ, new ItemStack(Block.WOOL.id, var19, ((EntitySheep) var4).getColor()));

					world.addEntity(var21);
				} else if (var4 instanceof EntityMushroomCow) {
					if (world.random.nextInt(100) < var7) {
						EntityMushroomCow var18 = new EntityMushroomCow(world);
						double var9 = var4.locX - human.locX;
						double var11 = var4.locZ - human.locZ;
						if (var9 < 0.0D) var9 *= -1D;
						if (var11 < 0.0D) var11 *= -1D;
						var9 += var4.locX;
						var11 += var4.locZ;
						double var13 = var4.locY;
						for (int var15 = -5; var15 <= 5; var15++) {
							if (world.getTypeId((int) var9, (int) var13 + var15, (int) var11) == 0
									|| world.getTypeId((int) var9, (int) var13 + var15 + 1, (int) var11) != 0) continue;
							var18.setPosition(var9, var13 + var15 + 1.0D, var11);
							world.addEntity(var18);
							break;
						}

					}
					((EntityMushroomCow) var4).die();
					EntityCow var20 = new EntityCow(world);
					var20.setPositionRotation(var4.locX, var4.locY, var4.locZ, var4.yaw, var4.pitch);
					var20.setHealth(((EntityMushroomCow) var4).getHealth());
					var20.V = ((EntityMushroomCow) var4).V;
					world.addEntity(var20);
					world.a("largeexplode", var4.locX, var4.locY + (var4.length / 2.0F), var4.locZ, 0.0D, 0.0D, 0.0D);
					int var23 = 5 + world.random.nextInt(2) + chargeLevel(item) / 4;

					for (int var24 = 0; var24 < var23; var24++)
						new EntityItem(world, human.locX, human.locY, human.locZ, new ItemStack(Block.RED_MUSHROOM, var23));

				}
			}
		} else if (var4 instanceof EntitySheep) {
			new EntitySheep(world);
			((EntitySheep) var4).setSheared(true);
			int var6 = 3 + world.random.nextInt(2);
			EntityItem var17 = null;
			for (int var19 = 0; var19 < var6; var19++)
				var17 = new EntityItem(world, human.locX, human.locY, human.locZ, new ItemStack(Block.WOOL.id, var6, ((EntitySheep) var4).getColor()));

			world.addEntity(var17);
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
				((EntityMushroomCow) var4).world.addEntity(new EntityItem(((EntityMushroomCow) var4).world, ((EntityMushroomCow) var4).locX, ((EntityMushroomCow) var4).locY
								+ ((EntityMushroomCow) var4).length, ((EntityMushroomCow) var4).locZ, new ItemStack(Block.RED_MUSHROOM)));

		}
	}

	public int a(Entity var1) {
		return (var1 instanceof EntitySheep) || (var1 instanceof EntityMushroomCow) ? 1 : weaponDamage;
	}

	public boolean a(ItemStack item, EntityLiving var2, EntityLiving var3) {
		if (!(var3 instanceof EntityHuman)) return true;
		
		EntityHuman human = (EntityHuman) var3;
		if (var2 instanceof EntitySheep) {
			if (!((EntitySheep) var2).isSheared()) {
				if (EEEventManager.callEvent(new EERMShearsEvent(item, EEAction.RIGHTCLICK, human, EEAction2.Shear))) return true;
				doShear(item, human.world, human, var2);
			}
			var2.heal(1);
		} else if (var2 instanceof EntityMushroomCow) {
			if (EEEventManager.callEvent(new EERMShearsEvent(item, EEAction.RIGHTCLICK, human, EEAction2.Shear))) return true;
			doShear(item, human.world, human, var2);
			var2.heal(1);
		}
		
		return true;
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) {
			return item;
		} else {
			if (EEEventManager.callEvent(new EERMShearsEvent(item, EEAction.RIGHTCLICK, human, EEAction2.BreakRadius))) return item;
			doBreak(item, world, human);
			return item;
		}
	}

	public void doHeld(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	public void doRelease(ItemStack item, World var2, EntityHuman human) {
		if (EEEventManager.callEvent(new EERMShearsEvent(item, EEAction.RELEASE, human, EEAction2.BreakRadius))) return;
		doBreak(item, var2, human);
	}

	public void doAlternate(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	public void doLeftClick(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	static {
		blocksEffectiveAgainst = (new Block[] { Block.LEAVES, Block.WEB, Block.WOOL });
	}
	
	private static Block blocksEffectiveAgainst[];
	@SuppressWarnings("unused")
	private static boolean leafHit;
	@SuppressWarnings("unused")
	private static boolean vineHit;
}