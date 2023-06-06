// *************************************
// THIS FILE FOR SHARED STATIC CONSTANTS
// *************************************

import java.awt.Color;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;

// ---------------------
// Enums
// ---------------------
enum eANSIColors{
    BLACK,
    RED,
    GREEN,
    YELLOW,
    BLUE,
    MAGENTA,
    CYAN,
    WHITE
}

// ---------------------
// Shared Static Constants
// ---------------------
public class OMUD{
    // --------------
    // Terminal Stuff
    // --------------
    public static final int TERMINAL_ROWS = 25;
    public static final int TERMINAL_COLS = 80;

    // --------------
    // ANSI Colors/Styles
    // --------------
    public static final Color[] ANSI_COLORS_BOLD = {
        new Color( 85,  85,  85), 
        new Color(240,   0,   0), 
        new Color(  0, 240,   0),
        new Color(240, 240,   0),
        new Color(  0,   0, 240),
        new Color(240,   0, 240),
        new Color(  0, 240, 240),
        new Color(240, 240, 240)
    };
    public static final Color[] ANSI_COLORS_NORM = {
        new Color(  0,   0,   0), 
        new Color(170,   0,   0), 
        new Color(  0, 170,   0),
        new Color(170,  85,   0),
        new Color(  0,   0, 170),
        new Color(170,   0, 170),
        new Color(  0, 170, 170),
        new Color(170, 170, 170)
    };
    public static final Color[] ANSI_COLORS_DIM = {
        new Color( 42,  42,  42), // dim untested on both fg/bg for accuracy - leaving for visibility when found
        new Color( 85,   0,   0), 
        new Color(  0,  85,   0),
        new Color( 85,  85,   0),
        new Color(  0,   0,  85),
        new Color( 85,   0,  85),
        new Color(  0,  85,  85),
        new Color( 85,  85,  85)
    };
    public static final eANSIColors ANSI_DEFAULT_FG = eANSIColors.WHITE;
    public static final eANSIColors ANSI_DEFAULT_BG = eANSIColors.BLACK;
    public static final Color TERMINAL_LOCAL_INFO_FG = new Color(232, 232, 232);
    public static final Color TERMINAL_LOCAL_INFO_BG = new Color(0, 0, 164);
    public static final Color GUI_BG = new Color(42, 42, 42);
    
    // --------------
    // ASCII Codes (Decimal)
    // --------------
    public static final char ASCII_BEL  = 7;
    public static final char ASCII_BS   = 8;
    public static final char ASCII_HTB  = 9;
    public static final char ASCII_LF   = 10;
    public static final char ASCII_VTB  = 11;
    public static final char ASCII_FF   = 12; // form-feed/new page
    public static final char ASCII_CR   = 13;
    public static final char ASCII_ESC  = 27;
    public static final char ASCII_SPC  = 32;
    public static final char ASCII_EXC  = 33;
    public static final char ASCII_ZRO  = 48;
    public static final char ASCII_AT   = 64;
    public static final char ASCII_LBR  = 91;
    public static final char ASCII_DEL  = 127;

    // --------------
    // ANSI CSI Escape Sequqnces
    // --------------
    // char versions
    public static final char CSI_GRAPHICS =             'm';
    public static final char CSI_CRSR_SAVE =            's';
    public static final char CSI_CRSR_REST =            'u';
    public static final char CSI_CRSR_UP =              'A';
    public static final char CSI_CRSR_DOWN =            'B';
    public static final char CSI_CRSR_RIGHT =           'C';
    public static final char CSI_CRSR_LEFT =            'D';
    public static final char CSI_CRSR_RWCL1 =           'f';
    public static final char CSI_CRSR_RWCL2 =           'H';
    // string versions for indexOf and other funcs
    public static final String CSI_GRAPHICS_STR =       "m";
    public static final String CSI_CRSR_UP_STR =        "A";
    public static final String CSI_CRSR_DOWN_STR =      "B";
    public static final String CSI_CRSR_RIGHT_STR =     "C";
    public static final String CSI_CRSR_LEFT_STR =      "D";

    public static final String CSI_CLR_FULL =           "2J";
    public static final String CSI_CLR_LINE_FULL =      "2K";
    public static final String CSI_CLR_LINE_CRSR1 =     "K";
    public static final String CSI_CLR_LINE_CRSR2 =     "0K";

