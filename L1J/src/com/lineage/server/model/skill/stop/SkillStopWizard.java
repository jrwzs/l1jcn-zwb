package com.lineage.server.model.skill.stop;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
import static com.lineage.server.model.skill.L1SkillId.BLESSED_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.BLESS_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.CURSE_BLIND;
import static com.lineage.server.model.skill.L1SkillId.CURSE_PARALYZE;
import static com.lineage.server.model.skill.L1SkillId.DARKNESS;
import static com.lineage.server.model.skill.L1SkillId.DISEASE;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BLIZZARD;
import static com.lineage.server.model.skill.L1SkillId.GREATER_HASTE;
import static com.lineage.server.model.skill.L1SkillId.HASTE;
import static com.lineage.server.model.skill.L1SkillId.HOLY_WALK;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.LIGHT;
import static com.lineage.server.model.skill.L1SkillId.MASS_SLOW;
import static com.lineage.server.model.skill.L1SkillId.MEDITATION;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static com.lineage.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static com.lineage.server.model.skill.L1SkillId.SHAPE_CHANGE;
import static com.lineage.server.model.skill.L1SkillId.SHIELD;
import static com.lineage.server.model.skill.L1SkillId.SLOW;
import static com.lineage.server.model.skill.L1SkillId.WEAKNESS;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_CurseBlind;
import com.lineage.server.serverpackets.S_Dexup;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillIconShield;
import com.lineage.server.serverpackets.S_Strup;

/**
 * ????????????:??????
 * 
 * @author jrwz
 */
public class SkillStopWizard implements L1SkillStop {

    @Override
    public void stopSkill(final L1Character cha, final int skillId) {

        switch (skillId) {
            case LIGHT: // ???????????? (?????????)
                if (cha instanceof L1PcInstance) {
                    if (!cha.isInvisble()) {
                        final L1PcInstance pc = (L1PcInstance) cha;
                        pc.turnOnOffLight();
                    }
                }
                break;

            case SHIELD: // ???????????? (?????????)
                cha.addAc(2);
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillIconShield(5, 0));
                }
                break;

            case ENCHANT_WEAPON: // ???????????? (??????????????????)
                cha.addDmgup(-2);
                break;

            case CURSE_BLIND: // ???????????? (????????????)
            case DARKNESS: // ???????????? (????????????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_CurseBlind(0));
                }
                break;

            case BLESSED_ARMOR: // ???????????? (????????????)
                cha.addAc(3);
                break;

            case PHYSICAL_ENCHANT_DEX: // ???????????? (???????????????)???DEX
                cha.addDex((byte) -5);
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Dexup(pc, 5, 0));
                }
                break;

            case SLOW: // ???????????? (??????)
            case MASS_SLOW: // ???????????? (???????????????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                    pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
                }
                cha.setMoveSpeed(0);
                break;

            case MEDITATION: // ???????????? (?????????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMpr(-5);
                }
                break;

            case CURSE_PARALYZE: // ???????????? (??????????????????
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Poison(pc.getId(), 0));
                    pc.broadcastPacket(new S_Poison(pc.getId(), 0));
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS,
                            false));
                }
                break;

            case PHYSICAL_ENCHANT_STR: // ???????????? (???????????????)???STR
                cha.addStr((byte) -5);
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Strup(pc, 5, 0));
                }
                break;

            case HASTE: // ???????????? (?????????)(???????????????)
            case GREATER_HASTE:
                cha.setMoveSpeed(0);
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
                    pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
                }
                break;

            case WEAKNESS: // ???????????? (?????????)
                cha.addDmgup(5);
                cha.addHitup(1);
                break;

            case BLESS_WEAPON: // ???????????? (??????????????????)
                cha.addDmgup(-2);
                cha.addHitup(-2);
                cha.addBowHitup(-2);
                break;

            case ICE_LANCE: // ???????????? (????????????)
            case FREEZING_BLIZZARD: // ???????????? (????????????)
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

            case HOLY_WALK: // ????????????
                cha.setBraveSpeed(0);
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                    pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
                }
                break;

            case BERSERKERS: // ???????????? (?????????)
                cha.addAc(-10);
                cha.addDmgup(-5);
                cha.addHitup(-2);
                break;

            case DISEASE: // ???????????? (?????????)
                cha.addDmgup(6);
                cha.addAc(-12);
                break;

            case FOG_OF_SLEEPING: // ???????????? (????????????)
                cha.setSleeped(false);
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP,
                            false));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                }
                break;

            case SHAPE_CHANGE: // ???????????? (?????????)
                L1PolyMorph.undoPoly(cha);
                break;

            case ABSOLUTE_BARRIER: // ???????????? (????????????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.startHpRegeneration();
                    pc.startMpRegeneration();
                    pc.startHpRegenerationByDoll();
                    pc.startMpRegenerationByDoll();
                }
                break;

            case ADVANCE_SPIRIT: // ???????????? (????????????)
                if (cha instanceof L1PcInstance) {
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.addMaxHp(-pc.getAdvenHp());
                    pc.addMaxMp(-pc.getAdvenMp());
                    pc.setAdvenHp(0);
                    pc.setAdvenMp(0);
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
                            .getMaxHp()));
                    if (pc.isInParty()) { // ?????????
                        pc.getParty().updateMiniHP(pc);
                    }
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
                            .getMaxMp()));
                }
                break;
        }
    }

}
