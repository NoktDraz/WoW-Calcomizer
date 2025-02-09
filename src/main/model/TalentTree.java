package main.model;

import java.util.LinkedList;
import java.util.TreeMap;

public class TalentTree extends ItemContainer {
    private LinkedList<Arrow> arrows;

    public TalentTree(int index, String name) {
        super(index, name);

        this.items = new TreeMap<Integer, Talent>();
        this.arrows = new LinkedList<>();
    }

    public TreeMap<Integer, Talent> getTalents() { return (TreeMap) this.items; }
    public Talent getTalentByIndex(int index) { return (Talent) this.items.get(index); }
    public LinkedList<Arrow> getArrows() { return this.arrows; }
    public void addArrow(Arrow arrow) { this.arrows.add(arrow); }
}
