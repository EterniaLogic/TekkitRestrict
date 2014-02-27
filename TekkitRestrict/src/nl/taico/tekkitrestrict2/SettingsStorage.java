package nl.taico.tekkitrestrict2;

import java.util.ArrayList;
import java.util.List;

public class SettingsStorage {
	public static ConfigManager manager = new ConfigManager();
	public static TRConfig bannedConfig,
						   groupPermsConfig,
						   safeZoneConfig,
						   hackDupeConfig,
						   databaseConfig,
						   performanceConfig,
						   limitedCreativeConfig,
						   limiterConfig,
						   loggingConfig,
						   modModificationsConfig,
						   unloadConfig;
	
	private static final String[] header = {
		"Configuration file for TekkitRestrict",
		"Authors: Taeir, DreadEnd (aka DreadSlicer)",
		"BukkitDev: http://dev.bukkit.org/server-mods/tekkit-restrict/",
		"Please ask questions/report issues on the BukkitDev page."
	};
	
	public static void genBanned(){
		bannedConfig = manager.getNewConfig("Banned.yml", header, 93);
		
		{
			final String[] comment = new String[] {
					"###################################################################################################",
					"########################################### Banned Items ##########################################",
					"###################################################################################################",
					"Should disabledItemBlocks be removed from the map?",
					"WARNING: It can cause lag as the complete map has to be searched for disabled blocks.",
					"Default: false"
			};
			bannedConfig.set("RemoveDisabledItemBlocks", false, comment);
		}
		{
			final String[] comment = new String[] {
					"When a disabled item is found in someone's inventory, it is changed into this item ID.",
					"Default: 3 (dirt)"
			};
			bannedConfig.set("ChangeDisabledItemsIntoId", false, comment);
		}
		{
			final String[] comment = new String[] {
				"All Items listed below will be banned. This means that if a player does not have the bypass "
				+ "permission (tekkitrestrict.bypass.noitem), any item listed here will be uncraftable for him. If "
				+ "he has an item listed here in his inventory, it will be changed to the item specified by "
				+ "ChangeDisabledItemsIntoId (default dirt), and he will be informed with the message you set, or "
				+ "a default message.",
				"",
				"You can also use individual permissions to add bans to some players only.",
				"The individual permission is: tekkitrestrict.noitem.ID.DATA",
				"",
				"There are multiple ways to add items to this list:",
				"1. You can use single id's:",
				"- \"12\"",
				"- \"13:5\"",
				"2. You can use ranges(*1):",
				"- \"20-30\"",
				"- \"30-45:5\"(*2)",
				"3. You can use EE and IC2 item names(*3) (without spaces):",
				"- \"RedMatterPickaxe\"",
				"- \"Jetpack\"",
				"4. You can use preset groups (NOT caseSENSItive):",
				"- \"ee\"",
				"- \"buildcraft\"",
				"5. You can also use groups you made yourself in the GroupPermissions config.",
				"",
				"You can also set the message that is shown to a player when he tries to craft or click on a banned item like so:",
				"- \"50 {We don't like torches on this server...}\"",
				"- \"EE {Equivalent Exchange is too overpowered, so it is banned!}\"",
				"- \"20-30 {Items 20 to 30 are banned!}\"",
				"",
				"You can add colours(*4) and styling(*5) to these messages:",
				"- \"EE {&0&n(Black Underlined)NO EE!}\"",
				"",
				"You can add line breaks with \\n:",
				"- \"50 {We don't like torches on this server...\\nSo you are not allowed to have them!}\"",
				"",
				"These are all preset Groups (NOT caseSENSItive):",
				"EE, RedPowerCore, RedPowerControl, RedPowerLogic, RedPowerMachine, RedPowerLighting, WirelessRedstone, BuildCraft, AdditionalPipes, IronChests, "
				+ "IndustrialCraft, IC2, NuclearControl, CompactSolars, ChargingBench, PowerConverters, MFFS, RailCraft, TubeStuff, AdvancedMachines, WeaponMod, "
				+ "EnderChest and ChunkLoaders",
				"",
				"*1: Ranges are inclusive, 20-22 means items 20, 21 and 22.",
				"*2: 15-17:10 means items 15:10, 16:10 and 17:10.",
				"*3: Not all items names are included. You will be informed in the console when you add",
				"    an item that is not known.",
				"    You can also use /tr warnings config to view these warnings.",
				"*4: You can add colours with &0 to &9 and &a to &f.",
				"*5: You can add styling with &k to &o. &r will reset all styling and colours.",
			};
			bannedConfig.set("BannedItems", new ArrayList<String>(), comment);
		}
		{
			final String[] comment = {
					"###################################################################################################",
					"######################################## Banned Interacts #########################################",
					"###################################################################################################",
					"If you enable this, TekkitRestrict will check on every click action if the player that clicked has the permission \"tekkitrestrict.noclick.id.data[.left|right|trample]\"",
					"As this thus checks up to 2 permissions for each click action, it might cause some lag.",
					"Default: false"
			};
			bannedConfig.set("UseNoClickPermissions", false, comment);
		}
		{
			final String[] comment = {
				"######################### Disable the left or right click with the item. ##########################",
				"- \"27562\"                          All Item Data Types, Left and Right Clicking",
				"- \"27562 left\"                     All Item Data Types, Left clicking",
				"- \"27562-27566 left\"               All Items in range, Left clicking",
				"- \"27562:1 right\"                  Prevent right click (in the air and on blocks)",
				"- \"27562:1 both\"                   Prevent left and right click (in the air and on blocks)",
				"- \"27562:1 trample\"                Prevent trampling while holding this item",
				"- \"27562:1 all\"                    Prevent clicking and trampling with this item",
				"                                   (in the air and on blocks)",
				"- \"27562:1 left safezone\"          Prevent left-clicking with this item in a safezone",
				"                                   (in the air and on blocks)",
				"- \"27562:1 safezone\"               Prevent clicking with this item in a safezone",
				"                                   (in the air and on blocks)",
				"- \"27562:1 right air\"              Prevent right-clicking with this item in the air",
				"- \"27562:1 both block\"             Prevent clicking with this item on a block",
				"- \"27562:1 all air safezone\"       Prevents clicking and trampling in the air in a safezone.",
				"- \"27562-27566 all block safezone\" Prevents clicking and trampling on blocks in a safezone with",
				"                                   a range of items",
				"- \"ee left\"                        Prevents EE items from being right-clicked",
				"",
				"############################ Disable the GUI or Right-click on a block ############################",
				"- \"block 126:1\"        When you right-click on this block, it will be disallowed.",
				"- \"block 126-150\"      When you right-click on a block in this range, it will be disallowed.",
				"- \"block ee\"           When you right-click on any EE block, it will be disallowed."
			};
			bannedConfig.set("BannedInteracts", new ArrayList<String>(), comment);
		}
		{
			final String[] comment = {
				"###################################################################################################",
				"########################################## Banned Clicks ##########################################",
				"###################################################################################################",
				"Ban clicking on certain items in certain inventories.",
				"",
				"Format: item [inventory] [left|right|shift]",
				"If you do not specify an inventory, the action will be blocked in all inventories",
				"If you do not specify left, right or shift, all clicks will be blocked.",
				"",
				"Examples:",
				"- \"3 Chest right {You are not allowed to rightclick on dirt in chests!}\"",
				"- \"1 left {You are not allowed to leftclick on stone!}\"",
				"- \"1 Recycler {You are not allowed to click on stone in a Recycler!}\"",
				"- \"1 shift {You are not allowed to shiftclick on stone!}\"",
				"- \"1 {You are not allowed to click on stone!}\"",
				"",
				"Possible inventories:",
				"Chest              - Chest or double chest",
				"MinecartChest      - Minecart Chest",
				"BrewingStand",
				"EnchantingTable",
				"Furnace",
				"Dispenser",
				"Inventory          - Player Inventory or crafting window",
				"",
				"AlchemicalChest",
				"Condenser",
				"DMFurnace          - Dark Matter Furnace",
				"RMFurnace          - Red Matter Furnace",
				"Collector          - Energy Collector",
				"EERelay            - Anti-Matter Relay",
				"Pedestal           - Dark Matter Pedestal",
				"TransmutionTable   - Transmution table and tablet",
				"MercurialEye",
				"Alchemy Bag",
				"",
				"Builder",
				"Filler",
				"Template",
				"Engine             - Combustion or Steam Engine",
				"AutoWorkbench",
				"",
				"FFCamoflage        - Forcefield Camouflage",
				"AutoCraftingTable2 - Automatic Crafting Table MK 2",
				"",
				"AlloyFurnace",
				"BlueAlloyFurnace",
				"BlulectricFurnace",
				"Buffer",
				"Deployer",
				"Ejector",
				"Filter",
				"Retriever",
				"Sorter",
				"ItemDetector",
				"Regulator",
				"RPRelay            - RedPower Relay",
				"ProjectTable",
				"",
				"CokeOven",
				"BlastFurnace",
				"CartDispenser",
				"EnergyLoader",
				"EnergyUnloader",
				"LiquidLoader",
				"LiquidUnloader",
				"ItemLoader",
				"AdvItemLoader",
				"AdvItemUnloader",
				"ItemUnloader",
				"RollingMachine    ",
				"",
				"CanningMachine",
				"Compressor",
				"CropMatron",
				"Cropnalyzer",
				"ElectricFurnace",
				"Electrolyzer",
				"Extractor",
				"Generator",
				"GeothermalGenerator",
				"InductionFurnace",
				"IronFurnace",
				"Macerator",
				"MassFabricator",
				"Miner",
				"NuclearReactor",
				"PersonalSafe",
				"Pump",
				"Recycler",
				"SolarPanel",
				"Terraformer",
				"TradeOMat",
				"WaterMill",
				"WindMill"
			};
			bannedConfig.set("BannedClicks", new ArrayList<String>(), comment);
		}
		
		bannedConfig.saveConfig();		
	}
	
