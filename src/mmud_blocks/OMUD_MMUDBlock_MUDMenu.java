public class OMUD_MMUDBlock_MUDMenu extends OMUD_MMUDBlocks.Block{
	private final String MSTR_MUD_MENU = "[1;30m[[0mMAJORMUD[1;30m][0m: [0;37;40m";

	public OMUD_MMUDBlock_MUDMenu(){}

	public void updateActiveBlock(int pos_block, String strFoundCmdFull, OMUD_MMUDChar.ActiveBlock ablk){
		ablk.update(pos_block, strFoundCmdFull, false);
	}

	public int findBlockData(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		return findData(sbTelnetData, sbTelnetData.length() - 1, false, false, MSTR_MUD_MENU, "");
	}
}
