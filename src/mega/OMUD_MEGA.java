// ========================
// MegaMUD File
// ========================

import java.util.ArrayList;

public class OMUD_MEGA{

    /*
    ----------------
    RoomID Hex Layout
    ----------------
    [########]
    1-3: Room Name Hash
      4: U/D
      5: SE/SW
      6: NE/NW
      7: E/W
      8: N/S

    > Room name hash calc (first three digits) just uses the last three digits of the calced hex value.
    > Exit values in each sequence position are summed hex values (see below).
    */

    /*
    ----------------
    Exit Hex Values
    (DR is for exits with doors, gates, etc)
    ----------------
     U = 1
     D = 4
    SE = 1
    SW = 4
    NE = 1
    NW = 4
     E = 1
     W = 4
     N = 1
     S = 4

     U_DR = 2
     D_DR = 8
    SE_DR = 2
    SW_DR = 8
    NE_DR = 2
    NW_DR = 8
     E_DR = 2
     W_DR = 8
     N_DR = 2
     S_DR = 8
    */

    // -----------------
    // MegaMUD RoomID: Exits Code #####
    // -----------------
    public static String getRoomExitsCode(ArrayList<OMUD_MMUD.RoomExit> arrExits){
        int u_d =   0;
        int se_sw = 0;
        int ne_nw = 0;
        int e_w =   0;
        int n_s =   0;
        int door_mod = 1;
        OMUD_MMUD.eExitDir edir = OMUD_MMUD.eExitDir.NONE;
        for (int i = 0; i < arrExits.size(); ++i){
            edir =      arrExits.get(i).eDir;
            door_mod =  arrExits.get(i).eDoor == OMUD_MMUD.eDoorType.NONE ? 1 : 2;
            // changed ordering from above for processing efficiency (more common exits on top)...
                 if (edir == OMUD_MMUD.eExitDir.NORTH)  n_s     += (1 * door_mod);
            else if (edir == OMUD_MMUD.eExitDir.SOUTH)  n_s     += (4 * door_mod);
            else if (edir == OMUD_MMUD.eExitDir.EAST)   e_w     += (1 * door_mod);
            else if (edir == OMUD_MMUD.eExitDir.WEST)   e_w     += (4 * door_mod);
            else if (edir == OMUD_MMUD.eExitDir.SE) se_sw   += (1 * door_mod);
            else if (edir == OMUD_MMUD.eExitDir.SW) se_sw   += (4 * door_mod);
            else if (edir == OMUD_MMUD.eExitDir.NE) ne_nw   += (1 * door_mod);
            else if (edir == OMUD_MMUD.eExitDir.NW) ne_nw   += (4 * door_mod);
            else if (edir == OMUD_MMUD.eExitDir.UP)     u_d     += (1 * door_mod);
            else if (edir == OMUD_MMUD.eExitDir.DOWN)   u_d     += (4 * door_mod);
        }

        // return as ##### - see notes at bottom of file...
        String strExitsCode = new String(
            Integer.toHexString(u_d) +
            Integer.toHexString(se_sw) +
            Integer.toHexString(ne_nw) +
            Integer.toHexString(e_w) +
            Integer.toHexString(n_s)
        ).toUpperCase();
        return strExitsCode;
    }

