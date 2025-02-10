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
import java.util.function.Consumer;

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

    public void init() {
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
        //region Adding event filters to main and modal stages after WindowController is ready
        Consumer<Stage> mainEventHandler = stage -> {
            stage.addEventFilter(CustomEvent.SET_MAIN_WINDOW,
                customEvent -> {
                    Window window = (Window) customEvent.getMainObjectOfEvent();

                    if (window == Window.CUSTOMIZER) {
                        this.customizerController.activate();
                    }
                    else if (window == Window.CALCULATOR){
                        this.calculatorController.activate();
                    }

                    this.windowController.setMainWindow(window);
                    this.windowController.showMainStage();
                });
            stage.addEventFilter(CustomEvent.SAVE_ALL,
                    customEvent -> {
                        this.customizerController.getActiveCustomizationSet().getClassesData().forEach((characterClass, classData) -> {
                            try {
                                this.xmlController.writeToXML(this.customizerController.getActiveCustomizationSet(), classData);
                            } catch (IOException | TransformerException | XMLStreamException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    });
            stage.addEventFilter(CustomEvent.SAVE_CLASS_DATA,
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
            stage.addEventFilter(CustomEvent.EXIT_APPLICATION,
                customEvent -> Platform.exit());

            stage.addEventFilter(CustomEvent.CUSTOMIZE_ITEM,
                customEvent -> {
                    if (customEvent.getEventObjectOfClass(GridItem.class).getClass() == Talent.class) {
                        this.windowController.setModalWindow(Window.TALENT_CUSTOMIZER);
                        this.windowController.showModalWindow();

                        this.talentCustomizerController.setTalentObjects(
                                (TalentTree) customEvent.getEventObjectOfClass(ItemContainer.class),
                                (Talent) customEvent.getEventObjectOfClass(GridItem.class));

                    } else {
                        this.windowController.setModalWindow(Window.NOTE_CUSTOMIZER);
                        this.windowController.showModalWindow();

                        this.noteCustomizerController.setNoteObjects(
                            (NoteCollection) customEvent.getEventObjectOfClass(ItemContainer.class),
                            (Note) customEvent.getEventObjectOfClass(GridItem.class)
                        );
                    }
                }
            );

            stage.addEventFilter(CustomEvent.NEW_CUSTOM_SET,
                customEvent -> {
                    this.windowController.setModalWindow(Window.NEW_SET);

                    this.newSetController.setCheckBoxState(CalcomizerBase.isBaseSetActive());

                    this.windowController.showModalWindow();
                });
        };

        Consumer<Stage> modalEventHandler = stage -> {
            stage.addEventFilter(CustomEvent.UPDATE_CONTAINER_VIEW,
                    customEvent -> {
                        ItemContainer itemContainer = (ItemContainer) customEvent.getMainObjectOfEvent();

                        this.customizerController.updateView(itemContainer);
                    }
            );
            stage.addEventFilter(CustomEvent.BROWSE_ITEM_ICONS,
                customEvent -> {
                    File iconFile = this.fileIOController.showOpenDialog(stage);

                    if (iconFile != null) {
                        ((GridItem) customEvent.getMainObjectOfEvent()).setIconName(iconFile.getName());
                    }
                }
            );
            stage.addEventFilter(CustomEvent.CHOOSE_PREREQUISITE,
                customEvent -> {
                    this.prerequisiteController.setTalentObjects(
                            (TalentTree) customEvent.getEventObjectOfClass(TalentTree.class),
                            (Talent) customEvent.getEventObjectOfClass(Talent.class),
                            (ImageView) customEvent.getEventObjectOfClass(ImageView.class));
                    this.windowController.setTransparentModalWindow(Window.PREREQUISITE);
                    this.windowController.showTransparentModalWindow();
                }
            );
            stage.addEventFilter(CustomEvent.FINISH_PREREQUISITE,
                customEvent -> {
                    this.talentCustomizerController.setPrerequisite(
                            (int) customEvent.getMainObjectOfEvent());
                    this.windowController.setModalWindow(Window.TALENT_CUSTOMIZER);
                    this.windowController.showModalWindow();
                }
            );
            stage.addEventFilter(CustomEvent.SHOW_ALERT,
                    customEvent -> {
                        this.alertMessageController.setAlert(
                                (String) customEvent.getEventObjectOfClass(String.class),
                                (Alert.AlertType) customEvent.getEventObjectOfClass(Alert.AlertType.class));
                        this.windowController.setModalWindow(Window.ALERT);
                        this.windowController.getModalStage().showAndWait();
                        this.windowController.setModalWindow((Window) customEvent.getEventObjectOfClass(Window.class));
                        this.windowController.showModalWindow();
                    }
            );
            stage.setOnHidden(windowEvent -> {
                if (stage.getScene() == this.windowController.getScene(Window.PREREQUISITE)) {
                    this.windowController.closeTransparentModal();

                    this.windowController.setModalWindow(Window.TALENT_CUSTOMIZER);
                    this.windowController.showModalWindow();
                } else if (stage.getScene() == this.windowController.getScene(Window.NOTE_CUSTOMIZER)) {
                    this.windowController.closeModal();
                } else
                    this.windowController.closeModal();

                windowEvent.consume();
            });

            stage.addEventFilter(CustomEvent.CREATE_CUSTOM_SET,
                customEvent -> {
                    try {
                        File newCustomSetFolder = new File(String.valueOf(Paths.get(UtilityFunction.Resources.getCustomDataSetsFolder().getPath(), (String) customEvent.getEventObjectOfClass(String.class))));
                        Files.createDirectory(newCustomSetFolder.toPath());

                        boolean isCurrentAsBaseSelected = (boolean) customEvent.getEventObjectOfClass(boolean.class);
                        if (isCurrentAsBaseSelected && this.customizerController.getActiveCustomizationSet().getIndex() != 0) {
                            for (File dataFile : this.customizerController.getActiveCustomizationSet().getDataFolder().listFiles()) {
                                Files.copy(dataFile.toPath(), Path.of(newCustomSetFolder.getPath(), dataFile.getName()));
                            }
                        } else {
                            UtilityFunction.Resources.getBaseDataStreams().forEach((cls, dataFileStream) -> {
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
        };
        //endregion

        this.loadCustomizationSetData();

        this.windowController.setMainStage(mainStage);
        mainEventHandler.accept(this.windowController.getMainStage());
        this.windowController.initModalStages(new Stage(StageStyle.UTILITY), new Stage(StageStyle.TRANSPARENT));
        modalEventHandler.accept(this.windowController.getModalStage());
        modalEventHandler.accept(this.windowController.getTransparentModalStage());

        this.windowController.linkControllerToWindow(this.customizerController, Window.CUSTOMIZER);
        this.windowController.linkControllerToWindow(this.calculatorController, Window.CALCULATOR);
        this.windowController.linkControllerToWindow(this.talentCustomizerController, Window.TALENT_CUSTOMIZER);
        this.windowController.linkControllerToWindow(this.prerequisiteController, Window.PREREQUISITE);
        this.windowController.linkControllerToWindow(this.noteCustomizerController, Window.NOTE_CUSTOMIZER);
        this.windowController.linkControllerToWindow(this.newSetController, Window.NEW_SET);
        this.windowController.linkControllerToWindow(this.alertMessageController, Window.ALERT);

        this.customizerController.activate();
        Event.fireEvent(mainStage, new CustomEvent(CustomEvent.SET_MAIN_WINDOW, Window.CUSTOMIZER));
    }

    public static void main(String[] args) { launch(); }

    private void loadCustomizationSetData() throws Exception {
        CalcomizerBase.initCustomizationSets(this.xmlController.parse(UtilityFunction.Resources.getBaseDataStreams()));

        for (File dataSetFolder : UtilityFunction.Resources.getCustomDataSetsFolder().listFiles()) {
            CalcomizerBase.addCustomizationSet(this.xmlController.parse(dataSetFolder));
        }
    }
}
