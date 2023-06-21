public class OMUD_MMUDBlock_Stats extends OMUD_MMUDBlocks.Block{
	private final String MSTR_ROW1 = 	"[0;37;40m[79D[K[0m[32mName: ";
	private final String MSTR_ROW2 = 	"[32mRace: ";
	private final String MSTR_ROW3 = 	"[32mClass: ";
	private final String MSTR_ROW4 = 	"[32mHits: ";
	private final String MSTR_ROW5 = 	"[32mMana: ";
	private final String MSTR_ROW6 = 	"[32m                                       Picklocks:   ";
	private final String MSTR_ROW7 =	"[32mStrength: ";
	private final String MSTR_ROW8 = 	"[32mIntellect:";
	private final String MSTR_ROW9 = 	"[32mWillpower:";

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
			cleanData(false, true);
			mmc.dataStats.name_first = _sbBlockData.toString();

		// ------------------
		// Row2: Race + Exp + Perception
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW2, "")) > -1){
			cleanData(false, true);
			mmc.dataStats.name_first += "\n" + _sbBlockData.toString();

		// ------------------
		// Row3: Class + Level + Stealth
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW3, "")) > -1){
			cleanData(false, true);
			mmc.dataStats.name_first += "\n" + _sbBlockData.toString();

		// ------------------
		// Row4: Hits + AC + Thievery
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW4, "")) > -1){
			cleanData(false, true);
			mmc.dataStats.name_first += "\n" + _sbBlockData.toString();

		// ------------------
		// Row5: Mana + SC + Traps
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW5, "")) > -1){
			cleanData(false, true);
			mmc.dataStats.name_first += "\n" + _sbBlockData.toString();

		// ------------------
		// Row6: Picklocks
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW6, "")) > -1){
			cleanData(false, true);
			mmc.dataStats.name_first += "\n" + _sbBlockData.toString();

		// ------------------
		// Row7: Str + Agil + Tracking
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW7, "")) > -1){
			cleanData(false, true);
			mmc.dataStats.name_first += "\n" + _sbBlockData.toString();

		// ------------------
		// Row8: Int + Health + MA
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW8, "")) > -1){
			cleanData(false, true);
			mmc.dataStats.name_first += "\n" + _sbBlockData.toString();

		// ------------------
		// Row9: Wil + Charm + MR
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROW9, "")) > -1){
			cleanData(false, true);
			mmc.dataStats.name_first += "\n" + _sbBlockData.toString();
		}

		return pos_data_found_start;
	}
}

/*
[0;37;40m[79D[K[0m[32mName: [36mChartest                         [32mLives/CP:  [36m    9/0    
[32mRace: [36mWood-Elf    [32mExp: [36m0               [32mPerception:  [36m   65
[32mClass: [36mMage       [32mLevel: [36m1             [32mStealth:     [36m   38
[32mHits: [36m   35/35    [32mArmour Class: [36m  0/0  [32mThievery:    [36m    0
[32mMana: [1;31m*[0;36m  32/32    [32mSpellcasting: [36m60     [32mTraps:       [36m    0
[32m                                       Picklocks:   [36m    0
[32mStrength: [36m 40     [0;32mAgility:[36m 50          [0;32mTracking:    [36m    0
[32mIntellect:[36m 70     [0;32mHealth: [36m 60          [0;32mMartial Arts:[36m   19
[32mWillpower:[36m 50     [0;32mCharm:  [36m 70          [0;32mMagicRes:    [36m   55
*/