/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   ItemHyperkineticLens.java

package ee;

import net.minecraft.server.*;

// Referenced classes of package ee:
//            ItemEECharged, EntityHyperkinesis

public class ItemHyperkineticLens extends ItemEECharged
{

    public ItemHyperkineticLens(int var1)
    {
        super(var1, 3);
    }

    public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int i, int j, int k, int l)
    {
        return false;
    }

    public void doBreak(ItemStack var1, World var2, EntityHuman var3)
    {
        int var4 = 1;
        if(chargeLevel(var1) > 0)
            var4++;
        if(chargeLevel(var1) > 1)
        {
            var4++;
            var4++;
        }
        if(chargeLevel(var1) > 2)
        {
            var4++;
            var4++;
        }
        var2.makeSound(var3, "wall", 1.0F, 1.0F);
        var2.addEntity(new EntityHyperkinesis(var2, var3, chargeLevel(var1), var4));
    }

    public ItemStack a(ItemStack var1, World var2, EntityHuman var3)
    {
        if(EEProxy.isClient(var2))
        {
            return var1;
        } else
        {
            doBreak(var1, var2, var3);
            return var1;
        }
    }

    public void doRelease(ItemStack var1, World var2, EntityHuman var3)
    {
        if(!EEProxy.isClient(var2))
            doBreak(var1, var2, var3);
    }

    public void doLeftClick(ItemStack var1, World var2, EntityHuman var3)
    {
        if(!EEProxy.isClient(var2))
            doBreak(var1, var2, var3);
    }

    public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman)
    {
    }

    public boolean itemCharging;
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/Downloads/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 16 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/