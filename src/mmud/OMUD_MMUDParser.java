import java.util.ArrayList;

interface OMUD_IMUDEvents{
	public void notifyMUDExp();
	public void notifyMUDStats();
	public void notifyMUDInv();
	public void notifyMUDParty();
	public void notifyMUDSpells();
	public void notifyMUDCombatToggle(boolean is_on);
	public void notifyMUDLocation(final OMUD.eBBSLocation eLoc);
	public void notifyMUDWelcome(final String strText);
	public void notifyMUDDebugCmd(final String strText);
	public void notifyMUDDebugStatline(final OMUD_MMUD.DataStatline dataStatline);
	public void notifyMUDDebugRoom(final OMUD_MMUD.DataRoom dataRoom);
	public void notifyMUDDebugOther(final String strText);
}

public class OMUD_MMUDParser{

	// ------------------
	// OMUD_MMUDParser
	// ------------------
	private OMUD.eBBSLocation 			_eBBSLoc = 		OMUD.eBBSLocation.BBS;
	private OMUD_IMUDEvents				_ommme  = 		null;
	private StringBuilder 				_sbDataTelnet = null;
	private OMUD_MMUDBlocks.ActiveBlock _ablk = 		null;
	private static OMUD_MMUDBlocks  	_s_blocks = 	new OMUD_MMUDBlocks();

	public OMUD_MMUDParser(OMUD_IMUDEvents ommme){
		_ommme = ommme;
		_sbDataTelnet = new StringBuilder();
		_ablk = new OMUD_MMUDBlocks.ActiveBlock();
		resetData(OMUD.eBBSLocation.BBS);
	}

	public void resetData(OMUD.eBBSLocation eBBSLoc){
		_eBBSLoc = eBBSLoc;
		_s_blocks.resetData();
	}

	public void clearTelnetData() 			{_sbDataTelnet.setLength(0);}
	public void appendChar(char c)			{_sbDataTelnet.append(c);}
	public void appendSB(StringBuilder sb)	{_sbDataTelnet.append(sb);}
	public void deleteLastChar(){
		if (_sbDataTelnet.length() > 0) 
			_sbDataTelnet.deleteCharAt(_sbDataTelnet.length() - 1);
	}

	// updateParseDeleteLen(): loop helper func -
	// keeps track of the total delete length from zero for the telnet data buffer.
	private int updateParseDeleteLen(int pos_data_found_start, int pos_buf_delete_len){
		if (pos_data_found_start < pos_buf_delete_len || pos_buf_delete_len == 0)
			pos_buf_delete_len = pos_data_found_start;		
		return pos_buf_delete_len;
	}

