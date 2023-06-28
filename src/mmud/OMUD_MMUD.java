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
		NONE,
		COUNT
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
		CLOSED,
		COUNT
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
		NONE,
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
		WEAPON,
		OFFHAND,
		NOWHERE
	}

	public static final String[] EQUIP_SLOT_STRINGS = {
		"(NONE)", // not a real slot
		"(Head)",
		"(Torso)",
		"(Arms)",
		"(Wrist)",
		"(Hands)",
		"(Waist)",
		"(Legs)",
		"(Feet)",
		"(Back)",
		"(Face)",
		"(Eyes)",
		"(Ears)",
		"(Neck)",
		"(Finger)",
		"(Worn)",
		"(Weapon)",
		"(Off-Hand)",
		"(Nowhere)"
	};	

	// ------------------
	// Individual Containers
	// ------------------
	public static class DataItem{
		public int 			id 	= 			-1;
		public String 		name = 			"";
		public eEquipSlot 	equip_slot = 	eEquipSlot.NONE;
	}

	public static class Spell{
		public int 		level = 		0;
		public int 		mana = 			0;
		public String 	name_short = 	"";
		public String 	name_long = 	"";
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
			SPELLS,
			PARTY,
			SHOP
		}
		public abstract eBlockType getType();
	}

	public static class DataStatline extends DataBlock{
		public int 		hp_cur = 	0;
		public int 		hp_max = 	0;
		public int 		ma_cur = 	0;
		public int 		ma_max = 	0;
		public boolean 	hp_mod = 	false;
		public boolean 	ma_mod = 	false;
		public String  	hp_str = 	"";
		public String  	ma_str = 	"";
		public eRestState rest = 	eRestState.READY;

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
			rest = 	 dsl.rest;
		}
	}

	public static class DataRoom extends DataBlock{
		public String roomID = 			""; // MegaMUD RoomID
		public String name = 			"";
		public String desc = 			"";
		public String items = 			"";
		public String items_hidden = 	"";
		public String units = 			"";
		public String exits = 			"";
		public eRoomLight light = 		eRoomLight.NORMAL; 
		public ArrayList<String> 		arrlItems = 		new ArrayList<String>();
		public ArrayList<String> 		arrlItemsHidden = 	new ArrayList<String>();
		public ArrayList<String> 		arrlUnits = 		new ArrayList<String>();
		public ArrayList<RoomExit> 		arrlExits = 		new ArrayList<RoomExit>();

		public eBlockType getType(){return eBlockType.ROOM;}
		public DataRoom(){}
		public DataRoom(DataRoom room){
			roomID = 			new String(room.roomID);
			name = 				new String(room.name);
			desc = 				new String(room.desc);
			items = 			new String(room.items);
			items_hidden = 		new String(room.items_hidden);
			units = 			new String(room.units);
			exits = 			new String(room.exits);
			light = 			room.light;
			OMUD.copyStringArrayList(arrlItems, 		room.arrlItems);
			OMUD.copyStringArrayList(arrlItemsHidden, 	room.arrlItemsHidden);
			OMUD.copyStringArrayList(arrlUnits, 		room.arrlUnits);
	        for (int i = 0; i < room.arrlExits.size(); ++i)
	            arrlExits.add(new RoomExit(room.arrlExits.get(i)));
		}
		
		public void resetOptional(){
			desc = "";
			items = "";
			items_hidden = "";
			units = "";
			light = eRoomLight.NORMAL;
		}
	}

	public static class DataInv extends DataBlock{
		public int wealth = 		-1; // in copper
		public int coins_runic = 	0;
		public int coins_plat = 	0; 
		public int coins_gold = 	0; 
		public int coins_silver = 	0; 
		public int coins_copper = 	0; 
		public int enc_cur = 		-1;
		public int enc_max = 		-1;
		public String enc_level = 	"";
		public String items = 		"";
		public String keys =  		"";

		public eBlockType getType(){return eBlockType.INV;}
		public DataInv(){}
		public DataInv(DataInv inv){
			wealth = 		inv.wealth;
			coins_runic = 	inv.coins_runic;
			coins_plat = 	inv.coins_plat;
			coins_gold = 	inv.coins_gold;
			coins_silver = 	inv.coins_silver;
			coins_copper = 	inv.coins_copper;
			enc_cur = 		inv.enc_cur;
			enc_max =		inv.enc_max;
			enc_level = 	new String(inv.enc_level);
			items = 		new String(inv.items);
			keys = 			new String(inv.keys);
		}
	}

	// DataStats(): HP/MA in DataStatline, EXP in DataExp
	public static class DataStats extends DataBlock{
		public String 	name_first = 	"";
		public String 	name_last = 	"";
		public String 	stats_race = 	""; // (see note below) -
		public String 	stats_class = 	""; // 'stats' prefix because of reserved keyword 'class' (and race for consistency)
		public int 		level = 		-1;
		public int 		lives = 		-1;
		public int 		cp = 			-1;
		public int 		str = 			-1;
		public int 		intel = 		-1;
		public int 		wil = 			-1;
		public int 		agi = 			-1;
		public int 		hea = 			-1;
		public int 		cha = 			-1;
		public int 		ac_ac = 		-1;
		public int 		ac_accy = 		-1;
		public int 		sc = 			-1;
		public int 		perc = 			-1;
		public int 		stealth = 		-1;
		public int 		thievery = 		-1;
		public int 		traps = 		-1;
		public int 		pick = 			-1;
		public int 		track = 		-1;
		public int 		ma = 			-1;
		public int 		mr = 			-1;
		public eBlockType getType(){return eBlockType.STATS;}
		public DataStats(){}
		public DataStats(DataStats stats){
			name_first = 	stats.name_first;
			name_last = 	stats.name_last;
			stats_race = 	stats.stats_race;
			stats_class = 	stats.stats_class;
			level = 		stats.level;
			lives = 		stats.lives;
			cp = 			stats.cp;
			str = 			stats.str;
			intel = 		stats.intel;
			wil = 			stats.wil;
			agi = 			stats.agi;
			hea = 			stats.hea;
			cha = 			stats.cha;
			ac_ac = 		stats.ac_ac;
			ac_accy = 		stats.ac_accy;
			sc = 			stats.sc;
			perc = 			stats.perc;
			stealth = 		stats.stealth;
			thievery = 		stats.thievery;
			traps = 		stats.traps;
			pick = 			stats.pick;
			track = 		stats.track;
			ma = 			stats.ma;
			mr = 			stats.mr;
		}
	}

	public static class DataExp extends DataBlock{
		public int cur_total = 	-1;
		public int next_total = -1;
		public int next_rem = 	-1;
		public int per_hr =	 	-1;
		public eBlockType getType(){return eBlockType.EXP;}
		public DataExp(){}
		public DataExp(DataExp exp){
			cur_total = 	exp.cur_total;
			next_total = 	exp.next_total;
			next_rem = 		exp.next_rem;
			per_hr = 		exp.per_hr;
		}
	}

	public static class DataSpells extends DataBlock{
		public ArrayList<Spell> spells = new ArrayList<Spell>();
		public eBlockType getType(){return eBlockType.SPELLS;}
	}

	public static class DataParty extends DataBlock{
		public static class PartyMember{
			public String name = 		"";
			public String party_class = "";
			public int hp_cur = 		 0;
			public int ma_cur = 		 0;
			public int hp_max = 		 0;
			public int ma_max = 		 0;
			public String rank = 		"";
		}
		public ArrayList<PartyMember> members = new ArrayList<PartyMember>();
		public eBlockType getType(){return eBlockType.PARTY;}
	}

	public static class DataShop extends DataBlock{
		public static class ShopItem{
			public String 	name = 	"";
			public int 		qty = 	0;
			public String 	price = ""; // string for now, change later when conversions in place
			public ShopItem(){}
			public ShopItem(ShopItem item){
				name = 	new String(item.name);
				qty = 	item.qty;
				price = new String(item.price);
			}
		}
		public ArrayList<ShopItem> items = new ArrayList<ShopItem>();
		public eBlockType getType(){return eBlockType.SHOP;}
		public DataShop(){}
		public DataShop(DataShop shop){
			items.clear();
			for (int i = 0; i < shop.items.size(); ++i)
				items.add(new ShopItem(shop.items.get(i)));
		}
	}
}