package ee;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.server.*;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import ee.events.EEChargeEvent;
import ee.events.EEEventManager;

public abstract class ItemEECharged extends ItemModEE
{
	protected int weaponDamage;
	private int maxCharge;
	
	protected ItemEECharged(int var1, int var2)
	{
		super(var1);
		maxStackSize = 1;
		maxCharge = var2;
		if(var2 == 0)
			setMaxDurability(0);
		else
			setMaxDurability(10 * var2 + 1 + (canActivate2() ? 2 : ((int) (canActivate() ? 1 : 0))) << 1 * (canActivate2() ? 2 : ((int) (canActivate() ? 1 : 0))));
		weaponDamage = 1;
	}
	
	public void ConsumeReagent(ItemStack var1, EntityHuman var2, boolean var3)
	{
		EEBase.ConsumeReagent(var1, var2, var3);
	}

	public void setFuelRemaining(ItemStack var1, int var2)
	{
		setShort(var1, "fuelRemaining", var2);
	}

	public int getFuelRemaining(ItemStack var1)
	{
		return getShort(var1, "fuelRemaining");
	}

	/** Passive item effect */
	public void doPassive(ItemStack itemstack1, World world1, EntityHuman entityhuman1) {}

	/** Item effect when enabled */
	public void doActive(ItemStack itemstack1, World world1, EntityHuman entityhuman1) {}

	/** Item effect when merely held. */
	public void doHeld(ItemStack itemstack1, World world1, EntityHuman entityhuman1) {}

	/** Effect when you press R with this item. */
	public void doRelease(ItemStack itemstack1, World world1, EntityHuman entityhuman1) {}

	/** Effect when you press the C key*/
	public void doAlternate(ItemStack itemstack1, World world1, EntityHuman entityhuman1) {}

	/** Effect when you left click with this item. */
	public void doLeftClick(ItemStack itemstack1, World world1, EntityHuman entityhuman1) {}

	public boolean isItemOut(ItemStack var1, EntityHuman var2)
	{
		return var1 == null ? false : EEBase.isCurrentItem(var1.getItem(), var2);
	}

	/** @return If the given item is on the players hotbar. */
	public boolean isItemEquipped(ItemStack var1, EntityHuman var2)
	{
		return var1 == null ? false : EEBase.isOnQuickBar(var1.getItem(), var2);
	}

	public int getMaxCharge()
	{
		return maxCharge;
	}

	/** When the charge key is pressed (V) */
	public void doCharge(ItemStack item, World world, EntityHuman human)
	{
		short old = getShort(item, "chargeGoal");
		if(old < maxCharge){
			setShort(item, "chargeGoal", old + 1);
		}
	}

	public void ejectDropList(World world, ItemStack var2, double var3, double var5, double var7)
	{
		ItemStack var9[] = getDroplist(var2);
		if(var9 == null) return;

		int var10 = 0;
		ItemStack var11[] = var9;
		int var12 = var9.length;
		for(int var13 = 0; var13 < var12; var13++)
		{
			ItemStack var14 = var11[var13];
			if(var14 != null)
				var10 += var14.count;
		}

		if(var10 != 0)
		{
			ItemStack var16 = new ItemStack(EEItem.lootBall);
			var16.setTag(new NBTTagCompound());
			cleanDroplist(var16);
			ItemStack var17[] = var9;
			int var13 = var9.length;
			for(int var18 = 0; var18 < var13; var18++)
			{
				ItemStack var15 = var17[var18];
				addToDroplist(var16, var15);
			}

			dropItemInWorld(world, var16, var3, var5, var7);
			cleanDroplist(var2);
		}
		
	}

