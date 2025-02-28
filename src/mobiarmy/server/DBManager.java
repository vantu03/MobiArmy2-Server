package mobiarmy.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DBManager {
    
    private final HikariDataSource dataSource;

    public DBManager(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        // Other HikariCP configurations as needed
        this.dataSource = new HikariDataSource(config);
    }

    public ArrayList<Object[]> selectColumnIndex(String sql, Object... params) throws SQLException {
        ArrayList<Object[]> resultList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; ++i) {
                        row[i] = rs.getObject(i + 1);
                    }
                    resultList.add(row);
                }
            }
        }
        return resultList;
    }
    
    public ArrayList<DataRow> selectColumnName(String sql, Object... params) throws SQLException {
        ArrayList<DataRow> resultList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    DataRow row = new DataRow();
                    for (int i = 1; i <= columnCount; ++i) {
                        String columnName = metaData.getColumnName(i);
                        row.put(columnName, rs.getObject(i));
                    }
                    resultList.add(row);
                }
            }
        }
        return resultList;
    }

    public int update(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }
    
    public int updateWithMap(String sql, HashMap<String, Object> values, Object... params) {
        Object[] params2;
        if (sql.contains("_SET_")) {
            Object[] keys = values.keySet().toArray();
            params2 = new Object[keys.length + params.length];

            StringBuilder setBuilder = new StringBuilder("SET ");
            for (int i = 0; i < keys.length; i++) {
                if (i != 0) {
                    setBuilder.append(", ");
                }
                setBuilder.append(keys[i]).append(" = ?");
                params2[i] = values.get((String)keys[i]);
            }
            sql = sql.replaceFirst("_SET_", setBuilder.toString());
            System.arraycopy(params, 0, params2, keys.length, params.length);
        } else {
            params2 = params; 
        }

        return update(sql, params2);
    }
    
    public int insertWithMap(String tableName, HashMap<String, Object> values) {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        Object[] keys = values.keySet().toArray();
        Object[] params = new Object[keys.length];

        for (int i = 0; i < keys.length; i++) {
            if (i != 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(keys[i]);

            if (values.get((String)keys[i]) == null) {
                placeholders.append("NULL");
                params[i] = null;
            } else {
                placeholders.append("?");
                params[i] = values.get((String)keys[i]);
            }
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns.toString(), placeholders.toString());
        return insertAndGetId(sql, params);
    }

    private int insertAndGetId(String sql, Object... params) {
        int generatedId = -1;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int paramIndex = 1;
            for (Object param : params) {
                if (param != null) {
                    stmt.setObject(paramIndex++, param);
                }
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return generatedId;
    }


    public void close() throws SQLException {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    public static class DataRow extends HashMap<String, Object> {
        
        public long getLong(String columnName) {
            return (long) super.get(columnName);
        }
        
        public int getInt(String columnName) {
            return (int) super.get(columnName);
        }
        
        public byte getByte(String columnName) {
            return (byte) getInt(columnName);
        }
        
        public short getShort(String columnName) {
            return (short) getInt(columnName);
        }
        
        public String getString(String columnName) {
            return (String) super.get(columnName);
        }
        
        public boolean getBoolean(String columnName) {
            return (boolean) super.get(columnName);
        }
    }
}
