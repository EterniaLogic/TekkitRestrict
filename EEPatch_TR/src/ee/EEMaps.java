package ee;

//import java.io.PrintStream;
import java.util.HashMap;
import net.minecraft.server.Block;
//import net.minecraft.server.BlockDeadBush;
import net.minecraft.server.BlockFlower;
//import net.minecraft.server.BlockGrass;
//import net.minecraft.server.BlockLeaves;
//import net.minecraft.server.BlockLongGrass;
//import net.minecraft.server.BlockMycel;
import net.minecraft.server.Item;
//import net.minecraft.server.ItemShears;
import net.minecraft.server.ItemStack;
//import net.minecraft.server.ItemWorldMap;
import net.minecraft.server.ModLoader;

public class EEMaps {
	public static HashMap<Integer, HashMap<Integer, Integer>> alchemicalValues = new HashMap<Integer, HashMap<Integer, Integer>>();
	public static HashMap<Integer, String> modBlacklist = new HashMap<Integer, String>();
	@SuppressWarnings("rawtypes")
	public static HashMap flyingArmor = new HashMap();
	public static HashMap<Integer, Integer> flyingItems = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> fireImmuneItems = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> fireImmuneArmors = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Boolean> durationEffectItems = new HashMap<Integer, Boolean>();
	public static HashMap<Integer, Integer> modItems = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> chargedItems = new HashMap<Integer, Integer>();
	public static HashMap<Integer, HashMap<Integer, Boolean>> pedestalItems = new HashMap<Integer, HashMap<Integer, Boolean>>();
	public static HashMap<Integer, HashMap<Integer, Boolean>> chestItems = new HashMap<Integer, HashMap<Integer, Boolean>>();
	public static HashMap<Integer, Integer> woodBlockRegistry = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> leafBlockRegistry = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> oreBlockRegistry = new HashMap<Integer, Integer>();
	public static HashMap<Integer, HashMap<Integer, Integer>> fuelItemRegistry = new HashMap<Integer, HashMap<Integer, Integer>>();
	public static HashMap<Integer, Integer> metaMappings = new HashMap<Integer, Integer>();

	public static boolean isBlacklisted(String var0) {
		return modBlacklist.containsValue(var0);
	}

	public static void addNameToBlacklist(String var0) {
		int var1 = 0;
		for (; modBlacklist.get(var1) != null; var1++);
		modBlacklist.put(var1, var0);
	}

	public static boolean isLeaf(int id) {
		return leafBlockRegistry.containsValue(id);
	}

	public static void addLeafBlock(int id) {
		int i = 0;
		for (; leafBlockRegistry.get(i) != null; i++);
		leafBlockRegistry.put(i, id);
	}

	public static boolean isWood(int id) {
		return woodBlockRegistry.containsValue(id);
	}

	public static void addWoodBlock(int id) {
		int i = 0;
		for (; woodBlockRegistry.get(i) != null; i++);
		woodBlockRegistry.put(i, id);
	}

	public static boolean isChestItem(int var0) {
		return isChestItem(var0, 0);
	}

	public static boolean isChestItem(int id, int data) {
		HashMap<Integer, Boolean> old = chestItems.get(id);
		return old == null ? false : old.get(data).booleanValue();
	}

	private static void addChestItem(int id) {
		addChestItem(id, 0);
	}

	private static void addChestItem(int id, int data) {
		HashMap<Integer, Boolean> var2 = chestItems.get(id);

		if (var2 == null) {
			var2 = new HashMap<Integer, Boolean>();
		}

		var2.put(data, true);
		chestItems.put(id, var2);
	}

	public static void AddRepairRecipe(ItemStack var0, Object[] var1) {
		if (var0 != null) {
			ItemStack var2 = var0.cloneItemStack();
			Object[] var3 = new Object[var1.length + 1];

			for (int var4 = 0; var4 < var3.length; var4++) {
				if (var4 >= var1.length) {
					var3[var4] = var0;
					break;
				}

				var3[var4] = var1[var4];
			}

			var2.setData(0);
			ModLoader.addShapelessRecipe(var2, var3);
		}
	}

	public static void AddPSRecipes(ItemStack var0, ItemStack var1, ItemStack var2, ItemStack var3, ItemStack var4, ItemStack var5, ItemStack var6,
			ItemStack var7, ItemStack var8) {
		ItemStack[] var9 = {var1, var2, var3, var4, var5, var6, var7, var8};

		for (int var10 = 2; var10 <= 9; var10++) {
			Object[] var11 = new Object[var10];
			ItemStack[] var12 = new ItemStack[var10 - 1];
			var11[0] = pstone();

			for (int var13 = 1; var13 < var10; var13++) {
				var11[var13] = var0;
				var12[(var13 - 1)] = var0;
			}

			checkRecipe(var9[(var10 - 2)], var12);
			ModLoader.addShapelessRecipe(var9[(var10 - 2)], var11);
		}
	}

	public static void checkRecipe(ItemStack var0, ItemStack[] var1) {
		if ((var0 != null) && (var1[0] != null)) {
			int var2 = 0;
			String var3 = var1[0].getItem().getName();
			String var4 = var0.getItem().getName();
			ItemStack[] var5 = var1;
			int var6 = var1.length;

			for (int var7 = 0; var7 < var6; var7++) {
				ItemStack var8 = var5[var7];

				if (var8 != null) {
					var2 += getEMC(var8.id, var8.getData() == -1 ? 0 : var8.getData());

					if (var8.getItem().k()) {
						var2 -= getEMC(var8.getItem().j().id);
					}
				}
			}

			int var9 = getEMC(var0.id, var0.getData()) * var0.count;

			if (var0.getItem().k()) {
				var9 -= getEMC(var0.getItem().j().id);
			}

			if (var2 != var9) {
				System.out.println("Inconsistency when combining " + var1.length + " meta " + var1[0].getData() + " " + var3 + " to make " + var4);
			}
		} else {
			System.out.println("EMC Consistency Check - error, output or first input returns null..");
		}
	}

	public static boolean isChargedItem(int id) {
		return chargedItems.containsValue(id);
	}

	public static void addChargedItem(int id) {
		int i = 0;
		for (; chargedItems.get(i) != null; i++);
		chargedItems.put(i, id);
	}

	public static boolean isPedestalItem(int var0) {
		return isPedestalItem(var0, 0);
	}

	public static boolean isPedestalItem(int id, int data) {
		HashMap<Integer, Boolean> old = pedestalItems.get(id);

		return old == null ? false : old.get(data).booleanValue();
	}

	private static void addPedestalItem(int var0) {
		addPedestalItem(var0, 0);
	}

	private static void addPedestalItem(int id, int data) {
		HashMap<Integer, Boolean> var2 = pedestalItems.get(id);

		if (var2 == null) {
			var2 = new HashMap<Integer, Boolean>();
		}

		var2.put(data, true);
		pedestalItems.put(id, var2);
	}

	public static boolean isModItem(int var0) {
		return modItems.containsValue(var0);
	}

	public static void addModItem(int id) {
		int i = 0;
		for (; modItems.get(i) != null; i++);
		modItems.put(i, id);
	}

	public static void addDurationEffectItem(int var0) {
		durationEffectItems.put(var0, true);
	}

	public static boolean hasDurationEffect(Item var0) {
		Boolean old = durationEffectItems.get(var0.id);
		return old == null ? false : old.booleanValue();
	}

	public static boolean isFuel(ItemStack var0) {
		return isFuel(var0.id, var0.getData());
	}

	public static boolean isFuel(int var0, int var1) {
		HashMap<Integer, Integer> old = fuelItemRegistry.get(var0);
		return old == null ? false : old.containsValue(var1);
	}

	public static void addFuelItem(int var0) {
		addFuelItem(var0, 0);
	}

	public static void addFuelItem(int id, int data) {
		HashMap<Integer, Integer> var2 = fuelItemRegistry.get(id);

		if (var2 == null) {
			var2 = new HashMap<Integer, Integer>();
		}

		int i = 0;
		for (; var2.get(i) != null; i++);
		var2.put(i, data);
		fuelItemRegistry.put(id, var2);
	}

	public static boolean isOreBlock(int var0) {
		return oreBlockRegistry.containsValue(var0);
	}

	public static void addOreBlock(int id) {
		int i = 0;
		for (; oreBlockRegistry.get(i) != null; i++);
		oreBlockRegistry.put(i, id);
	}

	public static int getEMC(ItemStack var0) {
		if (var0 == null) return 0;

		int emc = getEMC(var0.id, var0.getData());
		if (emc != 0) {
			return emc < 0 ? 0 : emc;
		} else {
			emc = getEMC(var0.id);
			if (emc <= 0) {
				return 0;
			} else {
				if (var0.d()) {
					return (int) (emc * ((float) (var0.i() - var0.getData()) / (float) var0.i()));
				} else {
					return emc;
				}
			}
		}
		/*
		if (var0 == null) return 0;
		return getEMC(var0.id, var0.getData()) == 0 ? 0 :
			getEMC(var0.id) > 0 ? getEMC(var0.id) :
				var0.d() ? (int)(getEMC(var0.id) * ((var0.i() - var0.getData()) / var0.i())) :
					getEMC(var0.id, var0.getData());*/
	}

	public static int getEMC(int id) {
		return getEMC(id, 0);
	}

	public static int getEMC(int id, int data) {
		HashMap<Integer, Integer> old = alchemicalValues.get(id);
		if (old == null) return 0;
		Integer old2 = old.get(data);
		return old2 == null ? 0 : old2.intValue();
	}

	public static void addEMC(int id, int emc) {
		addEMC(id, 0, emc);
	}

	public static void addEMC(int id, int data, int emc) {
		if (emc == 0) {
			System.out.println("Error: Alchemical Value of 0 being added to hashmap for item index " + id + " of meta " + data);
		}

		HashMap<Integer, Integer> var3 = alchemicalValues.get(id);

		if (var3 == null) {
			var3 = new HashMap<Integer, Integer>();
		}

		var3.put(data, emc);
		alchemicalValues.put(id, var3);
	}

	public static int getMeta(int var0) {
		Integer old = metaMappings.get(var0);
		return old == null ? 0 : old.intValue();
	}

	public static void addMeta(int var0, int var1) {
		metaMappings.put(var0, var1);
	}

	public static boolean isFlyingItem(int var0) {
		return flyingItems.containsValue(var0);
	}

	public static void addFlyingItem(int var0) {
		int var1;
		for (var1 = 0; flyingItems.get(var1) != null; var1++);
		flyingItems.put(var1, var0);
	}

	public static boolean isFireImmuneItem(int var0) {
		return fireImmuneItems.containsValue(var0);
	}

	public static void addFireImmuneItem(int var0) {
		int var1;
		for (var1 = 0; fireImmuneItems.get(var1) != null; var1++);
		fireImmuneItems.put(var1, var0);
	}

	public static boolean isFireImmuneArmor(int var0) {
		return fireImmuneArmors.containsValue(var0);
	}

	public static void addFireImmuneArmor(int var0) {
		int var1;
		for (var1 = 0; fireImmuneArmors.get(var1) != null; var1++);
		fireImmuneArmors.put(var1, var0);
	}

