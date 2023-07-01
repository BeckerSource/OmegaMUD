import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

interface OMUD_ITextInputEvents{
    public void notifyInputText(String text);
}

public class OMUD_GUITextInput extends JTextField{
	private OMUD_ITextInputEvents _tfe = null;
	private boolean _single_mode = false;

	public OMUD_GUITextInput(OMUD_ITextInputEvents tfe){
		_tfe = tfe;

        setBackground(OMUD.GUI_BG);
        setForeground(OMUD.TERMINAL_LOCAL_INFO_FG);
        setCaretColor(OMUD.TERMINAL_LOCAL_INFO_FG);

		((AbstractDocument) getDocument()).setDocumentFilter(new SingleModeDocFilter());
        addKeyListener(new KL_Special());
	}

    private class KL_Special implements KeyListener{
        public void keyReleased(KeyEvent e){}
        public void keyTyped(KeyEvent e){}
        public void keyPressed(KeyEvent e){
            if (e.getKeyCode() == KeyEvent.VK_ENTER){
                _tfe.notifyInputText(getText() + "\n");
                if (getText().length() > 0)
                    setText("");
            } else if (e.getKeyCode() == KeyEvent.VK_UP){
                _tfe.notifyInputText(OMUD.ANSI_SEND_ARROW_UP);
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN){
                _tfe.notifyInputText(OMUD.ANSI_SEND_ARROW_DOWN);
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                _tfe.notifyInputText(OMUD.ANSI_SEND_ARROW_RIGHT);
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT){
                _tfe.notifyInputText(OMUD.ANSI_SEND_ARROW_LEFT);
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                _tfe.notifyInputText(OMUD.ANSI_SEND_ESCAPE);
            } else if (e.getKeyCode() == KeyEvent.VK_TAB){
                _tfe.notifyInputText(OMUD.ANSI_SEND_TAB);
            } else if (_single_mode && e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                _tfe.notifyInputText(OMUD.ANSI_SEND_BACKSPACE);
            } else if (_single_mode && e.getKeyCode() == KeyEvent.VK_DELETE){
                _tfe.notifyInputText(OMUD.ANSI_SEND_DELETE);
            // ---------------
            // CTRL+[a-zA-Z]
            // ---------------
            } else if (e.getKeyCode() !=  KeyEvent.VK_CONTROL){
                // check if CTRL is down...
                if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0){
                    // have to do it this way to get the correct key that is pressed with CTRL...
                    int c = e.getKeyText(e.getKeyCode()).charAt(0);
                    // only process CTRL+[a-zA-Z] range chars...
                    if  (c >= 97 && c <= 122 || c >= 65 && c <= 90){ // a-z || A-Z
                        // ANSI control key codes work like this:
                        // CTRL+A == ASCII 0x01 (01, SOH)
                        // CTRL+B == ASCII 0x02 (02, STX)
                        // ...
                        // CTRL+G == ASCII 0x07 (07, BEL)
                        // CTRL+H == ASCII 0x08 (08, BS)
                        // -- Send the appropriate ascii byte to reflect which CTRL+KEY combination we have.

                        // this will offset the position to the correct range for the send...
                        int offset = c >= 97 ? 97 : 65;
                        c = c - offset + 1;

                        // have to convert the char to a byte array for the string to pick it up correctly as binary data...
                        _tfe.notifyInputText(new String(new byte[] {(byte) c}));
                    }
                }
            }
        }
    }

	// SingleModeDocFilter: for grabbing single char input (doesn't detect non-printable chars like enter, up/down, etc)
    private class SingleModeDocFilter extends DocumentFilter {

        // replace(): replace or append...
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            // length check for if text was cleared on mode change..
            if (_single_mode && text.length() > 0)
                _tfe.notifyInputText(text);
            else super.replace(fb, offset, length, text, attrs);
        }

        // insertString(): inserting between char
        //public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        //    super.insertString(fb, offset, text, attr);
        //}
    }

	public void setSingleMode(boolean v){
        // if single mode, clear out the text...
        if ((_single_mode = v))
            setText("");
    }
}