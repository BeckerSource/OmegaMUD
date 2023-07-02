public class OMUD_MMUDBlock_Spells extends OMUD_MMUDBlocks.Block{
    private final String MSTR_SPELLS =      "[0;37;40m[79D[K[1;37mYou have the following spells:\n[0;35mLevel Mana Short Spell Name\n";
    private final String MSTR_POWERS =      "[0;37;40m[79D[K[1;37mYou have the following powers:\n[0;35mLevel Kai  Short Spell Name\n";
    private final String MSTR_COLOR_PRE =   "[36m";

    public boolean getStatlineWait()                    {return true;}
    public OMUD_MMUD.DataBlock.eBlockType getDataType() {return OMUD_MMUD.DataBlock.eBlockType.SPELLS;}
    public OMUD_MMUDBlock_Spells(){
        _arrlCmdText.add(new CmdText("spells", 2));
        _arrlCmdText.add(new CmdText("powers", 2));
    }

    public int findBlockData(OMUD_IMUDEvents omme, OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_SPELLS, "")) > -1 ||
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_POWERS, "")) > -1){
            
            // remove color prefix from first entry...
            if (_sbBlockData.indexOf(MSTR_COLOR_PRE, 0) == 0)
                _sbBlockData.delete(0, MSTR_COLOR_PRE.length());

            int pos_left =  -1;
            int pos_right = -1;
            mmc.dataSpells = new OMUD_MMUD.DataSpells();

            // split lines, get spell data...
            String[] lines = _sbBlockData.toString().split("\n");
            for (String line: lines) {
                line = line.trim();

                OMUD_MMUD.DataSpell spell = new OMUD_MMUD.DataSpell();

                // ---------------
                // Spell Level
                // ---------------
                pos_left = 0;
                if ((pos_right = line.indexOf(" ", pos_left)) > -1){
                    spell.level = Integer.parseInt(line.substring(pos_left, pos_right));
                    pos_left = pos_right;

                    // ---------------
                    // Spell Cost
                    // ---------------
                    while (pos_left + 1 < line.length() && line.charAt(++pos_left) == ' '){/*go right until non-space*/}
                    if ((pos_right = line.indexOf(" ", pos_left)) > -1){
                        spell.cost = Integer.parseInt(line.substring(pos_left, pos_right));
                        pos_left = pos_right;

                        // ---------------
                        // Spell Names
                        // ---------------
                        while (pos_left + 1 < line.length() && line.charAt(++pos_left) == ' '){/*go right until non-space*/}
                        if ((pos_right = line.indexOf(" ", pos_left)) > -1){
                            spell.name_short =  line.substring(pos_left,  pos_right);
                            spell.name_long =   line.substring(pos_right, line.length()).trim();
                            mmc.dataSpells.spells.add(spell);
                        }
                    }
                }
            }
        }

        return pos_data_found_start;
    }
}
