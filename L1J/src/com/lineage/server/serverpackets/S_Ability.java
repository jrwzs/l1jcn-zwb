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
package com.lineage.server.serverpackets;

import com.lineage.server.Opcodes;

// Referenced classes of package com.lineage.server.serverpackets:
// ServerBasePacket

/**
 * 戒指
 */
public class S_Ability extends ServerBasePacket {

    private static final String S_ABILITY = "[S] S_Ability";

    private byte[] _byte = null;

    /**
     * 戒指
     * 
     * @param type
     * @param equipped
     */
    public S_Ability(final int type, final boolean equipped) {
        this.buildPacket(type, equipped);
    }

    private void buildPacket(final int type, final boolean equipped) {
        this.writeC(Opcodes.S_OPCODE_ABILITY);
        this.writeC(type); // 1:ROTC 5:ROSC
        if (equipped) {
            this.writeC(0x01);
        } else {
            this.writeC(0x00);
        }
        this.writeC(0x02);
        this.writeH(0x0000);
    }

    @Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return S_ABILITY;
    }
}
