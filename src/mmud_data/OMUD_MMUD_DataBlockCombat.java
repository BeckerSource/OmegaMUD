import java.util.ArrayList;

public class OMUD_MMUD_DataBlockCombat extends OMUD_MMUD_DataBlock{
    public static class CombatLine{
        public OMUD_MMUD_DataUnit unit = new OMUD_MMUD_DataUnit();
        public String   weapon =    "";
        public String   action =    "";
        public String   tgt_name =  "";
        public int      tgt_dmg =   0;
        public boolean  tgt_miss  = false;
        public boolean  tgt_dodge = false;

        CombatLine(){}
        CombatLine(boolean miss){tgt_miss = miss;}
        CombatLine(CombatLine cl){
            unit =      new OMUD_MMUD_DataUnit(cl.unit);
            weapon =    new String(cl.weapon);
            action =    new String(cl.action);
            tgt_name =  new String(cl.tgt_name);
            tgt_dmg =   cl.tgt_dmg;
            tgt_miss =  cl.tgt_miss;
            tgt_dodge = cl.tgt_dodge;
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

    public ArrayList<CombatLine> lines = new ArrayList<CombatLine>();

    public eBlockType getType(){return eBlockType.COMBAT;}
    OMUD_MMUD_DataBlockCombat(){}
    OMUD_MMUD_DataBlockCombat(OMUD_MMUD_DataBlockCombat dc){
        //for (int i = 0; i < dc.rounds.size(); ++i)
        //    rounds.add(new CombatRound(dc.rounds.get(i)));
        for (int i = 0; i < dc.lines.size(); ++i)
            lines.add(new CombatLine(dc.lines.get(i)));
    }
}
