package nl.taico.tekkitrestrict.api;

import java.util.HashMap;

public class EMC {
	/**
	 * Remove the EMC value of an item.
	 * WARNING: If that item is also used as fuel, unexpected behavior may occur.
	 * */
	public static void removeEMC(int id, int data){
		HashMap<Integer, Integer> old = ee.EEMaps.alchemicalValues.get(id);
		if (old == null) return;
		old.remove(data);
		ee.EEMaps.alchemicalValues.put(id, old);
	}

	/**
	 * Add or set the EMC value of a single item.
	 * If EMC = 0, it will remove the EMC value of that item.
	 * @see #removeEMC(int, int)
	 */
	public static void setEMC(int id, int data, int EMC){
		if (EMC == 0)
			removeEMC(id, data);
		else
			ee.EEMaps.addEMC(id, data, EMC);
	}
}
