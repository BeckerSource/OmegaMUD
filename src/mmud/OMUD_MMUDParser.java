import java.util.ArrayList;

interface OMUD_IMUDEvents{
	public void notifyMUDLocation(final OMUD.eBBSLocation eLoc);
	public void notifyMUDStatline(final OMUD_MMUD.DataStatline dataStatline);
	public void notifyMUDCombatToggle(boolean is_on);
	public void notifyMUDCmd(final String strText);
	public void notifyMUDOther(final String strText);
	public void notifyMUDWelcome(final String strText);
	public void notifyMUDRoom(final OMUD_MMUD.DataRoom dataRoom);
	public void notifyMUDInv(final OMUD_MMUD.DataInv dataInv);
	public void notifyMUDStats(final OMUD_MMUD.DataStats dataStats);
	public void notifyMUDExp();
	public void notifyMUDParty();
	public void notifyMUDSpells();
}

public class OMUD_MMUDParser{

	// ------------------
	// OMUD_MMUDParser
	// ------------------
	private OMUD.eBBSLocation 			_eBBSLoc = 		OMUD.eBBSLocation.BBS;
	private OMUD_IMUDEvents				_ommme  = 		null;
    private OMUD_MMUDChar       		_mmc =          null;	
	private StringBuilder 				_sbDataTelnet = null;
	private static OMUD_MMUDBlocks  	_s_blocks = 	new OMUD_MMUDBlocks();

	public OMUD_MMUDParser(OMUD_IMUDEvents ommme){
		_ommme = ommme;
		_sbDataTelnet = new StringBuilder();
		resetData(OMUD.eBBSLocation.BBS);
	}

	private void resetData(OMUD.eBBSLocation eBBSLoc){		
		_eBBSLoc = eBBSLoc;
        _mmc =  new OMUD_MMUDChar();
	}

	public void appendChar(char c)			{_sbDataTelnet.append(c);}
	public void appendSB(StringBuilder sb)	{_sbDataTelnet.append(sb);}
	public void deleteLastChar(){
		if (_sbDataTelnet.length() > 0) 
			_sbDataTelnet.deleteCharAt(_sbDataTelnet.length() - 1);
	}

	// clearTelnetData(): only clear at specific locations
	public void clearTelnetData(){
		if (_eBBSLoc == OMUD.eBBSLocation.MUD_EDITOR || _eBBSLoc == OMUD.eBBSLocation.BBS)
			_sbDataTelnet.setLength(0);
	}

	// updateParseDeleteLen(): loop helper func -
	// keeps track of the total delete length from zero for the telnet data buffer.
	private int updateParseDeleteLen(int pos_data_found_start, int pos_buf_delete_len){
		if (pos_data_found_start < pos_buf_delete_len || pos_buf_delete_len == 0)
			pos_buf_delete_len = pos_data_found_start;		
		return pos_buf_delete_len;
	}

	private void findMenuCmd(StringBuilder sbCmd){
		if (sbCmd != null && sbCmd.length() > 0){

			int pos_cmd_start = -1;
			if (_sbDataTelnet.length() > 0)
				pos_cmd_start = _sbDataTelnet.indexOf(sbCmd.toString(), 0);

			boolean valid_cmd = false;
			OMUD.eBBSLocation eNewLoc = _eBBSLoc;
			char cmd_first_lower = Character.toLowerCase(sbCmd.charAt(0));
        		 if ((valid_cmd = cmd_first_lower == 'e'))
				eNewLoc = pos_cmd_start > -1 ? OMUD.eBBSLocation.MUD_EDITOR : _eBBSLoc;
        	else if ((valid_cmd = cmd_first_lower == 'x'))
    		 	eNewLoc = pos_cmd_start > -1 ? OMUD.eBBSLocation.BBS : _eBBSLoc;

			// valid command and was found in telnet data...
			if (eNewLoc != _eBBSLoc){
				_sbDataTelnet.delete(0, pos_cmd_start + sbCmd.length());
				sbCmd.setLength(0);

				_ommme.notifyMUDLocation((_eBBSLoc = eNewLoc));
				_mmc.ablk.statline_wait = false;
			// else have a valid command and still waiting for it in the telnet data...
			} else if (valid_cmd){
				_mmc.ablk.statline_wait = true;
			}
		}
	}