    public static final String ANSI_SEND_ARROW_UP =     "\u001B[A";
    public static final String ANSI_SEND_ARROW_DOWN =   "\u001B[B";
    public static final String ANSI_SEND_ARROW_RIGHT =  "\u001B[C";
    public static final String ANSI_SEND_ARROW_LEFT =   "\u001B[D";
    public static final String ANSI_SEND_BACKSPACE =    "\u0008";
    public static final String ANSI_SEND_CONTROL =      "\u001E";
    public static final String ANSI_SEND_ESCAPE =       "\u001B";
    public static final String ANSI_SEND_DELETE =       "\u007F";
    public static final String ANSI_SEND_TAB =          "\u0009";

    // --------------
    // RIPscrip Sequences
    // --------------
    public static final String RIP_QVERSION1 =          "\u001b[!";     // query version style 1
    public static final String RIP_QVERSION2 =          "\u001b[0!";    // query version style 2
    public static final String RIP_OFF =                "\u001b[1!";
    public static final String RIP_ON =                 "\u001b[2!";

    // --------------
    // Get Prev/Next LineFeed Convenience Funs
    // --------------
    public static final int getPrevLF(StringBuilder sb, int pos_offset){return sb.lastIndexOf("\n", pos_offset);}
    public static final int getNextLF(StringBuilder sb, int pos_offset){return sb.indexOf("\n", pos_offset);}

    // --------------
    // Console/On-screen Logging
    // --------------
    public static void logError(String msg){
        //SwingUtilities.invokeLater(new Runnable(){public void run(){
        //    _term.appendLocalANSI("[ERR: " + msg + "]\n");}});
        System.err.println("[ERR: " + msg + "]");
    }
    public static void logInfo(String msg){
        //SwingUtilities.invokeLater(new Runnable(){public void run(){
        //     _term.appendLocalANSI("[INF: " + msg + "]\n");}});
        System.out.println("[INF: " + msg + "]");
    }
    public static void logDebug(String msg){
        //SwingUtilities.invokeLater(new Runnable(){public void run(){
        //     _term.appendLocalANSI("[DBG: " + msg + "]");}});
        System.out.println("[DBG: " + msg + "]");
    }

    // --------------
    // File Logging
    // --------------
    public static final String LOG_FILENAME_TELNET =   "dump_telnet.txt";
    public static final String LOG_FILENAME_TERMINAL = "dump_terminal.txt";
    // file loggers: assume calls are coming from separate threads
    public static void logToFile(String filename, boolean clear_only, char[] data, int data_len){
        try{
            PrintWriter out = new PrintWriter(new FileOutputStream(new File(filename), clear_only ? false : true));
            if (!clear_only && data != null && data_len > 0)
                out.write(data, 0, data_len);
            out.close();
        } catch (Exception e){
            OMUD.logError("Error logging char[] data to file '" + filename + "': " + e.getMessage());
        }
    }
    public static void logToFile(String filename, String data){
        try{
            PrintWriter out = new PrintWriter(new FileOutputStream(new File(filename), false));
            if (data != null && data.length() > 0)
                out.print(data);
            out.close();
        } catch (Exception e){
            OMUD.logError("Error logging String data to file '" + filename + "': " + e.getMessage());
        }
    }

    // --------------
    // StringBuilder Compares (SB.equals() doesn't exist)
    // --------------
    public static boolean compareSBString(StringBuilder sb, String str_find){
        boolean equals = sb.length() == str_find.length();
        for (int i = 0; i < sb.length() && equals; ++i)
            equals = sb.charAt(i) == str_find.charAt(i);
        return equals;
    }
    public static boolean compareSBSB(StringBuilder sb, StringBuilder sb_find){
        boolean equals = sb.length() == sb_find.length();
        for (int i = 0; i < sb.length() && equals; ++i)
            equals = sb.charAt(i) == sb_find.charAt(i);
        return equals;
    }

    // --------------
    // Misc Functions
    // --------------
    // getFillString(): convenience func for filling spaces...
    public static String getFillString(String strFill, int fill_size){return new String(new char[fill_size]).replace("\0", strFill);}
}
