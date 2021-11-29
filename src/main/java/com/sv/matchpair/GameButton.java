package com.sv.matchpair;

import com.sv.swingui.component.AppButton;

import javax.swing.border.LineBorder;
import java.awt.*;

public final class GameButton extends AppButton {

    public GameButton(String text) {
        super(text);
        int gap = 20;
        setOpaque(true);
        setBackground(Color.white);
        setMargin(new Insets(gap, gap, gap, gap));
        setBorder(new LineBorder(Color.blue, 1, true));
    }
}
