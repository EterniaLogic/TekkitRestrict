package ee.events.armor;

import org.bukkit.Location;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.World;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEArmorAction;
import ee.events.EEPlayerEvent;

/**
 * Represents an EE Armor Related event.
 */
public class EEArmorEvent extends EEPlayerEvent {
	protected double x, y, z;
	protected World world;
	protected EEAction action;
	protected EEArmorAction extra;
	
	public EEArmorEvent(EntityHuman human, EEAction action, EEArmorAction extra){
		super(human);
		this.action = action;
		this.extra = extra;
		this.world = human.world;
		this.x = human.locX;
		this.y = human.locY;
		this.z = human.locZ;
	}
	
	/**
	 * @return The action of this event.
	 * @see EEAction
	 */
	public EEAction getAction() {
		return action;
	}
		
	public Location getLocation(){
		return new Location(world.getWorld(), x, y, z);
	}
	
	/**
	 * @return Extra Information about the action
	 * @see EEArmorAction
	 */
	public EEArmorAction getExtraInfo(){
		return extra;
	}
}
