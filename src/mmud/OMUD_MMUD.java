import java.util.ArrayList;

public class OMUD_MMUD{

    // ------------------
    // Room Data
    // ------------------
    public static enum eExitDir{
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NE,
        NW,
        SE,
        SW,
        UP,
        DOWN,
        NONE
    }

    public static final String[] EXIT_DIR_STRINGS = {
        "north",
        "south",
        "east",
        "west",
        "northeast",
        "northwest",
        "southeast",
        "southwest",
        "up",
        "down",
        "NONE!!!"
    };

    public static enum eDoorType{
        NONE,
        OPEN,
        CLOSED
    }

    public static final String[] DOOR_TYPE_STRINGS = {
        "",
        "open",
        "closed"
    };

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

    public static class RoomExit{
        public eExitDir   eDir =  eExitDir.NONE;
        public eDoorType  eDoor = eDoorType.NONE;

        public RoomExit(eExitDir dir, eDoorType door){eDir = dir; eDoor = door;}
        public RoomExit(RoomExit re){eDir = re.eDir; eDoor = re.eDoor;}
    }

    // ------------------
    // Char Data
    // ------------------
    public static enum eRestState{
        READY,
        REST,
        MED
    }

    public static final String[] REST_STATE_STRINGS = {
        "[READY]", // not a real game string, just a state for non-rest and non-med
        "[REST]",
        "[MED]"
    };  

    // ------------------
    // Item / Equipment Data
    // ------------------
    public static enum eEquipSlot{
        NONE, 		// not a real slot
        WEAPON,
        OFFHAND,
        HEAD,
        TORSO,
        ARMS,
        WRIST,
        HANDS,
        WAIST,
        LEGS,
        FEET,
        BACK,
        FACE,
        EYES,
        EARS,
        NECK,
        FINGER,
        WORN,
        NOWHERE
    }

    public static final String[] EQUIP_SLOT_STRINGS = {
        "(NONE)",   // not a real slot
        "(weapon)",
        "(off-hand)",
        "(head)",
        "(torso)",
        "(arms)",
        "(wrist)",
        "(hands)",
        "(waist)",
        "(legs)",
        "(feet)",
        "(back)",
        "(face)",
        "(eyes)",
        "(ears)",
        "(neck)",
        "(finger)",
        "(worn)",
        "(nowhere)"
    };  

    // ------------------
    // Coin/Money Types
    // ------------------
    public static enum eCoinType{
        COPPER,
        SILVER,
        GOLD,
        PLATINUM,
        RUNIC               // BBS can have custom runic name
    }

    // base (singular) full names
    public static final String[] COIN_TYPE_STRINGS = {
        "copper farthing",
        "silver noble",
        "gold crown",
        "platinum piece",
        "adamantite piece"  // BBS can have custom runic name "runic piece" (adamantite for testing)
    };

    // ------------------
    // Alignments
    // ------------------
    public static enum eAlignment{
        NEUTRAL,
        LAWFUL,
        SAINT,
        GOOD,
        SEEDY,
        OUTLAW,
        CRIMINAL,
        VILLAIN,
        FIEND
    }

    public static final String[] ALIGNMENT_STRINGS = {
        "Neutral", // shown as empty string in mud who list
        "Lawful",
        "Saint",
        "Good",
        "Seedy",
        "Outlaw",
        "Criminal",
        "Villain",
        "FIEND"
    };

    // ------------------
    // Individual Containers
    // ------------------
    public static class DataItem{
        public int          id  =           -1;
        public String       name =          "";
        public eEquipSlot   equip_slot =    eEquipSlot.NONE;

        DataItem(String n)  {name = n;}
        DataItem(DataItem item){
            id =    		item.id;
            name =  		new String(item.name);
            equip_slot = 	item.equip_slot;
        }
    }

    public static class DataSpell{
        public int      level =         0;
        public int      cost =          0;
        public String   name_short =    "";
        public String   name_long =     "";

        DataSpell(){}
        DataSpell(DataSpell spell){
        	level = 		spell.level;
        	cost  = 		spell.cost;
        	name_short = 	new String(spell.name_short);
        	name_long = 	new String(spell.name_long);
        }
    }

    // ------------------
    // Block Containers
    // ------------------
    public static abstract class DataBlock{
        public enum eBlockType{
            NONE,
            STATLINE,
            ROOM,
            INV,
            STATS,
            EXP,
            SHOP,
            SPELLS,
            WHO,
            PARTY
        }

        public static final String[] CMD_STRINGS = {
            "",
            "",
            "look",
            "i",
            "stat",
            "experience",
            "list",
            "spells",
            "who",
            "party"
        };
        public static final String CMD_SPELLS_KAI = "powers";

        public abstract eBlockType getType();
    }

