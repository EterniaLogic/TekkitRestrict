package nl.taico.tekkitrestrict.safezones;

import java.util.ArrayList;

import nl.taico.tekkitrestrict.objects.TRWorldPos;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NativeSafezone extends TRSafezone {
	protected static ArrayList<NativeSafezone> nativezones = new ArrayList<NativeSafezone>();
	public static ArrayList<NativeSafezone> getZones(){
		synchronized (nativezones){
			return new ArrayList<NativeSafezone>(nativezones);
		}
	}

	public static boolean isInSafezone(final Player player){
		final Location loc = player.getLocation();
		for (final NativeSafezone zone : getZones()){
			if (!zone.location.contains(loc)) continue;
			return zone.isSafezoneFor(player);
		}

		return false;
	}

	protected NativeSafezone(final String name, final Location loc1, final Location loc2) {
		super(0, name);
		this.world = loc1.getWorld().getName().toLowerCase();
		this.location = new TRWorldPos(loc1, loc2);
		this.valid = true;
		synchronized (nativezones){
			nativezones.add(this);
		}
	}

	protected NativeSafezone(final String name, final TRWorldPos loc) {
		super(0, name);
		this.world = loc.getWorld().getName().toLowerCase();
		this.location = loc;
		this.valid = true;
		synchronized (nativezones){
			nativezones.add(this);
		}
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
	protected void remove(){
		synchronized (nativezones){
			nativezones.remove(this);
		}
	}

	public boolean shortCheck(final Player player) {
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
