package main.model;

import main.enums.CharacterClass;
import main.enums.Resource;

import java.io.File;
import java.util.HashMap;

public class CustomizationSet {
    private final String dataFolderPath;
    private final String name;
    private HashMap<CharacterClass, ClassData> classesData;
    private int menuIndex;

    public CustomizationSet() {
        this.dataFolderPath = Resource.Folder.DEFAULT_DATASET.getPath();
        this.name = Resource.Folder.VANILLA_DATASET_FOLDER.getName();
        this.classesData = new HashMap<>();
    }

    public CustomizationSet(File dataFolder) {
        this.dataFolderPath = dataFolder.getPath();
        this.name = dataFolder.getName();
        this.classesData = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }
    public HashMap<CharacterClass, ClassData> getClassesData() {
        return this.classesData;
    }
    public void addClassData(ClassData classData) { this.classesData.put(CharacterClass.valueOf(classData.getName()), classData); }
    public File getDataFolder() { return new File(this.dataFolderPath); }
    public int getIndex() { return this.menuIndex; }
    public void setIndex(int menuIndex) {
        this.menuIndex = menuIndex;
    }
}
