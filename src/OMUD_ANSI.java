import java.awt.Color;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;

public class OMUD_ANSI{
    private StringBuilder       _text =         new StringBuilder();
    private OMUD.eANSIColors    _fg =           OMUD.ANSI_DEFAULT_FG;
    private OMUD.eANSIColors    _bg =           OMUD.ANSI_DEFAULT_BG;
    private boolean             _fg_set =       false;
    private boolean             _bg_set =       false;
    private boolean             _bold =         false;
    private boolean             _dim =          false;
    private boolean             _italic =       false;
    private boolean             _underline =    false;
    private boolean             _blink =        false;
    private boolean             _invert =       false;
    private boolean             _invis =        false;
    private boolean             _strike =       false;

    public OMUD_ANSI(){}
    public OMUD_ANSI(String text)   {_text.append(text);}
    public OMUD_ANSI(OMUD_ANSI ansi){
        _text.append(ansi.getText());
        _fg =           ansi.getFG();
        _bg =           ansi.getBG();
        _bold =         ansi.isBold();
        _dim =          ansi.isDim();
        _italic =       ansi.isItalic();
        _underline =    ansi.isUnderline();
        _blink =        ansi.isBlink();
        _invert =       ansi.isInvert();
        _invis =        ansi.isInvis();
        _strike =       ansi.isStrike();
    }

    // --------------
    // Setters/Getters
    // --------------
    public StringBuilder getText()      {return _text;}
    public OMUD.eANSIColors getFG()     {return _fg;}
    public OMUD.eANSIColors getBG()     {return _bg;}
    public boolean isFGSet()            {return _fg_set;}
    public boolean isBGSet()            {return _bg_set;}
    public boolean isBold()             {return _bold;}
    public boolean isDim()              {return _dim;}
    public boolean isItalic()           {return _italic;}
    public boolean isUnderline()        {return _underline;}
    public boolean isBlink()            {return _blink;}
    public boolean isInvert()           {return _invert;}
    public boolean isInvis()            {return _invis;}
    public boolean isStrike()           {return _strike;}
    public void setFG(OMUD.eANSIColors eac)  {_fg = eac; _fg_set = true;}
    public void setBG(OMUD.eANSIColors eac)  {_bg = eac; _bg_set = true;}
    public void setBold(boolean v)      {_bold = v;}
    public void setDim(boolean v)       {_dim = v;}
    public void setItalic(boolean v)    {_italic = v;}
    public void setUnderline(boolean v) {_underline = v;}
    public void setBlink(boolean v)     {_blink = v;}
    public void setInvert(boolean v)    {_invert = v;}
    public void setInvis(boolean v)     {_invis = v;}
    public void setStrike(boolean v)    {_strike = v;}
    public void resetFG()               {_fg = OMUD.ANSI_DEFAULT_FG; _fg_set = false;}
    public void resetBG()               {_bg = OMUD.ANSI_DEFAULT_BG; _bg_set = false;}
    public void resetStyles(){
        _bold =      false;
        _dim =       false;
        _italic =    false;
        _underline = false;
        _blink =     false;
        _invert =    false;
        _invis =     false;
        _strike =    false;
    }

    // --------------
    // Static Util Functions
    // --------------
    public static void setAttrFromANSI(OMUD_ANSI ansi, SimpleAttributeSet attr, boolean set_fg){
        Color color = Color.WHITE;
        OMUD.eANSIColors eac = set_fg ? ansi.getFG() : ansi.getBG();

        if (set_fg){
            if (ansi.isBold())
                 color = OMUD.ANSI_COLORS_BOLD[eac.ordinal()];
            else if (ansi.isDim())
                 color = OMUD.ANSI_COLORS_DIM[eac.ordinal()];
            else color = OMUD.ANSI_COLORS_NORM[eac.ordinal()];
        } else {
            // NOTE: background color set calc requires checking 
            // if the server has actually set the BG color. If not, use full black.
            // Also, "dim BG" is untested/unverified for accuracy.
            if (ansi.isBold() && ansi.isBGSet())
                 color = OMUD.ANSI_COLORS_BOLD[eac.ordinal()];
            else if (ansi.isDim() && ansi.isBGSet())
                 color = OMUD.ANSI_COLORS_DIM[eac.ordinal()];
            else color = OMUD.ANSI_COLORS_NORM[eac.ordinal()];
        }

        if ((set_fg && !ansi.isInvert()) || (!set_fg && ansi.isInvert()))
             StyleConstants.setForeground(attr, color);
        else StyleConstants.setBackground(attr, color);
    }

    public static void setANSIFromAttr(OMUD_ANSI ansi, SimpleAttributeSet attr){
        Color fg = StyleConstants.getForeground(attr);
        Color bg = StyleConstants.getBackground(attr);
        OMUD.eANSIColors new_eac_fg = null;
        OMUD.eANSIColors new_eac_bg = null;
        boolean loop = true;

        for (int i = 0; i < 3 && loop; ++i){
        for (OMUD.eANSIColors eac : OMUD.eANSIColors.values()){
            Color[] ptrArr = OMUD.ANSI_COLORS_NORM;
            if (i == 1)
                ptrArr = OMUD.ANSI_COLORS_BOLD;
            else if (i == 2)
                ptrArr = OMUD.ANSI_COLORS_DIM;

            if (new_eac_fg == null && fg.equals(ptrArr[eac.ordinal()])); new_eac_fg = eac;
            if (new_eac_bg == null && bg.equals(ptrArr[eac.ordinal()])); new_eac_bg = eac;

            if (!(loop = new_eac_fg == null || new_eac_bg == null))
                break;
        }}
        ansi.setFG(new_eac_fg); // NOTE: might have to revisit later -
        ansi.setBG(new_eac_bg); // makes assumption that color was set by server.
    }

    public static Color getDefaultFGColor(){return OMUD.ANSI_COLORS_NORM[OMUD.ANSI_DEFAULT_FG.ordinal()];}
    public static Color getDefaultBGColor(){return OMUD.ANSI_COLORS_NORM[OMUD.ANSI_DEFAULT_BG.ordinal()];}
}
