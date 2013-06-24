package com.github.dreadslicer.tekkitrestrict;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.Field;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.SafeZones;
import com.github.dreadslicer.tekkitrestrict.api.SafeZones.SafeZoneCreate;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.SSMode;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FPerm;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

//IMPORTANT Do not check with isEnabled, but try to get plugin immediately?
public class TRSafeZone {

	public int x1, y1, z1;
	public int x2, y2, z2;
	public String name;
	public String world, data = "";
	public boolean loadedFromSqlite = false;
	/**
	 * 0 = NONE<br>
	 * 1 = WorldGuard<br>
	 * 2 = PStones<br>
	 * 3 = Factions<br>
	 * 4 = GriefPrevention
	 */
	public int mode = 0;
	//public static HashMap<String, >

	public static List<TRSafeZone> zones = Collections.synchronizedList(new LinkedList<TRSafeZone>());

	public static void init() {
		ResultSet rs = null;
		try {
			rs = tekkitrestrict.db.query("SELECT * FROM `tr_saferegion`");
			while (rs.next()) {
				TRSafeZone sz = new TRSafeZone();
				sz.name = rs.getString("name");
				sz.world = rs.getString("world");
				sz.mode = rs.getInt("mode");
				sz.data = rs.getString("data");
				sz.loadedFromSqlite = true;
				zones.add(sz);
			}
			rs.close();
		} catch (SQLException e) {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {}
		}
		
	}

	public long getID(){
		long estID = 0;
		// get a numeral from the player's name & world
		for (int j = 0; j < name.length(); j++) {
			char c = name.charAt(j);
			estID += Character.getNumericValue(c);
		}
		for (int j = 0; j < world.length(); j++) {
			char c = world.charAt(j);
			estID += Character.getNumericValue(c);
		}
		estID=estID+x1+x2+y1+y2+z1+z2;
		estID=estID+x1*x2+y1*y2+z1*z2;
		//tekkitrestrict.log.info("estID: "+estID);
		return estID;
	}
	
	public static void save() {
		for (int i = 0; i < zones.size(); i++) {
			TRSafeZone z = zones.get(i);
			if (!z.loadedFromSqlite) {
				// insert the new rows!
				try {
					tekkitrestrict.db.query(
							"INSERT OR REPLACE INTO `tr_saferegion` (`id`,`name`,`mode`,`data`,`world`) VALUES ('"
									+ z.getID() +"','"
									+ tekkitrestrict.antisqlinject(z.name)
									+ "'," + z.mode + ",'" + z.data + "','"
									+ z.world + "')");
					z.loadedFromSqlite = true;
				} catch (Exception E) {
				}
			}
		}
	}
	private static PluginManager PM(){
		return Bukkit.getPluginManager();
	}
	
	/**
	 * Removes a safezone from the database.<br>
	 * For GP this also removes [tekkitrestrict] from the managers.<br>
	 * Do not use this method in a loop. It will cause concurrentmodification exceptions.
	 * @return True if the removal succeeded.
	 */
	public static boolean removeSafeZone(TRSafeZone zone){
		if (zone.mode == 1){
			zones.remove(zone); //Remove from database
			return true;
		}
		
		if (zone.mode == 4){
			//IMPORTANT Potential problem when a claim gets resized.
			if (!SafeZones.UseGP || !PM().isPluginEnabled("GriefPrevention")) return false;
			if (zone.data == null) return false;
			
			String locStr[] = zone.data.split(",");
			int x, y, z;
			try {
				x = Integer.parseInt(locStr[0]);
				y = Integer.parseInt(locStr[1]);
				z = Integer.parseInt(locStr[2]);
			} catch (Exception ex){
				return false;
			}
			Location loc = new Location(Bukkit.getWorld(zone.world), x, y, z);
			GriefPrevention pl = (GriefPrevention) PM().getPlugin("GriefPrevention");
			Claim claim = pl.dataStore.getClaimAt(loc, true, null);
			if (claim == null) return true; //Already removed
			claim.managers.remove("[tekkitrestrict]"); //Remove from managers
			zones.remove(zone); //Remove from database
			return true;
		}
		
		return false;
	}
	
