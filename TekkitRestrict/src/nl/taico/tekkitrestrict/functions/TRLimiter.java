package nl.taico.tekkitrestrict.functions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import lombok.NonNull;
import nl.taico.taeirlib.concurrent.ConcurrentList;
import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor2;
import nl.taico.tekkitrestrict.TRListener;
import nl.taico.tekkitrestrict.TRPermHandler;
import nl.taico.tekkitrestrict.TekkitRestrict;
import nl.taico.tekkitrestrict.annotations.Async;
import nl.taico.tekkitrestrict.config.SettingsStorage;
import nl.taico.tekkitrestrict.objects.TRConfigLimit;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TRLimit;
import nl.taico.tekkitrestrict.objects.TRLocation;
import nl.taico.tekkitrestrict.objects.TRPermLimit;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class TRLimiter {
	protected int expire;
	public String player;
	public boolean isModified;
	/** A list of different kinds of limited blocks and the locations where they are placed (For this player). */
	public List<TRLimit> itemlimits;

	protected static ConcurrentList<TRLimiter> limiters = new ConcurrentList<TRLimiter>(new ArrayList<TRLimiter>());
	//private static List<TRLimiter> limiters = Collections.synchronizedList(new LinkedList<TRLimiter>());
	protected static ArrayList<TRConfigLimit> configLimits = new ArrayList<>();
	protected static Map<String, String> allBlockOwners = Collections.synchronizedMap(new HashMap<String, String>());

	//	private static ConcurrentHashMap<String, List<TRPermLimit>> limiterPermCache = new ConcurrentHashMap<>(); (No Concurrent access --> No Concurrent map)
	protected static HashMap<String, List<TRPermLimit>> limiterPermCache = new HashMap<>();

	protected static final TRFakeLimiter turtle = new TRFakeLimiter("[ComputerCraft]"),
			buildcraft = new TRFakeLimiter("[BuildCraft]"),
			redpower = new TRFakeLimiter("[RedPower]");
	private static boolean logged = false, logged2 = false;

	/**
	 * Dynamically unload a player (after they logout).<br>
	 * Saves the limiter first, then uses <code>limitBlock.clearLimits()</code>.
	 * @see #saveLimiter(TRLimiter)
	 */
	private static void deLoadLimiter(final TRLimiter limitBlock) {
		saveLimiter(limitBlock);
		// clear limits
		limitBlock.clearLimits();
	}

	/**
	 * Called by the heartbeat to unload limits of players that are offline.
	 * @see #deLoadLimiter(TRLimiter) Uses deLoadLimiter(TRLimitBlock)
	 * @see TekkitRestrict#initHeartBeat() Called by tekkitrestrict.initHeartBeat()
	 */
	@Async
	public static void expireLimiters() {
		// loop through each limiter.
		final HashSet<TRLimiter> tbr = new HashSet<TRLimiter>();

		try {
			Iterator<TRLimiter> it = limiters.iteratorLOCKED(true);
			while (it.hasNext()){
				TRLimiter il = it.next();
				if (il instanceof TRFakeLimiter) continue;
				if (il.expire == -1) continue;
				if (il.expire == 0) { // do expire
					tbr.add(il);
					it.remove();
					//Expired limiter
				} else {
					// Age limiter
					il.expire--;
				}
			}
		} finally {
			limiters.unlockIterator(true);
		}

		//No lock on limiters
		for (final TRLimiter lb : tbr){
			deLoadLimiter(lb);
		}
	}

	@NonNull public static ArrayList<String> getDebugInfo(){
		final ArrayList<String> tbr = new ArrayList<String>();
		for (final TRConfigLimit limit : configLimits){
			tbr.add("L:" + limit.id+":"+limit.data+"_"+limit.configcount);
		}

		return tbr;
	}

	/**
	 * First checks all loaded limiters.<br>
	 * If it cannot find this player's limiter, it will look into the database.<br>
	 * If that also results in nothing, it will create a new limiter.
	 * @return The limiter the given player has. If it doesn't exist, it creates one.
	 */
	@NonNull public static TRLimiter getLimiter(@NonNull String playerName) {
		playerName = playerName.toLowerCase();
		if (playerName.startsWith("[computercraft]")) return turtle;
		if (playerName.equals("[buildcraft]")) return buildcraft;
		if (playerName.equals("[redpower]")) return redpower;
		// check if a previous itemlimiter exists...

		try {
			Iterator<TRLimiter> it = limiters.iteratorLOCKED(false);
			while (it.hasNext()){
				TRLimiter il = it.next();
				if (il.player.equalsIgnoreCase(playerName)) return il;
			}
		} finally {
			limiters.unlockIterator(false);
		}
		//Player is not loaded or offline.

		final TRLimiter r = new TRLimiter();
		r.player = playerName;

		limiters.add(r);

		//If player is online, check for bypass.
		final Player p = Bukkit.getPlayer(playerName);
		if ((p != null) && p.hasPermission("tekkitrestrict.bypass.limiter")) return r; //return an empty limiter

		//If player is offline or isn't loaded, check the database.
		ResultSet dbin = null;
		try {
			dbin = TekkitRestrict.db.query("SELECT * FROM `tr_limiter` WHERE `player` = '" + playerName + "';");
			if (dbin == null){
				Warning.other("Unknown error occured when trying to get limits from database!", false);
				return r;
			}
			if (dbin.next()) {
				//This player exists in the database, so now we have to load him/her up.

				// add data
				String blockdata = dbin.getString("blockdata");
				dbin.close(); //We dont need dbin any more from here so we can close it.
				// scheme:
				// id:data/DATA|id:data/DATA
				if (blockdata.length() >= 3) {
					if (blockdata.contains("%")) {
						final String[] prelimits = blockdata.split("%");
						for (int i = 0; i < prelimits.length; i++) {
							r.itemlimits.add(loadLimitFromString(prelimits[i]));
						}
					} else {
						r.itemlimits.add(loadLimitFromString(blockdata));
					}
				}

				for (final TRLimit l : r.itemlimits) {
					for (final TRLocation l1 : l.placedBlock) {
						allBlockOwners.put(l1.world + ":" + l1.x + ":" + l1.y + ":" + l1.z, r.player);
					}
				}

				return r;
			}

			dbin.close();
		} catch (final Exception e) {
			try {
				if (dbin != null) dbin.close();
			} catch (final SQLException e1) {}
		}

		return r; //Return an empty limiter
	}

	/** @return The limiter the given player has. If it doesn't exist, it creates one. */
	@NonNull public static TRLimiter getOnlineLimiter(@NonNull final Player player) {
		final String playerName = player.getName().toLowerCase();
		// check if a previous itemlimiter exists...

		try {
			Iterator<TRLimiter> it = limiters.iteratorLOCKED(false);
			while (it.hasNext()){
				TRLimiter il = it.next();
				if (il.player.equalsIgnoreCase(playerName)) {
					return il;
				}
			}
		} finally {
			limiters.unlockIterator(false);
		}

		final TRLimiter r = new TRLimiter();
		r.player = playerName;

		limiters.add(r);

		if (player.hasPermission("tekkitrestrict.bypass.limiter")) return r; //return an empty limiter

		// check to see if this player exists in the database...
		ResultSet dbin = null;
		try {
			dbin = TekkitRestrict.db.query("SELECT * FROM `tr_limiter` WHERE `player` = '" + playerName + "';");
			if (dbin == null){
				Warning.other("Unknown error occured when trying to get limits from database!", false);
				return r;
			}
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
						final String[] prelimits = blockdata.split("%");
						for (int i = 0; i < prelimits.length; i++) {
							r.itemlimits.add(loadLimitFromString(prelimits[i]));
						}
					} else {
						r.itemlimits.add(loadLimitFromString(blockdata));
					}
				}

				for (final TRLimit l : r.itemlimits) {
					for (final TRLocation l1 : l.placedBlock) {
						allBlockOwners.put(l1.world + ":" + l1.x + ":" + l1.y + ":" + l1.z, r.player);
					}
				}

				return r;
			}

			dbin.close();
		} catch (final Exception e) {
			try {
				if (dbin != null) dbin.close();
			} catch (final SQLException e1) {}
		}

		return r; //Return an empty limiter
	}

	/** @return The owner of this block, or null if none is found. */
	@Nullable public static String getPlayerAt(@NonNull final Block block) {
		// Cache block:owners, so this goes really fast.
		final Location bloc = block.getLocation();
		return allBlockOwners.get(bloc.getWorld().getName() + ":" + bloc.getBlockX() + ":" + bloc.getBlockY() + ":" + bloc.getBlockZ());
	}

	/** @return The owner of this block, or null if none is found. */
	@Nullable public static String getPlayerAt(@NonNull final Location bloc) {
		// Cache block:owners, so this goes really fast.
		return allBlockOwners.get(bloc.getWorld().getName() + ":" + bloc.getBlockX() + ":" + bloc.getBlockY() + ":" + bloc.getBlockZ());
	}

	/** load all of the block:player pairs from db. */
	public static void init() {
		ResultSet dbin = null;
		Log.trace("Limiter - Loading Limits From Database...");
		try {
			dbin = TekkitRestrict.db.query("SELECT * FROM `tr_limiter`;");
			if (dbin == null){
				Warning.other("Unable to load the limits from the database!", true);
				return;
			}

			int m = 0;
			while (dbin.next()) {
				// This player exists in the database!!!
				// load them up!

				// This player does not have a bypass =(
				// add data
				final String player = dbin.getString("player").toLowerCase();
				final String blockdata = dbin.getString("blockdata");
				// scheme:
				// id:data/DATA|id:data/DATA
				if (blockdata.length() >= 3) {
					if (blockdata.contains("%")) {
						final String[] prelimits = blockdata.split("%");
						for (int i = 0; i < prelimits.length; i++) {
							final String g = prelimits[i];
							final TRLimit l = loadLimitFromString(g);
							final List<TRLocation> blks = l.placedBlock;
							for (final TRLocation loc : blks) {
								allBlockOwners.put(
										loc.world + ":"
												+ loc.x + ":"
												+ loc.y + ":"
												+ loc.z, player);
							}
						}
					} else {
						final String g = blockdata;
						final TRLimit l = loadLimitFromString(g);
						final List<TRLocation> blks = l.placedBlock;
						for (final TRLocation loc : blks) {
							allBlockOwners.put(loc.world + ":"
									+ loc.x + ":" + loc.y
									+ ":" + loc.z, player);
						}
					}
				}
				m++;
			}
			dbin.close();
			Log.trace("Limiter - Loaded Limits for "+m+" players");
		} catch (final Exception ex) {
			try {
				if (dbin != null) dbin.close();
			} catch (final SQLException ex2) {}
			Warning.otherWarnings.add("[SEVERE] An error occurred while loading the limiter!");
			Log.severe("An error occurred while loading the limiter!");
			Log.Exception(ex, true);
		}
	}

	/** Note: You should add the locations in this limit to allBlockOwners after calling this method. */
	@NonNull private static TRLimit loadLimitFromString(@NonNull final String ins) {
		// ins = ins.replace("/", "!");
		final String[] limit = ins.split("&");

		final TRLimit l = new TRLimit();
		if (limit.length == 2) {
			final String item = limit[0];
			final String locStr = limit[1];
			// block id parse
			if (item.length() > 0) {
				if (item.contains(":")) {
					// c = org.bukkit.block.
					final String[] mat = item.split(":");
					try {
						l.id = Integer.parseInt(mat[0]);
						l.data = Byte.parseByte(mat[1]);
					} catch (NumberFormatException ex){
						Warning.config("Invalid limiter value in database: \""+item+"\"!", false);
						l.id = 0;
						l.data = 0;
					}
				} else {
					try {
						l.id = Integer.parseInt(item);
						l.data = 0;
					} catch (NumberFormatException ex){
						Warning.config("Invalid limiter value in database: \""+item+"\"!", false);
						l.id = 0;
						l.data = 0;
					}
				}

				// DATA parse:
				// world,x,y,z=world,x,y,z=...=...
				if (locStr.length() > 0) {
					if (locStr.contains("_")) {
						final String[] datas = locStr.split("_");
						for (int j = 0; j < datas.length; j++) {
							final TRLocation loc = locParse(datas[j]);
							if (loc != null) l.placedBlock.add(loc);
						}
					} else {
						final TRLocation loc = locParse(locStr);
						if (loc != null) l.placedBlock.add(loc);
					}
				}
			}
		}
		return l;
	}

	/** Parses a String formatted like <code>"world,x,y,z"</code> to a location. */
	@Nullable private static TRLocation locParse(final String ins) {
		try {
			if (ins.contains(",")) {
				// determine if the world for this exists...
				final String[] lac = ins.split(",");
				final World cw = Bukkit.getWorld(lac[0]);
				if (cw != null) {
					return new TRLocation(cw.getName(), Integer.parseInt(lac[1]), Integer.parseInt(lac[2]), Integer.parseInt(lac[3]));
				}
			}
		} catch (final Exception ex){
			Warning.other("Error while loading a limiter: malformed limiter location in the database!", false);
		}

		return null;
	}

	//	/** Used by {@link #manageData()} for the Future call. */
	//	private static Location tempLoc;
	/**
	 * Manages and removes bad data.
	 * Determines if the limit exists at a location. If not, remove it.
	 * Called by limiter manager
	 */
	public static void manageData() {
		try {
			Iterator<TRLimiter> it = limiters.iteratorLOCKED(false);
			while (it.hasNext()){
				final TRLimiter lb = it.next();
				boolean changed = false;
				for (final TRLimit l : lb.itemlimits) {
					try {
						final Iterator<TRLocation> it2 = l.placedBlock.iterator();
						while (it2.hasNext()){
							final TRLocation loc = it2.next();
							final Chunk chunk = loc.getChunk();

							if(chunk.isLoaded()){
								final Block b = loc.getBlock();

								if (b.getTypeId() != l.id){
									it2.remove();
									changed = true;
								} else if ((l.data != b.getData()) && (l.data != -1)) {//TODO IMPORTANT FIXME change THIS!
									//} else if (l.data != b.getData() && !(b.getData() == 0 && l.data == -10) && l.data != -1) {//TODO IMPORTANT FIXME change THIS!
									it2.remove();
									changed = true;
								}
							}
						}
					} catch (ConcurrentModificationException ex){
						continue;
					}
				}

				if (changed){
					Bukkit.getScheduler().scheduleAsyncDelayedTask(TekkitRestrict.getInstance(), new Runnable(){
						@Override
						public void run(){
							saveLimiter(lb);
						}
					});
				}
			}
		} finally {
			limiters.unlockIterator(false);
		}
	}

	public static void reload() {
		//for (String str : limiterPermCache.keySet()){
		//	for (TRPermLimit t : limiterPermCache.get(str)){
		//		tekkitrestrict.log.info("[DEBUG] limiterPermCache.get("+str+"): " + t.id + ":" + t.data + " max="+t.max);
		//	}
		//}

		limiterPermCache = new HashMap<>();

		final ArrayList<TRConfigLimit> temp = new ArrayList<TRConfigLimit>();
		final List<String> limitedBlocks = SettingsStorage.limiterConfig.getStringList("LimitBlocks");
		Log.trace("Loading Limits...");
		for (String limBlock : limitedBlocks) {
			String msg = null;
			if (limBlock.contains("{")){
				final String tempe[] = limBlock.split("\\{");
				limBlock = tempe[0].trim();
				msg = Log.replaceColors(tempe[1].replace("}", ""));
			}
			try {
				final String[] tempe = limBlock.split(" ");
				if (tempe.length!=2){
					Warning.config("You have an error in your Advanced.config.yml in LimitBlocks:", false);
					Warning.config("\""+limBlock+"\" does not follow the syntaxis \"itemIndex limit\"!", false);
					continue;
				}
				final int limit;
				try {
					limit = Integer.parseInt(tempe[1]);
				} catch (NumberFormatException ex){
					Warning.config("You have an error in your Advanced.config.yml in LimitBlocks:", false);
					Warning.config("\""+tempe[1]+"\" is not a valid number!", false);
					continue;
				}
				final List<TRItem> items;
				try {
					items = TRItemProcessor2.processString(tempe[0]);
				} catch (TRException ex) {
					Warning.config("You have an error in your Advanced.config.yml in LimitBlocks:", false);
					Warning.config(ex.getMessage(), false);
					continue;
				}

				for (final TRItem ci : items){
					final TRConfigLimit cLimit = new TRConfigLimit();
					cLimit.id = ci.id;
					cLimit.data = ci.data;
					cLimit.msg = (msg == null ? "" : msg);
					cLimit.configcount = limit;
					temp.add(cLimit);
				}
			} catch (Exception ex) {
				Warning.config("LimitBlocks: has an error!", false);
			}
		}
		configLimits = temp;
	}

	/** Called when a player logs in to make his limits not expire any more. */
	public static void removeExpire(String playerName) {
		playerName = playerName.toLowerCase();

		try {
			Iterator<TRLimiter> it = limiters.iteratorLOCKED(false);
			while (it.hasNext()){
				TRLimiter il = it.next();
				if (il.player.equalsIgnoreCase(playerName))
					il.expire = -1;
			}
		} finally {
			limiters.unlockIterator(false);
		}
	}

	/** Saves 1 limiter to the database. */
	private static void saveLimiter(@NonNull final TRLimiter lb) {
		if (lb.player == null){
			Warning.other("An error occurred while saving the limits! Error: Null player name!", false);
			return;
		}
		final String player = lb.player.toLowerCase();
		String blockdata = null;

		try {
			final int size = lb.itemlimits.size();
			int i = 0;
			final Iterator<TRLimit> limitsIt = lb.itemlimits.iterator();
			while (limitsIt.hasNext()){
				final TRLimit limit1 = limitsIt.next();
				if (limit1.id == -1) continue;

				final String suf;
				if (i == (size - 1)) suf = "";
				else suf = "%";

				final String block;// = "" + limit1.id; //id or id:data

				// set data if it exists
				if (limit1.data == 0)
					block = "" + limit1.id;
				else
					block = "" + limit1.id + ":" + limit1.data;


				// Get DATA.
				String DATA = "";
				// loop through each block data
				final int size2 = limit1.placedBlock.size();
				int j = 0;

				final Iterator<TRLocation> pblockIt = limit1.placedBlock.iterator();
				while (pblockIt.hasNext()){
					final String suf1;
					if (j == (size2 - 1)) suf1 = "";
					else suf1 = "_";
					final TRLocation l = pblockIt.next();
					DATA += l.world + "," + l.x + "," + l.y + "," + l.z + suf1;
					j++;
				}

				if (blockdata == null) blockdata = "";
				if (size2 > 0) blockdata += block + "&" + DATA + suf;

				i++;
			}
			if (blockdata == null) blockdata = "";
		} catch (final Exception ex) {
			if (!logged){
				Warning.other("An error occurred while saving the limits! Error: Cannot create string to save to database!", false);
				logged = true;
			}
		}

		if (blockdata == null) return;
		try {
			TekkitRestrict.db.query("INSERT OR REPLACE INTO `tr_limiter` (`player`,`blockdata`) VALUES ('"
					+ player
					+ "','"
					+ blockdata
					+ "');");

		} catch (final SQLException ex) {
			if (!logged2){
				Warning.other("An error occurred while saving the limits! Error: Cannot insert into database!", false);
				logged2 = true;
			}
		}
	}

	/** Save the limiters to the database. */
	public static void saveLimiters() {
		// looping through each player's limiters
		/*
		int i = 0;
		while (changing2.get()){
			try {
				Thread.sleep(1);
				i++;
			} catch (Exception ex){}
			if (i == 10000) tekkitrestrict.instance.getLogger().warning("Saving limiter has waited over 10 seconds for window! Aborting");
		}*/

		try {
			Iterator<TRLimiter> it = limiters.iteratorLOCKED(false);
			while (it.hasNext()){
				saveLimiter(it.next());
			}
		} finally {
			limiters.unlockIterator(false);
		}
	}

	/**
	 * Called by QuitListener.quit(Player) to make a players limits expire (after 6x32 = 192 ticks) when he logs off.
	 * @see nl.taico.tekkitrestrict.listeners.QuitListener#quit(Player) QuitListener.quit(Player)
	 * @see TekkitRestrict#initHeartBeat()
	 */
	public static void setExpire(@NonNull String player) {
		player = player.toLowerCase();
		// gets the player from the list of limiters (does nothing if player doesn't exist)

		try {
			Iterator<TRLimiter> it = limiters.iteratorLOCKED(false);
			while (it.hasNext()){
				TRLimiter il = it.next();
				if (!il.player.equals(player)) continue;

				il.expire = 6; // Every 32 ticks 32*6 = Every 192 ticks
				return;
			}
		} finally {
			limiters.unlockIterator(false);
		}
	}

	protected String lastString = "";

	public TRLimiter(){
		expire = -1;
		player = "";
		isModified = true; // default limiters will get saved.
		itemlimits = Collections.synchronizedList(new LinkedList<TRLimit>());
	}

	protected TRLimiter(String fakeplayer){
		this.expire = -1;
		this.player = fakeplayer;
		this.isModified = false;
		this.itemlimits = new ArrayList<TRLimit>(0);
		limiters.add(this);
	}

	/**
	 * Updates the breakLimits of this player.
	 * @param event
	 * @throws IllegalArgumentException If event == {@code null}
	 * @see #checkBreakLimit(int, byte, Location)
	 */
	public void checkBreakLimit(BlockBreakEvent event) {
		if (event == null) throw new IllegalArgumentException("Event cannot be null!");
		checkBreakLimit(event.getBlock().getTypeId(), event.getBlock().getData(), event.getBlock().getLocation());
	}

	/**
	 * @param id
	 * @param data
	 * @param bloc
	 * @throws IllegalArgumentException If bloc == {@code null}
	 */
	public void checkBreakLimit(int id, byte data, Location bloc) {
		if (bloc == null) throw new IllegalArgumentException("Location cannot be null!");
		//READ itemlimits
		// loop through player's limits.
		for (final TRLimit limit : itemlimits) {
			if ((limit.id != id) || (limit.data != data)) continue;

			final int currentnum = limit.placedBlock.size();
			if (currentnum <= 0) {
				// this would be at minimum
				// (LOG) maxed out
				return;
			} else {
				final TRLocation loc = new TRLocation(bloc);
				limit.placedBlock.remove(loc);

				allBlockOwners.remove(loc.world + ":" + loc.x + ":" + loc.y + ":" + loc.z);
				isModified = true;
				return;
			}
		}
	}

	/**
	 * If the player has not yet maxed out his limits, it will add the placed block to his limits.
	 * @return Whether a player has already maxed out their limits.
	 * @see TRListener#onBlockPlace(BlockPlaceEvent) Used by TRListener.onBlockPlace(BlockPlaceEvent)
	 */
	@Nullable public String checkLimit(final BlockPlaceEvent event, final boolean doBypassCheck) {
		//String r = null;
		if (doBypassCheck && event.getPlayer().hasPermission("tekkitrestrict.bypass.limiter")) return null;//true
		final Block block = event.getBlock();
		final int thisid = block.getTypeId();
		final int thisdata = block.getData();

		final int TLimit = getMax(event.getPlayer(), thisid, thisdata);//Get the max for this player for id:data
		String tbr = lastString;
		lastString = null;
		if (TLimit != -1) {
			final TRLocation bloc2 = new TRLocation(block.getLocation());
			for (final TRLimit limit : itemlimits) {
				if ((limit.id != thisid) || (limit.data != thisdata)) continue;

				final int currentnum = limit.placedBlock.size();
				if (currentnum >= TLimit) {
					// this would be at max.
					return tbr == null?"":tbr;//false
				} else {
					// loop through the placedblocks to make sure that we
					// aren't placing the same one down twice.
					boolean place2 = false;
					for (TRLocation j : limit.placedBlock) {
						if (j.equals(bloc2)) {
							place2 = true;
							break;
						}
					}

					if (!place2) {
						limit.placedBlock.add(bloc2);

						allBlockOwners.put(bloc2.world + ":" + bloc2.x + ":" + bloc2.y + ":" + bloc2.z, event.getPlayer().getName());
						isModified = true;
						return null;//true
					} else {
						//This block is already in the placed list, so allow placement but do not increment counts.
						return null;//true
					}
				}
			}

			// it hasn't quite gone through yet. We need to make a new limiter
			// and add this block to it.
			final TRLimit g = new TRLimit();
			g.id = thisid;
			g.data = thisdata;
			g.placedBlock.add(bloc2);
			itemlimits.add(g);

			allBlockOwners.put(bloc2.world + ":" + bloc2.x + ":" + bloc2.y + ":" + bloc2.z, event.getPlayer().getName());
			isModified = true;
			// Making new
			return null;//true
		}

		return null;//true
	}

	public void clearLimits() {
		for (final TRLimit ll : itemlimits) {
			ll.placedBlock.clear();
		}
		itemlimits.clear();
	}

	/** Remove all limits from this player. */
	public void clearLimitsAndClearInDB() {
		for (final TRLimit ll : itemlimits) {
			ll.placedBlock.clear();
		}
		itemlimits.clear();
		saveLimiter(this);
	}

	//Sync
	public int getMax(@NonNull final Player player, final int thisid, final int thisdata){
		try {
			List<TRPermLimit> cached = limiterPermCache.get(player.getName());
			if (cached != null){
				boolean found = false;
				for (final TRPermLimit lim : cached){
					if (lim.compare(thisid, thisdata)){
						if (lim.max == -2) return -1;
						if (lim.max != -1) return lim.max;

						found = true;
					}
				}
				if (found) return -1;
			}

			final TRPermLimit pl = TRPermHandler.getPermLimitFromPerm(player, "tekkitrestrict.limiter", thisid, thisdata);
			if (pl != null) {
				if (cached == null) cached = new ArrayList<TRPermLimit>();
				cached.add(pl);
				limiterPermCache.put(player.getName(), cached);
				if (pl.max == -2) return -1;
				return pl.max;
			} else {
				for (final TRConfigLimit cc : configLimits) {
					if (cc.compare(thisid, thisdata)) {
						lastString = cc.msg;
						return cc.configcount;
					}
				}
			}
		} catch (Exception ex){
			Warning.other("An error occurred while trying to get the maxlimit of a player ('+TRLimiter.getMax(...):int')!", false);
			Log.Exception(ex, false);
		}
		return -1;
	}

	//TODO Is this correct?
	@Override
	public int hashCode(){
		return player.hashCode();
	}
}
