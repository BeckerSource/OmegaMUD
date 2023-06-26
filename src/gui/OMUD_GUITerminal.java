import java.awt.Point;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;

// OMUD_IBufferMod: buffer modifications that are added to array lists for ordered changes to the terminal JTextPane
interface OMUD_IBufferMod{
    public void render(JTextPane tp, StyledDocument docSwap, ArrayList<OMUD_GUIBlinkText> arrlBlink);
}

public class OMUD_GUITerminal extends JTextPane {
    private OMUD_GUIScrollPane                  _scroll =       null;
    private SimpleAttributeSet                  _attrLocal =    null;
    private StyledDocument                      _docSwap =      null;
    private ArrayList<OMUD_GUIBlinkText>        _arrlBlink =    null;
    private Timer                               _tmrBlink =     null;
    private final int BLINK_DELAY_MS = 500;

    public OMUD_GUITerminal(OMUD_GUIScrollPane scroll){
        _scroll = scroll;
        _attrLocal = new SimpleAttributeSet();
        _docSwap =  new DefaultStyledDocument();
        _tmrBlink = new Timer(BLINK_DELAY_MS, null);
        _arrlBlink = new ArrayList<OMUD_GUIBlinkText>();

        setCaret(new OMUD_GUICaret(OMUD_ANSI.getDefaultFGColor()));
        setEditable(false);
        getCaret().setVisible(true);
        setBackground(OMUD_ANSI.getDefaultBGColor());
        StyleConstants.setForeground(_attrLocal, OMUD.TERMINAL_LOCAL_INFO_FG);  
        StyleConstants.setBackground(_attrLocal, OMUD.TERMINAL_LOCAL_INFO_BG);
        setFont(OMUD.getTerminalFont());

        // blink stuff...
        _tmrBlink.setInitialDelay(0);
        _tmrBlink.addActionListener(new AL_TimerBlink());
        _tmrBlink.start();
    }

    // updateTerminalSize(): get accurate terminal viewing area size based on font and number of rows/cols...
    public void finalizeGUI(){
        int char_width =    getGraphics().getFontMetrics(OMUD.getTerminalFont()).stringWidth(" ");
        int char_height =   getGraphics().getFontMetrics(OMUD.getTerminalFont()).getHeight();
        _scroll.getVerticalScrollBar().setUnitIncrement(char_height);
        //char_width * TERMINAL_COLS;
        //char_height * TERMINAL_ROWS;        
    }

    public void render(final OMUD_Buffer omb, final ArrayList<OMUD_IBufferMod> arrlBMods){
        for (int i = 0; i < arrlBMods.size(); ++i)
            arrlBMods.get(i).render(this, _docSwap, _arrlBlink);
    }

    // --------------
    // Blinking ANSI Text
    // --------------    
    // NOTE: for efficiency, move the blink updates/calcs to when text changes and on scroll...
    private class AL_TimerBlink implements ActionListener {
        private boolean _blinked =  false;
        public void actionPerformed(ActionEvent event){
            try{
                JViewport vp =      _scroll.getViewport();
                Dimension vp_size = vp.getExtentSize();
                Point vp_start =    vp.getViewPosition();
                Point vp_end =      new Point(vp_start.x + vp_size.width, vp_start.y + vp_size.height);
                int start =         viewToModel(vp_start);
                int end =           viewToModel(vp_end);

                //String vp_text = "";
                //try{
                //    vp_text = getStyledDocument().getText(start, end - start);
                //} catch (Exception e){
                //    OMUD.logError("Error getting blink viewport text from styled doc: " + e.getMessage());
                //}

                // loop through blinks - check if within visible bounds...
                for(int i = 0; i < _arrlBlink.size(); i++){
                    if (_arrlBlink.get(i).pos >= start && _arrlBlink.get(i).pos <= end){
                        StyleConstants.setForeground(_arrlBlink.get(i).attr, _blinked ? _arrlBlink.get(i).fg_off : _arrlBlink.get(i).fg_on);
                        getStyledDocument().setCharacterAttributes(_arrlBlink.get(i).pos, _arrlBlink.get(i).length, _arrlBlink.get(i).attr, false); // for efficiency, not sure if this should be replace or not?
                    }
                }
            } catch (Exception e){
                OMUD.logError("Error on blink: " + e.getMessage());
            }
            _blinked = !_blinked;
        }
    }
}