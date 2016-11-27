package ee.events.amulet;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAmuletAction;

/**
 * Represents an Action with an Evertide Amulet
 */
public class EEEvertideAmuletEvent extends EEAmuletEvent {
	public EEEvertideAmuletEvent(ItemStack amulet, EEAction action, EntityHuman human, EEAmuletAction extra) {
		super(amulet, action, human, extra);
	}
	
	public EEEvertideAmuletEvent(ItemStack amulet, EEAction action, EntityHuman human, int x, int y, int z, EEAmuletAction extra) {
		super(amulet, action, human, x, y, z, extra);
	}
}
