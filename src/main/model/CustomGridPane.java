package main.model;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class CustomGridPane extends GridPane {
    private static ColumnConstraints DEFAULT_COLUMN = new ColumnConstraints(10, 30, 30);
    private static RowConstraints DEFAULT_ROW = new RowConstraints(10, 100, 100);
    private Class<?> acceptedItemType;

    public CustomGridPane() {}
    private CustomGridPane(int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            this.getColumnConstraints().add(DEFAULT_COLUMN);
        }
        for (int j = 0; j < columns; j++) {
            this.getRowConstraints().add(DEFAULT_ROW);
        }
    }

    public void add(GridItem gridItem) {
        if (gridItem.getClass() == this.acceptedItemType) super.add(gridItem.getImageView(), gridItem.getRowPosition(), gridItem.getColumnPosition());
    }

    public static class construct {
        public static CustomGridPane talentTreeGrid(int rows, int columns) {
            CustomGridPane gridPane = new CustomGridPane(rows, columns);
            gridPane.acceptedItemType = Talent.class;

            return gridPane;
        }
        public static CustomGridPane noteGrid(int rows, int columns) {
            CustomGridPane gridPane = new CustomGridPane(rows, columns);
            gridPane.acceptedItemType = Note.class;

            return gridPane;
        }

        public static CustomGridPane talentTreeGrid(GridPane gridPane) { return talentTreeGrid(gridPane.getRowCount(), gridPane.getColumnCount()); }
        public static CustomGridPane noteGrid(GridPane gridPane) { return noteGrid(gridPane.getRowCount(), gridPane.getColumnCount()); }
    }
}
