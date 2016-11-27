package ee.events.amulet;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAmuletAction;

/**
 * Represents an Action with a Volcanite Amulet
 */
public class EEVolcaniteAmuletEvent extends EEAmuletEvent {
	public EEVolcaniteAmuletEvent(ItemStack amulet, EEAction action, EntityHuman human, EEAmuletAction extra) {
		super(amulet, action, human, extra);
	}
	
	public EEVolcaniteAmuletEvent(ItemStack amulet, EEAction action, EntityHuman human, int x, int y, int z, EEAmuletAction extra) {
		super(amulet, action, human, x, y, z, extra);
	}
}
