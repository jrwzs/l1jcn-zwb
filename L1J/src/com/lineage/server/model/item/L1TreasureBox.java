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
package com.lineage.server.model.item;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.L1World;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.Random;
import com.lineage.server.utils.collections.Maps;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1TreasureBox {

    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Item {
        @XmlAttribute(name = "ItemId")
        private int _itemId;

        @XmlAttribute(name = "Count")
        private int _count;

        private int _chance;

        @XmlAttribute(name = "Enchant")
        private int _enchant;

        public double getChance() {
            return this._chance;
        }

        public int getCount() {
            return this._count;
        }

        public int getEnchant() {
            return this._enchant;
        }

        public int getItemId() {
            return this._itemId;
        }

        @SuppressWarnings("unused")
        @XmlAttribute(name = "Chance")
        private void setChance(final double chance) {
            this._chance = (int) (chance * 10000);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "TreasureBoxList")
    private static class TreasureBoxList implements Iterable<L1TreasureBox> {
        @XmlElement(name = "TreasureBox")
        private List<L1TreasureBox> _list;

        @Override
        public Iterator<L1TreasureBox> iterator() {
            return this._list.iterator();
        }
    }

    private static enum TYPE {
        RANDOM, SPECIFIC
    }

    private static Logger _log = Logger
            .getLogger(L1TreasureBox.class.getName());

    private static final String PATH = "./data/xml/Item/TreasureBox.xml";

    private static final Map<Integer, L1TreasureBox> _dataMap = Maps.newMap();

    /**
     * ????????????ID???TreasureBox???
     * 
     * @param id
     *            - TreasureBox???ID??????????????????ItemId???
     * @return ???????????????ID???TreasureBox????????????????????????????????????null???
     */
    public static L1TreasureBox get(final int id) {
        return _dataMap.get(id);
    }

    public static void load() {
        final PerformanceTimer timer = new PerformanceTimer();
        System.out.print("?????????????????? TreasureBox...");
        try {
            final JAXBContext context = JAXBContext
                    .newInstance(L1TreasureBox.TreasureBoxList.class);

            final Unmarshaller um = context.createUnmarshaller();

            final File file = new File(PATH);
            final TreasureBoxList list = (TreasureBoxList) um.unmarshal(file);

            for (final L1TreasureBox each : list) {
                each.init();
                _dataMap.put(each.getBoxId(), each);
            }
        } catch (final Exception e) {
            _log.log(Level.SEVERE, PATH + "??????????????????", e);
            System.exit(0);
        }
        System.out.println("??????!\t??????: " + timer.get() + "\t??????");
    }

    private static void storeItem(final L1PcInstance pc,
            final L1ItemInstance item) {
        L1Inventory inventory;

        if (pc.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
            inventory = pc.getInventory();
        } else {
            // ??????????????????????????????????????????????????????????????????
            inventory = L1World.getInstance().getInventory(pc.getLocation());
        }
        inventory.storeItem(item);
        pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // ??????%0%o ???
    }

    @XmlAttribute(name = "ItemId")
    private int _boxId;

    @XmlAttribute(name = "Type")
    private TYPE _type;

    @XmlElement(name = "Item")
    private CopyOnWriteArrayList<Item> _items;

    /** ???????????? */
    private int _totalChance;

    private int getBoxId() {
        return this._boxId;
    }

    private List<Item> getItems() {
        return this._items;
    }

    /** ?????????????????? */
    private int getTotalChance() {
        return this._totalChance;
    }

    private TYPE getType() {
        return this._type;
    }

    private void init() {
        for (final Item each : this.getItems()) {
            this._totalChance += each.getChance();
            if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
                this.getItems().remove(each);
                _log.warning("?????? ID : " + each.getItemId()
                        + " ?????????????????????Template???");
            }
        }
        if ((this.getTotalChance() != 0) && (this.getTotalChance() != 1000000)) {
            _log.warning("ID " + this.getBoxId() + " ????????????????????????100%???");
        }
    }

    /**
     * TreasureBox????????????PC????????????????????????????????????PC??????????????????????????????????????????????????? ????????????????????????????????????
     * 
     * @param pc
     *            - TreasureBox????????????PC
     * @return ??????????????????????????????????????????????????????????????????true???????????? ??????????????????????????????????????????true????????????
     */
    public boolean open(final L1PcInstance pc) {
        L1ItemInstance item = null;

        if (this.getType().equals(TYPE.SPECIFIC)) {
            // ?????????????????????????????????????????????
            for (final Item each : this.getItems()) {
                item = ItemTable.getInstance().createItem(each.getItemId());
                item.setEnchantLevel(each.getEnchant()); // Enchant Feature for
                                                         // treasure_box
                if (item != null) {
                    item.setCount(each.getCount());
                    storeItem(pc, item);
                }
            }

        } else if (this.getType().equals(TYPE.RANDOM)) {
            // ???????????????????????????????????????????????????
            int chance = 0;

            final int r = Random.nextInt(this.getTotalChance());

            for (final Item each : this.getItems()) {
                chance += each.getChance();

                if (r < chance) {
                    item = ItemTable.getInstance().createItem(each.getItemId());
                    item.setEnchantLevel(each.getEnchant()); // Enchant Feature
                                                             // for treasure_box
                    if (item != null) {
                        item.setCount(each.getCount());
                        storeItem(pc, item);
                    }
                    break;
                }
            }
        }

        if (item == null) {
            return false;
        }
        final int itemId = this.getBoxId();

        switch (itemId) {
            case 40411: // ?????????????????????
            case 49013: // ???????????????
                pc.death(null); // ????????????
                break;

            case 46000: // ???????????????
                final L1ItemInstance box = pc.getInventory().findItemId(itemId);
                box.setChargeCount(box.getChargeCount() - 1);
                pc.getInventory().updateItem(box,
                        L1PcInventory.COL_CHARGE_COUNT);
                if (box.getChargeCount() < 1) {
                    pc.getInventory().removeItem(box, 1);
                }
                break;
        }

        return true;
    }
}
