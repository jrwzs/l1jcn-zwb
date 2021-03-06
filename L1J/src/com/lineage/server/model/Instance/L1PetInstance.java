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

import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.PetItemTable;
import com.lineage.server.datatables.PetTable;
import com.lineage.server.datatables.PetTypeTable;
import com.lineage.server.model.L1Attack;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PetFood;
import com.lineage.server.model.L1World;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_HPMeter;
import com.lineage.server.serverpackets.S_NpcChatPacket;
import com.lineage.server.serverpackets.S_PetCtrlMenu;
import com.lineage.server.serverpackets.S_PetMenuPacket;
import com.lineage.server.serverpackets.S_PetPack;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SummonPack;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.templates.L1PetItem;
import com.lineage.server.templates.L1PetType;
import com.lineage.server.utils.Random;

/**
 * ???????????????
 */
public class L1PetInstance extends L1NpcInstance {

    private static final long serialVersionUID = 1L;

    private int _dir;

    /** ?????? */
    private L1ItemInstance _weapon;

    /** ?????? */
    private L1ItemInstance _armor;

    /** ?????????????????? */
    private int _hitByWeapon;

    /** ??????????????? */
    private int _damageByWeapon;

    /** ?????????????????? */
    private int _currentPetStatus;

    /** ???????????? */
    private final L1PcInstance _petMaster;

    /** ???????????????ID */
    private int _itemObjId;

    /** ?????? */
    private L1PetType _type;

    /** EXP???????????? */
    private int _expPercent;

    /** ???????????????????????? */
    private L1PetFood _petFood;

    /** ???????????? */
    public L1PetInstance(final L1Npc template, final L1PcInstance master,
            final L1Pet l1pet) {
        super(template);

        this._petMaster = master;
        this._itemObjId = l1pet.get_itemobjid();
        this._type = PetTypeTable.getInstance().get(template.get_npcId());

        // ????????????
        this.setId(l1pet.get_objid());
        this.setName(l1pet.get_name());
        this.setLevel(l1pet.get_level());
        // HPMP???MAX?????????
        this.setMaxHp(l1pet.get_hp());
        this.setCurrentHpDirect(l1pet.get_hp());
        this.setMaxMp(l1pet.get_mp());
        this.setCurrentMpDirect(l1pet.get_mp());
        this.setExp(l1pet.get_exp());
        this.setExpPercent(ExpTable.getExpPercentage(l1pet.get_level(),
                l1pet.get_exp()));
        this.setLawful(l1pet.get_lawful());
        this.setTempLawful(l1pet.get_lawful());
        this.set_food(l1pet.get_food());
        // ????????????????????????
        this.startFoodTimer(this);

        this.setMaster(master);
        this.setX(master.getX() + Random.nextInt(5) - 2);
        this.setY(master.getY() + Random.nextInt(5) - 2);
        this.setMap(master.getMapId());
        this.setHeading(5);
        this.setLightSize(template.getLightSize());

        this._currentPetStatus = 3;

        L1World.getInstance().storeObject(this);
        L1World.getInstance().addVisibleObject(this);
        for (final L1PcInstance pc : L1World.getInstance().getRecognizePlayer(
                this)) {
            this.onPerceive(pc);
        }
        master.addPet(this);
    }

