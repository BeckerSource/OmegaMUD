public class OMUD_MMUDBlock_Other extends OMUD_MMUDBlocks.Block{
    private final String MSTR_PREFIX_RESET_WHBL =       "[0;37;40m";

    private final String MSTR_COMBAT_ON =               "[79D[K[0;33m*Combat Engaged*";
    private final String MSTR_COMBAT_OFF =              "[79D[K[0;33m*Combat Off*";

    private final String MSTR_COMBAT_THEY_HIT_PRE =     "[79D[K[1m[31m";
    private final String MSTR_COMBAT_THEY_HIT_FOR =     " you for ";
    private final String MSTR_COMBAT_YOU_HIT_PRE =      "[79D[K[1;31mYou ";
    private final String MSTR_COMBAT_YOU_HIT_FOR =      " for ";
    private final String MSTR_COMBAT_HIT_END =          " damage!";
    private final String MSTR_COMBAT_THE_PRE =          "The ";

    private final String MSTR_COMBAT_YOU_MISS =         "[79D[K[0;36mYou ";
    private final String MSTR_COMBAT_THEY_MISS =        " at you";
    private final String MSTR_COMBAT_MISS_END =         "!";
    private final String MSTR_COMBAT_YOU_DODGE =        ", but you dodge out of the way";

    private final String MSTR_EXP_GAIN_PRE =            "[79D[KYou gain ";
    private final String MSTR_EXP_GAIN_END =            " experience.";
    private final String MSTR_MOVE_NO_EXIT_DIR =        "[0;37;40m[79D[KThere is no exit in that direction!";
    private final String MSTR_MEDITATE_WAKE =           "[79D[KYou awake from deep meditation feeling stronger!";
    private final String MSTR_MEDITATE_WONT_HELP =      "[0;37;40mMeditation will not help at this time.";
    private final String MSTR_CMD_NO_EFFECT =           "[0;37mYour command had no effect.";
    private final String MSTR_REG_RESTING =             "You are now resting.";
    private final String MSTR_REG_MEDITATING =          "You are now meditating.";
    private final String MSTR_REG_SPELL_ALREADY_CAST =  "You have already cast a spell this round!";
    private final String MSTR_COLOR_MAGENTA =           "[79D[K[0;35m";
    private final String MSTR_COLOR_CYAN =              "[79D[K[0;36m";
    private final String MSTR_COLOR_WHITE =             "[79D[K[1;37m";
    //private final String MSTR_NQC =                     "[0m(N)onstop, (Q)uit, or (C)ontinue?";

    public boolean getStatlineWait(){return false;}
    public OMUD_MMUDBlock_Other(){}

    public int findBlockData(OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        // ------------------
        // Combat: On
        // ------------------
        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_COMBAT_ON, "")) > -1){
            mmc.dataStatline.in_combat = true;

        // ------------------
        // Combat: Off
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_COMBAT_OFF, "")) > -1){
            mmc.dataStatline.in_combat = false;
            pos_data_found_start = checkPrefix("Combat Broken by Cmd", mmc.ablk.sbDebug, sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);


        // ------------------
        // Combat: You (I) Hit
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COMBAT_YOU_HIT_PRE, MSTR_COMBAT_HIT_END)) > -1){
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            int dmg = 0;
            String strAttacker =    "YOU";
            String strAttacked =    "";
            String strAttackType =  "";

            int pos_left =  0;
            int pos_right = _sbBlockData.length() - 1;
            if ((pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){

                dmg = Integer.parseInt(_sbBlockData.substring(pos_left + 1, pos_right + 1));

                pos_right = pos_left + 1;
                if (_sbBlockData.substring(pos_left - MSTR_COMBAT_YOU_HIT_FOR.length() + 1, pos_right).equals(MSTR_COMBAT_YOU_HIT_FOR)){
                    pos_right = pos_left - MSTR_COMBAT_YOU_HIT_FOR.length();
                    if ((pos_left   = _sbBlockData.indexOf(" ", 0)) > -1){
                        strAttackType  = _sbBlockData.substring(0, pos_left);
                        strAttacked = _sbBlockData.substring(pos_left + 1, pos_right + 1);
                    }
                }
            }
OMUD.logDebug("Z1: " + strAttacker + " vs " + strAttacked + " (Hit: " + dmg + ") (" + strAttackType + ")");

        // ------------------
        // Combat: They Hit
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COMBAT_THEY_HIT_PRE, MSTR_COMBAT_HIT_END)) > -1){
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            int dmg = 0;
            String strAttacker =    "";
            String strAttacked =    "YOU";
            String strAttackType  = "";

            int pos_left =  0;
            int pos_right = _sbBlockData.length() - 1;
            if ((pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){

                dmg = Integer.parseInt(_sbBlockData.substring(pos_left + 1, pos_right + 1));

                pos_right = pos_left + 1;
                if (_sbBlockData.substring(pos_left - MSTR_COMBAT_THEY_HIT_FOR.length() + 1, pos_right).equals(MSTR_COMBAT_THEY_HIT_FOR)){
                    pos_right = pos_left - MSTR_COMBAT_THEY_HIT_FOR.length();
                    if ((pos_left   = _sbBlockData.lastIndexOf(" ", pos_right)) > -1){
                        int name_prefix_len = 0;
                        if (_sbBlockData.substring(0, MSTR_COMBAT_THE_PRE.length()).equals(MSTR_COMBAT_THE_PRE))
                            name_prefix_len = MSTR_COMBAT_THE_PRE.length();

                        strAttacker = _sbBlockData.substring(name_prefix_len, pos_left);
                        strAttackType  = _sbBlockData.substring(pos_left + 1, pos_right + 1);
                    }
                }
            }
OMUD.logDebug("Z1: " + strAttacker + " vs " + strAttacked + " (Hit: " + dmg + ") (" + strAttackType + ")");

        // ------------------
        // Combat: You Miss
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COMBAT_YOU_MISS, MSTR_COMBAT_MISS_END)) > -1){
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            String strAttacker =    "YOU";
            String strAttacked =    "";
            String strAttackType =  "";
            boolean dodge =         false;

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right = _sbBlockData.indexOf(" ", pos_left)) > -1)
                strAttackType = _sbBlockData.substring(pos_left, pos_right);

            // skip " at " and get the attacked name...
            pos_left = pos_right + 1;
            if ((pos_right = _sbBlockData.indexOf(" ", pos_left)) > -1)
                strAttacked = _sbBlockData.substring(pos_right + 1, _sbBlockData.length());

