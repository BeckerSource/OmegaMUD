public class OMUD_MMUD_DataExit{
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

    public eExitDir   eDir =  eExitDir.NONE;
    public eDoorType  eDoor = eDoorType.NONE;

    public OMUD_MMUD_DataExit(eExitDir dir, eDoorType door) {eDir = dir;     eDoor = door;}
    public OMUD_MMUD_DataExit(OMUD_MMUD_DataExit de)        {eDir = de.eDir; eDoor = de.eDoor;}
}
