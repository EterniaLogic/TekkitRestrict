package nl.taico.tekkitrestrict.listeners;

import ic2.api.IWrenchable;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class WrenchFixer implements Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event){
		final ItemStack item = event.getPlayer().getItemInHand();
		if ((item == null) || ((item.getTypeId() != 30183) && (item.getTypeId() != 30140))) return;
		final Block block = event.getBlock();
		if (!(((CraftWorld) block.getWorld()).getHandle().getTileEntity(block.getX(), block.getY(), block.getZ()) instanceof IWrenchable)) event.setCancelled(true);
	}
}
