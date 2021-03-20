package ru.fomin.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ru.fomin.core.HandlerCommands;
import ru.fomin.util.ControllersUtil;

public class ConnectionPropertiesController {

    private static final String IP_PATTERN = "(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])(\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])){3}";

    @FXML
    private Button btn_info;

    @FXML
    private Button btn_cancel;

    @FXML
    private TextField field_port;

    @FXML
    private Button btn_change;

    @FXML
    private TextField field_ip;

    @FXML
    private Label label_port;

    @FXML
    private Label label_ip;

    @FXML
    void initialize() {

        label_port.setText(String.valueOf(HandlerCommands.getPort()));
        label_ip.setText(HandlerCommands.getIp());
        field_ip.setText(HandlerCommands.getIp());
        field_port.setText(String.valueOf(HandlerCommands.getPort()));

        btn_info.setOnAction(event -> ControllersUtil.showDeveloperInfo());

        btn_cancel.setOnAction(event -> ControllersUtil.showAndHideStages("/fxml/authentication.fxml", btn_cancel));

        btn_change.setOnAction(event -> {
            String newIP = field_ip.getText();
            String newPort = field_port.getText();
            if (isValidConnectionProperties(newIP, newPort)) {
                HandlerCommands.setIp(newIP);
                HandlerCommands.setPort(Integer.parseInt(newPort));
                ControllersUtil.showInfoMessage(String.format("Successful\nIp: %s\nPort: %s", newIP, newPort));
                ControllersUtil.showAndHideStages("/fxml/authentication.fxml", btn_cancel);
            } else {
                field_ip.setText("");
                field_port.setText("");
            }
        });
    }

    private boolean isValidConnectionProperties(String newIP, String newPort) {
        if (!(ControllersUtil.hasText(newIP) || ControllersUtil.hasText(newPort))) {
            ControllersUtil.showErrorMessage("All field should be fill");
            return false;
        }
        int port = 0;
        try {
            port = Integer.parseInt(newPort);
        } catch (NumberFormatException e) {
            ControllersUtil.showErrorMessage("The port can only be made up of numbers");
            return false;
        }
        if (port < 0 || port > 65536) {
            ControllersUtil.showErrorMessage("The port should be less than 65536 and greater than zero");
            return false;
        }
        if (!newIP.matches(IP_PATTERN)) {
            ControllersUtil.showErrorMessage("Invalid IP: " + newIP);
            return false;
        }
        return true;
    }
}

