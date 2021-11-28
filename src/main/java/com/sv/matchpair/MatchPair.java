package com.sv.matchpair;

import com.sv.core.Utils;
import com.sv.core.config.DefaultConfigs;
import com.sv.core.logger.MyLogger;
import com.sv.swingui.KeyActionDetails;
import com.sv.swingui.SwingUtils;
import com.sv.swingui.component.*;
import com.sv.swingui.component.table.AppTable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

import static com.sv.swingui.UIConstants.*;

/**
 * Java Game as MatchPair
 */
public class MatchPair extends AppFrame {

    /**
     * This is config and program will search getter
     * of each enum to store in config file.
     * <p>
     * e.g. if enum is Xyz then when storing getXyz will be called
     */
    public enum Configs {
        AppFontSize
    }

    public enum Status {
        NOT_STARTED, START, PAUSED, STOP
    }

    private MyLogger logger;
    private DefaultConfigs configs;

    private TitledBorder titledBorder;
    private AppMenu menu;
    private AppButton btnStart, btnStop, btnPause, btnExit;
    private AppLabel lblTime, lblScore;
    private AppTable tblTopScore, tblRecentScore;
    private AppPanel topPanel, buttonsPanel;
    private List<Timer> timers = new ArrayList<>();
    private Timer timerScore = null;
    private ColorsNFonts[] appColors = SwingUtils.getFilteredCnF(false);

    private Status gameStatus = Status.NOT_STARTED;
    private String username, fontName;
    private int gameLevel, cnfIdx = 0;

