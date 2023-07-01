public class OMUD_MMUDBlock_Room extends OMUD_MMUDBlocks.Block{
	private final String MSTR_PREFIX_RESET_WHBL =  	"[0;37;40m";
	private final String MSTR_ROOM_NAME =  			"[79D[K[1;36m";
	private final String MSTR_OBVIOUS_EXITS =  		"Obvious exits: ";
	private final String MSTR_ROOM_DESC =  			"    ";
	private final String MSTR_YOU_NOTICE_PRE =  	"You notice ";
	private final String MSTR_YOU_NOTICE_END =  	"here.";
	private final String MSTR_ALSO_HERE_PRE =  		"Also here: ";
	private final String MSTR_ALSO_HERE_END =  		".";
	private final String MSTR_LIGHT_DIM =  			"The room is dimly lit";
	private final String MSTR_LIGHT_DARK =  		"The room is very dark";
	private final String MSTR_LIGHT_BARELY =  		"The room is barely visible";
	private final String MSTR_LIGHT_BLACK =  		"The room is pitch black";
	private final String MSTR_SEARCH_NONE = 		"[0;36mYour search revealed nothing.";
	private final String MSTR_SEARCH_PRE =  		"[0;37;40m[0;36mYou notice ";

	public boolean getStatlineWait()				{return true;}
	public OMUD_MMUD.DataBlock.eBlockType getDataType()	{return OMUD_MMUD.DataBlock.eBlockType.ROOM;}
	public OMUD_MMUDBlock_Room(){
		_arrlCmdText.add(new CmdText("look", 	0)); // 0-len covers LF/enter only (zero-len) and all chars as part of look
		_arrlCmdText.add(new CmdText("search", 	3)); // only "sea" is required
	}

	public int findBlockData(OMUD_IMUDEvents omme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROOM_NAME, "")) > -1){
			cleanData(_sbBlockData, true, true);
			mmc.dataRoom.resetOptional();

			int pos_left  = 0;
			int pos_right = _sbBlockData.length() - 1;

			// ------------------
			// Obvious Exits
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_OBVIOUS_EXITS, pos_right)) > -1){
				// ------------------
				// Optional: Light
				// ------------------
					 if ((pos_right = _sbBlockData.indexOf(MSTR_LIGHT_DIM, 		pos_left)) 	> -1)
					mmc.dataRoom.light = OMUD_MMUD.eRoomLight.DIMLY_LIT;
				else if ((pos_right = _sbBlockData.indexOf(MSTR_LIGHT_DARK, 	pos_left)) 	> -1)
					mmc.dataRoom.light = OMUD_MMUD.eRoomLight.VERY_DARK;
				else if ((pos_right = _sbBlockData.indexOf(MSTR_LIGHT_BARELY, 	pos_left)) 	> -1)
					mmc.dataRoom.light = OMUD_MMUD.eRoomLight.BARELY_VIS;
				else if ((pos_right = _sbBlockData.indexOf(MSTR_LIGHT_BLACK, 	pos_left)) 	> -1)
					mmc.dataRoom.light = OMUD_MMUD.eRoomLight.PITCH_BLACK;
				// if no light string, set right to the end...
				if (pos_right == -1)
					 pos_right = _sbBlockData.length() - 1;
				// else move back one to be at the end of the exits...
				else pos_right--;

				// get exits here...
				mmc.dataRoom.exits = _sbBlockData.substring(pos_left + MSTR_OBVIOUS_EXITS.length(), pos_right + 1).trim();
				buildRoomExits(mmc.dataRoom);
				pos_right = pos_left - 1;
			} // else shouldn't happen

			// ------------------
			// Optional: Also Here (Units)
			// ------------------
			int pos_right_prev = pos_right;
			if ((pos_right = _sbBlockData.lastIndexOf(MSTR_ALSO_HERE_END, pos_right)) > -1 &&
				(pos_left  = _sbBlockData.lastIndexOf(MSTR_ALSO_HERE_PRE, pos_right)) > -1){
	        	mmc.dataRoom.units = _sbBlockData.substring(pos_left + MSTR_ALSO_HERE_PRE.length(), pos_right).trim();
				splitCommaListToArray(mmc.dataRoom.units, mmc.dataRoom.arrlUnits);
	        	pos_right = pos_left - 1;
	        } else pos_right = pos_right_prev;

			// ------------------
			// Optional: Items (You Notice)
			// ------------------
			pos_right_prev = pos_right;
			if ((pos_right = _sbBlockData.lastIndexOf(MSTR_YOU_NOTICE_END, pos_right)) > -1 &&
				(pos_left  = _sbBlockData.lastIndexOf(MSTR_YOU_NOTICE_PRE, pos_right)) > -1){
	        	mmc.dataRoom.items = _sbBlockData.substring(pos_left + MSTR_YOU_NOTICE_PRE.length(), pos_right).trim();
				splitCommaListToArray(mmc.dataRoom.items, mmc.dataRoom.arrlItems);
	        	pos_right = pos_left - 1;
	        } else pos_right = pos_right_prev;

			// ------------------
			// Optional: Room Description
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_ROOM_DESC, pos_right)) > -1){
	        	mmc.dataRoom.desc = _sbBlockData.substring(pos_left + MSTR_ROOM_DESC.length(), pos_right + 1).trim();
	        	pos_right = pos_left - 1;
	        }

			// ------------------
			// Room Name
			// ------------------
			// get the welcome message if we don't have a room name yet (is above the room name after entering)...
			if (mmc.dataRoom.name.length() == 0){
				StringBuilder sbWelcome = new StringBuilder(sbTelnetData.substring(0, pos_data_found_start));
				cleanData(sbWelcome, false, true);
				omme.notifyMUDWelcome(sbWelcome.toString().trim());
				sbTelnetData.delete(0, pos_data_found_start);
				pos_data_found_start = 0;
			// PREFIX: if room name is shown after a move command, it will have a white/black reset prefix...
			} else {
				pos_data_found_start = checkPrefix("Room Name After Move", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);
			}

        	mmc.dataRoom.name = _sbBlockData.substring(0, pos_right + 1).trim();
			// create Megamud RoomID after the exit data is built above...
			mmc.dataRoom.roomID = 
				OMUD_MEGA.getRoomNameHash(mmc.dataRoom.name) + 
				OMUD_MEGA.getRoomExitsCode(mmc.dataRoom.arrlExits);

		// ------------------
		// Search: Found Items
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_SEARCH_PRE, MSTR_YOU_NOTICE_END)) > -1){
			cleanData(_sbBlockData, true, false);
			mmc.dataRoom.items_hidden = _sbBlockData.toString();
			splitCommaListToArray(mmc.dataRoom.items_hidden, mmc.dataRoom.arrlItemsHidden);

		// ------------------
		// Search: No Items
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_SEARCH_NONE, "")) > -1){
			omme.notifyMUDOther("[ROOM_SEARCH_NONE]\n");
		}			

		return pos_data_found_start;
	}

	private void buildRoomExits(OMUD_MMUD.DataRoom dr){
		dr.arrlExits.clear();

        String[] dirs = dr.exits.split(",");
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

				dr.arrlExits.add(new OMUD_MMUD.RoomExit(edir, edoor));
        	}
        }
	}
}
