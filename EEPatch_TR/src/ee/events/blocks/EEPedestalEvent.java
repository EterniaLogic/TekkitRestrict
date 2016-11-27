package ee.events.blocks;

import org.bukkit.entity.Player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import ee.TilePedestal;
import ee.events.EEEnums.EEPedestalAction;
import ee.events.EEPlayerEvent;

/**
 * Represents an event involving a DM Pedestal
 */
public class EEPedestalEvent extends EEPlayerEvent {
	protected TilePedestal block;
	protected EEPedestalAction action;
	protected ItemStack item;
	
	public EEPedestalEvent(TilePedestal pedestal, ItemStack item, EntityHuman human, EEPedestalAction action) {
		super(human);
		this.block = pedestal;
		this.item = item;
		this.action = action;
	}

	/**
	 * @return The action of this event
	 * @see EEPedestalAction
	 */
	public EEPedestalAction getAction() {
		return action;
	}

	/**
	 * @return The pedestal.
	 */
	public TilePedestal getPedestal() {
		return block;
	}

	/**
	 * @return The item that this pedestal is trying to use.
	 */
	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * Same as {@link #getPlayer()}, just for clarification.
	 * @return The player that activated this pedestal.
	 */
	public Player getActivationPlayer(){
		return getPlayer();
	}
	
	public void setAction(EEPedestalAction action){
		this.action = action;
	}
	
}
