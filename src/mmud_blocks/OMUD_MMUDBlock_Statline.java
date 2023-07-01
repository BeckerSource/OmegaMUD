public class OMUD_MMUDBlock_Statline extends OMUD_MMUDBlocks.Block{
	private final String MSTR_PREFIX_RESET_WHBL =  	"[0;37;40m";
	private final String MSTR_STATLINE_PRE = 		"[79D[K[0;37m[";
	private final String MSTR_STATLINE_END = 		"]:";
	private final String MSTR_STATLINE_REST = 		" (Resting) ";
	private final String MSTR_STATLINE_MED =	 	" (Meditating) ";

	public boolean getStatlineWait()				{return false;}
	public OMUD_MMUD.DataBlock.eBlockType getDataType()	{return OMUD_MMUD.DataBlock.eBlockType.NONE;}
	public OMUD_MMUDBlock_Statline(){}

	public int findBlockData(OMUD_IMUDEvents omme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		if ((pos_data_found_start = findData(sbTelnetData, sbTelnetData.length() - 1, false, true, MSTR_STATLINE_PRE, MSTR_STATLINE_END)) > -1){

			// PREFIX: statline after stats has an ANSI reset prefix...
			pos_data_found_start = checkPrefix("Statline Stats Prefix", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);

			// default to active first...
			mmc.dataStatline.rest = OMUD_MMUD.eRestState.READY;

			// ------------------
			// Statline: Resting/Meditation: MA/KAI Chars
			// ------------------
			// MA/KAI chars: resting/meditation strings are a suffix after the statline prompt outer brackets.
			if (pos_data_found_start < sbTelnetData.length()){
				int pos_rest_start = 0;
				if ((pos_rest_start = sbTelnetData.indexOf(MSTR_STATLINE_MED, pos_data_found_start)) > -1){
					sbTelnetData.delete(pos_rest_start, pos_rest_start + MSTR_STATLINE_MED.length());
					mmc.dataStatline.rest = OMUD_MMUD.eRestState.MED;
				} else if ((pos_rest_start = sbTelnetData.indexOf(MSTR_STATLINE_REST, pos_data_found_start)) > -1){
					sbTelnetData.delete(pos_rest_start, pos_rest_start + MSTR_STATLINE_REST.length());
					mmc.dataStatline.rest = OMUD_MMUD.eRestState.REST;
				}
			}

			// ------------------
			// Statline: Resting/Meditation: Non-MA/KAI Chars
			// ------------------
			// Non-MA/KAI chars: if not found above, try inside of the statline...
			if (mmc.dataStatline.rest == OMUD_MMUD.eRestState.READY){
				int pos_rest_start = 0;
				if ((pos_rest_start = _sbBlockData.indexOf(MSTR_STATLINE_REST, 0)) > -1){
					_sbBlockData.delete(pos_rest_start, pos_rest_start + MSTR_STATLINE_REST.length());
					mmc.dataStatline.rest = OMUD_MMUD.eRestState.REST;
				// not sure if non-MA/KAI chars would ever use meditate?
				} else if ((pos_rest_start = _sbBlockData.indexOf(MSTR_STATLINE_MED, 0)) > -1){
					_sbBlockData.delete(pos_rest_start, pos_rest_start + MSTR_STATLINE_MED.length());
					mmc.dataStatline.rest = OMUD_MMUD.eRestState.MED;
				}
			}

			// strip ansi...
			cleanData(_sbBlockData, false, true);

			// check for Mana/Kai...
			int pos_equals  = 0;
			int pos_hp_end  = 0;
			if ((pos_hp_end = _sbBlockData.indexOf("/", pos_equals)) > -1 &&
				(pos_equals = _sbBlockData.indexOf("=", pos_hp_end)) > -1){
				if (mmc.dataStatline.ma_str.length() == 0)
					mmc.dataStatline.ma_str = _sbBlockData.substring(pos_hp_end + 1, pos_equals);
				mmc.dataStatline.ma_cur = Integer.parseInt(_sbBlockData.substring(++pos_equals, _sbBlockData.length()));
			} else pos_hp_end = _sbBlockData.length();
			if ((pos_equals = _sbBlockData.indexOf("=", 0)) > -1){
				if (mmc.dataStatline.hp_str.length() == 0)
					mmc.dataStatline.hp_str = _sbBlockData.substring(0, pos_equals);
				mmc.dataStatline.hp_cur = Integer.parseInt(_sbBlockData.substring(pos_equals + 1, pos_hp_end));				
			}

			// ------------------
			// Statline: Remove Extra
			// ------------------
			// check for extra (previous) statlines:
			// if multiple statlines were sent, 
			// delete all previous and keep the most recent from above.
			if (sbTelnetData.length() > 0){
				int pos_multi_statline = -1;
				do {
					if ((pos_multi_statline = findData(sbTelnetData, sbTelnetData.length() - 1, false, true, MSTR_STATLINE_PRE, MSTR_STATLINE_END)) > -1)
						pos_data_found_start = pos_multi_statline;
				} while (pos_multi_statline > -1);
			}
		}

		return pos_data_found_start;
	}	
}