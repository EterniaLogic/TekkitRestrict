package ee;

import java.util.ArrayList;
import java.util.Iterator;

import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.rm.EERedMorningStarEvent;
import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.World;

public class ItemRedMace extends ItemRedTool {
	private static Block[] blocksEffectiveAgainst;
	
	static {
		blocksEffectiveAgainst = new Block[] {
				Block.COBBLESTONE, Block.DOUBLE_STEP, Block.STEP, Block.STONE, Block.SANDSTONE, Block.MOSSY_COBBLESTONE, Block.IRON_ORE, Block.IRON_BLOCK, 
				Block.COAL_ORE, Block.GOLD_BLOCK, Block.GOLD_ORE, Block.DIAMOND_ORE, Block.DIAMOND_BLOCK, Block.REDSTONE_ORE, Block.GLOWING_REDSTONE_ORE, 
				Block.ICE, Block.NETHERRACK, Block.LAPIS_ORE, Block.LAPIS_BLOCK, Block.OBSIDIAN, Block.GRASS, Block.DIRT, Block.SOUL_SAND, Block.SAND, 
				Block.GRAVEL, Block.SNOW, Block.SNOW_BLOCK, Block.CLAY, Block.SOIL, EEBlock.eeStone, EEBlock.eePedestal, EEBlock.eeDevice, EEBlock.eeChest
			};
	}

	protected ItemRedMace(int var1) {
		super(var1, 4, 16, blocksEffectiveAgainst);
	}

	public float getStrVsBlock(ItemStack var1, Block var2, int var3) {
		float var4 = 1.0F;
		if ((var2.id == EEBlock.eePedestal.id && var3 == 0) || (var2.id == EEBlock.eeStone.id && (var3 == 8 || var3 == 9))) {
			return 1200000F / var4;
		} else {
			if ((var2.material != Material.STONE && var2.material != Material.ORE) || chargeLevel(var1) <= 0){
				if (var2.material == Material.STONE || var2.material == Material.ORE){
					return 16F / var4;
				} else {
					return (super.getDestroySpeed(var1, var2) + 4F * chargeLevel(var1)) / var4;
				}
			} else {
				return 16F + (16F * chargeLevel(var1)) / var4;
			}
		}
		
		
		/*
		if ((var2.id != EEBlock.eePedestal.id || var3 != 0) &&
			(var2.id != EEBlock.eeStone.id || (var3 != 8 && var3 != 9))){
			return 16.0F;
		} else {
			if (var2.material != Material.STONE && var2.material != Material.ORE){
				return (super.getDestroySpeed(var1, var2) + (4.0F * chargeLevel(var1)));
			} else {
				if ((var2.material == Material.STONE || var2.material == Material.ORE) &&
					(chargeLevel(var1) > 0)) {
					return (16.0F + (16.0F * chargeLevel(var1)));
				} else {
					return 1200000.0F;
				}
				
			}
		}
		*/
		
		/*
		if ((var2.id == EEBlock.eePedestal.id && var3 == 0) || (var2.id == EEBlock.eeStone.id && (var3 == 8 || var3 == 9))){
			return 1200000F / var4;
		} else {
			if ((var2.material == Material.STONE || var2.material == Material.ORE)){
				return 16F + (16F * chargeLevel(var1)) / var4;
			}
			if (chargeLevel(var1) <= 0){
				return 16F / var4;
			}
			return (super.getDestroySpeed(var1, var2) + 4.0F * chargeLevel(var1)) / var4;
			
		}
		*/
		
		//return (((((var2.id != EEBlock.eePedestal.id) || (var3 != 0))) && (((var2.id != EEBlock.eeStone.id) || ((var3 != 8) && (var3 != 9))))) ? 16.0F / var4 : ((var2.material != Material.STONE) && (var2.material != Material.ORE)) ? (super.getDestroySpeed(var1, var2) + 4.0F * chargeLevel(var1)) / var4 : ((((var2.material == Material.STONE) || (var2.material == Material.ORE))) && (chargeLevel(var1) > 0)) ? 16.0F + 16.0F * chargeLevel(var1) / var4 : 1200000.0F / var4);
		/*
		float var4 = 1.0F;
		
		return (var2.id != EEBlock.eePedestal.id || var3 != 0) && (var2.id != EEBlock.eeStone.id || var3 != 8 && var3 != 9) ? 16.0F / var4
				: var2.material != Material.STONE && var2.material != Material.ORE ? (super.getDestroySpeed(var1, var2) + 4.0F * chargeLevel(var1))
						/ var4 : (var2.material == Material.STONE || var2.material == Material.ORE) && chargeLevel(var1) > 0 ? 16.0F + (16.0F
						* chargeLevel(var1)) / var4 : 1200000.0F / var4;
		*/
	}

