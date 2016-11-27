/* Warning: No line numbers available in class file */
package ee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

import net.minecraft.server.EEProxy;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.IInventory;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.World;
import net.minecraft.server.WorldMapBase;

public class TransTabletData extends WorldMapBase implements IInventory {
	public int latentEnergy = 0;
	public int currentEnergy = 0;
	public int learned = 0;
	public ItemStack[] items = new ItemStack[26];
	public boolean isMatterLocked;
	public boolean isFuelLocked;
	public EntityHuman player;
	private boolean readTome;
	private HashMap<Integer, int[]> knowledge = new HashMap<Integer, int[]>();
	public static List<TransTabletData> datas = new LinkedList<TransTabletData>();

	public TransTabletData(String var1) {
		super(var1);
		datas.add(this);
	}

	public void onUpdate(World var1, EntityHuman var2) {
		if (EEProxy.isClient(var1)) return;
		if (player == null) player = var2;

		if (currentEnergy + latentEnergy == 0) {
			unlock();
		}

		calculateEMC();
		displayResults(currentEnergy + latentEnergy);
	}

	public ItemStack target() {
		return items[8];
	}
	
	private ItemStack target = null;
	private int targetemc = -1;
	public int targetEMC(){
		if (target != target()) target = target();
		if (target == null) return 0;
		return targetemc==-1?(targetemc=EEMaps.getEMC(target)):targetemc;
		
	}

	public boolean isOnGridBut(ItemStack var1, int var2) {
		for (int var3 = 10; var3 < items.length; var3++) {
			if (var3 != var2 && items[var3] != null && items[var3].doMaterialsMatch(var1)) {
				return true;
			}
		}

		return false;
	}

	public boolean isOnGrid(ItemStack var1) {
		for (int var2 = 10; var2 < items.length; var2++) {
			if (items[var2] != null && items[var2].doMaterialsMatch(var1)) {
				return true;
			}
		}

		return false;
	}

	public int kleinEMCTotal() {
		int var1 = 0;

		for (int var2 = 0; var2 < 8; var2++) {
			if (items[var2] == null || !(items[var2].getItem() instanceof ItemKleinStar)) continue;
			var1 += ((ItemKleinStar) items[var2].getItem()).getKleinPoints(items[var2]);
		}

		return var1;
	}

	private int lastEmc = -1, lastId = -1;
	public void displayResults(int emc) {
		final int newid = (items[9] == null?0:items[9].id);
		if (lastEmc == emc && lastId == newid) return;

		lastEmc = emc;
		lastId = newid;//0
		
		//clear grid
		for (int var2 = 10; var2 < items.length; var2++){
			items[var2] = null;
		}
		final int targetemc = (target() != null? targetEMC() : -1);
		for (int var2 = 10; var2 < items.length; var2++) {
			int temc;
			if (items[var2] != null && (emc < (temc = EEMaps.getEMC(items[var2])) || !matchesLock(items[var2]) || isOnGridBut(items[var2], var2) || target() != null && temc > targetemc)) {
				items[var2] = null;
			}

			if (var2 == 9 && target() != null && targetemc > 0 && emc > targetemc && matchesLock(target())) {
				items[var2] = new ItemStack(target().id, 1, target().getData());
			}

			if (var2 == 10 && target() != null && targetemc > 0 && emc >= targetemc && matchesLock(target())) {
				items[10] = new ItemStack(target().id, 1, target().getData());
			}

			for (int var3 = 0; var3 < Item.byId.length; ++var3) {
				if (Item.byId[var3] == null) continue;
				int var4 = EEMaps.getMeta(var3);

				for (int var5 = 0; var5 <= var4; var5++) {
					ItemStack var6 = new ItemStack(var3, 1, var5);

					if (isOnGrid(var6) || !matchesLock(var6)) continue;
					final int var7 = EEMaps.getEMC(var6);

					if (var7 == 0 || target() != null && var7 > targetemc || !playerKnows(var6.id, var6.getData()) || emc < var7 || var7 <= EEMaps.getEMC(getItem(var2))) continue;
					items[var2] = new ItemStack(var3, 1, var5);
				}

			}

		}

		update();
	}
	
	public void quickCalculateEMC(){
		int var1 = 0;
		for (int var3 = 0; var3 < 8; var3++) {
			ItemStack itemstack = items[var3];
			if (itemstack == null) continue;
			if (!(itemstack.getItem() instanceof ItemKleinStar))
					var1 += EEMaps.getEMC(itemstack);
		}
		currentEnergy = var1;
	}

