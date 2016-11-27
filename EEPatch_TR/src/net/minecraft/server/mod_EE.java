/* Warning: No line numbers available in class file */
package net.minecraft.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ee.EEAddonBC;
import ee.EEAddonForestry;
import ee.EEAddonIC2;
import ee.EEAddonRP2;
import ee.EEBase;
import ee.EEBlock;
import ee.EEGuiHandler;
import ee.EEItem;
import ee.EEMaps;
import ee.EEPatch;
import ee.EntityGrimArrow;
import ee.EntityHyperkinesis;
import ee.EntityLavaEssence;
import ee.EntityLootBall;
import ee.EntityNovaPrimed;
import ee.EntityPhilosopherEssence;
import ee.EntityPyrokinesis;
import ee.EntityWaterEssence;
import ee.EntityWindEssence;
import ee.ItemEECharged;
import ee.ItemRedArmorPlus;
import ee.ItemSwiftWolfRing;
import ee.ItemWatchOfTime;
import ee.core.PickupHandler;
import ee.network.PacketHandler;
import forge.DimensionManager;
import forge.MinecraftForge;
import forge.NetworkMod;

public class mod_EE extends NetworkMod {
	public static final String MOD_NAME = "Equivalent Exchange 2";
	public static final String CHANNEL_NAME = "EE2";
	public static final String SOUND_RESOURCE_LOCATION = "/ee/sound/";
	public static final String SOUND_PREFIX = "ee.sound.";
	private static int[] tickCounter = new int[40];
	private static mod_EE instance;
	private int blackListTimer;
	@SuppressWarnings("unused")
	private int rmArmorClearSlot;
	public static int pedestalModelID;
	public static int chestModelID;
	@SuppressWarnings("rawtypes")
	public static HashMap playerShockwave = new HashMap();

	public void load() {}

	public mod_EE() {
		instance = this;
		MinecraftForge.versionDetect("Equivalent Exchange 2", 3, 3, 7);
		ModLoader.setInGameHook(this, true, true);
		MinecraftForge.registerConnectionHandler(new PacketHandler());
		MinecraftForge.setGuiHandler(this, new EEGuiHandler());
		EEBase.init(this);
		MinecraftForge.registerEntity(EntityPhilosopherEssence.class, this, 143, 300, 2, true);
		MinecraftForge.registerEntity(EntityWaterEssence.class, this, 144, 300, 2, true);
		MinecraftForge.registerEntity(EntityLavaEssence.class, this, 145, 300, 2, true);
		MinecraftForge.registerEntity(EntityWindEssence.class, this, 146, 300, 2, true);
		MinecraftForge.registerEntity(EntityPyrokinesis.class, this, 147, 300, 2, true);
		MinecraftForge.registerEntity(EntityGrimArrow.class, this, 148, 300, 2, true);
		MinecraftForge.registerEntity(EntityLootBall.class, this, 149, 300, 2, true);
		MinecraftForge.registerEntity(EntityNovaPrimed.class, this, 150, 300, 2, true);
		MinecraftForge.registerEntity(EntityHyperkinesis.class, this, 151, 300, 2, true);
		EEItem.init();
		EEBlock.init();
		EEMaps.InitAlchemicalValues();
		EEMaps.InitFlyingItems();
		EEMaps.InitFuelItems();
		EEMaps.InitFireImmunities();
		EEMaps.InitDurationEffectItems();
		EEMaps.InitEERecipes();
		EEMaps.InitRepairRecipes();
		EEMaps.InitChestItems();
		EEMaps.InitChargeditems();
		EEMaps.InitWoodAndLeafBlocks();
		EEMaps.InitPedestalItems();
		EEMaps.InitModItems();
		EEMaps.InitOreBlocks();
		EEMaps.InitBlacklist();
		EEMaps.InitMetaData();
		pedestalModelID = ModLoader.getUniqueBlockModelID(this, true);
		chestModelID = ModLoader.getUniqueBlockModelID(this, true);
		blackListTimer = 100;
		MinecraftForge.registerPickupHandler(new PickupHandler());
	}

	public String getVersion() {
		return String.format("%d.%d.%d.%d", new Object[] {1, 4, 6, 5});
	}

	@SuppressWarnings("unchecked")
	public boolean onTickInGame(MinecraftServer var1) {
		EEProxy.Init(var1, this);
		
		World[] var2 = DimensionManager.getWorlds();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			World var5 = var2[var4];
			onTickInGame(var1, var5.players, var5);
		}

