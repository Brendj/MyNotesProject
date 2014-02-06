package ru.axetta;

import org.apache.log4j.*;

import java.sql.*;

public class DbWrapper {

  private String protocol;
  private String url;
  private String user;
  private String password;
  private static Logger logger = Logger.getLogger(DbWrapper.class);

  public DbWrapper(String protocol, String url, String user, String password) {
    this.protocol = protocol;
    this.url = url;
    this.user = user;
    this.password = password;
  }

  public Connection getConnection() {
    try {
      Class.forName(protocol);
      return DriverManager.getConnection(this.url, this.user, this.password);
    } catch (SQLException ex) {
      logger.error("Database error: " + ex.getMessage());
      throw new RuntimeException("Connection with Database failed! Check connection parameters.");
    } catch (NoClassDefFoundError ex) {
      logger.error("Database driver is not defined: " + ex.getMessage());
      throw new RuntimeException("Connection with database failed! Check whether driver is defined.");
    } catch (ClassNotFoundException ex) {
      logger.error("Database driver is not loaded: " + ex.getMessage());
      throw new RuntimeException("Connection with database failed! Check whether driver is loaded.");
    }
  }

  public void setAutoCommit(Connection conn, Boolean flag) {
    try {
      DatabaseMetaData dbMeta = conn.getMetaData();
      if (dbMeta.supportsTransactions()) {
        conn.setAutoCommit(flag);
        logger.debug("Autocommit has been set: " + flag);
      }
    } catch (SQLException ex) {
      logger.error("Database error: " + ex.getMessage());
      throw new RuntimeException("Database error. Can't set autocommit.");
    }
  }

  public void close(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
        logger.debug("Connection with database has been closed.");
      } catch (SQLException ex) {
        logger.error("Can't close connection with database: " + ex.getMessage());
      }
    }
  }

  public void rollback(Connection conn) {
    try {
      conn.rollback();
      logger.debug("Transaction rolled back.");
    } catch (SQLException ex) {
      logger.error("Database error: " + ex.getMessage());
      throw new RuntimeException("Database error: " + ex.getMessage());
    }
  }

  public void commit(Connection conn) {
    try {
      conn.commit();
      logger.debug("Transaction committed.");
    } catch (SQLException ex) {
      logger.error("Database error: " + ex.getMessage());
      throw new RuntimeException("Database error: " + ex.getMessage());
    }
  }

  public void closeStatement(PreparedStatement stmt) {
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException ex) {
        logger.error(ex.getMessage());
      }
    }
  }
}
