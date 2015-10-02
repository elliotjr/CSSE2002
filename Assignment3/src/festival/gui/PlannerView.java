package festival.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import festival.Event;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The view for the Festival Planner.
 */
@SuppressWarnings("serial")
public class PlannerView extends JFrame {
	// The model of the view.
	private PlannerModel model;
	// A combo box which contains the line up events.
	private JComboBox<Event> lineUp;
	// A combo box which contains the session times of the festival.
	private JComboBox<String> sessions;
	// A button used to add events to the day plan. 
	private JButton addButton;
	// A button used to remove events from the day plan.
	private JButton removeButton;
	// A JList used to store the users proposed plan.
	private JList<Event> dayPlan;
	// The error dialog scroll pane.
	private JScrollPane errorDialog;
	// The error dialog text box.
	private JTextArea errorDialogText;
	// The model of the users proposed day plan.
	private DefaultListModel<Event> dayPlanListModel;
	// The model for the sessions of the festival.
	private DefaultComboBoxModel<String> sessionModel;
	// The model for the line up of the festival.
	private DefaultComboBoxModel<Event> lineUpModel;
	// The button to save the list to a pdf
	private JButton save; 
	// The button to add the author of the festival 
	private JTextField name; 

	/**
	 * Creates a new Festival Planner window.
	 */
	public PlannerView(PlannerModel model) {
		this.model = model;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(600, 500);
		// Set the title of the window.
		this.setTitle("Festival Day Planner");
		this.setResizable(false);
		// View appears in the middle of the screen.
		this.setLocationRelativeTo(null);
		// Total area of the view.
		Container content = getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		JPanel topTitle = new JPanel();
		topTitle.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel title = new JLabel(
				"Please Select a session to find some events.");
		// Add a title to the top of the screen. (Not the window title).
		topTitle.add(title);
		content.add(topTitle);
		addTop(content);
		// Adds a horizontal line across the view.
		content.add(new JSeparator());
		addBottom(content);
	}

	/**
	 * A method which adds the bottom panel to the JFrame (day plan list and the
	 * buttons)
	 * 
	 * @require content is not null.
	 * @ensure that the elements will be added to the content component
	 * @param content
	 *            - the component where elements in this method are put into
	 */
	private void addBottom(Container content) {
		// A panel at the bottom of the window.
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
		// Add the day plan into this panel.
		addDayPlan(bottomPanel);
		JPanel bottom = new JPanel();
		bottom.setLayout(new FlowLayout(FlowLayout.CENTER));
		// A label describing the users day plan.
		bottom.add(new JLabel("Your Day Plan:"));
		bottom.add(bottomPanel);
		// Creates a space between elements.
		bottom.add(Box.createRigidArea(new Dimension(25, 0)));
		// Add the remove buttons.
		addBottomButtons(bottom);
		content.add(bottom);
	}

	/**
	 * A method which adds the top panel to the JFrame
	 * 
	 * @require content is not null.
	 * @ensure that the elements will be added to the content component
	 * @param content
	 *            - the component where elements in this method are put into
	 */
	private void addTop(Container content) {
		// A panel at the top of the window.
		JPanel topPanel = new JPanel();
		name = new JTextField("Default"); 
		// Set the size of the top panel.
		topPanel.setPreferredSize(new Dimension(500, 100));
		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		// Add a new JLabel describing the line up combo box.
		topPanel.add(name); 
		topPanel.add(new JLabel("Festival Line Up:", JLabel.LEFT));
		// Add the combo boxes to this panel.
		addComboBoxes(topPanel);
		topPanel.add(Box.createRigidArea(new Dimension(25, 0)));
		// Add the top buttons to this panel.
		addTopButtons(topPanel);
		content.add(topPanel);
	}
	
	/**
	 * 
	 * @return The name of the person who is making lineup
	 */
	public String getUserName() {
		return name.getText();
	}
	
	public JButton getSave() {
		return save; 
	}

