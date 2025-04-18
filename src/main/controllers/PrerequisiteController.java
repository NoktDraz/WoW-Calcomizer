package main.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.model.*;

import java.util.HashSet;

public class PrerequisiteController implements FXMLController {
    private Talent currentTalent;
    private TalentTree currentTalentTree;
    private ImageView currentIcon;
    private HashSet<Integer> occupiedGridSlots; // Contains the grid position indexes of talent and arrow nodes

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
        this.occupiedGridSlots = new HashSet<>();
        this.tree.setFont(CustomFont.ITEM_NAME);
        this.hBox.setBackground(talentTree.getBackground());
        this.close.setCursor(CustomCursor.DEFAULT);

        this.grid.getChildren().clear();

        this.populateTalentTreeGrid();
        this.findValidPrerequisiteOptions();
    }

    private void findValidPrerequisiteOptions() {
        int talentIndex = this.currentTalent.getIndex();
        boolean isUpLocked = false, isLeftLocked = false, isLeftBlocked = false, isRightLocked = false, isRightBlocked = false;

        // Is talent in the first row
        if (talentIndex < 4) {
            isUpLocked = true;
        }
        // Is talent on the left edge
        if (talentIndex % 4 == 0) {
            isLeftBlocked = true;
            isLeftLocked = true;
        }
        // Is talent on the right edge
        else if ((talentIndex + 1) % 4 == 0) {
            isRightBlocked = true;
            isRightLocked = true;
        }

        // Check if the slot to the left and/or right of the slot at the descendingSlotIndex is occupied
        // Is the slot occupied by a talent?  Skip it, if it is a dependant of the current talent
        // Adjust the slot to be a valid selection option
        // Lock the slot direction (avoids unreachable talents being tagged as valid options)
        // Shift a row upward.  Check the slot at the descendingSlotIndex
        // If the current slot is not occupied, unlock slot directions to both sides (unless blocked by an edge)
        // Repeat until all sides are locked
        int descendingSlotIndex = talentIndex;
        do {
            if (isLeftLocked != true) {
                if (this.occupiedGridSlots.contains(descendingSlotIndex - 1)) {
                    if (this.currentTalentTree.getTalentByIndex(descendingSlotIndex - 1) != null) {
                        if (this.currentTalent.hasDependants() != true ||
                                this.currentTalent.getDependantIndexes().contains(descendingSlotIndex - 1) != true) {
                                    this.setAsValidPrerequisiteOption(this.currentTalentTree.getTalentByIndex(descendingSlotIndex - 1));
                        }
                    }

                    isLeftLocked = true;
                }
            }
            if (isRightLocked != true) {
                if (this.occupiedGridSlots.contains(descendingSlotIndex + 1)) {
                    if (this.currentTalentTree.getTalentByIndex(descendingSlotIndex + 1) != null) {
                        if (this.currentTalent.hasDependants() != true ||
                                this.currentTalent.getDependantIndexes().contains(descendingSlotIndex + 1) != true) {
                                    this.setAsValidPrerequisiteOption(this.currentTalentTree.getTalentByIndex(descendingSlotIndex + 1));
                        }
                    }

                    isRightLocked = true;
                }
            }

            descendingSlotIndex -= Constant.TALENTGRID_ROW_STEP;
            if (descendingSlotIndex < 0) {
                isUpLocked = true;
                isLeftLocked = true;
                isRightLocked = true;
            }

            if (isUpLocked != true) {
                if (this.occupiedGridSlots.contains(descendingSlotIndex)) {
                    if (this.currentTalentTree.getTalentByIndex(descendingSlotIndex) != null)
                        this.setAsValidPrerequisiteOption(this.currentTalentTree.getTalentByIndex(descendingSlotIndex));

                    isUpLocked = true;
                }

                if (isLeftBlocked != true && isUpLocked != true)
                    isLeftLocked = false;
                if (isRightBlocked != true && isUpLocked != true)
                    isRightLocked = false;
            }
        }
        while ((isUpLocked && isLeftLocked && isRightLocked) == false);
    }

    private void populateTalentTreeGrid() {
        this.currentTalentTree.getTalents().values().forEach(talent -> {
            if (talent.getColumnPosition() == this.currentTalent.getColumnPosition() &&
                    talent.getRowPosition() == this.currentTalent.getRowPosition()) return;

            ImageView talentNode = UtilityFunction.clone.ImageView(talent.getImageView());
            talentNode.setEffect(CustomBorder.LOCKED);
            talentNode.setCursor(null);

            this.grid.add(talentNode, talent.getColumnPosition(), talent.getRowPosition());
            this.occupiedGridSlots.add(talent.getIndex());
        });
        this.currentTalentTree.getArrows().forEach(arrow ->
                arrow.getParts().forEach(part -> {
                    ImageView arrowNode = UtilityFunction.clone.ImageView(part.getNode());

                    if (arrow.getIndex() != currentTalent.getIndex()) {
                        arrowNode.setEffect(CustomEffect.DESATURATE_FULL);
                        // Arrows for the current prerequisite aren't counted as occupying a slot
                        this.occupiedGridSlots.add(part.getPositionIndex());
                    }

                    this.grid.add(arrowNode, part.getColumnPosition(), part.getRowPosition());
                })
        );

        // Emphasize the current talent being customized
        ImageView talentNode = UtilityFunction.clone.ImageView(this.currentIcon);
        talentNode.setScaleX(1.2);
        talentNode.setScaleY(1.2);
        talentNode.setEffect(CustomBorder.MAXED);
        talentNode.setCursor(null);

        this.grid.add(talentNode, this.currentTalent.getColumnPosition(), this.currentTalent.getRowPosition());
    }

    public void setAsValidPrerequisiteOption(Talent talent) {
        ImageView talentNode = UtilityFunction.clone.ImageView(talent.getImageView());

        talentNode.setEffect(CustomBorder.OPEN);
        talentNode.setOnMouseEntered(event -> talentNode.setEffect(UtilityFunction.Effects.constructEffectChain(CustomBorder.OPEN, CustomEffect.VALID)));
        talentNode.setOnMouseExited(event -> talentNode.setEffect(CustomBorder.OPEN));
        talentNode.setOnMouseClicked(event -> Event.fireEvent(this.hBox, new CustomEvent(CustomEvent.FINISH_PREREQUISITE, talent.getIndex())));

        this.grid.add(talentNode, talent.getColumnPosition(), talent.getRowPosition());
    }
}
