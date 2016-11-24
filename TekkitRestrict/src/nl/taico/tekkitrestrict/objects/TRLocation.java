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
		worldObj = loc.getWorld();
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

	@Override
	public boolean equals(Object obj){
		if (obj == this) return true;

		if (!(obj instanceof TRLocation)) return false;

		return equals((TRLocation) obj);
	}

	public boolean equals(TRLocation loc){
		return (this.x == loc.x) &&
				(this.y == loc.y) &&
				(this.z == loc.z) &&
				this.world.equalsIgnoreCase(loc.world);
	}

	public Block getBlock() {
		if (!loadWorld()) return null;
		return worldObj.getBlockAt(x, y, z);
	}

	/**
	 * WARNING: This also loads the chunk.
	 * @return The chunk for this location.
	 */
	public Chunk getChunk(){
		if (!loadWorld()) return null;

		return worldObj.getChunkAt(x >> 4, z >> 4);
	}

	public Chunk getChunkNoLoad(){
		if (!loadWorld()) return null;

		return ((CraftWorld) worldObj).getHandle().chunkProviderServer.chunks.get(x >> 4, z >> 4).bukkitChunk;
	}

	@Override
	public int hashCode(){
		return (31 * ((31 * (31 + x)) + y)) + z;
	}

	private boolean loadWorld(){
		if (worldObj != null) return true;
		for (World w : Bukkit.getWorlds()){
			if (w.getName().equalsIgnoreCase(world)){
				worldObj = w;
				return true;
			}
		}
		return false;
	}
}