	public static EEProps InitProps(EEProps var0) {
		var0.getInt("machineFactor", 4);
		var0.getInt("CondenserGUI", 46);
		var0.getInt("CollectorGUI", 47);
		var0.getInt("Collector2GUI", 48);
		var0.getInt("Collector3GUI", 49);
		var0.getInt("AlchChestGUI", 50);
		var0.getInt("DMFurnaceGUI", 51);
		var0.getInt("RMFurnaceGUI", 52);
		var0.getInt("RelayGUI", 53);
		var0.getInt("Relay2GUI", 54);
		var0.getInt("Relay3GUI", 55);
		var0.getInt("AlchBagGUI", 56);
		var0.getInt("TransmutationGUI", 57);
		var0.getInt("PortableTransmutationGUI", 58);
		var0.getInt("PortableCraftingGUI", 59);
		var0.getInt("PedestalGUI", 60);
		var0.getInt("MercurialGUI", 61);
		var0.getInt("BlockEEStone", 175);
		var0.getInt("BlockEEPedestal", 176);
		var0.getInt("BlockEEChest", 177);
		var0.getInt("BlockEETorch", 178);
		var0.getInt("BlockEEDevice", 179);
		var0.getInt("ItemPhilStone", 27270);
		var0.getInt("ItemCatalystStone", 27271);
		var0.getInt("ItemBaseRing", 27272);
		var0.getInt("ItemSoulStone", 27273);
		var0.getInt("ItemEvertide", 27274);
		var0.getInt("ItemVolcanite", 27275);
		var0.getInt("ItemAttractionRing", 27276);
		var0.getInt("ItemIgnitionRing", 27277);
		var0.getInt("ItemGrimarchRing", 27278);
		var0.getInt("ItemHyperkineticLens", 27279);
		var0.getInt("ItemSwiftWolfRing", 27280);
		var0.getInt("ItemHarvestRing", 27281);
		var0.getInt("ItemWatchOfTime", 27282);
		var0.getInt("ItemAlchemicalCoal", 27283);
		var0.getInt("ItemMobiusFuel", 27284);
		var0.getInt("ItemDarkMatter", 27285);
		var0.getInt("ItemCovalenceDust", 27286);
		var0.getInt("ItemDarkPickaxe", 27287);
		var0.getInt("ItemDarkSpade", 27288);
		var0.getInt("ItemDarkHoe", 27289);
		var0.getInt("ItemDarkSword", 27290);
		var0.getInt("ItemDarkAxe", 27291);
		var0.getInt("ItemDarkShears", 27292);
		var0.getInt("ItemDarkHammer", 27299);
		var0.getInt("ItemDarkMatterArmor", 27293);
		var0.getInt("ItemDarkMatterHelmet", 27294);
		var0.getInt("ItemDarkMatterGreaves", 27295);
		var0.getInt("ItemDarkMatterBoots", 27296);
		var0.getInt("ItemEternalDensity", 27297);
		var0.getInt("ItemRepairCharm", 27298);
		var0.getInt("ItemHyperCatalyst", 27300);
		var0.getInt("ItemKleinStar", 27301);
		var0.getInt("ItemKleinStar2", 27302);
		var0.getInt("ItemKleinStar3", 27303);
		var0.getInt("ItemKleinStar4", 27304);
		var0.getInt("ItemKleinStar5", 27305);
		var0.getInt("ItemKleinStar6", 27335);
		var0.getInt("ItemAlchemyBag", 27306);
		var0.getInt("ItemRedMatter", 27307);
		var0.getInt("ItemRedPickaxe", 27308);
		var0.getInt("ItemRedSpade", 27309);
		var0.getInt("ItemRedHoe", 27310);
		var0.getInt("ItemRedSword", 27311);
		var0.getInt("ItemRedAxe", 27312);
		var0.getInt("ItemRedShears", 27313);
		var0.getInt("ItemRedHammer", 27314);
		var0.getInt("ItemAeternalisFuel", 27315);
		var0.getInt("ItemRedKatar", 27316);
		var0.getInt("ItemRedMace", 27317);
		var0.getInt("ItemZeroRing", 27318);
		var0.getInt("ItemRedMatterArmor", 27319);
		var0.getInt("ItemRedMatterHelmet", 27320);
		var0.getInt("ItemRedMatterGreaves", 27321);
		var0.getInt("ItemRedMatterBoots", 27322);
		var0.getInt("ItemRedMatterArmorP", 27323);
		var0.getInt("ItemRedMatterHelmetP", 27324);
		var0.getInt("ItemRedMatterGreavesP", 27325);
		var0.getInt("ItemRedMatterBootsP", 27326);
		var0.getInt("ItemMercurialEye", 27327);
		var0.getInt("ItemArcaneRing", 27328);
		var0.getInt("ItemDiviningRod", 27329);
		var0.getInt("ItemBodyStone", 27332);
		var0.getInt("ItemLifeStone", 27333);
		var0.getInt("ItemMindStone", 27334);
		var0.getInt("ItemTransTablet", 27336);
		var0.getInt("ItemVoidRing", 27337);
		var0.getInt("ItemAlchemyTome", 27338);
		var0.getInt("AllowCollectors", 1);
		var0.getInt("AllowCondensers", 1);
		var0.getInt("AllowRelays", 1);
		var0.getInt("AllowChests", 1);
		var0.getInt("AllowPedestals", 1);
		var0.getInt("AllowFurnaces", 1);
		var0.getInt("AllowInterdiction", 1);
		var0.getInt("AllowIgnition", 1);
		var0.getInt("AllowZeroRing", 1);
		var0.getInt("AllowSwiftWolf", 1);
		var0.getInt("AllowHarvestBand", 1);
		var0.getInt("AllowArcana", 1);
		var0.getInt("AllowArchangel", 1);
		var0.getInt("AllowVoidRing", 1);
		var0.getInt("AllowBlackHoleBand", 1);
		var0.getInt("AllowEternalDensity", 1);
		var0.getInt("AllowSoulstone", 1);
		var0.getInt("AllowBodystone", 1);
		var0.getInt("AllowLifestone", 1);
		var0.getInt("AllowMindstone", 1);
		var0.getInt("AllowRepair", 1);
		var0.getInt("AllowWatchOfTime", 1);
		var0.getInt("AllowMercurial", 1);
		var0.getInt("AllowDCatalyst", 1);
		var0.getInt("AllowHKLens", 1);
		var0.getInt("AllowHCLens", 1);
		var0.getInt("AllowDMTools", 1);
		var0.getInt("AllowRMTools", 1);
		var0.getInt("AllowAlchemyBags", 1);
		var0.getInt("AllowDMArmor", 1);
		var0.getInt("AllowRMArmor", 1);
		var0.getInt("AllowRMArmorPlus", 1);
		var0.getInt("AllowKleinStar", 1);
		var0.getInt("AllowEvertide", 1);
		var0.getInt("AllowVolcanite", 1);
		var0.getInt("AllowDiviningRod", 1);
		var0.getInt("AllowNovaC1", 1);
		var0.getInt("AllowNovaC2", 1);
		var0.getInt("AllowTransmutationTable", 1);
		return var0;
	}

