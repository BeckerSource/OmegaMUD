import java.util.ArrayList;

public class OMUD_MMUD_DataBlockCombat extends OMUD_MMUD_DataBlock{
    public static class CombatLine{
        public static enum eHitType{
            HIT,
            MISS,
            DODGE,
            FAIL
        }

        public static final String[] HIT_TYPE_STRINGS = {
            "HIT",
            "MISS",
            "DODGE",
            "FAIL"
        };

        public OMUD_MMUD_DataUnit unit = new OMUD_MMUD_DataUnit();
        public String       unit_action =   "";
        public String       tgt_name =      "";
        public String       tgt_weapon =    "";
        public int          tgt_dmg =       0;
        public int          tgt_exp =       0;
        public eHitType     tgt_htype =     eHitType.HIT;

        CombatLine(){}
        CombatLine(eHitType htype){tgt_htype = htype;}
        CombatLine(CombatLine cl){
            unit =          new OMUD_MMUD_DataUnit(cl.unit);
            unit_action =   new String(cl.unit_action);
            tgt_name =      new String(cl.tgt_name);
            tgt_weapon =    new String(cl.tgt_weapon);
            tgt_dmg =       cl.tgt_dmg;
            tgt_exp =       cl.tgt_exp;
            tgt_htype =     cl.tgt_htype;
        }
    }

    /*
    public static class CombatRound{
        public ArrayList<CombatLine> lines = new ArrayList<CombatLine>();

        public CombatRound(){}
        public CombatRound(CombatRound cr){
            for (int i = 0; i < cr.lines.size(); ++i)
                lines.add(new CombatLine(cr.lines.get(i)));
        }
    }
    public ArrayList<CombatRound> rounds = new ArrayList<CombatRound>();
    */

    public int exp_gained = 0;
    public ArrayList<CombatLine> lines = new ArrayList<CombatLine>();

    public eBlockType getType(){return eBlockType.COMBAT;}
    OMUD_MMUD_DataBlockCombat(){}
    OMUD_MMUD_DataBlockCombat(OMUD_MMUD_DataBlockCombat dc){
        exp_gained = dc.exp_gained;
        //for (int i = 0; i < dc.rounds.size(); ++i)
        //    rounds.add(new CombatRound(dc.rounds.get(i)));
        for (int i = 0; i < dc.lines.size(); ++i)
            lines.add(new CombatLine(dc.lines.get(i)));
    }
}
