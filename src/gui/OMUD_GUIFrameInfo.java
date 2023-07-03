import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OMUD_GUIFrameInfo extends JFrame {
    private enum eTab{
        TERM_DEBUG,
        MUD_OTHER,
        MUD_CMDS,
        MUD_WELCOME,
        MUD_ROOM,
        MUD_INV,
        MUD_STATS,
        MUD_SHOP,
        MUD_SPELLS,
        MUD_WHO
    }

    private SimpleDateFormat    _sdf =          null; 
    private OMUD_IMUDEvents     _omme =         null;
    private OMUD_GUITextField   _lblInvRunic =  null;
    private OMUD_GUITextField   _lblInvPlat =   null;
    private OMUD_GUITextField   _lblInvGold =   null;
    private OMUD_GUITextField   _lblInvSilver = null;
    private OMUD_GUITextField   _lblInvCopper = null;
    private OMUD_GUITextField   _lblInvWealth = null;
    private OMUD_GUITextArea    _txtTermDbg =   null;
    private OMUD_GUITextArea    _txtCmds =      null;
    private OMUD_GUITextArea    _txtOther =     null;
    private OMUD_GUITextArea    _txtWelcome =   null;
    private OMUD_GUITextArea    _txtRoom =      null;
    private OMUD_GUITextArea    _txtInv =       null;
    private OMUD_GUITextArea    _txtStats =     null;
    private OMUD_GUITextArea    _txtShop =      null;
    private OMUD_GUITextArea    _txtSpells =    null;
    private OMUD_GUITextArea    _txtWho =       null;
    private JPanel              _pnlInv =       null;
    private JPanel              _pnlInvMoney =  null;
    private JTabbedPane         _tabs =         null;
    private eTab                _tab_prev =     eTab.TERM_DEBUG;
    private static final int FRAME_MIN_WIDTH  = 710;
    private static final int FRAME_MIN_HEIGHT = 550;

    OMUD_GUIFrameInfo(OMUD_IMUDEvents omme){
        setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));
        setTitle("Char Info");

        _omme = omme;
        _sdf = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");

        // inventory stuff...
        _lblInvRunic =  new OMUD_GUITextField("Runic: ?",  false, true);
        _lblInvPlat =   new OMUD_GUITextField("Plat: ?",   false, true);
        _lblInvGold =   new OMUD_GUITextField("Gold: ?",   false, true);
        _lblInvSilver = new OMUD_GUITextField("Silver: ?", false, true);
        _lblInvCopper = new OMUD_GUITextField("Copper: ?", false, true);
        _lblInvWealth = new OMUD_GUITextField("Wealth: ?", false, true);

        // tabs...
        _pnlInv =       new JPanel();
        _pnlInvMoney =  new JPanel();
        _txtTermDbg =   new OMUD_GUITextArea(false);
        _txtCmds =      new OMUD_GUITextArea(false);
        _txtOther =     new OMUD_GUITextArea(false);
        _txtWelcome =   new OMUD_GUITextArea(false);
        _txtRoom =      new OMUD_GUITextArea(true);
        _txtInv =       new OMUD_GUITextArea(true);
        _txtStats =     new OMUD_GUITextArea(false);
        _txtShop =      new OMUD_GUITextArea(false);
        _txtSpells =    new OMUD_GUITextArea(false);
        _txtWho =       new OMUD_GUITextArea(false);
        _tabs =         new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        _tabs.add("TermDbg",    _txtTermDbg);
        _tabs.add("MOther",     new JScrollPane(_txtOther));
        _tabs.add("MCmds",      new JScrollPane(_txtCmds));
        _tabs.add("MWelcome",   new JScrollPane(_txtWelcome));
        _tabs.add("MRoom",      new JScrollPane(_txtRoom));
        _tabs.add("MInv",       new JScrollPane(_pnlInv));
        _tabs.add("MStats",     new JScrollPane(_txtStats));
        _tabs.add("MShop",      new JScrollPane(_txtShop));
        _tabs.add("MSpells",    new JScrollPane(_txtSpells));
        _tabs.add("MWho",       new JScrollPane(_txtWho));
        _tabs.addMouseListener(new ML_Tabs());
        add(_tabs);

        // layouts...
        GridBagLayout gbl = new GridBagLayout();
        layoutInv(gbl);
        pack();
    }

    private void layoutInv(GridBagLayout gbl){
        GridBagConstraints gblc = new GridBagConstraints();
        _lblInvRunic.setPreferredSize(  new Dimension(100, 25));
        _lblInvPlat.setPreferredSize(   new Dimension(100, 25));
        _lblInvGold.setPreferredSize(   new Dimension(100, 25));
        _lblInvSilver.setPreferredSize( new Dimension(100, 25));
        _lblInvCopper.setPreferredSize( new Dimension(100, 25));
        _lblInvWealth.setPreferredSize( new Dimension(100, 25));

        // money labels...
        gblc.weightx =      1.0;
        gblc.weighty =      0.0;
        gblc.gridwidth =    5;
        gblc.gridheight =   GridBagConstraints.REMAINDER;
        gblc.fill =         GridBagConstraints.HORIZONTAL;
        gbl.setConstraints(_pnlInvMoney,    gblc);
        gbl.setConstraints(_lblInvRunic,    gblc);
        gbl.setConstraints(_lblInvPlat,     gblc);
        gbl.setConstraints(_lblInvGold,     gblc);
        gbl.setConstraints(_lblInvSilver,   gblc);
        gbl.setConstraints(_lblInvCopper,   gblc);
        gbl.setConstraints(_lblInvWealth,   gblc);
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
    // GUI Events
    // --------------
    private class ML_Tabs implements MouseListener {
        public void mouseClicked(MouseEvent event)  {}
        public void mouseEntered(MouseEvent event)  {}
        public void mouseExited(MouseEvent event)   {}
        public void mouseReleased(MouseEvent event) {}
        public void mousePressed(MouseEvent event) {
            if (_tabs.getSelectedIndex() != _tab_prev.ordinal()){
                _tab_prev = eTab.values()[_tabs.getSelectedIndex()];

                     if (_tab_prev == eTab.MUD_ROOM)
                    _omme.requestMUDData(OMUD_MMUD.DataBlock.eBlockType.ROOM);
                else if (_tab_prev == eTab.MUD_INV)
                    _omme.requestMUDData(OMUD_MMUD.DataBlock.eBlockType.INV);
                else if (_tab_prev == eTab.MUD_STATS)
                    _omme.requestMUDData(OMUD_MMUD.DataBlock.eBlockType.STATS);
                else if (_tab_prev == eTab.MUD_SHOP)
                    _omme.requestMUDData(OMUD_MMUD.DataBlock.eBlockType.SHOP);
                else if (_tab_prev == eTab.MUD_SPELLS)
                    _omme.requestMUDData(OMUD_MMUD.DataBlock.eBlockType.SPELLS);
                else if (_tab_prev == eTab.MUD_WHO)
                    _omme.requestMUDData(OMUD_MMUD.DataBlock.eBlockType.WHO);
            }
        }
    }

    private void setTab(eTab tab){
        _tabs.setSelectedIndex((_tab_prev = tab).ordinal());
    }

    // --------------
    // Telnet Events
    // --------------
    public void processTelnetParsed(final OMUD_Buffer omb){
        if (isVisible() && _tabs.getSelectedIndex() == 0){
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
        setTab(eTab.MUD_ROOM);

        StringBuilder sb = new StringBuilder();
        sb.append("[RoomID]: "      + dataRoom.roomID);
        sb.append("\n[RoomName]: "  + dataRoom.name + " (" + OMUD_MMUD.ROOM_LIGHT_STRINGS[dataRoom.light.ordinal()] + ")");

        sb.append("\n\n--------------------\n");
        sb.append("[RoomItems] (*=HIDDEN)\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataRoom.arrlItems.size(); ++i)
            sb.append("  (" + dataRoom.arrlItems.get(i).qty + ") " + dataRoom.arrlItems.get(i).name + "\n");
        for (int i = 0; i < dataRoom.arrlItemsHidden.size(); ++i)
            sb.append("* (" + dataRoom.arrlItemsHidden.get(i).qty + ") " + dataRoom.arrlItemsHidden.get(i).name + "\n");

        sb.append("\n--------------------\n");
        sb.append("[RoomUnits]\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataRoom.arrlUnits.size(); ++i)
            sb.append(dataRoom.arrlUnits.get(i) + "\n");

        sb.append("\n--------------------\n");
        sb.append("[RoomExits]\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataRoom.arrlExits.size(); ++i)
            sb.append(OMUD_MMUD.EXIT_DIR_STRINGS[dataRoom.arrlExits.get(i).eDir.ordinal()] + "\n");

        sb.append("\n--------------------\n");
        sb.append("[RoomDesc]\n");
        sb.append("--------------------\n");
        sb.append(dataRoom.desc);

        _txtRoom.setText(sb.toString());
        _txtRoom.setCaretPosition(0);
    }

    public void processMUDInv(final OMUD_MMUD.DataInv dataInv){
        setTab(eTab.MUD_INV);

        StringBuilder sb = new StringBuilder();
        sb.append("\n[InvEnc]: (" + dataInv.enc_level + ") " + dataInv.enc_cur + "/" + dataInv.enc_max + String.format(" [%.0f", ((float) dataInv.enc_cur / dataInv.enc_max) * 100) + "%]");
        
        sb.append("\n\n--------------------\n");
        sb.append("[InvItemsWorn]\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataInv.items_worn.size(); ++i){
            sb.append(dataInv.items_worn.get(i).name + " ");
            sb.append(OMUD_MMUD.EQUIP_SLOT_STRINGS[dataInv.items_worn.get(i).equip_slot.ordinal()].toUpperCase() + "\n");
        }
        
        sb.append("\n--------------------\n");
        sb.append("[InvItemsExtra]\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataInv.items_extra.size(); ++i)
            sb.append(dataInv.items_extra.get(i).name + "\n");
        
        sb.append("\n--------------------\n");
        sb.append("[InvKeys]\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataInv.keys.size(); ++i)
            sb.append(dataInv.keys.get(i).name + "\n");
        
        _txtInv.setText(sb.toString());
        _txtInv.setCaretPosition(0);

        // set coins labels...
        _lblInvRunic.setText("Runic: "      +   dataInv.coins_runic);
        _lblInvPlat.setText("Plat: "        +   dataInv.coins_plat);
        _lblInvGold.setText("Gold: "        +   dataInv.coins_gold);
        _lblInvSilver.setText("Silver: "    +   dataInv.coins_silver);
        _lblInvCopper.setText("Copper: "    +   dataInv.coins_copper);
        _lblInvWealth.setText("Wealth: "    +   dataInv.wealth);
    }

    public void processMUDStats(final OMUD_MMUD.DataStats dataStats){
        setTab(eTab.MUD_STATS);

        StringBuilder sb = new StringBuilder();
        sb.append("[NameFirst]: "   + dataStats.name_first);
        sb.append("\n[NameLast]: "  + dataStats.name_last);
        sb.append("\n[Race]: "      + dataStats.stats_race);
        sb.append("\n[Class]: "     + dataStats.stats_class);
        sb.append("\n[Level]: "     + dataStats.level);
        sb.append("\n[Lives]: "     + dataStats.lives);
        sb.append("\n[CP]: "        + dataStats.cp);
        sb.append("\n[AC]: "        + dataStats.ac_ac + "/" + dataStats.ac_accy);
        sb.append("\n[SC]: "        + dataStats.sc);
        sb.append("\n--------------------");
        sb.append("\n[Str]: "       + dataStats.str     + (dataStats.str_mod    ? " *" : ""));
        sb.append("\n[Int]: "       + dataStats.intel   + (dataStats.intel_mod  ? " *" : ""));
        sb.append("\n[Wil]: "       + dataStats.wil     + (dataStats.wil_mod    ? " *" : ""));
        sb.append("\n[Agi]: "       + dataStats.agi     + (dataStats.agi_mod    ? " *" : ""));
        sb.append("\n[Hea]: "       + dataStats.hea     + (dataStats.hea_mod    ? " *" : ""));
        sb.append("\n[Cha]: "       + dataStats.cha     + (dataStats.cha_mod    ? " *" : ""));
        sb.append("\n--------------------");
        sb.append("\n[Perc]: "      + dataStats.perc);
        sb.append("\n[Stealth]: "   + dataStats.stealth);
        sb.append("\n[Thiev]: "     + dataStats.thievery);
        sb.append("\n[Traps]: "     + dataStats.traps);
        sb.append("\n[Pick]: "      + dataStats.pick);
        sb.append("\n[Track]: "     + dataStats.track);
        sb.append("\n[MA]: "        + dataStats.ma);
        sb.append("\n[MR]: "        + dataStats.mr);

        _txtStats.setText(sb.toString());
        _txtStats.setCaretPosition(0);            
    }

    public void processMUDShop(final OMUD_MMUD.DataShop dataShop, final String strRoomID, final String strRoomName){
        setTab(eTab.MUD_SHOP);

        StringBuilder sb = new StringBuilder();
        sb.append("[RoomID]: "      + strRoomID);
        sb.append("\n[RoomName]: "  + strRoomName);
        sb.append("\n\n--------------------\n");
        sb.append("[Qty] Name: Price\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataShop.shop_items.size(); ++i){
            sb.append("[" + dataShop.shop_items.get(i).qty + "]:\t" + dataShop.shop_items.get(i).name);

            int fill_len = 30 - dataShop.shop_items.get(i).name.length();
            if (fill_len > 0)
               sb.append(OMUD.getFillString(" ", fill_len));

            sb.append(dataShop.shop_items.get(i).price + "\n");
        }

        _txtShop.setText(sb.toString());
        _txtShop.setCaretPosition(0);
    }

    public void processMUDSpells(final OMUD_MMUD.DataSpells dataSpells){
        setTab(eTab.MUD_SPELLS);

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------\n");
        sb.append("Short (Lvl, Cost) Full\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataSpells.spells.size(); ++i){
            sb.append(dataSpells.spells.get(i).name_short + " (" + dataSpells.spells.get(i).level + ", " + dataSpells.spells.get(i).cost + ") ");
            sb.append(dataSpells.spells.get(i).name_long + "\n");
        }

        _txtSpells.setText(sb.toString());
        _txtSpells.setCaretPosition(0);
    }

    public void processMUDWho(final OMUD_MMUD.DataWho dataWho){
        setTab(eTab.MUD_WHO);

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------\n");
        sb.append("FName LName (Align Title of Guild)\n");
        sb.append("--------------------\n");
        for (int i = 0; i < dataWho.chars.size(); ++i){
            // name...
            int fill_len = 24 - dataWho.chars.get(i).name_first.length();
            sb.append(dataWho.chars.get(i).name_first);
            if (dataWho.chars.get(i).name_last.length() > 0){
                fill_len -= (dataWho.chars.get(i).name_last.length() + 1); // +1 for inner space delim
                sb.append(" " + dataWho.chars.get(i).name_last);
            }
            if (fill_len > 0)
                sb.append(OMUD.getFillString(" ", fill_len));

            // align, title...
            String strAlign = OMUD_MMUD.ALIGNMENT_STRINGS[dataWho.chars.get(i).alignment.ordinal()];
            sb.append(" (" + strAlign + " " + dataWho.chars.get(i).title);

            // guild...
            if (dataWho.chars.get(i).guild.length() > 0){
                fill_len = (8 + 16) - (strAlign.length() + dataWho.chars.get(i).title.length());
                if (fill_len > 0)
                    sb.append(OMUD.getFillString(" ", fill_len));
                sb.append(" of " + dataWho.chars.get(i).guild);
            }
            sb.append(")\n");
        }

        _txtWho.setText(sb.toString());
        _txtWho.setCaretPosition(0);
    }
}