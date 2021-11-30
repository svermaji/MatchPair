package com.sv.matchpair;

import com.sv.core.Utils;

import java.util.ArrayList;
import java.util.List;

public enum UIName {
    BTN_USER("Hi", 'u', "Username, click to change"),
    LBL_USER("Username", 'u', "set Username and hit enter to save"),
    BTN_START("Start", 's', "Start/Stop the game"),
    BTN_LEVEL("Level", 'v', "Present level of the game"),
    BTN_PAUSE("Pause", 'p', "Pause/Resume the game"),
    LBL_SCORE("Score", 'l', "Score of the game"),
    LBL_TIME("Time", 'm', "Time remaining of the game"),
    MENU("Settings", 's', "Different system settings");

    String name, tip;
    char mnemonic;
    List<String> keys;

    UIName(String name, char mnemonic) {
        this(name, mnemonic, null);
    }

    UIName(String name, char mnemonic, String tip) {
        this.name = name;
        this.tip = tip;
        this.mnemonic = mnemonic;
    }

    UIName(String name, char mnemonic, String addlKey, String tip) {
        this(name, mnemonic, tip);
        keys = new ArrayList<>();
        keys.add(mnemonic + "");
        if (Utils.hasValue(addlKey)) {
            keys.add(addlKey);
        }
    }
}
