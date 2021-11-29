package com.sv.matchpair;

import java.awt.*;
import java.util.Arrays;

public final class GameInfo {

    private String gameLevel;
    private int rows, cols;
    private Color colors[];

    public String getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(String gameLevel) {
        this.gameLevel = gameLevel;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public Color[] getColors() {
        return colors;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "gameLevel='" + gameLevel + '\'' +
                ", rows=" + rows +
                ", cols=" + cols +
                ", colors=" + Arrays.toString(colors) +
                '}';
    }
}
