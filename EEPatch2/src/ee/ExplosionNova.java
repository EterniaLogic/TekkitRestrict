/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   ExplosionNova.java

package ee;

import forge.ISpecialResistance;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.BlockFire;
import net.minecraft.server.ChunkPosition;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Vec3D;
import net.minecraft.server.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginManager;

// Referenced classes of package ee:
//            EntityLootBall, EEBase

public class ExplosionNova
{

    public ExplosionNova(World var1, EntityHuman var2, double var3, double var5, double var7, float var9)
    {
        isFlaming = false;
        player = var2;
        ExplosionRNG = new Random();
        destroyedBlockPositions = new HashSet();
        dropList = new ItemStack[64];
        worldObj = var1;
        exploder = var2;
        explosionSize = var9;
        explosionX = var3;
        explosionY = var5;
        explosionZ = var7;
    }

    protected boolean attemptBreak(EntityHuman player, int i, int j, int k)
    {
        if(player == null)
            return false;
        CraftWorld craftWorld = player.world.getWorld();
        CraftServer craftServer = player.world.getServer();
        Block block = craftWorld.getBlockAt(i, j, k);
        if(block == null)
            return false;
        Player ply = craftServer.getPlayer((EntityPlayer)player);
        if(ply == null)
        {
            return false;
        } else
        {
            BlockBreakEvent event = new BlockBreakEvent(block, ply);
            craftServer.getPluginManager().callEvent(event);
            return !event.isCancelled();
        }
    }

    public void doExplosionA()
    {
        float var1 = explosionSize;
        byte var2 = 16;
        int var3;
        int var4;
        int var5;
        for(var3 = 0; var3 < var2; var3++)
            for(var4 = 0; var4 < var2; var4++)
                for(var5 = 0; var5 < var2; var5++)
                    if(var3 == 0 || var3 == var2 - 1 || var4 == 0 || var4 == var2 - 1 || var5 == 0 || var5 == var2 - 1)
                    {
                        double var6 = ((float)var3 / ((float)var2 - 1.0F)) * 2.0F - 1.0F;
                        double var8 = ((float)var4 / ((float)var2 - 1.0F)) * 2.0F - 1.0F;
                        double var10 = ((float)var5 / ((float)var2 - 1.0F)) * 2.0F - 1.0F;
                        double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
                        var6 /= var12;
                        var8 /= var12;
                        var10 /= var12;
                        float var14 = explosionSize * (0.7F + worldObj.random.nextFloat() * 0.8F);
                        double var15 = explosionX;
                        double var17 = explosionY;
                        double var19 = explosionZ;
                        float var21 = 0.6F;
                        for(; var14 > 0.0F; var14 -= var21 * 0.75F)
                        {
                            int var22 = MathHelper.floor(var15);
                            int var23 = MathHelper.floor(var17);
                            int var24 = MathHelper.floor(var19);
                            int var25 = worldObj.getTypeId(var22, var23, var24);
                            if(var25 > 0)
                                if(net.minecraft.server.Block.byId[var25] instanceof ISpecialResistance)
                                {
                                    ISpecialResistance var26 = (ISpecialResistance)net.minecraft.server.Block.byId[var25];
                                    var14 -= (var26.getSpecialExplosionResistance(worldObj, var22, var23, var24, explosionX, explosionY, explosionZ, exploder) + 0.3F) * var21;
                                } else
                                {
                                    var14 -= (net.minecraft.server.Block.byId[var25].a(exploder) + 0.3F) * var21;
                                }
                            if(var14 > 0.0F || net.minecraft.server.Block.byId[var25].a(exploder) < 30F)
                                destroyedBlockPositions.add(new ChunkPosition(var22, var23, var24));
                            var15 += var6 * (double)var21;
                            var17 += var8 * (double)var21;
                            var19 += var10 * (double)var21;
                        }

                    }



        explosionSize *= 1.7F;
        var3 = MathHelper.floor(explosionX - (double)explosionSize - 1.0D);
        var4 = MathHelper.floor(explosionX + (double)explosionSize + 1.0D);
        var5 = MathHelper.floor(explosionY - (double)explosionSize - 1.0D);
        int var29 = MathHelper.floor(explosionY + (double)explosionSize + 1.0D);
        int var7 = MathHelper.floor(explosionZ - (double)explosionSize - 1.0D);
        int var30 = MathHelper.floor(explosionZ + (double)explosionSize + 1.0D);
        List var9 = worldObj.getEntities(exploder, AxisAlignedBB.b(var3, var5, var7, var4, var29, var30));
        Vec3D var31 = Vec3D.create(explosionX, explosionY, explosionZ);
        for(int var11 = 0; var11 < var9.size(); var11++)
        {
            Entity var32 = (Entity)var9.get(var11);
            double var13 = var32.f(explosionX, explosionY, explosionZ) / (double)explosionSize;
            if(var13 <= 1.0D)
            {
                double var15 = var32.locX - explosionX;
                double var17 = var32.locY - explosionY;
                double var19 = var32.locZ - explosionZ;
                double var40 = MathHelper.sqrt(var15 * var15 + var17 * var17 + var19 * var19);
                var15 /= var40;
                var17 /= var40;
                var19 /= var40;
                double var39 = worldObj.a(var31, var32.boundingBox);
                double var41 = (1.0D - var13) * var39;
                var32.motX += var15 * var41;
                var32.motY += var17 * var41;
                var32.motZ += var19 * var41;
            }
        }

        explosionSize = var1;
        ArrayList var34 = new ArrayList();
        var34.addAll(destroyedBlockPositions);
        if(isFlaming)
        {
            for(int var33 = var34.size() - 1; var33 >= 0; var33--)
            {
                ChunkPosition var35 = (ChunkPosition)var34.get(var33);
                int var37 = var35.x;
                int var36 = var35.y;
                int var16 = var35.z;
                int var38 = worldObj.getTypeId(var37, var36, var16);
                int var18 = worldObj.getTypeId(var37, var36 - 1, var16);
                if(var38 == 0 && net.minecraft.server.Block.n[var18] && ExplosionRNG.nextInt(3) == 0)
                    worldObj.setTypeId(var37, var36, var16, net.minecraft.server.Block.FIRE.id);
            }

        }
    }

