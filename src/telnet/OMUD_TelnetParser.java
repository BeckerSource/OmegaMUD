import java.util.ArrayList;

public class OMUD_TelnetParser{
    private OMUD_ITelnetEvents          _omte =         null;
    private OMUD_ANSI                   _ansi =         null; // pseudo cursor to save stles/colors during parser
    private OMUD_Buffer                 _omb =          null;
    private OMUD_MMUDParser             _mmp =          null;
    private ArrayList<OMUD_IBufferMod>  _arrlBMods =    null;
    private StringBuilder               _sbEscSeq =     null;
    private StringBuilder               _sbNewData =    null;
    private boolean                     _gui_busy =     false;
    private final int MAX_BUFFER_LINES = 150;

    public OMUD_TelnetParser(OMUD_ITelnetEvents omte, OMUD_IMUDEvents omme){
        _omte = omte;
        _omb = new OMUD_Buffer();
        _mmp = new OMUD_MMUDParser(omme);
        _arrlBMods = new ArrayList<OMUD_IBufferMod>();
        _sbEscSeq = new StringBuilder();
        _sbNewData = new StringBuilder();
        reset();
    }

    public void reset(){
        _ansi = new OMUD_ANSI();
        _sbEscSeq.setLength(0);
        _mmp.clearTelnetData();
    }

    public int getBufferPos(){return _omb.getPos();}

    // --------------
    // Cursor Move Functions
    // --------------
    // crsrMoveUpDown(): move corsor up/down # rows/lines
    private void crsrMoveUpDown(String strDbgFnc, boolean move_up, int move_dist){
        int caret_row_start =       OMUD.getPrevLF(_omb.getText(), _omb.getPos()) + 1;
        int col_offset =            _omb.getPos() - caret_row_start;
        int caret_new_row_end =     _omb.getPos();
        int caret_new_row_start =   _omb.getPos();

        for (; move_dist > 0 ; --move_dist){
            if (move_up)
                 caret_new_row_end =    OMUD.getPrevLF(_omb.getText(), caret_new_row_end)   - 1;
            else caret_new_row_start =  OMUD.getNextLF(_omb.getText(), caret_new_row_start) + 1;

            if (move_dist == 1){
                if (move_up){
                    caret_new_row_start =   OMUD.getPrevLF(_omb.getText(), caret_new_row_end)   + 1; // +1 covers -1 result
                } else {
                    caret_new_row_end =     OMUD.getNextLF(_omb.getText(), caret_new_row_start) - 1;
                    if (caret_new_row_end < 0)
                        caret_new_row_end = _omb.getText().length();
                }
            }
        }

        // if the destination line isn't long enough for the new caret pos, insert spaces...
        int caret_pos_new = caret_new_row_start + col_offset;
        if (caret_pos_new > caret_new_row_end){
            OMUD_ANSI ansi_spacer = new OMUD_ANSI();
            ansi_spacer.getText().append(OMUD.getFillString(" ", caret_pos_new - caret_new_row_end));
            _arrlBMods.add(new OMUD_BufferInsert(strDbgFnc, _omb, ansi_spacer, caret_new_row_end + 1, caret_pos_new, caret_new_row_end));
        // else just set the new pos...
        } else {
            _arrlBMods.add(new OMUD_BufferMove(strDbgFnc, _omb, caret_pos_new));
        }
    }

    // crsrMoveLeftRight(): move cursor left/right # columns
    private void crsrMoveLeftRight(String strDbgFnc, boolean move_left, int move_dist){
        int caret_row_start =   OMUD.getPrevLF(_omb.getText(), _omb.getPos()) + 1;
        int caret_row_end =     OMUD.getNextLF(_omb.getText(), _omb.getPos()) - 1;
        if (caret_row_end < 0)
            caret_row_end =     _omb.getText().length();
        int caret_pos_new = _omb.getPos() - (move_left ? move_dist : -move_dist);

        // special: moving right: if not enough space, do an insert and move the cursor using the inser mod...
        if (!move_left && caret_pos_new > caret_row_end){
            OMUD_ANSI ansi_spacer = new OMUD_ANSI();
            ansi_spacer.getText().append(OMUD.getFillString(" ", caret_pos_new - caret_row_end));
            _arrlBMods.add(new OMUD_BufferInsert(strDbgFnc, _omb, ansi_spacer, _omb.getPos(), caret_pos_new));
        // else moving right within bounds or moving left...
        } else {
            if (move_left && caret_pos_new < caret_row_start)
                caret_pos_new = caret_row_start;
            _arrlBMods.add(new OMUD_BufferMove(strDbgFnc, _omb, caret_pos_new));
        }
    }

