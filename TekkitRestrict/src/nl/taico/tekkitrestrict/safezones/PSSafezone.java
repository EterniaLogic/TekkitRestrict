package nl.taico.tekkitrestrict.safezones;

import java.util.ArrayList;

import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.managers.ForceFieldManager;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.Field;
import nl.taico.tekkitrestrict.objects.TRWorldPos;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PSSafezone extends TRSafezone {
	protected static ArrayList<PSSafezone> pszones = new ArrayList<PSSafezone>();
	protected static Plugin plugin;
	protected Field field;
	static {
		plugin = Bukkit.getPluginManager().getPlugin("PreciousStones");
	}

	public static ArrayList<PSSafezone> getZones(){
		synchronized (pszones){
			return new ArrayList<PSSafezone>(pszones);
		}
	}

	public static boolean isInSafezone(final Player player){
		final Location loc = player.getLocation();
		final ForceFieldManager ps = ((PreciousStones) plugin).getForceFieldManager();

		final Field field = ps.getEnabledSourceField(loc.getBlock().getLocation(), FieldFlag.CUBOID);
		if (field == null) return false;

		final String name = player.getName();

		if (ps.isApplyToAllowed(field, name)) return false;


		if (field.hasFlag(FieldFlag.APPLY_TO_ALL)) return true;
		else if (ps.isAllowed(field, name)) return false;

		return true;
	}

	protected PSSafezone(final String name, final Location loc1, final Location loc2) {
		super(2, name);
		final PreciousStones ps = (PreciousStones) plugin;
		this.world = loc1.getWorld().getName().toLowerCase();

		Field field = ps.getForceFieldManager().getEnabledSourceField(loc1.getBlock().getLocation(), FieldFlag.CUBOID);
		if (field == null){
			field = ps.getForceFieldManager().getEnabledSourceField(loc2.getBlock().getLocation(), FieldFlag.CUBOID);
			if (field == null){
				field = ps.getForceFieldManager().getEnabledSourceField(new Location (loc1.getWorld(), (loc1.getBlockX()+loc2.getBlockX())/2, (loc1.getBlockY()+loc2.getBlockY())/2, (loc1.getBlockZ()+loc2.getBlockZ())/2), FieldFlag.CUBOID);
			}
		}

		if (field != null){
			this.location = new TRWorldPos(loc1.getWorld(), field.getMinx(), field.getMiny(), field.getMinz(), field.getMaxx(), field.getMaxy(), field.getMaxz());
			this.valid = true;
			this.field = field;
		} else {
			this.valid = false;
			this.location = new TRWorldPos(loc1, loc2);
		}

		synchronized (pszones){
			pszones.add(this);
		}
	}

	protected PSSafezone(final String name, final TRWorldPos loc) {
		this(name, loc.getLesserCorner(), loc.getGreaterCorner());
	}

	@Override
	public boolean isSafezoneFor(final Player player) {
		if (!valid || (field == null)) return false;
		final Location loc = player.getLocation();
		if (!location.contains(loc)) return false;

		final String name = player.getName();
		final Boolean b = cache.get(name);
		if (b != null) return b.booleanValue();
		if (((PreciousStones) plugin).getForceFieldManager().isApplyToAllowed(field, name)){
			cache.put(name, false);
			return false;
		}

		if (field.hasFlag(FieldFlag.APPLY_TO_ALL)){
			cache.put(name, true);
			return true;
		} else if(((PreciousStones) plugin).getForceFieldManager().isAllowed(field, name)){
			cache.put(name, false);
			return false;
		}

		cache.put(name, true);
		return true;
	}

	@Override
	protected void remove(){
		synchronized (pszones){
			pszones.remove(this);
		}
	}

	@Override
	public void update() {
		cache.clear();
	}

}
