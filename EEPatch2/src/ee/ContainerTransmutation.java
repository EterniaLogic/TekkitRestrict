/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   ContainerTransmutation.java

package ee;

import java.util.List;
import net.minecraft.server.*;

// Referenced classes of package ee:
//            TransTabletData, SlotTransmuteInput, SlotConsume, SlotTransmute, 
//            EEBase, EEMaps

public class ContainerTransmutation extends Container
{

    public ContainerTransmutation(PlayerInventory var1, EntityHuman var2, TransTabletData var3)
    {
        player = var2;
        setPlayer(var2);
        transGrid = var3;
        learned = var3.learned;
        lock = var3.isFuelLocked() ? 2 : ((int) (var3.isMatterLocked() ? 1 : 0));
        latentEnergy = var3.getLatentEnergy();
        currentEnergy = var3.getCurrentEnergy();
        a(new SlotTransmuteInput(var3, 0, 43, 29));
        a(new SlotTransmuteInput(var3, 1, 34, 47));
        a(new SlotTransmuteInput(var3, 2, 52, 47));
        a(new SlotTransmuteInput(var3, 3, 16, 56));
        a(new SlotTransmuteInput(var3, 4, 70, 56));
        a(new SlotTransmuteInput(var3, 5, 34, 65));
        a(new SlotTransmuteInput(var3, 6, 52, 65));
        a(new SlotTransmuteInput(var3, 7, 43, 83));
        a(new SlotTransmuteInput(var3, 8, 158, 56));
        a(new SlotConsume(var3, 9, 107, 103));
        a(new SlotTransmute(var3, 10, 158, 15));
        a(new SlotTransmute(var3, 11, 140, 19));
        a(new SlotTransmute(var3, 12, 176, 19));
        a(new SlotTransmute(var3, 13, 123, 36));
        a(new SlotTransmute(var3, 14, 158, 37));
        a(new SlotTransmute(var3, 15, 193, 36));
        a(new SlotTransmute(var3, 16, 116, 56));
        a(new SlotTransmute(var3, 17, 139, 56));
        a(new SlotTransmute(var3, 18, 177, 56));
        a(new SlotTransmute(var3, 19, 199, 56));
        a(new SlotTransmute(var3, 20, 123, 76));
        a(new SlotTransmute(var3, 21, 158, 75));
        a(new SlotTransmute(var3, 22, 193, 76));
        a(new SlotTransmute(var3, 23, 140, 93));
        a(new SlotTransmute(var3, 24, 176, 93));
        a(new SlotTransmute(var3, 25, 158, 97));
        for(int var4 = 0; var4 < 3; var4++)
        {
            for(int var5 = 0; var5 < 9; var5++)
                a(new Slot(player.inventory, var5 + var4 * 9 + 9, 35 + var5 * 18, 123 + var4 * 18));

        }

        for(int var4 = 0; var4 < 9; var4++)
            a(new Slot(player.inventory, var4, 35 + var4 * 18, 181));

        a(transGrid);
        EEBase.watchTransGrid(player);
    }

    public IInventory getInventory()
    {
        return transGrid;
    }

    public void setItem(int var1, ItemStack var2)
    {
        super.setItem(var1, var2);
        if(var1 < 26)
            if(var2 == null)
                transGrid.items[var1] = null;
            else
                transGrid.items[var1] = var2.cloneItemStack();
        a(transGrid);
    }

    public void a(IInventory var1)
    {
        a();
        if(!EEProxy.isClient(EEProxy.theWorld))
        {
            transGrid.update();
            transGrid.displayResults(transGrid.latentEnergy + transGrid.currentEnergy);
        }
    }

    public void a()
    {
        super.a();
        for(int var1 = 0; var1 < listeners.size(); var1++)
        {
            ICrafting var2 = (ICrafting)listeners.get(var1);
            if(latentEnergy != transGrid.latentEnergy || !initialized)
                var2.setContainerData(this, 0, transGrid.latentEnergy & 65535);
            if(latentEnergy != transGrid.latentEnergy || !initialized)
                var2.setContainerData(this, 1, transGrid.latentEnergy >>> 16);
            if(currentEnergy != transGrid.currentEnergy || !initialized)
                var2.setContainerData(this, 2, transGrid.currentEnergy & 65535);
            if(currentEnergy != transGrid.currentEnergy || !initialized)
                var2.setContainerData(this, 3, transGrid.currentEnergy >>> 16);
            if(learned != transGrid.learned || !initialized)
                var2.setContainerData(this, 4, transGrid.learned);
            if(lock != (transGrid.isFuelLocked() ? 2 : ((int) (transGrid.isMatterLocked() ? 1 : 0))) || !initialized)
                var2.setContainerData(this, 5, transGrid.isFuelLocked() ? 2 : ((int) (transGrid.isMatterLocked() ? 1 : 0)));
        }

        learned = transGrid.learned;
        lock = transGrid.isFuelLocked() ? 2 : ((int) (transGrid.isMatterLocked() ? 1 : 0));
        latentEnergy = transGrid.latentEnergy;
        currentEnergy = transGrid.currentEnergy;
        initialized = true;
    }

