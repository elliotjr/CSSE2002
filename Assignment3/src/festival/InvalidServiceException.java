package festival;

/**
 * An exception indicating an invalid shuttle service.
 */
@SuppressWarnings("serial")
public class InvalidServiceException extends RuntimeException {

	public InvalidServiceException() {
		super();
	}

	public InvalidServiceException(String s) {
		super(s);
	}

}
