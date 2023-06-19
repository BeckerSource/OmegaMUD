public class OMUD_MMUDBlock_Statline extends OMUD_MMUDBlocks.Block{
	private final String MSTR_STATLINE_PRE = 		"[79D[K[0;37m[";
	private final String MSTR_STATLINE_END = 		"]:";
	private final String MSTR_STATLINE_RESTING = 	" (Resting) ";
	private final String MSTR_STATLINE_MEDITATING = " (Meditating) ";

	private OMUD_MMUD.DataStatline _dataStatline = null;

	public OMUD_MMUDBlock_Statline(){
		_dataStatline = new OMUD_MMUD.DataStatline();
	}

	public int findBlockData(OMUD_IMUDEvents ommme, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		if ((pos_data_found_start = findData(sbTelnetData, sbTelnetData.length() - 1, false, true, MSTR_STATLINE_PRE, MSTR_STATLINE_END)) > -1){
			// default to active first...
			_dataStatline.rest = OMUD_MMUD.eRestState.ACTIVE;

			// ------------------
			// Statline: Resting/Meditation: MA/KAI Chars
			// ------------------
			// MA/KAI chars: resting/meditation strings are a suffix after the statline prompt outer brackets.
			if (pos_data_found_start < sbTelnetData.length()){
				int pos_rest_start = 0;
				if ((pos_rest_start = sbTelnetData.indexOf(MSTR_STATLINE_MEDITATING, pos_data_found_start)) > -1){
					sbTelnetData.delete(pos_rest_start, pos_rest_start + MSTR_STATLINE_MEDITATING.length());
					_dataStatline.rest = OMUD_MMUD.eRestState.MEDITATING;
				} else if ((pos_rest_start = sbTelnetData.indexOf(MSTR_STATLINE_RESTING, pos_data_found_start)) > -1){
					sbTelnetData.delete(pos_rest_start, pos_rest_start + MSTR_STATLINE_RESTING.length());
					_dataStatline.rest = OMUD_MMUD.eRestState.RESTING;
				}
			}

			// ------------------
			// Statline: Resting/Meditation: Non-MA/KAI Chars
			// ------------------
			// Non-MA/KAI chars: if not found above, try inside of the statline...
			if (_dataStatline.rest == OMUD_MMUD.eRestState.ACTIVE){
				int pos_rest_start = 0;
				if ((pos_rest_start = _sbBlockData.indexOf(MSTR_STATLINE_RESTING, 0)) > -1){
					_sbBlockData.delete(pos_rest_start, pos_rest_start + MSTR_STATLINE_RESTING.length());
					_dataStatline.rest = OMUD_MMUD.eRestState.RESTING;
				// not sure if non-MA/KAI chars would ever use meditate?
				} else if ((pos_rest_start = _sbBlockData.indexOf(MSTR_STATLINE_MEDITATING, 0)) > -1){
					_sbBlockData.delete(pos_rest_start, pos_rest_start + MSTR_STATLINE_MEDITATING.length());
					_dataStatline.rest = OMUD_MMUD.eRestState.MEDITATING;
				}
			}

			cleanData(false, true); // strip ansi
			_dataStatline.text = _sbBlockData.toString();
			ommme.notifyMUDStatline(new OMUD_MMUD.DataStatline(_dataStatline));

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

	public void resetData(){}
	public void notifyEvents(OMUD_IMUDEvents ommme){}
	public boolean waitForStatline(){return false;}
}