	public static void InitAlchemicalValues() {
		System.out.println("Initializing alchemy values for Equivalent Exchange..");

		for (int var0 = 0; var0 < 4; var0++) {
			addEMC(Block.LEAVES.id, var0, 1);
			addEMC(Block.LOG.id, var0, 32);
			addEMC(Block.SAPLING.id, var0, getEMC(Block.LOG.id));
			addEMC(Block.WOOD.id, var0, 32 / 4);
		}
		addEMC(Item.COAL.id, 1, getEMC(Block.LOG.id));
		int charcoal = getEMC(Item.COAL.id, 1);
		addEMC(Item.REDSTONE.id, 0, charcoal * 2);
		int redstone = getEMC(Item.REDSTONE.id);
		addEMC(Item.COAL.id, 0, charcoal * 4);
		int wood = getEMC(Block.WOOD.id);
		addEMC(Block.WORKBENCH.id, wood * 4);
		addEMC(Item.WOOD_DOOR.id, wood * 6);
		addEMC(Block.CHEST.id, wood * 8);
		addEMC(Block.WOOD_STAIRS.id, wood * 6 / 4);
		addEMC(Block.STEP.id, 2, wood / 2);
		addEMC(Item.BOAT.id, wood * 5);
		addEMC(Block.WOOD_PLATE.id, wood * 2);
		addEMC(Block.TRAP_DOOR.id, wood * 6 / 2);
		addEMC(Item.BOWL.id, wood * 3 / 4);
		addEMC(Item.STICK.id, wood / 2);
		int stick = getEMC(Item.STICK.id);
		addEMC(Block.FENCE.id, stick * 6 / 2);
		addEMC(Block.FENCE_GATE.id, wood * 2 + stick * 4);
		addEMC(Item.SIGN.id, wood * 6 + stick);
		addEMC(Block.LADDER.id, stick * 7 / 2);
		addEMC(Item.FISHING_ROD.id, stick * 3 + getEMC(Item.STRING.id) * 2);
		addEMC(Item.WOOD_SPADE.id, wood + stick * 2);
		addEMC(Item.WOOD_SWORD.id, wood * 2 + stick);
		addEMC(Item.WOOD_HOE.id, wood * 2 + stick * 2);
		addEMC(Item.WOOD_PICKAXE.id, wood * 3 + stick * 2);
		addEMC(Item.WOOD_AXE.id, wood * 3 + stick * 2);
		addEMC(Item.BONE.id, 96);

		for (int var0 = 0; var0 < 16; var0++) {
			if (var0 == 15) {
				addEMC(Item.INK_SACK.id, var0, getEMC(Item.BONE.id) / 3);
			} else if (var0 == 4) {
				addEMC(Item.INK_SACK.id, var0, 864);
			} else if (var0 == 3) {
				addEMC(Item.INK_SACK.id, var0, 128);
			} else {
				addEMC(Item.INK_SACK.id, var0, 8);
			}
		}

		addEMC(Block.LAPIS_BLOCK.id, getEMC(Item.INK_SACK.id, 4) * 9);
		addEMC(BlockFlower.YELLOW_FLOWER.id, getEMC(Item.INK_SACK.id, 11) * 2);
		addEMC(BlockFlower.RED_ROSE.id, getEMC(Item.INK_SACK.id, 1) * 2);
		addEMC(EEItem.covalenceDust.id, 0, 1);
		addEMC(EEItem.covalenceDust.id, 1, 8);
		addEMC(EEItem.covalenceDust.id, 2, 208);
		addEMC(Block.DIRT.id, 1);
		int dirt = getEMC(Block.DIRT.id);
		addEMC(Block.SAND.id, dirt);
		int sand = dirt;
		addEMC(Block.GRASS.id, dirt);
		addEMC(Block.MYCEL.id, getEMC(Block.GRASS.id));
		addEMC(Block.LONG_GRASS.id, getEMC(Block.GRASS.id));
		addEMC(Block.DEAD_BUSH.id, getEMC(Block.LONG_GRASS.id));
		addEMC(Block.WATER_LILY.id, 16);
		addEMC(Block.VINE.id, 8);
		addEMC(Block.SANDSTONE.id, sand * 4);
		addEMC(Block.STEP.id, 1, getEMC(Block.SANDSTONE.id) / 2);
		addEMC(Block.GLASS.id, sand);
		addEMC(Item.GLASS_BOTTLE.id, getEMC(Block.GLASS.id));
		addEMC(Block.GRAVEL.id, getEMC(Block.SANDSTONE.id));
		addEMC(Item.FLINT.id, getEMC(Block.GRAVEL.id));
		addEMC(Block.NETHER_BRICK.id, getEMC(Block.GRAVEL.id));
		addEMC(Block.NETHER_FENCE.id, getEMC(Block.NETHER_BRICK.id));
		addEMC(Block.NETHER_BRICK_STAIRS.id, getEMC(Block.NETHER_BRICK.id) * 6 / 4);
		addEMC(Item.FEATHER.id, 48);
		addEMC(Item.ARROW.id, (stick + getEMC(Item.FLINT.id) + getEMC(Item.FEATHER.id)) / 4);
		addEMC(Block.COBBLESTONE.id, 1);
		int cobble = getEMC(Block.COBBLESTONE.id);
		addEMC(Block.FURNACE.id, cobble * 8);
		addEMC(Block.STEP.id, 3, cobble);
		addEMC(Block.COBBLESTONE_STAIRS.id, cobble * 6 / 4);
		addEMC(Block.LEVER.id, cobble + stick);
		addEMC(Block.NETHERRACK.id, cobble);
		addEMC(Block.STONE.id, cobble);
		int netherrack = cobble;
		int stone = cobble;
		addEMC(Block.WHITESTONE.id, netherrack);
		addEMC(Block.STONE_BUTTON.id, stone * 2);
		addEMC(Block.STONE_PLATE.id, stone * 2);
		addEMC(Block.STEP.id, 0, stone);

		for (int var0 = 0; var0 < 4; var0++) {
			addEMC(Block.SMOOTH_BRICK.id, var0, stone);
		}
		int smooth_brick = stone;

		addEMC(Block.STEP.id, 5, smooth_brick);
		addEMC(Block.STONE_STAIRS.id, smooth_brick * 6 / 4);
		addEMC(Item.STONE_SPADE.id, cobble + stick * 2);
		addEMC(Item.STONE_SWORD.id, cobble * 2 + stick);
		addEMC(Item.STONE_HOE.id, cobble * 2 + stick * 2);
		addEMC(Item.STONE_PICKAXE.id, cobble * 3 + stick * 2);
		addEMC(Item.STONE_AXE.id, cobble * 3 + stick * 2);
		addEMC(Item.STRING.id, 12);
		int string = getEMC(Item.STRING.id);
		addEMC(Item.BOW.id, stick * 3 + string * 3);
		addEMC(Item.SLIME_BALL.id, 24);
		int slime = getEMC(Item.SLIME_BALL.id);
		addEMC(Block.WEB.id, (string * 2 + slime) / 4);
		addEMC(Block.MOSSY_COBBLESTONE.id, cobble + getEMC(Item.SEEDS.id) + slime * 6);

		for (int var0 = 0; var0 < 16; var0++) {
			addEMC(Block.WOOL.id, var0, string * 4);
		}

		addEMC(Item.BED.id, getEMC(Block.WOOL.id) * 3 + wood * 3);
		addEMC(Item.PAINTING.id, getEMC(Block.WOOL.id) + stick * 8);
		addEMC(Item.SEEDS.id, 16);
		addEMC(Item.SUGAR_CANE.id, 32);
		addEMC(Item.SUGAR.id, getEMC(Item.SUGAR_CANE.id));
		addEMC(Item.PAPER.id, getEMC(Item.SUGAR_CANE.id));
		addEMC(Item.BOOK.id, getEMC(Item.PAPER.id) * 3);
		addEMC(Block.BOOKSHELF.id, getEMC(Item.BOOK.id) * 3 + wood * 6);
		addEMC(Item.WHEAT.id, 24);
		int wheat = getEMC(Item.WHEAT.id);
		addEMC(Item.BREAD.id, wheat * 3);
		addEMC(Item.COOKIE.id, (wheat * 2 + getEMC(Item.INK_SACK.id, 3)) / 8);
		addEMC(Block.CACTUS.id, 8);
		addEMC(Item.MELON.id, 16);
		addEMC(Item.MELON_SEEDS.id, getEMC(Item.MELON.id));
		addEMC(Block.MELON.id, getEMC(Item.MELON.id) * 9);
		addEMC(Block.PUMPKIN.id, 144);
		addEMC(Block.JACK_O_LANTERN.id, getEMC(Block.PUMPKIN.id) + getEMC(Block.TORCH.id));
		addEMC(Item.PUMPKIN_SEEDS.id, getEMC(Block.PUMPKIN.id) / 4);
		addEMC(BlockFlower.BROWN_MUSHROOM.id, 32);
		addEMC(BlockFlower.RED_MUSHROOM.id, getEMC(BlockFlower.BROWN_MUSHROOM.id));
		addEMC(Item.MUSHROOM_SOUP.id, getEMC(Item.BOWL.id) + getEMC(BlockFlower.BROWN_MUSHROOM.id) * 2);
		addEMC(Item.LEATHER.id, 64);
		int leather = getEMC(Item.LEATHER.id);
		addEMC(Item.LEATHER_BOOTS.id, leather * 4);
		addEMC(Item.LEATHER_HELMET.id, leather * 5);
		addEMC(Item.LEATHER_LEGGINGS.id, leather * 7);
		addEMC(Item.LEATHER_CHESTPLATE.id, leather * 8);
		addEMC(Item.ROTTEN_FLESH.id, 24);
		addEMC(Item.APPLE.id, 128);
		addEMC(Item.EGG.id, 32);
		addEMC(Item.BLAZE_ROD.id, 1536);
		addEMC(Item.BLAZE_POWDER.id, getEMC(Item.BLAZE_ROD.id) / 2);
		addEMC(Item.FIREBALL.id, (getEMC(Item.BLAZE_POWDER.id) + getEMC(Item.COAL.id) + getEMC(Item.SULPHUR.id)) / 3);
		addEMC(Item.MAGMA_CREAM.id, getEMC(Item.BLAZE_POWDER.id) + slime);
		addEMC(Block.BREWING_STAND.id, getEMC(Item.BLAZE_ROD.id) + cobble * 3);
		addEMC(Item.ENDER_PEARL.id, 1024);
		addEMC(Item.EYE_OF_ENDER.id, getEMC(Item.ENDER_PEARL.id) + getEMC(Item.BLAZE_POWDER.id));
		addEMC(Item.SPIDER_EYE.id, 128);
		addEMC(Item.FERMENTED_SPIDER_EYE.id, getEMC(Item.SPIDER_EYE.id) + getEMC(BlockFlower.BROWN_MUSHROOM.id) + getEMC(Item.SUGAR.id));
		addEMC(Item.NETHER_STALK.id, 24);
		addEMC(Item.PORK.id, 64);
		int meat = getEMC(Item.PORK.id);
		addEMC(Item.RAW_BEEF.id, meat);
		addEMC(Item.RAW_CHICKEN.id, meat);
		addEMC(Item.RAW_FISH.id, meat);
		addEMC(Item.GRILLED_PORK.id, meat);
		addEMC(Item.COOKED_BEEF.id, meat);
		addEMC(Item.COOKED_CHICKEN.id, meat);
		addEMC(Item.COOKED_FISH.id, meat);
		addEMC(Item.CLAY_BALL.id, 16);
		addEMC(Block.CLAY.id, getEMC(Item.CLAY_BALL.id) * 4);
		addEMC(Item.CLAY_BRICK.id, getEMC(Item.CLAY_BALL.id));
		addEMC(Block.BRICK.id, getEMC(Item.CLAY_BRICK.id) * 4);
		addEMC(Block.STEP.id, 4, getEMC(Block.BRICK.id) / 2);
		addEMC(Block.BRICK_STAIRS.id, getEMC(Block.BRICK.id) * 6 / 4);
		addEMC(Block.DISPENSER.id, getEMC(Item.BOW.id) + redstone + cobble * 7);
		addEMC(Block.NOTE_BLOCK.id, wood * 8 + redstone);
		addEMC(Block.REDSTONE_TORCH_ON.id, stick + redstone);
		addEMC(Item.DIODE.id, getEMC(Block.STONE.id) * 3 + getEMC(Block.REDSTONE_TORCH_ON.id) * 2 + redstone);
		addEMC(Item.SULPHUR.id, 192);
		addEMC(EEItem.alchemicalCoal.id, getEMC(Item.COAL.id, 0) * 4);
		addEMC(EEItem.mobiusFuel.id, getEMC(EEItem.alchemicalCoal.id) * 4);
		addEMC(EEItem.aeternalisFuel.id, getEMC(EEItem.mobiusFuel.id) * 4);
		addEMC(Block.TORCH.id, (getEMC(Item.COAL.id, 1) + stick) / 4);
		addEMC(Item.GLOWSTONE_DUST.id, redstone * 6);
		addEMC(Block.GLOWSTONE.id, getEMC(Item.GLOWSTONE_DUST.id) * 4);
		addEMC(Block.SOUL_SAND.id, (getEMC(Item.GLOWSTONE_DUST.id) + sand * 8) / 8);
		addEMC(Block.REDSTONE_LAMP_OFF.id, getEMC(Block.GLOWSTONE.id) + redstone * 4);
		addEMC(Block.TNT.id, getEMC(Item.SULPHUR.id) * 5 + sand * 4);
		addEMC(EEBlock.eeStone.id, 10, (getEMC(Block.TNT.id) + getEMC(EEItem.mobiusFuel.id)) / 2);
		addEMC(EEBlock.eeStone.id, 11, (getEMC(EEBlock.eeStone.id, 10) + getEMC(EEItem.aeternalisFuel.id)) / 2);
		addEMC(Item.IRON_INGOT.id, 256);
		int iron = getEMC(Item.IRON_INGOT.id);
		addEMC(Item.FLINT_AND_STEEL.id, getEMC(Item.FLINT.id) + iron);
		addEMC(Item.IRON_SPADE.id, iron + stick * 2);
		addEMC(Item.IRON_SWORD.id, iron * 2 + stick);
		addEMC(Item.IRON_HOE.id, iron * 2 + stick * 2);
		addEMC(Item.IRON_PICKAXE.id, iron * 3 + stick * 2);
		addEMC(Item.IRON_AXE.id, iron * 3 + stick * 2);
		addEMC(Block.PISTON.id, redstone + iron + wood * 3 + cobble * 4);
		addEMC(Block.PISTON_STICKY.id, getEMC(Block.PISTON.id) + slime);
		addEMC(Block.RAILS.id, iron * 6 / 16);
		addEMC(Block.DETECTOR_RAIL.id, iron);
		addEMC(Item.COMPASS.id, redstone + iron * 4);
		addEMC(Item.MAP.id, getEMC(Item.COMPASS.id) + getEMC(Item.PAPER.id) * 8);
		addEMC(Block.IRON_FENCE.id, iron * 6 / 16);
		addEMC(Item.IRON_BOOTS.id, iron * 4);
		addEMC(Item.IRON_HELMET.id, iron * 5);
		addEMC(Item.IRON_LEGGINGS.id, iron * 7);
		addEMC(Item.IRON_CHESTPLATE.id, iron * 8);
		addEMC(Item.IRON_DOOR.id, iron * 6);
		addEMC(Block.IRON_BLOCK.id, iron * 9);
		addEMC(Item.MINECART.id, iron * 5);
		addEMC(Item.STORAGE_MINECART.id, getEMC(Item.MINECART.id) + getEMC(Block.CHEST.id));
		addEMC(Item.POWERED_MINECART.id, getEMC(Item.MINECART.id) + getEMC(Block.FURNACE.id));
		addEMC(Item.BUCKET.id, iron * 3);
		addEMC(Block.SNOW_BLOCK.id, 1);
		addEMC(Item.WATER_BUCKET.id, getEMC(Item.BUCKET.id) + getEMC(Block.SNOW_BLOCK.id));
		addEMC(Block.ICE.id, getEMC(Block.SNOW_BLOCK.id));
		addEMC(Item.MILK_BUCKET.id, getEMC(Item.WATER_BUCKET.id) + getEMC(Item.SUGAR.id) + getEMC(Item.INK_SACK.id, 15));
		addEMC(Item.LAVA_BUCKET.id, getEMC(Item.BUCKET.id) + redstone);
		addEMC(Block.OBSIDIAN.id, redstone);
		addEMC(Item.CAKE.id, getEMC(Item.MILK_BUCKET.id) * 3 - getEMC(Item.BUCKET.id) * 3 + getEMC(Item.SUGAR.id) * 2 + wheat * 3 + getEMC(Item.EGG.id));
		addEMC(Item.GOLD_INGOT.id, 2048);
		int gold = getEMC(Item.GOLD_INGOT.id);
		addEMC(Item.GOLD_NUGGET.id, 227);
		addEMC(Item.SPECKLED_MELON.id, getEMC(Item.GOLD_NUGGET.id) + getEMC(Item.MELON.id));
		addEMC(Item.GOLDEN_APPLE.id, getEMC(Item.APPLE.id) + getEMC(Item.GOLD_NUGGET.id) * 8);
		addEMC(Block.GOLDEN_RAIL.id, gold);
		addEMC(Item.GOLD_SPADE.id, gold + stick * 2);
		addEMC(Item.GOLD_SWORD.id, gold * 2 + stick);
		addEMC(Item.GOLD_HOE.id, gold * 2 + stick * 2);
		addEMC(Item.GOLD_PICKAXE.id, gold * 3 + stick * 2);
		addEMC(Item.GOLD_AXE.id, gold * 3 + stick * 2);
		addEMC(Item.WATCH.id, gold * 4 + redstone);
		addEMC(Item.GOLD_BOOTS.id, gold * 4);
		addEMC(Item.GOLD_HELMET.id, gold * 5);
		addEMC(Item.GOLD_LEGGINGS.id, gold * 7);
		addEMC(Item.GOLD_CHESTPLATE.id, gold * 8);
		addEMC(Block.GOLD_BLOCK.id, gold * 9);
		addEMC(Item.DIAMOND.id, 8192);
		int diamond = getEMC(Item.DIAMOND.id);
		addEMC(Item.GHAST_TEAR.id, diamond / 2);
		addEMC(Block.JUKEBOX.id, diamond + wood * 8);
		addEMC(Block.ENCHANTMENT_TABLE.id, diamond * 2 + getEMC(Block.OBSIDIAN.id) * 4 + getEMC(Item.BOOK.id));
		addEMC(Item.DIAMOND_SPADE.id, diamond + stick * 2);
		addEMC(Item.DIAMOND_SWORD.id, diamond * 2 + stick);
		addEMC(Item.DIAMOND_HOE.id, diamond * 2 + stick * 2);
		addEMC(Item.DIAMOND_PICKAXE.id, diamond * 3 + stick * 2);
		addEMC(Item.DIAMOND_AXE.id, diamond * 3 + stick * 2);
		addEMC(Item.DIAMOND_BOOTS.id, diamond * 4);
		addEMC(Item.DIAMOND_HELMET.id, diamond * 5);
		addEMC(Item.DIAMOND_LEGGINGS.id, diamond * 7);
		addEMC(Item.DIAMOND_CHESTPLATE.id, diamond * 8);
		addEMC(Block.DIAMOND_BLOCK.id, diamond * 9);
		addEMC(EEItem.kleinStar1.id, diamond + getEMC(EEItem.mobiusFuel.id) * 8);
		addEMC(EEItem.kleinStar2.id, getEMC(EEItem.kleinStar1.id) * 4);
		addEMC(EEItem.kleinStar3.id, getEMC(EEItem.kleinStar2.id) * 4);
		addEMC(EEItem.kleinStar4.id, getEMC(EEItem.kleinStar3.id) * 4);
		addEMC(EEItem.kleinStar5.id, getEMC(EEItem.kleinStar4.id) * 4);
		addEMC(EEItem.kleinStar6.id, getEMC(EEItem.kleinStar5.id) * 4);
		addEMC(EEItem.darkMatter.id, getEMC(Block.DIAMOND_BLOCK.id) + getEMC(EEItem.aeternalisFuel.id) * 8);
		addEMC(EEItem.redMatter.id, getEMC(EEItem.darkMatter.id) * 3 + getEMC(EEItem.aeternalisFuel.id) * 6);
		addEMC(Item.SHEARS.id, iron * 2);
		addEMC(Item.SADDLE.id, leather * 3);
		addEMC(Block.DRAGON_EGG.id, getEMC(EEItem.darkMatter.id));
		addEMC(Item.RECORD_11.id, gold);
		addEMC(Item.RECORD_1.id, getEMC(Item.RECORD_11.id));
		addEMC(Item.RECORD_3.id, getEMC(Item.RECORD_11.id));
		addEMC(Item.RECORD_2.id, getEMC(Item.RECORD_11.id));
		addEMC(Item.RECORD_4.id, getEMC(Item.RECORD_11.id));
		addEMC(Item.RECORD_5.id, getEMC(Item.RECORD_11.id));
		addEMC(Item.RECORD_6.id, getEMC(Item.RECORD_11.id));
		addEMC(Item.RECORD_7.id, getEMC(Item.RECORD_11.id));
		addEMC(Item.RECORD_8.id, getEMC(Item.RECORD_11.id));
		addEMC(Item.RECORD_9.id, getEMC(Item.RECORD_11.id));
		addEMC(Item.RECORD_10.id, getEMC(Item.RECORD_11.id));
		InitModBlockValues();
	}

