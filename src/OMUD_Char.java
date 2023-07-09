import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class OMUD_Char implements OMUD_ITextInputEvents, OMUD_ITelnetEvents, OMUD_IMUDEvents{
    public static class ActiveDataBlock{
        public static final int BPOS_INVALID = -1;
        public int              block_pos =     BPOS_INVALID;
        public String           strCmdText =    "?";
        public boolean          statline_wait = false;
        public boolean          refresh_room =  false;
        public StringBuilder    sbDebug =       new StringBuilder();
        public OMUD_MMUD_DataBlock.eBlockType data_type = OMUD_MMUD_DataBlock.eBlockType.ROOM;

        public ActiveDataBlock(boolean sw, OMUD_MMUD_DataBlock.eBlockType dt){
            statline_wait = sw;
            data_type =     dt;
        }

        public void update(int bp, String ct, boolean sw){
            block_pos =     bp;
            strCmdText =    ct;
            statline_wait = sw;
        }
    }

    public class MMUD_Data{
        public String                       strWelcome =    "";
        public boolean                      got_statline =  false;
        public boolean                      is_kai =        false;
        public OMUD_MMUD_DataBlockRoom      dataRoom =      null;
        public OMUD_MMUD_DataBlockExp       dataExp =       null;
        public OMUD_MMUD_DataBlockStats     dataStats =     null;
        public OMUD_MMUD_DataBlockStatline  dataStatline =  null;
        public OMUD_MMUD_DataBlockInv       dataInv =       null;
        public OMUD_MMUD_DataBlockShop      dataShop =      null;
        public OMUD_MMUD_DataBlockSpells    dataSpells =    null;
        public OMUD_MMUD_DataBlockWho       dataWho =       null;
        public OMUD_MMUD_DataBlockParty     dataParty =     null;
        public OMUD_MMUD_DataBlockCombat    dataCombat =    null;
        public ActiveDataBlock              ablk =          null;

        MMUD_Data(){reset();}
        public void reset(){
            strWelcome =    "";
            got_statline =  false;
            is_kai =        false;
            dataRoom =      new OMUD_MMUD_DataBlockRoom();
            dataExp =       new OMUD_MMUD_DataBlockExp();
            dataStats =     new OMUD_MMUD_DataBlockStats();
            dataStatline =  new OMUD_MMUD_DataBlockStatline();
            dataInv =       new OMUD_MMUD_DataBlockInv();
            dataShop =      new OMUD_MMUD_DataBlockShop();
            dataSpells =    new OMUD_MMUD_DataBlockSpells();
            dataWho =       new OMUD_MMUD_DataBlockWho();
            dataParty =     new OMUD_MMUD_DataBlockParty();
            dataCombat =    new OMUD_MMUD_DataBlockCombat();
            ablk =          new ActiveDataBlock(false, OMUD_MMUD_DataBlock.eBlockType.ROOM);
        }
    }

    // -----------
    // OMUD_Char
    // -----------
    private OMUD_Telnet         _omt =      null;
    private OMUD_TelnetParser   _omtp =     null;
    private MMUD_Data           _mmd =      null;
    private OMUD_GUIFrameView   _fView =    null;
    private OMUD_GUIFrameInfo   _fInfo =    null;    
    OMUD_Char(){
        _mmd = new MMUD_Data();

        try{
            _omtp = new OMUD_TelnetParser(this, this, _mmd);
            _omt  = new OMUD_Telnet(this, _omtp);
        } catch (Exception e) {
            OMUD.logError("Error creating core OmegaMUD objects:" + e.getMessage());
        }

        _fView = new OMUD_GUIFrameView(this, _omt, _omtp);
        _fInfo = new OMUD_GUIFrameInfo(this);
    }
    public OMUD_GUIFrameView getViewFrame(){return _fView;}
    public OMUD_GUIFrameInfo getInfoFrame(){return _fInfo;}

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
    public void requestMUDData(final OMUD_MMUD_DataBlock.eBlockType block_type){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            String strCmdText = "";
                 if (block_type == OMUD_MMUD_DataBlock.eBlockType.ROOM)
                strCmdText = OMUD_MMUD_ParseBlockRoom.getCmdText();
            else if (block_type == OMUD_MMUD_DataBlock.eBlockType.INV)
                strCmdText = OMUD_MMUD_ParseBlockInventory.getCmdText();
            else if (block_type == OMUD_MMUD_DataBlock.eBlockType.EXP)
                strCmdText = OMUD_MMUD_ParseBlockExp.getCmdText();
            else if (block_type == OMUD_MMUD_DataBlock.eBlockType.STATS)
                strCmdText = OMUD_MMUD_ParseBlockStats.getCmdText();
            else if (block_type == OMUD_MMUD_DataBlock.eBlockType.SHOP)
                strCmdText = OMUD_MMUD_ParseBlockShop.getCmdText();
            else if (block_type == OMUD_MMUD_DataBlock.eBlockType.SPELLS)
                strCmdText = OMUD_MMUD_ParseBlockSpells.getCmdText(_mmd.is_kai);
            else if (block_type == OMUD_MMUD_DataBlock.eBlockType.WHO)
                strCmdText = OMUD_MMUD_ParseBlockWho.getCmdText();
            if (strCmdText.length() > 0)
                _omt.sendText(strCmdText);
        }});
    }

    public void notifyMUDInit(final String strWelcome){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDInit(strWelcome);
        }});
    }

    public void notifyMUDLocation(final OMUD.eBBSLocation eLoc){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processMUDLocation(eLoc);
        }});
    }

    public void notifyMUDStatline(final OMUD_MMUD_DataBlockStatline dataStatline){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processMUDStatline(dataStatline);
        }});
    }

    public void notifyMUDExp(final OMUD_MMUD_DataBlockExp dataExp){
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

    public void notifyMUDDebug(final String strDebugText){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDDebug(strDebugText);
        }});
    }

    public void notifyMUDRoom(final OMUD_MMUD_DataBlockRoom dataRoom){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fView.processMUDRoom(dataRoom);
            _fInfo.processMUDRoom(dataRoom);
        }});
    }

    public void notifyMUDInv(final OMUD_MMUD_DataBlockInv dataInv){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDInv(dataInv);
        }});
    }

    public void notifyMUDStats(final OMUD_MMUD_DataBlockStats dataStats, final OMUD_MMUD_DataBlockStatline dataStatline){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDStats(dataStats, dataStatline);
        }});
    }

    public void notifyMUDShop(final OMUD_MMUD_DataBlockShop dataShop){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDShop(dataShop);
        }});
    }

    public void notifyMUDSpells(final OMUD_MMUD_DataBlockSpells dataSpells){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDSpells(dataSpells);
        }});
    }

    public void notifyMUDWho(final OMUD_MMUD_DataBlockWho dataWho){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDWho(dataWho);
        }});
    }

    public void notifyMUDCombat(final OMUD_MMUD_DataBlockCombat dataCombat){
        SwingUtilities.invokeLater(new Runnable(){public void run(){
            _fInfo.processMUDCombat(dataCombat);
        }});        
    }

    public void notifyMUDParty(){}    
}
