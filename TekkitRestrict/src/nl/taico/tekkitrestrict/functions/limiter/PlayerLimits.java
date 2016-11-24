package nl.taico.tekkitrestrict.functions.limiter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerLimits {
	private static ConcurrentHashMap<String, PlayerLimits> limits = new ConcurrentHashMap<String, PlayerLimits>(16, 0.75f, 6);
	
	public static PlayerLimits getLimits(String player){
		return limits.get(player.toLowerCase());
	}
	//TODO Listen for world load and unload events
	
	private static Connection con;
	private static PreparedStatement delete, select, getId;
	
//	private static String convertWorldName(String name){
//		return name.replace(' ', '_').replaceAll("[^\\p{L}\\p{Nd}]+", "");
//	}
	
	private static HashMap<String, Integer> worlds = new HashMap<>(3);
	private static int highest;
	public static void load(Connection con){
		PlayerLimits.con = con;
		
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
		
		try {
			delete = con.prepareStatement("DELETE FROM tr_limits WHERE x = ? AND y = ? AND z = ? AND world = ?");
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		try {
			select = con.prepareStatement("SELECT * FROM tr_limits WHERE owner = ? AND blockid = ?");
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		try {
			getId = con.prepareStatement("SELECT id FROM tr_players WHERE name = ?");
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	//1 Have all limits loaded - PlayerLimitsLoadAll
	//2 Have all online players loaded, and update db - PlayerLimitsLoadOnline
	
	//On Place, update block of that type
	//Table players
	//integer id PRIMARY KEY, Text owner UNIQUE KEY
	
	//Table tr_limits
	//integer x, integer y, integer z, int world, int owner, integer blockid, integer blockdata
	//PRIMARY KEY(x, y, z, world)
	
	//MONITOR
	public void onBlockBreak(BlockBreakEvent event){
		if (event.isCancelled()) return;
		Block block = event.getBlock();

		try {
			delete.setInt(1, block.getX());
			delete.setInt(2, block.getY());
			delete.setInt(3, block.getZ());
			delete.setInt(4, worlds.get(block.getWorld().getName()));
			delete.executeUpdate();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	//HIGH
	public void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock();
		//SELECT * FROM tr_limits 
	}
	
	//TODO ASYNC!
	public void onLogin(PlayerJoinEvent event){
		String name = event.getPlayer().getName().toLowerCase();
		synchronized (getId){
			try {
				getId.setString(1, event.getPlayer().getName().toLowerCase());
				try (ResultSet rs = getId.executeQuery()){
					if (rs.next()){
						
					}
				}
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}
}