    /** ???????????? */
    public L1PetInstance(final L1NpcInstance target, final L1PcInstance master,
            final int itemid) {
        super(null);

        this._petMaster = master;
        this._itemObjId = itemid;
        this._type = PetTypeTable.getInstance().get(
                target.getNpcTemplate().get_npcId());

        // ????????????
        this.setId(IdFactory.getInstance().nextId());
        this.setting_template(target.getNpcTemplate());
        this.setCurrentHpDirect(target.getCurrentHp());
        this.setCurrentMpDirect(target.getCurrentMp());
        this.setExp(750); // Lv.5???EXP
        this.setExpPercent(0);
        this.setLawful(0);
        this.setTempLawful(0);
        this.set_food(50); // ??????????????????
        this.startFoodTimer(this); // ????????????????????????

        this.setMaster(master);
        this.setX(target.getX());
        this.setY(target.getY());
        this.setMap(target.getMapId());
        this.setHeading(target.getHeading());
        this.setLightSize(target.getLightSize());
        this.setPetcost(6);
        this.setInventory(target.getInventory());
        target.setInventory(null);

        this._currentPetStatus = 3;
        /* ?????????????????????&?????? */
        this.stopHpRegeneration();
        if (this.getMaxHp() > this.getCurrentHp()) {
            this.startHpRegeneration();
        }
        this.stopMpRegeneration();
        if (this.getMaxMp() > this.getCurrentMp()) {
            this.startMpRegeneration();
        }
        target.deleteMe();
        L1World.getInstance().storeObject(this);
        L1World.getInstance().addVisibleObject(this);
        for (final L1PcInstance pc : L1World.getInstance().getRecognizePlayer(
                this)) {
            this.onPerceive(pc);
        }

        master.addPet(this);
        PetTable.getInstance().storeNewPet(target, this.getId(), itemid);
    }

    /** ?????????????????? */
    private int actionType(final String action) {
        int status = 0;
        if (action.equalsIgnoreCase("aggressive")) { // ????????????
            status = 1;
        } else if (action.equalsIgnoreCase("defensive")) { // ????????????
            status = 2;
        } else if (action.equalsIgnoreCase("stay")) { // ??????
            status = 3;
        } else if (action.equalsIgnoreCase("extend")) { // ??????
            status = 4;
        } else if (action.equalsIgnoreCase("alert")) { // ??????
            status = 5;
        } else if (action.equalsIgnoreCase("dismiss")) { // ??????
            status = 6;
        } else if (action.equalsIgnoreCase("getitem")) { // ??????
            this.collect(false);
        }
        return status;
    }

    /** ?????????????????? */
    public void call() {
        int id = this._type.getMessageId(L1PetType.getMessageNumber(this
                .getLevel()));
        if ((id != 0) && !this.isDead()) {
            if (this.get_food() == 0) {
                id = this._type.getDefyMessageId();
            }
            this.broadcastPacket(new S_NpcChatPacket(this, "$" + id, 0));
        }

        if (this.get_food() > 0) {
            this.setCurrentPetStatus(7); // ???????????????????????????
        } else {
            this.setCurrentPetStatus(3); // ??????
        }
    }

    /** ???????????????????????? */
    public void collect(final boolean isDepositnpc) {
        L1Inventory targetInventory = this._petMaster.getInventory();
        final List<L1ItemInstance> itemList = this.getInventory().getItems();
        for (final Object itemObject : itemList) {
            final L1ItemInstance item = (L1ItemInstance) itemObject;
            if (item == null) {
                continue;
            }
            if (item.isEquipped()) { // ?????????
                if (!isDepositnpc) { // ???????????????
                    continue;
                }
                final L1PetItem petItem = PetItemTable.getInstance()
                        .getTemplate(item.getItemId());
                if (petItem.getUseType() == 1) { // ??????
                    this.setWeapon(null);
                } else if (petItem.getUseType() == 0) { // ??????
                    this.setArmor(null);
                }
                item.setEquipped(false);
            }
            if (this._petMaster.getInventory().checkAddItem( // ????????????????????????
                    item, item.getCount()) == L1Inventory.OK) {
                this.getInventory().tradeItem(item, item.getCount(),
                        targetInventory);
                this._petMaster.sendPackets(new S_ServerMessage(143, this
                        .getName(), item.getLogName()));
            } else { // ????????????
                targetInventory = L1World.getInstance().getInventory(
                        this.getX(), this.getY(), this.getMapId());
                this.getInventory().tradeItem(item, item.getCount(),
                        targetInventory);
            }
        }
    }

