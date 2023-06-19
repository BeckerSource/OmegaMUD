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
		PITCH_BLACK
	}

	public static final String[] ROOM_LIGHT_STRINGS = {
		"Light: Normal",
		"Light: Dimly Lit",
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
		ACTIVE,
		RESTING,
		MEDITATING
	}

	public static final String[] REST_STATE_STRINGS = {
		"[ACTIVE]", // not a real game string, just internal naming of non-rest and non-med state as 'active'
		"[RESTING]",
		"[MEDITATING]"
	};	

	// ------------------
	// Containers
	// ------------------
	public static class DataRoom{
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
	}

	public static class DataStatline{
		public String text = 		"";
		public int hp = 			0;
		public int ma = 			0;
		public int hp_max = 		0;
		public int ma_max = 		0;
		public eRestState rest = 	eRestState.ACTIVE;

		public DataStatline(){}
		public DataStatline(DataStatline dsl){
			text =   new String(dsl.text);
			hp = 	 dsl.hp;
			ma = 	 dsl.ma;
			hp_max = dsl.hp_max;
			ma_max = dsl.ma_max;
			rest = 	 dsl.rest;
		}
	}

	public static class DataExp{
		public int exp_total = 	0;
		public int exp_remain = 0;
		public int exp_per_hr = 0;
	}

	// DataStats(): note: putting max HP/MA in statline data for now, prob change later
	public static class DataStats{
		public String 	name_first = 	"";
		public String 	name_last = 	"";
		public String 	stats_race = 	""; // (see note below) -
		public String 	stats_class = 	""; // 'stats' prefix because of reserved keyword 'class' (and race for consistency)
		public int 		level = 		1;
		public int 		lives = 		9;
		public int 		cp = 			0;
		public int 		strength = 		0;
		public int 		intel = 		0;
		public int 		willpower = 	0;
		public int 		agility = 		0;
		public int 		health = 		0;
		public int 		charm = 		0;
		public int 		ac_ac = 		0;
		public int 		ac_accy = 		0;
		public int 		sp = 			0;
		public int 		perception = 	0;
		public int 		stealth = 		0;
		public int 		thievery = 		0;
		public int 		traps = 		0;
		public int 		pick = 			0;
		public int 		track = 		0;
		public int 		martial = 		0;
		public int 		mr = 			0;
	}

	public static class DataInv{
		public int wealth = 		0; // in copper
		public int coins_runic = 	0;
		public int coins_plat = 	0; 
		public int coins_gold = 	0; 
		public int coins_silver = 	0; 
		public int coins_copper = 	0; 
		public int enc_cur = 		0;
		public int enc_max = 		0;
		public String enc_level = 	"";
		public String items = 		"";
		public String keys =  		"";

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

	public static class DataParty{
		public ArrayList<String> 	name = 			new ArrayList<String>();
		public ArrayList<String> 	party_class = 	new ArrayList<String>();
		public ArrayList<Integer>   hp_cur = 		new ArrayList<Integer>();
		public ArrayList<Integer>   ma_cur = 		new ArrayList<Integer>();
		public ArrayList<Integer>   hp_max = 		new ArrayList<Integer>();
		public ArrayList<Integer>   ma_max = 		new ArrayList<Integer>();
		public ArrayList<String>  	rank = 			new ArrayList<String>();
	}

	public static class DataSpell{
		public ArrayList<Integer> 	level = 	 	new ArrayList<Integer>();
		public ArrayList<Integer> 	mana  = 	 	new ArrayList<Integer>();
		public ArrayList<String> 	name_short = 	new ArrayList<String>();
		public ArrayList<String> 	name_long =  	new ArrayList<String>();
	}
}