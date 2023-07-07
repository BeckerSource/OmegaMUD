import java.util.ArrayList;

public class OMUD_MMUD_DataBlockWho extends OMUD_MMUD_DataBlock{
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
  
    public static class WhoChar{
        public eAlignment   alignment =     eAlignment.NEUTRAL;
        public String       name_first =    "";
        public String       name_last =     "";
        public String       title =         "";
        public String       guild =         "";

        WhoChar(){}
        WhoChar(WhoChar dwc){
            alignment =     dwc.alignment;
            name_first =    new String(dwc.name_first);
            name_last =     new String(dwc.name_last);
            title =         new String(dwc.title);
            guild =         new String(dwc.guild);
        }
    }
    public ArrayList<WhoChar> chars = new ArrayList<WhoChar>();

    public eBlockType getType(){return eBlockType.WHO;}
    OMUD_MMUD_DataBlockWho(){}
    OMUD_MMUD_DataBlockWho(OMUD_MMUD_DataBlockWho dw){
         for (int i = 0; i < dw.chars.size(); ++i)
            chars.add(new WhoChar(dw.chars.get(i)));
    }
}