    public synchronized void death(final L1Character lastAttacker) {
        if (!this.isDead()) {
            this.setDead(true);
            // ????????????????????????
            this.stopFoodTimer(this);
            this.setStatus(ActionCodes.ACTION_Die);
            this.setCurrentHp(0);

            this.getMap().setPassable(this.getLocation(), true);
            this.broadcastPacket(new S_DoActionGFX(this.getId(),
                    ActionCodes.ACTION_Die));
        }
    }

    /** ??????????????????????????????????????? */
    public void dropItem() {
        final L1Inventory targetInventory = L1World.getInstance().getInventory(
                this.getX(), this.getY(), this.getMapId());
        final List<L1ItemInstance> items = this._inventory.getItems();
        final int size = this._inventory.getSize();
        for (int i = 0; i < size; i++) {
            final L1ItemInstance item = items.get(0);
            if (item.isEquipped()) { // ?????????
                final L1PetItem petItem = PetItemTable.getInstance()
                        .getTemplate(item.getItemId());
                if (petItem.getUseType() == 1) { // ??????
                    this.setWeapon(null);
                } else if (petItem.getUseType() == 0) { // ??????
                    this.setArmor(null);
                }
                item.setEquipped(false);
            }
            this._inventory.tradeItem(item, item.getCount(), targetInventory);
        }
    }

    /** ???????????? */
    public void evolvePet(final int new_itemobjid) {

        final L1Pet l1pet = PetTable.getInstance().getTemplate(this._itemObjId);
        if (l1pet == null) {
            return;
        }

        final int newNpcId = this._type.getNpcIdForEvolving();
        final int evolvItem = this._type.getEvolvItemId();
        // ???????????????????????????
        final int tmpMaxHp = this.getMaxHp();
        final int tmpMaxMp = this.getMaxMp();

        this.transform(newNpcId);
        this._type = PetTypeTable.getInstance().get(newNpcId);

        this.setLevel(1);
        // ?????????????????????
        this.setMaxHp(tmpMaxHp / 2);
        this.setMaxMp(tmpMaxMp / 2);
        this.setCurrentHpDirect(this.getMaxHp());
        this.setCurrentMpDirect(this.getMaxMp());
        this.setExp(0);
        this.setExpPercent(0);
        this.getInventory().consumeItem(evolvItem, 1); // ??????????????????

        // ?????????????????????????????????????????????????????????
        final L1Object obj = L1World.getInstance()
                .findObject(l1pet.get_objid());
        if ((obj != null) && (obj instanceof L1NpcInstance)) {
            final L1PetInstance new_pet = (L1PetInstance) obj;
            L1Inventory new_petInventory = new_pet.getInventory();
            final List<L1ItemInstance> itemList = this.getInventory()
                    .getItems();
            for (final Object itemObject : itemList) {
                final L1ItemInstance item = (L1ItemInstance) itemObject;
                if (item == null) {
                    continue;
                }
                if (item.isEquipped()) { // ?????????
                    item.setEquipped(false);
                    final L1PetItem petItem = PetItemTable.getInstance()
                            .getTemplate(item.getItemId());
                    if (petItem.getUseType() == 1) { // ??????
                        this.setWeapon(null);
                        new_pet.usePetWeapon(this, item);
                    } else if (petItem.getUseType() == 0) { // ??????
                        this.setArmor(null);
                        new_pet.usePetArmor(this, item);
                    }
                }
                if (new_pet.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                    this.getInventory().tradeItem(item, item.getCount(),
                            new_petInventory);
                } else { // ????????????
                    new_petInventory = L1World.getInstance().getInventory(
                            this.getX(), this.getY(), this.getMapId());
                    this.getInventory().tradeItem(item, item.getCount(),
                            new_petInventory);
                }
            }
            new_pet.broadcastPacket(new S_SkillSound(new_pet.getId(), 2127)); // ????????????
        }

        // ?????????????????????
        PetTable.getInstance().deletePet(this._itemObjId);

        // ?????????????????????
        l1pet.set_itemobjid(new_itemobjid);
        l1pet.set_npcid(newNpcId);
        l1pet.set_name(this.getName());
        l1pet.set_level(this.getLevel());
        l1pet.set_hp(this.getMaxHp());
        l1pet.set_mp(this.getMaxMp());
        l1pet.set_exp((int) this.getExp());
        l1pet.set_food(this.get_food());

        PetTable.getInstance().storeNewPet(this, this.getId(), new_itemobjid);

        this._itemObjId = new_itemobjid;
        // ????????????????????????
        if ((obj != null) && (obj instanceof L1NpcInstance)) {
            final L1PetInstance new_pet = (L1PetInstance) obj;
            this.startFoodTimer(new_pet);
        }
    }

