package main.controllers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Service;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import main.enums.Resource;
import main.enums.Window;
import main.model.*;

import java.util.ArrayList;
import java.util.Arrays;

public class TalentCustomizerController extends ItemCustomizerBase {
    private Talent currentTalent;
    private SimpleIntegerProperty prerequisiteId;
    private Text prerequisiteText;
    private TextArea[] rankTextAreas;
    private DoubleProperty tooltipOffset;
    private DoubleProperty tooltipX;
    private DoubleProperty tooltipY;

    public TalentCustomizerController() {}

    //region FXML Injection
    @FXML
    private ImageView prerequisiteIcon;
    @FXML
    private Pane prerequisitePane;
    @FXML
    private ImageView prerequisiteClear;
    @FXML
    private Label prerequisiteName;
    @FXML
    private TextArea rank1;
    @FXML
    private TextArea rank2;
    @FXML
    private TextArea rank3;
    @FXML
    private TextArea rank4;
    @FXML
    private TextArea rank5;
    //endregion

    @FXML
    public void initialize() {
        super.initialize();

        this.rankTextAreas = new TextArea[]{ rank1, rank2, rank3, rank4, rank5 };
        for (TextArea rankText : this.rankTextAreas) {
            rankText.setFont(CustomFont.ITEM_TEXT);
        }
        this.prerequisiteIcon.setEffect(CustomBorder.OPEN);
        this.prerequisiteIcon.setCursor(CustomCursor.INTERACT);
        this.prerequisiteIcon.setOnMouseEntered(event -> this.prerequisiteIcon.setEffect(CustomBorder.OPEN_HIGHLIGHT));
        this.prerequisiteIcon.setOnMouseExited(event -> this.prerequisiteIcon.setEffect(CustomBorder.OPEN));
        this.prerequisiteText = new Text();
        this.prerequisiteText.setFont(CustomFont.ITEM_NAME);
        this.prerequisiteText.setFill(Color.WHITE);
        this.prerequisiteText.setStroke(Color.RED);
        this.prerequisiteText.setStrokeWidth(0);
        this.prerequisiteName.setGraphic(prerequisiteText);
        this.prerequisiteClear.setImage(UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.GLYPH_CLEAR));
        this.prerequisiteClear.setEffect(CustomEffect.DESATURATE);
        this.prerequisiteClear.setCursor(CustomCursor.INTERACT);
        this.prerequisiteClear.setOnMouseEntered(event -> {
            this.prerequisiteIcon.setEffect(CustomEffect.INVALID);
            this.prerequisiteClear.setEffect(null);
            this.prerequisiteText.setStrikethrough(true);
            this.prerequisiteText.setStrokeWidth(0.5);
        });
        this.prerequisiteClear.setOnMouseExited(event -> {
            this.prerequisiteIcon.setEffect(CustomBorder.OPEN);
            this.prerequisiteClear.setEffect(new ColorAdjust(0,-0.75, 0,0));
            this.prerequisiteText.setStrikethrough(false);
            this.prerequisiteText.setStrokeWidth(0);
        });
        this.prerequisiteId = new SimpleIntegerProperty(-1);
    }
    @FXML
    public void finish() {
        if (super.validateFinish(this.rankTextAreas)) {
            this.currentTalent.setPrerequisiteIndex(this.prerequisiteId.intValue());
            this.currentTalent.getRankData().clear();
            ArrayList<TextArea> textAreas = new ArrayList<>(Arrays.stream(this.rankTextAreas).filter(textArea -> !textArea.getText().trim().isEmpty()).toList());
            textAreas.forEach(textArea -> this.currentTalent.getRankData().add(textArea.getText().trim()));
            this.currentTalent.getRankLabel().setVisible(true);

            super.finish();
        } else {
            Event.fireEvent(this.anchor, new CustomEvent(CustomEvent.SHOW_ALERT, this.errorMessage, Alert.AlertType.ERROR, Window.TALENT_CUSTOMIZER));
        }
    }
    @FXML
    public void delete() {
        if (currentTalent.hasDependants()) {
            currentTalent.getDependantIndexes().forEach(dependantIndex -> {
                ((Talent) currentItemContainer.getItemByIndex(dependantIndex)).setPrerequisiteIndex(-1);
            });
            currentTalent.getDependantIndexes().clear();
        }
        if (currentTalent.hasPrerequisite()) {
            ((Talent) currentItemContainer.getItemByIndex(currentTalent.getPrerequisiteIndex())).removeDependant(currentTalent.getIndex());
            currentTalent.setPrerequisiteIndex(-1);
        }

        super.delete();
    }

    @FXML
    public void choosePrerequisite() {
        Event.fireEvent(this.anchor, new CustomEvent(CustomEvent.CHOOSE_PREREQUISITE, (TalentTree) this.currentItemContainer, this.currentTalent, this.icon));
    }
    @FXML
    public void clearPrerequisite() {
        this.prerequisiteId.set(-1);
        this.prerequisiteIcon.setImage(GridItem.ICON_DEFAULT);
        this.prerequisitePane.setVisible(false);
    }

    public void setTalentObjects(TalentTree talentTree, Talent talent) {
        this.reset();

        this.currentTalent = talent;
        this.currentItemContainer = talentTree;

        super.setItem(this.currentItemContainer, this.currentTalent);

        for (int i = 0; i < this.currentTalent.getRankData().size(); i++) {
            this.rankTextAreas[i].setText(this.currentTalent.getRankData().get(i));
        }

        if (talent.hasPrerequisite()) this.setPrerequisite(talent.getPrerequisiteIndex());
    }

    public void setPrerequisite(int index) {
        this.prerequisiteId.set(index);
        this.prerequisitePane.setVisible(true);
        this.prerequisiteIcon.setImage(this.currentItemContainer.getItemByIndex(index).getIconImage());
        this.prerequisiteText.setText(this.currentItemContainer.getItemByIndex(index).getItemName());
        this.prerequisiteName.autosize();
        this.prerequisiteClear.setTranslateX(this.prerequisiteName.getWidth() - this.prerequisiteClear.getFitWidth());
    }

    void reset() {
        super.reset();
        this.clearPrerequisite();

        Arrays.stream(this.rankTextAreas).forEach(textArea -> textArea.setText(""));
    }
}
