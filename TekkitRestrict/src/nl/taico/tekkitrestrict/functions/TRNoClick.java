package nl.taico.tekkitrestrict.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import nl.taico.tekkitrestrict.Log.Warning;
import nl.taico.tekkitrestrict.TRException;
import nl.taico.tekkitrestrict.TRItemProcessor;
import nl.taico.tekkitrestrict.objects.TRItem;
import nl.taico.tekkitrestrict2.SettingsStorage;

public class TRNoClick {
	public static HashMap<String, String> inventories = new HashMap<String, String>();
	static {
		inventories.put("Chest", "container.chest");
		inventories.put("MinecartChest", "container.minecart");
		inventories.put("BrewingStand", "container.brewing");
		inventories.put("EnchantingTable", "Enchant");
		inventories.put("Furnace", "container.furnace");
		inventories.put("Dispenser", "container.dispenser");
		inventories.put("Inventory", "container.crafting");

		inventories.put("Alchemical Chest", "Chest");
		inventories.put("Condenser", "Condenser");
		inventories.put("DMFurnace", "DM Furnace");
		inventories.put("RMFurnace", "RM Furnace");
		inventories.put("Collector", "Energy Collector");
		inventories.put("EERelay", "AM Array");
		inventories.put("Pedestal", "Pedestal");
		inventories.put("Transmution Table", "Trans Tablet");
		inventories.put("MercurialEye", "Mercurial Eye");
		inventories.put("Alchemy Bag", "Bag");

		inventories.put("Builder", "Builder");
		inventories.put("Filler", "Filler");
		inventories.put("Template", "Template");
		inventories.put("Engine", "Engine");
		inventories.put("AutoWorkbench", "");

		inventories.put("AutoCraftingTable2", "ACT Mk II");

		inventories.put("AlloyFurnace", "AlloyFurnace");
		inventories.put("BlueAlloyFurnace", "BlueAlloyFurnace");
		inventories.put("BlulectricFurnace", "BlueFurnace");
		inventories.put("Buffer", "Buffer");
		inventories.put("Deployer", "Deployer");
		inventories.put("Ejector", "Ejector");
		inventories.put("Filter", "Filter");
		inventories.put("Retriever", "Retriever");
		inventories.put("Sorter", "Sorter");
		inventories.put("ItemDetector", "Item Detector");
		inventories.put("Regulator", "Regulator");
		inventories.put("RPRelay", "Relay");
		inventories.put("ProjectTable", "Project Table");

		inventories.put("CokeOven", "Coke Oven");
		inventories.put("BlastFurnace", "Blast Furnace");
		inventories.put("CartDispenser", "Cart Dispenser");
		inventories.put("EnergyLoader", "Energy Loader");
		inventories.put("EnergyUnloader", "Energy Unloader");
		inventories.put("LiquidLoader", "Liquid Loader");
		inventories.put("LiquidUnloader", "Liquid Unloader");
		inventories.put("ItemLoader", "Item Loader");
		inventories.put("AdvItemLoader", "Adv. Loader");
		inventories.put("AdvItemUnloader", "Adv. Unloader");
		inventories.put("ItemUnloader", "Item Unloader");
		inventories.put("RollingMachine", "Rolling Machine");

		inventories.put("CanningMachine", "Canning Machine");
		inventories.put("Compressor", "Compressor");
		inventories.put("CropMatron", "Crop-Matron");
		inventories.put("Cropnalyzer", "Cropnalyzer");
		inventories.put("ElectricFurnace", "Electric Furnace");
		inventories.put("Electrolyzer", "Electrolyzer");
		inventories.put("Extractor", "Extractor");
		inventories.put("Generator", "Generator");
		inventories.put("GeothermalGenerator", "Geoth. Generator");
		inventories.put("InductionFurnace", "InductionFurnace");
		inventories.put("IronFurnace", "Iron Furnace");
		inventories.put("Macerator", "Macerator");
		inventories.put("MassFabricator", "Mass Fabricator");
		inventories.put("Miner", "Miner");
		inventories.put("NuclearReactor", "Nuclear Reactor");
		inventories.put("PersonalSafe", "Personal Safe");
		inventories.put("Pump", "Pump");
		inventories.put("Recycler", "Recycler");
		inventories.put("SolarPanel", "Solar Panel");
		inventories.put("Terraformer", "Terraformer");
		inventories.put("TradeOMat", "Trade-O-Mat");
		inventories.put("WaterMill", "Water Mill");
		inventories.put("WindMill", "Wind Mill");

		inventories.put("FFCamoflage", "Camoflageupgrade");
	}
	
