import java.util.ArrayList;

public class OMUD_MMUD_ParseBlockCombat extends OMUD_MMUD_ParseBlocks.ParseBlock{
    private final String MSTR_PREFIX_RESET_WHBL =   "[0;37;40m";
    private final String MSTR_EXP_GAIN_PRE =        "[79D[KYou gain ";
    private final String MSTR_EXP_GAIN_END =        " experience.";
    
    private final String MSTR_ON =              "[79D[K[0;33m*Combat Engaged*";
    private final String MSTR_OFF =             "[79D[K[0;33m*Combat Off*";

    private final String MSTR_AT_TGT =          " at ";
    private final String MSTR_YOU_PRE =         "You ";
    private final String MSTR_YOU_TGT =         "you";
    private final String MSTR_YOU_TGT_SPC =     " you ";
    private final String MSTR_YOU_CAPS =        "YOU";
    private final String MSTR_THE_PRE =         "The ";

    private final String MSTR_HIT_PRE1 =        "[79D[K[1;31m";
    private final String MSTR_HIT_PRE2 =        "[79D[K[1m[31m";
    private final String MSTR_HIT_FOR =         " for ";
    private final String MSTR_HIT_END =         " damage!";

    private final String MSTR_YOU_FAIL_PRE =    "[79D[K[0;36mYou attempt to cast ";
    private final String MSTR_YOU_FAIL_END =    ", but fail."; 

    private final String MSTR_YOU_MISS_PRE =    "[79D[K[0;36mYou ";
    private final String MSTR_THEY_MISS_PRE =   "[79D[K[0;36m";
    private final String MSTR_MISS_END =        "!";
    private final String MSTR_DODGE_PRE =       ", but ";
    private final String MSTR_DODGE_END =       " out of the way";

    private final String MSTR_UNIT_ENTER_PRE =  "[79D[K[1;33mA ";
    private final String MSTR_UNIT_ENTER_MID =  "[0;32m ";
    private final String MSTR_UNIT_ENTER_END =  ".";

    // [79D[K[1;33mA kobold thief[0;32m sneaks into the room from nowhere.
    // [79D[K[1;33mA acid slime[0;32m oozes into the room from nowhere.
    // [79D[K[79D[KThe giant rat falls to the ground with a tortured squeak.

    public boolean getStatlineWait() {return false;}
    public static String getCmdText(){return "";}
    public OMUD_MMUD_ParseBlockCombat(){}

