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
package com.lineage.server.model.Instance;

import static com.lineage.server.model.identity.L1ItemId.B_POTION_OF_GREATER_HASTE_SELF;
import static com.lineage.server.model.identity.L1ItemId.B_POTION_OF_HASTE_SELF;
import static com.lineage.server.model.identity.L1ItemId.POTION_OF_EXTRA_HEALING;
import static com.lineage.server.model.identity.L1ItemId.POTION_OF_GREATER_HASTE_SELF;
import static com.lineage.server.model.identity.L1ItemId.POTION_OF_GREATER_HEALING;
import static com.lineage.server.model.identity.L1ItemId.POTION_OF_HASTE_SELF;
import static com.lineage.server.model.identity.L1ItemId.POTION_OF_HEALING;
import static com.lineage.server.model.skill.L1SkillId.BLIND_HIDING;
import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.DARKNESS;
import static com.lineage.server.model.skill.L1SkillId.DECAY_POTION;
import static com.lineage.server.model.skill.L1SkillId.INVISIBILITY;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HASTE;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.Config;
import com.lineage.server.ActionCodes;
import com.lineage.server.GeneralThreadPool;
import com.lineage.server.datatables.NpcChatTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.L1Attack;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1GroundInventory;
import com.lineage.server.model.L1HateList;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1MobGroupInfo;
import com.lineage.server.model.L1MobSkillUse;
import com.lineage.server.model.L1NpcChatTimer;
import com.lineage.server.model.L1NpcRegenerationTimer;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Spawn;
import com.lineage.server.model.L1World;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.npc.NpcExecutor;
import com.lineage.server.model.npc.action.L1NpcDefaultAction;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_MoveCharPacket;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_NpcChangeShape;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1NpcChat;
import com.lineage.server.types.Point;
import com.lineage.server.utils.Random;
import com.lineage.server.utils.TimerPool;
import com.lineage.server.utils.collections.Lists;
import com.lineage.server.utils.collections.Maps;

/**
 * ??????:Npc?????????
 */
public class L1NpcInstance extends L1Character {

    /**
     * ???????????????
     */
    protected static class DeleteTimer extends TimerTask {

        /**
		 * 
		 */
        private final int _id;

        /**
		 * 
		 */
        protected DeleteTimer(final int oId) {
            this._id = oId;
            if (!(L1World.getInstance().findObject(this._id) instanceof L1NpcInstance)) {
                throw new IllegalArgumentException("allowed only L1NpcInstance");
            }
        }

        @Override
        public void run() {
            final L1NpcInstance npc = (L1NpcInstance) L1World.getInstance()
                    .findObject(this._id);
            if ((npc == null) || !npc.isDead() || npc._destroyed) {
                return; // ?????????????????????????????????????????????????????????
            }
            try {
                npc.deleteMe();
            } catch (final Exception e) { // ????????????????????????????????????
                e.printStackTrace();
            }
        }
    }

    /**
	 * 
	 */
    class DigestItemTimer implements Runnable {
        @Override
        public void run() {
            L1NpcInstance.this._digestItemRunning = true;
            while (!L1NpcInstance.this._destroyed
                    && (L1NpcInstance.this._digestItems.size() > 0)) {
                try {
                    Thread.sleep(1000);
                } catch (final Exception exception) {
                    break;
                }

                final Object[] keys = L1NpcInstance.this._digestItems.keySet()
                        .toArray();
                for (final Object key2 : keys) {
                    final Integer key = (Integer) key2;
                    Integer digestCounter = L1NpcInstance.this._digestItems
                            .get(key);
                    digestCounter -= 1;
                    if (digestCounter <= 0) {
                        L1NpcInstance.this._digestItems.remove(key);
                        final L1ItemInstance digestItem = L1NpcInstance.this
                                .getInventory().getItem(key);
                        if (digestItem != null) {
                            L1NpcInstance.this.getInventory().removeItem(
                                    digestItem, digestItem.getCount());
                        }
                    } else {
                        L1NpcInstance.this._digestItems.put(key, digestCounter);
                    }
                }
            }
            L1NpcInstance.this._digestItemRunning = false;
        }
    }

    /**
	 * 
	 */
    class HprTimer extends TimerTask {

        /**
		 * 
		 */
        private final int _point;

        /**
		 * 
		 */
        public HprTimer(int point) {
            if (point < 1) {
                point = 1;
            }
            this._point = point;
        }

