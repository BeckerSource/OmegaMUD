public class OMUD_MMUD_Char{
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

    OMUD_MMUD_Char(){reset();}
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
