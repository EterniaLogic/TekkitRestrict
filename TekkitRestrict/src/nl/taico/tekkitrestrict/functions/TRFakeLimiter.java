package nl.taico.tekkitrestrict.functions;


import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.objects.TRConfigLimit;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class TRFakeLimiter extends TRLimiter {
	public TRFakeLimiter(String baseName){
		this.player = baseName;
	}

	@Override
	public void checkBreakLimit(final BlockBreakEvent event) {}

	@Override
	public void checkBreakLimit(final int id, final byte data, final Location bloc) {}

	@Override
	public String checkLimit(final BlockPlaceEvent event, final boolean doBypassCheck) {
		final Block block = event.getBlock();

		return getMax(event.getPlayer(), block.getTypeId(), block.getData()) == -1 ? null : "";
	}

	@Override
	public void clearLimits() {}

	@Override
	public void clearLimitsAndClearInDB() {}

	@Override
	public int getMax(final Player player, final int thisid, final int thisdata){
		try {
			for (final TRConfigLimit cc : configLimits) {
				if (cc.compare(thisid, thisdata)) {
					lastString = cc.msg;
					return cc.configcount;
				}
			}
		} catch (Exception ex){
			Warning.other("An error occurred while trying to get the maxlimit of a player ('+TRLimiter.getMax(...):int')!", false);
			Log.Exception(ex, false);
		}
		return -1;
	}
}
