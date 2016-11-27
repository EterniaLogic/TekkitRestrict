package ee;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityExperienceOrb;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntityWeatherLighting;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.Packet;
import net.minecraft.server.mod_EE;
import ee.core.GuiIds;
import ee.events.EEEnums.EEPedestalAction;
import ee.events.EEEventManager;
import ee.events.blocks.EEPedestalEvent;
import ee.network.EEPacket;
import ee.network.PacketHandler;
import ee.network.PacketTypeHandler;

public class TilePedestal extends TileEE implements IInventory {
	private ItemStack[] items = new ItemStack[1];
	@SuppressWarnings("unused")
	private int activeItem = 0;
	private boolean interdictionActive;
	private boolean evertideActive;
	private boolean grimarchActive;
	private boolean harvestActive;
	private boolean ignitionActive;
	private boolean zeroActive;
	private boolean repairActive;
	private boolean soulstoneActive;
	private boolean swiftwolfActive;
	private boolean volcaniteActive;
	private boolean watchActive;
	private boolean updateBlock;
	public int activationCooldown;
	private boolean activated;
	private EntityHuman activationPlayer;
	private boolean initActivation;
	private boolean initDeactivation;
	private int grimarchCounter = 0;
	private int repairTimer = 0;
	private int healingTimer;
	private boolean attractionActive;

	public String getName() {
		return "Pedestal";
	}

	public void a(NBTTagCompound var1) {
		super.a(var1);
		NBTTagList var2 = var1.getList("Items");
		items = new ItemStack[getSize()];

		for (int var3 = 0; var3 < var2.size(); var3++) {
			NBTTagCompound var4 = (NBTTagCompound) var2.get(var3);
			int var5 = var4.getByte("Slot") & 0xFF;

			if (var5 >= 0 && var5 < items.length) {
				items[var5] = ItemStack.a(var4);
			}
		}

		interdictionActive = var1.getBoolean("interdictionActive");
		attractionActive = var1.getBoolean("attractionActive");
		evertideActive = var1.getBoolean("evertideActive");
		grimarchActive = var1.getBoolean("grimarchActive");
		harvestActive = var1.getBoolean("harvestActive");
		ignitionActive = var1.getBoolean("ignitionActive");
		zeroActive = var1.getBoolean("zeroActive");
		repairActive = var1.getBoolean("repairActive");
		soulstoneActive = var1.getBoolean("soulstoneActive");
		swiftwolfActive = var1.getBoolean("swiftwolfActive");
		volcaniteActive = var1.getBoolean("volcaniteActive");
		watchActive = var1.getBoolean("watchActive");
		activated = var1.getBoolean("activated");

		if (!volcaniteActive && !evertideActive && activated) {
			initActivation = true;
		}
	}

