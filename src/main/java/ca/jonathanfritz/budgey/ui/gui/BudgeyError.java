package ca.jonathanfritz.budgey.ui.gui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class BudgeyError {

	private BudgeyError() {
		// private constructor forces use of the show method
	}

	public static void show(final String error, final String details) {
		show(error, details, null);
	}

	public static void show(final String error, final String details, final Throwable ex) {
		final Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Oops! Something Broke");
		alert.setHeaderText(error);
		alert.setContentText(details);

		// if an exception was supplied, show it
		if (ex != null) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			final String exceptionText = sw.toString();

			final Label label = new Label("The exception stacktrace was:");

			final TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			final GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			alert.getDialogPane().setExpandableContent(expContent);
		}

		alert.showAndWait();
	}
}
