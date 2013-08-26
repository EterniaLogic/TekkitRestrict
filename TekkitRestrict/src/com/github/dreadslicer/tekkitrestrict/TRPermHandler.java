package com.github.dreadslicer.tekkitrestrict;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;

public class TRPermHandler {

	public TRPermHandler() {}
	private static RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> v;
	
	public static boolean hasPermission(Player p, String type) {
		try {
			String perm = "tekkitrestrict." + type;

			PluginManager pm = Bukkit.getPluginManager();
			if(pm.isPluginEnabled("Vault")){
				if(v == null) v = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		        if (v != null) {
		        	net.milkbowl.vault.permission.Permission permission = v.getProvider();
		        	return permission.has(p, perm);
		        }
			} else if (pm.isPluginEnabled("PermissionsEx")) {
				return ((PermissionManager) tekkitrestrict.perm).getUser(p).has(perm);
			} else if (pm.isPluginEnabled("bPermissions")) {
				return ApiLayer.hasPermission(p.getWorld().getName(), CalculableType.USER, p.getName(), perm);
			} else if (pm.isPluginEnabled("GroupManager")) {
				GroupManager ps = (GroupManager) pm.getPlugin("GroupManager");
				return ps.getWorldsHolder().getWorldData(p).getUser(p.getName()).hasSamePermissionNode(perm);
			} else {
				return p.hasPermission(perm);
			}
		} catch (Exception ex) {
			tekkitrestrict.log.warning("An error occured in the PermHandler(0)! Please inform the author.");
			Log.Exception(ex, false);
		}
		return false;
	}
	
	public static boolean hasPermission(Player p, String type, String node1) {
		try {
			String n1 = node1.equals("") ? "" : "." + node1;
			String perm = "tekkitrestrict." + type + n1;

			PluginManager pm = Bukkit.getPluginManager();
			if(pm.isPluginEnabled("Vault")){
				if(v == null) v = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		        if (v != null) {
		        	net.milkbowl.vault.permission.Permission permission = v.getProvider();
		        	return permission.has(p, perm);
		        }
			} else if (pm.isPluginEnabled("PermissionsEx")) {
				return ((PermissionManager) tekkitrestrict.perm).getUser(p).has(perm);
			} else if (pm.isPluginEnabled("bPermissions")) {
				return ApiLayer.hasPermission(p.getWorld().getName(), CalculableType.USER, p.getName(), perm);
			} else if (pm.isPluginEnabled("GroupManager")) {
				GroupManager ps = (GroupManager) pm.getPlugin("GroupManager");
				return ps.getWorldsHolder().getWorldData(p).getUser(p.getName()).hasSamePermissionNode(perm);
			} else {
				return p.hasPermission(perm);
			}
		} catch (Exception ex) {
			tekkitrestrict.log.warning("An error occured in the PermHandler(1)! Please inform the author.");
			Log.Exception(ex, false);
		}
		return false;
	}
	
	/**
	 * Only use this for 2 nodes!
	 */
	public static boolean hasPermission(Player p, String type, String node1, String node2) {
		try {
			String n1 = node1.equals("") ? "" : "." + node1;
			String n2 = node2.equals("") ? "" : "." + node2;
			String perm = "tekkitrestrict." + type + n1 + n2;

			PluginManager pm = Bukkit.getPluginManager();
			if(pm.isPluginEnabled("Vault")){
				if(v == null) v = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		        if (v != null) {
		        	net.milkbowl.vault.permission.Permission permission = v.getProvider();
		        	return permission.has(p, perm);
		        }
			} else if (pm.isPluginEnabled("PermissionsEx")) {
				return ((PermissionManager) tekkitrestrict.perm).getUser(p).has(perm);
			} else if (pm.isPluginEnabled("bPermissions")) {
				return ApiLayer.hasPermission(p.getWorld().getName(), CalculableType.USER, p.getName(), perm);
			} else if (pm.isPluginEnabled("GroupManager")) {
				GroupManager ps = (GroupManager) pm.getPlugin("GroupManager");
				return ps.getWorldsHolder().getWorldData(p).getUser(p.getName()).hasSamePermissionNode(perm);
			} else {
				return p.hasPermission(perm);
			}
		} catch (Exception ex) {
			tekkitrestrict.log.warning("An error occured in the PermHandler(2)! Please inform the author.");
			Log.Exception(ex, false);
		}
		return false;
	}

	public static int getPermNumeral(Player p, String permBase, int id, int data) {
		int r = -1;
		
		String negPerms[] = getPermissions(p, "-"+permBase);
		for (int i = 0; i < negPerms.length; i++) {
			String gp[] = negPerms[i].replace('.', ';').split(";");//tekkitrestrict;limiter;id
			String gs[] = permBase.replace('.', ';').split(";");//tekkitrestrict;limiter
			if (gp.length < 2 || gs.length < 2) continue;
			if (gp[1] == null || gs[1] == null) continue;
			if (!gp[1].equals(gs[1])) continue;
			try {
				if (gp.length != 5){
					if (TRNoItem.isInRanged(gp[2]+":-10", id, data)) return -1;
				} else {
					if (TRNoItem.isInRanged(gp[2]+":"+gp[3], id, data)) return -1;
				}
			} catch (Exception ex){}
		}

		String perms[] = getPermissions(p, permBase);
		for (int i = 0; i < perms.length; i++) {
			String gp[] = perms[i].replace('.', ';').split(";");//tekkitrestrict;limiter;id
			String gs[] = permBase.replace('.', ';').split(";");//tekkitrestrict;limiter
			if (gp.length < 2 || gs.length < 2) continue;
			if (gp[1] == null || gs[1] == null) continue;
			if (!gp[1].equals(gs[1])) continue;
			
			try {
				if (gp.length != 5){
					if (TRNoItem.isInRanged(gp[2], id, data)) return Integer.parseInt(gp[3]);
				} else {
					if (TRNoItem.isInRanged(gp[2]+":"+gp[3], id, data)) return Integer.parseInt(gp[4]);
				}
			} catch (Exception ex){}
		}

		return r;
	}