	public static void genGroupPerms(){
		groupPermsConfig = manager.getNewConfig("GroupPermissions.yml", header, 93);
		{
			final String[] comment = {
					"###################################################################################################",
					"################################# Group Permissions Configuration #################################",
					"###################################################################################################",
					"PermissionGroups",
					"For more information, see: ",
					"http://dev.bukkit.org/bukkit-plugins/tekkit-restrict/pages/configuration/group-permisisons/",
					"",
					"Here you can add permission groups!",
					"",
					"You can reference items like:",
					"\"10\" (Simple ID) ",
					"\"10:0\" (ID with the only block that corresponds to data type \"0\") ",
					"\"10-20\" (Range of IDs that can be any number in between 10 and 20)",
					"",
					"The permissions will be:",
					"tekkitrestrict.noitem.name or tekkitrestrict.creative.name",
					"",
					"Example:",
					"  groupname: \"12;10:1;13;15-17\"",
					"",
					"If you now give the permission tekkitrestrict.noitem.groupname, the items",
					"12, 10:1, 13, 15, 16 and 17 will be banned items for that player.",
					"If you give the permission tekkitrestrict.creative.groupname, those items will only be",
					"banned if the player is in creative mode."
			};
			groupPermsConfig.set("PermissionGroups.default", "", comment);
		}
		groupPermsConfig.saveConfig();
	}
	
