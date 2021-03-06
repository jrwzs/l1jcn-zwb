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
package com.lineage.server.utils;

import static com.lineage.server.model.skill.L1SkillId.COOKING_1_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_7_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_7_S;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_POTION_OF_BATTLE;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_POTION_OF_EXP_150;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_POTION_OF_EXP_175;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_POTION_OF_EXP_200;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_POTION_OF_EXP_225;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_POTION_OF_EXP_250;

import java.util.List;
import java.util.logging.Logger;

import com.lineage.Config;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.PetTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1World;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_PetPack;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1MagicDoll;
import com.lineage.server.templates.L1Pet;

// Referenced classes of package com.lineage.server.utils:
// CalcStat

/**
 * ????????????????????????
 */
public class CalcExp {

    /**
     * ????????????UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * ?????????
     */
    private static Logger _log = Logger.getLogger(CalcExp.class.getName());

    /**
     * ???????????????
     */
    public static final long MAX_EXP = ExpTable.getExpByLevel(111) - 1;

    /**
     * ???????????????
     * 
     * @param pc
     *            ??????
     * @param exp
     *            ?????????
     * @param lawful
     *            ?????????
     */
    private static void AddExp(final L1PcInstance pc, final long exp,
            final int lawful) {

        final int add_lawful = (int) (lawful * Config.RATE_LA) * -1; // ???????????????????????????
        pc.addLawful(add_lawful); // ???PC???????????????
        final double exppenalty = ExpTable.getPenaltyRate(pc.getLevel()); // ?????????????????????????????????
        double foodBonus = 1.0; // ????????????????????????
        double expBonus = 1.0; // ????????????????????????
        final double expArmor = getExpByArmor(pc); // ??????????????????
        final double dollExp = L1MagicDoll.getExpByDoll(pc); // ????????????????????????
        // ????????????????????????
        if (pc.hasSkillEffect(COOKING_1_7_N)
                || pc.hasSkillEffect(COOKING_1_7_S)) {
            foodBonus = 1.01;
        }
        if (pc.hasSkillEffect(COOKING_2_7_N)
                || pc.hasSkillEffect(COOKING_2_7_S)) {
            foodBonus = 1.02;
        }
        if (pc.hasSkillEffect(COOKING_3_7_N)
                || pc.hasSkillEffect(COOKING_3_7_S)) {
            foodBonus = 1.03;
        }

        // ???????????????????????????????????????
        if (pc.hasSkillEffect(EFFECT_POTION_OF_BATTLE)) {
            expBonus = 1.2;
        }
        if (pc.hasSkillEffect(EFFECT_POTION_OF_EXP_150)) {
            expBonus = 2.5;
        }
        if (pc.hasSkillEffect(EFFECT_POTION_OF_EXP_175)) {
            expBonus = 2.75;
        }
        if (pc.hasSkillEffect(EFFECT_POTION_OF_EXP_200)) {
            expBonus = 3.0;
        }
        if (pc.hasSkillEffect(EFFECT_POTION_OF_EXP_225)) {
            expBonus = 3.25;
        }
        if (pc.hasSkillEffect(EFFECT_POTION_OF_EXP_250)) {
            expBonus = 3.5;
        }

        final long add_exp = (long) (exp // ???????????????
                * exppenalty // ?????????????????????????????????
                * Config.RATE_XP // ???????????????
                * foodBonus // ????????????????????????
                * expBonus // ????????????????????????
                * expArmor // ??????????????????
        * dollExp // ???????????????????????????
        );
        pc.addExp(add_exp); // ???PC???????????????
    }

