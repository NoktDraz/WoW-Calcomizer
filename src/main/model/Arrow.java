package main.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.controllers.UtilityFunction;
import main.enums.ArrowPartDirection;
import main.enums.ItemState;
import main.enums.Resource;

import java.util.LinkedList;

public class Arrow {
    public static final Image CAP = UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.ARROW_CAP);
    public static final Image BAR_PART = UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.ARROW_BAR);
    public static final Image CORNER_PART = UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.ARROW_CORNER);

    private int dependantIndex;
    private LinkedList<Part> arrowParts;
    private ItemState state;
    
    public Arrow(int dependantIndex) {
        this.arrowParts = new LinkedList<>();
        this.dependantIndex = dependantIndex;
    }
    public LinkedList<Part> getParts() { return this.arrowParts; }

    public class Part {

        private ImageView node;
        private Image partType;
        private int positionIndex;
        private ArrowPartDirection direction;

        public Part(int positionIndex) {   // Constructor for uncapped arrow parts
            this.positionIndex = positionIndex;
            this.node = new ImageView();
            this.node.setViewOrder(4);
            arrowParts.add(this);
        }

        public Part(int positionIndex, Image partType, ArrowPartDirection direction) {   // Constructor exclusively for capped arrow parts
            this.positionIndex = positionIndex;
            this.partType = partType;
            this.direction = direction;
            this.node = new ImageView(partType);
            arrowParts.add(this);
        }

        public ImageView getNode() { return this.node; }
        public int getPositionIndex() { return this.positionIndex; }
        public int getRowPosition() { return (this.positionIndex / Constant.TALENTGRID_COLUMN_COUNT); }
        public int getColumnPosition() { return (this.positionIndex % Constant.TALENTGRID_COLUMN_COUNT); }
        public void setPartType(Image partType) {
            this.partType = partType;
            this.node.setImage(partType);
        }
        public void setDirection(ArrowPartDirection direction) { this.direction = direction; }
        public ArrowPartDirection getDirection() { return this.direction; }
        public Image getPartType() { return this.partType; }
    }

    public int getIndex() { return this.dependantIndex; }
    public void setState(ItemState state) {
        switch (state) {
            case LOCKED -> this.arrowParts.forEach(part -> part.getNode().setEffect(CustomEffect.LOCKED));
            case OPEN -> this.arrowParts.forEach(part -> part.getNode().setEffect(null));
        }
    }
}
