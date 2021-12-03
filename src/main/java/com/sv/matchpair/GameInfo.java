package com.sv.matchpair;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public final class GameInfo {

    private String gameLevel;
    private int rows, cols, matchScore;
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

    public Color[] getColorsRandomly() {
        Random rand = new Random();
        int len = colors.length;
        Color[] cr = new Color[len];
        for (int i = 0; i < len; i++) {
            cr[i] = colors[rand.nextInt(len)];
        }
        return cr;
        /*for (int i = 0; i < len; i++) {
            Color c = colors[rand.nextInt(len)];
            if (!cr.contains(c)) {
                cr.add(c);
            }
        }
        return cr.toArray(new Color[0]);*/

    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "gameLevel='" + gameLevel + '\'' +
                ", rows=" + rows +
                ", cols=" + cols +
                ", matchScore=" + matchScore +
                ", colors=" + Arrays.toString(colors) +
                '}';
    }
}
