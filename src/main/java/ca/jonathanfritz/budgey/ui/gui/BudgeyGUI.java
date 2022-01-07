package ca.jonathanfritz.budgey.ui.gui;

import java.io.IOException;
import java.util.Optional;

import ca.jonathanfritz.budgey.Credentials;
import javafx.application.Application;
import javafx.stage.Stage;

public class BudgeyGUI extends Application {

	@Override
	public void start(final Stage primaryStage) throws Exception {
		// TODO: this is instantiated via reflection, so we might need to do something funky to use Guice in here
		// TODO: wait for button press so this acts like a modal?
		final Optional<Credentials> result = LoginForm.create(primaryStage).showAndWait();
		result.ifPresent(credentials -> {
			System.out.println("Username: " + credentials.getUsername());
			System.out.println("Password: " + credentials.getPassword());
			try {
				System.out.println("Path: " + credentials.getPath());
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public static void main(final String[] args) {
		launch(args);
	}
}