	public boolean threadParseData(StringBuilder sbCmd){
		int pos_data_found_start =  -1;
		int pos_buf_delete_len = 	 0;

		// ------------------
		// BBS MUD Menu
		// ------------------
		// find a better way to isolate this check based on a command in the future?...
		if (_sbDataTelnet.length() > 0 && (pos_data_found_start = _s_blocks.parseBBSMenu(_ommme, _mmc, _sbDataTelnet)) > -1){
			_sbDataTelnet.delete(0, pos_data_found_start);
			// reset data and location...
			resetData(OMUD.eBBSLocation.MUD_MENU);
			_ommme.notifyMUDLocation(_eBBSLoc);
		}

		// ------------------
		// MUD Menu: Enter/Exit
		// ------------------
		// user sent a command at the BBS MUD menu...
		if (_eBBSLoc == OMUD.eBBSLocation.MUD_MENU)
			findMenuCmd(sbCmd);

		// ------------------
		// Inside MUD
		// ------------------
		if (OMUD.isInsideMUD(_eBBSLoc)){

			// ------------------
			// Find User Commands
			// ------------------
			// only process commands when not at an editor (training stats, input prompt, etc.) -
			// sbCmd can be null if there are no current commands in the telnet array...
			if (_eBBSLoc != OMUD.eBBSLocation.MUD_EDITOR && sbCmd != null && sbCmd.length() > 0 && _sbDataTelnet.length() > 0){

				// always wait for statline if we have a command -
				// this may hit multiple times because
				// we don't know when the linefeed will be sent from telnet...
				_mmc.ablk.statline_wait = true;

				if (OMUD.getNextLF(_sbDataTelnet, 0) == sbCmd.length() - 1){
					_sbDataTelnet.delete(0, sbCmd.length());

					sbCmd.deleteCharAt(sbCmd.length() - 1); // delete the trailing LF
					if (_s_blocks.findCmd(sbCmd.toString().toLowerCase(), _mmc.ablk))
						_ommme.notifyMUDLocation((_eBBSLoc = OMUD.eBBSLocation.MUD_EDITOR));

					// if we just had a single linefeed/enter (length would be zero here),
					// translate that to something visible...
					if (sbCmd.length() == 0)
						sbCmd.append("<ENTER>");
					sbCmd.append(" (" + _mmc.ablk.strCmdText + ")");
						_ommme.notifyMUDCmd(sbCmd.toString());
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
					if ((pos_data_found_start = _s_blocks.parseLineBlocks(_ommme, _mmc, _sbDataTelnet, i)) > -1){
						pos_buf_delete_len = updateParseDeleteLen(pos_data_found_start, pos_buf_delete_len);
						i = pos_data_found_start;
					}						
				}					
			}

			// ------------------
			// Statline Check
			// ------------------
			if (_sbDataTelnet.length() > 0 && (pos_data_found_start = _s_blocks.parseStatline(_ommme, _mmc, _sbDataTelnet)) > -1){
				pos_buf_delete_len = updateParseDeleteLen(pos_data_found_start, pos_buf_delete_len);

				// check if returning from trainin stats or at a prompt/input...
				if (_eBBSLoc == OMUD.eBBSLocation.MUD_EDITOR)
					_ommme.notifyMUDLocation((_eBBSLoc = OMUD.eBBSLocation.MUD));

				// notify for statline update and other data that was updated...
				_ommme.notifyMUDStatline(new OMUD_MMUD.DataStatline(_mmc.dataStatline));
					 if (_mmc.ablk.data_type == OMUD_MMUD.Data.eDataType.ROOM)
					_ommme.notifyMUDRoom(new OMUD_MMUD.DataRoom(_mmc.dataRoom));
				else if (_mmc.ablk.data_type == OMUD_MMUD.Data.eDataType.INV)
					_ommme.notifyMUDInv(new OMUD_MMUD.DataInv(_mmc.dataInv));
				else if (_mmc.ablk.data_type == OMUD_MMUD.Data.eDataType.STATS)
					_ommme.notifyMUDStats(new OMUD_MMUD.DataStats(_mmc.dataStats));

				// reset active block with statline forced as last data type...
				_mmc.ablk = new OMUD_MMUDChar.ActiveBlock(false, OMUD_MMUD.Data.eDataType.STATLINE);
			}

			// ------------------
			// Unknown/Extra Data
			// ------------------
			// Keep mud buffer clean by removing extra data that we didn't match.
			// (also helps to find new/unprocessed mud strings)
			if (pos_buf_delete_len > 0){
				_ommme.notifyMUDOther("[UNKNOWN DATA #1] [LEN: " + pos_buf_delete_len + "]\n--------\n" + _sbDataTelnet.substring(0, pos_buf_delete_len) + "\n--------\n");
				_sbDataTelnet.delete(0, pos_buf_delete_len);
				if (_sbDataTelnet.length() > 0){
					_ommme.notifyMUDOther("[UNKNOWN DATA #2] [LEN: "     + _sbDataTelnet.length() + "]\n--------\n" + _sbDataTelnet.substring(0, _sbDataTelnet.length()) + "\n--------\n");
					OMUD.logError("Error parsing MUD: extra buffer: length: " + _sbDataTelnet.length() + ":\n--------\n" + _sbDataTelnet.substring(0, _sbDataTelnet.length()) + "\n--------\n");
					_sbDataTelnet.setLength(0);
				}
			}
		}

		return _eBBSLoc == OMUD.eBBSLocation.BBS ? true : !_mmc.ablk.statline_wait;
	}
}
