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
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.util.ArrayList;

public class OMUD_GUIFrameView extends JFrame{
    private OMUD_Telnet         _omt =              null;
    private OMUD_TelnetParser   _omtp =             null;
    private OMUD_GUIScrollPane  _scroll =           null;
    private OMUD_GUITerminal    _term =             null;
    private OMUD_GUITextInput   _inputTelnet =      null;
    private OMUD_GUITextField   _lblBBSNetAddr =    null;
    private OMUD_GUITextField   _lblBBSNetPort =    null;
    private OMUD_GUITextField   _lblBBSLoc =        null;
    private OMUD_GUITextField   _lblCharRoomID =    null;
    private OMUD_GUITextField   _lblCharStatline =  null;
    private OMUD_GUITextField   _lblCharExp =       null;
    private OMUD_GUITextField   _lblCharLastCmd =   null;
    private JButton             _btnBBSConnect =    null;
    private JToggleButton       _tglSingleMode =    null;
    private JPanel              _pnlTerminal =      null;
    private JPanel              _pnlBBS =           null;
    private JPanel              _pnlChar =          null;
    private JPanel              _pnlInput =         null;
    private JPanel              _pnlView =          null;
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
        _pnlTerminal =  new JPanel();
        _scroll =       new OMUD_GUIScrollPane();
        _term =         new OMUD_GUITerminal(_scroll);
        _term.addMouseListener(new MA_TerminalFocus());
        _scroll.setViewportView(_term);
        _scroll.removeCaretListeners(_term);

        // panel: BBS + telnet...
        _pnlBBS =           new JPanel();
        _lblBBSNetAddr =    new OMUD_GUITextField("bbs.bearfather.net", true, false);
        _lblBBSNetPort =    new OMUD_GUITextField("23", true, false);
        _lblBBSLoc =        new OMUD_GUITextField();
        _btnBBSConnect =    new JButton("Connect");
        _btnBBSConnect.addActionListener(new AL_BtnConnect());

        // char status fields...
        _pnlChar =          new JPanel();
        _lblCharRoomID =    new OMUD_GUITextField("RID: ?");
        _lblCharStatline =  new OMUD_GUITextField("Statline: ?");
        _lblCharExp =       new OMUD_GUITextField("XP: ?");
        _lblCharLastCmd =   new OMUD_GUITextField("CMD: ?");
        setBBSLocText(OMUD.BBS_LOCATION_STRINGS[OMUD.eBBSLocation.OFFLINE.ordinal()]);

        // input...
        _pnlInput =         new JPanel();
        _inputTelnet =      new OMUD_GUITextInput(omtie);
        _tglSingleMode =    new JToggleButton("SingleMode");
        _tglSingleMode.addItemListener(new IL_TglSelected());

        // main panel & tabs...
        _pnlView =  new JPanel();
        _tabsView = new JTabbedPane();
        _tabsView.add("Terminal View",  _pnlView);
        _tabsView.add("Fun View 1",     null);
        _tabsView.add("Fun View 2",     null);
        add(_tabsView);

