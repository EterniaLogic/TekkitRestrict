package ee.events.destruction;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEToolEvent;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;

/**
 * Represents an event with a Destruction Catalyst, a Hyper Catalyst or a Hyperkinetic Lens.
 */
public class EEDestructionToolEvent extends EEToolEvent {
	public EEDestructionToolEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(tool, action, human, extra);
	}

	public EEDestructionToolEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
		super(tool, action, human, x, y, z, extra);
	}
}
