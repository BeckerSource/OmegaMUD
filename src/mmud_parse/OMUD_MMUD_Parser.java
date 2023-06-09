import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

interface OMUD_IMUDEvents{
    public void requestMUDData(final OMUD_MMUD_DataBlock.eBlockType block_type);
    public void notifyMUDInit(final String strWelcome);
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
    public void notifyMUDCombatTimeout();
    public void notifyMUDParty();
}

public class OMUD_MMUD_Parser {
    // --------------
    // Timer Events
    // --------------
    private class TimerTaskCombat extends TimerTask{
        public void run(){
            _clear_combat_buffer = true;
            _omme.notifyMUDCombatTimeout();
        }
    }

    // ------------------
    // OMUD_MMUD_Parser
    // ------------------
    private OMUD.eBBSLocation   _eBBSLoc =      OMUD.eBBSLocation.BBS;
    private OMUD_IMUDEvents     _omme =         null;
    private OMUD_Char.MMUD_Data _mmd =          null;
    private StringBuilder       _sbDataTelnet = null;
    private Timer               _tmrCombat =    null;
    private TimerTask           _taskCombat =   null;
    private boolean             _clear_combat_buffer = false;
    private static OMUD_MMUD_ParseBlocks _s_blocks = new OMUD_MMUD_ParseBlocks();
    private static final int COMBAT_TIMEOUT_MS = 6000; // seconds estimate for assuming next round has combat if was still in combat

    public OMUD_MMUD_Parser(OMUD_IMUDEvents omme, OMUD_Char.MMUD_Data mmd){
        _omme = omme;
        _mmd =  mmd;
        _sbDataTelnet = new StringBuilder();
        _tmrCombat =    new Timer();
        _taskCombat =   new TimerTaskCombat();
        reset(OMUD.eBBSLocation.BBS);
    }

    private void reset(OMUD.eBBSLocation eBBSLoc){
        _eBBSLoc = eBBSLoc;
        _mmd.reset();
        resetCombatTimer(false);
    }
    
