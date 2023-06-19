public class OMUD_MMUDBlock_Inv extends OMUD_MMUDBlocks.Block{
	private final String MSTR_ITEMS = 		"[0;37;40m[0;37;40m[79D[KYou are carrying ";
	private final String MSTR_KEYS_PRE =  	"[79D[K[0;37;40mYou have the following keys:  ";
	private final String MSTR_KEYS_END =  	".";
	private final String MSTR_WEALTH =  	"[0;32mWealth: [36m";
	private final String MSTR_ENC_PRE =  	"[0;32mEncumbrance: [36m";
	private final String MSTR_ENC_END =  	"[0m";

	private OMUD_MMUD.DataInv _dataInv = null;

	public OMUD_MMUDBlock_Inv(){
		_dataInv = new OMUD_MMUD.DataInv();
		_arrlCmdText.add(new CmdText("i", 		  1));
		_arrlCmdText.add(new CmdText("inventory", 4)); // "inve" is min ("in" and "inv" conflict with "invite" so are ignored in mud)
	}

	public int findBlockData(OMUD_IMUDEvents ommme, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		// ------------------
		// Items
		// ------------------
		//if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ROOM_NAME, "")) > -1){
		//} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_YOU_NOTICE_PRE, MSTR_YOU_NOTICE_END)) > -1){
		//}

		return pos_data_found_start;
	}

	public void resetData(){
		_dataInv = new OMUD_MMUD.DataInv();	
	}

	public void notifyEvents(OMUD_IMUDEvents ommme){
		ommme.notifyMUDInv(new OMUD_MMUD.DataInv(_dataInv));
	}

	public boolean waitForStatline(){return true;}
}

/*
[0;37;40m[0;37;40m[79D[KYou are carrying 2 Adamantite Pieces, 84 platinum pieces, 31 gold crowns, 2
silver nobles, 5 copper farthings, wyvernbone bracelet (Wrist), ruby necklace
(Neck), emerald-studded bracelet (Wrist), skull shield (Off-Hand), red mithril
earrings (Ears), crimson scalemail leggings (Legs), mask of enlightenment
(Face), golden beaded belt (Waist), elven cloak (Back), starsteel plate boots
(Feet), adamantite platemail tunic (Torso), platinum bracers (Arms), etched
platinum ring (Finger), Sunstone (Worn), gold jeweled ring (Finger), gauntlets
of power (Hands), mithril helm (Head), runed crimson flail (Weapon), rope and
grapple, emerald main-gauche
[79D[K[0;37;40mYou have the following keys:  black serpent key, black star key, skeleton key.
[0;32mWealth: [36m2843125 copper farthings
[0;32mEncumbrance: [36m4475/11179 - [33mMedium [40%][0m
*/