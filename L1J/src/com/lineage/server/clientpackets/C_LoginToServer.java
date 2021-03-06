/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.COOKING_1_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_WONDER_DRUG;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_BEGIN;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_BLOODSTAIN_OF_ANTHARAS;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_BLOODSTAIN_OF_FAFURION;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_END;
import static com.lineage.server.model.skill.L1SkillId.MIRROR_IMAGE;
import static com.lineage.server.model.skill.L1SkillId.SHAPE_CHANGE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BLUE_POTION;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_BRAVE2;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_ELFBRAVE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HASTE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_RIBRAVE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_THIRD_SPEED;
import static com.lineage.server.model.skill.L1SkillId.UNCANNY_DODGE;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.Config;
import com.lineage.L1DatabaseFactory;
import com.lineage.server.Account;
import com.lineage.server.ActionCodes;
import com.lineage.server.ClientThread;
import com.lineage.server.WarTimeController;
import com.lineage.server.datatables.CharacterTable;
import com.lineage.server.datatables.GetBackRestartTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Getback;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Cooking;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1War;
import com.lineage.server.model.L1World;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ActiveSpells;
import com.lineage.server.serverpackets.S_AddSkill;
import com.lineage.server.serverpackets.S_Bookmarks;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_CharacterConfig;
import com.lineage.server.serverpackets.S_InitialAbilityGrowth;
import com.lineage.server.serverpackets.S_InvList;
import com.lineage.server.serverpackets.S_Invis;
import com.lineage.server.serverpackets.S_Karma;
import com.lineage.server.serverpackets.S_Liquor;
import com.lineage.server.serverpackets.S_LoginGame;
import com.lineage.server.serverpackets.S_MapID;
import com.lineage.server.serverpackets.S_OwnCharPack;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillIconGFX;
import com.lineage.server.serverpackets.S_SkillIconThirdSpeed;
import com.lineage.server.serverpackets.S_SummonPack;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_War;
import com.lineage.server.serverpackets.S_Weather;
import com.lineage.server.templates.L1BookMark;
import com.lineage.server.templates.L1GetBackRestart;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.SQLUtil;

// Referenced classes of package com.lineage.server.clientpackets:
// ClientBasePacket
//

/**
 * ?????????????????????????????????????????????????????????
 */
public class C_LoginToServer extends ClientBasePacket {

    private static final String C_LOGIN_TO_SERVER = "[C] C_LoginToServer";

    private static Logger _log = Logger.getLogger(C_LoginToServer.class
            .getName());

