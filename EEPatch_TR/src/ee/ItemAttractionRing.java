package ee;

import java.util.Iterator;
import java.util.List;

import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EERingAction;
import ee.events.EEEventManager;
import ee.events.ring.EEBHBEvent;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityExperienceOrb;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

public class ItemAttractionRing extends ItemEECharged {
	public ItemAttractionRing(int var1) {
		super(var1, 0);
		this.maxStackSize = 1;
	}

	public int getIconFromDamage(int var1) {
		return (!(isActivated(var1)) ? this.textureId : this.textureId + 1);
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
			EntityLootBall var18 = (EntityLootBall) entity;
			double var4 = (float) human.locX + 0.5F - var18.locX;
			double var6 = (float) human.locY + 0.5F - var18.locY;
			double var8 = (float) human.locZ + 0.5F - var18.locZ;
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

			var18.motX += var12 * 1.2D;
			var18.motY += var14 * 1.2D;
			var18.motZ += var16 * 1.2D;
		}
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return item;
		
		if (EEEventManager.callEvent(new EEBHBEvent(item, EEAction.RIGHTCLICK, human, EERingAction.DeleteLiquid))) return item;
		
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

		if (var24 == null) return item;

		if (var24.type == EnumMovingObjectType.TILE) {
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

		return item;
	}

	private int callnr = 600;
	private boolean allowed = true;
	public void doActive(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return;
		if (callnr >= 600){
			allowed = !EEEventManager.callEvent(new EEBHBEvent(item, EEAction.ACTIVE, human, EERingAction.AttractItems));
			if (!allowed){
				item.setData(1);
				world.makeSound(human, "heal", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
				callnr = 600;
				return;
			}
			callnr = 0;
		}
		callnr++;
		if (!allowed) return;
		
		double x1 = human.locX - 10.0D, y1 = human.locY - 10.0D, z1 = human.locZ - 10.0D;
		double x2 = human.locX + 10.0D, y2 = human.locY + 10.0D, z2 = human.locZ + 10.0D;
		
		List<Entity> entityItems = world.a(EntityItem.class, AxisAlignedBB.b(x1, y1, z1, x2, y2, z2));
		Iterator<Entity> itemsIterator = entityItems.iterator();

		while (itemsIterator.hasNext()) {
			Entity entityItem = itemsIterator.next();
			PullItems(entityItem, human);
		}

		List<Entity> entityLootBalls = world.a(EntityLootBall.class, AxisAlignedBB.b(x1, y1, z1, x2, y2, z2));
		Iterator<Entity> lootBallsIterator = entityLootBalls.iterator();

		while (lootBallsIterator.hasNext()) {
			Entity entityLootBall = lootBallsIterator.next();
			PullItems(entityLootBall, human);
		}

		List<Entity> entityXPOrbs = human.world.a(EntityExperienceOrb.class, AxisAlignedBB.b(x1, y1, z1, x2, y2, z2));
		Iterator<Entity> xpOrbsIterator = entityXPOrbs.iterator();

		while (xpOrbsIterator.hasNext()) {
			Entity entityXPOrb = xpOrbsIterator.next();
			PullItems(entityXPOrb, human);
		}
	}

	public void ConsumeReagent(ItemStack item, EntityHuman human, boolean unused) {
		EEBase.updatePlayerEffect(item.getItem(), 200, human);
	}

	public void doToggle(ItemStack item, World world, EntityHuman human) {
		if (isActivated(item.getData())) {
			item.setData(0);
			world.makeSound(human, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		} else {
			if (EEEventManager.callEvent(new EEBHBEvent(item, EEAction.TOGGLE, human, EERingAction.Activate))) return;
			item.setData(1);
			world.makeSound(human, "heal", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		}
	}

	//IMPORTANT This is a test
	public void doCharge(ItemStack var1, World var2, EntityHuman var3) {}
	
	
	public void doChargeTick(ItemStack var1, World var2, EntityHuman var3) {}
	public void doUncharge(ItemStack var1, World var2, EntityHuman var3) {}

	public boolean canActivate() {
		return true;
	}
}
