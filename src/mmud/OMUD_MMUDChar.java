import java.util.ArrayList;

public class OMUD_MMUDChar{
	public static class ActiveBlock{
		public static final int BPOS_INVALID = -1;
		public int 		block_pos = 	BPOS_INVALID;
		public String 	strCmdText = 	"?";
		public boolean 	statline_wait = false;
		public OMUD_MMUD.Data.eDataType data_type = OMUD_MMUD.Data.eDataType.ROOM;

		public ActiveBlock(boolean sw, OMUD_MMUD.Data.eDataType dt){
			statline_wait = sw;
			data_type = 	dt;
		}

		public void update(int bp, String ct, boolean sw, OMUD_MMUD.Data.eDataType dt){
			block_pos =  	bp;
			strCmdText = 	ct;
			statline_wait = sw;
			data_type = 	dt == OMUD_MMUD.Data.eDataType.NONE ? data_type : dt; // don't update if the data type is NONE
		}
	}

	public OMUD_MMUD.DataRoom 				dataRoom = 		new OMUD_MMUD.DataRoom();
	public OMUD_MMUD.DataExp 				dataExp = 		new OMUD_MMUD.DataExp();
	public OMUD_MMUD.DataStats 				dataStats = 	new OMUD_MMUD.DataStats();
	public OMUD_MMUD.DataStatline 			dataStatline = 	new OMUD_MMUD.DataStatline();
	public OMUD_MMUD.DataInv 				dataInv = 		new OMUD_MMUD.DataInv();
	public OMUD_MMUD.DataParty 				dataParty = 	new OMUD_MMUD.DataParty();
	public ArrayList<OMUD_MMUD.DataSpell> 	dataSpells = 	new ArrayList<OMUD_MMUD.DataSpell>();

	public ActiveBlock ablk = new ActiveBlock(false, OMUD_MMUD.Data.eDataType.ROOM);
}
