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
    private OMUD_BBS            _bbs =              null;
    private OMUD_MMUDChar       _mmc =              null;
    private JFrame              _fMain =            null;
    private JFrame              _fView =            null;
    private JFrame              _fInfo =            null;
    private JButton             _btnTelnetConnect = null;
    private JToggleButton       _tglSingleMode =    null;
    private JTextField          _txtTelnetConAdr =  null;
    private JTextField          _txtTelnetConPort = null;
    private JTextField          _txtStatusLoc =     null;
    private JTextField          _txtStatusRoomID =  null;
    private JTextField          _txtStatusEXPLeft = null;
    private JTextField          _txtStatusEXPRate = null;
    private JTextArea 			_txtDbgTerm = 	    null;
    private JTextArea           _txtDbgMUDChar =    null;
    private JTextArea           _txtDbgMUDCmds =    null;
    private JTextArea           _txtDbgMUDWelcome = null;
    private JTextArea           _txtDbgMUDOther =   null;
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
        // BBS/MUD Stuff
        // --------------
        _bbs = new OMUD_BBS();
        _mmc = new OMUD_MMUDChar();

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

    private void setStatusLocText(String text){_txtStatusLoc.setText("BBSLoc: " + text);}

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
        _btnTelnetConnect = new JButton("Connect");
        _btnTelnetConnect.addActionListener(new AL_BtnConnect());
        _txtTelnetConAdr.setBackground(OMUD.GUI_BG);
        _txtTelnetConPort.setBackground(OMUD.GUI_BG);
        _txtTelnetConAdr.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtTelnetConPort.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);

        // terminal...
        _scroll = new OMUD_GUIScrollPane();
        _term =   new OMUD_GUITerminal(_scroll);
        _term.addMouseListener(new MA_TerminalFocus());
        _scroll.setViewportView(_term);
        _scroll.removeCaretListeners(_term);

        // status...
        _pnlStatus =        new JPanel();
        _txtStatusLoc =     new JTextField();
        _txtStatusRoomID =  new JTextField("RoomID: ");
        _txtStatusEXPLeft = new JTextField("EXPLeft: ");
        _txtStatusEXPRate = new JTextField("EXPRate: ");
        _txtStatusLoc.setEditable(false);
        _txtStatusRoomID.setEditable(false);
        _txtStatusEXPLeft.setEditable(false);
        _txtStatusEXPRate.setEditable(false);
        _txtStatusLoc.setBackground(OMUD.GUI_BG);
        _txtStatusRoomID.setBackground(OMUD.GUI_BG);
        _txtStatusEXPLeft.setBackground(OMUD.GUI_BG);
        _txtStatusEXPRate.setBackground(OMUD.GUI_BG);
        _txtStatusLoc.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtStatusRoomID.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtStatusEXPLeft.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtStatusEXPRate.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        setStatusLocText(_bbs.getLocationString());

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
        _txtDbgTerm =       createGUI_DebugTab();
        _txtDbgMUDChar =    createGUI_DebugTab();
        _txtDbgMUDWelcome = createGUI_DebugTab();
        _txtDbgMUDCmds =    createGUI_DebugTab();
        _txtDbgMUDOther =   createGUI_DebugTab();
        _tabsInfo = new JTabbedPane();
        _tabsInfo.add("DbgTerm",        _txtDbgTerm);
        _tabsInfo.add("DbgMUDChar",     new JScrollPane(_txtDbgMUDChar));
        _tabsInfo.add("DbgMUDWelcome",  new JScrollPane(_txtDbgMUDWelcome));
        _tabsInfo.add("DbgMUDCmds",     new JScrollPane(_txtDbgMUDCmds));
        _tabsInfo.add("DbgMUDOther",    new JScrollPane(_txtDbgMUDOther));
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
        _btnTelnetConnect.setPreferredSize(new Dimension(150, 25));
        gblc.weightx = 1.0;
        gblc.weighty = 0.0;
        gblc.gridwidth = 2;
        gblc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(_txtTelnetConAdr, gblc);
        _pnlTelnet.add(_txtTelnetConAdr);
        gblc.weightx = gblc.weighty = 0.0;
        gblc.fill = GridBagConstraints.NONE;
        gbl.setConstraints(_txtTelnetConPort, gblc);
        _pnlTelnet.add(_txtTelnetConPort);
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
        _txtStatusLoc.setPreferredSize(new Dimension(50, 25));
        _txtStatusRoomID.setPreferredSize(new Dimension(50, 25));
        _txtStatusEXPLeft.setPreferredSize(new Dimension(50, 25));
        _txtStatusEXPRate.setPreferredSize(new Dimension(50, 25));
        gblc.weightx = 1.0;
        gblc.weighty = 0.0;
        gblc.gridwidth = 3;
        gblc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(_txtStatusLoc, gblc);
        _pnlStatus.add(_txtStatusLoc);
        gbl.setConstraints(_txtStatusRoomID, gblc);
        _pnlStatus.add(_txtStatusRoomID);
        gbl.setConstraints(_txtStatusEXPLeft, gblc);
        _pnlStatus.add(_txtStatusEXPLeft);
        gblc.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(_txtStatusEXPRate, gblc);
        _pnlStatus.add(_txtStatusEXPRate);
        // panel/frame...
        gblc.weightx = 1.0;
        gblc.weighty = 0.0;
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
            setStatusLocText(_bbs.setLocation(OMUD_BBS.eLocation.BBS));
        }});
    }

    public void notifyTelnetDisconnected(){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _btnTelnetConnect.setText("Connect");
            _bbs = new OMUD_BBS();
            _mmc = new OMUD_MMUDChar();
            setStatusLocText(_bbs.setLocation(OMUD_BBS.eLocation.OFFLINE));
        }});
    }

    public void notifyTelnetParsed(final OMUD_Buffer omb, final ArrayList<OMUD_IBufferMod> arrlBMods, final String strLastCmd){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            // parsed telnet data can come in after a disconnect (threaded), so pass connection state...
            if (_bbs.checkLocation(_omt.isConnected(), strLastCmd)){
                setStatusLocText(_bbs.getLocationString());
                // for testing convenince: switch to room debug tab when entering mud...
                if (_bbs.getLocation() == OMUD_BBS.eLocation.MUD && _tabsInfo.getSelectedIndex() == 0)
                    _tabsInfo.setSelectedIndex(1);
            }

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
    public boolean isInsideMUD(){return _bbs.getLocation() == OMUD_BBS.eLocation.MUD;}
    public void notifyMUDStatline(){}
    public void notifyMUDRoom(){}
    public void notifyMUDExp(){}
    public void notifyMUDStats(){}
    public void notifyMUDInv(){}
    public void notifyMUDParty(){}
    public void notifyMUDSpells(){}
    public void notifyMUDCombatToggle(boolean is_on){}

    public void notifyMUDBBSMenu(){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            setStatusLocText(_bbs.setLocation(OMUD_BBS.eLocation.BBS_MUD_MENU));
        }});
    }

    public void notifyMUDWelcome(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtDbgMUDWelcome.setText(strText);
        }});
    }    

    public void notifyMUDDebugCmd(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtDbgMUDCmds.setText(_txtDbgMUDCmds.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
        }});
    }    

    public void notifyMUDDebugOther(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtDbgMUDOther.setText(_txtDbgMUDOther.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
            _txtDbgMUDOther.setCaretPosition(_txtDbgMUDOther.getText().length());
        }});
    }

    public void notifyMUDDebugChar(final OMUD_MMUD.DataRoom dataRoom, final OMUD_MMUD.DataStatline dataStatline){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtStatusRoomID.setText("RoomID: " + dataRoom.roomID);
            
            StringBuilder sb = new StringBuilder();
            sb.append("[StatlineLastCmd]: " + dataStatline.last_cmd + "\n");
            sb.append("[Statline]: "        + dataStatline.text     + " " + OMUD_MMUD.REST_STATE_STRINGS[dataStatline.rest.ordinal()] + "\n\n");
            sb.append("[RoomID]: "          + dataRoom.roomID       + "\n\n");
            sb.append("[RoomName]: "        + dataRoom.name         + " (" + OMUD_MMUD.ROOM_LIGHT_STRINGS[dataRoom.light.ordinal()] + ")\n\n");
            sb.append("[RoomItems]\n"       + dataRoom.items        + "\n\n");
            sb.append("[RoomItemsHidden]\n" + dataRoom.items_hidden + "\n\n");
            sb.append("[RoomUnits]\n"       + dataRoom.units        + "\n\n");
            sb.append("[RoomExits]\n"       + dataRoom.exits        + "\n\n");
            sb.append("[RoomDesc]\n"        + dataRoom.desc);
            _txtDbgMUDChar.setText(sb.toString());
            _txtDbgMUDChar.setCaretPosition(0);
        }});
    }
}
