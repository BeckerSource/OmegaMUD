import java.util.ArrayList;

interface OMUD_IMUDEvents{
    public void requestMUDData(OMUD_MMUD_DataBlock.eBlockType block_type);
    public void notifyMUDInit(final String strWelcome, final String strSpellsCmd);
    public void notifyMUDUnknown(final String strText);
    public void notifyMUDDebug(final String strDebugText);
    public void notifyMUDUserCmd(final String strText);
    public void notifyMUDLocation(final OMUD.eBBSLocation eLoc);
    public void notifyMUDStatline(final OMUD_MMUD_DataBlockStatline dataStatline);
    public void notifyMUDExp(final OMUD_MMUD_DataBlockExp dataExp);
    public void notifyMUDRoom(final OMUD_MMUD_DataBlockRoom dataRoom);
    public void notifyMUDInv(final OMUD_MMUD_DataBlockInv dataInv);
    public void notifyMUDStats(final OMUD_MMUD_DataBlockStats dataStats, final OMUD_MMUD_DataBlockStatline dataStatline);
    public void notifyMUDShop(final OMUD_MMUD_DataBlockShop dataShop);
    public void notifyMUDSpells(final OMUD_MMUD_DataBlockSpells dataSpells);
    public void notifyMUDWho(final OMUD_MMUD_DataBlockWho dataWho);
    public void notifyMUDCombat(final OMUD_MMUD_DataBlockCombat dataCombat);
    public void notifyMUDParty();
}

public class OMUD_MMUD_Parser {

    // ------------------
    // OMUD_MMUD_Parser
    // ------------------
    private OMUD.eBBSLocation   _eBBSLoc =      OMUD.eBBSLocation.BBS;
    private OMUD_IMUDEvents     _omme =         null;
    private OMUD_MMUD_Char      _mmc =          null;
    private StringBuilder       _sbDataTelnet = null;
    private static OMUD_MMUD_ParseBlocks _s_blocks = new OMUD_MMUD_ParseBlocks();

    public OMUD_MMUD_Parser(OMUD_IMUDEvents omme){
        _omme = omme;
        _sbDataTelnet = new StringBuilder();
        resetData(OMUD.eBBSLocation.BBS);
    }

    private void resetData(OMUD.eBBSLocation eBBSLoc){
        _eBBSLoc = eBBSLoc;
        _mmc = new OMUD_MMUD_Char();
    }

    public void appendChar(char c)          {_sbDataTelnet.append(c);}
    public void appendSB(StringBuilder sb)  {_sbDataTelnet.append(sb);}
    public void deleteLastChar(){
        if (_sbDataTelnet.length() > 0)
            _sbDataTelnet.deleteCharAt(_sbDataTelnet.length() - 1);
    }

    // clearTelnetData(): only clear at specific locations
    public void clearTelnetData(){
        if (_eBBSLoc == OMUD.eBBSLocation.MUD_EDITOR || _eBBSLoc == OMUD.eBBSLocation.BBS)
            _sbDataTelnet.setLength(0);
    }

    // updateParseDeleteLen(): loop helper func -
    // keeps track of the total delete length from zero for the telnet data buffer.
    private int updateParseDeleteLen(int pos_data_found_start, int pos_buf_delete_len){
        if (pos_data_found_start < pos_buf_delete_len || pos_buf_delete_len == 0)
            pos_buf_delete_len = pos_data_found_start;
        return pos_buf_delete_len;
    }

    private void findMenuCmd(StringBuilder sbCmd){
        if (sbCmd != null && sbCmd.length() > 0){

            int pos_cmd_start = -1;
            if (_sbDataTelnet.length() > 0)
                pos_cmd_start = _sbDataTelnet.indexOf(sbCmd.toString(), 0);

            boolean valid_cmd = false;
            OMUD.eBBSLocation eNewLoc = _eBBSLoc;
            char cmd_first_lower = Character.toLowerCase(sbCmd.charAt(0));
                 if ((valid_cmd = cmd_first_lower == 'e'))
                eNewLoc = pos_cmd_start > -1 ? OMUD.eBBSLocation.MUD_EDITOR : _eBBSLoc;
            else if ((valid_cmd = cmd_first_lower == 'x'))
                eNewLoc = pos_cmd_start > -1 ? OMUD.eBBSLocation.BBS : _eBBSLoc;

            // valid command and was found in telnet data...
            if (eNewLoc != _eBBSLoc){
                _sbDataTelnet.delete(0, pos_cmd_start + sbCmd.length());
                sbCmd.setLength(0);

                _omme.notifyMUDLocation((_eBBSLoc = eNewLoc));
                _mmc.ablk.statline_wait = false;
            // else have a valid command and still waiting for it in the telnet data...
            } else if (valid_cmd){
                _mmc.ablk.statline_wait = true;
            }
        }
    }

