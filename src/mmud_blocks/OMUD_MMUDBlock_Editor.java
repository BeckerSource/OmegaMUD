public class OMUD_MMUDBlock_Editor extends OMUD_MMUDBlocks.Block{
	public OMUD_MMUDBlock_Editor(){
		_arrlCmdText.add(new CmdText("train stats", 11));
		_arrlCmdText.add(new CmdText("set suicide", 5));  // only "set s" required
		_arrlCmdText.add(new CmdText("reroll", 		6));
	}

	public void updateActiveBlock(int pos_block, String strFoundCmdFull, OMUD_MMUDChar.ActiveBlock ablk){
		ablk.update(pos_block, strFoundCmdFull, false);
	}

	public int findBlockData(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;
		return pos_data_found_start;
	}
}