	public static SafeZoneCreate addSafeZone(Player player, String pluginName, String name){
		if (!SafeZones.UseSafeZones) return SafeZoneCreate.SafeZonesDisabled;
		
		name = name.toLowerCase();
		
		for (TRSafeZone current : TRSafeZone.zones){
			//if (current.world.equalsIgnoreCase(player.getWorld().getName())){
				if (current.name.toLowerCase().equals(name)){
					return SafeZoneCreate.AlreadyExists;
				}
			//}
		}
		
		pluginName = pluginName.toLowerCase();
		PluginManager PM = PM();
		
		if (SafeZones.UseGP && pluginName.equals("griefprevention") && PM.isPluginEnabled("GriefPrevention")) {
			GriefPrevention pl = (GriefPrevention) PM.getPlugin("GriefPrevention");
			Claim claim = pl.dataStore.getClaimAt(player.getLocation(), false, null);
			if (claim == null){
				return SafeZoneCreate.RegionNotFound;
			}
			
			Location loc1 = claim.getLesserBoundaryCorner();
			Location loc2 = claim.getGreaterBoundaryCorner();
			
			if (!allowedToMakeGPSafeZone(player, claim)) return SafeZoneCreate.NoPermission;

			if (claim.managers.contains("[tekkitrestrict]")) return SafeZoneCreate.AlreadyExists;
			claim.managers.add("[tekkitrestrict]");
			
			TRSafeZone zone = new TRSafeZone();
			zone.mode = 4;
			zone.data = loc1.getBlockX() + "," + loc1.getBlockY() + "," + loc1.getBlockZ() + "," + loc2.getBlockX() + "," + loc2.getBlockY() + "," + loc2.getBlockZ();
			zone.name = name;
			zone.world = loc1.getWorld().getName();
			TRSafeZone.zones.add(zone);
			TRSafeZone.save();
			return SafeZoneCreate.Success;
			
		} else if (SafeZones.UseWG && pluginName.equals("worldguard") && PM.isPluginEnabled("WorldGuard")) {
			try {
				WorldGuardPlugin wg = (WorldGuardPlugin) PM.getPlugin("WorldGuard");
				Map<String, ProtectedRegion> rm = wg.getRegionManager(player.getWorld()).getRegions();
				ProtectedRegion pr = rm.get(name);
				if (pr == null) {
					return SafeZoneCreate.RegionNotFound;
				}
				
				TRSafeZone zone = new TRSafeZone();
				zone.mode = 1;
				zone.name = name;
				zone.world = player.getWorld().getName();
				TRSafeZone.zones.add(zone);
				TRSafeZone.save();
				return SafeZoneCreate.Success;
			} catch (Exception E) {
				return SafeZoneCreate.Unknown;
			}
		} else {
			return SafeZoneCreate.PluginNotFound;
		}
	}
	
	/** @return If the given player is allowed to turn the given claim into a safezone. */
	public static boolean allowedToMakeGPSafeZone(Player p, Claim claim){
		if (SafeZones.GPMode.isAdmin()){
			if (!claim.isAdminClaim()) return false; //Only admin claims can be made safezones.
			return p.hasPermission("griefprevention.adminclaims"); //Only admins can make admin claims safezones.
		}
		
		if (SafeZones.GPMode == SSMode.All) return false; //All claims are safezones, so specific adding is not allowed.
		
		String name = p.getName().toLowerCase();
		
		if (claim.ownerName.equalsIgnoreCase(name)) return true; //Owner of claim is allowed
		
		Iterator<String> managers = claim.managers.iterator();
		while (managers.hasNext()){
			if (managers.next().equalsIgnoreCase(name)) return true; //Manager of claim is allowed
		}
		
		return false; //Otherwise it's not allowed.
	}
	
	/**
	 * Uses {@link #getSafeZone(Player)}
	 * @return If the given player is currently in a safezone.
	 */
	public static boolean inSafeZone(Player player) {
		if (!SafeZones.UseSafeZones) return false;
		
		return !getSafeZone(player).equals("");
	}

	/**
	 * @return A String with information about the Safezone the given player is in.<br>
	 * Returns "" if the player isn't in a safezone, or if the safezone cannot be found.
	 */
	public static String getSafeZone(Player player) {
		if (!SafeZones.UseSafeZones) return "";
		
		if (Util.hasBypass(player, "safezone")) return "";
		
		if (!allowedInGriefPreventionSafeZone2(player)) return "GriefPrevention Safezone Claim owned by: " + lastGPClaim;
		
		String r = getSafeZoneByLocation(player.getLocation(), false);
		if (!r.equals("")) return r;
		
		if (!allowedInTownySafeZone(player)) return "Towny Safezone";
		if (!allowedInFactionsSafeZone(player)) return "Safezone Faction: " + lastFaction;
		if (!allowedInPreciousStonesSafeZone(player)) return "PreciousStones SafeZone Field: " + lastPS;
		
		return "";
	}

	/** Uses {@link #getSafeZoneByLocation(Location, boolean)} (doGP = true)*/
	public static boolean inXYZSafeZone(Location loc) {
		if (!SafeZones.UseSafeZones) return false;
		
		return !getSafeZoneByLocation(loc, true).equals("");
	}
	
