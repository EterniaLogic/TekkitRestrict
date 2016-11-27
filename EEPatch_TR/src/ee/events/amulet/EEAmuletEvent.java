package ee.events.amulet;

import org.bukkit.Location;

import ee.events.EEPlayerEvent;
import ee.events.EEEnums.*;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

/**
 * Represents an action of an Evertide Amulet or a Volcanite Amulet.
 */
public class EEAmuletEvent extends EEPlayerEvent {
	protected ItemStack amulet;
	protected EEAction action;
	protected EntityHuman human;
	protected EEAmuletAction extra;
	protected double x, y, z;
	protected World world;

	public EEAmuletEvent(ItemStack amulet, EEAction action, EntityHuman human, EEAmuletAction extra){
		super(human);
		this.amulet = amulet;
		this.action = action;
		this.extra = extra;
		this.world = human.world;
		this.x = human.locX;
		this.y = human.locY;
		this.z = human.locZ;
	}

	public EEAmuletEvent(ItemStack amulet, EEAction action, EntityHuman human, int x, int y, int z, EEAmuletAction extra) {
		super(human);
		this.action = action;
		this.amulet = amulet;
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
	 * @return The amulet that was used.
	 */
	public ItemStack getAmulet() {
		return amulet;
	}
	
	/**
	 * @see EEAmuletAction EEAmuletAction
	 */
	public EEAmuletAction getExtraInfo(){
		return extra;
	}
	
	
	public void setExtraInfo(EEAmuletAction extra){
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
