package ee;

import java.util.*;

import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.rm.EERMAxeEvent;

import net.minecraft.server.*;

public class ItemRedAxe extends ItemRedTool {

	protected ItemRedAxe(int var1) {
		super(var1, 3, 12, blocksEffectiveAgainst);
	}

	public float getDestroySpeed(ItemStack var1, Block var2) {
		return var2.material != Material.WOOD ? super.getDestroySpeed(var1, var2) : 18F + chargeLevel(var1) * 2;
	}

	public void doBreak(ItemStack var1, World var2, EntityHuman var3) {
		int charge = chargeLevel(var1);
		if (charge < 1) return;
		
		double x = EEBase.playerX(var3);
		double y = EEBase.playerY(var3);
		double z = EEBase.playerZ(var3);
		boolean var10 = false;
		cleanDroplist(var1);
		if (chargeLevel(var1) < 1) return;
		var3.C_();
		var2.makeSound(var3, "flash", 0.8F, 1.5F);
		for (int var11 = -(charge * 2) + 1; var11 <= charge * 2 - 1; var11++) {
			for (int var12 = charge * 2 + 1; var12 >= -2; var12--) {
				for (int var13 = -(charge * 2) + 1; var13 <= charge * 2 - 1; var13++) {
					int nx = (int) (x + var11);
					int ny = (int) (y + var12);
					int nz = (int) (z + var13);
					int id = var2.getTypeId(nx, ny, nz);
					if ((EEMaps.isWood(id) || EEMaps.isLeaf(id)) && attemptBreak(var3, nx, ny, nz)) {
						if (getFuelRemaining(var1) < 1) {
							if (var11 == charge && var13 == charge) {
								ConsumeReagent(var1, var3, var10);
								var10 = false;
							} else {
								ConsumeReagent(var1, var3, false);
							}
						}
						if (getFuelRemaining(var1) > 0) {
							int var18 = var2.getData(nx, ny, nz);
							ArrayList<ItemStack> var19 = Block.byId[id].getBlockDropped(var2, nx, ny, nz, var18, 0);
							ItemStack var21;
							for (Iterator<ItemStack> var20 = var19.iterator(); var20.hasNext(); addToDroplist(var1, var21))
								var21 = var20.next();

							var2.setTypeId(nx, ny, nz, 0);
							if (!EEMaps.isLeaf(id)) setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);
							if (var2.random.nextInt(8) == 0) var2.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if (var2.random.nextInt(8) == 0) var2.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
						}
					}
				}

			}

		}

		ejectDropList(var2, var1, x, y, z);
	}

	public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) return var1;

		if (EEEventManager.callEvent(new EERMAxeEvent(var1, EEAction.RIGHTCLICK, var3, EEAction2.BreakRadius))) return var1;
		doBreak(var1, var2, var3);
		return var1;
	}

	public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int x, int y, int z, int face) {
		if (EEProxy.isClient(var3)) return false;
		
		if (EEEventManager.callEvent(new EERMAxeEvent(var1, EEAction.RIGHTCLICK, var2, EEAction2.BreakRadius))) return false;
		
		int charge = chargeLevel(var1);
		if (charge < 1) return false;
		
		boolean var14 = false;
		cleanDroplist(var1);
		if (chargeLevel(var1) < 1) return false;
		var2.C_();
		var3.makeSound(var2, "flash", 0.8F, 1.5F);
		for (int var15 = -(charge * 2) + 1; var15 <= charge * 2 - 1; var15++) {
			for (int var16 = charge * 2 + 1; var16 >= -2; var16--) {
				for (int var17 = -(charge * 2) + 1; var17 <= charge * 2 - 1; var17++) {
					int nx = x + var15;
					int ny = y + var16;
					int nz = z + var17;
					int var21 = var3.getTypeId(nx, ny, nz);
					if ((EEMaps.isWood(var21) || EEMaps.isLeaf(var21)) && attemptBreak(var2, nx, ny, nz)) {
						if (getFuelRemaining(var1) < 1) {
							if (var15 == charge && var17 == charge) {
								ConsumeReagent(var1, var2, var14);
								var14 = false;
							} else {
								ConsumeReagent(var1, var2, false);
							}
						}
						
						if (getFuelRemaining(var1) > 0) {
							int var22 = var3.getData(nx, ny, nz);
							ArrayList<ItemStack> var23 = Block.byId[var21].getBlockDropped(var3, nx, ny, nz, var22, 0);
							ItemStack var25;
							for (Iterator<ItemStack> var24 = var23.iterator(); var24.hasNext(); addToDroplist(var1, var25))
								var25 = var24.next();

							var3.setTypeId(nx, ny, nz, 0);
							if (!EEMaps.isLeaf(var21)) setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);
							if (var3.random.nextInt(8) == 0) var3.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if (var3.random.nextInt(8) == 0) var3.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
						}
					}
				}

			}

		}

		ejectDropList(var3, var1, x, y, z);
		return false;
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	public boolean itemCharging;
	private static Block blocksEffectiveAgainst[];

	static {
		blocksEffectiveAgainst = (new Block[] { Block.WOOD, Block.BOOKSHELF, Block.LOG, Block.CHEST });
	}
}