    // --------------
    // Parsing
    // --------------
    // checkBufferLimits(): remove buffer lines from the beginning/top if at max
    private void checkBufferLimits(){
        if (_omb.getLineCount() > MAX_BUFFER_LINES){
            int delete_end = 0;
            for (int i = _omb.getLineCount() - MAX_BUFFER_LINES; i > 0; --i)
                delete_end = OMUD.getNextLF(_omb.getText(), delete_end) + 1; // +1 to include LF, end is exclusive
            // if need to limit...
            if (delete_end > 0){
                _arrlBMods.add(new OMUD_BufferRemove("BUFFER_LIMITS", _omb, 0, delete_end, _omb.getPos() - delete_end, true));
                // update line count and top-left position...
                _omb.setLineCount(MAX_BUFFER_LINES);
                _omb.setTopLeftPos(_omb.getTopLeftPos() - delete_end);
            }
        }
    }

    public void setGUIReady(){_gui_busy = false;}
    public boolean threadParseData(String strNewData, StringBuilder sbCmd){
        if (strNewData.length() > 0)
            _sbNewData.append(strNewData);

        boolean append_ansi_char = false;
        _ansi.getText().setLength(0);
        _arrlBMods.clear();
        char c = 0;
        int  d = 0;
        while (_sbNewData.length() > 0){
            c = _sbNewData.charAt(0);
            d = (int) c;
            _sbNewData.deleteCharAt(0);

            // 0x00-0x1F: check for ANSI control codes...
            if (d < OMUD.ASCII_SPC || d == OMUD.ASCII_DEL){
                // we shouldn't be here with an existing escape sequence - reset the escape sequence...
                if (_sbEscSeq.length() > 0)
                    _sbEscSeq.setLength(0);

                // BEL/bell...
                if (d == OMUD.ASCII_BEL){
                    // ignore (for now)
                // BS/backspace...
                } else if (d == OMUD.ASCII_BS){
                    if (_ansi.getText().length() > 0){
                        _arrlBMods.add(new OMUD_BufferInsert("BS", _omb, _ansi, _omb.getPos(), _omb.getPos() + _ansi.getText().length()));
                        _ansi.getText().setLength(0);
                    }

                    // backspacing at the end of the buffer...
                    if (_omb.getPos() == _omb.getText().length()){
                        _arrlBMods.add(new OMUD_BufferRemove("BS", _omb, _omb.getPos() - 1, 1, _omb.getPos() - 1, false));
                    // backspacing within a row somewhere...
                    } else if (_omb.getPos() - 1 > 0){
                        _ansi.getText().append(" ");
                        _arrlBMods.add(new OMUD_BufferInsert("BS", _omb, _ansi, _omb.getPos() - 1, _omb.getPos() - 1));
                        _ansi.getText().setLength(0);
                    }

                    // MajorMUD injects a random "alpha char+BS" seuqnce into each exit type string (like below),
                    // so just manually delete the last char that was added.  See example:
                    // Example: nEorth, eHast, wUest, dTown
                    _mmp.deleteLastChar();

                // LF: Process linefeeds immediately:
                // [1] Functions like 'ESC[K' (clear from cursor to end and don't move cursor)
                // can be the last thing on a row that is not full, and will have added extra spaces.
                // In that case, we need to move the buffer pos to the end before placing the linefeed.
                // [2] This also helps prevent extra complex logic for overwrite code and 
                // large blocks of text with multiple linefeeds inside.
                } else if (d == OMUD.ASCII_LF){
                    // if current ansi text is empty, force the buffer pos to the very end (above notes) - 
                    // otherwise use current buffer position...
                    int pos_offset = _ansi.getText().length() == 0 ? _omb.getText().length() : _omb.getPos();
                    _ansi.getText().append(c);
                    _arrlBMods.add(new OMUD_BufferInsert("LF", _omb, _ansi, pos_offset, pos_offset + _ansi.getText().length()));
                    _ansi.getText().setLength(0);

                    // majormud needs LF for finding game strings...
                    _mmp.appendChar(OMUD.ASCII_LF);
                // CR...
                } else if (d == OMUD.ASCII_CR){
                    // ignore (only process LF)
                // ESC/ANSI sequence...
                } else if (d == OMUD.ASCII_ESC){
                    _sbEscSeq.append(c);
                    // show/clear the current out text to use already processed sequence styles, modes, and colors...
                    if (_ansi.getText().length() > 0){
                        _arrlBMods.add(new OMUD_BufferInsert("ESC", _omb, _ansi, _omb.getPos(), _omb.getPos() + _ansi.getText().length()));
                        _ansi.getText().setLength(0);
                    }
                // tabs and form-feed: unhandled for now  until detected - 
                // use error message for visibility...
                } else if (d == OMUD.ASCII_HTB || d == OMUD.ASCII_VTB || d == OMUD.ASCII_FF){
                    append_ansi_char = true;
                    OMUD.logError("Found unhandled tab or formfeed: " + d);
                // else show as printed symbol...
                } else {
                    append_ansi_char = true;
                }
            // 0x20–0x2F: part 2 of escape sequence (zero or many)...
            } else if (d < OMUD.ASCII_ZRO){
                if (_sbEscSeq.length() > 0){
                    _sbEscSeq.append(c);

                // special: check for ANSI RIP escape sequence query (and ignore)...
                if (d == OMUD.ASCII_EXC && 
                    _sbEscSeq.length() > 2 && 
                    _sbEscSeq.charAt(0) == OMUD.ASCII_ESC && 
                    _sbEscSeq.charAt(1) == OMUD.ASCII_LBR && 
                    (OMUD.compareSBString(_sbEscSeq, OMUD.RIP_QVERSION1) ||
                     OMUD.compareSBString(_sbEscSeq, OMUD.RIP_QVERSION2) ||
                     OMUD.compareSBString(_sbEscSeq, OMUD.RIP_OFF)       ||
                     OMUD.compareSBString(_sbEscSeq, OMUD.RIP_ON))){
                        _sbEscSeq.setLength(0);
                        OMUD.logInfo("RIPscrip ANSI escape sequence found and ignored.");
                    }
                } else append_ansi_char = true;
            // 0x30–0x3F: part 1 of escape sequence (zero or many)...
            } else if (d < OMUD.ASCII_AT){
                if (_sbEscSeq.length() > 0)
                    _sbEscSeq.append(c);
                else append_ansi_char = true;
            // 0x40–0x7E: '[' char after ESC byte for CSI or final byte for ESC sequence...
            } else if (d < OMUD.ASCII_DEL){
                if (_sbEscSeq.length() > 0){
                    _sbEscSeq.append(c);

                    // ------------------
                    // Check for Completed "ESC[" Sequences
                    // ------------------
                    if (_sbEscSeq.length() > 2){
                        _sbEscSeq.delete(0, 2); // remove the "ESC[" prefix

                        // some convenience vars for processing below...
                        int  esc_seq_last_pos  = _sbEscSeq.length() - 1;
                        char esc_seq_last_char = _sbEscSeq.charAt(esc_seq_last_pos);

                        // ------------------
                        // Graphics Functions
                        // ------------------
                        if (esc_seq_last_char == OMUD.CSI_GRAPHICS){
                            // majormud game string compare needs the escape sequence prefix...
                            _mmp.appendChar(OMUD.ASCII_ESC);
                            _mmp.appendChar(OMUD.ASCII_LBR);
                            _mmp.appendSB(_sbEscSeq);

                            _sbEscSeq.deleteCharAt(esc_seq_last_pos); // strip last char (we know the function now)

                            // split with ; to compensate for multiple modes...
                            String[] strFuncs = _sbEscSeq.toString().split(";");
                            for (String code : strFuncs) {
                                // Styles: ENABLE...
                                if (code.equals("0")){
                                    _ansi.resetStyles();
                                    _ansi.resetFG();
                                    _ansi.resetBG();
                                } else if (code.equals("1")){
                                    _ansi.setBold(true);
                                //} else if (code.equals("2")){
                                //    _ansi.setDim(true);
                                //} else if (code.equals("3")){
                                //    _ansi.setItalic(true);
                                //} else if (code.equals("4")){
                                //    _ansi.setUnderline(true);
                                } else if (code.equals("5")){
                                    _ansi.setBlink(true);
                                } else if (code.equals("7")){
                                    _ansi.setInvert(true);
                                //} else if (code.equals("8")){
                                //    _ansi.setInvis(true);
                                //} else if (code.equals("9")){
                                //    _ansi.setStrike(true)

                                // Styles: DISABLE...
                                } else if (code.equals("22")){
                                    _ansi.setBold(false);
                                    _ansi.setDim(false);
                                //} else if (code.equals("23")){
                                //    _ansi.setItalic(false);
                                //} else if (code.equals("24")){
                                //    _ansi.setUnderline(false);
                                } else if (code.equals("25")){
                                    _ansi.setBlink(false);
                                //} else if (code.equals("27")){
                                //    _ansi.setInvert(false);
                                //} else if (code.equals("28")){
                                //    _ansi.setInvis(false);
                                //} else if (code.equals("29")){
                                //    _ansi.setStrike(false)

                                // Colors 8: FG...
                                } else if (code.equals("30")){
                                    _ansi.setFG(OMUD.eANSIColors.BLACK);
                                } else if (code.equals("31")){
                                    _ansi.setFG(OMUD.eANSIColors.RED);
                                } else if (code.equals("32")){
                                    _ansi.setFG(OMUD.eANSIColors.GREEN);
                                } else if (code.equals("33")){
                                    _ansi.setFG(OMUD.eANSIColors.YELLOW);
                                } else if (code.equals("34")){
                                    _ansi.setFG(OMUD.eANSIColors.BLUE);
                                } else if (code.equals("35")){
                                    _ansi.setFG(OMUD.eANSIColors.MAGENTA);
                                } else if (code.equals("36")){
                                    _ansi.setFG(OMUD.eANSIColors.CYAN);
                                } else if (code.equals("37")){
                                    _ansi.setFG(OMUD.eANSIColors.WHITE);
                                } else if (code.equals("39")){
                                    _ansi.resetFG();

                                // Colors 8: BG...
                                } else if (code.equals("40")){
                                    _ansi.setBG(OMUD.eANSIColors.BLACK);
                                } else if (code.equals("41")){
                                    _ansi.setBG(OMUD.eANSIColors.RED);
                                } else if (code.equals("42")){
                                    _ansi.setBG(OMUD.eANSIColors.GREEN);
                                } else if (code.equals("43")){
                                    _ansi.setBG(OMUD.eANSIColors.YELLOW);
                                } else if (code.equals("44")){
                                    _ansi.setBG(OMUD.eANSIColors.BLUE);
                                } else if (code.equals("45")){
                                    _ansi.setBG(OMUD.eANSIColors.MAGENTA);
                                } else if (code.equals("46")){
                                    _ansi.setBG(OMUD.eANSIColors.CYAN);
                                } else if (code.equals("47")){
                                    _ansi.setBG(OMUD.eANSIColors.WHITE);
                                } else if (code.equals("49")){
                                    _ansi.resetBG();

                                /*
                                // Colors 256: FG ( 38;5;{ID} )...
                                } else if (code.equals("38")){
                                // Colors 256: BG ( 48;5;{ID} )...
                                } else if (code.equals("48")){
                                */

                                /*
                                // Colors 8: Bright FG (bolded)...
                                } else if (code.equals("90")){
                                } else if (code.equals("91")){
                                } else if (code.equals("92")){
                                } else if (code.equals("93")){
                                } else if (code.equals("94")){
                                } else if (code.equals("95")){
                                } else if (code.equals("96")){
                                } else if (code.equals("97")){
                                // Colors 8: Bright BG (bolded)...
                                } else if (code.equals("100")){
                                } else if (code.equals("101")){
                                } else if (code.equals("102")){
                                } else if (code.equals("103")){
                                } else if (code.equals("104")){
                                } else if (code.equals("105")){
                                } else if (code.equals("106")){
                                } else if (code.equals("107")){
                                */
                                } else {
                                    OMUD.logDebug("FN_M: " + _sbEscSeq + ", " + code);
                                }
                            }

                        // ------------------
                        // Cursor Save/Restore
                        // ------------------
                        // save cursor position...
                        } else if (_sbEscSeq.charAt(0) == OMUD.CSI_CRSR_SAVE){
                            // ignore
                        // restore cursor position...
                        } else if (_sbEscSeq.charAt(0) == OMUD.CSI_CRSR_REST){
                            // ignore
                        // ------------------
                        // Cursor Up/Down/Right/Left
                        // NOTE: move value is not required in sequence, so default to 1 for if not present.
                        // ------------------
                        // moves cursor up...
                        } else if (esc_seq_last_char == OMUD.CSI_CRSR_UP){
                            int move_dist = _sbEscSeq.length() == 1 ? 1 : Integer.parseInt(_sbEscSeq.substring(0, esc_seq_last_pos));
                            crsrMoveUpDown(OMUD.CSI_CRSR_UP_STR, true, move_dist);
                        // moves cursor down...
                        } else if (esc_seq_last_char == OMUD.CSI_CRSR_DOWN){
                            int move_dist = _sbEscSeq.length() == 1 ? 1 : Integer.parseInt(_sbEscSeq.substring(0, esc_seq_last_pos));
                            crsrMoveUpDown(OMUD.CSI_CRSR_DOWN_STR, false, move_dist);
                        // moves cursor right...
                        } else if (esc_seq_last_char == OMUD.CSI_CRSR_RIGHT){
                            int move_dist = _sbEscSeq.length() == 1 ? 1 : Integer.parseInt(_sbEscSeq.substring(0, esc_seq_last_pos));
                            crsrMoveLeftRight(OMUD.CSI_CRSR_RIGHT_STR, false, move_dist);
                        // moves cursor left...
                        } else if (esc_seq_last_char == OMUD.CSI_CRSR_LEFT){
                            int move_dist = _sbEscSeq.length() == 1 ? 1 : Integer.parseInt(_sbEscSeq.substring(0, esc_seq_last_pos));
                            crsrMoveLeftRight(OMUD.CSI_CRSR_LEFT_STR, true, move_dist);
                            // majormud game string compare needs the escape sequence prefix...
                            _mmp.appendChar(OMUD.ASCII_ESC);
                            _mmp.appendChar(OMUD.ASCII_LBR);
                            _mmp.appendSB(_sbEscSeq);
                        // ------------------
                        // Cursor Move to Row+Col
                        // ------------------
                        // move cursor to row and column number...
                        } else if (esc_seq_last_char == OMUD.CSI_CRSR_RWCL1 || 
                                   esc_seq_last_char == OMUD.CSI_CRSR_RWCL2){

                            // strip last char id and split the move values ROW;COL...
                            _sbEscSeq.deleteCharAt(esc_seq_last_pos);
                            String[] strVals = _sbEscSeq.toString().split(";");
                            if (strVals.length == 2){
                                int dest_row = Integer.parseInt(strVals[0]);
                                int dest_col = Integer.parseInt(strVals[1]);
                                // NOTE: using stored "last clear pos" here with the assumption that
                                // the screen clear function was called - might have to revisit this later...
                                _arrlBMods.add(new OMUD_BufferMove(_sbEscSeq.toString(), _omb, _omb.getTopLeftPos()));
                                crsrMoveUpDown(_sbEscSeq.toString(), false, dest_row - 1);      // -1 here because we're already "moved" to row/col 1
                                crsrMoveLeftRight(_sbEscSeq.toString(), false, dest_col - 1);   // 
                            } else OMUD.logError("Found bad values for cursor move to row/col: " + _sbEscSeq);

                        // ------------------
                        // Erase Functions
                        // ------------------
                        // clear screen: clear terminal and move to top-left...
                        // (clears all after caret and move cursor to start of line)
                        } else if (OMUD.compareSBString(_sbEscSeq, OMUD.CSI_CLR_FULL)){
                            int caret_row_start = OMUD.getPrevLF(_omb.getText(), _omb.getPos()) + 1;
                            _arrlBMods.add(new OMUD_BufferRemove(OMUD.CSI_CLR_FULL, _omb, caret_row_start, _omb.getText().length() - caret_row_start, caret_row_start, false));
                            // SPECIAL: help manage the mud parser buffer by clearing it out on screen clears.
                            // The mud parser has no way of knowing if data is still incoming for commands.
                            _mmp.clearTelnetData();
                        // delete from cursor to end of line (don't move cursor)...
                        } else if (OMUD.compareSBString(_sbEscSeq, OMUD.CSI_CLR_LINE_CRSR1) || 
                                   OMUD.compareSBString(_sbEscSeq, OMUD.CSI_CLR_LINE_CRSR2)){

                            // majormud game string compare needs the escape sequence prefix - 
                            // (majormud only ever shows CSI_CLR_LINE_CRSR1 but assume it should be fine)...
                            _mmp.appendChar(OMUD.ASCII_ESC);
                            _mmp.appendChar(OMUD.ASCII_LBR);
                            _mmp.appendSB(_sbEscSeq);

                            int caret_row_start =   OMUD.getPrevLF(_omb.getText(), _omb.getPos()) + 1;
                            int caret_row_end =     OMUD.getNextLF(_omb.getText(), _omb.getPos()) - 1;
                            if (caret_row_end < 0)
                                caret_row_end =     _omb.getText().length();
                            int clear_len =         caret_row_end - _omb.getPos();
                            int row_len =           caret_row_end - caret_row_start;
                            int fill_len =          (OMUD.TERMINAL_COLS - row_len) + clear_len;

                            if (clear_len > 0)
                                _arrlBMods.add(new OMUD_BufferRemove(_sbEscSeq.toString(), _omb, _omb.getPos(), clear_len, _omb.getPos(), false));
                            if (fill_len > 0){
                                _ansi.getText().append(OMUD.getFillString(" ", fill_len));
                                _arrlBMods.add(new OMUD_BufferInsert(_sbEscSeq.toString(), _omb, _ansi, _omb.getPos(), _omb.getPos()));
                                _ansi.getText().setLength(0);
                            }

                        /*  ---- NOTE: UNTESTED! Uncomment when it's found somewhere in a game or on the BBS.
                        //  clear line (don't move cursor)...
                        } else if (OMUD.compareSBString(_sbEscSeq, OMUD.CSI_CLR_LINE_FULL)){
                            int caret_row_start =   OMUD.getPrevLF(_omb.getText(), _omb.getPos()) + 1;
                            int caret_row_end =     OMUD.getNextLF(_omb.getText(), _omb.getPos()) - 1;
                            if (caret_row_end < 0)
                                caret_row_end =     _omb.getText().length();
                            int clear_len =         caret_row_end - caret_row_start;

                            // remove entire line and fill with spaces...
                            if (clear_len > 0) {
                                _arrlBMods.add(new OMUD_BufferRemove(OMUD.CSI_CLR_LINE_FULL, _omb, caret_row_start, clear_len, -1, false));
                                _ansi.getText().append(OMUD.getFillString(" ", caret_row_end - _omb.getPos()));
                                _arrlBMods.add(new OMUD_BufferInsert(OMUD.CSI_CLR_LINE_FULL, _omb, _ansi, caret_row_start, -1));
                                _ansi.getText().setLength(0);
                            }*/

                        // ------------------
                        // Screen Functions
                        // ------------------
                        // ------------------
                        // Non-Standard Functions
                        // ------------------
                        } else {
                            OMUD.logDebug("FN: " + _sbEscSeq);
                        }
                        _sbEscSeq.setLength(0);
                    }
                } else {
                    append_ansi_char = true;
                }
            // extended ascii chars...
            } else {
                append_ansi_char = true;
            }

            if (append_ansi_char){
                append_ansi_char = false;
                _ansi.getText().append(c);
                _mmp.appendChar(c);
            }
        }

        // standard add of any remaining text...
        if (_ansi.getText().length() > 0)
            _arrlBMods.add(new OMUD_BufferInsert("0", _omb, _ansi, _omb.getPos(), _omb.getPos() + _ansi.getText().length()));

        // buffer changes are ready at this point, so update the log here (separate thread)...
        if (_arrlBMods.size() > 0){
            checkBufferLimits();
            _omb.updateRows();
            new Thread(new Runnable(){public void run(){OMUD.logToFile(OMUD.LOG_FILENAME_TERMINAL, _omb.getText().toString());}}).start();
        }

        // mud: parse data...
        boolean allow_cmds = _mmp.threadParseData(sbCmd);

        // notify/gui wait...
        _gui_busy = true;
        _omte.notifyTelnetParsed(_omb, _arrlBMods);
        try{
            while (_gui_busy) Thread.sleep(1);
        } catch (Exception e){OMUD.logError("Error parsing telnet data: error waiting (sleeping) for GUI: " + e.getMessage());}

        return allow_cmds;
    }
}