    public C_LoginToServer(final byte abyte0[], final ClientThread client)
            throws FileNotFoundException, Exception {

        // ????????????
        super(abyte0);

        // ??????????????????
        final String login = client.getAccountName();

        // ??????????????????
        final String charName = this.readS();

        if (client.getActiveChar() != null) {
            _log.info("?????????????????????????????????????????? " + client.getHostname() + ") ?????????");
            client.close();
            return;
        }

        // ????????????
        final L1PcInstance pc = L1PcInstance.load(charName);
        // ????????????
        final Account account = Account.load(pc.getAccountName());

        // ?????????????????????????????????
        if (account.isOnlineStatus()) {
            _log.info("???????????????????????????????????????????????? " + client.getHostname() + ") ?????????");
            client.close();
            return;
        }

        // ?????????????????????
        if ((pc == null) || !login.equals(pc.getAccountName())) {
            _log.info("?????????????????????: ??????=" + charName + " ??????=" + login + " IP="
                    + client.getHostname());
            client.close();
            return;
        }

        // ???????????????????????????
        if (Config.LEVEL_DOWN_RANGE != 0) {
            if (pc.getHighLevel() - pc.getLevel() >= Config.LEVEL_DOWN_RANGE) {
                _log.info("???????????????????????????????????????????????????: ??????=" + charName + " ??????=" + login
                        + " IP=" + client.getHostname());
                client.kick();
                return;
            }
        }

        _log.info("???????????????????????????: ??????=" + charName + " ??????=" + login + " IP="
                + client.getHostname());

        final int currentHpAtLoad = pc.getCurrentHp(); // ???????????????????????????
        final int currentMpAtLoad = pc.getCurrentMp(); // ???????????????????????????
        pc.clearSkillMastery();
        pc.setOnlineStatus(1); // ?????????????????????
        CharacterTable.updateOnlineStatus(pc); // ??????????????????
        L1World.getInstance().storeObject(pc);

        pc.setNetConnection(client); // ????????????????????????
        pc.setPacketOutput(client); // ????????????????????????
        client.setActiveChar(pc); // ?????????????????????

        /** ?????????????????? */
        final S_InitialAbilityGrowth AbilityGrowth = new S_InitialAbilityGrowth(
                pc);
        pc.sendPackets(AbilityGrowth); // ????????????????????????

        /*
         * S_Unknown1 s_unknown1 = new S_Unknown1(); pc.sendPackets(s_unknown1);
         * S_Unknown2 s_unknown2 = new S_Unknown2(); pc.sendPackets(s_unknown2);
         */
        pc.sendPackets(new S_LoginGame());
        this.bookmarks(pc); // ??????????????????

        // Online = 1
        // Account account = Account.load(pc.getAccountName());
        // Account.online(account, true);

        // OnlineStatus = 1
        Account.OnlineStatus(account, true); // ??????????????????

        // ??????????????????????????????????????????
        final GetBackRestartTable gbrTable = GetBackRestartTable.getInstance();
        final L1GetBackRestart[] gbrList = gbrTable
                .getGetBackRestartTableList();
        for (final L1GetBackRestart gbr : gbrList) {
            if (pc.getMapId() == gbr.getArea()) {
                pc.setX(gbr.getLocX());
                pc.setY(gbr.getLocY());
                pc.setMap(gbr.getMapId());
                break;
            }
        }

        // altsettings.properties ??? GetBack ????????? true ???????????????
        if (Config.GET_BACK) {
            final int[] loc = Getback.GetBack_Location(pc, true);
            pc.setX(loc[0]);
            pc.setY(loc[1]);
            pc.setMap((short) loc[2]);
        }

        // ????????????????????????????????????????????????????????????????????????
        final int castle_id = L1CastleLocation.getCastleIdByArea(pc);
        if (0 < castle_id) {
            if (WarTimeController.getInstance().isNowWar(castle_id)) {
                final L1Clan clan = L1World.getInstance().getClan(
                        pc.getClanname());
                if (clan != null) {
                    if (clan.getCastleId() != castle_id) {
                        // ????????????
                        int[] loc = new int[3];
                        loc = L1CastleLocation.getGetBackLoc(castle_id);
                        pc.setX(loc[0]);
                        pc.setY(loc[1]);
                        pc.setMap((short) loc[2]);
                    }
                } else {
                    // ????????????????????????
                    int[] loc = new int[3];
                    loc = L1CastleLocation.getGetBackLoc(castle_id);
                    pc.setX(loc[0]);
                    pc.setY(loc[1]);
                    pc.setMap((short) loc[2]);
                }
            }
        }

        L1World.getInstance().addVisibleObject(pc);
        final S_ActiveSpells s_activespells = new S_ActiveSpells(pc);
        pc.sendPackets(s_activespells);

        pc.beginGameTimeCarrier();

        final S_OwnCharStatus s_owncharstatus = new S_OwnCharStatus(pc);
        pc.sendPackets(s_owncharstatus); // ??????????????????????????????

        final S_MapID s_mapid = new S_MapID(pc.getMapId(), pc.getMap()
                .isUnderwater());
        pc.sendPackets(s_mapid); // ??????????????????????????????

        final S_OwnCharPack s_owncharpack = new S_OwnCharPack(pc);
        pc.sendPackets(s_owncharpack); // ????????????????????????

        pc.sendPackets(new S_SPMR(pc)); // ????????????????????????

        // XXX S_OwnCharPack ?????????????????????
        final S_CharTitle s_charTitle = new S_CharTitle(pc.getId(),
                pc.getTitle());
        pc.sendPackets(s_charTitle); // ??????????????????
        pc.broadcastPacket(s_charTitle);

        pc.sendVisualEffectAtLogin(); // ?????????????????????????????????????????????

        pc.sendPackets(new S_Weather(L1World.getInstance().getWeather())); // ??????????????????

        this.items(pc); // ?????????????????????
        this.skills(pc); // ?????????????????????
        this.buff(client, pc); // ?????????????????????
        pc.turnOnOffLight(); // ???????????? (????????????)

        pc.sendPackets(new S_Karma(pc)); // ?????????
        /* ????????? */
        pc.sendPackets(new S_PacketBox(88, pc.getDodge())); // ???
        pc.sendPackets(new S_PacketBox(101, pc.getNdodge())); // ???
        /* ????????? */

        // ????????????????????????0
        if (pc.getCurrentHp() > 0) {
            pc.setDead(false); // ?????????????????? (??????)
            pc.setStatus(0); // ???????????????
            // ???????????????
            if (pc.get_food() >= 225) {
                pc.setCryTime(0);
                pc.startCryOfSurvival();
            }
        } else {
            pc.setDead(true); // ?????????????????? (??????)
            pc.setStatus(ActionCodes.ACTION_Die); // ??????????????????
        }

        // ????????????????????????????????????
        final short map = pc.getMapId();
        if ((map == 53) || (map == 54) || (map == 55) || (map == 56)) {
            pc.startRocksPrison();
            // System.out.println("?????????????????????");
        }

        // GM ??????????????????
        if (pc.isGm() || pc.isMonitor()) {
            pc.setGmInvis(true);
            pc.sendPackets(new S_Invis(pc.getId(), 1));
            pc.broadcastPacket(new S_RemoveObject(pc));
            pc.sendPackets(new S_SystemMessage("????????????????????????"));
        }

        // ????????????????????????????????????
        if (Config.CHARACTER_CONFIG_IN_SERVER_SIDE) {
            pc.sendPackets(new S_CharacterConfig(pc.getId()));
        }

        this.serchSummon(pc); // ????????????????????????
        pc.setBeginGhostFlag(true);

        WarTimeController.getInstance().checkCastleWar(pc); // ???????????????

        if (pc.getClanid() != 0) { // ?????????
            // ??????????????????
            final L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                if ((pc.getClanid() == clan.getClanId()) && // ?????????????????????????????????????????????????????????
                        pc.getClanname().toLowerCase()
                                .equals(clan.getClanName().toLowerCase())) {
                    // ????????????????????????
                    final L1PcInstance[] clanMembers = clan
                            .getOnlineClanMember();
                    for (final L1PcInstance clanMember : clanMembers) {
                        if (clanMember.getId() != pc.getId()) {
                            clanMember.sendPackets(new S_ServerMessage(843, pc
                                    .getName())); // ????????????%0%s??????????????????
                        }
                    }

                    // ?????????????????????
                    for (final L1War war : L1World.getInstance().getWarList()) {
                        // ??????????????????????????????
                        final boolean ret = war
                                .CheckClanInWar(pc.getClanname());
                        // ?????????
                        if (ret) {
                            // ????????????????????????
                            final String enemy_clan_name = war
                                    .GetEnemyClanName(pc.getClanname());
                            if (enemy_clan_name != null) {
                                pc.sendPackets(new S_War(8, pc.getClanname(),
                                        enemy_clan_name)); // \f1????????????????????? %0
                                                           // ?????????????????????
                            }
                            break;
                        }
                    }
                } // ?????????????????????
                else {
                    pc.setClanid(0); // ????????????ID
                    pc.setClanname(""); // ??????????????????
                    pc.setClanRank(0); // ??????????????????
                    pc.save(); // ????????????????????????????????????
                }
            }
        }