	public static void genSafeZones(){
		safeZoneConfig = manager.getNewConfig("SafeZones.yml", header, 93);
		{
			final String[] comment = {
				"###################################################################################################",
				"##################################### SafeZone Configuration ######################################",
				"###################################################################################################",
				"Should TekkitRestrict use SafeZones?",
				"Default: true"
			};
			safeZoneConfig.set("UseSafeZones", true, comment);
		}
		{
			//safeZoneConfig.set("InSafeZones.Null", true);
			final String[] comment = {
				"# Note: This will only apply to Native, WorldGuard and GriefPrevention SafeZones, and",
				"#       NOT to Towny, Factions or PreciousStones SafeZones.",
				"If you turn DisableEntities on, then",
				"- No mobs will spawn in SafeZones.",
				"- If a mob enters a SafeZone, it is removed.",
				"Default: true"
			};
			safeZoneConfig.set("InSafeZones DisableEntities", true, comment);
			//safeZoneConfig.set("InSafeZones.Null", null);
		}
		{
			final String[] comment = {
				"This is a feature that allows tekkitrestrict to run the entities disabler thread with less lag and "
			  + "problems. Entities can get removed at range blocks from the corner of the safezone.",
				"",
				"Increase this value if you have problems with the entity remover or if you want to increase "
			  + "performance.",
				"Default: 10"
			};
			safeZoneConfig.set("InSafeZones DisableEntitiesRange", 10, comment);
		}
		{
			final String[] comment = {
				"WARNING: Case Sensitive!",
				"Tries to exclude org.bukkit.entity.[name] entities from SafeZone entity removal.",
				"Examples:",
				"\"Arrow\", \"Animals\", \"EnderDragon\", \"EnderPearl\", \"Fish\", \"IronGolem\", \"Pig\",",
				"\"Projectile\", \"ThrownPotion\", \"TNTPrimed\", \"Snowball\""
			};
			safeZoneConfig.set("InSafeZones ExemptEntityTypes", new ArrayList<String>(), comment);
		}
		{
			final String[] comment = {
				"If you turn DechargeEE on, then",
				"- All EE items specified in the ModModifications config will be decharged in SafeZones",
				"Default: true"
			};
			safeZoneConfig.set("InSafeZones DechargeEE", true, comment);
		}
		{
			final String[] comment = {
				"If you turn DisableRingOfArcana on, then",
				"- When someone is in a SafeZone with a ring of arcana, it will always be set to \"Earth\" mode and "
			  + "it will be turned off.",
				"Default: true"
			};
			safeZoneConfig.set("InSafeZones DisableRingOfArcana", true, comment);
		}
		{
			final String[] comment = {
				"The plugins TekkitRestrict should use to make safezones.",
				"",
				"It is recommended to use tekkitrestrict native safezones, to minimize the strain safezones put on "
			  + "the server.",
				"You can add these safezones with /tr admin safezone addnative <x1> <z1> <x2> <z2> <name>",
				"",
				"There is only basic support for Factions, Towny and PreciousStones.",
				"This means that all land claimed by a faction, a town, etcetera is a safezone.",
				"Players with the build/destroy permission for that land will bypass it.",
				"",
				"For GriefPrevention, you can choose different modes (See below).",
				"For WorldGuard, you can specify per region if it should be a safezone. You can",
				"compare this mode with the \"Specific\" mode of GriefPrevention.",
				"Default: true for all"
			};
			safeZoneConfig.set("SSEnabledPlugins.TekkitRestrict", true, comment);
			safeZoneConfig.set("SSEnabledPlugins.GriefPrevention", true);
			safeZoneConfig.set("SSEnabledPlugins.WorldGuard", true);
			safeZoneConfig.set("SSEnabledPlugins.Factions", true);
			safeZoneConfig.set("SSEnabledPlugins.Towny", true);
			safeZoneConfig.set("SSEnabledPlugins.PreciousStones", true);
		}
		{
			final String[] comment = {
				"###################################################################################################",
				"################################ GriefPrevention Specific settings ################################",
				"###################################################################################################",
				"If someone is a manager in a claim, the SafeZone will not apply for him.",
				"(If you would like a setting to turn this on or off, please make a feature request",
				"ticket on the Bukkit Dev TekkitRestrict page.)",
				"",
				"GriefPrevention SafeZone Method",
				"Can be All, Admin, Specific or SpecificAdmin",
				"All: All GriefPrevention claims are SafeZones.",
				"Admin: All GriefPrevention admin claims are SafeZones. (Default)",
				"Specific: You can specify per claim if you want it to be a SafeZone.",
				"SpecificAdmin: Only admin claims can be SafeZones, but you can specify it per claim."
			};
			safeZoneConfig.set("GriefPreventionSafeZoneMethod", "Admin", comment);
		}
		{
			final String[] comment = {
				"##########################################################################################",
				"############################## WorldGuard Specific settings ##############################",
				"##########################################################################################",
				"WorldGuard SafeZone Method",
				"Can be All or Specific",
				"All: All WorldGuard regions are SafeZones.",
				"Specific: You can specify per region if you want it to be a SafeZone. (Default)"
			};
			safeZoneConfig.set("WorldGuardSafeZoneMethod", "Specific", comment);
		}
		
		safeZoneConfig.saveConfig();
	}
	
