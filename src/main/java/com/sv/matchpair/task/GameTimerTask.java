package com.sv.matchpair.task;

import com.sv.matchpair.MatchPair;

import java.util.TimerTask;

public class GameTimerTask extends TimerTask {

    private final MatchPair mp;

    public GameTimerTask(MatchPair sbf) {
        this.mp = sbf;
    }

    @Override
    public void run() {
        mp.changeAppFont();
    }
}
