package nl.taico.tekkitrestrict.safezones;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import nl.taico.tekkitrestrict.objects.TRWorldPos;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;

public class TownySafezone extends TRSafezone {
	protected static ArrayList<TownySafezone> townyzones = new ArrayList<TownySafezone>();
	protected static Plugin plugin;
	protected Town town;
	static {
		plugin = Bukkit.getPluginManager().getPlugin("Towny");
	}
	
	protected TownySafezone(final String name, final Location loc1, final Location loc2) {
		super(2, name);
		this.valid = false;
		this.location = new TRWorldPos(loc1, loc2);
		/*
		final PreciousStones ps = (PreciousStones) plugin;
		this.world = loc1.getWorld().getName().toLowerCase();
		
		loc1.getWorld();
		Towny t = (Towny) plugin;
		t.getTownyUniverse().getTowns()
		TownBlock tb = TownyUniverse.getTownBlock(loc1);
		
		if (field != null){
			this.location = new TRWorldPos(loc1.getWorld(), field.getMinx(), field.getMiny(), field.getMinz(), field.getMaxx(), field.getMaxy(), field.getMaxz());
			this.valid = true;
			this.field = field;
		} else {
			this.valid = false;
			this.location = new TRWorldPos(loc1, loc2);
		}
		
		synchronized (townyzones){
			townyzones.add(this);
		}*/
	}
	
	protected TownySafezone(final String name, final TRWorldPos loc) {
		this(name, loc.getLesserCorner(), loc.getGreaterCorner());
	}

	@Override
	public boolean isSafezoneFor(final Player player) {
		if (!valid || town == null) return false;
		final Location loc = player.getLocation();
		if (!location.contains(loc)) return false;
		
		final String name = player.getName();
		final Boolean b = cache.get(name);
		if (b != null) return b.booleanValue();
		if (PlayerCacheUtil.getCachePermission(player, loc, loc.getWorld().getHighestBlockAt(loc).getTypeId(), (byte) 0, TownyPermission.ActionType.DESTROY)){
			cache.put(name, false);
			return false;
		}
		
		for (final Resident resident : town.getResidents()){
			if (resident.getName().equalsIgnoreCase(name)){
				cache.put(name, false);
				return false;
			}
		}
		
		cache.put(name, true);
		return true;
	}
	
	public static ArrayList<TownySafezone> getZones(){
		synchronized (townyzones){
			return new ArrayList<TownySafezone>(townyzones);
		}
	}
	
	public static boolean isInSafezone(final Player player){
		final Location loc = player.getLocation();
		final Block cb = loc.getWorld().getHighestBlockAt(loc);
		if (PlayerCacheUtil.getCachePermission(player, loc, cb.getTypeId(), (byte) 0, TownyPermission.ActionType.DESTROY)){
			return false;
		}
		
		final TownBlock tb = TownyUniverse.getTownBlock(loc);
		final Town town;
		try {
			town = tb.getTown();
		} catch (Exception e) {
			return false;
		}
		
		if (town == null) return false;
		
		final String name = player.getName();
		
		for (final Resident resident : town.getResidents()){
			if (resident.getName().equalsIgnoreCase(name)) return false;
		}
		
		return true;
	}

	@Override
	public void update() {
		cache.clear();
	}
	
	protected void remove(){
		synchronized (townyzones){
			townyzones.remove(this);
		}
	}

}
