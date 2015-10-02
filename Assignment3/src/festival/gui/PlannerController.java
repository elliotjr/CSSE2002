package festival.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.itextpdf.text.DocumentException;

import festival.Event;
import festival.FormatException;

/**
 * The controller for the Festival Planner.
 */
public class PlannerController {

	// the model that is being controlled
	private PlannerModel model;
	// the view that is being controlled
	private PlannerView view;
	private Pdf pdf = new Pdf(); 

	/**
	 * Initialises the Controller for the Festival Planner.
	 */
	public PlannerController(PlannerModel model, PlannerView view) {
		// Initiates the view.
		this.view = view;
		// Initiates the model.
		this.model = model;
		try {
			model.initiateDayPlan();
			setDayPlanList();
		} catch (IOException e) {
			// Catch exception where file doesn't load.
			view.showErrorMessage(e.getMessage());
			// Close the system.
			System.exit(-1);
		} catch (FormatException e) {
			// Catch a format error from the timetable file.
			view.showErrorMessage("timetable.txt", e.getMessage());
			System.exit(-1);
		}
		try {
			model.initiateLineUp();
			setLineUpLists();
		} catch (IOException e) {
			// Catch an IOException error from the line up file.
			view.showErrorMessage(e.getMessage());
			System.exit(-1);
		} catch (FormatException e) {
			// Catch a format error from the line up file.
			view.showErrorMessage("lineUp.txt", e.getMessage());
			System.exit(-1);
		}
		// If there are no events in the line up, disable the add button.
		if (model.getLineUpModel().size() == 0) {
			view.getAddButton().setEnabled(false);
		}
		// Adds event listeners to the view components.
		addListeners();
	}

	/**
	 * Adds event listeners to the components in the view.
	 *
	 * @require The view contains a component that takes a listener.
	 * @ensure That an event listener will be added to that component.
	 */
	private void addListeners() {
		// Add a listener to the add button.
		this.view.addAddListener(new AddActionListener());
		// Add a listener to the remove button.
		this.view.addRemoveListener(new RemoveActionListener());
		// Add a listener to the session combo box.
		this.view.addSessionListListener(new AddSessionListListener());
		this.view.addSaveListener(new AddSaveListener());
	}

	/**
	 * Sets the model lists to their respective view components.
	 * 
	 * @require dayPlan && lineUp are not null.
	 * @ensure A model will be set for the respective lists and any errors will
	 *         be handled appropriately.
	 */
	private void setDayPlanList() {
		// Gets the current day plan list from model and add it to the view.
		view.updateDayPlan(model.getDayPlanModel());
	}

	private void setLineUpLists() {
		// Gets the current line up from the model and add it to the view.
		view.updateLineUp(model.getLineUpModel());
		// Gets the current session model from the model and adds it to the
		// view.
		view.updateSessionList(model.getSessionList());
		checkSessionSize();
	}

	/**
	 * Checks the model for errors and displays error in view
	 * 
	 * @require model and view are not null.
	 * @ensure If there is an error, it will be displayed appropriatly in the
	 *         view.
	 */
	private void checkSessionSize() {
		// If there is are no events, disable the add button and tell there user
		// that there are no events.
		if (model.getSessionList().size() == 1) {
			view.getAddButton().setEnabled(false);
			view.errorBoxMessage("There are no events.", 2);
			// Tell the user that their plan is empty.
		} else {
			view.errorBoxMessage("Your plan is empty", 2);
		}
	}

	/**
	 * A private class which implements an action listener on an add button in
	 * the view.
	 */
	private class AddActionListener implements ActionListener {
		/**
		 * A method which adds the selected event from a lineup to the proposed
		 * day plan.
		 * 
		 * @require !(model.equals(null) && !(view.equals(null) &&
		 *          !selection.equals(null)
		 * @ensure That either the event will be added to the proposed day plan
		 *         or the user is informed of an invalid entry.
		 */
		public void actionPerformed(ActionEvent event) {
			// The selected event from the list.
			Event selection = view.getSelectedLineUpEvent();
			model.getIntermediatePlan().add(selection);
			checkAddExceptions(selection);
			// The size of the model is greater than 0.
			if (model.getDayPlanModel().size() > 0) {
				view.getRemoveButton().setEnabled(true);
				// If there are no events in the day plan, disable the remove
				// button.
			} else {
				view.getRemoveButton().setEnabled(false);
			}
		}
	}
	
