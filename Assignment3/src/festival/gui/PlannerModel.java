package festival.gui;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.util.ArrayList;

import festival.*;

/**
 * The model for the Festival Planner.
 */
public class PlannerModel {

	// The shuttle timetable for the model.
	private ShuttleTimetable timetable;
	// The line up of events in the festival.
	private LineUp lineUp;
	// The line model for the line up.
	private ArrayList<Event> lineUpModel;
	// The users proposed day plan.
	private ArrayList<Event> dayPlanModel;
	// The dayplanner to check for day plan compatability.
	private DayPlanner dayPlanner;
	// The list of all sessions in the festival.
	private ArrayList<String> sessionList;
	// An intermediate day plan used to check for validity when adding events.
	private ArrayList<Event> intermediatePlan;

	/**
	 * Initialises the model for the Festival Planner.
	 */
	public PlannerModel() {
	}

	/**
	 * Reads the timetable, and initiates their respective list models.
	 * 
	 * @require intermediatePlan && dayPlanModel && dayPlanner are not null.
	 * @ensure Timetable file will be either loaded, or an exception will be
	 *         thrown.
	 * @throws IOException
	 *             - Invalid file/file doesn't exist.
	 * @throws FormatException
	 *             - If there is an error in file format.
	 */
	public void initiateDayPlan() throws IOException, FormatException {
		timetable = ScheduleReader.read("timetable.txt");
		dayPlanner = new DayPlanner(timetable);
		intermediatePlan = new ArrayList<Event>();
		dayPlanModel = new ArrayList<Event>();
	}

	/**
	 * Reads the line up file, and intiates line up models.
	 * 
	 * @require lineUp is not null.
	 * @ensure lineUp file will be either loaded, or an exception will be
	 *         thrown.
	 * @throws IOException
	 *             - Invalid file/file doesn't exist.
	 * @throws FormatException
	 *             - If there is an error in file format.
	 */
	public void initiateLineUp() throws IOException, FormatException {
		// Load in the line up and populate both session and line up model.
		lineUp = LineUpReader.read("lineUp.txt");
		createLineUpModel();
		populateSessionList();
	}

	/**
	 * Returns the list of session numbers of the line up.
	 * 
	 * @require sessionList is not null.
	 * @ensure sessionList will be returned.
	 * @return the list of session numbers of the line up.
	 */
	public ArrayList<String> getSessionList() {
		return sessionList;
	}

	/**
	 * Returns the users proposed day plan.
	 * 
	 * @require dayPlanModel is not null.
	 * @ensure dayPlanModel will be returned.
	 * @return The users proposed day plan.
	 */
	public ArrayList<Event> getDayPlanModel() {
		return dayPlanModel;
	}

	/**
	 * Return the line up.
	 * 
	 * @require lineUpModel is not null.
	 * @ensure lineUpModel is returned.
	 * @return the line up.
	 */
	public ArrayList<Event> getLineUpModel() {
		return lineUpModel;
	}

	/**
	 * Return the intermediate day plan.
	 * 
	 * @require intermediatePlan is not null.
	 * @ensure intermediatePlan is returned.
	 * @return the intermediate day plan.
	 */
	public ArrayList<Event> getIntermediatePlan() {
		return intermediatePlan;
	}

	/**
	 * Creates the line up model.
	 * 
	 * @require lineUpModel and lineUp are not null.
	 * @ensure the lineUpModel will be created.
	 */
	private void createLineUpModel() {
		lineUpModel = new ArrayList<Event>();
		// Adds each event in the line up to the line up model.
		for (Event event : lineUp) {
			lineUpModel.add(event);
		}
	}

	/**
	 * Adds all events that take place in the session.
	 * 
	 * @require lineUpModell && lineUp are not null.
	 * @ensure The respective events will be added to the lineUpModel.
	 * @param session
	 *            - the session number that has been selected.
	 */
	public void createSessionModel(String session) {
		lineUpModel.clear();
		// If the selection is "All sessions" add all events to line up model.
		if (session == "All sessions") {
			for (Event event : lineUp) {
				lineUpModel.add(event);
			}
			// If the session selected is a session number.
		} else {
			for (Event event : lineUp) {
				String eventSession = event.getSession() + "";
				// Add the events to the line up model that have the same line
				// up
				// number as session.
				if (eventSession.equals(session)) {
					lineUpModel.add(event);
				}
			}
		}
	}

	/**
	 * Populates the session list with session numbers.
	 * 
	 * @require lineUp && sessionList are not null.
	 * @ensure the session list will be populated with session numbers that are
	 *         in the line up, with no duplicates.
	 */
	private void populateSessionList() {
		// A set to contain all session numbers (ensure no duplicates).
		Set<Integer> sessionSet = new HashSet<Integer>();
		// Add each session to the session set (removes duplicates).
		for (Event event : lineUp) {
			sessionSet.add(event.getSession());
		}
		sessionList = new ArrayList<String>();
		sessionList.add("All sessions");
		// For each session, add to the session model.
		for (Integer session : sessionSet) {
			sessionList.add("" + session);
		}
	}

