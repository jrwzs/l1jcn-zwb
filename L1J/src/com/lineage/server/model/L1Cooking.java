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

import static com.lineage.server.model.skill.L1SkillId.COOKING_1_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_1_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_1_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_2_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_3_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_3_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_1_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_1_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_2_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_3_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_3_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_1_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_1_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_2_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_3_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_3_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_WONDER_DRUG;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.identity.L1ItemId;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;

// Referenced classes of package com.lineage.server.model:
// L1Cooking

/**
 * ??????(??????)
 */
public class L1Cooking {

    public static void eatCooking(final L1PcInstance pc, final int cookingId,
            final int time) {
        int cookingType = 0;
        if ((cookingId == COOKING_1_0_N) || (cookingId == COOKING_1_0_S)) { // ??????????????????
            cookingType = 0;
            pc.addWind(10);
            pc.addWater(10);
            pc.addFire(10);
            pc.addEarth(10);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        } else if ((cookingId == COOKING_1_1_N) || (cookingId == COOKING_1_1_S)) { // ?????????
            cookingType = 1;
            pc.addMaxHp(30);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) { // ?????????
                pc.getParty().updateMiniHP(pc);
            }
        } else if ((cookingId == COOKING_1_2_N) || (cookingId == COOKING_1_2_S)) { // ??????
            cookingType = 2;
        } else if ((cookingId == COOKING_1_3_N) || (cookingId == COOKING_1_3_S)) { // ??????????????????
            cookingType = 3;
            pc.addAc(-1);
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else if ((cookingId == COOKING_1_4_N) || (cookingId == COOKING_1_4_S)) { // ????????????
            cookingType = 4;
            pc.addMaxMp(20);
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        } else if ((cookingId == COOKING_1_5_N) || (cookingId == COOKING_1_5_S)) { // ???????????????
            cookingType = 5;
        } else if ((cookingId == COOKING_1_6_N) || (cookingId == COOKING_1_6_S)) { // ???????????????
            cookingType = 6;
            pc.addMr(5);
            pc.sendPackets(new S_SPMR(pc));
        } else if ((cookingId == COOKING_1_7_N) || (cookingId == COOKING_1_7_S)) { // ?????????
            cookingType = 7;
        } else if ((cookingId == COOKING_2_0_N) || (cookingId == COOKING_2_0_S)) { // ?????????
            cookingType = 8;
        } else if ((cookingId == COOKING_2_1_N) || (cookingId == COOKING_2_1_S)) { // ????????????
            cookingType = 9;
            pc.addMaxHp(30);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) { // ?????????
                pc.getParty().updateMiniHP(pc);
            }
            pc.addMaxMp(30);
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        } else if ((cookingId == COOKING_2_2_N) || (cookingId == COOKING_2_2_S)) { // ???????????????
            cookingType = 10;
            pc.addAc(-2);
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else if ((cookingId == COOKING_2_3_N) || (cookingId == COOKING_2_3_S)) { // ???????????????
            cookingType = 11;
        } else if ((cookingId == COOKING_2_4_N) || (cookingId == COOKING_2_4_S)) { // ????????????
            cookingType = 12;
        } else if ((cookingId == COOKING_2_5_N) || (cookingId == COOKING_2_5_S)) { // ???????????????
            cookingType = 13;
            pc.addMr(10);
            pc.sendPackets(new S_SPMR(pc));
        } else if ((cookingId == COOKING_2_6_N) || (cookingId == COOKING_2_6_S)) { // ???????????????
            cookingType = 14;
            pc.addSp(1);
            pc.sendPackets(new S_SPMR(pc));
        } else if ((cookingId == COOKING_2_7_N) || (cookingId == COOKING_2_7_S)) { // ?????????
            cookingType = 15;
        } else if ((cookingId == COOKING_3_0_N) || (cookingId == COOKING_3_0_S)) { // ?????????????????????
            cookingType = 16;
        } else if ((cookingId == COOKING_3_1_N) || (cookingId == COOKING_3_1_S)) { // ???????????????
            cookingType = 17;
            pc.addMaxHp(50);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) { // ?????????
                pc.getParty().updateMiniHP(pc);
            }
            pc.addMaxMp(50);
            pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        } else if ((cookingId == COOKING_3_2_N) || (cookingId == COOKING_3_2_S)) { // ????????????????????????
            cookingType = 18;
        } else if ((cookingId == COOKING_3_3_N) || (cookingId == COOKING_3_3_S)) { // ???????????????
            cookingType = 19;
            pc.addAc(-3);
            pc.sendPackets(new S_OwnCharStatus(pc));
        } else if ((cookingId == COOKING_3_4_N) || (cookingId == COOKING_3_4_S)) { // ??????????????????
            cookingType = 20;
            pc.addMr(15);
            pc.sendPackets(new S_SPMR(pc));
            pc.addWind(10);
            pc.addWater(10);
            pc.addFire(10);
            pc.addEarth(10);
            pc.sendPackets(new S_OwnCharAttrDef(pc));
        } else if ((cookingId == COOKING_3_5_N) || (cookingId == COOKING_3_5_S)) { // ????????????
            cookingType = 21;
            pc.addSp(2);
            pc.sendPackets(new S_SPMR(pc));
        } else if ((cookingId == COOKING_3_6_N) || (cookingId == COOKING_3_6_S)) { // ???????????????
            cookingType = 22;
            pc.addMaxHp(30);
            pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
            if (pc.isInParty()) { // ?????????
                pc.getParty().updateMiniHP(pc);
            }
        } else if ((cookingId == COOKING_3_7_N) || (cookingId == COOKING_3_7_S)) { // ??????????????????
            cookingType = 23;
        } else if (cookingId == COOKING_WONDER_DRUG) { // ???????????????
            cookingType = 54;
            pc.addHpr(10);
            pc.addMpr(2);
        }
        pc.sendPackets(new S_PacketBox(53, cookingType, time));
        pc.setSkillEffect(cookingId, time * 1000);
        if (((cookingId >= COOKING_1_0_N) && (cookingId <= COOKING_1_6_N))
                || ((cookingId >= COOKING_1_0_S) && (cookingId <= COOKING_1_6_S))
                || ((cookingId >= COOKING_2_0_N) && (cookingId <= COOKING_2_6_N))
                || ((cookingId >= COOKING_2_0_S) && (cookingId <= COOKING_2_6_S))
                || ((cookingId >= COOKING_3_0_N) && (cookingId <= COOKING_3_6_N))
                || ((cookingId >= COOKING_3_0_S) && (cookingId <= COOKING_3_6_S))) {
            pc.setCookingId(cookingId);
        } else if ((cookingId == COOKING_1_7_N) || (cookingId == COOKING_1_7_S)
                || (cookingId == COOKING_2_7_N) || (cookingId == COOKING_2_7_S)
                || (cookingId == COOKING_3_7_N) || (cookingId == COOKING_3_7_S)) {
            pc.setDessertId(cookingId);
        }

