import java.util.ArrayList;

public class OMUD_MMUDBlocks{
	// ------------------
	// ActiveBlock
	// ------------------	
	public static class ActiveBlock{
		public static final int BPOS_INVALID = 	-1;
		public int 		block_pos = 		BPOS_INVALID;
		public boolean 	wait_for_statline = true;
		public String 	strCmdText = 		"?";

		public void update(int bp, boolean wfs, String ct){
			block_pos =  bp;
			wait_for_statline = wfs;
			strCmdText = ct;
		}
	}

	// ------------------
	// Block
	// ------------------	
	public static abstract class Block{
		protected class CmdText{
			public String 	text  = 	"";
			public int 		min_len = 	0;
			public CmdText(String t, int ml){text = t; min_len = ml;}
		}

		protected ArrayList<CmdText> _arrlCmdText = new ArrayList<CmdText>();
		protected StringBuilder 	 _sbBlockData = new StringBuilder();

		public abstract void resetData();
		public abstract void notifyEvents(OMUD_IMUDEvents ommme);
		public abstract boolean waitForStatline();

		public String matchCmdText(String strCmd){
			String strFoundCmdFull = null;

			int i = 0;
			boolean found = false;
			for (; i < _arrlCmdText.size() && !found; ++i){
				// initial check to meet min length requirement of the command...
				found = strCmd.length() >= _arrlCmdText.get(i).min_len && strCmd.length() <= _arrlCmdText.get(i).text.length();
				// match characters if valid (assumes all lower case)...
				for (int j = 0; j < strCmd.length() && found; ++j)
					found = strCmd.charAt(j) == _arrlCmdText.get(i).text.charAt(j);
			}

			if (found)
				strFoundCmdFull = _arrlCmdText.get(--i).text;
			return strFoundCmdFull;
		}

		public abstract int findBlockData(OMUD_IMUDEvents ommme, StringBuilder sbTelnetData, int pos_offset);
		protected int findData(StringBuilder sbTelnetData, int pos_offset, boolean offset_is_lf, boolean has_dynamic_text, String strStartSeq, String strEndSeq){
			int pos_endseq_left =  	 -1;
			int pos_endseq_right = 	 -1;
			int pos_startseq_left =  -1;
			int pos_startseq_right = -1;
			boolean valid_range = 	 sbTelnetData.length() >= strStartSeq.length() + strEndSeq.length();

			// don't bother unless we have enough overall space...
			if (valid_range){

				// get end sequence range (also checks/calcs against LF)...
				if ((pos_endseq_left = pos_offset - (offset_is_lf ? 1 : 0)) >= 0){
					if (strEndSeq.length() > 0){
						pos_endseq_left = sbTelnetData.lastIndexOf(strEndSeq, pos_endseq_left);
						pos_endseq_right = pos_endseq_left + strEndSeq.length();
						valid_range = pos_endseq_right - pos_endseq_left == strEndSeq.length();
						pos_endseq_right--; // -1 for zero-based pos/index
					} else {
						pos_endseq_right = pos_endseq_left;
						valid_range = true;
					}				
				} else valid_range = false;

				// check range and get start sequence left/starte...
				if (valid_range && 
					(pos_startseq_left = sbTelnetData.lastIndexOf(strStartSeq, pos_endseq_left)) > -1){

					// check ranges based on if has dynamic text...
					pos_startseq_right = pos_startseq_left + strStartSeq.length() - 1; // -1 for zero-based pos/index
					if (has_dynamic_text){
						pos_startseq_right++; // move to the dynamic text starting position
						valid_range = pos_startseq_right <  pos_endseq_left;
					} else {
						valid_range = pos_startseq_right == pos_endseq_left;
					}

					if (valid_range){
						_sbBlockData.setLength(0);
						if (pos_startseq_right < pos_endseq_left){
							// adjust the endseq left if for a LF with no end seq text...
							if (offset_is_lf && strEndSeq.length() == 0)
								pos_endseq_left++;
							_sbBlockData.append(sbTelnetData.substring(pos_startseq_right, pos_endseq_left));
						}
						// delete the data in the buffer...
						sbTelnetData.delete(pos_startseq_left, pos_endseq_right + (offset_is_lf ? 1 : 0) + 1); // +1 for exclusive
					} else pos_startseq_left = -1; 
				}
			}
			return pos_startseq_left;
		}

