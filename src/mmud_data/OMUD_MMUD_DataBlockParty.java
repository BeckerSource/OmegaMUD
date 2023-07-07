import java.util.ArrayList;

public class OMUD_MMUD_DataBlockParty extends OMUD_MMUD_DataBlock{
    public class PartyMember{
        public String   name =          "";
        public String   party_class =   "";
        public int      hp_cur =        0;
        public int      ma_cur =        0;
        public int      hp_max =        0;
        public int      ma_max =        0;
        public String   rank =          "";
    }
    public ArrayList<PartyMember> members = new ArrayList<PartyMember>();

    public eBlockType getType(){return eBlockType.PARTY;}
}
