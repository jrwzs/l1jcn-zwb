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

import static com.lineage.server.model.skill.L1SkillId.ADDITIONAL_FIRE;
import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_5_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_S;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_ITEM_OF_ARMOR_SETS;
import static com.lineage.server.model.skill.L1SkillId.EFFECT_OF_ARMOR_SETS;
import static com.lineage.server.model.skill.L1SkillId.EXOTIC_VITALIZE;
import static com.lineage.server.model.skill.L1SkillId.NATURES_TOUCH;
import static com.lineage.server.model.skill.L1SkillId.STATUS_UNDERWATER_BREATH;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.Config;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_bonusstats;
import com.lineage.server.types.Point;
import com.lineage.server.utils.Random;

/**
 * HP?????????(?????????)
 */
public class HpRegeneration extends TimerTask {

    private static final Logger _log = Logger.getLogger(HpRegeneration.class
            .getName());

    /**
     * ???????????????PC????????????????????????????????????
     * 
     * @param pc
     *            PC
     * @return true ??????PC??????????????????????????????
     */
    private static boolean isPlayerInLifeStream(final L1PcInstance pc) {
        for (final L1Object object : pc.getKnownObjects()) {
            if ((object instanceof L1EffectInstance) == false) {
                continue;
            }
            final L1EffectInstance effect = (L1EffectInstance) object;
            if ((effect.getNpcId() == 81169)
                    && (effect.getLocation().getTileLineDistance(
                            pc.getLocation()) < 4)) {
                return true;
            }
        }
        return false;
    }

    private final L1PcInstance _pc;
    /** ?????????????????? */
    private int _regenMax = 0;
    /** ????????? */
    private int _regenPoint = 0;

    /** ????????? */
    private int _curPoint = 4;

    public HpRegeneration(final L1PcInstance pc) {
        this._pc = pc;

        this.updateLevel();
    }

    /**
     * ????????????.
     */
    private void armorEffect() {

        // ????????????:??????
        if (!this._pc.hasSkillEffect(EFFECT_OF_ARMOR_SETS)) {
            final int effect = this._pc.getGfxEffect();
            if (effect > 0) {
                final int effectTime = this._pc.getGfxEffectTime() * 1000;
                this._pc.setSkillEffect(EFFECT_OF_ARMOR_SETS, effectTime); // ???????????????(???)
            }
        }

        // ????????????:??????????????????
        if (!this._pc.hasSkillEffect(EFFECT_ITEM_OF_ARMOR_SETS)) {
            final int obtainProps = this._pc.getObtainProps();
            if (obtainProps > 0) {
                final int effectTime = this._pc.getObtainPropsTime() * 1000;
                this._pc.setSkillEffect(EFFECT_ITEM_OF_ARMOR_SETS, effectTime); // ???????????????(???)
            }
        }

        // ???????????????????????????
        if (!(this._pc.isGm() || this._pc.isMonitor())) { // ??????GM????????????
            if ((this._pc.getLevel() >= 51)
                    && (this._pc.getLevel() - 50 > this._pc.getBonusStats())) {
                if ((this._pc.getBaseStr() + this._pc.getBaseDex()
                        + this._pc.getBaseCon() + this._pc.getBaseInt()
                        + this._pc.getBaseWis() + this._pc.getBaseCha()) < (Config.BONUS_STATS1 * 6)) {
                    this._pc.sendPackets(new S_bonusstats(this._pc.getId(), 1));
                }
            }
        }
    }

    /** 50????????? */
    private boolean isLv50Quest(final L1PcInstance pc) {
        final int mapId = pc.getMapId();
        return ((mapId == 2000) || (mapId == 2001)) ? true : false;
    }

    /** ???????????????????????? */
    private boolean isOverWeight(final L1PcInstance pc) {
        // ???????????????????????????????????????
        // ??????????????????????????????????????????????????????
        if (pc.hasSkillEffect(EXOTIC_VITALIZE)
                || pc.hasSkillEffect(ADDITIONAL_FIRE)) {
            return false;
        }
        if (pc.getInventory().checkEquipped(20049)) { // ????????????????????????
            return false;
        }

        return (121 <= pc.getInventory().getWeight242()) ? true : false;
    }

    /** ??????????????????????????? */
    private boolean isUnderwater(final L1PcInstance pc) {
        if (pc.isGhost()) {
            return false;
        }
        // ???????????????????????? ???????????????????????????????????? ???????????????????????????????????????
        if (pc.getInventory().checkEquipped(20207)) { // ????????????
            return false;
        }
        if (pc.hasSkillEffect(STATUS_UNDERWATER_BREATH)) {
            return false;
        }
        if (pc.getInventory().checkEquipped(21048) // ???????????????
                && pc.getInventory().checkEquipped(21049) // ???????????????
                && pc.getInventory().checkEquipped(21050)) { // ???????????????
            return false;
        }

        return pc.getMap().isUnderwater();
    }

