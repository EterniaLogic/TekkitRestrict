/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   ItemRedHammer.java

package ee;

import java.util.*;
import net.minecraft.server.*;

// Referenced classes of package ee:
//            ItemRedTool, EEBase

public class ItemRedHammer extends ItemRedTool
{

    protected ItemRedHammer(int var1)
    {
        super(var1, 3, 14, blocksEffectiveAgainst);
    }

    public boolean a(ItemStack var1, EntityLiving var2, EntityLiving var3)
    {
        return true;
    }

    public boolean a(ItemStack var1, int var2, int var3, int var4, int var5, EntityLiving var6)
    {
        EntityHuman var7 = null;
        if(var6 instanceof EntityHuman)
        {
            var7 = (EntityHuman)var6;
            if(EEBase.getHammerMode(var7))
                doMegaImpact(var7, var1, var3, var4, var5, EEBase.direction(var7));
            return true;
        } else
        {
            return true;
        }
    }

    public float getStrVsBlock(ItemStack var1, Block var2, int var3)
    {
        float var4 = 1.0F;
        return var2.material != Material.STONE || chargeLevel(var1) <= 0 ? var2.material != Material.STONE ? super.getDestroySpeed(var1, var2) / var4 : 14F / var4 : 30F / var4;
    }

    public void scanBlockAndBreak(World var1, ItemStack var2, int var3, int var4, int var5)
    {
        int var6 = var1.getTypeId(var3, var4, var5);
        int var7 = var1.getData(var3, var4, var5);
        ArrayList var8 = Block.byId[var6].getBlockDropped(var1, var3, var4, var5, var7, 0);
        ItemStack var10;
        for(Iterator var9 = var8.iterator(); var9.hasNext(); addToDroplist(var2, var10))
            var10 = (ItemStack)var9.next();

        var1.setTypeId(var3, var4, var5, 0);
        if(var1.random.nextInt(8) == 0)
            var1.a("largesmoke", var3, var4, var5, 0.0D, 0.0D, 0.0D);
        if(var1.random.nextInt(8) == 0)
            var1.a("explode", var3, var4, var5, 0.0D, 0.0D, 0.0D);
    }

    public boolean canBreak(int var1, int var2)
    {
        if(Block.byId[var1] == null)
            return false;
        if(!Block.byId[var1].b())
            return false;
        if(!Block.byId[var1].hasTileEntity(var2) && var1 != Block.BEDROCK.id)
        {
            if(Block.byId[var1].material == null)
                return false;
            if(Block.byId[var1].material == Material.STONE)
                return true;
            for(int var3 = 0; var3 < blocksEffectiveAgainst.length; var3++)
                if(var1 == blocksEffectiveAgainst[var3].id)
                    return true;

            return false;
        } else
        {
            return false;
        }
    }

    public void doMegaImpact(EntityHuman ply, ItemStack var2, int var3, int var4, int var5, double var6)
    {
        World var1 = ply.world;
        cleanDroplist(var2);
        for(int var8 = -1; var8 <= 1; var8++)
        {
            for(int var9 = -1; var9 <= 1; var9++)
            {
                int var10 = var3;
                int var11 = var4;
                int var12 = var5;
                if(var8 != 0 || var9 != 0)
                {
                    if(var6 != 0.0D && var6 != 1.0D)
                    {
                        if(var6 != 2D && var6 != 4D)
                        {
                            if(var6 == 3D || var6 == 5D)
                            {
                                var11 = var4 + var8;
                                var12 = var5 + var9;
                            }
                        } else
                        {
                            var10 = var3 + var8;
                            var11 = var4 + var9;
                        }
                    } else
                    {
                        var10 = var3 + var8;
                        var12 = var5 + var9;
                    }
                    int var13 = var1.getTypeId(var10, var11, var12);
                    int var14 = var1.getData(var10, var11, var12);
                    if(canBreak(var13, var14) && attemptBreak(ply, var10, var11, var12))
                        scanBlockAndBreak(var1, var2, var10, var11, var12);
                }
            }

        }

        ejectDropList(var1, var2, var3, (double)var4 + 0.5D, var5);
    }

