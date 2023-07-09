public class OMUD_MMUD_ParseBlockExp extends OMUD_MMUD_ParseBlocks.ParseBlock{
    private final String MSTR_EXP_PRE =             "[0;37;40m[0;32mExp: [36m";
    private final String MSTR_EXP_END =             "%]";
    private final String MSTR_EXP_LEVEL =           "Level:";
    private final String MSTR_EXP_NEXT_TOTAL_PRE =  "(";
    private final String MSTR_EXP_NEXT_TOTAL_END =  ")";

    public boolean getStatlineWait() {return true;}
    public static String getCmdText(){return "exp\n";}
    public OMUD_MMUD_ParseBlockExp(){
        _arrlCmdText.add(new CmdText("experience", 3));
    }

    public int findBlockData(OMUD_Char.MMUD_Data mmd, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_EXP_PRE, MSTR_EXP_END)) > -1){
            mmd.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.EXP;
            mmd.dataExp = new OMUD_MMUD_DataBlockExp();
            cleanData(_sbBlockData, false, true);

            int pos_left =  0;
            int pos_right = 0;
            if ((pos_right = _sbBlockData.indexOf(MSTR_EXP_LEVEL, pos_left)) > -1){
                mmd.dataExp.cur_total = Integer.parseInt(_sbBlockData.substring(pos_left, pos_right).trim());

                if ((pos_left  = _sbBlockData.indexOf(MSTR_EXP_NEXT_TOTAL_PRE, pos_right)) > -1 &&
                    (pos_right = _sbBlockData.indexOf(MSTR_EXP_NEXT_TOTAL_END, pos_left))  > -1){
                    mmd.dataExp.next_total = Integer.parseInt(_sbBlockData.substring(pos_left + MSTR_EXP_NEXT_TOTAL_PRE.length(), pos_right).trim());

                    if (mmd.dataExp.next_total > mmd.dataExp.cur_total)
                         mmd.dataExp.next_rem = mmd.dataExp.next_total - mmd.dataExp.cur_total;
                    else mmd.dataExp.next_rem = 0;
                }
            }
        }

        return pos_data_found_start;
    }
}
