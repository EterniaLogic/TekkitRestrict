package ee.events.entity;

import net.minecraft.server.EntityHuman;
import ee.events.EEPlayerEvent;

/**
 * Event that indicates that water was vaporized by an entity.<br>
 * This entity may or may not have been shot by a player.
 */
public class WaterVaporizeEvent extends EEPlayerEvent {
	protected int x, y, z;

	public WaterVaporizeEvent(EntityHuman human, int x, int y, int z) {
		super(human);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
}
