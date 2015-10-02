package festival;

/**
 * <p>
 * An immutable class representing a shuttle service for a festival.
 * </p>
 * 
 * <p>
 * A shuttle service departs a venue at the end of a session, and arrives at a
 * destination venue before the start of the next session. (The source and
 * destination venues in a service are distinct.)
 * </p>
 */
public class Service {

	// the venue that the service departs from
	private Venue source;
	// the venue that the service arrives at
	private Venue destination;
	// the service departs for its destination at the end of this session
	private int session;

	/*
	 * Invariant: source != null && destination != null &&
	 * !source.equals(destination) && session > 0
	 */

	/**
	 * Creates a new service that departs the source venue at the end of the
	 * given session, and arrives at the destination venue before the start of
	 * the next session.
	 * 
	 * @param source
	 *            the venue that the service departs from
	 * @param destination
	 *            the venue that the service arrives at
	 * @param session
	 *            the session when the service departs
	 * @throws NullPointerException
	 *             if either the source or destination is null
	 * @throws InvalidServiceException
	 *             if the source venue equals the destination venue
	 * @throws InvalidSessionException
	 *             if session <= 0
	 */
	public Service(Venue source, Venue destination, int session) {
		if (source == null || destination == null) {
			throw new NullPointerException("Input parameters cannot be null");
		}
		if (source.equals(destination)) {
			throw new InvalidServiceException(
					"The source and destination venue cannot be the same");
		}
		if (session <= 0) {
			throw new InvalidSessionException("Session number " + session
					+ " must be positive");
		}
		this.source = source;
		this.destination = destination;
		this.session = session;
	}

	/**
	 * Returns the venue that the service departs from.
	 * 
	 * @return the source venue of this service.
	 */
	public Venue getSource() {
		return source;
	}

	/**
	 * Returns the venue that the service arrives at.
	 * 
	 * @return the destination venue of this service.
	 */
	public Venue getDestination() {
		return destination;
	}

	/**
	 * Returns the number of the session when the service departs.
	 * 
	 * (To be precise, the service departs at the end of this session, arriving
	 * before the start of the next session.)
	 * 
	 * @return the session when this service departs
	 */
	public int getSession() {
		return session;
	}

	/**
	 * Two services are considered to be equal if they have the same source,
	 * destination and session.
	 * 
	 * (Two venues are considered to be the same if they are equal according to
	 * their equals method.)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Service)) {
			return false;
		}
		Service service = (Service) object; // service to compare
		return this.source.equals(service.source)
				&& this.destination.equals(service.destination)
				&& this.session == service.session;
	}

	@Override
	public int hashCode() {
		// calculates polynomial hashcode
		final int prime = 31; // a prime
		int result = 1; // hash code under construction
		result = prime * result + source.hashCode();
		result = prime * result + destination.hashCode();
		result = prime * result + session;
		return result;
	}

	/**
	 * Returns a string of the form:
	 * 
	 * "Departs SOURCE after session SESSION for DESTINATION"
	 * 
	 * where SOURCE is the name of the source venue, SESSION is the session
	 * number when this service departs and and DESTINATION is the name of the
	 * destination venue.
	 */
	@Override
	public String toString() {
		return "Departs " + source + " after session " + session + " for "
				+ destination;
	}

	/**
	 * Determines whether this Service is internally consistent (i.e. it
	 * satisfies its class invariant).
	 * 
	 * @return true if this Service is internally consistent, and false
	 *         otherwise.
	 */
	public boolean checkInvariant() {
		return (source != null && destination != null
				&& !source.equals(destination) && session > 0);
	}
}
