package nl.taico.tekkitrestrict.objects;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.Item;
import net.minecraft.server.NBTTagCompound;

public class TRItemStack{
	private ItemStack bukkitStack;
	/**
	 * An itemStack of id:0 with an amount of 1.
	 */
	public TRItemStack(int id) {
		this(id, 1, (short) 0, null);
	}

	/**
	 * An ItemStack of id:0 with an amount of 1.
	 */
	public TRItemStack(int id, int amount) {
		this(id, amount, (short) 0, null);
	}
	
	/**
	 * An ItemStack of id:damage with an amount of amount.
	 */
	public TRItemStack(int id, int amount, short damage) {
		this(id, amount, damage, null);
	}
	
	public TRItemStack(int type, int amount, short damage, Byte data) {
		bukkitStack = new ItemStack(type, amount, damage, data);
	}

	public TRItemStack(ItemStack stack) {
		this.bukkitStack = stack;
	}
	
	public int getId(){
		return bukkitStack.getTypeId();
	}
	public void setId(int id){
		bukkitStack.setTypeId(id);
	}
	
	public byte getByteData(){
		return bukkitStack.getData().getData();
	}
	public void setByteData(byte data){
		bukkitStack.getData().setData(data);
	}
	
	public short getShortData(){
		return bukkitStack.getDurability();
	}
	public void setShortData(short data){
		bukkitStack.setDurability(data);
	}
	
	public int getAmount(){
		return bukkitStack.getAmount();
	}
	public void setAmount(int amount){
		bukkitStack.setAmount(amount);
	}
	
	public ItemStack getBukkitStack(){
		return bukkitStack;
	}
	
	public net.minecraft.server.ItemStack getMCStack(){
		return ((CraftItemStack) bukkitStack).getHandle();
	}
	
	public CraftItemStack getCraftStack(){
		return (CraftItemStack) bukkitStack;
	}
	
	public NBTTagCompound getTag(){
		return getMCStack().tag;
	}
	
	public Item getMCItem(){
		return getMCStack().getItem();
	}

	public static int getId(ItemStack itemStack){
		return itemStack.getTypeId();
	}
	public static void setId(ItemStack itemStack, int id){
		itemStack.setTypeId(id);
	}
	
	public static byte getByteData(ItemStack itemStack){
		return itemStack.getData().getData();
	}
	public static void setByteData(ItemStack itemStack, byte data){
		itemStack.getData().setData(data);
	}
	
	public static short getShortData(ItemStack itemStack){
		return itemStack.getDurability();
	}
	public static void setShortData(ItemStack itemStack, short data){
		itemStack.setDurability(data);
	}
	
	public static int getAmount(ItemStack itemStack){
		return itemStack.getAmount();
	}
	public static void setAmount(ItemStack itemStack, int amount){
		itemStack.setAmount(amount);
	}
	
	public static net.minecraft.server.ItemStack getMCStack(ItemStack itemStack){
		return ((CraftItemStack) itemStack).getHandle();
	}
	public static CraftItemStack getCraftStack(ItemStack itemStack){
		return (CraftItemStack) itemStack;
	}
	public static Item getMCItem(ItemStack itemStack){
		return ((CraftItemStack) itemStack).getHandle().getItem();
	}
	
	public static NBTTagCompound getTag(ItemStack itemStack){
		return ((CraftItemStack) itemStack).getHandle().getTag();
	}
	public static NBTTagCompound getTagOrCreate(ItemStack itemStack){
		NBTTagCompound tag = ((CraftItemStack) itemStack).getHandle().getTag();
		if (tag == null){
			tag = new NBTTagCompound();
			((CraftItemStack) itemStack).getHandle().setTag(tag);
		}
		return tag;
	}
	public static void setTag(ItemStack itemStack, NBTTagCompound tag){
		((CraftItemStack) itemStack).getHandle().setTag(tag);
	}
}
