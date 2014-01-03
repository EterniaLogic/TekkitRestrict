package nl.taico.tekkitrestrict.safezones;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import nl.taico.tekkitrestrict.objects.TRWorldPos;

public class NativeSafezone extends TRSafezone {
	protected NativeSafezone(final String name, final Location loc1, final Location loc2) {
		super(0, name);
		this.world = loc1.getWorld().getName().toLowerCase();
		this.location = new TRWorldPos(loc1, loc2);
		this.valid = true;
	}

	@Override
	public boolean isSafezoneFor(final Player player) {
		if (!location.contains(player.getLocation())) return false;
		final Boolean b = cache.get(player.getName());
		if (b != null) return b.booleanValue();
		//FIXME do this check in a super class.
		//if (player.hasPermission("tekkitrestrict.bypass.safezone")){
		//	cache.put(player.getName(), false);
		//	return false;
		//}
		if (player.hasPermission("tekkitrestrict.bypass.safezone."+name)){
			cache.put(player.getName(), false);
			return false;
		}
		cache.put(player.getName(), true);
		return true;
	}

	@Override
	public void update() {
		cache.clear();
	}

}
