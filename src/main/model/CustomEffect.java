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

    static {
        INVALID.setHeight(50);
        INVALID.setWidth(50);
        VALID.setWidth(50);
        VALID.setHeight(50);
    }
}
