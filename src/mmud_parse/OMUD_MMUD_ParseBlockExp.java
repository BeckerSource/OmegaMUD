public class OMUD_MMUD_ParseBlockExp extends OMUD_MMUD_ParseBlocks.ParseBlock{
    private final String MSTR_EXP_PRE =             "[0;37;40m[0;32mExp: [36m";
    private final String MSTR_EXP_END =             "%]";
    private final String MSTR_EXP_LEVEL =           "Level:";
    private final String MSTR_EXP_NEXT_TOTAL_PRE =  "(";
    private final String MSTR_EXP_NEXT_TOTAL_END =  ")";

    public boolean getStatlineWait(){return true;}
    public OMUD_MMUD_ParseBlockExp(){
        _arrlCmdText.add(new CmdText(OMUD_MMUD_DataBlock.CMD_STRINGS[OMUD_MMUD_DataBlock.eBlockType.EXP.ordinal()], 3));
    }

    public int findBlockData(OMUD_MMUD_Char mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_EXP_PRE, MSTR_EXP_END)) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.EXP;
            mmc.dataExp = new OMUD_MMUD_DataBlockExp();
            cleanData(_sbBlockData, false, true);

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right = _sbBlockData.indexOf(MSTR_EXP_LEVEL, pos_left)) > -1){
                mmc.dataExp.cur_total = Integer.parseInt(_sbBlockData.substring(pos_left, pos_right).trim());

                if ((pos_left  = _sbBlockData.indexOf(MSTR_EXP_NEXT_TOTAL_PRE, pos_right)) > -1 &&
                    (pos_right = _sbBlockData.indexOf(MSTR_EXP_NEXT_TOTAL_END, pos_left))  > -1){
                    mmc.dataExp.next_total = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_EXP_NEXT_TOTAL_PRE.length(), pos_right).trim());

                    if (mmc.dataExp.next_total > mmc.dataExp.cur_total)
                         mmc.dataExp.next_rem = mmc.dataExp.next_total - mmc.dataExp.cur_total;
                    else mmc.dataExp.next_rem = 0;
                }
            }
        }

        return pos_data_found_start;
    }
}