	/**
	 * A method which removes an event from the dayPlanModel.
	 *
	 * @require dayPlanModel && intermediatePlan are not null.
	 * @ensure event will be removed from the dayPlanModel.
	 * @param event
	 *            - the event that is to be removed from the dayPlanModel.
	 */
	public void removeFromDayPlan(Event event) {
		// Remove the event from both the intermediate plan & the model.
		dayPlanModel.remove(event);
		intermediatePlan.remove(event);
	}

	/**
	 * A method which adds an event to the dayPlanModel
	 * 
	 * @require dayPlanModel is not null.
	 * @ensure the event will be added to the dayPlanModel.
	 * @param event
	 *            - The event that is to be added to the dayPlanModel.
	 */
	public void addToDayPlan(Event event) {
		dayPlanModel.add(event);
		Collections.sort(dayPlanModel, new sessionComparator());
	}

	/**
	 * Checks if the day plan already contains an event.
	 * 
	 * @require dayPlanModel is not null.
	 * @ensure Return true if event is in the day plan, else, return false.
	 * @param event
	 *            - the event to be checked.
	 * @return true if the current day plan contains the event, false otherwise.
	 */
	public boolean containsEvent(Event event) {
		// If dayPlanModel contains event, return true.
		if (dayPlanModel.contains(event)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether an event with the same session already resides in the day
	 * plan
	 * 
	 * @require dayPlanModel is not null.
	 * @ensure return true if there already exists an event with the same
	 *         session number in the day plan, false otherwise.
	 * 
	 * @param event
	 *            - the event that is to be added to be checked.
	 * @return true if there already exists an event with the same session
	 *         number in the day plan, false otherwiese.
	 */
	public boolean isNotUniqueSession(Event event) {
		for (int i = 0; i < dayPlanModel.size(); i++) {
			// The day plan model cannot contain an event with two same session
			// numbers.
			if (event.getSession() == dayPlanModel.get(i).getSession()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the event being added to the day plan can be reached by the
	 * previous event.
	 * 
	 * @require intermediatePlan && event && dayPlanner are not null.
	 * @ensure that if the event can be reached by the previous event, return
	 *         true, otherwise, return false.
	 * @param event
	 *            - The event being reached by the previous event.
	 * @return true if can be reached, otherwise, return false.
	 */
	public boolean canReachPrevious(Event event) {
		Collections.sort(intermediatePlan, new sessionComparator());
		int eventIndex = intermediatePlan.indexOf(event);
		// If there are no events in the day plan, then it's suitable to add the
		// selected event, or if the previous can reach the selected event.
		if (eventIndex == 0
				|| dayPlanner.canReach(intermediatePlan.get(eventIndex - 1),
						intermediatePlan.get(eventIndex))) {
			return true;
			// The pervious event cannot reach the selected event.
		} else {
			intermediatePlan.remove(event);
			return false;
		}
	}

	/**
	 * Checks if the event being added to the day plan can the next event in the
	 * day plan.
	 * 
	 * @require intermediatePlan && event && dayPlanner are not null.
	 * @ensure That if the event can reach the next event in the day plan,
	 *         return true, otherwise return false.
	 * @param event
	 *            - The event reaching the next event.
	 * @return true if can reach next event, otherwise, return false.
	 */
	public boolean canReachNext(Event event) {
		Collections.sort(intermediatePlan, new sessionComparator());
		// The index of the event being added to the list.
		int eventIndex = intermediatePlan.indexOf(event);
		// There is nothing in the list, or the event being added can reach the
		// next event.
		if (intermediatePlan.size() - 1 == eventIndex
				|| dayPlanner.canReach(intermediatePlan.get(eventIndex),
						intermediatePlan.get(eventIndex + 1))) {
			return true;
			// The selected event cannot reach the next event - remove it from
			// the day plan.
		} else {
			intermediatePlan.remove(event);
			return false;
		}
	}

	/**
	 * A comparator, which imposes a total ordering on a collection of events.
	 */
	private class sessionComparator implements Comparator<Event> {
		/**
		 * A compare method, which imposes a total ordering on the session
		 * number of an event (from lowest to highest).
		 * 
		 * @require event1 && event2 are not null.
		 * @ensure event1 and event2 will be ordered by session number.
		 */
		@Override
		public int compare(Event event1, Event event2) {
			// Compare two events on session number.
			return new Integer(event1.getSession()).compareTo(event2
					.getSession());
		}
	}
}
