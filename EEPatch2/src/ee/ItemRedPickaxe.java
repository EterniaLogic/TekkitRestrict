/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   ItemRedPickaxe.java

package ee;

import java.util.*;
import net.minecraft.server.*;

// Referenced classes of package ee:
//            ItemRedTool, EEBlock, BlockEEPedestal, BlockEEStone, 
//            EEBase, EEMaps

public class ItemRedPickaxe extends ItemRedTool
{

    protected ItemRedPickaxe(int var1)
    {
        super(var1, 3, 6, blocksEffectiveAgainst);
    }

    public float getStrVsBlock(ItemStack var1, Block var2, int var3)
    {
        float var4 = 1.0F;
        return var2.id == EEBlock.eePedestal.id && var3 == 0 || var2.id == EEBlock.eeStone.id && (var3 == 8 || var3 == 9) ? 1200000F / var4 : var2.material != Material.STONE && var2.material != Material.ORE || chargeLevel(var1) <= 0 ? var2.material == Material.STONE || var2.material == Material.ORE ? 16F / var4 : super.getDestroySpeed(var1, var2) / var4 : 16F + (16F * (float)chargeLevel(var1)) / var4;
    }

    public boolean a(ItemStack var1, int var2, int var3, int var4, int var5, EntityLiving var6)
    {
        EntityHuman var7 = null;
        if(var6 instanceof EntityHuman)
        {
            var7 = (EntityHuman)var6;
            if(EEBase.getToolMode(var7) != 0)
                if(EEBase.getToolMode(var7) == 1)
                    doTallImpact(var7, var1, var3, var4, var5, EEBase.direction(var7));
                else
                if(EEBase.getToolMode(var7) == 2)
                    doWideImpact(var7, var1, var3, var4, var5, EEBase.heading(var7));
                else
                if(EEBase.getToolMode(var7) == 3)
                    doLongImpact(var7, var1, var3, var4, var5, EEBase.direction(var7));
            return true;
        } else
        {
            return true;
        }
    }

    public void doLongImpact(EntityHuman ply, ItemStack var2, int var3, int var4, int var5, double var6)
    {
        World var1 = ply.world;
        cleanDroplist(var2);
        for(int var8 = 1; var8 <= 2; var8++)
        {
            int var9 = var3;
            int var10 = var4;
            int var11 = var5;
            if(var6 == 0.0D)
                var10 = var4 - var8;
            if(var6 == 1.0D)
                var10 += var8;
            if(var6 == 2D)
                var11 = var5 + var8;
            if(var6 == 3D)
                var9 = var3 - var8;
            if(var6 == 4D)
                var11 -= var8;
            if(var6 == 5D)
                var9 += var8;
            int var12 = var1.getTypeId(var9, var10, var11);
            int var13 = var1.getData(var9, var10, var11);
            if(canBreak(var12, var13) && attemptBreak(ply, var9, var10, var11))
                scanBlockAndBreak(var1, var2, var9, var10, var11);
        }

        ejectDropList(var1, var2, var3, (double)var4 + 0.5D, var5);
    }

    public void doWideImpact(EntityHuman ply, ItemStack var2, int var3, int var4, int var5, double var6)
    {
        World var1 = ply.world;
        cleanDroplist(var2);
        for(int var8 = -1; var8 <= 1; var8++)
        {
            int var9 = var3;
            int var11 = var5;
            if(var8 != 0)
            {
                if(var6 != 2D && var6 != 4D)
                    var11 = var5 + var8;
                else
                    var9 = var3 + var8;
                int var12 = var1.getTypeId(var9, var4, var11);
                int var13 = var1.getData(var9, var4, var11);
                if(canBreak(var12, var13) && attemptBreak(ply, var9, var4, var11))
                    scanBlockAndBreak(var1, var2, var9, var4, var11);
            }
        }

        ejectDropList(var1, var2, var3, (double)var4 + 0.5D, var5);
    }

    public void doTallImpact(EntityHuman ply, ItemStack var2, int var4, int var5, int var6, double var7)
    {
        World var1 = ply.world;
        cleanDroplist(var2);
        for(int var9 = -1; var9 <= 1; var9++)
        {
            int var10 = var4;
            int var11 = var5;
            int var12 = var6;
            if(var9 != 0)
            {
                if(var7 != 0.0D && var7 != 1.0D)
                    var11 = var5 + var9;
                else
                if(EEBase.heading(ply) != 2D && EEBase.heading(ply) != 4D)
                    var10 = var4 + var9;
                else
                    var12 = var6 + var9;
                int var13 = var1.getTypeId(var10, var11, var12);
                int var14 = var1.getData(var10, var11, var12);
                if(canBreak(var13, var14) && attemptBreak(ply, var10, var11, var12))
                    scanBlockAndBreak(var1, var2, var10, var11, var12);
            }
        }

        ejectDropList(var1, var2, var4, (double)var5 + 0.5D, var6);
    }

