import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OMUD_GUIFrameInfo extends JFrame{
    private SimpleDateFormat 	_sdf = 			null; 
    private OMUD_GUITextField   _lblInvRunic =  null;
    private OMUD_GUITextField   _lblInvPlat =   null;
    private OMUD_GUITextField   _lblInvGold =   null;
    private OMUD_GUITextField   _lblInvSilver = null;
    private OMUD_GUITextField   _lblInvCopper = null;
    private OMUD_GUITextField   _lblInvWealth = null;
    private OMUD_GUITextArea 	_txtTermDbg = 	null;
    private OMUD_GUITextArea    _txtCmds =      null;
    private OMUD_GUITextArea    _txtOther =   	null;
    private OMUD_GUITextArea    _txtWelcome =   null;
    private OMUD_GUITextArea    _txtRoom =      null;
    private OMUD_GUITextArea    _txtInv =       null;
    private OMUD_GUITextArea    _txtStats =     null;
    private OMUD_GUITextArea    _txtShop =      null;
    private JPanel              _pnlInv =       null;
    private JPanel              _pnlInvMoney =  null;
    private JTabbedPane         _tabsInfo =     null;
    private static final int FRAME_MIN_WIDTH  = 700;
    private static final int FRAME_MIN_HEIGHT = 550;

	OMUD_GUIFrameInfo(){
        setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));
        setTitle("Char Info");

        _sdf = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");

        // inventory stuff...
        _lblInvRunic = 	new OMUD_GUITextField("Runic: ?",  false, true);
        _lblInvPlat = 	new OMUD_GUITextField("Plat: ?",   false, true);
        _lblInvGold = 	new OMUD_GUITextField("Gold: ?",   false, true);
        _lblInvSilver = new OMUD_GUITextField("Silver: ?", false, true);
        _lblInvCopper = new OMUD_GUITextField("Copper: ?", false, true);
        _lblInvWealth = new OMUD_GUITextField("Wealth: ?", false, true);

        // tabs...
        _pnlInv =  		new JPanel();
        _pnlInvMoney =  new JPanel();
        _txtTermDbg =  	new OMUD_GUITextArea();
        _txtCmds =     	new OMUD_GUITextArea();
        _txtOther =   	new OMUD_GUITextArea();
        _txtWelcome =  	new OMUD_GUITextArea();
        _txtRoom =     	new OMUD_GUITextArea();
        _txtInv =      	new OMUD_GUITextArea();
        _txtStats =    	new OMUD_GUITextArea();
        _txtShop =     	new OMUD_GUITextArea();
        _tabsInfo = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        _tabsInfo.add("TermDbg",  _txtTermDbg);
        _tabsInfo.add("MCmds",    	new JScrollPane(_txtCmds));
        _tabsInfo.add("MOther", 	new JScrollPane(_txtOther));
        _tabsInfo.add("MWelcome", 	new JScrollPane(_txtWelcome));
        _tabsInfo.add("MRoom",    	new JScrollPane(_txtRoom));
        _tabsInfo.add("MInv",    	new JScrollPane(_pnlInv));
        _tabsInfo.add("MStats",   	new JScrollPane(_txtStats));
        _tabsInfo.add("MShop",    	new JScrollPane(_txtShop));
        add(_tabsInfo);

        // layouts...
		GridBagLayout gbl = new GridBagLayout();
        layoutInv(gbl);
        pack();
    }

    private void layoutInv(GridBagLayout gbl){
        GridBagConstraints gblc = new GridBagConstraints();
        _lblInvRunic.setPreferredSize( 	new Dimension(100, 25));
        _lblInvPlat.setPreferredSize(	new Dimension(100, 25));
        _lblInvGold.setPreferredSize(	new Dimension(100, 25));
        _lblInvSilver.setPreferredSize(	new Dimension(100, 25));
        _lblInvCopper.setPreferredSize(	new Dimension(100, 25));
        _lblInvWealth.setPreferredSize(	new Dimension(100, 25));

        // money labels...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    5;
        gblc.gridheight =   GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(_pnlInvMoney, 	gblc);
        gbl.setConstraints(_lblInvRunic, 	gblc);
        gbl.setConstraints(_lblInvPlat, 	gblc);
        gbl.setConstraints(_lblInvGold, 	gblc);
        gbl.setConstraints(_lblInvSilver, 	gblc);
        gbl.setConstraints(_lblInvCopper, 	gblc);
        gbl.setConstraints(_lblInvWealth, 	gblc);
		_pnlInvMoney.setLayout(gbl);
        _pnlInvMoney.add(_lblInvRunic);
        _pnlInvMoney.add(_lblInvPlat);
        _pnlInvMoney.add(_lblInvGold);
        _pnlInvMoney.add(_lblInvSilver);
        _pnlInvMoney.add(_lblInvCopper);
        _pnlInvMoney.add(_lblInvWealth);

        // text area + panel...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    GridBagConstraints.REMAINDER;
        gblc.gridheight =   1;
        gblc.fill =         GridBagConstraints.BOTH;
        gbl.setConstraints(_pnlInvMoney, gblc);
        gblc.weighty =      1.0;
        gblc.gridheight =   GridBagConstraints.REMAINDER;
        gbl.setConstraints(_txtInv, gblc);
        gbl.setConstraints(_pnlInv, gblc);
        _pnlInv.setLayout(gbl);
        _pnlInv.add(_pnlInvMoney);
        _pnlInv.add(_txtInv);
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
            _txtTermDbg.setText(sbTermDebug.toString());
            _txtTermDbg.setCaretPosition(caret_pos);
        }
	}

    // --------------
    // MUD Events
    // --------------
    public void processMUDUserCmd(final String strText){
        _txtCmds.setText(_txtCmds.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
    }

    public void processMUDOther(final String strText){
        _txtOther.setText(_txtOther.getText() + _sdf.format(new Date()) + ": " + strText + "\n");
        _txtOther.setCaretPosition(_txtOther.getText().length());
    }

    public void processMUDWelcome(final String strText){
        _txtWelcome.setText(strText);
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
        _txtRoom.setText(sb.toString());
        _txtRoom.setCaretPosition(0);
    }

    public void processMUDInv(final OMUD_MMUD.DataInv dataInv){
        _tabsInfo.setSelectedIndex(5);

        StringBuilder sb = new StringBuilder();
        sb.append("\n[InvEnc]: (" + dataInv.enc_level + ") " + dataInv.enc_cur + "/" + dataInv.enc_max + String.format(" [%.0f", ((float) dataInv.enc_cur / dataInv.enc_max) * 100) + "%]\n\n");
        
        sb.append("[InvItemsWorn]\n");
        for (int i = 0; i < dataInv.items_worn.size(); ++i){
        	sb.append(dataInv.items_worn.get(i).name + " ");
        	sb.append(OMUD_MMUD.EQUIP_SLOT_STRINGS[dataInv.items_worn.get(i).equip_slot.ordinal()].toUpperCase() + "\n");
        }
        
        sb.append("\n[InvItemsExtra]\n");
        for (int i = 0; i < dataInv.items_extra.size(); ++i)
        	sb.append(dataInv.items_extra.get(i).name + "\n");
        
        sb.append("\n[InvKeys]\n");
        for (int i = 0; i < dataInv.keys.size(); ++i)
        	sb.append(dataInv.keys.get(i).name + "\n");
        
        _txtInv.setText(sb.toString());
        _txtInv.setCaretPosition(0);

        // set coins labels...
        _lblInvRunic.setText("Runic: " 		+ 	dataInv.coins_runic);
        _lblInvPlat.setText("Plat: " 		+ 	dataInv.coins_plat);
        _lblInvGold.setText("Gold: " 		+ 	dataInv.coins_gold);
        _lblInvSilver.setText("Silver: " 	+ 	dataInv.coins_silver);
        _lblInvCopper.setText("Copper: " 	+ 	dataInv.coins_copper);
        _lblInvWealth.setText("Wealth: " 	+ 	dataInv.wealth);
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
        sb.append("[Str]: "         + dataStats.str             + (dataStats.str_mod 	? " *" : "") + "\n");
        sb.append("[Int]: "         + dataStats.intel           + (dataStats.intel_mod 	? " *" : "") + "\n");
        sb.append("[Wil]: "         + dataStats.wil             + (dataStats.wil_mod 	? " *" : "") + "\n");
        sb.append("[Agi]: "         + dataStats.agi             + (dataStats.agi_mod 	? " *" : "") + "\n");
        sb.append("[Hea]: "         + dataStats.hea             + (dataStats.hea_mod 	? " *" : "") + "\n");
        sb.append("[Cha]: "         + dataStats.cha             + (dataStats.cha_mod 	? " *" : "") + "\n");
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

        _txtStats.setText(sb.toString());
        _txtStats.setCaretPosition(0);            
    }

    public void processMUDShop(final OMUD_MMUD.DataShop dataShop, final String strRoomID, final String strRoomName){
        _tabsInfo.setSelectedIndex(7);

        StringBuilder sb = new StringBuilder();
        sb.append("[RoomID]: "    + strRoomID   + "\n");
        sb.append("[RoomName]: "  + strRoomName + "\n\n");
        sb.append("--------------------\n");
        sb.append("[Qty] Name: Price\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataShop.shop_items.size(); ++i){
            
            String strFill = "";
            int fill_len = 30 - dataShop.shop_items.get(i).name.length();
            if (fill_len > 0)
                strFill = OMUD.getFillString(" ", fill_len);
            
            sb.append("[" + dataShop.shop_items.get(i).qty + "]:\t" + dataShop.shop_items.get(i).name + strFill + dataShop.shop_items.get(i).price + "\n");
        }

        _txtShop.setText(sb.toString());
        _txtShop.setCaretPosition(0);
    }
}