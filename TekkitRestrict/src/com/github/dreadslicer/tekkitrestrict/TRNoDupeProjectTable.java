package com.github.dreadslicer.tekkitrestrict;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TRNoDupeProjectTable {
	private static Map<String,String> UsedTables = new ConcurrentHashMap<String, String>(); //loc, player
	private static Map<String,String> PlayerUsed = new ConcurrentHashMap<String, String>(); //player, loc
	public static void checkTable(org.bukkit.event.player.PlayerInteractEvent e){
		if (!TRPermHandler.hasPermission(e.getPlayer(), "dupe", "bypass", "")) {
			Block blk = e.getClickedBlock();
			Player p = e.getPlayer();
			if(blk != null){
				if(blk.getTypeId() == 137 && blk.getData() == 3){
					Location loc = blk.getLocation();
					String locstr = loc.getWorld().getName().toLowerCase()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
					if(UsedTables.get(locstr) != null){
						String usingPlayer = UsedTables.get(locstr);
						if(!p.getName().toLowerCase().equals(usingPlayer.toLowerCase())){
							e.setCancelled(true);
							p.sendRawMessage("[TRDupe] Somebody else is using this project table!");
							return;
						}
					}
					
					//assign!
					//tekkitrestrict.log.info("Assign table");
					PlayerUsed.put(p.getName().toLowerCase(), locstr);
					UsedTables.put(locstr, p.getName().toLowerCase());
				}
			}
		}
	}
	
	public static void playerUnuse(String player){
		if(PlayerUsed.containsKey(player.toLowerCase())){
			String l = PlayerUsed.get(player.toLowerCase()); //world,x,y,z
			if(l != null){
				UsedTables.remove(l);
				PlayerUsed.remove(player);
				//tekkitrestrict.log.info("Remove table");
			}
		}
	}
}
