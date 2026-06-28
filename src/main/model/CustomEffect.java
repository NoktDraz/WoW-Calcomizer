package main.model;

import javafx.scene.effect.*;
import javafx.scene.paint.Color;
import main.controllers.UtilityFunction;

public final class CustomEffect {
    public static final ColorAdjust DESATURATE = new ColorAdjust(0, -0.75, 0, 0);
    public static final ColorAdjust DESATURATE_FULL = new ColorAdjust(0,-1,0,0);
    public static final ColorAdjust DARKEN = new ColorAdjust(0, 0, -0.2, 0);
    public static final SepiaTone FILTER = new SepiaTone(0.6);
    public static final Glow HIGHLIGHT = new Glow(0.6);
    public static final InnerShadow INVALID = new InnerShadow(BlurType.ONE_PASS_BOX, Color.RED, 0, 0.5, 0, 0);
    public static final InnerShadow VALID = new InnerShadow(BlurType.ONE_PASS_BOX, Color.GREEN, 0, 0.5, 0, 0);
    public static final Effect LOCKED = UtilityFunction.Effects.constructEffectChain(DESATURATE_FULL, FILTER, DARKEN);
    public static final DropShadow SHADOW_CLASS_BUTTON_UP = new DropShadow(BlurType.ONE_PASS_BOX, Color.BLACK.deriveColor(1, 1, 1, 0.6), 0, 0.6, -1, 3);
    public static final DropShadow SHADOW_CLASS_BUTTON_DOWN = new DropShadow(BlurType.ONE_PASS_BOX, Color.BLACK.deriveColor(1, 1, 1, 0.6), 0, 0.8, -1, 1);
    public static final DropShadow SHADOW_ITEM_CONTAINER_PANE = new DropShadow(BlurType.ONE_PASS_BOX, Color.BLACK.deriveColor(1, 1, 1, 0.65), 0, 0.6, -1, 2);
    public static final DropShadow SHADOW_TALENT_POINTS = new DropShadow(BlurType.ONE_PASS_BOX, Color.BLACK.deriveColor(1, 1, 1, 0.8), 0, 0.6, -0.5, 1);
    public static final DropShadow SHADOW_CUSTOMIZATION_SET = new DropShadow(BlurType.ONE_PASS_BOX, Color.BLACK.deriveColor(1, 1, 1, 0.8), 0, 0.6, -0.5, 1);
    public static final DropShadow SHADOW_ITEM_CUSTOMIZATION_NAME = new DropShadow(BlurType.ONE_PASS_BOX, Color.BLACK.deriveColor(1, 1, 1, 0.8), 0, 0.6, 1, 0);
    public static final DropShadow SHADOW_ITEM_CUSTOMIZATION_DESCRIPTION = new DropShadow(BlurType.ONE_PASS_BOX, Color.BLACK.deriveColor(1, 1, 1, 0.65), 0, 0.6, 1, 2);

    static {
        INVALID.setHeight(50);
        INVALID.setWidth(50);
        VALID.setWidth(50);
        VALID.setHeight(50);
        SHADOW_CLASS_BUTTON_UP.setHeight(10);
        SHADOW_CLASS_BUTTON_UP.setWidth(25);
        SHADOW_CLASS_BUTTON_DOWN.setHeight(5);
        SHADOW_CLASS_BUTTON_DOWN.setWidth(10);
        SHADOW_ITEM_CONTAINER_PANE.setHeight(20);
        SHADOW_ITEM_CONTAINER_PANE.setWidth(30);
        SHADOW_TALENT_POINTS.setHeight(15);
        SHADOW_TALENT_POINTS.setWidth(20);
        SHADOW_CUSTOMIZATION_SET.setHeight(15);
        SHADOW_CUSTOMIZATION_SET.setWidth(20);
        SHADOW_ITEM_CUSTOMIZATION_NAME.setHeight(15);
        SHADOW_ITEM_CUSTOMIZATION_NAME.setWidth(20);
        SHADOW_ITEM_CUSTOMIZATION_DESCRIPTION.setHeight(20);
        SHADOW_ITEM_CUSTOMIZATION_DESCRIPTION.setWidth(30);
    }
}
