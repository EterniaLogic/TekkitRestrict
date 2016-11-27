package ee.events.destruction;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;

/**
 * Represents an event involving a Hyperkinetic Lens.
 */
public class EEHyperkineticLensEvent extends EEDestructionToolEvent {
	public EEHyperkineticLensEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(tool, action, human, extra);
	}

	public EEHyperkineticLensEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
		super(tool, action, human, x, y, z, extra);
	}
}