	public static void genHackDupe(){
		hackDupeConfig = manager.getNewConfig("HackDupe.yml", header, 93);
		{
			final String[] comment = {
				"###################################################################################################",
				"################################### Anti-Hack Configuration #######################################",
				"###################################################################################################",
				"# Block hackers from screwing your server up!",
				"",
				"# Enabled:          Do you want to enable Anti-Hack for this kind of hack?",
				"#                   Default: All true",
				"",
				"# Tolerance:        The amount of ticks the player has to hack before he is kicked.",
				"#                   If you set this too low, innocent people might get kicked for connection",
				"#                   problems.",
				"#                   Default:",
				"#                       MoveSpeed: 30",
				"#                       Fly: 40",
				"#                       Forcefield: 20",
				"",
				"# MaxMoveSpeed:     The maximum speed a player can have (in blocks per second).",
				"#                   Speeds above this are considered hacking.",
				"#                   People with quantum armor will have 3 times this limit.",
				"#                   Default: 2.5",
				"",
				"# MinHeight:        Minimal Height for the flycheck to kick in.",
				"#                   If you set this too low, people might get kicked for jumping.",
				"#                   Default: 3",
				"",
				"# Angle:            The maximum angle you are allowed to hit a player with.",
				"#                   Default: 40",
				"",
				"# Broadcast:        Should a message be broadcast to all players with the ",
				"#                   tekkitrestrict.notify.hack permission?",
				"#                   Default: All true",
				"",
				"# Kick:             Should a player get kicked if he hacks?",
				"#                   Default: All true",
				"",
				"# ExecuteCommand:",
				"#    Enable:        Should a command be executed when someone hacks for a certain amount",
				"#                   of times?",
				"#                   Default: All true",
				"",
				"#    Command:       The command to execute.",
				"#                   Default: \"\"",
				"#                   NOTE: The following will be replaced:",
				"#                   {PLAYER} - The player's name",
				"#                   {TYPE}   - The type of hack",
				"",
				"#    TriggerAfter:  Set the amount of times the player has to hack before the command is",
				"#                   executed. (Might implement save feature later. Currently only on",
				"#                   the current server session.)",
				"#                   Default: All 1",
				"",
				"# BroadcastString:  The formatting of the BroadcastString.",
				"#                   Default: \"{PLAYER} tried to {TYPE}-hack!\"",
				"#                   NOTE: The following will be replaced:",
				"#                   {PLAYER} - The player's name",
				"#                   {TYPE}   - The type of hack"
			};
			hackDupeConfig.set("Anti-Hacks.MoveSpeed.Enabled", true, comment);
			hackDupeConfig.set("Anti-Hacks.MoveSpeed.Tolerance", 30);
			hackDupeConfig.set("Anti-Hacks.MoveSpeed.MaxMoveSpeed", 2.5);
			hackDupeConfig.set("Anti-Hacks.MoveSpeed.Broadcast", true);
			hackDupeConfig.set("Anti-Hacks.MoveSpeed.Log", true);
			hackDupeConfig.set("Anti-Hacks.MoveSpeed.Kick", true);
			hackDupeConfig.set("Anti-Hacks.MoveSpeed.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Hacks.MoveSpeed.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Hacks.MoveSpeed.ExecuteCommand.TriggerAfter", 1);

			hackDupeConfig.set("Anti-Hacks.Fly.Enabled", true);
			hackDupeConfig.set("Anti-Hacks.Fly.Tolerance", 40);
			hackDupeConfig.set("Anti-Hacks.Fly.MinHeight", 3);
			hackDupeConfig.set("Anti-Hacks.Fly.Broadcast", true);
			hackDupeConfig.set("Anti-Hacks.Fly.Log", true);
			hackDupeConfig.set("Anti-Hacks.Fly.Kick", true);
			hackDupeConfig.set("Anti-Hacks.Fly.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Hacks.Fly.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Hacks.Fly.ExecuteCommand.TriggerAfter", 1);

			hackDupeConfig.set("Anti-Hacks.Forcefield.Enabled", true);
			hackDupeConfig.set("Anti-Hacks.Forcefield.Tolerance", 30);
			hackDupeConfig.set("Anti-Hacks.Forcefield.Angle", 40);
			hackDupeConfig.set("Anti-Hacks.Forcefield.Broadcast", true);
			hackDupeConfig.set("Anti-Hacks.Forcefield.Log", true);
			hackDupeConfig.set("Anti-Hacks.Forcefield.Kick", true);
			hackDupeConfig.set("Anti-Hacks.Forcefield.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Hacks.Forcefield.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Hacks.Forcefield.ExecuteCommand.TriggerAfter", 1);
			hackDupeConfig.set("Anti-Hacks.BroadcastString", "{PLAYER} tried to {TYPE}-hack!");
		}
		{
			final String[] comment = {
				"###################################################################################################",
				"##################################### Anti-Dupe Configuration #####################################",
				"###################################################################################################",
				"Stop players from Duping!",
				"Prevent:          Do you want to prevent this dupe?",
				"                  Default: All true",
				"",
				"Broadcast:        Should a message be broadcast to all players with the ",
				"                  tekkitrestrict.notify.dupe permission?",
				"                  Default: All true",
				"",
				"Kick:             Should players that try to use this dupe be kicked?",
				"                  Default: All false",
				"                  NOTE: It is not recommended to kick players on attempting to dupe. In",
				"                        most cases it was not the players intention to dupe.",
				"",
				"ExecuteCommand:",
				"   Enable:        Should a command be executed when someone uses this dupe for a certain",
				"                  amount of times?",
				"                  Default: All true",
				"",
				"   Command:       The command to execute.",
				"                  Default: \"\"",
				"                  NOTE: The following will be replaced:",
				"                  {PLAYER} - The player's name",
				"                  {TYPE}   - The type of dupe",
				"                  {ID}     - The item ID",
				"                  {DATA}   - The item's damage value",
				"                  {ITEM}   - A string representation of {ID}:{DATA}",
				"",
				"   TriggerAfter:  Set the amount of times the player has to use this dupe before the ",
				"                  command is executed. (Might implement save feature later. Currently",
				"                  only on the current server session.)",
				"                  Default: All 1",
				"",
				"BroadcastString:  The formatting of the BroadcastString for dupes.",
				"                  Default: \"{PLAYER} tried to dupe {ITEM} using {TYPE}!\"",
				"                  NOTE: The following will be replaced:",
				"                  {PLAYER} - The player's name",
				"                  {TYPE}   - The type of dupe",
				"                  {ID}     - The item ID",
				"                  {DATA}   - The item's damage value",
				"                  {ITEM}   - A string representation of {ID}:{DATA}"
			};
			hackDupeConfig.set("Anti-Dupes.AlchemyBagDupe.Prevent", true, comment);
			hackDupeConfig.set("Anti-Dupes.AlchemyBagDupe.Broadcast", true);
			hackDupeConfig.set("Anti-Dupes.AlchemyBagDupe.Kick", false);
			hackDupeConfig.set("Anti-Dupes.AlchemyBagDupe.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Dupes.AlchemyBagDupe.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Dupes.AlchemyBagDupe.ExecuteCommand.TriggerAfter", 1);

			hackDupeConfig.set("Anti-Dupes.RMFurnaceDupe.Prevent", true);
			hackDupeConfig.set("Anti-Dupes.RMFurnaceDupe.Broadcast", true);
			hackDupeConfig.set("Anti-Dupes.RMFurnaceDupe.Kick", false);
			hackDupeConfig.set("Anti-Dupes.RMFurnaceDupe.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Dupes.RMFurnaceDupe.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Dupes.RMFurnaceDupe.ExecuteCommand.TriggerAfter", 1);

			hackDupeConfig.set("Anti-Dupes.TransmuteDupe.Prevent", true);
			hackDupeConfig.set("Anti-Dupes.TransmuteDupe.Broadcast", true);
			hackDupeConfig.set("Anti-Dupes.TransmuteDupe.Kick", false);
			hackDupeConfig.set("Anti-Dupes.TransmuteDupe.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Dupes.TransmuteDupe.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Dupes.TransmuteDupe.ExecuteCommand.TriggerAfter", 1);

			hackDupeConfig.set("Anti-Dupes.DiskDriveDupe.Prevent", true);
			hackDupeConfig.set("Anti-Dupes.DiskDriveDupe.Broadcast", false);
			hackDupeConfig.set("Anti-Dupes.DiskDriveDupe.Kick", false);
			hackDupeConfig.set("Anti-Dupes.DiskDriveDupe.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Dupes.DiskDriveDupe.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Dupes.DiskDriveDupe.ExecuteCommand.TriggerAfter", 1);

			hackDupeConfig.set("Anti-Dupes.TankCartDupe.Prevent", true);
			hackDupeConfig.set("Anti-Dupes.TankCartDupe.Broadcast", true);
			hackDupeConfig.set("Anti-Dupes.TankCartDupe.Kick", false);
			hackDupeConfig.set("Anti-Dupes.TankCartDupe.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Dupes.TankCartDupe.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Dupes.TankCartDupe.ExecuteCommand.TriggerAfter", 1);

			hackDupeConfig.set("Anti-Dupes.TankCartGlitch.Prevent", true);
			hackDupeConfig.set("Anti-Dupes.TankCartGlitch.Broadcast", true);
			hackDupeConfig.set("Anti-Dupes.TankCartGlitch.Kick", false);
			hackDupeConfig.set("Anti-Dupes.TankCartGlitch.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Dupes.TankCartGlitch.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Dupes.TankCartGlitch.ExecuteCommand.TriggerAfter", 1);

			hackDupeConfig.set("Anti-Dupes.PedestalEmcGen.Prevent", true);//if EEPatch >1.9 ignore
			hackDupeConfig.set("Anti-Dupes.PedestalEmcGen.Broadcast", true);
			hackDupeConfig.set("Anti-Dupes.PedestalEmcGen.Kick", false);
			hackDupeConfig.set("Anti-Dupes.PedestalEmcGen.ExecuteCommand.Enabled", false);
			hackDupeConfig.set("Anti-Dupes.PedestalEmcGen.ExecuteCommand.Command", "");
			hackDupeConfig.set("Anti-Dupes.PedestalEmcGen.ExecuteCommand.TriggerAfter", 1);

			hackDupeConfig.set("Anti-Dupes.TeleportDupe.Prevent", true);

			hackDupeConfig.set("Anti-Dupes.BroadcastString", "{PLAYER} tried to dupe {ITEM} using {TYPE}!");
		}
		
		hackDupeConfig.saveConfig();
	}
	
