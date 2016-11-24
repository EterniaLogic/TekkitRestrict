package nl.taico.tekkitrestrict.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRConfigCache.Dupes;
import nl.taico.tekkitrestrict.TRConfigCache.Listeners;
import nl.taico.tekkitrestrict.functions.TRNoClick;
import nl.taico.tekkitrestrict.functions.TRNoDupe;
import nl.taico.tekkitrestrict.functions.TRNoItem;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TREnums.DupeType;

public class InventoryClickListener implements Listener {
	static boolean doDupeCheck = false;
	boolean logged = false;
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void onInventoryClick(InventoryClickEvent event){
		if (event.getWhoClicked() == null) return;
		
		final Player player = (Player) event.getWhoClicked();
		
		String blocked = TRNoClick.blockClick(event);
		if (blocked != null){
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "[NoClick] Not allowed: " + (blocked.isEmpty() ? "you are not allowed to do this!" : blocked));
			return;
		}
		
		
		final ItemStack currentItem;
		
		try {
			currentItem = event.getCurrentItem();
		} catch (Exception ex){
			if (!logged){
				Warning.other("An error occured in the InventoryClick Listener: "+ex.toString() + " (This error will only be logged once)", false);
				Log.debugEx(ex);
				logged = true;
			}
			return;
		}
		
		final int id1;
		final int data1;
		try {
			if (currentItem != null){
				id1 = currentItem.getTypeId();
				data1 = currentItem.getDurability();
				
				if (player.getGameMode() == GameMode.CREATIVE){
					String banned = null;
					if (Listeners.UseLimitedCreative && !player.hasPermission("tekkitrestrict.bypass.creative")){
						if (Listeners.BlockCreativeContainer && !"container.inventory".equals(event.getView().getTopInventory().getName())){
							player.sendMessage(ChatColor.RED + "You may not interact with other inventories in creative mode.");
							event.setCancelled(true);
							return;
						}
						
						banned = TRNoItem.isItemBannedInCreative(player, id1, data1, false);
					}
					
					if (banned == null) banned = TRNoItem.isItemBanned(player, id1, data1, true);
					
					if (banned != null){
						final String msg = ChatColor.RED + "This item is banned!" + (banned.isEmpty() ? "" : " Reason: "+ChatColor.RESET+banned);
						TRItem.sendBannedMessage(player, msg);
						if (event.getRawSlot() == -1) event.setResult(null);
						else 						  event.setCancelled(true);
						return;
					}
				} else {
					String banned = TRNoItem.isItemBanned(player, id1, data1, true);
					if (banned != null){
						final String msg = ChatColor.RED + "This item is banned!" + (banned.isEmpty() ? "" : " Reason: "+ChatColor.RESET+banned);
						TRItem.sendBannedMessage(player, msg);
						event.setCancelled(true);
						return;
					}
				}
			} else {
				id1 = 0;
				data1 = 0;
			}
			
			if (!doDupeCheck) return;
			
			final ItemStack cursor = event.getCursor();
			final int id2;
			
			if (cursor != null) id2 = cursor.getTypeId();
			else id2 = 0;
			
			final String title = event.getView().getTopInventory().getTitle();
			if (title == null) return;
			
			switch (title.toLowerCase()){
				case "rm furnace":
				{
					if (Dupes.rmFurnace.prevent && event.getSlot() == 35 && event.isShiftClick()){
						//if (!player.hasPermission("tekkitrestrict.bypass.dupe.rmfurnace")){
							event.setCancelled(true);
							player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click into a Red Matter Furnace from this slot!");
							TRNoDupe.handleDupe(player, DupeType.rmFurnace, id1, data1);
						//}
					}
					return;
				}
				case "tank cart":
				{
					final int rawslot = event.getRawSlot();
					
					if (event.getSlot() == 35 || rawslot == 29) {
						if (Dupes.tankcart.prevent && event.isShiftClick()){ //&& !player.hasPermission("tekkitrestrict.bypass.dupe.tankcart")){
								event.setCancelled(true);
								player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click into a Tank Cart from this slot!");
								TRNoDupe.handleDupe(player, DupeType.tankCart, id1, data1);
						}
					} else if (rawslot>29 && rawslot<39){
						if (event.isShiftClick() && Dupes.tankcartGlitch.prevent){
							event.setCancelled(true);
							player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click into a Tank Cart!");
							TRNoDupe.handleDupe(player, DupeType.tankCartGlitch, id1, data1);
						}
					}
					return;
				}
				case "trans tablet":
				{
					if (Dupes.transmute.prevent && event.isShiftClick()) {
						final int slot = event.getSlot();
						if (slot>=0 && slot<8 && !player.hasPermission("tekkitrestrict.bypass.dupe.transtablet")){
							event.setCancelled(true);
							player.sendMessage(ChatColor.DARK_RED + "You are not allowed to Shift+Click any item out of the Tranmutation Table(t)!");
							TRNoDupe.handleDupe(player, DupeType.transmution, id1, data1);
						}
					}
					return;
				}
				case "bag":
				{
					if (Dupes.alcBag.prevent && (id1 == 27532 || id1 == 27593 || id2 == 27532 || id2 == 27593)){
						if (!player.hasPermission("tekkitrestrict.bypass.dupe.alcbag")){
							event.setCancelled(true);
							if (id1 == 27532){
								player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Black Hole Bands in an alchemy bag!");
							} else if (id1 == 27593){
								player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Void Rings in an alchemy bag!");
							} else {
								player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Black Hole Bands or Void Rings in an alchemy bag!");
							}
							TRNoDupe.handleDupe(player, DupeType.alcBag, id1, data1);
						}
					}
					return;
				}
				case "pedestal":
				{
					if (Dupes.pedestal.prevent && (id1 == 27537 || id2 == 27537)){
						if (!player.hasPermission("tekkitrestrict.bypass.dupe.pedestal")){
							event.setCancelled(true);
							player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put Harvest rings on a pedestal!");
							TRNoDupe.handleDupe(player, DupeType.pedestal, id1, data1);
						}
					}
					return;
				}
				case "disk drive":
				{
					if (Dupes.diskdrive.prevent && (id1 == 4256 || id1 < 2256 || id1 > 2266 )){
						if (!player.hasPermission("tekkitrestrict.bypass.dupe.diskdrive")){
							event.setCancelled(true);
							player.sendMessage(ChatColor.DARK_RED + "You are not allowed to put anything but floppy and music disks into disk drives!");
							TRNoDupe.handleDupe(player, DupeType.diskdrive, id1, data1);
						}
					}
					return;
				}
			}
		} catch (Exception ex){
			Warning.other("Error in the Inventory Click Handler!", false);
			Log.debugEx(ex);
		}
	}
	
}
