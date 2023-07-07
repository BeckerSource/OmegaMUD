public class OMUD_MMUD_DataUnit{
    public int      mmex_id =   0;
    public int      dmg_taken = 0;
    public String   name =      "";
    public String   desc =      "";

    public OMUD_MMUD_DataUnit(){}
    public OMUD_MMUD_DataUnit(String n)   {name = n;}
    public OMUD_MMUD_DataUnit(OMUD_MMUD_DataUnit du){
        mmex_id =   du.mmex_id;
        dmg_taken = du.dmg_taken;
        name =      new String(du.name);
        desc =      new String(du.desc);
    }
}
