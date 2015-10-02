package festival.gui;


/**
 * This class provides the main method that runs the Festival Planner.
 * 
 * INSTRUCTIONS: DO NOT MODIFY THIS CLASS
 */
public class FestivalPlanner {

	/** Starts the GUI. */
	public static void main(String[] args) throws Exception {
		PlannerModel model = new PlannerModel();
		PlannerView view = new PlannerView(model);
		new PlannerController(model, view);
		view.setVisible(true);
	}

}
