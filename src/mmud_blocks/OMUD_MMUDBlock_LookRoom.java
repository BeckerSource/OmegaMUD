public class OMUD_MMUDBlock_LookRoom extends OMUD_MMUDBlocks.Block{
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

	public OMUD_MMUDBlock_LookRoom(){
		_arrlCmdText.add(new CmdText("look", 	0)); // 0-len covers LF/enter only (zero-len) and all chars as part of look
		_arrlCmdText.add(new CmdText("search", 	3)); // only "sea" is required
	}

	public void updateActiveBlock(int pos_block, String strFoundCmdFull, OMUD_MMUDChar.ActiveBlock ablk){
		ablk.update(pos_block, strFoundCmdFull, true, OMUD_MMUD.Data.eDataType.DT_ROOM);
	}

	public int findBlockData(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		// ------------------
		// Room Name
		// ------------------
		if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROOM_NAME, "")) > -1){
			cleanData(true, false);

			// PREFIX: if room name is shown after a move command, it will have a white/black reset prefix...
			pos_data_found_start = checkPrefix("Room Name After Move", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);

			// if no room name yet, process as the welcome msg -
			// we should always have a previous mud room name when inside the game...
			if (mmc.dataRoom.name.length() == 0){
				ommme.notifyMUDWelcome(sbTelnetData.substring(0, pos_data_found_start));
				sbTelnetData.delete(0, pos_data_found_start);
				pos_data_found_start = 0;
			}

			// reset the room data here -
			// mainly need for resetting room description in case it isn't shown...
			mmc.dataRoom = new OMUD_MMUD.DataRoom();
			mmc.dataRoom.name = _sbBlockData.toString();

		// ------------------
		// Items + Hidden (You Notice)
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_YOU_NOTICE_PRE, MSTR_YOU_NOTICE_END)) > -1){
			cleanData(true, false);

			// PREFIX: seaching: if a prefix is found, this was data from a search...
			int pos_data_delete_start_search_check = checkPrefix("You Notice Searched Items", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);
			if (pos_data_found_start > pos_data_delete_start_search_check){
				pos_data_found_start = pos_data_delete_start_search_check;
				mmc.dataRoom.items_hidden = _sbBlockData.toString();
				splitCommaListToArray(mmc.dataRoom.items_hidden, mmc.dataRoom.arrlItemsHidden);
			// NON-SEARCH / visible item listing...
			} else {
				mmc.dataRoom.items = _sbBlockData.toString();						
				splitCommaListToArray(mmc.dataRoom.items, mmc.dataRoom.arrlItems);
			}

		// ------------------
		// Also Here (Units)
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ALSO_HERE_PRE, MSTR_ALSO_HERE_END)) > -1){
			cleanData(true, true); // units (also here) has ANSI
			mmc.dataRoom.units = _sbBlockData.toString();
			splitCommaListToArray(mmc.dataRoom.units, mmc.dataRoom.arrlUnits);

		// ------------------
		// Obvious Exits
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_OBVIOUS_EXITS, "")) > -1){
			cleanData(true, false);

			// PREFIX: if shown after units/'also here', an ansi reset command will be present...
			pos_data_found_start = checkPrefix("Obv Exits After Also Here", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET);

			// build the exit data...
			mmc.dataRoom.exits = _sbBlockData.toString();
			buildRoomExits(mmc);

			// create Megamud RoomID after the exit data is built above...
			mmc.dataRoom.roomID = 
				OMUD_MEGA.getRoomNameHash(mmc.dataRoom.name) + 
				OMUD_MEGA.getRoomExitsCode(mmc.dataRoom.arrlExits);

		// ------------------
		// Light: Dim
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_ROOM_DIMLY_LIT, "")) > -1){
			mmc.dataRoom.light = OMUD_MMUD.eRoomLight.DIMLY_LIT;

		// ------------------
		// Light: Pitch Black
		// ------------------
		// NOTE: **UNTESTED**
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_ROOM_PITCH_BLACK, "")) > -1){
			mmc.dataRoom.light = OMUD_MMUD.eRoomLight.PITCH_BLACK;

		// ------------------
		// Room Description
		// ------------------
		// NOTE: not always shown, depends on verbose/brief setting
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROOM_DESCRIPTION, "")) > -1){
			mmc.dataRoom.desc = _sbBlockData.toString();

		// ------------------
		// Search Revealed None
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_ROOM_SEARCH_NONE, "")) > -1){
			ommme.notifyMUDOther("[ROOM_SEARCH_NONE]\n");
		}

		return pos_data_found_start;
	}

	private void buildRoomExits(OMUD_MMUDChar mmc){
        String[] dirs = mmc.dataRoom.exits.split(",");
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

				mmc.dataRoom.arrlExits.add(new OMUD_MMUD.RoomExit(edir, edoor));
        	}
        }
	}
}