import java.util.ArrayList;

public class OMUD_MMUDBlock_Inventory extends OMUD_MMUDBlocks.Block{
    private final String MSTR_INV_PRE =     "[0;37;40m[0;37;40m[79D[KYou are carrying ";
    private final String MSTR_INV_END =     "%][0m";
    private final String MSTR_NO_ITEMS =    "Nothing!";
    private final String MSTR_KEYS_PRE =    "You have ";
    private final String MSTR_KEYS_END =    ".";
    private final String MSTR_KEYS_YES =    "the following keys:  ";
    private final String MSTR_WEALTH_PRE =  "Wealth: ";
    private final String MSTR_WEALTH_END =  " copper farthings";
    private final String MSTR_ENC_PRE =     "Encumbrance: ";
    private final String MSTR_ENC_MID =     " - ";
    private final String MSTR_ENC_END =     "[";

    public boolean getStatlineWait()                    {return true;}
    public OMUD_MMUD.DataBlock.eBlockType getDataType() {return OMUD_MMUD.DataBlock.eBlockType.INV;}
    public OMUD_MMUDBlock_Inventory(){
        _arrlCmdText.add(new CmdText(OMUD_MMUD.DataBlock.CMD_STRINGS[getDataType().ordinal()], 1));
        _arrlCmdText.add(new CmdText("inventory", 4)); // "inve" is min ("in" and "inv" conflict with "invite" so are ignored in mud)
    }

    public int findBlockData(OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_INV_PRE, MSTR_INV_END)) > -1){
            cleanData(_sbBlockData, true, true);
            mmc.dataInv = new OMUD_MMUD.DataInv();

            int pos_left =  0;
            int pos_right = _sbBlockData.length() - 1;

            // ------------------
            // Encumbrance
            // ------------------
            if ((pos_right = _sbBlockData.lastIndexOf(MSTR_ENC_END, pos_right)) > -1 &&
                (pos_left  = _sbBlockData.lastIndexOf(MSTR_ENC_MID, pos_right)) > -1){
                mmc.dataInv.enc_level = _sbBlockData.substring(pos_left + MSTR_ENC_MID.length(), pos_right).trim().toLowerCase();
                pos_right = pos_left - 1;

                if ((pos_left = _sbBlockData.lastIndexOf(MSTR_ENC_PRE, pos_right)) > -1){
                    String[] tokens = _sbBlockData.substring(pos_left + MSTR_ENC_PRE.length(), pos_right + 1).trim().split("/");
                    if (tokens.length == 2){
                        mmc.dataInv.enc_cur = Integer.parseInt(tokens[0]);
                        mmc.dataInv.enc_max = Integer.parseInt(tokens[1]);
                    }
                    pos_right = pos_left - 1;
                }
            }

            // ------------------
            // Wealth
            // ------------------
            if ((pos_right = _sbBlockData.lastIndexOf(MSTR_WEALTH_END, pos_right)) > -1 &&
                (pos_left  = _sbBlockData.lastIndexOf(MSTR_WEALTH_PRE, pos_right)) > -1){
                mmc.dataInv.wealth = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_WEALTH_PRE.length(), pos_right).trim());
                pos_right = pos_left - 1;
            }

            // ------------------
            // Keys
            // ------------------
            if ((pos_right = _sbBlockData.lastIndexOf(MSTR_KEYS_END, pos_right)) > -1 &&
                (pos_left  = _sbBlockData.lastIndexOf(MSTR_KEYS_PRE, pos_right)) > -1){
                if (_sbBlockData.indexOf(MSTR_KEYS_YES, pos_left + MSTR_KEYS_PRE.length()) > -1){
                    String strKeys = _sbBlockData.substring(pos_left + MSTR_KEYS_PRE.length() + MSTR_KEYS_YES.length(), pos_right).trim().toLowerCase();
                    ArrayList<String> arrlKeys = new ArrayList<String>();
                    splitCommaListToArray(strKeys, arrlKeys);
                    for (int i = 0; i < arrlKeys.size(); ++i)
                        mmc.dataInv.arrlKeys.add(new OMUD_MMUD.DataItem(arrlKeys.get(i)));
                } //else mmc.dataInv.keys_str = "(no keys carried)";
                pos_right = pos_left - 1;
            }

            // ------------------
            // Items (+ Coins)
            // ------------------
            String strItems = _sbBlockData.substring(0, pos_right + 1).trim().toLowerCase();
            // get item list and coins...
            if (!strItems.equals(MSTR_NO_ITEMS)){
                ArrayList<String> arrlNew = new ArrayList<String>();
                splitCommaListToArray(strItems, arrlNew);
                buildItemList(arrlNew, mmc.dataInv.coins, mmc.dataInv.arrlItems, mmc.dataInv.arrlWorn);
            } // else mmc.dataInv.items_str = "(no items carried)";
        }

        return pos_data_found_start;
    }

    private int getItemCount(String strItem){
        int count = -1;
        String[] tokens = strItem.split(" ");
        if (tokens.length >= 2)
            count = Integer.parseInt(tokens[0]);
        return count;
    }
}
