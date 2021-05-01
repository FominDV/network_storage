package ru.fomin.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

/**
 * Utility class for base helpful operations.
 */
public class ControllersUtil {

    /*The password should be at least 8 characters long,
     contain at least 1 large letter,
     one small letter
     and contain special character OR digit*/
    private static final String PASSWORD_PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%]).{8,}";
    private static final String DEVELOPER_INFO = "<html>Developer: Dmitriy Fomin<br>GitHub: https://github.com/FominDV <br> Email: 79067773397@yandex.ru<br>*All rights reserved*</html>";
    private static final String PASSWORD_RULE = "The password should be at least 8 characters long,\n" +
            " contain at least 1 large letter,\n" +
            " one small letter\n" +
            " and contain special character OR digit.";

    /**
     * Shows pop-up window with information about developer.
     */
    public static void showDeveloperInfo() {
        JOptionPane.showMessageDialog(null,
                DEVELOPER_INFO,
                "Developer info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows pop-up window with question about overriding file when this name of file already exist.
     */
    public static boolean isConfirmOverrideFile(String fileName) {
        return isConfirm(
                String.format("<html>File with the name \"%s\" already exist.<br>Do you want override this file?</html>", fileName),
                "FileExistException");
    }

    /**
     * Shows pop-up window for confirming removing directory.
     */
    public static boolean isConfirmDeleteDirectory() {
        return isConfirm(
                "Are you sure you want to remove directory with all entity?",
                "Warning");
    }

    /**
     * Shows universal pop-up window for confirming.
     *
     * @param message - question to user
     * @param title   - header of pop-up window
     */
    private static boolean isConfirm(String message, String title) {
        int response = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        return response == 0 ? true : false;
    }

    /**
     * Universal method hiding window and showing other window.
     *
     * @param pathOfFXML - path to fxml of showing window
     * @param labeled    - Labeled of window that will be hide
     */
    public static void showAndHideStages(String pathOfFXML, Labeled labeled) {
        hideWindow(labeled);
        showStage(pathOfFXML);
    }

    /**
     * Hides window by it's Labeled.
     */
    public static void hideWindow(Labeled labeled) {
        Platform.runLater(() -> labeled.getScene().getWindow().hide());
    }

    /**
     * Shows window by it's path to fxml.
     */
    public static void showStage(String pathOfFXML) {
        Platform.runLater(() -> {
            Stage stage = getStage(pathOfFXML);
            stage.setResizable(false);
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
            stage.show();
        });
    }

    /**
     * Shows pop-up window with message of error.
     *
     * @param message - message of error
     */
    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows pop-up window with simple message.
     *
     * @param message - message for pop-up window
     */
    public static void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Returns stage of window by it's path to fxml.
     */
    public static Stage getStage(String pathOfFXML) {
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(ControllersUtil.class.getResource(pathOfFXML));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        return stage;
    }

    /**
     * Verifies string by length and existence.
     */
    public static boolean hasText(String str) {
        return str != null && str.length() != 0 ? true : false;
    }

    /**
     * Verifies login and password for registration.
     */
    public static boolean validation(String login, String password, String repeated_password) {

        //Verify empty field
        if (!verifyEmptyField(login, password, repeated_password)) {
            return false;
        }

        //Verify length of login
        int loginLends = login.length();
        if (loginLends < 3 || loginLends > 20) {
            showErrorMessage("Length of login should be greater than 2 and less than 21");
            return false;
        }

        //verifies password
        return verifyPassword(password, repeated_password);
    }

    /**
     * Verifies fields and show pop-up window when any field has not text.
     *
     * @param fields - fields for verifying
     * @return - true if all field have test.
     */
    public static boolean verifyEmptyField(String... fields) {
        for (String field : fields) {
            if (!hasText(field)) {
                showErrorMessage("All field should be fill");
                return false;
            }
        }
        return true;
    }

    /**
     * Verifies password for registration or changing password.
     */
    public static boolean verifyPassword(String password, String repeated_password) {
        //Verify password entity
        if (!password.matches(PASSWORD_PATTERN)) {
            showErrorMessage(PASSWORD_RULE);
            return false;
        }

        //Verify password comparison
        if (!password.equals(repeated_password)) {
            showErrorMessage("The passwords don't match");
            return false;
        }

        return true;
    }

    /**
     * Shows pop-up window with message of connection error.
     */
    public static void showConnectionError() {
        showErrorMessage("Unable to connect to the server");
    }
}