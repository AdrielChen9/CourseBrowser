package edu.virginia.cs.hw7;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.io.IOException;

public class MainMenuController {
    @FXML
    private Button logout;
    @FXML
    private Button submitReviewButton;
    @FXML
    private Button seeReviewButton;
    public static String userLoginName;

    public static void setUsername(String username) {
        userLoginName = username;
    }

    @FXML
    public void userLogOut(ActionEvent event) throws IOException {
        CourseReviewApplication app = new CourseReviewApplication();
        app.changeScene("login-menu.fxml");
    }

    @FXML
    public void userSubmitReview(ActionEvent event) throws IOException {
        CourseReviewApplication app = new CourseReviewApplication();
        SubmitReviewController.setUsername(userLoginName);
        app.changeScene("submit-reviews.fxml");
    }

    @FXML
    public void userSeeReview(ActionEvent event) throws IOException {
        CourseReviewApplication app = new CourseReviewApplication();
        app.changeScene("see-reviews.fxml");
    }
}
