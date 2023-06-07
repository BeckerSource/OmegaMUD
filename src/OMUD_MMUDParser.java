import java.util.ArrayList;

interface OMUD_IMUDEvents{
	public boolean isInsideMUD();
	public void notifyMUDStatline();
	public void notifyMUDRoom();
	public void notifyMUDExp();
	public void notifyMUDStats();
	public void notifyMUDInv();
	public void notifyMUDParty();
	public void notifyMUDSpells();
	public void notifyMUDCombatToggle(boolean is_on);
	public void notifyMUDBBSMenu();
	public void notifyMUDWelcome(final String strText);
	public void notifyMUDDebugCmd(final String strText);
	public void notifyMUDDebugChar(final OMUD_MMUD.DataRoom dataRoom, final OMUD_MMUD.DataStatline dataStatline);
	public void notifyMUDDebugOther(final String strText);
}

public class OMUD_MMUDParser{

	// ------------------
	// MUD Strings
	// ------------------
	private enum eMUDStrings{
		PREFIX_RESET,
		PREFIX_RESET_WHBL,
		STATLINE_PRE,
		STATLINE_END,
		STATLINE_RESTING,
		STATLINE_MEDITATING,
		ROOM_NAME,
		YOU_NOTICE_PRE,
		YOU_NOTICE_END,
		ALSO_HERE_PRE,
		ALSO_HERE_END,
		OBVIOUS_EXITS,
		ROOM_DIMLY_LIT,
		ROOM_PITCH_BLACK,
		COMBAT_ON,
		COMBAT_OFF,
		MOVE_NO_EXIT_DIR,
		BBS_MUD_MENU_PROMPT,
		ROOM_DESCRIPTION,
		ROOM_SEARCH_NONE,
		MEDITATE_WAKE,
		MEDITATE_WONT_HELP,
		CMD_NO_EFFECT,
		REG_RESTING,
		REG_MEDITATING,
		REG_SPELL_ALREADY_CAST,
		COLOR_MAGENTA,
		COLOR_CYAN,
		COLOR_WHITE,
		COLOR_OTHER
	}

	private final String[] MUD_STRINGS = {
		/* PREFIX_RESET */				"[0m",
		/* PREFIX_RESET_WHBL */ 		"[0;37;40m",
		/* STATLINE_PRE */ 				"[79D[K[0;37m[",
		/* STATLINE_END */ 				"]:",
		/* STATLINE_RESTING */ 			"(Resting) ",
		/* STATLINE_MEDITATING */ 		"(Meditating) ",
		/* ROOM_NAME */ 				"[79D[K[1;36m",
		/* YOU_NOTICE_PRE */ 			"[0;36mYou notice ",
		/* YOU_NOTICE_END */ 			"here.", // NOTE: don't use a space prefix here because it could be wrapped to the start of a line
		/* ALSO_HERE_PRE */ 			"[0;35mAlso here: ",
		/* ALSO_HERE_END */ 			"[0;35m.",
		/* OBVIOUS_EXITS */ 			"[0;32mObvious exits: ",
		/* ROOM_DIMLY_LIT */ 			"[0;37mThe room is dimly lit",
		/* ROOM_PITCH_BLACK */ 			"[0;37m__NEED_ANSI_TEXT_FOR_THIS", // need correct ansi text
		/* COMBAT_ON */ 				"[79D[K[0;33m*Combat Engaged*",
		/* COMBAT_OFF */ 				"[79D[K[0;33m*Combat Off*",
		/* MOVE_NO_EXIT_DIR */ 			"[0;37;40m[79D[KThere is no exit in that direction!",
		/* BBS_MUD_MENU_PROMPT */ 		"[1;30m[[0mMAJORMUD[1;30m][0m: [0;37;40m",
		/* ROOM_DESCRIPTION */ 			"[79D[K[0;37;40m    ",
		/* ROOM_SEARCH_NONE */			"[0;36mYour search revealed nothing.",
		/* MEDITATE_WAKE */ 			"[79D[KYou awake from deep meditation feeling stronger!",
		/* MEDITATE_WONT_HELP */ 		"[0;37;40mMeditation will not help at this time.",
		/* CMD_NO_EFFECT */ 			"[0;37mYour command had no effect.",
		/* REG_RESTING */ 				"You are now resting.",
		/* REG_MEDITATING */			"You are now meditating.",
		/* REG_SPELL_ALREADY_CAST */ 	"You have already cast a spell this round!",
		/* COLOR_MAGENTA */ 			"[79D[K[0;35m",
		/* COLOR_CYAN */ 				"[79D[K[0;36m",
		/* COLOR_WHITE */ 				"[79D[K[1;37m",
		/* COLOR_OTHER */ 				"[79D[K",
		/* INV_CARRY_PRE */ 	"",
		/* INV_KEYS */ 			"",
		/* INV_WEALTH */ 		"",
		/* INV_ENC */ 			"",
		/* WEALTH */ 			"",
		/* BANKS */ 			"",
		/* LOOK_DESC */ 		"",
		/* LOOK_EQUIPPED */ 	"",
		/* LOOK_ITEM */ 		"",
		/* EXP */ 				"",
		/* STAT_L1 */ 			"",
		/* STAT_L2 */ 			"",
		/* STAT_L3 */ 			"",
		/* STAT_L4 */ 			"",
		/* STAT_L5 */ 			"",
		/* STAT_L6 */ 			"",
		/* STAT_L7 */ 			"",
		/* STAT_L8 */ 			"",
		/* PARTY_NONE */ 		"",
		/* PARTY_CHAR */ 		"",
		/* TRAIN */ 			"",
	};

