public class OMUD_MMUD_ParseBlockWho extends OMUD_MMUD_ParseBlocks.ParseBlock{
    private final String MSTR_WHO =         "[0;37;40m[79D[K[1;33m         Current Adventurers\n[1;30m         ===================\n\n";
    private final String MSTR_COLOR_PRE =   "[0m";
    private final String MSTR_LINE_END =    " [37m ";
    private final String MSTR_DELIM_LR =    "  -  [35m";
    private final String MSTR_NAME =        "[0;32m";
    private final String MSTR_TITLE_END =   "[32m";
    private final String MSTR_GUILD =       "  of [33m";

    public boolean getStatlineWait() {return true;}
    public static String getCmdText(){return "who\n";}
    public OMUD_MMUD_ParseBlockWho(){
        _arrlCmdText.add(new CmdText("who", 3));
    }

    public int findBlockData(OMUD_Char.MMUD_Data mmd, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_WHO, "")) > -1){
            mmd.apblock.data_type = OMUD_MMUD_DataBlock.eBlockType.WHO;
            mmd.dataWho = new OMUD_MMUD_DataBlockWho();

            int pos_left =  -1;
            int pos_right = -1;

            // remove color prefix from first row...
            if (_sbBlockData.indexOf(MSTR_COLOR_PRE, 0) == 0)
                _sbBlockData.delete(0, MSTR_COLOR_PRE.length());

            // split lines, get data...
            String[] lines = _sbBlockData.toString().split("\n");
            for (String line: lines){

                // strip row-end sequence...
                if ((pos_right = line.lastIndexOf(MSTR_LINE_END, line.length() - 1)) > -1){
                    line = line.substring(0, pos_right);

                    OMUD_MMUD_DataBlockWho.WhoChar whoChar = new OMUD_MMUD_DataBlockWho.WhoChar();

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
                            if (strAlign.length() > 0){
                                for (int i = OMUD_MMUD_DataBlockWho.eAlignment.NEUTRAL.ordinal() + 1; i < OMUD_MMUD_DataBlockWho.ALIGNMENT_STRINGS.length; ++i)
                                    if (strAlign.lastIndexOf(OMUD_MMUD_DataBlockWho.ALIGNMENT_STRINGS[i], strAlign.length() - 1) > -1){
                                        whoChar.alignment = OMUD_MMUD_DataBlockWho.eAlignment.values()[i];
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

                        mmd.dataWho.chars.add(whoChar);
                    }
                }
            }
        }

        return pos_data_found_start;
    }
}
