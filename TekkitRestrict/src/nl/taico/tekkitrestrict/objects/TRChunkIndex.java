package nl.taico.tekkitrestrict.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

import forge.ForgeHooks;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.EmptyChunk;
import net.minecraft.server.WorldServer;

public class TRChunkIndex {
	private World world;
	private ArrayList<Chunk> spawnChunks = new ArrayList<Chunk>();
	private ArrayList<Chunk> forceloadedChunks = new ArrayList<Chunk>();
	private ArrayList<Chunk> normalChunks = new ArrayList<Chunk>();
	protected int total;
	
	public TRChunkIndex(World world){
		this.world = world;
	}
	
	public List<Chunk> getSpawnChunks(){
		return new ArrayList<Chunk>(spawnChunks);
	}
	
	public List<Chunk> getForceLoadedChunks(){
		return new ArrayList<Chunk>(forceloadedChunks);
	}
	
	public List<Chunk> getNormalChunks(){
		return new ArrayList<Chunk>(normalChunks);
	}
	
	public List<Chunk> getAllChunks(){
		List<Chunk> tbr = new ArrayList<Chunk>(normalChunks.size()+forceloadedChunks.size()+spawnChunks.size());
		tbr.addAll(normalChunks);
		tbr.addAll(forceloadedChunks);
		tbr.addAll(spawnChunks);
		return tbr;
	}
	
	@SuppressWarnings("null")
	public void index(){
		final WorldServer ws = ((CraftWorld) world).getHandle();
		final ChunkProviderServer provider = ws.chunkProviderServer;
		final boolean keepspawn = ws.keepSpawnInMemory;
		final ChunkCoordinates spawn = keepspawn ? ws.getSpawn() : null;
		@SuppressWarnings("unchecked")
		ArrayList<Chunk> all = new ArrayList<Chunk>(provider.chunkList);
		total = all.size();
		for (Chunk chunk : all){
			if (chunk == null || chunk instanceof EmptyChunk || provider.isChunkLoaded(chunk.x, chunk.z)) continue;
			if (hasChunkLoader(chunk)) forceloadedChunks.add(chunk);
			
			if (keepspawn){
				int k = chunk.x * 16 + 8 - spawn.x;
				int l = chunk.z * 16 + 8 - spawn.z;
	
				if (k >= -128 && k <= 128 && l >= -128 && l <= 128) spawnChunks.add(chunk);
				else normalChunks.add(chunk);
			} else {
				normalChunks.add(chunk);
			}
		}
	}
	
	private static boolean hasChunkLoader(Chunk chunk){
		return !ForgeHooks.canUnloadChunk(chunk);
	}
}
