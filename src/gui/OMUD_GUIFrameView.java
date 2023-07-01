import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.datatransfer.StringSelection;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.util.ArrayList;

public class OMUD_GUIFrameView extends JFrame{
	private OMUD_Telnet         _omt =    			null;
	private OMUD_TelnetParser 	_omtp = 			null;
    private OMUD_GUIScrollPane  _scroll =  			null;
    private OMUD_GUITerminal    _term =    			null;
	private OMUD_GUITextInput   _txtBBSInput =      null;
    private JTextField 			_txtBBSNetAdr =     null;
    private JTextField 			_txtBBSNetPort =    null;
    private JTextField 			_txtBBSLoc =        null;
    private JTextField 			_txtCharRoomID =    null;
    private JTextField 			_txtCharStatline =  null;
    private JTextField 			_txtCharExp =       null;
    private JTextField 			_txtCharLastCmd =   null;
    private JButton             _btnBBSConnect = 	null;
    private JToggleButton       _tglSingleMode =    null;
    private JPanel              _pnlView =          null;
    private JPanel              _pnlBBS =         	null;
    private JPanel              _pnlChar =        	null;
    private JPanel              _pnlInput =       	null;
    private JTabbedPane         _tabsView =         null;
    private static final int TERMINAL_WIDTH  =      675;
    private static final int FRAME_MIN_WIDTH  =     690;
    private static final int FRAME_MIN_HEIGHT =     550;

