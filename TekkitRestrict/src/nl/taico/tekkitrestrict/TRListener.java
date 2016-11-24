package nl.taico.tekkitrestrict;

//import net.minecraft.server.TileEntity;
//import net.minecraft.server.WorldServer;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.commands.TRCmdOpenAlc;
import nl.taico.tekkitrestrict.commands.TRCmdOpenInv;
import nl.taico.tekkitrestrict.functions.TRLWCProtect;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRNoDupeProjectTable;
import nl.taico.tekkitrestrict.functions.TRNoInteract;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.objects.TRItem;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.craftbukkit.CraftWorld;

//import eloraam.core.TileCovered;

public class TRListener implements Listener {
	//private int lastdata = 0;

	public static boolean errorBlockPlace = false;

	public static boolean errorInteract = false;

	//for covers, the event is called twice.
	//first one is 136:itemdata
	//second one is 136:0

	/**
	 * @return <b>True</b> if id < 7 or id = 12 (sand), 13 (gravel), 17 (logs), 20 (glass), 24 (sandstone), 35 (wool), 44, 98 (stonebricks) or 142 (marble).
	 * <b>False</b> otherwise.
	 */
	private static boolean exempt(int id){
		return ((id < 7) || (id == 12) || (id == 13) || (id == 17) || (id == 20) || (id == 24) || (id == 35) || (id == 44) || (id == 98) || (id == 142));
	}

	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		final Block block = event.getBlock();
		final int id = block.getTypeId();
		if (exempt(id)) return;

		final Player player;
		if ((player = event.getPlayer()) == null) return;

		final String pname = player.getName();

		try {
			if ((id != 136) && !TRLWCProtect.checkLWCAllowed(event)) return;

			final int data = block.getData();

			if (Listeners.UseBlockLimit && !pname.equals("[BuildCraft]") && !pname.equals("[RedPower]")){
				if (!player.hasPermission("tekkitrestrict.bypass.limiter")) {
					final TRLimiter il = TRLimiter.getOnlineLimiter(player);
					String limited = il.checkLimit(event, false);
					if (limited != null) {
						if (limited.isEmpty()) limited = ChatColor.RED + "[TRItemLimiter] You cannot place down any more of that block!";
						TRItem.sendBannedMessage(player, limited);
						event.setCancelled(true);
					}
				}
			}

			String msg = TRNoItem.isItemBanned(player, id, data, true);

			if (msg != null) {
				if (msg.isEmpty()) msg = ChatColor.RED + "[TRItemDisabler] You are not allowed to place down this type of block!";
				TRItem.sendBannedMessage(player, msg);
				event.setCancelled(true);
			}
		} catch(Exception ex){
			if (!errorBlockPlace){
				Warning.other("An error occurred in the BlockPlace Listener! Please inform the author (This error will only be logged once).", false);
				Log.Exception(ex, false);
				errorBlockPlace = true;
			}
		}

	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		final Player player;
		if ((player = (Player) e.getPlayer()) == null) return;

		TRNoDupeProjectTable.playerUnuse(player.getName());
		TRCmdOpenAlc.setPlayerInv(player, true);
		TRCmdOpenInv.closeInv(player);
	}

	// /////// START INTERACT //////////////
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player;
		if ((player = event.getPlayer()) == null) return;

		// lets do this based on a white-listed approach.
		// First, lets loop through the DisableClick list to stop clicks.
		// Perf: 8x
		if (TRNoInteract.isDisabled(event)){
			event.setCancelled(true);
			return;
		}

		final ItemStack str = player.getItemInHand();
		if (str == null) return;

		if ((TRConfigCache.LWC.lwc || !TRLWCProtect.init) && (event.getAction() == Action.RIGHT_CLICK_BLOCK) && (str.getTypeId() == 136)){
			//cover = (data >> 8 == 0 v >=16 && <=45
			if (!TRLWCProtect.isLWCAllowed(event)) return;
		}

		if (player.getGameMode() != GameMode.CREATIVE) return;

		String msg;
		try {
			msg = TRNoItem.isItemBannedInCreative(player, str.getTypeId(), str.getDurability(), true);
		} catch (Exception ex) {
			if (!errorInteract){
				Warning.other("An error occurred in the InteractListener for LimitedCreative!", false);
				Log.Exception(ex, false);
				errorInteract = true;
			}
			return;
		}

		if (msg != null) {
			if (msg.isEmpty()) msg = ChatColor.RED + "[TRLimitedCreative] You may not interact with this item.";
			TRItem.sendBannedMessage(player, msg);
			event.setCancelled(true);
			player.setItemInHand(null);
			return;
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract2(PlayerInteractEvent event){
		if (TRNoDupeProjectTable.isTableUseAllowed(event.getClickedBlock(), event.getPlayer())) return;
		event.getPlayer().sendMessage(ChatColor.RED + "Someone else is already using this project table!");
		event.setCancelled(true);
		return;
	}
	// /////////// END INTERACT /////////////
}
