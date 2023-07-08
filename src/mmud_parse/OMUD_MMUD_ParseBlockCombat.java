public class OMUD_MMUD_ParseBlockCombat extends OMUD_MMUD_ParseBlocks.ParseBlock{
    private final String MSTR_PREFIX_RESET_WHBL =   "[0;37;40m";
    private final String MSTR_EXP_GAIN_PRE =        "[79D[KYou gain ";
    private final String MSTR_EXP_GAIN_END =        " experience.";
    private final String MSTR_CMBT_ON =             "[79D[K[0;33m*Combat Engaged*";
    private final String MSTR_CMBT_OFF =            "[79D[K[0;33m*Combat Off*";
    private final String MSTR_CMBT_AT =             " at ";

    private final String MSTR_CMBT_THEY_HIT_PRE =   "[79D[K[1m[31m";
    private final String MSTR_CMBT_YOU_HIT_PRE =    "[79D[K[1;31mYou ";
    private final String MSTR_CMBT_HIT_FOR =        " for ";
    private final String MSTR_CMBT_HIT_END =        " damage!";
    private final String MSTR_CMBT_THE_PRE =        "The ";

    private final String MSTR_CMB_YOU_FAIL_PRE =    "[79D[K[0;36mYou attempt to cast ";
    private final String MSTR_CMB_YOU_FAIL_END =    ", but fail."; 

    private final String MSTR_CMBT_MISS_PRE =       "[79D[K[0;36m";
    private final String MSTR_CMBT_YOU_MISS =       "[79D[K[0;36mYou ";
    private final String MSTR_CMBT_MISS_END =       "!";
    private final String MSTR_CMBT_DODGE_PRE =      ", but ";
    private final String MSTR_CMBT_DODGE_END =      " out of the way";

/*
[79D[K[1;33mA kobold thief[0;32m sneaks into the room from nowhere.
[79D[K[1;33mA acid slime[0;32m oozes into the room from nowhere.
[79D[K[1;31mAcid burns you for 2 damage!
[79D[K[1m[31mThe acid slime whips you with its pseudopod for 5 damage!
[79D[K[79D[KThe giant rat falls to the ground with a tortured squeak.
*/

    public boolean getStatlineWait(){return false;}
    public OMUD_MMUD_ParseBlockCombat(){}

    public int findBlockData(OMUD_MMUD_Char mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        // ------------------
        // Combat: On
        // ------------------
        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_CMBT_ON, "")) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmc.dataStatline.in_combat = true;

        // ------------------
        // Combat: Off
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_CMBT_OFF, "")) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmc.dataStatline.in_combat = false;
            pos_data_found_start = checkPrefix("Combat Broken by Cmd", mmc.ablk.sbDebug, sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);

        // ------------------
        // Combat: You Hit
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_CMBT_YOU_HIT_PRE, MSTR_CMBT_HIT_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.NONE);
            cl.unit = new OMUD_MMUD_DataUnit();

            int pos_left =  0;
            int pos_right = _sbBlockData.length() - 1;
            if ((pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){

                cl.tgt_dmg = Integer.parseInt(_sbBlockData.substring(pos_left + 1, pos_right + 1));

                pos_right = pos_left + 1;
                pos_left =  pos_left - MSTR_CMBT_HIT_FOR.length() + 1;
                if (_sbBlockData.substring(pos_left, pos_right).equals(MSTR_CMBT_HIT_FOR)){
                    pos_right = pos_left - 1;
                    if ((pos_left = _sbBlockData.indexOf(" ", 0)) > -1){
                        // when casting spells, msg wil lbe "casts spell at target for x damage",
                        // so find at text for target name...
                        int pos_left_at = _sbBlockData.lastIndexOf(MSTR_CMBT_AT, pos_right);
                        if (pos_left_at > -1)
                            pos_left = pos_left_at + MSTR_CMBT_AT.length() - 1;

                        cl.unit_action = _sbBlockData.substring(0, pos_left_at > -1 ? pos_left_at : pos_left);
                        cl.tgt_name =    _sbBlockData.substring(pos_left + 1, pos_right + 1);
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

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.NONE);
            
            int pos_left =  0;
            int pos_right = _sbBlockData.length() - 1;
            if ((pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){

                cl.tgt_dmg = Integer.parseInt(_sbBlockData.substring(pos_left + 1, pos_right + 1));

                // match hit "for"...
                pos_right = pos_left + 1;
                pos_left =  pos_left - MSTR_CMBT_HIT_FOR.length() + 1;
                if (_sbBlockData.substring(pos_left, pos_right).equals(MSTR_CMBT_HIT_FOR)){
                    pos_right = pos_left - 1;

                    // find who was hit (you or other)...
                    if ((pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){
                        cl.tgt_name = _sbBlockData.substring(pos_left + 1, pos_right + 1);
                        if (cl.tgt_name.equals("you"))
                            cl.tgt_name = cl.tgt_name.toUpperCase();

                        // get other info...
                        pos_right = pos_left - 1;
                        if ((pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){
                            int name_prefix_len = 0;
                            if (_sbBlockData.substring(0, MSTR_CMBT_THE_PRE.length()).equals(MSTR_CMBT_THE_PRE))
                                name_prefix_len = MSTR_CMBT_THE_PRE.length();

                            cl.unit =        new OMUD_MMUD_DataUnit(_sbBlockData.substring(name_prefix_len, pos_left));
                            cl.unit_action = _sbBlockData.substring(pos_left + 1, pos_right + 1);
                        }
                    }
                }
            }

            mmc.dataCombat.lines.add(cl);

        // ------------------
        // Combat: You Fail Cast
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_CMB_YOU_FAIL_PRE, MSTR_CMB_YOU_FAIL_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.FAIL);
            cl.unit =        new OMUD_MMUD_DataUnit();
            cl.unit_action = _sbBlockData.toString();
            // no target on cast fails
            mmc.dataCombat.lines.add(cl);

        // ------------------
        // Combat: You Miss
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_CMBT_YOU_MISS, MSTR_CMBT_MISS_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.MISS);
            cl.unit = new OMUD_MMUD_DataUnit();

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right =     _sbBlockData.indexOf(" ", pos_left)) > -1)
                cl.unit_action = _sbBlockData.substring(pos_left, pos_right);

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

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.MISS);

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right = _sbBlockData.indexOf(MSTR_CMBT_AT, pos_left)) > -1 &&
                (pos_left =  _sbBlockData.lastIndexOf(" ", pos_right - 1)) > -1){

                int name_prefix_len = 0;
                if (_sbBlockData.substring(0, MSTR_CMBT_THE_PRE.length()).equals(MSTR_CMBT_THE_PRE))
                    name_prefix_len = MSTR_CMBT_THE_PRE.length();

                cl.unit =           new OMUD_MMUD_DataUnit(_sbBlockData.substring(name_prefix_len, pos_left));
                cl.unit_action =    _sbBlockData.substring(pos_left + 1, pos_right);

                pos_left =  pos_right + MSTR_CMBT_AT.length();
                if ((pos_right = _sbBlockData.indexOf(" ", pos_left)) > -1){
                    // check for dodge (from the end)...
                    if (_sbBlockData.length() > MSTR_CMBT_DODGE_END.length() &&
                        _sbBlockData.substring(_sbBlockData.length() - MSTR_CMBT_DODGE_END.length(), _sbBlockData.length()).equals(MSTR_CMBT_DODGE_END)){
                        cl.tgt_miss = OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.DODGE;
                        pos_right = _sbBlockData.indexOf(MSTR_CMBT_DODGE_PRE, pos_left);
                    } else pos_right = _sbBlockData.length();

                    // weapon...
                    int pos_left_weap = _sbBlockData.lastIndexOf(" ", pos_right - 1);
                    if (pos_left_weap > pos_left){
                        cl.tgt_weapon = _sbBlockData.substring(pos_left_weap + 1, pos_right);
                        pos_right =     _sbBlockData.indexOf(" ", pos_left); // set right to original pos after name for below
                    }
                } else pos_right = _sbBlockData.length();

                // get name...
                cl.tgt_name = _sbBlockData.substring(pos_left, pos_right);
                if (cl.tgt_name.equals("you"))
                    cl.tgt_name = cl.tgt_name.toUpperCase();
            }

            mmc.dataCombat.lines.add(cl);

        // ------------------
        // EXP Gained
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_EXP_GAIN_PRE, MSTR_EXP_GAIN_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;

            int exp = Integer.parseInt(_sbBlockData.toString());
            mmc.dataExp.combat_gain += exp; // reset in parser
            mmc.dataExp.cur_total   += exp;
            mmc.dataExp.next_rem    -= exp;
            if (mmc.dataExp.next_rem < 0)
                mmc.dataExp.next_rem = 0;

            // find "my/YOU" most recent hit and assume that kill exp was the exp from that hit...
            for (int i = mmc.dataCombat.lines.size() - 1; i >= 0; --i)
                if (mmc.dataCombat.lines.get(i).unit.name.equals("YOU")){
                    mmc.dataCombat.lines.get(i).tgt_exp = exp;
                    break;
                }
        }    

        return pos_data_found_start;
    }
}