        // ?????????????????????
        if (pc.getPartnerId() != 0) {
            // ????????????ID
            final L1PcInstance partner = (L1PcInstance) L1World.getInstance()
                    .findObject(pc.getPartnerId());
            if ((partner != null) && (partner.getPartnerId() != 0)) {
                if ((pc.getPartnerId() == partner.getId())
                        && (partner.getPartnerId() == pc.getId())) {
                    pc.sendPackets(new S_ServerMessage(548)); // ?????????????????????????????????
                    partner.sendPackets(new S_ServerMessage(549)); // ?????????????????????!!
                }
            }
        }

        if (currentHpAtLoad > pc.getCurrentHp()) {
            pc.setCurrentHp(currentHpAtLoad);
        }
        if (currentMpAtLoad > pc.getCurrentMp()) {
            pc.setCurrentMp(currentMpAtLoad);
        }
        pc.startHpRegeneration(); // ??????????????????????????????
        pc.startMpRegeneration(); // ??????????????????????????????
        pc.startObjectAutoUpdate(); // ??????????????????
        client.CharReStart(false); // ?????????????????? (??????)
        pc.beginExpMonitor(); // ?????????????????????????????????
        pc.save(); // ????????????????????????????????????

        pc.sendPackets(new S_OwnCharStatus(pc)); // ??????????????????????????????

