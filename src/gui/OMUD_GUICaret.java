import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Color;
import javax.swing.text.DefaultCaret;
import javax.swing.text.BadLocationException;
import javax.swing.Timer;

// OMUD_GUICaret: based on:
// https://stackoverflow.com/questions/24026774/how-to-change-the-caret-in-a-textfield
public class OMUD_GUICaret extends DefaultCaret {
    private Timer   _tmrBlink =     null;
    private Color   _clrFill =      null;
    private Color   _clrBorder =    null;
    private boolean _blinked =      false;
    private static final int BLINK_MS = 500;

    public OMUD_GUICaret(Color clr){
        _clrFill = clr;
        _clrBorder = new Color(255 - _clrFill.getRed(), 255 - _clrFill.getGreen(), 255 - _clrFill.getBlue()); // inverted

        // doing manual blinking with a timer because the caret stops blinking when the terminal loses focus...
        _tmrBlink = new Timer(BLINK_MS, new ActionListener(){public void actionPerformed(ActionEvent e){
            _blinked = !_blinked;
            repaint();
        }});
        _tmrBlink.setInitialDelay(BLINK_MS);
        _tmrBlink.setRepeats(true);
        _tmrBlink.start();

        setUpdatePolicy(DefaultCaret.NEVER_UPDATE); // doesn't seem to do jack shit
    }

    protected synchronized void damage(Rectangle r) {
        if (r == null)
            return;

        FontMetrics fm =    getComponent().getFontMetrics(getComponent().getFont());
        int char_width =    fm.stringWidth(" ");
        int char_height =   fm.getHeight();
        x = r.x;
        y = r.y;
        width =     char_width;
        height =    char_height;
        repaint(); // calls getComponent().repaint(x, y, width, height)
    }

    public void paint(Graphics g) {
        if (getComponent() == null)
            return;

        Rectangle r = null;
        try {
            if ((r = getComponent().modelToView(getDot())) == null)
                return;
        } catch (BadLocationException e) {
            return;
        }

        if ((x != r.x) || (y != r.y)) {
            repaint(); // erase previous location of caret
            damage(r);
        }

        // draw fill if not blinked and then draw the border...
        if (!_blinked){
            g.setColor(_blinked ? _clrBorder : _clrFill);
            g.fillRect(x, y, width - 1, height - 1);
        }
        g.setColor(_clrBorder);
        g.drawRect(x, y, width - 1, height - 1);
    }
}