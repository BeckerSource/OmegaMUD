public class OMUD_MMUDBlock_Stats extends OMUD_MMUDBlocks.Block{
    private final String MSTR_PREFIX_RESET_WHBL = "[0;37;40m";
    private final String MSTR_NAME =        "[79D[K[0m[32mName:";
    private final String MSTR_LIVES =       "Lives/CP:";
    private final String MSTR_RACE =        "Race:";
    private final String MSTR_EXP =         "Exp:";
    private final String MSTR_PERC =        "Perception:";
    private final String MSTR_CLASS =       "Class:";
    private final String MSTR_LEVEL =       "Level:";
    private final String MSTR_STEALTH =     "Stealth:";
    private final String MSTR_HITS =        "Hits:";
    private final String MSTR_AC =          "Armour Class:";
    private final String MSTR_THIEVERY =    "Thievery:";
    private final String MSTR_SC =          "Spellcasting:";
    private final String MSTR_TRAPS =       "Traps:";
    private final String MSTR_PICK =        "Picklocks:";
    private final String MSTR_STR =         "Strength:";
    private final String MSTR_AGI =         "Agility:";
    private final String MSTR_TRACK =       "Tracking:";
    private final String MSTR_INTEL =       "Intellect:";
    private final String MSTR_HEA =         "Health:";
    private final String MSTR_MA =          "Martial Arts:";
    private final String MSTR_WIL =         "Willpower:";
    private final String MSTR_CHA =         "Charm:";
    private final String MSTR_MR =          "MagicRes:";
    private final int    MSTR_STAT_ROW_COUNT = 9;

    public boolean getStatlineWait(){return true;}
    public OMUD_MMUDBlock_Stats(){
        _arrlCmdText.add(new CmdText(OMUD_MMUD.DataBlock.CMD_STRINGS[OMUD_MMUD.DataBlock.eBlockType.STATS.ordinal()], 2));
    }

    public int findBlockData(OMUD_MMUDChar mmc, StringBuilder sbTelnetData, int pos_offset){
        int pos_data_found_start = -1;

        if ((pos_data_found_start = findData(sbTelnetData, pos_offset, true, true, MSTR_NAME, "")) > -1){
            mmc.ablk.data_type = OMUD_MMUD.DataBlock.eBlockType.STATS;
            mmc.dataStats = new OMUD_MMUD.DataStats();
            cleanData(_sbBlockData, false, true);

            // PREFIX: normal stat command has a prefix, coming from a new char creation does not...
            pos_data_found_start = checkPrefix("Stats from User Cmd and not New Char", mmc.ablk.sbDebug, sbTelnetData, pos_data_found_start, MSTR_PREFIX_RESET_WHBL);

            // split by lines...
            String[] lines = _sbBlockData.toString().split("\n");
            if (lines.length >= MSTR_STAT_ROW_COUNT){
                int row_num =   0;
                int pos_left =  0;
                int pos_right = _sbBlockData.length() - 1;

                // ------------------
                // Row1: Name + Lives/CP
                // ------------------
                pos_right = lines[row_num].length() - 1;
                if ((pos_left = lines[row_num].lastIndexOf(MSTR_LIVES, pos_right)) > -1){
                    // get lives/cp...
                    String[] tokens = lines[row_num].substring(pos_left + MSTR_LIVES.length(), pos_right + 1).trim().split("/");
                    if (tokens.length == 2){
                        mmc.dataStats.lives = Integer.parseInt(tokens[0]);
                        mmc.dataStats.cp =    Integer.parseInt(tokens[1]);
                    }
                    pos_right = pos_left - 1;

                    // found a last name if split...
                    mmc.dataStats.name_first = lines[row_num].substring(0, pos_right + 1).trim();
                    tokens = mmc.dataStats.name_first.split(" ");
                    if (tokens.length == 2){
                        mmc.dataStats.name_first = tokens[0];
                        mmc.dataStats.name_last  = tokens[1];
                    }
                }

                // ------------------
                // Row2: Race + Exp + Perception
                // ------------------
                pos_right = lines[++row_num].length() - 1;
                if ((pos_left = lines[row_num].lastIndexOf(MSTR_PERC, pos_right)) > -1){
                    mmc.dataStats.perc = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_PERC.length(), pos_right + 1).trim());
                    pos_right = pos_left - 1;

                    if ((pos_left = lines[row_num].lastIndexOf(MSTR_EXP, pos_right)) > -1){
                        // don't bother getting XP here - we get it from the exp command
                        pos_right = pos_left - 1;

                        if ((pos_left = lines[row_num].lastIndexOf(MSTR_RACE, pos_right)) > -1){
                            mmc.dataStats.stats_race = lines[row_num].substring(pos_left + MSTR_RACE.length(), pos_right + 1).trim();
                            pos_right = pos_left - 1;
                        }
                    }
                }

                // ------------------
                // Row3: Class + Level + Stealth
                // ------------------
                pos_right = lines[++row_num].length() - 1;
                if ((pos_left = lines[row_num].lastIndexOf(MSTR_STEALTH, pos_right)) > -1){
                    mmc.dataStats.stealth = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_STEALTH.length(), pos_right + 1).trim());
                    pos_right = pos_left - 1;

                    if ((pos_left = lines[row_num].lastIndexOf(MSTR_LEVEL, pos_right)) > -1){
                        mmc.dataStats.level = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_LEVEL.length(), pos_right + 1).trim());
                        pos_right = pos_left - 1;

                        if ((pos_left = lines[row_num].lastIndexOf(MSTR_CLASS, pos_right)) > -1){
                            mmc.dataStats.stats_class = lines[row_num].substring(pos_left + MSTR_CLASS.length(), pos_right + 1).trim();
                            pos_right = pos_left - 1;
                        }
                    }
                }

                // ------------------
                // Row4: Hits + AC + Thievery
                // ------------------
                pos_right = lines[++row_num].length() - 1;
                if ((pos_left = lines[row_num].lastIndexOf(MSTR_THIEVERY, pos_right)) > -1){
                    mmc.dataStats.thievery = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_THIEVERY.length(), pos_right + 1).trim());
                    pos_right = pos_left - 1;

                    if ((pos_left = lines[row_num].lastIndexOf(MSTR_AC, pos_right)) > -1){
                        String[] tokens = lines[row_num].substring(pos_left + MSTR_AC.length(), pos_right + 1).trim().split("/");
                        if (tokens.length == 2){
                            mmc.dataStats.ac_ac =   Integer.parseInt(tokens[0]);
                            mmc.dataStats.ac_accy = Integer.parseInt(tokens[1]);
                        }
                        pos_right = pos_left - 1;

                        if ((pos_left = lines[row_num].lastIndexOf(MSTR_HITS, pos_right)) > -1){
                            // check for modifier...
                            StringBuilder sbVal = new StringBuilder();
                            mmc.dataStatline.hp_mod = parseModdedStat(lines[row_num].substring(pos_left + MSTR_HITS.length(), pos_right + 1).trim(), sbVal);
                            tokens = sbVal.toString().split("/");
                            if (tokens.length == 2){
                                mmc.dataStatline.hp_cur = Integer.parseInt(tokens[0]);
                                mmc.dataStatline.hp_max = Integer.parseInt(tokens[1]);
                            }
                            pos_right = pos_left - 1;
                        }
                    }
                }

                // ------------------
                // Row5: Mana/Kai + SC + Traps
                // ------------------
                pos_right = lines[++row_num].length() - 1;
                if ((pos_left = lines[row_num].lastIndexOf(MSTR_TRAPS, pos_right)) > -1){
                    mmc.dataStats.traps = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_TRAPS.length(), pos_right + 1).trim());
                    pos_right = pos_left - 1;

                    if ((pos_left = lines[row_num].lastIndexOf(MSTR_SC, pos_right)) > -1){
                        mmc.dataStats.sc = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_SC.length(), pos_right + 1).trim());
                        pos_right = pos_left - 1;
                    }

                    if ((pos_left = lines[row_num].lastIndexOf(":", pos_right)) > -1){
                        // check for modifier...
                        StringBuilder sbVal = new StringBuilder();
                        mmc.dataStatline.ma_mod = parseModdedStat(lines[row_num].substring(pos_left + 1, pos_right + 1).trim(), sbVal);
                        String[] tokens = sbVal.toString().split("/");
                        if (tokens.length == 2){
                            mmc.dataStatline.ma_cur = Integer.parseInt(tokens[0]);
                            mmc.dataStatline.ma_max = Integer.parseInt(tokens[1]);
                        }
                        pos_right = pos_left - 1;
                    }
                }

                // ------------------
                // Row6: Picklocks
                // ------------------
                pos_right = lines[++row_num].length() - 1;
                if ((pos_left = lines[row_num].lastIndexOf(MSTR_PICK, pos_right)) > -1){
                    mmc.dataStats.pick = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_PICK.length(), pos_right + 1).trim());
                    pos_right = pos_left - 1;
                }

                // ------------------
                // Row7: Str + Agil + Tracking
                // ------------------
                pos_right = lines[++row_num].length() - 1;
                if ((pos_left = lines[row_num].lastIndexOf(MSTR_TRACK, pos_right)) > -1){
                    mmc.dataStats.track = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_TRACK.length(), pos_right + 1).trim());
                    pos_right = pos_left - 1;

                    if ((pos_left = lines[row_num].lastIndexOf(MSTR_AGI, pos_right)) > -1){
                        StringBuilder sbVal = new StringBuilder();
                        mmc.dataStats.agi_mod = parseModdedStat(lines[row_num].substring(pos_left + MSTR_AGI.length(), pos_right + 1).trim(), sbVal);
                        mmc.dataStats.agi = Integer.parseInt(sbVal.toString());
                        pos_right = pos_left - 1;

                        if ((pos_left = lines[row_num].lastIndexOf(MSTR_STR, pos_right)) > -1){
                            mmc.dataStats.str_mod = parseModdedStat(lines[row_num].substring(pos_left + MSTR_STR.length(), pos_right + 1).trim(), sbVal);
                            mmc.dataStats.str = Integer.parseInt(sbVal.toString());
                            pos_right = pos_left - 1;
                        }
                    }
                }

                // ------------------
                // Row8: Int + Health + MA
                // ------------------
                pos_right = lines[++row_num].length() - 1;
                if ((pos_left = lines[row_num].lastIndexOf(MSTR_MA, pos_right)) > -1){
                    mmc.dataStats.ma = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_MA.length(), pos_right + 1).trim());
                    pos_right = pos_left - 1;

                    if ((pos_left = lines[row_num].lastIndexOf(MSTR_HEA, pos_right)) > -1){
                        StringBuilder sbVal = new StringBuilder();
                        mmc.dataStats.hea_mod = parseModdedStat(lines[row_num].substring(pos_left + MSTR_HEA.length(), pos_right + 1).trim(), sbVal);
                        mmc.dataStats.hea = Integer.parseInt(sbVal.toString());
                        pos_right = pos_left - 1;

                        if ((pos_left = lines[row_num].lastIndexOf(MSTR_INTEL, pos_right)) > -1){
                            mmc.dataStats.intel_mod = parseModdedStat(lines[row_num].substring(pos_left + MSTR_INTEL.length(), pos_right + 1).trim(), sbVal);
                            mmc.dataStats.intel = Integer.parseInt(sbVal.toString());
                            pos_right = pos_left - 1;
                        }
                    }
                }

                // ------------------
                // Row9: Wil + Charm + MR
                // ------------------
                pos_right = lines[++row_num].length() - 1;
                if ((pos_left = lines[row_num].lastIndexOf(MSTR_MR, pos_right)) > -1){
                    mmc.dataStats.mr = Integer.parseInt(lines[row_num].substring(pos_left + MSTR_MR.length(), pos_right + 1).trim());
                    pos_right = pos_left - 1;

                    if ((pos_left = lines[row_num].lastIndexOf(MSTR_CHA, pos_right)) > -1){
                        StringBuilder sbVal = new StringBuilder();
                        mmc.dataStats.cha_mod = parseModdedStat(lines[row_num].substring(pos_left + MSTR_CHA.length(), pos_right + 1).trim(), sbVal);
                        mmc.dataStats.cha = Integer.parseInt(sbVal.toString());
                        pos_right = pos_left - 1;

                        if ((pos_left = lines[row_num].lastIndexOf(MSTR_WIL, pos_right)) > -1){
                            mmc.dataStats.wil_mod = parseModdedStat(lines[row_num].substring(pos_left + MSTR_WIL.length(), pos_right + 1).trim(), sbVal);
                            mmc.dataStats.wil = Integer.parseInt(sbVal.toString());
                            pos_right = pos_left - 1;
                        }
                    }
                }

                // ------------------
                // Row10+: Buffs
                // ------------------
                // todo: parse buffs
            }
        }

        return pos_data_found_start;
    }

    private boolean parseModdedStat(String strVal, StringBuilder sbNewVal){
        boolean is_modded = strVal.charAt(0) == '*';
        if (is_modded)
            strVal = strVal.substring(1, strVal.length()).trim();
        sbNewVal.setLength(0);
        sbNewVal.append(strVal);
        return is_modded;
    }
}
