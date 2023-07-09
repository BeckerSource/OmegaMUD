import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class OMUD_GUIFrameChars extends JFrame{
    private OMUD_Char _omc =        null;
    private JTable _tblFakeChars =  null;
    private static final int        FRAME_MIN_WIDTH  =  450;
    private static final int        FRAME_MIN_HEIGHT =  150;
    private static final String[]   TABLE_COL_STRINGS = {"Realm", "Name"};

    OMUD_GUIFrameChars(){
        setMinimumSize(new Dimension(FRAME_MIN_WIDTH, FRAME_MIN_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("OmegaMUD v0 (Char Status/Editor)");

        _omc = new OMUD_Char();

        Object[][] objFakeChars = {
            {"BearsBBS_Normal", "Gandalf LastName"},
            {"BearsBBS_Edited", "Aragorn LastName"},
            {"BearsBBS_MudRev", "Legolas LastName"},
            {"ClassicMUD",      "Gimli LastName"},
            {"DarkwoodBBS",     "Syntax MudInfo"}};
        _tblFakeChars = new JTable(objFakeChars, TABLE_COL_STRINGS);
        add(new JScrollPane(_tblFakeChars)); // table must go inside a scroll pane to show column headers properly
        pack();
    }

    public OMUD_Char getSelectedChar(){return _omc;}
}