        // ????????????????????????0
        if (pc.getHellTime() > 0) {
            pc.beginHell(false); // ?????????????????? (??????)
        }

        // ????????????????????????(???????????????)?????????????????????
        pc.checkNoviceType();
    }

    // ???????????????????????????
    private void bookmarks(final L1PcInstance pc) {

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {

            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT * FROM character_teleport WHERE char_id=? ORDER BY name ASC");
            pstm.setInt(1, pc.getId());

            rs = pstm.executeQuery();
            while (rs.next()) {
                final L1BookMark bookmark = new L1BookMark();
                bookmark.setId(rs.getInt("id"));
                bookmark.setCharId(rs.getInt("char_id"));
                bookmark.setName(rs.getString("name"));
                bookmark.setLocX(rs.getInt("locx"));
                bookmark.setLocY(rs.getInt("locy"));
                bookmark.setMapId(rs.getShort("mapid"));
                final S_Bookmarks s_bookmarks = new S_Bookmarks(
                        bookmark.getName(), bookmark.getMapId(),
                        bookmark.getId());
                pc.addBookMark(bookmark);
                pc.sendPackets(s_bookmarks);
            }

        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // ?????????????????????
    private void buff(final ClientThread clientthread, final L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {

            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT * FROM character_buff WHERE char_obj_id=?");
            pstm.setInt(1, pc.getId());
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int skillid = rs.getInt("skill_id");
                int remaining_time = rs.getInt("remaining_time");
                int time = 0;
                switch (skillid) {
                    case SHAPE_CHANGE: // ??????
                        final int poly_id = rs.getInt("poly_id");
                        L1PolyMorph.doPoly(pc, poly_id, remaining_time,
                                L1PolyMorph.MORPH_BY_LOGIN);
                        break;
                    case STATUS_BRAVE: // ????????????
                        pc.sendPackets(new S_SkillBrave(pc.getId(), 1,
                                remaining_time));
                        pc.broadcastPacket(new S_SkillBrave(pc.getId(), 1, 0));
                        pc.setBraveSpeed(1);
                        pc.setSkillEffect(skillid, remaining_time * 1000);
                        break;
                    case STATUS_ELFBRAVE: // ????????????
                        pc.sendPackets(new S_SkillBrave(pc.getId(), 3,
                                remaining_time));
                        pc.broadcastPacket(new S_SkillBrave(pc.getId(), 3, 0));
                        pc.setBraveSpeed(3);
                        pc.setSkillEffect(skillid, remaining_time * 1000);
                        break;
                    case STATUS_BRAVE2: // ????????????
                        pc.sendPackets(new S_SkillBrave(pc.getId(), 5,
                                remaining_time));
                        pc.broadcastPacket(new S_SkillBrave(pc.getId(), 5, 0));
                        pc.setBraveSpeed(5);
                        pc.setSkillEffect(skillid, remaining_time * 1000);
                        break;
                    case STATUS_HASTE: // ??????
                        pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
                                remaining_time));
                        pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                        pc.setMoveSpeed(1);
                        pc.setSkillEffect(skillid, remaining_time * 1000);
                        break;
                    case STATUS_BLUE_POTION: // ????????????
                        pc.sendPackets(new S_SkillIconGFX(34, remaining_time));
                        pc.setSkillEffect(skillid, remaining_time * 1000);
                        break;
                    case STATUS_CHAT_PROHIBITED: // ??????
                        pc.sendPackets(new S_SkillIconGFX(36, remaining_time));
                        pc.setSkillEffect(skillid, remaining_time * 1000);
                        break;
                    case STATUS_THIRD_SPEED: // ????????????
                        time = remaining_time / 4;
                        pc.sendPackets(new S_Liquor(pc.getId(), 8)); // ?????? *
                                                                     // 1.15
                        pc.broadcastPacket(new S_Liquor(pc.getId(), 8)); // ?????? *
                                                                         // 1.15
                        pc.sendPackets(new S_SkillIconThirdSpeed(time));
                        pc.setSkillEffect(skillid, time * 4 * 1000);
                        break;
                    case MIRROR_IMAGE: // ??????
                    case UNCANNY_DODGE: // ????????????
                        time = remaining_time / 16;
                        pc.addDodge((byte) 5); // ????????? + 50%
                        // ?????????????????????
                        pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
                        pc.sendPackets(new S_PacketBox(21, time));
                        pc.setSkillEffect(skillid, time * 16 * 1000);
                        break;
                    case EFFECT_BLOODSTAIN_OF_ANTHARAS: // ?????????????????????
                        remaining_time = remaining_time / 60;
                        if (remaining_time != 0) {
                            L1BuffUtil.bloodstain(pc, (byte) 0, remaining_time,
                                    false);
                        }
                        break;
                    case EFFECT_BLOODSTAIN_OF_FAFURION: // ??????????????????
                        remaining_time = remaining_time / 60;
                        if (remaining_time != 0) {
                            L1BuffUtil.bloodstain(pc, (byte) 1, remaining_time,
                                    false);
                        }
                        break;
                    default:
                        // ????????????
                        if (((skillid >= COOKING_1_0_N) && (skillid <= COOKING_1_6_N))
                                || ((skillid >= COOKING_1_0_S) && (skillid <= COOKING_1_6_S))
                                || ((skillid >= COOKING_2_0_N) && (skillid <= COOKING_2_6_N))
                                || ((skillid >= COOKING_2_0_S) && (skillid <= COOKING_2_6_S))
                                || ((skillid >= COOKING_3_0_N) && (skillid <= COOKING_3_6_N))
                                || ((skillid >= COOKING_3_0_S) && (skillid <= COOKING_3_6_S))) {
                            L1Cooking.eatCooking(pc, skillid, remaining_time);
                        }
                        // ?????????????????????????????????
                        else if ((skillid == STATUS_RIBRAVE)
                                || ((skillid >= EFFECT_BEGIN) && (skillid <= EFFECT_END))
                                || (skillid == COOKING_WONDER_DRUG)) {
                            ;
                        } else {
                            final L1SkillUse l1skilluse = new L1SkillUse();
                            l1skilluse.handleCommands(
                                    clientthread.getActiveChar(), skillid,
                                    pc.getId(), pc.getX(), pc.getY(), null,
                                    remaining_time, L1SkillUse.TYPE_LOGIN);
                        }
                        break;
                }
            }
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public String getType() {
        return C_LOGIN_TO_SERVER;
    }

    private void items(final L1PcInstance pc) {
        // ????????????????????????????????????
        CharacterTable.getInstance().restoreInventory(pc);

        pc.sendPackets(new S_InvList(pc.getInventory().getItems()));
    }

    // ????????????????????????
    private void serchSummon(final L1PcInstance pc) {
        for (final L1SummonInstance summon : L1World.getInstance()
                .getAllSummons()) {
            if (summon.getMaster().getId() == pc.getId()) {
                summon.setMaster(pc);
                pc.addPet(summon);
                for (final L1PcInstance visiblePc : L1World.getInstance()
                        .getVisiblePlayer(summon)) {
                    visiblePc.sendPackets(new S_SummonPack(summon, visiblePc));
                }
            }
        }
    }

    // ?????????????????????
    private void skills(final L1PcInstance pc) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {

            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT * FROM character_skills WHERE char_obj_id=?");
            pstm.setInt(1, pc.getId());
            rs = pstm.executeQuery();
            int i = 0;
            int lv1 = 0;
            int lv2 = 0;
            int lv3 = 0;
            int lv4 = 0;
            int lv5 = 0;
            int lv6 = 0;
            int lv7 = 0;
            int lv8 = 0;
            int lv9 = 0;
            int lv10 = 0;
            int lv11 = 0;
            int lv12 = 0;
            int lv13 = 0;
            int lv14 = 0;
            int lv15 = 0;
            int lv16 = 0;
            int lv17 = 0;
            int lv18 = 0;
            int lv19 = 0;
            int lv20 = 0;
            int lv21 = 0;
            int lv22 = 0;
            int lv23 = 0;
            int lv24 = 0;
            int lv25 = 0;
            int lv26 = 0;
            int lv27 = 0;
            int lv28 = 0;
            while (rs.next()) {
                final int skillId = rs.getInt("skill_id");
                final L1Skills l1skills = SkillsTable.getInstance()
                        .getTemplate(skillId);
                switch (l1skills.getSkillLevel()) {
                    case 1:
                        lv1 |= l1skills.getId();
                        break;

                    case 2:
                        lv2 |= l1skills.getId();
                        break;

                    case 3:
                        lv3 |= l1skills.getId();
                        break;

                    case 4:
                        lv4 |= l1skills.getId();
                        break;

                    case 5:
                        lv5 |= l1skills.getId();
                        break;

                    case 6:
                        lv6 |= l1skills.getId();
                        break;

                    case 7:
                        lv7 |= l1skills.getId();
                        break;

                    case 8:
                        lv8 |= l1skills.getId();
                        break;

                    case 9:
                        lv9 |= l1skills.getId();
                        break;

                    case 10:
                        lv10 |= l1skills.getId();
                        break;

                    case 11:
                        lv11 |= l1skills.getId();
                        break;

                    case 12:
                        lv12 |= l1skills.getId();
                        break;

                    case 13:
                        lv13 |= l1skills.getId();
                        break;

                    case 14:
                        lv14 |= l1skills.getId();
                        break;

                    case 15:
                        lv15 |= l1skills.getId();
                        break;

                    case 16:
                        lv16 |= l1skills.getId();
                        break;

                    case 17:
                        lv17 |= l1skills.getId();
                        break;

                    case 18:
                        lv18 |= l1skills.getId();
                        break;

                    case 19:
                        lv19 |= l1skills.getId();
                        break;

                    case 20:
                        lv20 |= l1skills.getId();
                        break;

                    case 21:
                        lv21 |= l1skills.getId();
                        break;

                    case 22:
                        lv22 |= l1skills.getId();
                        break;

                    case 23:
                        lv23 |= l1skills.getId();
                        break;

                    case 24:
                        lv24 |= l1skills.getId();
                        break;

                    case 25:
                        lv25 |= l1skills.getId();
                        break;

                    case 26:
                        lv26 |= l1skills.getId();
                        break;

                    case 27:
                        lv27 |= l1skills.getId();
                        break;

                    case 28:
                        lv28 |= l1skills.getId();
                        break;
                }
                i = lv1 + lv2 + lv3 + lv4 + lv5 + lv6 + lv7 + lv8 + lv9 + lv10
                        + lv11 + lv12 + lv13 + lv14 + lv15 + lv16 + lv17 + lv18
                        + lv19 + lv20 + lv21 + lv22 + lv23 + lv24 + lv25 + lv26
                        + lv27 + lv28;
                pc.setSkillMastery(skillId);
            }

            // ????????????????????????
            if (i > 0) {
                pc.sendPackets(new S_AddSkill(lv1, lv2, lv3, lv4, lv5, lv6,
                        lv7, lv8, lv9, lv10, lv11, lv12, lv13, lv14, lv15,
                        lv16, lv17, lv18, lv19, lv20, lv21, lv22, lv23, lv24,
                        lv25, lv26, lv27, lv28));
            }
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
