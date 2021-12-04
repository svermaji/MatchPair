package com.sv.matchpair;

import com.sv.swingui.component.AppButton;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class GameButton extends AppButton {

    private int rowNum = -1, colNum = -1;

    public GameButton(String text, Color fg, MatchPair matchPair) {
        super(text);
        setForeground(fg);
        int gap = 20;
        setOpaque(true);
        setMargin(new Insets(gap, gap, gap, gap));
        setBorder(new LineBorder(Color.blue, 1, true));
        setBackground(AppConstants.GAME_BTN_COLOR);
        GameButton thisObj = this;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isVisible() && isEnabled()) {
                        setBackground(
                                getBackground().equals(AppConstants.GAME_BTN_COLOR) ?
                                        AppConstants.GAME_BTN_CLICK_COLOR : AppConstants.GAME_BTN_COLOR
                        );
                        matchPair.checkGameButton(thisObj);
                    }
                }
            }
        });
    }

    public boolean isClicked() {
        return getBackground().equals(AppConstants.GAME_BTN_CLICK_COLOR);
    }

    public void setGamePosition(int rowNum, int colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    @Override
    public String toString() {
        return "GameButton{" +
                "text=" + getText() +
                ", rowNum=" + rowNum +
                ", colNum=" + colNum +
                '}';
    }
}
