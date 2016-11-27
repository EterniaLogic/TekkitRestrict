package ee;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class EEPBase {
	public static int getKleinPoints(ItemStack var1) {
		return getInteger(var1, "points");
	}

	public static void setKleinPoints(ItemStack var0, int var1) {
		setInteger(var0, "points", var1);
	}
	
	@SuppressWarnings("unchecked")
	public static <M extends TileEntity> M getTileEntity(World world, int x, int y, int z, Class<M> clazz){
		if (y < 0) return null;
		TileEntity te = world.getTileEntity(x, y, z);
		return clazz.isInstance(te) ? (M) te : null;
	}
	
	/*
	public static TileEntity getTileEntity(World world, int x, int y, int z){
		if (y >= 256) return null;
		try {
			Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
	
			if (chunk == null) return null;
			
			TileEntity tileentity = getTileEntity2(world, x, y, z, false);
			//TileEntity tileentity = chunk.e(x & 0xF, y, z & 0xF);
	
			if (tileentity == null) {
				Iterator<TileEntity> iterator = ((List<TileEntity>) world.getClass().getField("J").get(world)).iterator();;
	
				while (iterator.hasNext()) {
					TileEntity tileentity1 = iterator.next();
	
					if (!tileentity1.l() && tileentity1.x == x && tileentity1.y == y && tileentity1.z == z) {
						tileentity = tileentity1;
						break;
					}
				}
			}
			return tileentity;
		} catch (Exception ex){
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <M extends TileEntity> M getTileEntity2(World world, int x, int y, int z, boolean set, Class<M> clazz){
		if (y < 0) return null;
		TileEntity te = getTileEntity2(world, x, y, z, set);
		return clazz.isInstance(te) ? (M) te : null;
	}
	
	public static TileEntity getTileEntity2(World world, int x, int y, int z, boolean set){
		try {
			Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
			x = x & 0xF;
			z = z & 0xF;
			ChunkPosition chunkposition = new ChunkPosition(x, y, z);
			TileEntity tileentity = (TileEntity)chunk.tileEntities.get(chunkposition);
			if (!set) return tileentity;
			
			if ((tileentity != null) && (tileentity.l())) {
				chunk.tileEntities.remove(chunkposition);
				tileentity = null;
			}

			if (tileentity == null) {
				int l = chunk.getTypeId(x, y, z);

				int meta = chunk.getData(x, y, z);
				if (l <= 0 || Block.byId[l] == null || !Block.byId[l].hasTileEntity(meta)) {
					return null;
				}

				tileentity = Block.byId[l].getTileEntity(meta);
				world.setTileEntity(chunk.x*16 + x, y, chunk.z*16 + z, tileentity);

				tileentity = (TileEntity)chunk.tileEntities.get(chunkposition);
			}
			
			return tileentity;
		} catch (Exception ex){
			return null;
		}
	}
	*/
	
	public static EntityHuman getHuman(World world, String name){
		return world.a(name);
	}

	public static void setDataAndUpdate(EntityHuman human, int x, int y, int z, int id, int data){
		human.world.setData(x, y, z, data);
		Player player = ((Player) human.getBukkitEntity());
		player.sendBlockChange(new Location(player.getWorld(), x, y, z), id, (byte) data);
	}

	public static String getString(ItemStack var1, String var2) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		if (!(var1.tag.hasKey(var2))) {
			setString(var1, var2, "");
		}

		return var1.tag.getString(var2);
	}

	public static void setString(ItemStack var1, String var2, String var3) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		var1.tag.setString(var2, var3);
	}

	public static boolean getBoolean(ItemStack var1, String var2) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		if (!(var1.tag.hasKey(var2))) {
			setBoolean(var1, var2, false);
		}

		return var1.tag.getBoolean(var2);
	}

	public static void setBoolean(ItemStack var1, String var2, boolean var3) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		var1.tag.setBoolean(var2, var3);
	}

	public static short getShort(ItemStack var1, String var2) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		if (!(var1.tag.hasKey(var2))) {
			setShort(var1, var2, 0);
		}

		return var1.tag.getShort(var2);
	}

	public static void setShort(ItemStack var1, String var2, int var3) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		var1.tag.setShort(var2, (short) var3);
	}

	public static int getInteger(ItemStack var1, String var2) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		if (!(var1.tag.hasKey(var2))) {
			setInteger(var1, var2, 0);
		}

		return var1.tag.getInt(var2);
	}

	public static void setInteger(ItemStack var1, String var2, int var3) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		var1.tag.setInt(var2, var3);
	}

	public static byte getByte(ItemStack var1, String var2) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		if (!(var1.tag.hasKey(var2))) {
			setByte(var1, var2, (byte) 0);
		}

		return var1.tag.getByte(var2);
	}

	public static void setByte(ItemStack var1, String var2, byte var3) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		var1.tag.setByte(var2, var3);
	}

	public static long getLong(ItemStack var1, String var2) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		if (!(var1.tag.hasKey(var2))) {
			setLong(var1, var2, 0L);
		}

		return var1.tag.getLong(var2);
	}

	public static void setLong(ItemStack var1, String var2, long var3) {
		if (var1.tag == null) {
			var1.setTag(new NBTTagCompound());
		}

		var1.tag.setLong(var2, var3);
	}

	
	public static void onUpdate_KleinStar(ItemStack var1) {
		int max = ItemKleinStar.getMax_s(var1);
		if (max <= 0) return;
		int points = getKleinPoints(var1);
		if (points == 0){
			var1.setData(0);
		} else if (points <= max) {
			var1.setData(var1.i() - (points / (max/1000)));
		} else {
			var1.setData(1);
		}
	}

}
