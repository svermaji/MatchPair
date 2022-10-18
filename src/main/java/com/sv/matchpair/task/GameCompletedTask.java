package com.sv.matchpair.task;

import com.sv.matchpair.MatchPair;

import java.util.TimerTask;

public class GameCompletedTask extends TimerTask {

    private final MatchPair mp;

    public GameCompletedTask(MatchPair mp) {
        this.mp = mp;
    }

    @Override
    public void run() {
        mp.gameCompletedActions();
    }
}
