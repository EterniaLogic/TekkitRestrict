package nl.taico.tekkitrestrict;

public class TRException extends RuntimeException {
	private static final long serialVersionUID = -3456608735246525626L;
	
	public TRException(final String message) {
		super(message);
	}
}