    public void updateProgressBar(int var1, int var2)
    {
        if(var1 == 0)
            transGrid.latentEnergy = transGrid.latentEnergy & -65536 | var2;
        if(var1 == 1)
            transGrid.latentEnergy = transGrid.latentEnergy & 65535 | var2 << 16;
        if(var1 == 2)
            transGrid.currentEnergy = transGrid.currentEnergy & -65536 | var2;
        if(var1 == 3)
            transGrid.currentEnergy = transGrid.currentEnergy & 65535 | var2 << 16;
        if(var1 == 4)
            transGrid.learned = var2;
        if(var1 == 5)
        {
            if(var2 == 0)
                transGrid.unlock();
            if(var2 == 1)
            {
                transGrid.fuelUnlock();
                transGrid.matterLock();
            }
            if(var2 == 2)
            {
                transGrid.matterUnlock();
                transGrid.fuelLock();
            }
        }
    }

    public boolean b(EntityHuman var1)
    {
        return true;
    }

    public void a(EntityHuman var1)
    {
        super.a(var1);
        EEBase.closeTransGrid(player);
        if(!player.world.isStatic)
        {
            for(int var2 = 0; var2 < 25; var2++)
            {
                ItemStack var3 = transGrid.splitWithoutUpdate(var2);
                if(var3 != null)
                    player.drop(var3);
            }

        }
    }

    public ItemStack a(int slotNum)
    {
        ItemStack var2 = null;
        Slot slot = (Slot)e.get(slotNum);
        ItemStack var4 = null;
        if(slotNum > 9 && slotNum < 26 && slot != null && slot.c())
            var4 = slot.getItem().cloneItemStack();
        if(slot != null && slot.c())
        {
            ItemStack var5 = slot.getItem();
            var2 = var5.cloneItemStack();
            if(slotNum <= 8)
            {
                if(!a(var5, 26, 62, true))
                    slot.set(null);
            } else
            if(slotNum > 9 && slotNum < 26)
            {
                if(!grabResult(var5, (Slot)e.get(slotNum), 26, 62, false))
                    slot.set(null);
            } else
            if(slotNum >= 26 && slotNum < 62)
            {
                if((EEMaps.getEMC(var5) > 0 || EEBase.isKleinStar(var5.id)) && !a(var5, 0, 8, false))
                {
                    if(var5.count == 0)
                        slot.set(null);
                    return null;
                }
            } else
            if(!a(var5, 26, 62, false))
            {
                if(var5.count == 0)
                    slot.set(null);
                return null;
            }
            if(var5.count == 0)
            {
                if(slotNum > 9 && slotNum < 26)
                    var5.count = 1;
                else
                    slot.set(null);
            } else
            {
                slot.d();
            }
            if(var5.count == var2.count)
                if(slotNum > 9 && slotNum < 26 && var4 != null)
                    return var4;
                else
                    return null;
            if(slotNum > 9 && slotNum < 26 && transGrid.latentEnergy + transGrid.currentEnergy < EEMaps.getEMC(var5))
                return null;
            slot.c(var5);
        }
        if(var4 != null && slotNum > 9 && slotNum < 26)
            slot.set(var4);
        return var2;
    }

