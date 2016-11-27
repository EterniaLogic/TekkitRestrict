package ee.events;

public class EEEnums {
	/**
	 * Possible Actions:
	 * <li><b>PASSIVE</b> - Passive Effect</li>
	 * <li><b>ACTIVE</b> - Effect when this item is Activated</li>
	 * <li><b>HELD</b> - Effect when this item is held (in hand)</li>
	 * <li><b>RELEASE</b> - Effect when the Release Key (default r) is pressed</li>
	 * <li><b>ALTERNATE</b> - Effect when the Alternate Key (default c) is pressed</li>
	 * <li><b>LEFTCLICK</b> - Effect when the player Left Clicks with this item</li>
	 * <li><b>RIGHTCLICK</b> - Effect when the player Right Clicks with this item</li>
	 * <li><b>CHARGE</b> - Effect when the Charge Key (default v) is pressed</li>
	 * <li><i><b>UNCHARGE</b> - (Deprecated) Effect when the Charge Key + shift (default v + shift) is pressed</i></li>
	 * <li><b>TOGGLE</b> - Effect when the Toggle Key (default g) is pressed</li>
	 * <li><b>TOGGLE2</b> - Second effect when the Toggle Key (default g) is pressed</li>
	 * <li><b>BREAKBLOCK</b> - Effect when a block is broken with this item</li>
	 */
	public enum EEAction {
		/** Passive Effect */
		PASSIVE,
		/** Effect when this item is Activated */
		ACTIVE,
		/** Effect when this item is held (in hand) */
		HELD,
		/** Effect when the Release Key (default r) is pressed */
		RELEASE,
		/** Effect when the Alternate Key (default c) is pressed */
		ALTERNATE,
		/** Effect when the player Left Clicks with this item */
		LEFTCLICK,
		/** Effect when the player Right Clicks with this item */
		RIGHTCLICK,
		/** Effect when the Charge Key (default v) is pressed */
		CHARGE,
		/** Effect when the Charge Key + shift (default v + shift) is pressed */
		@Deprecated
		UNCHARGE,
		/** Effect when the Toggle Key (default g) is pressed */
		TOGGLE,
		/** Second effect when the Toggle Key (default g) is pressed */
		TOGGLE2,
		/** Effect when a block is broken with this item */
		BREAKBLOCK
	}
	
	/**
	 * Possible Actions:
	 * <li><b>BreakRadius</b> - Break blocks in a radius.</li>
	 * <li><b>TillRadius</b> - Till blocks in a radius.</li>
	 * <li><b>AttackRadius</b> - Attack players and mobs in a radius. </li>
	 * <li><b>TallBreak</b> - Break 3 blocks using the tall mode available on some tools.</li>
	 * <li><b>WideBreak</b> - Break 3 blocks using the wide mode available on some tools. </li>
	 * <li><b>LongBreak</b> - Break 3 blocks using the long mode available on some tools.</li>
	 * <li><b>MegaBreak</b> - Break a big square of blocks using the mega break mode on hammers. </li>
	 * <li><b>DiviningRod</b> - The ability to use the divining rod </li>
	 * <li><b>Shear</b> - Shear 1 or multiple mobs with an EE tool. </li>
	 * <li><b>CreateWater</b> - Create 1 or more water blocks (used by evertide amulet)</li>
	 * <li><b>Unknown</b> - The action has not been specified or does not fall under any of the possible categories.</li>
	 */
	public enum EEAction2 {
		/** Break blocks in a radius. */
		BreakRadius(0),
		/** Till blocks in a radius. */
		TillRadius(1),
		/** Attack players and mobs in a radius. */
		AttackRadius(2), 
		/** Break 3 blocks using the tall mode available on some tools. */
		TallBreak(3),
		/** Break 3 blocks using the wide mode available on some tools. */
		WideBreak(4), 
		/** Break 3 blocks using the long mode available on some tools. */
		LongBreak(5),
		/** Break a big square of blocks using the mega break mode on hammers. */
		MegaBreak(6), 
		///** Toggle the tool mode between tall, wide and long. */
		//UpdateToolMode,
		///** Toggle the hammer mode between normal and mega. */
		//UpdateHammerMode,
		///** Toggle the sword mode between only hostile and all mobs. */
		//UpdateSwordMode, 
		/** The ability to use the divining rod */
		DiviningRod(7), 
		/** Shear 1 or multiple mobs with an EE tool. */
		Shear(8), 
		/** Create 1 or more water blocks (used by evertide amulet) */
		CreateWater(9),
		/** The action has not been specified or does not fall under any of the possible categories. */
		Unknown(10);
		private int nr;
		EEAction2(int nr){
			this.nr = nr;
		}
		