        // layouts...
        GridBagLayout gbl = new GridBagLayout();
        layoutV1Terminal(gbl);
        layoutV1BBS(gbl);
        layoutV1Char(gbl);
        layoutV1Input(gbl);
        pack();
    }

    private void layoutV1Terminal(GridBagLayout gbl){
        GridBagConstraints gblc = new GridBagConstraints();
        _scroll.setPreferredSize(   new Dimension(TERMINAL_WIDTH, 0));
        _scroll.setMinimumSize(     new Dimension(TERMINAL_WIDTH, 0));

        // terminal...
        gblc.weightx =      0.0;
        gblc.weighty =      1.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.gridheight =   3;
        gblc.fill =         GridBagConstraints.VERTICAL;
        gbl.setConstraints(_scroll, gblc);

        _pnlView.setLayout(gbl);
        _pnlView.add(_scroll);
    }

    private void layoutV1BBS(GridBagLayout gbl){
        GridBagConstraints gblc = new GridBagConstraints();
        _lblBBSNetAddr.setPreferredSize(new Dimension(200,  25));
        _lblBBSNetPort.setPreferredSize(new Dimension(50,   25));
        _lblBBSLoc.setPreferredSize(    new Dimension(125,  25));
        _btnBBSConnect.setPreferredSize(new Dimension(150,  25));

        // address...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    3;
        gblc.gridheight =   2;
        gblc.fill =         GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(_lblBBSNetAddr,  gblc);
        // port + loc + connect...
        gblc.weightx =      0.0;
        gblc.fill =         GridBagConstraints.NONE;
        gbl.setConstraints(_lblBBSNetPort,  gblc);
        gbl.setConstraints(_lblBBSLoc,      gblc);
        gbl.setConstraints(_btnBBSConnect,  gblc);
        // panel...
        gblc.weightx =      1.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(_pnlBBS, gblc);
        _pnlBBS.setLayout(gbl);
        _pnlBBS.add(_lblBBSNetAddr);
        _pnlBBS.add(_lblBBSNetPort);
        _pnlBBS.add(_lblBBSLoc);
        _pnlBBS.add(_btnBBSConnect);

        _pnlView.add(_pnlBBS);
    }

    private void layoutV1Char(GridBagLayout gbl){
        GridBagConstraints gblc = new GridBagConstraints();
        _lblCharRoomID.setPreferredSize(    new Dimension(60,   25));
        _lblCharStatline.setPreferredSize(  new Dimension(150,  25));
        _lblCharExp.setPreferredSize(       new Dimension(150,  25));
        _lblCharLastCmd.setPreferredSize(   new Dimension(150,  25));

        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    3;
        gblc.gridheight =   1;
        gblc.fill =         GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(_lblCharRoomID,      gblc);
        gbl.setConstraints(_lblCharStatline,    gblc);
        gbl.setConstraints(_lblCharExp,         gblc);
        gbl.setConstraints(_lblCharLastCmd,     gblc);
        // panel...
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gbl.setConstraints(_pnlChar, gblc);
        _pnlChar.setLayout(gbl);
        _pnlChar.add(_lblCharRoomID);
        _pnlChar.add(_lblCharStatline);
        _pnlChar.add(_lblCharExp);
        _pnlChar.add(_lblCharLastCmd);

        _pnlView.add(_pnlChar);
    }

    private void layoutV1Input(GridBagLayout gbl){
        GridBagConstraints gblc = new GridBagConstraints();
        _inputTelnet.setPreferredSize(  new Dimension(300, 25));
        _tglSingleMode.setPreferredSize(new Dimension(150, 25));

        // input...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    1;
        gblc.gridheight =   GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(_inputTelnet, gblc);
        // input mode button...
        gblc.weightx =      0.0;
        gblc.fill =         GridBagConstraints.NONE;
        gbl.setConstraints(_tglSingleMode, gblc);
        // panel...
        gblc.weightx =      1.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(_pnlInput, gblc);
        _pnlInput.setLayout(gbl);
        _pnlInput.add(_inputTelnet);
        _pnlInput.add(_tglSingleMode);

        _pnlView.add(_pnlInput);
    }

    private void setBBSLocText(String text){_lblBBSLoc.setText("BBSLoc: " + text);}

    // --------------
    // GUI Events
    // --------------
    public void finalizeGUI(){
        _term.finalizeGUI();
        _inputTelnet.requestFocus();

        // --------------
        // Optional Auto-Stuff
        // --------------
        // auto single-mode...
        // _tglSingleMode.setSelected(true);
        // auto connect...
        // _omt.connect(_lblBBSNetAddr.getText(), _lblBBSNetPort.getText());
    }

    private class AL_BtnConnect implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (_omt.isConnected())
                 _omt.disconnect(true);
            else _omt.connect(_lblBBSNetAddr.getText(), _lblBBSNetPort.getText());

            SwingUtilities.invokeLater(new Runnable(){public void run(){
                _inputTelnet.requestFocus();
            }});
        }
    }

    private class IL_TglSelected implements ItemListener {
        public void itemStateChanged(ItemEvent event) {
            _inputTelnet.setSingleMode(event.getStateChange() == ItemEvent.SELECTED ? true : false);
            SwingUtilities.invokeLater(new Runnable(){public void run(){
                _inputTelnet.requestFocus();
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
                _inputTelnet.requestFocus();
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
    public void processMUDLocation(final OMUD.eBBSLocation eLoc){
        setBBSLocText(OMUD.BBS_LOCATION_STRINGS[eLoc.ordinal()]);
    }

    public void processMUDUserCmd(final String strText){
        _lblCharLastCmd.setText("CMD: " + strText);
    }

    public void processMUDStatline(final OMUD_MMUD.DataStatline dataStatline){
        StringBuilder sb = new StringBuilder();
        sb.append(dataStatline.hp_str + "=" + dataStatline.hp_cur + "/" + dataStatline.hp_max + (dataStatline.hp_mod ? "*" : ""));
        if (dataStatline.ma_str.length() > 0)
            sb.append(", " + dataStatline.ma_str + "=" + dataStatline.ma_cur + "/" + dataStatline.ma_max + (dataStatline.ma_mod ? "*" : ""));
        sb.append(" " + OMUD_MMUD.REST_STATE_STRINGS[dataStatline.rest.ordinal()]);
        _lblCharStatline.setText(sb.toString());
    }

    public void processMUDExp(final OMUD_MMUD.DataExp dataExp){
        _lblCharExp.setText("XP: " + dataExp.next_rem + String.format(" (%.0f", ((float) dataExp.cur_total / dataExp.next_total) * 100) + "%) [" + dataExp.cur_total + "/" + dataExp.next_total + "]");
        _lblCharExp.setCaretPosition(0);
    }

    public void processMUDRoom(final OMUD_MMUD.DataRoom dataRoom){
        _lblCharRoomID.setText("RID: " + dataRoom.roomID);
    }
}