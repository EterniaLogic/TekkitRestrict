package ee.events.other;

import org.bukkit.Location;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import ee.events.EEEnums.EETransmuteAction;
import ee.events.EEEnums.EEAction;
import ee.events.EEPlayerEvent;

/**
 * Represents an event with a Transmution Table
 */
public class EETransmutationTableEvent extends EEPlayerEvent {
	protected ItemStack tool;
	protected EEAction action;
	protected EETransmuteAction extra;
	protected double x, y, z;
	protected World world;
	
	public EETransmutationTableEvent(ItemStack tool, EEAction action, EntityHuman human, EETransmuteAction extra) {
		super(human);
		this.action = action;
		this.tool = tool;
		this.extra = extra;
		this.world = human.world;
		this.x = human.locX;
		this.y = human.locY;
		this.z = human.locZ;
	}
	
	public EETransmutationTableEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EETransmuteAction extra) {
		super(human);
		this.action = action;
		this.tool = tool;
		this.extra = extra;
		this.world = human.world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return The action of this event
	 * @see EEAction
	 */
	public EEAction getAction() {
		return action;
	}

	/**
	 * @return The Transmution Table
	 */
	public ItemStack getTool() {
		return tool;
	}
	
	/**
	 * @return Extra information about this event.
	 * @see EETransmuteAction
	 */
	public EETransmuteAction getExtraInfo(){
		return extra;
	}
	
	public void setExtraInfo(EETransmuteAction extra){
		this.extra = extra;
	}
	
	public Location getLocation(){
		return new Location(world.getWorld(), x, y, z);
	}
}
