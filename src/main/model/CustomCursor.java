package main.model;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import main.controllers.UtilityFunction;
import main.enums.Resource;

public final class CustomCursor {
    public static final Cursor INTERACT = new ImageCursor(UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.CURSOR_INTERACT));
    public static final Cursor DEFAULT = new ImageCursor(UtilityFunction.Resources.getInterfaceAsset(Resource.InterfaceAsset.CURSOR_DEFAULT));
}
