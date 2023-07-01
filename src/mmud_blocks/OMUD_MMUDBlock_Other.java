public class OMUD_MMUDBlock_Other extends OMUD_MMUDBlocks.Block{
    private final String MSTR_PREFIX_RESET_WHBL =       "[0;37;40m";
    private final String MSTR_COMBAT_ON =               "[79D[K[0;33m*Combat Engaged*";
    private final String MSTR_COMBAT_OFF =              "[79D[K[0;33m*Combat Off*";
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
    private final String MSTR_EXP_GAIN =                "[79D[KYou gain 56 experience.";
    //private final String MSTR_NQC =                   "[0m(N)onstop, (Q)uit, or (C)ontinue?";

    public boolean getStatlineWait()                {return false;}
    public OMUD_MMUD.DataBlock.eBlockType getDataType() {return OMUD_MMUD.DataBlock.eBlockType.NONE;}
    public OMUD_MMUDBlock_Other(){}
        
    public int findBlockData(OMUD_IMUDEvents omme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        // ------------------
        // Invalid Move Dir (no exit in that dir)
        // ------------------
        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_MOVE_NO_EXIT_DIR, "")) > -1){
            omme.notifyMUDOther("[MSTR_MOVE_NO_EXIT_DIR]\n");

        // ------------------
        // Meditate Wake
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_MEDITATE_WAKE, "")) > -1){
            omme.notifyMUDOther("[MSTR_MEDITATE_WAKE]\n");

        // ------------------
        // Meditate Won't Help
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_MEDITATE_WONT_HELP, "")) > -1){
            omme.notifyMUDOther("[MSTR_MEDITATE_WONT_HELP]\n");

        // ------------------
        // Invalid Command (command has no effect)
        // ------------------
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_CMD_NO_EFFECT, "")) > -1){
            omme.notifyMUDOther("[MSTR_CMD_NO_EFFECT]\n");

        // ------------------
        // Nonstop/Quit/Continue
        // ------------------
        //} else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_NQC, "")) > -1){
        //  omme.notifyMUDOther("[MSTR_NQC]\n");

        // ------------------
        // Regular/Non-ANSI Strings
        // ------------------
        // capture the text so that it's deleted correctly from the buffer (maybe do stuff with it later?)
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_REG_RESTING, "")) > -1){
            omme.notifyMUDOther("[MSTR_REG_RESTING]\n");
            // PREFIX: can have a clear white/black prefix if was already resting
            pos_data_found_start = checkPrefix("Rest Cmd When Already Resting", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_REG_MEDITATING, "")) > -1){
            omme.notifyMUDOther("[MSTR_REG_MEDITATING]\n");
            // PREFIX: can have a clear white/black prefix if was already resting
            pos_data_found_start = checkPrefix("Med Cmd When Already Meditating", sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_REG_SPELL_ALREADY_CAST, "")) > -1){
            omme.notifyMUDOther("[MSTR_REG_SPELL_ALREADY_CAST]\n");

        // ------------------
        // Various ANSI Color-Prefix Strings (LAST)
        // ------------------
        // NOTE: COLOR_OTHER should be last because the gamestring is common amongst others above
        } else if (
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COLOR_MAGENTA, "")) > -1 ||
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COLOR_CYAN, ""))    > -1 ||
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_COLOR_WHITE, ""))   > -1){
            //omme.notifyMUDOther("[COLOR_TEXT]\n" + _sbBlockData.toString() + "\n");
        }

        return pos_data_found_start;
    }
}