	// ------------------
	// OMUD_MMUDParser
	// ------------------
	private OMUD_IMUDEvents			_ommme  = 			null;
	private OMUD_MMUD.DataRoom 		_dataRoom = 		null;
	private OMUD_MMUD.DataStatline 	_dataStatline = 	null;
	private StringBuilder 			_sbDataTelnet = 	null;
	private StringBuilder 			_sbDataMUD = 		null;
	private final String CMD_ENTER = "<ENTER>"; // change/move this later, using for debugging visual on room look with enter key

	public OMUD_MMUDParser(OMUD_IMUDEvents ommme){
		_ommme = ommme;
		_sbDataTelnet = new StringBuilder();
		_sbDataMUD = 	new StringBuilder();
		resetData();
	}

	public void resetData(){
		_dataRoom = 	new OMUD_MMUD.DataRoom();
		_dataStatline = new OMUD_MMUD.DataStatline();
	}

	public boolean isInsideMUD() 			{return _ommme.isInsideMUD();} // kinda exposure-hack for telnet parser
	public void clearTelnetData() 			{_sbDataTelnet.setLength(0);}
	public void appendChar(char c)			{_sbDataTelnet.append(c);}
	public void appendSB(StringBuilder sb)	{_sbDataTelnet.append(sb);}
	public void deleteLastChar(){
		if (_sbDataTelnet.length() > 0) 
			_sbDataTelnet.deleteCharAt(_sbDataTelnet.length() - 1);
	}
	
	// ------------------
	// Room Data Processors
	// ------------------
	private void splitCommaListToArray(String strItems, ArrayList<String> arrlItems){
        String[] items = strItems.split(",");
        for (String item : items)
        	arrlItems.add(item.trim());
	}

