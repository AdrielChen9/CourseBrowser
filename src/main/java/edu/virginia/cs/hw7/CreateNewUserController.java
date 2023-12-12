package edu.virginia.cs.hw7;

import edu.virginia.cs.hw7.reviews.DatabaseManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.io.IOException;

public class CreateNewUserController {
    static DatabaseManager databaseManager;
    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;
    @FXML
    private Label errorLogin;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField confirmPassword;
    private PauseTransition errorPause;

    private void initializeErrorPause() {
        errorPause = new PauseTransition(Duration.seconds(3));
        errorPause.setOnFinished( event ->
                errorLogin.setVisible(false));
    }

    @FXML
    public void userRegister(ActionEvent event) {
        databaseManager = new DatabaseManager();
        validRegister();
    }

    @FXML
    public void backToLoginScreen(ActionEvent event) throws IOException {
        CourseReviewApplication app = new CourseReviewApplication();
        app.changeScene("login-menu.fxml");
    }

    private void validRegister() {
        CourseReviewApplication app = new CourseReviewApplication();

        if (username.getText().isEmpty()) {
            initializeErrorPause();
            errorLogin.setText("Please enter a username.");
            errorLogin.setVisible(true);
            errorPause.play();
        }

        else if (password.getText().isEmpty()) {
            initializeErrorPause();
            errorLogin.setText("Please enter a password.");
            errorLogin.setVisible(true);
            errorPause.play();
        }

        else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            initializeErrorPause();
            errorLogin.setText("Passwords do not match. Try again.");
            errorLogin.setVisible(true);
            errorPause.play();
        }
        else if (databaseManager.checkExistingUser(username.getText().toString())) {
            initializeErrorPause();
            errorLogin.setText("Username already exists. Please try again.");
            errorLogin.setVisible(true);
            errorPause.play();
        }
        else {
            databaseManager.addUser(username.getText().toString(), password.getText().toString());
            MainMenuController.setUsername(username.getText().toString());

            initializeErrorPause();
            errorLogin.setStyle("-fx-text-fill: green; -fx-font-weight: bold");
            errorLogin.setText("You have successfully created an account!");
            errorLogin.setVisible(true);
            errorPause.play();

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> {
                try {
                    app.changeScene("main-menu.fxml");
                } catch (IOException e) {
                    errorLogin.setText("Error returning to Main Menu, please exit out of the program.");
                }
            });
            pause.play();
        }
    }
}
