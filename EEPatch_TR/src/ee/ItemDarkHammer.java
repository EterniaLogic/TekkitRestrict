package ee;

import java.util.*;

import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.dm.EEDMHammerEvent;

import net.minecraft.server.*;

public class ItemDarkHammer extends ItemDarkTool {

	protected ItemDarkHammer(int var1) {
		super(var1, 2, 12, blocksEffectiveAgainst);
	}

	public float getStrVsBlock(ItemStack var1, Block var2, int var3) {
		float var4 = 1.0F;
		return var2.material != Material.STONE || chargeLevel(var1) <= 0 ? var2.material != Material.STONE ? super.getDestroySpeed(var1, var2) / var4
				: 14F / var4 : 30F / var4;
	}

	public boolean a(ItemStack var1, EntityLiving var2, EntityLiving var3) {
		return true;
	}

	public boolean a(ItemStack hammer, int var2, int x, int y, int z, EntityLiving entity) {
		if (!(entity instanceof EntityHuman)) return true;
		
		EntityHuman human = (EntityHuman) entity;
		
		if (EEBase.getHammerMode(human)){
			if (EEEventManager.callEvent(new EEDMHammerEvent(hammer, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.MegaBreak))) return true;
			doMegaImpact(human, hammer, x, y, z, EEBase.direction(human));
		}
		return true;
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

	public boolean canBreak(int id, int var2) {
		if (Block.byId[id] == null) return false;
		
		if (!Block.byId[id].b()) return false;
		if (!Block.byId[id].hasTileEntity(var2) && id != Block.BEDROCK.id) {
			if (Block.byId[id].material == null) return false;
			if (Block.byId[id].material == Material.STONE) return true;
			for (int var3 = 0; var3 < blocksEffectiveAgainst.length; var3++)
				if (id == blocksEffectiveAgainst[var3].id) return true;

			return false;
		} else {
			return false;
		}
	}

	public void doMegaImpact(EntityHuman human, ItemStack hammer, int x, int y, int z, double var6) {
		World world = human.world;
		cleanDroplist(hammer);
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
					int id = world.getTypeId(nx, ny, nz);
					int data = world.getData(nx, ny, nz);
					if (canBreak(id, data) && attemptBreak(human, nx, ny, nz)) scanBlockAndBreak(world, hammer, nx, ny, nz);
				}
			}

		}

		ejectDropList(world, hammer, x, y + 0.5D, z);
	}

	public boolean interactWith(ItemStack hammer, EntityHuman human, World world, int x, int y, int z, int var7) {
		if (EEProxy.isClient(world)) return false;
		
		if (EEEventManager.callEvent(new EEDMHammerEvent(hammer, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return true;
		
		int charge = chargeLevel(hammer);
		if (charge <= 0) return false;
		boolean var8 = true;
		
		cleanDroplist(hammer);
		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);
		
		
		for (int var9 = -(charge * (var7 != 5 ? (byte) (var7 != 4 ? 1 : 0) : 2)); var9 <= charge
				* (var7 != 5 ? var7 != 4 ? 1 : 2 : 0); var9++) {
			for (int var10 = -(charge * (var7 != 1 ? (byte) (var7 != 0 ? 1 : 0) : 2)); var10 <= charge
					* (var7 != 1 ? var7 != 0 ? 1 : 2 : 0); var10++) {
				for (int var11 = -(charge * (var7 != 3 ? (byte) (var7 != 2 ? 1 : 0) : 2)); var11 <= charge
						* (var7 != 3 ? var7 != 2 ? 1 : 2 : 0); var11++) {
					int nx = x + var9;
					int ny = y + var10;
					int nz = z + var11;
					int id = world.getTypeId(nx, ny, nz);
					int data = world.getData(nx, ny, nz);
					if (canBreak(id, data) && attemptBreak(human, nx, ny, nz)) {
						if (getFuelRemaining(hammer) < 1) {
							ConsumeReagent(hammer, human, var8);
							var8 = false;
						}
						if (getFuelRemaining(hammer) > 0) {
							ArrayList<ItemStack> var17 = Block.byId[id].getBlockDropped(world, nx, ny, nz, data, 0);
							ItemStack var19;
							for (Iterator<ItemStack> var18 = var17.iterator(); var18.hasNext(); addToDroplist(hammer, var19))
								var19 = var18.next();

							world.setTypeId(nx, ny, nz, 0);
							if (world.random.nextInt(8) == 0) world.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if (world.random.nextInt(8) == 0) world.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							setShort(hammer, "fuelRemaining", getFuelRemaining(hammer) - 1);
						}
					}
				}

			}

		}

		ejectDropList(world, hammer, x, y, z);
		return false;
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return var1 != Block.OBSIDIAN ? var1 == Block.DIAMOND_BLOCK || var1 == Block.DIAMOND_ORE ? true
				: var1 == Block.GOLD_BLOCK || var1 == Block.GOLD_ORE ? true : var1 == Block.IRON_BLOCK || var1 == Block.IRON_ORE ? true
						: var1 == Block.LAPIS_BLOCK || var1 == Block.LAPIS_ORE ? true : var1 == Block.REDSTONE_ORE || var1 == Block.GLOWING_REDSTONE_ORE ? true
								: var1.material != Material.STONE ? var1.material == Material.ORE : true : true;
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EEDMHammerEvent(var1, EEAction.RIGHTCLICK, var3, EEAction2.BreakRadius))) return;
		doBreak(var1, var2, var3);
	}

	/** Toggle the hammer mode */
	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		//if (EEEventManager.callEvent(new EEDMHammerEvent(var1, EEAction.RIGHTCLICK, var3, EEAction2.UpdateHammerMode))) return;
		EEBase.updateHammerMode(var3, true);
	}

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