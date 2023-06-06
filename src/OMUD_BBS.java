public class OMUD_BBS{
	public enum eLocation{
		OFFLINE,
	    BBS,
	    BBS_MUD_MENU,
	    MUD
	}

    private final String[] LOCATION_STRINGS = {
        "Offline",
        "BBS",
        "MUD Menu",
        "MUD"
    };

    private eLocation _eLoc = eLocation.OFFLINE;

    // checkLocation() do some text processing to check where we are based on commands and current locations
    public boolean checkLocation(boolean online, final String strLastCmd){
        boolean changed = false;
        
        // if already online...
        if (online && _eLoc != eLocation.OFFLINE){

            // arrived at the BBS mud menu after sending an enter/line input.
            // (could be from the BBS or exiting from mud)
            if (_eLoc == eLocation.BBS_MUD_MENU && strLastCmd.length() > 0){
                
                // check for enter or exit: anything that starts with e/E or x/X...
                eLocation eLoc_new = _eLoc;
                     if (strLastCmd.charAt(0) == 'e' || strLastCmd.charAt(0) == 'E')
                    eLoc_new = eLocation.MUD;
                else if (strLastCmd.charAt(0) == 'x' || strLastCmd.charAt(0) == 'X')
                    eLoc_new = eLocation.BBS;

                if ((changed = eLoc_new != _eLoc))
                    setLocation(eLoc_new);
            }
        // check for offline/online changes...
        } else {
                 if ((changed =  online && _eLoc == eLocation.OFFLINE))
                setLocation(eLocation.OFFLINE);
            else if ((changed = !online && _eLoc != eLocation.OFFLINE))
                setLocation(eLocation.BBS);
        }
        return changed;                  
    }

    // setLocation(): returns new location string for convenience
    public String setLocation(eLocation eLoc){
        _eLoc = eLoc;
        return getLocationString();
    }

    public eLocation getLocation()      {return _eLoc;}
    public String getLocationString()   {return LOCATION_STRINGS[_eLoc.ordinal()];}
}
