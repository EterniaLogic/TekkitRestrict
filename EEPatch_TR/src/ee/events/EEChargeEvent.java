package ee.events;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

/**
 * This event is called whenever an item is charged or uncharged.
 */
public class EEChargeEvent extends EEPlayerEvent {
	protected ItemStack item;
	protected int oldlevel, newlevel;
	protected int maxlevel;
	
	public EEChargeEvent (ItemStack item, EntityHuman human, int oldlevel, int newlevel, int maxlevel){
		super(human);
		this.item = item;
		this.oldlevel = oldlevel;
		this.newlevel = newlevel;
		this.maxlevel = maxlevel;
	}
	
	/**
	 * @return The old (current) charge level of this item.
	 */
	public int getOldChargeLevel(){
		return oldlevel;
	}
	/**
	 * @return The new charge level this item will have if this event is not cancelled.
	 */
	public int getNewChargeLevel(){
		return newlevel;
	}
	/**
	 * @return The max charge level of this item.
	 */
	public int getMaxChargeLevel(){
		return maxlevel;
	}
	/**
	 * @return The item that was charged or uncharged
	 */
	public ItemStack getItem(){
		return item;
	}
}
