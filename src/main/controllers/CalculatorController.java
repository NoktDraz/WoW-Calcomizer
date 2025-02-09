package main.controllers;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.FontWeight;
import main.enums.CharacterClass;
import main.enums.ItemState;
import main.enums.Window;
import main.model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CalculatorController extends CalcomizerBase {
    private IntegerProperty pointsAllocated;
    private HashMap<Integer, List<Integer>> pointsInRows;
    private HashMap<Integer, List<Boolean>> rowLocks;

    public CalculatorController() {}

    @FXML
    Label talentPoints;

    public void initialize() {
        super.initialize();

        this.focusedClassButton = this.warrior;

        this.talentPoints.setFont(UtilityFunction.Resources.CustomFont(FontWeight.NORMAL, 16));
        this.pointsAllocated = new SimpleIntegerProperty(0);
        this.pointsInTrees.forEach(label -> {
            label.setViewOrder(-1);
            label.setFont(CustomFont.ITEM_TEXT);
        });

        Set<Integer> talentTreeIds = new HashSet<>(IntStream.range(0, 3).boxed().toList());
        this.pointsInRows = new HashMap<>(
                talentTreeIds.stream().collect(Collectors.toMap(
                        talentTreeId -> talentTreeId,
                        rows -> new ArrayList<>(Constant.POINTS_IN_ROWS_INITIAL_STATE)
                )));
        this.rowLocks = new HashMap<>(
                talentTreeIds.stream().collect(Collectors.toMap(
                        talentTreeId -> talentTreeId,
                        rows -> Collections.nCopies(Constant.GRID_ROW_COUNT, Boolean.TRUE)
                )));
    }

    @FXML
    public void switchCustomizer() {
        this.talentTreeGridPanes.forEach(gridPane -> gridPane.getChildren().retainAll(gridPane.getChildren().get(0)));

        Event.fireEvent(this.main, new CustomEvent(CustomEvent.SET_MAIN_WINDOW, Window.CUSTOMIZER));
    }
    public void activate() {
        super.activate();

        this.loadActiveSetData();
    }

    void loadActiveSetData() {
        super.loadActiveSetData();

        classesData.forEach((cls, classData) -> this.applyCalculatorSettings(classData));
        this.setActiveClass(activeClass);
    }

    void loadActiveClassData() {
        super.loadActiveClassData();

        activeTalentTrees = classesData.get(activeClass).getTalentTrees();
        activeTalentTrees.forEach(this::initTalentTreeView);
        activeNoteCollection = classesData.get(activeClass).getNoteCollection();
        this.initNoteCollectionView();
    }

    public void initCustomizationSetMenu() {
        this.customizationSetMenu.getItems().clear();

        super.initCustomizationSetMenu();
    }
    void setActiveClass(CharacterClass cls) {
        super.setActiveClass(cls);

        this.resetPoints();

        activeTalentTrees.forEach(this::initTalentTreeView);
        this.initNoteCollectionView();
    }

    private void applyCalculatorSettings(ClassData classData) {
        classData.getItemContainers().forEach(this::applyCalculatorSettings);
    }

    private void applyCalculatorSettings(ItemContainer itemContainer) {
        if (itemContainer.getClass() == TalentTree.class) {
            TalentTree talentTree = (TalentTree) itemContainer;

            talentTree.getTalents().forEach((integer, talent) -> this.applyCalculatorSettings(talentTree, talent));

            this.initArrows(talentTree);
        } else {
            ((NoteCollection) itemContainer).getNotes().forEach(((integer, note) -> this.applyCalculatorSettings(note)));
        }
    }

    private void applyCalculatorSettings(TalentTree talentTree, Talent talent) {
        talent.setState(ItemState.LOCKED);
        talent.setCurrentRank(0);
        talent.updateTooltip();

        if (talent.hasPrerequisite()) talentTree.getTalentByIndex(talent.getPrerequisiteIndex()).addDependant(talent.getIndex());

        this.applyTalentNodeEvents(talentTree, talent);
        this.adjustRankLabel(talent);
    }

    private void applyCalculatorSettings(Note note) {
        note.setState(ItemState.LOCKED);
    }

    private void applyTalentNodeEvents(TalentTree talentTree, Talent talent) {
        Node talentNode = talent.getImageView();

        talentNode.setOnMouseClicked(event -> {
            if (talent.getState() != ItemState.LOCKED) {
                int operation = 0;

                switch (event.getButton()) {
                    case PRIMARY -> operation = 1;
                    case SECONDARY -> operation = -1;
                }

                this.attemptTalentRankOperation(talentTree, talent, operation);
            }
        });
    }

    private void attemptTalentRankOperation(TalentTree talentTree, Talent talent, int operation) {
        if (isTalentRankOperationValid(talentTree, talent, operation)) {
            talent.setCurrentRank(talent.getCurrentRank() + operation);

            this.adjustPointsInRow(this.pointsInRows.get(talentTree.getId()), talent.getIndex() / 4, operation);

            this.updateRowLocks(talentTree.getId());
            this.updateTalentStates(talentTree);
        }
    }

    private void initTalentTreeView(TalentTree talentTree) {
        super.initTalentTreeContainers(talentTree);

        talentTree.getTalents().forEach((index, talent) -> {
            talent.setCurrentRank(0);
            super.addToGrid(talentTree, talent);
        });
        talentTree.getArrows().forEach(arrow -> arrow.getParts().forEach(part -> super.addToGrid(talentTree, part)));
        this.updateTalentStates(talentTree);
    }

    private void initNoteCollectionView() {
        this.noteGrid.getChildren().clear();

        activeNoteCollection.getNotes().forEach((index, note) -> super.addToGrid(note));
    }

    private boolean isTalentRowLocked(TalentTree talentTree, Talent talent) {
        return this.rowLocks.get(talentTree.getId()).get(talent.getIndex() / 4);
    }

    private boolean isTalentRankOperationValid(TalentTree tree, Talent talent, int operation) {
        // Reduce rank - current rank must not be 0
        if (operation < 0 && talent.getCurrentRank() != 0) {
            // Dependant talents must not be active, if the talent has any
            if (talent.hasDependants()) {
                for (Integer index : talent.getDependantIndexes()) {
                    if (tree.getTalentByIndex(index).getCurrentRank() != 0) {
                        return false;
                    }
                }
            }

            List<Integer> rows = this.pointsInRows.get(tree.getId());
            int highestActiveRow = this.getHighestActiveRow(rows);
            // Is the sum of points up to the current row sufficient if there are talents active in the higher rows
            if (talent.getRowPosition() == highestActiveRow) {
                return true;
            } else if (this.getSumOfPointsInRangeOfRows(rows, 0, talent.getRowPosition()) > (1 + talent.getRowPosition()) * Constant.POINTS_REQUIRED_PER_ROW &&
                    this.getSumOfPointsInRangeOfRows(rows,0, highestActiveRow - 1) > highestActiveRow * Constant.POINTS_REQUIRED_PER_ROW) {
                return true;
            }
        }
        // Increase rank - must have enough talent points
        else if (operation > 0 && this.pointsAllocated.intValue() < Constant.INITIAL_TALENT_POINTS) {
            // Rank must not be maxed
            if (talent.getState() != ItemState.MAXED) {
                return true;
            }
        }

        return false;
    }

    private void updateTalentStates(TalentTree talentTree) {
        talentTree.getTalents().forEach((index, talent) -> {
            if (isTalentRowLocked(talentTree, talent)) {
                talent.setState(ItemState.LOCKED);
            } else {
                if (talent.isAtMaxRank()) {
                    talent.setState(ItemState.MAXED);
                } else {
                    if (talent.hasPrerequisite() && talentTree.getTalentByIndex(talent.getPrerequisiteIndex()).isAtMaxRank() != true) {
                        talent.setState(ItemState.LOCKED);
                    } else {
                        talent.setState(ItemState.OPEN);
                    }
                }
            }

            talent.updateTooltip();
            talent.updateRankLabel();
        });

        talentTree.getArrows().forEach(arrow -> {
            if (talentTree.getTalentByIndex(arrow.getIndex()).getState() == ItemState.LOCKED)
                arrow.setState(ItemState.LOCKED);
            else
                arrow.setState(ItemState.OPEN);
        });
    }

    private void adjustRankLabel(Talent talent) {
        super.initRankLabel(talent.getRankLabel());

        talent.getRankLabel().setMaxRank(talent.getRankData().size());
        talent.getRankLabel().showCurrentRank(true);
        talent.getRankLabel().setTranslateX(3);
    }

    private void updateRowLocks(int talentTreeId) {
        int sumPointsInRows = this.pointsInRows.get(talentTreeId).stream().mapToInt(row -> row).sum();

        for (int i = 1; i < Constant.GRID_ROW_COUNT; i++) {
            if (i <= sumPointsInRows / Constant.POINTS_REQUIRED_PER_ROW)
                this.rowLocks.get(talentTreeId).set(i, false);
            else {
                this.rowLocks.get(talentTreeId).set(i, true);

                break;
            }
        }

        this.pointsInTrees.get(talentTreeId).setText(String.valueOf(sumPointsInRows));
    }

    private int getSumOfPointsInRangeOfRows(List<Integer> rows, int startRowIndex, int endRowIndex) {
        int sum = 0;

        for (int i = startRowIndex; i <= endRowIndex; i++) {
            sum += rows.get(i);
        }

        return sum;
    }

    private void adjustPointsInRow(List<Integer> rows, int rowIndex, int operand) {
        rows.set(rowIndex, rows.get(rowIndex) + operand);

        this.pointsAllocated.set(this.pointsAllocated.get() + operand);
        this.talentPoints.setText(String.valueOf(Constant.INITIAL_TALENT_POINTS - this.pointsAllocated.intValue()));
    }

    private void resetPoints() {
        this.pointsAllocated.set(0);
        this.talentPoints.setText(String.valueOf(Constant.INITIAL_TALENT_POINTS));
        this.pointsInRows.forEach((treeId, rows) -> Collections.fill(rows, 0));
        this.pointsInTrees.forEach(label -> label.setText("0"));

        this.rowLocks.forEach((treeId, rowLocks) -> {
            this.rowLocks.replace(treeId, new ArrayList<>(Constant.ROW_LOCKS_INITIAL_STATE));
        });
    }

    private int getHighestActiveRow(List<Integer> rows) {
        ListIterator<Integer> reverseIterator = rows.listIterator(rows.size());

        while (reverseIterator.hasPrevious()) {
            if (reverseIterator.previous() != 0) return reverseIterator.previousIndex() + 1;
        }

        return 0;
    }
}
