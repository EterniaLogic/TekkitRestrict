package nl.taico.tekkitrestrict.objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.eclipse.jdt.annotation.NonNull;

import com.sk89q.worldedit.BlockVector;

public class TRPos {
	public int x1, y1, z1, x2, y2, z2;
	
	public TRPos(){}
	public TRPos(@NonNull Location loc1, @NonNull Location loc2){
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
	
	public TRPos(BlockVector loc1, BlockVector loc2){
		this(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
	}
	/**
	 * @param loc1
	 * @param loc2
	 * @return
	 * @deprecated Use new {@link #TRPos(BlockVector, BlockVector)} instead
	 */
	public static TRPos parse(@NonNull BlockVector loc1, @NonNull BlockVector loc2){
		TRPos p = new TRPos();
		p.x1 = loc1.getBlockX();
		p.x2 = loc2.getBlockX();
		p.y1 = loc1.getBlockY();
		p.y2 = loc2.getBlockY();
		p.z1 = loc1.getBlockZ();
		p.z2 = loc2.getBlockZ();
		if (p.x1 > p.x2){
			int t = p.x1;
			p.x1 = p.x2;
			p.x2 = t;
		}
		if (p.y1 > p.y2){
			int t = p.y1;
			p.y1 = p.y2;
			p.y2 = t;
		}
		if (p.z1 > p.z2){
			int t = p.z1;
			p.z1 = p.z2;
			p.z2 = t;
		}
		return p;
	}
	public TRPos(int x1, int y1, int z1, int x2, int y2, int z2){
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
	}
	public TRPos(@NonNull String[] temp){
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
	
	public boolean contains(@NonNull Location loc){
		int x = loc.getBlockX();
		if (x < x1 || x > x2) return false;
		int z = loc.getBlockZ();
		if (z < z1 || z > z2) return false;
		int y = loc.getBlockY();
		if (y < y1 || y > y2) return false;
		return true;
	}
	
	public boolean contains(int x, int y, int z) {
		if (x < x1 || x > x2) return false;
		if (z < z1 || z > z2) return false;
		if (y < y1 || y > y2) return false;
		return true;
	}
	
	public boolean containsIgnoreY(@NonNull Location loc){
		int x = loc.getBlockX();
		if (x < x1 || x > x2) return false;
		int z = loc.getBlockZ();
		if (z < z1 || z > z2) return false;
		return true;
	}
	
	public boolean containsIgnoreY(int x, int z){
		if (x < x1 || x > x2) return false;
		if (z < z1 || z > z2) return false;
		return true;
	}
	
	public Location getLesserCorner(World world){
		return new Location(world, x1, y1, z1);
	}
	
	public Location getCenter(World world){
		return new Location(world, (x1+x2)/2, (y1+y2)/2, (z1+z2)/2);
	}
	
	public Location getGreaterCorner(World world){
		return new Location(world, x2, y2, z2);
	}
	
	@NonNull public Location toLoc(World world){
		return new Location(world, (x1+x2)/2, (y1+y2)/2, (z1+z2)/2);
	}
	
	@Override
	public String toString(){
		return ""+x1+","+y1+","+z1+","+x2+","+y2+","+z2;
	}
	
}
