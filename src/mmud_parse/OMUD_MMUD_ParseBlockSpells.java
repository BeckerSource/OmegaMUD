public class OMUD_MMUD_ParseBlockSpells extends OMUD_MMUD_ParseBlocks.ParseBlock{
    private final String MSTR_SPELLS =      "[0;37;40m[79D[K[1;37mYou have the following spells:\n[0;35mLevel Mana Short Spell Name\n";
    private final String MSTR_POWERS =      "[0;37;40m[79D[K[1;37mYou have the following powers:\n[0;35mLevel Kai  Short Spell Name\n";
    private final String MSTR_COLOR_PRE =   "[36m";

    public boolean getStatlineWait() {return true;}
    public static String getCmdText(boolean is_kai){return is_kai ? "po\n" : "sp\n";}
    public OMUD_MMUD_ParseBlockSpells(){
        _arrlCmdText.add(new CmdText("spells", 2));
        _arrlCmdText.add(new CmdText("powers", 2));
    }

    public int findBlockData(OMUD_Char.MMUD_Data mmd, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_SPELLS, "")) > -1 ||
            (pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_POWERS, "")) > -1){
            mmd.apblock.data_type = OMUD_MMUD_DataBlock.eBlockType.SPELLS;
            mmd.dataSpells = new OMUD_MMUD_DataBlockSpells();

            int pos_left =  -1;
            int pos_right = -1;
        
            // remove color prefix from first row...
            if (_sbBlockData.indexOf(MSTR_COLOR_PRE, 0) == 0)
                _sbBlockData.delete(0, MSTR_COLOR_PRE.length());

            // split lines, get data...
            String[] lines = _sbBlockData.toString().split("\n");
            for (String line: lines){
                line = line.trim();

                OMUD_MMUD_DataBlockSpells.Spell spell = new OMUD_MMUD_DataBlockSpells.Spell();

                String[] tokens = line.split(" +"); // regex remove multiple spaces
                if (tokens.length >= 4){
                    spell.level =       Integer.parseInt(tokens[0]);
                    spell.cost =        Integer.parseInt(tokens[1]);
                    spell.name_short =  tokens[2];

                    // concat the long name...
                    StringBuilder sb = new StringBuilder();
                    for (int i = 3; i < tokens.length; ++i)
                        sb.append(tokens[i] + (i + 1 < tokens.length ? " " : ""));
                    spell.name_long = sb.toString();

                    mmd.dataSpells.spells.add(spell);
                }
            }
        }

        return pos_data_found_start;
    }
}
