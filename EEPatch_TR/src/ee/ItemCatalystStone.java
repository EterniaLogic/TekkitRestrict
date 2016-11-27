package ee;

import java.util.*;

import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.destruction.EEDestructionCatalystEvent;
import net.minecraft.server.*;

public class ItemCatalystStone extends ItemEECharged {

	public ItemCatalystStone(int var1) {
		super(var1, 3);
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) {
			return item;
		} else {
			doRelease(item, world, human);
			return item;
		}
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int var7) {
		if (EEProxy.isClient(world)) return false;
		if (getCooldown(item) > 0) return false;

		if (EEEventManager.callEvent(new EEDestructionCatalystEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;

		initCooldown(item);
		world.makeSound(human, "destruct", 0.5F, 1.0F);
		cleanDroplist(item);
		// boolean var8 = true;
		int nx = x;
		int ny = y;
		int nz = z;

		int charge = chargeLevel(item) + 1;
		if (var7 == 0) {
			for (int var12 = 0; var12 < charge * charge; var12++) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1)
							// if(var12 == (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) - 1 && var13 == 1 && var14 == 1)
							// {
							// ConsumeReagent(var1, var2, false);
							// var8 = false;
							// } else
							// {
							ConsumeReagent(item, human, false);
						// }
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var13, ny + var12, nz + var14);
					}
				}
			}
		} else if (var7 == 1) {
			for (int var12 = 0; var12 > -(charge * charge); var12--) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1)
							// if(var12 == -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) + 1 && var13 == 1 && var14 == 1)
							// {
							// ConsumeReagent(var1, var2, false);
							// var8 = false;
							// } else
							// {
							ConsumeReagent(item, human, false);
						// }
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var13, ny + var12, nz + var14);
					}
				}
			}
		} else if (var7 == 2) {
			for (int var12 = 0; var12 < charge * charge; var12++) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1)
							// if(var12 == (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) - 1 && var13 == 1 && var14 == 1)
							// {
							ConsumeReagent(item, human, false);
						// var8 = false;
						// } else
						// {
						// ConsumeReagent(var1, var2, false);
						// }
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var13, ny + var14, nz + var12);
					}
				}
			}
		} else if (var7 == 3) {
			for (int var12 = 0; var12 > -(charge * charge); var12--) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1)
							// if(var12 == -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) + 1 && var13 == 1 && var14 == 1)
							// {
							// ConsumeReagent(var1, var2, false);
							// var8 = false;
							// } else
							// {
							ConsumeReagent(item, human, false);
						// }
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var13, ny + var14, nz + var12);
					}
				}
			}
		} else if (var7 == 4) {
			for (int var12 = 0; var12 < charge * charge; var12++) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1)
							// if(var12 == (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) - 1 && var13 == 1 && var14 == 1)
							// {
							ConsumeReagent(item, human, false);
						// var8 = false;
						// } else
						// {
						// ConsumeReagent(var1, var2, false);
						// }
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var12, ny + var14, nz + var13);
					}
				}
			}
		} else if (var7 == 5) {
			for (int var12 = 0; var12 > -(charge * charge); var12--) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var14 = -1; var14 <= 1; var14++) {
						if (getFuelRemaining(item) < 1)
							// if(var12 == -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) + 1 && var14 == 1 && var13 == 1)
							// {
							// ConsumeReagent(var1, var2, false);
							// var8 = false;
							// } else
							// {
							ConsumeReagent(item, human, false);
						// }
						if (getFuelRemaining(item) > 0) breakBlock(item, human, nx + var12, ny + var14, nz + var13);
					}
				}
			}
		}
		ejectDropList(world, item, x, y, z);
		return true;
	}

	public void breakBlock(ItemStack item, EntityHuman human, int x, int y, int z) {
		World world = human.world;
		int id = world.getTypeId(x, y, z);
		int data = world.getData(x, y, z);
		if (id == 0 || Block.byId[id] == null || id == 194) return;

		float hardness = Block.byId[id].getHardness(data);
		if (hardness < 0.0F || hardness > 10F) return;

		if (!attemptBreak(human, x, y, z)) return;
		ArrayList<ItemStack> var8 = Block.byId[id].getBlockDropped(world, x, y, z, data, 0);
		Iterator<ItemStack> var9 = var8.iterator();

		while (var9.hasNext())
		{
			addToDroplist(item, var9.next());
		}

		setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);
		world.setTypeId(x, y, z, 0);
		if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y, z, 0.0D, 0.0D, 0.0D);
		if (world.random.nextInt(8) == 0) world.a("explode", x, y, z, 0.0D, 0.0D, 0.0D);

	}

	public void doPassive(ItemStack item, World world, EntityHuman human) {
		decCooldown(item);
	}

	public void doActive(ItemStack itemstack, World world, EntityHuman human) {}

	public void doHeld(ItemStack itemstack, World world, EntityHuman human) {}

	public void doRelease(ItemStack item, World world, EntityHuman human) {
		if (getCooldown(item) > 0) return;

		if (EEEventManager.callEvent(new EEDestructionCatalystEvent(item, EEAction.RELEASE, human, EEAction2.BreakRadius))) return;

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
						if (getFuelRemaining(item) < 1) {
							if (var10 == charge * charge + 1 && var11 == 1 && var12 == 1) {
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
		} else if (dir == 2D) {
			for (int var10 = 1; var10 <= charge * charge; var10++) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1) {
							if (var10 == charge * charge && var11 == 1 && var12 == 1) {
								ConsumeReagent(item, human, false);
								var4 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var11, y + var12, z + var10);
					}
				}
			}
		} else if (dir == 3D) {
			for (int var10 = -1; var10 >= -(charge * charge); var10--) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1) {
							if (var10 == -(charge * charge) && var11 == 1 && var12 == 1) {
								ConsumeReagent(item, human, false);
								var4 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var10, y + var12, z + var11);
					}
				}
			}
		} else if (dir == 4D) {
			for (int var10 = -1; var10 >= -(charge * charge); var10--) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1){
							if (var10 == -(charge * charge) && var11 == 1 && var12 == 1) {
								ConsumeReagent(item, human, false);
								var4 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var11, y + var12, z + var10);
					}
				}
			}
		} else if (dir == 5D) {
			for (int var10 = 1; var10 <= charge * charge; var10++) {
				for (int var11 = -1; var11 <= 1; var11++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (getFuelRemaining(item) < 1){
							if (var10 == charge * charge && var11 == 1 && var12 == 1) {
								ConsumeReagent(item, human, false);
								var4 = false;
							} else {
								ConsumeReagent(item, human, false);
							}
						}
						if (getFuelRemaining(item) > 0) breakBlock(item, human, x + var10, y + var12, z + var11);
					}
				}
			}
		}
		ejectDropList(world, item, human.locX, human.locY, human.locZ);
	}

	public void doAlternate(ItemStack item, World world, EntityHuman human) {}

	public void doLeftClick(ItemStack item, World world, EntityHuman human) {}

	public boolean canActivate() {
		return false;
	}

	public void doToggle(ItemStack item, World world, EntityHuman human) {}

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