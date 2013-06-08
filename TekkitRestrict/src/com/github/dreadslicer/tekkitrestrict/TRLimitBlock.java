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

import com.github.dreadslicer.tekkitrestrict.lib.TRLimit;

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
		// tekkitrestrict.log.info(thisid+":"+thisdata+" isLimiter?");
		Player pl = Bukkit.getPlayer(p);
		if(pl != null){
			TRCacheItem ci = TRCacheItem.getPermCacheItem(pl, "limiter", thisid, thisdata);
			if (ci != null) {
				//tekkitrestrict.log.info(ci.id + ":" + ci.getData() + " "
				//		+ ci.getIntData());
				TLimit = ci.getIntData();
			}
			
			if (TLimit == -1) {
				TLimit = TRPermHandler.getPermNumeral(pl, "limiter",
						thisid, thisdata);
			}
		}
		 //tekkitrestrict.log.info("TLimit - "+TLimit);
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

		int thisid = event.getBlock().getTypeId();
		int thisdata = event.getBlock().getData();
		// tekkitrestrict.log.info("limit - "+thisid+":"+thisdata);
		// if(TRPermHandler.hasPermission(event.getPlayer(),
		// "limit",thisid+"",(thisdata!=0 ? ""+thisdata : ""))){

		int TLimit = getMax(event.getPlayer().getName(),thisid,thisdata);
		
		if (TLimit != -1) {
			// tekkitrestrict.log.info("limited?");
			for (int i = 0; i < itemlimits.size(); i++) {
				TRLimit limit = itemlimits.get(i);

				// tekkitrestrict.log.info(limit.toString());
				if (limit.blockID == thisid && limit.blockData == thisdata) {
					int currentnum = limit.placedBlock.size();
					if (currentnum >= TLimit) {
						// this would be at max.
						// tekkitrestrict.log.info("maxed out.");
						return false;
					} else {
						// loop through the placedblocks to make sure that we
						// aren't placing the same one down twice.
						boolean place2 = false;
						for (int j = 0; j < limit.placedBlock.size(); j++) {
							if (limit.placedBlock.get(j).equals(
									event.getBlock().getLocation())) {
								place2 = true;
							}
						}
						if (!place2) {
							limit.placedBlock.add(event.getBlock()
									.getLocation());
							// save the itemlimit.
							itemlimits.set(i, limit);
							Location bloc = event.getBlock().getLocation();
							int x = bloc.getBlockX();
							int y = bloc.getBlockY();
							int z = bloc.getBlockZ();
							allBlockOwners.put(bloc.getWorld().getName() + ":"
									+ x + ":" + y + ":" + z, event.getPlayer()
									.getName());
							isModified = true;
							return true;
						}
					}
				}
			}

			// it hasn't quite gone through yet. We need to make a new limiter
			// and add this block to it.
			TRLimit g = new TRLimit();
			g.blockID = event.getBlock().getTypeId();
			g.blockData = event.getBlock().getData();
			g.placedBlock.add(event.getBlock().getLocation());
			itemlimits.add(g);
			isModified = true;
			// tekkitrestrict.log.info("Making new");
			return true;
		}

		return r;
	}

	public void checkBreakLimit(BlockBreakEvent event) {
		// event.getPlayer();

		// loop through player's limits.
		for (int i = 0; i < itemlimits.size(); i++) {
			TRLimit limit = itemlimits.get(i);

			// tekkitrestrict.log.info(limit.toString());
			if (limit.blockID == event.getBlock().getTypeId()
					&& (limit.blockData == event.getBlock().getData() || limit.blockData == 0)) {
				int currentnum = limit.placedBlock.size();
				if (currentnum <= 0) {
					// this would be at minimum
					// tekkitrestrict.log.info("maxed out.");
					return;
				} else {
					// add to it!
					// tekkitrestrict.log.info("Adding to");
					limit.placedBlock.remove(event.getBlock().getLocation());
					// save the itemlimit.
					itemlimits.set(i, limit);
					Location bloc = event.getBlock().getLocation();
					int x = bloc.getBlockX();
					int y = bloc.getBlockY();
					int z = bloc.getBlockZ();
					allBlockOwners.remove(bloc.getWorld().getName() + ":" + x
							+ ":" + y + ":" + z);
					isModified = true;
					return;
				}
			}
		}
	}

	// static members.

	/** See {@link #getLimiter(String)} */
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
			dbin = tekkitrestrict.db.query("SELECT * FROM `tr_limiter` WHERE `player` = '"
							+ playerName.toLowerCase() + "'");
			if (dbin.next()) {
				// This player exists in the database!!!
				// load them up!
				r.player = playerName;

				Player p = Bukkit.getPlayer(playerName);

				// see if they have a bypass... (For this world)

				if (!Util.hasBypass(p, "limit")) {
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
						allBlockOwners.put(
								l1.getWorld().getName() + ":" + l1.getBlockX()
										+ ":" + l1.getBlockY() + ":"
										+ l1.getBlockZ(), r.player);
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
		r.player = playerName.toLowerCase();
		limiters.add(r);

		// return an empty limiter
		return r;
	}

	private static TRLimit loadLimitFromString(String ins) {
		// ins = ins.replace("/", "!");
		// tekkitrestrict.log.info(ins);
		String[] limit = ins.split("&");

		TRLimit l = new TRLimit();
		if (limit.length == 2) {
			String t = limit[0];
			String t1 = limit[1];
			// block id parse
			// tekkitrestrict.log.info(limit.toString());
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

	public static void saveLimiters() {
		// save the limiters to the database.
		// looping through each player's limiters
		for (int i = 0; i < limiters.size(); i++) {
			TRLimitBlock il = limiters.get(i);
			saveLimiter(il);
		}
	}

	public static void setExpire(String player) {
		// gets the player from the list of limiters (does nothing if player
		// doesn't exist)
		for (int i = 0; i < limiters.size(); i++) {
			TRLimitBlock il = limiters.get(i);
			if (il.player.equals(player)) {
				il.expire = 6; // Every 32 ticks 32*6 = Every 192 ticks
				return;
			}
		}
	}

	private static void deLoadLimiter(TRLimitBlock p) {
		// dynamically unload a player (after they logout)
		saveLimiter(p);
		// clear limits
		p.clearLimits();
		// remove this limiter from the list.
		limiters.remove(p);
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
				String block = li1.blockID + "";
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
		int estID = 0;
		// get a numeral from the player's name.
		for (int i = 0; i < player.length(); i++) {
			char c = player.charAt(i);
			estID += Character.getNumericValue(c);
		}

		try {
			if (blockdata != "") {
				tekkitrestrict.db
						.query("INSERT OR REPLACE INTO `tr_limiter` (`id`,`player`,`blockdata`) VALUES ("
								+ estID
								+ ",'"
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
				// tekkitrestrict.log.info("CCC"+l);
			}
		}

		return l;
	}

	public static void init() {
		// load all of the block:player pairs from db.
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
												+ loc.getBlockZ(), player.toLowerCase());
							}
						}
					} else {
						String g = blockdata;
						TRLimit L = loadLimitFromString(g);
						List<Location> blks = L.placedBlock;
						for (Location loc : blks) {
							allBlockOwners.put(loc.getWorld().getName() + ":"
									+ loc.getBlockX() + ":" + loc.getBlockY()
									+ ":" + loc.getBlockZ(), player.toLowerCase());
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
		List<String> ccl = tekkitrestrict.config.getStringList("LimitBlocks");
		configLimits.clear();
		for (String ir : ccl) {
			try {
				String[] g = ir.split(" ");
				TRCacheItem.processItemString("limiter", "afsd90ujpj", g[0],
						Integer.valueOf(g[1]));
				/*
				 * ItemStack[] ar = TRNoItem.getRangedItemValues(g[0]); int
				 * limit = Integer.valueOf(g[1]); for (ItemStack iss : ar) {
				 * TRLimit ccr = new TRLimit(); for (int i = 0; i < limit; i++)
				 * { ccr.placedBlock.add(null); } ccr.blockID = iss.id;
				 * ccr.blockData = iss.getData(); configLimits.add(ccr); }
				 */
			} catch (Exception e) {
				tekkitrestrict.log.info("[config] LimitBlocks: has an error!");
			}
		}
	}

	public static void expireLimiters() {
		// loop through each limiter.
		for (TRLimitBlock il : limiters){
			if (il.expire != -1) {
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
		if (pl != null) {
			return pl;
		}
		return null;
	}
}
