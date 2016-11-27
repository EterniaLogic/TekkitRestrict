package ee;

import ee.core.GuiIds;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class EEGuiHandler implements forge.IGuiHandler {
	public EEGuiHandler() {}

	public Object getGuiElement(int var1, EntityHuman var2, World var3, int var4, int var5, int var6) {
		TileEntity var7 = null;

		if (!isItemGui(var1)) {
			if (!var3.isLoaded(var4, var5, var6)) {
				return null;
			}

			var7 = var3.getTileEntity(var4, var5, var6);
		}

		if (var1 == GuiIds.COLLECTOR_1) return new ContainerCollector(var2.inventory, (TileCollector) var7);
		if (var1 == GuiIds.COLLECTOR_2) return new ContainerCollector2(var2.inventory, (TileCollector2) var7);
		if (var1 == GuiIds.COLLECTOR_3) return new ContainerCollector3(var2.inventory, (TileCollector3) var7);
		if (var1 == GuiIds.RELAY_1) return new ContainerRelay(var2.inventory, (TileRelay) var7);
		if (var1 == GuiIds.RELAY_2) return new ContainerRelay2(var2.inventory, (TileRelay2) var7);
		if (var1 == GuiIds.RELAY_3) return new ContainerRelay3(var2.inventory, (TileRelay3) var7);
		if (var1 == GuiIds.DM_FURNACE) return new ContainerDMFurnace(var2.inventory, (TileDMFurnace) var7);
		if (var1 == GuiIds.RM_FURNACE) return new ContainerRMFurnace(var2.inventory, (TileRMFurnace) var7);
		if (var1 == GuiIds.CONDENSER) return new ContainerCondenser(var2.inventory, (TileCondenser) var7);
		if (var1 == GuiIds.PEDESTAL) return new ContainerPedestal(var2.inventory, (TilePedestal) var7);
		if (var1 == GuiIds.TRANS_TABLE) return new ContainerTransmutation(var2.inventory, var2, EEPatch.getTransData(var2));
		if (var1 == GuiIds.PORT_TRANS_TABLE) return new ContainerTransmutation(var2.inventory, var2, EEPatch.getTransData(var2));
		if (var1 == GuiIds.ALCH_CHEST) return new ContainerAlchChest((TileAlchChest) var7, var2.inventory, false);
		if (var1 == GuiIds.ALCH_BAG) {
			AlchemyBagData var9 = ItemAlchemyBag.getBagData(var4, var2, var3);
			return new ContainerAlchChest(var9, var2.inventory, true);
		}
		if (var1 == GuiIds.MERCURIAL_EYE) {
			MercurialEyeData var8 = ItemMercurialEye.getEyeData(var2, var3);
			return new ContainerMercurial(var2.inventory, var2, var8);
		}

		return var1 == GuiIds.PORT_CRAFTING ? new ContainerPortableCrafting(var2.inventory, var2) : null;
	}

	private static boolean isItemGui(int var0) {
		return var0 == GuiIds.PORT_CRAFTING || var0 == GuiIds.MERCURIAL_EYE || var0 == GuiIds.PORT_TRANS_TABLE || var0 == GuiIds.ALCH_BAG;
	}
}