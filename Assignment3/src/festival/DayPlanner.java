package festival;

import java.util.*;

/**
 * A class with functionality for helping a festival-goer to plan their day at a
 * festival.
 */
public class DayPlanner {

	// the timetable of the festival
	private ShuttleTimetable timetable;

	/**
	 * @require timetable!=null
	 * @ensure Creates a new day planner for a festival with a copy of the given
	 *         shuttle timetable (so that changes to the parameter timetable
	 *         from outside of this class won't affect the timetable of the
	 *         day-planner.)
	 */
	public DayPlanner(ShuttleTimetable timetable) {
		this.timetable = new ShuttleTimetable();
		for (Service service : timetable) {
			this.timetable.addService(service);
		}
	}

	/**
	 * @require plan!=null && !plan.contains(null) && the events in the plan are
	 *          ordered (smallest to largest) by session number.
	 * @ensure Returns true if (and only if) the events in the plan are
	 *         compatible (as per assignment 2 hand-out). That is, (i) no event
	 *         appears more than once in the plan and no two different events in
	 *         the plan are scheduled for the same session, and (ii) for each
	 *         event in the plan, it is possible to go to that event and then
	 *         (using the available shuttle services in the day-planner's
	 *         timetable if necessary), get to the next event in the plan (on
	 *         time), if there is one.
	 * 
	 *         The timetable of the day-planner is not modified in any way by
	 *         this method.
	 * 
	 */
	public boolean compatible(List<Event> plan) {
		for (int i = 1; i < plan.size(); i++) {
			// adjacent events to check for collisions and compatibility
			Event previous = plan.get(i - 1);
			Event next = plan.get(i);
			if (previous.getSession() == next.getSession()
					|| !canReach(previous, next)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @require source != null && destination != null
	 * @ensure Returns true if it is possible to be at the source's venue for
	 *         the duration of the source's session, and then, using the
	 *         available shuttle services (if necessary) to get there, be at the
	 *         destination's venue in time for the given destination's session.
	 **/
	public boolean canReach(Event source, Event destination) {
		return canReach(source.getVenue(), source.getSession(),
				destination.getVenue(), destination.getSession());
	}

	/**
	 * @require sourceVenue != null && destinationVenue != null &&
	 *          sourceSession>0 && destinationSession >0
	 * @ensure Returns true if it is possible to be at the source venue for the
	 *         duration of the source session, and then, using the available
	 *         shuttle services (if necessary) to get there, be at the
	 *         destination venue in time for the given destination session.
	 **/
	private boolean canReach(Venue sourceVenue, int sourceSession,
			Venue destinationVenue, int destinationSession) {

		// you can't reach an event at an earlier time
		if (destinationSession < sourceSession) {
			return false;
		}
		// events at the same time have to be the same time and place
		if (destinationSession == sourceSession) {
			return sourceVenue.equals(destinationVenue);

		}
		// you can reach an event at the same venue at a later time
		if (sourceVenue.equals(destinationVenue)) {
			return true;
		}
		// services would be required to reach the destination
		for (int t = sourceSession; t < destinationSession; t++) {
			Set<Venue> adjacent = timetable.getDestinations(sourceVenue, t);
			for (Venue v : adjacent) {
				if (canReach(v, t + 1, destinationVenue, destinationSession)) {
					return true;
				}
			}
		}
		return false;
	}

}
