package ee;

import ee.item.ItemLootBall;

import java.util.*;

import net.minecraft.server.*;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

public class AlchemyBagData extends WorldMapBase implements IInventory {

	public boolean voidOn;
	public boolean repairOn;
	public boolean markForUpdate;
	public boolean condenseOn;
	public int repairTimer = 0;
	public int condenseCheckTimer = 0;
	public static final String prefix = "bag";
	public static final String prefix_ = "bag_";
	public ItemStack items[] = new ItemStack[113];
	private int eternalDensity;
	private boolean initialized;
	public static List<AlchemyBagData> datas = new LinkedList<AlchemyBagData>();
	private List<HumanEntity> transaction = new ArrayList<HumanEntity>();
	//public boolean keepRepairOff = false;

	public AlchemyBagData(String var1) {
		super(var1);
		datas.add(this);
	}

	public void onUpdate(World var1, EntityHuman var2) {
		if (!initialized) {
			initialized = true;
			update();
			
			//if (keepRepairOff){
			//	repairOn = false;
			//} else if (!TRUtil.isAllowed(var2, "Disabler_RepairCharm_InBag", "disabler.repaircharm.inbag")){
			//	keepRepairOff = true;
			//	repairOn = false;
			//}
		}
		
		if (repairOn) doRepair();
		if (condenseOn) doCondense(items[eternalDensity]);
		
		if (voidOn) {
			boolean var3 = false;
			for (int data = 0; data <= 15; data++) {
				boolean found = false;
				ItemStack items[] = var2.inventory.items;
				int itemsLength = items.length;
				
				//Search through all items in the inventory for an alchemy bag.
				for (int i = 0; i < itemsLength; i++) {
					ItemStack var9 = items[i];
					if (var9 != null && var9.doMaterialsMatch(new ItemStack(EEItem.alchemyBag, 1, data))){
						found = true;
						break;
					}
				}

				if (!found) continue;
				
				//ADDED
				if (data > EEPatch.alcBagAmount) continue;
				
				String datName = EEPatch.getBag(var2, var1, data);
				//---
				//REMOVED
				//String datName = new StringBuilder("bag_").append(var2.name).append(data).toString();
				//---
				AlchemyBagData bag = (AlchemyBagData) var1.a(ee.AlchemyBagData.class, datName);
				
				if (bag == null) continue;
				if (var3) break;

				if (bag.voidOn) var3 = true;
				if (bag != this || !var3) continue;
				doAttraction(var2);
				break;
			}
		}
		if (markForUpdate) a();
	}

	public int getSize() {
		return 104;
	}
	public int getMaxStackSize() {
		return 64;
	}
	public String getName() {
		return "Bag";
	}
	
	public ItemStack splitStack(int var1, int var2) {
		if (items[var1] == null) return null;
		ItemStack var3;
		if (items[var1].count <= var2) {
			var3 = items[var1];
			items[var1] = null;
			update();
			return var3;
		}
		var3 = items[var1].a(var2);

		if (items[var1].count == 0) items[var1] = null;

		update();
		return var3;
	}
	
	public ItemStack getItem(int var1) {
		return items[var1];
	}
	public void setItem(int var1, ItemStack var2) {
		items[var1] = var2;

		if (var2 != null && var2.count > getMaxStackSize()) var2.count = getMaxStackSize();

		update();
	}

