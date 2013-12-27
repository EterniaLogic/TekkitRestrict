package nl.taico.tekkitrestrict.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import nl.taico.tekkitrestrict.Log;
import nl.taico.tekkitrestrict.tekkitrestrict;
import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.functions.TRLimiter;

import net.minecraft.server.TileEntity;
import ee.TileAlchChest;

public class BlockBreakListener implements Listener{
	/** @return <b>True</b> if id < 8 or id = 12, 13, 17, 24, 35, 44, 98 or 142. <b>False</b> otherwise. */
	private static boolean exempt(int id){
		return (id < 8 || id == 12 || id == 13 || id == 17 || id == 24 || id == 35 || id == 44 || id == 98 || id == 142);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		final int id = event.getBlock().getTypeId();
		if (exempt(id)) return;
		final Block block = event.getBlock();
		final byte data = block.getData();
		if (id == 128 && data == 0){
			TileEntity entity = ((CraftWorld) block.getWorld()).getTileEntityAt(block.getX(), block.getY(), block.getZ());
			if (entity instanceof TileAlchChest){
				for (HumanEntity h : ((TileAlchChest) entity).getViewers()){
					h.closeInventory();
				}
			}
			/*
			event.setCancelled(true);
			
			if (!(entity instanceof TileAlchChest)) return;
			TileAlchChest chest = (TileAlchChest) entity;
			
			ItemStack[] items = new ItemStack[chest.getSize()];
			for (int i =0; i<chest.getSize(); i++){
				items[i] = chest.getItem(i);
				chest.setItem(i, null);
			}
			
			block.setType(Material.AIR);
			for (ItemStack i : items){
				if (i != null && i.id != 0){
					block.getWorld().dropItemNaturally(block.getLocation(), new CraftItemStack(i));
				}
			}
			
			block.getWorld().dropItemNaturally(block.getLocation(), new org.bukkit.inventory.ItemStack(128, 1, (short) 0));
			*/
		}
		
		Player player = event.getPlayer();
		if (player == null) return;
		
		String pname = player.getName();
		if (pname.startsWith("[ComputerCraft] Turtle") && id == 194 && event.getBlock().getData() == 1){
			event.setCancelled(true);
			return;
		}
		Bukkit.getScheduler().scheduleAsyncDelayedTask(tekkitrestrict.getInstance(), new Runnable(){
			public void run(){
				try {
					String blockPlayerName = TRLimiter.getPlayerAt(block);
					if (blockPlayerName != null) {
						TRLimiter il = TRLimiter.getLimiter(blockPlayerName);
						il.checkBreakLimit(id, data, block.getLocation());
					}
				} catch(Exception ex){
					Warning.other("Exception in '+BlockBreakListener.onBlockBreak(...)'!", false);
					Log.Exception(ex, false);
				}
			}
		});
	}
}
