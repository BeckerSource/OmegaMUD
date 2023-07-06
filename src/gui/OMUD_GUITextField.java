import java.awt.Font;
import javax.swing.JTextField;

public class OMUD_GUITextField extends JTextField{
    OMUD_GUITextField()                 {this("",       false, false, false);}
    OMUD_GUITextField(String strText)   {this(strText,  false, false, false);}
    OMUD_GUITextField(String strText, boolean editable, boolean bold, boolean center_text){
        setText(strText);

        setEditable(editable);
        setBackground(OMUD.GUI_BG);
        setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        if (bold)
            setFont(getFont().deriveFont(Font.BOLD, getFont().getSize()));
        if (center_text)
            setHorizontalAlignment(JTextField.CENTER);
    }
}
