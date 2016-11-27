package ee;

import java.util.List;

import ee.events.EEEnums.EERingAction;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.ring.EEZeroRingEvent;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntitySnowball;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.World;

public class ItemZeroRing extends ItemEECharged {
	public ItemZeroRing(int var1) {
		super(var1, 4);
	}

	public int getIconFromDamage(int var1) {
		return !isActivated(var1) ? this.textureId : this.textureId + 1;
	}

	public void doBreak(ItemStack var1, World var2, EntityHuman human) {
		
		int charge = chargeLevel(var1);
		var2.makeSound(human, "wall", 1.0F, 1.0F);
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);

		for (int var8 = -charge - 1; var8 <= charge + 1; var8++) {
			for (int var9 = -2; var9 <= 1; var9++) {
				for (int var10 = -charge - 1; var10 <= charge + 1; var10++) {
					int nx = x + var10;
					int ny = y + var9;
					int nz = z + var8;
					int var11 = var2.getTypeId(nx, ny - 1, nz);

					if ((var11 != 0) && (Block.byId[var11].a()) && (var2.getMaterial(nx, ny - 1, nz).isBuildable()) && (var2.getTypeId(nx, ny, nz) == 0)) {
						if (attemptPlace(human, nx, ny, nz)){
							var2.setTypeId(nx, ny, nz, Block.SNOW.id);
						}
						
					}
					Material mat = var2.getMaterial(nx, ny, nz);
					int id = var2.getTypeId(nx, ny + 1, nz);
					if ((mat == Material.WATER) && (id == 0)) {
						if (attemptBreak(human, nx, ny, nz)) var2.setTypeId(nx, ny, nz, Block.ICE.id);
					}

					else if ((mat == Material.LAVA) && (id == 0) && (var2.getData(nx, ny, nz) == 0)) {
						if (attemptBreak(human, nx, ny, nz)) var2.setTypeId(nx, ny, nz, Block.OBSIDIAN.id);
					}
				}
			}
		}
	}

	public void doFreezeOverTime(ItemStack item, World world, EntityHuman human) {
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);
		List<Entity> var7 = world.a(EntityMonster.class,
				AxisAlignedBB.b(human.locX - 5.0D, human.locY - 5.0D, human.locZ - 5.0D, human.locX + 5.0D, human.locY + 5.0D, human.locZ + 5.0D));

		for (int var8 = 0; var8 < var7.size(); var8++) {
			Entity var9 = var7.get(var8);

			if ((var9.motX > 0.0D) || (var9.motZ > 0.0D)) {
				var9.motX *= 0.2D;
				var9.motZ *= 0.2D;
			}
		}

		for (int var8 = -4; var8 <= 4; var8++) {
			for (int var12 = -4; var12 <= 4; var12++) {
				for (int var10 = -4; var10 <= 4; var10++) {
					int nx = x + var8;
					int ny = y + var12;
					int nz = z + var10;
					if ((var8 <= -2 || var8 >= 2 || var12 != 0) && (var10 <= -2 || var10 >= 2 || var12 != 0)) {
						if (world.random.nextInt(20) == 0) {
							int var11 = world.getTypeId(nx, ny - 1, nz);

							if ((var11 != 0) && (Block.byId[var11].a()) && (world.getMaterial(nx, ny - 1, nz).isBuildable())
									&& (world.getTypeId(nx, ny, nz) == 0)) {
								if (attemptPlace(human, nx, ny, nz)){
									world.setTypeId(nx, ny, nz, Block.SNOW.id);
								}
							}
						}

						if ((world.random.nextInt(3) == 0) && (world.getMaterial(nx, ny, nz) == Material.WATER)
								&& (world.getTypeId(nx, ny + 1, nz) == 0)) {
							if (attemptBreak(human, nx, ny, nz))
								world.setTypeId(nx, ny, nz, Block.ICE.id);
						}

						else if ((world.random.nextInt(3) == 0) && (world.getMaterial(nx, ny, nz) == Material.LAVA)
								&& (world.getTypeId(nx, ny + 1, nz) == 0)
								&& (world.getData(nx, ny, nz) == 0)) {
							if (attemptBreak(human, nx, ny, nz))
								world.setTypeId(nx, ny, nz, Block.OBSIDIAN.id);
						}
					}
				}
			}
		}
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return item;
		
		if (EEEventManager.callEvent(new EEZeroRingEvent(item, EEAction.RIGHTCLICK, human, EERingAction.Freeze))) return item;
		doBreak(item, world, human);
		return item;
	}

	public void ConsumeReagent(ItemStack var1, EntityHuman var2, boolean var3) {
		EEBase.ConsumeReagentForDuration(var1, var2, var3);
	}

	private int callnr = 1200;
	private boolean allowed = true;
	public void doActive(ItemStack item, World world, EntityHuman human) {
		if (callnr >= 1200){
			allowed = !EEEventManager.callEvent(new EEZeroRingEvent(item, EEAction.ACTIVE, human, EERingAction.Freeze));
			if (!allowed){
				if (isActivated(item.getData())) item.setData(item.getData() - 1);
				item.tag.setBoolean("active", false);
				world.makeSound(human, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
				callnr = 1200;
				return;
			}
			callnr = 0;
		}

		callnr++;
		if (allowed) doFreezeOverTime(item, world, human);
	}

	public void doHeld(ItemStack item, World world, EntityHuman human) {}

	public void doRelease(ItemStack item, World world, EntityHuman human) {
		if (EEEventManager.callEvent(new EEZeroRingEvent(item, EEAction.RELEASE, human, EERingAction.Freeze))) return;
		doBreak(item, world, human);
	}

	public void doAlternate(ItemStack item, World world, EntityHuman human) {}

	public void doLeftClick(ItemStack item, World world, EntityHuman human) {
		human.C_();
		world.makeSound(human, "random.bow", 0.5F, 0.4F / (c.nextFloat() * 0.4F + 0.8F));

		if (!world.isStatic) {
			if (EEEventManager.callEvent(new EEZeroRingEvent(item, EEAction.LEFTCLICK, human, EERingAction.ThrowSnowball))) return;
			world.addEntity(new EntitySnowball(world, human));
		}
	}

	public boolean canActivate() {
		return true;
	}
}