package com.github.dreadslicer.tekkitrestrict;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;


public class TRLimitBlock {
	private static List<TRLimitBlock> limiters = Collections.synchronizedList(new LinkedList<TRLimitBlock>());
	private static List<TRLimit> configLimits = Collections.synchronizedList(new LinkedList<TRLimit>());
	private static Map<String, String> allBlockOwners = Collections.synchronizedMap(new HashMap<String, String>());
	private int expire = -1;
	public String player = "";
	public boolean isModified = true; // default limiters will get saved.
	public boolean ignoreAll = false; // used for people who have the limit bypass.
	public List<TRLimit> itemlimits = Collections.synchronizedList(new LinkedList<TRLimit>());

	public void clearLimits() {
		for (TRLimit ll : itemlimits) {
			ll.placedBlock.clear();
		}
		itemlimits.clear();
	}
	
	public int getMax(String p, int thisid, int thisdata){
		int TLimit = -1;

		Player pl = Bukkit.getPlayer(p);
		if(pl != null){
			TRCacheItem ci = TRCacheItem.getPermCacheItem(pl, "l", "limiter", thisid, thisdata);
			//TRCacheItem ci = TRCacheItem.getPermCacheItem(pl, "limiter", thisid, thisdata);
			if (ci != null) {
				TLimit = ci.getIntData();
			}

			
			if (TLimit == -1) {
				TLimit = TRPermHandler.getPermNumeral(pl, "limiter", thisid, thisdata);
			}
		}

		if (TLimit == -1) {
			for (int i = 0; i < configLimits.size(); i++) {
				TRLimit cc = configLimits.get(i);
				if (thisid == cc.blockID && (thisdata == cc.blockData || thisdata == 0)) {
					TLimit = cc.placedBlock.size();
				}
			}
		}
		return TLimit;
	}

	/** @return Whether a player has already maxed out their limits. */
	public boolean checkLimit(BlockPlaceEvent event) {
		// Return decides whether that player has already maxed out their
		// limits.
		boolean r = true;
		// list.addAll(getPermLimits(event.getPlayer()));
		// tekkitrestrict.log.info(list.toString());
		// get list of permission-based limits for this player

		Block block = event.getBlock();
		int thisid = block.getTypeId();
		int thisdata = block.getData();

		int TLimit = getMax(event.getPlayer().getName(),thisid,thisdata);
		
		if (TLimit != -1) {
			// tekkitrestrict.log.info("limited?");
			for (int i = 0; i < itemlimits.size(); i++) {
				TRLimit limit = itemlimits.get(i);

				if (limit.blockID == thisid && limit.blockData == thisdata) {
					int currentnum = limit.placedBlock.size();
					if (currentnum >= TLimit) {
						// this would be at max.
						return false;
					} else {
						// loop through the placedblocks to make sure that we
						// aren't placing the same one down twice.
						boolean place2 = false;
						for (int j = 0; j < limit.placedBlock.size(); j++) {
							if (limit.placedBlock.get(j).equals(block.getLocation())) {
								place2 = true;
							}
						}
						
						if (!place2) {
							limit.placedBlock.add(block.getLocation());
							// save the itemlimit.
							itemlimits.set(i, limit);
							Location bloc = block.getLocation();
							int x = bloc.getBlockX();
							int y = bloc.getBlockY();
							int z = bloc.getBlockZ();
							allBlockOwners.put(bloc.getWorld().getName() + ":" + x + ":" + y + ":" + z, event.getPlayer().getName());
							isModified = true;
							return true;
						}
					}
				}
			}

			// it hasn't quite gone through yet. We need to make a new limiter
			// and add this block to it.
			TRLimit g = new TRLimit();
			g.blockID = thisid;
			g.blockData = thisdata;
			g.placedBlock.add(block.getLocation());
			itemlimits.add(g);
			isModified = true;
			// Making new
			return true;
		}

		return r;
	}