	private static String[] getPermissions(Player p, String s) {
		PluginManager pm = Bukkit.getPluginManager();
		if (pm.isPluginEnabled("PermissionsEx")) {
			return getAllPEXPlayerPerms(p, s);
		}
		
		if (pm.isPluginEnabled("PermissionsBukkit")) {
			return new String[0];
		}
		
		if (pm.isPluginEnabled("bPermissions")) {
			Permission ps[] = ApiLayer.getPermissions(p.getWorld().getName(), CalculableType.USER, p.getName());
			LinkedList<String> sr = new LinkedList<String>();
			Permission apermission[];
			int k = (apermission = ps).length;
			for (int j = 0; j < k; j++) {
				Permission px = apermission[j];
				sr.add(px.nameLowerCase());
			}

			for (int i = 0; i < sr.size(); i++) {
				if (!sr.get(i).startsWith(s)) {
					sr.remove(i);
					i--;
				}
			}

			String lz[] = sr.toArray(new String[0]);
			//sr.clear();
			return lz;
		}
		if (pm.isPluginEnabled("GroupManager")) {
			GroupManager ps = (GroupManager) pm.getPlugin("GroupManager");
			HashSet<String> sr = new HashSet<String>();
			User user = ps.getWorldsHolder().getWorldData(p).getUser(p.getName());
			sr.addAll(user.getPermissionList());
			for (Group group : user.subGroupListCopy()){
				sr.addAll(group.getPermissionList());
			}
			sr.addAll(user.getGroup().getPermissionList());
			//String a;
			for (String inherit: user.getGroup().getInherits()){
				Group gi = ps.getWorldsHolder().getWorldData(p).getGroup(inherit);
				if (gi == null) continue;
				
				sr.addAll(gi.getPermissionList());
			}
			
			//for (Iterator<String> iterator = user.getGroup().getInherits().iterator(); iterator.hasNext(); sr.add(a)) {
			//	a = iterator.next();
			//}
			Iterator<String> it = sr.iterator();
			while (it.hasNext()){
				String str = it.next();
				if (!str.startsWith(s)) {
					it.remove();
				}
			}

			String lz[] = sr.toArray(new String[0]);
			return lz;
		} else {
			return new String[0];
		}
	}

	private static String[] getAllPEXPlayerPerms(Player p, String sss) {
		LinkedList<String> l = new LinkedList<String>();
		PermissionUser pu = ((PermissionManager) tekkitrestrict.perm).getUser(p);
		String as[];
		int j = (as = pu.getPermissions(p.getWorld().getName())).length;
		for (int i = 0; i < j; i++) {
			String sg = as[i];
			if (sg.startsWith(sss)) {
				l.add(sg);
			}
		}

		Map<String, String[]> sssrr = pu.getAllPermissions();
		for (Iterator<String> iterator = sssrr.keySet().iterator(); iterator.hasNext();) {
			String ssworld = iterator.next();
			if (ssworld == null || ssworld.equals("null")) {
				String as1[];
				int k1 = (as1 = sssrr.get(ssworld)).length;
				for (int j1 = 0; j1 < k1; j1++) {
					String value = as1[j1];
					if (value.startsWith(sss)) {
						l.add(value);
					}
				}

			}
		}

		PermissionGroup apermissiongroup[];
		int i1 = (apermissiongroup = pu.getGroups()).length;
		for (int k = 0; k < i1; k++) {
			PermissionGroup P1 = apermissiongroup[k];
			// l.addAll(LoopPEXGroups(p.getWorld().getName(), P1));
			{
				String as2[];
				int j2 = (as2 = P1.getPermissions(p.getWorld().getName())).length;
				for (int l1 = 0; l1 < j2; l1++) {
					String sr = as2[l1];
					if (sr.startsWith(sss)) {
						l.add(sr);
					}
				}

				j2 = (as2 = P1.getPermissions(null)).length;
				for (int i2 = 0; i2 < j2; i2++) {
					String sr = as2[i2];
					if (sr.startsWith(sss)) {
						l.add(sr);
					}
				}
			}

			PermissionGroup pgr[] = P1.getChildGroups();
			for (int i = 0; i < pgr.length; i++) {
				String as2[];
				int j2 = (as2 = P1.getPermissions(p.getWorld().getName())).length;
				for (int l1 = 0; l1 < j2; l1++) {
					String sr = as2[l1];
					if (sr.startsWith(sss)) {
						l.add(sr);
					}
				}

				j2 = (as2 = P1.getPermissions(null)).length;
				for (int i2 = 0; i2 < j2; i2++) {
					String sr = as2[i2];
					if (sr.startsWith(sss)) {
						l.add(sr);
					}
				}
			}
		}

		String lsst[] = l.toArray(new String[0]);
		//l.clear();
		return lsst;
	}

}
