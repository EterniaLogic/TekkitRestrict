package com.github.dreadslicer.tekkitrestrict.objects;

public class TRRange {
	private boolean same;
	public int from, to;
	public TRRange(int from, int to){
		if (from < to){
			this.from = from;
			this.to = to;
		} else if (from > to){
			this.from = to;
			this.to = from;
		} else {
			this.same = true;
			this.from = from;
		}
	}
	
	public boolean inRange(int id){
		if (same) return id == from;
		return id >= from && id <= to;
	}
}
