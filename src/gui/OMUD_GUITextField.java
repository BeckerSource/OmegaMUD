import javax.swing.JTextField;

public class OMUD_GUITextField extends JTextField{
	OMUD_GUITextField()					{this("", 		false);}
	OMUD_GUITextField(String strText)	{this(strText, 	false);}
	OMUD_GUITextField(String strText, boolean editable){
		setText(strText);
		
        setEditable(editable);
        setBackground(OMUD.GUI_BG);
        setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
	}
}
