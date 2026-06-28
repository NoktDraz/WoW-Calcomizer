package main.model;

import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.controllers.UtilityFunction;
import main.enums.AnimationSet;
import main.enums.ItemState;
import main.enums.Resource;

public class GridItem {
    public static final Image ICON_DEFAULT = UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.ICON_DEFAULT);
    public static final Image ICON_EMPTY_SLOT = UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.ICON_EMPTY_SLOT);
    public static final Image ICON_CREATE_NEW = UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.ICON_CREATE_NEW);
    public static final String EMPTY_SLOT_NAME = "Empty Slot";

    int index;
    int rowPosition, columnPosition;
    String itemName;
    String iconName;
    ImageView icon;
    ItemTooltip tooltip;
    ItemState state;

    public GridItem() { this.setState(ItemState.INITIALIZING); }

    public GridItem(int row, int column) {
        this.rowPosition = row;
        this.columnPosition = column;
    }
    public GridItem(GridItem gridItem) {
        this.state = gridItem.getState();
        this.index = gridItem.getIndex();
        this.itemName = gridItem.getItemName();
        this.rowPosition = gridItem.getRowPosition();
        this.columnPosition = gridItem.getColumnPosition();
        this.iconName = gridItem.getIconName();
        this.icon = gridItem.getImageView();
        this.tooltip = new ItemTooltip();
    }
    public GridItem(int index, String itemName, String itemIconPath, int row, int column) {
        this.setState(ItemState.INITIALIZING);

        this.index = index;
        this.rowPosition = row;
        this.columnPosition = column;
        this.itemName = itemName;
        this.iconName = itemIconPath;
        this.icon.setImage(UtilityFunction.Resources.getPublicIcon(itemIconPath));
        this.tooltip = new ItemTooltip();

        this.initTooltip();
    }
    public static GridItem getEmptySlot(int index) {
        GridItem emptySlotItem = new GridItem();

        emptySlotItem.index = index;
        emptySlotItem.itemName = EMPTY_SLOT_NAME;
        emptySlotItem.tooltip = new ItemTooltip();

        return emptySlotItem;
    }

    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public void setPosition(int row, int column) { this.rowPosition = row; this.columnPosition = column; }
    public int getRowPosition() {
        return this.rowPosition;
    }
    public int getColumnPosition() { return this.columnPosition; }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public String getItemName() { return this.itemName; }
    public String getIconName() {
        return this.iconName;
    }
    public void setIconName(String name) {
        this.iconName = name;
    }
    public void setIcon(Image iconImage) {
        this.icon.setImage(iconImage);
    }
    public void setIconBorder(DropShadow border) { this.icon.setEffect(border); }
    public ImageView getImageView() { return this.icon; }
    public void setImageView(ImageView imageView) { this.icon = imageView; }
    public Image getIconImage() { return this.icon.getImage(); }
    public ItemTooltip getTooltip() { return this.tooltip; }
    public ItemState getState() { return this.state; }
    public void setState(ItemState state) {
        this.state = state;
        this.update();
    }

    void update() {
        switch (this.state) {
            case INITIALIZING -> {
                this.icon = new ImageView(ICON_DEFAULT);
                this.icon.setViewOrder(3);
                this.icon.setFitWidth(40);
                this.icon.setFitHeight(40);
                this.setIconBorder(CustomBorder.BASE);
                this.initMouseOverEffects();
            }
            case EMPTY -> {
                this.setIconBorder(CustomBorder.OPEN);
                this.itemName = EMPTY_SLOT_NAME;
                this.icon.setImage(ICON_EMPTY_SLOT);
                this.icon.setOpacity(0.5);

                this.initEmptySlotAnimations();
            }
        }
    }
    void initMouseOverEffects() {
        this.icon.setPickOnBounds(true);
        this.icon.setCursor(CustomCursor.INTERACT);
    }
    void initEmptySlotAnimations() {
        UtilityFunction.Animations.apply(AnimationSet.EMPTY_GRID_SLOT, this);
    }
    public void initTooltip() {
        this.tooltip.initialize();

        Tooltip.install(this.icon, this.tooltip);
    }

    public void updateTooltip(String itemText) {
        this.tooltip.updateContent(this.itemName, itemText);
    }
    public void updateTooltip(String itemText, String nextRankText) {
        this.tooltip.updateContent(this.itemName, itemText, nextRankText);
    }
}
