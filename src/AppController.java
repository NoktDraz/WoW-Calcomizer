import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.controllers.*;
import main.enums.Window;
import main.model.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppController extends Application {
    private WindowController windowController;
    private CalculatorController calculatorController;
    private CustomizerController customizerController;
    private TalentCustomizerController talentCustomizerController;
    private NoteCustomizerController noteCustomizerController;
    private PrerequisiteController prerequisiteController;
    private NewSetController newSetController;
    private AlertMessageController alertMessageController;
    private XMLController xmlController;
    private FileIOController fileIOController;

    public AppController() {
        this.windowController = new WindowController();
        this.calculatorController = new CalculatorController();
        this.customizerController = new CustomizerController();
        this.talentCustomizerController = new TalentCustomizerController();
        this.noteCustomizerController = new NoteCustomizerController();
        this.prerequisiteController = new PrerequisiteController();
        this.newSetController = new NewSetController();
        this.alertMessageController = new AlertMessageController();
        this.xmlController = new XMLController();
        this.fileIOController = new FileIOController();
    }
    @Override
    public void start(Stage mainStage) throws Exception {
        this.loadCustomizationSetData();
        this.initStages(mainStage);
        this.initViews();

        this.customizerController.activate();

        Event.fireEvent(this.windowController.getMainStage(), new CustomEvent(CustomEvent.SET_MAIN_WINDOW, Window.CUSTOMIZER));
    }

    public void launch() { Application.launch(); }

    private void loadCustomizationSetData() throws Exception {
        CalcomizerBase.initCustomizationSets(this.xmlController.parse(UtilityFunction.Resources.getDefaultDataStreams()));

        for (File dataSetFolder : UtilityFunction.Resources.getCustomDataSetsFolder().listFiles()) {
            CalcomizerBase.addCustomizationSet(this.xmlController.parse(dataSetFolder));
        }
    }

    private void initStages(Stage mainStage) {
        this.windowController.setMainStage(mainStage);
        this.initMainStageEvents();

        this.windowController.initModalStages(new Stage(StageStyle.UTILITY), new Stage(StageStyle.TRANSPARENT));
        this.initModalStageEvents();
    }

    private void initViews() throws IOException {
        this.windowController.linkControllerToWindow(this.customizerController, Window.CUSTOMIZER);
        this.windowController.linkControllerToWindow(this.calculatorController, Window.CALCULATOR);
        this.windowController.linkControllerToWindow(this.talentCustomizerController, Window.TALENT_CUSTOMIZER);
        this.windowController.linkControllerToWindow(this.prerequisiteController, Window.PREREQUISITE);
        this.windowController.linkControllerToWindow(this.noteCustomizerController, Window.NOTE_CUSTOMIZER);
        this.windowController.linkControllerToWindow(this.newSetController, Window.NEW_SET);
        this.windowController.linkControllerToWindow(this.alertMessageController, Window.ALERT);
    }

    private void initMainStageEvents() {
        Stage mainStage = this.windowController.getMainStage();

        mainStage.addEventFilter(CustomEvent.SET_MAIN_WINDOW,
                customEvent -> {
                    Window window = (Window) customEvent.getMainObjectOfEvent();

                    if (window == Window.CUSTOMIZER) {
                        this.customizerController.activate();
                    }
                    else if (window == Window.CALCULATOR){
                        this.calculatorController.activate();
                    }

                    this.windowController.setMainWindow(window);
                    this.windowController.showMain();
                });
        mainStage.addEventFilter(CustomEvent.SAVE_ALL,
                customEvent -> {
                    this.customizerController.getActiveCustomizationSet().getClassesData().forEach((characterClass, classData) -> {
                        try {
                            this.xmlController.writeToXML(this.customizerController.getActiveCustomizationSet(), classData);
                        } catch (IOException | TransformerException | XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
        mainStage.addEventFilter(CustomEvent.SAVE_CLASS_DATA,
                customEvent -> {
                    try {
                        this.xmlController.writeToXML(
                                this.customizerController.getActiveCustomizationSet(),
                                this.customizerController.getActiveClassData()
                        );
                    } catch (IOException | TransformerException | XMLStreamException e) {
                        throw new RuntimeException(e);
                    }
                });
        mainStage.addEventFilter(CustomEvent.EXIT_APPLICATION,
                customEvent -> Platform.exit());

        mainStage.addEventFilter(CustomEvent.CUSTOMIZE_ITEM,
                customEvent -> {
                    if (customEvent.getEventObjectOfClass(GridItem.class).getClass() == Talent.class) {
                        this.windowController.setPrimaryModalWindow(Window.TALENT_CUSTOMIZER);
                        this.windowController.showPrimaryModal();

                        this.talentCustomizerController.setTalentObjects(
                                (TalentTree) customEvent.getEventObjectOfClass(ItemContainer.class),
                                (Talent) customEvent.getEventObjectOfClass(GridItem.class));

                    } else {
                        this.windowController.setPrimaryModalWindow(Window.NOTE_CUSTOMIZER);
                        this.windowController.showPrimaryModal();

                        this.noteCustomizerController.setNoteObjects(
                                (NoteCollection) customEvent.getEventObjectOfClass(ItemContainer.class),
                                (Note) customEvent.getEventObjectOfClass(GridItem.class)
                        );
                    }
                }
        );
        mainStage.addEventFilter(CustomEvent.NEW_CUSTOM_SET,
            customEvent -> {
                this.windowController.setPrimaryModalWindow(Window.NEW_SET);

                this.newSetController.setCheckBoxState(CalcomizerBase.isDefaultSetActive());

                this.windowController.showPrimaryModal();
            }
        );
    }

    private void initModalStageEvents() {
        Stage primaryModalStage = this.windowController.getPrimaryModalStage();
        Stage secondaryModalStage = this.windowController.getSecondaryModalStage();

        primaryModalStage.addEventFilter(CustomEvent.UPDATE_CONTAINER_VIEW,
                customEvent -> {
                    ItemContainer itemContainer = (ItemContainer) customEvent.getMainObjectOfEvent();

                    this.customizerController.updateView(itemContainer);
                }
        );
        primaryModalStage.addEventFilter(CustomEvent.BROWSE_ITEM_ICONS,
                customEvent -> {
                    File iconFile = this.fileIOController.showOpenDialog(primaryModalStage);

                    if (iconFile != null) {
                        ((GridItem) customEvent.getMainObjectOfEvent()).setIconName(iconFile.getName());
                    }
                }
        );
        primaryModalStage.addEventFilter(CustomEvent.CHOOSE_PREREQUISITE,
                customEvent -> {
                    this.prerequisiteController.setTalentObjects(
                            (TalentTree) customEvent.getEventObjectOfClass(TalentTree.class),
                            (Talent) customEvent.getEventObjectOfClass(Talent.class),
                            (ImageView) customEvent.getEventObjectOfClass(ImageView.class));
                    this.windowController.setSecondaryModalWindow(Window.PREREQUISITE);
                    this.windowController.showSecondaryModal();
                }
        );
        primaryModalStage.addEventFilter(CustomEvent.SHOW_ALERT,
                customEvent -> {
                    this.alertMessageController.setAlert(
                            (String) customEvent.getEventObjectOfClass(String.class),
                            (Alert.AlertType) customEvent.getEventObjectOfClass(Alert.AlertType.class));
                    this.windowController.setPrimaryModalWindow(Window.ALERT);
                    this.windowController.getPrimaryModalStage().showAndWait();
                    this.windowController.setPrimaryModalWindow((Window) customEvent.getEventObjectOfClass(Window.class));
                    this.windowController.showPrimaryModal();
                }
        );
        primaryModalStage.setOnHidden(windowEvent -> {
            if (primaryModalStage.getScene() == this.windowController.getScene(Window.PREREQUISITE)) {
                this.windowController.closeSecondaryModal();

                this.windowController.setPrimaryModalWindow(Window.TALENT_CUSTOMIZER);
                this.windowController.showPrimaryModal();
            } else if (primaryModalStage.getScene() == this.windowController.getScene(Window.NOTE_CUSTOMIZER)) {
                this.windowController.closePrimaryModal();
            } else
                this.windowController.closePrimaryModal();

            windowEvent.consume();
        });

        primaryModalStage.addEventFilter(CustomEvent.CREATE_CUSTOM_SET,
            customEvent -> {
                try {
                    File newCustomSetFolder = new File(String.valueOf(Paths.get(UtilityFunction.Resources.getCustomDataSetsFolder().getPath(), (String) customEvent.getEventObjectOfClass(String.class))));
                    Files.createDirectory(newCustomSetFolder.toPath());

                    boolean isUseCurrentAsBaseSelected = (boolean) customEvent.getEventObjectOfClass(boolean.class);
                    if (isUseCurrentAsBaseSelected && CalcomizerBase.isDefaultSetActive() == false) {
                        for (File dataFile : this.customizerController.getActiveCustomizationSet().getDataFolder().listFiles()) {
                            Files.copy(dataFile.toPath(), Path.of(newCustomSetFolder.getPath(), dataFile.getName()));
                        }
                    } else {
                        UtilityFunction.Resources.getDefaultDataStreams().forEach((cls, dataFileStream) -> {
                            try {
                                Files.copy(dataFileStream, Path.of(newCustomSetFolder.getPath(), cls.name().toLowerCase() + ".xml"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                    this.customizerController.addNewCustomizationSet(this.xmlController.parse(newCustomSetFolder));
                    this.calculatorController.initCustomizationSetMenu();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );

        secondaryModalStage.addEventFilter(CustomEvent.FINISH_PREREQUISITE,
                customEvent -> {
                    this.talentCustomizerController.setPrerequisite(
                            (int) customEvent.getMainObjectOfEvent());
                    this.windowController.setPrimaryModalWindow(Window.TALENT_CUSTOMIZER);
                    this.windowController.showPrimaryModal();
                }
        );
    }
}
