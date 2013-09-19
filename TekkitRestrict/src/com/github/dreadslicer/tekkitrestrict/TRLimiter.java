package com.github.dreadslicer.tekkitrestrict;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.dreadslicer.tekkitrestrict.Log.Warning;
import com.github.dreadslicer.tekkitrestrict.annotations.Safe;
import com.github.dreadslicer.tekkitrestrict.objects.TRConfigLimit;
import com.github.dreadslicer.tekkitrestrict.objects.TRLimit;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;


public class TRLimiter {
	private int expire = -1;
	public String player = "";
	public boolean isModified = true; // default limiters will get saved.
	/** A list of different kinds of limited blocks and the locations where they are placed (For this player). */
	public List<TRLimit> itemlimits = Collections.synchronizedList(new LinkedList<TRLimit>());
	
	private static CopyOnWriteArrayList<TRLimiter> limiters = new CopyOnWriteArrayList<TRLimiter>();
	//private static List<TRLimiter> limiters = Collections.synchronizedList(new LinkedList<TRLimiter>());
	private static List<TRConfigLimit> configLimits = Collections.synchronizedList(new LinkedList<TRConfigLimit>());
	private static Map<String, String> allBlockOwners = Collections.synchronizedMap(new HashMap<String, String>());

	public static void reload() {
		List<String> limitedBlocks = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "LimitBlocks");
		configLimits.clear();
		for (String limBlock : limitedBlocks) {
			try {
				String[] temp = limBlock.split(" ");
				if (temp.length!=2){
					Warning.config("You have an error in your Advanced.config.yml in LimitBlocks!");
					Warning.config("\""+limBlock+"\" does not follow the syntaxis \"itemIndex limit\"!");
					continue;
				}
				int limit = 0;
				try {
					limit = Integer.parseInt(temp[1]);
				} catch (NumberFormatException ex){
					Warning.config("You have an error in your Advanced.config.yml in LimitBlocks!");
					Warning.config("\""+temp[1]+"\" is not a valid number!");
					continue;
				}
				
				for (TRCacheItem ci : TRCacheItem.processItemStringNoCache(temp[0])){
					TRConfigLimit cLimit = new TRConfigLimit();
					cLimit.id = ci.id;
					cLimit.data = ci.data;
					cLimit.configcount = limit;
					configLimits.add(cLimit);
				}
				//TRCacheItem.processItemString("limiter", "afsd90ujpj", temp[0], limit);
				/*
				 * ItemStack[] ar = TRNoItem.getRangedItemValues(g[0]); int
				 * limit = Integer.valueOf(g[1]); for (ItemStack iss : ar) {
				 * TRLimit ccr = new TRLimit(); for (int i = 0; i < limit; i++)
				 * { ccr.placedBlock.add(null); } ccr.blockID = iss.id;
				 * ccr.blockData = iss.getData(); configLimits.add(ccr); }
				 */
			} catch (Exception e) {
				Warning.config("LimitBlocks: has an error!");
			}
		}
	}
	
	/** Remove all limits from this player. */
	public void clearLimits() {
		for (TRLimit ll : itemlimits) {
			ll.placedBlock.clear();
		}
		itemlimits.clear();
	}
	
	public int getMax(Player player, int thisid, int thisdata){
		int max = -1;
		try {
			for (int i = 0; i < configLimits.size(); i++) {
				TRConfigLimit cc = configLimits.get(i);
				if (cc.equals(thisid, thisdata)) {
					max = cc.configcount;
					if (max != -1) return max;
				}
			}
			
			max = TRPermHandler.getPermNumeral(player, "tekkitrestrict.limiter", thisid, thisdata);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return max;
	}

	/**
	 * If the player has not yet maxed out his limits, it will add the placed block to his limits.
	 * @return Whether a player has already maxed out their limits.
	 * @see TRListener#onBlockPlace(BlockPlaceEvent) Used by TRListener.onBlockPlace(BlockPlaceEvent)
	 */
	public boolean checkLimit(BlockPlaceEvent event, boolean doBypassCheck) {
		boolean r = true;
		// list.addAll(getPermLimits(event.getPlayer()));
		// tekkitrestrict.log.info(list.toString());
		// get list of permission-based limits for this player
		if (doBypassCheck && event.getPlayer().hasPermission("tekkitrestrict.bypass.limiter")) return true;
		Block block = event.getBlock();
		int thisid = block.getTypeId();
		int thisdata = block.getData();
		
		Location bloc = block.getLocation();
		
		int TLimit = getMax(event.getPlayer(), thisid, thisdata);//Get the max for this player for id:data
		//tekkitrestrict.log.info("[DEBUG] getMax("+event.getPlayer().getName()+","+thisid+","+thisdata+") = "+TLimit);
		
		if (TLimit != -1) {
			// tekkitrestrict.log.info("limited?");
			for (int i = 0; i < itemlimits.size(); i++) {
				TRLimit limit = itemlimits.get(i);

				if (limit.id != thisid || limit.data != thisdata) continue;
				
				int currentnum = limit.placedBlock.size();
				if (currentnum >= TLimit) {
					// this would be at max.
					return false;
				} else {
					// loop through the placedblocks to make sure that we
					// aren't placing the same one down twice.
					boolean place2 = false;
					for (int j = 0; j < limit.placedBlock.size(); j++) {
						if (limit.placedBlock.get(j).equals(bloc)) {
							place2 = true;
						}
					}
					
					if (!place2) {
						limit.placedBlock.add(block.getLocation());

						int x = bloc.getBlockX();
						int y = bloc.getBlockY();
						int z = bloc.getBlockZ();
						allBlockOwners.put(bloc.getWorld().getName() + ":" + x + ":" + y + ":" + z, event.getPlayer().getName());
						isModified = true;
						return true;
					} else {
						//This block is already in the placed list, so allow placement but do not increment counts.
						return true;
					}
				}
			}

			// it hasn't quite gone through yet. We need to make a new limiter
			// and add this block to it.
			TRLimit g = new TRLimit();
			g.id = thisid;
			g.data = thisdata;
			g.placedBlock.add(bloc);
			itemlimits.add(g);
			
			int x = bloc.getBlockX();
			int y = bloc.getBlockY();
			int z = bloc.getBlockZ();
			allBlockOwners.put(bloc.getWorld().getName() + ":" + x + ":" + y + ":" + z, event.getPlayer().getName());
			isModified = true;
			// Making new
			return true;
		}

		return r;
	}

	public void checkBreakLimit(BlockBreakEvent event) {

		// loop through player's limits.
		
		int id = event.getBlock().getTypeId();
		byte data = event.getBlock().getData();
		for (int i = 0; i < itemlimits.size(); i++) {
			TRLimit limit = itemlimits.get(i);

			if (limit.id != id || limit.data != data) continue;
			
			int currentnum = limit.placedBlock.size();
			if (currentnum <= 0) {
				// this would be at minimum
				// (LOG) maxed out
				return;
			} else {
				// add to it!
				Location bloc = event.getBlock().getLocation();
				limit.placedBlock.remove(bloc);
				
				int x = bloc.getBlockX();
				int y = bloc.getBlockY();
				int z = bloc.getBlockZ();
				allBlockOwners.remove(bloc.getWorld().getName() + ":" + x + ":" + y + ":" + z);
				isModified = true;
				return;
			}
			
		}
	}

	/**
	 * First checks all loaded limiters.<br>
	 * If it cannot find this player's limiter, it will look into the database.<br>
	 * If that also results in nothing, it will create a new limiter.
	 * @return The limiter the given player has. If it doesn't exist, it creates one.
	 */
	public static TRLimiter getLimiter(String playerName) {
		playerName = playerName.toLowerCase();
		// check if a previous itemlimiter exists...

		for (TRLimiter il : limiters){
			if (il.player.toLowerCase().equals(playerName)) {
				return il;
			}
		}
		//Player is not loaded or offline.
		
		TRLimiter r = new TRLimiter();
		r.player = playerName;
		limiters.add(r);
		
		//If player is online, check for bypass.
		Player p = Bukkit.getPlayer(playerName);
		if (p != null && p.hasPermission("tekkitrestrict.bypass.limiter")) return r; //return an empty limiter
		
		//If player is offline or isn't loaded, check the database.
		ResultSet dbin = null;
		try {
			dbin = tekkitrestrict.db.query("SELECT * FROM `tr_limiter` WHERE `player` = '" + playerName + "';");
			if (dbin.next()) {
				//This player exists in the database, so now we have to load him/her up.

				// add data
				String blockdata = dbin.getString("blockdata");
				dbin.close(); //We dont need dbin any more from here so we can close it.
				// scheme:
				// id:data/DATA|id:data/DATA
				if (blockdata.length() >= 3) {
					if (blockdata.contains("%")) {
						String[] prelimits = blockdata.split("%");
						for (int i = 0; i < prelimits.length; i++) {
							r.itemlimits.add(loadLimitFromString(prelimits[i]));
						}
					} else {
						r.itemlimits.add(loadLimitFromString(blockdata));
					}
				}

				for (TRLimit l : r.itemlimits) {
					for (Location l1 : l.placedBlock) {
						allBlockOwners.put(l1.getWorld().getName() + ":" + l1.getBlockX() + ":" + l1.getBlockY() + ":" + l1.getBlockZ(), r.player);
					}
				}

				return r;
			}
			
			dbin.close();
		} catch (Exception e) {
			try {
				if (dbin != null) dbin.close();
			} catch (SQLException e1) {}
		}

		return r; //Return an empty limiter
	}

	/** @return The limiter the given player has. If it doesn't exist, it creates one. */
	public static TRLimiter getOnlineLimiter(Player player) {
		String playerName = player.getName().toLowerCase();
		// check if a previous itemlimiter exists...

		for (TRLimiter il : limiters){
			if (il.player.toLowerCase().equals(playerName)) {
				return il;
			}
		}
		
		
		TRLimiter r = new TRLimiter();
		r.player = playerName;
		limiters.add(r);
		
		if (player.hasPermission("tekkitrestrict.bypass.limiter")) return r; //return an empty limiter
		
		// check to see if this player exists in the database...
		ResultSet dbin = null;
		try {
			dbin = tekkitrestrict.db.query("SELECT * FROM `tr_limiter` WHERE `player` = '" + playerName + "';");
			if (dbin.next()) {
				// This player exists in the database!!!
				// load them up!

				// add data
				String blockdata = dbin.getString("blockdata");
				dbin.close(); //We dont need dbin any more from here so we can close it.
				// scheme:
				// id:data/DATA|id:data/DATA
				if (blockdata.length() >= 3) {
					if (blockdata.contains("%")) {
						String[] prelimits = blockdata.split("%");
						for (int i = 0; i < prelimits.length; i++) {
							r.itemlimits.add(loadLimitFromString(prelimits[i]));
						}
					} else {
						r.itemlimits.add(loadLimitFromString(blockdata));
					}
				}

				for (TRLimit l : r.itemlimits) {
					for (Location l1 : l.placedBlock) {
						allBlockOwners.put(l1.getWorld().getName() + ":" + l1.getBlockX() + ":" + l1.getBlockY() + ":" + l1.getBlockZ(), r.player);
					}
				}

				return r;
			}
			
			dbin.close();
		} catch (Exception e) {
			try {
				if (dbin != null) dbin.close();
			} catch (SQLException e1) {}
		}

		return r; //Return an empty limiter
	}
	
	/** Note: You should add the locations in this limit to allBlockOwners after calling this method. */
	private static TRLimit loadLimitFromString(String ins) {
		// ins = ins.replace("/", "!");
		String[] limit = ins.split("&");

		TRLimit l = new TRLimit();
		if (limit.length == 2) {
			String item = limit[0];
			String locStr = limit[1];
			// block id parse
			if (item.length() > 0) {
				if (item.contains(":")) {
					// c = org.bukkit.block.
					String[] mat = item.split(":");
					try {
						l.id = Integer.parseInt(mat[0]);
						l.data = Byte.parseByte(mat[1]);
					} catch (NumberFormatException ex){
						Warning.config("Invalid limiter value in database: \""+item+"\"!");
						l.id = 0;
						l.data = 0;
					}
				} else {
					try {
						l.id = Integer.parseInt(item);
					} catch (NumberFormatException ex){
						Warning.config("Invalid limiter value in database: \""+item+"\"!");
						l.id = 0;
					}
				}

				// DATA parse:
				// world,x,y,z=world,x,y,z=...=...
				if (locStr.length() > 0) {
					if (locStr.contains("_")) {
						String[] datas = locStr.split("_");
						for (int j = 0; j < datas.length; j++) {
							Location loc = locParse(datas[j]);
							if (loc != null) {
								l.placedBlock.add(loc);
							}
						}
					} else {
						Location loc = locParse(locStr);
						if (loc != null) {
							l.placedBlock.add(loc);
						}
					}
				}
			}
		}
		return l;
	}

	/** Save the limiters to the database. */
	public static void saveLimiters() {
		// looping through each player's limiters

		for (TRLimiter lb : limiters){
			saveLimiter(lb);
		}
	}

	/**
	 * Called by QuitListener.quit(Player) to make a players limits expire (after 6x32 = 192 ticks) when he logs off.
	 * @see com.github.dreadslicer.tekkitrestrict.listeners.QuitListener#quit(Player) QuitListener.quit(Player)
	 * @see tekkitrestrict#initHeartBeat()
	 */
	public static void setExpire(String player) {
		player = player.toLowerCase();
		// gets the player from the list of limiters (does nothing if player doesn't exist)

		for (int i = 0; i < limiters.size(); i++) {
			TRLimiter il = limiters.get(i);
			if (!il.player.equals(player)) continue;
			
			il.expire = 6; // Every 32 ticks 32*6 = Every 192 ticks
			return;
		}
	}

	/**
	 * Dynamically unload a player (after they logout).<br>
	 * Saves the limiter first, then uses <code>limitBlock.clearLimits()</code> and
	 * then uses <code>limiters.remove(limitBlock)</code>
	 * @see #saveLimiter(TRLimiter)
	 */
	private static void deLoadLimiter(TRLimiter limitBlock) {
		saveLimiter(limitBlock);
		// clear limits
		limitBlock.clearLimits();
		// remove this limiter from the list.
		limiters.remove(limitBlock);
	}

	private static boolean logged = false, logged2 = false;
	
	/** Saves 1 limitblock to the database. */
	@Safe(allownull = false)
	private static void saveLimiter(TRLimiter lb) {
		if (lb.player == null){
			Warning.other("An error occurred while saving the limits! Error: Null player name!");
			return;
		}
		String player = lb.player.toLowerCase();
		String blockdata = "";
		
		try {
			int size = lb.itemlimits.size();
			for (int j = 0; j < size; j++) {
				String suf;
				if (j == size - 1)
					suf = "";
				else
					suf = "%";
				
				// loop through each Limit
				TRLimit limit1 = lb.itemlimits.get(j);
				
				if (limit1.id == -1) continue;
				
				String block = "" + limit1.id; //id or id:data
				
				// set data if it exists
				if (limit1.data == 0)
					block = "" + limit1.id;
				else
					block = "" + limit1.id + ":" + limit1.data;
				
				
				// Get DATA.
				String DATA = "";
				// loop through each block data
				int size2 = limit1.placedBlock.size();
				for (int k = 0; k < size2; k++) {
					String suf1;
					if (k == size2 - 1)
						suf1 = "";
					else
						suf1 = "_";
					
					Location l = limit1.placedBlock.get(k);
					//DATA = world,x,y,z_world,x,y,z
					DATA += l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + suf1;
				}
				String datarr = "";
				if (size2 > 0) {
					datarr = block + "&" + DATA + suf;
				}
				blockdata += datarr;
			}
		} catch (Exception ex) {
			if (!logged){
				Warning.other("An error occurred while saving the limits! Error: Cannot create string to save to database!");
				logged = true;
			}
		}
		/*
		int estID = 0;
		// get a numeral from the player's name.
		for (int i = 0; i < player.length(); i++) {
			char c = player.charAt(i); //TODO FIXME IMPORTANT This can be the same for multiple names. (abba = 1+2+2+1=6, baab = 2+1+1+2 = 6)
			estID += Character.getNumericValue(c);
		}
		*/
		if (blockdata.equals("")) return;
		try {
				//tekkitrestrict.db.query("INSERT OR REPLACE INTO `tr_limiter` (`id`,`player`,`blockdata`) VALUES ("
				//						+ estID
				//						+ ",'"
				//						+ player
				//						+ "','"
				//						+ blockdata
				//						+ "')");
				tekkitrestrict.db.query("INSERT OR REPLACE INTO `tr_limiter` (`player`,`blockdata`) VALUES ('"
						+ player
						+ "','"
						+ blockdata
						+ "');");
			
		} catch (SQLException ex) {
			if (!logged2){
				Warning.other("An error occurred while saving the limits! Error: Cannot insert into database!");
				logged2 = true;
			}
		}
	}

	/** Parses a String formatted like <code>"world,x,y,z"</code> to a location. */
	private static Location locParse(String ins) {
		Location l = null;
		try {
			if (ins.contains(",")) {
				// determine if the world for this exists...
				String[] lac = ins.split(",");
				World cw = Bukkit.getWorld(lac[0]);
				if (cw != null) {
					l = new Location(cw, Integer.parseInt(lac[1]), Integer.parseInt(lac[2]), Integer.parseInt(lac[3]));
				}
			}
		} catch (Exception ex){
			Warning.other("Error while loading a limiter: malformed limiter location in the database!");
		}

		return l;
	}

	/** load all of the block:player pairs from db. */
	public static void init() {
		ResultSet dbin = null;
		try {
			dbin = tekkitrestrict.db.query("SELECT * FROM `tr_limiter`;");
			//if (dbin.next()) {//FIXME IMPORTANT only loads 1 player
			while (dbin.next()) {//FIXME IMPORTANT changed this here to load all players instead of one
				// This player exists in the database!!!
				// load them up!

				// This player does not have a bypass =(
				// add data
				String player = dbin.getString("player").toLowerCase();
				String blockdata = dbin.getString("blockdata");
				// scheme:
				// id:data/DATA|id:data/DATA
				if (blockdata.length() >= 3) {
					if (blockdata.contains("%")) {
						String[] prelimits = blockdata.split("%");
						for (int i = 0; i < prelimits.length; i++) {
							String g = prelimits[i];
							TRLimit L = loadLimitFromString(g);
							List<Location> blks = L.placedBlock;
							for (Location loc : blks) {
								allBlockOwners.put(
										loc.getWorld().getName() + ":"
												+ loc.getBlockX() + ":"
												+ loc.getBlockY() + ":"
												+ loc.getBlockZ(), player);
							}
						}
					} else {
						String g = blockdata;
						TRLimit L = loadLimitFromString(g);
						List<Location> blks = L.placedBlock;
						for (Location loc : blks) {
							allBlockOwners.put(loc.getWorld().getName() + ":"
									+ loc.getBlockX() + ":" + loc.getBlockY()
									+ ":" + loc.getBlockZ(), player);
						}
					}
				}
			}
			dbin.close();
		} catch (Exception ex) {
			try {
				if (dbin != null) dbin.close();
			} catch (SQLException ex2) {}
			Warning.otherWarnings.add("[SEVERE] An error occurred while loading the limiter!");
			tekkitrestrict.log.severe("An error occurred while loading the limiter!");
			Log.Exception(ex, true);
		}
	}

	/** Used by {@link #manageData()} for the Future call. */
	private static Location tempLoc;
	/**
	 * Manages and removes bad data.
	 * Determines if the limit exists at a location. If not, remove it.
	 */
	public static void manageData() {
		for (TRLimiter lb : limiters) {
			boolean changed = false;
			for (TRLimit l : lb.itemlimits) {
				for (int i = 0; i < l.placedBlock.size(); i++) {
					try {
						Location loc = l.placedBlock.get(i);
						tempLoc = loc;
						Future<Chunk> returnFuture = Bukkit.getScheduler().callSyncMethod(tekkitrestrict.getInstance(), new Callable<Chunk>() {
						   public Chunk call() {
							   Chunk c = tempLoc.getChunk();
							   c.load();
						       return c;
						   }
						});

					    // This will block the current thread 
						Chunk returnValue = returnFuture.get();//Load the chunk
						if(returnValue != null){
							Block b = loc.getBlock();
							if (b.getTypeId() != l.id){
								l.placedBlock.remove(i);
								i--;
								changed = true;
							} else if (l.data != b.getData() && !(b.getData() == 0 && l.data == -10) && l.data != -1) {//TODO IMPORTANT FIXME change THIS!
								l.placedBlock.remove(i);
								i--;
								changed = true;
							}
						}
					} catch (Exception e) {
					}
				}
			}
			
			if (changed) saveLimiter(lb);
		}
	}

	/**
	 * Called by the heartbeat to unload limits of players that are offline.
	 * @see #deLoadLimiter(TRLimiter) Uses deLoadLimiter(TRLimitBlock)
	 * @see tekkitrestrict#initHeartBeat() Called by tekkitrestrict.initHeartBeat()
	 */
	public static void expireLimiters() {
		// loop through each limiter.
		ArrayList<TRLimiter> tbr = new ArrayList<TRLimiter>();

		for (TRLimiter il : limiters){
			if (il.expire == -1) continue;
			if (il.expire == 0) { // do expire
				tbr.add(il);
				// tekkitrestrict.log.info("Expired limiter");
			} else {
				// tekkitrestrict.log.info("Age limiter");
				il.expire--;
			}
		}
		
		for (TRLimiter lb : tbr){
			deLoadLimiter(lb);
		}
	}

	/** Called when a player logs in to make his limits not expire any more. */
	public static void removeExpire(String playerName) {
		playerName = playerName.toLowerCase();
		for (TRLimiter il : limiters){
			if (il.player.toLowerCase().equals(playerName))
				il.expire = -1;
		}
	}

	/** @return The owner of this block, or null if none is found. */
	public static String getPlayerAt(Block block) {
		// Cache block:owners, so this goes really fast.
		Location bloc = block.getLocation();
		int x = bloc.getBlockX();
		int y = bloc.getBlockY();
		int z = bloc.getBlockZ();
		String pl = allBlockOwners.get(bloc.getWorld().getName() + ":" + x + ":" + y + ":" + z);
		return pl;
	}

	public static ArrayList<String> getDebugInfo(){
		ArrayList<String> tbr = new ArrayList<String>();
		for (TRConfigLimit limit : configLimits){
			tbr.add("L:" + limit.id+":"+limit.data+"_"+limit.configcount);
		}
		
		return tbr;
	}
}