	/**
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return If the given player is allowed in the Towny safezone he is in. <br>(All towny zones are safezones by default).
	 */
	public static boolean allowedInTownySafeZone(Player p){
		if (!SafeZones.UseTowny || !PM().isPluginEnabled("Towny")) return true;
		
		Block cb = p.getWorld().getHighestBlockAt(p.getLocation());
		boolean hasperm = PlayerCacheUtil.getCachePermission(p, p.getLocation(), cb.getTypeId(), (byte) 0, TownyPermission.ActionType.DESTROY);
		//TownBlockStatus tbs = PlayerCacheUtil.getTownBlockStatus(p, WorldCoord.parseWorldCoord(p.getLocation()));
		//boolean ls = tbs != TownBlockStatus.UNCLAIMED_ZONE && tbs != TownBlockStatus.WARZONE && tbs != TownBlockStatus.UNKOWN;
		return hasperm;
	}
	
	private static String lastFaction = "";
	/**
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return If the given player is allowed in the Factions safezone he is in. <br>(All faction zones are safezones if enabled).
	 */
	public static boolean allowedInFactionsSafeZone(Player p){
		//TODO Check if Factions has an option to add flags.
		if (!SafeZones.UseFactions || !PM().isPluginEnabled("Factions")) return true;
		String name = p.getName();
		
		FPlayer fplayer = FPlayers.i.get(name);
		if (Conf.playersWhoBypassAllProtection.contains(name)) return true;
		
		FLocation ccc = new FLocation(p);
		Faction f = Board.getFactionAt(ccc);
		if (f != null) lastFaction = f.getTag();
		if (!FPerm.BUILD.has(fplayer, ccc)) return false;
		
		return true;
	}
	
	private static String lastPS = "";
	/**
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return If the given player is allowed in the PreciousStones safezone he is in. <br>(All PS zones are safezones if enabled).
	 */
	public static boolean allowedInPreciousStonesSafeZone(Player p){
		//TODO Check if PS has an option to add flags.
		PluginManager PM = PM();
		if (!SafeZones.UsePS || !PM.isPluginEnabled("PreciousStones")) return true;
		PreciousStones ps = (PreciousStones) PM.getPlugin("PreciousStones");
		Block fblock = p.getWorld().getBlockAt(p.getLocation());
		
		Field field = ps.getForceFieldManager().getEnabledSourceField(fblock.getLocation(), FieldFlag.CUBOID);
		//ps.getForceFieldManager().
		if (field == null) return true;
		lastPS = field.getName();

		boolean allowed = ps.getForceFieldManager().isApplyToAllowed(field, p.getName());
		if (!allowed || field.hasFlag(FieldFlag.APPLY_TO_ALL)) return false;
		
		return true;
	}

	private static String lastGPClaim = "";
	/**
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return If the given player is allowed in the GriefPrevention claim he is in.<br>
	 * If this player isn't in a GriefPrevention claim or if the claim isn't a safezone, this will return true.
	 * @deprecated Does not check with the database if a GP claim is an actual safezone.
	 */
	public static boolean allowedInGriefPreventionSafeZone(Player p){
		PluginManager PM = PM();
		if (!SafeZones.UseGP || !PM.isPluginEnabled("GriefPrevention")) return true; //If plugin disabled or not used for SafeZones, return true. (allowed)
		
		GriefPrevention pl = (GriefPrevention) PM.getPlugin("GriefPrevention");
		Claim claim = pl.dataStore.getClaimAt(p.getLocation(), false, null);
		if (claim == null) return true; //If no claim here, return true. (allowed)
		lastGPClaim = claim.ownerName;
		
		String name = p.getName().toLowerCase();
		if (claim.ownerName.equalsIgnoreCase(name)) return true; //If owner of claim, return true. (allowed)
		
		//Admin
		if (SafeZones.GPMode == SSMode.Admin){
			if (!claim.isAdminClaim()) return true; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode: return true. (allowed)
			return p.hasPermission("griefprevention.adminclaims"); //If the player is a GPAdmin, return true. (allowed) Else return false. (not allowed)
		}
		
		//SpecificAdmin
		if (SafeZones.GPMode == SSMode.SpecificAdmin){
			if (!claim.isAdminClaim()) return true; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode: return true. (allowed)
			if (p.hasPermission("griefprevention.adminclaims")) return true; //If the player is a GPAdmin, return true. (allowed) Else return false. (not allowed)
			return !claim.managers.contains("[tekkitrestrict]"); //If it contains [tekkitrestrict], then it returns false. (not allowed) Else return true. (allowed)
		}
		
		//Specific
		if (SafeZones.GPMode == SSMode.Specific){
			if (!claim.managers.contains("[tekkitrestrict]")) return true; //If it is not a safezone, return true. (allowed)
		}
		
		//All
		Iterator<String> managers = claim.managers.iterator();
		while (managers.hasNext()){
			if (managers.next().equalsIgnoreCase(name)) return true; //If manager of claim, return true. (allowed)
		}
		
		return false; //Otherwise return false. (not allowed)
	}
	
