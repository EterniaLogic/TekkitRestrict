package ee;

import ee.events.EEEventManager;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.destruction.EEHyperkineticLensEvent;
import net.minecraft.server.*;

public class ItemHyperkineticLens extends ItemEECharged {

	public ItemHyperkineticLens(int var1) {
		super(var1, 3);
	}

	public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int i, int j, int k, int l) {
		return false;
	}

	public void doBreak(ItemStack var1, World var2, EntityHuman human) {
		int var4 = 1;
		int charge = chargeLevel(var1);
		if (charge > 0) var4++;
		if (charge > 1) {
			var4++;
			var4++;
		}
		if (charge > 2) {
			var4++;
			var4++;
		}
		var2.makeSound(human, "wall", 1.0F, 1.0F);
		var2.addEntity(new EntityHyperkinesis(var2, human, charge, var4));
	}

	public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) {
			return var1;
		} else {
			if (EEEventManager.callEvent(new EEHyperkineticLensEvent(var1, EEAction.RIGHTCLICK, var3, EEAction2.BreakRadius))) return var1;
			//if (!var3.getBukkitEntity().hasPermission("eepatch.delay") || delay <= System.currentTimeMillis()){
				doBreak(var1, var2, var3);
			//	delay = System.currentTimeMillis()+1000*5;
			//}
			return var1;
		}
	}

	public void doRelease(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return;
		if (EEEventManager.callEvent(new EEHyperkineticLensEvent(item, EEAction.RELEASE, human, EEAction2.BreakRadius))) return;
		//if (!human.getBukkitEntity().hasPermission("eepatch.delay") || delay <= System.currentTimeMillis()){
			doBreak(item, world, human);
		//	delay = System.currentTimeMillis()+1000*5;
		//}
	}

	//private long delay = 0;
	public void doLeftClick(ItemStack var1, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) return;
		if (EEEventManager.callEvent(new EEHyperkineticLensEvent(var1, EEAction.LEFTCLICK, var3, EEAction2.BreakRadius))) return;
		//if (!var3.getBukkitEntity().hasPermission("eepatch.delay") || delay <= System.currentTimeMillis()){
			doBreak(var1, var2, var3);
		//	delay = System.currentTimeMillis()+1000*5;
		//}
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	public boolean itemCharging;
}