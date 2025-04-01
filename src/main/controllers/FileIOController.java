package main.controllers;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.enums.Resource;

import java.io.File;

public class FileIOController {
    private static final FileChooser.ExtensionFilter FILTER_IMAGES = new FileChooser.ExtensionFilter("Image Files (JPG,PNG,BMP)", "*.jpg", "*.png", "*.bmp");
    private static final File FOLDER_ICONS = new File(Resource.Folder.PUBLIC_ICONS.getPath());
    private static final String WINDOW_TITLE = "Choose Image File for Icon";

    private FileChooser fileInput;

    public FileIOController() {
        this.fileInput = new FileChooser();

        this.fileInput.setTitle(WINDOW_TITLE);
        this.fileInput.getExtensionFilters().add(FILTER_IMAGES);
        this.fileInput.setInitialDirectory(FOLDER_ICONS);

    }

    public File showOpenDialog(Stage stage) {
        return this.fileInput.showOpenDialog(stage.getScene().getWindow());
    }
}