	/**
	 * A method which adds combo boxes to a container.
	 * 
	 * @require content is not null.
	 * @ensure that the elements will be added to the content component
	 * @param content
	 *            - the component where elements in this method are put into
	 */
	private void addComboBoxes(Container content) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		sessions = new JComboBox<String>();
		// Hover tips for the session combo box.
		sessions.setToolTipText("Sessions for the line-up");
		panel.add(sessions);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		lineUp = new JComboBox<Event>();
		// Text appears when mouse hovers over line up combo box.
		lineUp.setToolTipText("The line-up for the festival");
		lineUp.setPreferredSize(new Dimension(260, 20));
		// Add the line up into this panel.
		panel.add(lineUp);
		content.add(panel);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		// Add the error box into this panel.
		addErrorBox(panel);
	}

	/**
	 * A method which adds the day plan component to a container.
	 * 
	 * @require content is not null.
	 * @ensure that the elements will be added to the content component
	 * @param content
	 *            - the component where elements in this method are put into
	 */
	private void addDayPlan(Container content) {
		JPanel panel = new JPanel();
		dayPlan = new JList<Event>();
		// Single selection only.
		dayPlan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Add the day plan into the scroll pane.
		JScrollPane scrollPane = new JScrollPane(dayPlan);
		// Set the size of scroll pane
		scrollPane.setPreferredSize(new Dimension(258, 200));
		scrollPane.setMaximumSize(new Dimension(258, 200));
		panel.add(scrollPane);
		content.add(panel, BorderLayout.AFTER_LAST_LINE);
	}

	/**
	 * A method which adds the top buttons panel to a container
	 * 
	 * @require content is not null.
	 * @ensure that the elements will be added to the content component
	 * @param content
	 *            - the component where elements in this method are put into
	 */
	private void addTopButtons(Container content) {
		JPanel panel = new JPanel();
		// The size of the largest button - make all buttons this size.
		JButton buttonSize = new JButton("remove event");
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		addButton = new JButton("add event");
		// Hover hints for add event button.
		addButton.setToolTipText("Click here to add the event to the day plan");
		// Make the add button the size of the largest button.
		addButton.setPreferredSize(buttonSize.getPreferredSize());
		// Adds the add button to this panel.
		panel.add(addButton);
		content.add(panel);
	}

	/**
	 * A method which adds the bottom buttons panel to a container
	 * 
	 * @require content is not null.
	 * @ensure that the elements will be added to the content component
	 * @param content
	 *            - the component where elements in this method are put into
	 */
	private void addBottomButtons(Container content) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		removeButton = new JButton("remove event");
		save = new JButton("Save"); 
		// Disable the "remove event" button.
		removeButton.setEnabled(false);
		// Add the remove button to this panel.
		panel.add(removeButton);
		panel.add(save); 
		content.add(panel);
	}

	/**
	 * A method which adds the error panel to a container
	 * 
	 * @require content is not null.
	 * @ensure that the elements will be added to the content component
	 * @param content
	 *            - the component where elements in this method are put into
	 */
	private void addErrorBox(Container content) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout());
		panel.setPreferredSize(new Dimension(256, 50));
		// Create an etched border for the error text area.
		Border errorTitle = new EtchedBorder(EtchedBorder.LOWERED);
		panel.setBorder(errorTitle);
		errorDialogText = new JTextArea();
		errorDialogText = new JTextArea("Your day plan is currently empty.");
		// Add a margin to the inside of the text area.
		errorDialogText.setMargin(new Insets(2, 2, 2, 2));
		errorDialogText.setLineWrap(true);
		// The user is not able to edit this text area.
		errorDialogText.setEditable(false);
		errorDialogText.setWrapStyleWord(true);
		// Make errorDialogText scrollable.
		errorDialog = new JScrollPane(errorDialogText);
		// Add the error dialog to this panel.
		panel.add(errorDialog);
		content.add(panel);
	}

	/**
	 * Updates the users proposed day plan.
	 * 
	 * @require dayPlanListModel && dayPlan && dayPlanModel are not null.
	 * @ensure the view will be updated with current model.
	 * @param dayPlanModel
	 *            - A list of events from the model of the GUI.
	 */
	public void updateDayPlan(ArrayList<Event> dayPlanModel) {
		dayPlanListModel = new DefaultListModel<Event>();
		// Add events to the day plan model.
		for (Event event : dayPlanModel) {
			dayPlanListModel.addElement(event);
		}
		// Sets the updated model to the JList
		dayPlan.setModel(dayPlanListModel);
	}

	/**
	 * Updates the session combo box.
	 * 
	 * @require sessionListModel && sessionModel && sessions are not null.
	 * @ensure the view will be updated with the current model.
	 * @param sessionListModel
	 *            - A list of sessions from the model of the GUI.
	 */
	public void updateSessionList(ArrayList<String> sessionListModel) {
		sessionModel = new DefaultComboBoxModel<String>();
		// Add all session numbers to the sessionModel so they are displayed in
		// the JComboBox.
		for (String session : sessionListModel) {
			sessionModel.addElement(session);
		}
		// Sets the model to the combo box.
		sessions.setModel(sessionModel);
	}

	/**
	 * Updates the line up list.
	 * 
	 * @require likeUpListModel && lineUpModel && lineUp are not null.
	 * @ensure the view will be updated with the current model.
	 * @param lineUpListModel
	 *            - A list of events in the festival.
	 */
	public void updateLineUp(ArrayList<Event> lineUpListModel) {
		lineUpModel = new DefaultComboBoxModel<Event>();
		// For each event in the line up, add to the line up JList.
		for (Event event : lineUpListModel) {
			lineUpModel.addElement(event);
		}
		// Sets the model to the combo box.
		lineUp.setModel(lineUpModel);
	}

	/**
	 * A method which returns the day plan JList.
	 *
	 * @require dayPlan is not null.
	 * @ensure dayPlan will be returned.
	 * @return The day plan JList
	 */
	public JList<Event> getDayPlan() {
		return dayPlan;
	}

	/**
	 * A mathod which returns the line up JComboBox.
	 * 
	 * @require lineUp is not null.
	 * @ensure lineUp will be returned.
	 * @return The line up JComboBox.
	 */
	public JComboBox<Event> getLineUp() {
		return lineUp;
	}

	/**
	 * A mathod which returns the session JComboBox.
	 * 
	 * @require sessions is not null.
	 * @ensure sessions will be returned.
	 * @return The session JComboBox.
	 */
	public JComboBox<String> getSessions() {
		return sessions;
	}

	/**
	 * A mathod which returns the add button.
	 * 
	 * @require addButton is not null.
	 * @ensure addButton will be returned.
	 * @return The add JButton.
	 */
	public JButton getAddButton() {
		return addButton;
	}

	/**
	 * A mathod which returns the remove button.
	 * 
	 * @require removeButton is not null.
	 * @ensure removeButton will be returned.
	 * @return The remove JButton.
	 */
	public JButton getRemoveButton() {
		return removeButton;
	}

	/**
	 * A method to update the view when an error is caused by user input or an
	 * invalid file.
	 * 
	 * @require exceptionMessage is not null.
	 * @ensure The exception message will be displayed to the user without
	 *         crashing the program.
	 * 
	 * @param exceptionMessage
	 *            - the exception message that is to be displayed in the view
	 */
	public void showErrorMessage(String exceptionMessage) {
		// Creates a dialog box that contains an exception message.
		JOptionPane.showMessageDialog(this, exceptionMessage, "Invalid File",
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * A method to update the view when an error is caused by user input or an
	 * invalid file.
	 * 
	 * @require
	 * @ensure The exception message will be displayed to the user without
	 *         crashing the program.
	 * 
	 * @param exceptionMessage
	 *            - the exception message that is to be displayed in the view
	 * @param filename
	 *            - the file that emmitted the error
	 */
	public void showErrorMessage(String filename, String exceptionMessage) {
		// Creates a dialog box that contains an exception message.
		JOptionPane.showMessageDialog(this, exceptionMessage, filename,
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * A method to update the view when an error is caused by user input or an
	 * invalid file.
	 * 
	 * @require message is not null and success >= 0 && success <=2.
	 * @ensure The exception message will be displayed to the user without
	 *         crashing the program.
	 * 
	 * @param exceptionMessage
	 *            - the exception message that is to be displayed in the view
	 * @param success
	 *            - 0 for failed user input, 1 for valid, 2 for neutral (when
	 *            application first starts up)
	 */
	public void errorBoxMessage(String message, int success) {
		// There was a failure in trying to add the event.
		if (success == 0) {
			errorDialog.getParent().setBackground(new Color(240, 128, 128));
			// Adding the event was successful.
		} else if (success == 1) {
			errorDialog.getParent().setBackground(new Color(50, 205, 50));
			// Neither a success or failure - i.e. when the application starts
			// up.
		} else if (success == 2) {
			errorDialog.getParent().setBackground(errorDialog.getBackground());
		}
		errorDialogText.setText(message);
	}

	/**
	 * A method which returns the selected event in the day plan list.
	 * 
	 * @require Day plan model is not null.
	 * @ensure That the selected event is returned.
	 * @return The Event selected in the dayPlan JList.
	 */
	public Event getSelectedDayPlanEvent() {
		Event event = (Event) dayPlan.getSelectedValue();
		return event;
	}

	/**
	 * A method which returns the selected event in the day plan list.
	 * 
	 * @require Line up model is not null.
	 * @ensure That the selected event is returned.
	 * @return The Event selected in the lineUp JList.
	 */
	public Event getSelectedLineUpEvent() {
		Event event = (Event) lineUp.getSelectedItem();
		return event;
	}

	/**
	 * A method which returns the selected event in the day plan list.
	 * 
	 * @require sessions is not null.
	 * @ensure That the selected event is returned.
	 * @return The Event selected in the lineUp JList.
	 */
	public String getSelectedSession() {
		// The selected session.
		String session = (String) sessions.getSelectedItem();
		return session;
	}

	/**
	 * A method which adds an action listener to the session JList.
	 * 
	 * @require (!sessions.equals(null) && !a.equals(null))
	 * @ensure An action listener will be added to the respective component.
	 * @param actionListener
	 *            - An action listener class.
	 */
	public void addSessionListListener(ActionListener actionListener) {
		sessions.addActionListener(actionListener);
	}

	/**
	 * A method which adds an action listener to the add JButton.
	 * 
	 * @require (!add.equals(null) && !a.equals(null))
	 * @ensure An action listener will be added to the respective component.
	 * @param actionListener
	 *            - An action listener class.
	 */
	public void addAddListener(ActionListener actionListener) {
		addButton.addActionListener(actionListener);
	}

	/**
	 * A method which adds an action listener to the remove JButton.
	 * 
	 * @require (!remove.equals(null) && !a.equals(null))
	 * @ensure An action listener will be added to the respective component.
	 * @param actionListener
	 *            - An action listener class.
	 */
	public void addRemoveListener(ActionListener actionListener) {
		removeButton.addActionListener(actionListener);
	}
	
	public void addSaveListener(ActionListener actionListener) {
		save.addActionListener(actionListener); 
	}

}