	private void buildRoomExits(){
        String[] dirs = _dataRoom.exits.split(",");
        for (String dir : dirs){

        	// split again, for now just use the last token to get the direction...
        	String[] words = dir.trim().split(" ");
        	if (words.length > 0){
	        	String word_door_state = "";
	        	String word_door_name  = "";
	        	String word_dir = words[words.length - 1];
	    		OMUD_MMUD.eExitDir  edir =	OMUD_MMUD.eExitDir.NONE;
	    		OMUD_MMUD.eDoorType edoor = OMUD_MMUD.eDoorType.NONE;

	    		// if exists, match up the door data...
	        	if (words.length == 3){
	        		word_door_state = words[0];
	        		word_door_name  = words[1]; // not sure if door name will ever be needed or not
	        		// match up the door state -
	        		// ignore case by default, not sure if needed for doors...
		    		int ds_count = OMUD_MMUD.eDoorType.COUNT.ordinal();
		    		for (int i = 0; i < ds_count; ++i){
		    			if (word_door_state.equalsIgnoreCase(OMUD_MMUD.DOOR_TYPE_STRINGS[i])){
		    				edoor = OMUD_MMUD.eDoorType.values()[i];
		    				break;
		    			}
		    		}
	        	}

	        	// match up the direction -
	        	// ignore case for some situations with secret passages with upper first char directions and probably others...
		    	int ed_count = OMUD_MMUD.eExitDir.COUNT.ordinal();
	        	for (int i = 0; i < ed_count; ++i){
	        		if (word_dir.equalsIgnoreCase(OMUD_MMUD.EXIT_DIR_STRINGS[i])){
	        			edir = OMUD_MMUD.eExitDir.values()[i];
	        			break;
	        		}
	        	}

				_dataRoom.arrlExits.add(new OMUD_MMUD.RoomExit(edir, edoor));
        	}
        }
	}

	// ------------------
	// Parsing
	// ------------------
	// cleanData(): trim lf/space and strip ansi as requested...
	private void cleanData(StringBuilder sbText, boolean trim_lf_spc, boolean strip_ansi){
		int pos_first_char = -1;
		int pos_last_char  = -1;
		for (int i = 0; i < sbText.length(); ++i){
			if (strip_ansi && sbText.charAt(i) == OMUD.ASCII_ESC){
				int pos_ansi_end = sbText.indexOf(OMUD.CSI_GRAPHICS_STR, i);
				// if matching ansi end is not found, just delete the escape char.
				if (pos_ansi_end == -1)
					pos_ansi_end = i + 1; 	// +1 for exclusive end
				else pos_ansi_end++; 		// 
				sbText.delete(i--, pos_ansi_end); // move 'i' back after delete so that we pick up the first char after the delete

			} else if (trim_lf_spc){
				if (sbText.charAt(i) == OMUD.ASCII_LF){
					sbText.setCharAt(i, OMUD.ASCII_SPC);
				} else if (sbText.charAt(i) != OMUD.ASCII_SPC){
					pos_last_char = i;
					if (pos_first_char == -1)
						pos_first_char = i;
				}				
			}
		}

		// trim: delete end first...
		if (trim_lf_spc){
			if (pos_last_char > pos_first_char && pos_last_char < sbText.length() - 1)
				sbText.delete(pos_last_char + 1, sbText.length()); // +1 to move forward to begin at space after last char
			if (pos_first_char > 0)
				sbText.delete(0, pos_first_char);			
		}
	}

	// checkMudStringPrefix(): some mud strings have a possible prefix - check for it and remove it -
	// NOTE: debug string is just for internal visual/coding reference.  Not a string required for function.
	public int checkMudStringPrefix(String strReasonDbg, int pos_offset, String strPrefix){
		int prefix_len = strPrefix.length();
		int pos_start = pos_offset - prefix_len;
		if (pos_start >= 0 && _sbDataTelnet.substring(pos_start, pos_start + prefix_len).equals(strPrefix)){
			_sbDataTelnet.delete(pos_start, pos_start + prefix_len);
			pos_offset = pos_start;
		}
		return pos_offset;
	}

