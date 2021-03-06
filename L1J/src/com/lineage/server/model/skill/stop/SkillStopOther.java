package com.lineage.server.model.skill.stop;

import static com.lineage.server.model.skill.L1SkillId.COOKING_WONDER_DRUG;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_BLESS_OF_CRAY;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_BLESS_OF_MAZU;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_BLESS_OF_SAELL;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_BLOODSTAIN_OF_ANTHARAS;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_BLOODSTAIN_OF_FAFURION;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_ENCHANTING_BATTLE;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_EYE_OF_AHTHARTS;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_EYE_OF_BIRTH;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_EYE_OF_FAFURION;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_EYE_OF_FIGURE;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_EYE_OF_LIFE;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_EYE_OF_LINDVIOR;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_EYE_OF_VALAKAS;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_A_1;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_A_2;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_A_3;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_A_4;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_A_5;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_A_6;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_A_7;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_A_8;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_A_9;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_B_1;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_B_2;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_B_3;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_B_4;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_B_5;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_B_6;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_B_7;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_B_8;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_B_9;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_C_1;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_C_2;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_C_3;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_C_4;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_C_5;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_C_6;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_C_7;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_C_8;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_C_9;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_D_1;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_D_2;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_D_3;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_D_4;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_D_5;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_D_6;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_D_7;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_D_8;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_MAGIC_STONE_D_9;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_STRENGTHENING_HP;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_STRENGTHENING_MP;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE_BASILISK;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE_COCKATRICE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_BARLOG;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_YAHEE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_FREEZE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillIconAura;
import com.lineage.server.serverpackets.S_SkillIconBloodstain;

/**
 * ????????????:??????
 * 
 * @author jrwz
 */
public class SkillStopOther implements L1SkillStop {

    @Override
    public void stopSkill(final L1Character cha, final int skillId) {

        switch (skillId) {

            case EFFECT_MAGIC_STONE_A_1: // ?????????1???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-10);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_A_2: // ?????????2???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-20);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_A_3: // ?????????3???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-30);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_A_4: // ?????????4???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-40);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_A_5: // ?????????5???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-50);
                    pc.addHpr(-1);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_A_6: // ?????????6???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-60);
                    pc.addHpr(-2);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_A_7: // ?????????7???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-70);
                    pc.addHpr(-3);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_A_8: // ?????????8???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-80);
                    pc.addHpr(-4);
                    pc.addHitup(-1);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_A_9: // ?????????9???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-100);
                    pc.addHpr(-5);
                    pc.addHitup(-2);
                    pc.addDmgup(-2);
                    pc.addStr((byte) -1);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_B_1: // ?????????1???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-5);
                    pc.addMaxMp(-3);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_B_2: // ?????????2???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-10);
                    pc.addMaxMp(-6);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_B_3: // ?????????3???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-15);
                    pc.addMaxMp(-10);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_B_4: // ?????????4???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-20);
                    pc.addMaxMp(-15);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_B_5: // ?????????5???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-25);
                    pc.addMaxMp(-20);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_B_6: // ?????????6???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-30);
                    pc.addMaxMp(-20);
                    pc.addHpr(-1);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_B_7: // ?????????7???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-35);
                    pc.addMaxMp(-20);
                    pc.addHpr(-1);
                    pc.addMpr(-1);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_B_8: // ?????????8???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-40);
                    pc.addMaxMp(-25);
                    pc.addHpr(-2);
                    pc.addMpr(-1);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_B_9: // ?????????9???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-50);
                    pc.addMaxMp(-30);
                    pc.addHpr(-2);
                    pc.addMpr(-2);
                    pc.addBowDmgup(-2);
                    pc.addBowHitup(-2);
                    pc.addDex((byte) -1);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_C_1: // ?????????1???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-5);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_C_2: // ?????????2???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-10);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_C_3: // ?????????3???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-15);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_C_4: // ?????????4???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-20);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_C_5: // ?????????5???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-25);
                    pc.addMpr(-1);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_C_6: // ?????????6???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-30);
                    pc.addMpr(-2);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_C_7: // ?????????7???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-35);
                    pc.addMpr(-3);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_C_8: // ?????????8???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-40);
                    pc.addMpr(-4);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_C_9: // ?????????9???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-50);
                    pc.addMpr(-5);
                    pc.addInt((byte) -1);
                    pc.addSp(-1);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_D_1: // ?????????1???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(-2);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_D_2: // ?????????2???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(-4);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_D_3: // ?????????3???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(-6);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_D_4: // ?????????4???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(-8);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_D_5: // ?????????5???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(-10);
                    pc.addAc(1);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_D_6: // ?????????6???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(-10);
                    pc.addAc(2);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_D_7: // ?????????7???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(-10);
                    pc.addAc(3);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_D_8: // ?????????8???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(-15);
                    pc.addAc(4);
                    pc.addDamageReductionByArmor(-1);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_STONE_D_9: // ?????????9???(??????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMr(-20);
                    pc.addAc(5);
                    pc.addCon((byte) -1);
                    pc.addDamageReductionByArmor(-3);
                    pc.sendPackets(new S_SPMR(pc));
                    pc.setMagicStoneLevel((byte) 0);
                }
                break;

            case EFFECT_MAGIC_EYE_OF_AHTHARTS: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addRegistStone(-3); // ????????????
                    pc.addDodge((byte) -1); // ????????? - 10%
                    // ?????????????????????
                    pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
                }
                break;

