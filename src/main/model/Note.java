package main.model;

import main.enums.ItemState;

public class Note extends GridItem {
    private String description;

    public Note(GridItem emptySlotItem) {
        super(emptySlotItem);
        super.setPosition((this.index / Constant.NOTEGRID_COLUMN_COUNT), (this.index % Constant.NOTEGRID_COLUMN_COUNT));

        this.setState(ItemState.EMPTY);
    }
    public Note(int id, String title, String iconName, String description) {
        super(id, title, iconName, (id / Constant.NOTEGRID_ROW_STEP), (id % Constant.NOTEGRID_ROW_STEP));

        this.description = description;

        super.setIconBorder(CustomBorder.NOTE);
        super.icon.setOnMouseEntered(event -> this.setIconBorder(CustomBorder.NOTE_HIGHLIGHT));
        super.icon.setOnMouseExited(event -> this.setIconBorder(CustomBorder.NOTE));
    }

    void update() {
        switch (this.state) {
            case INITIALIZING -> super.update();
            case EMPTY -> {
                super.update();
            }
            case OPEN -> {
                super.icon.setCursor(CustomCursor.INTERACT);

                super.icon.setOnMouseEntered(event -> {
                    super.setIconBorder(CustomBorder.OPEN_HIGHLIGHT);
                });
                super.icon.setOnMouseExited(event -> {
                    super.setIconBorder(CustomBorder.OPEN);
                });

                if (super.icon.isHover()) {
                    super.setIconBorder(CustomBorder.OPEN_HIGHLIGHT);
                } else {
                    super.setIconBorder(CustomBorder.OPEN);
                }
            }
            case LOCKED -> {
                super.icon.setCursor(CustomCursor.DEFAULT);

                super.icon.setOnMouseEntered(event -> {
                    super.setIconBorder(CustomBorder.MAXED_HIGHLIGHT);
                });
                super.icon.setOnMouseExited(event -> {
                    super.setIconBorder(CustomBorder.MAXED);
                });

                if (super.icon.isHover()) {
                    super.setIconBorder(CustomBorder.MAXED_HIGHLIGHT);
                } else {
                    super.setIconBorder(CustomBorder.MAXED);
                }
            }
        }
    }

    public void setIndex(int index) {
        super.setIndex(index);
        super.setPosition(index / Constant.NOTEGRID_ROW_STEP, index % Constant.NOTEGRID_ROW_STEP);
    }

    public void updateTooltip() {
        super.updateTooltip(this.description);
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public static Note getEmptySlot(int index) { return new Note(GridItem.getEmptySlot(index)); }
}
