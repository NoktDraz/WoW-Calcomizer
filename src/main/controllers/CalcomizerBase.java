package main.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import main.enums.CharacterClass;
import main.enums.ItemState;
import main.enums.Resource;
import main.model.*;

import java.util.*;

public abstract class CalcomizerBase implements FXMLController {
    static ArrayList<CustomizationSet> customizationSets;
    static Map<CharacterClass, ClassData> classesData;
    static CustomizationSet activeCustomizationSet;
    static CharacterClass activeClass;
    static List<TalentTree> activeTalentTrees;
    static NoteCollection activeNoteCollection;
    static int focusedClassButtonIndex;

    static {
        customizationSets = new ArrayList<>();
        classesData = new HashMap<>();
        focusedClassButtonIndex = -1;
    }

    LinkedList<TitledPane> talentTreeTitledPanes;
    LinkedList<HBox> talentTreeHBoxes;
    LinkedList<GridPane> talentTreeGridPanes;
    LinkedList<Label> pointsInTrees;
    LinkedList<Button> classButtons;
    Button focusedClassButton;

    //region FXML Injection
    @FXML
    MenuItem exit;
    @FXML
    Label switcher;
    @FXML
    AnchorPane main;
    @FXML
    Button warrior;
    @FXML
    Button rogue;
    @FXML
    Button hunter;
    @FXML
    Button paladin;
    @FXML
    Button shaman;
    @FXML
    Button mage;
    @FXML
    Button warlock;
    @FXML
    Button priest;
    @FXML
    Button druid;
    @FXML
    TitledPane tree1;
    @FXML
    Label pointsInTree1;
    @FXML
    HBox treeHBox1;
    @FXML
    GridPane treeGrid1;
    @FXML
    TitledPane tree2;
    @FXML
    Label pointsInTree2;
    @FXML
    HBox treeHBox2;
    @FXML
    GridPane treeGrid2;
    @FXML
    TitledPane tree3;
    @FXML
    Label pointsInTree3;
    @FXML
    HBox treeHBox3;
    @FXML
    GridPane treeGrid3;
    @FXML
    TitledPane notes;
    @FXML
    HBox noteHBox;
    @FXML
    GridPane noteGrid;
    @FXML
    ChoiceBox<String> customizationSetMenu;
    //endregion