	public void calculateEMC() {
		int var1 = 0;
		boolean var2 = false;

		for (int var3 = 0; var3 < 8; var3++) {
			ItemStack itemstack = items[var3];
			if (itemstack == null) continue;
			
			int emc = EEMaps.getEMC(itemstack);
			if (emc == 0 && !EEBase.isKleinStar(itemstack)) {
				rejectItem(var3);
			} else if (EEBase.isKleinStar(itemstack)) {
				if (!playerKnows(itemstack.id, itemstack.getData()) && emc > 0) {
					pushKnowledge(itemstack.id, itemstack.getData());
					learned = 60;
				}

				if (latentEnergy > 0) {
					int var4 = ((ItemKleinStar) itemstack.getItem()).getMaxPoints(itemstack)
							- ((ItemKleinStar) itemstack.getItem()).getKleinPoints(itemstack);

					if (var4 > 0) {
						if (var4 > latentEnergy) var4 = latentEnergy;

						latentEnergy -= var4;
						EEBase.addKleinStarPoints(items[var3], var4);
					}
				}

				var1 += ((ItemKleinStar) itemstack.getItem()).getKleinPoints(itemstack);
			} else {
				if (!playerKnows(itemstack.id, itemstack.getData()) && emc > 0) {
					if (itemstack.id == EEItem.alchemyTome.id) pushTome();

					pushKnowledge(itemstack.id, itemstack.getData());
					learned = 60;
				}

				if (!var2 && !isFuelLocked() && !isMatterLocked()) {
					if (EEMaps.isFuel(itemstack)) {
						fuelLock();
					} else {
						matterLock();
					}
				}

				if (!matchesLock(itemstack)) {
					rejectItem(var3);
				} else {
					var1 += emc;
				}
			}

		}

		currentEnergy = var1;
	}

	public boolean matchesLock(ItemStack var1) {
		if (isFuelLocked) {
			if (EEMaps.isFuel(var1.id, var1.getData())) return true;
		} else {
			if (!isMatterLocked) return true;

			if (!EEMaps.isFuel(var1.id, var1.getData())) return true;
		}

		return false;
	}

	public int getSize() {
		return items.length;
	}

	public ItemStack getItem(int var1) {
		return items[var1];
	}

