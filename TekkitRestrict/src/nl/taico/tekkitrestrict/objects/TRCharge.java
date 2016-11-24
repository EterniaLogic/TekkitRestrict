package nl.taico.tekkitrestrict.objects;

public class TRCharge extends TRItem {
	public int maxcharge, chargerate;
	public Object itemstack;

	@Override
	@lombok.NonNull public TRCharge clone(){
		final TRCharge tc = new TRCharge();
		tc.id = this.id;
		tc.data = this.data;
		tc.maxcharge = this.maxcharge;
		tc.chargerate = this.chargerate;
		tc.itemstack = this.itemstack;
		return tc;
	}

	/**
	 * An object equals this object if id, data, maxcharge and chargerate are the same.<br>
	 * This does not check if the itemstack is the same.
	 * @see nl.taico.tekkitrestrict.objects.TRItem#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
		if (obj == this) return true;

		if (!(obj instanceof TRCharge)) return false;

		final TRCharge tri = (TRCharge) obj;
		return (tri.id == id) && (tri.data == data) && (tri.maxcharge == maxcharge) && (tri.chargerate == chargerate);
	}

	@Override
	public int hashCode(){
		return (17 * super.hashCode()) + maxcharge;
	}

	@Override
	public String toString() {
		return "ID="+id+";Data="+data+";MaxCharge="+maxcharge+";ChargeRate="+chargerate;
	}
}
