package edu.virginia.cs.hw7;

import edu.virginia.cs.hw7.reviews.DatabaseManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class SeeReviewController {
    static DatabaseManager databaseManager;
    @FXML
    private TextField courseName;
    @FXML
    private Button checkReviewsButton;
    @FXML
    private Button mainMenuButton;
    @FXML
    private Label errorMessage;
    @FXML
    private Label averageRatingLabel;
    @FXML
    private VBox vBox;
    private PauseTransition errorPause;

    private void initializeErrorPause() {
        errorPause = new PauseTransition(Duration.seconds(3));
        errorPause.setOnFinished( event ->
                errorMessage.setVisible(false));
    }

    @FXML
    public void returnToMainMenu(ActionEvent event) throws IOException {
        CourseReviewApplication app = new CourseReviewApplication();
        app.changeScene("main-menu.fxml");
    }

    @FXML
    public void checkReviews(ActionEvent event) {
        databaseManager = new DatabaseManager();
        getCourseReviews();
    }

    private void getCourseReviews() {
        String course = courseName.getText().toString();
        String[] splitCourse = course.split(" ");

        if (courseName.getText().isEmpty()) {
            initializeErrorPause();
            errorMessage.setText("No course entered. Please try again.");
            errorMessage.setVisible(true);
            errorPause.play();
            return;
        }

        if (splitCourse.length != 2 || splitCourse[0].length() > 4 || splitCourse[1].length() != 4) {
            initializeErrorPause();
            errorMessage.setText("Invalid course. Please check the course name and try again.");
            errorMessage.setVisible(true);
            errorPause.play();
        }

        else {
            String courseDepartment = splitCourse[0].toUpperCase();
            int courseNumber = Integer.parseInt(splitCourse[1]);

            if (!databaseManager.checkIfCourseReviewExist(courseDepartment, courseNumber)) {
                vBox.getChildren().clear();
                averageRatingLabel.setText("");
                vBox.getChildren().add(new Label("No reviews for this course."));
            }

            else {
                List<String> reviewsList = databaseManager.getCourseReviews(courseDepartment, courseNumber);
                double averageRating = databaseManager.getCourseAverageRating(courseDepartment, courseNumber);
                vBox.getChildren().clear();

                // Resource: https://stackoverflow.com/questions/59891297/javafx-how-to-print-string-of-array-in-label-using-button-and-textfield
                for (int i = 0; i < reviewsList.size(); i++) {
                    Label label = new Label(reviewsList.get(i).toString());
                    label.setStyle("-fx-font-family: Roboto; -fx-font-size: 12; -fx-text-fill: black");
                    vBox.getChildren().add(label);
                }

                averageRatingLabel.setText(averageRating + "/5");
            }
        }
    }


}
