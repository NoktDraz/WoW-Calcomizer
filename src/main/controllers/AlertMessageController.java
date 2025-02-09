package main.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.enums.Resource;

public class AlertMessageController implements FXMLController {
    private static final Image ERROR_IMAGE = UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.ALERT_ERROR);
    private static final Image WARNING_IMAGE = UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.ALERT_WARNING);

    @FXML
    Pane main;
    @FXML
    Button ok;
    @FXML
    ImageView graphic;
    @FXML
    Label content;

    @FXML
    void closeWindow() {
        ((Stage) this.main.getScene().getWindow()).close();
    }

    public AlertMessageController() {}

    public void setAlert(String message, Alert.AlertType type) {
        this.content.setText(message);
    }
}