OMUD.logDebug("Z2: " + strAttacker + " vs " + strAttacked + (dodge ? " (Dodge) " : " (Miss) ") + "(" + strAttackType + ")");

        // ------------------
        // Combat: They Miss
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COLOR_CYAN, MSTR_COMBAT_MISS_END)) > -1){
            mmc.dataStatline.in_combat = true;
            cleanData(_sbBlockData, true, false);

            String strAttacker =    "";
            String strAttacked =    "YOU";
            String strAttackType =  "";
            String strWeapon =      "";
            boolean dodge =         false;

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right = _sbBlockData.indexOf(MSTR_COMBAT_THEY_MISS, pos_left)) > -1 &&
                (pos_left =  _sbBlockData.lastIndexOf(" ", pos_right - 1)) > -1){

                int name_prefix_len = 0;
                if (_sbBlockData.substring(0, MSTR_COMBAT_THE_PRE.length()).equals(MSTR_COMBAT_THE_PRE))
                    name_prefix_len = MSTR_COMBAT_THE_PRE.length();

                strAttacker =    _sbBlockData.substring(name_prefix_len, pos_left);
                strAttackType =  _sbBlockData.substring(pos_left + 1, pos_right);

                // get the attack weapon if shown (some mobs don't show their weapon on a dodge)...
                pos_left = pos_right + MSTR_COMBAT_THEY_MISS.length();
                if ((pos_right = _sbBlockData.lastIndexOf(MSTR_COMBAT_YOU_DODGE, _sbBlockData.length() - 1)) > -1){
                    pos_right--;
                    dodge = true;
                } else pos_right = _sbBlockData.length() - 1;
                if (pos_right > pos_left && 
                    (pos_left = _sbBlockData.lastIndexOf(" ", pos_right)) > -1)
                    strWeapon = _sbBlockData.substring(pos_left + 1, pos_right + 1);
            }

OMUD.logDebug("Z2: " + strAttacker + " vs " + strAttacked + (dodge ? " (Dodge) " : " (Miss) ") + "(" + strAttackType + ")" + (strWeapon.length() > 0 ? " (" + strWeapon + ")" : ""));

        // ------------------
        // EXP Gained
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_EXP_GAIN_PRE, MSTR_EXP_GAIN_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD.DataBlock.eBlockType.EXP;
            int exp = Integer.parseInt(_sbBlockData.toString());
            mmc.dataExp.cur_total   += exp;
            mmc.dataExp.next_rem    -= exp;
            if (mmc.dataExp.next_rem < 0)
                mmc.dataExp.next_rem = 0;

        // ------------------
        // Invalid Move Dir (no exit in that dir)
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_MOVE_NO_EXIT_DIR, "")) > -1){
            mmc.ablk.sbDebug.append("[MSTR_MOVE_NO_EXIT_DIR]\n");

        // ------------------
        // Meditate Wake
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_MEDITATE_WAKE, "")) > -1){
            mmc.ablk.sbDebug.append("[MSTR_MEDITATE_WAKE]\n");

        // ------------------
        // Meditate Won't Help
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_MEDITATE_WONT_HELP, "")) > -1){
            mmc.ablk.sbDebug.append("[MSTR_MEDITATE_WONT_HELP]\n");

        // ------------------
        // Invalid Command (command has no effect)
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_CMD_NO_EFFECT, "")) > -1){
            mmc.ablk.sbDebug.append("[MSTR_CMD_NO_EFFECT]\n");

        // ------------------
        // Regular/Non-ANSI Strings
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_REG_RESTING, "")) > -1){
            mmc.ablk.sbDebug.append("[MSTR_REG_RESTING]\n");
            pos_data_found_start = checkPrefix("Rest Cmd When Already Resting", mmc.ablk.sbDebug, sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_REG_MEDITATING, "")) > -1){
            mmc.ablk.sbDebug.append("[MSTR_REG_MEDITATING]\n");
            pos_data_found_start = checkPrefix("Med Cmd When Already Meditating", mmc.ablk.sbDebug, sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_REG_SPELL_ALREADY_CAST, "")) > -1){
            mmc.ablk.sbDebug.append("[MSTR_REG_SPELL_ALREADY_CAST]\n");

        // ------------------
        // Various ANSI Color-Prefix Strings (LAST)
        // ------------------
        // NOTE: COLOR_OTHER should be last because the gamestring is common amongst others above
        } else if (
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COLOR_MAGENTA,  "")) > -1 ||
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COLOR_CYAN,     "")) > -1 ||
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COLOR_WHITE,    "")) > -1){
            mmc.ablk.sbDebug.append("[COLOR_TEXT]\n" + _sbBlockData.toString() + "\n");
        }

        // ------------------
        // Nonstop/Quit/Continue
        // ------------------
        //} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_NQC, "")) > -1){
        //  mmc.ablk.sbDebug.append("[MSTR_NQC]\n");

        return pos_data_found_start;
    }
}
