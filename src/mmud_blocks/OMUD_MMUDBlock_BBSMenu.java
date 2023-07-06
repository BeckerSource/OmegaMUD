public class OMUD_MMUDBlock_BBSMenu extends OMUD_MMUDBlocks.Block{
    private final String MSTR_MUD_MENU = "[1;30m[[0mMAJORMUD[1;30m][0m: [0;37;40m";

    public boolean getStatlineWait(){return false;}
    public OMUD_MMUDBlock_BBSMenu(){}

    public int findBlockData(OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        return findData(sbTelnetData, sbTelnetData.length() - 1, false, false, MSTR_MUD_MENU, "");
    }
}
