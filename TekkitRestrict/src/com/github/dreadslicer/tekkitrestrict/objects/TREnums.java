package com.github.dreadslicer.tekkitrestrict.objects;

public class TREnums {
	public static enum TRClickType{
		Left, Right, Both, Trample, All;
		public boolean right(){return (this == TRClickType.Right);}
		public boolean left(){return (this == TRClickType.Left);}
		public boolean both(){return (this == TRClickType.Both);}
		public boolean trample(){return (this == TRClickType.Trample);}
		public boolean all(){return (this == TRClickType.All);}
	}
}
