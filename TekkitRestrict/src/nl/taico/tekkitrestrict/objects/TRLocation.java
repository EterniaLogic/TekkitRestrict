package nl.taico.tekkitrestrict.objects;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;

public class TRLocation {
	public int x, y, z;
	public String world;
	private World worldObj;
	public TRLocation(Location loc){
		world = loc.getWorld().getName();
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
	}
	
	public TRLocation(String world, int x, int y, int z){
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean equals(TRLocation loc){
		return (this.world.equals(loc.world)&& this.x == loc.x && this.y == loc.y && this.z == loc.z);
	}
	
	/**
	 * WARNING: This also loads the chunk.
	 * @return The chunk for this location.
	 */
	public Chunk getChunk(){
		if (worldObj == null){
			for (World w : Bukkit.getWorlds()){
				if (w.getName().equalsIgnoreCase("world")){
					worldObj = w;
					break;
				}
			}
		}
		net.minecraft.server.Chunk c = ((CraftWorld) worldObj).getHandle().chunkProviderServer.chunks.get(x >> 4, z >> 4);
		return worldObj.getChunkAt(x >> 4, z >> 4);
	}

	public Block getBlock() {
		if (worldObj == null){
			for (World w : Bukkit.getWorlds()){
				if (w.getName().equalsIgnoreCase("world")){
					worldObj = w;
					break;
				}
			}
		}
		return worldObj.getBlockAt(x, y, z);
	}
}
