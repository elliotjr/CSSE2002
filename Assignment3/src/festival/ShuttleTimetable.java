package festival;

import java.util.*;

/**
 * <p>
 * A mutable representation of the shuttle services between venues at a
 * festival.
 * </p>
 * 
 * <p>
 * A shuttle timetable does not contain duplicate services (no two services run
 * from a source venue to a destination venue at the same time).
 * </p>
 */
public class ShuttleTimetable implements Iterable<Service> {

	// the services in the shuttle timetable
	private Set<Service> services;

	/*
	 * Invariant: services != null
	 */

	/**
	 * Constructs a new shuttle timetable without any services.
	 **/
	public ShuttleTimetable() {
		services = new HashSet<>();
	}

	/**
	 * Unless the shuttle timetable already contains an equivalent service, this
	 * method adds the given service to the shuttle timetable. If the timetable
	 * already contains an equivalent service, then this method should not
	 * change the shuttle timetable.
	 * 
	 * (Equivalence of services is judged using the equals method in the Service
	 * class.)
	 * 
	 * @param service
	 *            the service to be added to the shuttle timetable.
	 * @throws NullPointerException
	 *             if service is null
	 */
	public void addService(Service service) {
		if (service == null) {
			throw new NullPointerException("Service cannot be null");
		}
		if (!services.contains(service)) {
			services.add(service);
		}
	}

	/**
	 * If the shuttle timetable contains a service that is equivalent to this
	 * one, then it is removed from the timetable. If there is no equivalent
	 * service, then the timetable is unchanged by the operation.
	 * 
	 * @param service
	 *            the service to be removed from the timetable.
	 */
	public void removeService(Service service) {
		services.remove(service);
	}

	/**
	 * Returns true if the timetable contains a shuttle service equivalent to
	 * the parameter service, and false otherwise.
	 * 
	 * @param service
	 *            the service to be searched for
	 * @return true iff the timetable contains a shuttle service equivalent to
	 *         the given parameter.
	 */
	public boolean hasService(Service service) {
		return services.contains(service);
	}

	/**
	 * Returns the number of services in the shuttle timetable.
	 */
	public int size() {
		return services.size();
	}

	/**
	 * Returns the set of venues that you can get to by catching an available
	 * shuttle service from the source venue at the end of the given session.
	 * 
	 * @param source
	 *            the source venue
	 * @param session
	 *            the session number
	 * @return A set of venues that can be reached by catching a single shuttle
	 *         service from the source venue at the end of the given session.
	 * 
	 * @throws NullPointerException
	 *             if source is null
	 * @throws InvalidSessionException
	 *             if the session number is not positive
	 */
	public Set<Venue> getDestinations(Venue source, int session) {
		if (source == null) {
			throw new NullPointerException("The source venue cannot be null");
		}
		if (session <= 0) {
			throw new InvalidSessionException("Session number " + session
					+ " must be positive");
		}
		// venues reachable from source at end of the session
		Set<Venue> destinations = new HashSet<>();
		for (Service s : services) {
			if (s.getSource().equals(source) && s.getSession() == session) {
				destinations.add(s.getDestination());
			}
		}
		return destinations;
	}

	/**
	 * Returns an iterator over the services in the shuttle timetable.
	 */
	@Override
	public Iterator<Service> iterator() {
		return services.iterator();
	}

	/**
	 * Returns any meaningful implementation of the toString method for this
	 * class.
	 */
	@Override
	public String toString() {
		return services.toString();
	}

	/**
	 * Determines whether this ShuttleTimetable is internally consistent (i.e.
	 * it satisfies its class invariant).
	 * 
	 * @return true if this ShuttleTimetable is internally consistent, and false
	 *         otherwise.
	 */
	public boolean checkInvariant() {
		return services != null;
	}

}
