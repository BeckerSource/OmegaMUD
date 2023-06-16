import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class OMUD_BufferRemove implements OMUD_IBufferMod{
    private String  _strDbgFnc =      "";
    private int     _pos_offset =     0;
    private int     _rem_length =     0;
    private int     _pos_caret_new =  0;
    private boolean _buf_limit_mod =  false;

    public OMUD_BufferRemove(String strDbgFnc, OMUD_Buffer omb, int p, int r, int c, boolean buf_limit_mod){
        _strDbgFnc =     strDbgFnc;
        _pos_offset =    p;
        _rem_length =    r;
        _pos_caret_new = c;
        _buf_limit_mod = buf_limit_mod;
        omb.setPos(_pos_caret_new);

        if (_pos_offset >= 0 && _pos_offset <= omb.getText().length())
            omb.deleteText(_pos_offset, _pos_offset + _rem_length);
        else OMUD.logError("Error adding buffer remove mod: " + _strDbgFnc + ", " + _pos_offset + ", " + omb.getText().length() + ", " + _rem_length);
    }

    public void render(JTextPane tp, StyledDocument docSwap, ArrayList<OMUD_GUIBlinkText> arrlBlink){
        try{
            // update blinking ANSI positions outside the delete range and 
            // remove blinking ANSI stored in the deleted range...
            if (_buf_limit_mod){
                int delete_end = _pos_offset + _rem_length;
                for(int i = arrlBlink.size() - 1; i >= 0; --i){
                    if  (arrlBlink.get(i).pos <= delete_end)
                         arrlBlink.remove(i);
                    else arrlBlink.get(i).pos -= delete_end;
                }                
            }

            // JTextPane performance boost by writing to the doc when not attached/visible (swap)...
            StyledDocument docBuf = tp.getStyledDocument();
            tp.setStyledDocument(docSwap);
                docBuf.remove(_pos_offset, _rem_length);
            tp.setStyledDocument(docBuf);
            tp.setCaretPosition(_pos_caret_new);
        } catch (Exception e){
            OMUD.logError("Error on buffer remove mod render: " + _strDbgFnc + ": " + e.getMessage());
        }
    }
}