	public ItemStack splitStack(int var1, int var2) {
		if (items[var1] != null) {
			if (items[var1].count <= var2) {
				ItemStack var3 = items[var1];
				items[var1] = null;
				update();
				return var3;
			}

			ItemStack var3 = items[var1].a(var2);

			if (items[var1].count == 0) {
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
		return "Trans Tablet";
	}

	public int getMaxStackSize() {
		return 64;
	}

	public void update() {
		a();
	}

	public boolean a(EntityHuman var1) {
		return true;
	}

	public void f() {}

	public void g() {}

	public void a(NBTTagCompound var1) {
		isMatterLocked = var1.getBoolean("matterLock");
		isFuelLocked = var1.getBoolean("fuelLock");
		currentEnergy = var1.getInt("currentEnergy");
		latentEnergy = var1.getInt("latentEnergy");
		NBTTagList var2 = var1.getList("Items");
		items = new ItemStack[getSize()];

		for (int var3 = 0; var3 < var2.size(); ++var3) {
			NBTTagCompound var4 = (NBTTagCompound) var2.get(var3);
			int var5 = var4.getByte("Slot") & 0xFF;

			if (var5 < 0 || var5 >= items.length) continue;
			items[var5] = ItemStack.a(var4);
		}
		
		readTome = var1.getBoolean("readTome");

		NBTTagList var8 = var1.getList("knowledge");
		knowledge = new HashMap<Integer, int[]>();

		for (int var9 = 0; var9 < var8.size(); ++var9) {
			NBTTagCompound var10 = (NBTTagCompound) var8.get(var9);
			int var6 = var10.getInt("item");
			int var7 = var10.getInt("meta");
			pushKnowledge(var6, var7);
		}
	}

	public void b(NBTTagCompound var1) {
		var1.setBoolean("matterLock", isMatterLocked);
		var1.setBoolean("fuelLock", isFuelLocked);
		var1.setInt("currentEnergy", currentEnergy);
		var1.setInt("latentEnergy", latentEnergy);
		NBTTagList var2 = new NBTTagList();

		for (int var3 = 0; var3 < items.length; var3++) {
			if (items[var3] == null) continue;
			NBTTagCompound var4 = new NBTTagCompound();
			var4.setByte("Slot", (byte) var3);
			items[var3].save(var4);
			var2.add(var4);
		}

		var1.set("Items", var2);
		var1.setBoolean("readTome", readTome);

		for (int var3 = 0; var3 < knowledge.size(); var3++) {
			int[] vals = knowledge.get(var3);
			if (vals == null) continue;
			NBTTagCompound var4 = new NBTTagCompound();
			var4.setInt("item", vals[0]);
			var4.setInt("meta", vals[1]);
			var2.add(var4);
		}

		var1.set("knowledge", var2);
	}

	public HashMap<Integer, int[]> getKnowledge() {
		return knowledge;
	}

	public void pushKnowledge(int id, int data) {
		Item item = Item.byId[id];
		if (item == null) return;
		if (item.g()) data = 0;
		
		if (playerKnows(id, data)) return;

		int var3 = 0;
		for (; knowledge.get(var3) != null; var3++);
		knowledge.put(var3, new int[] {id, data});
		a();
	}

	public boolean playerKnows(int id, int data) {
		if (readTome) return true;

		ItemStack var3 = new ItemStack(id, 1, data);

		if (var3.d()) data = 0;
		
		/*
		int i = 0;
		while ((knows = knowledge.get(i)) != null){
			if (knows[0].intValue() == var1 && knows[1].intValue() == var2) return true;
			i++;
		}
		*/
		
		int[] knows;
		for (int i = 0; (knows = knowledge.get(i)) != null; i++) {
			if (knows[0] == id && knows[1] == data) return true;
		}

		return false;
	}

	public void pushTome() {
		readTome = true;
		a();
	}

	public long getDisplayEnergy() {
		return latentEnergy + currentEnergy;
	}

	public int getLatentEnergy() {
		return latentEnergy;
	}

	public void setLatentEnergy(int var1) {
		latentEnergy = var1;
		a();
	}

	public int getCurrentEnergy() {
		return currentEnergy;
	}

	public void setCurrentEnergy(int var1) {
		currentEnergy = var1;
		a();
	}

	public boolean isFuelLocked() {
		return isFuelLocked;
	}

	public void fuelUnlock() {
		isFuelLocked = false;
		a();
	}

	public void fuelLock() {
		isFuelLocked = true;
		a();
	}

	public boolean isMatterLocked() {
		return isMatterLocked;
	}

	public void matterUnlock() {
		isMatterLocked = false;
		a();
	}

	public void matterLock() {
		isMatterLocked = true;
		a();
	}

	public void unlock() {
		fuelUnlock();
		matterUnlock();
	}

	public void rejectItem(int var1) {
		if (player == null || player.world == null || EEProxy.isClient(player.world) || getItem(var1) == null) return;
		EntityItem var2 = new EntityItem(player.world, player.locX, player.locY - 0.5D, player.locZ, getItem(var1));
		nullStack(var1);
		var2.pickupDelay = 1;
		player.world.addEntity(var2);
	}

	private void nullStack(int var1) {
		items[var1] = null;
		a();
	}

	public ItemStack splitWithoutUpdate(int var1) {
		if (var1 <= 8) {
			if (items[var1] != null) {
				ItemStack var2 = items[var1];
				items[var1] = null;
				return var2;
			}

			return null;
		}

		return null;
	}

	public ItemStack[] getContents() {
		return items;
	}

	private ArrayList<HumanEntity> transaction = new ArrayList<HumanEntity>(2);
	public void onOpen(CraftHumanEntity who) {
		lastEmc = -1;
		lastId = -1;
		transaction.add(who);
	}

	public void onClose(CraftHumanEntity who) {
		transaction.remove(who);
	}

	public List<HumanEntity> getViewers() {
		return transaction;
		//return new ArrayList<HumanEntity>(0);
	}

	public InventoryHolder getOwner() {
		return player == null ? null : player.getBukkitEntity();
	}

	public void setMaxStackSize(int size) {}
}

/* Location:           C:\Program Files\eclipse_Kepler\lib\Tekkit_Classic\mods\EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
 * Qualified Name:     ee.TransTabletData
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */
