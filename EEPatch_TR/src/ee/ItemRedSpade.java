package ee;

import java.util.*;

import ee.events.EEEnums.EEAction;
import ee.events.EEEnums.EEAction2;
import ee.events.EEEventManager;
import ee.events.rm.EERMSpadeEvent;

import net.minecraft.server.*;

public class ItemRedSpade extends ItemRedTool
{

	public ItemRedSpade(int var1) {
		super(var1, 3, 5, blocksEffectiveAgainst);
	}

	public boolean canDestroySpecialBlock(Block var1) {
		return var1 != Block.SNOW ? var1 == Block.SNOW_BLOCK : true;
	}

	public float getDestroySpeed(ItemStack var1, Block var2) {
		return super.getDestroySpeed(var1, var2) / 1.0F;
	}

	public boolean a(ItemStack item, int var2, int x, int y, int z, EntityLiving var6) {
		if(!(var6 instanceof EntityHuman)) return true;
		
		EntityHuman human = (EntityHuman) var6;
		int mode = EEBase.getToolMode(human);
		
		if(mode == 1){
			if (EEEventManager.callEvent(new EERMSpadeEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.TallBreak))) return true;
			doTallImpact(human, item, x, y, z, EEBase.direction(human));
		} else if(mode == 2){
			if (EEEventManager.callEvent(new EERMSpadeEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.WideBreak))) return true;
			doWideImpact(human, item, x, y, z, EEBase.heading(human));
		} else if(mode == 3){
			if (EEEventManager.callEvent(new EERMSpadeEvent(item, EEAction.BREAKBLOCK, human, x, y, z, EEAction2.LongBreak))) return true;
			doLongImpact(human, item, x, y, z, EEBase.direction(human));
		}
		return true;
	}

	public void scanBlockAndBreak(World world, ItemStack item, int x, int y, int z) {
		int id = world.getTypeId(x, y, z);
		int data = world.getData(x, y, z);
		ArrayList<ItemStack> var8 = Block.byId[id].getBlockDropped(world, x, y, z, data, 0);
		
		ItemStack var10;
		for(Iterator<ItemStack> var9 = var8.iterator(); var9.hasNext(); addToDroplist(item, var10))
			var10 = var9.next();

		world.setTypeId(x, y, z, 0);
		if(world.random.nextInt(8) == 0) world.a("largesmoke", x, y, z, 0.0D, 0.0D, 0.0D);
		if(world.random.nextInt(8) == 0) world.a("explode", x, y, z, 0.0D, 0.0D, 0.0D);
	}

	public boolean canBreak(int id, int var2) {
		if(Block.byId[id] == null) return false;
		if(!Block.byId[id].hasTileEntity(var2) && id != Block.BEDROCK.id) {
			Material mat = Block.byId[id].material;
			if(mat == null) return false;
			for(int var3 = 0; var3 < blocksEffectiveAgainst.length; var3++)
				if(id == blocksEffectiveAgainst[var3].id)
					return true;

			return mat == Material.GRASS || mat == Material.EARTH || mat == Material.SAND || mat == Material.SNOW_LAYER || mat == Material.CLAY;
		} else {
			return false;
		}
	}

	public void doLongImpact(EntityHuman human, ItemStack item, int x, int y, int z, double var6) {
		World world = human.world;
		cleanDroplist(item);
		for(int var8 = 1; var8 <= 2; var8++) {
			int nx = x;
			int ny = y;
			int nz = z;
			
			if(var6 == 0.0D) ny = y - var8;
			else if(var6 == 1.0D) ny = y + var8;
			else if(var6 == 2D) nz = z + var8;
			else if(var6 == 3D) nx = x - var8;
			else if(var6 == 4D) nz = z - var8;
			else if(var6 == 5D) nx = x + var8;
			
			int id = world.getTypeId(nx, ny, nz);
			int data = world.getData(nx, ny, nz);
			if(canBreak(id, data) && attemptBreak(human, nx, ny, nz))
				scanBlockAndBreak(world, item, nx, ny, nz);
		}

		ejectDropList(world, item, x, y + 0.5D, z);
	}
	public void doWideImpact(EntityHuman human, ItemStack item, int x, int y, int z, double var6) {
		World world = human.world;
		cleanDroplist(item);
		for(int var8 = -1; var8 <= 1; var8++) {
			int nx = x;
			int nz = z;
			
			if(var8 != 0) {
				if(var6 != 2D && var6 != 4D) nz = z + var8;
				else nx = x + var8;
				
				int id = world.getTypeId(nx, y, nz);
				int data = world.getData(nx, y, nz);
				if(canBreak(id, data) && attemptBreak(human, nx, y, nz))
					scanBlockAndBreak(world, item, nx, y, nz);
			}
		}

		ejectDropList(world, item, x, y + 0.5D, z);
	}
	public void doTallImpact(EntityHuman human, ItemStack item, int x, int y, int z, double var7) {
		cleanDroplist(item);
		World world = human.world;
		for(int var9 = -1; var9 <= 1; var9++) {
			int nx = x;
			int ny = y;
			int nz = z;
			
			if(var9 != 0) {
				if(var7 != 0.0D && var7 != 1.0D)
					ny = y + var9;
				else if(EEBase.heading(human) != 2D && EEBase.heading(human) != 4D)
					nx = x + var9;
				else
					nz = z + var9;
				
				int id = world.getTypeId(nx, ny, nz);
				int data = world.getData(nx, ny, nz);
				if(canBreak(id, data) && attemptBreak(human, nx, ny, nz))
					scanBlockAndBreak(world, item, nx, ny, nz);
			}
		}

		ejectDropList(world, item, x, y + 0.5D, z);
	}

	public boolean interactWith(ItemStack item, EntityHuman human, World world, int x, int y, int z, int var7) {
		if(EEProxy.isClient(world)) return false;
		
		if (EEEventManager.callEvent(new EERMSpadeEvent(item, EEAction.RIGHTCLICK, human, x, y, z, EEAction2.BreakRadius))) return false;
		
		if(chargeLevel(item) >= 1) {
			cleanDroplist(item);
			int var8 = world.getTypeId(x, y, z);
			if(var8 == Block.GRAVEL.id) {
				startSearch(world, human, item, var8, x, y, z, false);
				return true;
			}
		}
		
		if(chargeLevel(item) <= 0) return false;
		
		boolean var19 = true;
		cleanDroplist(item);
		human.C_();
		world.makeSound(human, "flash", 0.8F, 1.5F);
		
		int charge = chargeLevel(item);
		
		for(int var9 = -charge; var9 <= charge; var9++) {
			for(int var10 = -charge; var10 <= charge; var10++) {
				int nx = x + var9;
				int nz = z + var10;
				
				if(var7 == 2) nz += charge;
				else if(var7 == 3) nz -= charge;
				else if(var7 == 4) nx += charge;
				else if(var7 == 5) nx -= charge;
				
				int id = world.getTypeId(nx, y, nz);
				int data = world.getData(nx, y, nz);
				if(canBreak(id, data) && attemptBreak(human, nx, y, nz)) {
					if(getFuelRemaining(item) < 1)
						if(var9 == chargeLevel(item) && var10 == chargeLevel(item)) {
							ConsumeReagent(item, human, var19);
							var19 = false;
						} else {
							ConsumeReagent(item, human, false);
						}
					if(getFuelRemaining(item) > 0) {
						ArrayList<ItemStack> var16 = Block.byId[id].getBlockDropped(world, nx, y, nz, data, 0);
						ItemStack var18;
						for(Iterator<ItemStack> var17 = var16.iterator(); var17.hasNext(); addToDroplist(item, var18))
							var18 = var17.next();

						world.setTypeId(nx, y, nz, 0);
						if(world.random.nextInt(8) == 0) world.a("largesmoke", nx, y, nz, 0.0D, 0.0D, 0.0D);
						if(world.random.nextInt(8) == 0) world.a("explode", nx, y, nz, 0.0D, 0.0D, 0.0D);
					}
				}
			}

		}

		ejectDropList(world, item, x, y, z);
		return true;
	}

	public void startSearch(World world, EntityHuman human, ItemStack item, int var4, int x, int y, int z, boolean var8) {
		world.makeSound(human, "flash", 0.8F, 1.5F);
		if(var8) human.C_();
		doBreakShovel(world, human, item, var4, x, y, z);
	}

	public void doBreakShovel(World world, EntityHuman human, ItemStack item, int id, int x, int y, int z) {
		if(!attemptBreak(human, x, y, z)) return;
		
		if(getFuelRemaining(item) < 1) ConsumeReagent(item, human, false);
		if(getFuelRemaining(item) > 0) {
			int var8 = world.getData(x, y, z);
			ArrayList<ItemStack> var9 = Block.byId[id].getBlockDropped(world, x, y, z, var8, 0);
			ItemStack var11;
			for(Iterator<ItemStack> var10 = var9.iterator(); var10.hasNext(); addToDroplist(item, var11))
				var11 = var10.next();

			world.setTypeId(x, y, z, 0);
			setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);
			for(int var14 = -1; var14 <= 1; var14++) {
				for(int var13 = -1; var13 <= 1; var13++) {
					for(int var12 = -1; var12 <= 1; var12++) {
						if(world.getTypeId(x + var14, y + var13, z + var12) == id)
							doBreakShovelAdd(world, human, item, id, x + var14, y + var13, z + var12);
					}

				}

			}

			if(world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			if(world.random.nextInt(8) == 0) world.a("explode", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			ejectDropList(world, item, x, y, z);
		}
	}

	public void doBreakShovelAdd(World world, EntityHuman human, ItemStack item, int var4, int x, int y, int z) {
		if(!attemptBreak(human, x, y, z)) return;
		
		if(getFuelRemaining(item) < 1) ConsumeReagent(item, human, false);
		if(getFuelRemaining(item) > 0)
		{
			int data = world.getData(x, y, z);
			ArrayList<ItemStack> var9 = Block.byId[var4].getBlockDropped(world, x, y, z, data, 0);
			ItemStack var11;
			for(Iterator<ItemStack> var10 = var9.iterator(); var10.hasNext(); addToDroplist(item, var11))
				var11 = var10.next();

			world.setTypeId(x, y, z, 0);
			setShort(item, "fuelRemaining", getFuelRemaining(item) - 1);
			for(int var14 = -1; var14 <= 1; var14++)
			{
				for(int var13 = -1; var13 <= 1; var13++)
				{
					for(int var12 = -1; var12 <= 1; var12++)
						if(world.getTypeId(x + var14, y + var13, z + var12) == var4)
							doBreakShovelAdd(world, human, item, var4, x + var14, y + var13, z + var12);

				}

			}

			if(world.random.nextInt(8) == 0) world.a("largesmoke", x, y + 1, z, 0.0D, 0.0D, 0.0D);
			if(world.random.nextInt(8) == 0) world.a("explode", x, y + 1, z, 0.0D, 0.0D, 0.0D);
		}
	}

	public void doAlternate(ItemStack var1, World var2, EntityHuman var3) {
		//if (EEEventManager.callEvent(new EERMSpadeEvent(var1, EEAction.ALTERNATE, var3, EEAction2.UpdateToolMode))) return;
		EEBase.updateToolMode(var3);
	}

	public void doToggle(ItemStack itemstack, World world, EntityHuman entityhuman) {}

	private static Block blocksEffectiveAgainst[] = {
		Block.GRASS, Block.DIRT, Block.SOUL_SAND, Block.SAND, Block.GRAVEL, Block.SNOW, Block.SNOW_BLOCK, Block.CLAY, Block.SOIL
	};
}