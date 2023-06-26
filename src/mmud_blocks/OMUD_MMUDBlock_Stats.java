public class OMUD_MMUDBlock_Stats extends OMUD_MMUDBlocks.Block{
	private final String MSTR_PREFIX_RESET_WHBL = "[0;37;40m";
	private final String MSTR_STATS_PRE = 	"[79D[K[0m[32mName:";
	private final String MSTR_STATS_END = 	"[0;32mMagicRes:";
	private final String MSTR_LIVES = 		"Lives/CP:";
	private final String MSTR_RACE = 		"Race:";
	private final String MSTR_EXP = 		"Exp:";
	private final String MSTR_PERC = 		"Perception:";
	private final String MSTR_CLASS = 		"Class:";
	private final String MSTR_LEVEL = 		"Level:";
	private final String MSTR_STEALTH = 	"Stealth:";
	private final String MSTR_HITS = 		"Hits:";
	private final String MSTR_AC = 			"Armour Class:";
	private final String MSTR_THIEVERY = 	"Thievery:";
	private final String MSTR_MANA = 		"Mana:";
	private final String MSTR_KAI = 		"Kai:";
	private final String MSTR_SC = 			"Spellcasting:";
	private final String MSTR_TRAPS = 		"Traps:";
	private final String MSTR_PICK = 		"Picklocks:";
	private final String MSTR_STR =  		"Strength:";
	private final String MSTR_AGI =  		"Agility:";
	private final String MSTR_TRACK =  		"Tracking:";
	private final String MSTR_INTEL =		"Intellect:";
	private final String MSTR_HEA =			"Health:";
	private final String MSTR_MA =			"Martial Arts:";
	private final String MSTR_WIL =			"Willpower:";
	private final String MSTR_CHA =			"Charm:";
	private final String MSTR_MR = 			"    [36m";

	public boolean getStatlineWait()				{return true;}
	public OMUD_MMUD.Data.eDataType getDataType()	{return OMUD_MMUD.Data.eDataType.STATS;}
	public OMUD_MMUDBlock_Stats(){
		_arrlCmdText.add(new CmdText("stat", 2));
	}

	public int findBlockData(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		if ((pos_data_found_start = findData(sbTelnetData, pos_offset, false, true, MSTR_STATS_PRE, MSTR_STATS_END)) > -1){	
			cleanData(_sbBlockData, true, true);
			mmc.dataStats = new OMUD_MMUD.DataStats();

			// PREFIX: normal stat command has a prefix, coming from a new char creation does not...
			pos_data_found_start = checkPrefix("Stat Command (not new char)", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);

			int pos_left =  0;
			int pos_right = _sbBlockData.length() - 1;

			// ------------------
			// Row9: MR
			// ------------------
			// special: MR is further in the buffer, so get it here separately...
			if ((pos_left  = sbTelnetData.indexOf(MSTR_MR, pos_data_found_start)) > -1 &&
				(pos_right = sbTelnetData.indexOf("\n", pos_left)) > -1){
				StringBuilder sbMR = new StringBuilder(sbTelnetData.substring(pos_left + MSTR_MR.length(), pos_right));
				cleanData(sbMR, true, true);
				mmc.dataStats.mr = Integer.parseInt(sbMR.toString());
				sbTelnetData.delete(pos_data_found_start, pos_right + 1);
			}
			pos_right = _sbBlockData.length() - 1;

			// ------------------
			// Row9: Wil + Charm
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_CHA, pos_right)) > -1){
	        	mmc.dataStats.cha = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_CHA.length(), pos_right + 1).trim());
	        	pos_right = pos_left - 1;

				if ((pos_left = _sbBlockData.lastIndexOf(MSTR_WIL, pos_right)) > -1){
		        	mmc.dataStats.wil = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_WIL.length(), pos_right + 1).trim());
		        	pos_right = pos_left - 1;
				}
			}

			// ------------------
			// Row8: Int + Health + MA
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_MA, pos_right)) > -1){
	        	mmc.dataStats.ma = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_MA.length(), pos_right + 1).trim());
	        	pos_right = pos_left - 1;

				if ((pos_left = _sbBlockData.lastIndexOf(MSTR_HEA, pos_right)) > -1){
		        	mmc.dataStats.hea = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_HEA.length(), pos_right + 1).trim());
		        	pos_right = pos_left - 1;

					if ((pos_left = _sbBlockData.lastIndexOf(MSTR_INTEL, pos_right)) > -1){
			        	mmc.dataStats.intel = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_INTEL.length(), pos_right + 1).trim());
			        	pos_right = pos_left - 1;
					}
				}
			}

			// ------------------
			// Row7: Str + Agil + Tracking
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_TRACK, pos_right)) > -1){
	        	mmc.dataStats.track = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_TRACK.length(), pos_right + 1).trim());
	        	pos_right = pos_left - 1;

				if ((pos_left = _sbBlockData.lastIndexOf(MSTR_AGI, pos_right)) > -1){
		        	mmc.dataStats.agi = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_AGI.length(), pos_right + 1).trim());
		        	pos_right = pos_left - 1;

					if ((pos_left = _sbBlockData.lastIndexOf(MSTR_STR, pos_right)) > -1){
			        	mmc.dataStats.str = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_STR.length(), pos_right + 1).trim());
			        	pos_right = pos_left - 1;
					}
				}
			}

			// ------------------
			// Row6: Picklocks
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_PICK, pos_right)) > -1){
	        	mmc.dataStats.pick = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_PICK.length(), pos_right + 1).trim());
	        	pos_right = pos_left - 1;
	        }

			// ------------------
			// Row5: Mana/Kai + SC + Traps
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_TRAPS, pos_right)) > -1){
	        	mmc.dataStats.traps = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_TRAPS.length(), pos_right + 1).trim());
	        	pos_right = pos_left - 1;

				if ((pos_left = _sbBlockData.lastIndexOf(MSTR_SC, pos_right)) > -1){
		        	mmc.dataStats.sc = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_SC.length(), pos_right + 1).trim());
		        	pos_right = pos_left - 1;
		        }

				if ((pos_left = _sbBlockData.lastIndexOf(MSTR_MANA, pos_right)) > -1 ||
					(pos_left = _sbBlockData.lastIndexOf(MSTR_KAI,  pos_right)) > -1){

		        	int pos_colon = _sbBlockData.indexOf(":", pos_left);
					String strMana = _sbBlockData.substring(pos_colon + 1, pos_right + 1).trim();
					// check for modifier...
					if ((mmc.dataStatline.ma_mod = strMana.charAt(0) == '*'))
						strMana = strMana.substring(1, strMana.length()).trim();
			        String[] tokens = strMana.split("/");
			        if (tokens.length == 2){
						mmc.dataStatline.ma_cur = Integer.parseInt(tokens[0]);
						mmc.dataStatline.ma_max = Integer.parseInt(tokens[1]);
					}
		        	pos_right = pos_left - 1;
				}
			}

			// ------------------
			// Row4: Hits + AC + Thievery
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_THIEVERY, pos_right)) > -1){
	        	mmc.dataStats.thievery = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_THIEVERY.length(), pos_right + 1).trim());
	        	pos_right = pos_left - 1;

				if ((pos_left = _sbBlockData.lastIndexOf(MSTR_AC, pos_right)) > -1){
			        String[] tokens = _sbBlockData.substring(pos_left + MSTR_AC.length(), pos_right + 1).trim().split("/");
			        if (tokens.length == 2){
						mmc.dataStats.ac_ac =   Integer.parseInt(tokens[0]);
						mmc.dataStats.ac_accy = Integer.parseInt(tokens[1]);
			        }
		        	pos_right = pos_left - 1;

					if ((pos_left = _sbBlockData.lastIndexOf(MSTR_HITS, pos_right)) > -1){
						String strHits = _sbBlockData.substring(pos_left + MSTR_HITS.length(), pos_right + 1).trim();
						// check for modifier...
						if ((mmc.dataStatline.hp_mod = strHits.charAt(0) == '*'))
							strHits = strHits.substring(1, strHits.length()).trim();
				        tokens = strHits.split("/");
				        if (tokens.length == 2){
							mmc.dataStatline.hp_cur = Integer.parseInt(tokens[0]);
							mmc.dataStatline.hp_max = Integer.parseInt(tokens[1]);
						}						
			        	pos_right = pos_left - 1;
					}
		        }
		    }

			// ------------------
			// Row3: Class + Level + Stealth
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_STEALTH, pos_right)) > -1){
	        	mmc.dataStats.stealth = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_STEALTH.length(), pos_right + 1).trim());
	        	pos_right = pos_left - 1;

				if ((pos_left = _sbBlockData.lastIndexOf(MSTR_LEVEL, pos_right)) > -1){
		        	mmc.dataStats.level = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_LEVEL.length(), pos_right + 1).trim());
		        	pos_right = pos_left - 1;

					if ((pos_left = _sbBlockData.lastIndexOf(MSTR_CLASS, pos_right)) > -1){
			        	mmc.dataStats.stats_class = _sbBlockData.substring(pos_left + MSTR_CLASS.length(), pos_right + 1).trim();
			        	pos_right = pos_left - 1;
					}
				}
			}

			// ------------------
			// Row2: Race + Exp + Perception
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_PERC, pos_right)) > -1){
	        	mmc.dataStats.perc = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_PERC.length(), pos_right + 1).trim());
	        	pos_right = pos_left - 1;

				if ((pos_left = _sbBlockData.lastIndexOf(MSTR_EXP, pos_right)) > -1){
					// don't bother getting XP here - we get it from the exp command
		        	pos_right = pos_left - 1;

					if ((pos_left = _sbBlockData.lastIndexOf(MSTR_RACE, pos_right)) > -1){
			        	mmc.dataStats.stats_race = _sbBlockData.substring(pos_left + MSTR_RACE.length(), pos_right + 1).trim();
			        	pos_right = pos_left - 1;
					}
				}
			}

			// ------------------
			// Row1: Name + Lives/CP
			// ------------------
			if ((pos_left = _sbBlockData.lastIndexOf(MSTR_LIVES, pos_right)) > -1){
				// get lives/cp...
		        String[] tokens = _sbBlockData.substring(pos_left + MSTR_LIVES.length(), pos_right + 1).trim().split("/");
		        if (tokens.length == 2){
		        	mmc.dataStats.lives = Integer.parseInt(tokens[0]);
		        	mmc.dataStats.cp = 	  Integer.parseInt(tokens[1]);
		        }						
	        	pos_right = pos_left - 1;

				// found a last name if split...
				mmc.dataStats.name_first = _sbBlockData.substring(0, pos_right + 1).trim();
		        tokens = mmc.dataStats.name_first.split(" ");
		        if (tokens.length == 2){
		        	mmc.dataStats.name_first = tokens[0];
		        	mmc.dataStats.name_last  = tokens[1];
		        }
			}
		}

		return pos_data_found_start;
	}
}