    public static class DataStatline extends DataBlock{
        public int      hp_cur =    0;
        public int      hp_max =    0;
        public int      ma_cur =    0;
        public int      ma_max =    0;
        public boolean  hp_mod =    false;
        public boolean  ma_mod =    false;
        public String   hp_str =    "";
        public String   ma_str =    "";
        public eRestState rest =    eRestState.READY;
        public static final String MA_STR = "MA";

        public eBlockType getType(){return eBlockType.STATLINE;}
        public DataStatline(){}
        public DataStatline(DataStatline dsl){
            hp_cur = dsl.hp_cur;
            hp_max = dsl.hp_max;
            ma_cur = dsl.ma_cur;
            ma_max = dsl.ma_max;
            hp_mod = dsl.hp_mod;
            ma_mod = dsl.ma_mod;
            hp_str = new String(dsl.hp_str);
            ma_str = new String(dsl.ma_str);
            rest =   dsl.rest;
        }
    }

    public static class DataRoom extends DataBlock{
        public String roomID =          ""; // MegaMUD RoomID
        public String name =            "";
        public String desc =            "";
        public String items =           "";
        public String items_hidden =    "";
        public String units =           "";
        public String exits =           "";
        public eRoomLight light =       eRoomLight.NORMAL; 
        public ArrayList<String>        arrlItems =         new ArrayList<String>();
        public ArrayList<String>        arrlItemsHidden =   new ArrayList<String>();
        public ArrayList<String>        arrlUnits =         new ArrayList<String>();
        public ArrayList<RoomExit>      arrlExits =         new ArrayList<RoomExit>();

        public eBlockType getType(){return eBlockType.ROOM;}
        public DataRoom(){}
        public DataRoom(DataRoom dr){
            roomID =        new String(dr.roomID);
            name =          new String(dr.name);
            desc =          new String(dr.desc);
            items =         new String(dr.items);
            items_hidden =  new String(dr.items_hidden);
            units =         new String(dr.units);
            exits =         new String(dr.exits);
            light =         dr.light;
            OMUD.copyStringArrayList(arrlItems,         dr.arrlItems);
            OMUD.copyStringArrayList(arrlItemsHidden,   dr.arrlItemsHidden);
            OMUD.copyStringArrayList(arrlUnits,         dr.arrlUnits);
            for (int i = 0; i < dr.arrlExits.size(); ++i)
                arrlExits.add(new RoomExit(dr.arrlExits.get(i)));
        }
        
        public void resetOptional(){
            desc =  "";
            items = "";
            items_hidden = "";
            units = "";
            light = eRoomLight.NORMAL;
        }
    }

    public static class DataInv extends DataBlock{
        public int      wealth =        -1; // in copper
        public int      coins_runic =   0;
        public int      coins_plat =    0; 
        public int      coins_gold =    0; 
        public int      coins_silver =  0; 
        public int      coins_copper =  0; 
        public int      enc_cur =       -1;
        public int      enc_max =       -1;
        public String   enc_level =     "";
        public ArrayList<DataItem> items_worn  =    new ArrayList<DataItem>();
        public ArrayList<DataItem> items_extra =    new ArrayList<DataItem>();
        public ArrayList<DataItem> keys =           new ArrayList<DataItem>();

        public eBlockType getType(){return eBlockType.INV;}
        public DataInv(){}
        public DataInv(DataInv di){
            wealth =        di.wealth;
            coins_runic =   di.coins_runic;
            coins_plat =    di.coins_plat;
            coins_gold =    di.coins_gold;
            coins_silver =  di.coins_silver;
            coins_copper =  di.coins_copper;
            enc_cur =       di.enc_cur;
            enc_max =       di.enc_max;
            enc_level =     new String(di.enc_level);
            for (int i = 0; i < di.items_worn.size(); ++i)
                items_worn.add(new DataItem(di.items_worn.get(i)));
            for (int i = 0; i < di.items_extra.size(); ++i)
                items_extra.add(new DataItem(di.items_extra.get(i)));
            for (int i = 0; i < di.keys.size(); ++i)
                keys.add(new DataItem(di.keys.get(i)));
        }
    }

    // DataStats(): HP/MA in DataStatline, EXP in DataExp
    public static class DataStats extends DataBlock{
        public String   name_first =    "";
        public String   name_last =     "";
        public String   stats_race =    ""; // (see note below) -
        public String   stats_class =   ""; // 'stats' prefix because of reserved keyword 'class' (and race for consistency)
        public int      level =         -1;
        public int      lives =         -1;
        public int      cp =            -1;
        public int      str =           -1;
        public int      intel =         -1;
        public int      wil =           -1;
        public int      agi =           -1;
        public int      hea =           -1;
        public int      cha =           -1;
        public int      ac_ac =         -1;
        public int      ac_accy =       -1;
        public int      sc =            -1;
        public int      perc =          -1;
        public int      stealth =       -1;
        public int      thievery =      -1;
        public int      traps =         -1;
        public int      pick =          -1;
        public int      track =         -1;
        public int      ma =            -1;
        public int      mr =            -1;
        public boolean  str_mod =       false;
        public boolean  intel_mod =     false;
        public boolean  wil_mod =       false;
        public boolean  agi_mod =       false;
        public boolean  hea_mod =       false;
        public boolean  cha_mod =       false;