            case EFFECT_MAGIC_EYE_OF_FAFURION: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.add_regist_freeze(-3); // ????????????
                    // ??????????????????
                }
                break;

            case EFFECT_MAGIC_EYE_OF_LINDVIOR: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addRegistSleep(-3); // ????????????
                    // ???????????????
                }
                break;

            case EFFECT_MAGIC_EYE_OF_VALAKAS: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addRegistStun(-3); // ????????????
                    pc.addDmgup(-2); // ??????????????????
                }
                break;

            case EFFECT_MAGIC_EYE_OF_BIRTH: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addRegistBlind(-3); // ????????????
                    // ??????????????????
                    pc.addDodge((byte) -1); // ????????? - 10%
                    // ?????????????????????
                    pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
                }
                break;

            case EFFECT_MAGIC_EYE_OF_FIGURE: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addRegistSustain(-3); // ????????????
                    // ??????????????????
                    // ???????????????
                    pc.addDodge((byte) -1); // ????????? - 10%
                    // ?????????????????????
                    pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
                }
                break;

            case EFFECT_MAGIC_EYE_OF_LIFE: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addDmgup(2); // ??????????????????
                    // ??????????????????
                    // ???????????????
                    // ??????????????????
                    pc.addDodge((byte) -1); // ????????? - 10%
                    // ?????????????????????
                    pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
                }
                break;

            case EFFECT_BLESS_OF_CRAY: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-100);
                    pc.addMaxMp(-50);
                    pc.addHpr(-3);
                    pc.addMpr(-3);
                    pc.addEarth(-30);
                    pc.addDmgup(-1);
                    pc.addHitup(-5);
                    pc.addWeightReduction(-40);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                }
                break;

            case EFFECT_BLESS_OF_SAELL: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-80);
                    pc.addMaxMp(-10);
                    pc.addWater(-30);
                    pc.addAc(8);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                }
                break;

            case STATUS_CURSE_YAHEE: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillIconAura(221, 0, 1));
                }
                break;

            case STATUS_CURSE_BARLOG: // ?????????????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillIconAura(221, 0, 2));
                }
                break;

            case ICE_LANCE_COCKATRICE: // ?????????????????????
            case ICE_LANCE_BASILISK: // ????????????????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Poison(pc.getId(), 0));
                    pc.broadcastPacket(new S_Poison(pc.getId(), 0));
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE,
                            false));
                } else if ((cha instanceof L1MonsterInstance)
                        || (cha instanceof L1SummonInstance)
                        || (cha instanceof L1PetInstance)) {
                    final L1NpcInstance npc = (L1NpcInstance) cha;
                    npc.broadcastPacket(new S_Poison(npc.getId(), 0));
                    npc.setParalyzed(false);
                }
                break;

            case STATUS_FREEZE: // ?????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
                } else if ((cha instanceof L1MonsterInstance)
                        || (cha instanceof L1SummonInstance)
                        || (cha instanceof L1PetInstance)) {
                    final L1NpcInstance npc = (L1NpcInstance) cha;
                    npc.setParalyzed(false);
                }
                break;

            case STATUS_CHAT_PROHIBITED: // ??????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_ServerMessage(288)); // ?????????????????????????????????
                }
                break;

            // ****** ?????????
            case STATUS_POISON: // ?????????
                cha.curePoison();
                break;

            case COOKING_WONDER_DRUG: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHpr(-10);
                    pc.addMpr(-2);
                }
                break;

            case EFFECT_BLESS_OF_MAZU: // ???????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHitup(-3);
                    pc.addDmgup(-3);
                    pc.addMpr(-2);
                }
                break;

            case EFFECT_STRENGTHENING_HP: // ??????????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-50);
                    pc.addHpr(-4);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                }
                break;

            case EFFECT_STRENGTHENING_MP: // ??????????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxMp(-40);
                    pc.addMpr(-4);
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                }
                break;

            case EFFECT_ENCHANTING_BATTLE: // ??????????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addHitup(-3);
                    pc.addDmgup(-3);
                    pc.addBowHitup(-3);
                    pc.addBowDmgup(-3);
                    pc.addSp(-3);
                    pc.sendPackets(new S_SPMR(pc));
                }
                break;

            case EFFECT_BLOODSTAIN_OF_ANTHARAS: // ?????????????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addAc(2);
                    pc.addWater(-50);
                    pc.sendPackets(new S_SkillIconBloodstain(82, 0));
                }
                break;

            case EFFECT_BLOODSTAIN_OF_FAFURION: // ??????????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addWind(-50);
                    pc.sendPackets(new S_SkillIconBloodstain(85, 0));
                }
                break;
        }
    }

}
