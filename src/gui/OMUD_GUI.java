import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import java.util.ArrayList;

public class OMUD_GUI implements OMUD_ITextInputEvents, OMUD_ITelnetEvents, OMUD_IMUDEvents{
    private OMUD_Telnet         _omt =    null;
    private OMUD_TelnetParser   _omtp =   null;
    private OMUD_GUIFrameChars  _fChars = null;
    private OMUD_GUIFrameView   _fView =  null;
    private OMUD_GUIFrameInfo   _fInfo =  null;

    public OMUD_GUI() {

        // --------------
        // Telnet
        // --------------
        try{
            _omtp = new OMUD_TelnetParser(this, this);
            _omt  = new OMUD_Telnet(this, _omtp);
        } catch (Exception e) {
            OMUD.logError("Error creating core OmegaMUD objects:" + e.getMessage());
        }                

        // --------------
        // GUI
        // --------------
        _fChars =   new OMUD_GUIFrameChars();
        _fView =    new OMUD_GUIFrameView(this, _omt, _omtp);
        _fInfo =    new OMUD_GUIFrameInfo();

        //_fView.setLocationRelativeTo(null); // center in main display
        Dimension size = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().getSize();
        _fChars.setSize(_fChars.getMinimumSize());      
        _fInfo.setLocation((int) (size.getWidth() - _fInfo.getSize().getWidth()) - 50, (int) _fChars.getSize().getHeight() + 50);
        _fView.setLocation((int) (size.getWidth() - (_fView.getSize().getWidth() * 1.5)), _fInfo.getLocation().y + 100);
        _fChars.setLocation(_fInfo.getLocation().x, 50);
        _fChars.setVisible(true);
        _fInfo.setVisible(true);
        _fView.setVisible(true);

        // do some post-processing...
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.finalizeGUI();
        }});

        OMUD.logInfo("GUI Created");
    }

    // --------------
    // GUI Events
    // --------------
    public void notifyInputText(String text){
        _omt.sendText(text);
    }

    // --------------
    // Telnet Events
    // --------------
    public void notifyTelnetConnected(){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processTelnetConnected();
        }});
    }

    public void notifyTelnetDisconnected(){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processTelnetDisconnected();
        }});
    }

    public void notifyTelnetParsed(final OMUD_Buffer omb, final ArrayList<OMUD_IBufferMod> arrlBMods){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processTelnetParsed(omb, arrlBMods);
            _fInfo.processTelnetParsed(omb);
            _omtp.setGUIReady();
        }});
    }

    // --------------
    // MUD Events
    // --------------
    public void notifyMUDAutoCmd(final String strCmd){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processMUDAutoCmd(strCmd);
        }});
    }

    public void notifyMUDLocation(final OMUD.eBBSLocation eLoc){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processMUDLocation(eLoc);
        }});
    }
    
    public void notifyMUDStatline(final OMUD_MMUD.DataStatline dataStatline){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processMUDStatline(dataStatline);
        }});
    }
    
    public void notifyMUDExp(final OMUD_MMUD.DataExp dataExp){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processMUDExp(dataExp);
        }});
    }
    
    public void notifyMUDUserCmd(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processMUDUserCmd(strText);
            _fInfo.processMUDUserCmd(strText);
        }});
    }    
    
    public void notifyMUDUnknown(final String strText){        
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            OMUD.logToFile(OMUD.LOG_FILENAME_UNKNOWN, strText);
        }});
    }

    public void notifyMUDOther(final String strText){        
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDOther(strText);
        }});
    }
    
    public void notifyMUDWelcome(final String strText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDWelcome(strText);
        }});
    }
    
    public void notifyMUDRoom(final OMUD_MMUD.DataRoom dataRoom){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processMUDRoom(dataRoom);
            _fInfo.processMUDRoom(dataRoom);
        }});
    }
    
    public void notifyMUDInv(final OMUD_MMUD.DataInv dataInv){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDInv(dataInv);
        }});
    }
    
    public void notifyMUDStats(final OMUD_MMUD.DataStats dataStats){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDStats(dataStats);
        }});
    }
    
    public void notifyMUDShop(final OMUD_MMUD.DataShop dataShop, final String strRoomID, final String strRoomName){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDShop(dataShop, strRoomID, strRoomName);
        }});
    }
    
    public void notifyMUDSpells(final OMUD_MMUD.DataSpells dataSpells){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDSpells(dataSpells);
        }});        
    }

    public void notifyMUDParty(){}
    public void notifyMUDCombat(){}
}
