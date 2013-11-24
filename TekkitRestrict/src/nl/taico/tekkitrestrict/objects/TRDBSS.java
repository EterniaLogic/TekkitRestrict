package nl.taico.tekkitrestrict.objects;

public class TRDBSS {
	public String name, world, data;
	public int mode;
	public TRDBSS(String name, int mode, String data, String world){
		this.name = name;
		this.world = world;
		this.data = data;
		this.mode = mode;
	}
}
