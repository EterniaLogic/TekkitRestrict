package com.github.dreadslicer.tekkitrestrict;

public class ItemStack {
	public int id, amount, data;

	public ItemStack(int id, int amount, int data) {
		this.id = id;
		this.amount = amount;
		this.data = data;
	}

	/*
	 * public ItemStack(int id, int amount) { this.id = id; this.amount =
	 * amount; this.data = 0; }
	 */

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public net.minecraft.server.ItemStack getHandle() {
		return new net.minecraft.server.ItemStack(id, 0, getData());
	}
}