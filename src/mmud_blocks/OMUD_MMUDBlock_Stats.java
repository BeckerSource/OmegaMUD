public class OMUD_MMUDBlock_Stats extends OMUD_MMUDBlocks.Block{
	private final String MSTR_ROW1 = 	"[32mName:";
	private final String MSTR_ROW2 = 	"[32mRace:";
	private final String MSTR_ROW3 = 	"[32mClass:";
	private final String MSTR_ROW4 = 	"[32mHits:";
	private final String MSTR_ROW5 = 	"[32mMana:";
	private final String MSTR_ROW6 = 	"[32m                                       Picklocks:";
	private final String MSTR_ROW7 =	"[32mStrength:";
	private final String MSTR_ROW8 = 	"[32mIntellect:";
	private final String MSTR_ROW9 = 	"[32mWillpower:";
	private final String MSTR_LIVES = 	"Lives/CP:";
	private final String MSTR_EXP = 	"Exp:";
	private final String MSTR_PERC = 	"Perception:";
	private final String MSTR_LEVEL = 	"Level:";
	private final String MSTR_STEALTH = "Stealth:";
	private final String MSTR_AC = 		"Armour Class:";
	private final String MSTR_THIEV = 	"Thievery:";
	private final String MSTR_SC = 		"Spellcasting:";
	private final String MSTR_TRAPS = 	"Traps:";
	private final String MSTR_AGI = 	"Agility:";
	private final String MSTR_TRACK = 	"Tracking:";
	private final String MSTR_HEALTH = 	"Health:";
	private final String MSTR_MA = 		"Martial Arts:";
	private final String MSTR_CHA = 	"Charm:";
	private final String MSTR_MR = 		"MagicRes:";

	public boolean getStatlineWait()				{return true;}
	public OMUD_MMUD.Data.eDataType getDataType()	{return OMUD_MMUD.Data.eDataType.STATS;}
	public OMUD_MMUDBlock_Stats(){
		_arrlCmdText.add(new CmdText("stat", 2));
	}

	public int findBlockData(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		// ------------------
		// Row1: Name + Lives/CP
		// ------------------
		if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW1, "")) > -1){
			cleanData(_sbBlockData, true, true);

			// reset...
			mmc.dataStats = new OMUD_MMUD.DataStats();

			int pos_left  = 0;
			int pos_right = 0;
			// get first name...
			if ((pos_right = _sbBlockData.indexOf(" ", pos_left)) > -1){
				mmc.dataStats.name_first = _sbBlockData.substring(pos_left, pos_right);

				// get last name...
				pos_left = pos_right;
				if ((pos_right = _sbBlockData.indexOf(MSTR_LIVES, pos_right)) > -1){
					mmc.dataStats.name_last = _sbBlockData.substring(pos_left, pos_right).trim(); // could result in empty for no last name

					// get lives/cp...
					String strLivesCP = _sbBlockData.substring(pos_right + MSTR_LIVES.length(), _sbBlockData.length()).trim();
			        String[] tokens = strLivesCP.split("/");
			        if (tokens.length == 2){
			        	mmc.dataStats.lives = Integer.parseInt(tokens[0]);
			        	mmc.dataStats.cp = 	  Integer.parseInt(tokens[1]);
			        }						
				}
			}

		// ------------------
		// Row2: Race + Exp + Perception
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW2, "")) > -1){
			cleanData(_sbBlockData, true, true);

			int pos_left  = 0;
			int pos_right = 0;
			if ((pos_right = _sbBlockData.indexOf(MSTR_EXP, pos_left)) > -1){
				mmc.dataStats.stats_race = _sbBlockData.substring(pos_left, pos_right).trim();

				pos_left = pos_right;
				if ((pos_right = _sbBlockData.indexOf(MSTR_PERC, pos_right)) > -1){
					//mmc.dataStats.exp =  Integer.parseInt(_sbBlockData.substring(pos_left  + MSTR_EXP.length(),  pos_right).trim());
					mmc.dataStats.perc = Integer.parseInt(_sbBlockData.substring(pos_right + MSTR_PERC.length(), _sbBlockData.length()).trim());
				}
			}

		// ------------------
		// Row3: Class + Level + Stealth
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW3, "")) > -1){
			cleanData(_sbBlockData, true, true);

			int pos_left  = 0;
			int pos_right = 0;
			if ((pos_right = _sbBlockData.indexOf(MSTR_LEVEL, pos_left)) > -1){
				mmc.dataStats.stats_class = _sbBlockData.substring(pos_left, pos_right).trim();

				pos_left = pos_right;
				if ((pos_right = _sbBlockData.indexOf(MSTR_STEALTH, pos_right)) > -1){
					mmc.dataStats.level =  	Integer.parseInt(_sbBlockData.substring(pos_left  + MSTR_LEVEL.length(),   pos_right).trim());
					mmc.dataStats.stealth = Integer.parseInt(_sbBlockData.substring(pos_right + MSTR_STEALTH.length(), _sbBlockData.length()).trim());
				}
			}

		// ------------------
		// Row4: Hits + AC + Thievery
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW4, "")) > -1){
			cleanData(_sbBlockData, true, true);

			int pos_left  = 0;
			int pos_right = 0;
			if ((pos_right = _sbBlockData.indexOf(MSTR_AC, pos_left)) > -1){
				String strHits = _sbBlockData.substring(pos_left, pos_right).trim();
				// check for modifier...
				if (strHits.charAt(0) == '*')
					strHits = strHits.substring(1, strHits.length()).trim();
		        String[] tokens = strHits.split("/");
		        if (tokens.length == 2){
					mmc.dataStats.hp_cur = Integer.parseInt(tokens[0]);
					mmc.dataStats.hp_max = Integer.parseInt(tokens[1]);
				}

				pos_left = pos_right;
				if ((pos_right = _sbBlockData.indexOf(MSTR_THIEV, pos_right)) > -1){
					String strAC = _sbBlockData.substring(pos_left + MSTR_AC.length(), pos_right).trim();
			        tokens = strAC.split("/");
			        if (tokens.length == 2){
						mmc.dataStats.ac_ac =   Integer.parseInt(tokens[0]);
						mmc.dataStats.ac_accy = Integer.parseInt(tokens[1]);
			        }
					mmc.dataStats.thievery = Integer.parseInt(_sbBlockData.substring(pos_right + MSTR_THIEV.length(), _sbBlockData.length()).trim());
				}
			}

		// ------------------
		// Row5: Mana + SC + Traps
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW5, "")) > -1){
			cleanData(_sbBlockData, true, true);

			int pos_left  = 0;
			int pos_right = 0;
			if ((pos_right = _sbBlockData.indexOf(MSTR_SC, pos_left)) > -1){
				String strMana = _sbBlockData.substring(pos_left, pos_right).trim();
				// check for modifier...
				if (strMana.charAt(0) == '*')
					strMana = strMana.substring(1, strMana.length()).trim();
		        String[] tokens = strMana.split("/");
		        if (tokens.length == 2){
					mmc.dataStats.ma_cur = Integer.parseInt(tokens[0]);
					mmc.dataStats.ma_max = Integer.parseInt(tokens[1]);
				}

				pos_left = pos_right;
				if ((pos_right = _sbBlockData.indexOf(MSTR_TRAPS, pos_left)) > -1){
					mmc.dataStats.sc = 		Integer.parseInt(_sbBlockData.substring(pos_left  + MSTR_SC.length(), 	 pos_right).trim());
					mmc.dataStats.traps = 	Integer.parseInt(_sbBlockData.substring(pos_right + MSTR_TRAPS.length(), _sbBlockData.length()).trim());
				}
			}

		// ------------------
		// Row6: Picklocks
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW6, "")) > -1){
			cleanData(_sbBlockData, true, true);
			mmc.dataStats.pick = Integer.parseInt(_sbBlockData.toString().trim());

		// ------------------
		// Row7: Str + Agil + Tracking
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW7, "")) > -1){
			cleanData(_sbBlockData, true, true);

			int pos_left  = 0;
			int pos_right = 0;
			if ((pos_right = _sbBlockData.indexOf(MSTR_AGI, pos_left)) > -1){
				mmc.dataStats.str = Integer.parseInt(_sbBlockData.substring(pos_left, pos_right).trim());

				pos_left = pos_right;
				if ((pos_right = _sbBlockData.indexOf(MSTR_TRACK, pos_right)) > -1){
					mmc.dataStats.agi =   Integer.parseInt(_sbBlockData.substring(pos_left  + MSTR_AGI.length(),   pos_right).trim());
					mmc.dataStats.track = Integer.parseInt(_sbBlockData.substring(pos_right + MSTR_TRACK.length(), _sbBlockData.length()).trim());
				}
			}

		// ------------------
		// Row8: Int + Health + MA
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW8, "")) > -1){
			cleanData(_sbBlockData, true, true);

			int pos_left  = 0;
			int pos_right = 0;
			if ((pos_right = _sbBlockData.indexOf(MSTR_HEALTH, pos_left)) > -1){
				mmc.dataStats.intel = Integer.parseInt(_sbBlockData.substring(pos_left, pos_right).trim());

				pos_left = pos_right;
				if ((pos_right = _sbBlockData.indexOf(MSTR_MA, pos_right)) > -1){
					mmc.dataStats.hea = Integer.parseInt(_sbBlockData.substring(pos_left  + MSTR_HEALTH.length(),	pos_right).trim());
					mmc.dataStats.ma =  Integer.parseInt(_sbBlockData.substring(pos_right + MSTR_MA.length(), 	    _sbBlockData.length()).trim());
				}
			}

		// ------------------
		// Row9: Wil + Charm + MR
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW9, "")) > -1){
			cleanData(_sbBlockData, true, true);

			int pos_left  = 0;
			int pos_right = 0;
			if ((pos_right = _sbBlockData.indexOf(MSTR_CHA, pos_left)) > -1){
				mmc.dataStats.wil = Integer.parseInt(_sbBlockData.substring(pos_left, pos_right).trim());

				pos_left = pos_right;
				if ((pos_right = _sbBlockData.indexOf(MSTR_MR, pos_right)) > -1){
					mmc.dataStats.cha = Integer.parseInt(_sbBlockData.substring(pos_left  + MSTR_CHA.length(),	pos_right).trim());
					mmc.dataStats.mr =  Integer.parseInt(_sbBlockData.substring(pos_right + MSTR_MR.length(), 	_sbBlockData.length()).trim());
				}
			}
		}

		return pos_data_found_start;
	}
}
