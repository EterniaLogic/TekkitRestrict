package com.github.dreadslicer.tekkitrestrict.objects;

public class TRItemStack {
	public int id, amount, data;

	public TRItemStack(int id, int amount, int data) {
		this.id = id;
		this.amount = amount;
		this.data = data;
	}
	
	public net.minecraft.server.ItemStack getHandle() {
		return new net.minecraft.server.ItemStack(id, 0, data);
	}
}