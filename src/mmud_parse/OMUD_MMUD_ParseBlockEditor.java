public class OMUD_MMUD_ParseBlockEditor extends OMUD_MMUD_ParseBlocks.ParseBlock{

    public boolean getStatlineWait() {return false;}
    public static String getCmdText(){return "";}
    public OMUD_MMUD_ParseBlockEditor(){
        _arrlCmdText.add(new CmdText("train stats", 11));
        _arrlCmdText.add(new CmdText("set suicide", 5));  // only "set s" required
        _arrlCmdText.add(new CmdText("reroll",      6));
        _arrlCmdText.add(new CmdText("x",           1));
    }

    public int findBlockData(OMUD_Char.MMUD_Data mmd, StringBuilder sbTelnetData, int pos_offset){
        return -1;
    }
}
