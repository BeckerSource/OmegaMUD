public class OMUD_MMUD_ParseBlockCombat extends OMUD_MMUD_ParseBlocks.ParseBlock{
    private final String MSTR_PREFIX_RESET_WHBL =   "[0;37;40m";
    private final String MSTR_EXP_GAIN_PRE =        "[79D[KYou gain ";
    private final String MSTR_EXP_GAIN_END =        " experience.";
    private final String MSTR_CMBT_ON =             "[79D[K[0;33m*Combat Engaged*";
    private final String MSTR_CMBT_OFF =            "[79D[K[0;33m*Combat Off*";

    private final String MSTR_CMBT_THEY_HIT_PRE =   "[79D[K[1m[31m";
    private final String MSTR_CMBT_THEY_HIT_FOR =   " you for ";
    private final String MSTR_CMBT_YOU_HIT_PRE =    "[79D[K[1;31mYou ";
    private final String MSTR_CMBT_YOU_HIT_FOR =    " for ";
    private final String MSTR_CMBT_HIT_END =        " damage!";
    private final String MSTR_CMBT_THE_PRE =        "The ";

    private final String MSTR_CMBT_MISS_PRE =       "[79D[K[0;36m";
    private final String MSTR_CMBT_YOU_MISS =       "[79D[K[0;36mYou ";
    private final String MSTR_CMBT_THEY_MISS =      " at you";
    private final String MSTR_CMBT_MISS_END =       "!";
    private final String MSTR_CMBT_YOU_DODGE =      ", but you dodge out of the way";

    public boolean getStatlineWait(){return false;}
    public OMUD_MMUD_ParseBlockCombat(){}

    public int findBlockData(OMUD_MMUD_Char mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        // ------------------
        // Combat: On
        // ------------------
        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_CMBT_ON, "")) > -1){
            mmc.dataStatline.in_combat = true;

        // ------------------
        // Combat: Off
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_CMBT_OFF, "")) > -1){
            mmc.dataStatline.in_combat = false;
            pos_data_found_start = checkPrefix("Combat Broken by Cmd", mmc.ablk.sbDebug, sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);

        // ------------------
        // Combat: You (I) Hit
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_CMBT_YOU_HIT_PRE, MSTR_CMBT_HIT_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(false);
            cl.unit = new OMUD_MMUD_DataUnit("YOU");

            int pos_left =  0;
            int pos_right = _sbBlockData.length() - 1;
            if ((pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){

                cl.tgt_dmg = Integer.parseInt(_sbBlockData.substring(pos_left + 1, pos_right + 1));

                pos_right = pos_left + 1;
                if (_sbBlockData.substring(pos_left - MSTR_CMBT_YOU_HIT_FOR.length() + 1, pos_right).equals(MSTR_CMBT_YOU_HIT_FOR)){
                    pos_right = pos_left - MSTR_CMBT_YOU_HIT_FOR.length();
                    if ((pos_left = _sbBlockData.indexOf(" ", 0)) > -1){
                        cl.action =   _sbBlockData.substring(0, pos_left);
                        cl.tgt_name = _sbBlockData.substring(pos_left + 1, pos_right + 1);
                    }
                }
            }

            mmc.dataCombat.lines.add(cl);

        // ------------------
        // Combat: They Hit
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_CMBT_THEY_HIT_PRE, MSTR_CMBT_HIT_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(false);
            cl.tgt_name = "YOU";

            int pos_left =  0;
            int pos_right = _sbBlockData.length() - 1;
            if ((pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){

                cl.tgt_dmg = Integer.parseInt(_sbBlockData.substring(pos_left + 1, pos_right + 1));

                pos_right = pos_left + 1;
                if (_sbBlockData.substring(pos_left - MSTR_CMBT_THEY_HIT_FOR.length() + 1, pos_right).equals(MSTR_CMBT_THEY_HIT_FOR)){
                    pos_right = pos_left - MSTR_CMBT_THEY_HIT_FOR.length();
                    if ((pos_left   = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){
                        int name_prefix_len = 0;
                        if (_sbBlockData.substring(0, MSTR_CMBT_THE_PRE.length()).equals(MSTR_CMBT_THE_PRE))
                            name_prefix_len = MSTR_CMBT_THE_PRE.length();

                        cl.unit = new OMUD_MMUD_DataUnit(_sbBlockData.substring(name_prefix_len, pos_left));
                        cl.action = _sbBlockData.substring(pos_left + 1, pos_right + 1);
                    }
                }
            }

            mmc.dataCombat.lines.add(cl);

        // ------------------
        // Combat: You Miss
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_CMBT_YOU_MISS, MSTR_CMBT_MISS_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(true);
            cl.unit = new OMUD_MMUD_DataUnit("YOU");

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right = _sbBlockData.indexOf(" ", pos_left)) > -1)
                cl.action = _sbBlockData.substring(pos_left, pos_right);

            // skip " at " and get the attacked name...
            pos_left = pos_right + 1;
            if ((pos_right =  _sbBlockData.indexOf(" ", pos_left)) > -1)
                cl.tgt_name = _sbBlockData.substring(pos_right + 1, _sbBlockData.length());

            mmc.dataCombat.lines.add(cl);

        // ------------------
        // Combat: They Miss
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_CMBT_MISS_PRE, MSTR_CMBT_MISS_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(true);
            cl.tgt_name = "YOU";

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right = _sbBlockData.indexOf(MSTR_CMBT_THEY_MISS, pos_left)) > -1 &&
                (pos_left =  _sbBlockData.lastIndexOf(" ", pos_right - 1)) > -1){

                int name_prefix_len = 0;
                if (_sbBlockData.substring(0, MSTR_CMBT_THE_PRE.length()).equals(MSTR_CMBT_THE_PRE))
                    name_prefix_len = MSTR_CMBT_THE_PRE.length();

                cl.unit = new OMUD_MMUD_DataUnit(_sbBlockData.substring(name_prefix_len, pos_left));
                cl.action = _sbBlockData.substring(pos_left + 1, pos_right);

                // get the attack weapon if shown (some mobs don't show their weapon on a dodge)...
                pos_left = pos_right + MSTR_CMBT_THEY_MISS.length();
                if ((pos_right = _sbBlockData.lastIndexOf(MSTR_CMBT_YOU_DODGE, _sbBlockData.length() - 1)) > -1){
                    pos_right--;
                    cl.tgt_dodge = true;
                } else pos_right = _sbBlockData.length() - 1;
                if (pos_right > pos_left && 
                    (pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1)
                    cl.weapon = _sbBlockData.substring(pos_left + 1, pos_right + 1);
            }

            mmc.dataCombat.lines.add(cl);

        // ------------------
        // EXP Gained
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_EXP_GAIN_PRE, MSTR_EXP_GAIN_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.EXP;
            int exp = Integer.parseInt(_sbBlockData.toString());
            mmc.dataExp.cur_total   += exp;
            mmc.dataExp.next_rem    -= exp;
            if (mmc.dataExp.next_rem < 0)
                mmc.dataExp.next_rem = 0;
        }    

        return pos_data_found_start;
    }
}