package nl.taico.tekkitrestrict.safezones;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import nl.taico.tekkitrestrict.objects.TRPos;

public abstract class TRSafezone {
	private final int type;
	protected TRPos location;
	protected String name;
	protected String world;
	protected boolean valid;
	protected ConcurrentHashMap<String, Boolean> cache = new ConcurrentHashMap<String, Boolean>();
	protected TRSafezone(final int type, final String name){
		this.type = type;
		this.name = name;
		this.valid = true;
	}
	
	public int getType(){
		return type;
	}
	public static int getType(final TRSafezone zone){
		return zone.type;
	}
	
	public String getName(){
		return name;
	}
	public static String getName(final TRSafezone zone){
		return zone.name;
	}
	
	public TRPos getLocation(){
		return location;
	}
	public static TRPos getLocation(final TRSafezone zone){
		return zone.location;
	}
	
	public boolean isInside(final Location loc){
		if (!loc.getWorld().getName().equalsIgnoreCase(world)) return false;
		return location.contains(loc);
	}
	
	public boolean isInside(final World world, final int x, final int y, final int z){
		if (!world.getName().equalsIgnoreCase(this.world)) return false;
		return location.contains(x, y, z);
	}
	
	public boolean isInside(final String world, final int x, final int y, final int z){
		if (!world.equalsIgnoreCase(this.world)) return false;
		return location.contains(x, y, z);
	}
	
	public boolean isValid(){
		return valid;
	}
	public static boolean isValid(final TRSafezone zone){
		return zone.valid;
	}
	
	public abstract boolean isSafezoneFor(final Player player);
	
	public abstract void update();
}
