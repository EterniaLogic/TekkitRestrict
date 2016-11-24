package nl.taico.tekkitrestrict.objects;

public class TRDBSS {
	public String name, world, data;
	public int mode;
	public TRDBSS(final String name, final int mode, final String data, final String world){
		this.name = name;
		this.world = world;
		this.data = data;
		this.mode = mode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TRDBSS)) return false;
		
		final TRDBSS other = (TRDBSS) obj;
		
		if (mode != other.mode) return false;
		
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		
		if (world == null) {
			if (other.world != null) return false;
		} else if (!world.equals(other.world)) return false;
		
		if (data == null) {
			if (other.data != null) return false;
		} else if (!data.equals(other.data)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 + ((name == null) ? 0 : name.hashCode());
		result = 31 * result + ((world == null) ? 0 : world.hashCode());
		result = 31 * result + ((data == null) ? 0 : data.hashCode());
		return 31 * result + mode;
	}
}
