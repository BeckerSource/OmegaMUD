import java.util.ArrayList;

public class OMUD_MMUD_DataBlockSpells extends OMUD_MMUD_DataBlock{
    public static class Spell{
        public int      level =         0;
        public int      cost =          0;
        public String   name_short =    "";
        public String   name_long =     "";

        Spell(){}
        Spell(Spell sp){
            level =         sp.level;
            cost  =         sp.cost;
            name_short =    new String(sp.name_short);
            name_long =     new String(sp.name_long);
        }
    }
    public ArrayList<Spell> spells = new ArrayList<Spell>();

    public eBlockType getType(){return eBlockType.SPELLS;}
    public OMUD_MMUD_DataBlockSpells(){}
    public OMUD_MMUD_DataBlockSpells(OMUD_MMUD_DataBlockSpells ds){
        for (int i = 0; i < ds.spells.size(); ++i)
            spells.add(new Spell(ds.spells.get(i)));
    }
}
