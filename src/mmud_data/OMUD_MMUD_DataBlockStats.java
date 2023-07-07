// OMUD_MMUD_DataBlockStats(): HP/MA in DataStatline, EXP in DataExp
public class OMUD_MMUD_DataBlockStats extends OMUD_MMUD_DataBlock{
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
    public OMUD_MMUD_DataBlockStats(){}
    public OMUD_MMUD_DataBlockStats(OMUD_MMUD_DataBlockStats ds){
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