	private static EntityItem dropItemInWorld(World var0, ItemStack var1, double var2, double var4, double var6)
	{
		if(var1 == null) return null;
		
		EntityItem var8 = new EntityItem(var0, var2, var4 + 0.5D, var6, var1);
		var8.pickupDelay = 40;
		float var9 = 0.1F;
		Random var11 = new Random();
		var9 = 0.3F;
		var8.motX = (float)(Math.random() * 0.20000000298023221D - 0.10000000149011611D);
		var8.motY = 0.20000000298023221D;
		var8.motZ = (float)(Math.random() * 0.20000000298023221D - 0.10000000149011611D);
		var9 = 0.02F;
		float var10 = var11.nextFloat() * 3.141593F * 2.0F;
		var9 *= var11.nextFloat();
		var8.motX += Math.cos(var10) * var9;
		var8.motY += (var11.nextFloat() - var11.nextFloat()) * 0.1F;
		var8.motZ += Math.sin(var10) * var9;
		var0.addEntity(var8);
		return var8;
	}

	public ItemStack[] getDroplist(ItemStack var1)
	{
		if(var1.tag == null)
			var1.setTag(new NBTTagCompound());
		if(!var1.tag.hasKey("droplist"))
			cleanDroplist(var1);
		NBTTagList var2 = var1.tag.getList("droplist");
		ItemStack var3[] = new ItemStack[var2.size()];
		for(int var4 = 0; var4 < var2.size(); var4++)
		{
			NBTTagCompound var5 = (NBTTagCompound)var2.get(var4);
			var3[var4] = ItemStack.a(var5);
		}

		return var3;
	}

	public void addToDroplist(ItemStack var1, ItemStack var2)
	{
		if(!var1.tag.hasKey("droplist"))
		{
			System.out.println("Forced to wipe droplist");
			cleanDroplist(var1);
		}
		NBTTagList var3 = var1.tag.getList("droplist");
		ArrayList<ItemStack> var4 = new ArrayList<ItemStack>();
		boolean var5 = false;
		for(int var6 = 0; var6 < var3.size(); var6++)
		{
			NBTTagCompound var7 = (NBTTagCompound)var3.get(var6);
			var4.add(ItemStack.a(var7));
		}

		ArrayList<ItemStack> var10 = sortArrayList(var4);
		for(int var11 = 0; var11 < var10.size() && var2 != null; var11++)
		{
			ItemStack current = var10.get(var11);
			if(current == null)
			{
				var10.set(var11, var2.cloneItemStack());
				var2 = null;
				var5 = true;
			}
			else if(current.doMaterialsMatch(var2))
			{
				if(current.count < current.getMaxStackSize())
				{
					/*while(((ItemStack)var10.get(var11)).count < ((ItemStack)var10.get(var11)).getMaxStackSize() && var2 != null) 
					{
						var10.set(var11, new ItemStack(((ItemStack)var10.get(var11)).id, ((ItemStack)var10.get(var11)).count + 1, ((ItemStack)var10.get(var11)).getData()));
						var2.count--;
						if(var2.count == 0)
						{
							var2 = null;
							var5 = true;
						}
					}*/
					do
					{
						var10.set(var11, new ItemStack(current.id, current.count + 1, current.getData()));
						current = var10.get(var11);
						var2.count--;

						if (var2.count == 0)
						{
							var2 = null;
							var5 = true;
						}
						if (current.count >= current.getMaxStackSize()) break;
					} while (var2 != null);
				}
				else
				{
					var10.add(var2);
					var2 = null;
					var5 = true;
				}
			}
			else if(!var5)
			{
				var10.add(var2);
				var2 = null;
				var5 = true;
			}
		}

		NBTTagList var12 = new NBTTagList();
		for(int var8 = 0; var8 < var10.size(); var8++)
			if(var10.get(var8) != null)
			{
				NBTTagCompound var9 = new NBTTagCompound();
				var10.get(var8).save(var9);
				var12.add(var9);
			}

		var1.tag.set("droplist", var12);
	}

