package main.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.enums.Resource;
import main.enums.Window;

import java.io.IOException;
import java.util.HashMap;

public class WindowController {
    private static final Image APP_ICON = UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.APPLICATION_ICON);

    private Stage mainStage;
    private Stage modalStage;
    private Stage modalStageTransparent;
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
        this.mainStage.getIcons().add(this.APP_ICON);
        this.mainStage.setResizable(false);
    }
    public Stage getMainStage() { return this.mainStage; }
    public void showMainStage() { this.mainStage.show(); }
    public void setModalWindow(Window window) {
        if (this.modalStage.isShowing()) this.modalStage.close();
        if (this.modalStageTransparent.isShowing()) this.modalStageTransparent.close();

        this.modalStage.setScene(this.getScene(this.fxmlObjects.get(window)));
        this.modalStage.setTitle(window.getTitle());
    }
    public void setTransparentModalWindow(Window window) {
        if (this.modalStage.isShowing()) this.modalStage.close();
        if (this.modalStageTransparent.isShowing()) this.modalStageTransparent.close();

        this.modalStageTransparent.setScene(this.getScene(this.fxmlObjects.get(window)));
    }
    public void initModalStages(Stage modalStage, Stage modalStageTransparent) {
        this.modalStage = modalStage;
        this.modalStageTransparent = modalStageTransparent;

        this.modalStage.initOwner(this.mainStage);
        this.modalStageTransparent.initOwner(this.mainStage);
        this.modalStage.initModality(Modality.APPLICATION_MODAL);
        this.modalStageTransparent.initModality(Modality.APPLICATION_MODAL);
        this.modalStage.setResizable(false);
        this.modalStageTransparent.setResizable(false);
    }
    public Stage getModalStage() { return this.modalStage; }
    public Stage getTransparentModalStage() { return this.modalStageTransparent; }
    public void closeTransparentModal() {
        this.modalStageTransparent.close();
    }
    public void closeModal() {
        this.modalStage.close();
    }

    public void showModalWindow() {
        this.modalStage.show();
    }
    public void showTransparentModalWindow() {
        this.modalStageTransparent.show();
    }

    public void linkControllerToWindow(FXMLController fxmlController, Window window) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(window.getFXMLFilePath()));

        loader.setController(fxmlController);

        this.fxmlObjects.put(window, loader.load());
    }

    private Scene getScene(Parent fxmlObject) {
        if (fxmlObject.getScene() == null) {
            Scene scene = new Scene(fxmlObject);
            scene.setCursor(new ImageCursor(UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.CURSOR_DEFAULT)));

            return scene;
        }
        else return fxmlObject.getScene();
    }

    public Scene getScene(Window window) {
        return this.fxmlObjects.get(window).getScene();
    }

    public void setScene(Window window) {
        this.modalStage.setScene(this.fxmlObjects.get(window).getScene());
    }
}
