package main.model;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import main.enums.Window;

import java.util.HashMap;

public final class CustomEvent extends Event {
    public static final EventType<CustomEvent> SET_MAIN_WINDOW = new EventType<>("SET_MAIN_WINDOW");
    public static final EventType<CustomEvent> UPDATE_CONTAINER_VIEW = new EventType<>("UPDATE_CONTAINER_VIEW");
    public static final EventType<CustomEvent> SHOW_ALERT = new EventType<>("SHOW_ALERT");
    public static final EventType<CustomEvent> NEW_CUSTOM_SET = new EventType<>("NEW_CUSTOM_SET");
    public static final EventType<CustomEvent> CREATE_CUSTOM_SET = new EventType<>("CREATE_CUSTOM_SET");
    public static final EventType<CustomEvent> CUSTOMIZE_ITEM = new EventType<>("CUSTOMIZE_ITEM");
    public static final EventType<CustomEvent> BROWSE_ITEM_ICONS = new EventType<>("BROWSE_ITEM_ICONS");
    public static final EventType<CustomEvent> CHOOSE_PREREQUISITE = new EventType<>("CHOOSE_PREREQUISITE");
    public static final EventType<CustomEvent> FINISH_PREREQUISITE = new EventType<>("FINISH_PREREQUISITE");
    public static final EventType<CustomEvent> SAVE_CLASS_DATA = new EventType<>("SAVE_CLASS_DATA");
    public static final EventType<CustomEvent> SAVE_ALL = new EventType<>("SAVE_ALL");
    public static final EventType<CustomEvent> EXIT_APPLICATION = new EventType<>("EXIT_APPLICATION");

    private HashMap<Class<?>, Object> eventObjects;

    public CustomEvent(EventType<CustomEvent> customEvent, Window targetWindow) {
        super(null, null, customEvent);
        this.eventObjects = new HashMap<>();
        this.eventObjects.put(Window.class, targetWindow);
    }
    public CustomEvent(EventType<CustomEvent> customEvent, TalentTree talentTree, Talent talent, ImageView currentIcon) {
        super(null, null, customEvent);
        this.eventObjects = new HashMap<>();
        this.eventObjects.put(TalentTree.class, talentTree);
        this.eventObjects.put(Talent.class, talent);
        this.eventObjects.put(ImageView.class, currentIcon);
    }
    public CustomEvent(EventType<CustomEvent> customEvent, ItemContainer itemContainer, GridItem item) {
        super(null, null, customEvent);
        this.eventObjects = new HashMap<>();
        this.eventObjects.put(ItemContainer.class, itemContainer);
        this.eventObjects.put(GridItem.class, item);
    }
    public CustomEvent(EventType<CustomEvent> customEvent, GridItem item) {
        super(null, null, customEvent);
        this.eventObjects = new HashMap<>();
        this.eventObjects.put(GridItem.class, item);
    }
    public CustomEvent(EventType<CustomEvent> customEvent, ItemContainer itemContainer) {
        super(null, null, customEvent);
        this.eventObjects = new HashMap<>();
        this.eventObjects.put(ItemContainer.class, itemContainer);
    }
    public CustomEvent(EventType<CustomEvent> customEvent, int talentId) {
        super(null, null, customEvent);
        this.eventObjects = new HashMap<>();
        this.eventObjects.put(int.class, talentId);
    }
    public CustomEvent(EventType<CustomEvent> customEvent, String setName, boolean useActiveSetAsBase) {
        super(null, null, customEvent);
        this.eventObjects = new HashMap<>();
        this.eventObjects.put(String.class, setName);
        this.eventObjects.put(boolean.class, useActiveSetAsBase);
    }
    public CustomEvent(EventType<CustomEvent> customEvent, String message, Alert.AlertType type, Window returnWindow) {
        super(null, null, customEvent);
        this.eventObjects = new HashMap<>();
        this.eventObjects.put(String.class, message);
        this.eventObjects.put(Alert.AlertType.class, type);
        this.eventObjects.put(Window.class, returnWindow);
    }
    public CustomEvent(EventType<CustomEvent> customEvent) {
        super(null, null, customEvent);
    }

    public Object getEventObjectOfClass(Class<?> cls) {
        return this.eventObjects.get(cls);
    }
    public Object getMainObjectOfEvent() {
        return this.eventObjects.values().stream().findFirst().get();
    }
}
