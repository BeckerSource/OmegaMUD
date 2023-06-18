public class OMUD_MMUDBlock_MUDMenu extends OMUD_MMUDBlocks.Block{
	private final String MSTR_MUD_MENU = "[1;30m[[0mMAJORMUD[1;30m][0m: [0;37;40m";

	public OMUD_MMUDBlock_MUDMenu(){}

	public int findBlockData(OMUD_IMUDEvents ommme, StringBuilder sbTelnetData, int pos_offset){
		return findData(sbTelnetData, sbTelnetData.length() - 1, false, false, MSTR_MUD_MENU, "");
	}

	public void resetData(){}
	public void notifyEvents(OMUD_IMUDEvents ommme){}
	public boolean waitForStatline(){return false;}
}