	OMUD_GUIFrameView(OMUD_ITextInputEvents omtie, OMUD_Telnet omt, OMUD_TelnetParser omtp){
        setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));
		setTitle("Char View");

		_omt  = omt;
		_omtp = omtp;

        // terminal/scroll...
        _scroll = new OMUD_GUIScrollPane();
        _term =   new OMUD_GUITerminal(_scroll);
        _term.addMouseListener(new MA_TerminalFocus());
        _scroll.setViewportView(_term);
        _scroll.removeCaretListeners(_term);

        // terminal panel & tabs...
        _pnlView =    new JPanel();
        _tabsView = new JTabbedPane();
        _tabsView.add("Terminal View",  _pnlView);
        _tabsView.add("Fun View 1",     null);
        _tabsView.add("Fun View 2",     null);
        add(_tabsView);

        // panel: BBS + telnet...
        _pnlBBS =         new JPanel();
        _txtBBSNetAdr =     new JTextField("bbs.bearfather.net");
        _txtBBSNetPort =    new JTextField("23");
        _txtBBSLoc =        new JTextField();
        _btnBBSConnect = 	new JButton("Connect");
        _btnBBSConnect.addActionListener(new AL_BtnConnect());
        _txtBBSNetAdr.setBackground(OMUD.GUI_BG);
        _txtBBSNetPort.setBackground(OMUD.GUI_BG);
        _txtBBSLoc.setBackground(OMUD.GUI_BG);
        _txtBBSNetAdr.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSNetPort.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSLoc.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSLoc.setEditable(false);

        // char status fields...
        _pnlChar =        new JPanel();
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
        _pnlInput =       new JPanel();
        _txtBBSInput =      new OMUD_GUITextInput(omtie);
        _tglSingleMode =    new JToggleButton("SingleMode");
        _tglSingleMode.addItemListener(new IL_TglSelected());
        _txtBBSInput.setBackground(OMUD.GUI_BG);
        _txtBBSInput.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        _txtBBSInput.setCaretColor(OMUD.TERMINAL_LOCAL_INFO_FG);

        // layouts...
        GridBagLayout gbl =         new GridBagLayout();
        GridBagConstraints gblc =   new GridBagConstraints();
        layoutCharView1Terminal(gbl,    gblc);
        layoutCharView1BBS(gbl,         gblc);
        layoutCharView1Char(gbl,        gblc);
        layoutCharView1Input(gbl,       gblc);
        pack();
	}

    private void layoutCharView1Terminal(GridBagLayout gbl, GridBagConstraints gblc){
        _scroll.setPreferredSize(new Dimension(TERMINAL_WIDTH, 0));
        _scroll.setMinimumSize(new Dimension(TERMINAL_WIDTH, 0));

        // terminal + parent panel...
        gblc.weightx =      0.0;
        gblc.weighty =      1.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.gridheight =   3;
        gblc.fill =         GridBagConstraints.VERTICAL;
        gbl.setConstraints(_scroll, gblc);
        _pnlView.add(_scroll);
        _pnlView.setLayout(gbl);
    }

    private void layoutCharView1BBS(GridBagLayout gbl, GridBagConstraints gblc){
        _txtBBSNetPort.setPreferredSize(new Dimension(50, 25));
        _txtBBSLoc.setPreferredSize(new Dimension(125, 25));
        _btnBBSConnect.setPreferredSize(new Dimension(150, 25));

        // bbs address...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    3;
        gblc.gridheight =   2;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_txtBBSNetAdr, gblc);
        _pnlBBS.add(_txtBBSNetAdr);
        // bbs port...
        gblc.weightx =      0.0;
        gblc.weighty =      0.0;
        gblc.fill =         GridBagConstraints.NONE;
        gbl.setConstraints(_txtBBSNetPort, gblc);
        _pnlBBS.add(_txtBBSNetPort);
        // bbs loc...
        gbl.setConstraints(_txtBBSLoc, gblc);
        _pnlBBS.add(_txtBBSLoc);
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gbl.setConstraints(_btnBBSConnect, gblc);
        _pnlBBS.add(_btnBBSConnect);
        // panel...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlBBS, gblc);
        _pnlBBS.setLayout(gbl);
        _pnlView.add(_pnlBBS);
    }

    private void layoutCharView1Char(GridBagLayout gbl, GridBagConstraints gblc){
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
        _pnlChar.add(_txtCharRoomID);
        // statline...
        gbl.setConstraints(_txtCharStatline, gblc);
        _pnlChar.add(_txtCharStatline);
        // exp...
        gbl.setConstraints(_txtCharExp, gblc);
        _pnlChar.add(_txtCharExp);
        // last cmd...
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gbl.setConstraints(_txtCharLastCmd, gblc);
        _pnlChar.add(_txtCharLastCmd);
        // panel...
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlChar, gblc);
        _pnlChar.setLayout(gbl);
        _pnlView.add(_pnlChar);
    }

    private void layoutCharView1Input(GridBagLayout gbl, GridBagConstraints gblc){
        _tglSingleMode.setPreferredSize(new Dimension(150, 25));

        // input box...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    1;
        gblc.gridheight =   GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_txtBBSInput, gblc);
        _pnlInput.add(_txtBBSInput);
        // input mode button...
        gblc.weightx =      0.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.NONE;
        gbl.setConstraints(_tglSingleMode, gblc);
        _pnlInput.add(_tglSingleMode);
        // panel...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlInput, gblc);
        _pnlInput.setLayout(gbl);
        _pnlView.add(_pnlInput);
    }

    private void setBBSLocText(String text){_txtBBSLoc.setText("BBSLoc: " + text);}

    // --------------
    // GUI Events
    // --------------
    public void finalizeGUI(){
    	_term.finalizeGUI();
        _txtBBSInput.requestFocus();

		// --------------
		// Optional Auto-Stuff
		// --------------
        // auto single-mode...
		// _tglSingleMode.setSelected(true);
        // auto connect...
		// _omt.connect(_txtBBSNetAdr.getText(), _txtBBSNetPort.getText());
    }

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
    public void processTelnetConnected(){
        _btnBBSConnect.setText("Disconnect");
        setBBSLocText(OMUD.BBS_LOCATION_STRINGS[OMUD.eBBSLocation.BBS.ordinal()]);
    }

    public void processTelnetDisconnected(){
        _btnBBSConnect.setText("Connect");
        setBBSLocText(OMUD.BBS_LOCATION_STRINGS[OMUD.eBBSLocation.OFFLINE.ordinal()]);
    }

    public void processTelnetParsed(final OMUD_Buffer omb, final ArrayList<OMUD_IBufferMod> arrlBMods){
        // allow the terminal to render buffer mods...
        _term.render(omb, arrlBMods);

        // force update scrollbars...
        _scroll.scrollToBottom();
        // horiz update requires GUI update delay...
        //SwingUtilities.invokeLater(new Runnable(){public void run(){
        //    sp.getHorizontalScrollBar().setValue(0);}});
    }

    // --------------
    // MUD Events
    // --------------
    public void processMUDAutoCmd(final String strCmd){
        _omt.sendText(strCmd);
    }

    public void processMUDLocation(final OMUD.eBBSLocation eLoc){
        setBBSLocText(OMUD.BBS_LOCATION_STRINGS[eLoc.ordinal()]);
    }

    public void processMUDUserCmd(final String strText){
        _txtCharLastCmd.setText("CMD: " + strText);
    }

    public void processMUDStatline(final OMUD_MMUD.DataStatline dataStatline){
        StringBuilder sb = new StringBuilder();
        sb.append(dataStatline.hp_str + "=" + dataStatline.hp_cur + "/" + dataStatline.hp_max + (dataStatline.hp_mod ? "*" : ""));
        if (dataStatline.ma_cur > 0)
            sb.append(", " + dataStatline.ma_str + "=" + dataStatline.ma_cur + "/" + dataStatline.ma_max + (dataStatline.ma_mod ? "*" : ""));
        sb.append(" " + OMUD_MMUD.REST_STATE_STRINGS[dataStatline.rest.ordinal()]);
        _txtCharStatline.setText(sb.toString());
    }

    public void processMUDExp(final OMUD_MMUD.DataExp dataExp){
        _txtCharExp.setText("XP: " + dataExp.next_rem + String.format(" (%.0f", ((float) dataExp.cur_total / dataExp.next_total) * 100) + "%) [" + dataExp.cur_total + "/" + dataExp.next_total + "]");
        _txtCharExp.setCaretPosition(0);
    }

    public void processMUDRoom(final OMUD_MMUD.DataRoom dataRoom){
        _txtCharRoomID.setText("RID: " + dataRoom.roomID);
    }
}