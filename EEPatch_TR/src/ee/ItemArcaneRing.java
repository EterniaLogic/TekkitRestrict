package ee;

import java.util.Iterator;
import java.util.List;

import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EERingAction;
import ee.events.ring.EEArcaneRingEvent;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.BlockFlower;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntitySnowball;
import net.minecraft.server.EntityWeatherLighting;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.World;
import net.minecraft.server.WorldGenBigTree;
import net.minecraft.server.WorldGenForest;
import net.minecraft.server.WorldGenTaiga2;
import net.minecraft.server.WorldGenTrees;
import net.minecraft.server.WorldGenerator;

public class ItemArcaneRing extends ItemEECharged {
	@SuppressWarnings("unused")
	private boolean initialized;

	public ItemArcaneRing(int var1) {
		super(var1, 0);
	}

	public void doGale(ItemStack unused, World world, EntityHuman player) {
		world.makeSound(player, "gust", 0.6F, 1.0F);
		world.addEntity(new EntityWindEssence(world, player));
	}

	public void doInterdiction(ItemStack var1, World var2, EntityHuman var3) {
		List<EntityMonster> var4 = var2.a(EntityMonster.class, AxisAlignedBB.b((float) var3.locX - 5.0F, var3.locY - 5.0D, (float) var3.locZ - 5.0F,
				(float) var3.locX + 5.0F, var3.locY + 5.0D, (float) var3.locZ + 5.0F));
		Iterator<EntityMonster> var6 = var4.iterator();

		while (var6.hasNext()) {
			Entity var5 = var6.next();
			PushEntities(var5, var3);
		}

		List<EntityArrow> var11 = var2.a(EntityArrow.class, AxisAlignedBB.b((float) var3.locX - 5.0F, var3.locY - 5.0D, (float) var3.locZ - 5.0F,
				(float) var3.locX + 5.0F, var3.locY + 5.0D, (float) var3.locZ + 5.0F));
		Iterator<EntityArrow> var8 = var11.iterator();

		while (var8.hasNext()) {
			Entity var7 = var8.next();
			PushEntities(var7, var3);
		}

		List<EntityFireball> var12 = var2.a(EntityFireball.class, AxisAlignedBB.b((float) var3.locX - 5.0F, var3.locY - 5.0D, (float) var3.locZ - 5.0F,
				(float) var3.locX + 5.0F, var3.locY + 5.0D, (float) var3.locZ + 5.0F));
		Iterator<EntityFireball> var10 = var12.iterator();

		while (var10.hasNext()) {
			Entity var9 = var10.next();
			PushEntities(var9, var3);
		}

		if (!EEProxy.getWorldInfo(var2).isThundering()) {
			EEProxy.getWorldInfo(var2).setThundering(true);
			EEProxy.getWorldInfo(var2).setThunderDuration(300);
		}
	}

	public void doThunder(ItemStack var1, World world, EntityHuman human) {
		List<EntityMonster> var4 = world.a(EntityMonster.class, AxisAlignedBB.b((float) human.locX - 5.0F, human.locY - 5.0D, (float) human.locZ - 5.0F,
				(float) human.locX + 5.0F, human.locY + 5.0D, (float) human.locZ + 5.0F));
		Iterator<EntityMonster> var6 = var4.iterator();

		while (var6.hasNext()) {
			Entity var5 = var6.next();
			doBolt(var5, var1, human);
		}
	}

	private void doBolt(Entity var1, ItemStack ring, EntityHuman human) {
		if ((getThunderCooldown(ring) <= 0) && (human.world.isChunkLoaded((int) var1.locX, (int) var1.locY, (int) var1.locZ))) {
			human.world.strikeLightning(new EntityWeatherLighting(human.world, var1.locX, var1.locY, var1.locZ));
			resetThunderCooldown(ring);
		}
	}

	private int getThunderCooldown(ItemStack var1) {
		return getShort(var1, "thunderCooldown");
	}
	/** Lower the thunder cooldown of the given ring by 1.*/
	private void decThunderCooldown(ItemStack var1) {
		int old = getThunderCooldown(var1);
		setShort(var1, "thunderCooldown", old <= 0 ? 0 : old - 1);
	}
	/** Set the thunder cooldown for the given ring to 20 (ticks?)*/
	private void resetThunderCooldown(ItemStack var1) {
		setShort(var1, "thunderCooldown", 20);
	}

	private void PushEntities(Entity mob, EntityHuman human) {
		if ((mob instanceof EntityHuman)) return; //Dont push other players

		if ((human.world.random.nextInt(1200) == 0) && (human.world.isChunkLoaded((int) mob.locX, (int) mob.locY, (int) mob.locZ))) {
			human.world.strikeLightning(new EntityWeatherLighting(human.world, mob.locX, mob.locY, mob.locZ));
		}

		double var4 = human.locX + 0.5D - mob.locX;
		double var6 = human.locY + 0.5D - mob.locY;
		double var8 = human.locZ + 0.5D - mob.locZ;
		double var10 = var4 * var4 + var6 * var6 + var8 * var8;
		var10 *= var10;

		if (var10 <= 1296d) {
			double var12 = -(var4 * 0.01999999955296516D / var10) * 216d;
			double var14 = -(var6 * 0.01999999955296516D / var10) * 216d;
			double var16 = -(var8 * 0.01999999955296516D / var10) * 216d;

			if (var12 > 0.0D) {
				var12 = 0.12D;
			} else if (var12 < 0.0D) {
				var12 = -0.12D;
			}

			if (var14 > 0.2D) {
				var14 = 0.12D;
			} else if (var14 < -0.1D) {
				var14 = 0.12D;
			}

			if (var16 > 0.0D) {
				var16 = 0.12D;
			} else if (var16 < 0.0D) {
				var16 = -0.12D;
			}

			mob.motX += var12;
			mob.motY += var14;
			mob.motZ += var16;
		}
	}

