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
package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.L1DatabaseFactory;
import com.lineage.server.datatables.InnTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.templates.L1Inn;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;

// Referenced classes of package com.lineage.server.model:
// L1Teleport, L1PcInstance

/**
 * 各个传送点(上下各种地监、楼层的出入口等等)
 */
public class Dungeon {

    private enum DungeonType {
        NONE, SHIP_FOR_FI, SHIP_FOR_HEINE, SHIP_FOR_PI, SHIP_FOR_HIDDENDOCK, SHIP_FOR_GLUDIN, SHIP_FOR_TI, TALKING_ISLAND_HOTEL, GLUDIO_HOTEL, SILVER_KNIGHT_HOTEL, WINDAWOOD_HOTEL, HEINE_HOTEL, GIRAN_HOTEL, OREN_HOTEL
    }

    private static class NewDungeon {
        int _newX;

        int _newY;

        short _newMapId;

        int _heading;

        DungeonType _dungeonType;

        NewDungeon(final int newX, final int newY, final short newMapId,
                final int heading, final DungeonType dungeonType) {
            this._newX = newX;
            this._newY = newY;
            this._newMapId = newMapId;
            this._heading = heading;
            this._dungeonType = dungeonType;

        }
    }

    private static Logger _log = Logger.getLogger(Dungeon.class.getName());

    private static Dungeon _instance = null;

    private static Map<String, NewDungeon> _dungeonMap = Maps.newMap();

    public static Dungeon getInstance() {
        if (_instance == null) {
            _instance = new Dungeon();
        }
        return _instance;
    }

    private Dungeon() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();

