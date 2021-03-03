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
    public static void showDeveloperInfo() {
        JOptionPane.showMessageDialog(null,
                "<html>Developer: Dmitriy Fomin<br>GitHub: https://github.com/FominDV <br> Email: 79067773397@yandex.ru<br>*All rights reserved*</html>",
                "Developer info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showAndHideStages(String pathOfFXML, Labeled labeled) {
        Platform.runLater(()->labeled.getScene().getWindow().hide());
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
        Stage stage=getStage(pathOfFXML);
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

}
