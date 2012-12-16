/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   ItemEternalDensity.java

package ee;

import net.minecraft.server.*;

// Referenced classes of package ee:
//            ItemEECharged, EEItem, EEMaps, EEBase

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
        if(var1 == null)
            return false;
        for(int var3 = 0; var3 < var2.inventory.items.length; var3++)
        {
            if(var2.inventory.items[var3] == null)
                return true;
            if(var2.inventory.items[var3].doMaterialsMatch(var1) && var2.inventory.items[var3].count <= var1.getMaxStackSize() - var1.count)
                return true;
        }

        return false;
    }

    public void PushStack(ItemStack var1, EntityHuman var2)
    {
        if(var1 != null)
        {
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
                    while(var2.inventory.items[var3].count < var2.inventory.items[var3].getMaxStackSize() && var1 != null) 
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
        return getInteger(var1, "targetID") == 0 ? null : getInteger(var1, "targetMeta") == 0 ? new ItemStack(getInteger(var1, "targetID"), 1, 0) : new ItemStack(getInteger(var1, "targetID"), 1, getInteger(var1, "targetMeta"));
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

    public void doCondense(ItemStack var1, World var2, EntityHuman var3)
    {
        if(!EEProxy.isClient(var2))
        {
            if(product(var1) != null && emc(var1) >= EEMaps.getEMC(product(var1)) && roomFor(product(var1), var3))
            {
                PushStack(product(var1), var3);
                takeEMC(var1, EEMaps.getEMC(product(var1)));
            }
            int var4 = 0;
            ItemStack var5[] = var3.inventory.items;
            int var6 = var5.length;
            for(int var7 = 0; var7 < var6; var7++)
            {
                ItemStack var8 = var5[var7];
                if(var8 != null && EEMaps.getEMC(var8) != 0 && isValidMaterial(var8, var3) && EEMaps.getEMC(var8) > var4)
                    var4 = EEMaps.getEMC(var8);
            }

            var5 = var3.inventory.items;
            var6 = var5.length;
            for(int var7 = 0; var7 < var6; var7++)
            {
                ItemStack var8 = var5[var7];
                if(var8 != null && EEMaps.getEMC(var8) != 0 && isValidMaterial(var8, var3) && EEMaps.getEMC(var8) <= var4)
                {
                    var4 = EEMaps.getEMC(var8);
                    setInteger(var1, "targetID", var8.id);
                    setInteger(var1, "targetMeta", var8.getData());
                }
            }

            if(target(var1) != null && ConsumeMaterial(target(var1), var3))
                addEMC(var1, EEMaps.getEMC(target(var1)));
        }
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
        if(EEMaps.getEMC(var1) == 0)
            return false;
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
        return EEMaps.isFuel(var1) ? false : var1.id == Block.LOG.id || var1.id == Block.WOOD.id ? false : var3 >= Block.byId.length || !(Block.byId[var3] instanceof BlockContainer) || !Block.byId[var3].hasTileEntity(var1.getData()) ? EEMaps.isValidEDItem(var1) : false;
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
    boolean isWait=false;
    public void doActive(ItemStack var1, World var2, EntityHuman var3)
    {
    	if(!isWait){
    		doCondense(var1, var2, var3);
    		isWait=true;
    	}else isWait=false;
    }

    public boolean canActivate()
    {
        return true;
    }

    public void doChargeTick(ItemStack itemstack, World world, EntityHuman entityhuman)
    {
    }

    public void doUncharge(ItemStack itemstack, World world, EntityHuman entityhuman)
    {
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/Downloads/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 31 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/