	public boolean ConsumeReagentBonemeal(EntityHuman human, boolean var2) {
		return EEBase.Consume(new ItemStack(Item.INK_SACK, 4, 15), human, var2);
	}
	public boolean ConsumeReagentSapling(ItemStack var1, EntityHuman var2, boolean var3) {
		if (EEBase.Consume(new ItemStack(Block.SAPLING, 1, 0), var2, var3)) {
			tempMeta(var1, 0);
			return true;
		}
		if (EEBase.Consume(new ItemStack(Block.SAPLING, 1, 1), var2, var3)) {
			tempMeta(var1, 1);
			return true;
		}
		if (EEBase.Consume(new ItemStack(Block.SAPLING, 1, 2), var2, var3)) {
			tempMeta(var1, 2);
			return true;
		}

		return false;
	}

	private void tempMeta(ItemStack var1, int var2) {
		((ItemEECharged) var1.getItem()).setInteger(var1, "tempMeta", var2);
	}

	public void doPassiveHarvest(ItemStack var1, World world, EntityHuman human) {
		for (int var4 = -1; var4 <= 1; var4++) {
			for (int var5 = -1; var5 <= 1; var5++) {
				int x = (int) EEBase.playerX(human) + var4;
				int y = (int) EEBase.playerY(human) - 1;
				int z = (int) EEBase.playerZ(human) + var5;
				if (world.getTypeId(x, y, z) == Block.FIRE.id && attemptBreak(human, x, y, z)) {
					world.setTypeId(x, y, z, 0);
				}
			}
		}

		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);