    public void regenHp() {
        if (this._pc.isDead()) {
            return;
        }

        int maxBonus = 1;

        // CON??????
        if ((11 < this._pc.getLevel()) && (14 <= this._pc.getCon())) {
            maxBonus = this._pc.getCon() - 12;
            if (25 < this._pc.getCon()) {
                maxBonus = 14;
            }
        }

        int equipHpr = this._pc.getInventory().hpRegenPerTick();
        equipHpr += this._pc.getHpr();
        int bonus = Random.nextInt(maxBonus) + 1;

        if (this._pc.hasSkillEffect(NATURES_TOUCH)) { // ????????????
            bonus += 15;
        }
        if (L1HouseLocation.isInHouse(this._pc.getX(), this._pc.getY(),
                this._pc.getMapId())) {
            bonus += 5;
        }
        if ((this._pc.getMapId() == 16384) || (this._pc.getMapId() == 16896)
                || (this._pc.getMapId() == 17408)
                || (this._pc.getMapId() == 17920)
                || (this._pc.getMapId() == 18432)
                || (this._pc.getMapId() == 18944)
                || (this._pc.getMapId() == 19968)
                || (this._pc.getMapId() == 19456)
                || (this._pc.getMapId() == 20480)
                || (this._pc.getMapId() == 20992)
                || (this._pc.getMapId() == 21504)
                || (this._pc.getMapId() == 22016)
                || (this._pc.getMapId() == 22528)
                || (this._pc.getMapId() == 23040)
                || (this._pc.getMapId() == 23552)
                || (this._pc.getMapId() == 24064)
                || (this._pc.getMapId() == 24576)
                || (this._pc.getMapId() == 25088)) { // ??????
            bonus += 5;
        }
        // ?????????????????????
        if ((this._pc.getLocation().isInScreen(new Point(33055, 32336))
                && (this._pc.getMapId() == 4) && this._pc.isElf())) {
            bonus += 5;
        }
        if (this._pc.hasSkillEffect(COOKING_1_5_N)
                || this._pc.hasSkillEffect(COOKING_1_5_S)) {
            bonus += 3;
        }
        if (this._pc.hasSkillEffect(COOKING_2_4_N)
                || this._pc.hasSkillEffect(COOKING_2_4_S)
                || this._pc.hasSkillEffect(COOKING_3_6_N)
                || this._pc.hasSkillEffect(COOKING_3_6_S)) {
            bonus += 2;
        }
        if (this._pc.getOriginalHpr() > 0) { // ??????CON HPR??????
            bonus += this._pc.getOriginalHpr();
        }

        boolean inLifeStream = false;
        if (isPlayerInLifeStream(this._pc)) {
            inLifeStream = true;
            // ?????????????????????????????????HPR+3??????????????????
            bonus += 3;
        }

        // ?????????????????????
        if ((this._pc.get_food() < 3) || this.isOverWeight(this._pc)
                || this._pc.hasSkillEffect(BERSERKERS)) { // ?????????
            bonus = 0;
            // ?????????????????????????????????????????????????????? ??????????????????????????????????????????????????????
            if (equipHpr > 0) {
                equipHpr = 0;
            }
        }

        int newHp = this._pc.getCurrentHp();
        newHp += bonus + equipHpr;

        if (newHp < 1) {
            newHp = 1; // ??????????????????????????????
        }
        // ???????????????HP??????
        // ?????????????????????????????????????????????????????????
        if (this.isUnderwater(this._pc)) {
            newHp -= 20;
            if (newHp < 1) {
                if (this._pc.isGm()) {
                    newHp = 1;
                } else {
                    this._pc.death(null); // ??????HP????????????????????????
                }
            }
        }
        // Lv50????????????????????????1F2F HP????????????
        if (this.isLv50Quest(this._pc) && !inLifeStream) {
            newHp -= 10;
            if (newHp < 1) {
                if (this._pc.isGm()) {
                    newHp = 1;
                } else {
                    this._pc.death(null); // ??????HP??????????????????
                }
            }
        }
        // ???????????????HP????????????
        if ((this._pc.getMapId() == 410) && !inLifeStream) {
            newHp -= 10;
            if (newHp < 1) {
                if (this._pc.isGm()) {
                    newHp = 1;
                } else {
                    this._pc.death(null); // ??????HP??????????????????
                }
            }
        }

        if (!this._pc.isDead()) {
            this._pc.setCurrentHp(Math.min(newHp, this._pc.getMaxHp()));
        }
    }

    @Override
    public void run() {
        try {
            if (this._pc.isDead()) {
                return;
            }

            this._regenPoint += this._curPoint;
            this._curPoint = 4;

            synchronized (this) {
                if (this._regenMax <= this._regenPoint) {
                    this._regenPoint = 0;
                    this.regenHp();
                }
            }

            this.armorEffect();
        } catch (final Throwable e) {
            _log.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }

    public void setState(final int state) {
        if (this._curPoint < state) {
            return;
        }

        this._curPoint = state;
    }

    /** ???????????? */
    public void updateLevel() {
        final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9,
                3, 2 };

        int regenLvl = Math.min(10, this._pc.getLevel());
        if ((30 <= this._pc.getLevel()) && this._pc.isKnight()) {
            regenLvl = 11;
        }

        synchronized (this) {
            this._regenMax = lvlTable[regenLvl - 1] * 4;
        }
    }
}
