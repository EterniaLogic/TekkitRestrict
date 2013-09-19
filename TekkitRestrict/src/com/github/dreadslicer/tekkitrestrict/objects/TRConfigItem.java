package com.github.dreadslicer.tekkitrestrict.objects;

public class TRConfigItem {
	public int id = -1;
	public int data = 0;
	
	public static boolean equals(int id, int data, TRConfigItem mainItem){
		return id == mainItem.id && (data == mainItem.data || mainItem.data == -1 || (data == 0 && mainItem.data == -10));
	}
	
	public static boolean equals(int id, int data, int mainId, int mainData){
		return id == mainId && (data == mainData || mainData == -1 || (data == 0 && mainData == -10));
	}
	
	public boolean equals(int id, int data){
		return id == this.id && (data == this.data || this.data == -1 || (data == 0 && this.data == -10));
	}
}