	private ArrayList<ItemStack> sortArrayList(ArrayList<ItemStack> var1)
	{
		for(int var2 = 0; var2 < var1.size(); var2++)
		{
			ItemStack current1 = var1.get(var2);
			if(current1 != null)
			{
				for(int var3 = 0; var3 < var1.size(); var3++)
				{
					if(var2 != var3 && var1.get(var3) != null && current1.doMaterialsMatch(var1.get(var3)) && current1.count < current1.getMaxStackSize())
					{
						while(current1.count < current1.getMaxStackSize() && var1.get(var3) != null) 
						{
							var1.set(var2, new ItemStack(current1.id, current1.count + 1, current1.getData()));
							current1 = var1.get(var2);
							var1.set(var3, new ItemStack(var1.get(var3).id, var1.get(var3).count - 1, var1.get(var3).getData()));
							
							if(var1.get(var3).count == 0)
								var1.set(var3, null);
						}
					}
				}
			}
		}
		return var1;
	}

	public void cleanDroplist(ItemStack var1)
	{
		NBTTagList var2 = new NBTTagList();
		NBTTagCompound var3 = new NBTTagCompound();
		var2.add(var3);
		var1.tag.set("droplist", var2);
	}

	public void doChargeTick(ItemStack var1, World var2, EntityHuman var3)
	{
		if(chargeLevel(var1) < chargeGoal(var1)) {
			if(chargeTicks(var1) < 10)
			{
				setShort(var1, "chargeTicks", chargeTicks(var1) + 1);
				var2.makeSound(var3, "chargetick", 1.0F, 0.5F + (var1.i() - var1.getData()) / var1.i());
			}
			else
			{
				int lvl = chargeLevel(var1);
				if (EEEventManager.callEvent(new EEChargeEvent(var1, var3, lvl, lvl == maxCharge ? lvl : lvl+1, maxCharge))){
					setShort(var1, "chargeTicks", 0);
					setShort(var1, "chargeGoal", lvl);
					return;
				}
				setShort(var1, "chargeTicks", 0);
				setShort(var1, "chargeLevel", chargeLevel(var1) + 1);
				var2.makeSound(var3, "flash", 0.5F, 0.5F + chargeLevel(var1) / (var1.i() / 10));
			}
		}
		//var1.setData(var1.i() - (chargeLevel(var1) * 10 + chargeTicks(var1) << (canActivate2() ? 2 : ((canActivate() ? 1 : 0)))));
		var1.setData(var1.i() - (chargeLevel(var1) * 10 + chargeTicks(var1) << (canActivate() ? 1 : canActivate2() ? 2 : 0)));
		if(canActivate())
			if(isActivated(var1))
			{
				if((var1.getData() & 1) == 0)
					var1.setData(var1.getData() + 1);
			} else if((var1.getData() & 1) == 1)
				var1.setData(var1.getData() - 1);
		
		if(canActivate2())
			if(isActivated2(var1))
			{
				if((var1.getData() & 2) == 0)
					var1.setData(var1.getData() + 2);
			} else if((var1.getData() & 2) == 2)
				var1.setData(var1.getData() - 2);
	}

	/** When the charge key is pressed + shifting (shift + V) */
	public void doUncharge(ItemStack item, World var2, EntityHuman human)
	{
		int lvl = chargeLevel(item);
		if (EEEventManager.callEvent(new EEChargeEvent(item, human, lvl, lvl==0 ? 0 : lvl-1, maxCharge))) return;
		
		if(chargeLevel(item) > 0)//If level > 0
			setShort(item, "chargeLevel", chargeLevel(item) - 1);//Set charge down 1
		if(chargeGoal(item) > chargeLevel(item))//If supposed to charge to is bigger than the new level, set the chargegoal to the new chargelevel
			setShort(item, "chargeGoal", chargeLevel(item));
		if(chargeTicks(item) > 0)//If currently incrementing charge, stop it.
			setShort(item, "chargeTicks", 0);
		//var1.setData(var1.i() - (chargeLevel(var1) * 10 + chargeTicks(var1) << (canActivate2() ? 2 : ((int) (canActivate() ? 1 : 0))) + (isActivated(var1) ? 1 : 0) + (isActivated2(var1) ? 2 : 0)));
		//var2.makeSound(var3, "break", 0.5F, 0.5F + (float)chargeLevel(var1) / (float)(var1.i() / 10));
		item.setData(item.i() - (chargeLevel(item) * 10 + chargeTicks(item) << (canActivate() ? 1 : canActivate2() ? 2 : 0) + (isActivated(item) ? 1 : 0) + (isActivated2(item) ? 2 : 0)));
	    var2.makeSound(human, "break", 0.5F, 0.5F + chargeLevel(item) / (item.i() / 10));
	}

