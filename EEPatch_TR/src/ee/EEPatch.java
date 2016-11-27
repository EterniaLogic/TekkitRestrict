package ee;

import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import forge.DimensionManager;

import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import net.minecraft.server.EntityHuman;

public class EEPatch {
	public static double version = 2.5D;
	public static int alcBagAmount = 15;
	public static boolean allowAlcBags = true;
	public static boolean separateInvs = false;
	public static boolean applySidePatch = false;
	public static String mainSharedWorld = "";
	public static int transTableInterval;
	public static boolean fixUnderscore = true;
	
	public static HashMap<String, String> shareGroups = new HashMap<>();
	
	public static EEProps InitProps(EEProps props) {
		int alcbag = props.getInt("EEPatch_AllowAlchemyBags", 1);
		if (alcbag == 0){
			allowAlcBags = false;
			alcBagAmount = 0;
		} else {
			alcBagAmount = EEBase.props.getInt("EEPatch_AlchemyBagAmount", 16)-1;
		}
		
		int hadSeparate = props.getInt("EEPatch_SeparateAlcBagsPerWorld", 2);
		boolean upgraded = hadSeparate != 2;
		fixUnderscore = props.getInt("EEPatch_FixAlcBagSaveFormat", upgraded ? 0 : 1) == 1;
		
		separateInvs = props.getInt("EEPatch_SeparateInvsPerWorld", hadSeparate == 2 ? 0 : hadSeparate) == 1;
		
		if (separateInvs) parseGroups(props);
		
//		String shared = props.func_26599_getString("EEPatch_AlcBag_Shared_Worlds", "");
//		if (!shared.isEmpty()){
//			String[] w = shared.split(",");
//			mainSharedWorld = w[0].trim();
//			for (String s : w){
//				sharedWorlds.add(s.trim().toLowerCase());
//			}
//		}
		applySidePatch = props.getInt("EEPatch_ApplyCondenserSidePatch", 1) == 1;
		transTableInterval = props.getInt("EEPatch_TransmutionTableInterval", 10);
		if (transTableInterval < 1) transTableInterval = 1;

		return props;
	}
	
	public static void parseGroups(EEProps props){
		String groups = props.func_26599_getString("EEPatch_Shared_Groups", "");
		if (groups.isEmpty()){
			
			return;
		}
		
		for (String group : groups.split(",")){
			group = group.trim();
			String worlds = props.func_26599_getString("EEPatch_Shared_Group_"+group, "").toLowerCase();
			
			for (String world : worlds.split(",")){
				world = world.trim().toLowerCase();
				shareGroups.put(world, group);
			}
		}
		//Survival,PvP,Hub
	}
	
	public static String getBag(EntityHuman player, World world, int color){
		if (!EEPatch.allowAlcBags) return "bag_global";
		
		if (color > alcBagAmount){
			player.a("You are not allowed to have more than " + (alcBagAmount+1) + " different bags!");
			color = alcBagAmount;
		}
		
		if (!separateInvs){
			if (fixUnderscore) return "bag_"+player.name+"_"+color;
			else			   return "bag_"+player.name+color;
		}
		
		String sg = shareGroups.get(world.worldData.name.toLowerCase());
		if (sg == null) sg = world.worldData.name.toLowerCase();
		
		if (fixUnderscore) return "bag_"+sg+"_"+player.name+"_"+color;
		else			   return "bag_"+sg+"_"+player.name+color;
	}
	
	public static String getBag(EntityHuman player, World world, ItemStack item){
		if (!allowAlcBags) return "bag_global";
		
		if (item.getData() > alcBagAmount){
			player.a("You are not allowed to have more than " + (alcBagAmount+1) + " different bags!");
			item.setData(alcBagAmount);
		}
		
		if (!separateInvs){
			if (fixUnderscore) return "bag_"+player.name+"_"+item.getData();
			else			   return "bag_"+player.name+item.getData();
		}
		
		String sg = shareGroups.get(world.worldData.name.toLowerCase());
		if (sg == null) sg = world.worldData.name.toLowerCase();
		if (fixUnderscore) return "bag_"+sg+"_"+player.name+"_"+item.getData();
		else			   return "bag_"+sg+"_"+player.name+item.getData();
	}
	
	public static TransTabletData getTransData(EntityHuman human) {
		String var1;
		if (separateInvs){
			String sg = shareGroups.get(human.world.worldData.name.toLowerCase());
			if (sg == null) sg = human.world.worldData.name.toLowerCase();
			var1 = "tablet_"+sg+"_"+human.name;
		} else {
			var1 = "tablet_" + human.name;
		}

		World world = DimensionManager.getWorld(0);
		TransTabletData var2 = (TransTabletData) world.a(TransTabletData.class, var1);

		if (var2 == null) {
			var2 = new TransTabletData(var1);
			var2.a();
			world.a(var1, var2);
		}

		return var2;
	}
	
	public static String getEye(EntityHuman human, World world){
		if (!separateInvs) return "eye_"+human.name;
		
		String sg = shareGroups.get(world.worldData.name.toLowerCase());
		if (sg == null) sg = world.worldData.name.toLowerCase();
		return "eye_"+sg+"_"+human.name;
	}
	
	public static boolean attemptBreak(EntityHuman player, int x, int y, int z){
		if (player == null) return false;
		
		CraftWorld craftWorld = player.world.getWorld();
		CraftServer craftServer = player.world.getServer();
		Block block = craftWorld.getBlockAt(x, y, z);
		if(block == null) return false;
		Player ply = (Player) player.getBukkitEntity();
		//Player ply = craftServer.getPlayer((EntityPlayer)player);
		if(ply != null)
		{
			BlockBreakEvent event = new BlockBreakEvent(block, ply);
			craftServer.getPluginManager().callEvent(event);
			return !event.isCancelled();
		}
		return false;
	}

	public static boolean attemptPlace(EntityHuman player, int x, int y, int z) {
		if(player == null) return false;
		CraftWorld craftWorld = player.world.getWorld();
		CraftServer craftServer = player.world.getServer();
		Player ply = (Player) player.getBukkitEntity();

		BlockState state = CraftBlockState.getBlockState(player.world, x, y, z);
		Block placedagainst = craftWorld.getBlockAt(x, y - 1, z);
		
		if(ply != null)
		{
			BlockPlaceEvent event = new BlockPlaceEvent(state.getBlock(), state, placedagainst, ply.getItemInHand(), ply, true);
			craftServer.getPluginManager().callEvent(event);
			return !event.isCancelled();
		}
		return false;
	}
	
	/**
	 * @return The version of EEPatch.
	 */
	public static double getVersion(){
		return version;
	}
}
