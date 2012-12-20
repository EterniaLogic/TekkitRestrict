/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   TileCollector3.java

package ee;

import buildcraft.api.ISpecialInventory;
import buildcraft.api.Orientations;
import ee.core.GuiIds;
import forge.ISidedInventory;
import java.io.PrintStream;
import java.util.Random;
import net.minecraft.server.*;

// Referenced classes of package ee:
//            TileEE, IEEPowerNet, ItemKleinStar, EEMaps, 
//            EEBase, EEItem

public class TileCollector3 extends TileEE
    implements ISpecialInventory, ISidedInventory, IEEPowerNet
{

    public TileCollector3()
    {
        items = new ItemStack[19];
        kleinPoints = 0;
        currentSunStatus = 1;
        collectorSunTime = 0;
        woftFactor = 1.0F;
        accumulate = 0;
        currentFuelProgress = 0;
        kleinProgressScaled = 0;
        sunTimeScaled = 0;
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

    public void onBlockRemoval()
    {
        for(int i = 0; i < getSize(); i++)
        {
            ItemStack itemstack = getItem(i);
            if(itemstack != null)
            {
                float f = world.random.nextFloat() * 0.8F + 0.1F;
                float f1 = world.random.nextFloat() * 0.8F + 0.1F;
                float f2 = world.random.nextFloat() * 0.8F + 0.1F;
                do
                {
                    if(itemstack.count <= 0)
                        break;
                    int j = world.random.nextInt(21) + 10;
                    if(j > itemstack.count)
                        j = itemstack.count;
                    itemstack.count -= j;
                    EntityItem entityitem = new EntityItem(world, (float)x + f, (float)y + f1, (float)z + f2, new ItemStack(itemstack.id, j, itemstack.getData()));
                    if(entityitem != null)
                    {
                        float f3 = 0.05F;
                        entityitem.motX = (float)world.random.nextGaussian() * f3;
                        entityitem.motY = (float)world.random.nextGaussian() * f3 + 0.2F;
                        entityitem.motZ = (float)world.random.nextGaussian() * f3;
                        if(entityitem.itemStack.getItem() instanceof ItemKleinStar)
                            ((ItemKleinStar)entityitem.itemStack.getItem()).setKleinPoints(entityitem.itemStack, ((ItemKleinStar)itemstack.getItem()).getKleinPoints(itemstack));
                        world.addEntity(entityitem);
                    }
                } while(true);
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

    public ItemStack getItem(int i)
    {
        return items[i];
    }

    public ItemStack splitStack(int i, int j)
    {
        if(items[i] != null)
        {
            if(items[i].count <= j)
            {
                ItemStack itemstack = items[i];
                items[i] = null;
                return itemstack;
            }
            ItemStack itemstack1 = items[i].a(j);
            if(items[i].count == 0)
                items[i] = null;
            return itemstack1;
        } else
        {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack)
    {
        items[i] = itemstack;
        if(itemstack != null && itemstack.count > getMaxStackSize())
            itemstack.count = getMaxStackSize();
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
            if(EEMaps.isFuel(var1))
            {
                for(int var4 = 0; var4 <= items.length - 3; var4++)
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
            if(!EEBase.isKleinStar(var1.id) || items[0] != null)
                break;
            if(var2)
            {
                items[0] = var1.cloneItemStack();
                for(; var1.count > 0; var1.count--);
            }
            return true;
        }
        return false;
    }

    public ItemStack extractItem(boolean flag, Orientations orientations)
    {
        switch(this.orientationx[orientations.ordinal()])
        {
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
            for(int i = 0; i < items.length; i++)
                if(items[i] != null && i != items.length - 1)
                    if(i == 0)
                    {
                        if(EEBase.isKleinStar(items[i].id))
                        {
                            ItemStack itemstack = items[i].cloneItemStack();
                            if(flag)
                                items[i] = null;
                            return itemstack;
                        }
                    } else
                    if(items[i].id == EEItem.aeternalisFuel.id || items[items.length - 1] != null && items[i].doMaterialsMatch(items[items.length - 1]))
                    {
                        ItemStack itemstack1 = items[i].cloneItemStack();
                        itemstack1.count = 1;
                        if(flag)
                        {
                            items[i].count--;
                            if(items[i].count < 1)
                                items[i] = null;
                        }
                        return itemstack1;
                    }

            break;
        }
        return null;
    }

    public String getName()
    {
        return "Energy Collector";
    }

    public void a(NBTTagCompound nbttagcompound)
    {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Items");
        items = new ItemStack[getSize()];
        for(int i = 0; i < nbttaglist.size(); i++)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.get(i);
            byte byte0 = nbttagcompound1.getByte("Slot");
            if(byte0 >= 0 && byte0 < items.length)
                items[byte0] = ItemStack.a(nbttagcompound1);
        }

        currentSunStatus = nbttagcompound.getShort("sunStatus");
        woftFactor = nbttagcompound.getFloat("timeFactor");
        accumulate = nbttagcompound.getInt("accumulate");
        collectorSunTime = nbttagcompound.getInt("sunTime");
    }

    public void b(NBTTagCompound nbttagcompound)
    {
        super.b(nbttagcompound);
        nbttagcompound.setInt("sunTime", collectorSunTime);
        nbttagcompound.setFloat("timeFactor", woftFactor);
        nbttagcompound.setInt("accumulate", accumulate);
        nbttagcompound.setShort("sunStatus", (short)currentSunStatus);
        NBTTagList nbttaglist = new NBTTagList();
        for(int i = 0; i < items.length; i++)
            if(items[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                items[i].save(nbttagcompound1);
                nbttaglist.add(nbttagcompound1);
            }

        nbttagcompound.set("Items", nbttaglist);
    }

    public int getSunProgressScaled(int i)
    {
        if(canUpgrade())
        {
            if(getFuelDifference() <= 0)
                return items[0] != null && EEBase.isKleinStar(items[0].id) ? 24 : 0;
            if((collectorSunTime * i) / (getFuelDifference() * 80) > 24)
                return 24;
            else
                return (collectorSunTime * i) / (getFuelDifference() * 80);
        } else
        {
            return 0;
        }
    }

    public boolean canUpgrade()
    {
        if(items[0] == null)
        {
            int i = items.length - 3;
            do
            {
                if(i < 1)
                    break;
                if(items[i] != null && (items[items.length - 1] == null || !items[i].doMaterialsMatch(items[items.length - 1])) && EEMaps.isFuel(items[i]) && items[i].getItem().id != EEItem.aeternalisFuel.id)
                {
                    items[0] = items[i].cloneItemStack();
                    items[i] = null;
                    break;
                }
                i--;
            } while(true);
        }
        if(items[0] == null)
            if(items[items.length - 2] != null)
            {
                if(EEMaps.isFuel(items[items.length - 2]) && items[items.length - 2].getItem().id != EEItem.aeternalisFuel.id)
                {
                    items[0] = items[items.length - 2].cloneItemStack();
                    items[items.length - 2] = null;
                }
            } else
            {
                return false;
            }
        if(items[0] == null)
            return false;
        if(EEBase.isKleinStar(items[0].id))
        {
            if(EEBase.canIncreaseKleinStarPoints(items[0], world))
                return true;
            if(items[items.length - 2] == null)
            {
                items[items.length - 2] = items[0].cloneItemStack();
                items[0] = null;
                return false;
            }
            for(int j = 1; j <= items.length - 3; j++)
                if(items[j] == null)
                {
                    items[j] = items[items.length - 2].cloneItemStack();
                    items[items.length - 2] = items[0].cloneItemStack();
                    items[0] = null;
                    return false;
                }

        }
        if(items[0].getItem().id != EEItem.aeternalisFuel.id && EEMaps.isFuel(items[0]))
            return true;
        return items[0].getItem().id == EEItem.darkMatter.id;
    }

    public boolean receiveEnergy(int i, byte byte0, boolean flag)
    {
        if(!isUsingPower())
            return false;
        if(flag)
        {
            accumulate += i;
            return true;
        } else
        {
            return true;
        }
    }

    public boolean sendEnergy(int i, byte byte0, boolean flag)
    {
        TileEntity tileentity = world.getTileEntity(x + (byte0 == 5 ? -1 : ((byte)(byte0 == 4 ? 1 : 0))), y + (byte0 == 1 ? -1 : ((byte)(byte0 == 0 ? 1 : 0))), z + (byte0 == 3 ? -1 : ((byte)(byte0 == 2 ? 1 : 0))));
        if(tileentity == null)
            return false;
        return (tileentity instanceof IEEPowerNet) && ((IEEPowerNet)tileentity).receiveEnergy(i + ((IEEPowerNet)tileentity).relayBonus(), byte0, flag);
    }

    public void sendAllPackets(int i)
    {
        int j = 0;
        for(byte byte0 = 0; byte0 < 6; byte0++)
            if(sendEnergy(i, byte0, false))
                j++;

        if(j == 0)
        {
            if(collectorSunTime <= 4800000 - i)
                collectorSunTime += i;
            return;
        }
        int k = i / j;
        if(k < 1)
            return;
        for(byte byte1 = 0; byte1 < 6; byte1++)
            sendEnergy(k, byte1, true);

    }

    public boolean passEnergy(int i, byte byte0, boolean flag)
    {
        return false;
    }

    public int relayBonus()
    {
        return 0;
    }

    public int getRealSunStatus()
    {
        if(world == null)
        {
            System.out.println("World object is turning a null for collectors..");
            return 0;
        }
        if(world.worldProvider.d)
            currentSunStatus = 16;
        else
            currentSunStatus = world.getLightLevel(x, y + 1, z) + 1;
        return currentSunStatus;
    }

    public int getSunStatus(int i)
    {
        return (getRealSunStatus() * i) / 16;
    }

    public void q_()
    {
        if(clientFail())
            return;
        if(!world.isStatic)
        {
            if(collectorSunTime < 0)
                collectorSunTime = 0;
            if(items[0] != null && (items[0].getItem() instanceof ItemKleinStar))
            {
                kleinProgressScaled = getKleinProgressScaled(48);
                kleinPoints = getKleinPoints(items[0]);
            }
            sunTimeScaled = getSunTimeScaled(48);
            currentFuelProgress = getSunProgressScaled(24);
            currentSunStatus = getSunStatus(12);
            isUsingPower = isUsingPower();
            for(int i = items.length - 3; i >= 2; i--)
                if(items[i] == null && items[i - 1] != null)
                {
                    items[i] = items[i - 1].cloneItemStack();
                    items[i - 1] = null;
                }

            woftFactor = EEBase.getPedestalFactor(world) * EEBase.getPlayerWatchFactor();
            if(isUsingPower())
            {
                collectorSunTime += getFactoredProduction();
                if(accumulate > 0)
                {
                    collectorSunTime += accumulate;
                    accumulate = 0;
                }
                if(EEBase.isKleinStar(items[0].id))
                {
                    for(int j = getFactoredProduction() * EEBase.getKleinLevel(items[0].id); j > 0 && collectorSunTime >= 80 && EEBase.addKleinStarPoints(items[0], 1, world); j--)
                        collectorSunTime -= 80;

                } else
                {
                    for(; getFuelDifference() > 0 && collectorSunTime >= getFuelDifference() * 80; uptierFuel())
                        collectorSunTime -= getFuelDifference() * 80;

                }
            } else
            {
                if(accumulate > 0)
                {
                    collectorSunTime += accumulate;
                    accumulate = 0;
                }
                sendAllPackets(getFactoredProduction());
            }
        }
    }

    private int getKleinPoints(ItemStack itemstack)
    {
        if(itemstack == null)
            return 0;
        if(itemstack.getItem() instanceof ItemKleinStar)
            return ((ItemKleinStar)itemstack.getItem()).getKleinPoints(itemstack);
        else
            return 0;
    }

    private int getSunTimeScaled(int i)
    {
        return (collectorSunTime * i) / 4800000;
    }

    private int getKleinProgressScaled(int i)
    {
        if(items[0] != null && (items[0].getItem() instanceof ItemKleinStar))
            return (((ItemKleinStar)items[0].getItem()).getKleinPoints(items[0]) * i) / ((ItemKleinStar)items[0].getItem()).getMaxPoints(items[0]);
        else
            return 0;
    }

    public int getFactoredProduction()
    {
        return (int)((float)getProduction() * getWOFTReciprocal(woftFactor));
    }

    public int getProduction()
    {
        return getRealSunStatus() * 10;
    }

    public boolean isUsingPower()
    {
        return canUpgrade() && canCollect();
    }

    private int getFuelDifference()
    {
        if(items[0] == null)
            return 0;
        else
            return getFuelLevel(getNextFuel(items[0])) - getFuelLevel(items[0]);
    }

    private int getFuelLevel(ItemStack itemstack)
    {
        return EEMaps.getEMC(itemstack);
    }

    private ItemStack getNextFuel(ItemStack itemstack)
    {
        int i = itemstack.id;
        int j = itemstack.getData();
        if(items[items.length - 1] == null)
        {
            if(EEMaps.isFuel(itemstack))
            {
                if(i == Item.COAL.id && j == 1)
                    return new ItemStack(Item.REDSTONE.id, 1, 0);
                if(i == Item.REDSTONE.id)
                    return new ItemStack(Item.COAL.id, 1, 0);
                if(i == Item.COAL.id)
                    return new ItemStack(Item.SULPHUR.id, 1, 0);
                if(i == Item.SULPHUR.id)
                    return new ItemStack(Item.GLOWSTONE_DUST.id, 1, 0);
                if(i == Item.GLOWSTONE_DUST.id)
                    return new ItemStack(EEItem.alchemicalCoal.id, 1, 0);
                if(i == EEItem.alchemicalCoal.id)
                    return new ItemStack(Item.BLAZE_POWDER.id, 1, 0);
                if(i == Item.BLAZE_POWDER.id)
                    return new ItemStack(Block.GLOWSTONE.id, 1, 0);
                if(i == Block.GLOWSTONE.id)
                    return new ItemStack(EEItem.mobiusFuel.id, 1, 0);
                if(i == EEItem.mobiusFuel.id)
                    return new ItemStack(EEItem.aeternalisFuel.id, 1, 0);
            }
        } else
        if(EEMaps.isFuel(items[items.length - 1]))
        {
            if(EEMaps.getEMC(i, j) < EEMaps.getEMC(items[items.length - 1].id, items[items.length - 1].getData()))
                return items[items.length - 1];
            else
                return null;
        } else
        {
            EntityItem entityitem = new EntityItem(world, x, y, z, items[items.length - 1].cloneItemStack());
            items[items.length - 1] = null;
            entityitem.pickupDelay = 10;
            world.addEntity(entityitem);
            return null;
        }
        return null;
    }

    private boolean canCollect()
    {
        if(items[0] == null)
        {
            int i = 1;
            do
            {
                if(i > items.length - 3)
                    break;
                if(items[i] != null && (items[items.length - 1] == null || items[items.length - 1] != null && items[items.length - 1].doMaterialsMatch(items[i])))
                {
                    items[0] = items[i].cloneItemStack();
                    items[i] = null;
                    break;
                }
                i++;
            } while(true);
            if(items[0] == null)
                return false;
        }
        if(EEBase.isKleinStar(items[0].id))
            return true;
        if(getNextFuel(items[0]) == null)
            return false;
        ItemStack itemstack = getNextFuel(items[0]).cloneItemStack();
        if(items[items.length - 2] == null)
            return true;
        if(!items[items.length - 2].doMaterialsMatch(itemstack))
        {
label0:
            for(int j = 1; j <= items.length - 3; j++)
                if(items[j] != null)
                {
                    if(!items[j].doMaterialsMatch(items[items.length - 2]))
                        continue;
                    do
                    {
                        if(items[items.length - 2] == null || items[j].count >= 64)
                            continue label0;
                        items[items.length - 2].count--;
                        items[j].count++;
                    } while(items[items.length - 2].count != 0);
                    items[items.length - 2] = null;
                    return true;
                } else
                {
                    items[j] = items[items.length - 2].cloneItemStack();
                    items[items.length - 2] = null;
                    return true;
                }

        }
        if(items[items.length - 2] != null && !items[items.length - 2].doMaterialsMatch(itemstack))
            return false;
        if(items[items.length - 2].count < getMaxStackSize() && items[items.length - 2].count < items[items.length - 2].getMaxStackSize())
            return true;
        for(int k = 1; k <= items.length - 2; k++)
            if(items[k] != null && (items[k].getItem().id == EEItem.mobiusFuel.id || items[items.length - 1] != null && items[k].doMaterialsMatch(items[items.length - 1])) && items[k].count >= items[k].getMaxStackSize() && tryDropInChest(new ItemStack(items[k].getItem(), items[k].count)))
                items[k] = null;

        if(items[items.length - 2] == null)
            return true;
label1:
        for(int l = 1; l <= items.length - 3; l++)
            if(items[l] != null)
            {
                if(!items[l].doMaterialsMatch(items[items.length - 2]))
                    continue;
                do
                {
                    if(items[items.length - 2] == null || items[l].count >= 64)
                        continue label1;
                    items[items.length - 2].count--;
                    items[l].count++;
                } while(items[items.length - 2].count != 0);
                items[items.length - 2] = null;
                return true;
            } else
            {
                items[l] = items[items.length - 2].cloneItemStack();
                items[items.length - 2] = null;
                return true;
            }

        return items[items.length - 2].count < itemstack.getMaxStackSize();
    }

    public void uptierFuel()
    {
        if(!canCollect())
            return;
        if(getNextFuel(items[0]) == null)
            return;
        ItemStack itemstack = getNextFuel(items[0]).cloneItemStack();
        itemstack.count = 1;
        if(items[items.length - 2] == null)
        {
            if(items[items.length - 1] != null && itemstack.doMaterialsMatch(items[items.length - 1]) || itemstack.getItem() == EEItem.aeternalisFuel)
            {
                if(!tryDropInChest(itemstack))
                    items[items.length - 2] = itemstack.cloneItemStack();
            } else
            {
                items[items.length - 2] = itemstack.cloneItemStack();
            }
        } else
        if(items[items.length - 2].id == itemstack.id)
        {
            if(items[items.length - 2].count == itemstack.getMaxStackSize())
            {
                if(items[items.length - 2].getItem().id == EEItem.aeternalisFuel.id || items[items.length - 1] != null && items[items.length - 2].doMaterialsMatch(items[items.length - 1]))
                {
                    if(tryDropInChest(items[items.length - 2].cloneItemStack()))
                        items[items.length - 2] = null;
                } else
                {
                    int i = 1;
                    do
                    {
                        if(i > items.length - 3)
                            break;
                        if(items[i] == null)
                        {
                            items[i] = items[items.length - 2].cloneItemStack();
                            items[items.length - 2] = null;
                            break;
                        }
                        if(items[i].doMaterialsMatch(items[items.length - 2]))
                            do
                            {
                                if(items[i].count >= items[i].getMaxStackSize() || items[items.length - 2] == null)
                                    break;
                                items[items.length - 2].count--;
                                items[i].count++;
                                if(items[items.length - 2].count == 0)
                                    items[items.length - 2] = null;
                            } while(true);
                        i++;
                    } while(true);
                }
            } else
            if(items[items.length - 1] != null && itemstack.doMaterialsMatch(items[items.length - 1]) || itemstack.getItem() == EEItem.aeternalisFuel)
            {
                if(!tryDropInChest(itemstack))
                    items[items.length - 2].count += itemstack.count;
            } else
            {
                items[items.length - 2].count += itemstack.count;
            }
        } else
        if((items[items.length - 1] != null && itemstack.doMaterialsMatch(items[items.length - 1]) || itemstack.getItem() == EEItem.aeternalisFuel) && tryDropInChest(items[items.length - 2].cloneItemStack()))
            items[items.length - 2] = null;
        if(items[0].getItem().k())
            items[0] = new ItemStack(items[0].getItem().j());
        else
            items[0].count--;
        if(items[0].count <= 0)
            items[0] = null;
    }

    public void f()
    {
    }

    public void g()
    {
    }

    public boolean a(EntityHuman entityhuman)
    {
        if(world.getTileEntity(x, y, z) != this)
            return false;
        return entityhuman.e((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D) <= 64D;
    }

    public int getStartInventorySide(int i)
    {
        return i == 0 ? 0 : 1;
    }

    public int getSizeInventorySide(int i)
    {
        if(i == 0)
            return 1;
        else
            return items.length - 2;
    }

    public boolean onBlockActivated(EntityHuman entityhuman)
    {
        if(!world.isStatic)
            entityhuman.openGui(mod_EE.getInstance(), GuiIds.COLLECTOR_3, world, x, y, z);
        return true;
    }

    public int getTextureForSide(int i)
    {
        if(i == 1)
            return EEBase.collector3Top;
        byte byte0 = direction;
        if(i != byte0)
            return EEBase.collectorSide;
        else
            return EEBase.collectorFront;
    }

    public int getInventoryTexture(int i)
    {
        if(i == 1)
            return EEBase.collector3Top;
        if(i == 3)
            return EEBase.collectorFront;
        else
            return EEBase.collectorSide;
    }

    public int getLightValue()
    {
        return 15;
    }

    public void onNeighborBlockChange(int j)
    {
    }

    public ItemStack splitWithoutUpdate(int i)
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
    /*static int[] $SWITCH_TABLE$buildcraft$api$Orientations()
    {
    	if(orientationx == null) return new int[]{0};
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
        return (int[])(orientationx = ai);
    }*/

    private ItemStack items[];
    public int currentSunStatus;
    public int collectorSunTime;
    private int accumulate;
    private float woftFactor;
    public int currentFuelProgress;
    public boolean isUsingPower;
    public int sunTimeScaled;
    public int kleinProgressScaled;
    public int kleinPoints;
    private static int $SWITCH_TABLE$buildcraft$api$Orientations[];
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/Downloads/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 35 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
Couldn't fully decompile method $SWITCH_TABLE$buildcraft$api$Orientations
	Exit status: 0
	Caught exceptions:
*/