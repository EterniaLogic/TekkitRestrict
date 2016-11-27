package ee.events.rm;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;

public class EERMSpadeEvent extends EERMToolEvent{
	public EERMSpadeEvent(ItemStack tool, EEAction action, EntityHuman human) {
		super(tool, action, human);
	}

	public EERMSpadeEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(tool, action, human, extra);
	}

	public EERMSpadeEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z) {
		super(tool, action, human, x, y, z);
	}

	public EERMSpadeEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
		super(tool, action, human, x, y, z, extra);
	}
}
