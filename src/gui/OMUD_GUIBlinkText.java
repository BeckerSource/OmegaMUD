import java.awt.Color;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;

public class OMUD_GUIBlinkText{
    public Color fg_on = null;
    public Color fg_off = null;
    public int pos = 0;
    public int length = 0;
    public SimpleAttributeSet attr = null;
    public OMUD_GUIBlinkText(int p, int l, SimpleAttributeSet at){
        pos =    p;
        length = l;
        attr =   at;
        fg_on =  StyleConstants.getForeground(attr);
        fg_off = StyleConstants.getBackground(attr);
    }
}
