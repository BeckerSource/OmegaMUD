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
		public RoomExit(eExitDir dir, eDoorType door){eDir = dir; eDoor = door;}
		eExitDir 	eDir = eExitDir.NONE;
		eDoorType  eDoor = eDoorType.NONE;
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
	}	

	public static class DataStatline{
		public String last_cmd = 	"";
		public String text = 		"";
		public int hp = 			0;
		public int ma = 			0;
		public int hp_max = 		0;
		public int ma_max = 		0;
		public eRestState rest = 	eRestState.ACTIVE;
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
		public String keys = 		"";
		public String carry_items = "";
		public int carry_runic = 	0;
		public int carry_plat = 	0; 
		public int carry_gold = 	0; 
		public int carry_silver = 	0; 
		public int carry_copper = 	0; 
		public int wealth = 		0; // in copper
		public int enc_cur = 		0;
		public int enc_max = 		0;
		public String enc_level = 	"";
	}

	public static class DataParty{
		ArrayList<String> 	name = 			new ArrayList<String>();
		ArrayList<String> 	party_class = 	new ArrayList<String>();
		ArrayList<Integer>  hp_cur = 		new ArrayList<Integer>();
		ArrayList<Integer>  ma_cur = 		new ArrayList<Integer>();
		ArrayList<Integer>  hp_max = 		new ArrayList<Integer>();
		ArrayList<Integer>  ma_max = 		new ArrayList<Integer>();
		ArrayList<String>  	rank = 			new ArrayList<String>();
	}

	public static class DataSpell{
		ArrayList<Integer> 	level = 	 new ArrayList<Integer>();
		ArrayList<Integer> 	mana  = 	 new ArrayList<Integer>();
		ArrayList<String> 	name_short = new ArrayList<String>();
		ArrayList<String> 	name_long =  new ArrayList<String>();
	}
}