            pstm = con.prepareStatement("SELECT * FROM dungeon");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int srcMapId = rs.getInt("src_mapid");
                final int srcX = rs.getInt("src_x");
                final int srcY = rs.getInt("src_y");
                final String key = new StringBuilder().append(srcMapId)
                        .append(srcX).append(srcY).toString();
                final int newX = rs.getInt("new_x");
                final int newY = rs.getInt("new_y");
                final int newMapId = rs.getInt("new_mapid");
                final int heading = rs.getInt("new_heading");
                DungeonType dungeonType = DungeonType.NONE;
                if ((((srcX == 33423) || (srcX == 33424) || (srcX == 33425) || (srcX == 33426))
                        && (srcY == 33502) && (srcMapId == 4 // ハイネ船着场->FI行きの船
                ))
                        || (((srcX == 32733) || (srcX == 32734)
                                || (srcX == 32735) || (srcX == 32736))
                                && (srcY == 32794) && (srcMapId == 83))) { // FI行きの船->ハイネ船着场
                    dungeonType = DungeonType.SHIP_FOR_FI;
                } else if ((((srcX == 32935) || (srcX == 32936) || (srcX == 32937))
                        && (srcY == 33058) && (srcMapId == 70 // FI船着场->ハイネ行きの船
                ))
                        || (((srcX == 32732) || (srcX == 32733)
                                || (srcX == 32734) || (srcX == 32735))
                                && (srcY == 32796) && (srcMapId == 84))) { // ハイネ行きの船->FI船着场
                    dungeonType = DungeonType.SHIP_FOR_HEINE;
                } else if ((((srcX == 32750) || (srcX == 32751) || (srcX == 32752))
                        && (srcY == 32874) && (srcMapId == 445 // 隐された船着场->海贼岛行きの船
                ))
                        || (((srcX == 32731) || (srcX == 32732) || (srcX == 32733))
                                && (srcY == 32796) && (srcMapId == 447))) { // 海贼岛行きの船->隐された船着场
                    dungeonType = DungeonType.SHIP_FOR_PI;
                } else if ((((srcX == 32296) || (srcX == 32297) || (srcX == 32298))
                        && (srcY == 33087) && (srcMapId == 440 // 海贼岛船着场->隐された船着场行きの船
                ))
                        || (((srcX == 32735) || (srcX == 32736) || (srcX == 32737))
                                && (srcY == 32794) && (srcMapId == 446))) { // 隐された船着场行きの船->海贼岛船着场
                    dungeonType = DungeonType.SHIP_FOR_HIDDENDOCK;
                } else if ((((srcX == 32630) || (srcX == 32631) || (srcX == 32632))
                        && (srcY == 32983) && (srcMapId == 0 // TalkingIsland->TalkingIslandShiptoAdenMainland
                ))
                        || (((srcX == 32733) || (srcX == 32734) || (srcX == 32735))
                                && (srcY == 32796) && (srcMapId == 5))) { // TalkingIslandShiptoAdenMainland->TalkingIsland
                    dungeonType = DungeonType.SHIP_FOR_GLUDIN;
                } else if ((((srcX == 32540) || (srcX == 32542)
                        || (srcX == 32543) || (srcX == 32544) || (srcX == 32545))
                        && (srcY == 32728) && (srcMapId == 4 // AdenMainland->AdenMainlandShiptoTalkingIsland
                ))
                        || (((srcX == 32734) || (srcX == 32735)
                                || (srcX == 32736) || (srcX == 32737))
                                && (srcY == 32794) && (srcMapId == 6))) { // AdenMainlandShiptoTalkingIsland->AdenMainland
                    dungeonType = DungeonType.SHIP_FOR_TI;
                } else if ((srcX == 32600) && (srcY == 32931)
                        && (srcMapId == 0)) { // 说话之岛旅馆
                    dungeonType = DungeonType.TALKING_ISLAND_HOTEL;
                } else if ((srcX == 32632) && (srcY == 32761)
                        && (srcMapId == 4)) { // 古鲁丁旅馆
                    dungeonType = DungeonType.GLUDIO_HOTEL;
                } else if ((srcX == 33116) && (srcY == 33379)
                        && (srcMapId == 4)) { // 银骑士旅馆
                    dungeonType = DungeonType.SILVER_KNIGHT_HOTEL;
                } else if ((srcX == 32628) && (srcY == 33167)
                        && (srcMapId == 4)) { // 风木旅馆
                    dungeonType = DungeonType.WINDAWOOD_HOTEL;
                } else if ((srcX == 33605) && (srcY == 33275)
                        && (srcMapId == 4)) { // 海音旅馆
                    dungeonType = DungeonType.HEINE_HOTEL;
                } else if ((srcX == 33437) && (srcY == 32789)
                        && (srcMapId == 4)) { // 奇岩旅馆
                    dungeonType = DungeonType.GIRAN_HOTEL;
                } else if ((srcX == 34068) && (srcY == 32254)
                        && (srcMapId == 4)) { // 欧瑞旅馆
                    dungeonType = DungeonType.OREN_HOTEL;
                }
                final NewDungeon newDungeon = new NewDungeon(newX, newY,
                        (short) newMapId, heading, dungeonType);
                if (_dungeonMap.containsKey(key)) {
                    _log.log(Level.WARNING, "Navicat dungeon 传送点重复。key=" + key);
                }
                _dungeonMap.put(key, newDungeon);
            }
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    // 检查身上的钥匙
    private int checkInnKey(final L1PcInstance pc, final int npcid) {
        for (final L1ItemInstance item : pc.getInventory().getItems()) {
            if (item.getInnNpcId() == npcid) { // 钥匙与旅馆NPC相符
                for (int i = 0; i < 16; i++) {
                    final L1Inn inn = InnTable.getInstance().getTemplate(npcid,
                            i);
                    if (inn.getKeyId() == item.getKeyId()) {
                        final Timestamp dueTime = item.getDueTime();
                        if (dueTime != null) { // 时间不为空值
                            final Calendar cal = Calendar.getInstance();
                            if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) { // 租用时间未到
                                pc.setInnKeyId(item.getKeyId()); // 登入此钥匙
                                return item.checkRoomOrHall() ? 2 : 1; // 1:房间
                                                                       // 2:会议室
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    public boolean dg(final int locX, final int locY, final int mapId,
            final L1PcInstance pc) {
        final int servertime = L1GameTimeClock.getInstance().currentTime()
                .getSeconds();
        final int nowtime = servertime % 86400;
        final String key = new StringBuilder().append(mapId).append(locX)
                .append(locY).toString();
        if (_dungeonMap.containsKey(key)) {
            final NewDungeon newDungeon = _dungeonMap.get(key);
            short newMap = newDungeon._newMapId;
            int newX = newDungeon._newX;
            int newY = newDungeon._newY;
            int heading = newDungeon._heading;
            final DungeonType dungeonType = newDungeon._dungeonType;
            boolean teleportable = false;

            if (dungeonType == DungeonType.NONE) {
                teleportable = true;
            } else {
                if ((dungeonType == DungeonType.TALKING_ISLAND_HOTEL)
                        || (dungeonType == DungeonType.GLUDIO_HOTEL)
                        || (dungeonType == DungeonType.WINDAWOOD_HOTEL)
                        || (dungeonType == DungeonType.SILVER_KNIGHT_HOTEL)
                        || (dungeonType == DungeonType.HEINE_HOTEL)
                        || (dungeonType == DungeonType.GIRAN_HOTEL)
                        || (dungeonType == DungeonType.OREN_HOTEL)) {
                    int npcid = 0;
                    int[] data = null;
                    if (dungeonType == DungeonType.TALKING_ISLAND_HOTEL) {
                        npcid = 70012; // 说话之岛 - 瑟琳娜
                        data = new int[] { 32745, 32803, 16384, 32743, 32808,
                                16896 };
                    } else if (dungeonType == DungeonType.GLUDIO_HOTEL) {
                        npcid = 70019; // 古鲁丁 - 罗利雅
                        data = new int[] { 32743, 32803, 17408, 32744, 32807,
                                17920 };
                    } else if (dungeonType == DungeonType.GIRAN_HOTEL) {
                        npcid = 70031; // 奇岩 - 玛理
                        data = new int[] { 32744, 32803, 18432, 32744, 32807,
                                18944 };
                    } else if (dungeonType == DungeonType.OREN_HOTEL) {
                        npcid = 70065; // 欧瑞 - 小安安
                        data = new int[] { 32744, 32803, 19456, 32744, 32807,
                                19968 };
                    } else if (dungeonType == DungeonType.WINDAWOOD_HOTEL) {
                        npcid = 70070; // 风木 - 维莱莎
                        data = new int[] { 32744, 32803, 20480, 32744, 32807,
                                20992 };
                    } else if (dungeonType == DungeonType.SILVER_KNIGHT_HOTEL) {
                        npcid = 70075; // 银骑士 - 米兰德
                        data = new int[] { 32744, 32803, 21504, 32744, 32807,
                                22016 };
                    } else if (dungeonType == DungeonType.HEINE_HOTEL) {
                        npcid = 70084; // 海音 - 伊莉
                        data = new int[] { 32744, 32803, 22528, 32744, 32807,
                                23040 };
                    }

                    final int type = this.checkInnKey(pc, npcid);

                    if (type == 1) { // 房间
                        newX = data[0];
                        newY = data[1];
                        newMap = (short) data[2];
                        heading = 6;
                        teleportable = true;
                    } else if (type == 2) { // 会议室
                        newX = data[3];
                        newY = data[4];
                        newMap = (short) data[5];
                        heading = 6;
                        teleportable = true;
                    }
                } else if (((nowtime >= 15 * 360) && (nowtime < 25 * 360 // 1.30~2.30
                ))
                        || ((nowtime >= 45 * 360) && (nowtime < 55 * 360 // 4.30~5.30
                        ))
                        || ((nowtime >= 75 * 360) && (nowtime < 85 * 360 // 7.30~8.30
                        ))
                        || ((nowtime >= 105 * 360) && (nowtime < 115 * 360 // 10.30~11.30
                        ))
                        || ((nowtime >= 135 * 360) && (nowtime < 145 * 360))
                        || ((nowtime >= 165 * 360) && (nowtime < 175 * 360))
                        || ((nowtime >= 195 * 360) && (nowtime < 205 * 360))
                        || ((nowtime >= 225 * 360) && (nowtime < 235 * 360))) {
                    if ((pc.getInventory().checkItem(40299, 1) && (dungeonType == DungeonType.SHIP_FOR_GLUDIN)) // TalkingIslandShiptoAdenMainland
                            || (pc.getInventory().checkItem(40301, 1) && (dungeonType == DungeonType.SHIP_FOR_HEINE)) // AdenMainlandShiptoForgottenIsland
                            || (pc.getInventory().checkItem(40302, 1) && (dungeonType == DungeonType.SHIP_FOR_PI))) { // ShipPirateislandtoHiddendock
                        teleportable = true;
                    }
                } else if (((nowtime >= 0) && (nowtime < 360))
                        || ((nowtime >= 30 * 360) && (nowtime < 40 * 360))
                        || ((nowtime >= 60 * 360) && (nowtime < 70 * 360))
                        || ((nowtime >= 90 * 360) && (nowtime < 100 * 360))
                        || ((nowtime >= 120 * 360) && (nowtime < 130 * 360))
                        || ((nowtime >= 150 * 360) && (nowtime < 160 * 360))
                        || ((nowtime >= 180 * 360) && (nowtime < 190 * 360))
                        || ((nowtime >= 210 * 360) && (nowtime < 220 * 360))) {
                    if ((pc.getInventory().checkItem(40298, 1) && (dungeonType == DungeonType.SHIP_FOR_TI)) // AdenMainlandShiptoTalkingIsland
                            || (pc.getInventory().checkItem(40300, 1) && (dungeonType == DungeonType.SHIP_FOR_FI)) // ForgottenIslandShiptoAdenMainland
                            || (pc.getInventory().checkItem(40303, 1) && (dungeonType == DungeonType.SHIP_FOR_HIDDENDOCK))) { // ShipHiddendocktoPirateisland
                        teleportable = true;
                    }
                }
            }

            if (teleportable) {
                // 2秒无敌状态。
                pc.setSkillEffect(ABSOLUTE_BARRIER, 2000);
                pc.stopHpRegeneration();
                pc.stopMpRegeneration();
                pc.stopHpRegenerationByDoll();
                pc.stopMpRegenerationByDoll();
                L1Teleport.teleport(pc, newX, newY, newMap, heading, false);
                return true;
            }
        }
        return false;
    }

}