		// cleanData(): trim lf/space and strip ansi as requested...
		protected void cleanData(boolean trim_lf_spc, boolean strip_ansi){
			int pos_first_char = -1;
			int pos_last_char  = -1;
			for (int i = 0; i < _sbBlockData.length(); ++i){
				if (strip_ansi && _sbBlockData.charAt(i) == OMUD.ASCII_ESC){
					int pos_ansi_end = _sbBlockData.indexOf(OMUD.CSI_GRAPHICS_STR, i);
					// if matching ansi end is not found, just delete the escape char.
					if (pos_ansi_end == -1)
						pos_ansi_end = i + 1; 	// +1 for exclusive end
					else pos_ansi_end++; 		// 
					_sbBlockData.delete(i--, pos_ansi_end); // move 'i' back after delete so that we pick up the first char after the delete

				} else if (trim_lf_spc){
					if (_sbBlockData.charAt(i) == OMUD.ASCII_LF){
						_sbBlockData.setCharAt(i, OMUD.ASCII_SPC);
					} else if (_sbBlockData.charAt(i) != OMUD.ASCII_SPC){
						pos_last_char = i;
						if (pos_first_char == -1)
							pos_first_char = i;
					}				
				}
			}

			// trim: delete end first...
			if (trim_lf_spc){
				if (pos_last_char > pos_first_char && pos_last_char < _sbBlockData.length() - 1)
					_sbBlockData.delete(pos_last_char + 1, _sbBlockData.length()); // +1 to move forward to begin at space after last char
				if (pos_first_char > 0)
					_sbBlockData.delete(0, pos_first_char);			
			}
		}

		// checkPrefix(): some mud strings have a possible prefix - check for it and remove it -
		// NOTE: strReasonDbg is just for internal visual/coding reference.  Not a string required for function.
		protected int checkPrefix(String strReasonDbg, StringBuilder sbTelnetData, int pos_offset, String strPrefix){
			int prefix_len = strPrefix.length();
			int pos_start = pos_offset - prefix_len;
			if (pos_start >= 0 && sbTelnetData.substring(pos_start, pos_start + prefix_len).equals(strPrefix)){
				sbTelnetData.delete(pos_start, pos_start + prefix_len);
				pos_offset = pos_start;
			}
			return pos_offset;
		}

		protected void splitCommaListToArray(String strItems, ArrayList<String> arrlItems){
	        String[] items = strItems.split(",");
	        for (String item : items)
	        	arrlItems.add(item.trim());
		}		
	}

	// ------------------
	// OMUD_MMUDBlocks
	// ------------------
	private ArrayList<Block> 		_arrlBlocks = 		null;
	private final int 				BPOS_MUD_MENU = 	0;
	private final int 				BPOS_STATLINE = 	1;
	private final int  				BPOS_CMDS_START =  	2;
	private final int 				BPOS_CMD_LOOKROOM = 2;
	private final int  				BPOS_CMDS_STOP =  	3;
	private final int 				BPOS_OTHER = 		3;

	public OMUD_MMUDBlocks(){
		_arrlBlocks = new ArrayList<Block>();
		_arrlBlocks.add(new OMUD_MMUDBlock_MUDMenu());	// BPOS_MUD_MENU
		_arrlBlocks.add(new OMUD_MMUDBlock_Statline()); // BPOS_STATLINE
		_arrlBlocks.add(new OMUD_MMUDBlock_LookRoom()); // BPOS_CMD_LOOKROOM
		_arrlBlocks.add(new OMUD_MMUDBlock_Other()); 	// BPOS_OTHER
	}

	// resetData(): reset internal data for some special commands
	public void resetData(){
		_arrlBlocks.get(BPOS_CMD_LOOKROOM).resetData();
	}

	public int parseLineBlocks(ActiveBlock ablk, OMUD_IMUDEvents ommme, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		// if current block is the statline or mud menu, just parse all the line blocks...
		if (ablk.block_pos < BPOS_CMDS_START){
			for (int i = BPOS_CMDS_START; i < _arrlBlocks.size() && pos_data_found_start == -1; ++i)
				pos_data_found_start = _arrlBlocks.get(i).findBlockData(ommme, sbTelnetData, pos_offset);
		// else parse only the current line block...
		} else {
			pos_data_found_start = _arrlBlocks.get(ablk.block_pos).findBlockData(ommme, sbTelnetData, pos_offset);			
		}

		return pos_data_found_start;
	}

	public int parseStatline(ActiveBlock ablk, OMUD_IMUDEvents ommme, StringBuilder sbTelnetData){
		int pos_data_found_start = _arrlBlocks.get(BPOS_STATLINE).findBlockData(ommme, sbTelnetData, 0);
		if (pos_data_found_start > -1){
			ablk.update(BPOS_STATLINE, _arrlBlocks.get(BPOS_STATLINE).waitForStatline(), "");
			_arrlBlocks.get(BPOS_CMD_LOOKROOM).notifyEvents(ommme);
		}
		return pos_data_found_start;
	}

