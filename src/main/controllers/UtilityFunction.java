package main.controllers;

import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.enums.ArrowPartOrientation;
import main.enums.CharacterClass;
import main.enums.Resource;
import main.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public final class UtilityFunction {
    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    private UtilityFunction() {}

    public static class clone {
        public static ImageView ImageView(ImageView imageView) {
            ImageView imageViewClone = new ImageView(imageView.getImage());
            imageViewClone.effectProperty().set(imageView.getEffect());
            imageViewClone.cursorProperty().set(imageView.getCursor());
            imageViewClone.fitHeightProperty().set(imageView.getFitHeight());
            imageViewClone.fitWidthProperty().set(imageView.getFitWidth());
            imageViewClone.viewOrderProperty().set(imageView.getViewOrder());
            imageViewClone.rotateProperty().set(imageView.getRotate());
            imageViewClone.scaleXProperty().set(imageView.getScaleX());
            imageViewClone.translateXProperty().set(imageView.getTranslateX());
            imageViewClone.translateYProperty().set(imageView.getTranslateY());

            return imageViewClone;
        }

        public static Effect Effect(Effect effect) {
            if (effect.getClass() == ColorAdjust.class) return clone.Effect((ColorAdjust) effect);
            else if (effect.getClass() == SepiaTone.class) return clone.Effect((SepiaTone) effect);
            else if (effect.getClass() == Glow.class) return clone.Effect((Glow) effect);
            else if (effect.getClass() == DropShadow.class) return clone.Effect((DropShadow) effect);
            else if (effect.getClass() == InnerShadow.class) return clone.Effect((InnerShadow) effect);

            return null;
        }
        public static ColorAdjust Effect(ColorAdjust effect) { return new ColorAdjust(effect.getHue(), effect.getSaturation(), effect.getBrightness(), effect.getContrast()); }
        public static SepiaTone Effect(SepiaTone effect) {
            return new SepiaTone(effect.getLevel());
        }
        public static Glow Effect(Glow effect) {
            return new Glow(effect.getLevel());
        }
        public static InnerShadow Effect(InnerShadow effect) {
            InnerShadow innerShadowClone = new InnerShadow(effect.getBlurType(), effect.getColor(), effect.getRadius(), effect.getChoke(), effect.getOffsetX(), effect.getOffsetY());

            innerShadowClone.setHeight(effect.getHeight());
            innerShadowClone.setWidth(effect.getWidth());
            return innerShadowClone;
        }
        public static DropShadow Effect(DropShadow effect) {
            DropShadow dropShadowClone = new DropShadow(effect.getBlurType(), effect.getColor(), effect.getRadius(), effect.getSpread(), effect.getOffsetX(), effect.getOffsetY());

            dropShadowClone.setHeight(effect.getHeight());
            dropShadowClone.setWidth(effect.getWidth());
            return dropShadowClone;
        }
    }
    public static class Effects {
        private static EffectInputChain effectChain; // "Delegate", Effect.setInput() is not public in the base class

        public static Effect constructEffectChain(Effect... effects) {
            Iterator<Effect> effectIterator = Arrays.stream(effects).iterator();
            Effect effectChainBase = clone.Effect(effectIterator.next());
            identify(effectChainBase);

            Effect currentEffect;
            while (effectIterator.hasNext()) {
                currentEffect = clone.Effect(effectIterator.next());
                effectChain.add(currentEffect);
                identify(currentEffect);
            }

            return effectChainBase;
        }

        private static void identify(Effect effect) {
            if (effect.getClass() == ColorAdjust.class) effectChain = ((ColorAdjust) effect)::setInput;
            else if (effect.getClass() == SepiaTone.class) effectChain = ((SepiaTone) effect)::setInput;
            else if (effect.getClass() == Glow.class) effectChain = ((Glow) effect)::setInput;
            else if (effect.getClass() == DropShadow.class) effectChain = ((DropShadow) effect)::setInput;
            else if (effect.getClass() == InnerShadow.class) effectChain = ((InnerShadow) effect)::setInput;
        }
    }
    public static class Arrows {
        public static Arrow constructArrowFromGridIndexes(int prerequisiteIndex, int dependantIndex, TalentTree talentTree) {
            Arrow arrow = new Arrow(dependantIndex);
            LinkedList<Integer> positionIndexes = resolvePositionIndexes(dependantIndex, prerequisiteIndex, talentTree);

            int previousPositionIndex = positionIndexes.poll();
            int currentPositionIndex = positionIndexes.poll();

            // Creates (and adds to the arrow) a capped part
            arrow.new Part(currentPositionIndex, Arrow.CAP, resolvePartOrientation(Arrow.CAP, previousPositionIndex, currentPositionIndex, 0));

            while (positionIndexes.isEmpty() == false) {
                Arrow.Part arrowPart = arrow.new Part(currentPositionIndex);
                int nextPositionIndex = positionIndexes.peek();

                arrowPart.setPartType(resolvePartType(previousPositionIndex, nextPositionIndex));
                arrowPart.setDirection(resolvePartOrientation(arrowPart.getPartType(), previousPositionIndex, currentPositionIndex, nextPositionIndex));

                previousPositionIndex = currentPositionIndex;
                currentPositionIndex = positionIndexes.poll();
            }

            assemble(arrow);
            return arrow;
        }
        private static void assemble(Arrow arrow) {
            // Orients the image nodes of the arrow's parts
            arrow.getParts().forEach(part -> {
                if (part.getPartType() == Arrow.CORNER_PART) {
                    switch (part.getDirection()) {
                        case LEFT_DOWN -> part.getNode().setScaleX(-1);
                        case DOWN_LEFT -> {
                            part.getNode().setRotate(90);
                            part.getNode().setTranslateX(-1);   // Slight nudge to fix visual offset
                        }
                        case DOWN_RIGHT -> {
                            part.getNode().setScaleX(-1);
                            part.getNode().setRotate(270);
                            part.getNode().setTranslateX(1);    // Slight nudge to fix visual offset
                        }
                    }
                } else {
                    switch (part.getDirection()) {
                        case RIGHT -> part.getNode().setRotate(-90);
                        case LEFT -> part.getNode().setRotate(90);
                    }
                }
            });
        }
        private static Image resolvePartType(int previousIndex, int nextIndex) {
            Image partType = Arrow.BAR_PART;

            if (previousIndex % Constant.TALENTGRID_ROW_STEP != nextIndex % Constant.TALENTGRID_ROW_STEP) {
                partType = Arrow.CORNER_PART;
            }

            return partType;
        }
        private static ArrowPartOrientation resolvePartOrientation(Image partType, int previousIndex, int currentIndex, int nextIndex) {
            ArrowPartOrientation direction = ArrowPartOrientation.DOWN;

            if (partType == Arrow.CORNER_PART) {
                switch (currentIndex - nextIndex) {
                    case Constant.COLUMN_STEP -> direction = ArrowPartOrientation.RIGHT_DOWN;
                    case -Constant.COLUMN_STEP -> direction = ArrowPartOrientation.LEFT_DOWN;
                    case Constant.TALENTGRID_ROW_STEP -> {
                        if (previousIndex - currentIndex == Constant.COLUMN_STEP) {
                            direction = ArrowPartOrientation.DOWN_RIGHT;
                        } else {
                            direction = ArrowPartOrientation.DOWN_LEFT;
                        }
                    }
                }
            } else {
                switch (previousIndex - currentIndex) {
                    case Constant.COLUMN_STEP -> direction = ArrowPartOrientation.RIGHT;
                    case -Constant.COLUMN_STEP -> direction = ArrowPartOrientation.LEFT;
                }
            }

            return direction;
        }
        private static LinkedList<Integer> resolvePositionIndexes(int startIndex, int endIndex, TalentTree talentTree) {
            LinkedList<Integer> positionIndexes = new LinkedList<>();
            int currentIndex = startIndex;
            positionIndexes.add(currentIndex);

            while (currentIndex - endIndex != 0) {
                switch (currentIndex % Constant.TALENTGRID_COLUMN_COUNT - endIndex % Constant.TALENTGRID_COLUMN_COUNT) {
                    // Prerequisite is offset to the right
                    case -Constant.COLUMN_STEP:
                        // Is there a talent blocking the slot to the left of the prerequisite
                        if (talentTree.getTalentByIndex(endIndex - Constant.COLUMN_STEP) != null || currentIndex - endIndex == -1) {
                            currentIndex += Constant.COLUMN_STEP;
                        } else {
                            currentIndex -= Constant.TALENTGRID_ROW_STEP;
                        }

                        break;

                    // Straight below the prerequisite
                    case 0:
                        currentIndex -= Constant.TALENTGRID_ROW_STEP;
                        break;

                    // Prerequisite is offset to the left
                    case Constant.COLUMN_STEP:
                        // Is there a talent blocking the slot to the right of the prerequisite
                        if (talentTree.getTalentByIndex(endIndex + Constant.COLUMN_STEP) != null || currentIndex - endIndex == 1) {
                            currentIndex -= Constant.COLUMN_STEP;
                        } else {
                            currentIndex -= Constant.TALENTGRID_ROW_STEP;
                        }

                        break;
                }

                positionIndexes.add(currentIndex);
            }

            return positionIndexes;
        }
    }
    public static class Resources {
        public static HashMap<CharacterClass, InputStream> getDefaultDataStreams() throws IOException {
            HashMap<CharacterClass, InputStream> dataFileStreams = new HashMap<>();

            for (CharacterClass cls : CharacterClass.values()) {
                dataFileStreams.put(cls, loader.getResource(Resource.Folder.DEFAULT_DATASET.getPath() + cls.name().toLowerCase() + ".xml").openStream());
            }

            return dataFileStreams;
        }
        public static File getCustomDataSetsFolder() {
            return new File(Resource.Folder.PUBLIC_DATASETS.getPath());
        }

        public static Image getInterfaceAsset(Resource.InterfaceAsset asset) {
            return new Image(loader.getResourceAsStream(asset.getPath()));
        }
        public static Image getPublicIcon(String fileName) {
            File imageFile = new File(Resource.Folder.PUBLIC_ICONS.getPath() + fileName);

            if (imageFile.isFile())
                return new Image(imageFile.getAbsolutePath());
            else
                return GridItem.ICON_DEFAULT;
        }
        public static Background getBackgroundImage(String fileName) {
            String path = Resource.Folder.BACKGROUNDS.getPath() + fileName;

            try {
                return new Background(
                        new BackgroundImage(
                                new Image(loader.getResourceAsStream(path)),
                                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                new BackgroundSize(1,1,true,true,false,false)
                        )
                );
            } catch (Exception e) {
                return Background.fill(Color.BLACK);
            }
        }
        public static Font CustomFont(FontWeight fontWeight, double textSize) {
            Resource.Font font = Resource.Font.REGULAR;
            if (fontWeight == FontWeight.BOLD) font = Resource.Font.BOLD;

            return Font.loadFont(loader.getResourceAsStream(font.getPath()), textSize);
        }
    }
}
