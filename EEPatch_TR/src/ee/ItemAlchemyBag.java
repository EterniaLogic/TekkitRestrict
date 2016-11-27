package ee;

import net.minecraft.server.EEProxy;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ModLoader;
import net.minecraft.server.World;
import net.minecraft.server.mod_EE;
import ee.core.GuiIds;

public class ItemAlchemyBag extends ItemEECharged {
	public static MinecraftServer mc = ModLoader.getMinecraftServerInstance();
	public static final String prefix = "bag";
	public static final String prefix_ = "bag_";

	public ItemAlchemyBag(int var1) {
		super(var1, 0);
		maxStackSize = 1;
		a(true);
	}

	public int getIconFromDamage(int color) {
		return color >= 0 && color < 16 ? textureId + color : textureId;
	}

	public ItemStack a(ItemStack item, World world, EntityHuman human) {
		if (ModLoader.getMinecraftServerInstance() != null) {
			if (EEPatch.allowAlcBags) human.openGui(mod_EE.getInstance(), GuiIds.ALCH_BAG, world, item.getData(), (int) human.locY, (int) human.locZ);
		}

		return item;
	}

	@SuppressWarnings("unused")
	private AlchemyBagData getBagData(ItemStack item, World world) {
		String datName = "bag_global_" + item.getData();
		AlchemyBagData bag = (AlchemyBagData) world.a(AlchemyBagData.class, datName);

		if (bag == null) {
			bag = new AlchemyBagData(datName);
			bag.a();
			world.a(datName, bag);
		}

		return bag;
	}

