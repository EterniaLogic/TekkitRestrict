package nl.taico.tekkitrestrict.objects;

import org.eclipse.jdt.annotation.NonNull;

public class TRCharge extends TRItem {
	public int maxcharge, chargerate;
	public Object itemstack;
	
	@Override
	public String toString() {
		return "ID="+id+";Data="+data+";MaxCharge="+maxcharge+";ChargeRate="+chargerate;
	}
	
	/**
	 * An object equals this object if id, data, maxcharge and chargerate are the same.<br>
	 * This does not check if the itemstack is the same.
	 * @see nl.taico.tekkitrestrict.objects.TRItem#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		if (obj == null) return false;
		if (!(obj instanceof TRCharge)) return false;
		final TRCharge tri = (TRCharge) obj;
		if (tri.id == id && tri.data == data && tri.maxcharge == maxcharge && tri.chargerate == chargerate) return true;
		return false;
	}
	
	@Override
	@NonNull public TRCharge clone(){
		final TRCharge tc = new TRCharge();
		tc.id = this.id;
		tc.data = this.data;
		tc.maxcharge = this.maxcharge;
		tc.chargerate = this.chargerate;
		tc.itemstack = this.itemstack;
		return tc;
	}
}
