package main.enums;

public enum Window {
    CALCULATOR ("Talent Calculator", Resource.Folder.FXML.getPath() + "Calculator.fxml"),
    CUSTOMIZER("Customizer", Resource.Folder.FXML.getPath() + "Customizer.fxml"),
    TALENT_CUSTOMIZER ("Customize Talent", Resource.Folder.FXML.getPath() + "TalentCustomizer.fxml"),
    NOTE_CUSTOMIZER ("Customize Note", Resource.Folder.FXML.getPath() + "NoteCustomizer.fxml"),
    PREREQUISITE ("Choose Prerequisite", Resource.Folder.FXML.getPath() + "PrerequisitePicker.fxml"),
    NEW_SET ("New Customization Set", Resource.Folder.FXML.getPath() + "NewSet.fxml"),
    ALERT ("Error", Resource.Folder.FXML.getPath() + "AlertMessage.fxml");

    private String title;
    private String fxmlFilePath;

    Window(String title, String fxmlFilePath) {
        this.title = title;
        this.fxmlFilePath = fxmlFilePath;
    }

    public String getTitle() {
        return this.title;
    }

    public String getFXMLFilePath() { return this.fxmlFilePath; }
}
