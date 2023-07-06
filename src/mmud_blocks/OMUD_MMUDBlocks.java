import java.util.ArrayList;

public class OMUD_MMUDBlocks{
    // ------------------
    // Block
    // ------------------
    public static abstract class Block{
        protected class CmdText{
            public String   text  =     "";
            public int      min_len =   0;
            public CmdText(String t, int ml){text = t; min_len = ml;}
        }

        protected ArrayList<CmdText> _arrlCmdText = new ArrayList<CmdText>();
        protected StringBuilder      _sbBlockData = new StringBuilder();

        public abstract boolean getStatlineWait();
        
        public String matchCmdText(String strCmd){
            String strFoundCmdFull = null;

            int i = 0;
            boolean found = false;
            for (; i < _arrlCmdText.size() && !found; ++i){
                // initial check to meet min length requirement of the command...
                found = strCmd.length() >= _arrlCmdText.get(i).min_len && strCmd.length() <= _arrlCmdText.get(i).text.length();
                // match characters if valid (assumes all lower case)...
                for (int j = 0; j < strCmd.length() && found; ++j)
                    found = strCmd.charAt(j) == _arrlCmdText.get(i).text.charAt(j);
            }

            if (found)
                strFoundCmdFull = _arrlCmdText.get(--i).text;
            return strFoundCmdFull;
        }

        public abstract int findBlockData(OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset);
        protected int findData(StringBuilder sbTelnetData, int pos_offset, boolean offset_is_lf, boolean has_dynamic_text, String strStartSeq, String strEndSeq){
            int pos_endseq_left =    -1;
            int pos_endseq_right =   -1;
            int pos_startseq_left =  -1;
            int pos_startseq_right = -1;
            boolean valid_range =    sbTelnetData.length() >= strStartSeq.length() + strEndSeq.length();

            // don't bother unless we have enough overall space...
            if (valid_range){

                // get end sequence range (also checks/calcs against LF)...
                if ((pos_endseq_left = pos_offset - (offset_is_lf ? 1 : 0)) >= 0){
                    if (strEndSeq.length() > 0){
                        pos_endseq_left = sbTelnetData.lastIndexOf(strEndSeq, pos_endseq_left);
                        pos_endseq_right = pos_endseq_left + strEndSeq.length();
                        valid_range = pos_endseq_right - pos_endseq_left == strEndSeq.length();
                        pos_endseq_right--; // -1 for zero-based pos/index
                    } else {
                        pos_endseq_right = pos_endseq_left;
                        valid_range = true;
                    }
                } else valid_range = false;

                // check range and get start sequence left/starte...
                if (valid_range &&
                    (pos_startseq_left = sbTelnetData.lastIndexOf(strStartSeq, pos_endseq_left)) > -1){

                    // check ranges based on if has dynamic text...
                    pos_startseq_right = pos_startseq_left + strStartSeq.length() - 1; // -1 for zero-based pos/index
                    if (has_dynamic_text){
                        pos_startseq_right++; // move to the dynamic text starting position
                        valid_range = pos_startseq_right <  pos_endseq_left;
                    } else {
                        valid_range = pos_startseq_right == pos_endseq_left;
                    }

                    if (valid_range){
                        _sbBlockData.setLength(0);
                        if (pos_startseq_right < pos_endseq_left){
                            // adjust the endseq left if for a LF with no end seq text...
                            if (offset_is_lf && strEndSeq.length() == 0)
                                pos_endseq_left++;
                            _sbBlockData.append(sbTelnetData.substring(pos_startseq_right, pos_endseq_left));
                        }
                        // delete the data in the buffer...
                        sbTelnetData.delete(pos_startseq_left, pos_endseq_right + (offset_is_lf ? 1 : 0) + 1); // +1 for exclusive
                    } else pos_startseq_left = -1;
                }
            }
            return pos_startseq_left;
        }

        // cleanData(): trim lf/space and strip ansi as requested...
        protected void cleanData(StringBuilder sbData, boolean trim_lf_spc, boolean strip_ansi){
            int pos_first_char = -1;
            int pos_last_char  = -1;
            for (int i = 0; i < sbData.length(); ++i){
                if (strip_ansi && sbData.charAt(i) == OMUD.ASCII_ESC){
                    int pos_ansi_end = sbData.indexOf(OMUD.CSI_GRAPHICS_STR, i);
                    // if matching ansi end is not found, just delete the escape char.
                    if (pos_ansi_end == -1)
                        pos_ansi_end = i + 1;   // +1 for exclusive end
                    else pos_ansi_end++;        //
                    sbData.delete(i--, pos_ansi_end); // move 'i' back after delete so that we pick up the first char after the delete

                } else if (trim_lf_spc){
                    if (sbData.charAt(i) == OMUD.ASCII_LF){
                        sbData.setCharAt(i, OMUD.ASCII_SPC);
                    } else if (sbData.charAt(i) != OMUD.ASCII_SPC){
                        pos_last_char = i;
                        if (pos_first_char == -1)
                            pos_first_char = i;
                    }
                }
            }

            // trim: delete end first...
            if (trim_lf_spc){
                if (pos_last_char + 1 <= sbData.length() - 1)
                    sbData.delete(pos_last_char + 1, sbData.length()); // +1 to move forward to begin at space after last char
                if (pos_first_char > 0)
                    sbData.delete(0, pos_first_char);
            }
        }

