public class OMUD_MMUD_DataBlockExp extends OMUD_MMUD_DataBlock{
    public int cur_total =   0;
    public int next_total = -1;
    public int next_rem =   -1;
    public int per_hr =      0;
    public int combat_gain = 0;

    public eBlockType getType(){return eBlockType.EXP;}
    public OMUD_MMUD_DataBlockExp(){}
    public OMUD_MMUD_DataBlockExp(OMUD_MMUD_DataBlockExp de){
        cur_total =     de.cur_total;
        next_total =    de.next_total;
        next_rem =      de.next_rem;
        per_hr =        de.per_hr;
        combat_gain =   de.combat_gain;
    }
}
