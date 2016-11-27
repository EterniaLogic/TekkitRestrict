package ee;

import java.util.*;

import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.rm.EERMHammerEvent;

import net.minecraft.server.*;

public class ItemRedHammer extends ItemRedTool {

	protected ItemRedHammer(int var1) {
		super(var1, 3, 14, blocksEffectiveAgainst);
	}

	public boolean a(ItemStack var1, EntityLiving var2, EntityLiving var3) {
		return true;
	}

	public boolean a(ItemStack var1, int unused, int x, int y, int z, EntityLiving var6) {
		if (var6 instanceof EntityHuman) {
			EntityHuman human = (EntityHuman) var6;
			if (EEBase.getHammerMode(human)){
				if (EEEventManager.callEvent(new EERMHammerEvent(var1, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.MegaBreak))) return true;
				doMegaImpact(human, var1, x, y, z, EEBase.direction(human));
			}
			return true;
		}

		return true;
	}

	public float getStrVsBlock(ItemStack var1, Block var2, int var3) {
		float var4 = 1.0F;
		return var2.material != Material.STONE || chargeLevel(var1) <= 0 ? var2.material != Material.STONE ? super.getDestroySpeed(var1, var2) / var4 : 14F / var4 : 30F / var4;
	}

	public void scanBlockAndBreak(World var1, ItemStack var2, int var3, int var4, int var5) {
		int var6 = var1.getTypeId(var3, var4, var5);
		int var7 = var1.getData(var3, var4, var5);
		ArrayList<ItemStack> var8 = Block.byId[var6].getBlockDropped(var1, var3, var4, var5, var7, 0);
		ItemStack var10;
		for (Iterator<ItemStack> var9 = var8.iterator(); var9.hasNext(); addToDroplist(var2, var10))
			var10 = var9.next();

		var1.setTypeId(var3, var4, var5, 0);
		if (var1.random.nextInt(8) == 0) var1.a("largesmoke", var3, var4, var5, 0.0D, 0.0D, 0.0D);
		if (var1.random.nextInt(8) == 0) var1.a("explode", var3, var4, var5, 0.0D, 0.0D, 0.0D);
	}

	public boolean canBreak(int var1, int var2) {
		if (Block.byId[var1] == null) return false;
		if (!Block.byId[var1].b()) return false;
		if (!Block.byId[var1].hasTileEntity(var2) && var1 != Block.BEDROCK.id) {
			if (Block.byId[var1].material == null) return false;
			if (Block.byId[var1].material == Material.STONE) return true;
			for (int var3 = 0; var3 < blocksEffectiveAgainst.length; var3++)
				if (var1 == blocksEffectiveAgainst[var3].id) return true;

			return false;
		} else {
			return false;
		}
	}

	public void doMegaImpact(EntityHuman human, ItemStack var2, int x, int y, int z, double var6) {
		World var1 = human.world;
		cleanDroplist(var2);
		for (int var8 = -1; var8 <= 1; var8++) {
			for (int var9 = -1; var9 <= 1; var9++) {
				int nx = x;
				int ny = y;
				int nz = z;
				if (var8 != 0 || var9 != 0) {
					if (var6 != 0.0D && var6 != 1.0D) {
						if (var6 != 2D && var6 != 4D) {
							if (var6 == 3D || var6 == 5D) {
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
					int var13 = var1.getTypeId(nx, ny, nz);
					int var14 = var1.getData(nx, ny, nz);
					if (canBreak(var13, var14) && attemptBreak(human, nx, ny, nz)) scanBlockAndBreak(var1, var2, nx, ny, nz);
				}
			}

		}

		ejectDropList(var1, var2, x, y + 0.5D, z);
	}

	public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int x, int y, int z, int var7) {
		if (EEProxy.isClient(var3)) return false;
		
		if (EEEventManager.callEvent(new EERMHammerEvent(var1, EEAction.RIGHTCLICK, var2, x, y, z, EEAction2.BreakRadius))) return false;
		
		int charge = chargeLevel(var1);
		if (charge < 1) return false;
		
		boolean var8 = true;
		
		cleanDroplist(var1);
		var2.C_();
		var3.makeSound(var2, "flash", 0.8F, 1.5F);
		for (int var9 = -(charge * (var7 != 5 ? (byte) (var7 != 4 ? 1 : 0) : 2)); var9 <= charge * (var7 != 5 ? var7 != 4 ? 1 : 2 : 0); var9++) {
			for (int var10 = -(charge * (var7 != 1 ? (byte) (var7 != 0 ? 1 : 0) : 2)); var10 <= charge * (var7 != 1 ? var7 != 0 ? 1 : 2 : 0); var10++) {
				for (int var11 = -(charge * (var7 != 3 ? (byte) (var7 != 2 ? 1 : 0) : 2)); var11 <= charge * (var7 != 3 ? var7 != 2 ? 1 : 2 : 0); var11++) {
					int nx = x + var9;
					int ny = y + var10;
					int nz = z + var11;
					int id = var3.getTypeId(nx, ny, nz);
					int data = var3.getData(nx, ny, nz);
					if (canBreak(id, data) && attemptBreak(var2, nx, ny, nz)) {
						if (getFuelRemaining(var1) < 1) {
							ConsumeReagent(var1, var2, var8);
							var8 = false;
						}
						if (getFuelRemaining(var1) > 0) {
							ArrayList<ItemStack> var17 = Block.byId[id].getBlockDropped(var3, nx, ny, nz, data, 0);
							ItemStack var19;
							for (Iterator<ItemStack> var18 = var17.iterator(); var18.hasNext(); addToDroplist(var1, var19))
								var19 = var18.next();

							var3.setTypeId(nx, ny, nz, 0);
							if (var3.random.nextInt(8) == 0) var3.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if (var3.random.nextInt(8) == 0) var3.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);
						}
					}
				}

			}

		}

		ejectDropList(var3, var1, x, y, z);
		return false;
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return var1 != Block.OBSIDIAN ? var1 == Block.DIAMOND_BLOCK || var1 == Block.DIAMOND_ORE ? true : var1 == Block.GOLD_BLOCK || var1 == Block.GOLD_ORE ? true : var1 == Block.IRON_BLOCK || var1 == Block.IRON_ORE ? true : var1 == Block.LAPIS_BLOCK || var1 == Block.LAPIS_ORE ? true : var1 == Block.REDSTONE_ORE || var1 == Block.GLOWING_REDSTONE_ORE ? true : var1.material != Material.STONE ? var1.material == Material.ORE : true : true;
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		//if (EEEventManager.callEvent(new EERMHammerEvent(var1, EEAction.ALTERNATE, var3, EEAction2.UpdateHammerMode))) return;
		EEBase.updateHammerMode(var3, true);
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	public static boolean breakMode;
	@SuppressWarnings("unused")
	private boolean haltImpact;
	private static Block blocksEffectiveAgainst[];

	static {
		blocksEffectiveAgainst = (new Block[] { Block.COBBLESTONE, Block.STONE, Block.SANDSTONE, Block.MOSSY_COBBLESTONE, Block.IRON_ORE, Block.IRON_BLOCK,
				Block.COAL_ORE, Block.GOLD_BLOCK, Block.GOLD_ORE, Block.DIAMOND_ORE, Block.DIAMOND_BLOCK, Block.REDSTONE_ORE, Block.GLOWING_REDSTONE_ORE,
				Block.ICE, Block.NETHERRACK, Block.LAPIS_ORE, Block.LAPIS_BLOCK, Block.OBSIDIAN });
	}
}