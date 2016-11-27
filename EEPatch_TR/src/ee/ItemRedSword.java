package ee;

import java.util.List;

import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.rm.EERMSwordEvent;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EnumAnimation;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ItemRedSword extends ItemEECharged {
	public ItemRedSword(int var1) {
		super(var1, 3);
		this.maxStackSize = 1;
		this.weaponDamage = 14;
	}

	public boolean a(ItemStack var1, int var2, int x, int y, int z, EntityLiving var6) {
		if (var2 == Block.WEB.id) {
			EEProxy.dropBlockAsItemStack(Block.byId[var2], var6, x, y, z, new ItemStack(var2, 1, var6.world.getData(x, y, z)));
		}

		return super.a(var1, var2, x, y, z, var6);
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return (var1.id == Block.WEB.id);
	}

	public boolean ConsumeReagent(int var1, ItemStack var2, EntityHuman var3, boolean var4) {
		if (getFuelRemaining(var2) >= 16) {
			setFuelRemaining(var2, getFuelRemaining(var2) - 16);
			return true;
		}

		int var5 = getFuelRemaining(var2);

		while (getFuelRemaining(var2) < 16) {
			ConsumeReagent(var2, var3, var4);

			if (var5 == getFuelRemaining(var2)) {
				break;
			}

			var5 = getFuelRemaining(var2);

			if (getFuelRemaining(var2) < 16) continue;
			setFuelRemaining(var2, getFuelRemaining(var2) - 16);
			return true;
		}

		return false;
	}

	public void doBreak(ItemStack var1, World var2, EntityHuman var3) {
		if (chargeLevel(var1) <= 0) return;

		boolean var4 = false;
		int var5 = 0;
		for (var5 = 1; var5 <= chargeLevel(var1); ++var5) {
			if (var5 == chargeLevel(var1)) {
				var4 = true;
			}

			if (ConsumeReagent(1, var1, var3, var4)) continue;

			--var5;
			break;
		}

		if (var5 < 1) {
			return;
		}

		var3.C_();
		var2.makeSound(var3, "flash", 0.8F, 1.5F);
		List<Entity> var6 = var2.getEntities(var3, AxisAlignedBB.b((float) var3.locX - (var5 / 1.5D + 2.0D), var3.locY - (var5 / 1.5D + 2.0D),
				(float) var3.locZ - (var5 / 1.5D + 2.0D), (float) var3.locX + var5 / 1.5D + 2.0D, var3.locY + var5 / 1.5D + 2.0D, (float) var3.locZ + var5
						/ 1.5D + 2.0D));

		for (int var7 = 0; var7 < var6.size(); ++var7) {
			Entity var8 = var6.get(var7);
			if ((!(var8 instanceof EntityLiving)) || ((!(EEBase.getSwordMode(var3))) && (!(var8 instanceof EntityMonster)))) continue;

			var8.damageEntity(DamageSource.playerAttack(var3), this.weaponDamage + chargeLevel(var1) * 2);
		}
	}

	public float getDestroySpeed(ItemStack var1, Block var2) {
		return ((var2.id != Block.WEB.id) ? 1.5F : 15.0F);
	}

	public EnumAnimation d(ItemStack var1) {
		return EnumAnimation.d;
	}

	public int c(ItemStack var1) {
		return 72000;
	}

	public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
		var3.a(var1, c(var1));
		return var1;
	}

	public int a(Entity var1) {
		return this.weaponDamage;
	}

	public boolean a(ItemStack var1, EntityLiving var2, EntityLiving var3) {
		return true;
	}

	public boolean isFull3D() {
		return true;
	}

	public void doRelease(ItemStack item, World world, EntityHuman human) {
		if (EEEventManager.callEvent(new EERMSwordEvent(item, EEAction.RELEASE, human, EEAction2.AttackRadius))) return;
		doBreak(item, world, human);
	}

	public void doAlternate(ItemStack item, World world, EntityHuman human) {
		//if (EEEventManager.callEvent(new EERMSwordEvent(item, EEAction.RELEASE, human, EEAction2.UpdateSwordMode))) return;
		EEBase.updateSwordMode(human);
	}

	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {}
}