    private JComponent[] componentsToColor;
    private final String TITLE_HEADING = "Controls";
    private final int GAME_TIME_SEC = 120;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatchPair().initComponents());
    }

    public MatchPair() {
        super("Match Pair");
    }

    /**
     * This method initializes the form.
     */
    private void initComponents() {
        logger = MyLogger.createLogger(getClass());
        configs = new DefaultConfigs(logger, Utils.getConfigsAsArr(Configs.class));
        logger.setSimpleClassName(true);

        super.setLogger(logger);

        appFontSize = Utils.validateInt(configs.getIntConfig(Configs.AppFontSize.name()),
                DEFAULT_APPFONTSIZE, MIN_APPFONTSIZE, MAX_APPFONTSIZE);

        logger.info("appFontSize " + Utils.addBraces(appFontSize));

        btnExit = new AppExitButton();
        btnExit.addActionListener(evt -> exitForm());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exitForm();
            }
        });

        Container parentContainer = getContentPane();
        parentContainer.setLayout(new BorderLayout());

        topPanel = new AppPanel(new BorderLayout());
        AppToolBar tbControls = new AppToolBar();
        titledBorder = SwingUtils.createTitledBorder(TITLE_HEADING, fg);
        topPanel.setBorder(titledBorder);
        UIName uin = UIName.BTN_START;
        btnStart = new AppButton(uin.name, uin.mnemonic, uin.tip);
        btnStart.addActionListener(e -> startGame());
        uin = UIName.BTN_PAUSE;
        btnPause = new AppButton(uin.name, uin.mnemonic, uin.tip);
        btnPause.addActionListener(e -> startGame());
        uin = UIName.LBL_TIME;
        lblTime = new AppLabel(uin.name, uin.mnemonic, uin.tip);
        lblTime.setHorizontalAlignment(SwingConstants.CENTER);
        btnPause.addActionListener(e -> startGame());
        uin = UIName.LBL_SCORE;
        lblScore = new AppLabel(uin.name, uin.mnemonic, uin.tip);
        lblScore.setHorizontalAlignment(SwingConstants.CENTER);
        btnPause.addActionListener(e -> startGame());
        uin = UIName.MENU;
        menu = new AppMenu(uin.name, uin.mnemonic, uin.tip);
        menu.add(SwingUtils.getColorsMenu(true, true,
                true, true, false, this, logger));
        menu.add(SwingUtils.getAppFontMenu(this, this, appFontSize, logger));

        tbControls.add(btnStart);
        tbControls.add(btnPause);
        tbControls.add(lblTime);
        tbControls.add(lblScore);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        menu.setAlignmentX(SwingUtilities.CENTER);
        menu.setSize(menuBar.getSize());
        tbControls.add(menuBar);
        tbControls.add(btnExit);
        tbControls.setLayout(new GridLayout(1, tbControls.getComponentCount()));
        tbControls.setMargin(new Insets(1, 3, 1, 3));
        topPanel.add(tbControls);
        topPanel.setSize(topPanel.getWidth(), 100);
        topPanel.setBorder(SwingUtils.createLineBorder(Color.BLUE));

        AppPanel centerPanel = new AppPanel(new BorderLayout());
        buttonsPanel = new AppPanel();
        createButtons();
        centerPanel.add(buttonsPanel);
        parentContainer.add(topPanel, BorderLayout.NORTH);
        parentContainer.add(centerPanel, BorderLayout.CENTER);

        String[] topScoreCols = new String[]{"Top Score", "User", "Date"};
        String[] recentScoreCols = new String[]{"Recent Score", "User", "Date"};
        DefaultTableModel topScoreModel = SwingUtils.getTableModel(topScoreCols);
        DefaultTableModel recentScoreModel = SwingUtils.getTableModel(recentScoreCols);
        AppTable tblTopScore = new AppTable(topScoreModel);
        AppTable tblRecentScore = new AppTable(recentScoreModel);
        setTable (tblTopScore, topScoreModel);
        setTable (tblRecentScore, recentScoreModel);
        AppPanel tblPanel = new AppPanel(new GridLayout(2, 1));
        tblPanel.add(new JScrollPane(tblTopScore));
        tblPanel.add(new JScrollPane(tblRecentScore));
        tblPanel.setBorder(EMPTY_BORDER);
        centerPanel.add(tblPanel, BorderLayout.WEST);

        componentsToColor = new JComponent[]{btnStart, btnPause, lblTime, lblScore,
                menu, btnExit, tblTopScore.getTableHeader(), tblRecentScore.getTableHeader()};
        colorChange(1);
        //changeAppColor();
        setControlsToEnable();
        addBindings();

        setToCenter();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        enableControls();
    }

    private void setTable(AppTable tbl, DefaultTableModel model) {
        tbl.setScrollProps();
        tbl.setRowHeight(appFontSize + 4);
        tbl.setBorder(EMPTY_BORDER);
        SwingUtils.removeAndCreateEmptyRows(3, 5, model);
    }

    public void changeAppFont() {
        SwingUtils.applyAppFont(this, appFontSize, this, logger);
    }

    // This will be called by reflection from SwingUI jar
    public void appFontChanged(Integer fs) {
        appFontSize = fs;
        logger.info("Application font changed to " + Utils.addBraces(fs));

        TitledBorder[] borders = {titledBorder};
        Arrays.stream(borders).forEach(t -> t.setTitleFont(SwingUtils.getNewFontSize(t.getTitleFont(), fs)));
        // calling to change tooltip font
        changeAppColor();
    }

    // This will be called by reflection from SwingUI jar
    public void colorChange(Integer x) {
        //if (isWindowActive())
        {
            cnfIdx = x;
            ColorsNFonts c = appColors[cnfIdx];
            bg = c.getBk();
            fg = c.getFg(); // foreground not working with highlighter //c.getFg();
            hbg = c.getSelbk();
            hfg = c.getSelfg();
            fontName = c.getFont();
            changeAppColor();
        }
    }

    private void changeAppColor() {
        createBorders();
        titledBorder = SwingUtils.createTitledBorder(TITLE_HEADING, fg);
        TitledBorder[] toTitleColor = {titledBorder};
        Arrays.stream(toTitleColor).forEach(t -> t.setTitleColor(fg));
        topPanel.setBorder(titledBorder);

        Arrays.stream(componentsToColor).forEach(c -> c.setBorder(SwingUtils.createLineBorder(fg)));
        SwingUtils.setComponentColor(componentsToColor, bg, fg, hbg, hfg);
        // to make exit button different
        SwingUtils.setComponentColor(btnExit, hbg, hfg, bg, fg);
        Arrays.stream(componentsToColor).forEach(c ->
                SwingUtils.applyTooltipColorNFont(c, bg, fg, SwingUtils.getNewFont(c.getFont(), fontName)));
        // will set colors for pwd screens
        setAppColors(fg, bg, hfg, hbg);
    }

    private void createButtons() {
        gameLevel = 1;
        int rows = 6;
        int cols = 6;
        BoxLayout boxlayout = new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS);
        buttonsPanel.setLayout(boxlayout);
        AppPanel btns = new AppPanel(new GridLayout(rows, cols));

        int gap = 20;
        Border EMPTY_BORDER = new EmptyBorder(new Insets(gap, gap, gap, gap));
        btns.setBorder(EMPTY_BORDER);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                btns.add(new AppButton(i + "-" + j));
            }
        }

        buttonsPanel.add(btns);
    }

    private void createBorders() {
        Arrays.stream(componentsToColor).forEach(c -> c.setBorder(SwingUtils.createLineBorder(bg)));
    }

    private void startGame() {

    }

    /**
     * Exit the Application
     */
    private void exitForm() {
        cancelTimers();
        configs.saveConfig(this);
        setVisible(false);
        dispose();
        logger.dispose();
        System.exit(0);
    }

    private void cancelTimers() {
        timers.forEach(Timer::cancel);
    }

    private void setControlsToEnable() {
        Component[] components = {
        };
        setComponentToEnable(components);
        setComponentContrastToEnable(new Component[]{});
        enableControls();
    }

    private void addBindings() {
        final JComponent[] addBindingsTo = {};
        addKeyBindings(addBindingsTo);
    }

    private void addKeyBindings(JComponent[] addBindingsTo) {
        Action actionTxtSearch = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //SwingUtils.getInFocus(txtSearch);
            }
        };

        List<KeyActionDetails> keyActionDetails = new ArrayList<>();
        keyActionDetails.add(new KeyActionDetails(KS_CTRL_F, actionTxtSearch));

        SwingUtils.addKeyBindings(addBindingsTo, keyActionDetails);
    }
}