	public void b(NBTTagCompound var1) {
		super.b(var1);
		var1.setBoolean("watchActive", watchActive);
		var1.setBoolean("volcaniteActive", volcaniteActive);
		var1.setBoolean("swiftwolfActive", swiftwolfActive);
		var1.setBoolean("soulstoneActive", soulstoneActive);
		var1.setBoolean("repairActive", repairActive);
		var1.setBoolean("ignitionActive", ignitionActive);
		var1.setBoolean("zeroActive", zeroActive);
		var1.setBoolean("harvestActive", harvestActive);
		var1.setBoolean("grimarchActive", grimarchActive);
		var1.setBoolean("evertideActive", evertideActive);
		var1.setBoolean("interdictionActive", interdictionActive);
		var1.setBoolean("attractionActive", attractionActive);
		var1.setBoolean("activated", activated);
		NBTTagList var2 = new NBTTagList();

		for (byte var3 = 0; var3 < items.length; var3 = (byte) (var3 + 1)) {
			if (items[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", var3);
				items[var3].save(var4);
				var2.add(var4);
			}
		}

		var1.set("Items", var2);
	}

	private boolean a, b, c, d, e, f, g, h, i, j, k, l;
	private int counter;
	public void update() {
		super.update();

		if (items[0] == null) {
			resetAll();
		} else {
			int var1 = items[0].id;
			int var2 = items[0].getData();

			if (EEMaps.isPedestalItem(var1)) {
				if (activationPlayer == null){
					return;
				}
				counter++;
				if (var1 == EEBlock.eeTorch.id && var2 == 0) {
					resetAll();
					if (counter > 50){
						a = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.Interdict));
						counter = 0;
					}
					if (a){
						interdictionActive = false;
					} else {
						interdictionActive = true;
					}
					updateBlock = true;
					
				} else if (var1 == EEItem.evertide.id) {
					resetAll();
					if (counter > 50){
						b = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.Storm));
						counter = 0;
					}
					if (b){
						evertideActive = false;
					} else {
						evertideActive = true;
					}
				} else if (var1 == EEItem.grimarchRing.id) {
					resetAll();
					if (counter > 50){
						c = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.ShootArrow));
						counter = 0;
					}
					if (c){
						grimarchActive = false;
					} else {
						grimarchActive = true;
					}
				} else if (var1 == EEItem.harvestRing.id) {
					resetAll();
					if (counter > 50){
						d = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.Harvest));
						counter = 0;
					}
					if (d){
						harvestActive = false;
					} else {
						harvestActive = true;
					}
				} else if (var1 == EEItem.zeroRing.id) {
					resetAll();
					if (counter > 100){
						e = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.None));
						counter = 0;
					}
					if (e){
						zeroActive = false;
					} else {
						zeroActive = true;
					}
				} else if (var1 == EEItem.ignitionRing.id) {
					resetAll();
					if (counter>50){
						f = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.Ignition));
						counter = 0;
					}
					if (f){
						ignitionActive = false;
					} else {
						ignitionActive = true;
					}
				} else if (var1 == EEItem.repairCharm.id) {
					resetAll();
					if (counter>50){
						g = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.Repair));
						counter = 0;
					}
					if (g){
						repairActive = false;
					} else {
						repairActive = true;
					}
				} else if (var1 == EEItem.soulStone.id) {
					resetAll();
					if (counter>50){
						h = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.Heal));
						counter = 0;
					}
					if (h){
						soulstoneActive = false;
					} else {
						soulstoneActive = true;
					}
				} else if (var1 == EEItem.swiftWolfRing.id) {
					resetAll();
					if (counter>50){
						i = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.StrikeLightning));
						counter = 0;
					}
					if (i){
						swiftwolfActive = false;
					} else {
						swiftwolfActive = true;
					}
				} else if (var1 == EEItem.volcanite.id) {
					resetAll();
					if (counter>50){
						j = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.StopStorm));
						counter = 0;
					}
					if (j){
						volcaniteActive = false;
					} else {
						volcaniteActive = true;
					}
				} else if (var1 == EEItem.watchOfTime.id) {
					resetAll();
					if (counter>50){
						k = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.Time));
						counter = 0;
					}
					if (k){
						watchActive = false;
					} else {
						watchActive = true;
					}
				} else if (var1 == EEItem.attractionRing.id) {
					resetAll();
					if (counter>50){
						l = EEEventManager.callEvent(new EEPedestalEvent(this, items[0], activationPlayer, EEPedestalAction.Attract));
						counter = 0;
					}
					if (l){
						attractionActive = false;
					} else {
						attractionActive = true;
					}
				} else {
					updateBlock = true;
					resetAll();
				}
			} else {
				EntityItem var3 = new EntityItem(world, x, y, z, items[0].cloneItemStack());
				var3.pickupDelay = 10;
				world.addEntity(var3);
				items[0] = null;
				updateBlock = true;
				resetAll();
			}
		}
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean var1) {
		activated = var1;
	}

	private void resetAll() {
		activated = false;
		initActivation = false;
		initDeactivation = false;
		attractionActive = false;
		interdictionActive = false;
		evertideActive = false;
		grimarchActive = false;
		harvestActive = false;
		ignitionActive = false;
		repairActive = false;
		soulstoneActive = false;
		swiftwolfActive = false;
		volcaniteActive = false;
		watchActive = false;
		resetTimeFactor();
	}

	public void q_() {
		if (!clientFail()) {
			if (activated) {
				if (world.random.nextInt(5) == 0) {
					for (int var1 = 0; var1 < 1; var1++) {
						// double var10000 = this.x + this.world.random.nextFloat();
						double var4 = y + world.random.nextFloat();
						// var10000 = this.z + this.world.random.nextFloat();
						double var8 = 0.0D;
						double var10 = 0.0D;
						double var12 = 0.0D;
						int var14 = world.random.nextInt(2) * 2 - 1;
						var8 = (world.random.nextFloat() - 0.5D) * 0.5D;
						var10 = (world.random.nextFloat() - 0.5D) * 0.5D;
						var12 = (world.random.nextFloat() - 0.5D) * 0.5D;
						double var6;
						if (world.random.nextInt(2) == 0) {
							var6 = z + 0.5D + 0.25D * var14;
							var12 = world.random.nextFloat() * 2.0F * var14;
						} else {
							var6 = z + 0.5D + 0.25D * var14 * -1.0D;
							var12 = world.random.nextFloat() * 2.0F * var14 * -1.0F;
						}
						double var2;
						if (world.random.nextInt(2) == 0) {
							var2 = x + 0.5D + 0.25D * var14;
							var8 = world.random.nextFloat() * 2.0F * var14;
						} else {
							var2 = x + 0.5D + 0.25D * var14 * -1.0D;
							var8 = world.random.nextFloat() * 2.0F * var14 * -1.0F;
						}

						world.a("portal", var2, var4, var6, var8, var10, var12);
					}
				}

				for (int var1 = 0; var1 < 1; var1++) {
					float var15 = x + 0.45F + world.random.nextFloat() / 16.0F;
					float var3 = y + 0.3F + world.random.nextFloat() / 16.0F;
					float var16 = z + 0.45F + world.random.nextFloat() / 16.0F;
					float var5 = 0.2F;

					if (world.random.nextInt(8) == 0) world.a("flame", var15 - var5, var3, var16 - var5, 0.0D, 0.0D, 0.0D);
					if (world.random.nextInt(8) == 0) world.a("flame", var15 - var5, var3, var16, 0.0D, 0.0D, 0.0D);
					if (world.random.nextInt(8) == 0) world.a("flame", var15 - var5, var3, var16 + var5, 0.0D, 0.0D, 0.0D);
					if (world.random.nextInt(8) == 0) world.a("flame", var15, var3, var16 - var5, 0.0D, 0.0D, 0.0D);
					if (world.random.nextInt(8) == 0) world.a("flame", var15 + var5, var3, var16 - var5, 0.0D, 0.0D, 0.0D);
					if (world.random.nextInt(8) == 0) world.a("flame", var15 + var5, var3, var16, 0.0D, 0.0D, 0.0D);
					if (world.random.nextInt(8) == 0) world.a("flame", var15 + var5, var3, var16 + var5, 0.0D, 0.0D, 0.0D);
					if (world.random.nextInt(8) == 0) world.a("flame", var15, var3, var16 + var5, 0.0D, 0.0D, 0.0D);
				}
			}

			if (attractionActive) doAttraction(x, y, z);
			else if (interdictionActive) doInterdiction(x, y, z);
			else if (evertideActive) doEvertide();
			else if (grimarchActive) doGrimarch();
			else if (harvestActive) doHarvest();
			else if (repairActive) doRepair();
			else if (ignitionActive) doIgnition();
			else if (soulstoneActive) doSoulstone();
			else if (swiftwolfActive) doSwiftwolf();
			else if (volcaniteActive) doVolcanite();
			else if (watchActive) doWatch();

			if (updateBlock) {
				world.notify(x, y, z);
			}

			if (activationCooldown > 0) {
				activationCooldown--;
			}
		}
	}

	private void doAttraction(int var1, int var2, int var3) {
		if (initActivation) {
			initActivation = false;
			activationCooldown = 20;
		}

		if (initDeactivation) {
			initDeactivation = false;
			activationCooldown = 20;
		}

		if (activated) {
			List<Entity> var4 = world.getEntities(world.a(player), AxisAlignedBB.b(var1 - 10, var2 - 10, var3 - 10, var1 + 10, var2 + 10, var3 + 10));
			Iterator<Entity> var6 = var4.iterator();

			while (var6.hasNext()) {
				Entity var5 = var6.next();
				pullEntities(var5, var1, var2, var3);
			}

			List<Entity> var17 = world.a(EntityLootBall.class, AxisAlignedBB.b(var1 - 10, var2 - 10, var3 - 10, var1 + 10, var2 + 10, var3 + 10));
			Iterator<Entity> var8 = var17.iterator();

			while (var8.hasNext()) {
				Entity var7 = var8.next();
				PullItems(var7);
			}

			List<Entity> var18 = world.a(EntityLootBall.class, AxisAlignedBB.b(var1 - 10, var2 - 10, var3 - 10, var1 + 10, var2 + 10, var3 + 10));
			Iterator<Entity> var10 = var18.iterator();

			while (var10.hasNext()) {
				Entity var9 = var10.next();
				PullItems(var9);
			}

			List<Entity> var19 = world.a(EntityItem.class, AxisAlignedBB.b(var1 - 10, var2 - 10, var3 - 10, var1 + 10, var2 + 10, var3 + 10));
			Iterator<Entity> var12 = var19.iterator();

			while (var12.hasNext()) {
				Entity var11 = var12.next();
				PullItems(var11);
			}

			List<Entity> var20 = world.a(EntityLootBall.class, AxisAlignedBB.b(var1 - 0.5D, var2 - 0.5D, var3 - 0.5D, var1 + 1.25D, var2 + 1.25D, var3 + 1.25D));
			Iterator<Entity> var14 = var20.iterator();

			while (var14.hasNext()) {
				Entity var13 = var14.next();
				GrabItems(var13);
			}

			List<Entity> var21 = world.a(EntityItem.class, AxisAlignedBB.b(var1 - 0.5D, var2 - 0.5D, var3 - 0.5D, var1 + 1.25D, var2 + 1.25D, var3 + 1.25D));
			Iterator<Entity> var16 = var21.iterator();

			while (var16.hasNext()) {
				Entity var15 = var16.next();
				GrabItems(var15);
			}
		}
	}
	
	public boolean PushStack(ItemStack var1) {
		return var1 == null ? false : tryDropInChest(var1);
	}

	public boolean PushStack(EntityItem var1) {
		if (var1 == null) {
			return false;
		}
		if (var1.itemStack == null) {
			var1.die();
			return false;
		}
		if (var1.itemStack.count < 1) {
			var1.die();
			return false;
		}

		ItemStack var2 = var1.itemStack.cloneItemStack();

		for (var2.count = 1; var1.itemStack.count > 0 && tryDropInChest(var2.cloneItemStack()); var1.itemStack.count--);
		return var1.itemStack.count <= 0;
	}

	private void PushDenseStacks(EntityLootBall var1) {
		for (int var2 = 0; var2 < var1.items.length; var2++) {
			if (var1.items[var2] != null && tryDropInChest(var1.items[var2])) {
				var1.items[var2] = null;
			}
		}
	}

	private void GrabItems(Entity var1) {
		if (var1 == null || var1.dead) return; 
		
		if (var1 instanceof EntityItem) {
			if (((EntityItem) var1).itemStack == null) {
				var1.die();
			}

			if (PushStack((EntityItem) var1)) {
				var1.die();
			}
		} else if (var1 instanceof EntityLootBall) {
			if (((EntityLootBall) var1).items == null) {
				var1.die();
			}

			// ItemStack[] var2 = ((EntityLootBall)var1).items;
			PushDenseStacks((EntityLootBall) var1);

			if (((EntityLootBall) var1).isEmpty()) {
				var1.die();
			}
		}
	}

	private void PullItems(Entity var1) {
		if (var1 instanceof EntityItem || var1 instanceof EntityLootBall) {
			if (var1 instanceof EntityLootBall) {
				((EntityLootBall) var1).setBeingPulled(true);
			}

			double var3 = x + 0.5D - var1.locX;
			double var5 = y + 0.5D - var1.locY;
			double var7 = z + 0.5D - var1.locZ;
			double var9 = var3 * var3 + var5 * var5 + var7 * var7;
			var9 *= var9;

			if (var9 <= 1296d) {
				double var11 = var3 * 0.02D / var9 * 216d;
				double var13 = var5 * 0.02D / var9 * 216d;
				double var15 = var7 * 0.02D / var9 * 216d;

				if (var11 > 0.1D) {
					var11 = 0.1D;
				} else if (var11 < -0.1D) {
					var11 = -0.1D;
				}

				if (var13 > 0.1D) {
					var13 = 0.1D;
				} else if (var13 < -0.1D) {
					var13 = -0.1D;
				}

				if (var15 > 0.1D) {
					var15 = 0.1D;
				} else if (var15 < -0.1D) {
					var15 = -0.1D;
				}

				var1.motX += var11 * 1.2D;
				var1.motY += var13 * 1.2D;
				var1.motZ += var15 * 1.2D;
			}
		}
	}

	private void pullEntities(Entity var1, int var2, int var3, int var4) {
		if (!(var1 instanceof EntityHuman)) {
			if (var1 instanceof EntityLiving) {
				double var5 = var2 + 0.5F - var1.locX;
				double var7 = var3 + 0.5F - var1.locY;
				double var9 = var4 + 0.5F - var1.locZ;
				double var11 = var5 * var5 + var7 * var7 + var9 * var9;
				var11 *= var11;

				if (var11 <= 1296d) {
					double var13 = var5 * 0.02D / var11 * 216d;
					double var15 = var7 * 0.02D / var11 * 216d;
					double var17 = var9 * 0.02D / var11 * 216d;

					if (var13 > 0.1D) {
						var13 = 0.1D;
					} else if (var13 < -0.1D) {
						var13 = -0.1D;
					}

					if (var15 > 0.1D) {
						var15 = 0.1D;
					} else if (var15 < -0.1D) {
						var15 = -0.1D;
					}

					if (var17 > 0.1D) {
						var17 = 0.1D;
					} else if (var17 < -0.1D) {
						var17 = -0.1D;
					}

					if (var1 instanceof EntityItem) {
						var1.motX += var13 * 1.8D;
						var1.motY += var15 * 2.8D;
						var1.motZ += var17 * 1.8D;
					} else {
						var1.motX += var13 * 1.4D;
						var1.motY += var15 * 1.2D;
						var1.motZ += var17 * 1.4D;
					}
				}
			}
		}
	}

	private void doWatch() {
		if (initActivation) {
			setTimeFactor();
			initActivation = false;
			activationCooldown = 20;
		}

		if (initDeactivation) {
			resetTimeFactor();
			initDeactivation = false;
			activationCooldown = 20;
		}

		if (activated) {
			List<Entity> var1 = world.getEntities(world.a(player), AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
			Iterator<Entity> var3 = var1.iterator();

			while (var3.hasNext()) {
				Entity var2 = var3.next();
				slowEntities(var2);
			}
		}
	}

	private void doVolcanite() {
		if (activated && initActivation) {
			initActivation = false;
			activationCooldown = 60;

			if (EEProxy.getWorldInfo(world).isThundering()) {
				EEProxy.getWorldInfo(world).setThundering(false);
				EEProxy.getWorldInfo(world).setThunderDuration(0);
			}

			if (EEProxy.getWorldInfo(world).hasStorm()) {
				EEProxy.getWorldInfo(world).setStorm(false);
				EEProxy.getWorldInfo(world).setWeatherDuration(0);
			}
		}

		if (activationCooldown == 0 || initDeactivation) {
			activated = false;
			initDeactivation = false;
		}
	}

	private void doSwiftwolf() {
		if (initActivation) {
			activationCooldown = 20;
			initActivation = false;
		}

		if (initDeactivation) {
			initDeactivation = false;
		}

		if (activated) {
			if (world.random.nextInt(1000 / ((world.x() ? 2 : 1) * (world.w() ? 2 : 1))) == 0) {
				int var1 = 0;
				int var2 = 0;
				int var3 = 0;
				int var4 = 0;

				for (int var5 = -5; var5 <= 5; var5++) {
					for (int var6 = -5; var6 <= 5; var6++) {
						for (int var7 = 127; var7 >= 0; var7--) {
							var1 = world.getTypeId(x + var5, var7, z + var6);

							if (var1 != 0) {
								var3 = var7;
								break;
							}
						}

						if (var1 != 0) {
							var4 = z + var6;
							break;
						}
					}

					if (var1 != 0) {
						var2 = z + var5;
						break;
					}
				}

				world.strikeLightning(new EntityWeatherLighting(world, var2, var3, var4));
			}

			List<Entity> var8 = world.a(EntityMonster.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));

			for (int var2 = 0; var2 < var8.size(); var2++) {
				if (world.random.nextInt(60) == 0) {
					Entity var9 = var8.get(var2);

					if (var9 != null) {
						if (world.isChunkLoaded((int) var9.locX, (int) var9.locY, (int) var9.locZ)) {
							if (EEProxy.getWorldInfo(world).isThundering()) {
								world.strikeLightning(new EntityWeatherLighting(world, var9.locX, var9.locY, var9.locZ));

								for (int var4 = 0; var4 <= world.random.nextInt(3); var4++) {
									world.strikeLightning(new EntityWeatherLighting(world, var9.locX, var9.locY, var9.locZ));
								}
							} else if (EEProxy.getWorldInfo(world).hasStorm()) {
								world.strikeLightning(new EntityWeatherLighting(world, var9.locX, var9.locY, var9.locZ));

								for (int var4 = 0; var4 <= world.random.nextInt(2); var4++) {
									world.strikeLightning(new EntityWeatherLighting(world, var9.locX, var9.locY, var9.locZ));
								}
							} else {
								world.strikeLightning(new EntityWeatherLighting(world, var9.locX, var9.locY, var9.locZ));
							}
						} else {
							world.strikeLightning(new EntityWeatherLighting(world, var9.locX, var9.locY, var9.locZ));
						}
					}
				}
			}
		}
	}

	private void doSoulstone() {
		if (initActivation) {
			activationCooldown = 20;
			healingTimer = 20;
			initActivation = false;
		}

		if (initDeactivation) {
			initDeactivation = false;
		}

		if (activated) {
			if (healingTimer <= 0) {
				healingTimer = 20;
				List<Entity> var1 = world.a(EntityHuman.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
				Iterator<Entity> var3 = var1.iterator();

				while (var3.hasNext()) {
					Entity var2 = var3.next();
					HealForPlayer(var2);
				}
			}

			if (healingTimer > 0) {
				healingTimer--;
			}
		}
	}

	private Object HealForPlayer(Entity var1) {
		if (var1 instanceof EntityHuman) {
			EntityHuman var2 = (EntityHuman) var1;

			if (EEProxy.getEntityHealth(var2) < 20) {
				world.makeSound(var1, "heal", 0.8F, 1.5F);
				((EntityHuman) var1).heal(1);
			}
		}

		return null;
	}

	private void doIgnition() {
		if (initActivation) {
			activationCooldown = 20;
			initActivation = false;
		}

		if (initDeactivation) {
			initDeactivation = false;
		}

		if (activated) {
			List<Entity> var1 = world.a(EntityMonster.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));

			for (int var2 = 0; var2 < var1.size(); var2++) {
				if (world.random.nextInt(5) == 0) {
					Entity var3 = var1.get(var2);
					EEProxy.dealFireDamage(var3, 3);
					var3.setOnFire(40);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void doZero() {
		if (initActivation) {
			activationCooldown = 20;
			initActivation = false;
		}

		if (initDeactivation) {
			initDeactivation = false;
		}

		if (activated) {
			doFreezeOverTime();
		}
	}
	
	public void doFreezeOverTime() {
		List<Entity> var1 = world.a(EntityMonster.class, AxisAlignedBB.b(x - 5, y - 5, z - 5, x + 5, y + 5, z + 5));

		for (int var2 = 0; var2 < var1.size(); var2++) {
			Entity var3 = var1.get(var2);

			if (var3.motX > 0.0D || var3.motZ > 0.0D) {
				var3.motX *= 0.2D;
				var3.motZ *= 0.2D;
			}
		}

		for (int var2 = -4; var2 <= 4; var2++) {
			for (int var6 = -4; var6 <= 4; var6++) {
				for (int var4 = -4; var4 <= 4; var4++) {
					if (var2 <= -2 && var2 >= 2 || var6 != 0 && (var4 <= -2 && var4 >= 2 || var6 != 0)) {
						if (world.random.nextInt(20) == 0) {
							int var5 = world.getTypeId(x + var2, y + var6 - 1, z + var4);

							if (var5 != 0 && Block.byId[var5].a() && world.getMaterial(x + var2, y + var6 - 1, z + var4).isBuildable()
									&& world.getTypeId(x + var2, y + var6, z + var4) == 0 && EEPatch.attemptPlace(activationPlayer, x + var2, y + var6, z + var4)) {
								world.setTypeId(x + var2, y + var6, z + var4, Block.SNOW.id);
							}
						}

						if (world.random.nextInt(3) == 0 && world.getMaterial(x + var2, y + var6, z + var4) == Material.WATER
								&& world.getTypeId(x + var2, y + var6 + 1, z + var4) == 0  && EEPatch.attemptBreak(activationPlayer, x + var2, y + var6, z + var4)) {
							world.setTypeId(x + var2, y + var6, z + var4, Block.ICE.id);
						}

						if (world.random.nextInt(3) == 0 && world.getMaterial(x + var2, y + var6, z + var4) == Material.LAVA
								&& world.getTypeId(x + var2, y + var6 + 1, z + var4) == 0 && world.getData(x + var2, y + var6, z + var4) == 0  && EEPatch.attemptBreak(activationPlayer, x + var2, y + var6, z + var4)) {
							world.setTypeId(x + var2, y + var6, z + var4, Block.OBSIDIAN.id);
						}
					}
				}
			}
		}
	}

	private void doRepair() {
		if (initActivation) {
			activationCooldown = 20;
			initActivation = false;
		}

		if (initDeactivation) {
			initDeactivation = false;
		}

		if (activated) {
			List<Entity> var1 = world.a(EntityHuman.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
			Iterator<Entity> var3 = var1.iterator();

			while (var3.hasNext()) {
				Entity var2 = var3.next();
				RepairForPlayer(var2);
			}
		}
	}

	private void RepairForPlayer(Entity var1) {
		if (var1 instanceof EntityHuman) {
			if (repairTimer >= 3)// 2
			{
				repairTimer = 0;
				ItemStack[] var2 = new ItemStack[((EntityHuman) var1).inventory.items.length];
				// ItemStack[] var3 = new ItemStack[((EntityHuman)var1).inventory.armor.length];
				var2 = ((EntityHuman) var1).inventory.items;
				// var3 = ((EntityHuman)var1).inventory.armor;
				ItemStack var4 = null;
				boolean var5 = false;

				for (int var6 = 0; var6 < var2.length; var6++) {
					var5 = false;
					var4 = var2[var6];

					if (var4 != null) {
						for (int var7 = 0; var7 < EEMaps.chargedItems.size(); var7++) {
							if (EEMaps.chargedItems.get(var7).intValue() == var4.id) {
								var5 = true;
								break;
							}
						}

						if (!var5 && var4.getData() >= 1 && var4.d()) {
							var4.setData(var4.getData() - 1);
						}
					}
				}

				// for (int i = 0; i < var3.length; i++){

				// }
			}

			repairTimer++;
		}
	}

	private void doHarvest() {
		if (initActivation) {
			initActivation = false;
			activationCooldown = 20;
		}

		if (initDeactivation) {
			initDeactivation = false;
		}

		if (activated) {
			doPassiveHarvest();
			doActiveHarvest();
		}
	}

	public void doPassiveHarvest() {
		int var1 = x;
		int var2 = y;
		int var3 = z;

		for (int var4 = -5; var4 <= 5; var4++) {
			for (int var5 = -5; var5 <= 5; var5++) {
				for (int var6 = -5; var6 <= 5; var6++) {
					int var7 = world.getTypeId(var1 + var4, var2 + var5, var3 + var6);

					if (var7 == Block.CROPS.id) {
						int var8 = world.getData(var1 + var4, var2 + var5, var3 + var6);

						if (var8 < 7 && world.random.nextInt(600) == 0) {
							var8++;
							world.setData(var1 + var4, var2 + var5, var3 + var6, var8);
						}
					} else if (var7 != Block.YELLOW_FLOWER.id && var7 != Block.RED_ROSE.id && var7 != Block.BROWN_MUSHROOM.id && var7 != Block.RED_MUSHROOM.id) {
						if (var7 == Block.GRASS.id && world.getTypeId(var1 + var4, var2 + var5 + 1, var3 + var6) == 0 && world.random.nextInt(4000) == 0) {
							world.setTypeId(var1 + var4, var2 + var5 + 1, var3 + var6, Block.LONG_GRASS.id);
							world.setData(var1 + var4, var2 + var5 + 1, var3 + var6, 1);
						}

						if (var7 == Block.DIRT.id && world.getTypeId(var1 + var4, var2 + var5 + 1, var3 + var6) == 0 && world.random.nextInt(800) == 0) {
							if (EEPatch.attemptBreak(activationPlayer, var5, var6, var7))
								world.setTypeId(var1 + var4, var2 + var5, var3 + var6, Block.GRASS.id);
						} else if ((var7 == Block.SUGAR_CANE_BLOCK.id || var7 == Block.CACTUS.id)
								&& world.getTypeId(var1 + var4, var2 + var5 + 1, var3 + var6) == 0
								&& world.getTypeId(var1 + var4, var2 + var5 - 4, var3 + var6) != Block.SUGAR_CANE_BLOCK.id
								&& world.getTypeId(var1 + var4, var2 + var5 - 4, var3 + var6) != Block.CACTUS.id && world.random.nextInt(600) == 0) {
							if (EEPatch.attemptBreak(activationPlayer, var5, var6, var7))
								world.setTypeId(var1 + var4, var2 + var5 + 1, var3 + var6, var7);
							world.a("largesmoke", var1 + var4, var2 + var5, var3 + var6, 0.0D, 0.05D, 0.0D);
						}
					} else if (world.random.nextInt(2) == 0) {
						for (int var8 = -1; var8 < 0; var8++) {
							if (world.getTypeId(var1 + var4 + var8, var2 + var5, var3 + var6) == 0
									&& world.getTypeId(var1 + var4 + var8, var2 + var5 - 1, var3 + var6) == Block.GRASS.id) {
								if (world.random.nextInt(800) == 0) {
									world.setTypeId(var1 + var4 + var8, var2 + var5, var3 + var6, var7);
									world.a("largesmoke", var1 + var4 + var8, var2 + var5, var3 + var6, 0.0D, 0.05D, 0.0D);
								}
							} else if (world.getTypeId(var1 + var4, var2 + var5, var3 + var6 + var8) == 0
									&& world.getTypeId(var1 + var4, var2 + var5 - 1, var3 + var6 + var8) == Block.GRASS.id && world.random.nextInt(1800) == 0) {
								world.setTypeId(var1 + var4, var2 + var5, var3 + var6 + var8, var7);
								world.a("largesmoke", var1 + var4, var2 + var5, var3 + var6 + var8, 0.0D, 0.05D, 0.0D);
							}
						}
					}
				}
			}
		}
	}

	public void doActiveHarvest() {
		int var1 = x;
		int var2 = y;
		int var3 = z;

		for (int var4 = -5; var4 <= 5; var4++) {
			for (int var5 = -5; var5 <= 5; var5++) {
				for (int var6 = -5; var6 <= 5; var6++) {
					int nx = var1 + var4;
					int ny = var2 + var5;
					int nz = var3 + var6;
					int id = world.getTypeId(nx, ny, nz);
					int data = world.getData(nx, ny, nz);

					if (id == Block.CROPS.id) {
						int var8 = data;
						
						if (EEPatch.attemptBreak(activationPlayer, nx, ny, nz)){
							if (var8 >= 7) {
								Block.byId[id].dropNaturally(world, nx, ny, nz, data, 0.05F, 1);
								Block.byId[id].dropNaturally(world, nx, ny, nz, data, 1.0F, 1);
								world.setTypeId(nx, ny, nz, 0);
								world.a("largesmoke", nx, ny, nz, 0.0D, 0.05D, 0.0D);
							} else if (world.random.nextInt(400) == 0) {
								var8++;
								world.setData(nx, ny, nz, var8);
							}
						}
					} else if (id != Block.YELLOW_FLOWER.id && id != Block.RED_ROSE.id && id != Block.BROWN_MUSHROOM.id && id != Block.RED_MUSHROOM.id
							&& id != Block.LONG_GRASS.id) {
						int sc = Block.SUGAR_CANE_BLOCK.id;
						int id2 = world.getTypeId(nx, ny - 4, nz);
						if (((id == sc && id2 == sc && world.getTypeId(nx, ny-1, nz) == sc && world.getTypeId(nx, ny-2, nz) == sc && world.getTypeId(nx, ny-3, nz) == sc)
						  || (id == Block.CACTUS.id && id2 == Block.CACTUS.id)) && EEPatch.attemptBreak(activationPlayer, nx, ny, nz)) {
							if (id == sc) {
								Block.byId[id].dropNaturally(world, nx, ny - 3, nz, data, 0.25F, 1);
								Block.byId[id].dropNaturally(world, nx, ny - 3, nz, data, 1.0F, 1);
								world.setTypeId(nx, ny - 3, nz, 0);
							} else {
								Block.byId[id].dropNaturally(world, nx, ny - 4, nz, data, 0.25F, 1);
								Block.byId[id].dropNaturally(world, nx, ny - 4, nz, data, 1.0F, 1);
								world.setTypeId(nx, ny - 4, nz, 0);
							}

							world.a("largesmoke", nx, ny - 3, nz, 0.0D, 0.05D, 0.0D);
						}
					} else {
						if (!EEPatch.attemptBreak(activationPlayer, nx, ny, nz)) continue;
						Block.byId[id].dropNaturally(world, nx, ny, nz, data, 0.05F, 1);
						Block.byId[id].dropNaturally(world, nx, ny, nz, data, 1.0F, 1);
						world.setTypeId(nx, ny, nz, 0);
						world.a("largesmoke", nx, ny, nz, 0.0D, 0.05D, 0.0D);
					}
				}
			}
		}
	}

	private void doGrimarch() {
		if (activationPlayer != null) {
			if (initActivation) {
				grimarchCounter = 40;
				activationCooldown = 20;
				initActivation = false;
			}

			if (initDeactivation) {
				initDeactivation = false;
			}

			if (activated) {
				int var1 = x;
				int var2 = y;
				int var3 = z;
				byte var4 = 10;

				if (grimarchCounter >= 0 && grimarchCounter < 5) {
					List<Entity> var5 = world.a(EntityLiving.class, AxisAlignedBB.b(var1 - var4, var2 - var4, var3 - var4, var1 + var4, var2 + var4, var3 + var4));
					Iterator<Entity> var7 = var5.iterator();

					while (var7.hasNext()) {
						Entity var6 = var7.next();
						ShootArrowAt(var6, var1, var2, var3);
					}

					grimarchCounter--;

					if (grimarchCounter == 0) {
						grimarchCounter = 40;
					}
				}

				if (grimarchCounter >= 5) {
					grimarchCounter--;
				}
			}
		}
	}

	private void ShootArrowAt(Entity var1, int var2, int var3, int var4) {
		if (!(var1 instanceof EntityHuman)) {
			double var5 = var1.locX - x;
			double var7 = (var1.boundingBox.b + (var1.length / 2.0F)) - (y + 1.0D);
			double var9 = var1.locZ - z;
			EntityGrimArrow var11 = new EntityGrimArrow(world, var5, var7, var9);
			// double var12 = 4.0D;
			var11.locX = x;
			var11.locY = y + 2.0D + 0.5D;
			var11.locZ = z;
			world.addEntity(var11);
			world.makeSound(var11, "random.bow", 0.8F, 0.8F / (world.random.nextFloat() * 0.4F + 0.8F));
		}
	}

	private void doEvertide() {
		if (activated && initActivation) {
			initActivation = false;
			activationCooldown = 60;

			if (!EEProxy.getWorldInfo(world).hasStorm()) {
				EEProxy.getWorldInfo(world).setStorm(true);
				EEProxy.getWorldInfo(world).setWeatherDuration(6000);
			} else {
				EEProxy.getWorldInfo(world).setWeatherDuration(EEProxy.getWorldInfo(world).getWeatherDuration() + 6000);
			}
		}

		if (activationCooldown == 0 || initDeactivation) {
			activated = false;
			initDeactivation = false;
		}
	}

	private void doInterdiction(int var1, int var2, int var3) {
		if (initActivation) {
			activationCooldown = 20;
			initActivation = false;
		}

		if (initDeactivation) {
			initDeactivation = false;
		}

		if (activated) {
			float var4 = 9.0F;
			List<Entity> var5 = world.a(EntityMonster.class, AxisAlignedBB.b(var1 - var4, var2 - var4, var3 - var4, var1 + var4, var2 + var4, var3 + var4));
			Iterator<Entity> var7 = var5.iterator();

			while (var7.hasNext()) {
				Entity var6 = var7.next();
				PushEntities(var6, var1, var2, var3);
			}

			List<Entity> var12 = world.a(EntityArrow.class, AxisAlignedBB.b(var1 - var4, var2 - var4, var3 - var4, var1 + var4, var2 + var4, var3 + var4));
			Iterator<Entity> var9 = var12.iterator();

			while (var9.hasNext()) {
				Entity var8 = var9.next();
				PushEntities(var8, var1, var2, var3);
			}

			List<Entity> var13 = world.a(EntityFireball.class, AxisAlignedBB.b(var1 - var4, var2 - var4, var3 - var4, var1 + var4, var2 + var4, var3 + var4));
			Iterator<Entity> var11 = var13.iterator();

			while (var11.hasNext()) {
				Entity var10 = var11.next();
				PushEntities(var10, var1, var2, var3);
			}
		}
	}

	private void PushEntities(Entity var1, int var2, int var3, int var4) {
		if (!(var1 instanceof EntityHuman)) {
			double var6 = var2 - var1.locX;
			double var8 = var3 - var1.locY;
			double var10 = var4 - var1.locZ;
			double var12 = var6 * var6 + var8 * var8 + var10 * var10;
			var12 *= var12;

			if (var12 <= 1296d) {
				double var14 = -(var6 * 0.02D / var12) * 216d;
				double var16 = -(var8 * 0.02D / var12) * 216d;
				double var18 = -(var10 * 0.02D / var12) * 216d;

				if (var14 > 0.0D) {
					var14 = 0.22D;
				} else if (var14 < 0.0D) {
					var14 = -0.22D;
				}

				if (var16 > 0.2D) {
					var16 = 0.12D;
				} else if (var16 < -0.1D) {
					var16 = 0.12D;
				}

				if (var18 > 0.0D) {
					var18 = 0.22D;
				} else if (var18 < 0.0D) {
					var18 = -0.22D;
				}

				var1.motX += var14;
				var1.motY += var16;
				var1.motZ += var18;
			}
		}
	}

	public boolean isInterdicting() {
		return interdictionActive;
	}

	public void setTimeFactor() {
		EEBase.addPedestalCoords(this);
	}

	/**
	 * Indirectly gets tile entity
	 */
	public void resetTimeFactor() {
		EEBase.validatePedestalCoords(world);
	}

	public void slowEntities(Entity var1) {
		if (!(var1 instanceof EntityItem) && !(var1 instanceof EntityExperienceOrb) && !(var1 instanceof EntityGrimArrow)
				&& !(var1 instanceof EntityPhilosopherEssence) && !(var1 instanceof EntityLavaEssence) && !(var1 instanceof EntityWaterEssence)
				&& !(var1 instanceof EntityWindEssence) && !(var1 instanceof EntityHyperkinesis) && !(var1 instanceof EntityPyrokinesis)) {
			byte var2 = 4;
			var1.motX /= var2 * var2 * var2 * var2 + 1;
			var1.motZ /= var2 * var2 * var2 * var2 + 1;

			if (var1.motY < 0.0D) {
				var1.motY /= 1.0D + 0.002D * (var2 * var2 + 1);
			}
		}
	}

	public void activate(EntityHuman var1) {
		world.makeSound(var1, "transmute", 0.6F, 1.0F);
		
		if (items[0] != null && EEMaps.isPedestalItem(items[0].id) && activationCooldown <= 0) {
			activated = !activated;

			if (activated) {
				if (EEEventManager.callEvent(new EEPedestalEvent(this, items[0], var1, EEPedestalAction.Activate))){
					activated = !activated;
					return;
				}
				
				initActivation = true;

				for (int var2 = 0; var2 < 4; var2++) {
					float var3 = x + 0.5F + world.random.nextFloat() / 16.0F;
					float var4 = y + 1.0F + world.random.nextFloat() / 16.0F;
					float var5 = z + 0.5F + world.random.nextFloat() / 16.0F;
					world.a("flame", var3, var4, var5, 0.0D, 0.0D, 0.0D);
				}
			} else {
				initDeactivation = true;

				for (int var2 = 0; var2 < 4; var2++) {
					float var3 = x + 0.5F + world.random.nextFloat() / 16.0F;
					float var4 = y + 1.0F + world.random.nextFloat() / 16.0F;
					float var5 = z + 0.5F + world.random.nextFloat() / 16.0F;
					world.a("smoke", var3, var4, var5, 0.0D, 0.02D, 0.0D);
				}
			}
			
			var1.a("Pedestal " + (activated ? "activated." : "disabled."));
		}
		activationPlayer = var1;
	}

	public void activate() {
		if (activationCooldown <= 0) {
			world.makeSound(x, y, z, "transmute", 0.6F, 1.0F);

			if (items[0] != null && EEMaps.isPedestalItem(items[0].id)) {
				if (items[0].getItem() instanceof ItemGrimarchRing) {
					return;
				}

				activated = !activated;

				if (activated) {
					initActivation = true;

					for (int var1 = 0; var1 < 4; var1++) {
						float var2 = x + 0.5F + world.random.nextFloat() / 16.0F;
						float var3 = y + 1.0F + world.random.nextFloat() / 16.0F;
						float var4 = z + 0.5F + world.random.nextFloat() / 16.0F;
						world.a("flame", var2, var3, var4, 0.0D, 0.0D, 0.0D);
					}
				} else {
					initDeactivation = true;

					for (int var1 = 0; var1 < 4; var1++) {
						float var2 = x + 0.5F + world.random.nextFloat() / 16.0F;
						float var3 = y + 1.0F + world.random.nextFloat() / 16.0F;
						float var4 = z + 0.5F + world.random.nextFloat() / 16.0F;
						world.a("smoke", var2, var3, var4, 0.0D, 0.02D, 0.0D);
					}
				}
			}
		}
		
		activationPlayer = null;
	}

	public ItemStack splitStack(int var1, int var2) {
		if (items[var1] != null) {
			if (items[var1].count <= var2) {
				ItemStack var3 = items[var1];
				items[var1] = null;
				return var3;
			}

			ItemStack var3 = items[var1].a(var2);

			if (items[var1].count == 0) {
				items[var1] = null;
			}

			return var3;
		}

		return null;
	}
	
	public void setItem(int var1, ItemStack var2) {
		items[var1] = var2;

		if (var2 != null && var2.count > getMaxStackSize()) {
			var2.count = getMaxStackSize();
		}
	}
	
	public ItemStack getItem(int var1) {
		return items[var1];
	}
	
	public int getSize() {
		return items.length;
	}
	
	public int getMaxStackSize() {
		return 64;
	}

	public void f() {}

	public void g() {}

	public boolean a(EntityHuman var1) {
		return world.getTileEntity(x, y, z) == this ? var1.f(x + 0.5D, y + 0.5D, z + 0.5D) <= 64D : false;
	}
	
	public boolean onBlockActivated(EntityHuman var1) {
		var1.openGui(mod_EE.getInstance(), GuiIds.PEDESTAL, var1.world, (int) var1.locX, (int) var1.locY, (int) var1.locZ);
		return true;
	}
	
	public void setPlayer(EntityHuman var1) {
		player = var1.name;
	}
	
	public ItemStack splitWithoutUpdate(int var1) {
		return null;
	}

	public Packet d() {
		EEPacket var1 = PacketHandler.getPacket(PacketTypeHandler.PEDESTAL);
		var1.setCoords(x, y, z);

		if (getItem(0) != null) {
			var1.setItem(getItem(0).getItem().id);
		} else {
			var1.setItem(-1);
		}

		var1.setState(activated);
		return PacketHandler.getPacketForSending(var1);
	}
	
	public ItemStack[] getContents() {
		return items;
	}

	public void setMaxStackSize(int size) {}
}