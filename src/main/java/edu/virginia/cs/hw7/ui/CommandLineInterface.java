package edu.virginia.cs.hw7.ui;

import edu.virginia.cs.hw7.reviews.DatabaseManager;

import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    static DatabaseManager databaseManager;
    private Scanner scanner;
    private String loginName;

    public static void main(String[] args) {
        CommandLineInterface play = new CommandLineInterface();
        databaseManager = new DatabaseManager();
        databaseManager.initialize();
        play.run();
    }

    public void run() {
        loginMenu();
    }

    private void initializeScanner() {
        scanner = new Scanner(System.in);
    }

    private void loginMenu() {
        System.out.println("Welcome to the Course Review Application! Please enter the number associated with what you would like to do: \n" +
                "(1) Login-to existing user\n" +
                "(2) Create a new user\n" +
                "(3) Quit ");
        initializeScanner();
        int option = scanner.nextInt();
        switch(option) {
            case 1:
                loginExistingUser();
                break;
            case 2:
                createNewUser();
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again. \n");
                loginMenu();
        }
    }

    private void loginExistingUser() {
        System.out.println("\nPlease enter your username and password.");
        initializeScanner();
        System.out.print("Username: ");
        String username = scanner.next();
        System.out.print("Password: ");
        String password = scanner.next();
        boolean validLogin = databaseManager.checkValidLogin(username, password);
        if (validLogin) {
            this.loginName = username;
            System.out.println("You have successfully logged in!\n");
            mainMenu();
        }
        else {
            System.out.println("Invalid username or password. Please try again.\n");
            loginMenu();
        }

    }

    private void createNewUser() {
        System.out.println("\nPlease enter a username and password.");
        initializeScanner();
        System.out.print("Username: ");
        String username = scanner.next();
        System.out.print("Password: ");
        String password = scanner.next();
        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.next();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.\n");
            loginMenu();
        }

        boolean existingUser = databaseManager.checkExistingUser(username);
        if (existingUser) {
            System.out.println("Username already exists. Please try again with a different username.\n");
            loginMenu();
        }

        databaseManager.addUser(username, password);
        this.loginName = username;
        System.out.println("You have successfully logged in!\n");
        mainMenu();
    }

    public void mainMenu() {
        System.out.println("Welcome to the Main Menu! Please enter the number associated with what you would like to do: \n" +
                "(1) Submit a review for a course\n" +
                "(2) See reviews for course\n" +
                "(3) Log-out ");
        initializeScanner();
        int option = scanner.nextInt();
        switch (option) {
            case 1:
                submitReviewForCourse();
                break;
            case 2:
                seeReviewsForCourse();
                break;
            case 3:
                System.out.println("You have been logged out. \n");
                loginMenu();
                break;
            default:
                System.out.println("Invalid choice. Please try again. \n");
                mainMenu();
        }
    }

    public void submitReviewForCourse() {
        System.out.print("\nEnter the course name (e.g. CS 3140): ");
        initializeScanner();

        // How to split input string by whitespace:
        // https://javarevisited.blogspot.com/2016/10/how-to-split-string-in-java-by-whitespace-or-tabs.html#axzz80btoVTN8
        String course = scanner.nextLine();
        String[] splitCourse = course.split(" ");
        if (splitCourse.length != 2 || splitCourse[0].length() > 4 || splitCourse[1].length() != 4) {
            System.out.println("Invalid course. Please check the course name and try again.\n");
            mainMenu();
        }
        String courseDepartment = splitCourse[0].toUpperCase();
        int courseNumber = Integer.parseInt(splitCourse[1]);

        if (databaseManager.checkIfUserAlreadyReviewed(loginName, courseDepartment, courseNumber)) {
            System.out.println("You have already submitted a review for this course.\n");
            mainMenu();
        }

        if (!databaseManager.checkIfCourseInCoursesTable(courseDepartment, courseNumber)) {
            databaseManager.addCourse(courseDepartment, courseNumber);
        }

        System.out.print("Enter your review: ");
        String review = scanner.nextLine();
        System.out.print("Enter your rating (1-5): ");
        int rating = scanner.nextInt();
        while (rating > 5 || rating < 1) {
            System.out.print("Enter your rating (1-5): ");
            rating = scanner.nextInt();
        }

        int studentID = databaseManager.getStudentID(loginName);
        int courseID = databaseManager.getCourseID(courseDepartment, courseNumber);
        databaseManager.addCourseReview(studentID, courseID, review, rating);
        System.out.println("Your review has been submitted.\n");
        mainMenu();
    }

    public void seeReviewsForCourse() {
        System.out.print("\nEnter the course name (e.g. CS 3140): ");
        initializeScanner();

        String course = scanner.nextLine();
        String[] splitCourse = course.split(" ");
        if (splitCourse.length != 2 || splitCourse[0].length() > 4 || splitCourse[1].length() != 4) {
            System.out.println("Invalid course. Please check the course name and try again.\n");
            mainMenu();
        }
        String courseDepartment = splitCourse[0].toUpperCase();
        int courseNumber = Integer.parseInt(splitCourse[1]);

        if (!databaseManager.checkIfCourseReviewExist(courseDepartment, courseNumber)) {
            System.out.println("No reviews for this course.\n");
            mainMenu();
        }

        List<String> reviewsList = databaseManager.getCourseReviews(courseDepartment, courseNumber);
        for (int i = 0; i < reviewsList.size(); i++) {
            System.out.println(reviewsList.get(i));
        }
        double averageRating = databaseManager.getCourseAverageRating(courseDepartment, courseNumber);
        System.out.println("Course Average Rating: " + averageRating + "/5\n");

        mainMenu();
    }

}