    /**
     * ??????????????????
     * 
     * @param pet
     *            ??????
     * @param exp
     *            ?????????
     */
    private static void AddExpPet(final L1PetInstance pet, final long exp) {

        // ????????????
        final L1PcInstance pc = (L1PcInstance) pet.getMaster();

        // ????????????
        final int petItemObjId = pet.getItemObjId();

        // ????????????
        final int levelBefore = pet.getLevel();

        // ???????????????
        long totalExp = (long) (exp * Config.RATE_XP + pet.getExp());

        // ??????????????????
        final int maxLevel = 51;
        if (totalExp >= ExpTable.getExpByLevel(maxLevel)) {
            totalExp = ExpTable.getExpByLevel(maxLevel) - 1;
        }

        // ?????????????????????
        pet.setExp(totalExp);

        // ??????????????????
        pet.setLevel(ExpTable.getLevelByExp(totalExp));

        final int expPercentage = ExpTable.getExpPercentage(pet.getLevel(),
                totalExp);

        final int gap = pet.getLevel() - levelBefore;
        for (int i = 1; i <= gap; i++) {
            final IntRange hpUpRange = pet.getPetType().getHpUpRange();
            final IntRange mpUpRange = pet.getPetType().getMpUpRange();
            pet.addMaxHp(hpUpRange.randomValue());
            pet.addMaxMp(mpUpRange.randomValue());
        }

        pet.setExpPercent(expPercentage);
        pc.sendPackets(new S_PetPack(pet, pc));

        if (gap != 0) { // ??????????????????????????????????????????
            final L1Pet petTemplate = PetTable.getInstance().getTemplate(
                    petItemObjId);
            if (petTemplate == null) { // PetTable???
                _log.warning("L1Pet == null");
                return;
            }
            petTemplate.set_exp((int) pet.getExp()); // ?????????
            petTemplate.set_level(pet.getLevel()); // ??????
            petTemplate.set_hp(pet.getMaxHp()); // HP
            petTemplate.set_mp(pet.getMaxMp()); // MP
            PetTable.getInstance().storePet(petTemplate); // ????????????
            pc.sendPackets(new S_ServerMessage(320, pet.getName())); // \f1%0????????????
        }
    }

