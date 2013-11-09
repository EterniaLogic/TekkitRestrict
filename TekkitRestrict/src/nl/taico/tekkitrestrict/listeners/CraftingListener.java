package nl.taico.tekkitrestrict.listeners;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.taico.tekkitrestrict.objects.TRItem;

import com.github.dreadslicer.tekkitrestrict.TRConfigCache;
import com.github.dreadslicer.tekkitrestrict.TRNoItem;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import forge.ICraftingHandler;
import forge.MinecraftForge;

public class CraftingListener {
	public static void setupCraftHook() {
		
		ICraftingHandler var0 = new ICraftingHandler() {
			@SuppressWarnings("deprecation")
			public void onTakenFromCrafting(EntityHuman var1, ItemStack var2, IInventory var3) {
				if (var1 == null || var2 == null || !(var1.getBukkitEntity() instanceof Player)) return;
				
				if (!(var1.getBukkitEntity() instanceof Player)) return;//AutocraftingTable mk2
				Player player = (Player) var1.getBukkitEntity();
				
				String banned = TRNoItem.isItemBanned(player, var2.id, var2.getData(), true);

				if (banned != null){
					if (banned.equals("")) banned = ChatColor.RED + "[TRItemDisabler] This item is banned!";
					TRItem.sendBannedMessage(player, banned);
					var2.id = TRConfigCache.Threads.ChangeDisabledItemsIntoId;
					var2.setData(0);
					var2.count = 1;
					for (int i=0;i<var3.getSize();i++){
						ItemStack item = var3.getItem(i);
						if (item == null) continue;
						HashMap<Integer, org.bukkit.inventory.ItemStack> failed = player.getInventory().addItem(new org.bukkit.inventory.ItemStack(item.id, 1, (short) item.getData()));
						if (!failed.isEmpty()){
							for (org.bukkit.inventory.ItemStack t : failed.values()){
								player.getWorld().dropItem(player.getLocation(), t);
							}
						}
					}
					player.updateInventory();
				}
			}
		};
		MinecraftForge.registerCraftingHandler(var0);
	}
}
