package nl.taico.tekkitrestrict.util;

public class ArrayUtil {
	public static boolean contains(int[] array, int i){
		for (int k : array) if (k == i) return true;
		return false;
	}
}
