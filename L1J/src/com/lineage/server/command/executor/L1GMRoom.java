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
package com.lineage.server.command.executor;

import com.lineage.server.GMCommandsConfig;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;

public class L1GMRoom implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1GMRoom();
    }

    private L1GMRoom() {
    }

    @Override
    public void execute(final L1PcInstance pc, final String cmdName,
            final String arg) {
        try {
            int i = 0;
            try {
                i = Integer.parseInt(arg);
            } catch (final NumberFormatException e) {
            }

            if (i == 1) {
                L1Teleport.teleport(pc, 32737, 32796, (short) 99, 5, false);
            } else if (i == 2) {
                L1Teleport.teleport(pc, 32734, 32799, (short) 17100, 5, false); // 17100!?
            } else if (i == 3) {
                L1Teleport.teleport(pc, 32644, 32955, (short) 0, 5, false);
            } else if (i == 4) {
                L1Teleport.teleport(pc, 33429, 32814, (short) 4, 5, false);
            } else if (i == 5) {
                L1Teleport.teleport(pc, 32894, 32535, (short) 300, 5, false);
            } else {
                final L1Location loc = GMCommandsConfig.ROOMS.get(arg
                        .toLowerCase());
                if (loc == null) {
                    pc.sendPackets(new S_SystemMessage(arg + " ????????????Room???"));
                    return;
                }
                L1Teleport.teleport(pc, loc.getX(), loc.getY(),
                        (short) loc.getMapId(), 5, false);
            }
        } catch (final Exception exception) {
            pc.sendPackets(new S_SystemMessage(
                    "????????? .gmroom1???.gmroom5 or .gmroom name ???"));
        }
    }
}
