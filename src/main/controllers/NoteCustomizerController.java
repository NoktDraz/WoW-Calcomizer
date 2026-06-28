package main.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import main.enums.Window;
import main.model.*;

public class NoteCustomizerController extends ItemCustomizerBase {
    private Note currentNote;

    public NoteCustomizerController() {}
    //region FXML Injection
    @FXML
    private TextField name;
    @FXML
    private TextArea description;
    //endregion
    @FXML
    void initialize() {
        super.initialize();

        this.name.setEffect(CustomEffect.SHADOW_ITEM_CUSTOMIZATION_NAME);
        this.description.setFont(CustomFont.ITEM_TEXT);
        this.description.setEffect(CustomEffect.SHADOW_ITEM_CUSTOMIZATION_DESCRIPTION);
    }
    @FXML
    public void finish() {
        if (super.validateFinish(this.description)) {
            this.currentNote.setDescription(this.description.getText());

            super.finish();
        } else {
            Event.fireEvent(this.anchor, new CustomEvent(CustomEvent.SHOW_ALERT, this.errorMessage, Alert.AlertType.ERROR, Window.NOTE_CUSTOMIZER));
        }
    }

    public void setNoteObjects(NoteCollection noteCollection, Note note) {
        super.reset();
        this.description.setText("");

        this.currentNote = note;
        this.currentItemContainer = noteCollection;
        this.setItem(this.currentItemContainer, this.currentNote);

        this.description.setText(note.getDescription());
    }
}
