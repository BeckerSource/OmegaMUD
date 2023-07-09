public class OMUD_MMUD_ParseBlockBBSMenu extends OMUD_MMUD_ParseBlocks.ParseBlock{
    private final String MSTR_MUD_MENU = "[1;30m[[0mMAJORMUD[1;30m][0m: [0;37;40m";

    public boolean getStatlineWait() {return false;}
    public static String getCmdText(){return "";}
    public OMUD_MMUD_ParseBlockBBSMenu(){}

    public int findBlockData(OMUD_MMUD_Char mmc, StringBuilder sbTelnetData, int pos_offset){
        return findData(sbTelnetData, sbTelnetData.length() - 1, false, false, MSTR_MUD_MENU, "");
    }
}
