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
    private OMUD_GUITextInput   _txtBBSInput =      null;
    private JFrame              _fChars =           null;
    private JFrame              _fView =            null;
    private JFrame              _fInfo =            null;
    private JButton             _btnTelnetConnect = null;
    private JToggleButton       _tglSingleMode =    null;
    private JTextField          _txtBBSNetAdr =     null;
    private JTextField          _txtBBSNetPort =    null;
    private JTextField          _txtBBSLoc =        null;
    private JTextField          _txtCharRoomID =    null;
    private JTextField          _txtCharStatline =  null;
    private JTextField          _txtCharExp =       null;
    private JTextField          _txtCharLastCmd =   null;
    private JTextField          _txtCharMoney =     null;
    private JTextArea 			_txtInfoTermDbg = 	null;
    private JTextArea           _txtInfoCmds =      null;
    private JTextArea           _txtInfoOther =     null;
    private JTextArea           _txtInfoWelcome =   null;
    private JTextArea           _txtInfoRoom =      null;
    private JTextArea           _txtInfoInv =       null;
    private JTextArea           _txtInfoStats =     null;
    private JTextArea           _txtInfoShop =      null;
    private JPanel              _pnlV1 =            null;
    private JPanel              _pnlV1BBS =         null;
    private JPanel              _pnlV1Char =        null;
    private JPanel              _pnlV1Input =       null;
    private JPanel              _pnlInfoInv =       null;
    private JTabbedPane         _tabsView =         null;
    private JTabbedPane         _tabsInfo =         null;
    private JTable              _tblFakeChars =     null;
    private SimpleDateFormat    _sdf =              null; 
    private static final int TERMINAL_WIDTH  =      675;
    private static final int FCHARS_MIN_WIDTH  =    450;
    private static final int FCHARS_MIN_HEIGHT =    150;
    private static final int FVIEW_MIN_WIDTH  =     690;
    private static final int FVIEW_MIN_HEIGHT =     550;
    private static final int FINFO_MIN_WIDTH  =     700;
    private static final int FINFO_MIN_HEIGHT =     550;
    private static final String[] CHARS_COLS = {"Realm", "Name"};

    public OMUD_GUI() {
        _sdf = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");

        // GUI frames...
        createGUI_FrameChars();
        createGUI_FrameCharView();
        createGUI_FrameCharInfo();

        // finalize GUI...
        _fChars.pack();
        _fView.pack();
        _fInfo.pack();
        _fChars.setSize(new Dimension(FCHARS_MIN_WIDTH, FCHARS_MIN_HEIGHT)); // needed to get correct size after pack (table/scroll updates)
        //_fView.setLocationRelativeTo(null); // center in main display
        Dimension size = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().getSize();
        _fInfo.setLocation((int) (size.getWidth() - _fInfo.getSize().getWidth()) - 50, (int) _fChars.getSize().getHeight() + 50);
        _fView.setLocation((int) (size.getWidth() - (_fView.getSize().getWidth() * 1.5)), _fInfo.getLocation().y + 100);
        _fChars.setLocation(_fInfo.getLocation().x, 50);
        _fChars.setVisible(true);
        _fView.setVisible(true);
        _fInfo.setVisible(true);
        _term.finalizeGUI();

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
            _txtBBSInput.requestFocus();
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
		//	_omt.connect(_txtBBSNetAdr.getText(), _txtBBSNetPort.getText());
        //}});
    }

    // --------------
    // GUI Frame: Chars
    // --------------
    private void createGUI_FrameChars(){
        _fChars = new JFrame("OmegaMUD v0 (Char Status/Editor)");
        _fChars.setMinimumSize(new Dimension(FCHARS_MIN_WIDTH, FCHARS_MIN_HEIGHT));
        _fChars.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Object[][] objFakeChars = {
            {"BearsBBS_Normal", "Gandalf LastName"},
            {"BearsBBS_Edited", "Aragorn LastName"},
            {"BearsBBS_MudRev", "Legolas LastName"},
            {"ClassicMUD",      "Gimli LastName"},
            {"DarkwoodBBS",     "Syntax MudInfo"}};
        _tblFakeChars = new JTable(objFakeChars, CHARS_COLS);
        _fChars.add(new JScrollPane(_tblFakeChars)); // table must go inside a scroll pane to show column headers properly
    }

    // --------------
    // GUI Frame: Char View
    // --------------
    private void createGUI_FrameCharView(){
        // frame...
        _fView = new JFrame("Char View");
        _fView.setMinimumSize(new Dimension(FVIEW_MIN_WIDTH, FVIEW_MIN_HEIGHT));

        // terminal/scroll...
        _scroll = new OMUD_GUIScrollPane();
        _term =   new OMUD_GUITerminal(_scroll);
        _term.addMouseListener(new MA_TerminalFocus());
        _scroll.setViewportView(_term);
        _scroll.removeCaretListeners(_term);

        // terminal panel & tabs...
        _pnlV1 =    new JPanel();
        _tabsView = new JTabbedPane();
        _tabsView.add("Terminal View",  _pnlV1);
        _tabsView.add("Fun View 1",     null);
        _tabsView.add("Fun View 2",     null);
        _fView.add(_tabsView);

        // panel: BBS + telnet...
        _pnlV1BBS =         new JPanel();
        _txtBBSNetAdr =     new JTextField("bbs.bearfather.net");
        _txtBBSNetPort =    new JTextField("23");
        _txtBBSLoc =        new JTextField();
        _btnTelnetConnect = new JButton("Connect");
        _btnTelnetConnect.addActionListener(new AL_BtnConnect());
        _txtBBSNetAdr.setBackground(OMUD.GUI_BG);
        _txtBBSNetPort.setBackground(OMUD.GUI_BG);
        _txtBBSLoc.setBackground(OMUD.GUI_BG);
        _txtBBSNetAdr.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSNetPort.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSLoc.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSLoc.setEditable(false);

        // char status fields...
        _pnlV1Char =        new JPanel();
        _txtCharRoomID =    new JTextField("RID: ?");
        _txtCharStatline =  new JTextField("Statline: ?");
        _txtCharExp =       new JTextField("XP: ?");
        _txtCharLastCmd =   new JTextField("CMD: ?");
        _txtCharRoomID.setEditable(false);
        _txtCharStatline.setEditable(false);
        _txtCharExp.setEditable(false);
        _txtCharLastCmd.setEditable(false);
        _txtCharRoomID.setBackground(OMUD.GUI_BG);
        _txtCharStatline.setBackground(OMUD.GUI_BG);
        _txtCharExp.setBackground(OMUD.GUI_BG);
        _txtCharLastCmd.setBackground(OMUD.GUI_BG);
        _txtCharRoomID.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtCharStatline.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtCharExp.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtCharLastCmd.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        setBBSLocText(OMUD.BBS_LOCATION_STRINGS[OMUD.eBBSLocation.OFFLINE.ordinal()]);

        // input...
        _pnlV1Input =       new JPanel();
        _txtBBSInput =      new OMUD_GUITextInput(this);
        _tglSingleMode =    new JToggleButton("SingleMode");
        _tglSingleMode.addItemListener(new IL_TglSelected());
        _txtBBSInput.setBackground(OMUD.GUI_BG);
        _txtBBSInput.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSInput.setCaretColor(OMUD.TERMINAL_LOCAL_INFO_FG);

        // layouts...
        GridBagLayout gbl =         new GridBagLayout();
        GridBagConstraints gblc =   new GridBagConstraints();
        layoutGUI_CharView1Terminal(gbl,    gblc);
        layoutGUI_CharView1BBS(gbl,         gblc);
        layoutGUI_CharView1Char(gbl,        gblc);
        layoutGUI_CharView1Input(gbl,       gblc);
    }

    private void layoutGUI_CharView1Terminal(GridBagLayout gbl, GridBagConstraints gblc){
        _scroll.setPreferredSize(new Dimension(TERMINAL_WIDTH, 0));
        _scroll.setMinimumSize(new Dimension(TERMINAL_WIDTH, 0));

        // terminal + parent panel...
        gblc.weightx =      0.0;
        gblc.weighty =      1.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.gridheight =   3;
        gblc.fill =         GridBagConstraints.VERTICAL;
        gbl.setConstraints(_scroll, gblc);
        _pnlV1.add(_scroll);
        _pnlV1.setLayout(gbl);
    }

    private void layoutGUI_CharView1BBS(GridBagLayout gbl, GridBagConstraints gblc){
        _txtBBSNetPort.setPreferredSize(new Dimension(50, 25));
        _txtBBSLoc.setPreferredSize(new Dimension(125, 25));
        _btnTelnetConnect.setPreferredSize(new Dimension(150, 25));

        // bbs address...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    3;
        gblc.gridheight =   2;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_txtBBSNetAdr, gblc);
        _pnlV1BBS.add(_txtBBSNetAdr);
        // bbs port...
        gblc.weightx =      0.0;
        gblc.weighty =      0.0;
        gblc.fill =         GridBagConstraints.NONE;
        gbl.setConstraints(_txtBBSNetPort, gblc);
        _pnlV1BBS.add(_txtBBSNetPort);
        // bbs loc...
        gbl.setConstraints(_txtBBSLoc, gblc);
        _pnlV1BBS.add(_txtBBSLoc);
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gbl.setConstraints(_btnTelnetConnect, gblc);
        _pnlV1BBS.add(_btnTelnetConnect);
        // panel...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlV1BBS, gblc);
        _pnlV1BBS.setLayout(gbl);
        _pnlV1.add(_pnlV1BBS);
    }

    private void layoutGUI_CharView1Char(GridBagLayout gbl, GridBagConstraints gblc){
        _txtCharRoomID.setPreferredSize(new Dimension(60, 25));
        _txtCharStatline.setPreferredSize(new Dimension(150, 25));
        _txtCharExp.setPreferredSize(new Dimension(150, 25));
        _txtCharLastCmd.setPreferredSize(new Dimension(150, 25));

        // room id...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    3;
        gblc.gridheight =   1;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_txtCharRoomID, gblc);
        _pnlV1Char.add(_txtCharRoomID);
        // statline...
        gbl.setConstraints(_txtCharStatline, gblc);
        _pnlV1Char.add(_txtCharStatline);
        // exp...
        gbl.setConstraints(_txtCharExp, gblc);
        _pnlV1Char.add(_txtCharExp);
        // last cmd...
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gbl.setConstraints(_txtCharLastCmd, gblc);
        _pnlV1Char.add(_txtCharLastCmd);
        // panel...
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlV1Char, gblc);
        _pnlV1Char.setLayout(gbl);
        _pnlV1.add(_pnlV1Char);
    }

    private void layoutGUI_CharView1Input(GridBagLayout gbl, GridBagConstraints gblc){
        _tglSingleMode.setPreferredSize(new Dimension(150, 25));

        // input box...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    1;
        gblc.gridheight =   GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_txtBBSInput, gblc);
        _pnlV1Input.add(_txtBBSInput);
        // input mode button...
        gblc.weightx =      0.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.NONE;
        gbl.setConstraints(_tglSingleMode, gblc);
        _pnlV1Input.add(_tglSingleMode);
        // panel...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlV1Input, gblc);
        _pnlV1Input.setLayout(gbl);
        _pnlV1.add(_pnlV1Input);
    }

    // --------------
    // GUI Frame: Char Info
    // --------------
    private void createGUI_FrameCharInfo(){
        GridBagLayout gbl =         new GridBagLayout();
        GridBagConstraints gblc =   new GridBagConstraints();

        // frame...
        _fInfo = new JFrame("Char Info");
        _fInfo.setMinimumSize(new Dimension(FINFO_MIN_WIDTH, FINFO_MIN_HEIGHT));

        // tabs...
        _pnlInfoInv =       new JPanel();
        _txtInfoTermDbg =   createOMUDTextArea();
        _txtInfoCmds =      createOMUDTextArea();
        _txtInfoOther =     createOMUDTextArea();
        _txtInfoWelcome =   createOMUDTextArea();
        _txtInfoRoom =      createOMUDTextArea();
        _txtInfoInv =       createOMUDTextArea();
        _txtInfoStats =     createOMUDTextArea();
        _txtInfoShop =      createOMUDTextArea();
        _tabsInfo = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        _tabsInfo.add("TermDbg",  _txtInfoTermDbg);
        _tabsInfo.add("MCmds",    new JScrollPane(_txtInfoCmds));
        _tabsInfo.add("MOther",   new JScrollPane(_txtInfoOther));
        _tabsInfo.add("MWelcome", new JScrollPane(_txtInfoWelcome));
        _tabsInfo.add("MRoom",    new JScrollPane(_txtInfoRoom));
        _tabsInfo.add("MInv",     new JScrollPane(_pnlInfoInv));
        _tabsInfo.add("MStats",   new JScrollPane(_txtInfoStats));
        _tabsInfo.add("MShop",    new JScrollPane(_txtInfoShop));
        _fInfo.add(_tabsInfo);

        // inventory stuff...
        _txtCharMoney = new JTextField("Money: ?");
        _txtCharMoney.setEditable(false);
        _txtCharMoney.setBackground(OMUD.GUI_BG);
        _txtCharMoney.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);

        // layouts...
        layoutGUI_CharInfo(gbl, gblc);
    }

    private void layoutGUI_CharInfo(GridBagLayout gbl, GridBagConstraints gblc){
        _txtCharMoney.setPreferredSize(new Dimension(0, 25));

        // text area...
        gblc.weightx =      1.0;
        gblc.weighty =      1.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.gridheight =   1;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_txtInfoInv, gblc);
        _pnlInfoInv.add(_txtInfoInv);
        _pnlInfoInv.setLayout(gbl);
        // money...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.gridheight =   GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_txtCharMoney, gblc);
        _pnlInfoInv.add(_txtCharMoney);
    }

    // --------------
    // GUI Misc
    // --------------
    private JTextArea createOMUDTextArea(){
        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.getCaret().setVisible(true);
        txt.setLineWrap(false);
        txt.setBackground(OMUD.GUI_BG);
        txt.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        txt.setCaretColor(OMUD.TERMINAL_LOCAL_INFO_FG);
        txt.setFont(OMUD.getTerminalFont());
        return txt;
    }

    private void setBBSLocText(String text){_txtBBSLoc.setText("BBSLoc: " + text);}

    // --------------
    // GUI Events
    // --------------
    public void notifyInputText(String text){_omt.sendText(text);}

    private class AL_BtnConnect implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (_omt.isConnected())
                 _omt.disconnect(true);
            else _omt.connect(_txtBBSNetAdr.getText(), _txtBBSNetPort.getText());

            SwingUtilities.invokeLater(new Runnable(){public void run(){
                _txtBBSInput.requestFocus();
            }});        
        }
    }

    private class IL_TglSelected implements ItemListener {
        public void itemStateChanged(ItemEvent event) {
            _txtBBSInput.setSingleMode(event.getStateChange() == ItemEvent.SELECTED ? true : false);            
            SwingUtilities.invokeLater(new Runnable(){public void run(){
                _txtBBSInput.requestFocus();
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
                _txtBBSInput.requestFocus();
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
            setBBSLocText(OMUD.BBS_LOCATION_STRINGS[OMUD.eBBSLocation.BBS.ordinal()]);
        }});
    }

    public void notifyTelnetDisconnected(){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _btnTelnetConnect.setText("Connect");
            setBBSLocText(OMUD.BBS_LOCATION_STRINGS[OMUD.eBBSLocation.OFFLINE.ordinal()]);
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
                _txtInfoTermDbg.setText(sbTermDebug.toString());
                _txtInfoTermDbg.setCaretPosition(caret_pos);
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
            setBBSLocText(OMUD.BBS_LOCATION_STRINGS[eLoc.ordinal()]);
        }});
    }

    public void notifyMUDUserCmd(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtCharLastCmd.setText("CMD: " + strText);
            _txtInfoCmds.setText(_txtInfoCmds.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
        }});
    }

    public void notifyMUDOther(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtInfoOther.setText(_txtInfoOther.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
            _txtInfoOther.setCaretPosition(_txtInfoOther.getText().length());
        }});
    }

    public void notifyMUDStatline(final OMUD_MMUD.DataStatline dataStatline){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            StringBuilder sb = new StringBuilder();
            sb.append(dataStatline.hp_str + "=" + dataStatline.hp_cur + "/" + dataStatline.hp_max + (dataStatline.hp_mod ? "*" : ""));
            if (dataStatline.ma_cur > 0)
                sb.append(", " + dataStatline.ma_str + "=" + dataStatline.ma_cur + "/" + dataStatline.ma_max + (dataStatline.ma_mod ? "*" : ""));
            sb.append(" " + OMUD_MMUD.REST_STATE_STRINGS[dataStatline.rest.ordinal()]);
            _txtCharStatline.setText(sb.toString());
        }});
    }

    public void notifyMUDWelcome(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtInfoWelcome.setText(strText);
        }});
    }

    public void notifyMUDRoom(final OMUD_MMUD.DataRoom dataRoom){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _tabsInfo.setSelectedIndex(4);
            _txtCharRoomID.setText("RID: " + dataRoom.roomID);

            StringBuilder sb = new StringBuilder();
            sb.append("[RoomID]: "          + dataRoom.roomID       + "\n\n");
            sb.append("[RoomName]: "        + dataRoom.name         + " (" + OMUD_MMUD.ROOM_LIGHT_STRINGS[dataRoom.light.ordinal()] + ")\n\n");
            sb.append("[RoomItems]\n"       + dataRoom.items        + "\n\n");
            sb.append("[RoomItemsHidden]\n" + dataRoom.items_hidden + "\n\n");
            sb.append("[RoomUnits]\n"       + dataRoom.units        + "\n\n");
            sb.append("[RoomExits]\n"       + dataRoom.exits        + "\n\n");
            sb.append("[RoomDesc]\n"        + dataRoom.desc);
            _txtInfoRoom.setText(sb.toString());
            _txtInfoRoom.setCaretPosition(0);
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
            _txtInfoInv.setText(sb.toString());
            _txtInfoInv.setCaretPosition(0);
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

            _txtInfoStats.setText(sb.toString());
            _txtInfoStats.setCaretPosition(0);            
        }});
    }

    public void notifyMUDExp(final OMUD_MMUD.DataExp dataExp){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _txtCharExp.setText("XP: " + dataExp.next_rem + String.format(" (%.0f", ((float) dataExp.cur_total / dataExp.next_total) * 100) + "%) [" + dataExp.cur_total + "/" + dataExp.next_total + "]");
            _txtCharExp.setCaretPosition(0);
        }});
    }

    public void notifyMUDShop(final OMUD_MMUD.DataShop dataShop, final String strRoomID, final String strRoomName){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _tabsInfo.setSelectedIndex(7);

            StringBuilder sb = new StringBuilder();
            sb.append("[RoomID]: "    + strRoomID   + "\n");
            sb.append("[RoomName]: "  + strRoomName + "\n\n");
            sb.append("--------------------\n");
            sb.append("[Qty] Name: Price\n");
            sb.append("--------------------\n");
            for (int i = 0; i < dataShop.items.size(); ++i){
                
                String strFill = "";
                int fill_len = 30 - dataShop.items.get(i).name.length();
                if (fill_len > 0)
                    strFill = OMUD.getFillString(" ", fill_len);
                
                sb.append("[" + dataShop.items.get(i).qty + "]:\t" + dataShop.items.get(i).name + strFill + dataShop.items.get(i).price + "\n");
            }

            _txtInfoShop.setText(sb.toString());
            _txtInfoShop.setCaretPosition(0);
        }});        
    }

    public void notifyMUDParty(){}
    public void notifyMUDSpells(){}
    public void notifyMUDCombat(){}
}
