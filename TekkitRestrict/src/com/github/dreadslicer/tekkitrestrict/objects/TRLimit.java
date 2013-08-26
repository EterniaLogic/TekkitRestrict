package com.github.dreadslicer.tekkitrestrict.objects;

import java.util.LinkedList;

import org.bukkit.Location;

public class TRLimit {
	public int blockID = -1;
	public int blockData = 0;
	/** A list of locations where this limitblock is placed. */
	public LinkedList<Location> placedBlock = new LinkedList<Location>();
}