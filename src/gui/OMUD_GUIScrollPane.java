import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.CaretListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

public class OMUD_GUIScrollPane extends JScrollPane{
    // removeCaretListeners():
    // --------------------------------
    // THIS SHIT IS NOT WORKING.  I CAN'T GET THE DAMN AUTO-SCROLL TO STOP
    // --------------------------------
    // Prevent auto-scroll when setting the caret -
    // update policy "never" does not prevent scroll when setting the caret.
    // We need to set the caret as if it were a cursor for accurate ANSI text placement 
    // and don't want the display moving around.
    // https://stackoverflow.com/questions/23608144/jtextpane-set-caret-position-without-changing-scrollpane
    public void removeCaretListeners(JTextPane tp){
        CaretListener[] cl = tp.getCaretListeners();
        for (int i = 0; i < cl.length; ++i)
            tp.removeCaretListener(cl[i]);
        //tp.addCaretListener(cl[i]); // for future reference
    }

    // scrollToBottom(): added to allow moving the scrollbars without moving the cursor -
    // tried a few different methods but none of them works.  Found this on stack overflow
    // https://stackoverflow.com/questions/5147768/scroll-jscrollpane-to-bottom
    public void scrollToBottom(){
        getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
            public void adjustmentValueChanged(AdjustmentEvent e){
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                getVerticalScrollBar().removeAdjustmentListener(this);
        }});        
    }
}