package nl.taico.tekkitrestrict.lib;


public class BrokenRandomString {
	public static final char[] symbols = new char[36];
	static {
		for (int idx = 26; idx < 36; ++idx) {
			symbols[idx] = (char) ('a' + idx);
		}
	}

}
