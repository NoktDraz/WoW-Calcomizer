package main.enums;

public enum Resource {;
    public enum Folder {
        RESOURCES("resources"),
        INTERFACE(RESOURCES.getPath() + "Interface"),
        CLASS_ICONS(INTERFACE.getPath() + "Class_Icons"),
        FONTS(INTERFACE.getPath() + "Font"),
        BACKGROUNDS(INTERFACE.getPath() + "Backgrounds"),
        VIEW(RESOURCES.getPath() + "View"),
        FXML(VIEW.getPath() + "FXML"),
        VANILLA_DATASET(RESOURCES.getPath() + "Default Vanilla Talents"),

        PUBLIC_DATASETS("Custom Sets"),
        PUBLIC_ICONS("Icons");

        private String folderPath;
        Folder(String identifier) { this.folderPath = identifier; }
        public String getPath() { return this.folderPath + "/"; }
    }
    public enum InterfaceAsset {
        APPLICATION_ICON(Folder.INTERFACE.getPath() + "TC.png"),
        CURSOR_DEFAULT(Folder.INTERFACE.getPath() + "cursor_default.png"),
        CURSOR_INTERACT(Folder.INTERFACE.getPath() + "cursor_interact.png"),
        BACKGROUND_NOTES(Folder.INTERFACE.getPath() + "notes_bg.png"),
        ICON_DEFAULT(Folder.INTERFACE.getPath() + "missing_icon.png"),
        ICON_EMPTY_SLOT(Folder.INTERFACE.getPath() + "empty_slot.png"),
        ICON_CREATE_NEW(Folder.INTERFACE.getPath() + "create_new.png"),
        ARROW_BAR(Folder.INTERFACE.getPath() + "Bar.png"),
        ARROW_CAP(Folder.INTERFACE.getPath() + "Cap.png"),
        ARROW_CORNER(Folder.INTERFACE.getPath() + "Corner.png"),
        GLYPH_FINISH(Folder.INTERFACE.getPath() + "finish.png"),
        GLYPH_CANCEL(Folder.INTERFACE.getPath() + "cancel.png"),
        GLYPH_CLEAR(Folder.INTERFACE.getPath() + "clear.png"),
        GLYPH_DELETE(Folder.INTERFACE.getPath() + "delete.png"),
        ALERT_ERROR(Folder.INTERFACE.getPath() + "error.png"),
        ALERT_WARNING(Folder.INTERFACE.getPath() + "warning.png");

        private String assetPath;
        InterfaceAsset(String identifier) { this.assetPath = identifier; }
        public String getPath() { return this.assetPath; }
    }
    public enum Font {
        REGULAR(Folder.FONTS.getPath() + "Friz Quadrata Regular.ttf"),
        BOLD(Folder.FONTS.getPath() + "Friz Quadrata Bold.otf");

        private String path;
        Font(String fontPath) { this.path = fontPath; }
        public String getPath() { return this.path; }
    }

}
