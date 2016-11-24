package nl.taico.tekkitrestrict.safezones;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.TRConfigCache.SafeZones;
import nl.taico.tekkitrestrict.TRDB;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TekkitRestrict;
import nl.taico.tekkitrestrict.annotations.Async;
import nl.taico.tekkitrestrict.objects.TRPos;
import nl.taico.tekkitrestrict.objects.TRWorldPos;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public abstract class TRSafezone {
	/**
	 * @param zone
	 * @return
	 * @throws TRException
	 * @async Should be called asynchronously or at least not from the main thread.
	 */
	@Async
	public static TRSafezone convertToNative(final TRSafezone zone) throws TRException{
		if ((zone.location == null) || zone.location.isEmpty) throw new TRException("Cannot convert safezone! Reason: the safezone has no location set!");
		if (zone instanceof NativeSafezone) throw new TRException("This safezone is already a Native Safezone.");
		final NativeSafezone tbr;
		if (zone.location instanceof TRWorldPos){
			tbr = new NativeSafezone(zone.name, ((TRWorldPos) zone.location).getLesserCorner(), ((TRWorldPos) zone.location).getGreaterCorner());
		} else {
			final World world = Bukkit.getWorld(zone.name);
			if (world == null) throw new TRException("Cannot convert safezone! Reason: cannot find the world for the given safezone!");
			tbr = new NativeSafezone(zone.name, zone.location.getGreaterCorner(world), zone.location.getLesserCorner(world));
		}
		zone.removeZone();
		return tbr;
	}
	public static String getName(final TRSafezone zone){
		return zone.name;
	}
	private final int type;
	protected TRPos location;
	protected String name;
	protected String world;
	protected boolean valid;
	private static ArrayList<TRSafezone> allzones = new ArrayList<TRSafezone>();
	private static ArrayList<TRSafezone> zonesToBeRemoved = new ArrayList<TRSafezone>();

	protected ConcurrentHashMap<String, Boolean> cache = new ConcurrentHashMap<String, Boolean>();

	public static boolean fac, gp, wg, towny, ps;

	static {
		PluginManager PM = Bukkit.getPluginManager();
		fac = SafeZones.UseFactions && PM.isPluginEnabled("Factions");
		gp = SafeZones.UseGP && PM.isPluginEnabled("GriefPrevention");
		wg = SafeZones.UseWG && PM.isPluginEnabled("WorldGuard");
		towny = SafeZones.UseTowny && PM.isPluginEnabled("Towny");
		ps = SafeZones.UsePS && PM.isPluginEnabled("PreciousStones");
	}
	/**
	 * @return A clone of the list of all zones
	 * @async Should be called asynchronously or at least not from the main thread.
	 */
	@Async
	public static ArrayList<TRSafezone> getAllZones(){
		synchronized (allzones){
			return new ArrayList<TRSafezone>(allzones);
		}
	}

	public static TRPos getLocation(final TRSafezone zone){
		return zone.location;
	}
	public static int getType(final TRSafezone zone){
		return zone.type;
	}

	/**
	 * @async Should be called asynchronously or at least not from the main thread.
	 */
	@Async
	private static ArrayList<TRSafezone> getZonesToBeRemoved(){
		synchronized (zonesToBeRemoved){
			return new ArrayList<TRSafezone>(zonesToBeRemoved);
		}
	}
	public static boolean inSafeZone(final Player player){
		if (SafeZones.useNative && NativeSafezone.isInSafezone(player)) return true;
		if (gp && GPSafezone.isInSafezone(player)) return true;
		if (wg && WGSafezone.isInSafezone(player)) return true;
		if (fac && FSafezone.isInSafezone(player)) return true;
		if (ps && PSSafezone.isInSafezone(player)) return true;
		//if (towny && TownySafezone.isInSafezone(player)) return true;

		return false;
	}

	public static boolean isValid(final TRSafezone zone){
		return zone.valid;
	}

	/**
	 * @throws TRException
	 * @async Should be called asynchronously or at least not from the main thread.
	 */
	@Async
	public static void loadFromDB() throws TRException {
		ResultSet rs = null;
		try {
			rs = TekkitRestrict.db.query("SELECT * FROM `tr_safezones`;");
			if (rs == null) throw new TRException("Unable to get Safezones from database!");
			while (rs.next()){
				final int type = rs.getInt("type");

				final World world = Bukkit.getWorld(rs.getString("world"));
				if (world == null) continue;
				switch (type){
				case 0:
					new NativeSafezone(rs.getString("name"), new TRWorldPos(world, rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1"), rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2")));
					break;
				case 1:
					new WGSafezone(rs.getString("name"), new TRWorldPos(world, rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1"), rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2")));
					break;
				case 2:
					break;
				case 3://factions
					new FSafezone(rs.getString("name"), new TRWorldPos(world, rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1"), rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2")));
					break;
				case 4:
					new GPSafezone(rs.getString("name"), new TRWorldPos(world, rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1"), rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2")));
					break;
				}
			}
			rs.close();
		} catch (final SQLException e) {
			try {
				if (rs != null) rs.close();
			} catch (final SQLException e1) {}
			throw new TRException("An error occured while getting safezones from the database! Error: " + e.toString());
		}
	}

	/**
	 * @async Should be called asynchronously or at least not from the main thread.
	 */
	@Async
	public static void saveToDB(){
		for (final TRSafezone zone : getZonesToBeRemoved()){
			try {
				TekkitRestrict.db.query("DELETE FROM `tr_safezones` WHERE `name`=='"+zone.name+"';");
			} catch (SQLException e) {
				Log.Warning.db("Unable to remove zone '" + zone.name + "' from the database!", false);
			}
		}
		synchronized (zonesToBeRemoved){
			zonesToBeRemoved.clear();
		}
		for (final TRSafezone zone : getAllZones()){
			if (zone == null) continue;
			try {
				TekkitRestrict.db.query("INSERT OR REPLACE INTO `tr_safezones` (`name`,`type`,`world`,`x1`,`y1`,`z1`,`x2`,`y2`,`z2`) VALUES ('"
						+ TRDB.antisqlinject(zone.name) + "',"
						+ zone.type + ",'"
						+ zone.world + "',"
						+ zone.location.x1 + ","
						+ zone.location.y1 + ","
						+ zone.location.z1 + ","
						+ zone.location.x2 + ","
						+ zone.location.y2 + ","
						+ zone.location.z2 + ","
						+ ");");
			} catch (final Exception ex) {
				Log.Warning.db("Unable to safe zone '" + zone.name + "' to the database!", false);
			}
		}
	}

	protected TRSafezone(final int type, final String name){
		this.type = type;
		this.name = name;
		this.valid = true;
		synchronized (allzones){
			allzones.add(this);
		}
	}
	public TRPos getLocation(){
		return location;
	}

	public String getName(){
		return name;
	}

	public int getType(){
		return type;
	}

	public boolean isInside(final Location loc){
		if (!loc.getWorld().getName().equalsIgnoreCase(world)) return false;
		return location.contains(loc);
	}

	public boolean isInside(final String world, final int x, final int y, final int z){
		if (!world.equalsIgnoreCase(this.world)) return false;
		return location.contains(x, y, z);
	}
	public boolean isInside(final World world, final int x, final int y, final int z){
		if (!world.getName().equalsIgnoreCase(this.world)) return false;
		return location.contains(x, y, z);
	}

	public abstract boolean isSafezoneFor(final Player player);

	public boolean isValid(){
		return valid;
	}

	/**
	 * @async Should be called asynchronously or at least not from the main thread.
	 */
	@Async
	protected abstract void remove();

	/**
	 * @async Should be called asynchronously or at least not from the main thread.
	 */
	@Async
	public final void removeZone(){
		synchronized (zonesToBeRemoved){
			zonesToBeRemoved.add(this);
		}
		synchronized (allzones){
			allzones.remove(this);
		}
		remove();
	}

	public abstract void update();
}
