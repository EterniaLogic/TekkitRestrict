package ee.events.other;

import org.bukkit.Location;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEWatchAction;
import ee.events.EEPlayerEvent;

/**
 * Represents an event with a Watch of Flowing Time
 */
public class EEWOFTEvent extends EEPlayerEvent {
	protected ItemStack tool;
	protected EEAction action;
	protected EEWatchAction extra;
	protected double x, y, z;
	protected World world;
	
	public EEWOFTEvent(ItemStack tool, EEAction action, EntityHuman human, EEWatchAction extra) {
		super(human);
		this.action = action;
		this.tool = tool;
		this.extra = extra;
		this.world = human.world;
		this.x = human.locX;
		this.y = human.locY;
		this.z = human.locZ;
	}
	
	public EEWOFTEvent(EEAction action, EntityHuman human, EEWatchAction extra) {
		super(human);
		this.action = action;
		this.tool = null;
		this.extra = extra;
		this.world = human.world;
		this.x = human.locX;
		this.y = human.locY;
		this.z = human.locZ;
	}
	
	public EEWOFTEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEWatchAction extra) {
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
	 * @return The watch, null if not available
	 */
	public ItemStack getTool() {
		return tool;
	}

	/**
	 * @return Extra information about this action
	 * @see EEWatchAction
	 */
	public EEWatchAction getExtraInfo(){
		return extra;
	}
	
	public void setExtraInfo(EEWatchAction extra){
		this.extra = extra;
	}
	
	public Location getLocation(){
		return new Location(world.getWorld(), x, y, z);
	}
}
