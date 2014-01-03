package nl.taico.tekkitrestrict.objects;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldedit.BlockVector;

public class TRWorldPos extends TRPos {
	protected World world;
	public TRWorldPos(World world, int x1, int y1, int z1, int x2, int y2, int z2){
		super(x1, x2, y1, y2, z1, z2);
		this.world = world;
	}
	
	public TRWorldPos(Location loc1, Location loc2){
		super(loc1, loc2);
		this.world = loc1.getWorld();
	}
	
	public TRWorldPos(World world, BlockVector loc1, BlockVector loc2){
		super(loc1, loc2);
		this.world = world;
	}
	
	public Location getCenter(){
		return new Location(world, (x1+x2)/2, (y1+y2)/2, (z1+z2)/2);
	}
	
	/**
	 * Ignores world and returns the location with the world in this TRWorldPos
	 * @see nl.taico.tekkitrestrict.objects.TRPos#getCenter(org.bukkit.World)
	 */
	@Override
	public Location getCenter(World unused){
		return getCenter();
	}
	
	/**
	 * Ignores world and returns the location with the world in this TRWorldPos
	 * @see nl.taico.tekkitrestrict.objects.TRPos#getGreaterCorner(org.bukkit.World)
	 */
	@Override
	public Location getGreaterCorner(World unused){
		return getGreaterCorner();
	}
	
	public Location getGreaterCorner(){
		return new Location(world, x2, y2, z2);
	}
	
	/**
	 * Ignores world and returns the location with the world in this TRWorldPos
	 * @see nl.taico.tekkitrestrict.objects.TRPos#getLesserCorner(org.bukkit.World)
	 */
	@Override
	public Location getLesserCorner(World unused){
		return getLesserCorner();
	}
	
	public Location getLesserCorner(){
		return new Location(world, x1, y1, z1);
	}
}
