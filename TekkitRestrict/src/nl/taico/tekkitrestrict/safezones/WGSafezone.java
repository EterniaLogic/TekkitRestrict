package nl.taico.tekkitrestrict.safezones;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import nl.taico.tekkitrestrict.TRConfigCache.SafeZones;
import nl.taico.tekkitrestrict.objects.TRWorldPos;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class WGSafezone extends TRSafezone {
	protected static Plugin plugin;
	protected ProtectedRegion region;
	static {
		plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
	}
	
	private static ArrayList<WGSafezone> wgzones = new ArrayList<WGSafezone>();
	
	protected WGSafezone(final String name, final Location loc) {
		super(1, name);
		this.world = loc.getWorld().getName().toLowerCase();
		
		if (plugin != null){
			try {
				this.region = ((WorldGuardPlugin) plugin).getRegionManager(loc.getWorld()).getRegion(name);
				if (this.region == null) this.region = getRegionAtPos(loc);
			} catch (Exception ex) {}
		}
		if (this.region == null){
			this.location = new TRWorldPos(loc, loc);//store saved location.
			this.valid = false;
		} else {
			this.location = new TRWorldPos(loc.getWorld(), region.getMinimumPoint(), region.getMaximumPoint());
			this.valid = true;
		}
		synchronized (wgzones){
			wgzones.add(this);
		}
	}
	
	protected WGSafezone(final String name, final Location loc1, final Location loc2) {
		super(1, name);
		this.world = loc1.getWorld().getName().toLowerCase();
		
		if (plugin != null){
			try {
				this.region = ((WorldGuardPlugin) plugin).getRegionManager(loc1.getWorld()).getRegion(name);
				if (this.region == null) this.region = getRegionAtPos(loc1);
				if (this.region == null) this.region = getRegionAtPos(loc2);
			} catch (Exception ex) {}
		}
		if (this.region == null){
			this.location = new TRWorldPos(loc1, loc2);//store saved location.
			this.valid = false;
		} else {
			this.location = new TRWorldPos(loc1.getWorld(), region.getMinimumPoint(), region.getMaximumPoint());
			this.valid = true;
		}
		synchronized (wgzones){
			wgzones.add(this);
		}
	}
	
	protected WGSafezone(final String name, final TRWorldPos loc) {
		super(1, name);
		this.world = loc.getWorld().getName().toLowerCase();
		
		if (plugin != null){
			try {
				this.region = ((WorldGuardPlugin) plugin).getRegionManager(loc.getWorld()).getRegion(name);
				if (this.region == null) this.region = getRegionAtPos(loc.getCenter());
				if (this.region == null) this.region = getRegionAtPos(loc.getLesserCorner());
				if (this.region == null) this.region = getRegionAtPos(loc.getGreaterCorner());
			} catch (Exception ex) {}
		}
		if (this.region == null){
			this.location = loc;//store saved location.
			this.valid = false;
		} else {
			this.location = new TRWorldPos(loc.getWorld(), region.getMinimumPoint(), region.getMaximumPoint());
			this.valid = true;
		}
		synchronized (wgzones){
			wgzones.add(this);
		}
	}

	@Override
	public boolean isSafezoneFor(final Player player) {
		if (!valid || region == null) return false;
		final Location loc = player.getLocation();
		if (!location.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) return false;
		final String name = player.getName();
		final Boolean b = cache.get(name);
		if (b != null) return b.booleanValue();
		if (region.isMember(name)){
			cache.put(name, false);
			return false;
		}
		final boolean c = !((WorldGuardPlugin) plugin).canBuild(player, loc);
		cache.put(name, c);
		return c;
	}
	
	/**
	 * Does not check validness or location.
	 */
	public boolean shortCheck(final Player player, final Location loc) {
		final String name = player.getName();
		final Boolean b = cache.get(name);
		if (b != null) return b.booleanValue();
		if (region.isMember(name)){
			cache.put(name, false);
			return false;
		}
		final boolean c = !((WorldGuardPlugin) plugin).canBuild(player, loc);
		cache.put(name, c);
		return c;
	}

	@Override
	public void update() {
		cache.clear();
	}

	protected void remove(){
		synchronized (wgzones){
			wgzones.remove(this);
		}
	}
	
	public static ArrayList<WGSafezone> getZones(){
		synchronized (wgzones){
			return new ArrayList<WGSafezone>(wgzones);
		}
	}
	
	public static boolean isInSafezone(final Player player){
		final Location loc = player.getLocation();
		switch (SafeZones.WGMode){
			case All:
				return !((WorldGuardPlugin) plugin).canBuild(player, loc);
			default:
				for (final WGSafezone zone : getZones()){
					if (!zone.valid || zone.region == null) continue;
					if (!zone.location.contains(loc)) continue;
					return zone.shortCheck(player, loc);
				}
		}
		return false;
	}
	
	
	/**
	 * @return the region at this position. (Null if there are multiple)
	 */
	private static ProtectedRegion getRegionAtPos(final Location loc){
		if (plugin == null) return null;
		try {
			final ApplicableRegionSet regions = ((WorldGuardPlugin) plugin).getRegionManager(loc.getWorld()).getApplicableRegions(loc);
			if (regions.size() == 0) return null;
			if (regions.size() > 1){
				final ProtectedRegion region = regions.iterator().next();
				if (region.getParent() != null) return region.getParent();
				return region;
			} else {
				return regions.iterator().next();
			}
		} catch (Exception ex){}
		return null;
	}
	
	@SuppressWarnings("unused")
	private static ProtectedRegion getRegion(final String name, final World world){
		if (plugin == null) return null;
		try {
			return ((WorldGuardPlugin) plugin).getRegionManager(world).getRegion(name);
		} catch (Exception ex) {}
		return null;
	}
}
