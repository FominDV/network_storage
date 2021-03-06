package ru.fomin.client.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class ControllersUtil {

    private static final String PASSWORD_PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%]).{8,}";

    public static void showDeveloperInfo() {
        JOptionPane.showMessageDialog(null,
                "<html>Developer: Dmitriy Fomin<br>GitHub: https://github.com/FominDV <br> Email: 79067773397@yandex.ru<br>*All rights reserved*</html>",
                "Developer info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showAndHideStages(String pathOfFXML, Labeled labeled) {
        Platform.runLater(() -> labeled.getScene().getWindow().hide());
        Stage stage = getStage(pathOfFXML);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    static void showStage(String pathOfFXML) {
        Stage stage = getStage(pathOfFXML);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> event.consume());
        stage.show();
    }

    static void hideStage(String pathOfFXML) {
        getStage(pathOfFXML).hide();
    }

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

    public static void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    public static boolean hasText(String str) {
        return str != null && str.length() != 0 ? true : false;
    }

   public static boolean validation(String login, String password, String repeated_password) {
        //Verify empty field
        if (!(hasText(login) && hasText(password) && hasText(repeated_password))) {
            showErrorMessage("All field should be fill");
            return false;
        }

        //Verify length of login
        int loginLends = login.length();
        if (loginLends < 3 || loginLends > 20) {
            showErrorMessage("Length of login should be greater than 2 and less than 21");
            return false;
        }

        //Verify password entity
        if (!password.matches(PASSWORD_PATTERN)) {
            showErrorMessage("The password should be at least 8 characters long,\n contain at least 1 large letter,\n one small letter\n and contain special character OR digit.");
            return false;
        }

        //Verify password comparison
        if (!password.equals(repeated_password)) {
            showErrorMessage("The passwords don't match");
            return false;
        }
        return true;
    }
}
