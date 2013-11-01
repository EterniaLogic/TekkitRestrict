package com.github.dreadslicer.tekkitrestrict;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.dreadslicer.tekkitrestrict.objects.TRItem;

public class NameProcessor {
	private static Map<Integer, String> EEItems = Collections.synchronizedMap(new HashMap<Integer, String>());
	private static Map<TRItem, String> EEBlocks = new HashMap<TRItem, String>();
	static {
		EEBlocks.put(TRItem.parseItem(126, 0), "Energy Collector");
		EEBlocks.put(TRItem.parseItem(126, 1), "Collector Mk. 2");
		EEBlocks.put(TRItem.parseItem(126, 2), "Collector Mk. 3");
		EEBlocks.put(TRItem.parseItem(126, 3), "DM Furnace");
		EEBlocks.put(TRItem.parseItem(126, 4), "RM Furnace");
		EEBlocks.put(TRItem.parseItem(126, 5), "Anti-Matter Relay");
		EEBlocks.put(TRItem.parseItem(126, 6), "Relay Mk. 2");
		EEBlocks.put(TRItem.parseItem(126, 7), "Relay Mk. 3");
		EEBlocks.put(TRItem.parseItem(126, 8), "DM Block");
		EEBlocks.put(TRItem.parseItem(126, 9), "RM Block");
		EEBlocks.put(TRItem.parseItem(126, 10), "Nova Catalyst");
		EEBlocks.put(TRItem.parseItem(126, 11), "Nova Cataclysm");
		EEBlocks.put(TRItem.parseItem(127, 0), "DM Pedestal");
		EEBlocks.put(TRItem.parseItem(128, 0), "Alchemical Chest");
		EEBlocks.put(TRItem.parseItem(128, 1), "Energy Condenser");
		EEBlocks.put(TRItem.parseItem(129, 0), "Interdiction Torch");
		EEBlocks.put(TRItem.parseItem(130, 0), "Transmution Tablet");
		
		EEItems.put(27526, "Philosopher Stone");
		EEItems.put(27527, "Destruction Catalyst");
		EEItems.put(27528, "Iron Band");
		EEItems.put(27529, "Soul Stone");
		EEItems.put(27530, "Evertide Amulet");
		EEItems.put(27531, "Volcanite Amulet");
		EEItems.put(27532, "Black Hole Band");
		EEItems.put(27533, "Ring of Ignition");
		EEItems.put(27534, "Archangel's Smite");
		EEItems.put(27535, "Hyperkinetic Lens");

		EEItems.put(27536, "SwiftWolf's Rending Gale");
		EEItems.put(27537, "Harvest Ring");
		EEItems.put(27538, "Watch of Flowing Time");
		EEItems.put(27539, "Alchemical Coal");
		EEItems.put(27540, "Mobius Fuel");
		EEItems.put(27541, "Dark Matter");
		EEItems.put(27542, "Covalence Dust");

		EEItems.put(27543, "Dark Matter Pickaxe");
		EEItems.put(27544, "Dark Matter Spade");
		EEItems.put(27545, "Dark Matter Hoe");
		EEItems.put(27546, "Dark Matter Sword");
		EEItems.put(27547, "Dark Matter Axe");
		EEItems.put(27548, "Dark Matter Shears");

		EEItems.put(27549, "Dark Matter Chestplate");
		EEItems.put(27550, "Dark Matter Helmet");
		EEItems.put(27551, "Dark Matter Greaves");
		EEItems.put(27552, "Dark Matter Boots");

		EEItems.put(27553, "Gem of Eternal Density");
		EEItems.put(27554, "Repair Talisman");
		EEItems.put(27555, "Dark Matter Hammer");
		EEItems.put(27556, "Cataclyctic Lens");
		EEItems.put(27557, "Klein Star Ein");
		EEItems.put(27558, "Klein Star Zwei");
		EEItems.put(27559, "Klein Star Drei");
		EEItems.put(27560, "Klein Star Vier");
		EEItems.put(27561, "Klein Star Sphere");
		
		EEItems.put(27591, "Klein Star Omega");
		
		EEItems.put(27562, "Alchemy Bag");
		EEItems.put(27563, "Red Matter");
		EEItems.put(27564, "Red Matter Pickaxe");
		EEItems.put(27565, "Red Matter Spade");
		EEItems.put(27566, "Red Matter Hoe");
		EEItems.put(27567, "Red Matter Sword");
		EEItems.put(27568, "Red Matter Axe");
		EEItems.put(27569, "Red Matter Shears");
		EEItems.put(27570, "Red Matter Hammer");
		EEItems.put(27571, "Aeternalis Fuel");
		EEItems.put(27572, "Red Katar");
		EEItems.put(27573, "Red Morning Star");
		EEItems.put(27574, "Zero Ring");
		EEItems.put(27575, "Red Matter Chestplate");
		EEItems.put(27576, "Red Matter Helmet");
		EEItems.put(27577, "Red Matter Greaves");
		EEItems.put(27578, "Red Matter Boots");
		EEItems.put(27579, "Infernal Armor/Gem Chestplate");
		EEItems.put(27580, "Abyss Helmet/Gem Helmet");
		EEItems.put(27581, "Gravity Greaves/Gem Leggings");
		EEItems.put(27582, "Hurricane Boots/Gem Boots");
		EEItems.put(27583, "Mercurial Eye");
		EEItems.put(27584, "Ring of Arcana");
		EEItems.put(27585, "Divining Rod");
		EEItems.put(27588, "Body Stone");
		EEItems.put(27589, "Life Stone");
		EEItems.put(27590, "Mind Stone");
		EEItems.put(27592, "Transmutation Tablet");
		EEItems.put(27593, "Void Ring");
		EEItems.put(27594, "Alchemy Tome");
	}

	private static Map<Integer, String> IC2Items = Collections.synchronizedMap(new HashMap<Integer, String>());
	static {
		IC2Items.put(30171, "Quantum Helmet");
		IC2Items.put(30172, "Quantum Chestplate");
		IC2Items.put(30173, "Quantum Leggings");
		IC2Items.put(30174, "Quantum Boots");
		
		IC2Items.put(30178, "Nano Helmet");
		IC2Items.put(30177, "Nano Chestplate");
		IC2Items.put(30176, "Nano Leggings");
		IC2Items.put(30175, "Nano Boots");
		
		IC2Items.put(30209, "Electric Jetpack");
		
		IC2Items.put(30180, "Batpack");
		IC2Items.put(30127, "Lappack");
		
		IC2Items.put(30233, "Chainsaw");
		IC2Items.put(30235, "Mining Drill");
		IC2Items.put(30234, "Diamond Drill");
		
		IC2Items.put(30119, "Electric Hoe");
		IC2Items.put(30141, "Electric Wrench");
		IC2Items.put(30124, "Electric Treetap");
		
		IC2Items.put(30148, "Nano Saber");
		
		IC2Items.put(30208, "Mining Laser");
		
		IC2Items.put(30242, "RE-Battery");
		IC2Items.put(30241, "Energy Crystal");
		IC2Items.put(30240, "Lapatron Crystal");
		IC2Items.put(30220, "OD-Scanner");
		IC2Items.put(30219, "OV-Scanner");
		
		IC2Items.put(31257, "Digital Thermometer");
	}

}
