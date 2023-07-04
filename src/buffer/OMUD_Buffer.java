public class OMUD_Buffer{
    private class Row {public int pos_start = 0; public int pos_end = 0;}
    private StringBuilder   _sbText =           null;
    private Row[]           _rows =             null;
    private int             _row_count =        1; // 1-based count
    private int             _line_count =       1; // 1-based count
    private int             _row_num =          0;
    private int             _col_num =          0;
    private int             _pos =              0;
    private int             _pos_top_left =     0;
    private boolean         _update_rows_edit = false;
    private boolean         _update_rows_move = false;

    public OMUD_Buffer(){
        _sbText = new StringBuilder();
        resetRows();
    }

    private void resetRows(){
        _row_num = 0;
        _col_num = 0;
        _row_count = 1;
        _rows = new Row[OMUD.TERMINAL_ROWS];
        for (int i = 0; i < OMUD.TERMINAL_ROWS; ++i)
            _rows[i] = new Row();
    }

    public void insertText(int insert_offset, StringBuilder sbText, boolean ends_with_lf){
        _sbText.insert(insert_offset, sbText);
        _update_rows_edit = true;

        if (ends_with_lf){
            _line_count++;

            // terminal rows not filled yet...
            if (_row_count < OMUD.TERMINAL_ROWS)
                _row_count++;
            // terminal rows filled, move the top-left position...
            else _pos_top_left = OMUD.getNextLF(_sbText, _pos_top_left) + 1; // skip to next row char after LF
        }
    }

    public void deleteText(int delete_start, int delete_end){
        _update_rows_edit = delete_start != delete_end;

        // if screen clear is detected, reset the rows and set the terminal top-left buffer position...
        if (!_update_rows_edit && delete_start == _sbText.length() && _row_count == OMUD.TERMINAL_ROWS){
            _pos_top_left = delete_start;
            resetRows();
        } else if (_update_rows_edit) {
            _sbText.delete(delete_start, delete_end);
        }
    }

    public String getRowText(int row_num){
        if (row_num >= 0 && row_num < _row_count && _rows[row_num].pos_end > _rows[row_num].pos_start)
             return _sbText.substring(_rows[row_num].pos_start, _rows[row_num].pos_end + 1);
        else return "";
    }

    // updateRows(): called after all mod renders are completed on a pass, update row positions and cursor position if requested
    public void updateRows(){
        int buf_row_start = 0;
        int buf_row_end =   _sbText.length() - 1;
        int final_row =     _row_count - 1;

        // go reverse and update the row data (and cursor position if requested)...
        for (int i = final_row; i >= 0 && (_update_rows_edit || _update_rows_move); --i){

            // try to find earliest matching row start/end vs buffer positions...
            if (_update_rows_edit){
                buf_row_start = OMUD.getPrevLF(_sbText, buf_row_end) + 1;
                if (_rows[i].pos_start == buf_row_start && _rows[i].pos_end == buf_row_end){
                    _update_rows_edit = false;
                } else {
                    _rows[i].pos_start = buf_row_start;
                    _rows[i].pos_end =   buf_row_end;

                    buf_row_end = buf_row_start - 2; // skip backwards (through the LF) to the prev row end
                    if (buf_row_end < 0)
                        buf_row_end = 0;
                }

            }

            // check if the cursor is here...
            if (_update_rows_move){
                if (_pos >= _rows[i].pos_start && _pos <= _rows[i].pos_end + 1){ // +1 for at the end of the buffer
                    _row_num = i;
                    _col_num = _pos - _rows[i].pos_start;
                    _update_rows_move = false;
                }
            }
        }

        _update_rows_edit = false;
        _update_rows_move = false;
    }

    public void setPos(int pos_new){
        if (_pos != pos_new){
            _pos  = pos_new;
            _update_rows_move = true;
        }
    }

    public int getPos()             {return _pos;}
    public int getTopLeftPos()      {return _pos_top_left;}
    public int getRowNum()          {return _row_num;}
    public int getColNum()          {return _col_num;}
    public int getLineCount()       {return _line_count;}
    public StringBuilder getText()  {return _sbText;}
    public void setLineCount(int line_count)    {_line_count = line_count;}
    public void setTopLeftPos(int pos_top_left) {_pos_top_left = pos_top_left;}
}
