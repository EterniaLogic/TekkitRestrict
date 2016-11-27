package ee;

import net.minecraft.server.*;

public class ItemEternalDensity extends ItemEECharged
{

	public ItemEternalDensity(int var1)
	{
		super(var1, 0);
		maxStackSize = 1;
	}

	public int getIconFromDamage(int var1)
	{
		return isActivated(var1) ? textureId + 1 : textureId;
	}

	public boolean roomFor(ItemStack var1, EntityHuman var2)
	{
		if(var1 == null) return false;
		for(int var3 = 0; var3 < var2.inventory.items.length; var3++)
		{
			if(var2.inventory.items[var3] == null) return true;
			if(var2.inventory.items[var3].doMaterialsMatch(var1) && var2.inventory.items[var3].count <= var1.getMaxStackSize() - var1.count)
				return true;
		}

		return false;
	}

	public void PushStack(ItemStack var1, EntityHuman var2)
	{
		if(var1 == null) return;
		
		for(int var3 = 0; var3 < var2.inventory.items.length; var3++)
		{
			if(var2.inventory.items[var3] == null)
			{
				var2.inventory.items[var3] = var1.cloneItemStack();
				var1 = null;
				return;
			}
			if(var2.inventory.items[var3].doMaterialsMatch(var1) && var2.inventory.items[var3].count <= var1.getMaxStackSize() - var1.count)
			{
				var2.inventory.items[var3].count += var1.count;
				var1 = null;
				return;
			}
			if(var2.inventory.items[var3].doMaterialsMatch(var1))
				while(var2.inventory.items[var3].count < var2.inventory.items[var3].getMaxStackSize()) 
				{
					var2.inventory.items[var3].count++;
					var1.count--;
					if(var1.count <= 0)
					{
						var1 = null;
						return;
					}
				}
		}

		
	}

	private void dumpContents(ItemStack var1, EntityHuman var2)
	{
		for(; emc(var1) >= EEMaps.getEMC(EEItem.redMatter.id) && roomFor(new ItemStack(EEItem.redMatter, 1), var2); PushStack(new ItemStack(EEItem.redMatter, 1), var2))
			takeEMC(var1, EEMaps.getEMC(EEItem.redMatter.id));

		for(; emc(var1) >= EEMaps.getEMC(EEItem.darkMatter.id) && roomFor(new ItemStack(EEItem.darkMatter, 1), var2); PushStack(new ItemStack(EEItem.darkMatter, 1), var2))
			takeEMC(var1, EEMaps.getEMC(EEItem.darkMatter.id));

		for(; emc(var1) >= EEMaps.getEMC(Item.DIAMOND.id) && roomFor(new ItemStack(Item.DIAMOND, 1), var2); PushStack(new ItemStack(Item.DIAMOND, 1), var2))
			takeEMC(var1, EEMaps.getEMC(Item.DIAMOND.id));

		for(; emc(var1) >= EEMaps.getEMC(Item.GOLD_INGOT.id) && roomFor(new ItemStack(Item.GOLD_INGOT, 1), var2); PushStack(new ItemStack(Item.GOLD_INGOT, 1), var2))
			takeEMC(var1, EEMaps.getEMC(Item.GOLD_INGOT.id));

		for(; emc(var1) >= EEMaps.getEMC(Item.IRON_INGOT.id) && roomFor(new ItemStack(Item.IRON_INGOT, 1), var2); PushStack(new ItemStack(Item.IRON_INGOT, 1), var2))
			takeEMC(var1, EEMaps.getEMC(Item.IRON_INGOT.id));

		for(; emc(var1) >= EEMaps.getEMC(Block.COBBLESTONE.id) && roomFor(new ItemStack(Block.COBBLESTONE, 1), var2); PushStack(new ItemStack(Block.COBBLESTONE, 1), var2))
			takeEMC(var1, EEMaps.getEMC(Block.COBBLESTONE.id));

	}

	public ItemStack target(ItemStack var1)
	{
		int id = getInteger(var1, "targetID");
		if (id == 0) return null;
		
		int meta = getInteger(var1, "targetMeta");
		return new ItemStack(id, 1, meta);
	}

