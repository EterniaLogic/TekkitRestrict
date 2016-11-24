package nl.taico.tekkitrestrict.objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.eclipse.jdt.annotation.NonNull;

import com.sk89q.worldedit.BlockVector;

public class TRPos {
	public int x1, y1, z1, x2, y2, z2;
	public boolean isEmpty;
	public TRPos(){
		isEmpty = true;
	}
	public TRPos(@NonNull final Location loc1, @NonNull final Location loc2){
		this(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
		/*
		x1 = loc1.getBlockX();
		x2 = loc2.getBlockX();
		y1 = loc1.getBlockY();
		y2 = loc2.getBlockY();
		z1 = loc1.getBlockZ();
		z2 = loc2.getBlockZ();
		if (x1 > x2){
			int t = x1;
			x1 = x2;
			x2 = t;
		}
		if (y1 > y2){
			int t = y1;
			y1 = y2;
			y2 = t;
		}
		if (z1 > z2){
			int t = z1;
			z1 = z2;
			z2 = t;
		}
		*/
	}
	
	public TRPos(final BlockVector loc1, final BlockVector loc2){
		this(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
	}
	
	public TRPos(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2){
		if (x1 > x2){
			this.x1 = x2;
			this.x2 = x1;
		} else {
			this.x1 = x1;
			this.x2 = x2;
		}
		if (y1 > y2){
			this.y1 = y2;
			this.y2 = y1;
		} else {
			this.y1 = y1;
			this.y2 = y2;
		}
		if (z1 > z2){
			this.z1 = z2;
			this.z2 = z1;
		} else {
			this.z1 = z1;
			this.z2 = z2;
		}
		isEmpty = false;
	}
	public TRPos(@NonNull final String[] temp){
		this(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Integer.parseInt(temp[4]), Integer.parseInt(temp[5]));
		/*
		x1 = Integer.parseInt(temp[0]);
		y1 = Integer.parseInt(temp[1]);
		z1 = Integer.parseInt(temp[2]);
		x2 = Integer.parseInt(temp[3]);
		y2 = Integer.parseInt(temp[4]);
		z2 = Integer.parseInt(temp[5]);
		if (x1 > x2){
			int t = x1;
			x1 = x2;
			x2 = t;
		}
		if (y1 > y2){
			int t = y1;
			y1 = y2;
			y2 = t;
		}
		if (z1 > z2){
			int t = z1;
			z1 = z2;
			z2 = t;
		}
		*/
	}
	
	public boolean contains(@NonNull final Location loc){
		final int x = loc.getBlockX();
		if (x < x1 || x > x2) return false;
		final int z = loc.getBlockZ();
		if (z < z1 || z > z2) return false;
		final int y = loc.getBlockY();
		if (y < y1 || y > y2) return false;
		return true;
	}
	
	public boolean contains(final int x, final int y, final int z) {
		if (x < x1 || x > x2) return false;
		if (z < z1 || z > z2) return false;
		if (y < y1 || y > y2) return false;
		return true;
	}
	
	public boolean containsIgnoreY(@NonNull final Location loc){
		final int x = loc.getBlockX();
		if (x < x1 || x > x2) return false;
		final int z = loc.getBlockZ();
		if (z < z1 || z > z2) return false;
		return true;
	}
	
	public boolean containsIgnoreY(final int x, final int z){
		if (x < x1 || x > x2) return false;
		if (z < z1 || z > z2) return false;
		return true;
	}
	
	public Location getLesserCorner(final World world){
		return new Location(world, x1, y1, z1);
	}
	
	public Location getCenter(final World world){
		return new Location(world, (x1+x2)/2, (y1+y2)/2, (z1+z2)/2);
	}
	
	public Location getGreaterCorner(final World world){
		return new Location(world, x2, y2, z2);
	}
	
	@NonNull public Location toLoc(final World world){
		return new Location(world, (x1+x2)/2, (y1+y2)/2, (z1+z2)/2);
	}
	
	@Override
	public String toString(){
		return ""+x1+","+y1+","+z1+","+x2+","+y2+","+z2;
	}
	
	public int hashCode(){
		//int result = 17 + y1;
		//result = 17 * result + y2;
		//result = 17 * result + z1;
		//result = 17 * result + x2;
		//result = 17 * result + z2;
		//return 17 * result + x1;
		
		return 17 * (17 * (17 * (17 * (17 * (17 + y1) + y2) + z1) + x2) + z2) + x1;
	}
	
	public boolean equals(Object obj){
		if (this == obj) return true;
		
		if (!(obj instanceof TRPos)) return false;
		TRPos other = (TRPos) obj;
		
		return x1 == other.x1 && z1 == other.z1 &&
			   x2 == other.x2 && z2 == other.z2 &&
			   y1 == other.y1 && y2 == other.y2;
	}
}
