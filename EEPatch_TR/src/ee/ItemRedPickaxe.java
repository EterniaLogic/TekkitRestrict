package ee;

import java.util.*;

import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.rm.EERMPickaxeEvent;
import net.minecraft.server.*;

public class ItemRedPickaxe extends ItemRedTool {

	protected ItemRedPickaxe(int var1) {
		super(var1, 3, 6, blocksEffectiveAgainst);
	}

	public float getStrVsBlock(ItemStack var1, Block var2, int var3) {
		float var4 = 1.0F;
		if (var2.id == EEBlock.eePedestal.id && var3 == 0 || var2.id == EEBlock.eeStone.id && (var3 == 8 || var3 == 9)){
			return 1200000F / var4;
		} else {
			if (var2.material != Material.STONE && var2.material != Material.ORE || chargeLevel(var1) <= 0){
				if (var2.material == Material.STONE || var2.material == Material.ORE){
					return 16F / var4;
				}
				else{
					return super.getDestroySpeed(var1, var2) / var4;
				}
			} else {
				return 16F + (16F * chargeLevel(var1)) / var4;
			}
		}
		
		/*
		return var2.id == EEBlock.eePedestal.id && var3 == 0 || var2.id == EEBlock.eeStone.id && (var3 == 8 || var3 == 9) ? 1200000F / var4 :
			var2.material != Material.STONE && var2.material != Material.ORE || chargeLevel(var1) <= 0 ? var2.material == Material.STONE || var2.material == Material.ORE ? 16F / var4 :
				super.getDestroySpeed(var1, var2) / var4 :
					16F + (16F * chargeLevel(var1)) / var4;
		*/
	}

