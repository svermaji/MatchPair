package com.sv.matchpair;

import com.sv.swingui.SwingUtils;
import com.sv.swingui.component.AppButton;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class GameButton extends AppButton {

    public GameButton(String text, Color fg) {
        super(text);
        setForeground(fg);
        int gap = 20;
        setOpaque(true);
        setMargin(new Insets(gap, gap, gap, gap));
        setBorder(new LineBorder(Color.blue, 1, true));
        setBackground(AppConstants.GAME_BTN_COLOR);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isVisible()) {
                        setBackground(
                                getBackground().equals(AppConstants.GAME_BTN_COLOR) ?
                                        AppConstants.GAME_BTN_CLICK_COLOR : AppConstants.GAME_BTN_COLOR
                        );
                    }
                }
            }
        });
    }
}
