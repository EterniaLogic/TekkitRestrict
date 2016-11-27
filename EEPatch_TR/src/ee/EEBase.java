package ee;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.BaseMod;
import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityGhast;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityIronGolem;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMagmaCube;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntitySlime;
import net.minecraft.server.EntitySnowman;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.EntityWeatherLighting;
import net.minecraft.server.EnumMovingObjectType;
import net.minecraft.server.IInventory;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MovingObjectPosition;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;

import ee.events.EEChargeEvent;
import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEArmorAction;
import ee.events.EEEnums.EEWatchAction;
import ee.events.EEEventManager;
import ee.events.armor.EEArmorEvent;
import ee.events.other.EEWOFTEvent;
import forge.ICraftingHandler;
import forge.MinecraftForge;

public class EEBase {

	public static HashMap<EntityHuman, Boolean> playerSwordMode = new HashMap<EntityHuman, Boolean>();
	public static HashMap<EntityHuman, Integer> playerWatchCycle = new HashMap<EntityHuman, Integer>();
	public static HashMap<EntityHuman, Integer> playerBuildMode = new HashMap<EntityHuman, Integer>();
	public static HashMap<EntityHuman, Boolean> playerInWater = new HashMap<EntityHuman, Boolean>();
	public static HashMap<EntityHuman, Boolean> playerInLava = new HashMap<EntityHuman, Boolean>();
	public static HashMap<EntityHuman, Boolean> playerHammerMode = new HashMap<EntityHuman, Boolean>();
	public static HashMap<EntityHuman, Boolean> playerArmorOffensiveToggle = new HashMap<EntityHuman, Boolean>();
	public static HashMap<EntityHuman, Boolean> playerArmorMovementToggle = new HashMap<EntityHuman, Boolean>();
	public static HashMap<EntityHuman, Integer> playerToggleCooldown = new HashMap<EntityHuman, Integer>();
	public static HashMap<EntityHuman, Integer> playerToolMode = new HashMap<EntityHuman, Integer>();
	public static HashMap<EntityHuman, Integer> playerWatchMagnitude = new HashMap<EntityHuman, Integer>();
	public static HashMap<EntityHuman, Boolean> playerLeftClick = new HashMap<EntityHuman, Boolean>();
	public static HashMap<EntityHuman, Boolean> playerTransGridOpen = new HashMap<EntityHuman, Boolean>();
	public static HashMap<EntityHuman, HashMap<Item, Boolean>> playerItemCharging = new HashMap<EntityHuman, HashMap<Item, Boolean>>();
	public static HashMap<EntityHuman, HashMap<Item, Integer>> playerEffectDurations = new HashMap<EntityHuman, HashMap<Item, Integer>>();
	public static HashMap<Integer, Integer[]> playerKnowledge = new HashMap<Integer, Integer[]>();
	@SuppressWarnings("unused")
	private static BaseMod instance;
	private static EEBase eeBaseInstance;
	public static boolean initialized = false;
	public static EEProps props;
	public static int playerWoftFactor = 1;
	@SuppressWarnings("unused")
	private static boolean leftClickWasDown;
	@SuppressWarnings("unused")
	private static boolean extraKeyWasDown;
	@SuppressWarnings("unused")
	private static boolean releaseKeyWasDown;
	@SuppressWarnings("unused")
	private static boolean chargeKeyWasDown;
	@SuppressWarnings("unused")
	private static boolean toggleKeyWasDown;
	public static boolean externalModsInitialized;
	public static int alchChestFront = 0;
	public static int alchChestSide = 1;
	public static int alchChestTop = 2;
	public static int condenserFront = 3;
	public static int condenserSide = 4;
	public static int condenserTop = 5;
	public static int relayFront = 6;
	public static int relaySide = 7;
	public static int relayTop = 8;
	public static int collectorFront = 9;
	public static int collectorSide = 10;
	public static int collectorTop = 11;
	public static int dmFurnaceFront = 12;
	public static int dmBlockSide = 13;
	public static int rmFurnaceFront = 14;
	public static int rmBlockSide = 15;
	public static int iTorchSide = 16;
	public static int novaCatalystSide = 17;
	public static int novaCataclysmSide = 18;
	public static int novaCatalystTop = 19;
	public static int novaCatalystBottom = 20;
	public static int collector2Top = 21;
	public static int collector3Top = 22;
	public static int relay2Top = 23;
	public static int relay3Top = 24;
	public static int transTabletSide = 25;
	public static int transTabletBottom = 26;
	public static int transTabletTop = 27;
	public static int portalDeviceSide = 28;
	public static int portalDeviceBottom = 29;
	public static int portalDeviceTop = 30;
	private static HashMap<Integer, int[]> pedestalCoords = new HashMap<Integer, int[]>();
	private static int machineFactor;

	public static void init(BaseMod var0) {
		if (!initialized) {
			initialized = true;
			instance = var0;
			props = new EEProps(new File("mod_EE.props").getPath());
			props = EEMaps.InitProps(props);
			props = EEPatch.InitProps(props);
			props.func_26596_save();
			machineFactor = props.getInt("machineFactor");
			setupCraftHook();
			
		}
	}

	public int AddFuel(int var1, int var2) {
		if (var1 == EEItem.alchemicalCoal.id) {
			if (var2 == 0) return 6400;
		} else if (var1 == EEItem.mobiusFuel.id) {
			return 25600;
		}

		return 0;
	}

