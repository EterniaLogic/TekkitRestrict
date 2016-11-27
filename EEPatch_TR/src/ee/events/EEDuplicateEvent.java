package ee.events;

import net.minecraft.server.ItemStack;
import ee.events.EEEnums.DuplicateType;

/**
 * Called when an item is about to be duplicated.<br>
 * This is possible in an RM Furnace (It turns 1 ore into 2 ingots).
 */
public class EEDuplicateEvent extends EEEvent {
	protected DuplicateType type;
	protected ItemStack item;
	public EEDuplicateEvent(ItemStack item, DuplicateType type){
		this.type = type;
		this.item = item;
	}
	
	/**
	 * @return The item that is about to get duplicated.
	 */
	public ItemStack getItemStack(){
		return item;
	}
	
	/**
	 * @return The Type of Duplication
	 * @see DuplicateType
	 */
	public DuplicateType getDuplicateType(){
		return type;
	}
}
