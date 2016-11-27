package ee;

import java.util.Iterator;
import java.util.List;

import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EERingAction;
import ee.events.ring.EESWRingEvent;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ItemSwiftWolfRing extends ItemEECharged {
	@SuppressWarnings("unused")
	private int ticksLastSpent;
	public boolean itemCharging;

	public ItemSwiftWolfRing(int var1) {
		super(var1, 0);
	}

	public int getIconFromDamage(int var1) {
		boolean a1 = isActivated(var1);
		boolean a2 = isActivated2(var1);
		return a1 && !a2 ? this.textureId + 1 :
			   a2 && !a1 ? this.textureId + 2 :
			   a1 && a2 ? this.textureId + 3 :
			   this.textureId;
	}

	public void doGale(ItemStack item, World world, EntityHuman human) {
		world.makeSound(human, "gust", 0.6F, 1.0F);
		world.addEntity(new EntityWindEssence(world, human));
	}

	public void doInterdiction(ItemStack item, World world, EntityHuman human) {
		double x = (float) human.locX - 5.0F, x2 = (float) human.locX + 5.0F;
		double y = human.locY - 5.0D, y2 = human.locY + 5.0D;
		double z = (float) human.locZ - 5.0F, z2 = (float) human.locZ + 5.0F;
		List<Entity> var4 = world.a(EntityMonster.class, AxisAlignedBB.b(x, y, z, x2, y2, z2));
		Iterator<Entity> var6 = var4.iterator();

		while (var6.hasNext()) {
			Entity var5 = var6.next();
			PushEntities(var5, human);
		}

		List<Entity> var11 = world.a(EntityArrow.class, AxisAlignedBB.b(x, y, z, x2, y2, z2));
		Iterator<Entity> var8 = var11.iterator();

		while (var8.hasNext()) {
			Entity var7 = var8.next();
			PushEntities(var7, human);
		}

		List<Entity> var12 = world.a(EntityFireball.class, AxisAlignedBB.b(x, y, z, x2, y2, z2));
		Iterator<Entity> var10 = var12.iterator();

		while (var10.hasNext()) {
			Entity var9 = var10.next();
			PushEntities(var9, human);
		}
	}

	private void PushEntities(Entity var1, EntityHuman human) {
		if (var1 instanceof EntityHuman) return;
		
		double dx = human.locX + 0.5D - var1.locX;
		double dy = human.locY + 0.5D - var1.locY;
		double dz = human.locZ + 0.5D - var1.locZ;
		double var10 = dx * dx + dy * dy + dz * dz;
		var10 *= var10;

		if (var10 <= 1296d) {
			double vx = -(dx * 0.02D / var10) * 216;
			double vy = -(dy * 0.02D / var10) * 216;
			double vz = -(dz * 0.02D / var10) * 216;

			if (vx > 0.0D) vx = 0.12D;
			else if (vx < 0.0D) vx = -0.12D;

			if (vy > 0.2D) vy = 0.12D;
			else if (vy < -0.1D) vy = 0.12D;

			if (vz > 0.0D) vz = 0.12D;
			else if (vz < 0.0D) vz = -0.12D;

			var1.motX += vx;
			var1.motY += vy;
			var1.motZ += vz;
		}
		
	}

	public void ConsumeReagent(ItemStack item, EntityHuman human, boolean var3) {
		EEBase.ConsumeReagentForDuration(item, human, var3);
	}

	private int callnr = 20;
	private boolean allowed = true;
	public void doPassive(ItemStack item, World world, EntityHuman human) {
		if (human.fallDistance > 0.0F) {
			if (callnr >= 20){
				allowed = !EEEventManager.callEvent(new EESWRingEvent(item, EEAction.PASSIVE, human, EERingAction.NegateFallDamage));
				callnr = 0;
			}
			
			callnr++;
			if (allowed) human.fallDistance = 0.0F;
		}
	}
	private int callnr2 = 1200;
	private boolean allowed2 = true;
	public void doActive(ItemStack item, World world, EntityHuman human) {
		if (isActivated2(item.getData())) {
			if (callnr2 >= 1200){
				allowed2 = !EEEventManager.callEvent(new EESWRingEvent(item, EEAction.ACTIVE, human, EERingAction.Interdict));
				if (!allowed2){
					//Turn off
					item.setData(item.getData() - 2);
					item.tag.setBoolean("active2", false);
					world.makeSound(human, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
					callnr2 = 1200;
					return;
				}
				callnr2 = 0;
			}
			callnr2++;
			if (allowed2) doInterdiction(item, world, human);
		}
	}

	public void doHeld(ItemStack item, World world, EntityHuman human) {}

	public void doRelease(ItemStack item, World world, EntityHuman human) {
		if (EEEventManager.callEvent(new EESWRingEvent(item, EEAction.RELEASE, human, EERingAction.Gust))) return;
		doGale(item, world, human);
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return item;
		
		if (EEEventManager.callEvent(new EESWRingEvent(item, EEAction.RIGHTCLICK, human, EERingAction.Gust))) return item;
		
		doGale(item, world, human);
		return item;
	}

	public void doAlternate(ItemStack item, World world, EntityHuman human) {
		if(isActivated2(item.getData())) {
			item.setData(item.getData() - 2);
			item.tag.setBoolean("active2", false);
			world.makeSound(human, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		} else {
			if (EEEventManager.callEvent(new EESWRingEvent(item, EEAction.ALTERNATE, human, EERingAction.ActivateInterdict))) return;
			item.setData(item.getData() + 2);
			item.tag.setBoolean("active2", true);
			world.makeSound(human, "heal", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		}
	}

	public void doLeftClick(ItemStack item, World world, EntityHuman human) {}

	public void doToggle(ItemStack item, World world, EntityHuman human) {
		if(isActivated(item)) {
			//if (EEEventManager.callEvent(new EESWRingEvent(item, EEAction.TOGGLE, human, EERingAction.Deactivate))) return;
			item.setData(item.getData() - 1);
			item.tag.setBoolean("active", false);
			world.makeSound(human, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		} else {
			if (EEEventManager.callEvent(new EESWRingEvent(item, EEAction.TOGGLE, human, EERingAction.Activate))) return;
			item.setData(item.getData() + 1);
			item.tag.setBoolean("active", true);
			world.makeSound(human, "heal", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		}

		if (!isActivated(item) && !EEBase.isPlayerInWater(human) && !EEBase.isPlayerInLava(human)) {
			human.abilities.isFlying = false;
			human.updateAbilities();
		}
	}

	public boolean canActivate() {
		return true;
	}
	public boolean canActivate2() {
		return true;
	}

	public void doCharge(ItemStack var1, World var2, EntityHuman var3) {}
	public void doChargeTick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doUncharge(ItemStack var1, World var2, EntityHuman var3) {}
}