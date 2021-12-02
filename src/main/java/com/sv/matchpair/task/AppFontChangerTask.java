package com.sv.matchpair.task;

import com.sv.matchpair.MatchPair;

import java.util.TimerTask;

public class AppFontChangerTask extends TimerTask {

    private final MatchPair mp;

    public AppFontChangerTask(MatchPair sbf) {
        this.mp = sbf;
    }

    @Override
    public void run() {
        mp.changeAppFont();
    }
}
