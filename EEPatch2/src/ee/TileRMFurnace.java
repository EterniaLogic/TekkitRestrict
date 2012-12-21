/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   TileRMFurnace.java

package ee;

import buildcraft.api.ISpecialInventory;
import buildcraft.api.Orientations;
import ee.core.GuiIds;
import forge.ISidedInventory;
import java.util.Random;
import net.minecraft.server.*;

// Referenced classes of package ee:
//            TileEE, IEEPowerNet, TileAlchChest, ItemKleinStar, 
//            EEBase, EEMaps, EEItem, EEBlock, 
//            BlockEEStone

public class TileRMFurnace extends TileEE
    implements ISpecialInventory, ISidedInventory, IEEPowerNet
{

    public TileRMFurnace()
    {
        items = new ItemStack[27];
        furnaceBurnTime = 0;
        currentItemBurnTime = 0;
        furnaceCookTime = 0;
        woftFactor = 1.0F;
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
        orientationx = ai;
    }

    private boolean isChest(TileEntity var1)
    {
        return (var1 instanceof TileEntityChest) || (var1 instanceof TileAlchChest);
    }

    public void onBlockRemoval()
    {
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

    public int getSize()
    {
        return items.length;
    }

    public int getMaxStackSize()
    {
        return 64;
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
                return var3;
            }
            var3 = items[var1].a(var2);
            if(items[var1].count == 0)
                items[var1] = null;
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
    }

    public boolean addItem(ItemStack var1, boolean var2, Orientations var3)
    {
        switch(this.orientationx[var3.ordinal()])
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
            if(var1 == null)
                break;
            if(getItemBurnTime(var1, true) > 0 && var1.id != Block.LOG.id)
            {
                if(items[0] == null)
                {
                    if(var2)
                    {
                        items[0] = var1.cloneItemStack();
                        for(; var1.count > 0; var1.count--);
                    }
                    return true;
                }
                if(items[0].doMaterialsMatch(var1) && items[0].count < items[0].getMaxStackSize())
                {
                    if(var2)
                        for(; items[0].count < items[0].getMaxStackSize() && var1.count > 0; var1.count--)
                            items[0].count++;

                    return true;
                }
                break;
            }
            if(FurnaceRecipes.getInstance().getSmeltingResult(var1) == null)
                break;
            for(int var4 = 1; var4 <= 13; var4++)
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

            break;
        }
        return false;
    }

    public ItemStack extractItem(boolean var1, Orientations var2)
    {
        switch(this.orientationx[var2.ordinal()])
        {
        case 2: // '\002'
            if(items[0] == null)
                return null;
            if(items[0].getItem() instanceof ItemKleinStar)
            {
                ItemStack var5 = items[0].cloneItemStack();
                if(var1)
                    items[0] = null;
                return var5;
            }
            // fall through

        case 1: // '\001'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
            for(int var3 = 10; var3 < items.length; var3++)
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

            // fall through

        default:
            return null;
        }
    }

    public String getName()
    {
        return "RM Furnace";
    }

    public void a(NBTTagCompound var1)
    {
        super.a(var1);
        NBTTagList var2 = var1.getList("Items");
        items = new ItemStack[getSize()];
        for(int var3 = 0; var3 < var2.size(); var3++)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.get(var3);
            byte var5 = var4.getByte("Slot");
            if(var5 >= 0 && var5 < items.length)
                items[var5] = ItemStack.a(var4);
        }

        woftFactor = var1.getFloat("TimeFactor");
        furnaceBurnTime = var1.getInt("BurnTime");
        furnaceCookTime = var1.getShort("CookTime");
        currentItemBurnTime = getItemBurnTime(items[1], false);
    }

    public void b(NBTTagCompound var1)
    {
        super.b(var1);
        var1.setInt("BurnTime", furnaceBurnTime);
        var1.setShort("CookTime", (short)furnaceCookTime);
        var1.setFloat("TimeFactor", woftFactor);
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

    public int getCookProgressScaled(int var1)
    {
        return world == null || EEProxy.isClient(world) ? 0 : ((furnaceCookTime + (!isBurning() || !canSmelt() ? 0 : 1)) * var1) / 3;
    }

    public int getBurnTimeRemainingScaled(int var1)
    {
        if(currentItemBurnTime == 0)
            currentItemBurnTime = 10;
        return (furnaceBurnTime * var1) / currentItemBurnTime;
    }

    public boolean isBurning()
    {
        return furnaceBurnTime > 0;
    }

    public void q_()
    {
        if(!clientFail())
        {
            woftFactor = EEBase.getPedestalFactor(world) * EEBase.getPlayerWatchFactor();
            boolean var1 = furnaceBurnTime > 0;
            boolean var2 = false;
            boolean var3 = false;
            if(furnaceBurnTime > 0)
            {
                furnaceBurnTime = (int)((float)furnaceBurnTime - (getWOFTReciprocal(woftFactor) < 1.0F ? 1.0F : getWOFTReciprocal(woftFactor)));
                if(furnaceBurnTime <= 0)
                {
                    furnaceBurnTime = 0;
                    var3 = true;
                }
            }
            if(!world.isStatic)
            {
                if(furnaceBurnTime <= 0 && canSmelt())
                {
                    currentItemBurnTime = furnaceBurnTime = getItemBurnTime(items[0], false) / 48;
                    if(furnaceBurnTime > 0)
                    {
                        var2 = true;
                        var3 = true;
                        if(items[0] != null && !EEBase.isKleinStar(items[0].id))
                        {
                            if(items[0].getItem().k())
                                items[0] = new ItemStack(items[0].getItem().j());
                            else
                                items[0].count--;
                            if(items[0].count == 0)
                                items[0] = null;
                        }
                    }
                }
                if(isBurning() && canSmelt())
                {
                    for(furnaceCookTime = (int)((float)furnaceCookTime + (getWOFTReciprocal(woftFactor) < 1.0F ? 1.0F : getWOFTReciprocal(woftFactor))); furnaceCookTime >= 3 && canSmelt();)
                    {
                        furnaceCookTime -= 3;
                        smeltItem();
                        var2 = true;
                        var3 = true;
                    }

                } else
                {
                    for(int var4 = 15; var4 < 27; var4++)
                        if(items[var4] != null && items[var4].count >= items[var4].getMaxStackSize() && tryDropInChest(new ItemStack(items[var4].getItem(), items[var4].count)))
                            items[var4] = null;

                    furnaceCookTime = 0;
                    furnaceBurnTime = 0;
                }
            }
            if(var2)
                update();
            if(var3)
                world.notify(x, y, z);
        }
    }

    private boolean canSmelt()
    {
        if(items[1] == null)
        {
            for(int var1 = 2; var1 <= 13; var1++)
            {
                if(items[var1] == null)
                    continue;
                items[1] = items[var1].cloneItemStack();
                items[var1] = null;
                break;
            }

            if(items[1] == null)
                return false;
        }
        ItemStack var3 = FurnaceRecipes.getInstance().getSmeltingResult(items[1]);
        if(var3 == null)
            return false;
        if(items[14] == null)
            return true;
        if(!items[14].doMaterialsMatch(var3))
        {
            if(tryDropInChest(items[14].cloneItemStack()))
            {
                items[14] = null;
                return true;
            }
            for(int var2 = 15; var2 <= 26; var2++)
            {
                if(items[var2] == null)
                {
                    items[var2] = items[14].cloneItemStack();
                    items[14] = null;
                    return true;
                }
                if(items[var2].doMaterialsMatch(items[14]))
                    while(items[14] != null && items[var2].count < 64) 
                    {
                        items[14].count--;
                        items[var2].count++;
                        if(items[14].count == 0)
                        {
                            items[14] = null;
                            return true;
                        }
                    }
            }

        }
        if(items[14].count < getMaxStackSize() && items[14].count < items[14].getMaxStackSize())
            return true;
        for(int var2 = 15; var2 < 27; var2++)
            if(items[var2] != null && items[var2].count >= items[var2].getMaxStackSize() && tryDropInChest(items[var2].cloneItemStack()))
                items[var2] = null;

        if(items[14] == null)
            return true;
        for(int var2 = 15; var2 <= 26; var2++)
        {
            if(items[var2] == null)
            {
                items[var2] = items[14].cloneItemStack();
                items[14] = null;
                return true;
            }
            if(items[var2].doMaterialsMatch(items[14]))
                while(items[14] != null && items[var2].count < 64) 
                {
                    items[14].count--;
                    items[var2].count++;
                    if(items[14].count == 0)
                    {
                        items[14] = null;
                        return true;
                    }
                }
        }

        return items[14].count < var3.getMaxStackSize();
    }

    public void smeltItem()
    {
        if(canSmelt())
        {
            ItemStack var1 = FurnaceRecipes.getInstance().getSmeltingResult(items[1]);
            boolean var2 = false;
            if(items[14] == null)
            {
                items[14] = var1.cloneItemStack();
                if(EEMaps.isOreBlock(items[1].id))
                    items[14].count++;
            } else
            if(items[14].id == var1.id)
            {
                items[14].count += var1.count;
                if(EEMaps.isOreBlock(items[1].id))
                    if(items[14].count < var1.getMaxStackSize())
                        items[14].count++;
                    else
                        var2 = true;
            }
            if(items[14].count == var1.getMaxStackSize())
                if(tryDropInChest(items[14]))
                {
                    items[14] = null;
                    if(var2)
                        items[14] = var1.cloneItemStack();
                } else
                {
                    for(int var3 = 15; var3 <= 26; var3++)
                    {
                        if(items[var3] != null)
                            continue;
                        items[var3] = items[14].cloneItemStack();
                        items[14] = null;
                        if(var2)
                            items[14] = var1.cloneItemStack();
                        break;
                    }

                }
            if(items[1].getItem().k())
                items[1] = new ItemStack(items[1].getItem().j());
            else
                items[1].count--;
            if(items[1].count < 1)
                items[1] = null;
            world.notify(x, y, z);
        }
    }

    public int getItemBurnTime(ItemStack var1, boolean var2)
    {
        if(var1 == null)
            return 0;
        int var3 = var1.getItem().id;
        if(EEBase.isKleinStar(var3) && EEBase.takeKleinStarPoints(var1, var2 ? 0 : 32, world))
        {
            return 1600;
        } else
        {
            int var4 = var1.getData();
            return var3 >= 256 || Block.byId[var3].material != Material.WOOD ? var3 != Item.STICK.id ? var3 != Item.COAL.id ? var3 != Item.LAVA_BUCKET.id ? var3 != Block.SAPLING.id ? var3 != EEItem.alchemicalCoal.id ? var3 != EEItem.mobiusFuel.id ? var3 != EEItem.aeternalisFuel.id ? ModLoader.addAllFuel(var3, var4) : 409600 : 102400 : 25600 : 100 : 3200 : 6400 : 100 : 300;
        }
    }

    public void f()
    {
    }

    public void g()
    {
    }

    public boolean a(EntityHuman var1)
    {
        return world.getTileEntity(x, y, z) == this ? var1.e((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D) <= 64D : false;
    }

    public int getStartInventorySide(int var1)
    {
        return var1 != 1 ? 1 : 0;
    }

    public int getSizeInventorySide(int var1)
    {
        return var1 != 1 ? 26 : 1;
    }

    public boolean onBlockActivated(EntityHuman var1)
    {
        if(!world.isStatic)
            var1.openGui(mod_EE.getInstance(), GuiIds.RM_FURNACE, world, x, y, z);
        return true;
    }

    public int getTextureForSide(int var1)
    {
        byte var2 = direction;
        return var1 != var2 ? EEBase.rmBlockSide : EEBase.rmFurnaceFront;
    }

    public int getInventoryTexture(int var1)
    {
        return var1 != 3 ? EEBase.rmBlockSide : EEBase.rmFurnaceFront;
    }

    public int getLightValue()
    {
        return isBurning() ? 15 : 0;
    }

    public void randomDisplayTick(Random var1)
    {
        if(isBurning())
        {
            byte var2 = direction;
            float var3 = (float)x + 0.5F;
            float var4 = (float)y + 0.0F + (var1.nextFloat() * 6F) / 16F;
            float var5 = (float)z + 0.5F;
            float var6 = 0.52F;
            float var7 = var1.nextFloat() * 0.6F - 0.3F;
            if(var2 == 4)
            {
                world.a("smoke", var3 - var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
                world.a("flame", var3 - var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
            } else
            if(var2 == 5)
            {
                world.a("smoke", var3 + var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
                world.a("flame", var3 + var6, var4, var5 + var7, 0.0D, 0.0D, 0.0D);
            } else
            if(var2 == 2)
            {
                world.a("smoke", var3 + var7, var4, var5 - var6, 0.0D, 0.0D, 0.0D);
                world.a("flame", var3 + var7, var4, var5 - var6, 0.0D, 0.0D, 0.0D);
            } else
            if(var2 == 3)
            {
                world.a("smoke", var3 + var7, var4, var5 + var6, 0.0D, 0.0D, 0.0D);
                world.a("flame", var3 + var7, var4, var5 + var6, 0.0D, 0.0D, 0.0D);
            }
            for(int var8 = 0; var8 < 4; var8++)
            {
                double var9 = (float)x + var1.nextFloat();
                double var11 = (float)y + var1.nextFloat();
                double var13 = (float)z + var1.nextFloat();
                double var15 = 0.0D;
                double var17 = 0.0D;
                double var19 = 0.0D;
                int var21 = var1.nextInt(2) * 2 - 1;
                var15 = ((double)var1.nextFloat() - 0.5D) * 0.5D;
                var17 = ((double)var1.nextFloat() - 0.5D) * 0.5D;
                var19 = ((double)var1.nextFloat() - 0.5D) * 0.5D;
                if((world.getTypeId(x - 1, y, z) != EEBlock.eeStone.id || world.getData(x - 1, y, z) != 3) && (world.getTypeId(x + 1, y, z) != EEBlock.eeStone.id || world.getData(x + 1, y, z) != 3))
                {
                    var9 = (double)x + 0.5D + 0.25D * (double)var21;
                    var15 = var1.nextFloat() * 2.0F * (float)var21;
                } else
                {
                    var13 = (double)z + 0.5D + 0.25D * (double)var21;
                    var19 = var1.nextFloat() * 2.0F * (float)var21;
                }
                world.a("portal", var9, var11, var13, var15, var17, var19);
            }

        }
    }

    public boolean receiveEnergy(int var1, byte var2, boolean var3)
    {
        if(canSmelt())
        {
            if(var3)
                furnaceBurnTime += var1;
            return true;
        } else
        {
            return false;
        }
    }

    public boolean sendEnergy(int var1, byte var2, boolean var3)
    {
        return false;
    }

    public boolean passEnergy(int var1, byte var2, boolean var3)
    {
        return false;
    }

    public void sendAllPackets(int i)
    {
    }

    public int relayBonus()
    {
        return 0;
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

    static int[] orientationx = null;

    private ItemStack items[];
    public int furnaceBurnTime;
    public int currentItemBurnTime;
    public int furnaceCookTime;
    public int nextinstack;
    public int nextoutstack;
    private float woftFactor;
    private static int $SWITCH_TABLE$buildcraft$api$Orientations[];
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/tekkit_server/mods/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 16 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
Couldn't fully decompile method $SWITCH_TABLE$buildcraft$api$Orientations
	Exit status: 0
	Caught exceptions:
*/