/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   TileAlchChest.java

package ee;

import buildcraft.api.ISpecialInventory;
import buildcraft.api.Orientations;
import ee.core.GuiIds;
import ee.item.ItemLootBall;
import forge.ISidedInventory;
import java.util.*;
import net.minecraft.server.*;

// Referenced classes of package ee:
//            TileEE, EEItem, ItemVoidRing, ItemEECharged, 
//            ItemAttractionRing, EEBlock, BlockEETorch, EEMaps, 
//            ItemKleinStar, ItemEternalDensity, EntityLootBall, EEBase

public class TileAlchChest extends TileEE
    implements ISpecialInventory, ISidedInventory
{

    public TileAlchChest()
    {
        items = new ItemStack[113];
        repairTimer = 0;
    }
    
    private boolean Exists=true;

    public boolean addItem(ItemStack var1, boolean var2, Orientations var3)
    {
    	int kk = 0;
    	if(var3.ordinal() == Orientations.YNeg.ordinal()) kk = 1;
    	if(var3.ordinal() == Orientations.YPos.ordinal()) kk = 2;
    	if(var3.ordinal() == Orientations.ZNeg.ordinal()) kk = 3;
    	if(var3.ordinal() == Orientations.ZPos.ordinal()) kk = 4;
    	if(var3.ordinal() == Orientations.XNeg.ordinal()) kk = 5;
    	if(var3.ordinal() == Orientations.XPos.ordinal()) kk = 6;
    	if(var3.ordinal() == Orientations.Unknown.ordinal()) kk = 7;
    	
        switch(kk)
        {
        default:
            break;

        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
            if(var1 != null)
            {
                for(int var4 = 0; var4 < items.length; var4++)
                {
                    if(items[var4] == null)
                    {
                        if(var2)
                        {
                            items[var4] = var1.cloneItemStack();
                            for(; var1.count > 0; var1.count--);
                        }
                        return true;
                    }
                    if(!items[var4].doMaterialsMatch(var1) || items[var4].count >= items[var4].getMaxStackSize())
                        continue;
                    if(var2)
                    {
                        for(; items[var4].count < items[var4].getMaxStackSize() && var1.count > 0; var1.count--)
                            items[var4].count++;

                        if(var1.count != 0)
                            continue;
                    }
                    return true;
                }

            }
            break;
        }
        return false;
    }

    public ItemStack extractItem(boolean var1, Orientations var2)
    {
    	if(!Exists) return new ItemStack(3,1,0);
    	int kk = 0;
    	if(var2.ordinal() == Orientations.YNeg.ordinal()) kk = 1;
    	if(var2.ordinal() == Orientations.YPos.ordinal()) kk = 2;
    	if(var2.ordinal() == Orientations.ZNeg.ordinal()) kk = 3;
    	if(var2.ordinal() == Orientations.ZPos.ordinal()) kk = 4;
    	if(var2.ordinal() == Orientations.XNeg.ordinal()) kk = 5;
    	if(var2.ordinal() == Orientations.XPos.ordinal()) kk = 6;
    	if(var2.ordinal() == Orientations.Unknown.ordinal()) kk = 7;
    	
        switch(kk)
        {
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
            for(int var3 = 0; var3 < items.length; var3++)
                if(items[var3] != null)
                {
                    ItemStack var4 = items[var3].cloneItemStack();
                    var4.count = 1;
                    if(var1)
                    {
                        items[var3].count--;
                        if(items[var3].count < 1)
                            items[var3] = null;
                    }
                    return var4;
                }

            break;
        }
        return null;
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
        return "Chest";
    }

    public void a(NBTTagCompound var1)
    {
        super.a(var1);
        NBTTagList var2 = var1.getList("Items");
        items = new ItemStack[getSize()];
        for(int var3 = 0; var3 < var2.size(); var3++)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.get(var3);
            int var5 = var4.getByte("Slot") & 255;
            if(var5 >= 0 && var5 < items.length)
                items[var5] = ItemStack.a(var4);
        }

        condenseOn = var1.getBoolean("condenseOn");
        repairOn = var1.getBoolean("repairOn");
        eternalDensity = var1.getShort("eternalDensity");
        timeWarp = var1.getBoolean("timeWarp");
        interdictionOn = var1.getBoolean("interdictionOn");
    }

    public void b(NBTTagCompound var1)
    {
        super.b(var1);
        var1.setBoolean("timeWarp", timeWarp);
        var1.setBoolean("condenseOn", condenseOn);
        var1.setBoolean("repairOn", repairOn);
        var1.setShort("eternalDensity", (short)eternalDensity);
        var1.setBoolean("interdictionOn", interdictionOn);
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

    public int getMaxStackSize()
    {
        return 64;
    }

    public void update()
    {
        super.update();
        if(world != null && !EEProxy.isClient(world))
        {
            boolean var1 = false;
            boolean var2 = false;
            boolean var3 = false;
            boolean var4 = false;
            boolean var5 = false;
            for(int var6 = 0; var6 < getSize(); var6++)
                if(items[var6] != null)
                {
                    if(items[var6].getItem().id == EEItem.watchOfTime.id)
                        var4 = true;
                    if(items[var6].getItem().id == EEItem.repairCharm.id)
                        var1 = true;
                    if(items[var6].getItem() instanceof ItemVoidRing)
                    {
                        eternalDensity = var6;
                        if((items[var6].getData() & 1) == 0)
                        {
                            items[var6].setData(items[var6].getData() + 1);
                            ((ItemEECharged)items[var6].getItem()).setBoolean(items[var6], "active", true);
                        }
                        var2 = true;
                        var5 = true;
                    }
                    if(items[var6].getItem().id == EEItem.eternalDensity.id)
                    {
                        eternalDensity = var6;
                        if((items[var6].getData() & 1) == 0)
                        {
                            items[var6].setData(items[var6].getData() + 1);
                            ((ItemEECharged)items[var6].getItem()).setBoolean(items[var6], "active", true);
                        }
                        var2 = true;
                    }
                    if(items[var6].getItem() instanceof ItemAttractionRing)
                    {
                        if((items[var6].getData() & 1) == 0)
                        {
                            items[var6].setData(items[var6].getData() + 1);
                            ((ItemEECharged)items[var6].getItem()).setBoolean(items[var6], "active", true);
                        }
                        var5 = true;
                    }
                    if(items[var6].getItem().id == EEBlock.eeTorch.id && items[var6].getData() == 0)
                        var3 = true;
                }

            if(var4 != timeWarp)
                timeWarp = var4;
            if(var1 != repairOn)
                repairOn = var1;
            if(var5 != attractionOn)
                attractionOn = var5;
            if(var2 != condenseOn)
                condenseOn = var2;
            else
            if(!var2)
                eternalDensity = -1;
            if(var3 != interdictionOn)
            {
                world.notify(x, y, z);
                interdictionOn = var3;
            }
        }
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

    public void q_()
    {
        if((++ticksSinceSync % 20) * 4 == 0)
            world.playNote(x, y, z, 1, numUsingPlayers);
        prevLidAngle = lidAngle;
        float var1 = 0.1F;
        if(numUsingPlayers > 0 && lidAngle == 0.0F)
        {
            double var4 = (double)x + 0.5D;
            double var2 = (double)z + 0.5D;
            world.makeSound(var4, (double)y + 0.5D, var2, "random.chestopen", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
        if(numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F)
        {
            float var8 = lidAngle;
            if(numUsingPlayers > 0)
                lidAngle += var1;
            else
                lidAngle -= var1;
            if(lidAngle > 1.0F)
                lidAngle = 1.0F;
            float var5 = 0.5F;
            if(lidAngle < var5 && var8 >= var5)
            {
                double var2 = (double)x + 0.5D;
                double var6 = (double)z + 0.5D;
                world.makeSound(var2, (double)y + 0.5D, var6, "random.chestclosed", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            }
            if(lidAngle < 0.0F)
                lidAngle = 0.0F;
        }
        if(repairOn)
            doRepair();
        if(attractionOn)
            doAttraction();
        if(condenseOn && eternalDensity >= 0)
            doCondense(items[eternalDensity]);
    }

    private void doAttraction()
    {
        List var1 = world.a(ee.EntityLootBall.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
        Entity var2;
        for(Iterator var3 = var1.iterator(); var3.hasNext(); PullItems(var2))
            var2 = (Entity)var3.next();

        List var12 = world.a(ee.EntityLootBall.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
        Entity var4;
        for(Iterator var5 = var12.iterator(); var5.hasNext(); PullItems(var4))
            var4 = (Entity)var5.next();

        List var13 = world.a(net.minecraft.server.EntityItem.class, AxisAlignedBB.b(x - 10, y - 10, z - 10, x + 10, y + 10, z + 10));
        Entity var6;
        for(Iterator var7 = var13.iterator(); var7.hasNext(); PullItems(var6))
            var6 = (Entity)var7.next();

        List var14 = world.a(ee.EntityLootBall.class, AxisAlignedBB.b((double)x - 0.5D, (double)y - 0.5D, (double)z - 0.5D, (double)x + 1.25D, (double)y + 1.25D, (double)z + 1.25D));
        Entity var8;
        for(Iterator var9 = var14.iterator(); var9.hasNext(); GrabItems(var8))
            var8 = (Entity)var9.next();

        List var15 = world.a(net.minecraft.server.EntityItem.class, AxisAlignedBB.b((double)x - 0.5D, (double)y - 0.5D, (double)z - 0.5D, (double)x + 1.25D, (double)y + 1.25D, (double)z + 1.25D));
        Entity var10;
        for(Iterator var11 = var15.iterator(); var11.hasNext(); GrabItems(var10))
            var10 = (Entity)var11.next();

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

    public boolean PushStack(ItemStack var1)
    {
        if(var1 == null)
            return false;
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

    private void PushDenseStacks(EntityLootBall var1)
    {
        for(int var2 = 0; var2 < var1.items.length; var2++)
            if(var1.items[var2] != null && PushStack(var1.items[var2]))
                var1.items[var2] = null;

    }

    private void GrabItems(Entity var1)
    {
    	if(var1.dead) return;
        if(var1 != null && (var1 instanceof EntityItem))
        {
            ItemStack var9 = ((EntityItem)var1).itemStack;
            /*if(var9 == null)
            {
                var1.die();
                return;
            }*/
            
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

    private void PullItems(Entity var1)
    {
        if((var1 instanceof EntityItem) || (var1 instanceof EntityLootBall))
        {
            if(var1 instanceof EntityLootBall)
                ((EntityLootBall)var1).setBeingPulled(true);
            double var3 = ((double)x + 0.5D) - var1.locX;
            double var5 = ((double)y + 0.5D) - var1.locY;
            double var7 = ((double)z + 0.5D) - var1.locZ;
            double var9 = var3 * var3 + var5 * var5 + var7 * var7;
            var9 *= var9;
            if(var9 <= Math.pow(6D, 4D))
            {
                double var11 = ((var3 * 0.019999999552965164D) / var9) * Math.pow(6D, 3D);
                double var13 = ((var5 * 0.019999999552965164D) / var9) * Math.pow(6D, 3D);
                double var15 = ((var7 * 0.019999999552965164D) / var9) * Math.pow(6D, 3D);
                if(var11 > 0.10000000000000001D)
                    var11 = 0.10000000000000001D;
                else
                if(var11 < -0.10000000000000001D)
                    var11 = -0.10000000000000001D;
                if(var13 > 0.10000000000000001D)
                    var13 = 0.10000000000000001D;
                else
                if(var13 < -0.10000000000000001D)
                    var13 = -0.10000000000000001D;
                if(var15 > 0.10000000000000001D)
                    var15 = 0.10000000000000001D;
                else
                if(var15 < -0.10000000000000001D)
                    var15 = -0.10000000000000001D;
                var1.motX += var11 * 1.2D;
                var1.motY += var13 * 1.2D;
                var1.motZ += var15 * 1.2D;
            }
        }
    }

    public boolean isInterdicting()
    {
        return interdictionOn;
    }

    private void PushEntities(Entity var1, int var2, int var3, int var4)
    {
        if(!(var1 instanceof EntityHuman) && !(var1 instanceof EntityItem))
        {
            double var6 = (double)(float)var2 - var1.locX;
            double var8 = (double)(float)var3 - var1.locY;
            double var10 = (double)(float)var4 - var1.locZ;
            double var12 = var6 * var6 + var8 * var8 + var10 * var10;
            var12 *= var12;
            if(var12 <= Math.pow(6D, 4D))
            {
                double var14 = -((var6 * 0.019999999552965164D) / var12) * Math.pow(6D, 3D);
                double var16 = -((var8 * 0.019999999552965164D) / var12) * Math.pow(6D, 3D);
                double var18 = -((var10 * 0.019999999552965164D) / var12) * Math.pow(6D, 3D);
                if(var14 > 0.0D)
                    var14 = 0.22D;
                else
                if(var14 < 0.0D)
                    var14 = -0.22D;
                if(var16 > 0.20000000000000001D)
                    var16 = 0.12000000000000001D;
                else
                if(var16 < -0.10000000000000001D)
                    var16 = 0.12000000000000001D;
                if(var18 > 0.0D)
                    var18 = 0.22D;
                else
                if(var18 < 0.0D)
                    var18 = -0.22D;
                var1.motX += var14;
                var1.motY += var16;
                var1.motZ += var18;
            }
        }
    }

    public void b(int var1, int var2)
    {
        if(var1 == 1)
            numUsingPlayers = var2;
    }

    public void f()
    {
        numUsingPlayers++;
        world.playNote(x, y, z, 1, numUsingPlayers);
    }

    public void g()
    {
        numUsingPlayers--;
        world.playNote(x, y, z, 1, numUsingPlayers);
    }

    public boolean a(EntityHuman var1)
    {
        return world.getTileEntity(x, y, z) == this ? var1.e((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D) <= 64D : false;
    }

    public int getStartInventorySide(int var1)
    {
        return 0;
    }

    public int getSizeInventorySide(int var1)
    {
        return getSize();
    }

    public boolean onBlockActivated(EntityHuman var1)
    {
        if(!world.isStatic)
            var1.openGui(mod_EE.getInstance(), GuiIds.ALCH_CHEST, world, x, y, z);
        return true;
    }
    
    public void onBlockRemoval()
    {
    	Exists=false;
        for(int var1 = 0; var1 < getSize(); var1++)
        {
            ItemStack var2 = getItem(var1);
            if(var2 != null)
            {
                float var3 = world.random.nextFloat() * 0.8F + 0.1F;
                float var4 = world.random.nextFloat() * 0.8F + 0.1F;
                float var5 = world.random.nextFloat() * 0.8F + 0.1F;
                while(var2.count > 0) 
                {
                    int var6 = world.random.nextInt(21) + 10;
                    if(var6 > var2.count)
                        var6 = var2.count;
                    var2.count -= var6;
                    EntityItem var7 = new EntityItem(world, (float)x + var3, (float)y + var4, (float)z + var5, new ItemStack(var2.id, var6, var2.getData()));
                    if(var7 != null)
                    {
                        float var8 = 0.05F;
                        var7.motX = (float)world.random.nextGaussian() * var8;
                        var7.motY = (float)world.random.nextGaussian() * var8 + 0.2F;
                        var7.motZ = (float)world.random.nextGaussian() * var8;
                        if(var7.itemStack.getItem() instanceof ItemKleinStar)
                            ((ItemKleinStar)var7.itemStack.getItem()).setKleinPoints(var7.itemStack, ((ItemKleinStar)var2.getItem()).getKleinPoints(var2));
                        world.addEntity(var7);
                    }
                }
            }
        }

    }

    public int getTextureForSide(int var1)
    {
        if(var1 != 1 && var1 != 0)
        {
            byte var2 = direction;
            return var1 == var2 ? EEBase.alchChestFront : EEBase.alchChestSide;
        } else
        {
            return EEBase.alchChestTop;
        }
    }

    public int getInventoryTexture(int var1)
    {
        return var1 != 1 ? var1 != 3 ? EEBase.alchChestSide : EEBase.alchChestFront : EEBase.alchChestTop;
    }

    public int getLightValue()
    {
        return isInterdicting() ? 15 : 0;
    }

    public void onNeighborBlockChange(int i)
    {
    }

    public ItemStack splitWithoutUpdate(int var1)
    {
        return null;
    }

    public ItemStack[] getContents()
    {
        return items;
    }

    public void setMaxStackSize(int i)
    {
    }

    /*static int[] $SWITCH_TABLE$buildcraft$api$Orientations()
    {
        $SWITCH_TABLE$buildcraft$api$Orientations;
        if($SWITCH_TABLE$buildcraft$api$Orientations == null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        JVM INSTR pop ;
        int ai[] = new int[Orientations.values().length];
        try
        {
            ai[Orientations.Unknown.ordinal()] = 7;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[Orientations.XNeg.ordinal()] = 5;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[Orientations.XPos.ordinal()] = 6;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[Orientations.YNeg.ordinal()] = 1;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[Orientations.YPos.ordinal()] = 2;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[Orientations.ZNeg.ordinal()] = 3;
        }
        catch(NoSuchFieldError _ex) { }
        try
        {
            ai[Orientations.ZPos.ordinal()] = 4;
        }
        catch(NoSuchFieldError _ex) { }
        return $SWITCH_TABLE$buildcraft$api$Orientations = ai;
    }*/

    private ItemStack items[];
    private int repairTimer;
    private int eternalDensity;
    private boolean repairOn;
    private boolean condenseOn;
    private boolean interdictionOn;
    public boolean timeWarp;
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;
    private int ticksSinceSync;
    private boolean initialized;
    private boolean attractionOn;
    private static int $SWITCH_TABLE$buildcraft$api$Orientations[];
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/tekkit_server/mods/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 45 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
Couldn't fully decompile method $SWITCH_TABLE$buildcraft$api$Orientations
	Exit status: 0
	Caught exceptions:
*/