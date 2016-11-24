package nl.taico.tekkitrestrict.functions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.TRConfigCache;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor2;
import nl.taico.tekkitrestrict.TekkitRestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.config.SettingsStorage;
import nl.taico.tekkitrestrict.objects.TRItem;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Permission;
import com.griefcraft.model.Protection;

public class TRLWCProtect {
	public static ArrayList<TRItem> lwcBlocked = new ArrayList<TRItem>();
	
	public static boolean init = false;
	public static void reload(){
		init = false;
		final List<String> blockedList = SettingsStorage.advancedConfig.getStringList("LWCPreventNearLocked");
		final ArrayList<TRItem> temp = new ArrayList<TRItem>();
		for (final String str : blockedList){
			try {
				temp.addAll(TRItemProcessor2.processString(str));
			} catch (TRException ex) {
				Warning.config("You have an error in your Advanced.config.yml in LWCPreventNearLocked:", false);
				Warning.config(ex.getMessage(), false);
				continue;
			}
		}
		lwcBlocked = temp;
	}
	
	private static void init(){
		if (init) return;
		
		if (TekkitRestrict.getInstance().getServer().getPluginManager().getPlugin("LWC") != null){
			TRConfigCache.LWC.lwc = true;
		} else {
			TRConfigCache.LWC.lwc = false;
		}
		init = true;
	}
	
	/**
	 * Checks tekkitrestrict.bypass.lwc permission.
	 * @return False if the event was cancelled.
	 */
	public static boolean checkLWCAllowed(@NonNull BlockPlaceEvent event) {
		init();
		if (!TRConfigCache.LWC.lwc || LWC.getInstance() == null) return true;
		
		final Player player = event.getPlayer();

		if (player.hasPermission("tekkitrestrict.bypass.lwc")) return true;
		
		final Block block = event.getBlock();
		final int id = block.getTypeId();
		final byte data = block.getData();

		String blocked = null;
		for (final TRItem tci : lwcBlocked){
			if (tci.compare(id, data)){
				blocked = (tci.msg == null || tci.msg.isEmpty() ? (ChatColor.RED + "You are not allowed to place this next to a locked block!") : tci.msg);
				break;
			}
		}
		
		if (blocked == null) return true;
		
		final String playername = player.getName().toLowerCase();

		outerloop:
			for (int i=-1;i<2;i++){
				for (int j=-1;j<2;j++){
					for (int k=-1;k<2;k++){
						final Protection prot = LWC.getInstance().getProtectionCache().getProtection(block.getRelative(i, j, k));
						if (prot == null) continue;
						if (prot.isOwner(player)) continue;
						
						for (final Permission pe : prot.getPermissions()) {
							if (pe.getName().equalsIgnoreCase(playername)) continue outerloop;
						}
						
						TRItem.sendBannedMessage(player, blocked);
						event.setCancelled(true);
	
						return false;
					}
				}
			}
		
		return true;
	}
	
	public static boolean isLWCAllowed(PlayerInteractEvent event){
		init();
		if (!TRConfigCache.LWC.lwc || LWC.getInstance() == null) return true;
		
		final Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.lwc")) return true;
		
		final int data = player.getItemInHand().getDurability();
	
		String blocked = null;
		for (final TRItem tci : lwcBlocked){
			if (tci.compare(136, data)){
				blocked = (tci.msg == null || tci.msg.isEmpty() ? (ChatColor.RED + "You are not allowed to place this next to a locked block!") : tci.msg);
				break;
			}
		}
		
		if (blocked == null) return true;
		
		final String playername = player.getName().toLowerCase();
		final Block block = event.getClickedBlock().getRelative(event.getBlockFace());
		
		outerloop:
			for (int i=-1;i<2;i++){
				for (int j=-1;j<2;j++){
					for (int k=-1;k<2;k++){
						final Protection prot = LWC.getInstance().getProtectionCache().getProtection(block.getRelative(i, j, k));
						if (prot == null) continue;
						if (prot.isOwner(player)) continue;
						
						for (final Permission pe : prot.getPermissions()) {
							if (pe.getName().equalsIgnoreCase(playername)) continue outerloop;
						}
						
						TRItem.sendBannedMessage(player, blocked);
						event.setCancelled(true);
	
						return false;
					}
				}
			}
		return true;
	}
}