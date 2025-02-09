package main.controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.transform.Transform;
import javafx.util.Pair;
import main.enums.CharacterClass;
import main.enums.ItemState;
import main.enums.Window;
import main.model.*;

import java.util.ArrayList;

public class CustomizerController extends CalcomizerBase {
    private static final DataFormat CUSTOM_DATAFORMAT = new DataFormat("GridItem Data");

    private ArrayList<ItemContainer> activeItemContainers;

    @FXML
    MenuItem saveAll;
    @FXML
    Button saveClass;

    public CustomizerController() {}

    public void initialize() {
        super.initialize();

        this.saveClass.setCursor(CustomCursor.INTERACT);
        this.focusedClassButton = this.warrior;
        this.toggleClassButtonFocus(0);
        activeClass = CharacterClass.WARRIOR;

        this.customizationSetMenu.getSelectionModel().selectFirst();
    }

    @FXML
    public void switchCalculator() {
        this.talentTreeGridPanes.forEach(gridPane -> gridPane.getChildren().retainAll(gridPane.getChildren().get(0)));

        Event.fireEvent(this.main, new CustomEvent(CustomEvent.SET_MAIN_WINDOW, Window.CALCULATOR));
    }
    @FXML
    public void saveAll() {
        Event.fireEvent(this.main, new CustomEvent(CustomEvent.SAVE_ALL));
    }
    @FXML
    public void saveClass() {
        Event.fireEvent(this.main, new CustomEvent(CustomEvent.SAVE_CLASS_DATA));
    }
    @FXML
    public void newCustomSet() {
        Event.fireEvent(this.main, new CustomEvent(CustomEvent.NEW_CUSTOM_SET));
    }
    public CustomizationSet getActiveCustomizationSet() {
        return activeCustomizationSet;
    }
    public ClassData getActiveClassData() {
        return classesData.get(activeClass);
    }

    public void activate() {
        super.activate();

        this.loadActiveSetData();
    }

    public void addNewCustomizationSet(CustomizationSet customSet) {
        customizationSets.add(customSet);

        this.customizationSetMenu.getItems().add(customSet.getName());
        this.customizationSetMenu.getSelectionModel().selectLast();
    }

    void setActiveCustomizationSet(int index) {
        super.setActiveCustomizationSet(index);

        this.saveAll.setDisable(index == 0);
        this.saveClass.setDisable(index == 0);
    }

    void loadActiveSetData() {
        super.loadActiveSetData();

        classesData.forEach((cls, classData) -> this.applyCustomizerSettings(classData));
        this.setActiveClass(activeClass);
    }

    void loadActiveClassData() {
        super.loadActiveClassData();

        this.activeItemContainers = classesData.get(activeClass).getItemContainers();
        this.updateView();
    }

    private void applyCustomizerSettings(ClassData classData) {
        classData.getItemContainers().forEach(this::applyCustomizerSettings);
    }

    private void applyCustomizerSettings(ItemContainer itemContainer) {
        if (itemContainer.getClass() == TalentTree.class) {
            TalentTree talentTree = (TalentTree) itemContainer;

            talentTree.getTalents().forEach((integer, talent) -> this.applyCustomizerSettings(talentTree, talent));
        } else {
            ((NoteCollection) itemContainer).getNotes().forEach(((integer, note) -> this.applyCustomizerSettings(note)));
        }
    }

    private void applyCustomizerSettings(TalentTree talentTree, Talent talent) {
        talent.setState(ItemState.MAXED);
        talent.setCurrentRank(talent.getRankData().size());
        talent.updateTooltip();

        this.applyItemNodeEvents(talentTree, talent);
        this.adjustRankLabel(talent);
    }

    private void applyCustomizerSettings(Note note) {
        note.setState(ItemState.OPEN);

        this.applyItemNodeEvents(activeNoteCollection, note);
    }

    void updateView() {
        activeItemContainers.forEach(this::updateView);
    }

    public void updateView(ItemContainer itemContainer) {
        if (itemContainer.getClass() == TalentTree.class) {
            this.updateTalentTreeView((TalentTree) itemContainer);
        } else {
            this.updateNoteCollectionView();
        }
    }

    private void updateTalentTreeView(TalentTree talentTree) {
        super.initTalentTreeContainers(talentTree);

        boolean[] talentGridSlotOccupancy = new boolean[Constant.GRID_ROW_COUNT * Constant.TALENTGRID_COLUMN_COUNT];

        talentTree.getTalents().forEach((index, talent) -> {
            this.applyCustomizerSettings(talentTree, talent);

            if (talent.getState() != ItemState.EMPTY) talent.updateTooltip();
            if (talent.hasPrerequisite()) talentTree.getTalentByIndex(talent.getPrerequisiteIndex()).addDependant(talent.getIndex());   //Memory leak?

            super.addToGrid(talentTree, talent);
            talentGridSlotOccupancy[index] = true;
        });

        super.initArrows(talentTree);
        talentTree.getArrows().forEach(arrow -> {
            arrow.getParts().forEach(part -> {
                super.addToGrid(talentTree, part);
                talentGridSlotOccupancy[part.getPositionIndex()] = true;
            });
        });

        // Populate the rest of the talent grid with empty slot items
        for (int i = 0; i < talentGridSlotOccupancy.length; i++) {
            if (talentGridSlotOccupancy[i] == false) {
                Talent emptyTalentSlot = new Talent(Talent.getEmptySlot(i));

                this.applyItemNodeEvents(talentTree, emptyTalentSlot);
                this.adjustRankLabel(emptyTalentSlot);

                super.addToGrid(talentTree, emptyTalentSlot);
            }
        }
    }
    private void updateNoteCollectionView() {
        this.noteGrid.getChildren().clear();

        int gridPaneSlotCount = Constant.GRID_ROW_COUNT * Constant.NOTEGRID_COLUMN_COUNT;
        for (int i = 0; i < gridPaneSlotCount; i++) {
            Note note = activeNoteCollection.get(i);

            if (note == null) {
                note = Note.getEmptySlot(i);
                this.applyItemNodeEvents(activeNoteCollection, note);
            } else {
                this.applyCustomizerSettings(note);
            }

            super.addToGrid(note);
        }
    }

