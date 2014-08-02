package nl.taico.tekkitrestrict.functions.limiter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import nl.taico.taeirlib.collections.KeepChangesReentrantMultiKeyMap;
import nl.taico.taeirlib.concurrent.ReentrantMultiKeyMap;
import nl.taico.tekkitrestrict.TRPermHandler;
import nl.taico.tekkitrestrict.objects.TRConfigLimit;
import nl.taico.tekkitrestrict.objects.TRPermLimit;

public class PlayerLimitsLoadAll {
	
	private AtomicInteger plrNr = new AtomicInteger();
	/**
	 * A Map of PlayerName, PlayerID.
	 */
	private ConcurrentHashMap<String, Integer> players = new ConcurrentHashMap<String, Integer>(64, 0.75f, 4);
	private ConcurrentHashMap<String, PlayerLimits> limits = new ConcurrentHashMap<String, PlayerLimits>(64, 0.75f, 6);
	
	//KeepChangesReentrantMultiKeyMap
	private KeepChangesReentrantMultiKeyMap<Integer, LBlock> index = new KeepChangesReentrantMultiKeyMap<>(new ReentrantMultiKeyMap<>(new MultiKeyMap<Integer, LBlock>()));

	private ReentrantMultiKeyMap<Integer, HashMap<Integer, Set<LBlock>>> index2 = new ReentrantMultiKeyMap<>(new MultiKeyMap<Integer, HashMap<Integer, Set<LBlock>>>());
	private HashMap<String, Integer> worlds = new HashMap<>();
	
	protected static HashMap<String, List<TRPermLimit>> limiterPermCache = new HashMap<>();
	protected static List<TRConfigLimit> configLimits = new ArrayList<>();
	
	private int highest;
	private void load(Connection con){
		try (Statement s = con.createStatement()){
			try (ResultSet rs = s.executeQuery("SELECT * FROM tr_worlds")){
				while (rs.next()){
					int id = rs.getInt(1);
					String name = rs.getString(2);
					worlds.put(name, id);
					if (id > highest) highest = id;
				}
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		for (World world : Bukkit.getWorlds()){
			//TODO Listen for world load and unload events
			if (worlds.get(world.getName()) == null){
				try (PreparedStatement ps = con.prepareStatement("INSERT INTO tr_worlds (id, name) VALUES (?, ?)")){
					ps.setInt(1, ++highest);
					ps.setString(2, world.getName());
					ps.executeUpdate();
					worlds.put(world.getName(), highest);
				} catch (SQLException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
		
		try (Statement s = con.createStatement()){
			try (ResultSet rs = s.executeQuery("SELECT * FROM tr_limits;")){
				while (rs.next()){
					int x = rs.getInt(1);
					int y = rs.getInt(2);
					int z = rs.getInt(3);
					int world = rs.getInt(4);
					int owner = rs.getInt(5);
					int id = rs.getInt(6);
					int data = rs.getInt(7);
					//owner, id, data
					LBlock l = new LBlock(x, y, z, world, owner, id, data);
					index.put(x, y, z, world, l);
					HashMap<Integer, Set<LBlock>> h = index2.get(owner, id);
					if (h == null){
						h = new HashMap<Integer, Set<LBlock>>(2);
						index2.put(owner, id, h);
					}
					Set<LBlock> sl = h.get(data);
					if (sl == null){
						sl = new HashSet<LBlock>(8);
						h.put(data, sl);
					}
					sl.add(l);
				}
			}
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		try (Statement s = con.createStatement()){
			try (ResultSet rs = s.executeQuery("SELECT * FROM tr_players;")){
				while (rs.next()){
					int id = rs.getInt(1);
					String name = rs.getString(2);
					players.put(name, id);
					if (id > plrNr.get()) plrNr.set(id);
				}
			}
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	public void onPlace(BlockPlaceEvent event){
		Integer plr = getPlayer(event.getPlayer().getName());
		
	}
	
	public LBlock placeBlock(int x, int y, int z, int world, int player, int id, int data){
		LBlock block = new LBlock(x, y, z, world, player, id, data);
		index.put(x, z, y, world, block);
		HashMap<Integer, Set<LBlock>> h = index2.get(player, id);
		if (h == null){
			h = new HashMap<Integer, Set<LBlock>>(2);
			index2.put(player, id, h);
		}
		Set<LBlock> s = h.get(data);
		if (s == null){
			s = new HashSet<LBlock>(8);
			h.put(data, s);
		}
		s.add(block);
		return block;
	}
	
	//Async read access
	public LBlock getLimitBlock(int x, int y, int z, int world){
		return index.get(x, z, y, world);
	}
	
	//WARNING: DO NOT MODIFY THE RETURNED SET!
	public Set<LBlock> getPlaced(int player, int id, int data){
		if (data == -1){
			HashMap<Integer, Set<LBlock>> h = index2.get(player, id);
			if (h == null) return null;
			HashSet<LBlock> tbr = new HashSet<>();
			for (Set<LBlock> s : h.values()) tbr.addAll(s);
			
			return tbr;
		} else {
			HashMap<Integer, Set<LBlock>> h = index2.get(player, id);
			if (h == null) return null;
			return h.get(data);
		}
	}
	
	public int getAmount(int player, int id, int data){
		if (data == -1){
			HashMap<Integer, Set<LBlock>> h = index2.get(player, id);
			if (h == null) return 0;
			int nr = 0;
			for (Set<LBlock> s : h.values()) nr += s.size();
			
			return nr;
		} else {
			HashMap<Integer, Set<LBlock>> h = index2.get(player, id);
			if (h == null) return 0;
			Set<LBlock> s = h.get(data);
			return s == null ? 0 : s.size();
		}
	}

	public int getMax(int player, int id, int data){
		String name = getPlayer(player);
		if (name == null){
			return getConfigMax(id, data);
		} else {
			Player plr = Bukkit.getPlayerExact(name);
			if (plr == null) return getConfigMax(id, data);
			
			List<TRPermLimit> cached = limiterPermCache.get(name);
			if (cached != null){
				boolean found = false;
				for (final TRPermLimit lim : cached){
					if (lim.compare(id, data)){
						if (lim.max == -2) return -1;
						if (lim.max != -1) return lim.max;
						
						found = true;
					}
				}
				if (found) return -1;
			}
			
			final TRPermLimit pl = TRPermHandler.getPermLimitFromPerm(plr, "tekkitrestrict.limiter", id, data);
			if (pl != null) {
				if (cached == null) cached = new ArrayList<TRPermLimit>();
				cached.add(pl);
				limiterPermCache.put(name, cached);
				if (pl.max == -2) return -1;
				return pl.max;
			} else {
				return getConfigMax(id, data);
			}
		}
	}
	
	protected String lastString = null;
	public int getConfigMax(int id, int data){
		//READ
		for (final TRConfigLimit cc : configLimits) {
			if (cc.compare(id, data)) {
				lastString = cc.msg;
				return cc.configcount;
			}
		}
		return -1;
	}
	
	/**
	 * @param player
	 * @return The name of the player with this ID, in lowercase.
	 * {@code null} if there is no player with the given id.
	 */
	public String getPlayer(int player){
		for (Entry<String, Integer> e : players.entrySet()){
			if (e.getValue().intValue() == player) return e.getKey();
		}
		return null;
	}
	
	public Integer getPlayer(String player){
		if (player == null) return null;
		return players.get(player.toLowerCase());
	}

	public Integer addNewPlayer(String player){
		if (player == null) return null;
		return players.put(player.toLowerCase(), plrNr.incrementAndGet());
	}
}