        // checkPrefix(): some mud strings have a possible prefix - check for it and remove it -
        // NOTE: strDbgReason is just for internal visual/coding reference.  Not a string required for function.
        protected int checkPrefix(String strDbgReason, StringBuilder sbDbgReason, StringBuilder sbTelnetData, int pos_offset, String strPrefix){
            int prefix_len = strPrefix.length();
            int pos_start = pos_offset - prefix_len;
            if (pos_start >= 0 && sbTelnetData.substring(pos_start, pos_start + prefix_len).equals(strPrefix)){
                sbTelnetData.delete(pos_start, pos_start + prefix_len);
                pos_offset = pos_start;
                sbDbgReason.append("Block Prefix Found/Removed: " + strDbgReason + "\n");
            }
            return pos_offset;
        }

        protected void splitCommaListToArray(String strItems, ArrayList<String> arrlItems){
            String[] items = strItems.split(",");
            for (String item : items)
                arrlItems.add(item.trim());
        }

        protected boolean findCoins(OMUD_MMUD.DataCoins coins, int qty, String strText){
            boolean coin_match = false;
                 if ((coin_match = (strText.indexOf(OMUD_MMUD.COIN_ITEM_STRINGS[OMUD_MMUD.eCoinType.RUNIC.ordinal()], 0))      > -1))
                coins.runic =   qty;
            else if ((coin_match = (strText.indexOf(OMUD_MMUD.COIN_ITEM_STRINGS[OMUD_MMUD.eCoinType.PLATINUM.ordinal()], 0))   > -1))
                coins.plat =    qty;
            else if ((coin_match = (strText.indexOf(OMUD_MMUD.COIN_ITEM_STRINGS[OMUD_MMUD.eCoinType.GOLD.ordinal()], 0))       > -1))
                coins.gold =    qty;
            else if ((coin_match = (strText.indexOf(OMUD_MMUD.COIN_ITEM_STRINGS[OMUD_MMUD.eCoinType.SILVER.ordinal()], 0))     > -1))
                coins.silver =  qty;
            else if ((coin_match = (strText.indexOf(OMUD_MMUD.COIN_ITEM_STRINGS[OMUD_MMUD.eCoinType.COPPER.ordinal()], 0))     > -1))
                coins.copper =  qty;
            return coin_match;
        }

        protected void buildItemList(ArrayList<String> arrlNew, OMUD_MMUD.DataCoins coins, ArrayList<OMUD_MMUD.DataItem> arrlItems, ArrayList<OMUD_MMUD.DataItem> arrlItemsWorn){
            for (int i = 0; i < arrlNew.size(); ++i){
                OMUD_MMUD.DataItem ri = new OMUD_MMUD.DataItem(arrlNew.get(i));

                boolean is_coin = false;
                boolean is_dupe = false;

                // if inventory call (worn non-null), set worn items...
                if (arrlItemsWorn != null && ri.name.charAt(ri.name.length() - 1) == ')'){
                    int pos_slot_start = -1;
                    for (int j = OMUD_MMUD.eEquipSlot.WEAPON.ordinal(); j < OMUD_MMUD.EQUIP_SLOT_STRINGS.length && pos_slot_start == -1; ++j)
                        if ((pos_slot_start = ri.name.lastIndexOf(OMUD_MMUD.EQUIP_SLOT_STRINGS[j], ri.name.length() - 1)) > -1){
                            ri.equip_slot = OMUD_MMUD.eEquipSlot.values()[j];
                            ri.name = ri.name.substring(0, pos_slot_start - 1); // remove slot and trailing space (-1)
                            arrlItemsWorn.add(ri);
                        }
                }

                // non-equipped: check for quantity prefixes and dupes...
                if (ri.equip_slot == OMUD_MMUD.eEquipSlot.NONE){

                    // check for quantity prefix...
                    int pos_left = -1;
                    if ((pos_left = ri.name.indexOf(" ", 0)) > -1){
                        boolean is_number = true;

                        String strFirstWord = ri.name.substring(0, pos_left);
                        for (int j = 0; j < strFirstWord.length() && is_number; ++j)
                            is_number = strFirstWord.charAt(j) >= 48 && strFirstWord.charAt(j) <= 57; // 0-9 ascii

                        if (is_number){
                            ri.qty =  Integer.parseInt(strFirstWord);
                            ri.name = ri.name.substring(pos_left + 1, ri.name.length());

                            // coin item names always have two words, so check for another space first for efficiency and 
                            // then coin names - coins are always displayed first and in order,
                            // so can exit early if out-of-range or if copper is found...
                            if (ri.name.indexOf(" ", 0) > -1)
                                for (int j = 0; j < OMUD_MMUD.COIN_ITEM_STRINGS.length && coins.copper == 0; ++j)
                                    is_coin = findCoins(coins, ri.qty, ri.name);
                        }
                    }

                    // check for existing and update quantities...
                    if (!is_coin){
                        for (int j = 0; j < arrlItems.size() && !is_dupe; ++j)
                            if ((is_dupe = ri.name.equals(arrlItems.get(j).name))){
                                // inventory: worn item with extra non-worn in inv -
                                // keep the counter on the original equipped item
                                if (arrlItems.get(j).equip_slot != OMUD_MMUD.eEquipSlot.NONE)
                                    arrlItems.get(j).qty = ri.qty + 1;
                                // else this is from a search and the greater quantity should be updated
                                // based on the search results...
                                else if (ri.qty > arrlItems.get(j).qty)
                                    arrlItems.get(j).qty = ri.qty;
                            }
                    }
                }

                if (!is_coin && !is_dupe)
                    arrlItems.add(ri);
            }
        }
    }

