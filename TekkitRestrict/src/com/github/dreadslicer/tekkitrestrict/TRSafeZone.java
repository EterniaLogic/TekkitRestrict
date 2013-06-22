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
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.SafeZones.SSGPMode;
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

public class TRSafeZone {
	public static enum SafeZoneCreate {
		Success, AlreadyExists, RegionNotFound, PluginNotFound, Unknown, SafeZonesDisabled;
	}

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
	public static boolean removeSafeZone(TRSafeZone zone){
		if (zone.mode == 1){
			return true;
		}
		
		if (zone.mode == 4){
			//IMPORTANT Potential problem when a claim gets resized.
			PluginManager PM = Bukkit.getPluginManager();
			if (!PM.isPluginEnabled("GriefPrevention") || !SafeZones.SSPlugins.contains("griefprevention")) return false;
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
			GriefPrevention pl = (GriefPrevention) PM.getPlugin("GriefPrevention");
			Claim claim = pl.dataStore.getClaimAt(loc, true, null);
			if (claim == null) return true; //Already removed
			return claim.managers.remove("[tekkitrestrict]");
		}
		
		return false;
	}
	public static SafeZoneCreate addSafeZone(Player player, String pluginName, String name){
		if (!SafeZones.UseSafeZones) return SafeZoneCreate.SafeZonesDisabled;
		
		name = name.toLowerCase();
		
		for (TRSafeZone current : TRSafeZone.zones){
			if (current.world.equalsIgnoreCase(player.getWorld().getName())){
				if (current.name.toLowerCase().equals(name)){
					return SafeZoneCreate.AlreadyExists;
				}
			}
		}
		
		pluginName = pluginName.toLowerCase();
		PluginManager PM = Bukkit.getPluginManager();
		
		if (pluginName.equals("griefprevention") && PM.isPluginEnabled("GriefPrevention") && SafeZones.SSPlugins.contains("griefprevention")) {
			GriefPrevention pl = (GriefPrevention) PM.getPlugin("GriefPrevention");
			Claim claim = pl.dataStore.getClaimAt(player.getLocation(), false, null);
			if (claim == null){
				return SafeZoneCreate.RegionNotFound;
			}

			if (claim.managers.contains("[tekkitrestrict]")) return SafeZoneCreate.AlreadyExists;
			claim.managers.add("[tekkitrestrict]");
			
			TRSafeZone zone = new TRSafeZone();
			zone.mode = 4;
			Location loc = player.getLocation();
			zone.data = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
			zone.name = name;
			zone.world = player.getWorld().getName();
			TRSafeZone.zones.add(zone);
			TRSafeZone.save();
			return SafeZoneCreate.Success;
			
		} else if (pluginName.equals("WorldGuard") && PM.isPluginEnabled("WorldGuard") && SafeZones.SSPlugins.contains("worldguard")) {
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
	
	public static boolean inSafeZone(Player p) {
		if (!SafeZones.UseSafeZones) return false;
		
		return !getSafeZone(p).equals("");
	}

	public static String getSafeZone(Player p) {
		if (!SafeZones.UseSafeZones) return "";
		
		if (Util.hasBypass(p, "safezone")) return "";
		
		PluginManager PM = tekkitrestrict.getInstance().getServer().getPluginManager();
		if (!allowedInGriefPreventionSafeZone(p, PM)) return "GriefPrevention Safezone Claim owned by: " + lastGPClaim;
		if (!allowedInTownySafeZone(p, PM)) return "Towny Safezone";
		if (!allowedInFactionsSafeZone(p, PM)) return "Safezone Faction: " + lastFaction;
		if (!allowedInPreciousStonesSafeZone(p, PM)) return "PreciousStones SafeZone Field: " + lastPS;

		return getSafeZoneByLocation(p.getLocation());
	}

	/**
	 * Uses {@link #getSafeZoneByLocation(Location)}
	 */
	public static boolean inXYZSafeZone(Location loc) {
		if (!SafeZones.UseSafeZones) return false;
		
		return !getSafeZoneByLocation(loc).equals("");
	}
	
	/**
	 * Note: Does not check if safezones are disabled.<br>
	 * Note: Does not check for bypass permission (tekkitrestrict.bypass.safezone)
	 * @return If the given player is allowed in the Towny safezone he is in. <br>(All towny zones are safezones by default).
	 */
	public static boolean allowedInTownySafeZone(Player p, PluginManager PM){
		if (!SafeZones.UseTowny || !PM.isPluginEnabled("Towny")) return true;
		
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
	public static boolean allowedInFactionsSafeZone(Player p, PluginManager PM){
		//TODO Check if Factions has an option to add flags.
		if (!SafeZones.UseFactions || !PM.isPluginEnabled("Factions")) return true;
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
	public static boolean allowedInPreciousStonesSafeZone(Player p, PluginManager PM){
		//TODO Check if PS has an option to add flags.
		if (!SafeZones.UsePS || !PM.isPluginEnabled("PreciousStones")) return true;
		PreciousStones ps = (PreciousStones) PM.getPlugin("PreciousStones");
		Block fblock = p.getWorld().getBlockAt(p.getLocation());
		
		Field field = ps.getForceFieldManager().getEnabledSourceField(fblock.getLocation(), FieldFlag.CUBOID);

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
	 */
	public static boolean allowedInGriefPreventionSafeZone(Player p, PluginManager PM){
		if (!SafeZones.UseGP || !PM.isPluginEnabled("GriefPrevention")) return true; //If plugin disabled or not used for SafeZones, return true. (allowed)
		
		GriefPrevention pl = (GriefPrevention) PM.getPlugin("GriefPrevention");
		Claim claim = pl.dataStore.getClaimAt(p.getLocation(), false, null);
		if (claim == null) return true; //If no claim here, return true. (allowed)
		lastGPClaim = claim.ownerName;
		
		String name = p.getName().toLowerCase();
		if (claim.ownerName.equalsIgnoreCase(name)) return true; //If owner of claim, return true. (allowed)
		
		//Admin
		if (SafeZones.GPMode == SSGPMode.Admin){
			if (!claim.isAdminClaim()) return true; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode: return true. (allowed)
			return p.hasPermission("griefprevention.adminclaims"); //If the player is a GPAdmin, return true. (allowed) Else return false. (not allowed)
		}
		
		//SpecificAdmin
		if (SafeZones.GPMode == SSGPMode.SpecificAdmin){
			if (!claim.isAdminClaim()) return true; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode: return true. (allowed)
			if (p.hasPermission("griefprevention.adminclaims")) return true; //If the player is a GPAdmin, return true. (allowed) Else return false. (not allowed)
			return !claim.managers.contains("[tekkitrestrict]"); //If it contains [tekkitrestrict], then it returns false. (not allowed) Else return true. (allowed)
		}
		
		//Specific
		if (SafeZones.GPMode == SSGPMode.Specific){
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
	 * @return A string with information about the type of safezone and its name/owner.
	 */
	public static String getSafeZoneByLocation(Location loc) {
		if (!SafeZones.UseSafeZones) return "";
		String r = "";
		// ResultSet rs =
		// tekkitrestrict.db.query("SELECT * FROM `tr_safezones` WHERE `world` = '"+world+"'");
		/*
		 * if(tekkitrestrict.config.getBoolean("UseSafeZones")){ //determine if
		 * the player is in the 3D cube. for(int i=0;i<zones.size();i++){
		 * safeZone rs = zones.get(i); if(world.equals(rs.world)){ int x1 =
		 * rs.x1; int y1 = rs.y1; int z1 = rs.z1; int x2 = rs.x2; int y2 =
		 * rs.y2; int z2 = rs.z2; double x = l.getX(); double y= l.getY();
		 * double z = l.getZ(); boolean it = x >= x1 && x < x2 + 1 && y >= y1 &&
		 * y < y2 + 1 && z >= z1 && z < z2 + 1; if(it){ return rs.name; } } } }
		 */
		
		PluginManager PM = tekkitrestrict.getInstance().getServer().getPluginManager();
		boolean WGEnabled = PM.isPluginEnabled("WorldGuard"), GPEnabled = PM.isPluginEnabled("GriefPrevention");
		//if (!SafeZones.UseWG || !PM.isPluginEnabled("WorldGuard")) return "";
		
		for (int i = 0; i < zones.size(); i++) {
			TRSafeZone a = zones.get(i);
			if (a.mode == 0){
				// do nothing... (for now)
				continue;
			}
			
			if (a.mode == 1){ //WorldGuard
				if (!WGEnabled || !SafeZones.UseWG) continue;
				r = getWGRegion(a, loc, PM);
				if (!r.equals("")) return "WorldGuard Safezone Region: " + r;
				continue;
			}
			
			if (a.mode == 4){ //GriefPrevention
				if (!GPEnabled || !SafeZones.UseGP) continue;
				r = getGPRegion(a, loc, PM);
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
	public static String getWGRegion(TRSafeZone a, Location loc, PluginManager PM){
		WorldGuardPlugin WGB = (WorldGuardPlugin) PM.getPlugin("WorldGuard");
		try {
			// WorldGuard may not be loaded
			World world = tekkitrestrict.getInstance().getServer().getWorld(a.world);
			if (world == null) world = loc.getWorld();
			ProtectedRegion PR = WGB.getRegionManager(world).getRegion(a.name);
			if (PR.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
				return a.name;
			}
		} catch (Exception ex) {
		}
		return "";
	}
	
	/**
	 * Gets a GriefPrevention SafeZone at the given location.<br>
	 * <b>Note: Doesn't check if WorldGuard is enabled.</b>
	 */
	public static String getGPRegion(TRSafeZone a, Location l, PluginManager PM){
		GriefPrevention pl = (GriefPrevention) PM.getPlugin("GriefPrevention");
		Claim claim = pl.dataStore.getClaimAt(l, false, null);
		if (claim == null) return ""; //No claim here.
		
		//Admin
		if (SafeZones.GPMode == SSGPMode.Admin){
			if (!claim.isAdminClaim()) return ""; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
			return claim.getOwnerName();
		}
		
		//All
		else if (SafeZones.GPMode == SSGPMode.All){
			return claim.getOwnerName();
		}
		
		//SpecificAdmin
		else if (SafeZones.GPMode == SSGPMode.SpecificAdmin){
			if (!claim.isAdminClaim()) return ""; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
			if (!claim.managers.contains("[tekkitrestrict]")) return ""; //Not SafeZone
			return claim.getOwnerName();
		}
		
		//Specific
		else if (SafeZones.GPMode == SSGPMode.Specific){
			if (!claim.managers.contains("[tekkitrestrict]")) return ""; //Not SafeZone
			return claim.getOwnerName();
		}
		
		//There shouldn't be any more possible cases.
		return claim.getOwnerName();
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
