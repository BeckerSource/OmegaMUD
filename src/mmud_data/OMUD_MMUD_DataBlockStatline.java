public class OMUD_MMUD_DataBlockStatline extends OMUD_MMUD_DataBlock{
    public static enum eRestState{
        READY, // not a real game state, just a state for non-rest/med
        REST,
        MED
    }
    public static final String[] REST_STATE_STRINGS = {
        "[READY]",
        "[REST]",
        "[MED]"
    };
    public static final String MSTR_COMBAT_STATE =  "[COMBAT]";
    public static final String MSTR_SLINE_MA =      "MA";

    public int      hp_cur =        0;
    public int      hp_max =        0;
    public int      ma_cur =        0;
    public int      ma_max =        0;
    public boolean  hp_mod =        false;
    public boolean  ma_mod =        false;
    public String   hp_str =        "";
    public String   ma_str =        "";
    public boolean  in_combat =     false;
    public eRestState rest_state =  eRestState.READY;

    public eBlockType getType(){return eBlockType.STATLINE;}
    public OMUD_MMUD_DataBlockStatline(){}
    public OMUD_MMUD_DataBlockStatline(OMUD_MMUD_DataBlockStatline dsl){
        hp_cur = dsl.hp_cur;
        hp_max = dsl.hp_max;
        ma_cur = dsl.ma_cur;
        ma_max = dsl.ma_max;
        hp_mod = dsl.hp_mod;
        ma_mod = dsl.ma_mod;
        hp_str = new String(dsl.hp_str);
        ma_str = new String(dsl.ma_str);
        in_combat =  dsl.in_combat;
        rest_state = dsl.rest_state;
    }
}
