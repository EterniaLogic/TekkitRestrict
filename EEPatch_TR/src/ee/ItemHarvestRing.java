package ee;

import ee.events.EEEnums.EERingAction;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.ring.EEHarvestRingEvent;
import net.minecraft.server.Block;
import net.minecraft.server.BlockFlower;
import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.World;
import net.minecraft.server.WorldGenBigTree;
import net.minecraft.server.WorldGenForest;
import net.minecraft.server.WorldGenTaiga2;
import net.minecraft.server.WorldGenTrees;
import net.minecraft.server.WorldGenerator;

public class ItemHarvestRing extends ItemEECharged {
	@SuppressWarnings("unused")
	private int ticksLastSpent;
	public int tempMeta = 0;
	public int costCounter = 0;

	public ItemHarvestRing(int var1) {
		super(var1, 0);
	}

	public int getIconFromDamage(int var1) {
		return !isActivated(var1) ? this.textureId : this.textureId + 1;
	}

	public void doFertilize(ItemStack var1, World var2, EntityHuman var3) {
		boolean var4 = false;
		boolean var5 = true;
		boolean var6 = true;
		int x = (int) EEBase.playerX(var3);
		int y = (int) EEBase.playerY(var3);
		int z = (int) EEBase.playerZ(var3);

		for (int var10 = -15; var10 <= 15; var10++) {
			for (int var11 = -15; var11 <= 15; var11++) {
				for (int var12 = -15; var12 <= 15; var12++) {
					int id = var2.getTypeId(x + var10, y + var11, z + var12);

					if ((3 >= var10) && (var10 >= -3) && (3 >= var11) && (var11 >= -3) && (3 >= var12) && (var12 >= -3)) {
						if (id == Block.CROPS.id) {
							int data = var2.getData(x + var10, y + var11, z + var12);

							if (data < 7) {
								if (attemptBreak(var3, x + var10, y + var11, z + var12)){
									if (!var4) {
										if (ConsumeReagentBonemeal(var3, var5)) {
											if (var6) {
												var2.makeSound(var3, "flash", 0.7F, 1.0F);
												var6 = false;
											}
	
											var4 = true;
											data++;
	
											if (var2.random.nextInt(8) == 0) {
												var2.a("largesmoke", x + var10, y + var11, z + var12, 0.0D, 0.05D, 0.0D);
											}
	
											var2.setData(x + var10, y + var11, z + var12, data);
										}
	
										var5 = false;
									} else {
										if (var6) {
											var2.makeSound(var3, "flash", 0.7F, 1.0F);
											var6 = false;
										}
	
										data++;
	
										if (var2.random.nextInt(8) == 0) {
											var2.a("largesmoke", x + var10, y + var11, z + var12, 0.0D, 0.05D, 0.0D);
										}
	
										var2.setData(x + var10, y + var11, z + var12, data);
									}
								}
							}
						} else if ((id == Block.SUGAR_CANE_BLOCK.id)
								&& (var2.getTypeId(x + var10, y + var11 - 4, z + var12) != Block.SUGAR_CANE_BLOCK.id)
								&& (var2.getTypeId(x + var10, y + var11 + 1, z + var12) == 0)) {
							if (attemptPlace(var3, x + var10, y + var11 + 1, z + var12)){
								if (!var4) {
									if (ConsumeReagentBonemeal(var3, var5)) {
										if (var6) {
											var2.makeSound(var3, "flash", 0.7F, 1.0F);
											var6 = false;
										}
	
										var4 = true;
										var2.setTypeId(x + var10, y + var11 + 1, z + var12, Block.SUGAR_CANE_BLOCK.id);
									}
	
									var5 = false;
								} else {
									if (var6) {
										var2.makeSound(var3, "flash", 0.7F, 1.0F);
										var6 = false;
									}
	
									var2.setTypeId(x + var10, y + var11 + 1, z + var12, Block.SUGAR_CANE_BLOCK.id);
								}
							}
						} else if ((id == Block.CACTUS.id) && (var2.getTypeId(x + var10, y + var11 - 4, z + var12) != Block.CACTUS.id)
								&& (var2.getTypeId(x + var10, y + var11 + 1, z + var12) == 0)) {
							if (attemptPlace(var3, x + var10, y + var11 + 1, z + var12)){
								if (!var4) {
									if (ConsumeReagentBonemeal(var3, var5)) {
										if (var6) {
											var2.makeSound(var3, "flash", 0.7F, 1.0F);
											var6 = false;
										}
	
										var4 = true;
										var2.setTypeId(x + var10, y + var11 + 1, z + var12, Block.CACTUS.id);
									}
	
									var5 = false;
								} else {
									if (var6) {
										var2.makeSound(var3, "flash", 0.7F, 1.0F);
										var6 = false;
									}
	
									var2.setTypeId(x + var10, y + var11 + 1, z + var12, Block.CACTUS.id);
								}
							}
						}
						
						if ((id == BlockFlower.RED_ROSE.id) || (id == BlockFlower.YELLOW_FLOWER.id) || (id == BlockFlower.BROWN_MUSHROOM.id)
								|| (id == BlockFlower.RED_MUSHROOM.id)) {
							for (int var14 = -1; var14 <= 0; var14++) {
								if (var2.getTypeId(x + var10 + var14, y + var11, z + var12) == 0) {
									if (attemptPlace(var3, x + var10 + var14, y + var11, z + var12)){
										if (!var4) {
											if (ConsumeReagentBonemeal(var3, var5)) {
												if (var6) {
													var2.makeSound(var3, "flash", 0.7F, 1.0F);
													var6 = false;
												}
	
												var4 = true;
												var2.setTypeId(x + var10 + var14, y + var11, z + var12, id);
											}
	
											var5 = false;
										} else {
											if (var6) {
												var2.makeSound(var3, "flash", 0.7F, 1.0F);
												var6 = false;
											}
	
											var2.setTypeId(x + var10 + var14, y + var11, z + var12, id);
										}
									}
								} else if (var2.getTypeId(x + var10, y + var11, z + var12 + var14) == 0) {
									if (attemptPlace(var3, x + var10, y + var11, z + var12)){
										if (var4) {
											if (var6) {
												var2.makeSound(var3, "flash", 0.7F, 1.0F);
												var6 = false;
											}
	
											var2.setTypeId(x + var10, y + var11, z + var12 + var14, id);
											break;
										}
	
										if (ConsumeReagentBonemeal(var3, var5)) {
											if (var6) {
												var2.makeSound(var3, "flash", 0.7F, 1.0F);
												var6 = false;
											}
	
											var4 = true;
											var2.setTypeId(x + var10, y + var11, z + var12 + var14, id);
										}
									}

									var5 = false;
								}
							}
						}
					}

					if (id == Block.SAPLING.id) {
						if (!var4) {
							if (ConsumeReagentBonemeal(var3, var5)) {
								if (var6) {
									var2.makeSound(var3, "flash", 0.7F, 1.0F);
									var6 = false;
								}

								var4 = true;

								if (var2.random.nextInt(100) < 25 && attemptBreak(var3, x + var10, y + var11, z + var12)) {
									int var14 = var2.getData(x + var10, y + var11, z + var12) & 0x3;
									var2.setRawTypeId(x + var10, y + var11, z + var12, 0);
									Object var15 = null;

									if (var14 == 1)
										var15 = new WorldGenTaiga2(true);
									else if (var14 == 2)
										var15 = new WorldGenForest(true);
									else {
										var15 = new WorldGenTrees(true);

										if (var2.random.nextInt(10) == 0) {
											var15 = new WorldGenBigTree(true);
										}
									}

									if (var2.random.nextInt(8) == 0) {
										var2.a("largesmoke", x + var10, y + var11, z + var12, 0.0D, 0.05D, 0.0D);
									}

									if (!((WorldGenerator) var15).a(var2, var2.random, x + var10, y + var11, z + var12)) {
										var2.setRawTypeIdAndData(x + var10, y + var11, z + var12, Block.SAPLING.id, var14);
									}
								}
							}

							var5 = false;
						} else {
							if (var6) {
								var2.makeSound(var3, "flash", 0.7F, 1.0F);
								var6 = false;
							}

							if (var2.random.nextInt(100) < 25 && attemptBreak(var3, x + var10, y + var11, z + var12)) {
								int var14 = var2.getData(x + var10, y + var11, z + var12) & 0x3;
								var2.setRawTypeId(x + var10, y + var11, z + var12, 0);
								Object var15 = null;

								if (var14 == 1) {
									var15 = new WorldGenTaiga2(true);
								} else if (var14 == 2) {
									var15 = new WorldGenForest(true);
								} else {
									var15 = new WorldGenTrees(true);

									if (var2.random.nextInt(10) == 0) {
										var15 = new WorldGenBigTree(true);
									}
								}

								if (var2.random.nextInt(8) == 0) {
									var2.a("largesmoke", x + var10, y + var11, z + var12, 0.0D, 0.05D, 0.0D);
								}

								if (!((WorldGenerator) var15).a(var2, var2.random, x + var10, y + var11, z + var12)) {
									var2.setRawTypeIdAndData(x + var10, y + var11, z + var12, Block.SAPLING.id, var14);
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean ConsumeReagentBonemeal(EntityHuman var1, boolean var2) {
		return EEBase.Consume(new ItemStack(Item.INK_SACK, 4, 15), var1, var2);
	}

	public boolean ConsumeReagentSapling(EntityHuman var1, boolean var2) {
		if (EEBase.Consume(new ItemStack(Block.SAPLING, 1, 0), var1, var2)) {
			this.tempMeta = 0;
			return true;
		}
		if (EEBase.Consume(new ItemStack(Block.SAPLING, 1, 1), var1, var2)) {
			this.tempMeta = 1;
			return true;
		}
		if (EEBase.Consume(new ItemStack(Block.SAPLING, 1, 2), var1, var2)) {
			this.tempMeta = 2;
			return true;
		}

		return false;
	}

	public boolean ConsumeReagentCactus(EntityHuman var1, boolean var2) {
		return EEBase.Consume(new ItemStack(Block.CACTUS, 1), var1, var2);
	}

	public boolean ConsumeReagentSeeds(EntityHuman var1, boolean var2) {
		return EEBase.Consume(new ItemStack(Item.SEEDS, 1), var1, var2);
	}

	public boolean ConsumeReagentReed(EntityHuman var1, boolean var2) {
		return EEBase.Consume(new ItemStack(Item.SUGAR_CANE, 1), var1, var2);
	}

	public float getDestroySpeed(ItemStack var1, Block var2) {
		return 0.0F;
	}

	private int callnr = 600;
	private boolean allowed = true;
	public void doPassive(ItemStack var1, World var2, EntityHuman var3) {
		if (callnr >= 600){
			allowed = !EEEventManager.callEvent(new EEHarvestRingEvent(var1, EEAction.PASSIVE, var3, EERingAction.Fertilize));
			callnr = 0;
		}
		callnr++;
		if (!allowed) return;
		int x = (int) EEBase.playerX(var3);
		int y = (int) EEBase.playerY(var3);
		int z = (int) EEBase.playerZ(var3);

		for (int var7 = -5; var7 <= 5; var7++) {
			for (int var8 = -5; var8 <= 5; var8++) {
				for (int var9 = -5; var9 <= 5; var9++) {
					int id = var2.getTypeId(x + var7, y + var8, z + var9);

					if (id == Block.CROPS.id) {
						int data = var2.getData(x + var7, y + var8, z + var9);

						if ((data < 7) && (var2.random.nextInt(600) == 0) && attemptBreak(var3, x + var7, y + var8, z + var9)) {
							data++;
							var2.setData(x + var7, y + var8, z + var9, data);
						}
					} else if ((id != BlockFlower.YELLOW_FLOWER.id) && (id != BlockFlower.RED_ROSE.id) && (id != BlockFlower.BROWN_MUSHROOM.id)
							&& (id != BlockFlower.RED_MUSHROOM.id)) {
						if ((id == Block.GRASS.id) && (var2.getTypeId(x + var7, y + var8 + 1, z + var9) == 0) && (var2.random.nextInt(4000) == 0) && attemptPlace(var3, x + var7, y + var8 + 1, z + var9)) {
							var2.setTypeId(x + var7, y + var8 + 1, z + var9, BlockFlower.LONG_GRASS.id);
							var2.setData(x + var7, y + var8 + 1, z + var9, 1);
						}

						if ((id == Block.DIRT.id) && (var2.getTypeId(x + var7, y + var8 + 1, z + var9) == 0) && (var2.random.nextInt(800) == 0)) {
							if (attemptPlace(var3, x + var7, y + var8, z + var9))
								var2.setTypeId(x + var7, y + var8, z + var9, Block.GRASS.id);
						} else if (((id == Block.SUGAR_CANE_BLOCK.id) || (id == Block.CACTUS.id))
								&& (var2.getTypeId(x + var7, y + var8 + 1, z + var9) == 0)
								&& (var2.getTypeId(x + var7, y + var8 - 4, z + var9) != Block.SUGAR_CANE_BLOCK.id)
								&& (var2.getTypeId(x + var7, y + var8 - 4, z + var9) != Block.CACTUS.id) && (var2.random.nextInt(600) == 0)
								&& attemptPlace(var3, x + var7, y + var8 + 1, z + var9)) {
							var2.setTypeId(x + var7, y + var8 + 1, z + var9, id);

							if (var2.random.nextInt(8) == 0) {
								var2.a("largesmoke", x + var7, y + var8, z + var9, 0.0D, 0.05D, 0.0D);
							}
						}
					} else if (var2.random.nextInt(2) == 0) {
						for (int var11 = -1; var11 < 0; var11++) {
							if ((var2.getTypeId(x + var7 + var11, y + var8, z + var9) == 0)
									&& (var2.getTypeId(x + var7 + var11, y + var8 - 1, z + var9) == Block.GRASS.id)) {
								if (var2.random.nextInt(800) == 0 && attemptPlace(var3, x + var7 + var11, y + var8, z + var9)) {
									var2.setTypeId(x + var7 + var11, y + var8, z + var9, id);

									if (var2.random.nextInt(8) == 0) {
										var2.a("largesmoke", x + var7 + var11, y + var8, z + var9, 0.0D, 0.05D, 0.0D);
									}
								}
							} else if ((var2.getTypeId(x + var7, y + var8, z + var9 + var11) == 0)
									&& (var2.getTypeId(x + var7, y + var8 - 1, z + var9 + var11) == Block.GRASS.id)
									&& (var2.random.nextInt(1800) == 0) && attemptPlace(var3, x + var7, y + var8, z + var9 + var11)) {
								var2.setTypeId(x + var7, y + var8, z + var9 + var11, id);

								if (var2.random.nextInt(8) == 0) {
									var2.a("largesmoke", x + var7, y + var8, z + var9 + var11, 0.0D, 0.05D, 0.0D);
								}
							}
						}
					}
				}
			}
		}
	}

	private int callnr2 = 1200;
	private boolean allowed2 = true;
	public void doActive(ItemStack item, World world, EntityHuman human) {
		if (callnr2 >= 1200){
			allowed2 = !EEEventManager.callEvent(new EEHarvestRingEvent(item, EEAction.ACTIVE, human, EERingAction.Harvest));
			if (!allowed2){
				//Turn off
				if (isActivated(item.getData())) item.setData(item.getData() - 1);
				item.tag.setBoolean("active", false);
				world.makeSound(human, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
				callnr2 = 1200;
				return;
			}
			callnr2 = 0;
		}
		callnr2++;
		if (!allowed2) return;
		
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);

		for (int c1 = -5; c1 <= 5; c1++) {
			for (int c2 = -5; c2 <= 5; c2++) {
				for (int c3 = -5; c3 <= 5; c3++) {
					int id = world.getTypeId(x + c1, y + c2, z + c3);

					if (id == Block.CROPS.id) {
						int data = world.getData(x + c1, y + c2, z + c3);

						if (data >= 7) {
							if (attemptBreak(human, x + c1, y + c2, z + c3)){
								Block.byId[id].dropNaturally(world, x + c1, y + c2, z + c3, world.getData(x + c1, y + c2, z + c3), 0.05F, 1);
								Block.byId[id].dropNaturally(world, x + c1, y + c2, z + c3, world.getData(x + c1, y + c2, z + c3), 1.0F, 1);
								world.setTypeId(x + c1, y + c2, z + c3, 0);

								if (world.random.nextInt(8) == 0) {
									world.a("largesmoke", x + c1, y + c2, z + c3, 0.0D, 0.05D, 0.0D);
								}
							}
						} else if (world.random.nextInt(400) == 0) {
							data++;
							world.setData(x + c1, y + c2, z + c3, data);
						}
					} else if ((id != BlockFlower.YELLOW_FLOWER.id) && (id != BlockFlower.RED_ROSE.id) && (id != BlockFlower.BROWN_MUSHROOM.id)
							&& (id != BlockFlower.RED_MUSHROOM.id) && (id != BlockFlower.LONG_GRASS.id)) {
						int id2 = world.getTypeId(x + c1, y + c2 - 4, z + c3);
						int sc = Block.SUGAR_CANE_BLOCK.id;
						int c = Block.CACTUS.id;
						if ((id == sc && id2 == sc && world.getTypeId(x+c1,y+c2-1,z+c3) == sc && world.getTypeId(x+c1,y+c2-2,z+c3) == sc && world.getTypeId(x+c1,y+c2-3,z+c3) == sc) || (id == c && id2 == c)) {
							if (id == sc) {
								if (attemptBreak(human, x + c1, y + c2 - 3, z + c3)){
									Block.byId[id].dropNaturally(world, x + c1, y + c2 - 3, z + c3, world.getData(x + c1, y + c2, z + c3), 0.25F, 1);
									Block.byId[id].dropNaturally(world, x + c1, y + c2 - 3, z + c3, world.getData(x + c1, y + c2, z + c3), 1.0F, 1);
									world.setTypeId(x + c1, y + c2 - 3, z + c3, 0);
								}
							} else {
								if (attemptBreak(human, x + c1, y + c2 - 4, z + c3)){
									Block.byId[id].dropNaturally(world, x + c1, y + c2 - 4, z + c3, world.getData(x + c1, y + c2, z + c3), 0.25F, 1);
									Block.byId[id].dropNaturally(world, x + c1, y + c2 - 4, z + c3, world.getData(x + c1, y + c2, z + c3), 1.0F, 1);
									world.setTypeId(x + c1, y + c2 - 4, z + c3, 0);
								}
							}

							if (world.random.nextInt(8) == 0) {
								world.a("largesmoke", x + c1, y + c2 - 3, z + c3, 0.0D, 0.05D, 0.0D);
							}
						}
					} else {
						if (attemptBreak(human, x + c1, y + c2, z + c3)){
							Block.byId[id].dropNaturally(world, x + c1, y + c2, z + c3, world.getData(x + c1, y + c2, z + c3), 0.05F, 1);
							Block.byId[id].dropNaturally(world, x + c1, y + c2, z + c3, world.getData(x + c1, y + c2, z + c3), 1.0F, 1);
							world.setTypeId(x + c1, y + c2, z + c3, 0);
	
							if (world.random.nextInt(8) == 0) {
								world.a("largesmoke", x + c1, y + c2, z + c3, 0.0D, 0.05D, 0.0D);
							}
						}
					}
				}
			}
		}
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World var3, int x, int y, int z, int face) {
		if (EEProxy.isClient(var3)) return false;
		
		if (EEEventManager.callEvent(new EEHarvestRingEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EERingAction.PlantRadius))) return false;

		if (var3.getTypeId(x, y, z) == 60) {
			for (int var14 = -5; var14 <= 5; var14++) {
				for (int var9 = -5; var9 <= 5; var9++) {
					int id = var3.getTypeId(var14 + x, y, var9 + z);
					int id2 = var3.getTypeId(var14 + x, y + 1, var9 + z);

					if (id == 60 && id2 == 0 && attemptPlace(human, var14 + x, y + 1, var9 + z) && ConsumeReagentSeeds(human, false)) {
						var3.setTypeId(var14 + x, y + 1, var9 + z, Block.CROPS.id);

						if (var3.random.nextInt(8) == 0) {
							var3.a("largesmoke", var14 + x, y, var9 + z, 0.0D, 0.05D, 0.0D);
						}
					}
				}
			}

			return true;
		}

		if (var3.getTypeId(x, y, z) == 12) {
			double var15 = EEBase.direction(human);

			if (var15 == 5.0D) x += 5;
			else if (var15 == 4.0D) z -= 5;
			else if (var15 == 3.0D) x -= 5;
			else if (var15 == 2.0D) z += 5;

			for (int var10 = -5; var10 <= 5; var10++) {
				for (int var11 = -5; var11 <= 5; var11++) {
					int id = var3.getTypeId(var10 + x, y, var11 + z);
					int id2 = var3.getTypeId(var10 + x, y + 1, var11 + z);

					if (id == 12 && id2 == 0 && var10 % 5 == 0 && var11 % 5 == 0 && attemptPlace(human, var10 + x, y + 1, var11 + z) && ConsumeReagentCactus(human, false)) {
						var3.setTypeId(var10 + x, y + 1, var11 + z, Block.CACTUS.id);

						if (var3.random.nextInt(8) == 0) {
							var3.a("largesmoke", var10 + x, y, var11 + z, 0.0D, 0.05D, 0.0D);
						}
					}
				}
			}

			return true;
		}

		boolean var8 = false;

		int tempid = var3.getTypeId(x, y, z);
		if ((tempid != Block.DIRT.id) && (tempid != Block.GRASS.id) && (tempid != BlockFlower.LONG_GRASS.id)) {
			return false;
		}

		if (tempid == BlockFlower.LONG_GRASS.id && attemptBreak(human, x, y, z)) {
			Block.byId[BlockFlower.LONG_GRASS.id].dropNaturally(var3, x, y, z, 1, 1.0F, 1);
			var3.setTypeId(x, y, z, 0);
			y--;
		}

		if ((var3.getMaterial(x + 1, y, z) == Material.WATER) || (var3.getMaterial(x - 1, y, z) == Material.WATER)
				|| (var3.getMaterial(x, y, z + 1) == Material.WATER) || (var3.getMaterial(x, y, z - 1) == Material.WATER)) {
			var8 = true;
		}

		for (int var9 = -8; var9 <= 8; var9++) {
			for (int var10 = -8; var10 <= 8; var10++) {
				int id = var3.getTypeId(var9 + x, y, var10 + z);
				int id2 = var3.getTypeId(var9 + x, y + 1, var10 + z);

				if (var8) {
					if (((var3.getMaterial(var9 + x + 1, y, var10 + z) == Material.WATER)
						|| (var3.getMaterial(var9 + x - 1, y, var10 + z) == Material.WATER)
						|| (var3.getMaterial(var9 + x, y, var10 + z + 1) == Material.WATER)
						|| (var3.getMaterial(var9 + x, y, var10 + z - 1) == Material.WATER))
						&& ((id == Block.DIRT.id) || (id == Block.GRASS.id)) && (id2 == 0) && attemptPlace(human, var9 + x, y + 1, var10 + z) && (ConsumeReagentReed(human, false))) {
						var3.setTypeId(var9 + x, y + 1, var10 + z, Block.SUGAR_CANE_BLOCK.id);
					}
				} else if ((id == Block.DIRT.id || id == Block.GRASS.id) && (id2 == 0 || id2 == BlockFlower.LONG_GRASS.id) && var9 % 4 == 0
						&& var10 % 4 == 0 && attemptPlace(human, var9 + x, y + 1, var10 + z) && ConsumeReagentSapling(human, false)) {
					if (id2 == BlockFlower.LONG_GRASS.id) {
						Block.byId[id2].dropNaturally(var3, var9 + x, y + 1, var10 + z, 1, 1.0F, 1);
						var3.setTypeId(var9 + x, y + 1, var10 + z, 0);
					}

					var3.setTypeIdAndData(var9 + x, y + 1, var10 + z, Block.SAPLING.id, this.tempMeta);

					if (var3.random.nextInt(8) == 0) {
						var3.a("largesmoke", var9 + x, y, var10 + z, 0.0D, 0.05D, 0.0D);
					}
				}
			}
		}

		return true;
	}

	public void ConsumeReagent(ItemStack var1, EntityHuman var2, boolean var3) {
		EEBase.ConsumeReagentForDuration(var1, var2, var3);
	}

	public void doLeftClick(ItemStack item, World var2, EntityHuman human) {
		if (EEEventManager.callEvent(new EEHarvestRingEvent(item, EEAction.LEFTCLICK, human, EERingAction.Fertilize))) return;
		doFertilize(item, var2, human);
	}

	public boolean canActivate() {
		return true;
	}

	public void doCharge(ItemStack var1, World var2, EntityHuman var3) {}
	public void doChargeTick(ItemStack var1, World var2, EntityHuman var3) {}
	public void doUncharge(ItemStack var1, World var2, EntityHuman var3) {}
}