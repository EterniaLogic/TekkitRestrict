package ee;

import java.util.Iterator;
import java.util.List;

import ee.events.EEEnums.EERingAction;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.ring.EEVoidRingEvent;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.BlockContainer;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityExperienceOrb;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class ItemVoidRing extends ItemEECharged {
	@SuppressWarnings("unused")
	private int ticksLastSpent;

	public ItemVoidRing(int var1) {
		super(var1, 0);
	}

	public int getIconFromDamage(int data) {
		return ((data < 2) ? this.textureId + data : this.textureId);
	}

	private void PullItems(Entity entity, EntityHuman human) {
		if (entity.getClass().equals(EntityItem.class)) {
			EntityItem eItem = (EntityItem) entity;
			double var4 = (float) human.locX + 0.5F - eItem.locX;
			double var6 = (float) human.locY + 0.5F - eItem.locY;
			double var8 = (float) human.locZ + 0.5F - eItem.locZ;
			double var10 = var4 * var4 + var6 * var6 + var8 * var8;
			var10 *= var10;

			if (var10 > 1296d) return;
			double var12 = var4 * 0.01999999955296516D / var10 * 216d;
			double var14 = var6 * 0.01999999955296516D / var10 * 216d;
			double var16 = var8 * 0.01999999955296516D / var10 * 216d;

			if (var12 > 0.1D)
				var12 = 0.1D;
			else if (var12 < -0.1D)
				var12 = -0.1D;

			if (var14 > 0.1D)
				var14 = 0.1D;
			else if (var14 < -0.1D)
				var14 = -0.1D;

			if (var16 > 0.1D)
				var16 = 0.1D;
			else if (var16 < -0.1D)
				var16 = -0.1D;

			eItem.motX += var12 * 1.2D;
			eItem.motY += var14 * 1.2D;
			eItem.motZ += var16 * 1.2D;
		} else {
			if (!(entity.getClass().equals(EntityLootBall.class))) return;
			EntityLootBall eLootBall = (EntityLootBall) entity;
			double var4 = (float) human.locX + 0.5F - eLootBall.locX;
			double var6 = (float) human.locY + 0.5F - eLootBall.locY;
			double var8 = (float) human.locZ + 0.5F - eLootBall.locZ;
			double var10 = var4 * var4 + var6 * var6 + var8 * var8;
			var10 *= var10;

			if (var10 > 1296d) return;
			double var12 = var4 * 0.01999999955296516D / var10 * 216d;
			double var14 = var6 * 0.01999999955296516D / var10 * 216d;
			double var16 = var8 * 0.01999999955296516D / var10 * 216d;

			if (var12 > 0.1D)
				var12 = 0.1D;
			else if (var12 < -0.1D)
				var12 = -0.1D;

			if (var14 > 0.1D)
				var14 = 0.1D;
			else if (var14 < -0.1D)
				var14 = -0.1D;


			if (var16 > 0.1D)
				var16 = 0.1D;
			else if (var16 < -0.1D)
				var16 = -0.1D;

			eLootBall.motX += var12 * 1.2D;
			eLootBall.motY += var14 * 1.2D;
			eLootBall.motZ += var16 * 1.2D;
		}
	}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EEVoidRingEvent(var1, EEAction.RELEASE, var3, EERingAction.Teleport))) return;
		doTeleport(var1, var2, var3);
	}

	
	private long delay = 0;
	private void doTeleport(ItemStack item, World world, EntityHuman human) {
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
		double var21 = 150.0D;
		Vec3D var23 = var13.add(var18 * var21, var17 * var21, var20 * var21);
		MovingObjectPosition var24 = world.rayTrace(var13, var23, true);

		if ((var24 == null) || (var24.type != EnumMovingObjectType.TILE)) return;
		int var25 = var24.b;
		int var26 = var24.c;
		int var27 = var24.d;
		int var28 = var24.face;

		if (var28 == 0) var26 -= 2;
		else if (var28 == 1) ++var26;
		else if (var28 == 2) --var27;
		else if (var28 == 3) ++var27;
		else if (var28 == 4) --var25;
		else if (var28 == 5) ++var25;

		for (int var29 = 0; var29 < 32; var29++) {
			human.world.a("portal", var25, var26 + human.world.random.nextDouble() * 2.0D, var27, human.world.random.nextGaussian(), 0.0D,
					human.world.random.nextGaussian());
		}

		if ((human.world.getTypeId(var25, var26, var27) == 0) && (human.world.getTypeId(var25, var26 + 1, var27) == 0)) {
			if (!human.getBukkitEntity().hasPermission("eepatch.delay.voidring") || delay <= System.currentTimeMillis()){
				human.enderTeleportTo(var25, var26, var27);
				delay = System.currentTimeMillis()+1000*5;
			}
		}

		human.fallDistance = 0.0F;
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return item;

		if (EEEventManager.callEvent(new EEVoidRingEvent(item, EEAction.RIGHTCLICK, human, EERingAction.DeleteLiquid))) return item;
		doDisintegrate(item, world, human);
		return item;
	}

	private void doDisintegrate(ItemStack item, World world, EntityHuman human) {
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

		if ((var24 == null) || (var24.type != EnumMovingObjectType.TILE)) return;
		int nx = var24.b;
		int ny = var24.c;
		int nz = var24.d;

		Material mat = world.getMaterial(nx, ny, nz);
		if (mat == Material.WATER) {
			if (attemptBreak(human, nx, ny, nz))
				world.setTypeId(nx, ny, nz, 0);
		} else if (world.getMaterial(nx, ny + 1, nz) == Material.WATER) {
			if (attemptBreak(human, nx, ny + 1, nz))
				world.setTypeId(nx, ny + 1, nz, 0);
		} else if (mat == Material.LAVA) {
			if (attemptBreak(human, nx, ny, nz))
				world.setTypeId(nx, ny, nz, 0);
		} else if (world.getMaterial(nx, ny + 1, nz) == Material.LAVA) {
			if (attemptBreak(human, nx, ny + 1, nz))
				world.setTypeId(nx, ny + 1, nz, 0);
		}
	}

	private int callnr = 1200;
	private boolean allowAttract = true, allowCondense = true;
	public void doPassive(ItemStack var1, World var2, EntityHuman var3) {
		if (isActivated(var1)) {
			if (callnr >= 1200) {
				allowAttract = !EEEventManager.callEvent(new EEVoidRingEvent(var1, EEAction.PASSIVE, var3, EERingAction.AttractItems));
				allowCondense = !EEEventManager.callEvent(new EEVoidRingEvent(var1, EEAction.PASSIVE, var3, EERingAction.Condense));
				if (!allowAttract && !allowCondense){
					var1.setData(0);
					var1.tag.setBoolean("active", false);
					var2.makeSound(var3, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
					callnr = 1200;
					if (isActivated(var1.getData())) return;
					dumpContents(var1, var3);
					return;
				}
				callnr = 0;
			}

			callnr++;

			if (allowAttract) doAttraction(var1, var2, var3);
			if (allowCondense) doCondense(var1, var2, var3);
		}

		if (!isActivated(var1.getData()))
			dumpContents(var1, var3);
	}

	public boolean roomFor(ItemStack var1, EntityHuman var2) {
		if (var1 == null) return false;

		for (int var3 = 0; var3 < var2.inventory.items.length; ++var3) {
			if (var2.inventory.items[var3] == null) return true;


			if ((var2.inventory.items[var3].doMaterialsMatch(var1)) && (var2.inventory.items[var3].count <= var1.getMaxStackSize() - var1.count)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("null")
	public void PushStack(ItemStack var1, EntityHuman var2) {
		if (var1 == null) return;
		for (int var3 = 0; var3 < var2.inventory.items.length; var3++) {
			if (var2.inventory.items[var3] == null) {
				var2.inventory.items[var3] = var1.cloneItemStack();
				var1 = null;
				return;
			}

			if ((var2.inventory.items[var3].doMaterialsMatch(var1)) && (var2.inventory.items[var3].count <= var1.getMaxStackSize() - var1.count)) {
				var2.inventory.items[var3].count += var1.count;
				var1 = null;
				return;
			}

			if (!(var2.inventory.items[var3].doMaterialsMatch(var1))) continue;
			while ((var2.inventory.items[var3].count < var2.inventory.items[var3].getMaxStackSize()) && (var1 != null)) {
				var2.inventory.items[var3].count++;
				var1.count--;

				if (var1.count <= 0){
					var1 = null;
					return;
				}
			}
		}
	}

	private void dumpContents(ItemStack var1, EntityHuman var2) {
		for(; emc(var1) >= EEMaps.getEMC(EEItem.redMatter.id) && roomFor(new ItemStack(EEItem.redMatter, 1), var2); PushStack(new ItemStack(EEItem.redMatter, 1), var2))
			takeEMC(var1, EEMaps.getEMC(EEItem.redMatter.id));

		for(; emc(var1) >= EEMaps.getEMC(EEItem.darkMatter.id) && roomFor(new ItemStack(EEItem.darkMatter, 1), var2); PushStack(new ItemStack(EEItem.darkMatter, 1), var2))
			takeEMC(var1, EEMaps.getEMC(EEItem.darkMatter.id));

		for(; emc(var1) >= EEMaps.getEMC(Item.DIAMOND.id) && roomFor(new ItemStack(Item.DIAMOND, 1), var2); PushStack(new ItemStack(Item.DIAMOND, 1), var2))
			takeEMC(var1, EEMaps.getEMC(Item.DIAMOND.id));

		for(; emc(var1) >= EEMaps.getEMC(Item.GOLD_INGOT.id) && roomFor(new ItemStack(Item.GOLD_INGOT, 1), var2); PushStack(new ItemStack(Item.GOLD_INGOT, 1), var2))
			takeEMC(var1, EEMaps.getEMC(Item.GOLD_INGOT.id));

		for(; emc(var1) >= EEMaps.getEMC(Item.IRON_INGOT.id) && roomFor(new ItemStack(Item.IRON_INGOT, 1), var2); PushStack(new ItemStack(Item.IRON_INGOT, 1), var2))
			takeEMC(var1, EEMaps.getEMC(Item.IRON_INGOT.id));

		for(; emc(var1) >= EEMaps.getEMC(Block.COBBLESTONE.id) && roomFor(new ItemStack(Block.COBBLESTONE, 1), var2); PushStack(new ItemStack(Block.COBBLESTONE, 1), var2))
			takeEMC(var1, EEMaps.getEMC(Block.COBBLESTONE.id));
	}

	public ItemStack target(ItemStack var1) {
		int id = getInteger(var1, "targetID");
		if (id == 0) return null;
		
		int meta = getInteger(var1, "targetMeta");
		return new ItemStack(id, 1, meta);
	}

	public ItemStack product(ItemStack item) {
		if (target(item) == null) return null;

		int emc = EEMaps.getEMC(target(item));//dirt, 1

		if (emc < EEMaps.getEMC(Item.IRON_INGOT.id)) return new ItemStack(Item.IRON_INGOT, 1);
		if (emc < EEMaps.getEMC(Item.GOLD_INGOT.id)) return new ItemStack(Item.GOLD_INGOT, 1);
		if (emc < EEMaps.getEMC(Item.DIAMOND.id)) return new ItemStack(Item.DIAMOND, 1);
		if (emc < EEMaps.getEMC(EEItem.darkMatter.id)) return new ItemStack(EEItem.darkMatter, 1);
		if (emc < EEMaps.getEMC(EEItem.redMatter.id)) return new ItemStack(EEItem.redMatter, 1);

		return null;
	}

	public void doCondense(ItemStack ring, World var2, EntityHuman human)
	{
		if (EEProxy.isClient(var2)) return;
		//emc ring = 0.
		ItemStack prod = product(ring);//product(ring) = product(dirt) = iron ingot.
		
		if (prod != null){
			int emcprod = EEMaps.getEMC(prod);
			if (emc(ring) >= emcprod && roomFor(prod, human))
			{
				PushStack(prod, human);
				takeEMC(ring, emcprod);
			}
		}

		int var4 = 0;//Item with highest emc value
		ItemStack[] invItems = human.inventory.items;//64 dirt
		int length = invItems.length;

		for (int i = 0; i < length; i++) {
			ItemStack invItem = invItems[i];
			if (invItem == null) continue;
			int emc = EEMaps.getEMC(invItem);
			if (emc != 0 && isValidMaterial(invItem, human) && emc > var4) {
				var4 = emc;
			}
		}

		invItems = human.inventory.items;
		length = invItems.length;

		for (int var7 = 0; var7 < length; var7++) {
			ItemStack var8 = invItems[var7];
			if (var8 == null) continue;
			int emc = EEMaps.getEMC(var8);
			if (emc != 0 && isValidMaterial(var8, human) && emc <= var4) {//all items with lower emc than the highest
				var4 = emc;
				setInteger(ring, "targetID", var8.id);
				setInteger(ring, "targetMeta", var8.getData());
			}
		}
		//Strange way to select the item with the lowest emc value
		ItemStack tar = target(ring);
		if (tar != null) {
			if (ConsumeMaterial(tar, human)) {
				addEMC(ring, EEMaps.getEMC(tar));
			}
		}
	}

	private boolean isLastCobbleStack(EntityHuman var1) {
		int var2 = 0;

		for (int var3 = 0; var3 < var1.inventory.items.length; ++var3) {
			if ((var1.inventory.items[var3] == null) || (var1.inventory.items[var3].id != Block.COBBLESTONE.id)) continue;
			var2 += var1.inventory.items[var3].count;
		}

		return (var2 <= 64);
	}

	private boolean isValidMaterial(ItemStack var1, EntityHuman var2) {
		if (EEMaps.getEMC(var1) == 0) return false;

		if (var1.id == Block.COBBLESTONE.id && isLastCobbleStack(var2)) {
			return false;
		}

		int var3 = var1.id;

		if (var3 >= Block.byId.length) {
			if (var3 != Item.IRON_INGOT.id && var3 != Item.GOLD_INGOT.id && var3 != Item.DIAMOND.id && var3 != EEItem.darkMatter.id) {
				return false;
			}

			if (var3 == EEItem.redMatter.id) return false;

		}
		//return (!EEMaps.isFuel(var1));
		if (EEMaps.isFuel(var1)) return false;
		else {
			if (var1.id == Block.LOG.id || var1.id == Block.WOOD.id){
				return false;
			} else {
				if (var3 >= Block.byId.length || !(Block.byId[var3] instanceof BlockContainer) || !Block.byId[var3].hasTileEntity(var1.getData())){
					return EEMaps.isValidEDItem(var1);
				} else {
					return false;
				}
			}
		}
	}

	private int emc(ItemStack var1) {
		return getInteger(var1, "emc");
	}

	private void takeEMC(ItemStack var1, int var2) {
		setInteger(var1, "emc", emc(var1) - var2);
	}

	private void addEMC(ItemStack var1, int var2) {
		setInteger(var1, "emc", emc(var1) + var2);
	}

	public boolean ConsumeMaterial(ItemStack var1, EntityHuman var2) {
		return EEBase.Consume(var1, var2, false);
	}

	public void doActive(ItemStack var1, World var2, EntityHuman human) {}

	private void doAttraction(ItemStack var1, World var2, EntityHuman human) {
		if (EEProxy.isClient(var2)) return;

		double x1 = human.locX - 10.0D, y1 = human.locY - 10.0D, z1 = human.locZ - 10.0D;
		double x2 = human.locX + 10.0D, y2 = human.locY + 10.0D, z2 = human.locZ + 10.0D;

		List<Entity> var4 = var2.a(EntityItem.class, AxisAlignedBB.b(x1, y1, z1, x2, y2, z2));
		Iterator<Entity> var6 = var4.iterator();

		while (var6.hasNext()) {
			Entity var5 = var6.next();
			PullItems(var5, human);
		}

		List<Entity> var11 = var2.a(EntityLootBall.class, AxisAlignedBB.b(x1, y1, z1, x2, y2, z2));
		Iterator<Entity> var8 = var11.iterator();

		while (var8.hasNext()) {
			Entity var7 = var8.next();
			PullItems(var7, human);
		}

		List<Entity> var12 = human.world.a(EntityExperienceOrb.class, AxisAlignedBB.b(x1, y1, z1, x2, y2, z2));
		Iterator<Entity> var10 = var12.iterator();

		while (var10.hasNext()) {
			Entity var9 = var10.next();
			PullItems(var9, human);
		}
	}

	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {
		if (isActivated(var1)) {
			var1.setData(0);
			var1.tag.setBoolean("active", false);
			var2.makeSound(var3, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		} else {
			if (EEEventManager.callEvent(new EEVoidRingEvent(var1, EEAction.PASSIVE, var3, EERingAction.Activate))) return;
			var1.setData(1);
			var1.tag.setBoolean("active", true);
			var2.makeSound(var3, "heal", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		}
	}

	public boolean canActivate() {
		return true;
	}

	public void doChargeTick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doUncharge(ItemStack var1, World var2, EntityHuman var3) {}
}
