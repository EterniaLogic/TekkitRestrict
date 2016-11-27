package ee.events.dm;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;

public class EEDMHoeEvent extends EEDMToolEvent{
	public EEDMHoeEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(tool, action, human, extra);
	}

	public EEDMHoeEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
		super(tool, action, human, x, y, z, extra);
	}
}
