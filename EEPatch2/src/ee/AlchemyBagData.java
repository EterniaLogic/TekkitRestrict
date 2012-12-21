/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   AlchemyBagData.java

package ee;

import ee.item.ItemLootBall;
import java.util.*;
import net.minecraft.server.*;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.inventory.InventoryHolder;

// Referenced classes of package ee:
//            EEItem, ItemEECharged, EEMaps, ItemKleinStar, 
//            ItemEternalDensity, ItemVoidRing, EEBase, EntityLootBall

public class AlchemyBagData extends WorldMapBase
    implements IInventory
{

    public AlchemyBagData(String var1)
    {
        super(var1);
        repairTimer = 0;
        condenseCheckTimer = 0;
        items = new ItemStack[113];
        datas.add(this);
    }

    public void onUpdate(World var1, EntityHuman var2)
    {
        if(!initialized)
        {
            initialized = true;
            update();
        }
        if(repairOn)
            doRepair();
        if(condenseOn)
            doCondense(items[eternalDensity]);
        if(voidOn)
        {
            boolean var3 = false;
            for(int var4 = 0; var4 <= 15; var4++)
            {
                boolean var5 = true;
                ItemStack var6[] = var2.inventory.items;
                int var7 = var6.length;
                for(int var8 = 0; var8 < var7; var8++)
                {
                    ItemStack var9 = var6[var8];
                    if(var9 != null && var9.doMaterialsMatch(new ItemStack(EEItem.alchemyBag, 1, var4)))
                        var5 = false;
                }

                if(var5)
                    continue;
                String var10 = (new StringBuilder("bag_")).append(var2.name).append(var4).toString();
                AlchemyBagData var11 = (AlchemyBagData)var1.a(ee.AlchemyBagData.class, var10);
                if(var11 == null)
                    continue;
                if(var3)
                    break;
                if(var11.voidOn)
                    var3 = true;
                if(var11 != this || !var3)
                    continue;
                doAttraction(var2);
                break;
            }

        }
        if(markForUpdate)
            a();
    }

    public int getSize()
    {
        return 104;
    }

    public ItemStack getItem(int var1)
    {
        return items[var1];
    }

    public ItemStack splitStack(int var1, int var2)
    {
        if(items[var1] != null)
        {
            ItemStack var3;
            if(items[var1].count <= var2)
            {
                var3 = items[var1];
                items[var1] = null;
                update();
                return var3;
            }
            var3 = items[var1].a(var2);
            if(items[var1].count == 0)
                items[var1] = null;
            update();
            return var3;
        } else
        {
            return null;
        }
    }

    public void setItem(int var1, ItemStack var2)
    {
        items[var1] = var2;
        if(var2 != null && var2.count > getMaxStackSize())
            var2.count = getMaxStackSize();
        update();
    }

    public String getName()
    {
        return "Bag";
    }

    public int getMaxStackSize()
    {
        return 64;
    }

    public void update()
    {
        markForUpdate = true;
        boolean var1 = false;
        boolean var2 = false;
        boolean var3 = false;
        for(int var4 = 0; var4 < items.length; var4++)
            if(items[var4] != null)
            {
                if(items[var4].getItem() == EEItem.repairCharm)
                    var1 = true;
                if(items[var4].getItem() == EEItem.voidRing)
                {
                    eternalDensity = var4;
                    if((items[var4].getData() & 1) == 0)
                    {
                        items[var4].setData(items[var4].getData() + 1);
                        ((ItemEECharged)items[var4].getItem()).setBoolean(items[var4], "active", true);
                    }
                    var3 = true;
                    var2 = true;
                }
                if(items[var4].getItem() == EEItem.eternalDensity)
                {
                    eternalDensity = var4;
                    if((items[var4].getData() & 1) == 0)
                    {
                        items[var4].setData(items[var4].getData() + 1);
                        ((ItemEECharged)items[var4].getItem()).setBoolean(items[var4], "active", true);
                    }
                    var2 = true;
                }
                if(items[var4].getItem() == EEItem.attractionRing)
                {
                    var3 = true;
                    if((items[var4].getData() & 1) == 0)
                    {
                        items[var4].setData(items[var4].getData() + 1);
                        ((ItemEECharged)items[var4].getItem()).setBoolean(items[var4], "active", true);
                    }
                }
            }

        if(var1 != repairOn)
            repairOn = var1;
        if(var2 != condenseOn)
            condenseOn = var2;
        if(var3 != voidOn)
            voidOn = var3;
    }

    public void doRepair()
    {
        if(repairTimer >= 20)
        {
            ItemStack var1 = null;
            boolean var2 = false;
            for(int var3 = 0; var3 < getSize(); var3++)
            {
                var2 = false;
                var1 = items[var3];
                if(var1 != null)
                {
                    for(int var4 = 0; var4 < EEMaps.chargedItems.size(); var4++)
                    {
                        if(((Integer)EEMaps.chargedItems.get(Integer.valueOf(var4))).intValue() != var1.id)
                            continue;
                        var2 = true;
                        break;
                    }

                    if(!var2 && var1.getData() >= 1 && var1.d())
                        var1.setData(var1.getData() - 1);
                }
            }

            repairTimer = 0;
        }
        repairTimer++;
        markForUpdate = true;
    }

    public void doCondense(ItemStack var1)
    {
        if(eternalDensity != -1)
        {
            int var2 = 0;
            for(int var3 = 0; var3 < items.length; var3++)
                if(items[var3] != null && isValidMaterial(items[var3]) && EEMaps.getEMC(items[var3]) > var2)
                    var2 = EEMaps.getEMC(items[var3]);

            for(int var3 = 0; var3 < items.length; var3++)
                if(items[var3] != null && isValidMaterial(items[var3]) && EEMaps.getEMC(items[var3]) < var2)
                    var2 = EEMaps.getEMC(items[var3]);

            if(var2 < EEMaps.getEMC(EEItem.redMatter.id) && !AnalyzeTier(items[eternalDensity], EEMaps.getEMC(EEItem.redMatter.id)) && var2 < EEMaps.getEMC(EEItem.darkMatter.id) && !AnalyzeTier(items[eternalDensity], EEMaps.getEMC(EEItem.darkMatter.id)) && var2 < EEMaps.getEMC(Item.DIAMOND.id) && !AnalyzeTier(items[eternalDensity], EEMaps.getEMC(Item.DIAMOND.id)) && var2 < EEMaps.getEMC(Item.GOLD_INGOT.id) && !AnalyzeTier(items[eternalDensity], EEMaps.getEMC(Item.GOLD_INGOT.id)) && var2 < EEMaps.getEMC(Item.IRON_INGOT.id))
                if(!AnalyzeTier(items[eternalDensity], EEMaps.getEMC(Item.IRON_INGOT.id)));
        }
    }

    private boolean AnalyzeTier(ItemStack var1, int var2)
    {
        if(var1 == null)
            return false;
        int var3 = 0;
        for(int var4 = 0; var4 < items.length; var4++)
            if(items[var4] != null && isValidMaterial(items[var4]) && EEMaps.getEMC(items[var4]) < var2)
                var3 += EEMaps.getEMC(items[var4]) * items[var4].count;

        if(var3 + emc(var1) < var2)
            return false;
        for(int var4 = 0; var3 + emc(var1) >= var2 && var4 < 10; ConsumeMaterialBelowTier(var1, var2))
            var4++;

        if(emc(var1) >= var2 && roomFor(getProduct(var2)))
        {
            PushStack(getProduct(var2));
            takeEMC(var1, var2);
        }
        return true;
    }

    private boolean roomFor(ItemStack var1)
    {
        if(var1 == null)
            return false;
        for(int var2 = 0; var2 < items.length; var2++)
        {
            if(items[var2] == null)
                return true;
            if(items[var2].doMaterialsMatch(var1) && items[var2].count <= var1.getMaxStackSize() - var1.count)
                return true;
        }

        return false;
    }

    private ItemStack getProduct(int var1)
    {
        return var1 != EEMaps.getEMC(Item.IRON_INGOT.id) ? var1 != EEMaps.getEMC(Item.GOLD_INGOT.id) ? var1 != EEMaps.getEMC(Item.DIAMOND.id) ? var1 != EEMaps.getEMC(EEItem.darkMatter.id) ? var1 != EEMaps.getEMC(EEItem.redMatter.id) ? null : new ItemStack(EEItem.redMatter, 1) : new ItemStack(EEItem.darkMatter, 1) : new ItemStack(Item.DIAMOND, 1) : new ItemStack(Item.GOLD_INGOT, 1) : new ItemStack(Item.IRON_INGOT, 1);
    }

    public boolean PushStack(ItemStack var1)
    {
        if(var1 == null)
            return true;
        for(int var2 = 0; var2 < items.length; var2++)
        {
            if(items[var2] == null)
            {
                items[var2] = var1.cloneItemStack();
                var1 = null;
                return true;
            }
            if(items[var2].doMaterialsMatch(var1) && items[var2].count <= var1.getMaxStackSize() - var1.count)
            {
                items[var2].count += var1.count;
                var1 = null;
                return true;
            }
        }

        return false;
    }

    private void ConsumeMaterialBelowTier(ItemStack var1, int var2)
    {
        for(int var3 = 0; var3 < items.length; var3++)
            if(items[var3] != null && isValidMaterial(items[var3]) && EEMaps.getEMC(items[var3]) < var2)
            {
                addEMC(var1, EEMaps.getEMC(items[var3]));
                items[var3].count--;
                if(items[var3].count == 0)
                    items[var3] = null;
                return;
            }

    }

    private boolean isValidMaterial(ItemStack var1)
    {
        if(var1 == null)
            return false;
        if(EEMaps.getEMC(var1) == 0)
            return false;
        if(var1.getItem() instanceof ItemKleinStar)
        {
            return false;
        } else
        {
            int var2 = var1.id;
            return var2 != EEItem.redMatter.id ? var2 >= Block.byId.length || !(Block.byId[var2] instanceof BlockContainer) || !Block.byId[var2].hasTileEntity(var1.getData()) : false;
        }
    }

    private int emc(ItemStack var1)
    {
        return (var1.getItem() instanceof ItemEternalDensity) || (var1.getItem() instanceof ItemVoidRing) ? (var1.getItem() instanceof ItemEternalDensity) ? ((ItemEternalDensity)var1.getItem()).getInteger(var1, "emc") : ((ItemVoidRing)var1.getItem()).getInteger(var1, "emc") : 0;
    }

    private void takeEMC(ItemStack var1, int var2)
    {
        if((var1.getItem() instanceof ItemEternalDensity) || (var1.getItem() instanceof ItemVoidRing))
            if(var1.getItem() instanceof ItemEternalDensity)
                ((ItemEternalDensity)var1.getItem()).setInteger(var1, "emc", emc(var1) - var2);
            else
                ((ItemVoidRing)var1.getItem()).setInteger(var1, "emc", emc(var1) - var2);
    }

    private void addEMC(ItemStack var1, int var2)
    {
        if((var1.getItem() instanceof ItemEternalDensity) || (var1.getItem() instanceof ItemVoidRing))
            if(var1.getItem() instanceof ItemEternalDensity)
                ((ItemEternalDensity)var1.getItem()).setInteger(var1, "emc", emc(var1) + var2);
            else
                ((ItemVoidRing)var1.getItem()).setInteger(var1, "emc", emc(var1) + var2);
    }

    public void doAttraction(EntityHuman var1)
    {
        List var2 = var1.world.a(net.minecraft.server.EntityItem.class, AxisAlignedBB.b(EEBase.playerX(var1) - 10D, EEBase.playerY(var1) - 10D, EEBase.playerZ(var1) - 10D, EEBase.playerX(var1) + 10D, EEBase.playerY(var1) + 10D, EEBase.playerZ(var1) + 10D));
        Entity var3;
        for(Iterator var4 = var2.iterator(); var4.hasNext(); PullItems(var3, var1))
            var3 = (Entity)var4.next();

        List var14 = var1.world.a(net.minecraft.server.EntityItem.class, AxisAlignedBB.b(EEBase.playerX(var1) - 0.55000000000000004D, EEBase.playerY(var1) - 0.55000000000000004D, EEBase.playerZ(var1) - 0.55000000000000004D, EEBase.playerX(var1) + 0.55000000000000004D, EEBase.playerY(var1) + 0.55000000000000004D, EEBase.playerZ(var1) + 0.55000000000000004D));
        Entity var5;
        for(Iterator var6 = var14.iterator(); var6.hasNext(); GrabItems(var5))
            var5 = (Entity)var6.next();

        List var15 = var1.world.a(ee.EntityLootBall.class, AxisAlignedBB.b(EEBase.playerX(var1) - 10D, EEBase.playerY(var1) - 10D, EEBase.playerZ(var1) - 10D, EEBase.playerX(var1) + 10D, EEBase.playerY(var1) + 10D, EEBase.playerZ(var1) + 10D));
        Entity var7;
        for(Iterator var8 = var15.iterator(); var8.hasNext(); PullItems(var7, var1))
            var7 = (Entity)var8.next();

        List var16 = var1.world.a(ee.EntityLootBall.class, AxisAlignedBB.b(EEBase.playerX(var1) - 0.55000000000000004D, EEBase.playerY(var1) - 0.55000000000000004D, EEBase.playerZ(var1) - 0.55000000000000004D, EEBase.playerX(var1) + 0.55000000000000004D, EEBase.playerY(var1) + 0.55000000000000004D, EEBase.playerZ(var1) + 0.55000000000000004D));
        Entity var9;
        for(Iterator var10 = var16.iterator(); var10.hasNext(); GrabItems(var9))
            var9 = (Entity)var10.next();

        List var13 = var1.world.a(net.minecraft.server.EntityExperienceOrb.class, AxisAlignedBB.b(EEBase.playerX(var1) - 10D, EEBase.playerY(var1) - 10D, EEBase.playerZ(var1) - 10D, EEBase.playerX(var1) + 10D, EEBase.playerY(var1) + 10D, EEBase.playerZ(var1) + 10D));
        Entity var11;
        for(Iterator var12 = var13.iterator(); var12.hasNext(); PullItems(var11, var1))
            var11 = (Entity)var12.next();

    }

    private void PullItems(Entity var1, EntityHuman var2)
    {
        if((var1 instanceof EntityItem) || (var1 instanceof EntityLootBall))
        {
            if(var1 instanceof EntityLootBall)
                ((EntityLootBall)var1).setBeingPulled(true);
            double var4 = (EEBase.playerX(var2) + 0.5D) - var1.locX;
            double var6 = (EEBase.playerY(var2) + 0.5D) - var1.locY;
            double var8 = (EEBase.playerZ(var2) + 0.5D) - var1.locZ;
            double var10 = var4 * var4 + var6 * var6 + var8 * var8;
            var10 *= var10;
            if(var10 <= Math.pow(6D, 4D))
            {
                double var12 = ((var4 * 0.019999999552965164D) / var10) * Math.pow(6D, 3D);
                double var14 = ((var6 * 0.019999999552965164D) / var10) * Math.pow(6D, 3D);
                double var16 = ((var8 * 0.019999999552965164D) / var10) * Math.pow(6D, 3D);
                if(var12 > 0.10000000000000001D)
                    var12 = 0.10000000000000001D;
                else
                if(var12 < -0.10000000000000001D)
                    var12 = -0.10000000000000001D;
                if(var14 > 0.10000000000000001D)
                    var14 = 0.10000000000000001D;
                else
                if(var14 < -0.10000000000000001D)
                    var14 = -0.10000000000000001D;
                if(var16 > 0.10000000000000001D)
                    var16 = 0.10000000000000001D;
                else
                if(var16 < -0.10000000000000001D)
                    var16 = -0.10000000000000001D;
                var1.motX += var12 * 1.2D;
                var1.motY += var14 * 1.2D;
                var1.motZ += var16 * 1.2D;
            }
        }
    }

    private void GrabItems(Entity var1)
    {
    	if(var1.dead) return;
        if(var1 != null && (var1 instanceof EntityItem))
        {
            ItemStack var9 = ((EntityItem)var1).itemStack;
            if(var9 == null)
            {
                var1.die();
                return;
            }
            
            if(var9.getItem() instanceof ItemLootBall)
            {
                ItemLootBall var3 = (ItemLootBall)var9.getItem();
                ItemStack var4[] = var3.getDroplist(var9);
                ItemStack var5[] = var4;
                int var6 = var4.length;
                for(int var7 = 0; var7 < var6; var7++)
                {
                    ItemStack var8 = var5[var7];
                    PushStack(var8);
                }

                var1.die();
            } else
            {
                PushStack(var9);
                var1.die();
            }
        } else
        if(var1 != null && (var1 instanceof EntityLootBall))
        {
            if(((EntityLootBall)var1).items == null)
                var1.die();
            ItemStack var2[] = ((EntityLootBall)var1).items;
            PushDenseStacks((EntityLootBall)var1);
            if(((EntityLootBall)var1).isEmpty())
                var1.die();
        }
    }

    private void PushDenseStacks(EntityLootBall var1)
    {
        for(int var2 = 0; var2 < var1.items.length; var2++)
            if(var1.items[var2] != null && PushStack(var1.items[var2]))
                var1.items[var2] = null;

    }

    public boolean PushStack(EntityItem var1)
    {
        if(var1 == null)
            return false;
        if(var1.itemStack == null)
        {
            var1.die();
            return false;
        }
        if(var1.itemStack.count < 1)
        {
            var1.die();
            return false;
        }
        for(int var2 = 0; var2 < items.length; var2++)
        {
            if(items[var2] == null)
            {
                items[var2] = var1.itemStack.cloneItemStack();
                for(items[var2].count = 0; var1.itemStack.count > 0 && items[var2].count < items[var2].getMaxStackSize(); var1.itemStack.count--)
                    items[var2].count++;

                var1.die();
                return true;
            }
            if(items[var2].doMaterialsMatch(var1.itemStack) && items[var2].count <= var1.itemStack.getMaxStackSize() - var1.itemStack.count)
            {
                for(; var1.itemStack.count > 0 && items[var2].count < items[var2].getMaxStackSize(); var1.itemStack.count--)
                    items[var2].count++;

                var1.die();
                return true;
            }
        }

        return false;
    }

    private void PushDenseStacks(EntityLootBall var1, EntityHuman var2)
    {
        for(int var3 = 0; var3 < var1.items.length; var3++)
            if(var1.items[var3] != null)
            {
                PushStack(var1.items[var3], var2);
                var1.items[var3] = null;
            }

    }

    public void PushStack(ItemStack var1, EntityHuman var2)
    {
        for(int var3 = 0; var3 < getSize(); var3++)
            if(var1 != null)
            {
                if(items[var3] == null)
                {
                    items[var3] = var1.cloneItemStack();
                    var1 = null;
                    markForUpdate = true;
                    return;
                }
                if(items[var3].doMaterialsMatch(var1))
                    while(items[var3].count < items[var3].getMaxStackSize() && var1 != null) 
                    {
                        items[var3].count++;
                        var1.count--;
                        if(var1.count == 0)
                        {
                            var1 = null;
                            markForUpdate = true;
                            return;
                        }
                    }
                else
                if(var3 == items.length - 1)
                {
                    EntityItem var4 = new EntityItem(var2.world, EEBase.playerX(var2), EEBase.playerY(var2), EEBase.playerZ(var2), var1);
                    var4.pickupDelay = 1;
                    var2.world.addEntity(var4);
                    markForUpdate = true;
                    return;
                }
            }

        if(var1 != null)
        {
            for(int var3 = 0; var3 < items.length; var3++)
                if(items[var3] == null)
                {
                    items[var3] = var1.cloneItemStack();
                    var1 = null;
                    markForUpdate = true;
                    return;
                }

        }
    }

    public boolean a(EntityHuman var1)
    {
        return true;
    }

    public void f()
    {
    }

    public void g()
    {
    }

    public void a(NBTTagCompound var1)
    {
        voidOn = var1.getBoolean("voidOn");
        repairOn = var1.getBoolean("repairOn");
        condenseOn = var1.getBoolean("condenseOn");
        eternalDensity = var1.getShort("eternalDensity");
        NBTTagList var2 = var1.getList("Items");
        items = new ItemStack[113];
        for(int var3 = 0; var3 < var2.size(); var3++)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.get(var3);
            int var5 = var4.getByte("Slot") & 255;
            if(var5 >= 0 && var5 < items.length)
                items[var5] = ItemStack.a(var4);
        }

    }

    public void b(NBTTagCompound var1)
    {
        var1.setBoolean("voidOn", voidOn);
        var1.setBoolean("repairOn", repairOn);
        var1.setBoolean("condenseOn", condenseOn);
        var1.setShort("eternalDensity", (short)eternalDensity);
        NBTTagList var2 = new NBTTagList();
        for(int var3 = 0; var3 < items.length; var3++)
            if(items[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                items[var3].save(var4);
                var2.add(var4);
            }

        var1.set("Items", var2);
    }

    public ItemStack splitWithoutUpdate(int var1)
    {
        return null;
    }

    public ItemStack[] getContents()
    {
        return items;
    }

    public void onOpen(CraftHumanEntity crafthumanentity)
    {
    }

    public void onClose(CraftHumanEntity crafthumanentity)
    {
    }

    public List getViewers()
    {
        return new ArrayList(0);
    }

    public InventoryHolder getOwner()
    {
        return null;
    }

    public void setMaxStackSize(int i)
    {
    }

    public boolean voidOn;
    public boolean repairOn;
    public boolean markForUpdate;
    public boolean condenseOn;
    public int repairTimer;
    public int condenseCheckTimer;
    public static final String prefix = "bag";
    public static final String prefix_ = "bag_";
    public ItemStack items[];
    private int eternalDensity;
    private boolean initialized;
    public static List datas = new LinkedList();

}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/tekkit_server/mods/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 49 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/