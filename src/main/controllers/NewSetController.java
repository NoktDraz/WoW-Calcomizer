package main.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.enums.Window;
import main.model.CustomCursor;
import main.model.CustomEvent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class NewSetController implements FXMLController {
    private String newSetName;
    private String alertMessage;

    @FXML
    AnchorPane anchor;
    @FXML
    TextField setName;
    @FXML
    CheckBox currentAsBase;
    @FXML
    Button create;
    @FXML
    Button cancel;

    public NewSetController() {}

    @FXML
    void initialize() {
        this.currentAsBase.setCursor(CustomCursor.INTERACT);
        this.create.setCursor(CustomCursor.INTERACT);
        this.cancel.setCursor(CustomCursor.INTERACT);
    }

    @FXML
    void createNewSet() throws URISyntaxException, IOException {
        this.newSetName = this.setName.getText();

        if (this.isNewSetValid()) {
            Event.fireEvent(this.anchor, new CustomEvent(CustomEvent.CREATE_CUSTOM_SET, this.newSetName, this.currentAsBase.isSelected()));

            this.closeWindow();
        } else {
            Event.fireEvent(this.anchor, new CustomEvent(CustomEvent.SHOW_ALERT, this.alertMessage, Alert.AlertType.ERROR, Window.NEW_SET));
        }
    }
    @FXML
    void closeWindow() {
        ((Stage) this.anchor.getScene().getWindow()).close();
    }

    public void setCheckBoxState(boolean disableCheckBox) {
        this.currentAsBase.setDisable(disableCheckBox);

        this.currentAsBase.setSelected(this.currentAsBase.isDisabled());
    }

    private boolean isNewSetValid() throws URISyntaxException, IOException {
        if (this.newSetName.isBlank() != true) {
            if (this.newSetName.equalsIgnoreCase("Default Vanilla Talents") != true &&
                Arrays.stream(UtilityFunction.Resources.getCustomDataSetsFolder().listFiles()).noneMatch(
                file -> file.getName().equalsIgnoreCase(this.newSetName)
                )
            )
                return true;
            else
                this.alertMessage = "Set with this name already exists";
        } else
            this.alertMessage = "Name field can't be empty";

        return false;
    }
}
