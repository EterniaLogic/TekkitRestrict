package com.github.dreadslicer.tekkitrestrict.objects;

import com.github.dreadslicer.tekkitrestrict.annotations.Safe;

public class TRItem {
	public int id;
	public int data;
	
	public static TRItem parseItem(int id, int data) {
		TRItem item = new TRItem();
		item.id = id;
		item.data = data;
		return item;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null) return false;
		if (!(obj instanceof TRItem)) return false;
		TRItem tri = (TRItem) obj;
		if (tri.id == id && tri.data == data) return true;
		return false;
	}
	
	@Override
	public String toString(){
		return ""+id+":"+data;
	}
	
	/**
	 * Compare this TRItem with the given id and data
	 * @return True if:<br>
	 * <ul>
	 * <li>this.id == -11</li>
	 * <li>this.id == id AND this.data == data</li>
	 * <li>this.id == id AND this.data == -1</li>
	 * <li>this.id == id AND this.data == -10 AND data == 0</li>
	 * </ul>
	 */
	@Safe
	public boolean compare(int id, int data) {
		return this.id == -11 || this.id == id && (this.data == data || this.data == -1 || this.data == -10 && data == 0);
	}
}
