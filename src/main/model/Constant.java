package main.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Constant {
    public static final int INITIAL_TALENT_POINTS = 51;
    public static final int POINTS_REQUIRED_PER_ROW = 5;
    public static final int GRID_ROW_COUNT = 7;
    public static final int TALENTGRID_COLUMN_COUNT = 4;
    public static final int NOTEGRID_COLUMN_COUNT = 2;
    public static final int TALENTGRID_ROW_STEP = 4;
    public static final int NOTEGRID_ROW_STEP = 2;
    public static final int COLUMN_STEP = 1;
    public static final List<Boolean> ROW_LOCKS_INITIAL_STATE = new ArrayList<>(Collections.nCopies(GRID_ROW_COUNT, Boolean.TRUE));
    public static final List<Integer> POINTS_IN_ROWS_INITIAL_STATE = new ArrayList<>(Collections.nCopies(GRID_ROW_COUNT, 0));

    static {
        ROW_LOCKS_INITIAL_STATE.set(0, false);
    }
}