		for (int var7 = -5; var7 <= 5; var7++) {
			for (int var8 = -5; var8 <= 5; var8++) {
				for (int var9 = -5; var9 <= 5; var9++) {
					int nx = x + var7;
					int ny = y + var8;
					int nz = z + var9;
					int id = world.getTypeId(nx, ny, nz);

					if (id == Block.CROPS.id) {
						int data = world.getData(nx, ny, nz);

						if ((data < 7 && world.random.nextInt(600) == 0) && attemptBreak(human, nx, ny, nz)) {
							data++;
							world.setData(nx, ny, nz, data);
						}
					} else if ((id != BlockFlower.YELLOW_FLOWER.id) && (id != BlockFlower.RED_ROSE.id) && (id != BlockFlower.BROWN_MUSHROOM.id)
							&& (id != BlockFlower.RED_MUSHROOM.id)) {
						
						if ((id == Block.GRASS.id && world.getTypeId(nx, ny + 1, nz) == 0 && world.random.nextInt(4000) == 0) && attemptPlace(human, nx, ny + 1, nz)) {
							world.setTypeId(nx, ny + 1, nz, BlockFlower.LONG_GRASS.id);
							world.setData(nx, ny + 1, nz, 1);
						}

						else if ((id == Block.DIRT.id && world.getTypeId(nx, ny + 1, nz) == 0 && world.random.nextInt(800) == 0) && attemptPlace(human, nx, ny, nz)) {
							world.setTypeId(nx, ny, nz, Block.GRASS.id);
						} else if (((id == Block.SUGAR_CANE_BLOCK.id || id == Block.CACTUS.id)
								&& world.getTypeId(nx, ny + 1, nz) == 0
								&& world.getTypeId(nx, ny - 4, nz) != Block.SUGAR_CANE_BLOCK.id
								&& world.getTypeId(nx, ny - 4, nz) != Block.CACTUS.id && world.random.nextInt(600) == 0)
								&& attemptBreak(human, nx, ny + 1, nz)) {
							world.setTypeId(nx, ny + 1, nz, id);
							world.a("largesmoke", nx, ny, nz, 0.0D, 0.05D, 0.0D);
						}
					} else if (world.random.nextInt(2) == 0) {
						for (int var11 = -1; var11 < 0; var11++) {
							if ((world.getTypeId(nx + var11, ny, nz) == 0 && world.getTypeId(nx + var11, ny - 1, nz) == Block.GRASS.id)) {
								if (world.random.nextInt(800) == 0 && attemptPlace(human, nx + var11, ny, nz)) {
									world.setTypeId(nx + var11, ny, nz, id);
									world.a("largesmoke", nx + var11, ny, nz, 0.0D, 0.05D, 0.0D);
								}
							} else if (((world.getTypeId(nx, ny, nz + var11) == 0)
									&& (world.getTypeId(nx, ny - 1, nz + var11) == Block.GRASS.id) && (world.random.nextInt(1800) == 0))
									&& attemptPlace(human, nx, ny, nz + var11)) {
								world.setTypeId(nx, ny, nz + var11, id);
								world.a("largesmoke", nx, ny, nz + var11, 0.0D, 0.05D, 0.0D);
							}
						}
					}
				}
			}
		}
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int var7) {
		if (EEProxy.isClient(world)) return false;
		
		if (EEEventManager.callEvent(new EEArcaneRingEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EERingAction.Fertilize))) return false;

		int id = world.getTypeId(x, y, z);
		if (id == 60) {
			for (int var14 = -5; var14 <= 5; var14++) {
				for (int var9 = -5; var9 <= 5; var9++) {
					int nx = var14+x;
					int nz = var9+z;
					int var10 = world.getTypeId(nx, y, nz);
					int var11 = world.getTypeId(nx, y + 1, nz);

					if ((var10 == 60 && var11 == 0 && ConsumeReagentSeeds(human, false)) && attemptBreak(human, nx, y + 1, nz)) {
						world.setTypeId(nx, y + 1, nz, Block.CROPS.id);
						world.a("largesmoke", nx, y, nz, 0.0D, 0.05D, 0.0D);
					}
				}
			}

			return true;
		}

		if (id == 12) {
			double direction = EEBase.direction(human);

			if (direction == 5.0D) x += 5;
			else if (direction == 4.0D) z -= 5;
			else if (direction == 3.0D) x -= 5;
			else if (direction == 2.0D) z += 5;

			for (int var10 = -5; var10 <= 5; var10++) {
				for (int var11 = -5; var11 <= 5; var11++) {
					int var12 = world.getTypeId(var10 + x, y, var11 + z);
					int var13 = world.getTypeId(var10 + x, y + 1, var11 + z);

					if (((var12 == 12) && (var13 == 0) && (var10 % 5 == 0) && (var11 % 5 == 0) && (ConsumeReagentCactus(human, false)))
							&& attemptBreak(human, var10 + x, y + 1, var11 + z)) {
						world.setTypeId(var10 + x, y + 1, var11 + z, Block.CACTUS.id);
						world.a("largesmoke", var10 + x, y, var11 + z, 0.0D, 0.05D, 0.0D);
					}
				}
			}

			return true;
		}

		boolean var8 = false;

		if (id != Block.DIRT.id && id != Block.GRASS.id && id != BlockFlower.LONG_GRASS.id) return false;
		
		if (id == BlockFlower.LONG_GRASS.id && attemptBreak(human, x, y, z)) {
			Block.byId[BlockFlower.LONG_GRASS.id].dropNaturally(world, x, y, z, 1, 1.0F, 1);
			world.setTypeId(x, y, z, 0);
			y--;
		}

		if (world.getMaterial(x + 1, y, z) == Material.WATER || world.getMaterial(x - 1, y, z) == Material.WATER
				|| world.getMaterial(x, y, z + 1) == Material.WATER || world.getMaterial(x, y, z - 1) == Material.WATER) {
			var8 = true;
		}

		for (int var9 = -8; var9 <= 8; var9++) {
			for (int var10 = -8; var10 <= 8; var10++) {
				int var11 = world.getTypeId(var9 + x, y, var10 + z);
				int var12 = world.getTypeId(var9 + x, y + 1, var10 + z);

				if (var8) {
					if (((world.getMaterial(var9 + x + 1, y, var10 + z) == Material.WATER)
						|| (world.getMaterial(var9 + x - 1, y, var10 + z) == Material.WATER)
						|| (world.getMaterial(var9 + x, y, var10 + z + 1) == Material.WATER)
						|| (world.getMaterial(var9 + x, y, var10 + z - 1) == Material.WATER))
						&& (var11 == Block.DIRT.id || var11 == Block.GRASS.id)
						&& var12 == 0
						&& ConsumeReagentReed(human, false)
						&& attemptBreak(human, var9 + x, y + 1, var10 + z)) {
						world.setTypeId(var9 + x, y + 1, var10 + z, Block.SUGAR_CANE_BLOCK.id);
					}
				} else if (((var11 == Block.DIRT.id) || (var11 == Block.GRASS.id)) && ((var12 == 0) || (var12 == BlockFlower.LONG_GRASS.id)) && (var9 % 4 == 0)
						&& (var10 % 4 == 0) && (ConsumeReagentSapling(item, human, false))
						&& attemptBreak(human, var9 + x, y, var10 + z)) {
					if (var12 == BlockFlower.LONG_GRASS.id) {
						Block.byId[var12].dropNaturally(world, var9 + x, y + 1, var10 + z, 1, 1.0F, 1);
						world.setTypeId(var9 + x, y + 1, var10 + z, 0);
					}

					world.setTypeIdAndData(var9 + x, y + 1, var10 + z, Block.SAPLING.id, getTempMeta(item));
					world.a("largesmoke", var9 + x, y, var10 + z, 0.0D, 0.05D, 0.0D);
				}
			}
		}

		return true;
	}

	private int getTempMeta(ItemStack var1) {
		return ((ItemEECharged) var1.getItem()).getInteger(var1, "tempMeta");
	}

	public void doActiveHarvest(ItemStack var1, World world, EntityHuman human) {
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);

		for (int var7 = -5; var7 <= 5; var7++) {
			for (int var8 = -5; var8 <= 5; var8++) {
				for (int var9 = -5; var9 <= 5; var9++) {
					int nx = x + var7;
					int ny = y + var8;
					int nz = z + var9;
					int id = world.getTypeId(nx, ny, nz);
					int data = world.getData(nx, ny, nz);
					
					if (id == Block.CROPS.id) {
						if (data >= 7) {
							if (attemptBreak(human, nx, ny, nz)) {
								Block.byId[id].dropNaturally(world, nx, ny, nz, data, 0.05F, 1);
								Block.byId[id].dropNaturally(world, nx, ny, nz, data, 1.0F, 1);
								world.setTypeId(nx, ny, nz, 0);
								world.a("largesmoke", nx, ny, nz, 0.0D, 0.05D, 0.0D);
							}
						} else if (world.random.nextInt(400) == 0 && attemptBreak(human, nx, ny, nz)) {
							data++;
							world.setData(nx, ny, nz, data);
						}
					} else if (id != BlockFlower.YELLOW_FLOWER.id && id != BlockFlower.RED_ROSE.id &&
							id != BlockFlower.BROWN_MUSHROOM.id && id != BlockFlower.RED_MUSHROOM.id &&
							id != BlockFlower.LONG_GRASS.id) {
						if ((id == Block.SUGAR_CANE_BLOCK.id && world.getTypeId(nx, ny - 4, nz) == Block.SUGAR_CANE_BLOCK.id)
								|| (id == Block.CACTUS.id && world.getTypeId(nx, ny - 4, nz) == Block.CACTUS.id)) {
							if (id == Block.SUGAR_CANE_BLOCK.id) {
								if (attemptBreak(human, nx, ny - 3, nz)) {
									Block.byId[id].dropNaturally(world, nx, ny - 3, nz, data, 0.25F, 1);
									Block.byId[id].dropNaturally(world, nx, ny - 3, nz, data, 1.0F, 1);
									world.setTypeId(nx, ny - 3, nz, 0);
								}
							} else {
								if (attemptBreak(human, nx, ny - 4, nz)) {
									Block.byId[id].dropNaturally(world, nx, ny - 4, nz, data, 0.25F, 1);
									Block.byId[id].dropNaturally(world, nx, ny - 4, nz, data, 1.0F, 1);
									world.setTypeId(nx, ny - 4, nz, 0);
								}
							}

							world.a("largesmoke", nx, ny - 3, nz, 0.0D, 0.05D, 0.0D);
						}
					} else {
						if (attemptBreak(human, nx, ny, nz)) {
							Block.byId[id].dropNaturally(world, nx, ny, nz, data, 0.05F, 1);
							Block.byId[id].dropNaturally(world, nx, ny, nz, data, 1.0F, 1);
							world.setTypeId(nx, ny, nz, 0);
							world.a("largesmoke", nx, ny, nz, 0.0D, 0.05D, 0.0D);
						}
					}
				}
			}
		}
	}

	public boolean ConsumeReagentCactus(EntityHuman human, boolean var2) {
		return EEBase.Consume(new ItemStack(Block.CACTUS, 1), human, var2);
	}
	public boolean ConsumeReagentSeeds(EntityHuman human, boolean var2) {
		return EEBase.Consume(new ItemStack(Item.SEEDS, 1), human, var2);
	}
	public boolean ConsumeReagentReed(EntityHuman human, boolean var2) {
		return EEBase.Consume(new ItemStack(Item.SUGAR_CANE, 1), human, var2);
	}

	public void doFertilize(ItemStack var1, World world, EntityHuman human) {
		boolean var4 = false;
		boolean var5 = true;
		boolean var6 = true;
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);

		for (int var10 = -15; var10 <= 15; var10++) {
			for (int var11 = -15; var11 <= 15; var11++) {
				for (int var12 = -15; var12 <= 15; var12++) {
					int nx = x + var10;
					int ny = y + var11;
					int nz = z + var12;
					int id = world.getTypeId(nx, ny, nz);

					if ((3 >= var10) && (var10 >= -3) && (3 >= var11) && (var11 >= -3) && (3 >= var12) && (var12 >= -3)) {
						if (id == Block.CROPS.id) {
							int data = world.getData(nx, ny, nz);

							if (data < 7) {
								if (!var4) {
									if (attemptBreak(human, nx, ny, nz)) {
										if (ConsumeReagentBonemeal(human, var5)) {
											if (var6) {
												world.makeSound(human, "flash", 0.7F, 1.0F);
												var6 = false;
											}

											var4 = true;
											data++;
											world.a("largesmoke", nx, ny, nz, 0.0D, 0.05D, 0.0D);
											world.setData(nx, ny, nz, data);
										}

										var5 = false;
									}
								} else {
									if (attemptBreak(human, nx, ny, nz)) {
										if (var6) {
											world.makeSound(human, "flash", 0.7F, 1.0F);
											var6 = false;
										}

										data++;
										world.a("largesmoke", nx, ny, nz, 0.0D, 0.05D, 0.0D);
										world.setData(nx, ny, nz, data);
									}
								}
							}
						} else if ((id == Block.SUGAR_CANE_BLOCK.id)
								&& (world.getTypeId(nx, ny - 4, nz) != Block.SUGAR_CANE_BLOCK.id)
								&& (world.getTypeId(nx, ny + 1, nz) == 0)) {
							if (!var4) {
								if (attemptPlace(human, nx, ny + 1, nz)) {
									if (ConsumeReagentBonemeal(human, var5)) {
										if (var6) {
											world.makeSound(human, "flash", 0.7F, 1.0F);
											var6 = false;
										}

										var4 = true;
										world.setTypeId(nx, ny + 1, nz, Block.SUGAR_CANE_BLOCK.id);
									}

									var5 = false;
								}
							} else {
								if (attemptPlace(human, nx, ny + 1, nz)) {
									if (var6) {
										world.makeSound(human, "flash", 0.7F, 1.0F);
										var6 = false;
									}

									world.setTypeId(nx, ny + 1, nz, Block.SUGAR_CANE_BLOCK.id);
								}
							}
						} else if ((id == Block.CACTUS.id) && (world.getTypeId(nx, ny - 4, nz) != Block.CACTUS.id)
								&& (world.getTypeId(nx, ny + 1, nz) == 0)) {
							if (!var4) {
								if (attemptPlace(human, nx, ny + 1, nz)) {
									if (ConsumeReagentBonemeal(human, var5)) {
										if (var6) {
											world.makeSound(human, "flash", 0.7F, 1.0F);
											var6 = false;
										}

										var4 = true;
										world.setTypeId(nx, ny + 1, nz, Block.CACTUS.id);
									}

									var5 = false;
								}
							} else {
								if (attemptPlace(human, nx, ny + 1, nz)) {
									if (var6) {
										world.makeSound(human, "flash", 0.7F, 1.0F);
										var6 = false;
									}

									world.setTypeId(nx, ny + 1, nz, Block.CACTUS.id);
								}
							}
						}

						if ((id == BlockFlower.RED_ROSE.id) || (id == BlockFlower.YELLOW_FLOWER.id) || (id == BlockFlower.BROWN_MUSHROOM.id) || (id == BlockFlower.RED_MUSHROOM.id)) {
							for (int var14 = -1; var14 <= 0; var14++) {
								if (world.getTypeId(nx + var14, ny, nz) == 0) {
									if (!var4) {
										if (attemptPlace(human, nx + var14, ny, nz)) {
											if (ConsumeReagentBonemeal(human, var5)) {
												if (var6) {
													world.makeSound(human, "flash", 0.7F, 1.0F);
													var6 = false;
												}

												var4 = true;
												world.setTypeId(nx + var14, ny, nz, id);
											}

										}
										
										var5 = false;
									} else {
										if (attemptPlace(human, nx + var14, ny, nz)) {
											if (var6) {
												world.makeSound(human, "flash", 0.7F, 1.0F);
												var6 = false;
											}

											world.setTypeId(nx + var14, ny, nz, id);
										}
									}
								} else if (world.getTypeId(nx, ny, nz + var14) == 0) {
									if (var4) {
										if (var6) {
											world.makeSound(human, "flash", 0.7F, 1.0F);
											var6 = false;
										}

										if (attemptPlace(human, nx, ny, nz + var14))
											world.setTypeId(nx, ny, nz + var14, id);
										break;
									}

									if (ConsumeReagentBonemeal(human, var5) && attemptPlace(human, nx, ny, nz + var14)) {
										if (var6) {
											world.makeSound(human, "flash", 0.7F, 1.0F);
											var6 = false;
										}

										var4 = true;
										world.setTypeId(nx, ny, nz + var14, id);
									}

									var5 = false;
								}
							}
						}
					}

					if (id == Block.SAPLING.id) {
						if (!var4) {
							if (ConsumeReagentBonemeal(human, var5)) {
								if (var6) {
									world.makeSound(human, "flash", 0.7F, 1.0F);
									var6 = false;
								}

								var4 = true;

								if (world.random.nextInt(100) < 25 && attemptBreak(human, nx, ny, nz)) {
									int var14 = world.getData(nx, ny, nz) & 0x3;
									world.setRawTypeId(nx, ny, nz, 0);
									WorldGenerator var15 = null;

									if (var14 == 1)
										var15 = new WorldGenTaiga2(true);
									else if (var14 == 2)
										var15 = new WorldGenForest(true);
									else {
										if (world.random.nextInt(10) == 0)
											var15 = new WorldGenBigTree(true);
										else
											var15 = new WorldGenTrees(true);
									}

									world.a("largesmoke", nx, ny, nz, 0.0D, 0.05D, 0.0D);

									if (!var15.a(world, world.random, nx, ny, nz)) {
										world.setRawTypeIdAndData(nx, ny, nz, Block.SAPLING.id, var14);
									}
								}
							}

							var5 = false;
						} else {
							if (var6) {
								world.makeSound(human, "flash", 0.7F, 1.0F);
								var6 = false;
							}

							if (world.random.nextInt(100) < 25 && attemptBreak(human, nx, ny, nz)) {
								int var14 = world.getData(nx, ny, nz) & 0x3;
								world.setRawTypeId(nx, ny, nz, 0);
								WorldGenerator var15 = null;

								if (var14 == 1)
									var15 = new WorldGenTaiga2(true);
								else if (var14 == 2)
									var15 = new WorldGenForest(true);
								else {
									if (world.random.nextInt(10) == 0)
										var15 = new WorldGenBigTree(true);
									else
										var15 = new WorldGenTrees(true);
								}

								world.a("largesmoke", nx, ny, nz, 0.0D, 0.05D, 0.0D);

								if (!var15.a(world, world.random, nx, ny, nz)) {
									world.setRawTypeIdAndData(nx, ny, nz, Block.SAPLING.id, var14);
								}
							}
						}
					}
				}
			}
		}
	}

	public void doFireWall(ItemStack var1, World var2, EntityHuman human) {
		byte var4 = 10;
		var2.makeSound(human, "wall", 1.0F, 1.0F);
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);
		double var8 = MathHelper.floor(human.yaw * 4.0F / 360.0F + 0.5D) & 0x3;

		for (int var10 = -1; var10 <= 1; var10++) {
			for (int var11 = -2; var11 <= 1; var11++) {
				for (int var12 = -var4 * 3; var12 <= var4 * 3; var12++) {
					int ny = y + var11;
					if (var8 == 3.0D) {
						if (((var2.getTypeId(x + var10, ny, z + var12) == 0 || var2.getTypeId(x + var10, ny, z + var12) == 78) &&
							var2.getTypeId(x + var10, ny - 1, z + var12) != 0) && attemptBreak(human, x + var10, ny, z + var12)) {
							var2.setTypeId(x + var10, ny, z + var12, Block.FIRE.id);
						}
					} else if (var8 == 2.0D) {
						if (((var2.getTypeId(x + var12, ny, z - var10) == 0 || var2.getTypeId(x + var12, ny, z - var10) == 78) &&
							var2.getTypeId(x + var12, ny - 1, z - var10) != 0) && attemptBreak(human, x + var12, ny, z - var10)) {
							var2.setTypeId(x + var12, ny, z - var10, Block.FIRE.id);
						}
					} else if (var8 == 1.0D) {
						if (((var2.getTypeId(x - var10, ny, z + var12) == 0 || var2.getTypeId(x - var10, ny, z + var12) == 78) &&
							var2.getTypeId(x - var10, ny - 1, z + var12) != 0) && attemptBreak(human, x - var10, ny, z + var12)) {
							var2.setTypeId(x - var10, ny, z + var12, Block.FIRE.id);
						}
					} else if ((var8 == 0.0D)
							&& (var2.getTypeId(x + var12, ny, z + var10) == 0 || var2.getTypeId(x + var12, ny, z + var10) == 78)
							&& var2.getTypeId(x + var12, ny - 1, z + var10) != 0
							&& attemptBreak(human, x + var12, ny, z + var10)) {
						var2.setTypeId(x + var12, ny, z + var10, Block.FIRE.id);
					}
				}
			}
		}
	}

	public void ConsumeReagent(ItemStack var1, EntityHuman var2, boolean var3) {
		EEBase.ConsumeReagentForDuration(var1, var2, var3);
	}

	public void a(ItemStack ring, World world, Entity unused1, int unused2, boolean unused3) {
		if (EEProxy.isClient(world)) return;
		
		if (!isInitialized(ring)) {
			changeModes(ring);
			initialize(ring);
		}

		updateIcon(ring);
	}

	public void doBurnOverTime(ItemStack var1, World world, EntityHuman human) {
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);
		List<EntityMonster> var7 = world.a(EntityMonster.class,
				AxisAlignedBB.b(human.locX - 5.0D, human.locY - 5.0D, human.locZ - 5.0D, human.locX + 5.0D, human.locY + 5.0D, human.locZ + 5.0D));

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
					int nx = x + var8;
					int ny = y + var13;
					int nz = z + var10;
					if (((var8 <= -2) || (var8 >= 2) || (var13 != 0)) && ((var10 <= -2) || (var10 >= 2) || (var13 != 0)) && (world.random.nextInt(120) == 0)) {
						if (((world.getTypeId(nx, ny, nz) == 0) && (world.getTypeId(nx, ny - 1, nz) != 0))) {
							if (attemptBreak(human, nx, ny, nz))
								world.setTypeId(nx, ny, nz, Block.FIRE.id);
						} else {
							boolean var11 = false;

							for (int var12 = -1; var12 <= 1; var12++) {
								if (((world.getTypeId(nx + var12, ny, nz) == Block.LEAVES.id) || (world.getTypeId(nx + var12, ny, nz) == Block.LOG.id))) {
									if (attemptBreak(human, nx, ny, nz))
										world.setTypeId(nx, ny, nz, Block.FIRE.id);
									var11 = true;
									break;
								}
							}

							if (!var11) {
								for (int var12 = -1; var12 <= 1; var12++) {
									if (((world.getTypeId(nx, ny + var12, nz) == Block.LEAVES.id) || (world.getTypeId(nx, ny + var12, nz) == Block.LOG.id))) {
										if (attemptBreak(human, nx, ny, nz))
											world.setTypeId(nx, ny, nz, Block.FIRE.id);
										var11 = true;
										break;
									}
								}
							}

							if (!var11) {
								for (int var12 = -1; var12 <= 1; var12++) {
									if (((world.getTypeId(nx, ny, nz + var12) == Block.LEAVES.id) || (world.getTypeId(nx, ny, nz + var12) == Block.LOG.id))) {
										if (attemptBreak(human, nx, ny, nz))
											world.setTypeId(nx, ny, nz, Block.FIRE.id);
										var11 = true;
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void doFreezeOverTime(ItemStack var1, World world, EntityHuman human) {
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);
		List<EntityMonster> var7 = world.a(EntityMonster.class,
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
					if (((var8 <= -2) || (var8 >= 2) || (var12 != 0)) && ((var10 <= -2) || (var10 >= 2) || (var12 != 0))) {
						if (world.random.nextInt(20) == 0) {
							int var11 = world.getTypeId(nx, ny - 1, nz);

							if ((var11 != 0) && (Block.byId[var11].a()) && (world.getMaterial(nx, ny - 1, nz).isBuildable())
									&& (world.getTypeId(nx, ny, nz) == 0)) {
								if (attemptBreak(human, nx, ny, nz))
									world.setTypeId(nx, ny, nz, Block.SNOW.id);
							}
						}

						if ((world.random.nextInt(3) == 0) && (world.getMaterial(nx, ny, nz) == Material.WATER)
								&& (world.getTypeId(nx, ny + 1, nz) == 0)) {
							if (attemptBreak(human, nx, ny, nz))
								world.setTypeId(nx, ny, nz, Block.ICE.id);
						}

						if ((world.random.nextInt(3) == 0) && (world.getMaterial(nx, ny, nz) == Material.LAVA)
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

	public void doHeld(ItemStack var1, World var2, EntityHuman var3) {}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3)  {
		String mode = getMode(var1);
		if (mode.equals("wind")) {
			if (EEEventManager.callEvent(new EEArcaneRingEvent(var1, EEAction.RELEASE, var3, EERingAction.StrikeLightning))) return;
			doThunder(var1, var2, var3);
		} else if (mode.equals("ice")) {
			if (EEEventManager.callEvent(new EEArcaneRingEvent(var1, EEAction.RELEASE, var3, EERingAction.Freeze))) return;
			doFreeze(var1, var2, var3);
		} else if (mode.equals("fire")) {
			if (EEEventManager.callEvent(new EEArcaneRingEvent(var1, EEAction.RELEASE, var3, EERingAction.Burn))) return;
			doFireWall(var1, var2, var3);
		} else if (mode.equals("earth")) {
			if (EEEventManager.callEvent(new EEArcaneRingEvent(var1, EEAction.RELEASE, var3, EERingAction.Fertilize))) return;
			doFertilize(var1, var2, var3);
		}
	}

	public void doFreeze(ItemStack ring, World world, EntityHuman human) {
		int charge = chargeLevel(ring);
		world.makeSound(human, "wall", 1.0F, 1.0F);
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);

		for (int var8 = -charge - 1; var8 <= charge + 1; var8++) {
			for (int var9 = -2; var9 <= 1; var9++) {
				for (int var10 = -charge - 1; var10 <= charge + 1; var10++) {
					int nx = x + var10;
					int ny = y + var9;
					int nz = z + var8;
					int id = world.getTypeId(nx, ny - 1, nz);

					if (id != 0 && Block.byId[id].a() && world.getMaterial(nx, ny - 1, nz).isBuildable() && world.getTypeId(nx, ny, nz) == 0) {
						if (attemptPlace(human, nx, ny, nz)) world.setTypeId(nx, ny, nz, Block.SNOW.id);
					}

					Material mat = world.getMaterial(nx, ny, nz);
					int id2 = world.getTypeId(nx, ny + 1, nz);
					if (mat == Material.WATER && id2 == 0) {
						if (attemptBreak(human, nx, ny, nz))
							world.setTypeId(nx, ny, nz, Block.ICE.id);
					} else if (mat == Material.LAVA && id2 == 0 && world.getData(nx, ny, nz) == 0) {
						if (attemptBreak(human, nx, ny, nz))
							world.setTypeId(nx, ny, nz, Block.OBSIDIAN.id);
					}
				}
			}
		}
	}

	public ItemStack a(ItemStack ring, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return ring;

		String mode = getMode(ring);
		if (mode.equals("wind")){
			if (EEEventManager.callEvent(new EEArcaneRingEvent(ring, EEAction.RIGHTCLICK, human, EERingAction.Gust))) return ring;
			doGale(ring, world, human);
		}
		else if (mode.equals("ice")){
			if (EEEventManager.callEvent(new EEArcaneRingEvent(ring, EEAction.RIGHTCLICK, human, EERingAction.ThrowSnowball))) return ring;
			doSnowball(ring, world, human);
		}
		else if (mode.equals("fire")){
			if (EEEventManager.callEvent(new EEArcaneRingEvent(ring, EEAction.RIGHTCLICK, human, EERingAction.ThrowPyrokinesis))) return ring;
			doFireball(ring, world, human);
		}

		return ring;
	}

	private void doSnowball(ItemStack var1, World world, EntityHuman human) {
		human.C_();
		world.makeSound(human, "random.bow", 0.5F, 0.4F / (c.nextFloat() * 0.4F + 0.8F));

		if (!world.isStatic) world.addEntity(new EntitySnowball(world, human));
	}

	private void doFireball(ItemStack var1, World world, EntityHuman human) {
		human.C_();
		world.makeSound(human, "wall", 1.0F, 1.0F);
		world.addEntity(new EntityPyrokinesis(world, human));
	}

	public void doChargeTick(ItemStack var1, World var2, EntityHuman var3) {}
	public void doUncharge(ItemStack var1, World var2, EntityHuman var3) {}
	
	//IMPORTANT this is a test
	public void doCharge(ItemStack var1, World var2, EntityHuman var3) {}
	
	private int negFall = 150, callInterdict = 1200, callFreeze = 1200, callFire = 1200, callEarth = 600;
	private boolean allowNegFall = true, allowEarth = true, allowInterdict = true, allowFreeze = true, allowFire = true;
	public void doPassive(ItemStack ring, World var2, EntityHuman var3) {
		if (var3.fallDistance >= 0.0F){
			if (negFall >= 150){
				allowNegFall = !EEEventManager.callEvent(new EEArcaneRingEvent(ring, EEAction.PASSIVE, var3, EERingAction.NegateFallDamage));
				negFall = 0;
			}
			negFall++;
			if (allowNegFall) var3.fallDistance = 0.0F;
		}

		decThunderCooldown(ring);
		String mode = getMode(ring);
		if (isActivated(ring)) {
			if (mode.equals("wind")) {
				int effect = EEBase.getPlayerEffect(ring.getItem(), var3);
				if (effect <= 0)
					ConsumeReagent(ring, var3, false);
				else if (effect > 0) {
					if (callInterdict >= 1200){
						allowInterdict = !EEEventManager.callEvent(new EEArcaneRingEvent(ring, EEAction.ACTIVE, var3, EERingAction.Interdict));
						if (!allowInterdict){
							if ((ring.getData() & 0x1) == 0) ring.setData(ring.getData() + 1);

							ring.tag.setBoolean("active", false);
							var2.makeSound(var3, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
							callInterdict = 1200;
							return;
						}
						callInterdict = 0;
					}
					callInterdict++;
					
					if (allowInterdict){
						doInterdiction(ring, var2, var3);
						EEBase.updatePlayerEffect(ring.getItem(), effect - 1, var3);
					}
				} else {
					doToggle(ring, var2, var3);
				}
				
				return;
			}
			
			if (mode.equals("ice")) {
				if (callFreeze >= 1200){
					allowFreeze = !EEEventManager.callEvent(new EEArcaneRingEvent(ring, EEAction.ACTIVE, var3, EERingAction.Freeze));
					if (!allowFreeze){
						if ((ring.getData() & 0x1) == 0) ring.setData(ring.getData() + 1);

						ring.tag.setBoolean("active", false);
						var2.makeSound(var3, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
						callFreeze = 1200;
						return;
					}
					callFreeze = 0;
				}
				callFreeze++;
				
				if (allowFreeze) doFreezeOverTime(ring, var2, var3);
				return;
			}
			
			if (mode.equals("fire")) {
				if (callFire >= 1200){
					allowFire = !EEEventManager.callEvent(new EEArcaneRingEvent(ring, EEAction.ACTIVE, var3, EERingAction.Burn));
					if (!allowFire){
						if ((ring.getData() & 0x1) == 0) ring.setData(ring.getData() + 1);

						ring.tag.setBoolean("active", false);
						var2.makeSound(var3, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
						callFire = 1200;
						return;
					}
					callFire = 0;
				}
				callFire++;
				
				if (allowFire) doBurnOverTime(ring, var2, var3);
				return;
			}

		}

		if (mode.equals("earth")) {
			if (callEarth >= 600){
				allowEarth = !EEEventManager.callEvent(new EEArcaneRingEvent(ring, EEAction.ACTIVE, var3, EERingAction.Fertilize));
				callEarth = 0;
			}
			callEarth++;
			
			if (allowEarth) doPassiveHarvest(ring, var2, var3);
			return;
		}
	}

	public void doActive(ItemStack var1, World var2, EntityHuman var3) {
		if (!getMode(var1).equals("earth")) return;
		if (EEEventManager.callEvent(new EEArcaneRingEvent(var1, EEAction.ACTIVE, var3, EERingAction.Harvest))){
			//Turn off
			if ((var1.getData() & 0x1) == 0) var1.setData(var1.getData() + 1);

			var1.tag.setBoolean("active", false);
			var2.makeSound(var3, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
			return;
		}
		
		doActiveHarvest(var1, var2, var3);
	}

	private String getMode(ItemStack ring) {
		String old = getString(ring, "mode");
		if (old == null || old.equals("")) {
			setMode(ring, "ice");
			return "ice";
		}
		return old;
	}
	private void setMode(ItemStack var1, String var2) {
		setString(var1, "mode", var2);
	}

	private boolean isInitialized(ItemStack var1) {
		return getBoolean(var1, "init");
	}
	private void initialize(ItemStack var1) {
		setBoolean(var1, "init", true);
	}

	private void changeModes(ItemStack var1) {
		String mode = getMode(var1);
		if (mode.equals("ice")) {
			setMode(var1, "fire");
			var1.setData(var1.getData() + 2);
		} else if (mode.equals("fire")) {
			setMode(var1, "wind");
			var1.setData(var1.getData() + 2);
		} else if (mode.equals("wind")) {
			setMode(var1, "earth");
			var1.setData(var1.getData() + 2);
		} else {
			setMode(var1, "ice");
			var1.setData(var1.getData() - 6);
		}

		updateIcon(var1);
	}

	private void updateIcon(ItemStack var1) {
		String mode = getMode(var1);
		if (mode == null || mode.equals("")) {
			var1.setData(0);
			setMode(var1, "ice");
			mode = "ice";
		}

		if (mode.equals("earth")) var1.setData(6);
		else if (mode.equals("wind")) var1.setData(4);
		else if (mode.equals("fire")) var1.setData(2);
		else if (mode.equals("ice")) var1.setData(0);

		if ((isActivated(var1)) && ((var1.getData() & 0x1) == 0)) {
			var1.setData(var1.getData() + 1);
		} else if ((!isActivated(var1)) && ((var1.getData() & 0x1) == 1)) {
			var1.setData(var1.getData() - 1);
		}
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		//if (EEEventManager.callEvent(new EEArcaneRingEvent(var1, EEAction.ALTERNATE, var3, EERingAction.ChangeMode))) return;
		changeModes(var1);
	}

	public void doLeftClick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {
		if (isActivated(var1)) {
			//if (EEEventManager.callEvent(new EEArcaneRingEvent(var1, EEAction.TOGGLE, var3, EERingAction.Deactivate))) return;
			if ((var1.getData() & 0x1) == 0) {
				var1.setData(var1.getData() + 1);
			}

			var1.tag.setBoolean("active", false);
			var2.makeSound(var3, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		} else {
			if (EEEventManager.callEvent(new EEArcaneRingEvent(var1, EEAction.TOGGLE, var3, EERingAction.Activate))) return;
			if ((var1.getData() & 0x1) == 1) {
				var1.setData(var1.getData() - 1);
			}

			var1.tag.setBoolean("active", true);
			var2.makeSound(var3, "heal", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		}

		if (!EEProxy.isClient(var2)) updateIcon(var1);
	}

	public boolean canActivate() {
		return true;
	}
}
