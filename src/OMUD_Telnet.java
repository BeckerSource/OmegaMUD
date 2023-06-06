import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.net.telnet.TelnetClient; 
//import org.apache.commons.net.telnet.SimpleOptionHandler;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.TelnetCommand;

// OMUD_ITelnetEvents(): callbacks for GUI
interface OMUD_ITelnetEvents {
    public void notifyTelnetConnected();
    public void notifyTelnetDisconnected();
    public void notifyTelnetParsed(OMUD_Buffer omb, ArrayList<OMUD_IBufferMod> arrlBMods, final String strLastCmd);
}

public class OMUD_Telnet {
    private OMUD_ITelnetEvents          _omte =         null;
    private OMUD_TelnetParser           _omtp =         null;
    private ReentrantLock               _lockSend =     null;
    private ReentrantLock               _lockParse =    null;
    private RunApache                   _runApache =    null;
    private Thread                      _threadReader = null;
    private TelnetClient                _tnc =          null;
    private TerminalTypeOptionHandler   _tncOptType =   null;
    private EchoOptionHandler           _tncOptEcho  =  null;
    private SuppressGAOptionHandler     _tncOptGA  =    null;
    private Timer                       _tmrAYT =       null;
    private TimerTask                   _taskAYT =      null;
    private StringBuilder               _sbCurrCmd =    null;
    private StringBuilder               _sbLastCmd =    null;
    private long                        _ayt_last_response_time_ms = 0;
    private final String    TERMINAL_TYPE =     "ANSI";
    private final String    TERMINAL_CHARSET =  "ISO-8859-1";
    private final String    TELNET_NEG_DO =     "DO";
    private final String    TELNET_NEG_DONT =   "DONT";
    private final String    TELNET_NEG_WILL =   "WILL";
    private final String    TELNET_NEG_WONT =   "WONT";
    private final int       TELNET_BUF_SIZE =   1024;
    private final long      TELNET_AYT_MS =     2000;

    public OMUD_Telnet(OMUD_ITelnetEvents omte, OMUD_TelnetParser omtp) throws IOException {
        _omte = omte;
        _omtp = omtp;
        _lockSend =     new ReentrantLock(true);
        _lockParse =    new ReentrantLock(true);
        _runApache =    new RunApache();
        _sbCurrCmd =    new StringBuilder();
        _sbLastCmd =    new StringBuilder();

        // create telet client object...
        _tncOptType = new TerminalTypeOptionHandler(TERMINAL_TYPE, false, false, true, false);
        _tncOptEcho = new EchoOptionHandler(true, false, true, false);
        _tncOptGA = new SuppressGAOptionHandler(true, true, true, true);
        _tnc = new TelnetClient();
        try {
            _tnc.addOptionHandler(_tncOptType);
            _tnc.addOptionHandler(_tncOptEcho);
            _tnc.addOptionHandler(_tncOptGA);
        } catch (InvalidTelnetOptionException e) {
            OMUD.logError("Telnet: error registering option handlers: " + e.getMessage());
        }

        // clear debug log file...
        OMUD.logToFile(OMUD.LOG_FILENAME_TELNET, true, null, 0);
    }

    // --------------
    // Connect/Disconnect/AYT
    // --------------
    public boolean isConnected(){return _threadReader != null;}
    public void connect(String strTelnetAdr, String strTelnetPort){
        if (isConnected()){
            OMUD.logInfo("Telnet: error on connect: already connected!");
        } else {
            try{
                OMUD.logInfo("Telnet: attempting connection: " + strTelnetAdr + ":" + strTelnetPort);
                _tnc.registerNotifHandler(_runApache);
                _tnc.connect(strTelnetAdr, Integer.parseInt(strTelnetPort));       
                _threadReader = new Thread(_runApache);
                _threadReader.start();
                // create timer/task for connectivity checks...
                _ayt_last_response_time_ms = 0;
                _taskAYT = new TimerTaskAYT();
                _tmrAYT = new Timer();
                _tmrAYT.schedule(_taskAYT, TELNET_AYT_MS, TELNET_AYT_MS);
            } catch (Exception e) {
                OMUD.logError("Telnet: error on connect: " + e.getMessage());            
            }
        }
    }

    public void disconnect(boolean user_request){
        if (!isConnected()){
            OMUD.logInfo("Telnet: error on disconnect: already disconnected!");
        } else {
            try{
                if (user_request)
                     OMUD.logInfo("Telnet: disconnected: user request");
                else OMUD.logInfo("Telnet: disconnected: timed out");

                _tmrAYT.cancel();
                _tmrAYT.purge();
                _tnc.disconnect();
                _tnc.unregisterNotifHandler();
                _threadReader.interrupt();
                _threadReader = null;
                _omte.notifyTelnetDisconnected();
                _omtp.reset();
                new ThreadParse("\n\n").start(); // add a couple linefeeds for text readability on reconnect
            } catch (Exception e) {
                OMUD.logError("Telnet: error on disconnect: " + e.getMessage());            
            }
        }
    }

    // TimerTaskAYT: a fake AYT that checks for a connection.
    // Real AYT responses are server-custom and send printed text, which is not what we want.
    // Just send a NOP byte and check for an exception/failure on send.
    private class TimerTaskAYT extends TimerTask{
        public void run(){
            long current_time_ms = System.currentTimeMillis();
            if (current_time_ms - _ayt_last_response_time_ms >= TELNET_AYT_MS){                        
                //OMUD.logInfo("Telnet: checking idle connection...");                    
                try {
                    _tnc.sendCommand((byte) TelnetCommand.NOP);
                    _ayt_last_response_time_ms = current_time_ms;
                } catch (Exception e){
                    disconnect(false);
                }
            }
        }
    }

