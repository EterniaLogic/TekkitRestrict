package ee;

import java.util.*;

import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.dm.EEDMSpadeEvent;

import net.minecraft.server.*;

public class ItemDarkSpade extends ItemDarkTool {

	public ItemDarkSpade(int var1) {
		super(var1, 2, 4, blocksEffectiveAgainst);
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return var1 != Block.SNOW ? var1 == Block.SNOW_BLOCK : true;
	}

	public float getDestroySpeed(ItemStack var1, Block var2) {
		float var3 = 1.0F;
		return super.getDestroySpeed(var1, var2) / var3;
	}

	public boolean a(ItemStack item, int var2, int x, int y, int z, EntityLiving var6) {
		if (!(var6 instanceof EntityHuman)) return true;

		EntityHuman human = (EntityHuman) var6;
		int mode = EEBase.getToolMode(human);
		if (mode != 0)
			if (mode == 1){
				if (EEEventManager.callEvent(new EEDMSpadeEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.TallBreak))) return true;
				doTallImpact(human, item, x, y, z, EEBase.direction(human));
			}
			else if (mode == 2){
				if (EEEventManager.callEvent(new EEDMSpadeEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.WideBreak))) return true;
				doWideImpact(human, item, x, y, z, EEBase.heading(human));
			}
			else if (mode == 3){
				if (EEEventManager.callEvent(new EEDMSpadeEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.LongBreak))) return true;
				doLongImpact(human, item, x, y, z, EEBase.direction(human));
			}
		return true;

	}

	public void scanBlockAndBreak(World var1, ItemStack var2, int x, int y, int z) {
		int var6 = var1.getTypeId(x, y, z);
		int var7 = var1.getData(x, y, z);
		ArrayList<ItemStack> var8 = Block.byId[var6].getBlockDropped(var1, x, y, z, var7, 0);
		ItemStack var10;
		for (Iterator<ItemStack> var9 = var8.iterator(); var9.hasNext(); addToDroplist(var2, var10))
			var10 = var9.next();

		var1.setTypeId(x, y, z, 0);
		if (var1.random.nextInt(8) == 0) var1.a("largesmoke", x, y, z, 0.0D, 0.0D, 0.0D);
		if (var1.random.nextInt(8) == 0) var1.a("explode", x, y, z, 0.0D, 0.0D, 0.0D);
	}

	public boolean canBreak(int var1, int var2) {
		if (Block.byId[var1] == null) return false;
		if (!Block.byId[var1].hasTileEntity(var2) && var1 != Block.BEDROCK.id) {
			if (Block.byId[var1].material == null) return false;
			for (int var3 = 0; var3 < blocksEffectiveAgainst.length; var3++)
				if (var1 == blocksEffectiveAgainst[var3].id) return true;

			return Block.byId[var1].material == Material.GRASS || Block.byId[var1].material == Material.EARTH || Block.byId[var1].material == Material.SAND
					|| Block.byId[var1].material == Material.SNOW_LAYER || Block.byId[var1].material == Material.CLAY;
		} else {
			return false;
		}
	}

	public void doLongImpact(EntityHuman ply, ItemStack var2, int x, int y, int z, double var6) {
		World var1 = ply.world;
		cleanDroplist(var2);
		for (int var8 = 1; var8 <= 2; var8++) {
			int nx = x;
			int ny = y;
			int nz = z;
			if (var6 == 0.0D)
				ny = y - var8;
			else if (var6 == 1.0D)
				ny = y + var8;
			else if (var6 == 2D)
				nz = z + var8;
			else if (var6 == 3D)
				nx = x - var8;
			else if (var6 == 4D)
				nz = z - var8;
			else if (var6 == 5D) nx = x + var8;
			int var12 = var1.getTypeId(nx, ny, nz);
			int var13 = var1.getData(nx, ny, nz);
			if (canBreak(var12, var13) && attemptBreak(ply, nx, ny, nz)) scanBlockAndBreak(var1, var2, nx, ny, nz);
		}

		ejectDropList(var1, var2, x, y + 0.5D, z);
	}

	public void doWideImpact(EntityHuman human, ItemStack var2, int x, int y, int z, double var6) {
		World var1 = human.world;
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
				if (canBreak(var12, var13) && attemptBreak(human, nx, y, nz)) scanBlockAndBreak(var1, var2, nx, y, nz);
			}
		}

		ejectDropList(var1, var2, x, y + 0.5D, z);
	}

	public void doTallImpact(EntityHuman human, ItemStack var2, int x, int y, int z, double var7) {
		World var1 = human.world;
		cleanDroplist(var2);
		for (int var9 = -1; var9 <= 1; var9++) {
			int nx = x;
			int ny = y;
			int nz = z;
			if (var9 != 0) {
				if (var7 != 0.0D && var7 != 1.0D)
					ny = y + var9;
				else if (EEBase.heading(human) != 2D && EEBase.heading(human) != 4D)
					nx = x + var9;
				else
					nz = z + var9;
				int var13 = var1.getTypeId(nx, ny, nz);
				int var14 = var1.getData(nx, ny, nz);
				if (canBreak(var13, var14) && attemptBreak(human, nx, ny, nz)) scanBlockAndBreak(var1, var2, nx, ny, nz);
			}
		}

		ejectDropList(var1, var2, x, y + 0.5D, z);
	}

	/** When you rightclick with this item */
	public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int x, int y, int z, int var7) {
		if (EEProxy.isClient(var3)) return false;
		
		if (EEEventManager.callEvent(new EEDMSpadeEvent(var1, EEAction.RIGHTCLICK, var2, x, y, z, EEAction2.BreakRadius))) return false;
		
		int charge = chargeLevel(var1);
		
		if (charge < 1) return false;
		
		if (charge >= 1) {
			cleanDroplist(var1);
			int var8 = var3.getTypeId(x, y, z);
			if (var8 == Block.GRAVEL.id) {
				startSearch(var3, var2, var1, var8, x, y, z, false);
				return true;
			}
		}
		
		boolean var19 = true;
		cleanDroplist(var1);
		var2.C_();
		var3.makeSound(var2, "flash", 0.8F, 1.5F);
		for (int var9 = -charge; var9 <= charge; var9++) {
			for (int var10 = -charge; var10 <= charge; var10++) {
				int nx = x + var9;
				int nz = z + var10;
				if (var7 == 2)
					nz += charge;
				else if (var7 == 3)
					nz -= charge;
				else if (var7 == 4)
					nx += charge;
				else if (var7 == 5) nx -= charge;
				int id = var3.getTypeId(nx, y, nz);
				int data = var3.getData(nx, y, nz);
				if (canBreak(id, data) && attemptBreak(var2, nx, y, nz)) {
					if (getFuelRemaining(var1) < 1) if (var9 == chargeLevel(var1) && var10 == chargeLevel(var1)) {
						ConsumeReagent(var1, var2, var19);
						var19 = false;
					} else {
						ConsumeReagent(var1, var2, false);
					}
					if (getFuelRemaining(var1) > 0) {
						ArrayList<ItemStack> var16 = Block.byId[id].getBlockDropped(var3, nx, y, nz, data, 0);
						ItemStack var18;
						for (Iterator<ItemStack> var17 = var16.iterator(); var17.hasNext(); addToDroplist(var1, var18))
							var18 = var17.next();

						var3.setTypeId(nx, y, nz, 0);
						if (var3.random.nextInt(8) == 0) var3.a("largesmoke", nx, y, nz, 0.0D, 0.0D, 0.0D);
						if (var3.random.nextInt(8) == 0) var3.a("explode", nx, y, nz, 0.0D, 0.0D, 0.0D);
					}
				}
			}

		}

		ejectDropList(var3, var1, x, y, z);
		return true;
	}

	public void startSearch(World var1, EntityHuman var2, ItemStack var3, int var4, int var5, int var6, int var7, boolean var8) {
		var1.makeSound(var2, "flash", 0.8F, 1.5F);
		if (var8) var2.C_();
		doBreakShovel(var1, var2, var3, var4, var5, var6, var7);
	}

	public void doBreakShovel(World var1, EntityHuman var2, ItemStack var3, int var4, int var5, int var6, int var7) {
		if (!attemptBreak(var2, var5, var6, var7)) return;
		if (getFuelRemaining(var3) < 1) ConsumeReagent(var3, var2, false);
		if (getFuelRemaining(var3) > 0) {
			int var8 = var1.getData(var5, var6, var7);
			ArrayList<ItemStack> var9 = Block.byId[var4].getBlockDropped(var1, var5, var6, var7, var8, 0);
			ItemStack var11;
			for (Iterator<ItemStack> var10 = var9.iterator(); var10.hasNext(); addToDroplist(var3, var11))
				var11 = var10.next();

			var1.setTypeId(var5, var6, var7, 0);
			setShort(var3, "fuelRemaining", getFuelRemaining(var3) - 1);
			for (int var14 = -1; var14 <= 1; var14++) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var12 = -1; var12 <= 1; var12++)
						if (var1.getTypeId(var5 + var14, var6 + var13, var7 + var12) == var4)
							doBreakShovelAdd(var1, var2, var3, var4, var5 + var14, var6 + var13, var7 + var12);

				}

			}

			if (var1.random.nextInt(8) == 0) var1.a("largesmoke", var5, var6 + 1, var7, 0.0D, 0.0D, 0.0D);
			if (var1.random.nextInt(8) == 0) var1.a("explode", var5, var6 + 1, var7, 0.0D, 0.0D, 0.0D);
			ejectDropList(var1, var3, var5, var6, var7);
		}
	}

	public void doBreakShovelAdd(World var1, EntityHuman var2, ItemStack var3, int var4, int var5, int var6, int var7) {
		if (!attemptBreak(var2, var5, var6, var7)) return;
		if (getFuelRemaining(var3) < 1) ConsumeReagent(var3, var2, false);
		if (getFuelRemaining(var3) > 0) {
			int var8 = var1.getData(var5, var6, var7);
			ArrayList<ItemStack> var9 = Block.byId[var4].getBlockDropped(var1, var5, var6, var7, var8, 0);
			ItemStack var11;
			for (Iterator<ItemStack> var10 = var9.iterator(); var10.hasNext(); addToDroplist(var3, var11))
				var11 = var10.next();

			var1.setTypeId(var5, var6, var7, 0);
			setShort(var3, "fuelRemaining", getFuelRemaining(var3) - 1);
			for (int var14 = -1; var14 <= 1; var14++) {
				for (int var13 = -1; var13 <= 1; var13++) {
					for (int var12 = -1; var12 <= 1; var12++)
						if (var1.getTypeId(var5 + var14, var6 + var13, var7 + var12) == var4)
							doBreakShovelAdd(var1, var2, var3, var4, var5 + var14, var6 + var13, var7 + var12);

				}

			}

			if (var1.random.nextInt(8) == 0) var1.a("largesmoke", var5, var6 + 1, var7, 0.0D, 0.0D, 0.0D);
			if (var1.random.nextInt(8) == 0) var1.a("explode", var5, var6 + 1, var7, 0.0D, 0.0D, 0.0D);
		}
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		//if (EEEventManager.callEvent(new EEDMSpadeEvent(var1, EEAction.ALTERNATE, var3, EEAction2.UpdateToolMode))) return;
		EEBase.updateToolMode(var3);
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	private static Block blocksEffectiveAgainst[];

	static {
		blocksEffectiveAgainst = (new Block[] { Block.GRASS, Block.DIRT, Block.SOUL_SAND, Block.SAND, Block.GRAVEL, Block.SNOW, Block.SNOW_BLOCK, Block.CLAY, Block.SOIL });
	}
}