	/**
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)<br>
	 * Checks with the database if the claim the player is in is an actual safezone.
	 * @return If the given player is allowed in the GriefPrevention claim he is in.<br>
	 * If this player isn't in a GriefPrevention claim or if the claim isn't a safezone, this will return true.
	 */
	public static boolean allowedInGriefPreventionSafeZone2(Player p){
		PluginManager PM = PM();
		if (!SafeZones.UseGP || !PM.isPluginEnabled("GriefPrevention")) return true; //If plugin disabled or not used for SafeZones, return true. (allowed)
		
		GriefPrevention pl = (GriefPrevention) PM.getPlugin("GriefPrevention");
		Claim claim = pl.dataStore.getClaimAt(p.getLocation(), false, null);
		if (claim == null) return true; //If no claim here, return true. (allowed)
		lastGPClaim = claim.ownerName;
		
		String name = p.getName().toLowerCase();
		if (claim.ownerName.equalsIgnoreCase(name)) return true; //If owner of claim, return true. (allowed)
		
		//Admin
		if (SafeZones.GPMode == SSMode.Admin){
			if (!claim.isAdminClaim()) return true; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode: return true. (allowed)
			return p.hasPermission("griefprevention.adminclaims"); //If the player is a GPAdmin, return true. (allowed) Else return false. (not allowed)
		}
		
		//SpecificAdmin
		if (SafeZones.GPMode == SSMode.SpecificAdmin){
			if (!claim.isAdminClaim()) return true; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode: return true. (allowed)
			if (p.hasPermission("griefprevention.adminclaims")) return true; //If the player is a GPAdmin, return true. (allowed) Else return false. (not allowed)
		}
		
		if (!claim.managers.contains("[tekkitrestrict]")) return true; //If it doesn't contain [tekkitrestrict], then it returns false. (not allowed)
		Iterator<String> managers = claim.managers.iterator();
		while (managers.hasNext()){
			if (managers.next().equalsIgnoreCase(name)) return true; //If manager of claim, return true. (allowed)
		}
		
		Location locp = p.getLocation();
		double xp = locp.getX();
		//double yp = locp.getY();
		double zp = locp.getZ();
		
		Iterator<TRSafeZone> zonesIterator = zones.iterator();
		while (zonesIterator.hasNext()) {
			TRSafeZone a = zonesIterator.next();
			if (a.mode == 4){ //GriefPrevention
				String temp[] = a.data.split(",");
				if (temp.length != 6) continue;
				World world = Bukkit.getWorld(a.world);
				if (world == null) world = p.getWorld();
				
				double x1 = IP(temp[0]), x2 = IP(temp[3]);
				//double y1 = IP(temp[1]), y2 = IP(temp[4]);
				double z1 = IP(temp[2]), z2 = IP(temp[5]);
				if (!(xp >= x1 && xp <= x2) && !(xp >= x2 && xp <= x1)) continue;
				if (!(zp >= z1 && zp <= z2) && !(zp >= z2 && zp <= z1)) continue;
				return false;
			}
		}
		
		return true;
	}
	
