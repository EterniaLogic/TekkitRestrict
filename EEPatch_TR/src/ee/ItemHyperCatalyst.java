package ee;

import java.util.*;

import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.destruction.EEHyperCatalystEvent;
import net.minecraft.server.*;

public class ItemHyperCatalyst extends ItemEECharged {

	public ItemHyperCatalyst(int var1) {
		super(var1, 7);
	}

	public void doBreak(ItemStack var1, World var2, EntityHuman var3) {
		int var4 = 1;
		if (chargeLevel(var1) > 2) var4++;
		if (chargeLevel(var1) > 4) var4++;
		if (chargeLevel(var1) > 6) var4++;
		var2.makeSound(var3, "wall", 1.0F, 1.0F);
		var2.addEntity(new EntityHyperkinesis(var2, var3, (chargeLevel(var1) + 1) / 2, var4));
	}

	public void doBreak2(ItemStack item, World world, EntityHuman human) {
		if (getCooldown(item) > 0) return;
		initCooldown(item);
		
		world.makeSound(human, "destruct", 0.5F, 1.0F);
		human.C_();
		cleanDroplist(item);
		boolean var4 = true;
		double dir = EEBase.direction(human);
		int x = (int) EEBase.playerX(human);
		int y = (int) (EEBase.playerY(human) + human.getHeadHeight());
		int z = (int) EEBase.playerZ(human);
		
		int charge = chargeLevel(item) + 1;
		
		if (dir == 0.0D) {
			for (int var10 = -2; var10 >= -(charge * charge) - 1; var10--) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1) {
							if (var10 == -(charge * charge) - 1 && var11 == 1 && var12 == 1) {
								ConsumeReagent(item, human, var4);
								var4 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var11, y + var10, z + var12);
					}

				}

			}

		} else if (dir == 1.0D) {
			for (int var10 = 2; var10 <= charge * charge + 1; var10++) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1) if (var10 == charge * charge + 1 && var11 == 1 && var12 == 1) {
							ConsumeReagent(item, human, var4);
							var4 = false;
						} else {
							ConsumeReagent(item, human, false);
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var11, y + var10, z + var12);
					}

				}

			}

		} else if (dir == 2D) {
			for (int var10 = 1; var10 <= charge * charge; var10++) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1) if (var10 == charge * charge && var11 == 1 && var12 == 1) {
							ConsumeReagent(item, human, var4);
							var4 = false;
						} else {
							ConsumeReagent(item, human, false);
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var11, y + var12, z + var10);
					}

				}

			}

		} else if (dir == 3D) {
			for (int var10 = -1; var10 >= -(charge * charge); var10--) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1) if (var10 == -(charge * charge) && var11 == 1 && var12 == 1) {
							ConsumeReagent(item, human, var4);
							var4 = false;
						} else {
							ConsumeReagent(item, human, false);
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var10, y + var12, z + var11);
					}

				}

			}

		} else if (dir == 4D) {
			for (int var10 = -1; var10 >= -(charge * charge); var10--) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1) if (var10 == -(charge * charge) && var11 == 1 && var12 == 1) {
							ConsumeReagent(item, human, var4);
							var4 = false;
						} else {
							ConsumeReagent(item, human, false);
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var11, y + var12, z + var10);
					}

				}

			}

		} else if (dir == 5D) {
			for (int var10 = 1; var10 <= charge * charge; var10++) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1) if (var10 == charge * charge && var11 == 1 && var12 == 1) {
							ConsumeReagent(item, human, var4);
							var4 = false;
						} else {
							ConsumeReagent(item, human, false);
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var10, y + var12, z + var11);
					}

				}

			}

		}
		ejectDropList(world, item, human.locX, human.locY, human.locZ);
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int var7) {
		if (EEProxy.isClient(world)) return false;
		if (getCooldown(item) > 0) return false;
		//if (human.getBukkitEntity().hasPermission("eepatch.delay") && delay > System.currentTimeMillis()){
		//	return false;
		//}
		//delay = System.currentTimeMillis()+1000*5;
		if (EEEventManager.callEvent(new EEHyperCatalystEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;
		
		initCooldown(item);
		world.makeSound(human, "destruct", 0.5F, 1.0F);
		cleanDroplist(item);
		boolean var8 = true;
		int nx = x;
		int ny = y;
		int nz = z;
		int charge = (chargeLevel(item) + 1);
		if (var7 == 0) {
			for (int var12 = 0; var12 < charge * charge; var12++) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1) {
							if (var12 == charge * charge - 1 && var13 == 1 && var14 == 1) {
								ConsumeReagent(item, human, var8);
								var8 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var13, ny + var12, nz + var14);
					}

				}

			}

		} else if (var7 == 1) {
			for (int var12 = 0; var12 > -(charge * charge); var12--) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1) {
							if (var12 == -(charge * charge) + 1 && var13 == 1 && var14 == 1) {
								ConsumeReagent(item, human, var8);
								var8 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var13, ny + var12, nz + var14);
					}

				}

			}

		} else if (var7 == 2) {
			for (int var12 = 0; var12 < charge * charge; var12++) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1) {
							if (var12 == charge * charge - 1 && var13 == 1 && var14 == 1) {
								ConsumeReagent(item, human, var8);
								var8 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var13, ny + var14, nz + var12);
					}

				}

			}

		} else if (var7 == 3) {
			for (int var12 = 0; var12 > -(charge * charge); var12--) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1) {
							if (var12 == -(charge * charge) + 1 && var13 == 1 && var14 == 1) {
								ConsumeReagent(item, human, var8);
								var8 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var13, ny + var14, nz + var12);
					}

				}

			}

		} else if (var7 == 4) {
			for (int var12 = 0; var12 < charge * charge; var12++) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1) {
							if (var12 == charge * charge - 1 && var13 == 1 && var14 == 1) {
								ConsumeReagent(item, human, var8);
								var8 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var12, ny + var14, nz + var13);
					}

				}

			}

		} else if (var7 == 5) {
			for (int var12 = 0; var12 > -(charge * charge); var12--) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1) {
							if (var12 == -(charge * charge) + 1 && var14 == 1 && var13 == 1) {
								ConsumeReagent(item, human, var8);
								var8 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var12, ny + var14, nz + var13);
					}

				}

			}

		}
		ejectDropList(world, item, x, y, z);
		return true;
	}

	public void breakBlock(ItemStack var1, EntityHuman human, int x, int y, int z) {
		World world = human.world;
		int id = world.getTypeId(x, y, z);
		int data = world.getData(x, y, z);
		if (id == 0 || Block.byId[id] == null || Block.byId[id].getHardness(data) < 0.0F || Block.byId[id].getHardness(data) > 10F) return;
		
		if (!attemptBreak(human, x, y, z)) return;
		
		ArrayList<ItemStack> var8 = Block.byId[id].getBlockDropped(world, x, y, z, data, 0);
		ItemStack var10;
		for (Iterator<ItemStack> var9 = var8.iterator(); var9.hasNext(); addToDroplist(var1, var10))
			var10 = var9.next();

		setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);
		world.setTypeId(x, y, z, 0);
		if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y, z, 0.0D, 0.0D, 0.0D);
		if (world.random.nextInt(8) == 0) world.a("explode", x, y, z, 0.0D, 0.0D, 0.0D);
	}

	//private long delay2 = 0;
	public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) {
			return var1;
		} else {
			if (EEEventManager.callEvent(new EEHyperCatalystEvent(var1, EEAction.RIGHTCLICK, var3, EEAction2.BreakRadius))) return var1;
			//if (!var3.getBukkitEntity().hasPermission("eepatch.delay") || delay2 <= System.currentTimeMillis()){
				doBreak2(var1, var2, var3);
			//	delay2 = System.currentTimeMillis()+1000*5;
			//}
			
			return var1;
		}
	}

	public void doToggle(ItemStack itemstack1, World world1, EntityHuman entityhuman1) {}

	//private long delay = 0;
	public void doRelease(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return;
		
		if (EEEventManager.callEvent(new EEHyperCatalystEvent(item, EEAction.RELEASE, human, EEAction2.BreakRadius))) return;
		//if (!human.getBukkitEntity().hasPermission("eepatch.delay") || delay <= System.currentTimeMillis()){
			doBreak(item, world, human);
		//	delay = System.currentTimeMillis()+1000*5;
		//}
	}

	public void doPassive(ItemStack var1, World var2, EntityHuman var3) {
		decCooldown(var1);
	}

	public void setCooldown(ItemStack var1, int var2) {
		setInteger(var1, "cooldown", var2);
	}

	public int getCooldown(ItemStack var1) {
		return getInteger(var1, "cooldown");
	}

	public void decCooldown(ItemStack var1) {
		setCooldown(var1, getCooldown(var1) - 1);
	}

	public void initCooldown(ItemStack var1) {
		setCooldown(var1, 5);
	}
}