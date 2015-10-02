package festival;

import java.util.*;

/**
 * <p>
 * A mutable class representing the line-up of a festival.
 * </p>
 * 
 * <p>
 * The line-up of a festival keeps track of the events that are scheduled to
 * take place. Time during the festival is broken up into a number of
 * consecutive sessions, and each event is scheduled to take place at a given
 * venue in a particular session. (Sessions are denoted simply by positive
 * integers.)
 * 
 * The session times are the same for all venues, so that events taking place in
 * the same session at different venues, are actually taking place at the same
 * time.
 * </p>
 * 
 * <p>
 * At most one event can be scheduled for a venue in a particular session,
 * although there is no requirement that there is an event scheduled at a venue
 * for every session.
 * </p>
 */
public class LineUp implements Iterable<Event> {

	// Correct line separator for executing machine (used in toString method)
	private static String LINE_SEPARATOR = System.getProperty("line.separator");

	// the events in the line-up
	private List<Event> events;

	/*
	 * Invariant: events!=null && !events.contains(null) && there are no two
	 * events scheduled in the same venue for the same session
	 */

	/**
	 * Creates a new line-up with no events scheduled.
	 */
	public LineUp() {
		events = new ArrayList<>();
	}

	/**
	 * Adds a new event to the line-up.
	 * 
	 * @param event
	 *            the event to be added to the line-up
	 * @throws NullPointerException
	 *             if event is null
	 * @throws InvalidLineUpException
	 *             if there is already an event scheduled for the same venue and
	 *             session as the given event
	 */
	public void addEvent(Event event) {
		if (event == null) {
			throw new NullPointerException(
					"Cannot add a null event to a line-up.");
		}
		if (sessionTaken(event.getVenue(), event.getSession())) {
			throw new InvalidLineUpException(
					"Line up already includes an event at venue "
							+ event.getVenue() + " at time "
							+ event.getSession());
		}
		events.add(event);
	}

	/**
	 * Returns true if the given venue is already occupied for the given
	 * session.
	 * 
	 * @param venue
	 *            the venue to check
	 * @param session
	 *            the session to check
	 * @return true if there is already an event scheduled for the given venue
	 *         and session.
	 */
	private boolean sessionTaken(Venue venue, int session) {
		for (Event e : events) {
			if (e.getVenue().equals(venue) && e.getSession() == session) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If the line-up contains an event that is equivalent to this one, then it
	 * is removed from the line-up. If there is no equivalent event, then the
	 * line-up is unchanged by the operation.
	 *
	 * @param event
	 *            the event to be removed from the line-up.
	 */
	public void removeEvent(Event event) {
		events.remove(event);
	}

	/**
	 * Returns a list of the events scheduled for the given venue. The list of
	 * events should be ordered by session number (in ascending order).
	 * 
	 * @param venue
	 *            the venue for which the events will be retrieved
	 * @return a list of the events scheduled for the given venue, ordered by
	 *         session number
	 * @throws NullPointerException
	 *             if the given venue is null
	 */
	public List<Event> getEvents(Venue venue) {
		if (venue == null) {
			throw new NullPointerException(
					"Cannot retrieve events for a null venue.");
		}
		// the events for the given venue
		List<Event> venueEvents = new ArrayList<>();
		for (Event e : events) {
			if (venue.equals(e.getVenue())) {
				venueEvents.add(e);
			}
		}
		Collections.sort(venueEvents);
		return venueEvents;
	}

	/**
	 * Returns a list of the events scheduled for the given session time (across
	 * all venues). The list should be ordered by venue name (in ascending
	 * order).
	 * 
	 * @param session
	 *            the session to retrieve the events for
	 * @return A list of the events scheduled for the given session time.
	 * @throws InvalidSessionException
	 *             if session <= 0
	 */
	public List<Event> getEvents(int session) {
		if (session <= 0) {
			throw new InvalidSessionException("Session number " + session
					+ " must be positive");
		}
		// the events for the given session
		List<Event> sessionEvents = new ArrayList<>();
		for (Event e : events) {
			if (session == e.getSession()) {
				sessionEvents.add(e);
			}
		}
		Collections.sort(sessionEvents);
		return sessionEvents;
	}

	/**
	 * Returns a set of all the venues where at least one event from the line-up
	 * takes place.
	 * 
	 * @return The venues where events from the line-up will take place.
	 */
	public Set<Venue> getVenues() {
		Set<Venue> venues = new HashSet<>(); // venues used by the line-up
		for (Event e : events) {
			venues.add(e.getVenue());
		}
		return venues;
	}

	/**
	 * If there is at least one event scheduled, then this method returns the
	 * number of the first session where there is an event scheduled. Otherwise
	 * it returns 0.
	 * 
	 * @return If there is at least one event scheduled, then the first session
	 *         number that an event is scheduled for, and 0 otherwise.
	 */
	public int getFirstUsedSession() {
		if (events.isEmpty()) {
			return 0;
		}
		// the minimum session number that there is an event scheduled for
		int result = events.get(0).getSession();
		for (Event e : events) {
			if (e.getSession() < result) {
				result = e.getSession();
			}
		}
		return result;
	}

	/**
	 * If there is at least one event scheduled, then this method returns the
	 * number of the last session where there is an event scheduled. Otherwise
	 * it returns 0.
	 * 
	 * @return If there is at least one event scheduled, then the last session
	 *         number that an event is scheduled for, and 0 otherwise.
	 */
	public int getLastUsedSession() {
		if (events.isEmpty()) {
			return 0;
		}
		// the maximum session number that there is an event scheduled for
		int result = events.get(0).getSession();
		for (Event e : events) {
			if (e.getSession() > result) {
				result = e.getSession();
			}
		}
		return result;
	}

	/**
	 * Returns an iterator over the events in the line-up.
	 */
	@Override
	public Iterator<Event> iterator() {
		return events.iterator();
	}

	/**
	 * The string representation of a line-up contains a line-separated
	 * concatenation of the string representations of the events in the line up.
	 * The events in the line-up should be ordered using their natural ordering
	 * (i.e. using the compareTo method defined in the Event class).
	 * 
	 * The line separator string used to separate the events should be retrieved
	 * in a machine-independent way by calling the function
	 * System.getProperty("line.separator").
	 */
	@Override
	public String toString() {
		Collections.sort(events);
		String result = ""; // the string representation under construction
		for (int i = 0; i < events.size(); i++) {
			if (i != 0) {
				result += LINE_SEPARATOR;
			}
			result = result + events.get(i).toString();
		}
		return result;
	}

	/**
	 * Determines whether this LineUp is internally consistent (i.e. it
	 * satisfies its class invariant).
	 * 
	 * @return true if this LineUp is internally consistent, and false
	 *         otherwise.
	 */
	public boolean checkInvariant() {
		return (events != null && !events.contains(null) && !eventsClash());
	}

	/**
	 * Returns true if there are two or more events scheduled for both the same
	 * venue and session time.
	 * 
	 * @return true if there are two or more events occurring at the same venue
	 *         at the same time
	 */
	private boolean eventsClash() {
		for (Event a : events) {
			for (Event b : events) {
				if (!a.equals(b) && a.getVenue().equals(b.getVenue())
						&& a.getSession() == b.getSession()) {
					return true;
				}
			}
		}
		return false;
	}

}
