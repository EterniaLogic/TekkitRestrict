package nl.taico.tekkitrestrict.safezones;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import nl.taico.tekkitrestrict.TRConfigCache.SafeZones;
import nl.taico.tekkitrestrict.objects.TRWorldPos;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class GPSafezone extends TRSafezone {
	protected static Plugin plugin;
	protected Claim claim;
	
	static {
		plugin = Bukkit.getPluginManager().getPlugin("GriefPrevention");
	}
	
	protected static ArrayList<GPSafezone> gpzones = new ArrayList<GPSafezone>();
	protected GPSafezone(final String name, final Location location) {
		super(4, name);
		this.world = location.getWorld().getName().toLowerCase();
		this.claim = getClaimAtPos(location);
		if (this.claim == null){
			this.valid = false;
			this.location = new TRWorldPos(location, location);
		} else {
			this.location = new TRWorldPos(this.claim.getLesserBoundaryCorner(), this.claim.getGreaterBoundaryCorner());
			switch (SafeZones.GPMode){
				case Admin:
				case SpecificAdmin:
					if (!claim.isAdminClaim()) this.valid = false;
					break;
				default:
					break;
				
			}
		}
		gpzones.add(this);
	}
	
	private static Claim getClaimAtPos(final Location loc){
		if (plugin == null) return null;
		final DataStore ds = ((GriefPrevention) plugin).dataStore;
		if (ds == null) return null;
		return ds.getClaimAt(loc, false, null);
	}
	
	/*
	protected GPSafezone(String name, World world, int x, int z) {
		super(4, name);
		this.world = world.getName().toLowerCase();
		
	}
	*/
	
	@Override
	public boolean isSafezoneFor(final Player player) {
		if (!valid || claim == null) return false;
		if (!location.containsIgnoreY(player.getLocation())) return false;
		final String name = player.getName();
		final Boolean b = cache.get(name);
		if (b != null) return b.booleanValue();
		if (claim.ownerName.equalsIgnoreCase(name)){
			cache.put(name, false);
			return false;
		}
		final Iterator<String> managers = claim.managers.iterator();
		while (managers.hasNext()){
			if (managers.next().equalsIgnoreCase(name)){
				cache.put(name, false);
				return false; //If manager of claim, return false.
			}
		}
		cache.put(name, true);
		return true;
	}
	
	private boolean fastCheck(final Player player, final Location loc){
		final Boolean b = cache.get(player.getName());
		if (b != null) return b.booleanValue();
		final String name = player.getName();
		if (claim.ownerName.equalsIgnoreCase(name)){
			cache.put(name, false);
			return false;
		}
		final Iterator<String> managers = claim.managers.iterator();
		while (managers.hasNext()){
			if (managers.next().equalsIgnoreCase(name)){
				cache.put(name, false);
				return false; //If manager of claim, return false.
			}
		}
		cache.put(name, true);
		return true;
	}
	
	private static boolean fastCheck(final Claim claim, final Player player, final Location loc){
		final String name = player.getName();
		if (claim.ownerName.equalsIgnoreCase(name)) return false;
		
		final Iterator<String> managers = claim.managers.iterator();
		while (managers.hasNext()){
			if (managers.next().equalsIgnoreCase(name)) return false; //If manager of claim, return false.
		}
		return true;
	}
	
	public static boolean isInSafezone(final Player player){
		final Location loc = player.getLocation();
		switch (SafeZones.GPMode){
			case SpecificAdmin:
				if (player.hasPermission("griefprevention.adminclaims")) return false;
			case Specific:
				for (final GPSafezone zone : gpzones){
					if (!zone.valid || zone.claim == null) continue;
					if (!zone.location.containsIgnoreY(loc)) continue;
					return zone.fastCheck(player, loc);
				}
				return false;
			case Admin:
				if (player.hasPermission("griefprevention.adminclaims")) return false;
			case All:			
				final Claim claim = getClaimAtPos(loc);
				if (claim == null) return false;
				return fastCheck(claim, player, loc);
		}
		
		return false;
	}

	@Override
	public void update() {
		Claim claim = getClaimAtPos(location.getCenter(null));
		if (claim == null) claim = getClaimAtPos(location.getGreaterCorner(null));
		if (claim == null) claim = getClaimAtPos(location.getLesserCorner(null));
		if (claim == null) valid = false;
		else valid = true;
		
		cache.clear();
	}
	
	

}
