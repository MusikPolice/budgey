package ca.jonathanfritz.budgey.ui.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class BudgeyGUI extends Application {

	@Override
	public void start(final Stage primaryStage) throws Exception {
		// TODO: this is instantiated via reflection, so we might need to do something funky to use Guice in here
		// TODO: wait for button press so this acts like a modal?
		LoginForm.create(primaryStage).show();

	}

	public static void main(final String[] args) {
		launch(args);
	}
}