    // ------------------
    // OMUD_MMUDBlocks
    // ------------------
    private OMUD_MMUDBlock_BBSMenu      _blkBBSMenu = null;
    private ArrayList<Block>            _arrlBlocks = null;
    private int _bpos_cmd_editor =      0;
    private final int BPOS_STATLINE =   0;
    private final int BPOS_CMDS_START = 1;

    public OMUD_MMUDBlocks(){
        _arrlBlocks = new ArrayList<Block>();
        // ==================
        // ------------------
        // Statline
        // ------------------
        _arrlBlocks.add(new OMUD_MMUDBlock_Statline());     // BPOS_STATLINE
        // ------------------
        // Command Blocks
        // ------------------
        _arrlBlocks.add(new OMUD_MMUDBlock_Room());
        _arrlBlocks.add(new OMUD_MMUDBlock_Exp());
        _arrlBlocks.add(new OMUD_MMUDBlock_Inventory());
        _arrlBlocks.add(new OMUD_MMUDBlock_Stats());
        _arrlBlocks.add(new OMUD_MMUDBlock_Shop());
        _arrlBlocks.add(new OMUD_MMUDBlock_Spells());
        _arrlBlocks.add(new OMUD_MMUDBlock_Who());
        _arrlBlocks.add(new OMUD_MMUDBlock_Editor());       // NOTE: editor should always be at the end!
        _bpos_cmd_editor = _arrlBlocks.size() - 1;          //
        // ------------------
        // Other Line Blocks
        // ------------------
        _arrlBlocks.add(new OMUD_MMUDBlock_Other());
        // ==================

        // ------------------
        // MUD Menu (separate)
        // ------------------
        // somewhat unique so keep this separate...
        _blkBBSMenu = new OMUD_MMUDBlock_BBSMenu();
    }

    public int parseStatline(OMUD_MMUDChar mmc, StringBuilder sbTelnetData){
        int pos_data_found_start = _arrlBlocks.get(BPOS_STATLINE).findBlockData(mmc, sbTelnetData, 0);
        if (pos_data_found_start > -1 && sbTelnetData.length() > 0){

            int pos_cmd_data_found_start = -1;

            // check all blocks if active is unset/statline...
            if (mmc.ablk.block_pos < BPOS_CMDS_START){
                for (int i = BPOS_CMDS_START; i <= _bpos_cmd_editor && pos_cmd_data_found_start == -1; ++i)
                    if ((pos_cmd_data_found_start = _arrlBlocks.get(i).findBlockData(mmc, sbTelnetData, pos_data_found_start)) > -1)
                        mmc.ablk.update(i, "", _arrlBlocks.get(i).getStatlineWait());
            // else parse the active block...
            } else {
                pos_cmd_data_found_start = _arrlBlocks.get(mmc.ablk.block_pos).findBlockData(mmc, sbTelnetData, pos_data_found_start);
            }

            if (pos_cmd_data_found_start > -1)
                pos_data_found_start = pos_cmd_data_found_start;
        }

        return pos_data_found_start;
    }

    public int parseLineBlocks(OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;
        for (int i = _bpos_cmd_editor + 1; i < _arrlBlocks.size() && pos_data_found_start == -1; ++i)
            pos_data_found_start = _arrlBlocks.get(i).findBlockData(mmc, sbTelnetData, pos_offset);
        return pos_data_found_start;
    }

    public int parseBBSMenu(OMUD_MMUDChar mmc, StringBuilder sbTelnetData){
        return _blkBBSMenu.findBlockData(mmc, sbTelnetData, 0);
    }

    // findCmd(): main external call to match a user-input command (assumes passed in as lower-case)
    // returns true if at an in-game menu/editor (train stats, etc.)
    public boolean findCmd(String strCmd, OMUD_MMUDChar.ActiveBlock ablk){
        String strFoundCmdFull = null;
        for (int i = BPOS_CMDS_START; i <= _bpos_cmd_editor && strFoundCmdFull == null; ++i)
            if ((strFoundCmdFull = _arrlBlocks.get(i).matchCmdText(strCmd)) != null)
                ablk.update(i, strFoundCmdFull, _arrlBlocks.get(i).getStatlineWait());
        return ablk.block_pos == _bpos_cmd_editor;
    }
}