	public void update() {
		markForUpdate = true;
		boolean repair = false;
		boolean condense = false;
		boolean voidring = false;

		int counter = 0;
		
		
		for (ItemStack current : items) {
			if (current == null) continue;
			Item item = current.getItem();
			if (item == EEItem.repairCharm)
				repair = true;
			else if (item == EEItem.voidRing) {
				eternalDensity = counter;
				if ((current.getData() & 1) == 0) {
					current.setData(current.getData() + 1);
					((ItemEECharged) item).setBoolean(current, "active", true);
				}
				voidring = true;
				condense = true;
			} else if (item == EEItem.eternalDensity) {
				eternalDensity = counter;
				if ((current.getData() & 1) == 0) {
					current.setData(current.getData() + 1);
					((ItemEECharged) item).setBoolean(current, "active", true);
				}
				condense = true;
			} else if (item == EEItem.attractionRing) {
				voidring = true;
				if ((current.getData() & 1) == 0) {
					current.setData(current.getData() + 1);
					((ItemEECharged) item).setBoolean(current, "active", true);
				}
			}
			counter++;
		}
		
		/*
		for (int var4 = 0; var4 < items.length; var4++){
			if (items[var4] == null) continue;
			Item item = items[var4].getItem();
			if (item == EEItem.repairCharm)
				var1 = true;
			else if (item == EEItem.voidRing) {
				eternalDensity = counter;
				if ((items[var4].getData() & 1) == 0) {
					items[var4].setData(items[var4].getData() + 1);
					((ItemEECharged) item).setBoolean(items[var4], "active", true);
				}
				var3 = true;
				var2 = true;
			} else if (item == EEItem.eternalDensity) {
				eternalDensity = counter;
				if ((items[var4].getData() & 1) == 0) {
					items[var4].setData(items[var4].getData() + 1);
					((ItemEECharged) item).setBoolean(items[var4], "active", true);
				}
				var2 = true;
			} else if (item == EEItem.attractionRing) {
				var3 = true;
				if ((items[var4].getData() & 1) == 0) {
					items[var4].setData(items[var4].getData() + 1);
					((ItemEECharged) item).setBoolean(items[var4], "active", true);
				}
			}
			counter++;
		}*/

		/*
		 * for(int var4 = 0; var4 < items.length; var4++) { if(items[var4] != null) { if(items[var4].getItem() == EEItem.repairCharm) var1 = true;
		 * if(items[var4].getItem() == EEItem.voidRing) { eternalDensity = var4; if((items[var4].getData() & 1) == 0) {
		 * items[var4].setData(items[var4].getData() + 1); ((ItemEECharged)items[var4].getItem()).setBoolean(items[var4], "active", true); } var3 = true; var2 =
		 * true; } if(items[var4].getItem() == EEItem.eternalDensity) { eternalDensity = var4; if((items[var4].getData() & 1) == 0) {
		 * items[var4].setData(items[var4].getData() + 1); ((ItemEECharged)items[var4].getItem()).setBoolean(items[var4], "active", true); } var2 = true; }
		 * if(items[var4].getItem() == EEItem.attractionRing) { var3 = true; if((items[var4].getData() & 1) == 0) { items[var4].setData(items[var4].getData() +
		 * 1); ((ItemEECharged)items[var4].getItem()).setBoolean(items[var4], "active", true); } } } }
		 */

		//if (!keepRepairOff && repair != repairOn) repairOn = repair;
		if (repair != repairOn) repairOn = repair;
		if (condense != condenseOn) condenseOn = condense;
		if (voidring != voidOn) voidOn = voidring;
		
	}

	public void doRepair() {
		if (repairTimer >= 20) {
			boolean var2 = false;
			for (int i = 0; i < getSize(); i++) {
				var2 = false;
				ItemStack current = items[i];

				if (current == null) continue;
				
				for (int j = 0; j < EEMaps.chargedItems.size(); j++) {
					if (EEMaps.chargedItems.get(j).intValue() != current.id) continue;
					var2 = true;
					break;
				}

				if (!var2 && current.getData() >= 1 && current.d())
					current.setData(current.getData() - 1);
			}

			repairTimer = 0;
		}
		repairTimer++;
		markForUpdate = true;
	}

