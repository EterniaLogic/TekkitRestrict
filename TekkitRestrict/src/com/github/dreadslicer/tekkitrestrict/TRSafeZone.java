package com.github.dreadslicer.tekkitrestrict;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
//import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.Field;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache.SafeZones;
import com.github.dreadslicer.tekkitrestrict.api.SafeZones.SafeZoneCreate;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.SSMode;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.SafeZone;
import com.github.dreadslicer.tekkitrestrict.objects.TRPos;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FPerm;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TRSafeZone {
	//public int x1, y1, z1;
	//public int x2, y2, z2;
	public TRPos location;
	public boolean locSet = false;
	public String name;
	public String world;
	protected String data = null;
	private boolean loadedFromSql = false;
	
	public Object pluginRegion = null;
	/**
	 * 0 = NONE<br>
	 * 1 = WorldGuard<br>
	 * 2 = PStones<br>
	 * 3 = Factions<br>
	 * 4 = GriefPrevention
	 */
	public int mode = 0;

	public static List<TRSafeZone> zones = Collections.synchronizedList(new LinkedList<TRSafeZone>());
	private static Plugin worldGuard = null, griefPrevention = null, preciousStones = null;
	//private static HashMap<TRPos, TRSafeZone> regions = new HashMap<TRPos, TRSafeZone>();
	
	private String getData(){
		if (data != null) return data;
		if (!locSet) return "";
		
		this.data = this.location.toString();
		return data;
	}
	
	public static void init() {
		if (SafeZones.UseWG) worldGuard = PM().getPlugin("WorldGuard");
		if (SafeZones.UseGP) griefPrevention = PM().getPlugin("GriefPrevention");
		if (SafeZones.UsePS) preciousStones = PM().getPlugin("PreciousStones");
		
		ResultSet rs = null;
		try {
			rs = tekkitrestrict.db.query("SELECT * FROM `tr_saferegion`;");
			while (rs.next()) {
				TRSafeZone sz = new TRSafeZone();
				sz.name = rs.getString("name");
				sz.world = rs.getString("world");
				World world = Bukkit.getWorld(sz.world);
				if (world == null) continue;
				
				sz.mode = rs.getInt("mode");
				sz.data = rs.getString("data");
				if ("".equals(sz.data)) sz.data = null;
				
				sz.loadedFromSql = true;
				
				if (sz.mode == 4 && sz.data != null){
					if (griefPrevention == null) continue;
					String temp[] = sz.data.split(",");
					sz.location = new TRPos(temp);
					sz.locSet = true;
					sz.pluginRegion = getGPClaim(sz.location.toLoc(world));
				} else if (sz.mode == 1) {
					if (worldGuard == null) continue;
					ProtectedRegion region = getWGRegion(sz.name);
					if (region != null){
						BlockVector loc1 = region.getMinimumPoint();
						BlockVector loc2 = region.getMaximumPoint();
						sz.location = TRPos.parse(loc1, loc2);
						sz.pluginRegion = region;
						/*
						sz.x1 = loc1.getBlockX();
						sz.y1 = loc1.getBlockY();
						sz.z1 = loc1.getBlockZ();
						sz.x2 = loc2.getBlockX();
						sz.y2 = loc2.getBlockY();
						sz.z2 = loc2.getBlockZ();
						*/
						sz.locSet = true;
					} else if (sz.data != null){
						String temp[] = sz.data.split(",");
						sz.location = new TRPos(temp);
						/*
						sz.x1 = Integer.parseInt(temp[0]);
						sz.y1 = Integer.parseInt(temp[1]);
						sz.z1 = Integer.parseInt(temp[2]);
						sz.x2 = Integer.parseInt(temp[3]);
						sz.y2 = Integer.parseInt(temp[4]);
						sz.z2 = Integer.parseInt(temp[5]);
						*/
						sz.locSet = true;
					}
				}
				zones.add(sz);
			}
			rs.close();
		} catch (SQLException e) {
			try {
				if (rs != null) rs.close();
			} catch (SQLException ex) {}
		}
	}
	
	private static ProtectedRegion getWGRegion(String name){
		if (worldGuard == null) return null;
		try {
			WorldGuardPlugin wgPlugin = (WorldGuardPlugin) worldGuard;
			for (World world : Bukkit.getWorlds()){
				ProtectedRegion region = wgPlugin.getRegionManager(world).getRegion(name);
				return region;
			}
		} catch (Exception ex) {
		}
		return null;
	}
	private static ProtectedRegion getWGRegion(String name, Location loc){
		if (worldGuard == null) return null;
		try {
			WorldGuardPlugin wgPlugin = (WorldGuardPlugin) worldGuard;
			
			ProtectedRegion region = wgPlugin.getRegionManager(loc.getWorld()).getRegion(name);
			
			if (region == null) return null;
			if (region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) return region;
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * Gets a GriefPrevention SafeZone at the given location.<br>
	 * Note: Does not actually get SafeZones from the database.
	 */
	@SuppressWarnings("unused")
	private static String getGPSafeZone(Location loc){
		if (griefPrevention == null) return "";
		
		GriefPrevention gpPlugin = (GriefPrevention) griefPrevention;
		Claim claim = gpPlugin.dataStore.getClaimAt(loc, false, null);
		if (claim == null) return ""; //No claim here.
		
		String ownername = claim.getOwnerName();
		if (ownername.equals("")) ownername = "Admin";
		
		//Admin
		if (SafeZones.GPMode == SSMode.Admin){
			if (!claim.isAdminClaim()) return ""; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
			return ownername;
		}
		
		//All
		if (SafeZones.GPMode == SSMode.All){
			return ownername;
		}
		
		//SpecificAdmin
		if (SafeZones.GPMode == SSMode.SpecificAdmin){
			if (!claim.isAdminClaim()) return ""; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
			if (!claim.managers.contains("[tekkitrestrict]")) return ""; //Not SafeZone
			return ownername;
		}
		
		//Specific
		if (SafeZones.GPMode == SSMode.Specific){
			if (!claim.managers.contains("[tekkitrestrict]")) return ""; //Not SafeZone
			return ownername;
		}
		
		//There shouldn't be any more possible cases.
		return ownername;
	}
	
	private static Claim getGPClaim(Location loc){
		if (griefPrevention == null) return null;
		
		GriefPrevention gpPlugin = (GriefPrevention) griefPrevention;
		Claim claim = gpPlugin.dataStore.getClaimAt(loc, false, null);
		if (claim == null) return null; //No claim here.
		
		//Admin
		if (SafeZones.GPMode == SSMode.Admin){
			if (!claim.isAdminClaim()) return null; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
			return claim;
		}
		
		//All
		if (SafeZones.GPMode == SSMode.All){
			return claim;
		}
		
		//SpecificAdmin
		if (SafeZones.GPMode == SSMode.SpecificAdmin){
			if (!claim.isAdminClaim()) return null; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
			if (!claim.managers.contains("[tekkitrestrict]")) return null; //Not SafeZone
			return claim;
		}
		
		//Specific
		if (SafeZones.GPMode == SSMode.Specific){
			if (!claim.managers.contains("[tekkitrestrict]")) return null; //Not SafeZone
			return claim;
		}
		
		//There shouldn't be any more possible cases.
		return claim;
	}
	
	public long getID(){
		long estID = 0;
		// get a numeral from the safezone's name & world
		for (int j = 0; j < name.length(); j++) {
			char c = name.charAt(j);
			estID += Character.getNumericValue(c);
		}
		for (int j = 0; j < world.length(); j++) {
			char c = world.charAt(j);
			estID += Character.getNumericValue(c);
		}
		estID=estID+location.x1+location.x2+location.y1+location.y2+location.z1+location.z2;
		estID=estID+location.x1*location.x2+location.y1*location.y2+location.z1*location.z2;
		//tekkitrestrict.log.info("estID: "+estID);
		return estID;
	}
	
	public static void save() {
		for (int i = 0; i < zones.size(); i++) {
			TRSafeZone z = zones.get(i);
			if (!z.loadedFromSql) {
				// insert the new rows!
				try {
					tekkitrestrict.db.query("INSERT OR REPLACE INTO `tr_saferegion` (`id`,`name`,`mode`,`data`,`world`) VALUES ('"
									+ z.getID() +"','"
									+ TRDB.antisqlinject(z.name)
									+ "'," + z.mode + ",'" + z.getData() + "','"
									+ z.world + "');");
					z.loadedFromSql = true;
				} catch (Exception E) {
				}
			}
		}
	}
	
	private static PluginManager PM(){
		return Bukkit.getPluginManager();
	}
	
	private static void delete(TRSafeZone zone){
		try {
			tekkitrestrict.db.query("DELETE FROM `tr_saferegion` WHERE name='" + zone.name +"');");
		} catch (Exception ex) {
		}
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
			delete(zone);
			return true;
		}
		
		if (zone.mode == 4){
			//IMPORTANT Potential problem when a claim gets resized.
			if (griefPrevention == null) return false;
			if (zone.getData().equals("")) return false;
			World world = Bukkit.getWorld(zone.world);
			if (world == null) return false;
			Location loc;

			if (zone.locSet){
				loc = zone.location.toLoc(world);
			} else {
				String locStr[] = zone.getData().split(",");
				
				try {
					loc = (new TRPos(locStr)).toLoc(world);
				} catch (Exception ex){
					return false;
				}
			}
			
			GriefPrevention gpPlugin = (GriefPrevention) griefPrevention;
			Claim claim = gpPlugin.dataStore.getClaimAt(loc, true, null);
			
			if (claim == null) return true; //Already removed
			claim.managers.remove("[tekkitrestrict]"); //Remove from managers
			zones.remove(zone); //Remove from database
			delete(zone);
			return true;
		}
		
		return false;
	}
	
	public static SafeZoneCreate addSafeZone(Player player, String pluginName, String name){
		if (!SafeZones.UseSafeZones) return SafeZoneCreate.SafeZonesDisabled;
		
		name = name.toLowerCase();
		
		for (TRSafeZone current : TRSafeZone.zones){
			if (current.name.toLowerCase().equals(name)){
				return SafeZoneCreate.AlreadyExists;
			}
		}
		
		pluginName = pluginName.toLowerCase();
		
		if (SafeZones.UseGP && pluginName.equals("griefprevention") && griefPrevention != null) {
			GriefPrevention gpPlugin = (GriefPrevention) griefPrevention;
			Claim claim = gpPlugin.dataStore.getClaimAt(player.getLocation(), false, null);
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
			zone.location = new TRPos(loc1, loc2);
			zone.pluginRegion = claim;
			zone.locSet = true;
			
			zone.name = name;
			zone.world = loc1.getWorld().getName();
			TRSafeZone.zones.add(zone);
			TRSafeZone.save();
			return SafeZoneCreate.Success;
			
		} else if (SafeZones.UseWG && pluginName.equals("worldguard") && worldGuard != null) {
			try {
				WorldGuardPlugin wgPlugin = (WorldGuardPlugin) worldGuard;
				Map<String, ProtectedRegion> rm = wgPlugin.getRegionManager(player.getWorld()).getRegions();
				ProtectedRegion pr = rm.get(name);
				if (pr == null) {
					return SafeZoneCreate.RegionNotFound;
				}
				
				BlockVector loc1 = pr.getMinimumPoint();
				BlockVector loc2 = pr.getMaximumPoint();
				
				TRSafeZone zone = new TRSafeZone();
				zone.mode = 1;
				zone.location = TRPos.parse(loc1, loc2);
				zone.pluginRegion = pr;
				zone.locSet = true;

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
	private static boolean allowedToMakeGPSafeZone(Player player, Claim claim){
		if (SafeZones.GPMode.isAdmin()){
			if (!claim.isAdminClaim()) return false; //Only admin claims can be made safezones.
			return player.hasPermission("griefprevention.adminclaims"); //Only admins can make admin claims safezones.
		}
		
		if (SafeZones.GPMode == SSMode.All) return false; //All claims are safezones, so specific adding is not allowed.
		
		String name = player.getName().toLowerCase();
		
		if (claim.ownerName.equalsIgnoreCase(name)) return true; //Owner of claim is allowed
		
		Iterator<String> managers = claim.managers.iterator();
		while (managers.hasNext()){
			if (managers.next().equalsIgnoreCase(name)) return true; //Manager of claim is allowed
		}
		
		return false; //Otherwise it's not allowed.
	}
	
	private static TRSafeZone lastZone;
	
	/**
	 * <b>Uses the database for information.</b>
	 * @return A string with information about the type of safezone and its name/owner.<br>
	 * Returns "" if there is none.
	 */
	public static String getSafeZoneByLocation(Location loc, boolean doGP) {
		if (!SafeZones.UseSafeZones) return "";
		if (lastZone != null){
			if (lastZone.mode == 1){
				if (lastZone.location.contains(loc)){
					if (lastZone.pluginRegion != null) return "WorldGuard Safezone Region: " + lastZone.name;
					else return "TekkitRestrict SafeZone Region: " + lastZone.name;
				}
			} else if (lastZone.mode == 4){
				if (lastZone.location.containsIgnoreY(loc)){
					if (lastZone.pluginRegion != null){
						Claim c = (Claim) lastZone.pluginRegion;
						String r = c.ownerName;
						if (r == null || r.equals("")) r = "Admin";
						return "GriefPrevention Safezone Claim owned by: " + r;
					} else {
						return "GriefPrevention Safezone Claim: " + lastZone.name;
					}
				}
			}
		}
		
		boolean WGEnabled = (worldGuard != null), GPEnabled = (griefPrevention != null);
		String r = "";

		Iterator<TRSafeZone> zonesIterator = zones.iterator();
		while (zonesIterator.hasNext()) {
			TRSafeZone a = zonesIterator.next();
			if (a.mode == 0){
				// do nothing... (for now)
				continue;
			}
			
			if (a.mode == 1){ //WorldGuard
				if (!WGEnabled) continue;
				if (a.locSet){
					if (a.location.contains(loc)){
						lastZone = a;
						if (a.pluginRegion != null) return "WorldGuard Safezone Region: " + a.name;
						else return "TekkitRestrict SafeZone Region: " + a.name;
					}
				} else {
					if (getWGRegion(a.name, loc) != null){
						lastZone = a;
						return "WorldGuard Safezone Region: " + a.name;
					}
				}
				continue;
			}
			
			//TODO PS support
			
			if (a.mode == 4){ //GriefPrevention
				if (!doGP || !GPEnabled) continue;
				
				if (a.locSet){
					if (!a.location.containsIgnoreY(loc)) continue;
					lastZone = a;
					if (a.pluginRegion != null){
						Claim c = (Claim) a.pluginRegion;
						r = c.ownerName;
						if (r == null || r.equals("")) r = "Admin";
						return "GriefPrevention Safezone Claim owned by: " + r;
					} else {
						return "GriefPrevention Safezone Claim: " + a.name;
					}
				} else {
					String temp[] = a.getData().split(",");
					if (temp.length != 6) continue;
					try {
						a.location = new TRPos(temp);
						a.locSet = true;
						if (!(a.location.containsIgnoreY(loc))) continue;
						lastZone = a;
						if (a.pluginRegion != null){
							Claim c = (Claim) a.pluginRegion;
							r = c.ownerName;
							if (r == null || r.equals("")) r = "Admin";
							return "GriefPrevention Safezone Claim owned by: " + r;
						} else {
							return "GriefPrevention Safezone Claim: " + a.name;
						}
					} catch (Exception ex){
						continue;
					}
				}
			}
		}
		return "";
		
	}
	
	public static boolean isSafeZoneFor(Player player, boolean strict, boolean doBypassCheck){
		if (!SafeZones.UseSafeZones) return false;
		if (doBypassCheck && player.hasPermission("tekkitrestrict.bypass.safezone")) return false;
		
		if (GP.isSafeZoneFor(player)) return true;
		if (WG.isSafeZoneFor(player)) return true;
		
		if (Towny.isSafeZoneFor(player, strict)) return true;
		if (Factions.isSafeZoneFor(player, strict)) return true;
		if (PS.isSafeZoneFor(player, strict)) return true;
		
		return false;
	}
	

	/** Only used by /tr admin safezone check */
	public static Object[] getSafeZoneStatusFor(Player player){
		Object[] obj = new Object[3];
		obj[0] = "GriefPrevention";
		SafeZone status = GP.getSafeZoneStatusFor(player);
		obj[1] = GP.lastGP;
		
		if (status == SafeZone.isNone || status == SafeZone.pluginDisabled){
			obj[0] = "WorldGuard";
			status = WG.getSafeZoneStatusFor(player);
			obj[1] = WG.lastWG;
		}
		if (status == SafeZone.isNone || status == SafeZone.pluginDisabled){
			obj[0] = "Towny";
			status = Towny.getSafeZoneStatusFor(player);
			obj[1] = Towny.lastTown;
		}
		if (status == SafeZone.isNone || status == SafeZone.pluginDisabled){
			obj[0] = "Factions";
			status = Factions.getSafeZoneStatusFor(player);
			obj[1] = Factions.lastFaction;
		}
		if (status == SafeZone.isNone || status == SafeZone.pluginDisabled){
			obj[0] = "PreciousStones";
			status = PS.getSafeZoneStatusFor(player);
			obj[1] = PS.lastPS;
		}
		obj[2] = status;
		return obj;
	}
	
	public static class WG {
		public static String lastWG = "";
		public static SafeZone getSafeZoneStatusFor(Player player){
			if (!SafeZones.UseSafeZones || worldGuard == null) return SafeZone.pluginDisabled;
			WorldGuardPlugin wgPlugin = (WorldGuardPlugin) worldGuard;
			if (SafeZones.WGMode == SSMode.All) {
				if (wgPlugin.canBuild(player, player.getLocation())){
					return SafeZone.isAllowedStrict;
				}
				
				return SafeZone.isDisallowed;
			} else {
				Location loc = player.getLocation();
				World world = loc.getWorld();
				
				Iterator<TRSafeZone> zonesIterator = zones.iterator();
				while (zonesIterator.hasNext()) {
					TRSafeZone a = zonesIterator.next();
					if (a.mode == 1){ //WorldGuard
						ProtectedRegion PR = wgPlugin.getRegionManager(world).getRegion(a.name);
						
						if (PR == null) continue;
						if (!PR.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) continue;

						lastWG = a.name;
						if (wgPlugin.canBuild(player, loc)) return SafeZone.isAllowedStrict;
						
						return SafeZone.isDisallowed;
					}
				}
				
				return SafeZone.isNone;
			}
		}
		
		public static boolean isSafeZoneFor(Player player){
			if (worldGuard == null) return false;
			try {
				WorldGuardPlugin wgPlugin = (WorldGuardPlugin) worldGuard;
				Location loc = player.getLocation();
				if (SafeZones.WGMode == SSMode.All){
					return !wgPlugin.canBuild(player, loc);
				} else {
					Iterator<TRSafeZone> zonesIterator = zones.iterator();
					while (zonesIterator.hasNext()) {
						TRSafeZone a = zonesIterator.next();
						if (a.mode != 1) continue;
						
						ProtectedRegion region = wgPlugin.getRegionManager(loc.getWorld()).getRegion(a.name);
						
						if (region == null) continue;
						if (!region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) continue;
						
						if (wgPlugin.canBuild(player, loc)) return false;
						return true;
					}
				}
			} catch (Exception ex){}
			return false;
		}
	}
	
	public static class GP {
		public static String lastGP = "";
		
		public static SafeZone getSafeZoneStatusFor(Player player){
			if (!SafeZones.UseSafeZones || griefPrevention == null) return SafeZone.pluginDisabled;
			
			GriefPrevention gpPlugin = (GriefPrevention) griefPrevention;
			Location loc = player.getLocation();
			
			Claim claim = gpPlugin.dataStore.getClaimAt(loc, false, null);
			if (claim == null) return SafeZone.isNone;
			lastGP = (claim.ownerName == "" ? "Admin" : claim.ownerName);
			
			String name = player.getName().toLowerCase();
			
			if (claim.ownerName.equalsIgnoreCase(name)) return SafeZone.isAllowedStrict;
			
			//Admin
			if (SafeZones.GPMode == SSMode.Admin){
				if (!claim.isAdminClaim()) return SafeZone.isNone; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
				if (player.hasPermission("griefprevention.adminclaims")){
					return SafeZone.hasBypass; //If the player is a GPAdmin, return HasBypass.
				}
				
				return SafeZone.isDisallowed;
			}
			
			if (SafeZones.GPMode == SSMode.All){
				Iterator<String> managers = claim.managers.iterator();
				while (managers.hasNext()){
					if (managers.next().equalsIgnoreCase(name)) return SafeZone.isAllowedStrict; //If manager of claim, return isAllowedStrict.
				}
				
				return SafeZone.isDisallowed;
			}
			
			//SpecificAdmin
			if (SafeZones.GPMode == SSMode.SpecificAdmin){
				if (!claim.isAdminClaim()) return SafeZone.isNone; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode.
				if (player.hasPermission("griefprevention.adminclaims")){
					return SafeZone.hasBypass; //If the player is a GPAdmin, return HasBypass.
				}
				
				if (!claim.managers.contains("[tekkitrestrict]")) return SafeZone.isNone; //If it doesn't contain [tekkitrestrict], then it cannot be one.
				Iterator<String> managers = claim.managers.iterator();
				while (managers.hasNext()){
					if (managers.next().equalsIgnoreCase(name)) return SafeZone.isAllowedStrict; //If manager of claim, return isAllowedStrict.
				}
				
				//Check for database to see if it is really a legit claim.
				
				Iterator<TRSafeZone> zonesIterator = zones.iterator();
				while (zonesIterator.hasNext()) {
					TRSafeZone a = zonesIterator.next();
					if (a.mode == 4){ //GriefPrevention
						World world = Bukkit.getWorld(a.world);
						if (world == null) world = player.getWorld();
						
						if (a.locSet){
							if (!a.location.containsIgnoreY(loc)) continue;
							return SafeZone.isDisallowed;
						} else {
							String temp[] = a.getData().split(",");
							if (temp.length != 6) continue;
							
							try {
								a.location = new TRPos(temp);
								a.locSet = true;
								if (!a.location.containsIgnoreY(loc)) continue;
								return SafeZone.isDisallowed;
							} catch (Exception ex){
								continue;
							}
						}
					}
				}
				
				return SafeZone.isAllowedNonStrict;
			}
			
			if (SafeZones.GPMode == SSMode.Specific){
				if (!claim.managers.contains("[tekkitrestrict]")) return SafeZone.isNone; //If it doesn't contain [tekkitrestrict], then it cannot be one.
				Iterator<String> managers = claim.managers.iterator();
				while (managers.hasNext()){
					if (managers.next().equalsIgnoreCase(name)) return SafeZone.isAllowedStrict; //If manager of claim, return isAllowedStrict.
				}
				
				//Check for database to see if it is really a legit claim.
				
				Iterator<TRSafeZone> zonesIterator = zones.iterator();
				while (zonesIterator.hasNext()) {
					TRSafeZone a = zonesIterator.next();
					if (a.mode == 4){ //GriefPrevention
						
						World world = Bukkit.getWorld(a.world);
						if (world == null) world = player.getWorld();
						
						if (a.locSet){
							if (!a.location.containsIgnoreY(loc)) continue;
							return SafeZone.isDisallowed;
						} else {
							String temp[] = a.getData().split(",");
							if (temp.length != 6) continue;
							try {
								a.location = new TRPos(temp);
								a.locSet = true;
								if (!a.location.containsIgnoreY(loc)) continue;
								return SafeZone.isDisallowed;
							} catch (Exception ex){
								continue;
							}
						}
					}
				}
				
				return SafeZone.isAllowedNonStrict;
			}
			
			return SafeZone.isDisallowed;
		}
		
		public static boolean isSafeZoneFor(Player player){
			if (!SafeZones.UseGP || griefPrevention == null) return false; //If plugin disabled or not used for SafeZones, return false. (allowed)
			
			Location loc = player.getLocation();
			
			GriefPrevention pl = (GriefPrevention) griefPrevention;
			Claim claim = pl.dataStore.getClaimAt(loc, false, null);
			if (claim == null) return false; //If no claim here, return false. (allowed)
			
			String name = player.getName().toLowerCase();
			if (claim.ownerName.equalsIgnoreCase(name)) return false; //If owner of claim, return false. (allowed)
			
			if (SafeZones.GPMode == SSMode.Admin){ //Admin
				if (!claim.isAdminClaim()) return false; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode: return false. (allowed)
				return !player.hasPermission("griefprevention.adminclaims"); //If the player is a GPAdmin, return false. (allowed) Else return true. (not allowed)
			}
			
			if (SafeZones.GPMode == SSMode.All){ //All
				Iterator<String> managers = claim.managers.iterator();
				while (managers.hasNext()){
					if (managers.next().equalsIgnoreCase(name)) return false; //If manager of claim, return false. (allowed)
				}
				return true;
			}
			
			if (SafeZones.GPMode == SSMode.SpecificAdmin){ //SpecificAdmin (first part)
				if (!claim.isAdminClaim()) return false; //Not an admin claim, so it cannot be a SafeZone in ADMIN mode: return false. (allowed)
				if (player.hasPermission("griefprevention.adminclaims")) return false; //If the player is a GPAdmin, return false. (allowed)
				
				if (!claim.managers.contains("[tekkitrestrict]")) return false; //If it doesn't contain [tekkitrestrict], then it returns false. (allowed)
				Iterator<String> managers = claim.managers.iterator();
				while (managers.hasNext()){
					if (managers.next().equalsIgnoreCase(name)) return false; //If manager of claim, return false. (allowed)
				}
				
				Iterator<TRSafeZone> zonesIterator = zones.iterator();
				while (zonesIterator.hasNext()) {
					TRSafeZone a = zonesIterator.next();
					if (a.mode == 4){ //GriefPrevention
						World world = Bukkit.getWorld(a.world);
						if (world == null) world = player.getWorld();
						
						if (a.locSet){
							if (!a.location.containsIgnoreY(loc)) continue;
							return true;
						} else {
							String temp[] = a.getData().split(",");
							if (temp.length != 6) continue;
							try {
								a.location = new TRPos(temp);
								a.locSet = true;
								if (!a.location.containsIgnoreY(loc)) continue;
								return true;
							} catch (Exception ex){
								continue;
							}
						}
						//Not allowed because it has been set as a specific claim in the database.
					}
				}
				
				return false; //Allowed because it isn't a specific one.
			}
			
			if (SafeZones.GPMode == SSMode.Specific){ //Specific and SpecificAdmin (second part)
				if (!claim.managers.contains("[tekkitrestrict]")) return false; //If it doesn't contain [tekkitrestrict], then it returns false. (allowed)
				Iterator<String> managers = claim.managers.iterator();
				while (managers.hasNext()){
					if (managers.next().equalsIgnoreCase(name)) return false; //If manager of claim, return false. (allowed)
				}
				
				Iterator<TRSafeZone> zonesIterator = zones.iterator();
				while (zonesIterator.hasNext()) {
					TRSafeZone a = zonesIterator.next();
					if (a.mode == 4){ //GriefPrevention
						World world = Bukkit.getWorld(a.world);
						if (world == null) world = player.getWorld();
						
						if (a.locSet){
							if (!a.location.containsIgnoreY(loc)) continue;
							return true;
						} else {
							String temp[] = a.getData().split(",");
							if (temp.length != 6) continue;
							try {
								a.location = new TRPos(temp);
								a.locSet = true;
								if (!a.location.containsIgnoreY(loc)) continue;
								return true;
							} catch (Exception ex){
								continue;
							}
						}
						//Not allowed because it has been set as a specific claim in the database.
					}
				}
				
				return false; //Allowed because it isn't a specific one.
			}
			
			return false; //Unknown mode
		}
	}
	
	public static class PS {
		public static String lastPS = "";
		public static SafeZone getSafeZoneStatusFor(Player player){
			if (!SafeZones.UseSafeZones || preciousStones == null) return SafeZone.pluginDisabled;
			
			Location loc = player.getLocation();
			
			PreciousStones ps = (PreciousStones) preciousStones;
			Block fblock = loc.getWorld().getBlockAt(loc);
			
			Field field = ps.getForceFieldManager().getEnabledSourceField(fblock.getLocation(), FieldFlag.CUBOID);
			if (field == null) return SafeZone.isNone;
			lastPS = field.getName();
			
			String name = player.getName();
			if(ps.getForceFieldManager().isApplyToAllowed(field, name)){
				return SafeZone.isAllowedStrict;
			} else if (field.hasFlag(FieldFlag.APPLY_TO_ALL)) {
				return SafeZone.isDisallowed;
			} else if(ps.getForceFieldManager().isAllowed(field, name)){
				return SafeZone.isAllowedNonStrict;
			} else {
				return SafeZone.isDisallowed;
			}
		}
		
		public static boolean isSafeZoneFor(Player player, boolean strict){
			if (!SafeZones.UseSafeZones || preciousStones == null) return false;
			
			Location loc = player.getLocation();
			
			PreciousStones ps = (PreciousStones) preciousStones;
			Block fblock = loc.getWorld().getBlockAt(loc);
			
			Field field = ps.getForceFieldManager().getEnabledSourceField(fblock.getLocation(), FieldFlag.CUBOID);
			if (field == null) return false;
			
			String name = player.getName();
			
			if(ps.getForceFieldManager().isApplyToAllowed(field, name)){
				return false;
			} else if (strict){ //If cannot build here, stop further checks as it is strict
				return true;
			}
			
			if (field.hasFlag(FieldFlag.APPLY_TO_ALL)) {
				return true;
			} else if(ps.getForceFieldManager().isAllowed(field, name)){
				return false;
			}
			
			return true;
			
		}
	}
	
	public static class Factions {
		public static String lastFaction = "";
		public static SafeZone getSafeZoneStatusFor(Player player){
			if (!SafeZones.UseSafeZones || !SafeZones.UseFactions || !PM().isPluginEnabled("Factions")) return SafeZone.pluginDisabled;
			String name = player.getName();
			
			FPlayer fplayer = FPlayers.i.get(name);
			if (Conf.playersWhoBypassAllProtection.contains(name)) return SafeZone.hasBypass;
			
			Location loc = player.getLocation();
			
			FLocation ccc = new FLocation(loc);
			Faction faction = Board.getFactionAt(ccc);
			
			if (faction == null) return SafeZone.isNone;
			lastFaction = faction.getTag();
			
			if (FPerm.BUILD.has(fplayer, ccc)){
				return SafeZone.isAllowedStrict;
			} else if (faction.getFPlayers().contains(fplayer)){
				return SafeZone.isAllowedNonStrict;
			} else {
				return SafeZone.isDisallowed;
			}
		}
		
		public static boolean isSafeZoneFor(Player player, boolean strict){
			if (!SafeZones.UseSafeZones || !SafeZones.UseFactions || !PM().isPluginEnabled("Factions")) return false;
			String name = player.getName();
			
			FPlayer fplayer = FPlayers.i.get(name);
			if (Conf.playersWhoBypassAllProtection.contains(name)) return false;
			
			Location loc = player.getLocation();
			
			FLocation ccc = new FLocation(loc);
			Faction faction = Board.getFactionAt(ccc);
			
			if (faction == null) return false;
			
			if (FPerm.BUILD.has(fplayer, ccc)){
				return false;
			} else if (strict) { //If cannot build there, stop further checks as it is strict.
				return true;
			}
			
			if (faction.getFPlayers().contains(fplayer)){
				return false;
			} else {
				return true;
			}
		}
	}
	
	public static class Towny {
		public static String lastTown = "";
		public static SafeZone getSafeZoneStatusFor(Player player){
			if (!SafeZones.UseSafeZones || !SafeZones.UseTowny || !PM().isPluginEnabled("Towny")) return SafeZone.pluginDisabled;
			Location loc = player.getLocation();
			
			Block cb = loc.getWorld().getHighestBlockAt(loc);
			if (PlayerCacheUtil.getCachePermission(player, loc, cb.getTypeId(), (byte) 0, TownyPermission.ActionType.DESTROY)){
				return SafeZone.isAllowedStrict;
			}
			
			Town town = null;
			TownBlock tb = TownyUniverse.getTownBlock(loc);
			try {
				town = tb.getTown();
			} catch (Exception e) {
				return SafeZone.isNone;
			}
			
			if (town == null) return SafeZone.isNone;
			lastTown = town.getName();
			
			String name = player.getName();
			
			for (Resident resident : town.getResidents()){
				if (resident.getName().equalsIgnoreCase(name)) return SafeZone.isAllowedNonStrict;
			}
			
			return SafeZone.isDisallowed;
		}
		
		public static boolean isSafeZoneFor(Player player, boolean strict){
			if (!SafeZones.UseSafeZones || !SafeZones.UseTowny || !PM().isPluginEnabled("Towny")) return false;
			Location loc = player.getLocation();
			
			Block cb = loc.getWorld().getHighestBlockAt(loc);
			if (PlayerCacheUtil.getCachePermission(player, loc, cb.getTypeId(), (byte) 0, TownyPermission.ActionType.DESTROY)){
				return false;
			} else if (strict){ //If cannot break here, then stop as it is strict.
				return true;
			}
			
			Town town = null;
			TownBlock tb = TownyUniverse.getTownBlock(loc);
			try {
				town = tb.getTown();
			} catch (Exception e) {
				return false;
			}
			
			if (town == null) return false;
			
			String name = player.getName();
			
			for (Resident resident : town.getResidents()){
				if (resident.getName().equalsIgnoreCase(name)) return false;
			}
			
			return true;
		}
	}
}
