package ee.events.rm;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;

public class EERMHoeEvent extends EERMToolEvent{
	public EERMHoeEvent(ItemStack tool, EEAction action, EntityHuman human) {
		super(tool, action, human);
	}

	public EERMHoeEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(tool, action, human, extra);
	}

	public EERMHoeEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z) {
		super(tool, action, human, x, y, z);
	}

	public EERMHoeEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
		super(tool, action, human, x, y, z, extra);
	}
}
