import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;

public class OMUD_GUI{
    private OMUD_GUIFrameChars _fChars = null;

    public OMUD_GUI() {
        _fChars = new OMUD_GUIFrameChars();
        final OMUD_GUIFrameView fView = _fChars.getSelectedChar().getViewFrame();
        final OMUD_GUIFrameInfo fInfo = _fChars.getSelectedChar().getInfoFrame();

        //fView.setLocationRelativeTo(null); // center in main display
        Dimension size = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().getSize();
        _fChars.setSize(_fChars.getMinimumSize());
        fInfo.setLocation((int) (size.getWidth() - fInfo.getSize().getWidth()) - 50, (int) _fChars.getSize().getHeight() + 50);
        fView.setLocation((int) (size.getWidth() - (fView.getSize().getWidth() * 1.5)), fInfo.getLocation().y + 100);
        _fChars.setLocation(fInfo.getLocation().x, 50);
        _fChars.setVisible(true);
        fInfo.setVisible(true);
        fView.setVisible(true);

        // do some post-processing...
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            fView.finalizeGUI();
        }});

        OMUD.logInfo("GUI Created");
    }
}
