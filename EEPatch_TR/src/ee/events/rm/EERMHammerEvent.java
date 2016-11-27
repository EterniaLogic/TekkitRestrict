package ee.events.rm;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;

public class EERMHammerEvent extends EERMToolEvent{
	public EERMHammerEvent(ItemStack tool, EEAction action, EntityHuman human) {
		super(tool, action, human);
	}

	public EERMHammerEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(tool, action, human, extra);
	}

	public EERMHammerEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z) {
		super(tool, action, human, x, y, z);
	}

	public EERMHammerEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
		super(tool, action, human, x, y, z, extra);
	}
}
