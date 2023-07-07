public class OMUD_MMUD_ParseBlockShop extends OMUD_MMUD_ParseBlocks.ParseBlock{
    private final String MSTR_SHOP_YES =    "[0;37;40m[0;37;40m[79D[KThe following items are for sale here:\n\n[0;32mItem                          [36mQuantity    Price\n------------------------------------------------------\n";
    private final String MSTR_SHOP_NO =     "[0;37;40m[1;31;40mYou cannot LIST if you are not in a shop!\n";
    private final String MSTR_ITEM_NAME =   "[32m";
    private final String MSTR_ITEM_QTY =    "[36m";
    private final String MSTR_NO_USE =      " (You can't use)";
    private final String MSTR_FREE =        "Free";

    public boolean getStatlineWait(){return true;}
    public OMUD_MMUD_ParseBlockShop(){
        _arrlCmdText.add(new CmdText(OMUD_MMUD_DataBlock.CMD_STRINGS[OMUD_MMUD_DataBlock.eBlockType.SHOP.ordinal()], 3));
    }

    public int findBlockData(OMUD_MMUD_Char mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_SHOP_YES, "")) > -1){
            mmc.ablk.data_type = OMUD_MMUD_DataBlock.eBlockType.SHOP;
            mmc.dataShop = new OMUD_MMUD_DataBlockShop();
            mmc.dataShop.megaID =   mmc.dataRoom.megaID;
            mmc.dataShop.roomName = mmc.dataRoom.name;

            int pos_left = 0;
            int pos_right = 0;
            String[] tokens = _sbBlockData.toString().split("\n");
            for (String token : tokens){
                OMUD_MMUD_DataBlockShop.ShopItem item = new OMUD_MMUD_DataBlockShop.ShopItem();

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
                        item.qty = Integer.parseInt(token.substring(pos_left, pos_right).trim());
                        String strPrice = token.substring(pos_right, token.length()).trim();

                        // check for "can't use"...
                        if ((pos_right = strPrice.lastIndexOf(MSTR_NO_USE, strPrice.length() - 1)) > -1){
                            strPrice = strPrice.substring(0, pos_right);
                            item.can_use = false;
                        }

                        // get price/coins (checks for free): price defaults to zero...
                        if (!strPrice.equals(MSTR_FREE) &&
                            (pos_right = strPrice.indexOf(" ", 0)) > -1){
                            findCoins(item.coins, 
                                Integer.parseInt(strPrice.substring(0, pos_right)), 
                                strPrice.substring(pos_right + 1, strPrice.length()));
                            item.coins.convert(true); // default up-conversion
                        }
                    }
                }
                mmc.dataShop.shop_items.add(item);
            }
        } else if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, false, MSTR_SHOP_NO, "")) > -1){
            mmc.ablk.sbDebug.append("[NO_SHOP_IN_ROOM]\n");
        }

        return pos_data_found_start;
    }
}
