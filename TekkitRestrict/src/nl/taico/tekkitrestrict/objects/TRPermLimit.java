package nl.taico.tekkitrestrict.objects;

import lombok.NonNull;

public class TRPermLimit extends TRItem {
	public int max = 0;

	public boolean compare_Perm(@NonNull final TRPermLimit limit){
		return (this.id == limit.id) && ((this.data == limit.data) || (this.data == -1) || (limit.data == -1));
	}
}
