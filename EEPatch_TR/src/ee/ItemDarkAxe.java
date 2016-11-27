package ee;

import java.util.*;

import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.dm.EEDMAxeEvent;

import net.minecraft.server.*;

public class ItemDarkAxe extends ItemDarkTool
{
	public boolean itemCharging;
	private static Block blocksEffectiveAgainst[];

	static 
	{
		blocksEffectiveAgainst = (new Block[] {Block.WOOD, Block.BOOKSHELF, Block.LOG, Block.CHEST});
	}
	
	protected ItemDarkAxe(int var1)
	{
		super(var1, 2, 8, blocksEffectiveAgainst);
	}

	public float getDestroySpeed(ItemStack var1, Block var2)
	{
		return var2.material != Material.WOOD ? super.getDestroySpeed(var1, var2) : 14F + chargeLevel(var1) * 2;
	}

	public void doBreak(ItemStack axe, World world, EntityHuman human) {
		int charge = chargeLevel(axe);
		if (charge < 1) return;
		
		double playerX = EEBase.playerX(human);
		double playerY = EEBase.playerY(human);
		double playerZ = EEBase.playerZ(human);
		cleanDroplist(axe);
		if(chargeLevel(axe) < 1) return;
		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);
		
		for(int var11 = -(charge * 2) + 1; var11 <= charge * 2 - 1; var11++) {
			for(int var12 = charge * 2 + 1; var12 >= -2; var12--) {
				for(int var13 = -(charge * 2) + 1; var13 <= charge * 2 - 1; var13++) {
					int nx = (int)(playerX + var11);
					int ny = (int)(playerY + var12);
					int nz = (int)(playerZ + var13);
					int id = world.getTypeId(nx, ny, nz);
					if((EEMaps.isWood(id) || EEMaps.isLeaf(id)) && attemptBreak(human, nx, ny, nz))
					{
						if(getFuelRemaining(axe) < 1){
							//if(var11 == chargeLevel(axe) && var13 == chargeLevel(axe))
							//{
								ConsumeReagent(axe, human, false);
							//} else
							//{
							//	ConsumeReagent(axe, human, false);
							//}
						}
						
						if(getFuelRemaining(axe) > 0)
						{
							int var18 = world.getData(nx, ny, nz);
							ArrayList<ItemStack> var19 = Block.byId[id].getBlockDropped(world, nx, ny, nz, var18, 0);
							ItemStack var21;
							for(Iterator<ItemStack> var20 = var19.iterator(); var20.hasNext(); addToDroplist(axe, var21))
								var21 = var20.next();

							world.setTypeId(nx, ny, nz, 0);
							if(!EEMaps.isLeaf(id))
								setShort(axe, "fuelRemaining", getFuelRemaining(axe) - 1);
							if(world.random.nextInt(8) == 0)
								world.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if(world.random.nextInt(8) == 0)
								world.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
						}
					}
				}

			}

		}

		ejectDropList(world, axe, playerX, playerY, playerZ);
		
	}

	public ItemStack a(ItemStack dmaxe, World world, EntityHuman human)
	{
		if(EEProxy.isClient(world)) return dmaxe;
		
		if (EEEventManager.callEvent(new EEDMAxeEvent(dmaxe, EEAction.RIGHTCLICK, human, EEAction2.BreakRadius))) return dmaxe;//IMPORTANT Is this correct?
		
		doBreak(dmaxe, world, human);
		return dmaxe;
	}

	public boolean interactWith(ItemStack dmaxe, EntityHuman human, World world, int x, int y, int z, int var7) {
		if(EEProxy.isClient(world)) return false;
		
		if (EEEventManager.callEvent(new EEDMAxeEvent(dmaxe, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;
		
		int charge = chargeLevel(dmaxe);
		if(charge <= 0) return false;
		
		//boolean var14 = false;
		cleanDroplist(dmaxe);
		if(chargeLevel(dmaxe) < 1) return false;
		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);
		for(int var15 = -(charge * 2) + 1; var15 <= charge * 2 - 1; var15++) {
			for(int var16 = charge * 2 + 1; var16 >= -2; var16--) {
				for(int var17 = -(charge * 2) + 1; var17 <= charge * 2 - 1; var17++) {
					int nx = x + var15;
					int ny = y + var16;
					int nz = z + var17;
					int id = world.getTypeId(nx, ny, nz);
					if((EEMaps.isWood(id) || EEMaps.isLeaf(id)) && attemptBreak(human, nx, ny, nz)) {
						if(getFuelRemaining(dmaxe) < 1) {
							//if(var15 == chargeLevel(var1) && var17 == chargeLevel(var1))
							//{
							//	ConsumeReagent(var1, human, var14);
							//	var14 = false;
							//} else
							//{
								ConsumeReagent(dmaxe, human, false);
							//}
						}
						
						if(getFuelRemaining(dmaxe) > 0) {
							int var22 = world.getData(nx, ny, nz);
							ArrayList<ItemStack> var23 = Block.byId[id].getBlockDropped(world, nx, ny, nz, var22, 0);
							ItemStack var25;
							for(Iterator<ItemStack> var24 = var23.iterator(); var24.hasNext(); addToDroplist(dmaxe, var25))
								var25 = var24.next();

							world.setTypeId(nx, ny, nz, 0);
							
							if(!EEMaps.isLeaf(id))
								setShort(dmaxe, "fuelRemaining", getShort(dmaxe, "fuelRemaining") - 1);
							if(world.random.nextInt(8) == 0)
								world.a("largesmoke", nx, ny, nz, 0.0D, 0.0D, 0.0D);
							if(world.random.nextInt(8) == 0)
								world.a("explode", nx, ny, nz, 0.0D, 0.0D, 0.0D);
						}
					}
				}

			}

		}

		ejectDropList(world, dmaxe, x, y, z);
		return false;
	}

	public void doPassive(ItemStack itemstack, World world, EntityHuman human){}
	public void doActive(ItemStack itemstack, World world, EntityHuman human){}
	public void doHeld(ItemStack itemstack, World world, EntityHuman human){}

	/** When you press R: {@link #doBreak(ItemStack, World, EntityHuman)} */
	public void doRelease(ItemStack dmaxe, World world, EntityHuman human) {
		if (EEEventManager.callEvent(new EEDMAxeEvent(dmaxe, EEAction.RELEASE, human, EEAction2.BreakRadius))) return;
		doBreak(dmaxe, world, human);
	}

	public void doAlternate(ItemStack itemstack, World world, EntityHuman human){}
	public void doLeftClick(ItemStack itemstack, World world, EntityHuman human){}

	public boolean canActivate() {
		return false;
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman human){}

}