	private class AddSaveListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				String name = view.getUserName(); 
				pdf.addOwner(name); 
				pdf.addLineUp(model.getDayPlanModel());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//view.getSave().setEnabled(false);
		}
	}

	/**
	 * A method which checks the validity of adding a new event to the proposed
	 * day plan
	 * 
	 * @require model && view && selection are not null.
	 * @ensure Either the successful adding of an event to the day plan or
	 *         message sent to the user.
	 * @param selection
	 *            - the event the user wants added to the day plan.
	 */
	private void checkAddExceptions(Event selection) {
		// The day plan already contains selection, inform the user.
		if (model.containsEvent(selection)) {
			view.errorBoxMessage("The day plan already contains '" + selection
					+ "'", 0);
			model.getIntermediatePlan().remove(selection);
			// The day plan already contains an event with the same session as
			// selection.
		} else if (model.isNotUniqueSession(selection)) {
			view.errorBoxMessage(
					"The plan already includes an event in session "
							+ selection.getSession(), 0);
			model.getIntermediatePlan().remove(selection);
			// The previous event in the day plan cannot reach selection.
		} else if (!model.canReachPrevious(selection)) {
			view.errorBoxMessage(selection
					+ " cannot be reached by the previous event.", 0);
			// Selection cannot reach the next event in the day plan.
		} else if (!model.canReachNext(selection)) {
			view.errorBoxMessage(selection + " cannot reach the next event.", 0);
			// Add selection to the day plan.
		} else {
			view.errorBoxMessage("Successfully added '" + selection + "'", 1);
			model.addToDayPlan(selection);
			setDayPlanList();
		}

	}

	/**
	 * A private class which implements the action listener for a list full of
	 * sessions. The user is able to select a session which will then sort the
	 * line up by that session.
	 */
	private class AddSessionListListener implements ActionListener {
		/**
		 * Populates the line up list with a respective session number which has
		 * been selected form the session number list.
		 * 
		 * @require model and view are not null.
		 * @ensure If session x is selected, all events with session x in the
		 *         line up will be added to the line up model.
		 * @param event
		 *            - The event that has taken place in the view.
		 */
		public void actionPerformed(ActionEvent event) {
			String selection = view.getSelectedSession();
			// Populates the line up model that have the session number of
			// selected session.
			model.createSessionModel(selection);
			// Update the line up with these events.
			view.updateLineUp(model.getLineUpModel());
		}
	}

	/**
	 * A private class which implements an action listener on an remove button
	 * in the view.
	 */
	private class RemoveActionListener implements ActionListener {
		/**
		 * A method which removes the selected event from a lineup to the
		 * proposed day plan.
		 * 
		 * @require model && view && selection are not null.
		 * @ensure That either the event will be removed from the proposed day
		 *         plan or the user is informed of an invalid removal.
		 * @param event
		 *            - the event that has taken place on the view - (The user
		 *            has clicked the remove button).
		 */
		public void actionPerformed(ActionEvent event) {
			Event selection = view.getSelectedDayPlanEvent();
			checkRemoveErrors(selection);
		}
	}

	/**
	 * A method which checks the validity of removing an event from the proposed
	 * day plan
	 * 
	 * @require model && view && selection are not null.
	 * @ensure Either the successful removal of an event from the day plan or
	 *         message sent to the user.
	 * @param selection
	 *            - The event that the user wants removed from the day plan.
	 */
	private void checkRemoveErrors(Event selection) {
		// The model cannot remove the event without cause the day plan to be
		// compatible.
		if (view.getDayPlan().isSelectionEmpty()) {
			view.errorBoxMessage("Please select an event to remove.", 0);
			// Remove the event from the day plan.
		} else {
			view.errorBoxMessage("'" + selection + "' has been removed.", 1);
			model.removeFromDayPlan(selection);
			view.getDayPlan().clearSelection();
			setDayPlanList();
		}
		// If the day plan model is empty, the remove button is disabled.
		if (model.getDayPlanModel().size() == 0) {
			view.getRemoveButton().setEnabled(false);
		}
	}

}
