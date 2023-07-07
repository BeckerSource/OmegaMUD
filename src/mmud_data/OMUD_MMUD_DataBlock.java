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

    public static final String[] CMD_STRINGS = {
        "",
        "look",
        "i",
        "stat",
        "experience",
        "list",
        "spells",
        "who",
        "party",
        ""
    };
    public static final String MSTR_CMD_SPELLS_KAI = "powers";

    public abstract eBlockType getType();
}
