package edu.virginia.cs.hw7.reviews;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    static Connection connection;
    static DatabaseManager databaseManager;

//    public static void main (String[] args) {
////        System.out.println("Starting");
//        DatabaseManager test = new DatabaseManager();
//        test.connect();
//        test.deleteTables();
//        test.createTables();
//    }

    public void initialize() {
        databaseManager = new DatabaseManager();
        File databaseFile = new File("Reviews.sqlite3");
        boolean fileExists = databaseFile.exists();
        if (!fileExists) {
            initializeNewDatabase();
        }
        else {
            databaseManager.connect();
        }
    }

    public void initializeNewDatabase() {
        try {
            File databaseFile = new File("Reviews.sqlite3");
            databaseFile.createNewFile();
            connect();
            createTables();
        } catch (IOException e) {
            throw new RuntimeException("Error making new database: ", e);
        }
    }
    public void connect() {
        if (connection != null) {
            throw new IllegalStateException("Already connected to database");
        }
        else {
            try {
                String databaseURL = "jdbc:sqlite:Reviews.sqlite3";
                connection = DriverManager.getConnection(databaseURL);
            } catch (SQLException e) {
                throw new RuntimeException("Error connecting: ", e);
            }
        }
    }

    public void createTables() {
        try {
            Statement statement = connection.createStatement();

            String createStudentsTable = "CREATE TABLE Students (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "login_name TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL)";
            statement.executeUpdate(createStudentsTable);

            String populateStudentsTable = "INSERT INTO Students (login_name, password) VALUES" +
                    "('aaa1aa', 'password1')," +
                    "('bbb2bb', 'password2')," +
                    "('ccc3cc', 'password3')";
            statement.executeUpdate(populateStudentsTable);

            String createCoursesTable = "CREATE TABLE Courses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "department TEXT NOT NULL," +
                    "catalog_number INTEGER NOT NULL)";
            statement.executeUpdate(createCoursesTable);

            String populateCoursesTable = "INSERT INTO Courses (department, catalog_number) VALUES" +
                    "('CS', '3140')," +
                    "('CS', '3100')," +
                    "('MATH', '3000')," +
                    "('BIOL', '2100')";
            statement.executeUpdate(populateCoursesTable);

            String createReviewsTable = "CREATE TABLE Reviews (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "student_id INTEGER NOT NULL," +
                    "course_id INTEGER NOT NULL," +
                    "review TEXT NOT NULL," +
                    "rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5)," +
                    "FOREIGN KEY (student_id) REFERENCES Students(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (course_id) REFERENCES Courses(id) ON DELETE CASCADE)";
            statement.executeUpdate(createReviewsTable);

            String populateReviewsTable = "INSERT INTO Reviews (student_id, course_id, review, rating) VALUES" +
                    "(1, 1, 'Great course, I love Richie!', 5)," +
                    "(2, 1, 'Terrible course, good professor though', 2)," +
                    "(3, 2, 'Interesting content, love Goatnelle', 4)," +
                    "(1, 3, 'Too much work, topic very hard', 1)," +
                    "(2, 3, 'Best class ever!!', 5)";
            statement.executeUpdate(populateReviewsTable);

            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error creating tables: ", e);
        }
    }

    public void deleteTables() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE Courses");
            statement.executeUpdate("DROP TABLE Reviews");
            statement.executeUpdate("DROP TABLE Students");
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error deleting tables: ", e);
        }
    }

    public boolean checkValidLogin(String loginName, String password) {
        try {
            String checkInfo = "SELECT * FROM Students WHERE login_name = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(checkInfo);
            statement.setString(1, loginName);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            boolean validLogin = resultSet.next();
            resultSet.close();
            statement.close();
            return validLogin;
        } catch (SQLException e) {
            throw new IllegalStateException("Error checking login information: ", e);
        }
    }

    public boolean checkExistingUser(String loginName) {
        try {
            String checkUser = "SELECT * FROM Students WHERE login_name = ?";
            PreparedStatement statement = connection.prepareStatement(checkUser);
            statement.setString(1, loginName);
            ResultSet resultSet = statement.executeQuery();
            boolean existingUser = resultSet.next();
            resultSet.close();
            statement.close();
            return existingUser;
        } catch (SQLException e) {
            throw new IllegalStateException("Error checking existing user: ", e);
        }
    }

    public void addUser(String loginName, String password) {
        try {
            String user = "INSERT INTO Students (login_name, password) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(user);
            statement.setString(1, loginName);
            statement.setString(2, password);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error adding user: ", e);
        }
    }

    public boolean checkIfCourseReviewExist(String courseDepartment, int courseNumber) {
        try {
            String getReview = "SELECT review, rating FROM Reviews INNER JOIN Courses ON Reviews.course_id = Courses.id " +
                    "WHERE Courses.department = ? AND Courses.catalog_number = ?";
            PreparedStatement statement = connection.prepareStatement(getReview);
            statement.setString(1, courseDepartment);
            statement.setInt(2, courseNumber);
            ResultSet resultSet = statement.executeQuery();
            boolean reviewExist = resultSet.next();
            resultSet.close();
            statement.close();
            return reviewExist;
        } catch (SQLException e) {
            throw new IllegalStateException("Error checking if course review exists: ", e);
        }
    }

    public List<String> getCourseReviews(String courseDepartment, int courseNumber) {
        try {
            String getReview = "SELECT review, rating FROM Reviews INNER JOIN Courses ON Reviews.course_id = Courses.id " +
                    "WHERE Courses.department = ? AND Courses.catalog_number = ?";
            PreparedStatement statement = connection.prepareStatement(getReview);
            statement.setString(1, courseDepartment);
            statement.setInt(2, courseNumber);
            ResultSet resultSet = statement.executeQuery();
            List<String> reviewsList = new ArrayList<>();
            while (resultSet.next()) {
                String review = resultSet.getString("review");
                reviewsList.add(review);
            }
            resultSet.close();
            statement.close();
            return reviewsList;
        } catch (SQLException e) {
            throw new IllegalStateException("Error getting course reviews: ", e);
        }
    }

    public double getCourseAverageRating(String courseDepartment, int courseNumber) {
        try {
            String getReview = "SELECT review, rating FROM Reviews INNER JOIN Courses ON Reviews.course_id = Courses.id " +
                    "WHERE Courses.department = ? AND Courses.catalog_number = ?";
            PreparedStatement statement = connection.prepareStatement(getReview);
            statement.setString(1, courseDepartment);
            statement.setInt(2, courseNumber);
            ResultSet resultSet = statement.executeQuery();
            int count = 0;
            int totalRating = 0;
            while (resultSet.next()) {
                int rating = resultSet.getInt("rating");
                totalRating += rating;
                count++;
            }
            double averageRating = (double) totalRating / count;
            resultSet.close();
            statement.close();
            return averageRating;
        } catch (SQLException e) {
            throw new IllegalStateException("Error getting course average rating: ", e);
        }
    }

    public boolean checkIfUserAlreadyReviewed(String loginName, String courseDepartment, int courseNumber) {
        try {
            String checkReview = "SELECT * FROM Reviews " +
                    "JOIN Students ON Reviews.student_id = Students.id " +
                    "JOIN Courses ON Reviews.course_id = Courses.id " +
                    "WHERE Students.login_name = ? AND Courses.department = ? AND Courses.catalog_number = ?";
            PreparedStatement statement = connection.prepareStatement(checkReview);
            statement.setString(1, loginName);
            statement.setString(2, courseDepartment);
            statement.setInt(3, courseNumber);
            ResultSet resultSet = statement.executeQuery();
            boolean reviewExist = resultSet.next();
            resultSet.close();
            statement.close();
            return reviewExist;
        } catch (SQLException e) {
            throw new IllegalStateException("Error checking if user already submitted a review: ", e);
        }
    }

    public boolean checkIfCourseInCoursesTable(String courseDepartment, int courseNumber) {
        try {
            String checkCourse = "SELECT * FROM Courses WHERE Courses.department = ? AND Courses.catalog_number = ?";
            PreparedStatement statement = connection.prepareStatement(checkCourse);
            statement.setString(1, courseDepartment);
            statement.setInt(2, courseNumber);
            ResultSet resultSet = statement.executeQuery();
            boolean reviewExist = resultSet.next();
            resultSet.close();
            statement.close();
            return reviewExist;
        } catch (SQLException e) {
            throw new IllegalStateException("Error checking if course is in courses table: ", e);
        }
    }

    public void addCourse(String courseDepartment, int courseNumber) {
        try {
            String course = "INSERT INTO Courses (department, catalog_number) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(course);
            statement.setString(1, courseDepartment);
            statement.setInt(2, courseNumber);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error adding course: ", e);
        }
    }

    public int getCourseID(String courseDepartment, int courseNumber) {
        try {
            String getCourse = "SELECT id FROM Courses WHERE Courses.department = ? AND Courses.catalog_number = ?";
            PreparedStatement statement = connection.prepareStatement(getCourse);
            statement.setString(1, courseDepartment);
            statement.setInt(2, courseNumber);
            ResultSet resultSet = statement.executeQuery();
            int courseID = resultSet.getInt("id");
            resultSet.close();
            statement.close();
            return courseID;
        } catch (SQLException e) {
            throw new IllegalStateException("Error getting course id: ", e);
        }
    }

    public int getStudentID(String loginName) {
        try {
            String getStudent = "SELECT id FROM Students WHERE Students.login_name = ?";
            PreparedStatement statement = connection.prepareStatement(getStudent);
            statement.setString(1, loginName);
            ResultSet resultSet = statement.executeQuery();
            int studentID = resultSet.getInt("id");
            resultSet.close();
            statement.close();
            return studentID;
        } catch (SQLException e) {
            throw new IllegalStateException("Error getting student id: ", e);
        }
    }

    public void addCourseReview(int studentID, int courseID, String review, int rating) {
        try {
            String addReview = "INSERT INTO Reviews (student_id, course_id, review, rating) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(addReview);
            statement.setInt(1, studentID);
            statement.setInt(2, courseID);
            statement.setString(3, review);
            statement.setInt(4, rating);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error adding review: ", e);
        }
    }



}