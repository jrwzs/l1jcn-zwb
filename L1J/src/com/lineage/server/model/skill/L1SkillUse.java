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
package com.lineage.server.model.skill;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.PolyTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1EffectSpawn;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1War;
import com.lineage.server.model.L1World;
import com.lineage.server.model.Instance.L1AuctionBoardInstance;
import com.lineage.server.model.Instance.L1BoardInstance;
import com.lineage.server.model.Instance.L1CrownInstance;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1DwarfInstance;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1GuardInstance;
import com.lineage.server.model.Instance.L1HousekeeperInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.Instance.L1TeleporterInstance;
import com.lineage.server.model.Instance.L1TowerInstance;
import com.lineage.server.model.skill.stop.Producer;
import com.lineage.server.model.skill.use.L1SkillEffect;
import com.lineage.server.model.skill.use.TargetStatus;
import com.lineage.server.model.trap.L1WorldTraps;
import com.lineage.server.serverpackets.S_AttackPacket;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_Dexup;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_RangeSkill;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillIconAura;
import com.lineage.server.serverpackets.S_SkillIconGFX;
import com.lineage.server.serverpackets.S_SkillIconShield;
import com.lineage.server.serverpackets.S_SkillIconWaterLife;
import com.lineage.server.serverpackets.S_SkillIconWindShackle;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_Sound;
import com.lineage.server.serverpackets.S_Strup;
import com.lineage.server.serverpackets.S_TrueTarget;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.templates.L1BookMark;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.Random;
import com.lineage.server.utils.collections.Lists;

/**
 * ??????????????????
 */
public class L1SkillUse {

    private static Logger _log = Logger.getLogger(L1SkillUse.class.getName());

    /** ??????:?????? */
    public static final int TYPE_NORMAL = 0;
    /** ??????:?????? */
    public static final int TYPE_LOGIN = 1;
    /** ??????:???????????? */
    public static final int TYPE_SPELLSC = 2;
    /** ??????:NPC BUFF */
    public static final int TYPE_NPCBUFF = 3;
    /** ??????:GM BUFF */
    public static final int TYPE_GMBUFF = 4;
    /** ?????? */
    private L1Skills _skill;
    /** ??????ID */
    private int _skillId;
    /** ?????? */
    private int _dmg;
    /** ???????????????????????? */
    private int _getBuffDuration;
    /** ???????????????????????? */
    private int _shockStunDuration;
    /** ??????????????????????????? */
    private int _getBuffIconDuration;
    /** ?????? ID */
    private int _targetID;
    /** MP ?????? */
    private int _mpConsume = 0;
    /** HP ?????? */
    private int _hpConsume = 0;
    /** ?????? X ?????? */
    private int _targetX = 0;
    /** ??????Y ?????? */
    private int _targetY = 0;
    /** ???????????? */
    private String _message = null;
    /** ???????????? */
    private int _skillTime = 0;
    /** ?????? */
    private int _type = 0;
    /** PK??? */
    private boolean _isPK = false;
    /** ????????????ID */
    private int _bookmarkId = 0;
    /** ???????????????ID */
    private int _itemobjid = 0;
    /** ????????????????????? */
    private boolean _checkedUseSkill = false;
    /** ???????????? (1/10???) */
    private int _leverage = 10;
    /** ?????????????????? */
    private int _skillRanged = 0;
    /** ?????????????????? */
    private int _skillArea = 0;
    /** ??????????????? */
    private boolean _isFreeze = false;
    /** ???????????????????????? */
    private boolean _isCounterMagic = true;
    /**  */
    private boolean _isGlanceCheckFail = false;
    /** ??????????????? */
    private L1Character _user = null;
    /** ?????????????????? */
    private L1Character _target = null;
    /** ????????????PC */
    private L1PcInstance _player = null;
    /** ????????????NPC */
    private L1NpcInstance _npc = null;
    /** ?????? (????????????) */
    private int _calcType;
    /** PC ??? PC */
    private static final int PC_PC = 1;
    /** PC ??? NPC */
    private static final int PC_NPC = 2;
    /** NPC ??? PC */
    private static final int NPC_PC = 3;
    /** NPC ??? NPC */
    private static final int NPC_NPC = 4;
    /** ???????????? */
    private List<TargetStatus> _targetList;
    /** ?????? ID */
    private int _actid = 0;
    /** ?????? ID */
    private int _gfxid = 0;

    /** ??????????????????????????????????????? */
    private static final int[] EXCEPT_COUNTER_MAGIC = { HEAL, LIGHT, SHIELD,
            TELEPORT, HOLY_WEAPON, CURE_POISON, ENCHANT_WEAPON, DETECTION,
            DECREASE_WEIGHT, EXTRA_HEAL, BLESSED_ARMOR, PHYSICAL_ENCHANT_DEX,
            COUNTER_MAGIC, MEDITATION, GREATER_HEAL, REMOVE_CURSE,
            PHYSICAL_ENCHANT_STR, HASTE, CANCELLATION, BLESS_WEAPON, HEAL_ALL,
            HOLY_WALK, GREATER_HASTE, BERSERKERS, FULL_HEAL, INVISIBILITY,
            RESURRECTION, LIFE_STREAM, SHAPE_CHANGE, IMMUNE_TO_HARM,
            MASS_TELEPORT, COUNTER_DETECTION, CREATE_MAGICAL_WEAPON,
            GREATER_RESURRECTION, ABSOLUTE_BARRIER, ADVANCE_SPIRIT, SHOCK_STUN,
            REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER,
            BLIND_HIDING, ENCHANT_VENOM, SHADOW_ARMOR, BRING_STONE,
            MOVING_ACCELERATION, BURNING_SPIRIT, VENOM_RESIST, DOUBLE_BRAKE,
            UNCANNY_DODGE, SHADOW_FANG, DRESS_MIGHTY, DRESS_DEXTERITY,
            DRESS_EVASION, TRUE_TARGET, GLOWING_AURA, SHINING_AURA, CALL_CLAN,
            BRAVE_AURA, RUN_CLAN, RESIST_MAGIC, BODY_TO_MIND,
            TELEPORT_TO_MATHER, TRIPLE_ARROW, COUNTER_MIRROR, CLEAR_MIND,
            RESIST_ELEMENTAL, BLOODY_SOUL, ELEMENTAL_PROTECTION, FIRE_WEAPON,
            WIND_SHOT, WIND_WALK, EARTH_SKIN, FIRE_BLESS, STORM_EYE,
            NATURES_TOUCH, EARTH_BLESS, AREA_OF_SILENCE, BURNING_WEAPON,
            NATURES_BLESSING, CALL_OF_NATURE, STORM_SHOT, IRON_SKIN,
            EXOTIC_VITALIZE, WATER_LIFE, ELEMENTAL_FIRE, SOUL_OF_FLAME,
            ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS, AWAKEN_FAFURION,
            AWAKEN_VALAKAS, MIRROR_IMAGE, ILLUSION_OGRE, ILLUSION_LICH,
            PATIENCE, ILLUSION_DIA_GOLEM, INSIGHT, ILLUSION_AVATAR, 10026,
            10027, 10028, 10029 };

