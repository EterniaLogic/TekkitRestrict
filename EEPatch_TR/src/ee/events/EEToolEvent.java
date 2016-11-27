package ee.events;

import org.bukkit.Location;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

import ee.events.EEEnums.*;

/**
 * Represents an event with an EE Tool.
 */
public class EEToolEvent extends EEPlayerEvent {
	protected ItemStack tool;
	protected EEAction action;
	protected EEAction2 extra;
	protected double x, y, z;
	protected World world;
	
	public EEToolEvent(ItemStack tool, EEAction action, EntityHuman human, EEAction2 extra) {
		super(human);
		this.action = action;
		this.tool = tool;
		this.extra = extra;
		this.world = human.world;
		this.x = human.locX;
		this.y = human.locY;
		this.z = human.locZ;
	}
	
	public EEToolEvent(ItemStack tool, EEAction action, EntityHuman human, int x, int y, int z, EEAction2 extra) {
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
	 * @return The Action of this Event.
	 * @see EEAction
	 */
	public EEAction getAction() {
		return action;
	}

	/**
	 * @return The tool that caused this event.
	 */
	public ItemStack getTool() {
		return tool;
	}
	
	/**
	 * Common extra info:<br>
	 * BreakRadius, TillRadius<br>
	 * TallBreak, WideBreak, LongBreak, MegaBreak (hammer)<br>
	 * UpdateToolMode, UpdateHammerMode<br>
	 * Shear
	 * @return Extra info about this event.
	 * @see EEAction2
	 */
	public EEAction2 getExtraInfo(){
		return extra;
	}
	
	public void setExtraInfo(EEAction2 extra){
		this.extra = extra;
	}
	
	public Location getLocation(){
		return new Location(world.getWorld(), x, y, z);
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getZ(){
		return z;
	}
	
	public World getMCWorld(){
		return world;
	}
	
	public org.bukkit.World getWorld(){
		return world.getWorld();
	}
}
