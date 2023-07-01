import javax.swing.JTextArea;

public class OMUD_GUITextArea extends JTextArea{
    OMUD_GUITextArea(){     
        setEditable(false);
        setBackground(OMUD.GUI_BG);
        setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        setCaretColor(OMUD.TERMINAL_LOCAL_INFO_FG);
        getCaret().setVisible(true);
        setFont(OMUD.getTerminalFont());
        setLineWrap(true);        
    }
}
