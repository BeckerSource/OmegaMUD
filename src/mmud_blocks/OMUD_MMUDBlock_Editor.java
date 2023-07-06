public class OMUD_MMUDBlock_Editor extends OMUD_MMUDBlocks.Block{

    public boolean getStatlineWait(){return false;}
    public OMUD_MMUDBlock_Editor(){
        _arrlCmdText.add(new CmdText("train stats", 11));
        _arrlCmdText.add(new CmdText("set suicide", 5));  // only "set s" required
        _arrlCmdText.add(new CmdText("reroll",      6));
        _arrlCmdText.add(new CmdText("x",           1));
    }

    public int findBlockData(OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        return -1;
    }
}
