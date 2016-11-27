package ee.events.rm;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEToolEvent;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;

/**
 * Represents an event with a Red Matter Tool.
 */
public class EERMToolEvent extends EEToolEvent {

	public EERMToolEvent(ItemStack tool, EEAction action, EntityHuman human) {
		super(tool, action, human, EEAction2.Unknown);
	}
	
	public EERMToolEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(tool, action, human, extra);
	}
	
	public EERMToolEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z) {
		super(tool, action, human, x, y, z, EEAction2.Unknown);
	}
	
	public EERMToolEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
		super(tool, action, human, x, y, z, extra);
	}
	
}
