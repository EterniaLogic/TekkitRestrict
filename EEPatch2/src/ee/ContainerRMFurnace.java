/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   ContainerRMFurnace.java

package ee;

import java.util.List;
import net.minecraft.server.*;

// Referenced classes of package ee:
//            TileRMFurnace

public class ContainerRMFurnace extends Container
{

    public ContainerRMFurnace(IInventory var1, TileRMFurnace var2)
    {
        cookTime = 0;
        burnTime = 0;
        itemBurnTime = 0;
        furnace = var2;
        setPlayer(((PlayerInventory)var1).player);
        a(new Slot(var2, 0, 65, 53));
        a(new Slot(var2, 1, 65, 17));
        for(int var3 = 0; var3 <= 2; var3++)
        {
            for(int var4 = 0; var4 <= 3; var4++)
                a(new Slot(var2, var3 * 4 + var4 + 2, 11 + var3 * 18, 8 + var4 * 18));

        }

        a(new Slot(var2, 14, 125, 35));
        for(int var3 = 0; var3 <= 2; var3++)
        {
            for(int var4 = 0; var4 <= 3; var4++)
                a(new Slot(var2, var3 * 4 + var4 + 15, 147 + var3 * 18, 8 + var4 * 18));

        }

        for(int var3 = 0; var3 < 3; var3++)
        {
            for(int var4 = 0; var4 < 9; var4++)
                a(new Slot(var1, var4 + var3 * 9 + 9, 24 + var4 * 18, 84 + var3 * 18));

        }

        for(int var3 = 0; var3 < 9; var3++)
            a(new Slot(var1, var3, 24 + var3 * 18, 142));

    }

    public IInventory getInventory()
    {
        return furnace;
    }

    //Update tick
    public void a()
    {
        super.a();
        for(int var1 = 0; var1 < listeners.size(); var1++)
        {
            ICrafting var2 = (ICrafting)listeners.get(var1);
            if(cookTime != furnace.furnaceCookTime || !initialized)
                var2.setContainerData(this, 0, furnace.furnaceCookTime);
            if(burnTime != furnace.furnaceBurnTime || !initialized)
                var2.setContainerData(this, 1, furnace.furnaceBurnTime);
            if(itemBurnTime != furnace.currentItemBurnTime || !initialized)
                var2.setContainerData(this, 2, furnace.currentItemBurnTime);
        }

        cookTime = furnace.furnaceCookTime;
        burnTime = furnace.furnaceBurnTime;
        itemBurnTime = furnace.currentItemBurnTime;
        initialized = true;
    }

    public void updateProgressBar(int type, int time)
    {
        if(type == 0)
            furnace.furnaceCookTime = time;
        if(type == 1)
            furnace.furnaceBurnTime = time;
        if(type == 2)
            furnace.currentItemBurnTime = time;
    }

    public ItemStack a(int slot)
    {
        ItemStack var2 = null;
        Slot var3 = (Slot)e.get(slot);
        if(var3 != null && var3.c())
        {
            ItemStack var4 = var3.getItem();
            var2 = var4.cloneItemStack(); // <--
            if(slot >= 14 && slot <= 26)
            {
                if(!a(var4, 27, 62, true))
                {
                    if(var4.count == 0)
                        var3.set(null);
                    return null;
                }
            } else
            if(slot >= 27 && slot < 53)
            {
                if(furnace.getItemBurnTime(var4, true) > 0)
                {
                    if(!a(var4, 0, 0, true))
                    {
                        if(var4.count == 0)
                            var3.set(null);
                        return null;
                    }
                } else
                {
                    if(!a(var4, 1, 13, false))
                    {
                        if(var4.count == 0)
                            var3.set(null);
                        return null;
                    }
                    if(!a(var4, 53, 62, false))
                    {
                        if(var4.count == 0)
                            var3.set(null);
                        return null;
                    }
                }
            } else
            if(slot >= 54 && slot < 63)
            {
                if(furnace.getItemBurnTime(var4, true) > 0)
                {
                    if(!a(var4, 0, 0, true))
                    {
                        if(var4.count == 0)
                            var3.set(null);
                        return null;
                    }
                } else
                {
                    if(!a(var4, 1, 13, false))
                    {
                        if(var4.count == 0)
                            var3.set(null);
                        return null;
                    }
                    if(!a(var4, 27, 53, false))
                    {
                        if(var4.count == 0)
                            var3.set(null);
                        return null;
                    }
                }
            } else
            if(!a(var4, 27, 62, false))
            {
                if(var4.count == 0)
                    var3.set(null);
                return null;
            }
            if(var4.count == 0)
                var3.set(null);
            else
                var3.d();
            if(var4.count == var2.count)
                return null;
            var3.c(var4);
        }
        return var2;
    }

    public void addSlotListener(ICrafting icrafting)
    {
        super.addSlotListener(icrafting);
        icrafting.setContainerData(this, 0, furnace.furnaceCookTime);
        icrafting.setContainerData(this, 1, furnace.furnaceBurnTime);
        icrafting.setContainerData(this, 2, furnace.currentItemBurnTime);
    }

    public boolean b(EntityHuman var1)
    {
        return furnace.a(var1);
    }

    private TileRMFurnace furnace;
    private int cookTime;
    private int burnTime;
    private int itemBurnTime;
    private boolean initialized;
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/tekkit_server/mods/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 5 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/