	private static ArrayList<TRNoClick> noclicks = new ArrayList<TRNoClick>();
	private List<TRItem> items;
	private String inventory;
	private boolean left, right, shift;
	
	private TRNoClick(){}
	
	public TRNoClick(List<TRItem> items, String inventory, boolean left, boolean right, boolean shift){
		this.items = items;
		this.inventory = inventory;
		this.left = left;
		this.right = right;
		this.shift = shift;
		noclicks.add(this);
	}
	
	public static void load(){
		List<String> it = SettingsStorage.bannedConfig.getStringList("BannedClicks");
		for (String s : it){
			int in = s.indexOf("{");
			final String msg;
			if (in != -1){
				msg = " "+ s.substring(in);
				s = s.substring(0, in).trim();
			} else {
				msg = "";
			}
			String[] temp = s.split(" ");
			TRNoClick nc = new TRNoClick();
			try {
				nc.items = TRItemProcessor.processItemString(temp[0]+msg);
			} catch (TRException e) {
				Warning.config("You have an error in your Banned.yml: "+e.getMessage(), false);
				continue;
			}
			if (temp.length == 1){//only item
				nc.left = nc.right = nc.shift = true;
				nc.inventory = null;
				noclicks.add(nc);
			} else {
				boolean b = false;
				for (int i = 1;i<temp.length;i++){
					if (!loadType(s, temp[i], nc)) b = true;
				}
				if (!b) noclicks.add(nc);
			}
			
			//new TRNoClick(TRItemProcessor.processItemString(temp[0]+msg);
		}
	}
	
	private static boolean loadType(String line, String type, TRNoClick nc){
		switch (type.toLowerCase()){
			case "left": nc.left = true;
				break;
			case "right": nc.right = true;
				break;
			case "shift": nc.shift = true;
				break;
			default:
				if (nc.inventory != null){
					Warning.config("You have an invalid value in Banned.yml at \""+line+"\": You can only specify 1 inventory per item.", false);
					return false;
				}
				Iterator<Entry<String, String>> itt = inventories.entrySet().iterator();
				while (itt.hasNext()){
					Entry<String, String> e = itt.next();
					if (e.getKey().equalsIgnoreCase(type)){
						nc.inventory = e.getValue();
						break;
					}
				}
				if (nc.inventory == null){
					Warning.config("You have an invalid value in Banned.yml: "+type+" is not a valid value (left, right, shift or an inventoryname)", false);
					return false;
				}
		}
		return true;
	}

	public static boolean blockClick(InventoryClickEvent event){
		if (event.getWhoClicked() == null) return false;
		final Player player = (Player) event.getWhoClicked();
		if (player.hasPermission("tekkitrestrict.bypass.noclick")) return false;
		
		ItemStack item = event.getCurrentItem();
		if (item == null) return false;
		final int id = item.getTypeId();
		final int data = item.getDurability();
		final String title = event.getView().getTitle();
		final boolean shift = event.isShiftClick();
		final boolean left = event.isLeftClick();
		final boolean right = event.isRightClick();
		
		for (TRNoClick nc : noclicks){
			if (nc.inventory != null && !nc.inventory.equalsIgnoreCase(title)) continue;
			if ((left && nc.left) || (right && nc.right) || (shift && nc.shift)){
				for (TRItem tritem : nc.items){
					if (tritem.compare(id, data)) return true;
				}
			}
		}
		return false;
	}
}
