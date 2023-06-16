public class OMUD_MMUDCmd_MUDMenu extends OMUD_MMUDCmds.Cmd{
	private final String MSTR_MUD_MENU = "[1;30m[[0mMAJORMUD[1;30m][0m: [0;37;40m";

	public boolean allowCmds(){return true;}
	public OMUD_MMUDCmd_MUDMenu(){}

	public int findCmdData(OMUD_IMUDEvents ommme, StringBuilder sbTelnetData, int pos_offset){
		return findData(sbTelnetData, sbTelnetData.length() - 1, false, false, MSTR_MUD_MENU, "");
	}
}
