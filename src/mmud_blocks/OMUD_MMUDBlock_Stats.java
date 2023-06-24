public class OMUD_MMUDBlock_Stats extends OMUD_MMUDBlocks.Block{
	private final String MSTR_NAME = 	"[0;37;40m[79D[K[0m[32mName:";
	private final String MSTR_LIVES = 	"[32mLives/CP:";
	private final String MSTR_RACE = 	"[32mRace:";
	private final String MSTR_EXP = 	"[32mExp:";
	private final String MSTR_PERC = 	"[32mPerception:";
	private final String MSTR_CLASS = 	"[32mClass:";
	private final String MSTR_LEVEL = 	"[32mLevel:";
	private final String MSTR_STEALTH = "[32mStealth:";
	private final String MSTR_HITS = 	"[32mHits:";
	private final String MSTR_AC = 		"[32mArmour Class:";
	private final String MSTR_THIEV = 	"[32mThievery:";
	private final String MSTR_MANA = 	"[32mMana:";
	private final String MSTR_KAI = 	"[32mKai:";
	private final String MSTR_SC = 		"[32mSpellcasting:";
	private final String MSTR_TRAPS = 	"[32mTraps:";
	private final String MSTR_PICK = 	"[32m                                       Picklocks:";
	private final String MSTR_STR =  	"[32mStrength:";
	private final String MSTR_AGI =  	"[0;32mAgility:";
	private final String MSTR_TRACK =  	"[0;32mTracking:";
	private final String MSTR_INTEL =	"[32mIntellect:";
	private final String MSTR_HEA =		"[0;32mHealth:";
	private final String MSTR_MA =		"[0;32mMartial Arts:";
	private final String MSTR_WIL =		"[32mWillpower:";
	private final String MSTR_CHA =		"[0;32mCharm:";
	private final String MSTR_MR =		"[0;32mMagicRes:";

	public boolean getStatlineWait()				{return true;}
	public OMUD_MMUD.Data.eDataType getDataType()	{return OMUD_MMUD.Data.eDataType.STATS;}
	public OMUD_MMUDBlock_Stats(){
		_arrlCmdText.add(new CmdText("stat", 2));
	}

	public int findBlockData(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset, boolean is_matched){
		int pos_data_found_start = -1;

		// ------------------
		// Row9: Wil + Charm + MR
		// ------------------
		if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_MR, "")) > -1){	
			mmc.dataStats = new OMUD_MMUD.DataStats();

			cleanData(_sbBlockData, true, true);
			mmc.dataStats.mr = Integer.parseInt(_sbBlockData.toString());

	        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_CHA, "")) > -1){
	        	cleanData(_sbBlockData, true, true);
	        	mmc.dataStats.cha = Integer.parseInt(_sbBlockData.toString());
	        }

	        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_WIL, "")) > -1){
	        	cleanData(_sbBlockData, true, true);
	        	mmc.dataStats.wil = Integer.parseInt(_sbBlockData.toString());
	        }

			// ------------------
			// Row8: Int + Health + MA
			// ------------------
			if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, true, true, MSTR_MA, "")) > -1){
				cleanData(_sbBlockData, true, true);
				mmc.dataStats.ma = Integer.parseInt(_sbBlockData.toString());

		        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_HEA, "")) > -1){
		        	cleanData(_sbBlockData, true, true);
		        	mmc.dataStats.hea = Integer.parseInt(_sbBlockData.toString());
		        }

		        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_INTEL, "")) > -1){
		        	cleanData(_sbBlockData, true, true);
		        	mmc.dataStats.intel = Integer.parseInt(_sbBlockData.toString());
		        }
		    }

			// ------------------
			// Row7: Str + Agil + Tracking
			// ------------------
			if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, true, true, MSTR_TRACK, "")) > -1){
				cleanData(_sbBlockData, true, true);
				mmc.dataStats.track = Integer.parseInt(_sbBlockData.toString());

		        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_AGI, "")) > -1){
		        	cleanData(_sbBlockData, true, true);
		        	mmc.dataStats.agi = Integer.parseInt(_sbBlockData.toString());
		        }

		        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_STR, "")) > -1){
		        	cleanData(_sbBlockData, true, true);
		        	mmc.dataStats.str = Integer.parseInt(_sbBlockData.toString());
		        }
		    }

			// ------------------
			// Row6: Picklocks
			// ------------------
			if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, true, true, MSTR_PICK, "")) > -1){
				cleanData(_sbBlockData, true, true);
				mmc.dataStats.pick = Integer.parseInt(_sbBlockData.toString());
			}

			// ------------------
		    // Rows 4 + 5
		    // NOTE: search backwards from Traps because non-casters just have a LF without an ANSI break from Thiev to Traps
			// ------------------
			// Row5: Mana/Kai + SC + Traps
			// ------------------
			if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, true, true, MSTR_TRAPS, "")) > -1){
				cleanData(_sbBlockData, true, true);
				mmc.dataStats.traps = Integer.parseInt(_sbBlockData.toString());

				// vars stuff to compensate for optional lines...
				pos_data_found_start--;
				int pos_data_found_caster = -1;

		        if ((pos_data_found_caster = findData(sbTelnetData, pos_data_found_start, false, true, MSTR_SC, "")) > -1){
					cleanData(_sbBlockData, true, true);
					mmc.dataStats.sc = Integer.parseInt(_sbBlockData.toString());
					pos_data_found_start = --pos_data_found_caster;					
				}

				if ((pos_data_found_caster = findData(sbTelnetData, pos_data_found_start, false, true, MSTR_MANA, "")) > -1 ||
					(pos_data_found_caster = findData(sbTelnetData, pos_data_found_start, false, true, MSTR_KAI,  "")) > -1){
					cleanData(_sbBlockData, true, true);

					String strMana = _sbBlockData.toString();
					// check for modifier...
					if ((mmc.dataStatline.ma_mod = strMana.charAt(0) == '*'))
						strMana = strMana.substring(1, strMana.length()).trim();
			        String[] tokens = strMana.split("/");
			        if (tokens.length == 2){
						mmc.dataStatline.ma_cur = Integer.parseInt(tokens[0]);
						mmc.dataStatline.ma_max = Integer.parseInt(tokens[1]);
					}
					pos_data_found_start = --pos_data_found_caster;
				}

				// ------------------
				// Row4: Hits + AC + Thievery
				// ------------------
				if ((pos_data_found_start = findData(sbTelnetData, pos_data_found_start, true, true, MSTR_THIEV, "")) > -1){
					cleanData(_sbBlockData, true, true);
					mmc.dataStats.thievery = Integer.parseInt(_sbBlockData.toString());

			        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_AC, "")) > -1){
						cleanData(_sbBlockData, true, true);

						String strAC = _sbBlockData.toString();
				        String[] tokens = strAC.split("/");
				        if (tokens.length == 2){
							mmc.dataStats.ac_ac =   Integer.parseInt(tokens[0]);
							mmc.dataStats.ac_accy = Integer.parseInt(tokens[1]);
				        }
			        }

			        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_HITS, "")) > -1){
						cleanData(_sbBlockData, true, true);

						String strHits = _sbBlockData.toString();
						// check for modifier...
						if ((mmc.dataStatline.hp_mod = strHits.charAt(0) == '*'))
							strHits = strHits.substring(1, strHits.length()).trim();
				        String[] tokens = strHits.split("/");
				        if (tokens.length == 2){
							mmc.dataStatline.hp_cur = Integer.parseInt(tokens[0]);
							mmc.dataStatline.hp_max = Integer.parseInt(tokens[1]);
						}
			        }
			    }
			}

			// ------------------
			// Row3: Class + Level + Stealth
			// ------------------
			if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, true, true, MSTR_STEALTH, "")) > -1){
				cleanData(_sbBlockData, true, true);
				mmc.dataStats.stealth = Integer.parseInt(_sbBlockData.toString());

		        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_LEVEL, "")) > -1){
		        	cleanData(_sbBlockData, true, true);
		        	mmc.dataStats.level = Integer.parseInt(_sbBlockData.toString());
		        }

		        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_CLASS, "")) > -1){
		        	cleanData(_sbBlockData, true, true);
		        	mmc.dataStats.stats_class = _sbBlockData.toString();
		        }
		    }

			// ------------------
			// Row2: Race + Exp + Perception
			// ------------------
			if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, true, true, MSTR_PERC, "")) > -1){
				cleanData(_sbBlockData, true, true);
				mmc.dataStats.perc = Integer.parseInt(_sbBlockData.toString());

		        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_EXP, "")) > -1){
		        	cleanData(_sbBlockData, true, true);
		        	mmc.dataExp.exp_total = Integer.parseInt(_sbBlockData.toString());
		        }

		        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_RACE, "")) > -1){
		        	cleanData(_sbBlockData, true, true);
		        	mmc.dataStats.stats_race = _sbBlockData.toString();
		        }
		    }

			// ------------------
			// Row1: Name + Lives/CP
			// ------------------
			if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, true, true, MSTR_LIVES, "")) > -1){
				cleanData(_sbBlockData, true, true);

				// get lives/cp...
		        String[] tokens = _sbBlockData.toString().split("/");
		        if (tokens.length == 2){
		        	mmc.dataStats.lives = Integer.parseInt(tokens[0]);
		        	mmc.dataStats.cp = 	  Integer.parseInt(tokens[1]);
		        }						

		        // get name...
		        if ((pos_data_found_start = findData(sbTelnetData, --pos_data_found_start, false, true, MSTR_NAME, "")) > -1){
					cleanData(_sbBlockData, true, true);

					// found a last name if split...
					mmc.dataStats.name_first = _sbBlockData.toString();
			        tokens = mmc.dataStats.name_first.split(" ");
			        if (tokens.length == 2){
			        	mmc.dataStats.name_first = tokens[0];
			        	mmc.dataStats.name_last  = tokens[1];
			        }						
		        }
			}
		}

		return pos_data_found_start;
	}
}
