/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   ItemRedKatar.java

package ee;

import java.util.*;
import net.minecraft.server.*;

// Referenced classes of package ee:
//            ItemRedTool, EEMaps, ItemEECharged, EEBase

public class ItemRedKatar extends ItemRedTool
{

    protected ItemRedKatar(int var1)
    {
        super(var1, 4, 18, blocksEffectiveAgainst);
    }

    public boolean a(ItemStack var1, int var2, int var3, int var4, int var5, EntityLiving var6)
    {
        boolean var7 = false;
        if(!EEMaps.isLeaf(var2) && var2 != Block.WEB.id && var2 != Block.VINE.id && var2 != BlockFlower.LONG_GRASS.id && var2 != BlockFlower.DEAD_BUSH.id)
            var7 = true;
        if(!var7)
            EEProxy.dropBlockAsItemStack(Block.byId[var2], var6, var3, var4, var5, new ItemStack(var2, 1, var2 != Block.LEAVES.id ? ((int) (((ItemEECharged)var1.getItem()).getShort(var1, "lastMeta"))) : ((ItemEECharged)var1.getItem()).getShort(var1, "lastMeta") & 3));
        return super.a(var1, var2, var3, var4, var5, var6);
    }

    public boolean canDestroySpecialBlock(Block var1)
    {
        return var1.id == Block.WEB.id;
    }

    public boolean ConsumeReagent(int var1, ItemStack var2, EntityHuman var3, boolean var4)
    {
        if(getFuelRemaining(var2) >= 16)
        {
            setFuelRemaining(var2, getFuelRemaining(var2) - 16);
            return true;
        }
        int var5 = getFuelRemaining(var2);
        while(getFuelRemaining(var2) < 16) 
        {
            ConsumeReagent(var2, var3, var4);
            if(var5 == getFuelRemaining(var2))
                break;
            var5 = getFuelRemaining(var2);
            if(getFuelRemaining(var2) >= 16)
            {
                setFuelRemaining(var2, getFuelRemaining(var2) - 16);
                return true;
            }
        }
        return false;
    }

    public float getStrVsBlock(ItemStack var1, Block var2, int var3)
    {
        if(getShort(var1, "lastMeta") != var3)
            setShort(var1, "lastMeta", var3);
        if(var2.id != Block.VINE.id && var2.id != Block.LEAVES.id && var2.id != Block.WEB.id)
        {
            if(var2.id == Block.WOOL.id)
                return 5F;
            if(var2.material != Material.EARTH && var2.material != Material.GRASS)
            {
                return var2.material != Material.WOOD ? super.getDestroySpeed(var1, var2) : 18F + (float)(chargeLevel(var1) * 2);
            } else
            {
                float var4 = 18F + (float)(chargeLevel(var1) * 4);
                return var4;
            }
        } else
        {
            return 15F;
        }
    }

    public void doSwordBreak(ItemStack var1, World var2, EntityHuman var3)
    {
        if(chargeLevel(var1) > 0)
        {
            boolean var4 = false;
            int var5;
            for(var5 = 1; var5 <= chargeLevel(var1); var5++)
            {
                if(var5 == chargeLevel(var1))
                    var4 = true;
                if(ConsumeReagent(1, var1, var3, var4))
                    continue;
                var5--;
                break;
            }

            if(var5 < 1)
                return;
            var3.C_();
            var2.makeSound(var3, "flash", 0.8F, 1.5F);
            List var6 = var2.getEntities(var3, AxisAlignedBB.b((double)(float)var3.locX - ((double)var5 / 1.5D + 2D), var3.locY - ((double)var5 / 1.5D + 2D), (double)(float)var3.locZ - ((double)var5 / 1.5D + 2D), (double)(float)var3.locX + (double)var5 / 1.5D + 2D, var3.locY + (double)var5 / 1.5D + 2D, (double)(float)var3.locZ + (double)var5 / 1.5D + 2D));
            for(int var7 = 0; var7 < var6.size(); var7++)
                if((var6.get(var7) instanceof EntityLiving) && (EEBase.getSwordMode(var3) || (var6.get(var7) instanceof EntityMonster)))
                {
                    Entity var8 = (Entity)var6.get(var7);
                    var8.damageEntity(DamageSource.playerAttack(var3), weaponDamage + chargeLevel(var1) * 2);
                }

        }
    }