    protected boolean grabResult(ItemStack var1, Slot var2, int var3, int var4, boolean var5)
    {
        if(transGrid.latentEnergy + transGrid.currentEnergy < EEMaps.getEMC(var1))
            return false;
        var2.c(var1);
        boolean var6 = false;
        int var7 = var3;
        if(var5)
            var7 = var4 - 1;
        if(var1.isStackable())
            while(var1.count > 0 && (!var5 && var7 < var4 || var5 && var7 >= var3)) 
            {
                Slot var8 = (Slot)e.get(var7);
                ItemStack var9 = var8.getItem();
                if(var9 != null && var9.id == var1.id && (!var1.usesData() || var1.getData() == var9.getData()) && ItemStack.equals(var1, var9))
                {
                    int var10 = var9.count + var1.count;
                    if(var10 <= var1.getMaxStackSize())
                    {
                        var1.count = 0;
                        var9.count = var10;
                        var8.d();
                        var6 = true;
                    } else
                    if(var9.count < var1.getMaxStackSize())
                    {
                        var1.count -= var1.getMaxStackSize() - var9.count;
                        var9.count = var1.getMaxStackSize();
                        var8.d();
                        var6 = true;
                    }
                }
                if(var5)
                    var7--;
                else
                    var7++;
            }
        if(var1.count > 0)
        {
            int var11;
            if(var5)
                var11 = var4 - 1;
            else
                var11 = var3;
            while(!var5 && var11 < var4 || var5 && var11 >= var3) 
            {
                Slot var12 = (Slot)e.get(var11);
                ItemStack var13 = var12.getItem();
                if(var13 == null)
                {
                    var12.set(var1.cloneItemStack());
                    var12.d();
                    var1.count = 0;
                    var6 = true;
                    break;
                }
                if(var5)
                    var11--;
                else
                    var11++;
            }
        }
        var1.count = 1;
        return var6;
    }

    public ItemStack clickItem(int var1, int var2, boolean var3, EntityHuman var4)
    {
        ItemStack var5 = null;
        if(var2 > 1)
            return null;
        if(var2 == 0 || var2 == 1)
        {
            PlayerInventory var6 = var4.inventory;
            if(var1 == -999)
            {
                if(var6.getCarried() != null && var1 == -999)
                {
                    if(var2 == 0)
                    {
                        var4.drop(var6.getCarried());
                        var6.setCarried(null);
                    }
                    if(var2 == 1)
                    {
                        var4.drop(var6.getCarried().a(1));
                        if(var6.getCarried().count == 0)
                            var6.setCarried(null);
                    }
                }
            } else
            if(var3)
            {
                ItemStack var7 = a(var1);
                if(var7 != null)
                {
                    int var8 = var7.id;
                    var5 = var7.cloneItemStack();
                    Slot var9 = (Slot)e.get(var1);
                    if(var9 != null && var9.getItem() != null && var9.getItem().id == var8 && var9.getItem().isStackable())
                        retrySlotClick(var1, var2, 1, var9.getItem().getMaxStackSize(), var3, var4);
                }
            } else
            {
                if(var1 < 0)
                    return null;
                Slot var12 = (Slot)e.get(var1);
                if(var12 != null)
                {
                    var12.d();
                    ItemStack var13 = var12.getItem();
                    ItemStack var14 = var6.getCarried();
                    if(var13 != null)
                        var5 = var13.cloneItemStack();
                    if(var13 == null)
                    {
                        if(var14 != null && var12.isAllowed(var14))
                        {
                            int var10 = var2 == 0 ? var14.count : 1;
                            if(var10 > var12.a())
                                var10 = var12.a();
                            var12.set(var14.a(var10));
                            if(var14.count == 0)
                                var6.setCarried(null);
                        }
                    } else
                    if(var14 == null)
                    {
                        int var10 = var2 == 0 ? var13.count : (var13.count + 1) / 2;
                        ItemStack var11 = var12.a(var10);
                        var6.setCarried(var11);
                        if(var1 >= 10 && var1 <= 25)
                            var12.set(new ItemStack(var11.id, 1, var11.getData()));
                        else
                        if(var13.count == 0)
                            var12.set(null);
                        var12.c(var6.getCarried());
                    } else
                    if(var12.isAllowed(var14))
                    {
                        if(var13.id == var14.id && (!var13.usesData() || var13.getData() == var14.getData()) && ItemStack.equals(var13, var14))
                        {
                            int var10 = var2 == 0 ? var14.count : 1;
                            if(var10 > var12.a() - var13.count)
                                var10 = var12.a() - var13.count;
                            if(var10 > var14.getMaxStackSize() - var13.count)
                                var10 = var14.getMaxStackSize() - var13.count;
                            var14.a(var10);
                            if(var14.count == 0)
                                var6.setCarried(null);
                            var13.count += var10;
                        } else
                        if(var14.count <= var12.a())
                        {
                            var12.set(var14);
                            var6.setCarried(var13);
                        }
                    } else
                    if(var13.id == var14.id && var14.getMaxStackSize() > 1 && (!var13.usesData() || var13.getData() == var14.getData()) && ItemStack.equals(var13, var14))
                    {
                        int var10 = var13.count;
                        if(var10 > 0 && var10 + var14.count <= var14.getMaxStackSize())
                        {
                            var14.count += var10;
                            if(var1 < 10 || var1 > 25)
                            {
                                var13.a(var10);
                                if(var13.count == 0)
                                    var12.set(null);
                            }
                            var12.c(var6.getCarried());
                        }
                    }
                }
            }
        }
        return var5;
    }

