package nl.taico.tekkitrestrict.safezones;

import java.util.ArrayList;

import nl.taico.tekkitrestrict.TRConfigCache.SafeZones;
import nl.taico.tekkitrestrict.objects.TRWorldPos;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;

public class FSafezone extends TRSafezone {
	protected static ArrayList<FSafezone> zones = new ArrayList<FSafezone>();
	protected static Plugin plugin;
	public static boolean isInSafezone(Player player) {
		final Location loc = player.getLocation();
		final Faction faction = Board.getFactionAt(loc);
		if (faction == null) return false;

		final String name = player.getName();

		if (Conf.playersWhoBypassAllProtection.contains(name)) return false;

		FPlayer fplayer = FPlayers.i.get(name);
		//fplayer.getFaction().getId()

		if (faction.getFPlayers().contains(fplayer)){
			return false;
		} else {
			if (FPerm.BUILD.has(fplayer, faction)) return false;
			else return true;
		}
	}

	protected Faction faction;

	static {
		plugin = Bukkit.getPluginManager().getPlugin("Factions");
	}

	protected FSafezone(final String name, final TRWorldPos loc) {
		super(3, name);
		this.location = loc;
		faction = Board.getFactionAt(loc.getCenter());

		if (faction == null) faction = Board.getFactionAt(loc.getLesserCorner());
		if (faction == null) faction = Board.getFactionAt(loc.getGreaterCorner());

		if (faction == null){
			this.valid = false;
		} else {
			switch (SafeZones.FMode){
			case SpecificAdmin:
				if (faction.getFlag(FFlag.EXPLOSIONS)) valid = false;
				break;
			case Specific:
				if (!faction.getFlag(FFlag.PEACEFUL)) valid = false;
				break;
			default:
				break;
			}
			Location home = faction.getHome();
			if (home != null) this.location = new TRWorldPos(home, home);
		}
		this.world = loc.getWorld().getName().toLowerCase();
		zones.add(this);
	}

	@Override
	public boolean isSafezoneFor(final Player player) {
		final Location loc = player.getLocation();
		final Faction faction = Board.getFactionAt(loc);
		if (faction == null) return false;

		final String name = player.getName();
		final Boolean b = cache.get(name);
		if (b != null) return b.booleanValue();

		if (Conf.playersWhoBypassAllProtection.contains(name)){
			cache.put(name, false);
			return false;
		}

		FPlayer fplayer = FPlayers.i.get(name);
		//fplayer.getFaction().getId()

		if (faction.getFPlayers().contains(fplayer)){
			cache.put(name, false);
			return false;
		} else {
			if (FPerm.BUILD.has(fplayer, faction)){
				cache.put(name, false);
				return false;
			} else {
				cache.put(name, true);
				return true;
			}
		}
	}

	@Override
	protected void remove() {
		// TODO Auto-generated method stub

	}


	@Override
	public void update() {
		cache.clear();
	}

}
