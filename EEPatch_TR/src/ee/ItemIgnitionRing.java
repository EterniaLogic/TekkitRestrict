package ee;

import java.util.List;

import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EERingAction;
import ee.events.ring.EEIgnitionRingEvent;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.World;

public class ItemIgnitionRing extends ItemEECharged {
	public boolean itemCharging;

	public ItemIgnitionRing(int var1) {
		super(var1, 4);
	}

	public int getIconFromDamage(int var1) {
		return !isActivated(var1) ? this.textureId : this.textureId + 1;
	}

	public void doBreak(ItemStack item, World world, EntityHuman human) {
		int charge = chargeLevel(item);
		if (charge < 1) return;
		
		world.makeSound(human, "wall", 1.0F, 1.0F);
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);
		double var8 = MathHelper.floor(human.yaw * 4.0F / 360.0F + 0.5D) & 0x3;

		for (int var10 = -1; var10 <= 1; var10++) {
			for (int var11 = -2; var11 <= 1; var11++) {
				for (int var12 = -charge * 3; var12 <= charge * 3; var12++) {
					int nx = 0;
					int ny = y + var11;
					int nz = 0;
					int id = 1;
					if (var8 == 3.0D) {
						nx = x + var10;
						nz = z + var12;
						id = world.getTypeId(nx, ny, nz);
					} else if (var8 == 2.0D) {
						nx = x + var12;
						nz = z - var10;
						id = world.getTypeId(nx, ny, nz);
					} else if (var8 == 1.0D) {
						nx = x - var10;
						nz = z + var12;
						id = world.getTypeId(nx, ny, nz);
					} else if (var8 == 0.0D) {
						nx = x + var12;
						nz = z + var10;
						id = world.getTypeId(nx, ny, nz);
					}
					
					if (((id == 0 || id == 78) && world.getTypeId(nx, ny - 1, nz) != 0) && attemptBreak(human, nx, ny, nz)) {
						world.setTypeId(nx, ny, nz, Block.FIRE.id);
					}
				}
			}
		}
	}

	public void doBurn(ItemStack item, World world, EntityHuman human) {
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);
		List<EntityMonster> var7 = world.a(EntityMonster.class, AxisAlignedBB.b(human.locX - 5.0D, human.locY - 5.0D, human.locZ - 5.0D,
				human.locX + 5.0D, human.locY + 5.0D, human.locZ + 5.0D));

		for (int var8 = 0; var8 < var7.size(); var8++) {
			if (world.random.nextInt(30) == 0) {
				Entity var9 = var7.get(var8);
				EEProxy.dealFireDamage(var9, 5);
				var9.setOnFire(60);
			}
		}
		for (int var8 = -4; var8 <= 4; var8++) {
			for (int var13 = -4; var13 <= 4; var13++) {
				for (int var10 = -4; var10 <= 4; var10++) {
					if (((var8 <= -2) || (var8 >= 2) || (var13 != 0)) && ((var10 <= -2) || (var10 >= 2) || (var13 != 0)) && (world.random.nextInt(120) == 0)) {
						int nx = x + var8;
						int ny = y + var13;
						int nz = z + var10;
						if (world.getTypeId(nx, ny, nz) == 0 && world.getTypeId(nx, ny - 1, nz) != 0) {
							if (attemptBreak(human, nx, ny - 1, nz))
								world.setTypeId(nx, ny, nz, Block.FIRE.id);
						} else {
							boolean var11 = false;
							int var12 = 0;
							for (var12 = -1; var12 <= 1; var12++) {
								int id = world.getTypeId(nx + var12, ny, nz);
								if (id == Block.LEAVES.id || id == Block.LOG.id) {
									var11 = true;
									if (!attemptBreak(human, nx, ny, nz)) break;
									world.setTypeId(nx, ny, nz, Block.FIRE.id);
									break;
								}
							}

							if (!var11)
								for (var12 = -1; var12 <= 1; var12++) {
									int id = world.getTypeId(nx, ny + var12, nz);
									if (id == Block.LEAVES.id || id == Block.LOG.id) {
										var11 = true;
										if (!attemptBreak(human, nx, ny, nz)) break;
										world.setTypeId(nx, ny, nz, Block.FIRE.id);
										break;
									}
								}

							if (!var11)
								for (var12 = -1; var12 <= 1; var12++) {
									int id = world.getTypeId(nx, ny, nz + var12);
									if (id == Block.LEAVES.id || id == Block.LOG.id) {
										var11 = true;
										if (!attemptBreak(human, nx, ny, nz)) break;
										world.setTypeId(nx, ny, nz, Block.FIRE.id);
										break;
									}
								}
							
						}
					}
				}
			}
		}
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return item;
		
		if (EEEventManager.callEvent(new EEIgnitionRingEvent(item, EEAction.RIGHTCLICK, human, EERingAction.Burn))) return item;
		
		doBreak(item, world, human);
		return item;
	}

	public void ConsumeReagent(ItemStack item, EntityHuman human, boolean bool) {
		EEBase.ConsumeReagentForDuration(item, human, bool);
	}

	private int callnr = 600;
	private boolean allowed = true;
	public void doPassive(ItemStack item, World world, EntityHuman human) {
		if (callnr >= 600){
			allowed = !EEEventManager.callEvent(new EEIgnitionRingEvent(item, EEAction.PASSIVE, human, EERingAction.Extinguish));
			callnr = 0;
		}
		if (allowed){
			for (int var4 = -1; var4 <= 1; var4++) {
				for (int var5 = -1; var5 <= 1; var5++) {
					int x = (int) EEBase.playerX(human) + var4;
					int y = (int) EEBase.playerY(human);
					int z = (int) EEBase.playerZ(human) + var5;
					if (world.getTypeId(x, y - 1, z) == Block.FIRE.id && attemptBreak(human, x, y - 1, z))
						world.setTypeId(x, y - 1, z, 0);
				}
			}
		}
		callnr++;
	}

	private int callnr2 = 1200;
	private boolean allowed2 = true;
	public void doActive(ItemStack item, World world, EntityHuman human) {
		if (callnr2 >= 1200){
			allowed2 = !EEEventManager.callEvent(new EEIgnitionRingEvent(item, EEAction.ACTIVE, human, EERingAction.Burn));
			
			if (!allowed2){
				if (isActivated(item.getData())) item.setData(item.getData() - 1);
				item.tag.setBoolean("active", false);
				world.makeSound(human, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
				callnr2 = 1200;
				return;
			}
			callnr2 = 0;
		}
		
		if (allowed2) doBurn(item, world, human);
		callnr2++;
	}

	public void doHeld(ItemStack item, World world, EntityHuman human) {}

	public void doRelease(ItemStack item, World world, EntityHuman human) {
		if (EEEventManager.callEvent(new EEIgnitionRingEvent(item, EEAction.RELEASE, human, EERingAction.Burn))) return;
		doBreak(item, world, human);
	}

	public void doAlternate(ItemStack item, World world, EntityHuman human) {}

	public void doLeftClick(ItemStack item, World world, EntityHuman human) {
		if (EEEventManager.callEvent(new EEIgnitionRingEvent(item, EEAction.LEFTCLICK, human, EERingAction.ThrowPyrokinesis))) return;
		human.C_();
		world.makeSound(human, "wall", 1.0F, 1.0F);
		world.addEntity(new EntityPyrokinesis(world, human));
	}

	public boolean canActivate() {
		return true;
	}
}