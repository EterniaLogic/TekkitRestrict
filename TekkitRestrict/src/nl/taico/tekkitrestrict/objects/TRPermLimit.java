package nl.taico.tekkitrestrict.objects;

public class TRPermLimit extends TRItem {
	public int max = 0;
	
	public boolean compare_Perm(TRPermLimit limit){
		if (this.id == limit.id && (this.data == limit.data || this.data == -1 || limit.data == -1)) return true;
		return false;
	}
}
