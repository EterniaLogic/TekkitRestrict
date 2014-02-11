package nl.taico.tekkitrestrict2;

import java.util.ArrayList;

public class SettingsStorage {
	public static ConfigManager manager = new ConfigManager();
	public static TRConfig bannedConfig, groupPermsConfig, safeZoneConfig, hackDupeConfig, databaseConfig, performanceConfig, limitedCreativeConfig;
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
			final String[] comment = {
				"# Note: This will only apply to Native, WorldGuard and GriefPrevention SafeZones, and",
				"#       NOT to Towny, Factions or PreciousStones SafeZones."	
			};
			safeZoneConfig.set("InSafeZones.Null", true, comment);
		}
		{
			final String[] comment = {
				"#   If you turn DisableEntities on, then",
				"#   - No mobs will spawn in SafeZones.",
				"#   - If a mob enters a SafeZone, it is removed.",
				"#   Default: true"
			};
			safeZoneConfig.set("InSafeZones.DisableEntities", true, comment);
		}
		{
			final String[] comment = {
				"#   This is a feature that allows tekkitrestrict to run the entities disabler",
				"#   thread with less lag and problems. Entities can get removed at range blocks",
				"#   from the corner of the safezone.",
				"#",
				"#   Increase this value if you have problems with the entity remover or if you want",
				"#   to increase performance.",
				"#   Default: 10"
			};
			safeZoneConfig.set("InSafeZones.DisableEntitiesRange", 10, comment);
		}
		{
			final String[] comment = {
				"#   WARNING: Case Sensitive!",
				"#   Tries to exclude org.bukkit.entity.[name] entities from SafeZone entity removal.",
				"#   Examples:",
				"#   \"Arrow\", \"Animals\", \"EnderDragon\", \"EnderPearl\", \"Fish\", \"IronGolem\", \"Pig\",",
				"#   \"Projectile\", \"ThrownPotion\", \"TNTPrimed\", \"Snowball\""
			};
			safeZoneConfig.set("InSafeZones.ExemptEntityTypes", new ArrayList<String>(), comment);
		}
		{
			final String[] comment = {
				"#   If you turn DechargeEE on, then",
				"#   - All EE items specified in the ModModifications config will be decharged in SafeZones",
				"#   Default: true"
			};
			safeZoneConfig.set("InSafeZones.DechargeEE", true, comment);
		}
		{
			final String[] comment = {
				"#   If you turn DisableRingOfArcana on, then",
				"#   - When someone is in a SafeZone with a ring of arcana, it will always be set to \"Earth\" mode",
				"#   and it will be turned off.",
				"#   Default: true"
			};
			safeZoneConfig.set("InSafeZones.DisableRingOfArcana", true, comment);
		}
		{
			final String[] comment = {
				"The plugins TekkitRestrict should use to make safezones.",
				"",
				"It is recommended to use tekkitrestrict native safezones, to minimize the strain safezones put on the server.",
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
		
		safeZoneConfig.set("InSafeZones.Null", null);
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
				"This thread takes care of the ChunkUnloader and the WorldScrubber.",
				"",
				"Recommended: [30000-90000]",
				"Default: 60000",
				"# WorldCleanerThread: 60000"
			};
			performanceConfig.set("WorldCleanerThread", 60000, comment);
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
				"#################################### Performance Configuration ####################################",
				"###################################################################################################",
				"Should TekkitRestrict favor server performance over memory usage?",
				"Default: false"
			};
			limitedCreativeConfig.set("FavorPerformanceOverMemory", false, comment);
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
			limitedCreativeConfig.set("GemArmorDThread", 120, comment);
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
			limitedCreativeConfig.set("SSEntityRemoverThread", 500, comment);
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
			limitedCreativeConfig.set("InventoryThread", 400, comment);
		}
		
		{
			final String[] comment = new String[] {
				"The amount of time the WorldCleanerThread sleeps for.",
				"This thread takes care of the ChunkUnloader and the WorldScrubber.",
				"",
				"Recommended: [30000-90000]",
				"Default: 60000",
				"# WorldCleanerThread: 60000"
			};
			limitedCreativeConfig.set("WorldCleanerThread", 60000, comment);
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
			limitedCreativeConfig.set("AutoSaveThreadSpeed", 11000, comment);
		}
		
		limitedCreativeConfig.saveConfig();
	}
}
