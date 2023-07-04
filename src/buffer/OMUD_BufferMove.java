import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class OMUD_BufferMove implements OMUD_IBufferMod{
    private String  _strDbgFnc =     "";
    private int     _pos_caret_new = 0;

    public OMUD_BufferMove(String strDbgFnc, OMUD_Buffer omb, int c){
        _strDbgFnc = strDbgFnc;
        _pos_caret_new = c;
        omb.setPos(_pos_caret_new);
    }

    public void render(JTextPane tp, StyledDocument docSwap, ArrayList<OMUD_GUIBlinkText> arrlBlink){
        try{
            tp.setCaretPosition(_pos_caret_new);
        } catch (Exception e){
            OMUD.logError("Error on buffer move mod render: " + _strDbgFnc + ": " + e.getMessage());
        }
    }
}
