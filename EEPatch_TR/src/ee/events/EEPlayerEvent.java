package ee.events;

import org.bukkit.entity.Player;

import net.minecraft.server.EntityHuman;

/**
 * Convenience Class, do not listen for this one.
 */
public class EEPlayerEvent extends EEEvent {
	protected EntityHuman human;
	public EEPlayerEvent(EntityHuman human){
		super();
		this.human = human;
	}
	
	/**
	 * @return The Player that caused this event,
	 * or {@code null} if it is unknown who caused it.
	 */
	public Player getPlayer(){
		return human == null ? null : (Player) human.getBukkitEntity();
	}
	
	/**
	 * @return The EntityHuman that caused this event,
	 * or {@code null} if it is unknown who caused it.
	 */
	public EntityHuman getMCPlayer(){
		return human;
	}
	
	/**
	 * @deprecated Use {@link #getMCPlayer()} instead.
	 * @return The EntityHuman that caused this event,
	 * or {@code null} if it is unknown who caused it.
	 * @see #getMCPlayer()
	 */
	public EntityHuman getHuman(){
		return getMCPlayer();
	}
}
