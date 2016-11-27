package buildcraft.api;

import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

public abstract interface ISpecialInventory extends IInventory
{
	/**
	 * @param item The item to add.
	 * @param add If add is false, it will check if the item can be added, but it will not add it.
	 * @param side The side that the pipe is connected to.
	 * @return If the item can be added (or if the item was added if add is true).
	 */
	public abstract boolean addItem(ItemStack item, boolean add, Orientations side);
	
	/**
	 * @param extract If extract is false, it will return what item can be extracted, but it will not extract it.
	 * @param side The side that the pipe is connected to.
	 * @return The item that was extracted (or can be extracted if extract is false).
	 */
	public abstract ItemStack extractItem(boolean extract, Orientations side);
}