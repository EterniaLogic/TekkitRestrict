package ee;

import java.util.*;

import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.dm.EEDMPickaxeEvent;

import net.minecraft.server.*;

public class ItemDarkPickaxe extends ItemDarkTool {

	protected ItemDarkPickaxe(int var1) {
		super(var1, 2, 6, blocksEffectiveAgainst);
	}

	public float getStrVsBlock(ItemStack var1, Block var2, int var3) {
		float var4 = 1.0F;
		return var2.id == EEBlock.eePedestal.id && var3 == 0 || var2.id == EEBlock.eeStone.id && var3 == 8 ? 1200000F / var4 : var2.material != Material.STONE
				&& var2.material != Material.ORE || chargeLevel(var1) <= 0 ? var2.material == Material.STONE || var2.material == Material.ORE ? 12F / var4
				: super.getDestroySpeed(var1, var2) / var4 : 12F + (12F * chargeLevel(var1)) / var4;
	}

	public boolean a(ItemStack var1, int var2, int x, int y, int z, EntityLiving var6) {
		if (!(var6 instanceof EntityHuman)) return true;
		
		EntityHuman human = (EntityHuman) var6;
		int mode = EEBase.getToolMode(human);
		
		if (mode != 0) {
			if (mode == 1){
				if (EEEventManager.callEvent(new EEDMPickaxeEvent(var1, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.TallBreak))) return true;
				doTallImpact(human, var1, x, y, z, EEBase.direction(human));
			}
			else if (mode == 2){
				if (EEEventManager.callEvent(new EEDMPickaxeEvent(var1, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.WideBreak))) return true;
				doWideImpact(human, var1, x, y, z, EEBase.heading(human));
			}
			else if (mode == 3){
				if (EEEventManager.callEvent(new EEDMPickaxeEvent(var1, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.LongBreak))) return true;
				doLongImpact(human, var1, x, y, z, EEBase.direction(human));
			}
		}
		
		return true;
	}

	public void doLongImpact(EntityHuman human, ItemStack var2, int x, int y, int z, double face) {
		World var1 = human.world;
		cleanDroplist(var2);
		for (int var8 = 1; var8 <= 2; var8++) {
			int nx = x;
			int ny = y;
			int nz = z;
			if (face == 0.0D)
				ny = y - var8;
			else if (face == 1.0D)
				ny += var8;
			else if (face == 2D)
				nz = z + var8;
			else if (face == 3D)
				nx = x - var8;
			else if (face == 4D)
				nz -= var8;
			else if (face == 5D)
				nx += var8;
			
			int var12 = var1.getTypeId(nx, ny, nz);
			int var13 = var1.getData(nx, ny, nz);
			if (canBreak(var12, var13) && attemptBreak(human, nx, ny, nz)) scanBlockAndBreak(var1, var2, nx, ny, nz);
		}

		ejectDropList(var1, var2, x, y + 0.5D, z);
	}

	public void doWideImpact(EntityHuman ply, ItemStack var2, int x, int y, int z, double var6) {
		World var1 = ply.world;
		cleanDroplist(var2);
		for (int var8 = -1; var8 <= 1; var8++) {
			int nx = x;
			int nz = z;
			if (var8 != 0) {
				if (var6 != 2D && var6 != 4D)
					nz = z + var8;
				else
					nx = x + var8;
				int var12 = var1.getTypeId(nx, y, nz);
				int var13 = var1.getData(nx, y, nz);
				if (canBreak(var12, var13) && attemptBreak(ply, nx, y, nz)) scanBlockAndBreak(var1, var2, nx, y, nz);
			}
		}

		ejectDropList(var1, var2, x, y + 0.5D, z);
	}

	public void doTallImpact(EntityHuman ply, ItemStack var2, int x, int y, int z, double var7) {
		World var1 = ply.world;
		cleanDroplist(var2);
		for (int var9 = -1; var9 <= 1; var9++) {
			int nx = x;
			int ny = y;
			int nz = z;
			if (var9 != 0) {
				if (var7 != 0.0D && var7 != 1.0D)
					ny = y + var9;
				else if (EEBase.heading(ply) != 2D && EEBase.heading(ply) != 4D)
					nx = x + var9;
				else
					nz = z + var9;
				int id = var1.getTypeId(nx, ny, nz);
				int data = var1.getData(nx, ny, nz);
				if (canBreak(id, data) && attemptBreak(ply, nx, ny, nz)) scanBlockAndBreak(var1, var2, nx, ny, nz);
			}
		}

		ejectDropList(var1, var2, x, y + 0.5D, z);
	}

	public void doBreak(ItemStack var1, World world, EntityHuman human) {
		if (chargeLevel(var1) > 0) {
			int x = (int) EEBase.playerX(human);
			int y = (int) EEBase.playerY(human);
			int z = (int) EEBase.playerZ(human);
			for (int var7 = -2; var7 <= 2; var7++) {
				for (int var8 = -2; var8 <= 2; var8++) {
					for (int var9 = -2; var9 <= 2; var9++) {
						int id = world.getTypeId(x + var7, y + var8, z + var9);
						if (isOre(id)) startSearch(world, var1, human, id, x + var7, y + var8, z + var9, true);
					}

				}

			}

		}
	}

