import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;

public class OMUD_BufferInsert implements OMUD_IBufferMod{
    private String      _strDbgFnc =        "";
    private OMUD_ANSI   _ansi =             null;
    private int         _pos_offset =       0;
    private int         _pos_caret_new =    0;
    private int         _attr_pos =         -1;
    private int         _overwrite_len =    0;

    public OMUD_BufferInsert(String strDbgFnc, OMUD_Buffer omb, OMUD_ANSI ansi, int p, int c) {this(strDbgFnc, omb, ansi, p, c, -1);}
    public OMUD_BufferInsert(String strDbgFnc, OMUD_Buffer omb, OMUD_ANSI ansi, int p, int c, int attr_pos){
        _strDbgFnc = strDbgFnc;
        _ansi = (ansi != null ? new OMUD_ANSI(ansi) : new OMUD_ANSI());
        _pos_offset = p;
        _pos_caret_new = c;
        _attr_pos = attr_pos;
        omb.setPos(_pos_caret_new);

        if (_ansi.getText().length() > 0 && _pos_offset >= 0 && _pos_offset <= omb.getText().length()){

            // ------------------
            // Overwrite Logic
            // ------------------
            // check for a linefeed in the text, don't include it in the overwrite calcs if found...
            int text_len = _ansi.getText().length();
            boolean ends_with_lf = _ansi.getText().charAt(text_len - 1) == OMUD.ASCII_LF;

            if (ends_with_lf)
                text_len--;
            // this will skip solo linefeeds from above calc...
            if (text_len > 0){
                int row_start = OMUD.getPrevLF(omb.getText(), _pos_offset) + 1;
                if (row_start < 0)
                    row_start = 0;
                int row_end = OMUD.getNextLF(omb.getText(), _pos_offset) - 1;
                if (row_end < 0)
                    row_end = omb.getText().length();

                // get available overwrite space...
                _overwrite_len = row_end - _pos_offset;
                if (text_len < _overwrite_len)
                    _overwrite_len = text_len;
                if (_overwrite_len  > 0)
                    omb.deleteText(_pos_offset, _pos_offset + _overwrite_len);
            }
            omb.insertText(_pos_offset, _ansi.getText(), ends_with_lf);
        }
        else OMUD.logError("Error adding buffer insert mod: " + _strDbgFnc + ", " + _pos_offset + ", " + omb.getText().length() + ", " + _ansi.getText());
    }

    public void render(JTextPane tp, StyledDocument docSwap, ArrayList<OMUD_GUIBlinkText> arrlBlink){
        if (_ansi.getText().length() > 0){
            SimpleAttributeSet attr = null;

            // use attributes set in the ANSI object...
            if (_attr_pos <= -1){
                attr = new SimpleAttributeSet();
                OMUD_ANSI.setAttrFromANSI(_ansi, attr, true);
                OMUD_ANSI.setAttrFromANSI(_ansi, attr, false);
                StyleConstants.setItalic(attr,          _ansi.isItalic());
                StyleConstants.setUnderline(attr,       _ansi.isUnderline());
                StyleConstants.setStrikeThrough(attr,   _ansi.isStrike());
            // use attributes from a specific position (for cursor move functions)...
            } else {
                attr = new SimpleAttributeSet(tp.getStyledDocument().getCharacterElement(_attr_pos).getAttributes());
                OMUD_ANSI.setANSIFromAttr(_ansi, attr);
            }

            if (_ansi.isBlink())
                arrlBlink.add(new OMUD_GUIBlinkText(_pos_offset, _ansi.getText().length(), (SimpleAttributeSet) attr.copyAttributes()));
            try{
                // JTextPane performance boost by writing to the doc when not attached/visible (swap)...
                StyledDocument docBuf = tp.getStyledDocument();
                tp.setStyledDocument(docSwap);
                    // check for overwriting fisrt...
                    if (_overwrite_len > 0)
                        docBuf.remove(_pos_offset, _overwrite_len);
                    docBuf.insertString(_pos_offset, _ansi.getText().toString(), attr);
                tp.setStyledDocument(docBuf);
                tp.setCaretPosition(_pos_caret_new);
            } catch (Exception e){
                OMUD.logError("Error on buffer insert mod render: " + _strDbgFnc + ": " + e.getMessage());
            }
        }
    }
}