    /** ?????? */
    public L1SkillUse() {
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     * 
     * @param cha
     * @param repetition
     */
    private void addMagicList(final L1Character cha, final boolean repetition) {
        if (this._skillTime == 0) {
            this._getBuffDuration = this._skill.getBuffDuration() * 1000; // ????????????
            if (this._skill.getBuffDuration() == 0) {
                if (this._skillId == INVISIBILITY) { // ?????????
                    cha.setSkillEffect(INVISIBILITY, 0);
                }
                return;
            }
        } else {
            this._getBuffDuration = this._skillTime * 1000; // ????????????time???0?????????????????????
        }

        if (this._skillId == SHOCK_STUN) {
            this._getBuffDuration = this._shockStunDuration;
        }

        // ??????????????????L1Poison?????????
        if (this._skillId == CURSE_POISON) {
            return;
        }

        // ?????????????????????????????????????????????L1CurseParalysis?????????
        if ((this._skillId == CURSE_PARALYZE)
                || (this._skillId == CURSE_PARALYZE2)) {
            return;
        }

        // ??????????????????????????? L1PolyMorph ?????????
        if (this._skillId == SHAPE_CHANGE) {
            return;
        }

        // ???????????????????????????L1ItemInstance?????????
        if ((this._skillId == BLESSED_ARMOR) || (this._skillId == HOLY_WEAPON)
                || (this._skillId == ENCHANT_WEAPON)
                || (this._skillId == BLESS_WEAPON)
                || (this._skillId == SHADOW_FANG)) {
            return;
        }

        // ????????????
        if (((this._skillId == ICE_LANCE)
                || (this._skillId == FREEZING_BLIZZARD)
                || (this._skillId == FREEZING_BREATH)
                || (this._skillId == ICE_LANCE_COCKATRICE) || (this._skillId == ICE_LANCE_BASILISK))
                && !this._isFreeze) {
            return;
        }

        // ????????????????????????L1Awake?????????
        if ((this._skillId == AWAKEN_ANTHARAS)
                || (this._skillId == AWAKEN_FAFURION)
                || (this._skillId == AWAKEN_VALAKAS)) {
            return;
        }

        // ????????????????????????????????????
        if ((this._skillId == BONE_BREAK) || (this._skillId == CONFUSION)) {
            return;
        }
        cha.setSkillEffect(this._skillId, this._getBuffDuration);

        if ((this._skillId == ELEMENTAL_FALL_DOWN) && repetition) { // ????????????????????????
            if (this._skillTime == 0) {
                this._getBuffIconDuration = this._skill.getBuffDuration(); // ????????????
            } else {
                this._getBuffIconDuration = this._skillTime;
            }
            this._target.removeSkillEffect(ELEMENTAL_FALL_DOWN);
            this.runSkill();
            return;
        }
        if ((cha instanceof L1PcInstance) && repetition) { // ?????????PC ???????????????????????????
            final L1PcInstance pc = (L1PcInstance) cha;
            this.sendIcon(pc);
        }
    }

    /**
     * @param player
     *            ????????????PC
     * @param skillid
     *            ????????????
     * @param target_id
     *            ??????OBJID
     * @param x
     *            X??????
     * @param y
     *            Y??????
     * @param message
     *            ??????
     * @param time
     *            ??????
     * @param type
     *            ??????
     * @param attacker
     *            ????????????NPC
     * @return
     */
    public boolean checkUseSkill(final L1PcInstance player, final int skillid,
            final int target_id, final int x, final int y,
            final String message, final int time, final int type,
            final L1Character attacker) {
        return this.checkUseSkill(player, skillid, target_id, x, y, message,
                time, type, attacker, 0, 0, 0);
    }

    /**
     * @param player
     *            ????????????PC
     * @param skillid
     *            ????????????
     * @param target_id
     *            ??????OBJID
     * @param x
     *            X??????
     * @param y
     *            Y??????
     * @param message
     *            ??????
     * @param time
     *            ??????
     * @param type
     *            ??????
     * @param attacker
     *            ????????????NPC
     * @param actid
     *            ??????ID
     * @param mpConsume
     *            ??????MP
     * @return
     */
    public boolean checkUseSkill(final L1PcInstance player, final int skillid,
            final int target_id, final int x, final int y,
            final String message, final int time, final int type,
            final L1Character attacker, final int actid, final int gfxid,
            final int mpConsume) {
        // ?????????????????????
        this.setCheckedUseSkill(true); // ??????????????????
        this._targetList = Lists.newList(); // ????????????????????????

        this._skill = SkillsTable.getInstance().getTemplate(skillid);
        this._skillId = skillid;
        this._targetX = x;
        this._targetY = y;
        this._message = message;
        this._skillTime = time;
        this._type = type;
        this._actid = actid;
        this._gfxid = gfxid;
        this._mpConsume = mpConsume;
        boolean checkedResult = true;

        if (attacker == null) {
            // pc
            this._player = player;
            this._user = this._player;
        } else {
            // npc
            this._npc = (L1NpcInstance) attacker;
            this._user = this._npc;
        }

        if (this._skill.getTarget().equals("none")) {
            this._targetID = this._user.getId();
            this._targetX = this._user.getX();
            this._targetY = this._user.getY();
        } else {
            this._targetID = target_id;
        }

        switch (type) {
            case TYPE_NORMAL: // ??????????????????
                checkedResult = this.isNormalSkillUsable();
                break;

            case TYPE_SPELLSC: // ??????????????????
                checkedResult = this.isSpellScrollUsable();
                break;

            case TYPE_NPCBUFF: // ????????????NPC
                checkedResult = true;
                break;
        }
        if (!checkedResult) {
            return false;
        }

        // ???????????????????????????????????????
        // ????????????????????????????????????
        if ((this._skillId == FIRE_WALL) || (this._skillId == LIFE_STREAM)
                || (this._skillId == TRUE_TARGET)) {
            return true;
        }

        final L1Object l1object = L1World.getInstance().findObject(
                this._targetID);
        if (l1object instanceof L1ItemInstance) {
            _log.fine("????????????????????????: " + ((L1ItemInstance) l1object).getViewName());
            // ????????????????????????????????????
            // Linux???????????????Windows????????????
            // 2008.5.4???????????????????????????????????????????????????????????????return
            return false;
        }
        if (this._user instanceof L1PcInstance) {
            if (l1object instanceof L1PcInstance) {
                this._calcType = PC_PC;
            } else {
                this._calcType = PC_NPC;
            }
        } else if (this._user instanceof L1NpcInstance) {
            if (l1object instanceof L1PcInstance) {
                this._calcType = NPC_PC;
            } else if (this._skill.getTarget().equals("none")) {
                this._calcType = NPC_PC;
            } else {
                this._calcType = NPC_NPC;
            }
        }

        switch (this._skillId) {

            // ????????????????????????????????????
            case TELEPORT: // ???????????? (????????????)
            case MASS_TELEPORT: // ???????????? (???????????????)
                this._bookmarkId = target_id;
                break;

            // ?????????????????????
            case CREATE_MAGICAL_WEAPON: // ???????????? (??????????????????)
            case BRING_STONE: // ?????????????????? (????????????)
            case BLESSED_ARMOR: // ???????????? (????????????)
            case ENCHANT_WEAPON: // ???????????? (??????????????????)
            case SHADOW_FANG: // ?????????????????? (????????????)
                this._itemobjid = target_id;
                break;
        }

        this._target = (L1Character) l1object;

        // ??????????????????????????????????????????????????????PK?????????
        if (!(this._target instanceof L1MonsterInstance)
                && this._skill.getTarget().equals("attack")
                && (this._user.getId() != target_id)) {
            this._isPK = true;
        }

        // ??????????????????????????????
        // ?????????

        // ?????????????????????????????????????????????
        if (!(l1object instanceof L1Character)) {
            checkedResult = false;
        }

        // ???????????? ??????????????????
        this.makeTargetList();

        // ??????????????????&&????????????NPC
        if (this._targetList.isEmpty() && (this._user instanceof L1NpcInstance)) {
            checkedResult = false;
        }
        // ??????????????????
        return checkedResult;
    }

    /**
     * ??????????????????/????????????????????????????????????????????????????????????
     * 
     * @param cha
     */
    private void deleteRepeatedSkills(final L1Character cha) {
        final int[][] repeatedSkills = {

                // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
                { FIRE_WEAPON, WIND_SHOT, FIRE_BLESS, STORM_EYE,
                        BURNING_WEAPON, STORM_SHOT, EFFECT_BLESS_OF_MAZU },
                // ????????????????????????????????????????????????????????????????????????
                { SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN },
                // ??????????????????????????????(??????????????????????????????????????????)??????????????????????????????
                { STATUS_BRAVE, STATUS_ELFBRAVE, HOLY_WALK,
                        MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE2,
                        BLOODLUST },
                // ????????????????????????????????????????????????
                { HASTE, GREATER_HASTE, STATUS_HASTE },
                // ????????????????????????????????????
                { SLOW, MASS_SLOW, ENTANGLE },
                // ??????????????????????????????
                { PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY },
                // ??????????????????????????????
                { PHYSICAL_ENCHANT_STR, DRESS_MIGHTY },
                // ???????????????????????????
                { GLOWING_AURA, SHINING_AURA },
                // ?????????????????????
                { MIRROR_IMAGE, UNCANNY_DODGE } };

        for (final int[] skills : repeatedSkills) {
            for (final int id : skills) {
                if (id == this._skillId) {
                    this.stopSkillList(cha, skills);
                }
            }
        }
    }

    /**
     * ????????????
     * 
     * @param pc
     */
    private void detection(final L1PcInstance pc) {
        if (!pc.isGmInvis() && pc.isInvisble()) { // ???????????????
            pc.delInvis();
            pc.beginInvisTimer();
        }

        for (final L1PcInstance tgt : L1World.getInstance()
                .getVisiblePlayer(pc)) { // ????????????????????????
            if (!tgt.isGmInvis() && tgt.isInvisble()) {
                tgt.delInvis();
            }
        }
        L1WorldTraps.getInstance().onDetection(pc); // ??????????????????
    }

    /**
     * ??????????????????(???PC)
     */
    private void failSkill() {
        this.setCheckedUseSkill(false);
        switch (this._skillId) {
            // ????????????
            case TELEPORT:
            case MASS_TELEPORT:
            case TELEPORT_TO_MATHER:
                // ????????????????????????
                this._player.sendPackets(new S_Paralysis(
                        S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
                break;
        }
    }

    /**
     * ?????????????????? (1/10)
     * 
     * @return
     */
    public int getLeverage() {
        return this._leverage;
    }

    /**
     * ?????????????????????????????????
     * 
     * @return
     */
    public int getSkillArea() {
        if (this._skillArea == 0) {
            return this._skill.getArea();
        }
        return this._skillArea;
    }

    /**
     * ?????????????????????????????????
     * 
     * @return
     */
    public int getSkillRanged() {
        if (this._skillRanged == 0) {
            return this._skill.getRanged();
        }
        return this._skillRanged;
    }

    /**
     * pc ??????????????????
     * 
     * @param player
     *            ??????
     * @param skillId
     *            ??????ID
     * @param targetId
     *            ??????ID
     * @param x
     *            X??????
     * @param y
     *            Y??????
     * @param message
     *            ??????
     * @param timeSecs
     *            ??????:???
     * @param type
     *            ??????
     */
    public void handleCommands(final L1PcInstance player, final int skillId,
            final int targetId, final int x, final int y, final String message,
            final int timeSecs, final int type) {
        final L1Character attacker = null;
        this.handleCommands(player, skillId, targetId, x, y, message, timeSecs,
                type, attacker);
    }

    /**
     * ????????????????????????
     * 
     * @param player
     *            ??????
     * @param skillId
     *            ??????ID
     * @param targetId
     *            ??????ID
     * @param x
     *            X??????
     * @param y
     *            Y??????
     * @param message
     *            ??????
     * @param timeSecs
     *            ??????:???
     * @param type
     *            ??????
     * @param attacker
     *            ?????????
     */
    public void handleCommands(final L1PcInstance player, final int skillId,
            final int targetId, final int x, final int y, final String message,
            final int timeSecs, final int type, final L1Character attacker) {

        try {
            // ???????????????
            if (!this.isCheckedUseSkill()) {
                final boolean isUseSkill = this.checkUseSkill(player, skillId,
                        targetId, x, y, message, timeSecs, type, attacker);

                // ??????????????????
                if (!isUseSkill) {
                    this.failSkill(); // ???????????????
                    return;
                }
            }

            switch (type) {
                case TYPE_NORMAL: // ???????????????
                    if (!this._isGlanceCheckFail || (this.getSkillArea() > 0)
                            || this._skill.getTarget().equals("none")) {
                        this.runSkill();
                        this.useConsume();
                        this.sendGrfx(true);
                        this.sendFailMessageHandle();
                        this.setDelay();
                    }
                    break;

                case TYPE_LOGIN: // ??????????????????????????????HPMP??????????????????
                    this.runSkill();
                    break;

                case TYPE_SPELLSC: // ????????????????????? (??????????????????HPMP)
                    this.runSkill();
                    this.sendGrfx(true);
                    break;

                case TYPE_GMBUFF: // GMBUFF??????????????????????????????HPMP????????????????????????
                    this.runSkill();
                    this.sendGrfx(false);
                    break;

                case TYPE_NPCBUFF: // NPCBUFF??????????????????????????????HPMP???
                    this.runSkill();
                    this.sendGrfx(true);
                    break;
            }
            this.setCheckedUseSkill(false);
        } catch (final Exception e) {
            _log.log(Level.SEVERE, "", e);
        }
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private boolean isAttrAgrees() {
        final int magicattr = this._skill.getAttr();
        if (this._user instanceof L1NpcInstance) { // NPC????????????????????????OK
            return true;
        }

        // ?????????????????????????????????
        switch (this._skill.getSkillLevel()) {
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22: // ???????????????????????????????????? GM??????
                if ((magicattr != 0)
                        && (magicattr != this._player.getElfAttr())
                        && (!this._player.isGm())) {
                    return false;
                }
                break;
        }
        return true;
    }

    /**
     * ?????????????????????
     * 
     * @return
     */
    private boolean isCheckedUseSkill() {
        return this._checkedUseSkill;
    }

    /**
     * ???????????????????????????????????????HP/MP
     * 
     * @return
     */
    private boolean isHPMPConsume() {
        if (this._mpConsume == 0) {
            this._mpConsume = this._skill.getMpConsume();
        }
        this._hpConsume = this._skill.getHpConsume();
        int currentMp = 0;
        int currentHp = 0;

        if (this._user instanceof L1NpcInstance) {
            currentMp = this._npc.getCurrentMp();
            currentHp = this._npc.getCurrentHp();
        } else {
            currentMp = this._player.getCurrentMp();
            currentHp = this._player.getCurrentHp();

            // ?????????MP??????
            if ((this._player.getInt() > 12) && (this._skillId > HOLY_WEAPON)
                    && (this._skillId <= FREEZING_BLIZZARD)) { // LV2??????
                this._mpConsume--;
            }
            if ((this._player.getInt() > 13) && (this._skillId > STALAC)
                    && (this._skillId <= FREEZING_BLIZZARD)) { // LV3??????
                this._mpConsume--;
            }
            if ((this._player.getInt() > 14)
                    && (this._skillId > WEAK_ELEMENTAL)
                    && (this._skillId <= FREEZING_BLIZZARD)) { // LV4??????
                this._mpConsume--;
            }
            if ((this._player.getInt() > 15) && (this._skillId > MEDITATION)
                    && (this._skillId <= FREEZING_BLIZZARD)) { // LV5??????
                this._mpConsume--;
            }
            if ((this._player.getInt() > 16) && (this._skillId > DARKNESS)
                    && (this._skillId <= FREEZING_BLIZZARD)) { // LV6??????
                this._mpConsume--;
            }
            if ((this._player.getInt() > 17) && (this._skillId > BLESS_WEAPON)
                    && (this._skillId <= FREEZING_BLIZZARD)) { // LV7??????
                this._mpConsume--;
            }
            if ((this._player.getInt() > 18) && (this._skillId > DISEASE)
                    && (this._skillId <= FREEZING_BLIZZARD)) { // LV8??????
                this._mpConsume--;
            }

            // ??????????????????
            if ((this._player.getInt() > 12) && (this._skillId >= SHOCK_STUN)
                    && (this._skillId <= COUNTER_BARRIER)) {
                if (this._player.getInt() <= 17) {
                    this._mpConsume -= (this._player.getInt() - 12);
                } else {
                    this._mpConsume -= 5; // int > 18
                    if (this._mpConsume > 1) { // ?????????????????????
                        final byte extraInt = (byte) (this._player.getInt() - 17);
                        // ????????????
                        for (int first = 1, range = 2; first <= extraInt; first += range, range++) {
                            this._mpConsume--;
                        }
                    }
                }

            }

            // ??????MP?????? ????????????????????????
            switch (this._skillId) {
                case PHYSICAL_ENCHANT_DEX: // ???????????????????????????????????????
                    if (this._player.getInventory().checkEquipped(20013)) {
                        this._mpConsume /= 2;
                    }
                    break;

                case HEAL: // ???????????????????????????????????????
                case EXTRA_HEAL: // ???????????????????????????????????????
                    if (this._player.getInventory().checkEquipped(20014)) {
                        this._mpConsume /= 2;
                    }
                    break;

                case ENCHANT_WEAPON: // ??????????????????????????????????????????
                case DETECTION: // ???????????????????????????????????????
                case PHYSICAL_ENCHANT_STR: // ???????????????????????????????????????
                    if (this._player.getInventory().checkEquipped(20015)) {
                        this._mpConsume /= 2;
                    }
                    break;

                case HASTE: // ?????????????????????????????????????????????????????????????????????
                    if (this._player.getInventory().checkEquipped(20008)
                            || this._player.getInventory().checkEquipped(20013)
                            || this._player.getInventory().checkEquipped(20023)) {
                        this._mpConsume /= 2;
                    }
                    break;

                case GREATER_HASTE: // ?????????????????????????????????
                    if (this._player.getInventory().checkEquipped(20023)) {
                        this._mpConsume /= 2;
                    }
                    break;
            }

            // ??????????????????
            if (this._player.getOriginalMagicConsumeReduction() > 0) {
                this._mpConsume -= this._player
                        .getOriginalMagicConsumeReduction();
            }

            if (0 < this._skill.getMpConsume()) {
                this._mpConsume = Math.max(this._mpConsume, 1); // ????????? 1
            }
        }

        if (currentHp < this._hpConsume + 1) {
            if (this._user instanceof L1PcInstance) {
                this._player.sendPackets(new S_ServerMessage(279)); // \f1???????????????????????????????????????
            }
            return false;
        } else if (currentMp < this._mpConsume) {
            if (this._user instanceof L1PcInstance) {
                this._player.sendPackets(new S_ServerMessage(278)); // \f1???????????????????????????????????????
            }
            return false;
        }

        return true;
    }

    /**
     * ?????????????????????????????????????????????
     * 
     * @return
     */
    private boolean isItemConsume() {

        final int itemConsume = this._skill.getItemConsumeId();
        final int itemConsumeCount = this._skill.getItemConsumeCount();

        // ???????????????????????????
        if (itemConsume == 0) {
            return true;
        }

        // ?????????????????????
        if (!this._player.getInventory().checkItem(itemConsume,
                itemConsumeCount)) {
            return false;
        }

        return true;
    }

    /**
     * ?????????????????????????????????????????????
     * 
     * @return false ????????????
     */
    private boolean isNormalSkillUsable() {

        // ??????PC?????????????????????
        if (this._user instanceof L1PcInstance) {

            // ??????????????? (PC)
            final L1PcInstance pc = (L1PcInstance) this._user;

            // ?????????
            if (pc.isTeleport()) {
                return false;
            }

            // ?????????????????????
            if (pc.isParalyzed()) {
                return false;
            }

            // ???????????????????????????
            if ((pc.isInvisble() || pc.isInvisDelay())
                    && !this._skill.isInvisUsableSkill()) {
                return false;
            }

            // ????????????
            if (pc.getInventory().getWeight242() >= 197) {
                pc.sendPackets(new S_ServerMessage(316)); // \f1???????????????????????????????????????????????????
                return false;
            }

            // ????????????ID
            final int polyId = pc.getTempCharGfx();

            // ????????????
            final L1PolyMorph poly = PolyTable.getInstance()
                    .getTemplate(polyId);

            // ??????????????????????????????????????????
            if ((poly != null) && !poly.canUseSkill()) {
                pc.sendPackets(new S_ServerMessage(285)); // \f1????????????????????????????????????
                return false;
            }

            // ?????????????????????????????????????????????
            if (!this.isAttrAgrees()) {
                return false;
            }

            // ?????????????????????????????????????????????
            if ((this._skillId == ELEMENTAL_PROTECTION)
                    && (pc.getElfAttr() == 0)) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1???????????????
                return false;
            }

            // ?????????????????????????????????
            if (pc.getMap().isUnderwater() && (this._skill.getAttr() == 2)) {
                pc.sendPackets(new S_ServerMessage(280)); // \f1???????????????
                return false;
            }

            // ???????????????????????????
            if (pc.isSkillDelay()) {
                return false;
            }

            // ?????????????????????????????????????????????
            if (pc.hasSkillEffect(SILENCE)
                    || pc.hasSkillEffect(AREA_OF_SILENCE)
                    || pc.hasSkillEffect(STATUS_POISON_SILENCE)
                    || pc.hasSkillEffect(CONFUSION_ING)) {
                pc.sendPackets(new S_ServerMessage(285)); // \f1????????????????????????????????????
                return false;
            }

            // ???????????????500???????????? ???????????????
            if ((this._skillId == DISINTEGRATE) && (pc.getLawful() < 500)) {
                pc.sendPackets(new S_ServerMessage(352, "$967")); // ?????????????????????????????????????????????(??????)???
                return false;
            }

            // ??????????????????????????????????????????????????????
            if ((this._skillId == CUBE_IGNITION // ??????????????? (???????????????)
                    )
                    || (this._skillId == CUBE_QUAKE // ??????????????? (???????????????)
                    ) || (this._skillId == CUBE_SHOCK // ??????????????? (???????????????)
                    ) || (this._skillId == CUBE_BALANCE // ??????????????? (???????????????)
                    )) {

                // ?????????????????????
                boolean isNearSameCube = false;

                // ??????ID
                int gfxId = 0;

                for (final L1Object obj : L1World.getInstance()
                        .getVisibleObjects(pc, 3)) {
                    if (obj instanceof L1EffectInstance) {
                        final L1EffectInstance effect = (L1EffectInstance) obj;
                        gfxId = effect.getGfxId();
                        if (((this._skillId == CUBE_IGNITION) && (gfxId == 6706))
                                || ((this._skillId == CUBE_QUAKE) && (gfxId == 6712))
                                || ((this._skillId == CUBE_SHOCK) && (gfxId == 6718))
                                || ((this._skillId == CUBE_BALANCE) && (gfxId == 6724))) {
                            isNearSameCube = true;
                            break;
                        }
                    }
                }
                if (isNearSameCube) {
                    pc.sendPackets(new S_ServerMessage(1412)); // ??????????????????????????????????????????
                    return false;
                }
            }

            // ???????????? - ???????????????????????????
            if (((pc.getAwakeSkillId() == AWAKEN_ANTHARAS) && (this._skillId != AWAKEN_ANTHARAS))
                    || ((pc.getAwakeSkillId() == AWAKEN_FAFURION) && (this._skillId != AWAKEN_FAFURION))
                    || (((pc.getAwakeSkillId() == AWAKEN_VALAKAS) && (this._skillId != AWAKEN_VALAKAS))
                            && (this._skillId != MAGMA_BREATH)
                            && (this._skillId != SHOCK_SKIN) && (this._skillId != FREEZING_BREATH))) {
                pc.sendPackets(new S_ServerMessage(1385)); // ??????????????????????????????????????????
                return false;
            }

            // ???????????????????????????
            if ((this.isItemConsume() == false) && !this._player.isGm()) {
                this._player.sendPackets(new S_ServerMessage(299)); // \f1?????????????????????????????????
                return false;
            }
        }

        // ????????????????????????NPC?????????
        else if (this._user instanceof L1NpcInstance) {

            // ????????????????????????
            if (this._user.hasSkillEffect(SILENCE)) {
                // NPC?????????????????????1???????????????(??????????????????)
                this._user.removeSkillEffect(SILENCE);
                return false;
            }
        }

        // PC???NPC????????????HP???MP????????????
        if (!this.isHPMPConsume()) { // ?????????HP???MP??????
            return false;
        }
        return true;
    }

    /**
     * ???????????? ?????????PC?????????????????????
     * 
     * @param cha
     * @return
     */
    private boolean isPcSummonPet(final L1Character cha) {

        switch (this._calcType) {
            case PC_PC: // ?????????PC
                return true;

            case PC_NPC:
                if (cha instanceof L1SummonInstance) { // ???????????????
                    final L1SummonInstance summon = (L1SummonInstance) cha;
                    if (summon.isExsistMaster()) { // ?????????
                        return true;
                    }
                }
                if (cha instanceof L1PetInstance) { // ????????????
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * ??????????????????????????????????????????
     * 
     * @return false ????????????
     */
    private boolean isSpellScrollUsable() {

        // ????????????????????????PC
        final L1PcInstance pc = (L1PcInstance) this._user;

        // ?????????
        if (pc.isTeleport()) {
            return false;
        }

        // ?????????????????????
        if (pc.isParalyzed()) {
            return false;
        }

        // ?????????????????????
        if ((pc.isInvisble() || pc.isInvisDelay())
                && !this._skill.isInvisUsableSkill()) {
            return false;
        }

        return true;
    }

    /**
     * ??????????????????????????????
     * 
     * @param cha
     *            ???????????????????????????
     * @return true:??????????????? false:??????????????????
     * @throws Exception
     */
    private boolean isTarget(final L1Character cha) throws Exception {

        // ????????????
        if (cha == null) {
            return false;
        }

        // ???????????????
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            if (pc.isGhost() || pc.isGmInvis()) {
                return false;
            }
        }

        boolean _flg = false;

        // NPC ??? PC ?????????PC???PC??????????????????
        if ((this._calcType == NPC_PC)
                && ((cha instanceof L1PcInstance)
                        || (cha instanceof L1PetInstance) || (cha instanceof L1SummonInstance))) {
            _flg = true;
        }

        // ??????????????????
        if (cha instanceof L1DoorInstance) {
            if ((cha.getMaxHp() == 0) || (cha.getMaxHp() == 1)) {
                return false;
            }
        }

        // ????????????????????????????????????
        if ((cha instanceof L1DollInstance) && (this._skillId != HASTE)) {
            return false;
        }

        // ???????????????????????????????????????????????????
        if (((this._user instanceof L1PetInstance) || (this._user instanceof L1SummonInstance))
                && ((this._skill.getArea() > 0) || (this._skillId == LIGHTNING))) {
            if ((this._user.glanceCheck(cha.getX(), cha.getY()))
                    || (this._skill.isThrough())) {
                return (cha instanceof L1MonsterInstance);
            }
        }

        // ????????????????????????Pet???Summon?????????NPC????????????PC???Pet???Summon????????????
        if ((this._calcType == PC_NPC)
                && (this._target instanceof L1NpcInstance)
                && !(this._target instanceof L1PetInstance)
                && !(this._target instanceof L1SummonInstance)
                && ((cha instanceof L1PetInstance)
                        || (cha instanceof L1SummonInstance) || (cha instanceof L1PcInstance))) {
            return false;
        }

        // ??????????????????????????????????????????NPC?????????????????????????????????
        if ((this._calcType == PC_NPC)
                && (this._target instanceof L1NpcInstance)
                && !(this._target instanceof L1GuardInstance)
                && (cha instanceof L1GuardInstance)) {
            return false;
        }

        // NPC???PC???????????????????????????????????????????????????????????????????????????
        if ((this._skill.getTarget().equals("attack") || (this._skill.getType() == L1Skills.TYPE_ATTACK))
                && (this._calcType == NPC_PC)
                && !(cha instanceof L1PetInstance)
                && !(cha instanceof L1SummonInstance)
                && !(cha instanceof L1PcInstance)) {
            return false;
        }

        // NPC???NPC???????????????MOB????????????????????????MOB???????????????????????????????????????
        if ((this._skill.getTarget().equals("attack") || (this._skill.getType() == L1Skills.TYPE_ATTACK))
                && (this._calcType == NPC_NPC)
                && (this._user instanceof L1MonsterInstance)
                && (cha instanceof L1MonsterInstance)) {
            return false;
        }

        // ??????????????????????????? ????????????NPC?????????
        if (this._skill.getTarget().equals("none")
                && (this._skill.getType() == L1Skills.TYPE_ATTACK)
                && ((cha instanceof L1AuctionBoardInstance)
                        || (cha instanceof L1BoardInstance)
                        || (cha instanceof L1CrownInstance)
                        || (cha instanceof L1DwarfInstance)
                        || (cha instanceof L1EffectInstance)
                        || (cha instanceof L1FieldObjectInstance)
                        || (cha instanceof L1FurnitureInstance)
                        || (cha instanceof L1HousekeeperInstance)
                        || (cha instanceof L1MerchantInstance) || (cha instanceof L1TeleporterInstance))) {
            return false;
        }

        // ?????????????????????????????????
        if ((this._skill.getType() == L1Skills.TYPE_ATTACK)
                && (cha.getId() == this._user.getId())) {
            return false;
        }

        // ???????????????????????????????????????
        if ((cha.getId() == this._user.getId()) && (this._skillId == HEAL_ALL)) {
            return false;
        }

        if ((((this._skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC)
                || ((this._skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) || ((this._skill
                .getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY))
                && (cha.getId() == this._user.getId())
                && (this._skillId != HEAL_ALL)) {
            return true; // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        }

        // ??????????????????PC?????????PK????????????????????????????????????
        if ((this._user instanceof L1PcInstance)
                && (this._skill.getTarget().equals("attack") || (this._skill
                        .getType() == L1Skills.TYPE_ATTACK))
                && (this._isPK == false)) {
            // ??????????????????
            if (cha instanceof L1SummonInstance) {
                final L1SummonInstance summon = (L1SummonInstance) cha;
                // ??????????????????
                if (this._player.getId() == summon.getMaster().getId()) {
                    return false;
                }
            }
            // ???????????????
            else if (cha instanceof L1PetInstance) {
                final L1PetInstance pet = (L1PetInstance) cha;
                // ???????????????
                if (this._player.getId() == pet.getMaster().getId()) {
                    return false;
                }
            }
        }

        if ((this._skill.getTarget().equals("attack") || (this._skill.getType() == L1Skills.TYPE_ATTACK))
                // ??????????????????
                && !(cha instanceof L1MonsterInstance)
                // ??????PK??????
                && (this._isPK == false)
                // ???????????????
                && (this._target instanceof L1PcInstance)) {
            final L1PcInstance enemy = (L1PcInstance) cha;
            // ?????????????????????
            if ((this._skillId == COUNTER_DETECTION)
                    && (enemy.getZoneType() != 1)
                    && (cha.hasSkillEffect(INVISIBILITY) || cha
                            .hasSkillEffect(BLIND_HIDING))) {
                return true; // ????????????????????????
            }
            if ((this._player.getClanid() != 0) && (enemy.getClanid() != 0)) { // ?????????
                // ????????????????????????
                for (final L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(this._player.getClanname())) { // ??????????????????
                        if (war.CheckClanInSameWar( // ??????????????????????????????
                                this._player.getClanname(), enemy.getClanname())) {
                            if (L1CastleLocation.checkInAllWarArea(
                                    enemy.getX(), enemy.getY(),
                                    enemy.getMapId())) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false; // ??????????????????PK???????????????????????????
        }

        if ((this._user.glanceCheck(cha.getX(), cha.getY()) == false)
                && (this._skill.isThrough() == false)) {
            // ??????????????????????????????????????????
            if (!((this._skill.getType() == L1Skills.TYPE_CHANGE) || (this._skill
                    .getType() == L1Skills.TYPE_RESTORE))) {
                this._isGlanceCheckFail = true;
                return false; // ???????????????????????????
            }
        }

        if (cha.hasSkillEffect(ICE_LANCE)
                || cha.hasSkillEffect(FREEZING_BLIZZARD)
                || cha.hasSkillEffect(FREEZING_BREATH)
                || cha.hasSkillEffect(ICE_LANCE_COCKATRICE)
                || cha.hasSkillEffect(ICE_LANCE_BASILISK)) {
            if ((this._skillId == ICE_LANCE)
                    || (this._skillId == FREEZING_BLIZZARD)
                    || (this._skillId == FREEZING_BREATH)
                    || (this._skillId == ICE_LANCE_COCKATRICE)
                    || (this._skillId == ICE_LANCE_BASILISK)) {
                return false;
            }
        }

        // ??????????????????????????????
        if (cha.hasSkillEffect(EARTH_BIND) && (this._skillId == EARTH_BIND)) {
            return false;
        }

        // ?????????????????? ???????????? ???????????????
        if (!(cha instanceof L1MonsterInstance)
                && ((this._skillId == TAMING_MONSTER) || (this._skillId == CREATE_ZOMBIE))) {
            return false;
        }

        // ??????????????? ??????????????????
        if (cha.isDead()
                && ((this._skillId != CREATE_ZOMBIE)
                        && (this._skillId != RESURRECTION)
                        && (this._skillId != GREATER_RESURRECTION) && (this._skillId != CALL_OF_NATURE))) {
            return false;
        }

        // ??????????????? ???????????????
        if ((cha.isDead() == false)
                && ((this._skillId == CREATE_ZOMBIE)
                        || (this._skillId == RESURRECTION)
                        || (this._skillId == GREATER_RESURRECTION) || (this._skillId == CALL_OF_NATURE))) {
            return false;
        }

        // ??????????????????????????????
        if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance))
                && ((this._skillId == CREATE_ZOMBIE)
                        || (this._skillId == RESURRECTION)
                        || (this._skillId == GREATER_RESURRECTION) || (this._skillId == CALL_OF_NATURE))) {
            return false;
        }

        // ??????????????????????????????
        if (cha instanceof L1PcInstance) {
            final L1PcInstance pc = (L1PcInstance) cha;
            if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // ?????????????????????
                switch (this._skillId) {
                    case CURSE_BLIND:
                    case WEAPON_BREAK:
                    case DARKNESS:
                    case WEAKNESS:
                    case DISEASE:
                    case FOG_OF_SLEEPING:
                    case MASS_SLOW:
                    case SLOW:
                    case CANCELLATION:
                    case SILENCE:
                    case DECAY_POTION:
                    case MASS_TELEPORT:
                    case DETECTION:
                    case COUNTER_DETECTION:
                    case ERASE_MAGIC:
                    case ENTANGLE:
                    case PHYSICAL_ENCHANT_DEX:
                    case PHYSICAL_ENCHANT_STR:
                    case BLESS_WEAPON:
                    case EARTH_SKIN:
                    case IMMUNE_TO_HARM:
                    case REMOVE_CURSE:
                        return true;
                    default:
                        return false;
                }
            }
        }

        // ??????????????????????????????????????? (??????)
        if (cha instanceof L1NpcInstance) {
            final int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
            switch (hiddenStatus) {
                case L1NpcInstance.HIDDEN_STATUS_SINK:
                    switch (this._skillId) {
                        case DETECTION:
                        case COUNTER_DETECTION: // ?????????????????????????????????
                            return true;
                    }
                    return false;

                case L1NpcInstance.HIDDEN_STATUS_FLY: // ????????????????????????
                    return false;
            }
        }

        // ??????PC
        if (((this._skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC)
                && (cha instanceof L1PcInstance)) {
            _flg = true;
        }
        // ??????NPC
        else if (((this._skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC)
                && ((cha instanceof L1MonsterInstance)
                        || (cha instanceof L1NpcInstance)
                        || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance))) {
            _flg = true;
        }
        // ?????? ??????????????????
        else if (((this._skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET)
                && (this._user instanceof L1PcInstance)) {
            if (cha instanceof L1SummonInstance) {
                final L1SummonInstance summon = (L1SummonInstance) cha;
                if (summon.getMaster() != null) {
                    if (this._player.getId() == summon.getMaster().getId()) {
                        _flg = true;
                    }
                }
            } else if (cha instanceof L1PetInstance) {
                final L1PetInstance pet = (L1PetInstance) cha;
                if (pet.getMaster() != null) {
                    if (this._player.getId() == pet.getMaster().getId()) {
                        _flg = true;
                    }
                }
            }
        }

        if ((this._calcType == PC_PC) && (cha instanceof L1PcInstance)) {
            // ????????????
            if (((this._skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN)
                    && (((this._player.getClanid() != 0) && (this._player
                            .getClanid() == ((L1PcInstance) cha).getClanid())) || this._player
                            .isGm())) {
                return true;
            }
            // ????????????
            if (((this._skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY)
                    && (this._player.getParty().isMember((L1PcInstance) cha) || this._player
                            .isGm())) {
                return true;
            }
        }

        return _flg;
    }

    /**
     * ????????????
     * 
     * @param cha
     * @param cha
     * @return
     */
    private boolean isTargetCalc(final L1Character cha) {
        // ?????????????????????????????????????????????
        if ((this._user instanceof L1PcInstance)
                && ((this._skillId == TRIPLE_ARROW)
                        || (this._skillId == FOE_SLAYER)
                        || (this._skillId == SMASH) || (this._skillId == BONE_BREAK))) {
            return true;
        }
        // ???????????????Non???PvP??????
        if (this._skill.getTarget().equals("attack")
                && (this._skillId != TURN_UNDEAD)) { // ????????????
            if (this.isPcSummonPet(cha)) { // ?????????PC??????????????????
                if ((this._player.getZoneType() == 1)
                        || (cha.getZoneType() == 1 // ????????????????????????????????????
                        ) || this._player.checkNonPvP(this._player, cha)) { // Non-PvP??????
                    return false;
                }
            }
        }

        // ??????????????????????????????
        if ((this._skillId == FOG_OF_SLEEPING)
                && (this._user.getId() == cha.getId())) {
            return false;
        }

        // ???????????????????????????????????????
        if (this._skillId == MASS_SLOW) {
            if (this._user.getId() == cha.getId()) {
                return false;
            }
            if (cha instanceof L1SummonInstance) {
                final L1SummonInstance summon = (L1SummonInstance) cha;
                if (this._user.getId() == summon.getMaster().getId()) {
                    return false;
                }
            } else if (cha instanceof L1PetInstance) {
                final L1PetInstance pet = (L1PetInstance) cha;
                if (this._user.getId() == pet.getMaster().getId()) {
                    return false;
                }
            }
        }

        // ??????????????????????????????????????????????????????????????????
        if (this._skillId == MASS_TELEPORT) {
            if (this._user.getId() != cha.getId()) {
                return false;
            }
        }

        return true;
    }

    /**
     * ??????????????????
     * 
     * @param cha
     */
    private boolean isTargetFailure(final L1Character cha) {
        boolean isTU = false;
        boolean isErase = false;
        boolean isManaDrain = false;
        int undeadType = 0;

        if ((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance)) { // ???????????????
                                                                                   // ?????????????????????
            return true;
        }

        if (cha instanceof L1PcInstance) { // ???PC?????????
            if ((this._calcType == PC_PC)
                    && this._player.checkNonPvP(this._player, cha)) { // Non-PvP??????
                final L1PcInstance pc = (L1PcInstance) cha;
                if ((this._player.getId() == pc.getId())
                        || ((pc.getClanid() != 0) && (this._player.getClanid() == pc
                                .getClanid()))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        if (cha instanceof L1MonsterInstance) { // ???????????????????????????????????????
            isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
        }

        if (cha instanceof L1MonsterInstance) { // ???????????????????????????????????????
            isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
        }

        if (cha instanceof L1MonsterInstance) { // ????????????????????????
            undeadType = ((L1MonsterInstance) cha).getNpcTemplate()
                    .get_undead();
        }

        // ?????????????????????????????????
        if (cha instanceof L1MonsterInstance) {
            isManaDrain = true;
        }
        /*
         * ????????????????????????T-U???????????????????????????????????????????????????????????? ????????????????????????T-U??????????????????????????????????????????????????????????????????
         * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * ?????????????????????????????????????????????????????????????????????????????????????????????
         */
        if (((this._skillId == TURN_UNDEAD) && ((undeadType == 0) || (undeadType == 2)))
                || ((this._skillId == TURN_UNDEAD) && (isTU == false))
                || (((this._skillId == ERASE_MAGIC) || (this._skillId == SLOW)
                        || (this._skillId == MANA_DRAIN)
                        || (this._skillId == MASS_SLOW)
                        || (this._skillId == ENTANGLE) || (this._skillId == WIND_SHACKLE)) && (isErase == false))
                || ((this._skillId == MANA_DRAIN) && (isManaDrain == false))) {
            return true;
        }
        return false;
    }

    /**
     * ????????????????????????
     * 
     * @param cha
     */
    private boolean isUseCounterMagic(final L1Character cha) {
        if (this._isCounterMagic && cha.hasSkillEffect(COUNTER_MAGIC)) {
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

    /**
     * ???????????? ??????????????????
     */
    private void makeTargetList() {
        try {
            if (this._type == TYPE_LOGIN) { // ?????????????????? (????????? ???????????????????????????)
                this._targetList.add(new TargetStatus(this._user));
                return;
            }
            if ((this._skill.getTargetTo() == L1Skills.TARGET_TO_ME)
                    && ((this._skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK)) {
                this._targetList.add(new TargetStatus(this._user)); // ???????????????
                return;
            }

            // ?????????????????? (??????????????????)
            if (this.getSkillRanged() != -1) {
                if (this._user.getLocation().getTileLineDistance(
                        this._target.getLocation()) > this.getSkillRanged()) {
                    return; // ???????????????
                }
            } else {
                if (!this._user.getLocation().isInScreen(
                        this._target.getLocation())) {
                    return; // ???????????????
                }
            }

            if ((this.isTarget(this._target) == false)
                    && !(this._skill.getTarget().equals("none"))) {
                // ???????????????????????????????????????????????????
                return;
            }

            // ???????????????
            switch (this._skillId) {
                case LIGHTNING:
                case FREEZING_BREATH: // ???????????????????????????
                    final List<L1Object> al1object = L1World.getInstance()
                            .getVisibleLineObjects(this._user, this._target);

                    for (final L1Object tgobj : al1object) {
                        if (tgobj == null) {
                            continue;
                        }
                        if (!(tgobj instanceof L1Character)) { // ??????????????????????????????
                            continue;
                        }
                        final L1Character cha = (L1Character) tgobj;
                        if (this.isTarget(cha) == false) {
                            continue;
                        }
                        this._targetList.add(new TargetStatus(cha));
                    }
                    return;
            }

            // ????????????
            if (this.getSkillArea() == 0) {
                if (!this._user.glanceCheck(this._target.getX(),
                        this._target.getY())) { // ?????????????????????
                    if (((this._skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK)
                            && (this._skillId != 10026)
                            && (this._skillId != 10027)
                            && (this._skillId != 10028)
                            && (this._skillId != 10029)) { // ????????????????????????????????????
                        this._targetList.add(new TargetStatus(this._target,
                                false)); // ??????????????????????????????????????????????????????????????????????????????????????????
                        return;
                    }
                }
                this._targetList.add(new TargetStatus(this._target));
            }

            // ????????????
            else {
                if (!this._skill.getTarget().equals("none")) {
                    this._targetList.add(new TargetStatus(this._target));
                }

                // ???????????? (???????????????)
                if ((this._skillId != HEAL_ALL)
                        && !(this._skill.getTarget().equals("attack") || (this._skill
                                .getType() == L1Skills.TYPE_ATTACK))) {
                    // ??????????????????????????????H-A??????????????????????????????????????????
                    this._targetList.add(new TargetStatus(this._user));
                }

                List<L1Object> objects;

                // ???????????????
                if (this.getSkillArea() == -1) {
                    objects = L1World.getInstance().getVisibleObjects(
                            this._user);
                }
                // ??????????????????
                else {
                    objects = L1World.getInstance().getVisibleObjects(
                            this._target, this.getSkillArea());
                }
                for (final L1Object tgobj : objects) {
                    if (tgobj == null) {
                        continue;
                    }
                    if (!(tgobj instanceof L1Character)) { // ?????????????????????????????????????????????????????????????????????
                        continue;
                    }
                    final L1Character cha = (L1Character) tgobj;
                    if (!this.isTarget(cha)) {
                        continue;
                    }

                    // ???????????? ??????????????????:?????????????????? - ??????
                    this._targetList.add(new TargetStatus(cha));
                }
                return;
            }

        } catch (final Exception e) {
            _log.log(Level.FINEST, "exception in L1Skilluse makeTargetList{0}",
                    e);
        }
    }

    /**
     * ??????????????????
     */
    private void runSkill() {

        switch (this._skillId) {
            case LIFE_STREAM: // ??????????????????
                L1EffectSpawn.getInstance().spawnEffect(81169,
                        this._skill.getBuffDuration() * 1000, this._targetX,
                        this._targetY, this._user.getMapId());
                return;
            case CUBE_IGNITION: // ??????:??????
                L1EffectSpawn.getInstance().spawnEffect(80149,
                        this._skill.getBuffDuration() * 1000, this._targetX,
                        this._targetY, this._user.getMapId(),
                        (L1PcInstance) this._user, this._skillId);
                return;
            case CUBE_QUAKE: // ??????:??????
                L1EffectSpawn.getInstance().spawnEffect(80150,
                        this._skill.getBuffDuration() * 1000, this._targetX,
                        this._targetY, this._user.getMapId(),
                        (L1PcInstance) this._user, this._skillId);
                return;
            case CUBE_SHOCK: // ??????:??????
                L1EffectSpawn.getInstance().spawnEffect(80151,
                        this._skill.getBuffDuration() * 1000, this._targetX,
                        this._targetY, this._user.getMapId(),
                        (L1PcInstance) this._user, this._skillId);
                return;
            case CUBE_BALANCE: // ??????:??????
                L1EffectSpawn.getInstance().spawnEffect(80152,
                        this._skill.getBuffDuration() * 1000, this._targetX,
                        this._targetY, this._user.getMapId(),
                        (L1PcInstance) this._user, this._skillId);
                return;
            case FIRE_WALL: // ??????
                L1EffectSpawn.getInstance().doSpawnFireWall(this._user,
                        this._targetX, this._targetY);
                return;
            case BLKS_FIRE_WALL: // ??????????????????
                this._user.setSkillEffect(this._skillId, 11 * 1000);
                L1EffectSpawn.getInstance().doSpawnFireWallforNpc(this._user,
                        this._target);
                return;
            case TRUE_TARGET: // ????????????
                if (this._user instanceof L1PcInstance) {
                    final L1PcInstance pri = (L1PcInstance) this._user;
                    final L1EffectInstance effect = L1EffectSpawn.getInstance()
                            .spawnEffect(80153, 5 * 1000, this._targetX + 2,
                                    this._targetY - 1, this._user.getMapId());
                    if (this._targetID != 0) {
                        pri.sendPackets(new S_TrueTarget(this._targetID, pri
                                .getId(), this._message));
                        if (pri.getClanid() != 0) {
                            final L1PcInstance players[] = L1World
                                    .getInstance().getClan(pri.getClanname())
                                    .getOnlineClanMember();
                            for (final L1PcInstance pc : players) {
                                pc.sendPackets(new S_TrueTarget(this._targetID,
                                        pc.getId(), this._message));
                            }
                        }
                    } else if (effect != null) {
                        pri.sendPackets(new S_TrueTarget(effect.getId(), pri
                                .getId(), this._message));
                        if (pri.getClanid() != 0) {
                            final L1PcInstance players[] = L1World
                                    .getInstance().getClan(pri.getClanname())
                                    .getOnlineClanMember();
                            for (final L1PcInstance pc : players) {
                                pc.sendPackets(new S_TrueTarget(effect.getId(),
                                        pc.getId(), this._message));
                            }
                        }
                    }
                }
                return;
            default:
                break;
        }

        // ?????????????????????????????????
        for (final int skillId : EXCEPT_COUNTER_MAGIC) {
            if (this._skillId == skillId) {
                this._isCounterMagic = false; // ??????
                break;
            }
        }

        // NPC?????????????????????????????????????????????onAction???NullPointerException?????????????????????
        // ???????????????PC????????????????????????
        if ((this._skillId == SHOCK_STUN)
                && (this._user instanceof L1PcInstance)) {
            this._target.onAction(this._player);
        }

        if (!this.isTargetCalc(this._target)) {
            return;
        }

        try {
            TargetStatus ts = null;
            L1Character cha = null;
            int dmg = 0;
            int drainMana = 0;
            int heal = 0;
            boolean isSuccess = false;
            int undeadType = 0;

            for (final Iterator<TargetStatus> iter = this._targetList
                    .iterator(); iter.hasNext();) {
                ts = null;
                cha = null;
                dmg = 0;
                heal = 0;
                isSuccess = false;
                undeadType = 0;

                ts = iter.next();
                cha = ts.getTarget();

                if (!ts.isCalc() || !this.isTargetCalc(cha)) {
                    continue; // ??????????????????????????????
                }

                final L1Magic _magic = new L1Magic(this._user, cha);
                _magic.setLeverage(this.getLeverage());

                if (cha instanceof L1MonsterInstance) { // ???????????????
                    undeadType = ((L1MonsterInstance) cha).getNpcTemplate()
                            .get_undead();
                }

                // ???????????????????????????
                if (((this._skill.getType() == L1Skills.TYPE_CURSE) || (this._skill
                        .getType() == L1Skills.TYPE_PROBABILITY))
                        && this.isTargetFailure(cha)) {
                    iter.remove();
                    continue;
                }

                if (cha instanceof L1PcInstance) { // ??????????????????PC????????????????????????????????????????????????
                    if (this._skillTime == 0) {
                        this._getBuffIconDuration = this._skill
                                .getBuffDuration(); // ????????????
                    } else {
                        this._getBuffIconDuration = this._skillTime; // ??????????????????time???0????????????????????????????????????????????????
                    }
                }

                this.deleteRepeatedSkills(cha); // ???????????????????????????????????????

                // ????????????????????????????????????
                if ((this._skill.getType() == L1Skills.TYPE_ATTACK)
                        && (this._user.getId() != cha.getId())) {
                    if (this.isUseCounterMagic(cha)) { // ????????????????????????????????????????????????????????????????????????
                        iter.remove();
                        continue;
                    }
                    dmg = _magic.calcMagicDamage(this._skillId);
                    this._dmg = dmg;
                    cha.removeSkillEffect(ERASE_MAGIC); // ?????????????????????????????????????????????????????????
                } else if ((this._skill.getType() == L1Skills.TYPE_CURSE)
                        || (this._skill.getType() == L1Skills.TYPE_PROBABILITY)) { // ??????????????????
                    isSuccess = _magic.calcProbabilityMagic(this._skillId);
                    if (this._skillId != ERASE_MAGIC) {
                        cha.removeSkillEffect(ERASE_MAGIC); // ?????????????????????????????????????????????????????????
                    }
                    if (this._skillId != FOG_OF_SLEEPING) {
                        cha.removeSkillEffect(FOG_OF_SLEEPING); // ??????????????????????????????????????????????????????????????????
                    }
                    if (isSuccess) { // ???????????????????????????????????????????????????????????????????????????????????????
                        if (this.isUseCounterMagic(cha)) { // ?????????????????????????????????????????????
                            iter.remove();
                            continue;
                        }
                    } else { // ??????????????????????????????????????????
                        if ((this._skillId == FOG_OF_SLEEPING)
                                && (cha instanceof L1PcInstance)) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_ServerMessage(297)); // ???????????????????????????
                        }
                        iter.remove();
                        continue;
                    }
                }

                // ???????????????
                else if (this._skill.getType() == L1Skills.TYPE_HEAL) {
                    // ?????????
                    dmg = -1 * _magic.calcHealing(this._skillId);
                    if (cha.hasSkillEffect(WATER_LIFE)) { // ????????????-?????? 2???
                        dmg *= 2;
                        cha.killSkillEffectTimer(WATER_LIFE); // ??????????????????
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_SkillIconWaterLife());
                        }
                    }
                    if (cha.hasSkillEffect(POLLUTE_WATER)) { // ????????????-????????????
                        dmg /= 2;
                    }
                }
                // ??????????????????????????????????????????
                else if (((this._skillId == FIRE_BLESS)
                        || (this._skillId == STORM_EYE // ???????????????????????????
                        ) || (this._skillId == EARTH_BLESS // ???????????????
                        ) || (this._skillId == GLOWING_AURA // ????????????
                        ) || (this._skillId == SHINING_AURA) || (this._skillId == BRAVE_AURA)) // ???????????????????????????
                        && (this._user.getId() != cha.getId())) {
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance _targetPc = (L1PcInstance) cha;
                        _targetPc.sendPackets(new S_SkillSound(_targetPc
                                .getId(), this._skill.getCastGfx()));
                        _targetPc.broadcastPacket(new S_SkillSound(_targetPc
                                .getId(), this._skill.getCastGfx()));
                    }
                }

                // ???????????? ???????????????????????????????????????????????????????????? ????????????

                // ??????????????????????????????????????????????????????????????????????????????????????????
                if (cha.hasSkillEffect(this._skillId)
                        && ((this._skillId != SHOCK_STUN)
                                && (this._skillId != BONE_BREAK)
                                && (this._skillId != CONFUSION) && (this._skillId != THUNDER_GRAB))) {
                    this.addMagicList(cha, true); // ????????????????????????
                    if (this._skillId != SHAPE_CHANGE) { // ?????????????????????
                        continue;
                    }
                }

                switch (this._skillId) {
                    // ?????????
                    case HASTE:
                        if (cha.getMoveSpeed() != 2) { // ???????????????
                            if (cha instanceof L1PcInstance) {
                                final L1PcInstance pc = (L1PcInstance) cha;
                                if (pc.getHasteItemEquipped() > 0) {
                                    continue;
                                }
                                pc.setDrink(false);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
                                        this._getBuffIconDuration));
                            }
                            cha.broadcastPacket(new S_SkillHaste(cha.getId(),
                                    1, 0));
                            cha.setMoveSpeed(1);
                        } else { // ?????????
                            int skillNum = 0;
                            if (cha.hasSkillEffect(SLOW)) {
                                skillNum = SLOW;
                            } else if (cha.hasSkillEffect(MASS_SLOW)) {
                                skillNum = MASS_SLOW;
                            } else if (cha.hasSkillEffect(ENTANGLE)) {
                                skillNum = ENTANGLE;
                            }
                            if (skillNum != 0) {
                                cha.removeSkillEffect(skillNum);
                                cha.removeSkillEffect(HASTE);
                                cha.setMoveSpeed(0);
                                continue;
                            }
                        }
                        break;
                    // ???????????????
                    case GREATER_HASTE:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                            }
                            if (pc.getMoveSpeed() != 2) { // ???????????????
                                pc.setDrink(false);
                                pc.setMoveSpeed(1);
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
                                        this._getBuffIconDuration));
                                pc.broadcastPacket(new S_SkillHaste(pc.getId(),
                                        1, 0));
                            } else { // ?????????
                                int skillNum = 0;
                                if (pc.hasSkillEffect(SLOW)) {
                                    skillNum = SLOW;
                                } else if (pc.hasSkillEffect(MASS_SLOW)) {
                                    skillNum = MASS_SLOW;
                                } else if (pc.hasSkillEffect(ENTANGLE)) {
                                    skillNum = ENTANGLE;
                                }
                                if (skillNum != 0) {
                                    pc.removeSkillEffect(skillNum);
                                    pc.removeSkillEffect(GREATER_HASTE);
                                    pc.setMoveSpeed(0);
                                    continue;
                                }
                            }
                        }
                        break;
                    // ??????????????????????????????????????????
                    case SLOW:
                    case MASS_SLOW:
                    case ENTANGLE:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getHasteItemEquipped() > 0) {
                                continue;
                            }
                        }
                        if (cha.getMoveSpeed() == 0) {
                            if (cha instanceof L1PcInstance) {
                                final L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_SkillHaste(pc.getId(), 2,
                                        this._getBuffIconDuration));
                            }
                            cha.broadcastPacket(new S_SkillHaste(cha.getId(),
                                    2, this._getBuffIconDuration));
                            cha.setMoveSpeed(2);
                        } else if (cha.getMoveSpeed() == 1) {
                            int skillNum = 0;
                            if (cha.hasSkillEffect(HASTE)) {
                                skillNum = HASTE;
                            } else if (cha.hasSkillEffect(GREATER_HASTE)) {
                                skillNum = GREATER_HASTE;
                            } else if (cha.hasSkillEffect(STATUS_HASTE)) {
                                skillNum = STATUS_HASTE;
                            }
                            if (skillNum != 0) {
                                cha.removeSkillEffect(skillNum);
                                cha.removeSkillEffect(this._skillId);
                                cha.setMoveSpeed(0);
                                continue;
                            }
                        }
                        break;
                    // ??????????????????????????????
                    case CHILL_TOUCH:
                    case VAMPIRIC_TOUCH:
                        heal = dmg;
                        break;
                    // ?????????????????????
                    case ICE_LANCE_COCKATRICE:
                        // ????????????????????????
                    case ICE_LANCE_BASILISK:
                        // ??????????????????????????????????????????
                    case ICE_LANCE:
                    case FREEZING_BLIZZARD:
                    case FREEZING_BREATH:
                        this._isFreeze = _magic
                                .calcProbabilityMagic(this._skillId);
                        if (this._isFreeze) {
                            final int time = this._skill.getBuffDuration() * 1000;
                            L1EffectSpawn.getInstance().spawnEffect(81168,
                                    time, cha.getX(), cha.getY(),
                                    cha.getMapId());
                            if (cha instanceof L1PcInstance) {
                                final L1PcInstance pc = (L1PcInstance) cha;
                                pc.sendPackets(new S_Poison(pc.getId(), 2));
                                pc.broadcastPacket(new S_Poison(pc.getId(), 2));
                                pc.sendPackets(new S_Paralysis(
                                        S_Paralysis.TYPE_FREEZE, true));
                            } else if ((cha instanceof L1MonsterInstance)
                                    || (cha instanceof L1SummonInstance)
                                    || (cha instanceof L1PetInstance)) {
                                final L1NpcInstance npc = (L1NpcInstance) cha;
                                npc.broadcastPacket(new S_Poison(npc.getId(), 2));
                                npc.setParalyzed(true);
                                npc.setParalysisTime(time);
                            }
                        }
                        break;
                    // ????????????
                    case EARTH_BIND:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_Poison(pc.getId(), 2));
                            pc.broadcastPacket(new S_Poison(pc.getId(), 2));
                            pc.sendPackets(new S_Paralysis(
                                    S_Paralysis.TYPE_FREEZE, true));
                        } else if ((cha instanceof L1MonsterInstance)
                                || (cha instanceof L1SummonInstance)
                                || (cha instanceof L1PetInstance)) {
                            final L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.broadcastPacket(new S_Poison(npc.getId(), 2));
                            npc.setParalyzed(true);
                            npc.setParalysisTime(this._skill.getBuffDuration() * 1000);
                        }
                        break;
                    case POISON_SMOG: // ??????-?????? 3X3
                        this._user.setHeading(this._user.targetDirection(
                                this._targetX, this._targetY)); // ????????????
                        int locX = 0;
                        int locY = 0;
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                switch (this._user.getHeading()) {
                                    case 0:
                                        locX = (-1 + j);
                                        locY = -1 * (-3 + i);
                                        break;
                                    case 1:
                                        locX = -1 * (2 + j - i);
                                        locY = -1 * (-4 + j + i);
                                        break;
                                    case 2:
                                        locX = -1 * (3 - i);
                                        locY = (-1 + j);
                                        break;
                                    case 3:
                                        locX = -1 * (4 - j - i);
                                        locY = -1 * (2 + j - i);
                                        break;
                                    case 4:
                                        locX = (1 - j);
                                        locY = -1 * (3 - i);
                                        break;
                                    case 5:
                                        locX = -1 * (-2 - j + i);
                                        locY = -1 * (4 - j - i);
                                        break;
                                    case 6:
                                        locX = -1 * (-3 + i);
                                        locY = (1 - j);
                                        break;
                                    case 7:
                                        locX = -1 * (-4 + j + i);
                                        locY = -1 * (-2 - j + i);
                                        break;
                                }
                                L1EffectSpawn.getInstance().spawnEffect(93002,
                                        10000, this._user.getX() - locX,
                                        this._user.getY() - locY,
                                        this._user.getMapId());
                            }
                        }
                        break;
                    // ????????????
                    case SHOCK_STUN:
                        final int[] stunTimeArray = { 500, 1000, 1500, 2000,
                                2500, 3000 };
                        final int rnd = Random.nextInt(stunTimeArray.length);
                        this._shockStunDuration = stunTimeArray[rnd];
                        if ((cha instanceof L1PcInstance)
                                && cha.hasSkillEffect(SHOCK_STUN)) {
                            this._shockStunDuration += cha
                                    .getSkillEffectTimeSec(SHOCK_STUN) * 1000;
                        }

                        L1EffectSpawn.getInstance().spawnEffect(81162,
                                this._shockStunDuration, cha.getX(),
                                cha.getY(), cha.getMapId());
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            pc.sendPackets(new S_Paralysis(
                                    S_Paralysis.TYPE_STUN, true));
                        } else if ((cha instanceof L1MonsterInstance)
                                || (cha instanceof L1SummonInstance)
                                || (cha instanceof L1PetInstance)) {
                            final L1NpcInstance npc = (L1NpcInstance) cha;
                            npc.setParalyzed(true);
                            npc.setParalysisTime(this._shockStunDuration);
                        }
                        break;
                    // ????????????
                    case THUNDER_GRAB:
                        isSuccess = _magic.calcProbabilityMagic(this._skillId);
                        if (isSuccess) {
                            if (!cha.hasSkillEffect(THUNDER_GRAB_START)
                                    && !cha.hasSkillEffect(STATUS_FREEZE)) {
                                if (cha instanceof L1PcInstance) {
                                    final L1PcInstance pc = (L1PcInstance) cha;
                                    pc.sendPackets(new S_Paralysis(
                                            S_Paralysis.TYPE_BIND, true));
                                    pc.sendPackets(new S_SkillSound(pc.getId(),
                                            4184));
                                    pc.broadcastPacket(new S_SkillSound(pc
                                            .getId(), 4184));
                                } else if (cha instanceof L1NpcInstance) {
                                    final L1NpcInstance npc = (L1NpcInstance) cha;
                                    npc.setParalyzed(true);
                                    npc.broadcastPacket(new S_SkillSound(npc
                                            .getId(), 4184));
                                }
                                cha.setSkillEffect(THUNDER_GRAB_START, 500);
                            }
                        }
                        break;
                    // ???????????????
                    case TURN_UNDEAD:
                        if ((undeadType == 1) || (undeadType == 3)) {
                            dmg = cha.getCurrentHp();
                        }
                        break;
                    // ????????????
                    case MANA_DRAIN:
                        final int chance = Random.nextInt(10) + 5;
                        drainMana = chance + (this._user.getInt() / 2);
                        if (cha.getCurrentMp() < drainMana) {
                            drainMana = cha.getCurrentMp();
                        }
                        break;
                    // ??????????????????????????????
                    case TELEPORT:
                    case MASS_TELEPORT:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            final L1BookMark bookm = pc
                                    .getBookMark(this._bookmarkId);
                            if (bookm != null) { // ????????????????????????????????????
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    final int newX = bookm.getLocX();
                                    final int newY = bookm.getLocY();
                                    final short mapId = bookm.getMapId();

                                    if (this._skillId == MASS_TELEPORT) { // ???????????????
                                        final List<L1PcInstance> clanMember = L1World
                                                .getInstance()
                                                .getVisiblePlayer(pc);
                                        for (final L1PcInstance member : clanMember) {
                                            if ((pc.getLocation()
                                                    .getTileLineDistance(
                                                            member.getLocation()) <= 3)
                                                    && (member.getClanid() == pc
                                                            .getClanid())
                                                    && (pc.getClanid() != 0)
                                                    && (member.getId() != pc
                                                            .getId())) {
                                                L1Teleport.teleport(member,
                                                        newX, newY, mapId, 5,
                                                        true);
                                            }
                                        }
                                    }
                                    L1Teleport.teleport(pc, newX, newY, mapId,
                                            5, true);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(79));
                                    pc.sendPackets(new S_Paralysis(
                                            S_Paralysis.TYPE_TELEPORT_UNLOCK,
                                            true));
                                }
                            } else { // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                if (pc.getMap().isTeleportable() || pc.isGm()) {
                                    final L1Location newLocation = pc
                                            .getLocation().randomLocation(200,
                                                    true);
                                    final int newX = newLocation.getX();
                                    final int newY = newLocation.getY();
                                    final short mapId = (short) newLocation
                                            .getMapId();

                                    if (this._skillId == MASS_TELEPORT) {
                                        final List<L1PcInstance> clanMember = L1World
                                                .getInstance()
                                                .getVisiblePlayer(pc);
                                        for (final L1PcInstance member : clanMember) {
                                            if ((pc.getLocation()
                                                    .getTileLineDistance(
                                                            member.getLocation()) <= 3)
                                                    && (member.getClanid() == pc
                                                            .getClanid())
                                                    && (pc.getClanid() != 0)
                                                    && (member.getId() != pc
                                                            .getId())) {
                                                L1Teleport.teleport(member,
                                                        newX, newY, mapId, 5,
                                                        true);
                                            }
                                        }
                                    }
                                    L1Teleport.teleport(pc, newX, newY, mapId,
                                            5, true);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(276)); // \f1???????????????????????????
                                    pc.sendPackets(new S_Paralysis(
                                            S_Paralysis.TYPE_TELEPORT_UNLOCK,
                                            true));
                                }
                            }
                        }
                        break;
                    // ????????????
                    case CALL_CLAN:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            final L1PcInstance clanPc = (L1PcInstance) L1World
                                    .getInstance().findObject(this._targetID);
                            if (clanPc != null) {
                                clanPc.setTempID(pc.getId());
                                clanPc.sendPackets(new S_Message_YN(729, "")); // ??????????????????????????????????????????????????????(Y/N)
                            }
                        }
                        break;
                    // ????????????
                    case RUN_CLAN:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            final L1PcInstance clanPc = (L1PcInstance) L1World
                                    .getInstance().findObject(this._targetID);
                            if (clanPc != null) {
                                if (pc.getMap().isEscapable() || pc.isGm()) {
                                    final boolean castle_area = L1CastleLocation
                                            .checkInAllWarArea(clanPc.getX(),
                                                    clanPc.getY(),
                                                    clanPc.getMapId());
                                    if (((clanPc.getMapId() == 0)
                                            || (clanPc.getMapId() == 4) || (clanPc
                                            .getMapId() == 304))
                                            && (castle_area == false)) {
                                        L1Teleport.teleport(pc, clanPc.getX(),
                                                clanPc.getY(),
                                                clanPc.getMapId(), 5, true);
                                    } else {
                                        pc.sendPackets(new S_ServerMessage(79));
                                    }
                                } else {
                                    // ??????????????????????????????????????????????????????????????????????????????
                                    pc.sendPackets(new S_ServerMessage(647));
                                    pc.sendPackets(new S_Paralysis(
                                            S_Paralysis.TYPE_TELEPORT_UNLOCK,
                                            true));
                                }
                            }
                        }
                        break;
                    // ??????????????????
                    case COUNTER_DETECTION:
                        if (cha instanceof L1PcInstance) {
                            dmg = _magic.calcMagicDamage(this._skillId);
                        } else if (cha instanceof L1NpcInstance) {
                            final L1NpcInstance npc = (L1NpcInstance) cha;
                            final int hiddenStatus = npc.getHiddenStatus();
                            if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
                                npc.appearOnGround(this._player);
                            } else {
                                dmg = 0;
                            }
                        } else {
                            dmg = 0;
                        }
                        break;
                    // ??????????????????
                    case CREATE_MAGICAL_WEAPON:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            final L1ItemInstance item = pc.getInventory()
                                    .getItem(this._itemobjid);
                            if ((item != null)
                                    && (item.getItem().getType2() == 1)) {
                                final int item_type = item.getItem().getType2();
                                final int safe_enchant = item.getItem()
                                        .get_safeenchant();
                                final int enchant_level = item
                                        .getEnchantLevel();
                                String item_name = item.getName();
                                if (safe_enchant < 0) { // ????????????
                                    pc.sendPackets(new S_ServerMessage(79)); // \f1???????????????????????????
                                } else if (safe_enchant == 0) { // ?????????+0
                                    pc.sendPackets(new S_ServerMessage(79)); // \f1???????????????????????????
                                } else if ((item_type == 1)
                                        && (enchant_level == 0)) {
                                    if (!item.isIdentified()) { // ?????????
                                        pc.sendPackets(new S_ServerMessage(161,
                                                item_name, "$245", "$247")); // \f1%0%s
                                                                             // %2
                                                                             // %1
                                                                             // ?????????
                                    } else {
                                        item_name = "+0 " + item_name;
                                        pc.sendPackets(new S_ServerMessage(161,
                                                "+0 " + item_name, "$245",
                                                "$247")); // \f1%0%s
                                                          // %2
                                                          // %1
                                                          // ?????????
                                    }
                                    item.setEnchantLevel(1);
                                    pc.getInventory().updateItem(item,
                                            L1PcInventory.COL_ENCHANTLVL);
                                } else {
                                    pc.sendPackets(new S_ServerMessage(79)); // \f1???????????????????????????
                                }
                            } else {
                                pc.sendPackets(new S_ServerMessage(79)); // \f1???????????????????????????
                            }
                        }
                        break;
                    // ????????????
                    case BRING_STONE:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            final L1ItemInstance item = pc.getInventory()
                                    .getItem(this._itemobjid);
                            if (item != null) {
                                final int dark = (int) (10 + (pc.getLevel() * 0.8) + (pc
                                        .getWis() - 6) * 1.2);
                                final int brave = (int) (dark / 2.1);
                                final int wise = (int) (brave / 2.0);
                                final int kayser = (int) (wise / 1.9);
                                final int run = Random.nextInt(100) + 1;
                                final int oldItem = item.getItem().getItemId();
                                switch (oldItem) {
                                    case 40320: // ???????????????
                                        pc.getInventory().removeItem(item, 1);
                                        if (dark >= run) {
                                            pc.getInventory().storeItem(40321,
                                                    1);
                                            pc.sendPackets(new S_ServerMessage(
                                                    403, "$2475")); // ??????%0%o ???
                                        } else {
                                            pc.sendPackets(new S_ServerMessage(
                                                    280)); // \f1???????????????
                                        }
                                        break;

                                    case 40321: // ???????????????
                                        pc.getInventory().removeItem(item, 1);
                                        if (brave >= run) {
                                            pc.getInventory().storeItem(40322,
                                                    1);
                                            pc.sendPackets(new S_ServerMessage(
                                                    403, "$2476")); // ??????%0%o ???
                                        } else {
                                            pc.sendPackets(new S_ServerMessage(
                                                    280));// \f1???????????????
                                        }
                                        break;

                                    case 40322: // ???????????????
                                        pc.getInventory().removeItem(item, 1);
                                        if (wise >= run) {
                                            pc.getInventory().storeItem(40323,
                                                    1);
                                            pc.sendPackets(new S_ServerMessage(
                                                    403, "$2477")); // ??????%0%o ???
                                        } else {
                                            pc.sendPackets(new S_ServerMessage(
                                                    280));// \f1???????????????
                                        }
                                        break;

                                    case 40323: // ???????????????
                                        pc.getInventory().removeItem(item, 1);
                                        if (kayser >= run) {
                                            pc.getInventory().storeItem(40324,
                                                    1);
                                            pc.sendPackets(new S_ServerMessage(
                                                    403, "$2478")); // ??????%0%o ???
                                        } else {
                                            pc.sendPackets(new S_ServerMessage(
                                                    280));// \f1???????????????
                                        }
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }
                        break;
                    // ?????????
                    case LIGHT:
                        if (cha instanceof L1PcInstance) {
                        }
                        break;
                    // ????????????
                    case SHADOW_FANG:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            final L1ItemInstance item = pc.getInventory()
                                    .getItem(this._itemobjid);
                            if ((item != null)
                                    && (item.getItem().getType2() == 1)) {
                                item.setSkillWeaponEnchant(pc, this._skillId,
                                        this._skill.getBuffDuration() * 1000);
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                        break;
                    // ??????????????????
                    case ENCHANT_WEAPON:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            final L1ItemInstance item = pc.getInventory()
                                    .getItem(this._itemobjid);
                            if ((item != null)
                                    && (item.getItem().getType2() == 1)) {
                                pc.sendPackets(new S_ServerMessage(161, item
                                        .getLogName(), "$245", "$247"));
                                item.setSkillWeaponEnchant(pc, this._skillId,
                                        this._skill.getBuffDuration() * 1000);
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                        break;
                    // ?????????????????????????????????
                    case HOLY_WEAPON:
                    case BLESS_WEAPON:
                        if (cha instanceof L1PcInstance) {
                            if (!(cha instanceof L1PcInstance)) {
                                return;
                            }
                            final L1PcInstance pc = (L1PcInstance) cha;
                            if (pc.getWeapon() == null) {
                                pc.sendPackets(new S_ServerMessage(79));
                                return;
                            }
                            for (final L1ItemInstance item : pc.getInventory()
                                    .getItems()) {
                                if (pc.getWeapon().equals(item)) {
                                    pc.sendPackets(new S_ServerMessage(161,
                                            item.getLogName(), "$245", "$247"));
                                    item.setSkillWeaponEnchant(
                                            pc,
                                            this._skillId,
                                            this._skill.getBuffDuration() * 1000);
                                    return;
                                }
                            }
                        }
                        break;
                    // ????????????
                    case BLESSED_ARMOR:
                        if (cha instanceof L1PcInstance) {
                            final L1PcInstance pc = (L1PcInstance) cha;
                            final L1ItemInstance item = pc.getInventory()
                                    .getItem(this._itemobjid);
                            if ((item != null)
                                    && (item.getItem().getType2() == 2)
                                    && (item.getItem().getType() == 2)) {
                                pc.sendPackets(new S_ServerMessage(161, item
                                        .getLogName(), "$245", "$247"));
                                item.setSkillArmorEnchant(pc, this._skillId,
                                        this._skill.getBuffDuration() * 1000);
                            } else {
                                pc.sendPackets(new S_ServerMessage(79));
                            }
                        }
                        break;
                    default:
                        // L1BuffUtil.skillEffect(this._user, cha, this._target,
                        // this._skillId, this._getBuffIconDuration, dmg);
                        final List<?> queue = Producer.useRequests();
                        for (final Object name : queue) {
                            ((L1SkillEffect) name).execute(this._user, cha,
                                    this._target, this._skillId,
                                    this._getBuffIconDuration, dmg);
                        }
                        break;
                }

                // ???????????? ?????????????????????????????? ????????????

                // ??????????????????????????????????????????
                if ((this._skill.getType() == L1Skills.TYPE_HEAL)
                        && (this._calcType == PC_NPC) && (undeadType == 1)) {
                    dmg *= -1;
                }
                // ?????????????????????????????????????????????
                if ((this._skill.getType() == L1Skills.TYPE_HEAL)
                        && (this._calcType == PC_NPC) && (undeadType == 3)) {
                    dmg = 0;
                }
                // ?????????????????????????????????
                if (((cha instanceof L1TowerInstance) || (cha instanceof L1DoorInstance))
                        && (dmg < 0)) {
                    dmg = 0;
                }
                // ???????????????
                if ((dmg > 0) || (drainMana != 0)) {
                    _magic.commit(dmg, drainMana);
                }
                // ????????????
                if ((this._skill.getType() == L1Skills.TYPE_HEAL) && (dmg < 0)) {
                    cha.setCurrentHp((dmg * -1) + cha.getCurrentHp());
                }
                // ??????????????????????????????(??????????????????)
                if (heal > 0) {
                    this._user.setCurrentHp(heal + this._user.getCurrentHp());
                }

                if (cha instanceof L1PcInstance) { // ??????????????????
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight();
                    pc.sendPackets(new S_OwnCharAttrDef(pc));
                    pc.sendPackets(new S_OwnCharStatus(pc));
                    this.sendHappenMessage(pc); // ?????????????????????
                }

                this.addMagicList(cha, false); // ?????????????????????????????????

                if (cha instanceof L1PcInstance) { // ???????????????PC?????????????????????
                    final L1PcInstance pc = (L1PcInstance) cha;
                    pc.turnOnOffLight(); // ?????????
                }
            }

            // ????????????
            if ((this._skillId == DETECTION)
                    || (this._skillId == COUNTER_DETECTION)) { // ?????????????????????????????????
                this.detection(this._player);
            }

        } catch (final Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    /** ?????????????????????????????? */
    private void sendFailMessage() {
        final int msgID = this._skill.getSysmsgIdFail();
        if ((msgID > 0) && (this._user instanceof L1PcInstance)) {
            this._player.sendPackets(new S_ServerMessage(msgID));
        }
    }

    /** ??????????????????????????? */
    private void sendFailMessageHandle() {
        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ????????????????????????????????????????????????????????????????????????????????????????????????
        if ((this._skill.getType() != L1Skills.TYPE_ATTACK)
                && !this._skill.getTarget().equals("none")
                && this._targetList.isEmpty()) {
            this.sendFailMessage();
        }
    }

    /**
     * ?????????????????????????????????????????????
     * 
     * @param isSkillAction
     */
    private void sendGrfx(final boolean isSkillAction) {
        if (this._actid == 0) {
            this._actid = this._skill.getActionId();
        }
        if (this._gfxid == 0) {
            this._gfxid = this._skill.getCastGfx();
        }
        if (this._gfxid == 0) {
            return; // ?????????????????? ???
        }
        int[] data = null;

        // ????????????PC
        if (this._user instanceof L1PcInstance) {
            int targetid = 0;
            if ((this._skillId != FIRE_WALL) && (this._target != null)) {
                targetid = this._target.getId();
            }
            final L1PcInstance pc = (L1PcInstance) this._user;

            switch (this._skillId) {
                case FIRE_WALL: // ??????
                case LIFE_STREAM: // ??????????????????
                case ELEMENTAL_FALL_DOWN: // ????????????
                    if ((this._skillId == FIRE_WALL)
                    /* || (this._skillId == LIFE_STREAM) */) {
                        pc.setHeading(pc.targetDirection(this._targetX,
                                this._targetY));
                        pc.sendPackets(new S_ChangeHeading(pc));
                        pc.broadcastPacket(new S_ChangeHeading(pc));
                    }
                    final S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(),
                            this._actid);
                    pc.sendPackets(gfx);
                    pc.broadcastPacket(gfx);
                    return;
                case SHOCK_STUN: // ????????????
                    if (this._targetList.isEmpty()) { // ??????
                        return;
                    }
                    if (this._target instanceof L1PcInstance) {
                        final L1PcInstance targetPc = (L1PcInstance) this._target;
                        targetPc.sendPackets(new S_SkillSound(targetid, 4434));
                        targetPc.broadcastPacket(new S_SkillSound(targetid,
                                4434));
                    } else if (this._target instanceof L1NpcInstance) {
                        this._target.broadcastPacket(new S_SkillSound(targetid,
                                4434));
                    }
                    return;
                case LIGHT: // ?????????
                    pc.sendPackets(new S_Sound(145));
                    break;
                case MIND_BREAK: // ????????????
                case JOY_OF_PAIN: // ???????????????
                    data = new int[] { this._actid, this._dmg, 0 }; // data =
                                                                    // {actid,
                                                                    // dmg,
                                                                    // effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    pc.sendPackets(new S_SkillSound(targetid, this._gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, this._gfxid));
                    return;
                case CONFUSION: // ??????
                    data = new int[] { this._actid, this._dmg, 0 }; // data =
                                                                    // {actid,
                                                                    // dmg,
                                                                    // effect}
                    pc.sendPackets(new S_AttackPacket(pc, targetid, data));
                    pc.broadcastPacket(new S_AttackPacket(pc, targetid, data));
                    return;
                case SMASH: // ??????
                    pc.sendPackets(new S_SkillSound(targetid, this._gfxid));
                    pc.broadcastPacket(new S_SkillSound(targetid, this._gfxid));
                    return;
                case TAMING_MONSTER: // ??????
                    pc.sendPackets(new S_EffectLocation(this._targetX,
                            this._targetY, this._gfxid));
                    pc.broadcastPacket(new S_EffectLocation(this._targetX,
                            this._targetY, this._gfxid));
                    return;
                default:
                    break;
            }

            if (this._targetList.isEmpty()
                    && !(this._skill.getTarget().equals("none"))) {

                final int tempchargfx = this._player.getTempCharGfx();

                switch (tempchargfx) {
                    case 5727:
                    case 5730:
                        this._actid = ActionCodes.ACTION_SkillBuff;
                        break;

                    case 5733:
                    case 5736:
                        this._actid = ActionCodes.ACTION_Attack;
                        break;
                }
                if (isSkillAction) {
                    final S_DoActionGFX gfx = new S_DoActionGFX(
                            this._player.getId(), this._actid);
                    this._player.sendPackets(gfx);
                    this._player.broadcastPacket(gfx);
                }
                return;
            }

            if (this._skill.getTarget().equals("attack")
                    && (this._skillId != 18)) {
                if (this.isPcSummonPet(this._target)) { // ?????????????????????????????????
                    if ((this._player.getZoneType() == 1) // ??????????????????
                            || (this._target.getZoneType() == 1) // ??????????????????
                            || this._player.checkNonPvP(this._player,
                                    this._target)) { // Non-PvP??????
                        data = new int[] { this._actid, 0, this._gfxid, 6 };
                        this._player.sendPackets(new S_UseAttackSkill(
                                this._player, this._target.getId(),
                                this._targetX, this._targetY, data));
                        this._player.broadcastPacket(new S_UseAttackSkill(
                                this._player, this._target.getId(),
                                this._targetX, this._targetY, data));
                        return;
                    }
                }

                // ?????????????????? (NPC / PC ????????????)
                if (this.getSkillArea() == 0) {
                    data = new int[] { this._actid, this._dmg, this._gfxid, 6 };
                    this._player.sendPackets(new S_UseAttackSkill(this._player,
                            targetid, this._targetX, this._targetY, data));
                    this._player.broadcastPacket(new S_UseAttackSkill(
                            this._player, targetid, this._targetX,
                            this._targetY, data));
                    this._target.broadcastPacketExceptTargetSight(
                            new S_DoActionGFX(targetid,
                                    ActionCodes.ACTION_Damage), this._player);
                }

                // ???????????????????????????
                else {
                    final L1Character[] cha = new L1Character[this._targetList
                            .size()];
                    int i = 0;
                    for (final TargetStatus ts : this._targetList) {
                        cha[i] = ts.getTarget();
                        i++;
                    }
                    this._player.sendPackets(new S_RangeSkill(this._player,
                            cha, this._gfxid, this._actid,
                            S_RangeSkill.TYPE_DIR));
                    this._player.broadcastPacket(new S_RangeSkill(this._player,
                            cha, this._gfxid, this._actid,
                            S_RangeSkill.TYPE_DIR));
                }
            }

            // ???????????????????????????
            else if (this._skill.getTarget().equals("none")
                    && (this._skill.getType() == L1Skills.TYPE_ATTACK)) { // ???????????????????????????
                final L1Character[] cha = new L1Character[this._targetList
                        .size()];
                int i = 0;
                for (final TargetStatus ts : this._targetList) {
                    cha[i] = ts.getTarget();
                    cha[i].broadcastPacketExceptTargetSight(new S_DoActionGFX(
                            cha[i].getId(), ActionCodes.ACTION_Damage),
                            this._player);
                    i++;
                }
                this._player.sendPackets(new S_RangeSkill(this._player, cha,
                        this._gfxid, this._actid, S_RangeSkill.TYPE_NODIR));
                this._player
                        .broadcastPacket(new S_RangeSkill(this._player, cha,
                                this._gfxid, this._actid,
                                S_RangeSkill.TYPE_NODIR));
            }

            // ????????????
            else {
                // ?????????????????????????????????????????????????????????
                if ((this._skillId != TELEPORT)
                        && (this._skillId != MASS_TELEPORT)
                        && (this._skillId != TELEPORT_TO_MATHER)) {
                    // ????????????
                    if (isSkillAction) {
                        final S_DoActionGFX gfx = new S_DoActionGFX(
                                this._player.getId(), this._skill.getActionId());
                        this._player.sendPackets(gfx);
                        this._player.broadcastPacket(gfx);
                    }
                    // ??????????????????????????????????????? ??????????????????????????????
                    if ((this._skillId == COUNTER_MAGIC)
                            || (this._skillId == COUNTER_BARRIER)
                            || (this._skillId == COUNTER_MIRROR)) {
                        this._player.sendPackets(new S_SkillSound(targetid,
                                this._gfxid));
                    } else if ((this._skillId == AWAKEN_ANTHARAS // ?????????????????????
                            )
                            || (this._skillId == AWAKEN_FAFURION // ??????????????????
                            ) || (this._skillId == AWAKEN_VALAKAS)) { // ?????????????????????
                        if (this._skillId == this._player.getAwakeSkillId()) { // ?????????????????????????????????
                            this._player.sendPackets(new S_SkillSound(targetid,
                                    this._gfxid));
                            this._player.broadcastPacket(new S_SkillSound(
                                    targetid, this._gfxid));
                        } else {
                            return;
                        }
                    } else {
                        this._player.sendPackets(new S_SkillSound(targetid,
                                this._gfxid));
                        this._player.broadcastPacket(new S_SkillSound(targetid,
                                this._gfxid));
                    }
                }

                // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                for (final TargetStatus ts : this._targetList) {
                    final L1Character cha = ts.getTarget();
                    if (cha instanceof L1PcInstance) {
                        final L1PcInstance chaPc = (L1PcInstance) cha;
                        chaPc.sendPackets(new S_OwnCharStatus(chaPc));
                    }
                }
            }
        }

        // ????????????NPC
        else if (this._user instanceof L1NpcInstance) {
            final int targetid = this._target.getId();

            if (this._user instanceof L1MerchantInstance) {
                this._user.broadcastPacket(new S_SkillSound(targetid,
                        this._gfxid));
                return;
            }

            if ((this._skillId == CURSE_PARALYZE)
                    || (this._skillId == WEAKNESS)
                    || (this._skillId == DISEASE)) { // ??????????????????????????????????????????
                this._user.setHeading(this._user.targetDirection(this._targetX,
                        this._targetY)); // ????????????
                this._user.broadcastPacket(new S_ChangeHeading(this._user));
            }

            if (this._targetList.isEmpty()
                    && !(this._skill.getTarget().equals("none"))) {
                // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                final S_DoActionGFX gfx = new S_DoActionGFX(this._user.getId(),
                        this._actid);
                this._user.broadcastPacket(gfx);
                return;
            }

            if (this._skill.getTarget().equals("attack")
                    && (this._skillId != TURN_UNDEAD)) {
                if (this.getSkillArea() == 0) { // ??????????????????
                    data = new int[] { this._actid, this._dmg, this._gfxid, 6 };
                    this._user.broadcastPacket(new S_UseAttackSkill(this._user,
                            targetid, this._targetX, this._targetY, data));
                    this._target.broadcastPacketExceptTargetSight(
                            new S_DoActionGFX(targetid,
                                    ActionCodes.ACTION_Damage), this._user);
                } else { // ???????????????????????????
                    final L1Character[] cha = new L1Character[this._targetList
                            .size()];
                    int i = 0;
                    for (final TargetStatus ts : this._targetList) {
                        cha[i] = ts.getTarget();
                        cha[i].broadcastPacketExceptTargetSight(
                                new S_DoActionGFX(cha[i].getId(),
                                        ActionCodes.ACTION_Damage), this._user);
                        i++;
                    }
                    this._user.broadcastPacket(new S_RangeSkill(this._user,
                            cha, this._gfxid, this._actid,
                            S_RangeSkill.TYPE_DIR));
                }
            }

            // ?????????????????????
            else if (this._skill.getTarget().equals("none")
                    && (this._skill.getType() == L1Skills.TYPE_ATTACK)) { // ???????????????????????????
                final L1Character[] cha = new L1Character[this._targetList
                        .size()];
                int i = 0;
                for (final TargetStatus ts : this._targetList) {
                    cha[i] = ts.getTarget();
                    i++;
                }
                this._user.broadcastPacket(new S_RangeSkill(this._user, cha,
                        this._gfxid, this._actid, S_RangeSkill.TYPE_NODIR));
            }

            // ????????????
            else {
                // ?????????????????????????????????????????????????????????
                if ((this._skillId != TELEPORT)
                        && (this._skillId != MASS_TELEPORT)
                        && (this._skillId != TELEPORT_TO_MATHER)) {
                    // ?????????????????????????????????????????????????????????
                    final S_DoActionGFX gfx = new S_DoActionGFX(
                            this._user.getId(), this._actid);
                    this._user.broadcastPacket(gfx);
                    this._user.broadcastPacket(new S_SkillSound(targetid,
                            this._gfxid));
                }
            }
        }
    }

    /**
     * ????????????????????????????????????????????????
     * 
     * @param pc
     */
    private void sendHappenMessage(final L1PcInstance pc) {
        final int msgID = this._skill.getSysmsgIdHappen();
        if (msgID > 0) {
            // ????????????????????????????????????
            if ((this._skillId == AREA_OF_SILENCE)
                    && (this._user.getId() == pc.getId())) { // ????????????
                return;
            }
            pc.sendPackets(new S_ServerMessage(msgID));
        }
    }

    /**
     * ??????????????????
     * 
     * @param pc
     */
    private void sendIcon(final L1PcInstance pc) {
        if (this._skillTime == 0) {
            this._getBuffIconDuration = this._skill.getBuffDuration(); // ????????????
        } else {
            this._getBuffIconDuration = this._skillTime; // ??????????????????time???0????????????????????????????????????????????????
        }

        switch (this._skillId) {
            case SHIELD: // ?????????
                pc.sendPackets(new S_SkillIconShield(5,
                        this._getBuffIconDuration));
                break;

            case SHADOW_ARMOR: // ????????????
                pc.sendPackets(new S_SkillIconShield(3,
                        this._getBuffIconDuration));
                break;

            case DRESS_DEXTERITY: // ????????????
                pc.sendPackets(new S_Dexup(pc, 2, this._getBuffIconDuration));
                break;

            case DRESS_MIGHTY: // ????????????
                pc.sendPackets(new S_Strup(pc, 2, this._getBuffIconDuration));
                break;

            case GLOWING_AURA: // ????????????
                pc.sendPackets(new S_SkillIconAura(113,
                        this._getBuffIconDuration));
                break;

            case SHINING_AURA: // ????????????
                pc.sendPackets(new S_SkillIconAura(114,
                        this._getBuffIconDuration));
                break;

            case BRAVE_AURA: // ????????????
                pc.sendPackets(new S_SkillIconAura(116,
                        this._getBuffIconDuration));
                break;

            case FIRE_WEAPON: // ????????????
                pc.sendPackets(new S_SkillIconAura(147,
                        this._getBuffIconDuration));
                break;

            case WIND_SHOT: // ????????????
                pc.sendPackets(new S_SkillIconAura(148,
                        this._getBuffIconDuration));
                break;

            case FIRE_BLESS: // ????????????
                pc.sendPackets(new S_SkillIconAura(154,
                        this._getBuffIconDuration));
                break;

            case STORM_EYE: // ????????????
                pc.sendPackets(new S_SkillIconAura(155,
                        this._getBuffIconDuration));
                break;

            case EARTH_BLESS: // ???????????????
                pc.sendPackets(new S_SkillIconShield(7,
                        this._getBuffIconDuration));
                break;

            case BURNING_WEAPON: // ????????????
                pc.sendPackets(new S_SkillIconAura(162,
                        this._getBuffIconDuration));
                break;

            case STORM_SHOT: // ????????????
                pc.sendPackets(new S_SkillIconAura(165,
                        this._getBuffIconDuration));
                break;

            case IRON_SKIN: // ????????????
                pc.sendPackets(new S_SkillIconShield(10,
                        this._getBuffIconDuration));
                break;

            case EARTH_SKIN: // ????????????
                pc.sendPackets(new S_SkillIconShield(6,
                        this._getBuffIconDuration));
                break;

            case PHYSICAL_ENCHANT_STR: // ??????????????????STR
                pc.sendPackets(new S_Strup(pc, 5, this._getBuffIconDuration));
                break;

            case PHYSICAL_ENCHANT_DEX: // ??????????????????DEX
                pc.sendPackets(new S_Dexup(pc, 5, this._getBuffIconDuration));
                break;

            case HASTE:
            case GREATER_HASTE: // ?????????,???????????????
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
                        this._getBuffIconDuration));
                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                break;

            case HOLY_WALK:
            case MOVING_ACCELERATION:
            case WIND_WALK: // ??????????????????????????????????????????
                pc.sendPackets(new S_SkillBrave(pc.getId(), 4,
                        this._getBuffIconDuration));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
                break;

            case BLOODLUST: // ????????????
                pc.sendPackets(new S_SkillBrave(pc.getId(), 6,
                        this._getBuffIconDuration));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
                break;

            case SLOW:
            case MASS_SLOW:
            case ENTANGLE: // ????????????????????????????????????
                pc.sendPackets(new S_SkillHaste(pc.getId(), 2,
                        this._getBuffIconDuration));
                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
                break;

            case IMMUNE_TO_HARM: // ?????????
                pc.sendPackets(new S_SkillIconGFX(40, this._getBuffIconDuration));
                break;

            case WIND_SHACKLE: // ????????????
                pc.sendPackets(new S_SkillIconWindShackle(pc.getId(),
                        this._getBuffIconDuration));
                pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(),
                        this._getBuffIconDuration));
                break;
        }
        pc.sendPackets(new S_OwnCharStatus(pc));
    }

    /**
     * ???????????????????????????
     * 
     * @param flg
     */
    private void setCheckedUseSkill(final boolean flg) {
        this._checkedUseSkill = flg;
    }

    // ????????????????????????
    private void setDelay() {
        if (this._skill.getReuseDelay() > 0) {
            L1SkillDelay.onSkillUse(this._user, this._skill.getReuseDelay());
        }
    }

    /**
     * ?????????????????? (1/10)
     * 
     * @param i
     */
    public void setLeverage(final int i) {
        this._leverage = i;
    }

    /**
     * ?????????????????????????????????
     * 
     * @param i
     */
    public void setSkillArea(final int i) {
        this._skillArea = i;
    }

    /**
     * ?????????????????????????????????
     * 
     * @param i
     */
    public void setSkillRanged(final int i) {
        this._skillRanged = i;
    }

    /**
     * ??????????????????????????????????????????
     * 
     * @param cha
     * @param repeat_skill
     */
    private void stopSkillList(final L1Character cha, final int[] repeat_skill) {
        for (final int skillId : repeat_skill) {
            if (skillId != this._skillId) {
                cha.removeSkillEffect(skillId);
            }
        }
    }

    /**
     * ???????????????????????????HP???MP???Lawful??????????????????
     */
    private void useConsume() {

        // ?????????NPC????????????HP???MP
        if (this._user instanceof L1NpcInstance) {
            final int current_hp = this._npc.getCurrentHp() - this._hpConsume;
            this._npc.setCurrentHp(current_hp);

            final int current_mp = this._npc.getCurrentMp() - this._mpConsume;
            this._npc.setCurrentMp(current_mp);
            return;
        }

        // HP???MP?????? ?????????????????????
        if (this._skillId == FINAL_BURN) { // ????????????
            this._player.setCurrentHp(1);
            this._player.setCurrentMp(0);
        } else {
            final int current_hp = this._player.getCurrentHp()
                    - this._hpConsume;
            this._player.setCurrentHp(current_hp);

            final int current_mp = this._player.getCurrentMp()
                    - this._mpConsume;
            this._player.setCurrentMp(current_mp);
        }

        // ??????Lawful
        int lawful = this._player.getLawful() + this._skill.getLawful();
        if (lawful > 32767) {
            lawful = 32767;
        } else if (lawful < -32767) {
            lawful = -32767;
        }
        this._player.setLawful(lawful);

        final int itemConsume = this._skill.getItemConsumeId();
        final int itemConsumeCount = this._skill.getItemConsumeCount();

        if (itemConsume == 0) {
            return; // ??????????????????????????????
        }

        // ??????????????????
        this._player.getInventory().consumeItem(itemConsume, itemConsumeCount);
    }

}
