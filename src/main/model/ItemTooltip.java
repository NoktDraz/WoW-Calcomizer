package main.model;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class ItemTooltip extends Tooltip {
    private TextFlow aggregateText;
    private TextFlow aggregateTextNextRank;
    private Text itemName;
    private Text itemText;
    private Text itemNameCopy;
    private Text itemTextCopy;
    private Text nextRankSeparator;
    private Text nextRankText;

    public ItemTooltip() {
        this.aggregateText = new TextFlow();
        this.itemName = new Text();
        this.itemText = new Text();
        this.itemNameCopy = new Text();
        this.itemTextCopy = new Text();
        this.nextRankSeparator = new Text(System.lineSeparator() + System.lineSeparator() + "Next Rank:" + System.lineSeparator());
        this.nextRankText = new Text();
    }

    public void initialize() {
        this.itemName.setFont(CustomFont.ITEM_NAME);
        this.itemName.setUnderline(true);
        this.itemName.setFill(Color.WHITE);
        this.itemText.setFont(CustomFont.ITEM_TEXT);
        this.itemText.setFill(Color.GOLD);
        this.itemNameCopy.setFont(CustomFont.ITEM_NAME);
        this.itemNameCopy.setUnderline(true);
        this.itemNameCopy.setFill(Color.WHITE);
        this.itemTextCopy.setFont(CustomFont.ITEM_TEXT);
        this.itemTextCopy.setFill(Color.GOLD);
        this.nextRankSeparator.setFill(Color.WHITE);
        this.nextRankSeparator.setFont(CustomFont.ITEM_TEXT);
        this.nextRankText.setFill(Color.GOLD);
        this.nextRankText.setFont(CustomFont.ITEM_TEXT);

        this.aggregateTextNextRank = new TextFlow(this.itemName, new Text(System.lineSeparator()), this.itemText, this.nextRankSeparator, this.nextRankText);
        this.aggregateText = new TextFlow(this.itemNameCopy, new Text(System.lineSeparator()), this.itemTextCopy);

        this.setShowDelay(Duration.ZERO);
        this.setHideDelay(Duration.ZERO);
        this.setShowDuration(Duration.INDEFINITE);
        this.setMaxWidth(330);
        this.setWrapText(true);
        this.setAutoFix(false);
    }

    public void updateContent(String itemName, String itemText, String nextRankText) {
        this.setGraphic(aggregateTextNextRank);
        this.itemName.setText(itemName);
        this.itemText.setText(itemText);
        this.nextRankText.setText(nextRankText);
    }

    public void updateContent(String itemName, String itemText) {
        this.setGraphic(aggregateText);
        this.itemNameCopy.setText(itemName);
        this.itemTextCopy.setText(itemText);
    }
}
