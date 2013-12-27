package nl.taico.tekkitrestrict.objects;

import java.util.LinkedList;

public class TRLimit {
	public int id = 0;
	public int data = -1;
	/** A list of locations where this limitblock is placed. */
	public LinkedList<TRLocation> placedBlock = new LinkedList<TRLocation>();
}