        public eBlockType getType(){return eBlockType.STATS;}
        public DataStats(){}
        public DataStats(DataStats ds){
            name_first =    new String(ds.name_first);
            name_last =     new String(ds.name_last);
            stats_race =    new String(ds.stats_race);
            stats_class =   new String(ds.stats_class);
            level =         ds.level;
            lives =         ds.lives;
            cp =            ds.cp;
            str =           ds.str;
            intel =         ds.intel;
            wil =           ds.wil;
            agi =           ds.agi;
            hea =           ds.hea;
            cha =           ds.cha;
            ac_ac =         ds.ac_ac;
            ac_accy =       ds.ac_accy;
            sc =            ds.sc;
            perc =          ds.perc;
            stealth =       ds.stealth;
            thievery =      ds.thievery;
            traps =         ds.traps;
            pick =          ds.pick;
            track =         ds.track;
            ma =            ds.ma;
            mr =            ds.mr;
            str_mod =       ds.str_mod;
            intel_mod =     ds.intel_mod;
            wil_mod =       ds.wil_mod;
            agi_mod =       ds.agi_mod;
            hea_mod =       ds.hea_mod;
            cha_mod =       ds.cha_mod;
        }
    }

    public static class DataExp extends DataBlock{
        public int cur_total =  -1;
        public int next_total = -1;
        public int next_rem =   -1;
        public int per_hr =     -1;

        public eBlockType getType(){return eBlockType.EXP;}
        public DataExp(){}
        public DataExp(DataExp de){
            cur_total =     de.cur_total;
            next_total =    de.next_total;
            next_rem =      de.next_rem;
            per_hr =        de.per_hr;
        }
    }

    public static class DataShop extends DataBlock{
        public static class ShopItem{
            public String   name =  "";
            public int      qty =   0;
            public String   price = ""; // string for now, change later when conversions in place

            public ShopItem(){}
            public ShopItem(ShopItem si){
                name =  new String(si.name);
                qty =   si.qty;
                price = new String(si.price);
            }
        }
        public ArrayList<ShopItem> shop_items = new ArrayList<ShopItem>();

        public eBlockType getType(){return eBlockType.SHOP;}
        public DataShop(){}
        public DataShop(DataShop ds){
            shop_items.clear();
            for (int i = 0; i < ds.shop_items.size(); ++i)
                shop_items.add(new ShopItem(ds.shop_items.get(i)));
        }
    }

    public static class DataSpells extends DataBlock{
        public ArrayList<DataSpell> spells = new ArrayList<DataSpell>();

        public eBlockType getType(){return eBlockType.SPELLS;}
        public DataSpells(){}
        public DataSpells(DataSpells ds){           
            for (int i = 0; i < ds.spells.size(); ++i)
                spells.add(new DataSpell(ds.spells.get(i)));
        }
    }

    public static class DataWho extends DataBlock{
        public static class DataWhoChar{
            public eAlignment   alignment =     eAlignment.NEUTRAL;
            public String       name_first =    "";
            public String       name_last =     "";
            public String       title =         "";
            public String       guild =         "";

            DataWhoChar(){}
            DataWhoChar(DataWhoChar dwc){
                alignment =     dwc.alignment;
                name_first =    new String(dwc.name_first);
                name_last =     new String(dwc.name_last);
                title =         new String(dwc.title);
                guild =         new String(dwc.guild);
            }
        }
        public ArrayList<DataWhoChar> chars = new ArrayList<DataWhoChar>();

        public eBlockType getType(){return eBlockType.WHO;}
        DataWho(){}
        DataWho(DataWho dw){
             for (int i = 0; i < dw.chars.size(); ++i)
                chars.add(new DataWhoChar(dw.chars.get(i)));           
        }
    }

    public static class DataParty extends DataBlock{
        public static class PartyMember{
            public String   name =          "";
            public String   party_class =   "";
            public int      hp_cur =        0;
            public int      ma_cur =        0;
            public int      hp_max =        0;
            public int      ma_max =        0;
            public String   rank =          "";
        }
        public ArrayList<PartyMember> members = new ArrayList<PartyMember>();

        public eBlockType getType(){return eBlockType.PARTY;}
    }
}