    private void resetCombatTimer(boolean start_timer){
        _tmrCombat.cancel();
        _tmrCombat.purge();
        if (start_timer){
            _taskCombat = new TimerTaskCombat();
            _tmrCombat =  new Timer();
            _tmrCombat.schedule(_taskCombat, COMBAT_TIMEOUT_MS);
        }
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
                _mmd.apblock.statline_wait = false;
            // else have a valid command and still waiting for it in the telnet data...
            } else if (valid_cmd){
                _mmd.apblock.statline_wait = true;
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
        if (_sbDataTelnet.length() > 0 && (pos_data_found_start = _s_blocks.parseBBSMenu(_mmd, _sbDataTelnet)) > -1){
            _sbDataTelnet.delete(0, pos_data_found_start);
            // reset data and location...
            reset(OMUD.eBBSLocation.MUD_MENU);
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
                _mmd.apblock.statline_wait = true;

                if (OMUD.getNextLF(_sbDataTelnet, 0) == sbCmd.length() - 1){
                    _sbDataTelnet.delete(0, sbCmd.length());

                    sbCmd.deleteCharAt(sbCmd.length() - 1); // delete the trailing LF
                    if (_s_blocks.findCmd(sbCmd.toString().toLowerCase(), _mmd.apblock))
                        _omme.notifyMUDLocation((_eBBSLoc = OMUD.eBBSLocation.MUD_EDITOR));

                    // if we just had a single linefeed/enter (length would be zero here),
                    // translate that to something visible...
                    if (sbCmd.length() == 0)
                        sbCmd.append("<ENTER>");
                    sbCmd.append(" (" + _mmd.apblock.strCmdText + ")");
                        _omme.notifyMUDUserCmd(sbCmd.toString());
                    sbCmd.setLength(0); // clear to show as processed
                }
            }

            // ------------------
            // Find Statline
            // ------------------
            if (_sbDataTelnet.length() > 0 && (pos_data_found_start = _s_blocks.parseStatline(_mmd, _sbDataTelnet)) > -1){
                pos_buf_delete_len = updateParseDeleteLen(pos_data_found_start, pos_buf_delete_len);

                // ------------------
                // Combat Timer
                // ------------------
                // check if we should clear the combat lines/buffer...
                if (_clear_combat_buffer){
                    _clear_combat_buffer = false;
                    _mmd.dataCombat.lines.clear();
                    // don't change the state if already something other than combat (rest, med, etc...
                    if (_mmd.dataStatline.action_state == OMUD_MMUD_DataBlockStatline.eActionState.COMBAT)
                        _mmd.dataStatline.action_state  = OMUD_MMUD_DataBlockStatline.eActionState.READY;                    
                }

                // ------------------
                // Return from Editor
                // ------------------
                // check if returning from training stats or at a prompt/input...
                if (_eBBSLoc == OMUD.eBBSLocation.MUD_EDITOR){
                    _omme.notifyMUDLocation((_eBBSLoc = OMUD.eBBSLocation.MUD));

                    // check for welcome msg...
                    if (!_mmd.got_statline){
                        _mmd.got_statline = true;
                        _mmd.is_kai = 
                             _mmd.dataStatline.ma_str.length() > 0 &&
                            !_mmd.dataStatline.ma_str.equals(OMUD_MMUD_DataBlockStatline.MSTR_SLINE_MA);
                        _omme.notifyMUDInit(new String(_mmd.strWelcome));
                    }

                    // some basic auto commands on enter - can make manual/auto modes for this later...
                    if (_mmd.dataRoom.name.length() == 0)
                        _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.ROOM);
                    if (_mmd.dataStats.name_first.length() == 0)
                        _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.STATS);
                    _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.INV);
                    _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.EXP);
                    if (_mmd.dataStatline.ma_str.length() > 0)
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
                        (pos_data_found_start = _s_blocks.parseLineBlocks(_mmd, _sbDataTelnet, i)) > -1){
                        pos_buf_delete_len = updateParseDeleteLen(pos_data_found_start, pos_buf_delete_len);
                        i = pos_data_found_start;
                    }

                // notify for statline update and other data that was updated...
                OMUD_MMUD_DataBlockStatline dStatline = new OMUD_MMUD_DataBlockStatline(_mmd.dataStatline);
                _omme.notifyMUDStatline(dStatline);
                     if (_mmd.apblock.data_type == OMUD_MMUD_DataBlock.eBlockType.ROOM)
                    _omme.notifyMUDRoom(new OMUD_MMUD_DataBlockRoom(_mmd.dataRoom));
                else if (_mmd.apblock.data_type == OMUD_MMUD_DataBlock.eBlockType.COMBAT){
                    resetCombatTimer(true);
                    _omme.notifyMUDCombat(new OMUD_MMUD_DataBlockCombat(_mmd.dataCombat));
                    if (_mmd.dataCombat.exp_gained > 0){
                        _mmd.dataCombat.exp_gained = 0;
                        _omme.notifyMUDExp(new OMUD_MMUD_DataBlockExp(_mmd.dataExp));
                    }
                }
                else if (_mmd.apblock.data_type == OMUD_MMUD_DataBlock.eBlockType.INV)
                    _omme.notifyMUDInv(new OMUD_MMUD_DataBlockInv(_mmd.dataInv));
                else if (_mmd.apblock.data_type == OMUD_MMUD_DataBlock.eBlockType.EXP)
                    _omme.notifyMUDExp(new OMUD_MMUD_DataBlockExp(_mmd.dataExp));
                else if (_mmd.apblock.data_type == OMUD_MMUD_DataBlock.eBlockType.STATS)
                    _omme.notifyMUDStats(new OMUD_MMUD_DataBlockStats(_mmd.dataStats), dStatline);
                else if (_mmd.apblock.data_type == OMUD_MMUD_DataBlock.eBlockType.SHOP)
                    _omme.notifyMUDShop(new OMUD_MMUD_DataBlockShop(_mmd.dataShop));
                else if (_mmd.apblock.data_type == OMUD_MMUD_DataBlock.eBlockType.SPELLS)
                    _omme.notifyMUDSpells(new OMUD_MMUD_DataBlockSpells(_mmd.dataSpells));
                else if (_mmd.apblock.data_type == OMUD_MMUD_DataBlock.eBlockType.WHO)
                    _omme.notifyMUDWho(new OMUD_MMUD_DataBlockWho(_mmd.dataWho));
                if (_mmd.apblock.sbDebug.length() > 0)
                    _omme.notifyMUDDebug(_mmd.apblock.sbDebug.toString());

                // check if we need to get a room refresh (gets reset below)...
                if (_mmd.apblock.refresh_room)
                    _omme.requestMUDData(OMUD_MMUD_DataBlock.eBlockType.ROOM);

                // reset active block with statline forced as last data type...
                _mmd.apblock = new OMUD_MMUD_ParseBlocks.ActiveParseBlock(false, OMUD_MMUD_DataBlock.eBlockType.STATLINE);
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

        return _eBBSLoc == OMUD.eBBSLocation.BBS ? true : !_mmd.apblock.statline_wait;
    }
}
