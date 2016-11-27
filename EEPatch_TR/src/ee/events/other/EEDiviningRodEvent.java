package ee.events.other;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.EEToolEvent;

/**
 * Represents an event with a Divining Rod
 */
public class EEDiviningRodEvent extends EEToolEvent {
	public EEDiviningRodEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(tool, action, human, extra);
	}

	public EEDiviningRodEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
		super(tool, action, human, x, y, z, extra);
	}

}