	public static boolean isCurrentItem(Item var0, EntityHuman var1) {
		ItemStack temp = var1.inventory.getItemInHand();
		if (temp != null) return temp.getItem() == var0;// FIXME is this correct now?
		return var0 == null;
	}

	//private static boolean t = false;
	public static boolean isOnQuickBar(Item var0, EntityHuman var1) {
		//if (!t){
		//	Permission l = new Permission("eepatch.delay");
		//	t = true;
		//}
		for (int var2 = 0; var2 < 9; var2++) {
			ItemStack current = var1.inventory.getItem(var2);
			if ((current == null) || (current.getItem() != var0)) continue;
			return true;
		}

		return false;
	}

	/** @return the items on the hotbar. */
	public static ItemStack[] quickBar(EntityHuman var0) {
		ItemStack[] var1 = new ItemStack[9];

		for (int var2 = 0; var2 < 9; var2++) {
			var1[var2] = var0.inventory.items[var2];
		}

		return var1;
	}

	public static boolean EntityHasItemStack(ItemStack var0, IInventory var1) {
		boolean var2 = var0.getData() == -1;
		ItemStack[] var3 = new ItemStack[40];

		for (int var4 = 0; var4 < var1.getSize(); var4++) {
			ItemStack current = var1.getItem(var4);
			if ((current != null) && ((current.doMaterialsMatch(var0)) || ((current.getItem() == var0.getItem()) && (var2)))) {
				if (current.count >= var0.count) return true;

				var3[var4] = current;
			}
		}

		int var4 = 0;

		for (int var5 = 0; var5 < var1.getSize(); var5++) {
			ItemStack current = var3[var5];
			if ((current != null) && ((current.doMaterialsMatch(var0)) || ((current.getItem() == var0.getItem()) && (var2)))) {
				var4 += current.count;

				if (var4 >= var0.count) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean HasItemStack(ItemStack var0, EntityHuman var1) {
		boolean var2 = var0.getData() == -1;
		ItemStack[] var3 = new ItemStack[40];
		PlayerInventory var4 = var1.inventory;

		for (int var5 = 0; var5 < var4.items.length + var4.armor.length; var5++) {
			ItemStack current = var4.getItem(var5);
			if ((current != null) && ((current.doMaterialsMatch(var0)) || ((current.getItem() == var0.getItem()) && (var2)))) {
				if (current.count >= var0.count) return true;

				var3[var5] = current;
			}
		}

		int var5 = 0;

		for (int var6 = 0; var6 < var4.items.length + var4.armor.length; var6++) {
			ItemStack current = var3[var6];
			if ((current != null) && ((current.doMaterialsMatch(var0)) || ((current.getItem() == var0.getItem()) && (var2)))) {
				var5 += current.count;

				if (var5 >= var0.count) return true;
			}
		}

		return false;
	}

	public static int getKleinEnergyForDisplay(ItemStack var0) {
		return (var0.getItem() instanceof ItemKleinStar) ? EEPBase.getKleinPoints(var0) : 0;
	}

	public static int getDisplayEnergy(ItemStack var0) {
		if (var0 == null) return 0;
		if (var0.getItem() instanceof ItemTransTablet) {
			return EEPBase.getInteger(var0, "displayEnergy");
		}

		return 0;
	}

	public static void setDisplayEnergy(ItemStack var0, int var1) {
		if (var0 == null) return;

		if (var0.getItem() instanceof ItemTransTablet) {
			EEPBase.setInteger(var0, "displayEnergy", var1);
		}
	}

	public static int getLatentEnergy(ItemStack var0) {
		if (var0 == null) return 0;

		if (var0.getItem() instanceof ItemTransTablet) {
			return EEPBase.getInteger(var0, "latentEnergy");
		}

		return 0;
	}

	public static void setLatentEnergy(ItemStack var0, int var1) {
		if (var0 != null) {
			if (var0.getItem() instanceof ItemTransTablet) {
				EEPBase.setInteger(var0, "latentEnergy", var1);
			}
		}
	}

	public static boolean canIncreaseKleinStarPoints(ItemStack var0, World var1) {
		if (EEProxy.isClient(var1) || var0 == null || !isKleinStar(var0)) return false;
		return var0.getData() - 1 != 0;
	}

	@Deprecated
	public static boolean isKleinStar(int itemId) {
		return (itemId == EEItem.kleinStar1.id) || (itemId == EEItem.kleinStar2.id) || (itemId == EEItem.kleinStar3.id) || (itemId == EEItem.kleinStar4.id)
				|| (itemId == EEItem.kleinStar5.id) || (itemId == EEItem.kleinStar6.id);
	}
	
	public static boolean isKleinStar(Item item){
		return item instanceof ItemKleinStar;
	}
	
	public static boolean isKleinStar(ItemStack item){
		return item.getItem() instanceof ItemKleinStar;
	}

	/**
	 * @param itemId
	 * @return The level of the given kleinstar item id.
	 * @deprecated Use item.getLevel() or ItemKleinStar.getLevel(var1);
	 */
	public static int getKleinLevel(int itemId) {
		return itemId == EEItem.kleinStar6.id ? 6 : itemId == EEItem.kleinStar5.id ? 5 : itemId == EEItem.kleinStar4.id ? 4
				: itemId == EEItem.kleinStar3.id ? 3 : itemId == EEItem.kleinStar2.id ? 2 : itemId == EEItem.kleinStar1.id ? 1 : 0;
	}

	public static boolean addKleinStarPoints(ItemStack var0, int var1, World var2) {
		if (EEProxy.isClient(var2) || var0 == null || !isKleinStar(var0)) return false;

		ItemKleinStar var3 = (ItemKleinStar) var0.getItem();
		
		int points;
		if ((points = EEPBase.getKleinPoints(var0)) <= var3.getMax() - var1) {
			EEPBase.setKleinPoints(var0, points + var1);
			var3.onUpdate(var0);
			return true;
		}

		return false;
	}

	public static boolean addKleinStarPoints(ItemStack var0, int var1) {
		if (var0 == null || !isKleinStar(var0)) return false;

		ItemKleinStar var2 = (ItemKleinStar) var0.getItem();

		if (EEPBase.getKleinPoints(var0) <= var2.getMax() - var1) {
			EEPBase.setKleinPoints(var0, EEPBase.getKleinPoints(var0) + var1);
			var2.onUpdate(var0);
			return true;
		}

		return false;
	}

	public static boolean takeKleinStarPoints(ItemStack var0, int var1, World var2) {
		if (EEProxy.isClient(var2) || var0 == null || !isKleinStar(var0)) return false;
		
		int points = EEPBase.getKleinPoints(var0);
		if (points >= var1) {
			EEPBase.setKleinPoints(var0, points - var1);
			EEPBase.onUpdate_KleinStar(var0);
			return true;
		}

		return false;
	}

	public static boolean consumeKleinStarPoint(EntityHuman var0, int var1) {
		if (var0 == null || EEProxy.isClient(var0.world)) return false;

		PlayerInventory var2 = var0.inventory;
		/*
		for (int var3 = 0; var3 < var2.items.length; var3++) {
			ItemStack var4 = var2.getItem(var3);
			if (var4 != null) {
				if (isKleinStar(var4.id) && takeKleinStarPoints(var4, var1, var0.world))
					return true;
			}
		}*/
		for (ItemStack current : var2.items) {
			if (current == null) continue;
			if ((isKleinStar(current)) && (takeKleinStarPoints(current, var1, var0.world))) return true;
		}

		return false;
	}

	public static boolean Consume(ItemStack item, EntityHuman human, boolean notifyUser) {
		if (human == null || EEProxy.isClient(human.world)) return false;
		
		int amount = item.count;
		int var4 = 0;
		boolean dataMinusOne = (item.getData() == -1);

		ItemStack[] items = human.inventory.items;

		for (int i = 0; i < items.length; i++) {// for each item in inventory
			if (items[i] == null) continue;
			if (amount <= var4) break; // if the amount of items in the given stack <= 0

			if (items[i].doMaterialsMatch(item) || (dataMinusOne && items[i].id == item.id))
				var4 += items[i].count;
		}// This counts the amount of items of the given stack

		if (var4 < amount) return false;

		var4 = 0;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == null  || (!items[i].doMaterialsMatch(item)  && (!dataMinusOne || items[i].id  != item.id))) continue;

			for (int var8 = items[i].count; var8 > 0; var8--) {
				if (items[i].count <= 1) items[i] = null;
				else items[i].count--;

				if (++var4 >= amount) return true;
			}

		}

		if (notifyUser) human.a("You don't have enough fuel/klein power to do that.");

		return false;
	}

	public static double direction(EntityHuman var0) {		
		if (var0.pitch <= -55F)
			return 1.0D;
		else if ((var0.pitch > -55.0F) && (var0.pitch < 55.0F))
			return (MathHelper.floor(var0.yaw * 4.0F / 360.0F + 0.5f) & 0x3) + 2;
		else
			return 0.0D;
				
	}
	public static double heading(EntityHuman var0) {
		return (MathHelper.floor(var0.yaw * 4.0F / 360.0F + 0.5f) & 0x3) + 2;
	}
	public static double playerX(EntityHuman var0) {
		return MathHelper.floor(var0.locX);
	}
	public static double playerY(EntityHuman var0) {
		return MathHelper.floor(var0.locY);
	}
	public static double playerZ(EntityHuman var0) {
		return MathHelper.floor(var0.locZ);
	}

	public static void doLeftClick(World var0, EntityHuman var1) {
		ItemStack old = var1.U();//iteminhand
		if (old != null) {
			if (old.getItem() instanceof ItemEECharged) {
				((ItemEECharged) old.getItem()).doLeftClick(old, var0, var1);
			}
		}
	}

	public static void doAlternate(World var0, EntityHuman human) {
		ItemStack var2 = human.U();

		if (var2 == null || !(var2.getItem() instanceof ItemEECharged)) {
			armorCheck(human);
		} else {
			((ItemEECharged) var2.getItem()).doAlternate(var2, var0, human);
		}
	}

	/** Gem armor explodes if offensive turned on. Calls event. */
	private static void armorCheck(EntityHuman human) {
		if (hasRedArmor(human) && getPlayerArmorOffensive(human)) {
			if (EEEventManager.callEvent(new EEArmorEvent(human, EEAction.ALTERNATE, EEArmorAction.OffensiveExplode))) return;
			Combustion var1 = new Combustion(human.world, human, human.locX, human.locY, human.locZ, 4.0F);
			var1.doExplosionA();
			var1.doExplosionB(true);
		}
	}

	private static boolean hasRedArmor(EntityHuman var0) {
		return (var0.inventory.armor[2] != null) && (var0.inventory.armor[2].getItem() instanceof ItemRedArmorPlus);
	}

	public static void doToggle(World var0, EntityHuman var1) {
		ItemStack var2 = var1.U();

		if (var2 == null) {
			if ((hasMovementArmor(var1)) && (getPlayerToggleCooldown(var1) <= 0)) {
				updatePlayerArmorMovement(var1, true);
				setPlayerToggleCooldown(var1, 20);
			}
		} else if ((var2.getItem() instanceof ItemEECharged)) {
			((ItemEECharged) var2.getItem()).doToggle(var2, var0, var1);
		} else if ((hasMovementArmor(var1)) && (getPlayerToggleCooldown(var1) <= 0)) {
			updatePlayerArmorMovement(var1, true);
			setPlayerToggleCooldown(var1, 20);
		}
	}

	public static void doJumpTick(World var0, EntityPlayer var1) {
		bootsCheck(var1);
	}

	private static void bootsCheck(EntityHuman var0) {
		if ((hasRedBoots(var0)) && (getPlayerArmorMovement(var0))) {
			var0.motY += 0.1D;
		}
	}

	private static boolean hasRedBoots(EntityHuman var0) {
		return (var0.inventory.armor[0] != null) && ((var0.inventory.armor[0].getItem() instanceof ItemRedArmorPlus));
	}

	public static void doSneakTick(World var0, EntityPlayer var1) {
		greavesCheck(var1);
	}

	private static void greavesCheck(EntityHuman var0) {
		if ((hasRedGreaves(var0)) && (getPlayerArmorOffensive(var0))) {
			var0.motY -= 0.97D;
			doShockwave(var0);
		}
	}

	private static void doShockwave(EntityHuman var0) {
		List<Entity> var1 = var0.world.a(EntityLiving.class,
				AxisAlignedBB.b(var0.locX - 7.0D, var0.locY - 7.0D, var0.locZ - 7.0D, var0.locX + 7.0D, var0.locY + 7.0D, var0.locZ + 7.0D));

		for (Entity var3 : var1) {
			if (!(var3 instanceof EntityHuman)) {
				var3.motX += 0.2D / (var3.locX - var0.locX);
				var3.motY += 0.06D;
				var3.motZ += 0.2D / (var3.locZ - var0.locZ);
			}
		}

		/*
		for (int var2 = 0; var2 < var1.size(); var2++)
		{
			Entity var3 = var1.get(var2);

			if (!(var3 instanceof EntityHuman))
			{
				var3.motX += 0.2D / (var3.locX - var0.locX);
				var3.motY += 0.05999999865889549D;
				var3.motZ += 0.2D / (var3.locZ - var0.locZ);
			}
		}*/

		List<Entity> var6 = var0.world.a(EntityArrow.class, AxisAlignedBB.b((float) var0.locX - 5.0F, var0.locY - 5.0D, (float) var0.locZ - 5.0F,
				(float) var0.locX + 5.0F, var0.locY + 5.0D, (float) var0.locZ + 5.0F));

		for (Entity var4 : var6) {
			var4.motX += 0.2D / (var4.locX - var0.locX);
			var4.motY += 0.06D;
			var4.motZ += 0.2D / (var4.locZ - var0.locZ);
		}

		/*
		for (int var7 = 0; var7 < var6.size(); var7++)
		{
			Entity var4 = var6.get(var7);
			var4.motX += 0.2D / (var4.locX - var0.locX);
			var4.motY += 0.05999999865889549D;
			var4.motZ += 0.2D / (var4.locZ - var0.locZ);
		}*/

		List<Entity> var8 = var0.world.a(EntityFireball.class, AxisAlignedBB.b((float) var0.locX - 5.0F, var0.locY - 5.0D, (float) var0.locZ - 5.0F,
				(float) var0.locX + 5.0F, var0.locY + 5.0D, (float) var0.locZ + 5.0F));

		for (Entity var5 : var8) {
			var5.motX += 0.2D / (var5.locX - var0.locX);
			var5.motY += 0.06D;
			var5.motZ += 0.2D / (var5.locZ - var0.locZ);
		}

		/*
		for (int var9 = 0; var9 < var8.size(); var9++)
		{
			Entity var5 = var8.get(var9);
			var5.motX += 0.2D / (var5.locX - var0.locX);
			var5.motY += 0.05999999865889549D;
			var5.motZ += 0.2D / (var5.locZ - var0.locZ);
		}*/
	}

	private static boolean hasRedGreaves(EntityHuman var0) {
		return (var0.inventory.armor[1] != null) && ((var0.inventory.armor[1].getItem() instanceof ItemRedArmorPlus));
	}

	public static void doRelease(World var0, EntityHuman var1) {
		ItemStack var2 = var1.U();

		if (var2 == null || !(var2.getItem() instanceof ItemEECharged)) {
			helmetCheck(var1);
		} else {
			((ItemEECharged) var2.getItem()).doRelease(var2, var0, var1);
		}
	}

	/** Gem helmet creates lightning if offensive turned on. Calls event. */
	private static void helmetCheck(EntityHuman var0) {
		if ((hasRedHelmet(var0)) && (getPlayerArmorOffensive(var0))) {
			if (EEEventManager.callEvent(new EEArmorEvent(var0, EEAction.RELEASE, EEArmorAction.OffensiveStrike))) return;

			float var2 = var0.lastPitch + (var0.pitch - var0.lastPitch) * 1f;
			float var3 = var0.lastYaw + (var0.yaw - var0.lastYaw) * 1f;
			double var4 = var0.lastX + (var0.locX - var0.lastX) * 1f;
			double var6 = var0.lastY + (var0.locY - var0.lastY) * 1f + 1.62D - var0.height;
			double var8 = var0.lastZ + (var0.locZ - var0.lastZ) * 1f;
			Vec3D var10 = Vec3D.create(var4, var6, var8);
			
			float tmp = -var3 * 0.01745329F - 3.141593F;
			float var11 = MathHelper.cos(tmp);
			float var12 = MathHelper.sin(tmp);
			tmp = -var2 * 0.01745329F;
			float var13 = -MathHelper.cos(tmp);
			float var14 = MathHelper.sin(tmp);
			float var15 = var12 * var13;
			float var17 = var11 * var13;

			Vec3D var20 = var10.add(var15 * 150d, var14 * 150d, var17 * 150d);
			MovingObjectPosition var21 = var0.world.rayTrace(var10, var20, true);

			if (var21 == null) return;

			if (var21.type == EnumMovingObjectType.TILE) {
				int var22 = var21.b;
				int var23 = var21.c;
				int var24 = var21.d;
				var0.world.strikeLightning(new EntityWeatherLighting(var0.world, var22, var23, var24));
			}
		}
	}

	/** @return If the player has a Gem armor helmet */
	private static boolean hasRedHelmet(EntityHuman human) {
		return (human.inventory.armor[3] != null) && ((human.inventory.armor[3].getItem() instanceof ItemRedArmorPlus));
	}

	public static void doCharge(World world, EntityHuman human) {
		ItemStack itemInHand = human.U();

		if (itemInHand == null || !(itemInHand.getItem() instanceof ItemEECharged)) {
			if (hasOffensiveArmor(human) && (getPlayerToggleCooldown(human) <= 0)) {
				updatePlayerArmorOffensive(human, true);
				setPlayerToggleCooldown(human, 20);
			}
		} else {
			ItemEECharged var3 = (ItemEECharged) itemInHand.getItem();

			if (!human.isSneaking()) {
				int max = var3.getMaxCharge();
				int lvl = var3.chargeLevel(itemInHand);
				int charge = var3.chargeGoal(itemInHand);
				if (max > 0 && lvl < max && charge < max) {
					if (EEEventManager.callEvent(new EEChargeEvent(itemInHand, human, lvl, lvl + 1, max))) return;

					var3.setShort(itemInHand, "chargeGoal", charge + 1);
				}
			} else {
				var3.doUncharge(itemInHand, world, human);
			}
		}
	}

	/** @return If the player has offensive armor (Gem chest, Gem leggings, Gem helmet) */
	private static boolean hasOffensiveArmor(EntityHuman human) {
		return ((human.inventory.armor[2] != null) && (human.inventory.armor[2].getItem() instanceof ItemRedArmorPlus))
				|| ((human.inventory.armor[1] != null) && (human.inventory.armor[1].getItem() instanceof ItemRedArmorPlus))
				|| ((human.inventory.armor[3] != null) && (human.inventory.armor[3].getItem() instanceof ItemRedArmorPlus));
	}

	/** @return If the player has movement armor (Gem boots) */
	private static boolean hasMovementArmor(EntityHuman human) {
		return (human.inventory.armor[0] != null) && ((human.inventory.armor[0].getItem() instanceof ItemRedArmorPlus));
	}

	static boolean isPlayerCharging(EntityHuman var0, Item var1) {
		HashMap<Item, Boolean> old = playerItemCharging.get(var0);
		if (old != null) {
			Boolean old2 = old.get(var1);
			if (old2 != null) return old2.booleanValue();
		}
		return false;
	}

	public static void updatePlayerEffect(Item var0, int var1, EntityHuman var2) {
		HashMap<Item, Integer> old = playerEffectDurations.get(var2);

		HashMap<Item, Integer> var3 = old != null ? old : new HashMap<Item, Integer>();
		var3.put(var0, var1);
		playerEffectDurations.put(var2, var3);
	}

	public static int getPlayerEffect(Item var0, EntityHuman var1) {
		HashMap<Item, Integer> old = playerEffectDurations.get(var1);
		if (old != null) {
			Integer old2 = old.get(var0);
			if (old2 != null) return old2.intValue();
		}
		return 0;
	}

	public static int getPlayerToggleCooldown(EntityHuman var0) {
		Integer old = playerToggleCooldown.get(var0);
		if (old == null) {
			playerToggleCooldown.put(var0, 0);
			return 0;
		}

		return old.intValue();
	}

	public static void setPlayerToggleCooldown(EntityHuman var0, int var1) {
		if (playerToggleCooldown.get(var0) == null) {
			playerToggleCooldown.put(var0, 0);
		} else {
			playerToggleCooldown.put(var0, var1);
		}
	}

	public static void updatePlayerToggleCooldown(EntityHuman var0) {
		Integer old = playerToggleCooldown.get(var0);
		if (old == null) {
			playerToggleCooldown.put(var0, 0);
		} else {
			playerToggleCooldown.put(var0, old.intValue() - 1);
		}
	}

	/** @return The current buildmode */
	public static int getBuildMode(EntityHuman var0) {
		Integer old = playerBuildMode.get(var0);
		if (old == null) {
			playerBuildMode.put(var0, 0);
			return 0;
		}
		return old.intValue();
	}

	/** Toggle the buildmode */
	public static void updateBuildMode(EntityHuman var0) {
		/*HumanEntity bukkitEntity = var0.getBukkitEntity();
		boolean canUse = true;
		if (bukkitEntity.hasMetadata("CanUseMercurialEye")) canUse = bukkitEntity.getMetadata("CanUseMercurialEye").get(0).asBoolean();
		if (!canUse){
			var0.a("You dont have permission to use the Mercurial Eye.");
			return;
		}*/
		Integer old = playerBuildMode.get(var0);
		
		int mode = (old != null ? (old.intValue() + 1)%4:0);

		playerBuildMode.put(var0, mode);

		if (mode == 0) var0.a("Mercurial Extension mode.");
		else if (mode == 1) var0.a("Mercurial Creation mode.");
		else if (mode == 2) var0.a("Mercurial Transmute mode.");
		else if (mode == 3) var0.a("Mercurial Pillar mode. [Careful!]");
	}

	/** @return The current offensive mode */
	public static boolean getPlayerArmorOffensive(EntityHuman var0) {
		Boolean old = playerArmorOffensiveToggle.get(var0);
		if (old == null) {
			playerArmorOffensiveToggle.put(var0, false);
			return false;
		}
		return old.booleanValue();

	}

	/** Toggle the offensive mode. Calls event if enabling. */
	public static void updatePlayerArmorOffensive(EntityHuman human, boolean notify) {
		Boolean old = playerArmorOffensiveToggle.get(human);
		boolean mode = old == null ? false : !old.booleanValue();
		
		if (mode){
			if (EEEventManager.callEvent(new EEArmorEvent(human, EEAction.TOGGLE, EEArmorAction.OffensiveActivate))) return;
		}
		
		playerArmorOffensiveToggle.put(human, mode);

		if (notify){
			human.a("Armor offensive powers "+ (mode ? "on" : "off") + ".");
		}
	}

	/** @return The current Movement mode */
	public static boolean getPlayerArmorMovement(EntityHuman var0) {
		Boolean old = playerArmorMovementToggle.get(var0);
		if (old == null) {
			playerArmorMovementToggle.put(var0, false);
			return false;
		}
		return old.booleanValue();
	}

	/** Toggle the movement mode. */
	public static void updatePlayerArmorMovement(EntityHuman human, boolean notify) {
		Boolean old = playerArmorMovementToggle.get(human);
		boolean mode = old == null ? false : !old.booleanValue();
		
		if (mode){
			if (EEEventManager.callEvent(new EEArmorEvent(human, EEAction.CHARGE, EEArmorAction.MovementActivate))) return;
		}
		
		playerArmorMovementToggle.put(human, mode);

		if (notify){
			human.a("Armor movement powers "+ (mode ? "on" : "off") + ".");
		}
	}

	/** @return The current hammer mode */
	public static boolean getHammerMode(EntityHuman var0) {
		Boolean hammerMode = playerHammerMode.get(var0);
		if (hammerMode == null) {
			playerHammerMode.put(var0, false);
			return false;
		}

		return hammerMode.booleanValue();
	}

	/** Toggle the hammer mode */
	public static void updateHammerMode(EntityHuman human, boolean notify) {
		Boolean old = playerHammerMode.get(human);
		boolean mode = old == null ? false : !old.booleanValue();
		
		playerHammerMode.put(human, mode);
		
		if (notify){
			human.a("Hammer "+ (mode ? "mega" : "normal") + "-impact mode.");
		}
	}

	/** @return The current sword mode */
	public static boolean getSwordMode(EntityHuman var0) {
		Boolean old = playerSwordMode.get(var0);
		if (old == null) {
			playerSwordMode.put(var0, false);
			return false;
		}
		return old.booleanValue();
	}

	/** Toggle the swordmode */
	public static void updateSwordMode(EntityHuman human) {
		Boolean old = playerSwordMode.get(human);
		boolean mode = old == null ? false : !old.booleanValue();
		
		playerSwordMode.put(human, mode);

		if (mode) human.a("Sword AoE will harm peaceful/aggressive.");
		else human.a("Sword AoE will harm aggressive only.");
	}

	/** @return The current watch mode */
	public static int getWatchCycle(EntityHuman var0) {
		Integer old = playerWatchCycle.get(var0);
		if (old == null) {
			playerWatchCycle.put(var0, 0);
			return 0;
		}
		return old.intValue();
	}

	/** Toggle the watch mode */
	public static void updateWatchCycle(EntityHuman human) {
		Integer old = playerWatchCycle.get(human);
		int mode = (old != null? (old.intValue() + 1)%3 : 0);

		if (mode == 1){
			if (EEEventManager.callEvent(new EEWOFTEvent(EEAction.RELEASE, human, EEWatchAction.TimeForward))) return;
		} else if (mode == 2) {
			if (EEEventManager.callEvent(new EEWOFTEvent(EEAction.RELEASE, human, EEWatchAction.TimeBackward))) return;
		}
		
		 playerWatchCycle.put(human, mode);

		if (mode == 0) human.a("Sun-scroll is off.");
		else if (mode == 1) human.a("Sun-scrolling forward.");
		else if (mode == 2) human.a("Sun-scrolling backwards.");
	}

	/** @return The current tool mode */
	public static int getToolMode(EntityHuman var0) {
		Integer old = playerToolMode.get(var0);
		if (old == null) {
			playerToolMode.put(var0, 0);
			return 0;
		}
		return old.intValue();
	}

	/** Toggle a tool's modes. */
	public static void updateToolMode(EntityHuman human) {
		Integer old = playerToolMode.get(human);

		int mode = 0;
		if (old != null) mode = (old.intValue()+1)%4;
		
		playerToolMode.put(human, mode);

		if (mode == 0) human.a("Tool set to normal.");
		else if (mode == 1) human.a("Tool set to tall-shot.");
		else if (mode == 2) human.a("Tool set to wide-shot.");
		else if (mode == 3) human.a("Tool set to long-shot.");
	}

	public static boolean isPlayerInLava(EntityHuman var0) {
		Boolean old = playerInLava.get(var0);
		if (old == null) {
			playerInLava.put(var0, false);
			return false;
		}

		return old.booleanValue();
	}

	public static void updatePlayerInLava(EntityHuman var0, boolean var1) {
		playerInLava.put(var0, Boolean.valueOf(var1));
	}

	public static boolean isPlayerInWater(EntityHuman var0) {
		Boolean old = playerInWater.get(var0);
		if (old == null) {
			playerInWater.put(var0, false);
			return false;
		}

		return old.booleanValue();
	}

	public static void updatePlayerInWater(EntityHuman var0, boolean var1) {
		playerInWater.put(var0, Boolean.valueOf(var1));
	}

	private static void setupCraftHook() {
		ICraftingHandler var0 = new ICraftingHandler() {
			public void onTakenFromCrafting(EntityHuman var1, ItemStack var2, IInventory var3) {
				int var4 = 0;

				if ((var2 != null) && (EEMergeLib.mergeOnCraft.contains(var2.id))) {
					for (int var5 = 0; var5 < var3.getSize(); var5++) {
						ItemStack var6 = var3.getItem(var5);

						if ((var6 != null) && ((var6.getItem() instanceof ItemKleinStar)) && (((ItemKleinStar) var6.getItem()).getKleinPoints(var6) > 0)) {
							var4 += ((ItemKleinStar) var6.getItem()).getKleinPoints(var6);
						}
					}

					((ItemKleinStar) var2.getItem()).setKleinPoints(var2, var4);
				} else if ((var2 != null) && (EEMergeLib.destroyOnCraft.contains(var2.id)) && (var2.id == EEItem.arcaneRing.id)) {
					for (int var5 = 0; var5 < var3.getSize(); var5++) {
						var3.setItem(var5, null);
					}
				}
			}
		};
		MinecraftForge.registerCraftingHandler(var0);
	}

	public static EEBase getInstance() {
		return eeBaseInstance;
	}

	/**
	 * Notice: indirectly gets Tile Entity
	 */
	public static float getPedestalFactor(World var0) {
		float var1 = 1.0F;
		validatePedestalCoords(var0);

		for (int var2 = 0; var2 < pedestalCoords.size(); var2++) {
			if (pedestalCoords.get(var2) != null) {
				var1 = (float) (var1 * 0.9D);
				if (var1 < 0.1F) return 0.1F;
			}
		}

		return var1 < 0.1F ? 0.1F : var1;
	}

	/**
	 * Notice: indirectly gets Tile Entity
	 */
	public static void addPedestalCoords(TilePedestal var0) {
		int var1 = 0;
		int[] var2 = {var0.x, var0.y, var0.z};
		for (; pedestalCoords.get(var1) != null; var1++);
		pedestalCoords.put(var1, var2);
		validatePedestalCoords(var0.world);
	}

	/**
	 * Notice: gets Tile Entity
	 */
	public static void validatePedestalCoords(World var0) {
		for (int var1 = 0; var1 < pedestalCoords.size(); var1++) {
			int[] var2 = pedestalCoords.get(var1);
			if (var2 != null) {
				TilePedestal old = EEPBase.getTileEntity(var0, var2[0], var2[1], var2[2], TilePedestal.class);
				if (old == null) {
					removePedestalCoord(var1);
				} else if (!old.isActivated()) {
					removePedestalCoord(var1);
				} else {
					for (int var3 = 0; var3 < pedestalCoords.size(); var3++) {
						int[] var4 = pedestalCoords.get(var3);
						if ((var1 != var3) && (var4 != null)) {
							if (coordsEqual(var2, var4)) removePedestalCoord(var3);
						}
					}
				}
			}
		}
	}
	
	private static boolean coordsEqual(int[] var0, int[] var1) {
		return (var0[0] == var1[0]) && (var0[1] == var1[1]) && (var0[2] == var1[2]);
	}

	private static void removePedestalCoord(int var0) {
		pedestalCoords.remove(var0);
	}

	public static float getPlayerWatchFactor() {
		float var0 = 1.0F;

		for (int var1 = 0; var1 < playerWoftFactor; var1++) {
			var0 = (float) (var0 * 0.9D);
		}

		return var0;
	}

	public static void ConsumeReagentForDuration(ItemStack var0, EntityHuman var1, boolean var2) {
		if (!EEProxy.isClient(var1.world)) {
			if (consumeKleinStarPoint(var1, 32)) {
				updatePlayerEffect(var0.getItem(), 64, var1);
			} else if (Consume(new ItemStack(EEItem.aeternalisFuel), var1, var2)) {
				updatePlayerEffect(var0.getItem(), 16384, var1);
			} else if (Consume(new ItemStack(EEItem.mobiusFuel), var1, var2)) {
				updatePlayerEffect(var0.getItem(), 4096, var1);
			} else if (Consume(new ItemStack(Block.GLOWSTONE), var1, false)) {
				updatePlayerEffect(var0.getItem(), 3072, var1);
			} else if (Consume(new ItemStack(EEItem.alchemicalCoal), var1, false)) {
				updatePlayerEffect(var0.getItem(), 1024, var1);
			} else if (Consume(new ItemStack(Item.GLOWSTONE_DUST), var1, false)) {
				updatePlayerEffect(var0.getItem(), 768, var1);
			} else if (Consume(new ItemStack(Item.SULPHUR), var1, false)) {
				updatePlayerEffect(var0.getItem(), 384, var1);
			} else if (Consume(new ItemStack(Item.COAL, 1, 0), var1, false)) {
				updatePlayerEffect(var0.getItem(), 256, var1);
			} else if (Consume(new ItemStack(Item.REDSTONE), var1, false)) {
				updatePlayerEffect(var0.getItem(), 128, var1);
			} else if (Consume(new ItemStack(Item.COAL, 1, 1), var1, false)) {
				updatePlayerEffect(var0.getItem(), 64, var1);
			}
		}
	}

	public static void ConsumeReagent(ItemStack var0, EntityHuman var1, boolean var2) {
		if (consumeKleinStarPoint(var1, 32)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 4);
		} else if (Consume(new ItemStack(EEItem.aeternalisFuel, 1), var1, false)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 1024);
		} else if (Consume(new ItemStack(EEItem.mobiusFuel, 1), var1, false)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 256);
		} else if (Consume(new ItemStack(Block.GLOWSTONE, 1), var1, false)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 192);
		} else if (Consume(new ItemStack(EEItem.alchemicalCoal, 1), var1, false)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 64);
		} else if (Consume(new ItemStack(Item.GLOWSTONE_DUST, 1), var1, false)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 48);
		} else if (Consume(new ItemStack(Item.SULPHUR, 1), var1, false)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 24);
		} else if (Consume(new ItemStack(Item.COAL, 1, 0), var1, var2)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 16);
		} else if (Consume(new ItemStack(Item.REDSTONE, 1), var1, var2)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 8);
		} else if (Consume(new ItemStack(Item.COAL, 1, 1), var1, var2)) {
			EEPBase.setShort(var0, "fuelRemaining", EEPBase.getShort(var0, "fuelRemaining") + 4);
		}
	}

	public static boolean isLeftClickDown(EntityHuman var0, MinecraftServer var1) {
		Boolean old = playerLeftClick.get(var0);
		if (old == null) {
			resetLeftClick(var0);
			return false;
		}
		return old.booleanValue();
	}

	public static void resetLeftClick(EntityHuman var0) {
		playerLeftClick.put(var0, false);
	}

	public static void watchTransGrid(EntityHuman var0) {
		playerTransGridOpen.put(var0, Boolean.valueOf(true));
	}

	public static void closeTransGrid(EntityHuman var0) {
		playerTransGridOpen.put(var0, Boolean.valueOf(false));
	}

	public static Boolean getTransGridOpen(EntityHuman var0) {
		if (playerTransGridOpen.get(var0) == null) {
			playerTransGridOpen.put(var0, Boolean.valueOf(false));
		}

		return playerTransGridOpen.get(var0);
	}

	public static int getMachineFactor() {
		return machineFactor > 16 ? 16 : machineFactor < 1 ? 1 : machineFactor;
	}

	/** @return True if the given entity is passive or neutral. */
	public static boolean isNeutralEntity(Entity entity) {
		return (entity instanceof EntityAnimal) || (entity instanceof EntityVillager) || ((entity instanceof EntitySnowman))
				|| ((entity instanceof EntityIronGolem));
		/*return ((entity instanceof EntitySheep))
				|| ((entity instanceof EntityCow))
				|| ((entity instanceof EntityPig))
				|| ((entity instanceof EntityChicken))
				|| ((entity instanceof EntityMushroomCow))
				|| ((entity instanceof EntityVillager))
				|| ((entity instanceof EntityOcelot))
				|| ((entity instanceof EntityWolf))
				|| ((entity instanceof EntitySnowman))
				|| ((entity instanceof EntityIronGolem));*/
	}

	/** @return True if the given entity is hostile */
	public static boolean isHostileEntity(Entity entity) {
		return (entity instanceof EntityMonster) || (entity instanceof EntitySlime) || (entity instanceof EntityGhast) || (entity instanceof EntityMagmaCube);
		/*return ((entity instanceof EntityCreeper))
				|| ((entity instanceof EntityZombie))
				|| ((entity instanceof EntitySkeleton))
				|| ((entity instanceof EntitySpider))
				|| ((entity instanceof EntityCaveSpider))
				|| ((entity instanceof EntityEnderman))
				|| ((entity instanceof EntitySilverfish))
				|| ((entity instanceof EntitySlime))
				|| ((entity instanceof EntityGhast))
				|| ((entity instanceof EntityMagmaCube))
				|| ((entity instanceof EntityPigZombie))
				|| ((entity instanceof EntityBlaze));*/
	}

	public static boolean isEmpty(ItemStack[] items) {
		for (ItemStack current : items) {
			if (current != null) return false;
		}
		return true;
	}

}