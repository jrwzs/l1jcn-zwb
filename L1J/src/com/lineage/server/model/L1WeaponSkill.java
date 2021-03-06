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
import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BLIZZARD;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BREATH;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.ILLUSION_AVATAR;
import static com.lineage.server.model.skill.L1SkillId.STATUS_FREEZE;

import com.lineage.server.ActionCodes;
import com.lineage.server.WarTimeController;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.WeaponSkillTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.Random;

// Referenced classes of package com.lineage.server.model:
// L1PcInstance

/**
 * ????????????
 */
public class L1WeaponSkill {

    /** ?????????????????? */
    public static double calcDamageReduction(final L1PcInstance pc,
            final L1Character cha, double dmg, final int attr) {

        // ????????????or???????????????
        if (isFreeze(cha)) {
            return 0;
        }

        // MR????????????
        final int mr = cha.getMr();
        double mrFloor = 0;
        if (mr <= 100) {
            mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 2);
        } else if (mr >= 100) {
            mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 10);
        }
        double mrCoefficient = 0;
        if (mr <= 100) {
            mrCoefficient = 1 - 0.01 * mrFloor;
        } else if (mr >= 100) {
            mrCoefficient = 0.6 - 0.01 * mrFloor;
        }
        dmg *= mrCoefficient;

        // ??????????????????
        int resist = 0;
        if (attr == L1Skills.ATTR_EARTH) {
            resist = cha.getEarth();
        } else if (attr == L1Skills.ATTR_FIRE) {
            resist = cha.getFire();
        } else if (attr == L1Skills.ATTR_WATER) {
            resist = cha.getWater();
        } else if (attr == L1Skills.ATTR_WIND) {
            resist = cha.getWind();
        }
        int resistFloor = (int) (0.32 * Math.abs(resist));
        if (resist >= 0) {
            resistFloor *= 1;
        } else {
            resistFloor *= -1;
        }
        final double attrDeffence = resistFloor / 32.0;
        dmg = (1.0 - attrDeffence) * dmg;

        return dmg;
    }

    /** ?????????????????????????????? */
    public static double getAreaSkillWeaponDamage(final L1PcInstance pc,
            final L1Character cha, final int weaponId) {
        double dmg = 0;
        int probability = 0;
        int attr = 0;
        final int chance = Random.nextInt(100) + 1;
        if ((weaponId == 263) || (weaponId == 287)) { // ????????????????????????
            probability = 5;
            attr = L1Skills.ATTR_WATER;
        } else if (weaponId == 260) { // ????????????
            probability = 4;
            attr = L1Skills.ATTR_WIND;
        }
        if (probability >= chance) {
            final int sp = pc.getSp();
            final int intel = pc.getInt();
            int area = 0;
            int effectTargetId = 0;
            int effectId = 0;
            L1Character areaBase = cha;
            double damageRate = 0;

            if ((weaponId == 263) || (weaponId == 290)) { // ??????????????????????????????
                area = 3;
                damageRate = 1.4D;
                effectTargetId = cha.getId();
                effectId = 1804;
                areaBase = cha;
            } else if (weaponId == 260) { // ????????????
                area = 4;
                damageRate = 1.5D;
                effectTargetId = pc.getId();
                effectId = 758;
                areaBase = pc;
            }
            double bsk = 0;
            if (pc.hasSkillEffect(BERSERKERS)) {
                bsk = 0.2;
            }
            dmg = (intel + sp) * (damageRate + bsk)
                    + Random.nextInt(intel + sp) * damageRate;
            pc.sendPackets(new S_SkillSound(effectTargetId, effectId));
            pc.broadcastPacket(new S_SkillSound(effectTargetId, effectId));

            for (final L1Object object : L1World.getInstance()
                    .getVisibleObjects(areaBase, area)) {
                if (object == null) {
                    continue;
                }
                if (!(object instanceof L1Character)) {
                    continue;
                }
                if (object.getId() == pc.getId()) {
                    continue;
                }
                if (object.getId() == cha.getId()) { // ?????????????????????
                    continue;
                }

                // ???????????????MOB???????????????????????????MOB??????????????????
                // ???????????????PC,Summon,Pet???????????????????????????PC,Summon,Pet,MOB????????????
                if (cha instanceof L1MonsterInstance) {
                    if (!(object instanceof L1MonsterInstance)) {
                        continue;
                    }
                }
                if ((cha instanceof L1PcInstance)
                        || (cha instanceof L1SummonInstance)
                        || (cha instanceof L1PetInstance)) {
                    if (!((object instanceof L1PcInstance)
                            || (object instanceof L1SummonInstance)
                            || (object instanceof L1PetInstance) || (object instanceof L1MonsterInstance))) {
                        continue;
                    }
                }

                dmg = calcDamageReduction(pc, (L1Character) object, dmg, attr);
                if (dmg <= 0) {
                    continue;
                }
                if (object instanceof L1PcInstance) {
                    final L1PcInstance targetPc = (L1PcInstance) object;
                    targetPc.sendPackets(new S_DoActionGFX(targetPc.getId(),
                            ActionCodes.ACTION_Damage));
                    targetPc.broadcastPacket(new S_DoActionGFX(
                            targetPc.getId(), ActionCodes.ACTION_Damage));
                    targetPc.receiveDamage(pc, (int) dmg, false);
                } else if ((object instanceof L1SummonInstance)
                        || (object instanceof L1PetInstance)
                        || (object instanceof L1MonsterInstance)) {
                    final L1NpcInstance targetNpc = (L1NpcInstance) object;
                    targetNpc.broadcastPacket(new S_DoActionGFX(targetNpc
                            .getId(), ActionCodes.ACTION_Damage));
                    targetNpc.receiveDamage(pc, (int) dmg);
                }
            }
        }
        return calcDamageReduction(pc, cha, dmg, attr);
    }

    /** ????????????(??????????????????) */
    public static double getBaphometStaffDamage(final L1PcInstance pc,
            final L1Character cha) {
        double dmg = 0;
        final int chance = Random.nextInt(100) + 1;
        if (14 >= chance) {
            final int locx = cha.getX();
            final int locy = cha.getY();
            final int sp = pc.getSp();
            final int intel = pc.getInt();
            double bsk = 0;
            if (pc.hasSkillEffect(BERSERKERS)) {
                bsk = 0.2;
            }
            dmg = (intel + sp) * (1.8 + bsk) + Random.nextInt(intel + sp) * 1.8;
            final S_EffectLocation packet = new S_EffectLocation(locx, locy,
                    129);
            pc.sendPackets(packet);
            pc.broadcastPacket(packet);
        }
        return calcDamageReduction(pc, cha, dmg, L1Skills.ATTR_EARTH);
    }

    /** ???????????? */
    public static double getDiceDaggerDamage(final L1PcInstance pc,
            final L1Character cha, final L1ItemInstance weapon) {
        double dmg = 0;
        final int chance = Random.nextInt(100) + 1;
        if (2 >= chance) {
            dmg = cha.getCurrentHp() * 2 / 3;
            if (cha.getCurrentHp() - dmg < 0) {
                dmg = 0;
            }
            final String msg = weapon.getLogName();
            pc.sendPackets(new S_ServerMessage(158, msg)); // \f1%0%s ?????????
            pc.getInventory().removeItem(weapon, 1);
        }
        return dmg;
    }

    /** ????????????????????? */
    public static double getKiringkuDamage(final L1PcInstance pc,
            final L1Character cha) {
        int dmg = 0;
        final int dice = 5;
        final int diceCount = 2;
        int value = 0;
        int kiringkuDamage = 0;
        int charaIntelligence = 0;
        if (pc.getWeapon().getItem().getItemId() == 270) {
            value = 16;
        } else {
            value = 14;
        }

        for (int i = 0; i < diceCount; i++) {
            kiringkuDamage += (Random.nextInt(dice) + 1);
        }
        kiringkuDamage += value;

        final int spByItem = pc.getSp() - pc.getTrueSp(); // ?????????SP??????
        charaIntelligence = pc.getInt() + spByItem - 12;
        if (charaIntelligence < 1) {
            charaIntelligence = 1;
        }
        final double kiringkuCoefficientA = (1.0 + charaIntelligence * 3.0 / 32.0);

        kiringkuDamage *= kiringkuCoefficientA;

        final double kiringkuFloor = Math.floor(kiringkuDamage);

        dmg += kiringkuFloor + pc.getWeapon().getEnchantLevel()
                + pc.getOriginalMagicDamage();

        if (pc.hasSkillEffect(ILLUSION_AVATAR)) {
            dmg += 10;
        }

        if (pc.getWeapon().getItem().getItemId() == 270) { // ??????????????????
            pc.sendPackets(new S_SkillSound(pc.getId(), 6983));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), 6983));
        } else {
            pc.sendPackets(new S_SkillSound(pc.getId(), 7049));
            pc.broadcastPacket(new S_SkillSound(pc.getId(), 7049));
        }

        return calcDamageReduction(pc, cha, dmg, 0);
    }

    /** ????????????(???????????????) */
    public static double getLightningEdgeDamage(final L1PcInstance pc,
            final L1Character cha) {
        double dmg = 0;
        final int chance = Random.nextInt(100) + 1;
        if (4 >= chance) {
            final int sp = pc.getSp();
            final int intel = pc.getInt();
            double bsk = 0;
            if (pc.hasSkillEffect(BERSERKERS)) {
                bsk = 0.2;
            }
            dmg = (intel + sp) * (2 + bsk) + Random.nextInt(intel + sp) * 2;

            pc.sendPackets(new S_SkillSound(cha.getId(), 10));
            pc.broadcastPacket(new S_SkillSound(cha.getId(), 10));
        }
        return calcDamageReduction(pc, cha, dmg, L1Skills.ATTR_WIND);
    }

    /** ???????????????????????? */
    public static double getWeaponSkillDamage(final L1PcInstance pc,
            final L1Character cha, final int weaponId) {

        final L1WeaponSkill weaponSkill = WeaponSkillTable.getInstance()
                .getTemplate(weaponId);

        if ((pc == null) || (cha == null) || (weaponSkill == null)) {
            return 0;
        }

        final int chance = Random.nextInt(100) + 1;
        if (weaponSkill.getProbability() < chance) {
            return 0;
        }

        final int skillId = weaponSkill.getSkillId();
        if (skillId != 0) {
            final L1Skills skill = SkillsTable.getInstance().getTemplate(
                    skillId);
            if ((skill != null) && skill.getTarget().equals("buff")) {
                if (!isFreeze(cha)) { // ????????????or???????????????
                    cha.setSkillEffect(skillId,
                            weaponSkill.getSkillTime() * 1000);
                }
            }
        }

        final int effectId = weaponSkill.getEffectId();
        if (effectId != 0) {
            int chaId = 0;
            if (weaponSkill.getEffectTarget() == 0) {
                chaId = cha.getId();
            } else {
                chaId = pc.getId();
            }
            final boolean isArrowType = weaponSkill.isArrowType();
            if (!isArrowType) {
                pc.sendPackets(new S_SkillSound(chaId, effectId));
                pc.broadcastPacket(new S_SkillSound(chaId, effectId));
            } else {
                final int[] data = { ActionCodes.ACTION_Attack, 0, effectId, 6 };
                final S_UseAttackSkill packet = new S_UseAttackSkill(pc,
                        cha.getId(), cha.getX(), cha.getY(), data, false);
                pc.sendPackets(packet);
                pc.broadcastPacket(packet);
            }
        }

        double damage = 0;
        final int randomDamage = weaponSkill.getRandomDamage();
        if (randomDamage != 0) {
            damage = Random.nextInt(randomDamage);
        }
        damage += weaponSkill.getFixDamage();

        final int area = weaponSkill.getArea();
        if ((area > 0) || (area == -1)) { // ???????????????
            for (final L1Object object : L1World.getInstance()
                    .getVisibleObjects(cha, area)) {
                if (object == null) {
                    continue;
                }
                if (!(object instanceof L1Character)) {
                    continue;
                }
                if (object.getId() == pc.getId()) {
                    continue;
                }
                if (object.getId() == cha.getId()) { // ???????????????L1Attack???????????????????????????
                    continue;
                }

                // ???????????????MOB???????????????????????????MOB??????????????????
                // ???????????????PC,Summon,Pet???????????????????????????PC,Summon,Pet,MOB????????????
                if (cha instanceof L1MonsterInstance) {
                    if (!(object instanceof L1MonsterInstance)) {
                        continue;
                    }
                }
                if ((cha instanceof L1PcInstance)
                        || (cha instanceof L1SummonInstance)
                        || (cha instanceof L1PetInstance)) {
                    if (!((object instanceof L1PcInstance)
                            || (object instanceof L1SummonInstance)
                            || (object instanceof L1PetInstance) || (object instanceof L1MonsterInstance))) {
                        continue;
                    }
                }

                // ???????????????????????????
                boolean isNowWar = false;
                final int castleId = L1CastleLocation
                        .getCastleIdByArea((L1Character) object);
                if (castleId > 0) {
                    isNowWar = WarTimeController.getInstance().isNowWar(
                            castleId);
                }
                if (!isNowWar) { // ??????????????????
                    // ?????????????????? ??????????????? ????????????
                    if (!(object instanceof L1MonsterInstance)
                            && (((L1Character) object).getZoneType() == 1)) {
                        continue;
                    }
                    // ????????????
                    if (object instanceof L1PetInstance) {
                        damage /= 8;
                    } else if (object instanceof L1SummonInstance) {
                        final L1SummonInstance summon = (L1SummonInstance) object;
                        if (summon.isExsistMaster()) {
                            damage /= 8;
                        }
                    }
                }

                damage = calcDamageReduction(pc, (L1Character) object, damage,
                        weaponSkill.getAttr());
                if (damage <= 0) {
                    continue;
                }
                if (object instanceof L1PcInstance) {
                    final L1PcInstance targetPc = (L1PcInstance) object;
                    targetPc.sendPackets(new S_DoActionGFX(targetPc.getId(),
                            ActionCodes.ACTION_Damage));
                    targetPc.broadcastPacket(new S_DoActionGFX(
                            targetPc.getId(), ActionCodes.ACTION_Damage));
                    targetPc.receiveDamage(pc, (int) damage, false);
                } else if ((object instanceof L1SummonInstance)
                        || (object instanceof L1PetInstance)
                        || (object instanceof L1MonsterInstance)) {
                    final L1NpcInstance targetNpc = (L1NpcInstance) object;
                    targetNpc.broadcastPacket(new S_DoActionGFX(targetNpc
                            .getId(), ActionCodes.ACTION_Damage));
                    targetNpc.receiveDamage(pc, (int) damage);
                }
            }
        }

        return calcDamageReduction(pc, cha, damage, weaponSkill.getAttr());
    }

    /** ????????????????????? */
    public static void giveArkMageDiseaseEffect(final L1PcInstance pc,
            final L1Character cha) {
        final int chance = Random.nextInt(1000) + 1;
        int probability = (5 - ((cha.getMr() / 10) * 5)) * 10;
        if (probability == 0) {
            probability = 10;
        }
        if (probability >= chance) {
            final L1SkillUse l1skilluse = new L1SkillUse();
            l1skilluse.handleCommands(pc, 56, cha.getId(), cha.getX(),
                    cha.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
        }
    }

    /** ?????????????????? */
    public static void giveFettersEffect(final L1PcInstance pc,
            final L1Character cha) {
        final int fettersTime = 8000;
        if (isFreeze(cha)) { // ????????????or???????????????
            return;
        }
        if ((Random.nextInt(100) + 1) <= 2) {
            L1EffectSpawn.getInstance().spawnEffect(81182, fettersTime,
                    cha.getX(), cha.getY(), cha.getMapId());
            if (cha instanceof L1PcInstance) {
                final L1PcInstance targetPc = (L1PcInstance) cha;
                targetPc.setSkillEffect(STATUS_FREEZE, fettersTime);
                targetPc.sendPackets(new S_SkillSound(targetPc.getId(), 4184));
                targetPc.broadcastPacket(new S_SkillSound(targetPc.getId(),
                        4184));
                targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND,
                        true));
            } else if ((cha instanceof L1MonsterInstance)
                    || (cha instanceof L1SummonInstance)
                    || (cha instanceof L1PetInstance)) {
                final L1NpcInstance npc = (L1NpcInstance) cha;
                npc.setSkillEffect(STATUS_FREEZE, fettersTime);
                npc.broadcastPacket(new S_SkillSound(npc.getId(), 4184));
                npc.setParalyzed(true);
            }
        }
    }

    /** ??????????????? */
    private static boolean isFreeze(final L1Character cha) {
        if (cha.hasSkillEffect(STATUS_FREEZE)) {
            return true;
        }
        if (cha.hasSkillEffect(ABSOLUTE_BARRIER)) {
            return true;
        }
        if (cha.hasSkillEffect(ICE_LANCE)) {
            return true;
        }
        if (cha.hasSkillEffect(FREEZING_BLIZZARD)) {
            return true;
        }
        if (cha.hasSkillEffect(FREEZING_BREATH)) {
            return true;
        }
        if (cha.hasSkillEffect(EARTH_BIND)) {
            return true;
        }

        // ??????????????????
        if (cha.hasSkillEffect(COUNTER_MAGIC)) {
            cha.removeSkillEffect(COUNTER_MAGIC);
            final int castgfx = SkillsTable.getInstance()
                    .getTemplate(COUNTER_MAGIC).getCastGfx();
            cha.broadcastPacket(new S_SkillSound(cha.getId(), castgfx));
            if (cha instanceof L1PcInstance) {
                final L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
            }
            return true;
        }
        return false;
    }

    /** ??????ID */
    private final int _weaponId;

    /** ?????? */
    private final int _probability;

    /** ???????????? */
    private final int _fixDamage;

    /** ???????????? */
    private final int _randomDamage;

    /** ?????? */
    private final int _area;

    /** ??????ID */
    private final int _skillId;

    /** ???????????? */
    private final int _skillTime;

    /** ??????ID */
    private final int _effectId;

    /** ???????????? 0:?????? 1:?????? */
    private final int _effectTarget;

    /** ???????????? */
    private final boolean _isArrowType;

    /** ?????? */
    private final int _attr;

    public L1WeaponSkill(final int weaponId, final int probability,
            final int fixDamage, final int randomDamage, final int area,
            final int skillId, final int skillTime, final int effectId,
            final int effectTarget, final boolean isArrowType, final int attr) {
        this._weaponId = weaponId;
        this._probability = probability;
        this._fixDamage = fixDamage;
        this._randomDamage = randomDamage;
        this._area = area;
        this._skillId = skillId;
        this._skillTime = skillTime;
        this._effectId = effectId;
        this._effectTarget = effectTarget;
        this._isArrowType = isArrowType;
        this._attr = attr;
    }

    /** ???????????? */
    public int getArea() {
        return this._area;
    }

    /** ???????????? */
    public int getAttr() {
        return this._attr;
    }

    /** ????????????ID */
    public int getEffectId() {
        return this._effectId;
    }

    /** ?????????????????? 0:?????? 1:?????? */
    public int getEffectTarget() {
        return this._effectTarget;
    }

    /** ?????????????????? */
    public int getFixDamage() {
        return this._fixDamage;
    }

    /** ???????????? */
    public int getProbability() {
        return this._probability;
    }

    /** ?????????????????? */
    public int getRandomDamage() {
        return this._randomDamage;
    }

    /** ????????????ID */
    public int getSkillId() {
        return this._skillId;
    }

    /** ?????????????????? */
    public int getSkillTime() {
        return this._skillTime;
    }

    /** ????????????ID */
    public int getWeaponId() {
        return this._weaponId;
    }

    /** ???????????? */
    public boolean isArrowType() {
        return this._isArrowType;
    }

}