    private void applyItemNodeEvents(ItemContainer itemContainer, GridItem item) {
        Node itemNode = item.getImageView();

        itemNode.setOnMouseClicked(event -> Event.fireEvent(this.main, new CustomEvent(CustomEvent.CUSTOMIZE_ITEM, itemContainer, item)));

        ItemState draggedItemState = item.getState();
        if (draggedItemState != ItemState.EMPTY) {
            itemNode.setOnDragDetected(event -> {
                if (event.isPrimaryButtonDown()) {
                    Dragboard dragBoard = itemNode.startDragAndDrop(TransferMode.MOVE);
                    Pair<Integer, Integer> draggedItemData = new Pair<>(itemContainer.getId(), item.getIndex());
                    ClipboardContent content = new ClipboardContent();
                    content.put(CUSTOM_DATAFORMAT, draggedItemData);

                    SnapshotParameters snapshot = new SnapshotParameters();
                    snapshot.setTransform(Transform.scale(0.75, 0.75));
                    WritableImage dragImage = item.getImageView().snapshot(snapshot, null);

                    dragBoard.setDragView(dragImage, dragImage.getWidth() / 2, dragImage.getHeight() / 2);
                    dragBoard.setContent(content);
                }
            });
        }

        itemNode.setOnDragOver(event -> {
            Pair<Integer, Integer> draggedItemData = (Pair<Integer, Integer>) event.getDragboard().getContent(CUSTOM_DATAFORMAT);

            if (this.isValidDragTarget(draggedItemData, itemContainer, item)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });


        itemNode.setOnDragDropped(event -> {
            Pair<Integer, Integer> draggedItemData = (Pair<Integer, Integer>) event.getDragboard().getContent(CUSTOM_DATAFORMAT);
            GridItem draggedItem = activeItemContainers.get(draggedItemData.getKey()).getItemByIndex(draggedItemData.getValue());

            if (this.isValidDragTarget(draggedItemData, itemContainer, item)) {
                if (item.getState() != ItemState.EMPTY) {
                    this.swapItems(itemContainer, draggedItem, item);
                    this.applyItemNodeEvents(itemContainer, item);
                } else {
                    this.changeItemIndex(itemContainer, draggedItem, item.getIndex());
                }

                event.setDropCompleted(true);

                this.applyItemNodeEvents(itemContainer, draggedItem);
                this.updateView(itemContainer);
            }
        });
    }

    private void adjustRankLabel(Talent talent) {
        super.initRankLabel(talent.getRankLabel());

        talent.getRankLabel().setMaxRank(talent.getRankData().size());
        talent.getRankLabel().showCurrentRank(false);
        talent.getRankLabel().setTranslateX(-3);
    }

    private boolean isValidDragTarget(Pair<Integer, Integer> draggedItemData, ItemContainer targetContainer, GridItem targetItem) {
        if (draggedItemData.getKey() == targetContainer.getId() &&
                draggedItemData.getValue() != targetItem.getIndex()) return true;
        else return false;
    }

    private void swapItems(ItemContainer itemContainer, GridItem sourceItem, GridItem targetItem) {
        if (itemContainer.getClass() == TalentTree.class) {
            this.clearDependencyLinks((TalentTree) itemContainer, (Talent) sourceItem);
            this.clearDependencyLinks((TalentTree) itemContainer, (Talent) targetItem);
        }

        int sourceIndex = sourceItem.getIndex();

        itemContainer.getItems().put(targetItem.getIndex(), sourceItem);
        itemContainer.getItems().put(sourceIndex, targetItem);

        sourceItem.setIndex(targetItem.getIndex());
        targetItem.setIndex(sourceIndex);
    }

    private void changeItemIndex(ItemContainer itemContainer, GridItem item, int newIndex) {
        if (itemContainer.getClass() == TalentTree.class) {
            this.clearDependencyLinks((TalentTree) itemContainer, (Talent) item);
        }

        int oldIndex = item.getIndex();

        itemContainer.getItems().put(newIndex, item);
        itemContainer.getItems().remove(oldIndex);

        item.setIndex(newIndex);
    }

    private void clearDependencyLinks(TalentTree talentTree, Talent talent) {
        if (talent.hasDependants()) {
            talent.getDependantIndexes().forEach(dependantIndex -> {
                talentTree.getTalentByIndex(dependantIndex).setPrerequisiteIndex(-1);
            });
            talent.getDependantIndexes().clear();
        }
        if (talent.hasPrerequisite()) {
            talentTree.getTalentByIndex(talent.getPrerequisiteIndex()).removeDependant(talent.getIndex());
            talent.setPrerequisiteIndex(-1);
        }
    }
}