        // XXX ?????????17%????????????S_PacketBox?????????????????????????????????
        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    /** ?????????????????? */
    public static void useCookingItem(final L1PcInstance pc,
            final L1ItemInstance item) {
        final int itemId = item.getItem().getItemId();
        if ((itemId == 41284) || (itemId == 41292) || (itemId == 49056)
                || (itemId == 49064) || (itemId == 49251) || (itemId == 49259)) { // ????????????????????????
            if (pc.get_food() != 225) {
                pc.sendPackets(new S_ServerMessage(74, item.getNumberedName(1))); // \f1%0%o
                                                                                  // ???????????????
                return;
            }
        }

        // ?????? LV1?????????????????? LV1????????? LV2?????????????????? LV2????????? LV3?????????????????? LV3 - ????????????
        if (((itemId >= 41277) && (itemId <= 41283))
                || ((itemId >= 41285) && (itemId <= 41291))
                || ((itemId >= 49049) && (itemId <= 49055))
                || ((itemId >= 49057) && (itemId <= 49063))
                || ((itemId >= 49244) && (itemId <= 49250))
                || ((itemId >= 49252) && (itemId <= 49258))) {
            final int cookingId = pc.getCookingId();
            if (cookingId != 0) {
                pc.removeSkillEffect(cookingId);
            }
        }

        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????? - ????????????
        if ((itemId == 41284) || (itemId == 41292) || (itemId == 49056)
                || (itemId == 49064) || (itemId == 49251) || (itemId == 49259)) {
            final int dessertId = pc.getDessertId();
            if (dessertId != 0) {
                pc.removeSkillEffect(dessertId);
            }
        }

        int cookingId;
        final int time = 900;
        if ((itemId == 41277) || (itemId == 41285)) { // ??????????????????
            if (itemId == 41277) {
                cookingId = COOKING_1_0_N;
            } else {
                cookingId = COOKING_1_0_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 41278) || (itemId == 41286)) { // ?????????
            if (itemId == 41278) {
                cookingId = COOKING_1_1_N;
            } else {
                cookingId = COOKING_1_1_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 41279) || (itemId == 41287)) { // ??????
            if (itemId == 41279) {
                cookingId = COOKING_1_2_N;
            } else {
                cookingId = COOKING_1_2_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 41280) || (itemId == 41288)) { // ??????????????????
            if (itemId == 41280) {
                cookingId = COOKING_1_3_N;
            } else {
                cookingId = COOKING_1_3_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 41281) || (itemId == 41289)) { // ????????????
            if (itemId == 41281) {
                cookingId = COOKING_1_4_N;
            } else {
                cookingId = COOKING_1_4_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 41282) || (itemId == 41290)) { // ???????????????
            if (itemId == 41282) {
                cookingId = COOKING_1_5_N;
            } else {
                cookingId = COOKING_1_5_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 41283) || (itemId == 41291)) { // ???????????????
            if (itemId == 41283) {
                cookingId = COOKING_1_6_N;
            } else {
                cookingId = COOKING_1_6_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 41284) || (itemId == 41292)) { // ?????????
            if (itemId == 41284) {
                cookingId = COOKING_1_7_N;
            } else {
                cookingId = COOKING_1_7_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49049) || (itemId == 49057)) { // ?????????
            if (itemId == 49049) {
                cookingId = COOKING_2_0_N;
            } else {
                cookingId = COOKING_2_0_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49050) || (itemId == 49058)) { // ????????????
            if (itemId == 49050) {
                cookingId = COOKING_2_1_N;
            } else {
                cookingId = COOKING_2_1_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49051) || (itemId == 49059)) { // ???????????????
            if (itemId == 49051) {
                cookingId = COOKING_2_2_N;
            } else {
                cookingId = COOKING_2_2_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49052) || (itemId == 49060)) { // ???????????????
            if (itemId == 49052) {
                cookingId = COOKING_2_3_N;
            } else {
                cookingId = COOKING_2_3_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49053) || (itemId == 49061)) { // ????????????
            if (itemId == 49053) {
                cookingId = COOKING_2_4_N;
            } else {
                cookingId = COOKING_2_4_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49054) || (itemId == 49062)) { // ???????????????
            if (itemId == 49054) {
                cookingId = COOKING_2_5_N;
            } else {
                cookingId = COOKING_2_5_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49055) || (itemId == 49063)) { // ???????????????
            if (itemId == 49055) {
                cookingId = COOKING_2_6_N;
            } else {
                cookingId = COOKING_2_6_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49056) || (itemId == 49064)) { // ?????????
            if (itemId == 49056) {
                cookingId = COOKING_2_7_N;
            } else {
                cookingId = COOKING_2_7_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49244) || (itemId == 49252)) { // ?????????????????????
            if (itemId == 49244) {
                cookingId = COOKING_3_0_N;
            } else {
                cookingId = COOKING_3_0_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49245) || (itemId == 49253)) { // ???????????????
            if (itemId == 49245) {
                cookingId = COOKING_3_1_N;
            } else {
                cookingId = COOKING_3_1_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49246) || (itemId == 49254)) { // ????????????????????????
            if (itemId == 49246) {
                cookingId = COOKING_3_2_N;
            } else {
                cookingId = COOKING_3_2_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49247) || (itemId == 49255)) { // ???????????????
            if (itemId == 49247) {
                cookingId = COOKING_3_3_N;
            } else {
                cookingId = COOKING_3_3_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49248) || (itemId == 49256)) { // ??????????????????
            if (itemId == 49248) {
                cookingId = COOKING_3_4_N;
            } else {
                cookingId = COOKING_3_4_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49249) || (itemId == 49257)) { // ????????????
            if (itemId == 49249) {
                cookingId = COOKING_3_5_N;
            } else {
                cookingId = COOKING_3_5_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49250) || (itemId == 49258)) { // ???????????????
            if (itemId == 49250) {
                cookingId = COOKING_3_6_N;
            } else {
                cookingId = COOKING_3_6_S;
            }
            eatCooking(pc, cookingId, time);
        } else if ((itemId == 49251) || (itemId == 49259)) { // ??????????????????
            if (itemId == 49251) {
                cookingId = COOKING_3_7_N;
            } else {
                cookingId = COOKING_3_7_S;
            }
            eatCooking(pc, cookingId, time);
        } else if (itemId == L1ItemId.POTION_OF_WONDER_DRUG) { // ???????????????
            cookingId = COOKING_WONDER_DRUG;
            eatCooking(pc, cookingId, time);
        }
        pc.sendPackets(new S_ServerMessage(76, item.getNumberedName(1))); // \f1???
                                                                          // %0%o???
        pc.getInventory().removeItem(item, 1);
    }

    private L1Cooking() {
    }

}
