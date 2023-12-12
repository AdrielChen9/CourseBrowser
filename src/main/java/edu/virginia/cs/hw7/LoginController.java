package edu.virginia.cs.hw7;

import edu.virginia.cs.hw7.reviews.DatabaseManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {
    static DatabaseManager databaseManager;
    @FXML
    private Button signInButton;
    @FXML
    private Label errorLogin;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button createNewUserButton;
    private PauseTransition errorPause;

    // Resource: from our Wordle HW
    private void initializeErrorPause() {
        errorPause = new PauseTransition(Duration.seconds(3));
        errorPause.setOnFinished( event ->
                errorLogin.setVisible(false));
    }

    @FXML
    public void userLogin() throws IOException {
        databaseManager = new DatabaseManager();
        validLogin();
    }

    @FXML
    public void registerUser() throws IOException {
        CourseReviewApplication app = new CourseReviewApplication();
        app.changeScene("create-user.fxml");
    }


    private void validLogin() throws IOException {
        CourseReviewApplication app = new CourseReviewApplication();
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            initializeErrorPause();
            errorLogin.setText("Please enter your username and password.");
            errorLogin.setVisible(true);
            errorPause.play();
        }
        else if (databaseManager.checkValidLogin(username.getText().toString(), password.getText().toString())) {
            MainMenuController.setUsername(username.getText().toString());
            app.changeScene("main-menu.fxml");
        }
        else {
            initializeErrorPause();
            errorLogin.setText("Invalid username or password.");
            errorLogin.setVisible(true);
            errorPause.play();
        }
    }


}