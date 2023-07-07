public class OMUD_MMUD_Char{
    public static class ActiveDataBlock{
        public static final int BPOS_INVALID = -1;
        public int              block_pos =     BPOS_INVALID;
        public String           strCmdText =    "?";
        public boolean          statline_wait = false;
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
    public OMUD_MMUD_DataBlockRoom      dataRoom =      new OMUD_MMUD_DataBlockRoom();
    public OMUD_MMUD_DataBlockExp       dataExp =       new OMUD_MMUD_DataBlockExp();
    public OMUD_MMUD_DataBlockStats     dataStats =     new OMUD_MMUD_DataBlockStats();
    public OMUD_MMUD_DataBlockStatline  dataStatline =  new OMUD_MMUD_DataBlockStatline();
    public OMUD_MMUD_DataBlockInv       dataInv =       new OMUD_MMUD_DataBlockInv();
    public OMUD_MMUD_DataBlockShop      dataShop =      new OMUD_MMUD_DataBlockShop();
    public OMUD_MMUD_DataBlockSpells    dataSpells =    new OMUD_MMUD_DataBlockSpells();
    public OMUD_MMUD_DataBlockWho       dataWho =       new OMUD_MMUD_DataBlockWho();
    public OMUD_MMUD_DataBlockParty     dataParty =     new OMUD_MMUD_DataBlockParty();
    public OMUD_MMUD_DataBlockCombat    dataCombat =    new OMUD_MMUD_DataBlockCombat();
    public ActiveDataBlock ablk = new ActiveDataBlock(false, OMUD_MMUD_DataBlock.eBlockType.ROOM);
}