	public int chargeLevel(ItemStack var1)
	{
		return getShort(var1, "chargeLevel");
	}

	public int chargeTicks(ItemStack var1)
	{
		return getShort(var1, "chargeTicks");
	}

	public int chargeGoal(ItemStack var1)
	{
		return getShort(var1, "chargeGoal");
	}

	public void resetCharge(ItemStack var1, World var2, EntityHuman var3, boolean var4)
	{
		if(var1.d())
		{
			if(var4) var2.makeSound(var3, "break", 0.5F, 1.0F);
			var1.setData(0);
		}
	}

	public void a(ItemStack var1, World var2, Entity var3, int var4, boolean var5)
	{
		if(!EEProxy.isClient(var2) && (var3 instanceof EntityHuman) && var1 != null)
		{
			EntityHuman var6 = (EntityHuman)var3;
			if(maxCharge != 0) doChargeTick(var1, var2, var6);
		}
	}

	public boolean onItemUseFirst(ItemStack var1, EntityHuman var2, World var3, int i1, int j1, int k1, int l1)
	{
		return false;
	}

	/** The damage this item deals to the given entity. */
	public int a(Entity var1)
	{
		return weaponDamage;
	}

	public boolean a(ItemStack var1, EntityLiving var2, EntityLiving var3)
	{
		return super.a(var1, var2, var3);
	}

