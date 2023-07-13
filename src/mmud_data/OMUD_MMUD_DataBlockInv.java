import java.util.ArrayList;

public class OMUD_MMUD_DataBlockInv extends OMUD_MMUD_DataBlock{
    public enum eEncLevel{
        NONE,
        LIGHT,
        MEDIUM,
        HEAVY
    }

    public static final String[] ENC_LEVEL_STRINGS = {
        "None",
        "Light",
        "Medium",
        "Heavy"
    };

    public int          wealth =        -1; // in copper
    public int          enc_cur =       -1;
    public int          enc_max =       -1;
    public String       enc_level =     "";
    public OMUD_MMUD_DataCoins coins =  new OMUD_MMUD_DataCoins();
    public ArrayList<OMUD_MMUD_DataItem> arrlItems = new ArrayList<OMUD_MMUD_DataItem>();
    public ArrayList<OMUD_MMUD_DataItem> arrlWorn  = new ArrayList<OMUD_MMUD_DataItem>();
    public ArrayList<OMUD_MMUD_DataItem> arrlKeys =  new ArrayList<OMUD_MMUD_DataItem>();

    public eBlockType getType(){return eBlockType.INV;}
    public OMUD_MMUD_DataBlockInv(){}
    public OMUD_MMUD_DataBlockInv(OMUD_MMUD_DataBlockInv di){
        wealth =        di.wealth;
        enc_cur =       di.enc_cur;
        enc_max =       di.enc_max;
        enc_level =     new String(di.enc_level);
        coins =         new OMUD_MMUD_DataCoins(di.coins);
        for (int i = 0; i < di.arrlItems.size(); ++i)
            arrlItems.add(new OMUD_MMUD_DataItem(di.arrlItems.get(i)));
        for (int i = 0; i < di.arrlWorn.size(); ++i)
            arrlWorn.add(new OMUD_MMUD_DataItem(di.arrlWorn.get(i)));
        for (int i = 0; i < di.arrlKeys.size(); ++i)
            arrlKeys.add(new OMUD_MMUD_DataItem(di.arrlKeys.get(i)));
    }
}