    /** ???????????? */
    public L1ItemInstance getArmor() {
        return this._armor;
    }

    /** ???????????????????????? */
    public int getCurrentPetStatus() {
        return this._currentPetStatus;
    }

    /** ????????????????????? */
    public int getDamageByWeapon() {
        return this._damageByWeapon;
    }

    /** ??????EXP???????????? */
    public int getExpPercent() {
        return this._expPercent;
    }

    /** ???????????????????????? */
    public int getHitByWeapon() {
        return this._hitByWeapon;
    }

    /** ?????????????????????ID */
    public int getItemObjId() {
        return this._itemObjId;
    }

    /** ?????????????????? */
    public L1PetType getPetType() {
        return this._type;
    }

    /** ???????????? */
    public L1ItemInstance getWeapon() {
        return this._weapon;
    }

    /** ???????????? */
    public void liberate() {
        final L1MonsterInstance monster = new L1MonsterInstance(
                this.getNpcTemplate());
        monster.setId(IdFactory.getInstance().nextId());

        monster.setX(this.getX());
        monster.setY(this.getY());
        monster.setMap(this.getMapId());
        monster.setHeading(this.getHeading());
        monster.set_storeDroped(true);
        monster.setInventory(this.getInventory());
        this.setInventory(null);
        monster.setLevel(this.getLevel());
        monster.setMaxHp(this.getMaxHp());
        monster.setCurrentHpDirect(this.getCurrentHp());
        monster.setMaxMp(this.getMaxMp());
        monster.setCurrentMpDirect(this.getCurrentMp());

        this._petMaster.getPetList().remove(this.getId());
        if (this._petMaster.getPetList().isEmpty()) {
            this._petMaster.sendPackets(new S_PetCtrlMenu(this._master,
                    monster, false)); // ??????????????????????????????
        }

        this.deleteMe();

        // ??????PetTable???DB???????????????
        this._petMaster.getInventory().removeItem(this._itemObjId, 1);
        PetTable.getInstance().deletePet(this._itemObjId);

        L1World.getInstance().storeObject(monster);
        L1World.getInstance().addVisibleObject(monster);
        for (final L1PcInstance pc : L1World.getInstance().getRecognizePlayer(
                monster)) {
            this.onPerceive(pc);
        }
    }