	public static void genDatabase(){
		databaseConfig = manager.getNewConfig("Database.yml", header, 93);
		
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"###################################### Database Configuration #####################################",
				"###################################################################################################",
				"Set the type of database tekkitrestrict should use.",
				"Possible: SQLite, MySQL",
				"Default: SQLite"
			};
			databaseConfig.set("DatabaseType", "SQLite", comment);
		}
		{
			final String[] comment = new String[] {
				"MySQL connection settings"
			};
			databaseConfig.set("MySQL.Hostname", "localhost", comment);
			databaseConfig.set("MySQL.Port", 3306);
			databaseConfig.set("MySQL.Username", "root");
			databaseConfig.set("MySQL.Password", "minecraft");
			databaseConfig.set("MySQL.Database", "minecraft");
		}
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"######################################## Transfer settings ########################################",
				"###################################################################################################",
				"Here you can set if you want to transfer a database from SQLite to MySQL or vice versa. Only one "
			  + "of these options can be true.",
				"",
				"Transfer from SQLite to MySQL",
				"If you set this to true, the data currently in the Data.db file will be written to the MySQL "
			  + "database as set above."
			};
			databaseConfig.set("TransferDBFromSQLiteToMySQL", false, comment);
		}
		
		{
			final String[] comment = new String[] {
				"Transfer from MySQL to SQLite",
				"If you set this to true, the data currently in the MySQL database as set above will be written "
			  + "into a SQLite database file named Data.db."
			};
			databaseConfig.set("TransferDBFromMySQLToSQLite", false, comment);
		}
		
		databaseConfig.saveConfig();
	}

	public static void genPerformance(){
		performanceConfig = manager.getNewConfig("Performance.yml", header, 93);
		
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"#################################### Performance Configuration ####################################",
				"###################################################################################################",
				"Should TekkitRestrict favor server performance over memory usage?",
				"Default: false"
			};
			performanceConfig.set("FavorPerformanceOverMemory", false, comment);
		}
		
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"##################################### Threading Configuration #####################################",
				"###################################################################################################",
				"Do not edit these values unless you know what you are doing.",
				"These numbers are in Milliseconds. (1000 milliseconds = 1 second)",
				"Lower values = more server strain (more lag)",
				"",
				"The amount of time the thread that disables GemArmor powers (see ModModifications config) sleeps "
			  + "for.",
				"If you set this too high, players might be able to use their powers by spamming.",
				"If you set this too low, it might lag the server.",
				"",
				"Recommended: [100-200]",
				"Default: 120"
			};
			performanceConfig.set("GemArmorDThread", 120, comment);
		}
		
		{
			final String[] comment = new String[] {
				"The amount of time the thread that removes entities in SafeZones powers (see SafeZones config) "
			  + "sleeps for.",
				"If removing entities from a SafeZones is not something that is very important for your server, you "
			  + "can raise this to 1000-2000 (1-2 seconds)",
				"",
				"Recommended: [350-2000]",
				"Default: 500"
			};
			performanceConfig.set("SSEntityRemoverThread", 500, comment);
		}
		
		{
			final String[] comment = new String[] {
				"The amount of time the inventory thread sleeps for.",
				"The inventory thread takes care of removing DisabledItems, Decharging EE Tools and Applying the "
			  + "Max EU values.",
				"",
				"Recommended: [250-500]",
				"Default: 400"
			};
			performanceConfig.set("InventoryThread", 400, comment);
		}
		
		{
			final String[] comment = new String[] {
				"The amount of time the WorldCleanerThread sleeps for.",
				"This thread takes care of the Removal of Banned blocks from the world.",
				"",
				"Recommended: [60000-120000] (1-2 minutes)",
				"Default: 60000",
			};
			performanceConfig.set("WorldCleanerThread", 60000, comment);
		}
		
		{
			final String[] comment = new String[] {
				"The time between checking for unloading chunks. If the amount of chunks loaded is to high, chunks will be unloaded shortly after the check.",
				"",
				"Recommended: [60000-120000] (1-2 minutes)",
				"Default: 90000",
			};
			performanceConfig.set("ChunkUnloader", 90000, comment);
		}
		
		{
			final String[] comment = new String[] {
				"The amount of time the AutoSave Thread sleeps for.",
				"This thread makes sure that all information is correctly saved to the database.",
				"In case of a crash, you will only lose any data that has been modified within the sleep time of "
			  + "this thread. (e.g. if you set this to 11000, you will only lose data changed within 11 seconds "
			  + "before the crash)",
				"",
				"Recommended: [10000-30000]",
				"Default: 11000"
			};
			performanceConfig.set("AutoSaveThreadSpeed", 11000, comment);
		}
		
		performanceConfig.saveConfig();
	}
	
	public static void genCreative(){
		limitedCreativeConfig = manager.getNewConfig("LimitedCreative.yml", header, 93);
		
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"################################# Limited Creative Configuration ##################################",
				"###################################################################################################",
				"Limited creative is a function of TekkitRestrict where you can disallow items for players in "
			  + "creative mode. This way, you can give creative mode to people without having to worry about them "
			  + "making tons of Collectors, Solar panels, Red Matter Blocks and so on. You only have to set the "
			  + "items you don't want people to use in creative mode here, and TekkitRestrict will do the rest.",
				"",
				"Limited creative will also prevent players in creative mode from dropping items on the ground.",
				"This makes sure they don't give items to players in survival mode.",
				"",
				"Disable (false) or enable (true) Limited creative.",
				"Default: false"
			};
			limitedCreativeConfig.set("UseLimitedCreative", false, comment);
		}
		
		{
			final String[] comment = new String[] {
				"If you set this option to true, it will prevent the use of ANY container while a player is in "
			  + "creative mode. This means that he cannot place his creative diamond blocks in a chest and then "
			  + "use them in survival. ",
				"If this is enabled, it prevents the use of ANY container while in creative mode. This is "
			  + "everything you can interact with, with the exception of your own inventory.",
				"Default: true"
			};
			limitedCreativeConfig.set("LimitedCreativeNoContainer", true, comment);
		}
		
		{
			final String[] comment = new String[] {
				"Here you can set the items you don't want creative players to use. You can enter mods, id's, item "
			  + "names, and more.",
				"Examples:",
				"- \"EE {&cYou are not allowed to use Equivalent Exchange when you are in creative mode!}\"",
				"- \"1 {&cStone is banned for creative players!}\"",
				"- \"35:15 {&cWe just hate black wool, so don't build with it!}\"",
				"- 50",
				"- \"NetherGoldOre {&6Nether ores are explosive, so you are not allowed to build with them!}\"",
				"",
				"For a full list of mods, usable item names and more examples, see Info.yml"
			};
			final List<String> content = new ArrayList<String>();
			content.add("RedPowerControl");
			content.add("RedPowerLogic");
			content.add("RedPowerMachine");
			content.add("WirelessRedstone");
			content.add("BuildCraft");
			content.add("AdditionalPipes");
			content.add("AdvancedMachines");
			content.add("IndustrialCraft");
			content.add("NuclearControl");
			content.add("CompactSolars");
			content.add("ChargingBench");
			content.add("PowerConverters");
			content.add("Mffs");
			content.add("RailCraft");
			content.add("TubeStuffs");
			content.add("IronChests");
			content.add("BalkonWeaponMod");
			content.add("EnderChest");
			content.add("ChunkLoaders");
			
			limitedCreativeConfig.set("LimitedCreative", content, comment);
		}
		
		limitedCreativeConfig.saveConfig();
	}

	public static void genLimiter(){
		limiterConfig = manager.getNewConfig("Limiter.yml", header, 93);
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"###################################### Limiter Configuration ######################################",
				"###################################################################################################",
				"Limits the number of blocks a player can place. (Global)",
				"Please note that these cannot be changed in-game.",
				"Item Limit (Please use ONE space between the Item and the limit to separate them)",
				"- \"153 1\"",
				"- \"100-200 1\"",
				"- \"52:55 1\"",
				"- \"227-228:10 3\"",
				"- \"ee 5 {&cYou can only have &a5 &cee blocks!}\"",
				"- \"Wool 10 {&cOnly &a10 &cWool per player, sorry :P}\""
			};
			limiterConfig.set("LimitBlocks", new ArrayList<String>(), comment);
		}
		
		limiterConfig.saveConfig();
	}
	
	public static void getLogging(){
		loggingConfig = manager.getNewConfig("Logging.yml", header, 93);
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"############################################# Logging #############################################",
				"###################################################################################################",
				"",
				"The format of the name of the logfile.",
				"Default: \"{TYPE}-{DAY}-{MONTH}-{YEAR}.log\""
			};
			loggingConfig.set("FilenameFormat", "{TYPE}-{DAY}-{MONTH}-{YEAR}.log", comment);
		}

		{
			final String[] comment = new String[] {
				"The format to log a string.",
				"Default: \"[{HOUR}:{MINUTE}:{SECOND}] {INFO}\""
			};
			loggingConfig.set("LogStringFormat", "[{HOUR}:{MINUTE}:{SECOND}] {INFO}", comment);
		}
		{
			final String[] comment = new String[] {
				"Should debug messages be logged?",
				"Default: false"
			};
			loggingConfig.set("LogDebug", false, comment);
		}
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"############################################ Split Logs ###########################################",
				"###################################################################################################",
				"",
				"Should tekkitrestrict split the logs into many parts?",
				"(e.g. Chat, Commands, Login/Logout, Warnings, etc.)",
				"Default: true"
			};
			loggingConfig.set("SplitLogs", true, comment);
		}
		{
			final String[] comment = new String[] {
				"The folder where TekkitRestrict should place the split logs.",
				"NOTE: The path starts at the server root.",
				"Example file: server_root/log/chat/chat-23-10-13.log",
				"Default: \"log\""
			};
			loggingConfig.set("SplitLogsLocation", "log", comment);
		}
		{
			final String[] comment = new String[] {
				"There are 9 different levels:",
				"Severe, Warning",
				"Errorlevels (= Severe and Warning)",
				"Info",
				"Fine, Finer, Finest",
				"Finelevels (= Fine, Finer and Finest)",
				"All (= All levels)"
			};
			loggingConfig.set("Splitters.Option1.Description", "For the login and logout", comment);
			loggingConfig.set("Splitters.Option1.File", "Login");
			loggingConfig.set("Splitters.Option1.Level", "Info");
			loggingConfig.set("Splitters.Option1.CaseSensitive", true);
			loggingConfig.set("Splitters.Option1.Method", "Contains");
			final List<String> content = new ArrayList<String>();
			content.add(" lost connection: disconnect.");
			content.add(" logged in with entity id ");
			content.add(" lost connection: user was kicked.");
			loggingConfig.set("Splitters.Option1.Messages", content);
		}
		{
			loggingConfig.set("Splitters.Chat.Description", "Set this to your own chatformat. <Rank> <Name>: <message> = <(.*)> <(.*)>: (.*)");
			loggingConfig.set("Splitters.Chat.File", "Chat");
			loggingConfig.set("Splitters.Chat.Level", "Info");
			loggingConfig.set("Splitters.Chat.CaseSensitive", true);
			loggingConfig.set("Splitters.Chat.Method", "Regex");
			loggingConfig.set("Splitters.Chat.Messages", filledList("\\\\[(.*)\\\\] (.*): (.*)"));
		}
		{
			loggingConfig.set("Splitters.Errors.Description", "Logs all errors to a separate file.");
			loggingConfig.set("Splitters.Errors.File", "Error");
			loggingConfig.set("Splitters.Errors.Level", "ErrorLevels");
			loggingConfig.set("Splitters.Errors.CaseSensitive", true);
			loggingConfig.set("Splitters.Errors.Method", "Regex");
			final List<String> content = new ArrayList<String>();
			content.add("(.*)");
			loggingConfig.set("Splitters.Errors.Messages", content);
			loggingConfig.set("Splitters.Errors.File", "Chat");
		}
		{
			final String[] comment = new String[] {
				"Should all commands be logged to a file?",
				"Default: Commands",
				"Possible: a filename or false"
			};
			loggingConfig.set("LogAllCommandsToFile", "Command", comment);
		}
		{
			final String[] comment = new String[] {
				"Should NEI Item giving be logged to a file?",
				"Default: SpawnItem",
				"Possible: a filename or false"
			};
			loggingConfig.set("LogNEIGiveToFile", "SpawnItem", comment);
		}
		{
			final String[] comment = new String[] {
				"EndsWith is special here, in that it does not look at the arguments, like the other methods do "
			  + "/fakecommand kick will not match \"kick\" with endswith",
			};
			loggingConfig.set("CommandSplitters.Private.File", "PrivateChat", comment);
			loggingConfig.set("CommandSplitters.Private.Method", "Regex");
			final List<String> content = new ArrayList<String>();
			content.add("e?m(sg)? .*");
			content.add("e?tell .*");
			content.add("e?r(eply)? .*");
			content.add("e?mail .*");
			content.add("e?whisper .*");
			loggingConfig.set("CommandSplitters.Private.Commands", content);
		}
		{
			loggingConfig.set("CommandSplitters.Punishments.Description", "Should include punishment commands from most plugins automatically");
			loggingConfig.set("CommandSplitters.Punishments.File", "Punishment");
			loggingConfig.set("CommandSplitters.Punishments.Method", "EndsWith");
			final List<String> content = new ArrayList<String>();
			content.add("kick");
			content.add("ban");
			content.add("jail");
			content.add("mute");
			content.add("pardon");
			content.add("banip");
			content.add("pardonip");
			loggingConfig.set("CommandSplitters.Punishments.Commands", content);
		}
		{
			loggingConfig.set("CommandSplitters.SpawnItems.File", "SpawnItem");
			loggingConfig.set("CommandSplitters.SpawnItems.Method", "Regex");
			final List<String> content = new ArrayList<String>();
			content.add("i .*");
			content.add("e?item .*");
			content.add("e?give .*");
			content.add("e?more .*");
			content.add("e?un(l(imited)?)? .*");
			loggingConfig.set("CommandSplitters.SpawnItems.Commands", content);
		}
		

		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"########################################### Filter Logs ###########################################",
				"###################################################################################################",
				"",
				"Should TekkitRestrict filter out certain things from the logs?",
				"Default: true"
			};
			loggingConfig.set("FilterLogs", true, comment);
		}
		{
			final String[] comment = new String[] {
				"Here you can define the filters.",
				"There are 5 different methods:",
				"Contains - If a logmessage contains ...",
				"StartsWith - If a logmessage starts with ...",
				"EndsWith - If a logmessage ends with ...",
				"Equals - If a logmessage is identical to ...",
				"RegEx - If a logmessage matches ... (please do not use (?i))",
				"",
				"There are multiple types",
				"Console - Filter messages only from the console, NOT the logs",
				"ServerLog - Filter messages only from the server logs",
				"ForgeServerLog - Filter messages only from the forge logs",
				"All - Filter messages everywhere",
				"Console_ServerLog - Filter messages from the console and server logs",
				"Forge_ServerLog - Filter messages from forge logs and the server logs",
				"",
				"Please make sure every groupname is unique"
			};
			loggingConfig.set("Filters.SpammyMessages1.Description", "Spammy messages like slotChanging and Sending Triang", comment);
			loggingConfig.set("Filters.SpammyMessages1.Method", "regex");
			loggingConfig.set("Filters.SpammyMessages1.CaseSensitive", true);
			loggingConfig.set("Filters.SpammyMessages1.Type", "console_serverlog");
			final List<String> content = new ArrayList<String>(2);
			content.add("Sending Triang: (-?)(\\\\d+), (-?)(\\\\d+)\\\\.(\\\\d+)");
			content.add("slotChanging\\\\((\\\\d+)\\\\)");
			loggingConfig.set("Filters.SpammyMessages1.Messages", content);
		}
		{
			loggingConfig.set("Filters.SpammyMessages2.Description", "Filters out Repair is active.. and Player found.. Disable this if you have eepatch");
			loggingConfig.set("Filters.SpammyMessages2.Method", "equals");
			loggingConfig.set("Filters.SpammyMessages2.CaseSensitive", true);
			loggingConfig.set("Filters.SpammyMessages2.Type", "console_serverlog");
			loggingConfig.set("Filters.SpammyMessages2.Messages", filledList("Repair is active..", "Player found.."));
		}
		{
			loggingConfig.set("Filters.MovedWrongly.Description", "Filters incorrect moving warnings from the console.");
			loggingConfig.set("Filters.MovedWrongly.Method", "regex");
			loggingConfig.set("Filters.MovedWrongly.CaseSensitive", true);
			loggingConfig.set("Filters.MovedWrongly.Type", "console");
			final List<String> content = new ArrayList<String>(2);
			content.add("Got position (-?)(\\\\d+)\\\\.(\\\\d+), (-?)(\\\\d+)\\\\.(\\\\d+), (-?)(\\\\d+)\\\\.(\\\\d+)");
			content.add("Expected (-?)(\\\\d+)\\\\.(\\\\d+), (-?)(\\\\d+)\\\\.(\\\\d+), (-?)(\\\\d+)\\\\.(\\\\d+)");
			loggingConfig.set("Filters.MovedWrongly.Messages", content);
		}
		{
			final String[] comment = new String[] {
				"   IPLostConnection:",
				"       Description: Filters out xxx.xxx.xxx.xxx:xxxx lost connection",
				"       Method: regex",
				"       CaseSensitive: true",
				"       Type: console_serverlog",
				"       Messages:",
				"       - \"/(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+)\\\\.(\\\\d+):(\\\\d+) lost connection\""
			};
			loggingConfig.set("Filters.Join1.Method", "startswith", comment);
			loggingConfig.set("Filters.Join1.CaseSensitive", true);
			loggingConfig.set("Filters.Join1.Type", "console_serverlog");
			loggingConfig.set("Filters.Join1.Messages", filledList("Sending serverside check to: "));
		}
		{
			loggingConfig.set("Filters.Join2.Description", "Filters out the listing of mods a player has when joining");
			loggingConfig.set("Filters.Join2.Method", "contains");
			loggingConfig.set("Filters.Join2.CaseSensitive", true);
			loggingConfig.set("Filters.Join2.Type", "console_serverlog");
			loggingConfig.set("Filters.Join2.Messages", filledList(" joined with: ["));
		}
		{
			loggingConfig.set("Filters.ConnectionErrors.Description", "Filters out Connection reset and End of stream messages.");
			loggingConfig.set("Filters.ConnectionErrors.Method", "equals");
			loggingConfig.set("Filters.ConnectionErrors.CaseSensitive", true);
			loggingConfig.set("Filters.ConnectionErrors.Type", "console_serverlog");
			loggingConfig.set("Filters.ConnectionErrors.Messages", filledList("Connection reset", "Reached end of stream"));
		}
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"######################################## Console Enhancer #########################################",
				"###################################################################################################"
			};
			loggingConfig.set("UseLogEnchancer", true, comment);
			loggingConfig.set("EnchanceEssentialsCmd", true);
			loggingConfig.set("ChangeGive", true);
			loggingConfig.set("ShortenErrors", true);
			loggingConfig.set("EnhanceEssentialsCmdDeny", true);
		}
		{
			final String[] comment = new String[] {
				"RemovePlayerTags: false",
				"",
				"Reformat:",
				"   Option1:",
				"       Message: \"\\\\[(.*)\\\\] \\\\[(.*)\\\\] (.*): (.*)\"",
				"       Replacement: \"\\\\[($2)\\\\] ($3): ($4)\""
			};
			loggingConfig.set("WIP", false, comment);
			loggingConfig.set("WIP", null);
		}
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"######################################### Log Hacks/Dupes #########################################",
				"###################################################################################################",
				"NOTE: These will be logged at plugins/tekkitrestrict/log/",
				"",
				"When a user is FlyHacking, should it be logged?",
				"Default: true"
			};
			loggingConfig.set("LogFlyHack", true, comment);
		}
		{
			final String[] comment = new String[] {
				"When a user is SpeedHacking, should it be logged?",
				"Default: true"
			};
			loggingConfig.set("LogMovementSpeedHack", true, comment);
		}
		{
			final String[] comment = new String[] {
				"When a user is ForcefieldHacking, should it be logged?",
				"Default: true"
			};
			loggingConfig.set("LogForcefieldHack", true, comment);
		}
		{
			final String[] comment = new String[] {
				"When a player (tries to) dupe, should it be logged?",
				"Default: true"
			};
			loggingConfig.set("LogDupes", true, comment);
		}
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"########################################## Log EE Items ###########################################",
				"###################################################################################################",
				"NOTE: These will be logged at plugins/tekkitrestrict/log/",
				"",
				"When a player uses an amulet, should it be logged?",
				"Default: true"
			};
			
			loggingConfig.set("LogAmulets", true, comment);
		}
		{
			final String[] comment = new String[] {
				"When a player uses a ring, should it be logged?",
				"Default: true"
			};
			loggingConfig.set("LogRings", true, comment);
		}
		{
			final String[] comment = new String[] {
				"When a player uses a Dark Matter Tool, should it be logged?",
				"Default: true"
			};
			loggingConfig.set("LogDMTools", true, comment);
		}
		{
			final String[] comment = new String[] {
				"When a player uses a Red Matter Tool, should it be logged?",
				"Default: true"
			};
			loggingConfig.set("LogRMTools", true, comment);
		}
		{
			final String[] comment = new String[] {
				"When a player uses certain EE items, should it be logged?",
				"Default: true"
			};
			loggingConfig.set("LogEEMisc", true, comment);
		}
		{
			final String[] comment = new String[] {
				"When a player uses a Destructive EE item, should it be logged?",
				"Default: true"
			};
			loggingConfig.set("LogEEDestructive", true, comment);
		}
		
		loggingConfig.saveConfig();
	}
	
	public static void genUnload(){
		unloadConfig = manager.getNewConfig("ChunkUnloader.yml", header, 93);
		{
			final String[] comment = new String[]{
				"###################################################################################################",
				"########################################## ChunkUnloader ##########################################",
				"###################################################################################################",
				"# WARNING: On LARGE servers, chunk unloaders are known to cause problems that make the server "
			  + "CRASH! This will happen with an error about tile entities. If you see this error, you may want to "
			  + "consider turning the chunk unloader OFF!",
				"",
				"# NOTE: If you are using MCPCSpout, you will not have this problem, and you can leave the Chunk "
			  + "Unloader OFF.",
				"",
				"# This will be very useful for preventing your server from getting over 30,000 chunks.",
				"",
				"# There is a bug with the Normal Tekkit server in which the server gains \"Trash\" in the memory "
			  + "from disposing of chunks. I recommend keeping your MaxChunks around 3000-4000 to prevent this from "
			  + "disabling your server over time.",
				"",
				"# If you lower it below 1000, you have a chance of lagging your server by player movements."
			};
			unloadConfig.set("UseChunkUnloader", true, comment);
		}
		{
			final String[] comment = new String[]{
				"# The maximal amount of chunks loaded at a time. If this amount is exceeded, chunks will be "
			  + "forcefully unloaded (even if they have chunk loaders) to clear RAM.",
				"# The more players you have on the server, the higher you should set this to.",
				"",
				"# Max number of chunks for the End.",
				"# Recommended: 100-400 (the End is usually not a often visited place)",
				"# Default: 200"
			};
			unloadConfig.set("MaxChunks TheEnd", 200, comment);
		}
		{
			final String[] comment = new String[]{
				"# Max number of chunks for the Nether.",
				"# Recommended: 300-600 (depends on how many players live in the Nether)",
				"# Default: 400"
			};
			unloadConfig.set("MaxChunks Nether", 400, comment);
		}
		{
			final String[] comment = new String[]{
				"# Max number of chunks for normal type worlds.",
				"# Recommended: (Server RAM in GB)*700 to (Server RAM in GB)*900",
				"# Default: 4000"
			};
			unloadConfig.set("MaxChunks Normal", 4000, comment);
		}
		{
			final String[] comment = new String[]{
				"# Max number of chunks loaded in total (all worlds)",
				"# If this number is exceeded, the UnloadOrder will come in effect.",
				"# Recommended: (Server RAM in GB)*700 to (Server RAM in GB)*900",
				"# Default: 4000"
			};
			unloadConfig.set("MaxChunks Total", 4000, comment);
		}
		{
			final String[] comment = new String[]{
				"# UnloadOrder",
				"# The order in which chunks from worlds will be unloaded when the total number of chunks is "
			  + "exceeded.",
				"# 0 - The End, Nether, Normal worlds    (default)",
				"# 1 - Nether, The End, Normal worlds",
				"",
				"# 2 - Normal worlds, The End, Nether    (not recommended)",
				"# 3 - The End, Normal worlds, Nether    (not recommended)",
				"",
				"# 4 - Nether, Normal worlds, The End    (not recommended)",
				"# 5 - Normal worlds, Nether, The End    (not recommended)",
				"",
				"# 2 and 3 are not recommended unless your main world is a Nether world.",
				"# 4 and 5 are not recommended unless your main world is an End world.",
				"",
				"# It is recommended to put the least used world first."
			};
			unloadConfig.set("UnloadOrder", 0, comment);
		}

		{
			final String[] comment = new String[]{
				"# The radius of blocks that should stay loaded around a player.",
				"# The ChunkUnloader will not unload chunks that are within this radius to a player.",
				"# Minecraft's default is 256 blocks (16 chunks).",
				"",
				"# Recommended: 128-256",
				"# Default: 256"
			};
			unloadConfig.set("MaxRadii", 256, comment);
		}
		
		unloadConfig.saveConfig();
	}
	
	public static void genModifications(){
		modModificationsConfig = manager.getNewConfig("ModModifications.yml", header, 93);
		{
			final String[] comment = new String[] {
				"###################################################################################################",
				"###################################### Limiter Configuration ######################################",
				"###################################################################################################",
				"Limits the number of blocks a player can place. (Global)",
				"Please note that these cannot be changed in-game.",
				"Item Limit (Please use ONE space between the Item and the limit to separate them)",
				"- \"153 1\"",
				"- \"100-200 1\"",
				"- \"52:55 1\"",
				"- \"227-228:10 3\"",
				"- \"ee 5 {&cYou can only have &a5 &cee blocks!}\"",
				"- \"Wool 10 {&cOnly &a10 &cWool per player, sorry :P}\""
			};
			modModificationsConfig.set("LimitBlocks", new ArrayList<String>(), comment);
		}
		
		modModificationsConfig.saveConfig();
	}
	
	private static List<String> filledList(String... strings){
		final List<String> tbr = new ArrayList<String>();
		for (final String s : strings) tbr.add(s);
		return tbr;
	}
}
