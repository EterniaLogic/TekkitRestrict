package ee.events.ring;

import org.bukkit.Location;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import ee.events.EEPlayerEvent;
import ee.events.EEEnums.*;

/**
 * Represents an event with a Ring
 */
public class EERingEvent extends EEPlayerEvent {
	protected ItemStack ring;
	protected EEAction action;
	protected EERingAction extra;
	protected double x, y, z;
	protected World world;
	
	public EERingEvent(ItemStack ring, EEAction action, EntityHuman human, EERingAction extra) {
		super(human);
		this.action = action;
		this.ring = ring;
		this.extra = extra;
		this.world = human.world;
		this.x = human.locX;
		this.y = human.locY;
		this.z = human.locZ;
	}
	
	public EERingEvent(ItemStack ring, EEAction action, EntityHuman human, int x, int y, int z, EERingAction extra) {
		super(human);
		this.action = action;
		this.ring = ring;
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
	 * @return The Ring used.
	 */
	public ItemStack getRing() {
		return ring;
	}

	/**
	 * @return Extra information about this event
	 * @see EERingAction
	 */
	public EERingAction getExtraInfo(){
		return extra;
	}
	
	public void setExtraInfo(EERingAction extra){
		this.extra = extra;
	}
	
	public Location getLocation(){
		return new Location(world.getWorld(), x, y, z);
	}
}