	public static void InitModBlockValues() {
		addEMC(EEBlock.eeStone.id, 8, getEMC(EEItem.darkMatter.id));
		addEMC(EEBlock.eeStone.id, 9, getEMC(EEItem.redMatter.id));

		if (EEBase.props.getInt("AllowCollectors") == 1) {
			addEMC(EEBlock.eeStone.id, 0, getEMC(Block.GLOWSTONE.id) * 6 + getEMC(Block.GLASS.id) + getEMC(Block.DIAMOND_BLOCK.id) + getEMC(Block.FURNACE.id));
			addEMC(EEBlock.eeStone.id, 1, getEMC(Block.GLOWSTONE.id) * 7 + getEMC(EEBlock.eeStone.id, 0) + getEMC(EEItem.darkMatter.id));
			addEMC(EEBlock.eeStone.id, 2, getEMC(Block.GLOWSTONE.id) * 7 + getEMC(EEBlock.eeStone.id, 1) + getEMC(EEItem.redMatter.id));
		}

		if (EEBase.props.getInt("AllowFurnaces") == 1) {
			addEMC(EEBlock.eeStone.id, 3, getEMC(Block.FURNACE.id) + getEMC(EEBlock.eeStone.id, 8) * 8);
			addEMC(EEBlock.eeStone.id, 4, getEMC(EEBlock.eeStone.id, 3) + getEMC(EEBlock.eeStone.id, 9) * 3);
		}

		if (EEBase.props.getInt("AllowRelays") == 1) {
			addEMC(EEBlock.eeStone.id, 5, getEMC(Block.OBSIDIAN.id) * 7 + getEMC(Block.DIAMOND_BLOCK.id) + getEMC(Block.GLASS.id));
			addEMC(EEBlock.eeStone.id, 6, getEMC(Block.OBSIDIAN.id) * 7 + getEMC(EEBlock.eeStone.id, 5) + getEMC(EEItem.darkMatter.id));
			addEMC(EEBlock.eeStone.id, 7, getEMC(Block.OBSIDIAN.id) * 7 + getEMC(EEBlock.eeStone.id, 6) + getEMC(EEItem.redMatter.id));
		}

		if (EEBase.props.getInt("AllowInterdiction") == 1) {
			addEMC(EEBlock.eeTorch.id, 0, (getEMC(Block.REDSTONE_TORCH_ON.id) * 2 + getEMC(Item.DIAMOND.id) * 3 + getEMC(Item.GLOWSTONE_DUST.id) * 3) / 2);
		}

		if (EEBase.props.getInt("AllowPedestals") == 1) {
			addEMC(EEBlock.eePedestal.id, 0, getEMC(EEBlock.eeStone.id, 8) * 5 + getEMC(EEItem.redMatter.id) * 4);
		}

		if (EEBase.props.getInt("AllowChests") == 1) {
			addEMC(EEBlock.eeChest.id, 0, getEMC(Block.CHEST.id) + getEMC(Item.IRON_INGOT.id) * 2 + getEMC(Item.DIAMOND.id) + getEMC(Block.STONE.id) * 2
					+ getEMC(EEItem.covalenceDust.id, 0) + getEMC(EEItem.covalenceDust.id, 1) + getEMC(EEItem.covalenceDust.id, 2));
		}

		if (EEBase.props.getInt("AllowCondensers") == 1) {
			addEMC(EEBlock.eeChest.id, 1, getEMC(EEBlock.eeChest.id, 0) + getEMC(Block.OBSIDIAN.id) * 4 + getEMC(Item.DIAMOND.id) * 4);
		}

		addEMC(EEBlock.eeDevice.id, 0, getEMC(Block.OBSIDIAN.id) * 4 + getEMC(Block.STONE.id) * 4);
	}

	public static void InitChestItems() {
		addChestItem(EEItem.repairCharm.id);
		addChestItem(EEBlock.eeTorch.id, 0);
		addChestItem(EEItem.eternalDensity.id);
	}

	public static void InitBlacklist() {}

	public static void InitChargeditems() {
		addChargedItem(EEItem.philStone.id);
		addChargedItem(EEItem.catalystStone.id);
		addChargedItem(EEItem.evertide.id);
		addChargedItem(EEItem.volcanite.id);
		addChargedItem(EEItem.ignitionRing.id);
		addChargedItem(EEItem.zeroRing.id);
		addChargedItem(EEItem.arcaneRing.id);
		addChargedItem(EEItem.grimarchRing.id);
		addChargedItem(EEItem.hyperkineticLens.id);
		addChargedItem(EEItem.watchOfTime.id);
		addChargedItem(EEItem.darkPickaxe.id);
		addChargedItem(EEItem.darkAxe.id);
		addChargedItem(EEItem.darkSpade.id);
		addChargedItem(EEItem.darkHoe.id);
		addChargedItem(EEItem.darkSword.id);
		addChargedItem(EEItem.darkShears.id);
		addChargedItem(EEItem.darkHammer.id);
		addChargedItem(EEItem.kleinStar1.id);
		addChargedItem(EEItem.kleinStar2.id);
		addChargedItem(EEItem.kleinStar3.id);
		addChargedItem(EEItem.kleinStar4.id);
		addChargedItem(EEItem.kleinStar5.id);
		addChargedItem(EEItem.kleinStar6.id);
		addChargedItem(EEItem.hyperCatalyst.id);
		addChargedItem(EEItem.redPickaxe.id);
		addChargedItem(EEItem.redAxe.id);
		addChargedItem(EEItem.redSpade.id);
		addChargedItem(EEItem.redHoe.id);
		addChargedItem(EEItem.redSword.id);
		addChargedItem(EEItem.redShears.id);
		addChargedItem(EEItem.redHammer.id);
		addChargedItem(EEItem.redKatar.id);
		addChargedItem(EEItem.redMace.id);
		addChargedItem(EEItem.mercurialEye.id);
	}

	public static void InitFuelItems() {
		addFuelItem(Item.REDSTONE.id);
		addFuelItem(Item.BLAZE_POWDER.id);
		addFuelItem(Item.COAL.id);
		addFuelItem(Item.COAL.id, 1);
		addFuelItem(Item.BLAZE_ROD.id);
		addFuelItem(Block.GLOWSTONE.id);
		addFuelItem(EEItem.alchemicalCoal.id);
		addFuelItem(Item.GLOWSTONE_DUST.id);
		addFuelItem(Item.SULPHUR.id);
		addFuelItem(EEItem.mobiusFuel.id);
		addFuelItem(EEItem.aeternalisFuel.id);
	}

	public static void InitWoodAndLeafBlocks() {
		addLeafBlock(Block.LEAVES.id);
		addWoodBlock(Block.LOG.id);
	}

	public static void InitPedestalItems() {
		addPedestalItem(EEItem.evertide.id);
		addPedestalItem(EEItem.volcanite.id);
		addPedestalItem(EEItem.soulStone.id);
		addPedestalItem(EEItem.ignitionRing.id);
		addPedestalItem(EEItem.zeroRing.id);
		addPedestalItem(EEItem.grimarchRing.id);
		addPedestalItem(EEItem.swiftWolfRing.id);
		addPedestalItem(EEItem.harvestRing.id);
		addPedestalItem(EEItem.watchOfTime.id);
		addPedestalItem(EEItem.repairCharm.id);
		addPedestalItem(EEItem.attractionRing.id);
		addPedestalItem(EEBlock.eeTorch.id, 0);
	}