    // -----------------
    // MegaMUD RoomID: Room Name Hash: ### (in hex)
    // -----------------
    // [1] loop/get the decimal value of each char in the room name
    // [2] multiple the char in the loop by the iterator count (1-based)
    // [3] sum/concat those values
    // [4] get the hex value of the result
    // [5] only the last three "chars" of the hex string are used (prepend with zeroes if less than three)
    private static final int MMUD_ROOM_NAME_HASH_LEN = 3;
    public static String getRoomNameHash(String strRoomName){
        int char_sum = 0;
        for (int i = 0; i < strRoomName.length(); ++i)
            char_sum += (strRoomName.charAt(i) * (i + 1));

        // we only want the last 3 digits of the hex value or prepend with zeros if less than 3...
        StringBuilder sbHash = new StringBuilder(Integer.toHexString(char_sum).toUpperCase());
        if (sbHash.length() > MMUD_ROOM_NAME_HASH_LEN)
            sbHash.delete(0, sbHash.length() - MMUD_ROOM_NAME_HASH_LEN); // end is exclusive
        else if (sbHash.length() < MMUD_ROOM_NAME_HASH_LEN)
            sbHash.insert(0, OMUD.getFillString("0", MMUD_ROOM_NAME_HASH_LEN - sbHash.length()));
        return sbHash.toString();
    }



/*
----------------
MP File Format
----------------
[Loop Path Name][Author Name]       <-- User: Loop Path Name (Loops Only), Author Name
[NEWH:Newhaven:Newhaven Town]       <-- MEGA Start Room: Generated Prefix, MEGA "Goto Tree" Group, MEGA "Goto Tree" Node Name
[NSPL:Newhaven:Spell Shop (Rayth)]  <-- MEGA End Room: Same as above. Line only exists for non-loop paths.
38B40051:38B40051:6:-1:0:::         <-- MEGA: Start RoomID, End RoomID, Step/Move Count, <UNKNOWN>, Req. Gold, Fail Path, Finish Path
38B40051:0000:w                     <-- MEGA: Step RoomID, step bitflags (below), step command
17900010:0000:e
38B40051:0000:n
5AE00004:0000:s
38B40051:0000:e
42D00055:0000:w

----------------
MP File Format: Step Bit Flags
----------------
Dark Room:          0001
Pitch Black:        0008 (0009) (forces dark room + pitch black, becomes 0009)
Rest Up Here:       0002
Don't Rest Here:    0004
Don't Attack Here:  0040
Disarm Trap:        0200
Pick Lock:          0400
Stash Point:        0010
Re-learn Room:      0080
*/

/*
----------------
Remote Commands: Help File
----------------
@attack-last [on | off]     Toggles or sets the player's attack-last setting.
@auto-all [on | off]        Toggles or sets the player's all-off mode.
@auto-bless [on | off]      Toggles or sets the player's auto-bless setting.
@auto-cash [on | off]       Toggles or sets the player's auto-cash setting.
@auto-combat [on | off]     Toggles or sets the player's auto-combat setting.
@auto-get [on | off]        Toggles or sets the player's auto-get setting.
@auto-heal [on | off]       Toggles or sets the player's auto-heal setting.
@auto-hide [on | off]       Toggles or sets the player's auto-hide setting.
@auto-light [on | off]      Toggles or sets the player's auto-light setting.
@auto-nuke [on | off]       Toggles or sets the player's auto-nuke (room attacks) setting.
@auto-search [on | off]     Toggles or sets the player's auto-search setting.
@auto-sneak [on | off]      Toggles or sets the player's auto-sneak setting.
@blind      Spoken by a party member as a general request for a blindness cure. MegaMud will send this if you become blinded while in a party. Other party members who are running MegaMud will attempt to cure the player if they are capable.
@comeback       If you are following in a party, and you get seperated from your leader because you can't move, MegaMud will send this command to the leader. The leader will respond to this by back-tracking up to 5 rooms to try and find you (after that, he/she will continue as normal). If the leader sees you and re-invites you, the leader will continue on as normal.
@deposit-all        Makes the receiver deposit any excess cash they are carrying.
@diseased       Spoken by a party member as a general request for a disease cure. MegaMud will send this if you become diseased while in a party. Other party members who are running MegaMud will attempt to cure the player if they are capable.
@divert [on | off]      Signals that local conversations (eg. telepathes, pages, etc.) should be re-diverted to the sender. This allows the same player to simultaneously run two or more characters and not miss anyone trying to contact them. The person being diverted may also cancel this at any time by simply saying "@divert" themselves.
@do <cmd>       Executes the <cmd> as if it where typed by the player. The results of the command however are not returned.
@drop-all       Makes the receiver drop all items in their inventory.
@enc        Returns the current player encumberance.
@equip-all      Makes the receiver equip any items not already equiped.
@exp        Returns the amount of experience made since starting MegaMud, plus the current experience rate.
@forget     Causes MegaMud to remove the player who sent the command from your current party.
@get-all        Makes the receiver pick up all items within the room.
@goto <room>        Sets the receiver's destination to be the specified room name. Note that the room name must be defined in the room list on the receiver's copy of MegaMud to work correctly.  You may also substitute the 4-character room code for <room> as well.
@hangup     Causes the player to hangup immediately. Be careful not to do this if hangup penalties are in place and the player is in the middle of combat. You can check via @status. A safer method may also be @do x. Note also that there are other commands which MegaMud will respond to (any may even generate automatically), but are primarily designed for use with partying players who are both running MegaMud. Note also that MegaMud will also recognise MudWalk party commands such as "!wait" and "!ok" (if spoken by a follower).
@have <item>        Checks to see if the player has the named item. Note that you must send the full name of the item with correct spelling for this to work. If the player does have the item, the count of the items held is also returned.
@heal       Spoken by a party member as a general request for aid. MegaMud will send this if your health falls below the 'Run' mark and you are within a party. Other party members who are running MegaMud will attempt to heal the player if they are capable.
@health     Returns the current health and mana/kai of the player. Any special player states (eg. Resting, Blinded, etc.) will also be returned.
@held       Spoken by a party member when they become held. MegaMud will send this if you become held while in a party. Other party members who are running MegaMud will attempt to free the player if they are capable.
@home <monster>     This command can be sent to mudop players who can check on the status of a monster.
@invite     Causes the MegaMud to invite the player to join your party. This allows you define friendly players who are allowed to join your party at any time.
@join       Causes MegaMud to join the player who sent the command.
@kill <player>      Used only by party members who have the 'Defend Party' option turned on. It is used to signal to other party members that a member is being attacked by another player, and that they should all join in to help defend the party.
@level      Returns the player's current level.
@lives      Returns the character's number of lives remaining.
@loop <path-file>       Sets the receiver's loop destination to be the specified path file. Note that the file name must be defined in the path list on the receiver's copy of MegaMud to work correctly.
@looponce <path-file>       Tells the receiver to loop once around the specified destination. Once complete, the player will return to their original loop.
@ok     Telepathed by MegaMud to the party leader when it has finished resting. This is sent after a @wait command to inform the party leader it may recommence movement. MegaMud will keep track of multiple party members and will not restart movement until all of the followers have rested.
@panic!     Sent by the party leader as they are about to teleport or hangup due to low HP's. Allows party followers to do the same (can be disabled in the party settings dialog).
@party <cmd>        Spoken by the party leader, all party followers will execute the specified command. This is used in situations where the party followers need to execute some command (eg. go hole) to keep up with the party leader. The leader will then re-check the party and re-invite members if needed.
@path       Returns the name of the path file the character is currently using.
@rego       Causes the player to start moving again. This will only work if they were originally roaming and they hit the stop button or were sent a @stop command.
@relog      Causes the player to issue their defined relog command and re-logon.
@reset      Resets all of MegaMud's internal flags and statistics.
@roam [on | off]        Toggles or sets the player's auto-roaming mode.
@seen <player>      Returns the whether or not the specified player has been seen, and if so, where they were last seen.
@settings       Returns a list of the player's auto settings which are currently enabled.
@share      Used only by the party leader to signal all members should share any cash collected between the rest of the party.
@status     Returns the current task and action (the messages displayed in the user's status bar) to the sender.
@stop       Causes the player to stop moving.
@version        Returns the version of MegaMud currently running on the receiver's computer.
@wait       Telepathed by MegaMud to the party leader if it needs to rest. This is sent to inform the leader that it should stop moving and wait for the player to heal.
@wealth     Returns the wealth (in copper) being carried by the player.
@what       Returns the list of items visible within the current room.
@where      Returns the current location (room name) of the player. The available room exits are also shown.
@who        Returns the list of players and monsters who are currently within the same room as you.

----------------
Remote Commands: EXE Strings Dump (not listed in help file)
----------------
@ver        (@version in help file)
@capture    (not listed in help file)
@debug      (not listed in help file)
*/
}
