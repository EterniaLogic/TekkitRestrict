package nl.taico.tekkitrestrict.safezones;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import nl.taico.tekkitrestrict.objects.TRPos;
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
	
	private static ArrayList<WGSafezone> wgSafezones = new ArrayList<WGSafezone>();
	protected WGSafezone(final String name) {
		super(1, name);
		if (plugin != null){
			try {
				WorldGuardPlugin wgPlugin = (WorldGuardPlugin) plugin;
				for (World world : Bukkit.getWorlds()){
					ProtectedRegion region = wgPlugin.getRegionManager(world).getRegion(name);
					if (region == null) continue;
					this.region = region;
					this.world = world.getName().toLowerCase();
					this.location = new TRWorldPos(world, region.getMinimumPoint(), region.getMaximumPoint());
					break;
				}
			} catch (Exception ex) {}
		}
		if (this.region == null){
			this.location = new TRPos();//IMPORTANT do I want implementation like this?
			this.valid = false;
		} else {
			this.valid = true;
		}
		wgSafezones.add(this);
	}
	
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
			this.location = new TRWorldPos(loc, loc);
			this.valid = false;
		} else {
			this.location = new TRWorldPos(loc.getWorld(), region.getMinimumPoint(), region.getMaximumPoint());
			this.valid = true;
		}
		wgSafezones.add(this);
	}

	@Override
	public boolean isSafezoneFor(final Player player) {
		if (!valid || region == null) return false;
		final Location loc = player.getLocation();
		if (!region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) return false;
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

	
	/**
	 * @return the region at this position. (Null if there are multiple)
	 */
	private static ProtectedRegion getRegionAtPos(final Location loc){
		if (plugin == null) return null;
		try {
			ApplicableRegionSet regions = ((WorldGuardPlugin) plugin).getRegionManager(loc.getWorld()).getApplicableRegions(loc);
			if (regions.size() == 0) return null;
			if (regions.size() > 1){
				ProtectedRegion region = regions.iterator().next();
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
