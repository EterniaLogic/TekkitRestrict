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
}