    // --------------
    // Sending Text
    // --------------
    private class ThreadSend extends Thread{
        private String _text = "";
        public ThreadSend(String text){_text = text;}
        public void run(){
            if (isConnected()){
                _lockSend.lock(); // lock for ordered sending

                    // throttle a bit by waiting for the parser to be completely finished -
                    // not guaranteed but seems to help when spamming 
                    // both delayed and instant mud commands (search vs just enter, etc)...
                    try{ 
                        while (_lockParse.hasQueuedThreads()) Thread.sleep(1);
                    } catch (Exception e){OMUD.logError("Telnet: error sleeping for parse lock queued threads: " + e.getMessage());}                    
                    _lockParse.lock(); // prevent parsing while sending

                        // validate sent text:
                        // check the first character for a valid range of printable chars and enter.
                        // only really matters for single mode, but just check every time for simplicity...
                        if ((_text.charAt(0) >= OMUD.ASCII_SPC && _text.charAt(0) < OMUD.ASCII_DEL) || _text.charAt(0) == OMUD.ASCII_LF){
                            _sbCurrCmd.append(_text);
                            if (_sbCurrCmd.charAt(_sbCurrCmd.length() - 1) == OMUD.ASCII_LF){
                                _sbLastCmd.setLength(0);
                                _sbLastCmd.append(_sbCurrCmd);
                                _sbCurrCmd.setLength(0);
                            }
                        // if backspace, delete the last char (assumes single mode)...
                        } else if (_text.charAt(0) == OMUD.ASCII_BS && _sbCurrCmd.length() > 0){
                            _sbCurrCmd.deleteCharAt(_sbCurrCmd.length() - 1);
                        }

                        // send...
                        try {
                            _tnc.getOutputStream().write(_text.getBytes(), 0, _text.length());
                            _tnc.getOutputStream().flush();
                        } catch (Exception e) {
                            OMUD.logError("Telnet: error sending: " + e.getMessage());
                        }                        
                    _lockParse.unlock();
                _lockSend.unlock();
            } else {
                OMUD.logInfo("Telnet: error sending: not connected: " + _text);
            }
        }
    }

    public void sendText(String text){
        if (text.length() > 0) 
            new ThreadSend(new String(text)).start();
    }

    // --------------
    // Receive/Parsing
    // --------------
    private class ThreadParse extends Thread{
        private String _strData = "";
        public ThreadParse(String strData){_strData = strData;}
        public void run(){
            _lockParse.lock();
                _omtp.threadParseData(_strData, _sbLastCmd, _sbLastCmd.toString()); // string copy passed for convenience
            _lockParse.unlock();
        }
    }

    private class RunApache implements Runnable, TelnetNotificationHandler {
        private BufferedReader  _instrBuf = null;
        private char[]          _data =     null;
        private int             _data_len = 0;

        public void run() {
            // use a buffered reader to force the ISO char set to show characters properly on all systems -
            // windows was displaying correctly but linux wasn't showing extended ASCII correctly and 
            // was displaying text very slow.  not sure about mac but just do this anyway.
            try {
                _instrBuf = new BufferedReader(new InputStreamReader(_tnc.getInputStream(), TERMINAL_CHARSET));
            } catch (Exception e) {
                OMUD.logError("Telnet: error creating buffered reader:" + e.getMessage());
            }

            _data_len = 0;
            _data = new char[TELNET_BUF_SIZE];
            do {
                try {
                    _data_len = _instrBuf.read(_data, 0, TELNET_BUF_SIZE); // read() is a blocking call
                } catch (Exception e) {
                    OMUD.logError("Telnet: error reading socket:" + e.getMessage());
                }

                // not sure when length would ever be zero if data was read?
                if (_data_len > 0){
                    _ayt_last_response_time_ms = System.currentTimeMillis();
                    new ThreadParse(new String(_data, 0, _data_len)).start();
                    OMUD.logToFile(OMUD.LOG_FILENAME_TELNET, false, _data, _data_len);
                }
            } while (_data_len > -1);

            try {
                _tnc.disconnect();
            } catch (Exception e) {
                OMUD.logError("Telnet: error closing telnet:" + e.getMessage());
            }
        }

        // receivedNegotiation(): telnet negotiations... 
        public void receivedNegotiation(int negotiation_code, int option_code) {
            if (_ayt_last_response_time_ms == 0){
                OMUD.logInfo("Telnet: connected!");
                _omte.notifyTelnetConnected();
            }
            _ayt_last_response_time_ms = System.currentTimeMillis();            

            String command = null;
            if (negotiation_code == TelnetNotificationHandler.RECEIVED_DO) {
                command = TELNET_NEG_DO;
            } else if (negotiation_code == TelnetNotificationHandler.RECEIVED_DONT) {
                command = TELNET_NEG_DONT;
            } else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WILL) {
                command = TELNET_NEG_WILL;
            } else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WONT) {
                command = TELNET_NEG_WONT;
            }
            OMUD.logInfo("Telnet: cmd (" + command + "), opt (" + option_code + ")");
        }        
    }
}
