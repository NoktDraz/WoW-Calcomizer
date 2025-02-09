package main.model;

import main.enums.CharacterClass;

import java.util.ArrayList;
import java.util.List;

public class ClassData {
    private ArrayList<ItemContainer> dataItemContainers;
    private final String className;

    public ClassData(CharacterClass cls) {
        this.dataItemContainers = new ArrayList<>();
        this.className = cls.name();
    }

    public List<TalentTree> getTalentTrees() {
        List<TalentTree> talentTrees = new ArrayList<>();

        this.dataItemContainers.subList(0, this.dataItemContainers.size() - 1).
                forEach(itemContainer -> talentTrees.add((TalentTree) itemContainer));

        return talentTrees;
    }

    public NoteCollection getNoteCollection() { return (NoteCollection) this.dataItemContainers.get(this.dataItemContainers.size() - 1); }
    public ItemContainer getItemContainerById(int id) { return this.dataItemContainers.get(id); }
    public ArrayList<ItemContainer> getItemContainers() { return this.dataItemContainers; }
    public void setItemContainers(ArrayList<ItemContainer> itemContainers) { this.dataItemContainers = itemContainers; }
    public String getName() { return this.className; }
}
