package com.github.dreadslicer.tekkitrestrict;

import java.util.List;

public class TREMCSet {
	private static List<String> ak;

	public static void reload() {
		ak = tekkitrestrict.config.getStringList("SetEMC");
		setEMCs();
	}

	private static void setEMCs() {
		if (tekkitrestrict.EEEnabled) {
			for (int i = 0; i < ak.size(); i++) {
				String ic = ak.get(i);
				if (ic.contains(" ")) {
					String[] pc = ic.split(" ");
					String emc = pc[1];
					int EMC = Integer.valueOf(emc);
					List<TRCacheItem> iss = TRCacheItem.processItemString("",
							pc[0], -1);
					for (TRCacheItem cr : iss) {
						int id = cr.id;
						int data = cr.getData() == -10 ? 0 : cr.getData();
						ee.EEMaps.addEMC(id, data, EMC);
					}
				}
			}
		}
	}
}
