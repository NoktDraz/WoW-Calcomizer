package main.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.enums.Resource;
import main.enums.Window;
import main.model.CustomCursor;

import java.io.IOException;
import java.util.HashMap;

public class WindowController {
    private Stage mainStage;
    private Stage modalPrimary;
    private Stage modalSecondary;
    private HashMap<Window, Parent> fxmlObjects;

    public WindowController() {
        this.fxmlObjects = new HashMap<>();
    }
    public void setMainWindow(Window window) {
        this.mainStage.setScene(this.getScene(this.fxmlObjects.get(window)));
        this.mainStage.setTitle(window.getTitle());
    }
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        this.mainStage.setResizable(false);
        this.mainStage.getIcons().add(UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.APPLICATION_ICON));
    }
    public void setPrimaryModalWindow(Window window) {
        if (this.modalPrimary.isShowing()) this.modalPrimary.close();
        if (this.modalSecondary.isShowing()) this.modalSecondary.close();

        this.modalPrimary.setScene(this.getScene(this.fxmlObjects.get(window)));
        this.modalPrimary.setTitle(window.getTitle());
    }
    public void setSecondaryModalWindow(Window window) {
        if (this.modalPrimary.isShowing()) this.modalPrimary.close();
        if (this.modalSecondary.isShowing()) this.modalSecondary.close();

        this.modalSecondary.setScene(this.getScene(this.fxmlObjects.get(window)));
    }
    public void initModalStages(Stage primaryModalStage, Stage secondaryModalStage) {
        this.modalPrimary = primaryModalStage;
        this.modalSecondary = secondaryModalStage;

        this.modalPrimary.initOwner(this.mainStage);
        this.modalSecondary.initOwner(this.mainStage);
        this.modalPrimary.initModality(Modality.APPLICATION_MODAL);
        this.modalSecondary.initModality(Modality.APPLICATION_MODAL);
        this.modalPrimary.setResizable(false);
        this.modalSecondary.setResizable(false);
    }

    public void linkControllerToWindow(FXMLController fxmlController, Window window) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(window.getFXMLFilePath()));

        loader.setController(fxmlController);

        this.fxmlObjects.put(window, loader.load());
    }

    private Scene getScene(Parent fxmlObject) {
        if (fxmlObject.getScene() == null) {
            Scene scene = new Scene(fxmlObject);
            scene.setCursor(CustomCursor.DEFAULT);

            return scene;
        }
        else return fxmlObject.getScene();
    }

    public Stage getMainStage() { return this.mainStage; }
    public Stage getPrimaryModalStage() { return this.modalPrimary; }
    public Stage getSecondaryModalStage() { return this.modalSecondary; }
    public void closeSecondaryModal() {
        this.modalSecondary.close();
    }
    public void closePrimaryModal() {
        this.modalPrimary.close();
    }
    public void showMain() { this.mainStage.show(); }
    public void showPrimaryModal() {
        this.modalPrimary.show();
    }
    public void showSecondaryModal() {
        this.modalSecondary.show();
    }
    public Scene getScene(Window window) {
        return this.fxmlObjects.get(window).getScene();
    }
    public void setScene(Window window) {
        this.modalPrimary.setScene(this.fxmlObjects.get(window).getScene());
    }
}