	public void checkBreakLimit(BlockBreakEvent event) {

		// loop through player's limits.
		for (int i = 0; i < itemlimits.size(); i++) {
			TRLimit limit = itemlimits.get(i);

			if (limit.blockID == event.getBlock().getTypeId() && (limit.blockData == event.getBlock().getData() || limit.blockData == 0)) {
				int currentnum = limit.placedBlock.size();
				if (currentnum <= 0) {
					// this would be at minimum
					// (LOG) maxed out
					return;
				} else {
					// add to it!
					limit.placedBlock.remove(event.getBlock().getLocation());
					// save the itemlimit.
					itemlimits.set(i, limit);
					Location bloc = event.getBlock().getLocation();
					int x = bloc.getBlockX();
					int y = bloc.getBlockY();
					int z = bloc.getBlockZ();
					allBlockOwners.remove(bloc.getWorld().getName() + ":" + x + ":" + y + ":" + z);
					isModified = true;
					return;
				}
			}
		}
	}

	// static members.

	/**
	 * See {@link #getLimiter(String)}
	 * @deprecated Use getLimiter(String) instead.
	*/
	public static TRLimitBlock getLimiter(Player player) {
		return getLimiter(player.getName());
	}

	/** @return The limiter the given player has. If it doesn't exist, it creates one. */
	public static TRLimitBlock getLimiter(String playerName) {
		playerName = playerName.toLowerCase();
		// check if a previous itemlimiter exists...
		for (TRLimitBlock il : limiters){
			if (il.player.toLowerCase().equals(playerName)) {
				return il;
			}
		}

		TRLimitBlock r = new TRLimitBlock();
		
		// check to see if this player exists in the database...
		ResultSet dbin = null;
		try {
			dbin = tekkitrestrict.db.query("SELECT * FROM `tr_limiter` WHERE `player` = '" + playerName + "'");
			if (dbin.next()) {
				// This player exists in the database!!!
				// load them up!
				r.player = playerName;

				Player p = Bukkit.getPlayer(playerName);

				// see if they have a bypass... (For this world)

				if (!p.hasPermission("tekkitrestrict.bypass.limiter")) {
					// This player does not have a bypass =(
					// add data
					String blockdata = dbin.getString("blockdata");
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
				}

				for (TRLimit l : r.itemlimits) {
					for (Location l1 : l.placedBlock) {
						allBlockOwners.put(l1.getWorld().getName() + ":" + l1.getBlockX() + ":" + l1.getBlockY() + ":" + l1.getBlockZ(), r.player);
					}
				}

				// add to limiters
				limiters.add(r);
			}
			
			dbin.close();
		} catch (Exception e) {
			try {
				if (dbin != null) dbin.close();
			} catch (SQLException e1) {}
		}

		// it did not exist :O
		// generate a new one!
		r.player = playerName;
		limiters.add(r);

		// return an empty limiter
		return r;
	}

