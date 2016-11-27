package ee;

import net.minecraft.server.Item;

import ee.core.GuiIds;
//import java.util.HashMap;
//import java.util.Random;
import net.minecraft.server.Block;
import net.minecraft.server.BlockFlower;
//import net.minecraft.server.BlockLongGrass;
import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import net.minecraft.server.mod_EE;

public class ItemMercurialEye extends ItemEECharged {
	public ItemMercurialEye(int var1) {
		super(var1, 4);
	}

	public static boolean ConsumeReagent(EntityHuman human, int fuelid, int fueldata, MercurialEyeData var3) {
		if (fuelid == 0) return false;

		ItemStack fuel = new ItemStack(fuelid, 1, fueldata);

		if (EEMaps.getEMC(fuel) == 0) {
			return false;
		}
		//If the charge of the kleinstar in slot 0 of the mercurial eye is bigger than or equal to the fuel
		if (getKleinStarPoints(var3.getItem(0)) >= EEMaps.getEMC(fuelid, fueldata)) {
			decKleinStarPoints(var3.getItem(0), EEMaps.getEMC(fuelid, fueldata));
			return true;
		}

		return false;
	}

	private boolean ConsumeTransmuteReagent(EntityHuman human, int fuelid, int fueldata, int var4, int var5, MercurialEyeData var6) {
		if (fuelid == 0) {
			return false;
		}

		ItemStack fuel = new ItemStack(fuelid, 1, fueldata);

		if (EEMaps.getEMC(fuel) == 0) {
			return false;
		}
		if (var4 == 0) {
			return false;
		}
		if (EEMaps.getEMC(var4, var5) == 0) {
			return false;
		}
		//If the charge of the kleinstar in slot 0 of the mercurial eye is bigger than or equal to the fuel - ...
		
		if (getKleinStarPoints(var6.getItem(0)) >= EEMaps.getEMC(fuelid, fueldata) - EEMaps.getEMC(var4, var5)) {
			if ((EEMaps.getEMC(fuelid, fueldata) - EEMaps.getEMC(var4, var5) < 0)
					&& (getKleinStarPoints(var6.getItem(0)) - (EEMaps.getEMC(fuelid, fueldata) - EEMaps.getEMC(var4, var5)) > ((ItemKleinStar) var6.getItem(0)
							.getItem()).getMaxPoints(var6.getItem(0)))) {
				return false;
			}

			decKleinStarPoints(var6.getItem(0), EEMaps.getEMC(fuelid, fueldata) - EEMaps.getEMC(var4, var5));
			return true;
		}

		return false;
	}

	public static void decKleinStarPoints(ItemStack var0, int var1) {
		if (var0 == null) return;
		if ((var0.getItem() instanceof ItemKleinStar)) {
			ItemKleinStar var2 = (ItemKleinStar) var0.getItem();
			var2.setKleinPoints(var0, var2.getKleinPoints(var0) - var1 >= 0 ? var2.getKleinPoints(var0) - var1 : 0);
			var2.onUpdate(var0);
		}
	}

	public static int getKleinStarPoints(ItemStack var0) {
		if (var0 == null) return 0;
		Item i0;
		return ((i0 = var0.getItem()) instanceof ItemKleinStar) ? ((ItemKleinStar) i0).getKleinPoints(var0) : 0;
	}

	public static MercurialEyeData getEyeData(EntityHuman human, World world) {
//		String plname = human.name;
//		String var3 = prefix_ + plname;
		String var3 = EEPatch.getEye(human, world);
		MercurialEyeData eyedata = (MercurialEyeData) world.a(MercurialEyeData.class, var3);

		if (eyedata == null) {
			eyedata = new MercurialEyeData(var3);
			eyedata.a();
			world.a(var3, eyedata);
		}

		return eyedata;
	}

	public static MercurialEyeData getEyeData(ItemStack item, EntityHuman human, World world) {
//		String plname = human.name;
//		String var4 = prefix_ + plname;
		String var4 = EEPatch.getEye(human, world);
		MercurialEyeData var5 = (MercurialEyeData) world.a(MercurialEyeData.class, var4);

		if (var5 == null) {
			var5 = new MercurialEyeData(var4);
			var5.a();
			world.a(var4, var5);
		}

		return var5;
	}

	public void doAlternate(ItemStack var1, World world, EntityHuman human) {
		doExtra(world, var1, human);
	}

