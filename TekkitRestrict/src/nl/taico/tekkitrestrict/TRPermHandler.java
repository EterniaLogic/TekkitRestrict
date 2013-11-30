package nl.taico.tekkitrestrict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TRPermLimit;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;

public class TRPermHandler {
	public static PermissionManager permEx;
	private static RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> v;

	/**
	 * @deprecated Unnecessary, use player.hasPermission("tekkitrestrict."+type+"."+node1) instead
	 */
	public static boolean hasPermission(@NonNull Player player, @NonNull String type, @NonNull String node1, @NonNull String node2) {
		Log.Debug("[WARNING] Call to deprecated permission checker!");
		try {
			String n1 = node1.equals("") ? "" : "." + node1;
			String n2 = node2.equals("") ? "" : "." + node2;
			String perm = "tekkitrestrict." + type + n1 + n2;

			PluginManager pm = Bukkit.getPluginManager();
			if(pm.isPluginEnabled("Vault")){
				if(v == null) v = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		        if (v != null) {
		        	net.milkbowl.vault.permission.Permission permission = v.getProvider();
		        	return permission.has(player, perm);
		        }
			} else if (pm.isPluginEnabled("PermissionsEx")) {
				if (permEx == null) permEx = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
				return permEx.getUser(player).has(perm);
			} else if (pm.isPluginEnabled("bPermissions")) {
				return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), perm);
			} else if (pm.isPluginEnabled("GroupManager")) {
				GroupManager ps = (GroupManager) pm.getPlugin("GroupManager");
				return ps.getWorldsHolder().getWorldData(player).getUser(player.getName()).hasSamePermissionNode(perm);
			} else {
				return player.hasPermission(perm);
			}
		} catch (Exception ex) {
			Warning.other("An error occurred in the PermHandler ('+TRPermHandler.hasPermission(...):boolean')!", false);
			Log.Exception(ex, false);
		}
		return false;
	}

	private static boolean logged = false;
	@Nullable public static TRPermLimit getPermLimitFromPerm(@NonNull Player player, @NonNull String permBase, int id, int data) {
		TRPermLimit t = new TRPermLimit();
		Set<String> negPerms = getNegPermissions(player, permBase);
		
		for (String negPerm : negPerms) {
			String gp[] = negPerm.split("\\.");//tekkitrestrict;limiter;id;data
			try {
				if (gp.length == 5){
					if (TRItemProcessor.isInRange(gp[2]+":"+gp[3], id, data, negPerm)){
						t.id = id;
						t.data = Integer.parseInt(gp[3]);
						t.max = -2;
						return t;
					}
				} else if (gp.length == 4){
					if (TRItemProcessor.isInRange(gp[2], id, data, negPerm)){
						t.id = id;
						t.data = -1;
						t.max = -2;
						return t;
					}
				}
			} catch (Exception ex){
				if (!logged){
					Warning.other("You have set an invalid limiter permission \""+negPerm+"\":", false);
					Warning.other("Unexpected error occurred! Please inform the author of this error.", true);
					Log.Exception(ex, true);
					logged = true;
				}
			}
		}

		Set<String> perms = getPermissions(player, permBase);
		
		if (perms.isEmpty()){
			for (int i = 0; i <= 20; i++){
				if (player.hasPermission("tekkitrestrict.limiter."+id+"."+data+"."+i)) {
					t.id = id;
					t.data = data;
					t.max = i;
					return t;
				}
				
				if (player.hasPermission("tekkitrestrict.limiter."+id+"."+i)){
					t.id = id;
					t.data = -1;
					t.max = i;
					return t;
				}
			}
		}
		
		for (String perm : perms) {
			String gp[] = perm.split("\\.");//tekkitrestrict;limiter;id
			try {
				if (gp.length == 5){
					if (TRItemProcessor.isInRange(gp[2]+":"+gp[3], id, data, perm)){
						try {
							t.id = id;
							t.data = Integer.parseInt(gp[3]);
							t.max = Integer.parseInt(gp[4]);
							return t;
						} catch (NumberFormatException ex){
							Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
							Warning.other("Invalid max amount: \""+gp[4]+"\"", false);
							return null;
						}
					}
				} else if (gp.length == 4) {
					if (TRItemProcessor.isInRange(gp[2], id, data, perm)){
						try {
							t.id = id;
							t.data = -1;
							t.max = Integer.parseInt(gp[3]);
							return t;
						} catch (NumberFormatException ex){
							Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
							Warning.other("Invalid max amount: \""+gp[3]+"\"", false);
							return null;
						}
					}
				}
			} catch (Exception ex){
				if (!logged){
					Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
					Warning.other("Unexpected error occurred! Please inform the author of this error.", true);
					Log.Exception(ex, true);
					logged = true;
				}
			}
		}

		return null;
	}

	@NonNull public static List<TRPermLimit> getAllLimiterPerms(@NonNull Player player){
		ArrayList<TRPermLimit> tbr = new ArrayList<TRPermLimit>(100);
		
		Set<String> negPerms = getNegPermissions(player, "tekkitrestrict.limiter");
		for (String negPerm : negPerms) {
			String gp[] = negPerm.split("\\.");//tekkitrestrict;limiter;id;data
			try {
				if (gp.length == 5){
					List<TRItem> items = TRItemProcessor.processItemString(gp[2]+":"+gp[3]);
					for (TRItem item : items){
						TRPermLimit t = new TRPermLimit();
						t.id = item.id;
						t.data = item.data;
						t.max = -2;
						tbr.add(t);
					}
				} else if (gp.length == 4){
					List<TRItem> items = TRItemProcessor.processItemString(gp[2]);
					for (TRItem item : items){
						TRPermLimit t = new TRPermLimit();
						t.id = item.id;
						t.data = item.data;
						t.max = -2;
						tbr.add(t);
					}
				}
			} catch (Exception ex){
				if (!logged){
					Warning.other("You have set an invalid limiter permission \""+negPerm+"\":", false);
					Warning.other("Unexpected error occurred! Please inform the author of this error.", true);
					Log.Exception(ex, true);
					logged = true;
				}
			}
		}

		Set<String> perms = getPermissions(player, "tekkitrestrict.limiter");
		for (String perm : perms) {
			String gp[] = perm.split("\\.");//tekkitrestrict;limiter;id
			try {
				if (gp.length == 5){
					int max;
					
					try {
						max = Integer.parseInt(gp[4]);
					} catch (NumberFormatException ex){
						Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
						Warning.other("Invalid max amount: \""+gp[4]+"\"", false);
						continue;
					}
					
					List<TRItem> items = TRItemProcessor.processItemString(gp[2]+":"+gp[3]);
					for (TRItem item : items){
						TRPermLimit t = new TRPermLimit();
						t.id = item.id;
						t.data = item.data;
						t.max = max;
						tbr.add(t);
					}
				} else if (gp.length == 4) {
					int max;
					
					try {
						max = Integer.parseInt(gp[3]);
					} catch (NumberFormatException ex){
						Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
						Warning.other("Invalid max amount: \""+gp[3]+"\"", false);
						continue;
					}
					
					List<TRItem> items = TRItemProcessor.processItemString(gp[2]);
					for (TRItem item : items){
						TRPermLimit t = new TRPermLimit();
						t.id = item.id;
						t.data = item.data;
						t.max = max;
						tbr.add(t);
					}
				}
			} catch (Exception ex){
				if (!logged){
					Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
					Warning.other("Unexpected error occurred! Please inform the author of this error.", true);
					Log.Exception(ex, true);
					logged = true;
				}
			}
		}
		
		HashSet<TRPermLimit> removals = new HashSet<TRPermLimit>();
		for (int i = 0; i<tbr.size(); i++){
			TRPermLimit t = tbr.get(i);
			for (int j = 0; j < tbr.size(); j++){
				TRPermLimit t2 = tbr.get(j);
				if (t == t2) continue;
				if (t.compare_Perm(t2)){
					if (t.max == -2){
						removals.add(t2);
					} else if (t2.max == -2){
						removals.add(t);
					}
				}
			}
		}
		tbr.removeAll(removals);
		
		return tbr;
	}
	
	/**
	 * @return A String Array of negated permissions, or a <code>new String[0]</code> if permissions plugin is not found.
	 */
	@NonNull private static Set<String> getNegPermissions(@NonNull Player player, @NonNull String permBase) {
		PluginManager pm = Bukkit.getPluginManager();
		if (pm.isPluginEnabled("PermissionsEx")) {
			return getAllPEXPlayerPerms(player, "-"+permBase);
		}
		
		if (pm.isPluginEnabled("bPermissions")) {
			Permission ps[] = ApiLayer.getPermissions(player.getWorld().getName(), CalculableType.USER, player.getName());
			HashSet<String> sr = new HashSet<String>(30);
			Permission apermission[];
			int k = (apermission = ps).length;
			for (int j = 0; j < k; j++) {
				Permission px = apermission[j];
				if (px.isTrue()) continue;//Only negated ones.
				String perm = px.nameLowerCase();
				if (!perm.startsWith(permBase)) continue;
				sr.add(perm);
			}

			return sr;
		}
		
		if (pm.isPluginEnabled("GroupManager")) {
			GroupManager ps = (GroupManager) pm.getPlugin("GroupManager");
			HashSet<String> sr = new HashSet<String>(30);
			User user = ps.getWorldsHolder().getWorldData(player).getUser(player.getName());
			sr.addAll(user.getPermissionList());
			for (Group group : user.subGroupListCopy()){
				sr.addAll(group.getPermissionList());
			}
			sr.addAll(user.getGroup().getPermissionList());
			//String a;
			for (String inherit: user.getGroup().getInherits()){
				Group gi = ps.getWorldsHolder().getWorldData(player).getGroup(inherit);
				if (gi == null) continue;
				
				sr.addAll(gi.getPermissionList());
			}
			permBase = "-"+permBase;
			Iterator<String> it = sr.iterator();
			while (it.hasNext()){
				String str = it.next();
				if (!str.startsWith(permBase)) it.remove();
			}

			return sr;
		}
		
		return new HashSet<String>(0);
	}
	
	@NonNull private static Set<String> getPermissions(@NonNull Player player, @NonNull String permBase) {
		PluginManager pm = Bukkit.getPluginManager();
		if (pm.isPluginEnabled("PermissionsEx")) {
			return getAllPEXPlayerPerms(player, permBase);
		}
		
		if (pm.isPluginEnabled("bPermissions")) {
			Permission ps[] = ApiLayer.getPermissions(player.getWorld().getName(), CalculableType.USER, player.getName());
			HashSet<String> sr = new HashSet<String>(30);
			Permission apermission[];
			int k = (apermission = ps).length;
			for (int j = 0; j < k; j++) {
				Permission px = apermission[j];
				if (!px.isTrue()) continue; //Only positive permissions
				String perm = px.nameLowerCase();
				if (!perm.startsWith(permBase)) continue;
				sr.add(perm);
			}

			return sr;
		}
		
		if (pm.isPluginEnabled("GroupManager")) {
			GroupManager ps = (GroupManager) pm.getPlugin("GroupManager");
			HashSet<String> sr = new HashSet<String>(30);
			User user = ps.getWorldsHolder().getWorldData(player).getUser(player.getName());
			sr.addAll(user.getPermissionList());
			for (Group group : user.subGroupListCopy()){
				sr.addAll(group.getPermissionList());
			}
			sr.addAll(user.getGroup().getPermissionList());
			//String a;
			for (String inherit: user.getGroup().getInherits()){
				Group gi = ps.getWorldsHolder().getWorldData(player).getGroup(inherit);
				if (gi == null) continue;
				
				sr.addAll(gi.getPermissionList());
			}
			
			Iterator<String> it = sr.iterator();
			while (it.hasNext()){
				String str = it.next();
				if (!str.startsWith(permBase)) {
					it.remove();
				}
			}

			return sr;
		}
		
		return new HashSet<String>(0);
	}

	@NonNull private static Set<String> getAllPEXPlayerPerms(@NonNull Player player, @NonNull String permBase) {
		HashSet<String> tbr = new HashSet<String>(30);
		if (permEx == null) permEx = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
		PermissionUser user = permEx.getUser(player);
		
		String world = player.getWorld().getName();
		String userPerms[] = user.getPermissions(world);
		for (String perm : userPerms) {
			if (!perm.startsWith(permBase)) continue;
			tbr.add(perm);
		}
		
		Collection<String[]> allPermissions = user.getAllPermissions().values();
		for (String[] perms : allPermissions){
			if (perms == null) continue;
			for (String perm : perms){
				if (!perm.startsWith(permBase)) continue;
				tbr.add(perm);
			}
		}

		PermissionGroup groups[] = user.getGroups();
		for (PermissionGroup group : groups) {
			String groupPerms[] = group.getPermissions(world);
			for (String perm : groupPerms) {
				if (!perm.startsWith(permBase)) continue;
				tbr.add(perm);
			}

			groupPerms = group.getPermissions(null);
			for (String perm : groupPerms) {
				if (!perm.startsWith(permBase)) continue;
				tbr.add(perm);
			}
			

			PermissionGroup childrenGroups[] = group.getChildGroups();
			for (int i = 0; i < childrenGroups.length; i++) {
				String perms[] = group.getPermissions(world);
				for (String perm : perms) {
					if (!perm.startsWith(permBase)) continue;
					tbr.add(perm);
				}

				perms = group.getPermissions(null);
				for (String perm : perms) {
					if (!perm.startsWith(permBase)) continue;
					tbr.add(perm);
				}
			}
		}

		return tbr;
	}

}
