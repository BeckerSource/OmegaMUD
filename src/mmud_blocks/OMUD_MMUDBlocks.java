import java.util.ArrayList;

public class OMUD_MMUDBlocks{
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

		public abstract void updateActiveBlock(int pos_block, String strFoundCmdFull, OMUD_MMUDChar.ActiveBlock ablk);

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

		public abstract int findBlockData(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset);
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
	private OMUD_MMUDBlock_MUDMenu 		_blkMUDMenu = null;
	private ArrayList<Block> 			_arrlBlocks = null;
	private int _bpos_cmd_editor =  	0;
	private final int BPOS_STATLINE = 	0;
	private final int BPOS_CMDS_START = 1;
	private final int BPOS_LOOKROOM = 	1;

	public OMUD_MMUDBlocks(){
		_arrlBlocks = new ArrayList<Block>();
		// ==================
		// ------------------
		// Statline
		// ------------------
		_arrlBlocks.add(new OMUD_MMUDBlock_Statline()); 	// BPOS_STATLINE
		// ------------------
		// Command Blocks
		// ------------------
		_arrlBlocks.add(new OMUD_MMUDBlock_LookRoom()); 	// BPOS_LOOKROOM
		_arrlBlocks.add(new OMUD_MMUDBlock_Inventory());
		_arrlBlocks.add(new OMUD_MMUDBlock_Editor()); 		// NOTE: should always be at the end!
		_bpos_cmd_editor = _arrlBlocks.size() - 1; 			// 
		// ------------------
		// Other Line Blocks
		// ------------------
		_arrlBlocks.add(new OMUD_MMUDBlock_Other());
		// ==================

		// ------------------
		// MUD Menu (separate)
		// ------------------
		// somewhat unique so keep this separate...
		_blkMUDMenu = new OMUD_MMUDBlock_MUDMenu();
	}

	public int parseLineBlocks(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		// if current block is the statline or mud menu, just parse all the line blocks...
		if (mmc.ablk.block_pos < BPOS_CMDS_START){
			for (int i = BPOS_CMDS_START; i < _arrlBlocks.size() && pos_data_found_start == -1; ++i)
				pos_data_found_start = _arrlBlocks.get(i).findBlockData(ommme, mmc, sbTelnetData, pos_offset);
		// else parse only the current line block...
		} else {
			pos_data_found_start = _arrlBlocks.get(mmc.ablk.block_pos).findBlockData(ommme, mmc, sbTelnetData, pos_offset);			
		}

		return pos_data_found_start;
	}

	public int parseStatline(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData){
		int pos_data_found_start = _arrlBlocks.get(BPOS_STATLINE).findBlockData(ommme, mmc, sbTelnetData, 0);
		if (pos_data_found_start > -1)
			_arrlBlocks.get(BPOS_STATLINE).updateActiveBlock(BPOS_STATLINE, "", mmc.ablk);
		return pos_data_found_start;
	}

	public int parseMUDEditor(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData){
		return _arrlBlocks.get(_bpos_cmd_editor).findBlockData(ommme, mmc, sbTelnetData, 0);
	}

	public int parseMUDMenu(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData){
		return _blkMUDMenu.findBlockData(ommme, mmc, sbTelnetData, 0);
	}

	// findCmd(): main external call to match a user-input command (assumes passed in as lower-case)
	// returns true if at an in-game menu/editor (train stats, etc.)
	public boolean findCmd(String strCmd, OMUD_MMUDChar.ActiveBlock ablk){
		// reset some active block stuff...
		ablk.block_pos = OMUD_MMUDChar.ActiveBlock.BPOS_INVALID;
		ablk.data_type = OMUD_MMUD.Data.eDataType.DT_STATLINE;

		String strFoundCmdFull = null;
		for (int i = BPOS_CMDS_START; i <= _bpos_cmd_editor && strFoundCmdFull == null; ++i)
			if ((strFoundCmdFull = _arrlBlocks.get(i).matchCmdText(strCmd)) != null)
				_arrlBlocks.get(i).updateActiveBlock(i, strFoundCmdFull, ablk);
		return ablk.block_pos == _bpos_cmd_editor;
	}	
}