	public void doCondense(ItemStack var1) {
		if (eternalDensity == -1) return;
			int var2 = 0;
			for (ItemStack current : items) {
				if (current != null && isValidMaterial(current)) {
					int emc = EEMaps.getEMC(current);
					if (emc > var2) var2 = emc;
				}
			}

			for (ItemStack current : items) {
				if (current != null && isValidMaterial(current)) {
					int emc = EEMaps.getEMC(current);
					if (emc < var2) var2 = emc;
				}
			}

			/*for (int var3 = 0; var3 < items.length; var3++)
				if (items[var3] != null && isValidMaterial(items[var3]) && EEMaps.getEMC(items[var3]) > var2)
					var2 = EEMaps.getEMC(items[var3]);

			for (int var3 = 0; var3 < items.length; var3++)
				if (items[var3] != null && isValidMaterial(items[var3]) && EEMaps.getEMC(items[var3]) < var2)
					var2 = EEMaps.getEMC(items[var3]);*/

			int rmemc = EEMaps.getEMC(EEItem.redMatter.id);
			int dmemc = EEMaps.getEMC(EEItem.darkMatter.id);
			int diamondemc = EEMaps.getEMC(Item.DIAMOND.id);
			int goldemc = EEMaps.getEMC(Item.GOLD_INGOT.id);
			int ironemc = EEMaps.getEMC(Item.IRON_INGOT.id);
			// if(var2 < rmemc && !AnalyzeTier(items[eternalDensity], rmemc) &&
			// var2 < dmemc && !AnalyzeTier(items[eternalDensity], dmemc) &&
			// var2 < diamondemc && !AnalyzeTier(items[eternalDensity], diamondemc) &&
			// var2 < goldemc && !AnalyzeTier(items[eternalDensity], goldemc) &&
			// var2 < ironemc)
			// if(!AnalyzeTier(items[eternalDensity], ironemc));
			// FIXME does this still work now?

			if (var2 >= rmemc || AnalyzeTier(items[eternalDensity], rmemc) || var2 >= dmemc || AnalyzeTier(items[eternalDensity], dmemc) || var2 >= diamondemc
					|| AnalyzeTier(items[eternalDensity], diamondemc) || var2 >= goldemc || AnalyzeTier(items[eternalDensity], goldemc) || var2 >= ironemc
					|| !AnalyzeTier(items[eternalDensity], ironemc))
			return;
	}

	private boolean AnalyzeTier(ItemStack var1, int var2) {
		if (var1 == null) return false;
		int var3 = 0;
		// for(int var4 = 0; var4 < items.length; var4++)
		// if(items[var4] != null && isValidMaterial(items[var4]) && EEMaps.getEMC(items[var4]) < var2)
		// var3 += EEMaps.getEMC(items[var4]) * items[var4].count;

		for (ItemStack current : items) {
			int emc = EEMaps.getEMC(current);
			if (current != null && isValidMaterial(current) && emc < var2) var3 += emc * current.count;
		}

		if (var3 + emc(var1) < var2)
			return false;

		int var4 = 0;

		while ((var3 + emc(var1) >= var2) && (var4 < 10)) {
			var4++;
			ConsumeMaterialBelowTier(var1, var2);
		}

		// for(int var4 = 0; var3 + emc(var1) >= var2 && var4 < 10; ConsumeMaterialBelowTier(var1, var2))
		// var4++;

		if (emc(var1) >= var2 && roomFor(getProduct(var2))) {
			PushStack(getProduct(var2));
			takeEMC(var1, var2);
		}
		return true;
	}

	private boolean roomFor(ItemStack var1) {
		if (var1 == null) return false;
		for (int var2 = 0; var2 < items.length; var2++) {
			ItemStack current = items[var2];
			if (current == null)
				return true;
			if (current.doMaterialsMatch(var1) && current.count <= var1.getMaxStackSize() - var1.count)
				return true;
		}

		return false;
	}

	private ItemStack getProduct(int emc) {
		// return var1 != EEMaps.getEMC(Item.IRON_INGOT.id) ? var1 != EEMaps.getEMC(Item.GOLD_INGOT.id) ? var1 != EEMaps.getEMC(Item.DIAMOND.id) ? var1 !=
		// EEMaps.getEMC(EEItem.darkMatter.id) ? var1 != EEMaps.getEMC(EEItem.redMatter.id) ? null : new ItemStack(EEItem.redMatter, 1) : new
		// ItemStack(EEItem.darkMatter, 1) : new ItemStack(Item.DIAMOND, 1) : new ItemStack(Item.GOLD_INGOT, 1) : new ItemStack(Item.IRON_INGOT, 1);
		return emc == EEMaps.getEMC(EEItem.redMatter.id) ? new ItemStack(EEItem.redMatter, 1) :
			emc == EEMaps.getEMC(EEItem.darkMatter.id) ? new ItemStack(EEItem.darkMatter, 1) :
				emc == EEMaps.getEMC(Item.DIAMOND.id) ? new ItemStack(Item.DIAMOND, 1) :
					emc == EEMaps.getEMC(Item.GOLD_INGOT.id) ? new ItemStack(Item.GOLD_INGOT, 1) :
						emc == EEMaps.getEMC(Item.IRON_INGOT.id) ? new ItemStack(Item.IRON_INGOT, 1) :
							null;
	}