    // ???????????????????????????
    @Override
    public boolean noTarget() {
        switch (this._currentPetStatus) {
            case 3: // ??????
                return true;
            case 4: // ??????
                if ((this._petMaster != null)
                        && (this._petMaster.getMapId() == this.getMapId())
                        && (this.getLocation().getTileLineDistance(
                                this._petMaster.getLocation()) < 5)) {
                    this._dir = this.targetReverseDirection(
                            this._petMaster.getX(), this._petMaster.getY());
                    this._dir = checkObject(this.getX(), this.getY(),
                            this.getMapId(), this._dir);
                    this.setDirectionMove(this._dir);
                    this.setSleepTime(this.calcSleepTime(this.getPassispeed(),
                            MOVE_SPEED));
                } else { // ???????????? 5???????????????
                    this._currentPetStatus = 3;
                    return true;
                }
                return false;
            case 5: // ??????
                if ((Math.abs(this.getHomeX() - this.getX()) > 1)
                        || (Math.abs(this.getHomeY() - this.getY()) > 1)) {
                    final int dir = this.moveDirection(this.getHomeX(),
                            this.getHomeY());
                    if (dir == -1) {
                        this.setHomeX(this.getX());
                        this.setHomeY(this.getY());
                    } else {
                        this.setDirectionMove(dir);
                        this.setSleepTime(this.calcSleepTime(
                                this.getPassispeed(), MOVE_SPEED));
                    }
                }
                return false;
            case 7: // ????????????
                if ((this._petMaster != null)
                        && (this._petMaster.getMapId() == this.getMapId())
                        && (this.getLocation().getTileLineDistance(
                                this._petMaster.getLocation()) <= 1)) {
                    this._currentPetStatus = 3;
                    return true;
                }
                final int locx = this._petMaster.getX() + Random.nextInt(1);
                final int locy = this._petMaster.getY() + Random.nextInt(1);
                this._dir = this.moveDirection(locx, locy);
                if (this._dir == -1) {
                    this._currentPetStatus = 3;
                    return true;
                }
                this.setDirectionMove(this._dir);
                this.setSleepTime(this.calcSleepTime(this.getPassispeed(),
                        MOVE_SPEED));
                return false;
            default:
                if ((this._petMaster != null)
                        && (this._petMaster.getMapId() == this.getMapId())) {
                    if (this.getLocation().getTileLineDistance(
                            this._petMaster.getLocation()) > 2) {
                        this._dir = this.moveDirection(this._petMaster.getX(),
                                this._petMaster.getY());
                        this.setDirectionMove(this._dir);
                        this.setSleepTime(this.calcSleepTime(
                                this.getPassispeed(), MOVE_SPEED));
                    }
                } else { // ????????????????????????
                    this._currentPetStatus = 3;
                    return true;
                }
                return false;
        }
    }

    @Override
    public void onAction(final L1PcInstance pc) {
        this.onAction(pc, 0);
    }

    @Override
    public void onAction(final L1PcInstance pc, final int skillId) {
        final L1Character cha = this.getMaster();
        final L1PcInstance master = (L1PcInstance) cha;
        if (master.isTeleport()) { // ???????????????
            return;
        }
        if (this.getZoneType() == 1) { // ??????????????????
            final L1Attack attack_mortion = new L1Attack(pc, this, skillId); // ??????????????????
            attack_mortion.action();
            return;
        }

        if (pc.checkNonPvP(pc, this)) {
            return;
        }

        final L1Attack attack = new L1Attack(pc, this, skillId);
        if (attack.calcHit()) {
            attack.calcDamage();
        }
        attack.action();
        attack.commit();
    }

    @Override
    public void onFinalAction(final L1PcInstance player, final String action) {
        final int status = this.actionType(action);
        if (status == 0) {
            return;
        }
        if (status == 6) {
            final L1PcInstance petMaster = (L1PcInstance) this._master;
            this.liberate(); // ????????????
            // ????????????????????????
            final Object[] petList = petMaster.getPetList().values().toArray();
            for (final Object petObject : petList) {
                if (petObject instanceof L1SummonInstance) {
                    final L1SummonInstance summon = (L1SummonInstance) petObject;
                    petMaster.sendPackets(new S_SummonPack(summon, petMaster));
                    return;
                } else if (petObject instanceof L1PetInstance) {
                    final L1PetInstance pet = (L1PetInstance) petObject;
                    petMaster.sendPackets(new S_PetPack(pet, petMaster));
                    return;
                }
            }
        } else {
            // ???????????????????????????????????????????????????
            final Object[] petList = this._petMaster.getPetList().values()
                    .toArray();
            for (final Object petObject : petList) {
                if (petObject instanceof L1PetInstance) { // ??????
                    final L1PetInstance pet = (L1PetInstance) petObject;
                    if ((this._petMaster != null)
                            && (this._petMaster.getLevel() >= pet.getLevel())
                            && (pet.get_food() > 0)) {
                        pet.setCurrentPetStatus(status);
                    } else {
                        if (!pet.isDead()) {
                            final L1PetType type = PetTypeTable.getInstance()
                                    .get(pet.getNpcTemplate().get_npcId());
                            final int id = type.getDefyMessageId();
                            if (id != 0) {
                                pet.broadcastPacket(new S_NpcChatPacket(pet,
                                        "$" + id, 0));
                            }
                        }
                    }
                } else if (petObject instanceof L1SummonInstance) { // ?????????
                    final L1SummonInstance summon = (L1SummonInstance) petObject;
                    summon.set_currentPetStatus(status);
                }
            }
        }
    }