    public void doExplosionB()
    {
        for(int var1 = 0; var1 <= 63; var1++)
            dropList[var1] = null;

        worldObj.makeSound(explosionX, explosionY, explosionZ, "nova", 4F, (1.0F + (worldObj.random.nextFloat() - worldObj.random.nextFloat()) * 0.2F) * 0.7F);
        worldObj.a("hugeexplosion", explosionX, explosionY, explosionZ, 0.0D, 0.0D, 0.0D);
        ArrayList var25 = new ArrayList();
        var25.addAll(destroyedBlockPositions);
        for(int var2 = var25.size() - 1; var2 >= 0; var2--)
        {
            ChunkPosition var3 = (ChunkPosition)var25.get(var2);
            int var4 = var3.x;
            int var5 = var3.y;
            int var6 = var3.z;
            int var7 = worldObj.getTypeId(var4, var5, var6);
            for(int var8 = 0; var8 < 1; var8++)
            {
                double var9 = (float)var4 + worldObj.random.nextFloat();
                double var11 = (float)var5 + worldObj.random.nextFloat();
                double var13 = (float)var6 + worldObj.random.nextFloat();
                double var15 = var9 - explosionX;
                double var17 = var11 - explosionY;
                double var19 = var13 - explosionZ;
                double var21 = MathHelper.sqrt(var15 * var15 + var17 * var17 + var19 * var19);
                var15 /= var21;
                var17 /= var21;
                var19 /= var21;
                double var23 = 0.5D / (var21 / (double)explosionSize + 0.10000000000000001D);
                var23 *= worldObj.random.nextFloat() * worldObj.random.nextFloat() + 0.3F;
                var15 *= var23;
                var17 *= var23;
                var19 *= var23;
                if(worldObj.random.nextInt(8) == 0)
                    worldObj.a("explode", (var9 + explosionX * 1.0D) / 2D, (var11 + explosionY * 1.0D) / 2D, (var13 + explosionZ * 1.0D) / 2D, var15, var17, var19);
                if(worldObj.random.nextInt(8) == 0)
                    worldObj.a("smoke", var9, var11, var13, var15, var17, var19);
            }

            if(var7 > 0 && attemptBreak(player, var4, var5, var6))
            {
                int var8 = worldObj.getData(var4, var5, var6);
                ArrayList var27 = net.minecraft.server.Block.byId[var7].getBlockDropped(worldObj, var4, var5, var6, var8, 0);
                for(Iterator var10 = var27.iterator(); var10.hasNext();)
                {
                    ItemStack var28 = (ItemStack)var10.next();
                    for(int var12 = 0; var12 < dropList.length; var12++)
                    {
                        if(dropList[var12] == null)
                        {
                            dropList[var12] = var28.cloneItemStack();
                            var28 = null;
                        } else
                        if(dropList[var12].doMaterialsMatch(var28) && dropList[var12].count < dropList[var12].getMaxStackSize())
                            while(dropList[var12].count < dropList[var12].getMaxStackSize() && var28 != null) 
                            {
                                dropList[var12].count++;
                                var28.count--;
                                if(var28.count == 0)
                                    var28 = null;
                            }
                        if(var28 == null)
                            break;
                    }

                }

                net.minecraft.server.Block.byId[var7].wasExploded(worldObj, var4, var5, var6);
                worldObj.setTypeId(var4, var5, var6, 0);
            }
        }

        if(exploder != null)
        {
            if(dropList != null)
            {
                EntityLootBall var26 = new EntityLootBall(worldObj, EEBase.playerX(player), EEBase.playerY(player), EEBase.playerZ(player), dropList);
                if(var26 != null)
                    worldObj.addEntity(var26);
            }
        } else
        if(dropList != null)
        {
            EntityLootBall var26 = new EntityLootBall(worldObj, explosionX, explosionY, explosionZ, dropList);
            if(var26 != null)
                worldObj.addEntity(var26);
        }
    }

    public boolean isFlaming;
    private Random ExplosionRNG;
    private World worldObj;
    private ItemStack dropList[];
    public double explosionX;
    public double explosionY;
    public double explosionZ;
    public Entity exploder;
    public float explosionSize;
    public Set destroyedBlockPositions;
    private EntityHuman player;
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