public class OMUD_MMUDBlock_Who extends OMUD_MMUDBlocks.Block{
    private final String MSTR_WHO =         "[0;37;40m[79D[K[1;33m         Current Adventurers\n[1;30m         ===================\n\n";
    private final String MSTR_COLOR_PRE =   "[0m";
    private final String MSTR_LINE_END =    " [37m ";
    private final String MSTR_DELIM_LR =    "  -  [35m";
    private final String MSTR_NAME =        "[0;32m";
    private final String MSTR_TITLE_END =   "[32m";
    private final String MSTR_GUILD =       "  of [33m";

    public boolean getStatlineWait()                    {return true;}
    public OMUD_MMUD.DataBlock.eBlockType getDataType() {return OMUD_MMUD.DataBlock.eBlockType.WHO;}
    public OMUD_MMUDBlock_Who(){
        _arrlCmdText.add(new CmdText("who", 3));
    }

    public int findBlockData(OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_WHO, "")) > -1){            
            // remove color prefix from first row...
            if (_sbBlockData.indexOf(MSTR_COLOR_PRE, 0) == 0)
                _sbBlockData.delete(0, MSTR_COLOR_PRE.length());

            int pos_left =  -1;
            int pos_right = -1;
            mmc.dataWho = new OMUD_MMUD.DataWho();

            // split lines, get data...
            String[] lines = _sbBlockData.toString().split("\n");
            for (String line: lines){

                // strip row-end sequence...
                if ((pos_right = line.lastIndexOf(MSTR_LINE_END, line.length() - 1)) > -1){
                    line = line.substring(0, pos_right);

                    OMUD_MMUD.DataWho.DataWhoChar whoChar = new OMUD_MMUD.DataWho.DataWhoChar();

                    // ---------------
                    // Left/Right Sections
                    // ---------------
                    pos_left = 0;
                    if ((pos_right = line.indexOf(MSTR_DELIM_LR, pos_left)) > -1){
                        String strLeft =  line.substring(pos_left, pos_right);
                        String strRight = line.substring(pos_right + MSTR_DELIM_LR.length(), line.length());

                        // ---------------
                        // Char Alignment + First/Last Name
                        // ---------------
                        pos_right = strLeft.length() - 1;
                        if ((pos_left = strLeft.lastIndexOf(MSTR_NAME, pos_right)) > -1){
                            whoChar.name_first = strLeft.substring(pos_left + MSTR_NAME.length(), pos_right + 1).trim();

                            String[] tokens = whoChar.name_first.split(" ");
                            if (tokens.length == 2){
                                whoChar.name_first = tokens[0];
                                whoChar.name_last =  tokens[1];
                            }

                            // if title is non-neutral, more data to the left (skip the pre-space)...
                            pos_right = pos_left - 1;
                            String strAlign = strLeft.substring(0, pos_right).trim();
                            if (strAlign.length() > 0 &&
                                (pos_left = strLeft.lastIndexOf(" ", --pos_right)) > -1){

                                strAlign = strLeft.substring(pos_left + 1, pos_right + 1);
                                for (int i = OMUD_MMUD.eAlignment.NEUTRAL.ordinal() + 1; i < OMUD_MMUD.ALIGNMENT_STRINGS.length; ++i)
                                    if (strAlign.equals(OMUD_MMUD.ALIGNMENT_STRINGS[i])){
                                        whoChar.alignment = OMUD_MMUD.eAlignment.values()[i];
                                        break;
                                    }                                    
                            }
                        }

                        // ---------------
                        // Char Title + Guild
                        // ---------------
                        pos_left = 0;
                        if ((pos_right = strRight.indexOf(MSTR_TITLE_END, pos_left)) > -1){
                            whoChar.title = strRight.substring(pos_left, pos_right);

                            pos_left = pos_right;
                            if ((pos_left = strRight.indexOf(MSTR_GUILD, pos_left)) > -1)
                                whoChar.guild = strRight.substring(pos_left + MSTR_GUILD.length(), strRight.length());
                        }

                        mmc.dataWho.chars.add(whoChar);
                    }
                }
            }
        }

        return pos_data_found_start;
    }
}
