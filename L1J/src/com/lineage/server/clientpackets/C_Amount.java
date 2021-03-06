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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

import com.lineage.Config;
import com.lineage.server.ClientThread;
import com.lineage.server.datatables.AuctionBoardTable;
import com.lineage.server.datatables.HouseTable;
import com.lineage.server.datatables.InnKeyTable;
import com.lineage.server.datatables.InnTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.NpcActionTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1World;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.identity.L1ItemId;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.model.npc.action.L1NpcAction;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.storage.CharactersItemStorage;
import com.lineage.server.templates.L1AuctionBoard;
import com.lineage.server.templates.L1House;
import com.lineage.server.templates.L1Inn;

// Referenced classes of package com.lineage.server.clientpackets:
// ClientBasePacket, C_Amount

/**
 * ????????????????????????????????????
 */
public class C_Amount extends ClientBasePacket {

    private static final String C_AMOUNT = "[C] C_Amount";

    public C_Amount(final byte[] decrypt, final ClientThread client)
            throws Exception {
        super(decrypt);
        final int objectId = this.readD();
        final int amount = this.readD();
        this.readC();
        final String s = this.readS();

        final L1PcInstance pc = client.getActiveChar();
        final L1NpcInstance npc = (L1NpcInstance) L1World.getInstance()
                .findObject(objectId);
        if (npc == null) {
            return;
        }

        String s1 = "";
        String s2 = "";
        try {
            final StringTokenizer stringtokenizer = new StringTokenizer(s);
            s1 = stringtokenizer.nextToken();
            s2 = stringtokenizer.nextToken();
        } catch (final NoSuchElementException e) {
            s1 = "";
            s2 = "";
        }
        if (s1.equalsIgnoreCase("agapply")) { // ????????????????????????
            final String pcName = pc.getName();
            final AuctionBoardTable boardTable = new AuctionBoardTable();
            for (final L1AuctionBoard board : boardTable
                    .getAuctionBoardTableList()) {
                if (pcName.equalsIgnoreCase(board.getBidder())) {
                    pc.sendPackets(new S_ServerMessage(523)); // ???????????????????????????????????????
                    return;
                }
            }
            final int houseId = Integer.valueOf(s2);
            final L1AuctionBoard board = boardTable
                    .getAuctionBoardTable(houseId);
            if (board != null) {
                final int nowPrice = board.getPrice();
                final int nowBidderId = board.getBidderId();
                if (pc.getInventory().consumeItem(L1ItemId.ADENA, amount)) {
                    // ??????????????????
                    board.setPrice(amount);
                    board.setBidder(pcName);
                    board.setBidderId(pc.getId());
                    boardTable.updateAuctionBoard(board);
                    if (nowBidderId != 0) {
                        // ???????????????????????????
                        final L1PcInstance bidPc = (L1PcInstance) L1World
                                .getInstance().findObject(nowBidderId);
                        if (bidPc != null) { // ???????????????
                            bidPc.getInventory().storeItem(L1ItemId.ADENA,
                                    nowPrice);
                            // ???????????????????????????????????????????????????????????????%n???????????????????????????????????????????????????
                            // %0?????????%n?????????%n%n
                            bidPc.sendPackets(new S_ServerMessage(525, String
                                    .valueOf(nowPrice)));
                        } else { // ???????????????
                            final L1ItemInstance item = ItemTable.getInstance()
                                    .createItem(L1ItemId.ADENA);
                            item.setCount(nowPrice);
                            final CharactersItemStorage storage = CharactersItemStorage
                                    .create();
                            storage.storeItem(nowBidderId, item);
                        }
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1???????????????
                }
            }
        } else if (s1.equalsIgnoreCase("agsell")) { // ????????????
            final int houseId = Integer.valueOf(s2);
            final AuctionBoardTable boardTable = new AuctionBoardTable();
            final L1AuctionBoard board = new L1AuctionBoard();
            if (board != null) {
                // ??????????????????????????????
                board.setHouseId(houseId);
                final L1House house = HouseTable.getInstance().getHouseTable(
                        houseId);
                board.setHouseName(house.getHouseName());
                board.setHouseArea(house.getHouseArea());
                final TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
                final Calendar cal = Calendar.getInstance(tz);
                cal.add(Calendar.DATE, 5); // 5??????
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                board.setDeadline(cal);
                board.setPrice(amount);
                board.setLocation(house.getLocation());
                board.setOldOwner(pc.getName());
                board.setOldOwnerId(pc.getId());
                board.setBidder("");
                board.setBidderId(0);
                boardTable.insertAuctionBoard(board);

                house.setOnSale(true); // ????????????????????????
                house.setPurchaseBasement(true); // ?????????????????????????????????
                HouseTable.getInstance().updateHouse(house); // ?????????????????????
            }
        } else {
            // ??????NPC
            final int npcId = npc.getNpcId();
            if ((npcId == 70070) || (npcId == 70019) || (npcId == 70075)
                    || (npcId == 70012) || (npcId == 70031) || (npcId == 70084)
                    || (npcId == 70065) || (npcId == 70054) || (npcId == 70096)) {

                if (pc.getInventory().checkItem(L1ItemId.ADENA, (300 * amount))) { // ????????????
                                                                                   // =
                                                                                   // ????????????(300)
                                                                                   // *
                                                                                   // ????????????(amount)
                    final L1Inn inn = InnTable.getInstance().getTemplate(npcId,
                            pc.getInnRoomNumber());
                    if (inn != null) {
                        final Timestamp dueTime = inn.getDueTime();
                        if (dueTime != null) { // ??????????????????????????????
                            final Calendar cal = Calendar.getInstance();
                            if (((cal.getTimeInMillis() - dueTime.getTime()) / 1000) < 0) { // ??????????????????
                                // ?????????????????????...
                                pc.sendPackets(new S_NPCTalkReturn(npcId, ""));
                                return;
                            }
                        }
                        // ???????????? 4??????
                        final Timestamp ts = new Timestamp(
                                System.currentTimeMillis()
                                        + (60 * 60 * 4 * 1000));
                        // ??????????????????
                        final L1ItemInstance item = ItemTable.getInstance()
                                .createItem(40312); // ????????????
                        if (item != null) {
                            item.setKeyId(item.getId()); // ????????????
                            item.setInnNpcId(npcId); // ??????NPC
                            item.setHall(pc.checkRoomOrHall()); // ??????????????? or ?????????
                            item.setDueTime(ts); // ????????????
                            item.setCount(amount); // ????????????

                            inn.setKeyId(item.getKeyId()); // ????????????
                            inn.setLodgerId(pc.getId()); // ?????????
                            inn.setHall(pc.checkRoomOrHall()); // ??????????????? or ?????????
                            inn.setDueTime(ts); // ????????????
                            // DB??????
                            InnTable.getInstance().updateInn(inn);

                            pc.getInventory().consumeItem(L1ItemId.ADENA,
                                    (300 * amount)); // ????????????

                            // ?????????????????????????????????
                            L1Inventory inventory;
                            if (pc.getInventory().checkAddItem(item, amount) == L1Inventory.OK) {
                                inventory = pc.getInventory();
                            } else {
                                inventory = L1World.getInstance().getInventory(
                                        pc.getLocation());
                            }
                            inventory.storeItem(item);

                            if (InnKeyTable.checkey(item)) {
                                InnKeyTable.DeleteKey(item);
                                InnKeyTable.StoreKey(item);
                            } else {
                                InnKeyTable.StoreKey(item);
                            }

                            String itemName = (item.getItem().getName() + item
                                    .getInnKeyName());
                            if (amount > 1) {
                                itemName = (itemName + " (" + amount + ")");
                            }
                            pc.sendPackets(new S_ServerMessage(143, npc
                                    .getName(), itemName)); // \f1%0%s ?????? %1%o ???
                            final String[] msg = { npc.getName() };
                            pc.sendPackets(new S_NPCTalkReturn(npcId, "inn4",
                                    msg)); // ??????????????????????????????????????????????????????????????????????????????????????????
                        }
                    }
                } else {
                    final String[] msg = { npc.getName() };
                    pc.sendPackets(new S_NPCTalkReturn(npcId, "inn3", msg)); // ??????????????????????????????????????????
                }
            } else {
                final L1NpcAction action = NpcActionTable.getInstance().get(s,
                        pc, npc);
                if (action != null) {
                    final L1NpcHtml result = action.executeWithAmount(s, pc,
                            npc, amount);
                    if (result != null) {
                        pc.sendPackets(new S_NPCTalkReturn(npcId, result));
                    }
                    return;
                }
            }
        }
    }

    @Override
    public String getType() {
        return C_AMOUNT;
    }
}