	public boolean threadParseData(StringBuilder sbCmd){
		int pos_data_found_start =  -1;
		int pos_buf_delete_len = 	 0;

		if (OMUD.isInsideMUD(_eBBSLoc)){

			// don't process commands and line blocks when training stats
			// (training stats needs to wait for statline)...
			if (_eBBSLoc != OMUD.eBBSLocation.MUD_EDITOR){

				// ------------------
				// Find User Commands
				// ------------------
				// sbCmd can be null if there are no current commands in the telnet array...
				if (sbCmd != null && sbCmd.length() > 0 && _sbDataTelnet.length() > 0){
					_ablk = new OMUD_MMUDBlocks.ActiveBlock();
					if (OMUD.getNextLF(_sbDataTelnet, 0) == sbCmd.length() - 1){
						_sbDataTelnet.delete(0, sbCmd.length());

						sbCmd.deleteCharAt(sbCmd.length() - 1); // delete the trailing LF
						if (_s_blocks.findCmd(sbCmd.toString().toLowerCase(), _ablk)){
							_eBBSLoc = OMUD.eBBSLocation.MUD_EDITOR;
							_ommme.notifyMUDLocation(_eBBSLoc);
						}

						// if we just had a single linefeed/enter (length would be zero here),
						// translate that to something visible...
						if (_ablk.strCmdText.length() == 0)
							sbCmd.append("<ENTER>");
						sbCmd.append(" (" + _ablk.strCmdText + ")");

						_ommme.notifyMUDDebugCmd(sbCmd.toString());		
						sbCmd.setLength(0); // clear to show as processed
					}
				}

				// ------------------
				// Find Line Blocks (LF+ESC or LF+End)
				// ------------------
				// check length again in case above changes...
				for (int i = 0; i < _sbDataTelnet.length(); ++i){
					char char_next = i + 1 < _sbDataTelnet.length() ? _sbDataTelnet.charAt(i + 1) : 0; // 0 val is end of bufer
					if (_sbDataTelnet.charAt(i) == OMUD.ASCII_LF && (char_next == OMUD.ASCII_ESC || char_next == 0)){
						// parse/strip out line blocks as they are found, reset the iterator to find more until none are found...
						if ((pos_data_found_start = _s_blocks.parseLineBlocks(_ommme, _sbDataTelnet, i, _ablk)) > -1){
							pos_buf_delete_len = updateParseDeleteLen(pos_data_found_start, pos_buf_delete_len);
							i = pos_data_found_start;
						}						
					}					
				}
			}

			// ------------------
			// Statline Check
			// ------------------
			if (_sbDataTelnet.length() > 0 && (pos_data_found_start = _s_blocks.parseStatline(_ommme, _sbDataTelnet, _ablk)) > -1){
				if (_eBBSLoc == OMUD.eBBSLocation.MUD_EDITOR){
					_eBBSLoc  = OMUD.eBBSLocation.MUD;
					_sbDataTelnet.delete(0, _sbDataTelnet.length());
					pos_data_found_start = 0;
					_ommme.notifyMUDLocation(_eBBSLoc);
				}
				pos_buf_delete_len = updateParseDeleteLen(pos_data_found_start, pos_buf_delete_len);
			}

			// ------------------
			// Unknown/Extra Data
			// ------------------
			// Keep mud buffer clean by removing extra data that we didn't match.
			// (also helps to find new/unprocessed mud strings)
			if (pos_buf_delete_len > 0){
				_ommme.notifyMUDDebugOther("[UNKNOWN DATA #1] [LEN: " + pos_buf_delete_len + "]\n--------\n" + _sbDataTelnet.substring(0, pos_buf_delete_len) + "\n--------\n");
				_sbDataTelnet.delete(0, pos_buf_delete_len);
				if (_sbDataTelnet.length() > 0){
					_ommme.notifyMUDDebugOther("[UNKNOWN DATA #2] [LEN: "     + _sbDataTelnet.length() + "]\n--------\n" + _sbDataTelnet.substring(0, _sbDataTelnet.length()) + "\n--------\n");
					OMUD.logError("Error parsing MUD: extra buffer: length: " + _sbDataTelnet.length() + ":\n--------\n" + _sbDataTelnet.substring(0, _sbDataTelnet.length()) + "\n--------\n");
					_sbDataTelnet.setLength(0);
				}
			}

		// ------------------
		// MUD Menu Enter/Exit
		// ------------------
		} else if (_eBBSLoc == OMUD.eBBSLocation.MUD_MENU && sbCmd != null && sbCmd.length() > 0 && 
			_sbDataTelnet.length() > 0 && _sbDataTelnet.charAt(_sbDataTelnet.length() - 1) == OMUD.ASCII_LF){
			char cmd_first_lower = Character.toLowerCase(sbCmd.charAt(0));
        	if (cmd_first_lower == 'e'){
        		_eBBSLoc = OMUD.eBBSLocation.MUD;
				_ommme.notifyMUDLocation(_eBBSLoc);
        	} else if (cmd_first_lower == 'x'){
        		_eBBSLoc = OMUD.eBBSLocation.BBS;
				_ommme.notifyMUDLocation(_eBBSLoc);
        	}
		}

		// ------------------
		// Check for BBS MUD Menu
		// ------------------
		// can tweak later to check for X/exit from mud but for now, just always check if buffer is remaining...
		if (_sbDataTelnet.length() > 0 && (pos_data_found_start = _s_blocks.parseMUDMenu(_ommme, _sbDataTelnet)) > -1){
			resetData(OMUD.eBBSLocation.MUD_MENU);
			_ommme.notifyMUDLocation(_eBBSLoc); // loc set by reset above
		}		

		return OMUD.isInsideMUD(_eBBSLoc) ? !_ablk.wait_for_statline : true;
	}
}
