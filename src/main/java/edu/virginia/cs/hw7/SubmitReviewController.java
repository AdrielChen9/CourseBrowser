package edu.virginia.cs.hw7;

import edu.virginia.cs.hw7.reviews.DatabaseManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.io.IOException;

public class SubmitReviewController {
    static DatabaseManager databaseManager;
    @FXML
    private TextField courseName;
    @FXML
    private TextArea reviewText;
    @FXML
    private TextField ratingNumber;
    @FXML
    private Label errorSubmitReview;
    @FXML
    private Button submitReviewButton;
    @FXML
    private Button mainMenuButton;
    public static String userLoginName;
    private PauseTransition errorPause;

    public static void setUsername(String username) {
        userLoginName = username;
    }

    private void initializeErrorPause() {
        errorPause = new PauseTransition(Duration.seconds(3));
        errorPause.setOnFinished( event ->
                errorSubmitReview.setVisible(false));
    }

    @FXML
    public void userSubmitReview(ActionEvent event) throws IOException {
        databaseManager = new DatabaseManager();
        validReview();
    }

    @FXML
    public void returnToMainMenu(ActionEvent event) throws IOException {
        CourseReviewApplication app = new CourseReviewApplication();
        app.changeScene("main-menu.fxml");
    }

    private void validReview() {
        CourseReviewApplication app = new CourseReviewApplication();
        String course = courseName.getText().toString();
        String[] splitCourse = course.split(" ");

        if (courseName.getText().isEmpty()) {
            initializeErrorPause();
            errorSubmitReview.setText("No course entered. Please try again.");
            errorSubmitReview.setVisible(true);
            errorPause.play();
            return;
        }

        if (splitCourse.length != 2 || splitCourse[0].length() > 4 || splitCourse[1].length() != 4) {
            initializeErrorPause();
            errorSubmitReview.setText("Invalid course. Please try again.");
            errorSubmitReview.setVisible(true);
            errorPause.play();
            return;
        }

        if (reviewText.getText().isEmpty()) {
            initializeErrorPause();
            errorSubmitReview.setText("Please enter a review.");
            errorSubmitReview.setVisible(true);
            errorPause.play();
            return;
        }

        if (ratingNumber.getText().isEmpty()) {
            initializeErrorPause();
            errorSubmitReview.setText("Please enter a rating.");
            errorSubmitReview.setVisible(true);
            errorPause.play();
            return;
        }

        String courseDepartment = splitCourse[0].toUpperCase();
        int courseNumber = Integer.parseInt(splitCourse[1]);

        if (!isNumeric(ratingNumber.getText())) {
            initializeErrorPause();
            errorSubmitReview.setText("Invalid rating (1-5). Please ensure rating is within bounds.");
            errorSubmitReview.setVisible(true);
            errorPause.play();
            return;
        }

        if (databaseManager.checkIfUserAlreadyReviewed(userLoginName, courseDepartment, courseNumber)) {
            initializeErrorPause();
            errorSubmitReview.setText("You have already submitted a review for this course.");
            errorSubmitReview.setVisible(true);
            errorPause.play();
            return;
        }

        if (!databaseManager.checkIfCourseInCoursesTable(courseDepartment, courseNumber)) {
            databaseManager.addCourse(courseDepartment, courseNumber);
        }

        String review = reviewText.getText().toString();
        int rating = Integer.parseInt(ratingNumber.getText());

        if (rating > 5 || rating < 1) {
            initializeErrorPause();
            errorSubmitReview.setText("Invalid rating (1-5). Please ensure rating is within bounds.");
            errorSubmitReview.setVisible(true);
            errorPause.play();
        }

        else {
            int studentID = databaseManager.getStudentID(userLoginName);
            int courseID = databaseManager.getCourseID(courseDepartment, courseNumber);
            databaseManager.addCourseReview(studentID, courseID, review, rating);

            initializeErrorPause();
            errorSubmitReview.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 15");
            errorSubmitReview.setText("Your review has been submitted!");
            errorSubmitReview.setVisible(true);
            errorPause.play();

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> {
                try {
                    app.changeScene("main-menu.fxml");
                } catch (IOException e) {
                    errorSubmitReview.setText("Error returning to Main Menu, please exit out of the program.");
                }
            });
            pause.play();
        }
    }

    private boolean isNumeric(String rate) {
        try {
            Integer.parseInt(rate);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