	private static TRLimit loadLimitFromString(String ins) {
		// ins = ins.replace("/", "!");
		String[] limit = ins.split("&");

		TRLimit l = new TRLimit();
		if (limit.length == 2) {
			String t = limit[0];
			String t1 = limit[1];
			// block id parse
			if (t.length() > 0) {
				if (t.contains(":")) {
					// c = org.bukkit.block.
					String[] mat = t.split(":");
					l.blockID = Integer.valueOf(mat[0]);
					l.blockData = Byte.parseByte(mat[1]);
				} else {
					l.blockID = Integer.valueOf(t);
				}

				// DATA parse:
				// world,x,y,z=world,x,y,z=...=...
				if (t1.length() > 0) {
					if (t1.contains("_")) {
						String[] datas = t1.split("_");
						for (int j = 0; j < datas.length; j++) {
							Location looc = locParse(datas[j]);
							if (looc != null) {
								l.placedBlock.add(looc);
							}
						}
					} else {
						Location looc = locParse(t1);
						if (looc != null) {
							l.placedBlock.add(looc);
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
		synchronized (limiters) {
			for (TRLimitBlock lb : limiters){
				saveLimiter(lb);
			}
		}
		for (int i = 0; i < limiters.size(); i++) {
			TRLimitBlock il = limiters.get(i);
			saveLimiter(il);
		}
	}

	public static void setExpire(String player) {
		// gets the player from the list of limiters (does nothing if player doesn't exist)
		for (int i = 0; i < limiters.size(); i++) {
			TRLimitBlock il = limiters.get(i);
			if (il.player.equals(player)) {
				il.expire = 6; // Every 32 ticks 32*6 = Every 192 ticks
				return;
			}
		}
	}

	/** Dynamically unload a player (after they logout) */
	private static void deLoadLimiter(TRLimitBlock limitBlock) {
		saveLimiter(limitBlock);
		// clear limits
		limitBlock.clearLimits();
		// remove this limiter from the list.
		limiters.remove(limitBlock);
	}

	private static void saveLimiter(TRLimitBlock il) {
		String player = il.player.toLowerCase();
		String blockdata = "";
		try {
			for (int j = 0; j < il.itemlimits.size(); j++) {
				String suf = "";

				if (j < il.itemlimits.size() - 1) {
					suf = "%";
				}
				// loop through each Limit
				TRLimit li1 = il.itemlimits.get(j);
				String block = "" + li1.blockID;//id or id:data
				String DATA = "";
				// tekkitrestrict.log.info(block+":"+li1.blockData+" "+li1.placedBlock.size());
				// set data if it exists
				if (li1.blockID != -1) {
					if (li1.blockData != 0) {
						block += ":" + li1.blockData;
					}
					// Get DATA.
					// loop through each block data
					for (int k = 0; k < li1.placedBlock.size(); k++) {
						String suf1 = "";
						if (k < li1.placedBlock.size() - 1) {
							suf1 = "_";
						}
						Location l = li1.placedBlock.get(k);
						DATA = DATA + l.getWorld().getName() + ","
								+ l.getBlockX() + "," + l.getBlockY() + ","
								+ l.getBlockZ() + suf1;
					}
					String datarr = "";
					if (li1.placedBlock.size() > 0) {
						datarr = block + "&" + DATA + suf;
					}
					blockdata = blockdata + datarr;
					// tekkitrestrict.log.info(blockdata);
				}
			}
		} catch (Exception E) {
		}
		/*
		int estID = 0;
		// get a numeral from the player's name.
		for (int i = 0; i < player.length(); i++) {
			char c = player.charAt(i); //TODO FIXME IMPORTANT This can be the same for multiple names. (abba = 1+2+2+1=6, baab = 2+1+1+2 = 6)
			estID += Character.getNumericValue(c);
		}
		for (int i = 0; i < player.length(); i++) {
			char c = player.charAt(i); //TODO FIXME IMPORTANT This can be the same for multiple names. (abba = 1+2+2+1=6, baab = 2+1+1+2 = 6)
			estID += Character.getNumericValue(c)*i;//abba = 1*1+2*2+2*3+1*4 = 1+4+6+4 = 15, baab = 2*1+1*2+1*3+2*4 = 2+2+3+8 = 15)
		}
		
		String estID2 = "";
		for (int i = 0; i < player.length(); i++) {
			char c = player.charAt(i); //TODO FIXME IMPORTANT This can be the same for multiple names. (abba = 1+2+2+1=6, baab = 2+1+1+2 = 6)
			int nr = Character.getNumericValue(c);
			if (nr < 0) estID2 += 00;
			else if (nr < 10) estID2 += "0" + nr;
			estID2 += Character.getNumericValue(c);//abba = 1221, baab = 2112, z = 
		}*/

		try {
			if (!blockdata.equals("")) {
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
						+ "')");
			}
		} catch (Exception E) {
		}
	}

	private static Location locParse(String ins) {
		Location l = null;

		if (ins.contains(",")) {
			// determine if the world for this exists...
			String[] lac = ins.split(",");
			World cw = tekkitrestrict.getInstance().getServer().getWorld(lac[0]);
			if (cw != null) {
				l = new Location(cw, Integer.valueOf(lac[1]),
						Integer.valueOf(lac[2]), Integer.valueOf(lac[3]));
			}
		}

		return l;
	}

	/** load all of the block:player pairs from db. */
	public static void init() {
		ResultSet dbin = null;
		try {
			dbin = tekkitrestrict.db.query("SELECT * FROM `tr_limiter` LIMIT 0,9999999");
			if (dbin.next()) {
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
		} catch (Exception e) {
			try {
				if (dbin != null) dbin.close();
			} catch (SQLException e1) {}
			TRLogger.Log("debug", "Error! [TRLimitBlockLoad] " + e.getMessage());
		}
	}

	private static Location ladslfl;
	public static void manageData() {
		// manages and removes bad data.
		// determines if the limit exists at a location. If not, remove it.
		for (TRLimitBlock lb : limiters) {
			boolean changed = false;
			for (TRLimit l : lb.itemlimits) {
				for (int i = 0; i < l.placedBlock.size(); i++) {
					try {
						Location loc = l.placedBlock.get(i);
						ladslfl = loc;
						Future<Chunk> returnFuture = tekkitrestrict.getInstance().getServer().getScheduler().callSyncMethod(tekkitrestrict.getInstance(), new Callable<Chunk>() {
						   public Chunk call() {
							   Chunk c = ladslfl.getChunk();
							   c.load();
						       return c;
						   }
						});

					    // This will block the current thread 
						Chunk returnValue = returnFuture.get();
						if(returnValue != null){
							Block b = loc.getWorld().getBlockAt(loc.getBlockX(),
									loc.getBlockY(), loc.getBlockZ());
							if (b.getTypeId() != l.blockID
									&& !(l.blockData == 0 || l.blockData == b.getData())) {
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

	public static void reload() {
		List<String> limitedBlocks = tekkitrestrict.config.getStringList("LimitBlocks");
		configLimits.clear();
		for (String limBlock : limitedBlocks) {
			try {
				String[] temp = limBlock.split(" ");
				if (temp.length!=2){
					tekkitrestrict.log.warning("[Config] You have an error in your Advanced.config.yml in LimitBlocks!");
					tekkitrestrict.log.warning("[Config] \""+limBlock+"\" does not follow the syntaxis \"itemIndex limit\"!");
					continue;
				}
				int limit = 0;
				try {
					limit = Integer.parseInt(temp[1]);
				} catch (NumberFormatException ex){
					tekkitrestrict.log.warning("[Config] You have an error in your Advanced.config.yml in LimitBlocks!");
					tekkitrestrict.log.warning("[Config] \""+temp[1]+"\" is not a valid number!");
					continue;
				}
				
				TRCacheItem.processItemString("l", "afsd90ujpj", temp[0], limit);
				//TRCacheItem.processItemString("limiter", "afsd90ujpj", temp[0], limit);
				/*
				 * ItemStack[] ar = TRNoItem.getRangedItemValues(g[0]); int
				 * limit = Integer.valueOf(g[1]); for (ItemStack iss : ar) {
				 * TRLimit ccr = new TRLimit(); for (int i = 0; i < limit; i++)
				 * { ccr.placedBlock.add(null); } ccr.blockID = iss.id;
				 * ccr.blockData = iss.getData(); configLimits.add(ccr); }
				 */
			} catch (Exception e) {
				tekkitrestrict.log.warning("[Config] LimitBlocks: has an error!");
			}
		}
	}

	public static void expireLimiters() {
		// loop through each limiter.
		for (TRLimitBlock il : limiters){
			if (il.expire == -1) continue;
			if (il.expire == 0) { // do expire
				saveLimiter(il);
				deLoadLimiter(il);
				// tekkitrestrict.log.info("Expired limiter");
			} else {
				// tekkitrestrict.log.info("Age limiter");
				il.expire--;
			}
		}
	}

	public static void removeExpire(String playerName) {
		playerName = playerName.toLowerCase();
		for (TRLimitBlock il : limiters){
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
		String pl = allBlockOwners.get(bloc.getWorld().getName() + ":" + x
				+ ":" + y + ":" + z);
		if (pl != null) return pl;
		return null;
	}
}
