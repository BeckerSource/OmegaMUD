public class OMUD_MMUD_DataCoins{
    public static enum eCoinType{
        COPPER,
        SILVER,
        GOLD,
        PLATINUM,
        RUNIC
    }

    public static final String[] COIN_TYPE_STRINGS = {
        "copper",
        "silver",
        "gold",
        "platinum",
        "adamantite"    // BBS can have custom runic name
    };

    // singular coind end-types
    public static final String[] COIN_DESC_STRINGS = {
        "farthing",
        "noble",
        "crown",
        "piece",
        "piece"
    };
    
    // base (singular) full names
    public static final String[] COIN_ITEM_STRINGS = {
        COIN_TYPE_STRINGS[eCoinType.COPPER.ordinal()]   + " " + COIN_DESC_STRINGS[eCoinType.COPPER.ordinal()],
        COIN_TYPE_STRINGS[eCoinType.SILVER.ordinal()]   + " " + COIN_DESC_STRINGS[eCoinType.SILVER.ordinal()],
        COIN_TYPE_STRINGS[eCoinType.GOLD.ordinal()]     + " " + COIN_DESC_STRINGS[eCoinType.GOLD.ordinal()],
        COIN_TYPE_STRINGS[eCoinType.PLATINUM.ordinal()] + " " + COIN_DESC_STRINGS[eCoinType.PLATINUM.ordinal()],
        COIN_TYPE_STRINGS[eCoinType.RUNIC.ordinal()]    + " " + COIN_DESC_STRINGS[eCoinType.RUNIC.ordinal()]
    };

    public int runic =   0;
    public int plat =    0;
    public int gold =    0;
    public int silver =  0;
    public int copper =  0;
    public OMUD_MMUD_DataCoins(){}
    public OMUD_MMUD_DataCoins(OMUD_MMUD_DataCoins dc){
        runic =   dc.runic;
        plat =    dc.plat;
        gold =    dc.gold;
        silver =  dc.silver;
        copper =  dc.copper;
    }

    public void convert(boolean convert_up){
        // convert up: get max coins for each type...
        if (convert_up){
            if (copper  >= 100){
                silver  += copper / 100;
                copper   = copper % 100;
            }
            if (silver  >= 100){
                gold    += silver / 100;
                silver   = silver % 100;
            }
            if (gold    >= 100){
                plat    += gold   / 100;
                gold     = gold   % 100;
            }
            if (plat    >= 100){
                runic   += plat   / 100;
                plat     = plat   % 100;
            }
        // convert down: convert all to copper...
        } else {
            plat    += runic  * 100;
            runic    = 0;
            // ---------------------
            gold    += plat   * 100;
            plat     = 0;
            // ---------------------
            silver  += gold   * 100;
            gold     = 0;
            // ---------------------
            copper  += silver * 100;
            silver   = 0;
        }
    }
}