    public boolean threadParseData(StringBuilder sbCmd){
        int pos_data_found_start =  -1;
        int pos_buf_delete_len =     0;

        // ------------------
        // BBS MUD Menu
        // ------------------
        // find a better way to isolate this check based on a command in the future?...
        if (_sbDataTelnet.length() > 0 && (pos_data_found_start = _s_blocks.parseBBSMenu(_mmc, _sbDataTelnet)) > -1){
            _sbDataTelnet.delete(0, pos_data_found_start);
            // reset data and location...
            resetData(OMUD.eBBSLocation.MUD_MENU);
            _omme.notifyMUDLocation(_eBBSLoc);
        }

        // ------------------
        // MUD Menu: Enter/Exit
        // ------------------
        // user sent a command at the BBS MUD menu...
        if (_eBBSLoc == OMUD.eBBSLocation.MUD_MENU)
            findMenuCmd(sbCmd);

        // ------------------
        // Inside MUD
        // ------------------
        if (OMUD.isInsideMUD(_eBBSLoc)){

            // ------------------
            // Find User Commands
            // ------------------
            // only process user commands and line blocks when not inside an editor (training stats, input prompt, etc.) -
            // sbCmd can be null if there are no current commands in the telnet array...
            if (_eBBSLoc != OMUD.eBBSLocation.MUD_EDITOR && sbCmd != null && sbCmd.length() > 0 && _sbDataTelnet.length() > 0){
                // always wait for statline if we have a command -
                // this may hit multiple times because
                // we don't know when the linefeed will be sent from telnet...
                _mmc.ablk.statline_wait = true;

                if (OMUD.getNextLF(_sbDataTelnet, 0) == sbCmd.length() - 1){
                    _sbDataTelnet.delete(0, sbCmd.length());

                    sbCmd.deleteCharAt(sbCmd.length() - 1); // delete the trailing LF
                    if (_s_blocks.findCmd(sbCmd.toString().toLowerCase(), _mmc.ablk))
                        _omme.notifyMUDLocation((_eBBSLoc = OMUD.eBBSLocation.MUD_EDITOR));

                    // if we just had a single linefeed/enter (length would be zero here),
                    // translate that to something visible...
                    if (sbCmd.length() == 0)
                        sbCmd.append("<ENTER>");
                    sbCmd.append(" (" + _mmc.ablk.strCmdText + ")");
                        _omme.notifyMUDUserCmd(sbCmd.toString());
                    sbCmd.setLength(0); // clear to show as processed
                }
            }

            // ------------------
            // Find Statline
            // ------------------
            if (_sbDataTelnet.length() > 0 && (pos_data_found_start = _s_blocks.parseStatline(_mmc, _sbDataTelnet)) > -1){
                pos_buf_delete_len = updateParseDeleteLen(pos_data_found_start, pos_buf_delete_len);

                // ------------------
                // Return from Editor
                // ------------------
                // check if returning from training stats or at a prompt/input...
                if (_eBBSLoc == OMUD.eBBSLocation.MUD_EDITOR){
                    _omme.notifyMUDLocation((_eBBSLoc = OMUD.eBBSLocation.MUD));

                    // check for welcome msg...
                    if (!_mmc.got_statline){
                         _mmc.got_statline = true;

                        String strSpellsCmd = OMUD_MMUD_DataBlock.CMD_STRINGS[OMUD_MMUD_DataBlock.eBlockType.SPELLS.ordinal()];
                        if ( _mmc.dataStatline.ma_str.length() > 0 &&
                            !_mmc.dataStatline.ma_str.equals(OMUD_MMUD_DataBlockStatline.MSTR_SLINE_MA))
                            strSpellsCmd = OMUD_MMUD_DataBlock.MSTR_CMD_SPELLS_KAI;
                        _omme.notifyMUDInit(new String(_mmc.strWelcome), strSpellsCmd);
                    }

                    // some basic auto commands on enter - can make manual/auto modes for this later...
                    if (_mmc.dataRoom.name.length() == 0)
                        _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.ROOM);
                    if (_mmc.dataStats.name_first.length() == 0)
                        _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.STATS);
                    _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.INV);
                    _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.EXP);
                    if (_mmc.dataStatline.ma_str.length() > 0)
                        _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.SPELLS);
                    _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.WHO);
                }

                // ------------------
                // Find Non-Cmd LF Blocks
                // ------------------
                // parse other non-cmd blocks that are based on linefeeds -
                // reset the iterator to find more until none are found...
                for (int i = 0; i < _sbDataTelnet.length(); ++i)
                    if (_sbDataTelnet.charAt(i) == OMUD.ASCII_LF &&
                        (pos_data_found_start = _s_blocks.parseLineBlocks(_mmc, _sbDataTelnet, i)) > -1){
                        pos_buf_delete_len = updateParseDeleteLen(pos_data_found_start, pos_buf_delete_len);
                        i = pos_data_found_start;
                    }

                // notify for statline update and other data that was updated...
                OMUD_MMUD_DataBlockStatline dStatline = new OMUD_MMUD_DataBlockStatline(_mmc.dataStatline);
                _omme.notifyMUDStatline(dStatline);
                     if (_mmc.ablk.data_type == OMUD_MMUD_DataBlock.eBlockType.ROOM)
                    _omme.notifyMUDRoom(new OMUD_MMUD_DataBlockRoom(_mmc.dataRoom));
                else if (_mmc.ablk.data_type == OMUD_MMUD_DataBlock.eBlockType.COMBAT){
                    _omme.notifyMUDCombat(new OMUD_MMUD_DataBlockCombat(_mmc.dataCombat));
                    if (_mmc.dataCombat.exp_gained > 0){
                        _mmc.dataCombat.exp_gained = 0;
                        _omme.notifyMUDExp(new OMUD_MMUD_DataBlockExp(_mmc.dataExp));
                    }
                }
                else if (_mmc.ablk.data_type == OMUD_MMUD_DataBlock.eBlockType.INV)
                    _omme.notifyMUDInv(new OMUD_MMUD_DataBlockInv(_mmc.dataInv));
                else if (_mmc.ablk.data_type == OMUD_MMUD_DataBlock.eBlockType.EXP)
                    _omme.notifyMUDExp(new OMUD_MMUD_DataBlockExp(_mmc.dataExp));
                else if (_mmc.ablk.data_type == OMUD_MMUD_DataBlock.eBlockType.STATS)
                    _omme.notifyMUDStats(new OMUD_MMUD_DataBlockStats(_mmc.dataStats), dStatline);
                else if (_mmc.ablk.data_type == OMUD_MMUD_DataBlock.eBlockType.SHOP)
                    _omme.notifyMUDShop(new OMUD_MMUD_DataBlockShop(_mmc.dataShop));
                else if (_mmc.ablk.data_type == OMUD_MMUD_DataBlock.eBlockType.SPELLS)
                    _omme.notifyMUDSpells(new OMUD_MMUD_DataBlockSpells(_mmc.dataSpells));
                else if (_mmc.ablk.data_type == OMUD_MMUD_DataBlock.eBlockType.WHO)
                    _omme.notifyMUDWho(new OMUD_MMUD_DataBlockWho(_mmc.dataWho));
                if (_mmc.ablk.sbDebug.length() > 0)
                    _omme.notifyMUDDebug(_mmc.ablk.sbDebug.toString());

                // reset active block with statline forced as last data type...
                _mmc.ablk = new OMUD_MMUD_Char.ActiveDataBlock(false, OMUD_MMUD_DataBlock.eBlockType.STATLINE);
            }

            // ------------------
            // Unknown/Extra Data
            // ------------------
            // Keep mud buffer clean by removing extra data that we didn't match.
            // (also helps to find new/unprocessed mud strings)
            if (pos_buf_delete_len > 0){
                _omme.notifyMUDUnknown("[UNKNOWN DATA #1] [LEN: " + pos_buf_delete_len + "]\n--------\n" + _sbDataTelnet.substring(0, pos_buf_delete_len) + "\n--------\n");
                _sbDataTelnet.delete(0, pos_buf_delete_len);
                if (_sbDataTelnet.length() > 0){
                    _omme.notifyMUDUnknown("[UNKNOWN DATA #2] [LEN: "     + _sbDataTelnet.length() + "]\n--------\n" + _sbDataTelnet.substring(0, _sbDataTelnet.length()) + "\n--------\n");
                    OMUD.logError("Error parsing MUD: extra buffer: length: " + _sbDataTelnet.length() + ":\n--------\n" + _sbDataTelnet.substring(0, _sbDataTelnet.length()) + "\n--------\n");
                    _sbDataTelnet.setLength(0);
                }
            }
        }

        return _eBBSLoc == OMUD.eBBSLocation.BBS ? true : !_mmc.ablk.statline_wait;
    }
}