	public boolean PushStack(ItemStack var1) {
		if (var1 == null) return true;
		for (int var2 = 0; var2 < items.length; var2++) {
			if (items[var2] == null) {
				items[var2] = var1.cloneItemStack();
				var1 = null;
				return true;
			}
			if (items[var2].doMaterialsMatch(var1) && items[var2].count <= var1.getMaxStackSize() - var1.count) {
				items[var2].count += var1.count;
				var1 = null;
				return true;
			}
		}

		return false;
	}

	private void ConsumeMaterialBelowTier(ItemStack var1, int var2) {
		for (int var3 = 0; var3 < items.length; var3++) {
			ItemStack current = items[var3];
			if (current != null && isValidMaterial(current)) {
				int emc = EEMaps.getEMC(current);
				if (emc < var2) {
					addEMC(var1, emc);
					items[var3].count--;
					if (items[var3].count == 0) items[var3] = null;
					return;
				}
			}
		}

	}

	private boolean isValidMaterial(ItemStack stack) {
		if (stack == null) return false;
		
		if (EEMaps.getEMC(stack) == 0) return false;
		if (stack.getItem() instanceof ItemKleinStar) return false;

		int id = stack.id;
		return id != EEItem.redMatter.id ? id >= Block.byId.length || !(Block.byId[id] instanceof BlockContainer) || !Block.byId[id].hasTileEntity(stack.getData()) : false;

	}

	private int emc(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemEternalDensity)
			return ((ItemEternalDensity) item).getInteger(stack, "emc");
		else if (item instanceof ItemVoidRing)
			return ((ItemVoidRing) item).getInteger(stack, "emc");
		else
			return 0;

