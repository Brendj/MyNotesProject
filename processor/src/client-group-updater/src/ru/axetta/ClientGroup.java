package ru.axetta;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 05.02.14
 * Time: 14:10
 */

public class ClientGroup {

  private Long idOfOrg;
  private Long idOfGroup;
  private String groupName;

  public ClientGroup() {
  }

  public ClientGroup(Long idOfOrg, Long idOfGroup, String groupName) {
    this.idOfOrg = idOfOrg;
    this.idOfGroup = idOfGroup;
    this.groupName = groupName;
  }

  public Long getIdOfOrg() {
    return idOfOrg;
  }

  public Long getIdOfGroup() {
    return idOfGroup;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setIdOfOrg(Long idOfOrg) {
    this.idOfOrg = idOfOrg;
  }

  public void setIdOfGroup(Long idOfGroup) {
    this.idOfGroup = idOfGroup;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
}