	public boolean a(ItemStack item, int unused, int x, int y, int z, EntityLiving var6) {
		if (!(var6 instanceof EntityHuman)) return true;
		
		EntityHuman human = (EntityHuman) var6;

		int mode = EEBase.getToolMode(human);
		if (mode != 0) {
			if (mode == 1) {
				if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.TallBreak))) return true;
				doTallImpact(human, item, x, y, z, EEBase.direction(human));
			} else if (mode == 2) {
				if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.WideBreak))) return true;
				doWideImpact(human, item, x, y, z, EEBase.heading(human));
			} else if (mode == 3) {
				if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.LongBreak))) return true;
				doLongImpact(human, item, x, y, z, EEBase.direction(human));
			}
		} else if (EEBase.getHammerMode(human)) {
			if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.MegaBreak))) return true;
			doMegaImpact(human, item, x, y, z, EEBase.direction(human));
		}

		return true;
	}

	public void doLongImpact(EntityHuman human, ItemStack item, int x, int y, int z, double var6) {
		World world = human.world;
		cleanDroplist(item);

		for (int var8 = 1; var8 <= 2; ++var8) {
			int nx = x;
			int ny = y;
			int nz = z;

			if (var6 == 0.0D) ny = y - var8;
			else if (var6 == 1.0D) ny += var8;
			else if (var6 == 2.0D) nz = z + var8;
			else if (var6 == 3.0D) nx = x - var8;
			else if (var6 == 4.0D) nz -= var8;
			else if (var6 == 5.0D) nx += var8;

			int id = world.getTypeId(nx, ny, nz);
			int data = world.getData(nx, ny, nz);

			if (canBreak(id, data) && attemptBreak(human, nx, ny, nz)) 
				scanBlockAndBreak(world, item, nx, ny, nz);
		}

		ejectDropList(world, item, x, y + 0.5D, z);
	}
	public void doWideImpact(EntityHuman human, ItemStack item, int x, int y, int z, double var6) {
		World world = human.world;
		cleanDroplist(item);

		for (int var8 = -1; var8 <= 1; ++var8) {
			int nx = x;
			int nz = z;

			if (var8 == 0) continue;
			if ((var6 != 2.0D) && (var6 != 4.0D)) {
				nz = z + var8;
			} else {
				nx = x + var8;
			}

			int id = world.getTypeId(nx, y, nz);
			int data = world.getData(nx, y, nz);

			if (canBreak(id, data) && attemptBreak(human, nx, y, nz)) 
				scanBlockAndBreak(world, item, nx, y, nz);
		}

		ejectDropList(world, item, x, y + 0.5D, z);
	}
	public void doTallImpact(EntityHuman human, ItemStack item, int x, int y, int z, double var7) {
		World world = human.world;
		cleanDroplist(item);

		for (int var9 = -1; var9 <= 1; ++var9) {
			int nx = x;
			int ny = y;
			int nz = z;

			if (var9 == 0) continue;
			if ((var7 != 0.0D) && (var7 != 1.0D)) {
				ny = y + var9;
			} else if ((EEBase.heading(human) != 2.0D) && (EEBase.heading(human) != 4.0D)) {
				nx = x + var9;
			} else {
				nz = z + var9;
			}

			int id = world.getTypeId(nx, ny, nz);
			int data = world.getData(nx, ny, nz);

			if (canBreak(id, data) && attemptBreak(human, nx, ny, nz))
				scanBlockAndBreak(world, item, nx, ny, nz);
		}

		ejectDropList(world, item, x, y + 0.5D, z);
	}

	public void doMegaImpact(EntityHuman human, ItemStack item, int x, int y, int z, double var6) {
		World world = human.world;
		cleanDroplist(item);

		for (int var8 = -1; var8 <= 1; ++var8) {
			for (int var9 = -1; var9 <= 1; ++var9) {
				int nx = x;
				int ny = y;
				int nz = z;

				if ((var8 == 0) && (var9 == 0)) continue;
				if ((var6 != 0.0D) && (var6 != 1.0D)) {
					if ((var6 != 2.0D) && (var6 != 4.0D)) {
						if ((var6 == 3.0D) || (var6 == 5.0D)) {
							ny = y + var8;
							nz = z + var9;
						}
					} else {
						nx = x + var8;
						ny = y + var9;
					}
				} else {
					nx = x + var8;
					nz = z + var9;
				}

				int id = world.getTypeId(nx, ny, nz);
				int data = world.getData(nx, ny, nz);

				if (canBreak(id, data) && attemptBreak(human, nx, ny, nz))
					scanBlockAndBreak(world, item, nx, ny, nz);
			}

		}

		ejectDropList(world, item, x, y + 0.5D, z);
	}

	public void scanBlockAndBreak(World world, ItemStack item, int x, int y, int z) {
		int id = world.getTypeId(x, y, z);
		int data = world.getData(x, y, z);
		ArrayList<ItemStack> var8 = Block.byId[id].getBlockDropped(world, x, y, z, data, 0);
		Iterator<ItemStack> var9 = var8.iterator();

		while (var9.hasNext()) {
			ItemStack var10 = var9.next();
			addToDroplist(item, var10);
		}

		world.setTypeId(x, y, z, 0);

		if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y, z, 0.0D, 0.0D, 0.0D);
		if (world.random.nextInt(8) == 0) world.a("explode", x, y, z, 0.0D, 0.0D, 0.0D);
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return var1 != Block.OBSIDIAN ? var1 == Block.DIAMOND_BLOCK || var1 == Block.DIAMOND_ORE ? true
				: var1 == Block.GOLD_BLOCK || var1 == Block.GOLD_ORE ? true : var1 == Block.IRON_BLOCK || var1 == Block.IRON_ORE ? true
						: var1 == Block.LAPIS_BLOCK || var1 == Block.LAPIS_ORE ? true : var1 == Block.REDSTONE_ORE || var1 == Block.GLOWING_REDSTONE_ORE ? true
								: var1.material != Material.STONE ? var1.material == Material.ORE : true : true;
	}

	public boolean canBreak(int id, int data) {
		if (Block.byId[id] == null) return false;
		
		if (!Block.byId[id].hasTileEntity(data) && id != Block.BEDROCK.id) {
			if (Block.byId[id].material == null) return false;
			
			for (int var3 = 0; var3 < blocksEffectiveAgainst.length; var3++) {
				if (blocksEffectiveAgainst[var3] != null && id == blocksEffectiveAgainst[var3].id) return true;
			}
			
			Material mat = Block.byId[id].material;
			if (mat == Material.STONE) return true;
			
			if (mat != Material.GRASS && mat != Material.EARTH && mat != Material.SAND && mat != Material.SNOW_LAYER && mat != Material.CLAY) {
				return canDestroySpecialBlock(Block.byId[id]);
			}
			
			return true;
		}
		return false;
	}

	public void doPickaxeBreak(ItemStack item, World var2, EntityHuman human) {
		if (chargeLevel(item) <= 0) return;
		
		int x = (int) EEBase.playerX(human);
		int y = (int) EEBase.playerY(human);
		int z = (int) EEBase.playerZ(human);

		for (int var7 = -2; var7 <= 2; ++var7) {
			for (int var8 = -2; var8 <= 2; ++var8) {
				for (int var9 = -2; var9 <= 2; ++var9) {
					int id = var2.getTypeId(x + var7, y + var8, z + var9);

					if (!(isOre(id))) continue;
					startSearchPick(var2, item, human, id, x + var7, y + var8, z + var9, true);
				}
			}
		}
	}

	private boolean isOre(int id) {
		return EEMaps.isOreBlock(id);
	}

	public void doAlternate(ItemStack item, World var2, EntityHuman human) {
		int mode = EEBase.getToolMode(human);
		boolean hmode = EEBase.getHammerMode(human);
		if (mode == 0 && hmode) {
			//if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.ALTERNATE, human, EEAction2.UpdateToolMode))) return;
			EEBase.updateToolMode(human);
			EEBase.updateHammerMode(human, false);
		} else if (mode == 0 && !hmode) {
			//Note, This can block switching mode completely
			//if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.ALTERNATE, human, EEAction2.UpdateHammerMode))) return;
			EEBase.updateHammerMode(human, true);
		} else {
			//if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.ALTERNATE, human, EEAction2.UpdateToolMode))) return;
			EEBase.updateToolMode(human);
		}
	}

	public void startSearchPick(World world, ItemStack item, EntityHuman human, int id, int x, int y, int z, boolean var8) {
		if (id == Block.BEDROCK.id) return;

		world.makeSound(human, "flash", 0.8F, 1.5F);

		if (var8) human.C_();
		
		doBreakPick(world, item, human, id, x, y, z);
	}

	public void startSearchShovel(World world, EntityHuman human, ItemStack item, int id, int x, int y, int z, boolean var8) {
		world.makeSound(human, "flash", 0.8F, 1.5F);

		if (var8) human.C_();
		
		doBreakShovel(world, human, item, id, x, y, z);
	}

	public void doBreakPick(World world, ItemStack item, EntityHuman human, int id, int x, int y, int z) {
		if (!attemptBreak(human, x, y, z)) return;
		scanBlockAndBreak(world, item, x, y, z);
		for (int var8 = -1; var8 <= 1; var8++) {
			for (int var9 = -1; var9 <= 1; var9++) {
				for (int var10 = -1; var10 <= 1; var10++) {
					int id2 = world.getTypeId(x + var8, y + var9, z + var10);
					if (id != Block.REDSTONE_ORE.id && id != Block.GLOWING_REDSTONE_ORE.id) {
						if (id2 == id) doBreakPick(world, item, human, id, x + var8, y + var9, z + var10);
					} else if (id2 == Block.GLOWING_REDSTONE_ORE.id || id2 == Block.REDSTONE_ORE.id)
						doBreakPick(world, item, human, id, x + var8, y + var9, z + var10);
				}

			}

		}

		ejectDropList(world, item, EEBase.playerX(human), EEBase.playerY(human), EEBase.playerZ(human));
	}

	public void doBreakShovel(World world, EntityHuman human, ItemStack item, int id, int x, int y, int z) {
		if (getFuelRemaining(item) < 1) ConsumeReagent(item, human, false);
		

		if (getFuelRemaining(item) <= 0) return;
		int var8 = world.getData(x, y, z);
		ArrayList<ItemStack> var9 = Block.byId[id].getBlockDropped(world, x, y, z, var8, 0);
		Iterator<ItemStack> var10 = var9.iterator();

		while (var10.hasNext()) {
			ItemStack var11 = var10.next();
			addToDroplist(item, var11);
		}

		world.setTypeId(x, y, z, 0);
		setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);

		for (int var14 = -1; var14 <= 1; ++var14) {
			for (int var13 = -1; var13 <= 1; ++var13) {
				for (int var12 = -1; var12 <= 1; ++var12) {
					if (world.getTypeId(x + var14, y + var13, z + var12) != id) continue;
					doBreakShovelAdd(world, human, item, id, x + var14, y + var13, z + var12);
				}
			}

		}

		if (world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
		if (world.random.nextInt(8) == 0) world.a("explode", x, y + 1, z, 0.0D, 0.0D, 0.0D);
		ejectDropList(world, item, x, y, z);
	}

	public void doBreakShovelAdd(World var1, EntityHuman var2, ItemStack var3, int id, int x, int y, int z) {
		if (!attemptBreak(var2, x, y, z)) return;
		if (getFuelRemaining(var3) < 1) ConsumeReagent(var3, var2, false);
		if (getFuelRemaining(var3) > 0) {
			int var8 = var1.getData(x, y, z);
			ArrayList<ItemStack> var9 = Block.byId[id].getBlockDropped(var1, x, y, z, var8, 0);
			ItemStack var11;
			for (Iterator<ItemStack> var10 = var9.iterator(); var10.hasNext(); addToDroplist(var3, var11))
				var11 = var10.next();

			var1.setTypeId(x, y, z, 0);
			setShort(var3, "fuelRemaining", getFuelRemaining(var3) - 1);
			for (int var14 = -1; var14 <= 1; var14++) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var12 = -1; var12 <= 1; var12++) {
						if (var1.getTypeId(x + var14, y + var13, z + var12) == id)
							doBreakShovelAdd(var1, var2, var3, id, x + var14, y + var13, z + var12);
					}

				}

			}

			if (var1.random.nextInt(8) == 0) var1.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			if (var1.random.nextInt(8) == 0) var1.a("explode", x, y + 1, z, 0.0D, 0.0D, 0.0D);
		}
	}

	public void doRelease(ItemStack item, World world, EntityHuman human) {
		if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.RELEASE, human, EEAction2.BreakRadius))) return;
		doBreak(item, world, human);
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int face) {
		if (EEProxy.isClient(world)) return false;

		if (chargeLevel(item) < 1) return false;
		
		cleanDroplist(item);
		int id = world.getTypeId(x, y, z);
		if (isOre(id)){
			if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;
			startSearchPick(world, item, human, id, x, y, z, false);
		} else {
			Material mat = world.getMaterial(x, y, z);
			if (mat == Material.STONE){
				if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.RIGHTCLICK, human, x, y, z, chargeLevel(item)>=2?EEAction2.MegaBreak:EEAction2.BreakRadius))) return false;
				onItemUseHammer(item, human, world, x, y, z, face);
			} else if (mat == Material.EARTH || mat == Material.GRASS || mat == Material.CLAY || mat == Material.SAND || mat == Material.SNOW_LAYER){
				if (EEEventManager.callEvent(new EERedMorningStarEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;
				onItemUseShovel(item, human, world, x, y, z, face);
			}
		}
		return true;
	}

	public boolean onItemUseHammer(ItemStack item, EntityHuman human, World world, int x, int y, int z, int face) {
		boolean var8 = true;
		int charge = chargeLevel(item);
		if (charge < 1) return false;
		
		cleanDroplist(item);
		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);
		for (int var9 = -(charge * (face != 5 ? (byte) (face != 4 ? 1 : 0) : 2)); var9 <= charge * (face != 5 ? face != 4 ? 1 : 2 : 0); var9++) {
			for (int var10 = -(charge * (face != 1 ? (byte) (face != 0 ? 1 : 0) : 2)); var10 <= charge * (face != 1 ? face != 0 ? 1 : 2 : 0); var10++) {
				for (int var11 = -(charge * (face != 3 ? (byte) (face != 2 ? 1 : 0) : 2)); var11 <= charge * (face != 3 ? face != 2 ? 1 : 2 : 0); var11++) {
					int nx = x + var9;
					int ny = y + var10;
					int nz = z + var11;
					int id = world.getTypeId(nx, ny, nz);
					int data = world.getData(nx, ny, nz);
					if (canBreak(id, data) && attemptBreak(human, nx, ny, nz)) {
						if (getFuelRemaining(item) < 1) {
							ConsumeReagent(item, human, var8);
							var8 = false;
						}
						if (getFuelRemaining(item) > 0) {
							ArrayList<ItemStack> var17 = Block.byId[id].getBlockDropped(world, nx, ny, nz, data, 0);
							ItemStack var19;
							for (Iterator<ItemStack> var18 = var17.iterator(); var18.hasNext(); addToDroplist(item, var19))
								var19 = var18.next();

							world.setTypeId(nx, ny, nz, 0);
							if (world.random.nextInt(8) == 0) world.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if (world.random.nextInt(8) == 0) world.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);
						}
					}
				}

			}

		}

		ejectDropList(world, item, x, y, z);
		return false;
	}

	public boolean onItemUseShovel(ItemStack item, EntityHuman human, World world, int x, int y, int z, int face) {
		int charge = chargeLevel(item);
		
		if (charge < 1) return false;
		
		cleanDroplist(item);
		int id = world.getTypeId(x, y, z);
		if (id == Block.GRAVEL.id) {
			startSearchShovel(world, human, item, id, x, y, z, false);
			return true;
		}
		
		
		boolean var19 = true;
		cleanDroplist(item);
		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);
		for (int var9 = -charge; var9 <= charge; var9++) {
			for (int var10 = -charge; var10 <= charge; var10++) {
				int nx = x + var9;
				int nz = z + var10;
				
				if (face == 2) nz += charge;
				else if (face == 3) nz -= charge;
				else if (face == 4) nx += charge;
				else if (face == 5) nx -= charge;
				
				int id2 = world.getTypeId(nx, y, nz);
				int data = world.getData(nx, y, nz);
				if (canBreak(id2, data) && attemptBreak(human, nx, y, nz)) {
					if (getFuelRemaining(item) < 1) {
						if (var9 == charge && var10 == charge) {
							ConsumeReagent(item, human, var19);
							var19 = false;
						} else {
							ConsumeReagent(item, human, false);
						}
					}
					if (getFuelRemaining(item) > 0) {
						ArrayList<ItemStack> var16 = Block.byId[id2].getBlockDropped(world, nx, y, nz, data, 0);
						ItemStack var18;
						for (Iterator<ItemStack> var17 = var16.iterator(); var17.hasNext(); addToDroplist(item, var18))
							var18 = var17.next();

						world.setTypeId(nx, y, nz, 0);
						if (world.random.nextInt(8) == 0) world.a("largesmoke", nx, y, nz, 0.0D, 0.0D, 0.0D);
						if (world.random.nextInt(8) == 0) world.a("explode", nx, y, nz, 0.0D, 0.0D, 0.0D);
					}
				}
			}

		}

		ejectDropList(world, item, x, y, z);
		return true;
	}

	public void doToggle(ItemStack item, World world, EntityHuman human) {}
}
