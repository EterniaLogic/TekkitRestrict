package ee.events.ring;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EERingAction;

public class EEHarvestRingEvent extends EERingEvent {
	public EEHarvestRingEvent(ItemStack ring, EEAction action, EntityHuman human, EERingAction extra) {
		super(ring, action, human, extra);
	}

	public EEHarvestRingEvent(ItemStack ring, EEAction action, EntityHuman human, int x, int y, int z, EERingAction extra) {
		super(ring, action, human, x, y, z, extra);
	}
}
