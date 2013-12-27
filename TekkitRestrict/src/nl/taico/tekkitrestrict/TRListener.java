package nl.taico.tekkitrestrict;

import net.minecraft.server.TileEntity;
import net.minecraft.server.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.commands.TRCommandAlc;
import nl.taico.tekkitrestrict.functions.TRLWCProtect;
import nl.taico.tekkitrestrict.functions.TRLimiter;
import nl.taico.tekkitrestrict.functions.TRNoClick;
import nl.taico.tekkitrestrict.functions.TRNoDupeProjectTable;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.objects.TRItem;

import eloraam.core.TileCovered;

public class TRListener implements Listener {
	private int lastdata = 0;

	/**
	 * @return <b>True</b> if id < 7 or id = 12 (sand), 13 (gravel), 17 (logs), 20 (glass), 24 (sandstone), 35 (wool), 44, 98 (stonebricks) or 142 (marble).
	 * <b>False</b> otherwise.
	 */
	private static boolean exempt(int id){
		return (id < 7 || id == 12 || id == 13 || id == 17 || id == 20 || id == 24 || id == 35 || id == 44 || id == 98 || id == 142);
	}
	
	public static boolean errorBlockPlace = false;
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		int id = block.getTypeId();
		if (exempt(id)) return;

		Player player = event.getPlayer();
		
		if (player == null) {
			lastdata = block.getData();
			return;
		}
		
		String pname = player.getName();
		
		try {
			if (!TRLWCProtect.checkLWCAllowed(event)) return;
			
			int data = block.getData();
			
			if (!pname.equals("[BuildCraft]") && !pname.equals("[RedPower]")){
				if (Listeners.UseBlockLimit && !player.hasPermission("tekkitrestrict.bypass.limiter")) {
					TRLimiter il = TRLimiter.getOnlineLimiter(player);
					String limited = il.checkLimit(event, false);
					if (limited != null) {
						if (limited.equals("")) limited = ChatColor.RED + "[TRItemLimiter] You cannot place down any more of that block!";
						TRItem.sendBannedMessage(player, limited);
						event.setCancelled(true);

						if (id == 136){
							WorldServer ws = ((CraftWorld) block.getWorld()).getHandle();
							TileEntity te1 = ws.getTileEntity(block.getX(), block.getY(), block.getZ());
							if (te1 instanceof TileCovered) {
								TileCovered tc = (TileCovered) te1;
								for (int i = 0; i < 6; i++) {
									if (tc.getCover(i) != -1 && tc.getCover(i) == data) {
										tc.tryRemoveCover(i);
									}
								}
								tc.updateBlockChange();
							}
						}
					}
				}
				
				if (id == 136 && data == 0){
					WorldServer ws = ((CraftWorld) block.getWorld()).getHandle();
					TileEntity te1 = ws.getTileEntity(block.getX(), block.getY(), block.getZ());
					if (te1 != null && te1 instanceof TileCovered) {
						data = lastdata;
					}
				}
				
			}
			
			String msg = TRNoItem.isItemBanned(player, id, data, true);
			
			if (msg != null) {
				// tekkitrestrict.log.info(cc.id+":"+cc.getData());
				if (msg.equals("")) msg = ChatColor.RED + "[TRItemDisabler] You are not allowed to place down this type of block!";
				TRItem.sendBannedMessage(player, msg);
				event.setCancelled(true);
				
				if (id == 136){
					WorldServer ws = ((CraftWorld) block.getWorld()).getHandle();
					TileEntity te1 = ws.getTileEntity(block.getX(), block.getY(), block.getZ());
					if (te1 instanceof TileCovered) {
						TileCovered tc = (TileCovered) te1;
						for (int i = 0; i < 6; i++) {
							if (tc.getCover(i) != -1 && tc.getCover(i) == data) {
								tc.tryRemoveCover(i);
							}
						}
						tc.updateBlockChange();
					}
				}
			}
			lastdata = block.getData();
		} catch(Exception ex){
			if (!errorBlockPlace){
				Warning.other("An error occurred in the BlockPlace Listener! Please inform the author (This error will only be logged once).", false);
				Log.Exception(ex, false);
				errorBlockPlace = true;
			}
		}
		
	}

	public static boolean errorInteract = false;
	// /////// START INTERACT //////////////
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		
		// lets do this based on a white-listed approach.
		// First, lets loop through the DisableClick list to stop clicks.
		// Perf: 8x
		if (TRNoClick.isDisabled(event)){
			event.setCancelled(true);
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE) {
			ItemStack str = player.getItemInHand();
			if (str != null) {
				String msg = null;
				try {
					msg = TRNoItem.isItemBannedInCreative(player, str.getTypeId(), str.getDurability(), true);
				} catch (Exception ex) {
					if (!errorInteract){
						Warning.other("An error occurred in the InteractListener for LimitedCreative!", false);
						Log.Exception(ex, false);
						errorInteract = true;
					}
				}
				
				if (msg != null) {
					if (msg.equals("")) msg = ChatColor.RED + "[TRLimitedCreative] You may not interact with this item.";
					TRItem.sendBannedMessage(player, msg);
					event.setCancelled(true);
					player.setItemInHand(null);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract2(PlayerInteractEvent event){
		if (TRNoDupeProjectTable.tableUseNotAllowed(event.getClickedBlock(), event.getPlayer())){
			event.getPlayer().sendMessage(ChatColor.RED + "Someone else is already using this project table!");
			event.setCancelled(true);
			return;
		}
	}
	// /////////// END INTERACT /////////////

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();
		if (player == null) return;
		
		TRNoDupeProjectTable.playerUnuse(player.getName());
		TRCommandAlc.setPlayerInv(player, true);
	}
}
