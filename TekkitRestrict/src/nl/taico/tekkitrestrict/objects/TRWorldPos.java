package nl.taico.tekkitrestrict.objects;

import lombok.NonNull;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldedit.BlockVector;

public class TRWorldPos extends TRPos {
	protected World world;
	public TRWorldPos(final Location loc1, final Location loc2){
		super(loc1, loc2);
		this.world = loc1.getWorld();
	}

	public TRWorldPos(final World world, final BlockVector loc1, final BlockVector loc2){
		super(loc1, loc2);
		this.world = world;
	}

	public TRWorldPos(final World world, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2){
		super(x1, x2, y1, y2, z1, z2);
		this.world = world;
	}

	@Override
	public boolean contains(@NonNull final Location loc){
		if (world != loc.getWorld()) return false;
		final int x = loc.getBlockX();
		if ((x < x1) || (x > x2)) return false;
		final int z = loc.getBlockZ();
		if ((z < z1) || (z > z2)) return false;
		final int y = loc.getBlockY();
		if ((y < y1) || (y > y2)) return false;
		return true;
	}

	public Location getCenter(){
		return new Location(world, (x1+x2)/2, (y1+y2)/2, (z1+z2)/2);
	}

	/**
	 * Ignores world and returns the location with the world in this TRWorldPos
	 * @see nl.taico.tekkitrestrict.objects.TRPos#getCenter(org.bukkit.World)
	 */
	@Override
	public Location getCenter(final World unused){
		return getCenter();
	}

	public Location getGreaterCorner(){
		return new Location(world, x2, y2, z2);
	}

	/**
	 * Ignores world and returns the location with the world in this TRWorldPos
	 * @see nl.taico.tekkitrestrict.objects.TRPos#getGreaterCorner(org.bukkit.World)
	 */
	@Override
	public Location getGreaterCorner(final World unused){
		return getGreaterCorner();
	}

	public Location getLesserCorner(){
		return new Location(world, x1, y1, z1);
	}

	/**
	 * Ignores world and returns the location with the world in this TRWorldPos
	 * @see nl.taico.tekkitrestrict.objects.TRPos#getLesserCorner(org.bukkit.World)
	 */
	@Override
	public Location getLesserCorner(final World unused){
		return getLesserCorner();
	}

	public World getWorld(){
		return world;
	}
}
