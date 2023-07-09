import java.util.ArrayList;

public class OMUD_MMUD_ParseBlockInventory extends OMUD_MMUD_ParseBlocks.ParseBlock{
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

    public boolean getStatlineWait() {return true;}
    public static String getCmdText(){return "i\n";}
    public OMUD_MMUD_ParseBlockInventory(){
        _arrlCmdText.add(new CmdText("i", 1));
        _arrlCmdText.add(new CmdText("inventory", 4)); // "inve" is min ("in" and "inv" conflict with "invite" so are ignored in mud)
    }

    public int findBlockData(OMUD_Char.MMUD_Data mmd, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_INV_PRE, MSTR_INV_END)) > -1){
            mmd.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.INV;
            mmd.dataInv = new OMUD_MMUD_DataBlockInv();            
            cleanData(_sbBlockData, true, true);

            int pos_left =  0;
            int pos_right = _sbBlockData.length() - 1;

            // ------------------
            // Encumbrance
            // ------------------
            if ((pos_right = _sbBlockData.lastIndexOf(MSTR_ENC_END, pos_right)) > -1 &&
                (pos_left  = _sbBlockData.lastIndexOf(MSTR_ENC_MID, pos_right)) > -1){
                mmd.dataInv.enc_level = _sbBlockData.substring(pos_left + MSTR_ENC_MID.length(), pos_right).trim().toLowerCase();
                pos_right = pos_left - 1;

                if ((pos_left = _sbBlockData.lastIndexOf(MSTR_ENC_PRE, pos_right)) > -1){
                    String[] tokens = _sbBlockData.substring(pos_left + MSTR_ENC_PRE.length(), pos_right + 1).trim().split("/");
                    if (tokens.length == 2){
                        mmd.dataInv.enc_cur = Integer.parseInt(tokens[0]);
                        mmd.dataInv.enc_max = Integer.parseInt(tokens[1]);
                    }
                    pos_right = pos_left - 1;
                }
            }

            // ------------------
            // Wealth
            // ------------------
            if ((pos_right = _sbBlockData.lastIndexOf(MSTR_WEALTH_END, pos_right)) > -1 &&
                (pos_left  = _sbBlockData.lastIndexOf(MSTR_WEALTH_PRE, pos_right)) > -1){
                mmd.dataInv.wealth = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_WEALTH_PRE.length(), pos_right).trim());
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
                        mmd.dataInv.arrlKeys.add(new OMUD_MMUD_DataItem(arrlKeys.get(i)));
                } //else mmd.dataInv.keys_str = "(no keys carried)";
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
                buildItems(arrlNew, mmd.dataInv.arrlItems, mmd.dataInv.coins, mmd.dataInv.arrlWorn);
            } // else mmd.dataInv.items_str = "(no items carried)";
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
