package main.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.model.*;

import java.util.ArrayList;

public class PrerequisiteController implements FXMLController {
    private Talent currentTalent;
    private TalentTree currentTalentTree;
    private ImageView currentIcon;
    private ArrayList<Integer> validPrerequisiteIndexes;

    public PrerequisiteController() {}
    @FXML
    HBox hBox;
    @FXML
    TitledPane tree;
    @FXML
    ImageView close;
    @FXML
    GridPane grid;

    @FXML
    void initialize() {
        this.tree.setOnMousePressed(event -> {
            this.tree.setOnMouseDragged(dragEvent -> {
                this.tree.getScene().getWindow().setX(dragEvent.getScreenX() - event.getSceneX());
                this.tree.getScene().getWindow().setY(dragEvent.getScreenY() - event.getSceneY());
            });
        });

        this.close.setOnMouseEntered(event -> this.close.setEffect(CustomEffect.HIGHLIGHT));
        this.close.setOnMouseExited(event -> this.close.setEffect(null));
    }

    @FXML
    void closeWindow() {
        ((Stage) this.tree.getScene().getWindow()).close();
    }

    public void setTalentObjects(TalentTree talentTree, Talent talent, ImageView currentIcon) {
        this.currentTalent = talent;
        this.currentTalentTree = talentTree;
        this.currentIcon = currentIcon;
        this.validPrerequisiteIndexes = new ArrayList<>();
        this.tree.setFont(CustomFont.ITEM_NAME);
        this.hBox.setBackground(talentTree.getBackground());

        this.grid.getChildren().clear();

        this.findValidPrerequisiteIndexes();
        this.populateTalentTreeGrid();
    }

    private void findValidPrerequisiteIndexes() {
        int talentIndex = currentTalent.getIndex();
        boolean isUpBlocked = false, isLeftBlocked = false, isRightBlocked = false;

        // Is talent in the first row
        if (talentIndex < 4) {
            isUpBlocked = true;
        }
        // Is talent on the left edge
        if (talentIndex % 4 == 0) {
            isLeftBlocked = true;
        } // Is talent on the right edge
        else if ((talentIndex + 1) % 4 == 0) {
            isRightBlocked = true;
        }

        int descendingTalentIndex = talentIndex;
        do {
            if (isLeftBlocked) {
                this.validPrerequisiteIndexes.add(descendingTalentIndex + 1);
            } else if (isRightBlocked) {
                this.validPrerequisiteIndexes.add(descendingTalentIndex - 1);
            } else {
                this.validPrerequisiteIndexes.add(descendingTalentIndex + 1);
                this.validPrerequisiteIndexes.add(descendingTalentIndex - 1);
            }

            descendingTalentIndex -= 4;
            if (descendingTalentIndex < 0) {
                isUpBlocked = true;
            }
            if (this.currentTalentTree.getTalentByIndex(descendingTalentIndex) != null) {
                this.validPrerequisiteIndexes.add(descendingTalentIndex);

                isUpBlocked = true;
            }
        }
        while (isUpBlocked == false);
    }

    private void populateTalentTreeGrid() {
        this.currentTalentTree.getTalents().values().forEach(talent -> {
            if (talent.getColumnPosition() == this.currentTalent.getColumnPosition() &&
                    talent.getRowPosition() == this.currentTalent.getRowPosition()) return;

            ImageView imageView = UtilityFunction.clone.ImageView(talent.getImageView());

            if (this.validPrerequisiteIndexes.contains(talent.getIndex())) {
                imageView.setEffect(CustomBorder.OPEN);
                imageView.setOnMouseEntered(event -> imageView.setEffect(UtilityFunction.Effects.constructEffectChain(CustomBorder.OPEN, CustomEffect.VALID)));
                imageView.setOnMouseExited(event -> imageView.setEffect(CustomBorder.OPEN));
                imageView.setOnMouseClicked(event -> Event.fireEvent(this.hBox, new CustomEvent(CustomEvent.FINISH_PREREQUISITE, talent.getIndex())));
            } else {
                imageView.setEffect(CustomBorder.LOCKED);
                imageView.setCursor(null);
            }

            this.grid.add(imageView, talent.getColumnPosition(), talent.getRowPosition());
        });
        this.currentTalentTree.getArrows().forEach(arrow ->
                arrow.getParts().forEach(part -> {
                    ImageView arrowNode = UtilityFunction.clone.ImageView(part.getNode());

                    if (arrow.getIndex() != currentTalent.getIndex()) {
                        arrowNode.setEffect(CustomEffect.DESATURATE_FULL);
                    }

                    this.grid.add(arrowNode, part.getColumnPosition(), part.getRowPosition());
                })
        );

        ImageView imageView = UtilityFunction.clone.ImageView(this.currentIcon);
        imageView.setScaleX(1.2);
        imageView.setScaleY(1.2);
        imageView.setEffect(CustomBorder.MAXED);
        imageView.setCursor(null);

        this.grid.add(imageView, this.currentTalent.getColumnPosition(), this.currentTalent.getRowPosition());
    }

}