    @Override
    public void onGetItem(final L1ItemInstance item) {
        if (this.getNpcTemplate().get_digestitem() > 0) {
            this.setDigestItem(item);
        }
        Arrays.sort(healPotions);
        Arrays.sort(haestPotions);
        if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
            if (this.getCurrentHp() != this.getMaxHp()) {
                this.useItem(USEITEM_HEAL, 100);
            }
        } else if (Arrays
                .binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
            this.useItem(USEITEM_HASTE, 100);
        }
    }

    @Override
    public void onItemUse() {
        if (!this.isActived()) {
            this.useItem(USEITEM_HASTE, 100); // ?????????????????????????????????????????????????????????
        }
        if (this.getCurrentHp() * 100 / this.getMaxHp() < 40) { // ??????????????????????????????
            this.useItem(USEITEM_HEAL, 100); // ???????????????????????????????????????????????????
        }
    }

    @Override
    public void onPerceive(final L1PcInstance perceivedFrom) {
        perceivedFrom.addKnownObject(this);
        perceivedFrom.sendPackets(new S_PetPack(this, perceivedFrom)); // PET???????????????
        if (this.isDead()) {
            perceivedFrom.sendPackets(new S_DoActionGFX(this.getId(),
                    ActionCodes.ACTION_Die));
        }
    }

    @Override
    public void onTalkAction(final L1PcInstance player) {
        if (this.isDead()) {
            return;
        }
        if (this._petMaster.equals(player)) {
            player.sendPackets(new S_PetMenuPacket(this, this.getExpPercent()));
            final L1Pet l1pet = PetTable.getInstance().getTemplate(
                    this._itemObjId);
            // XXX ????????????????????????????????????DB??????????????????????????????
            if (l1pet != null) {
                l1pet.set_exp((int) this.getExp());
                l1pet.set_level(this.getLevel());
                l1pet.set_hp(this.getMaxHp());
                l1pet.set_mp(this.getMaxMp());
                l1pet.set_food(this.get_food());
                PetTable.getInstance().storePet(l1pet); // DB???????????????
            }
        }
    }

    // ???????????????????????????????????????????????????
    @Override
    public void receiveDamage(final L1Character attacker, int damage) {
        if (this.getCurrentHp() > 0) {
            if (damage > 0) { // ????????????????????????????????????
                this.setHate(attacker, 0); // ???????????????????????????
                this.removeSkillEffect(FOG_OF_SLEEPING);
            }

            if ((attacker instanceof L1PcInstance) && (damage > 0)) {
                final L1PcInstance player = (L1PcInstance) attacker;
                player.setPetTarget(this);
            }

            if (attacker instanceof L1PetInstance) {
                final L1PetInstance pet = (L1PetInstance) attacker;
                // ???????????????????????????????????????NOPVP
                if ((this.getZoneType() == 1) || (pet.getZoneType() == 1)) {
                    damage = 0;
                }
            } else if (attacker instanceof L1SummonInstance) {
                final L1SummonInstance summon = (L1SummonInstance) attacker;
                // ???????????????????????????????????????NOPVP
                if ((this.getZoneType() == 1) || (summon.getZoneType() == 1)) {
                    damage = 0;
                }
            }

            final int newHp = this.getCurrentHp() - damage;
            if (newHp <= 0) {
                this.death(attacker);
            } else {
                this.setCurrentHp(newHp);
            }
        } else if (!this.isDead()) { // ????????????
            this.death(attacker);
        }
    }

    /** ?????????????????? */
    private void removePetArmor(final L1PetInstance pet,
            final L1ItemInstance armor) {
        final int itemId = armor.getItem().getItemId();
        final L1PetItem petItem = PetItemTable.getInstance()
                .getTemplate(itemId);
        if (petItem == null) {
            return;
        }

        pet.addAc(-petItem.getAddAc());
        pet.addStr(-petItem.getAddStr());
        pet.addCon(-petItem.getAddCon());
        pet.addDex(-petItem.getAddDex());
        pet.addInt(-petItem.getAddInt());
        pet.addWis(-petItem.getAddWis());
        pet.addMaxHp(-petItem.getAddHp());
        pet.addMaxMp(-petItem.getAddMp());
        pet.addSp(-petItem.getAddSp());
        pet.addMr(-petItem.getAddMr());

        pet.setArmor(null);
        armor.setEquipped(false);
    }

    /** ?????????????????? */
    private void removePetWeapon(final L1PetInstance pet,
            final L1ItemInstance weapon) {
        final int itemId = weapon.getItem().getItemId();
        final L1PetItem petItem = PetItemTable.getInstance()
                .getTemplate(itemId);
        if (petItem == null) {
            return;
        }

        pet.setHitByWeapon(0);
        pet.setDamageByWeapon(0);
        pet.addStr(-petItem.getAddStr());
        pet.addCon(-petItem.getAddCon());
        pet.addDex(-petItem.getAddDex());
        pet.addInt(-petItem.getAddInt());
        pet.addWis(-petItem.getAddWis());
        pet.addMaxHp(-petItem.getAddHp());
        pet.addMaxMp(-petItem.getAddMp());
        pet.addSp(-petItem.getAddSp());
        pet.addMr(-petItem.getAddMr());

        pet.setWeapon(null);
        weapon.setEquipped(false);
    }

    /** ???????????? */
    public void setArmor(final L1ItemInstance armor) {
        this._armor = armor;
    }

    @Override
    public void setCurrentHp(final int i) {
        int currentHp = i;
        if (currentHp >= this.getMaxHp()) {
            currentHp = this.getMaxHp();
        }
        this.setCurrentHpDirect(currentHp);

        if (this.getMaxHp() > this.getCurrentHp()) {
            this.startHpRegeneration();
        }

        if (this._petMaster != null) {
            final int HpRatio = 100 * currentHp / this.getMaxHp();
            final L1PcInstance Master = this._petMaster;
            Master.sendPackets(new S_HPMeter(this.getId(), HpRatio));
        }
    }

    @Override
    public void setCurrentMp(final int i) {
        int currentMp = i;
        if (currentMp >= this.getMaxMp()) {
            currentMp = this.getMaxMp();
        }
        this.setCurrentMpDirect(currentMp);

        if (this.getMaxMp() > this.getCurrentMp()) {
            this.startMpRegeneration();
        }
    }

    /** ???????????????????????? */
    public void setCurrentPetStatus(final int i) {
        this._currentPetStatus = i;
        if (this._currentPetStatus == 5) {
            this.setHomeX(this.getX());
            this.setHomeY(this.getY());
        }
        if (this._currentPetStatus == 7) {
            this.allTargetClear();
        }

        if (this._currentPetStatus == 3) {
            this.allTargetClear();
        } else {
            if (!this.isAiRunning()) {
                this.startAI();
            }
        }
    }

    /** ????????????????????? */
    public void setDamageByWeapon(final int i) {
        this._damageByWeapon = i;
    }

    /** ??????EXP???????????? */
    public void setExpPercent(final int expPercent) {
        this._expPercent = expPercent;
    }

    /** ???????????????????????? */
    public void setHitByWeapon(final int i) {
        this._hitByWeapon = i;
    }

    /** ???????????????????????? */
    public void setMasterTarget(final L1Character target) {
        if ((target != null)
                && ((this._currentPetStatus == 1) || (this._currentPetStatus == 5))
                && (this.get_food() > 0)) {
            this.setHate(target, 0);
            if (!this.isAiRunning()) {
                this.startAI();
            }
        }
    }

    /** ?????????????????? */
    private void setPetArmor(final L1PetInstance pet, final L1ItemInstance armor) {
        final int itemId = armor.getItem().getItemId();
        final L1PetItem petItem = PetItemTable.getInstance()
                .getTemplate(itemId);
        if (petItem == null) {
            return;
        }

        pet.addAc(petItem.getAddAc());
        pet.addStr(petItem.getAddStr());
        pet.addCon(petItem.getAddCon());
        pet.addDex(petItem.getAddDex());
        pet.addInt(petItem.getAddInt());
        pet.addWis(petItem.getAddWis());
        pet.addMaxHp(petItem.getAddHp());
        pet.addMaxMp(petItem.getAddMp());
        pet.addSp(petItem.getAddSp());
        pet.addMr(petItem.getAddMr());

        pet.setArmor(armor);
        armor.setEquipped(true);
    }

    /** ?????????????????? */
    private void setPetWeapon(final L1PetInstance pet,
            final L1ItemInstance weapon) {
        final int itemId = weapon.getItem().getItemId();
        final L1PetItem petItem = PetItemTable.getInstance()
                .getTemplate(itemId);
        if (petItem == null) {
            return;
        }

        pet.setHitByWeapon(petItem.getHitModifier());
        pet.setDamageByWeapon(petItem.getDamageModifier());
        pet.addStr(petItem.getAddStr());
        pet.addCon(petItem.getAddCon());
        pet.addDex(petItem.getAddDex());
        pet.addInt(petItem.getAddInt());
        pet.addWis(petItem.getAddWis());
        pet.addMaxHp(petItem.getAddHp());
        pet.addMaxMp(petItem.getAddMp());
        pet.addSp(petItem.getAddSp());
        pet.addMr(petItem.getAddMr());

        pet.setWeapon(weapon);
        weapon.setEquipped(true);
    }

    /** ????????????????????? */
    public void setTarget(final L1Character target) {
        if ((target != null)
                && ((this._currentPetStatus == 1)
                        || (this._currentPetStatus == 2) || (this._currentPetStatus == 5))
                && (this.get_food() > 0)) {
            this.setHate(target, 0);
            if (!this.isAiRunning()) {
                this.startAI();
            }
        }
    }

    /** ???????????? */
    public void setWeapon(final L1ItemInstance weapon) {
        this._weapon = weapon;
    }

    /** ?????????????????????????????? */
    public void startFoodTimer(final L1PetInstance pet) {
        this._petFood = new L1PetFood(pet, this._itemObjId);
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(this._petFood, 1000, 200000); // ??? X?????????
    }

    /** ?????????????????????????????? */
    public void stopFoodTimer(final L1PetInstance pet) {
        if (this._petFood != null) {
            this._petFood.cancel();
            this._petFood = null;
        }
    }

    /** ??????????????????(??????) */
    public void usePetArmor(final L1PetInstance pet, final L1ItemInstance armor) {
        if (pet.getArmor() == null) {
            this.setPetArmor(pet, armor);
        } else { // ??????????????????????????????????????????????????????
            if (pet.getArmor().equals(armor)) {
                this.removePetArmor(pet, pet.getArmor());
            } else {
                this.removePetArmor(pet, pet.getArmor());
                this.setPetArmor(pet, armor);
            }
        }
    }

    /** ??????????????????(??????) */
    public void usePetWeapon(final L1PetInstance pet,
            final L1ItemInstance weapon) {
        if (pet.getWeapon() == null) {
            this.setPetWeapon(pet, weapon);
        } else { // ??????????????????????????????????????????????????????
            if (pet.getWeapon().equals(weapon)) {
                this.removePetWeapon(pet, pet.getWeapon());
            } else {
                this.removePetWeapon(pet, pet.getWeapon());
                this.setPetWeapon(pet, weapon);
            }
        }
    }
}