        @Override
        public void run() {
            try {
                if ((!L1NpcInstance.this._destroyed && !L1NpcInstance.this
                        .isDead())
                        && ((L1NpcInstance.this.getCurrentHp() > 0) && (L1NpcInstance.this
                                .getCurrentHp() < L1NpcInstance.this.getMaxHp()))) {
                    L1NpcInstance.this.setCurrentHp(L1NpcInstance.this
                            .getCurrentHp() + this._point);
                } else {
                    this.cancel();
                    L1NpcInstance.this._hprRunning = false;
                }
            } catch (final Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
    }

    /**
	 * 
	 */
    class MprTimer extends TimerTask {

        /**
		 * 
		 */
        private final int _point;

        /**
		 * 
		 */
        public MprTimer(int point) {
            if (point < 1) {
                point = 1;
            }
            this._point = point;
        }

        @Override
        public void run() {
            try {
                if ((!L1NpcInstance.this._destroyed && !L1NpcInstance.this
                        .isDead())
                        && ((L1NpcInstance.this.getCurrentHp() > 0) && (L1NpcInstance.this
                                .getCurrentMp() < L1NpcInstance.this.getMaxMp()))) {
                    L1NpcInstance.this.setCurrentMp(L1NpcInstance.this
                            .getCurrentMp() + this._point);
                } else {
                    this.cancel();
                    L1NpcInstance.this._mprRunning = false;
                }
            } catch (final Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
    }

    /**
	 * 
	 */
    interface NpcAI {
        public void start();
    }

    /**
	 * 
	 */
    class NpcAIThreadImpl implements Runnable, NpcAI {
        @Override
        public void run() {
            try {
                L1NpcInstance.this.setAiRunning(true);
                while (!L1NpcInstance.this._destroyed
                        && !L1NpcInstance.this.isDead()
                        && (L1NpcInstance.this.getCurrentHp() > 0)
                        && (L1NpcInstance.this.getHiddenStatus() == HIDDEN_STATUS_NONE)) {
                    /*
                     * if (_paralysisTime > 0) { try {
                     * Thread.sleep(_paralysisTime); } catch (Exception
                     * exception) { break; } finally { setParalyzed(false);
                     * _paralysisTime = 0; } }
                     */
                    while (L1NpcInstance.this.isParalyzed()
                            || L1NpcInstance.this.isSleeped()) {
                        try {
                            Thread.sleep(200);
                        } catch (final InterruptedException e) {
                            L1NpcInstance.this.setParalyzed(false);
                        }
                    }

                    if (L1NpcInstance.this.AIProcess()) {
                        break;
                    }
                    try {
                        // ????????????????????????
                        Thread.sleep(L1NpcInstance.this.getSleepTime());
                    } catch (final Exception e) {
                        break;
                    }
                }
                L1NpcInstance.this.mobSkill.resetAllSkillUseCount();
                do {
                    try {
                        Thread.sleep(L1NpcInstance.this.getSleepTime());
                    } catch (final Exception e) {
                        break;
                    }
                } while (L1NpcInstance.this.isDeathProcessing());
                L1NpcInstance.this.allTargetClear();
                L1NpcInstance.this.setAiRunning(false);
            } catch (final Exception e) {
                _log.log(Level.WARNING, "NpcAI????????????????????????", e);
            }
        }

        @Override
        public void start() {
            GeneralThreadPool.getInstance().execute(NpcAIThreadImpl.this);
        }
    }

    /**
	 * 
	 */
    class NpcAITimerImpl extends TimerTask implements NpcAI {

        /**
         * ???????????????????????????
         */
        private class DeathSyncTimer extends TimerTask {
            public DeathSyncTimer() {
                // TODO Auto-generated constructor stub
            }

            @Override
            public void run() {
                if (L1NpcInstance.this.isDeathProcessing()) {
                    this.schedule(L1NpcInstance.this.getSleepTime());
                    return;
                }
                L1NpcInstance.this.allTargetClear();
                L1NpcInstance.this.setAiRunning(false);
            }

            private void schedule(final int delay) {
                _timerPool.getTimer().schedule(new DeathSyncTimer(), delay);
            }
        }

        /**
		 * 
		 */
        private boolean notContinued() {
            return L1NpcInstance.this._destroyed
                    || L1NpcInstance.this.isDead()
                    || (L1NpcInstance.this.getCurrentHp() <= 0)
                    || (L1NpcInstance.this.getHiddenStatus() != HIDDEN_STATUS_NONE);
        }

        @Override
        public void run() {
            try {
                if (this.notContinued()) {
                    this.stop();
                    return;
                }

                // XXX ??????????????????????????????????????????
                if (0 < L1NpcInstance.this._paralysisTime) {
                    this.schedule(L1NpcInstance.this._paralysisTime);
                    L1NpcInstance.this._paralysisTime = 0;
                    L1NpcInstance.this.setParalyzed(false);
                    return;
                } else if (L1NpcInstance.this.isParalyzed()
                        || L1NpcInstance.this.isSleeped()) {
                    this.schedule(200);
                    return;
                }

                if (!L1NpcInstance.this.AIProcess()) { // AI??????????????????????????????????????????????????????????????????????????????
                    this.schedule(L1NpcInstance.this.getSleepTime());
                    return;
                }
                this.stop();
            } catch (final Exception e) {
                _log.log(Level.WARNING, "NpcAI????????????????????????", e);
            }
        }

        // ???????????????????????????Timer??????????????????????????????????????????
        /**
		 * 
		 */
        private void schedule(final int delay) {
            _timerPool.getTimer().schedule(new NpcAITimerImpl(), delay);
        }

        @Override
        public void start() {
            L1NpcInstance.this.setAiRunning(true);
            _timerPool.getTimer().schedule(NpcAITimerImpl.this, 0);
        }

        /**
		 * 
		 */
        private void stop() {
            L1NpcInstance.this.mobSkill.resetAllSkillUseCount();
            _timerPool.getTimer().schedule(new DeathSyncTimer(), 0); // ?????????????????????
        }
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
	 * 
	 */
    static Logger _log = Logger.getLogger(L1NpcInstance.class.getName());

    /**
     * ????????????
     */
    public static final int MOVE_SPEED = 0;

    /**
     * ????????????
     */
    public static final int ATTACK_SPEED = 1;

    /**
     * ????????????
     */
    public static final int MAGIC_SPEED = 2;

    /**
     * ???????????? (???)
     */
    public static final int HIDDEN_STATUS_NONE = 0;

    /**
     * ???????????? (??????)
     */
    public static final int HIDDEN_STATUS_SINK = 1;

    /**
     * ???????????? (??????)
     */
    public static final int HIDDEN_STATUS_FLY = 2;

    /**
     * ???????????? (??????)
     */
    public static final int HIDDEN_STATUS_ICE = 3;

    /**
     * ???????????? (????????????????????????)
     */
    public static final int HIDDEN_STATUS_COUNTERATTACK_BARRIER = 4;

    /**
     * ?????????????????????
     */
    public static final int CHAT_TIMING_APPEARANCE = 0;

    /** 
	 * 
	 */
    public static final int CHAT_TIMING_DEAD = 1;

    /** 
	 * 
	 */
    public static final int CHAT_TIMING_HIDE = 2;

    /** 
	 * 
	 */
    public static final int CHAT_TIMING_GAME_TIME = 3;

    /**
     * NPC????????????
     */
    public NpcExecutor TALK = null;

    /**
     * NPC????????????
     */
    public NpcExecutor ACTION = null;

    /** 
	 * 
	 */
    private L1Npc _npcTemplate;

    /** 
	 * 
	 */
    private L1Spawn _spawn;

    /** 
	 * 
	 */
    private int _spawnNumber; // L1Spawn????????????????????????????????????

    /**
     * ???????????????
     */
    private int _petcost; // ???????????????????????????????????????

    /** 
	 * 
	 */
    public L1Inventory _inventory = new L1Inventory();

    // ??????????????????????????????????????? ???????????? ?????????????????????????????????

    /** 
	 * 
	 */
    L1MobSkillUse mobSkill;

    /**
     * ??????????????????????????????????????????
     */
    private boolean firstFound = true;

    /**
     * ?????????????????????????????? ???????????????????????????
     */
    public static int courceRange = 15;

    /**
     * ?????? MP
     */
    private int _drainedMana = 0;

    /**
     * ??????
     */
    private boolean _rest = false;

    // ??????????????????????????????
    /**
     * ??????????????????
     */
    private int _randomMoveDistance = 0;

    /**
     * ??????????????????
     */
    private int _randomMoveDirection = 0;

    /**
     * ???????????????????????????????????????????????????????????????????????????AI???????????????
     */
    static final TimerPool _timerPool = new TimerPool(4);

    /**
     * ??????
     */
    public static void shuffle(final L1Object[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            final int t = Random.nextInt(i);

            // ??????????????????
            final L1Object tmp = arr[i];
            arr[i] = arr[t];
            arr[t] = tmp;
        }
    }

    /**
     * ???????????????
     */
    private boolean _aiRunning = false; // ???????????????

    // ????????????????????????????????????????????????????????????????????????????????????????????????
    /**
	 * 
	 */
    private boolean _actived = false; // ??????????????????????????????

    // ???????????????false???_target??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    /**
	 * 
	 */
    private boolean _firstAttack = false; // ???????????????????????????????????????

    /**
	 * 
	 */
    private int _sleep_time; // ???????????????????????????(ms) ???????????????????????????????????????????????????????????????

    /**
	 * 
	 */
    protected L1HateList _hateList = new L1HateList();

    /**
	 * 
	 */
    protected L1HateList _dropHateList = new L1HateList();

    // ?????????????????????????????????????????????????????????????????????????????????
    /**
     * ??????????????????
     */
    protected List<L1ItemInstance> _targetItemList = Lists.newList(); // ?????????????????????????????????

    /**
     * ??????
     */
    protected L1Character _target = null; // ????????????????????????

    /**
     * ????????????
     */
    protected L1ItemInstance _targetItem = null; // ????????????????????????????????????

    /**
	 * 
	 */
    protected L1Character _master = null; // ??????or????????????????????????

    /**
	 * 
	 */
    private boolean _deathProcessing = false; // ??????????????????

    /**
	 * 
	 */
    int _paralysisTime = 0; // Paralysis RestTime

    /**
     * ??????????????????
     */
    boolean _hprRunning = false;

    /**
     * ???????????????
     */
    private HprTimer _hprTimer;

    /**
     * ??????????????????
     */
    boolean _mprRunning = false;

    /**
     * ???????????????
     */
    private MprTimer _mprTimer;

    /**
     * ????????????
     */
    Map<Integer, Integer> _digestItems;

    /**
     * ??????????????????
     */
    public boolean _digestItemRunning = false;

    /**
	 * 
	 */
    private int _passispeed;

    /**
     * ????????????
     */
    private int _atkspeed;

    /**
     * ????????????
     */
    private boolean _pickupItem;

    // ?????????????????????????????????
    /**
     * ??????????????????
     */
    public static final int USEITEM_HEAL = 0;

    /**
     * ??????????????????
     */
    public static final int USEITEM_HASTE = 1;

    /**
     * ????????????
     */
    public static int[] healPotions = { POTION_OF_GREATER_HEALING,
            POTION_OF_EXTRA_HEALING, POTION_OF_HEALING };

    /**
     * ????????????
     */
    public static int[] haestPotions = { B_POTION_OF_GREATER_HASTE_SELF,
            POTION_OF_GREATER_HASTE_SELF, B_POTION_OF_HASTE_SELF,
            POTION_OF_HASTE_SELF };

    // ??????????????????????????????????????????????????????????????????????????????????????????????????????
    // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public static int checkObject(final int x, final int y, final short m,
            final int d) { // ????????? ?????????
        // ???????????????
        // ????????????
        final L1Map map = L1WorldMap.getInstance().getMap(m);
        switch (d) {
            case 1:
                if (map.isPassable(x, y, 1)) {
                    return 1;
                } else if (map.isPassable(x, y, 0)) {
                    return 0;
                } else if (map.isPassable(x, y, 2)) {
                    return 2;
                }
                break;

            case 2:
                if (map.isPassable(x, y, 2)) {
                    return 2;
                } else if (map.isPassable(x, y, 1)) {
                    return 1;
                } else if (map.isPassable(x, y, 3)) {
                    return 3;
                }
                break;

            case 3:
                if (map.isPassable(x, y, 3)) {
                    return 3;
                } else if (map.isPassable(x, y, 2)) {
                    return 2;
                } else if (map.isPassable(x, y, 4)) {
                    return 4;
                }
                break;

            case 4:
                if (map.isPassable(x, y, 4)) {
                    return 4;
                } else if (map.isPassable(x, y, 3)) {
                    return 3;
                } else if (map.isPassable(x, y, 5)) {
                    return 5;
                }
                break;

            case 5:
                if (map.isPassable(x, y, 5)) {
                    return 5;
                } else if (map.isPassable(x, y, 4)) {
                    return 4;
                } else if (map.isPassable(x, y, 6)) {
                    return 6;
                }
                break;

            case 6:
                if (map.isPassable(x, y, 6)) {
                    return 6;
                } else if (map.isPassable(x, y, 5)) {
                    return 5;
                } else if (map.isPassable(x, y, 7)) {
                    return 7;
                }
                break;

            case 7:
                if (map.isPassable(x, y, 7)) {
                    return 7;
                } else if (map.isPassable(x, y, 6)) {
                    return 6;
                } else if (map.isPassable(x, y, 0)) {
                    return 0;
                }
                break;

            case 0:
                if (map.isPassable(x, y, 0)) {
                    return 0;
                } else if (map.isPassable(x, y, 7)) {
                    return 7;
                } else if (map.isPassable(x, y, 1)) {
                    return 1;
                }
                break;
        }
        return -1;
    }

    // ----------From L1Character-------------
    /**
     * ????????????
     */
    private String _nameId;

    /**
	 * 
	 */
    private boolean _Agro; // ??? ??????????????????

    /**
	 * 
	 */
    private boolean _Agrocoi; // ??? ??????????????????????????????

    /**
     * ????????????
     */
    private boolean _Agrososc;

    /**
	 * 
	 */
    private int _homeX; // ??? ??????????????????????????????????????????????????????????????????????????????????????????

    /**
	 * 
	 */
    private int _homeY; // ??? ??????????????????????????????????????????????????????????????????????????????????????????

    // EXP???Drop??????????????????????????????????????????????????????????????????????????????

    /**
	 * 
	 */
    private boolean _reSpawn; // ??? ??????????????????????????????

    /**
	 * 
	 */
    private int _lightSize; // ??? ????????? ???????????? ????????????????????????

    /**
     * ????????????
     */
    private boolean _weaponBreaked;

    /**
     * ????????????
     */
    private int _hiddenStatus; // ??? ????????????????????????????????????

    /**
     * ????????????
     */
    private int _movementDistance = 0;

    /**
     * ???????????????
     */
    private int _tempLawful = 0;

    /**
	 * 
	 */
    public boolean _destroyed = false; // ???????????????????????????

    /**
	 * 
	 */
    private boolean _isResurrect;

    // ???????????????????????????????????? ??????????????? ??????????????????????????????

    /**
     * ???????????? ????????????
     */
    private boolean _isDropitems = false;

    /**
	 * 
	 */
    private boolean _forDropitems = false;

    // ????????????????????????????????????????????????
    /**
	 * 
	 */
    private DeleteTimer _deleteTask;

    /**
	 * 
	 */
    private ScheduledFuture<?> _future = null;

    /**
     * MOB????????????
     */
    private L1MobGroupInfo _mobGroupInfo = null;

    /**
     * MOB??????ID
     */
    private int _mobGroupId = 0;

    /**
     * ??????????????????
     */
    private int _polyAtkRanged = -1;

    /**
     * ????????????GFX
     */
    private int _polyArrowGfx = 0;

    /**
	 * 
	 */
    public L1NpcInstance(final L1Npc template) {
        this.setStatus(0);
        this.setMoveSpeed(0);
        this.setDead(false);
        this.setreSpawn(false);

        if (template != null) {
            this.setting_template(template);
        }
    }

    // ???????????????????????????????????????????????????????????????????????????????????????

    /**
     * ????????????
     * 
     * @param ary
     * @param d
     */
    private void _getFront(final int[] ary, final int d) {
        switch (d) {
            case 1:
                ary[4] = 2;
                ary[3] = 0;
                ary[2] = 1;
                ary[1] = 3;
                ary[0] = 7;
                break;

            case 2:
                ary[4] = 2;
                ary[3] = 4;
                ary[2] = 0;
                ary[1] = 1;
                ary[0] = 3;
                break;

            case 3:
                ary[4] = 2;
                ary[3] = 4;
                ary[2] = 1;
                ary[1] = 3;
                ary[0] = 5;
                break;

            case 4:
                ary[4] = 2;
                ary[3] = 4;
                ary[2] = 6;
                ary[1] = 3;
                ary[0] = 5;
                break;

            case 5:
                ary[4] = 4;
                ary[3] = 6;
                ary[2] = 3;
                ary[1] = 5;
                ary[0] = 7;
                break;

            case 6:
                ary[4] = 4;
                ary[3] = 6;
                ary[2] = 0;
                ary[1] = 5;
                ary[0] = 7;
                break;

            case 7:
                ary[4] = 6;
                ary[3] = 0;
                ary[2] = 1;
                ary[1] = 5;
                ary[0] = 7;
                break;

            case 0:
                ary[4] = 2;
                ary[3] = 6;
                ary[2] = 0;
                ary[1] = 1;
                ary[0] = 7;
                break;
        }
    }

    /**
     * ????????????
     * 
     * @param ary
     * @param d
     */
    private void _moveLocation(final int[] ary, final int d) {
        switch (d) {
            case 1:
                ary[0] = ary[0] + 1;
                ary[1] = ary[1] - 1;
                break;

            case 2:
                ary[0] = ary[0] + 1;
                break;

            case 3:
                ary[0] = ary[0] + 1;
                ary[1] = ary[1] + 1;
                break;

            case 4:
                ary[1] = ary[1] + 1;
                break;

            case 5:
                ary[0] = ary[0] - 1;
                ary[1] = ary[1] + 1;
                break;

            case 6:
                ary[0] = ary[0] - 1;
                break;

            case 7:
                ary[0] = ary[0] - 1;
                ary[1] = ary[1] - 1;
                break;

            case 0:
                ary[1] = ary[1] - 1;
                break;
        }
        ary[2] = d;
    }

    // ?????????????????????????????????
    // ????????????????????????????????????????????????????????????
    /**
     * ?????????????????????????????????
     * 
     * @param x
     *            ????????????
     * @param y
     *            ????????????
     */
    private int _serchCource(final int x, final int y) // ???????????? ????????????
    {
        int i;
        final int locCenter = courceRange + 1;
        final int diff_x = x - locCenter; // ??????????????????????????????????????????
        final int diff_y = y - locCenter; // ??????????????????????????????????????????
        int[] locBace = { this.getX() - diff_x, this.getY() - diff_y, 0, 0 }; // ???
                                                                              // ???
                                                                              // ??????
                                                                              // ????????????
        final int[] locNext = new int[4];
        int[] locCopy;
        final int[] dirFront = new int[5];
        final boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
        final LinkedList<int[]> queueSerch = new LinkedList<int[]>();

        // ??????????????????
        for (int j = courceRange * 2 + 1; j > 0; j--) {
            for (i = courceRange - Math.abs(locCenter - j); i >= 0; i--) {
                serchMap[j][locCenter + i] = true;
                serchMap[j][locCenter - i] = true;
            }
        }

        // ?????????????????????
        final int[] firstCource = { 2, 4, 6, 0, 1, 3, 5, 7 };
        for (i = 0; i < 8; i++) {
            System.arraycopy(locBace, 0, locNext, 0, 4);
            this._moveLocation(locNext, firstCource[i]);
            if ((locNext[0] - locCenter == 0) && (locNext[1] - locCenter == 0)) {
                // ????????????????????????????????????:???
                return firstCource[i];
            }
            if (serchMap[locNext[0]][locNext[1]]) {
                final int tmpX = locNext[0] + diff_x;
                final int tmpY = locNext[1] + diff_y;
                boolean found = false;
                switch (i) {
                    case 0:
                        found = this.getMap().isPassable(tmpX, tmpY + 1, i);
                        break;

                    case 1:
                        found = this.getMap().isPassable(tmpX - 1, tmpY + 1, i);
                        break;

                    case 2:
                        found = this.getMap().isPassable(tmpX - 1, tmpY, i);
                        break;

                    case 3:
                        found = this.getMap().isPassable(tmpX - 1, tmpY - 1, i);
                        break;

                    case 4:
                        found = this.getMap().isPassable(tmpX, tmpY - 1, i);
                        break;

                    case 5:
                        found = this.getMap().isPassable(tmpX + 1, tmpY - 1, i);
                        break;

                    case 6:
                        found = this.getMap().isPassable(tmpX + 1, tmpY, i);
                        break;

                    case 7:
                        found = this.getMap().isPassable(tmpX + 1, tmpY + 1, i);
                        break;
                }
                if (found)// ??????????????????????????????
                {
                    locCopy = new int[4];
                    System.arraycopy(locNext, 0, locCopy, 0, 4);
                    locCopy[2] = firstCource[i];
                    locCopy[3] = firstCource[i];
                    queueSerch.add(locCopy);
                }
                serchMap[locNext[0]][locNext[1]] = false;
            }
        }
        locBace = null;

        // ?????????????????????
        while (queueSerch.size() > 0) {
            locBace = queueSerch.removeFirst();
            this._getFront(dirFront, locBace[2]);
            for (i = 4; i >= 0; i--) {
                System.arraycopy(locBace, 0, locNext, 0, 4);
                this._moveLocation(locNext, dirFront[i]);
                if ((locNext[0] - locCenter == 0)
                        && (locNext[1] - locCenter == 0)) {
                    return locNext[3];
                }
                if (serchMap[locNext[0]][locNext[1]]) {
                    final int tmpX = locNext[0] + diff_x;
                    final int tmpY = locNext[1] + diff_y;
                    boolean found = false;
                    switch (i) {
                        case 0:
                            found = this.getMap().isPassable(tmpX, tmpY + 1, i);
                            break;

                        case 1:
                            found = this.getMap().isPassable(tmpX - 1,
                                    tmpY + 1, i);
                            break;

                        case 2:
                            found = this.getMap().isPassable(tmpX - 1, tmpY, i);
                            break;

                        case 3:
                            found = this.getMap().isPassable(tmpX - 1,
                                    tmpY - 1, i);
                            break;

                        case 4:
                            found = this.getMap().isPassable(tmpX, tmpY - 1, i);
                            break;
                    }
                    if (found) // ??????????????????????????????
                    {
                        locCopy = new int[4];
                        System.arraycopy(locNext, 0, locCopy, 0, 4);
                        locCopy[2] = dirFront[i];
                        queueSerch.add(locCopy);
                    }
                    serchMap[locNext[0]][locNext[1]] = false;
                }
            }
            locBace = null;
        }
        return -1; // ????????????????????????????????????
    }

    /**
     * ??????????????? (??????????????????????????????)
     */
    boolean AIProcess() {
        this.setSleepTime(300);

        this.checkTarget();
        if ((this._target == null) && (this._master == null)) {
            // ?????????????????? ??????????????????
            // ??????????????????????????????????????????
            this.searchTarget();
        }

        this.onDoppel(true);
        this.onItemUse();

        // ?????????????????????
        if (this._target == null) {
            this.checkTargetItem();
            // ???????????????????????? ????????????
            if (this.isPickupItem() && (this._targetItem == null)) {
                this.searchTargetItem();
            }

            if (this._targetItem == null) {
                if (this.noTarget()) {
                    return true;
                }
            } else {
                // onTargetItem();
                final L1Inventory groundInventory = L1World.getInstance()
                        .getInventory(this._targetItem.getX(),
                                this._targetItem.getY(),
                                this._targetItem.getMapId());
                if (groundInventory.checkItem(this._targetItem.getItemId())) {
                    this.onTargetItem();
                } else {
                    this._targetItemList.remove(this._targetItem);
                    this._targetItem = null;
                    this.setSleepTime(1000);
                    return false;
                }
            }
        } else { // ??????????????????
            if (this.getHiddenStatus() == HIDDEN_STATUS_NONE) {
                this.onTarget();
            } else {
                return true;
            }
        }

        return false; // ??????????????????
    }

    /**
     * ??????????????????
     */
    public void allTargetClear() {
        this._hateList.clear();
        this._dropHateList.clear();
        this._target = null;
        this._targetItemList.clear();
        this._targetItem = null;
    }

    /**
     * ?????????????????? (??????) ???????????????????????????????????????
     * 
     * @param pc
     */
    public void appearOnGround(final L1PcInstance pc) {
        switch (this.getHiddenStatus()) {
            case HIDDEN_STATUS_SINK: // ????????????:??????
                this.setHiddenStatus(HIDDEN_STATUS_NONE);
                this.setStatus(L1NpcDefaultAction.getInstance().getStatus(
                        this.getTempCharGfx()));
                this.broadcastPacket(new S_DoActionGFX(this.getId(),
                        ActionCodes.ACTION_Appear));
                this.broadcastPacket(new S_CharVisualUpdate(this, this
                        .getStatus()));
                if (!pc.hasSkillEffect(INVISIBILITY)
                        && !pc.hasSkillEffect(BLIND_HIDING) // ???????????? (?????????)?????????????????????
                                                            // (?????????)????????????GM??????
                        && !pc.isGm()) {
                    this._hateList.add(pc, 0);
                    this._target = pc;
                }
                this.onNpcAI(); // ??????AI?????????
                this.startChat(CHAT_TIMING_HIDE);
                break;

            case HIDDEN_STATUS_FLY: // ????????????:??????
                this.setHiddenStatus(HIDDEN_STATUS_NONE);
                this.setStatus(L1NpcDefaultAction.getInstance().getStatus(
                        this.getTempCharGfx()));
                this.broadcastPacket(new S_DoActionGFX(this.getId(),
                        ActionCodes.ACTION_Movedown));
                if (!pc.hasSkillEffect(INVISIBILITY)
                        && !pc.hasSkillEffect(BLIND_HIDING) // ???????????? (?????????)?????????????????????
                                                            // (?????????)????????????GM??????
                        && !pc.isGm()) {
                    this._hateList.add(pc, 0);
                    this._target = pc;
                }
                this.onNpcAI(); // ??????AI?????????
                this.startChat(CHAT_TIMING_HIDE);
                break;

            case HIDDEN_STATUS_ICE: // ????????????:??????
                this.setHiddenStatus(HIDDEN_STATUS_NONE);
                this.setStatus(L1NpcDefaultAction.getInstance().getStatus(
                        this.getTempCharGfx()));
                this.broadcastPacket(new S_DoActionGFX(this.getId(),
                        ActionCodes.ACTION_AxeWalk));
                this.broadcastPacket(new S_CharVisualUpdate(this, this
                        .getStatus()));
                if (!pc.hasSkillEffect(INVISIBILITY)
                        && !pc.hasSkillEffect(BLIND_HIDING) // ???????????? (?????????)?????????????????????
                                                            // (?????????)????????????GM??????
                        && !pc.isGm()) {
                    this._hateList.add(pc, 0);
                    this._target = pc;
                }
                this.onNpcAI(); // ??????AI?????????
                this.startChat(CHAT_TIMING_HIDE);
                break;

            case HIDDEN_STATUS_COUNTERATTACK_BARRIER: // ???????????????????????????
                this.setHiddenStatus(HIDDEN_STATUS_NONE);
                this.setStatus(L1NpcDefaultAction.getInstance().getStatus(
                        this.getTempCharGfx()));
                this.broadcastPacket(new S_DoActionGFX(this.getId(),
                        ActionCodes.ACTION_Appear));
                this.broadcastPacket(new S_CharVisualUpdate(this, this
                        .getStatus()));
                if (!pc.hasSkillEffect(INVISIBILITY)
                        && !pc.hasSkillEffect(BLIND_HIDING) // ???????????? (?????????)?????????????????????
                                                            // (?????????)????????????GM??????
                        && !pc.isGm()) {
                    this._hateList.add(pc, 0);
                    this._target = pc;
                }
                this.onNpcAI(); // ??????AI?????????
                this.startChat(CHAT_TIMING_HIDE);
                break;
        }
    }

    /**
     * ?????????????????? (????????????)
     * 
     * @param pc
     */
    public void approachPlayer(final L1PcInstance pc) {
        if (pc.hasSkillEffect(60) || pc.hasSkillEffect(97)) { // ????????????
                                                              // (?????????)?????????????????????
                                                              // (?????????)???
            return;
        }

        switch (this.getHiddenStatus()) {
            case HIDDEN_STATUS_SINK: // ????????????:??????
                if (this.getCurrentHp() == this.getMaxHp()) {
                    if (pc.getLocation()
                            .getTileLineDistance(this.getLocation()) <= 2) {
                        this.appearOnGround(pc);
                    }
                }
                break;

            case HIDDEN_STATUS_FLY: // ????????????:??????
                if (this.getCurrentHp() == this.getMaxHp()) {
                    if (pc.getLocation()
                            .getTileLineDistance(this.getLocation()) <= 1) {
                        this.appearOnGround(pc);
                    }
                } else {
                    this.searchItemFromAir(); // ????????????????????????
                }
                break;

            case HIDDEN_STATUS_ICE: // ????????????:??????
                if (this.getCurrentHp() < this.getMaxHp()) {
                    this.appearOnGround(pc);
                }
                break;

            case HIDDEN_STATUS_COUNTERATTACK_BARRIER: // ????????????:????????????
                if (this.getCurrentHp() == this.getMaxHp()) {
                    if (pc.getLocation()
                            .getTileLineDistance(this.getLocation()) <= 2) {
                        this.appearOnGround(pc);
                    }
                }
                break;
        }
    }

    /**
     * ?????????????????????
     * 
     * @param target
     */
    public void attackTarget(final L1Character target) {
        if (target instanceof L1PcInstance) {
            final L1PcInstance player = (L1PcInstance) target;
            if (player.isTeleport()) { // ???????????????
                return;
            }
        } else if (target instanceof L1PetInstance) {
            final L1PetInstance pet = (L1PetInstance) target;
            final L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // ???????????????
                    return;
                }
            }
        } else if (target instanceof L1SummonInstance) {
            final L1SummonInstance summon = (L1SummonInstance) target;
            final L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // ???????????????
                    return;
                }
            }
        }
        if (this instanceof L1PetInstance) {
            final L1PetInstance pet = (L1PetInstance) this;
            final L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // ???????????????
                    return;
                }
            }
        } else if (this instanceof L1SummonInstance) {
            final L1SummonInstance summon = (L1SummonInstance) this;
            final L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                final L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // ???????????????
                    return;
                }
            }
        }

        if (target instanceof L1NpcInstance) {
            final L1NpcInstance npc = (L1NpcInstance) target;
            if (npc.getHiddenStatus() != HIDDEN_STATUS_NONE) { // ???????????????????????????
                this.allTargetClear();
                return;
            }
        }

        boolean isCounterBarrier = false;
        final L1Attack attack = new L1Attack(this, target);
        if (attack.calcHit()) {
            if (target.hasSkillEffect(COUNTER_BARRIER)) {
                final L1Magic magic = new L1Magic(target, this);
                final boolean isProbability = magic
                        .calcProbabilityMagic(COUNTER_BARRIER);
                final boolean isShortDistance = attack.isShortDistance();
                if (isProbability && isShortDistance) {
                    isCounterBarrier = true;
                }
            }
            if (!isCounterBarrier) {
                attack.calcDamage();
            }
        }
        if (isCounterBarrier) {
            attack.actionCounterBarrier();
            attack.commitCounterBarrier();
        } else {
            attack.action();
            attack.commit();
        }
        this.setSleepTime(this.calcSleepTime(this.getAtkspeed(), ATTACK_SPEED));
    }

    /**
     * ??????????????????
     * 
     * @param sleepTime
     * @param type
     */
    protected int calcSleepTime(int sleepTime, final int type) {
        switch (this.getMoveSpeed()) {
            case 0: // ??????
                break;
            case 1: // ??????
                sleepTime -= (sleepTime * 0.25);
                break;
            case 2: // ??????
                sleepTime *= 2;
                break;
        }
        if (this.getBraveSpeed() == 1) {
            sleepTime -= (sleepTime * 0.25);
        }
        if (this.hasSkillEffect(WIND_SHACKLE)) {
            if ((type == ATTACK_SPEED) || (type == MAGIC_SPEED)) {
                sleepTime += (sleepTime * 0.25);
            }
        }
        return sleepTime;
    }

    /**
     * ??????????????????
     */
    public void checkTarget() {
        if ((this._target == null)
                || (this._target.getMapId() != this.getMapId())
                || (this._target.getCurrentHp() <= 0)
                || this._target.isDead()
                || (this._target.isInvisble()
                        && !this.getNpcTemplate().is_agrocoi() && !this._hateList
                        .containsKey(this._target))
                // ??????????????????30?????? (?????????)
                || (this._target.getTileLineDistance(this) > 30)) {
            if (this._target != null) {
                this.tagertClear();
            }
            if (!this._hateList.isEmpty()) {
                this._target = this._hateList.getMaxHateCharacter();
                this.checkTarget();
            }
        }
    }

    /**
     * ??????????????????
     */
    public void checkTargetItem() {
        if ((this._targetItem == null)
                || (this._targetItem.getMapId() != this.getMapId())
                || (this.getLocation().getTileDistance(
                        this._targetItem.getLocation()) > 15)) {
            if (!this._targetItemList.isEmpty()) {
                this._targetItem = this._targetItemList.get(0);
                this._targetItemList.remove(0);
                this.checkTargetItem();
            } else {
                this._targetItem = null;
            }
        }
    }

    /**
     * ????????????
     */
    public void deleteMe() {
        this._destroyed = true;
        if (this.getInventory() != null) {
            this.getInventory().clearItems();
        }
        this.allTargetClear();
        this._master = null;
        L1World.getInstance().removeVisibleObject(this);
        L1World.getInstance().removeObject(this);
        final List<L1PcInstance> players = L1World.getInstance()
                .getRecognizePlayer(this);
        if (players.size() > 0) {
            final S_RemoveObject s_deleteNewObject = new S_RemoveObject(this);
            for (final L1PcInstance pc : players) {
                if (pc != null) {
                    pc.removeKnownObject(this);
                    // if(!L1Character.distancepc(user, this))
                    pc.sendPackets(s_deleteNewObject);
                }
            }
        }
        this.removeAllKnownObjects();

        // ????????????
        final L1MobGroupInfo mobGroupInfo = this.getMobGroupInfo();
        if (mobGroupInfo == null) {
            if (this.isReSpawn()) {
                this.onDecay(true);
            }
        } else {
            if (mobGroupInfo.removeMember(this) == 0) { // ??????????????????
                this.setMobGroupInfo(null);
                if (this.isReSpawn()) {
                    this.onDecay(false);
                }
            }
        }
    }

    /**
     * @param drain
     */
    public int drainMana(final int drain) {
        if (this._drainedMana >= Config.MANA_DRAIN_LIMIT_PER_NPC) {
            return 0;
        }
        int result = Math.min(drain, this.getCurrentMp());
        if (this._drainedMana + result > Config.MANA_DRAIN_LIMIT_PER_NPC) {
            result = Config.MANA_DRAIN_LIMIT_PER_NPC - this._drainedMana;
        }
        this._drainedMana += result;
        return result;
    }

    /**
	 * 
	 */
    public boolean forDropitems() {
        return this._forDropitems;
    }

    /**
     * ??????????????????
     */
    public int getAtkRanged() {
        if (this._polyAtkRanged == -1) {
            return this.getNpcTemplate().get_ranged();
        }
        return this._polyAtkRanged;
    }

    /**
     * ??????????????????
     */
    public int getAtkspeed() {
        return this._atkspeed;
    }

    /**
	 * 
	 */
    public L1HateList getHateList() {
        return this._hateList;
    }

    /**
     * ??????????????????
     */
    public int getHiddenStatus() {
        return this._hiddenStatus;
    }

    /** 
	 * 
	 */
    public int getHomeX() {
        return this._homeX;
    }

    /** 
	 * 
	 */
    public int getHomeY() {
        return this._homeY;
    }

    // ??????????????????
    @Override
    public L1Inventory getInventory() {
        return this._inventory;
    }

    /** 
	 * 
	 */
    public int getLightSize() {
        return this._lightSize;
    }

    /**
     * ????????????
     */
    public L1Character getMaster() {
        return this._master;
    }

    /**
     * ??????MOB??????ID
     */
    public int getMobGroupId() {
        return this._mobGroupId;
    }

    /**
     * ??????MOB????????????
     */
    public L1MobGroupInfo getMobGroupInfo() {
        return this._mobGroupInfo;
    }

    /**
     * ??????????????????
     */
    public int getMovementDistance() {
        return this._movementDistance;
    }

    /**
     * ??????????????????
     */
    public String getNameId() {
        return this._nameId;
    }

    /**
     * ??????NPC ID
     */
    public int getNpcId() {
        return this._npcTemplate.get_npcId();
    }

    /**
     * ??????NPC??????
     */
    public L1Npc getNpcTemplate() {
        return this._npcTemplate;
    }

    /**
	 * 
	 */
    public int getParalysisTime() {
        return this._paralysisTime;
    }

    /**
     * ??????????????????
     */
    public int getPassispeed() {
        return this._passispeed;
    }

    // ??????????????????????????????????????? ???????????? ?????????????????????????????????

    /**
     * ?????????????????????
     */
    public int getPetcost() {
        return this._petcost;
    }

    /**
     * ??????????????????GFX
     */
    public int getPolyArrowGfx() {
        return this._polyArrowGfx;
    }

    /**
     * ????????????????????????
     */
    public int getPolyAtkRanged() {
        return this._polyAtkRanged;
    }

    /**
     * ??????????????????
     */
    protected int getSleepTime() {
        return this._sleep_time;
    }

    /** 
	 * 
	 */
    public L1Spawn getSpawn() {
        return this._spawn;
    }

    /** 
	 * 
	 */
    public int getSpawnNumber() {
        return this._spawnNumber;
    }

    /**
     * ?????????????????????
     */
    public int getTempLawful() {
        return this._tempLawful;
    }

    /**
     * @param i
     */
    public void giveDropItems(final boolean i) {
        this._forDropitems = i;
    }

    /**
	 * 
	 */
    protected boolean isActived() {
        return this._actived;
    }

    // ???????????????????????????????????? ???????????? ??????????????????????????????

    /**
	 * 
	 */
    public boolean isAgro() {
        return this._Agro;
    }

    /**
	 * 
	 */
    public boolean isAgrocoi() {
        return this._Agrocoi;
    }

    /**
	 * 
	 */
    public boolean isAgrososc() {
        return this._Agrososc;
    }

    /**
     * AI?????????
     */
    protected boolean isAiRunning() {
        return this._aiRunning;
    }

    /**
     * ????????????
     */
    protected boolean isDeathProcessing() {
        return this._deathProcessing;
    }

    /**
	 * 
	 */
    public boolean isDropitems() {
        return this._isDropitems;
    }

    /**
     * ??????????????????????????????
     * 
     * @param dir
     */
    private boolean isExsistCharacterBetweenTarget(final int dir) {
        if (!(this instanceof L1MonsterInstance)) { // ?????????????????????
            return false;
        }
        if (this._target == null) { // ??????????????????
            return false;
        }

        final int locX = this.getX();
        final int locY = this.getY();
        int targetX = locX;
        int targetY = locY;

        switch (dir) {
            case 1:
                targetX = locX + 1;
                targetY = locY - 1;
                break;

            case 2:
                targetX = locX + 1;
                break;

            case 3:
                targetX = locX + 1;
                targetY = locY + 1;
                break;

            case 4:
                targetY = locY + 1;
                break;

            case 5:
                targetX = locX - 1;
                targetY = locY + 1;
                break;

            case 6:
                targetX = locX - 1;
                break;

            case 7:
                targetX = locX - 1;
                targetY = locY - 1;
                break;

            case 0:
                targetY = locY - 1;
                break;
        }

        for (final L1Object object : L1World.getInstance().getVisibleObjects(
                this, 1)) {
            // PC, Summon, Pet?????????
            if ((object instanceof L1PcInstance)
                    || (object instanceof L1SummonInstance)
                    || (object instanceof L1PetInstance)) {
                final L1Character cha = (L1Character) object;
                // ??????????????????????????????????????????????????????
                if ((cha.getX() == targetX) && (cha.getY() == targetY)
                        && (cha.getMapId() == this.getMapId())) {
                    if (object instanceof L1PcInstance) {
                        final L1PcInstance pc = (L1PcInstance) object;
                        if (pc.isGhost()) { // ??????UB????????????PC
                            continue;
                        }
                    }
                    this._hateList.add(cha, 0);
                    this._target = cha;
                    return true;
                }
            }
        }
        return false;
    }

    // ??????????????????????????????????????? ????????????(npcskills??????????????????????????????????????????) ?????????????????????????????????

    /**
     * ???????????????
     */
    protected boolean isFirstAttack() {
        return this._firstAttack;
    }

    /**
     * MOB??????
     */
    public boolean isInMobGroup() {
        return this.getMobGroupInfo() != null;
    }

    /**
     * ????????????
     */
    public boolean isPickupItem() {
        return this._pickupItem;
    }

    /**
	 * 
	 */
    public boolean isReSpawn() {
        return this._reSpawn;
    }

    /**
	 * 
	 */
    public boolean isRest() {
        return this._rest;
    }

    /**
	 * 
	 */
    public boolean isResurrect() {
        return this._isResurrect;
    }

    /**
     * ???????????????
     */
    public boolean isWeaponBreaked() {
        return this._weaponBreaked;
    }

    /**
	 * 
	 */
    public int moveDirection(final int x, final int y) { // ???????????? ????????????
        return this.moveDirection(x, y,
                this.getLocation().getLineDistance(new Point(x, y)));
    }

    /**
     * ????????????????????????????????????
     */
    public int moveDirection(final int x, final int y, final double d) { // ????????????
                                                                         // ????????????
                                                                         // ???????????????
        int dir = 0;
        if ((this.hasSkillEffect(40) == true) && (d >= 2D)) { // ????????????????????????????????????????????????2???????????????????????????
            return -1;
        } else if (d > 30D) { // ?????????????????????????????????????????????
            return -1;
        } else if (d > courceRange) { // ????????????????????????????????????
            dir = this.targetDirection(x, y);
            dir = checkObject(this.getX(), this.getY(), this.getMapId(), dir);
        } else { // ???????????????????????????
            dir = this._serchCource(x, y);
            if (dir == -1) { // ??????????????????????????????????????????????????????????????????????????????
                dir = this.targetDirection(x, y);
                if (!this.isExsistCharacterBetweenTarget(dir)) {
                    dir = checkObject(this.getX(), this.getY(),
                            this.getMapId(), dir);
                }
            }
        }
        return dir;
    }

    /**
     * ?????????????????????
     * 
     * @param nx
     * @param ny
     */
    public boolean nearTeleport(int nx, int ny) {
        final int rdir = Random.nextInt(8);
        int dir;
        for (int i = 0; i < 8; i++) {
            dir = rdir + i;
            if (dir > 7) {
                dir -= 8;
            }
            switch (dir) {
                case 1:
                    nx++;
                    ny--;
                    break;

                case 2:
                    nx++;
                    break;

                case 3:
                    nx++;
                    ny++;
                    break;

                case 4:
                    ny++;
                    break;

                case 5:
                    nx--;
                    ny++;
                    break;

                case 6:
                    nx--;
                    break;

                case 7:
                    nx--;
                    ny--;
                    break;

                case 0:
                    ny--;
                    break;
            }
            if (this.getMap().isPassable(nx, ny)) {
                dir += 4;
                if (dir > 7) {
                    dir -= 8;
                }
                this.teleport(nx, ny, dir);
                this.setCurrentMp(this.getCurrentMp() - 10);
                return true;
            }
        }
        return false;
    }

    /**
     * ?????????????????????????????????????????????????????????AI???
     */
    public boolean noTarget() {
        if ((this._master != null)
                && (this._master.getMapId() == this.getMapId())
                && (this.getLocation().getTileLineDistance(
                        this._master.getLocation()) > 2)) { // ????????????????????????????????????????????????????????????
            final int dir = this.moveDirection(this._master.getX(),
                    this._master.getY());
            if (dir != -1) {
                this.setDirectionMove(dir);
                this.setSleepTime(this.calcSleepTime(this.getPassispeed(),
                        MOVE_SPEED));
            } else {
                return true;
            }
        } else {
            if (L1World.getInstance().getRecognizePlayer(this).isEmpty()) {
                return true; // ??????????????????????????????????????????????????????????????????
            }
            // ?????????????????????????????????????????????????????????
            if ((this._master == null) && (this.getPassispeed() > 0)
                    && !this.isRest()) {
                // ?????????????????????????????????or????????????????????????????????????????????????????????????????????????????????????
                final L1MobGroupInfo mobGroupInfo = this.getMobGroupInfo();
                if ((mobGroupInfo == null)
                        || ((mobGroupInfo != null) && mobGroupInfo
                                .isLeader(this))) {
                    // ??????????????????????????????????????????????????????????????????????????????????????????
                    // ????????????????????????????????????????????????????????????????????????
                    if (this._randomMoveDistance == 0) {
                        this._randomMoveDistance = Random.nextInt(5) + 1;
                        this._randomMoveDirection = Random.nextInt(20);
                        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        if ((this.getHomeX() != 0) && (this.getHomeY() != 0)
                                && (this._randomMoveDirection < 8)
                                && (Random.nextInt(3) == 0)) {
                            this._randomMoveDirection = this.moveDirection(
                                    this.getHomeX(), this.getHomeY());
                        }
                    } else {
                        this._randomMoveDistance--;
                    }
                    final int dir = checkObject(this.getX(), this.getY(),
                            this.getMapId(), this._randomMoveDirection);
                    if (dir != -1) {
                        this.setDirectionMove(dir);
                        this.setSleepTime(this.calcSleepTime(
                                this.getPassispeed(), MOVE_SPEED));
                    }
                } else { // ????????????
                    final L1NpcInstance leader = mobGroupInfo.getLeader();
                    if (this.getLocation().getTileLineDistance(
                            leader.getLocation()) > 2) {
                        final int dir = this.moveDirection(leader.getX(),
                                leader.getY());
                        if (dir == -1) {
                            return true;
                        }
                        this.setDirectionMove(dir);
                        this.setSleepTime(this.calcSleepTime(
                                this.getPassispeed(), MOVE_SPEED));
                    }
                }
            }
        }
        return false;
    }

    /**
     * NPC????????????
     */
    public void npcSleepTime(final int i, final int type) {
        this.setSleepTime(this.calcSleepTime(SprTable.getInstance()
                .getSprSpeed(this.getTempCharGfx(), i), type));
    }

    // ??????ID??????SpawnTask??????
    // ????????????????????? ?????????????????????
    public void onDecay(final boolean isReuseId) {
        int id = 0;
        if (isReuseId) {
            id = this.getId();
        } else {
            id = 0;
        }
        this._spawn.executeSpawnTask(this._spawnNumber, id);
    }

    /**
     * ???????????????
     */
    public void onDoppel(final boolean isChangeShape) {
    }

    /**
     * ??????????????????
     */
    public void onFinalAction(final L1PcInstance pc, final String s) {
    }

    /**
     * ????????????
     */
    public void onGetItem(final L1ItemInstance item) {
        this.refineItem();
        this.getInventory().shuffle();
        if (this.getNpcTemplate().get_digestitem() > 0) {
            this.setDigestItem(item);
        }
    }

    // ????????????????????????????????????????????????????????????????????????????????????????????????
    public void onItemUse() {
    }

    /** ???????????? */
    public void onNpcAI() {
    }

    // ?????????????????????????????????
    @Override
    public void onPerceive(final L1PcInstance perceivedFrom) {
        perceivedFrom.addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));
        this.onNpcAI();
    }

    /**
     * ????????????
     */
    public void onTarget() {
        this.setActived(true);
        this._targetItemList.clear();
        this._targetItem = null;
        final L1Character target = this._target; // ??????????????????_target?????????????????????????????????????????????????????????
        if (this.getAtkspeed() == 0) { // ??????????????????
            if (this.getPassispeed() > 0) { // ??????????????????
                int escapeDistance = 15;
                if (this.hasSkillEffect(DARKNESS) == true) { // ???????????? (????????????)
                    escapeDistance = 1;
                }
                if (this.getLocation()
                        .getTileLineDistance(target.getLocation()) > escapeDistance) { // ???????????????????????????????????????
                    this.tagertClear();
                } else { // ????????????
                    int dir = this.targetReverseDirection(target.getX(),
                            target.getY());
                    dir = checkObject(this.getX(), this.getY(),
                            this.getMapId(), dir);
                    this.setDirectionMove(dir);
                    this.setSleepTime(this.calcSleepTime(this.getPassispeed(),
                            MOVE_SPEED));
                }
            }
        } else { // ???????????????
            if (this.isAttackPosition(target.getX(), target.getY(),
                    this.getAtkRanged())) { // ??????????????????
                if (this.mobSkill.isSkillTrigger(target)) { // ?????????????????????????????????????????????
                    if (this.mobSkill.skillUse(target, true)) { // ???????????????(mobskill.sql???TriRnd?????????)
                        this.setSleepTime(this.calcSleepTime(
                                this.mobSkill.getSleepTime(), MAGIC_SPEED));
                    } else { // ?????????????????????????????????????????????
                        this.setHeading(this.targetDirection(target.getX(),
                                target.getY()));
                        this.attackTarget(target);
                    }
                } else {
                    this.setHeading(this.targetDirection(target.getX(),
                            target.getY()));
                    this.attackTarget(target);
                }
            } else { // ?????????????????????
                if (this.mobSkill.skillUse(target, false)) { // ????????????(mobskill.sql???TriRnd??????????????????????????????100%?????????????????????????????????????????????TriRnd????????????)
                    this.setSleepTime(this.calcSleepTime(
                            this.mobSkill.getSleepTime(), MAGIC_SPEED));
                    return;
                }

                if (this.getPassispeed() > 0) {
                    // ??????????????????
                    final int distance = this.getLocation().getTileDistance(
                            target.getLocation());
                    if ((this.firstFound == true)
                            && this.getNpcTemplate().is_teleport()
                            && (distance > 3) && (distance < 15)) {
                        if (this.nearTeleport(target.getX(), target.getY()) == true) {
                            this.firstFound = false;
                            return;
                        }
                    }

                    if (this.getNpcTemplate().is_teleport()
                            && (20 > Random.nextInt(100))
                            && (this.getCurrentMp() >= 10) && (distance > 6)
                            && (distance < 15)) { // ????????????
                        if (this.nearTeleport(target.getX(), target.getY()) == true) {
                            return;
                        }
                    }
                    final int dir = this.moveDirection(target.getX(),
                            target.getY());
                    if (dir == -1) {
                        // ???????????????????????? ?????????????????????????????????
                        this.searchTarget();
                    } else {
                        this.setDirectionMove(dir);
                        this.setSleepTime(this.calcSleepTime(
                                this.getPassispeed(), MOVE_SPEED));
                    }
                } else {
                    // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    this.tagertClear();
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    public void onTargetItem() {
        if (this.getLocation().getTileLineDistance(
                this._targetItem.getLocation()) == 0) { // ?????????????????????
            this.pickupTargetItem(this._targetItem);
        } else { // ???????????????
            final int dir = this.moveDirection(this._targetItem.getX(),
                    this._targetItem.getY());
            if (dir == -1) { // ????????????
                this._targetItemList.remove(this._targetItem);
                this._targetItem = null;
            } else { // ???????????????
                this.setDirectionMove(dir);
                this.setSleepTime(this.calcSleepTime(this.getPassispeed(),
                        MOVE_SPEED));
            }
        }
    }

    /**
     * ??????????????????
     */
    public void pickupTargetItem(final L1ItemInstance targetItem) {
        final L1Inventory groundInventory = L1World.getInstance().getInventory(
                targetItem.getX(), targetItem.getY(), targetItem.getMapId());
        final L1ItemInstance item = groundInventory.tradeItem(targetItem,
                targetItem.getCount(), this.getInventory());
        this.turnOnOffLight();
        this.onGetItem(item);
        this._targetItemList.remove(this._targetItem);
        this._targetItem = null;
        this.setSleepTime(1000);
    }

    /**
     * ????????????
     */
    public void receiveDamage(final L1Character attacker, final int damage) {
    }

    /**
     * ??????????????????
     */
    public void ReceiveManaDamage(final L1Character attacker, final int damageMp) {
    }

    /**
     * ????????????
     */
    public void refineItem() {

        int[] materials = null;
        int[] counts = null;
        int[] createitem = null;
        int[] createcount = null;

        switch (this._npcTemplate.get_npcId()) {
            case 45032: // ?????????
                // ????????????????????????
                if ((this.getExp() != 0) && !this._inventory.checkItem(20)) {
                    materials = new int[] { 40508, 40521, 40045 }; // ??????????????????????????????????????????
                    counts = new int[] { 150, 3, 3 };
                    createitem = new int[] { 20 };
                    createcount = new int[] { 1 };
                    if (this._inventory.checkItem(materials, counts)) {
                        for (int i = 0; i < materials.length; i++) {
                            this._inventory
                                    .consumeItem(materials[i], counts[i]);
                        }
                        for (int j = 0; j < createitem.length; j++) {
                            this._inventory.storeItem(createitem[j],
                                    createcount[j]);
                        }
                    }
                }
                // ???????????????
                if ((this.getExp() != 0) && !this._inventory.checkItem(19)) {
                    materials = new int[] { 40494, 40521 }; // ????????????????????????????????????
                    counts = new int[] { 150, 3 };
                    createitem = new int[] { 19 };
                    createcount = new int[] { 1 };
                    if (this._inventory.checkItem(materials, counts)) {
                        for (int i = 0; i < materials.length; i++) {
                            this._inventory
                                    .consumeItem(materials[i], counts[i]);
                        }
                        for (int j = 0; j < createitem.length; j++) {
                            this._inventory.storeItem(createitem[j],
                                    createcount[j]);
                        }
                    }
                }
                // ???????????????
                if ((this.getExp() != 0) && !this._inventory.checkItem(3)) {
                    materials = new int[] { 40494, 40521 }; // ????????????????????????????????????
                    counts = new int[] { 50, 1 };
                    createitem = new int[] { 3 };
                    createcount = new int[] { 1 };
                    if (this._inventory.checkItem(materials, counts)) {
                        for (int i = 0; i < materials.length; i++) {
                            this._inventory
                                    .consumeItem(materials[i], counts[i]);
                        }
                        for (int j = 0; j < createitem.length; j++) {
                            this._inventory.storeItem(createitem[j],
                                    createcount[j]);
                        }
                    }
                }
                // ???????????????????????????
                if ((this.getExp() != 0) && !this._inventory.checkItem(100)) {
                    materials = new int[] { 88, 40508, 40045 }; // ???????????????????????????????????????
                    counts = new int[] { 4, 80, 3 };
                    createitem = new int[] { 100 };
                    createcount = new int[] { 1 };
                    if (this._inventory.checkItem(materials, counts)) {
                        for (int i = 0; i < materials.length; i++) {
                            this._inventory
                                    .consumeItem(materials[i], counts[i]);
                        }
                        for (int j = 0; j < createitem.length; j++) {
                            this._inventory.storeItem(createitem[j],
                                    createcount[j]);
                        }
                    }
                }
                // ?????????????????????
                if ((this.getExp() != 0) && !this._inventory.checkItem(89)) {
                    materials = new int[] { 88, 40494 }; // ?????????????????????????????????
                    counts = new int[] { 2, 80 };
                    createitem = new int[] { 89 };
                    createcount = new int[] { 1 };
                    if (this._inventory.checkItem(materials, counts)) {
                        for (int i = 0; i < materials.length; i++) {
                            this._inventory
                                    .consumeItem(materials[i], counts[i]);
                        }
                        for (int j = 0; j < createitem.length; j++) {
                            final L1ItemInstance item = this._inventory
                                    .storeItem(createitem[j], createcount[j]);
                            if (this.getNpcTemplate().get_digestitem() > 0) {
                                this.setDigestItem(item);
                            }
                        }
                    }
                }
                break;

            case 81069: // ??????????????????45????????????
                // ???????????????
                if ((this.getExp() != 0) && !this._inventory.checkItem(40542)) {
                    materials = new int[] { 40032 }; // ???????????????
                    counts = new int[] { 1 };
                    createitem = new int[] { 40542 };
                    createcount = new int[] { 1 };
                    if (this._inventory.checkItem(materials, counts)) {
                        for (int i = 0; i < materials.length; i++) {
                            this._inventory
                                    .consumeItem(materials[i], counts[i]);
                        }
                        for (int j = 0; j < createitem.length; j++) {
                            this._inventory.storeItem(createitem[j],
                                    createcount[j]);
                        }
                    }
                }
                break;

            case 45166: // ?????????
            case 45167: // ?????????
                // ????????????
                if ((this.getExp() != 0) && !this._inventory.checkItem(40726)) {
                    materials = new int[] { 40725 }; // ????????????
                    counts = new int[] { 1 };
                    createitem = new int[] { 40726 };
                    createcount = new int[] { 1 };
                    if (this._inventory.checkItem(materials, counts)) {
                        for (int i = 0; i < materials.length; i++) {
                            this._inventory
                                    .consumeItem(materials[i], counts[i]);
                        }
                        for (int j = 0; j < createitem.length; j++) {
                            this._inventory.storeItem(createitem[j],
                                    createcount[j]);
                        }
                    }
                }
                break;
        }
    }

    // ??????
    @Override
    public synchronized void resurrect(final int hp) {
        if (this._destroyed) {
            return;
        }
        if (this._deleteTask != null) {
            if (!this._future.cancel(false)) { // ???????????????
                return;
            }
            this._deleteTask = null;
            this._future = null;
        }
        super.resurrect(hp);

        // ???????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????????????????????????????????????????
        final L1SkillUse skill = new L1SkillUse();
        skill.handleCommands(null, CANCELLATION, this.getId(), this.getX(),
                this.getY(), null, 0, L1SkillUse.TYPE_LOGIN, this);
    }

    /**
     * ???????????????????????????????????????????????????????????????
     */
    public void searchItemFromAir() {
        final List<L1GroundInventory> gInventorys = Lists.newList();

        for (final L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
            if ((obj != null) && (obj instanceof L1GroundInventory)) {
                gInventorys.add((L1GroundInventory) obj);
            }
        }
        if (gInventorys.isEmpty()) {
            return;
        }

        final int pickupIndex = Random.nextInt(gInventorys.size());
        final L1GroundInventory inventory = gInventorys.get(pickupIndex);
        for (final L1ItemInstance item : inventory.getItems()) {
            if ((item.getItem().getType() == 6) // potion(??????)
                    || (item.getItem().getType() == 7)) { // food(??????)
                if (this.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                    if (this.getHiddenStatus() == HIDDEN_STATUS_FLY) {
                        this.setHiddenStatus(HIDDEN_STATUS_NONE);
                        this.setStatus(L1NpcDefaultAction.getInstance()
                                .getStatus(this.getTempCharGfx()));
                        this.broadcastPacket(new S_DoActionGFX(this.getId(),
                                ActionCodes.ACTION_Movedown));
                        this.onNpcAI();
                        this.startChat(CHAT_TIMING_HIDE);
                        this._targetItem = item;
                        this._targetItemList.add(this._targetItem);
                    }
                }
            }
        }
    }

    /**
     * ????????????
     */
    public void searchTarget() {
        this.tagertClear();
    }

    /**
     * ??????????????????
     */
    public void searchTargetItem() {
        final List<L1GroundInventory> gInventorys = Lists.newList();

        for (final L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
            if ((obj != null) && (obj instanceof L1GroundInventory)) {
                gInventorys.add((L1GroundInventory) obj);
            }
        }
        if (gInventorys.size() == 0) {
            return;
        }

        // ??????????????????????????????????????????
        final int pickupIndex = Random.nextInt(gInventorys.size());
        final L1GroundInventory inventory = gInventorys.get(pickupIndex);
        for (final L1ItemInstance item : inventory.getItems()) {
            if (this.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) { // ??????????????????????????????????????????????????????
                this._targetItem = item;
                this._targetItemList.add(this._targetItem);
            }
        }
    }

    // ???????????????????????????????????????????????????????????????????????????????????????????????????
    public void serchLink(final L1PcInstance targetPlayer, final int family) {
        final List<L1Object> targetKnownObjects = targetPlayer
                .getKnownObjects();
        for (final Object knownObject : targetKnownObjects) {
            if (knownObject instanceof L1NpcInstance) {
                final L1NpcInstance npc = (L1NpcInstance) knownObject;
                if (npc.getNpcTemplate().get_agrofamily() > 0) {
                    // ????????????????????????????????????????????????
                    if (npc.getNpcTemplate().get_agrofamily() == 1) {
                        // ???????????????????????????????????????
                        if (npc.getNpcTemplate().get_family() == family) {
                            npc.setLink(targetPlayer);
                        }
                    } else {
                        // ??????????????????????????????????????????
                        npc.setLink(targetPlayer);
                    }
                }
                final L1MobGroupInfo mobGroupInfo = this.getMobGroupInfo();
                if (mobGroupInfo != null) {
                    if ((this.getMobGroupId() != 0)
                            && (this.getMobGroupId() == npc.getMobGroupId())) { // ??????????????????
                        npc.setLink(targetPlayer);
                    }
                }
            }
        }
    }

    /**
     * ??????
     * 
     * @param actived
     */
    protected void setActived(final boolean actived) {
        this._actived = actived;
    }

    /** 
	 * 
	 */
    public void setAgro(final boolean flag) {
        this._Agro = flag;
    }

    /** 
	 * 
	 */
    public void setAgrocoi(final boolean flag) {
        this._Agrocoi = flag;
    }

    /** 
	 * 
	 */
    public void setAgrososc(final boolean flag) {
        this._Agrososc = flag;
    }

    /**
     * ??????AI??????
     * 
     * @param aiRunning
     */
    protected void setAiRunning(final boolean aiRunning) {
        this._aiRunning = aiRunning;
    }

    /**
     * ??????????????????
     */
    public void setAtkspeed(final int i) {
        this._atkspeed = i;
    }

    /**
     * ??????????????????
     * 
     * @param deathProcessing
     */
    protected void setDeathProcessing(final boolean deathProcessing) {
        this._deathProcessing = deathProcessing;
    }

    /**
     * ??????????????????
     */
    public void setDigestItem(final L1ItemInstance item) {
        this._digestItems.put(new Integer(item.getId()), new Integer(this
                .getNpcTemplate().get_digestitem()));
        if (!this._digestItemRunning) {
            final DigestItemTimer digestItemTimer = new DigestItemTimer();
            GeneralThreadPool.getInstance().execute(digestItemTimer);
        }
    }

    // ????????????????????????
    /**
     * ??????????????????
     */
    public void setDirectionMove(final int dir) {
        if (dir >= 0) {
            int nx = 0;
            int ny = 0;

            switch (dir) {
                case 1:
                    nx = 1;
                    ny = -1;
                    this.setHeading(1);
                    break;

                case 2:
                    nx = 1;
                    ny = 0;
                    this.setHeading(2);
                    break;

                case 3:
                    nx = 1;
                    ny = 1;
                    this.setHeading(3);
                    break;

                case 4:
                    nx = 0;
                    ny = 1;
                    this.setHeading(4);
                    break;

                case 5:
                    nx = -1;
                    ny = 1;
                    this.setHeading(5);
                    break;

                case 6:
                    nx = -1;
                    ny = 0;
                    this.setHeading(6);
                    break;

                case 7:
                    nx = -1;
                    ny = -1;
                    this.setHeading(7);
                    break;

                case 0:
                    nx = 0;
                    ny = -1;
                    this.setHeading(0);
                    break;

                default:
                    break;

            }

            this.getMap().setPassable(this.getLocation(), true);

            final int nnx = this.getX() + nx;
            final int nny = this.getY() + ny;
            this.setX(nnx);
            this.setY(nny);

            this.getMap().setPassable(this.getLocation(), false);

            this.broadcastPacket(new S_MoveCharPacket(this));

            // movement_distance?????????????????????????????????
            if (this.getMovementDistance() > 0) {
                if ((this instanceof L1GuardInstance)
                        || (this instanceof L1MerchantInstance)
                        || (this instanceof L1MonsterInstance)) {
                    if (this.getLocation().getLineDistance(
                            new Point(this.getHomeX(), this.getHomeY())) > this
                            .getMovementDistance()) {
                        this.teleport(this.getHomeX(), this.getHomeY(),
                                this.getHeading());
                    }
                }
            }
            // ?????????????????????????????????????????????????????????????????????????????????????????????
            if ((this.getNpcTemplate().get_npcId() >= 45912)
                    && (this.getNpcTemplate().get_npcId() <= 45916)) {
                if (!((this.getX() >= 32591) && (this.getX() <= 32644)
                        && (this.getY() >= 32643) && (this.getY() <= 32688) && (this
                        .getMapId() == 4))) {
                    this.teleport(this.getHomeX(), this.getHomeY(),
                            this.getHeading());
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    public void setDropItems(final boolean i) {
        this._isDropitems = i;
    }

    /**
     * ?????????????????????
     * 
     * @param firstAttack
     */
    protected void setFirstAttack(final boolean firstAttack) {
        this._firstAttack = firstAttack;
    }

    /**
     * ????????????
     */
    public void setHate(final L1Character cha, int hate) {
        if ((cha != null) && (cha.getId() != this.getId())) {
            if (!this.isFirstAttack() && (hate != 0)) {
                // hate += 20; // ???????????????
                hate += this.getMaxHp() / 10; // ???????????????
                this.setFirstAttack(true);
            }

            this._hateList.add(cha, hate);
            this._dropHateList.add(cha, hate);
            this._target = this._hateList.getMaxHateCharacter();
            this.checkTarget();
        }
    }

    /**
     * ??????????????????
     */
    public void setHiddenStatus(final int i) {
        this._hiddenStatus = i;
    }

    /** 
	 * 
	 */
    public void setHomeX(final int i) {
        this._homeX = i;
    }

    /** 
	 * 
	 */
    public void setHomeY(final int i) {
        this._homeY = i;
    }

    /**
     * ??????????????????
     */
    public void setInventory(final L1Inventory inventory) {
        this._inventory = inventory;
    }

    /**
     * ??????????????????
     */
    public void setLightSize(final int i) {
        this._lightSize = i;
    }

    // ????????????
    public void setLink(final L1Character cha) {
    }

    /**
     * ????????????
     */
    public void setMaster(final L1Character cha) {
        this._master = cha;
    }

    /**
     * ??????MOB??????ID
     */
    public void setMobGroupId(final int i) {
        this._mobGroupId = i;
    }

    /**
     * ??????MOB????????????
     */
    public void setMobGroupInfo(final L1MobGroupInfo m) {
        this._mobGroupInfo = m;
    }

    // ???????????????????????????????????????????????????????????????????????????????????????????????????

    /**
     * ??????????????????
     */
    public void setMovementDistance(final int i) {
        this._movementDistance = i;
    }

    /**
     * ??????????????????
     */
    public void setNameId(final String s) {
        this._nameId = s;
    }

    /** 
	 * 
	 */
    public void setParalysisTime(final int ptime) {
        this._paralysisTime = ptime;
    }

    /** 
	 * 
	 */
    public void setPassispeed(final int i) {
        this._passispeed = i;
    }

    /**
     * ?????????????????????
     */
    public void setPetcost(final int i) {
        this._petcost = i;
    }

    /**
     * ??????????????????
     */
    public void setPickupItem(final boolean flag) {
        this._pickupItem = flag;
    }

    /**
     * ??????????????????GFX
     */
    public void setPolyArrowGfx(final int i) {
        this._polyArrowGfx = i;
    }

    /**
     * ????????????????????????
     */
    public void setPolyAtkRanged(final int i) {
        this._polyAtkRanged = i;
    }

    /** 
	 * 
	 */
    public void setreSpawn(final boolean flag) {
        this._reSpawn = flag;
    }

    /**
     * ????????????
     * 
     * @param _rest
     */
    public void setRest(final boolean _rest) {
        this._rest = _rest;
    }

    /**
     * ????????????
     * 
     * @param flag
     */
    public void setResurrect(final boolean flag) {
        this._isResurrect = flag;
    }

    /**
     * ??????????????????
     * 
     * @param sleep_time
     */
    protected void setSleepTime(final int sleep_time) {
        this._sleep_time = sleep_time;
    }

    /** 
	 * 
	 */
    public void setSpawn(final L1Spawn spawn) {
        this._spawn = spawn;
    }

    /** 
	 * 
	 */
    public void setSpawnNumber(final int number) {
        this._spawnNumber = number;
    }

    /**
     * ?????????????????????
     * 
     * @param i
     */
    public void setTempLawful(final int i) {
        this._tempLawful = i;
    }

    /**
     * ?????????????????????????????????
     * 
     * @param template
     */
    public void setting_template(final L1Npc template) {
        this._npcTemplate = template;
        int randomlevel = 0;
        double rate = 0;
        double diff = 0;
        this.setName(template.get_name());
        this.setNameId(template.get_nameid());
        if (template.get_randomlevel() == 0) { // ????????????Lv
            this.setLevel(template.get_level());
        } else { // ????????????Lv????????????:get_level(),?????????:get_randomlevel()???
            randomlevel = Random.nextInt(template.get_randomlevel()
                    - template.get_level() + 1);
            diff = template.get_randomlevel() - template.get_level();
            rate = randomlevel / diff;
            randomlevel += template.get_level();
            this.setLevel(randomlevel);
        }
        if (template.get_randomhp() == 0) {
            this.setMaxHp(template.get_hp());
            this.setCurrentHpDirect(template.get_hp());
        } else {
            final double randomhp = rate
                    * (template.get_randomhp() - template.get_hp());
            final int hp = (int) (template.get_hp() + randomhp);
            this.setMaxHp(hp);
            this.setCurrentHpDirect(hp);
        }
        if (template.get_randommp() == 0) {
            this.setMaxMp(template.get_mp());
            this.setCurrentMpDirect(template.get_mp());
        } else {
            final double randommp = rate
                    * (template.get_randommp() - template.get_mp());
            final int mp = (int) (template.get_mp() + randommp);
            this.setMaxMp(mp);
            this.setCurrentMpDirect(mp);
        }
        if (template.get_randomac() == 0) {
            this.setAc(template.get_ac());
        } else {
            final double randomac = rate
                    * (template.get_randomac() - template.get_ac());
            final int ac = (int) (template.get_ac() + randomac);
            this.setAc(ac);
        }
        if (template.get_randomlevel() == 0) {
            this.setStr(template.get_str());
            this.setCon(template.get_con());
            this.setDex(template.get_dex());
            this.setInt(template.get_int());
            this.setWis(template.get_wis());
            this.setMr(template.get_mr());
        } else {
            this.setStr((byte) Math.min(template.get_str() + diff, 127));
            this.setCon((byte) Math.min(template.get_con() + diff, 127));
            this.setDex((byte) Math.min(template.get_dex() + diff, 127));
            this.setInt((byte) Math.min(template.get_int() + diff, 127));
            this.setWis((byte) Math.min(template.get_wis() + diff, 127));
            this.setMr((byte) Math.min(template.get_mr() + diff, 127));

            this.addHitup((int) diff * 2);
            this.addDmgup((int) diff * 2);
        }
        this.setAgro(template.is_agro());
        this.setAgrocoi(template.is_agrocoi());
        this.setAgrososc(template.is_agrososc());
        this.setTempCharGfx(template.get_gfxid());
        this.setGfxId(template.get_gfxid());
        this.setStatus(L1NpcDefaultAction.getInstance().getStatus(
                this.getTempCharGfx()));
        this.setPolyAtkRanged(template.get_ranged());
        this.setPolyArrowGfx(template.getBowActId());

        // ??????
        if (template.get_passispeed() != 0) {
            this.setPassispeed(SprTable.getInstance().getSprSpeed(
                    this.getTempCharGfx(), this.getStatus()));
        } else {
            this.setPassispeed(0);
        }
        // ??????
        if (template.get_atkspeed() != 0) {
            int actid = (this.getStatus() + 1);
            if (L1NpcDefaultAction.getInstance().getDefaultAttack(
                    this.getTempCharGfx()) != actid) {
                actid = L1NpcDefaultAction.getInstance().getDefaultAttack(
                        this.getTempCharGfx());
            }
            this.setAtkspeed(SprTable.getInstance().getSprSpeed(
                    this.getTempCharGfx(), actid));
        } else {
            this.setAtkspeed(0);
        }

        if (template.get_randomexp() == 0) {
            this.setExp(template.get_exp());
        } else {
            final int level = this.getLevel();
            int exp = level * level;
            exp += 1;
            this.setExp(exp);
        }
        if (template.get_randomlawful() == 0) {
            this.setLawful(template.get_lawful());
            this.setTempLawful(template.get_lawful());
        } else {
            final double randomlawful = rate
                    * (template.get_randomlawful() - template.get_lawful());
            final int lawful = (int) (template.get_lawful() + randomlawful);
            this.setLawful(lawful);
            this.setTempLawful(lawful);
        }
        this.setPickupItem(template.is_picupitem());
        if (template.is_bravespeed()) {
            this.setBraveSpeed(1);
        } else {
            this.setBraveSpeed(0);
        }
        if (template.get_digestitem() > 0) {
            this._digestItems = Maps.newMap();
        }
        this.setKarma(template.getKarma());
        this.setLightSize(template.getLightSize());

        if (template.talk()) { // NPC????????????
            this.TALK = template.getNpcExecutor();
        }
        if (template.action()) { // NPC????????????
            this.ACTION = template.getNpcExecutor();
        }

        this.mobSkill = new L1MobSkillUse(this);
    }

    /**
     * ??????????????????
     * 
     * @param flag
     */
    public void setWeaponBreaked(final boolean flag) {
        this._weaponBreaked = flag;
    }

    /**
     * ??????AI
     */
    protected void startAI() {
        if (Config.NPCAI_IMPLTYPE == 1) {
            new NpcAITimerImpl().start();
        } else if (Config.NPCAI_IMPLTYPE == 2) {
            new NpcAIThreadImpl().start();
        } else {
            new NpcAITimerImpl().start();
        }
    }

    /**
     * ??????????????????
     * 
     * @param chatTiming
     */
    public void startChat(final int chatTiming) {
        // ??????????????????????????????
        if ((chatTiming == CHAT_TIMING_APPEARANCE) && this.isDead()) {
            return;
        }
        if ((chatTiming == CHAT_TIMING_DEAD) && !this.isDead()) {
            return;
        }
        if ((chatTiming == CHAT_TIMING_HIDE) && this.isDead()) {
            return;
        }
        if ((chatTiming == CHAT_TIMING_GAME_TIME) && this.isDead()) {
            return;
        }

        final int npcId = this.getNpcTemplate().get_npcId();
        L1NpcChat npcChat = null;
        switch (chatTiming) {
            case CHAT_TIMING_APPEARANCE:
                npcChat = NpcChatTable.getInstance().getTemplateAppearance(
                        npcId);
                break;

            case CHAT_TIMING_DEAD:
                npcChat = NpcChatTable.getInstance().getTemplateDead(npcId);
                break;

            case CHAT_TIMING_HIDE:
                npcChat = NpcChatTable.getInstance().getTemplateHide(npcId);
                break;

            case CHAT_TIMING_GAME_TIME:
                npcChat = NpcChatTable.getInstance().getTemplateGameTime(npcId);
                break;
        }
        if (npcChat == null) {
            return;
        }

        final Timer timer = new Timer(true);
        final L1NpcChatTimer npcChatTimer = new L1NpcChatTimer(this, npcChat);
        if (!npcChat.isRepeat()) {
            timer.schedule(npcChatTimer, npcChat.getStartDelayTime());
        } else {
            timer.scheduleAtFixedRate(npcChatTimer,
                    npcChat.getStartDelayTime(), npcChat.getRepeatInterval());
        }
    }

    /**
     * ?????????????????????
     */
    protected synchronized void startDeleteTimer() {
        if (this._deleteTask != null) {
            return;
        }
        this._deleteTask = new DeleteTimer(this.getId());
        this._future = GeneralThreadPool.getInstance().schedule(
                this._deleteTask, Config.NPC_DELETION_TIME * 1000);
    }

    /**
     * HP????????????
     */
    public final void startHpRegeneration() {
        final int hprInterval = this.getNpcTemplate().get_hprinterval();
        final int hpr = this.getNpcTemplate().get_hpr();
        if (!this._hprRunning && (hprInterval > 0) && (hpr > 0)) {
            this._hprTimer = new HprTimer(hpr);
            L1NpcRegenerationTimer.getInstance().scheduleAtFixedRate(
                    this._hprTimer, hprInterval, hprInterval);
            this._hprRunning = true;
        }
    }

    /**
     * MP????????????
     */
    public final void startMpRegeneration() {
        final int mprInterval = this.getNpcTemplate().get_mprinterval();
        final int mpr = this.getNpcTemplate().get_mpr();
        if (!this._mprRunning && (mprInterval > 0) && (mpr > 0)) {
            this._mprTimer = new MprTimer(mpr);
            L1NpcRegenerationTimer.getInstance().scheduleAtFixedRate(
                    this._mprTimer, mprInterval, mprInterval);
            this._mprRunning = true;
        }
    }

    /**
     * ????????????HP
     */
    public final void stopHpRegeneration() {
        if (this._hprRunning) {
            this._hprTimer.cancel();
            this._hprRunning = false;
        }
    }

    /**
     * ????????????MP
     */
    public final void stopMpRegeneration() {
        if (this._mprRunning) {
            this._mprTimer.cancel();
            this._mprRunning = false;
        }
    }

    /**
     * ?????????????????????
     */
    public void tagertClear() {
        if (this._target == null) {
            return;
        }
        this._hateList.remove(this._target);
        this._target = null;
    }

    /**
     * ?????????????????????
     * 
     * @param target
     */
    public void targetRemove(final L1Character target) {
        this._hateList.remove(target);
        if ((this._target != null) && this._target.equals(target)) {
            this._target = null;
        }
    }

    /**
     * ???????????????????????????
     * 
     * @param tx
     *            ????????????
     * @param ty
     *            ????????????
     * @return dir ??????
     */
    public int targetReverseDirection(final int tx, final int ty) {
        int dir = this.targetDirection(tx, ty);
        dir += 4;
        if (dir > 7) {
            dir -= 8;
        }
        return dir;
    }

    /**
     * ???????????????
     * 
     * @param nx
     * @param ny
     * @param dir
     */
    public void teleport(final int nx, final int ny, final int dir) {
        for (final L1PcInstance pc : L1World.getInstance().getRecognizePlayer(
                this)) {
            pc.sendPackets(new S_SkillSound(this.getId(), 169));
            pc.sendPackets(new S_RemoveObject(this));
            pc.removeKnownObject(this);
        }
        this.setX(nx);
        this.setY(ny);
        this.setHeading(dir);
    }

    // NPC?????????NPC???????????????????????????
    /**
     * NPC???????????????NPC???????????????
     * 
     * @param transformId
     */
    protected void transform(final int transformId) {
        this.stopHpRegeneration();
        this.stopMpRegeneration();
        final int transformGfxId = this.getNpcTemplate().getTransformGfxId();
        if (transformGfxId != 0) {
            this.broadcastPacket(new S_SkillSound(this.getId(), transformGfxId));
        }
        final L1Npc npcTemplate = NpcTable.getInstance().getTemplate(
                transformId);
        this.setting_template(npcTemplate);

        this.broadcastPacket(new S_NpcChangeShape(this.getId(), this
                .getTempCharGfx(), this.getLawful(), this.getStatus()));
        for (final L1PcInstance pc : L1World.getInstance().getRecognizePlayer(
                this)) {
            this.onPerceive(pc);
        }

    }

    /**
     * ??????????????????
     * 
     * @param time
     */
    private void useHastePotion(final int time) {
        this.broadcastPacket(new S_SkillHaste(this.getId(), 1, time));
        this.broadcastPacket(new S_SkillSound(this.getId(), 191));
        this.setMoveSpeed(1);
        this.setSkillEffect(STATUS_HASTE, time * 1000);
    }

    /**
     * ??????????????????
     * 
     * @param healHp
     * @param effectId
     */
    private void useHealPotion(int healHp, final int effectId) {
        this.broadcastPacket(new S_SkillSound(this.getId(), effectId));
        if (this.hasSkillEffect(POLLUTE_WATER)) { // ???????????? ???????????????
            healHp /= 2;
        }
        if (this instanceof L1PetInstance) {
            ((L1PetInstance) this).setCurrentHp(this.getCurrentHp() + healHp);
        } else if (this instanceof L1SummonInstance) {
            ((L1SummonInstance) this)
                    .setCurrentHp(this.getCurrentHp() + healHp);
        } else {
            this.setCurrentHpDirect(this.getCurrentHp() + healHp);
        }
    }

    /**
     * ???????????? (NPC???)
     * 
     * @param type
     *            ??????:???????????????
     * @param chance
     *            ????????????
     */
    public void useItem(final int type, final int chance) {

        // ???????????? (???????????????)
        if (this.hasSkillEffect(DECAY_POTION)) {
            return;
        }

        if (Random.nextInt(100) > chance) {
            return; // ????????????
        }

        switch (type) {
            case USEITEM_HEAL: // ??????????????????
                // ?????????
                if (this.getInventory().consumeItem(POTION_OF_GREATER_HEALING,
                        1)) {
                    this.useHealPotion(75, 197);
                } else if (this.getInventory().consumeItem(
                        POTION_OF_EXTRA_HEALING, 1)) {
                    this.useHealPotion(45, 194);
                } else if (this.getInventory()
                        .consumeItem(POTION_OF_HEALING, 1)) {
                    this.useHealPotion(15, 189);
                }
                break;

            case USEITEM_HASTE: // ??????????????????

                // ????????????
                if (this.hasSkillEffect(STATUS_HASTE)) {
                    return; // ??????????????????
                }

                // ??????????????????
                if (this.getInventory().consumeItem(
                        B_POTION_OF_GREATER_HASTE_SELF, 1)) {
                    this.useHastePotion(2100);
                } else if (this.getInventory().consumeItem(
                        POTION_OF_GREATER_HASTE_SELF, 1)) {
                    this.useHastePotion(1800);
                } else if (this.getInventory().consumeItem(
                        B_POTION_OF_HASTE_SELF, 1)) {
                    this.useHastePotion(350);
                } else if (this.getInventory().consumeItem(
                        POTION_OF_HASTE_SELF, 1)) {
                    this.useHastePotion(300);
                }
                break;
        }
    }

}
