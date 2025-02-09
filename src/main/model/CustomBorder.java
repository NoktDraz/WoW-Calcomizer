package main.model;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import main.controllers.UtilityFunction;

public abstract class CustomBorder extends Effect {
    public static final DropShadow BASE = new DropShadow(BlurType.ONE_PASS_BOX, Color.GRAY, 0.0,1.0,0.0,0.0);
    public static final DropShadow CLASS_ICON = new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.66, 0,0);

    static { BASE.setWidth(15); BASE.setHeight(15); }

    public static final DropShadow LOCKED = UtilityFunction.clone.Effect(BASE);
    public static final DropShadow OPEN = UtilityFunction.clone.Effect(BASE);
    public static final DropShadow MAXED = UtilityFunction.clone.Effect(BASE);
    public static final DropShadow NOTE = UtilityFunction.clone.Effect(BASE);
    public static final DropShadow ERROR = UtilityFunction.clone.Effect(BASE);

    static {
        LOCKED.setInput(CustomEffect.LOCKED);

        OPEN.setColor(Color.LIME);
        OPEN.setRadius(6);

        MAXED.setColor(Color.GOLD);

        NOTE.setColor(Color.LIGHTSEAGREEN);

        ERROR.setColor(Color.RED);
        ERROR.setRadius(6);
    }

    public static final DropShadow LOCKED_HIGHLIGHT = (DropShadow) UtilityFunction.Effects.constructEffectChain(LOCKED, CustomEffect.HIGHLIGHT);
    public static final DropShadow OPEN_HIGHLIGHT = (DropShadow) UtilityFunction.Effects.constructEffectChain(OPEN, CustomEffect.HIGHLIGHT);
    public static final DropShadow MAXED_HIGHLIGHT = (DropShadow) UtilityFunction.Effects.constructEffectChain(MAXED, CustomEffect.HIGHLIGHT);
    public static final DropShadow NOTE_HIGHLIGHT = (DropShadow) UtilityFunction.Effects.constructEffectChain(NOTE, CustomEffect.HIGHLIGHT);
    public static final DropShadow CLASS_BUTTON_UNSELECTED = (DropShadow) UtilityFunction.Effects.constructEffectChain(CLASS_ICON, CustomEffect.DESATURATE_FULL);
}
