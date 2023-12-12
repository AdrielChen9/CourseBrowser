module CourseReviews.main {
    requires javafx.controls;
    requires javafx.fxml;

        requires org.controlsfx.controls;
    requires java.sql;

    exports edu.virginia.cs.hw7;
    opens edu.virginia.cs.hw7 to javafx.fxml;
}