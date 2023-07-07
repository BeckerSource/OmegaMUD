public class OMUD_MMUD_DataItem{
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

    public int          id  =           -1;
    public int          qty =            1;
    public String       name =          "";
    public eEquipSlot   equip_slot =    eEquipSlot.NONE;

    OMUD_MMUD_DataItem(String n)  {name = n;}
    OMUD_MMUD_DataItem(OMUD_MMUD_DataItem item){
        id =            item.id;
        qty =           item.qty;
        name =          new String(item.name);
        equip_slot =    item.equip_slot;
    }
}