    public boolean interactWith(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7)
    {
        if(EEProxy.isClient(var3))
            return false;
        int var8 = var3.getTypeId(var4, var5, var6);
        chargeLevel(var1);
        if(EEMaps.isLeaf(var8) || var8 == Block.WEB.id || var8 == Block.VINE.id || var8 == BlockFlower.LONG_GRASS.id || var8 == BlockFlower.DEAD_BUSH.id)
            onItemUseShears(var1, var2, var3, var4, var5, var6, var7);
        if((var8 == Block.DIRT.id || var8 == Block.GRASS.id) && var3.getTypeId(var4, var5 + 1, var6) == 0)
            onItemUseHoe(var1, var2, var3, var4, var5, var6, var7);
        if(EEMaps.isWood(var8))
            onItemUseAxe(var1, var2, var3, var4, var5, var6, var7);
        return false;
    }

    public boolean onItemUseShears(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7)
    {
        if(chargeLevel(var1) > 0)
        {
            boolean var8 = false;
            cleanDroplist(var1);
            var2.C_();
            var3.makeSound(var2, "flash", 0.8F, 1.5F);
            for(int var9 = -(chargeLevel(var1) + 2); var9 <= chargeLevel(var1) + 2; var9++)
            {
                for(int var10 = -(chargeLevel(var1) + 2); var10 <= chargeLevel(var1) + 2; var10++)
                {
                    for(int var11 = -(chargeLevel(var1) + 2); var11 <= chargeLevel(var1) + 2; var11++)
                    {
                        int var12 = var3.getTypeId(var4 + var9, var5 + var10, var6 + var11);
                        if(attemptBreak(var2, var4 + var9, var5 + var10, var6 + var11) && (EEMaps.isLeaf(var12) || var12 == Block.VINE.id || var12 == Block.WEB.id || var12 == BlockFlower.LONG_GRASS.id || var12 == BlockFlower.DEAD_BUSH.id))
                        {
                            if(getFuelRemaining(var1) < 1)
                                ConsumeReagent(var1, var2, false);
                            if(getFuelRemaining(var1) > 0)
                            {
                                int var13 = var3.getData(var4 + var9, var5 + var10, var6 + var11);
                                if(!EEMaps.isLeaf(var12) && var12 != Block.VINE.id && var12 != Block.WEB.id && var12 != Block.LONG_GRASS.id && var12 != Block.DEAD_BUSH.id)
                                {
                                    ArrayList var14 = Block.byId[var12].getBlockDropped(var3, var4 + var9, var5 + var10, var6 + var11, var13, 0);
                                    ItemStack var16;
                                    for(Iterator var15 = var14.iterator(); var15.hasNext(); addToDroplist(var1, var16))
                                        var16 = (ItemStack)var15.next();

                                } else
                                if(var12 == Block.LEAVES.id)
                                    addToDroplist(var1, new ItemStack(Block.LEAVES.id, 1, var13 & 3));
                                else
                                    addToDroplist(var1, new ItemStack(Block.byId[var12], 1, var13));
                                setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);
                                var3.setTypeId(var4 + var9, var5 + var10, var6 + var11, 0);
                                if(var3.random.nextInt(8) == 0)
                                    var3.a("largesmoke", var4 + var9, var5 + var10, var6 + var11, 0.0D, 0.0D, 0.0D);
                                if(var3.random.nextInt(8) == 0)
                                    var3.a("explode", var4 + var9, var5 + var10, var6 + var11, 0.0D, 0.0D, 0.0D);
                            }
                        }
                    }

                }

            }

            ejectDropList(var3, var1, var4, var5, var6);
        }
        return false;
    }

    public boolean onItemUseHoe(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7)
    {
        int var8;
        int var9;
        if(chargeLevel(var1) > 0)
        {
            var2.C_();
            var3.makeSound(var2, "flash", 0.8F, 1.5F);
            if(var3.getTypeId(var4, var5, var6) == BlockFlower.YELLOW_FLOWER.id || var3.getTypeId(var4, var5, var6) == BlockFlower.RED_ROSE.id || var3.getTypeId(var4, var5, var6) == BlockFlower.BROWN_MUSHROOM.id || var3.getTypeId(var4, var5, var6) == BlockFlower.RED_MUSHROOM.id || var3.getTypeId(var4, var5, var6) == BlockLongGrass.LONG_GRASS.id || var3.getTypeId(var4, var5, var6) == BlockDeadBush.DEAD_BUSH.id)
                var5--;
            for(var8 = -(chargeLevel(var1) * chargeLevel(var1)) - 1; var8 <= chargeLevel(var1) * chargeLevel(var1) + 1; var8++)
                for(var9 = -(chargeLevel(var1) * chargeLevel(var1)) - 1; var9 <= chargeLevel(var1) * chargeLevel(var1) + 1; var9++)
                {
                    int var15 = var4 + var8;
                    int var12 = var6 + var9;
                    int var13 = var3.getTypeId(var15, var5, var12);
                    int var14 = var3.getTypeId(var15, var5 + 1, var12);
                    if(var14 == BlockFlower.YELLOW_FLOWER.id || var14 == BlockFlower.RED_ROSE.id || var14 == BlockFlower.BROWN_MUSHROOM.id || var14 == BlockFlower.RED_MUSHROOM.id || var14 == BlockLongGrass.LONG_GRASS.id || var14 == BlockDeadBush.DEAD_BUSH.id)
                    {
                        Block.byId[var14].dropNaturally(var3, var15, var5 + 1, var12, var3.getData(var15, var5 + 1, var12), 1.0F, 1);
                        var3.setTypeId(var15, var5 + 1, var12, 0);
                        var14 = 0;
                    }
                    if(var14 == 0 && (var13 == Block.DIRT.id || var13 == Block.GRASS.id))
                    {
                        if(getFuelRemaining(var1) < 1)
                            ConsumeReagent(var1, var2, false);
                        if(getFuelRemaining(var1) > 0)
                        {
                            var3.setTypeId(var15, var5, var12, 60);
                            setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);
                            if(var3.random.nextInt(8) == 0)
                                var3.a("largesmoke", var15, var5, var12, 0.0D, 0.0D, 0.0D);
                            if(var3.random.nextInt(8) == 0)
                                var3.a("explode", var15, var5, var12, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }


            return false;
        }
        if(var2 != null && !var2.d(var4, var5, var6))
            return false;
        var8 = var3.getTypeId(var4, var5, var6);
        var9 = var3.getTypeId(var4, var5 + 1, var6);
        if((var7 == 0 || var9 != 0 || var8 != Block.GRASS.id) && var8 != Block.DIRT.id)
            return false;
        Block var10 = Block.SOIL;
        var3.makeSound((float)var4 + 0.5F, (float)var5 + 0.5F, (float)var6 + 0.5F, var10.stepSound.getName(), (var10.stepSound.getVolume1() + 1.0F) / 2.0F, var10.stepSound.getVolume2() * 0.8F);
        if(var3.isStatic)
        {
            return true;
        } else
        {
            var3.setTypeId(var4, var5, var6, var10.id);
            return true;
        }
    }

    public boolean onItemUseAxe(ItemStack var1, EntityHuman var2, World var3, int var4, int var5, int var6, int var7)
    {
        if(chargeLevel(var1) > 0)
        {
            double var8 = var4;
            double var10 = var5;
            double var12 = var6;
            boolean var14 = false;
            cleanDroplist(var1);
            if(chargeLevel(var1) < 1)
                return false;
            var2.C_();
            var3.makeSound(var2, "flash", 0.8F, 1.5F);
            for(int var15 = -(chargeLevel(var1) * 2) + 1; var15 <= chargeLevel(var1) * 2 - 1; var15++)
            {
                for(int var16 = chargeLevel(var1) * 2 + 1; var16 >= -2; var16--)
                {
                    for(int var17 = -(chargeLevel(var1) * 2) + 1; var17 <= chargeLevel(var1) * 2 - 1; var17++)
                    {
                        int var18 = (int)(var8 + (double)var15);
                        int var19 = (int)(var10 + (double)var16);
                        int var20 = (int)(var12 + (double)var17);
                        int var21 = var3.getTypeId(var18, var19, var20);
                        if((EEMaps.isWood(var21) || EEMaps.isLeaf(var21)) && attemptBreak(var2, var18, var19, var20))
                        {
                            if(getFuelRemaining(var1) < 1)
                                if(var15 == chargeLevel(var1) && var17 == chargeLevel(var1))
                                {
                                    ConsumeReagent(var1, var2, var14);
                                    var14 = false;
                                } else
                                {
                                    ConsumeReagent(var1, var2, false);
                                }
                            if(getFuelRemaining(var1) > 0)
                            {
                                int var22 = var3.getData(var18, var19, var20);
                                ArrayList var23 = Block.byId[var21].getBlockDropped(var3, var18, var19, var20, var22, 0);
                                ItemStack var25;
                                for(Iterator var24 = var23.iterator(); var24.hasNext(); addToDroplist(var1, var25))
                                    var25 = (ItemStack)var24.next();

                                var3.setTypeId(var18, var19, var20, 0);
                                if(!EEMaps.isLeaf(var21))
                                    setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 1);
                                if(var3.random.nextInt(8) == 0)
                                    var3.a("largesmoke", var18, var19, var20, 0.0D, 0.0D, 0.0D);
                                if(var3.random.nextInt(8) == 0)
                                    var3.a("explode", var18, var19, var20, 0.0D, 0.0D, 0.0D);
                            }
                        }
                    }

                }

            }

            ejectDropList(var3, var1, var8, var10, var12);
        }
        return false;
    }

    public boolean isFull3D()
    {
        return true;
    }

    public EnumAnimation d(ItemStack var1)
    {
        return EnumAnimation.d;
    }

    public int c(ItemStack var1)
    {
        return 72000;
    }

    public ItemStack a(ItemStack var1, World var2, EntityHuman var3)
    {
        if(EEProxy.isClient(var2))
        {
            return var1;
        } else
        {
            var3.a(var1, c(var1));
            return var1;
        }
    }

    public void doShear(ItemStack var1, World var2, EntityHuman var3, Entity var4)
    {
        if(chargeLevel(var1) > 0)
        {
            boolean var5 = false;
            int var6 = 0;
            if(getFuelRemaining(var1) < 10)
                ConsumeReagent(var1, var3, false);
            if(getFuelRemaining(var1) < 10)
                ConsumeReagent(var1, var3, false);
            if(getFuelRemaining(var1) < 10)
                ConsumeReagent(var1, var3, false);
            while(getFuelRemaining(var1) >= 10 && var6 < chargeLevel(var1)) 
            {
                setShort(var1, "fuelRemaining", getFuelRemaining(var1) - 10);
                var6++;
                if(getFuelRemaining(var1) < 10)
                    ConsumeReagent(var1, var3, false);
                if(getFuelRemaining(var1) < 10)
                    ConsumeReagent(var1, var3, false);
                if(getFuelRemaining(var1) < 10)
                    ConsumeReagent(var1, var3, false);
            }
            if(var6 > 0)
            {
                var3.C_();
                var2.makeSound(var3, "flash", 0.8F, 1.5F);
                int var7 = 3 * var6;
                if(var4 instanceof EntitySheep)
                {
                    if(var2.random.nextInt(100) < var7)
                    {
                        EntitySheep var8 = new EntitySheep(var2);
                        double var9 = var4.locX - var3.locX;
                        double var11 = var4.locZ - var3.locZ;
                        if(var9 < 0.0D)
                            var9 *= -1D;
                        if(var11 < 0.0D)
                            var11 *= -1D;
                        var9 += var4.locX;
                        var11 += var4.locZ;
                        double var13 = var4.locY;
                        for(int var15 = -5; var15 <= 5; var15++)
                        {
                            if(var2.getTypeId((int)var9, (int)var13 + var15, (int)var11) == 0 || var2.getTypeId((int)var9, (int)var13 + var15 + 1, (int)var11) != 0)
                                continue;
                            var8.setPosition(var9, var13 + (double)var15 + 1.0D, var11);
                            var8.setColor(((EntitySheep)var4).getColor());
                            var2.addEntity(var8);
                            break;
                        }

                    }
                    ((EntitySheep)var4).setSheared(true);
                    int var19 = 3 + var2.random.nextInt(2) + chargeLevel(var1) / 2;
                    EntityItem var21 = null;
                    for(int var10 = 0; var10 < var19; var10++)
                        var21 = new EntityItem(var2, var3.locX, var3.locY, var3.locZ, new ItemStack(Block.WOOL.id, var19, ((EntitySheep)var4).getColor()));

                    var2.addEntity(var21);
                } else
                if(var4 instanceof EntityMushroomCow)
                {
                    if(var2.random.nextInt(100) < var7)
                    {
                        EntityMushroomCow var18 = new EntityMushroomCow(var2);
                        double var9 = var4.locX - var3.locX;
                        double var11 = var4.locZ - var3.locZ;
                        if(var9 < 0.0D)
                            var9 *= -1D;
                        if(var11 < 0.0D)
                            var11 *= -1D;
                        var9 += var4.locX;
                        var11 += var4.locZ;
                        double var13 = var4.locY;
                        for(int var15 = -5; var15 <= 5; var15++)
                        {
                            if(var2.getTypeId((int)var9, (int)var13 + var15, (int)var11) == 0 || var2.getTypeId((int)var9, (int)var13 + var15 + 1, (int)var11) != 0)
                                continue;
                            var18.setPosition(var9, var13 + (double)var15 + 1.0D, var11);
                            var2.addEntity(var18);
                            break;
                        }

                    }
                    ((EntityMushroomCow)var4).die();
                    EntityCow var20 = new EntityCow(var2);
                    var20.setPositionRotation(var4.locX, var4.locY, var4.locZ, var4.yaw, var4.pitch);
                    var20.setHealth(((EntityMushroomCow)var4).getHealth());
                    var20.V = ((EntityMushroomCow)var4).V;
                    var2.addEntity(var20);
                    var2.a("largeexplode", var4.locX, var4.locY + (double)(var4.length / 2.0F), var4.locZ, 0.0D, 0.0D, 0.0D);
                    int var23 = 5 + var2.random.nextInt(2) + chargeLevel(var1) / 2;
                    Object var22 = null;
                    for(int var24 = 0; var24 < var23; var24++)
                        new EntityItem(var2, var3.locX, var3.locY, var3.locZ, new ItemStack(Block.RED_MUSHROOM, var23));

                }
            }
        } else
        if(var4 instanceof EntitySheep)
        {
            new EntitySheep(var2);
            ((EntitySheep)var4).setSheared(true);
            int var6 = 3 + var2.random.nextInt(2);
            EntityItem var17 = null;
            for(int var19 = 0; var19 < var6; var19++)
                var17 = new EntityItem(var2, var3.locX, var3.locY, var3.locZ, new ItemStack(Block.WOOL.id, var6, ((EntitySheep)var4).getColor()));

            var2.addEntity(var17);
        } else
        if(var4 instanceof EntityMushroomCow)
        {
            ((EntityMushroomCow)var4).die();
            EntityCow var16 = new EntityCow(((EntityMushroomCow)var4).world);
            var16.setPositionRotation(((EntityMushroomCow)var4).locX, ((EntityMushroomCow)var4).locY, ((EntityMushroomCow)var4).locZ, ((EntityMushroomCow)var4).yaw, ((EntityMushroomCow)var4).pitch);
            var16.setHealth(((EntityMushroomCow)var4).getHealth());
            var16.V = ((EntityMushroomCow)var4).V;
            ((EntityMushroomCow)var4).world.addEntity(var16);
            ((EntityMushroomCow)var4).world.a("largeexplode", ((EntityMushroomCow)var4).locX, ((EntityMushroomCow)var4).locY + (double)(((EntityMushroomCow)var4).length / 2.0F), ((EntityMushroomCow)var4).locZ, 0.0D, 0.0D, 0.0D);
            for(int var6 = 0; var6 < 5; var6++)
                ((EntityMushroomCow)var4).world.addEntity(new EntityItem(((EntityMushroomCow)var4).world, ((EntityMushroomCow)var4).locX, ((EntityMushroomCow)var4).locY + (double)((EntityMushroomCow)var4).length, ((EntityMushroomCow)var4).locZ, new ItemStack(Block.RED_MUSHROOM)));

        }
    }

    public int a(Entity var1)
    {
        return (var1 instanceof EntitySheep) || (var1 instanceof EntityMushroomCow) ? 1 : weaponDamage;
    }

    public boolean a(ItemStack var1, EntityLiving var2, EntityLiving var3)
    {
        if(var3 instanceof EntityHuman)
        {
            EntityHuman var4 = (EntityHuman)var3;
            if(var2 instanceof EntitySheep)
            {
                if(!((EntitySheep)var2).isSheared())
                    doShear(var1, var4.world, var4, var2);
                var2.heal(1);
            } else
            if(var2 instanceof EntityMushroomCow)
            {
                doShear(var1, var4.world, var4, var2);
                var2.heal(1);
            }
        }
        return true;
    }

    public void doAlternate(ItemStack var1, World var2, EntityHuman var3)
    {
        EEBase.updateSwordMode(var3);
    }

    public void doRelease(ItemStack var1, World var2, EntityHuman var3)
    {
        doSwordBreak(var1, var2, var3);
    }

    public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman)
    {
    }

    public boolean itemCharging;
    private static Block blocksEffectiveAgainst[];

    static 
    {
        blocksEffectiveAgainst = (new Block[] {
            Block.WOOD, Block.BOOKSHELF, Block.LOG, Block.CHEST, Block.DIRT, Block.GRASS, Block.LEAVES, Block.WEB, Block.WOOL
        });
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: /home/dread/Downloads/EE2ServerV1.4.6.5-bukkit-mcpc-1.2.5-r5.zip
	Total time: 34 ms
	Jad reported messages/errors:
The class file version is 51.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/