    /**
     * ????????????????????????
     * 
     * @param l1pcinstance
     *            PC
     * @param targetid
     *            ??????ID
     * @param acquisitorList
     *            ????????????
     * @param hateList
     * @param exp
     *            ?????????
     */
    public static void calcExp(final L1PcInstance l1pcinstance,
            final int targetid, final List<L1Character> acquisitorList,
            final List<Integer> hateList, final long exp) {

        int i = 0;
        double party_level = 0;
        double dist = 0;
        long member_exp = 0;
        int member_lawful = 0;
        final L1Object l1object = L1World.getInstance().findObject(targetid);
        final L1NpcInstance npc = (L1NpcInstance) l1object;

        // ??????????????? hate
        L1Character acquisitor;
        int hate = 0;
        long acquire_exp = 0;
        int acquire_lawful = 0;
        long party_exp = 0;
        int party_lawful = 0;
        long totalHateExp = 0;
        int totalHateLawful = 0;
        long partyHateExp = 0;
        int partyHateLawful = 0;
        long ownHateExp = 0;

        if (acquisitorList.size() != hateList.size()) {
            return;
        }
        for (i = hateList.size() - 1; i >= 0; i--) {
            acquisitor = acquisitorList.get(i);
            hate = hateList.get(i);
            if ((acquisitor != null) && !acquisitor.isDead()) {
                totalHateExp += hate;
                if (acquisitor instanceof L1PcInstance) {
                    totalHateLawful += hate;
                }
            } else { // ?????? null????????? ??? ??????
                acquisitorList.remove(i);
                hateList.remove(i);
            }
        }
        if (totalHateExp == 0) { // ??????????????????????????????
            return;
        }

        if ((l1object != null) && !(npc instanceof L1PetInstance)
                && !(npc instanceof L1SummonInstance)) {
            // int exp = npc.get_exp();
            /*
             * if (!L1World.getInstance().isProcessingContributionTotal() &&
             * (l1pcinstance.getHomeTownId() > 0)) { int contribution =
             * npc.getLevel() / 10; l1pcinstance.addContribution(contribution);
             * }
             */// ????????????????????????????????????????????????????????????????????????????????? for 3.3C
            final int lawful = npc.getLawful();

            if (l1pcinstance.isInParty()) { // ?????????
                // ??????????????? hate
                // ???????????????????????????
                partyHateExp = 0;
                partyHateLawful = 0;
                for (i = hateList.size() - 1; i >= 0; i--) {
                    acquisitor = acquisitorList.get(i);
                    hate = hateList.get(i);

                    // PC
                    if (acquisitor instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) acquisitor;
                        if (pc == l1pcinstance) {
                            partyHateExp += hate;
                            partyHateLawful += hate;
                        } else if (l1pcinstance.getParty().isMember(pc)) {
                            partyHateExp += hate;
                            partyHateLawful += hate;
                        } else {
                            if (totalHateExp > 0) {
                                acquire_exp = (exp * hate / totalHateExp);
                            }
                            if (totalHateLawful > 0) {
                                acquire_lawful = (lawful * hate / totalHateLawful);
                            }
                            AddExp(pc, acquire_exp, acquire_lawful);
                        }
                    }

                    // ??????
                    else if (acquisitor instanceof L1PetInstance) {
                        final L1PetInstance pet = (L1PetInstance) acquisitor;
                        final L1PcInstance master = (L1PcInstance) pet
                                .getMaster();
                        if (master == l1pcinstance) {
                            partyHateExp += hate;
                        } else if (l1pcinstance.getParty().isMember(master)) {
                            partyHateExp += hate;
                        } else {
                            if (totalHateExp > 0) {
                                acquire_exp = (exp * hate / totalHateExp);
                            }
                            AddExpPet(pet, acquire_exp);
                        }
                    }

                    // ?????????
                    else if (acquisitor instanceof L1SummonInstance) {
                        final L1SummonInstance summon = (L1SummonInstance) acquisitor;
                        final L1PcInstance master = (L1PcInstance) summon
                                .getMaster();
                        if (master == l1pcinstance) {
                            partyHateExp += hate;
                        } else if (l1pcinstance.getParty().isMember(master)) {
                            partyHateExp += hate;
                        } else {
                        }
                    }
                }
                if (totalHateExp > 0) {
                    party_exp = (exp * partyHateExp / totalHateExp);
                }
                if (totalHateLawful > 0) {
                    party_lawful = (lawful * partyHateLawful / totalHateLawful);
                }

                // EXP?????????????????????

                // ?????????
                double pri_bonus = 0;
                final L1PcInstance leader = l1pcinstance.getParty().getLeader();
                if (leader.isCrown()
                        && (l1pcinstance.knownsObject(leader) || l1pcinstance
                                .equals(leader))) {
                    pri_bonus = 0.059;
                }

                // PT??????????????????
                final L1PcInstance[] ptMembers = l1pcinstance.getParty()
                        .getMembers();
                double pt_bonus = 0;
                for (final L1PcInstance each : ptMembers) {
                    if (l1pcinstance.knownsObject(each)
                            || l1pcinstance.equals(each)) {
                        party_level += each.getLevel() * each.getLevel();
                    }
                    if (l1pcinstance.knownsObject(each)) {
                        pt_bonus += 0.04;
                    }
                }

                party_exp = (long) (party_exp * (1 + pt_bonus + pri_bonus));

                // ????????????????????????????????? Hate
                if (party_level > 0) {
                    dist = ((l1pcinstance.getLevel() * l1pcinstance.getLevel()) / party_level);
                }
                member_exp = (long) (party_exp * dist);
                member_lawful = (int) (party_lawful * dist);

                ownHateExp = 0;
                for (i = hateList.size() - 1; i >= 0; i--) {
                    acquisitor = acquisitorList.get(i);
                    hate = hateList.get(i);
                    if (acquisitor instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) acquisitor;
                        if (pc == l1pcinstance) {
                            ownHateExp += hate;
                        }
                    } else if (acquisitor instanceof L1PetInstance) {
                        final L1PetInstance pet = (L1PetInstance) acquisitor;
                        final L1PcInstance master = (L1PcInstance) pet
                                .getMaster();
                        if (master == l1pcinstance) {
                            ownHateExp += hate;
                        }
                    } else if (acquisitor instanceof L1SummonInstance) {
                        final L1SummonInstance summon = (L1SummonInstance) acquisitor;
                        final L1PcInstance master = (L1PcInstance) summon
                                .getMaster();
                        if (master == l1pcinstance) {
                            ownHateExp += hate;
                        }
                    }
                }
                // ??????????????????????????????
                if (ownHateExp != 0) { // ???????????????
                    for (i = hateList.size() - 1; i >= 0; i--) {
                        acquisitor = acquisitorList.get(i);
                        hate = hateList.get(i);
                        if (acquisitor instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) acquisitor;
                            if (pc == l1pcinstance) {
                                if (ownHateExp > 0) {
                                    acquire_exp = (member_exp * hate / ownHateExp);
                                }
                                AddExp(pc, acquire_exp, member_lawful);
                            }
                        } else if (acquisitor instanceof L1PetInstance) {
                            final L1PetInstance pet = (L1PetInstance) acquisitor;
                            final L1PcInstance master = (L1PcInstance) pet
                                    .getMaster();
                            if (master == l1pcinstance) {
                                if (ownHateExp > 0) {
                                    acquire_exp = (member_exp * hate / ownHateExp);
                                }
                                AddExpPet(pet, acquire_exp);
                            }
                        } else if (acquisitor instanceof L1SummonInstance) {
                        }
                    }
                } else { // ??????????????????
                         // ??????????????????
                    AddExp(l1pcinstance, member_exp, member_lawful);
                }

