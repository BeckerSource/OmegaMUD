public class OMUD_MMUDCmd_LookRoom extends OMUD_MMUDCmds.Cmd{
	private final String MSTR_PREFIX_RESET = 		"[0m";
	private final String MSTR_PREFIX_RESET_WHBL =  	"[0;37;40m";
	private final String MSTR_ROOM_NAME =  			"[79D[K[1;36m";
	private final String MSTR_YOU_NOTICE_PRE =  	"[0;36mYou notice ";
	private final String MSTR_YOU_NOTICE_END =  	"here."; // NOTE: don't use a space prefix here because it could be wrapped to the start of a line
	private final String MSTR_ALSO_HERE_PRE =  		"[0;35mAlso here: ";
	private final String MSTR_ALSO_HERE_END =  		"[0;35m.";
	private final String MSTR_OBVIOUS_EXITS =  		"[0;32mObvious exits: ";
	private final String MSTR_ROOM_DIMLY_LIT =  	"[0;37mThe room is dimly lit";
	private final String MSTR_ROOM_PITCH_BLACK =  	"[0;37m__NEED_ANSI_TEXT_FOR_THIS"; // need correct ansi text
	private final String MSTR_ROOM_DESCRIPTION =  	"[79D[K[0;37;40m    ";
	private final String MSTR_ROOM_SEARCH_NONE = 	"[0;36mYour search revealed nothing.";

	private OMUD_MMUD.DataRoom _dataRoom = null;

	public boolean allowCmds(){return false;}
	public OMUD_MMUDCmd_LookRoom(){
		_dataRoom = new OMUD_MMUD.DataRoom();
		_arrlCmdText.add(new CmdText("\n",   		0, false));
		_arrlCmdText.add(new CmdText("look", 		0, false));
		_arrlCmdText.add(new CmdText("search", 	0, true));
	}

	public int findCmdData(OMUD_IMUDEvents ommme, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		// ------------------
		// Room: Name
		// ------------------
		if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROOM_NAME, "")) > -1){
			cleanData(true, false);

			// PREFIX: if room name is shown after a move command, it will have a white/black reset prefix...
			pos_data_found_start = checkPrefix("Room Name After Move", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);

			// if no room name yet, process as the welcome msg -
			// we should always have a previous mud room name when inside the game...
			if (_dataRoom.name.length() == 0){
				ommme.notifyMUDWelcome(sbTelnetData.substring(0, pos_data_found_start));
				sbTelnetData.delete(0, pos_data_found_start);
				pos_data_found_start = 0;
			}

			// reset the room data here -
			// mainly need for resetting room description in case it isn't shown...
			_dataRoom = new OMUD_MMUD.DataRoom();
			_dataRoom.name = _sbDataFound.toString();

		// ------------------
		// Room: You Notice (Items+Hidden)
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_YOU_NOTICE_PRE, MSTR_YOU_NOTICE_END)) > -1){
			cleanData(true, false);

			// PREFIX: seaching: if a prefix is found, this was data from a search...
			int pos_data_delete_start_search_check = checkPrefix("You Notice Searched Items", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);
			if (pos_data_found_start > pos_data_delete_start_search_check){
				pos_data_found_start = pos_data_delete_start_search_check;
				_dataRoom.items_hidden = _sbDataFound.toString();
				splitCommaListToArray(_dataRoom.items_hidden, _dataRoom.arrlItemsHidden);
			// NON-SEARCH / visible item listing...
			} else {
				_dataRoom.items = _sbDataFound.toString();						
				splitCommaListToArray(_dataRoom.items, _dataRoom.arrlItems);
			}

		// ------------------
		// Room: Also Here (Units)
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ALSO_HERE_PRE, MSTR_ALSO_HERE_END)) > -1){
			cleanData(true, true); // units (also here) has ANSI
			_dataRoom.units = _sbDataFound.toString();
			splitCommaListToArray(_dataRoom.units, _dataRoom.arrlUnits);

		// ------------------
		// Room: Obvious Exits
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_OBVIOUS_EXITS, "")) > -1){
			cleanData(true, false);

			// PREFIX: if shown after units/'also here', an ansi reset command will be present...
			pos_data_found_start = checkPrefix("Obv Exits After Also Here", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET);

			// build the exit data...
			_dataRoom.exits = _sbDataFound.toString();
			buildRoomExits();

			// create Megamud RoomID after the exit data is built above...
			_dataRoom.roomID = 
				OMUD_MEGA.getRoomNameHash(_dataRoom.name) + 
				OMUD_MEGA.getRoomExitsCode(_dataRoom.arrlExits);

		// ------------------
		// Room: Light: Dim
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_ROOM_DIMLY_LIT, "")) > -1){
			_dataRoom.light = OMUD_MMUD.eRoomLight.DIMLY_LIT;

		// ------------------
		// Room: Light: Pitch Black
		// ------------------
		// NOTE: **UNTESTED**
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_ROOM_PITCH_BLACK, "")) > -1){
			_dataRoom.light = OMUD_MMUD.eRoomLight.PITCH_BLACK;

		// ------------------
		// Room: Description
		// ------------------
		// NOTE: not always shown, depends on verbose/brief setting
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROOM_DESCRIPTION, "")) > -1){
			_dataRoom.desc = _sbDataFound.toString();

		// ------------------
		// Room Search Revealed None
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_ROOM_SEARCH_NONE, "")) > -1){
			ommme.notifyMUDDebugOther("[ROOM_SEARCH_NONE]\n");
		}

		return pos_data_found_start;
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

	public void resetData(){
		_dataRoom = new OMUD_MMUD.DataRoom();
	}

	public void notifyRoomData(OMUD_IMUDEvents ommme){
		ommme.notifyMUDDebugRoom(new OMUD_MMUD.DataRoom(_dataRoom));
	}
}
