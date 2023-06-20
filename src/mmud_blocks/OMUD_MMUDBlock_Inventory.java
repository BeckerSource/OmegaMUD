public class OMUD_MMUDBlock_Inventory extends OMUD_MMUDBlocks.Block{
	private final String MSTR_ITEMS = 		"[0;37;40m[0;37;40m[79D[KYou are carrying ";
	private final String MSTR_KEYS_PRE =  	"[79D[K[0;37;40mYou have ";
	private final String MSTR_KEYS_END =  	".";
	private final String MSTR_KEYS_YES =  	"the following keys:  ";
	private final String MSTR_KEYS_NO =  	"no keys";
	private final String MSTR_WEALTH_PRE =  "[0;32mWealth: [36m";
	private final String MSTR_WEALTH_END =  " copper farthings";
	private final String MSTR_ENC_PRE =  	"[0;32mEncumbrance: [36m";
	private final String MSTR_ENC_END =  	"[0m";

	public OMUD_MMUDBlock_Inventory(){
		_arrlCmdText.add(new CmdText("i", 		  1));
		_arrlCmdText.add(new CmdText("inventory", 4)); // "inve" is min ("in" and "inv" conflict with "invite" so are ignored in mud)
	}

	public void updateActiveBlock(int pos_block, String strFoundCmdFull, OMUD_MMUDChar.ActiveBlock ablk){
		ablk.update(pos_block, strFoundCmdFull, true, OMUD_MMUD.Data.eDataType.DT_INV);
	}

	public int findBlockData(OMUD_IMUDEvents ommme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
		int pos_data_found_start = -1;

		// ------------------
		// Items (+ Coins)
		// ------------------
		if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ITEMS, "")) > -1){
			mmc.dataInv = new OMUD_MMUD.DataInv();
			if (OMUD.compareSBString(_sbBlockData, "Nothing!")){
				 mmc.dataInv.items = "(no items carried)";
			} else {
				cleanData(true, false);
				mmc.dataInv.items = _sbBlockData.toString();
			}

		// ------------------
		// Keys 
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_KEYS_PRE, MSTR_KEYS_END)) > -1){
			int pos_keys_start = 0;
			if ((pos_keys_start = _sbBlockData.indexOf(MSTR_KEYS_YES, 0)) > -1){
				_sbBlockData.delete(pos_keys_start, MSTR_KEYS_YES.length());
				cleanData(true, false);
				mmc.dataInv.keys = _sbBlockData.toString();
			} else /* MSTR_KEYS_NO */ {
				mmc.dataInv.keys = "(no keys carried)";				
			}

		// ------------------
		// Wealth
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_WEALTH_PRE, MSTR_WEALTH_END)) > -1){
			mmc.dataInv.wealth = Integer.parseInt(_sbBlockData.toString());

		// ------------------
		// Encumbrance
		// ------------------
		} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_ENC_PRE, MSTR_ENC_END)) > -1){
			cleanData(true, true);
			mmc.dataInv.enc_level = _sbBlockData.toString();
		}

		return pos_data_found_start;
	}
}