	public ItemStack product(ItemStack var1)
	{
		if(target(var1) != null)
		{
			int var2 = EEMaps.getEMC(target(var1));
			if(var2 < EEMaps.getEMC(Item.IRON_INGOT.id))
				return new ItemStack(Item.IRON_INGOT, 1);
			if(var2 < EEMaps.getEMC(Item.GOLD_INGOT.id))
				return new ItemStack(Item.GOLD_INGOT, 1);
			if(var2 < EEMaps.getEMC(Item.DIAMOND.id))
				return new ItemStack(Item.DIAMOND, 1);
			if(var2 < EEMaps.getEMC(EEItem.darkMatter.id))
				return new ItemStack(EEItem.darkMatter, 1);
			if(var2 < EEMaps.getEMC(EEItem.redMatter.id))
				return new ItemStack(EEItem.redMatter, 1);
		}
		return null;
	}

	public void doCondense(ItemStack stack, World world, EntityHuman human)
	{
		if(EEProxy.isClient(world)) return;
		
		if(product(stack) != null && emc(stack) >= EEMaps.getEMC(product(stack)) && roomFor(product(stack), human))
		{
			PushStack(product(stack), human);
			takeEMC(stack, EEMaps.getEMC(product(stack)));
		}
		int var4 = 0;
		ItemStack var5[] = human.inventory.items;
		int var6 = var5.length;
		for(int var7 = 0; var7 < var6; var7++)
		{
			ItemStack var8 = var5[var7];
			if(var8 != null && EEMaps.getEMC(var8) != 0 && isValidMaterial(var8, human) && EEMaps.getEMC(var8) > var4)
				var4 = EEMaps.getEMC(var8);
		}

		var5 = human.inventory.items;
		var6 = var5.length;
		for(int var7 = 0; var7 < var6; var7++)
		{
			ItemStack var8 = var5[var7];
			if(var8 != null && EEMaps.getEMC(var8) != 0 && isValidMaterial(var8, human) && EEMaps.getEMC(var8) <= var4)
			{
				var4 = EEMaps.getEMC(var8);
				setInteger(stack, "targetID", var8.id);
				setInteger(stack, "targetMeta", var8.getData());
			}
		}

		if(target(stack) != null && ConsumeMaterial(target(stack), human))
			addEMC(stack, EEMaps.getEMC(target(stack)));
	}

	private boolean isLastCobbleStack(EntityHuman var1)
	{
		int var2 = 0;
		for(int var3 = 0; var3 < var1.inventory.items.length; var3++)
			if(var1.inventory.items[var3] != null && var1.inventory.items[var3].id == Block.COBBLESTONE.id)
				var2 += var1.inventory.items[var3].count;

		return var2 <= 64;
	}

	private boolean isValidMaterial(ItemStack var1, EntityHuman var2)
	{
		if(EEMaps.getEMC(var1) == 0) return false;
		if(var1.id == Block.COBBLESTONE.id && isLastCobbleStack(var2))
			return false;
		int var3 = var1.id;
		if(var3 >= Block.byId.length)
		{
			if(var3 != Item.IRON_INGOT.id && var3 != Item.GOLD_INGOT.id && var3 != Item.DIAMOND.id && var3 != EEItem.darkMatter.id)
				return false;
			if(var3 == EEItem.redMatter.id)
				return false;
		}
		return EEMaps.isFuel(var1) ? false :
			var1.id == Block.LOG.id || var1.id == Block.WOOD.id ? false :
				var3 >= Block.byId.length || !(Block.byId[var3] instanceof BlockContainer) || !Block.byId[var3].hasTileEntity(var1.getData()) ? EEMaps.isValidEDItem(var1) : false;
	}

	private int emc(ItemStack var1)
	{
		return getInteger(var1, "emc");
	}

	private void takeEMC(ItemStack var1, int var2)
	{
		setInteger(var1, "emc", emc(var1) - var2);
	}

	private void addEMC(ItemStack var1, int var2)
	{
		setInteger(var1, "emc", emc(var1) + var2);
	}

	public boolean ConsumeMaterial(ItemStack var1, EntityHuman var2)
	{
		return EEBase.Consume(var1, var2, false);
	}

	public void ConsumeReagent(ItemStack var1, EntityHuman var2, boolean var3)
	{
		EEBase.updatePlayerEffect(var1.getItem(), 200, var2);
	}

	public void doPassive(ItemStack var1, World var2, EntityHuman var3)
	{
		if(!isActivated(var1.getData()))
			dumpContents(var1, var3);
	}
	
	private boolean isWait=false;
	public void doActive(ItemStack var1, World var2, EntityHuman var3)
	{
		if (isWait){
			isWait = false;
			return;
		}
		doCondense(var1, var2, var3);
		isWait = true;
	}

	public boolean canActivate()
	{
		return true;
	}

	public void doChargeTick(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	public void doUncharge(ItemStack itemstack, World world, EntityHuman entityhuman) {}
}