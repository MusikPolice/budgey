package ca.jonathanfritz.budgey.ui.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import ca.jonathanfritz.budgey.BudgeyFile;
import ca.jonathanfritz.budgey.Credentials;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class LoginForm {
	public final static ExtensionFilter PROFILE_EXTENSION_FILTER = new ExtensionFilter("Budgey Database File", BudgeyFile.DATABASE_FILE_EXTENSION, BudgeyFile.DATABASE_BACKUP_FILE_EXTENSION);

	private LoginForm() {
		// private constructor to force use of static factory method
	}

	public static Dialog<Credentials> create(final Stage primaryStage) {
		final Dialog<Credentials> dialog = new Dialog<>();
		dialog.setTitle("Unlock Database - Budgey");
		dialog.setGraphic(new ImageView(new Image("padlock.svg", 25, 25, true, true)));
		dialog.setHeaderText("Budgey: A budgeting tool for people with dollars and sense");
		dialog.getDialogPane().getButtonTypes().add(new ButtonType("Login", ButtonData.OK_DONE));

		final String profileName = BudgeyFile.getDefaultProfileName();
		Path profilePath = null;
		try {
			profilePath = BudgeyFile.getDefaultFilePath(profileName);
		} catch (final IOException e) {
			BudgeyError.show("Failed to create profile directory", "Please ensure that you have permission to create the folder "
			        + BudgeyFile.BUDGEY_FOLDER, e);
			System.exit(1);
		}

		final GridPane grid = createGridPane();
		final ChangeListener<String> usernameChangeListener = createProfilePathControls(primaryStage, grid, profilePath);
		final TextField usernameTextField = createUsernameControls(grid, profileName, usernameChangeListener);
		final PasswordField passwordTextField = createPasswordControls(grid);

		dialog.getDialogPane().setContent(grid);
		Platform.runLater(() -> passwordTextField.requestFocus());
		dialog.setResultConverter(dialogButton -> {
			// TODO: this doens't respect custom profile paths
			// TODO: validation of entries?
			return Credentials.newBuilder(passwordTextField.getText())
			                                               .setUsername(usernameTextField.getText())
			                                               .setPath(path); // argh! i need the path field!
		});
		return dialog;
	}

	private static GridPane createGridPane() {
		final GridPane grid = new GridPane();

		// the column with the text field always takes up all extra space. The other two columns are fixed to
		// accommodate their contents
		final ColumnConstraints labelColumnConstraints = new ColumnConstraints();
		labelColumnConstraints.setMinWidth(60);
		final ColumnConstraints textFieldColumnConstraints = new ColumnConstraints(100, 250, Double.MAX_VALUE);
		textFieldColumnConstraints.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints()
		    .addAll(labelColumnConstraints, textFieldColumnConstraints, new ColumnConstraints());
		grid.setAlignment(Pos.CENTER);

		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(25, 25, 25, 25));
		return grid;
	}

	private static ChangeListener<String> createProfilePathControls(final Stage primaryStage, final GridPane grid, final Path profilePath) {
		// text field and file chooser for picking the database file to open
		final Label filePathLabel = new Label("Profile:");
		final Label filePathTextField = new Label(profilePath.toString());
		final ChangeListener<String> usernameChangeListener = new ChangeListener<String>() {

			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
				try {
					filePathTextField.setText(BudgeyFile.getDefaultFilePath(newValue).toString());
				} catch (final IOException e) {
					BudgeyError.show("Failed to create profile directory", "Please ensure that you have permission to create the folder "
					        + BudgeyFile.BUDGEY_FOLDER, e);
					System.exit(1);
				}
			}
		};
		final Button filePickerButton = new Button();
		filePickerButton.setGraphic(new ImageView(new Image("file-folder.svg", 15, 15, true, true)));
		filePickerButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				final FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Profile");
				fileChooser.setInitialDirectory(profilePath.getParent().toFile());
				fileChooser.getExtensionFilters().add(PROFILE_EXTENSION_FILTER);
				final File file = fileChooser.showSaveDialog(primaryStage);
				if (file != null) {
					filePathTextField.setText(file.getAbsolutePath());
				}
			}
		});
		grid.add(filePathLabel, 0, 0);
		grid.add(filePathTextField, 1, 0);
		grid.add(filePickerButton, 2, 0);

		return usernameChangeListener;
	}

	private static TextField createUsernameControls(final GridPane grid, final String profileName, final ChangeListener<String> usernameChangeListener) {
		final Label userNameLabel = new Label("Username:");
		final TextField userNameTextField = new TextField(profileName);
		userNameTextField.textProperty().addListener(usernameChangeListener);
		grid.add(userNameLabel, 0, 1);
		grid.add(userNameTextField, 1, 1, 2, 1);
		return userNameTextField;
	}

	private static PasswordField createPasswordControls(final GridPane grid) {
		final Label passwordLabel = new Label("Password:");
		final PasswordField passwordTextField = new PasswordField();
		grid.add(passwordLabel, 0, 2);
		grid.add(passwordTextField, 1, 2, 2, 1);
		return passwordTextField;
	}
}