    protected void retrySlotClick(int var1, int var2, int var3, int var4, boolean var5, EntityHuman var6)
    {
        if(var3 < var4)
        {
            var3++;
            slotClick(var1, var2, var3, var4, var5, var6);
        }
    }

    public ItemStack slotClick(int var1, int var2, int var3, int var4, boolean var5, EntityHuman var6)
    {
        ItemStack var7 = null;
        if(var2 > 1)
            return null;
        if(var2 == 0 || var2 == 1)
        {
            PlayerInventory var8 = var6.inventory;
            if(var1 == -999)
            {
                if(var8.getCarried() != null && var1 == -999)
                {
                    if(var2 == 0)
                    {
                        var6.drop(var8.getCarried());
                        var8.setCarried(null);
                    }
                    if(var2 == 1)
                    {
                        var6.drop(var8.getCarried().a(1));
                        if(var8.getCarried().count == 0)
                            var8.setCarried(null);
                    }
                }
            } else
            if(var5)
            {
                ItemStack var9 = a(var1);
                if(var9 != null)
                {
                    int var10 = var9.id;
                    var7 = var9.cloneItemStack();
                    Slot var11 = (Slot)e.get(var1);
                    if(var11 != null && var11.getItem() != null && var11.getItem().id == var10)
                        retrySlotClick(var1, var2, var3, var4, var5, var6);
                }
            } else
            {
                if(var1 < 0)
                    return null;
                Slot var14 = (Slot)e.get(var1);
                if(var14 != null)
                {
                    var14.d();
                    ItemStack var16 = var14.getItem();
                    ItemStack var15 = var8.getCarried();
                    if(var16 != null)
                        var7 = var16.cloneItemStack();
                    if(var16 == null)
                    {
                        if(var15 != null && var14.isAllowed(var15))
                        {
                            int var12 = var2 == 0 ? var15.count : 1;
                            if(var12 > var14.a())
                                var12 = var14.a();
                            var14.set(var15.a(var12));
                            if(var15.count == 0)
                                var8.setCarried(null);
                        }
                    } else
                    if(var15 == null)
                    {
                        int var12 = var2 == 0 ? var16.count : (var16.count + 1) / 2;
                        ItemStack var13 = var14.a(var12);
                        var8.setCarried(var13);
                        if(var1 >= 10 && var1 <= 25)
                            var14.set(new ItemStack(var13.id, 1, var13.getData()));
                        else
                        if(var16.count == 0)
                            var14.set(null);
                        var14.c(var8.getCarried());
                    } else
                    if(var14.isAllowed(var15))
                    {
                        if(var16.id == var15.id && (!var16.usesData() || var16.getData() == var15.getData()) && ItemStack.equals(var16, var15))
                        {
                            int var12 = var2 == 0 ? var15.count : 1;
                            if(var12 > var14.a() - var16.count)
                                var12 = var14.a() - var16.count;
                            if(var12 > var15.getMaxStackSize() - var16.count)
                                var12 = var15.getMaxStackSize() - var16.count;
                            var15.a(var12);
                            if(var15.count == 0)
                                var8.setCarried(null);
                            var16.count += var12;
                        } else
                        if(var15.count <= var14.a())
                        {
                            var14.set(var15);
                            var8.setCarried(var16);
                        }
                    } else
                    if(var16.id == var15.id && var15.getMaxStackSize() > 1 && (!var16.usesData() || var16.getData() == var15.getData()) && ItemStack.equals(var16, var15))
                    {
                        int var12 = var16.count;
                        if(var12 > 0 && var12 + var15.count <= var15.getMaxStackSize())
                        {
                            var15.count += var12;
                            if(var1 < 10 || var1 > 25)
                            {
                                var16.a(var12);
                                if(var16.count == 0)
                                    var14.set(null);
                            }
                            var14.c(var8.getCarried());
                        }
                    }
                }
            }
        }
        return var7;
    }

    private EntityHuman player;
    private TransTabletData transGrid;
    private int latentEnergy;
    private int currentEnergy;
    private int learned;
    private int lock;
    private boolean initialized;
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/tekkit_server/mods/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 40 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/