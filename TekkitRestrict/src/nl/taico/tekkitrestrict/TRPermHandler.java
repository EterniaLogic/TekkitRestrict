package nl.taico.tekkitrestrict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import lombok.NonNull;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TRPermLimit;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import de.bananaco.bpermissions.api.util.Permission;

public class TRPermHandler {
	public static PermissionManager permEx;

	private static boolean logged = false;
	@NonNull public static List<TRPermLimit> getAllLimiterPerms(@NonNull final Player player){
		final ArrayList<TRPermLimit> tbr = new ArrayList<TRPermLimit>(100);

		final Set<String> negPerms = getNegPermissions(player, "tekkitrestrict.limiter");
		for (final String negPerm : negPerms) {
			final String gp[] = negPerm.split("\\.");//tekkitrestrict;limiter;id;data
			try {
				if (gp.length == 5){
					final List<TRItem> items = TRItemProcessor2.processString(gp[2]+":"+gp[3]);
					for (final TRItem item : items){
						TRPermLimit t = new TRPermLimit();
						t.id = item.id;
						t.data = item.data;
						t.max = -2;
						tbr.add(t);
					}
				} else if (gp.length == 4){
					final List<TRItem> items = TRItemProcessor2.processString(gp[2]);
					for (final TRItem item : items){
						final TRPermLimit t = new TRPermLimit();
						t.id = item.id;
						t.data = item.data;
						t.max = -2;
						tbr.add(t);
					}
				}
			} catch (final Exception ex){
				if (!logged){
					Warning.other("You have set an invalid limiter permission \""+negPerm+"\":", false);
					Warning.other("Unexpected error occurred! Please inform the author of this error.", true);
					Log.Exception(ex, true);
					logged = true;
				}
			}
		}

		final Set<String> perms = getPermissions(player, "tekkitrestrict.limiter");
		for (final String perm : perms) {
			final String gp[] = perm.split("\\.");//tekkitrestrict;limiter;id
			try {
				if (gp.length == 5){
					final int max;

					try {
						max = Integer.parseInt(gp[4]);
					} catch (final NumberFormatException ex){
						Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
						Warning.other("Invalid max amount: \""+gp[4]+"\"", false);
						continue;
					}

					final List<TRItem> items = TRItemProcessor2.processString(gp[2]+":"+gp[3]);
					for (final TRItem item : items){
						final TRPermLimit t = new TRPermLimit();
						t.id = item.id;
						t.data = item.data;
						t.max = max;
						tbr.add(t);
					}
				} else if (gp.length == 4) {
					final int max;

					try {
						max = Integer.parseInt(gp[3]);
					} catch (final NumberFormatException ex){
						Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
						Warning.other("Invalid max amount: \""+gp[3]+"\"", false);
						continue;
					}

					final List<TRItem> items = TRItemProcessor2.processString(gp[2]);
					for (final TRItem item : items){
						final TRPermLimit t = new TRPermLimit();
						t.id = item.id;
						t.data = item.data;
						t.max = max;
						tbr.add(t);
					}
				}
			} catch (final Exception ex){
				if (!logged){
					Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
					Warning.other("Unexpected error occurred! Please inform the author of this error.", true);
					Log.Exception(ex, true);
					logged = true;
				}
			}
		}

		final HashSet<TRPermLimit> removals = new HashSet<TRPermLimit>();
		for (int i = 0; i<tbr.size(); i++){
			final TRPermLimit t = tbr.get(i);
			for (int j = 0; j < tbr.size(); j++){
				final TRPermLimit t2 = tbr.get(j);
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

	@NonNull private static Set<String> getAllPEXPlayerPerms(@NonNull final Player player, @NonNull String permBase) {
		final HashSet<String> tbr = new HashSet<String>(30);
		if (permEx == null) permEx = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
		final PermissionUser user = permEx.getUser(player);

		final String world = player.getWorld().getName();
		final String userPerms[] = user.getPermissions(world);
		for (final String perm : userPerms) {
			if (!perm.startsWith(permBase)) continue;
			tbr.add(perm);
		}

		final Collection<String[]> allPermissions = user.getAllPermissions().values();
		for (final String[] perms : allPermissions){
			if (perms == null) continue;
			for (final String perm : perms){
				if (!perm.startsWith(permBase)) continue;
				tbr.add(perm);
			}
		}

		final PermissionGroup groups[] = user.getGroups();
		for (final PermissionGroup group : groups) {
			String groupPerms[] = group.getPermissions(world);
			for (final String perm : groupPerms) {
				if (!perm.startsWith(permBase)) continue;
				tbr.add(perm);
			}

			groupPerms = group.getPermissions(null);
			for (final String perm : groupPerms) {
				if (!perm.startsWith(permBase)) continue;
				tbr.add(perm);
			}


			final PermissionGroup childrenGroups[] = group.getChildGroups();
			for (int i = 0; i < childrenGroups.length; i++) {
				String perms[] = group.getPermissions(world);
				for (final String perm : perms) {
					if (!perm.startsWith(permBase)) continue;
					tbr.add(perm);
				}

				perms = group.getPermissions(null);
				for (final String perm : perms) {
					if (!perm.startsWith(permBase)) continue;
					tbr.add(perm);
				}
			}
		}

		return tbr;
	}

	/**
	 * @return A String Array of negated permissions, or a <code>new String[0]</code> if permissions plugin is not found.
	 */
	@NonNull private static Set<String> getNegPermissions(@NonNull final Player player, @NonNull String permBase) {
		final PluginManager pm = Bukkit.getPluginManager();
		if (pm.isPluginEnabled("PermissionsEx")) {
			return getAllPEXPlayerPerms(player, "-"+permBase);
		}

		if (pm.isPluginEnabled("bPermissions")) {
			final Permission ps[] = ApiLayer.getPermissions(player.getWorld().getName(), CalculableType.USER, player.getName());
			final HashSet<String> sr = new HashSet<String>(30);
			final Permission apermission[];
			int k = (apermission = ps).length;
			for (int j = 0; j < k; j++) {
				final Permission px = apermission[j];
				if (px.isTrue()) continue;//Only negated ones.
				final String perm = px.nameLowerCase();
				if (!perm.startsWith(permBase)) continue;
				sr.add(perm);
			}

			return sr;
		}

		if (pm.isPluginEnabled("GroupManager")) {
			final GroupManager ps = (GroupManager) pm.getPlugin("GroupManager");
			final HashSet<String> sr = new HashSet<String>(30);
			final User user = ps.getWorldsHolder().getWorldData(player).getUser(player.getName());
			sr.addAll(user.getPermissionList());
			for (final Group group : user.subGroupListCopy()){
				sr.addAll(group.getPermissionList());
			}
			sr.addAll(user.getGroup().getPermissionList());
			//String a;
			for (final String inherit: user.getGroup().getInherits()){
				final Group gi = ps.getWorldsHolder().getWorldData(player).getGroup(inherit);
				if (gi == null) continue;

				sr.addAll(gi.getPermissionList());
			}
			permBase = "-"+permBase;
			final Iterator<String> it = sr.iterator();
			while (it.hasNext()){
				final String str = it.next();
				if (!str.startsWith(permBase)) it.remove();
			}

			return sr;
		}

		return new HashSet<String>(0);
	}

	@NonNull private static Set<String> getPermissions(@NonNull final Player player, @NonNull String permBase) {
		final PluginManager pm = Bukkit.getPluginManager();
		if (pm.isPluginEnabled("PermissionsEx")) {
			return getAllPEXPlayerPerms(player, permBase);
		}

		if (pm.isPluginEnabled("bPermissions")) {
			final Permission ps[] = ApiLayer.getPermissions(player.getWorld().getName(), CalculableType.USER, player.getName());
			final HashSet<String> sr = new HashSet<String>(30);
			final Permission apermission[];
			int k = (apermission = ps).length;
			for (int j = 0; j < k; j++) {
				final Permission px = apermission[j];
				if (!px.isTrue()) continue; //Only positive permissions
				final String perm = px.nameLowerCase();
				if (!perm.startsWith(permBase)) continue;
				sr.add(perm);
			}

			return sr;
		}

		if (pm.isPluginEnabled("GroupManager")) {
			final GroupManager ps = (GroupManager) pm.getPlugin("GroupManager");
			final HashSet<String> sr = new HashSet<String>(30);
			final User user = ps.getWorldsHolder().getWorldData(player).getUser(player.getName());
			sr.addAll(user.getPermissionList());
			for (final Group group : user.subGroupListCopy()){
				sr.addAll(group.getPermissionList());
			}
			sr.addAll(user.getGroup().getPermissionList());
			//String a;
			for (final String inherit: user.getGroup().getInherits()){
				final Group gi = ps.getWorldsHolder().getWorldData(player).getGroup(inherit);
				if (gi == null) continue;

				sr.addAll(gi.getPermissionList());
			}

			final Iterator<String> it = sr.iterator();
			while (it.hasNext()){
				final String str = it.next();
				if (!str.startsWith(permBase)) it.remove();
			}

			return sr;
		}

		return new HashSet<String>(0);
	}

	@Nullable public static TRPermLimit getPermLimitFromPerm(@NonNull final Player player, @NonNull final String permBase, final int id, final int data) {
		final TRPermLimit t = new TRPermLimit();
		final Set<String> negPerms = getNegPermissions(player, permBase);

		for (final String negPerm : negPerms) {
			final String gp[] = negPerm.split("\\.");//tekkitrestrict;limiter;id;data
			try {
				if (gp.length == 5){
					if (TRItemProcessor2.isInRange(gp[2]+":"+gp[3], id, data, negPerm)){
						t.id = id;
						t.data = Integer.parseInt(gp[3]);
						t.max = -2;
						return t;
					}
				} else if (gp.length == 4){
					if (TRItemProcessor2.isInRange(gp[2], id, data, negPerm)){
						t.id = id;
						t.data = -1;
						t.max = -2;
						return t;
					}
				}
			} catch (final Exception ex){
				if (!logged){
					Warning.other("You have set an invalid limiter permission \""+negPerm+"\":", false);
					Warning.other("Unexpected error occurred! Please inform the author of this error.", true);
					Log.Exception(ex, true);
					logged = true;
				}
			}
		}

		final Set<String> perms = getPermissions(player, permBase);

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

		for (final String perm : perms) {
			final String gp[] = perm.split("\\.");//tekkitrestrict;limiter;id
			try {
				if (gp.length == 5){
					if (TRItemProcessor2.isInRange(gp[2]+":"+gp[3], id, data, perm)){
						try {
							t.id = id;
							t.data = Integer.parseInt(gp[3]);
							t.max = Integer.parseInt(gp[4]);
							return t;
						} catch (final NumberFormatException ex){
							Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
							Warning.other("Invalid max amount: \""+gp[4]+"\"", false);
							return null;
						}
					}
				} else if (gp.length == 4) {
					if (TRItemProcessor2.isInRange(gp[2], id, data, perm)){
						try {
							t.id = id;
							t.data = -1;
							t.max = Integer.parseInt(gp[3]);
							return t;
						} catch (final NumberFormatException ex){
							Warning.other("You have set an invalid limiter permission \""+perm+"\":", false);
							Warning.other("Invalid max amount: \""+gp[3]+"\"", false);
							return null;
						}
					}
				}
			} catch (final Exception ex){
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

}
