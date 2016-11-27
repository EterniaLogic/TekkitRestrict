package ee;

import ee.core.GuiIds;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EETransmuteAction;
import ee.events.EEEventManager;
import ee.events.other.EETransmutationTableEvent;
import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import net.minecraft.server.mod_EE;

public class ItemTransTablet extends ItemEECharged {
	public ItemTransTablet(int var1) {
		super(var1, 0);
	}

	public void doExtra(World var1, ItemStack var2, EntityHuman var3) {
		if (EEProxy.isClient(var1)) return;
		
		var3.openGui(mod_EE.getInstance(), GuiIds.PORT_TRANS_TABLE, var1, (int) var3.locX, (int) var3.locY, (int) var3.locZ);
	}

	public void doRelease(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EETransmutationTableEvent(var1, EEAction.RELEASE, var3, EETransmuteAction.ChangeMob))) return;
		var3.C_();
		var2.makeSound(var3, "transmute", 0.6F, 1.0F);
		var2.addEntity(new EntityPhilosopherEssence(var2, var3, chargeLevel(var1)));
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		if (EEEventManager.callEvent(new EETransmutationTableEvent(var1, EEAction.ALTERNATE, var3, EETransmuteAction.PortableTable))) return;
		doExtra(var2, var1, var3);
	}

	public ItemStack a(ItemStack var1, World var2, EntityHuman var3) {
		if (EEProxy.isClient(var2)) return var1;

		if (EEEventManager.callEvent(new EETransmutationTableEvent(var1, EEAction.RIGHTCLICK, var3, EETransmuteAction.PortableTable))) return var1;
		var3.openGui(mod_EE.getInstance(), GuiIds.PORT_TRANS_TABLE, var2, (int) var3.locX, (int) var3.locY, (int) var3.locZ);
		return var1;
	}

	public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7) {
		if (EEProxy.isClient(var3)) return true;

		if (EEEventManager.callEvent(new EETransmutationTableEvent(var1, EEAction.RIGHTCLICK, var2, var4, var5, var6, EETransmuteAction.PortableTable))) return true;
		var2.openGui(mod_EE.getInstance(), GuiIds.PORT_TRANS_TABLE, var3, var4, var5, var6);
		return true;
	}

	public void doLeftClick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {}

	public void doChargeTick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doUncharge(ItemStack var1, World var2, EntityHuman var3) {}
}