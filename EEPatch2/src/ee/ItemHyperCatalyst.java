/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   ItemHyperCatalyst.java

package ee;

import java.util.*;
import net.minecraft.server.*;

// Referenced classes of package ee:
//            ItemEECharged, EntityHyperkinesis, EEBase

public class ItemHyperCatalyst extends ItemEECharged
{

    public ItemHyperCatalyst(int var1)
    {
        super(var1, 7);
    }

    public void doBreak(ItemStack var1, World var2, EntityHuman var3)
    {
        int var4 = 1;
        if(chargeLevel(var1) > 2)
            var4++;
        if(chargeLevel(var1) > 4)
            var4++;
        if(chargeLevel(var1) > 6)
            var4++;
        var2.makeSound(var3, "wall", 1.0F, 1.0F);
        var2.addEntity(new EntityHyperkinesis(var2, var3, (chargeLevel(var1) + 1) / 2, var4));
    }

    public void doBreak2(ItemStack var1, World var2, EntityHuman var3)
    {
        if(getCooldown(var1) <= 0)
        {
            initCooldown(var1);
            var2.makeSound(var3, "destruct", 0.5F, 1.0F);
            var3.C_();
            cleanDroplist(var1);
            boolean var4 = true;
            double var5 = EEBase.direction(var3);
            int var7 = (int)EEBase.playerX(var3);
            int var8 = (int)(EEBase.playerY(var3) + (double)var3.getHeadHeight());
            int var9 = (int)EEBase.playerZ(var3);
            if(var5 == 0.0D)
            {
                for(int var10 = -2; var10 >= -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) - 1; var10--)
                {
                    for(int var11 = -1; var11 <= 1; var11++)
                    {
                        for(int var12 = -1; var12 <= 1; var12++)
                        {
                            if(getFuelRemaining(var1) < 1)
                                if(var10 == -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) - 1 && var11 == 1 && var12 == 1)
                                {
                                    ConsumeReagent(var1, var3, var4);
                                    var4 = false;
                                } else
                                {
                                    ConsumeReagent(var1, var3, false);
                                }
                            if(getFuelRemaining(var1) > 0)
                                breakBlock(var1, var3, var7 + var11, var8 + var10, var9 + var12);
                        }

                    }

                }

            } else
            if(var5 == 1.0D)
            {
                for(int var10 = 2; var10 <= (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) + 1; var10++)
                {
                    for(int var11 = -1; var11 <= 1; var11++)
                    {
                        for(int var12 = -1; var12 <= 1; var12++)
                        {
                            if(getFuelRemaining(var1) < 1)
                                if(var10 == (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) + 1 && var11 == 1 && var12 == 1)
                                {
                                    ConsumeReagent(var1, var3, var4);
                                    var4 = false;
                                } else
                                {
                                    ConsumeReagent(var1, var3, false);
                                }
                            if(getFuelRemaining(var1) > 0)
                                breakBlock(var1, var3, var7 + var11, var8 + var10, var9 + var12);
                        }

                    }

                }

            } else
            if(var5 == 2D)
            {
                for(int var10 = 1; var10 <= (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1); var10++)
                {
                    for(int var11 = -1; var11 <= 1; var11++)
                    {
                        for(int var12 = -1; var12 <= 1; var12++)
                        {
                            if(getFuelRemaining(var1) < 1)
                                if(var10 == (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) && var11 == 1 && var12 == 1)
                                {
                                    ConsumeReagent(var1, var3, var4);
                                    var4 = false;
                                } else
                                {
                                    ConsumeReagent(var1, var3, false);
                                }
                            if(getFuelRemaining(var1) > 0)
                                breakBlock(var1, var3, var7 + var11, var8 + var12, var9 + var10);
                        }

                    }

                }

            } else
            if(var5 == 3D)
            {
                for(int var10 = -1; var10 >= -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)); var10--)
                {
                    for(int var11 = -1; var11 <= 1; var11++)
                    {
                        for(int var12 = -1; var12 <= 1; var12++)
                        {
                            if(getFuelRemaining(var1) < 1)
                                if(var10 == -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) && var11 == 1 && var12 == 1)
                                {
                                    ConsumeReagent(var1, var3, var4);
                                    var4 = false;
                                } else
                                {
                                    ConsumeReagent(var1, var3, false);
                                }
                            if(getFuelRemaining(var1) > 0)
                                breakBlock(var1, var3, var7 + var10, var8 + var12, var9 + var11);
                        }

                    }

                }

            } else
            if(var5 == 4D)
            {
                for(int var10 = -1; var10 >= -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)); var10--)
                {
                    for(int var11 = -1; var11 <= 1; var11++)
                    {
                        for(int var12 = -1; var12 <= 1; var12++)
                        {
                            if(getFuelRemaining(var1) < 1)
                                if(var10 == -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) && var11 == 1 && var12 == 1)
                                {
                                    ConsumeReagent(var1, var3, var4);
                                    var4 = false;
                                } else
                                {
                                    ConsumeReagent(var1, var3, false);
                                }
                            if(getFuelRemaining(var1) > 0)
                                breakBlock(var1, var3, var7 + var11, var8 + var12, var9 + var10);
                        }

                    }

                }

            } else
            if(var5 == 5D)
            {
                for(int var10 = 1; var10 <= (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1); var10++)
                {
                    for(int var11 = -1; var11 <= 1; var11++)
                    {
                        for(int var12 = -1; var12 <= 1; var12++)
                        {
                            if(getFuelRemaining(var1) < 1)
                                if(var10 == (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) && var11 == 1 && var12 == 1)
                                {
                                    ConsumeReagent(var1, var3, var4);
                                    var4 = false;
                                } else
                                {
                                    ConsumeReagent(var1, var3, false);
                                }
                            if(getFuelRemaining(var1) > 0)
                                breakBlock(var1, var3, var7 + var10, var8 + var12, var9 + var11);
                        }

                    }

                }

            }
            ejectDropList(var2, var1, var3.locX, var3.locY, var3.locZ);
        }
    }

    public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7)
    {
        if(EEProxy.isClient(var3))
            return false;
        if(getCooldown(var1) > 0)
            return false;
        initCooldown(var1);
        var3.makeSound(var2, "destruct", 0.5F, 1.0F);
        cleanDroplist(var1);
        boolean var8 = true;
        int var9 = var4;
        int var10 = var5;
        int var11 = var6;
        if(var7 == 0)
        {
            for(int var12 = 0; var12 < (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1); var12++)
            {
                for(int var13 = -1; var13 <= 1; var13++)
                {
                    for(int var14 = -1; var14 <= 1; var14++)
                    {
                        if(getFuelRemaining(var1) < 1)
                            if(var12 == (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) - 1 && var13 == 1 && var14 == 1)
                            {
                                ConsumeReagent(var1, var2, var8);
                                var8 = false;
                            } else
                            {
                                ConsumeReagent(var1, var2, false);
                            }
                        if(getFuelRemaining(var1) > 0)
                            breakBlock(var1, var2, var9 + var13, var10 + var12, var11 + var14);
                    }

                }

            }

        } else
        if(var7 == 1)
        {
            for(int var12 = 0; var12 > -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)); var12--)
            {
                for(int var13 = -1; var13 <= 1; var13++)
                {
                    for(int var14 = -1; var14 <= 1; var14++)
                    {
                        if(getFuelRemaining(var1) < 1)
                            if(var12 == -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) + 1 && var13 == 1 && var14 == 1)
                            {
                                ConsumeReagent(var1, var2, var8);
                                var8 = false;
                            } else
                            {
                                ConsumeReagent(var1, var2, false);
                            }
                        if(getFuelRemaining(var1) > 0)
                            breakBlock(var1, var2, var9 + var13, var10 + var12, var11 + var14);
                    }

                }

            }

        } else
        if(var7 == 2)
        {
            for(int var12 = 0; var12 < (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1); var12++)
            {
                for(int var13 = -1; var13 <= 1; var13++)
                {
                    for(int var14 = -1; var14 <= 1; var14++)
                    {
                        if(getFuelRemaining(var1) < 1)
                            if(var12 == (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) - 1 && var13 == 1 && var14 == 1)
                            {
                                ConsumeReagent(var1, var2, var8);
                                var8 = false;
                            } else
                            {
                                ConsumeReagent(var1, var2, false);
                            }
                        if(getFuelRemaining(var1) > 0)
                            breakBlock(var1, var2, var9 + var13, var10 + var14, var11 + var12);
                    }

                }

            }

        } else
        if(var7 == 3)
        {
            for(int var12 = 0; var12 > -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)); var12--)
            {
                for(int var13 = -1; var13 <= 1; var13++)
                {
                    for(int var14 = -1; var14 <= 1; var14++)
                    {
                        if(getFuelRemaining(var1) < 1)
                            if(var12 == -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) + 1 && var13 == 1 && var14 == 1)
                            {
                                ConsumeReagent(var1, var2, var8);
                                var8 = false;
                            } else
                            {
                                ConsumeReagent(var1, var2, false);
                            }
                        if(getFuelRemaining(var1) > 0)
                            breakBlock(var1, var2, var9 + var13, var10 + var14, var11 + var12);
                    }

                }

            }

        } else
        if(var7 == 4)
        {
            for(int var12 = 0; var12 < (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1); var12++)
            {
                for(int var13 = -1; var13 <= 1; var13++)
                {
                    for(int var14 = -1; var14 <= 1; var14++)
                    {
                        if(getFuelRemaining(var1) < 1)
                            if(var12 == (chargeLevel(var1) + 1) * (chargeLevel(var1) + 1) - 1 && var13 == 1 && var14 == 1)
                            {
                                ConsumeReagent(var1, var2, var8);
                                var8 = false;
                            } else
                            {
                                ConsumeReagent(var1, var2, false);
                            }
                        if(getFuelRemaining(var1) > 0)
                            breakBlock(var1, var2, var9 + var12, var10 + var14, var11 + var13);
                    }

                }

            }

        } else
        if(var7 == 5)
        {
            for(int var12 = 0; var12 > -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)); var12--)
            {
                for(int var13 = -1; var13 <= 1; var13++)
                {
                    for(int var14 = -1; var14 <= 1; var14++)
                    {
                        if(getFuelRemaining(var1) < 1)
                            if(var12 == -((chargeLevel(var1) + 1) * (chargeLevel(var1) + 1)) + 1 && var14 == 1 && var13 == 1)
                            {
                                ConsumeReagent(var1, var2, var8);
                                var8 = false;
                            } else
                            {
                                ConsumeReagent(var1, var2, false);
                            }
                        if(getFuelRemaining(var1) > 0)
                            breakBlock(var1, var2, var9 + var12, var10 + var14, var11 + var13);
                    }

                }

            }

        }
        ejectDropList(var3, var1, var4, var5, var6);
        return true;
    }

    public void breakBlock(ItemStack var1, EntityHuman ply, int var3, int var4, int var5)
    {
        if(!attemptBreak(ply, var3, var4, var5))
            return;
        World var2 = ply.world;
        int var6 = var2.getTypeId(var3, var4, var5);
        int var7 = var2.getData(var3, var4, var5);
        if(Block.byId[var6] != null && var6 != 0 && Block.byId[var6].getHardness(var7) >= 0.0F && Block.byId[var6].getHardness(var7) <= 10F)
        {
            ArrayList var8 = Block.byId[var6].getBlockDropped(var2, var3, var4, var5, var7, 0);
            ItemStack var10;
            for(Iterator var9 = var8.iterator(); var9.hasNext(); addToDroplist(var1, var10))
                var10 = (ItemStack)var9.next();

            setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);
            var2.setTypeId(var3, var4, var5, 0);
            if(var2.random.nextInt(8) == 0)
                var2.a("largesmoke", var3, var4, var5, 0.0D, 0.0D, 0.0D);
            if(var2.random.nextInt(8) == 0)
                var2.a("explode", var3, var4, var5, 0.0D, 0.0D, 0.0D);
        }
    }

    public ItemStack a(ItemStack var1, World var2, EntityHuman var3)
    {
        if(EEProxy.isClient(var2))
        {
            return var1;
        } else
        {
            doBreak2(var1, var2, var3);
            return var1;
        }
    }

    public void doToggle(ItemStack itemstack1, World world1, EntityHuman entityhuman1)
    {
    }

    public void doRelease(ItemStack var1, World var2, EntityHuman var3)
    {
        if(!EEProxy.isClient(var2))
            doBreak(var1, var2, var3);
    }

    public void doPassive(ItemStack var1, World var2, EntityHuman var3)
    {
        decCooldown(var1);
    }

    public void setCooldown(ItemStack var1, int var2)
    {
        setInteger(var1, "cooldown", var2);
    }

    public int getCooldown(ItemStack var1)
    {
        return getInteger(var1, "cooldown");
    }

    public void decCooldown(ItemStack var1)
    {
        setCooldown(var1, getCooldown(var1) - 1);
    }

    public void initCooldown(ItemStack var1)
    {
        setCooldown(var1, 5);
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