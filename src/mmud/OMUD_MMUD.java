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
        NONE,       // not a real slot
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
    public static class DataCoins{
        public int runic =   0;
        public int plat =    0;
        public int gold =    0;
        public int silver =  0;
        public int copper =  0;
        public DataCoins(){}
        public DataCoins(DataCoins dc){
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

                gold    += plat   * 100;
                plat     = 0;

                silver  += gold   * 100;
                gold     = 0;

                copper  += silver * 100;
                silver   = 0;
            }
        }
    }

    public static class DataItem{
        public int          id  =           -1;
        public int          qty =            1;
        public String       name =          "";
        public eEquipSlot   equip_slot =    eEquipSlot.NONE;

        DataItem(String n)  {name = n;}
        DataItem(DataItem item){
            id =            item.id;
            qty =           item.qty;
            name =          new String(item.name);
            equip_slot =    item.equip_slot;
        }
    }

    public static class DataSpell{
        public int      level =         0;
        public int      cost =          0;
        public String   name_short =    "";
        public String   name_long =     "";

        DataSpell(){}
        DataSpell(DataSpell spell){
            level =         spell.level;
            cost  =         spell.cost;
            name_short =    new String(spell.name_short);
            name_long =     new String(spell.name_long);
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
        public String       megaID =        ""; // MegaMUD RoomID
        public String       name =          "";
        public String       desc =          "";
        public DataCoins    coins =         new DataCoins();
        public DataCoins    coins_hidden =  new DataCoins();
        public eRoomLight   light =         eRoomLight.NORMAL;
        public ArrayList<DataItem>  arrlItems =         new ArrayList<DataItem>();
        public ArrayList<DataItem>  arrlItemsHidden =   new ArrayList<DataItem>();
        public ArrayList<String>    arrlUnits =         new ArrayList<String>();
        public ArrayList<RoomExit>  arrlExits =         new ArrayList<RoomExit>();

        public eBlockType getType(){return eBlockType.ROOM;}
        public DataRoom(){}
        public DataRoom(DataRoom dr){
            megaID =        new String(dr.megaID);
            name =          new String(dr.name);
            desc =          new String(dr.desc);
            coins =         new DataCoins(dr.coins);
            coins_hidden =  new DataCoins(dr.coins_hidden);
            light =         dr.light;
            for (int i = 0; i < dr.arrlItems.size(); ++i)
                arrlItems.add(new DataItem(dr.arrlItems.get(i)));
            for (int i = 0; i < dr.arrlItemsHidden.size(); ++i)
                arrlItemsHidden.add(new DataItem(dr.arrlItemsHidden.get(i)));
            OMUD.copyStringArrayList(dr.arrlUnits, arrlUnits);
            for (int i = 0; i < dr.arrlExits.size(); ++i)
                arrlExits.add(new RoomExit(dr.arrlExits.get(i)));
        }
    }

    public static class DataInv extends DataBlock{
        public int          wealth =        -1; // in copper
        public DataCoins    coins =         new DataCoins();
        public int          enc_cur =       -1;
        public int          enc_max =       -1;
        public String       enc_level =     "";
        public ArrayList<DataItem> arrlItems = new ArrayList<DataItem>();
        public ArrayList<DataItem> arrlWorn  = new ArrayList<DataItem>();
        public ArrayList<DataItem> arrlKeys =  new ArrayList<DataItem>();

        public eBlockType getType(){return eBlockType.INV;}
        public DataInv(){}
        public DataInv(DataInv di){
            wealth =        di.wealth;
            coins =         new DataCoins(di.coins);
            enc_cur =       di.enc_cur;
            enc_max =       di.enc_max;
            enc_level =     new String(di.enc_level);
            for (int i = 0; i < di.arrlItems.size(); ++i)
                arrlItems.add(new DataItem(di.arrlItems.get(i)));
            for (int i = 0; i < di.arrlWorn.size(); ++i)
                arrlWorn.add(new DataItem(di.arrlWorn.get(i)));
            for (int i = 0; i < di.arrlKeys.size(); ++i)
                arrlKeys.add(new DataItem(di.arrlKeys.get(i)));
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
            public String       name =      "";
            public int          qty =       0;
            public DataCoins    coins =     new DataCoins();
            public boolean      can_use =   true;

            public ShopItem(){}
            public ShopItem(ShopItem si){
                name =      new String(si.name);
                qty =       si.qty;
                coins =     new DataCoins(si.coins);
                can_use =   si.can_use;
            }

            public String getPriceString(){
                StringBuilder sbPrice = new StringBuilder();
                if (coins.runic     > 0)
                    sbPrice.append(coins.runic + " " + COIN_TYPE_STRINGS[eCoinType.RUNIC.ordinal()]);
                if (coins.plat      > 0)
                    sbPrice.append((sbPrice.length() > 0 ? ", " : "") + coins.plat    + " " + COIN_TYPE_STRINGS[eCoinType.PLATINUM.ordinal()]);
                if (coins.gold      > 0)
                    sbPrice.append((sbPrice.length() > 0 ? ", " : "") + coins.gold    + " " + COIN_TYPE_STRINGS[eCoinType.GOLD.ordinal()]);
                if (coins.silver    > 0)
                    sbPrice.append((sbPrice.length() > 0 ? ", " : "") + coins.silver  + " " + COIN_TYPE_STRINGS[eCoinType.SILVER.ordinal()]);
                if (coins.copper    > 0)
                    sbPrice.append((sbPrice.length() > 0 ? ", " : "") + coins.copper  + " " + COIN_TYPE_STRINGS[eCoinType.COPPER.ordinal()]);
                if (sbPrice.length() == 0)
                    sbPrice.append("FREE");
                return sbPrice.toString();
            }
        }
        public String megaID =      "";
        public String roomName =    "";
        public ArrayList<ShopItem> shop_items = new ArrayList<ShopItem>();

        public eBlockType getType(){return eBlockType.SHOP;}
        public DataShop(){}
        public DataShop(DataShop ds){
            megaID =    new String(ds.megaID);
            roomName =  new String(ds.roomName);
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