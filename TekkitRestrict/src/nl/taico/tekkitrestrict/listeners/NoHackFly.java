package nl.taico.tekkitrestrict.listeners;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.EntityPlayer;

import org.bukkit.GameMode;
import org.bukkit.Location;
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

import nl.taico.tekkitrestrict.TRConfigCache.Hacks;
import nl.taico.tekkitrestrict.functions.TRNoHack;
import nl.taico.tekkitrestrict.objects.TREnums.HackType;
import nl.taico.tekkitrestrict.objects.TRItemStack;

public class NoHackFly implements Listener {
	private static ConcurrentHashMap<String, Integer> tickTolerance = new ConcurrentHashMap<String, Integer>();
	private static ConcurrentHashMap<String, Double> tickLastLoc = new ConcurrentHashMap<String, Double>();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void handleFly(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("tekkitrestrict.bypass.hack.fly")) return;
		if (player.getGameMode() == GameMode.CREATIVE) return;
		if (!isFlying(player)) return;
		//if (Util.hasHackBypass(player, "fly")) return;
		
		groundPlayer(player);
		TRNoHack.handleHack(player, HackType.fly);
	}

	private static ArrayList<Integer> nearBlocks = new ArrayList<Integer>();
	static {
		nearBlocks.add(220);//Scaffold
		nearBlocks.add(235);//Iron scaffold
		nearBlocks.add(212);//Ladder rail
		nearBlocks.add(106);//Vine
		nearBlocks.add(65);//Ladder
	}
	
	/**
	 * @return If the player is flying.
	 */
	public static boolean isFlying(Player player) {
		int flyTolerance = Hacks.fly.tolerance;
		PlayerInventory inventory = player.getInventory();
		ItemStack boots = inventory.getBoots();
		int minHeight = (int) Hacks.fly.value;
		if (boots != null){
			//checks if the player is wearing boots before deciding whether or not they are flyhacking
			if (boots.getTypeId() == 30171 && (inventory.getBoots().getDurability() < 27)) { //wearing quantum boots. checks for charge
				minHeight += 12;//10 is required for this to work
		    } else if (boots.getTypeId() == 27582) { //hurricane boots
		    	return false; //has flyItem
		    }
		}
		
		ItemStack chest = inventory.getChestplate();
		if (chest != null){
			//jetpack check
			if (chest.getTypeId() == 30209 || chest.getTypeId() == 30210) {
				if (TRItemStack.getJetpackCharge(chest)>0) return false;
			}
		}
		
		for (int i = 0; i<=8; i++){ //Ring on hotbar check
			ItemStack itemStack = inventory.getItem(i);
			if (itemStack == null) continue;
			int id = itemStack.getTypeId();
			
			if (id == 27536 || id == 27584) return false;
			
			/*
			if (id == 27536){
				NBTTagCompound tag = ((CraftItemStack)itemStack).getHandle().tag;
				if (tag == null){
					if ((itemStack.getData().getData() & 1) == 1) return false;
				} else {
					if (tag.getBoolean("active")) return false;
					if ((itemStack.getData().getData() & 1) == 1) return false;
				}
			}
			else if (id == 27584) return false;
			*/
		}
		
		EntityPlayer Eplayer = ((CraftPlayer) player).getHandle();
		if (player.isInsideVehicle()) return false;
		
		String name = player.getName();
		if (!Eplayer.abilities.isFlying) {
			if (!player.isSneaking()) {
				Location loc = player.getLocation();
				int x = loc.getBlockX();
				int z = loc.getBlockZ();
				int y = loc.getBlockY();
				// checks min height...
				boolean flight = true;
				for (int j = 0; j <= minHeight; j++) {
					Block b1 = player.getWorld().getBlockAt(x, y, z);//Get the block at the players position.
					if (!b1.isEmpty()){
						flight = false; //If there is a block, flight = false.
						break;
					}
					
					y--;
				}
				
				if (flight) {
					Double oldY = tickLastLoc.get(name);
					
					double velo, playery = loc.getY();
					if (oldY == null)
						velo = 0;
					else
						velo = playery - oldY;
					
					tickLastLoc.put(name, playery);
					
					//if (velo != 0) tekkitrestrict.log.info("[DEBUG] velo: " + velo);

					// they are constant 0 or are going upwards
					if (velo >= 0) {
						Block cb = loc.getBlock();
						for (BlockFace bf : BlockFace.values()) {
							if (!nearBlocks.contains(cb.getRelative(bf).getTypeId())) continue;
							lowerScore(name, 1);
							return false;
						}
						
						Integer ticks = tickTolerance.get(name);
						if (ticks == null) ticks = 1;
						else ticks = ticks + 1;
						
						if (ticks >= flyTolerance) {
							resetScore(name);
							return true;
						} else {
							tickTolerance.put(name, ticks);//Make if not exist, increase otherwise.
							return false;
						}
					} else {
						return false;
					}
				} else {
					lowerScore(name, 1);
					return false;
				}
			}
			return false;
		} else {//Flying (dropped ring)
			Block cb = player.getLocation().getBlock();
			for (BlockFace bf : BlockFace.values()) {
				if (!cb.getRelative(bf).isLiquid()) continue;
				lowerScore(name, 1);
				return false;
			}
			
			Integer ticks = tickTolerance.get(name);
			if (ticks == null) ticks = 1;
			else ticks = ticks + 1;
			
			if (ticks >= flyTolerance) {
				resetScore(name);
				return true;
			} else {
				tickTolerance.put(name, ticks);//Make if not exist, increase otherwise.
				return false;
			}
		}
	}
	
	private static void lowerScore(String name, int amount){
		Integer ticks = tickTolerance.get(name);
		if (ticks == null) ticks = 0;
		else if (ticks > 0) ticks = ticks - amount;
		else if (ticks == 0) return;
		
		if (ticks < 0 ) ticks = 0;
		tickTolerance.put(name, ticks);
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