	private int findMUDData(boolean has_dynamic_text, int pos_offset, String strStartSeq, String strEndSeq){
		int pos_start = -1;
		int pos_end = -1;
		if (_sbDataTelnet.length() >= strStartSeq.length() + strEndSeq.length()){
			pos_end = pos_offset;
			if (strEndSeq.length() > 0)
				pos_end = _sbDataTelnet.lastIndexOf(strEndSeq, pos_offset);

			// matched the end...
			if (pos_end + strEndSeq.length() <= pos_offset + 1){ // +1 in calc to compensate for 'exclusive' bounds checking
				pos_start = _sbDataTelnet.lastIndexOf(strStartSeq, pos_end);
				if (pos_start >= 0){

					// match the start - check requirements and if data exists between...
					int pos_start_data = pos_start + strStartSeq.length();
					if (has_dynamic_text && pos_end <= pos_start_data){
						pos_start = -1; // needed data but not found - returns fail
					} else {
						if (pos_end > pos_start_data)
							_sbDataMUD.append(_sbDataTelnet.substring(pos_start_data, pos_end));
						_sbDataTelnet.delete(pos_start, (pos_end + strEndSeq.length()) + 1); // +1 for exclusive
					}
				}
			}
		}
		return pos_start;
	}

	public void threadParseData(StringBuilder sbLastCmd, String strLastCmd){
		int pos_data_delete_start = 	-1;
		int pos_unknown_delete_end = 	 0;
		for (int i = 0; i < _sbDataTelnet.length(); ++i){
			// ------------------
			// Check for User Command
			// ------------------
			if (sbLastCmd.length() > 0 && i < _sbDataTelnet.length() && _sbDataTelnet.charAt(i) == OMUD.ASCII_LF && _ommme.isInsideMUD()){
				int pos_new_lf = 0;
				int pos_cmd_start = 0;
				if (i > 0){
					if ((pos_new_lf = OMUD.getPrevLF(_sbDataTelnet, i - 1)) > -1)
						 pos_cmd_start = pos_new_lf + 1;
					else pos_new_lf = 0;
				}

				if (_sbDataTelnet.substring(pos_cmd_start, i + 1).equals(strLastCmd)){
					_sbDataTelnet.delete(pos_cmd_start, i + 1);

					// remove the LF, set the new cmd, and clear the cmd to avoid checking for it again in the loop...
					sbLastCmd.delete(sbLastCmd.length() - 1, sbLastCmd.length()); 
					_dataStatline.last_cmd = sbLastCmd.toString();
					sbLastCmd.setLength(0);

					// DEBUG HACK: change "enter-only" room look with something easier to see -
					// change/move this later...
					if (_dataStatline.last_cmd.length() == 0) // LF was removed above
						_dataStatline.last_cmd = CMD_ENTER;
					_ommme.notifyMUDDebugCmd(new String(_dataStatline.last_cmd));

					i = pos_new_lf; // update the main iter
				}
			}

			// ------------------
			// Find Game Data: LF+ESC or LF+End Data Strings
			// ------------------
			if (i < _sbDataTelnet.length()){
				char char_next = i + 1 < _sbDataTelnet.length() ? _sbDataTelnet.charAt(i + 1) : 0; // return 0 val is end of bufer
				if (_ommme.isInsideMUD() && _sbDataTelnet.charAt(i) == OMUD.ASCII_LF && (char_next == OMUD.ASCII_ESC || char_next == 0)){

					// ------------------
					// Room: Name
					// ------------------
					if ((pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.ROOM_NAME.ordinal()], "")) > -1){
						cleanData(_sbDataMUD, true, false);

						// PREFIX: if room name is shown after a move command, it will have a white/black reset prefix...
						pos_data_delete_start = checkMudStringPrefix("Room Name After Move", pos_data_delete_start, MUD_STRINGS[eMUDStrings.PREFIX_RESET_WHBL.ordinal()]);

						// if no room name yet, process as the welcome msg -
						// we should always have a previous mud room name when inside the game...
						if (_dataRoom.name.length() == 0){
							_ommme.notifyMUDWelcome(_sbDataTelnet.substring(0, pos_data_delete_start));
							_sbDataTelnet.delete(0, pos_data_delete_start);
							pos_data_delete_start = 0;
						}

						// reset the room data here -
						// mainly need for resetting room description in case it isn't shown...
						_dataRoom = new OMUD_MMUD.DataRoom();
						_dataRoom.name = _sbDataMUD.toString();

					// ------------------
					// Room: You Notice (Items+Hidden)
					// ------------------
					} else if ((pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.YOU_NOTICE_PRE.ordinal()], MUD_STRINGS[eMUDStrings.YOU_NOTICE_END.ordinal()])) > -1){
						cleanData(_sbDataMUD, true, false);

						// PREFIX: seaching: if a prefix is found, this was data from a search...
						int pos_data_delete_start_search_check = checkMudStringPrefix("You Notice Searched Items", pos_data_delete_start, MUD_STRINGS[eMUDStrings.PREFIX_RESET_WHBL.ordinal()]);
						if (pos_data_delete_start > pos_data_delete_start_search_check){
							pos_data_delete_start = pos_data_delete_start_search_check;
							_dataRoom.items_hidden = _sbDataMUD.toString();
							splitCommaListToArray(_dataRoom.items_hidden, _dataRoom.arrlItemsHidden);
						// NON-SEARCH / visible item listing...
						} else {
							_dataRoom.items = _sbDataMUD.toString();						
							splitCommaListToArray(_dataRoom.items, _dataRoom.arrlItems);
						}

					// ------------------
					// Room: Also Here (Units)
					// ------------------
					} else if ((pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.ALSO_HERE_PRE.ordinal()], MUD_STRINGS[eMUDStrings.ALSO_HERE_END.ordinal()])) > -1){
						cleanData(_sbDataMUD, true, true); // units (also here) has ANSI
						_dataRoom.units = _sbDataMUD.toString();
						splitCommaListToArray(_dataRoom.units, _dataRoom.arrlUnits);

					// ------------------
					// Room: Obvious Exits
					// ------------------
					} else if ((pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.OBVIOUS_EXITS.ordinal()], "")) > -1){
						cleanData(_sbDataMUD, true, false);

						// PREFIX: if shown after units/'also here', an ansi reset command will be present...
						pos_data_delete_start = checkMudStringPrefix("Obv Exits After Also Here", pos_data_delete_start, MUD_STRINGS[eMUDStrings.PREFIX_RESET.ordinal()]);

						// build the exit data...
						_dataRoom.exits = _sbDataMUD.toString();
						buildRoomExits();

						// create Megamud RoomID after the exit data is built above...
						_dataRoom.roomID = 
							OMUD_MEGA.getRoomNameHash(_dataRoom.name) + 
							OMUD_MEGA.getRoomExitsCode(_dataRoom.arrlExits);

					// ------------------
					// Room: Light: Dim
					// ------------------
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.ROOM_DIMLY_LIT.ordinal()], "")) > -1){
						_dataRoom.light = OMUD_MMUD.eRoomLight.DIMLY_LIT;

					// ------------------
					// Room: Light: Pitch Black
					// ------------------
					// NOTE: **UNTESTED**
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.ROOM_PITCH_BLACK.ordinal()], "")) > -1){
						_dataRoom.light = OMUD_MMUD.eRoomLight.PITCH_BLACK;

					// ------------------
					// Room: Description
					// ------------------
					// NOTE: not always shown, depends on verbose/brief setting
					} else if ((pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.ROOM_DESCRIPTION.ordinal()], "")) > -1){
						_dataRoom.desc = _sbDataMUD.toString();
						_ommme.notifyMUDDebugChar(new OMUD_MMUD.DataRoom(_dataRoom), new OMUD_MMUD.DataStatline(_dataStatline));

					// ------------------
					// Room Search Revealed None
					// ------------------
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.ROOM_SEARCH_NONE.ordinal()], "")) > -1){
						_ommme.notifyMUDDebugOther("[ROOM_SEARCH_NONE]\n");

					// ------------------
					// Invalid Move Dir (no exit in that dir)
					// ------------------
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.MOVE_NO_EXIT_DIR.ordinal()], "")) > -1){
						_ommme.notifyMUDDebugOther("[MOVE_NO_EXIT_DIR]\n");

					// ------------------
					// Meditate Wake
					// ------------------
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.MEDITATE_WAKE.ordinal()], "")) > -1){
						_ommme.notifyMUDDebugOther("[MEDITATE_WAKE]\n");

					// ------------------
					// Meditate Won't Help
					// ------------------
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.MEDITATE_WONT_HELP.ordinal()], "")) > -1){
						_ommme.notifyMUDDebugOther("[MEDITATE_WONT_HELP]\n");

					// ------------------
					// Invalid Command (command has no effect)
					// ------------------
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.CMD_NO_EFFECT.ordinal()], "")) > -1){
						_ommme.notifyMUDDebugOther("[CMD_NO_EFFECT]\n");

					// ------------------
					// Regular/Non-ANSI Strings
					// ------------------
					// capture the text so that it's deleted correctly from the buffer (maybe do stuff with it later?)
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.REG_RESTING.ordinal()], "")) > -1){
						_ommme.notifyMUDDebugOther("[REG_RESTING]\n");
						// PREFIX: can have a clear white/black prefix if was already resting
						pos_data_delete_start = checkMudStringPrefix("Rest Cmd When Already Resting", pos_data_delete_start, MUD_STRINGS[eMUDStrings.PREFIX_RESET_WHBL.ordinal()]);
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.REG_MEDITATING.ordinal()], "")) > -1){
						_ommme.notifyMUDDebugOther("[REG_MEDITATING]\n");
						// PREFIX: can have a clear white/black prefix if was already resting
						pos_data_delete_start = checkMudStringPrefix("Med Cmd When Already Meditating", pos_data_delete_start, MUD_STRINGS[eMUDStrings.PREFIX_RESET_WHBL.ordinal()]);
					} else if ((pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.REG_SPELL_ALREADY_CAST.ordinal()], "")) > -1){
						_ommme.notifyMUDDebugOther("[REG_SPELL_ALREADY_CAST]\n");

					// ------------------
					// Various ANSI Color-Prefix Strings (LAST)
					// ------------------
					// NOTE: COLOR_OTHER should be last because the gamestring is common amongst others above
					} else if (
						(pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.COLOR_MAGENTA.ordinal()], ""))	> -1 ||
						(pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.COLOR_CYAN.ordinal()], "")) 		> -1 ||
						(pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.COLOR_WHITE.ordinal()], "")) 		> -1 ||
						(pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.COLOR_OTHER.ordinal()], "")) 		> -1){
						_ommme.notifyMUDDebugOther("[COLOR_TEXT]\n" + _sbDataMUD.toString() + "\n");
					}

				// ------------------
				// Find Game Data: Buffer End
				// (prompt-style strings: bbs mud menu, statline, etc)...
				// ------------------
				} else if (char_next == 0){
					if (_ommme.isInsideMUD()){

						// ------------------
						// Statline
						// ------------------
						if ((pos_data_delete_start = findMUDData(true, i, MUD_STRINGS[eMUDStrings.STATLINE_PRE.ordinal()], MUD_STRINGS[eMUDStrings.STATLINE_END.ordinal()])) > -1){
							cleanData(_sbDataMUD, false, true); // has ansi inside to strip

							// default to active first...
							_dataStatline.rest = OMUD_MMUD.eRestState.ACTIVE;

							// ------------------
							// Statline: Resting/Meditation: MA/KAI Chars
							// ------------------
							// MA/KAI chars: resting/meditation strings are a suffix after the statline prompt outer brackets.
							if (pos_data_delete_start < _sbDataTelnet.length()){
								int pos_rest_start = 0;
								if ((pos_rest_start = _sbDataTelnet.indexOf(MUD_STRINGS[eMUDStrings.STATLINE_MEDITATING.ordinal()], pos_data_delete_start)) > -1){
									_sbDataTelnet.delete(pos_rest_start, pos_rest_start + MUD_STRINGS[eMUDStrings.STATLINE_MEDITATING.ordinal()].length());
									_dataStatline.rest = OMUD_MMUD.eRestState.MEDITATING;
								} else if ((pos_rest_start = _sbDataTelnet.indexOf(MUD_STRINGS[eMUDStrings.STATLINE_RESTING.ordinal()], pos_data_delete_start)) > -1){
									_sbDataTelnet.delete(pos_rest_start, pos_rest_start + MUD_STRINGS[eMUDStrings.STATLINE_RESTING.ordinal()].length());
									_dataStatline.rest = OMUD_MMUD.eRestState.RESTING;
								}
							}

							// ------------------
							// Statline: Resting/Meditation: Non-MA/KAI Chars
							// ------------------
							// Non-MA/KAI chars: if not found above, try inside of the statline...
							if (_dataStatline.rest == OMUD_MMUD.eRestState.ACTIVE){
								int pos_rest_start = 0;
								if ((pos_rest_start = _sbDataMUD.indexOf(MUD_STRINGS[eMUDStrings.STATLINE_RESTING.ordinal()], 0)) > -1){
									_sbDataMUD.delete(pos_rest_start, pos_rest_start + MUD_STRINGS[eMUDStrings.STATLINE_RESTING.ordinal()].length());
									_dataStatline.rest = OMUD_MMUD.eRestState.RESTING;
								// not sure if non-MA/KAI chars would ever use meditate?
								} else if ((pos_rest_start = _sbDataMUD.indexOf(MUD_STRINGS[eMUDStrings.STATLINE_MEDITATING.ordinal()], 0)) > -1){
									_sbDataMUD.delete(pos_rest_start, pos_rest_start + MUD_STRINGS[eMUDStrings.STATLINE_MEDITATING.ordinal()].length());
									_dataStatline.rest = OMUD_MMUD.eRestState.MEDITATING;
								}
							}

							_dataStatline.text = _sbDataMUD.toString();
							_ommme.notifyMUDDebugChar(new OMUD_MMUD.DataRoom(_dataRoom), new OMUD_MMUD.DataStatline(_dataStatline));
						}
					}

					// ------------------
					// BBS Mud Menu
					// ------------------
					// only check if no other game data was found...
					if (pos_data_delete_start == -1 && 
						(pos_data_delete_start = findMUDData(false, i, MUD_STRINGS[eMUDStrings.BBS_MUD_MENU_PROMPT.ordinal()], "")) > -1){
						_ommme.notifyMUDBBSMenu();
						resetData();
					}
				} 

				// ------------------
				// Found Data/Extra Delete Calc
				// ------------------
				// If mud data was found, reset the loop pos to search again from the start.
				// The order of finding mud data will be somewhat random and based on which IF statement above hits first.
				// We also keep track of the "earliest" found mud data which will be used as 
				// the "pos_unknown_delete_end" when deleting unused data. Actual delete will take place outside of this loop.
				if (pos_data_delete_start > -1){
					_sbDataMUD.setLength(0);
					if (pos_data_delete_start < pos_unknown_delete_end || pos_unknown_delete_end == 0)
						pos_unknown_delete_end = pos_data_delete_start;
					pos_data_delete_start = -1;
					i = 0;
				}
			}
		}

		// ------------------
		// Unknown/Extra Data
		// ------------------
		// Keep the buffer clean by removing extra data that we didn't match.  See notes in loop above.
		// Also acts as a way to see unprocessed strings.
		if (_ommme.isInsideMUD() && pos_unknown_delete_end > 0){
			_ommme.notifyMUDDebugOther("[UNKNOWN] [LEN: " + _sbDataTelnet.length() + "]\n" + _sbDataTelnet.substring(0, pos_unknown_delete_end));
			_sbDataTelnet.delete(0, pos_unknown_delete_end);
			if (_sbDataTelnet.length() > 0){
				_ommme.notifyMUDDebugOther("[UNKNOWN WARNING] [EXTRA BUFFER LEN: " + _sbDataTelnet.length() + "]\n" + _sbDataTelnet.substring(0, _sbDataTelnet.length()));
				OMUD.logError("Error parsing MUD data: remaining buffer: length: " + _sbDataTelnet.length() + ": "  + _sbDataTelnet.substring(0, _sbDataTelnet.length()));
			}
		}
	}

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
}