	private static double IP(String s){
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException ex){
			return 0;
		}
	}
	
	
	/**
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)<br>
	 * <b>Note: Does not check if the WorldGuard region is a safezone or not.</b><br>
	 * @return If the given player is allowed in the WorldGuard Region he is in.
	 */
	public static boolean allowedInWorldGuardRegion(Player player){
		try {
			WorldGuardPlugin WGB = (WorldGuardPlugin) PM().getPlugin("WorldGuard");
			if (SafeZones.WGMode == SSMode.All) return WGB.canBuild(player, player.getLocation());
			else {
				
			}
		} catch (Exception ex){}
		return true;
	}
	
	/**
	 * <b>Uses the database for information.</b>
	 * @return A string with information about the type of safezone and its name/owner.<br>
	 * Returns "" if there is none.
	 */
	public static String getSafeZoneByLocation(Location loc, boolean doGP) {
		if (!SafeZones.UseSafeZones) return "";
		
		boolean WGEnabled = PM().isPluginEnabled("WorldGuard"), GPEnabled = PM().isPluginEnabled("GriefPrevention");
		String r = "";

		double xl = loc.getX();
		double zl = loc.getX();
		Iterator<TRSafeZone> zonesIterator = zones.iterator();
		while (zonesIterator.hasNext()) {
			TRSafeZone a = zonesIterator.next();
			if (a.mode == 0){
				// do nothing... (for now)
				continue;
			}
			
			if (a.mode == 1){ //WorldGuard
				if (!WGEnabled || !SafeZones.UseWG) continue;
				r = getWGRegion(a.name, loc);
				if (!r.equals("")) return "WorldGuard Safezone Region: " + r;
				continue;
			}
			
			//TODO PS support
			
			if (a.mode == 4){ //GriefPrevention
				if (!doGP) continue;
				if (!GPEnabled || !SafeZones.UseGP) continue;
				String temp[] = a.data.split(",");
				if (temp.length != 6) continue;
				
				double x1 = IP(temp[0]), x2 = IP(temp[3]);
				//double y1 = IP(temp[1]), y2 = IP(temp[4]);
				double z1 = IP(temp[2]), z2 = IP(temp[5]);
				if (!(xl >= x1 && xl <= x2) && !(xl >= x2 && xl <= x1)) continue;
				if (!(zl >= z1 && zl <= z2) && !(zl >= z2 && zl <= z1)) continue;

				r = getGPSafeZone(loc);
				if (!r.equals("")) return "GriefPrevention Safezone Claim owned by: " + r;
				continue;
			}
		}
		return r;
		
	}
	
	/**
	 * Gets a WorldGuard SafeZone at the given location.<br>
	 * <b>Note: Doesn't check if WorldGuard is enabled.</b>
	 */
	public static String getWGRegion(String name, Location loc){
		WorldGuardPlugin WGB = (WorldGuardPlugin) PM().getPlugin("WorldGuard");
		try {
			//World world = tekkitrestrict.getInstance().getServer().getWorld(a.world);
			//if (world == null) world = loc.getWorld();

			World world = loc.getWorld();
			ProtectedRegion PR = WGB.getRegionManager(world).getRegion(name);
			
			if (PR == null) return "";
			if (PR.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
				return name;
			}
		} catch (Exception ex) {
		}
		return "";
	}
	
	/**
	 * Gets a GriefPrevention SafeZone at the given location.<br>
	 * <b>Note: Doesn't check if GriefPrevention is enabled.</b>
	 * @deprecated Does not actually get SafeZones from the database.
	 */
	public static String getGPSafeZone(Location loc){
		//TODO can be made more efficient (move ownername stuff over abit)
		try {
			GriefPrevention pl = (GriefPrevention) PM().getPlugin("GriefPrevention");
			Claim claim = pl.dataStore.getClaimAt(loc, false, null);
			if (claim == null) return ""; //No claim here.
			
			String ownername = claim.getOwnerName();
			if (ownername.equals("")) ownername = "Admin";
			lastGPClaim = ownername;
			//Admin
			if (SafeZones.GPMode == SSMode.Admin){
				if (!claim.isAdminClaim()) return ""; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
				return ownername;
			}
			
			//All
			else if (SafeZones.GPMode == SSMode.All){
				return ownername;
			}
			
			//SpecificAdmin
			else if (SafeZones.GPMode == SSMode.SpecificAdmin){
				if (!claim.isAdminClaim()) return ""; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
				if (!claim.managers.contains("[tekkitrestrict]")) return ""; //Not SafeZone
				return ownername;
			}
			
			//Specific
			else if (SafeZones.GPMode == SSMode.Specific){
				if (!claim.managers.contains("[tekkitrestrict]")) return ""; //Not SafeZone
				return ownername;
			}
			
			//There shouldn't be any more possible cases.
			return ownername;
		} catch (Exception ex){}
		return "";
	}
	
	public static Claim getGPClaim(Location loc){
		//TODO can be made more efficient (move ownername stuff over abit)
		try {
			GriefPrevention pl = (GriefPrevention) PM().getPlugin("GriefPrevention");
			Claim claim = pl.dataStore.getClaimAt(loc, false, null);
			return claim;
		} catch (Exception ex){}
		return null;
	}

	//Unused.
	/*public static void setFly(PlayerMoveEvent e) {
		if (SafeZones.SSDisableFly) {
			Player player = e.getPlayer();
			if (inSafeZone(player)) {
				// ground player
				TRNoHack.groundPlayer(player);
				player.sendMessage(ChatColor.RED + "[TRSafeZone] You may not fly in safezones!");
			}
		}
	}*/
}