		public int getNr() {
			return nr;
		}
	}
	
	/**
	 * Possible Actions:
	 * <li><b>NegateFallDamage</b> - Negate Falldamage (SWRG and Ring of Arcana) (0)</li>
	 * <li><b>Fly</b> - Fly (SWRG and Ring of Arcana) (1)</li>
	 * <li><b>Interdict</b> - Interdict (SWRG and Ring of Arcana) (2)</li>
	 * <br><br>
	 * <li><b>Freeze</b> - Freeze (Zero Ring and Ring of Arcana) (3)</li>
	 * <li><b>Burn</b> - Burn (Ring of Ignition and Ring of Arcana) (4)</li>
	 * <li><b>Extinguish</b> - Extinguish (Ring of Ignition) (5)</li>
	 * <br><br>
	 * <li><b>PlantRadius</b> - Plant plants in a radius (Harvest Goddess Band) (6)</li>
	 * <li><b>Fertilize</b> - Fertilize (Harvest Goddess Band and Ring of Arcana) (7)</li>
	 * <li><b>Harvest</b> - Harvest (Harvest Goddess Band and Ring of Arcana) (8)</li>
	 * <br><br>
	 * <li><b>AttractItems</b> - Attract Items (BHB and Void Ring) (9)</li>
	 * <li><b>DeleteLiquid</b> - Vaporize liquid (BHB and Void Ring) (10)</li>
	 * <br><br>
	 * <li><b>Teleport</b> - Teleport (Void Ring) (11)</li>
	 * <li><b>Condense</b> - Condense (Gem of Eternal Density and Void Ring) (12)</li>
	 * <br><br>
	 * <li><b>Gust</b> - Gust (SWWRG and Ring of Arcana) (13)</li>
	 * <li><b>StrikeLightning</b> - Strike Lightning (Ring of Arcana) (14)</li>
	 * <br><br>
	 * <li><b>ThrowSnowball</b> - Throw a snowball (Zero Ring and Ring of Arcana) (15)</li>
	 * <li><b>ThrowPyrokinesis</b> - Throw a pyrokinesis entity (Ring of Ignition and Ring of Arcana) (16)</li>
	 * <li><b>ThrowWater</b> - Throw a water essence (Evertide Amulet) (17)</li>
	 * <li><b>ThrowLava</b> - Throw a lava essence (Vulcanite Amulet) (18)</li>
	 * <br><br>
	 * <li><b>Activate</b> - Activate (19)</li>
	 * <li><b>ActivateInterdict</b> - Activate Interdict (SWRG and Ring of Arcana) (20)</li>
	 * <li><i><b>Deactivate</b> - Deprecated, When a Ring is decativated (21)</i></li>
	 * <br><br>
	 * <li><b>Unknown</b> - Unknown (22)</li>
	 * <li><b>ShootArrows</b> - Shoot arrows (Archangels Smite) (23)</li>
	 */
	public enum EERingAction {
		/** Negate Falldamage (SWRG and Ring of Arcana) (0)*/
		NegateFallDamage("negatefalldamage",0),
		/** Fly (SWRG and Ring of Arcana) (1)*/
		Fly("fly with",1),
		/** Interdict (SWRG and Ring of Arcana) (2)*/
		Interdict("interdict mobs with",2),
		
		/** Freeze (Zero Ring and Ring of Arcana) (3)*/
		Freeze("freeze an area",3),
		/** Burn (Ring of Ignition and Ring of Arcana) (4)*/
		Burn("burn an area with",4),
		/** Extinguish (Ring of Ignition) (5)*/
		Extinguish("extinguish fires around you with",5),
		
		/** Plant plants in a radius (Harvest Goddess Band) (6)*/
		PlantRadius("automatically plant plants in a radius",6),
		/** Fertilize (Harvest Goddess Band and Ring of Arcana) (7)*/
		Fertilize("make plants grow faster with",7), 
		/** Harvest (Harvest Goddess Band and Ring of Arcana) (8)*/
		Harvest("harvest plants in a radius with",8),
		