    public int findBlockData(OMUD_Char.MMUD_Data mmd, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        // ------------------
        // Combat: On
        // ------------------
        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_ON, "")) > -1){
            mmd.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmd.dataStatline.in_combat = true;

        // ------------------
        // Combat: Off
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_OFF, "")) > -1){
            mmd.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            // don't set as not-in-combat - using a timer to detect combat in next round
            pos_data_found_start = checkPrefix("Combat Broken by Cmd", mmd.ablk.sbDebug, sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);

        // ------------------
        // Combat: Hit (You/I & They)
        // ------------------
        } else if (
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_HIT_PRE1, MSTR_HIT_END)) > -1 ||
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_HIT_PRE2, MSTR_HIT_END)) > -1){
            mmd.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmd.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.HIT);

            int pos_left =  0;
            int pos_right = 0;
            cl.unit = new OMUD_MMUD_DataUnit();

            // get damage...
            if ((pos_right = _sbBlockData.lastIndexOf(" ", _sbBlockData.length() - 1)) > -1)
                cl.tgt_dmg = Integer.parseInt(_sbBlockData.substring(pos_right + 1, _sbBlockData.length()));

            // find for, and check for spell casts and other attacks that have " at " target...
            if ((pos_right  = _sbBlockData.lastIndexOf(MSTR_HIT_FOR, pos_right)) > -1 &&
                (pos_left   = _sbBlockData.lastIndexOf(MSTR_AT_TGT,  pos_right)) > -1){
                cl.tgt_name = _sbBlockData.substring(pos_left + MSTR_AT_TGT.length(), pos_right);
                pos_right   = pos_left;
            }
            
            // check for you/I as prefix...
            if (_sbBlockData.substring(0, MSTR_YOU_PRE.length()).equals(MSTR_YOU_PRE)){
                pos_left = MSTR_YOU_PRE.length();
                cl.unit.name = MSTR_YOU_CAPS;
            // check for "the" prefix (skip it)...
            } else if (_sbBlockData.substring(0, MSTR_THE_PRE.length()).equals(MSTR_THE_PRE)){            
                pos_left = MSTR_THE_PRE.length();
            } else pos_left = 0;

            // get a cropped version of the string after above...
            String strCrop = _sbBlockData.substring(pos_left, pos_right + 1);
            int pos_unit_left = 0;
            int pos_tgt_left  = 0;
            pos_right = strCrop.length() - 1;
            
            // find target name...
            if (cl.tgt_name.length() == 0){
                if ((pos_tgt_left = strCrop.lastIndexOf(MSTR_YOU_TGT_SPC, pos_right)) > -1){
                    cl.tgt_name = MSTR_YOU_CAPS;
                } else if ((pos_tgt_left = findUnitName(cl, mmd.dataRoom.arrlUnits, strCrop, false)) == -1){
                    cl.tgt_name = "???";
                    pos_tgt_left = pos_right;
                }
            } else pos_tgt_left = pos_right;

            // find unit name...
            if (cl.unit.name.length() == 0){
                if ((pos_unit_left  = findUnitName(cl, mmd.dataRoom.arrlUnits, strCrop, true)) > -1){
                     pos_unit_left += cl.unit.name.length() + 1; // +1 skip space to next word after name
                // unit is unknown, just get text until the target as the unit name...
                } else {
                    cl.unit.name  = strCrop.substring(0, pos_tgt_left + 1).trim();
                    pos_unit_left = pos_tgt_left;
                }
            }

            // get unit action text...
            if (pos_unit_left < pos_tgt_left){
                cl.unit_action = strCrop.substring(pos_unit_left, pos_tgt_left).trim();
                // check for more on the right...
                pos_tgt_left += cl.tgt_name.length() + 1; // +1 skip space to next word after name
                if (pos_tgt_left < pos_right)
                    cl.unit_action += ", " + strCrop.substring(pos_tgt_left, pos_right + 1).trim();                
            }

            mmd.dataCombat.lines.add(cl);

        // ------------------
        // Combat: You Fail Cast
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_YOU_FAIL_PRE, MSTR_YOU_FAIL_END)) > -1){
            mmd.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmd.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.FAIL);
            cl.unit =        new OMUD_MMUD_DataUnit(MSTR_YOU_CAPS);
            cl.unit_action = _sbBlockData.toString();
            // no target on cast fails
            mmd.dataCombat.lines.add(cl);

        // ------------------
        // Combat: You Miss
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_YOU_MISS_PRE, MSTR_MISS_END)) > -1){
            mmd.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmd.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.MISS);
            cl.unit = new OMUD_MMUD_DataUnit(MSTR_YOU_CAPS);

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right =     _sbBlockData.indexOf(" ", pos_left)) > -1)
                cl.unit_action = _sbBlockData.substring(pos_left, pos_right);

            // skip " at " and get the attacked name...
            pos_left = pos_right + 1;
            if ((pos_right =  _sbBlockData.indexOf(" ", pos_left)) > -1)
                cl.tgt_name = _sbBlockData.substring(pos_right + 1, _sbBlockData.length());

            mmd.dataCombat.lines.add(cl);

        // ------------------
        // Combat: They Miss
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_THEY_MISS_PRE, MSTR_MISS_END)) > -1){
            mmd.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmd.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            OMUD_MMUD_DataBlockCombat.CombatLine cl = new OMUD_MMUD_DataBlockCombat.CombatLine(OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.MISS);

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right = _sbBlockData.indexOf(MSTR_AT_TGT, pos_left)) > -1 &&
                (pos_left =  _sbBlockData.lastIndexOf(" ", pos_right - 1)) > -1){

                int name_prefix_len = 0;
                if (_sbBlockData.substring(0, MSTR_THE_PRE.length()).equals(MSTR_THE_PRE))
                    name_prefix_len = MSTR_THE_PRE.length();

                cl.unit =           new OMUD_MMUD_DataUnit(_sbBlockData.substring(name_prefix_len, pos_left));
                cl.unit_action =    _sbBlockData.substring(pos_left + 1, pos_right);

                pos_left =  pos_right + MSTR_AT_TGT.length();
                if ((pos_right = _sbBlockData.indexOf(" ", pos_left)) > -1){
                    // check for dodge (from the end)...
                    if (_sbBlockData.length() > MSTR_DODGE_END.length() &&
                        _sbBlockData.substring(_sbBlockData.length() - MSTR_DODGE_END.length(), _sbBlockData.length()).equals(MSTR_DODGE_END)){
                        cl.tgt_miss = OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.DODGE;
                        pos_right = _sbBlockData.indexOf(MSTR_DODGE_PRE, pos_left);
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
                if (cl.tgt_name.equals(MSTR_YOU_TGT))
                    cl.tgt_name = cl.tgt_name.toUpperCase();
            }

            mmd.dataCombat.lines.add(cl);

        // ------------------
        // EXP Gained
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_EXP_GAIN_PRE, MSTR_EXP_GAIN_END)) > -1){
            mmd.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.COMBAT;
            mmd.ablk.refresh_room = true;
            
            int exp = Integer.parseInt(_sbBlockData.toString());
            mmd.dataExp.cur_total   += exp;
            mmd.dataExp.next_rem    -= exp;
            if (mmd.dataExp.next_rem < 0)
                mmd.dataExp.next_rem = 0;

            // accumulate exp gained during the current combat session (reset after session detected as complete)...
            mmd.dataCombat.exp_gained += exp;

            // find "my/YOU" most recent hit and assume that kill exp was the exp from that hit...
            for (int i = mmd.dataCombat.lines.size() - 1; i >= 0; --i)
                if (mmd.dataCombat.lines.get(i).tgt_miss == OMUD_MMUD_DataBlockCombat.CombatLine.eMissType.HIT){
                    mmd.dataCombat.lines.get(i).tgt_exp = exp;
                    break;
                }

        // ------------------
        // Unit Enters Room
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_UNIT_ENTER_PRE, MSTR_UNIT_ENTER_END)) > -1){
            mmd.ablk.refresh_room = true;
        }    

        return pos_data_found_start;
    }

    private int findUnitName(OMUD_MMUD_DataBlockCombat.CombatLine cl, ArrayList<OMUD_MMUD_DataUnit> arrlUnits, String strText, boolean for_unit_name){
        int unit_num =      -1;
        int pos_left =      -1;
        int pos_left_ret =  -1;

        // loop through the room units and find a match in the text...
        for (int i = 0; i < arrlUnits.size(); ++i)
            if ((pos_left = strText.indexOf(arrlUnits.get(i).name, 0)) > -1){
                // if this is our first match or 
                // check for best match on mob name descriptive prefixes (always use the prefix with the most words)...
                if (unit_num == -1 ||
                    (arrlUnits.get(i).name.length() > arrlUnits.get(unit_num).name.length())){
                    unit_num = i;
                    pos_left_ret = pos_left;
                }
            }

        if (unit_num > -1){
            if (for_unit_name)
                 cl.unit.name = arrlUnits.get(unit_num).name;
            else cl.tgt_name  = arrlUnits.get(unit_num).name;
        }

        return pos_left_ret;
    }
}