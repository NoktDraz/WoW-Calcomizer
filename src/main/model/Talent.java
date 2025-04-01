package main.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import main.enums.ItemState;

import java.util.ArrayList;

public class Talent extends GridItem {
    private IntegerProperty currentRank;
    private int prerequisiteIndex;
    private ArrayList<Integer> dependantIndexes;
    private ArrayList<String> rankData;
    private RankLabel rankLabel;

    public Talent() {}
    public Talent(int index, String talentName, String iconName, int rowPosition, int columnPosition, int prerequisiteIndex, ArrayList<String> rankData) {
        super(index, talentName, iconName, rowPosition, columnPosition);
        this.prerequisiteIndex = prerequisiteIndex;
        this.dependantIndexes = new ArrayList<>();
        this.rankData = rankData;
        this.rankLabel = new RankLabel();
        this.currentRank = new SimpleIntegerProperty(-1);
    }
    public Talent(int index, String talentName, String iconName, int prerequisiteIndex) {
        super(index, talentName, iconName, (index / Constant.TALENTGRID_COLUMN_COUNT), (index % Constant.TALENTGRID_COLUMN_COUNT));
        this.prerequisiteIndex = prerequisiteIndex;
        this.dependantIndexes = new ArrayList<>();
        this.rankData = new ArrayList<>();
        this.rankLabel = new RankLabel();
        this.currentRank = new SimpleIntegerProperty(-1);

    }
    public Talent(GridItem emptySlotItem) {
        super(emptySlotItem);
        this.setPosition((this.index / Constant.TALENTGRID_COLUMN_COUNT), (this.index % Constant.TALENTGRID_COLUMN_COUNT));

        this.prerequisiteIndex = -1;
        this.dependantIndexes = new ArrayList<>();
        this.rankData = new ArrayList<>();
        this.rankLabel = new RankLabel();
        this.currentRank = new SimpleIntegerProperty(-1);

        this.setState(ItemState.EMPTY);
    }

    void update() {
        switch (this.state) {
            case INITIALIZING -> {
                super.update();
            }
            case EMPTY -> {
                super.update();

                this.rankLabel.setVisible(false);
            }
            case LOCKED -> {
                this.setIconBorder(CustomBorder.LOCKED);
                this.icon.setCursor(CustomCursor.DEFAULT);
                this.rankLabel.setEffect(CustomBorder.LOCKED);
                this.rankLabel.setTextFill(Color.GREY);

                this.icon.setOnMouseEntered(null);
                this.icon.setOnMouseExited(null);
            }
            case OPEN -> {
                this.icon.setCursor(CustomCursor.INTERACT);
                this.rankLabel.setEffect(CustomBorder.OPEN);
                this.rankLabel.setTextFill(Color.LIMEGREEN);

                this.icon.setOnMouseEntered(event -> {
                    this.setIconBorder(CustomBorder.OPEN_HIGHLIGHT);
                });
                this.icon.setOnMouseExited(event -> {
                    this.setIconBorder(CustomBorder.OPEN);
                });

                if (this.icon.isHover()) {
                    this.setIconBorder(CustomBorder.OPEN_HIGHLIGHT);
                } else {
                    this.setIconBorder(CustomBorder.OPEN);
                }
            }
            case MAXED -> {
                this.icon.setCursor(CustomCursor.INTERACT);
                this.rankLabel.setEffect(CustomBorder.MAXED);
                this.rankLabel.setTextFill(Color.GOLD);

                this.icon.setOnMouseEntered(event -> {
                    this.setIconBorder(CustomBorder.MAXED_HIGHLIGHT);
                });
                this.icon.setOnMouseExited(event -> {
                    this.setIconBorder(CustomBorder.MAXED);
                });

                if (this.icon.isHover()) {
                    this.setIconBorder(CustomBorder.MAXED_HIGHLIGHT);
                } else {
                    this.setIconBorder(CustomBorder.MAXED);
                }
            }
        }
    }

    public void updateTooltip() {
        if (this.currentRank.intValue() != 0) {
            if (this.isAtMaxRank() != true) {
                super.updateTooltip(this.rankData.get(this.currentRank.intValue() - 1), this.rankData.get(this.currentRank.intValue()));
            } else
                super.updateTooltip(this.rankData.get(this.currentRank.intValue() - 1));
        }
        else
            super.updateTooltip(this.rankData.get(0));
    }

    public void setIndex(int index) {
        super.setIndex(index);
        super.setPosition(index / 4, index % 4);
    }
    public void setState(ItemState newState) {
        this.state = newState;
        this.update();
    }

    public int getCurrentRank() { return this.currentRank.intValue(); }
    public void setCurrentRank(int value) { this.currentRank.set(value); }
    public boolean isAtMaxRank() { return this.currentRank.intValue() == this.rankData.size(); }
    public boolean hasPrerequisite() { return (this.prerequisiteIndex >= 0); }
    public boolean hasDependants() { return this.dependantIndexes.isEmpty() == false; }
    public int getPrerequisiteIndex() { return this.prerequisiteIndex; }
    public void setPrerequisiteIndex(int positionIndex) { this.prerequisiteIndex = positionIndex; }
    public ArrayList<Integer> getDependantIndexes() { return this.dependantIndexes; }
    public void addDependant(int positionIndex) { if (this.dependantIndexes.contains(positionIndex) != true) this.dependantIndexes.add(positionIndex); }
    public void removeDependant(int dependantIndex) { this.dependantIndexes.remove(((Object) dependantIndex)); }
    public ArrayList<String> getRankData() { return this.rankData; }
    public RankLabel getRankLabel() { return this.rankLabel; }
    public void updateRankLabel() {
        this.rankLabel.setCurrentRank(this.currentRank.intValue());
    }
    public static Talent getEmptySlot(int index) { return new Talent(GridItem.getEmptySlot(index)); }
}