		/** Attract Items (BHB and Void Ring) (9)*/
		AttractItems("attract items with",9),
		/** Vaporize liquid (BHB and Void Ring) (10)*/
		DeleteLiquid("delete liquids with",10),
		
		/** Teleport (Void Ring) (11)*/
		Teleport("teleport with",11),
		/** Condense (Gem of Eternal Density and Void Ring) (12)*/
		Condense("condense items with",12),
		
		/** Gust (SWWRG and Ring of Arcana) (13)*/
		Gust("create Gusts with",13),
		/** Strike Lightning (Ring of Arcana) (14)*/
		StrikeLightning("cause lightning to strike with",14),
		
		/** Throw a snowball (Zero Ring and Ring of Arcana) (15)*/
		ThrowSnowball("throw snowballs with",15),
		/** Throw a pyrokinesis entity (Ring of Ignition and Ring of Arcana) (16)*/
		ThrowPyrokinesis("throw fireballs with",16),
		/** Throw a water essence (Evertide Amulet) (17)*/
		ThrowWater("throw water essences with",17),
		/** Throw a lava essence (Vulcanite Amulet) (18)*/
		ThrowLava("throw lava essences with",18),
		
		/** Activate (19)*/
		Activate("Activate",19),
		/** Activate Interdict (SWRG and Ring of Arcana) (20)*/
		ActivateInterdict("Activate Interdiction",20),
		/** Deactivate (21) 
		 * @deprecated */
		Deactivate("deactivate",21),

		/** Unknown (22) */
		Unknown("unknown",22),
		/** Shoot arrows (Archangels Smite) (23) */
		ShootArrows("shoot arrows with",23);
		
		private int nr;
		private String name;
		EERingAction(String name, int nr){
			this.name = name;
			this.nr = nr;
		}
		
		public int getNr(){
			return nr;
		}
		
		public String getName(){
			return name;
		}
	}
	
	/**
	 * Possible Actions:
	 * <li><b>CreateWater</b> - Create 1 or more water blocks (used by evertide amulet)</li>
	 * <li><b>CreateWaterBall</b> - Create a water essence which creates water where it hits, and turns lava to obsidian</li>
	 * <li><b>StopDrowning</b> - Stop the player from drowning (Passive of Evertide Amulet) (Drowntime of 15s)</li>
	 * <li><b>CreateLavaBall</b> - Create a lava essence which creates lava where it hits.</li>
	 * <li><b>CreateLava</b> - Create 1 or more lava blocks (used by volcanite amulet)</li>
	 * <li><b>Vaporize</b> - Vaporize water in a range (used by volcanite amulet)</li>
	 * <li><b>FireImmune</b> - Stop the player from taking fire damage (Passive of Volcanite Amulet) (30s)</li>
	 */
	public enum EEAmuletAction {
		/** Create 1 or more water blocks (used by evertide amulet) */
		CreateWater(0),
		/** Create a water essence which creates water where it hits, and turns lava to obsidian */
		CreateWaterBall(1),
		/** Stop the player from drowning (Passive of Evertide Amulet) (Drowntime of 15s)*/
		StopDrowning(2),
		/** Create a lava essence which creates lava where it hits. */
		CreateLavaBall(3),
		/** Create 1 or more lava blocks (used by volcanite amulet) */
		CreateLava(4),
		/** Vaporize water in a range (used by volcanite amulet) */
		Vaporize(5),
		/** Stop the player from taking fire damage (Passive of Volcanite Amulet) (30s) */
		FireImmune(6);
		
		private int nr;
		EEAmuletAction(int nr){
			this.nr = nr;
		}
		
		public int getNr(){
			return nr;
		}
	}
	
	/**
	 * Possible Actions:
	 * <li><b>Transmute</b> - Transmute an item into a different item.</li>
	 * <li><b>ChangeMob</b> - Change a mob into a different mob of the same type.</li>
	 * <li><b>PortableCrafting</b> - Open a portable crafting window.</li>
	 * <li><b>PortableTable</b> - Open a portable Transmution Table.</li>
	 */
	public enum EETransmuteAction {
		/**
		 * Transmute an item into a different item.
		 */
		Transmute(0),
		/**
		 * Change a mob into a different mob of the same type.
		 */
		ChangeMob(1),
		/**
		 * Open a portable crafting window.
		 */
		PortableCrafting(2),
		/**
		 * Open a portable Transmution Table.
		 */
		PortableTable(3);
		
		private int nr;
		EETransmuteAction(int nr){
			this.nr = nr;
		}
		
