import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class OMUD_GUI implements OMUD_ITelnetEvents, OMUD_ITextInputEvents, OMUD_IMUDEvents{
    private OMUD_Telnet         _omt =              null;
    private OMUD_TelnetParser   _omtp =             null;
    private OMUD_GUIScrollPane  _scroll =           null;
    private OMUD_GUITerminal    _term =             null;
    private OMUD_GUITextInput   _txtInput =         null;
    private JFrame              _fMain =            null;
    private JFrame              _fView =            null;
    private JFrame              _fInfo =            null;
    private JButton             _btnTelnetConnect = null;
    private JToggleButton       _tglSingleMode =    null;
    private JTextField          _txtTelnetConAdr =  null;
    private JTextField          _txtTelnetConPort = null;
    private JTextField          _txtBBSLoc =        null;
    private JTextField          _txtRoomID =        null;
    private JTextField          _txtStatline =      null;
    private JTextField          _txtExp =           null;
    private JTextField          _txtLastCmd =       null;
    private JTextArea 			_txtDbgTerm = 	    null;
    private JTextArea           _txtMUDCmds =       null;
    private JTextArea           _txtMUDOther =      null;
    private JTextArea           _txtMUDWelcome =    null;
    private JTextArea           _txtMUDRoom =       null;
    private JTextArea           _txtMUDInv =        null;
    private JTextArea           _txtMUDStats =      null;
    private JPanel              _pnlTelnet =        null;
    private JPanel              _pnlStatus =        null;
    private JPanel              _pnlInput =         null;
    private JTabbedPane         _tabsView =         null;
    private JTabbedPane         _tabsInfo =         null;
    private JTable              _tblFakeChars =     null;
    private SimpleDateFormat    _sdf =              null; 
    private static final int TERMINAL_WIDTH  =      675;
    private static final int FMAIN_MIN_WIDTH  =     450;
    private static final int FMAIN_MIN_HEIGHT =     150;
    private static final int FVIEW_MIN_WIDTH  =     685;
    private static final int FVIEW_MIN_HEIGHT =     550;
    private static final int FINFO_MIN_WIDTH  =     550;
    private static final int FINFO_MIN_HEIGHT =     550;
    private static final String[] CHARS_COLS = {"Realm", "Name"};

    public OMUD_GUI() {
        _sdf = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");

        // --------------
        // GUI Creation
        // --------------
        createGUI();

        // --------------
        // Telnet
        // --------------
        try{
            _omtp = new OMUD_TelnetParser(this, this);
            _omt =  new OMUD_Telnet(this, _omtp);
        } catch (Exception e) {
            OMUD.logError("Error creating core OmegaMUD objects:" + e.getMessage());
        }        

		// --------------
		// GUI Post-Creation
		// --------------
        // set default focus...
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtInput.requestFocus();
        }});
        OMUD.logInfo("GUI Created");

		// --------------
		// Optional Auto-Stuff
		// --------------
        // auto single-mode...
		//SwingUtilities.invokeLater(new Runnable(){public void run(){
		//	_tglSingleMode.setSelected(true);
        //}});
        // auto connect...
		//SwingUtilities.invokeLater(new Runnable(){public void run(){
		//	_omt.connect(_txtTelnetConAdr.getText(), _txtTelnetConPort.getText());
        //}});
    }

    private void setStatusLocText(String text){_txtBBSLoc.setText("BBSLoc: " + text);}

    // --------------
    // GUI Creation
    // --------------
    private void createGUI(){
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gblc = new GridBagConstraints();

        // main / char select frame...
        _fMain = new JFrame("OmegaMUD v0: Char Select/Editor");
        _fMain.setMinimumSize(new Dimension(FMAIN_MIN_WIDTH, FMAIN_MIN_HEIGHT));
        _fMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // char view frame...
        _fView = new JFrame("Char View");
        _fView.getContentPane().setLayout(gbl);
        _fView.setMinimumSize(new Dimension(FVIEW_MIN_WIDTH, FVIEW_MIN_HEIGHT));

        // char info frame...
        _fInfo = new JFrame("Char Info");
        _fInfo.setMinimumSize(new Dimension(FINFO_MIN_WIDTH, FINFO_MIN_HEIGHT));

        // network/telnet...
        _pnlTelnet =        new JPanel();
        _txtTelnetConAdr =  new JTextField("bbs.bearfather.net");
        _txtTelnetConPort = new JTextField("23");
        _txtBBSLoc =        new JTextField();
        _btnTelnetConnect = new JButton("Connect");
        _btnTelnetConnect.addActionListener(new AL_BtnConnect());
        _txtTelnetConAdr.setBackground(OMUD.GUI_BG);
        _txtTelnetConPort.setBackground(OMUD.GUI_BG);
        _txtBBSLoc.setBackground(OMUD.GUI_BG);
        _txtTelnetConAdr.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtTelnetConPort.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSLoc.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSLoc.setEditable(false);

        // terminal...
        _scroll = new OMUD_GUIScrollPane();
        _term =   new OMUD_GUITerminal(_scroll);
        _term.addMouseListener(new MA_TerminalFocus());
        _scroll.setViewportView(_term);
        _scroll.removeCaretListeners(_term);

        // status...
        _pnlStatus =    new JPanel();
        _txtRoomID =    new JTextField("RID: ?");
        _txtStatline =  new JTextField("Statline: ?");
        _txtExp =       new JTextField("XP: ?");
        _txtLastCmd =   new JTextField("CMD: ?");
        _txtRoomID.setEditable(false);
        _txtStatline.setEditable(false);
        _txtExp.setEditable(false);
        _txtLastCmd.setEditable(false);
        _txtRoomID.setBackground(OMUD.GUI_BG);
        _txtStatline.setBackground(OMUD.GUI_BG);
        _txtExp.setBackground(OMUD.GUI_BG);
        _txtLastCmd.setBackground(OMUD.GUI_BG);
        _txtRoomID.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtStatline.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtExp.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtLastCmd.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        setStatusLocText(OMUD.BBS_LOCATION_STRINGS[OMUD.eBBSLocation.OFFLINE.ordinal()]);

        // input...
        _pnlInput = new JPanel();
        _txtInput = new OMUD_GUITextInput(this);
        _tglSingleMode = new JToggleButton("SingleMode");
        _tglSingleMode.addItemListener(new IL_TglSelected());
        _txtInput.setBackground(OMUD.GUI_BG);
        _txtInput.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtInput.setCaretColor(OMUD.TERMINAL_LOCAL_INFO_FG);

        // tabs: char view...
        _tabsView = new JTabbedPane();
        _tabsView.add("Terminal View", _scroll);
        _tabsView.add("Fun View 1", null);
        _tabsView.add("Fun View 2", null);

        // tabs: char info...
        _txtDbgTerm =    createGUI_DebugTab();
        _txtMUDCmds =    createGUI_DebugTab();
        _txtMUDOther =   createGUI_DebugTab();
        _txtMUDWelcome = createGUI_DebugTab();
        _txtMUDRoom =    createGUI_DebugTab();
        _txtMUDInv =     createGUI_DebugTab();
        _txtMUDStats =   createGUI_DebugTab();
        _tabsInfo = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        _tabsInfo.add("DbgTerm",  _txtDbgTerm);
        _tabsInfo.add("MCmds",    new JScrollPane(_txtMUDCmds));
        _tabsInfo.add("MOther",   new JScrollPane(_txtMUDOther));
        _tabsInfo.add("MWelcome", new JScrollPane(_txtMUDWelcome));
        _tabsInfo.add("MRoom",    new JScrollPane(_txtMUDRoom));
        _tabsInfo.add("MInv",     new JScrollPane(_txtMUDInv));
        _tabsInfo.add("MStats",   new JScrollPane(_txtMUDStats));
        _fInfo.add(_tabsInfo);

        // chars table...
        createGUI_CharsTable();

        // panel layouts...
        layoutTerminal(gbl, gblc);
        layoutTelnet(gbl,   gblc);
        layoutStatus(gbl,   gblc);
        layoutInput(gbl,    gblc);

        // finalize GUI...
        _fMain.pack();
        _fView.pack();
        _fInfo.pack();
        _fMain.setSize(new Dimension(FMAIN_MIN_WIDTH, FMAIN_MIN_HEIGHT)); // needed to get correct size after pack (table/scroll updates)
        //_fView.setLocationRelativeTo(null); // center in main display
        _fView.setLocation(25, (int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().getSize().getHeight() * 0.25));
        _fInfo.setLocation(_fView.getLocation().x + (int) _fView.getSize().getWidth(), _fView.getLocation().y);
        _fMain.setLocation(_fView.getLocation().x, _fView.getLocation().y - FMAIN_MIN_HEIGHT);
        _fMain.setVisible(true);
        _fView.setVisible(true);
        _fInfo.setVisible(true);
        _term.finalizeGUI();
    }

    private JTextArea createGUI_DebugTab(){
        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.getCaret().setVisible(true);
        txt.setLineWrap(false);
        txt.setBackground(OMUD.GUI_BG);
        txt.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        txt.setCaretColor(OMUD.TERMINAL_LOCAL_INFO_FG);
        return txt;
    }

    private void createGUI_CharsTable(){
        Object[][] objFakeChars = {
            {"BearsBBS_Normal", "Gandalf LastName"},
            {"BearsBBS_Edited", "Aragorn LastName"},
            {"BearsBBS_MudRev", "Legolas LastName"},
            {"ClassicMUD",      "Gimli LastName"},
            {"DarkwoodBBS",     "Syntax MudInfo"}};
        _tblFakeChars = new JTable(objFakeChars, CHARS_COLS);
        _fMain.add(new JScrollPane(_tblFakeChars)); // table must go inside a scroll pane to show column headers properly
    }

    // --------------
    // GUI Layout Stuff
    // --------------    
    private void layoutTelnet(GridBagLayout gbl, GridBagConstraints gblc){
        _txtTelnetConPort.setPreferredSize(new Dimension(50, 25));
        _txtBBSLoc.setPreferredSize(new Dimension(125, 25));
        _btnTelnetConnect.setPreferredSize(new Dimension(150, 25));
        gblc.weightx = 1.0;
        gblc.weighty = 0.0;
        gblc.gridwidth = 3;
        gblc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(_txtTelnetConAdr, gblc);
        _pnlTelnet.add(_txtTelnetConAdr);
        gblc.weightx = gblc.weighty = 0.0;
        gblc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(_txtTelnetConPort, gblc);
        _pnlTelnet.add(_txtTelnetConPort);
        gbl.setConstraints(_txtBBSLoc, gblc);
        _pnlTelnet.add(_txtBBSLoc);
        gblc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(_btnTelnetConnect, gblc);
        _pnlTelnet.add(_btnTelnetConnect);
        // panel/frame...
        gblc.weightx = 1.0;
        gblc.weighty = 0.0;
        gblc.gridwidth = GridBagConstraints.REMAINDER;
        gblc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlTelnet, gblc);
        _pnlTelnet.setLayout(gbl);
        _fView.add(_pnlTelnet);
    }

    private void layoutTerminal(GridBagLayout gbl, GridBagConstraints gblc){
        _tabsView.setPreferredSize(new Dimension(TERMINAL_WIDTH, 0));
        _tabsView.setMinimumSize(new Dimension(TERMINAL_WIDTH, 0));
        gblc.weightx = 0.0;
        gblc.weighty = 1.0;
        gblc.gridwidth = GridBagConstraints.REMAINDER;
        gblc.fill = GridBagConstraints.VERTICAL;
        gbl.setConstraints(_tabsView, gblc);
        _fView.add(_tabsView);
    } 

    private void layoutStatus(GridBagLayout gbl, GridBagConstraints gblc){
        _txtRoomID.setPreferredSize(new Dimension(60, 25));
        _txtStatline.setPreferredSize(new Dimension(150, 25));
        _txtExp.setPreferredSize(new Dimension(150, 25));
        _txtLastCmd.setPreferredSize(new Dimension(150, 25));
        gblc.weightx = 1.0;
        gblc.weighty = 0.0;
        gblc.gridwidth = 3;
        gblc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(_txtRoomID, gblc);
        _pnlStatus.add(_txtRoomID);
        gbl.setConstraints(_txtStatline, gblc);
        _pnlStatus.add(_txtStatline);
        gbl.setConstraints(_txtExp, gblc);
        _pnlStatus.add(_txtExp);
        gblc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(_txtLastCmd, gblc);
        _pnlStatus.add(_txtLastCmd);
        // panel/frame...
        gblc.gridwidth = GridBagConstraints.REMAINDER;
        gblc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlStatus, gblc);
        _pnlStatus.setLayout(gbl);
        _fView.add(_pnlStatus);
    }

    private void layoutInput(GridBagLayout gbl, GridBagConstraints gblc){
        _tglSingleMode.setPreferredSize(new Dimension(150, 25));
        gblc.weightx = 1.0;
        gblc.weighty = 0.0;
        gblc.gridwidth = 1;
        gblc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(_txtInput, gblc);
        _pnlInput.add(_txtInput);
        gblc.weightx = gblc.weighty = 0.0;
        gblc.gridwidth = GridBagConstraints.REMAINDER;
        gblc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(_tglSingleMode, gblc);
        _pnlInput.add(_tglSingleMode);
        _pnlInput.setLayout(gbl);
        // panel/frame...        
        gblc.weightx = 1.0;
        gblc.weighty = 0.0;
        gblc.gridwidth = GridBagConstraints.REMAINDER;
        gblc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlInput, gblc);
        _pnlInput.setLayout(gbl);
        _fView.add(_pnlInput);
    }

    // --------------
    // GUI Events
    // --------------
    public void notifyInputText(String text){_omt.sendText(text);}

    private class AL_BtnConnect implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (_omt.isConnected())
                 _omt.disconnect(true);
            else _omt.connect(_txtTelnetConAdr.getText(), _txtTelnetConPort.getText());

            SwingUtilities.invokeLater(new Runnable(){public void run(){
                _txtInput.requestFocus();
            }});        
        }
    }

    private class IL_TglSelected implements ItemListener {
        public void itemStateChanged(ItemEvent event) {
            _txtInput.setSingleMode(event.getStateChange() == ItemEvent.SELECTED ? true : false);            
            SwingUtilities.invokeLater(new Runnable(){public void run(){
                _txtInput.requestFocus();
            }});
        }
    }

    private class MA_TerminalFocus extends MouseAdapter {
        private Timer _timer = null;
        public void mousePressed(MouseEvent e) {
            _timer = new Timer(100, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // waiting for mouse to be released
                }
            });
            _timer.start();
        }
        public void mouseReleased(MouseEvent e) {
            if (_timer != null)
                _timer.stop();

            // copy to clipboard...
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(_term.getSelectedText()), null);

            // return focus...
            SwingUtilities.invokeLater(new Runnable(){public void run(){
                _term.getCaret().setVisible(false);
                _term.getCaret().setVisible(true);
                _txtInput.requestFocus();
            }});

            // restore the terminal buffer/caret position in case it moved...
            _term.setCaretPosition(_omtp.getBufferPos());
        }
    }

    // --------------
    // Telnet Events
    // --------------
    public void notifyTelnetConnected(){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _btnTelnetConnect.setText("Disconnect");
            setStatusLocText(OMUD.BBS_LOCATION_STRINGS[OMUD.eBBSLocation.BBS.ordinal()]);
        }});
    }

    public void notifyTelnetDisconnected(){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _btnTelnetConnect.setText("Connect");
            setStatusLocText(OMUD.BBS_LOCATION_STRINGS[OMUD.eBBSLocation.OFFLINE.ordinal()]);
        }});
    }

    public void notifyTelnetParsed(final OMUD_Buffer omb, final ArrayList<OMUD_IBufferMod> arrlBMods){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            // allow the terminal to render buffer mods...
            _term.render(omb, arrlBMods);

            // force update scrollbars...
            _scroll.scrollToBottom();
            // horiz update requires GUI update delay...
            //SwingUtilities.invokeLater(new Runnable(){public void run(){
            //    sp.getHorizontalScrollBar().setValue(0);}});

            if (_fInfo.isVisible() && _tabsInfo.getSelectedIndex() == 0){
                // terminal debug text...
                int caret_pos = 0;
                String strRow;
                Boolean is_buffer_row = false;
                StringBuilder sbTermDebug = new StringBuilder();
                for (int i = 0; i < OMUD.TERMINAL_ROWS; ++i){
                    strRow = omb.getRowText(i);
                    sbTermDebug.append(String.format("[%02d]", i + 1) + (strRow.length() > 0 ? " " : "_"));

                    is_buffer_row = i == omb.getRowNum();
                    if (is_buffer_row)
                        caret_pos = sbTermDebug.length();
                    if (strRow.length() > 0){
                        sbTermDebug.append(strRow);
                        if (is_buffer_row)
                            caret_pos += omb.getColNum();
                    }

                    sbTermDebug.append("\n");
                }
                sbTermDebug.append("---------------------------------\n");
                sbTermDebug.append("BLC/BTL/BSZ: " + omb.getLineCount() + ", " + omb.getTopLeftPos() + ", " + omb.getText().length() + "\n");
                sbTermDebug.append("ROW/COL: " + (omb.getRowNum() + 1) + ", " + (omb.getColNum() + 1) + "\n");
                _txtDbgTerm.setText(sbTermDebug.toString());
                _txtDbgTerm.setCaretPosition(caret_pos);
            }
            _omtp.setGUIReady();
        }});
    }

    // --------------
    // MUD Events
    // --------------
    public void notifyMUDAutoCmd(final String strCmd){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _omt.sendText(strCmd);
        }});
    }

    public void notifyMUDLocation(final OMUD.eBBSLocation eLoc){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            setStatusLocText(OMUD.BBS_LOCATION_STRINGS[eLoc.ordinal()]);
        }});
    }

    public void notifyMUDUserCmd(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtLastCmd.setText("CMD: " + strText);
            _txtMUDCmds.setText(_txtMUDCmds.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
        }});
    }

    public void notifyMUDOther(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtMUDOther.setText(_txtMUDOther.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
            _txtMUDOther.setCaretPosition(_txtMUDOther.getText().length());
        }});
    }

    public void notifyMUDStatline(final OMUD_MMUD.DataStatline dataStatline){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            StringBuilder sb = new StringBuilder();
            sb.append(dataStatline.hp_str + "=" + dataStatline.hp_cur + "/" + dataStatline.hp_max + (dataStatline.hp_mod ? "*" : ""));
            if (dataStatline.ma_cur > 0)
                sb.append(", " + dataStatline.ma_str + "=" + dataStatline.ma_cur + "/" + dataStatline.ma_max + (dataStatline.ma_mod ? "*" : ""));
            sb.append(" " + OMUD_MMUD.REST_STATE_STRINGS[dataStatline.rest.ordinal()]);
            _txtStatline.setText(sb.toString());
        }});
    }

    public void notifyMUDWelcome(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtMUDWelcome.setText(strText);
        }});
    }

    public void notifyMUDRoom(final OMUD_MMUD.DataRoom dataRoom){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _tabsInfo.setSelectedIndex(4);
            _txtRoomID.setText("RID: " + dataRoom.roomID);

            StringBuilder sb = new StringBuilder();
            sb.append("[RoomID]: "          + dataRoom.roomID       + "\n\n");
            sb.append("[RoomName]: "        + dataRoom.name         + " (" + OMUD_MMUD.ROOM_LIGHT_STRINGS[dataRoom.light.ordinal()] + ")\n\n");
            sb.append("[RoomItems]\n"       + dataRoom.items        + "\n\n");
            sb.append("[RoomItemsHidden]\n" + dataRoom.items_hidden + "\n\n");
            sb.append("[RoomUnits]\n"       + dataRoom.units        + "\n\n");
            sb.append("[RoomExits]\n"       + dataRoom.exits        + "\n\n");
            sb.append("[RoomDesc]\n"        + dataRoom.desc);
            _txtMUDRoom.setText(sb.toString());
            _txtMUDRoom.setCaretPosition(0);
        }});
    }

    public void notifyMUDInv(final OMUD_MMUD.DataInv dataInv){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _tabsInfo.setSelectedIndex(5);

            StringBuilder sb = new StringBuilder();
            sb.append("[InvEnc]: ("     + dataInv.enc_level + ") " + dataInv.enc_cur + "/" + dataInv.enc_max + String.format(" [%.0f", ((float) dataInv.enc_cur / dataInv.enc_max) * 100) + "%]\n\n");
            sb.append("[InvWealth]: "   + dataInv.wealth  + " (copper)\n\n");
            sb.append("[InvCoins]\n"    + 
                "Run: " + dataInv.coins_runic     + "\n"  + 
                "Plt: " + dataInv.coins_plat      + "\n"  + 
                "Gld: " + dataInv.coins_gold      + "\n"  + 
                "Sil: " + dataInv.coins_silver    + "\n"  + 
                "Cop: " + dataInv.coins_copper    + "\n\n");
            sb.append("[InvItems]\n"    + dataInv.items   + "\n\n");
            sb.append("[InvKeys]\n"     + dataInv.keys    + "\n\n");
            _txtMUDInv.setText(sb.toString());
            _txtMUDInv.setCaretPosition(0);
        }});
    }

    public void notifyMUDStats(final OMUD_MMUD.DataStats dataStats){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _tabsInfo.setSelectedIndex(6);

            StringBuilder sb = new StringBuilder();
            sb.append("[NameFirst]: "   + dataStats.name_first      + "\n");
            sb.append("[NameLast]: "    + dataStats.name_last       + "\n");
            sb.append("[Race]: "        + dataStats.stats_race      + "\n");
            sb.append("[Class]: "       + dataStats.stats_class     + "\n");
            sb.append("[Level]: "       + dataStats.level           + "\n");
            sb.append("[Lives]: "       + dataStats.lives           + "\n");
            sb.append("[CP]: "          + dataStats.cp              + "\n");
            sb.append("[Str]: "         + dataStats.str             + "\n");
            sb.append("[Int]: "         + dataStats.intel           + "\n");
            sb.append("[Wil]: "         + dataStats.wil             + "\n");
            sb.append("[Agi]: "         + dataStats.agi             + "\n");
            sb.append("[Hea]: "         + dataStats.hea             + "\n");
            sb.append("[Cha]: "         + dataStats.cha             + "\n");
            sb.append("[AC]: "          + dataStats.ac_ac + "/" + dataStats.ac_accy  + "\n");
            sb.append("[SC]: "          + dataStats.sc              + "\n");
            sb.append("[Perc]: "        + dataStats.perc            + "\n");
            sb.append("[Stealth]: "     + dataStats.stealth         + "\n");
            sb.append("[Thiev]: "       + dataStats.thievery        + "\n");
            sb.append("[Traps]: "       + dataStats.traps           + "\n");
            sb.append("[Pick]: "        + dataStats.pick            + "\n");
            sb.append("[Track]: "       + dataStats.track           + "\n");
            sb.append("[MA]: "          + dataStats.ma              + "\n");
            sb.append("[MR]: "          + dataStats.mr              + "\n");

            _txtMUDStats.setText(sb.toString());
            _txtMUDStats.setCaretPosition(0);            
        }});
    }

    public void notifyMUDExp(final OMUD_MMUD.DataExp dataExp){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtExp.setText("XP: " + dataExp.next_rem + String.format(" (%.0f", ((float) dataExp.cur_total / dataExp.next_total) * 100) + "%) [" + dataExp.cur_total + "/" + dataExp.next_total + "]");
            _txtExp.setCaretPosition(0);
        }});
    }

    public void notifyMUDParty(){}
    public void notifyMUDSpells(){}
    public void notifyMUDCombat(){}
}
