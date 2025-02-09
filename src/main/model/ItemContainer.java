package main.model;

import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import main.controllers.UtilityFunction;

import java.util.Map;

public abstract class ItemContainer {
    int id;
    String name;
    Background background;
    Map items;

    public ItemContainer(int id, String name) {
        this.id = id;
        this.name = name;
        this.background = Background.fill(Paint.valueOf(Color.BLACK.toString()));
    }

    public GridItem getItemByIndex(int index) { return (GridItem) this.items.get(index); }
    public void addItem(GridItem item) { this.items.put(item.getIndex(), item); }
    public Map getItems() { return this.items; }
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBackground(String name) { this.background = UtilityFunction.Resources.getBackgroundImage(name); }
    public Background getBackground() { return this.background; }
}
