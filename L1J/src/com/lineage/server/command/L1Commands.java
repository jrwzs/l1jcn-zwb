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
package com.lineage.server.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.L1DatabaseFactory;
import com.lineage.server.templates.L1Command;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Lists;

/**
 * 处理 GM 指令
 */
public class L1Commands {
    private static Logger _log = Logger.getLogger(L1Commands.class.getName());

    public static List<L1Command> availableCommandList(final int accessLevel) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        final List<L1Command> result = Lists.newList();
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT * FROM commands WHERE access_level <= ?");
            pstm.setInt(1, accessLevel);
            rs = pstm.executeQuery();
            while (rs.next()) {
                result.add(fromResultSet(rs));
            }
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, "错误的指令", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return result;
    }

    private static L1Command fromResultSet(final ResultSet rs)
            throws SQLException {
        return new L1Command(rs.getString("name"), rs.getInt("access_level"),
                rs.getString("class_name"));
    }

    public static L1Command get(final String name) {
        /*
         * 每次为便于调试和实验，以便读取数据库。缓存性能低于理论是微不足道的。
         */
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM commands WHERE name=?");
            pstm.setString(1, name);
            rs = pstm.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return fromResultSet(rs);
        } catch (final SQLException e) {
            _log.log(Level.SEVERE, "错误的指令", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return null;
    }
}
