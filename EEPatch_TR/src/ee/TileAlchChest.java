package ee;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.HumanEntity;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Block;
import net.minecraft.server.BlockContainer;
import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.mod_EE;
import buildcraft.api.ISpecialInventory;
import buildcraft.api.Orientations;
import ee.core.GuiIds;
import ee.item.ItemLootBall;
import forge.ISidedInventory;

public class TileAlchChest extends TileEE implements ISpecialInventory, ISidedInventory {
	private ItemStack[] items = new ItemStack[113];
	private int repairTimer = 0;
	private int eternalDensity;
	private boolean repairOn;
	private boolean condenseOn;
	private boolean interdictionOn;
	public boolean timeWarp;
	public float lidAngle;
	public float prevLidAngle;
	public int numUsingPlayers;
	private int ticksSinceSync;
	@SuppressWarnings("unused")
	private boolean initialized;
	private boolean attractionOn;
	
	private boolean removed = false;
	
	//private static HashSet<Entity> grabbing = new HashSet<Entity>();

	public boolean addItem(ItemStack item, boolean add, Orientations side) {
		if (item == null) return false;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				if (add) {
					for (items[i] = item.cloneItemStack(); item.count > 0; item.count -= 1);
				}

				return true;
			}

			if (items[i].doMaterialsMatch(item) && items[i].count < items[i].getMaxStackSize()) {
				if (add) {
					while (items[i].count < items[i].getMaxStackSize() && item.count > 0) {
						items[i].count++;
						item.count--;
					}

					if (item.count != 0) continue;
				}
				return true;
			}
		}
		
		return false;
	}

	public ItemStack extractItem(boolean var1, Orientations var2) {
		if (removed) return new ItemStack(3,1,0);
		for (int var3 = 0; var3 < items.length; var3++) {
			if (items[var3] != null) {
				ItemStack var4 = items[var3].cloneItemStack();
				var4.count = 1;

				if (var1) {
					if (items[var3].count <= 1) items[var3] = null;
					else items[var3].count--;
				}

				return var4;
			}
		}

		return null;
	}

	public int getSize() {
		return 104;
	}
	
	public ItemStack getItem(int var1) {
		if (removed) return new ItemStack(3, 1, 0);
		return items[var1];
	}
	
	public ItemStack splitStack(int var1, int var2) {
		if (items[var1] != null) {
			ItemStack var3;
			if ((var3 = items[var1]).count <= var2) {
				items[var1] = null;
				update();
				return var3;
			}
			
			var3 = items[var1].a(var2);

			if (items[var1].count < 1) {
				items[var1] = null;
			}

			update();
			return var3;
		}

		return null;
	}

	public void setItem(int var1, ItemStack var2) {
		items[var1] = var2;

		if (var2 != null && var2.count > getMaxStackSize()) {
			var2.count = getMaxStackSize();
		}

		update();
	}

	public String getName() {
		return "Chest";
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

		condenseOn = var1.getBoolean("condenseOn");
		repairOn = var1.getBoolean("repairOn");
		eternalDensity = var1.getShort("eternalDensity");
		timeWarp = var1.getBoolean("timeWarp");
		interdictionOn = var1.getBoolean("interdictionOn");
	}

	public void b(NBTTagCompound var1) {
		super.b(var1);
		var1.setBoolean("timeWarp", timeWarp);
		var1.setBoolean("condenseOn", condenseOn);
		var1.setBoolean("repairOn", repairOn);
		var1.setShort("eternalDensity", (short) eternalDensity);
		var1.setBoolean("interdictionOn", interdictionOn);
		NBTTagList var2 = new NBTTagList();

		for (int var3 = 0; var3 < items.length; var3++) {
			if (items[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				items[var3].save(var4);
				var2.add(var4);
			}
		}

		var1.set("Items", var2);
	}

	public int getMaxStackSize() {
		return 64;
	}

	public void update() {
		super.update();
		
		if (world == null || EEProxy.isClient(world)) return;
		
		boolean repair = false;
		boolean eternal = false;
		boolean torch = false;
		boolean watch = false;
		boolean bhb = false;

		for (int var6 = 0; var6 < getSize(); var6++) {
			if (items[var6] != null) {
				if (items[var6].getItem().id == EEItem.watchOfTime.id) {
					watch = true;
				}

				if (items[var6].getItem().id == EEItem.repairCharm.id) {
					repair = true;
				}

				if (items[var6].getItem() instanceof ItemVoidRing) {
					eternalDensity = var6;

					if ((items[var6].getData() & 0x1) == 0) {
						items[var6].setData(items[var6].getData() + 1);
						((ItemEECharged) items[var6].getItem()).setBoolean(items[var6], "active", true);
					}

					eternal = true;
					bhb = true;
				}

				if (items[var6].getItem().id == EEItem.eternalDensity.id) {
					eternalDensity = var6;

					if ((items[var6].getData() & 0x1) == 0) {
						items[var6].setData(items[var6].getData() + 1);
						((ItemEECharged) items[var6].getItem()).setBoolean(items[var6], "active", true);
					}

					eternal = true;
				}

				if (items[var6].getItem() instanceof ItemAttractionRing) {
					if ((items[var6].getData() & 0x1) == 0) {
						items[var6].setData(items[var6].getData() + 1);
						((ItemEECharged) items[var6].getItem()).setBoolean(items[var6], "active", true);
					}

					bhb = true;
				}

				if (items[var6].getItem().id == EEBlock.eeTorch.id && items[var6].getData() == 0) {
					torch = true;
				}
			}
		}

		if (watch != timeWarp) 
			timeWarp = watch;

		if (repair != repairOn) 
			repairOn = repair;

		if (bhb != attractionOn) 
			attractionOn = bhb;

		if (eternal != condenseOn) 
			condenseOn = eternal;
		else if (!eternal)
			eternalDensity = -1;

		if (torch != interdictionOn) {
			world.notify(x, y, z);
			interdictionOn = torch;
		}
	}

	public void doRepair() {
		if (repairTimer >= 20) {
			ItemStack var1 = null;
			boolean var2 = false;

			for (int var3 = 0; var3 < getSize(); var3++) {
				var2 = false;
				var1 = items[var3];

				if (var1 != null) {
					for (int var4 = 0; var4 < EEMaps.chargedItems.size(); var4++) {
						if (EEMaps.chargedItems.get(var4).intValue() != var1.id) continue;
						var2 = true;
						break;
					}

					if (!var2 && var1.getData() >= 1 && var1.d()) {
						var1.setData(var1.getData() - 1);
					}
				}
			}

			repairTimer = 0;
		}

		repairTimer++;
	}

	public void doCondense(ItemStack var1) {
		if (eternalDensity != -1) {
			int var2 = 0;

			for (int var3 = 0; var3 < items.length; var3++) {
				if (items[var3] != null && isValidMaterial(items[var3]) && EEMaps.getEMC(items[var3]) > var2) {
					var2 = EEMaps.getEMC(items[var3]);
				}
			}

			for (int var3 = 0; var3 < items.length; var3++) {
				if (items[var3] != null && isValidMaterial(items[var3]) && EEMaps.getEMC(items[var3]) < var2) {
					var2 = EEMaps.getEMC(items[var3]);
				}
			}

			if (var2 < EEMaps.getEMC(EEItem.redMatter.id) && !AnalyzeTier(items[eternalDensity], EEMaps.getEMC(EEItem.redMatter.id)) &&
				var2 < EEMaps.getEMC(EEItem.darkMatter.id) && !AnalyzeTier(items[eternalDensity], EEMaps.getEMC(EEItem.darkMatter.id)) &&
				var2 < EEMaps.getEMC(Item.DIAMOND.id) && !AnalyzeTier(items[eternalDensity], EEMaps.getEMC(Item.DIAMOND.id)) &&
				var2 < EEMaps.getEMC(Item.GOLD_INGOT.id) && !AnalyzeTier(items[eternalDensity], EEMaps.getEMC(Item.GOLD_INGOT.id)) &&
				var2 < EEMaps.getEMC(Item.IRON_INGOT.id))
				if (!AnalyzeTier(items[eternalDensity], EEMaps.getEMC(Item.IRON_INGOT.id)));
			
			/*
			if (var2 >= EEMaps.getEMC(EEItem.redMatter.id) || AnalyzeTier(items[eternalDensity], EEMaps.getEMC(EEItem.redMatter.id))
					|| var2 >= EEMaps.getEMC(EEItem.darkMatter.id) || AnalyzeTier(items[eternalDensity], EEMaps.getEMC(EEItem.darkMatter.id))
					|| var2 >= EEMaps.getEMC(Item.DIAMOND.id) || AnalyzeTier(items[eternalDensity], EEMaps.getEMC(Item.DIAMOND.id))
					|| var2 >= EEMaps.getEMC(Item.GOLD_INGOT.id) || AnalyzeTier(items[eternalDensity], EEMaps.getEMC(Item.GOLD_INGOT.id))
					|| var2 >= EEMaps.getEMC(Item.IRON_INGOT.id) || !AnalyzeTier(items[eternalDensity], EEMaps.getEMC(Item.IRON_INGOT.id))) ;
			*/
		}
	}

	private boolean AnalyzeTier(ItemStack var1, int var2) {
		if (var1 == null) {
			return false;
		}

		int var3 = 0;

		for (int var4 = 0; var4 < items.length; var4++) {
			if (items[var4] == null || !isValidMaterial(items[var4])) continue;
			int emc = EEMaps.getEMC(items[var4]);
			if (emc < var2) {
				var3 += emc * items[var4].count;
			}
		}

		if (var3 + emc(var1) < var2) {
			return false;
		}

		int var4 = 0;

		while (var3 + emc(var1) >= var2 && var4 < 10) {
			var4++;
			ConsumeMaterialBelowTier(var1, var2);
		}

		if (emc(var1) >= var2 && roomFor(getProduct(var2))) {
			PushStack(getProduct(var2));
			takeEMC(var1, var2);
		}

		return true;
	}

	private boolean roomFor(ItemStack var1) {
		if (var1 == null) return false;

		for (int var2 = 0; var2 < items.length; var2++) {
			if (items[var2] == null) {
				return true;
			}

			if (items[var2].doMaterialsMatch(var1) && items[var2].count <= var1.getMaxStackSize() - var1.count) {
				return true;
			}
		}

		return false;
	}

	private ItemStack getProduct(int var1) {
		if (var1 != EEMaps.getEMC(Item.IRON_INGOT.id)){
			if (var1 != EEMaps.getEMC(Item.GOLD_INGOT.id)){
				if (var1 != EEMaps.getEMC(Item.DIAMOND.id)){
					if (var1 != EEMaps.getEMC(EEItem.darkMatter.id)){
						if (var1 != EEMaps.getEMC(EEItem.redMatter.id)){
							return null;
						}
						else return new ItemStack(EEItem.redMatter, 1);
					}
					else return new ItemStack(EEItem.darkMatter, 1);
				}
				else return new ItemStack(Item.DIAMOND, 1);
			}
			else return new ItemStack(Item.GOLD_INGOT, 1);
		}
		else return new ItemStack(Item.IRON_INGOT, 1);
		
		/*
		return var1 == EEMaps.getEMC(EEItem.redMatter.id) ? new ItemStack(EEItem.redMatter, 1) :
					var1 == EEMaps.getEMC(EEItem.darkMatter.id) ? new ItemStack(EEItem.darkMatter, 1) :
						var1 == EEMaps.getEMC(Item.DIAMOND.id) ? new ItemStack(Item.DIAMOND, 1) :
							var1 == EEMaps.getEMC(Item.GOLD_INGOT.id) ? new ItemStack(Item.GOLD_INGOT, 1) :
								var1 == EEMaps.getEMC(Item.IRON_INGOT.id) ? new ItemStack(Item.IRON_INGOT, 1) :
									null;
								*/
	}

	private void ConsumeMaterialBelowTier(ItemStack var1, int var2) {
		for (int var3 = 0; var3 < items.length; var3++) {
			if (items[var3] == null || !isValidMaterial(items[var3])) continue;
			int emc = EEMaps.getEMC(items[var3]);
			if (emc < var2) {
				addEMC(var1, emc);
				if (items[var3].count <= 1) items[var3] = null;
				else items[var3].count--;

				return;
			}
		}
	}

	private boolean isValidMaterial(ItemStack var1) {
		if (var1 == null) return false;
		
		if (EEMaps.getEMC(var1) == 0) return false;
		
		if (var1.getItem() instanceof ItemKleinStar) {
			return false;
		}

		int var2 = var1.id;
		return var2 != EEItem.redMatter.id ? var2 >= Block.byId.length || !(Block.byId[var2] instanceof BlockContainer) || !Block.byId[var2].hasTileEntity(var1.getData()) : false;
	}

	private int emc(ItemStack var1) {
		if (var1.getItem() instanceof ItemEternalDensity)
			return ((ItemEternalDensity) var1.getItem()).getInteger(var1, "emc");
		else if (var1.getItem() instanceof ItemVoidRing)
			return ((ItemVoidRing) var1.getItem()).getInteger(var1, "emc");
		else
			return 0;
	}

	private void takeEMC(ItemStack var1, int var2) {
		if (var1.getItem() instanceof ItemEternalDensity) {
			((ItemEternalDensity) var1.getItem()).setInteger(var1, "emc", emc(var1) - var2);
		} else if (var1.getItem() instanceof ItemVoidRing){
			((ItemVoidRing) var1.getItem()).setInteger(var1, "emc", emc(var1) - var2);
		}
	}

	private void addEMC(ItemStack var1, int var2) {
		if (var1.getItem() instanceof ItemEternalDensity) {
			((ItemEternalDensity) var1.getItem()).setInteger(var1, "emc", emc(var1) + var2);
		} else if (var1.getItem() instanceof ItemVoidRing){
			((ItemVoidRing) var1.getItem()).setInteger(var1, "emc", emc(var1) + var2);
		}
	}

	public void q_() {
		if ((++ticksSinceSync % 20) * 4 == 0) {
			world.playNote(x, y, z, 1, numUsingPlayers);
		}

		prevLidAngle = lidAngle;
		float var1 = 0.1F;

		if (numUsingPlayers > 0 && lidAngle == 0.0F) {
			double var4 = x + 0.5D;
			double var2 = z + 0.5D;
			world.makeSound(var4, y + 0.5D, var2, "random.chestopen", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		}

		if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F) {
			float var8 = lidAngle;

			if (numUsingPlayers > 0) {
				lidAngle += var1;
			} else {
				lidAngle -= var1;
			}

			if (lidAngle > 1.0F) {
				lidAngle = 1.0F;
			}

			float var5 = 0.5F;

			if (lidAngle < var5 && var8 >= var5) {
				double var2 = x + 0.5D;
				double var6 = z + 0.5D;
				world.makeSound(var2, y + 0.5D, var6, "random.chestclosed", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (lidAngle < 0.0F) {
				lidAngle = 0.0F;
			}
		}

		if (repairOn) {
			doRepair();
		}

		if (attractionOn) {
			doAttraction();
		}

		if (condenseOn && eternalDensity >= 0) {
			doCondense(items[eternalDensity]);
		}
	}

	private void doAttraction() {
		List<Entity> var1 = world.a(EntityLootBall.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
		Iterator<Entity> var3 = var1.iterator();

		while (var3.hasNext()) {
			Entity var2 = var3.next();
			PullItems(var2);
		}

		// IMPORTANT Double???
		List<Entity> var12 = world.a(EntityLootBall.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
		Iterator<Entity> var5 = var12.iterator();

		while (var5.hasNext()) {
			Entity var4 = var5.next();
			PullItems(var4);
		}

		List<Entity> var13 = world.a(EntityItem.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
		Iterator<Entity> var7 = var13.iterator();

		while (var7.hasNext()) {
			Entity var6 = var7.next();
			PullItems(var6);
		}

		List<Entity> var14 = world.a(EntityLootBall.class, AxisAlignedBB.b(x - 0.5D, y - 0.5D, z - 0.5D, x + 1.25D, y + 1.25D, z + 1.25D));
		Iterator<Entity> var9 = var14.iterator();

		while (var9.hasNext()) {
			Entity var8 = var9.next();
			GrabItems(var8);
		}

		List<Entity> var15 = world.a(EntityItem.class, AxisAlignedBB.b(x - 0.5D, y - 0.5D, z - 0.5D, x + 1.25D, y + 1.25D, z + 1.25D));
		Iterator<Entity> var11 = var15.iterator();

		while (var11.hasNext()) {
			Entity var10 = var11.next();
			GrabItems(var10);
		}
	}

	public boolean PushStack(EntityItem var1) {
		if (var1 == null || var1.dead) return false;
		
		if (var1.itemStack == null) {
			var1.die();
			return false;
		}
		if (var1.itemStack.count < 1) {
			var1.die();
			return false;
		}

		for (int var2 = 0; var2 < items.length; var2++) {
			if (items[var2] == null) {
				items[var2] = var1.itemStack.cloneItemStack();

				for (items[var2].count = 0; var1.itemStack.count > 0 && items[var2].count < items[var2].getMaxStackSize(); var1.itemStack.count--) {
					items[var2].count++;
				}

				var1.die();
				return true;
			}

			if (items[var2].doMaterialsMatch(var1.itemStack) && items[var2].count <= var1.itemStack.getMaxStackSize() - var1.itemStack.count) {
				while (var1.itemStack.count > 0 && items[var2].count < items[var2].getMaxStackSize()) {
					items[var2].count++;
					var1.itemStack.count--;
				}

				var1.die();
				return true;
			}
		}

		return false;
	}

	public boolean PushStack(ItemStack var1) {
		if (var1 == null) return false;

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

	private ItemStack[] PushDenseStacks(ItemStack[] var1) {
		for (int var2 = 0; var2 < var1.length; var2++) {
			if (var1[var2] != null && PushStack(var1[var2])) {
				var1[var2] = null;
			}
		}
		return var1;
	}

	private void GrabItems(Entity var1) {
		if (var1 == null || var1.dead) return;
		//if (!grabbing.add(var1)) return;
		if (var1 instanceof EntityItem) {
			if (((EntityItem) var1).itemStack == null) {
				var1.die();
				return;
			}
			ItemStack var9 = ((EntityItem) var1).itemStack.cloneItemStack();
			((EntityItem) var1).itemStack = null;

			if (var9.getItem() instanceof ItemLootBall) {
				ItemLootBall var3 = (ItemLootBall) var9.getItem();
				//if (var3.isGrabbed) return;
				//var3.isGrabbed = true;
				ItemStack[] var4 = var3.getDroplist(var9);
				ItemStack[] var5 = var4;
				int var6 = var4.length;

				for (int var7 = 0; var7 < var6; var7++) {
					ItemStack var8 = var5[var7];
					PushStack(var8);
				}

				var1.die();
			} else {
				if (PushStack(var9)) var1.die();
				else {
					((EntityItem) var1).itemStack = var9;
					var1.dead = false;
				}
			}
		} else if (var1 instanceof EntityLootBall) {
			if (((EntityLootBall) var1).items == null) {
				var1.die();
				return;
			}

			ItemStack[] var2 = ((EntityLootBall) var1).items;
			((EntityLootBall) var1).items = PushDenseStacks(var2);

			if (((EntityLootBall) var1).isEmpty()) {
				var1.die();
			}
		}
	}

	private void PullItems(Entity var1) {
		if (var1 instanceof EntityItem || var1 instanceof EntityLootBall) {
			if (var1 instanceof EntityLootBall) ((EntityLootBall) var1).setBeingPulled(true);
			

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

	public boolean isInterdicting() {
		return interdictionOn;
	}

	@SuppressWarnings("unused")
	private void PushEntities(Entity var1, int var2, int var3, int var4) {
		if (!(var1 instanceof EntityHuman) && !(var1 instanceof EntityItem)) {
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

	public void b(int var1, int var2) {
		if (var1 == 1) {
			numUsingPlayers = var2;
		}
	}
	

	public void f() {
		numUsingPlayers++;
		world.playNote(x, y, z, 1, numUsingPlayers);
	}
	

	public void g() {
		numUsingPlayers--;
		world.playNote(x, y, z, 1, numUsingPlayers);
	}
	
	/**
	 * Gets tile entity
	 */
	public boolean a(EntityHuman var1) {
		return world.getTileEntity(x, y, z) == this ? var1.e(x + 0.5D, y + 0.5D, z + 0.5D) <= 64D : false;
	}
	
	public int getStartInventorySide(int var1) {
		return 0;
	}

	public int getSizeInventorySide(int var1) {
		return getSize();
	}

	public boolean onBlockActivated(EntityHuman var1) {
		if (!world.isStatic) {
			if (removed) return false;
			var1.openGui(mod_EE.getInstance(), GuiIds.ALCH_CHEST, world, x, y, z);
		}

		return true;
	}

	@SuppressWarnings("null")
	public void onBlockRemoval() {
		for (HumanEntity h : this.getViewers()) h.closeInventory();
		removed = true;
		for (int var1 = 0; var1 < getSize(); var1++) {
			ItemStack var2 = getItem(var1);

			if (var2 != null) {
				float var3 = world.random.nextFloat() * 0.8F + 0.1F;
				float var4 = world.random.nextFloat() * 0.8F + 0.1F;
				float var5 = world.random.nextFloat() * 0.8F + 0.1F;

				while (var2.count > 0) {
					int var6 = world.random.nextInt(21) + 10;

					if (var6 > var2.count) {
						var6 = var2.count;
					}

					var2.count -= var6;
					EntityItem var7 = new EntityItem(world, x + var3, y + var4, z + var5, new ItemStack(var2.id, var6, var2.getData()));

					if (var7 != null) {
						float var8 = 0.05F;
						var7.motX = (float) world.random.nextGaussian() * var8;
						var7.motY = (float) world.random.nextGaussian() * var8 + 0.2F;
						var7.motZ = (float) world.random.nextGaussian() * var8;

						if (var7.itemStack.getItem() instanceof ItemKleinStar) {
							((ItemKleinStar) var7.itemStack.getItem()).setKleinPoints(var7.itemStack, ((ItemKleinStar) var2.getItem()).getKleinPoints(var2));
						}

						world.addEntity(var7);
					}
				}
			}
		}
	}

	public int getTextureForSide(int var1) {
		if (var1 != 1 && var1 != 0) {
			byte var2 = direction;
			return var1 != var2 ? EEBase.alchChestSide : EEBase.alchChestFront;
		}

		return EEBase.alchChestTop;
	}
	
	public int getInventoryTexture(int var1) {
		return var1 == 3 ? EEBase.alchChestFront : var1 == 1 ? EEBase.alchChestTop : EEBase.alchChestSide;
	}

	public int getLightValue() {
		return isInterdicting() ? 15 : 0;
	}

	public void onNeighborBlockChange(int var1) {}

	public ItemStack splitWithoutUpdate(int var1) {
		return null;
	}

	public ItemStack[] getContents() {
		if (removed) return new ItemStack[113];
		return items;
	}

	public void setMaxStackSize(int size) {}
}