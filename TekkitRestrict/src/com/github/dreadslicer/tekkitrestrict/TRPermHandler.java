/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   TRPermHandler.java

package com.github.dreadslicer.tekkitrestrict;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.Vault;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;

@SuppressWarnings("rawtypes")
public class TRPermHandler {

	public TRPermHandler() {
	}
	private static RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> v;
	public static boolean hasPermission(Player p, String type, String node1,
			String node2) {
		try {
			String n1 = node1 != "" ? "." + node1 : "";
			String n2 = node2 != "" ? "." + node2 : "";
			String perm = "tekkitrestrict." + type + n1 + n2;

			if(tekkitrestrict.pm.isPluginEnabled("Vault")){
				if(v == null) v = tekkitrestrict.getInstance().getServer()
						.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		        if (v != null) {
		        	net.milkbowl.vault.permission.Permission permission = v.getProvider();
		        	return permission.has(p, perm);
		        }
			} else if (perm != null
					&& tekkitrestrict.pm.isPluginEnabled("PermissionsEx")
					&& isForceThis(0)) {
				return ((PermissionManager) tekkitrestrict.perm).getUser(p)
						.has(perm);
			} else if (tekkitrestrict.pm.isPluginEnabled("bPermissions")
					&& isForceThis(2)) {
				return ApiLayer.hasPermission(p.getWorld().getName(),
						CalculableType.USER, p.getName(), perm);
			} else if (!tekkitrestrict.pm.isPluginEnabled("GroupManager")
					|| !isForceThis(3)) {
				GroupManager ps;
				ps = (GroupManager) tekkitrestrict.getInstance().getServer()
						.getPluginManager().getPlugin("GroupManager");
				return ps.getWorldsHolder().getWorldData(p)
						.getUser(p.getName()).hasSamePermissionNode(perm);
			} else {
				return p.hasPermission(perm);
			}
		} catch (Exception e) {
			TRLogger.Log("debug",
					(new StringBuilder("Error: [PermHandle_hasPermission] "))
							.append(e.getMessage()).toString());
		}
		return false;
	}

	/*
	 * public static boolean hassSpecialPermission(Player p, String s, int id,
	 * int data) { boolean r = false;
	 * 
	 * /*String perms[] = getPermissions(p, s); String negPerms[] =
	 * getPermissions(p, (new StringBuilder("-")) .append(s).toString()); for
	 * (int i = 0; i < negPerms.length; i++) { String gp[] =
	 * negPerms[i].replace('.', ';').split(";"); String gs[] = s.replace('.',
	 * ';').split(";"); if (gp.length >= 2 && gs.length >= 2 && gp[1] != null &&
	 * gs[1] != null && gp[1].equals(gs[1]) && TRNoItem.isInRanged(gp.length !=
	 * 4 ? gp[2] : (new StringBuilder(String.valueOf(gp[2])))
	 * .append(":").append(gp[3]).toString(), id, data)) return false; }
	 * 
	 * for (int i = 0; i < perms.length; i++) { String gp[] =
	 * perms[i].replace('.', ';').split(";"); String gs[] = s.replace('.',
	 * ';').split(";"); if (gp.length >= 2 && gs.length >= 2 && gp[1] != null &&
	 * gs[1] != null && gp[1].equals(gs[1]) && TRNoItem.isInRanged(gp.length !=
	 * 4 ? gp[2] : (new StringBuilder(String.valueOf(gp[2])))
	 * .append(":").append(gp[3]).toString(), id, data)) return true; }* /
	 * 
	 * return r; }
	 */

	public static int getPermNumeral(Player player, String string, int thisid) {
		return 0;
	}