	public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7) {
		if (ModLoader.getMinecraftServerInstance() != null) {
			if (EEPatch.allowAlcBags) var2.openGui(mod_EE.getInstance(), GuiIds.ALCH_BAG, var3, var1.getData(), (int) var2.locY, (int) var2.locZ);
		}

		return true;
	}

	public void doChargeTick(ItemStack var1, World var2, EntityHuman var3) {}

	public void doToggle(ItemStack var1, World var2, EntityHuman var3) {}

	public void doUncharge(ItemStack var1, World var2, EntityHuman var3) {}

	public void doAlternate(ItemStack item, World world, EntityHuman human) {
		if (ModLoader.getMinecraftServerInstance() == null) return;
		if (EEPatch.allowAlcBags) human.openGui(mod_EE.getInstance(), GuiIds.ALCH_BAG, world, item.getData(), (int) human.locY, (int) human.locZ);
	}

	public void a(ItemStack item, World world, Entity entity, int unused1, boolean unused2) {
		if (EEProxy.isClient(world) || !(entity instanceof EntityHuman)) return;
		if (!EEPatch.allowAlcBags) return;
		EntityHuman human = (EntityHuman) entity;
		
		String datName = EEPatch.getBag(human, world, item);
//		String datName;
//		
//		if (item.getData() > EEPatch.alcBagAmount){
//			human.a("You are not allowed to have more than " + (EEPatch.alcBagAmount+1) + " different bags!");
//			item.setData(EEPatch.alcBagAmount);
//		}
//		
//		if (EEPatch.separateAlcBags){
//			if (EEPatch.sharedWorlds.contains(world.worldData.name.toLowerCase()))
//				datName = "bag_"+EEPatch.mainSharedWorld+"_"+human.name+item.getData();
//			else
//				datName = "bag_" + world.worldData.name + "_" + human.name + item.getData();
//		} else {
//			datName = "bag_" + human.name + item.getData();
//		}
		
		AlchemyBagData bag = (AlchemyBagData) world.a(AlchemyBagData.class, datName);
		
		if (bag == null) {
			bag = new AlchemyBagData(datName);
			bag.a();
			world.a(datName, bag);
		}

		bag.onUpdate(world, human);
	}

	public static AlchemyBagData getBagData(ItemStack item, EntityHuman human, World world) {
		String datName = EEPatch.getBag(human, world, item);
//		String datName;
//		
//		if (!EEPatch.allowAlcBags){
//			datName = "bag_global";
//		} else if (item.getData() > EEPatch.alcBagAmount){
//			human.a("You are not allowed to have more than " + (EEPatch.alcBagAmount+1) + " different bags!");
//			item.setData(EEPatch.alcBagAmount);
//			if (EEPatch.separateAlcBags){
//				if (EEPatch.sharedWorlds.contains(world.worldData.name.toLowerCase()))
//					datName = "bag_"+EEPatch.mainSharedWorld+"_"+human.name+item.getData();
//				else
//					datName = "bag_" + world.worldData.name + "_" + human.name + item.getData();
//			} else {
//				datName = "bag_" + human.name + item.getData();
//			}
//		} else {
//			if (EEPatch.separateAlcBags){
//				if (EEPatch.sharedWorlds.contains(world.worldData.name.toLowerCase()))
//					datName = "bag_" + EEPatch.mainSharedWorld + "_" + human.name + item.getData();
//				else
//					datName = "bag_" + world.worldData.name + "_" + human.name + item.getData();
//			} else {
//				datName = "bag_" + human.name + item.getData();
//			}
//		}
		
		AlchemyBagData bag = (AlchemyBagData) world.a(AlchemyBagData.class, datName);

		if (bag == null) {
			bag = new AlchemyBagData(datName);
			bag.a();
			world.a(datName, bag);
		}

		return bag;
	}

	public static AlchemyBagData getBagData(int color, EntityHuman human, World world) {
		String datName = EEPatch.getBag(human, world, color);
		
		//String name = human.name;
//		String datName; //= "bag_" + name + color;
//		if (!EEPatch.allowAlcBags){
//			datName = "bag_global";
//		} else if (color > EEPatch.alcBagAmount) {
//			human.a("You are not allowed to have more than " + (EEPatch.alcBagAmount+1) + " different bags!");
//			color = EEPatch.alcBagAmount;
//			
//			if (EEPatch.separateAlcBags){
//				if (EEPatch.sharedWorlds.contains(world.worldData.name.toLowerCase()))
//					datName = "bag_" + EEPatch.mainSharedWorld + "_" + human.name + color;
//				else
//					datName = "bag_" + world.worldData.name + "_" + human.name + color;
//			} else {
//				datName = "bag_" + human.name + color;
//			}
//		} else {
//			if (EEPatch.separateAlcBags){
//				if (EEPatch.sharedWorlds.contains(world.worldData.name.toLowerCase()))
//					datName = "bag_" + EEPatch.mainSharedWorld + "_" + human.name + color;
//				else
//					datName = "bag_" + world.worldData.name + "_" + human.name + color;
//			} else {
//				datName = "bag_" + human.name + color;
//			}
//		}
		AlchemyBagData bag = (AlchemyBagData) world.a(AlchemyBagData.class, datName);

		if (bag == null) {
			bag = new AlchemyBagData(datName);
			bag.a();
			world.a(datName, bag);
		}

		return bag;
	}

	/** Creates a new bag. If it already exists, this does nothing. */
	public void d(ItemStack item, World world, EntityHuman human) {
		if (EEProxy.isClient(world)) return;
		if (!EEPatch.allowAlcBags) return;
		
		String datName = EEPatch.getBag(human, world, item);
//		if (item.getData() > EEPatch.alcBagAmount){
//			human.a("You are not allowed to have more than " + (EEPatch.alcBagAmount+1) + " different bags!");
//			item.setData(EEPatch.alcBagAmount);
//		}
//
//		String datName;
//		if (EEPatch.separateAlcBags){
//			if (EEPatch.sharedWorlds.contains(world.worldData.name.toLowerCase()))
//				datName = "bag_" + EEPatch.mainSharedWorld + "_" + human.name + item.getData();
//			else
//				datName = "bag_" + world.worldData.name + "_" + human.name + item.getData();
//		} else {
//			datName = "bag_" + human.name + item.getData();
//		}

		AlchemyBagData bag = (AlchemyBagData) world.a(AlchemyBagData.class, datName);

		if (bag != null) return;
		bag = new AlchemyBagData(datName);
		world.a(datName, bag);
		bag.a();
	}
}
