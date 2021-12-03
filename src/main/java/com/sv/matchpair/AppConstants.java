package com.sv.matchpair;

import com.sv.core.Constants;

import java.awt.*;

public final class AppConstants {

    private AppConstants() {
    }

    public static final int GAME_START_TIME_SEC = 3;
    public static final int ALARM_TIME_SEC = 5;
    public static final int GAME_TIME_SEC = 80;
    public static final int DEFAULT_TABLE_ROWS = 8;
    public static final int GAME_SEQ_LIMIT_MAX = 8;
    public static final int GAME_SEQ_LIMIT_MIN = 2;
    public static final int PAIRS_COUNT = 3;
    public static final String SCORE_DATA_SEP = Constants.PIPE;
    public static final String SCORE_DATA_SEP_FOR_SPLIT = Constants.SLASH + SCORE_DATA_SEP;
    public static final String SCORE_SEP = Constants.SEMI_COLON;
    public static final String PROP_SCORES_SUFFIX = "-scores";
    public static final Character[] GAME_CHARS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    public static final Color GAME_BTN_COLOR = Color.white;
    public static final Color GAME_BTN_CLICK_COLOR = Color.lightGray;
}
