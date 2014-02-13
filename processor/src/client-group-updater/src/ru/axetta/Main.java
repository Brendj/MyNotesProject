package ru.axetta;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class Main {

  private List<ClientGroup> groups = new ArrayList<ClientGroup>();
  private DbWrapper dbWrapper;
  private static Logger logger = Logger.getLogger(Main.class);
  private long clientRegistryVersion;

  private void initProperties() {
    FileInputStream fin = null;
    try {
      fin = new FileInputStream("config.properties");
      Properties props = new Properties();
      props.load(fin);
      String protocol = props.getProperty("database.protocol");
      String url = props.getProperty("database.url");
      String user = props.getProperty("database.user");
      String password = props.getProperty("database.password");
      dbWrapper = new DbWrapper(protocol, url, user, password);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new RuntimeException(e.getMessage());
    } finally {
      if (fin != null) {
        try {
          fin.close();
        } catch (Exception ex) {
          logger.error(ex.getMessage(), ex);
        }
      }
    }
  }

  private static final Long TEACH_GROUP = 1100000000L;
    private static final Long EMP_GROUP = 1100000001L;

  private void initGroups() {
    groups.add(new ClientGroup(null, 1100000000L, "Пед. состав"));
    groups.add(new ClientGroup(null, 1100000001L, "Сотрудники"));
    groups.add(new ClientGroup(null, 1100000010L, "Администрация"));
    groups.add(new ClientGroup(null, 1100000020L, "Тех. персонал"));
    groups.add(new ClientGroup(null, 1100000030L, "Родители"));
    groups.add(new ClientGroup(null, 1100000040L, "Посетители"));
    groups.add(new ClientGroup(null, 1100000050L, "Другое"));
    groups.add(new ClientGroup(null, 1100000060L, "Выбывшие"));
    groups.add(new ClientGroup(null, 1100000070L, "Удаленные"));
    groups.add(new ClientGroup(null, 1100000080L, "Перемещенные"));
  }

  public static void main(String[] args) {
    Main main = new Main();
    System.out.println("Update work began...");
    main.initGroups();
    main.initProperties();
    main.updateClientVersion();
    // Первоначально надо разобраться с "Сотрудниками".
    main.preProcessEmployeeGroup();
    for (ClientGroup clientGroup : main.groups) {
      main.processOneGroup(clientGroup.getIdOfGroup(), clientGroup.getGroupName());
    }
    System.out.println("Update work is over. View changes in 'application.log' file.");
  }

  private void processOneGroup(Long idOfGroup, String groupName) {
    Set<ClientGroup> oldGroups = new HashSet<ClientGroup>();
    Connection con = null;
    PreparedStatement st = null;
    try {
      con = dbWrapper.getConnection();
      st = con.prepareStatement("SELECT idoforg, idofclientgroup, groupname FROM cf_clientgroups WHERE upper(groupname) LIKE ? AND idofclientgroup <> ?");
      st.setString(1, '%' + groupName.toUpperCase() + '%');
      st.setLong(2, idOfGroup);
      ResultSet rs = st.executeQuery();
      while (rs.next()) {
        oldGroups.add(new ClientGroup(rs.getLong("idoforg"), rs.getLong("idofclientgroup"), rs.getString("groupname")));
      }
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      throw new RuntimeException(ex.getMessage());
    } finally {
      dbWrapper.closeStatement(st);
      dbWrapper.close(con);
    }
    for (ClientGroup clientGroup : oldGroups) {
      processOneGroupForOrg(clientGroup.getIdOfOrg(), idOfGroup, groupName, clientGroup.getIdOfGroup(), groupName);
    }
  }

  // Метод переводит клиентов из "неправильной" группы в "правильную".
  // "Правильная группа" - та, у которой имя и предопределенный ай-ди совпадают как надо.
  // Алгоритм: ищем правильную группу, если ее нет, то добавляем, переводим туда всех клиентов из неправильной и неправильную удаляем.
  private void processOneGroupForOrg(Long idOfOrg, Long newGroupId, String newGroupName, Long oldGroupId, String oldGroupName) {
    Connection con = null;
    try {
      con = dbWrapper.getConnection();
      dbWrapper.setAutoCommit(con, false);
      // Пытаемся найти "правильную группу". То есть группу, у которой с соответствием ай-ди и
      // имени все в порядке.
      ClientGroup validGroup = findGroup(con, idOfOrg, newGroupId, newGroupName);
      // Если правильная группа отсутствует в базе, то всталяем ее.
      if (validGroup == null) {
        insertGroup(con, idOfOrg, newGroupId, newGroupName);
      }
      updateClients(con, idOfOrg, newGroupId, oldGroupId, newGroupName, oldGroupName);
      deleteGroup(con, idOfOrg, oldGroupId, oldGroupName);
      dbWrapper.commit(con);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      dbWrapper.rollback(con);
      throw new RuntimeException(ex.getMessage());
    } finally {
      dbWrapper.setAutoCommit(con, true);
      dbWrapper.close(con);
    }
  }

  private void preProcessEmployeeGroup() {
    Set<ClientGroup> clientGroups = new HashSet<ClientGroup>();
    Connection con = null;
    PreparedStatement st = null;
    try {
      con = dbWrapper.getConnection();
      st = con.prepareStatement("SELECT idoforg, idofclientgroup FROM cf_clientgroups WHERE upper(groupname) LIKE ?");
      st.setString(1, "%Сотрудники%".toUpperCase());
      ResultSet rs = st.executeQuery();
      while (rs.next()) {
        clientGroups.add(new ClientGroup(rs.getLong("idoforg"), rs.getLong("idofclientgroup"), "Сотрудники"));
      }
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      throw new RuntimeException(ex.getMessage());
    } finally {
      dbWrapper.closeStatement(st);
      dbWrapper.close(con);
    }
    for (ClientGroup clientGroup : clientGroups) {
      preProcessEmployeeGroupForOneOrg(clientGroup.getIdOfOrg(), clientGroup.getIdOfGroup());
    }
  }

  private void preProcessEmployeeGroupForOneOrg(Long idOfOrg, Long oldGroupId) {
    Connection con = null;
    try {
      con = dbWrapper.getConnection();
      dbWrapper.setAutoCommit(con, false);
      // Если ай-ди "Сотрудников" совпадает с ай-ди "Пед. состава", то просто изменим имя.
      //if (oldGroupId.equals(TEACH_GROUP)) {
      //  changeGroupName(con, idOfOrg, oldGroupId, "Сотрудники", "Пед. состав");
      //    /* Должны поменять идентификаторы */
      //} else {
      //  // Иначе, переводим клиентов в "Пед. состав", а старую удаляем.
      //  // Первоначально, конечно, проверяем наличие группы "Пед. состав". Если такой группы нет, то создаем.
      //  ClientGroup clientGroup = findGroup(con, idOfOrg, TEACH_GROUP, "Пед. состав");
      //  if (clientGroup == null) {
      //    insertGroup(con, idOfOrg, TEACH_GROUP, "Пед. состав");
      //    clientGroup = new ClientGroup(idOfOrg, TEACH_GROUP, "Пед. состав");
      //  }
      //  updateClients(con, idOfOrg, clientGroup.getIdOfGroup(), oldGroupId, "Пед. состав", "Сотрудники");
      //  deleteGroup(con, idOfOrg, oldGroupId, "Сотрудники");
      //}
        ClientGroup clientGroup = findGroup(con, idOfOrg, EMP_GROUP, "Сотрудники");
        if (clientGroup == null) {
            insertGroup(con, idOfOrg, EMP_GROUP, "Сотрудники");
            clientGroup = new ClientGroup(idOfOrg, EMP_GROUP, "Сотрудники");
        }
        updateClients(con, idOfOrg, clientGroup.getIdOfGroup(), oldGroupId, "Сотрудники", "Сотрудники");
        //deleteGroup(con, idOfOrg, oldGroupId, "Сотрудники");

      dbWrapper.commit(con);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      dbWrapper.rollback(con);
      throw new RuntimeException(ex.getMessage());
    } finally {
      dbWrapper.setAutoCommit(con, true);
      dbWrapper.close(con);
    }
  }

  private int insertGroup(Connection con, Long idOfOrg, Long newGroupId, String newGroupName) throws Exception {
    PreparedStatement insertSt = null;
    try {
      insertSt = con.prepareStatement("INSERT INTO cf_clientgroups (idoforg, idofclientgroup, groupname) VALUES (?,?,?)");
      insertSt.setLong(1, idOfOrg);
      insertSt.setLong(2, newGroupId);
      insertSt.setString(3, newGroupName);
      int row = insertSt.executeUpdate();
      if (row != 0)
        logger.info(String.format("Group{%s, %s} was inserted for Org{%s}", newGroupId, newGroupName, idOfOrg));
      return row;
    } finally {
      dbWrapper.closeStatement(insertSt);
    }
  }

  private int updateClients(Connection con, Long idOfOrg, Long newGroupId, Long oldGroupId, String newGroupName, String oldGroupName) throws Exception {
    PreparedStatement updateSt = null;
    try {
      updateSt = con.prepareStatement("UPDATE cf_clients SET idofclientgroup = ?, clientregistryversion = ? WHERE idoforg = ? AND idofclientgroup = ?");
      updateSt.setLong(1, newGroupId);
      updateSt.setLong(2, clientRegistryVersion);
      updateSt.setLong(3, idOfOrg);
      updateSt.setLong(4, oldGroupId);
      int updateCount = updateSt.executeUpdate();
      if (updateCount != 0)
        logger.info(String.format("%s clients of Org{%s} - Group{%s, %s} was updated to Group{%s, %s}", updateCount, idOfOrg, oldGroupId, oldGroupName, newGroupId, newGroupName));
      return updateCount;
    } finally {
      dbWrapper.closeStatement(updateSt);
    }
  }

  private int deleteGroup(Connection con, Long idOfOrg, Long idOfGroup, String oldGroupName) throws Exception {
    PreparedStatement deleteSt = null;
    try {
      deleteSt = con.prepareStatement("DELETE FROM cf_clientgroups WHERE idoforg = ? AND idofclientgroup = ?");
      deleteSt.setLong(1, idOfOrg);
      deleteSt.setLong(2, idOfGroup);
      int deleteCount = deleteSt.executeUpdate();
      if (deleteCount != 0) {
        logger.info(String.format("Group{%s, %s} of Org{%s} was deleted.", idOfGroup, oldGroupName, idOfOrg));
      }
      return deleteCount;
    } finally {
      dbWrapper.closeStatement(deleteSt);
    }
  }

  private int changeGroupName(Connection con, Long idOfOrg, Long idOfGroup, String oldGroupName, String newGroupName) throws Exception {
    PreparedStatement updateSt = null;
    try {
      updateSt = con.prepareStatement("UPDATE cf_clientgroups SET groupname = ? WHERE idoforg = ? AND idofclientgroup = ?");
      updateSt.setString(1, newGroupName);
      updateSt.setLong(2, idOfOrg);
      updateSt.setLong(3, idOfGroup);
      int updateCount = updateSt.executeUpdate();
      if (updateCount != 0)
        logger.info(String.format("Group{%s, %s} of Org{%s} was updated to Group{%s, %s}", idOfGroup, oldGroupName, idOfOrg, idOfGroup, newGroupName));
      return updateCount;
    } finally {
      dbWrapper.closeStatement(updateSt);
    }
  }

  private ClientGroup findGroup(Connection con, Long idOfOrg, Long idOfGroup, String groupName) throws Exception {
    PreparedStatement stmt = null;
    try {
      stmt = con.prepareStatement("SELECT idofclientgroup FROM cf_clientgroups WHERE idoforg = ? AND idofclientgroup = ? AND upper(groupname) LIKE ?");
      stmt.setLong(1, idOfOrg);
      stmt.setLong(2, idOfGroup);
      stmt.setString(3, '%' + groupName.toUpperCase() + '%');
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return new ClientGroup(idOfOrg, idOfGroup, groupName);
      } else {
        return null;
      }
    } finally {
      dbWrapper.closeStatement(stmt);
    }
  }

  private void updateClientVersion() {
    Connection con = null;
    PreparedStatement stmt = null;
    PreparedStatement updateStmt = null;
    try {
      con = dbWrapper.getConnection();
      dbWrapper.setAutoCommit(con, false);
      stmt = con.prepareStatement("SELECT clientregistryversion FROM cf_registry");
      updateStmt = con.prepareStatement("UPDATE cf_registry SET clientregistryversion = ?");
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        clientRegistryVersion = rs.getLong("clientregistryversion") + 1;
        updateStmt.setLong(1, clientRegistryVersion);
        updateStmt.executeUpdate();
      }
      dbWrapper.commit(con);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      dbWrapper.rollback(con);
      throw new RuntimeException(ex.getMessage());
    } finally {
      dbWrapper.closeStatement(stmt);
      dbWrapper.closeStatement(updateStmt);
      dbWrapper.setAutoCommit(con, true);
      dbWrapper.close(con);
    }
  }
}