		// return (var1.getItem() instanceof ItemEternalDensity) || (var1.getItem() instanceof ItemVoidRing) ? (var1.getItem() instanceof ItemEternalDensity) ?
		// ((ItemEternalDensity)var1.getItem()).getInteger(var1, "emc") :
		// ((ItemVoidRing)var1.getItem()).getInteger(var1, "emc") :
		// 0;
	}

	private void takeEMC(ItemStack stack, int emc) {
		// if((var1.getItem() instanceof ItemEternalDensity) || (var1.getItem() instanceof ItemVoidRing))
		Item item = stack.getItem();
		if (item instanceof ItemEternalDensity)
			((ItemEternalDensity) item).setInteger(stack, "emc", emc(stack) - emc);
		else if (item instanceof ItemVoidRing)
			((ItemVoidRing) item).setInteger(stack, "emc", emc(stack) - emc);
	}

	private void addEMC(ItemStack stack, int emc) {
		Item item = stack.getItem();
		if (item instanceof ItemEternalDensity)
			((ItemEternalDensity) item).setInteger(stack, "emc", emc(stack) + emc);
		else if (item instanceof ItemVoidRing)
			((ItemVoidRing) item).setInteger(stack, "emc", emc(stack) + emc);
	}

	public void doAttraction(EntityHuman var1) {
		/*
		 * List<Entity> var2 = var1.world.a(net.minecraft.server.EntityItem.class, AxisAlignedBB.b(EEBase.playerX(var1) - 10D, EEBase.playerY(var1) - 10D,
		 * EEBase.playerZ(var1) - 10D, EEBase.playerX(var1) + 10D, EEBase.playerY(var1) + 10D, EEBase.playerZ(var1) + 10D)); Entity var3; for(Iterator<Entity>
		 * var4 = var2.iterator(); var4.hasNext(); PullItems(var3, var1)) var3 = var4.next();
		 * 
		 * List<Entity> var14 = var1.world.a(net.minecraft.server.EntityItem.class, AxisAlignedBB.b(EEBase.playerX(var1) - 0.55000000000000004D,
		 * EEBase.playerY(var1) - 0.55000000000000004D, EEBase.playerZ(var1) - 0.55000000000000004D, EEBase.playerX(var1) + 0.55000000000000004D,
		 * EEBase.playerY(var1) + 0.55000000000000004D, EEBase.playerZ(var1) + 0.55000000000000004D)); Entity var5; for(Iterator<Entity> var6 =
		 * var14.iterator(); var6.hasNext(); GrabItems(var5)) var5 = var6.next();
		 * 
		 * List<Entity> var15 = var1.world.a(ee.EntityLootBall.class, AxisAlignedBB.b(EEBase.playerX(var1) - 10D, EEBase.playerY(var1) - 10D,
		 * EEBase.playerZ(var1) - 10D, EEBase.playerX(var1) + 10D, EEBase.playerY(var1) + 10D, EEBase.playerZ(var1) + 10D)); Entity var7; for(Iterator<Entity>
		 * var8 = var15.iterator(); var8.hasNext(); PullItems(var7, var1)) var7 = var8.next();
		 * 
		 * List<Entity> var16 = var1.world.a(ee.EntityLootBall.class, AxisAlignedBB.b(EEBase.playerX(var1) - 0.55000000000000004D, EEBase.playerY(var1) -
		 * 0.55000000000000004D, EEBase.playerZ(var1) - 0.55000000000000004D, EEBase.playerX(var1) + 0.55000000000000004D, EEBase.playerY(var1) +
		 * 0.55000000000000004D, EEBase.playerZ(var1) + 0.55000000000000004D)); Entity var9; for(Iterator<Entity> var10 = var16.iterator(); var10.hasNext();
		 * GrabItems(var9)) var9 = var10.next();
		 * 
		 * List<Entity> var13 = var1.world.a(net.minecraft.server.EntityExperienceOrb.class, AxisAlignedBB.b(EEBase.playerX(var1) - 10D, EEBase.playerY(var1) -
		 * 10D, EEBase.playerZ(var1) - 10D, EEBase.playerX(var1) + 10D, EEBase.playerY(var1) + 10D, EEBase.playerZ(var1) + 10D)); Entity var11;
		 * for(Iterator<Entity> var12 = var13.iterator(); var12.hasNext(); PullItems(var11, var1)) var11 = var12.next();
		 */
		List<EntityItem> var2 = var1.world.a(EntityItem.class,
				AxisAlignedBB.b(EEBase.playerX(var1) - 10.0D, EEBase.playerY(var1) - 10.0D, EEBase.playerZ(var1) - 10.0D, EEBase.playerX(var1) + 10.0D,
						EEBase.playerY(var1) + 10.0D, EEBase.playerZ(var1) + 10.0D));
		Iterator<EntityItem> var4 = var2.iterator();

		while (var4.hasNext()) {
			Entity var3 = var4.next();
			PullItems(var3, var1);
		}

		List<EntityItem> var14 = var1.world.a(EntityItem.class,
				AxisAlignedBB.b(EEBase.playerX(var1) - 0.55D, EEBase.playerY(var1) - 0.55D, EEBase.playerZ(var1) - 0.55D, EEBase.playerX(var1) + 0.55D,
						EEBase.playerY(var1) + 0.55D, EEBase.playerZ(var1) + 0.55D));
		Iterator<EntityItem> var6 = var14.iterator();

		while (var6.hasNext()) {
			Entity var5 = var6.next();
			GrabItems(var5);
		}

		List<EntityLootBall> var15 = var1.world.a(EntityLootBall.class,
				AxisAlignedBB.b(EEBase.playerX(var1) - 10.0D, EEBase.playerY(var1) - 10.0D, EEBase.playerZ(var1) - 10.0D, EEBase.playerX(var1) + 10.0D,
						EEBase.playerY(var1) + 10.0D, EEBase.playerZ(var1) + 10.0D));
		Iterator<EntityLootBall> var8 = var15.iterator();

		while (var8.hasNext()) {
			Entity var7 = var8.next();
			PullItems(var7, var1);
		}

		List<EntityLootBall> var16 = var1.world.a(EntityLootBall.class,
				AxisAlignedBB.b(EEBase.playerX(var1) - 0.55D, EEBase.playerY(var1) - 0.55D, EEBase.playerZ(var1) - 0.55D, EEBase.playerX(var1) + 0.55D,
						EEBase.playerY(var1) + 0.55D, EEBase.playerZ(var1) + 0.55D));
		Iterator<EntityLootBall> var10 = var16.iterator();

		while (var10.hasNext()) {
			Entity var9 = var10.next();
			GrabItems(var9);
		}

		List<EntityExperienceOrb> var13 = var1.world.a(EntityExperienceOrb.class,
				AxisAlignedBB.b(EEBase.playerX(var1) - 10.0D, EEBase.playerY(var1) - 10.0D, EEBase.playerZ(var1) - 10.0D, EEBase.playerX(var1) + 10.0D,
						EEBase.playerY(var1) + 10.0D, EEBase.playerZ(var1) + 10.0D));
		Iterator<EntityExperienceOrb> var12 = var13.iterator();

		while (var12.hasNext()) {
			Entity var11 = var12.next();
			PullItems(var11, var1);
		}

	}

	private void PullItems(Entity entity, EntityHuman human) {
		if (!(entity instanceof EntityItem) && !(entity instanceof EntityLootBall)) return;
		if (entity instanceof EntityLootBall) ((EntityLootBall) entity).setBeingPulled(true);
		double dx = (EEBase.playerX(human) + 0.5D) - entity.locX;
		double dy = (EEBase.playerY(human) + 0.5D) - entity.locY;
		double dz = (EEBase.playerZ(human) + 0.5D) - entity.locZ;
		double var10 = dx * dx + dy * dy + dz * dz;
		var10 *= var10;
		
		if (var10 <= 1296d) {
			double vx = ((dx * 0.01999999955296516D) / var10) * 216d;
			double vy = ((dy * 0.01999999955296516D) / var10) * 216d;
			double vz = ((dz * 0.01999999955296516D) / var10) * 216d;
			
			if (vx > 0.1D) vx = 0.1D;
			else if (vx < -0.1D) vx = -0.1D;
			
			if (vy > 0.1D) vy = 0.1D;
			else if (vy < -0.1D) vy = -0.1D;
			
			if (vz > 0.1D) vz = 0.1D;
			else if (vz < -0.1D) vz = -0.1D;
			
			entity.motX += vx * 1.2D;
			entity.motY += vy * 1.2D;
			entity.motZ += vz * 1.2D;
		}
	}

	private void GrabItems(Entity entity) {
		if (entity == null || entity.dead) return;
		
		if ((entity instanceof EntityItem)) {
			ItemStack stack = ((EntityItem) entity).itemStack;
			if (stack == null) {
				entity.die();
				return;
			}

			if (stack.getItem() instanceof ItemLootBall) {
				ItemLootBall var3 = (ItemLootBall) stack.getItem();
				ItemStack var4[] = var3.getDroplist(stack);
				ItemStack var5[] = var4;
				int var6 = var4.length;
				for (int var7 = 0; var7 < var6; var7++) {
					ItemStack var8 = var5[var7];
					PushStack(var8);
				}

				entity.die();
			} else {
				PushStack(stack);
				entity.die();
			}
		} else if ((entity instanceof EntityLootBall)) {
			if (((EntityLootBall) entity).items == null) entity.die();
			//ItemStack var2[] = ((EntityLootBall) var1).items;
			PushDenseStacks((EntityLootBall) entity);
			if (((EntityLootBall) entity).isEmpty())
				entity.die();
		}
	}

	private void PushDenseStacks(EntityLootBall var1) {
		for (int var2 = 0; var2 < var1.items.length; var2++) {
			if (var1.items[var2] != null && PushStack(var1.items[var2])) var1.items[var2] = null;
		}
	}

	public boolean PushStack(EntityItem entity) {
		if (entity == null) return false;
		
		if (entity.itemStack == null || entity.itemStack.count < 1) {
			entity.die();
			return false;
		}
		
		for (int var2 = 0; var2 < items.length; var2++) {
			if (items[var2] == null) {
				items[var2] = entity.itemStack.cloneItemStack();
				for (items[var2].count = 0; entity.itemStack.count > 0 && items[var2].count < items[var2].getMaxStackSize(); entity.itemStack.count--)
					items[var2].count++;

				entity.die();
				return true;
			}
			if (items[var2].doMaterialsMatch(entity.itemStack) && items[var2].count <= entity.itemStack.getMaxStackSize() - entity.itemStack.count) {
				while (entity.itemStack.count > 0 && items[var2].count < items[var2].getMaxStackSize()) {
					items[var2].count++;
					entity.itemStack.count--;
				}
				entity.die();
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unused")
	private void PushDenseStacks(EntityLootBall var1, EntityHuman var2) {
		for (int var3 = 0; var3 < var1.items.length; var3++) {
			if (var1.items[var3] != null) {
				PushStack(var1.items[var3], var2);
				var1.items[var3] = null;
			}
		}
	}

	public void PushStack(ItemStack var1, EntityHuman var2) {
		for (int var3 = 0; var3 < getSize(); var3++) {
			if (var1 == null) continue;
			
			if (items[var3] == null) {
				items[var3] = var1.cloneItemStack();
				var1 = null;
				markForUpdate = true;
				return;
			}
			
			if (items[var3].doMaterialsMatch(var1)) {
				while (items[var3].count < items[var3].getMaxStackSize()) {
					items[var3].count++;
					var1.count--;

					if (var1.count == 0) {
						var1 = null;
						markForUpdate = true;
						return;
					}
				}
			}

			else if (var3 == items.length - 1) {
				EntityItem var4 = new EntityItem(var2.world, EEBase.playerX(var2), EEBase.playerY(var2), EEBase.playerZ(var2), var1);
				var4.pickupDelay = 1;
				var2.world.addEntity(var4);
				markForUpdate = true;
				return;
			}
		}

		if (var1 != null) {
			for (int var3 = 0; var3 < items.length; var3++) {
				if (items[var3] == null) {
					items[var3] = var1.cloneItemStack();
					var1 = null;
					markForUpdate = true;
					return;
				}
			}
		}
	}

	public boolean a(EntityHuman var1) {
		return true;
	}

	public void f() {}
	public void g() {}

	public void a(NBTTagCompound var1) {
		if (!EEPatch.allowAlcBags){
			voidOn = false;
			repairOn = false;
			condenseOn = false;
			eternalDensity = 0;
			items = new ItemStack[113];
		} else {
			voidOn = var1.getBoolean("voidOn");
			repairOn = var1.getBoolean("repairOn");
			condenseOn = var1.getBoolean("condenseOn");
			eternalDensity = var1.getShort("eternalDensity");
			NBTTagList var2 = var1.getList("Items");
			items = new ItemStack[113];
			for (int var3 = 0; var3 < var2.size(); var3++) {
				NBTTagCompound var4 = (NBTTagCompound) var2.get(var3);
				int var5 = var4.getByte("Slot") & 255;
				if (var5 >= 0 && var5 < items.length) items[var5] = ItemStack.a(var4);
			}
		}
	}
	public void b(NBTTagCompound var1) {
		if (!EEPatch.allowAlcBags) return;
		var1.setBoolean("voidOn", voidOn);
		var1.setBoolean("repairOn", repairOn);
		var1.setBoolean("condenseOn", condenseOn);
		var1.setShort("eternalDensity", (short) eternalDensity);
		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < items.length; var3++)
			if (items[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				items[var3].save(var4);
				var2.add(var4);
			}

		var1.set("Items", var2);
	}

	public ItemStack splitWithoutUpdate(int var1) {
		return null;
	}

	public ItemStack[] getContents() {
		return items;
	}

	public void onOpen(CraftHumanEntity crafthumanentity) {
		transaction.add(crafthumanentity);
	}
	public void onClose(CraftHumanEntity crafthumanentity) {
		transaction.remove(crafthumanentity);
	}
	
	public List<HumanEntity> getViewers() {
		return transaction;
		//return new ArrayList<HumanEntity>(0);
	}

	public InventoryHolder getOwner() {
		return null;
	}

	public void setMaxStackSize(int i) {}

}