		return true;
	}

	public boolean onTickInGame(MinecraftServer var1, List<EntityHuman> var2, World var3) {
		if (!EEBase.externalModsInitialized) {
			for (int var4 = 0; var4 < ModLoader.getLoadedMods().size(); ++var4) {
				if (ModLoader.getLoadedMods().get(var4).toString().contains("mod_IC2")) {
					EEAddonIC2.initialize();
				} else if (ModLoader.getLoadedMods().get(var4).toString().contains("mod_RedPowerCore")) {
					EEAddonRP2.initBase();
				} else if (ModLoader.getLoadedMods().get(var4).toString().contains("mod_RedPowerWorld")) {
					EEAddonRP2.initBase();
					EEAddonRP2.initWorld();
				} else if (ModLoader.getLoadedMods().get(var4).toString().contains("mod_BuildCraftEnergy")) {
					EEAddonBC.initialize();
				} else {
					if (!ModLoader.getLoadedMods().get(var4).toString().contains("mod_Forestry")) continue;
					EEAddonForestry.initialize();
				}
			}

			EEBase.externalModsInitialized = true;
		}

		//Indirectly gets tile entity
		EEBase.validatePedestalCoords(var3);
		
		//String name = var3.worldProvider.dimension.getWorld().getName();
		//Integer remaining = tickCounter.get(name);
		int remaining = tickCounter[var3.worldProvider.dimension+10];

		if (remaining % EEPatch.transTableInterval == 0) {
			doTransGridUpdates(var2);
			remaining = 0;
		}

		doWatchCheck(var2, var3);
		doFlightCheck(var2, var3);
		Iterator<EntityHuman> var6 = var2.iterator();

		while (var6.hasNext()) {
			EntityHuman var5 = var6.next();

			if (blackListTimer <= 0) {
				blackListTimer = 100;

				if (EEMaps.isBlacklisted(var5.name)) {
					var5.world.strikeLightning(new EntityWeatherLighting(var5.world, var5.locX, var5.locY, var5.locZ));
				}
			}

			doGemPowers(var5);
			doEquipCheck(var5, var3);
			doFireImmuneCheck(var5);
		}

		if (blackListTimer > 0) {
			blackListTimer -= 1;
		}

		tickCounter[var3.worldProvider.dimension+10] = remaining+1;
		return true;
	}

	private void doTransGridUpdates(List<EntityHuman> var1) {
		Iterator<EntityHuman> var2 = var1.iterator();

		while (var2.hasNext()) {
			EntityHuman var3 = var2.next();

			if (!EEBase.getTransGridOpen(var3).booleanValue()) continue;
			EEPatch.getTransData(var3).onUpdate(var3.world, var3);
		}
	}

	private void doGemPowers(EntityHuman var1) {
		EEBase.updatePlayerToggleCooldown(var1);

		for (int var2 = 0; var2 <= 3; ++var2) {
			if (var1.inventory.armor[var2] == null) continue;
			ItemStack var3 = var1.inventory.armor[var2];

			if (var2 == 2 && var3.getItem() instanceof ItemRedArmorPlus && var1.hasEffect(MobEffectList.POISON)) {
				var1.d(var1.getEffect(MobEffectList.POISON));
			}

			if (var2 == 1 && var3.getItem() instanceof ItemRedArmorPlus) {
				List<Entity> var4 = var1.world.a(EntityLiving.class,
						AxisAlignedBB.b(var1.locX - 5.0D, var1.locY - 5.0D, var1.locZ - 5.0D, var1.locX + 5.0D, var1.locY + 5.0D, var1.locZ + 5.0D));

				for (int var5 = 0; var5 < var4.size(); ++var5) {
					Entity var6 = var4.get(var5);

					if (var6 instanceof EntityHuman || var6.motX <= 0.0D && var6.motZ <= 0.0D) continue;
					var6.motX *= 0.1D;
					var6.motZ *= 0.1D;
				}

			}

			if (var2 == 0 && var3.getItem() instanceof ItemRedArmorPlus) {
				if (!var1.isSprinting() && EEBase.getPlayerArmorMovement(var1)) {
					var1.setSprinting(true);
				}

				if (EEBase.getPlayerArmorMovement(var1)) {
					var1.am = (float) (var1.am + 0.04D);

					if (var1.am > 0.3F) {
						var1.am = 0.3F;
					}

					if (var1.motY > 1.0D) {
						var1.motY = 1.0D;
					}

					if (var1.motY < 0.0D && !var1.isSneaking()) {
						var1.motY *= 0.88D;
					}
				}

				if (var1.fallDistance > 0.0F) {
					var1.fallDistance = 0.0F;
				}
			}

			if (var2 != 3 || !(var3.getItem() instanceof ItemRedArmorPlus) || var1.getAirTicks() >= 20) continue;
			var1.setAirTicks(20);
		}
	}

	@SuppressWarnings("unused")
	private void doShockwave(EntityHuman var1) {
		List<Entity> var2 = var1.world.a(EntityLiving.class,
				AxisAlignedBB.b(var1.locX - 7.0D, var1.locY - 7.0D, var1.locZ - 7.0D, var1.locX + 7.0D, var1.locY + 7.0D, var1.locZ + 7.0D));

		for (int var3 = 0; var3 < var2.size(); ++var3) {
			Entity var4 = var2.get(var3);

			if (var4 instanceof EntityHuman) continue;
			var4.motX += 0.2D / (var4.locX - var1.locX);
			var4.motY += 0.06D;
			var4.motZ += 0.2D / (var4.locZ - var1.locZ);
		}

		List<Entity> var7 = var1.world.a(EntityArrow.class, AxisAlignedBB.b((float) var1.locX - 5.0F, var1.locY - 5.0D, (float) var1.locZ - 5.0F,
				(float) var1.locX + 5.0F, var1.locY + 5.0D, (float) var1.locZ + 5.0F));

		for (int var8 = 0; var8 < var7.size(); ++var8) {
			Entity var5 = var7.get(var8);
			var5.motX += 0.2D / (var5.locX - var1.locX);
			var5.motY += 0.06D;
			var5.motZ += 0.2D / (var5.locZ - var1.locZ);
		}

		List<Entity> var10 = var1.world.a(EntityFireball.class, AxisAlignedBB.b((float) var1.locX - 5.0F, var1.locY - 5.0D, (float) var1.locZ - 5.0F,
				(float) var1.locX + 5.0F, var1.locY + 5.0D, (float) var1.locZ + 5.0F));

		for (int var9 = 0; var9 < var10.size(); ++var9) {
			Entity var6 = var10.get(var9);
			var6.motX += 0.2D / (var6.locX - var1.locX);
			var6.motY += 0.06D;
			var6.motZ += 0.2D / (var6.locZ - var1.locZ);
		}
	}

	private void doWatchCheck(List<EntityHuman> var1, World var2) {
		int var3 = 0;
		Iterator<EntityHuman> var4 = var1.iterator();

		while (var4.hasNext()) {
			EntityHuman var5 = var4.next();
			ItemStack[] var6 = EEBase.quickBar(var5);
			int var7 = var6.length;

			for (int var8 = 0; var8 < var7; ++var8) {
				ItemStack var9 = var6[var8];

				if (var9 == null || !(var9.getItem() instanceof ItemWatchOfTime) || (var9.getData() & 0x1) != 1 || EEBase.getPlayerEffect(var9.getItem(), var5) <= 0) continue;
				var3 += ((ItemEECharged) var9.getItem()).chargeLevel(var9) + 1;
				EEBase.playerWatchMagnitude.put(var5, ((ItemEECharged) var9.getItem()).chargeLevel(var9) + 1);
			}

		}

		EEBase.playerWoftFactor = var3;
	}

	private void doFlightCheck(List<EntityHuman> var1, World var2) {
		Iterator<EntityHuman> var3 = var1.iterator();

		while (var3.hasNext()) {
			EntityHuman var4 = var3.next();

			if (((EntityPlayer) var4).itemInWorldManager.isCreative()) {
				return;
			}

			boolean arcana = false;
			boolean volcaniteLava = false;
			boolean evertideWater = false;
			boolean swrg = false;
			ItemStack[] var9 = EEBase.quickBar(var4);
			int var10 = var9.length;

			for (int var11 = 0; var11 < var10; ++var11) {
				ItemStack var12 = var9[var11];

				if (var12 == null || !EEMaps.isFlyingItem(var12.id)) continue;
				Item item = var12.getItem();
				if (item == EEItem.arcaneRing) {
					arcana = true;
					var4.abilities.canFly = true;
				} else if (item == EEItem.evertide && EEBase.isPlayerInWater(var4)) {
					evertideWater = true;
					var4.abilities.isFlying = true;
				} else if (item == EEItem.volcanite && EEBase.isPlayerInLava(var4)) {
					volcaniteLava = true;
					var4.abilities.isFlying = true;
				} else if (item == EEItem.swiftWolfRing){
					swrg = true;
					var4.abilities.canFly = true;
				}

			}

			if (swrg && (evertideWater || volcaniteLava || arcana)) {
				swrg = false;
				disableSWRG(var4);
			} else if (swrg && !volcaniteLava && !evertideWater && !arcana) {
				if (var4.abilities.isFlying) {
					forceEnableSWRG(var4);
				}

				if (!var4.abilities.isFlying) {
					disableSWRG(var4);
				}
			}

			if (!swrg && !volcaniteLava && !evertideWater && !arcana) {
				var4.abilities.canFly = false;
				var4.abilities.isFlying = false;
				var4.updateAbilities();
			} else {
				var4.abilities.canFly = true;
			}
		}
	}

	private void forceEnableSWRG(EntityHuman var1) {
		ItemStack[] var2 = EEBase.quickBar(var1);
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			ItemStack var5 = var2[var4];

			if (var5 == null || var5.getItem() != EEItem.swiftWolfRing) continue;
			if (!((ItemEECharged) var5.getItem()).isActivated(var5)) {
				((ItemEECharged) var5.getItem()).doToggle(var5, var1.world, var1);
				return;
			}

			return;
		}
	}

	private void disableSWRG(EntityHuman var1) {
		ItemStack[] var2 = EEBase.quickBar(var1);
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			ItemStack var5 = var2[var4];
			Item item;
			if (var5 == null || (item = var5.getItem()) != EEItem.swiftWolfRing) continue;
			if (var1.abilities.isFlying) {
				if (!((ItemEECharged) item).isActivated(var5)) continue;
				if (EEBase.getPlayerEffect(item, var1) <= 0) {
					((ItemEECharged) item).ConsumeReagent(var5, var1, false);
				}

				if (EEBase.getPlayerEffect(item, var1) > 0) continue;
				((ItemEECharged) item).doToggle(var5, var1.world, var1);
			} else {
				if (!((ItemEECharged) item).isActivated(var5)) continue;
				((ItemEECharged) item).doToggle(var5, var1.world, var1);
			}
		}
	}

	private void doEquipCheck(EntityHuman var1, World var2) {
		ItemStack[] var3 = EEBase.quickBar(var1);
		int var4 = var3.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			ItemStack var6 = var3[var5];
			Item item;
			if (var6 == null || !((item = var6.getItem()) instanceof ItemEECharged)) continue;
			if (var6 == var1.U()) {
				((ItemEECharged) item).doHeld(var6, var2, var1);
			}

			((ItemEECharged) item).doPassive(var6, var2, var1);

			if ((var6.getData() & 0x1) == 1 && EEMaps.hasDurationEffect(item)) {
				if (item instanceof ItemWatchOfTime) {
					if (EEBase.getPlayerEffect(item, var1) > 0) {
						EEBase.updatePlayerEffect(item, EEBase.getPlayerEffect(item, var1) - (((ItemEECharged) item).chargeLevel(var6) + 1) * (((ItemEECharged) item).chargeLevel(var6) + 1), var1);
					}
				} else if (EEBase.getPlayerEffect(item, var1) > 0) {
					EEBase.updatePlayerEffect(item, EEBase.getPlayerEffect(item, var1) - 1, var1);
				}

				if (EEBase.getPlayerEffect(item, var1) <= 0) {
					((ItemEECharged) item).ConsumeReagent(var6, var1, false);
				}

				if (EEBase.getPlayerEffect(item, var1) <= 0) {
					((ItemEECharged) item).doToggle(var6, var2, var1);
				} else {
					((ItemEECharged) item).doActive(var6, var2, var1);
				}
			}

			if ((var6.getData() & 0x2) != 2 || !(item instanceof ItemSwiftWolfRing)) continue;
			if (EEBase.getPlayerEffect(item, var1) > 0) {
				EEBase.updatePlayerEffect(item, EEBase.getPlayerEffect(item, var1) - 2, var1);
			}

			if (EEBase.getPlayerEffect(item, var1) <= 0) {
				((ItemEECharged) item).ConsumeReagent(var6, var1, false);
			}

			if (EEBase.getPlayerEffect(item, var1) <= 0) {
				((ItemEECharged) item).doToggle(var6, var2, var1);
			} else {
				((ItemEECharged) item).doActive(var6, var2, var1);
			}
		}
	}

	private void doFireImmuneCheck(EntityHuman var1) {
		boolean var2 = false;

		for (int var3 = 0; var3 < 9; ++var3) {
			if (var1.inventory.items[var3] == null || !EEMaps.isFireImmuneItem(var1.inventory.items[var3].id)) continue;
			var2 = true;
		}

		for (int var3 = 0; var3 < 4; ++var3) {
			if (var1.inventory.armor[var3] == null || !EEMaps.isFireImmuneArmor(var1.inventory.armor[var3].id)) continue;
			var2 = true;
		}

		var1.fireProof = var2;
	}

	public boolean clientSideRequired() {
		return true;
	}

	public boolean serverSideRequired() {
		return false;
	}

	public static BaseMod getInstance() {
		return instance;
	}
}

/* Location:           C:\Program Files\eclipse_Kepler\lib\Tekkit_Classic\mods\EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
 * Qualified Name:     net.minecraft.server.mod_EE
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
