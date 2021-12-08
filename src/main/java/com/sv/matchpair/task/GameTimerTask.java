package com.sv.matchpair.task;

import com.sv.core.Utils;
import com.sv.matchpair.MatchPair;

import java.util.TimerTask;

public class GameTimerTask extends TimerTask {

    private final MatchPair mp;

    public GameTimerTask(MatchPair mp) {
        this.mp = mp;
    }

    @Override
    public void run() {
        System.out.println(mp.isGameStart());
        if (mp.isGameStart()) {
            mp.updateGameTime();
        }
        if (mp.isGamePaused()) {
            mp.performPauseAction();
        }
    }
}
