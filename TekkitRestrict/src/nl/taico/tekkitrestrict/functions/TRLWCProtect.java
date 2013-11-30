package nl.taico.tekkitrestrict.functions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;
import org.eclipse.jdt.annotation.NonNull;

import nl.taico.tekkitrestrict.TRConfigCache;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict.objects.TREnums.ConfigFile;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Permission;
import com.griefcraft.model.Protection;

public class TRLWCProtect {
	public static List<TRItem> lwcBlocked = Collections.synchronizedList(new LinkedList<TRItem>());
	
	public static void reload(){
		List<String> blockedList = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "LWCPreventNearLocked");
		for (String str : blockedList){
			try {
				lwcBlocked.addAll(TRItemProcessor.processItemString(str));
			} catch (TRException ex) {
				Warning.config("You have an error in your Advanced.config.yml in LWCPreventNearLocked:", false);
				Warning.config(ex.getMessage(), false);
				continue;
			}
		}
	}
	
	/**
	 * Checks tekkitrestrict.bypass.lwc permission.
	 * @return False if the event was cancelled.
	 */
	public static boolean checkLWCAllowed(@NonNull BlockPlaceEvent event) {
		if (TRConfigCache.LWC.lwcPlugin == null){
			PluginManager PM = tekkitrestrict.getInstance().getServer().getPluginManager();
			if (PM.isPluginEnabled("LWC")) TRConfigCache.LWC.lwcPlugin = (LWCPlugin) PM.getPlugin("LWC");
			
			if (TRConfigCache.LWC.lwcPlugin == null) return true;
		}
		
		Player player = event.getPlayer();

		if (player.hasPermission("tekkitrestrict.bypass.lwc")) return true;
		
		Block block = event.getBlock();
		int id = block.getTypeId();
		byte data = block.getData();

		String blocked = null;
		for (TRItem tci : lwcBlocked){
			if (tci.compare(id, data)){
				blocked = tci.msg == null ? "" : tci.msg;
				break;
			}
		}
		
		if (blocked == null) return true;
		
		LWC LWC = TRConfigCache.LWC.lwcPlugin.getLWC();
		String playername = player.getName().toLowerCase();
		
		outerloop:
			for (BlockFace bf : BlockFace.values()) {
				Protection prot = LWC.getProtectionCache().getProtection(block.getRelative(bf));
				if (prot == null) continue;
				if (prot.isOwner(player)) continue;
				
				for (Permission pe : prot.getPermissions()) {
					if (pe.getName().toLowerCase().equals(playername)) continue outerloop;
				}
	
				if (blocked.equals("")) blocked = ChatColor.RED + "You are not allowed to place this here!";
				TRItem.sendBannedMessage(player, blocked);
				event.setCancelled(true);

				return false;
			}
		return true;
	}
}