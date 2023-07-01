import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OMUD_GUIFrameInfo extends JFrame{
    private SimpleDateFormat 	_sdf = 				null; 
    private JTextField          _txtInvWealth =    	null;
    private JTextArea 			_txtFullTermDbg = 	null;
    private JTextArea           _txtFullCmds =      null;
    private JTextArea           _txtFullOther =   	null;
    private JTextArea           _txtFullWelcome =   null;
    private JTextArea           _txtFullRoom =      null;
    private JTextArea           _txtFullInv =       null;
    private JTextArea           _txtFullStats =     null;
    private JTextArea           _txtFullShop =      null;
    private JPanel              _pnlInv =       	null;
    private JTabbedPane         _tabsInfo =         null;
    private static final int FRAME_MIN_WIDTH  = 700;
    private static final int FRAME_MIN_HEIGHT = 550;

	OMUD_GUIFrameInfo(){
        setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));
        setTitle("Char Info");

        _sdf = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");

        // tabs...
        _pnlInv =      		new JPanel();
        _txtFullTermDbg =   createOMUDTextArea();
        _txtFullCmds =      createOMUDTextArea();
        _txtFullOther =   	createOMUDTextArea();
        _txtFullWelcome =   createOMUDTextArea();
        _txtFullRoom =      createOMUDTextArea();
        _txtFullInv =       createOMUDTextArea();
        _txtFullStats =     createOMUDTextArea();
        _txtFullShop =      createOMUDTextArea();
        _tabsInfo = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        _tabsInfo.add("TermDbg",  _txtFullTermDbg);
        _tabsInfo.add("MCmds",    	new JScrollPane(_txtFullCmds));
        _tabsInfo.add("MOther", 	new JScrollPane(_txtFullOther));
        _tabsInfo.add("MWelcome", 	new JScrollPane(_txtFullWelcome));
        _tabsInfo.add("MRoom",    	new JScrollPane(_txtFullRoom));
        _tabsInfo.add("MInv",    	new JScrollPane(_pnlInv));
        _tabsInfo.add("MStats",   	new JScrollPane(_txtFullStats));
        _tabsInfo.add("MShop",    	new JScrollPane(_txtFullShop));
        add(_tabsInfo);

        // inventory stuff...
        _txtInvWealth = new JTextField("Wealth: ?");
        _txtInvWealth.setEditable(false);
        _txtInvWealth.setBackground(OMUD.GUI_BG);
        _txtInvWealth.setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);

        // layouts...
        GridBagLayout gbl =         new GridBagLayout();
        GridBagConstraints gblc =   new GridBagConstraints();
        layoutCharInfo(gbl, gblc);
        pack();
    }

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

    private void layoutCharInfo(GridBagLayout gbl, GridBagConstraints gblc){
        _txtInvWealth.setPreferredSize(new Dimension(0, 25));

        // text area...
        gblc.weightx =      1.0;
        gblc.weighty =      1.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.gridheight =   1;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_txtFullInv, gblc);
        _pnlInv.add(_txtFullInv);
        _pnlInv.setLayout(gbl);
        // wealth...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.gridheight =   GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_txtInvWealth, gblc);
        _pnlInv.add(_txtInvWealth);
    }

    // --------------
    // Telnet Events
    // --------------
    public void processTelnetParsed(final OMUD_Buffer omb){
        if (isVisible() && _tabsInfo.getSelectedIndex() == 0){
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
            _txtFullTermDbg.setText(sbTermDebug.toString());
            _txtFullTermDbg.setCaretPosition(caret_pos);
        }
	}

    // --------------
    // MUD Events
    // --------------
    public void processMUDUserCmd(final String strText){
        _txtFullCmds.setText(_txtFullCmds.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
    }

    public void processMUDOther(final String strText){
        _txtFullOther.setText(_txtFullOther.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
        _txtFullOther.setCaretPosition(_txtFullOther.getText().length());
    }

    public void processMUDWelcome(final String strText){
        _txtFullWelcome.setText(strText);
    }

    public void processMUDRoom(final OMUD_MMUD.DataRoom dataRoom){
        _tabsInfo.setSelectedIndex(4);

        StringBuilder sb = new StringBuilder();
        sb.append("[RoomID]: "          + dataRoom.roomID       + "\n\n");
        sb.append("[RoomName]: "        + dataRoom.name         + " (" + OMUD_MMUD.ROOM_LIGHT_STRINGS[dataRoom.light.ordinal()] + ")\n\n");
        sb.append("[RoomItems]\n"       + dataRoom.items        + "\n\n");
        sb.append("[RoomItemsHidden]\n" + dataRoom.items_hidden + "\n\n");
        sb.append("[RoomUnits]\n"       + dataRoom.units        + "\n\n");
        sb.append("[RoomExits]\n"       + dataRoom.exits        + "\n\n");
        sb.append("[RoomDesc]\n"        + dataRoom.desc);
        _txtFullRoom.setText(sb.toString());
        _txtFullRoom.setCaretPosition(0);
    }

    public void processMUDInv(final OMUD_MMUD.DataInv dataInv){
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
        _txtFullInv.setText(sb.toString());
        _txtFullInv.setCaretPosition(0);
    }

    public void processMUDStats(final OMUD_MMUD.DataStats dataStats){
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

        _txtFullStats.setText(sb.toString());
        _txtFullStats.setCaretPosition(0);            
    }

    public void processMUDShop(final OMUD_MMUD.DataShop dataShop, final String strRoomID, final String strRoomName){
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

        _txtFullShop.setText(sb.toString());
        _txtFullShop.setCaretPosition(0);
    }
}