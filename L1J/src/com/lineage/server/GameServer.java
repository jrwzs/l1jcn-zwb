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
package com.lineage.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.logging.Logger;

import com.lineage.Config;
import com.lineage.L1Message;
import com.lineage.console.ConsoleProcess;
import com.lineage.server.datatables.CastleTable;
import com.lineage.server.datatables.CharacterTable;
import com.lineage.server.datatables.ChatLogTable;
import com.lineage.server.datatables.ClanTable;
import com.lineage.server.datatables.DoorTable;
import com.lineage.server.datatables.DropItemTable;
import com.lineage.server.datatables.DropTable;
import com.lineage.server.datatables.FurnitureSpawnTable;
import com.lineage.server.datatables.GetBackRestartTable;
import com.lineage.server.datatables.InnTable;
import com.lineage.server.datatables.IpTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.MagicDollTable;
import com.lineage.server.datatables.MailTable;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.datatables.MobGroupTable;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.datatables.NpcActionTable;
import com.lineage.server.datatables.NpcChatTable;
import com.lineage.server.datatables.NpcSpawnTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.PetTable;
import com.lineage.server.datatables.PetTypeTable;
import com.lineage.server.datatables.PolyTable;
import com.lineage.server.datatables.RaceTicketTable;
import com.lineage.server.datatables.ResolventTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.SpawnTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.datatables.UBSpawnTable;
import com.lineage.server.datatables.WeaponSkillTable;
import com.lineage.server.model.Dungeon;
import com.lineage.server.model.ElementalStoneGenerator;
import com.lineage.server.model.Getback;
import com.lineage.server.model.L1BossCycle;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1DeleteItemOnGround;
import com.lineage.server.model.L1NpcRegenerationTimer;
import com.lineage.server.model.L1World;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.game.L1BugBearRace;
import com.lineage.server.model.gametime.L1GameReStart;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.model.item.L1TreasureBox;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.npc.action.L1NpcDefaultAction;
import com.lineage.server.model.trap.L1WorldTraps;
import com.lineage.server.storage.mysql.MysqlAutoBackup;
import com.lineage.server.utils.MysqlAutoBackupTimer;
import com.lineage.server.utils.SystemUtil;

// Referenced classes of package com.lineage.server:
// ClientThread, Logins, RateTable, IdFactory,
// LoginController, GameTimeController, Announcements,
// MobTable, SpawnTable, SkillsTable, PolyTable,
// TeleportLocations, ShopTable, NPCTalkDataTable, NpcSpawnTable,
// IpTable, Shutdown, NpcTable, MobGroupTable, NpcShoutTable

/**
 * ???????????????
 */
public class GameServer extends Thread {

    /** ????????????????????? */
    private class ServerShutdownThread extends Thread {
        private final int _secondsCount;

        public ServerShutdownThread(final int secondsCount) {
            this._secondsCount = secondsCount;
        }

        @Override
        public void run() {
            final L1World world = L1World.getInstance();
            try {
                int secondsCount = this._secondsCount;
                world.broadcastServerMessage("????????????????????????");
                world.broadcastServerMessage("??????????????????????????????????????????");
                while (0 < secondsCount) {
                    if (secondsCount <= 30) {
                        world.broadcastServerMessage("???????????????" + secondsCount
                                + "????????????????????????????????????????????????????????????");
                    } else {
                        if (secondsCount % 60 == 0) {
                            world.broadcastServerMessage("???????????????" + secondsCount
                                    / 60 + "??????????????????");
                        }
                    }
                    Thread.sleep(1000);
                    secondsCount--;
                }
                GameServer.this.shutdown();
            } catch (final InterruptedException e) {
                world.broadcastServerMessage("?????????????????????????????????????????????????????????");
                return;
            }
        }
    }

    /** ???????????? */
    private static Logger _log = Logger.getLogger(GameServer.class.getName());

    private ServerSocket _serverSocket;

    private static int YesNoCount = 0;

    public static GameServer getInstance() {
        if (_instance == null) {
            _instance = new GameServer();
        }
        return _instance;
    }

    private int _port;

    // private Logins _logins;
    private LoginController _loginController;

    private int chatlvl;

    private static GameServer _instance;

    /**
     * ?????????????????????YesNo?????????
     * 
     * @return YesNo?????????
     */
    public static int getYesNoCount() {
        YesNoCount += 1;
        return YesNoCount;
    }

    private ServerShutdownThread _shutdownThread = null;

    private GameServer() {
        super("GameServer");
    }