	public int parseMUDMenu(ActiveBlock ablk, OMUD_IMUDEvents ommme, StringBuilder sbTelnetData){
		int pos_data_found_start = _arrlBlocks.get(BPOS_MUD_MENU).findBlockData(ommme, sbTelnetData, 0);
		if (pos_data_found_start > -1)
			ablk.update(BPOS_MUD_MENU, _arrlBlocks.get(BPOS_MUD_MENU).waitForStatline(), "");
		return pos_data_found_start;
	}

	// findCmd(): main external call to match a user-input command
	// NOTE: assumes passed in as lower-case...
	public void findCmd(ActiveBlock ablk, String strCmd){
		String strFoundCmdFull = null;
		for (int i = BPOS_CMDS_START; i < BPOS_CMDS_STOP && ablk.block_pos == ActiveBlock.BPOS_INVALID; ++i)
			if ((strFoundCmdFull = _arrlBlocks.get(i).matchCmdText(strCmd)) != null)
				ablk.update(i, _arrlBlocks.get(i).waitForStatline(), strFoundCmdFull);
	}	
}



	/* INV_CARRY_PRE */
	/* INV_KEYS */
	/* INV_WEALTH */ 
	/* INV_ENC */
	/* WEALTH */
	/* BANKS */
	/* LOOK_DESC */ 
	/* LOOK_EQUIPPED */
	/* LOOK_ITEM */
	/* EXP */ 
	/* STAT_L1 */ 
	/* STAT_L2 */ 
	/* STAT_L3 */ 
	/* STAT_L4 */ 
	/* STAT_L5 */ 
	/* STAT_L6 */
	/* STAT_L7 */
	/* STAT_L8 */ 
	/* PARTY_NONE */ 
	/* PARTY_CHAR */
	/* TRAIN */ 

	/*
	public enum eCmdList{
		CMD_LOOK_ROOM,
		CMD_LOOK_UNIT,
		CMD_MOVE,
		CMD_INVENTORY,
		CMD_STATS,
		CMD_PARTY,
		CMD_REST,
		CMD_MEDITATE,
		CMD_TRAIN,
		CMD_TRAIN_STATS,
		CMD_LIST_STOCK,
	}
	*/



/* SLINE_HP */ 				//"[79D[K[0;37m[HP=56[0;37m]:",
/* SLINE_HP_MA */ 			//"[79D[K[0;37m[HP=26[0;37m/MA=12[0;37m]:",
/* SLINE_HP_KAI */ 			//"[79D[K[0;37m[HP=57[0;37m/KAI=1[0;37m]:",
/* SLINE_HP_RESTING */ 		//"[79D[K[0;37m[HP=56[0;37m (Resting) ]:",
/* SLINE_HP_MA_RESTING */ 	//"[79D[K[0;37m[HP=26[0;37m/MA=12[0;37m]: (Resting)",
/* SLINE_HP_KAI_RESTING */ 	//"[79D[K[0;37m[HP=57[0;37m/KAI=1[0;37m]: (Resting)",
/*
--------------
Important ANSI Lines
--------------
> also here (monsters 1):
[0;35mAlso here: [1;35mbig baby green dragon[0m[0;35m.
> also here (players):
[0;35mAlso here: [1;35mCosma, ZedsDead, BobLemon[0;35m.
> room exits:
[0;32mObvious exits: nCorth, eJast, wTest, dNown
> generic text (player enter realm, city PR spam, etc):
[79D[KTrelic just entered the Realm.
[79D[KA cheer of many voices can be heard in the distance.
[79D[KChildren rush past you hopping around in youthful glee.
[79D[KA voice shouts aloud "Read the bulletin in the Adventurer's Guild!"
[79D[KA dog barks off in the distance.
> hear movement:
[79D[K[0;35mYou hear movement to the sBouth.
> player name prefixed action lines:
[79D[K[1;31mPoopship[0;32m walks into the room from the sBouth.
> player hung up:
[79D[K[1;37mBaseNote just hung up!!!
> room description (has ansi + 4 spaces prefix, ends when next ANSI ESC sequence hits):
[79D[K[0;37;40m    
> resting (no ansi):
You are now resting.
> gossips:
[0;37;40m[79D[KTestChar gossips: [0;35mwell boys
[0;37;40m[79D[KTestChar gossips: [0;35mand ladies
> player looking at someone:
[79D[K[0;36mSolace is looking at you.
> Bank Rates
[79D[K[0;36m
The currency conversion rates are:
100 platinum pieces == 1 Adamantite Pieces
100 gold crowns == 1 platinum piece
10 silver nobles == 1 gold crown
10 copper farthings == 1 silver noble
> searching items:
[0;37;40m[0;36mYou notice vorpal sword here.
[0;36mYour search revealed nothing.
> searching directions:
[79D[KYou notice nothing different to the eLast.
> dimly lit:
[0;37mThe room is dimly lit
> unit (mob) moves into the room:
[79D[K[1;33mtownsman[0;32m moves into the room from the eSast.
*/