	public static void InitMetaData() {
		addMeta(Item.COAL.id, 1);
		addMeta(Block.LOG.id, 2);
		addMeta(Block.LOG.id, 3);
		addMeta(Block.WOOD.id, 1);
		addMeta(Block.WOOD.id, 2);
		addMeta(Block.WOOD.id, 3);
		addMeta(Block.SANDSTONE.id, 1);
		addMeta(Block.SANDSTONE.id, 2);
		addMeta(Block.SAPLING.id, 2);
		addMeta(Block.SAPLING.id, 3);
		addMeta(Block.LEAVES.id, 2);
		addMeta(Block.LEAVES.id, 3);
		addMeta(Block.LONG_GRASS.id, 1);
		addMeta(Block.LONG_GRASS.id, 2);
		addMeta(Block.SMOOTH_BRICK.id, 2);
		addMeta(Block.STEP.id, 5);
		addMeta(Block.WOOL.id, 15);
		addMeta(Item.INK_SACK.id, 15);
		addMeta(EEBlock.eeStone.id, 11);
		addMeta(EEBlock.eeChest.id, 1);
		addMeta(EEItem.covalenceDust.id, 2);
	}

	public static void InitModItems() {
		addModItem(EEItem.covalenceDust.id);
		addModItem(EEItem.philStone.id);
		addModItem(EEItem.catalystStone.id);
		addModItem(EEItem.baseRing.id);
		addModItem(EEItem.evertide.id);
		addModItem(EEItem.volcanite.id);
		addModItem(EEItem.soulStone.id);
		addModItem(EEItem.attractionRing.id);
		addModItem(EEItem.swiftWolfRing.id);
		addModItem(EEItem.ignitionRing.id);
		addModItem(EEItem.zeroRing.id);
		addModItem(EEItem.arcaneRing.id);
		addModItem(EEItem.grimarchRing.id);
		addModItem(EEItem.hyperkineticLens.id);
		addModItem(EEItem.harvestRing.id);
		addModItem(EEItem.watchOfTime.id);
		addModItem(EEItem.eternalDensity.id);
		addModItem(EEItem.repairCharm.id);
		addModItem(EEItem.alchemicalCoal.id);
		addModItem(EEItem.mobiusFuel.id);
		addModItem(EEItem.darkMatter.id);
		addModItem(EEItem.darkPickaxe.id);
		addModItem(EEItem.darkSpade.id);
		addModItem(EEItem.darkHoe.id);
		addModItem(EEItem.darkSword.id);
		addModItem(EEItem.darkShears.id);
		addModItem(EEItem.darkHammer.id);
		addModItem(EEItem.kleinStar1.id);
		addModItem(EEItem.kleinStar2.id);
		addModItem(EEItem.kleinStar3.id);
		addModItem(EEItem.kleinStar4.id);
		addModItem(EEItem.kleinStar5.id);
		addModItem(EEItem.kleinStar6.id);
		addModItem(EEItem.alchemyBag.id);
		addModItem(EEItem.redMatter.id);
		addModItem(EEItem.hyperCatalyst.id);
		addModItem(EEItem.redPickaxe.id);
		addModItem(EEItem.redSpade.id);
		addModItem(EEItem.redHoe.id);
		addModItem(EEItem.redSword.id);
		addModItem(EEItem.redShears.id);
		addModItem(EEItem.redHammer.id);
		addModItem(EEItem.redKatar.id);
		addModItem(EEItem.redMace.id);
	}

	public static void InitOreBlocks() {
		addOreBlock(Block.COAL_ORE.id);
		addOreBlock(Block.DIAMOND_ORE.id);
		addOreBlock(Block.GOLD_ORE.id);
		addOreBlock(Block.IRON_ORE.id);
		addOreBlock(Block.LAPIS_ORE.id);
		addOreBlock(Block.REDSTONE_ORE.id);
		addOreBlock(Block.GLOWING_REDSTONE_ORE.id);
	}

	public static void InitDurationEffectItems() {
		addDurationEffectItem(EEItem.harvestRing.id);
		addDurationEffectItem(EEItem.ignitionRing.id);
		addDurationEffectItem(EEItem.swiftWolfRing.id);
		addDurationEffectItem(EEItem.watchOfTime.id);
		addDurationEffectItem(EEItem.grimarchRing.id);
		addDurationEffectItem(EEItem.attractionRing.id);
		addDurationEffectItem(EEItem.eternalDensity.id);
		addDurationEffectItem(EEItem.arcaneRing.id);
		addDurationEffectItem(EEItem.zeroRing.id);
		addDurationEffectItem(EEItem.soulStone.id);
		addDurationEffectItem(EEItem.bodyStone.id);
		addDurationEffectItem(EEItem.lifeStone.id);
		addDurationEffectItem(EEItem.mindStone.id);
	}

	public static void InitFlyingItems() {
		addFlyingItem(EEItem.volcanite.id);
		addFlyingItem(EEItem.evertide.id);
		addFlyingItem(EEItem.swiftWolfRing.id);
		addFlyingItem(EEItem.arcaneRing.id);
	}

	public static void InitFireImmunities() {
		addFireImmuneItem(EEItem.volcanite.id);
		addFireImmuneItem(EEItem.ignitionRing.id);
		addFireImmuneItem(EEItem.arcaneRing.id);
		addFireImmuneArmor(EEItem.redMatterArmorP.id);
	}

	public static void InitRepairRecipes() {
		ModLoader.addShapelessRecipe(coval(40, 0),
				new Object[] {cobble(1), cobble(1), cobble(1), cobble(1), cobble(1), cobble(1), cobble(1), cobble(1), coal(1, 1)});
		ModLoader.addShapelessRecipe(coval(40, 1), new Object[] {iing(1), redstone(1)});
		ModLoader.addShapelessRecipe(coval(40, 2), new Object[] {diamond(1), coal(1, 0)});

		if (EEBase.props.getInt("AllowRepair") == 1) {
			AddRepairRecipe(new ItemStack(Item.LEATHER_CHESTPLATE, 1, -1), multiStack(lcov(), 8));
			AddRepairRecipe(new ItemStack(Item.LEATHER_BOOTS, 1, -1), multiStack(lcov(), 4));
			AddRepairRecipe(new ItemStack(Item.LEATHER_LEGGINGS, 1, -1), multiStack(lcov(), 7));
			AddRepairRecipe(new ItemStack(Item.LEATHER_HELMET, 1, -1), multiStack(lcov(), 5));
			AddRepairRecipe(new ItemStack(Item.FISHING_ROD, 1, -1), multiStack(lcov(), 1));
			AddRepairRecipe(new ItemStack(Item.BOW, 1, -1), multiStack(lcov(), 3));
			AddRepairRecipe(new ItemStack(Item.WOOD_SPADE, 1, -1), multiStack(lcov(), 1));
			AddRepairRecipe(new ItemStack(Item.WOOD_SWORD, 1, -1), multiStack(lcov(), 1));
			AddRepairRecipe(new ItemStack(Item.WOOD_HOE, 1, -1), multiStack(lcov(), 1));
			AddRepairRecipe(new ItemStack(Item.WOOD_AXE, 1, -1), multiStack(lcov(), 1));
			AddRepairRecipe(new ItemStack(Item.WOOD_PICKAXE, 1, -1), multiStack(lcov(), 1));
			AddRepairRecipe(new ItemStack(Item.CHAINMAIL_CHESTPLATE, 1, -1), multiStack(lcov(), 8));
			AddRepairRecipe(new ItemStack(Item.CHAINMAIL_BOOTS, 1, -1), multiStack(lcov(), 4));
			AddRepairRecipe(new ItemStack(Item.CHAINMAIL_LEGGINGS, 1, -1), multiStack(lcov(), 7));
			AddRepairRecipe(new ItemStack(Item.CHAINMAIL_HELMET, 1, -1), multiStack(lcov(), 5));
			AddRepairRecipe(new ItemStack(Item.STONE_SPADE, 1, -1), multiStack(lcov(), 1));
			AddRepairRecipe(new ItemStack(Item.STONE_SWORD, 1, -1), multiStack(lcov(), 2));
			AddRepairRecipe(new ItemStack(Item.STONE_HOE, 1, -1), multiStack(lcov(), 2));
			AddRepairRecipe(new ItemStack(Item.STONE_AXE, 1, -1), multiStack(lcov(), 3));
			AddRepairRecipe(new ItemStack(Item.STONE_PICKAXE, 1, -1), multiStack(lcov(), 3));
			AddRepairRecipe(new ItemStack(Item.IRON_CHESTPLATE, 1, -1), multiStack(mcov(), 8));
			AddRepairRecipe(new ItemStack(Item.IRON_BOOTS, 1, -1), multiStack(mcov(), 4));
			AddRepairRecipe(new ItemStack(Item.IRON_LEGGINGS, 1, -1), multiStack(mcov(), 7));
			AddRepairRecipe(new ItemStack(Item.IRON_HELMET, 1, -1), multiStack(mcov(), 5));
			AddRepairRecipe(new ItemStack(Item.SHEARS, 1, -1), multiStack(mcov(), 1));
			AddRepairRecipe(new ItemStack(Item.FLINT_AND_STEEL, 1, -1), multiStack(mcov(), 1));
			AddRepairRecipe(new ItemStack(Item.IRON_SPADE, 1, -1), multiStack(mcov(), 1));
			AddRepairRecipe(new ItemStack(Item.IRON_SWORD, 1, -1), multiStack(mcov(), 2));
			AddRepairRecipe(new ItemStack(Item.IRON_HOE, 1, -1), multiStack(mcov(), 2));
			AddRepairRecipe(new ItemStack(Item.IRON_AXE, 1, -1), multiStack(mcov(), 3));
			AddRepairRecipe(new ItemStack(Item.IRON_PICKAXE, 1, -1), multiStack(mcov(), 3));
			AddRepairRecipe(new ItemStack(Item.GOLD_SPADE, 1, -1), multiStack(mcov(), 1));
			AddRepairRecipe(new ItemStack(Item.GOLD_SWORD, 1, -1), multiStack(mcov(), 1));
			AddRepairRecipe(new ItemStack(Item.GOLD_HOE, 1, -1), multiStack(mcov(), 1));
			AddRepairRecipe(new ItemStack(Item.GOLD_AXE, 1, -1), multiStack(mcov(), 1));
			AddRepairRecipe(new ItemStack(Item.GOLD_PICKAXE, 1, -1), multiStack(mcov(), 1));
			AddRepairRecipe(new ItemStack(Item.GOLD_CHESTPLATE, 1, -1), multiStack(mcov(), 8));
			AddRepairRecipe(new ItemStack(Item.GOLD_BOOTS, 1, -1), multiStack(mcov(), 4));
			AddRepairRecipe(new ItemStack(Item.GOLD_LEGGINGS, 1, -1), multiStack(mcov(), 7));
			AddRepairRecipe(new ItemStack(Item.GOLD_HELMET, 1, -1), multiStack(mcov(), 5));
			AddRepairRecipe(new ItemStack(Item.DIAMOND_SPADE, 1, -1), multiStack(hcov(), 1));
			AddRepairRecipe(new ItemStack(Item.DIAMOND_SWORD, 1, -1), multiStack(hcov(), 2));
			AddRepairRecipe(new ItemStack(Item.DIAMOND_HOE, 1, -1), multiStack(hcov(), 2));
			AddRepairRecipe(new ItemStack(Item.DIAMOND_AXE, 1, -1), multiStack(hcov(), 3));
			AddRepairRecipe(new ItemStack(Item.DIAMOND_PICKAXE, 1, -1), multiStack(hcov(), 3));
			AddRepairRecipe(new ItemStack(Item.DIAMOND_CHESTPLATE, 1, -1), multiStack(hcov(), 8));
			AddRepairRecipe(new ItemStack(Item.DIAMOND_BOOTS, 1, -1), multiStack(hcov(), 4));
			AddRepairRecipe(new ItemStack(Item.DIAMOND_LEGGINGS, 1, -1), multiStack(hcov(), 7));
			AddRepairRecipe(new ItemStack(Item.DIAMOND_HELMET, 1, -1), multiStack(hcov(), 5));
		}
	}