    void initialize() {
        this.talentTreeTitledPanes = new LinkedList<>(Arrays.asList(this.tree1, this.tree2, this.tree3));
        this.talentTreeHBoxes = new LinkedList<>(Arrays.asList(this.treeHBox1, this.treeHBox2, this.treeHBox3));
        this.talentTreeGridPanes = new LinkedList<>(Arrays.asList(this.treeGrid1, this.treeGrid2, this.treeGrid3));

        this.pointsInTrees = new LinkedList<>(Arrays.asList(this.pointsInTree1, this.pointsInTree2, this.pointsInTree3));

        this.talentTreeTitledPanes.forEach(pane -> pane.setFont(CustomFont.ITEM_NAME));
        this.notes.setFont(CustomFont.ITEM_NAME);

        this.noteHBox.setBackground(new Background(new BackgroundImage(
                UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.BACKGROUND_NOTES),
                null, null, null,
                new BackgroundSize(1,1,true,true,false,false)
        )));
        this.classButtons = new LinkedList<>(
                (Arrays.asList(this.warrior, this.rogue, this.hunter, this.paladin, this.shaman, this.druid, this.priest, this.mage, this.warlock)));
        this.classButtons.forEach(button -> {
            button.getGraphic().setEffect(CustomBorder.CLASS_BUTTON_UNSELECTED);

            button.setOnMouseEntered(event -> {
                if (button.isDisabled() != true)
                    button.getGraphic().setEffect(CustomBorder.CLASS_ICON);
            });
            button.setOnMouseExited(event -> {
                if (this.focusedClassButton != button && button.isDisabled() != true)
                    button.getGraphic().setEffect(CustomBorder.CLASS_BUTTON_UNSELECTED);
            });
        });
        this.initCustomizationSetMenu();
        this.customizationSetMenu.getSelectionModel().selectedItemProperty().addListener(
            (observableValue, oldValue, newValue) -> {
                if (newValue != null)
                    this.setActiveCustomizationSet(this.customizationSetMenu.getSelectionModel().getSelectedIndex());
            }
        );
        //region Class button action events
        this.warrior.setOnAction(customEvent -> {
            this.toggleClassButtonFocus(this.classButtons.indexOf(this.warrior));
            setActiveClass(CharacterClass.WARRIOR);
        });
        this.rogue.setOnAction(customEvent -> {
            this.toggleClassButtonFocus(this.classButtons.indexOf(this.rogue));
            setActiveClass(CharacterClass.ROGUE);
        });
        this.hunter.setOnAction(customEvent -> {
            this.toggleClassButtonFocus(this.classButtons.indexOf(this.hunter));
            setActiveClass(CharacterClass.HUNTER);
        });
        this.paladin.setOnAction(customEvent -> {
            this.toggleClassButtonFocus(this.classButtons.indexOf(this.paladin));
            setActiveClass(CharacterClass.PALADIN);
        });
        this.shaman.setOnAction(customEvent -> {
            this.toggleClassButtonFocus(this.classButtons.indexOf(this.shaman));
            setActiveClass(CharacterClass.SHAMAN);
        });
        this.druid.setOnAction(customEvent -> {
            this.toggleClassButtonFocus(this.classButtons.indexOf(this.druid));
            setActiveClass(CharacterClass.DRUID);
        });
        this.priest.setOnAction(customEvent -> {
            this.toggleClassButtonFocus(this.classButtons.indexOf(this.priest));
            setActiveClass(CharacterClass.PRIEST);
        });
        this.mage.setOnAction(customEvent -> {
            this.toggleClassButtonFocus(this.classButtons.indexOf(this.mage));
            setActiveClass(CharacterClass.MAGE);
        });
        this.warlock.setOnAction(customEvent -> {
            this.toggleClassButtonFocus(this.classButtons.indexOf(this.warlock));
            setActiveClass(CharacterClass.WARLOCK);
        });
        //endregion
    }

    @FXML
    void exit() { Platform.exit(); }

    void activate() {
        this.classButtons.get(focusedClassButtonIndex).fire();
        this.customizationSetMenu.getSelectionModel().select(activeCustomizationSet.getIndex());
    }

    public static void initCustomizationSets(CustomizationSet defaultDataSet) {
        customizationSets.clear();

        addCustomizationSet(defaultDataSet);
    }
    public static void addCustomizationSet(CustomizationSet dataSet) {
        customizationSets.add(dataSet);
    }

    void setActiveCustomizationSet(int index) {
        activeCustomizationSet = customizationSets.get(index);

        this.loadActiveSetData();
    }

    void loadActiveSetData() {
        classesData = activeCustomizationSet.getClassesData();
    }

    public static boolean isDefaultSetActive() {
        return customizationSets.indexOf(activeCustomizationSet) == 0;
    }
    void initCustomizationSetMenu() {

        customizationSets.forEach(customizationSet -> {
            customizationSet.setIndex(this.customizationSetMenu.getItems().size());
            this.customizationSetMenu.getItems().add(customizationSet.getName());
        });
    }

    void toggleClassButtonFocus(int buttonIndex) {
        focusedClassButtonIndex = buttonIndex;

        this.focusedClassButton.getStyleClass().remove("focused");
        this.focusedClassButton.getGraphic().setEffect(CustomBorder.CLASS_BUTTON_UNSELECTED);

        this.focusedClassButton = this.classButtons.get(buttonIndex);
        this.focusedClassButton.getStyleClass().add("focused");
        this.focusedClassButton.getGraphic().setEffect(CustomBorder.CLASS_ICON);
    }

    void setActiveClass(CharacterClass cls) {
        activeClass = cls;

        this.loadActiveClassData();
    }
    void loadActiveClassData() {
        activeTalentTrees = classesData.get(activeClass).getTalentTrees();
        activeNoteCollection = classesData.get(activeClass).getNoteCollection();
    }

    void initTalentTreeContainers(TalentTree talentTree) {
        int talentTreeId = talentTree.getId();

        this.talentTreeHBoxes.get(talentTreeId).setBackground(talentTree.getBackground());
        this.talentTreeGridPanes.get(talentTreeId).getChildren().clear();
        this.talentTreeTitledPanes.get(talentTreeId).setText(talentTree.getName());
    }
    void initArrows(TalentTree talentTree) {
        talentTree.getArrows().clear();

        talentTree.getTalents().forEach((index, talent) -> {
            if (talent.hasPrerequisite()) {
                Arrow arrow = UtilityFunction.Arrows.constructArrowFromGridIndexes(talent.getPrerequisiteIndex(), talent.getIndex());

                talentTree.addArrow(arrow);
            }
        });
    }
    void initRankLabel(RankLabel label) {
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setValignment(label, VPos.BOTTOM);
    }

    void addToGrid(TalentTree talentTree, Talent talent) {
        GridPane gridPane = this.talentTreeGridPanes.get(talentTree.getId());

        gridPane.add(talent.getImageView(), talent.getColumnPosition(), talent.getRowPosition());
        gridPane.add(talent.getRankLabel(), talent.getColumnPosition(), talent.getRowPosition());
    }
    void addToGrid(TalentTree talentTree, Arrow.Part part) {
        GridPane gridPane = this.talentTreeGridPanes.get(talentTree.getId());

        gridPane.add(part.getNode(), part.getColumnPosition(), part.getRowPosition());
    }
    void addToGrid(Note note) {
        if (note.getState() != ItemState.EMPTY) note.updateTooltip();

        this.noteGrid.add(note.getImageView(), note.getColumnPosition(), note.getRowPosition());
    }
}
