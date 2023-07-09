public abstract class OMUD_MMUD_DataBlock{
    public static enum eBlockType{
        STATLINE,
        ROOM,
        INV,
        STATS,
        EXP,
        SHOP,
        SPELLS,
        WHO,
        PARTY,
        COMBAT
    }
    
    public abstract eBlockType getType();
}
