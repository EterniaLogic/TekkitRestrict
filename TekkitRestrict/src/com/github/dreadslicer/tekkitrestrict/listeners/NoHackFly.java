package com.github.dreadslicer.tekkitrestrict.listeners;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.TileEntity;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.dreadslicer.tekkitrestrict.Log;
import com.github.dreadslicer.tekkitrestrict.TRConfigCache.Hacks;
import com.github.dreadslicer.tekkitrestrict.TRNoHack;
import com.github.dreadslicer.tekkitrestrict.TRNoHack.HackType;
import com.github.dreadslicer.tekkitrestrict.Util;

public class NoHackFly implements Listener {
	private static ConcurrentHashMap<String, Integer> tickTolerance = new ConcurrentHashMap<String, Integer>(),
			tickLastLoc = new ConcurrentHashMap<String, Integer>();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void handleFly(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (Util.hasHackBypass(player, "fly")) return;
		if (player.getGameMode() == GameMode.CREATIVE) return;
		if (!isFlying(player)) return;
		//if (Util.hasHackBypass(player, "fly")) return;
		
		groundPlayer(player);
		TRNoHack.handleHack(player, HackType.fly);
	}

	/**
	 * @return If the player is flying.
	 */
	public static boolean isFlying(Player player) {
		int flyTolerance = Hacks.flyTolerance;
		PlayerInventory inventory = player.getInventory();
		
		ItemStack boots = inventory.getBoots();
		if (boots != null){
			//checks if the player is wearing boots before deciding whether or not they are flyhacking
			if (boots.getTypeId() == 30171 && (inventory.getBoots().getData().getData() < 27)) { //wearing quantum boots. checks for charge
		    	//if charged boots, increase fly tolerance. *10 is just an example
				flyTolerance *= 5;
		    } else if (boots.getTypeId() == 27582) { //checks for hurricane boots
		    	return false; //has flyItem
		    }
		}
		
		ItemStack chest = inventory.getChestplate();
		if (chest != null){
			//jetpack check
			if (chest.getTypeId() == 30209) {
				int data = chest.getData().getData();
				if (data <= 25 && data != 0) return false; //Fuel check
				
			} else if (chest.getTypeId() == 30210) {
				int data = chest.getData().getData();
				if (data < 18000 && data != 0) return false; //Fuel check
				
			}
		}
		
		for (int i = 0; i<=8; i++){ //Ring on hotbar check
			ItemStack item = inventory.getItem(i);
			if (item == null) continue;
			int id = item.getTypeId();
			if (id == 27536 || id == 27584) return false;
		}
		
		LinkedList<Integer> nearBlocks = new LinkedList<Integer>();
		nearBlocks.add(220);//Scaffold
		nearBlocks.add(235);//Iron scaffold
		nearBlocks.add(212);//Ladder rail
		nearBlocks.add(106);//Vine
		nearBlocks.add(65);//Ladder
		
		EntityPlayer Eplayer = ((CraftPlayer) player).getHandle();
		if (player.isInsideVehicle()) return false;
		if (Eplayer.vehicle != null) {
			Log.Debug(ChatColor.RED + "player.isInsideVehicle()==false, but Eplayer.vehicle != null!");
			return false;
		}
		
		String name = player.getName();
		if (!Eplayer.abilities.isFlying) {
			if (!player.isSneaking()) {
				int x = player.getLocation().getBlockX();
				int z = player.getLocation().getBlockZ();
				int y = player.getLocation().getBlockY();
				// checks min height...
				boolean flight = true;
				for (int j = 0; j < Hacks.flyMinHeight + 1; j++) {
					Block b1 = player.getWorld().getBlockAt(x, y, z);//Get the block at the players position.
					if (!b1.isEmpty()){
						flight = false; //If there is a block, flight = false.
						break;
					}
					
					TileEntity te1 = Eplayer.world.getTileEntity(x, y, z);
					if (te1 != null){
						flight = false;
						break;
					}
					
					y--;
				}
				
				if (flight) {
					Integer ticks = tickTolerance.get(name);
					if (ticks == null) ticks = 1;
					else ticks = ticks + 1;
					tickTolerance.put(name, ticks);//Make if not exist, increase otherwise.
					
					Integer oldY = tickLastLoc.get(name);
					
					int velo, playery = player.getLocation().getBlockY();
					if (oldY == null)
						velo = 0;
					else
						velo = playery - oldY;
					
					tickLastLoc.put(name, playery);
					
					if (velo != 0) Log.Debug("velo: " + velo);

					// they are constant 0 or are going upwards
					if (velo >= 0) {
						Block cb = player.getWorld().getBlockAt(player.getLocation());
						for (BlockFace bf : BlockFace.values()) {
							if (!nearBlocks.contains(cb.getRelative(bf).getTypeId())) continue;
							resetScore(name);
							return false;
						}

						if (ticks >= flyTolerance) {
							resetScore(name);
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				}
			}
			resetScore(name); // is not flying.
			return false;
		} else {
			resetScore(name);
			return true;
		}
	}
	
	private static void resetScore(String name){
		tickTolerance.remove(name);
		tickLastLoc.remove(name);
	}

	public static void clearMaps() {
		tickTolerance.clear();
		tickLastLoc.clear();
	}

	public static void playerLogout(String playerName) {
		tickTolerance.remove(playerName);
		tickLastLoc.remove(playerName);
	}
	
	/**
	 * Teleport the player to the highest block on the ground.
	 */
	private static void groundPlayer(Player player) {
		Block highest = player.getWorld().getHighestBlockAt(player.getLocation());
		player.teleport(highest.getLocation());
	}
}