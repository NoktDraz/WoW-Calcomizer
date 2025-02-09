package main.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.enums.ItemState;
import main.enums.Resource;
import main.model.*;

abstract class ItemCustomizerBase implements FXMLController {
    ItemContainer currentItemContainer;
    GridItem currentItem;
    String errorMessage;

    @FXML
    AnchorPane anchor;
    @FXML
    Button finish;
    @FXML
    ImageView finishGlyph;
    @FXML
    Button cancel;
    @FXML
    ImageView cancelGlyph;
    @FXML
    Button delete;
    @FXML
    TextField name;
    @FXML
    ImageView icon;

    void initialize() {
        this.name.setFont(CustomFont.ITEM_NAME);
        this.finish.setCursor(CustomCursor.INTERACT);
        this.delete.setCursor(CustomCursor.INTERACT);
        this.delete.setEffect(CustomEffect.DESATURATE);
        this.cancel.setCursor(CustomCursor.INTERACT);
        this.finishGlyph.setImage(UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.GLYPH_FINISH));
        this.cancelGlyph.setImage(UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.GLYPH_CANCEL));
        this.finish.setOnMouseEntered(event -> this.finish.getGraphic().setVisible(true));
        this.delete.setOnMouseEntered(event -> {
            this.anchor.getParent().setEffect(CustomEffect.INVALID);
            this.delete.setEffect(null);
        });
        this.cancel.setOnMouseEntered(event -> this.cancel.getGraphic().setVisible(true));
        this.finish.setOnMouseExited(event -> this.finish.getGraphic().setVisible(false));
        this.delete.setOnMouseExited(event -> {
            this.anchor.getParent().setEffect(null);
            this.delete.setEffect(CustomEffect.DESATURATE);
        });
        this.cancel.setOnMouseExited(event -> this.cancel.getGraphic().setVisible(false));

        this.icon.setCursor(CustomCursor.INTERACT);
        this.icon.setOnMouseEntered(event -> {
            if (this.icon.getImage() == GridItem.ICON_CREATE_NEW) this.icon.setEffect(CustomEffect.HIGHLIGHT);
            else this.icon.setEffect(CustomBorder.OPEN_HIGHLIGHT);
        });
        this.icon.setOnMouseExited(event -> {
            if (this.icon.getImage() == GridItem.ICON_CREATE_NEW) this.icon.setEffect(null);
            else this.icon.setEffect(CustomBorder.OPEN);
        });
    }

    @FXML
    void finish() {
        this.currentItem.setIcon(this.icon.getImage());
        this.currentItem.setItemName(this.name.getText());
        this.currentItem.getImageView().setOpacity(1);
        this.currentItem.initTooltip();
        this.currentItemContainer.addItem(this.currentItem);

        this.closeWindow();

        Event.fireEvent(this.anchor, new CustomEvent(CustomEvent.UPDATE_CONTAINER_VIEW, this.currentItemContainer));
    }
    @FXML
    void delete() {
        this.currentItemContainer.getItems().remove(currentItem.getIndex());

        this.closeWindow();

        Event.fireEvent(this.anchor, new CustomEvent(CustomEvent.UPDATE_CONTAINER_VIEW, this.currentItemContainer));
    }
    @FXML
    void closeWindow() {
        ((Stage) this.anchor.getScene().getWindow()).close();
    }
    @FXML
    void chooseIcon() {
        Event.fireEvent(this.anchor, new CustomEvent(CustomEvent.BROWSE_ITEM_ICONS, this.currentItem));

        this.icon.setImage(UtilityFunction.Resources.getPublicIcon(this.currentItem.getIconName()));
    }

    void setItem(ItemContainer itemContainer, GridItem item) {
        this.currentItemContainer = itemContainer;
        this.currentItem = item;
        this.name.setText(item.getItemName());

        if (item.getState() == ItemState.EMPTY) {
            this.icon.setImage(GridItem.ICON_DEFAULT);
        } else {
            this.icon.setImage(item.getIconImage());
        }
        this.icon.setEffect(CustomBorder.OPEN);
    }

    boolean validateFinish(TextArea... descriptions) {
        if (this.name.getText().isBlank()) {
            this.errorMessage = "Item must have a name";

            return false;
        }

        for (TextArea textArea : descriptions) {
            if (textArea.getText() != null && textArea.getText().isBlank() == false) {
                return true;
            }
        }

        if (descriptions.length > 1) {
            this.errorMessage = "Talent must have a description for at least 1 rank";
        } else {
            this.errorMessage = "Note must have a description";
        }

        return false;
    }

    void reset() {
        this.name.setText("");
        this.icon.setImage(GridItem.ICON_DEFAULT);
    }
}