	/** When you press the g key */
	public void doToggle(ItemStack var1, World world, EntityHuman human) {
		if(isActivated(var1)) {
			var1.setData(var1.getData() - 1);
			var1.tag.setBoolean("active", false);
			world.makeSound(human, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		} else {
			var1.setData(var1.getData() + 1);
			var1.tag.setBoolean("active", true);
			world.makeSound(human, "heal", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		}
	}

	public void doToggle2(ItemStack var1, World var2, EntityHuman var3) {
		if(isActivated(var1.getData())) {
			var1.setData(var1.getData() - 2);
			var1.tag.setBoolean("active2", false);
			var2.makeSound(var3, "break", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		} else {
			var1.setData(var1.getData() + 2);
			var1.tag.setBoolean("active2", true);
			var2.makeSound(var3, "heal", 0.8F, 1.0F / (c.nextFloat() * 0.4F + 0.8F));
		}
	}

	public boolean canActivate()
	{
		return false;
	}

	public boolean canActivate2()
	{
		return false;
	}

	public boolean isActivated(int var1)
	{
		return (var1 & 1) == 1;
	}

	public boolean isActivated2(int var1)
	{
		return (var1 & 2) == 2;
	}

	public boolean isActivated(ItemStack var1)
	{
		return getBoolean(var1, "active");
	}

	public boolean isActivated2(ItemStack var1)
	{
		return getBoolean(var1, "active2");
	}

	public static boolean attemptBreak(EntityHuman player, int x, int y, int z)
	{
		if (player == null) return false;
		
		CraftWorld craftWorld = player.world.getWorld();
		CraftServer craftServer = player.world.getServer();
		Block block = craftWorld.getBlockAt(x, y, z);
		if(block == null) return false;
		Player ply = (Player) player.getBukkitEntity();
		//Player ply = craftServer.getPlayer((EntityPlayer)player);
		if(ply != null)
		{
			BlockBreakEvent event = new BlockBreakEvent(block, ply);
			craftServer.getPluginManager().callEvent(event);
			return !event.isCancelled();
		}
		return false;
	}

	public static boolean attemptPlace(EntityHuman player, int x, int y, int z)
	{
		if(player == null) return false;
		CraftWorld craftWorld = player.world.getWorld();
		CraftServer craftServer = player.world.getServer();
		Player ply = (Player) player.getBukkitEntity();

		BlockState state = CraftBlockState.getBlockState(player.world, x, y, z);
		Block placedagainst = craftWorld.getBlockAt(x, y - 1, z);
		
		if(ply != null)
		{
			BlockPlaceEvent event = new BlockPlaceEvent(state.getBlock(), state, placedagainst, ply.getItemInHand(), ply, true);
			craftServer.getPluginManager().callEvent(event);
			return !event.isCancelled();
		}
		return false;
	}
	
	public static boolean attemptIgnite(EntityHuman player, int x, int y, int z){
		
		if(player == null) return false;
		CraftWorld craftWorld = player.world.getWorld();
		CraftServer craftServer = player.world.getServer();
		Player ply = (Player) player.getBukkitEntity();

		Block placed = craftWorld.getBlockAt(x, y, z);
		if(ply != null)
		{
			//BlockIgniteEvent event = new BlockIgniteEvent(placed, IgniteCause.FLINT_AND_STEEL, ply);
			//craftServer.getPluginManager().callEvent(event);
			//if (!event.isCancelled()){
				Block placedagainst = craftWorld.getBlockAt(x, y - 1, z);
				BlockPlaceEvent event2 = new BlockPlaceEvent(placed, placed.getState(), placedagainst, new org.bukkit.inventory.ItemStack(50,1,(short) 0), ply, true);
				craftServer.getPluginManager().callEvent(event2);
				return !event2.isCancelled();
			//}
			//return !event.isCancelled();
		}
		return false;
	}

	protected boolean attemptInteractRight(EntityHuman player, Block clickedBlock, BlockFace clickedFace)
	{
		if(player == null) return false;
		CraftServer craftServer = player.world.getServer();
		Player ply = (Player) player.getBukkitEntity();
		if(ply != null)
		{
			PlayerInteractEvent event = new PlayerInteractEvent(ply, Action.RIGHT_CLICK_BLOCK, ply.getItemInHand(), clickedBlock, clickedFace);
			craftServer.getPluginManager().callEvent(event);
			return !event.isCancelled();
		}
		return false;
	}
	protected boolean attemptInteractRight(EntityHuman player, Integer x, Integer y, Integer z, BlockFace clickedFace)
	{
		if(player == null) return false;
		CraftWorld craftWorld = player.world.getWorld();
		CraftServer craftServer = player.world.getServer();
		Player ply = (Player) player.getBukkitEntity();

		Block clickedBlock = craftWorld.getBlockAt(x, y, z);
		if(ply != null)
		{
			PlayerInteractEvent event = new PlayerInteractEvent(ply, Action.RIGHT_CLICK_BLOCK, ply.getItemInHand(), clickedBlock, clickedFace);
			craftServer.getPluginManager().callEvent(event);
			return !event.isCancelled();
		}
		return false;
	}

	protected static final int yFlower = BlockFlower.YELLOW_FLOWER.id;
	protected static final int rFlower = BlockFlower.RED_ROSE.id;
	protected static final int bMush = BlockFlower.BROWN_MUSHROOM.id;
	protected static final int rMush = BlockFlower.RED_MUSHROOM.id;
	protected static final int longGrass = BlockLongGrass.LONG_GRASS.id;
	protected static final int bush = BlockDeadBush.DEAD_BUSH.id;
	
	
}