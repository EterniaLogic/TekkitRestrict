package nl.taico.tekkitrestrict.objects;

import java.util.LinkedList;

public class TRLimit {
	public int id = 0;
	public int data = -1;
	/** A list of locations where this limitblock is placed. */
	public LinkedList<TRLocation> placedBlock = new LinkedList<TRLocation>();
	
	@Override
	public int hashCode(){
		return id << 16 | data;
		//15  = 2^4
		//512 = 2^9
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == this) return true;
		if (!(obj instanceof TRLimit)) return false;
		
		final TRLimit other = (TRLimit) obj;
		return other.id == this.id && other.data == this.data;
	}
}