package com.sv.matchpair;

import com.sv.core.Constants;
import com.sv.core.Utils;
import com.sv.core.config.DefaultConfigs;
import com.sv.core.logger.MyLogger;
import com.sv.matchpair.task.AppFontChangerTask;
import com.sv.matchpair.task.GameTimerTask;
import com.sv.swingui.KeyActionDetails;
import com.sv.swingui.SwingUtils;
import com.sv.swingui.component.*;
import com.sv.swingui.component.table.AppTable;
import com.sv.swingui.component.table.CellRendererCenterAlign;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sv.core.Constants.*;
import static com.sv.matchpair.AppConstants.*;
import static com.sv.matchpair.AppUtils.lastButton;
import static com.sv.swingui.UIConstants.*;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

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
        AppFontSize, GameBtnFontSize, CNFIdx, Username
    }

    public enum Status {
        NOT_STARTED, START, PAUSED, STOP
    }

    private MyLogger logger;
    private DefaultConfigs configs;
    private Map<String, GameInfo> gameInfos;
    private Map<String, GameScores> gameScores;

    private TitledBorder titledBorder;
    private JMenuBar menuBar;
    private AppMenu menu;
    private AppButton btnStart, btnUser, btnPause, btnExit;
    private AppTextField txtUser;
    private AppLabel lblTime, lblScore, lblLevel;
    private AppTable tblTopScore, tblRecentScore, tblUsers;
    private DefaultTableModel topScoreModel, recentScoreModel, userModel;
    private AppPanel topPanel, centerPanel, buttonsPanel, btnsPanel, userPanel;
    private List<Timer> timers = new ArrayList<>();
    private Timer timerScore = null;
    private ColorsNFonts[] appColors = SwingUtils.getFilteredCnF(false);
    private JComponent[] componentsToColor;
    private GameInfo gameInfo;
    private final CellRendererCenterAlign CENTER_RENDERER = new CellRendererCenterAlign();

    private Status gameStatus = Status.NOT_STARTED;
    private String username, fontName, topScores, recentScores;
    private int gameLevel = 1, gameScore, cnfIdx = 0, gameBtnFontSize;

    private final String TITLE_HEADING = "Controls";
    private final String GAME_SCORE_LOC = "./src/main/resources/scores.config";
    private final String GAME_CONFIGS_LOC = "./src/main/resources/game-configs";
    private final int MAX_NAME = 12;

    public static int gamePairMatched = 0;
    public static int gameTime = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatchPair().initComponents());
        /*MatchPair mp = new MatchPair();
        mp.username = "default";
        mp.setScoreFile();*/
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
        gameInfos = new ConcurrentHashMap<>();
        gameScores = new ConcurrentHashMap<>();
        loadConfigValues();
        loadGameConfigs();
        loadGameScores();
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
        titledBorder = SwingUtils.createTitledBorder(TITLE_HEADING, fg);
        topPanel.setBorder(titledBorder);
        UIName uin = UIName.BTN_USER;
        btnUser = new AppButton(uin.name + Constants.SPACE + username, uin.mnemonic, uin.tip);
        btnUser.addActionListener(e -> changeUsername());
        uin = UIName.LBL_USER;
        txtUser = new AppTextField(uin.name, 10);
        txtUser.setToolTipText(uin.tip);
        txtUser.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveUsername();
                }
            }
        });
        txtUser.setVisible(false);
        uin = UIName.BTN_LEVEL;
        lblLevel = new AppLabel(uin.name, uin.mnemonic, uin.tip);
        updateLevel();
        uin = UIName.BTN_START;
        btnStart = new AppButton(uin.name, uin.mnemonic, uin.tip);
        btnStart.addActionListener(e -> startGame());
        uin = UIName.BTN_PAUSE;
        btnPause = new AppButton(uin.name, uin.mnemonic, uin.tip);
        btnPause.addActionListener(e -> pauseGame());
        uin = UIName.LBL_TIME;
        lblTime = new AppLabel(uin.name, uin.mnemonic, uin.tip);
        uin = UIName.LBL_SCORE;
        lblScore = new AppLabel(uin.name, uin.mnemonic, uin.tip);
        updateScore();
        uin = UIName.MENU;
        menu = new AppMenu(uin.name, uin.mnemonic, uin.tip);
        menu.add(SwingUtils.getColorsMenu(true, true,
                true, true, false, this, logger));
        menu.add(SwingUtils.getAppFontMenu(this, this, appFontSize, logger));

        AppLabel[] lbls = {lblLevel, lblTime, lblScore};
        Arrays.stream(lbls).forEach(l -> l.setHorizontalAlignment(SwingConstants.CENTER));

        userPanel = new AppPanel();
        userPanel.setLayout(new GridLayout(1, 1));
        userPanel.add(btnUser);
        AppToolBar tbControls = new AppToolBar();
        tbControls.add(userPanel);
        tbControls.add(btnStart);
        tbControls.add(btnPause);
        tbControls.add(lblLevel);
        tbControls.add(lblTime);
        tbControls.add(lblScore);
        menuBar = new JMenuBar();
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

        centerPanel = new AppPanel(new BorderLayout());
        buttonsPanel = new AppPanel();
        createButtons();
        centerPanel.add(buttonsPanel);
        parentContainer.add(topPanel, BorderLayout.NORTH);
        parentContainer.add(centerPanel, BorderLayout.CENTER);

        setAllTables();

        componentsToColor = new JComponent[]{btnUser, txtUser, btnStart, btnPause, lblLevel, lblTime, lblScore,
                menuBar, menu, btnExit, tblTopScore.getTableHeader(), tblRecentScore.getTableHeader(),
                tblUsers.getTableHeader()
        };
        colorChange(cnfIdx);
        setControlsToEnable();
        addBindings();

        maximiseWin();
        enableControls();
        SwingUtils.getInFocus(btnStart);
        hideGamePanel();
        new Timer().schedule(new AppFontChangerTask(this), SEC_1);
    }

    private void setAllTables() {
        String[] topScoreCols = new String[]{"Top Score", "Date"};
        String[] recentScoreCols = new String[]{"Recent Score", "Date"};
        String[] userCols = new String[]{"User", "Top Score"};

        topScoreModel = SwingUtils.getTableModel(topScoreCols);
        recentScoreModel = SwingUtils.getTableModel(recentScoreCols);
        userModel = SwingUtils.getTableModel(userCols);

        tblTopScore = new AppTable(topScoreModel);
        tblRecentScore = new AppTable(recentScoreModel);
        tblUsers = new AppTable(userModel);

        setTable(tblTopScore, topScoreModel);
        setTable(tblRecentScore, recentScoreModel);
        setTable(tblUsers, userModel);
        loadTableData();

        AppPanel tblPanel = new AppPanel(new GridLayout(3, 1));
        tblPanel.add(new JScrollPane(tblTopScore));
        tblPanel.add(new JScrollPane(tblRecentScore));
        tblPanel.add(new JScrollPane(tblUsers));
        tblPanel.setBorder(EMPTY_BORDER);

        centerPanel.add(tblPanel, BorderLayout.WEST);
    }

    private void loadTableData() {
        GameScores gs = gameScores.get(username);
        if (gs != null) {
            populateScoreTbl(gs.getTopScores(), topScoreModel);
            populateScoreTbl(gs.getRecentScores(), recentScoreModel);
        }
        populateUsersTopScore(userModel);
    }

    private void populateScoreTbl(List<GameScore> list, DefaultTableModel model) {
        // empty
        model.setRowCount(0);
        int sz = list.size();
        for (int i = 0; i < sz; i++) {
            if (i < DEFAULT_TABLE_ROWS - 1) {
                GameScore gs = list.get(i);
                model.addRow(new String[]{gs.getScore(), gs.getDate()});
            }
        }
        if (DEFAULT_TABLE_ROWS > sz) {
            int n = DEFAULT_TABLE_ROWS - sz;
            SwingUtils.createEmptyRows(model.getColumnCount(), n, model);
        }
    }

    private void populateUsersTopScore(DefaultTableModel model) {
        // empty
        model.setRowCount(0);
        int sz = gameScores.size();
        int a = 0;
        Map<String, String> topScores = new ConcurrentHashMap<>();
        for (GameScores v : gameScores.values()) {
            topScores.put(v.getUsername(), v.getTopScore());
        }
        Map<String, String> sorted = topScores.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        sorted.forEach((k, v) -> model.addRow(new String[]{k, v}));
    }

    public List<GameButton> prepareGameButtons(GameInfo gi) {
        Integer[] seq = AppUtils.getRandomGameSeq(gi);
        int total = gi.getRows() * gi.getCols();

        List<GameButton> list = new ArrayList<>(total);
        List<Character> chList = new ArrayList<>(total);
        int elem = total - PAIRS_COUNT;
        Random rand = new Random();
        while (chList.size() < elem) {
            Character ch = AppConstants.GAME_CHARS[rand.nextInt(GAME_CHARS.length)];
            if (!chList.contains(ch)) {
                chList.add(ch);
            }
        }
        int x = 0;
        for (int i = 0; i < PAIRS_COUNT; i++) {
            chList.add(x, chList.get(x));
            int seqE = seq[i];
            x += seqE;
        }
        AtomicInteger k = new AtomicInteger();
        AtomicInteger t = new AtomicInteger();
        // first 3 element in sequence must be > 2
        Arrays.stream(seq).forEach(i -> {
            for (int j = 0; j < i; j++) {
                list.add(new GameButton(chList.get(t.getAndIncrement()) + Constants.EMPTY,
                        gi.getColors()[k.intValue()], this));
            }
            k.getAndIncrement();
        });

        return list;
    }

    public void checkGameButton(GameButton gb) {
        if (lastButton != null && !lastButton.isClicked()) {
            lastButton = null;
            gb = null;
        }
        if (lastButton != null && lastButton.isClicked() && gb.isClicked()
                && lastButton.getText().equals(gb.getText())) {
            gb.setVisible(false);
            lastButton.setVisible(false);
            lastButton = null;
            gb = null;
            gamePairMatched++;
            gameScore += gameInfo.getMatchScore();
            updateScore();
            if (gamePairMatched == PAIRS_COUNT) {
                gameLevel++;
                updateLevel();
                gamePairMatched = 0;
                createButtons();
            }
        } else {
            if (lastButton != null && lastButton.isClicked() && gb.isClicked()
                    && !(lastButton.getText().equals(gb.getText()))) {
                lastButton = null;
                gb = null;
                gamePairMatched = 0;
                gameLevelFailed();
            }
        }
        if (lastButton == null && gb != null) {
            lastButton = gb;
        }
    }

    private void updateLevel() {
        lblLevel.setText(UIName.BTN_LEVEL.name + SPACE + gameLevel);
    }

    private void updateScore() {
        lblScore.setText(UIName.LBL_SCORE.name + SPACE + gameScore);
    }

    private void gameLevelFailed() {
        createButtons();
    }

    private void maximiseWin() {
        setToCenter();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void loadGameScores() {
        Properties props = Utils.readPropertyFile(GAME_SCORE_LOC, logger);
        props.stringPropertyNames().forEach(k -> {
            if (k.endsWith(PROP_SCORES_SUFFIX)) {
                String v = props.getProperty(k);
                String user = getUserFromProp(k);
                if (gameScores.containsKey(user)) {
                    logger.warn("Duplicate file found for user " + Utils.addBraces(user));
                } else {
                    gameScores.put(user, getGameScores(user, v));
                }
            }
        });
        logger.info("All gameScores = " + gameScores);
    }

    private GameScores getGameScores(String user, String gameScoreCsv) {
        return new GameScores(user, processScores(gameScoreCsv));
    }

    private List<GameScore> processScores(String scoreStr) {
        List<GameScore> list = new ArrayList<>();
        String[] scoreDate = scoreStr.split(AppConstants.SCORE_SEP);
        Arrays.stream(scoreDate).forEach(sd -> {
            if (Utils.hasValue(sd)) {
                String[] arr = sd.split(AppConstants.SCORE_DATA_SEP_FOR_SPLIT);
                list.add(new GameScore(arr[0], arr[1]));
            }
        });
        return list;
    }

    private String getUserFromProp(String k) {
        if (k.contains(Constants.DASH)) {
            k = k.substring(0, k.indexOf(Constants.DASH));
        }
        return k;
    }

    private void loadGameConfigs() {
        List<String> paths = Utils.listFiles(GAME_CONFIGS_LOC, logger);
        paths.forEach(p -> {
            GameInfo gi = makeGameInfoObj(Utils.readPropertyFile(p, logger));
            gameInfos.put(gi.getGameLevel(), gi);
        });
        logger.info("Game configs load as " + gameInfos);
    }

    private GameInfo makeGameInfoObj(Properties props) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setGameLevel(props.getProperty("game-level"));
        gameInfo.setMatchScore(Utils.convertToInt(props.getProperty("match-score")));
        gameInfo.setRows(Utils.convertToInt(props.getProperty("rows")));
        gameInfo.setCols(Utils.convertToInt(props.getProperty("cols")));
        List<String> colorProps = new ArrayList<>();
        List<Color> colors = new ArrayList<>();
        props.stringPropertyNames().forEach(p -> {
            if (p.startsWith("color-")) {
                colorProps.add(props.getProperty(p));
            }
        });
        // should be in format <R,G,B>
        colorProps.forEach(cs -> {
            String[] arr = cs.split(Constants.COMMA);
            if (arr.length == 3) {
                colors.add(new Color(Utils.convertToInt(arr[0]),
                        Utils.convertToInt(arr[1]), Utils.convertToInt(arr[2])));
            } else {
                colors.add(AppUtils.getColor(cs));
            }
        });
        gameInfo.setColors(colors.toArray(new Color[0]));
        return gameInfo;
    }

    private void loadConfigValues() {
        cnfIdx = configs.getIntConfig(Configs.CNFIdx.name());
        appFontSize = configs.getIntConfig(Configs.AppFontSize.name());
        username = configs.getConfig(Configs.Username.name());
        if (!Utils.hasValue(username)) {
            username = "default";
        }
        gameBtnFontSize = configs.getIntConfig(Configs.GameBtnFontSize.name());
        logger.info("All configs: cnfIdx [" + cnfIdx +
                "], appFontSize [" + appFontSize +
                "], gameBtnFontSize [" + gameBtnFontSize +
                "], username " + Utils.addBraces(username));
    }

    private void setTable(AppTable tbl, DefaultTableModel model) {
        tbl.setScrollProps();
        tbl.setRowHeight(appFontSize + 4);
        tbl.setBorder(EMPTY_BORDER);
        for (int i = 0; i < model.getColumnCount(); i++) {
            tbl.getColumnModel().getColumn(i).setCellRenderer(CENTER_RENDERER);
        }
    }

    public void changeGameBtnFont() {
        SwingUtils.changeFont(btnsPanel, gameBtnFontSize);
    }

    public void changeAppFont() {
        SwingUtils.applyAppFont(this, appFontSize, this, logger);
        changeGameBtnFont();
        SwingUtils.applyAppFont(txtUser, appFontSize, this, logger);
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

    private void showGamePanel() {
        changeGamePanel(true);
    }

    private void changeGamePanel(boolean status) {
        btnsPanel.setVisible(status);
    }

    private void hideGamePanel() {
        changeGamePanel(false);
    }

    private void startNewGame() {
        resetGame();
        Timer t = new Timer();
        t.schedule(new GameTimerTask(this), 0);
        timers.add(t);
        createButtons();
    }

    private void resetGame() {
        gameLevel = 1;
        gameTime = GAME_TIME_SEC;
        gameStatus = Status.START;
        gameScore = 0;
        updateScore();
        updateLevel();
        updateGameTime();
        showGamePanel();
    }

    private void createButtons() {
        gameInfo = getGameInfoFor(gameLevel);
        int rows = gameInfo.getRows();
        int cols = gameInfo.getCols();
        BoxLayout boxlayout = new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS);
        buttonsPanel.setLayout(boxlayout);
        if (btnsPanel != null) {
            buttonsPanel.remove(btnsPanel);
            btnsPanel.removeAll();
            btnsPanel.repaint();
        }
        btnsPanel = new AppPanel(new GridLayout(rows, cols));

        int gap = 50;
        btnsPanel.setBorder(new EmptyBorder(new Insets(gap, gap, gap, gap)));
        // randomize buttons
        List<GameButton> gameBtns = prepareGameButtons(gameInfo);
        List<GameButton> gameBtnsRandomize = new ArrayList<>();
        long time = Utils.getNowMillis();
        Random rand = new Random();
        int sz = gameBtns.size();
        while (gameBtnsRandomize.size() < sz) {
            GameButton gb = gameBtns.get(rand.nextInt(sz));
            if (!gameBtnsRandomize.contains(gb)) {
                gameBtnsRandomize.add(gb);
            }
        }
        logger.info("Time taken to randomization " + Utils.getTimeDiffSecMilliStr(time));
        gameBtnsRandomize.forEach(btnsPanel::add);
        changeGameBtnFont();
        buttonsPanel.add(btnsPanel);
        SwingUtilities.updateComponentTreeUI(buttonsPanel);
    }

    private GameInfo getGameInfoFor(int gameLevel) {
        GameInfo gi = gameInfos.containsKey(gameLevel + "") ?
                gameInfos.get(gameLevel + "") : gameInfos.get("default");
        logger.info("Returning game info for level " + Utils.addBraces(gameLevel) + " as " + gi);
        return gi;
    }

    public void stopGame() {
        hideGamePanel();
        enableControls();
        gameScores.get(username).addScore(new GameScore(gameScore + "", Utils.getDateDDMMYYYY()));
        loadTableData();
        cancelTimers();
    }

    public void updateGameTime() {
        if (gameTime == 0) {
            gameStatus = Status.STOP;
            stopGame();
            lblTime.setForeground(fg);
        }
        if (gameTime <= ALARM_TIME_SEC && gameTime > 0) {
            lblTime.setForeground(Color.red);
        }
        lblTime.setText(UIName.LBL_TIME.name + SPACE + Utils.formatTime(gameTime--));
    }

    public boolean isGameRunning() {
        return isGameStart() || isGamePaused();
    }

    public boolean isGameStart() {
        return gameStatus == Status.START;
    }

    public boolean isGamePaused() {
        return gameStatus == Status.PAUSED;
    }

    private void createBorders() {
        Arrays.stream(componentsToColor).forEach(c -> c.setBorder(SwingUtils.createLineBorder(bg)));
    }

    private void changeUsername() {
        btnUser.setVisible(false);
        txtUser.setVisible(true);
        userPanel.remove(btnUser);
        userPanel.add(txtUser);
        SwingUtils.getInFocus(txtUser);
        txtUser.selectAll();
    }

    private void saveUsername() {
        username = txtUser.getText().trim();
        if (isValidName(username)) {
            btnUser.setVisible(true);
            txtUser.setVisible(false);
            btnUser.setText(UIName.BTN_USER.name + SPACE + username);
            userPanel.remove(txtUser);
            userPanel.add(btnUser);
            if (!gameScores.containsKey(username)) {
                gameScores.put(username, new GameScores(username, null));
            }
            loadTableData();
        } else {
            getToolkit().beep();
            if (username.length() > MAX_NAME) {
                txtUser.setText("max " + MAX_NAME + " char");
            } else {
                txtUser.setText("Fill Name");
            }
        }
    }

    private boolean isValidName(String username) {
        return Utils.hasValue(username) && username.length() < MAX_NAME;
    }

    private void startGame() {
        startNewGame();
        disableControls();
    }

    private void pauseGame() {
        gameStatus = gameStatus == Status.PAUSED ? Status.START : Status.PAUSED;
        if (isGamePaused()) {
            btnPause.setText("Resume");
            hideGamePanel();
        } else {
            btnPause.setText(UIName.BTN_PAUSE.name);
            showGamePanel();
        }
    }

    /**
     * Exit the Application
     */
    private void exitForm() {
        saveScores();
        cancelTimers();
        configs.saveConfig(this);
        setVisible(false);
        dispose();
        logger.dispose();
        System.exit(0);
    }

    private void saveScores() {
        Properties prop = new Properties();
        gameScores.values().forEach(gs -> {
            prop.setProperty(gs.getUsername() + PROP_SCORES_SUFFIX, prepareScoreCsv(gs.getRecentScores()));
        });
        Utils.saveProperties(prop, GAME_SCORE_LOC, logger);
    }

    private String prepareScoreCsv(List<GameScore> score) {
        StringBuilder sb = new StringBuilder();
        score.forEach(s -> sb.append(s.getScore())
                .append(SCORE_DATA_SEP)
                .append(s.getDate())
                .append(SCORE_SEP)
        );
        return sb.toString();
    }

    private void cancelTimers() {
        timers.forEach(Timer::cancel);
    }

    private void setControlsToEnable() {
        Component[] components = {btnStart, menuBar, menu};
        setComponentToEnable(components);
        setComponentContrastToEnable(new Component[]{btnPause});
        enableControls();
    }

    private void addBindings() {
        final JComponent[] addBindingsTo = {};
        addKeyBindings(addBindingsTo);
    }

    private void addKeyBindings(JComponent[] addBindingsTo) {
        Action actionTxtSearch = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            }
        };

        List<KeyActionDetails> keyActionDetails = new ArrayList<>();
        keyActionDetails.add(new KeyActionDetails(KS_CTRL_F, actionTxtSearch));

        SwingUtils.addKeyBindings(addBindingsTo, keyActionDetails);
    }

    public String getAppFontSize() {
        return appFontSize + "";
    }

    public String getCNFIdx() {
        return cnfIdx + "";
    }

    public String getGameBtnFontSize() {
        return gameBtnFontSize + "";
    }

    public String getUsername() {
        return username;
    }

}
