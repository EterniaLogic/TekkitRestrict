package com.github.dreadslicer.tekkitrestrict;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.Field;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.SafeZones;
import com.massivecraft.factions.struct.FPerm;
import com.palmergames.bukkit.towny.object.PlayerCache.TownBlockStatus;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TRSafeZone {
	public TRSafeZone() {}
	
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
	private static List<String> depends;
	private static boolean SSDisableFly;

	public static void reload() {
		depends = tekkitrestrict.config.getStringList("SSEnabledPlugins");
		SSDisableFly = tekkitrestrict.config.getBoolean("SSDisableFlying");
	}

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
			if (!PM.isPluginEnabled("GriefPrevention") || !depends.contains("griefprevention")) return false;
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
		if (!tekkitrestrict.config.getBoolean("UseSafeZones")) return SafeZoneCreate.SafeZonesDisabled;
		
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
		
		if (pluginName.equals("griefprevention") && PM.isPluginEnabled("GriefPrevention") && depends.contains("griefprevention")) {
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
			
		} else if (pluginName.equals("WorldGuard") && PM.isPluginEnabled("WorldGuard") && depends.contains("worldguard")) {
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
		if (!tekkitrestrict.config.getBoolean("UseSafeZones")) return false;
		
		return getSafeZone(p) != "";
	}

	@SuppressWarnings("deprecation")
	public static String getSafeZone(Player p) {
		if (!tekkitrestrict.config.getBoolean("UseSafeZones")) return "";
		
		if (Util.hasBypass(p, "safezone")) return "";
		
		PluginManager PM = tekkitrestrict.getInstance().getServer().getPluginManager();
		if (PM.isPluginEnabled("Towny") && depends.contains("towny")) {
			/*
			 * com.palmergames.bukkit.towny.Towny tapp =
			 * (com.palmergames.bukkit
			 * .towny.Towny)tekkitrestrict.getInstance
			 * ().getServer().getPluginManager().getPlugin("Towny");
			 * com.palmergames.bukkit.towny.object.PlayerCache c =
			 * tapp.getCache(p);
			 * if(!tapp.getTownyUniverse().isWilderness(p.getWorld
			 * ().getHighestBlockAt(p.getLocation()))){ return "towny";
			 * }
			 */
			Block cb = p.getWorld().getHighestBlockAt(p.getLocation());
			boolean hasperm = com.palmergames.bukkit.towny.utils.PlayerCacheUtil
					.getCachePermission(
							p,
							p.getLocation(),
							cb.getTypeId(),
							com.palmergames.bukkit.towny.object.TownyPermission.ActionType.DESTROY);
			TownBlockStatus tbs = com.palmergames.bukkit.towny.utils.PlayerCacheUtil.getTownBlockStatus(p, WorldCoord.parseWorldCoord(p.getLocation()));
			//boolean ls = tbs != TownBlockStatus.UNCLAIMED_ZONE && tbs != TownBlockStatus.WARZONE && tbs != TownBlockStatus.UNKOWN;
			if (!hasperm) {
				// tekkitrestrict.log.info("towny");
				return "towny+";
			}
		}
		// tekkitrestrict.log.info("deb");
		if (PM.isPluginEnabled("Factions") && depends.contains("factions")) {
			// if(!com.massivecraft.factions.listeners.FactionsPlayerListener.canPlayerUseBlock(p,
			// p.getWorld().getHighestBlockAt(p.getLocation()), true)){
			// Location cccc =
			// p.getWorld().getHighestBlockAt(p.getLocation()).getLocation();

			com.massivecraft.factions.FLocation ccc = new com.massivecraft.factions.FLocation(
					p.getPlayer());
			com.massivecraft.factions.Faction f = com.massivecraft.factions.Board
					.getFactionAt(ccc);
			String name = p.getPlayer().getName();

			com.massivecraft.factions.FPlayer me = com.massivecraft.factions.FPlayers.i
					.get(name);
			if (!com.massivecraft.factions.Conf.playersWhoBypassAllProtection
					.contains(name)) {
				/*if (me.getFaction() != null && f != null && !me.hasAdminMode()) {
					if ((me.getFaction().getTag() != f.getTag())
							&& !f.isNone()) {
						// tekkitrestrict.log.info("factions");
						
					}
				}*/
				if (!FPerm.BUILD.has(me, ccc)){
					return "factions+" + f.getTag();
				}
			}
		}

		if (PM.isPluginEnabled("PreciousStones") && depends.contains("preciousstones")) {
			net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones ps = (net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones) PM.getPlugin("PreciousStones");
			Block fblock = p.getWorld().getBlockAt(p.getLocation());
			
			
			Field field = ps.getForceFieldManager().getEnabledSourceField(fblock.getLocation(), FieldFlag.CUBOID);
			//tekkitrestrict.log.info("a");
			if (field != null) {
				//tekkitrestrict.log.info("b");
				boolean allowed = ps.getForceFieldManager().isApplyToAllowed(field, p.getName());
				if (!allowed || field.hasFlag(FieldFlag.APPLY_TO_ALL)) {
					//if (field.getSettings().canGrief(fblock.getTypeId()))
						//return;
					//tekkitrestrict.log.info("c");
					return "preciousstones";
				}
			}
		}

		if (PM.isPluginEnabled("GriefPrevention") && depends.contains("griefprevention")) {
			GriefPrevention pl = (GriefPrevention) PM.getPlugin("GriefPrevention");
			Claim claim = pl.dataStore.getClaimAt(p.getLocation(), false, null);
			if (claim != null) {
				if (!SafeZones.allowNormalUser) {
					if (!claim.isAdminClaim()) return "";
				}
				if (claim.managers.contains("[tekkitrestrict]")) return "griefprevention";
				
				String noAccessReason = claim.allowAccess(p);
				if (noAccessReason != null) {
					return "griefprevention";
				}
			}
		}

		return getXYZSafeZone(p.getLocation(), p.getWorld().getName());
	}

	public static boolean inXYZSafeZone(Location l, String world) {
		if (!tekkitrestrict.config.getBoolean("UseSafeZones")) return false;
		
		return getXYZSafeZone(l, world) != "";
	}

	// main
	public static String getXYZSafeZone(Location l, String world) {
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
		if (!tekkitrestrict.config.getBoolean("UseSafeZones")) return "";
		
		for (int i = 0; i < zones.size(); i++) {
			TRSafeZone a = zones.get(i);
			if (a.mode == 0) {
				// do nothing... (for now)
			} else if (a.mode == 1) { // WorldGuard!
				// determine whether in WG region or not...
				// com.sk89q.worldguard.protection.regions.ProtectedRegion
				Plugin plugin = tekkitrestrict.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
				try {
					// WorldGuard may not be loaded
					if (plugin != null && (plugin instanceof WorldGuardPlugin)) {
						WorldGuardPlugin WGB = (WorldGuardPlugin) plugin;
						ProtectedRegion PR = WGB.getRegionManager(
										tekkitrestrict.getInstance().getServer().getWorld(a.world)).getRegion(a.name);
						if (PR.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ())) {
							return a.name;
						}
					}
				} catch (Exception E) {
					//E.printStackTrace();
				}
			}
		}
		return r;
		
	}

	public static void setFly(PlayerMoveEvent e) {
		if (SSDisableFly) {
			Player player = e.getPlayer();
			if (inSafeZone(player)) {
				// ground player
				TRNoHack.groundPlayer(player);
				player.sendMessage("[TRSafeZone] You may not fly in safezones!");
			}
		}
	}
}