    /** ???????????? */
    public synchronized void abortShutdown() {
        if (this._shutdownThread == null) {
            // ??????????????????
            // TODO ?????????????????????????????????
            return;
        }

        this._shutdownThread.interrupt();
        this._shutdownThread = null;
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    public void disconnectAllCharacters() {
        final Collection<L1PcInstance> players = L1World.getInstance()
                .getAllPlayers();
        for (final L1PcInstance pc : players) {
            pc.getNetConnection().setActiveChar(null);
            pc.getNetConnection().kick();
        }
        // ??????????????????????????????
        for (final L1PcInstance pc : players) {
            ClientThread.quitGame(pc);
            L1World.getInstance().removeObject(pc);
            final Account account = Account.load(pc.getAccountName());
            Account.online(account, false);
        }
    }

    /** ????????? */
    public void initialize() throws Exception {
        final String s = Config.GAME_SERVER_HOST_NAME;
        final double rateXp = Config.RATE_XP;
        final double LA = Config.RATE_LA;
        final double rateKarma = Config.RATE_KARMA;
        final double rateDropItems = Config.RATE_DROP_ITEMS;
        final double rateDropAdena = Config.RATE_DROP_ADENA;

        // Locale ????????????
        L1Message.getInstance();

        this.chatlvl = Config.GLOBAL_CHAT_LEVEL;
        this._port = Config.GAME_SERVER_PORT;
        if (!"*".equals(s)) {
            final InetAddress inetaddress = InetAddress.getByName(s);
            inetaddress.getHostAddress();
            this._serverSocket = new ServerSocket(this._port, 50, inetaddress);
            System.out.println(L1Message.setporton + this._port);
        } else {
            this._serverSocket = new ServerSocket(this._port);
            System.out.println(L1Message.setporton + this._port);
        }

        System.out.println("???????????????????????????????????????????????????????????????????????????????????????????????????");
        System.out.println("???     " + L1Message.ver + "\t" + "" + "???");
        System.out.println("???????????????????????????????????????????????????????????????????????????????????????????????????" + "\n");

        System.out.println(L1Message.settingslist + "\n");
        System.out.println("???" + L1Message.exp + ": " + (rateXp) + L1Message.x
                + "\n\r???" + L1Message.justice + ": " + (LA) + L1Message.x
                + "\n\r???" + L1Message.karma + ": " + (rateKarma) + L1Message.x
                + "\n\r???" + L1Message.dropitems + ": " + (rateDropItems)
                + L1Message.x + "\n\r???" + L1Message.dropadena + ": "
                + (rateDropAdena) + L1Message.x + "\n\r???"
                + L1Message.enchantweapon + ": "
                + (Config.ENCHANT_CHANCE_WEAPON) + "%" + "\n\r???"
                + L1Message.enchantarmor + ": " + (Config.ENCHANT_CHANCE_ARMOR)
                + "%");
        System.out.println("???" + L1Message.chatlevel + ": " + (this.chatlvl)
                + L1Message.level);

        if (Config.ALT_NONPVP) { // Non-PvP??????
            System.out.println("???" + L1Message.nonpvpNo + "\n");
        } else {
            System.out.println("???" + L1Message.nonpvpYes + "\n");
        }

        final int maxOnlineUsers = Config.MAX_ONLINE_USERS;
        System.out.println(L1Message.maxplayer + (maxOnlineUsers)
                + L1Message.player);

        System.out.println("???????????????????????????????????????????????????????????????????????????????????????????????????");
        System.out.println("???     " + L1Message.ver + "\t" + "" + "???");
        System.out.println("???????????????????????????????????????????????????????????????????????????????????????????????????" + "\n");

        IdFactory.getInstance();
        L1WorldMap.getInstance();
        this._loginController = LoginController.getInstance();
        this._loginController.setMaxAllowedOnlinePlayers(maxOnlineUsers);

        // ????????????????????????
        CharacterTable.getInstance().loadAllCharName();

        // ??????????????????????????????
        CharacterTable.clearOnlineStatus();

        // ?????????????????????
        L1GameTimeClock.init();

        // ??????????????????????????????
        if (Config.REST_TIME != 0) {
            L1GameReStart.init();
        }

        // ?????????????????????
        final UbTimeController ubTimeContoroller = UbTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(ubTimeContoroller);

        // ???????????????
        final WarTimeController warTimeController = WarTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(warTimeController);

        // ????????????????????????
        if (Config.ELEMENTAL_STONE_AMOUNT > 0) {
            final ElementalStoneGenerator elementalStoneGenerator = ElementalStoneGenerator
                    .getInstance();
            GeneralThreadPool.getInstance().execute(elementalStoneGenerator);
        }

        // ????????? HomeTown ??????
        HomeTownTimeController.getInstance();

        // ?????????????????????
        final AuctionTimeController auctionTimeController = AuctionTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(auctionTimeController);

        // ????????????????????????
        final HouseTaxTimeController houseTaxTimeController = HouseTaxTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(houseTaxTimeController);

        // ???????????????
        final FishingTimeController fishingTimeController = FishingTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(fishingTimeController);

        // ????????? NPC ??????
        final NpcChatTimeController npcChatTimeController = NpcChatTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(npcChatTimeController);

        // ????????? Light
        final LightTimeController lightTimeController = LightTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(lightTimeController);

        // ?????????????????????
        Announcements.getInstance();

        // ???????????????????????????
        if (Config.Announcements_Cycle_Time > 0) {
            AnnouncementsCycle.getInstance();
        }

        // ?????????MySQL??????????????????
        MysqlAutoBackup.getInstance();

        // ?????? MySQL?????????????????? ?????????
        MysqlAutoBackupTimer.TimerStart();

        // ???????????????????????????
        Account.InitialOnlineStatus();

        NpcTable.getInstance();
        final L1DeleteItemOnGround deleteitem = new L1DeleteItemOnGround();
        deleteitem.initialize();

        if (!NpcTable.getInstance().isInitialized()) {
            throw new Exception("??????????????? npc table");
        }
        L1NpcDefaultAction.getInstance();
        DoorTable.initialize();
        SpawnTable.getInstance();
        MobGroupTable.getInstance();
        SkillsTable.getInstance();
        PolyTable.getInstance();
        ItemTable.getInstance();
        DropTable.getInstance();
        DropItemTable.getInstance();
        ShopTable.getInstance();
        NPCTalkDataTable.getInstance();
        L1World.getInstance();
        L1WorldTraps.getInstance();
        Dungeon.getInstance();
        NpcSpawnTable.getInstance();
        IpTable.getInstance();
        MapsTable.getInstance();
        UBSpawnTable.getInstance();
        PetTable.getInstance();
        ClanTable.getInstance();
        CastleTable.getInstance();
        L1CastleLocation.setCastleTaxRate(); // ?????? CastleTable ???????????????
        GetBackRestartTable.getInstance();
        GeneralThreadPool.getInstance();
        L1NpcRegenerationTimer.getInstance();
        ChatLogTable.getInstance();
        WeaponSkillTable.getInstance();
        NpcActionTable.load();
        GMCommandsConfig.load();
        Getback.loadGetBack();
        PetTypeTable.load();
        L1BossCycle.load();
        L1TreasureBox.load();
        SprTable.getInstance();
        ResolventTable.getInstance();
        FurnitureSpawnTable.getInstance();
        NpcChatTable.getInstance();
        MailTable.getInstance();
        RaceTicketTable.getInstance();
        L1BugBearRace.getInstance();
        InnTable.getInstance();
        MagicDollTable.getInstance();

        System.out.println(L1Message.initialfinished);
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        // cmd????????????
        if (Config.CmdActive) {
            final Thread cp = new ConsoleProcess();
            cp.start();
        }

        this.start();
    }

    @Override
    public void run() {
        System.out.println(L1Message.memoryUse + SystemUtil.getUsedMemoryMB()
                + L1Message.memory);
        System.out.println(L1Message.waitingforuser);
        while (true) {
            try {
                final Socket socket = this._serverSocket.accept();
                System.out.println(L1Message.from + socket.getInetAddress()
                        + L1Message.attempt);
                final String host = socket.getInetAddress().getHostAddress();
                if (IpTable.getInstance().isBannedIp(host)) {
                    _log.info("??????IP (" + host + ")");
                } else {
                    final ClientThread client = new ClientThread(socket);
                    GeneralThreadPool.getInstance().execute(client);
                }
            } catch (final IOException ioexception) {
            }
        }
    }

    /** ?????? */
    public void shutdown() {
        this.disconnectAllCharacters();
        System.exit(0);
    }

    /** ??????????????? */
    public synchronized void shutdownWithCountdown(final int secondsCount) {
        if (this._shutdownThread != null) {
            // ??????????????????
            // TODO ?????????????????????????????????
            return;
        }
        this._shutdownThread = new ServerShutdownThread(secondsCount);
        GeneralThreadPool.getInstance().execute(this._shutdownThread);
    }
}