		public int getNr(){
			return nr;
		}
	}
	
	/**
	 * Possible Actions:
	 * <li><b>Interdict</b> - Push mobs away (EE Torch)</li>
	 * <li><b>Storm</b> - Keep the weather Stormy (Evertide Amulet)</li>
	 * <li><b>ShootArrow</b> - Shoot arrows at players (Archangels Smite)</li>
	 * <li><b>Harvest</b> - Harvest plants (Harvest Godess band)</li>
	 * <li><b>None</b> - Zero Ring (Does nothing)</li>
	 * <li><b>Ignition</b> - Ignite mobs that come close (Ring of Ignition)</li>
	 * <li><b>Repair</b> - Repair tools of players nearby (Repair Talisman)</li>
	 * <li><b>Heal</b> - Heal nearby mobs and players (Soul stone)</li>
	 * <li><b>StrikeLightning</b> - Strike nearby monsters with Lightning (SWRG)</li>
	 * <li><b>StopStorm</b> - Stop all storms (Volcanite Amulet)</li>
	 * <li><b>Time</b> - Slow Entities and speed up machines. (Watch Of Flowing Time)</li>
	 * <li><b>Attract</b> - Attract Items (Black Hole Band)</li>
	 * <li><b>Activate</b> - Activate the pedestal</li>
	 */
	public enum EEPedestalAction {
		/** Push mobs away (EE Torch) */
		Interdict(0),
		/** Keep the weather Stormy (Evertide Amulet) */
		Storm(1),
		/** Shoot arrows at players (Archangels Smite) */
		ShootArrow(2),
		/** Harvest plants (Harvest Godess band) */
		Harvest(3),
		/** Zero Ring (Does nothing) */
		None(4),
		/** Ignite mobs that come close (Ring of Ignition) */
		Ignition(5),
		/** Repair tools of players nearby (Repair Talisman) */
		Repair(6),
		/** Heal nearby mobs and players (Soul stone) */
		Heal(7),
		/** Strike nearby monsters with Lightning (SWRG) */
		StrikeLightning(8),
		/** Stop all storms (Volcanite Amulet) */
		StopStorm(9),
		/** Slow Entities and speed up machines. (Watch Of Flowing Time) */
		Time(10),
		/** Attract Items (Black Hole Band) */
		Attract(11),
		/** Activate the pedestal */
		Activate(12);
		
		private int nr;
		EEPedestalAction(int nr){
			this.nr = nr;
		}
		
		public int getNr(){
			return nr;
		}
	}
	
	/**
	 * Possible Types:
	 * <li><b>RMFurnace</b> - 1 Ore is turned into 2 ingots</li>
	 */
	public enum DuplicateType {
		/** 1 Ore is turned into 2 ingots */
		RMFurnace(0);
		
		private int nr;
		DuplicateType(int nr){
			this.nr = nr;
		}
		public int getNr(){
			return nr;
		}
	}

	/**
	 * Possible Actions:
	 * <li><b>OffensiveActivate</b> - Activate Offensive Powers</li>
	 * <li><b>OffensiveExplode</b> - Player Explodes with his Offensive Powers</li>
	 * <li><b>OffensiveStrike</b> - Player Strikes with his Offensive Powers</li>
	 * <li><b>MovementActivate</b> - Activate Movement Powers</li>
	 */
	public enum EEArmorAction {
		/** When a player activates offensive powers. */
		OffensiveActivate(true, 0),
		/** Player Explodes with his Offensive Powers */
		OffensiveExplode(true, 1),
		/** Player Strikes with his Offensive Powers */
		OffensiveStrike(true, 2),
		/** Activate Movement Powers */
		MovementActivate(false, 3);
		private boolean offensive = false;
		
		private int nr;
		EEArmorAction(boolean offensive, int nr){
			this.offensive = offensive;
			this.nr = nr;
		}
		
		/**
		 * @return If this action is an offensive one.
		 */
		public boolean isOffensive(){
			return offensive;
		}
		
		public int getNr(){
			return nr;
		}
	}
	
	/**
	 * Possible Actions:
	 * <li><b>TimeForward</b> - The time is going forwards faster</li>
	 * <li><b>TimeBackward</b> - The time is going backwards</li>
	 */
	public enum EEWatchAction {
		/** The time is going forwards faster */
		TimeForward,
		/** The time is going backwards */
		TimeBackward
	}
}
