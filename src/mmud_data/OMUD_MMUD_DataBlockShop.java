import java.util.ArrayList;

public class OMUD_MMUD_DataBlockShop extends OMUD_MMUD_DataBlock{
    public static class ShopItem{
        public String   name =      "";
        public int      qty =       0;
        public boolean  can_use =   true;
        public OMUD_MMUD_DataCoins coins = new OMUD_MMUD_DataCoins();

        public ShopItem(){}
        public ShopItem(ShopItem si){
            name =      new String(si.name);
            qty =       si.qty;
            can_use =   si.can_use;
            coins =     new OMUD_MMUD_DataCoins(si.coins);
        }

        public String getPriceString(){
            StringBuilder sbPrice = new StringBuilder();
            if (coins.runic     > 0)
                sbPrice.append(coins.runic + " " + OMUD_MMUD_DataCoins.COIN_TYPE_STRINGS[OMUD_MMUD_DataCoins.eCoinType.RUNIC.ordinal()]);
            if (coins.plat      > 0)
                sbPrice.append((sbPrice.length() > 0 ? ", " : "") + coins.plat    + " " + OMUD_MMUD_DataCoins.COIN_TYPE_STRINGS[OMUD_MMUD_DataCoins.eCoinType.PLATINUM.ordinal()]);
            if (coins.gold      > 0)
                sbPrice.append((sbPrice.length() > 0 ? ", " : "") + coins.gold    + " " + OMUD_MMUD_DataCoins.COIN_TYPE_STRINGS[OMUD_MMUD_DataCoins.eCoinType.GOLD.ordinal()]);
            if (coins.silver    > 0)
                sbPrice.append((sbPrice.length() > 0 ? ", " : "") + coins.silver  + " " + OMUD_MMUD_DataCoins.COIN_TYPE_STRINGS[OMUD_MMUD_DataCoins.eCoinType.SILVER.ordinal()]);
            if (coins.copper    > 0)
                sbPrice.append((sbPrice.length() > 0 ? ", " : "") + coins.copper  + " " + OMUD_MMUD_DataCoins.COIN_TYPE_STRINGS[OMUD_MMUD_DataCoins.eCoinType.COPPER.ordinal()]);
            if (sbPrice.length() == 0)
                sbPrice.append("FREE");
            return sbPrice.toString();
        }
    }
    
    public String megaID =      "";
    public String roomName =    "";
    public ArrayList<ShopItem> shop_items = new ArrayList<ShopItem>();

    public eBlockType getType(){return eBlockType.SHOP;}
    public OMUD_MMUD_DataBlockShop(){}
    public OMUD_MMUD_DataBlockShop(OMUD_MMUD_DataBlockShop ds){
        megaID =    new String(ds.megaID);
        roomName =  new String(ds.roomName);
        for (int i = 0; i < ds.shop_items.size(); ++i)
            shop_items.add(new ShopItem(ds.shop_items.get(i)));
    }
}