	public static int getPermNumeral(Player p, String permBase, int id, int data) {
		int r = -1;
		String perms[] = getPermissions(p, permBase);
		String negPerms[] = getPermissions(p,
				(new StringBuilder("-")).append(permBase).toString());
		for (int i = 0; i < negPerms.length; i++) {
			String gp[] = negPerms[i].replace('.', ';').split(";");
			String gs[] = permBase.replace('.', ';').split(";");
			if (gp.length >= 2
					&& gs.length >= 2
					&& gp[1] != null
					&& gs[1] != null
					&& gp[1].equals(gs[1])
					&& TRNoItem.isInRanged(gp.length != 5 ? (new StringBuilder(
							String.valueOf(gp[2]))).append(":-10").toString()
							: (new StringBuilder(String.valueOf(gp[2])))
									.append(":").append(gp[3]).toString(), id,
							data)) {
				return -1;
			}
		}

		for (int i = 0; i < perms.length; i++) {
			String gp[] = perms[i].replace('.', ';').split(";");
			String gs[] = permBase.replace('.', ';').split(";");
			if (gp.length >= 2
					&& gs.length >= 2
					&& gp[1] != null
					&& gs[1] != null
					&& gp[1].equals(gs[1])
					&& TRNoItem.isInRanged(gp.length != 5 ? gp[2]
							: (new StringBuilder(String.valueOf(gp[2])))
									.append(":").append(gp[3]).toString(), id,
							data)) {
				return Integer.valueOf(gp.length != 5 ? gp[3] : gp[4])
						.intValue();
			}
		}

		return r;
	}

	private static String[] getPermissions(Player p, String s) {
		if (tekkitrestrict.pm.isPluginEnabled("PermissionsEx")
				&& isForceThis(0)) {
			return getAllPEXPlayerPerms(p, s);
		}
		if (tekkitrestrict.pm.isPluginEnabled("PermissionsBukkit")
				&& isForceThis(1)) {
			return new String[0];
		}
		if (tekkitrestrict.pm.isPluginEnabled("bPermissions") && isForceThis(2)) {
			Permission ps[] = ApiLayer.getPermissions(p.getWorld().getName(),
					CalculableType.USER, p.getName());
			List<String> sr = new LinkedList<String>();
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
			sr.clear();
			return lz;
		}
		if (tekkitrestrict.pm.isPluginEnabled("GroupManager") && isForceThis(3)) {
			GroupManager ps = (GroupManager) tekkitrestrict.getInstance()
					.getServer().getPluginManager().getPlugin("GroupManager");
			List<String> sr = new LinkedList<String>();
			sr.addAll(ps.getWorldsHolder().getWorldData(p).getUser(p.getName())
					.getPermissionList());
			sr.addAll(ps.getWorldsHolder().getWorldData(p).getUser(p.getName())
					.getGroup().getPermissionList());
			String a;
			for (Iterator iterator = ps.getWorldsHolder().getWorldData(p)
					.getUser(p.getName()).getGroup().getInherits().iterator(); iterator
					.hasNext(); sr.add(a)) {
				a = (String) iterator.next();
			}

			for (int i = 0; i < sr.size(); i++) {
				if (!sr.get(i).startsWith(s)) {
					sr.remove(i);
					i--;
				}
			}

			String lz[] = sr.toArray(new String[0]);
			sr.clear();
			return lz;
		} else {
			return new String[0];
		}
	}

	static String[] getAllPEXPlayerPerms(Player p, String sss) {
		List<String> l = new LinkedList<String>();
		PermissionUser pu = ((PermissionManager) tekkitrestrict.perm)
				.getUser(p);
		String as[];
		int j = (as = pu.getPermissions(p.getWorld().getName())).length;
		for (int i = 0; i < j; i++) {
			String sg = as[i];
			if (sg.startsWith(sss)) {
				l.add(sg);
			}
		}

		Map sssrr = pu.getAllPermissions();
		for (Iterator iterator = sssrr.keySet().iterator(); iterator.hasNext();) {
			Object sg = iterator.next();
			String ssworld = (String) sg;
			if (ssworld == null || ssworld == "null") {
				String as1[];
				int k1 = (as1 = (String[]) sssrr.get(ssworld)).length;
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
		l.clear();
		return lsst;
	}

	public static void testPerms(Player player) {

	}

	private static boolean isForceThis(int t) {
		return forcepermmanager == -1 ? true : forcepermmanager == t;
	}

	private static int forcepermmanager = -1;

}
