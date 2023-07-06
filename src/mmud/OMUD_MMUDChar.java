public class OMUD_MMUDChar{
    public static class ActiveBlock{
        public static final int BPOS_INVALID = -1;
        public int              block_pos =     BPOS_INVALID;
        public String           strCmdText =    "?";
        public boolean          statline_wait = false;
        public StringBuilder    sbDebug =       new StringBuilder();
        public OMUD_MMUD.DataBlock.eBlockType data_type = OMUD_MMUD.DataBlock.eBlockType.ROOM;

        public ActiveBlock(boolean sw, OMUD_MMUD.DataBlock.eBlockType dt){
            statline_wait = sw;
            data_type =     dt;
        }

        public void update(int bp, String ct, boolean sw){
            block_pos =     bp;
            strCmdText =    ct;
            statline_wait = sw;
        }
    }

    public String                   strWelcome =    "";
    public boolean                  got_statline =  false;
    public OMUD_MMUD.DataRoom       dataRoom =      new OMUD_MMUD.DataRoom();
    public OMUD_MMUD.DataExp        dataExp =       new OMUD_MMUD.DataExp();
    public OMUD_MMUD.DataStats      dataStats =     new OMUD_MMUD.DataStats();
    public OMUD_MMUD.DataStatline   dataStatline =  new OMUD_MMUD.DataStatline();
    public OMUD_MMUD.DataInv        dataInv =       new OMUD_MMUD.DataInv();
    public OMUD_MMUD.DataShop       dataShop =      new OMUD_MMUD.DataShop();
    public OMUD_MMUD.DataSpells     dataSpells =    new OMUD_MMUD.DataSpells();
    public OMUD_MMUD.DataWho        dataWho =       new OMUD_MMUD.DataWho();
    public OMUD_MMUD.DataParty      dataParty =     new OMUD_MMUD.DataParty();
    public ActiveBlock ablk = new ActiveBlock(false, OMUD_MMUD.DataBlock.eBlockType.ROOM);
}