                // ????????????????????????????????? Hate ??????
                for (final L1PcInstance ptMember : ptMembers) {
                    if (l1pcinstance.knownsObject(ptMember)) {
                        if (party_level > 0) {
                            dist = ((ptMember.getLevel() * ptMember.getLevel()) / party_level);
                        }
                        member_exp = (int) (party_exp * dist);
                        member_lawful = (int) (party_lawful * dist);

                        ownHateExp = 0;
                        for (i = hateList.size() - 1; i >= 0; i--) {
                            acquisitor = acquisitorList.get(i);
                            hate = hateList.get(i);
                            if (acquisitor instanceof L1PcInstance) {
                                final L1PcInstance pc = (L1PcInstance) acquisitor;
                                if (pc == ptMember) {
                                    ownHateExp += hate;
                                }
                            } else if (acquisitor instanceof L1PetInstance) {
                                final L1PetInstance pet = (L1PetInstance) acquisitor;
                                final L1PcInstance master = (L1PcInstance) pet
                                        .getMaster();
                                if (master == ptMember) {
                                    ownHateExp += hate;
                                }
                            } else if (acquisitor instanceof L1SummonInstance) {
                                final L1SummonInstance summon = (L1SummonInstance) acquisitor;
                                final L1PcInstance master = (L1PcInstance) summon
                                        .getMaster();
                                if (master == ptMember) {
                                    ownHateExp += hate;
                                }
                            }
                        }
                        // ????????????????????????????????? Hate
                        if (ownHateExp != 0) { // ???????????????
                            for (i = hateList.size() - 1; i >= 0; i--) {
                                acquisitor = acquisitorList.get(i);
                                hate = hateList.get(i);
                                if (acquisitor instanceof L1PcInstance) {
                                    final L1PcInstance pc = (L1PcInstance) acquisitor;
                                    if (pc == ptMember) {
                                        if (ownHateExp > 0) {
                                            acquire_exp = (member_exp * hate / ownHateExp);
                                        }
                                        AddExp(pc, acquire_exp, member_lawful);
                                    }
                                } else if (acquisitor instanceof L1PetInstance) {
                                    final L1PetInstance pet = (L1PetInstance) acquisitor;
                                    final L1PcInstance master = (L1PcInstance) pet
                                            .getMaster();
                                    if (master == ptMember) {
                                        if (ownHateExp > 0) {
                                            acquire_exp = (member_exp * hate / ownHateExp);
                                        }
                                        AddExpPet(pet, acquire_exp);
                                    }
                                } else if (acquisitor instanceof L1SummonInstance) {
                                }
                            }
                        } else { // ??????????????????
                                 // ???????????????????????????
                            AddExp(ptMember, member_exp, member_lawful);
                        }
                    }
                }
            } else { // ????????????????????????????????????
                     // EXP????????????????????????
                for (i = hateList.size() - 1; i >= 0; i--) {
                    acquisitor = acquisitorList.get(i);
                    hate = hateList.get(i);
                    acquire_exp = (exp * hate / totalHateExp);
                    if (acquisitor instanceof L1PcInstance) {
                        if (totalHateLawful > 0) {
                            acquire_lawful = (lawful * hate / totalHateLawful);
                        }
                    }

                    if (acquisitor instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) acquisitor;
                        AddExp(pc, acquire_exp, acquire_lawful);
                    } else if (acquisitor instanceof L1PetInstance) {
                        final L1PetInstance pet = (L1PetInstance) acquisitor;
                        AddExpPet(pet, acquire_exp);
                    } else if (acquisitor instanceof L1SummonInstance) {
                    }
                }
            }
        }
    }

    /**
     * ?????????????????????(??????).
     * 
     * @param pc
     *            ??????
     * @return ???????????????
     */
    private static double getExpByArmor(final L1PcInstance pc) {
        double ss = 1.0;
        final double s = pc.getExpBonus();
        if (s > 0.0) {
            ss = pc.getExpBonus();
        }
        return ss;
    }

    /**
     * ??????????????????UID
     * 
     * @return
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    private CalcExp() {
    }
}
