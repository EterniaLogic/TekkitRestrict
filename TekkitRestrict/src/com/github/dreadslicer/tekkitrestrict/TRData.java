package com.github.dreadslicer.tekkitrestrict;

import java.util.ArrayList;

public class TRData {
	public boolean all;
	public ArrayList<Integer> data;
	public TRData(boolean all, ArrayList<Integer> data){
		this.all = all;
		if (!all) this.data = data;
		else data = null;
	}
	
	public TRData(int dataValue){
		this.all = dataValue == -1;
		if (!all){
			this.data = new ArrayList<Integer>();
			this.data.add(dataValue);
		} else data = null;
	}
	
	public boolean contains(int dataValue){
		if (all) return true;
		return data.contains(dataValue);
	}
	
	public void add(int dataValue){
		if (all) return;
		if (data == null) data = new ArrayList<Integer>();
		data.add(dataValue);
	}
}
