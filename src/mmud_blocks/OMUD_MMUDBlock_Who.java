public class OMUD_MMUDBlock_Who extends OMUD_MMUDBlocks.Block{
    private final String MSTR_WHO =         "[0;37;40m[79D[K[1;33m         Current Adventurers\n[1;30m         ===================\n\n";
    private final String MSTR_DELIM_LR =    " - ";
    private final String MSTR_DELIM_GUILD = " of ";

    public boolean getStatlineWait()                    {return true;}
    public OMUD_MMUD.DataBlock.eBlockType getDataType() {return OMUD_MMUD.DataBlock.eBlockType.WHO;}
    public OMUD_MMUDBlock_Who(){
        _arrlCmdText.add(new CmdText("who", 3));
    }

    public int findBlockData(OMUD_IMUDEvents omme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_WHO, "")) > -1){            
            cleanData(_sbBlockData, false, true);
            mmc.dataWho = new OMUD_MMUD.DataWho();

            int pos_left =  -1;
            int pos_right = -1;

            // split lines, get spell data...
            String[] lines = _sbBlockData.toString().split("\n");
            for (String line: lines){
                line = line.trim();

                OMUD_MMUD.DataWho.DataWhoChar whoChar = new OMUD_MMUD.DataWho.DataWhoChar();

                // ---------------
                // Left/Right Sections
                // ---------------
                pos_left = 0;
                if ((pos_right = line.indexOf(MSTR_DELIM_LR, pos_left)) > -1){
                    String strLeft =  line.substring(pos_left, pos_right).trim();
                    String strRight = line.substring(pos_right + MSTR_DELIM_LR.length(), line.length()).trim();

                    // ---------------
                    // Char Alignment + First/Last Name
                    // ---------------
                    String[] tokens = strLeft.split(" +");  // regex remove multiple spaces
                    if (tokens.length >= 1){
                        int pos_token = 0;

                        // need at least two tokens for an alignment string...
                        if (tokens.length >= 2){
                            // neutral alignment is a blank string on the who list, so start on enum after...
                            for (int i = OMUD_MMUD.eAlignment.NEUTRAL.ordinal() + 1; i < OMUD_MMUD.ALIGNMENT_STRINGS.length && pos_token == 0; ++i)
                                if (tokens[pos_token].equals(OMUD_MMUD.ALIGNMENT_STRINGS[i])){
                                    whoChar.alignment = OMUD_MMUD.eAlignment.values()[i];
                                    pos_token++;
                                }                        
                        }
                        // get first and (optional) last name...
                        whoChar.name_first = tokens[pos_token++];
                        if (pos_token < tokens.length)
                            whoChar.name_last = tokens[pos_token];
                    }

                    // ---------------
                    // Char Title + Guild
                    // ---------------
                    pos_left = 0;
                    if ((pos_right = strRight.indexOf(MSTR_DELIM_GUILD, pos_left)) > -1)
                         whoChar.guild = strRight.substring(pos_right + MSTR_DELIM_GUILD.length(), strRight.length());
                    else pos_right = strRight.length();
                    whoChar.title = strRight.substring(pos_left, pos_right).trim();

                    mmc.dataWho.chars.add(whoChar);
                }                
            }
        }

        return pos_data_found_start;
    }
}