    public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7)
    {
        if(EEProxy.isClient(var3))
            return false;
        boolean var8 = true;
        if(chargeLevel(var1) > 0)
        {
            cleanDroplist(var1);
            var2.C_();
            var3.makeSound(var2, "flash", 0.8F, 1.5F);
            for(int var9 = -(chargeLevel(var1) * (var7 != 5 ? (byte)(var7 != 4 ? 1 : 0) : 2)); var9 <= chargeLevel(var1) * (var7 != 5 ? var7 != 4 ? 1 : 2 : 0); var9++)
            {
                for(int var10 = -(chargeLevel(var1) * (var7 != 1 ? (byte)(var7 != 0 ? 1 : 0) : 2)); var10 <= chargeLevel(var1) * (var7 != 1 ? var7 != 0 ? 1 : 2 : 0); var10++)
                {
                    for(int var11 = -(chargeLevel(var1) * (var7 != 3 ? (byte)(var7 != 2 ? 1 : 0) : 2)); var11 <= chargeLevel(var1) * (var7 != 3 ? var7 != 2 ? 1 : 2 : 0); var11++)
                    {
                        int var12 = var4 + var9;
                        int var13 = var5 + var10;
                        int var14 = var6 + var11;
                        int var15 = var3.getTypeId(var12, var13, var14);
                        int var16 = var3.getData(var12, var13, var14);
                        if(canBreak(var15, var16) && attemptBreak(var2, var12, var13, var14))
                        {
                            if(getFuelRemaining(var1) < 1)
                            {
                                ConsumeReagent(var1, var2, var8);
                                var8 = false;
                            }
                            if(getFuelRemaining(var1) > 0)
                            {
                                ArrayList var17 = Block.byId[var15].getBlockDropped(var3, var12, var13, var14, var16, 0);
                                ItemStack var19;
                                for(Iterator var18 = var17.iterator(); var18.hasNext(); addToDroplist(var1, var19))
                                    var19 = (ItemStack)var18.next();

                                var3.setTypeId(var12, var13, var14, 0);
                                if(var3.random.nextInt(8) == 0)
                                    var3.a("largesmoke", var12, var13, var14, 0.0D, 0.0D, 0.0D);
                                if(var3.random.nextInt(8) == 0)
                                    var3.a("explode", var12, var13, var14, 0.0D, 0.0D, 0.0D);
                                setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);
                            }
                        }
                    }

                }

            }

            ejectDropList(var3, var1, var4, var5, var6);
        }
        return false;
    }

    public boolean canDestroySpecialBlock(Block var1)
    {
        return var1 != Block.OBSIDIAN ? var1 == Block.DIAMOND_BLOCK || var1 == Block.DIAMOND_ORE ? true : var1 == Block.GOLD_BLOCK || var1 == Block.GOLD_ORE ? true : var1 == Block.IRON_BLOCK || var1 == Block.IRON_ORE ? true : var1 == Block.LAPIS_BLOCK || var1 == Block.LAPIS_ORE ? true : var1 == Block.REDSTONE_ORE || var1 == Block.GLOWING_REDSTONE_ORE ? true : var1.material != Material.STONE ? var1.material == Material.ORE : true : true;
    }

    public void doAlternate(ItemStack var1, World var2, EntityHuman var3)
    {
        EEBase.updateHammerMode(var3, true);
    }

    public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman)
    {
    }

    public static boolean breakMode;
    private boolean haltImpact;
    private static Block blocksEffectiveAgainst[];

    static 
    {
        blocksEffectiveAgainst = (new Block[] {
            Block.COBBLESTONE, Block.STONE, Block.SANDSTONE, Block.MOSSY_COBBLESTONE, Block.IRON_ORE, Block.IRON_BLOCK, Block.COAL_ORE, Block.GOLD_BLOCK, Block.GOLD_ORE, Block.DIAMOND_ORE, 
            Block.DIAMOND_BLOCK, Block.REDSTONE_ORE, Block.GLOWING_REDSTONE_ORE, Block.ICE, Block.NETHERRACK, Block.LAPIS_ORE, Block.LAPIS_BLOCK, Block.OBSIDIAN
        });
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/Downloads/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 25 ms
	Jad reported messages/errors:
The class file version is 51.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/