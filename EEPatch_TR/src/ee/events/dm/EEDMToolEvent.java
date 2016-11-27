package ee.events.dm;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEToolEvent;
import ee.events.EEEnums.*;

/**
 * Represents an event with a Dark Matter Tool.
 */
public class EEDMToolEvent extends EEToolEvent {
	public EEDMToolEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(tool, action, human, extra);
	}

	public EEDMToolEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
		super(tool, action, human, x, y, z, extra);
	}
}