    public void doBreak(ItemStack var1, World var2, EntityHuman var3)
    {
        if(chargeLevel(var1) > 0)
        {
            int var4 = (int)EEBase.playerX(var3);
            int var5 = (int)EEBase.playerY(var3);
            int var6 = (int)EEBase.playerZ(var3);
            for(int var7 = -2; var7 <= 2; var7++)
            {
                for(int var8 = -2; var8 <= 2; var8++)
                {
                    for(int var9 = -2; var9 <= 2; var9++)
                    {
                        int var10 = var2.getTypeId(var4 + var7, var5 + var8, var6 + var9);
                        if(isOre(var10))
                            startSearch(var2, var1, var3, var10, var4 + var7, var5 + var8, var6 + var9, true);
                    }

                }

            }

        }
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
            for(int var3 = 0; var3 < blocksEffectiveAgainst.length; var3++)
                if(var1 == blocksEffectiveAgainst[var3].id)
                    return true;

            return canDestroySpecialBlock(Block.byId[var1]);
        } else
        {
            return false;
        }
    }

    public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7)
    {
        if(EEProxy.isClient(var3))
            return false;
        if(chargeLevel(var1) >= 1)
        {
            cleanDroplist(var1);
            int var8 = var3.getTypeId(var4, var5, var6);
            if(isOre(var8))
                startSearch(var3, var1, var2, var8, var4, var5, var6, false);
            return true;
        } else
        {
            return false;
        }
    }

    private boolean isOre(int var1)
    {
        return EEMaps.isOreBlock(var1);
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

    public void startSearch(World var1, ItemStack var2, EntityHuman var3, int var4, int var5, int var6, int var7, 
            boolean var8)
    {
        if(var4 == Block.BEDROCK.id)
        {
            var3.a("Nice try. You can't break bedrock.");
        } else
        {
            var1.makeSound(var3, "flash", 0.8F, 1.5F);
            if(var8)
                var3.C_();
            doBreakS(var1, var2, var3, var4, var5, var6, var7);
        }
    }

    public void doBreakS(World var1, ItemStack var2, EntityHuman var3, int var4, int var5, int var6, int var7)
    {
        if(!attemptBreak(var3, var5, var6, var7))
            return;
        scanBlockAndBreak(var1, var2, var5, var6, var7);
        for(int var8 = -1; var8 <= 1; var8++)
        {
            for(int var9 = -1; var9 <= 1; var9++)
            {
                for(int var10 = -1; var10 <= 1; var10++)
                    if(var4 != Block.REDSTONE_ORE.id && var4 != Block.GLOWING_REDSTONE_ORE.id)
                    {
                        if(var1.getTypeId(var5 + var8, var6 + var9, var7 + var10) == var4)
                            doBreakS(var1, var2, var3, var4, var5 + var8, var6 + var9, var7 + var10);
                    } else
                    if(var1.getTypeId(var5 + var8, var6 + var9, var7 + var10) == Block.GLOWING_REDSTONE_ORE.id || var1.getTypeId(var5 + var8, var6 + var9, var7 + var10) == Block.REDSTONE_ORE.id)
                        doBreakS(var1, var2, var3, var4, var5 + var8, var6 + var9, var7 + var10);

            }

        }

        ejectDropList(var1, var2, EEBase.playerX(var3), EEBase.playerY(var3), EEBase.playerZ(var3));
    }

    public boolean canDestroySpecialBlock(Block var1)
    {
        return var1 != Block.OBSIDIAN ? var1 == Block.DIAMOND_BLOCK || var1 == Block.DIAMOND_ORE ? true : var1 == Block.GOLD_BLOCK || var1 == Block.GOLD_ORE ? true : var1 == Block.IRON_BLOCK || var1 == Block.IRON_ORE ? true : var1 == Block.LAPIS_BLOCK || var1 == Block.LAPIS_ORE ? true : var1 == Block.REDSTONE_ORE || var1 == Block.GLOWING_REDSTONE_ORE ? true : var1.material != Material.STONE ? var1.material == Material.ORE : true : true;
    }

    public void doRelease(ItemStack var1, World var2, EntityHuman var3)
    {
        doBreak(var1, var2, var3);
    }

    public void doAlternate(ItemStack var1, World var2, EntityHuman var3)
    {
        EEBase.updateToolMode(var3);
    }

    public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman)
    {
    }

    private static Block blocksEffectiveAgainst[];

    static 
    {
        blocksEffectiveAgainst = (new Block[] {
            Block.COBBLESTONE, Block.DOUBLE_STEP, Block.STEP, Block.STONE, Block.SANDSTONE, Block.MOSSY_COBBLESTONE, Block.IRON_ORE, Block.IRON_BLOCK, Block.COAL_ORE, Block.GOLD_BLOCK, 
            Block.GOLD_ORE, Block.DIAMOND_ORE, Block.DIAMOND_BLOCK, Block.REDSTONE_ORE, Block.GLOWING_REDSTONE_ORE, Block.ICE, Block.NETHERRACK, Block.LAPIS_ORE, Block.LAPIS_BLOCK, Block.OBSIDIAN
        });
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/Downloads/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 33 ms
	Jad reported messages/errors:
The class file version is 51.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/