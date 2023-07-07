import java.util.ArrayList;

public class OMUD_MMUD_DataBlockRoom extends OMUD_MMUD_DataBlock{
    public static enum eRoomLight{
        NORMAL,
        DIMLY_LIT,
        VERY_DARK,
        BARELY_VIS,
        PITCH_BLACK
    }
    public static final String[] ROOM_LIGHT_STRINGS = {
        "Light: Normal",
        "Light: Dimly Lit",
        "Light: Very Dark",
        "Light: Barely Visible",
        "Light: Pitch Black"
    };

    public String       megaID =        ""; // MegaMUD RoomID
    public String       name =          "";
    public String       desc =          "";
    public eRoomLight   light =         eRoomLight.NORMAL;
    public OMUD_MMUD_DataCoins coins =         new OMUD_MMUD_DataCoins();
    public OMUD_MMUD_DataCoins coins_hidden =  new OMUD_MMUD_DataCoins();
    public ArrayList<OMUD_MMUD_DataItem>  arrlItems =         new ArrayList<OMUD_MMUD_DataItem>();
    public ArrayList<OMUD_MMUD_DataItem>  arrlItemsHidden =   new ArrayList<OMUD_MMUD_DataItem>();
    public ArrayList<OMUD_MMUD_DataUnit>  arrlUnits =         new ArrayList<OMUD_MMUD_DataUnit>();
    public ArrayList<OMUD_MMUD_DataExit>  arrlExits =         new ArrayList<OMUD_MMUD_DataExit>();

    public eBlockType getType(){return eBlockType.ROOM;}
    public OMUD_MMUD_DataBlockRoom(){}
    public OMUD_MMUD_DataBlockRoom(OMUD_MMUD_DataBlockRoom dr){
        megaID =        new String(dr.megaID);
        name =          new String(dr.name);
        desc =          new String(dr.desc);
        light =         dr.light;
        coins =         new OMUD_MMUD_DataCoins(dr.coins);
        coins_hidden =  new OMUD_MMUD_DataCoins(dr.coins_hidden);
        for (int i = 0; i < dr.arrlItems.size(); ++i)
            arrlItems.add(new OMUD_MMUD_DataItem(dr.arrlItems.get(i)));
        for (int i = 0; i < dr.arrlItemsHidden.size(); ++i)
            arrlItemsHidden.add(new OMUD_MMUD_DataItem(dr.arrlItemsHidden.get(i)));
        for (int i = 0; i < dr.arrlUnits.size(); ++i)
            arrlUnits.add(new OMUD_MMUD_DataUnit(dr.arrlUnits.get(i)));
        for (int i = 0; i < dr.arrlExits.size(); ++i)
            arrlExits.add(new OMUD_MMUD_DataExit(dr.arrlExits.get(i)));
    }
}