	public boolean a(ItemStack item, int unused, int x, int y, int z, EntityLiving var6) {
		if (!(var6 instanceof EntityHuman)) return true;
		
		EntityHuman human = (EntityHuman) var6;
		int mode = EEBase.getToolMode(human);
		if (mode == 1){
			if (EEEventManager.callEvent(new EERMPickaxeEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.TallBreak))) return true;
			doTallImpact(human, item, x, y, z, EEBase.direction(human));
		} else if (mode == 2){
			if (EEEventManager.callEvent(new EERMPickaxeEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.WideBreak))) return true;
			doWideImpact(human, item, x, y, z, EEBase.heading(human));
		} else if (mode == 3){
			if (EEEventManager.callEvent(new EERMPickaxeEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.LongBreak))) return true;
			doLongImpact(human, item, x, y, z, EEBase.direction(human));
		}
		
		return true;
	}

	public void doLongImpact(EntityHuman human, ItemStack item, int x, int y, int z, double var6) {
		World world = human.world;
		cleanDroplist(item);
		for (int var8 = 1; var8 <= 2; var8++) {
			int nx = x;
			int ny = y;
			int nz = z;
			
			if (var6 == 0.0D) ny = y - var8;
			else if (var6 == 1.0D) ny += var8;
			else if (var6 == 2D) nz = z + var8;
			else if (var6 == 3D) nx = x - var8;
			else if (var6 == 4D) nz -= var8;
			else if (var6 == 5D) nx += var8;
			
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
		for (int var8 = -1; var8 <= 1; var8++) {
			if (var8 == 0) continue;
			
			int nx = x;
			int nz = z;
			
			if (var6 != 2D && var6 != 4D) nz = z + var8;
			else nx = x + var8;
			
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
		for (int var9 = -1; var9 <= 1; var9++) {
			if (var9 == 0) continue;
			int nx = x;
			int ny = y;
			int nz = z;
			
			if (var7 != 0.0D && var7 != 1.0D)
				ny = y + var9;
			else if (EEBase.heading(human) != 2D && EEBase.heading(human) != 4D)
				nx = x + var9;
			else
				nz = z + var9;
			
			int id = world.getTypeId(nx, ny, nz);
			int data = world.getData(nx, ny, nz);
			if (canBreak(id, data) && attemptBreak(human, nx, ny, nz))
				scanBlockAndBreak(world, item, nx, ny, nz);
		
		}

		ejectDropList(world, item, x, y + 0.5D, z);
	}

	public void doBreak(ItemStack item, World world, EntityHuman human) {
		if (chargeLevel(item) > 0) {
			int x = (int) EEBase.playerX(human);
			int y = (int) EEBase.playerY(human);
			int z = (int) EEBase.playerZ(human);
			for (int var7 = -2; var7 <= 2; var7++) {
				for (int var8 = -2; var8 <= 2; var8++) {
					for (int var9 = -2; var9 <= 2; var9++) {
						int id = world.getTypeId(x + var7, y + var8, z + var9);
						if (isOre(id))
							startSearch(world, item, human, id, x + var7, y + var8, z + var9, true);
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

	public boolean canBreak(int id, int data) {
		if (Block.byId[id] == null) return false;
		if (!Block.byId[id].b()) return false;
		if (!Block.byId[id].hasTileEntity(data) && id != Block.BEDROCK.id) {
			if (Block.byId[id].material == null) return false;
			for (int var3 = 0; var3 < blocksEffectiveAgainst.length; var3++)
				if (id == blocksEffectiveAgainst[var3].id) return true;

			return canDestroySpecialBlock(Block.byId[id]);
		} else {
			return false;
		}
	}

	public boolean interactWith(ItemStack var1, EntityHuman human, World world, int x, int y, int z, int var7) {
		if (EEProxy.isClient(world)) return false;
		
		if (chargeLevel(var1) >= 1) {
			cleanDroplist(var1);
			int id = world.getTypeId(x, y, z);
			
			if (isOre(id)) {
				if (EEEventManager.callEvent(new EERMPickaxeEvent(var1, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;
				startSearch(world, var1, human, id, x, y, z, false);
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean isOre(int id) {
		return EEMaps.isOreBlock(id);
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) {
			return item;
		} else {
			if (EEEventManager.callEvent(new EERMPickaxeEvent(item, EEAction.RIGHTCLICK, human, EEAction2.BreakRadius))) return item;
			doBreak(item, world, human);
			return item;
		}
	}

	public void startSearch(World var1, ItemStack var2, EntityHuman human, int id, int x, int y, int z, boolean var8) {
		if (id == Block.BEDROCK.id) return;

		var1.makeSound(human, "flash", 0.8F, 1.5F);
		if (var8) human.C_();
		doBreakS(var1, var2, human, id, x, y, z);
	
	}

	public void doBreakS(World world, ItemStack item, EntityHuman human, int var4, int x, int y, int z) {
		if (!attemptBreak(human, x, y, z)) return;
		scanBlockAndBreak(world, item, x, y, z);
		for (int var8 = -1; var8 <= 1; var8++) {
			for (int var9 = -1; var9 <= 1; var9++) {
				for (int var10 = -1; var10 <= 1; var10++) {
					int id = world.getTypeId(x + var8, y + var9, z + var10);
					if (var4 != Block.REDSTONE_ORE.id && var4 != Block.GLOWING_REDSTONE_ORE.id) {
						if (id == var4)
							doBreakS(world, item, human, var4, x + var8, y + var9, z + var10);
					} else if (id == Block.GLOWING_REDSTONE_ORE.id || id == Block.REDSTONE_ORE.id)
						doBreakS(world, item, human, var4, x + var8, y + var9, z + var10);
				}

			}

		}

		ejectDropList(world, item, EEBase.playerX(human), EEBase.playerY(human), EEBase.playerZ(human));
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return var1 != Block.OBSIDIAN ? var1 == Block.DIAMOND_BLOCK || var1 == Block.DIAMOND_ORE ? true
				: var1 == Block.GOLD_BLOCK || var1 == Block.GOLD_ORE ? true : var1 == Block.IRON_BLOCK || var1 == Block.IRON_ORE ? true
						: var1 == Block.LAPIS_BLOCK || var1 == Block.LAPIS_ORE ? true : var1 == Block.REDSTONE_ORE || var1 == Block.GLOWING_REDSTONE_ORE ? true
								: var1.material != Material.STONE ? var1.material == Material.ORE : true : true;
	}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EERMPickaxeEvent(var1, EEAction.RELEASE, var3, EEAction2.BreakRadius))) return;
		doBreak(var1, var2, var3);
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		//if (EEEventManager.callEvent(new EERMPickaxeEvent(var1, EEAction.ALTERNATE, var3, EEAction2.UpdateToolMode))) return;
		EEBase.updateToolMode(var3);
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	private static Block blocksEffectiveAgainst[];
	
	static {
		blocksEffectiveAgainst = (new Block[] { Block.COBBLESTONE, Block.DOUBLE_STEP, Block.STEP, Block.STONE, Block.SANDSTONE, Block.MOSSY_COBBLESTONE,
				Block.IRON_ORE, Block.IRON_BLOCK, Block.COAL_ORE, Block.GOLD_BLOCK, Block.GOLD_ORE, Block.DIAMOND_ORE, Block.DIAMOND_BLOCK, Block.REDSTONE_ORE,
				Block.GLOWING_REDSTONE_ORE, Block.ICE, Block.NETHERRACK, Block.LAPIS_ORE, Block.LAPIS_BLOCK, Block.OBSIDIAN });
	}
}
