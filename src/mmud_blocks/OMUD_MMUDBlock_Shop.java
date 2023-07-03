public class OMUD_MMUDBlock_Shop extends OMUD_MMUDBlocks.Block{
    private final String MSTR_SHOP_YES =    "[0;37;40m[0;37;40m[79D[KThe following items are for sale here:\n\n[0;32mItem                          [36mQuantity    Price\n------------------------------------------------------\n";
    private final String MSTR_SHOP_NO =     "[0;37;40m[1;31;40mYou cannot LIST if you are not in a shop!\n";
    private final String MSTR_ITEM_NAME =   "[32m";
    private final String MSTR_ITEM_QTY =    "[36m";

    public boolean getStatlineWait()                    {return true;}
    public OMUD_MMUD.DataBlock.eBlockType getDataType() {return OMUD_MMUD.DataBlock.eBlockType.SHOP;}
    public OMUD_MMUDBlock_Shop(){
        _arrlCmdText.add(new CmdText("list", 3));
    }

    public int findBlockData(OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_SHOP_YES, "")) > -1){
            mmc.dataShop = new OMUD_MMUD.DataShop();

            int pos_left = 0;
            int pos_right = 0;
            String[] tokens = _sbBlockData.toString().split("\n");
            for (String token : tokens){
                OMUD_MMUD.DataShop.ShopItem item = new OMUD_MMUD.DataShop.ShopItem();

                // ---------------
                // Item Name
                // ---------------
                pos_left = MSTR_ITEM_NAME.length();
                if ((pos_right = token.indexOf(MSTR_ITEM_QTY, pos_left)) > -1){
                    item.name  = token.substring(pos_left, pos_right).trim();

                    // ---------------
                    // Item Quantity + Price
                    // ---------------
                    pos_left = pos_right + MSTR_ITEM_QTY.length();
                    if ((pos_right = token.indexOf(" ", pos_left)) > -1){
                        item.qty   = Integer.parseInt(token.substring(pos_left, pos_right).trim());
                        item.price = token.substring(pos_right, token.length()).trim();
                    }
                }
                mmc.dataShop.shop_items.add(item);
            }                       
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_SHOP_NO, "")) > -1){
            mmc.dataShop = new OMUD_MMUD.DataShop();
        }

        return pos_data_found_start;
    }
}
