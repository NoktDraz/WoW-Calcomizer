package main.model;

import java.util.HashMap;

public class NoteCollection extends ItemContainer {

    public NoteCollection(int id) {
        super(id, "Notes");

        this.items = new HashMap<Integer, Note>();
    }

    public HashMap<Integer, Note> getNotes() {
        return (HashMap) this.items;
    }
    public Note get(int index) { return (Note) this.items.get(index); }
}