	public void doExtra(World var1, ItemStack var2, EntityHuman human) {
		if (EEProxy.isServer()) {
			human.openGui(mod_EE.getInstance(), GuiIds.MERCURIAL_EYE, var1, (int) human.locX, (int) human.locY, (int) human.locZ);
		}
	}

	public void doExtra(World var1, ItemStack var2, EntityHuman var3, int var4, int var5, int var6, int var7) {
		if (EEProxy.isServer()) {
			var3.openGui(mod_EE.getInstance(), GuiIds.MERCURIAL_EYE, var1, var5, var6, var7);
		}
	}

	public void d(ItemStack var1, World var2, EntityHuman var3) {
		if (!EEProxy.isClient(var2)) {
//			String var4 = var3.name;
//			String var5 = prefix_ + var4;
			String var5 = EEPatch.getEye(var3, var2);
			MercurialEyeData var6 = (MercurialEyeData) var2.a(MercurialEyeData.class, var5);

			if (var6 == null) {
				var6 = new MercurialEyeData(var5);
				var2.a(var5, var6);
				var6.a();
			}
		}
	}

	//private long delay = 0;
	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int face) {
		if (EEProxy.isClient(world)) return false;
		
		//if (human.getBukkitEntity().hasPermission("eepatch.delay") && delay > System.currentTimeMillis()){
		//	return false;
		//}
		//delay = System.currentTimeMillis()+(1000*5);
		
		MercurialEyeData eyedata = getEyeData(item, human, world);

		ItemStack i1;
		if ((eyedata.getItem(0) != null) && ((i1 = eyedata.getItem(1)) != null)) {
			if (EEMaps.getEMC(i1) == 0) return false;
			if (!EEBase.isKleinStar(eyedata.getItem(0))) return false;
			if (i1.id >= Block.byId.length) return false;

			if (Block.byId[world.getTypeId(x, y, z)].hasTileEntity(world.getData(x, y, z))) {
				if ((world.getTypeId(x, y, z) == EEBlock.eeStone.id) && (world.getData(x, y, z) <= 7)) {
					return false;
				}

				if (world.getTypeId(x, y, z) != EEBlock.eeStone.id) {
					return false;
				}
			}

			if (Block.byId[i1.id].hasTileEntity(i1.getData())) {
				if ((i1.id == EEBlock.eeStone.id) && (i1.getData() <= 7)) {
					return false;
				}

				if (i1.id != EEBlock.eeStone.id) {
					return false;
				}
			}

			int itemid = i1.id;
			int itemdata = i1.getData();
			double direction = EEBase.direction(human);

			if (world.getTypeId(x, y, z) == Block.SNOW.id) {
				if (face == 1) {
					y--;
				} else if (face == 2) {
					z++;
					y++;
				} else if (face == 3) {
					z--;
					y++;
				} else if (face == 4) {
					x++;
					y++;
				} else if (face == 5) {
					x--;
					y++;
				}
			}
			
			int buildmode = EEBase.getBuildMode(human);
			
			if (buildmode == 1) {
				if (face == 0)
					y--;
				else if (face == 1)
					y++;
				else if (face == 2)
					z--;
				else if (face == 3)
					z++;
				else if (face == 4)
					x--;
				else if (face == 5)
					x++;
			}

			byte var13 = 0;
			byte var14 = 0;
			byte var15 = 0;
			byte var16 = 0;
			byte var17 = 0;
			byte var18 = 0;
			
			
			if (buildmode != 3) {
				if ((direction != 0.0D) && (direction != 1.0D)) {
					if ((direction != 2.0D) && (direction != 4.0D)) {
						if ((direction == 3.0D) || (direction == 5.0D)) {
							if (face == 0) {
								var17 = -1;
								var18 = 1;
								var15 = -2;
							} else if (face == 1) {
								var17 = -1;
								var18 = 1;
								var16 = 2;
							} else if (face == 2) {
								var17 = -2;
								var15 = -1;
								var16 = 1;
							} else if (face == 3) {
								var18 = 2;
								var15 = -1;
								var16 = 1;
							} else if ((face == 4) || (face == 5)) {
								var17 = -1;
								var18 = 1;
								var15 = -1;
								var16 = 1;
							}
						}
					} else if (face == 0) {
						var13 = -1;
						var14 = 1;
						var15 = -2;
					} else if (face == 1) {
						var13 = -1;
						var14 = 1;
						var16 = 2;
					} else if ((face != 2) && (face != 3)) {
						if (face == 4) {
							var13 = -2;
							var15 = -1;
							var16 = 1;
						} else if (face == 5) {
							var14 = 2;
							var15 = -1;
							var16 = 1;
						}
					} else {
						var13 = -1;
						var14 = 1;
						var15 = -1;
						var16 = 1;
					}
				} else if ((face != 0) && (face != 1)) {
					if (face == 2) {
						var13 = -1;
						var14 = 1;
						var17 = -2;
					} else if (face == 3) {
						var13 = -1;
						var14 = 1;
						var18 = 2;
					} else if (face == 4) {
						var13 = -2;
						var17 = -1;
						var18 = 1;
					} else if (face == 5) {
						var14 = 2;
						var17 = -1;
						var18 = 1;
					}
				} else {
					var13 = -1;
					var14 = 1;
					var17 = -1;
					var18 = 1;
				}
			}

			if (buildmode != 3) {
				doWall(world, item, human, x, y, z, var13, var15, var17, var14, var16, var18, itemid, itemdata);
			} else {
				doPillar(world, item, human, x, y, z, face, itemid, itemdata);
			}

			return true;
		}

		return false;
	}

	private void doPillar(World world, ItemStack item, EntityHuman entityPlayer, int x, int y, int z, int blockface, int itemid, int itemdata) {
		MercurialEyeData var10 = getEyeData(item, entityPlayer, entityPlayer.world);
		boolean var11 = false;
		byte var12 = 1;
		byte var13 = 1;
		byte var14 = 1;
		byte var15 = -1;
		byte var16 = -1;
		byte var17 = -1;
		int var18 = 3 + 3 * chargeLevel(item) - 1;

		if (blockface == 0) {
			byte var27 = 0;
			int var23 = var18;

			for (int var19 = var15; var19 <= var12; var19++) {
				for (int var20 = var17; var20 <= var14; var20++) {
					for (int var21 = var27; (var21 <= var23)
							&& (doPillarBlock(world, x + var19, y + var21, z + var20, itemid, itemdata, var11, entityPlayer, var10, var21 - var27)); var21++);
				}

			}

		} else if (blockface == 1) {
			int var26 = -var18;
			var13 = 0;

			for (int var19 = var15; var19 <= var12; var19++) {
				for (int var20 = var17; var20 <= var14; var20++) {
					for (int var21 = var13; (var21 >= var26)
							&& (doPillarBlock(world, x + var19, y + var21, z + var20, itemid, itemdata, var11, entityPlayer, var10, -var21)); var21--);
				}

			}

		} else if (blockface == 2) {
			byte var30 = 0;
			int var25 = var18;

			for (int var19 = var15; var19 <= var12; var19++) {
				for (int var20 = var16; var20 <= var13; var20++) {
					for (int var21 = var30; (var21 <= var25)
							&& (doPillarBlock(world, x + var19, y + var20, z + var21, itemid, itemdata, var11, entityPlayer, var10, var21 - var30)); var21++);
				}

			}

		} else if (blockface == 3) {
			int var29 = -var18;
			var14 = 0;

			for (int var19 = var15; var19 <= var12; var19++) {
				for (int var20 = var16; var20 <= var13; var20++) {
					for (int var21 = var14; (var21 >= var29)
							&& (doPillarBlock(world, x + var19, y + var20, z + var21, itemid, itemdata, var11, entityPlayer, var10, -var21)); var21--);
				}

			}

		} else if (blockface == 4) {
			byte var24 = 0;
			int var22 = var18;

			for (int var19 = var16; var19 <= var13; var19++) {
				for (int var20 = var17; var20 <= var14; var20++) {
					for (int var21 = var24; (var21 <= var22)
							&& (doPillarBlock(world, x + var21, y + var19, z + var20, itemid, itemdata, var11, entityPlayer, var10, var21 - var24)); var21++);
				}

			}

		} else if (blockface == 5) {
			int var28 = -var18;
			var12 = 0;

			for (int var19 = var16; var19 <= var13; var19++) {
				for (int var20 = var17; var20 <= var14; var20++) {
					for (int var21 = var12; (var21 >= var28)
							&& (doPillarBlock(world, x + var21, y + var19, z + var20, itemid, itemdata, var11, entityPlayer, var10, -var21)); var21--);
				}
			}
		}
	}

	public boolean doPillarBlock(World world, int x, int y, int z, int id, int data, boolean var7, EntityHuman entityPlayer, MercurialEyeData var9, int var10) {
		int curblockid = world.getTypeId(x, y, z);

		//Not: air, water, lava, long grass or snow
		if ((curblockid != 0) && (curblockid != 8) && (curblockid != 9) && (curblockid != 10) && (curblockid != 11) && (curblockid != BlockFlower.LONG_GRASS.id) && (curblockid != 78)) {
			return var10 <= 4;
		}

		if (curblockid == BlockFlower.LONG_GRASS.id && attemptBreak(entityPlayer, x, y + 1, z)) {
			Block.byId[curblockid].dropNaturally(world, x, y + 1, z, 1, 1.0F, 1);
		}

		if (ConsumeReagent(entityPlayer, id, data, var9) && attemptBreak(entityPlayer, x, y, z)) {
			if (!var7) {
				world.makeSound(entityPlayer, "wall", 0.8F, 0.8F / (c.nextFloat() * 0.4F + 0.8F));
				var7 = true;
			}

			world.setTypeIdAndData(x, y, z, id, data);

			if (world.random.nextInt(8) == 0) {
				world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			}
		}

		return true;
	}

	public void doWall(World world, ItemStack item, EntityHuman entityPlayer, int x, int y, int z, int var7, int var8, int var9, int var10, int var11, int var12, int id, int data) {
		MercurialEyeData eyedata = getEyeData(item, entityPlayer, entityPlayer.world);
		boolean var16 = false;

		for (int chargeX = chargeLevel(item) * var7; chargeX <= chargeLevel(item) * var10; chargeX++) {
			for (int chargeY = chargeLevel(item) * var8; chargeY <= chargeLevel(item) * var11; chargeY++) {
				for (int chargeZ = chargeLevel(item) * var9; chargeZ <= chargeLevel(item) * var12; chargeZ++) {
					int curblock = world.getTypeId(chargeX + x, chargeY + y, chargeZ + z);

					int buildmode = EEBase.playerBuildMode.get(entityPlayer).intValue();
					//Not: air, water, lava, long grass or snow
					if ((curblock != 0) && (curblock != 8) && (curblock != 9) && (curblock != 10) && (curblock != 11) && (curblock != BlockFlower.LONG_GRASS.id) && (curblock != 78)) {
						if (buildmode == 2) {
							int var21 = world.getTypeId(chargeX + x, chargeY + y, chargeZ + z);
							int var22 = world.getData(chargeX + x, chargeY + y, chargeZ + z);

							if ((EEMaps.getEMC(var21, var22) != 0) && (ConsumeTransmuteReagent(entityPlayer, id, data, var21, var22, eyedata))
									&& attemptBreak(entityPlayer, chargeX + x, chargeY + y, chargeZ + z)) {
								if (!var16) {
									world.makeSound(entityPlayer, "wall", 0.8F, 0.8F / (c.nextFloat() * 0.4F + 0.8F));
									var16 = true;
								}

								world.setTypeIdAndData(chargeX + x, chargeY + y, chargeZ + z, id, data);

								if (world.random.nextInt(8) == 0) {
									world.a("largesmoke", x + chargeX, y + chargeY + 1, z + chargeZ, 0.0D, 0.0D, 0.0D);
								}
							}
						}
					} else if (buildmode != 2) {
						if (curblock == BlockFlower.LONG_GRASS.id && attemptBreak(entityPlayer, chargeX + x, chargeY + y + 1, chargeZ + z)) {
							Block.byId[curblock].dropNaturally(world, chargeX + x, chargeY + y + 1, chargeZ + z, 1, 1.0F, 1);
						}

						if (ConsumeReagent(entityPlayer, id, data, eyedata) && attemptBreak(entityPlayer, chargeX + x, chargeY + y, chargeZ + z)) {
							if (!var16) {
								world.makeSound(entityPlayer, "wall", 0.8F, 0.8F / (c.nextFloat() * 0.4F + 0.8F));
								var16 = true;
							}

							world.setTypeIdAndData(chargeX + x, chargeY + y, chargeZ + z, id, data);

							if (world.random.nextInt(8) == 0) {
								world.a("largesmoke", x + chargeX, y + chargeY + 1, z + chargeZ, 0.0D, 0.0D, 0.0D);
							}
						}
					}
				}
			}
		}
	}

	public void ConsumeReagent(ItemStack var1, EntityHuman var2, boolean var3) {}

	public void doPassive(ItemStack var1, World var2, EntityHuman var3) {}

	public void doActive(ItemStack var1, World var2, EntityHuman var3) {}

	public void doHeld(ItemStack var1, World var2, EntityHuman var3) {}

	public void doLeftClick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {
		EEBase.updateBuildMode(var3);
	}

}
