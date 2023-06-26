public class OMUD_MMUDBlock_Exp extends OMUD_MMUDBlocks.Block{
	private final String MSTR_EXP_PRE = 		"[0;37;40m[0;32mExp: [36m";
	private final String MSTR_EXP_END = 		"%]";
	private final String MSTR_LEVEL = 			"Level:";
	private final String MSTR_NEXT_TOTAL_PRE = 	"(";
	private final String MSTR_NEXT_TOTAL_END = 	")";

	public boolean getStatlineWait()				{return true;}
	public OMUD_MMUD.Data.eDataType getDataType()	{return OMUD_MMUD.Data.eDataType.EXP;}
	public OMUD_MMUDBlock_Exp(){
		_arrlCmdText.add(new CmdText("experience", 3));
	}

	public int findBlockData(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_EXP_PRE, MSTR_EXP_END)) > -1){
			cleanData(_sbBlockData, false, true);
			mmc.dataExp = new OMUD_MMUD.DataExp();

			int pos_left =  0;
			int pos_right = 0;
			if ((pos_right = _sbBlockData.indexOf(MSTR_LEVEL, pos_left)) > -1){
				mmc.dataExp.cur_total = Integer.parseInt(_sbBlockData.substring(pos_left, pos_right).trim());

				if ((pos_left  = _sbBlockData.indexOf(MSTR_NEXT_TOTAL_PRE, pos_right)) > -1 &&
					(pos_right = _sbBlockData.indexOf(MSTR_NEXT_TOTAL_END, pos_left))  > -1){
					mmc.dataExp.next_total = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_NEXT_TOTAL_PRE.length(), pos_right).trim());

					if (mmc.dataExp.next_total > mmc.dataExp.cur_total)
						 mmc.dataExp.next_rem = mmc.dataExp.next_total - mmc.dataExp.cur_total;
					else mmc.dataExp.next_rem = 0;
				}
			}
		}

		return pos_data_found_start;
	}
}