	public void scanBlockAndBreak(World var1, ItemStack var2, int x, int y, int z) {
		int id = var1.getTypeId(x, y, z);
		int data = var1.getData(x, y, z);
		ArrayList<ItemStack> var8 = Block.byId[id].getBlockDropped(var1, x, y, z, data, 0);
		ItemStack var10;
		for (Iterator<ItemStack> var9 = var8.iterator(); var9.hasNext(); addToDroplist(var2, var10))
			var10 = var9.next();

		var1.setTypeId(x, y, z, 0);
		if (var1.random.nextInt(8) == 0) var1.a("largesmoke", x, y, z, 0.0D, 0.0D, 0.0D);
		if (var1.random.nextInt(8) == 0) var1.a("explode", x, y, z, 0.0D, 0.0D, 0.0D);
	}

	public boolean canBreak(int var1, int var2) {
		if (Block.byId[var1] == null) return false;
		if (!Block.byId[var1].b()) return false;
		if (!Block.byId[var1].hasTileEntity(var2) && var1 != Block.BEDROCK.id) {
			if (Block.byId[var1].material == null) return false;
			for (int var3 = 0; var3 < blocksEffectiveAgainst.length; var3++)
				if (var1 == blocksEffectiveAgainst[var3].id) return true;

			return canDestroySpecialBlock(Block.byId[var1]);
		} else {
			return false;
		}
	}

	public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int x, int y, int z, int var7) {
		if (EEProxy.isClient(var3)) return false;
		
		if (EEEventManager.callEvent(new EEDMPickaxeEvent(var1, EEAction.RIGHTCLICK, var2, x, y, z, EEAction2.BreakRadius))) return false;
		
		if (chargeLevel(var1) >= 1) {
			cleanDroplist(var1);
			int id = var3.getTypeId(x, y, z);
			if (isOre(id)) startSearch(var3, var1, var2, id, x, y, z, false);
			return true;
		} else {
			return false;
		}
	}

	private boolean isOre(int var1) {
		return EEMaps.isOreBlock(var1);
	}

	public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) return var1;
		
		if (EEEventManager.callEvent(new EEDMPickaxeEvent(var1, EEAction.RIGHTCLICK, var3, EEAction2.BreakRadius))) return var1;//IMPORTANT is this a rightclick?
		doBreak(var1, var2, var3);
		return var1;
	}

	public void startSearch(World world, ItemStack var2, EntityHuman human, int id, int x, int y, int z, boolean var8) {
		if (id == Block.BEDROCK.id) return;

		world.makeSound(human, "flash", 0.8F, 1.5F);
		if (var8) human.C_();
		doBreakS(world, var2, human, id, x, y, z);
	}

	public void doBreakS(World world, ItemStack var2, EntityHuman human, int id, int x, int y, int z) {
		if (!attemptBreak(human, x, y, z)) return;
		scanBlockAndBreak(world, var2, x, y, z);
		for (int var8 = -1; var8 <= 1; var8++) {
			for (int var9 = -1; var9 <= 1; var9++) {
				for (int var10 = -1; var10 <= 1; var10++) {
					int id2 = world.getTypeId(x + var8, y + var9, z + var10);
					if (id2 == id || id2 == Block.GLOWING_REDSTONE_ORE.id || id2 == Block.REDSTONE_ORE.id)
						doBreakS(world, var2, human, id, x + var8, y + var9, z + var10);

					/*
					if(id != Block.REDSTONE_ORE.id && id != Block.GLOWING_REDSTONE_ORE.id)
					{
						if(id2 == id)
							doBreakS(world, var2, human, id, x + var8, y + var9, z + var10);
					} else if(id2 == Block.GLOWING_REDSTONE_ORE.id || id2 == Block.REDSTONE_ORE.id)
						doBreakS(world, var2, human, id, x + var8, y + var9, z + var10);
					*/

				}

			}

		}

		ejectDropList(world, var2, EEBase.playerX(human), EEBase.playerY(human), EEBase.playerZ(human));
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return var1 != Block.OBSIDIAN ? var1 == Block.DIAMOND_BLOCK || var1 == Block.DIAMOND_ORE ? true
				: var1 == Block.GOLD_BLOCK || var1 == Block.GOLD_ORE ? true : var1 == Block.IRON_BLOCK || var1 == Block.IRON_ORE ? true
						: var1 == Block.LAPIS_BLOCK || var1 == Block.LAPIS_ORE ? true : var1 == Block.REDSTONE_ORE || var1 == Block.GLOWING_REDSTONE_ORE ? true
								: var1.material != Material.STONE ? var1.material == Material.ORE : true : true;
	}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EEDMPickaxeEvent(var1, EEAction.RELEASE, var3, EEAction2.BreakRadius))) return;
		doBreak(var1, var2, var3);
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		//if (EEEventManager.callEvent(new EEDMPickaxeEvent(var1, EEAction.ALTERNATE, var3, EEAction2.UpdateToolMode))) return;
		EEBase.updateToolMode(var3);
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	private static Block blocksEffectiveAgainst[];
	@SuppressWarnings("unused")
	private static int breakMode = 0;

	static {
		blocksEffectiveAgainst = (new Block[] { Block.COBBLESTONE, Block.DOUBLE_STEP, Block.STEP, Block.STONE, Block.SANDSTONE, Block.MOSSY_COBBLESTONE,
				Block.IRON_ORE, Block.IRON_BLOCK, Block.COAL_ORE, Block.GOLD_BLOCK, Block.GOLD_ORE, Block.DIAMOND_ORE, Block.DIAMOND_BLOCK, Block.REDSTONE_ORE,
				Block.GLOWING_REDSTONE_ORE, Block.ICE, Block.NETHERRACK, Block.LAPIS_ORE, Block.LAPIS_BLOCK, Block.OBSIDIAN });
	}
}