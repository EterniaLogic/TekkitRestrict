package nl.taico.tekkitrestrict.functions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.WorldServer;
import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TRConfigCache.ChunkUnloader;
import nl.taico.tekkitrestrict.TekkitRestrict;
import nl.taico.tekkitrestrict.objects.TRChunkIndex;
import nl.taico.tekkitrestrict.objects.TREnums.ChunkUnloadMethod;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkUnloadEvent;

public class TRChunkUnloadCommandLogic {
	public static Collection<TRChunkUnloadCommandLogic> getAll(){
		return cus.values();
	}
	public static TRChunkUnloadCommandLogic getForWorld(String worldname){
		return cus.get(worldname.toLowerCase());
	}
	private final World world;
	private ChunkUnloadMethod method;
	private TRChunkIndex index;

	private static HashMap<String, TRChunkUnloadCommandLogic> cus = new HashMap<String, TRChunkUnloadCommandLogic>();

	public TRChunkUnloadCommandLogic(World world){
		this.world = world;
		this.method = ChunkUnloadMethod.UnloadLowWhenForced;
		cus.put(world.getName().toLowerCase(), this);
	}

	public void forceUnload(final CommandSender sender, final ChunkUnloadMethod method){
		final TRChunkIndex index = new TRChunkIndex(world);
		index.index();
		final List<Chunk> toUnload;
		switch (method.nr){
		case 1: {
			toUnload = index.getNormalChunks();
			final Iterator<Chunk> it = toUnload.iterator();
			while (it.hasNext()){
				final Chunk chunk = it.next();
				if (isChunkInUse(chunk.x, chunk.z, ChunkUnloader.maxRadii)) it.remove();
			}
			break;
		} case 2: {
			toUnload = index.getNormalChunks();
			toUnload.addAll(index.getForceLoadedChunks());
			final Iterator<Chunk> it = toUnload.iterator();
			while (it.hasNext()) {
				final Chunk chunk = it.next();
				if (isChunkInUse(chunk.x, chunk.z, ChunkUnloader.maxRadii)) it.remove();
			}
			break;
		} case 3: {
			toUnload = index.getAllChunks();
			final Iterator<Chunk> it = toUnload.iterator();
			while (it.hasNext()){
				final Chunk chunk = it.next();
				if (isChunkInUse(chunk.x, chunk.z, ChunkUnloader.maxRadii)) it.remove();
			}
			break;
		} case 4: {
			toUnload = index.getAllChunks();
			break;
		} default: {
			return;
		}
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(TekkitRestrict.getInstance(), new Runnable(){
			@Override
			public void run(){
				int i = 0;
				for (Chunk chunk : toUnload){
					if (unloadChunk(chunk, true)) i++;
				}
				if (sender instanceof Player) sender.sendMessage(ChatColor.GREEN + "Unloaded " + i + " chunks.");
				Log.info("Unloaded " + i + " chunks");
			}
		});
	}

	public ChunkUnloadMethod getMethod() {
		return method;
	}

	/** @return If there are currently players near that chunk. */
	private boolean isChunkInUse(final int x, final int z, final int dist) {
		try {
			final List<EntityHuman> k = ((CraftWorld) world).getHandle().players;
			for (EntityHuman h : k){
				if ((Math.abs(h.x - (x << 4)) <= dist) && (Math.abs(h.z - (z << 4)) <= dist)) {
					return true;
				}
			}
		} catch (Exception ex){

		}

		return false;
	}

	public void setMethod(ChunkUnloadMethod method) {
		this.method = method;
	}


	public void unload(){
		if (index == null) index = new TRChunkIndex(world);
		index.index();//TODO check last index time
		final List<Chunk> toUnload;
		//if (method.isForced()){
		switch (method.nr){
		case 1:
			toUnload = index.getNormalChunks();
			final Iterator<Chunk> it = toUnload.iterator();
			while (it.hasNext()){
				final Chunk chunk = it.next();
				if (isChunkInUse(chunk.x, chunk.z, ChunkUnloader.maxRadii*2)) it.remove();
			}
			break;
		case 2:
			toUnload = index.getNormalChunks();
			final Iterator<Chunk> it2 = toUnload.iterator();
			while (it2.hasNext()) {
				final Chunk chunk = it2.next();
				if (isChunkInUse(chunk.x, chunk.z, ChunkUnloader.maxRadii)) it2.remove();
			}
			break;
		case 3:
			toUnload = index.getAllChunks();
			final Iterator<Chunk> it3 = toUnload.iterator();
			while (it3.hasNext()){
				final Chunk chunk = it3.next();
				if (isChunkInUse(chunk.x, chunk.z, ChunkUnloader.maxRadii)) it3.remove();
			}
			break;
		case 4:
			toUnload = index.getAllChunks();
			break;
		default: return;
		}
		//} else {

		//}
		Bukkit.getScheduler().scheduleSyncDelayedTask(TekkitRestrict.getInstance(), new Runnable(){
			@Override
			public void run(){
				int i = 0;
				boolean forced = method.isForced();
				for (Chunk chunk : toUnload){
					if (unloadChunk(chunk, forced)) i++;
				}
				Log.fine("Unloaded " + i + " chunks");
			}
		});


		if (!method.isExtreme()){

		} else {

		}

	}

	/**
	 * WARNING: This method should be called synchronized
	 * @param chunk
	 * @param force
	 * @return
	 */
	public boolean unloadChunk(Chunk chunk, boolean force){
		WorldServer world = (WorldServer) chunk.world;
		ChunkProviderServer cps = world.chunkProviderServer;
		if (world.savingDisabled) return false;

		ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled() || force) {
			chunk.removeEntities();
			cps.saveChunk(chunk);
			cps.saveChunkNOP(chunk);
			cps.chunks.remove(chunk.x, chunk.z);
			cps.chunkList.remove(chunk);
		}
		return true;
	}
}