	public static void InitEERecipes() {
		debugRecipes();
		ModLoader.addShapelessRecipe(new ItemStack(EEItem.mobiusFuel, 4), new Object[] {pstone(), EEItem.aeternalisFuel});
		ModLoader.addShapelessRecipe(new ItemStack(EEItem.aeternalisFuel), new Object[] {pstone(), EEItem.mobiusFuel, EEItem.mobiusFuel, EEItem.mobiusFuel,
				EEItem.mobiusFuel});
		ModLoader.addShapelessRecipe(new ItemStack(EEItem.alchemicalCoal, 4), new Object[] {pstone(), EEItem.mobiusFuel});
		ModLoader.addShapelessRecipe(new ItemStack(EEItem.mobiusFuel), new Object[] {pstone(), EEItem.alchemicalCoal, EEItem.alchemicalCoal,
				EEItem.alchemicalCoal, EEItem.alchemicalCoal});
		ModLoader.addShapelessRecipe(new ItemStack(Item.COAL, 4, 0), new Object[] {pstone(), EEItem.alchemicalCoal});
		ModLoader.addShapelessRecipe(new ItemStack(EEItem.alchemicalCoal), new Object[] {pstone(), new ItemStack(Item.COAL, 1, 0),
				new ItemStack(Item.COAL, 1, 0), new ItemStack(Item.COAL, 1, 0), new ItemStack(Item.COAL, 1, 0)});
		ModLoader.addShapelessRecipe(new ItemStack(Item.COAL, 4, 1), new Object[] {pstone(), new ItemStack(Item.COAL, 1, 0)});
		ModLoader.addShapelessRecipe(new ItemStack(Item.COAL, 1, 0), new Object[] {pstone(), new ItemStack(Item.COAL, 1, 1), new ItemStack(Item.COAL, 1, 1),
				new ItemStack(Item.COAL, 1, 1), new ItemStack(Item.COAL, 1, 1)});
		ModLoader.addShapelessRecipe(new ItemStack(Item.DIAMOND, 1),
				new Object[] {pstone(), Item.GOLD_INGOT, Item.GOLD_INGOT, Item.GOLD_INGOT, Item.GOLD_INGOT});
		ModLoader.addShapelessRecipe(new ItemStack(Item.GOLD_INGOT, 4), new Object[] {pstone(), Item.DIAMOND});
		ModLoader.addShapelessRecipe(new ItemStack(Item.GOLD_INGOT, 1), new Object[] {pstone(), Item.IRON_INGOT, Item.IRON_INGOT, Item.IRON_INGOT,
				Item.IRON_INGOT, Item.IRON_INGOT, Item.IRON_INGOT, Item.IRON_INGOT, Item.IRON_INGOT});
		ModLoader.addShapelessRecipe(new ItemStack(Item.IRON_INGOT, 8), new Object[] {pstone(), Item.GOLD_INGOT});
		ModLoader.addShapelessRecipe(new ItemStack(Block.ICE), new Object[] {new ItemStack(EEItem.zeroRing, 1, -1), Item.WATER_BUCKET});
		ModLoader.addShapelessRecipe(new ItemStack(Block.ICE), new Object[] {new ItemStack(EEItem.arcaneRing, 1, -1), Item.WATER_BUCKET});
		ModLoader.addShapelessRecipe(new ItemStack(Block.GRASS), new Object[] {new ItemStack(EEItem.harvestRing, 1, -1), Block.DIRT});
		ModLoader.addShapelessRecipe(new ItemStack(Block.GRASS), new Object[] {new ItemStack(EEItem.arcaneRing, 1, -1), Block.DIRT});
		ModLoader.addShapelessRecipe(new ItemStack(Item.WATER_BUCKET), new Object[] {new ItemStack(EEItem.evertide, 1, -1), Item.BUCKET});
		ModLoader.addShapelessRecipe(new ItemStack(Item.LAVA_BUCKET), new Object[] {new ItemStack(EEItem.volcanite, 1, -1), Item.BUCKET, Item.REDSTONE});

		if (EEBase.props.getInt("AllowPedestals") == 1) {
			ModLoader.addRecipe(EEBlock.pedestal, new Object[] {"R#R", "R#R", "###", Character.valueOf('R'), EEItem.redMatter, Character.valueOf('#'),
					EEBlock.dmBlock});
		}

		ModLoader.addRecipe(new ItemStack(EEItem.philStone), new Object[] {"LRL", "RXR", "LRL", Character.valueOf('R'), Item.REDSTONE, Character.valueOf('L'),
				Item.GLOWSTONE_DUST, Character.valueOf('X'), Item.DIAMOND});
		ModLoader.addRecipe(new ItemStack(EEItem.philStone), new Object[] {"RLR", "LXL", "RLR", Character.valueOf('R'), Item.REDSTONE, Character.valueOf('L'),
				Item.GLOWSTONE_DUST, Character.valueOf('X'), Item.DIAMOND});

		if (EEBase.props.getInt("AllowTransmutationTable") == 1) {
			ModLoader.addRecipe(new ItemStack(EEBlock.eeDevice, 1, 0),
					new Object[] {"DSD", "SPS", "DSD", Character.valueOf('D'), Block.OBSIDIAN, Character.valueOf('S'), Block.STONE, Character.valueOf('P'),
							pstone()});
			ModLoader.addRecipe(new ItemStack(EEItem.transTablet),
					new Object[] {"DSD", "SPS", "DSD", Character.valueOf('D'), EEBlock.dmBlock, Character.valueOf('S'), Block.STONE, Character.valueOf('P'),
							new ItemStack(EEBlock.eeDevice, 1, 0)});
		}

		ModLoader.addRecipe(new ItemStack(EEItem.darkMatter),
				new Object[] {"FFF", "FDF", "FFF", Character.valueOf('D'), Block.DIAMOND_BLOCK, Character.valueOf('F'), EEItem.aeternalisFuel});
		ModLoader.addRecipe(new ItemStack(EEBlock.eeStone, 4, 8), new Object[] {"DD", "DD", Character.valueOf('D'), EEItem.darkMatter});
		ModLoader.addRecipe(new ItemStack(EEItem.redMatter),
				new Object[] {"FFF", "DDD", "FFF", Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('F'), EEItem.aeternalisFuel});
		ModLoader.addRecipe(new ItemStack(EEBlock.eeStone, 4, 9), new Object[] {"DD", "DD", Character.valueOf('D'), EEItem.redMatter});

		if (EEBase.props.getInt("AllowDMTools") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.darkPickaxe),
					new Object[] {"###", " D ", " D ", Character.valueOf('#'), EEItem.darkMatter, Character.valueOf('D'), Item.DIAMOND});
			ModLoader.addRecipe(new ItemStack(EEItem.darkSpade),
					new Object[] {" # ", " D ", " D ", Character.valueOf('#'), EEItem.darkMatter, Character.valueOf('D'), Item.DIAMOND});
			ModLoader.addRecipe(new ItemStack(EEItem.darkHoe),
					new Object[] {"## ", " D ", " D ", Character.valueOf('#'), EEItem.darkMatter, Character.valueOf('D'), Item.DIAMOND});
			ModLoader.addRecipe(new ItemStack(EEItem.darkSword),
					new Object[] {" # ", " # ", " D ", Character.valueOf('#'), EEItem.darkMatter, Character.valueOf('D'), Item.DIAMOND});
			ModLoader.addRecipe(new ItemStack(EEItem.darkAxe),
					new Object[] {"## ", "#D ", " D ", Character.valueOf('#'), EEItem.darkMatter, Character.valueOf('D'), Item.DIAMOND});
			ModLoader.addRecipe(new ItemStack(EEItem.darkShears), new Object[] {" # ", "D  ", Character.valueOf('#'), EEItem.darkMatter,
					Character.valueOf('D'), Item.DIAMOND});
			ModLoader.addRecipe(new ItemStack(EEItem.darkHammer),
					new Object[] {"#D#", " D ", " D ", Character.valueOf('#'), EEItem.darkMatter, Character.valueOf('D'), Item.DIAMOND});
		}

		if (EEBase.props.getInt("AllowRMTools") == 1) {
			ModLoader.addRecipe(
					new ItemStack(EEItem.redPickaxe),
					new Object[] {"###", " T ", " D ", Character.valueOf('#'), EEItem.redMatter, Character.valueOf('D'), EEItem.darkMatter,
							Character.valueOf('T'), new ItemStack(EEItem.darkPickaxe, 1, -1)});
			ModLoader.addRecipe(
					new ItemStack(EEItem.redSpade),
					new Object[] {" # ", " T ", " D ", Character.valueOf('#'), EEItem.redMatter, Character.valueOf('D'), EEItem.darkMatter,
							Character.valueOf('T'), new ItemStack(EEItem.darkSpade, 1, -1)});
			ModLoader.addRecipe(
					new ItemStack(EEItem.redHoe),
					new Object[] {"## ", " T ", " D ", Character.valueOf('#'), EEItem.redMatter, Character.valueOf('D'), EEItem.darkMatter,
							Character.valueOf('T'), new ItemStack(EEItem.darkHoe, 1, -1)});
			ModLoader.addRecipe(
					new ItemStack(EEItem.redSword),
					new Object[] {" # ", " # ", " T ", Character.valueOf('#'), EEItem.redMatter, Character.valueOf('D'), EEItem.darkMatter,
							Character.valueOf('T'), new ItemStack(EEItem.darkSword, 1, -1)});
			ModLoader.addRecipe(
					new ItemStack(EEItem.redAxe),
					new Object[] {"## ", "#T ", " D ", Character.valueOf('#'), EEItem.redMatter, Character.valueOf('D'), EEItem.darkMatter,
							Character.valueOf('T'), new ItemStack(EEItem.darkAxe, 1, -1)});
			ModLoader.addRecipe(new ItemStack(EEItem.redShears), new Object[] {" #", "T ", Character.valueOf('#'), EEItem.redMatter, Character.valueOf('D'),
					EEItem.darkMatter, Character.valueOf('T'), new ItemStack(EEItem.darkShears, 1, -1)});
			ModLoader.addRecipe(
					new ItemStack(EEItem.redHammer),
					new Object[] {"#D#", " T ", " D ", Character.valueOf('#'), EEItem.redMatter, Character.valueOf('D'), EEItem.darkMatter,
							Character.valueOf('T'), new ItemStack(EEItem.darkHammer, 1, -1)});
			ModLoader.addShapelessRecipe(new ItemStack(EEItem.redKatar), new Object[] {new ItemStack(EEItem.redShears, 1, -1),
					new ItemStack(EEItem.redAxe, 1, -1), new ItemStack(EEItem.redSword, 1, -1), new ItemStack(EEItem.redHoe, 1, -1), EEItem.redMatter,
					EEItem.redMatter, EEItem.redMatter, EEItem.redMatter, EEItem.redMatter});
			ModLoader.addShapelessRecipe(new ItemStack(EEItem.redMace), new Object[] {new ItemStack(EEItem.redHammer, 1, -1),
					new ItemStack(EEItem.redPickaxe, 1, -1), new ItemStack(EEItem.redSpade, 1, -1), EEItem.redMatter, EEItem.redMatter, EEItem.redMatter,
					EEItem.redMatter, EEItem.redMatter, EEItem.redMatter});
		}

		if (EEBase.props.getInt("AllowDMArmor") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.darkMatterArmor, 1), new Object[] {"X X", "XXX", "XXX", Character.valueOf('X'), EEItem.darkMatter});
			ModLoader.addRecipe(new ItemStack(EEItem.darkMatterHelmet, 1), new Object[] {"XXX", "X X", Character.valueOf('X'), EEItem.darkMatter});
			ModLoader.addRecipe(new ItemStack(EEItem.darkMatterGreaves, 1), new Object[] {"XXX", "X X", "X X", Character.valueOf('X'), EEItem.darkMatter});
			ModLoader.addRecipe(new ItemStack(EEItem.darkMatterBoots, 1), new Object[] {"X X", "X X", Character.valueOf('X'), EEItem.darkMatter});
		}

		if (EEBase.props.getInt("AllowRMArmor") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.redMatterArmor),
					new Object[] {"XAX", "XXX", "XXX", Character.valueOf('X'), EEItem.redMatter, Character.valueOf('A'), EEItem.darkMatterArmor});
			ModLoader.addRecipe(new ItemStack(EEItem.redMatterHelmet),
					new Object[] {"XXX", "XAX", Character.valueOf('X'), EEItem.redMatter, Character.valueOf('A'), EEItem.darkMatterHelmet});
			ModLoader.addRecipe(new ItemStack(EEItem.redMatterBoots),
					new Object[] {"XAX", "X X", Character.valueOf('X'), EEItem.redMatter, Character.valueOf('A'), EEItem.darkMatterBoots});
			ModLoader.addRecipe(new ItemStack(EEItem.redMatterGreaves),
					new Object[] {"XXX", "XAX", "X X", Character.valueOf('X'), EEItem.redMatter, Character.valueOf('A'), EEItem.darkMatterGreaves});
		}

		if (EEBase.props.getInt("AllowRMArmorPlus") == 1) {
			ModLoader.addShapelessRecipe(new ItemStack(EEItem.redMatterArmorP), new Object[] {EEItem.redMatterArmor, new ItemStack(EEItem.kleinStar6, 1, 1),
					new ItemStack(EEItem.volcanite, 1, -1), EEItem.bodyStone});
			ModLoader.addShapelessRecipe(new ItemStack(EEItem.redMatterHelmetP), new Object[] {EEItem.redMatterHelmet, new ItemStack(EEItem.kleinStar6, 1, 1),
					new ItemStack(EEItem.evertide, 1, -1), EEItem.soulStone});
			ModLoader.addShapelessRecipe(new ItemStack(EEItem.redMatterGreavesP), new Object[] {EEItem.redMatterGreaves,
					new ItemStack(EEItem.kleinStar6, 1, 1), EEItem.eternalDensity, new ItemStack(EEItem.watchOfTime, 1, -1)});
			ModLoader.addShapelessRecipe(new ItemStack(EEItem.redMatterBootsP), new Object[] {EEItem.redMatterBoots, new ItemStack(EEItem.kleinStar6, 1, 1),
					new ItemStack(EEItem.swiftWolfRing, 1, -1), new ItemStack(EEItem.swiftWolfRing, 1, -1)});
		}

		ModLoader.addRecipe(new ItemStack(EEItem.alchemyTome), new Object[] {"LMH", "KBK", "HML", Character.valueOf('L'), lcov(), Character.valueOf('M'),
				mcov(), Character.valueOf('H'), hcov(), Character.valueOf('K'), new ItemStack(EEItem.kleinStar6, 1, 1), Character.valueOf('B'), Item.BOOK});
		ModLoader.addRecipe(new ItemStack(EEItem.alchemyTome), new Object[] {"HML", "KBK", "LMH", Character.valueOf('L'), lcov(), Character.valueOf('M'),
				mcov(), Character.valueOf('H'), hcov(), Character.valueOf('K'), new ItemStack(EEItem.kleinStar6, 1, 1), Character.valueOf('B'), Item.BOOK});

		if (EEBase.props.getInt("AllowFurnaces") == 1) {
			ModLoader.addRecipe(EEBlock.dmFurnace, new Object[] {"DDD", "DFD", "DDD", Character.valueOf('D'), EEBlock.dmBlock, Character.valueOf('F'),
					Block.FURNACE});
			ModLoader.addRecipe(EEBlock.rmFurnace, new Object[] {" R ", "RFR", "   ", Character.valueOf('R'), EEBlock.rmBlock, Character.valueOf('F'),
					EEBlock.dmFurnace});
		}

		if (EEBase.props.getInt("AllowCollectors") == 1) {
			ModLoader.addRecipe(EEBlock.collector, new Object[] {"#G#", "#D#", "#F#", Character.valueOf('#'), Block.GLOWSTONE, Character.valueOf('D'),
					Block.DIAMOND_BLOCK, Character.valueOf('G'), Block.GLASS, Character.valueOf('F'), Block.FURNACE});
			ModLoader.addRecipe(EEBlock.collector2, new Object[] {"#D#", "#C#", "###", Character.valueOf('#'), Block.GLOWSTONE, Character.valueOf('D'),
					EEItem.darkMatter, Character.valueOf('C'), EEBlock.collector});
			ModLoader.addRecipe(EEBlock.collector3, new Object[] {"#D#", "#C#", "###", Character.valueOf('#'), Block.GLOWSTONE, Character.valueOf('D'),
					EEItem.redMatter, Character.valueOf('C'), EEBlock.collector2});
		}

		if (EEBase.props.getInt("AllowCondensers") == 1) {
			ModLoader.addRecipe(EEBlock.condenser, new Object[] {"ODO", "DAD", "ODO", Character.valueOf('O'), Block.OBSIDIAN, Character.valueOf('A'),
					EEBlock.alchChest, Character.valueOf('D'), Item.DIAMOND});
		}

		if (EEBase.props.getInt("AllowChests") == 1) {
			ModLoader.addRecipe(EEBlock.alchChest, new Object[] {"LMH", "SDS", "ICI", Character.valueOf('L'), new ItemStack(EEItem.covalenceDust, 1, 0),
					Character.valueOf('M'), new ItemStack(EEItem.covalenceDust, 1, 1), Character.valueOf('H'), new ItemStack(EEItem.covalenceDust, 1, 2),
					Character.valueOf('C'), Block.CHEST, Character.valueOf('S'), Block.STONE, Character.valueOf('D'), Item.DIAMOND, Character.valueOf('I'),
					Item.IRON_INGOT});
		}

		if (EEBase.props.getInt("AllowAlchemyBags") == 1) {
			for (int var0 = 0; var0 < 16; var0++) {
				ModLoader.addShapelessRecipe(new ItemStack(EEItem.alchemyBag, 1, 15 - var0), new Object[] {new ItemStack(EEItem.alchemyBag, 1, -1),
						new ItemStack(Item.INK_SACK, 1, var0)});
				ModLoader.addRecipe(new ItemStack(EEItem.alchemyBag, 1, var0), new Object[] {"HHH", "WCW", "WWW", Character.valueOf('H'),
						new ItemStack(EEItem.covalenceDust, 1, 2), Character.valueOf('W'), new ItemStack(Block.WOOL, 1, var0), Character.valueOf('C'),
						EEBlock.alchChest});
			}
		}

		if (EEBase.props.getInt("AllowNovaC1") == 1) {
			ModLoader.addShapelessRecipe(new ItemStack(EEBlock.eeStone, 2, 10), new Object[] {Block.TNT, EEItem.mobiusFuel});
		}

		if (EEBase.props.getInt("AllowNovaC2") == 1) {
			ModLoader.addShapelessRecipe(new ItemStack(EEBlock.eeStone, 2, 11), new Object[] {new ItemStack(EEBlock.eeStone, 1, 10), EEItem.aeternalisFuel});
		}

		if (EEBase.props.getInt("AllowInterdiction") == 1) {
			ModLoader.addRecipe(new ItemStack(EEBlock.eeTorch, 2), new Object[] {"TDT", "DPD", "GGG", Character.valueOf('T'), Block.REDSTONE_TORCH_ON,
					Character.valueOf('D'), Item.DIAMOND, Character.valueOf('G'), Item.GLOWSTONE_DUST, Character.valueOf('P'), pstone()});
		}

		if (EEBase.props.getInt("AllowRelays") == 1) {
			ModLoader.addRecipe(EEBlock.relay, new Object[] {"OGO", "OAO", "OOO", Character.valueOf('A'), Block.DIAMOND_BLOCK, Character.valueOf('O'),
					Block.OBSIDIAN, Character.valueOf('G'), Block.GLASS});
			ModLoader.addRecipe(EEBlock.relay2, new Object[] {"ODO", "OAO", "OOO", Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('A'),
					EEBlock.relay, Character.valueOf('O'), Block.OBSIDIAN});
			ModLoader.addRecipe(EEBlock.relay3, new Object[] {"ODO", "OAO", "OOO", Character.valueOf('D'), EEItem.redMatter, Character.valueOf('A'),
					EEBlock.relay2, Character.valueOf('O'), Block.OBSIDIAN});
		}

		if (EEBase.props.getInt("AllowDCatalyst") == 1) {
			ModLoader.addRecipe(
					new ItemStack(EEItem.catalystStone),
					new Object[] {"#C#", "CFC", "#C#", Character.valueOf('#'), EEBlock.novaCatalyst, Character.valueOf('C'), EEItem.mobiusFuel,
							Character.valueOf('F'), new ItemStack(Item.FLINT_AND_STEEL, 1, -1)});
		}

		if (EEBase.props.getInt("AllowHKLens") == 1) {
			ModLoader.addRecipe(
					new ItemStack(EEItem.hyperkineticLens),
					new Object[] {"DDD", "MCM", "DDD", Character.valueOf('D'), Item.DIAMOND, Character.valueOf('C'), EEBlock.novaCatalyst,
							Character.valueOf('M'), EEItem.darkMatter});
		}

		if (EEBase.props.getInt("AllowHCLens") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.hyperCatalyst),
					new Object[] {"DDD", "CDL", "DDD", Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('C'),
							new ItemStack(EEItem.catalystStone, 1, -1), Character.valueOf('L'), new ItemStack(EEItem.hyperkineticLens, 1, -1)});
			ModLoader.addRecipe(new ItemStack(EEItem.hyperCatalyst),
					new Object[] {"DDD", "LDC", "DDD", Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('C'),
							new ItemStack(EEItem.catalystStone, 1, -1), Character.valueOf('L'), new ItemStack(EEItem.hyperkineticLens, 1, -1)});
		}

		if (EEBase.props.getInt("AllowSoulstone") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.soulStone),
					new Object[] {"LLL", "DXD", "LLL", Character.valueOf('L'), Item.GLOWSTONE_DUST, Character.valueOf('X'), new ItemStack(Item.INK_SACK, 1, 4),
							Character.valueOf('D'), EEItem.redMatter});
		}

		if (EEBase.props.getInt("AllowBodystone") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.bodyStone), new Object[] {"LLL", "DXD", "LLL", Character.valueOf('L'), Item.SUGAR, Character.valueOf('X'),
					new ItemStack(Item.INK_SACK, 1, 4), Character.valueOf('D'), EEItem.redMatter});
		}

		if (EEBase.props.getInt("AllowLifestone") == 1) {
			ModLoader.addShapelessRecipe(new ItemStack(EEItem.lifeStone), new Object[] {EEItem.bodyStone, EEItem.soulStone});
		}

		if (EEBase.props.getInt("AllowMindstone") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.mindStone), new Object[] {"LLL", "DXD", "LLL", Character.valueOf('L'), Item.BOOK, Character.valueOf('X'),
					new ItemStack(Item.INK_SACK, 1, 4), Character.valueOf('D'), EEItem.redMatter});
		}

		if (EEBase.props.getInt("AllowEvertide") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.evertide),
					new Object[] {"###", "DDD", "###", Character.valueOf('#'), Item.WATER_BUCKET, Character.valueOf('D'), EEItem.darkMatter});
		}

		if (EEBase.props.getInt("AllowVolcanite") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.volcanite),
					new Object[] {"BBB", "###", "BBB", Character.valueOf('B'), Item.LAVA_BUCKET, Character.valueOf('#'), EEItem.darkMatter});
		}

		ModLoader.addRecipe(new ItemStack(EEItem.baseRing), new Object[] {"###", "#X#", "###", Character.valueOf('#'), Item.IRON_INGOT, Character.valueOf('X'),
				new ItemStack(EEItem.volcanite, 1, -1)});
		ModLoader.addRecipe(new ItemStack(EEItem.baseRing), new Object[] {"###", "#X#", "###", Character.valueOf('#'), Item.IRON_INGOT, Character.valueOf('X'),
				Item.LAVA_BUCKET});

		if (EEBase.props.getInt("AllowBlackHoleBand") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.attractionRing),
					new Object[] {"###", "DRD", "###", Character.valueOf('#'), Item.STRING, Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('R'),
							EEItem.baseRing});
		}

		if (EEBase.props.getInt("AllowArchangel") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.grimarchRing), new Object[] {"#F#", "DRD", "#F#", Character.valueOf('#'), Item.BOW,
					Character.valueOf('F'), Item.FEATHER, Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('R'), EEItem.baseRing});
		}

		if (EEBase.props.getInt("AllowIgnition") == 1) {
			ModLoader.addRecipe(
					new ItemStack(EEItem.ignitionRing),
					new Object[] {"#F#", "DRD", "#F#", Character.valueOf('#'), Item.FLINT_AND_STEEL, Character.valueOf('F'), EEItem.mobiusFuel,
							Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('R'), EEItem.baseRing});
		}

		if (EEBase.props.getInt("AllowZeroRing") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.zeroRing),
					new Object[] {"#F#", "DRD", "#F#", Character.valueOf('#'), Block.SNOW_BLOCK, Character.valueOf('F'), Item.SNOW_BALL,
							Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('R'), EEItem.baseRing});
		}

		if (EEBase.props.getInt("AllowArcana") == 1) {
			ModLoader.addShapelessRecipe(new ItemStack(EEItem.arcaneRing), new Object[] {new ItemStack(EEItem.ignitionRing, 1, -1),
					new ItemStack(EEItem.zeroRing, 1, -1), new ItemStack(EEItem.swiftWolfRing, 1, -1), new ItemStack(EEItem.harvestRing, 1, -1),
					EEItem.redMatter, EEItem.redMatter, EEItem.redMatter, EEItem.redMatter, EEItem.redMatter});
		}

		if (EEBase.props.getInt("AllowVoidRing") == 1) {
			ModLoader.addShapelessRecipe(new ItemStack(EEItem.voidRing), new Object[] {EEItem.attractionRing, EEItem.eternalDensity, EEItem.redMatter,
					EEItem.redMatter});
		}

		if (EEBase.props.getInt("AllowSwiftWolf") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.swiftWolfRing),
					new Object[] {"DFD", "FBF", "DFD", Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('F'), Item.FEATHER, Character.valueOf('B'),
							EEItem.baseRing});
		}

		if (EEBase.props.getInt("AllowHarvestBand") == 1) {
			ModLoader.addRecipe(
					new ItemStack(EEItem.harvestRing),
					new Object[] {"SYS", "DBD", "SRS", Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('B'), EEItem.baseRing,
							Character.valueOf('S'), Block.SAPLING, Character.valueOf('R'), BlockFlower.RED_ROSE, Character.valueOf('Y'),
							BlockFlower.YELLOW_FLOWER});
			ModLoader.addRecipe(
					new ItemStack(EEItem.harvestRing),
					new Object[] {"SRS", "DBD", "SYS", Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('B'), EEItem.baseRing,
							Character.valueOf('S'), Block.SAPLING, Character.valueOf('R'), BlockFlower.RED_ROSE, Character.valueOf('Y'),
							BlockFlower.YELLOW_FLOWER});
		}

		if (EEBase.props.getInt("AllowDiviningRod") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.diviningRod, 1, 0),
					new Object[] {"LLL", "LSL", "LLL", Character.valueOf('S'), Item.STICK, Character.valueOf('L'), new ItemStack(EEItem.covalenceDust, 1, 0)});
			ModLoader.addRecipe(new ItemStack(EEItem.diviningRod, 1, 1), new Object[] {"LLL", "LSL", "LLL", Character.valueOf('S'),
					new ItemStack(EEItem.diviningRod, 1, 0), Character.valueOf('L'), new ItemStack(EEItem.covalenceDust, 1, 1)});
			ModLoader.addRecipe(new ItemStack(EEItem.diviningRod, 1, 2), new Object[] {"LLL", "LSL", "LLL", Character.valueOf('S'),
					new ItemStack(EEItem.diviningRod, 1, 1), Character.valueOf('L'), new ItemStack(EEItem.covalenceDust, 1, 2)});
		}

		if (EEBase.props.getInt("AllowRepair") == 1) {
			ModLoader.addRecipe(
					new ItemStack(EEItem.repairCharm),
					new Object[] {"LMH", "SBS", "HML", Character.valueOf('S'), Item.STRING, Character.valueOf('B'), Item.PAPER, Character.valueOf('L'),
							new ItemStack(EEItem.covalenceDust, 1, 0), Character.valueOf('M'), new ItemStack(EEItem.covalenceDust, 1, 1),
							Character.valueOf('H'), new ItemStack(EEItem.covalenceDust, 1, 2)});
			ModLoader.addRecipe(
					new ItemStack(EEItem.repairCharm),
					new Object[] {"HML", "SBS", "LMH", Character.valueOf('S'), Item.STRING, Character.valueOf('B'), Item.PAPER, Character.valueOf('L'),
							new ItemStack(EEItem.covalenceDust, 1, 0), Character.valueOf('M'), new ItemStack(EEItem.covalenceDust, 1, 1),
							Character.valueOf('H'), new ItemStack(EEItem.covalenceDust, 1, 2)});
		}

		if (EEBase.props.getInt("AllowWatchOfTime") == 1) {
			ModLoader.addRecipe(
					new ItemStack(EEItem.watchOfTime),
					new Object[] {"DOD", "GCG", "DOD", Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('O'), Block.OBSIDIAN,
							Character.valueOf('G'), Block.GLOWSTONE, Character.valueOf('C'), Item.WATCH});
			ModLoader.addRecipe(
					new ItemStack(EEItem.watchOfTime),
					new Object[] {"DGD", "OCO", "DGD", Character.valueOf('D'), EEItem.darkMatter, Character.valueOf('O'), Block.OBSIDIAN,
							Character.valueOf('G'), Block.GLOWSTONE, Character.valueOf('C'), Item.WATCH});
		}

		if (EEBase.props.getInt("AllowMercurial") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.mercurialEye),
					new Object[] {"OBO", "BRB", "BDB", Character.valueOf('D'), Item.DIAMOND, Character.valueOf('O'), Block.OBSIDIAN, Character.valueOf('R'),
							EEItem.redMatter, Character.valueOf('B'), Block.BRICK});
		}

		if (EEBase.props.getInt("AllowEternalDensity") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.eternalDensity),
					new Object[] {"DOD", "MDM", "DOD", Character.valueOf('M'), EEItem.darkMatter, Character.valueOf('D'), Item.DIAMOND, Character.valueOf('O'),
							Block.OBSIDIAN});
			ModLoader.addRecipe(new ItemStack(EEItem.eternalDensity),
					new Object[] {"DMD", "ODO", "DMD", Character.valueOf('M'), EEItem.darkMatter, Character.valueOf('D'), Item.DIAMOND, Character.valueOf('O'),
							Block.OBSIDIAN});
		}

		if (EEBase.props.getInt("AllowKleinStar") == 1) {
			ModLoader.addRecipe(new ItemStack(EEItem.kleinStar1),
					new Object[] {"FFF", "FDF", "FFF", Character.valueOf('F'), EEItem.mobiusFuel, Character.valueOf('D'), Item.DIAMOND});
			addKleinForMerge(EEItem.kleinStar1);
			ModLoader.addRecipe(new ItemStack(EEItem.kleinStar2), new Object[] {"FF", "FF", Character.valueOf('F'), new ItemStack(EEItem.kleinStar1, 1, -1)});
			addKleinForMerge(EEItem.kleinStar2);
			ModLoader.addRecipe(new ItemStack(EEItem.kleinStar3), new Object[] {"FF", "FF", Character.valueOf('F'), new ItemStack(EEItem.kleinStar2, 1, -1)});
			addKleinForMerge(EEItem.kleinStar3);
			ModLoader.addRecipe(new ItemStack(EEItem.kleinStar4), new Object[] {"FF", "FF", Character.valueOf('F'), new ItemStack(EEItem.kleinStar3, 1, -1)});
			addKleinForMerge(EEItem.kleinStar4);
			ModLoader.addRecipe(new ItemStack(EEItem.kleinStar5), new Object[] {"FF", "FF", Character.valueOf('F'), new ItemStack(EEItem.kleinStar4, 1, -1)});
			addKleinForMerge(EEItem.kleinStar5);
			ModLoader.addRecipe(new ItemStack(EEItem.kleinStar6), new Object[] {"FF", "FF", Character.valueOf('F'), new ItemStack(EEItem.kleinStar5, 1, -1)});
			addKleinForMerge(EEItem.kleinStar6);
		}
	}

	private static void debugRecipes() {}

	public static ItemStack cobble(int var0) {
		return new ItemStack(Block.COBBLESTONE, var0);
	}

	public static ItemStack iing(int var0) {
		return new ItemStack(Item.IRON_INGOT, var0);
	}

	public static ItemStack diamond(int var0) {
		return new ItemStack(Item.DIAMOND, var0);
	}

	public static ItemStack redstone(int var0) {
		return new ItemStack(Item.REDSTONE, var0);
	}

	public static ItemStack coal(int var0, int var1) {
		return new ItemStack(Item.COAL, var0, var1);
	}

	public static ItemStack glowdust(int var0) {
		return new ItemStack(Item.GLOWSTONE_DUST, var0);
	}

	public static ItemStack alcoal(int var0) {
		return new ItemStack(EEItem.alchemicalCoal, var0);
	}

	public static ItemStack glowblock(int var0) {
		return new ItemStack(Block.GLOWSTONE, var0);
	}

	public static ItemStack mobius(int var0) {
		return new ItemStack(EEItem.mobiusFuel, var0);
	}

	public static ItemStack coval(int var0, int var1) {
		return new ItemStack(EEItem.covalenceDust, var0, var1);
	}

	public static ItemStack lcov() {
		return coval(1, 0);
	}

	public static ItemStack mcov() {
		return coval(1, 1);
	}

	public static ItemStack hcov() {
		return coval(1, 2);
	}

	public static ItemStack pstone() {
		return new ItemStack(EEItem.philStone, 1, -1);
	}

	public static void addKleinForMerge(Item var0) {
		EEMergeLib.addMergeOnCraft(var0);
	}

	public static void addRingDestroy(Item var0) {
		EEMergeLib.addDestroyOnCraft(var0);
	}

	public static Object[] multiStack(ItemStack var0, int var1) {
		Object[] var2 = new Object[var1];

		for (int var3 = 0; var3 < var1; var3++) {
			var2[var3] = var0;
		}

		return var2;
	}

	public static boolean isValidEDItem(ItemStack var0) {
		int var1 = var0.id;
		return (var1 == Block.COBBLESTONE.id) || (var1 == Block.DIRT.id) || (var1 == Block.SAND.id) || (var1 == Block.NETHERRACK.id)
				|| (var1 == Block.SOUL_SAND.id) || (var1 == Block.GRAVEL.id) || (var1 == Block.SANDSTONE.id) || (var1 == Block.OBSIDIAN.id)
				|| (var1 == Block.LEAVES.id) || (var1 == Block.SNOW_BLOCK.id) || (var1 == Item.IRON_INGOT.id) || (var1 == Item.GOLD_INGOT.id)
				|| (var1 == Item.DIAMOND.id) || (var1 == EEItem.darkMatter.id);
	}

	public static void addAlchemicalValue(ItemStack var0, int var1) {
		if (var0 != null) {
			addEMC(var0.id, var0.getData(), var1);
		}
	}

	public static void addChargedItem(ItemStack var0) {
		if (var0 != null) {
			addChargedItem(var0.id);
		}
	}

	public static void addOreBlock(ItemStack var0) {
		if (var0 != null) {
			addOreBlock(var0.id);
		}
	}

	public static void addLeafBlock(ItemStack var0) {
		if (var0 != null) {
			addLeafBlock(var0.id);
		}
	}

	public static void addWoodBlock(ItemStack var0) {
		if (var0 != null) {
			addWoodBlock(var0.id);
		}
	}
}