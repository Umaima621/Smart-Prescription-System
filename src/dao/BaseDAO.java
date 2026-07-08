package dao;

import db.DatabaseConnection;
import java.sql.Connection;

public abstract class BaseDAO {

    protected Connection conn;

    protected BaseDAO() {
        
        this.conn = null;
    }

    protected Connection getConnection() {
        if (conn == null) {
            this.conn = DatabaseConnection.getInstance().getConnection();
        }
        return conn;
    }

    public abstract String getTableName();
}
