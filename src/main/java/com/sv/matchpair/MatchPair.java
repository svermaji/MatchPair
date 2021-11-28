package com.sv.matchpair;

import com.sv.core.Utils;
import com.sv.core.config.DefaultConfigs;
import com.sv.core.logger.MyLogger;
import com.sv.swingui.component.AppFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.sv.bigfile.AppConstants.*;
import static com.sv.core.Constants.*;
import static com.sv.swingui.UIConstants.*;

/**
 * Java Utility to search big files.
 * Searched for a string files of size 1GB
 */
public class MatchPair extends AppFrame {

    /**
     * This is config and program will search getter
     * of each enum to store in config file.
     * <p>
     * e.g. if enum is Xyz then when storing getXyz will be called
     */
    public enum Configs {
        RecentFiles, FilePath, SearchString, RecentSearches, LastN, FontSize, FontIndex,
        ColorIndex, ChangeFontAuto, ChangeHighlightAuto, ApplyColorToApp, AutoLock,
        ClipboardSupport, MatchCase, WholeWord, FixedWidth, MultiTab, ReopenLastTabs,
        ErrorTimeLimit, ErrorOccrLimit, DebugEnabled, LogSimpleClassName, ErrorMemoryLimitInMB,
        AppFontSize
    }

    private MyLogger logger;
    private DefaultConfigs configs;

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

        appFontSize = Utils.validateInt(configs.getIntConfig(Configs.AppFontSize.name()), DEFAULT_APPFONTSIZE, MIN_APPFONTSIZE, MAX_APPFONTSIZE);
    }
}
