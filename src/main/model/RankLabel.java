package main.model;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class RankLabel extends Label {
    int maxRank;
    int currentRank;
    boolean showCurrentRank;

    public RankLabel() {
        this.currentRank = 0;

        this.setViewOrder(2);
        this.setMouseTransparent(true);
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        this.setFont(CustomFont.ITEM_TEXT);
        this.setPadding(new Insets(-2, 0, -2, 1));
        this.setLayoutY(-2);
        this.setLayoutX(0);
    }

    public void setMaxRank(int maxRank) {
        this.maxRank = maxRank;

        this.update();
    }

    public void setCurrentRank(int rank) {
        this.currentRank = rank;

        this.update();
    }

    public void showCurrentRank(boolean showPrefix) {
        this.showCurrentRank = showPrefix;

        this.update();
    }

    private void update() {
        if (this.showCurrentRank == true) {
            super.setText(this.currentRank + "/" + this.maxRank);
        } else {
            super.setText(String.valueOf(this.maxRank));
        }
    }
}
