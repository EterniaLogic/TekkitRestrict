package com.github.dreadslicer.tekkitrestrict;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.TileEntity;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.dreadslicer.tekkitrestrict.TRNoHack.HackType;

public class TRHandleFly {
	private static Map<Player, Integer> tickTolerance = new ConcurrentHashMap<Player, Integer>(),
			tickLastLoc = new ConcurrentHashMap<Player, Integer>();
	private static boolean enabled = true;
	private static int flyTolerance = 10, flyMinHeight = 1;

	public static void reload() {
		flyTolerance = tekkitrestrict.config.getInt("HackFlyTolerance");
		flyMinHeight = tekkitrestrict.config.getInt("HackFlyMinHeight");
		enabled = tekkitrestrict.config.getBoolean("UseAntiFlyHack");
	}

	public static void handleFly(PlayerMoveEvent e) {
		net.minecraft.server.EntityPlayer player = ((CraftPlayer) e.getPlayer())
				.getHandle();
		// lets determine if they are flying...
		// Are they crouching?
		// Are they falling?
		// Are they 2 blocks above?
		// tekkitrestrict.log.info("log it1");
		boolean flying = isFlying(e.getPlayer());
		if (flying) {
			TRLimitFly.setFly(e.getPlayer());
			TRLimitFly.willGround(e.getPlayer());
			TRSafeZone.setFly(e);
			// ok, so we know that they are flying... but...
			// do they have permissions to fly?!?!
			// tekkitrestrict.log.info("is flying?");
			if (e.isCancelled()) {
				return;
			}
			if (!e.getPlayer().isOp() && !player.abilities.canFly) {
				if (!TRPermHandler.hasPermission(e.getPlayer(), "hack",
						"bypass", "")) {
					
					     
					
					// tekkitrestrict.log.info("fly!");

					// tekkitrestrict.log.info("fly?");
					//if (!hasFlyItem) {
						// ok, so they are flying without permission!
						// Ground them!
						if (enabled) {
							TRNoHack.groundPlayer(e.getPlayer());
							TRNoHack.handleHack(e.getPlayer(), HackType.fly);
						}
					//} else {
						// flight time!

						// TRLimitFly.willGround(e.getPlayer());
					//}
				}
			}
		} else {
			TRLimitFly.setGrounded(e.getPlayer());
		}
	}

	public static boolean isFlying(Player e) {
		PlayerInventory pi = e.getPlayer().getInventory();
		ItemStack[] iss = pi.getContents();
		int data = 0;
		
		data = pi.getBoots().getData().getData();
		if(pi.getBoots() !=null) { //checks if the player is wearing boots before deciding whether or not they are flyhacking
			int boots = pi.getBoots().getTypeId();
			
			if (boots == 30171 && (data < 27)) { //wearing quantum boots. checks for charge
		    	//if charged boots, increase fly tolerance. *10 is just an example
				flyTolerance *= 2;
		    } else if (boots == 27582) { //checks for hurricane boots
		    	return false; //has flyItem
		    }
		}
		
		if (pi.getChestplate() != null) { //jetpack check
			int chest = pi.getChestplate().getTypeId();
			data = pi.getChestplate().getData().getData();
			if (chest == 30209) {
				if (data <= 25 && data != 0) { // fuel check
					return false; //has flyItem
				}
			} else if (chest == 30210) {
				if (data < 18000 && data != 0) { // fuel check
					return false; //has flyItem
				}
			}
		}
		
		for (ItemStack s : iss) { //ring check
			if (s != null) {
				int id = s.getTypeId();
				if (id == 27536 || id == 27584) {
					return false; //has flyItem
				}
			}
		}
		
		
		List<Integer> nearBlocks = new LinkedList<Integer>();
		nearBlocks.add(220);
		nearBlocks.add(235);
		nearBlocks.add(212);
		net.minecraft.server.EntityPlayer player = ((CraftPlayer) e.getPlayer())
				.getHandle();
		if(player.vehicle instanceof net.minecraft.server.EntityBoat) return false;
		if (!player.abilities.isFlying && player.vehicle == null) {
			if (!e.isSneaking()) {
				int x = e.getLocation().getBlockX();
				int z = e.getLocation().getBlockZ();
				int y = e.getLocation().getBlockY();
				// checks min height...
				boolean flight = true;
				for (int j = 0; j < flyMinHeight + 1; j++) {
					Block b1 = e.getWorld().getBlockAt(x, y, z);
					TileEntity te1 = player.world.getTileEntity(x, y, z);
					// tekkitrestrict.log.info((!b1.isEmpty())+" - "+(te1!=null));
					if (!b1.isEmpty()) {
						flight = false;
					} else if (te1 != null) {
						flight = false;
					}
					y--;
				}
				y = e.getLocation().getBlockY();
				// tekkitrestrict.log.info("log it 2 "+b1.isEmpty()+":"+b2.isEmpty());

				if (flight) {
					tickTolerance.put(e, tickTolerance.get(e) == null ? 1
							: tickTolerance.get(e) + 1);
					// compare last downward velocity verses the current.
					int playery = e.getPlayer().getLocation().getBlockY();
					int yold = tickLastLoc.get(e) != null ? tickLastLoc.get(e)
							: playery;
					int ynew = playery;
					int velo = ynew - yold;
					// tekkitrestrict.log.info("log it 3 velo="+velo);
					tickLastLoc.put(e, e.getLocation().getBlockY());

					// they are constant 0 or are going upwards
					if (velo >= 0) {
						boolean isClimbing = false;
						// determine if a player is right next to some scaffold.
						Block cb = e.getWorld().getBlockAt(e.getLocation());
						for (BlockFace bf : BlockFace.values()) {

							if (nearBlocks.contains(cb.getRelative(bf)
									.getTypeId())) {
								isClimbing = true;
							}
						}

						if (isClimbing) {
							tickTolerance.put(e, 0);
						}

						if (tickTolerance.get(e) >= flyTolerance && !isClimbing) {
							tickTolerance.remove(e);
							return true;
						} else {
							return false; // wait for tolerance to override
						}
					} else {
						return false; // downward velocity is not constant
					}
				}
			}
			tickTolerance.remove(e); // is not flying.
			tickLastLoc.remove(e);
			return false;
		} else {
			return true;
		}
	}

	public static void clearMaps() {
		// flushes the fly locator map.
		tickTolerance.clear();
		tickLastLoc.clear();
	}

	public static void playerLogout(Player player) {
		if (tickTolerance.containsKey(player)) {
			tickTolerance.remove(player);
		}
		if (tickLastLoc.containsKey(player)) {
			tickLastLoc.remove(player);
		}
	}
}
